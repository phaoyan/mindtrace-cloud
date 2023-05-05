package pers.juumii.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.KnodeShare;
import pers.juumii.dto.share.KnodeShareDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.KnodeSimilarityClient;
import pers.juumii.mapper.KnodeShareMapper;
import pers.juumii.service.KnodeShareService;
import pers.juumii.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

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
    public List<KnodeShare> getRelatedKnodeShare(Long knodeId, Long count) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<KnodeShare> res = new ArrayList<>();
        for(Long i = count; res.size() < count && i < 64 * count ; i *= 2){
            List<Long> nearestNeighbors = knodeSimilarityClient.getNearestNeighbors(knodeId, i);
            LambdaQueryWrapper<KnodeShare> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(KnodeShare::getKnodeId, nearestNeighbors);
            List<KnodeShare> knodeShares =
                    nearestNeighbors.isEmpty() ?
                    new ArrayList<>() :
                    knodeShareMapper.selectList(wrapper);
            res = knodeShares.stream()
                    .filter(share->coreClient.check(share.getKnodeId()) != null)
                    .filter(share->!share.getUserId().equals(userId))
                    .toList();
        }
        return DataUtils.subList(res, 0, count.intValue());
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
