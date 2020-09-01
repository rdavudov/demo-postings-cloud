package com.postings.demo.post.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postings.demo.post.model.Category;
import com.postings.demo.post.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "${service.base.uri}/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {

	@Value("${service.base.uri}/categories")
	private String baseUri ;

	@Autowired
	private final CategoryService categoryService ;

	@GetMapping("/{id}")
	public ResponseEntity<?> getCategory(@PathVariable("id") Long id, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		return categoryService.findById(id).map(cat -> {
			try {
				return ResponseEntity.ok()
						.location(new URI(baseUri + "/" + id))
						.body(cat) ;
			} catch (Exception e) {
				log.error("error in getting category", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}
	
	@GetMapping
	public Iterable<Category> getCategories(@AuthenticationPrincipal JwtAuthenticationToken jwt) {
		return categoryService.findAll() ;
	}
	
	@PostMapping
	public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		try {
			Category createdCategory = categoryService.save(category) ;
			return ResponseEntity
					.created(new URI(baseUri + "/" + createdCategory.getId()))
					.body(createdCategory);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateategory(@PathVariable("id") Long id, @Valid @RequestBody Category category, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		return categoryService.findById(id).map(cat -> {
			try {
				category.setId(id);
				Category createdCategory = categoryService.save(category) ;
				return ResponseEntity
						.ok()
						.location(new URI(baseUri + "/" + createdCategory.getId()))
						.body(createdCategory);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id, @AuthenticationPrincipal JwtAuthenticationToken jwt) {
		return categoryService.findById(id).map(cat -> {
			try {
				categoryService.delete(id) ;
				return ResponseEntity.ok().build();
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}
}
