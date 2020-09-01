package com.postings.demo.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OAuth2MockUser {
	String[] authorities() default "ROLE_USER" ;
	String[] claims() default "sub=id" ;
	String clientId() default "google" ;
	String principal() default "id" ;
}
