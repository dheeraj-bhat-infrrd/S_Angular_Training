package com.realtech.socialsurvey.api.controllers;

import com.google.gson.GsonBuilder;
import com.realtech.socialsurvey.core.entities.Notes;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.vo.CompanyStatistics;
import com.realtech.socialsurvey.core.vo.CustomerSuccessInformation;
import com.realtech.socialsurvey.core.vo.UserVo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganizationManagementApiControllerTest
{
    @InjectMocks
    OrganizationManagementApiController organizationManagementApiController;
    
    private MockMvc mockMvc;

    @Mock
    OrganizationManagementService organizationManagementService;
    
    @Mock
    AdminAuthenticationService adminAuthenticationService;

    private static final String dummyHeader= "dummyHeader";

    private static final String authorizationHeader = "authorizationHeader";

    @Before
    public void setup()
    {

        // Process mock annotations
        MockitoAnnotations.initMocks( this );

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup( organizationManagementApiController ).build();

    }
    
    @Test
    public void testStatusOfUpdateCustomerInformationt() throws Exception {
        
        adminAuthenticationService.validateAuthHeader( dummyHeader );
        
        String message = "Successfully updated settings";
        
        Mockito.when( organizationManagementService.updateCustomerInformation( Mockito.anyLong(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyLong() ) ).thenReturn( message );
                
        this.mockMvc.perform( put( "/v1/updatecustomerinformation/{companyId}",
            995 ).param( "key", "tmc" ).param( "value", "dumValue" )
            .param( "modifiedBy", "30369"  ). header( authorizationHeader, dummyHeader ) ).andExpect( status().isOk() );
    }
    
    @Test
    public void testMessageOfUpdateCustomerInformationt() throws Exception {
        
        adminAuthenticationService.validateAuthHeader( dummyHeader );
        
        String message = "Successfully updated settings";
        
        Mockito.when( organizationManagementService.updateCustomerInformation( Mockito.anyLong(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong() ) ).thenReturn( message );
                
        this.mockMvc.perform( put( "/v1/updatecustomerinformation/{companyId}/",
            995 ).param( "key", "tmc" ).param( "value", "dumValue" )
            .param( "modifiedBy", "30369"  ).header( authorizationHeader, dummyHeader ) ).andExpect( content().string( message ) );
    }
    
    @Test
    public void testGetSocialSurveyAdmins() throws Exception {
        
        adminAuthenticationService.validateAuthHeader( dummyHeader );

        List<UserVo> userVoList = new ArrayList<>(  );
        Mockito.when( organizationManagementService.getSocialSurveyAdmins() ).thenReturn( userVoList );
        
        this.mockMvc.perform( get( "/v1/getsocialsurveyadmins" ).header( authorizationHeader, dummyHeader ) ).andExpect( status().isOk() );
    }
    
    @Test
    public void testSizeGetSocialSurveyAdmins() throws Exception {
        
        adminAuthenticationService.validateAuthHeader( dummyHeader );
        
        List<UserVo> userVoList = new ArrayList<>(  );
        Mockito.when( organizationManagementService.getSocialSurveyAdmins() ).thenReturn( userVoList );
        
        this.mockMvc.perform( get( "/v1/getsocialsurveyadmins" ).header( authorizationHeader, dummyHeader ) )
            .andExpect( content().string( "[]" ) );
    }

    @Test
    public void testFetchCompanyStatistics() throws Exception {

        CompanyStatistics companyStatistics = new CompanyStatistics();
        adminAuthenticationService.validateAuthHeader( dummyHeader );
        Mockito.when( organizationManagementService.fetchCompanyStatistics(1l) ).thenReturn( companyStatistics );

        this.mockMvc.perform( get( "/v1/fetchcompanystatistics/{companyId}", 1 )
            .header( "authorizationHeader", "sampleheader" ) )
            .andExpect( content().string( new GsonBuilder().serializeNulls().create().toJson( companyStatistics )))
            .andExpect( status().isOk() );
    }

    @Test
    public void testFetchCompanyStatisticsValidateHeader() throws Exception {

        Mockito.when(adminAuthenticationService.validateAuthHeader( dummyHeader )).thenThrow( AuthorizationException.class);

        this.mockMvc.perform( get( "/v1/fetchcompanystatistics/{companyId}", 1 )
            .header( "authorizationHeader", dummyHeader ) ).andExpect( status().is( 401 ) );
    }

    @Test
    public void testFetchCompanyStatisticsThrowsException() throws Exception {

        adminAuthenticationService.validateAuthHeader( dummyHeader );
        Mockito.when( organizationManagementService.fetchCompanyStatistics(1l) ).thenThrow( Exception.class);

        this.mockMvc.perform( get( "/v1/fetchcompanystatistics/{companyId}", 1 )
            .header( "authorizationHeader", dummyHeader ) ).andExpect( status().is( 500 ) );
    }

    @Test
    public void testFetchCustomerInfo() throws Exception {

        CustomerSuccessInformation customerSuccessInfo = new CustomerSuccessInformation(  );
        adminAuthenticationService.validateAuthHeader( dummyHeader );
        Mockito.when( organizationManagementService.fetchCustomerSuccessInformation(1l) ).thenReturn( customerSuccessInfo );

        this.mockMvc.perform( get( "/v1/fetchcustomersuccessinfo/{companyId}", 1 )
            .header( "authorizationHeader", dummyHeader ) )
            .andExpect( content().string( new GsonBuilder().serializeNulls().create().toJson( customerSuccessInfo )) )
            .andExpect( status().isOk() );
    }

    @Test
    public void testFetchCustomerInfoThrowsAuthException() throws Exception {

        Mockito.when(adminAuthenticationService.validateAuthHeader( dummyHeader )).thenThrow( AuthorizationException.class);

        this.mockMvc.perform( get( "/v1/fetchcustomersuccessinfo/{companyId}", 1 )
            .header( "authorizationHeader", dummyHeader ) ).andExpect( status().isUnauthorized() );
    }

    @Test
    public void testFetchCustomerInfoThrowsException() throws Exception {

        adminAuthenticationService.validateAuthHeader( dummyHeader );
        Mockito.when( organizationManagementService.fetchCustomerSuccessInformation(1l) ).thenThrow( Exception.class);

        this.mockMvc.perform( get( "/v1/fetchcustomersuccessinfo/{companyId}", 1 )
            .header( "authorizationHeader", dummyHeader ) ).andExpect( status().isInternalServerError() );
    }

    @Test
    public void testFetchNotes() throws Exception {

        adminAuthenticationService.validateAuthHeader( dummyHeader );

        List<Notes> notes = new ArrayList<>(  );
        Mockito.when( organizationManagementService.fetchNotes( Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong() ) ).thenReturn( notes );

        this.mockMvc.perform( get( "/v1/fetchnotes/{companyId}/startIndex/{startIndex}/limit/{limit}", 1,0,10 )
            .header( "authorizationHeader", dummyHeader ) ).andExpect( content().string( "[]"  )  ).andExpect( status().isOk() );
    }
}