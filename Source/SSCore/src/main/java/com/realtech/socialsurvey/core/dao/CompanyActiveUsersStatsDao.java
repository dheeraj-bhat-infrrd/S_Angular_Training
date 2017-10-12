package com.realtech.socialsurvey.core.dao;

import java.sql.Date;
import java.util.List;

import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;

/**
 * 
 * @author rohit
 *
 */
public interface CompanyActiveUsersStatsDao  extends GenericReportingDao<CompanyActiveUsersStats, String>
{

    List<CompanyActiveUsersStats> getActiveUsersCountStatsForCompanyForPastNDays( long companyId, Date startDate, Date endDate );

}
