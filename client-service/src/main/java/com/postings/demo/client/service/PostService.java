package com.postings.demo.client.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.postings.demo.client.dto.Post;

public interface PostService {
	Post create(Post post, String token) ;
	
	Post get(Long id, String token) ;
	
	Post update(Long id, Post post, String token) ;
	
	void delete(Long id, String token) ;
	
	List<Post> search(String text, Boolean onlyTitle, Long categoryId, String hashtags, Integer page, Integer size, String token) ;
	
	List<Post> getAll(Integer page, Integer size, String token) ;
}
