package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.postings.demo.user.model.Blocked;
import com.postings.demo.user.repository.BlockedRepository;
import com.postings.demo.user.service.BlockedService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
public class TestBlockedService {
	
	@Autowired
	private BlockedService blockedService ;
	
	@MockBean
	private BlockedRepository repository ;
	
	@Test
	@DisplayName("block user Success")
	public void givenBlockedWhenBlockedThenSuccess() {
		Blocked blocked = new Blocked() ;
		blocked.setId(ID);
		
		doReturn(blocked).when(repository).save(any()) ;
		boolean result = blockedService.block(blocked);
		assertThat(result).isTrue() ;
	}
	
	@Test
	@DisplayName("unblock user Success")
	public void givenBlockedWhenUnBlockedThenSuccess() {
		Blocked blocked = new Blocked() ;
		blocked.setId(ID);
		doReturn(Optional.of(blocked)).when(repository).findById(anyString()) ;
		
		doNothing().when(repository).deleteById(anyString()) ;
		boolean result = blockedService.unblock(ID);
		assertThat(result).isTrue() ;
	}
	
	@Test
	@DisplayName("unblock missing user Success")
	public void givenMissingBlockedWhenUnBlockedThenSuccess() {
		doReturn(Optional.empty()).when(repository).findById(anyString()) ;
		
		doNothing().when(repository).deleteById(anyString()) ;
		boolean result = blockedService.unblock(ID);
		assertThat(result).isFalse() ;
	}
}
