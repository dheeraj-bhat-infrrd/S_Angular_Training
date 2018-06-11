package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


public class EmailServicesImplTest
{
    private EmailServicesImpl emailServicesImpl;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {}


    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {}


    @Before
    public void setUp() throws Exception
    {
        emailServicesImpl = new EmailServicesImpl();
    }


    @After
    public void tearDown() throws Exception
    {}


    // Tests for sendRegistrationInviteMail
    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationInviteMailTestUrlNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationInviteMail( null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationInviteMailTestUrlEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationInviteMail( "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationInviteMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationInviteMail( "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationInviteMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationInviteMail( "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationInviteMailTestFirstNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationInviteMail( "test", "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationInviteMailTestFirstNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationInviteMail( "test", "test", "", "test" );
    }


    // Tests for sendInvitationToSocialSurveyAdmin
    @Test ( expected = InvalidInputException.class)
    public void sendInvitationToSocialSurveyAdminTestUrlNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvitationToSocialSurveyAdmin( null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendInvitationToSocialSurveyAdminTestUrlEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvitationToSocialSurveyAdmin( "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendInvitationToSocialSurveyAdminTestRecipientMailIdNull()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvitationToSocialSurveyAdmin( "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendInvitationToSocialSurveyAdminTestRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvitationToSocialSurveyAdmin( "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendInvitationToSocialSurveyAdminTestFirstNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvitationToSocialSurveyAdmin( "test", "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendInvitationToSocialSurveyAdminTestFirstNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvitationToSocialSurveyAdmin( "test", "test", "", "test" );
    }


    //Tests for sendAgentSurveyReminderMail
    @Test ( expected = InvalidInputException.class)
    public void sendAgentSurveyReminderMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAgentSurveyReminderMail( null, new SurveyPreInitiation() );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAgentSurveyReminderMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAgentSurveyReminderMail( "", new SurveyPreInitiation() );
    }


    //Tests for sendResetPasswordEmail
    @Test ( expected = InvalidInputException.class)
    public void sendResetPasswordEmailTestUrlNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendResetPasswordEmail( null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendResetPasswordEmailTestUrlEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendResetPasswordEmail( "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendResetPasswordEmailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendResetPasswordEmail( "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendResetPasswordEmailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendResetPasswordEmail( "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendResetPasswordEmailTestFirstNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendResetPasswordEmail( "test", "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendResetPasswordEmailTestFirstNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendResetPasswordEmail( "test", "test", "", "test" );
    }


    //Tests for sendSubscriptionChargeUnsuccessfulEmail
    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionChargeUnsuccessfulEmailTestRecipientMailIdNull()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionChargeUnsuccessfulEmail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionChargeUnsuccessfulEmailTestRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionChargeUnsuccessfulEmail( "", "test", "test" );
    }


    public void sendSubscriptionChargeUnsuccessfulEmailTestNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionChargeUnsuccessfulEmail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionChargeUnsuccessfulEmailTestNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionChargeUnsuccessfulEmail( "test", "", "test" );
    }


    //Tests for sendEmailVerificationMail
    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationMailTestUrlNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationMail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationMailTestUrlEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationMail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationMail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationMail( "test", "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationMailTestNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationMail( "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationMailTestNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationMail( "test", "test", "" );
    }


    //Tests for sendVerificationMail
    @Test ( expected = InvalidInputException.class)
    public void sendVerificationMailTestUrlNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendVerificationMail( null, "test", "test", null, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendVerificationMailTestUrlEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendVerificationMail( "", "test", "test", null, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendVerificationMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendVerificationMail( "test", null, "test", null, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendVerificationMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendVerificationMail( "test", "", "test", null, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendVerificationMailTestNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendVerificationMail( "test", "test", null, null, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendVerificationMailTestNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendVerificationMail( "test", "test", "", null, null, false );
    }


    //Tests for sendRegistrationCompletionEmail
    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationCompletionEmailTestUrlNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationCompletionEmail( null, "test", "test", null, null, false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationCompletionEmailTestUrlEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationCompletionEmail( "", "test", "test", null, null, false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationCompletionEmailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationCompletionEmail( "test", null, "test", null, null, false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationCompletionEmailTestRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationCompletionEmail( "test", "", "test", null, null, false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationCompletionEmailTestNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationCompletionEmail( "test", "test", null, null, null, false, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRegistrationCompletionEmailTestNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRegistrationCompletionEmail( "test", "test", "", null, null, false, false );
    }


    //Tests for sendFatalExceptionEmail
    @Test ( expected = InvalidInputException.class)
    public void sendFatalExceptionEmailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendFatalExceptionEmail( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendFatalExceptionEmailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendFatalExceptionEmail( "", "test" );
    }


    //Tests for sendEmailSendingFailureMail
    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestDestinationMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestDestinationMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( "test", "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( "test", "test", "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestStackTraceNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( "test", "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailSendingFailureMailTestStackTraceEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailSendingFailureMail( "test", "test", "test", "" );
    }


    //Tests for sendRetryChargeEmail
    @Test ( expected = InvalidInputException.class)
    public void sendRetryChargeEmailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryChargeEmail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRetryChargeEmailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryChargeEmail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRetryChargeEmailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryChargeEmail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRetryChargeEmailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryChargeEmail( "test", "", "test" );
    }


    //Tests for sendRetryExhaustedEmail
    @Test ( expected = InvalidInputException.class)
    public void sendRetryExhaustedEmailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryExhaustedEmail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRetryExhaustedEmailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryExhaustedEmail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRetryExhaustedEmailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryExhaustedEmail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRetryExhaustedEmailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRetryExhaustedEmail( "test", "", "test" );
    }


    //Tests for sendAccountDisabledMail
    @Test ( expected = InvalidInputException.class)
    public void sendAccountDisabledMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDisabledMail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountDisabledMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDisabledMail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountDisabledMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDisabledMail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountDisabledMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDisabledMail( "test", "", "test" );
    }


    //Tests for sendAccountDeletionMail
    @Test ( expected = InvalidInputException.class)
    public void sendAccountDeletionMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDeletionMail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountDeletionMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDeletionMail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountDeletionMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDeletionMail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountDeletionMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountDeletionMail( "test", "", "test" );
    }


    //Tests for sendAccountUpgradeMail
    @Test ( expected = InvalidInputException.class)
    public void sendAccountUpgradeMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountUpgradeMail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountUpgradeMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountUpgradeMail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountUpgradeMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountUpgradeMail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountUpgradeMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountUpgradeMail( "test", "", "test" );
    }

    //Tests for sendSurveyCompletionMailToAdminsAndAgent
    @Test ( expected = InvalidInputException.class)
    public void sendSurveyCompletionMailToAdminsAndAgentTestRecipientMailIdNull()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyCompletionMailToAdminsAndAgent( null, null, null, "test", null, null, null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyCompletionMailToAdminsAndAgentTestRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyCompletionMailToAdminsAndAgent( null, null, "", "test", null, null, null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyCompletionMailToAdminsAndAgentTestSurveyNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyCompletionMailToAdminsAndAgent( "test", "test", "test", null, null, null, null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyCompletionMailToAdminsAndAgentTestSurveyEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyCompletionMailToAdminsAndAgent( "test", "test", "test", "", null, null, null, null, null, null );
    }


    //Tests for sendContactUsMail
    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestRecipientEmailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail( null, "test", "test", "test", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestRecipientEmailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail( new ArrayList<String>(), "test", "test", "test", "test" , "test", "test");
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail(  new ArrayList<String>(), null, "test", "test", "test" , "test", "test");
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail(  new ArrayList<String>(), "test", "", "test", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestSenderNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail(  new ArrayList<String>(), "test", "test", null, "test" , "test", "test");
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestSenderNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail(  new ArrayList<String>(), "test", "test", "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestSenderEmailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail(  new ArrayList<String>(), "test", "test", "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestSenderEmailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail(  new ArrayList<String>(), "test", "test", "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestMessageNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail( new ArrayList<String>(), "test", "test", "test", "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendContactUsMailTestMessageEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendContactUsMail( new ArrayList<String>(), "test", "test", "test", "test", "","test" );
    }

    //Tests for sendSurveyRelatedMail
    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyRelatedMail( null, "test", "test", null, null, 0, 0, null, false);
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyRelatedMail( "", "test", "test", null, null, 0, 0, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestSubjectNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyRelatedMail( "test", null, "test", null, null, 0, 0, null, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyInvitationMailTestSubjectEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyRelatedMail( "test", "", "test", null, null, 0, 0, null, false );
    }


    //Tests for sendAccountBlockingMail
    @Test ( expected = InvalidInputException.class)
    public void sendAccountBlockingMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountBlockingMail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountBlockingMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountBlockingMail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountBlockingMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountBlockingMail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountBlockingMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountBlockingMail( "test", "", "test" );
    }


    //Tests for sendAccountReactivationMail
    @Test ( expected = InvalidInputException.class)
    public void sendAccountReactivationMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountReactivationMail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountReactivationMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountReactivationMail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountReactivationMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountReactivationMail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendAccountReactivationMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendAccountReactivationMail( "test", "", "test" );
    }


    //Tests for sendSubscriptionRevisionMail
    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( null, "test", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "", "test", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestOldAmountNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestOldAmountEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestRevisedAmountNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", "test", "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestRevisedAmountEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", "test", "test", "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestNumOfUsersNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", "test", "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSubscriptionRevisionMailTestNumOfUsersEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSubscriptionRevisionMail( "test", "test", "test", "test", "" );
    }


    //Tests for sendManualRegistrationLink
    @Test ( expected = InvalidInputException.class)
    public void sendManualRegistrationLinkTestRecipientIDNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendManualRegistrationLink( null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendManualRegistrationLinkTestRecipientIDEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendManualRegistrationLink( "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendManualRegistrationLinkTestFirstNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendManualRegistrationLink( "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendManualRegistrationLinkTestFirstNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendManualRegistrationLink( "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendManualRegistrationLinkTestLinkNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendManualRegistrationLink( "test", "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendManualRegistrationLinkTestLinkEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendManualRegistrationLink( "test", "test", "test", "" );
    }


    //Tests for sendSocialConnectMail
    @Test ( expected = InvalidInputException.class)
    public void sendSocialConnectMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSocialConnectMail( null, "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSocialConnectMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSocialConnectMail( "", "test", "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSocialConnectMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSocialConnectMail( "test", null, "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSocialConnectMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSocialConnectMail( "test", "", "test", null );
    }


    //Tests for sendReportAbuseMail
    @Test ( expected = InvalidInputException.class)
    public void sendReportAbuseMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendReportAbuseMail( null, "test", "test", null, null, null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendReportAbuseMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendReportAbuseMail( "", "test", "test", null, null, null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendReportAbuseMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendReportAbuseMail( "test", null, "test", null, null, null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendReportAbuseMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendReportAbuseMail( "test", "", "test", null, null, null, null, null, null );
    }


    //Tests for sendSurveyReportMail
    @Test ( expected = InvalidInputException.class)
    public void sendSurveyReportMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyReportMail( null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyReportMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyReportMail( "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyReportMailTestDisplayNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyReportMail( "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendSurveyReportMailTestDisplayNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendSurveyReportMail( "test", "", "test" );
    }


    //Tests for sendCorruptDataFromCrmNotificationMail
    @Test ( expected = InvalidInputException.class)
    public void sendCorruptDataFromCrmNotificationMailTestRecipientMailIdNull()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendCorruptDataFromCrmNotificationMail( null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendCorruptDataFromCrmNotificationMailTestRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendCorruptDataFromCrmNotificationMail( null, null, "", null );
    }


    //Tests for sendInvalidEmailsNotificationMail
    @Test ( expected = InvalidInputException.class)
    public void sendInvalidEmailsNotificationMailTestRecipientMailIdNull()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvalidEmailsNotificationMail( null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendInvalidEmailsNotificationMailTestRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendInvalidEmailsNotificationMail( null, null, "", null );
    }


    //Tests for sendRecordsNotUploadedCrmNotificationMail
    @Test ( expected = InvalidInputException.class)
    public void sendRecordsNotUploadedCrmNotificationMailTestRecipientMailIdNull()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRecordsNotUploadedCrmNotificationMail( null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendRecordsNotUploadedCrmNotificationMailTestRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendRecordsNotUploadedCrmNotificationMail( null, null, "", null );
    }


    //Tests for sendHelpMailToAdmin
    @Test ( expected = InvalidInputException.class)
    public void sendHelpMailToAdminTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendHelpMailToAdmin( null, null, null, null, null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendHelpMailToAdminTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendHelpMailToAdmin( null, null, null, null, null, "", null );
    }


    //Tests for sendReportBugMailToAdmin
    @Test ( expected = InvalidInputException.class)
    public void sendReportBugMailToAdminTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendReportBugMailToAdmin( null, null, null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendReportBugMailToAdminTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendReportBugMailToAdmin( null, null, "" );
    }


    //Tests for sendComplaintHandleMail
    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( null, null, "test", "test", null, null, null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( "", null, "test","test", null, null, null, "test" );
    }

    
    
    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestAgentNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( "test", null, "test", null, null, null, null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestAgentNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( "test", null, "test", "", null, null, null, "test" );
    }
    

    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestCustomerMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( "test", null, null, "test", null, null, null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestCustomerMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( "test", null, "", "test", null, null, null, "test" );
    }


    //SS-1435: Test for survey details    
    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestSurveyDetailNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( "test", null, "test", "test", null, null, null, null );
    }


    //SS-1435: Test for survey details
    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestSurveyDetailEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendComplaintHandleMail( "test", null, "test", "test", null, null, null, "" );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestRecipientMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( null, TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestRecipientMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestSubjectNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, null, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestSubjectEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestMailBodyNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING, null,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailMailBodyEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestMailSenderNameNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, null, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailSenderNameEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestMailSenderEmailAddressNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, null, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailSenderEmailAddressEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailTestMailMessageIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, null, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void forwardCustomerReplyMailMessageIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.forwardCustomerReplyMail( TestConstants.TEST_STRING, TestConstants.TEST_STRING,
            TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_STRING, TestConstants.TEST_EMPTY_STRING, TestConstants.TEST_EMPTY_STRING );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendBillingReportMailForMailIdNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendBillingReportMail( "abc", "xyz", null, new ArrayList<EmailAttachment>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendBillingReportMailForMailIdEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendBillingReportMail( "abc", "xyz", "", new ArrayList<EmailAttachment>() );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendZillowReviewComplaintHandleMailWithRecipientMailIdNull()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendZillowReviewComplaintHandleMail( null, null, "" + 0, "http://www.test.com" );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendZillowReviewComplaintHandleMailWithRecipientMailIdEmpty()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendZillowReviewComplaintHandleMail( "", null, "" + 0, "http://www.test.com" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestCustomerNameIsNull() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendZillowReviewComplaintHandleMail( "test", null, "" + 0, "http://www.test.com" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendComplaintHandleMailTestCustomerNameIsEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendZillowReviewComplaintHandleMail( "test", "", "" + 0, "http://www.test.com" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationRequestMailToAdminNullUrl() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationRequestMailToAdmin( null, "test", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationRequestMailToAdminEmptyUrl() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationRequestMailToAdmin( "", "test", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationRequestMailToAdminNullRcepientMail()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationRequestMailToAdmin( "test", null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationRequestMailToAdminEmptyRcepientMail()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationRequestMailToAdmin( "test", "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationRequestMailToAdminNullRcepientName()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationRequestMailToAdmin( "test", "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerificationRequestMailToAdminEmptyRcepientName()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerificationRequestMailToAdmin( "test", "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailNullRcepientMail() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMail( null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailEmptyRcepientMail() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMail( "", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailNullRcepientName() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMail( "test", null );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailEmptyRcepientName() throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMail( "test", "" );
    }

    ///////


    @Test ( expected = InvalidInputException.class)
    public void ssendEmailVerifiedNotificationMailToAdminNullRcepientMail()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMailToAdmin( null, "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailToAdminEmptyRcepientMail()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMailToAdmin( "", "test", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailToAdminNullRcepientName()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMailToAdmin( "test", null, "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailToAdminEmptyRcepientName()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMailToAdmin( "test", "", "test", "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailToAdminNullVerifiedEmail()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMailToAdmin( "test", "test", null, "test" );
    }


    @Test ( expected = InvalidInputException.class)
    public void sendEmailVerifiedNotificationMailToAdminEmptyVerifiedEmaill()
        throws InvalidInputException, UndeliveredEmailException
    {
        emailServicesImpl.sendEmailVerifiedNotificationMailToAdmin( "test", "test", "", "test" );
    }

}
