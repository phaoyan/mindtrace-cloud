package pers.juumii.utils;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Cypher {
    private String cypher;
    private Map<String, Object> params;

    public Cypher append(Cypher tail){
        if(CollectionUtil.containsAny(params.keySet(), tail.getParams().keySet()))
            throw new RuntimeException("Cypher param conflict ... ");
        HashMap<String, Object> paramUnion =
                CollectionUtil.toMap(CollectionUtil.union(params.entrySet(), tail.getParams().entrySet()));
        return Cypher.cypher(cypher + "\n" + tail.getCypher(), paramUnion);
    }

    public static Cypher cypher(String cypher, Map<String, Object> params){
        Cypher res = new Cypher();
        res.setCypher(cypher);
        res.setParams(params);
        return res;
    }
}
