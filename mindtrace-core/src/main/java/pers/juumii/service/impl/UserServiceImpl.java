package pers.juumii.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.data.User;
import pers.juumii.repo.UserRepository;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.KnodeService;
import pers.juumii.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final KnodeService knodeService;
    private final KnodeQueryService knodeQuery;
    private final UserRepository userRepo;

    @Autowired
    public UserServiceImpl(
            KnodeService knodeService,
            KnodeQueryService knodeQuery, UserRepository userRepo) {
        this.knodeService = knodeService;
        this.knodeQuery = knodeQuery;
        this.userRepo = userRepo;
    }

    @Override
    public Long register(Long userId) {
        Knode root = knodeService.branch(-1L, "ROOT");
        userRepo.save(User.prototype(userId, root));
        return root.getId();
    }

    @Override
    public Long unregister(Long userId) {
        Long rootId = checkRootId(userId);
        userRepo.deleteById(userId);
        return rootId;
    }

    @Override
    public Long checkRootId(Long userId) {
        return userRepo.checkRootId(userId);
    }

    @Override
    public Boolean possesses(Long userId, Long knodeId) {
        return findUserId(knodeId).equals(userId);
    }

    @Override
    public Long findUserId(Long knodeId) {
        // 两步：先从knodeId找rootId，再从rootId找userId
        return userRepo.findUserId(knodeQuery.findRoot(knodeId).getId());
    }





}
