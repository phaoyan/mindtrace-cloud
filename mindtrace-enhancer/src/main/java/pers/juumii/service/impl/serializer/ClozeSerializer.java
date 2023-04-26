package pers.juumii.service.impl.serializer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceSerializer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * data格式：
 * 前端传入raw string后端serializer直接将其存储
 * raw: 前端传入的原始数据，填空处为{{CLOZE::<txt>}}
 * 解析时，对于{{CLOZE:<txt>}}，将其在raw的字符串索引记录下来，由此每一个空都有一个[start,end]的坐标，将其存入indexes中
 * 将raw和indexes传给前端，前端负责根据这些渲染CLOZE
 * 由于milkdown默认以base64将图片等资源直接保存，所以暂不做图片资源的单独处理
 */
@Service
@ResourceType(ResourceType.CLOZE)
public class ClozeSerializer implements ResourceSerializer {


    private final ResourceRepository repository;

    @Autowired
    public ClozeSerializer(ResourceRepository repository) {
        this.repository = repository;
    }


    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        if(data == null)
            data = prototype();

        // 提取数据
        String raw = Convert.toStr(data.get("raw"));

        // 封装数据
        HashMap<String, InputStream> dataList = new HashMap<>();
        dataList.put("raw.md", IoUtil.toStream(raw, StandardCharsets.UTF_8));

        repository.save(meta.getCreateBy(), meta.getId(), dataList);
    }

    private Map<String, Object> prototype() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("raw", "");
        return res;
    }


}
