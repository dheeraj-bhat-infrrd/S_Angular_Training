package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.utils.MessageUtils;

@Controller
public class SurveyBuilderController {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyBuilderController.class);

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private SurveyBuilder surveyBuilder;

	@RequestMapping(value = "/createsurvey", method = RequestMethod.POST)
	public String createNewSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method addNewSurvey of SurveyBuilderController called");
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
		String surveyName = request.getParameter("surveyName");
		
		//surveyBuilder.createNewSurvey(user, surveyQuestions, surveyName);
		LOG.info("Method addNewSurvey of SurveyBuilderController finished successfully");
		return null;
	}

	@RequestMapping(value = "/addsurveytocompany", method = RequestMethod.POST)
	public String addSurveyToCompany(Model model, HttpServletRequest request) {
		LOG.info("Method addNewSurvey of SurveyBuilderController called");
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
		
		LOG.info("Method addNewSurvey of SurveyBuilderController finished successfully");
		return null;
	}

	@RequestMapping(value = "/addquestionstosurvey", method = RequestMethod.POST)
	public String addQuestionsToExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method addNewSurvey of SurveyBuilderController called");
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);
		
		LOG.info("Method addNewSurvey of SurveyBuilderController finished successfully");
		return null;
	}
}