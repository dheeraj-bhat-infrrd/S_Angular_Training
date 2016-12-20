package com.realtech.socialsurvey.core.services.organizationmanagement.impl;

import java.io.IOException;

import org.junit.Test;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VendastaProductSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


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


    @Test ( expected = InvalidInputException.class)
    public void testValidateUrlGeneratorWithNullValues() throws InvalidInputException, IOException
    {
        vendastaManagementServiceImpl.validateUrlGenerator( null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void testValidateUrlGeneratorWithEmptyValues() throws InvalidInputException, IOException
    {
        vendastaManagementServiceImpl.validateUrlGenerator( new User(), TestConstants.TEST_STRING,
            TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING );
    }


    @Test
    public void testValidateSSOTupleWithNullValues()
    {
        vendastaManagementServiceImpl.validateSSOTuple( null, null, null );
    }


    @Test ( expected = NoRecordsFetchedException.class)
    public void testFetchSSOTokenForReputationManagementAccountWithNullAndInvalidValues()
        throws InvalidInputException, NoRecordsFetchedException
    {
        vendastaManagementServiceImpl.fetchSSOTokenForReputationManagementAccount( null, TestConstants.TEST_LONG, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void TestGetUnitSettingsForAHierarchyWithNullAndInvalidValues()
        throws InvalidInputException, NoRecordsFetchedException
    {
        vendastaManagementServiceImpl.getUnitSettingsForAHierarchy( null, TestConstants.TEST_LONG );
    }


    @Test ( expected = InvalidInputException.class)
    public void TestGetgetSSOTicketByIdWithInvalidValues() throws InvalidInputException
    {
        vendastaManagementServiceImpl.getSSOTicketById( TestConstants.TEST_LONG );
    }

}
