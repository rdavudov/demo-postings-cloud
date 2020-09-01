package com.postings.demo.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.postings.demo.user.dto.UserStatsDto;
import com.postings.demo.user.model.UserStats;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserStatsMapper {
	@Mapping(target = "id", ignore = true)
	void mapUserStats(UserStatsDto dto, @MappingTarget UserStats stats) ;
	
	UserStats mapToUserStats(UserStatsDto user) ;
}
