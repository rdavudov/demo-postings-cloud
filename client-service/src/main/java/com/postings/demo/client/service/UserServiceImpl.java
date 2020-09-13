package com.postings.demo.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.postings.demo.client.dto.User;
import com.postings.demo.client.dto.UserRole;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private WebClient webClient ;
	
	@Override
	public UserRole getRoles(String userId, String token) {
		return this.webClient
				.get()
				.uri("http://gateway/api/roles/" + userId)
				.headers(header -> header.setBearerAuth(token))
				.retrieve()
				.bodyToMono(UserRole.class) 
				.onErrorReturn(new UserRole())
				.block() ;
		
	}

	@Override
	public User getUser(String userId, String token) {
		return this.webClient
				.get()
				.uri("http://gateway/api/users/" + userId)
				.headers(header -> header.setBearerAuth(token))
				.retrieve()
				.bodyToMono(User.class) 
				.block() ;
	}
}
