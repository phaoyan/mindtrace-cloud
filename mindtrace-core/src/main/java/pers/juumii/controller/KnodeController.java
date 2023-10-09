package pers.juumii.controller;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.KnodeService;
import pers.juumii.utils.AuthUtils;

import java.util.List;

@RestController
public class KnodeController {

    private final KnodeService knodeService;
    private final AuthUtils authUtils;
    private final CoreClient coreClient;

    @Autowired
    public KnodeController(
            KnodeService knodeService,
            AuthUtils authUtils,
            CoreClient coreClient) {
        this.knodeService = knodeService;
        this.authUtils = authUtils;
        this.coreClient = coreClient;
    }

    public void knodeSameUser(Long knodeId){
        KnodeDTO knode = coreClient.check(knodeId);
        authUtils.same(Convert.toLong(knode.getCreateBy()));
    }

    @PostMapping("/knode/{knodeId}/branch")
    public KnodeDTO branch(
            @PathVariable Long knodeId,
            @RequestParam String title){
        knodeSameUser(knodeId);
        return Knode.transfer(knodeService.branch(knodeId, title));
    }

    @DeleteMapping("/knode/{knodeId}")
    public void delete(@PathVariable Long knodeId){
        knodeSameUser(knodeId);
        knodeService.delete(knodeId);
    }

    @PostMapping("/knode/{knodeId}")
    public void update(
            @PathVariable Long knodeId,
            @RequestBody KnodeDTO dto){
        knodeSameUser(knodeId);
        knodeService.update(knodeId, dto);
    }

    @PostMapping("/knode/{knodeId}/createTime")
    public void editCreateTime(@PathVariable Long knodeId, @RequestParam String createTime){
        knodeSameUser(knodeId);
        knodeService.editCreateTime(knodeId, createTime);
    }

    @PostMapping("/knode/{knodeId}/createBy")
    public void editCreateBy(@PathVariable Long knodeId, @RequestParam String createBy){
        knodeSameUser(knodeId);
        knodeService.editCreateBy(knodeId, createBy);
    }

    @PostMapping("/knode/{knodeId}/title")
    public void editTitle(@PathVariable Long knodeId, @RequestParam String title){
        knodeSameUser(knodeId);
        knodeService.editTitle(knodeId, title);
    }

    @PostMapping("/knode/{knodeId}/index")
    public void editIndex(@PathVariable Long knodeId, @RequestParam Integer index){
        knodeSameUser(knodeId);
        knodeService.editIndex(knodeId, index);
    }

    // 将id为branchId的Knode移动到id为stemId的Knode下方
    @PostMapping("/knode/{stemId}/branch/{branchId}")
    public List<KnodeDTO> shift(
            @PathVariable Long stemId,
            @PathVariable Long branchId){
        knodeSameUser(stemId);
        knodeSameUser(branchId);
        return Knode.transfer(knodeService.shift(stemId, branchId));
    }

    @PostMapping("/knode/{knodeId}/branch/index/{index1}/{index2}")
    public void swapIndex(
            @PathVariable Long knodeId,
            @PathVariable Integer index1,
            @PathVariable Integer index2){
        knodeSameUser(knodeId);
        knodeService.swapIndex(knodeId, index1, index2);
    }

    @PostMapping("knode/connection")
    public void connect(@RequestParam Long knodeId1, @RequestParam Long knodeId2){
        knodeSameUser(knodeId1);
        knodeSameUser(knodeId2);
        knodeService.connect(knodeId1, knodeId2);
    }

    @DeleteMapping("/knode/connection")
    public void disconnect(@RequestParam Long knodeId1, @RequestParam Long knodeId2){
        knodeSameUser(knodeId1);
        knodeSameUser(knodeId2);
        knodeService.disconnect(knodeId1, knodeId2);
    }

}
