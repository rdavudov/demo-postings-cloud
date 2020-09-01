package com.postings.demo.user.controller;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;
import com.postings.demo.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("${service.base.uri}/admin/users")
@Slf4j
@Api("Users")
public class AdminUserController {
	
	@Value("${service.base.uri}/users")
	private String baseUri ;
	
	@Autowired
	private UserService service ;
	
	@PostMapping
	@ApiOperation("create a new user")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDto user) {
		log.info("saving user {}", user);
		try {
			User saved = service.save(user) ;
			return ResponseEntity
					.created(new URI(baseUri + "/" + saved.getId()))
					.body(saved);
		} catch (Exception e) {
			log.error("exception for input {}", new Object[] {user}, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
		}
	}
	
	@GetMapping("/{id}")
	@ApiOperation("get user by id")
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		return service.findById(id).map(user -> {
				try {
					return ResponseEntity
							.ok()
							.location(new URI(baseUri + "/" + user.getId()))
							.body(user) ;
				} catch (Exception e) {
					log.error("exception for input {}", new Object[] {user}, e);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
				}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
	
	@GetMapping
	@ApiOperation("get all users or filter users")
	public Iterable<User> getUsers(@RequestParam Map<String, Object> params) {
		if (params.size() == 0) {
			return service.findAll() ;
		} else {
			User user = new ObjectMapper().convertValue(params, User.class) ;
			return service.find(user) ;
		}
	}
	
	@PutMapping("/{id}")
	@ApiOperation("update an existing user")
	public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDto dto, @PathVariable("id") String id) {
		log.info("updating user {}", id);
		
		return service.update(id, dto).map(user -> {
			try {
				return ResponseEntity
						.ok()
						.location(new URI(baseUri + "/" + user.getId()))
						.body(user) ;
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation("delete a user")
	public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
		return service.findById(id).map(user -> {
			try {
				service.delete(id) ;
				return ResponseEntity.noContent().build() ;
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
	
	//TODO: use also AdviceController for more complicated exception handling
}
