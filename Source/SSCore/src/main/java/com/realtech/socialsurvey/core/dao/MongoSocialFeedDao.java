package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.ActionHistory;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import java.util.List;
import java.util.Set;

public interface MongoSocialFeedDao
{
    public void insertSocialFeed( SocialResponseObject<?> socialFeed, String collectionName );

    long updateDuplicateCount(int hash, long companyId);
                    
	public void updateSocialFeed(SocialFeedsActionUpdate socialFeedsActionUpdate, SocialResponseObject socialResponseObject, Long companyId, List<ActionHistory> actionHistory, int updateFlag, String collectionName);
	
	public OrganizationUnitSettings FetchMacros(Long companyId);
	
	public void updateMacros(SocialMonitorMacro socialMonitorMacro, long companyId);
	
	public void updateMacroList(List<SocialMonitorMacro> socialMonitorMacros, long companyId);
	
	public List<SocialResponseObject> getAllSocialFeeds(int startIndex, int limit, boolean flag, String status, List<String> feedtype, Long companyId, List<Long> regionId, List<Long> branchId, List<Long> agentid, String searchText, boolean isCompanySet);
	
	public long getAllSocialFeedsCount(boolean flag, String status, List<String> feedtype, Long companyId, List<Long> regionId, List<Long> branchId, List<Long> agentid, String searchText, boolean isCompanySet);
	
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

    
}
