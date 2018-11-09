package com.quarkdata.yunpan;

import com.quarkdata.yunpan.api.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/config/spring-context.xml", "classpath:/config/mybatis-config.xml"})
public class BaseJunit4Test {

    @Autowired
    private TestService testService;

    @Test
    public void test(){
        testService.getTest("", "", "");
        System.out.println("www");
    }
}
