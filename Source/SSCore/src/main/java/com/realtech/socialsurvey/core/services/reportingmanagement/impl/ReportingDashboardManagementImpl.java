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
import com.realtech.socialsurvey.core.dao.FileUploadDao;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportBranchDao;
import com.realtech.socialsurvey.core.dao.UserAdoptionReportDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
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
    
    @Override
    @Transactional(value = "transactionManagerForReporting")
    public List<List<Object>> getUserAdoptionReport(Long entityId , String entityType){
        List<List<Object>> userAdoption = new ArrayList<>();
        if(entityType.equals( CommonConstants.COMPANY_ID_COLUMN )){
            for(UserAdoptionReport UserAdoptionReport : userAdoptionReportDao.fetchUserAdoptionByCompanyId(entityId) ){
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
        }
        return userAdoption;
        
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
        //and since the string doesnt eliminate the escaped characters
        responseString = responseString.replace( "\\", "" );
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
        //and since the string doesnt eliminate the escaped characters
        responseString = responseString.replace( "\\", "" );
        List<List<String>> userAdoptionReport = null;
        Type listType = new TypeToken <List<List<String>>>() {}.getType();
        userAdoptionReport =  (List<List<String>>) ( new Gson().fromJson(responseString, listType) )  ;
        Map<Integer, List<Object>> data = workbookData.getUserAdoptionReportToBeWrittenInSheet( userAdoptionReport );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        XSSFSheet sheet = workbook.getSheetAt(0);
        this.makeRowBold( workbook, sheet.getRow(0));
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