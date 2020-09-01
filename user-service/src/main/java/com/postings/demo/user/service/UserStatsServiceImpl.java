package com.postings.demo.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.postings.demo.user.model.UserStats;
import com.postings.demo.user.repository.UserStatsRepository;

@Service
public class UserStatsServiceImpl implements UserStatsService {

	@Autowired
	private UserStatsRepository repository ; 
	
	@Override
	public Optional<UserStats> findById(String id) {
		return repository.findById(id) ;
	}

	@Override
	public UserStats save(UserStats stats) {
		return repository.save(stats) ;
	}

	@Override
	public void delete(String id) {
		repository.deleteById(id);
	}
}
