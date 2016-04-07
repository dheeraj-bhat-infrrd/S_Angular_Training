package com.realtech.socialsurvey.api.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.realtech.socialsurvey.api" })
@Import({ SwaggerConfig.class, SecurityConfig.class })
public class ApiConfig {

}