package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeService;

import java.util.List;

@RestController
public class KnodeController {

    private final KnodeService knodeService;

    @Autowired
    public KnodeController(KnodeService knodeService) {
        this.knodeService = knodeService;
    }

    @PostMapping("/knode/{knodeId}/branch")
    public KnodeDTO branch(
            @PathVariable Long knodeId,
            @RequestParam("title") String title){
        return Knode.transfer(knodeService.branch(knodeId, title));
    }

    @DeleteMapping("/knode/{knodeId}")
    public SaResult delete(@PathVariable Long knodeId){
        return knodeService.delete(knodeId);
    }

    @PostMapping("/knode/{knodeId}")
    public SaResult update(
            @PathVariable Long knodeId,
            @RequestBody KnodeDTO dto){
        return knodeService.update(knodeId, dto);
    }

    // 将id为branchId的Knode移动到id为stemId的Knode下方
    @PostMapping("/knode/{stemId}/branch/{branchId}")
    public List<KnodeDTO> shift(
            @PathVariable Long stemId,
            @PathVariable Long branchId){
        return Knode.transfer(knodeService.shift(stemId, branchId));
    }

    @PostMapping("/knode/{sourceId}/connection/{targetId}")
    public SaResult connect(
            @PathVariable Long sourceId,
            @PathVariable Long targetId){
        return knodeService.connect(sourceId, targetId);
    }

    @PostMapping("/knode/{knodeId}/branch/index/{index1}/{index2}")
    public SaResult swapIndex(
            @PathVariable Long knodeId,
            @PathVariable Integer index1,
            @PathVariable Integer index2){
        knodeService.swapIndex(knodeId, index1, index2);
        return SaResult.ok();
    }

}
