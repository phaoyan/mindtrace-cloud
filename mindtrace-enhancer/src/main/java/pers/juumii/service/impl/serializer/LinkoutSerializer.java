package pers.juumii.service.impl.serializer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceSerializer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * data格式：
 * type: 这个linkout的类型，目前支持： bilibili
 * url: 实际的url
 *
 * 不过在serializer这里数据的格式都是一样的，不一样的在于resolver对type的解析
 */
@Service
@ResourceType(ResourceTypes.LINKOUT)
public class LinkoutSerializer implements ResourceSerializer {

    private final ResourceRepository repository;

    @Autowired
    public LinkoutSerializer(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        if(data == null)
            data = prototype();

        // 提取数据
        String type = Convert.toStr(data.get("type"));
        String url = Convert.toStr(data.get("url"));

        // 封装数据
        JSONObject json = JSONUtil.createObj();
        json.set("type", type);
        json.set("url", url);
        Map<String, InputStream> dataList = new HashMap<>();
        dataList.put("data.json", IoUtil.toStream(json.toStringPretty(), StandardCharsets.UTF_8));

        repository.save(meta.getCreateBy(), meta.getId(), dataList);
    }

    private Map<String, Object> prototype() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("type", "");
        res.put("url","");
        return res;
    }
}
