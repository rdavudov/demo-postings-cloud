package com.postings.demo.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
	void mapUpdateUser(UserUpdateDto dto, @MappingTarget User user) ;
	
	void mapCreateUser(UserCreateDto dto, @MappingTarget User user) ;
	
//	void mapGetUser(User user, @MappingTarget UserCreateDto dto) ;
}
