package pers.juumii.service.impl.repository;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResourceRepositoryImplTest {

    @Autowired
    private ResourceRepositoryImpl repository;

    @Test
    void save() {
        Map<String, InputStream> dataList = new HashMap<>();
        String txt = "hello input stream";
        ByteArrayInputStream in = new ByteArrayInputStream(txt.getBytes(StandardCharsets.UTF_8));
        dataList.put("hello.txt", in);
        repository.save(10086L,12345L,dataList);
    }

    @Test
    void save2(){
        repository.save(10086L, 12345L, "HHHHH.txt", IoUtil.toStream("hello HHHH", StandardCharsets.UTF_8));
    }

    @Test
    void load(){
        for(Map.Entry<String, InputStream> data: repository.load(10086L,12345L).entrySet())
            System.out.println(data.getKey() + "  " +IoUtil.readUtf8(data.getValue()));
    }

    @Test
    void load2(){
        System.out.println(IoUtil.readUtf8(repository.load(10086L,12345L,"hello.txt")));
    }
}