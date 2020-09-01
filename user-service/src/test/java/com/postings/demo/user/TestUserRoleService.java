package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.EMAIL;
import static com.postings.demo.user.builder.TestUserBuilder.ROLES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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

import com.postings.demo.user.model.UserRole;
import com.postings.demo.user.repository.UserRoleRepository;
import com.postings.demo.user.service.UserRoleService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
public class TestUserRoleService {
	
	@Autowired
	private UserRoleService userRoleService ;
	
	@MockBean
	private UserRoleRepository userRoleRepository ;
	
	@Test
	public void givenUserRoleWhenSavedThenSuccess() {
		UserRole role = new UserRole(EMAIL, ROLES) ;
		doReturn(role).when(userRoleRepository).save(any()) ;
		
		userRoleService.save(role);
		
		verify(userRoleRepository).save(any()) ;
	}
	
	@Test
	public void givenEmailWhenDeleteThenSuccess() {
		doNothing().when(userRoleRepository).deleteById(anyString()) ;
		
		userRoleService.delete(EMAIL);
		
		verify(userRoleRepository).deleteById(any()) ;
	}
	
	@Test
	public void givenBlockedWhenGetThenSuccess() {
		UserRole role = new UserRole(EMAIL, ROLES) ;
		doReturn(Optional.of(role)).when(userRoleRepository).findById(anyString()) ;
		
		Optional<UserRole> result = userRoleService.get(EMAIL);
		assertThat(result).isPresent() ;
	}
}
