package pers.juumii.dto;

import lombok.Data;
import pers.juumii.entity.User;

@Data
public class UserDTO {

    private String id;
    private String username;
    private String password;
    private String phone;
    private String email;


    public static UserDTO transfer(User user) {
        UserDTO res = new UserDTO();
        res.setId(user.getId().toString());
        res.setUsername(user.getUsername());
        res.setPassword(user.getPassword());
        res.setPhone(user.getPhone());
        res.setEmail(user.getEmail());
        return res;
    }
}
