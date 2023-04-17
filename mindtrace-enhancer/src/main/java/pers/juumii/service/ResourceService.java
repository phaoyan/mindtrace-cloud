package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import org.springframework.stereotype.Service;
import pers.juumii.data.Resource;

import java.util.List;
import java.util.Map;

@Service
public interface ResourceService {


    Boolean exists(Long userId, Long resourceId);
    // 返回resource对象元数据
    Resource getResourceMetadata(Long resourceId);

    // 将Resource中存储的所有资源以json的形式返回
    Map<String, Object> getDataFromResource(Long resourceId);

    // 将Resource中ID为dataId的资源以json的形式返回
    Object getDataFromResource(Long resourceId, String dataName);

    Resource addResourceToUser(Long userId, Resource meta, Map<String, Object> data);

    SaResult addDataToResource(Long resourceId, Map<String, Object> data);

    // 删除resource中特定的文件
    Map<String, Boolean> release(Long resourceId, List<String> data);

    // 删除resource
    void removeResourceFromUser(Long resourceId);

    void removeAllResourcesFromEnhancer(Long enhancerId);

    Resource addResourceToEnhancer(Long enhancerId, Resource meta, Map<String, Object> data);

    List<Resource> getResourcesFromEnhancer(Long enhancerId);

    // 为enhancer挂载resource
    void connectResourceToEnhancer(Long enhancerId, Long resourceId);

    // 将enhancer与resource解绑
    void disconnectResourceFromEnhancer(Long enhancerId, Long resourceId);

}
