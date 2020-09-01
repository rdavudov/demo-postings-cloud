package com.postings.demo.user.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtility {
	public static String toJson(Object object) {
		try {
			return new ObjectMapper().writer().writeValueAsString(object) ;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e) ;
		}
	}
}
