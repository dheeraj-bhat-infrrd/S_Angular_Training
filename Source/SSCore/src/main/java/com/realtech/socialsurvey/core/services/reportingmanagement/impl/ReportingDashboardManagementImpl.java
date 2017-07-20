package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.api.builder.SSApiBatchIntegrationBuilder;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.CompanyUserReportDao;
import com.realtech.socialsurvey.core.dao.FileUploadDao;
import com.realtech.socialsurvey.core.dao.SurveyResponseTableDao;
import com.realtech.socialsurvey.core.dao.SurveyResultsCompanyReportDao;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportBranchDao;
import com.realtech.socialsurvey.core.dao.SurveyTransactionReportBranchDao;
import com.realtech.socialsurvey.core.dao.SurveyTransactionReportDao;
import com.realtech.socialsurvey.core.dao.SurveyTransactionReportRegionDao;
import com.realtech.socialsurvey.core.dao.UserAdoptionReportDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyUserReport;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReport;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReportRegion;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserAdoptionReport;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
//import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookData;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookOperations;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@Component
public class ReportingDashboardManagementImpl implements ReportingDashboardManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingDashboardManagementImpl.class );
    
    @Autowired
    private FileUploadDao fileUploadDao;
    
    @Autowired
    private SurveyStatsReportBranchDao SurveyStatsReportBranchDao;
    
    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private UserAdoptionReportDao userAdoptionReportDao;
    
    @Autowired
    private SurveyTransactionReportDao surveyTransactionReportDao;
    
    @Autowired
    private SurveyTransactionReportRegionDao surveyTransactionReportRegionDao;  
    
    @Autowired
    private SurveyTransactionReportBranchDao surveyTransactionReportBranchDao;
    
    @Autowired
    private SurveyResultsCompanyReportDao surveyResultsCompanyReportDao;
    
    @Autowired
    private SurveyResponseTableDao surveyResponseTableDao;
    
    @Autowired
    private CompanyUserReportDao companyUserReportDao;
    
    @Autowired
    private SSApiBatchIntegrationBuilder ssApiBatchIntergrationBuilder;
    
    @Autowired
    private WorkbookData workbookData;
    
    @Autowired
    private WorkbookOperations workbookOperations;
    
    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;
    
    @Value ( "${CDN_REPORTING_PATH}")
    private String endpoint;
    
    @Value ( "${REPORTING_BUCKET}")
    private String bucketName;
    
    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;
    

    
    @Override
    public void createEntryInFileUploadForReporting(int reportId , Date startDate , Date endDate, Long entityId , String entityType , Company company , Long adminUserId) throws InvalidInputException, NoRecordsFetchedException, FileNotFoundException, IOException{
        //adding entry in the feild and set status to pending
        LOG.info( "method to insert data into the generateReportList and save in aws server" );
        //input value into the generateReportList table 
        FileUpload fileUpload = new FileUpload();
       
        fileUpload.setCompany( company );
        if(adminUserId != null){
            fileUpload.setAdminUserId( adminUserId );

        }
        fileUpload.setFileName( " " );
        fileUpload.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        fileUpload.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        if(reportId == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT){
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT );
        }else if(reportId == CommonConstants.FILE_UPLOAD_REPORTING_USER_ADOPTION_REPORT){
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_USER_ADOPTION_REPORT );            
        }else if(reportId == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT){
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT );            
        }else if(reportId == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_COMPANY_REPORT){
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_COMPANY_REPORT );            
        }else if(reportId == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT){
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT );            
        }
        
        if ( startDate != null ) {
            fileUpload.setStartDate(new Timestamp(startDate.getTime()) );            
        }
        if ( endDate != null ) {
            fileUpload.setEndDate( new Timestamp( endDate.getTime() ) );          
        }
        fileUpload.setProfileValue( entityId );
        fileUpload.setProfileLevel( entityType );
        fileUpload.setStatus( CommonConstants.STATUS_PENDING );
        fileUpload.setShowOnUI( true );
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

        for(SurveyStatsReportBranch SurveyStatsReportCompany : SurveyStatsReportBranchDao.fetchSurveyStatsById(entityId , entityType) ){
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

 
        return surveyStats;
        
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public List<List<Object>> getUserAdoptionReport(Long entityId , String entityType){
        List<List<Object>> userAdoption = new ArrayList<>();
        for(UserAdoptionReport UserAdoptionReport : userAdoptionReportDao.fetchUserAdoptionById(entityId , entityType) ){
            List<Object> userAdoptionReportList = new ArrayList<>();
            userAdoptionReportList.add( UserAdoptionReport.getCompanyName() );
            if(UserAdoptionReport.getRegionName() != null && !UserAdoptionReport.getRegionName().isEmpty() ){
                userAdoptionReportList.add( UserAdoptionReport.getRegionName() );
            }else{
                userAdoptionReportList.add( "" );
            }
            if(UserAdoptionReport.getBranchName() != null && !UserAdoptionReport.getBranchName().isEmpty()){
                userAdoptionReportList.add( UserAdoptionReport.getBranchName() );
            }else{
                userAdoptionReportList.add( "" );
            }
            userAdoptionReportList.add( UserAdoptionReport.getInvitedUsers() );
            userAdoptionReportList.add( UserAdoptionReport.getActiveUsers() );
            userAdoptionReportList.add( UserAdoptionReport.getAdoptionRate() );
            userAdoption.add( userAdoptionReportList );
        }
        
        return userAdoption;
        
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public List<String> getSurveyResponseData(String surveyDetailsId){
    	List<String> surveyResponse =  new ArrayList<>();
    	for(SurveyResponseTable surveyResponseTable: surveyResponseTableDao.fetchSurveyResponsesBySurveyDetailsId(surveyDetailsId)){
    		  		
    		surveyResponse.add(surveyResponseTable.getAnswer());
    	}
    	return surveyResponse;
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public List<List<Object>> getSurveyResultsCompanyReport(Long entityId, String entityType,Timestamp startDate, Timestamp endDate){
    	
    	List<List<Object>> surveyResultsCompany = new ArrayList<>();
    	if(entityType.equals(CommonConstants.COMPANY_ID_COLUMN )){
    		for(SurveyResultsCompanyReport SurveyResultsCompanyReport: surveyResultsCompanyReportDao.fetchSurveyResultsCompanyReportByCompanyId(entityId,startDate,endDate)){
    			List<Object> surveyResultsCompanyReportList = new ArrayList<>();
    			
    			if(SurveyResultsCompanyReport.getUserFirstName() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getUserFirstName());
    			}
    			
    			if(SurveyResultsCompanyReport.getUserLastName() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getUserLastName());
    			}
    			
    			if(SurveyResultsCompanyReport.getCustomerFirstName() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getCustomerFirstName());
    			}
    			
    			if(SurveyResultsCompanyReport.getCustomerLastName() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getCustomerLastName());
    			}
    			
    			if(SurveyResultsCompanyReport.getSurveySentDate() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getSurveySentDate());
    			}
    			
    			if(SurveyResultsCompanyReport.getSurveyCompletedDate() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getSurveyCompletedDate());
    			}
    			
    			surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getTimeInterval());
    			
    			if(SurveyResultsCompanyReport.getSurveySource() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getSurveySource());
    			}
    			
    			if(SurveyResultsCompanyReport.getSurveySourceId() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getSurveySourceId());
    			}
    			
    			surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getSurveyScore());
    			
    			String surveyDetailsId = SurveyResultsCompanyReport.getSurveyDetailsId();
    			
    			int questionCounter = 0;
    			for(SurveyResponseTable surveyResponse: surveyResponseTableDao.fetchSurveyResponsesBySurveyDetailsId(surveyDetailsId)){
    				questionCounter++;
    			}
    			surveyResultsCompanyReportList.add(questionCounter);
    			
    			for(SurveyResponseTable surveyResponse: surveyResponseTableDao.fetchSurveyResponsesBySurveyDetailsId(surveyDetailsId)){
    				if(surveyResponse.getAnswer() == null){
    					surveyResultsCompanyReportList.add("");
    				}else{
    					surveyResultsCompanyReportList.add(surveyResponse.getAnswer());
    				}
    			}
    			if(questionCounter==0){
    				surveyResultsCompanyReportList.add("");
    			}
    			
    			if(SurveyResultsCompanyReport.getGateway() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getGateway());
    			}
    			
    			if(SurveyResultsCompanyReport.getCustomerComments() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getCustomerComments());
    			}
    			
    			if(SurveyResultsCompanyReport.getAgreedToShare() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getAgreedToShare());
    			}
    			
    			if(SurveyResultsCompanyReport.getBranchName() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getBranchName());
    			}
    			
    			if(SurveyResultsCompanyReport.getClickTroughForCompany() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getClickTroughForCompany());
    			}
    			
    			if(SurveyResultsCompanyReport.getClickTroughForAgent() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getClickTroughForAgent());
    			}
    			
    			if(SurveyResultsCompanyReport.getClickTroughForRegion() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getClickTroughForRegion());
    			}
    			
    			if(SurveyResultsCompanyReport.getClickTroughForBranch() == null){
    				surveyResultsCompanyReportList.add("");
    			}else{
    				surveyResultsCompanyReportList.add(SurveyResultsCompanyReport.getClickTroughForBranch());
    			}
    			
    			surveyResultsCompany.add(surveyResultsCompanyReportList);
    		}
    	}
    	
    	return surveyResultsCompany;
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public List<List<Object>> getCompanyUserReport(Long entityId , String entityType){
        List<List<Object>> companyUser = new ArrayList<>();
        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            for(CompanyUserReport companyUserReport : companyUserReportDao.fetchCompanyUserReportByCompanyId( entityId )){
                List<Object> companyUserReportList = new ArrayList<>();
                if(companyUserReport.getFirstName() != null && !companyUserReport.getFirstName().isEmpty()){
                    companyUserReportList.add( companyUserReport.getFirstName() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLastName() != null && !companyUserReport.getLastName().isEmpty()){
                    companyUserReportList.add( companyUserReport.getLastName() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getEmail() != null && !companyUserReport.getEmail().isEmpty()){
                    companyUserReportList.add( companyUserReport.getEmail() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getSocialSurveyAccessLevel() != null && !companyUserReport.getSocialSurveyAccessLevel().isEmpty()){
                    companyUserReportList.add( companyUserReport.getSocialSurveyAccessLevel() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getOfficeBranchAssignment() != null && !companyUserReport.getOfficeBranchAssignment().isEmpty()){
                    companyUserReportList.add( companyUserReport.getOfficeBranchAssignment() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getRegionAssignment() != null && !companyUserReport.getRegionAssignment().isEmpty()){
                    companyUserReportList.add( companyUserReport.getRegionAssignment() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getOfficeAdmin() != null && !companyUserReport.getOfficeAdmin().isEmpty()){
                    companyUserReportList.add( companyUserReport.getOfficeAdmin() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getRegionAdmin() != null && !companyUserReport.getRegionAdmin().isEmpty()){
                    companyUserReportList.add( companyUserReport.getRegionAdmin() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getSsInviteSentDate() != null && !companyUserReport.getSsInviteSentDate().isEmpty()){
                    companyUserReportList.add( companyUserReport.getSsInviteSentDate() );
                }else{
                    companyUserReportList.add( "" );
                }
                companyUserReportList.add( "" );
                if(companyUserReport.getEmailVerified() != null && !companyUserReport.getEmailVerified().isEmpty()){
                    companyUserReportList.add( companyUserReport.getEmailVerified() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLastLoginDate() != null){
                    companyUserReportList.add( companyUserReport.getLastLoginDate() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getProfileComplete() != null && !companyUserReport.getProfileComplete().isEmpty()){
                    companyUserReportList.add( companyUserReport.getProfileComplete() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getSociallyConnected() != null && !companyUserReport.getSociallyConnected().isEmpty()){
                    companyUserReportList.add( companyUserReport.getSociallyConnected() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getFbDataConnection() != null && !companyUserReport.getFbDataConnection().isEmpty()){
                    companyUserReportList.add( companyUserReport.getFbDataConnection() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getFbConnectionStatus() != null && !companyUserReport.getFbConnectionStatus().isEmpty()){
                    companyUserReportList.add( companyUserReport.getFbConnectionStatus() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLastPostDateFb() != null){
                    companyUserReportList.add( companyUserReport.getLastPostDateFb() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getTwitterDataConnection() != null && !companyUserReport.getTwitterDataConnection().isEmpty()){
                    companyUserReportList.add( companyUserReport.getTwitterDataConnection() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getTwitterConnectionStatus() != null && !companyUserReport.getTwitterConnectionStatus().isEmpty()){
                    companyUserReportList.add( companyUserReport.getTwitterConnectionStatus() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLastPostDateTwitter() != null){
                    companyUserReportList.add( companyUserReport.getLastPostDateFb() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLinkedinConnectionStatus() != null && !companyUserReport.getLinkedinConnectionStatus().isEmpty()){
                    companyUserReportList.add( companyUserReport.getLinkedinConnectionStatus() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLinkedinConnectionStatus() != null && !companyUserReport.getLinkedinConnectionStatus().isEmpty()){
                    companyUserReportList.add( companyUserReport.getLinkedinConnectionStatus() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLastPostDateLinkedin() != null){
                    companyUserReportList.add( companyUserReport.getLastPostDateLinkedin() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getGooglePlusUrl() != null && !companyUserReport.getGooglePlusUrl().isEmpty()){
                    companyUserReportList.add( companyUserReport.getGooglePlusUrl() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getZillowUrl() != null && !companyUserReport.getZillowUrl().isEmpty()){
                    companyUserReportList.add( companyUserReport.getZillowUrl() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getYelpUrl() != null && !companyUserReport.getYelpUrl().isEmpty()){
                    companyUserReportList.add( companyUserReport.getYelpUrl() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getRealtorUrl() != null && !companyUserReport.getRealtorUrl().isEmpty()){
                    companyUserReportList.add( companyUserReport.getRealtorUrl() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getGbUrl() != null && !companyUserReport.getGbUrl().isEmpty()){
                    companyUserReportList.add( companyUserReport.getGbUrl() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLendingtreeUrl() != null && !companyUserReport.getLendingtreeUrl().isEmpty()){
                    companyUserReportList.add( companyUserReport.getLendingtreeUrl() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getAdoptionCompletedDate() != null ){
                    companyUserReportList.add( companyUserReport.getAdoptionCompletedDate() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLastSurveySentDate() != null ){
                    companyUserReportList.add( companyUserReport.getLastSurveySentDate() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getLastSurveyPostedDate() != null ){
                    companyUserReportList.add( companyUserReport.getLastSurveyPostedDate() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getAddress() != null && !companyUserReport.getAddress().isEmpty()){
                    companyUserReportList.add( companyUserReport.getAddress() );
                }else{
                    companyUserReportList.add( "" );
                }
                if(companyUserReport.getSsProfile() != null && !companyUserReport.getSsProfile().isEmpty()){
                    companyUserReportList.add( applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + companyUserReport.getSsProfile() );
                }else{
                    companyUserReportList.add( "" );
                }
                companyUserReportList.add( companyUserReport.getTotalReviews() );
                companyUserReportList.add( companyUserReport.getSsReviews() );
                companyUserReportList.add( companyUserReport.getZillowReviews() );
                companyUserReportList.add( companyUserReport.getAbusiveReviews() );
                companyUserReportList.add( companyUserReport.getThirdPartyReviews() );
                companyUser.add( companyUserReportList );
            }
        }
        return companyUser;
        
    }
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public List<List<Object>> getSurveyTransactionReport(Long entityId , String entityType ,Timestamp startDate, Timestamp endDate){
        List<List<Object>> surveyTransaction = new ArrayList<>();
        Calendar calender = Calendar.getInstance();
        int startYear = 0;
        int startMonth = 0;
        int endYear = 0;
        int endMonth = 0;
        if( startDate != null){
            calender.setTime(startDate);
            startYear = calender.get(Calendar.YEAR);
            startMonth = calender.get(Calendar.MONTH) + 1;
        }
        if( endDate != null){

            calender.setTime(endDate);
            endYear = calender.get(Calendar.YEAR);
            endMonth = calender.get(Calendar.MONTH) + 1;
      }
        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )|| entityType.equals( CommonConstants.AGENT_ID_COLUMN )){
            for(SurveyTransactionReport surveyTransactionReport : surveyTransactionReportDao.fetchSurveyTransactionById( entityId,entityType, startYear, startMonth, endYear, endMonth )){
                List<Object> surveyTransactionReportList = new ArrayList<>();
                if(surveyTransactionReport.getUserName() != null && !surveyTransactionReport.getUserName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReport.getUserName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                
                surveyTransactionReportList.add( surveyTransactionReport.getUserId() );
                
                if(surveyTransactionReport.getNmls() != null && !surveyTransactionReport.getNmls().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReport.getNmls() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReport.getLicenseId() != null && !surveyTransactionReport.getLicenseId().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReport.getLicenseId() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReport.getCompanyName() != null && !surveyTransactionReport.getCompanyName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReport.getCompanyName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReport.getRegionName() != null && !surveyTransactionReport.getRegionName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReport.getRegionName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReport.getBranchName() != null && !surveyTransactionReport.getBranchName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReport.getBranchName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                surveyTransactionReportList.add( surveyTransactionReport.getTotalReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotalZillowReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotal_3rdPartyReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotalVerifiedCustomerReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotalUnverifiedCustomerReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotalSocialSurveyReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotalAbusiveReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotalRetakeReviews() );
                surveyTransactionReportList.add( surveyTransactionReport.getTotalRetakeCompleted() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionReceivedBySource() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionSent() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionUnprocessable() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionClicked() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionCompleted_() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionPartiallyCompleted() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionUnopened() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionDuplicates() );
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionMismatched() );     
                surveyTransactionReportList.add( surveyTransactionReport.getTransactionUnassigned() );
                surveyTransaction.add( surveyTransactionReportList );
            }
        }else if(entityType.equals( CommonConstants.REGION_ID_COLUMN )){
            for(SurveyTransactionReportRegion surveyTransactionReportRegion : surveyTransactionReportRegionDao.fetchSurveyTransactionByRegionId( entityId, startYear, startMonth, endYear, endMonth )){
                List<Object> surveyTransactionReportList = new ArrayList<>();
                if(surveyTransactionReportRegion.getUserName() != null && !surveyTransactionReportRegion.getUserName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportRegion.getUserName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                
                surveyTransactionReportList.add( surveyTransactionReportRegion.getUserId() );
                
                if(surveyTransactionReportRegion.getNmls() != null && !surveyTransactionReportRegion.getNmls().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportRegion.getNmls() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportRegion.getLicenseId() != null && !surveyTransactionReportRegion.getLicenseId().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportRegion.getLicenseId() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportRegion.getCompanyName() != null && !surveyTransactionReportRegion.getCompanyName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportRegion.getCompanyName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportRegion.getRegionName() != null && !surveyTransactionReportRegion.getRegionName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportRegion.getRegionName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportRegion.getBranchName() != null && !surveyTransactionReportRegion.getBranchName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportRegion.getBranchName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalZillowReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotal_3rdPartyReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalVerifiedCustomerReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalUnverifiedCustomerReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalSocialSurveyReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalAbusiveReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalRetakeReviews() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTotalRetakeCompleted() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionReceivedBySource() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionSent() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionUnprocessable() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionClicked() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionCompleted_() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionPartiallyCompleted() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionUnopened() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionDuplicates() );
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionMismatched() );     
                surveyTransactionReportList.add( surveyTransactionReportRegion.getTransactionUnassigned() );
                surveyTransaction.add( surveyTransactionReportList );
            }
        }else if(entityType.equals( CommonConstants.BRANCH_ID_COLUMN )){
            for(SurveyTransactionReportBranch surveyTransactionReportBranch : surveyTransactionReportBranchDao.fetchSurveyTransactionByBranchId( entityId, startYear, startMonth, endYear, endMonth )){
                List<Object> surveyTransactionReportList = new ArrayList<>();
                if(surveyTransactionReportBranch.getUserName() != null && !surveyTransactionReportBranch.getUserName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportBranch.getUserName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                
                surveyTransactionReportList.add( surveyTransactionReportBranch.getUserId() );
                
                if(surveyTransactionReportBranch.getNmls() != null && !surveyTransactionReportBranch.getNmls().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportBranch.getNmls() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportBranch.getLicenseId() != null && !surveyTransactionReportBranch.getLicenseId().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportBranch.getLicenseId() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportBranch.getCompanyName() != null && !surveyTransactionReportBranch.getCompanyName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportBranch.getCompanyName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportBranch.getRegionName() != null && !surveyTransactionReportBranch.getRegionName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportBranch.getRegionName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                if(surveyTransactionReportBranch.getBranchName() != null && !surveyTransactionReportBranch.getBranchName().isEmpty()){
                    surveyTransactionReportList.add( surveyTransactionReportBranch.getBranchName() );
                }else{
                    surveyTransactionReportList.add( "" );
                }
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalZillowReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotal_3rdPartyReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalVerifiedCustomerReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalUnverifiedCustomerReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalSocialSurveyReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalAbusiveReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalRetakeReviews() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTotalRetakeCompleted() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionReceivedBySource() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionSent() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionUnprocessable() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionClicked() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionCompleted_() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionPartiallyCompleted() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionUnopened() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionDuplicates() );
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionMismatched() );     
                surveyTransactionReportList.add( surveyTransactionReportBranch.getTransactionUnassigned() );
                surveyTransaction.add( surveyTransactionReportList );
            }
        
        }
        return surveyTransaction;
        
    }
    
    @Override
    public List<List<Object>> getRecentActivityList(Long entityId , String entityType , int startIndex , int batchSize) throws InvalidInputException{
        List<List<Object>> recentActivity = new ArrayList<>();
        for(FileUpload fileUpload : fileUploadDao.findRecentActivityForReporting(entityId, entityType, startIndex, batchSize)){
            List<Object> recentActivityList = new ArrayList<>();
            User user = userManagementService.getUserByUserId( fileUpload.getAdminUserId() );
            recentActivityList.add( fileUpload.getCreatedOn() );
            //Set the ReportName according to the upload type 
            if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT ){
                recentActivityList.add( CommonConstants.REPORTING_SURVEY_STATS_REPORT );
            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_ADOPTION_REPORT){
                recentActivityList.add( CommonConstants.REPORTING_USER_ADOPTION_REPORT );
            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT){
                recentActivityList.add( CommonConstants.REPORTING_COMPANY_USERS_REPORT );
            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_COMPANY_REPORT){
                recentActivityList.add( CommonConstants.REPORTING_SURVEY_REUSLTS_COMPANY_REPORT );
            }else if(fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT){
                recentActivityList.add( CommonConstants.REPORTING_SURVEY_TRANSACTION_REPORT );
            }
            recentActivityList.add( fileUpload.getStartDate() );
            recentActivityList.add( fileUpload.getEndDate() );
            recentActivityList.add( user.getFirstName() );
            recentActivityList.add( user.getLastName() );
            recentActivityList.add( fileUpload.getStatus());
            recentActivityList.add( fileUpload.getFileName() );
            recentActivityList.add( fileUpload.getFileUploadId() );
            recentActivity.add( recentActivityList );
        }
        return recentActivity;
        
    }
    
    @Override
    public Long getRecentActivityCount(Long entityId , String entityType){
        Long Count = null ;
        Count = fileUploadDao.getRecentActivityCountForReporting(entityId, entityType);
        return Count;
   
    }
    
    @Override
    @Transactional
    public void deleteRecentActivity( Long fileUploadId ){
        FileUpload fileUpload = fileUploadDao.findById( FileUpload.class, fileUploadId );
        fileUpload.setShowOnUI( false );
        fileUploadDao.changeShowOnUiStatus( fileUpload );
        
    }
    
    @Override
    public String generateSurveyStatsForReporting(Long entityId , String entityType , Long userId) throws UnsupportedEncodingException, NonFatalException{
        User user = userManagementService.getUserByUserId( userId );
        //file is too big for windows hence uncomment the alternative 
        String fileName = "Survey_Stats_Report-" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadSurveyStatsForReporting( entityId , entityType );
        String LocationInS3 = this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
        return LocationInS3;
        
    }
    
    @SuppressWarnings ( "unchecked")
    public XSSFWorkbook downloadSurveyStatsForReporting( long entityId , String entityType){
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getReportingSurveyStatsReport(entityId,entityType);
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        //String responseString = "[[\"CompanyOnebranchone2017_06\",\"CompanyOne\",\"branchone\",\"2017_06\",6,0,0,0,0,0,0,0,6,0,6,0,100,0],[\"CompanyOnebranchtwo2017_06\",\"CompanyOne\",\"branchtwo\",\"2017_06\",6,0,0,0,0,0,0,0,6,0,6,0,100,0]]";
        //since the string has ""abc"" an extra quote
        responseString = responseString.substring(1, responseString.length()-1);
        //Escape characters
        responseString = StringEscapeUtils.unescapeJava(responseString);
        List<List<String>> surveyStatsReport = null;
        Type listType = new TypeToken <List<List<String>>>() {}.getType();
        surveyStatsReport =  (List<List<String>>) ( new Gson().fromJson(responseString, listType) )  ;
        Map<Integer, List<Object>> data = workbookData.getSurveyStatsReportToBeWrittenInSheet( surveyStatsReport );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        XSSFSheet sheet = workbook.getSheetAt(0);
        this.makeRowBold( workbook, sheet.getRow(0));
        return workbook;
        
    }
    
    @Override
    public String generateUserAdoptionForReporting(Long entityId , String entityType , Long userId) throws UnsupportedEncodingException, NonFatalException{
        User user = userManagementService.getUserByUserId( userId );
        //file is too big for windows hence uncomment the alternative 
        String fileName = "User_Adoption_Report-" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + (Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadUserAdoptionForReporting( entityId , entityType );
        String LocationInS3 = this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
        return LocationInS3;
        
    }
    
    @SuppressWarnings ( "unchecked")
    public XSSFWorkbook downloadUserAdoptionForReporting( long entityId , String entityType){
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getUserAdoption(entityId,entityType);
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        //String responseString = "[[\"CompanyOnebranchone2017_06\",\"CompanyOne\",\"branchone\",\"2017_06\",6,0,0,0,0,0,0,0,6,0,6,0,100,0],[\"CompanyOnebranchtwo2017_06\",\"CompanyOne\",\"branchtwo\",\"2017_06\",6,0,0,0,0,0,0,0,6,0,6,0,100,0]]";
        //since the string has ""abc"" an extra quote
        responseString = responseString.substring(1, responseString.length()-1);
        //Escape characters
        responseString = StringEscapeUtils.unescapeJava(responseString);
        List<List<String>> userAdoptionReport = null;
        Type listType = new TypeToken <List<List<String>>>() {}.getType();
        userAdoptionReport =  (List<List<String>>) ( new Gson().fromJson(responseString, listType) )  ;
        Map<Integer, List<Object>> data = workbookData.getUserAdoptionReportToBeWrittenInSheet( userAdoptionReport );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        return workbook;
        
    }
    
    @Override
    public String generateCompanyUserForReporting(Long entityId , String entityType , Long userId) throws UnsupportedEncodingException, NonFatalException{
        User user = userManagementService.getUserByUserId( userId );
        //file is too big for windows hence uncomment the alternative 
        String fileName = "Company_User_Report" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + (Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadCompanyUserForReporting( entityId , entityType );
        String LocationInS3 = this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
        return LocationInS3;
        
    }
    
    
    @SuppressWarnings ( "unchecked")
    public XSSFWorkbook downloadCompanyUserForReporting( long entityId , String entityType){
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getCompanyUserReport(entityId,entityType);
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        //since the string has ""abc"" an extra quote
        responseString = responseString.substring(1, responseString.length()-1);
        //Escape characters
        responseString = StringEscapeUtils.unescapeJava(responseString);
        List<List<String>> companyUserReport = null;
        Type listType = new TypeToken <List<List<String>>>() {}.getType();
        companyUserReport =  (List<List<String>>) ( new Gson().fromJson(responseString, listType) )  ;
        Map<Integer, List<Object>> data = workbookData.getCompanyUserReportToBeWrittenInSheet( companyUserReport );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        return workbook;
        
    }
    
    @Override
    public String generateSurveyResultsCompanyForReporting(Long entityId , String entityType , Long userId,Timestamp startDate, Timestamp endDate) throws UnsupportedEncodingException, NonFatalException{
    	User user = userManagementService.getUserByUserId( userId );
    	String fileName = "Survey_Results_Company_Report"+entityType+"-"+user.getFirstName()+"_"+user.getLastName()+"-"
    			+ (Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
    	 XSSFWorkbook workbook = this.downloadSurveyResultsCompanyForReporting( entityId , entityType,startDate,endDate );
         String LocationInS3 = this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
         return LocationInS3;
    }
        
    @SuppressWarnings ( "unchecked")
    public XSSFWorkbook downloadSurveyResultsCompanyForReporting(long entityId,String entityType,Timestamp startDate, Timestamp endDate){
    	Response response =  ssApiBatchIntergrationBuilder.getIntegrationApi().getSurveyResultsCompany(entityId, entityType,startDate,endDate);
    	 String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
         //since the string has ""abc"" an extra quote
         responseString = responseString.substring(1, responseString.length()-1);
         //Escape characters
         responseString = StringEscapeUtils.unescapeJava(responseString);
         List<List<String>> surveyResultsCompanyReport = null;
         Type listType = new TypeToken <List<List<String>>>() {}.getType();
         surveyResultsCompanyReport =  (List<List<String>>) ( new Gson().fromJson(responseString, listType) )  ;
         Map<Integer, List<Object>> data = workbookData.getSurveyResultsCompanyReportToBeWrittenInSheet( surveyResultsCompanyReport );
         XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
         XSSFSheet sheet = workbook.getSheetAt(0);
         this.makeRowBold( workbook, sheet.getRow(0));
         return workbook;
    	
    }
    
    @Override
    public String generateSurveyTransactionForReporting(Long entityId , String entityType , Long userId ,Timestamp startDate, Timestamp endDate) throws UnsupportedEncodingException, NonFatalException{
        User user = userManagementService.getUserByUserId( userId );
        //file is too big for windows hence uncomment the alternative 
        String fileName = "Survey_Transaction_Report" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + (Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadSurveyTransactionForReporting( entityId , entityType, startDate, endDate );
        String LocationInS3 = this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
        return LocationInS3;
        
    }
    
    
    @SuppressWarnings ( "unchecked")
    public XSSFWorkbook downloadSurveyTransactionForReporting( long entityId , String entityType ,Timestamp startDate, Timestamp endDate){
        Response response =  ssApiBatchIntergrationBuilder.getIntegrationApi().getSurveyTransactionReport(entityId, entityType,startDate,endDate);
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        //since the string has ""abc"" an extra quote
        responseString = responseString.substring(1, responseString.length()-1);
        //Escape characters
        responseString = StringEscapeUtils.unescapeJava(responseString);
        List<List<String>> surveyTransactionReport = null;
        Type listType = new TypeToken <List<List<String>>>() {}.getType();
        surveyTransactionReport =  (List<List<String>>) ( new Gson().fromJson(responseString, listType) )  ;
        Map<Integer, List<Object>> data = workbookData.getSurveyTransactionReportToBeWrittenInSheet(surveyTransactionReport);
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        return workbook;
        
    }
    
    private String createExcelFileAndSaveInAmazonS3( String fileName, XSSFWorkbook workbook ) throws NonFatalException, UnsupportedEncodingException
    {
        // Create file and write report into it
        boolean excelCreated = false;
        FileOutputStream fileOutput = null;
        InputStream inputStream = null;
        File file = null;
        String filePath = null;
        String responseString = null;
        try {
            file = new File( fileDirectoryLocation + File.separator + fileName );
            file.createNewFile();
            fileOutput = new FileOutputStream( file );
            workbook.write( fileOutput );
            filePath = file.getPath();
            excelCreated = true;
        } catch ( FileNotFoundException fe ) {
            LOG.error( "Exception caught while generating report " + fileName + ": " + fe.getMessage() );
            excelCreated = false;
        } catch ( IOException e ) {
            LOG.error( "Exception caught while generating report " + fileName + ": " + e.getMessage() );
            excelCreated = false;
        } finally {
            try {
                if ( fileOutput != null )
                    fileOutput.close();
                if ( inputStream != null ) {
                    inputStream.close();
                }
            } catch ( IOException e ) {
                LOG.error( "Exception caught while generating report " + fileName + ": " + e.getMessage() );
                excelCreated = false;
            }
        }

        // SAVE REPORT IN S3
        if ( excelCreated ) {
            fileUploadService.uploadFileAtSpeicifiedBucket( file, fileName, bucketName, false );;
            String fileNameInS3 = endpoint + CommonConstants.FILE_SEPARATOR + URLEncoder.encode( fileName, "UTF-8" );
            responseString = fileNameInS3;
        }
        return responseString;
    }
    
    //Make Header Row Bold
    public static void makeRowBold(XSSFWorkbook wb, Row row){
        CellStyle style = wb.createCellStyle();//Create style
        Font font = wb.createFont();//Create font
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
        style.setFont(font);//set it to bold

        for(int i = 0; i < row.getLastCellNum(); i++){//For each cell in the row 
            row.getCell(i).setCellStyle(style);//Set the sty;e
        }
    }

    
    
}