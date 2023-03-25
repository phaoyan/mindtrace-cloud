package pers.juumii.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.lang.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.annotation.CheckEnhancerExistence;
import pers.juumii.annotation.CheckResourceExistence;
import pers.juumii.annotation.CheckUserExistence;
import pers.juumii.data.Resource;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.mapper.ResourceMapper;
import pers.juumii.service.ResourceService;
import pers.juumii.service.impl.router.ResourceRouter;

import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMapper resourceMapper;
    private final ResourceRouter resourceRouter;

    @Autowired
    public ResourceServiceImpl(
            ResourceMapper resourceMapper,
            ResourceRouter resourceRouter) {
        this.resourceMapper = resourceMapper;
        this.resourceRouter = resourceRouter;
    }

    @Override
    @CheckUserExistence
    @CheckResourceExistence
    public SaResult fetch(Long resourceId, Long userId) {
        Resource meta = resourceMapper.selectById(resourceId);
        Map<String, Object> data = resourceRouter.route(meta).resolve(meta);
        return SaResult.data(data);
    }

    @Override
    @CheckUserExistence
    public SaResult put(Long userId, MultipartFile file, ResourceDTO metaDTO) {
        Resource meta = Resource.prototype(metaDTO);
        resourceMapper.insert(meta);
        resourceRouter.route(meta, file).serialize(meta, file);
        return SaResult.ok();
    }

    @Override
    @CheckUserExistence
    @CheckResourceExistence
    public SaResult modify(Long resourceId, Long userId, ResourceDTO metaDTO) {
        Resource meta = resourceMapper.selectById(resourceId);
        Opt.of(metaDTO.getUrl()).ifPresent(meta::setUrl);
        Opt.of(metaDTO.getType()).ifPresent(meta::setType);
        Opt.of(metaDTO.getPrivacy()).ifPresent(meta::setPrivacy);
        Opt.of(metaDTO.getCreateBy()).ifPresent(meta::setCreateBy);
        Opt.of(metaDTO.getCreateTime()).ifPresent(meta::setCreateTime);
        resourceMapper.updateById(meta);
        return SaResult.ok();
    }

    @Override
    @CheckUserExistence
    @CheckResourceExistence
    public SaResult alterSource(Long resourceId, Long userId, MultipartFile source) {
        Resource meta = resourceMapper.selectById(resourceId);
        resourceRouter.route(meta, source).serialize(meta, source);
        return SaResult.ok();
    }


    @Override
    @CheckResourceExistence
    @CheckEnhancerExistence
    public SaResult disconnect(Long resourceId, Long enhancerId) {
        resourceMapper.deleteById(resourceId);

        return null;
    }
}
