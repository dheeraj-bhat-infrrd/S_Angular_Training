package com.realtech.socialsurvey.core.commons;

import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;

/**
 * Compares AgentRankingReport based on their average score
 */
public class AgentRankingReportComparator implements Comparator<AgentRankingReport> {

	private static final Logger LOG = LoggerFactory.getLogger(AgentRankingReportComparator.class);

	@Override
	public int compare(AgentRankingReport agentReport1, AgentRankingReport agentReport2) {
		LOG.debug("Comparing AgentRankingReports");

		if (agentReport1.getCompletedSurveys() > agentReport2.getCompletedSurveys()) {
			return -1;
		}
		else if (agentReport1.getCompletedSurveys() < agentReport2.getCompletedSurveys()) {
			return 1;
		}
		else {
			return 0;
		}
	}
}