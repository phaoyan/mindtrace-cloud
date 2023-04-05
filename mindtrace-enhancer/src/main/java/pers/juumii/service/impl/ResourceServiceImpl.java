package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Resource;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceService;
import pers.juumii.service.impl.router.ResourceRouter;
import pers.juumii.utils.DataUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRouter router;
    private final ResourceMapper resourceMapper;
    private final ResourceRepository repository;

    @Autowired
    public ResourceServiceImpl(
            ResourceRouter router,
            ResourceMapper resourceMapper,
            ResourceRepository repository) {
        this.router = router;
        this.resourceMapper = resourceMapper;
        this.repository = repository;
    }

    @Override
    public Boolean exists(Long userId, Long resourceId) {
        try {
            getResourceMetadata(userId, resourceId);
            return true;
        }catch (Throwable e){
            return false;
        }
    }

    @Override
    public Resource getResourceMetadata(Long userId, Long resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        if(!resource.getCreateBy().equals(userId))
            throw new RuntimeException("Resource access not allowed: " + resourceId);
        return resource;
    }

    @Override
    public Map<String, Object> getDataFromResource(Long userId, Long resourceId) {
        Resource meta = getResourceMetadata(userId, resourceId);
        return router.resolver(meta).resolve(meta);
    }

    @Override
    public Object getDataFromResource(Long userId, Long resourceId, String dataName) {
        Resource meta = getResourceMetadata(userId, resourceId);
        return router.resolver(meta).resolve(meta, dataName);
    }

    @Override
    public SaResult addResourceToUser(Long userId, Resource meta, Map<String, Object> data) {
        meta = Resource.prototype(meta);
        resourceMapper.insert(meta);
        router.serializer(meta).serialize(meta, data);
        return SaResult.data(meta);
    }

    @Override
    public SaResult addDataToResource(Long userId, Long resourceId, Map<String, Object> data) {
        Resource meta = getResourceMetadata(userId, resourceId);
        router.serializer(meta).serialize(meta, data);
        return SaResult.data(meta);
    }

    @Override
    public SaResult release(Long userId, Long resourceId, List<String> data) {
        // 权限检验
        getResourceMetadata(userId, resourceId);
        Map<String, Boolean> deleted = new HashMap<>();
        for(String dataName: data)
            deleted.put(dataName, repository.release(userId, resourceId, dataName));
        return SaResult.data(deleted);
    }

    @Override
    public SaResult delete(Long userId, Long resourceId) {
        // 先释放该resource的所有资源文件再删除resource
        // releaseAll中包含了权限检验
        SaResult res = releaseAll(userId, resourceId);
        resourceMapper.deleteById(resourceId);
        return res;
    }

    @Override
    public Resource addResourceToEnhancer(
            Long userId,
            Long enhancerId,
            Resource meta,
            Map<String, Object> data) {
        return null;
    }

    @Override
    public List<Resource> getResourcesFromEnhancer(Long userId, Long enhancerId) {
        return null;
    }

    private SaResult releaseAll(Long userId, Long resourceId) {
        List<String> dataNames = DataUtils.arrayToList(repository.peek(userId, resourceId))
                                 .stream().map(File::getName).toList();
        return release(userId, resourceId, dataNames);
    }


}
