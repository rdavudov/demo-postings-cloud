package com.postings.demo.client.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.postings.demo.client.dto.Post;
import com.postings.demo.client.service.PostService;

@Controller
@RequestMapping("/posts")
public class PostController {
	
	@Autowired
	private PostService postService ; 
	
	@GetMapping("/{id}")
	public String getPost(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal OidcUser oidcUser) {
		Post post = postService.get(id, oidcUser.getIdToken().getTokenValue());
		model.addAttribute("post", post) ;
		return "post" ;
	}
	
	@GetMapping
	public String getPosts(Model model, @RequestParam(name="page", defaultValue = "0") Integer page, @RequestParam(name="size", defaultValue = "20") Integer size, @AuthenticationPrincipal OidcUser oidcUser) {
		List<Post> posts = postService.getAll(page, size, oidcUser.getIdToken().getTokenValue()) ;
		model.addAttribute("count", posts.size()) ;
		return "posts" ;
	}
	
	@GetMapping("/search")
	public String searchPosts(Model model, 
			@RequestParam(name = "text", required = false) String text, 
			@RequestParam(name = "onlyTitle", required = false) Boolean onlyTitle, 
			@RequestParam(name = "categoryId", required = false) Long categoryId, 
			@RequestParam(name = "hashtags", required = false) String hashtags,
			@RequestParam(name="page", defaultValue = "0") Integer page, 
			@RequestParam(name="size", defaultValue = "20") Integer size,
			@AuthenticationPrincipal OidcUser oidcUser) {
		List<Post> posts = postService.search(text, onlyTitle, categoryId, hashtags, page, size, oidcUser.getIdToken().getTokenValue()) ;
		model.addAttribute("count", posts.size()) ;
		return "posts" ;
	}
	
	@PostMapping
	public String createPost(Model model, @Valid @RequestBody Post post, @AuthenticationPrincipal OidcUser oidcUser) {
		Post created = postService.create(post, oidcUser.getIdToken().getTokenValue()) ;
		
		return "post" ;
	}
	
	@PutMapping("/{id}")
	public String updatePost(Model model, @PathVariable("id") Long id, @Valid @RequestBody Post post, @AuthenticationPrincipal OidcUser oidcUser) {
		Post updated = postService.update(id, post, oidcUser.getIdToken().getTokenValue()) ;
		
		return "post" ;
	}
	
	@DeleteMapping("/{id}")
	public String deletePost(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal OidcUser oidcUser) {
		postService.delete(id, oidcUser.getIdToken().getTokenValue()) ;
		
		return "posts" ;
	}
	
	@ExceptionHandler(WebClientResponseException.class)
	public String handleError(WebClientResponseException ex) {
		
		if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
			return "notfound" ;
		} else if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
			return "forbidden" ;
		} else if (ex.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
			return "login" ;
		} 
		
		return "error" ;
	}
}
