package pers.juumii.service.impl.router;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceResolver;
import pers.juumii.service.ResourceSerializer;

// 负责将资源路由到能够处理它的Resolver
@Service
public class ResourceRouter {

    public ResourceResolver route(Resource meta){
        return null;
    }

    public ResourceSerializer route(Resource meta, MultipartFile file){
        return null;
    }
}
