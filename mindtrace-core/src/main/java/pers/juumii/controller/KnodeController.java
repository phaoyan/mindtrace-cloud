package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.service.KnodeService;
import pers.juumii.utils.SaResult;

@RestController
@RequestMapping("/knode")
public class KnodeController {

    private final KnodeService knodeService;

    @Autowired
    public KnodeController(KnodeService knodeService) {
        this.knodeService = knodeService;
    }

    @PostMapping("/{id}/branch")
    public SaResult branch(
            @PathVariable Long id,
            @RequestParam("title") String title){
        return knodeService.branch(id, title);
    }

    @PostMapping("/branch")
    public SaResult branch(
            @RequestParam("stemTitle") String stemTitle,
            @RequestParam("title") String title){
        return knodeService.branch(stemTitle, title);
    }

    @PostMapping("/{id}/label")
    public SaResult label(
            @PathVariable Long id,
            @RequestParam("label") String label){
        return knodeService.label(id, label);
    }

    @DeleteMapping("/{id}/label")
    public SaResult unlabel(
            @PathVariable Long id,
            @RequestParam("label") String label){
        return knodeService.unlabel(id, label);
    }

    @DeleteMapping("/{id}")
    public SaResult delete(@PathVariable Long id){
        return knodeService.delete(id);
    }

    @DeleteMapping
    public SaResult delete(@RequestParam("title") String title){
        return knodeService.delete(title);
    }

    @PostMapping("/{id}")
    public SaResult update(
            @PathVariable Long id,
            @RequestBody KnodeDTO dto){
        return knodeService.update(id, dto);
    }

    @PostMapping
    public SaResult update(
            @RequestParam("title") String title,
            @RequestBody KnodeDTO data){
        return knodeService.update(title, data);
    }

    @GetMapping("/{id}")
    public SaResult check(@PathVariable Long id){
        return knodeService.check(id);
    }

    @GetMapping
    public SaResult check(@RequestParam("title") String title){
        return knodeService.check(title);
    }

    // 将id为branchId的Knode移动到id为stemId的Knode下方
    @PostMapping("/{stemId}/branch/{branchId}")
    public SaResult shift(
            @PathVariable Long stemId,
            @PathVariable Long branchId){
        return knodeService.shift(stemId, branchId);
    }

    @PostMapping("/{sourceId}/connection/{targetId}")
    public SaResult connect(
            @PathVariable Long sourceId,
            @PathVariable Long targetId){
        return knodeService.connect(sourceId, targetId);
    }

}
