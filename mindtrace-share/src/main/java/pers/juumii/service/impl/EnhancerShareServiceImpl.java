package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.EnhancerShare;
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.enhancer.ResourceDTO;
import pers.juumii.dto.enhancer.ResourceWithData;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.mapper.EnhancerShareMapper;
import pers.juumii.mapper.KnodeShareMapper;
import pers.juumii.service.EnhancerShareService;

import java.util.*;

@Service
public class EnhancerShareServiceImpl implements EnhancerShareService {

    private final CoreClient coreClient;
    private final EnhancerClient enhancerClient;
    private final EnhancerShareMapper enhancerShareMapper;
    private final KnodeShareMapper knodeShareMapper;

    @Autowired
    public EnhancerShareServiceImpl(
            CoreClient coreClient,
            EnhancerClient enhancerClient,
            EnhancerShareMapper enhancerShareMapper,
            KnodeShareMapper knodeShareMapper) {
        this.coreClient = coreClient;
        this.enhancerClient = enhancerClient;
        this.enhancerShareMapper = enhancerShareMapper;
        this.knodeShareMapper = knodeShareMapper;
    }

    @Override
    public List<EnhancerShare> getOwnedEnhancerShare(Long knodeId){
        KnodeDTO knode = coreClient.check(knodeId);
        if(knode == null || knodeShareMapper.selectByKnodeId(knodeId) == null)
            return new ArrayList<>();
        List<EnhancerDTO> enhancers = enhancerClient.getEnhancersOfKnode(knodeId);
        return enhancers.stream().map(enhancer -> getEnhancerShare(Convert.toLong(enhancer.getId()))).toList();
    }

    @Override
    public EnhancerDTO forkEnhancerShare(Long shareId, Long targetId) {
        long userId = StpUtil.getLoginIdAsLong();
        // 提取数据
        EnhancerShare share = enhancerShareMapper.selectById(shareId);
        EnhancerDTO enhancer = enhancerClient.getEnhancerById(share.getEnhancerId());
        List<ResourceDTO> resources = enhancerClient.getResourcesOfEnhancer(share.getEnhancerId());

        // 添加新数据
        EnhancerDTO _enhancer = enhancerClient.addEnhancerToKnode(targetId);
        List<ResourceDTO> _resources = new ArrayList<>();
        for(ResourceDTO resource: resources){
            ResourceWithData resourceWithData = new ResourceWithData();
            ResourceDTO meta = new ResourceDTO();
            meta.setCreateBy(Long.toString(userId));
            meta.setTitle(resource.getTitle());
            meta.setType(resource.getType());
            Map<String, byte[]> data = enhancerClient.getDataFromResource(Convert.toLong(resource.getId()));
            resourceWithData.setMeta(meta);
            resourceWithData.setData(data);
            ResourceDTO _resource = enhancerClient.addResourceToEnhancer(Convert.toLong(_enhancer.getId()), resourceWithData);
            _resources.add(_resource);
        }

        _enhancer.setTitle(enhancer.getTitle());
        _enhancer.setCreateBy(Long.toString(userId));
        _enhancer.setLabels(enhancer.getLabels());
        _enhancer.setResourceIds(_resources.stream().map(ResourceDTO::getId).toList());
        _enhancer.setIsQuiz(enhancer.getIsQuiz());
        enhancerClient.updateEnhancer(Convert.toLong(_enhancer.getId()), _enhancer);

        return _enhancer;
    }

    @Override
    public EnhancerShare getEnhancerShare(Long enhancerId) {
        EnhancerShare res = enhancerShareMapper.selectByEnhancerId(enhancerId);
        if(res == null){
            EnhancerDTO enhancer = enhancerClient.getEnhancerById(enhancerId);
            res = EnhancerShare.prototype(Convert.toLong(enhancer.getCreateBy()), enhancerId);
            enhancerShareMapper.insert(res);
        }
        return res;
    }
}
