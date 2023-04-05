package pers.juumii.service.impl.router;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.juumii.data.Resource;
import pers.juumii.service.impl.serializer.QuizcardSerializer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResourceRouterTest {

    @Autowired
    private ResourceRouter router;

    @Test
    void resolver() {
        Resource resource = new Resource();
        resource.setType("quizcard");
        System.out.println(router.resolver(resource));
    }

    @Test
    void serializer() {
        Resource resource = new Resource();
        resource.setType("quizcard");
        System.out.println(router.serializer(resource));
    }
}