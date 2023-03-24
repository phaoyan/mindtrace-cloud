package pers.juumii.service;

import pers.juumii.data.Resource;

import java.util.Map;

public interface ResourceResolver {
    // 传入一个url，ResourceResolver负责对url对应的资源文件进行信息解析，将信息封装为Map返回
    Map<String, Object> resolve(Resource resource);
}
