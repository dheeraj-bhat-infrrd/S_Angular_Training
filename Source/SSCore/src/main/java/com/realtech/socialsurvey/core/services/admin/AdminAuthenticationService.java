package com.realtech.socialsurvey.core.services.admin;

import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface AdminAuthenticationService {

    /**
     * 
     * @param authorizationHeader
     * @return
     * @throws InvalidInputException
     */
    public long validateAuthHeader( String authorizationHeader ) throws AuthorizationException;
    
    public boolean validateSSOTuple( String productId, String ssoToken, String ssoTicket );

}
