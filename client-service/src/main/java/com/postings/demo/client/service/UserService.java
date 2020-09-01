package com.postings.demo.client.service;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import com.postings.demo.client.dto.User;
import com.postings.demo.client.dto.UserRole;

public interface UserService {
	UserRole getRoles(String userId, String token) ;
	
	User getUser(String userId, String token) ;
}
