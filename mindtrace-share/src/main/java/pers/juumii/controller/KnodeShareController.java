package pers.juumii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.KnodeShare;
import pers.juumii.dto.share.EnhancerShareDTO;
import pers.juumii.dto.share.KnodeShareDTO;
import pers.juumii.service.KnodeShareService;

import java.util.List;

@RestController
public class KnodeShareController {

    private final KnodeShareService knodeShareService;

    @Autowired
    public KnodeShareController(KnodeShareService knodeShareService) {
        this.knodeShareService = knodeShareService;
    }

    @GetMapping("/knode/{knodeId}")
    public KnodeShareDTO getKnodeShare(@PathVariable Long knodeId){
        return KnodeShare.transfer(knodeShareService.getKnodeShare(knodeId));
    }

    @PostMapping("/knode/{knodeId}")
    public void updateKnodeShare(@PathVariable Long knodeId, @RequestBody KnodeShareDTO dto){
        knodeShareService.updateKnodeShare(knodeId, dto);
    }

    @GetMapping("/knode/{knodeId}/similar")
    public List<KnodeShareDTO> getRelatedKnodeShare(
            @PathVariable Long knodeId,
            @RequestParam Long count){
        return KnodeShare.transfer(knodeShareService.getRelatedKnodeShare(knodeId, count));
    }

    @PostMapping("/knodeShare/{shareId}/to/{targetId}")
    public void forkKnodeShare(@PathVariable Long shareId, @PathVariable Long targetId){
        knodeShareService.forkKnodeShare(shareId, targetId);
    }

}
