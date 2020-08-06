package com.postings.demo.post.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

// applies to all types as specified in generics
@Converter(autoApply = true)
public class HashtagConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		if (attribute == null || attribute.size() == 0) {
			return null ;
		}
		return attribute.stream().collect(Collectors.joining(","));
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isBlank()) {
			return new ArrayList<String>() ;
		}
		return Arrays.stream(dbData.split(",")).collect(Collectors.toList()) ;
	}
}
