package com.realtech.socialsurvey.core.services.api;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.PaymentPlan;
import com.realtech.socialsurvey.core.entities.Phone;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.exception.HierarchyAlreadyExistsException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.exception.ActiveSubscriptionFoundException;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public interface AccountService
{
    public Map<String, Long> saveAccountRegistrationDetailsAndGetIdsInMap( User user, String companyName, Phone phone )
        throws InvalidInputException, UserAlreadyExistsException, SolrException, NoRecordsFetchedException, NonFatalException;


    public CompanyCompositeEntity getCompanyProfileDetails( long companyId ) throws InvalidInputException;


    public void updateCompanyProfile( long compId, long userId, CompanyCompositeEntity companyProfile )
        throws InvalidInputException;


    public void deleteCompanyProfileImage( long companyId, long userId ) throws InvalidInputException;


    public void updateCompanyProfileImage( long companyId, long userId, String imageUrl ) throws InvalidInputException;


    public void updateStage( long companyId, String stage );


    public List<VerticalsMaster> getIndustries();


    public List<PaymentPlan> getPaymentPlans();


    public void payForPlan( long companyId, int planId, String nonce, String cardHolderName, String name, String email,
        String message ) throws InvalidInputException, PaymentException, SubscriptionUnsuccessfulException,
        NoRecordsFetchedException, CreditCardException, ActiveSubscriptionFoundException, UndeliveredEmailException;


    public void generateDefaultHierarchy( long companyId )
        throws InvalidInputException, SolrException, HierarchyAlreadyExistsException;
}
