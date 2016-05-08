package com.realtech.socialsurvey.core.services.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.entities.api.PaymentPlan;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.services.api.AccountService;


@Service
public class AccountServiceImpl implements AccountService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AccountServiceImpl.class );
    private GenericDao<VerticalsMaster, Integer> industryDao;
    private GenericDao<AccountsMaster, Integer> paymentPlanDao;


    @Autowired
    public AccountServiceImpl( GenericDao<VerticalsMaster, Integer> industryDao,
        GenericDao<AccountsMaster, Integer> paymentPlanDao )
    {
        this.industryDao = industryDao;
        this.paymentPlanDao = paymentPlanDao;
    }


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


    @Override
    public List<VerticalsMaster> getIndustries()
    {
        LOGGER.info( "AccountServiceImpl.getIndustries started" );
        List<VerticalsMaster> industries = industryDao.findAll( VerticalsMaster.class );
        LOGGER.info( "AccountServiceImpl.getIndustries completed successfully" );
        return industries;
    }


    @Override
    public List<PaymentPlan> getPaymentPlans()
    {
        LOGGER.info( "AccountServiceImpl.getPaymentPlans started" );
        List<PaymentPlan> paymentPlans = new ArrayList<PaymentPlan>();
        List<AccountsMaster> plans = paymentPlanDao.findAll( AccountsMaster.class );
        for ( AccountsMaster plan : plans ) {
            if ( plan.getAccountsMasterId() == AccountType.INDIVIDUAL.getValue() ) {
                paymentPlans
                    .add( getPaymentPlan( 1, plan.getAmount(), "$", plan.getAccountsMasterId(), "Individual", "", "" ) );
            } else if ( plan.getAccountsMasterId() == AccountType.ENTERPRISE.getValue() ) {
                paymentPlans.add( getPaymentPlan( 2, plan.getAmount(), "$", plan.getAccountsMasterId(), "Business", "", "" ) );
                paymentPlans.add( getPaymentPlan( 3, 0, "$", plan.getAccountsMasterId(), "Enterprise", "", "" ) );
            }
        }
        LOGGER.info( "AccountServiceImpl.getPaymentPlans completed successfully" );
        return paymentPlans;
    }


    private PaymentPlan getPaymentPlan( int level, double amount, String currency, int planId, String planName, String text,
        String terms )
    {
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setAmount( amount );
        paymentPlan.setLevel( level );
        paymentPlan.setPlanCurrency( currency );
        paymentPlan.setPlanId( planId );
        paymentPlan.setPlanName( planName );
        paymentPlan.setSupportingText( text );
        paymentPlan.setTerms( terms );
        return paymentPlan;
    }
}
