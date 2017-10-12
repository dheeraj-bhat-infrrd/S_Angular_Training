package com.realtech.socialsurvey.core.services.activitymanager.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.CompanyActiveUsersStatsDao;
import com.realtech.socialsurvey.core.dao.CompanySurveyStatusStatsDao;
import com.realtech.socialsurvey.core.dao.CompanyTransactionsSourceStatsDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.CompanyTransactionsSourceStats;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.activitymanager.ActivityManagementService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

/**
 * 
 * @author rohit
 *
 */
@Service
public class ActivityManagementServiceImpl implements ActivityManagementService
{
    private static final Logger LOG = LoggerFactory.getLogger( ActivityManagementServiceImpl.class );

    
    @Autowired
    private CompanySurveyStatusStatsDao companySurveyStatusStatsDao;
    
    
    @Autowired
    private CompanyTransactionsSourceStatsDao companyTransactionsSourceStatsDao;
    
    @Autowired
    private CompanyActiveUsersStatsDao companyActiveUsersStatsDao;
    
    @Autowired
    private EmailServices emailServices;
    
    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Autowired
    private Utils utils;
    
    @Value ( "${APPLICATION_SUPPORT_EMAIL}")
    private String applicationSupportEmail;
    
    /**
     * 
     */
    @Override
    public void sendHighNotProcessedTransactionAlertMailForCompanies(List<Long> companyIds)
    {
        
        LOG.debug( "method sendHighNotProcessedTransactionAlertMailForCompanies started" );

        List<Company> companies = organizationManagementService.getCompaniesByCompanyIds( new HashSet<Long>(companyIds));
        String mailBody = "";
        
        int i = 0;
        for(Company company :  companies){
            mailBody +=  (i + ". " + company.getCompanyId() + " " + company.getCompany() + " . SocialSurvey has not received any transaction details since " );             
            mailBody += "</br>";
        }
        
        
        try {
            emailServices.sendHighVoulmeUnprocessedTransactionAlertMail( applicationSupportEmail, mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
           LOG.error( "Error while sending highNotProcessed aler email " ); e.printStackTrace();
        }
        LOG.debug( "method sendHighNotProcessedTransactionAlertMailForCompanies ended" );
    }
    
    /**
     * 
     * @param companyId
     * @param noOfDays
     * @return
     */
    @Override
    public List<Company> getCompaniesWithNoTransactionInPastNDays(List<Company> companies , int noOfDays){
        LOG.debug( "method getCompaniesWithNoTransactionInPastNDays started" );
        
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -(noOfDays) ); // subtract the no of days
        Date nDaysBackDate = new Date( calendar.getTimeInMillis() );
        
        Map<Long, Long> companyTransactionCountsMap  = companyTransactionsSourceStatsDao.getTransactionsByCompanyIdAndAfterTransactionDate( nDaysBackDate );
        List<Company> companyWithNoTransactions = new ArrayList<Company>();
        for(Company company : companies){
            if(companyTransactionCountsMap.get( company.getCompanyId() ) == null || companyTransactionCountsMap.get( company.getCompanyId() ) == 0l)
                companyWithNoTransactions.add( company );
        }
        
        LOG.debug( "method getCompaniesWithNoTransactionInPastNDays ended" );
        return companyWithNoTransactions;
    }
    
    /**
     * 
     * @param companies
     */
    @Override
    public void sendNoTransactionAlertMailForCompanies(List<Company> companies)
    {
        
        LOG.debug( "method sendNoTransactionAlertMailForCompanies started" );
        String mailBody = "";
        
        int i = 0;
        for(Company company :  companies){
            i++;
            mailBody +=  (i + ". " + company.getCompanyId() + " " + company.getCompany() + " . SocialSurvey has not received any transaction details since " );             
            mailBody += "</br>";
        }
        
        
        try {
            emailServices.sendNoTransactionAlertMail( applicationSupportEmail, mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
           LOG.error( "Error while sending highNotProcessed aler email " ); e.printStackTrace();
        }
        LOG.debug( "method sendNoTransactionAlertMailForCompanies ended" );
    }
    
    /**
     * 
     */
    @Override
    public List<CompanySurveyStatusStats> getSurveyStatusStatsForPastDay()
    {
        LOG.debug( "method getSurveyStatusStatsForPastDay started" );
        
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -1 );
        Date yesterDayDate = new Date( calendar.getTimeInMillis() );
        
        List<CompanySurveyStatusStats> companySurveyStatusStatsList = companySurveyStatusStatsDao.findByColumn(CompanySurveyStatusStats.class, "statsDate", yesterDayDate );
        
        LOG.debug( "method getSurveyStatusStatsForPastDay ended" );
        return companySurveyStatusStatsList;
    }
    
