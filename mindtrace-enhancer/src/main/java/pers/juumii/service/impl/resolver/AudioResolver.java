package pers.juumii.service.impl.resolver;

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
        return null;
    }

    @Override
    public Map<String, Object> resolve(Map<String, InputStream> dataList) {
        return null;
    }

    @Override
    public Map<String, Object> resolve(Resource resource){
        Map<String, InputStream> data = repository.load(resource.getCreateBy(), resource.getId());
        InputStream audio = data.get("audio");
        Map<String, String> meta = repository.getMeta(resource.getCreateBy(), resource.getId(), "audio");
        String contentType = meta.get("Content-Type");
        HashMap<String, Object> res = new HashMap<>();
        res.put("audio", audio);
        res.put("contentType", contentType);
        return res;
    }
}
