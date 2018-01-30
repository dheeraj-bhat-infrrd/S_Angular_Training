/**
 * 
 */
package com.realtech.socialsurvey.core.services.migration.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.services.migration.DataMigrationService;

/**
 * @author Subhrajit
 *
 */
@Service
public class DataMigrationServiceImpl implements DataMigrationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataMigrationServiceImpl.class);

	@Autowired
	private SurveyDetailsDao surveyDetailsDao;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.realtech.socialsurvey.core.services.migration.DataMigrationService#
	 * migrateMCQtoNPSMongo(long, java.lang.String)
	 */
	@Override
	@Transactional
	public void migrateMCQtoNPSMongo(long companyId, int questionId, String question) throws IOException {
		List<SurveyDetails> surveyDetailsList = surveyDetailsDao.getSurveyDetailsForCompanyAndQuestion(companyId,
				question);
		LOGGER.info("No. of surveys for the question {} in company with Id {} is : {}", question, companyId,
				surveyDetailsList.size());

		for (SurveyDetails surveyDetails : surveyDetailsList) {

			List<SurveyResponse> surveyResponseList = surveyDetails.getSurveyResponse();

			if (surveyResponseList != null && surveyResponseList.size() > 0) {
				LOGGER.info("survey details id : {}", surveyDetailsList.get(0).get_id());
				for (SurveyResponse surveyResponse : surveyResponseList) {
					if (surveyResponse.getQuestion().equals(question)) {
						String answer = surveyResponse.getAnswer();
						if(answer.trim().startsWith("10")){
							surveyResponse.setAnswer("10");
						} else if(answer.trim().startsWith("0")){
							surveyResponse.setAnswer("0");
						}
						surveyResponse.setConsiderForScore(false);
						surveyResponse.setIsNpsQuestion(true);
						surveyResponse.setIsUserRankingQuestion(true);
						surveyResponse.setQuestionId(questionId);
						surveyResponse.setQuestionType("sb-range-0to10");
						surveyDetailsDao.updateCustomerResponse(surveyDetails.get_id(), surveyResponse);
						surveyDetails.setNpsScore(Integer.parseInt(surveyResponse.getAnswer()));
						surveyDetailsDao.updateSurveyNPSScore(surveyDetails);
					}
				}

			}
		}
	}

}
