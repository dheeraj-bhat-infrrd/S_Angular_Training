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

import com.realtech.socialsurvey.core.commons.CommonConstants;
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
    
    @Value ( "${TRANSACTION_MONITOR_SUPPORT_EMAIL}")
    private String transactionMonitorSupportEmail;
    
    /**
     * 
     */
    @Override
    public void sendHighNotProcessedTransactionAlertMailForCompanies(List<Long> companyIdsToSendAlert, List<Company> allActiveCompanies)
    {
        
        LOG.info( "method sendHighNotProcessedTransactionAlertMailForCompanies started" );

        String mailBody = "";
        
        int i = 0;
        for(Company company :  allActiveCompanies){
            //check if we need to send alert mail for this company
            if(companyIdsToSendAlert.contains( company.getCompanyId() )){
                i++;
                mailBody +=  (i + ". " + " " + company.getCompany() + " with id "  + company.getCompanyId()  + " have more than 50% unprocessed transactions for previous day." );             
                mailBody += "<br>";
            }
            
        }
        
        
        try {
            //send mail if there is atleast one company with high not processed transactions
            if(i > 0 )
                emailServices.sendHighVoulmeUnprocessedTransactionAlertMail( getTransactionMonitorMailList(), mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
           LOG.error( "Error while sending highNotProcessed alert email " , e );
        }
        LOG.info( "method sendHighNotProcessedTransactionAlertMailForCompanies ended" );
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
    public void sendNoTransactionAlertMailForCompanies(List<Company> companies , int noOfDays)
    {
        
        LOG.info( "method sendNoTransactionAlertMailForCompanies started" );
        String mailBody = "";
        
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -(noOfDays) ); // subtract the no of days
        Date nDaysBackDate = new Date( calendar.getTimeInMillis() );
        
        int i = 0;
        for(Company company :  companies){
            i++;
            mailBody +=  (i + ". " + " " + company.getCompany() + " with id " + company.getCompanyId()  + " SocialSurvey has not received any transaction details since " + nDaysBackDate);             
            mailBody += "<br>"  ;
        }
        
        
        try {
            //send mail if there is at least one company with no transactions
            if(i >0 )
                emailServices.sendNoTransactionAlertMail( getTransactionMonitorMailList(), mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
           LOG.error( "Error while sending highNotProcessed aler email ", e );
        }
        LOG.info( "method sendNoTransactionAlertMailForCompanies ended" );
    }
    
    /**
     * 
     */
    @Override
    public List<CompanySurveyStatusStats> getSurveyStatusStatsForPastDay()
    {
        LOG.info( "method getSurveyStatusStatsForPastDay started" );
        
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -1 );
        Date yesterDayDate = new Date( calendar.getTimeInMillis() );
        
        List<CompanySurveyStatusStats> companySurveyStatusStatsList = companySurveyStatusStatsDao.findByColumn(CompanySurveyStatusStats.class, CommonConstants.TRANSACTION_MONITOR_DATE_COLUMN, yesterDayDate );
        
        LOG.info( "method getSurveyStatusStatsForPastDay ended" );
        return companySurveyStatusStatsList;
    }
    
    /**
     * 
     * @param companySurveyStatusStatsList
     */
    @Override
    public List<Long> validateSurveyStatsForCompanies(List<CompanySurveyStatusStats> companySurveyStatusStatsList)
    {
        
        LOG.info( "method validateSurveyStatsForCompanies started" );
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
        
        LOG.info( "method validateSurveyStatsForCompanies ended" );
        return companyIds;
    }

    /**
     * 
     */
    @Override
    public Map<Long, Long> getSurveyStatusStatsForPastOneMonth()
    {
        LOG.info( "method getSurveyStatusStatsForPastOneMonth started" );
        
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -30 );
        Date oneMonthBackDate = new Date( calendar.getTimeInMillis() );
        
        Map<Long, Long> companySurveyStatsCountsMap  = companySurveyStatusStatsDao.getSentSurveyCountForCompaniesAfterSentDate( oneMonthBackDate );
        
        LOG.info( "method getSurveyStatusStatsForPastOneMonth ended" );
        return companySurveyStatsCountsMap;
    }

    @Override
    public void validateAndSentLessSurveysAlert( List<CompanyActiveUsersStats> companyActiveUserCounts, Map<Long, Long> companySurveyStatsCountsMap )
    {
        LOG.info( "method validateAndSentLessSurveysAlert started" );
        String mailBody = "";

        int i = 0;
        for(CompanyActiveUsersStats companyActiveUsersStats : companyActiveUserCounts){
            Long surveyCount = companySurveyStatsCountsMap.get( companyActiveUsersStats.getCompanyId() );
            Integer userCount = companyActiveUsersStats.getNoOfActiveUsers();
            if(surveyCount != null && userCount!= null &&  (surveyCount/2) < userCount ){
                i++;
                mailBody +=  (i + ". " + "Company with id "  + companyActiveUsersStats.getCompanyId() + "  sent us  " + surveyCount + " transactions for total of " + userCount + " Users in past one month." );             
                mailBody += "<br>";
            }
        }
        
        try {
            //send mail only if there is at least one company with less survey transactions
            if(i > 0)
                emailServices.sendLessVoulmeOfTransactionReceivedAlertMail( getTransactionMonitorMailList(), mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Error while sending less survey alert email." , e );
}
        LOG.info( "method validateAndSentLessSurveysAlert ended" ); 
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
        
        LOG.info( "method getTransactionsCountForCompanyForPastNDays started for companyId {} and noOfDays {}" , companyId , noOfDays );
        
        if(companyId <= 0l){
            LOG.error("Invalid companyId is passed : {} " , companyId );
            throw new InvalidInputException("Passed parameter companyId is invalid ");
        }
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanyTransactionsSourceStats> companyTransactionsStats = companyTransactionsSourceStatsDao.getTransactionsCountForCompanyForPastNDays( companyId, startDate, endDate );

        LOG.info( "method getTransactionsCountForCompanyForPastNDays finished for companyId %s and noOfDays %s" , companyId , noOfDays );
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
        
        LOG.info( "method getSurveyStatusStatsForCompanyForPastNDays started for companyId %s and noOfDays %s" , companyId , noOfDays );
        
        if(companyId <= 0l){
            LOG.error("Invalid companyId is passed : {} " , companyId );
            throw new InvalidInputException("Passed parameter companyId is invalid ");
        }
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanySurveyStatusStats> companySurveyStats = companySurveyStatusStatsDao.getSurveyStatusCountForCompanyForPastNDays( companyId, startDate, endDate );

        LOG.info( "method getSurveyStatusStatsForCompanyForPastNDays finished for companyId {} and noOfDays {}" , companyId , noOfDays );
        return companySurveyStats;
    }
    
    
    /**
     * 
     */
    @Override
    @Transactional
    public List<CompanyActiveUsersStats> getActiveUserCountStatsForCompanyForPastNDays(long companyId, int noOfDays) throws InvalidInputException
    {
        
        LOG.info( "method getActiveUserCountStatsForCompanyForPastNDays started for companyId {} and noOfDays {}" , companyId , noOfDays );
        
        if(companyId <= 0l){
            LOG.error("Invalid companyId is passed : {} " , companyId );
            throw new InvalidInputException("Passed parameter companyId is invalid ");
        }
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanyActiveUsersStats> companyActiveUsersStats = companyActiveUsersStatsDao.getActiveUsersCountStatsForCompanyForPastNDays( companyId, startDate, endDate );

        LOG.info( "method getActiveUserCountStatsForCompanyForPastNDays finished for companyId {} and noOfDays {}" , companyId , noOfDays );
        return companyActiveUsersStats;
    }
    
    /**
     * 
     * @return
     */
    @Override
    public List<CompanyActiveUsersStats> getCompanyActiveUserCountForPastDay()
    {
        LOG.info( "method getCompanyActiveUserCountForPastDay started" );
        
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -1 );
        Date yesterDayDate = new Date( calendar.getTimeInMillis() );
        
        List<CompanyActiveUsersStats> companyActiveUsersStatsList = companyActiveUsersStatsDao.findByColumn(CompanyActiveUsersStats.class, CommonConstants.SURVEY_STATS_MONITOR_DATE_COLUMN, yesterDayDate );
        
        LOG.info( "method getCompanyActiveUserCountForPastDay ended" );
        return companyActiveUsersStatsList;
    }
    
    
    private List<String> getTransactionMonitorMailList()
    {
        String transactionMailRecipient[] =  transactionMonitorSupportEmail.split( "," );
        List<String> transactionMailList = new ArrayList<>();
        for(String recipient : transactionMailRecipient)
        {
            transactionMailList.add( recipient );
        }
        return transactionMailList;
        
    }
}
