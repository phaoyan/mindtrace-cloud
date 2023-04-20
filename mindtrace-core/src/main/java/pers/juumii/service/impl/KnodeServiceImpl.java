package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.config.RabbitMQConfig;
import pers.juumii.data.*;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.repo.UserRepository;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.KnodeService;
import pers.juumii.repo.LabelRepository;
import pers.juumii.thread.ThreadUtils;
import pers.juumii.utils.DataUtils;

import java.util.List;
import java.util.Optional;

@Service
public class KnodeServiceImpl implements KnodeService {

    private final UserRepository userRepo;
    private final KnodeRepository knodeRepo;
    private final LabelRepository labelRepo;
    private final KnodeQueryService knodeQuery;
    private final ThreadUtils threadUtils;
    private final RabbitTemplate rabbit;

    @Autowired
    public KnodeServiceImpl(
            KnodeRepository knodeRepo,
            LabelRepository labelRepo,
            KnodeQueryService knodeQuery,
            UserRepository userRepo,
            ThreadUtils threadUtils,
            RabbitTemplate rabbit) {
        this.knodeRepo = knodeRepo;
        this.labelRepo = labelRepo;
        this.knodeQuery = knodeQuery;
        this.userRepo = userRepo;
        this.threadUtils = threadUtils;
        this.rabbit = rabbit;
    }

    @Override
    public Knode branch(Long userId, Long knodeId, String title) {
        Knode stem = knodeQuery.check(knodeId);
        Knode branch = Knode.prototype(title, stem, userId);
        branch.setIndex(stem.getBranches().size());

        threadUtils.getUserBlockingQueue(userId).add(()->{
            knodeRepo.save(branch);
            stem.getBranches().add(branch);
            knodeRepo.save(stem);
        });
        return branch;
    }

    /**
     * 不是逻辑删除而是物理删除；
     * 会同时删除这个节点的所有offsprings
     */
    @Override
    public SaResult delete(Long knodeId) {
        Optional<Knode> knode = knodeRepo.findById(knodeId);
        String info;
        if(knode.isPresent()){
            knodeRepo.deleteAllById(knodeQuery.offsprings(knodeId).stream().map(Knode::getId).toList());
            knodeRepo.deleteById(knodeId);
            info = "Knode deleted: " + knodeId;
        }else
            info = "Knode not found: " + knodeId;
        return SaResult.ok(info);
    }

    @Override
    public SaResult clear(Long userId) {
        return SaResult.ok();
    }

    @Override
    public SaResult update(Long knodeId, KnodeDTO dto) {
        Knode target = knodeQuery.check(knodeId);
        Opt.ifPresent(dto.getTitle(), target::setTitle);
        Opt.ifPresent(dto.getIsLeaf(), target::setIsLeaf);
        Opt.ifPresent(dto.getLabels(), labels -> target.setLabels(labelRepo.findAllById(labels.stream().map(Label::getName).toList())));
        Opt.ifPresent(dto.getStemId(),
            stemId -> target.setStem(knodeRepo.findById(Convert.toLong(stemId)).orElse(null)));
        Opt.ifPresent(dto.getBranchIds(),
            branchIds-> target.setBranches(knodeRepo.findAllById(Convert.toList(Long.class, branchIds))));
        Opt.ifPresent(dto.getConnectionIds(),
            connectionIds-> target.setConnections(knodeRepo.findAllById(Convert.toList(Long.class, connectionIds))));
        knodeRepo.save(target);

        // 发送MQ消息
        rabbit.convertAndSend(
                RabbitMQConfig.KNODE_EVENT_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_UPDATE,
                JSONUtil.toJsonStr(Knode.transfer(target)));
        return SaResult.ok("Knode updated: " + knodeId);
    }

    @Override
    public SaResult addLabelToKnode(Long knodeId, String labelName) {
        Optional<Knode> optionalKnode = knodeRepo.findById(knodeId);
        if(optionalKnode.isEmpty())
            return SaResult.error("Knode not found: " + knodeId);
        Optional<Label> optionalLabel = labelRepo.findById(labelName);
        if(optionalLabel.isEmpty())
            return SaResult.error("Label not found: " + labelName);
        Knode knode = optionalKnode.get();
        Label Label = optionalLabel.get();
        knode.getLabels().add(Label);
        knodeRepo.save(knode);
        return SaResult.data(knode);
    }

    @Override
    public SaResult removeLabelFromKnode(Long knodeId, String labelName) {
        Optional<Knode> optionalKnode = knodeRepo.findById(knodeId);
        if(optionalKnode.isEmpty())
            return SaResult.error("Knode not found: " + knodeId);
        knodeRepo.unlabel(knodeId, labelName);
        return SaResult.ok();
    }

    @Override
    public List<Knode> shift(Long stemId, Long branchId, Long userId) {
        knodeRepo.shift(stemId, branchId);
        return knodeQuery.checkAll(userId);
    }

    @Override
    public SaResult connect(Long sourceId, Long targetId) {
        Optional<Knode> sourceOptional = knodeRepo.findById(sourceId);
        Optional<Knode> targetOptional = knodeRepo.findById(targetId);
        if(sourceOptional.isEmpty())
            return SaResult.error("Source not found: " + sourceId);
        if(targetOptional.isEmpty())
            return SaResult.error("Target not found: " + targetId);
        Knode source = sourceOptional.get();
        Knode target = targetOptional.get();
        source.getConnections().add(target);
        target.getConnections().add(source);
        knodeRepo.save(source);
        knodeRepo.save(target);
        return SaResult.ok();
    }

    public void initIndex(Knode knode){
        for(int i = 0; i < knode.getBranches().size(); i ++){
            Knode branch = knode.getBranches().get(i);
            branch.setIndex(i);
            knodeRepo.save(branch);
            initIndex(branch);
        }
    }

    @Override
    public List<Knode> initIndex(Long userId) {
        initIndex(knodeQuery.check(userRepo.checkRootId(userId)));
        return knodeQuery.checkAll(userId);
    }

    @Override
    public void swapIndex(Long userId, Long stemId, Integer index1, Integer index2){
        threadUtils.getUserBlockingQueue(userId).add(()->{
            Knode stem = knodeQuery.check(stemId);
            List<Knode> branches = stem.getBranches();
            Knode knode1 = DataUtils.getIf(branches, branch -> branch.getIndex().equals(index1));
            Knode knode2 = DataUtils.getIf(branches, branch -> branch.getIndex().equals(index2));
            knode1.setIndex(index2);
            knode2.setIndex(index1);
            knodeRepo.save(knode1);
            knodeRepo.save(knode2);
         });
    }

}
