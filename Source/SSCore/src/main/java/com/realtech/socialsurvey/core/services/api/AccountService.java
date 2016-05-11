package com.realtech.socialsurvey.core.services.api;

import java.util.List;

import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.entities.api.PaymentPlan;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;


public interface AccountService
{
    public void saveAccountRegistrationDetailsAndSetDataInDO( AccountRegistration accountRegistration )
        throws NonFatalException;


    public CompanyProfile getCompanyProfileDetails( int companyId ) throws InvalidInputException;


    public void updateCompanyProfile( int companyId, CompanyProfile companyProfile ) throws InvalidInputException;


    public void deleteCompanyProfileImage( int companyId );


    public void updateCompanyProfileImage( int companyId, String imageUrl );


    public void updateStage( int companyId, String stage );


    public List<VerticalsMaster> getIndustries();


    public List<PaymentPlan> getPaymentPlans();
}
