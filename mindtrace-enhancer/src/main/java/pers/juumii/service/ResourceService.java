package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.Resource;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.ResourceDTO;

import java.util.List;
import java.util.Map;

@Service
public interface ResourceService {

    Resource addResource();
    Resource addResourceToEnhancer(Long enhancerId, ResourceDTO dto);
    // 返回resource对象元数据
    Resource getResource(Long resourceId);
    List<Resource> getResourcesOfEnhancer(Long enhancerId);
    List<Resource> getResourcesOfKnode(Long knodeId);
    // 将Resource中存储的所有资源以json的形式返回
    Map<String, byte[]> getDataFromResource(Long resourceId);
    // 删除resource
    void removeResource(Long resourceId);
    void removeAllResourcesFromEnhancer(Long enhancerId);
    // 将Resource中ID为dataId的资源以json的形式返回
    byte[] getDataFromResource(Long resourceId, String dataName);
    void addDataToResource(Long resourceId, Map<String, byte[]> data);
    void addDataToResource(Long resourceId, String dataName, byte[] data);
    // 删除resource中特定的文件
    Map<String, Boolean> release(Long resourceId, List<String> data);
    // 为enhancer挂载resource
    void connectResourceToEnhancer(Long enhancerId, Long resourceId);
    // 将enhancer与resource解绑
    void disconnectResourceFromEnhancer(Long enhancerId, Long resourceId);

    List<IdPair> getEnhancerResourceRels(List<Long> enhancerIds);

    void editTitle(Long resourceId, String title);

    void editType(Long resourceId, String type);

    void editCreateTime(Long resourceId, String createTime);

}
