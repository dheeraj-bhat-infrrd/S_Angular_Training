package com.realtech.socialsurvey.api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import com.realtech.socialsurvey.api.web.SimpleCORSFilter;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "socialsecurity";

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.addFilterBefore(new SimpleCORSFilter(), ChannelProcessingFilter.class).authorizeRequests()
				.antMatchers("/**").permitAll();
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID);
		resources.tokenServices(remoteTokenServices());
	}

	@Bean
	RemoteTokenServices remoteTokenServices() {
		RemoteTokenServices rts = new RemoteTokenServices();
		rts.setClientId(RESOURCE_ID);
		rts.setClientSecret("secret");
		rts.setCheckTokenEndpointUrl("http://localhost:8082" + "/oauth/check_token");

		return rts;
	}
}