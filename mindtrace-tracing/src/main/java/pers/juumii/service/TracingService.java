package pers.juumii.service;

import cn.dev33.satoken.util.SaResult;
import pers.juumii.data.Mindtrace;
import pers.juumii.dto.MindtraceDTO;

public interface TracingService {

    // 增
    SaResult record(Long userId, MindtraceDTO dto);
    // 删
    SaResult delete(Long userId, Long traceId);
    // 改
    SaResult modify(Long userId, Long traceId, MindtraceDTO dto);
    // 查
    Mindtrace check(Long userId, Long traceId);

}
