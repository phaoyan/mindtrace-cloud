package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.persistent.Milestone;
import pers.juumii.data.persistent.MilestoneTraceRel;
import pers.juumii.dto.ResourceDTO;

import java.util.List;
import java.util.Optional;

@Service
public interface MilestoneService {

    Milestone add(Long knodeId, Long userId);

    void remove(Long id);

    void setDescription(Long id, String desc);

    void setKnodeId(Long id, Long knodeId);

    void setTime(Long id, String dateTime);

    ResourceDTO addResource(Long id, String type);

    void removeResource(Long resourceId);

    Milestone getById(Long id);

    List<Milestone> getMilestonesBeneathKnode(Long knodeId);

    List<ResourceDTO> getResourcesFromMilestone(Long id);

    void addStudyTrace(Long milestoneId, Long traceId);

    void removeStudyTrace(Long milestoneId, Long traceId);

    List<MilestoneTraceRel> getStudyTraces(Long milestoneId);

    List<MilestoneTraceRel> getMilestones(Long traceId);
}
