package com.realtech.socialsurvey.core.services.sendgridmanagement;

import java.util.List;

import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.vo.SendgridUnsubscribeVO;

public interface SendgridManagementService
{

    public void addNewEmailToUnsubscribeList( String emailId ) throws NonFatalException;

    public void removewEmailFromUnsubscribeList( String emailId ) throws NonFatalException;

    public List<SendgridUnsubscribeVO> getUnsubscribedEmailList() throws NonFatalException;

}
