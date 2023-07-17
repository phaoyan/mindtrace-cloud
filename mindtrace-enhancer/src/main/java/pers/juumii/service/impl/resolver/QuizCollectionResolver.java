package pers.juumii.service.impl.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceResolver;
import pers.juumii.utils.SpringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * data格式：
 * raw：serializer在存储区存储的json:
 *      {
 *          config:{
 *
 *          },
 *          ids: number[]
 *      }
 * content：将raw中的基本数据解析后形成的Enhancer数据，包括了其中的Resource数据
 */
@Service
@ResourceType(ResourceTypes.QUIZCARD_COLLECTION)
public class QuizCollectionResolver implements ResourceResolver {

    private final ResolverUtils resolverUtils;

    @Autowired
    public QuizCollectionResolver(ResolverUtils resolverUtils) {
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
        HashMap<String, Object> res = new HashMap<>();
        Map<String, Object> raw = resolverUtils.resolveJson(dataList);

        return res;
    }
}
