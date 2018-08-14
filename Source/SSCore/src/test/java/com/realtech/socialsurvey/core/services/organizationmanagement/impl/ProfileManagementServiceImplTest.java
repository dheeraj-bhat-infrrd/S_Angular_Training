package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.UnavailableException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SocialPostDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.Achievement;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Association;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyPositions;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.LockSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.MiscValues;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.SocialMediaTokens;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.ZillowToken;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.integration.zillow.ZillowIntergrationApiBuilder;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public class ProfileManagementServiceImplTest
{
    @Spy
    @InjectMocks
    private ProfileManagementServiceImpl profileManagementServiceImpl;

    @Mock
    private OrganizationManagementService organizationManagementService;
    
    @Mock
    private UserManagementService userManagementService;

    @Mock
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Mock
    private Utils utils;

    @Mock
    private GenericDao<User, Long> userDao;

    @Mock
    private GenericDao<Region, Long> regionDao;

    @Mock
    private URLGenerator urlGenerator;

    @Mock
    private SocialPostDao socialPostDao;

    @Mock
    private GenericDao<Company, Long> companyDao;

    @Mock
    private BranchDao branchDao;

    @Mock
    private ZillowIntergrationApiBuilder zillowIntegrationApiBuilder;

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
    public void testAggregateParentLockSettingsWithNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.aggregateParentLockSettings( null, AccountType.FREE, new UserSettings(), 0, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateParentLockSettingsWithNullAccountType() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.aggregateParentLockSettings( new User(), null, new UserSettings(), 0, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateParentLockSettingsWithNullUserSettings() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.aggregateParentLockSettings( new User(), null, new UserSettings(), 0, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateUserProfileWithNullUser() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.aggregateUserProfile( null, AccountType.FREE, new UserSettings(), 0, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateUserProfileWithNullAccountType() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.aggregateUserProfile( new User(), null, new UserSettings(), 0, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateUserProfileWithNullUserSettings() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.aggregateUserProfile( new User(), null, new UserSettings(), 0, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLogoWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLogo( null, new OrganizationUnitSettings(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLogoWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLogo( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLogoWithNullCompanySettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLogo( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLogoWithNullLogo() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLogo( null, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLogoWithEmptyLogo() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLogo( TestConstants.TEST_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileImageWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateProfileImage( null, new OrganizationUnitSettings(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileImageWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateProfileImage( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileImageWithNullCompanySettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateProfileImage( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileImageWithNullImage() throws InvalidInputException
    {
        profileManagementServiceImpl.updateProfileImage( null, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateProfileImageWithEmptyImage() throws InvalidInputException
    {
        profileManagementServiceImpl.updateProfileImage( TestConstants.TEST_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVerticalWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateVertical( null, new OrganizationUnitSettings(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVerticalWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateVertical( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVerticalWithNullOrganizationSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateVertical( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVerticalWithNullVertical() throws InvalidInputException
    {
        profileManagementServiceImpl.updateVertical( null, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVerticalWithEmptyVertical() throws InvalidInputException
    {
        profileManagementServiceImpl.updateVertical( TestConstants.TEST_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVerticalWhenVerticalMasterListIsEmpty() throws InvalidInputException
    {
        Mockito.when( organizationManagementService.getAllVerticalsMaster() ).thenReturn( new ArrayList<VerticalsMaster>() );
        profileManagementServiceImpl.updateVertical( MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION,
            new OrganizationUnitSettings(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAssociationsWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAssociations( null, new OrganizationUnitSettings(), Arrays.asList( new Association() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAssociationsWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAssociations( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            Arrays.asList( new Association() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAssociationsWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.addAssociations( TestConstants.TEST_STRING, null, Arrays.asList( new Association() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAssociationsWithNullAssociations() throws InvalidInputException
    {
        profileManagementServiceImpl.addAssociations( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAssociationsWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAssociations( null, new AgentSettings(), Arrays.asList( new Association() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAssociationsWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAssociations( TestConstants.TEST_EMPTY_STRING, new AgentSettings(),
            Arrays.asList( new Association() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAssociationsWithNullAgentSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAssociations( TestConstants.TEST_STRING, null, Arrays.asList( new Association() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAssociationsWithNullAssociations() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAssociations( TestConstants.TEST_STRING, new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLockSettingsWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLockSettings( null, new OrganizationUnitSettings(), new LockSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLockSettingsWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLockSettings( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            new LockSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLockSettingsWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLockSettings( TestConstants.TEST_STRING, null, new LockSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLockSettingsWithNullLockSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateLockSettings( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateContactDetailsWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateContactDetails( null, new OrganizationUnitSettings(), new ContactDetailsSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateContactDetailsWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateContactDetails( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            new ContactDetailsSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateContactDetailsWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateContactDetails( TestConstants.TEST_STRING, null, new ContactDetailsSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateContactDetailsWithNullContactDetailsSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateContactDetails( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentContactDetailsWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentContactDetails( null, new AgentSettings(), new ContactDetailsSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentContactDetailsWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentContactDetails( TestConstants.TEST_EMPTY_STRING, new AgentSettings(),
            new ContactDetailsSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentContactDetailsWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentContactDetails( TestConstants.TEST_STRING, null, new ContactDetailsSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentContactDetailsWithNullContactDetailsSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentContactDetails( TestConstants.TEST_STRING, new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAchievementsWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAchievements( null, new OrganizationUnitSettings(), Arrays.asList( new Achievement() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAchievementsWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAchievements( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            Arrays.asList( new Achievement() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAchievementsWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.addAchievements( TestConstants.TEST_STRING, null, Arrays.asList( new Achievement() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAchievementsWithNullAchievements() throws InvalidInputException
    {
        profileManagementServiceImpl.addAchievements( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAchievementsWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAchievements( null, new AgentSettings(), Arrays.asList( new Achievement() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAchievementsWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAchievements( TestConstants.TEST_EMPTY_STRING, new AgentSettings(),
            Arrays.asList( new Achievement() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAchievementsWithNullAgentSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAchievements( TestConstants.TEST_STRING, null, Arrays.asList( new Achievement() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentAchievementsWithNullAssociations() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentAchievements( TestConstants.TEST_STRING, new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddLicencesWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addLicences( null, new OrganizationUnitSettings(), Arrays.asList( new String() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddLicencesWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addLicences( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            Arrays.asList( new String() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddLicencesWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.addLicences( TestConstants.TEST_STRING, null, Arrays.asList( new String() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddLicencesWithNullAuthorisedIn() throws InvalidInputException
    {
        profileManagementServiceImpl.addLicences( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentLicencesWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentLicences( null, new AgentSettings(), Arrays.asList( new String() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentLicencesWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentLicences( TestConstants.TEST_EMPTY_STRING, new AgentSettings(),
            Arrays.asList( new String() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentLicencesWithNullAgentSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentLicences( TestConstants.TEST_STRING, null, Arrays.asList( new String() ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAddAgentLicencesWithNullAuthorisedIn() throws InvalidInputException
    {
        profileManagementServiceImpl.addAgentLicences( TestConstants.TEST_STRING, new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialMediaTokensWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateSocialMediaTokens( null, new OrganizationUnitSettings(), new SocialMediaTokens() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialMediaTokensWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateSocialMediaTokens( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            new SocialMediaTokens() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialMediaTokensWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateSocialMediaTokens( TestConstants.TEST_STRING, null, new SocialMediaTokens() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateSocialMediaTokensWithNullSocialMediaTokens() throws InvalidInputException
    {
        profileManagementServiceImpl.updateSocialMediaTokens( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateDisclaimerWithNullCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateDisclaimer( null, new OrganizationUnitSettings(), TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateDisclaimerWithEmptyCollectionName() throws InvalidInputException
    {
        profileManagementServiceImpl.updateDisclaimer( TestConstants.TEST_EMPTY_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateDisclaimerWithNullUnitSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.updateDisclaimer( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateDisclaimerWithNullDisclaimer() throws InvalidInputException
    {
        profileManagementServiceImpl.updateDisclaimer( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateDisclaimerWithEmptyDisclaimer() throws InvalidInputException
    {
        profileManagementServiceImpl.updateDisclaimer( TestConstants.TEST_STRING, new OrganizationUnitSettings(),
            TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualsForBranchWithNullCompanyProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualsForBranch( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualsForBranchWithEmptyCompanyProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualsForBranch( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualsForBranchWithNullBranchProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualsForBranch( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualsForBranchWithEmptyBranchProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualsForBranch( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForRegionWithNullCompanyProfileName() throws InvalidInputException, ProfileNotFoundException,
        NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsForRegion( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForRegionWithEmptyCompanyProfileName() throws InvalidInputException,
        ProfileNotFoundException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsForRegion( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForRegionWithNullRegionProfileName() throws InvalidInputException, ProfileNotFoundException,
        NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsForRegion( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForRegionWithEmptyRegionProfileName() throws InvalidInputException, ProfileNotFoundException,
        NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsForRegion( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForCompanyWithNullCompanyProfileName() throws InvalidInputException,
        ProfileNotFoundException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsForCompany( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForCompanyWithEmptyCompanyProfileName() throws InvalidInputException,
        ProfileNotFoundException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsForCompany( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForCompanyWithInvalidCompanyId() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualsForCompany( 0 );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetRegionByProfileNameWithNullCompanyProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getRegionByProfileName( null, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetRegionByProfileNameWithEmptyCompanyProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getRegionByProfileName( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetRegionByProfileNameWithNullRegionProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getRegionByProfileName( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetRegionByProfileNameWithEmptyRegionProfileName() throws InvalidInputException, ProfileNotFoundException
    {
        profileManagementServiceImpl.getRegionByProfileName( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetRegionByProfileNameWhenRegionSettingsIsNull() throws InvalidInputException, ProfileNotFoundException
    {
        Mockito.when( utils.generateRegionProfileUrl( Mockito.anyString(), Mockito.anyString() ) ).thenReturn(
            TestConstants.TEST_STRING );
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.getRegionByProfileName( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetBranchByProfileNameWhenCompanySettingsIsNull() throws InvalidInputException, ProfileNotFoundException
    {
        Mockito.doReturn( null ).when( profileManagementServiceImpl )
            .getCompanyProfileByProfileName( TestConstants.TEST_STRING );
        profileManagementServiceImpl.getBranchByProfileName( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetBranchByProfileNameWhenBranchSettingsIsNull() throws InvalidInputException, ProfileNotFoundException
    {
        Mockito.doReturn( new OrganizationUnitSettings() ).when( profileManagementServiceImpl )
            .getCompanyProfileByProfileName( TestConstants.TEST_STRING );
        Mockito.when( utils.generateBranchProfileUrl( Mockito.anyString(), Mockito.anyString() ) ).thenReturn(
            TestConstants.TEST_STRING );
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.getBranchByProfileName( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetBranchByProfileNameWhenBranchIsNull() throws InvalidInputException, ProfileNotFoundException
    {
        Mockito.doReturn( new OrganizationUnitSettings() ).when( profileManagementServiceImpl )
            .getCompanyProfileByProfileName( TestConstants.TEST_STRING );
        Mockito.when( utils.generateBranchProfileUrl( Mockito.anyString(), Mockito.anyString() ) ).thenReturn(
            TestConstants.TEST_STRING );
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileUrl( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( new OrganizationUnitSettings() );
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.getBranchByProfileName( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForCompanyNullProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualsForCompany( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsForCompanyEmptyProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualsForCompany( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualsForCompanyWhenCompanySettingsIsNull() throws InvalidInputException,
        NoRecordsFetchedException, ProfileNotFoundException
    {
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.getIndividualsForCompany( TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualByProfileNameNullProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualByProfileName( null );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualByProfileNameEmptyProfileName() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        profileManagementServiceImpl.getIndividualByProfileName( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualByProfileNameWhenAgentSettingsIsNull() throws InvalidInputException,
        NoRecordsFetchedException, ProfileNotFoundException
    {
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.getIndividualByProfileName( TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualByProfileNameWhenUserNotHaveAnyUserProfiles() throws InvalidInputException,
        NoRecordsFetchedException, ProfileNotFoundException
    {
        User user = new User();
        user.setUserProfiles( new ArrayList<UserProfile>() );
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( user );
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( new AgentSettings() );
        profileManagementServiceImpl.getIndividualByProfileName( "test" );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualSettingsByProfileNameNullAgentProfileName() throws ProfileNotFoundException,
        InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualSettingsByProfileName( null );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualSettingsByProfileNameEmptyAgentProfileName() throws ProfileNotFoundException,
        InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualSettingsByProfileName( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetIndividualSettingsByProfileNameWhenAgentProfileSettingsIsNull() throws ProfileNotFoundException,
        InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.getIndividualSettingsByProfileName( TestConstants.TEST_STRING );
    }


    @Test
    public void testAggregateSocialProfilesWithUnitSettingsHavingNullAsSocialMediaTokens() throws InvalidInputException,
        NoRecordsFetchedException
    {
        Region region = new Region();
        region.setCompany( new Company() );
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( region );
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( new OrganizationUnitSettings() );
        assertNull( "Social Media Token returned does not match expected",
            profileManagementServiceImpl.aggregateSocialProfiles( new OrganizationUnitSettings(), CommonConstants.REGION_ID ) );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetUserByProfileNameWithNullAgentProfileName() throws ProfileNotFoundException
    {
        profileManagementServiceImpl.getUserByProfileName( null, false );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetUserByProfileNameWithEmptyAgentProfileName() throws ProfileNotFoundException
    {
        profileManagementServiceImpl.getUserByProfileName( TestConstants.TEST_EMPTY_STRING, false );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetUserByProfileNameWhenAgentSettingsIsNull() throws ProfileNotFoundException
    {
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.getUserByProfileName( TestConstants.TEST_STRING, false );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetUserByProfileNameWhenUserStatusIsInActive() throws ProfileNotFoundException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( new User() );
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( new AgentSettings() );
        profileManagementServiceImpl.getUserByProfileName( TestConstants.TEST_STRING, true );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetCompositeUserObjectByProfileNameWithNullAgentProfileName() throws ProfileNotFoundException
    {
        profileManagementServiceImpl.getCompositeUserObjectByProfileName( null, false );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetCompositeUserObjectByProfileNameWithEmptyAgentProfileName() throws ProfileNotFoundException
    {
        profileManagementServiceImpl.getCompositeUserObjectByProfileName( TestConstants.TEST_EMPTY_STRING, false );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetCompositeUserObjectByProfileNameWhenOrganizationUnitSettingsIsNull() throws ProfileNotFoundException
    {
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.getCompositeUserObjectByProfileName( TestConstants.TEST_STRING, false );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetCompositeUserObjectByProfileNameWhenUserIsNull() throws ProfileNotFoundException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( new AgentSettings() );
        profileManagementServiceImpl.getCompositeUserObjectByProfileName( TestConstants.TEST_STRING, false );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetCompositeUserObjectByProfileNameWithTrueCheckStatusWhenUserStatusIsInactive()
        throws ProfileNotFoundException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( new User() );
        Mockito.when(
            organizationUnitSettingsDao.fetchOrganizationUnitSettingsByProfileName( Mockito.anyString(), Mockito.anyString() ) )
            .thenReturn( new AgentSettings() );
        profileManagementServiceImpl.getCompositeUserObjectByProfileName( TestConstants.TEST_STRING, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsByRegionIdWithInvalidRegionId() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsByRegionId( 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIndividualsByRegionIdForBatchWithInvalidRegionId() throws InvalidInputException,
        NoRecordsFetchedException
    {
        profileManagementServiceImpl.getIndividualsByRegionId( 0, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetReviewsWithInvalidIden() throws InvalidInputException
    {
        profileManagementServiceImpl.getReviews( 0, 0, 0, 0, 0, TestConstants.TEST_STRING, false, null, null, null, null, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAverageRatingsWithInvalidIden() throws InvalidInputException
    {
        profileManagementServiceImpl.getAverageRatings( 0, TestConstants.TEST_STRING, false, false, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetReviewsCountWithInvalidIden() throws InvalidInputException
    {
        profileManagementServiceImpl.getReviewsCount( 0, 0, 0, TestConstants.TEST_STRING, false, false, false, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProListByProfileLevelWithInvalidIden() throws InvalidInputException, SolrException
    {
        profileManagementServiceImpl.getProListByProfileLevel( 0, TestConstants.TEST_STRING, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProListByProfileLevelWithNullProfileLevel() throws InvalidInputException, SolrException
    {
        profileManagementServiceImpl.getProListByProfileLevel( 1, null, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProListByProfileLevelWithEmptyProfileLevel() throws InvalidInputException, SolrException
    {
        profileManagementServiceImpl.getProListByProfileLevel( 1, TestConstants.TEST_EMPTY_STRING, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateEmailVerificationStatusWhenUrlParamsAreNull() throws NonFatalException
    {
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( null );
        profileManagementServiceImpl.updateEmailVerificationStatus( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateEmailVerificationStatusWhenUrlParamsAreEmpty() throws NonFatalException
    {
        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( new HashMap<String, String>() );
        profileManagementServiceImpl.updateEmailVerificationStatus( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateEmailVerificationStatusWhenEmailTypeWorkAndWorkEmailToVerifyIsNull() throws NonFatalException
    {
        ContactDetailsSettings contact_details = new ContactDetailsSettings();
        contact_details.setMail_ids( new MailIdSettings() );
        OrganizationUnitSettings unitSettings = new OrganizationUnitSettings();
        unitSettings.setContact_details( contact_details );

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.EMAIL_TYPE, CommonConstants.EMAIL_TYPE_WORK );
        urlParams.put( CommonConstants.ENTITY_ID_COLUMN, "0" );

        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( urlParams );
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( unitSettings );
        profileManagementServiceImpl.updateEmailVerificationStatus( TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateEmailVerificationStatusWhenEmailTypePersonalAndPersonalEmailToVerifyIsNull() throws NonFatalException
    {
        ContactDetailsSettings contact_details = new ContactDetailsSettings();
        contact_details.setMail_ids( new MailIdSettings() );
        OrganizationUnitSettings unitSettings = new OrganizationUnitSettings();
        unitSettings.setContact_details( contact_details );

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put( CommonConstants.EMAIL_TYPE, CommonConstants.EMAIL_TYPE_PERSONAL );
        urlParams.put( CommonConstants.ENTITY_ID_COLUMN, "0" );

        Mockito.when( urlGenerator.decryptParameters( Mockito.anyString() ) ).thenReturn( urlParams );
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( unitSettings );
        profileManagementServiceImpl.updateEmailVerificationStatus( TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIncompleteSurveyWithInvalidIden() throws InvalidInputException
    {
        profileManagementServiceImpl.getIncompleteSurvey( 0, 0, 0, 0, 0, TestConstants.TEST_STRING, null, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithNullProfileName() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithEmptyProfileName() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithNullMessage() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, null, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithEmptyMessage() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithNullSenderName() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING, null,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithEmptySenderName() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithNullSenderMailId() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, null, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithEmptySenderMailId() throws InvalidInputException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithNullProfileType() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithEmptyProfileType() throws InvalidInputException, NoRecordsFetchedException,
        UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFindProfileMailIdAndSendMailWithInvalidProfileType() throws InvalidInputException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException
    {
        profileManagementServiceImpl.findProfileMailIdAndSendMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteSocialPostWhenSocialPostIsNull() throws InvalidInputException
    {
        Mockito.when( socialPostDao.getPostByMongoObjectId( Mockito.anyString() ) ).thenReturn( null );
        profileManagementServiceImpl.deleteSocialPost( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDeleteSocialPostWhenSocialPostSourceIsNotSocialSurvey() throws InvalidInputException
    {
        SocialPost socialPost = new SocialPost();
        socialPost.setSource( TestConstants.TEST_STRING );
        Mockito.when( socialPostDao.getPostByMongoObjectId( Mockito.anyString() ) ).thenReturn( socialPost );
        profileManagementServiceImpl.deleteSocialPost( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSocialPostsWithNullEntityType() throws InvalidInputException
    {
        profileManagementServiceImpl.getSocialPosts( 0, null, 0, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCumulativeSocialPostsWithNullEntityType() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.getCumulativeSocialPosts( 0, null, 0, 0, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateLinkedInProfileDataWithNullLinkedInProfileData() throws InvalidInputException
    {
        profileManagementServiceImpl
            .updateLinkedInProfileData( TestConstants.TEST_STRING, new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentExpertiseWithNullExpertise() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentExpertise( new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentExpertiseWithEmptyExpertise() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentExpertise( new AgentSettings(), new ArrayList<String>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentHobbiesWithNullHobbies() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentHobbies( new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentHobbiesWithEmptyHobbies() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentHobbies( new AgentSettings(), new ArrayList<String>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentCompanyPositionsWithNullCompanyPositions() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentCompanyPositions( new AgentSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateAgentCompanyPositionsWithEmptyCompanyPositions() throws InvalidInputException
    {
        profileManagementServiceImpl.updateAgentCompanyPositions( new AgentSettings(), new ArrayList<CompanyPositions>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSetAgentProfileUrlForReviewWhenUnitSettingIsNull() throws InvalidInputException
    {
        SurveyDetails surveyDetails = new SurveyDetails();
        surveyDetails.setAgentId( 2 );
        Mockito.when( organizationUnitSettingsDao.fetchOrganizationUnitSettingsById( Mockito.anyLong(), Mockito.anyString() ) )
            .thenReturn( null );
        profileManagementServiceImpl.setAgentProfileUrlForReview( Arrays.asList( new SurveyDetails[] { surveyDetails } ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentReportWithNullColumnName() throws InvalidInputException
    {
        profileManagementServiceImpl.getAgentReport( 0, null, new Date(), new Date(), new Object() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentReportWithEmptyColumnName() throws InvalidInputException
    {
        profileManagementServiceImpl.getAgentReport( 0, TestConstants.TEST_EMPTY_STRING, new Date(), new Date(), new Object() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentReportWithInvalidIden() throws InvalidInputException
    {
        profileManagementServiceImpl.getAgentReport( -1, TestConstants.TEST_STRING, new Date(), new Date(), new Object() );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testIndividualsBreadCrumbWhenUserProfileIsNull() throws InvalidInputException, NoRecordsFetchedException,
        ProfileNotFoundException
    {
        User user = new User();
        user.setUserProfiles( Arrays.asList( new UserProfile[] { new UserProfile() } ) );
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( user );
        profileManagementServiceImpl.getIndividualsBreadCrumb( 1l );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCompanyNameWhenCompanyIsNull() throws InvalidInputException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.updateCompanyName( 1, 1, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateRegionNameWhenRegionIsNull() throws InvalidInputException
    {
        Mockito.when( regionDao.findById( Mockito.eq( Region.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.updateRegionName( 1, 1, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateBranchNameWhenBranchIsNull() throws InvalidInputException
    {
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.updateBranchName( 1, 1, TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateIndividualNameWhenUserIsNull() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.updateIndividualName( 1, 1, TestConstants.TEST_STRING );

    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateIndividualNameWhenNameArrayIsNull() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( new User() );
        profileManagementServiceImpl.updateIndividualName( 1, 1, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateCompanyEmailWhenCompanyIsNull() throws NonFatalException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.updateCompanyEmail( 1, TestConstants.TEST_STRING );

    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateIndividualEmailWhenUserIsNull() throws InvalidInputException
    {
        Mockito.when( userDao.findById( Mockito.eq( User.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.updateIndividualEmail( 1, TestConstants.TEST_STRING );
    }


    @Test
    public void testFindNamesfromProfileNameWithEmptyProfileName()
    {
        Map<String, String> nameSeggregationMap = profileManagementServiceImpl
            .findNamesfromProfileName( TestConstants.TEST_EMPTY_STRING );
        assertEquals( "First name does not match expected", TestConstants.TEST_EMPTY_STRING,
            nameSeggregationMap.get( CommonConstants.PATTERN_FIRST ) );
        assertEquals( "Last name does not match expected", TestConstants.TEST_EMPTY_STRING,
            nameSeggregationMap.get( CommonConstants.PATTERN_LAST ) );
    }


    @Test
    public void testFindNamesfromProfileNameWithNoSpaceNoHyphenInProfileName()
    {
        Map<String, String> nameSeggregationMap = profileManagementServiceImpl.findNamesfromProfileName( "SocialSurvey" );
        assertEquals( "First name does not match expected", "SocialSurvey",
            nameSeggregationMap.get( CommonConstants.PATTERN_FIRST ) );
        assertEquals( "Last name does not match expected", TestConstants.TEST_EMPTY_STRING,
            nameSeggregationMap.get( CommonConstants.PATTERN_LAST ) );
    }


    @Test
    public void testFindNamesfromProfileNameWithSpaceInProfileName()
    {
        Map<String, String> nameSeggregationMap = profileManagementServiceImpl.findNamesfromProfileName( "Social Survey" );
        assertEquals( "First name does not match expected", "Social Survey",
            nameSeggregationMap.get( CommonConstants.PATTERN_FIRST ) );
        assertEquals( "Last name does not match expected", TestConstants.TEST_EMPTY_STRING,
            nameSeggregationMap.get( CommonConstants.PATTERN_LAST ) );
    }


    @Test
    public void testFindNamesfromProfileNameWithHyphenInProfileName()
    {
        Map<String, String> nameSeggregationMap = profileManagementServiceImpl.findNamesfromProfileName( "Social-Survey" );
        assertEquals( "First name does not match expected", "Social", nameSeggregationMap.get( CommonConstants.PATTERN_FIRST ) );
        assertEquals( "Last name does not match expected", "Survey", nameSeggregationMap.get( CommonConstants.PATTERN_LAST ) );
    }


    @Test
    public void testFindNamesfromProfileNameWithSpaceHyphenInProfileName()
    {
        Map<String, String> nameSeggregationMap = profileManagementServiceImpl.findNamesfromProfileName( "Social Survey-i" );
        assertEquals( "First name does not match expected", "Social Survey",
            nameSeggregationMap.get( CommonConstants.PATTERN_FIRST ) );
        assertEquals( "Last name does not match expected", "i", nameSeggregationMap.get( CommonConstants.PATTERN_LAST ) );
    }


    @Test
    public void testFindNamesfromProfileNameWithSpaceHyphenAndDigitAfterHyphenInProfileName()
    {
        Map<String, String> nameSeggregationMap = profileManagementServiceImpl.findNamesfromProfileName( "Social Survey-2" );
        assertEquals( "First name does not match expected", "Social Survey-2",
            nameSeggregationMap.get( CommonConstants.PATTERN_FIRST ) );
        assertEquals( "Last name does not match expected", TestConstants.TEST_EMPTY_STRING,
            nameSeggregationMap.get( CommonConstants.PATTERN_LAST ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateAgentDetailsWithNullProfileSettings() throws InvalidInputException, NoRecordsFetchedException
    {
        profileManagementServiceImpl.aggregateAgentDetails( new User(), null, new LockSettings() );
    }


    @Test
    public void testAggregateAgentDetailsWithProfileSettingsAsAgentSettings() throws InvalidInputException,
        NoRecordsFetchedException
    {
        ProfilesMaster profilesMaster = new ProfilesMaster();
        profilesMaster.setProfileId( CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );
        UserProfile userProfile = new UserProfile();
        userProfile.setProfilesMaster( profilesMaster );
        User user = new User();
        user.setUserProfiles( Arrays.asList( new UserProfile[] {} ) );
        OrganizationUnitSettings entitiySettings = new OrganizationUnitSettings();
        entitiySettings.setContact_details( new ContactDetailsSettings() );
        OrganizationUnitSettings profileSettings = new AgentSettings();
        Mockito.when( organizationManagementService.getCompanySettings( (User) Mockito.any() ) ).thenReturn( entitiySettings );
        OrganizationUnitSettings agentSettings = profileManagementServiceImpl.aggregateAgentDetails( user, profileSettings,
            new LockSettings() );
        assertNotNull( "Unit Settings does not match expected", ( (AgentSettings) agentSettings ).getCompanyProfileData() );
    }


    @Test
    public void testAggregateAgentDetailsWithProfileSettingsAsOrganizationUnitSettings() throws InvalidInputException,
        NoRecordsFetchedException
    {
        ProfilesMaster profilesMaster = new ProfilesMaster();
        profilesMaster.setProfileId( CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID );
        UserProfile userProfile = new UserProfile();
        userProfile.setProfilesMaster( profilesMaster );
        User user = new User();
        user.setUserProfiles( Arrays.asList( new UserProfile[] {} ) );
        OrganizationUnitSettings entitiySettings = new OrganizationUnitSettings();
        entitiySettings.setContact_details( new ContactDetailsSettings() );

        OrganizationUnitSettings profileSettings = new OrganizationUnitSettings();

        Mockito.when( organizationManagementService.getCompanySettings( (User) Mockito.any() ) ).thenReturn( entitiySettings );
        OrganizationUnitSettings unitSettings = profileManagementServiceImpl.aggregateAgentDetails( user, profileSettings,
            new LockSettings() );
        assertSame( "Unit Settings does not match expected", profileSettings, unitSettings );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetHierarchyDetailsByEntityWithEntityTypeRegionIdAndWhenCompanyIsNull() throws InvalidInputException,
        ProfileNotFoundException
    {
        Mockito.when( organizationManagementService.getPrimaryCompanyByRegion( Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.getHierarchyDetailsByEntity( CommonConstants.REGION_ID, TestConstants.TEST_INT );

    }


    @Test ( expected = InvalidInputException.class)
    public void testGetHierarchyDetailsByEntityWithEntityTypeBranchIdAndWhenRegionIsNull() throws InvalidInputException,
        ProfileNotFoundException
    {
        Mockito.when( organizationManagementService.getPrimaryRegionByBranch( Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.getHierarchyDetailsByEntity( CommonConstants.BRANCH_ID, TestConstants.TEST_INT );

    }


    @Test ( expected = InvalidInputException.class)
    public void testGetHierarchyDetailsByEntityWithEntityTypeBranchIdAndWhenRegionHasCompanyAsNull()
        throws InvalidInputException, ProfileNotFoundException
    {
        Mockito.when( organizationManagementService.getPrimaryRegionByBranch( Mockito.anyLong() ) ).thenReturn( new Region() );
        profileManagementServiceImpl.getHierarchyDetailsByEntity( CommonConstants.BRANCH_ID, TestConstants.TEST_INT );

    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetRegionSettingsByProfileNameWithNullRegionProfileName() throws ProfileNotFoundException,
        InvalidInputException
    {
        profileManagementServiceImpl.getRegionSettingsByProfileName( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetBranchSettingsByProfileNameWhenCompanySettingsIsNull() throws ProfileNotFoundException,
        InvalidInputException
    {
        Mockito.doReturn( null ).when( profileManagementServiceImpl ).getCompanyProfileByProfileName( Matchers.anyString() );
        profileManagementServiceImpl.getBranchSettingsByProfileName( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetBranchSettingsByProfileNameWhenBranchSettingsIsNull() throws ProfileNotFoundException,
        InvalidInputException
    {
        Mockito.doReturn( new OrganizationUnitSettings() ).when( profileManagementServiceImpl )
            .getCompanyProfileByProfileName( Matchers.anyString() );
        Mockito.when( utils.generateBranchProfileUrl( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( null );
        profileManagementServiceImpl.getBranchSettingsByProfileName( TestConstants.TEST_STRING, TestConstants.TEST_STRING );
    }


    @Test ( expected = ProfileNotFoundException.class)
    public void testGetRegionProfileByBranchWhenBranchIsNull() throws ProfileNotFoundException
    {
        Mockito.when( branchDao.findById( Mockito.eq( Branch.class ), Mockito.anyLong() ) ).thenReturn( null );
        profileManagementServiceImpl.getRegionProfileByBranch( new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchZillowFeedsWithNullProfile() throws InvalidInputException, UnavailableException
    {
        profileManagementServiceImpl.fetchAndPostZillowFeeds( null, TestConstants.TEST_STRING, 0, false );

    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchZillowFeedsWithNullCollectionName() throws InvalidInputException, UnavailableException
    {
        profileManagementServiceImpl.fetchAndPostZillowFeeds( new OrganizationUnitSettings(), null, 0, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchZillowFeedsWithEmptyCollectionName() throws InvalidInputException, UnavailableException
    {
        profileManagementServiceImpl.fetchAndPostZillowFeeds( new OrganizationUnitSettings(), TestConstants.TEST_EMPTY_STRING,
            0, false );
    }


    @Test
    public void testFetchZillowFeedsWithNullSocialMediaTokens() throws InvalidInputException, UnavailableException
    {
        List<SurveyDetails> zillowReviewList = profileManagementServiceImpl.fetchAndPostZillowFeeds( new OrganizationUnitSettings(),
            TestConstants.TEST_STRING, 0, false );
        assertEquals( "Zillow reviews size does not match expected", 0, zillowReviewList.size() );

    }


    @Test
    public void testFetchZillowFeedsWithNullZillowToken() throws InvalidInputException, UnavailableException
    {
        OrganizationUnitSettings profile = new OrganizationUnitSettings();
        profile.setSocialMediaTokens( new SocialMediaTokens() );
        List<SurveyDetails> zillowReviewList = profileManagementServiceImpl.fetchAndPostZillowFeeds( profile,
            TestConstants.TEST_STRING, 0, false );
        assertEquals( "Zillow reviews size does not match expected", 0, zillowReviewList.size() );

    }


    @Test
    public void testFetchZillowFeedsWithNullZillowScreenName() throws InvalidInputException, UnavailableException
    {
        SocialMediaTokens socialMediaTokens = new SocialMediaTokens();
        socialMediaTokens.setZillowToken( new ZillowToken() );
        OrganizationUnitSettings profile = new OrganizationUnitSettings();
        profile.setSocialMediaTokens( socialMediaTokens );
        List<SurveyDetails> zillowReviewList = profileManagementServiceImpl.fetchAndPostZillowFeeds( profile,
            TestConstants.TEST_STRING, 0, false );
        assertEquals( "Zillow reviews size does not match expected", 0, zillowReviewList.size() );

    }


    @Test
    public void testFetchZillowFeedsWithEmptyZillowScreenName() throws InvalidInputException, UnavailableException
    {
        ZillowToken zillowToken = new ZillowToken();
        zillowToken.setZillowScreenName( TestConstants.TEST_EMPTY_STRING );
        SocialMediaTokens socialMediaTokens = new SocialMediaTokens();
        socialMediaTokens.setZillowToken( zillowToken );
        OrganizationUnitSettings profile = new OrganizationUnitSettings();
        profile.setSocialMediaTokens( socialMediaTokens );
        List<SurveyDetails> zillowReviewList = profileManagementServiceImpl.fetchAndPostZillowFeeds( profile,
            TestConstants.TEST_STRING, 0, false );
        assertEquals( "Zillow reviews size does not match expected", 0, zillowReviewList.size() );

    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateEmailIdInSolrWithNullEmailId() throws NonFatalException
    {
        profileManagementServiceImpl.updateEmailIdInSolr( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateEmailIdInSolrWithEmptyEmailId() throws NonFatalException
    {
        profileManagementServiceImpl.updateEmailIdInSolr( TestConstants.TEST_EMPTY_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateEmailIdInSolrWithInvalidId() throws NonFatalException
    {
        profileManagementServiceImpl.updateEmailIdInSolr( TestConstants.TEST_STRING, 0 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchZillowDataWithNullProfile() throws InvalidInputException, UnavailableException
    {
        profileManagementServiceImpl.fetchAndPostZillowData( null, TestConstants.TEST_STRING, 0, false);
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchZillowDataWithNullCollectionName() throws InvalidInputException, UnavailableException
    {
        profileManagementServiceImpl.fetchAndPostZillowData( new OrganizationUnitSettings(), null, 0, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testFetchZillowDataWithEmptyCollectionName() throws InvalidInputException, UnavailableException
    {
        profileManagementServiceImpl.fetchAndPostZillowData( null, TestConstants.TEST_EMPTY_STRING, 0, false );
    }


    @Test
    public void testGetZillowTotalScoreAndReviewCountForProfileLevelWithNullProfileLevel()
    {
        assertNull( "Heirarchy level to Ids map is not as expected",
            profileManagementServiceImpl.getZillowTotalScoreAndReviewCountForProfileLevel( null, 1 ) );
    }


    @Test
    public void testGetZillowTotalScoreAndReviewCountForProfileLevelWithEmptyProfileLevel()
    {
        assertNull( "Heirarchy level to Ids map is not as expected",
            profileManagementServiceImpl.getZillowTotalScoreAndReviewCountForProfileLevel( TestConstants.TEST_EMPTY_STRING, 1 ) );
    }


    @Test
    public void testGetZillowTotalScoreAndReviewCountForProfileLevelWithInvalidIden()
    {
        assertNull( "Heirarchy level to Ids map is not as expected",
            profileManagementServiceImpl.getZillowTotalScoreAndReviewCountForProfileLevel( TestConstants.TEST_STRING, 0 ) );
    }


    @Test
    public void testGetZillowTotalScoreAndReviewCountForProfileLevelWithInvalidProfileLevel()
    {
        assertNull( "Heirarchy level to Ids map is not as expected",
            profileManagementServiceImpl.getZillowTotalScoreAndReviewCountForProfileLevel( TestConstants.TEST_STRING, 1 ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsTillRegionWithNullCompanySettings() throws InvalidInputException
    {
        profileManagementServiceImpl.lockSettingsTillRegion( null, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testLockSettingsTillBranchWithNullCompanySettings() throws InvalidInputException
    {
        profileManagementServiceImpl.lockSettingsTillBranch( null, new OrganizationUnitSettings(),
            new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateRegionProfileWithNullCompanySettings() throws InvalidInputException
    {
        profileManagementServiceImpl.aggregateRegionProfile( null, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateRegionProfileWithNullRegionSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.aggregateRegionProfile( new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateBranchProfileWithNullCompanySettings() throws InvalidInputException
    {
        profileManagementServiceImpl.aggregateBranchProfile( null, new OrganizationUnitSettings(),
            new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateBranchProfileWithNullBranchSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.aggregateBranchProfile( new OrganizationUnitSettings(), new OrganizationUnitSettings(),
            null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateAgentProfileWithNullCompanySettings() throws InvalidInputException
    {
        profileManagementServiceImpl.aggregateAgentProfile( null, new OrganizationUnitSettings(),
            new OrganizationUnitSettings(), new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testAggregateAgentProfileWithNullAgentSettings() throws InvalidInputException
    {
        profileManagementServiceImpl.aggregateAgentProfile( new OrganizationUnitSettings(), new OrganizationUnitSettings(),
            new OrganizationUnitSettings(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIdenColumnNameFromProfileLevelWithNullProfileLevel() throws InvalidInputException
    {
        profileManagementServiceImpl.getIdenColumnNameFromProfileLevel( null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIdenColumnNameFromProfileLevelWithEmptyProfileLevel() throws InvalidInputException
    {
        profileManagementServiceImpl.getIdenColumnNameFromProfileLevel( TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetIdenColumnNameFromProfileLevelWithInvalidProfileLevel() throws InvalidInputException
    {
        profileManagementServiceImpl.getIdenColumnNameFromProfileLevel( TestConstants.TEST_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentIdsByProfileLevelWithNullProfileLevel() throws InvalidInputException
    {
        profileManagementServiceImpl.getAgentIdsByProfileLevel( null, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentIdsByProfileLevelWithEmptyProfileLevel() throws InvalidInputException
    {
        profileManagementServiceImpl.getAgentIdsByProfileLevel( TestConstants.TEST_EMPTY_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAgentIdsByProfileLevelWithInvalidProfileLevel() throws InvalidInputException
    {
        profileManagementServiceImpl.getAgentIdsByProfileLevel( TestConstants.TEST_STRING, 1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetReviewsForReportsWithInvalidIden() throws InvalidInputException
    {
        profileManagementServiceImpl.getReviewsForReports( 0, 0, 0, 0, 0, "test", false, null, null, null );
    }
    
    
    
    @Test ( expected = InvalidInputException.class)
    public void testgenerateAndSendEmailVerificationRequestLinkToAdminForNullSettings() throws InvalidInputException, UndeliveredEmailException
    {
        profileManagementServiceImpl.generateAndSendEmailVerificationRequestLinkToAdmin( new ArrayList<MiscValues>(), 1, "test", null );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testgenerateAndSendEmailVerificationRequestLinkToAdminForInvalidCompany() throws InvalidInputException, UndeliveredEmailException
    {
        Mockito.when( userManagementService.getCompanyAdmin(Mockito.anyLong()) ).thenReturn( null );
        profileManagementServiceImpl.generateAndSendEmailVerificationRequestLinkToAdmin( new ArrayList<MiscValues>(), 1, "test", new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
      public void testFetchAndSaveNmlsIdWithNullProfile() throws InvalidInputException, UnavailableException
      {
          profileManagementServiceImpl.fetchAndSaveNmlsId( null, TestConstants.TEST_STRING, 0, false, false );

      }


      @Test ( expected = InvalidInputException.class)
      public void testFetchAndSaveNmlsIdWithNullCollectionName() throws InvalidInputException, UnavailableException
      {
          profileManagementServiceImpl.fetchAndSaveNmlsId( new OrganizationUnitSettings(), null, 0, false, false );
      }


      @Test ( expected = InvalidInputException.class)
      public void testFetchAndSaveNmlsIdWithEmptyCollectionName() throws InvalidInputException, UnavailableException
      {
          profileManagementServiceImpl.fetchAndSaveNmlsId( new OrganizationUnitSettings(), TestConstants.TEST_EMPTY_STRING,
              0, false, false );
      }


}
