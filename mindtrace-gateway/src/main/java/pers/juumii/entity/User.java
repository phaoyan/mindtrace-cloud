package pers.juumii.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;
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
    private List<Permission> permissions;
    @TableField(exist = false)
    private List<Role> roles;
}
