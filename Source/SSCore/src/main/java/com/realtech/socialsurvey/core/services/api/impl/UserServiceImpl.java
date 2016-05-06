package com.realtech.socialsurvey.core.services.api.impl;

import org.springframework.stereotype.Service;

import com.realtech.socialsurvey.core.entities.api.UserProfile;
import com.realtech.socialsurvey.core.services.api.UserService;


@Service
public class UserServiceImpl implements UserService
{

    @Override
    public void updateUserProfile( int userId, UserProfile userProfile )
    {
        // TODO Auto-generated method stub
        // Registration stage should be set as 2 for user

    }


    @Override
    public UserProfile getUserProfileDetails( int userId )
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void deleteUserProfileImage( int userId )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateUserProfileImage( int userId, String imageUrl )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateStage( int parseInt, String stage )
    {
        // TODO Auto-generated method stub

    }

}
