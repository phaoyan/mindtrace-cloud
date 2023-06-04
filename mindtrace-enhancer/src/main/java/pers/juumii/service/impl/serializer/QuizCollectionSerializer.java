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
 * data.json：
 *      {
 *          config:{
 *
 *          },
 *          ids: number[]
 *      }
 */
@Service
@ResourceType(ResourceTypes.QUIZCARD_COLLECTION)
public class QuizCollectionSerializer implements ResourceSerializer {

    private final SerializerUtils serializerUtils;

    @Autowired
    public QuizCollectionSerializer(SerializerUtils serializerUtils) {
        this.serializerUtils = serializerUtils;
    }

    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        serializerUtils.saveAsJson(meta, data);
    }
}
