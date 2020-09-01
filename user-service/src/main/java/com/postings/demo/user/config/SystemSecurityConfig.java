package com.postings.demo.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@Order(40)
public class SystemSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${system.password}")
	private String systemPassword ;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.mvcMatcher("/actuator/**")
			.authorizeRequests(reqs -> 
				reqs
					.mvcMatchers("/actuator").permitAll()
			)
			.httpBasic();
	}
	
	@Bean
	public UserDetailsService users() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		UserBuilder users = User.builder();
		UserDetails system = users
				.username("system")
				.password(encoder.encode(systemPassword))
				.roles("SYSTEM")
				.build();
		return new InMemoryUserDetailsManager(system);
	}
}
