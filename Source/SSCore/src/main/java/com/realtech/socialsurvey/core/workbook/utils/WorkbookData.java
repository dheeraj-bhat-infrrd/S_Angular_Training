package com.realtech.socialsurvey.core.workbook.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.RegionDao;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialPostDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.BranchRankingReportMonth;
import com.realtech.socialsurvey.core.entities.BranchRankingReportYear;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyDetailsReport;
import com.realtech.socialsurvey.core.entities.NpsReportMonth;
import com.realtech.socialsurvey.core.entities.NpsReportWeek;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.ReportingSurveyPreInititation;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyCompanyMapping;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;
import com.realtech.socialsurvey.core.vo.SurveyTransactionReportVO;
import com.realtech.socialsurvey.core.vo.UserList;


/**
 * @author RareMile
 *
 */
@Component
public class WorkbookData
{
    private static final Logger LOG = LoggerFactory.getLogger( WorkbookData.class );

    public static final String EXCEL_FORMAT = "application/vnd.ms-excel";
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    public static final String EXCEL_FILE_EXTENSION = ".xlsx";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( CommonConstants.DATE_FORMAT );
    public static final SimpleDateFormat REPORTING_DATE_FORMATTER = new SimpleDateFormat( CommonConstants.REPORTING_API_DATE_FORMAT );
    private static final String SURVEY_INVITATION_EMAIL_REPORT_HEADER = "Agent Name,Agent Email,Agent Branch,Agent Region,"
    		+ "Transactions Received for the Agent this month,Number of Emails sent for this agent this month,Number of emails delivered,"
    		+ "Number of email bounced,Number of emails dropped,Number of emails deferred,Number of emails opened,Number of Surveys Clicked";
    
	private static final String SURVEY_TRANSACTION_REPORT_HEADERS = "Name,User ID,Email ID,TRX_MONTH,NMLS,License ID,Company Name,"
			+ "Region Name,Branch Name,Total number of reviews [Zillow + SocialSurvey],Total number of Zillow Reviews,"
			+ "Total number of 3rd party reviews,Total number of verified customer reviews,Total number of unverified customer reviews,"
			+ "Total number of SocialSurvey Reviews,Total number of Abusive Reviews,Total number of Retake Reviews,"
			+ "Total number of Retake Completed,Transaction Received by Source,Transaction Sent,Transaction Unprocessable,"
			+ "Transaction Clicked,Transaction Completed,Transaction Partially Completed,Transaction Unopened,Transaction Duplicates";

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private RegionDao regionDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private GenericDao<SurveyCompanyMapping, Long> surveyCompanyMappingDao;

    @Autowired
    private GenericDao<SurveyQuestionsMapping, Long> surveyQuestionsMappingDao;
    
    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;


