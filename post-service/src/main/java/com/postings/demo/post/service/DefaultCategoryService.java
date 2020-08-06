package com.postings.demo.post.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.postings.demo.post.model.Category;
import com.postings.demo.post.repository.CategoryRepository;

@Service
public class DefaultCategoryService implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository ;
	
	@Override
	public Optional<Category> findById(Long categoryId) {
		return categoryRepository.findById(categoryId);
	}

	@Override
	public Iterable<Category> findAll() {
		return categoryRepository.findAll() ;
	}

	@Override
	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public void delete(Long categoryId) {
		categoryRepository.deleteById(categoryId);
	}
}
