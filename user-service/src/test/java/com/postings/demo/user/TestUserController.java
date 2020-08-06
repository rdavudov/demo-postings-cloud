package com.postings.demo.user;

import static com.postings.demo.user.builder.TestUserBuilder.*;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postings.demo.user.dto.UserUpdateDto;
import com.postings.demo.user.model.User;
import com.postings.demo.user.service.UserService;

@SpringBootTest(properties ={"eureka.client.enabled=false", "spring.cloud.config.enabled=false"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class TestUserController {
	@Autowired
	private MockMvc mockMvc ;
	
	@MockBean
	private UserService userService ;

	@Test
	@DisplayName("GET /users/{id}> Success")
	public void testGetUserSuccess() throws Exception {
		User existingUser = testUserBuilder().id(ID).version(VERSION).build() ;
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		
		mockMvc.perform(get(getBaseUrl() + "/{id}", ID))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + existingUser.getId()))
			.andExpect(jsonPath("$.id", is(existingUser.getId())))
			.andExpect(jsonPath("$.username", is(existingUser.getUsername())))
			.andExpect(jsonPath("$.password", is(existingUser.getPassword())))
			.andExpect(jsonPath("$.firstName", is(existingUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(existingUser.getLastName())))
			.andExpect(jsonPath("$.email", is(existingUser.getEmail())))
			.andExpect(jsonPath("$.isBlocked", is(UNBLOCKED)))
			.andExpect(jsonPath("$.version", is(existingUser.getVersion())));
	}
	
	@Test
	@DisplayName("POST /users Success")
	public void testCreateUserSuccess() throws Exception {
		User user = testUserBuilder().build() ;
		User savedUser = testUserBuilder().id(ID).version(VERSION).build() ;
		
		doReturn(savedUser).when(userService).save(any()) ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(user.toString()))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + savedUser.getId()))
			.andExpect(jsonPath("$.id", org.hamcrest.CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.username", is(user.getUsername())))
			.andExpect(jsonPath("$.password", is(user.getPassword())))
			.andExpect(jsonPath("$.email", is(user.getEmail())))
			.andExpect(jsonPath("$.isBlocked", is(UNBLOCKED)))
			.andExpect(jsonPath("$.firstName", is(user.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(user.getLastName())))
			.andExpect(jsonPath("$.version", is(1)));
	}
	
	@Test
	@DisplayName("POST /users Fail with Exception")
	public void testCreateUserFailWithException() throws Exception {
		User user = testUserBuilder().build() ;
		
		doThrow(RuntimeException.class).when(userService).save(any()) ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(user.toString()))
			.andExpect(status().isInternalServerError());
	}
	
	@Test
	@DisplayName("POST /users Fail Missing Username")
	public void testCreateUserFailMissingUsername() throws Exception {
		Map<String, Object> params = new HashMap<>() ;
		params.put("email", EMAIL) ;
		params.put("password", PASSWORD) ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectAsJsonString(params)))
			.andExpect(status().isBadRequest());
	}
	
	
	
	@Test
	@DisplayName("POST /users Fail Existing Username")
	public void testCreateUserFailExitingUsername() throws Exception {
		doReturn(Optional.of(testUser())).when(userService).findByUsername(USERNAME) ;
		
		Map<String, Object> params = new HashMap<>() ;
		params.put("email", EMAIL) ;
		params.put("username", USERNAME) ;
		params.put("password", PASSWORD) ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectAsJsonString(params)))
			.andExpect(status().isBadRequest()) ;
	}
	
	@Test
	@DisplayName("POST /users Fail Missing Password")
	public void testCreateUserFailMissingPassword() throws Exception {
		Map<String, Object> params = new HashMap<>() ;
		params.put("email", EMAIL) ;
		params.put("username", USERNAME) ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectAsJsonString(params)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("POST /users Fail Missing Email")
	public void testCreateUserFailMissingEmail() throws Exception {
		Map<String, Object> params = new HashMap<>() ;
		params.put("username", USERNAME) ;
		params.put("password", PASSWORD) ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectAsJsonString(params)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("POST /users Fail Invalid Email")
	public void testCreateUserFailInvalidEmail() throws Exception {
		Map<String, Object> params = new HashMap<>() ;
		params.put("username", USERNAME) ;
		params.put("password", PASSWORD) ;
		params.put("email", "invalid") ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectAsJsonString(params)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("POST /users Fail Existing Email")
	public void testCreateUserFailExitingEmail() throws Exception {
		doReturn(Optional.of(testUser())).when(userService).findByEmail(EMAIL) ;
		
		Map<String, Object> params = new HashMap<>() ;
		params.put("email", EMAIL) ;
		params.put("username", USERNAME) ;
		params.put("password", PASSWORD) ;
		
		mockMvc.perform(post(getBaseUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectAsJsonString(params)))
			.andExpect(status().isBadRequest()) ;
	}
	
	@Test
	@DisplayName("GET /users?username=testuser Success")
	public void testFindUserByUsernameSuccess() throws Exception {
		doReturn(List.of(fullUser())).when(userService).find(org.mockito.Mockito.any()) ;
		
		mockMvc.perform(get(getBaseUrl() + "?username={username}", USERNAME))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(1)))
			.andExpect(jsonPath("$[0].id", is(ID)))
			.andExpect(jsonPath("$[0].username", is(USERNAME)))
			.andExpect(jsonPath("$[0].password", is(PASSWORD)))
			.andExpect(jsonPath("$[0].email", is(EMAIL)))
			.andExpect(jsonPath("$[0].isBlocked", is(UNBLOCKED)))
			.andExpect(jsonPath("$[0].firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$[0].lastName", is(LASTNAME)))
			.andExpect(jsonPath("$[0].version", is(VERSION)));
	}
	
	@Test
	@DisplayName("GET /users?username=testuser Not Found")
	public void testFindUserByUsernameNotFound() throws Exception {
		doReturn(List.of()).when(userService).find(org.mockito.Mockito.any()) ;
		
		mockMvc.perform(get(getBaseUrl() + "?username={username}", USERNAME))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(0))) ;
	}
	
	@Test
	@DisplayName("GET /users Success")
	public void testFindUsersSuccess() throws Exception {
		doReturn(List.of(fullUser(), fullUserBuilder().id(ID + "2").build())).when(userService).findAll() ;
		
		MvcResult result = mockMvc.perform(get(getBaseUrl()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()", is(2)))
			.andExpect(jsonPath("$[0].id", is(ID)))
			.andExpect(jsonPath("$[0].username", is(USERNAME)))
			.andExpect(jsonPath("$[0].password", is(PASSWORD)))
			.andExpect(jsonPath("$[0].email", is(EMAIL)))
			.andExpect(jsonPath("$[0].isBlocked", is(UNBLOCKED)))
			.andExpect(jsonPath("$[0].firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$[0].lastName", is(LASTNAME)))			
			.andExpect(jsonPath("$[0].version", is(VERSION)))
			.andExpect(jsonPath("$[1].id", is(ID + "2")))
			.andExpect(jsonPath("$[1].username", is(USERNAME)))
			.andExpect(jsonPath("$[1].password", is(PASSWORD)))
			.andExpect(jsonPath("$[1].email", is(EMAIL)))
			.andExpect(jsonPath("$[1].isBlocked", is(UNBLOCKED)))
			.andExpect(jsonPath("$[1].firstName", is(FIRSTNAME)))
			.andExpect(jsonPath("$[1].lastName", is(LASTNAME)))			
			.andExpect(jsonPath("$[1].version", is(VERSION)))			
			.andReturn();
	}
	
	@Test
	@DisplayName("PUT /users Success")
	public void testUpdateUserSuccess() throws Exception {
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setLastName(DTO_LASTNAME);
		User existingUser = testUserBuilder().id(ID).version(VERSION).build() ;
		User updatedUser = testUserBuilder().id(ID).version(VERSION + 1).lastname(DTO_LASTNAME).build() ;
		
		doReturn(Optional.of(existingUser)).when(userService).findById(ID) ;
		doReturn(Optional.of(updatedUser)).when(userService).update(ID, dto) ;
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(dto.toString()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			
			.andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
			.andExpect(header().string(HttpHeaders.LOCATION, getBaseUrl() + "/" + existingUser.getId()))
			.andExpect(jsonPath("$.id", org.hamcrest.CoreMatchers.any(String.class)))
			.andExpect(jsonPath("$.username", is(updatedUser.getUsername())))
			.andExpect(jsonPath("$.password", is(updatedUser.getPassword())))
			.andExpect(jsonPath("$.email", is(updatedUser.getEmail())))
			.andExpect(jsonPath("$.isBlocked", is(UNBLOCKED)))
			.andExpect(jsonPath("$.firstName", is(updatedUser.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(updatedUser.getLastName())))
			.andExpect(jsonPath("$.version", is(2)));
	}
	
	@Test
	@DisplayName("PUT /users Failed email validation")
	public void testUpdateUserFailedEmailValidation() throws Exception {
		UserUpdateDto dto = new UserUpdateDto() ;
		dto.setPassword("123");
		
		mockMvc.perform(put(getBaseUrl() + "/{id}", ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(dto.toString()))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("DELETE /users/{id}> Success")
	public void testDeleteUserSuccess() throws Exception {
		doReturn(Optional.of(fullUser())).when(userService).findById(ID) ;
		
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID))
			.andExpect(status().isNoContent()) ;
	}
	
	@Test
	@DisplayName("DELETE /users/{id}> Not Found")
	public void testDeleteUserNotFound() throws Exception {
		doReturn(Optional.empty()).when(userService).findById(ID) ;
		
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID))
			.andExpect(status().isNotFound()) ;
	}
	
	@Test
	@DisplayName("DELETE /users Fail with Exception")
	public void testDeleteUserFailWithException() throws Exception {
		doReturn(Optional.of(fullUser())).when(userService).findById(ID) ;
		doThrow(RuntimeException.class).when(userService).delete(ID) ;
		
		mockMvc.perform(delete(getBaseUrl() + "/{id}", ID))
			.andExpect(status().isInternalServerError()) ;
	}
	
	public static String objectAsJsonString(Object object) throws Exception {
		return new ObjectMapper().writeValueAsString(object) ;
	}
	
	public static String getBaseUrl() {
		return "/api/v1/users" ;
	}
}
