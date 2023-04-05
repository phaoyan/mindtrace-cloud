package pers.juumii.service;

import pers.juumii.data.Mindtrace;

import java.util.List;

public interface SamplingService {

    // 返回给定knode的所有mindtrace记录
    List<Mindtrace> knodeTrace(Long userId, Long knodeId);
    // 返回给定enhancer的所有mindtrace记录
    List<Mindtrace> enhancerTrace(Long userId, Long enhancerId);
    // 返回给定knode label标注的所有knode的mindtrace记录
    List<Mindtrace> knodeFeatureTrace(Long userId, String labelName);
    // 返回给定enhancer label标注的所有enhancer的mindtrace记录
    List<Mindtrace> enhancerFeatureTrace(Long userId, String labelName);
    // 返回指定knode label标注knode、指定enhancer label标注enhancer的所有mindtrace记录
    List<Mindtrace> matchedFeatureTrace(Long userId, String knodeLabel, String enhancerLabel);
    // 按review layer过滤mindtrace
    default List<Mindtrace> layerFilter(Long userId, List<Mindtrace> mindtraces, int layer){
        return mindtraces.stream().filter(trace->trace.getReviewLayer().equals(layer)).toList();
    }

}
