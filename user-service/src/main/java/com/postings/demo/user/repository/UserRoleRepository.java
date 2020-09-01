package com.postings.demo.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.postings.demo.user.model.UserRole;

@Repository
public interface UserRoleRepository extends MongoRepository<UserRole, String> {
	
}
