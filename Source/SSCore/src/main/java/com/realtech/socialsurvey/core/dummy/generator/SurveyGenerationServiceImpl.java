package com.realtech.socialsurvey.core.dummy.generator;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.User;

@Component
public class SurveyGenerationServiceImpl implements SurveyGenerationService{

	@Autowired
	private UserDao userDao;
	
	@Override
	@Transactional
	public List<User> getAgents(List<String> emailIds) {
		
		return userDao.fetchUsersByEmailId(emailIds);
	}
	
}
