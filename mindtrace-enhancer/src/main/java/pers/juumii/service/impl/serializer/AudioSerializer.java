package pers.juumii.service.impl.serializer;

import cn.hutool.core.io.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceSerializer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@ResourceType(ResourceTypes.AUDIO)
public class AudioSerializer implements ResourceSerializer {

    private final ResourceRepository repository;

    @Autowired
    public AudioSerializer(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        byte[] audio = ((String)data.get("audio")).getBytes(StandardCharsets.UTF_8);
        String contentType = (String) data.get("contentType");
        repository.save(meta.getCreateBy(), meta.getId(), "audio" ,IoUtil.toStream(audio));
        repository.setMeta(meta.getCreateBy(), meta.getId(), "audio", Map.of("Content-Type", contentType));
    }

    @Override
    public void serialize(Resource meta, String dataName, Object data) {
        if(dataName.equals("audio"))
            repository.save(meta.getCreateBy(), meta.getId(), "audio", (InputStream) data);
        if(dataName.equals("contentType"))
            repository.setMeta(meta.getCreateBy(), meta.getId(), "audio", Map.of("Content-Type", (String) data));
    }
}
