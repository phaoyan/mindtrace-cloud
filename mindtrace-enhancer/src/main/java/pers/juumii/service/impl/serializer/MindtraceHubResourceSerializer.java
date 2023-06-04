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
 * data.json
 * {
 *     id: xxx //id
 *     url: xxx //资源在mindtrace-hub的url链接
 *     title: xxx //资源名称
 *     contentType: xxx //资源类型
 *     size: xxx mb //大小
 * }
 */
@Service
@ResourceType(ResourceTypes.MINDTRACE_HUB_RESOURCE)
public class MindtraceHubResourceSerializer implements ResourceSerializer {

    private final SerializerUtils serializerUtils;

    @Autowired
    public MindtraceHubResourceSerializer(SerializerUtils serializerUtils) {
        this.serializerUtils = serializerUtils;
    }

    // 在mindtrace-resource中仅存这个资源的基本信息，原文件存在mindtrace-hub中，
    // 以避免默认的resolve方法进行多余的加载
    // 前端独立调用mindtrace hub，这里不用处理mindtrace hub方面的存储逻辑，
    // 只负责将存储完成后返回的的URL以及标题等信息保存起来
    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        serializerUtils.saveAsJson(meta, data);
    }
}
