package com.postings.demo.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserGetDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "email", ignore = true)
	void mapUpdateUser(UserUpdateDto dto, @MappingTarget User user) ;
	
	void mapCreateUser(UserCreateDto dto, @MappingTarget User user) ;
	
	UserGetDto mapUserToDto(User user) ;
}
