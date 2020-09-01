package com.postings.demo.client;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.postings.demo.client.annotation.OAuth2Extension;
import com.postings.demo.client.annotation.OAuth2MockUser;
import com.postings.demo.client.annotation.OAuth2Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith({SpringExtension.class, OAuth2Extension.class})
@AutoConfigureMockMvc
public class ClientControllerTests implements OAuth2Test {
	
	@Autowired
	private MockMvc mockMvc ;
	
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository ;
	
	@Autowired
	private OAuth2AuthorizedClientRepository clientRepository ;
	
	private MockHttpSession session ;
	
//	@Test
//	@OAuth2MockUser(principal = "id", claims = {"given_name=Test", "family_name=Testov", "email=test@test.com", "email_verified=true", "picture=http://mypic.com"})
	public void givenOAuthWhenLoginThenSuccess() throws Exception {
		mockMvc
			.perform(get("/").session(session))
			.andExpect(status().isOk()) ;
	}
	
	@Test
	public void givenOAuthWhenLoginThenSuccess2() throws Exception {
		mockMvc
			.perform(get("/login/oauth2/code/").with(oidcLogin()))
			.andExpect(status().isOk()) ;
	}
	
	
	@Override
	public void setSession(MockHttpSession session) {
		this.session = session ;
	}

	@Override
	public ClientRegistrationRepository getClientRegistrationRepository() {
		return clientRegistrationRepository;
	}

	@Override
	public OAuth2AuthorizedClientRepository getOAuth2AuthorizedClientRepository() {
		return clientRepository;
	}
}
