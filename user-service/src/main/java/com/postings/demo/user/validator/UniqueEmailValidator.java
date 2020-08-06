package com.postings.demo.user.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.postings.demo.user.service.UserService;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

	@Autowired
	private UserService service ;
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return service.findByEmail(value).isEmpty() ;
	}
}
