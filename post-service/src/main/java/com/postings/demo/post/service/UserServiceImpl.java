package com.postings.demo.post.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.dto.UserStats;

@Service
public class UserServiceImpl implements UserService {

	@Value("${service.user.uri}")
	private String userUri ;
	
	@Autowired
	private WebClient webClient ;
	
	@Override
	public UserRole getRoles(String userId, String token) {
		return this.webClient
				.get()
				.uri(userUri + "/roles/" + userId)
				.headers(header -> header.setBearerAuth(token))
				.retrieve()
				.bodyToMono(UserRole.class) 
				.onErrorReturn(new UserRole())
				.block() ;
	}
	
	@Override
	public void setStats(String userId, String token, UserStats stats) {
		this.webClient
				.post()
				.uri(userUri + "/stats/" + userId)
				.bodyValue(stats)
				.headers(header -> header.setBearerAuth(token))
				.retrieve().toBodilessEntity() ;
	}
}
