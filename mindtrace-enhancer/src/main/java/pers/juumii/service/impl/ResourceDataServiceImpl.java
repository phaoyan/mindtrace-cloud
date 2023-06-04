package pers.juumii.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.UnfoldingKnodeData;
import pers.juumii.feign.CoreClient;
import pers.juumii.service.ResourceDataService;
import java.util.List;
import java.util.Map;

@Service
public class ResourceDataServiceImpl implements ResourceDataService {

    private final CoreClient coreClient;

    @Autowired
    public ResourceDataServiceImpl(CoreClient coreClient) {
        this.coreClient = coreClient;
    }

    @Override
    public List<UnfoldingKnodeData> getUnfoldingKnodeData(Long rootId) {
        // 将root下所有的knode都转换为UnfoldingKnodeData，统一查询chainStyleTitle以提高效率
        Map<String, List<String>> titleChainMap = coreClient.chainStyleTitleBeneath(rootId);
        List<KnodeDTO> offsprings = coreClient.offsprings(rootId);
        return offsprings.stream().map(knode->new UnfoldingKnodeData(
                knode.getId(),knode.getStemId(),
                knode.getTitle(),titleChainMap.get(knode.getId()),
                false, false)).toList();
    }
}
