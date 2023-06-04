package pers.juumii.service.impl.repository;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.juumii.service.ResourceRepository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceRepositoryImplCOS implements ResourceRepository {

    @NacosValue(value = "${tencent.cos.bucket.name}", autoRefreshed = true)
    private String BUCKET_NAME;
    private final COSClient cosClient;

    @Autowired
    public ResourceRepositoryImplCOS(COSClient cosClient) {
        this.cosClient = cosClient;
    }


    @Override
    public void save(Long userId, Long resourceId, Map<String, InputStream> dataList) {
        String prefix = userId + "/" + resourceId + "/";
        for(Map.Entry<String, InputStream> data: dataList.entrySet()) {
            String objKey = prefix + data.getKey();
            cosClient.putObject(BUCKET_NAME, objKey, data.getValue(), new ObjectMetadata());
        }
    }


    @Override
    public void save(Long userId, Long resourceId, String name, InputStream data) {
        String objKey = userId + "/" + resourceId + "/" + name;
        cosClient.putObject(BUCKET_NAME, objKey, data, new ObjectMetadata());
    }

    @Override
    public Map<String, InputStream> load(Long userId, Long resourceId) {
        HashMap<String, InputStream> res = new HashMap<>();
        String prefix = userId + "/" + resourceId + "/";
        List<COSObjectSummary> summaries = cosClient.listObjects(BUCKET_NAME, prefix).getObjectSummaries();
        for(COSObjectSummary summary: summaries) {
            COSObject data = cosClient.getObject(BUCKET_NAME, summary.getKey());
            if(data.getObjectContent() != null)
                res.put(summary.getKey().replaceFirst(prefix,""), data.getObjectContent());
        }
        return res;
    }

    @Override
    public InputStream load(Long userId, Long resourceId, String name) {
        String objKey = userId + "/" + resourceId + "/" + name;
        return cosClient.getObject(BUCKET_NAME, objKey).getObjectContent();
    }

    @Override
    public Boolean release(Long userId, Long resourceId, String dataName) {
        return null;
    }

    @Override
    public Boolean releaseAll(Long userId, Long resourceId) {
        return null;
    }

}
