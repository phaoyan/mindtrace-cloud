package pers.juumii.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Mindtrace;
import pers.juumii.feign.GlobalClient;
import pers.juumii.mapper.MindtraceMapper;
import pers.juumii.service.SamplingService;

import java.util.ArrayList;
import java.util.List;

@Service
public class SamplingServiceImpl implements SamplingService {

    private final GlobalClient client;
    private final MindtraceMapper mindtraceMapper;

    @Autowired
    public SamplingServiceImpl(
            GlobalClient client,
            MindtraceMapper mindtraceMapper) {
        this.client = client;
        this.mindtraceMapper = mindtraceMapper;
    }



    @Override
    public List<Mindtrace> knodeTrace(Long userId, Long knodeId) {
        LambdaQueryWrapper<Mindtrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Mindtrace::getKnodeId, knodeId);
        return mindtraceMapper.selectList(wrapper);
    }

    @Override
    public List<Mindtrace> enhancerTrace(Long userId, Long enhancerId) {
        LambdaQueryWrapper<Mindtrace> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Mindtrace::getEnhancerId, enhancerId);
        return mindtraceMapper.selectList(wrapper);
    }

    @Override
    public List<Mindtrace> knodeFeatureTrace(Long userId, String labelName) {
        LambdaQueryWrapper<Mindtrace> wrapper = new LambdaQueryWrapper<>();

        // TODO
        List<Long> knodeIds = new ArrayList<>();
        wrapper.in(Mindtrace::getKnodeId, knodeIds);
        return mindtraceMapper.selectList(wrapper);
    }

    @Override
    public List<Mindtrace> enhancerFeatureTrace(Long userId, String labelName) {
        LambdaQueryWrapper<Mindtrace> wrapper = new LambdaQueryWrapper<>();

        // TODO
        List<Long> enhancerIds = new ArrayList<>();
        wrapper.in(Mindtrace::getEnhancerId, enhancerIds);
        return mindtraceMapper.selectList(wrapper);
    }

    @Override
    public List<Mindtrace> matchedFeatureTrace(
            Long userId, String knodeLabel,
            String enhancerLabel) {
        List<Mindtrace> knodeFilter = knodeFeatureTrace(userId, knodeLabel);
        List<Mindtrace> enhancerFilter = enhancerFeatureTrace(userId, enhancerLabel);
        return ListUtil.toList(CollUtil.disjunction(knodeFilter, enhancerFilter));
    }
}
