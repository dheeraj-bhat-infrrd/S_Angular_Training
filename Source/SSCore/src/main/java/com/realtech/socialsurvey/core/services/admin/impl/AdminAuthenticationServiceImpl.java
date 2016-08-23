package com.realtech.socialsurvey.core.services.admin.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.AuthorizationException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
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
        Map<String, String> params = new HashMap<String, String>();
        if ( authorizationHeader == null || authorizationHeader.isEmpty() ) {
            throw new AuthorizationException( "Authorization header is null" );
        }

        
        try {
            String plainText = encryptionHelper.decryptAES( authorizationHeader, "" );
            String keyValuePairs[] = plainText.split( "&" );

            for ( int counter = 0; counter < keyValuePairs.length; counter += 1 ) {
                String[] keyValuePair = keyValuePairs[counter].split( "=" );
                params.put( keyValuePair[0], keyValuePair[1] );
            }
        } catch ( InvalidInputException e ) {
            throw new AuthorizationException( "Authorization failure" );
        }
        
        String comapnyIdStr = params.get( CommonConstants.COMPANY_ID_COLUMN );
        String apiKey = params.get( CommonConstants.API_KEY_COLUMN );
        String apiSecret = params.get( CommonConstants.API_SECRET_COLUMN );
        long comapnyId;
       
        boolean isValid;
        try {
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

}
