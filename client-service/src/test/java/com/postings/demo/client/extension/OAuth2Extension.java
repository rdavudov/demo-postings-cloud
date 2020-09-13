package com.postings.demo.client.extension;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.postings.demo.client.annotation.OAuth2MockUser;

public class OAuth2Extension implements BeforeEachCallback, AfterEachCallback {

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		getAnnotation(context).ifPresent(oauth2 -> {
			Optional<Object> instance = context.getTestInstance() ;
			if (instance.isPresent()) {
				OAuth2Test test = (OAuth2Test) instance.get() ;
				test.setSession(null);
			}
		}) ;
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		getAnnotation(context).ifPresent(oauth2 -> {
			Optional<Object> instance = context.getTestInstance() ;
			if (instance.isPresent()) {
				Instant now = Instant.now() ;
				OAuth2Test test = (OAuth2Test) instance.get() ;
				OAuth2AuthenticationToken authToken = authToken(oauth2) ;
				
				OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(test.getClientRegistrationRepository().findByRegistrationId(oauth2.clientId()), 
						oauth2.principal(), new OAuth2AccessToken(TokenType.BEARER, "abc", now, now.plusMillis(60000L))) ;
				
				test.getOauth2AuthorizedClientRepository().saveAuthorizedClient(client, authToken, null, null);
				
				MockHttpSession session = new MockHttpSession();
				session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(authToken));
				test.setSession(session);
			}
		});
	}
	
	private OAuth2AuthenticationToken authToken(OAuth2MockUser annotation) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        Arrays.stream(annotation.authorities()).forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));
        
        OidcUser user = new DefaultOidcUser(authorities, getIdToken(annotation), "sub");
        return new OAuth2AuthenticationToken(user, authorities, annotation.clientId());
    }
	
	private OidcIdToken getIdToken(OAuth2MockUser annotation) {
		Instant now = Instant.now() ;
		OidcIdToken.Builder builder = OidcIdToken.withTokenValue("abc") ;
		builder.issuedAt(now) ;
		builder.expiresAt(now.plusMillis(60000)) ;
		builder.claim("sub", annotation.principal()) ;
		Arrays.stream(annotation.claims()).forEach(claim -> {
			String[] claimParts = claim.split("=", 2) ;
			builder.claim(claimParts[0], claimParts[1]) ;
		});
		return builder.build() ;
	}
	
	public Optional<OAuth2MockUser> getAnnotation(ExtensionContext context) {
		Optional<Method> testMethod = context.getTestMethod() ;
		if (testMethod.isPresent() && testMethod.get().isAnnotationPresent(OAuth2MockUser.class)) {
			return Optional.of(testMethod.get().getAnnotation(OAuth2MockUser.class)) ;
		} else {
			Optional<Class<?>> clazz = context.getTestClass() ;
			if (clazz.isPresent() && clazz.get().isAnnotationPresent(OAuth2MockUser.class)) {
				return Optional.of(clazz.get().getAnnotation(OAuth2MockUser.class)) ;
			}
		}
		return Optional.empty() ;
	}
	
	
}
