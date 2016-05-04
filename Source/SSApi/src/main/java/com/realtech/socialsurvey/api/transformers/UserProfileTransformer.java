package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.request.UserProfileRequest;
import com.realtech.socialsurvey.api.models.response.UserProfileResponse;
import com.realtech.socialsurvey.core.entities.api.Phone;
import com.realtech.socialsurvey.core.entities.api.UserProfile;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class UserProfileTransformer implements Transformer<UserProfileRequest, UserProfile, UserProfileResponse>
{
    public UserProfile transformApiRequestToDomainObject( UserProfileRequest request )
    {
        UserProfile userProfile = new UserProfile();
        if ( request != null ) {
            userProfile.setFirstName( request.getFirstName() );
            userProfile.setLastName( request.getLastName() );
            userProfile.setTitle( request.getTitle() );
            userProfile.setProfilePhotoUrl( request.getProfilePhotoUrl() );
            userProfile.setUserId( request.getUserId() );
            userProfile.setWebsite( request.getWebsite() );
            userProfile.setLocation( request.getLocation() );

            if ( request.getPhone1() != null ) {
                Phone phone = new Phone();
                phone.setCountryCode( request.getPhone1().getCountryCode() );
                phone.setExtension( request.getPhone1().getExtension() );
                phone.setNumber( request.getPhone1().getNumber() );
                userProfile.setPhone1( phone );
            }

            if ( request.getPhone2() != null ) {
                Phone phone = new Phone();
                phone.setCountryCode( request.getPhone2().getCountryCode() );
                phone.setExtension( request.getPhone2().getExtension() );
                phone.setNumber( request.getPhone2().getNumber() );
                userProfile.setPhone1( phone );
            }
        }

        return userProfile;
    }


    public UserProfileResponse transformDomainObjectToApiResponse( UserProfile userProfile )
    {
        UserProfileResponse response = new UserProfileResponse();
        if ( userProfile != null ) {
            response.setFirstName( userProfile.getFirstName() );
            response.setLastName( userProfile.getLastName() );
            response.setTitle( userProfile.getTitle() );
            response.setProfilePhotoUrl( userProfile.getProfilePhotoUrl() );
            response.setUserId( userProfile.getUserId() );
            response.setWebsite( userProfile.getWebsite() );
            response.setLocation( userProfile.getLocation() );

            if ( userProfile.getPhone1() != null ) {
                com.realtech.socialsurvey.api.models.Phone phone = new com.realtech.socialsurvey.api.models.Phone();
                phone.setCountryCode( userProfile.getPhone1().getCountryCode() );
                phone.setExtension( userProfile.getPhone1().getExtension() );
                phone.setNumber( userProfile.getPhone1().getNumber() );
                response.setPhone1( phone );
            }

            if ( userProfile.getPhone2() != null ) {
                com.realtech.socialsurvey.api.models.Phone phone = new com.realtech.socialsurvey.api.models.Phone();
                phone.setCountryCode( userProfile.getPhone2().getCountryCode() );
                phone.setExtension( userProfile.getPhone2().getExtension() );
                phone.setNumber( userProfile.getPhone2().getNumber() );
                response.setPhone1( phone );
            }
        }

        return response;
    }
}
