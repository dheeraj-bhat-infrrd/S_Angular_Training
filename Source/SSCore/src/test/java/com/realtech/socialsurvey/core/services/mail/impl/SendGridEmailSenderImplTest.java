package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import com.realtech.socialsurvey.TestConstants;
import com.realtech.socialsurvey.core.entities.EmailEntity;
import com.realtech.socialsurvey.core.entities.FileContentReplacements;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;


public class SendGridEmailSenderImplTest
{
    @InjectMocks
    private SendGridEmailSenderImpl sendGridEmailSenderImpl;

    private EmailEntity emailEntity;
    private List<String> recipients;


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
        emailEntity = new EmailEntity();
        recipients = new ArrayList<String>();
        recipients.add( TestConstants.TEST_MAIL_ID_STRING );
    }


    @After
    public void tearDown() throws Exception
    {}


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailByEmailEntityWithNullRecipients() throws InvalidInputException
    {
        sendGridEmailSenderImpl.sendEmailByEmailEntity( emailEntity );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailByEmailEntityWithEmptyRecipients() throws InvalidInputException
    {
        emailEntity.setRecipients( new ArrayList<String>() );
        sendGridEmailSenderImpl.sendEmailByEmailEntity( emailEntity );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailByEmailEntityWithNullBody() throws InvalidInputException
    {
        emailEntity.setRecipients( recipients );
        sendGridEmailSenderImpl.sendEmailByEmailEntity( emailEntity );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailByEmailEntityWithEmptyBody() throws InvalidInputException
    {
        emailEntity.setRecipients( recipients );
        emailEntity.setBody( TestConstants.TEST_EMPTY_STRING );
        sendGridEmailSenderImpl.sendEmailByEmailEntity( emailEntity );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailByEmailEntityWithNullSubject() throws InvalidInputException
    {
        emailEntity.setRecipients( recipients );
        emailEntity.setBody( TestConstants.TEST_STRING );
        sendGridEmailSenderImpl.sendEmailByEmailEntity( emailEntity );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailByEmailEntityWithEmptySubject() throws InvalidInputException
    {
        emailEntity.setRecipients( recipients );
        emailEntity.setBody( TestConstants.TEST_STRING );
        emailEntity.setSubject( TestConstants.TEST_EMPTY_STRING );
        sendGridEmailSenderImpl.sendEmailByEmailEntity( emailEntity );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithBodyReplacementsWithNullSubjectFileName() throws InvalidInputException,
        UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmailWithBodyReplacements( emailEntity, null, null, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithBodyReplacementsWithEmptySubjectFileName() throws InvalidInputException,
        UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmailWithBodyReplacements( emailEntity, TestConstants.TEST_EMPTY_STRING, null, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithBodyReplacementsWithNullMessageBodyReplacements() throws InvalidInputException,
        UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmailWithBodyReplacements( emailEntity, TestConstants.TEST_STRING + ".txt", null, true,
            true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithSubjectAndBodyReplacementsWithNullSubjectReplacements() throws InvalidInputException,
        UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmailWithSubjectAndBodyReplacements( emailEntity, null, null, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithSubjectAndBodyReplacementsWithNullMessageBodyReplacements() throws InvalidInputException,
        UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmailWithSubjectAndBodyReplacements( emailEntity, new FileContentReplacements(), null,
            true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithNullSubject() throws InvalidInputException, UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmail( emailEntity, null, null, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithEmptySubject() throws InvalidInputException, UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmail( emailEntity, TestConstants.TEST_EMPTY_STRING, null, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSendEmailWithNullMailBody() throws InvalidInputException, UndeliveredEmailException
    {
        Whitebox.setInternalState( sendGridEmailSenderImpl, "sendMail", "Y" );
        sendGridEmailSenderImpl.sendEmail( emailEntity, TestConstants.TEST_STRING, null, true, true );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSaveEmailRecipientsNull() throws InvalidInputException, UndeliveredEmailException
    {
        sendGridEmailSenderImpl.saveEmail( new EmailEntity(), false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSaveEmailRecipientsEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        EmailEntity email = new EmailEntity();
        email.setRecipients( new ArrayList<String>() );
        sendGridEmailSenderImpl.saveEmail( email, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSaveEmailBodyNull() throws InvalidInputException, UndeliveredEmailException
    {
        EmailEntity email = new EmailEntity();
        List<String> list = new ArrayList<String>();
        list.add( "test" );
        email.setRecipients( list );
        sendGridEmailSenderImpl.saveEmail( email, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSaveEmailBodyEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        EmailEntity email = new EmailEntity();
        List<String> list = new ArrayList<String>();
        list.add( "test" );
        email.setRecipients( list );
        email.setBody( "" );
        sendGridEmailSenderImpl.saveEmail( email, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSaveEmailSubjectNull() throws InvalidInputException, UndeliveredEmailException
    {
        EmailEntity email = new EmailEntity();
        List<String> list = new ArrayList<String>();
        list.add( "test" );
        email.setRecipients( list );
        email.setBody( "test" );
        sendGridEmailSenderImpl.saveEmail( email, false );
    }


    @Test ( expected = InvalidInputException.class)
    public void testSaveEmailSubjectEmpty() throws InvalidInputException, UndeliveredEmailException
    {
        EmailEntity email = new EmailEntity();
        List<String> list = new ArrayList<String>();
        list.add( "test" );
        email.setRecipients( list );
        email.setBody( "test" );
        email.setSubject( "" );
        sendGridEmailSenderImpl.saveEmail( email, false );
    }
}
