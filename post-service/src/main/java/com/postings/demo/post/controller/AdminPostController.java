package com.postings.demo.post.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postings.demo.post.mapper.PostMapper;
import com.postings.demo.post.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "${service.base.uri}/admin/posts")
@Slf4j
@RequiredArgsConstructor
public class AdminPostController {

	@Value("${service.base.uri}/admin/posts")
	private String baseUri ;

	@Autowired
	private final PostService postService ;

	@Autowired
	private final PostMapper postMapper ;

	@GetMapping("/{id}")
	public ResponseEntity<?> getPost(@PathVariable("id") Long id) {
		return postService.findById(id).map(post -> {
			try {
				return ResponseEntity.ok()
						.location(new URI(baseUri + "/" + id))
						.body(postMapper.toDto(post)) ;
			} catch (Exception e) {
				log.error("error in getting post {}", id, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {
		return postService.findById(id).map(post -> {
			try {
				postService.delete(id) ;
				return ResponseEntity.ok().build();
			} catch (Exception e) {
				log.error("error in deleting post {}", id, e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
			}
		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()) ;
	}
}
