package com.postings.demo.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.postings.demo.post.model.Post;

public interface PostService {
	
	public Optional<Post> findById(long id) ;
	
	public Page<Post> findByUserId(String userId, Pageable pageable) ;
	
	public List<Post> findByUserIdOrIsPublic(String userId, boolean isPublic) ;
	
	public Post save(Post post) ;
	
	public Post update(Post post) ;
	
	public void delete(Long id) ;
}
