package com.postings.demo.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.postings.demo.user.model.Blocked;

@Repository
public interface BlockedRepository extends MongoRepository<Blocked, String> {
	
}
