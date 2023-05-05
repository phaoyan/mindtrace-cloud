package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.juumii.dto.KnodeDTO;
import pers.juumii.feign.interceptor.FeignSecurityInterceptor;

import java.util.List;

@FeignClient(
        contextId = "mindtrace-core",
        name = "mindtrace-gateway",
        configuration = FeignSecurityInterceptor.class)
public interface CoreClient {

    @GetMapping("/core/knode/{knodeId}")
    KnodeDTO check(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/leaves")
    List<KnodeDTO> leaves(@PathVariable Long knodeId);
    @GetMapping("/core/knode/{knodeId}/createBy")
    Long checkKnodeCreateBy(@PathVariable Long knodeId);

}
