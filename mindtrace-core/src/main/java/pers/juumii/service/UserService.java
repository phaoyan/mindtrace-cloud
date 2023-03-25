package pers.juumii.service;

import org.springframework.stereotype.Service;

/**
 * 用户注册后，其直接与一个唯一的根节点绑定；
 * 用户创建的所有学科都是这个根节点的唯一子节点；
 */
@Service
public interface UserService {

    //返回关联根节点的id
    Long register(Long userId);

    //返回关联根节点的id
    Long unregister(Long userId);

    //返回关联根节点的id
    Long checkRootId(Long userId);

    Boolean possesses(Long userId, Long knodeId);

    Long findUserId(Long knodeId);

}
