package com.postings.demo.user.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserStatsDto {
	@NotNull
	private String id ;
	private Integer posts ;
	private Integer followers ;
	private Integer following ;
}
