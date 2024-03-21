package pers.juumii.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.CoreClient;
import pers.juumii.feign.MqClient;
import pers.juumii.mq.MessageEvents;
import pers.juumii.service.ReviewService;

import java.util.List;

@Component
public class MqEventRegistration implements ApplicationRunner {

    private final MqClient mqClient;
    private final LoadBalancerClient loadBalancerClient;
    private final ReviewService reviewService;
    private final CoreClient coreClient;

    @Autowired
    public MqEventRegistration(
            MqClient mqClient,
            LoadBalancerClient loadBalancerClient,
            ReviewService reviewService,
            CoreClient coreClient) {
        this.mqClient = mqClient;
        this.loadBalancerClient = loadBalancerClient;
        this.reviewService = reviewService;
        this.coreClient = coreClient;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(1000);
        ServiceInstance self = loadBalancerClient.choose("mindtrace-mastery");
        String handleAddKnode = self.getUri().toString() + "/mq/knode/add";
        mqClient.addListener(MessageEvents.ADD_KNODE, handleAddKnode);
    }

    public void handleAddKnode(String knodeStr) {
        KnodeDTO knode = JSONUtil.toBean(knodeStr, KnodeDTO.class);
        Long knodeId = Convert.toLong(knode.getId());
        Long userId = Convert.toLong(knode.getCreateBy());
        List<Long> monitorList = reviewService.getReviewMonitorList(userId);
        for(Long rootId : monitorList)
            if(coreClient.isOffspring(knodeId, rootId)){
                reviewService.addReviewSchedule(knodeId, 0L);
                break;
            }
    }
}
