package com.postings.demo.client.service;

import java.util.Set;

import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Service;

import com.postings.demo.client.dto.User;
import com.postings.demo.client.dto.UserRole;
import com.postings.demo.client.dto.UserStats;

@Service
@Primary
public class FakeUserServiceImpl implements UserService {

	@Override
	public UserRole getRoles(String userId, String token) {
		return new UserRole("radjab@gmail.com", Set.of());
	}

	@Override
	public User getUser(String userId, String token) {
		return new User("12345", "radjab@gmail.com", "Rajab", "Davudov", "http://xxx.com", new UserStats());
	}
}
