package com.realtech.socialsurvey.api.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;


@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan ( basePackages = { "com.realtech.socialsurvey.api" })
@Import ( { SwaggerConfig.class, SecurityConfig.class })
@ImportResource ( { "classpath:sscore-beans.xml" })
public class ApiConfig extends WebMvcConfigurerAdapter
{

    @Bean
    public RestOperations restTemplate()
    {
        return new RestTemplate();
    }


    @Bean ( name = "dataSource")
    public JndiObjectFactoryBean dataSource()
    {
        JndiObjectFactoryBean dataSource = new JndiObjectFactoryBean();
        dataSource.setJndiName( "java:/env/datasources/ss_user" );
        return dataSource;
    }


    /*
     * Here we register the Hibernate4Module into an ObjectMapper, then set this
     * custom-configured ObjectMapper to the MessageConverter and return it to
     * be added to the HttpMessageConverters of our application
     */
    public MappingJackson2HttpMessageConverter jacksonMessageConverter()
    {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        ObjectMapper mapper = new ObjectMapper();
        // Registering Hibernate4Module to support lazy objects
        mapper.registerModule( new Hibernate4Module() );

        messageConverter.setObjectMapper( mapper );
        return messageConverter;

    }


    @Override
    public void configureMessageConverters( List<HttpMessageConverter<?>> converters )
    {
        // Here we add our custom-configured HttpMessageConverter
        converters.add( jacksonMessageConverter() );
        super.configureMessageConverters( converters );
    }
}