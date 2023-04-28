package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.controller.aop.ControllerAspect;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeService;

@RestController
@RequestMapping("/user/{userId}/knode")
public class KnodeController {

    private final KnodeService knodeService;
    private final ControllerAspect aspect;

    @Autowired
    public KnodeController(
            KnodeService knodeService,
            ControllerAspect aspect) {
        this.knodeService = knodeService;
        this.aspect = aspect;
    }

    @PostMapping("/{knodeId}/branch")
    public Object branch(
            @PathVariable Long userId,
            @PathVariable Long knodeId,
            @RequestParam("title") String title){
        aspect.checkKnodeAvailability(userId, knodeId);
        return Knode.transfer(knodeService.branch(userId, knodeId, title));
    }

    @PostMapping("/{knodeId}/label")
    public Object addLabelToKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId,
            @RequestParam("label") String label){
        aspect.checkKnodeAvailability(userId, knodeId);
        return knodeService.addLabelToKnode(knodeId, label);
    }

    @DeleteMapping("/{knodeId}/label")
    public Object removeLabelFromKnode(
            @PathVariable Long userId,
            @PathVariable Long knodeId,
            @RequestParam("label") String label){
        aspect.checkKnodeAvailability(userId, knodeId);
        return knodeService.removeLabelFromKnode(knodeId, label);
    }

    @DeleteMapping("/{knodeId}")
    public Object delete(
            @PathVariable Long userId,
            @PathVariable Long knodeId){
        aspect.checkKnodeAvailability(userId, knodeId);
        return knodeService.delete(knodeId);
    }

    @PostMapping("/{knodeId}")
    public Object update(
            @PathVariable Long userId,
            @PathVariable Long knodeId,
            @RequestBody KnodeDTO dto){
        aspect.checkKnodeAvailability(userId, knodeId);
        return knodeService.update(knodeId, dto);
    }

    // 将id为branchId的Knode移动到id为stemId的Knode下方
    @PostMapping("/{stemId}/branch/{branchId}")
    public Object shift(
            @PathVariable Long userId,
            @PathVariable Long stemId,
            @PathVariable Long branchId){
        aspect.checkKnodeAvailability(userId, stemId);
        aspect.checkKnodeAvailability(userId, branchId);
        return Knode.transfer(knodeService.shift(stemId, branchId, userId));
    }

    @PostMapping("/{sourceId}/connection/{targetId}")
    public Object connect(
            @PathVariable Long userId,
            @PathVariable Long sourceId,
            @PathVariable Long targetId){
        aspect.checkKnodeAvailability(userId, sourceId);
        aspect.checkKnodeAvailability(userId, targetId);
        return knodeService.connect(sourceId, targetId);
    }

    @PostMapping("/index")
    public Object initIndex(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return Knode.transfer(knodeService.initIndex(userId));
    }

    @PostMapping("/{knodeId}/branch/index/{index1}/{index2}")
    public Object swapIndex(
            @PathVariable Long userId,
            @PathVariable Long knodeId,
            @PathVariable Integer index1,
            @PathVariable Integer index2){
        aspect.checkKnodeAvailability(userId, knodeId);
        knodeService.swapIndex(userId, knodeId, index1, index2);
        return SaResult.ok();
    }

}
