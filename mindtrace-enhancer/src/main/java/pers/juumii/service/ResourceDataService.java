package pers.juumii.service;


import pers.juumii.dto.UnfoldingKnodeData;

import java.util.List;

public interface ResourceDataService {

    List<UnfoldingKnodeData> getUnfoldingKnodeData(Long rootId);
}