    /**
     * 
     * @param companySurveyStatusStatsList
     */
    @Override
    public List<Long> validateSurveyStatsForCompanies(List<CompanySurveyStatusStats> companySurveyStatusStatsList)
    {
        
        LOG.debug( "method validateSurveyStatsForCompanies started" );
        List<Long> companyIds = new ArrayList<Long>();
        for(CompanySurveyStatusStats companySurveyStatusStats :  companySurveyStatusStatsList){
            int transactionReceivedCount = companySurveyStatusStats.getTransactionReceivedCount();
            int surveyInvitationCount = companySurveyStatusStats.getSurveyInvitationSentCount();
            if(transactionReceivedCount > 0 && surveyInvitationCount > 0){
                if(surveyInvitationCount < (transactionReceivedCount/2)){
                    companyIds.add( companySurveyStatusStats.getCompanyId() );
                }
            }                     
        }
        
        LOG.debug( "method validateSurveyStatsForCompanies ended" );
        return companyIds;
    }

    /**
     * 
     */
    @Override
    public Map<Long, Long> getSurveyStatusStatsForPastOneMonth()
    {
        LOG.debug( "method getSurveyStatusStatsForPastOneMonth started" );
        
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -30 );
        Date oneMonthBackDate = new Date( calendar.getTimeInMillis() );
        
        Map<Long, Long> companySurveyStatsCountsMap  = companySurveyStatusStatsDao.getSentSurveyCountForCompaniesAfterSentDate( oneMonthBackDate );
        
        LOG.debug( "method getSurveyStatusStatsForPastOneMonth ended" );
        return companySurveyStatsCountsMap;
    }

    @Override
    public void validateAndSentLessSurveysAlert( List<Company> allCompanies, Map<Long, Long> companyActiveUserCounts,
        Map<Long, Long> companySurveyStatsCountsMap )
    {
        LOG.debug( "method validateAndSentLessSurveysAlert started" );
        String mailBody = "";

        int i = 0;
        for(Company company : allCompanies){
            Long surveyCount = companySurveyStatsCountsMap.get( company.getCompanyId() );
            Long userCount = companyActiveUserCounts.get( company.getCompanyId() );
            if(surveyCount != null && userCount!= null &&  (surveyCount/2) < userCount){
                i++;
                mailBody +=  (i + ". " + company.getCompanyId() + " " + company.getCompany() + "  sent us  " + surveyCount + " transactions for total of " + userCount + " Users." );             
                mailBody += "</br>";
            }
        }
        
        try {
            emailServices.sendLessVoulmeOfTransactionReceivedAlertMail( applicationSupportEmail, mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            e.printStackTrace();
        }
        LOG.debug( "method validateAndSentLessSurveysAlert ended" ); 
    }
    
    /**
     * 
     * @param companyId
     * @param noOfDays
     * @throws InvalidInputException 
     */
    @Override
    @Transactional
    public List<CompanyTransactionsSourceStats> getTransactionsCountForCompanyForPastNDays(long companyId, int noOfDays) throws InvalidInputException
    {
        
        LOG.debug( "method getTransactionsCountForCompanyForPastNDays started for companyId %s and noOfDays %s" , companyId , noOfDays );
        
        if(companyId <= 0l){
            LOG.error("Invalid companyId is passed : %s " + companyId );
            throw new InvalidInputException("Passed parameter companyId is invalid ");
        }
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanyTransactionsSourceStats> companyTransactionsStats = companyTransactionsSourceStatsDao.getTransactionsCountForCompanyForPastNDays( companyId, startDate, endDate );

        LOG.debug( "method getTransactionsCountForCompanyForPastNDays finished for companyId %s and noOfDays %s" , companyId , noOfDays );
        return companyTransactionsStats;
    }
    
    /**
     * 
     * @param companyId
     * @param noOfDays
     * @return
     * @throws InvalidInputException
     */
    @Override
    @Transactional
    public List<CompanySurveyStatusStats> getSurveyStatusStatsForCompanyForPastNDays(long companyId, int noOfDays) throws InvalidInputException
    {
        
        LOG.debug( "method getSurveyStatusStatsForCompanyForPastNDays started for companyId %s and noOfDays %s" , companyId , noOfDays );
        
        if(companyId <= 0l){
            LOG.error("Invalid companyId is passed : %s " + companyId );
            throw new InvalidInputException("Passed parameter companyId is invalid ");
        }
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanySurveyStatusStats> companySurveyStats = companySurveyStatusStatsDao.getSurveyStatusCountForCompanyForPastNDays( companyId, startDate, endDate );

        LOG.debug( "method getSurveyStatusStatsForCompanyForPastNDays finished for companyId %s and noOfDays %s" , companyId , noOfDays );
        return companySurveyStats;
    }
    
    
    /**
     * 
     */
    @Override
    @Transactional
    public List<CompanyActiveUsersStats> getActiveUserCountStatsForCompanyForPastNDays(long companyId, int noOfDays) throws InvalidInputException
    {
        
        LOG.debug( "method getActiveUserCountStatsForCompanyForPastNDays started for companyId %s and noOfDays %s" , companyId , noOfDays );
        
        if(companyId <= 0l){
            LOG.error("Invalid companyId is passed : %s " + companyId );
            throw new InvalidInputException("Passed parameter companyId is invalid ");
        }
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanyActiveUsersStats> companyActiveUsersStats = companyActiveUsersStatsDao.getActiveUsersCountStatsForCompanyForPastNDays( companyId, startDate, endDate );

        LOG.debug( "method getActiveUserCountStatsForCompanyForPastNDays finished for companyId %s and noOfDays %s" , companyId , noOfDays );
        return companyActiveUsersStats;
    }
}
