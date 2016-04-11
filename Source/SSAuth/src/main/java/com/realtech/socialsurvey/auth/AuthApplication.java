package com.realtech.socialsurvey.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.auth.model.CustomUserDetails;

@SpringBootApplication
@RestController
@EnableResourceServer
@EnableJpaRepositories("com.realtech.socialsurvey.auth.repositories")
@EntityScan(basePackages = { "com.realtech.socialsurvey.auth" })
@EnableTransactionManagement
public class AuthApplication extends SpringBootServletInitializer {

	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/ss_user");

		return dataSource;
	}

	@Autowired
	@Qualifier("CustomUserDetailsService")
	private UserDetailsService userDetailService;

	@RequestMapping("/user")
	public CustomUserDetails user() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

		return currentUser;
	}

	@Bean
	public ShaPasswordEncoder passwordEncoder() {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(512);
		return encoder;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AuthApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
	}

	@Configuration
	protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		@Autowired
		private OAuth2RequestFactory requestFactory;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
			endpoints.authenticationManager(authenticationManager);
			endpoints.requestFactory(requestFactory);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService());
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.checkTokenAccess("isAuthenticated()");
		}

		@Bean
		public ClientDetailsService clientDetailsService() {

			Map<String, ClientDetails> clientDetailsStore = new HashMap<String, ClientDetails>();

			Collection<String> scope = new HashSet<String>();
			scope.add("CA");
			scope.add("RA");
			scope.add("BA");
			scope.add("AN");
			scope.add("SSA");
			scope.add("NP");

			Collection<String> authorizedGrantTypes = new HashSet<String>();
			authorizedGrantTypes.add("password");
			authorizedGrantTypes.add("refresh_token");

			BaseClientDetails clientDetails = new BaseClientDetails();
			clientDetails.setClientId("socialsurvey");
			clientDetails.setClientSecret("secret");
			clientDetails.setScope(scope);
			clientDetails.setAuthorizedGrantTypes(authorizedGrantTypes);

			clientDetailsStore.put("socialsurvey", clientDetails);

			InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService();
			clientDetailsService.setClientDetailsStore(clientDetailsStore);

			return clientDetailsService;
		}

		@Bean
		public OAuth2RequestFactory requestFactory() {
			DefaultOAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService());

			requestFactory.setCheckUserScopes(true);

			return requestFactory;
		}

		@Configuration
		protected static class CustomAuthServerSecConfig extends AuthorizationServerSecurityConfiguration {

			@Autowired
			private AuthenticationManager authenticationManager;

			@Autowired
			private OAuth2RequestFactory requestFactory;

			@Override
			protected void configure(HttpSecurity http) throws Exception {
				super.configure(http);

				http.addFilterAfter(new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory),
						BasicAuthenticationFilter.class);
			}
		}

		@Configuration
		@Order(-1)
		public static class CorsConfiguration extends WebSecurityConfigurerAdapter {

			@Override
			protected void configure(HttpSecurity http) throws Exception {

				http.requestMatchers().antMatchers(HttpMethod.OPTIONS, "/oauth/token", "/user").and().csrf().disable()
						.authorizeRequests().anyRequest().permitAll().and().sessionManagement()
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			}
		}
	}
}