package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "mindtrace-knode-similarity-python")
public interface KnodeSimilarityClient {

    @GetMapping("/knode/{knodeId}/similar")
    List<List<Object>> getNearestNeighbors(@PathVariable Long knodeId, @RequestParam("count") Long count);
}
