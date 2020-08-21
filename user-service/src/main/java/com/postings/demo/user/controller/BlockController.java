package com.postings.demo.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postings.demo.user.model.Blocked;
import com.postings.demo.user.service.BlockedService;
import com.postings.demo.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${service.base.uri}/block")
@Slf4j
@Api("Blocked")
public class BlockController {
	
	@Value("${service.base.uri}/block")
	private String baseUri ;
	
	@Autowired
	private UserService userService ;
	
	@Autowired
	private BlockedService blockedService ;
	
	@PostMapping("/{id}")
	@ApiOperation("block user")
	public ResponseEntity<?> block(@PathVariable("id") String id) {
		return userService.findById(id).map(user -> {
			try {
				Blocked blocked = new Blocked(id) ;
				boolean blockingResult = blockedService.block(blocked);
				if (blockingResult) {
					return ResponseEntity.ok().build() ;
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
				}
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation("unblock user")
	public ResponseEntity<?> unblock(@PathVariable("id") String id) {
		return userService.findById(id).map(user -> {
			try {
				boolean unblockingResult = blockedService.unblock(id);
				if (unblockingResult) {
					return ResponseEntity.ok().build() ;
				} else {
					return ResponseEntity.notFound().build() ;
				}
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
}
