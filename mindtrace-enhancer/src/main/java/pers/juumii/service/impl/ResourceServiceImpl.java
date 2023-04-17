package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Resource;
import pers.juumii.mapper.EnhancerMapper;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceService;
import pers.juumii.service.impl.router.ResourceRouter;
import pers.juumii.utils.DataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {


    private final ResourceRouter router;
    private final ResourceMapper resourceMapper;
    private final ResourceRepository repository;
    private final EnhancerMapper enhancerMapper;

    @Autowired
    public ResourceServiceImpl(
            ResourceRouter router,
            ResourceMapper resourceMapper,
            ResourceRepository repository,
            EnhancerMapper enhancerMapper) {
        this.router = router;
        this.resourceMapper = resourceMapper;
        this.repository = repository;
        this.enhancerMapper = enhancerMapper;
    }

    @Override
    public Boolean exists(Long userId, Long resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        return resource != null && resource.getCreateBy().equals(userId);
    }

    @Override
    public Resource getResourceMetadata(Long resourceId) {
        return resourceMapper.selectById(resourceId);
    }

    @Override
    public Map<String, Object> getDataFromResource(Long resourceId) {
        Resource meta = getResourceMetadata(resourceId);
        return router.resolver(meta).resolve(meta);
    }

    @Override
    public Object getDataFromResource(Long resourceId, String dataName) {
        Resource meta = getResourceMetadata(resourceId);
        return router.resolver(meta).resolve(meta, dataName);
    }

    @Override
    public Resource addResourceToUser(Long userId, Resource meta, Map<String, Object> data) {
        meta = Resource.prototype(meta);
        resourceMapper.insert(meta);
        router.serializer(meta).serialize(meta, data);
        return meta;
    }

    @Override
    public SaResult addDataToResource(Long resourceId, Map<String, Object> data) {
        Resource meta = getResourceMetadata(resourceId);
        router.serializer(meta).serialize(meta, data);
        return SaResult.data(meta);
    }

    @Override
    public Map<String, Boolean> release(Long resourceId, List<String> data) {
        Resource meta = getResourceMetadata(resourceId);
        Map<String, Boolean> deleted = new HashMap<>();
        for(String dataName: data)
            deleted.put(dataName, repository.release(meta.getCreateBy() ,resourceId, dataName));
        return deleted;
    }

    @Override
    public void removeResourceFromUser(Long resourceId) {
        // 先释放该resource的所有资源文件再删除resource
        Long userId = getResourceMetadata(resourceId).getCreateBy();
        repository.releaseAll(userId, resourceId);
        resourceMapper.deleteById(resourceId);
    }

    @Override
    public void removeAllResourcesFromEnhancer(Long enhancerId) {
        for(Resource resource: getResourcesFromEnhancer(enhancerId))
            removeResourceFromUser(resource.getId());
    }

    @Override
    public Resource addResourceToEnhancer(
            Long enhancerId,
            Resource meta,
            Map<String, Object> data) {
        Long userId = enhancerMapper.selectById(enhancerId).getCreateBy();
        Resource resource = addResourceToUser(userId, meta, data);
        connectResourceToEnhancer(enhancerId, resource.getId());
        return resource;
    }

    @Override
    public List<Resource> getResourcesFromEnhancer(Long enhancerId) {
        return DataUtils.deNull(resourceMapper.queryByEnhancerId(enhancerMapper.selectById(enhancerId).getId()));
    }

    @Override
    public void connectResourceToEnhancer(Long enhancerId, Long resourceId) {
        resourceMapper.connectResourceToEnhancer(enhancerId, resourceId);
    }

    @Override
    public void disconnectResourceFromEnhancer(Long enhancerId, Long resourceId) {
        resourceMapper.disconnectResourceFromEnhancer(enhancerId, resourceId);
    }


}
