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
import org.springframework.web.bind.annotation.ModelAttribute;
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
@RequestMapping("${service.base.uri}")
@Slf4j
@Api("Users")
public class UserController {
	
	@Value("${service.base.uri}")
	public String BASE_URI ;
	
	@Autowired
	private UserService service ;
	
	@PostMapping
	@ApiOperation("create a new user")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDto user, @ApiIgnore Errors errors) {
		if (errors.hasErrors()) {
			String validationFailReason = errors.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining(", ")) ;
			log.error("user validation failed for request {} with validations {}", user, validationFailReason);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validationFailReason) ;
		}
		
		log.info("saving user {}", user);
		try {
			User saved = service.save(user) ;
			return ResponseEntity
					.created(new URI(BASE_URI + "/" + saved.getId()))
					.eTag(Integer.toString(saved.getVersion()))
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
							.eTag(Integer.toString(user.getVersion()))
							.location(new URI(BASE_URI + "/" + user.getId()))
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
	public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDto dto, @PathVariable("id") String id, @ApiIgnore Errors errors) {
		log.info("updating user {} with data {}", id, dto.toString());
		
		return service.update(id, dto).map(user -> {
			try {
				return ResponseEntity
						.ok()
						.eTag(Integer.toString(user.getVersion()))
						.location(new URI(BASE_URI + "/" + user.getId()))
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
