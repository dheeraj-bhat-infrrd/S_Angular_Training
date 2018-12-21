package com.realtech.socialsurvey.core.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.MongoApplicationSettingsDao;
import com.realtech.socialsurvey.core.entities.ApplicationSettings;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ApplicationSettingsInstanceProvider
{
	@Autowired
	MongoApplicationSettingsDao mongoApplicationSettingsDao;

	private static ApplicationSettings applicationSettings;
	
	public ApplicationSettings getApplicationSettings()  
	  { 
	    if (applicationSettings == null)  
	    { 
	      // if instance is null, initialize 
		applicationSettings = mongoApplicationSettingsDao.getApplicationSettings();
	    } 
	    return applicationSettings; 
	  } 
}
