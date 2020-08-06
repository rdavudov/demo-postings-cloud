package com.postings.demo.post.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.postings.demo.post.model.Category;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {
	
}
