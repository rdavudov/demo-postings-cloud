package com.postings.demo.security.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final String username = "user";
	private final String password = "pass";

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication()
//		.passwordEncoder(NoOpPasswordEncoder.getInstance())
//		.withUser(username)
//		.password(password)
//		.authorities("HELLO")
//		.and().withUser("friend").password("pass").authorities("HELLO", "FRIEND");
//	}
	
	// UserDetailsService is only used if the AuthenticationManagerBuilder has not been populated and no AuthenticationProviderBean is defined. 
	@Bean
	public UserDetailsService users() {
		UserBuilder users = User.builder();
	    UserDetails user = users
	        .username("user")
	        .password("{noop}pass")
	        .authorities("HELLO")
	        .build();
	    UserDetails fried = users
	        .username("friend")
	        .password("{noop}pass")
	        .authorities("HELLO", "FRIEND")
	        .build();
	    return new InMemoryUserDetailsManager(user, fried);
	}
	
//	@Bean
	public UserDetailsService users(DataSource dataSource) {
	    return new JdbcUserDetailsManager(dataSource); 
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
				.mvcMatchers("/hello/**").hasAuthority("HELLO")
				.mvcMatchers("/friend/**").hasAuthority("FRIEND")
				.anyRequest()
				.authenticated()
			.and()
//				.httpBasic() ;
				.formLogin(form -> form
		            .loginPage("/login")
		            .permitAll()
		        );
	}
	
	
}
