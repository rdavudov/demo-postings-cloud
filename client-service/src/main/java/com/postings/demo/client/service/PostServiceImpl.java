package com.postings.demo.client.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.postings.demo.client.dto.Post;

@Service
public class PostServiceImpl implements PostService {
	@Autowired
	private WebClient webClient ;

	@Override
	public Post create(Post post, String token) {
		return this.webClient
				.post()
				.uri("http://gateway/api/posts/")
				.headers(header -> header.setBearerAuth(token))
				.body(post, Post.class)
				.retrieve()
				.bodyToMono(Post.class)
				.block(); 
	}

	@Override
	public Post get(Long id, String token) {
		return this.webClient
				.get()
				.uri("http://gateway/api/posts/" + id)
				.headers(header -> header.setBearerAuth(token))
				.retrieve()
				.bodyToMono(Post.class) 
				.block() ;
	}
	
	@Override
	public List<Post> getAll(Integer page, Integer size, String token) {
		return this.webClient
				.get()
				.uri("http://gateway/api/posts")
					.attributes(atts -> {
						if (page != null) {
							atts.put("page", page) ;
						}
						if (size != null) {
							atts.put("size", size) ;
						}
					})
				.headers(header -> header.setBearerAuth(token))
				.retrieve()
				.toEntityList(Post.class)
				.block()
				.getBody() ;
	}

	@Override
	public Post update(Long id, Post post, String token) {
		return this.webClient
				.put()
				.uri("http://gateway/api/posts/")
				.headers(header -> header.setBearerAuth(token))
				.body(post, Post.class)
				.retrieve()
				.bodyToMono(Post.class)
				.block(); 
	}

	@Override
	public void delete(Long id, String token) {
		this.webClient
		.delete()
		.uri("http://gateway/api/posts/" + id)
		.headers(header -> header.setBearerAuth(token))
		.retrieve()
		.toBodilessEntity() ;
	}

	@Override
	public List<Post> search(String text, Boolean onlyTitle, Long categoryId, String hashtags, Integer page, Integer size, String token) {
		return this.webClient
				.get()
				.uri("http://gateway/api/posts/search")
					.attributes(atts -> {
						if (page != null) {
							atts.put("page", page) ;
						}
						if (size != null) {
							atts.put("size", size) ;
						}
						if (text != null) {
							atts.put("text", text) ;
						}
						if (onlyTitle != null) {
							atts.put("onlyTitle", onlyTitle) ;
						}
						if (categoryId != null) {
							atts.put("categoryId", categoryId) ;
						}
						if (hashtags != null) {
							atts.put("hashtags", hashtags) ;
						}
					})
				.headers(header -> header.setBearerAuth(token))
				.retrieve()
				.toEntityList(Post.class)
				.block()
				.getBody() ;
	}
}
