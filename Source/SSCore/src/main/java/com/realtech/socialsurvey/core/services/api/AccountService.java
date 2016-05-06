package com.realtech.socialsurvey.core.services.api;

import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;


public interface AccountService
{
    public void saveAccountRegistrationDetailsAndSetDataInDO( AccountRegistration accountRegistration );


    public CompanyProfile getCompanyProfileDetails( int parseInt );


    public void updateCompanyProfile( int companyId, CompanyProfile companyProfile );


    public void deleteCompanyProfileImage( int companyId );


    public void updateCompanyProfileImage( int companyId, String imageUrl );


    public void updateStage( int parseInt, String stage );
}
