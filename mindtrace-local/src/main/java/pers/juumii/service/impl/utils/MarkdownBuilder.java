package pers.juumii.service.impl.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.dto.ResourceDTO;
import pers.juumii.feign.EnhancerClient;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class MarkdownBuilder {

    private final EnhancerClient enhancerClient;

    @Autowired
    public MarkdownBuilder(EnhancerClient enhancerClient) {
        this.enhancerClient = enhancerClient;
    }


    public String buildMarkdownInLayer(
            List<String> titles,
            List<String> contents,
            List<Long> layerMap){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < titles.size(); i ++){
            //做标题语法
            long sharps = layerMap.get(i);
            for(int j = 0; j < Math.min(sharps, 6); j ++)
                res.append("#");
            for(int j = 0; j < sharps - 1; j ++)
                res.append(" >");
            res.append(" ")
                .append(titles.get(i))
                .append("\n")
                .append(contents.get(i));
        }
        return res.toString();
    }

    public String buildResourceContent(ResourceDTO resource) {
        return switch (resource.getType()){
            case ResourceTypes.MARKDOWN -> buildFromMarkdownType(resource);
            case ResourceTypes.QUIZCARD -> buildFromQuizcardType(resource);
            case ResourceTypes.CLOZE    -> buildFromClozeType(resource);
            case ResourceTypes.LINKOUT  -> buildFromLinkoutType(resource);
            case ResourceTypes.MINDTRACE_HUB_RESOURCE -> buildFromHubType(resource);
            default -> "Resource Type not Supported -> " + resource.getType();
        };
    }

    private String buildFromHubType(ResourceDTO resource) {
        return """
                [%s](%s)
                """
                .formatted("云端资源", enhancerClient.getCosResourceUrl(Convert.toLong(resource.getId()), "data"));
    }

    private String buildFromLinkoutType(ResourceDTO resource) {
        Map<String, byte[]> dataList = enhancerClient.getDataFromResource(Convert.toLong(resource.getId()));
        JSONObject data = JSONUtil.parseObj(StrUtil.str(dataList.get("data.json"), StandardCharsets.UTF_8));
        String url = data.getStr("url");
        String remark = data.getStr("remark");
        return """
                [%s](%s)
                """
                .formatted(
                    remark == null ? "" : remark,
                    url    == null ? "" : url);
    }

    private String buildFromClozeType(ResourceDTO resource) {
        return null;
    }

    private String buildFromQuizcardType(ResourceDTO resource) {
        Map<String, byte[]> dataList = enhancerClient.getDataFromResource(Convert.toLong(resource.getId()));
        JSONObject data = JSONUtil.parseObj(StrUtil.str(dataList.get("data.json"), StandardCharsets.UTF_8));
        String front = data.getStr("front");
        String back = data.getStr("back");
        return """
                > Front:
                %s
                
                Back:
                %s
                """
                .formatted(
                    front == null ? "": front,
                    back  == null ? "": back)
                .replaceAll("\n", "\n> ");
    }

    private String buildFromMarkdownType(ResourceDTO resource) {
        Map<String, byte[]> dataList = enhancerClient.getDataFromResource(Convert.toLong(resource.getId()));
        return "> " + StrUtil.str(dataList.get("content.md"), StandardCharsets.UTF_8).replaceAll("\n","\n> ");
    }
}