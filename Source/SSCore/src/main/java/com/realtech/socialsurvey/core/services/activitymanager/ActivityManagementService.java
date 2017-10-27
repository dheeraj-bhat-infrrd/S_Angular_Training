package com.realtech.socialsurvey.core.services.activitymanager;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.CompanyTransactionsSourceStats;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * 
 * @author rohit
 *
 */
public interface ActivityManagementService
{

    /**
     * 
     * @return
     */
    public List<CompanySurveyStatusStats> getSurveyStatusStatsForPastDay();

    /**
     * 
     * @param companySurveyStatusStatsList
     */
    public List<Long> validateSurveyStatsForCompanies( List<CompanySurveyStatusStats> companySurveyStatusStatsList );

    /**
     * 
     * @param companyIds
     */
    public void sendHighNotProcessedTransactionAlertMailForCompanies( List<Long> companyIdsToSendAlert, List<Company> allActiveCompanies );

    
    /**
     * 
     * @param companyId
     * @param noOfDays
     * @return
     */
    public List<Company> getCompaniesWithNoTransactionInPastNDays(List<Company> companies, int noOfDays );

    /**
     * 
     * @param companies
     */
    public void sendNoTransactionAlertMailForCompanies( List<Company> companies, int noOfDays );

    /**
     * 
     * @return
     */
    public Map<Long, Long> getSurveyStatusStatsForPastOneMonth();

    /**
     * 
     * @param allCompanies
     * @param companyActiveUserCounts
     * @param companySurveyStatsCountsMap
     */
    public void validateAndSentLessSurveysAlert( List<CompanyActiveUsersStats> companyActiveUserCounts, Map<Long, Long> companySurveyStatsCountsMap );

    /**
     * 
     * @param companyId
     * @param noOfDays
     * @return
     * @throws InvalidInputException
     */
    public List<CompanyTransactionsSourceStats> getTransactionsCountForCompanyForPastNDays( long companyId, int noOfDays )
        throws InvalidInputException;

    /**
     * 
     * @param companyId
     * @param noOfDays
     * @return
     * @throws InvalidInputException
     */
    public List<CompanySurveyStatusStats> getSurveyStatusStatsForCompanyForPastNDays( long companyId, int noOfDays )
        throws InvalidInputException;

    /**
     * 
     * @param companyId
     * @param noOfDays
     * @return
     * @throws InvalidInputException
     */
    public List<CompanyActiveUsersStats> getActiveUserCountStatsForCompanyForPastNDays( long companyId, int noOfDays )
        throws InvalidInputException;

    /**
     * 
     * @return
     */
    public List<CompanyActiveUsersStats> getCompanyActiveUserCountForPastDay();


}
