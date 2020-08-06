package com.postings.demo.user.service;

import java.util.List;
import java.util.Optional;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;

public interface UserService {
	Optional<User> findById(String id) ;
	
	Optional<User> findByUsername(String username) ;
	
	Optional<User> findByEmail(String email) ;
	
	List<User> findAll() ;
	
	List<User> find(User filter) ;
	
	User save(UserCreateDto user) ;
	
	Optional<User> update(String id, UserUpdateDto user) ;
	
	void delete(String id) ;
}
