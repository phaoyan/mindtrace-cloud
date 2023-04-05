package pers.juumii.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.entity.User;
import pers.juumii.mapper.UserMapper;
import pers.juumii.service.LoginService;

import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserMapper userMapper;

    @Autowired
    public LoginServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public SaResult login(User user) {
        // 按用户名查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User _user = userMapper.selectOne(wrapper);
        // 若用户不存在，则报异常
        if(Objects.isNull(_user))
            return SaResult.error("登陆失败: 用户不存在");
        // 若用户存在，继续验证密码
        if(!user.getPassword().equals(_user.getPassword()))
            return SaResult.error("登陆失败：密码错误");
        // 用户存在且密码正确，则登陆成功
        StpUtil.login(_user.getId());
        return SaResult.get(200,"登陆成功",_user.getId());
    }
}
