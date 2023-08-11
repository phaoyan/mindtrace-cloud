package pers.juumii.service;

import pers.juumii.data.KnodeInfoCollection;

import java.util.Map;

public interface ResolveService {
    void resolve(Long stemId, KnodeInfoCollection main, Map<String, byte[]> dataIndex);
}
