package com.realtech.socialsurvey.core.services.social.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import twitter4j.TwitterException;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostResponse;
import com.realtech.socialsurvey.core.entities.SocialMediaPostResponseDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;

import facebook4j.FacebookException;


public class SocialManagementServiceImplTest
{

    @InjectMocks
    private SocialManagementServiceImpl socialManagementServiceImpl;

    @Mock
    private EmailFormatHelper emailFormatHelper;
    
    @Mock
    private SurveyHandler surveyHandler;

    @Mock
    private OrganizationManagementService organizationManagementService;

    @Mock
    private UserManagementService userManagementService;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialMediaTokensWithNullSocialMediaTokens() throws InvalidInputException
    {
        socialManagementServiceImpl.updateSocialMediaTokens( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentSocialMediaTokensWithNullSocialMediaTokens() throws InvalidInputException
    {
        socialManagementServiceImpl.updateAgentSocialMediaTokens( new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateStatusIntoFacebookPageWithNullAgentSettings() throws InvalidInputException, FacebookException
    {
        socialManagementServiceImpl.updateStatusIntoFacebookPage( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_LONG, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testTweetNullAgentSettings() throws InvalidInputException, TwitterException
    {
        socialManagementServiceImpl.tweet( null, TestConstants.TEST_STRING, TestConstants.TEST_LONG );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLinkedinNullAgentSettings() throws NonFatalException
    {
        socialManagementServiceImpl.updateLinkedin( null, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, new OrganizationUnitSettings(), false, new AgentSettings(), new SocialMediaPostResponse(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectSocialNetworkWithInvalidSocialMedia() throws NonFatalException
    {
        socialManagementServiceImpl.disconnectSocialNetwork( TestConstants.TEST_STRING, true, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testcheckOrAddZillowLastUpdatedWithNullSocialMediaTokens() throws InvalidInputException
    {
        socialManagementServiceImpl.checkOrAddZillowLastUpdated( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testCheckOrAddZillowLastUpdatedWithNullZillowToken() throws InvalidInputException
    {
        socialManagementServiceImpl.checkOrAddZillowLastUpdated( new SocialMediaTokens() );
    }


    @Test ( expected = NonFatalException.class)
    public void testPostToSocialMediaWithIsAbusiveTrue() throws NonFatalException
    {

        Mockito.when( emailFormatHelper.getCustomerDisplayNameForEmail( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( "" );
        socialManagementServiceImpl.postToSocialMedia( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_LONG, TestConstants.TEST_DOUBLE, TestConstants.TEST_MAIL_ID_STRING,
            TestConstants.TEST_STRING, true, TestConstants.TEST_STRING, true, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistoryEntityTypeNull() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( null, 1l, new SocialMediaTokens(),
            CommonConstants.FACEBOOK_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistoryEntityTypeEmpty() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( "", 1l, new SocialMediaTokens(),
            CommonConstants.FACEBOOK_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistoryEntityIDInvalid() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( CommonConstants.COMPANY_ID, 0l, new SocialMediaTokens(),
            CommonConstants.FACEBOOK_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistoryMediaTokensNull() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( CommonConstants.COMPANY_ID, 1l, null,
            CommonConstants.FACEBOOK_SOCIAL_SITE, CommonConstants.SOCIAL_MEDIA_CONNECTED );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistoryActionNull() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( CommonConstants.COMPANY_ID, 1l, new SocialMediaTokens(),
            CommonConstants.FACEBOOK_SOCIAL_SITE, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistoryActionEmpty() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( CommonConstants.COMPANY_ID, 1l, new SocialMediaTokens(),
            CommonConstants.FACEBOOK_SOCIAL_SITE, "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistorySocialMediaNull() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( CommonConstants.COMPANY_ID, 1l, new SocialMediaTokens(),
            null, CommonConstants.SOCIAL_MEDIA_CONNECTED );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistorySocialMediaEmpty() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( CommonConstants.COMPANY_ID, 1l, new SocialMediaTokens(),
            "", CommonConstants.SOCIAL_MEDIA_CONNECTED );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialConnectionsHistorySocialMediaInvalid() throws InvalidInputException, ProfileNotFoundException
    {
        socialManagementServiceImpl.updateSocialConnectionsHistory( CommonConstants.COMPANY_ID, 1l, new SocialMediaTokens(),
            "test", CommonConstants.SOCIAL_MEDIA_CONNECTED );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForEntityTypeNull() throws InvalidInputException
    {
        socialManagementServiceImpl.disconnectAllSocialConnections( null, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForEntityTypeEmpty() throws InvalidInputException
    {
        socialManagementServiceImpl.disconnectAllSocialConnections( "", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForEntityIdInvalid() throws InvalidInputException
    {
        socialManagementServiceImpl.disconnectAllSocialConnections( "agentId", 0l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForEntityTypeInvalid() throws InvalidInputException
    {
        socialManagementServiceImpl.disconnectAllSocialConnections( "test", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForCompanyUnitSettingsNull() throws InvalidInputException
    {
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( null );
        socialManagementServiceImpl.disconnectAllSocialConnections( "companyId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForRegionUnitSettingsNull() throws InvalidInputException
    {
        Mockito.when( organizationManagementService.getRegionSettings( Mockito.anyLong() ) ).thenReturn( null );
        socialManagementServiceImpl.disconnectAllSocialConnections( "regionId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForBranchUnitSettingsNull() throws InvalidInputException,
        NoRecordsFetchedException
    {
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenReturn( null );
        socialManagementServiceImpl.disconnectAllSocialConnections( "branchId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForBranchUnitSettingsNoRecordsFetched() throws InvalidInputException,
        NoRecordsFetchedException
    {
        Mockito.when( organizationManagementService.getBranchSettingsDefault( Mockito.anyLong() ) ).thenThrow(
            new NoRecordsFetchedException() );
        socialManagementServiceImpl.disconnectAllSocialConnections( "branchId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectAllSocialConnectionsForAgentUnitSettingsNull() throws InvalidInputException
    {
        Mockito.when( userManagementService.getUserSettings( Mockito.anyLong() ) ).thenReturn( null );
        socialManagementServiceImpl.disconnectAllSocialConnections( "agentId", 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSettingsSetStatusByEntityTypeForEntityTypeNull() throws InvalidInputException
    {
        socialManagementServiceImpl.updateSettingsSetStatusByEntityType( null, 1l, SettingsForApplication.FACEBOOK, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSettingsSetStatusByEntityTypeForEntityTypeEmpty() throws InvalidInputException
    {
        socialManagementServiceImpl.updateSettingsSetStatusByEntityType( "", 1l, SettingsForApplication.FACEBOOK, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSettingsSetStatusByEntityTypeForEntityIdInvalid() throws InvalidInputException
    {
        socialManagementServiceImpl.updateSettingsSetStatusByEntityType( "agentId", 0l, SettingsForApplication.FACEBOOK, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveSocialMediaTokensForInvalidUnitSettings() throws InvalidInputException
    {
        socialManagementServiceImpl.removeSocialMediaTokens( null, CommonConstants.AGENT_SETTINGS_COLLECTION );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveSocialMediaTokensForCollectionNull() throws InvalidInputException
    {
        socialManagementServiceImpl.removeSocialMediaTokens( new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testRemoveSocialMediaTokensForCollectionEmpty() throws InvalidInputException
    {
        socialManagementServiceImpl.removeSocialMediaTokens( new OrganizationUnitSettings(), "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testPostToTwitterForHierarchyForNullSMPRD() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.postToTwitterForHierarchy( "test", 5.0, "test2", 2, null, new SocialMediaPostResponseDetails() , false);
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testPostToTwitterForHierarchyForNullSMPD() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.postToTwitterForHierarchy( "test", 5.0, "test2", 2, new SocialMediaPostDetails() , null , false);
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testPostToLinkedInForHierarchyForNullSMPRD() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.postToLinkedInForHierarchy( "test", 5.0, "test2", "test3", 2, null,
            new SocialMediaPostResponseDetails(), new OrganizationUnitSettings(), false , false, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testPostToLinkedInForHierarchyForNullSMPD() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.postToLinkedInForHierarchy( "test", 5.0, "test2", "test3", 2, new SocialMediaPostDetails(),
            null, new OrganizationUnitSettings(), false , false, TestConstants.TEST_STRING );
    }

    
    @Test ( expected = InvalidInputException.class)
    public void testPostToFacebookForHierarchyForNullSMPRD() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.postToFacebookForHierarchy( "test", 5.0, "test2", 2, null, new SocialMediaPostResponseDetails(), false , false, null);
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testPostToFacebookForHierarchyForNullSMPD() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.postToFacebookForHierarchy( "test", 5.0, "test2", 2, new SocialMediaPostDetails() , null, false , false, null);
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testgetUnmatchedPreInitiatedSurveysForInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.getUnmatchedPreInitiatedSurveys( 0, 1, 10 , -1 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testgetProcessedPreInitiatedSurveysForInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.getProcessedPreInitiatedSurveys( 0, 1, 10 , -1 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testgetCorruptPreInitiatedSurveysForInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.getCorruptPreInitiatedSurveys( 0, 1 , 10, -1 );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testupdateAgentIdOfSurveyPreinitiationRecordsForEmailForNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.updateAgentIdOfSurveyPreinitiationRecordsForEmail( null, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testupdateAgentIdOfSurveyPreinitiationRecordsForEmailForNullEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.updateAgentIdOfSurveyPreinitiationRecordsForEmail( new User(), null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testupdateAgentIdOfSurveyPreinitiationRecordsForEmailForEmptyEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.updateAgentIdOfSurveyPreinitiationRecordsForEmail( new User(), "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testupdateSurveyPreinitiationRecordsAsIgnoredForNullEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.updateSurveyPreinitiationRecordsAsIgnored( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testupdateSurveyPreinitiationRecordsAsIgnoredForEmptyEmail() throws InvalidInputException, NoRecordsFetchedException
    {
        socialManagementServiceImpl.updateSurveyPreinitiationRecordsAsIgnored( "" );
    }
    
}
