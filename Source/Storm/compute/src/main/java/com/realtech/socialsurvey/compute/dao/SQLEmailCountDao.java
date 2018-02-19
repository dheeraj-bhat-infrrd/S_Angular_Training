/**
 * 
 */
package com.realtech.socialsurvey.compute.dao;

import java.sql.SQLException;

import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;

/**
 * @author Subhrajit
 *
 */
public interface SQLEmailCountDao {
	
	public int getReceivedCount(String startDate,String endDate) throws SQLException;

	public void save(SurveyInvitationEmailCountMonth emailCountMonth) throws SQLException;

}
