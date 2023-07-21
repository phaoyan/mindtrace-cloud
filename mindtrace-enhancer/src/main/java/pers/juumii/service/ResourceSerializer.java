package pers.juumii.service;

import pers.juumii.data.Resource;

import java.util.Map;

public interface ResourceSerializer {

    void serialize(Resource meta, Map<String, Object> data);

    default void serialize(Resource meta, String dataName, Object data){}

}
