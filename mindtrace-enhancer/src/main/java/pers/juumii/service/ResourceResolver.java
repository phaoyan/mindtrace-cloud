package pers.juumii.service;

import pers.juumii.data.Resource;

import java.util.Map;

public interface ResourceResolver {
    // 通过Resource拿到相应资源文件并进行适当解析，将数据以json的格式返回给前端
    Map<String, Object> resolve(Resource resource);

    Object resolve(Resource resource, String name);

}
