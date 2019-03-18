package com.realtech.socialsurvey.core.services.reportingmanagement.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyUserReportDao;
import com.realtech.socialsurvey.core.entities.CompanyUserReport;

public class ReportingDashboardManagementImplTest
{

    @SuppressWarnings ( "rawtypes")
    @Spy
    @InjectMocks
    private ReportingDashboardManagementImpl reportingDashboardManagement;
    
    @Mock
    private CompanyUserReportDao companyUserReportDao;
    
    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
    }
    
    @Test
    public void testGetCompanyUserReportEmpty() {        
        
        List<CompanyUserReport> companyUserReports = new ArrayList<CompanyUserReport>();
        
        Mockito.when( companyUserReportDao.fetchCompanyUserReportByCompanyId( Mockito.anyLong() ) ).thenReturn( companyUserReports );
        
        assertEquals( 0, reportingDashboardManagement.getCompanyUserReport( 1l, CommonConstants.COMPANY_ID_COLUMN ).size() );
    }
    
    @Test
    public void testGetCompanyUserReportInvalidEntityType() {        
        
        assertEquals( 0, reportingDashboardManagement.getCompanyUserReport( 1l, CommonConstants.AGENT_ID_COLUMN ).size() );
    }
}
