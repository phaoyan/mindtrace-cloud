package pers.juumii.service.impl.resolver;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.annotation.ResourceType;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;
import pers.juumii.service.ResourceResolver;

import java.io.InputStream;
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
public class QuizcardResolver implements ResourceResolver {

    private final ResourceRepository repository;

    @Autowired
    public QuizcardResolver(ResourceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, Object> resolve(Resource resource) {
        return resolve(repository.load(resource));
    }

    @Override
    public Object resolve(Resource resource, String name) {
        InputStream data = repository.load(resource, name);
        Map<String, Object> resolve = resolve(Map.of(name, data));
        resolve.putAll(Convert.toMap(String.class, String.class, resolve.get("imgs")));
        return resolve.get(name);
    }


    private Map<String, Object> resolve(Map<String, InputStream> dataList){
        Map<String, Object> res = new HashMap<>();
        HashMap<Object, Object> imgs = new HashMap<>();
        res.put("imgs", imgs);

        for(Map.Entry<String, InputStream> data: dataList.entrySet())
            if(data.getKey().equals("front.md"))
                res.put("front", IoUtil.readUtf8(data.getValue()));
            else if(data.getKey().equals("back.md"))
                res.put("back", IoUtil.readUtf8(data.getValue()));
                // 除了front和back，剩下的就认为是图片
            else
                // 重新编码为base64以便传输
                imgs.put(data.getKey(), base64(data.getValue(), data.getKey()));

        return res;
    }

    // 在前面添加data:image/xxx;base64,
    private String base64(InputStream in, String fileName) {
        String[] split = fileName.split("\\.");
        return "data:image/"+split[split.length-1]+";base64,"+Base64.encode(in);
    }
}
