package pers.juumii.config;


import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.juumii.handler.MyJackson2HttpMessageConverter;

@Configuration
public class OpenFeignConfig {

    @Bean
    public Decoder feignDecoder() {
        MyJackson2HttpMessageConverter converter = new MyJackson2HttpMessageConverter();
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(converter);
        return new SpringDecoder(objectFactory);
    }
}
