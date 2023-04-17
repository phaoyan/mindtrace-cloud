package pers.juumii.service.impl.resolver;

import cn.hutool.core.io.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceResolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@ResourceType(ResourceType.MARKDOWN)
public class MarkdownResolver implements ResourceResolver {

    private final ResourceRepository repository;

    @Autowired
    public MarkdownResolver(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> resolve(Resource resource) {
        return resolve(repository.load(resource));
    }

    @Override
    public Object resolve(Resource resource, String name) {
        return null;
    }

    private Map<String, Object> resolve(Map<String, InputStream> dataList){
        Map<String, Object> res = new HashMap<>();
        res.put("content", IoUtil.readUtf8(dataList.get("content.md")));
        res.put("config", IoUtil.readUtf8(dataList.get("config.json")));
        return res;
    }
}
