package com.realtech.socialsurvey.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;


public class WidgetControllerTest
{
    @InjectMocks
    WidgetController controller;

    private MockMvc mockMvc;

    @Mock
    OrganizationManagementService organizationManagementService;

    @Mock
    ProfileManagementService profileManagementService;


    @Before
    public void setup()
    {

        // Process mock annotations
        MockitoAnnotations.initMocks( this );

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup( controller ).build();

    }


    @Test ( expected = NestedServletException.class)
    public void testFetchWidgetForInvalidProfileType() throws Exception
    {
        this.mockMvc.perform( get( "/widget/{profileType}/{iden}", "test", 0l ) ).andExpect( status().is( 404 ) );
    }


    @Test ( expected = NestedServletException.class)
    public void testFetchWidgetForInvalidIden() throws Exception
    {        
        this.mockMvc.perform( get( "/widget/{profileType}/{iden}", "company", 0l ) ).andExpect( status().is( 404 ) );
    }


    @Test
    public void testFetchWidgetForValidProfileType() throws Exception
    {
        SurveySettings survey = new SurveySettings();
        survey.setAuto_post_score( (float) 5.0 );
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        companySettings.setSurvey_settings( survey );
        companySettings.setProfileUrl( "test" );
        Mockito.when( organizationManagementService.getCompanySettings( Mockito.anyLong() ) ).thenReturn( companySettings );
        Mockito
            .when( profileManagementService.getAverageRatings( Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean() ) )
            .thenReturn( 5.0 );
        Mockito.when(
            profileManagementService.getReviewsCount( Mockito.anyLong(), Mockito.anyDouble(), Mockito.anyDouble(),
                Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean() ) ).thenReturn( 1l );
        Mockito.when(
            profileManagementService.getReviews( Mockito.anyLong(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(), (Date) Mockito.anyObject(),
                (Date) Mockito.anyObject(), Mockito.anyString(), Mockito.anyListOf( String.class ), Mockito.anyString(), Mockito.anyBoolean() ) ).thenReturn( new ArrayList<SurveyDetails>() );
        Whitebox.setInternalState( controller, "applicationBaseUrl", "test" );
        this.mockMvc.perform( get( "/widget/{profileType}/{iden}", "company", 1l ) ).andExpect( status().isOk() )
            .andExpect( model().attribute( "profileLevel", "COMPANY" ) ).andExpect( model().attribute( "averageRating", 5.0 ) )
            .andExpect( model().attribute( "reviewsCount", 1l ) )
            .andExpect( model().attribute( "profileLink", "testpages/companytest" ) )
            .andExpect( forwardedUrl( "widget/widget" ) );
    }
}
