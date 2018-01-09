package com.realtech.socialsurvey.stream.services;

import com.realtech.socialsurvey.stream.entities.FailedEmailMessage;

import java.util.List;


/**
 * Created by nishit on 04/01/18.
 */
public interface FailedMessagesService
{
    List<FailedEmailMessage> getFailedEmailMessages(String filter);

}
