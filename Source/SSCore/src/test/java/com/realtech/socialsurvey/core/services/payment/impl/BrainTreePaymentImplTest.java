package com.realtech.socialsurvey.core.services.payment.impl;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.braintreegateway.Subscription;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.payment.exception.CardUpdateUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.CustomerDeletionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentRetryUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionCancellationUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionPastDueException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUpgradeUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;

public class BrainTreePaymentImplTest
{

    @InjectMocks
    private BrainTreePaymentImpl brainTreePaymentImpl;
    
    @Mock
    private GenericDao<LicenseDetail, Long> licenseDetailDao;
    
    @Mock
    Subscription subscriptionMocked;
    
    User user;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks( this );
        user = new User();
        
    }

    @After
    public void tearDown() throws Exception {}
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertIntoLicenseTableForInvalidAccountMasterId() throws InvalidInputException{
        brainTreePaymentImpl.insertIntoLicenseTable( 0, user, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertIntoLicenseTableForNullUser() throws InvalidInputException{
        brainTreePaymentImpl.insertIntoLicenseTable( 10, null, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertIntoLicenseTableForEmptyUser() throws InvalidInputException{
        brainTreePaymentImpl.insertIntoLicenseTable( 10, user, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertIntoLicenseTableForNullSubscriptionId() throws InvalidInputException{
        brainTreePaymentImpl.insertIntoLicenseTable( 10, user, null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testInsertIntoLicenseTableForEmptySubscriptionId() throws InvalidInputException{
        brainTreePaymentImpl.insertIntoLicenseTable( 10, user, "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testSubscribeForNullUSer() throws InvalidInputException, PaymentException, NoRecordsFetchedException, SubscriptionUnsuccessfulException, CreditCardException{
        brainTreePaymentImpl.subscribe( null, 2, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testSubscribeForEmptyUSer() throws InvalidInputException, PaymentException, NoRecordsFetchedException, SubscriptionUnsuccessfulException, CreditCardException{
        brainTreePaymentImpl.subscribe( user, 2, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testSubscribeForInvalidAccountMasterId() throws InvalidInputException, PaymentException, NoRecordsFetchedException, SubscriptionUnsuccessfulException, CreditCardException{
        brainTreePaymentImpl.subscribe( user, 0, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testSubscribeForNullNonce() throws InvalidInputException, PaymentException, NoRecordsFetchedException, SubscriptionUnsuccessfulException, CreditCardException{
        brainTreePaymentImpl.subscribe( user, 0, null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testSubscribeForEmptyNonce() throws InvalidInputException, PaymentException, NoRecordsFetchedException, SubscriptionUnsuccessfulException, CreditCardException{
        brainTreePaymentImpl.subscribe( user, 0, "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetClientTokenWithCustomerIdForNullCustomerId() throws InvalidInputException{
        brainTreePaymentImpl.getClientTokenWithCustomerId( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetClientTokenWithCustomerIdForNEmptyCustomerId() throws InvalidInputException{
        brainTreePaymentImpl.getClientTokenWithCustomerId( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testMakePaymentForNullPaymentMethodToken() throws InvalidInputException{
        brainTreePaymentImpl.makePayment( null, new BigDecimal( 10 ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testMakePaymentForEmptyPaymentMethodToken() throws InvalidInputException{
        brainTreePaymentImpl.makePayment( "", new BigDecimal( 10 ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testMakePaymentForNullAmount() throws InvalidInputException{
        brainTreePaymentImpl.makePayment( "test", null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testMakePaymentForInvalidAmount() throws InvalidInputException{
        brainTreePaymentImpl.makePayment( "test", new BigDecimal( 0 ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangeLicenseToPastDueForNullSubscription() throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException{
        brainTreePaymentImpl.changeLicenseToPastDue( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangeLicenseToPastDueForEmptySubscription() throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException{
             brainTreePaymentImpl.changeLicenseToPastDue( new Subscription( null ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testRetrySubscriptionChargeForNullSubscriptionId() throws InvalidInputException, PaymentRetryUnsuccessfulException{
             brainTreePaymentImpl.retrySubscriptionCharge( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testRetrySubscriptionChargeForEmptySubscriptionId() throws InvalidInputException, PaymentRetryUnsuccessfulException{
             brainTreePaymentImpl.retrySubscriptionCharge( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckTransactionSettlingForNullTransactionId() throws InvalidInputException, NoRecordsFetchedException{
             brainTreePaymentImpl.checkTransactionSettling( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckTransactionSettlingForEmptyTransactionId() throws InvalidInputException, NoRecordsFetchedException{
             brainTreePaymentImpl.checkTransactionSettling( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckTransactionSettledForNullTransactionId() throws InvalidInputException, NoRecordsFetchedException{
             brainTreePaymentImpl.checkTransactionSettled( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckTransactionSettledForEmptyTransactionId() throws InvalidInputException, NoRecordsFetchedException{
             brainTreePaymentImpl.checkTransactionSettled( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckIfPaymentMadeForNullCompany() throws InvalidInputException, NoRecordsFetchedException{
             brainTreePaymentImpl.checkIfPaymentMade( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetDateForCompanyDeactivationForNullSubscriptionId() throws InvalidInputException, NoRecordsFetchedException, PaymentException{
             brainTreePaymentImpl.getDateForCompanyDeactivation( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetDateForCompanyDeactivationForEmptySubscriptionId() throws InvalidInputException, NoRecordsFetchedException, PaymentException{
             brainTreePaymentImpl.getDateForCompanyDeactivation( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUnsubscribeForNullSubscriptionId() throws InvalidInputException, SubscriptionCancellationUnsuccessfulException{
             brainTreePaymentImpl.unsubscribe( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUnsubscribeForEmptySubscriptionId() throws InvalidInputException, SubscriptionCancellationUnsuccessfulException{
             brainTreePaymentImpl.unsubscribe( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testDeleteCustomerForNullCustomerId() throws InvalidInputException, CustomerDeletionUnsuccessfulException{
             brainTreePaymentImpl.deleteCustomer( null );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testDeleteCustomerForEmptyCustomerId() throws InvalidInputException, CustomerDeletionUnsuccessfulException{
             brainTreePaymentImpl.deleteCustomer( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpgradePlanForSubscriptionForNullUser() throws InvalidInputException, NoRecordsFetchedException, SubscriptionPastDueException, PaymentException, SubscriptionUpgradeUnsuccessfulException, SolrException, UndeliveredEmailException, SubscriptionUnsuccessfulException, CreditCardException{
             brainTreePaymentImpl.upgradePlanForSubscription( null , 2, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpgradePlanForSubscriptionForEmptyUser() throws InvalidInputException, NoRecordsFetchedException, SubscriptionPastDueException, PaymentException, SubscriptionUpgradeUnsuccessfulException, SolrException, UndeliveredEmailException, SubscriptionUnsuccessfulException, CreditCardException{
             brainTreePaymentImpl.upgradePlanForSubscription( user , 2, "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpgradePlanForSubscriptionForInvalidAccountMasterId() throws InvalidInputException, NoRecordsFetchedException, SubscriptionPastDueException, PaymentException, SubscriptionUpgradeUnsuccessfulException, SolrException, UndeliveredEmailException, SubscriptionUnsuccessfulException, CreditCardException{
             brainTreePaymentImpl.upgradePlanForSubscription( user , -1 , "test" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetCurrentPaymentDetailsForNullSubscriptionId() throws InvalidInputException, NoRecordsFetchedException, PaymentException{
             brainTreePaymentImpl.getCurrentPaymentDetails( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetCurrentPaymentDetailsForEmptySubscriptionId() throws InvalidInputException, NoRecordsFetchedException, PaymentException{
             brainTreePaymentImpl.getCurrentPaymentDetails( "" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangePaymentMethodForNullSubscriptionId() throws InvalidInputException, NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException{
             brainTreePaymentImpl.changePaymentMethod( null, "test", "test1" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangePaymentMethodForEmptySubscriptionId() throws InvalidInputException, NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException{
             brainTreePaymentImpl.changePaymentMethod( "", "test", "test1" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangePaymentMethodForNullPaymentNonce() throws InvalidInputException, NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException{
             brainTreePaymentImpl.changePaymentMethod( "test", null, "test1" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangePaymentMethodForEmptyPaymentNonce() throws InvalidInputException, NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException{
             brainTreePaymentImpl.changePaymentMethod( "test", "", "test1" );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangePaymentMethodForNullCustomerId() throws InvalidInputException, NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException{
             brainTreePaymentImpl.changePaymentMethod( "test", "test1", null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testChangePaymentMethodForEmptyCustomerId() throws InvalidInputException, NoRecordsFetchedException, PaymentException, CreditCardException, CardUpdateUnsuccessfulException{
             brainTreePaymentImpl.changePaymentMethod( "test", "test1", "" );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testGetBalacnceAmountForPlanUpgradeForNullCompany() throws InvalidInputException{
             brainTreePaymentImpl.getBalacnceAmountForPlanUpgrade( null, 2, 3 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetBalacnceAmountForPlanUpgradeForEmptyCompany() throws InvalidInputException{
             brainTreePaymentImpl.getBalacnceAmountForPlanUpgrade( new Company(), 2, 3 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetBalacnceAmountForPlanUpgradeForInvalidFromAccountMasterIdLesser() throws InvalidInputException{
             brainTreePaymentImpl.getBalacnceAmountForPlanUpgrade( new Company(), 0, 3 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetBalacnceAmountForPlanUpgradeForInvalidFromAccountMasterIdHigher() throws InvalidInputException{
             brainTreePaymentImpl.getBalacnceAmountForPlanUpgrade( new Company(), 10, 3 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetBalacnceAmountForPlanUpgradeForInvalidToAccountMasterIdLesser() throws InvalidInputException{
             brainTreePaymentImpl.getBalacnceAmountForPlanUpgrade( new Company(), 3, 1 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testGetBalacnceAmountForPlanUpgradeForInvalidToAccountMasterIdHigher() throws InvalidInputException{
             brainTreePaymentImpl.getBalacnceAmountForPlanUpgrade( new Company(), 3, 10 );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckForExistingTransactionForNullLicenseDetail() throws InvalidInputException{
             brainTreePaymentImpl.checkForExistingTransaction( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckForExistingTransactionForEmptyLicenseDetail() throws InvalidInputException{
             brainTreePaymentImpl.checkForExistingTransaction( new LicenseDetail() );
    }
    
    
    @Test ( expected = InvalidInputException.class)
    public void testIncrementRetriesAndSendMailForNullSubscription() throws InvalidInputException, NoRecordsFetchedException{
             brainTreePaymentImpl.incrementRetriesAndSendMail( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testIncrementRetriesAndSendMailForEmptySubscription() throws InvalidInputException, NoRecordsFetchedException{
             brainTreePaymentImpl.incrementRetriesAndSendMail( new Subscription( null ) );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testIncrementRetriesAndSendMailForNullLicenceDetailList() throws InvalidInputException, NoRecordsFetchedException{
             Mockito.when( licenseDetailDao.findByColumn( Mockito.eq(LicenseDetail.class), Mockito.anyString() , Mockito.anyString() )).thenReturn( null );
             brainTreePaymentImpl.incrementRetriesAndSendMail( new Subscription( null ) );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testIncrementRetriesAndSendMailForEmptyLicenceDetailList() throws InvalidInputException, NoRecordsFetchedException{
             Mockito.when( licenseDetailDao.findByColumn( Mockito.eq(LicenseDetail.class), Mockito.anyString() , Mockito.anyString() )).thenReturn( new ArrayList<LicenseDetail>() );
             brainTreePaymentImpl.incrementRetriesAndSendMail( new Subscription( null ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableItForNullSubscription() throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException{
             brainTreePaymentImpl.checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testCheckIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableItForEmptySubscription() throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException{
             brainTreePaymentImpl.checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt( new Subscription( null ) );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testCheckIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableItForNullLicenceDetail() throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException{
        Mockito.when( licenseDetailDao.findByColumn( Mockito.eq(LicenseDetail.class), Mockito.anyString() , Mockito.anyString() )).thenReturn( null );
        brainTreePaymentImpl.checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt( new Subscription( null ) );
    }
    
    @Test ( expected = NoRecordsFetchedException.class)
    public void testCheckIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableItForEmptyLicenceDetail() throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException{
        Mockito.when( licenseDetailDao.findByColumn( Mockito.eq(LicenseDetail.class), Mockito.anyString(), Mockito.anyString() )).thenReturn( new ArrayList<LicenseDetail>() );
        brainTreePaymentImpl.checkIfCompanyIsDisabledOrSubscriptionIsPastDueAndEnableIt( new Subscription( null ) );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testIntimateUserForNullSubscription() throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException{
        brainTreePaymentImpl.intimateUser( null, 10 );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testIntimateUserForEmptySubscription() throws InvalidInputException, NoRecordsFetchedException, UndeliveredEmailException{
        brainTreePaymentImpl.intimateUser( new Subscription(null), 10 );
    }
        
    @Test ( expected = InvalidInputException.class)
    public void testUpdateSubscriptionPriceBasedOnUsersCountForNullCompany() throws InvalidInputException, NoRecordsFetchedException, PaymentException, SubscriptionUpgradeUnsuccessfulException{
        brainTreePaymentImpl.updateSubscriptionPriceBasedOnUsersCount( null );
    }
    
    @Test ( expected = InvalidInputException.class)
    public void testUpdateSubscriptionPriceBasedOnUsersCountForEmptyCompany() throws InvalidInputException, NoRecordsFetchedException, PaymentException, SubscriptionUpgradeUnsuccessfulException{
        brainTreePaymentImpl.updateSubscriptionPriceBasedOnUsersCount( new Company() );
    }
    

}
