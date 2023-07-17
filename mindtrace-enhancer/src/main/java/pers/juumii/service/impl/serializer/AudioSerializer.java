package pers.juumii.service.impl.serializer;

import cn.hutool.core.io.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceSerializer;

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
        byte[] audio = (byte[]) data.get("audio");
        String contentType = (String) data.get("Content-Type");
        repository.save(meta.getCreateBy(), meta.getId(), "audio", IoUtil.toStream(audio));
        repository.setMeta(meta.getCreateBy(), meta.getId(), "audio", Map.of("Content-Type", contentType));

    }
}
