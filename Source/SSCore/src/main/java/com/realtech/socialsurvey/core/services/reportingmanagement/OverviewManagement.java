package com.realtech.socialsurvey.core.services.reportingmanagement;

import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewCompany;
import com.realtech.socialsurvey.core.entities.OverviewRegion;
import com.realtech.socialsurvey.core.entities.OverviewUser;

public interface OverviewManagement
{
   public OverviewUser fetchOverviewUserDetails( long entityId, String entityType );
   
   public OverviewBranch fetchOverviewBranchDetails( long entityId, String entityType );

   public OverviewRegion fetchOverviewRegionDetails( long entityId, String entityType );

   public OverviewCompany fetchOverviewCompanyDetails( long entityId, String entityType );

}
