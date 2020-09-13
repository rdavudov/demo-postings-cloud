package com.postings.demo.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.postings.demo.client.handler.OAuth2AuthSuccessHandler;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private OAuth2AuthSuccessHandler oauth2authSuccessHandler ;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(reqs -> 
				reqs
					.mvcMatchers("/login").permitAll()
					.mvcMatchers("/posts").permitAll()
					.mvcMatchers("/blocked").hasRole("BLOCKED")
					.anyRequest().access("!hasRole('BLOCKED')"))
			.oauth2Login()
				.loginPage("/login")
				.defaultSuccessUrl("/")
				.successHandler(oauth2authSuccessHandler)
				.redirectionEndpoint().baseUri("/login/oauth2/code/google").and()
			.and()
				.exceptionHandling().accessDeniedPage("/blocked")
			.and()
				.logout().permitAll();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/images/**") ;
	}
}
