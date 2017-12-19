package com.realtech.socialsurvey.core.services.activitymanager.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.CompanyTransactionsSourceStats;
import com.realtech.socialsurvey.core.entities.CompanyView;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.activitymanager.ActivityManagementService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.vo.TransactionMonitorGraphDataVO;


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
     * @param companyId
     * @param noOfDays
     * @return
     */
    @Override
    @Transactional
    public List<CompanyView> getCompaniesWithNoTransactionInPastNDays( List<CompanyView> companies, int noOfDays )
    {
        LOG.info( "method getCompaniesWithNoTransactionInPastNDays started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -( noOfDays ) ); // subtract the no of days
        Date nDaysBackDate = new Date( calendar.getTimeInMillis() );

        Map<Long, Long> companyTransactionCountsMap = companyTransactionsSourceStatsDao
            .getTransactionsByCompanyIdAndAfterTransactionDate( nDaysBackDate );
        List<CompanyView> companyWithNoTransactions = new ArrayList<CompanyView>();
        for ( CompanyView company : companies ) {
            if ( companyTransactionCountsMap.get( company.getCompanyId() ) == null
                || companyTransactionCountsMap.get( company.getCompanyId() ) == 0l )
                companyWithNoTransactions.add( company );
        }

        LOG.info( "method getCompaniesWithNoTransactionInPastNDays ended" );
        return companyWithNoTransactions;
    }


    /**
     * 
     */
    @Override
    @Transactional
    public List<CompanySurveyStatusStats> getSurveyStatusStatsForPastDay()
    {
        LOG.info( "method getSurveyStatusStatsForPastDay started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -1 );
        Date yesterDayDate = new Date( calendar.getTimeInMillis() );

        List<CompanySurveyStatusStats> companySurveyStatusStatsList = companySurveyStatusStatsDao
            .findByColumn( CompanySurveyStatusStats.class, CommonConstants.TRANSACTION_MONITOR_DATE_COLUMN, yesterDayDate );

        LOG.info( "method getSurveyStatusStatsForPastDay ended" );
        return companySurveyStatusStatsList;
    }


    /**
     * 
     * @param companySurveyStatusStatsList
     */
    @Override
    @Transactional
    public List<Long> validateSurveyStatsForCompanies( List<CompanySurveyStatusStats> companySurveyStatusStatsList )
    {

        LOG.info( "method validateSurveyStatsForCompanies started" );
        List<Long> companyIds = new ArrayList<Long>();
        for ( CompanySurveyStatusStats companySurveyStatusStats : companySurveyStatusStatsList ) {
            int transactionReceivedCount = companySurveyStatusStats.getTransactionReceivedCount();
            int surveyInvitationCount = companySurveyStatusStats.getSurveyInvitationSentCount();
            if ( transactionReceivedCount > 0 && surveyInvitationCount > 0 ) {
                if ( surveyInvitationCount < ( transactionReceivedCount / 2 ) ) {
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
    @Transactional
    public Map<Long, Long> getSurveyStatusStatsForPastOneMonth()
    {
        LOG.info( "method getSurveyStatusStatsForPastOneMonth started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -30 );
        Date oneMonthBackDate = new Date( calendar.getTimeInMillis() );

        Map<Long, Long> companySurveyStatsCountsMap = companySurveyStatusStatsDao
            .getSentSurveyCountForCompaniesAfterSentDate( oneMonthBackDate );

        LOG.info( "method getSurveyStatusStatsForPastOneMonth ended" );
        return companySurveyStatsCountsMap;
    }


    /**
     * 
     * @param companyId
     * @param noOfDays
     * @throws InvalidInputException 
     */
    @Override
    @Transactional
    public List<CompanyTransactionsSourceStats> getTransactionsCountForCompanyForPastNDays( long companyId, int noOfDays )
        throws InvalidInputException
    {

        LOG.info( "method getTransactionsCountForCompanyForPastNDays started for companyId {} and noOfDays {}", companyId,
            noOfDays );

        Date startDate = null;
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanyTransactionsSourceStats> companyTransactionsStats;
        if(companyId <= 0){
            companyTransactionsStats  = companyTransactionsSourceStatsDao.getOverallTransactionsCountForPastNDays(  startDate, endDate );            
        }else{
            companyTransactionsStats = companyTransactionsSourceStatsDao.getTransactionsCountForCompanyForPastNDays( companyId, startDate, endDate );      
        }

        LOG.info( "method getTransactionsCountForCompanyForPastNDays finished for companyId %s and noOfDays %s", companyId,
            noOfDays );
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
    public List<CompanySurveyStatusStats> getSurveyStatusStatsForCompanyForPastNDays( long companyId, int noOfDays )
        throws InvalidInputException
    {
        
        LOG.info( "method getSurveyStatusStatsForCompanyForPastNDays started for companyId %s and noOfDays %s" , companyId , noOfDays );
       
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        List<CompanySurveyStatusStats> companySurveyStats = null;
        if(companyId <= 0){
            companySurveyStats = companySurveyStatusStatsDao.getOverallSurveyCountForPastNDays( startDate, endDate );
        }else{
            companySurveyStats = companySurveyStatusStatsDao.getSurveyStatusCountForCompanyForPastNDays( companyId, startDate, endDate );                        
        }
        
        LOG.info( "method getSurveyStatusStatsForCompanyForPastNDays finished for companyId {} and noOfDays {}" , companyId , noOfDays );
        return companySurveyStats;
    }


    /**
     * 
     */
    @Override
    @Transactional
    public List<CompanyActiveUsersStats> getActiveUserCountStatsForCompanyForPastNDays( long companyId, int noOfDays )
        throws InvalidInputException
    {

        LOG.info( "method getActiveUserCountStatsForCompanyForPastNDays started for companyId {} and noOfDays {}", companyId,
            noOfDays );

        if ( companyId <= 0l ) {
            LOG.error( "Invalid companyId is passed : {} ", companyId );
            throw new InvalidInputException( "Passed parameter companyId is invalid " );
        }

        Date startDate = null;
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }

        List<CompanyActiveUsersStats> companyActiveUsersStats = companyActiveUsersStatsDao
            .getActiveUsersCountStatsForCompanyForPastNDays( companyId, startDate, endDate );

        LOG.info( "method getActiveUserCountStatsForCompanyForPastNDays finished for companyId {} and noOfDays {}", companyId,
            noOfDays );
        return companyActiveUsersStats;
    }


    /**
     * 
     * @return
     */
    @Override
    @Transactional
    public List<CompanyActiveUsersStats> getCompanyActiveUserCountForPastDay()
    {
        LOG.info( "method getCompanyActiveUserCountForPastDay started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -1 );
        Date yesterDayDate = new Date( calendar.getTimeInMillis() );

        List<CompanyActiveUsersStats> companyActiveUsersStatsList = companyActiveUsersStatsDao
            .findByColumn( CompanyActiveUsersStats.class, CommonConstants.SURVEY_STATS_MONITOR_DATE_COLUMN, yesterDayDate );

        LOG.info( "method getCompanyActiveUserCountForPastDay ended" );
        return companyActiveUsersStatsList;
    }

    
    @Override
    public List<String> getTransactionMonitorMailList()
    {
        String[] transactionMailRecipient =  transactionMonitorSupportEmail.split( "," );
        List<String> transactionMailList = new ArrayList<>();
        for(String recipient : transactionMailRecipient)
        {
            transactionMailList.add( recipient );
        }
        return transactionMailList;
        
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
    public List<TransactionMonitorGraphDataVO> getTransactionsStatsByDaysAndAlertType( int noOfDays, String alertType) throws InvalidInputException
    {
        
        LOG.info( "method getTransactionsStatsByDaysAndAlertType started for alertType %s and noOfDays %s" , alertType , noOfDays );       
        
        Date startDate = null;       
        Date endDate = null;
        if ( noOfDays >= 0 ) {
            startDate = utils.getNDaysBackDate( noOfDays );
            endDate = new Date( System.currentTimeMillis() );
        }
        
        //get companies from mongo based on alert type
        List<OrganizationUnitSettings> companyList = organizationManagementService.getCompaniesByAlertType( alertType );
        
        //get companyId list
        List<Long> companyIdList = new ArrayList<Long>();
        for(OrganizationUnitSettings company : companyList){
            companyIdList.add( company.getIden() );
        }
        
        //get all monitored data for given companies from sql
       Map<Long , List<CompanySurveyStatusStats>> companyIdSurveyStatsMap = new HashMap<Long, List<CompanySurveyStatusStats>>();
       if( ! companyIdList.isEmpty())
           companyIdSurveyStatsMap = companySurveyStatusStatsDao.getSurveyStatusCountForCompaniesForPastNDays( companyIdList, startDate, endDate );                        
        
        //fill stats data to vo
       List<TransactionMonitorGraphDataVO> transactionMonitorGraphDataVOs = new ArrayList<TransactionMonitorGraphDataVO>();
       for(OrganizationUnitSettings company : companyList){
           TransactionMonitorGraphDataVO monitorGraphDataVO = new TransactionMonitorGraphDataVO();
           monitorGraphDataVO.setCompanyId( company.getIden() );
           monitorGraphDataVO.setEntityAlertDetails( company.getEntityAlertDetails() );
           monitorGraphDataVO.setCompanySurveyStatusStatslist( companyIdSurveyStatsMap.get(  company.getIden()  ) );
           if(company.getContact_details() != null)
               monitorGraphDataVO.setCompanyName( company.getContact_details().getName() );
           
           transactionMonitorGraphDataVOs.add( monitorGraphDataVO );
        }
        
        
        LOG.info( "method getTransactionsStatsByDaysAndAlertType finished for alertType {} and noOfDays {}" , alertType , noOfDays );
        return transactionMonitorGraphDataVOs;
    }
    
    /**
     * 
     */
    @Override
    @Transactional
    public Map<Long, Long> getTotalTransactionCountForPast3DaysForCompanies()
    {
        LOG.info( "method getTotalTransactionCountForPastNDaysForCompanies started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, - 3 );
        Date oneMonthBackDate = new Date( calendar.getTimeInMillis() );

        Map<Long, Long> companySurveyStatsCountsMap = companySurveyStatusStatsDao
            .getTotalTransactionCountForCompaniesAfterSentDate( oneMonthBackDate );

        LOG.info( "method getTotalTransactionCountForPastNDaysForCompanies ended" );
        return companySurveyStatsCountsMap;
    }
    
    
    /**
     * 
     */
    @Override
    @Transactional
    public Map<Long, Long> getTransactionCountForPreviousDay()
    {
        LOG.info( "method getTransactionCountForPreviousDay started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, - 1 );
        Date prevoiusDayDate = new Date( calendar.getTimeInMillis() );

        Map<Long, Long> companySurveyStatsCountsMap = companySurveyStatusStatsDao
            .getTotalTransactionCountForCompaniesAfterSentDate( prevoiusDayDate );

        LOG.info( "method getTransactionCountForPreviousDay ended" );
        return companySurveyStatsCountsMap;
    }
    
    @Override
    @Transactional
    public Map<Long, Long> getSendSurveyCountForPast7Days()
    {
        LOG.info( "method getSendSurveyCountForPreviousDay started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, - 7 );
        Date sevenDaysBackDate = new Date( calendar.getTimeInMillis() );

        Map<Long, Long> companySurveyStatsCountsMap = companySurveyStatusStatsDao
            .getSentSurveyCountForCompaniesAfterSentDate( sevenDaysBackDate );
        LOG.info( "method getSendSurveyCountForPreviousDay ended" );
        return companySurveyStatsCountsMap;
    }
    
    @Override
    @Transactional
    public Map<Long, Long> getSendSurveyCountForPreviousDay()
    {
        LOG.info( "method getSendSurveyCountForPreviousDay started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, - 1 );
        Date prevoiusDayDate = new Date( calendar.getTimeInMillis() );

        Map<Long, Long> companySurveyStatsCountsMap = companySurveyStatusStatsDao
            .getSentSurveyCountForCompaniesAfterSentDate( prevoiusDayDate );
        LOG.info( "method getSendSurveyCountForPreviousDay ended" );
        return companySurveyStatsCountsMap;
    }
    
    @Override
    @Transactional
    public Map<Long, List<CompanySurveyStatusStats>> getSurveStatsForPast7daysForAllCompanies()
    {
        LOG.info( "method getSendSurveyCountForPreviousDay started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, - 7 );
        Date sevenDaysBackDate = new Date( calendar.getTimeInMillis() );

        Map<Long, List<CompanySurveyStatusStats>> companySurveyStatsMap = companySurveyStatusStatsDao.getSurveyStatusCountForAllCompaniesForPastNDays( sevenDaysBackDate, null );
        LOG.info( "method getSendSurveyCountForPreviousDay ended" );
        return companySurveyStatsMap;
    }
    
    
    @Override
    @Transactional
    public Map<Long, List<CompanySurveyStatusStats>> getSurveStatsForLastToLatWeekForAllCompanies()
    {
        LOG.info( "method getSurveStatsForLastToLatWeekForAllCompanies started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, - 14 );
        Date startDate = new Date( calendar.getTimeInMillis() );
        
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add( Calendar.DATE, - 7 );
        Date endDate = new Date( calendar.getTimeInMillis() );

        Map<Long, List<CompanySurveyStatusStats>> companySurveyStatsMap = companySurveyStatusStatsDao.getSurveyStatusCountForAllCompaniesForPastNDays( startDate, endDate );
        LOG.info( "method getSurveStatsForLastToLatWeekForAllCompanies ended" );
        return companySurveyStatsMap;
    }
      
    /**
     * 
     */
    @Override
    @Transactional
    public Map<Long, Long> getCompletedSurveyCountForPast3DaysForCompanies()
    {
        LOG.info( "method getCompletedSurveyCountForPast3DaysForCompanies started" );

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, - 3 );
        Date oneMonthBackDate = new Date( calendar.getTimeInMillis() );

        Map<Long, Long> companySurveyCompletedCountsMap = companySurveyStatusStatsDao
            .getCompletedCountForCompaniesAfterSentDate( oneMonthBackDate );

        LOG.info( "method getCompletedSurveyCountForPast3DaysForCompanies ended" );
        return companySurveyCompletedCountsMap;
    }
}
