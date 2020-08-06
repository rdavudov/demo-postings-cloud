package com.postings.demo.post.service;

import java.util.Optional;

import com.postings.demo.post.model.Category;

public interface CategoryService {
	public Optional<Category> findById(Long categoryId) ;
	
	public Iterable<Category> findAll() ;
	
	public Category save(Category category) ;
	
	public void delete(Long categoryId) ;
}
