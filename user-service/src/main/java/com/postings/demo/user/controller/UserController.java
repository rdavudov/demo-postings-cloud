package com.postings.demo.user.controller;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserGetDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.helper.OidcUtility;
import com.postings.demo.user.mapper.UserMapper;
import com.postings.demo.user.model.User;
import com.postings.demo.user.model.UserStats;
import com.postings.demo.user.service.UserService;
import com.postings.demo.user.service.UserStatsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${service.base.uri}/users")
@Slf4j
@Api("Users")
public class UserController {
	
	@Value("${service.base.uri}/users")
	private String baseUri ;
	
	@Autowired
	private UserService userService ;
	
	@Autowired
	private UserStatsService userStatsService ;
	
	@Autowired
	private UserMapper userMapper ;
	
	@PostMapping
	@ApiOperation("create a new user")
	public ResponseEntity<?> createUser(@AuthenticationPrincipal JwtAuthenticationToken jwt) {
		OidcUser oidcUser = OidcUtility.getOidcUserFromJwt(jwt) ;

		if (!oidcUser.getEmailVerified()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build() ;
		}
		
		return userService.findById(oidcUser.getSubject()).map(u -> {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build() ;
		}).orElseGet(() -> {
			try {
				User saved = userService.save(getUserDto(oidcUser)) ;
				return ResponseEntity
						.created(new URI(baseUri + "/" + saved.getId()))
						.body(userMapper.mapUserToDto(saved));
			} catch (Exception e) {
				log.error("exception in creating user {}", new Object[] {oidcUser.getEmail()}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}) ;
	}
	
	@GetMapping("/{id}")
	@ApiOperation("get user by id")
	public ResponseEntity<?> getUser(@PathVariable("id") String id, @AuthenticationPrincipal JwtAuthenticationToken token) {
		OidcUser oidcUser = OidcUtility.getOidcUserFromJwt(token) ;
		
		if (!id.equals(oidcUser.getSubject())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build() ;
		}
		
		return userService.findById(oidcUser.getSubject()).map(user -> {
				try {
					UserGetDto getDto = userMapper.mapUserToDto(user) ;
					
					Optional<UserStats> stats = userStatsService.findById(oidcUser.getSubject()) ;
					if (stats.isPresent()) {
						getDto.setStats(stats.get());
					} else {
						getDto.setStats(new UserStats());
					}
					
					return ResponseEntity
							.ok()
							.location(new URI(baseUri + "/" + user.getId()))
							.body(getDto) ;
				} catch (Exception e) {
					log.error("exception for input {}", new Object[] {user}, e);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
				}
		}).orElseGet(() -> {
			try {
				User saved = userService.save(getUserDto(oidcUser)) ;
				return ResponseEntity
						.ok()
						.location(new URI(baseUri + "/" + saved.getId()))
						.body(userMapper.mapUserToDto(saved));
			} catch (Exception e) {
				log.error("exception in creating user {}", new Object[] {oidcUser.getEmail()}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}) ;
	}
	
	@PutMapping("/{id}")
	@ApiOperation("update an existing user")
	public ResponseEntity<?> updateUser(@PathVariable("id") String id, @Valid @RequestBody UserUpdateDto dto, @AuthenticationPrincipal JwtAuthenticationToken token) {
		OidcUser oidcUser = OidcUtility.getOidcUserFromJwt(token) ;
		
		if (!id.equals(oidcUser.getSubject())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build() ;
		}
		
		return userService.update(oidcUser.getSubject(), dto).map(user -> {
			try {
				return ResponseEntity
						.ok()
						.location(new URI(baseUri + "/" + user.getId()))
						.body(userMapper.mapUserToDto(user)) ;
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}

	
	private UserCreateDto getUserDto(OidcUser oidcUser) {
		return new UserCreateDto(oidcUser.getSubject(), oidcUser.getEmail(), oidcUser.getGivenName(), oidcUser.getFamilyName(), oidcUser.getPicture()) ;
	}
}
