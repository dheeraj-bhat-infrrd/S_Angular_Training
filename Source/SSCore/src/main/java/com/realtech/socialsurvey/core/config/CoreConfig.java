package com.realtech.socialsurvey.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class CoreConfig {
	
	@Bean( name = "rebrandlyRestTemplate" )
    public RestOperations rebrandlyRestTemplate()
    {
		RestTemplate rebrandlyRestTemplate = new RestTemplate();		
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = new ObjectMapper().configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
		converter.setObjectMapper( objectMapper );
		rebrandlyRestTemplate.getMessageConverters().add(converter);
        return rebrandlyRestTemplate;
    }
}
