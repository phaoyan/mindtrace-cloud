package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.entity.User;
import pers.juumii.mapper.UserMapper;
import pers.juumii.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RabbitTemplate rabbit;

    @Autowired
    public UserServiceImpl(
            UserMapper userMapper,
            RabbitTemplate rabbit) {
        this.userMapper = userMapper;
        this.rabbit = rabbit;
    }

    @Override
    public Boolean exists(Long id) {
        return userMapper.selectById(id) != null;
    }

    @Override
    public Boolean exists(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.exists(wrapper);
    }

    @Override
    public SaResult register(String username, String password) {
        if(exists(username))
            return SaResult.error("Username already used: " + username);

        User user = User.prototype(username, password);
        userMapper.insert(user);
        rabbit.convertAndSend("user_event_exchange", "register", user.getId());
        return SaResult.data(user.getId());
    }

    @Override
    public User check(Long loginId) {
        return userMapper.selectById(loginId);
    }
}
