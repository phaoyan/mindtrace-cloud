package pers.juumii.service.impl.resolver;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.dto.WebsiteDTO;
import pers.juumii.feign.SpiderClient;
import pers.juumii.service.ResourceResolver;

import java.io.InputStream;
import java.util.Map;

/**
 * data格式：
 * type: 这个linkout的类型，目前支持： bilibili
 * url: 实际的url
 */
@Service
@ResourceType(ResourceType.LINKOUT)
public class LinkoutResolver implements ResourceResolver {

    private final SpiderClient client;

    @Autowired
    public LinkoutResolver(SpiderClient client) {
        this.client = client;
    }

    @Override
    public Object resolve(Resource resource, String name) {
        return null;
    }

    @Override
    public Map<String, Object> resolve(Map<String, InputStream> dataList) {
        String json = IoUtil.readUtf8(dataList.get("data.json"));
        WebsiteDTO data = JSONUtil.toBean(json, WebsiteDTO.class);
        Map<String, Object> res = client.getWebsiteInfo(data);
        res.put("url", data.getUrl());
        res.put("type", data.getType());
        return res;
    }
}
