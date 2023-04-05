package pers.juumii.service.impl.router;

import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceResolver;
import pers.juumii.service.ResourceSerializer;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.SpringUtils;

import java.util.List;

// 负责将资源路由到能够处理它的Resolver
@Service
public class ResourceRouter {


    public ResourceResolver resolver(Resource meta){
        List<ResourceResolver> resolvers = SpringUtils.getBeans(ResourceResolver.class);
        ResourceResolver selected = DataUtils.getIf(resolvers, resolver->
                resolver.getClass().getAnnotation(ResourceType.class).value().equals(meta.getType()));
        if (selected == null)
            throw new RuntimeException("Rout to resolver failure: resolver not found" + meta.getType());
        return selected;
    }

    public ResourceSerializer serializer(Resource meta){
        List<ResourceSerializer> serializers = SpringUtils.getBeans(ResourceSerializer.class);
        ResourceSerializer selected = DataUtils.getIf(serializers, (serializer)->
                serializer.getClass().getAnnotation(ResourceType.class).value().equals(meta.getType()));
        if (selected == null)
            throw new RuntimeException("Route to serializer failure: serializer not found " + meta.getType());
        return selected;
    }
}
