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
 * knodes: 数组，每一条数据的格式为
 *      {
 *          knodeId: string,
 *          stemId: string,
 *          tag: boolean,
 *          unfolded: boolean
 *      }，
 *      tag为true时这个knode被用户标记，
 *      unfolded为true时这个knode默认就是展开的
 * configs: 一些配置
 *      {
 *          hotUpdate: boolean
 *      }
 */
@Service
@ResourceType(ResourceTypes.UNFOLDING)
public class UnfoldingSerializer implements ResourceSerializer {

    private final SerializerUtils serializerUtils;

    @Autowired
    public UnfoldingSerializer(SerializerUtils serializerUtils) {
        this.serializerUtils = serializerUtils;
    }

    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        serializerUtils.saveAsJson(meta, data);
    }
}
