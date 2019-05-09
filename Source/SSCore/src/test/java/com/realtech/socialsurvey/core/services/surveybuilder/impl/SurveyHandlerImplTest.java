package com.realtech.socialsurvey.core.services.surveybuilder.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.OngoingStubbing;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.ReviewReply;
import com.realtech.socialsurvey.core.entities.ReviewReplyVO;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.generator.UrlService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;


public class SurveyHandlerImplTest
{
    @Spy
    @InjectMocks
    private SurveyHandlerImpl surveyHandlerImpl;

    @Mock
    private Utils utils;

    @Mock
    private UrlService urlService;

    @Mock
    private SolrSearchService solrSearchService;

    @Mock
    private SurveyDetailsDao surveyDetailsDao;

    @Mock
    private UserDao userDao;


    @Mock
    private UserProfileDao userProfileDao;

    @Mock
    private URLGenerator urlGenerator;

    @Mock
    private EmailFormatHelper emailFormatHelper;

    @Mock
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private OrganizationManagementService organizationManagementService;

    @Mock
    SurveyPreInitiationDao surveyPreInitiationDao;

    @Mock
    ProfileManagementService profileManagementService;

    @Mock
    private EmailServices emailServices;

    private List<SurveyPreInitiation> incompleteSurveyCustomers;

