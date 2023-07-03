package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.data.User;

import java.io.InputStream;

@Service
public interface UserService {
    Boolean exists(Long id);
    Boolean exists(String username);
    SaResult sendValidateCode(String email);
    SaResult validate(User userdata, Integer validate); //返回userId
    User check(Long loginId);
    User getUserPublicInfo(Long userId);
    User getUserInfo(Long userId, String password);
    User getUserInfo(String username, String password);
    void updateAvatar(InputStream data, Long userId);
    ResponseEntity<byte[]> getAvatar(Long userId);
    SaResult changePassword(Long userId, String oriPassword, String newPassword);

}
