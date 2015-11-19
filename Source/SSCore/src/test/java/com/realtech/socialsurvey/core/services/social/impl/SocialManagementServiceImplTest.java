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
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import facebook4j.FacebookException;

public class SocialManagementServiceImplTest
{

    @InjectMocks
    private SocialManagementServiceImpl socialManagementServiceImpl;

    @Mock
    private EmailFormatHelper emailFormatHelper;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }


    @After
    public void tearDown() throws Exception {}


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
            TestConstants.TEST_LONG );
    }


    @Test ( expected = InvalidInputException.class)
    public void testTweetNullAgentSettings() throws InvalidInputException, TwitterException
    {
        socialManagementServiceImpl.tweet( null, TestConstants.TEST_STRING, TestConstants.TEST_LONG );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLinkedinNullAgentSettings() throws NonFatalException
    {
        socialManagementServiceImpl.updateLinkedin( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDisconnectSocialNetworkWithInvalidSocialMedia() throws NonFatalException
    {
        socialManagementServiceImpl.disconnectSocialNetwork( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
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
        socialManagementServiceImpl.postToSocialMedia( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_LONG, TestConstants.TEST_DOUBLE,
            TestConstants.TEST_MAIL_ID_STRING, TestConstants.TEST_STRING, true, TestConstants.TEST_STRING );
    }
}
