package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.ID;
import static com.postings.demo.user.builder.TestUserBuilder.testUserBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.postings.demo.user.model.Blocked;
import com.postings.demo.user.model.User;
import com.postings.demo.user.service.BlockedService;
import com.postings.demo.user.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TestBlockController {
	
	@Value("${service.base.uri:/api/v1}/block")
	private String baseUri ;

	@Autowired
	private MockMvc mockMvc ; 
	
	@MockBean
	private BlockedService blockedService ;
	
	@MockBean
	private UserService userService ;
	
	@Test
	@DisplayName("blocking user test")
	public void givenUserWhenBlockThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(true).when(blockedService).block(any(Blocked.class));
		
		mockMvc.perform(post(baseUri + "/{id}", ID))
			.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("blocking not existed user test")
	public void givenMissingUserWhenBlockThenNotFound() throws Exception {
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		
		mockMvc.perform(post(baseUri + "/{id}", ID))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("unblocking user test")
	public void givenUserWhenUnblockThenSuccess() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(true).when(blockedService).unblock(anyString());
		
		mockMvc.perform(delete(baseUri + "/{id}", ID))
			.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("unblocking not existed user test")
	public void givenMissingUserWhenUnblockThenNotFound() throws Exception {
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		
		mockMvc.perform(delete(baseUri + "/{id}", ID))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("unblocking not existed blocking test")
	public void givenMissingBlockingWhenUnblockThenNotFound() throws Exception {
		User user = testUserBuilder().id(ID).build() ;
		
		doReturn(Optional.of(user)).when(userService).findById(ID) ;
		doReturn(false).when(blockedService).unblock(anyString());
		
		mockMvc.perform(delete(baseUri + "/{id}", ID))
			.andExpect(status().isNotFound());
	}
}
