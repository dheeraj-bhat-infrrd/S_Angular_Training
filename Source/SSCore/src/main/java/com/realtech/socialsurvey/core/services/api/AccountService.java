package com.realtech.socialsurvey.core.services.api;

import com.realtech.socialsurvey.core.entities.api.AccountRegistration;


/**
 * @author Shipra Goyal, RareMile
 *
 */
public interface AccountService
{
    public void saveAccountRegistrationDetailsAndSetDataInDO( AccountRegistration accountRegistration );
}
