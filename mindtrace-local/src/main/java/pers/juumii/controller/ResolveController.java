package pers.juumii.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.data.KnodeInfoCollection;
import pers.juumii.service.ResolveService;
import pers.juumii.utils.ZipUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
public class ResolveController {

    private final ResolveService resolveService;

    @Autowired
    public ResolveController(ResolveService resolveService) {
        this.resolveService = resolveService;
    }

    @PostMapping("/knode/{stemId}/all")
    public void resolveAll(@PathVariable Long stemId, @RequestParam MultipartFile file) throws IOException {
        Map<String, byte[]> data = ZipUtils.zipToMap(file.getBytes());
        KnodeInfoCollection main = JSONUtil.toBean(StrUtil.str(data.remove("main.json"), StandardCharsets.UTF_8), KnodeInfoCollection.class);
        resolveService.resolve(stemId, main, data);

    }
}
