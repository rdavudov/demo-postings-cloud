package com.postings.demo.user.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.postings.demo.user.service.UserService;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

	@Autowired
	private UserService service ;
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return service.findByUsername(value).isEmpty() ;
	}
}
