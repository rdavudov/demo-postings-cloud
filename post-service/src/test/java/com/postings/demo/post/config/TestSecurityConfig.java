package com.postings.demo.post.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class TestSecurityConfig {
	
//	@Value("${jwt.secret:}")
	private String jwtSecret = "qwertyuiopasdfghjklzxcvbnm123456" ;
	
	@Bean
	@Primary
	public JwtDecoder simpleDecoder() {
		return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HS256")).build() ; 
	}
}
