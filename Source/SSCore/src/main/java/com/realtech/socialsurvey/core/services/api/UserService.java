package com.realtech.socialsurvey.core.services.api;

import com.realtech.socialsurvey.core.entities.api.UserProfile;


public interface UserService
{
    public void updateUserProfile( int userId, UserProfile userProfile );


    public UserProfile getUserProfileDetails( int userId );


    public void deleteUserProfileImage( int userId );


    public void updateUserProfileImage( int userId, String imageUrl );


    public void updateStage( int parseInt, String stage );
}
