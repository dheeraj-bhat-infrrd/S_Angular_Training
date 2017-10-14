package com.realtech.socialsurvey.core.workbook.utils;

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
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyCompanyMapping;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveyResultsCompanyReport;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;
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
            userAdoptionReportToPopulate.add(String.valueOf( row.get( 3 ) ));
            userAdoptionReportToPopulate.add(String.valueOf( row.get( 4 ) ));
            userAdoptionReportToPopulate.add(String.valueOf( row.get( 5 ) ));
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
    
    public Map<Integer, List<Object>> getSurveyResultsCompanyReportToBeWrittenInSheet( Map<String, SurveyResultsCompanyReport> surveyResultsCompanyReport , int maxQuestions , int surveyDataCounter)
    {
        Map<Integer, List<Object>>  surveyResultsCompanyData = new TreeMap<>();
        
        Integer surveyResultCompanyCounter = surveyDataCounter;
        
        List<Object> surveyResultsCompanyReportToPopulate = new ArrayList<>();
        for(Entry<String, SurveyResultsCompanyReport> row : surveyResultsCompanyReport.entrySet() ){
                surveyResultsCompanyReportToPopulate.add(row.getValue().getUserFirstName());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getUserLastName());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getCustomerFirstName());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getCustomerLastName());         
                if(row.getValue().getSurveySentDate() != null ){
                    surveyResultsCompanyReportToPopulate.add(row.getValue().getSurveySentDate());
                }else{
                    surveyResultsCompanyReportToPopulate.add(String.valueOf( "" ));
                }
                if(row.getValue().getSurveyCompletedDate() != null){
                    surveyResultsCompanyReportToPopulate.add(row.getValue().getSurveyCompletedDate());
                }else{
                    surveyResultsCompanyReportToPopulate.add(String.valueOf( "" ));
                }       
                surveyResultsCompanyReportToPopulate.add(row.getValue().getTimeInterval());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getSurveySource());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getSurveySourceId());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getSurveyScore());       
                int responseNumber = row.getValue().getSurveyResponseList().size();             
                if(responseNumber > 0){
                    int iterateAns = 0;
                    while ( iterateAns++ < responseNumber) 
                        surveyResultsCompanyReportToPopulate.add(row.getValue().getSurveyResponseList().get( iterateAns ));
                }else if(responseNumber < maxQuestions){
                    int questionDiff = maxQuestions - responseNumber;
                    for(int questionDiffLoop=0;questionDiffLoop < questionDiff;questionDiffLoop++){
                        surveyResultsCompanyReportToPopulate.add(String.valueOf( "" ));
                    }
                }
                surveyResultsCompanyReportToPopulate.add(row.getValue().getGateway());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getCustomerComments());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getAgreedToShare());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getBranchName());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getClickTroughForCompany());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getClickTroughForAgent());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getClickTroughForRegion());
                surveyResultsCompanyReportToPopulate.add(row.getValue().getClickTroughForBranch());
                
                surveyResultsCompanyData.put(++surveyResultCompanyCounter ,surveyResultsCompanyReportToPopulate );
                surveyResultsCompanyReportToPopulate = new ArrayList<>();
            }         
        return surveyResultsCompanyData;
        
    }
    
    public Map<Integer, List<Object>> writeSurveyResultsCompanyReportHeader( int maxQuestions )
    {
        Map<Integer, List<Object>>  surveyResultsCompanyData = new TreeMap<>();
        List<Object> surveyResultsCompanyReportToPopulate = new ArrayList<>();    
        // Setting up user sheet headers
        surveyResultsCompanyReportToPopulate.add( "User First Name" );
        surveyResultsCompanyReportToPopulate.add( "User Last Name" );
        surveyResultsCompanyReportToPopulate.add( "Customer First Name" );
        surveyResultsCompanyReportToPopulate.add( "Customer Last Name" );
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
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 0 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 1 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 2 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 3 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 4 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 5 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 6 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 7 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 8 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 9 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 10 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 11 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 12 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 13 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 14 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 15 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 16 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 17 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 18 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 19 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 20 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 21 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 22 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 23 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 24 ) ));
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
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 37 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 38 ) ));


            companyUserData.put(++companyUserCounter ,surveyTransactionReportToPopulate );
            surveyTransactionReportToPopulate = new ArrayList<>();
            
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
        surveyTransactionReportToPopulate.add("Twitter");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("");
        surveyTransactionReportToPopulate.add("Linkedin");
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
        surveyTransactionReportToPopulate.add("Date connection established");
        surveyTransactionReportToPopulate.add("Connection Status");
        surveyTransactionReportToPopulate.add("Date of last post");
        surveyTransactionReportToPopulate.add("Date connection established");
        surveyTransactionReportToPopulate.add("Connection Status");
        surveyTransactionReportToPopulate.add("Date of last post");
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
        companyUserData.put( 2, surveyTransactionReportToPopulate );
        
        return companyUserData;
        
    }
    
    public Map<Integer, List<Object>> getSurveyTransactionReportToBeWrittenInSheet( List<List<String>> surveyTransactionReport )
    {
     // This data needs to be written (List<Object>)
        Map<Integer, List<Object>>  surveyTransactionData = new TreeMap<>();
        
        Integer surveyTransactionCounter = 1;
        
        List<Object> surveyTransactionReportToPopulate = new ArrayList<>();
                
        for(List<String> row : surveyTransactionReport ){
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 0 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 1 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 2 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 3 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 4 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 5 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 6 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 7 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 8 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 9 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 10 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 11 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 12 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 13 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 14 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 15 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 16 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 17 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 18 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 19 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 20 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 21 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 22 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 23 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 24 ) ));
            surveyTransactionReportToPopulate.add(String.valueOf( row.get( 25 ) ));


            surveyTransactionData.put(++surveyTransactionCounter ,surveyTransactionReportToPopulate );
            surveyTransactionReportToPopulate = new ArrayList<>();
            
        }
       
        // Setting up user sheet headers
        surveyTransactionReportToPopulate.add( "Name" );
        surveyTransactionReportToPopulate.add( "User ID" );
        surveyTransactionReportToPopulate.add( "TRX_MONTH" );
        surveyTransactionReportToPopulate.add( "NMLS" );
        surveyTransactionReportToPopulate.add( "License ID" );
        surveyTransactionReportToPopulate.add( "Company Name" );
        surveyTransactionReportToPopulate.add( "Region Name" );
        surveyTransactionReportToPopulate.add( "Branch Name" );
        surveyTransactionReportToPopulate.add( "Total number of reviews [Zillow + Social Survey]" );
        surveyTransactionReportToPopulate.add( "Total number of Zillow Reviews" );
        surveyTransactionReportToPopulate.add( "Total number of 3rd party reviews" );
        surveyTransactionReportToPopulate.add( "Total number of verified customer reviews");
        surveyTransactionReportToPopulate.add( "Total number of unverified customer reviews");
        surveyTransactionReportToPopulate.add( "Total number of Social Survey Reviews");
        surveyTransactionReportToPopulate.add( "Total number of Abusive Reviews");
        surveyTransactionReportToPopulate.add( "Total number of Retake Reviews");
        surveyTransactionReportToPopulate.add( "Total number of Retake Completed");
        surveyTransactionReportToPopulate.add( "Transaction Received by Source");
        surveyTransactionReportToPopulate.add( "Transaction Sent (0,1,2,7)");
        surveyTransactionReportToPopulate.add( "Transaction Unprocessable (3,5,8,9,10)" );
        surveyTransactionReportToPopulate.add( "Transaction Clicked [2,7]" );
        surveyTransactionReportToPopulate.add( "Transaction Completed [7]" );
        surveyTransactionReportToPopulate.add( "Transaction Partially Completed" );
        surveyTransactionReportToPopulate.add( "Transaction Unopened [1]" );
        surveyTransactionReportToPopulate.add( "Transaction Duplicates [5]" );
        surveyTransactionReportToPopulate.add( "Transaction Mismatched [3]" );
        surveyTransactionReportToPopulate.add( "Transaction Unassigned [10]" );


        surveyTransactionData.put( 1, surveyTransactionReportToPopulate );
        
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
            userRankingReportToPopulate.add(String.valueOf( row.get( 3 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 4 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 5 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 6 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 7 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 8 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 9 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 10 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 11 ) ));
            userRankingReportToPopulate.add(String.valueOf( row.get( 12 ) ));
           
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
        userRankingReportToPopulate.add( "Total number of reviews [Zillow + Social Survey]" );
        userRankingReportToPopulate.add( "Average Score of Reviews [Average of Social Reviews" );
        userRankingReportToPopulate.add( "Rank Score" );
        userRankingReportToPopulate.add( "SPS Score");
        userRankingReportToPopulate.add( "Position in the Company");
       

        userRankingData.put( 1, userRankingReportToPopulate );
        
        return userRankingData;
        
    }
    
    
}
