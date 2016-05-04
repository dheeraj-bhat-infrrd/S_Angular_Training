package com.realtech.socialsurvey.core.services.api;

import com.realtech.socialsurvey.core.entities.api.UserProfile;


/**
 * @author Shipra Goyal, RareMile
 *
 */
public interface UserService
{
    public void connectLinkedIn( int userId, UserProfile userProfile );


    public void updateUserProfile( int userId, UserProfile userProfile );


    public UserProfile getUserProfileDetails( int userId );
}
