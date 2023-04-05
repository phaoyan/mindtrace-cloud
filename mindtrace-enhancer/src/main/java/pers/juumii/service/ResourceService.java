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
    Resource getResourceMetadata(Long userId, Long resourceId);

    // 将Resource中存储的所有资源以json的形式返回
    Map<String, Object> getDataFromResource(Long userId, Long resourceId);

    // 将Resource中ID为dataId的资源以json的形式返回
    Object getDataFromResource(Long userId, Long resourceId, String dataName);

    SaResult addResourceToUser(Long userId, Resource meta, Map<String, Object> data);

    SaResult addDataToResource(Long userId, Long resourceId, Map<String, Object> data);

    // 删除resource中特定的文件
    SaResult release(Long userId, Long resourceId, List<String> data);

    // 删除resource
    SaResult delete(Long userId, Long resourceId);

    Resource addResourceToEnhancer(Long userId, Long enhancerId, Resource meta, Map<String, Object> data);

    List<Resource> getResourcesFromEnhancer(Long userId, Long enhancerId);

}
