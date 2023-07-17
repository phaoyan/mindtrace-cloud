package pers.juumii.service.impl.resolver;

import cn.hutool.core.convert.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceResolver;
import pers.juumii.utils.SpringUtils;

import java.io.InputStream;
import java.util.Map;


/**
 * data格式：
 * front->md字符串
 * back->md字符串
 * imgs->[name->base64图片编码]的map，
 * - name包括拓展名，
 * - base64包括了data:image/png;base64,格式前缀
 */
@Service
@ResourceType(ResourceTypes.QUIZCARD)
public class QuizcardResolver implements ResourceResolver {

    private final ResolverUtils resolverUtils;

    @Autowired
    public QuizcardResolver(ResolverUtils resolverUtils) {
        this.resolverUtils = resolverUtils;
    }

    @Override
    public Map<String, Object> resolve(Resource resource) {
        return resolve(SpringUtils.getBean(ResourceRepository.class).load(resource));
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
