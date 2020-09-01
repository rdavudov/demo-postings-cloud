package com.postings.demo.user.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postings.demo.user.dto.UserStatsDto;
import com.postings.demo.user.helper.OidcUtility;
import com.postings.demo.user.mapper.UserStatsMapper;
import com.postings.demo.user.model.UserStats;
import com.postings.demo.user.service.UserService;
import com.postings.demo.user.service.UserStatsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${service.base.uri}/stats")
@Api("Statistics")
@Slf4j
public class UserStatsController {
	
	@Value("${service.base.uri}/stats")
	private String baseUri ;
	
	@Autowired
	private UserService userService ;
	
	@Autowired
	private UserStatsMapper statsMapper ;
	
	@Autowired
	private UserStatsService statsService ;
	
	@PostMapping("/{id}")
	@ApiOperation("create a new user")
	public ResponseEntity<?> createStats(@PathVariable("id") String id, @Valid @RequestBody UserStatsDto dto, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		OidcUser oidcUser = OidcUtility.getOidcUserFromJwt(jwt) ;
		
		if (!id.equals(oidcUser.getSubject())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build() ;
		}
		
		return userService.findById(id).map(user -> {
			try {
				Optional<UserStats> existing = statsService.findById(id) ;
				if (existing.isPresent()) {
					UserStats existingStats = existing.get() ;
					statsMapper.mapUserStats(dto, existingStats);
					statsService.save(existingStats) ;
				} else {
					statsService.save(statsMapper.mapToUserStats(dto)) ;
				}
				
				return ResponseEntity.ok().build() ;
			} catch (Exception e) {
				log.error("exception for input {}", new Object[] {user}, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.notFound().build()) ;
	}
}