    public Map<Integer, List<Object>> getCorruptSurveyDataToBeWrittenInSheet(
        SurveyPreInitiationList surveyPreInitiationListVO )
    {
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        Integer counter = 1;
        for ( SurveyPreInitiation survey : surveyPreInitiationListVO.getSurveyPreInitiationList() ) {
            surveyDetailsToPopulate.add( survey.getAgentName() );
            surveyDetailsToPopulate.add( survey.getAgentEmailId() );
            surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
            surveyDetailsToPopulate.add( survey.getCustomerLastName() );
            surveyDetailsToPopulate.add( survey.getCustomerEmailId() );
            surveyDetailsToPopulate.add( survey.getEngagementClosedTime() );
            surveyDetailsToPopulate.add( survey.getErrorCodeDescription() );
            data.put( ++counter, surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }
        surveyDetailsToPopulate.add( "Agent Name" );
        surveyDetailsToPopulate.add( "Transaction Email" );
        surveyDetailsToPopulate.add( "Customer First Name" );
        surveyDetailsToPopulate.add( "Customer Last Name" );
        surveyDetailsToPopulate.add( "Customer Email" );
        surveyDetailsToPopulate.add( "Date" );
        surveyDetailsToPopulate.add( "Reason" );
        data.put( 1, surveyDetailsToPopulate );
        return data;
    }


    public Map<Integer, List<Object>> getMappedSurveyDataToBeWrittenInSheet( UserList userList )
    {
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> userDetailsToPopulate = new ArrayList<>();
        Integer counter = 1;
        for ( User user : userList.getUsers() ) {
            userDetailsToPopulate.add( user.getFirstName() );
            userDetailsToPopulate.add( user.getMappedEmails() );
            data.put( ++counter, userDetailsToPopulate );
            userDetailsToPopulate = new ArrayList<>();
        }
        userDetailsToPopulate.add( "Name" );
        userDetailsToPopulate.add( "Mapped Email Id(s)" );
        data.put( 1, userDetailsToPopulate );
        return data;
    }


    public Map<Integer, List<Object>> getUnmatchedOrProcessedSurveyDataToBeWrittenInSheet(
        SurveyPreInitiationList surveyPreInitiationListVO )
    {
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        Integer counter = 1;
        for ( SurveyPreInitiation survey : surveyPreInitiationListVO.getSurveyPreInitiationList() ) {
            surveyDetailsToPopulate.add( survey.getAgentName() );
            surveyDetailsToPopulate.add( survey.getAgentEmailId() );
            surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
            surveyDetailsToPopulate.add( survey.getCustomerLastName() );
            surveyDetailsToPopulate.add( survey.getCustomerEmailId() );
            surveyDetailsToPopulate.add( survey.getEngagementClosedTime() );
            data.put( ++counter, surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }
        surveyDetailsToPopulate.add( "Agent Name" );
        surveyDetailsToPopulate.add( "Transaction Email" );
        surveyDetailsToPopulate.add( "Customer First Name" );
        surveyDetailsToPopulate.add( "Customer Last Name" );
        surveyDetailsToPopulate.add( "Customer Email" );
        surveyDetailsToPopulate.add( "Date" );
        data.put( 1, surveyDetailsToPopulate );
        return data;
    }


    public Map<Integer, List<Object>> getIncompleteSurveyDataToBeWrittenInSheet( List<SurveyPreInitiation> surveyDetails )
    {
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        Integer counter = 1;
        for ( SurveyPreInitiation survey : surveyDetails ) {
            surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
            surveyDetailsToPopulate.add( survey.getCustomerLastName() );
            surveyDetailsToPopulate.add( survey.getCustomerEmailId() );
            surveyDetailsToPopulate.add( survey.getCreatedOn() );
            surveyDetailsToPopulate.add( survey.getModifiedOn() );
            data.put( ++counter, surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }
        surveyDetailsToPopulate.add( "First Name" );
        surveyDetailsToPopulate.add( "Last Name" );
        surveyDetailsToPopulate.add( "Email Id" );
        surveyDetailsToPopulate.add( "Started On" );
        surveyDetailsToPopulate.add( "Last Updated On" );
        data.put( 1, surveyDetailsToPopulate );
        return data;
    }


    public Map<Integer, List<Object>> getSocialMonitorDataToBeWrittenInSheet( List<SocialPost> socialPosts )
    {
        Integer counter = 1;
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> socialPostsToPopulate = new ArrayList<>();
        for ( SocialPost post : socialPosts ) {
            if ( post.getSource() != null && !post.getSource().isEmpty() ) {
                socialPostsToPopulate.add( post.getPostText() );
                socialPostsToPopulate.add( DATE_FORMATTER.format( new Date( post.getTimeInMillis() ) ) );
                socialPostsToPopulate.add( post.getSource() );
                try {
                    if ( post.getAgentId() > 0 ) {
                        socialPostsToPopulate.add( "user" );

                        socialPostsToPopulate
                            .add( organizationManagementService.getAgentSettings( post.getAgentId() ).getProfileName() );

                    } else if ( post.getBranchId() > 0 ) {
                        socialPostsToPopulate.add( "branch" );
                        socialPostsToPopulate.add(
                            organizationManagementService.getBranchSettingsDefault( post.getBranchId() ).getProfileName() );
                    } else if ( post.getRegionId() > 0 ) {
                        socialPostsToPopulate.add( "region" );
                        socialPostsToPopulate
                            .add( organizationManagementService.getRegionSettings( post.getRegionId() ).getProfileName() );
                    } else if ( post.getCompanyId() > 0 ) {
                        socialPostsToPopulate.add( "company" );
                        socialPostsToPopulate
                            .add( organizationManagementService.getCompanySettings( post.getCompanyId() ).getProfileName() );
                    } else {
                        socialPostsToPopulate.add( "unavailable" );
                        socialPostsToPopulate.add( "unavailable" );
                    }
                    socialPostsToPopulate.add( post.getPostedBy() );
                    socialPostsToPopulate.add( post.getPostUrl() );
                } catch ( InvalidInputException e ) {
                    e.printStackTrace();
                } catch ( NoRecordsFetchedException e ) {
                    e.printStackTrace();
                }

                data.put( ++counter, socialPostsToPopulate );
                socialPostsToPopulate = new ArrayList<>();
            }
        }
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_COMMENT );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_DATE );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_SOURCE );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_LEVEL );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_LEVEL_NAME );
        socialPostsToPopulate.add( CommonConstants.HEADER_POSTED_BY );
        socialPostsToPopulate.add( CommonConstants.HEADER_POST_URL );
        data.put( 1, socialPostsToPopulate );
        return data;
    }


    @Transactional
    public Map<Integer, List<Object>> getCustomerSurveyResultDataToBeWrittenInSheet( List<SurveyDetails> surveyDetails,
        long companyId ) throws InvalidInputException
    {
        // Finding max questions
        int max = 0;
        int internalMax = 0;
        for ( SurveyDetails survey : surveyDetails ) {
            if ( survey.getSurveyResponse() != null ) {
                internalMax = survey.getSurveyResponse().size();
                if ( internalMax > max ) {
                    max = internalMax;
                }
            }
        }
        if ( max == 0 ) {
            try {
                // Find Survey Questions configured for company
                Company company = companyDao.findById( Company.class, companyId );
                Map<String, Object> queries = new HashMap<String, Object>();
                queries.put( CommonConstants.COMPANY_COLUMN, company );
                queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
                List<SurveyCompanyMapping> surveyCompanyMappingList = surveyCompanyMappingDao
                    .findByKeyValue( SurveyCompanyMapping.class, queries );
                if ( surveyCompanyMappingList != null && surveyCompanyMappingList.size() > 0 ) {
                    Survey survey = surveyCompanyMappingList.get( 0 ).getSurvey();
                    queries = new HashMap<String, Object>();
                    queries.put( CommonConstants.SURVEY_COLUMN, survey );
                    queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
                    List<SurveyQuestionsMapping> surveyQuestionsMappings = surveyQuestionsMappingDao.findByKeyValueAscending(
                        SurveyQuestionsMapping.class, queries, CommonConstants.SURVEY_QUESTION_ORDER_COLUMN );
                    if ( surveyQuestionsMappings != null && surveyCompanyMappingList.size() > 0 ) {
                        max = surveyCompanyMappingList.size();
                    }
                }
            } catch ( Exception e ) {
                LOG.warn( "Error occurred while fetching survey question details for company id : " + companyId );
            }
        }
        Integer counter = 1;
        Map<Integer, List<Object>> data = new TreeMap<>();


        //get the branches and regions for company
        Map<Long, Branch> branchesForCompany = new HashMap<Long, Branch>();
        Map<Long, Region> regionsForCompany = new HashMap<Long, Region>();

        List<Region> regionList = regionDao.getRegionsForCompany( companyId, -1, -1 );
        List<Branch> branchList = branchDao.getBranchesForCompany( companyId, CommonConstants.IS_DEFAULT_BY_SYSTEM_NO, -1, -1 );
        for ( Region region : regionList ) {
            if ( region != null )
                regionsForCompany.put( region.getRegionId(), region );
        }
        for ( Branch branch : branchList ) {
            if ( branch != null )
                branchesForCompany.put( branch.getBranchId(), branch );
        }

        //create list of objects to populate
        List<Object> surveyDetailsToPopulate = new ArrayList<>();

        for ( SurveyDetails survey : surveyDetails ) {
            if ( survey.getSurveyResponse() != null ) {
                String agentName = survey.getAgentName();
                surveyDetailsToPopulate.add( agentName.substring( 0, agentName.lastIndexOf( ' ' ) ) );
                surveyDetailsToPopulate.add( agentName.substring( agentName.lastIndexOf( ' ' ) + 1 ) );
                surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
                surveyDetailsToPopulate.add( survey.getCustomerLastName() );
                surveyDetailsToPopulate.add( DATE_FORMATTER.format( survey.getCreatedOn() ) );
                surveyDetailsToPopulate.add( DATE_FORMATTER.format( survey.getModifiedOn() ) );
                surveyDetailsToPopulate.add( Days
                    .daysBetween( new DateTime( survey.getCreatedOn() ), new DateTime( survey.getModifiedOn() ) ).getDays() );
                if ( survey.getSource() != null && !survey.getSource().isEmpty() ) {
                    if ( survey.getSource().equals( CommonConstants.SURVEY_REQUEST_AGENT ) )
                        surveyDetailsToPopulate.add( "user" );
                    else
                        surveyDetailsToPopulate.add( survey.getSource() );
                } else {
                    surveyDetailsToPopulate.add( MongoSocialPostDaoImpl.KEY_SOURCE_SS );
                }
                if ( survey.getSourceId() != null && !survey.getSourceId().isEmpty() ) {
                    surveyDetailsToPopulate.add( survey.getSourceId() );
                } else {
                    surveyDetailsToPopulate.add( "" );
                }

                //add score
                surveyDetailsToPopulate.add( surveyHandler.getFormattedSurveyScore( survey.getScore() ) );
                if ( survey.getSurveyResponse() != null && !survey.getSurveyResponse().isEmpty() ) {
                    for ( SurveyResponse response : survey.getSurveyResponse() ) {
                        if ( !StringUtils.isEmpty( response.getAnswer() ) && StringUtils.isNumeric( response.getAnswer() ) ) {
                            Integer responseInt = Integer.parseInt( response.getAnswer() );
                            surveyDetailsToPopulate.add( responseInt );
                        } else {
                            surveyDetailsToPopulate.add( response.getAnswer() );
                        }
                    }
                } else {
                    surveyDetailsToPopulate.add( "" );
                }
                if ( survey.getSurveyResponse().size() < max ) {
                    for ( int i = survey.getSurveyResponse().size() + 1; i <= max; i++ ) {
                        surveyDetailsToPopulate.add( "" );
                    }
                }
                surveyDetailsToPopulate.add( survey.getMood() );
                surveyDetailsToPopulate.add( survey.getReview() );
                if ( survey.getMood() != null && survey.getMood().equals( CommonConstants.SURVEY_MOOD_GREAT )
                    && survey.getAgreedToShare() != null && !survey.getAgreedToShare().isEmpty() ) {
                    String status = survey.getAgreedToShare();
                    if ( status.equals( "true" ) ) {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_YES );
                    } else {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                    }
                } else {
                    surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                }

                surveyDetailsToPopulate.add( branchesForCompany.get( survey.getBranchId() ) != null
                    ? branchesForCompany.get( survey.getBranchId() ).getBranch() : "" );
                
                if ( survey.getSocialMediaPostDetails() != null ) {
                    //for company
                    Set<String> companySocialMedia = new HashSet<>();
                    if ( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails() != null
                        && survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn() != null
                        && !survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn().isEmpty() ) {
                        companySocialMedia
                            .addAll( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn() );
                    }
                    surveyDetailsToPopulate.add( StringUtils.join( companySocialMedia, "," ) );

                    //for agent
                    Set<String> agentSocialMedia = new HashSet<>();
                    if ( survey.getSocialMediaPostDetails().getAgentMediaPostDetails() != null
                        && survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn() != null
                        && !survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn().isEmpty() ) {
                        agentSocialMedia.addAll( survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn() );
                    }
                    surveyDetailsToPopulate.add( StringUtils.join( agentSocialMedia, "," ) );

                    //for region
                    String regionShared = "";
                    if ( survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null
                        && !survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList().isEmpty() ) {
                        for ( RegionMediaPostDetails regionMediaDetail : survey.getSocialMediaPostDetails()
                            .getRegionMediaPostDetailsList() ) {
                            //get region
                            Region region = regionsForCompany.get( regionMediaDetail.getRegionId() );
                            //get shared on for region
                            if ( regionMediaDetail.getSharedOn() != null && !regionMediaDetail.getSharedOn().isEmpty()
                                && region != null
                                && region.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                                regionShared += region.getRegion() + ": { "
                                    + StringUtils.join( regionMediaDetail.getSharedOn(), "," ) + " }, ";
                            }
                        }
                    }
                    if ( regionShared.contains( "}," ) )
                        regionShared = regionShared.substring( 0, regionShared.lastIndexOf( "," ) );
                    surveyDetailsToPopulate.add( regionShared );

                    //for branch
                    String branchShared = "";
                    if ( survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null
                        && !survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList().isEmpty() ) {
                        for ( BranchMediaPostDetails branchMediaDetail : survey.getSocialMediaPostDetails()
                            .getBranchMediaPostDetailsList() ) {
                            //get branch
                            Branch branch = branchesForCompany.get( branchMediaDetail.getBranchId() );
                            //get shared on for region
                            if ( branchMediaDetail.getSharedOn() != null && !branchMediaDetail.getSharedOn().isEmpty()
                                && branch != null
                                && branch.getIsDefaultBySystem() == CommonConstants.IS_DEFAULT_BY_SYSTEM_NO ) {
                                branchShared += branch.getBranch() + ": { "
                                    + StringUtils.join( branchMediaDetail.getSharedOn(), "," ) + "}, ";
                            }
                        }
                    }
                    if ( branchShared.contains( "}," ) )
                        branchShared = branchShared.substring( 0, branchShared.lastIndexOf( "," ) );
                    surveyDetailsToPopulate.add( branchShared );
                }
                
                data.put( ++counter, surveyDetailsToPopulate );
                surveyDetailsToPopulate = new ArrayList<>();
            } else if ( survey.getSource().equalsIgnoreCase( CommonConstants.SURVEY_SOURCE_ZILLOW ) ) {
                if ( survey.getAgentId() > 0 ) {
                    String agentName = survey.getAgentName();
                    if ( agentName == null || agentName.isEmpty() ) {
                        try {
                            AgentSettings agentSettings = organizationManagementService.getAgentSettings( survey.getAgentId() );
                            if ( agentSettings.getContact_details() != null
                                && agentSettings.getContact_details().getName() != null
                                && !agentSettings.getContact_details().getName().isEmpty() ) {
                                agentName = agentSettings.getContact_details().getName();
                            } else {
                                agentName = "";
                            }
                        } catch ( NoRecordsFetchedException e ) {
                            LOG.warn( "Error occurred while fetching agent settings for id : " + survey.getAgentId() );
                        }
                    }
                    if ( agentName.contains( " " ) ) {
                        surveyDetailsToPopulate.add( agentName.substring( 0, agentName.lastIndexOf( ' ' ) ) );
                        surveyDetailsToPopulate.add( agentName.substring( agentName.lastIndexOf( ' ' ) + 1 ) );
                    } else {
                        surveyDetailsToPopulate.add( agentName );
                        surveyDetailsToPopulate.add( "" );
                    }
                    surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
                    surveyDetailsToPopulate.add( "" );
                    surveyDetailsToPopulate.add( DATE_FORMATTER.format( survey.getCreatedOn() ) );
                    surveyDetailsToPopulate.add( DATE_FORMATTER.format( survey.getModifiedOn() ) );
                    surveyDetailsToPopulate
                        .add( Days.daysBetween( new DateTime( survey.getCreatedOn() ), new DateTime( survey.getModifiedOn() ) )
                            .getDays() );
                    surveyDetailsToPopulate.add( survey.getSource() );
                    surveyDetailsToPopulate.add( survey.getSourceId() );

                    //add score
                    surveyDetailsToPopulate.add( surveyHandler.getFormattedSurveyScore( survey.getScore() ) );

                    // Since Zillow reviews have no Survey Response Data, push empty data
                    for ( int i = 1; i <= max; i++ ) {
                        surveyDetailsToPopulate.add( "" );
                    }
                    surveyDetailsToPopulate.add( "" );
                    surveyDetailsToPopulate.add( survey.getReview() );
                    if ( survey.getAgreedToShare() != null && !survey.getAgreedToShare().isEmpty() ) {
                        String status = survey.getAgreedToShare();
                        if ( status.equals( "true" ) ) {
                            surveyDetailsToPopulate.add( CommonConstants.STATUS_YES );
                        } else {
                            surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                        }
                    } else {
                        surveyDetailsToPopulate.add( CommonConstants.STATUS_NO );
                    }
                    
                    surveyDetailsToPopulate.add( branchesForCompany.get( survey.getBranchId() ) != null
                        ? branchesForCompany.get( survey.getBranchId() ).getBranch() : "" );
                    
                    if ( survey.getSocialMediaPostDetails() != null ) {
                        Set<String> socialMedia = new HashSet<>();
                        if ( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails() != null
                            && survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn() != null
                            && !survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn().isEmpty() ) {
                            socialMedia.addAll( survey.getSocialMediaPostDetails().getCompanyMediaPostDetails().getSharedOn() );
                        }
                        if ( survey.getSocialMediaPostDetails().getAgentMediaPostDetails() != null
                            && survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn() != null
                            && !survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn().isEmpty() ) {
                            socialMedia.addAll( survey.getSocialMediaPostDetails().getAgentMediaPostDetails().getSharedOn() );
                        }
                        if ( survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList() != null
                            && !survey.getSocialMediaPostDetails().getRegionMediaPostDetailsList().isEmpty() ) {
                            for ( RegionMediaPostDetails regionMediaDetail : survey.getSocialMediaPostDetails()
                                .getRegionMediaPostDetailsList() ) {
                                if ( regionMediaDetail.getSharedOn() != null && !regionMediaDetail.getSharedOn().isEmpty() ) {
                                    socialMedia.addAll( regionMediaDetail.getSharedOn() );
                                }
                            }
                        }
                        if ( survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList() != null
                            && !survey.getSocialMediaPostDetails().getBranchMediaPostDetailsList().isEmpty() ) {
                            for ( BranchMediaPostDetails branchMediaDetail : survey.getSocialMediaPostDetails()
                                .getBranchMediaPostDetailsList() ) {
                                if ( branchMediaDetail.getSharedOn() != null && !branchMediaDetail.getSharedOn().isEmpty() ) {
                                    socialMedia.addAll( branchMediaDetail.getSharedOn() );
                                }
                            }
                        }
                        surveyDetailsToPopulate.add( StringUtils.join( socialMedia, "," ) );
                    }

                    data.put( ++counter, surveyDetailsToPopulate );
                    surveyDetailsToPopulate = new ArrayList<>();
                }
            }
        }

        // Setting up headers
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AGENT_FIRST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AGENT_LAST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CUSTOMER_FIRST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CUSTOMER_LAST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_SENT_DATE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_COMPLETED_DATE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_TIME_INTERVAL );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_SOURCE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_SOURCE_ID );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_SCORE );
        for ( counter = 1; counter <= max; counter++ ) {
            surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_QUESTION + counter );
        }
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SURVEY_GATEWAY );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CUSTOMER_COMMENTS );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AGREED_SHARE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_BRANCH );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CLICK_THROUGH_FOR_COMPANY );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CLICK_THROUGH_FOR_AGENT );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CLICK_THROUGH_FOR_REGIONS );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_CLICK_THROUGH_FOR_BRANCHES );
        data.put( 1, surveyDetailsToPopulate );
        return data;
    }


    public Map<Integer, List<Object>> getAgentRankingDataToBeWrittenInSheet( List<AgentRankingReport> agentDetails )
    {
        Integer counter = 1;
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        for ( AgentRankingReport agentDetail : agentDetails ) {
            surveyDetailsToPopulate.add( agentDetail.getAgentFirstName() );
            surveyDetailsToPopulate.add( agentDetail.getAgentLastName() );
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis( agentDetail.getRegistrationDate() );
            surveyDetailsToPopulate.add( DATE_FORMATTER.format( calendar.getTime() ) );
            surveyDetailsToPopulate.add( agentDetail.getAverageScore() );
            surveyDetailsToPopulate.add( agentDetail.getCompletedSurveys() );
            surveyDetailsToPopulate.add( agentDetail.getIncompleteSurveys() );
            surveyDetailsToPopulate.add( agentDetail.getIncompleteSurveys() + agentDetail.getCompletedSurveys() );

            data.put( ++counter, surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }
        surveyDetailsToPopulate.add( CommonConstants.HEADER_FIRST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_LAST_NAME );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_REGISTRATION_DATE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_AVG_SCORE );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_COMPLETED_SURVEY_COUNT );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_INCOMPLETE_SURVEY_COUNT );
        surveyDetailsToPopulate.add( CommonConstants.HEADER_SUM_SURVEYS );
        data.put( 1, surveyDetailsToPopulate );
        return data;
    }


    public Map<Integer, List<Object>> getUserAdoptionReportDataToBeWrittenInSheet( List<Object[]> rows )
    {
        Integer counter = 1;
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> userAdoptionReportToPopulate = new ArrayList<>();
        for ( Object[] row : rows ) {
            // row 0 - company
            // row 1 - region
            // row 2 - branch
            // row 3 - invited users
            // row 4 - active users
            // row 5 - adoption rate
            userAdoptionReportToPopulate.add( String.valueOf( row[0] ) );
            if ( row[1] != null && !CommonConstants.DEFAULT_REGION_NAME.equalsIgnoreCase( String.valueOf( row[1] ) ) )
                userAdoptionReportToPopulate.add( String.valueOf( row[1] ) );
            else
                userAdoptionReportToPopulate.add( "" );
            if ( row[2] != null && !CommonConstants.DEFAULT_BRANCH_NAME.equalsIgnoreCase( String.valueOf( row[2] ) ) )
                userAdoptionReportToPopulate.add( String.valueOf( row[2] ) );
            else
                userAdoptionReportToPopulate.add( "" );
            Integer userCount = new Integer( String.valueOf( row[3] ) );
            Integer activeUserCount = new Integer( String.valueOf( row[4] ) );
            String adoptionRate = String.valueOf( row[5] ).replace( "\\.00", "" );
            userAdoptionReportToPopulate.add( userCount );
            userAdoptionReportToPopulate.add( activeUserCount );
            userAdoptionReportToPopulate.add( adoptionRate != "null" ? adoptionRate : "0%" );
            data.put( ++counter, userAdoptionReportToPopulate );
            userAdoptionReportToPopulate = new ArrayList<>();
        }
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_COMPANY );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_REGION );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_BRANCH );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_INVITED_USERS );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_ACTIVE_USERS );
        userAdoptionReportToPopulate.add( CommonConstants.HEADER_ADOPTION_RATES );
        data.put( 1, userAdoptionReportToPopulate );
        return data;
    }


    public Map<Integer, List<Object>> getCompanyReportDataToBeWrittenInSheet( List<Company> companies )
    {
        Integer counter = 1;
        int max = 0;
        int internalMax = 0;
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> companyDetailsToPopulate = new ArrayList<>();
        for ( Company company : companies ) {
            internalMax = 0;
            companyDetailsToPopulate.add( company.getCompany() );
            if ( company.getLicenseDetails() != null && !company.getLicenseDetails().isEmpty() )
                companyDetailsToPopulate.add( company.getLicenseDetails().get( 0 ).getAccountsMaster().getAccountName() );
            else
                companyDetailsToPopulate.add( "" );
            companyDetailsToPopulate.add( company.getCreatedOn() );
            companyDetailsToPopulate.add( company.getDisplayBillingMode() );
            if ( company.getLicenseDetails() != null && !company.getLicenseDetails().isEmpty() )
                companyDetailsToPopulate.add( "Registered" );
            else
                companyDetailsToPopulate.add( "Not registered" );
            data.put( ++counter, companyDetailsToPopulate );
            companyDetailsToPopulate = new ArrayList<>();
            if ( internalMax > max )
                max = internalMax;
        }
        companyDetailsToPopulate.add( "Company Name" );
        companyDetailsToPopulate.add( "Account Type" );
        companyDetailsToPopulate.add( "Created on" );
        companyDetailsToPopulate.add( "Billing mode" );
        companyDetailsToPopulate.add( "Status" );
        data.put( 1, companyDetailsToPopulate );
        return data;
    }
    
    public Map<Integer, List<Object>> getSurveyStatsReportToBeWrittenInSheet( List<List<String>> surveyStats )
    {
     // This data needs to be written (List<Object>)
        Map<Integer, List<Object>> surveyStatsData = new TreeMap<>();
        
        Integer surveyStatsCounter = 1;
        
        List<Object> surveyStatsReportToPopulate = new ArrayList<>();
        
        for(List<String> row : surveyStats ){
            surveyStatsReportToPopulate.add(String.valueOf( row.get( 0 ) ));
            surveyStatsReportToPopulate.add(String.valueOf( row.get( 1 ) ));
            surveyStatsReportToPopulate.add(String.valueOf( row.get( 2 ) ));
            surveyStatsReportToPopulate.add(String.valueOf( row.get( 3 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 4 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 5 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 6 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 7 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 8 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 9 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 10 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 11 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 12 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 13 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 14 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 15 ) ));
            surveyStatsReportToPopulate.add(Double.valueOf( row.get( 16 ) ));
            surveyStatsReportToPopulate.add(Integer.valueOf( row.get( 17 ) ));
            surveyStatsData.put(++surveyStatsCounter ,surveyStatsReportToPopulate );
            surveyStatsReportToPopulate = new ArrayList<>();
            
        }
        // Setting up user sheet headers
        surveyStatsReportToPopulate.add( "ID" );
        surveyStatsReportToPopulate.add( "COMPANY" );
        surveyStatsReportToPopulate.add( "BRANCH" );
        surveyStatsReportToPopulate.add( "MONTH" );
        surveyStatsReportToPopulate.add( "TRX_RCVD" );
        surveyStatsReportToPopulate.add( "PENDING" );
        surveyStatsReportToPopulate.add( "DUPLICATES" );
        surveyStatsReportToPopulate.add( "CORRUPTED" );
        surveyStatsReportToPopulate.add( "ABUSIVE" );
        surveyStatsReportToPopulate.add( "OLD RECORDS" );
        surveyStatsReportToPopulate.add( "IGNORED" );
        surveyStatsReportToPopulate.add( "MISMATCHED" );
        surveyStatsReportToPopulate.add( "SURVEYS SENT" );
        surveyStatsReportToPopulate.add( "SURVEYS CLICKED" );
        surveyStatsReportToPopulate.add( "SURVEYS COMPLETED" );
        surveyStatsReportToPopulate.add( "SURVEYS PARTIALLY COMPLETED" );
        surveyStatsReportToPopulate.add( "COMPLETE PERCENTAGE" );
        surveyStatsReportToPopulate.add( "DELTA" );
        surveyStatsData.put( 1, surveyStatsReportToPopulate );
        
        return surveyStatsData;
        
    }
    
    public Map<Integer, List<Object>> getUserAdoptionReportToBeWrittenInSheet( List<List<String>> userAdoption )
    {
     // This data needs to be written (List<Object>)
        Map<Integer, List<Object>>  userAdoptionData = new TreeMap<>();
        
        Integer userAdoptionCounter = 1;
        
        List<Object> userAdoptionReportToPopulate = new ArrayList<>();
        
        for(List<String> row : userAdoption ){
            userAdoptionReportToPopulate.add(String.valueOf( row.get( 0 ) ));
            userAdoptionReportToPopulate.add(String.valueOf( row.get( 1 ) ));
            userAdoptionReportToPopulate.add(String.valueOf( row.get( 2 ) ));
            userAdoptionReportToPopulate.add(Integer.valueOf( row.get( 3 ) ));
            userAdoptionReportToPopulate.add(Integer.valueOf( row.get( 4 ) ));
            userAdoptionReportToPopulate.add(Double.valueOf( row.get( 5 ) ));
            userAdoptionData.put(++userAdoptionCounter ,userAdoptionReportToPopulate );
            userAdoptionReportToPopulate = new ArrayList<>();
            
        }
        // Setting up user sheet headers
        userAdoptionReportToPopulate.add( "Company" );
        userAdoptionReportToPopulate.add( "Region" );
        userAdoptionReportToPopulate.add( "Branch" );
        userAdoptionReportToPopulate.add( "Invited Users" );
        userAdoptionReportToPopulate.add( "Verified Users" );
        userAdoptionReportToPopulate.add( "Verified Percentage" );
        userAdoptionData.put( 1, userAdoptionReportToPopulate );
        
        return userAdoptionData;
        
    }
    
    public Map<Integer, List<Object>> getSurveyResultsReportToBeWrittenInSheet( List<SurveyResultsReportVO> surveyResultsReportVO , int maxQuestions , int surveyDataCounter) throws ParseException
    {
        Map<Integer, List<Object>>  surveyResultsReportData = new TreeMap<>();
        
        Integer surveyResultReportCounter = surveyDataCounter;
        SimpleDateFormat date = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss.S" );
        
        List<Object> surveyResultsReportToPopulate = new ArrayList<>();
        for(SurveyResultsReportVO row : surveyResultsReportVO ){
            surveyResultsReportToPopulate.add(row.getUserFirstName());
            surveyResultsReportToPopulate.add(row.getUserLastName());
            surveyResultsReportToPopulate.add(row.getAgentEmailId());
            surveyResultsReportToPopulate.add(row.getState());
            surveyResultsReportToPopulate.add(row.getCity());
            surveyResultsReportToPopulate.add(row.getCustomerFirstName());
            surveyResultsReportToPopulate.add(row.getCustomerLastName());
            surveyResultsReportToPopulate.add(row.getCustomerEmailId());
            surveyResultsReportToPopulate.add(row.getParticipantType());
                String sentDate = "";
                if (row.getSurveySentDate()!=null){
                    sentDate = row.getSurveySentDate().toString();
                    surveyResultsReportToPopulate.add(date.parse( sentDate ));
                }else{
                    surveyResultsReportToPopulate.add(sentDate);
                }

                String completedDate = "";
                if (row.getSurveyCompletedDate()!=null){
                    completedDate = row.getSurveyCompletedDate().toString();
                    surveyResultsReportToPopulate.add(date.parse( completedDate ));
                }else{
                    surveyResultsReportToPopulate.add(completedDate);
                }
                    
                if (row.getSurveySentDate()!=null){
                    surveyResultsReportToPopulate.add(row.getTimeInterval());
                } else {
                    surveyResultsReportToPopulate.add("");
                }
                
                surveyResultsReportToPopulate.add(row.getSurveySource());
                String surveySourceId = row.getSurveySourceId();
                if(surveySourceId != null && !surveySourceId.equals(null) && !surveySourceId.equals("null")){
                    surveyResultsReportToPopulate.add(surveySourceId);
                }else{
                    surveyResultsReportToPopulate.add(""); 
                }
                surveyResultsReportToPopulate.add(row.getSurveyScore());
                int responseNumber = 0;
                if(row.getSurveyResponseList() != null && !row.getSurveyResponseList().isEmpty()){
                    responseNumber = row.getSurveyResponseList().size();
                }
                //where the answer is null
                if(responseNumber > 0){
                    int iterateAns = 0;
                    while ( iterateAns < responseNumber) {
                        String answer = row.getSurveyResponseList().get(iterateAns++).getAnswer();
                        if(answer != null && !answer.equals(null) && !answer.equals("null")){
                            surveyResultsReportToPopulate.add(answer);
                        }else{
                            surveyResultsReportToPopulate.add(""); 
                        }
                    }
                        
                }
                //where the responses is lesser then the max Questions 
                if(responseNumber < maxQuestions){
                    int questionDiff = maxQuestions - responseNumber ;
                    for(int questionDiffLoop=0;questionDiffLoop < questionDiff;questionDiffLoop++){
                        surveyResultsReportToPopulate.add("");
                    }
                }
                surveyResultsReportToPopulate.add(row.getGateway());
                surveyResultsReportToPopulate.add(row.getCustomerComments());
                surveyResultsReportToPopulate.add(row.getAgreedToShare());
                surveyResultsReportToPopulate.add(row.getBranchName());
                surveyResultsReportToPopulate.add(row.getClickTroughForCompany());
                surveyResultsReportToPopulate.add(row.getClickTroughForAgent());
                surveyResultsReportToPopulate.add(row.getClickTroughForRegion());
                surveyResultsReportToPopulate.add(row.getClickTroughForBranch());
                
                surveyResultsReportData.put(++surveyResultReportCounter ,surveyResultsReportToPopulate );
                surveyResultsReportToPopulate = new ArrayList<>();
            }         
        return surveyResultsReportData;
        
    }
    
    public Map<Integer, List<Object>> writeSurveyResultsCompanyReportHeader( int maxQuestions )
    {
        Map<Integer, List<Object>>  surveyResultsCompanyData = new TreeMap<>();
        List<Object> surveyResultsCompanyReportToPopulate = new ArrayList<>();    
        // Setting up user sheet headers
        surveyResultsCompanyReportToPopulate.add( "User First Name" );
        surveyResultsCompanyReportToPopulate.add( "User Last Name" );
        surveyResultsCompanyReportToPopulate.add( "User Email Address" );
        surveyResultsCompanyReportToPopulate.add( "State" );
        surveyResultsCompanyReportToPopulate.add( "City" );
        surveyResultsCompanyReportToPopulate.add( "Customer First Name" );
        surveyResultsCompanyReportToPopulate.add( "Customer Last Name" );
        surveyResultsCompanyReportToPopulate.add( "Customer Email Address" );
        surveyResultsCompanyReportToPopulate.add( "Participant Type" );
        surveyResultsCompanyReportToPopulate.add( "Survey Sent Date" );
        surveyResultsCompanyReportToPopulate.add( "Survey Completed Date" );
        surveyResultsCompanyReportToPopulate.add( "Time Interval" );
        surveyResultsCompanyReportToPopulate.add( "Survey Source" );
        surveyResultsCompanyReportToPopulate.add( "Survey Source ID" );
        surveyResultsCompanyReportToPopulate.add( "Survey Score" );
        for(int surveyQuestions=0; surveyQuestions<maxQuestions; surveyQuestions++){
            surveyResultsCompanyReportToPopulate.add( "Q"+(surveyQuestions+1));
        }
        if(maxQuestions == 0){
            surveyResultsCompanyReportToPopulate.add("Q1");
        }
        surveyResultsCompanyReportToPopulate.add( "Gateway");
        surveyResultsCompanyReportToPopulate.add( "Customer Comments");
        surveyResultsCompanyReportToPopulate.add( "Agreed To Share");
        surveyResultsCompanyReportToPopulate.add( "Branch");
        surveyResultsCompanyReportToPopulate.add( "Click Through for Company");
        surveyResultsCompanyReportToPopulate.add( "Click Through for Agent");
        surveyResultsCompanyReportToPopulate.add( "Click Through for Region");
        surveyResultsCompanyReportToPopulate.add( "Click Through for Branch");
        surveyResultsCompanyData.put( 1, surveyResultsCompanyReportToPopulate );
        
        return surveyResultsCompanyData;
        
    }
    
    public Map<Integer, List<Object>> getCompanyUserReportToBeWrittenInSheet( List<List<String>> companyUser )
    {
     // This data needs to be written (List<Object>)
        Map<Integer, List<Object>>  companyUserData = new TreeMap<>();
        
        Integer companyUserCounter = 2;
        
        List<Object> surveyTransactionReportToPopulate = new ArrayList<>();
        
        for(List<String> row : companyUser ){
            try{
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 0 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 1 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 2 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 3 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 4 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 5 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 6 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 7 ) ));
                if(!row.get(8).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 8 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                if(!row.get(9).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 9 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 10 ) ));
                if(!row.get(11).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 11 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 12 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 13 ) ));
                if(!row.get(14).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 14 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 15 ) ));
                if(!row.get(16).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 16 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 17 ) ));
                if(!row.get(18).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 18 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 19 ) ));
                if(!row.get(20).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 20 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 21 ) ));
                if(!row.get(22).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 22 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 23 ) ));
                if(!row.get(24).isEmpty()){
                    surveyTransactionReportToPopulate.add(REPORTING_DATE_FORMATTER.parse(row.get( 24 ) ));
                }
                else{
                    surveyTransactionReportToPopulate.add("");
                }
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 25 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 26 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 27 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 28 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 29 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 30 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 31 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 32 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 33 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 34 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 35 ) ));
                surveyTransactionReportToPopulate.add(String.valueOf( row.get( 36 ) ));
                surveyTransactionReportToPopulate.add(Integer.valueOf( row.get( 37 ) ));
                surveyTransactionReportToPopulate.add(Integer.valueOf( row.get( 38 ) ));
                surveyTransactionReportToPopulate.add(Integer.valueOf( row.get( 39 ) ));
                surveyTransactionReportToPopulate.add(Integer.valueOf( row.get( 40 ) ));
                surveyTransactionReportToPopulate.add(Integer.valueOf( row.get( 41 ) ));
                if(row.get( 42 ) == null || Integer.valueOf( row.get( 42 ) ) == 0) {
                 
                    surveyTransactionReportToPopulate.add("Opted In");
                }
                else {
                    
                    surveyTransactionReportToPopulate.add("Opted Out");
                }

                companyUserData.put(++companyUserCounter ,surveyTransactionReportToPopulate );
                surveyTransactionReportToPopulate = new ArrayList<>();
            }catch ( ParseException e ) {
               LOG.error( "Parse exception caught in getCompanyUserReportToBeWrittenInSheet {} ",e.getMessage() );
            }
            
        }
        // Setting up user sheet headers
        surveyTransactionReportToPopulate.add("First Name");
        surveyTransactionReportToPopulate.add("Last Name");
        surveyTransactionReportToPopulate.add("Email");
        surveyTransactionReportToPopulate.add("SocialSurvey Access Level");
        surveyTransactionReportToPopulate.add("Office Assignment(s)");
        surveyTransactionReportToPopulate.add("Region Assignment(s)");
        surveyTransactionReportToPopulate.add("Office Admin Privilege(s)");
        surveyTransactionReportToPopulate.add("Region Admin Privilege");
        surveyTransactionReportToPopulate.add("SocialSurvey Invite sent");
        surveyTransactionReportToPopulate.add("Date last invite sent");
        surveyTransactionReportToPopulate.add("Profile Verified");
        surveyTransactionReportToPopulate.add("Date of last log-in");
        surveyTransactionReportToPopulate.add("Profile Complete");
        surveyTransactionReportToPopulate.add("Socially Connected");
        surveyTransactionReportToPopulate.add("Facebook");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("Twitter");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("Linkedin");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("Google");
        surveyTransactionReportToPopulate.add("Zillow");
        surveyTransactionReportToPopulate.add("Yelp");
        surveyTransactionReportToPopulate.add("Realtor");
        surveyTransactionReportToPopulate.add("Google Business");
        surveyTransactionReportToPopulate.add("Lendingtree");
        surveyTransactionReportToPopulate.add("Date Adoption completed");
        surveyTransactionReportToPopulate.add("Date last survey sent");
        surveyTransactionReportToPopulate.add("Date last survey posted");
        surveyTransactionReportToPopulate.add("User Address");
        surveyTransactionReportToPopulate.add("SocialSurvey Profile");
        surveyTransactionReportToPopulate.add("Total Reviews");
        surveyTransactionReportToPopulate.add("SocialSurvey Reviews");
        surveyTransactionReportToPopulate.add("Zillow Reviews");
        surveyTransactionReportToPopulate.add("Abusive Reviews");
        surveyTransactionReportToPopulate.add("3rd Party Reviews");
        surveyTransactionReportToPopulate.add("Account Status");
        companyUserData.put( 1, surveyTransactionReportToPopulate );
        surveyTransactionReportToPopulate = new ArrayList<>();
        
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("(1=Green, 2=Red)");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("(Photo, Company Logo, Title, Location, Industry, Licenses, Disclaimer, About)");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("Date connection established");
        surveyTransactionReportToPopulate.add("Connection Status");
        surveyTransactionReportToPopulate.add("Date of last post");
        surveyTransactionReportToPopulate.add("Profile Url");
        surveyTransactionReportToPopulate.add("Date connection established");
        surveyTransactionReportToPopulate.add("Connection Status");
        surveyTransactionReportToPopulate.add("Date of last post");
        surveyTransactionReportToPopulate.add("Profile Url");
        surveyTransactionReportToPopulate.add("Date connection established");
        surveyTransactionReportToPopulate.add("Connection Status");
        surveyTransactionReportToPopulate.add("Date of last post");
        surveyTransactionReportToPopulate.add("Profile Url");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("(SS + Zillow + 3rd Party Reviews)");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        companyUserData.put( 2, surveyTransactionReportToPopulate );
        
        return companyUserData;
        
    }
    
    public Map<Integer, List<Object>> getSurveyTransactionReportToBeWrittenInSheet( List<SurveyTransactionReportVO> surveyTransactionReport )
    {
     // This data needs to be written (List<Object>)
        Map<Integer, List<Object>>  surveyTransactionData = writeReportHeader(SURVEY_TRANSACTION_REPORT_HEADERS); 
        int surveyTransactionCounter = 2;
        List<Object> surveyTransactionReportToPopulate;
        for(SurveyTransactionReportVO row : surveyTransactionReport ){
        	surveyTransactionReportToPopulate = new ArrayList<>();
            surveyTransactionReportToPopulate.add(row.getUserName());
            surveyTransactionReportToPopulate.add(row.getUserId());
            surveyTransactionReportToPopulate.add(row.getEmailId());
            surveyTransactionReportToPopulate.add( row.getYear()+"_"+row.getMonth());
            surveyTransactionReportToPopulate.add(row.getNmls());
            surveyTransactionReportToPopulate.add(row.getLicenseId());
            surveyTransactionReportToPopulate.add(row.getCompanyName());
            surveyTransactionReportToPopulate.add(row.getRegionName());
            surveyTransactionReportToPopulate.add(row.getBranchName());
            surveyTransactionReportToPopulate.add(row.getTotalReviews());
            surveyTransactionReportToPopulate.add(row.getTotalZillowReviews());
            surveyTransactionReportToPopulate.add(row.getTotal_3rdPartyReviews());
            surveyTransactionReportToPopulate.add(row.getTotalVerifiedCustomerReviews());
            surveyTransactionReportToPopulate.add(row.getTotalUnverifiedCustomerReviews());
            surveyTransactionReportToPopulate.add(row.getTotalSocialSurveyReviews());
            surveyTransactionReportToPopulate.add(row.getTotalAbusiveReviews());
            surveyTransactionReportToPopulate.add(row.getTotalRetakeReviews());
            surveyTransactionReportToPopulate.add(row.getTotalRetakeCompleted());
            surveyTransactionReportToPopulate.add(row.getTransactionReceivedBySource());
            surveyTransactionReportToPopulate.add(row.getTransactionSent());
            surveyTransactionReportToPopulate.add(row.getTransactionUnprocessable());
            surveyTransactionReportToPopulate.add(row.getTransactionClicked());
            surveyTransactionReportToPopulate.add(row.getTransactionCompleted());
            surveyTransactionReportToPopulate.add(row.getTransactionPartiallyCompleted());
            surveyTransactionReportToPopulate.add(row.getTransactionUnopened());
            surveyTransactionReportToPopulate.add(row.getTransactionDuplicates());
            surveyTransactionData.put(surveyTransactionCounter++ ,surveyTransactionReportToPopulate );
        }
       
        return surveyTransactionData;
        
    }
    
    public Map<Integer, List<Object>> getUserRankingReportToBeWrittenInSheet( List<List<String>> userRankingReport )
    {
     // This data needs to be written (List<Object>)
        Map<Integer, List<Object>>  userRankingData = new TreeMap<>();
        
        Integer userRankingCounter = 1;
        
        List<Object> userRankingReportToPopulate = new ArrayList<>();
                
        for(List<String> row : userRankingReport ){
            userRankingReportToPopulate.add(String.valueOf( row.get( 0 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 1 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 2 ) ));
            userRankingReportToPopulate.add(Integer.valueOf( row.get( 3 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 4 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 5 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 6 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 7 ) ));
            userRankingReportToPopulate.add(Integer.valueOf( row.get( 8 ) ));
            userRankingReportToPopulate.add(Double.valueOf( row.get( 9 ) ));
            userRankingReportToPopulate.add(Double.valueOf( row.get( 10 ) ));
            userRankingReportToPopulate.add(Double.valueOf( row.get( 11 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 12 ) ));
            userRankingReportToPopulate.add(Double.valueOf( row.get( 13 ) ));
           
            userRankingData.put(++userRankingCounter ,userRankingReportToPopulate );
            userRankingReportToPopulate = new ArrayList<>();
            
        }
       
        // Setting up user sheet headers
        userRankingReportToPopulate.add( "First Name" );
        userRankingReportToPopulate.add( "Last Name" );
        userRankingReportToPopulate.add( "Email Address" );
        userRankingReportToPopulate.add( "User ID " );
        userRankingReportToPopulate.add( "NMLS " );
        userRankingReportToPopulate.add( "Company Name" );
        userRankingReportToPopulate.add( "Region Name" );
        userRankingReportToPopulate.add( "Branch Name" );
        userRankingReportToPopulate.add( "Total number of SocialSurvey reviews" );
        userRankingReportToPopulate.add( "Average Score of Reviews [Average of Social Reviews" );
        userRankingReportToPopulate.add( "Rank Score" );
        userRankingReportToPopulate.add( "SPS Score");
        userRankingReportToPopulate.add( "Position in the Company");
        userRankingReportToPopulate.add( "Completion percentage");
       

        userRankingData.put( 1, userRankingReportToPopulate );
        
        return userRankingData;
        
    }


	/**
	 * Writes the header for company details report.
	 * @return
	 */
	public Map<Integer, List<Object>> writeCompanyDetailsReportHeader() {
		Map<Integer, List<Object>>  companyDetailsData = new TreeMap<>();
        List<Object> companyDetailsReportToPopulate = new ArrayList<>();    
        // Setting up user sheet headers
        companyDetailsReportToPopulate.add( "Company Name" );
        companyDetailsReportToPopulate.add( "User Count" );
        companyDetailsReportToPopulate.add( "Verified" );
        companyDetailsReportToPopulate.add( "Verified %" );
        companyDetailsReportToPopulate.add( "# of Regions" );
        companyDetailsReportToPopulate.add( "# of Locations" );
        companyDetailsReportToPopulate.add( "Completion rate %" );
        companyDetailsReportToPopulate.add( "Verified GMB" );
        companyDetailsReportToPopulate.add( "Missing GMB" );
        companyDetailsReportToPopulate.add( "Mismatches" );
        companyDetailsReportToPopulate.add( "Missing Photos");
        companyDetailsReportToPopulate.add( "Missing URLs");
        companyDetailsReportToPopulate.add( "Facebook Connects");
        companyDetailsReportToPopulate.add( "Twitter Connects");
        companyDetailsReportToPopulate.add( "LinkedIn Connects");
        companyDetailsData.put( 1, companyDetailsReportToPopulate );
        
        return companyDetailsData;
	}


	/**
	 * Writes the company details report data to excel.
	 * @param companyDetailsReportList
	 * @param enterNext
	 * @return
	 */
	public Map<Integer, List<Object>> getCompanyDetailsReportToBeWrittenInSheet(
			List<CompanyDetailsReport> companyDetailsReportList, int enterNext) {
		Map<Integer, List<Object>> companyDetailsData = new TreeMap<>();
		List<Object> companyDetailsReportToPopulate = null;
		for(CompanyDetailsReport companyDetailsReportData : companyDetailsReportList){
			companyDetailsReportToPopulate = new ArrayList<>();
			// Set data to list.
			companyDetailsReportToPopulate.add(companyDetailsReportData.getCompanyName());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getUserCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getVerifiedUserCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getVerifiedPercent());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getRegionCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getBranchCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getCompletionRate());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getVerifiedGmb());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getMissingGmb());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getMismatchCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getMissingPhotoCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getMissingURLCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getFacebookConnectionCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getTwitterConnectionCount());
			companyDetailsReportToPopulate.add(companyDetailsReportData.getLinkedinConnectionCount());
			// Set the list to the map.
			companyDetailsData.put(enterNext, companyDetailsReportToPopulate);
			enterNext++;
		}
		return companyDetailsData;
	}

    public Map<Integer, List<Object>> writeIncompleteSurveyResultsCompanyReportHeader()
    {
        Map<Integer, List<Object>> incompleteSurveyResultsReportData = new TreeMap<Integer, List<Object>>();
        List<Object> incompleteSurveyResultsReportToPopulate = new ArrayList<Object>();
        // Setting up user sheet headers
        incompleteSurveyResultsReportToPopulate.add( "Agent First Name" );
        incompleteSurveyResultsReportToPopulate.add( "Agent Last Name" );
        incompleteSurveyResultsReportToPopulate.add( "Agent Email Address" );
        incompleteSurveyResultsReportToPopulate.add( "Customer First Name" );
        incompleteSurveyResultsReportToPopulate.add( "Customer Last Name" );
        incompleteSurveyResultsReportToPopulate.add( "Customer Email Address" );
        incompleteSurveyResultsReportToPopulate.add( "Survey Source ID" );
        incompleteSurveyResultsReportToPopulate.add( "Survey Source" );
        incompleteSurveyResultsReportToPopulate.add( "Date Survey Sent" );
        incompleteSurveyResultsReportToPopulate.add( "No of Reminders Sent" );
        incompleteSurveyResultsReportToPopulate.add( "Date Last Reminder Sent" );
        incompleteSurveyResultsReportData.put( 1, incompleteSurveyResultsReportToPopulate );
        return incompleteSurveyResultsReportData;
    }


    public Map<Integer, List<Object>> getIncompleteSurveyResultsReportToBeWrittenInSheet(
        List<ReportingSurveyPreInititation> incompleteSurvey, int dataCounter )
    {
        Map<Integer, List<Object>> incompleteSurveyData = new TreeMap<>();

        Integer incompleteSurveyCounter = dataCounter;
        List<Object> incompleteSurveyResultsReportToPopulate = null;

        for ( ReportingSurveyPreInititation incompleteReportData : incompleteSurvey ) {
            incompleteSurveyResultsReportToPopulate = new ArrayList<Object>();
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getAgentFirstName() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getAgentLastName() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getAgentEmailId() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getCustomerFirstName() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getCustomerLastName() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getCustomerEmailId() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getSurveySourceId() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getSurveySource() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getCreatedOnEst() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getReminderCounts() );
            incompleteSurveyResultsReportToPopulate.add( incompleteReportData.getLastReminderTimeEst() );
            incompleteSurveyData.put( incompleteSurveyCounter, incompleteSurveyResultsReportToPopulate );
            incompleteSurveyCounter++;
        }
        return incompleteSurveyData;
    }
    
    public Map<Integer, List<Object>> getNpsReportWeekToBeWrittenInSheet(
        Map<Integer, List<Object>> data, List<NpsReportWeek> npsReportWeekList )
    {
        Integer npsReportWeekCounter = 2;
        List<Object> npsWeeklyReportToPopulate = null;
        for(NpsReportWeek npsReportWeek : npsReportWeekList) {
            npsWeeklyReportToPopulate = new ArrayList<Object>();
            if(npsReportWeek.getBranchId() == 0 && npsReportWeek.getRegionId() == 0){
                npsWeeklyReportToPopulate.add( npsReportWeek.getCompanyName() );
                npsWeeklyReportToPopulate.add( npsReportWeek.getCompanyId() );
            }
            else if(npsReportWeek.getBranchId() == 0 && npsReportWeek.getRegionId() > 0){
                npsWeeklyReportToPopulate.add( npsReportWeek.getRegionName() );
                npsWeeklyReportToPopulate.add( npsReportWeek.getRegionId() );
            }
            else if(npsReportWeek.getBranchId() > 0 ){
                npsWeeklyReportToPopulate.add( npsReportWeek.getBranchName() );
                npsWeeklyReportToPopulate.add( npsReportWeek.getBranchId() );
            }
            npsWeeklyReportToPopulate.add( npsReportWeek.getNps() );
            npsWeeklyReportToPopulate.add( npsReportWeek.getPreviousWeekNps() );
            npsWeeklyReportToPopulate.add( npsReportWeek.getNpsDelta() );
            npsWeeklyReportToPopulate.add( npsReportWeek.getResponders() );
            npsWeeklyReportToPopulate.add( npsReportWeek.getResponsePercent() );
            npsWeeklyReportToPopulate.add( npsReportWeek.getAvgNpsRating() );
            npsWeeklyReportToPopulate.add( npsReportWeek.getPromotorsPercent() );
            npsWeeklyReportToPopulate.add( npsReportWeek.getDetractorsPercent() );
            data.put( npsReportWeekCounter, npsWeeklyReportToPopulate );
            npsReportWeekCounter++;
        }
        return data;
        
    }
    
    public Map<Integer, List<Object>> getNpsReportMonthToBeWrittenInSheet(
        Map<Integer, List<Object>> data, List<NpsReportMonth> npsReportMonthList )
    {
        Integer npsReportMonthCounter = 2;
        List<Object> npsMonthlyReportToPopulate = null;
        for(NpsReportMonth npsReportMonth : npsReportMonthList) {
            npsMonthlyReportToPopulate = new ArrayList<Object>();
            if(npsReportMonth.getBranchId() == 0 && npsReportMonth.getRegionId() == 0){
                npsMonthlyReportToPopulate.add( npsReportMonth.getCompanyName() );
                npsMonthlyReportToPopulate.add( npsReportMonth.getCompanyId() );
            }
            else if(npsReportMonth.getBranchId() == 0 && npsReportMonth.getRegionId() > 0){
                npsMonthlyReportToPopulate.add( npsReportMonth.getRegionName() );
                npsMonthlyReportToPopulate.add( npsReportMonth.getRegionId() );
            }
            else if(npsReportMonth.getBranchId() > 0){
                npsMonthlyReportToPopulate.add( npsReportMonth.getBranchName() );
                npsMonthlyReportToPopulate.add( npsReportMonth.getBranchId() );
            }
            npsMonthlyReportToPopulate.add( npsReportMonth.getNps() );
            npsMonthlyReportToPopulate.add( npsReportMonth.getPreviousMonthNps() );
            npsMonthlyReportToPopulate.add( npsReportMonth.getNpsDelta() );
            npsMonthlyReportToPopulate.add( npsReportMonth.getResponders() );
            npsMonthlyReportToPopulate.add( npsReportMonth.getResponsePercent() );
            npsMonthlyReportToPopulate.add( npsReportMonth.getAvgNpsRating() );
            npsMonthlyReportToPopulate.add( npsReportMonth.getPromotorsPercent() );
            npsMonthlyReportToPopulate.add( npsReportMonth.getDetractorsPercent() );
            data.put( npsReportMonthCounter, npsMonthlyReportToPopulate );
            npsReportMonthCounter++;
        }
        return data;
        
    }
    
    public Map<Integer, List<Object>> writeNPSWeekReportHeader(int type)
    {
        Map<Integer, List<Object>> npsWeekReportData = new TreeMap<Integer, List<Object>>();
        List<Object> npsWeekReportDataToPopulate = new ArrayList<Object>();
        // Setting up user sheet headers
        npsWeekReportDataToPopulate.add( "Company/Region/Station" );
        npsWeekReportDataToPopulate.add( "Id" );
        npsWeekReportDataToPopulate.add( "NPS" );
        if(type == 1){
            npsWeekReportDataToPopulate.add( "Previous Week NPS" );
        } else if(type == 2){
            npsWeekReportDataToPopulate.add( "Previous Month NPS" );
        }
        npsWeekReportDataToPopulate.add( "NPS Score Δ" );
        npsWeekReportDataToPopulate.add( "Responders" );
        npsWeekReportDataToPopulate.add( "Response %" );
        npsWeekReportDataToPopulate.add( "Avg. NPS Rating" );
        npsWeekReportDataToPopulate.add( "Promotors" );
        npsWeekReportDataToPopulate.add( "Detractors" );
        npsWeekReportData.put( 1, npsWeekReportDataToPopulate );
        return npsWeekReportData;
    }


	public Map<Integer, List<Object>> getBranchRankingReportYearInSheet(
			List<BranchRankingReportYear> branchRankingReportYear) {
		Map<Integer, List<Object>> branchRankingReportData = writeBranchRankingReportHeader();
		int counter = 2;
		List<Object> branchRankingReportToPopulate = null;
		for (BranchRankingReportYear branchRankingReport : branchRankingReportYear) {
			branchRankingReportToPopulate = new ArrayList<Object>();

            branchRankingReportToPopulate
                .add( ( branchRankingReport.getIsEligible() == 1 ) ? branchRankingReport.getRankInCompany() : "NR" );
			branchRankingReportToPopulate.add(branchRankingReport.getBranchId());
			branchRankingReportToPopulate.add(branchRankingReport.getBranchName());
			branchRankingReportToPopulate.add(branchRankingReport.getRegionName());
			branchRankingReportToPopulate.add(branchRankingReport.getUserCount());
			branchRankingReportToPopulate.add(branchRankingReport.getAverageScore());
			branchRankingReportToPopulate.add(branchRankingReport.getRankingScore());
			branchRankingReportToPopulate.add(branchRankingReport.getCompletionPercentage());
			branchRankingReportToPopulate.add(branchRankingReport.getSps());
			branchRankingReportToPopulate.add(getCompleteURL(branchRankingReport.getPublicPageURL()));

			branchRankingReportData.put(counter++, branchRankingReportToPopulate);
		}
		return branchRankingReportData;
	}

	private String getCompleteURL(String publicPageURL) {
		return applicationBaseUrl + CommonConstants.BRANCH_PROFILE_FIXED_URL + publicPageURL;
	}


	public Map<Integer, List<Object>> getBranchRankingReportMonthInSheet(
			List<BranchRankingReportMonth> branchRankingReportMonth) {
		Map<Integer, List<Object>> branchRankingReportData = writeBranchRankingReportHeader();
		int counter = 2;
		List<Object> branchRankingReportToPopulate = null;
		for (BranchRankingReportMonth branchRankingReport : branchRankingReportMonth) {
			branchRankingReportToPopulate = new ArrayList<Object>();

            branchRankingReportToPopulate
                .add( ( branchRankingReport.getIsEligible() == 1 ) ? branchRankingReport.getRankInCompany() : "NR" );
			branchRankingReportToPopulate.add(branchRankingReport.getBranchId());
			branchRankingReportToPopulate.add(branchRankingReport.getBranchName());
			branchRankingReportToPopulate.add(branchRankingReport.getRegionName());
			branchRankingReportToPopulate.add(branchRankingReport.getUserCount());
			branchRankingReportToPopulate.add(branchRankingReport.getAverageScore());
			branchRankingReportToPopulate.add(branchRankingReport.getRankingScore());
			branchRankingReportToPopulate.add(branchRankingReport.getCompletionPercentage());
			branchRankingReportToPopulate.add(branchRankingReport.getSps());
			branchRankingReportToPopulate.add(getCompleteURL(branchRankingReport.getPublicPageURL()));

			branchRankingReportData.put(counter++, branchRankingReportToPopulate);
		}
		return branchRankingReportData;
	}

	private Map<Integer, List<Object>> writeBranchRankingReportHeader() {
		Map<Integer, List<Object>> branchRankingReportData = new TreeMap<Integer, List<Object>>();
		List<Object> branchRankingReportDataToPopulate = new ArrayList<Object>();
		branchRankingReportDataToPopulate.add("Ranking within Company");
		branchRankingReportDataToPopulate.add("Branch ID");
		branchRankingReportDataToPopulate.add("Branch Name");
		branchRankingReportDataToPopulate.add("Region Name");
		branchRankingReportDataToPopulate.add("Number of Users");
		branchRankingReportDataToPopulate.add("Average Score");
		branchRankingReportDataToPopulate.add("Ranking Score");
		branchRankingReportDataToPopulate.add("Completion Percentage");
		branchRankingReportDataToPopulate.add("SPS");
		branchRankingReportDataToPopulate.add("Public Page URL");
		branchRankingReportData.put(1, branchRankingReportDataToPopulate);
		return branchRankingReportData;
	}


	public Map<Integer, List<Object>> getSurveyInvitationEmailReportInSheet(
			List<SurveyInvitationEmailCountMonth> surveyInvitationEmailCountMonth) {
		Map<Integer, List<Object>> surveyInvitationReportData = writeReportHeader(SURVEY_INVITATION_EMAIL_REPORT_HEADER);
		
		List<Object> surveyInvitationMailReportToPopulate = null;
		int counter = 2;
		for (SurveyInvitationEmailCountMonth surveyInvitationEmailMonth : surveyInvitationEmailCountMonth) {
			surveyInvitationMailReportToPopulate = new ArrayList<Object>();

			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getAgentName());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getEmailId());
			//surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getBranchName());
			//surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getRegionName());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getReceived());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getAttempted());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getDelivered());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getBounced());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getDropped());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getDiffered());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getOpened());
			surveyInvitationMailReportToPopulate.add(surveyInvitationEmailMonth.getLinkClicked());

			surveyInvitationReportData.put(counter++, surveyInvitationMailReportToPopulate);
		}
		return surveyInvitationReportData;
	}
	
	/**
	 * This method takes the report headers separated with ',' and writes to the map as the 1st row.
	 * @param headers
	 * @return
	 */
	private Map<Integer, List<Object>> writeReportHeader(String headers){
		Map<Integer, List<Object>> reportDataToPopulate = new TreeMap<Integer, List<Object>>();
		List<Object> headerList = new ArrayList<Object>();
		String[] headerArr = headers.split(",");
		for(String header : headerArr) {
			headerList.add(header);
		}
		reportDataToPopulate.put(1, headerList);
		return reportDataToPopulate;
	}
}
