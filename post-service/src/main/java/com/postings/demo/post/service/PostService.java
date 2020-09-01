package com.postings.demo.post.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.postings.demo.post.model.Post;

public interface PostService {
	
	Optional<Post> findById(long id) ;
	
	Page<Post> findByUserId(String userId, Pageable pageable) ;
	
	List<Post> findByUserIdOrIsPublic(String userId, boolean isPublic) ;
	
	Post save(Post post) ;
	
	Post update(Post post) ;
	
	void delete(Long id) ;
	
	int findCountByUserId(String userId) ;
}
