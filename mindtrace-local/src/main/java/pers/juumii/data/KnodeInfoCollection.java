package pers.juumii.data;

import lombok.Data;
import pers.juumii.dto.enhancer.EnhancerDTO;
import pers.juumii.dto.IdPair;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.enhancer.ResourceDTO;
import pers.juumii.dto.tracing.StudyTraceDTO;

import java.util.List;
import java.util.Map;

@Data
public class KnodeInfoCollection {

    private List<KnodeDTO> knodes;
    private List<EnhancerDTO> enhancers;
    private List<ResourceDTO> resources;
    private Map<String, Map<String, String>> dataList;
    private List<StudyTraceDTO> studyTraces;
    private List<IdPair> knodeEnhancerRels;
    private List<IdPair> enhancerResourceRels;
    private List<IdPair> traceKnodeRels;
    private List<IdPair> traceEnhancerRels;

}
