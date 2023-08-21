package pers.juumii.service.impl.repository;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Opt;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import pers.juumii.service.ResourceRepository;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//@Service
public class ResourceRepositoryImpl implements ResourceRepository {

    @Getter
    @Value("${local.static.path}")
    private String staticRoot;

    public String path(Long userId, Long resourceId){
        return staticRoot + userId + "/" + resourceId;
    }

    public String path(Long userId, Long resourceId, String dataFileName){
        return path(userId, resourceId) + "/" + dataFileName;
    }

    public FileWriter writer(Long userId, Long resourceId, String dataFileName){
        return new FileWriter(path(userId, resourceId, dataFileName));
    }

    public void init(Long userId, Long resourceId){
        FileUtil.mkdir(path(userId, resourceId));
    }

    // 需保证user存在、resource存在
    @Override
    public void save(Long userId, Long resourceId, Map<String, InputStream> dataList) {
        init(userId, resourceId);
        for(Map.Entry<String, InputStream> data: dataList.entrySet())
            writer(userId,resourceId,data.getKey()).writeFromStream(data.getValue());
    }

    // 需保证user存在、resource存在
    @Override
    public void save(Long userId, Long resourceId, String name, InputStream data) {
        writer(userId, resourceId, name).writeFromStream(data);
    }

    @Override
    public void setMeta(Long userId, Long resourceId, String name, Map<String, String> meta) {

    }

    @Override
    public Map<String, Object> getMeta(Long userId, Long resourceId, String name) {
        return null;
    }

    @Override
    public Map<String, InputStream> load(Long userId, Long resourceId) {
        HashMap<String, InputStream> res = new HashMap<>();
        File[] dataList = new File(path(userId, resourceId)).listFiles();
        for (File data: Opt.ofNullable(dataList).orElse(new File[]{}))
            res.put(data.getName(), FileUtil.getInputStream(data));
        return res;
    }

    @Override
    public InputStream load(Long userId, Long resourceId, String name) {
        return FileUtil.getInputStream(path(userId, resourceId, name));
    }

    @Override
    public Boolean release(Long userId, Long resourceId, String dataName) {
        return FileUtil.del(path(userId, resourceId, dataName));
    }

    @Override
    public Boolean releaseAll(Long userId, Long resourceId) {
        return FileUtil.del(path(userId, resourceId));
    }

}
