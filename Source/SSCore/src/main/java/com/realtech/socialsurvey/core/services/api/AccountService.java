package com.realtech.socialsurvey.core.services.api;

import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;


/**
 * @author Shipra Goyal, RareMile
 *
 */
public interface AccountService
{
    public void saveAccountRegistrationDetailsAndSetDataInDO( AccountRegistration accountRegistration );


    public CompanyProfile getCompanyProfileDetails( int parseInt );


    public void updateCompanyProfile( int companyId, CompanyProfile companyProfile );


    public void deleteCompanyProfileImage( int companyId );


    public void updateCompanyProfileImage( int companyId, String imageUrl );
}
