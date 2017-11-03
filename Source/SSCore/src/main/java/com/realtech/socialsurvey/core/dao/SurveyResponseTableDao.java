package com.realtech.socialsurvey.core.dao;

import java.sql.Timestamp;

import com.realtech.socialsurvey.core.entities.SurveyResponseTable;


/**
 * @author sandra
 *
 */
public interface SurveyResponseTableDao extends GenericReportingDao<SurveyResponseTable, String>{

     /**
     * @param companyId
     * @param startDate
     * @param endDate
     * @return
     */
    public int getMaxResponseForCompanyId( long companyId, Timestamp startDate, Timestamp endDate );

	/**
	 * @param regionId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getMaxResponseForRegionId(Long regionId, Timestamp startDate, Timestamp endDate);

	/**
	 * @param branchId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getMaxResponseForBranchId(Long branchId, Timestamp startDate, Timestamp endDate);

	/**
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getMaxResponseForUserId(Long userId, Timestamp startDate, Timestamp endDate);

}
