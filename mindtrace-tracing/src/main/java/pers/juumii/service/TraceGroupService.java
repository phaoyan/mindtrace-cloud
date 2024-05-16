package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.StudyTrace;
import pers.juumii.data.persistent.TraceGroup;

import java.util.List;
import java.util.Map;

@Service
public interface TraceGroupService {

    // 将traceIds中的所有记录分配到同一个TraceGroup中
    TraceGroup union(List<Long> traceIds, Long groupId);

    void remove(Long traceId, Long groupId);

    void remove(Long traceId);

    void removeTraceGroup(Long groupId);

    TraceGroup getGroupById(Long groupId);

    List<TraceGroup> getGroupsByTraceId(Long traceId);

    List<TraceGroup> getGroupsByTraceIds(List<Long> traceIds);

    Map<Long, Long> getGroupMappingByTraceIds(List<Long> traceIds);

    List<StudyTrace> getTracesByGroupId(Long groupId);

    void setGroupTitle(Long groupId, String title);


}
