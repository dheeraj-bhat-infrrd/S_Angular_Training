package com.realtech.socialsurvey;
//JIRA: SS-61: By RM03

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.realtech.socialsurvey.core.commons.InitializeJNDI;
 
public class TestingApp {
	

    @SuppressWarnings("resource")
    public static void main(String areg[]) throws Exception{
         
		InitializeJNDI.initializeJNDIforTest();
    	
        @SuppressWarnings("unused")
		ApplicationContext context = new ClassPathXmlApplicationContext("launch-context.xml");
         
    }
 
}