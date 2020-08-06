package com.postings.demo.post.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

@TestConfiguration
public class LocalRibbonConfig {

	@Bean
	public ServerList<Server> ribbonServerList() {
		return new StaticServerList<Server>(new Server("localhost", 9999));
	}
}
