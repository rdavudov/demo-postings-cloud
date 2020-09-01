package com.postings.demo.post.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.service.UserService;

@Configuration
@Order(50)
public class PostSecurityConfig extends WebSecurityConfigurerAdapter {
	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuer;

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	private String jwkSetUri;
	
	@Autowired
	private UserService userService ;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(reqs -> 
				reqs
					.mvcMatchers("/api/*/admin/**").hasRole("ADMIN")
					.mvcMatchers("/api/*/categories/**").hasRole("ADMIN")
					.mvcMatchers("/api/*/posts/**").authenticated()
					.anyRequest().denyAll()
			)
			.oauth2ResourceServer()
				.jwt().jwtAuthenticationConverter(getJwtAuthenticationConverter()) ;
	}
	
	@Bean
	public JwtDecoder decoder() {
		NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);

		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
		OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer);

		jwtDecoder.setJwtValidator(withAudience);

		return jwtDecoder;
	}
	
	private Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
	    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
	    converter.setJwtGrantedAuthoritiesConverter(getJwtGrantedAuthoritiesConverter());
	    return converter;
	}
	
	private Converter<Jwt, Collection<GrantedAuthority>> getJwtGrantedAuthoritiesConverter() {  
		return new Converter<Jwt, Collection<GrantedAuthority>>() {
			@Override
			public Collection<GrantedAuthority> convert(Jwt source) {
				UserRole role = userService.getRoles(source.getSubject(), source.getTokenValue()) ;
				if (role != null) {
					List<GrantedAuthority> authorities = role.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList()) ;
					authorities.add(new SimpleGrantedAuthority("ROLE_USER")) ;
					return Collections.unmodifiableList(authorities) ;
				}
				return List.of(new SimpleGrantedAuthority("ROLE_USER"));
			}
		};  
	}  
}
