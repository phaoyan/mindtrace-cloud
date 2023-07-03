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
 * type: 这个linkout的类型，目前支持： bilibili
 * url: 实际的url
 * remark: 一些说明信息（可选）
 * 不过在serializer这里数据的格式都是一样的，不一样的在于resolver对type的解析
 */
@Service
@ResourceType(ResourceTypes.LINKOUT)
public class LinkoutSerializer implements ResourceSerializer {

    private final SerializerUtils serializerUtils;

    @Autowired
    public LinkoutSerializer(SerializerUtils serializerUtils) {
        this.serializerUtils = serializerUtils;
    }

    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        serializerUtils.saveAsJson(meta, data);
    }
}
