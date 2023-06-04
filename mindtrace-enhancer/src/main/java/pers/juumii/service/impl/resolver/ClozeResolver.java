package pers.juumii.service.impl.resolver;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.constants.enhancer.ResourceTypes;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceResolver;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * data格式：
 * 为了前端数据传输简便，前端传过来的数据就是一个字符串，后端拿到字符串后解析为下述data格式。
 * raw: 前端传入的原始数据，填空处为{{CLOZE::<txt>}}
 * withAnswer: 答案呈现的markdown string，其中答案被** **加粗包裹
 * withoutAnswer: 答案不呈现的markdown string，其中答案被**<?>**代替
 * 由于milkdown默认以base64将图片等资源直接保存，所以暂不做图片资源的单独处理
 */
@Service
@ResourceType(ResourceTypes.CLOZE)
public class ClozeResolver implements ResourceResolver {

    public static final String CLOZE_TEXT_REGEX = "\\{\\{CLOZE::(.+?)\\}\\}";


    @Override
    public Object resolve(Resource resource, String name) {
        return null;
    }

    // TODO
    @Override
    public Map<String, Object> resolve(Map<String, InputStream> dataList) {
        String raw = IoUtil.readUtf8(dataList.get("raw.md"));
        // 匹配{{CLOZE:: xxxx }}，制作content和answers
        // 解析数据
        StringBuilder noAnswer = new StringBuilder();
        JSONArray indexes = JSONUtil.createArray();
        Matcher matcher = Pattern.compile(CLOZE_TEXT_REGEX).matcher(raw);
        while (matcher.find()){
            String answer = matcher.group(1);
            matcher.appendReplacement(noAnswer, "");
            JSONObject index = JSONUtil.createObj();
            index.set("txt", answer);
            index.set("start", matcher.start());
            index.set("end", matcher.end());
            index.set("insert", noAnswer.length());
            indexes.add(index);
        }
        matcher.appendTail(noAnswer);

        HashMap<String,Object> res = new HashMap<>();
        res.put("noAnswer", noAnswer.toString());
        res.put("indexes", indexes);
        res.put("raw", raw);
        return res;
    }
}
