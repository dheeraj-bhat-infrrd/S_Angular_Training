package com.realtech.socialsurvey.core.dummy.generator;

import java.util.ArrayList;
import java.util.Arrays;
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
	public List<User> getAgents(String[] emails) {
		
		ArrayList<String> emailIds = new ArrayList<String>(Arrays.asList(emails));
		return userDao.fetchUsersByEmailId(emailIds);
	}
	
}
