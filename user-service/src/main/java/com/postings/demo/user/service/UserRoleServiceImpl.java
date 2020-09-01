package com.postings.demo.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.repository.UserRoleRepository;

@Service
public class UserRoleServiceImpl implements UserRoleService {

	@Autowired
	private UserRoleRepository userRoleRepository ; 
	
	@Override
	public void save(UserRole role) {
		userRoleRepository.save(role) ;
	}
	
	@Override
	public void delete(String email) {
		userRoleRepository.deleteById(email);
	}

	@Override
	public Optional<UserRole> get(String email) {
		return userRoleRepository.findById(email) ;
	}
}
