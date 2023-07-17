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

@Service
public class KnodeShareServiceImpl implements KnodeShareService {

    private static final Double SIMILARITY_THRESHOLD = 0.5;
    private static final Long MAXIMUM_SIMILARITY = 128L;
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
    public List<KnodeShare> getRelatedKnodeShare(Long knodeId, Long count) {
        List<KnodeShare> res = new ArrayList<>();
        for(Long i = count; res.size() < count && i < 64 * count ; i *= 2){
            List<List<Object>> nnData = knodeSimilarityClient.getNearestNeighbors(knodeId, i);
            List<Long> nearestNeighbors = nnData.stream().map(data->(Long)data.get(0)).toList();

            List<KnodeShare> knodeShares = nearestNeighbors.stream().map((neighbor)->{
                LambdaQueryWrapper<KnodeShare> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(KnodeShare::getKnodeId, neighbor);
                return knodeShareMapper.selectOne(wrapper);
            }).toList();
            res = knodeShares.stream()
                    .filter(share->
                        share != null &&
                        coreClient.check(share.getKnodeId()) != null)
//                    .filter(share->!share.getUserId().equals(userId))
                    .toList();
        }
        return DataUtils.subList(res, 0, count.intValue());
    }

    @Override
    public List<KnodeShare> getRelatedKnodeShare(Long knodeId, Double threshold){
        KnodeDTO knode = coreClient.check(knodeId);
        return knodeSimilarityClient.getNearestNeighbors(knodeId, MAXIMUM_SIMILARITY).stream()
                .map(data->Map.entry((Long)data.get(0), (Double)data.get(1)))
                .filter(data->data.getValue() > (threshold == null ? calculateThreshold(knodeId) : threshold))
                .map(data->knodeShareMapper.selectByKnodeId(data.getKey()))
                .filter(Objects::nonNull)
                .filter(share->!share.getUserId().equals(Convert.toLong(knode.getCreateBy())))
                .toList();
    }

    private Double calculateThreshold(Long knodeId) {
        List<KnodeDTO> ancestors = coreClient.ancestors(knodeId);
        return Math.min(0.4 + 0.1 * ancestors.size(), 0.9);
    }

    @Override
    public KnodeShare getKnodeShare(Long knodeId) {
        return knodeShareMapper.selectByKnodeId(knodeId);
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
