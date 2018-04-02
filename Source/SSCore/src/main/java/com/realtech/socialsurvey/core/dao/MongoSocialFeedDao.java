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
                    
	public void updateSocialFeed(SocialFeedsActionUpdate socialFeedsActionUpdate, String postId, List<ActionHistory> actionHistory, int updateFlag, String collectionName);
	
	public OrganizationUnitSettings FetchMacros(Long companyId);
	
	public void updateMacros(SocialMonitorMacro socialMonitorMacro, long companyId);
	
	public void updateMacroList(List<SocialMonitorMacro> socialMonitorMacros, long companyId);
	
	public List<SocialResponseObject> getAllSocialFeeds(int startIndex, int limit, boolean flag, String status, List<String> feedtype, Long companyId, List<Long> regionId, List<Long> branchId, List<Long> agentid, String searchText, boolean isCompanySet);
	
	public long getAllSocialFeedsCount(boolean flag, String status, List<String> feedtype, Long companyId, List<Long> regionId, List<Long> branchId, List<Long> agentid, String searchText, boolean isCompanySet);
	
	public OrganizationUnitSettings getCompanyDetails(Long companyId);
	
	public List<OrganizationUnitSettings> getAllRegionDetails(List<Long> regionIds);

	public List<OrganizationUnitSettings> getAllBranchDetails(List<Long> branchIds);
	
	public OrganizationUnitSettings getAllUserDetails(Long userId);
	
	public List<SocialResponseObject> getSocialPostsByIds(Set<String> postIds, String collectionName);
	
	public List<SocialResponseObject> getDuplicatePostIds(int hash, Long companyId);
	

}
