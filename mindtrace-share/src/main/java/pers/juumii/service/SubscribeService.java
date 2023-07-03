package pers.juumii.service;

import java.util.List;

public interface SubscribeService {

    List<Long> getUserSubscribes(Long knodeId);

    List<Long> getKnodeSubscribes(Long knodeId);

    List<Long> getEnhancerSubscribes(Long knodeId);

    void subscribeUser(Long knodeId, Long targetId);

    void subscribeKnode(Long knodeId, Long targetId);

    void subscribeEnhancer(Long knodeId, Long targetId);

    void removeUserSubscribe(Long knodeId, Long targetId);

    void removeKnodeSubscribe(Long knodeId, Long targetId);

    void removeEnhancerSubscribe(Long knodeId, Long targetId);
}
