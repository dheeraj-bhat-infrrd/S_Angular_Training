package com.realtech.socialsurvey.web.ui.entities;

import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;


public class AccountRegistration extends AccountRegistrationAPIRequest
{
    private static final long serialVersionUID = 1L;
    
    private String captchaResponse;


    public String getCaptchaResponse()
    {
        return captchaResponse;
    }


    public void setCaptchaResponse( String captchaResponse )
    {
        this.captchaResponse = captchaResponse;
    }
}
