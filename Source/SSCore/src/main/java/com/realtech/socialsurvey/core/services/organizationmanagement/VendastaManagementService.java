package com.realtech.socialsurvey.core.services.organizationmanagement;

import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.VendastaProductSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface VendastaManagementService
{
    public boolean updateVendastaAccess( String collectionName, OrganizationUnitSettings unitSettings )
        throws InvalidInputException;

    public boolean updateVendastaRMSettings( String collectionName, OrganizationUnitSettings unitSettings,
        VendastaProductSettings vendastaReputationManagementSettings ) throws InvalidInputException;
}
