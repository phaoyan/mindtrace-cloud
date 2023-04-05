package pers.juumii.entity;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
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
}
