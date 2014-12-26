package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.utils.MessageUtils;

@Controller
public class SurveyBuilderController {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyBuilderController.class);

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private SurveyBuilder surveyBuilder;

	@RequestMapping(value = "/corporateinvite", method = RequestMethod.POST)
	public String addNewSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method addNewSurvey of SurveyBuilderController called");

		LOG.info("Method addNewSurvey of SurveyBuilderController finished successfully");
		return null;
	}
}