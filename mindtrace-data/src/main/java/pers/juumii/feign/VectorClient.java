package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pers.juumii.dto.EmbeddingVectorDTO;

import java.util.List;

@FeignClient(name = "mindtrace-vector")
public interface VectorClient {

    @GetMapping("/vector-base/vector")
    List<EmbeddingVectorDTO> searchSimilar(
            @RequestParam String txt,
            @RequestParam("vector-base") String vectorBase,
            @RequestParam Double threshold);

    @GetMapping("/vector-base/vector/multi-txts")
    List<EmbeddingVectorDTO> searchSimilarMultiTxts(
            @RequestParam String txts,
            @RequestParam("vector-base") String vectorBase,
            @RequestParam Double threshold);

    @PutMapping("/vector-base/vector")
    void addVector(@RequestBody String data);

    @PutMapping("/vector-base/reset")
    void resetVectorBase(@RequestBody String data);
}
