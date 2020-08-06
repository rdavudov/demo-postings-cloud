package com.postings.demo.post.config;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@RibbonClient(name = "user", configuration = LocalRibbonConfig.class)
public class RibbonConfig {
	
}
