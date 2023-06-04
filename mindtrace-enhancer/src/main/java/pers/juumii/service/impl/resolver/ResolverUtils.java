package pers.juumii.service.impl.resolver;

import cn.hutool.core.io.IoUtil;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ResolverUtils {

    public Map<String, Object> resolveJson(Map<String, InputStream> dataList) {
        HashMap<String, Object> res = new HashMap<>();
        Optional<Map.Entry<String, InputStream>> optional = dataList.entrySet().stream().findAny();
        if(optional.isEmpty())
            return new HashMap<>();
        Map.Entry<String, InputStream> data = optional.get();
        res.put(data.getKey(), IoUtil.readUtf8(data.getValue()));
        return res;
    }
}
