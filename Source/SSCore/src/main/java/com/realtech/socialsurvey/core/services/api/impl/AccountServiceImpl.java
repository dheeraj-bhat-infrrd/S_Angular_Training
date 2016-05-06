package com.realtech.socialsurvey.core.services.api.impl;

import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.services.api.AccountService;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService
{
    // Validation of the object is expected by the calller of the method.
    @Override
    public void saveAccountRegistrationDetailsAndSetDataInDO( AccountRegistration accountRegistration )
    {
        // TODO Auto-generated method stub
        // validate if the email address is not taken already.
        // Create a company with registration stage as 1. Insert into mongo with status 'I'
        // Create a user in user table with registration stage as 1 and status 1, solr, mongo with status 'I'. Set the force password column to 1.
        // Create user profile with 'CA' (1)
        // Send registration email to user.
        // Send mail to sales lead, maybe to support
    }


    @Override
    public CompanyProfile getCompanyProfileDetails( int parseInt )
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void updateCompanyProfile( int companyId, CompanyProfile companyProfile )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void deleteCompanyProfileImage( int companyId )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateCompanyProfileImage( int companyId, String imageUrl )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateStage( int parseInt, String stage )
    {
        // TODO Auto-generated method stub

    }
}
