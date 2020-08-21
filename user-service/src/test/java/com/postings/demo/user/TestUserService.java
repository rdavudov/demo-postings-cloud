package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.postings.demo.user.dto.UserCreateDto;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;
import com.postings.demo.user.repository.UserRepository;
import com.postings.demo.user.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
public class TestUserService {
	
	@Autowired
	private UserService service ;
	
	@MockBean
	private UserRepository repository ;
	
	@Test
	@DisplayName("find user by id Success")
	public void testFindUserByIdSuccess() {
		doReturn(Optional.of(fullUser())).when(repository).findById(ID) ;
		Optional<User> user = service.findById(ID) ;
		assertThat(user).withFailMessage("User had to be found").isPresent() ;
	}
	
	@Test
	@DisplayName("find user by id Not Found")
	public void testFindUserByIdNotFound() {
		doReturn(Optional.empty()).when(repository).findById(ID) ;
		Optional<User> user = service.findById(ID) ;
		assertThat(user).withFailMessage("User had not to be found").isEmpty() ;
	}
	
	@Test
	@DisplayName("find user by email Success")
	public void testFindUserByEmailSuccess() {
		doReturn(Optional.of(fullUser())).when(repository).findByEmail(EMAIL) ;
		Optional<User> user = service.findByEmail(EMAIL);
		assertThat(user).withFailMessage("User had to be found").isPresent() ;
	}
	
	@Test
	@DisplayName("create user Success")
	public void testCreateUserSuccess() {
		UserCreateDto newDto = createDto() ;
		doReturn(fullUser()).when(repository).save(any()) ;
		User savedUser = service.save(newDto);
		assertThat(savedUser).withFailMessage("User must be not null").isNotNull() ;
		assertThat(savedUser.getId()).withFailMessage("id failed").isNotNull().isEqualTo(ID) ;
		assertThat(savedUser.getEmail()).withFailMessage("email failed").isNotNull().isEqualTo(EMAIL) ;
	}
	
	
	@Test
	@DisplayName("update user Success")
	public void testUpdateUserSuccess() {
		User existingUser = fullUser() ;
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setLastName(DTO_LASTNAME);
		doReturn(Optional.of(existingUser)).when(repository).findById(ID) ;
		doReturn(existingUser).when(repository).save(existingUser) ;
		Optional<User> user = service.update(ID, dto);
		
		assertThat(user).withFailMessage("user must be updated").isPresent() ;
		
		User savedUser = user.get() ;
		assertThat(savedUser).withFailMessage("User must be not null").isNotNull() ;
		assertThat(savedUser.getId()).withFailMessage("id failed").isNotNull().isEqualTo(ID) ;
		assertThat(savedUser.getEmail()).withFailMessage("email failed").isNotNull().isEqualTo(EMAIL) ;
		assertThat(savedUser.getFirstName()).withFailMessage("firstname failed").isNotNull().isEqualTo(FIRSTNAME) ;
		assertThat(savedUser.getLastName()).withFailMessage("lastname failed").isNotNull().isEqualTo(DTO_LASTNAME) ;
	}
	
	@Test
	@DisplayName("delete user Success")
	public void testDeleteUserSuccess() {
		service.delete(ID) ;
		verify(repository).deleteById(ID);
	}
}
