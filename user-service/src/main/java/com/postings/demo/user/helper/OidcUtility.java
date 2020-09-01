package com.postings.demo.user.helper;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class OidcUtility {
	public static OidcUser getOidcUserFromJwt(JwtAuthenticationToken token) {
		Jwt jwt = token.getToken() ;
		OidcIdToken idToken = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getClaims()) ;
		return new DefaultOidcUser(token.getAuthorities(), idToken) ;
	}
}
