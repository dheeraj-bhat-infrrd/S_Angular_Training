package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


/*
 * Interface for UserProfileDao to perform various operations on UserProfile.
 */
public interface UserProfileDao extends GenericDao<UserProfile, Long>
{

    public void deactivateAllUserProfilesForUser( User admin, User userToBeDeactivated );


    public void deactivateUserProfileForBranch( User admin, long branchId, User userToBeDeactivated );


    public void deactivateUserProfileForRegion( User admin, long regionId, User userToBeDeactivated );


    public List<Long> getBranchIdsForUser( User user );


    public List<Long> getBranchesForAdmin( User user, List<ProfilesMaster> profilesMasters );


    public void deleteUserProfilesByCompany( long companyId );


    public List<UserProfile> findUserProfilesInBatch( Map<String, Object> queries, int startIndex, int batchSize );


    public Map<String, Long> findPrimaryUserProfileByAgentId( long entityId );
    

    public Set<Long> findUserIdsByBranch( long branchId );


    public Set<Long> findUserIdsByRegion( long regionId );


    public int getUsersUnderBranchAdminCount( User admin );


    public int getUsersUnderRegionAdminCount( User admin );


    public int getUsersUnderCompanyAdminCount( User admin );


    public List<UserFromSearch> findUsersUnderBranchAdmin( User admin, int startIndex, int batchSize );


    public List<UserFromSearch> findUsersUnderRegionAdmin( User admin, int startIndex, int batchSize );


    public List<UserFromSearch> findUsersUnderCompanyAdmin( User admin, int startIndex, int batchSize );


    public List<UserFromSearch> getUserFromSearchByUserIds( Set<Long> userIds );


    /**
     * Method to update regionId for a particular branchId in user profiles
     * @param branchId
     * @param regionId
     * @throws InvalidInputException 
     */
    public void updateRegionIdForBranch( long branchId, long regionId ) throws InvalidInputException;


    void activateAllUserProfilesForUser( User userToBeActivated );


    public void updateEmailIdForUserProfile( long userId, String emailId ) throws InvalidInputException;


    Map<Long, List<UserProfile>> getUserProfilesForUsers( List<Long> userIds );
}
