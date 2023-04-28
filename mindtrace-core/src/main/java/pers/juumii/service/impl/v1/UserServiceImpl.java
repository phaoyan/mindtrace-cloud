package pers.juumii.service.impl.v1;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.data.User;
import pers.juumii.repo.UserRepository;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.KnodeService;
import pers.juumii.service.UserService;

//@Service
public class UserServiceImpl implements UserService {

    private final KnodeService knodeService;
    private final KnodeQueryService knodeQuery;
    private final UserRepository userRepo;

    @Autowired
    public UserServiceImpl(
            KnodeService knodeService,
            KnodeQueryService knodeQuery,
            UserRepository userRepo) {
        this.knodeService = knodeService;
        this.knodeQuery = knodeQuery;
        this.userRepo = userRepo;
    }

    @Override
    public SaResult register(Long userId) {
        Knode root = knodeService.createRoot(userId);
        userRepo.save(User.prototype(userId, root));
        return SaResult.data(root.getId().toString());
    }

    @Override
    public SaResult unregister(Long userId) {
        Long rootId = checkRootId(userId);
        userRepo.deleteById(userId);
        return SaResult.data(rootId.toString());
    }

    @Override
    public Long checkRootId(Long userId) {
        return userRepo.checkRootId(userId);
    }

}
