package pers.juumii.service.impl.serializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceSerializer;

import java.util.Map;

/**
 * data格式：
 * front->md字符串
 * back->md字符串
 */
@Service
@ResourceType(ResourceTypes.QUIZCARD)
public class QuizcardSerializer implements ResourceSerializer {

    private final SerializerUtils serializerUtils;

    @Autowired
    public QuizcardSerializer(SerializerUtils serializerUtils) {
        this.serializerUtils = serializerUtils;
    }


    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        serializerUtils.saveAsJson(meta, data);
    }


}
