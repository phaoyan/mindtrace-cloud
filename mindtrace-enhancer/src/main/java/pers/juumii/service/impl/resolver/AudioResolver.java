package pers.juumii.service.impl.resolver;

import cn.hutool.core.io.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceResolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * data格式：
 * {
 *     contentType: 音频格式字符串，如mp3等
 * }
 * 具体数据通过url请求直接获得
 */
@Service
@ResourceType(ResourceTypes.AUDIO)
public class AudioResolver implements ResourceResolver {

    private final ResourceRepository repository;

    @Autowired
    public AudioResolver(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Object resolve(Resource resource, String name) {
        if(name.equals("audio"))
            return IoUtil.readBytes(repository.load(resource.getCreateBy(), resource.getId(), name));
        return null;
    }

    @Override
    public Map<String, Object> resolve(Map<String, InputStream> dataList) {
        return null;
    }

    @Override
    public Map<String, Object> resolve(Resource resource){
        Map<String, Object> meta = repository.getMeta(resource.getCreateBy(), resource.getId(), "audio");
        String contentType = (String) meta.get("Content-Type");
        HashMap<String, Object> res = new HashMap<>();
        res.put("contentType", contentType);
        return res;
    }
}
