package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.*;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeQueryService;
import pers.juumii.service.KnodeService;
import pers.juumii.repo.LabelRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KnodeServiceImpl implements KnodeService {

    private final KnodeRepository knodeRepo;
    private final LabelRepository labelRepo;
    private final KnodeQueryService knodeQuery;


    @Autowired
    public KnodeServiceImpl(
            KnodeRepository knodeRepo,
            LabelRepository labelRepo,
            KnodeQueryService knodeQuery) {
        this.knodeRepo = knodeRepo;
        this.labelRepo = labelRepo;
        this.knodeQuery = knodeQuery;
    }

    @Override
    public Knode branch(Long knodeId, String title) {
        Optional<Knode> sup = knodeRepo.findById(knodeId);
        Knode data;
        if(sup.isPresent()){
            Knode stem = sup.get();
            Knode branch = knodeRepo.save(Knode.prototype(title, stem));
            stem.getBranches().add(branch);
            knodeRepo.save(stem);
            data = branch;
        } else data = knodeRepo.save(Knode.prototype(title));
        return data;
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
        List<Long> toBeDeletedIds = knodeQuery.checkAllIncludingDeleted(userId)
                .stream().filter(Knode::getDeleted)
                .map(Knode::getId).toList();
        knodeRepo.deleteAllById(toBeDeletedIds);
        return SaResult.ok();
    }

    @Override
    public SaResult update(Long knodeId, KnodeDTO dto) {
        Knode target = knodeRepo.findById(knodeId).get();
        // title
        Opt.ifPresent(dto.getTitle(), target::setTitle);
        // deleted
        Opt.ifPresent(dto.getDeleted(), target::setDeleted);
        // labels
        Opt.ifPresent(dto.getLabels(), labels -> target.setLabels(labelRepo.findAllById(labels.stream().map(Label::getName).toList())));
        // stem
        Opt.ifPresent(dto.getStemId(),
            stemId -> target.setStem(knodeRepo.findById(stemId).orElse(null)));
        // branches
        Opt.ifPresent(dto.getBranchIds(),
            branchIds-> target.setBranches(knodeRepo.findAllById(branchIds)));
        // connections
        Opt.ifPresent(dto.getConnectionIds(),
            connectionIds-> target.setConnections(knodeRepo.findAllById(connectionIds)));
        knodeRepo.save(target);
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
    public SaResult shift(Long stemId, Long branchId) {
        Optional<Knode> stemOptional = knodeRepo.findById(stemId);
        Optional<Knode> branchOptional = knodeRepo.findById(branchId);
        if(stemOptional.isEmpty() || branchOptional.isEmpty())
            return SaResult.error("Stem knode or branch knode not found.");
        knodeRepo.shift(stemId, branchId);

        return SaResult.ok();
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

}
