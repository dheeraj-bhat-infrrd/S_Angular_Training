package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ActionHistory;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorFeedData;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;

public interface MongoSocialFeedDao
{
    public void insertSocialFeed( SocialResponseObject<?> socialFeed, String collectionName );

    long updateDuplicateCount(int hash, long companyId);

    boolean isSocialPostSaved(String postId);
    
    public SocialResponseObject getSocialFeed(String postId, String collectionName);
        
    public List<SocialResponseObject> getAllSocialFeeds(long profileId, String key, int startIndex, int limit, boolean flag, String status, List<String> feedtype);
    
    public long getAllSocialFeedsCount(long profileId, String key, boolean flag, String status, List<String> feedtype);
    
	public void updateSocialFeed(SocialFeedsActionUpdate socialFeedsActionUpdate, List<ActionHistory> actionHistory, int updateFlag, String collectionName);
	
	public OrganizationUnitSettings FetchMacros(long companyId);
	
	public void updateMacros(SocialMonitorMacro socialMonitorMacro, long companyId);
	
	public void updateMacroCount(List<SocialMonitorMacro> socialMonitorMacros, long companyId);

}
