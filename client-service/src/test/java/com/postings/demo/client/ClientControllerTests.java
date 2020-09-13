package com.postings.demo.client;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.postings.demo.client.annotation.OAuth2MockUser;
import com.postings.demo.client.extension.OAuth2Extension;
import com.postings.demo.client.extension.OAuth2Test;
import com.postings.demo.client.service.UserService;

import lombok.Getter;
import lombok.Setter;

@SpringBootTest
@ExtendWith({SpringExtension.class, OAuth2Extension.class})
@AutoConfigureMockMvc
public class ClientControllerTests implements OAuth2Test {
	
	@Autowired
	private MockMvc mockMvc ;
	
	@Autowired
	@Getter
	private ClientRegistrationRepository clientRegistrationRepository ;
	
	@Autowired
	@Getter
	private OAuth2AuthorizedClientRepository oauth2AuthorizedClientRepository ;
	
	@MockBean
	private UserService userService ;
	
	@Getter
	@Setter
	private MockHttpSession session ;
	
	@Test
	@OAuth2MockUser(principal = "id", 
		claims = {"given_name=Test", "family_name=Testov", "email=test@test.com", "email_verified=true", "picture=http://mypic.com"})
	public void givenOAuthWhenLoginThenSuccess() throws Exception {
		
		mockMvc
			.perform(get("/").session(getSession()))
			.andExpect(status().isOk()) ;
	}
	
	@Test
	@OAuth2MockUser(principal = "id", 
		claims = {"given_name=Test", "family_name=Testov", "email=test@test.com", "email_verified=true", "picture=http://mypic.com"},
		authorities = {"ROLE_BLOCKED"})
	public void givenBlockedUserWhenHomeThenRedirected() throws Exception {
		
		mockMvc
			.perform(get("/").session(getSession()))
			.andExpect(status().isForbidden())
			.andExpect(forwardedUrl("/blocked"));
	}
	
}
