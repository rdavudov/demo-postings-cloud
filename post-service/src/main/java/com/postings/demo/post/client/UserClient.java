package com.postings.demo.post.client;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.postings.demo.post.model.User;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "user", path = "/api/v1/users")
public interface UserClient {
	
	@GetMapping(path = "/{id}")
	@CircuitBreaker(name = "user", fallbackMethod = "getUserFallback")
	@Retry(name = "user", fallbackMethod = "getUserFallback")
	Optional<User> getUser(@PathVariable("id") String userId) ;
	
	@PutMapping(path = "/{id}")
	@CircuitBreaker(name = "user", fallbackMethod = "updateUserFallback")
	@Retry(name = "user", fallbackMethod = "updateUserFallback")
	void updateUser(@PathVariable("id") String userId, User dto) ;
	
	default Optional<User> getUserFallback(String userId, Throwable throwable) {
		return Optional.empty() ;
	}
	
	default void updateUserFallback(String userId, User dto, Throwable throwable) {
		
	}
}
