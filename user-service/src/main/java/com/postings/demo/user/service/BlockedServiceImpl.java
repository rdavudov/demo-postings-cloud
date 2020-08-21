package com.postings.demo.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.postings.demo.user.model.Blocked;
import com.postings.demo.user.repository.BlockedRepository;

@Service
public class BlockedServiceImpl implements BlockedService {

	@Autowired
	private BlockedRepository blockedRepository ; 
	
	@Override
	public boolean block(Blocked blocked) {
		return blockedRepository.save(blocked) != null ;
	}

	@Override
	public boolean unblock(String id) {
		return blockedRepository.findById(id).map(b -> {
			blockedRepository.deleteById(id);
			return true ;
		}).orElse(false) ;
	}
}
