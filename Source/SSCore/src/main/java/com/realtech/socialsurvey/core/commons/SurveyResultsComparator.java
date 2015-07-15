package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.SurveyDetails;

/**
 * Compares AgentRankingReport based on their average score
 */
public class SurveyResultsComparator implements Comparator<SurveyDetails> {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyResultsComparator.class);

	@Override
	public int compare(SurveyDetails survey1, SurveyDetails survey2) {
		LOG.debug("Comparing SurveyDetails");

		if (survey1.getCreatedOn().compareTo(survey2.getCreatedOn()) > 0) {
			return -1;
		}
		else if (survey1.getCreatedOn().compareTo(survey2.getCreatedOn()) < 0) {
			return 1;
		}
		else {
			return 0;
		}
	}
}