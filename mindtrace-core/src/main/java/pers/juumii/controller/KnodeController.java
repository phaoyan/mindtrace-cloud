package pers.juumii.controller;

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
    public KnodeController(KnodeService knodeService, ControllerAspect aspect) {
        this.knodeService = knodeService;
        this.aspect = aspect;
    }

    @PostMapping("/{knodeId}/branch")
    public Object branch(
            @PathVariable Long userId,
            @PathVariable Long knodeId,
            @RequestParam("title") String title){
        aspect.checkKnodeAvailability(userId, knodeId);
        KnodeDTO res = Knode.transfer(knodeService.branch(knodeId, title));
        return res;
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

    // 将标记为 deleted 的knode彻底删除
    @DeleteMapping("/clear")
    public Object clear(@PathVariable Long userId){
        aspect.checkUserExistence(userId);
        return knodeService.clear(userId);
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
        return knodeService.shift(stemId, branchId);
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

}
