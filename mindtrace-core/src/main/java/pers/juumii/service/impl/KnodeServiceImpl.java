package pers.juumii.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.*;
import pers.juumii.dto.LabelDTO;
import pers.juumii.repo.KnodeRepository;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeService;
import pers.juumii.repo.LabelRepository;
import pers.juumii.utils.SaResult;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KnodeServiceImpl implements KnodeService {

    private final KnodeRepository knodeRepo;
    private final LabelRepository labelRepo;


    @Autowired
    public KnodeServiceImpl(
            KnodeRepository knodeRepo,
            LabelRepository labelRepo) {
        this.knodeRepo = knodeRepo;
        this.labelRepo = labelRepo;
    }

    @Override
    public SaResult branch(Long id, String title) {
        Optional<Knode> sup = knodeRepo.findById(id);
        String info;
        Knode data;
        if(sup.isPresent()){
            Knode stem = sup.get();
            Knode branch = knodeRepo.save(Knode.prototype(title, stem));
            stem.getBranches().add(branch);
            knodeRepo.save(stem);
            info = StrUtil.format("Branch created: {} to {}", id, branch.getId());
            data = branch;
        } else {
            Knode stem = knodeRepo.save(Knode.prototype(title));
            info = StrUtil.format("No knode with id {} is found. Knode {} created instead.", id, stem.getId());
            data = stem;
        }
        return SaResult.get(200,info,data);
    }

    @Override
    public SaResult branch(String stemTitle, String title) {
        Knode target = knodeRepo.findByTitle(stemTitle);
        if(!Objects.isNull(target))
            return branch(target.getId(), title);
        return SaResult.error("Knode not found: " + stemTitle);
    }


    // 逻辑删除
    @Override
    public SaResult delete(Long id) {
        Optional<Knode> knode = knodeRepo.findById(id);
        String info;
        if(knode.isPresent()){
            knode.get().setDeleted(true);
            knodeRepo.save(knode.get());
            info = "Knode logically deleted: " + id;
        }else
            info = "Knode not found: " + id;
        return SaResult.ok(info);
    }

    @Override
    public SaResult delete(String title) {
        Knode target = knodeRepo.findByTitle(title);
        if(!Objects.isNull(target))
            return delete(target.getId());
        return SaResult.error("Knode not found: " + title);
    }

    @Override
    public SaResult check(Long id) {
        Optional<Knode> selected = knodeRepo.findById(id).filter(knode -> !knode.getDeleted());
        KnodeDTO dto = selected.map(Knode::transfer).orElse(null);
        return SaResult.data(dto);
    }

    @Override
    public SaResult check(String title) {
        Knode target = knodeRepo.findByTitle(title);
        if(!Objects.isNull(target))
            return check(target.getId());
        return SaResult.error("Knode not found: " + title);
    }

    @Override
    public SaResult update(Long id, KnodeDTO dto) {
        Knode target = knodeRepo.findById(id).orElse(null);
        if(Objects.isNull(target))
            return SaResult.error("Knode not found: " + id);
        // title
        Opt.ifPresent(dto.getTitle(), target::setTitle);
        // deleted
        Opt.ifPresent(dto.getDeleted(), target::setDeleted);
        // labels
        Opt.ifPresent(dto.getLabels(),
            labelDTOS -> target.setLabels(
                labelRepo.findAllById(
                labelDTOS
                .stream().map(LabelDTO::getName)
                .collect(Collectors.toList()))));
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
        return SaResult.ok("Knode updated: " + id);
    }

    @Override
    public SaResult update(String title, KnodeDTO dto) {
        Knode target = knodeRepo.findByTitle(title);
        if(!Objects.isNull(target))
            return update(target.getId(), dto);
        return SaResult.error("Knode not found: " + title);
    }

    @Override
    public SaResult label(Long id, String labelName) {
        Optional<Knode> optionalKnode = knodeRepo.findById(id);
        if(optionalKnode.isEmpty())
            return SaResult.error("Knode not found: " + id);
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
    public SaResult unlabel(Long id, String labelName) {
        Optional<Knode> optionalKnode = knodeRepo.findById(id);
        if(optionalKnode.isEmpty())
            return SaResult.error("Knode not found: " + id);
        knodeRepo.unlabel(id, labelName);
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
