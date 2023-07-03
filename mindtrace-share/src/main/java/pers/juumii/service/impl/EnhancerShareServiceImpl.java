package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.EnhancerShare;
import pers.juumii.data.KnodeShare;
import pers.juumii.dto.EnhancerDTO;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.dto.ResourceWithData;
import pers.juumii.dto.share.EnhancerShareDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.EnhancerClient;
import pers.juumii.mapper.EnhancerShareMapper;
import pers.juumii.mapper.KnodeShareMapper;
import pers.juumii.service.EnhancerShareService;
import pers.juumii.service.KnodeShareService;
import pers.juumii.utils.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnhancerShareServiceImpl implements EnhancerShareService {

    private final CoreClient coreClient;
    private final KnodeShareService knodeShareService;
    private final EnhancerClient enhancerClient;
    private final EnhancerShareMapper enhancerShareMapper;
    private final KnodeShareMapper knodeShareMapper;

    @Autowired
    public EnhancerShareServiceImpl(
            CoreClient coreClient,
            KnodeShareService knodeShareService,
            EnhancerClient enhancerClient,
            EnhancerShareMapper enhancerShareMapper,
            KnodeShareMapper knodeShareMapper) {
        this.coreClient = coreClient;
        this.knodeShareService = knodeShareService;
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
        return enhancers.stream().map(enhancer -> enhancerShareMapper.selectByEnhancerId(Convert.toLong(enhancer.getId()))).toList();
    }

    @Override
    public List<EnhancerShare> getRelatedEnhancerShare(Long knodeId, Long knodeCount) {
        List<KnodeShare> knodeShares = knodeShareService.getRelatedKnodeShare(knodeId, knodeCount);
        return DataUtils.join(
                knodeShares.stream()
                .map(share -> enhancerClient.getEnhancersOfKnode(share.getKnodeId()))
                .toList()).stream()
                .filter(enhancer->enhancerShareMapper.existsByEnhancerId(Convert.toLong(enhancer.getId())))
                .filter(enhancer->enhancerClient.getEnhancerById(Convert.toLong(enhancer.getId())) != null)
                .map(enhancer-> enhancerShareMapper.selectByEnhancerId(Convert.toLong(enhancer.getId())))
                .toList();
    }

    @Override
    public Map<String, List<EnhancerShareDTO>> getRelatedEnhancerShareWithMapping(Long knodeId, Long knodeCount){
        List<KnodeShare> knodeShares = knodeShareService.getRelatedKnodeShare(knodeId, knodeCount);
        HashMap<String, List<EnhancerShareDTO>> res = new HashMap<>();
        for(KnodeShare knodeShare: knodeShares)
            res.put(knodeShare.getKnodeId().toString(),
                enhancerClient.getEnhancersOfKnode(knodeShare.getKnodeId()).stream()
                .map(enhancer->EnhancerShare.transfer(enhancerShareMapper.selectByEnhancerId(Convert.toLong(enhancer.getId()))))
                .toList());
        return res;
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
            Map<String, Object> data = enhancerClient.getDataFromResource(Convert.toLong(resource.getId()));
            resourceWithData.setMeta(meta);
            resourceWithData.setData(data);
            ResourceDTO _resource = enhancerClient.addResourceToEnhancer(Convert.toLong(_enhancer.getId()), resourceWithData);
            _resources.add(_resource);
        }

        _enhancer.setTitle(enhancer.getTitle());
        _enhancer.setCreateBy(Long.toString(userId));
        _enhancer.setLabels(enhancer.getLabels());
        _enhancer.setIntroduction(enhancer.getIntroduction());
        _enhancer.setResourceIds(_resources.stream().map(ResourceDTO::getId).toList());
        enhancerClient.updateEnhancer(Convert.toLong(_enhancer.getId()), _enhancer);

        return _enhancer;
    }

    @Override
    public EnhancerShare getEnhancerShare(Long enhancerId) {
        return enhancerShareMapper.selectByEnhancerId(enhancerId);
    }
}
