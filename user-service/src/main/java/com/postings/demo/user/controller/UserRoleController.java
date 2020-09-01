package com.postings.demo.user.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postings.demo.user.helper.OidcUtility;
import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.service.UserRoleService;
import com.postings.demo.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${service.base.uri}/roles")
@Slf4j
@Api("User Roles")
public class UserRoleController {
	
	@Autowired
	private UserService userService ;
	
	@Autowired
	private UserRoleService userRoleService ;
	
	@PostMapping("/{id}/add")
	@ApiOperation("add role user")
	public ResponseEntity<?> createRole(@PathVariable("id") String id, @Valid @RequestBody UserRole role, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		OidcUser oidcUser = OidcUtility.getOidcUserFromJwt(jwt) ;
		
		if (id.equals(oidcUser.getSubject())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build() ;
		}
		
		return userService.findById(id).map(user -> {
			try {
				Optional<UserRole> roles = userRoleService.get(user.getEmail()) ;
				if (roles.isPresent()) {
					role.getRoles().addAll(roles.get().getRoles()) ;
				}
				
				userRoleService.save(role);
				return ResponseEntity.ok().body(role) ;
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
	
	@PostMapping("/{id}/remove")
	@ApiOperation("delete role user")
	public ResponseEntity<?> deleteRole(@PathVariable("id") String id, @Valid @RequestBody UserRole role, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		OidcUser oidcUser = OidcUtility.getOidcUserFromJwt(jwt) ;
		
		if (id.equals(oidcUser.getSubject())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build() ;
		}
		
		return userService.findById(id).map(user -> {
			try {
				Optional<UserRole> roles = userRoleService.get(user.getEmail()) ;
				if (roles.isPresent()) {
					role.setRoles(roles.get().getRoles().stream().filter(r -> !role.getRoles().contains(r)).collect(Collectors.toSet()));
					
					if (role.getRoles().size() == 0) {
						userRoleService.delete(user.getEmail());
					} else {
						userRoleService.save(role);
					}
					
					return ResponseEntity.ok().body(role) ;
				}
				
				return ResponseEntity.ok().body(new UserRole(user.getEmail(), new HashSet<String>())) ;
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
	
	@GetMapping("/{id}")
	@ApiOperation("get roles user")
	public ResponseEntity<?> getRole(@PathVariable("id") String id, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		OidcUser oidcUser = OidcUtility.getOidcUserFromJwt(jwt) ;
		
		return userService.findById(id).map(user -> {
			try {
				Optional<UserRole> roles = userRoleService.get(user.getEmail()) ;
				if (roles.isPresent()) {
					return ResponseEntity.ok().body(roles.get()) ;
				} else {
					return ResponseEntity.ok().body(new UserRole(user.getEmail(), new HashSet<String>())) ;
				}
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
}
