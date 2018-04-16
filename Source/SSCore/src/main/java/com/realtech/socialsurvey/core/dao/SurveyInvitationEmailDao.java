/**
 * 
 */
package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyInvitationEmailCountMonth;

/**
 * @author Subhrajit
 *
 */
public interface SurveyInvitationEmailDao extends GenericReportingDao<SurveyInvitationEmailCountMonth, Long> {

	List<Object[]> getSurveyInvitationEmailReportForMonth(long companyId, int month, int year);
	List<Object[]> getSurveyInvitationEmailReportForAllTime(long companyId, int month, int year);
	public void deleteOldDataForMonth(int month, int year);

	

}
