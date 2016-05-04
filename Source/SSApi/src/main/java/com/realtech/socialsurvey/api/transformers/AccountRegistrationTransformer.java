package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.request.AccountRegistrationRequest;
import com.realtech.socialsurvey.api.models.response.AccountRegistrationResponse;
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.Phone;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class AccountRegistrationTransformer
    implements Transformer<AccountRegistrationRequest, AccountRegistration, AccountRegistrationResponse>
{
    public AccountRegistration transformApiRequestToDomainObject( AccountRegistrationRequest request )
    {
        AccountRegistration accountRegistration = new AccountRegistration();
        if ( request != null ) {
            accountRegistration.setFirstName( request.getFirstName() );
            accountRegistration.setLastName( request.getLastName() );
            accountRegistration.setCompanyName( request.getCompanyName() );
            accountRegistration.setEmail( request.getEmail() );

            if ( request.getPhone() != null ) {
                Phone phone = new Phone();
                phone.setCountryCode( request.getPhone().getCountryCode() );
                phone.setExtension( request.getPhone().getExtension() );
                phone.setNumber( request.getPhone().getNumber() );
                accountRegistration.setPhone( phone );
            }
        }

        return accountRegistration;
    }


    public AccountRegistrationResponse transformDomainObjectToApiResponse( AccountRegistration accountRegistration )
    {
        AccountRegistrationResponse response = new AccountRegistrationResponse();
        if ( accountRegistration != null ) {
            response.setCompanyId( accountRegistration.getCompanyId() );
            response.setUserId( accountRegistration.getUserId() );
        }
        return response;
    }
}
