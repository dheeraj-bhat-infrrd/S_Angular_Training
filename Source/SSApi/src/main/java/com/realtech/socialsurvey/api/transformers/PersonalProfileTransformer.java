package com.realtech.socialsurvey.api.transformers;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.api.models.PersonalProfile;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;


@Component
public class PersonalProfileTransformer implements Transformer<PersonalProfile, UserCompositeEntity, PersonalProfile>
{
    public UserCompositeEntity transformApiRequestToDomainObject( PersonalProfile request, Object... objects )
    {
        UserCompositeEntity userProfile = new UserCompositeEntity();
        User user = null;
        AgentSettings agentSettings = null;

        if ( request != null ) {

            if ( objects[0] != null && objects[0] instanceof User ) {
                user = (User) objects[0];
                user.setFirstName( request.getFirstName() );
                user.setLastName( request.getLastName() );
                userProfile.setUser( user );
            }

            if ( objects[1] != null && objects[1] instanceof AgentSettings ) {
                agentSettings = (AgentSettings) objects[1];
                ContactDetailsSettings contactDetails = agentSettings.getContact_details();
                if ( contactDetails == null ) {
                    contactDetails = new ContactDetailsSettings();
                }
                contactDetails.setFirstName( request.getFirstName() );
                contactDetails.setLastName( request.getLastName() );
                contactDetails.setLocation( request.getLocation() );
                contactDetails.setTitle( request.getTitle() );
                if ( contactDetails.getContact_numbers() == null ) {
                    contactDetails.setContact_numbers( new ContactNumberSettings() );
                }
                contactDetails.getContact_numbers().setPhone1( request.getPhone1() );
                contactDetails.getContact_numbers().setPhone2( request.getPhone2() );

                if ( contactDetails.getWeb_addresses() == null ) {
                    contactDetails.setWeb_addresses( new WebAddressSettings() );
                }
                contactDetails.getWeb_addresses().setWork( request.getWebsite() );
                if ( contactDetails.getMail_ids() == null ) {
                    contactDetails.setMail_ids( new MailIdSettings() );
                }
                contactDetails.getMail_ids().setWork( user.getEmailId() );

                agentSettings.setContact_details( contactDetails );
                agentSettings.setProfileImageUrl( request.getProfilePhotoUrl() );
                agentSettings.setProfileImageUrlThumbnail( request.getProfilePhotoUrl() );
                userProfile.setAgentSettings( agentSettings );
            }
        }
        return userProfile;
    }


    public PersonalProfile transformDomainObjectToApiResponse( UserCompositeEntity userProfile )
    {
        PersonalProfile response = new PersonalProfile();
        if ( userProfile != null ) {
            if ( userProfile.getUser() != null ) {
                response.setUserId( (int) userProfile.getUser().getUserId() );
                response.setFirstName( userProfile.getUser().getFirstName() );
                response.setLastName( userProfile.getUser().getLastName() );
            }
            if ( userProfile.getAgentSettings() != null ) {
                response.setProfilePhotoUrl( userProfile.getAgentSettings().getProfileImageUrl() );
                if ( userProfile.getAgentSettings().getContact_details() != null ) {
                    response.setLocation( userProfile.getAgentSettings().getContact_details().getLocation() );
                    response.setTitle( userProfile.getAgentSettings().getContact_details().getTitle() );
                    if ( userProfile.getAgentSettings().getContact_details().getContact_numbers() != null ) {
                        response
                            .setPhone1( userProfile.getAgentSettings().getContact_details().getContact_numbers().getPhone1() );
                        response
                            .setPhone2( userProfile.getAgentSettings().getContact_details().getContact_numbers().getPhone2() );
                    }
                    if ( userProfile.getAgentSettings().getContact_details().getWeb_addresses() != null ) {
                        response.setWebsite( userProfile.getAgentSettings().getContact_details().getWeb_addresses().getWork() );
                    }
                }
            }
        }
        return response;
    }
}
