package com.postings.demo.client.service;

import com.postings.demo.client.dto.User;
import com.postings.demo.client.dto.UserRole;

public interface UserService {
	//TODO: add resilience4j
	UserRole getRoles(String userId, String token) ;
	
	User getUser(String userId, String token) ;
}
