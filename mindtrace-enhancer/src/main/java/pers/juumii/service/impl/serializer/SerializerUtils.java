package pers.juumii.service.impl.serializer;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.data.Resource;
import pers.juumii.service.ResourceRepository;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class SerializerUtils {

    private final ResourceRepository repository;

    @Autowired
    public SerializerUtils(ResourceRepository repository) {
        this.repository = repository;
    }

    public void saveAsJson(Resource meta, Map<String, Object> data){
        saveAsJson(meta, data, "data.json");
    }

    public void saveAsJson(Resource meta, Map<String, Object> data, String fileName){
        String json;
        if(data == null)
            json = "{}";
        else
            try {
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                System.out.println("Serializing Warning: Processing Json Failed (SerializerUtils.saveUnmodified)");
                json = "{}";
            }
        HashMap<String, InputStream> dataList = new HashMap<>();
        dataList.put(fileName, IoUtil.toStream(json, StandardCharsets.UTF_8));
        repository.save(meta.getCreateBy(), meta.getId(), dataList);
    }
}
