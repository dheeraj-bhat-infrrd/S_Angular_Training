package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.GenericReportingDao;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportBranchDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.GenerateReportList;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportCompany;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportRegion;
import com.realtech.socialsurvey.core.enums.SurveyErrorCode;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;

@Component
public class ReportingDashboardManagementImpl implements ReportingDashboardManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingDashboardManagementImpl.class );

    @Autowired
    private GenericReportingDao<GenerateReportList, Long> GenerateReportListDao;
    
    @Autowired
    private GenericDao<FileUpload, Long> fileUploadDao;
    
    @Autowired
    private SurveyStatsReportBranchDao SurveyStatsReportBranchDao;
    
    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;
    
    @Override
    public void generateReports(int reportId , Date startDate , Date endDate, Long entityId , String entityType , Company company , Long adminUserid) throws InvalidInputException, NoRecordsFetchedException, FileNotFoundException, IOException{
        //adding entry in the feild and set status to pending
        createEntryForReportInFileUpload(reportId,startDate,endDate,entityId,entityType,company,adminUserid);
        
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public void createEntryForReportInFileUpload(int reportId , Date startDate , Date endDate ,Long entityId , String entityType ,Company company , Long adminUserId){
        LOG.info( "method to insert data into the generateReportList and save in aws server" );
        //input value into the generateReportList table 
        FileUpload fileUpload = new FileUpload();
       
        fileUpload.setCompany( company );
        fileUpload.setAdminUserId( CommonConstants.REALTECH_ADMIN_ID );
        fileUpload.setFileName( "default" );
        fileUpload.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        fileUpload.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        if(reportId == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT){
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT );
        }
        
        if ( startDate != null ) {
            fileUpload.setStartDate(new Timestamp(startDate.getTime()) );            
        }
        if ( endDate != null ) {
            fileUpload.setEndDate( new Timestamp( endDate.getTime() ) );          
        }
        fileUpload.setProfileValue( entityId );
        fileUpload.setProfileLevel( entityType );
        fileUpload.setStatus( CommonConstants.STATUS_ACTIVE );
        fileUploadDao.save(fileUpload);
        
    }
    
    /*
     * Generate report from the surveyStats Table
     * 
     */
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public List<List<Object>> getSurveyStatsReport(Long entityId , String entityType){
        
        List<List<Object>> surveyStats = new ArrayList<>();
        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            for(SurveyStatsReportBranch SurveyStatsReportCompany : SurveyStatsReportBranchDao.fetchSurveyStatsByCompanyId(entityId) ){
                List<Object> surveyStatsReportToPopulate = new ArrayList<>();
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getId());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getCompanyName());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getBranchName());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getTrxMonth());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getTrxRcvd());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getPending());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getDuplicates());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getCorrupted());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getAbusive());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getOldRecords());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getIgnored());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getMismatched());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getSentCount());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getClickedCount());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getCompleted());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getPartiallyCompleted());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getCompletePercentage());
                surveyStatsReportToPopulate.add(SurveyStatsReportCompany.getDelta());
                surveyStats.add( surveyStatsReportToPopulate );
            }
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
            for(SurveyStatsReportBranch SurveyStatsReportRegion : SurveyStatsReportBranchDao.fetchSurveyStatsByRegionId(entityId) ){
                List<Object> surveyStatsReportToPopulate = new ArrayList<>();
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getId());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getCompanyName());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getBranchName());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getTrxMonth());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getTrxRcvd());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getPending());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getDuplicates());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getCorrupted());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getAbusive());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getOldRecords());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getIgnored());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getMismatched());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getSentCount());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getClickedCount());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getCompleted());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getPartiallyCompleted());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getCompletePercentage());
                surveyStatsReportToPopulate.add(SurveyStatsReportRegion.getDelta());
                surveyStats.add( surveyStatsReportToPopulate );
            }
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            for(SurveyStatsReportBranch SurveyStatsReportBranch : SurveyStatsReportBranchDao.fetchBranchSurveyStatsById(entityId ) ){
                List<Object> surveyStatsReportToPopulate = new ArrayList<>();
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getId());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getCompanyName());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getBranchName());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getTrxMonth());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getTrxRcvd());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getPending());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getDuplicates());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getCorrupted());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getAbusive());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getOldRecords());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getIgnored());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getMismatched());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getSentCount());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getClickedCount());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getCompleted());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getPartiallyCompleted());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getCompletePercentage());
                surveyStatsReportToPopulate.add(SurveyStatsReportBranch.getDelta());
                surveyStats.add( surveyStatsReportToPopulate );
            }
        }
     
        return surveyStats;
        
    }
    
    
    
}