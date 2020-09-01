package com.postings.demo.user.service;

import java.util.Optional;

import com.postings.demo.user.model.UserStats;

public interface UserStatsService {
	Optional<UserStats> findById(String id) ;
	
	UserStats save(UserStats stats) ;
	
	void delete(String id) ;
}
