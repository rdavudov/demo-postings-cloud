package com.postings.demo.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.postings.demo.user.model.User;

@Repository
public interface UserRepositoy extends MongoRepository<User, String> {
	Optional<User> findByUsername(String username);
	
	Optional<User> findByEmail(String email);
}
