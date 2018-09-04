package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.*;

import java.util.List;
import java.util.Set;

public interface MongoSocialFeedDao
{
    public void insertSocialFeed( SocialResponseObject<?> socialFeed, String collectionName );

    int updateDuplicateCount( int hash, long companyId, String id );
                    
	public void updateSocialFeed(SocialFeedsActionUpdate socialFeedsActionUpdate, SocialResponseObject socialResponseObject, Long companyId, List<ActionHistory> actionHistory, int updateFlag, String collectionName);
	
	public OrganizationUnitSettings FetchMacros(Long companyId);
	
	public void updateMacros(SocialMonitorMacro socialMonitorMacro, long companyId);
	
	public void updateMacroList(List<SocialMonitorMacro> socialMonitorMacros, long companyId);
	

    public List<SocialResponseObject> getAllSocialFeeds( int startIndex, int limit, String status, List<String> feedtype,
        Long companyId, List<Long> regionId, List<Long> branchId, List<Long> agentid, String searchText, boolean isCompanySet,
        boolean fromTrustedSource, boolean isSocMonOnLoad );

	
    public long getAllSocialFeedsCount( String status, List<String> feedtype, Long companyId, List<Long> regionId,
        List<Long> branchId, List<Long> agentid, String searchText, boolean isCompanySet, boolean fromTrustedSource,
		boolean isSocMonOnLoad);

	
	public OrganizationUnitSettings getCompanyDetails(Long companyId);
	
	public List<OrganizationUnitSettings> getAllRegionDetails(List<Long> regionIds);

	public List<OrganizationUnitSettings> getAllBranchDetails(List<Long> branchIds);
	
	public OrganizationUnitSettings getAllUserDetails(Long userId);
	
	public List<SocialResponseObject> getSocialPostsByIds(Set<String> postIds, Long companyId, String collectionName);
	
	public List<SocialResponseObject> getDuplicatePostIds(int hash, Long companyId);
	
	public OrganizationUnitSettings getProfileImageUrl(long iden, String collectionName);
	
	public long fetchFacebookTokenCount(List<Long> ids, String collectioName);
	
	public long fetchTwitterTokenCount(List<Long> ids, String collectioName);
	
	public long fetchLinkedinTokenCount(List<Long> ids, String collectioName);
	
	public long fetchInstagramTokenCount(List<Long> ids, String collectioName);
	
	/**
	 * Method to move document from social feed collection to archive collection
	 * @param days : archive before days
	 * @return true if operation is successful, else false.
	 */
	boolean moveDocumentToArchiveCollection( int days );

	List<SocialResponseObject> getSocialFeed( String keyword, long companyId, long startTime, long endTime, int pageSize, int skips );


	List<SocialResponseObject> getSocialFeed( long companyId, long startTime, long endTime, int pageSize, int skips );

    /**
     * @param companyId
     * @param trustedSource
     * @param actionHistory
     * @return
     */
    public long updateForTrustedSource( long companyId, String trustedSource, ActionHistory actionHistory );
    
    /**
     * Method to get a social post
     * @param companyId
     * @param postId
     * @return
     */
    public SocialResponseObject getSocialPost( Long companyId, String postId, String collectionName );
    
    /**
     * Method to get all duplicate post details
     * @param companyId
     * @param hash
     * @return
     */
    public List<SocialResponseObject> getAllDuplicatePostDetails(Long companyId, int hash);

    /**
     * Method to to set fromTrustedSource to false
     * @param companyId
     * @param trustedSource
     * @return
     */
    public long updateForRemoveTrustedSource( long companyId, String trustedSource );

    /**
     * Add action history by mongoId
     * @param mongoId
     * @param actionHistory
     * @return
     */
    public long updateActionHistory( String mongoId, ActionHistory actionHistory );

	/**
	 * Bulk inserts social feeds into mongo
	 * @param socialFeeds
	 * @param socialFeedCollection
	 */
	void insertSocialFeeds( List<SocialResponseObject<?>> socialFeeds, String socialFeedCollection );
}
