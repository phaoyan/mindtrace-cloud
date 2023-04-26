package pers.juumii.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "mindtrace-knode-similarity-python")
public interface KnodeSimilarityClient {


}
