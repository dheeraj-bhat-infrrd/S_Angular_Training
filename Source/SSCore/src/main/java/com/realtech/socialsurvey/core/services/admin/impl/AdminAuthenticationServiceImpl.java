package com.realtech.socialsurvey.core.services.admin.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;

@Component
public class AdminAuthenticationServiceImpl implements AdminAuthenticationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(AdminAuthenticationServiceImpl.class);
	
	@Autowired
    private EncryptionHelper encryptionHelper;
	
	@Autowired
    private UserManagementService userManagementService;
	
	/**
	 * 
	 */
	@Override
	public long validateAuthHeader( String authorizationHeader ) throws AuthorizationException
    {
	    LOG.debug( " method validateAuthHeader started" );

	    if (StringUtils.isBlank( authorizationHeader )) {
            throw new AuthorizationException( "Authorization header is null" );
        }

        String encodedUserPassword = authorizationHeader.replaceFirst("Basic" + " ", "");
        
        String plainText = null;
        try {
             plainText = encryptionHelper.decryptAES( encodedUserPassword, "" );
        } catch ( Exception e ) {
            throw new AuthorizationException( "Authorization failure" );
        }
        
        long comapnyId;
       
        boolean isValid;
        try {
            String[] keyValuePair = plainText.split( ":" );
            String apiKey = keyValuePair[0];
            String apiSecret = keyValuePair[1];
            String comapnyIdStr = apiSecret.split( "_" )[0];
            comapnyId = Long.valueOf( comapnyIdStr ) ;
            isValid = userManagementService.validateUserApiKey( apiKey, apiSecret, comapnyId );
        } catch ( NumberFormatException | InvalidInputException e ) {
            throw new AuthorizationException( "Authorization failure" );
        }
        
        if(!isValid){
            throw new AuthorizationException( "Authorization failure" );
        }
       
        LOG.debug( " method validateAuthHeader ended" );
        return comapnyId;
        
    }

    @Override
    public boolean validateSSOTuple( String productId, String ssoToken, String ssoTicket )
    {
        if( productId != "RM" ){
            return false;
        }
        if( !ssoToken.isEmpty() ){
            long ticket = Long.parseLong( ssoTicket );
            ticket += 60;
            if( ticket <= System.currentTimeMillis() ){
                return true;
            }
            else
                return false;
        }
        return false;
    }
}
