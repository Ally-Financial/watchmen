package com.ally.d3.config;

import org.apache.logging.log4j.util.PropertySource;
import org.springframework.context.annotation.Import;




@org.springframework.context.annotation.PropertySource("config.properties")
@Import({ io.github.ally-financial.watchmen.config.SpringConfig.class })


public class SpringConfig {



}
