package com.postings.demo.post.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.postings.demo.post.model.Post;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
	Page<Post> findByUserId(String userId, Pageable pageable) ;
	
	List<Post> findByUserIdOrIsPublic(String userId, boolean isPublic) ;
	
	@Query("select count(*) from Post p where p.userId = :userId")
	Integer findCountByUserId(@Param("userId") String userId) ;
}
