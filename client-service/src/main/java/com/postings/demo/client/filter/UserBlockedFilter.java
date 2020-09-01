package com.postings.demo.client.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.filter.GenericFilterBean;

import com.postings.demo.client.dto.UserRole;
import com.postings.demo.client.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserBlockedFilter extends GenericFilterBean {
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	private final UserService userService ;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		HttpServletResponse servletResponse = (HttpServletResponse) response;

		if (!"/login".equals(servletRequest.getRequestURI())) {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication() ;
			if (authentication != null && authentication.isAuthenticated() && OAuth2AuthenticationToken.class.isInstance(authentication)) {

				DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal() ;
				
				UserRole roles = userService.getRoles(oidcUser.getSubject(), oidcUser.getIdToken().getTokenValue()) ;
				
				if (roles.getRoles().contains("BLOCKED")) {
					redirectStrategy.sendRedirect(servletRequest, servletResponse, "/blocked");
				}
			}
		}

		chain.doFilter(request, response);
	}
}
