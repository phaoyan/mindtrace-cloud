package pers.juumii.service;

import pers.juumii.data.Resource;
import pers.juumii.utils.SpringUtils;

import java.io.InputStream;
import java.util.Map;

public interface ResourceResolver {
    // 通过Resource拿到相应资源文件并进行适当解析，将数据以json的格式返回给前端
    Map<String, Object> resolve(Resource resource);

    // 返回具体单个文件的数据
    Object resolve(Resource resource, String name);

    Map<String, Object> resolve(Map<String, InputStream> dataList);

}
