package com.realtech.socialsurvey.api.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@EnableSwagger
@Configuration
public class SwaggerConfig extends WebMvcConfigurerAdapter {

	private SpringSwaggerConfig springSwaggerConfig;

	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}

	@Bean
	public SwaggerSpringMvcPlugin customImplementation() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
			.apiInfo(apiInfo())
			.includePatterns(".*.*");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("Social Survey API", "This document describes the REST API and resources provided by Social Survey. "
				+ "The REST APIs are for developers who want to integrate Social Survey into their application and for administrators who want "
				+ "to script interactions with the Social Survey server. Social Survey's REST APIs provide access to resources (data entities) "
				+ "via URI paths. To use a REST API, your application will make an HTTP request and parse the response. "
				+ "The response format is JSON. Your methods will be the standard HTTP methods like GET, PUT, POST and DELETE."
				+ "Because the REST API is based on open standards, you can use any web development language to access the API.", "", "", "", "");
		return apiInfo;
	}
}