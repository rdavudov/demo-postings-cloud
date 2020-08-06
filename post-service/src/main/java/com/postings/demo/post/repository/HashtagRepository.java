package com.postings.demo.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.postings.demo.post.model.Hashtag;
import com.postings.demo.post.model.HashtagId;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, HashtagId> {
	void deleteByPostId(Long postId) ;
	
	List<Hashtag> findByPostId(Long postId) ;
}
