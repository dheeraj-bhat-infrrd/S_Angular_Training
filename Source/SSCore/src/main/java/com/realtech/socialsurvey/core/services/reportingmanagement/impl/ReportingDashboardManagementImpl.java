package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.GenericReportingDao;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.GenerateReportList;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;

@Component
public class ReportingDashboardManagementImpl implements ReportingDashboardManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingDashboardManagementImpl.class );

    @Autowired
    private GenericReportingDao<GenerateReportList, Long> GenerateReportListDao;
    
    @Override
    public void generateReports(int reportId , Date startDate , Date endDate , Date currentDate , String firstName , String lastName , Long entityId , String entityType){
        //adding entry in the feild and set status to pending
        createEntryinGenerateReportList(reportId,startDate,endDate,currentDate,firstName,lastName,entityId,entityType);
        
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public void createEntryinGenerateReportList(int reportId , Date startDate , Date endDate , Date currentDate , String firstName , String lastName , Long entityId , String entityType){
        LOG.info( "method to insert data into the generateReportList and save in aws server" );
        //input value into the generateReportList table 
        GenerateReportList generateReportEntity = new GenerateReportList();
       
       
        if ( currentDate != null ) {
            generateReportEntity.setCurrentDate(new Timestamp(currentDate.getTime()) );          
        }
        if(reportId == CommonConstants.SURVEY_STATS_REPORT_ID){
            generateReportEntity.setReportName( CommonConstants.SURVEY_STATS_REPORT_NAME );
        }
        
        if ( startDate != null ) {
            generateReportEntity.setStartDate(new Timestamp(startDate.getTime()) );            
        }
        if ( endDate != null ) {
            generateReportEntity.setEndDate( new Timestamp( endDate.getTime() ) );          
        }
        generateReportEntity.setFirstName( firstName );
        generateReportEntity.setLastName( lastName );
        generateReportEntity.setEntityId( entityId );
        generateReportEntity.setEntityType( entityType );
        generateReportEntity.setStatus( CommonConstants.REPORT_STATUS_PENDING );
        LOG.info( "generateReportEntityValye"+generateReportEntity );
        LOG.info( "generateReportEntityValye"+GenerateReportListDao.save(generateReportEntity));
        GenerateReportListDao.save(generateReportEntity);
        
    }
}
