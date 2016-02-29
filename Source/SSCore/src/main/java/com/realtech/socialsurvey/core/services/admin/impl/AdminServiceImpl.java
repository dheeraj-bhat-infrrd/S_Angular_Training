package com.realtech.socialsurvey.core.services.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.braintreegateway.Subscription;
import com.braintreegateway.Transaction;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.admin.AdminService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.vo.SubscriptionVO;
import com.realtech.socialsurvey.core.vo.TransactionVO;


/**
 * 
 * @author rohit
 *
 */
@Component
public class AdminServiceImpl implements AdminService
{

    private static final Logger LOG = LoggerFactory.getLogger( AdminServiceImpl.class );

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private Payment payment;

    @Autowired
    private UserManagementService userManagementService;


    @Override
    @Transactional
    public SubscriptionVO getSubscriptionVOBySubscriptionId( String subscriptionId ) throws InvalidInputException
    {

        LOG.info( "methodd getSubscriptionVOBySubscriptionId started" );
        SubscriptionVO subscriptionVO = new SubscriptionVO();
        Subscription subscription = payment.getSubscriptionDetailFromBrainTree( subscriptionId );

        if ( subscription == null ) {
            return subscriptionVO;
        }

        subscriptionVO.setId( subscription.getId() );
        subscriptionVO.setBalance( subscription.getBalance() );
        subscriptionVO.setBillingDayOfMonth( subscription.getBillingDayOfMonth() );
        subscriptionVO.setBillingPeriodEndDate( subscription.getBillingPeriodEndDate() );
        subscriptionVO.setBillingPeriodStartDate( subscription.getBillingPeriodStartDate() );
        subscriptionVO.setCurrentBillingCycle( subscription.getCurrentBillingCycle() );
        subscriptionVO.setCreatedAt( subscription.getCreatedAt() );
        subscriptionVO.setUpdatedAt( subscription.getUpdatedAt() );
        subscriptionVO.setFirstBillingDate( subscription.getFirstBillingDate() );
        subscriptionVO.getNextBillAmount();
        subscriptionVO.setNextBillAmount( subscription.getNextBillAmount() );
        subscriptionVO.setNextBillingDate( subscription.getNextBillingDate() );
        subscriptionVO.setNextBillingPeriodAmount( subscription.getNextBillingPeriodAmount() );

        Company company = companyDao.getCompanyByBraintreeSubscriptionId( subscription.getId() );
        if ( company != null ) {
            subscriptionVO.setCompanyId( company.getCompanyId() );
            subscriptionVO.setCompanyName( company.getCompany() );
            User user = userManagementService.getCompanyAdmin( company.getCompanyId() );
            subscriptionVO.setCompanyAdminId( user.getUserId() );
            subscriptionVO.setCompanyAdminFirstName( user.getFirstName() );
            subscriptionVO.setCompanyAdminLastName( user.getLastName() );
        }

        return subscriptionVO;
    }


    @Override
    @Transactional
    public List<TransactionVO> getTransactionListBySubscriptionIs( String subscriptionId ) throws InvalidInputException
    {
        LOG.info( "Method getTransactionListBySubscriptionIs started for subscriptionId : " + subscriptionId );
        List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
        List<Transaction> transactions = payment.getTransactionListFromBrainTree( subscriptionId );
        if ( transactions == null ) {
            return transactionVOs;
        }
        for ( Transaction transaction : transactions ) {
            TransactionVO transactionVO = new TransactionVO();

            transactionVO.setId( transaction.getId() );
            transactionVO.setAmount( transaction.getAmount() );
            transactionVO.setCreatedAt( transaction.getCreatedAt() );
            transactionVO.setCreditCard( transaction.getCreditCard() );
            transactionVO.setStatus( transaction.getStatus() );
            transactionVO.setStatusHistory( transaction.getStatusHistory() );
            transactionVO.setSubscriptionId( transaction.getSubscriptionId() );

            Company company = companyDao.getCompanyByBraintreeSubscriptionId( transaction.getSubscriptionId() );
            if ( company != null ) {
                transactionVO.setCompanyId( company.getCompanyId() );
                transactionVO.setCompanyName( company.getCompany() );
                User user = userManagementService.getCompanyAdmin( company.getCompanyId() );
                transactionVO.setCompanyAdminId( user.getUserId() );
                transactionVO.setCompanyAdminFirstName( user.getFirstName() );
                transactionVO.setCompanyAdminLastName( user.getLastName() );
            }

            transactionVOs.add( transactionVO );

        }
        LOG.info( "Method getTransactionListBySubscriptionIs ended " );
        return transactionVOs;
    }


    @Override
    @Transactional
    public List<SubscriptionVO> getActiveSubscriptionsList() throws InvalidInputException
    {
        LOG.info( "Method getActiveSubscriptionsList started " );
        List<SubscriptionVO> subscriptionVOs = new ArrayList<SubscriptionVO>();
        List<Subscription> subscriptions = payment.getActiveSubscriptionsListFromBrainTree();
        if ( subscriptions == null ) {
            return subscriptionVOs;
        }
        for ( Subscription subscription : subscriptions ) {
            SubscriptionVO subscriptionVO = new SubscriptionVO();

            subscriptionVO.setId( subscription.getId() );
            subscriptionVO.setBalance( subscription.getBalance() );
            subscriptionVO.setBillingDayOfMonth( subscription.getBillingDayOfMonth() );
            subscriptionVO.setBillingPeriodEndDate( subscription.getBillingPeriodEndDate() );
            subscriptionVO.setBillingPeriodStartDate( subscription.getBillingPeriodStartDate() );
            subscriptionVO.setCurrentBillingCycle( subscription.getCurrentBillingCycle() );
            subscriptionVO.setCreatedAt( subscription.getCreatedAt() );
            subscriptionVO.setUpdatedAt( subscription.getUpdatedAt() );
            subscriptionVO.setFirstBillingDate( subscription.getFirstBillingDate() );
            subscriptionVO.getNextBillAmount();
            subscriptionVO.setNextBillAmount( subscription.getNextBillAmount() );
            subscriptionVO.setNextBillingDate( subscription.getNextBillingDate() );
            subscriptionVO.setNextBillingPeriodAmount( subscription.getNextBillingPeriodAmount() );

            Company company = companyDao.getCompanyByBraintreeSubscriptionId( subscription.getId() );
            if ( company != null ) {
                subscriptionVO.setCompanyId( company.getCompanyId() );
                subscriptionVO.setCompanyName( company.getCompany() );
                User user = userManagementService.getCompanyAdmin( company.getCompanyId() );
                subscriptionVO.setCompanyAdminId( user.getUserId() );
                subscriptionVO.setCompanyAdminFirstName( user.getFirstName() );
                subscriptionVO.setCompanyAdminLastName( user.getLastName() );
            }

            subscriptionVOs.add( subscriptionVO );
        }

        LOG.info( "Method getActiveSubscriptionsList ended " );
        return subscriptionVOs;
    }


    @Override
    @Transactional
    public List<Company> getAllAutoBillingModeCompanies()
    {
        LOG.info( "Method getAllAutoBillingModeCompanies started " );
        List<Company> CompanyList = companyDao.getCompaniesByBillingModeAuto();
        LOG.info( "Method getAllAutoBillingModeCompanies ended " );
        return CompanyList;
    }
}
