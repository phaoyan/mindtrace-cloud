package pers.juumii.service.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.KnodeShare;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.dto.share.KnodeShareDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.KnodeSimilarityClient;
import pers.juumii.mapper.KnodeShareMapper;
import pers.juumii.service.KnodeShareService;
import pers.juumii.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KnodeShareServiceImpl implements KnodeShareService {

    private final CoreClient coreClient;
    private final KnodeSimilarityClient knodeSimilarityClient;
    private final KnodeShareMapper knodeShareMapper;

    @Autowired
    public KnodeShareServiceImpl(
            CoreClient coreClient,
            KnodeSimilarityClient knodeSimilarityClient,
            KnodeShareMapper knodeShareMapper) {
        this.coreClient = coreClient;
        this.knodeSimilarityClient = knodeSimilarityClient;
        this.knodeShareMapper = knodeShareMapper;
    }

    @Override
    public List<KnodeShare> getRelatedKnodeShare(Long knodeId, Double threshold){
        KnodeDTO knode = coreClient.check(knodeId);
        return knodeSimilarityClient.getNearestNeighbors(knodeId, threshold).stream()
                .filter(data->coreClient.check(Convert.toLong(data.get("knodeId"))) != null)
                .filter(data->Convert.toDouble(data.get("score")) > threshold)
                .map(data->getKnodeShare(Convert.toLong(data.get("knodeId"))))
                .filter(Objects::nonNull)
                .filter(share->!share.getUserId().equals(Convert.toLong(knode.getCreateBy())))
                .toList();
    }

    @Override
    public KnodeShare getKnodeShare(Long knodeId) {
        KnodeShare res = knodeShareMapper.selectByKnodeId(knodeId);
        if(res == null){
            KnodeDTO knode = coreClient.check(knodeId);
            res = KnodeShare.prototype(Convert.toLong(knode.getCreateBy()), knodeId);
            knodeShareMapper.insert(res);
        }
        return res;
    }

    @Override
    public void updateKnodeShare(Long knodeId, KnodeShareDTO dto) {
        KnodeShare knodeShare = knodeShareMapper.selectByKnodeId(knodeId);
        Opt.ifPresent(dto.getVisits(), knodeShare::setVisits);
        Opt.ifPresent(dto.getLikes(), knodeShare::setLikes);
        Opt.ifPresent(dto.getFavorites(), knodeShare::setFavorites);
        Opt.ifPresent(dto.getRate(), knodeShare::setRate);
        knodeShareMapper.updateById(knodeShare);
    }

    @Override
    public void forkKnodeShare(Long shareId, Long targetId) {

    }
}
