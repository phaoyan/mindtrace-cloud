package pers.juumii.service.impl.serializer;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceSerializer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
@ResourceType(ResourceType.QUIZCARD)
public class QuizcardSerializer implements ResourceSerializer {

    private final ResourceRepository repository;

    @Autowired
    public QuizcardSerializer(ResourceRepository repository) {
        this.repository = repository;
    }


    @Override
    public void serialize(Resource meta, Map<String, Object> data) {
        if(data == null)
            data = prototype();

        // 提取数据
        String front = Convert.toStr(data.get("front"));
        String back = Convert.toStr(data.get("back"));
        Map<String, String> imgsBase64 = Convert.toMap(String.class, String.class, data.get("imgs"));

        // 封装数据
        Map<String, InputStream> dataList = new HashMap<>();
        Opt.ifPresent(front, (f)->dataList.put("front.md",IoUtil.toStream(f, StandardCharsets.UTF_8)));
        Opt.ifPresent(back, (b)->dataList.put("back.md",IoUtil.toStream(b, StandardCharsets.UTF_8)));
        if(Opt.isPresent(imgsBase64))
            for(Map.Entry<String, String> imgBase64: imgsBase64.entrySet())
                dataList.put(imgBase64.getKey(), IoUtil.toStream(Base64.decode(removePrefix(imgBase64.getValue()))));

        repository.save(meta.getCreateBy(), meta.getId(), dataList);
    }

    private String removePrefix(String base64) {
        if(!base64.contains(",")) return base64;
        else return base64.split(",")[1];
    }

    public static Map<String, Object> prototype() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("front", "");
        res.put("back","");
        res.put("imgs", new HashMap<>());

        return res;
    }

}
