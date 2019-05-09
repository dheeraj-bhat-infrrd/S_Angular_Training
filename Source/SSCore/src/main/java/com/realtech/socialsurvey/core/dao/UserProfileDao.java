package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.realtech.socialsurvey.core.entities.ProfilesMaster;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


/*
 * Interface for UserProfileDao to perform various operations on UserProfile.
 */
public interface UserProfileDao extends GenericDao<UserProfile, Long>
{

    public void deactivateAllUserProfilesForUser( User admin, User userToBeDeactivated, int status );


    public void deactivateUserProfileForBranch( User admin, long branchId, User userToBeDeactivated );


    public void deactivateUserProfileForRegion( User admin, long regionId, User userToBeDeactivated );


    public List<Long> getBranchIdsForUser( User user );


    public List<Long> getBranchesForAdmin( User user, List<ProfilesMaster> profilesMasters );


    public void deleteUserProfilesByCompany( long companyId );


    public List<UserProfile> findUserProfilesInBatch( Map<String, Object> queries, int startIndex, int batchSize );


    public Map<String, Long> findPrimaryUserProfileByAgentId( long entityId );


    public Set<Long> findUserIdsByBranch( long branchId );


    public Set<Long> findUserIdsByRegion( long regionId );


    public int getUsersUnderBranchAdminCount( User admin, String status );


    public int getUsersUnderRegionAdminCount( User admin, String status );


    public int getUsersUnderCompanyAdminCount( User admin, String status );


    public List<UserFromSearch> findUsersUnderBranchAdmin( User admin, int startIndex, int batchSize, String sortingOrder, String userStatus );


    public List<UserFromSearch> findUsersUnderRegionAdmin( User admin, int startIndex, int batchSize, String sortingOrder, String userStatus );


    public List<UserFromSearch> findUsersUnderCompanyAdmin( User admin, int startIndex, int batchSize, String sortingOrder, String userStatus );


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


    void deleteUserProfilesByUser( long userId );


    public UserProfile findUserProfile( long userId, long branchId, long regionId, int profilesMasterId )
        throws InvalidInputException, NoRecordsFetchedException;

    public List<UserProfile> getUserProfiles(Long companyId);


    public List<UserProfile> getUserProfiles( long userId );
    
    
    public List<UserProfile> getImmediateAdminForAgent (long agentId, long companyId);
    
    
    public List<UserProfile> getImmediateAdminForRegionOrBranch (long companyId, long regionId, long branchId);
    
    
    public List<UserProfile> getBranchAdminsForBranchId(long branchId);
    

    public List<UserProfile> getRegionAdminsForRegionId(long regionId);
    
    
    public List<UserProfile> getSMAdminsForCompanyId(long companyId);
    
    
    public List<UserProfile> getCompanyAdminForCompanyId(long companyId);


    /**
     * @param companyId
     * @return
     */
    public Map<String, Map<Long, List<Long>>> getUserListForhierarchy( long companyId );


	/**
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	public List<Long> findPrimaryUserProfile(String entityType, long entityId) ;
	
	/**
	 * Method to get all the active roles for userIds
	 * @param userIds
	 * @param sortOrder
     * @return
	 */

    public List<Long> findBranchUserProfile( String entityType, long entityId );

    public List<Long> findRegionUserProfile( String entityType, long entityId );

	public List<UserFromSearch> getActiveUserFromSearchByUserIds( Set<Long> userIds, String sortOrder );
}
