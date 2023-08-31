package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.User;
import pers.juumii.mapper.UserMapper;
import pers.juumii.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @NacosValue(value = "${tencent.cos.bucket.name}", autoRefreshed = true)
    private String BUCKET_NAME;
    private final COSClient cosClient;
    private final DiscoveryClient discoveryClient;
    private final UserMapper userMapper;
    private final JavaMailSender mail;
    private final StringRedisTemplate redis;

    @Autowired
    public UserServiceImpl(
            COSClient cosClient,
            DiscoveryClient discoveryClient,
            UserMapper userMapper,
            JavaMailSender javaMailSender,
            StringRedisTemplate redis) {
        this.cosClient = cosClient;
        this.discoveryClient = discoveryClient;
        this.userMapper = userMapper;
        this.mail = javaMailSender;
        this.redis = redis;
    }

    @Override
    public Boolean exists(Long id) {
        return userMapper.selectById(id) != null;
    }

    @Override
    public SaResult sendValidateCode(String email) {
        try {
            int validate = new Random().nextInt(100000,999999);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("1521324702@qq.com");
            message.setTo(email);
            message.setSubject("Mindtrace 账号注册邮箱验证");
            message.setText("欢迎使用Mindtrace！邮箱验证码：" + validate);
            mail.send(message);
            String key = "mindtrace::gateway::register::"+email;
            redis.opsForValue().set(key, Objects.toString(validate));
        }catch (RuntimeException e){
            e.printStackTrace();
            return SaResult.error("邮件发送失败");
        }
        return SaResult.ok();
    }

    @Override
    @Transactional
    public SaResult validate(User userdata, Integer validate) {
        String key = "mindtrace::gateway::register::"+userdata.getEmail();
        String dataStr = redis.opsForValue().get(key);
        if(dataStr == null || !validate.equals(Convert.toInt(dataStr)))
            return SaResult.error("验证码不正确");
        User user = User.prototype(userdata.getUsername(), userdata.getPassword(), userdata.getEmail());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userdata.getUsername());
        if(userMapper.exists(wrapper))
            return SaResult.error("用户名已存在");
        // bcrypt加密
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        userMapper.insert(user);
        return SaResult.data(user.getId());
    }


    @Override
    public User check(Long loginId) {
        return userMapper.selectById(loginId);
    }


    @Override
    public User getUserInfo(Long userId) {
        return check(userId);
    }

    @Override
    public User getUserInfo(String username) {
        if(username == null) return check(StpUtil.getLoginIdAsLong());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public List<User> getUserInfoByLike(String like) {
        if(StrUtil.isBlank(like)) return new ArrayList<>();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getUsername, like);
        return userMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void updateAvatar(InputStream data, Long userId) {
        String objKey = userId + "/avatar";
        cosClient.putObject(BUCKET_NAME, objKey, data, new ObjectMetadata());
        User user = userMapper.selectById(userId);
        Optional<ServiceInstance> gateway = discoveryClient.getInstances("mindtrace-gateway").stream().findAny();
        if(gateway.isEmpty())
            throw new RuntimeException("Service Not Available: mindtrace-gateway");
        String url = gateway.get().getUri().toString() + "/user/" + user.getId() + "/avatar";
        user.setAvatar(url);
        userMapper.updateById(user);
    }

    @Override
    public ResponseEntity<byte[]> getAvatar(Long userId) {
        try{
            String objKey = userId + "/avatar";
            byte[] bytes = cosClient.getObject(BUCKET_NAME, objKey).getObjectContent().readAllBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(bytes);
        }catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @Transactional
    public SaResult changePassword(Long userId, String oriPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if(!BCrypt.checkpw(oriPassword, user.getPassword()))
            return SaResult.error("密码错误");
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userMapper.updateById(user);
        return SaResult.ok();
    }




}
