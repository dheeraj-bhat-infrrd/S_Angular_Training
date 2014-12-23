package com.realtech.socialsurvey;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
 
public class TestingApp {
	

    @SuppressWarnings("resource")
    public static void main(String areg[]){
         
        @SuppressWarnings("unused")
		ApplicationContext context = new ClassPathXmlApplicationContext("launch-context.xml");
         
    }
 
}