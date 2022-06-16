package com.ally.d3.watchmen.config;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SimpleTest.TestConfig.class})
public class SimpleTest {

    public static class TestConfig {

        @Bean
        public RestTemplate restTemplate(){
            return new RestTemplate();
        }

    }

    @Autowired
    RestTemplate template;

    @Test
    public void test(){
        assertNotNull(template);
    }


}
