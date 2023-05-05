package pers.juumii.service;



public interface AuthService {

    Boolean isUserPublic(Long userId);
    Boolean isKnodePublic(Long knodeId);
    Boolean isEnhancerPublic(Long enhancerId);
    Boolean isResourcePublic(Long resourceId);
}
