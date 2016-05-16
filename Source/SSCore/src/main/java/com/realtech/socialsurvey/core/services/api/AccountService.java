package com.realtech.socialsurvey.core.services.api;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.PaymentPlan;
import com.realtech.socialsurvey.core.entities.Phone;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;


public interface AccountService
{
    public Map<String, Long> saveAccountRegistrationDetailsAndGetIdsInMap( User user, String companyName, Phone phone )
        throws NonFatalException;


    public CompanyCompositeEntity getCompanyProfileDetails( int companyId ) throws InvalidInputException;


    public void updateCompanyProfile( long compId, CompanyCompositeEntity companyProfile ) throws InvalidInputException;


    public void deleteCompanyProfileImage( int companyId ) throws InvalidInputException;


    public void updateCompanyProfileImage( int companyId, String imageUrl ) throws InvalidInputException;


    public void updateStage( int companyId, String stage );


    public List<VerticalsMaster> getIndustries();


    public List<PaymentPlan> getPaymentPlans();
}
