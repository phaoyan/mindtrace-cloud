package pers.juumii.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.dto.UserDTO;
import pers.juumii.data.User;
import pers.juumii.service.LoginService;
import pers.juumii.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class UserController {

    private final LoginService loginService;
    private final UserService userService;

    @Autowired
    public UserController(
            LoginService loginService,
            UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @PostMapping("/user/register/validate")
    public SaResult sendValidateCode(@RequestParam String email){
        return userService.sendValidateCode(email);
    }

    @PostMapping("/user/register/confirm")
    public SaResult confirmRegister(@RequestParam Integer validate, @RequestBody User userdata){
        return userService.validate(userdata, validate);
    }

    @PostMapping("/user/login")
    public SaResult login(@RequestBody User user) {
        return loginService.login(user);
    }

    @PostMapping("/user/logout")
    public SaResult logout(){
        StpUtil.logout();
        return SaResult.ok();
    }

    @GetMapping("/user/login")
    public SaResult isLogin() {
        return SaResult.ok("是否登录：" + StpUtil.isLogin());
    }

    @SaIgnore
    @GetMapping("/user/{id}/exists")
    public SaResult exists(@PathVariable Long id){
        return SaResult.data(userService.exists(id));
    }

    @GetMapping("/user/{userId}")
    public UserDTO getUserInfo(@PathVariable Long userId){
        return User.transfer(userService.getUserInfo(userId));
    }

    @GetMapping("/user")
    public UserDTO getUserInfo(@RequestParam(value = "username", required = false) String username){
        return User.transfer(userService.getUserInfo(username));
    }

    @GetMapping("/like/user")
    public List<UserDTO> getUserInfoByLike(@RequestParam String like){
        return User.transfer(userService.getUserInfoByLike(like));
    }

    @PostMapping("/user/{userId}/password")
    public SaResult changePassword(
            @PathVariable Long userId,
            @RequestParam String oriPassword,
            @RequestParam String newPassword){
        return userService.changePassword(userId, oriPassword, newPassword);
    }

    @PostMapping(value = "/user/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateAvatar(
            @PathVariable Long userId,
            @RequestPart("file") FilePart file) throws NullPointerException {
        try {
            List<DataBuffer> buffers = file.content().collectList().block();
            if(buffers == null) return;
            DataBuffer buffer = buffers.stream().reduce(DataBuffer::write).get();
            userService.updateAvatar(buffer.asInputStream(), userId);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @GetMapping("/user/{userId}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long userId){
        return userService.getAvatar(userId);
    }
}
