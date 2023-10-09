package pers.juumii.service;

import org.springframework.stereotype.Service;
import pers.juumii.data.Knode;
import pers.juumii.dto.KnodeDTO;

import java.util.List;

@Service
public interface KnodeService {

    Knode branch(Long knodeId, String title);

    void delete(Long knodeId);

    void update(Long knodeId, KnodeDTO dto);

    List<Knode> shift(Long stemId, Long branchId);

    void swapIndex(Long stemId, Integer index1, Integer index2);

    void editCreateTime(Long knodeId, String createTime);

    void editCreateBy(Long knodeId, String createBy);

    void editTitle(Long knodeId, String title);

    void editIndex(Long knodeId, Integer index);

    void connect(Long knodeId1, Long knodeId2);

    void disconnect(Long knodeId1, Long knodeId2);
}