    private User user, user2;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        surveyHandlerImpl = new SurveyHandlerImpl();
        MockitoAnnotations.initMocks( this );
        incompleteSurveyCustomers = new ArrayList<SurveyPreInitiation>();
        incompleteSurveyCustomers.add( new SurveyPreInitiation() );
        user = new User();
        user.setEmailId( "test@test.com" );
        user2 = new User();
        Company company = new Company();
        company.setCompany( "company" );
        user2.setCompany( company );
        user2.setEmailId( "abc@test.com" );
    }


    @After
    public void tearDown() throws Exception
    {}


    //Tests for saveSurveyPreInitiationObject
    @Test ( expected = InvalidInputException.class)
    public void saveSurveyPreInitiationObjectTestSurveyPreInitiationNull() throws InvalidInputException
    {
        surveyHandlerImpl.saveSurveyPreInitiationObject( null );
    }


    //Tests for getSurveyDetailsBySourceIdAndMongoCollection
    @Test
    public void getSurveyDetailsBySourceIdAndMongoCollectionTestIdNull()
    {
        assertEquals( "Test", null, surveyHandlerImpl.getSurveyDetailsBySourceIdAndMongoCollection( null, 0, "test" ) );
    }


    //Tests for sendSurveyRelatedMail
    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestFirstNameNull() throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.storeSPIandSendSurveyInvitationMail( null, "test", "test", "test", null, false, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestFirstNameEmpty() throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.storeSPIandSendSurveyInvitationMail( "", "test", "test", "test", null, false, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestEmailNull() throws InvalidInputException, SolrException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.storeSPIandSendSurveyInvitationMail( "test", "test", null, "test", null, false, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestEmailEmpty() throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.storeSPIandSendSurveyInvitationMail( "test", "test", "", "test", null, false, "test" );
    }


    //Tests for initiateSurveyRequest
    @Test ( expected = InvalidInputException.class)
    public void initiateSurveyRequestTestInvalidId() throws DuplicateSurveyRequestException, InvalidInputException,
        SelfSurveyInitiationException, SolrException, NoRecordsFetchedException, UndeliveredEmailException,
        ProfileNotFoundException
    {
        surveyHandlerImpl.initiateSurveyRequest( 0, "test", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void initiateSurveyRequestTestEmailNull() throws DuplicateSurveyRequestException, InvalidInputException,
        SelfSurveyInitiationException, SolrException, NoRecordsFetchedException, UndeliveredEmailException,
        ProfileNotFoundException
    {
        surveyHandlerImpl.initiateSurveyRequest( 1, null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void initiateSurveyRequestTestEmailEmpty() throws DuplicateSurveyRequestException, InvalidInputException,
        SelfSurveyInitiationException, SolrException, NoRecordsFetchedException, UndeliveredEmailException,
        ProfileNotFoundException
    {
        surveyHandlerImpl.initiateSurveyRequest( 1, "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void initiateSurveyRequestTestFirstNameNullLastNameNull() throws DuplicateSurveyRequestException,
        InvalidInputException, SelfSurveyInitiationException, SolrException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.initiateSurveyRequest( 1, "test@test.com", null, null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void initiateSurveyRequestTestFirstNameEmptyLastNameEmpty() throws DuplicateSurveyRequestException,
        InvalidInputException, SelfSurveyInitiationException, SolrException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.initiateSurveyRequest( 1, "test@test.com", "", "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void initiateSurveyRequestTestFirstNameNullLastNameEmpty() throws DuplicateSurveyRequestException,
        InvalidInputException, SelfSurveyInitiationException, SolrException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.initiateSurveyRequest( 1, "test@test.com", null, "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void initiateSurveyRequestTestFirstNameEmptyLastNameNull() throws DuplicateSurveyRequestException,
        InvalidInputException, SelfSurveyInitiationException, SolrException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        surveyHandlerImpl.initiateSurveyRequest( 1, "test@test.com", "", null, "test" );
    }


    @Test ( expected = SelfSurveyInitiationException.class)
    public void initiateSurveyRequestTestSelfSurvey() throws DuplicateSurveyRequestException, InvalidInputException,
        SelfSurveyInitiationException, SolrException, NoRecordsFetchedException, UndeliveredEmailException,
        ProfileNotFoundException
    {
        Mockito.when( userManagementService.getUserObjByUserId( Mockito.anyLong() ) ).thenReturn( user );
        Mockito.when( organizationManagementService.validateEmail( Mockito.anyString() ) ).thenReturn( true );
        surveyHandlerImpl.initiateSurveyRequest( 1, "test@test.com", "test", "test", "test" );
    }


    @SuppressWarnings ( "unchecked")
    @Test ( expected = DuplicateSurveyRequestException.class)
    public void initiateSurveyRequestTestCompleteSurveyCustomersExist() throws DuplicateSurveyRequestException,
        InvalidInputException, SelfSurveyInitiationException, SolrException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        Mockito.when( userManagementService.getUserObjByUserId( Mockito.anyLong() ) ).thenReturn( user2 );
        Mockito.when( organizationManagementService.validateEmail( Mockito.anyString() ) ).thenReturn( true );
        Mockito.when( surveyPreInitiationDao.findByKeyValue( Mockito.eq( SurveyPreInitiation.class ), Mockito.anyMap() ) )
            .thenReturn( null );
        Mockito.when(
            surveyDetailsDao.getSurveyByAgentIdAndCustomerEmailAndNoOfDays( Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), 
                Mockito.anyString() , Mockito.anyInt()) ).thenReturn( new SurveyDetails() );
        Mockito.when(
            surveyDetailsDao.getSurveyByAgentIdAndCustomerEmail( Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), 
                Mockito.anyString() ) ).thenReturn( new SurveyDetails() );
        surveyHandlerImpl.initiateSurveyRequest( 1, "test@test.com", "test", "test", "test" );
    }


    /**
     * Commented as Zillow surveys are not stored in database, SS-1276
     * */
//      Tests for deleteZillowSurveysByEntity
//    @Test ( expected = InvalidInputException.class)
//    public void deleteZillowSurveysByEntityTestEntityTypeNull() throws InvalidInputException
//    {
//        surveyHandlerImpl.deleteZillowSurveysByEntity( null, 1 );
//    }
//
//
//    @Test ( expected = InvalidInputException.class)
//    public void deleteZillowSurveysByEntityTestEntityTypeEmpty() throws InvalidInputException
//    {
//        surveyHandlerImpl.deleteZillowSurveysByEntity( "", 1 );
//    }
//
//
//    @Test ( expected = InvalidInputException.class)
//    public void deleteZillowSurveysByEntityTestInvalidId() throws InvalidInputException
//    {
//        surveyHandlerImpl.deleteZillowSurveysByEntity( CommonConstants.COMPANY_ID_COLUMN, -1 );
//    }
//
//
//    Tests for deleteExcessZillowSurveysByEntity
//    @Test ( expected = InvalidInputException.class)
//    public void deleteExcessZillowSurveysByEntityTestEntityTypeNull() throws InvalidInputException
//    {
//        surveyHandlerImpl.deleteExcessZillowSurveysByEntity( null, 1 );
//    }
//
//
//    @Test ( expected = InvalidInputException.class)
//    public void deleteExcessZillowSurveysByEntityTestEntityTypeEmpty() throws InvalidInputException
//    {
//        surveyHandlerImpl.deleteExcessZillowSurveysByEntity( "", 1 );
//    }
//
//
//    @Test ( expected = InvalidInputException.class)
//    public void deleteExcessZillowSurveysByEntityTestInvalidId() throws InvalidInputException
//    {
//        surveyHandlerImpl.deleteExcessZillowSurveysByEntity( CommonConstants.COMPANY_ID_COLUMN, -1 );
//    }


    //Tests for updateModifiedOnColumnForAgentHierachy
    @Test ( expected = InvalidInputException.class)
    public void updateModifiedOnColumnForAgentHierachyTestInvalidId() throws InvalidInputException
    {
        surveyHandlerImpl.updateModifiedOnColumnForAgentHierachy( -1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateModifiedOnColumnForAgentHierachyAgentNull() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        surveyHandlerImpl.updateModifiedOnColumnForAgentHierachy( 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void updateModifiedOnColumnForAgentHierachyCompanyNull() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( new User() );
        surveyHandlerImpl.updateModifiedOnColumnForAgentHierachy( 1 );
    }


    //Tests for canPostOnSocialMedia
    @Test
    public void canPostOnSocialMediaTestCantPost()
    {
        surveyHandlerImpl.canPostOnSocialMedia( new OrganizationUnitSettings(), 0.0 );
    }


    @Test
    public void canPostOnSocialMediaTestCanPost()
    {
        surveyHandlerImpl.canPostOnSocialMedia( new OrganizationUnitSettings(), 5.0 );
    }


    //Tests for validateUnitsettingsForDotloop
    @Test
    public void validateUnitsettingsForDotloopTestSurveyPreinitiationNull()
    {
        assertEquals( "", 1, surveyHandlerImpl.validateUnitsettingsForDotloop( null, null ) );
    }


    @Test
    public void validateUnitsettingsForDotloopTestSurveyPreinitiationCompanyIdNotSame()
    {
        User user = new User();
        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        Company company = new Company();
        company.setCompanyId( 1 );
        user.setCompany( company );
        surveyPreInitiation.setCompanyId( 2 );
        assertEquals( "", 3, surveyHandlerImpl.validateUnitsettingsForDotloop( user, surveyPreInitiation ) );
    }


    @Test
    public void validateUnitsettingsForDotloopTestSurveyPreinitiationCompanyIdSameInvalidCollectionName()
    {
        User user = new User();
        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        Company company = new Company();
        company.setCompanyId( 1 );
        user.setCompany( company );
        surveyPreInitiation.setCompanyId( 1 );
        surveyPreInitiation.setCollectionName( "test" );
        assertEquals( "", 3, surveyHandlerImpl.validateUnitsettingsForDotloop( user, surveyPreInitiation ) );
    }


    @Test
    public void validateUnitsettingsForDotloopTestSurveyPreinitiationCompanyIdSameRegionSettingsCollectionNoUserProfile()
    {
        User user = new User();
        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        Company company = new Company();
        company.setCompanyId( 1 );
        user.setCompany( company );
        surveyPreInitiation.setCompanyId( 1 );
        surveyPreInitiation.setCollectionName( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        assertEquals( "", 3, surveyHandlerImpl.validateUnitsettingsForDotloop( user, surveyPreInitiation ) );
    }


    @Test
    public void validateUnitsettingsForDotloopTestSurveyPreinitiationCompanyIdSameRegionSettingsCollectionValidUserProfile()
    {
        User user = new User();
        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        Company company = new Company();
        company.setCompanyId( 1 );
        user.setCompany( company );
        surveyPreInitiation.setCompanyId( 1 );
        List<UserProfile> profiles = new ArrayList<UserProfile>();
        UserProfile profile = new UserProfile();
        profile.setRegionId( 12 );
        profiles.add( profile );
        user.setUserProfiles( profiles );
        surveyPreInitiation.setCollectionName( MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION );
        assertEquals( "", 3, surveyHandlerImpl.validateUnitsettingsForDotloop( user, surveyPreInitiation ) );
    }


    @Test
    public void validateUnitsettingsForDotloopTestSurveyPreinitiationCompanyIdSameBranchSettingsCollectionValidUserProfile()
    {
        User user = new User();
        SurveyPreInitiation surveyPreInitiation = new SurveyPreInitiation();
        Company company = new Company();
        company.setCompanyId( 1 );
        user.setCompany( company );
        surveyPreInitiation.setCompanyId( 1 );
        List<UserProfile> profiles = new ArrayList<UserProfile>();
        profiles.add( new UserProfile() );
        user.setUserProfiles( profiles );
        surveyPreInitiation.setCollectionName( MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
        assertEquals( "", 1, surveyHandlerImpl.validateUnitsettingsForDotloop( user, surveyPreInitiation ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testMoveSurveysToAnotherUserWithInvalidFromUserId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        surveyHandlerImpl.moveSurveysToAnotherUser( 0l, 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testMoveSurveysToAnotherUserWithInvalidToUserId() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        surveyHandlerImpl.moveSurveysToAnotherUser( 1l, 0l );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testMoveSurveysToAnotherUserWhenUserListIsNull() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        Mockito.when( userDao.getUsersForUserIds( Mockito.anyListOf( Long.class ) ) ).thenReturn( null );
        surveyHandlerImpl.moveSurveysToAnotherUser( 1l, 1l );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testMoveSurveysToAnotherUserWhenUserListIsEmpty() throws InvalidInputException, NoRecordsFetchedException,
        SolrException
    {
        Mockito.when( userDao.getUsersForUserIds( Mockito.anyListOf( Long.class ) ) ).thenReturn( new ArrayList<User>() );
        surveyHandlerImpl.moveSurveysToAnotherUser( 1l, 1l );
    }


    @Test 
    public void testMoveSurveysToAnotherUserWhenFromUserAndToUserDoNotBelongToSameCompany() throws InvalidInputException,
        NoRecordsFetchedException, SolrException
    {
        Company fromUserCompany = new Company();
        fromUserCompany.setCompanyId( 1 );
        Company toUserCompany = new Company();
        toUserCompany.setCompanyId( 2 );

        ProfilesMaster profilesMaster = new ProfilesMaster();
        profilesMaster.setProfileId( 4 );
        UserProfile userProfile = new UserProfile();
        userProfile.setCompany( fromUserCompany );
        userProfile.setStatus( CommonConstants.STATUS_ACTIVE );
        userProfile.setProfilesMaster( profilesMaster );
        
        User fromUser = new User();
        fromUser.setCompany( fromUserCompany );
        fromUser.setUserProfiles( Arrays.asList( new UserProfile[] { userProfile } ) );
        User toUser = new User();
        toUser.setCompany( toUserCompany );
        toUser.setUserProfiles( Arrays.asList( new UserProfile[] { userProfile } ) );

        Mockito.doReturn( new UserProfile() ).when( surveyHandlerImpl ).getUserProfileWhereAgentForUser( fromUser );
        Mockito.when( userDao.getUsersForUserIds( Mockito.anyListOf( Long.class ) ) ).thenReturn(
            Arrays.asList( new User[] { fromUser, toUser } ) );
        surveyHandlerImpl.moveSurveysToAnotherUser( 1l, 1l );
    } 
}
