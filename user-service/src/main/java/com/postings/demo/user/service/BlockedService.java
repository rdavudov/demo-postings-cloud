package com.postings.demo.user.service;

import com.postings.demo.user.model.Blocked;

public interface BlockedService {
	
	boolean block(Blocked blocked) ;
	
	boolean unblock(String id) ;
}
