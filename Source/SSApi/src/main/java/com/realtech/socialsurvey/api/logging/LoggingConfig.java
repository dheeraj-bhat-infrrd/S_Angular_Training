package com.realtech.socialsurvey.api.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = "com.realtech.socialsurvey.web.logging")
@EnableWebMvc
public class LoggingConfig extends WebMvcConfigurerAdapter
{
    
    @Autowired
    private ApiTransactionInterceptor apiTransactionInterceptor;
    
    @Bean (name = "loggingTaskExecutor")
    public ThreadPoolTaskExecutor loggingTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(5);
        pool.setMaxPoolSize(10);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiTransactionInterceptor).addPathPatterns("/v[0-9]/*");
    }

}