package pers.juumii.service;

import pers.juumii.data.Resource;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * ResourceSerializer和ResourceResolver的下层服务类
 * save：拿到resource和资源文件（夹）后，负责将资源文件按照resource的metadata存储到文件系统中
 * load：拿到resource后，负责检索文件系统并返回相应的资源文件
 */

public interface ResourceRepository {

    void save(Long userId, Long resourceId, Map<String, InputStream> dataList);

    void save(Long userId, Long resourceId, String name, InputStream data);

    void setMeta(Long userId, Long resourceId, String name, Map<String, String> meta);

    Map<String, String> getMeta(Long userId, Long resourceId, String name);

    Map<String, InputStream> load(Long userId, Long resourceId);

    InputStream load(Long userId, Long resourceId, String name);

    Boolean release(Long userId, Long resourceId, String dataName);

    Boolean releaseAll(Long userId, Long resourceId);

    default Map<String, InputStream> load(Resource resource){
        return load(resource.getCreateBy(), resource.getId());
    }

    default InputStream load(Resource resource, String name){
        return load(resource.getCreateBy(), resource.getId(), name);
    }

}
