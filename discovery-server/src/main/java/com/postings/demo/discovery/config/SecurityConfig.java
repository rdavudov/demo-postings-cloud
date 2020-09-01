package com.postings.demo.discovery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final String username;
	private final String password;
	private final String webUsername;
	private final String webPassword;
	

	public SecurityConfig(@Value("${service.eureka-username}") String username, @Value("${service.eureka-password}") String password, 
			@Value("${service.eureka-web-username}") String webUsername, @Value("${service.eureka-web-password}") String webPassword) {
		this.username = username;
		this.password = password;
		this.webUsername = webUsername;
		this.webPassword = webPassword;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
		.passwordEncoder(NoOpPasswordEncoder.getInstance())
			.withUser(username)
			.password(password)
			.authorities("USER")
		.and()
			.withUser(webUsername)
			.password(webPassword)
			.authorities("WEB_USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// CSRF are useful with browser requested requests where evil sites can re-send request to our resource
			// if we are just building api consumed by other microservices it is not needed
			.csrf().disable()
			.authorizeRequests()
				.mvcMatchers("/eureka").hasAuthority("USER")
				.mvcMatchers("/").hasAuthority("WEB_USER")
				.anyRequest()
				.authenticated()
			.and()
				.httpBasic() ;
	}
	
}
