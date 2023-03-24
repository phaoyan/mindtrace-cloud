package pers.juumii.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.CheckUserExistence;
import pers.juumii.data.Knode;
import pers.juumii.data.User;
import pers.juumii.feign.UserClient;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.repo.UserRepository;
import pers.juumii.service.KnodeService;
import pers.juumii.service.UserService;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.SaResult;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserClient userClient;
    private final UserRepository userRepo;
    private final KnodeRepository knodeRepo;
    private final KnodeService knodeService;

    @Autowired
    public UserServiceImpl(
            UserClient userClient,
            UserRepository userRepo,
            KnodeRepository knodeRepo,
            KnodeService knodeService) {
        this.userClient = userClient;
        this.userRepo = userRepo;
        this.knodeRepo = knodeRepo;
        this.knodeService = knodeService;
    }

    @Override
    @CheckUserExistence
    public SaResult register(Long userId, String name) {
        if(userRepo.findById(userId).isPresent())
            return SaResult.error("User already registered: " + userId);
        User user = userRepo.save(User.prototype(userId, name));
        return SaResult.data(user);
    }

    @Override
    @CheckUserExistence
    public SaResult check(Long userId) {
        Optional<User> user = userRepo.findById(userId);
        return SaResult.data(user.get());
    }

    @Override
    @CheckUserExistence
    public SaResult remove(Long userId) {
        userRepo.deleteById(userId);
        return SaResult.ok();
    }

    @Override
    @CheckUserExistence
    public SaResult branch(Long userId, String title) {
        Optional<User> userOptional = userRepo.findById(userId);
        SaResult result = knodeService.branch(-1L, title);
        Knode root = (Knode) result.getData();
        User user = userOptional.get();
        user.getRoots().add(root);
        userRepo.save(user);
        return SaResult.data(root);
    }

    @Override
    @CheckUserExistence
    public SaResult dropBranch(Long userId, Long branchId) {
        userRepo.dropBranch(userId, branchId);
        return SaResult.ok();
    }

    @Override
    @CheckUserExistence
    public SaResult attachBranch(Long userId, Long branchId) {
        Optional<Knode> branchOptional = knodeRepo.findById(branchId);
        // 未找到branch时抛异常
        if(branchOptional.isEmpty())
            return SaResult.error("Knode not found: " + branchId);
        Knode branch = branchOptional.get();
        // branch并非根节点时抛异常
        if(!Objects.isNull(branch.getStem()))
            return SaResult.error("Knode to be root required: " + branchId);
        Optional<User> userOptional = userRepo.findById(userId);
        // 未找到user抛异常
        if(userOptional.isEmpty())
            return SaResult.error("User not found: " + userId);
        User user = userOptional.get();
        // branch已经被某个user占用时抛异常
        List<User> all = userRepo.findAll();
        if(DataUtils.ifAny(all,
            _user->DataUtils.getIf(
                _user.getRoots(),
                root->root.getId().equals(branchId))
                != null))
            return SaResult.error("Branch already used: " + branchId);

        user.getRoots().add(branch);
        userRepo.save(user);
        return SaResult.ok();
    }
}
