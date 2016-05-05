package com.realtech.socialsurvey.core.services.api.impl;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.services.api.AccountService;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class AccountServiceImpl implements AccountService
{
    @Override
    public void saveAccountRegistrationDetailsAndSetDataInDO( AccountRegistration accountRegistration )
    {
        // TODO Auto-generated method stub
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
}
