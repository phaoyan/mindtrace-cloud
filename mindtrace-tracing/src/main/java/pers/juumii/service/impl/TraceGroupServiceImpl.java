package pers.juumii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceGroup;
import pers.juumii.data.persistent.TraceGroupRel;
import pers.juumii.mapper.StudyTraceMapper;
import pers.juumii.mapper.TraceGroupMapper;
import pers.juumii.mapper.TraceGroupRelMapper;
import pers.juumii.service.TraceGroupService;
import pers.juumii.utils.DataUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TraceGroupServiceImpl implements TraceGroupService {

    private final TraceGroupMapper traceGroupMapper;
    private final TraceGroupRelMapper relMapper;
    private final StudyTraceMapper studyTraceMapper;

    @Autowired
    public TraceGroupServiceImpl(TraceGroupMapper traceGroupMapper, TraceGroupRelMapper relMapper, StudyTraceMapper studyTraceMapper) {
        this.traceGroupMapper = traceGroupMapper;
        this.relMapper = relMapper;
        this.studyTraceMapper = studyTraceMapper;
    }

    @Override
    @Transactional
    public TraceGroup union(List<Long> traceIds, Long groupId) {
        TraceGroup group = traceGroupMapper.selectById(groupId);
        if(Objects.isNull(group)) {
            group = TraceGroup.prototype();
            traceGroupMapper.insert(group);
            for(Long traceId: traceIds)
                relMapper.insert(TraceGroupRel.prototype(traceId, group.getId()));
            return group;
        }
        LambdaQueryWrapper<TraceGroupRel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TraceGroupRel::getGroupId, groupId);
        Set<Long> existTraceIds = relMapper
                .selectList(queryWrapper).stream()
                .map(TraceGroupRel::getTraceId)
                .collect(Collectors.toSet());
        List<Long> validTraceIds = DataUtils.getAllIf(traceIds, (traceId)->!existTraceIds.contains(traceId));
        validTraceIds.forEach(traceId->relMapper.insert(TraceGroupRel.prototype(traceId, groupId)));
        return group;
    }

    @Override
    @Transactional
    public void remove(Long traceId, Long groupId) {
        LambdaUpdateWrapper<TraceGroupRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TraceGroupRel::getTraceId, traceId)
                .eq(TraceGroupRel::getGroupId, groupId);
        relMapper.delete(wrapper);
        removeGroupIfEmpty(groupId);
    }

    @Override
    public void remove(Long traceId) {
        getGroupsByTraceId(traceId).forEach(group->remove(traceId, group.getId()));
    }

    @Override
    public void removeTraceGroup(Long groupId) {
        LambdaUpdateWrapper<TraceGroupRel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TraceGroupRel::getGroupId, groupId);
        relMapper.delete(wrapper);
        traceGroupMapper.deleteById(groupId);
    }

    private void removeGroupIfEmpty(Long groupId){
        List<StudyTrace> traces = getTracesByGroupId(groupId);
        if(traces.isEmpty())
            traceGroupMapper.deleteById(groupId);
    }

    @Override
    public TraceGroup getGroupById(Long groupId) {
        return traceGroupMapper.selectById(groupId);
    }

    @Override
    public List<TraceGroup> getGroupsByTraceId(Long traceId) {
        LambdaQueryWrapper<TraceGroupRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceGroupRel::getTraceId, traceId);
        return relMapper.selectList(wrapper).stream()
                .map(rel->traceGroupMapper.selectById(rel.getGroupId()))
                .toList();
    }

    @Override
    public List<TraceGroup> getGroupsByTraceIds(List<Long> traceIds) {
        if(traceIds.size() == 0) return new ArrayList<>();
        if(traceIds.size() == 1) return getGroupsByTraceId(traceIds.get(0));
        LambdaQueryWrapper<TraceGroupRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TraceGroupRel::getTraceId, traceIds);
        Set<Long> groupIds = relMapper.selectList(wrapper).stream().map(TraceGroupRel::getGroupId).collect(Collectors.toSet());
        if(groupIds.isEmpty()) return new ArrayList<>();
        return traceGroupMapper.selectBatchIds(groupIds);
    }

    @Override
    public Map<Long, Long> getGroupMappingByTraceIds(List<Long> traceIds) {
        if(traceIds.size() == 0) return new HashMap<>();
        LambdaQueryWrapper<TraceGroupRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TraceGroupRel::getTraceId, traceIds);
        return relMapper.selectList(wrapper).stream()
                .collect(Collectors.groupingBy(TraceGroupRel::getTraceId)).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry->entry.getValue().get(0).getGroupId()));
    }

    @Override
    public List<StudyTrace> getTracesByGroupId(Long groupId) {
        LambdaQueryWrapper<TraceGroupRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceGroupRel::getGroupId, groupId);
        Set<Long> traceIds = relMapper.selectList(wrapper).stream()
                .map(TraceGroupRel::getTraceId)
                .collect(Collectors.toSet());
        if(traceIds.isEmpty()) return new ArrayList<>();
        return studyTraceMapper.selectBatchIds(traceIds);
    }

    @Override
    public void setGroupTitle(Long groupId, String title) {
        TraceGroup group = getGroupById(groupId);
        group.setTitle(title);
        traceGroupMapper.updateById(group);
    }
}
