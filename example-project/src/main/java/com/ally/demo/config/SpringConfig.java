package com.ally.demo.config;

import org.apache.logging.log4j.util.PropertySource;
import org.springframework.context.annotation.Import;




@org.springframework.context.annotation.PropertySource("config.properties")
@Import({ com.ally.d3.watchmen.config.SpringConfig.class })


public class SpringConfig {



}
