package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pers.juumii.dto.WebsiteDTO;

import java.util.Map;

@FeignClient(name = "mindtrace-spider-python")
public interface SpiderClient {
    @PostMapping
    Map<String, Object> getWebsiteInfo(@RequestBody WebsiteDTO data);

}
