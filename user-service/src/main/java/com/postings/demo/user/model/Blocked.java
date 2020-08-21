package com.postings.demo.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Blocked")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Blocked {
	@Id
	private String id ;
}
