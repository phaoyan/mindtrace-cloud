package pers.juumii.service.impl.serializer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceSerializer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * data格式：
 * content->markdown字符串
 * config->json配置项，包括hide（是否为隐藏状态）
 * 因为milkdown默认图片直接存在文档里，所以暂不写图片存储的逻辑
 */
@Service
@ResourceType(ResourceType.MARKDOWN)
public class MarkdownSerializer implements ResourceSerializer {

    private final ResourceRepository repository;

    @Autowired
    public MarkdownSerializer(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        if(data == null)
            data = prototype();

        // 提取数据
        String content = Convert.toStr(data.get("content"));
        JSONObject config = JSONUtil.parseObj(data.get("config"));

        // 封装数据
        Map<String, InputStream> dataList = new HashMap<>();
        Opt.ifPresent(content, c->dataList.put("content.md", IoUtil.toStream(c, StandardCharsets.UTF_8)));
        Opt.ifPresent(config, c->dataList.put("config.json", IoUtil.toStream(config.toStringPretty(), StandardCharsets.UTF_8)));



        

        repository.save(meta.getCreateBy(), meta.getId(), dataList);
    }

    public static Map<String, Object> prototype(){
        HashMap<String, Object> res = new HashMap<>();
        res.put("content", "");
        return res;
    }
}
