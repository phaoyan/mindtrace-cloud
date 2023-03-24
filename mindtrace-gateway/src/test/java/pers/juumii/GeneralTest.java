package pers.juumii;

import cn.dev33.satoken.secure.SaSecureUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GeneralTest {

    @Test
    public void test(){
        System.out.println(SaSecureUtil.md5("123456"));
    }

}
