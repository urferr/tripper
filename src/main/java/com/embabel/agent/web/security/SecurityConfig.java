package com.embabel.agent.web.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @ConfigurationProperties(prefix = "embabel.security")
    private static class SecurityProperties {
	private boolean enabled = true;

	public boolean isEnabled() {
	    return enabled;
	}

	public void setEnabled(boolean theEnabled) {
	    enabled = theEnabled;
	}
    }

    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomOAuth2UserService customOAuth2UserService;
    private final SecurityProperties securityProperties;

    public SecurityConfig(CustomOAuth2UserService theCustomOAuth2UserService,
	    SecurityProperties theSecurityProperties) {
	customOAuth2UserService = theCustomOAuth2UserService;
	securityProperties = theSecurityProperties;
    }

    private SecurityFilterChain unsecuredFilterChain(HttpSecurity theHttp) throws Exception {
	theHttp.authorizeHttpRequests(theRequest -> theRequest.anyRequest().permitAll())
		.csrf(AbstractHttpConfigurer::disable);
	return theHttp.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity theHttp) throws Exception {
	if (!securityProperties.isEnabled()) {
	    logger.warn("Security is disabled in configuration. This is not recommended for production environments.");
	    return unsecuredFilterChain(theHttp);
	}
	theHttp.csrf(AbstractHttpConfigurer::disable) // For simplicity in demo applications
		.authorizeHttpRequests(theRequest -> theRequest
			.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/login").permitAll()
			.anyRequest().authenticated())
		.oauth2Login(oauth2 -> oauth2.loginPage("/login").defaultSuccessUrl("/", true)
			.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
		.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());
	return theHttp.build();
    }
}
