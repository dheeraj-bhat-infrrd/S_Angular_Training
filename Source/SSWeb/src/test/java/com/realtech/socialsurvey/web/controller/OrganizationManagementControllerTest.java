package com.realtech.socialsurvey.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

public class OrganizationManagementControllerTest
{
    @InjectMocks
    OrganizationManagementController organizationManagementController;
    
    private MockMvc mockMvc;
    
    @Mock
    SessionHelper sessionHelper;
    
    @Mock
    OrganizationManagementService organizationManagementService;
    
    @Before
    public void setup()
    {

        // Process mock annotations
        MockitoAnnotations.initMocks( this );

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup( organizationManagementController ).build();
    }
    
    @Test
    public void testStoreOptOutTextFlow() throws Exception {
        
        User user = new User();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmailId("userMailID@infrrd.ai");
        Company comp = new Company();
        comp.setCompanyId(995L);
        user.setCompany(comp);
        Mockito.when( sessionHelper.getCurrentUser() ).thenReturn( user );   
        
        String optOutText = "Your Login is currently disabled by your company admin. If you believe you are seeing this message in error, please contact 911";
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        Mockito.when( organizationManagementService.getCompanySettings( user ) ).thenReturn( companySettings );
        
        companySettings.setOptoutText( optOutText );
        organizationManagementService.updateCompanySettings( companySettings, "optoutText", optOutText );
        
        Whitebox.setInternalState( organizationManagementController, "applicationBaseUrl", "test" );
        this.mockMvc.perform( get( "/storeoptouttext" ).param( "text", optOutText ) ).andExpect( content().string( "success" ) );
    }
    
    @Test
    public void testStoreOptOutTextFlowEmpty() throws Exception {
        
        User user = new User();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmailId("userMailID@infrrd.ai");
        Company comp = new Company();
        comp.setCompanyId(995L);
        user.setCompany(comp);
        Mockito.when( sessionHelper.getCurrentUser() ).thenReturn( user );
        
        Whitebox.setInternalState( organizationManagementController, "applicationBaseUrl", "test" );
        this.mockMvc.perform( get( "/storeoptouttext" ) ).andExpect( content().string( "" ) );
    }
    
    @Test
    public void testResetOptOutText() throws Exception {
        
        User user = new User();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmailId("userMailID@infrrd.ai");
        Company comp = new Company();
        comp.setCompanyId(995L);
        user.setCompany(comp);
        Mockito.when( sessionHelper.getCurrentUser() ).thenReturn( user );
        
        String optOutText = "Your Login is currently disabled by your company admin. If you believe you are seeing this message in error, please contact support@socialsurvey.com";
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        companySettings.setOptoutText( optOutText );
        Mockito.when( organizationManagementService.getCompanySettings( user ) ).thenReturn( companySettings );
        
        Whitebox.setInternalState( organizationManagementController, "applicationBaseUrl", "test" );
        this.mockMvc.perform( get( "/resetoptouttext" ) ).andExpect( content().string( "Your Login is currently disabled by your company admin. If you believe you are seeing this message in error, please contact support@socialsurvey.com" ) );
    }
    
    @Test
    public void testSetEnableLoginButtonVisibilityTrue() throws Exception {
        
        User user = new User();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmailId("userMailID@infrrd.ai");
        Company comp = new Company();
        comp.setCompanyId(995L);
        user.setCompany(comp);
        Mockito.when( sessionHelper.getCurrentUser() ).thenReturn( user );
        
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        Mockito.when( organizationManagementService.getCompanySettings( user ) ).thenReturn( companySettings );
        
        companySettings.setIsLoginEnableAllowed( Boolean.TRUE );
        organizationManagementService.updateCompanySettings( companySettings, "isLoginEnableAllowed", companySettings.getIsLoginEnableAllowed().toString() );
        
        Whitebox.setInternalState( organizationManagementController, "applicationBaseUrl", "test" );
        this.mockMvc.perform( post( "/showenableloginbutton" ).param( "isLoginEnabled", "true" ) ).andExpect( content().string( "true" ) );
    }
    
    @Test
    public void testSetEnableLoginButtonVisibilityFalse() throws Exception {
        
        User user = new User();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmailId("userMailID@infrrd.ai");
        Company comp = new Company();
        comp.setCompanyId(995L);
        user.setCompany(comp);
        Mockito.when( sessionHelper.getCurrentUser() ).thenReturn( user );
        
        OrganizationUnitSettings companySettings = new OrganizationUnitSettings();
        Mockito.when( organizationManagementService.getCompanySettings( user ) ).thenReturn( companySettings );
        
        companySettings.setIsLoginEnableAllowed( Boolean.FALSE );
        organizationManagementService.updateCompanySettings( companySettings, "isLoginEnableAllowed", companySettings.getIsLoginEnableAllowed().toString() );
        
        Whitebox.setInternalState( organizationManagementController, "applicationBaseUrl", "test" );
        this.mockMvc.perform( post( "/showenableloginbutton" ).param( "isLoginEnabled", "false" ) ).andExpect( content().string( "false" ) );
    }
}
