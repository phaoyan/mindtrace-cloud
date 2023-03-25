package pers.juumii.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.repo.UserRepository;
import pers.juumii.service.KnodeQueryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnodeQueryServiceImpl implements KnodeQueryService {

    private final UserRepository userRepo;
    private final KnodeRepository knodeRepo;

    @Autowired
    public KnodeQueryServiceImpl(
            UserRepository userRepo,
            KnodeRepository knodeRepo) {
        this.userRepo = userRepo;
        this.knodeRepo = knodeRepo;
    }

    @Override
    public Knode check(Long knodeId) {
        // 存在性在Aspect中已经检验
        return knodeRepo.findById(knodeId).get();
    }

    @Override
    public List<Knode> checkByTitle(Long userId, String title) {
        Long rootId = userRepo.checkRootId(userId);
        List<Long> ids = knodeRepo.findOffSprings(rootId).stream().map(Knode::getId).toList();
        return knodeRepo.findByTitle(ids, title);
    }

    @Override
    public List<Knode> branches(Long knodeId) {
        // 存在性在Aspect中已经检验
        return knodeRepo.findAllById(
                knodeRepo.findById(knodeId).get().getBranches()
                .stream().map(Knode::getId)
                .collect(Collectors.toList()));
    }

    @Override
    public List<Knode> offsprings(Long knodeId) {
        return knodeRepo.findOffSprings(knodeId);
    }

    @Override
    public Knode stem(Long knodeId) {
        // 第一个get的存在性在Aspect中已经检验；第二个get为null代表其为根节点
        return knodeRepo.findById(knodeRepo.findById(knodeId).get().getId()).get();
    }

    @Override
    public List<Knode> ancestors(Long knodeId) {
        return knodeRepo.findAncestors(knodeId);
    }

    @Override
    public Knode findRoot(Long knodeId) {
        List<Knode> ancestors = ancestors(knodeId);
        if(ancestors.isEmpty()) return check(knodeId);
        return ancestors.get(ancestors.size()-1);
    }
}
