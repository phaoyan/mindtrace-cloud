package pers.juumii.mq;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.juumii.data.EnhancerKnodeRel;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.mapper.EnhancerKnodeRelationshipMapper;
import pers.juumii.service.EnhancerService;

import java.util.List;

@Component
public class KnodeEventConsumer {

    private final EnhancerKnodeRelationshipMapper ekrMapper;
    private final EnhancerService enhancerService;

    @Autowired
    public KnodeEventConsumer(
            EnhancerKnodeRelationshipMapper ekrMapper,
            EnhancerService enhancerService) {
        this.ekrMapper = ekrMapper;
        this.enhancerService = enhancerService;
    }

    @RabbitListener(queues = MessageQueues.REMOVE_KNODE_EVENT_MQ)
    public void handleRemoveKnode(String message){
        KnodeDTO knode = JSONUtil.toBean(message, KnodeDTO.class);
        Long knodeId = Convert.toLong(knode.getId());
        List<EnhancerKnodeRel> rels = ekrMapper.getByKnodeId(knodeId);
        for(EnhancerKnodeRel rel : rels)
            enhancerService.disconnectEnhancerFromKnode(knodeId,rel.getEnhancerId());
    }
}
