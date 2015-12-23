package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialPost;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;


public class DashboardServiceImplTest
{

    @InjectMocks
    private DashboardServiceImpl dashboardServiceImpl;

    @Mock
    private SurveyPreInitiationDao surveyPreInitiationDao;

    @Mock
    private CompanyDao companyDao;

    @Mock
    private OrganizationManagementService organizationManagementService;
    
    @Mock
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

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
    public void testGetAllSurveyCountForNullColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getAllSurveyCount( null, 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllSurveyCountForEmptyColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getAllSurveyCount( "", 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetAllSurveyCountForInvalidColumnValue() throws InvalidInputException
    {
        dashboardServiceImpl.getAllSurveyCount( "test", 0l, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCompleteSurveyCountForNullColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getCompleteSurveyCount( null, 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCompleteSurveyCountForEmptyColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getCompleteSurveyCount( "", 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetCompleteSurveyCountForInvalidColumnValue() throws InvalidInputException
    {
        dashboardServiceImpl.getCompleteSurveyCount( "test", 0l, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetClickedSurveyCountForPastNdaysForNullColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getClickedSurveyCountForPastNdays( null, 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetClickedSurveyCountForPastNdaysForEmptyColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getClickedSurveyCountForPastNdays( "", 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetClickedSurveyCountForPastNdaysForInvalidColumnValue() throws InvalidInputException
    {
        dashboardServiceImpl.getClickedSurveyCountForPastNdays( "test", 0l, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSocialPostsForPastNdaysWithHierarchyForNullColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getSocialPostsForPastNdaysWithHierarchy( null, 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSocialPostsForPastNdaysWithHierarchyForEmptyColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getSocialPostsForPastNdaysWithHierarchy( "", 2, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSocialPostsForPastNdaysWithHierarchyForInvalidColumnValue() throws InvalidInputException
    {
        dashboardServiceImpl.getSocialPostsForPastNdaysWithHierarchy( "test", 0l, 5 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSurveyScoreForNullColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getSurveyScore( null, 2, 5, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSurveyScoreForEmptyColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getSurveyScore( "", 2, 5, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSurveyScoreForInvalidColumnValue() throws InvalidInputException
    {
        dashboardServiceImpl.getSurveyScore( "test", 0l, 5, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProfileCompletionPercentageForNullColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getProfileCompletionPercentage( new User(), null, 2, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProfileCompletionPercentageForEmptyColumnName() throws InvalidInputException
    {
        dashboardServiceImpl.getProfileCompletionPercentage( new User(), "", 2, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProfileCompletionPercentageForInvalidColumnValue() throws InvalidInputException
    {
        dashboardServiceImpl.getProfileCompletionPercentage( new User(), "test", 0l, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProfileCompletionPercentageForNullUser() throws InvalidInputException
    {
        dashboardServiceImpl.getProfileCompletionPercentage( null, "test", 5, new OrganizationUnitSettings() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetProfileCompletionPercentageForNullOrganizationUnitSettings() throws InvalidInputException
    {
        dashboardServiceImpl.getProfileCompletionPercentage( new User(), "test", 5, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBadgesForInvalidSurveyScore() throws InvalidInputException
    {
        dashboardServiceImpl.getBadges( -1, 2, 3, 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBadgesForInvalidSurveyCount() throws InvalidInputException
    {
        dashboardServiceImpl.getBadges( 2, -1, 3, 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBadgesForInvalidSocialPosts() throws InvalidInputException
    {
        dashboardServiceImpl.getBadges( 2, 3, -1, 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetBadgesForInvalidProfileCompleteness() throws InvalidInputException
    {
        dashboardServiceImpl.getBadges( 2, 3, 2, -1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSurveyDetailsForGraphForNullColumnName() throws InvalidInputException, ParseException
    {
        dashboardServiceImpl.getSurveyDetailsForGraph( null, 2, 5, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSurveyDetailsForGraphForEmptyColumnName() throws InvalidInputException, ParseException
    {
        dashboardServiceImpl.getSurveyDetailsForGraph( "", 2, 5, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testGetSurveyDetailsForGraphForInvalidColumnValue() throws InvalidInputException, ParseException
    {
        dashboardServiceImpl.getSurveyDetailsForGraph( "test", 0l, 5, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadIncompleteSurveyDataForNullFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadIncompleteSurveyData( new ArrayList<SurveyPreInitiation>(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadIncompleteSurveyDataForEmptyFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadIncompleteSurveyData( new ArrayList<SurveyPreInitiation>(), "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadIncompleteSurveyDataForNullSurveyDetails() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadIncompleteSurveyData( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadSocialMonitorDataForNullFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadSocialMonitorData( new ArrayList<SocialPost>(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadSocialMonitorDataForEmptyFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadSocialMonitorData( new ArrayList<SocialPost>(), "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadSocialMonitorDataForNullSocialPosts() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadSocialMonitorData( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadCustomerSurveyResultsDataForNullFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadCustomerSurveyResultsData( new ArrayList<SurveyDetails>(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadCustomerSurveyResultsDataForEmptyFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadCustomerSurveyResultsData( new ArrayList<SurveyDetails>(), "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadCustomerSurveyResultsDataForNullSocialPosts() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadCustomerSurveyResultsData( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadAgentRankingDataForNullFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadAgentRankingData( new ArrayList<AgentRankingReport>(), null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadAgentRankingDataForEmptyFileName() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadAgentRankingData( new ArrayList<AgentRankingReport>(), "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadAgentRankingDataForNullSocialPosts() throws InvalidInputException, IOException
    {
        dashboardServiceImpl.downloadAgentRankingData( null, "test" );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testAggregateAllSurveysSentForIncompleteSurveysNull()
    {
        @SuppressWarnings ( "rawtypes") Map completeSurveys = new HashMap<Integer, Integer>();
        completeSurveys.put( 1, 1 );
        assertEquals( completeSurveys, dashboardServiceImpl.aggregateAllSurveysSent( null, completeSurveys ) );
    }


    @SuppressWarnings ( "unchecked")
    @Test
    public void testAggregateAllSurveysSentForCompleteSurveysNull()
    {
        @SuppressWarnings ( "rawtypes") Map incompleteSurveys = new HashMap<Integer, Integer>();
        incompleteSurveys.put( 1, 1 );
        assertEquals( incompleteSurveys, dashboardServiceImpl.aggregateAllSurveysSent( incompleteSurveys, null ) );
    }


    @Test
    public void testAggregateAllSurveysSentForCompleteAndIncompleteSurveysNull()
    {
        assertNull( dashboardServiceImpl.aggregateAllSurveysSent( null, null ) );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadUserAdoptionReportDataWithInvalidCompanyId() throws InvalidInputException,
        NoRecordsFetchedException
    {
        dashboardServiceImpl.downloadUserAdoptionReportData( 0 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testDownloadUserAdoptionReportDataWhenRowsIsNull() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( companyDao.getAllUsersAndAdminsUnderACompanyGroupedByBranches( Mockito.anyLong() ) ).thenReturn( null );
        Mockito.when( companyDao.getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches( Mockito.anyLong() ) ).thenReturn(
            new HashMap<Long, Integer>() );
        dashboardServiceImpl.downloadUserAdoptionReportData( 1 );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testDownloadUserAdoptionReportDataWhenRowsIsEmpty() throws InvalidInputException, NoRecordsFetchedException
    {
        Mockito.when( companyDao.getAllUsersAndAdminsUnderACompanyGroupedByBranches( Mockito.anyLong() ) ).thenReturn(
            new ArrayList<Object[]>() );
        Mockito.when( companyDao.getAllActiveUsersAndAdminsUnderACompanyGroupedByBranches( Mockito.anyLong() ) ).thenReturn(
            new HashMap<Long, Integer>() );
        dashboardServiceImpl.downloadUserAdoptionReportData( 1 );
    }
    
    //Test cases for DownloadBillingReport
    @Test ( expected = InvalidInputException.class)
    public void testDownloadBillingReportForInvalidCompanyId() throws InvalidInputException
    {
        dashboardServiceImpl.downloadBillingReport( -1 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadBillingReportForInvalidCompany() throws InvalidInputException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( null );
        dashboardServiceImpl.downloadBillingReport( 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadBillingReportForInvalidCompanySettings() throws InvalidInputException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( null );
        dashboardServiceImpl.downloadBillingReport( 2 );
    }


    @Test ( expected = InvalidInputException.class)
    public void testDownloadBillingReportForInvalidAgentSettings() throws InvalidInputException
    {
        Mockito.when( companyDao.findById( Mockito.eq( Company.class ), Mockito.anyLong() ) ).thenReturn( new Company() );
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( null );
        Mockito.when( companyDao.getAllUsersInCompanyForBillingReport( Mockito.anyLong() ) ).thenReturn(
            new ArrayList<BillingReportData>() );
        Mockito.when( organizationUnitSettingsDao.fetchAgentSettingsById( Mockito.anyLong() ) ).thenReturn( null );
        dashboardServiceImpl.downloadBillingReport( 2 );
    }
}
