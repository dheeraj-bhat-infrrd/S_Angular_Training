package com.realtech.socialsurvey.core.services.sms;

import com.realtech.socialsurvey.core.entities.SmsEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface SmsServices {
    
    public boolean sendSmsReminder( String configuredSmsText, String surveyLink, String customerFirstName, String agentFirstName, SmsEntity smsEntity, boolean saveToStreamLater ) throws InvalidInputException;

}

