package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.UserShare;
import pers.juumii.dto.share.UserShareDTO;
import pers.juumii.service.UserShareService;

@RestController
public class UserShareController {

    private final UserShareService userShareService;

    @Autowired
    public UserShareController(UserShareService userShareService) {
        this.userShareService = userShareService;
    }

    @PostMapping("/user/{userId}")
    public void openUserShare(@PathVariable Long userId){
        userShareService.openUserShare(userId);
    }

    @DeleteMapping("/user/{userId}")
    public void closeUserShare(@PathVariable Long userId){
        userShareService.closeUserShare(userId);
    }

    @GetMapping("/user/{userId}")
    public UserShareDTO getUserShare(@PathVariable Long userId){
        return UserShare.transfer(userShareService.getUserShare(userId));
    }

}
