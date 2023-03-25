package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeService;

@RestController
@RequestMapping("/user/{userId}/knode")
public class KnodeController {

    private final KnodeService knodeService;

    @Autowired
    public KnodeController(KnodeService knodeService) {
        this.knodeService = knodeService;
    }

    @PostMapping("/{id}/branch")
    public Object branch(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam("title") String title){
        return knodeService.branch(id, title);
    }

    @PostMapping("/{id}/label")
    public Object label(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam("label") String label){
        return knodeService.label(id, label);
    }

    @DeleteMapping("/{id}/label")
    public Object unlabel(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam("label") String label){
        return knodeService.unlabel(id, label);
    }

    @DeleteMapping("/{id}")
    public Object delete(
            @PathVariable Long userId,
            @PathVariable Long id){
        return knodeService.delete(id);
    }

    @PostMapping("/{id}")
    public Object update(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestBody KnodeDTO dto){
        return knodeService.update(id, dto);
    }

    // 将id为branchId的Knode移动到id为stemId的Knode下方
    @PostMapping("/{stemId}/branch/{branchId}")
    public Object shift(
            @PathVariable Long userId,
            @PathVariable Long stemId,
            @PathVariable Long branchId){
        return knodeService.shift(stemId, branchId);
    }

    @PostMapping("/{sourceId}/connection/{targetId}")
    public Object connect(
            @PathVariable Long userId,
            @PathVariable Long sourceId,
            @PathVariable Long targetId){
        return knodeService.connect(sourceId, targetId);
    }

}
