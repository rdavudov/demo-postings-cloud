package com.postings.demo.client.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.postings.demo.client.dto.User;
import com.postings.demo.client.service.UserService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration("oauth2authSuccessHandler")
@RequiredArgsConstructor
@Slf4j
@Getter
public class OAuth2AuthSuccessHandler implements AuthenticationSuccessHandler {
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Autowired
	private final UserService userService ; 
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal() ;
		log.info("user {} successfully logged", oidcUser.getEmail());
		
		User user = userService.getUser(oidcUser.getSubject(), oidcUser.getIdToken().getTokenValue()) ;
		log.info("user details received {}", user);
		
		getRedirectStrategy().sendRedirect(request, response, "/");
	}
}
