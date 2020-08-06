package com.postings.demo.post.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.postings.demo.post.repository.CategoryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidCategoryValidator implements ConstraintValidator<ValidCategory, Long> {

	@Autowired
	private CategoryRepository categoryRepository ;
	
	@Override
	public boolean isValid(Long id, ConstraintValidatorContext context) {
		try {
			return categoryRepository.findById(id).isPresent() ;
		} catch (Exception e) {
			log.error("error in getting category {}", id, e);
		}
		return false ;
	}
}
