package com.postings.demo.user.service;

import java.util.Optional;

import com.postings.demo.user.model.UserRole;

public interface UserRoleService {
	
	void save(UserRole role) ;
	
	void delete(String email) ;
	
	Optional<UserRole> get(String email) ;
}
