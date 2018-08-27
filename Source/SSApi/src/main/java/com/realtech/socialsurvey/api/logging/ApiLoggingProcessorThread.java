package com.realtech.socialsurvey.api.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.controllers.SurveyApiController;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.ApiRequestDetails;

@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class ApiLoggingProcessorThread extends Thread
{
	private static final Logger LOG = LoggerFactory.getLogger( ApiLoggingProcessorThread.class );

    
    @Autowired
    private SurveyDetailsDao surveyDetailsDao;
    
    private ApiRequestDetails apiRequestDetails; 

    public ApiRequestDetails getApiRequestDetails() {
		return apiRequestDetails;
	}

	public void setApiRequestDetails(ApiRequestDetails apiRequestDetails) {
		this.apiRequestDetails = apiRequestDetails;
	}

	@Override
    public void run()
    {
        try {
            surveyDetailsDao.insertApiRequestDetails(apiRequestDetails);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        	LOG.error("Thread interupted " , e);
        }
    }

}