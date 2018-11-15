package com.realtech.socialsurvey.core.services.api;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.Phone;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserCompositeEntity;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public interface UserService
{
    public void updateUserProfile( long userIdLong, UserCompositeEntity userProfile )
        throws SolrException, InvalidInputException;


    public UserCompositeEntity getUserProfileDetails( long userId ) throws InvalidInputException;


    public void deleteUserProfileImage( long userId ) throws InvalidInputException;


    public void updateUserProfileImage( long userId, String imageUrl ) throws InvalidInputException;


    public void updateStage( long userId, String stage );


    public User addUser( String firstName, String lastName, String emailId, Phone phone, Company company )
        throws InvalidInputException, SolrException, NoRecordsFetchedException;


    public void sendRegistrationEmail( User user, int planId ) throws NonFatalException;


    public boolean isUserExist( String emailId ) throws InvalidInputException;


    public void savePassword( long userId, String password ) throws InvalidInputException;
    
    public Long getOwnerByCompanyId(Long companyId) throws NonFatalException;
}
