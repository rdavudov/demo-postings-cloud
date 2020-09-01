package com.postings.demo.client;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ClientControllerTests2 {
	
	@Autowired
	private MockMvc mockMvc ;
	
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository ;
	
	@Rule
	public WireMockRule mockOAuth2Provider = new WireMockRule(wireMockConfig()
	  .port(8077)
	  .extensions(new ResponseTemplateTransformer(true)));
	
	public void setUp() {
		mockOAuth2Provider.stubFor(WireMock.get(urlPathMatching("/oauth/authorize?.*"))
		        .willReturn(aResponse()
		            .withStatus(200)
		            .withHeader("Content-Type", "text/html")
		            .withBodyFile("login.html")));
		
		mockOAuth2Provider.stubFor(WireMock.post(WireMock.urlPathEqualTo("/login"))
			    .willReturn(WireMock.temporaryRedirect("{{formData request.body 'form' urlDecode=true}}{{{form.redirectUri}}}?code={{{randomValue length=30 type='ALPHANUMERIC'}}}&state={{{form.state}}}")));
		
		mockOAuth2Provider.stubFor(WireMock.post(WireMock.urlPathEqualTo("/oauth/token"))
			    .willReturn(WireMock.okJson("{\"token_type\": \"Bearer\",\"access_token\":\"{{randomValue length=20 type='ALPHANUMERIC'}}\"}")));
		
		mockOAuth2Provider.stubFor(WireMock.get(WireMock.urlPathEqualTo("/userinfo"))
			    .willReturn(WireMock.okJson("{\"sub\":\"my-id\",\"email\":\"bwatkins@test.com\"}")));
	}
	
	@Test
	public void givenOAuthWhenLoginThenSuccess() throws Exception {
		MvcResult result = mockMvc
			.perform(get("/").with(oidcLogin().clientRegistration(clientRegistrationRepository.findByRegistrationId("wiremock"))))
			.andExpect(status().isFound()).andReturn() ;
		
		System.out.println(result);
	}
}
