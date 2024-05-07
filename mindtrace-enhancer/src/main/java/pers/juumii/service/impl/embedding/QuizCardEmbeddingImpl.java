package pers.juumii.service.impl.embedding;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.service.ResourceService;

@Service
public class QuizCardEmbeddingImpl implements ResourceEmbeddingService{
    private final ResourceService resourceService;

    @Autowired
    public QuizCardEmbeddingImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public String getEmbeddingText(Long resourceId) {
        byte[] data = resourceService.getDataFromResource(resourceId, "data.json");
        JSONObject json = JSONUtil.parseObj(data);
        String front = json.getStr("front");
        String back = json.getStr("back");
        return front + "\n" + back;
    }

    @Override
    public Boolean match(String resourceType) {
        return resourceType.equals(ResourceTypes.QUIZCARD);
    }
}
