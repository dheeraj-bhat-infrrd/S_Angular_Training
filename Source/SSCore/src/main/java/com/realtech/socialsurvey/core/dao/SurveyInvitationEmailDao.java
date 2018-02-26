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

	List<SurveyInvitationEmailCountMonth> getSurveyInvitationEmailReportForMonth(long companyId, int month, int year);

}
