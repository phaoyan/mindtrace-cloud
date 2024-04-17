package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mindtrace-knode-similarity-python")
public interface KnodeSimilarityClient {

    @GetMapping("/knode/similar")
    List<Map<String, ?>> getNearestNeighbors(@RequestParam Long knodeId, @RequestParam Double threshold);

    @GetMapping("/title/similar")
    List<Map<String, ?>> getNearestNeighborsByTitle(@RequestParam String title, @RequestParam Double threshold);

}
