package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import org.junit.Test;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.entities.VendastaProductSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public class VendastaManagementServiceImplTest
{

    private VendastaManagementServiceImpl vendastaManagementServiceImpl = new VendastaManagementServiceImpl();


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVendastaAccessWithNullUnitSettings() throws InvalidInputException
    {
        vendastaManagementServiceImpl.updateVendastaAccess( TestConstants.TEST_STRING, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testUpdateVendastaRMSettingsWithNullUnitSettings() throws InvalidInputException
    {
        vendastaManagementServiceImpl.updateVendastaRMSettings( TestConstants.TEST_STRING, null,
            new VendastaProductSettings() );
    }
}
