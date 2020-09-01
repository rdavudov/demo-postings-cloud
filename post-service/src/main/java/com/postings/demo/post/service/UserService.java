package com.postings.demo.post.service;

import java.util.Set;

import org.springframework.scheduling.annotation.Async;

import com.postings.demo.post.dto.UserRole;
import com.postings.demo.post.dto.UserStats;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

public interface UserService {
	@CircuitBreaker(name = "user", fallbackMethod = "getRolesFallback")
	@Retry(name = "user", fallbackMethod = "getRolesFallback")
	UserRole getRoles(String userId, String token) ;
	
	@CircuitBreaker(name = "user", fallbackMethod = "setStatsFallback")
	@Retry(name = "user", fallbackMethod = "setStatsFallback")
	@Async
	void setStats(String userId, String token, UserStats stats) ;
	
	default UserRole getRolesFallback(String userId, String token, Throwable throwable) {
		return new UserRole(userId, Set.of()) ;
	}
	
	default void setStatsFallback(String userId, String token, UserStats stats) {
		
	}
}
