package com.postings.demo.post.mapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.service.UserService;

@Component
public class UserGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

	@Autowired
	private UserService userService ;
	
	@Override
	public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Set<GrantedAuthority> mapAuthorities = new HashSet<>() ;
		
		authorities.forEach(authority -> {
			if (OidcUserAuthority.class.isInstance(authority)) {
				OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority ;
				OidcIdToken idToken = oidcUserAuthority.getIdToken() ;
				OidcUserInfo userInfo = oidcUserAuthority.getUserInfo() ;
				
				UserRole role = userService.getRoles(idToken.getSubject(), idToken.getTokenValue()) ;
				role.getRoles().forEach(r -> mapAuthorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
			}
			
			mapAuthorities.add(authority) ;
		});
		
		return mapAuthorities;
	}
}
