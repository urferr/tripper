package com.embabel.agent.web.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest theUserRequest) throws OAuth2AuthenticationException {
	var aUser = super.loadUser(theUserRequest);

	// Extract user attributes
	var attributes = aUser.getAttributes();

	// Create authorities (roles)
	var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

	// Return custom OAuth2User
	return new DefaultOAuth2User(authorities, attributes, "name" // The attribute that contains the user's name
	);
    }

}
