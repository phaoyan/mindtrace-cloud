package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient
public interface UserClient {

    @GetMapping("/{id}")
    Boolean userExists(@PathVariable Long id);
}
