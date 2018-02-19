package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.ActionHistory;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import java.util.List;

public interface MongoSocialFeedDao
{
    public void insertSocialFeed( SocialResponseObject<?> socialFeed, String collectionName );

    long updateDuplicateCount(int hash, long companyId);
    
    public SocialResponseObject getSocialFeed(String postId, String collectionName);
                
	public void updateSocialFeed(SocialFeedsActionUpdate socialFeedsActionUpdate, List<ActionHistory> actionHistory, int updateFlag, String collectionName);
	
	public OrganizationUnitSettings FetchMacros(long companyId);
	
	public void updateMacros(SocialMonitorMacro socialMonitorMacro, long companyId);
	
	public void updateMacroCount(List<SocialMonitorMacro> socialMonitorMacros, long companyId);
	
	public List<SocialResponseObject> getAllSocialFeeds(int startIndex, int limit, boolean flag, String status, List<String> feedtype, Long companyId, List<Long> regionId, List<Long> branchId, List<Long> agentid);
	
	public long getAllSocialFeedsCount(boolean flag, String status, List<String> feedtype, Long companyId, List<Long> regionId, List<Long> branchId, List<Long> agentid);


}
