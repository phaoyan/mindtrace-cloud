package pers.juumii.data;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import pers.juumii.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    @TableId
    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String phone;
    private String email;
    private String gender;
    private String avatar;
    private LocalDateTime createTime;
    private Boolean status;
    @TableLogic
    private Boolean deleted;
    @TableField(exist = false)
    private List<Role> roles;

    public static User prototype(String username, String password) {
        User user = new User();
        user.setId(IdUtil.getSnowflakeNextId());
        user.setUsername(username);
        user.setNickname(username);
        user.setPassword(password);
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(true);
        user.setDeleted(false);
        user.setRoles(new ArrayList<>());
        return user;
    }

    public static User prototype(String username, String password, String email){
        User user = new User();
        user.setId(IdUtil.getSnowflakeNextId());
        user.setUsername(username);
        user.setNickname(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(true);
        user.setDeleted(false);
        user.setRoles(new ArrayList<>());
        return user;
    }

    public static UserDTO transfer(User user) {
        UserDTO res = new UserDTO();
        res.setId(user.getId().toString());
        res.setUsername(user.getUsername());
        res.setPassword(user.getPassword());
        res.setPhone(user.getPhone());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setAvatar(user.getAvatar());
        return res;
    }
}
