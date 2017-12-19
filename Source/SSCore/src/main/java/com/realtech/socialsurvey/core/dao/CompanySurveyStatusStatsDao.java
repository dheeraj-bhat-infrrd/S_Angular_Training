package com.realtech.socialsurvey.core.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;

public interface CompanySurveyStatusStatsDao extends GenericReportingDao<CompanySurveyStatusStats, String>
{

    public Map<Long, Long> getSentSurveyCountForCompaniesAfterSentDate( Date surveySentDate );

    public List<CompanySurveyStatusStats> getSurveyStatusCountForCompanyForPastNDays( long companyId, Date startDate,
        Date endDate );

    public List<CompanySurveyStatusStats> getOverallSurveyCountForPastNDays( Date startDate, Date endDate );

    public Map<Long , List<CompanySurveyStatusStats>> getSurveyStatusCountForCompaniesForPastNDays( List<Long> set, Date startDate,
        Date endDate );

    public Map<Long, Long> getTotalTransactionCountForCompaniesAfterSentDate( Date surveySentDate );

    Map<Long, List<CompanySurveyStatusStats>> getSurveyStatusCountForAllCompaniesForPastNDays( Date startDate, Date endDate );

    Map<Long, Long> getCompletedCountForCompaniesAfterSentDate( Date surveySentDate );

}
