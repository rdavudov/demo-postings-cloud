package com.postings.demo.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FriendController {

	@GetMapping("/friend")
	public String friend() {
		return "You are my friend" ;
	}
}
