package com.realtech.socialsurvey.api.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;

@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class ApiLoggingProcessorThread extends Thread
{
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run()
    {
        System.out.println(getName() + " is started");
        try {
            System.out.println("found collection:" + mongoTemplate.collectionExists( CommonConstants.COMPANY_SETTINGS_COLLECTION ));
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getName() + " is finished");
    }

}