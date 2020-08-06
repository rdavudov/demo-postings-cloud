package com.postings.demo.edge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
//		http
//			.csrf().disable()
//			.authorizeExchange()
//				.pathMatchers("/eureka/**").permitAll()
//				.pathMatchers("/oauth/**").permitAll()
//				.anyExchange().authenticated()
//				.and()
//			.oauth2ResourceServer()
//				.jwt();
//		return http.build();
		http
		.csrf().disable()
		.authorizeExchange().anyExchange().permitAll();
		return http.build() ;
	}

}
