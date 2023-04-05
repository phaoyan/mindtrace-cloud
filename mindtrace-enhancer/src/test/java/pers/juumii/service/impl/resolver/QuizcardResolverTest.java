package pers.juumii.service.impl.resolver;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.juumii.data.Resource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuizcardResolverTest {

    @Autowired
    private QuizcardResolver resolver;

    @Test
    void resolve() {
        Resource resource = new Resource();
        resource.setId(12345L);
        resource.setCreateBy(10086L);

        Object resolve = resolver.resolve(resource, "test.png");
        System.out.println(JSONUtil.toJsonStr(resolve));
    }
}