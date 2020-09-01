package com.postings.demo.client.annotation;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

public interface OAuth2Test {
	ClientRegistrationRepository getClientRegistrationRepository() ;
	
	OAuth2AuthorizedClientRepository getOAuth2AuthorizedClientRepository() ;
	
	void setSession(MockHttpSession session) ;
}
