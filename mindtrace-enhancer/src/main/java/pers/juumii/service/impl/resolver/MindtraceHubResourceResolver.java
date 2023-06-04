package pers.juumii.service.impl.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceResolver;

import java.io.InputStream;
import java.util.Map;

@Service
@ResourceType(ResourceTypes.MINDTRACE_HUB_RESOURCE)
public class MindtraceHubResourceResolver implements ResourceResolver {

    private final ResolverUtils resolverUtils;

    @Autowired
    public MindtraceHubResourceResolver(ResolverUtils resolverUtils) {
        this.resolverUtils = resolverUtils;
    }

    @Override
    public Object resolve(Resource resource, String name) {
        return null;
    }

    @Override
    public Map<String, Object> resolve(Map<String, InputStream> dataList) {
        return resolverUtils.resolveJson(dataList);
    }
}
