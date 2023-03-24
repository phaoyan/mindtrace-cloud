package pers.juumii.service;

import org.springframework.web.multipart.MultipartFile;
import pers.juumii.data.Resource;

public interface ResourceSerializer {

    // 两个任务：1. 将资源持久化； 2. 将资源的地址赋给meta
    void serialize(Resource meta, MultipartFile file);

    // 释放资源
    default void release(Resource meta){}
}
