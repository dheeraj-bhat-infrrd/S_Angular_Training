package com.realtech.socialsurvey.core.services.reportingmanagement.impl;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.api.builder.SSApiBatchIntegrationBuilder;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.BranchRankingReportMonthDao;
import com.realtech.socialsurvey.core.dao.BranchRankingReportYearDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.CompanyDetailsReportDao;
import com.realtech.socialsurvey.core.dao.CompanyUserReportDao;
import com.realtech.socialsurvey.core.dao.FileUploadDao;
import com.realtech.socialsurvey.core.dao.GenericReportingDao;
import com.realtech.socialsurvey.core.dao.NpsReportMonthDao;
import com.realtech.socialsurvey.core.dao.NpsReportWeekDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.ReportingSurveyPreInititationDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallBranchDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallCompanyDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallRegionDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsOverallUserDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionBranchDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionCompanyDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionRegionDao;
import com.realtech.socialsurvey.core.dao.ScoreStatsQuestionUserDao;
import com.realtech.socialsurvey.core.dao.SurveyInvitationEmailDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.SurveyResponseTableDao;
import com.realtech.socialsurvey.core.dao.SurveyResultsCompanyReportDao;
import com.realtech.socialsurvey.core.dao.SurveyResultsReportBranchDao;
import com.realtech.socialsurvey.core.dao.SurveyResultsReportRegionDao;
import com.realtech.socialsurvey.core.dao.SurveyStatsReportBranchDao;
import com.realtech.socialsurvey.core.dao.UserAdoptionReportDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthBranchDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthMainDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastMonthRegionDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearBranchDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearMainDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearRegionDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsBranchDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsMainDao;
import com.realtech.socialsurvey.core.dao.UserRankingPastYearsRegionDao;
import com.realtech.socialsurvey.core.dao.UserRankingThisMonthBranchDao;
import com.realtech.socialsurvey.core.dao.UserRankingThisMonthMainDao;
import com.realtech.socialsurvey.core.dao.UserRankingThisMonthRegionDao;
import com.realtech.socialsurvey.core.dao.UserRankingThisYearBranchDao;
import com.realtech.socialsurvey.core.dao.UserRankingThisYearMainDao;
import com.realtech.socialsurvey.core.dao.UserRankingThisYearRegionDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchRankingReportMonth;
import com.realtech.socialsurvey.core.entities.BranchRankingReportYear;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanyDetailsReport;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.CompanyUserReport;
import com.realtech.socialsurvey.core.entities.CompanyView;
import com.realtech.socialsurvey.core.entities.Digest;
import com.realtech.socialsurvey.core.entities.DigestRequestData;
import com.realtech.socialsurvey.core.entities.DigestTemplateData;
import com.realtech.socialsurvey.core.entities.EntityAlertDetails;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.GenericReportingObject;
import com.realtech.socialsurvey.core.entities.MonthlyDigestAggregate;
import com.realtech.socialsurvey.core.entities.NpsReportMonth;
import com.realtech.socialsurvey.core.entities.NpsReportWeek;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RankingRequirements;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.ReportRequest;
import com.realtech.socialsurvey.core.entities.ReportingSurveyPreInititation;
import com.realtech.socialsurvey.core.entities.SavedDigestRecord;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallBranch;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallCompany;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallRegion;
import com.realtech.socialsurvey.core.entities.ScoreStatsOverallUser;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionBranch;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionCompany;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionRegion;
import com.realtech.socialsurvey.core.entities.ScoreStatsQuestionUser;
import com.realtech.socialsurvey.core.entities.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportRegion;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportVO;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReport;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReportBranch;
import com.realtech.socialsurvey.core.entities.SurveyTransactionReportRegion;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserAdoptionReport;
import com.realtech.socialsurvey.core.entities.UserRanking;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingPastMonthRegion;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearMain;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearRegion;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsBranch;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsMain;
import com.realtech.socialsurvey.core.entities.UserRankingPastYearsRegion;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisMonthRegion;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearBranch;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearMain;
import com.realtech.socialsurvey.core.entities.UserRankingThisYearRegion;
import com.realtech.socialsurvey.core.enums.EntityErrorAlertType;
import com.realtech.socialsurvey.core.enums.EntityWarningAlertType;
import com.realtech.socialsurvey.core.enums.ReportType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.mail.impl.DigestMailHelper;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.reportingmanagement.OverviewManagement;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.CommonUtils;
import com.realtech.socialsurvey.core.vo.SurveyInvitationEmailCountVO;
import com.realtech.socialsurvey.core.vo.SurveyTransactionReportVO;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookData;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookOperations;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@Component
public class ReportingDashboardManagementImpl<K> implements ReportingDashboardManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( ReportingDashboardManagementImpl.class );

    public static final String COUNT = "Count";
    public static final String STARTINDEX = "startIndex";

    @Autowired
    private FileUploadDao fileUploadDao;

    @Autowired
    private SurveyStatsReportBranchDao surveyStatsReportBranchDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private UserAdoptionReportDao userAdoptionReportDao;

    @Autowired
    private SurveyResultsCompanyReportDao surveyResultsCompanyReportDao;

    @Autowired
    private SurveyResultsReportRegionDao surveyResultsReportRegionDao;

    @Autowired
    private SurveyResultsReportBranchDao surveyResultsReportBranchDao;

    @Autowired
    private SurveyResponseTableDao surveyResponseTableDao;

    @Autowired
    private CompanyUserReportDao companyUserReportDao;

    @Autowired
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CompanyDetailsReportDao companyDetailsReportDao;

    @Autowired
    private SSApiBatchIntegrationBuilder ssApiBatchIntergrationBuilder;

    @Autowired
    private WorkbookData workbookData;

    @Autowired
    private WorkbookOperations workbookOperations;

    @Autowired
    private UserRankingThisYearRegionDao userRankingThisYearRegionDao;

    @Autowired
    private UserRankingThisMonthRegionDao userRankingThisMonthRegionDao;

    @Autowired
    private UserRankingPastYearRegionDao userRankingPastYearRegionDao;

    @Autowired
    private UserRankingPastMonthRegionDao userRankingPastMonthRegionDao;

    @Autowired
    private UserRankingThisYearMainDao userRankingThisYearMainDao;

    @Autowired
    private UserRankingThisMonthMainDao userRankingThisMonthMainDao;

    @Autowired
    private UserRankingPastYearMainDao userRankingPastYearMainDao;

    @Autowired
    private UserRankingPastMonthMainDao userRankingPastMonthMainDao;

    @Autowired
    private UserRankingThisYearBranchDao userRankingThisYearBranchDao;

    @Autowired
    private UserRankingThisMonthBranchDao userRankingThisMonthBranchDao;

    @Autowired
    private UserRankingPastYearBranchDao userRankingPastYearBranchDao;

    @Autowired
    private UserRankingPastMonthBranchDao userRankingPastMonthBranchDao;

    @Autowired
    private UserRankingPastYearsMainDao userRankingPastYearsMainDao;

    @Autowired
    private UserRankingPastYearsBranchDao userRankingPastYearsBranchDao;

    @Autowired
    private UserRankingPastYearsRegionDao userRankingPastYearsRegionDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private ScoreStatsOverallCompanyDao scoreStatsOverallCompanyDao;

    @Autowired
    private ScoreStatsOverallRegionDao scoreStatsOverallRegionDao;

    @Autowired
    private ScoreStatsOverallBranchDao scoreStatsOverallBranchDao;

    @Autowired
    private ScoreStatsOverallUserDao scoreStatsOverallUserDao;

    @Autowired
    private ScoreStatsQuestionCompanyDao scoreStatsQuestionCompanyDao;

    @Autowired
    private ScoreStatsQuestionRegionDao scoreStatsQuestionRegionDao;

    @Autowired
    private ScoreStatsQuestionBranchDao scoreStatsQuestionBranchDao;

    @Autowired
    private ScoreStatsQuestionUserDao scoreStatsQuestionUserDao;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private ReportingSurveyPreInititationDao reportingSurveyPreInititationDao;

    @Autowired
    private SurveyInvitationEmailDao surveyInvitationEmailDao;

    @Autowired
    private NpsReportWeekDao npsReportWeekDao;

    @Autowired
    private NpsReportMonthDao npsReportMonthDao;

    @Autowired
    private BranchRankingReportMonthDao branchRankingMonthDao;

    @Autowired
    private BranchRankingReportYearDao branchRankingYearDao;

    @Autowired
    private Utils utils;

    @Autowired
    private OverviewManagement overviewManagement;
    
    @Autowired
    private SurveyPreInitiationDao surveyPreInitiationDao;
    
    @Autowired
    @Qualifier("genericReporting")
    private GenericReportingDao<SurveyTransactionReport, Long> surveyTransactionReportDao;
    
    @Autowired
    @Qualifier("genericReporting")
    private GenericReportingDao<SurveyTransactionReportRegion, Long> surveyTransactionRegionReportDao;
    
    @Autowired
    @Qualifier("genericReporting")
    private GenericReportingDao<SurveyTransactionReportBranch, Long> surveyTransactionBranchReportDao;

    @Autowired
    private DigestMailHelper digestMailHelper;
    
    @Autowired
    private StreamApiIntegrationBuilder streamApiIntegrationBuilder;

    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;

    @Value ( "${CDN_PATH}")
    private String endpoint;

    @Value ( "${AMAZON_BUCKET}")
    private String bucket;

    @Value ( "${AMAZON_REPORTS_BUCKET}")
    private String reportBucket;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${TRANSACTION_MONITOR_SUPPORT_EMAIL}")
    private String transactionMonitorSupportEmail;

    @Value ( "${SEND_DIGEST_TO_APPLICATION_ADMIN_ONLY}")
    private String sendDigestToApplicationAdminOnly;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;
    
    @Value ( "${AMAZON_DIGEST_BUCKET}")
    private String digestBucket;

    public static final int DIGEST_MAIL_START_INDEX = 0;

    public static final int DIGEST_MAIL_BATCH_SIZE = 50;

    public static final int NUMBER_OF_DAYS = 3;
    
    public static final int NUMBER_OF_DAYS_SM_REPORT = 7;


    @Override
    public void createEntryInFileUploadForReporting( int reportId, Date startDate, Date endDate, Long entityId,
        String entityType, Company company, Long adminUserId, int actualTimeZoneOffset, GenericReportingObject genericReportingObject )
        throws InvalidInputException, NoRecordsFetchedException, IOException
    {
        // adding entry in the feild and set status to pending
        LOG.info( "method to insert data into the generateReportList and save in aws server" );
        // input value into the generateReportList table
        FileUpload fileUpload = new FileUpload();

        fileUpload.setCompany( company );
        if ( adminUserId != null ) {
            fileUpload.setAdminUserId( adminUserId );

        }
        fileUpload.setFileName( " " );
        fileUpload.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_SURVEY_INVITATION_EMAIL_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_SURVEY_INVITATION_EMAIL_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_DIGEST ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_REPORTING_DIGEST );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT );
        } else if ( reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD ) {
            fileUpload.setUploadType( CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD );
        }

        // get the time 23:59:59 in milliseconds
        long duration = ( ( ( 23 * 60 ) * 60 ) + ( 59 * 60 ) + 59 ) * 1000l;

        if ( startDate != null ) {
            fileUpload.setStartDate( new Timestamp( startDate.getTime() ) );
        }
        if ( endDate != null ) {
            Timestamp endTimeStamp = new Timestamp( endDate.getTime() );
            // add the the duration(23:59:59 in milliseconds) to the current endDate
            // timestamp and store it
            endTimeStamp.setTime( endTimeStamp.getTime() + duration );
            fileUpload.setEndDate( endTimeStamp );
        }
        fileUpload.setProfileValue( entityId );
        fileUpload.setProfileLevel( entityType );
        fileUpload.setStatus( CommonConstants.STATUS_PENDING );
        fileUpload.setShowOnUI( true );

        // get digest s3 URI from mongoDB if present
        if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_DIGEST ) {
            processDigestRequest( fileUpload );
        }
        
        //Social Monitor reports
        ReportRequest socialMonitorReportRequest = new ReportRequest();
        if ( reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT
            || reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD ) {
            ReportRequest reportRequest = new ReportRequest();
            if ( reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD ) {
                reportRequest.setReportType( ReportType.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD.getName() );
                reportRequest.setKeyword( genericReportingObject.getKeyword() );
            }
            if ( reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT ) {
                reportRequest.setReportType( ReportType.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT.getName() );
            }
            if ( startDate != null && endDate != null ) {
                reportRequest.setStartTime( fileUpload.getStartDate().getTime() );
                reportRequest.setEndTime( fileUpload.getEndDate().getTime() );
            } else if ( startDate != null && endDate == null ) {
                reportRequest.setStartTime( fileUpload.getStartDate().getTime() );
                reportRequest.setEndTime( new DateTime().withTimeAtStartOfDay().plusDays( 1 ).minusSeconds( 1 ).getMillis() );
                fileUpload.setEndDate( new Timestamp(new DateTime().withTimeAtStartOfDay().plusDays( 1 ).minusSeconds( 1 ).getMillis()));
            } else if ( startDate == null && endDate != null ) {
                reportRequest.setStartTime( endDate.getTime() - CommonUtils.daysToMilliseconds( NUMBER_OF_DAYS_SM_REPORT ) );
                reportRequest.setEndTime( fileUpload.getEndDate().getTime() );
                fileUpload.setStartDate(
                    new Timestamp( endDate.getTime() - CommonUtils.daysToMilliseconds( NUMBER_OF_DAYS_SM_REPORT ) ) );
            } else if ( startDate == null && endDate == null ) {
                reportRequest.setStartTime( CommonUtils.lastNdaysTimestamp( NUMBER_OF_DAYS_SM_REPORT ) );
                reportRequest.setEndTime( new DateTime().withTimeAtStartOfDay().plusDays( 1 ).minusSeconds( 1 ).getMillis() );
                fileUpload.setStartDate( new Timestamp( CommonUtils.lastNdaysTimestamp( NUMBER_OF_DAYS_SM_REPORT ) ) );
                fileUpload.setEndDate( new Timestamp( new DateTime().withTimeAtStartOfDay().plusDays( 1 ).minusSeconds( 1 ).getMillis() ) );

            }
            reportRequest.setCompanyId(fileUpload.getCompany().getCompanyId());
            socialMonitorReportRequest  = reportRequest;
        }

        fileUpload = fileUploadDao.save( fileUpload );
        
		if (reportId == CommonConstants.FILE_UPLOAD_SURVEY_INVITATION_EMAIL_REPORT) {
			ReportRequest reportRequest = new ReportRequest();
			String timeFrame = CommonConstants.TIME_FRAME_ALL_TIME;
			if (fileUpload.getStartDate() != null) {
				if (fileUpload.getStartDate().getTime() == getFirstDayOfThisMonthTime()) {
					timeFrame = CommonConstants.TIME_FRAME_THIS_MONTH;
				} else if (fileUpload.getStartDate().getTime() == getFirstDayOfPastMonthTime()) {
					timeFrame = CommonConstants.TIME_FRAME_PAST_MONTH;
				}
			}
			reportRequest.transform(timeFrame);
			reportRequest.setFileUploadId(fileUpload.getFileUploadId());
			reportRequest.setCompanyId(fileUpload.getCompany().getCompanyId());
			streamApiIntegrationBuilder.getStreamApi().generateEmailReport(reportRequest);
		}else if ( reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT || reportId == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD ) {
		    socialMonitorReportRequest.setFileUploadId(fileUpload.getFileUploadId());
		    try {
	            streamApiIntegrationBuilder.getStreamApi().generateEmailReport(socialMonitorReportRequest);
		    } catch(StreamApiException | StreamApiConnectException e) {
		        LOG.error( "Could not stream social monitor report", e );
		        fileUpload.setStatus( CommonConstants.STATUS_FAIL );
		        fileUploadDao.saveOrUpdate( fileUpload );
		    }
		}else {
			try {
	            streamApiIntegrationBuilder.getStreamApi().generateEmailReport(socialMonitorReportRequest);
		    } catch(StreamApiException | StreamApiConnectException e) {
		        LOG.error( "Could not stream  report", e );
		        fileUpload.setStatus( CommonConstants.STATUS_FAIL );
		        fileUploadDao.saveOrUpdate( fileUpload );
		    }
		}
    }


    private long getFirstDayOfPastMonthTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM,Calendar.AM);
		cal.add(Calendar.MONTH, -1);
		return cal.getTimeInMillis();
	}


	private long getFirstDayOfThisMonthTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM,Calendar.AM);
		return cal.getTimeInMillis();
	}


	/*
     * Generate report from the surveyStats Table
     * 
     */
    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public List<List<Object>> getSurveyStatsReport( Long entityId, String entityType )
    {

        List<List<Object>> surveyStats = new ArrayList<>();

        for ( SurveyStatsReportBranch SurveyStatsReportCompany : surveyStatsReportBranchDao.fetchSurveyStatsById( entityId,
            entityType ) ) {
            List<Object> surveyStatsReportToPopulate = new ArrayList<>();
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getId() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getCompanyName() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getBranchName() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getTrxMonth() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getTrxRcvd() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getPending() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getDuplicates() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getCorrupted() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getAbusive() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getOldRecords() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getIgnored() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getMismatched() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getSentCount() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getClickedCount() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getCompleted() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getPartiallyCompleted() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getCompletePercentage() );
            surveyStatsReportToPopulate.add( SurveyStatsReportCompany.getDelta() );
            surveyStats.add( surveyStatsReportToPopulate );
        }

        return surveyStats;

    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public List<List<Object>> getUserAdoptionReport( Long entityId, String entityType )
    {
        List<List<Object>> userAdoption = new ArrayList<>();
        for ( UserAdoptionReport UserAdoptionReport : userAdoptionReportDao.fetchUserAdoptionById( entityId, entityType ) ) {
            List<Object> userAdoptionReportList = new ArrayList<>();
            userAdoptionReportList.add( UserAdoptionReport.getCompanyName() );
            if ( UserAdoptionReport.getRegionName() != null && !UserAdoptionReport.getRegionName().isEmpty() ) {
                userAdoptionReportList.add( UserAdoptionReport.getRegionName() );
            } else {
                userAdoptionReportList.add( "" );
            }
            if ( UserAdoptionReport.getBranchName() != null && !UserAdoptionReport.getBranchName().isEmpty() ) {
                userAdoptionReportList.add( UserAdoptionReport.getBranchName() );
            } else {
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
    @Transactional ( value = "transactionManagerForReporting")
    public int getMaxQuestionForSurveyResultsReport( String entityType, Long entityId, Timestamp startDate, Timestamp endDate )
    {
        LOG.debug( "method getMaxQuestionForSurveyResultsReport started for entityId : {}", entityId );

        if ( entityType.equals( CommonConstants.COMPANY_ID ) ) {
            return surveyResponseTableDao.getMaxResponseForCompanyId( entityId, startDate, endDate );
        } else if ( entityType.equals( CommonConstants.REGION_ID ) ) {
            return surveyResponseTableDao.getMaxResponseForRegionId( entityId, startDate, endDate );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID ) ) {
            return surveyResponseTableDao.getMaxResponseForBranchId( entityId, startDate, endDate );
        } else if ( entityType.equals( CommonConstants.AGENT_ID ) ) {
            return surveyResponseTableDao.getMaxResponseForUserId( entityId, startDate, endDate );
        }
        return 0;

    }


    /**
     * This method accepts SurveyResultsReport data with entity type and assigns it
     * to common VO for SurveyResultsReport.
     * 
     * @param surveyResultsReportObject
     * @param type
     * @return
     */
    private Map<String, SurveyResultsReportVO> assignToVO( Map<String, ?> surveyResultsReportObject, String type )
    {
        Map<String, SurveyResultsReportVO> surveyResultsReportVOMap = new HashMap<String, SurveyResultsReportVO>();
        SurveyResultsCompanyReport surveyResultsCompanyReport = null;
        SurveyResultsReportRegion surveyResultsReportRegion = null;
        SurveyResultsReportBranch surveyResultsReportBranch = null;
        for ( Entry<String, ?> entry : surveyResultsReportObject.entrySet() ) {
            String surveyDetailsId = entry.getKey();
            SurveyResultsReportVO surveyResultsReportVO = new SurveyResultsReportVO();
            if ( type.equals( CommonConstants.COMPANY_ID ) || type.equals( CommonConstants.AGENT_ID ) ) {
                surveyResultsCompanyReport = (SurveyResultsCompanyReport) entry.getValue();
                surveyResultsReportVO.setSurveyDetailsId( surveyResultsCompanyReport.getSurveyDetailsId() );
                surveyResultsReportVO.setUserFirstName( surveyResultsCompanyReport.getUserFirstName() );
                surveyResultsReportVO.setUserLastName( surveyResultsCompanyReport.getUserLastName() );
                surveyResultsReportVO.setCustomerFirstName( surveyResultsCompanyReport.getCustomerFirstName() );
                surveyResultsReportVO.setCustomerLastName( surveyResultsCompanyReport.getCustomerLastName() );
                surveyResultsReportVO.setSurveySentDate( surveyResultsCompanyReport.getSurveySentDate() );
                surveyResultsReportVO.setSurveyCompletedDate( surveyResultsCompanyReport.getSurveyCompletedDate() );
                surveyResultsReportVO.setTimeInterval( surveyResultsCompanyReport.getTimeInterval() );
                surveyResultsReportVO.setSurveySource( surveyResultsCompanyReport.getSurveySource() );
                surveyResultsReportVO.setSurveySourceId( surveyResultsCompanyReport.getSurveySourceId() );
                surveyResultsReportVO.setSurveyScore( surveyResultsCompanyReport.getSurveyScore() );
                surveyResultsReportVO.setGateway( surveyResultsCompanyReport.getGateway() );
                surveyResultsReportVO.setCustomerComments( surveyResultsCompanyReport.getCustomerComments() );
                surveyResultsReportVO.setAgreedToShare( surveyResultsCompanyReport.getAgreedToShare() );
                surveyResultsReportVO.setBranchName( surveyResultsCompanyReport.getBranchName() );
                surveyResultsReportVO.setClickTroughForCompany( surveyResultsCompanyReport.getClickTroughForCompany() );
                surveyResultsReportVO.setClickTroughForAgent( surveyResultsCompanyReport.getClickTroughForAgent() );
                surveyResultsReportVO.setClickTroughForRegion( surveyResultsCompanyReport.getClickTroughForRegion() );
                surveyResultsReportVO.setClickTroughForBranch( surveyResultsCompanyReport.getClickTroughForBranch() );
                surveyResultsReportVO.setSurveyResponseList( surveyResultsCompanyReport.getSurveyResponseList() );
                surveyResultsReportVO.setParticipantType( surveyResultsCompanyReport.getParticipantType() );
                surveyResultsReportVO.setAgentEmailId( surveyResultsCompanyReport.getAgentEmailId() );
                surveyResultsReportVO.setCustomerEmailId( surveyResultsCompanyReport.getCustomerEmailId() );
                surveyResultsReportVO.setState( surveyResultsCompanyReport.getState() );
                surveyResultsReportVO.setCity( surveyResultsCompanyReport.getCity() );
            } else if ( type.equals( CommonConstants.REGION_ID ) ) {
                surveyResultsReportRegion = (SurveyResultsReportRegion) entry.getValue();
                surveyResultsReportVO.setSurveyDetailsId( surveyResultsReportRegion.getSurveyDetailsId() );
                surveyResultsReportVO.setUserFirstName( surveyResultsReportRegion.getUserFirstName() );
                surveyResultsReportVO.setUserLastName( surveyResultsReportRegion.getUserLastName() );
                surveyResultsReportVO.setCustomerFirstName( surveyResultsReportRegion.getCustomerFirstName() );
                surveyResultsReportVO.setCustomerLastName( surveyResultsReportRegion.getCustomerLastName() );
                surveyResultsReportVO.setSurveySentDate( surveyResultsReportRegion.getSurveySentDate() );
                surveyResultsReportVO.setSurveyCompletedDate( surveyResultsReportRegion.getSurveyCompletedDate() );
                surveyResultsReportVO.setTimeInterval( surveyResultsReportRegion.getTimeInterval() );
                surveyResultsReportVO.setSurveySource( surveyResultsReportRegion.getSurveySource() );
                surveyResultsReportVO.setSurveySourceId( surveyResultsReportRegion.getSurveySourceId() );
                surveyResultsReportVO.setSurveyScore( surveyResultsReportRegion.getSurveyScore() );
                surveyResultsReportVO.setGateway( surveyResultsReportRegion.getGateway() );
                surveyResultsReportVO.setCustomerComments( surveyResultsReportRegion.getCustomerComments() );
                surveyResultsReportVO.setAgreedToShare( surveyResultsReportRegion.getAgreedToShare() );
                surveyResultsReportVO.setBranchName( surveyResultsReportRegion.getBranchName() );
                surveyResultsReportVO.setClickTroughForCompany( surveyResultsReportRegion.getClickTroughForCompany() );
                surveyResultsReportVO.setClickTroughForAgent( surveyResultsReportRegion.getClickTroughForAgent() );
                surveyResultsReportVO.setClickTroughForRegion( surveyResultsReportRegion.getClickTroughForRegion() );
                surveyResultsReportVO.setClickTroughForBranch( surveyResultsReportRegion.getClickTroughForBranch() );
                surveyResultsReportVO.setSurveyResponseList( surveyResultsReportRegion.getSurveyResponseList() );
                surveyResultsReportVO.setParticipantType( surveyResultsReportRegion.getParticipantType() );
                surveyResultsReportVO.setAgentEmailId( surveyResultsReportRegion.getAgentEmailId() );
                surveyResultsReportVO.setCustomerEmailId( surveyResultsReportRegion.getCustomerEmailId() );
                surveyResultsReportVO.setState( surveyResultsReportRegion.getState() );
                surveyResultsReportVO.setCity( surveyResultsReportRegion.getCity() );
            } else if ( type.equals( CommonConstants.BRANCH_ID ) ) {
                surveyResultsReportBranch = (SurveyResultsReportBranch) entry.getValue();
                surveyResultsReportVO.setSurveyDetailsId( surveyResultsReportBranch.getSurveyDetailsId() );
                surveyResultsReportVO.setUserFirstName( surveyResultsReportBranch.getUserFirstName() );
                surveyResultsReportVO.setUserLastName( surveyResultsReportBranch.getUserLastName() );
                surveyResultsReportVO.setCustomerFirstName( surveyResultsReportBranch.getCustomerFirstName() );
                surveyResultsReportVO.setCustomerLastName( surveyResultsReportBranch.getCustomerLastName() );
                surveyResultsReportVO.setSurveySentDate( surveyResultsReportBranch.getSurveySentDate() );
                surveyResultsReportVO.setSurveyCompletedDate( surveyResultsReportBranch.getSurveyCompletedDate() );
                surveyResultsReportVO.setTimeInterval( surveyResultsReportBranch.getTimeInterval() );
                surveyResultsReportVO.setSurveySource( surveyResultsReportBranch.getSurveySource() );
                surveyResultsReportVO.setSurveySourceId( surveyResultsReportBranch.getSurveySourceId() );
                surveyResultsReportVO.setSurveyScore( surveyResultsReportBranch.getSurveyScore() );
                surveyResultsReportVO.setGateway( surveyResultsReportBranch.getGateway() );
                surveyResultsReportVO.setCustomerComments( surveyResultsReportBranch.getCustomerComments() );
                surveyResultsReportVO.setAgreedToShare( surveyResultsReportBranch.getAgreedToShare() );
                surveyResultsReportVO.setBranchName( surveyResultsReportBranch.getBranchName() );
                surveyResultsReportVO.setClickTroughForCompany( surveyResultsReportBranch.getClickTroughForCompany() );
                surveyResultsReportVO.setClickTroughForAgent( surveyResultsReportBranch.getClickTroughForAgent() );
                surveyResultsReportVO.setClickTroughForRegion( surveyResultsReportBranch.getClickTroughForRegion() );
                surveyResultsReportVO.setClickTroughForBranch( surveyResultsReportBranch.getClickTroughForBranch() );
                surveyResultsReportVO.setSurveyResponseList( surveyResultsReportBranch.getSurveyResponseList() );
                surveyResultsReportVO.setParticipantType( surveyResultsReportBranch.getParticipantType() );
                surveyResultsReportVO.setAgentEmailId( surveyResultsReportBranch.getAgentEmailId() );
                surveyResultsReportVO.setCustomerEmailId( surveyResultsReportBranch.getCustomerEmailId() );
                surveyResultsReportVO.setState( surveyResultsReportBranch.getState() );
                surveyResultsReportVO.setCity( surveyResultsReportBranch.getCity() );
            }
            surveyResultsReportVOMap.put( surveyDetailsId, surveyResultsReportVO );
        }
        return surveyResultsReportVOMap;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, SurveyResultsReportVO> getSurveyResultsReport( String entityType, Long entityId, Timestamp startDate,
        Timestamp endDate, int startIndex, int batchSize )
    {
        LOG.debug( " getSurveyResultsReport method started for entityId : {} ", entityId );
        Map<String, SurveyResultsReportVO> surveyResultsReportVO = null;
        if ( entityType.equals( CommonConstants.COMPANY_ID ) ) {
            Map<String, SurveyResultsCompanyReport> surveyResultsCompanyReport = surveyResultsCompanyReportDao
                .getSurveyResultForCompanyId( entityId, startDate, endDate, startIndex, batchSize );
            surveyResultsReportVO = assignToVO( surveyResultsCompanyReport, entityType );
        } else if ( entityType.equals( CommonConstants.REGION_ID ) ) {
            Map<String, SurveyResultsReportRegion> surveyResultsReportRegion = surveyResultsReportRegionDao
                .getSurveyResultForRegionId( entityId, startDate, endDate, startIndex, batchSize );
            surveyResultsReportVO = assignToVO( surveyResultsReportRegion, entityType );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID ) ) {
            Map<String, SurveyResultsReportBranch> surveyResultsReportBranch = surveyResultsReportBranchDao
                .getSurveyResultForBranchId( entityId, startDate, endDate, startIndex, batchSize );
            surveyResultsReportVO = assignToVO( surveyResultsReportBranch, entityType );
        } else if ( entityType.equals( CommonConstants.AGENT_ID ) ) {
            Map<String, SurveyResultsCompanyReport> surveyResultsReportUser = surveyResultsCompanyReportDao
                .getSurveyResultForUserId( entityId, startDate, endDate, startIndex, batchSize );
            surveyResultsReportVO = assignToVO( surveyResultsReportUser, entityType );
        }
        return surveyResultsReportVO;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public List<List<Object>> getCompanyUserReport( Long entityId, String entityType )
    {
        List<List<Object>> companyUser = new ArrayList<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            for ( CompanyUserReport companyUserReport : companyUserReportDao.fetchCompanyUserReportByCompanyId( entityId ) ) {
                List<Object> companyUserReportList = new ArrayList<>();
                if ( companyUserReport.getFirstName() != null && !companyUserReport.getFirstName().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getFirstName() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLastName() != null && !companyUserReport.getLastName().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getLastName() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getEmail() != null && !companyUserReport.getEmail().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getEmail() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getSocialSurveyAccessLevel() != null
                    && !companyUserReport.getSocialSurveyAccessLevel().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getSocialSurveyAccessLevel() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getOfficeBranchAssignment() != null
                    && !companyUserReport.getOfficeBranchAssignment().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getOfficeBranchAssignment() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getRegionAssignment() != null && !companyUserReport.getRegionAssignment().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getRegionAssignment() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getOfficeAdmin() != null && !companyUserReport.getOfficeAdmin().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getOfficeAdmin() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getRegionAdmin() != null && !companyUserReport.getRegionAdmin().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getRegionAdmin() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getSsInviteSentDate() != null ) {
                    companyUserReportList.add( companyUserReport.getSsInviteSentDate() );
                } else {
                    companyUserReportList.add( "" );
                }
                companyUserReportList.add( "" );
                if ( companyUserReport.getEmailVerified() != null && !companyUserReport.getEmailVerified().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getEmailVerified() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLastLoginDate() != null ) {
                    companyUserReportList.add( companyUserReport.getLastLoginDate() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getProfileComplete() != null && !companyUserReport.getProfileComplete().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getProfileComplete() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getSociallyConnected() != null && !companyUserReport.getSociallyConnected().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getSociallyConnected() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getFbDataConnection() != null && !companyUserReport.getFbDataConnection().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getFbDataConnection() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getFbConnectionStatus() != null
                    && !companyUserReport.getFbConnectionStatus().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getFbConnectionStatus() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLastPostDateFb() != null ) {
                    companyUserReportList.add( companyUserReport.getLastPostDateFb() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getTwitterDataConnection() != null
                    && !companyUserReport.getTwitterDataConnection().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getTwitterDataConnection() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getTwitterConnectionStatus() != null
                    && !companyUserReport.getTwitterConnectionStatus().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getTwitterConnectionStatus() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLastPostDateTwitter() != null ) {
                    companyUserReportList.add( companyUserReport.getLastPostDateTwitter() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLinkedinConnectionStatus() != null
                    && !companyUserReport.getLinkedinConnectionStatus().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getLinkedinDataConnection() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLinkedinConnectionStatus() != null
                    && !companyUserReport.getLinkedinConnectionStatus().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getLinkedinConnectionStatus() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLastPostDateLinkedin() != null ) {
                    companyUserReportList.add( companyUserReport.getLastPostDateLinkedin() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getGooglePlusUrl() != null && !companyUserReport.getGooglePlusUrl().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getGooglePlusUrl() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getZillowUrl() != null && !companyUserReport.getZillowUrl().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getZillowUrl() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getYelpUrl() != null && !companyUserReport.getYelpUrl().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getYelpUrl() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getRealtorUrl() != null && !companyUserReport.getRealtorUrl().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getRealtorUrl() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getGbUrl() != null && !companyUserReport.getGbUrl().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getGbUrl() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLendingtreeUrl() != null && !companyUserReport.getLendingtreeUrl().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getLendingtreeUrl() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getAdoptionCompletedDate() != null ) {
                    companyUserReportList.add( companyUserReport.getAdoptionCompletedDate() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLastSurveySentDate() != null ) {
                    companyUserReportList.add( companyUserReport.getLastSurveySentDate() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getLastSurveyPostedDate() != null ) {
                    companyUserReportList.add( companyUserReport.getLastSurveyPostedDate() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getAddress() != null && !companyUserReport.getAddress().isEmpty() ) {
                    companyUserReportList.add( companyUserReport.getAddress() );
                } else {
                    companyUserReportList.add( "" );
                }
                if ( companyUserReport.getSsProfile() != null && !companyUserReport.getSsProfile().isEmpty() ) {
                    companyUserReportList
                        .add( applicationBaseUrl + CommonConstants.AGENT_PROFILE_FIXED_URL + companyUserReport.getSsProfile() );
                } else {
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
	public List<SurveyTransactionReportVO> getSurveyTransactionReport(Long entityId, String entityType,
			int month, int year) {
		
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonConstants.MONTH, month);
		queryMap.put(CommonConstants.YEAR, year);
		
		List<SurveyTransactionReportVO> surveyTransactionReportVOs = null;

		switch (entityType) {
		case CommonConstants.COMPANY_ID_COLUMN:
			queryMap.put(CommonConstants.COMPANY_ID_COLUMN, entityId);
			surveyTransactionReportVOs = createSurveyTransactionReportVO(
					surveyTransactionReportDao.findByKeyValue(SurveyTransactionReport.class, queryMap));
			break;
		case CommonConstants.REGION_ID_COLUMN:
			queryMap.put(CommonConstants.REGION_ID_COLUMN, entityId);
			surveyTransactionReportVOs = createSurveyTransactionRegionReportVO(
					surveyTransactionRegionReportDao.findByKeyValue(SurveyTransactionReportRegion.class, queryMap));
			break;
		case CommonConstants.BRANCH_ID_COLUMN:
			queryMap.put(CommonConstants.BRANCH_ID_COLUMN, entityId);
			surveyTransactionReportVOs = createSurveyTransactionBranchReportVO(
					surveyTransactionBranchReportDao.findByKeyValue(SurveyTransactionReportBranch.class, queryMap));
			break;
		case CommonConstants.AGENT_ID_COLUMN:
			queryMap.put(CommonConstants.USER_ID, entityId);
			surveyTransactionReportVOs = createSurveyTransactionReportVO(
					surveyTransactionReportDao.findByKeyValue(SurveyTransactionReport.class, queryMap));
			break;
		}
		return surveyTransactionReportVOs;

	}


	private List<SurveyTransactionReportVO> createSurveyTransactionBranchReportVO(
			List<SurveyTransactionReportBranch> surveyTransactionReports) {
		List<SurveyTransactionReportVO> surveyTransactionReportVOs = new ArrayList<>();
		for (SurveyTransactionReportBranch surveyTransactionReport : surveyTransactionReports) {
			SurveyTransactionReportVO reportVO = new SurveyTransactionReportVO();
			reportVO.setMonth(surveyTransactionReport.getMonth());
			reportVO.setYear(surveyTransactionReport.getYear());
			reportVO.setTrxMonth(surveyTransactionReport.getYear() + "_" + surveyTransactionReport.getMonth());
			reportVO.setUserName(surveyTransactionReport.getUserName());
			reportVO.setUserId(surveyTransactionReport.getUserId());
			reportVO.setNmls(surveyTransactionReport.getNmls());
			reportVO.setLicenseId(surveyTransactionReport.getLicenseId());
			reportVO.setCompanyId(surveyTransactionReport.getCompanyId());
			reportVO.setCompanyName(surveyTransactionReport.getCompanyName());
			reportVO.setRegionName(surveyTransactionReport.getRegionName());
			reportVO.setBranchName(surveyTransactionReport.getBranchName());
			reportVO.setTotalReviews(surveyTransactionReport.getTotalReviews());
			reportVO.setTotalZillowReviews(surveyTransactionReport.getTotalZillowReviews());
			reportVO.setTotal_3rdPartyReviews(surveyTransactionReport.getTotal_3rdPartyReviews());
			reportVO.setTotalVerifiedCustomerReviews(surveyTransactionReport.getTotalVerifiedCustomerReviews());
			reportVO.setTotalUnverifiedCustomerReviews(surveyTransactionReport.getTotalUnverifiedCustomerReviews());
			reportVO.setTotalAbusiveReviews(surveyTransactionReport.getTotalAbusiveReviews());
			reportVO.setTotalRetakeReviews(surveyTransactionReport.getTotalRetakeReviews());
			reportVO.setTotalRetakeCompleted(surveyTransactionReport.getTotalRetakeCompleted());
			reportVO.setTransactionReceivedBySource(surveyTransactionReport.getTransactionReceivedBySource());
			reportVO.setTransactionSent(surveyTransactionReport.getTransactionSent());
			reportVO.setTransactionUnprocessable(surveyTransactionReport.getTransactionUnprocessable());
			reportVO.setTransactionClicked(surveyTransactionReport.getTransactionClicked());
			reportVO.setTransactionCompleted(surveyTransactionReport.getTransactionCompleted());
			reportVO.setTransactionUnopened(surveyTransactionReport.getTransactionUnopened());
			reportVO.setTransactionPartiallyCompleted(surveyTransactionReport.getTransactionPartiallyCompleted());
			reportVO.setTransactionDuplicates(surveyTransactionReport.getTransactionDuplicates());
			reportVO.setEmailId(surveyTransactionReport.getEmailId());
			surveyTransactionReportVOs.add(reportVO);
		}
		return surveyTransactionReportVOs;

	}

	private List<SurveyTransactionReportVO> createSurveyTransactionRegionReportVO(
			List<SurveyTransactionReportRegion> surveyTransactionReports) {
		List<SurveyTransactionReportVO> surveyTransactionReportVOs = new ArrayList<>();
    	for(SurveyTransactionReportRegion surveyTransactionReport : surveyTransactionReports) {
    		SurveyTransactionReportVO reportVO = new SurveyTransactionReportVO();
    		reportVO.setMonth(surveyTransactionReport.getMonth());
    		reportVO.setYear(surveyTransactionReport.getYear());
    		reportVO.setTrxMonth(surveyTransactionReport.getYear()+"_"+surveyTransactionReport.getMonth());
    		reportVO.setUserName(surveyTransactionReport.getUserName());
    		reportVO.setUserId(surveyTransactionReport.getUserId());
    		reportVO.setNmls(surveyTransactionReport.getNmls());
    		reportVO.setLicenseId(surveyTransactionReport.getLicenseId());
    		reportVO.setCompanyId(surveyTransactionReport.getCompanyId());
    		reportVO.setCompanyName(surveyTransactionReport.getCompanyName());
    		reportVO.setRegionName(surveyTransactionReport.getRegionName());
    		reportVO.setBranchName(surveyTransactionReport.getBranchName());
    		reportVO.setTotalReviews(surveyTransactionReport.getTotalReviews());
    		reportVO.setTotalZillowReviews(surveyTransactionReport.getTotalZillowReviews());
    		reportVO.setTotal_3rdPartyReviews(surveyTransactionReport.getTotal_3rdPartyReviews());
    		reportVO.setTotalVerifiedCustomerReviews(surveyTransactionReport.getTotalVerifiedCustomerReviews());
    		reportVO.setTotalUnverifiedCustomerReviews(surveyTransactionReport.getTotalUnverifiedCustomerReviews() );
    		reportVO.setTotalAbusiveReviews(surveyTransactionReport.getTotalAbusiveReviews());
    		reportVO.setTotalRetakeReviews(surveyTransactionReport.getTotalRetakeReviews());
    		reportVO.setTotalRetakeCompleted(surveyTransactionReport.getTotalRetakeCompleted());
    		reportVO.setTransactionReceivedBySource(surveyTransactionReport.getTransactionReceivedBySource());
    		reportVO.setTransactionSent(surveyTransactionReport.getTransactionSent());
    		reportVO.setTransactionUnprocessable(surveyTransactionReport.getTransactionUnprocessable());
    		reportVO.setTransactionClicked(surveyTransactionReport.getTransactionClicked());
    		reportVO.setTransactionCompleted(surveyTransactionReport.getTransactionCompleted());
    		reportVO.setTransactionUnopened(surveyTransactionReport.getTransactionUnopened());
    		reportVO.setTransactionPartiallyCompleted(surveyTransactionReport.getTransactionPartiallyCompleted());
    		reportVO.setTransactionDuplicates(surveyTransactionReport.getTransactionDuplicates());
    		reportVO.setEmailId(surveyTransactionReport.getEmailId());
    		surveyTransactionReportVOs.add(reportVO);
    	}
		return surveyTransactionReportVOs;
	}


	private List<SurveyTransactionReportVO> createSurveyTransactionReportVO(
			List<SurveyTransactionReport> surveyTransactionReports) {
		
    	List<SurveyTransactionReportVO> surveyTransactionReportVOs = new ArrayList<>();
    	for(SurveyTransactionReport surveyTransactionReport : surveyTransactionReports) {
    		SurveyTransactionReportVO reportVO = new SurveyTransactionReportVO();
    		reportVO.setMonth(surveyTransactionReport.getMonth());
    		reportVO.setYear(surveyTransactionReport.getYear());
    		reportVO.setTrxMonth(surveyTransactionReport.getYear()+"_"+surveyTransactionReport.getMonth());
    		reportVO.setUserName(surveyTransactionReport.getUserName());
    		reportVO.setUserId(surveyTransactionReport.getUserId());
    		reportVO.setNmls(surveyTransactionReport.getNmls());
    		reportVO.setLicenseId(surveyTransactionReport.getLicenseId());
    		reportVO.setCompanyId(surveyTransactionReport.getCompanyId());
    		reportVO.setCompanyName(surveyTransactionReport.getCompanyName());
    		reportVO.setRegionName(surveyTransactionReport.getRegionName());
    		reportVO.setBranchName(surveyTransactionReport.getBranchName());
    		reportVO.setTotalReviews(surveyTransactionReport.getTotalReviews());
    		reportVO.setTotalZillowReviews(surveyTransactionReport.getTotalZillowReviews());
    		reportVO.setTotal_3rdPartyReviews(surveyTransactionReport.getTotal_3rdPartyReviews());
    		reportVO.setTotalVerifiedCustomerReviews(surveyTransactionReport.getTotalVerifiedCustomerReviews());
    		reportVO.setTotalUnverifiedCustomerReviews(surveyTransactionReport.getTotalUnverifiedCustomerReviews() );
    		reportVO.setTotalAbusiveReviews(surveyTransactionReport.getTotalAbusiveReviews());
    		reportVO.setTotalRetakeReviews(surveyTransactionReport.getTotalRetakeReviews());
    		reportVO.setTotalRetakeCompleted(surveyTransactionReport.getTotalRetakeCompleted());
    		reportVO.setTransactionReceivedBySource(surveyTransactionReport.getTransactionReceivedBySource());
    		reportVO.setTransactionSent(surveyTransactionReport.getTransactionSent());
    		reportVO.setTransactionUnprocessable(surveyTransactionReport.getTransactionUnprocessable());
    		reportVO.setTransactionClicked(surveyTransactionReport.getTransactionClicked());
    		reportVO.setTransactionCompleted(surveyTransactionReport.getTransactionCompleted());
    		reportVO.setTransactionUnopened(surveyTransactionReport.getTransactionUnopened());
    		reportVO.setTransactionPartiallyCompleted(surveyTransactionReport.getTransactionPartiallyCompleted());
    		reportVO.setTransactionDuplicates(surveyTransactionReport.getTransactionDuplicates());
    		reportVO.setEmailId(surveyTransactionReport.getEmailId());
    		reportVO.setTotalSocialSurveyReviews(surveyTransactionReport.getTotalSocialSurveyReviews());
    		surveyTransactionReportVOs.add(reportVO);
    	}
		return surveyTransactionReportVOs;
	}


	@Override
    @Transactional ( value = "transactionManagerForReporting")
    public List<List<Object>> getUserRankingReportForYear( Long entityId, String entityType, int year )
    {
        Calendar calender = Calendar.getInstance();
        int thisYear = calender.get( Calendar.YEAR );
        List<List<Object>> userRanking = new ArrayList<>();

        try {
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                Company company = companyDao.findById( Company.class, entityId );

                List<Region> regionList = regionDao.getRegionsForCompany( company.getCompanyId(), -1, -1 );
                HashMap<Long, String> regionNameMap = new HashMap<>();

                for ( Region region : regionList ) {
                    regionNameMap.put( region.getRegionId(), region.getRegion() );
                }

                List<Branch> branchList = branchDao.getBranchesForCompany( company.getCompanyId(), 0, -1, -1 );
                HashMap<Long, String> branchNameMap = new HashMap<>();

                for ( Branch branch : branchList ) {
                    branchNameMap.put( branch.getBranchId(), branch.getBranch() );
                }

                if ( year == thisYear ) {
                    for ( UserRankingThisYearMain userRankingThisYearMain : userRankingThisYearMainDao
                        .fetchUserRankingReportForThisYearMain( entityId, year ) ) {
                        List<Object> userRankingThisYearMainList = new ArrayList<>();
                        if ( userRankingThisYearMain.getFirstName() != null
                            && !userRankingThisYearMain.getFirstName().isEmpty() ) {
                            userRankingThisYearMainList.add( userRankingThisYearMain.getFirstName() );
                        } else {
                            userRankingThisYearMainList.add( "" );
                        }

                        if ( userRankingThisYearMain.getLastName() != null
                            && !userRankingThisYearMain.getLastName().isEmpty() ) {
                            userRankingThisYearMainList.add( userRankingThisYearMain.getLastName() );
                        } else {
                            userRankingThisYearMainList.add( "" );
                        }

                        userRankingThisYearMainList.add( userRankingThisYearMain.getEmailId() );
                        userRankingThisYearMainList.add( userRankingThisYearMain.getUserId() );
                        userRankingThisYearMainList.add( userRankingThisYearMain.getNmlsId() );
                        userRankingThisYearMainList.add( company.getCompany() );
                        if ( userRankingThisYearMain.getRegionId() != 0 ) {
                            userRankingThisYearMainList.add( regionNameMap.get( userRankingThisYearMain.getRegionId() ) );
                        } else {
                            userRankingThisYearMainList.add( "" );
                        }
                        if ( userRankingThisYearMain.getBranchId() != 0 ) {
                            userRankingThisYearMainList.add( branchNameMap.get( userRankingThisYearMain.getBranchId() ) );
                        } else {
                            userRankingThisYearMainList.add( "" );
                        }
                        userRankingThisYearMainList.add( userRankingThisYearMain.getTotalReviews() );
                        userRankingThisYearMainList.add( userRankingThisYearMain.getAverageRating() );
                        userRankingThisYearMainList.add( userRankingThisYearMain.getRankingScore() );
                        userRankingThisYearMainList.add( userRankingThisYearMain.getSps() );
                        if ( userRankingThisYearMain.getIsEligible() == 1 ) {
                            userRankingThisYearMainList.add( userRankingThisYearMain.getRank() );
                        } else {
                            userRankingThisYearMainList.add( "NR" );
                        }
                        userRankingThisYearMainList.add( userRankingThisYearMain.getCompletedPercentage() );
                        userRanking.add( userRankingThisYearMainList );
                    }
                } else {
                    for ( UserRankingPastYearMain userRankingPastYearMain : userRankingPastYearMainDao
                        .fetchUserRankingReportForPastYearMain( entityId, year ) ) {
                        List<Object> userRankingPastYearMainList = new ArrayList<>();
                        if ( userRankingPastYearMain.getFirstName() != null
                            && !userRankingPastYearMain.getFirstName().isEmpty() ) {
                            userRankingPastYearMainList.add( userRankingPastYearMain.getFirstName() );
                        } else {
                            userRankingPastYearMainList.add( "" );
                        }

                        if ( userRankingPastYearMain.getLastName() != null
                            && !userRankingPastYearMain.getLastName().isEmpty() ) {
                            userRankingPastYearMainList.add( userRankingPastYearMain.getLastName() );
                        } else {
                            userRankingPastYearMainList.add( "" );
                        }

                        userRankingPastYearMainList.add( userRankingPastYearMain.getEmailId() );
                        userRankingPastYearMainList.add( userRankingPastYearMain.getUserId() );
                        userRankingPastYearMainList.add( userRankingPastYearMain.getNmlsId() );
                        userRankingPastYearMainList.add( company.getCompany() );
                        if ( userRankingPastYearMain.getRegionId() != 0 ) {
                            userRankingPastYearMainList.add( regionNameMap.get( userRankingPastYearMain.getRegionId() ) );
                        } else {
                            userRankingPastYearMainList.add( "" );
                        }
                        if ( userRankingPastYearMain.getBranchId() != 0 ) {
                            userRankingPastYearMainList.add( branchNameMap.get( userRankingPastYearMain.getBranchId() ) );
                        } else {
                            userRankingPastYearMainList.add( "" );
                        }
                        userRankingPastYearMainList.add( userRankingPastYearMain.getTotalReviews() );
                        userRankingPastYearMainList.add( userRankingPastYearMain.getAverageRating() );
                        userRankingPastYearMainList.add( userRankingPastYearMain.getRankingScore() );
                        userRankingPastYearMainList.add( userRankingPastYearMain.getSps() );
                        if ( userRankingPastYearMain.getIsEligible() == 1 ) {
                            userRankingPastYearMainList.add( userRankingPastYearMain.getRank() );
                        } else {
                            userRankingPastYearMainList.add( "NR" );
                        }

                        userRankingPastYearMainList.add( userRankingPastYearMain.getCompletedPercentage() );
                        userRanking.add( userRankingPastYearMainList );
                    }

                }

            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                Region region = regionDao.findById( Region.class, entityId );
                Company company = companyDao.findById( Company.class, region.getCompany().getCompanyId() );

                List<Branch> branchList = branchDao.getBranchesForRegion( region.getRegionId(), 0, -1, -1 );
                HashMap<Long, String> branchNameMap = new HashMap<>();

                for ( Branch branch : branchList ) {
                    branchNameMap.put( branch.getBranchId(), branch.getBranch() );
                }

                if ( year == thisYear ) {
                    for ( UserRankingThisYearRegion userRankingThisYearRegion : userRankingThisYearRegionDao
                        .fetchUserRankinReportForThisYearRegion( entityId, year ) ) {
                        List<Object> userRankingThisYearRegionList = new ArrayList<>();
                        if ( userRankingThisYearRegion.getFirstName() != null
                            && !userRankingThisYearRegion.getFirstName().isEmpty() ) {
                            userRankingThisYearRegionList.add( userRankingThisYearRegion.getFirstName() );
                        } else {
                            userRankingThisYearRegionList.add( "" );
                        }

                        if ( userRankingThisYearRegion.getLastName() != null
                            && !userRankingThisYearRegion.getLastName().isEmpty() ) {
                            userRankingThisYearRegionList.add( userRankingThisYearRegion.getLastName() );
                        } else {
                            userRankingThisYearRegionList.add( "" );
                        }

                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getEmailId() );
                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getUserId() );
                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getNmlsId() );
                        userRankingThisYearRegionList.add( company.getCompany() );

                        userRankingThisYearRegionList.add( region.getRegion() );

                        if ( userRankingThisYearRegion.getBranchId() != 0 ) {
                            userRankingThisYearRegionList.add( branchNameMap.get( userRankingThisYearRegion.getBranchId() ) );
                        } else {
                            userRankingThisYearRegionList.add( "" );
                        }
                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getTotalReviews() );
                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getAverageRating() );
                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getRankingScore() );
                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getSps() );
                        if ( userRankingThisYearRegion.getIsEligible() == 1 ) {
                            userRankingThisYearRegionList.add( userRankingThisYearRegion.getRank() );
                        } else {
                            userRankingThisYearRegionList.add( "NR" );
                        }
                        userRankingThisYearRegionList.add( userRankingThisYearRegion.getCompletedPercentage() );
                        userRanking.add( userRankingThisYearRegionList );
                    }
                } else {

                    for ( UserRankingPastYearRegion userRankingPastYearRegion : userRankingPastYearRegionDao
                        .fetchUserRankingReportForPastYearRegion( entityId, year ) ) {
                        List<Object> userRankingPastYearRegionList = new ArrayList<>();
                        if ( userRankingPastYearRegion.getFirstName() != null
                            && !userRankingPastYearRegion.getFirstName().isEmpty() ) {
                            userRankingPastYearRegionList.add( userRankingPastYearRegion.getFirstName() );
                        } else {
                            userRankingPastYearRegionList.add( "" );
                        }

                        if ( userRankingPastYearRegion.getLastName() != null
                            && !userRankingPastYearRegion.getLastName().isEmpty() ) {
                            userRankingPastYearRegionList.add( userRankingPastYearRegion.getLastName() );
                        } else {
                            userRankingPastYearRegionList.add( "" );
                        }

                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getEmailId() );
                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getUserId() );
                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getNmlsId() );
                        userRankingPastYearRegionList.add( company.getCompany() );

                        userRankingPastYearRegionList.add( region.getRegion() );

                        if ( userRankingPastYearRegion.getBranchId() != 0 ) {
                            userRankingPastYearRegionList.add( branchNameMap.get( userRankingPastYearRegion.getBranchId() ) );
                        } else {
                            userRankingPastYearRegionList.add( "" );
                        }
                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getTotalReviews() );
                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getAverageRating() );
                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getRankingScore() );
                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getSps() );
                        if ( userRankingPastYearRegion.getIsEligible() == 1 ) {
                            userRankingPastYearRegionList.add( userRankingPastYearRegion.getRank() );
                        } else {
                            userRankingPastYearRegionList.add( "NR" );
                        }
                        userRankingPastYearRegionList.add( userRankingPastYearRegion.getCompletedPercentage() );

                        userRanking.add( userRankingPastYearRegionList );
                    }
                }

            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                Branch branch = branchDao.findById( Branch.class, entityId );
                Company company = companyDao.findById( Company.class, branch.getCompany().getCompanyId() );
                Region region = regionDao.findById( Region.class, branch.getRegion().getRegionId() );

                if ( year == thisYear ) {
                    for ( UserRankingThisYearBranch userRankingThisYearBranch : userRankingThisYearBranchDao
                        .fetchUserRankingReportForThisYearBranch( entityId, year ) ) {
                        List<Object> userRankingThisYearBranchList = new ArrayList<>();
                        if ( userRankingThisYearBranch.getFirstName() != null
                            && !userRankingThisYearBranch.getFirstName().isEmpty() ) {
                            userRankingThisYearBranchList.add( userRankingThisYearBranch.getFirstName() );
                        } else {
                            userRankingThisYearBranchList.add( "" );
                        }

                        if ( userRankingThisYearBranch.getLastName() != null
                            && !userRankingThisYearBranch.getLastName().isEmpty() ) {
                            userRankingThisYearBranchList.add( userRankingThisYearBranch.getLastName() );
                        } else {
                            userRankingThisYearBranchList.add( "" );
                        }

                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getEmailId() );
                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getUserId() );
                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getNmlsId() );
                        userRankingThisYearBranchList.add( company.getCompany() );
                        if ( userRankingThisYearBranch.getRegionId() != 0 ) {
                            userRankingThisYearBranchList.add( region.getRegion() );
                        } else {
                            userRankingThisYearBranchList.add( "" );
                        }
                        userRankingThisYearBranchList.add( branch.getBranch() );

                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getTotalReviews() );
                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getAverageRating() );
                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getRankingScore() );
                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getSps() );
                        if ( userRankingThisYearBranch.getIsEligible() == 1 ) {
                            userRankingThisYearBranchList.add( userRankingThisYearBranch.getRank() );
                        } else {
                            userRankingThisYearBranchList.add( "NR" );
                        }
                        userRankingThisYearBranchList.add( userRankingThisYearBranch.getCompletedPercentage() );

                        userRanking.add( userRankingThisYearBranchList );
                    }
                } else {

                    for ( UserRankingPastYearBranch userRankingPastYearBranch : userRankingPastYearBranchDao
                        .fetchUserRankingReportForPastYearBranch( entityId, year ) ) {
                        List<Object> userRankingPastYearBranchList = new ArrayList<>();
                        if ( userRankingPastYearBranch.getFirstName() != null
                            && !userRankingPastYearBranch.getFirstName().isEmpty() ) {
                            userRankingPastYearBranchList.add( userRankingPastYearBranch.getFirstName() );
                        } else {
                            userRankingPastYearBranchList.add( "" );
                        }

                        if ( userRankingPastYearBranch.getLastName() != null
                            && !userRankingPastYearBranch.getLastName().isEmpty() ) {
                            userRankingPastYearBranchList.add( userRankingPastYearBranch.getLastName() );
                        } else {
                            userRankingPastYearBranchList.add( "" );
                        }

                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getEmailId() );
                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getUserId() );
                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getNmlsId() );
                        userRankingPastYearBranchList.add( company.getCompany() );
                        if ( userRankingPastYearBranch.getRegionId() != 0 ) {
                            userRankingPastYearBranchList.add( region.getRegion() );
                        } else {
                            userRankingPastYearBranchList.add( "" );
                        }
                        userRankingPastYearBranchList.add( branch.getBranch() );

                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getTotalReviews() );
                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getAverageRating() );
                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getRankingScore() );
                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getSps() );
                        if ( userRankingPastYearBranch.getIsEligible() == 1 ) {
                            userRankingPastYearBranchList.add( userRankingPastYearBranch.getRank() );
                        } else {
                            userRankingPastYearBranchList.add( "NR" );
                        }
                        userRankingPastYearBranchList.add( userRankingPastYearBranch.getCompletedPercentage() );

                        userRanking.add( userRankingPastYearBranchList );
                    }
                }

            }
        } catch ( InvalidInputException e ) {
            LOG.error( "Exception caught: EntityId cannot be less than 0", e );
            LOG.info( "Returning empty list as response" );
        }
        return userRanking;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public List<List<Object>> getUserRankingReportForMonth( Long entityId, String entityType, int year, int month )
    {
        Calendar calender = Calendar.getInstance();
        int thisYear = calender.get( Calendar.YEAR );
        int thisMonth = calender.get( Calendar.MONTH ) + 1;

        List<List<Object>> userRanking = new ArrayList<>();

        try {
            if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
                Company company = companyDao.findById( Company.class, entityId );

                List<Region> regionList = regionDao.getRegionsForCompany( company.getCompanyId(), -1, -1 );
                HashMap<Long, String> regionNameMap = new HashMap<>();

                for ( Region region : regionList ) {
                    regionNameMap.put( region.getRegionId(), region.getRegion() );
                }

                List<Branch> branchList = branchDao.getBranchesForCompany( company.getCompanyId(), 0, -1, -1 );
                HashMap<Long, String> branchNameMap = new HashMap<>();

                for ( Branch branch : branchList ) {
                    branchNameMap.put( branch.getBranchId(), branch.getBranch() );
                }

                if ( month == thisMonth && year == thisYear ) {
                    for ( UserRankingThisMonthMain userRankingThisMonthMain : userRankingThisMonthMainDao
                        .fetchUserRankingReportForThisMonthMain( entityId, month, year ) ) {
                        List<Object> userRankingThisMonthMainList = new ArrayList<>();
                        if ( userRankingThisMonthMain.getFirstName() != null
                            && !userRankingThisMonthMain.getFirstName().isEmpty() ) {
                            userRankingThisMonthMainList.add( userRankingThisMonthMain.getFirstName() );
                        } else {
                            userRankingThisMonthMainList.add( "" );
                        }

                        if ( userRankingThisMonthMain.getLastName() != null
                            && !userRankingThisMonthMain.getLastName().isEmpty() ) {
                            userRankingThisMonthMainList.add( userRankingThisMonthMain.getLastName() );
                        } else {
                            userRankingThisMonthMainList.add( "" );
                        }

                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getEmailId() );
                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getUserId() );
                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getNmlsId() );
                        userRankingThisMonthMainList.add( company.getCompany() );
                        if ( userRankingThisMonthMain.getRegionId() != 0 ) {
                            userRankingThisMonthMainList.add( regionNameMap.get( userRankingThisMonthMain.getRegionId() ) );
                        } else {
                            userRankingThisMonthMainList.add( "" );
                        }
                        if ( userRankingThisMonthMain.getBranchId() != 0 ) {
                            userRankingThisMonthMainList.add( branchNameMap.get( userRankingThisMonthMain.getBranchId() ) );
                        } else {
                            userRankingThisMonthMainList.add( "" );
                        }
                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getTotalReviews() );
                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getAverageRating() );
                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getRankingScore() );
                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getSps() );
                        if ( userRankingThisMonthMain.getIsEligible() == 1 ) {
                            userRankingThisMonthMainList.add( userRankingThisMonthMain.getRank() );
                        } else {
                            userRankingThisMonthMainList.add( "NR" );
                        }
                        userRankingThisMonthMainList.add( userRankingThisMonthMain.getCompletedPercentage() );

                        userRanking.add( userRankingThisMonthMainList );
                    }
                } else {
                    for ( UserRankingPastMonthMain userRankingPastMonthMain : userRankingPastMonthMainDao
                        .fetchUserRankingrReportForPastMonthMain( entityId, month, year ) ) {
                        List<Object> userRankingPastMonthMainList = new ArrayList<>();
                        if ( userRankingPastMonthMain.getFirstName() != null
                            && !userRankingPastMonthMain.getFirstName().isEmpty() ) {
                            userRankingPastMonthMainList.add( userRankingPastMonthMain.getFirstName() );
                        } else {
                            userRankingPastMonthMainList.add( "" );
                        }

                        if ( userRankingPastMonthMain.getLastName() != null
                            && !userRankingPastMonthMain.getLastName().isEmpty() ) {
                            userRankingPastMonthMainList.add( userRankingPastMonthMain.getLastName() );
                        } else {
                            userRankingPastMonthMainList.add( "" );
                        }

                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getEmailId() );
                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getUserId() );
                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getNmlsId() );
                        userRankingPastMonthMainList.add( company.getCompany() );
                        if ( userRankingPastMonthMain.getRegionId() != 0 ) {
                            userRankingPastMonthMainList.add( regionNameMap.get( userRankingPastMonthMain.getRegionId() ) );
                        } else {
                            userRankingPastMonthMainList.add( "" );
                        }
                        if ( userRankingPastMonthMain.getBranchId() != 0 ) {
                            userRankingPastMonthMainList.add( branchNameMap.get( userRankingPastMonthMain.getBranchId() ) );
                        } else {
                            userRankingPastMonthMainList.add( "" );
                        }
                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getTotalReviews() );
                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getAverageRating() );
                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getRankingScore() );
                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getSps() );
                        if ( userRankingPastMonthMain.getIsEligible() == 1 ) {
                            userRankingPastMonthMainList.add( userRankingPastMonthMain.getRank() );
                        } else {
                            userRankingPastMonthMainList.add( "NR" );
                        }
                        userRankingPastMonthMainList.add( userRankingPastMonthMain.getCompletedPercentage() );

                        userRanking.add( userRankingPastMonthMainList );
                    }

                }

            } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
                Region region = regionDao.findById( Region.class, entityId );
                Company company = companyDao.findById( Company.class, region.getCompany().getCompanyId() );

                List<Branch> branchList = branchDao.getBranchesForRegion( region.getRegionId(), 0, -1, -1 );
                HashMap<Long, String> branchNameMap = new HashMap<>();

                for ( Branch branch : branchList ) {
                    branchNameMap.put( branch.getBranchId(), branch.getBranch() );
                }

                if ( month == thisMonth && year == thisYear ) {
                    for ( UserRankingThisMonthRegion userRankingThisMonthRegion : userRankingThisMonthRegionDao
                        .fetchUserRankingReportForThisMonthRegion( entityId, month, year ) ) {
                        List<Object> userRankingThisMonthRegionList = new ArrayList<>();
                        if ( userRankingThisMonthRegion.getFirstName() != null
                            && !userRankingThisMonthRegion.getFirstName().isEmpty() ) {
                            userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getFirstName() );
                        } else {
                            userRankingThisMonthRegionList.add( "" );
                        }

                        if ( userRankingThisMonthRegion.getLastName() != null
                            && !userRankingThisMonthRegion.getLastName().isEmpty() ) {
                            userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getLastName() );
                        } else {
                            userRankingThisMonthRegionList.add( "" );
                        }

                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getEmailId() );
                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getUserId() );
                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getNmlsId() );
                        userRankingThisMonthRegionList.add( company.getCompany() );

                        userRankingThisMonthRegionList.add( region.getRegion() );

                        if ( userRankingThisMonthRegion.getBranchId() != 0 ) {
                            userRankingThisMonthRegionList.add( branchNameMap.get( userRankingThisMonthRegion.getBranchId() ) );
                        } else {
                            userRankingThisMonthRegionList.add( "" );
                        }
                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getTotalReviews() );
                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getAverageRating() );
                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getRankingScore() );
                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getSps() );
                        if ( userRankingThisMonthRegion.getIsEligible() == 1 ) {
                            userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getRank() );
                        } else {
                            userRankingThisMonthRegionList.add( "NR" );
                        }
                        userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getCompletedPercentage() );

                        userRanking.add( userRankingThisMonthRegionList );
                    }
                } else {

                    for ( UserRankingPastMonthRegion userRankingPastMonthRegion : userRankingPastMonthRegionDao
                        .fetchUserRankingReportForPastMonthRegion( entityId, month, year ) ) {
                        List<Object> userRankingPastMonthRegionList = new ArrayList<>();
                        if ( userRankingPastMonthRegion.getFirstName() != null
                            && !userRankingPastMonthRegion.getFirstName().isEmpty() ) {
                            userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getFirstName() );
                        } else {
                            userRankingPastMonthRegionList.add( "" );
                        }

                        if ( userRankingPastMonthRegion.getLastName() != null
                            && !userRankingPastMonthRegion.getLastName().isEmpty() ) {
                            userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getLastName() );
                        } else {
                            userRankingPastMonthRegionList.add( "" );
                        }

                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getEmailId() );
                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getUserId() );
                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getNmlsId() );
                        userRankingPastMonthRegionList.add( company.getCompany() );

                        userRankingPastMonthRegionList.add( region.getRegion() );

                        if ( userRankingPastMonthRegion.getBranchId() != 0 ) {
                            userRankingPastMonthRegionList.add( branchNameMap.get( userRankingPastMonthRegion.getBranchId() ) );
                        } else {
                            userRankingPastMonthRegionList.add( "" );
                        }
                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getTotalReviews() );
                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getAverageRating() );
                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getRankingScore() );
                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getSps() );
                        if ( userRankingPastMonthRegion.getIsEligible() == 1 ) {
                            userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getRank() );
                        } else {
                            userRankingPastMonthRegionList.add( "NR" );
                        }
                        userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getCompletedPercentage() );

                        userRanking.add( userRankingPastMonthRegionList );
                    }
                }

            } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
                Branch branch = branchDao.findById( Branch.class, entityId );
                Company company = companyDao.findById( Company.class, branch.getCompany().getCompanyId() );
                Region region = regionDao.findById( Region.class, branch.getRegion().getRegionId() );

                if ( month == thisMonth && year == thisYear ) {
                    for ( UserRankingThisMonthBranch userRankingThisMonthBranch : userRankingThisMonthBranchDao
                        .fetchUserRankingReportForThisMonthBranch( entityId, month, year ) ) {
                        List<Object> userRankingMonthYearBranchList = new ArrayList<>();
                        if ( userRankingThisMonthBranch.getFirstName() != null
                            && !userRankingThisMonthBranch.getFirstName().isEmpty() ) {
                            userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getFirstName() );
                        } else {
                            userRankingMonthYearBranchList.add( "" );
                        }

                        if ( userRankingThisMonthBranch.getLastName() != null
                            && !userRankingThisMonthBranch.getLastName().isEmpty() ) {
                            userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getLastName() );
                        } else {
                            userRankingMonthYearBranchList.add( "" );
                        }

                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getEmailId() );
                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getUserId() );
                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getNmlsId() );
                        userRankingMonthYearBranchList.add( company.getCompany() );
                        if ( userRankingThisMonthBranch.getRegionId() != 0 ) {
                            userRankingMonthYearBranchList.add( region.getRegion() );
                        } else {
                            userRankingMonthYearBranchList.add( "" );
                        }
                        userRankingMonthYearBranchList.add( branch.getBranch() );

                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getTotalReviews() );
                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getAverageRating() );
                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getRankingScore() );
                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getSps() );
                        if ( userRankingThisMonthBranch.getIsEligible() == 1 ) {
                            userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getRank() );
                        } else {
                            userRankingMonthYearBranchList.add( "NR" );
                        }
                        userRankingMonthYearBranchList.add( userRankingThisMonthBranch.getCompletedPercentage() );

                        userRanking.add( userRankingMonthYearBranchList );
                    }
                } else {

                    for ( UserRankingPastMonthBranch userRankingPastMonthBranch : userRankingPastMonthBranchDao
                        .fetchUserRankingReportForPastMonthBranch( entityId, month, year ) ) {
                        List<Object> userRankingPastMonthBranchList = new ArrayList<>();
                        if ( userRankingPastMonthBranch.getFirstName() != null
                            && !userRankingPastMonthBranch.getFirstName().isEmpty() ) {
                            userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getFirstName() );
                        } else {
                            userRankingPastMonthBranchList.add( "" );
                        }

                        if ( userRankingPastMonthBranch.getLastName() != null
                            && !userRankingPastMonthBranch.getLastName().isEmpty() ) {
                            userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getLastName() );
                        } else {
                            userRankingPastMonthBranchList.add( "" );
                        }

                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getEmailId() );
                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getUserId() );
                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getNmlsId() );
                        userRankingPastMonthBranchList.add( company.getCompany() );
                        if ( userRankingPastMonthBranch.getRegionId() != 0 ) {
                            userRankingPastMonthBranchList.add( region.getRegion() );
                        } else {
                            userRankingPastMonthBranchList.add( "" );
                        }
                        userRankingPastMonthBranchList.add( branch.getBranch() );

                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getTotalReviews() );
                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getAverageRating() );
                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getRankingScore() );
                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getSps() );
                        if ( userRankingPastMonthBranch.getIsEligible() == 1 ) {
                            userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getRank() );
                        } else {
                            userRankingPastMonthBranchList.add( "NR" );
                        }
                        userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getCompletedPercentage() );

                        userRanking.add( userRankingPastMonthBranchList );
                    }
                }

            }
        } catch ( InvalidInputException e ) {
            LOG.error( "Exception caught: EntityId cannot be less than 0", e );
            LOG.info( "Returning empty list as response" );
        }
        return userRanking;
    }


    @Override
    public List<List<Object>> getRecentActivityList( Long entityId, String entityType, int startIndex, int batchSize )
        throws InvalidInputException
    {
        List<List<Object>> recentActivity = new ArrayList<>();
        for ( FileUpload fileUpload : fileUploadDao.findRecentActivityForReporting( entityId, entityType, startIndex,
            batchSize ) ) {
            List<Object> recentActivityList = new ArrayList<>();
            User user = userManagementService.getUserByUserId( fileUpload.getAdminUserId() );
            recentActivityList.add( utils.convertDateToTimeZone( fileUpload.getCreatedOn().getTime(), CommonConstants.TIMEZONE_EST ) );
            // Set the ReportName according to the upload type
            if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_STATS_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_SURVEY_STATS_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_VERIFIED_USERS_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_VERIFIED_USERS_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_USERS_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_COMPANY_USERS_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_RESULTS_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_SURVEY_RESULTS_COMPANY_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_SURVEY_TRANSACTION_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_SURVEY_TRANSACTION_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_MONTHLY_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_USER_RANKING_MONTHLY_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_USER_RANKING_YEARLY_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_USER_RANKING_YEARLY_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_INCOMPLETE_SURVEY_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_INCOMPLETE_SURVEY_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_WEEK_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_NPS_REPORT_FOR_WEEK );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_NPS_MONTH_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_NPS_REPORT_FOR_MONTH );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_SURVEY_INVITATION_EMAIL_REPORT ) {
                recentActivityList.add( CommonConstants.SURVEY_INVITATION_EMAIL_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_MONTHLY_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_BRANCH_RANKING_MONTHLY_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_BRANCH_RANKING_YEARLY_REPORT ) {
                recentActivityList.add( CommonConstants.REPORTING_BRANCH_RANKING_YEARLY_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_REPORTING_DIGEST ) {
                recentActivityList.add( CommonConstants.REPORTING_DIGEST );
            }

            recentActivityList.add( fileUpload.getStartDate() );
            recentActivityList.add( fileUpload.getEndDate() );
            recentActivityList.add( user.getFirstName() );
            recentActivityList.add( user.getLastName() );
            recentActivityList.add( fileUpload.getStatus() );
            recentActivityList.add( fileUpload.getFileName() );
            recentActivityList.add( fileUpload.getFileUploadId() );
            recentActivityList.add( fileUpload.getUploadType() );
            recentActivity.add( recentActivityList );
        }
        return recentActivity;

    }


    @Override
    public Object getAccountStatisticsRecentActivity( Long reportId )
    {
        FileUpload fileUpload = null;
        if ( reportId == CommonConstants.FILE_UPLOAD_REPORTING_COMPANY_DETAILS_REPORT ) {
            fileUpload = fileUploadDao.getLatestActivityForReporting( reportId );
        }
        return fileUpload;
    }


    @Override
    public Long getRecentActivityCount( Long entityId, String entityType )
    {
        return fileUploadDao.getRecentActivityCountForReporting( entityId, entityType );

    }


    @Override
    @Transactional
    public void deleteRecentActivity( Long fileUploadId )
    {
        FileUpload fileUpload = fileUploadDao.findById( FileUpload.class, fileUploadId );
        fileUpload.setShowOnUI( false );
        fileUploadDao.changeShowOnUiStatus( fileUpload );

    }


    @Override
    public String generateSurveyStatsForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException
    {
        User user = userManagementService.getUserByUserId( userId );
        // file is too big for windows hence uncomment the alternative
        String fileName = "Survey_Stats_Report-" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadSurveyStatsForReporting( entityId, entityType );
        return this.createExcelFileAndSaveInAmazonS3( fileName, workbook );

    }


    public XSSFWorkbook downloadSurveyStatsForReporting( long entityId, String entityType )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getReportingSurveyStatsReport( entityId,
            entityType );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
        }
        List<List<String>> surveyStatsReport = null;
        Type listType = new TypeToken<List<List<String>>>() {}.getType();
        surveyStatsReport = new Gson().fromJson( responseString, listType );
        Map<Integer, List<Object>> data = workbookData.getSurveyStatsReportToBeWrittenInSheet( surveyStatsReport );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );
        XSSFSheet sheet = workbook.getSheetAt( 0 );
        makeRowBold( workbook, sheet.getRow( 0 ) );
        return workbook;

    }


    @Override
    public String generateUserAdoptionForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException
    {
        User user = userManagementService.getUserByUserId( userId );
        // file is too big for windows hence uncomment the alternative
        String fileName = "Verified_Users_Report-" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadUserAdoptionForReporting( entityId, entityType );
        return this.createExcelFileAndSaveInAmazonS3( fileName, workbook );

    }


    public XSSFWorkbook downloadUserAdoptionForReporting( long entityId, String entityType )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getUserAdoption( entityId, entityType );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
        }
        List<List<String>> userAdoptionReport = null;
        Type listType = new TypeToken<List<List<String>>>() {}.getType();
        userAdoptionReport = new Gson().fromJson( responseString, listType );
        Map<Integer, List<Object>> data = workbookData.getUserAdoptionReportToBeWrittenInSheet( userAdoptionReport );
        return workbookOperations.createWorkbook( data );

    }


    @Override
    public String generateCompanyUserForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException
    {
        User user = userManagementService.getUserByUserId( userId );
        // file is too big for windows hence uncomment the alternative
        String fileName = "Company_User_Report" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadCompanyUserForReporting( entityId, entityType );
        return this.createExcelFileAndSaveInAmazonS3( fileName, workbook );

    }


    public XSSFWorkbook downloadCompanyUserForReporting( long entityId, String entityType )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getCompanyUserReport( entityId, entityType );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
        }
        List<List<String>> companyUserReport = null;
        Type listType = new TypeToken<List<List<String>>>() {}.getType();
        companyUserReport = new Gson().fromJson( responseString, listType );
        Map<Integer, List<Object>> data = workbookData.getCompanyUserReportToBeWrittenInSheet( companyUserReport );
        return workbookOperations.createWorkbook( data );

    }


    @Override
    public String generateSurveyResultsReport( Long entityId, String entityType, Long userId, Timestamp startDate,
        Timestamp endDate ) throws UnsupportedEncodingException, NonFatalException, ParseException
    {
        LOG.info( "Generating survey results report for enitityId {}, entityType {}, userId {} startDate {}, endDate {}",
            entityId, entityType, userId, startDate, endDate );
        User user = userManagementService.getUserByUserId( userId );
        LOG.debug( "Found user {}", user );
        String fileName = "Survey_Results_Report_" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        LOG.debug( "fileName {} ", fileName );
        XSSFWorkbook workbook = this.downloadSurveyResultsReport( entityId, entityType, startDate, endDate );
        LOG.debug( "Writing {} number of records into file {}", workbook.getSheetAt( 0 ).getLastRowNum(), fileName );
        return createExcelFileAndSaveInAmazonS3( fileName, workbook );
    }


    public XSSFWorkbook downloadSurveyResultsReport( long entityId, String entityType, Timestamp startDate, Timestamp endDate )
        throws ParseException
    {
        int startIndex = 0;
        int batchSize = CommonConstants.BATCH_SIZE;
        int maxQuestion = 0;
        int enterNext = 1;
        Response maxQuestResponse = ssApiBatchIntergrationBuilder.getIntegrationApi().getCompanyMaxQuestion( entityType,
            entityId, startDate, endDate );
        String maxQuestResponseString = maxQuestResponse != null
            ? new String( ( (TypedByteArray) maxQuestResponse.getBody() ).getBytes() )
            : null;
        if ( maxQuestResponseString != null ) {
            maxQuestion = Integer.valueOf( maxQuestResponseString );
            LOG.debug( "maxQuestion {}", maxQuestion );
        }

        // write the excel header first
        Map<Integer, List<Object>> data = workbookData.writeSurveyResultsCompanyReportHeader( maxQuestion );
        // create workbook data
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );

        Map<String, SurveyResultsReportVO> surveyResultsReportVO = null;
        do {
            surveyResultsReportVO = getSurveyResultResponse( entityType, entityId, startDate, endDate, startIndex, batchSize );
            if ( surveyResultsReportVO != null && !surveyResultsReportVO.isEmpty() ) {
                enterNext = startIndex + 1;
                data = workbookData.getSurveyResultsReportToBeWrittenInSheet( surveyResultsReportVO, maxQuestion, enterNext );
                LOG.debug( "Got {} records starting at {} index", data.size(), enterNext );
                // use the created workbook when writing the header answer rewrite the same.
                workbook = workbookOperations.writeToWorkbook( data, workbook, enterNext );
                // calculate startIndex.
                startIndex = startIndex + batchSize;
            }
        } while ( surveyResultsReportVO != null && !surveyResultsReportVO.isEmpty()
            && surveyResultsReportVO.size() >= batchSize );

        XSSFSheet sheet = workbook.getSheetAt( 0 );
        makeRowBold( workbook, sheet.getRow( 0 ) );
        return workbook;

    }


    public Map<String, SurveyResultsReportVO> getSurveyResultResponse( String entityType, Long companyId, Timestamp startDate,
        Timestamp endDate, int startIndex, int batchSize )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getSurveyResultsReport( entityType, companyId,
            startDate, endDate, startIndex, batchSize );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        Map<String, SurveyResultsReportVO> surveyResultsReportVO = null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
            Type listType = new TypeToken<Map<String, SurveyResultsReportVO>>() {}.getType();
            surveyResultsReportVO = new Gson().fromJson( responseString, listType );

        }
        return surveyResultsReportVO;
    }


    @Override
    public String generateIncompleteSurveyResultsReport( Long entityId, String entityType, Long userId, Timestamp startDate,
        Timestamp endDate ) throws UnsupportedEncodingException, NonFatalException, ParseException
    {
        LOG.info(
            "Generating incomplete survey results report for enitityId {}, entityType {}, userId {} startDate {}, endDate {}",
            entityId, entityType, userId, startDate, endDate );
        User user = userManagementService.getUserByUserId( userId );
        LOG.debug( "Found user {}", user );
        String fileName = "Incomplete_Survey_Results_Report_" + entityType + "-" + user.getFirstName() + "_"
            + user.getLastName() + "-" + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        LOG.debug( "fileName {} ", fileName );
        XSSFWorkbook workbook = this.downloadIncompleteSurveyResultsReport( entityId, entityType, startDate, endDate );
        LOG.debug( "Writing {} number of records into file {}", workbook.getSheetAt( 0 ).getLastRowNum(), fileName );
        return createExcelFileAndSaveInAmazonS3( fileName, workbook );
    }


    private XSSFWorkbook downloadIncompleteSurveyResultsReport( long entityId, String entityType, Timestamp startDate,
        Timestamp endDate ) throws ParseException
    {
        int startIndex = 0;
        int batchSize = CommonConstants.BATCH_SIZE;
        int enterNext = 1;

        // write the excel header first
        Map<Integer, List<Object>> data = workbookData.writeIncompleteSurveyResultsCompanyReportHeader();
        // create workbook data
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );

        List<ReportingSurveyPreInititation> incompleteSurvey = null;
        do {
            incompleteSurvey = getIncompleteSurveyResultResponse( entityType, entityId, startDate, endDate, startIndex,
                batchSize );
            if ( incompleteSurvey != null && !incompleteSurvey.isEmpty() ) {
                enterNext = startIndex + 1;
                data = workbookData.getIncompleteSurveyResultsReportToBeWrittenInSheet( incompleteSurvey, enterNext );
                LOG.debug( "Got {} records starting at {} index", data.size(), enterNext );
                // use the created workbook when writing the header answer rewrite the same.
                workbook = workbookOperations.writeToWorkbook( data, workbook, enterNext );
                // calculate startIndex.
                startIndex = startIndex + batchSize;
            }
        } while ( incompleteSurvey != null && !incompleteSurvey.isEmpty() && incompleteSurvey.size() >= batchSize );

        XSSFSheet sheet = workbook.getSheetAt( 0 );
        makeRowBold( workbook, sheet.getRow( 0 ) );
        return workbook;

    }


    private List<ReportingSurveyPreInititation> getIncompleteSurveyResultResponse( String entityType, Long companyId,
        Timestamp startDate, Timestamp endDate, int startIndex, int batchSize )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getIncompleteSurveyResultsReport( companyId,
            entityType, startDate, endDate, startIndex, batchSize );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        List<ReportingSurveyPreInititation> incompleteSurvey = null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
            Type listType = new TypeToken<List<ReportingSurveyPreInititation>>() {}.getType();
            incompleteSurvey = new Gson().fromJson( responseString, listType );
        }
        return incompleteSurvey;
    }


    @Override
    public String generateSurveyTransactionForReporting( Long entityId, String entityType, Long userId, Timestamp startDate ) 
    		throws UnsupportedEncodingException, NonFatalException
    {
        User user = userManagementService.getUserByUserId( userId );
        Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
        // file is too big for windows hence uncomment the alternative
        String fileName = "Survey_Transaction_Report" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadSurveyTransactionForReporting( entityId, entityType, month, year );
        return this.createExcelFileAndSaveInAmazonS3( fileName, workbook );

    }


    public XSSFWorkbook downloadSurveyTransactionForReporting( long entityId, String entityType, int month, int year )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getSurveyTransactionReport( entityId, entityType,
            month, year );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
        }
        List<SurveyTransactionReportVO> surveyTransactionReport = null;
        Type listType = new TypeToken<List<SurveyTransactionReportVO>>() {}.getType();
        surveyTransactionReport = new Gson().fromJson( responseString, listType );
        Map<Integer, List<Object>> data = workbookData.getSurveyTransactionReportToBeWrittenInSheet( surveyTransactionReport );
        return workbookOperations.createWorkbook( data );

    }


    @Override
    public String generateUserRankingForReporting( Long entityId, String entityType, Long userId, Timestamp startDate,
        int type ) throws UnsupportedEncodingException, NonFatalException
    {
        User user = userManagementService.getUserByUserId( userId );
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis( startDate.getTime() );
        int year = calender.get( Calendar.YEAR );
        int month = calender.get( Calendar.MONTH ) + 1;
        // file is too big for windows hence uncomment the alternative
        String fileName = "User_Ranking_Report" + entityType + "-" + user.getFirstName() + "_" + user.getLastName() + "-"
            + ( Calendar.getInstance().getTimeInMillis() ) + CommonConstants.EXCEL_FILE_EXTENSION;
        XSSFWorkbook workbook = this.downloadUserRankingForReporting( entityId, entityType, year, month, type );
        return this.createExcelFileAndSaveInAmazonS3( fileName, workbook );

    }


    public XSSFWorkbook downloadUserRankingForReporting( long entityId, String entityType, int year, int month, int type )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getUserRankingReport( entityId, entityType, year,
            month, type );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
        }
        List<List<String>> userRankingReport = null;
        Type listType = new TypeToken<List<List<String>>>() {}.getType();
        userRankingReport = new Gson().fromJson( responseString, listType );
        Map<Integer, List<Object>> data = workbookData.getUserRankingReportToBeWrittenInSheet( userRankingReport );
        return workbookOperations.createWorkbook( data );

    }


    private String createExcelFileAndSaveInAmazonS3( String fileName, XSSFWorkbook workbook )
        throws NonFatalException, UnsupportedEncodingException
    {
        // Create file and write report into it
        LOG.debug( "The function createExcelFileAndSaveInAmazonS3 has started for filename : {} on location {}", fileName,
            fileDirectoryLocation );
        boolean excelCreated = false;
        FileOutputStream fileOutput = null;
        File file = null;
        String responseString = null;
        try {
            file = new File( fileDirectoryLocation + File.separator + fileName );
            if ( file.createNewFile() ) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "File created at {}. File Name {}", file.getAbsolutePath(), fileName );
                }
                fileOutput = new FileOutputStream( file );
                LOG.debug( "Created file output stream to write into {}", fileName );
                workbook.write( fileOutput );
                LOG.debug( "Wrote into file {}", fileName );
                excelCreated = true;
            } else {
                excelCreated = false;
            }
            LOG.debug( "Excel creation status {}", excelCreated );
            // SAVE REPORT IN S3
            if ( excelCreated ) {
                fileUploadService.uploadReport( file, fileName );
                LOG.debug( "fileUpload on s3 step is done for filename : {}", fileName );
                String fileNameInS3 = endpoint + CommonConstants.FILE_SEPARATOR + reportBucket + CommonConstants.FILE_SEPARATOR
                    + URLEncoder.encode( fileName, "UTF-8" );
                responseString = fileNameInS3;
                LOG.debug( "returning the response string : {}", responseString );
            } else {
                LOG.warn( "Could not write into file {}", fileName );
            }
        } catch ( FileNotFoundException fe ) {
            LOG.error( "File not found exception while creating file {}", fileName, fe );
            excelCreated = false;
        } catch ( IOException e ) {
            LOG.error( "IO  exception while creating file {}", fileName, e );
            excelCreated = false;
        } catch ( Throwable thrw ) {
            LOG.error( "Throwable while creating file {}", fileName, thrw );
            excelCreated = false;
        } finally {
            try {
                if ( fileOutput != null )
                    fileOutput.close();
            } catch ( IOException e ) {
                LOG.error( "Exception caught while generating report " + fileName + ": " + e.getMessage() );
                excelCreated = false;
            }
        }
        LOG.debug( "The function createExcelFileAndSaveInAmazonS3 has ended for locationInS3 : {}", responseString );
        return responseString;
    }


    // Make Header Row Bold
    public static void makeRowBold( XSSFWorkbook wb, Row row )
    {
        CellStyle style = wb.createCellStyle();// Create style
        Font font = wb.createFont();// Create font
        font.setBoldweight( Font.BOLDWEIGHT_BOLD );// Make font bold
        style.setFont( font );// set it to bold

        for ( int i = 0; i < row.getLastCellNum(); i++ ) {// For each cell in the row
            row.getCell( i ).setCellStyle( style );// Set the sty;e
        }
    }


    // Make row bold and blue for NPS report
    public static void makeRowBoldAndBlue( XSSFWorkbook wb, Row row )
    {
        CellStyle style = wb.createCellStyle();// Create style
        Font font = wb.createFont();// Create font
        font.setBoldweight( Font.BOLDWEIGHT_BOLD );// Make font bold
        style.setFillForegroundColor( IndexedColors.LIGHT_BLUE.index );
        style.setFillPattern( CellStyle.SOLID_FOREGROUND );
        style.setFont( font );// set it to bold

        for ( int i = 0; i < row.getLastCellNum(); i++ ) {// For each cell in the row
            row.getCell( i ).setCellStyle( style );// Set the sty;e
        }
    }


    @Override
    public List<List<Object>> getUserRankingThisYear( String entityType, Long entityId, int year, int startIndex,
        int batchSize )
    {
        LOG.info( "function to getUserRankingThisYear based on entityType: {} , entityId: {} started ", entityType, entityId );
        List<List<Object>> userRanking = new ArrayList<>();

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            for ( UserRankingThisYearMain userRankingThisYearMain : userRankingThisYearMainDao
                .fetchUserRankingWithProfileForThisYearMain( entityId, year, startIndex, batchSize ) ) {
                List<Object> userRankingThisYearMainList = new ArrayList<>();
                userRankingThisYearMainList.add( userRankingThisYearMain.getUserId() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getRank() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getFirstName() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getLastName() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getRankingScore() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getTotalReviews() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getAverageRating() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getSps() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getCompletedPercentage() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getIsEligible() );
                userRankingThisYearMainList.add( userRankingThisYearMain.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingThisYearMainList );
            }
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            for ( UserRankingThisYearRegion userRankingThisYearRegion : userRankingThisYearRegionDao
                .fetchUserRankingWithProfileForThisYearRegion( entityId, year, startIndex, batchSize ) ) {
                List<Object> userRankingThisYearRegionList = new ArrayList<>();
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getUserId() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getRank() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getFirstName() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getLastName() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getRankingScore() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getTotalReviews() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getAverageRating() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getSps() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getCompletedPercentage() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getIsEligible() );
                userRankingThisYearRegionList.add( userRankingThisYearRegion.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingThisYearRegionList );
            }
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            for ( UserRankingThisYearBranch userRankingThisYearBranch : userRankingThisYearBranchDao
                .fetchUserRankingWithProfileForThisYearBranch( entityId, year, startIndex, batchSize ) ) {
                List<Object> userRankingThisYearBranchList = new ArrayList<>();
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getUserId() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getRank() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getFirstName() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getLastName() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getRankingScore() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getTotalReviews() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getAverageRating() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getSps() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getCompletedPercentage() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getIsEligible() );
                userRankingThisYearBranchList.add( userRankingThisYearBranch.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingThisYearBranchList );
            }
        }
        LOG.info( "function to getUserRankingThisYear based on ended" );

        return userRanking;
    }


    @Override
    public List<List<Object>> getUserRankingThisMonth( String entityType, Long entityId, int month, int year, int startIndex,
        int batchSize )
    {
        List<List<Object>> userRanking = new ArrayList<>();

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            for ( UserRankingThisMonthMain userRankingThisMonthMain : userRankingThisMonthMainDao
                .fetchUserRankingWithProfileForThisMonthMain( entityId, month, year, startIndex, batchSize ) ) {
                List<Object> userRankingThisMonthMainList = new ArrayList<>();
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getUserId() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getRank() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getFirstName() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getLastName() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getRankingScore() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getTotalReviews() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getAverageRating() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getSps() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getCompletedPercentage() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getIsEligible() );
                userRankingThisMonthMainList.add( userRankingThisMonthMain.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingThisMonthMainList );
            }
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            for ( UserRankingThisMonthRegion userRankingThisMonthRegion : userRankingThisMonthRegionDao
                .fetchUserRankingWithProfileForThisMonthRegion( entityId, month, year, startIndex, batchSize ) ) {
                List<Object> userRankingThisMonthRegionList = new ArrayList<>();
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getUserId() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getRank() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getFirstName() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getLastName() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getRankingScore() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getTotalReviews() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getAverageRating() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getSps() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getCompletedPercentage() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getIsEligible() );
                userRankingThisMonthRegionList.add( userRankingThisMonthRegion.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingThisMonthRegionList );
            }
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            for ( UserRankingThisMonthBranch userRankingThisMonthBranch : userRankingThisMonthBranchDao
                .fetchUserRankingWithProfileForThisMonthBranch( entityId, month, year, startIndex, batchSize ) ) {
                List<Object> userRankingThisMonthBranchList = new ArrayList<>();
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getUserId() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getRank() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getFirstName() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getLastName() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getRankingScore() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getTotalReviews() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getAverageRating() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getSps() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getCompletedPercentage() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getIsEligible() );
                userRankingThisMonthBranchList.add( userRankingThisMonthBranch.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingThisMonthBranchList );
            }
        }
        return userRanking;
    }


    @Override
    public List<List<Object>> getUserRankingPastMonth( String entityType, Long entityId, int month, int year, int startIndex,
        int batchSize )
    {

        List<List<Object>> userRanking = new ArrayList<>();

        if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            for ( UserRankingPastMonthRegion userRankingPastMonthRegion : userRankingPastMonthRegionDao
                .fetchUserRankingWithProfileForPastMonthRegion( entityId, month, year, startIndex, batchSize ) ) {
                List<Object> userRankingPastMonthRegionList = new ArrayList<>();
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getUserId() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getRank() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getFirstName() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getLastName() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getRankingScore() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getTotalReviews() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getAverageRating() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getSps() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getCompletedPercentage() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getIsEligible() );
                userRankingPastMonthRegionList.add( userRankingPastMonthRegion.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingPastMonthRegionList );
            }
        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            for ( UserRankingPastMonthMain userRankingPastMonthMain : userRankingPastMonthMainDao
                .fetchUserRankingWithProfileForPastMonthMain( entityId, month, year, startIndex, batchSize ) ) {
                List<Object> userRankingPastMonthMainList = new ArrayList<>();
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getUserId() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getRank() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getFirstName() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getLastName() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getRankingScore() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getTotalReviews() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getAverageRating() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getSps() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getCompletedPercentage() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getIsEligible() );
                userRankingPastMonthMainList.add( userRankingPastMonthMain.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingPastMonthMainList );
            }
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            for ( UserRankingPastMonthBranch userRankingPastMonthBranch : userRankingPastMonthBranchDao
                .fetchUserRankingWithProfileForPastMonthBranch( entityId, month, year, startIndex, batchSize ) ) {
                List<Object> userRankingPastMonthBranchList = new ArrayList<>();
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getUserId() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getRank() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getFirstName() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getLastName() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getRankingScore() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getTotalReviews() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getAverageRating() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getSps() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getCompletedPercentage() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getIsEligible() );
                userRankingPastMonthBranchList.add( userRankingPastMonthBranch.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingPastMonthBranchList );
            }
        }
        return userRanking;
    }


    @Override
    public List<List<Object>> getUserRankingPastYear( String entityType, Long entityId, int year, int startIndex,
        int batchSize )
    {
        List<List<Object>> userRanking = new ArrayList<>();

        if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            for ( UserRankingPastYearRegion userRankingPastYearRegion : userRankingPastYearRegionDao
                .fetchUserRankingWithProfileForPastYearRegion( entityId, year, startIndex, batchSize ) ) {
                List<Object> userRankingPastYearRegionList = new ArrayList<>();
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getUserId() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getRank() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getFirstName() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getLastName() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getRankingScore() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getTotalReviews() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getAverageRating() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getSps() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getCompletedPercentage() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getIsEligible() );
                userRankingPastYearRegionList.add( userRankingPastYearRegion.getProfileImageUrlThumbnail() );
                userRanking.add( userRankingPastYearRegionList );
            }
        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            for ( UserRankingPastYearMain userRankingPastYearMain : userRankingPastYearMainDao
                .fetchUserRankingWithProfileForPastYearMain( entityId, year, startIndex, batchSize ) ) {
                List<Object> userRankingPastYearMainList = new ArrayList<>();
                userRankingPastYearMainList.add( userRankingPastYearMain.getUserId() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getRank() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getFirstName() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getLastName() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getRankingScore() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getTotalReviews() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getAverageRating() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getSps() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getCompletedPercentage() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getIsEligible() );
                userRankingPastYearMainList.add( userRankingPastYearMain.getProfileImageUrlThumbnail() );
                userRanking.add( userRankingPastYearMainList );
            }
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            for ( UserRankingPastYearBranch userRankingPastYearBranch : userRankingPastYearBranchDao
                .fetchUserRankingWithProfileForPastYearBranch( entityId, year, startIndex, batchSize ) ) {
                List<Object> userRankingPastYearBranchList = new ArrayList<>();
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getUserId() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getRank() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getFirstName() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getLastName() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getRankingScore() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getTotalReviews() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getAverageRating() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getSps() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getCompletedPercentage() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getIsEligible() );
                userRankingPastYearBranchList.add( userRankingPastYearBranch.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingPastYearBranchList );
            }
        }
        return userRanking;
    }


    @Override
    public List<List<Object>> getUserRankingPastYears( String entityType, Long entityId, int startIndex, int batchSize )
    {
        List<List<Object>> userRanking = new ArrayList<>();

        if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            for ( UserRankingPastYearsRegion userRankingPastYearsRegion : userRankingPastYearsRegionDao
                .fetchUserRankingWithProfileForPastYearsRegion( entityId, startIndex, batchSize ) ) {
                List<Object> userRankingPastYearsRegionList = new ArrayList<>();
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getUserId() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getRank() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getFirstName() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getLastName() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getRankingScore() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getTotalReviews() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getAverageRating() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getSps() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getCompletedPercentage() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getIsEligible() );
                userRankingPastYearsRegionList.add( userRankingPastYearsRegion.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingPastYearsRegionList );
            }
        } else if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            for ( UserRankingPastYearsMain userRankingPastYearsMain : userRankingPastYearsMainDao
                .fetchUserRankingWithProfileForPastYearsMain( entityId, startIndex, batchSize ) ) {
                List<Object> userRankingPastYearsMainList = new ArrayList<>();
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getUserId() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getRank() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getFirstName() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getLastName() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getRankingScore() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getTotalReviews() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getAverageRating() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getSps() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getCompletedPercentage() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getIsEligible() );
                userRankingPastYearsMainList.add( userRankingPastYearsMain.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingPastYearsMainList );
            }
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            for ( UserRankingPastYearsBranch userRankingPastYearsBranch : userRankingPastYearsBranchDao
                .fetchUserRankingWithProfileForPastYearsBranch( entityId, startIndex, batchSize ) ) {
                List<Object> userRankingPastYearsBranchList = new ArrayList<>();
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getUserId() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getRank() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getFirstName() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getLastName() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getRankingScore() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getTotalReviews() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getAverageRating() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getSps() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getCompletedPercentage() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getIsEligible() );
                userRankingPastYearsBranchList.add( userRankingPastYearsBranch.getProfileImageUrlThumbnail() );

                userRanking.add( userRankingPastYearsBranchList );
            }
        }
        return userRanking;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingRankCountThisYear( long userId, long entityId, String entityType, int year,
        int batchSize ) throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisYearMainDao.fetchUserRankingCountForThisYearMain( entityId, year ) );
            int rank = userRankingThisYearMainDao.fetchUserRankingRankForThisYearMain( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisYearBranchDao.fetchUserRankingCountForThisYearBranch( entityId, year ) );
            int rank = userRankingThisYearBranchDao.fetchUserRankingRankForThisYearBranch( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisYearRegionDao.fetchUserRankingCountForThisYearRegion( entityId, year ) );
            int rank = userRankingThisYearRegionDao.fetchUserRankingRankForThisYearRegion( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingRankCountThisMonth( long userId, long entityId, String entityType, int year,
        int month, int batchSize ) throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisMonthMainDao.fetchUserRankingCountForThisMonthMain( entityId, year, month ) );
            int rank = userRankingThisMonthMainDao.fetchUserRankingRankForThisMonthMain( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisMonthBranchDao.fetchUserRankingCountForThisMonthBranch( entityId, month, year ) );
            int rank = userRankingThisMonthBranchDao.fetchUserRankingRankForThisMonthBranch( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisMonthRegionDao.fetchUserRankingCountForThisMonthRegion( entityId, month, year ) );
            int rank = userRankingThisMonthRegionDao.fetchUserRankingRankForThisMonthRegion( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );
        }

        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingRankCountPastYear( long userId, long entityId, String entityType, int year,
        int batchSize ) throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearMainDao.fetchUserRankingCountForPastYearMain( entityId, year ) );
            int rank = userRankingPastYearMainDao.fetchUserRankingRankForPastYearMain( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearBranchDao.fetchUserRankingCountForPastYearBranch( entityId, year ) );
            int rank = userRankingPastYearBranchDao.fetchUserRankingRankForPastYearBranch( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearRegionDao.fetchUserRankingCountForPastYearRegion( entityId, year ) );
            int rank = userRankingPastYearRegionDao.fetchUserRankingRankForPastYearRegion( userId, entityId, year );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingRankCountPastYears( long userId, long entityId, String entityType, int batchSize )
        throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT, userRankingPastYearsMainDao.fetchUserRankingCountForPastYearsMain( entityId ) );
            int rank = userRankingPastYearsMainDao.fetchUserRankingRankForPastYearsMain( userId, entityId );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearsBranchDao.fetchUserRankingCountForPastYearsBranch( entityId ) );
            int rank = userRankingPastYearsBranchDao.fetchUserRankingRankForPastYearsBranch( userId, entityId );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearsRegionDao.fetchUserRankingCountForPastYearsRegion( entityId ) );
            int rank = userRankingPastYearsRegionDao.fetchUserRankingRankForPastYearsRegion( userId, entityId );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingRankCountPastMonth( long userId, long entityId, String entityType, int year,
        int month, int batchSize ) throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastMonthMainDao.fetchUserRankingCountForPastMonthMain( entityId, year, month ) );
            int rank = userRankingPastMonthMainDao.fetchUserRankingRankForPastMonthMain( userId, entityId, year, month );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }

            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastMonthBranchDao.fetchUserRankingCountForPastMonthBranch( entityId, month, year ) );
            int rank = userRankingPastMonthBranchDao.fetchUserRankingRankForPastMonthBranch( userId, entityId, year, month );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastMonthRegionDao.fetchUserRankingCountForPastMonthRegion( entityId, month, year ) );
            int rank = userRankingPastMonthRegionDao.fetchUserRankingRankForPastMonthRegion( userId, entityId, year, month );
            // get the mod to determine startIndex
            int startIndex = 0;
            int mod = ( rank % batchSize );
            int diff = ( batchSize / 2 );

            if ( rank >= ( batchSize / 2 ) ) {
                startIndex = rank - diff;
            } else {
                startIndex = rank - mod;
            }
            rankingCountStartIndex.put( STARTINDEX, startIndex );
        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingCountThisYear( long entityId, String entityType, int year, int batchSize )
        throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisYearMainDao.fetchUserRankingCountForThisYearMain( entityId, year ) );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisYearBranchDao.fetchUserRankingCountForThisYearBranch( entityId, year ) );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisYearRegionDao.fetchUserRankingCountForThisYearRegion( entityId, year ) );
        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingCountThisMonth( long entityId, String entityType, int year, int month,
        int batchSize ) throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisMonthMainDao.fetchUserRankingCountForThisMonthMain( entityId, year, month ) );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisMonthBranchDao.fetchUserRankingCountForThisMonthBranch( entityId, month, year ) );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingThisMonthRegionDao.fetchUserRankingCountForThisMonthRegion( entityId, month, year ) );
        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingCountPastYear( long entityId, String entityType, int year, int batchSize )
        throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearMainDao.fetchUserRankingCountForPastYearMain( entityId, year ) );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearBranchDao.fetchUserRankingCountForPastYearBranch( entityId, year ) );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearRegionDao.fetchUserRankingCountForPastYearRegion( entityId, year ) );
        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingCountPastMonth( long entityId, String entityType, int year, int month,
        int batchSize ) throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastMonthMainDao.fetchUserRankingCountForPastMonthMain( entityId, year, month ) );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastMonthBranchDao.fetchUserRankingCountForPastMonthBranch( entityId, month, year ) );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastMonthRegionDao.fetchUserRankingCountForPastMonthRegion( entityId, month, year ) );
        }
        return rankingCountStartIndex;
    }


    @Override
    @Transactional ( value = "transactionManagerForReporting")
    public Map<String, Object> fetchRankingCountPastYears( long entityId, String entityType, int batchSize )
        throws NonFatalException
    {
        Map<String, Object> rankingCountStartIndex = new HashMap<>();
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT, userRankingPastYearsMainDao.fetchUserRankingCountForPastYearsMain( entityId ) );
        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearsBranchDao.fetchUserRankingCountForPastYearsBranch( entityId ) );
        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            rankingCountStartIndex.put( COUNT,
                userRankingPastYearsRegionDao.fetchUserRankingCountForPastYearsRegion( entityId ) );
        }
        return rankingCountStartIndex;
    }


    @Override
    public RankingRequirements updateRankingRequirements( int minDaysOfRegistration, float minCompletedPercentage,
        int minNoOfReviews, double monthOffset, int yearOffset )
    {
        RankingRequirements rankingRequirements = new RankingRequirements();
        rankingRequirements.setMinCompletedPercentage( minCompletedPercentage );
        rankingRequirements.setMinDaysOfRegistration( minDaysOfRegistration );
        rankingRequirements.setMinNoOfReviews( minNoOfReviews );
        rankingRequirements.setMonthOffset( monthOffset );
        rankingRequirements.setYearOffset( yearOffset );
        return rankingRequirements;

    }


    // Reporting Ranking Requirements update
    @Override
    public RankingRequirements updateRankingRequirementsMongo( String collection, OrganizationUnitSettings unitSettings,
        RankingRequirements rankingRequirements ) throws InvalidInputException
    {
        if ( rankingRequirements == null ) {
            throw new InvalidInputException( "Social Tokens passed can not be null" );
        }
        LOG.info( "Updating Ranking Requirements Information" );
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_RANKING_REQUIREMENTS, rankingRequirements, unitSettings, collection );
        LOG.info( "Ranking Requirements updated successfully" );
        return rankingRequirements;
    }


    @Override
    public Long getRegionIdFromBranchId( long branchId )
    {
        return branchDao.getRegionIdByBranchId( branchId );
    }


    @Override
    public List<List<Object>> getScoreStatsForOverall( Long entityId, String entityType, int currentMonth, int currentYear )
    {

        List<List<Object>> scoreStatsForOverall = new ArrayList<>();
        int startMonth = 0;
        int startYear = currentYear - 1;
        // current month is usually
        // the graph shows 12 months in which the month 12 months back is +1 from
        // current month except for when current month is dec
        if ( currentMonth < 12 ) {
            startMonth = currentMonth + 1;
        } else if ( currentMonth == 12 ) {
            startMonth = 1;
        }
        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {

            for ( ScoreStatsOverallCompany scoreStatsOverallCompany : scoreStatsOverallCompanyDao
                .fetchScoreStatsOverallForCompany( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsOverallCompanyList = new ArrayList<>();
                double averageScore = scoreStatsOverallCompany.getAvgScore();
                scoreStatsOverallCompanyList
                    .add( scoreStatsOverallCompany.getMonthVal() + "/" + scoreStatsOverallCompany.getYearVal() );
                scoreStatsOverallCompanyList.add( averageScore );
                scoreStatsForOverall.add( scoreStatsOverallCompanyList );
            }

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {

            for ( ScoreStatsOverallRegion scoreStatsOverallRegion : scoreStatsOverallRegionDao
                .fetchScoreStatsOverallForRegion( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsOverallRegionList = new ArrayList<>();
                double averageScore = scoreStatsOverallRegion.getAvgScore();
                scoreStatsOverallRegionList
                    .add( scoreStatsOverallRegion.getMonthVal() + "/" + scoreStatsOverallRegion.getYearVal() );
                scoreStatsOverallRegionList.add( averageScore );
                scoreStatsForOverall.add( scoreStatsOverallRegionList );
            }

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {

            for ( ScoreStatsOverallBranch scoreStatsOverallBranch : scoreStatsOverallBranchDao
                .fetchScoreStatsOverallForBranch( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsOverallBranchList = new ArrayList<>();
                double averageScore = scoreStatsOverallBranch.getAvgScore();
                scoreStatsOverallBranchList
                    .add( scoreStatsOverallBranch.getMonthVal() + "/" + scoreStatsOverallBranch.getYearVal() );
                scoreStatsOverallBranchList.add( averageScore );
                scoreStatsForOverall.add( scoreStatsOverallBranchList );
            }

        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {

            for ( ScoreStatsOverallUser scoreStatsOverallUser : scoreStatsOverallUserDao
                .fetchScoreStatsOverallForUser( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsOverallUserList = new ArrayList<>();
                double averageScore = scoreStatsOverallUser.getAvgScore();
                scoreStatsOverallUserList.add( scoreStatsOverallUser.getMonthVal() + "/" + scoreStatsOverallUser.getYearVal() );
                scoreStatsOverallUserList.add( averageScore );
                scoreStatsForOverall.add( scoreStatsOverallUserList );
            }

        }

        return scoreStatsForOverall;
    }


    @Override
    public List<List<Object>> getScoreStatsForQuestion( Long entityId, String entityType, int currentMonth, int currentYear )
    {
        LOG.debug( "Service method call for get score stats for questions." );
        List<List<Object>> scoreStatsForQuestion = new ArrayList<>();
        int startMonth = 0;
        int startYear = currentYear - 1;
        // current month is usually
        // the graph shows 12 months in which the month 12 months back is +1 from
        // current month except for when current month is dec
        if ( currentMonth < 12 ) {
            startMonth = currentMonth + 1;
        } else if ( currentMonth == 12 ) {
            startMonth = 1;
        }

        if ( entityType.equals( CommonConstants.COMPANY_ID_COLUMN ) ) {
            for ( ScoreStatsQuestionCompany scoreStatsQuestionCompany : scoreStatsQuestionCompanyDao
                .fetchScoreStatsQuestionForCompany( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsQuestionCompanyList = new ArrayList<>();
                double averageScore = scoreStatsQuestionCompany.getAvgScore();
                scoreStatsQuestionCompanyList.add( scoreStatsQuestionCompany.getQuestionId() );
                scoreStatsQuestionCompanyList.add( scoreStatsQuestionCompany.getQuestion() );
                scoreStatsQuestionCompanyList
                    .add( scoreStatsQuestionCompany.getMonthVal() + "/" + scoreStatsQuestionCompany.getYearVal() );
                scoreStatsQuestionCompanyList.add( averageScore );
                scoreStatsForQuestion.add( scoreStatsQuestionCompanyList );
            }

        } else if ( entityType.equals( CommonConstants.REGION_ID_COLUMN ) ) {
            for ( ScoreStatsQuestionRegion scoreStatsQuestionRegion : scoreStatsQuestionRegionDao
                .fetchScoreStatsQuestionForRegion( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsQuestionRegionList = new ArrayList<>();
                double averageScore = scoreStatsQuestionRegion.getAvgScore();
                scoreStatsQuestionRegionList.add( scoreStatsQuestionRegion.getQuestionId() );
                scoreStatsQuestionRegionList.add( scoreStatsQuestionRegion.getQuestion() );
                scoreStatsQuestionRegionList
                    .add( scoreStatsQuestionRegion.getMonthVal() + "/" + scoreStatsQuestionRegion.getYearVal() );
                scoreStatsQuestionRegionList.add( averageScore );
                scoreStatsForQuestion.add( scoreStatsQuestionRegionList );
            }

        } else if ( entityType.equals( CommonConstants.BRANCH_ID_COLUMN ) ) {
            for ( ScoreStatsQuestionBranch scoreStatsQuestionBranch : scoreStatsQuestionBranchDao
                .fetchScoreStatsQuestionForBranch( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsQuestionBranchList = new ArrayList<>();
                double averageScore = scoreStatsQuestionBranch.getAvgScore();
                scoreStatsQuestionBranchList.add( scoreStatsQuestionBranch.getQuestionId() );
                scoreStatsQuestionBranchList.add( scoreStatsQuestionBranch.getQuestion() );
                scoreStatsQuestionBranchList
                    .add( scoreStatsQuestionBranch.getMonthVal() + "/" + scoreStatsQuestionBranch.getYearVal() );
                scoreStatsQuestionBranchList.add( averageScore );
                scoreStatsForQuestion.add( scoreStatsQuestionBranchList );
            }
        } else if ( entityType.equals( CommonConstants.AGENT_ID_COLUMN ) ) {

            for ( ScoreStatsQuestionUser scoreStatsQuestionUser : scoreStatsQuestionUserDao
                .fetchScoreStatsQuestionForUser( entityId, startMonth, startYear, currentMonth, currentYear ) ) {
                List<Object> scoreStatsQuestionUserList = new ArrayList<>();
                double averageScore = scoreStatsQuestionUser.getAvgScore();
                scoreStatsQuestionUserList.add( scoreStatsQuestionUser.getQuestionId() );
                scoreStatsQuestionUserList.add( scoreStatsQuestionUser.getQuestion() );
                scoreStatsQuestionUserList
                    .add( scoreStatsQuestionUser.getMonthVal() + "/" + scoreStatsQuestionUser.getYearVal() );
                scoreStatsQuestionUserList.add( averageScore );
                scoreStatsForQuestion.add( scoreStatsQuestionUserList );
            }
        }
        return scoreStatsForQuestion;
    }


    @Override
    public List<UserRanking> getTopTenUserRankingsThisMonthForAHierarchy( String profileLevel, long entityId,
        int monthUnderConcern, int year ) throws InvalidInputException
    {
        LOG.debug( "method getTopTenUserRankingsForACompany() running" );

        if ( entityId <= 0 ) {
            LOG.error( "companyId is invalid" );
            throw new InvalidInputException( "company Identifier is invalid" );
        } else if ( monthUnderConcern < 1 || monthUnderConcern > 12 ) {
            LOG.error( "month Under Concern is invalid" );
            throw new InvalidInputException( "month Under Concern is invalid" );
        } else if ( year <= 0 ) {
            LOG.error( "year value is invalid" );
            throw new InvalidInputException( "year value is invalid" );
        }

        if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileLevel ) ) {
            return getUserRankingFromMainEntity(
                userRankingPastMonthMainDao.fetchTopTenUserRankingsForACompany( entityId, monthUnderConcern, year ) );
        } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileLevel ) ) {
            return getUserRankingFromRegionEntity(
                userRankingPastMonthRegionDao.fetchTopTenUserRankingsForARegion( entityId, monthUnderConcern, year ) );
        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileLevel ) ) {
            return getUserRankingFromBranchEntity(
                userRankingPastMonthBranchDao.fetchTopTenUserRankingsForABranch( entityId, monthUnderConcern, year ) );
        } else {
            LOG.warn( "Invalid profile level" );
            throw new InvalidInputException( "Invalid profile level" );
        }
    }


    private List<UserRanking> getUserRankingFromBranchEntity( List<UserRankingPastMonthBranch> userRankings )
    {
        List<UserRanking> userRanks = new ArrayList<>();
        for ( UserRankingPastMonthBranch userRank : userRankings ) {

            UserRanking newRankObject = new UserRanking();
            newRankObject.setAverageRating( userRank.getAverageRating() );
            newRankObject.setBranchId( userRank.getBranchId() );
            newRankObject.setCompanyId( userRank.getCompanyId() );
            newRankObject.setCompleted( userRank.getCompleted() );
            newRankObject.setCompletedPercentage( userRank.getCompletedPercentage() );
            newRankObject.setDaysOfRegistration( userRank.getDaysOfRegistration() );
            newRankObject.setEmailId( userRank.getEmailId() );
            newRankObject.setFirstName( userRank.getFirstName() );
            newRankObject.setIsEligible( userRank.getIsEligible() );
            newRankObject.setLastName( userRank.getLastName() );
            newRankObject.setMonth( userRank.getMonth() );
            newRankObject.setNmlsId( userRank.getNmlsId() );
            newRankObject.setRank( userRank.getRank() );
            newRankObject.setRankingScore( userRank.getRankingScore() );
            newRankObject.setRegionId( userRank.getRegionId() );
            newRankObject.setSent( userRank.getSent() );
            newRankObject.setSps( userRank.getSps() );
            newRankObject.setTotalReviews( userRank.getTotalReviews() );
            newRankObject.setUserId( userRank.getUserId() );
            newRankObject.setYear( userRank.getYear() );

            userRanks.add( newRankObject );
        }
        return userRanks;
    }


    private List<UserRanking> getUserRankingFromRegionEntity( List<UserRankingPastMonthRegion> userRankings )
    {
        List<UserRanking> userRanks = new ArrayList<>();
        for ( UserRankingPastMonthRegion userRank : userRankings ) {

            UserRanking newRankObject = new UserRanking();
            newRankObject.setAverageRating( userRank.getAverageRating() );
            newRankObject.setBranchId( userRank.getBranchId() );
            newRankObject.setCompanyId( userRank.getCompanyId() );
            newRankObject.setCompleted( userRank.getCompleted() );
            newRankObject.setCompletedPercentage( userRank.getCompletedPercentage() );
            newRankObject.setDaysOfRegistration( userRank.getDaysOfRegistration() );
            newRankObject.setEmailId( userRank.getEmailId() );
            newRankObject.setFirstName( userRank.getFirstName() );
            newRankObject.setIsEligible( userRank.getIsEligible() );
            newRankObject.setLastName( userRank.getLastName() );
            newRankObject.setMonth( userRank.getMonth() );
            newRankObject.setNmlsId( userRank.getNmlsId() );
            newRankObject.setRank( userRank.getRank() );
            newRankObject.setRankingScore( userRank.getRankingScore() );
            newRankObject.setRegionId( userRank.getRegionId() );
            newRankObject.setSent( userRank.getSent() );
            newRankObject.setSps( userRank.getSps() );
            newRankObject.setTotalReviews( userRank.getTotalReviews() );
            newRankObject.setUserId( userRank.getUserId() );
            newRankObject.setYear( userRank.getYear() );

            userRanks.add( newRankObject );
        }
        return userRanks;
    }


    private List<UserRanking> getUserRankingFromMainEntity( List<UserRankingPastMonthMain> userRankings )
    {
        List<UserRanking> userRanks = new ArrayList<>();
        for ( UserRankingPastMonthMain userRank : userRankings ) {

            UserRanking newRankObject = new UserRanking();
            newRankObject.setAverageRating( userRank.getAverageRating() );
            newRankObject.setBranchId( userRank.getBranchId() );
            newRankObject.setCompanyId( userRank.getCompanyId() );
            newRankObject.setCompleted( userRank.getCompleted() );
            newRankObject.setCompletedPercentage( userRank.getCompletedPercentage() );
            newRankObject.setDaysOfRegistration( userRank.getDaysOfRegistration() );
            newRankObject.setEmailId( userRank.getEmailId() );
            newRankObject.setFirstName( userRank.getFirstName() );
            newRankObject.setIsEligible( userRank.getIsEligible() );
            newRankObject.setLastName( userRank.getLastName() );
            newRankObject.setMonth( userRank.getMonth() );
            newRankObject.setNmlsId( userRank.getNmlsId() );
            newRankObject.setRank( userRank.getRank() );
            newRankObject.setRankingScore( userRank.getRankingScore() );
            newRankObject.setRegionId( userRank.getRegionId() );
            newRankObject.setSent( userRank.getSent() );
            newRankObject.setSps( userRank.getSps() );
            newRankObject.setTotalReviews( userRank.getTotalReviews() );
            newRankObject.setUserId( userRank.getUserId() );
            newRankObject.setYear( userRank.getYear() );

            userRanks.add( newRankObject );
        }
        return userRanks;
    }


    @Override
    public Map<Integer, Digest> getDigestDataForLastFourMonths( String profileLevel, String entityName, long entityId,
        int monthUnderConcern, int year ) throws InvalidInputException, NoRecordsFetchedException
    {

        LOG.debug( "method getDigestDataForLastFourMonths() started" );

        if ( entityId == 0 ) {
            LOG.error( "Id cannot be zero" );
            throw new InvalidInputException( "Identifier can not be less than or equal to zero." );
        } else if ( year == 0 ) {
            LOG.error( "year cannot be zero" );
            throw new InvalidInputException( "Year can not be less than or equal to zero." );
        } else if ( monthUnderConcern < 1 || monthUnderConcern > 12 ) {
            LOG.error( "current month should be in the range 1 - 12" );
            throw new InvalidInputException( "Current month should be in the range 1 - 12." );
        } else if ( StringUtils.isEmpty( profileLevel ) ) {
            LOG.warn( "Invalid profile" );
            throw new InvalidInputException( "Invalid profile" );
        }

        Map<Integer, Digest> digestMap = new HashMap<>();

        Digest primaryDigest = overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId,
            monthUnderConcern, year );

        if ( primaryDigest == null || primaryDigest.getMonth() != monthUnderConcern ) {
            LOG.error( "No digest found for the the month under concern for {}: {}", profileLevel, entityId );
            throw new NoRecordsFetchedException( "No digest found for the the month under concern" );
        }

        digestMap.put( monthUnderConcern, primaryDigest );

        if ( monthUnderConcern == 3 ) {

            digestMap.put( 2, overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 2, year ) );
            digestMap.put( 1, overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 1, year ) );
            digestMap.put( 12,
                overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 12, year - 1 ) );

        } else if ( monthUnderConcern == 2 ) {
            digestMap.put( 1, overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 1, year ) );
            digestMap.put( 12,
                overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 12, year - 1 ) );
            digestMap.put( 11,
                overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 11, year - 1 ) );

        } else if ( monthUnderConcern == 1 ) {
            digestMap.put( 12,
                overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 12, year - 1 ) );
            digestMap.put( 11,
                overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 11, year - 1 ) );
            digestMap.put( 10,
                overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName, entityId, 10, year - 1 ) );

        } else {
            digestMap.put( monthUnderConcern - 1, overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName,
                entityId, monthUnderConcern - 1, year ) );
            digestMap.put( monthUnderConcern - 2, overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName,
                entityId, monthUnderConcern - 2, year ) );
            digestMap.put( monthUnderConcern - 3, overviewManagement.fetchDigestDataForAHierarchy( profileLevel, entityName,
                entityId, monthUnderConcern - 3, year ) );
        }

        LOG.debug( "method getDigestDataForLastFourMonths() finished" );
        return digestMap;
    }


    @Override
    public MonthlyDigestAggregate prepareMonthlyDigestMailData( String profileLevel, long entityId, String entityName,
        int monthUnderConcern, int year ) throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException
    {
        LOG.debug( "method prepareMonthlyDigestMailData() started" );

        if ( entityId == 0 ) {
            LOG.warn( "entityId cannot be zero" );
            throw new InvalidInputException( "entity Identifier can not be less than or equal to zero." );
        } else if ( StringUtils.isEmpty( entityName ) ) {
            LOG.warn( "entity name is not specified." );
            throw new InvalidInputException( "entity name is not specified." );
        } else if ( year == 0 ) {
            LOG.warn( "year cannot be zero" );
            throw new InvalidInputException( "Year can not be less than or equal to zero." );
        } else if ( monthUnderConcern < 1 || monthUnderConcern > 12 ) {
            LOG.warn( "current month should be in the range 1 - 12" );
            throw new InvalidInputException( "Current month should be in the range 1 - 12." );
        } else if ( StringUtils.isEmpty( profileLevel ) ) {
            LOG.warn( "Profile level is not specified" );
            throw new InvalidInputException( "Profile level is not specified" );
        }

        MonthlyDigestAggregate digestAggregate = buildMonthlyDigestAggregate( profileLevel, entityId, entityName,
            monthUnderConcern, year,
            buildOrderedMonthlyDigestList(
                getDigestDataForLastFourMonths( profileLevel, entityName, entityId, monthUnderConcern, year ),
                monthUnderConcern ),
            getTopTenUserRankingsThisMonthForAHierarchy( profileLevel, entityId, monthUnderConcern, year ) );

        LOG.debug( "method prepareMonthlyDigestMailData() finished" );
        return digestAggregate;
    }


    private MonthlyDigestAggregate buildMonthlyDigestAggregate( String profileLevel, long entityId, String entityName,
        int monthUnderConcern, int year, List<Digest> digestList, List<UserRanking> userRankings )
    {
        LOG.debug( "method buildMonthlyDigestAggregate() started" );

        MonthlyDigestAggregate digestAggregate = new MonthlyDigestAggregate();
        digestAggregate.setProfileLevel( profileLevel );
        digestAggregate.setEntityId( entityId );
        digestAggregate.setEntityName( entityName );
        digestAggregate.setMonthUnderConcern( new DateFormatSymbols().getMonths()[monthUnderConcern - 1] );
        digestAggregate.setYearUnderConcern( String.valueOf( year ) );


        boolean hasDigestData = false;
        boolean hasUserCountData = false; 

        for ( int i = 0; i < digestList.size(); i++ ) {
            if (  i != ( digestList.size() - 1 ) ) {
                if( !digestList.get( i ).isDigestRecordNull() )
                    hasDigestData = true;
                if( !digestList.get( i ).getHasZeroUserCount() )
                    hasUserCountData = true;
                if( hasDigestData && hasUserCountData )
                    break;
            }
        }

        digestAggregate.setDigestDataAbsent( hasDigestData ? false : true );
        digestAggregate.setUserCountDataAbsent( hasUserCountData ? false : true );
        
        if ( hasDigestData || hasUserCountData ) {

            // set NPS flag
            digestAggregate.setHavingNpsSection( checkForNpsQuestion( profileLevel, entityId ) );

            // initialize digestTemplate list
            digestAggregate.setDigestList( new ArrayList<DigestTemplateData>() );

            // start populating with appropriate data for the Digest template
            initializeAndPopulateDigestTemplateData( digestAggregate, digestList, monthUnderConcern );

            // create and add the Digest Dependent Data in HTML format
            constructAndPopulateChangeIndicatorIconsAndConclusionTextsForDigest( digestAggregate, digestList );

            // create rows of users with their ranking in HTML format
            buildUserRankingRows( digestAggregate, userRankings );

            // create NPS section if NPS question enabled
            buildNpsSection( digestAggregate );

        }

        LOG.debug( "method buildMonthlyDigestAggregate() finished" );
        return digestAggregate;
    }


    private void initializeAndPopulateDigestTemplateData( MonthlyDigestAggregate digestAggregate, List<Digest> digestList,
        int monthUnderConcern )
    {
        if ( digestList != null && digestAggregate != null && digestList.get( 0 ) != null ) {

            // get the month strings
            List<String> months = buildMonthStringsForDigest( monthUnderConcern );

            // populate digest data for three months in total
            for ( int i = 0; i < 3; i++ ) {

                Digest digest = null;
                DigestTemplateData templateData = new DigestTemplateData();

                try {
                    digest = digestList.get( i );
                } catch ( IndexOutOfBoundsException error ) {
                    LOG.warn( "Unable to get fetch digest for month: {} for {}: {}", i, digestAggregate.getProfileLevel(),
                        digestAggregate.getEntityId() );
                }

                // populate DigestTemplateData with relevant values
                templateData.setMonth( months.get( i ) );
                templateData.setYear( ( digest != null && digest.getYear() != 0 ) ? String.valueOf( digest.getYear() )
                    : CommonConstants.NOT_AVAILABLE );
                templateData.setAverageScoreRating( ( digest != null ) ? utils.formatNumber( 2, digest.getAverageScoreRating() )
                    : CommonConstants.NOT_AVAILABLE );


                templateData.setCompletedTransactions(
                    ( digest != null ) ? String.valueOf( digest.getCompletedTransactions() ) : CommonConstants.NOT_AVAILABLE );
                templateData.setTotalTransactions(
                    ( digest != null ) ? String.valueOf( digest.getTotalTransactions() ) : CommonConstants.NOT_AVAILABLE );
                templateData.setSurveyCompletionRate(
                    ( digest != null ) ? utils.formatNumber( 2, digest.getSurveyCompletionRate() ) + "%"
                        : CommonConstants.NOT_AVAILABLE );
                templateData
                    .setSps(
                        ( digest != null && digest.getSps() != null )
                            ? String.valueOf(
                                digest.getSps().doubleValue() > 0 ? "+" + utils.formatNumber( 2, digest.getSps().doubleValue() )
                                    : utils.formatNumber( 2, digest.getSps().doubleValue() ) )
                            : CommonConstants.NOT_AVAILABLE );
                templateData.setPromoters(
                    ( digest != null ) ? String.valueOf( digest.getPromoters() ) : CommonConstants.NOT_AVAILABLE );
                templateData.setDetractors(
                    ( digest != null ) ? String.valueOf( digest.getDetractors() ) : CommonConstants.NOT_AVAILABLE );
                templateData
                    .setPassives( ( digest != null ) ? String.valueOf( digest.getPassives() ) : CommonConstants.NOT_AVAILABLE );
                templateData.setTotalCompletedReviews(
                    ( digest != null ) ? String.valueOf( digest.getTotalCompletedReviews() ) : CommonConstants.NOT_AVAILABLE );

                // for NPS section
                templateData
                    .setNps(
                        ( digest != null && digest.getNps() != null )
                            ? String.valueOf(
                                digest.getNps().doubleValue() > 0 ? "+" + utils.formatNumber( 2, digest.getNps().doubleValue() )
                                    : utils.formatNumber( 2, digest.getNps().doubleValue() ) )
                            : CommonConstants.NOT_AVAILABLE );
                templateData.setNpsPromoters(
                    ( digest != null ) ? String.valueOf( digest.getNpsPromoters() ) : CommonConstants.NOT_AVAILABLE );
                templateData.setNpsDetractors(
                    ( digest != null ) ? String.valueOf( digest.getNpsDetractors() ) : CommonConstants.NOT_AVAILABLE );
                templateData.setNpsPassives(
                    ( digest != null ) ? String.valueOf( digest.getNpsPassives() ) : CommonConstants.NOT_AVAILABLE );
                templateData.setTotalCompletedNpsReviews( ( digest != null )
                    ? String.valueOf( digest.getNpsDetractors() + digest.getNpsPassives() + digest.getNpsPromoters() )
                    : CommonConstants.NOT_AVAILABLE );

                templateData.setUserCount( digest != null
                    ? String.valueOf( digest.getUserCount() + ( digest.getUserCount() > 1 ? " Users" : " User" ) )
                    : "0 Users" );

                digestAggregate.getDigestList().add( i, templateData );
            }

        }
    }


    private List<Digest> buildOrderedMonthlyDigestList( Map<Integer, Digest> digestMap, int monthUnderConcern )
    {
        List<Digest> digestList = new ArrayList<>( 4 );

        digestList.add( 0, digestMap.get( monthUnderConcern ) );

        if ( monthUnderConcern == 1 ) {
            digestList.add( 1, digestMap.get( 12 ) );
            digestList.add( 2, digestMap.get( 11 ) );
            digestList.add( 3, digestMap.get( 10 ) );

        } else if ( monthUnderConcern == 2 ) {
            digestList.add( 1, digestMap.get( 1 ) );
            digestList.add( 2, digestMap.get( 12 ) );
            digestList.add( 3, digestMap.get( 11 ) );

        } else if ( monthUnderConcern == 3 ) {
            digestList.add( 1, digestMap.get( 2 ) );
            digestList.add( 2, digestMap.get( 1 ) );
            digestList.add( 3, digestMap.get( 12 ) );

        } else {
            digestList.add( 1, digestMap.get( monthUnderConcern - 1 ) );
            digestList.add( 2, digestMap.get( monthUnderConcern - 2 ) );
            digestList.add( 3, digestMap.get( monthUnderConcern - 3 ) );
        }

        return digestList;
    }


    private void constructAndPopulateChangeIndicatorIconsAndConclusionTextsForDigest( MonthlyDigestAggregate digestAggregate,
        List<Digest> digestList )
    {
        // change indicators
        String increasedIndicatorIcon = digestMailHelper.getDigestMailIncreasedIndicatorIcon();
        String droppedIndicatorIcon = digestMailHelper.getDigestMailDroppedIndicatorIcon();
        String noChangeIndicatorIcon = digestMailHelper.getDigestMailNoChangeIndicatorIcon();
        String notAvailableIndicatorIcon = digestMailHelper.getDigestMailNotAvailableIndicatorIcon();

        // --- average score rating
        double avgScoreRating0 = digestList.get( 0 ) != null ? digestList.get( 0 ).getAverageScoreRating() : 0d;
        double avgScoreRating1 = digestList.get( 1 ) != null ? digestList.get( 1 ).getAverageScoreRating() : 0d;
        double avgScoreRating2 = digestList.get( 2 ) != null ? digestList.get( 2 ).getAverageScoreRating() : 0d;
        double avgScoreRating3 = digestList.get( 3 ) != null ? digestList.get( 3 ).getAverageScoreRating() : 0d;

        // --- survey completion rate
        double surveyCompletionRate0 = digestList.get( 0 ) != null ? digestList.get( 0 ).getSurveyCompletionRate() : 0d;
        double surveyCompletionRate1 = digestList.get( 1 ) != null ? digestList.get( 1 ).getSurveyCompletionRate() : 0d;
        double surveyCompletionRate2 = digestList.get( 2 ) != null ? digestList.get( 2 ).getSurveyCompletionRate() : 0d;
        double surveyCompletionRate3 = digestList.get( 3 ) != null ? digestList.get( 3 ).getSurveyCompletionRate() : 0d;

        // --- SPS score
        Double sps0 = digestList.get( 0 ) != null ? digestList.get( 0 ).getSps() : null;
        Double sps1 = digestList.get( 1 ) != null ? digestList.get( 1 ).getSps() : null;
        Double sps2 = digestList.get( 2 ) != null ? digestList.get( 2 ).getSps() : null;
        Double sps3 = digestList.get( 3 ) != null ? digestList.get( 3 ).getSps() : null;


        // --- NPS score
        Double nps0 = digestList.get( 0 ) != null ? digestList.get( 0 ).getNps() : null;
        Double nps1 = digestList.get( 1 ) != null ? digestList.get( 1 ).getNps() : null;
        Double nps2 = digestList.get( 2 ) != null ? digestList.get( 2 ).getNps() : null;
        Double nps3 = digestList.get( 3 ) != null ? digestList.get( 3 ).getNps() : null;

        // correct user count
        long userCount0 = Long
            .parseLong( StringUtils.split( digestAggregate.getDigestList().get( 0 ).getUserCount(), " " )[0] );
        long userCount1 = Long
            .parseLong( StringUtils.split( digestAggregate.getDigestList().get( 1 ).getUserCount(), " " )[0] );

        long transcationCount0 = digestList.get( 0 ) != null ? digestList.get( 0 ).getTotalTransactions() : 0l;
        long transcationCount1 = digestList.get( 1 ) != null ? digestList.get( 1 ).getTotalTransactions() : 0l;

        long totalCompletedReviews0 = digestList.get( 0 ) != null ? digestList.get( 0 ).getDetractors()
            + digestList.get( 0 ).getPassives() + digestList.get( 0 ).getPromoters() : 0l;
        long totalCompletedReviews1 = digestList.get( 1 ) != null ? digestList.get( 1 ).getDetractors()
            + digestList.get( 1 ).getPassives() + digestList.get( 1 ).getPromoters() : 0l;

        long totalCompletedNpsReviews0 = digestList.get( 0 ) != null ? digestList.get( 0 ).getNpsDetractors()
            + digestList.get( 0 ).getNpsPassives() + digestList.get( 0 ).getNpsPromoters() : 0l;
        long totalCompletedNpsReviews1 = digestList.get( 1 ) != null ? digestList.get( 1 ).getNpsDetractors()
            + digestList.get( 1 ).getNpsPassives() + digestList.get( 1 ).getNpsPromoters() : 0l;

        // choosing icons for average rating score
        digestAggregate.getDigestList().get( 0 )
            .setAverageScoreRatingIcon( ( avgScoreRating0 > avgScoreRating1 ? increasedIndicatorIcon
                : ( avgScoreRating0 == avgScoreRating1 ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );

        digestAggregate.getDigestList().get( 1 )
            .setAverageScoreRatingIcon( ( avgScoreRating1 > avgScoreRating2 ? increasedIndicatorIcon
                : ( avgScoreRating1 == avgScoreRating2 ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );

        digestAggregate.getDigestList().get( 2 )
            .setAverageScoreRatingIcon( ( avgScoreRating2 > avgScoreRating3 ? increasedIndicatorIcon
                : ( avgScoreRating2 == avgScoreRating3 ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );

        // choosing icons for survey completion rate
        digestAggregate.getDigestList().get( 0 )
            .setSurveyCompletionRateIcon( ( surveyCompletionRate0 > surveyCompletionRate1 ? increasedIndicatorIcon
                : ( surveyCompletionRate0 == surveyCompletionRate1 ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );

        digestAggregate.getDigestList().get( 1 )
            .setSurveyCompletionRateIcon( ( surveyCompletionRate1 > surveyCompletionRate2 ? increasedIndicatorIcon
                : ( surveyCompletionRate1 == surveyCompletionRate2 ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );

        digestAggregate.getDigestList().get( 2 )
            .setSurveyCompletionRateIcon( ( surveyCompletionRate2 > surveyCompletionRate3 ? increasedIndicatorIcon
                : ( surveyCompletionRate2 == surveyCompletionRate3 ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );


        // choosing icons for SPS
        ////c1
        if ( sps0 == null || sps1 == null ) {
            digestAggregate.getDigestList().get( 0 ).setSpsIcon( notAvailableIndicatorIcon );
        } else {
            digestAggregate.getDigestList().get( 0 )
                .setSpsIcon( ( sps0.doubleValue() > sps1.doubleValue() ? increasedIndicatorIcon
                    : ( sps0.doubleValue() == sps1.doubleValue() ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );
        }


        ////c2
        if ( sps1 == null || sps2 == null ) {
            digestAggregate.getDigestList().get( 1 ).setSpsIcon( notAvailableIndicatorIcon );
        } else {
            digestAggregate.getDigestList().get( 1 )
                .setSpsIcon( ( sps1.doubleValue() > sps2.doubleValue() ? increasedIndicatorIcon
                    : ( sps1.doubleValue() == sps2.doubleValue() ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );
        }


        ////c3
        if ( sps2 == null || sps3 == null ) {
            digestAggregate.getDigestList().get( 2 ).setSpsIcon( notAvailableIndicatorIcon );
        } else {
            digestAggregate.getDigestList().get( 2 )
                .setSpsIcon( ( sps2.doubleValue() > sps3.doubleValue() ? increasedIndicatorIcon
                    : ( sps2.doubleValue() == sps3.doubleValue() ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );
        }


        // choosing icons for NPS
        ////c1'
        if ( nps0 == null || nps1 == null ) {
            digestAggregate.getDigestList().get( 0 ).setNpsIcon( notAvailableIndicatorIcon );
        } else {
            digestAggregate.getDigestList().get( 0 )
                .setNpsIcon( ( nps0.doubleValue() > nps1.doubleValue() ? increasedIndicatorIcon
                    : ( nps0.doubleValue() == nps1.doubleValue() ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );
        }


        ////c2'
        if ( nps1 == null || nps2 == null ) {
            digestAggregate.getDigestList().get( 1 ).setNpsIcon( notAvailableIndicatorIcon );
        } else {
            digestAggregate.getDigestList().get( 1 )
                .setNpsIcon( ( nps1.doubleValue() > nps2.doubleValue() ? increasedIndicatorIcon
                    : ( nps1.doubleValue() == nps2.doubleValue() ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );
        }


        ////c3'
        if ( nps2 == null || nps3 == null ) {
            digestAggregate.getDigestList().get( 2 ).setNpsIcon( notAvailableIndicatorIcon );
        } else {
            digestAggregate.getDigestList().get( 2 )
                .setNpsIcon( ( nps2.doubleValue() > nps3.doubleValue() ? increasedIndicatorIcon
                    : ( nps2.doubleValue() == nps3.doubleValue() ? noChangeIndicatorIcon : droppedIndicatorIcon ) ) );
        }


        // building conclusion text for average rating score
        digestAggregate.setAvgRatingTxt( "Your average score rating "
            + ( avgScoreRating0 > avgScoreRating1
                ? "has increased by "
                    + digestMailHelper.getBoldTextForHtml( utils.formatNumber( 2, ( avgScoreRating0 - avgScoreRating1 ) ) )
                : ( avgScoreRating0 == avgScoreRating1 ? "did not change"
                    : "has dropped by " + digestMailHelper
                        .getBoldTextForHtml( utils.formatNumber( 2, ( avgScoreRating1 - avgScoreRating0 ) ) ) ) )
            + " and your user count "
            + ( userCount0 > userCount1
                ? "has increased by " + digestMailHelper.getBoldTextForHtml( String.valueOf( ( userCount0 - userCount1 ) ) )
                : ( userCount0 == userCount1 ? "did not change"
                    : "has dropped by "
                        + digestMailHelper.getBoldTextForHtml( String.valueOf( ( userCount1 - userCount0 ) ) ) ) )
            + " from last month." );

        // building conclusion text for survey completion rate
        digestAggregate
            .setSurveyPercentageTxt( "Your survey completion rate "
                + ( surveyCompletionRate0 > surveyCompletionRate1
                    ? "has increased by " + digestMailHelper
                        .getBoldTextForHtml( utils.formatNumber( 2, ( surveyCompletionRate0 - surveyCompletionRate1 ) ) + "%" )
                    : ( surveyCompletionRate0 == surveyCompletionRate1 ? "did not change"
                        : "has dropped by " + digestMailHelper.getBoldTextForHtml(
                            utils.formatNumber( 2, ( surveyCompletionRate1 - surveyCompletionRate0 ) ) + "%" ) ) )
                + " and your transaction count "
                + ( transcationCount0 > transcationCount1
                    ? "has increased by "
                        + digestMailHelper.getBoldTextForHtml( String.valueOf( ( transcationCount0 - transcationCount1 ) ) )
                    : ( transcationCount0 == transcationCount1 ? "did not change"
                        : "has dropped by " + digestMailHelper
                            .getBoldTextForHtml( String.valueOf( ( transcationCount1 - transcationCount0 ) ) ) ) )
                + " from last month." );

        // building conclusion text for SPS
        StringBuilder satisfactionRatingText = new StringBuilder( "" );

        if ( sps0 == null || sps1 == null ) {
            satisfactionRatingText.append( "Your " );
        } else {
            satisfactionRatingText.append( "Your satisfaction rating " );
            satisfactionRatingText.append( ( sps0.doubleValue() > sps1.doubleValue()
                ? "has increased by " + digestMailHelper
                    .getBoldTextForHtml( utils.formatNumber( 2, ( sps0.doubleValue() - sps1.doubleValue() ) ) )
                : ( sps0.doubleValue() == sps1.doubleValue() ? "did not change"
                    : "has dropped by " + digestMailHelper
                        .getBoldTextForHtml( utils.formatNumber( 2, ( sps1.doubleValue() - sps0.doubleValue() ) ) ) ) ) );
            satisfactionRatingText.append( " and your " );
        }

        satisfactionRatingText.append( "total review count " );

        satisfactionRatingText.append( ( totalCompletedReviews0 > totalCompletedReviews1
            ? "has increased by "
                + digestMailHelper.getBoldTextForHtml( String.valueOf( ( totalCompletedReviews0 - totalCompletedReviews1 ) ) )
            : ( totalCompletedReviews0 == totalCompletedReviews1 ? "did not change"
                : "has dropped by " + digestMailHelper
                    .getBoldTextForHtml( String.valueOf( ( totalCompletedReviews1 - totalCompletedReviews0 ) ) ) ) ) );

        satisfactionRatingText.append( " from last month." );

        digestAggregate.setStatisfactionRatingTxt( satisfactionRatingText.toString() );


        // building conclusion text for NPS
        StringBuilder npsInferenceTxt = new StringBuilder( "" );

        if ( nps0 == null || nps1 == null ) {
            npsInferenceTxt.append( "Your " );
        } else {
            npsInferenceTxt.append( "Your NPS rating " );
            npsInferenceTxt.append( ( nps0.doubleValue() > nps1.doubleValue()
                ? "has increased by " + digestMailHelper
                    .getBoldTextForHtml( utils.formatNumber( 2, ( nps0.doubleValue() - nps1.doubleValue() ) ) )
                : ( nps0.doubleValue() == nps1.doubleValue() ? "did not change"
                    : "has dropped by " + digestMailHelper
                        .getBoldTextForHtml( utils.formatNumber( 2, ( nps1.doubleValue() - nps0.doubleValue() ) ) ) ) ) );
            npsInferenceTxt.append( " and your " );
        }

        npsInferenceTxt.append( "total review count for reviews with NPS " );

        npsInferenceTxt.append( ( totalCompletedNpsReviews0 > totalCompletedNpsReviews1
            ? "has increased by " + digestMailHelper
                .getBoldTextForHtml( String.valueOf( ( totalCompletedNpsReviews0 - totalCompletedNpsReviews1 ) ) )
            : ( totalCompletedNpsReviews0 == totalCompletedNpsReviews1 ? "did not change"
                : "has dropped by " + digestMailHelper
                    .getBoldTextForHtml( String.valueOf( ( totalCompletedNpsReviews1 - totalCompletedNpsReviews0 ) ) ) ) ) );

        npsInferenceTxt.append( " from last month." );

        digestAggregate.setNpsInferenceTxt( npsInferenceTxt.toString() );
    }


    private void buildUserRankingRows( MonthlyDigestAggregate digestAggregate, List<UserRanking> userRankingList )
    {
        if ( userRankingList != null && userRankingList.size() > 0 ) {

            String head = digestMailHelper
                .wrapUserRankingTableHeadOrBody( digestMailHelper.wrapUserRankingTableRow(
                    digestMailHelper.wrapUserRankingTableCell( CommonConstants.DIGEST_USER_RANKING_COLUMN, true )
                        + digestMailHelper.wrapUserRankingTableCell( CommonConstants.DIGEST_USER_NAME_COLUMN, true )
                        + digestMailHelper.wrapUserRankingTableCell( CommonConstants.DIGEST_AVG_SCORE_COLUMN, true )
                        + digestMailHelper.wrapUserRankingTableCell( CommonConstants.DIGEST_REVIEWS_COLUMN, true ),
                    true, false ), true );

            String body = "";

            for ( int i = 0; i < userRankingList.size(); i++ ) {

                UserRanking userRanking = userRankingList.get( i );

                // add a row of HTML with user ranking information
                body += digestMailHelper.wrapUserRankingTableRow(
                    digestMailHelper
                        .wrapUserRankingTableCell( ( userRanking.getRank() == 0 ? CommonConstants.NOT_AVAILABLE
                            : String.valueOf( userRanking.getRank() ) ), false )
                        + digestMailHelper.wrapUserRankingTableCell(
                            buildDisplayName( userRanking.getFirstName(), userRanking.getLastName() ), false )
                        + digestMailHelper
                            .wrapUserRankingTableCell( userRanking.getAverageRating() == 0f ? CommonConstants.NOT_AVAILABLE
                                : String.valueOf( userRanking.getAverageRating() ), false )
                        + digestMailHelper
                            .wrapUserRankingTableCell( userRanking.getTotalReviews() == 0 ? CommonConstants.NOT_AVAILABLE
                                : String.valueOf( userRanking.getTotalReviews() ), false ),
                    false, ( ( i + 1 ) % 2 ) == 0 ? true : false );

            }

            body = digestMailHelper.wrapUserRankingTableHeadOrBody( body, false );


            digestAggregate.setUserRankingHtmlSection( digestMailHelper.getDigestMailSectionPaddingWithSeperator()
                + digestMailHelper.getDigestMailIntraSectionPadding()
                + digestMailHelper.wrapDigestMailSectionTitle( CommonConstants.DIGEST_USER_RANKING_TITLE )
                + digestMailHelper.getDigestMailInterLinePadding()
                + digestMailHelper.wrapDigestMailSectionDescription( CommonConstants.DIGEST_USER_RANKING_DESC )
                + digestMailHelper.getDigestMailIntraSectionPadding() + digestMailHelper.wrapUserRankingTable( head + body ) );


        } else {
            digestAggregate.setUserRankingHtmlSection( digestMailHelper.getDigestMailSectionPaddingWithSeperator()
                + digestMailHelper.getDigestMailIntraSectionPadding() );
        }
    }


    private String buildDisplayName( String firstName, String lastName )
    {
        if ( StringUtils.isNotEmpty( firstName ) ) {
            return StringUtils.isNotEmpty( lastName ) ? firstName + " " + lastName : firstName;
        } else {
            return StringUtils.isNotEmpty( lastName ) ? lastName : CommonConstants.NOT_AVAILABLE;
        }
    }


    @Override
    public void startMonthlyDigestProcess()
    {
        try {
            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_MONTHLY_DIGEST_STARTER, CommonConstants.BATCH_NAME_MONTHLY_DIGEST_STARTER );

            LOG.debug( "Starting startMonthlyDigestProcess" );

            // create a Calendar instance with time zone and locale of the device
            Calendar calendar = Calendar.getInstance();

            // set month and year
            int month = calendar.get( Calendar.MONTH );
            int year = calendar.get( Calendar.YEAR );

            if ( month == 0 ) {
                month = 12;
                year = year - 1;
            }

            generateMonthlyDigestForHierarchy( CommonConstants.PROFILE_LEVEL_COMPANY, month, year );
            generateMonthlyDigestForHierarchy( CommonConstants.PROFILE_LEVEL_REGION, month, year );
            generateMonthlyDigestForHierarchy( CommonConstants.PROFILE_LEVEL_BRANCH, month, year );

            // updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_MONTHLY_DIGEST_STARTER );
            LOG.debug( "Completed startMonthlyDigestProcess" );
        } catch ( Exception unhandledError ) {
            LOG.error( "Error in startMonthlyDigestProcess", unhandledError );
            try {
                // update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_MONTHLY_DIGEST_STARTER,
                    unhandledError.getMessage() );
                // send report bug mail to administrator
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_MONTHLY_DIGEST_STARTER,
                    System.currentTimeMillis(), unhandledError );
            } catch ( NoRecordsFetchedException | InvalidInputException errorWhileHandlingError ) {
                LOG.error( "Error while updating error message in startMonthlyDigestProcess " );
            } catch ( UndeliveredEmailException unableToDeliverMail ) {
                LOG.error( "Error while sending report excption mail to admin " );
            }
        }
    }


    private void generateMonthlyDigestForHierarchy( String profileLevel, int month, int year )
        throws InvalidInputException, UndeliveredEmailException
    {

        LOG.debug( "method generateMonthlyDigestForHierarchy started" );

        if ( StringUtils.isEmpty( profileLevel ) ) {
            LOG.warn( "Invalid profile level: {}", profileLevel );
            throw new InvalidInputException( "Invalid profile level" );
        } else if ( month < 1 || month > 12 ) {
            LOG.warn( "Invalid month: {}", month );
            throw new InvalidInputException( "Invalid month value" );
        } else if ( year < 0 ) {
            LOG.warn( "Invalid year: {}", year );
            throw new InvalidInputException( "Invalid year value" );
        }

        int startIndex = DIGEST_MAIL_START_INDEX;
        int batchSize = DIGEST_MAIL_BATCH_SIZE;

        List<DigestRequestData> digestRequestList = null;

        do {

            // fetch a list of digest requests for companies who have enabled send monthly
            // digest mail feature
            digestRequestList = getDigestRequestDataInBatch( profileLevel, startIndex, batchSize );

            if ( digestRequestList != null ) {
                for ( DigestRequestData digestRequest : digestRequestList ) {

                    try {

                        // get the digest aggregate object
                        MonthlyDigestAggregate digestAggregate = getMonthlyDigestAggregateForAHierarchy( digestRequest, month,
                            year );

                        // save the copy of digest generated for further use
                        if( !digestAggregate.isDigestDataAbsent() || !digestAggregate.isUserCountDataAbsent() ) {
                            constructAndSaveDigestCopy( digestAggregate );
                        }
                        
                        // check is digest data exists
                        if ( digestAggregate.isDigestDataAbsent() ) {
                            LOG.info( "Digest data for {} : {} is not present, Aborting", digestAggregate.getProfileLevel(),
                                digestAggregate.getEntityName() );
                            continue;
                        }

                        processRecipients( digestAggregate, digestRequest );

                        if ( digestAggregate.getRecipientMailIds() == null
                            || digestAggregate.getRecipientMailIds().isEmpty() ) {
                            LOG.error( "Digest recipients for {} : {} is not specified, aborting", profileLevel,
                                digestRequest.getEntityId() );
                            continue;
                        }

                        // send the digest email
                        emailServices.sendMonthlyDigestMail( digestAggregate );

                    } catch ( Exception unableToSendDigestMail ) {
                        LOG.warn( "Unable to send digest mail for {}", digestRequest.getEntityName() );
                        emailServices.sendDigestErrorMailForCompany( digestRequest, unableToSendDigestMail.getMessage() );
                    }

                }
            }

            startIndex += batchSize;

        } while ( digestRequestList != null && digestRequestList.size() >= batchSize );
    }


    private void processRecipients( MonthlyDigestAggregate digestAggregate, DigestRequestData digestRequest )
    {
        LOG.trace( "processRecipients running" );
        // manage recipients
        if ( digestAggregate != null ) {

            // check for send digest to administrator switch
            if ( CommonConstants.YES_STRING.equals( sendDigestToApplicationAdminOnly ) ) {
                digestAggregate.setRecipientMailIds( new HashSet<String>() );
                digestAggregate.getRecipientMailIds().add( applicationAdminEmail );
            } else if ( digestRequest != null && digestRequest.getRecipientMailIds() != null
                && !digestRequest.getRecipientMailIds().isEmpty() ) {
                digestAggregate.setRecipientMailIds( digestRequest.getRecipientMailIds() );
            }
        }
    }


    @Override
    public boolean updateSendDigestMailToggle( String entityType, long entityId, boolean sendMonthlyDigestMail )
        throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method updateSendDigestMailToggle() started." );

        if ( StringUtils.isEmpty( entityType ) ) {
            LOG.warn( "Entity type is not specified" );
            throw new InvalidInputException( "Entity type is not specified" );
        } else if ( entityId <= 0 ) {
            LOG.warn( "Entity Id is invalid" );
            throw new InvalidInputException( "Entity Id is invalid" );
        }

        OrganizationUnitSettings unitSettings = null;
        String collectionType = "";

        if ( CommonConstants.COMPANY_ID_COLUMN.equals( entityType ) ) {
            unitSettings = organizationManagementService.getCompanySettings( entityId );
            collectionType = CommonConstants.COMPANY_SETTINGS_COLLECTION;
        } else if ( CommonConstants.REGION_ID_COLUMN.equals( entityType ) ) {
            unitSettings = organizationManagementService.getRegionSettings( entityId );
            collectionType = CommonConstants.REGION_SETTINGS_COLLECTION;
        } else if ( CommonConstants.BRANCH_ID_COLUMN.equals( entityType ) ) {
            unitSettings = organizationManagementService.getBranchSettingsDefault( entityId );
            collectionType = CommonConstants.BRANCH_SETTINGS_COLLECTION;
        } else if ( CommonConstants.AGENT_ID_COLUMN.equals( entityType ) ) {
            unitSettings = organizationManagementService.getAgentSettings( entityId );
            collectionType = CommonConstants.AGENT_SETTINGS_COLLECTION;
        } else {
            LOG.warn( "Entity Type is invalid" );
            throw new InvalidInputException( "Entity type is invalid" );
        }

        if ( unitSettings == null ) {
            LOG.warn( "settings are not specified" );
            throw new InvalidInputException( "settings cannot be null." );
        }


        // update monthly digest mail toggle
        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_SEND_MONTHLY_DIGEST_MAIL, sendMonthlyDigestMail, unitSettings,
            collectionType );

        LOG.debug( "method updateSendDigestMailToggle() finished." );
        return true;
    }


    @Override
    public List<DigestRequestData> getEntitiesOptedForDigestMail( int startIndex, int batchSize, String profileLevel )
        throws InvalidInputException
    {

        LOG.debug( "method getCompaniesOptedForDigestMail started" );
        List<DigestRequestData> digestRequestData = null;
        List<OrganizationUnitSettings> entityList = organizationUnitSettingsDao.getMonthlyDigestEnabledEntities(
            organizationManagementService.getCollectionFromProfileLevel( profileLevel ), startIndex, batchSize );

        if ( entityList != null ) {

            digestRequestData = new ArrayList<>();

            for ( OrganizationUnitSettings unitSettings : entityList ) {

                Set<String> digestRecipients = new HashSet<>();

                if ( unitSettings.isSendMonthlyDigestMail() ) {
                    digestRecipients.addAll( organizationManagementService.getAdminEmailsSpecificForAHierarchy( profileLevel,
                        unitSettings.getIden() ) );
                }

                if ( unitSettings.getDigestRecipients() != null ) {
                    digestRecipients.addAll( unitSettings.getDigestRecipients() );
                }

                DigestRequestData digestRequest = new DigestRequestData();

                digestRequest.setProfileLevel( profileLevel );
                digestRequest.setEntityId( unitSettings.getIden() );
                digestRequest.setEntityName(
                    unitSettings.getContact_details() != null ? unitSettings.getContact_details().getName() : null );

                digestRequest.setRecipientMailIds( digestRecipients );
                digestRequestData.add( digestRequest );

            }
        }
        return digestRequestData;
    }


    @SuppressWarnings ( "unchecked")
    private List<DigestRequestData> getDigestRequestDataInBatch( String profileLevel, int startIndex, int batchSize )
    {
        LOG.debug( "method getDigestRequestDataInBatch() running" );
        Response digestListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getEntitiesOptedForDigestMail( profileLevel, startIndex, batchSize );

        String digestListString = StringEscapeUtils.unescapeJava(
            digestListResponse != null ? new String( ( (TypedByteArray) digestListResponse.getBody() ).getBytes() ) : null );

        return (List<DigestRequestData>) ( new Gson().fromJson( StringUtils.strip( digestListString, "\"" ),
            new TypeToken<List<DigestRequestData>>() {}.getType() ) );
    }


    private MonthlyDigestAggregate getMonthlyDigestAggregateForAHierarchy( DigestRequestData request, int month, int year )
    {
        LOG.debug( "method getMonthlyDigestAggregateForCompany() running" );

        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().buildMonthlyDigestAggregate(
            request.getProfileLevel(), request.getEntityId(), request.getEntityName(), month, year );

        String responseString = StringEscapeUtils
            .unescapeJava( response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null );

        return ( new Gson().fromJson( StringUtils.strip( responseString, "\"" ),
            new TypeToken<MonthlyDigestAggregate>() {}.getType() ) );

    }


    private List<String> buildMonthStringsForDigest( int monthUnderConcern )
    {
        List<String> monthStringsForDigest = new ArrayList<>( 3 );
        if ( monthUnderConcern == 1 ) {
            monthStringsForDigest.add( 0, new DateFormatSymbols().getMonths()[0] );
            monthStringsForDigest.add( 1, new DateFormatSymbols().getMonths()[11] );
            monthStringsForDigest.add( 2, new DateFormatSymbols().getMonths()[10] );
        } else if ( monthUnderConcern == 2 ) {
            monthStringsForDigest.add( 0, new DateFormatSymbols().getMonths()[1] );
            monthStringsForDigest.add( 1, new DateFormatSymbols().getMonths()[0] );
            monthStringsForDigest.add( 2, new DateFormatSymbols().getMonths()[11] );
        } else {
            monthStringsForDigest.add( 0, new DateFormatSymbols().getMonths()[monthUnderConcern - 1] );
            monthStringsForDigest.add( 1, new DateFormatSymbols().getMonths()[monthUnderConcern - 2] );
            monthStringsForDigest.add( 2, new DateFormatSymbols().getMonths()[monthUnderConcern - 3] );
        }
        return monthStringsForDigest;
    }


    @Override
    public void getCompaniesWithNotransactions()
    {
        LOG.info( "Started transactionMonitorForCompaniesWithNotransactions" );
        int noOfDays = NUMBER_OF_DAYS;

        // Method to call the api
        List<CompanyView> companiesWithNoTransactions = getCompaniesWithNoTransactionInPastNDaysInBatch( noOfDays );

        LOG.info( "sendNoTransactionAlertMailForCompanies" );
        String mailBody = "";

        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -( NUMBER_OF_DAYS ) ); // subtract the no of days
        Date nDaysBackDate = new Date( calendar.getTimeInMillis() );

        int i = 0;
        for ( CompanyView company : companiesWithNoTransactions ) {
            i++;
            mailBody += ( i + ". " + " " + company.getCompany() + " with id " + company.getCompanyId()
                + " SocialSurvey has not received any transaction details since " + nDaysBackDate );
            mailBody += "<br>";
        }

        try {
            // send mail if there is at least one company with no transactions
            if ( i > 0 )
                emailServices.sendNoTransactionAlertMail( getTransactionMonitorMailList(), mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Error while sending highNotProcessed aler email ", e );
        }
        LOG.info( "sendNoTransactionAlertMailForCompanies ended" );

    }


    @SuppressWarnings ( "unchecked")
    private List<CompanyView> getCompaniesWithNoTransactionInPastNDaysInBatch( int noOfDays )
    {
        LOG.debug( "getCompaniesWithNoTransactionInPastNDaysInBatch() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getCompaniesWithNoTransactionInPastNDays( noOfDays );

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (List<CompanyView>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<List<CompanyView>>() {}.getType() ) );
    }


    @Override
    public void getCompaniesWithHighNotProcessedTransactions()
    {
        LOG.debug( "Started transactionMonitorForCompaniesWithHighNotProcessedTransactions" );

        // Method to call the api
        List<Long> companyIdsForLessSurveyAlerts = validatesurveystatsforcompaniesInBatch();
        List<CompanyView> allActiveCompanies = getAllActiveEnterpriseCompaniesInBatch();

        LOG.info( "sendHighNotProcessedTransactionAlertMailForCompanies" );

        String mailBody = "";

        int i = 0;
        for ( CompanyView company : allActiveCompanies ) {
            // check if we need to send alert mail for this company
            if ( companyIdsForLessSurveyAlerts.contains( company.getCompanyId() ) ) {
                i++;
                mailBody += ( i + ". " + " " + company.getCompany() + " with id " + company.getCompanyId()
                    + " have more than 50% unprocessed transactions for previous day." );
                mailBody += "<br>";
            }

        }

        try {
            // send mail if there is atleast one company with high not processed
            // transactions
            if ( i > 0 )
                emailServices.sendHighVoulmeUnprocessedTransactionAlertMail( getTransactionMonitorMailList(), mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Error while sending highNotProcessed alert email ", e );
        }
        LOG.info( "method sendHighNotProcessedTransactionAlertMailForCompanies ended" );

    }


    @SuppressWarnings ( "unchecked")
    private List<Long> validatesurveystatsforcompaniesInBatch()
    {
        LOG.debug( "validatesurveystatsforcompaniesInBatch() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi().validateSurveyStatsForCompanies();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (List<Long>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<List<Long>>() {}.getType() ) );
    }


    @SuppressWarnings ( "unchecked")
    private List<CompanyView> getAllActiveEnterpriseCompaniesInBatch()
    {
        LOG.debug( "getAllActiveEnterpriseCompaniesInBatch() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi().getAllActiveEnterpriseCompanies();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (List<CompanyView>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<List<CompanyView>>() {}.getType() ) );
    }


    @Override
    public void getCompaniesWithLowVolumeOfTransactions()
    {
        LOG.debug( "Started transactionMonitorForCompaniesWithLowVolumeOfTransactions" );

        // Method to call the api
        Map<Long, Long> companySurveyStatsCountsMap = getSurveyStatusStatsForPastOneMonthInBatch();
        List<CompanyActiveUsersStats> companyActiveUserCounts = getCompanyActiveUserCountForPastDayInBatch();

        LOG.info( "validateAndSentLessSurveysAlert" );
        String mailBody = "";

        int i = 0;
        for ( CompanyActiveUsersStats companyActiveUsersStats : companyActiveUserCounts ) {
            Long surveyCount = companySurveyStatsCountsMap.get( companyActiveUsersStats.getCompanyId() );
            Integer userCount = companyActiveUsersStats.getNoOfActiveUsers();
            if ( surveyCount != null && userCount != null && ( surveyCount / 2 ) < userCount ) {
                i++;
                mailBody += ( i + ". " + "Company with id " + companyActiveUsersStats.getCompanyId() + "  sent us  "
                    + surveyCount + " transactions for total of " + userCount + " Users in past one month." );
                mailBody += "<br>";
            }
        }

        try {
            // send mail only if there is at least one company with less survey transactions
            if ( i > 0 )
                emailServices.sendLessVoulmeOfTransactionReceivedAlertMail( getTransactionMonitorMailList(), mailBody );
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Error while sending less survey alert email.", e );
        }
        LOG.info( "validateAndSentLessSurveysAlert ended" );

    }


    @SuppressWarnings ( "unchecked")
    private Map<Long, Long> getSurveyStatusStatsForPastOneMonthInBatch()
    {
        LOG.debug( "getSurveyStatusStatsForPastOneMonthInBatch() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getSurveyStatusStatsForPastOneMonth();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (Map<Long, Long>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<Map<Long, Long>>() {}.getType() ) );
    }


    @SuppressWarnings ( "unchecked")
    private List<CompanyActiveUsersStats> getCompanyActiveUserCountForPastDayInBatch()
    {
        LOG.debug( "getCompanyActiveUserCountForPastDayInBatch() started" );
        Response usersListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi().getCompanyActiveUserCountForPastDay();

        String usersListString = StringEscapeUtils.unescapeJava(
            usersListResponse != null ? new String( ( (TypedByteArray) usersListResponse.getBody() ).getBytes() ) : null );

        return (List<CompanyActiveUsersStats>) ( new Gson().fromJson( StringUtils.strip( usersListString, "\"" ),
            new TypeToken<List<CompanyActiveUsersStats>>() {}.getType() ) );
    }


    @Override
    public List<String> getTransactionMonitorMailList()
    {
        String[] transactionMailRecipient = transactionMonitorSupportEmail.split( "," );
        List<String> transactionMailList = new ArrayList<>();
        for ( String recipient : transactionMailRecipient ) {
            transactionMailList.add( recipient );
        }
        return transactionMailList;

    }


    @Transactional ( value = "transactionManagerForReporting")
    public List<CompanyDetailsReport> getCompanyDetailsReport( Long entityId, int startIndex, int batchSize )
        throws InvalidInputException
    {
        User user = userDao.findById( User.class, entityId );
        List<CompanyDetailsReport> companyDetailsReportData = null;
        if ( user != null && ( user.isSuperAdmin() || userManagementService.isUserSocialSurveyAdmin( entityId ) ) ) {
            companyDetailsReportData = companyDetailsReportDao.getCompanyDetailsReportData( startIndex, batchSize );
        }
        return companyDetailsReportData;
    }


    public String generateCompanyDetailsReport( long entityId, String entityType )
        throws UnsupportedEncodingException, NonFatalException
    {
        LOG.info( "Generating account statistics report for enitityId {}, entityType {}", entityId, entityType );
        String fileName = "Account_Statistics_Report" + "-" + ( Calendar.getInstance().getTimeInMillis() )
            + CommonConstants.EXCEL_FILE_EXTENSION;
        LOG.debug( "fileName {} ", fileName );
        XSSFWorkbook workbook = this.downloadCompanyDetailsReport( entityId, entityType );
        LOG.debug( "Writing {} number of records into file {}", workbook.getSheetAt( 0 ).getLastRowNum(), fileName );
        return createExcelFileAndSaveInAmazonS3( fileName, workbook );
    }


    private XSSFWorkbook downloadCompanyDetailsReport( long entityId, String entityType )
    {
        int startIndex = 0;
        int batchSize = CommonConstants.BATCH_SIZE;
        int enterNext = 1;

        // write the excel header first
        Map<Integer, List<Object>> data = workbookData.writeCompanyDetailsReportHeader();
        // create workbook data
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data );

        List<CompanyDetailsReport> companyDetailsReportList = null;
        do {
            companyDetailsReportList = getCompanyDetailsResponse( entityType, entityId, startIndex, batchSize );
            if ( companyDetailsReportList != null && !companyDetailsReportList.isEmpty() ) {
                enterNext = startIndex + 1;
                data = workbookData.getCompanyDetailsReportToBeWrittenInSheet( companyDetailsReportList, enterNext );
                LOG.debug( "Got {} records starting at {} index", data.size(), enterNext );
                // use the created workbook when writing the header answer rewrite the same.
                workbook = workbookOperations.writeToWorkbook( data, workbook, enterNext );
                // calculate startIndex.
                startIndex = startIndex + batchSize;
            }
        } while ( companyDetailsReportList != null && !companyDetailsReportList.isEmpty()
            && companyDetailsReportList.size() >= batchSize );

        XSSFSheet sheet = workbook.getSheetAt( 0 );
        makeRowBold( workbook, sheet.getRow( 0 ) );
        return workbook;
    }


    private List<CompanyDetailsReport> getCompanyDetailsResponse( String entityType, long entityId, int startIndex,
        int batchSize )
    {
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getCompanyDetailsReport( entityType, entityId,
            startIndex, batchSize );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        List<CompanyDetailsReport> companyDetailsReportList = null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
            Type listType = new TypeToken<List<CompanyDetailsReport>>() {}.getType();
            companyDetailsReportList = new Gson().fromJson( responseString, listType );

        }
        return companyDetailsReportList;
    }


    /**
     * Method to fetch reviews based on the profile level specified, iden is one of
     * agentId/branchId/regionId or companyId based on the profile level
     */
    @Override
    @Transactional
    public List<ReportingSurveyPreInititation> getIncompleteSurvey( long entityId, String entityType, Date startDate,
        Date endDate, int startIndex, int batchSize ) throws InvalidInputException
    {
        LOG.info(
            "Method getIncompleteSurvey() called for entityId: {}  ,entityType: {} ,startDate: {} ,endDate: {} ,startIndex: {}  ,batchSize: {} ",
            entityId, entityType, startDate, endDate, startIndex, batchSize );
        // check if its a valid entity id
        if ( entityId <= 0l ) {
            throw new InvalidInputException( "entityId is invalid while fetching incomplete reviews" );
        }
        Timestamp startTime = null;
        Timestamp endTime = null;
        if ( startDate != null )
            startTime = new Timestamp( startDate.getTime() );
        if ( endDate != null )
            endTime = new Timestamp( endDate.getTime() );
        return reportingSurveyPreInititationDao.getIncompleteSurveyForReporting( entityType, entityId, startTime, endTime,
            startIndex, batchSize );
    }


    @Override
    @Transactional
    public List<NpsReportWeek> getNpsReportForAWeek( long companyId, int week, int year ) throws InvalidInputException
    {
        LOG.debug( "Started fetching NPS report for a week" );
        List<NpsReportWeek> npsReportForWeekList = new ArrayList<>();

        if ( companyId <= 0 ) {
            LOG.warn( "company Identifier can not be null" );
            throw new InvalidInputException( "company ID is null." );
        } else if ( week < 1 || week > 53 ) {
            LOG.warn( "week not in range" );
            throw new InvalidInputException( "week not in range" );
        }
        if ( year <= 0 ) {
            LOG.warn( "year not in range" );
            throw new InvalidInputException( "year not in range" );
        }

        npsReportForWeekList = npsReportWeekDao.fetchNpsReportWeek( companyId, week, year );
        LOG.debug( "Finished fetching NPS report for a week" );
        return npsReportForWeekList;
    }


    @Override
    @Transactional
    public List<NpsReportMonth> getNpsReportForAMonth( long companyId, int month, int year ) throws InvalidInputException
    {
        LOG.debug( "Started fetching NPS report for a month" );
        List<NpsReportMonth> npsReportForMonthList = new ArrayList<>();

        if ( companyId <= 0 ) {
            LOG.warn( "company Identifier can not be null" );
            throw new InvalidInputException( "company ID is null." );
        } else if ( month <= 0 || month > 12 ) {
            LOG.warn( "month not in range" );
            throw new InvalidInputException( "month not in range" );
        }
        if ( year <= 0 ) {
            LOG.warn( "year not in range" );
            throw new InvalidInputException( "year not in range" );
        }

        npsReportForMonthList = npsReportMonthDao.fetchNpsReportMonth( companyId, month, year );
        LOG.debug( "Finished fetching NPS report for a week" );
        return npsReportForMonthList;
    }


    @Override
    @Transactional
    public String generateNpsReportForWeekOrMonth( long profileValue, String profileLevel, Timestamp startDate, int type )
        throws ParseException, UnsupportedEncodingException, NonFatalException
    {
        if ( !profileLevel.equalsIgnoreCase( CommonConstants.COMPANY_ID_COLUMN ) || profileValue <= 0 ) {
            LOG.warn( "ProfileValue or profileLevel is not valid" );
            throw new InvalidInputException( "ProfileValue or profileLevel is not valid" );
        }
        LOG.info( "Generating NPS report for profileValue {}, profileLevel {}", profileValue, profileLevel );
        String fileName = "NPS_Report" + "-" + ( Calendar.getInstance().getTimeInMillis() )
            + CommonConstants.EXCEL_FILE_EXTENSION;
        LOG.debug( "fileName {} ", fileName );
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( startDate );
        int week = calendar.get( Calendar.WEEK_OF_YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        int year = calendar.get( Calendar.YEAR );
        XSSFWorkbook workbook = this.downloadNPSReport( profileValue, profileLevel, year, month, week, type );
        if ( LOG.isDebugEnabled() && workbook != null && workbook.getSheetAt( 0 ) != null )
            LOG.debug( "Writing {} number of records into file {}", workbook.getSheetAt( 0 ).getLastRowNum(), fileName );
        return createExcelFileAndSaveInAmazonS3( fileName, workbook );
    }


    public XSSFWorkbook downloadNPSReport( long profileValue, String profileLevel, int year, int month, int week, int type )
    {
        Map<Integer, List<Object>> data = workbookData.writeNPSWeekReportHeader( type );
        XSSFWorkbook workBook = null;
        Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getNpsReportForWeekOrMonth( week, month,
            profileValue, year, type );
        String responseString = response != null ? new String( ( (TypedByteArray) response.getBody() ).getBytes() ) : null;
        if ( responseString != null ) {
            // since the string has ""abc"" an extra quote
            responseString = responseString.substring( 1, responseString.length() - 1 );
            // Escape characters
            responseString = StringEscapeUtils.unescapeJava( responseString );
            List<NpsReportWeek> npsReportWeekList = null;
            List<NpsReportMonth> npsReportMonthList = null;
            if ( type == CommonConstants.NPS_REPORT_TYPE_WEEK ) {
                Type listType = new TypeToken<List<NpsReportWeek>>() {}.getType();
                npsReportWeekList = new Gson().fromJson( responseString, listType );
                data = workbookData.getNpsReportWeekToBeWrittenInSheet( data, npsReportWeekList );
                workBook = workbookOperations.createWorkbook( data );
                workBook = formatForNPSReportWeek( workBook, npsReportWeekList );
            } else if ( type == CommonConstants.NPS_REPORT_TYPE_MONTH ) {
                Type listType = new TypeToken<List<NpsReportMonth>>() {}.getType();
                npsReportMonthList = new Gson().fromJson( responseString, listType );
                data = workbookData.getNpsReportMonthToBeWrittenInSheet( data, npsReportMonthList );
                workBook = workbookOperations.createWorkbook( data );
                workBook = formatForNPSReportMonth( workBook, npsReportMonthList );
            }

        }
        return workBook;
    }


    private XSSFWorkbook formatForNPSReportMonth( XSSFWorkbook workBook, List<NpsReportMonth> npsReportMonthList )
    {
        makeRowBoldAndBlue( workBook, workBook.getSheetAt( 0 ).getRow( 0 ) );
        if ( workBook.getSheetAt( 0 ).getRow( 1 ) == null || workBook.getSheetAt( 0 ).getRow( 1 ) == null ) {
            return workBook;
        }
        makeRowBoldAndBlue( workBook, workBook.getSheetAt( 0 ).getRow( 1 ) );
        int rownum = 1;

        for ( NpsReportMonth npsReportMonth : npsReportMonthList ) {
            if ( npsReportMonth.getBranchId() == 0 && npsReportMonth.getRegionId() == 0 ) {
                CellStyle style = workBook.createCellStyle();
                style.setAlignment( CellStyle.ALIGN_CENTER );
                style.setFillBackgroundColor( IndexedColors.AQUA.index );
            } else if ( npsReportMonth.getBranchId() == 0 && npsReportMonth.getRegionId() > 0 ) {
                makeRowBold( workBook, workBook.getSheetAt( 0 ).getRow( rownum ) );
            }
            rownum++;
        }
        return workBook;
    }


    private XSSFWorkbook formatForNPSReportWeek( XSSFWorkbook workBook, List<NpsReportWeek> npsReportWeekList )
    {
        makeRowBoldAndBlue( workBook, workBook.getSheetAt( 0 ).getRow( 0 ) );
        if ( workBook.getSheetAt( 0 ).getRow( 1 ) == null || workBook.getSheetAt( 0 ).getRow( 1 ) == null ) {
            return workBook;
        }
        makeRowBoldAndBlue( workBook, workBook.getSheetAt( 0 ).getRow( 1 ) );
        int rownum = 1;

        for ( NpsReportWeek npsReportWeek : npsReportWeekList ) {
            if ( npsReportWeek.getBranchId() == 0 && npsReportWeek.getRegionId() == 0 ) {
                CellStyle style = workBook.createCellStyle();
                style.setAlignment( CellStyle.ALIGN_CENTER );
                style.setFillBackgroundColor( IndexedColors.AQUA.index );
            } else if ( npsReportWeek.getBranchId() == 0 && npsReportWeek.getRegionId() > 0 ) {
                makeRowBold( workBook, workBook.getSheetAt( 0 ).getRow( rownum ) );
            }
            rownum++;
        }
        return workBook;
    }


    @Override
    public void updateTransactionMonitorAlertsForCompanies() throws InvalidInputException
    {
        LOG.debug( "Started updateTransactionMonitorAlertsForCompanies" );

        // Method to call the api
        Map<Long, List<CompanySurveyStatusStats>> surveStatsForPast7daysForAllCompanies = getSurveStatsForPast7daysForAllCompanies();
        Map<Long, List<CompanySurveyStatusStats>> surveStatsForLastToLatWeekForAllCompanies = getSurveStatsForLastToLatWeekForAllCompanies();

        Map<Long, Long> transacionCountForPast5Days = getTotalTransactionCountForPast5Days();
        Map<Long, Long> transacionCountForPast3Days = getTransactionCountForPast3Days();
        Map<Long, Long> sentSurveyCountForPast5Days = getSendSurveyCountForPast5Days();
        Map<Long, Long> completedSurveyCountForPastNDays = getCompletedSurveyCountForPastNDays();

        List<OrganizationUnitSettings> companySettingsList = organizationManagementService.getCompaniesForTransactionMonitor();

        for ( OrganizationUnitSettings companySettings : companySettingsList ) {
            long companyId = companySettings.getIden();


            List<CompanySurveyStatusStats> surveStatsForPast7days = surveStatsForPast7daysForAllCompanies.get( companyId );
            List<CompanySurveyStatusStats> surveStatsForLastToLatWeek = surveStatsForLastToLatWeekForAllCompanies
                .get( companyId );

            // check if lessTransactionInPastDays is true
            boolean isZeroIncomingTransactionInPastThreeDays = false;
            if ( transacionCountForPast5Days.get( companyId ) == null || transacionCountForPast5Days.get( companyId ) == 0 ) {
                isZeroIncomingTransactionInPastThreeDays = true;
            }

            // check if isLessInvitationSentInPastSevenDays is true
            boolean isLessInvitationSentInPastSevenDays = false;
            if ( getSentSurveyCountFromList(
                surveStatsForPast7days ) <= getSentSurveyCountFromList( surveStatsForLastToLatWeek ) / 2 ) {
                isLessInvitationSentInPastSevenDays = true;
            }

            //check if sent surey count in past 5 days is zero
            boolean isZeroInvitationSentInPastFiceDays = false;
            if ( sentSurveyCountForPast5Days.get( companyId ) == null || sentSurveyCountForPast5Days.get( companyId ) == 0 ) {
                isZeroInvitationSentInPastFiceDays = true;
            }

            //check if isMoreReminderSentInPastSevenDays is true
            boolean isMoreReminderSentInPastSevenDays = false;
            if ( getSentSurveyReminderCountFromList(
                surveStatsForPast7days ) >= getSentSurveyReminderCountFromList( surveStatsForLastToLatWeek ) * 2 ) {
                isMoreReminderSentInPastSevenDays = true;
            }

            // check if isZeroIncomingTransactionInPastOneDay is true
            boolean isZeroIncomingTransactionInPastOneDay = false;
            if ( transacionCountForPast3Days.get( companyId ) == null || transacionCountForPast3Days.get( companyId ) == 0 ) {
                isZeroIncomingTransactionInPastOneDay = true;
            }

            // check if isLessIncomingTransactionInPastSevenDays is true
            boolean isLessIncomingTransactionInPastSevenDays = false;
            if ( getTransactionReceivedCountFromList(
                surveStatsForPast7days ) <= getTransactionReceivedCountFromList( surveStatsForLastToLatWeek ) / 2 ) {
                isLessIncomingTransactionInPastSevenDays = true;
            }


            //check if isLessInvitationSentInPastSevenDaysWarning is true
            boolean isLessInvitationSentInPastSevenDaysWarning = false;
            if ( getSentSurveyCountFromList(
                surveStatsForPast7days ) <= ( getSentSurveyCountFromList( surveStatsForLastToLatWeek ) * 3 / 4 ) ) {
                isLessInvitationSentInPastSevenDaysWarning = true;
            }


            //check if isMoreReminderSentInPastSevenDays is true
            boolean isMoreReminderSentInPastSevenDaysWarning = false;
            if ( getSentSurveyReminderCountFromList(
                surveStatsForPast7days ) >= ( getSentSurveyReminderCountFromList( surveStatsForLastToLatWeek ) * 3 / 2 ) ) {
                isMoreReminderSentInPastSevenDaysWarning = true;
            }

            // check if isNoSurveyCompletedInPastThreeDays is true
            boolean isNoSurveyCompletedInPastThreeDays = false;
            if ( completedSurveyCountForPastNDays.get( companyId ) == null
                || completedSurveyCountForPastNDays.get( companyId ) == 0 ) {
                isNoSurveyCompletedInPastThreeDays = true;
            }

            EntityAlertDetails entityAlertDetails = companySettings.getEntityAlertDetails();
            if ( entityAlertDetails == null )
                entityAlertDetails = new EntityAlertDetails();

            List<String> currentErrorAlerts = new ArrayList<>();
            if ( isZeroIncomingTransactionInPastThreeDays )
                currentErrorAlerts.add( EntityErrorAlertType.LESS_TRANSACTION_IN_PAST_DAYS.getAlertType() );
            if ( isLessInvitationSentInPastSevenDays )
                currentErrorAlerts.add( EntityErrorAlertType.LESS_INVITATION_IN_PAST_DAYS.getAlertType() );

            entityAlertDetails.setCurrentErrorAlerts( currentErrorAlerts );
            if ( currentErrorAlerts.size() > 0 )
                entityAlertDetails.setErrorAlert( true );
            else
                entityAlertDetails.setErrorAlert( false );

            List<String> currentWarningAlerts = new ArrayList<>();
            if ( isZeroIncomingTransactionInPastOneDay )
                currentWarningAlerts.add( EntityWarningAlertType.LESS_TRANSACTION_IN_PAST_DAYS.getAlertType() );
            if ( isLessIncomingTransactionInPastSevenDays || isZeroInvitationSentInPastFiceDays )
                currentWarningAlerts.add( EntityWarningAlertType.LESS_TRANSACTION_IN_PAST_WEEK.getAlertType() );
            if ( isLessInvitationSentInPastSevenDaysWarning )
                currentWarningAlerts.add( EntityWarningAlertType.LESS_INVITATION_IN_PAST_WEEK.getAlertType() );
            if ( isNoSurveyCompletedInPastThreeDays )
                currentWarningAlerts.add( EntityWarningAlertType.LESS_SURVEY_COMPLETED_IN_PAST_DAYS.getAlertType() );

            entityAlertDetails.setCurrentWarningAlerts( currentWarningAlerts );
            if ( currentWarningAlerts.size() > 0 )
                entityAlertDetails.setWarningAlert( true );
            else
                entityAlertDetails.setWarningAlert( false );

            companySettings.setEntityAlertDetails( entityAlertDetails );
            organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettingsByIden(
                MongoOrganizationUnitSettingDaoImpl.KEY_ENTITY_ALERT_DETAILS, entityAlertDetails, companyId,
                MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        }

        LOG.info( "method updateTransactionMonitorAlertsForCompanies ended" );

    }


    @Override
    public int updateFileUploadStatus( long fileUploadId, int status ) throws InvalidInputException
    {
        int affectedColumns = 0;
        LOG.info( "updateFileUploadStatus() method started" );
        affectedColumns = fileUploadDao.updateStatus( fileUploadId, status );
        LOG.info( "updateFileUploadStatus() method completed" );
        return affectedColumns;
    }


    @Override
    public int updateFileUploadStatusAndFileName( long fileUploadId, int status, String location ) throws InvalidInputException
    {
        int affectedColumns = 0;
        LOG.info( "updateFileUploadStatusAndFileName() method started" );
        affectedColumns = fileUploadDao.updateStatusAndFileName( fileUploadId, status, location );
        LOG.info( "updateFileUploadStatusAndFileName() method completed" );
        return affectedColumns;
    }


    @SuppressWarnings ( "unchecked")
    private Map<Long, Long> getTotalTransactionCountForPast5Days()
    {
        LOG.debug( "getTotalTransactionCountForPast5Days() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getTotalTransactionCountForPast5Days();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (Map<Long, Long>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<Map<Long, Long>>() {}.getType() ) );
    }


    @SuppressWarnings ( "unchecked")
    private Map<Long, Long> getTransactionCountForPast3Days()
    {
        LOG.debug( "getTransactionCountForPast3Days() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi().getTransactionCountForPast3Days();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (Map<Long, Long>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<Map<Long, Long>>() {}.getType() ) );
    }


    @SuppressWarnings ( "unchecked")
    private Map<Long, Long> getSendSurveyCountForPast5Days()
    {
        LOG.debug( "getSendSurveyCountForPast5Days() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi().getSendSurveyCountForPast5Days();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (Map<Long, Long>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<Map<Long, Long>>() {}.getType() ) );
    }


    @SuppressWarnings ( "unchecked")
    private Map<Long, List<CompanySurveyStatusStats>> getSurveStatsForPast7daysForAllCompanies()
    {
        LOG.debug( "getSendSurveyCountForPreviousDay() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getSurveStatsForPast7daysForAllCompanies();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (Map<Long, List<CompanySurveyStatusStats>>) ( new Gson().fromJson(
            StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<Map<Long, List<CompanySurveyStatusStats>>>() {}.getType() ) );
    }


    @SuppressWarnings ( "unchecked")
    private Map<Long, List<CompanySurveyStatusStats>> getSurveStatsForLastToLatWeekForAllCompanies()
    {
        LOG.debug( "getSendSurveyCountForPreviousDay() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getSurveStatsForLastToLatWeekForAllCompanies();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (Map<Long, List<CompanySurveyStatusStats>>) ( new Gson().fromJson(
            StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<Map<Long, List<CompanySurveyStatusStats>>>() {}.getType() ) );
    }


    @SuppressWarnings ( "unchecked")
    private Map<Long, Long> getCompletedSurveyCountForPastNDays()
    {
        LOG.debug( "getSendSurveyCountForPreviousDay() started" );
        Response companiesListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getCompletedSurveyCountForPastNDays();

        String companiesListString = StringEscapeUtils.unescapeJava(
            companiesListResponse != null ? new String( ( (TypedByteArray) companiesListResponse.getBody() ).getBytes() )
                : null );

        return (Map<Long, Long>) ( new Gson().fromJson( StringUtils.strip( companiesListString, "\"" ),
            new TypeToken<Map<Long, Long>>() {}.getType() ) );
    }


    private int getSentSurveyCountFromList( List<CompanySurveyStatusStats> companySurveyStatusStatsList )
    {
        int sentSurveyCount = 0;
        if ( companySurveyStatusStatsList != null ) {
            for ( CompanySurveyStatusStats companySurveyStatusStats : companySurveyStatusStatsList ) {
                sentSurveyCount += companySurveyStatusStats.getSurveyInvitationSentCount();
            }
        }
        return sentSurveyCount;
    }


    private int getSentSurveyReminderCountFromList( List<CompanySurveyStatusStats> companySurveyStatusStatsList )
    {
        int sentSurveyReminderCount = 0;
        if ( companySurveyStatusStatsList != null ) {
            for ( CompanySurveyStatusStats companySurveyStatusStats : companySurveyStatusStatsList ) {
                sentSurveyReminderCount += companySurveyStatusStats.getSurveyReminderSentCount();
            }
        }
        return sentSurveyReminderCount;
    }


    private int getTransactionReceivedCountFromList( List<CompanySurveyStatusStats> companySurveyStatusStatsList )
    {
        int totalTransactionReceivedCount = 0;
        if ( companySurveyStatusStatsList != null ) {
            for ( CompanySurveyStatusStats companySurveyStatusStats : companySurveyStatusStatsList ) {
                totalTransactionReceivedCount += companySurveyStatusStats.getTransactionReceivedCount();
            }
        }
        return totalTransactionReceivedCount;
    }


    private boolean checkForNpsQuestion( String profileLevel, long entityId )
    {
        LOG.trace( "checking if survey has nps question" );
        if ( CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileLevel ) ) {

            return organizationManagementService.doesSurveyHaveNPSQuestions(
                organizationManagementService.getCompanyById( entityId ).getUsers().get( CommonConstants.INITIAL_INDEX ) );

        } else if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileLevel ) ) {

            return organizationManagementService.doesSurveyHaveNPSQuestions(
                userManagementService.getRegionById( entityId ).getCompany().getUsers().get( CommonConstants.INITIAL_INDEX ) );

        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileLevel ) ) {
            return organizationManagementService.doesSurveyHaveNPSQuestions(
                userManagementService.getBranchById( entityId ).getCompany().getUsers().get( CommonConstants.INITIAL_INDEX ) );
        }

        return false;
    }


    private void buildNpsSection( MonthlyDigestAggregate digestAggregate )
    {
        LOG.debug( " building NPS section for digest" );
        if ( digestAggregate.isHavingNpsSection() ) {

            digestAggregate.setNpsHtmlsection(
                digestMailHelper.WrapDigestMailSection( digestMailHelper.getDigestMailSectionPaddingWithSeperator()
                    + digestMailHelper.getDigestMailIntraSectionPadding()
                    + digestMailHelper.wrapDigestMailSectionTitle( CommonConstants.DIGEST_NPS_SECTION_TITLE )
                    + digestMailHelper.getDigestMailInterLinePadding()
                    + digestMailHelper.wrapDigestMailSectionDescription( CommonConstants.DIGEST_NPS_SECTION_DESC )
                    + digestMailHelper.getDigestMailIntraSectionPadding() + constructNpsComparisons( digestAggregate )
                    + digestMailHelper.getDigestMailIntraSectionPadding()
                    + digestMailHelper.wrapDigestMailSectionInference( digestAggregate.getNpsInferenceTxt() ) ) );

        } else {
            digestAggregate.setNpsHtmlsection( "" );
        }

    }


    private String constructNpsComparisons( MonthlyDigestAggregate digestAggregate )
    {
        return digestMailHelper.wrapDigestMailSectionComparison( digestMailHelper.wrapDigestMailSectionComparisonElement(
            constructNpsComparisonElement( digestAggregate.getDigestList().get( 2 ) ), CommonConstants.RIGHT )
            + digestMailHelper.wrapDigestMailSectionComparisonElement(
                constructNpsComparisonElement( digestAggregate.getDigestList().get( 1 ) ), CommonConstants.CENTER )
            + digestMailHelper.wrapDigestMailSectionComparisonElement(
                constructNpsComparisonElement( digestAggregate.getDigestList().get( 0 ) ), CommonConstants.LEFT ) );
    }


    private String constructNpsComparisonElement( DigestTemplateData digest )
    {
        return digestMailHelper
            .wrapDigestMailSectionComparisonElementValue( digest.getNpsIcon() + digest.getNps() + " "
                + digestMailHelper.wrapDigestMailTextWithSpan( CommonConstants.DIGEST_MAIL_NPS_TEXT, 14 ), 50 )
            + digestMailHelper.wrapDigestMailSectionComparisonElementStats( digestMailHelper
                .wrapDigestMailPromoterCountValue( digest.getNpsPromoters() + " "
                    + digestMailHelper.wrapDigestMailTextWithSpan( CommonConstants.SURVEY_MOOD_GREAT, 9 ) )
                + " / "
                + digestMailHelper.wrapDigestMailDetractorCountValue( digest.getNpsDetractors() + " "
                    + digestMailHelper.wrapDigestMailTextWithSpan( CommonConstants.SURVEY_MOOD_UNPLEASANT, 9 ) ) )
            + digestMailHelper.wrapDigestMailSectionComparisonElementCount( digest.getTotalCompletedNpsReviews() + " "
                + digestMailHelper.wrapDigestMailTextWithSpan( CommonConstants.TOTAL, 9 ) )
            + digestMailHelper.getSectionComparisonElementIntraPadding()
            + digestMailHelper.wrapDigestMailSectionComparisonElementMonth( StringUtils.upperCase( digest.getMonth() ) )
            + digestMailHelper.getSectionComparisonElementInterPadding();

    }


    private void constructAndSaveDigestCopy( MonthlyDigestAggregate digestAggregate ) throws IOException, NonFatalException
    {
        LOG.debug( "saving a digest copy for an entity" );
        digestMailHelper.validateDigestAggregate( digestAggregate, true );

        // save the digest mail without the disclaimer
        String digest = digestMailHelper.buildDigestMailReplacents( digestAggregate, null, null, null, true );

        long currentTime = System.currentTimeMillis();

        // create suitable file name for digest
        String uploadedFileName = "DIGEST_" + digestAggregate.getProfileLevel() + "_" + digestAggregate.getEntityId() + "_"
            + digestAggregate.getMonthUnderConcern() + "_" + digestAggregate.getYearUnderConcern() + "_" + currentTime
            + ".html";

        File digestHtml = new File( uploadedFileName );
        FileUtils.writeStringToFile( digestHtml, digest );

        // upload digest to s3
        fileUploadService.uploadFileAtDigestBucket( digestHtml, uploadedFileName );

        // remove file from file system
        digestHtml.delete();

        // create absolute file name
        uploadedFileName = endpoint + "/" + digestBucket + CommonConstants.FILE_SEPARATOR + URLEncoder.encode( uploadedFileName, "UTF-8" );

        // store the digest file name appropriately
        SavedDigestRecord digestRecord = new SavedDigestRecord();

        digestRecord.setAbsoluteFileName( uploadedFileName );
        digestRecord.setUploadedDate( new Date( currentTime ) );
        digestRecord.setMonth( digestAggregate.getMonthUnderConcern() );
        digestRecord.setYear( digestAggregate.getYearUnderConcern() );

        organizationUnitSettingsDao.saveDigestRecord( digestAggregate.getProfileLevel(), digestAggregate.getEntityId(),
            digestRecord );

    }


    private void processDigestRequest( FileUpload fileUpload ) throws InvalidInputException
    {
        LOG.debug( "method processDigestRequest started" );

        List<SavedDigestRecord> digestRecords = organizationUnitSettingsDao
            .fetchSavedDigestRecords( fileUpload.getProfileLevel(), fileUpload.getProfileValue() ).getSavedDigestRecords();

        if ( digestRecords != null && !digestRecords.isEmpty() ) {

            Date endDate = fileUpload.getEndDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime( endDate );
            String month = new DateFormatSymbols().getMonths()[cal.get( Calendar.MONTH )];
            String year = String.valueOf( cal.get( Calendar.YEAR ) );
            SavedDigestRecord digest = null;
            Date uploadedDate = new Date( CommonConstants.EPOCH_TIME_IN_MILLIS );
            for ( SavedDigestRecord digestRecord : digestRecords ) {
                if ( month.equals( digestRecord.getMonth() ) && year.equals( digestRecord.getYear() )
                    && uploadedDate.before( digestRecord.getUploadedDate() ) ) {
                    digest = digestRecord;
                    uploadedDate = digest.getUploadedDate();
                }
            }

            if ( digest != null ) {
                fileUpload.setStatus( CommonConstants.STATUS_VIEW );
                fileUpload.setFileName( digest.getAbsoluteFileName() );
            } else {
                fileUpload.setStatus( CommonConstants.STATUS_REPORT_NO_RECORDS );
            }

        } else {
            fileUpload.setStatus( CommonConstants.STATUS_REPORT_NO_RECORDS );
        }

        LOG.debug( "method processDigestRequest finished" );
    }


    @Override
	public String generateBranchRankingReportMonth(long profileValue, String profileLevel, long adminUserId,
			Timestamp startDate) throws UnsupportedEncodingException, NonFatalException {
		User user = userManagementService.getUserByUserId(adminUserId);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startDate.getTime());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		String fileName = "Branch_Ranking_Report_Month" + profileValue + "-" + user.getFirstName() + "_"
				+ user.getLastName() + "-" + (Calendar.getInstance().getTimeInMillis())
				+ CommonConstants.EXCEL_FILE_EXTENSION;
		XSSFWorkbook workbook = this.downloadBranchRankingReportMonth(profileValue, profileLevel, year, month);
		return this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
	}

	private XSSFWorkbook downloadBranchRankingReportMonth(long entityId, String entityType, int year, int month) {
		Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getBranchRankingReport(entityId,
				month, year, 1);
		String responseString = response != null ? new String(((TypedByteArray) response.getBody()).getBytes()) : null;
		if (responseString != null) {
			// since the string has ""abc"" an extra quote
			responseString = responseString.substring(1, responseString.length() - 1);
			// Escape characters
			responseString = StringEscapeUtils.unescapeJava(responseString);
		}
		List<BranchRankingReportMonth> branchRankingReportMonth = null;
		Type listType = new TypeToken<List<BranchRankingReportMonth>>() {
		}.getType();
		branchRankingReportMonth = new Gson().fromJson(responseString, listType);
		Map<Integer, List<Object>> data = workbookData.getBranchRankingReportMonthInSheet(branchRankingReportMonth);
		return workbookOperations.createWorkbook(data);
	}

	@Override
	public String generateBranchRankingReportYear(long profileValue, String profileLevel, long adminUserId,
			Timestamp startDate) throws UnsupportedEncodingException, NonFatalException {
		User user = userManagementService.getUserByUserId(adminUserId);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startDate.getTime());
		int year = calendar.get(Calendar.YEAR);
		String fileName = "Branch_Ranking_Report_Year" + profileValue + "-" + user.getFirstName() + "_"
				+ user.getLastName() + "-" + (Calendar.getInstance().getTimeInMillis())
				+ CommonConstants.EXCEL_FILE_EXTENSION;
		XSSFWorkbook workbook = this.downloadBranchRankingReportYear(profileValue, profileLevel, year);
		return this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
	}

	private XSSFWorkbook downloadBranchRankingReportYear(long entityId, String entityType, int year) {
		Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getBranchRankingReport(entityId,0,
				year,2);
		String responseString = response != null ? new String(((TypedByteArray) response.getBody()).getBytes()) : null;
		if (responseString != null) {
			// since the string has ""abc"" an extra quote
			responseString = responseString.substring(1, responseString.length() - 1);
			// Escape characters
			responseString = StringEscapeUtils.unescapeJava(responseString);
		}
		List<BranchRankingReportYear> branchRankingReportYear = null;
		Type listType = new TypeToken<List<BranchRankingReportYear>>() {
		}.getType();
		branchRankingReportYear = new Gson().fromJson(responseString, listType);
		Map<Integer, List<Object>> data = workbookData.getBranchRankingReportYearInSheet(branchRankingReportYear);
		return workbookOperations.createWorkbook(data);

	}


	@Override
	public List<BranchRankingReportMonth> getBranchRankingReportForMonth(long companyId, int month, int year) throws InvalidInputException {
		
		if(companyId < 1){
			throw new InvalidInputException("Invalid companyId.");
		} 
		if(month < 1 || month > 12){
			throw new InvalidInputException("Invalid month value.");
		}
		if(year < 0){
			throw new InvalidInputException("Invalid year value.");
		}
		
		return branchRankingMonthDao.getBranchRankingForMonth(companyId,month,year);
	}


	@Override
	public List<BranchRankingReportYear> getBranchRankingReportForYear(long companyId, int year) throws InvalidInputException {
		if(companyId < 1){
			throw new InvalidInputException("Invalid companyId.");
		} 
		if(year < 0){
			throw new InvalidInputException("Invalid year value.");
		}
		
		return branchRankingYearDao.getBranchRankingForYear(companyId,year);
	}


	@Transactional
	@Override
	public List<SurveyInvitationEmailCountMonth> getReceivedCountsMonth(long startDate, long endDate, int startIndex, int batchSize)
			throws ParseException {
		List<SurveyInvitationEmailCountMonth> receivedCountMonth = new ArrayList<SurveyInvitationEmailCountMonth>();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startDate);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String startDateStr = sdf.format(new Date(startDate));
		String endDateStr = sdf.format(new Date(endDate));
		LOG.debug("start date {} and end date {}",startDateStr,endDateStr);
		List<Object[]> receivedCount = surveyPreInitiationDao.getReceivedCountForDate(startDateStr, endDateStr,
				startIndex, batchSize);
		LOG.debug("Db returned row count : {}",receivedCount.size());
		for (Object[] obj : receivedCount) {
			if(obj[0] !=null && obj[1]!=null && obj[2] !=null) {
				SurveyInvitationEmailCountMonth mailCount = new SurveyInvitationEmailCountMonth();
				mailCount.setAgentId(new Long(obj[0].toString()));
				mailCount.setReceived(new Long(obj[1].toString()));
				mailCount.setCompanyId(new Long(obj[2].toString()));
				mailCount.setMonth(month);
				mailCount.setYear(year);
				mailCount.setAgentName((String)obj[3]);
				mailCount.setEmailId((String)obj[4]);
				receivedCountMonth.add(mailCount);
			}
		}
		return receivedCountMonth;

	}

	@Override
	@Transactional
	public boolean saveEmailCountMonthData(List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth) {
		try {
			int month = agentEmailCountsMonth.get(0).getMonth();
			int year = agentEmailCountsMonth.get(0).getYear();
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("month", month);
			queryMap.put("year", year);
			if(surveyInvitationEmailDao.getSurveyInvitationEmailReportCountForMonth(month, year) > 0) {
				surveyInvitationEmailDao.deleteOldDataForMonth(agentEmailCountsMonth.get(0).getMonth(),
						agentEmailCountsMonth.get(0).getYear());
			} else {
				LOG.info("No old data found to delete before save.");
			}
			surveyInvitationEmailDao.saveAll(agentEmailCountsMonth);
			LOG.info("Survey invitaion email count data saved to db for {}-{}",agentEmailCountsMonth.get(0).getMonth(),
					agentEmailCountsMonth.get(0).getYear());
			return true;
		} catch (Exception e) {
			LOG.error("Exception occured while saving survey invitation email count to db.");
			return false;
		}
	}


	@Override
	public String generateSurveyInvitationEmailReport(long companyId, String entityType, long adminUserId,
			Timestamp startDate) throws UnsupportedEncodingException, NonFatalException {
		LOG.info("Generating survey invitation email report.");
		User user = userManagementService.getUserByUserId(adminUserId);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startDate.getTime());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		String monthStr = new DateFormatSymbols().getMonths()[month-1];
		String fileName = "Email_Message_Report_Month_" + monthStr + "_" +
				 user.getFirstName() + " " + user.getLastName() +
				 ( Calendar.getInstance().getTimeInMillis() ) +
				 CommonConstants.EXCEL_FILE_EXTENSION;
		XSSFWorkbook workbook = this.downloadSurveyInvitationEmailReport(companyId, entityType, year, month);
		return this.createExcelFileAndSaveInAmazonS3(fileName, workbook);
	}


	private XSSFWorkbook downloadSurveyInvitationEmailReport(long companyId, String entityType, int year, int month) {
		Response response = ssApiBatchIntergrationBuilder.getIntegrationApi().getSurveyInvitationEmailReport(companyId,
				month, year);
		String responseString = response != null ? new String(((TypedByteArray) response.getBody()).getBytes()) : null;
		if (responseString != null) {
			// since the string has ""abc"" an extra quote
			responseString = responseString.substring(1, responseString.length() - 1);
			// Escape characters
			responseString = StringEscapeUtils.unescapeJava(responseString);
		}
		List<SurveyInvitationEmailCountMonth> surveyInvitationEmailCountMonth = null;
		Type listType = new TypeToken<List<SurveyInvitationEmailCountMonth>>() {
		}.getType();
		surveyInvitationEmailCountMonth = new Gson().fromJson(responseString, listType);
		Map<Integer, List<Object>> data = workbookData.getSurveyInvitationEmailReportInSheet(surveyInvitationEmailCountMonth);
		return workbookOperations.createWorkbook(data);
	}


	@Override
	@Transactional
	public List<SurveyInvitationEmailCountVO> getSurveyInvitationEmailReportForMonth(long companyId, int month, int year) {
		List<SurveyInvitationEmailCountVO> mailCountReport = new ArrayList<SurveyInvitationEmailCountVO>();
		List<Object[]> reportList = surveyInvitationEmailDao.getSurveyInvitationEmailReportForMonth(companyId,month,year);
		for(Object[] obj : reportList) {
			SurveyInvitationEmailCountVO reportObj = new SurveyInvitationEmailCountVO();
			reportObj.setAgentName((String)obj[0]);
			reportObj.setEmailId((String)obj[1]);
			reportObj.setBranchName((String)obj[2]);
			reportObj.setRegionName((String)obj[3]);
			reportObj.setReceived(new Long(obj[4].toString()));
			reportObj.setAttempted(new Long(obj[5].toString()));
			reportObj.setDelivered(new Long(obj[6].toString()));
			reportObj.setBounced(new Long(obj[7].toString()));
			reportObj.setDiffered(new Long(obj[8].toString()));
			reportObj.setOpened(new Long(obj[9].toString()));
			reportObj.setLinkClicked(new Long(obj[10].toString()));
			reportObj.setDropped(new Long(obj[11].toString()));
			reportObj.setMonth(new Integer(obj[12].toString()));
			reportObj.setYear(new Integer(obj[13].toString()));
			mailCountReport.add(reportObj);
		}
		return mailCountReport;
	}


	@Override
	@Transactional
	public List<SurveyInvitationEmailCountMonth> getAllTimeDataForSurveyInvitationMail(int startIndex, int batchSize) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("month", 0);
		queryMap.put("year", 0);
		return surveyInvitationEmailDao.findByKeyValueInBatch(SurveyInvitationEmailCountMonth.class, queryMap, startIndex, batchSize);
	}


	@Override
	@Transactional
	public List<SurveyInvitationEmailCountVO> getDataForSurveyInvitationMail(int month, int year, long companyId) {

		List<SurveyInvitationEmailCountVO> monthData = null;
		if (month == 0 && year == 0) {
			monthData = new ArrayList<>();
			Calendar cal = Calendar.getInstance();
			month = cal.get(Calendar.MONTH)+1;
			year = cal.get(Calendar.YEAR);
			List<Object[]> thisMonth = surveyInvitationEmailDao.getSurveyInvitationEmailReportForAllTime(companyId,
					month, year);

			for (Object[] obj : thisMonth) {
				SurveyInvitationEmailCountVO reportObj = new SurveyInvitationEmailCountVO();
				reportObj.setAgentName((String) obj[0]);
				reportObj.setEmailId((String) obj[1]);
				reportObj.setBranchName((String) obj[2]);
				reportObj.setRegionName((String) obj[3]);
				reportObj.setReceived(new Long(obj[4].toString()));
				reportObj.setAttempted(new Long(obj[5].toString()));
				reportObj.setDelivered(new Long(obj[6].toString()));
				reportObj.setBounced(new Long(obj[7].toString()));
				reportObj.setDiffered(new Long(obj[8].toString()));
				reportObj.setOpened(new Long(obj[9].toString()));
				reportObj.setLinkClicked(new Long(obj[10].toString()));
				reportObj.setDropped(new Long(obj[11].toString()));
				reportObj.setMonth(new Integer(obj[12].toString()));
				reportObj.setYear(new Integer(obj[13].toString()));
				monthData.add(reportObj);
			}

		} else {
			monthData = getSurveyInvitationEmailReportForMonth(companyId, month, year); 
		}
		return monthData;
	}
	

    

    @Override
    public boolean isSocialMonitorEnabled( long companyId ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "method enableSocialMonitorToggle() started." );

        if ( companyId <= 0 ) {
            LOG.warn( "companyId is invalid" );
            throw new InvalidInputException( "companyId is invalid" );
        }

        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( companyId );

        if ( unitSettings == null ) {
            LOG.warn( "settings are not specified" );
            throw new InvalidInputException( "settings cannot be null." );
        }

        return unitSettings.isSocialMonitorEnabled();
    }


    @Override
    public List<List<Object>> getRecentActivityListForSocialMonitor( Long entityId, String entityType, int startIndex,
        int batchSize ) throws InvalidInputException
    {
        List<List<Object>> recentActivity = new ArrayList<>();
        for ( FileUpload fileUpload : fileUploadDao.findRecentActivityForSocialMonitorReporting( entityId, entityType,
            startIndex, batchSize ) ) {
            List<Object> recentActivityList = new ArrayList<>();
            User user = userManagementService.getUserByUserId( fileUpload.getAdminUserId() );
            recentActivityList
                .add( utils.convertDateToTimeZone( fileUpload.getCreatedOn().getTime(), CommonConstants.TIMEZONE_EST ) );
            // Set the ReportName according to the upload type
            if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT ) {
                recentActivityList.add( CommonConstants.SOCIAL_MONITOR_DATE_REPORT );
            } else if ( fileUpload.getUploadType() == CommonConstants.FILE_UPLOAD_SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD ) {
                recentActivityList.add( CommonConstants.SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD );
            }

            recentActivityList.add( fileUpload.getStartDate() );
            recentActivityList.add( fileUpload.getEndDate() );
            recentActivityList.add( user.getFirstName() );
            recentActivityList.add( user.getLastName() );
            recentActivityList.add( fileUpload.getStatus() );
            recentActivityList.add( fileUpload.getFileName() );
            recentActivityList.add( fileUpload.getFileUploadId() );
            recentActivityList.add( fileUpload.getUploadType() );
            recentActivity.add( recentActivityList );
        }
        return recentActivity;
    }
    
    @Override
    public Long getRecentActivityCountForSocialMonitor( Long entityId, String entityType )
    {
        return fileUploadDao.getRecentActivityCountForSocialMonitorReporting( entityId, entityType );

    }
    
    
}
