package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.request.LinkedInConnectRequest;
import com.realtech.socialsurvey.core.entities.api.LinkedInConnect;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class LinkedInConnectTransformer implements Transformer<LinkedInConnectRequest, LinkedInConnect, Void>
{

    public LinkedInConnect transformApiRequestToDomainObject( LinkedInConnectRequest request )
    {
        LinkedInConnect linkedInConnect = new LinkedInConnect();
        linkedInConnect.setFirstName( request.getFirstName() );
        linkedInConnect.setLastName( request.getLastName() );
        linkedInConnect.setTitle( request.getTitle() );
        linkedInConnect.setProfilePhotoUrl( request.getProfilePhotoUrl() );
        linkedInConnect.setUserId( request.getUserId() );
        return linkedInConnect;
    }


    public Void transformDomainObjectToApiResponse( LinkedInConnect linkedInConnect )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
