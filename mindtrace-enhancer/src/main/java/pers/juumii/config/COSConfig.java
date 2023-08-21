package pers.juumii.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class COSConfig {

    @NacosValue(value = "${tencent.cos.security.secret-id}", autoRefreshed = true)
    private String secretId;
    @NacosValue(value = "${tencent.cos.security.secret-key}", autoRefreshed = true)
    private String secretKey;
    @NacosValue(value = "${tencent.cos.region}", autoRefreshed = true)
    private String regionName;

    @Bean
    public COSClient cosClient(){
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(regionName);
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }

    @Bean
    public TransferManager transferManager(COSClient cosClient){
        return new TransferManager(cosClient, Executors.newFixedThreadPool(8));
    }
}
