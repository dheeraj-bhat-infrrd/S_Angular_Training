package com.realtech.socialsurvey.core.services.activitymanager;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.CompanyTransactionsSourceStats;
import com.realtech.socialsurvey.core.entities.CompanyView;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.TransactionMonitorGraphDataVO;


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
     * @param companyId
     * @param noOfDays
     * @return
     */
    public List<CompanyView> getCompaniesWithNoTransactionInPastNDays( List<CompanyView> allCompanies, int noOfDays );


    /**
     * 
     * @return
     */
    public Map<Long, Long> getSurveyStatusStatsForPastOneMonth();


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

    /**
     * 
     * @return List of String
     */
    public List<String> getTransactionMonitorMailList();
    
    /**
     * 
     * @param noOfDays
     * @param alertType
     * @return
     * @throws InvalidInputException
     */
    public List<TransactionMonitorGraphDataVO> getTransactionsStatsByDaysAndAlertType( int noOfDays, String alertType )
        throws InvalidInputException;


    /**
     * 
     * @param noOfDays
     * @return
     */
    public Map<Long, Long> getTotalTransactionCountForPast3DaysForCompanies();


    public Map<Long, Long> getTransactionCountForPreviousDay();


    public Map<Long, Long> getSendSurveyCountForPreviousDay();


    public Map<Long, Long> getSendSurveyCountForPast7Days();


    Map<Long, List<CompanySurveyStatusStats>> getSurveStatsForPast7daysForAllCompanies();


    Map<Long, List<CompanySurveyStatusStats>> getSurveStatsForLastToLatWeekForAllCompanies();

}
