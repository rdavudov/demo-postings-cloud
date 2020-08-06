package com.postings.demo.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.mapper.UserMapper;
import com.postings.demo.user.model.User;
import com.postings.demo.user.repository.UserRepositoy;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepositoy repository ; 
	
	@Autowired
	private UserMapper userMapper ;
	
	@Override
	public Optional<User> findById(String id) {
		return repository.findById(id) ;
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return repository.findByUsername(username) ;
	}
	
	@Override
	public Optional<User> findByEmail(String email) {
		return repository.findByEmail(email) ;
	}

	@Override
	public List<User> findAll() {
		return repository.findAll() ;
	}

	@Override
	public User save(UserCreateDto dto) {
		User user = new User() ;
		userMapper.mapCreateUser(dto, user);
		user.setVersion(1);
		return repository.save(user) ;
	}

	@Override
	public Optional<User> update(String id, UserUpdateDto dto) {
		return findById(id).map(user -> {
			userMapper.mapUpdateUser(dto, user);
			user.setVersion(user.getVersion() + 1);
			return Optional.of(repository.save(user));
		}).orElse(Optional.empty()) ;
	}

	@Override
	public void delete(String id) {
		repository.deleteById(id);
	}

	@Override
	public List<User> find(User filter) {
		return repository.findAll(Example.of(filter));
	}
}
