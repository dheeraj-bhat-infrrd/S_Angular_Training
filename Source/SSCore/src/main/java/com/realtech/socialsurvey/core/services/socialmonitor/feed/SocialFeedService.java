package com.realtech.socialsurvey.core.services.socialmonitor.feed;

import com.realtech.socialsurvey.core.entities.SocialResponseObject;
import java.util.List;

import com.realtech.socialsurvey.core.entities.SegmentsEntity;
import com.realtech.socialsurvey.core.entities.SegmentsVO;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialMonitorResponseData;
import com.realtech.socialsurvey.core.entities.SocialMonitorUsersVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;

/**
 * @author manish
 *
 */
public interface SocialFeedService
{
    /**
     * Method to save social feed data in mongo
     * @param socialFeed
     * @return
     * @throws InvalidInputException 
     */
    public SocialResponseObject<?> saveFeed(SocialResponseObject<?> socialFeed) throws InvalidInputException;

    /**
     * Updates all posts matching given hash and companyId with the given duplicateCount
     * @param hash
     * @param companyId
     * @param duplicateCount
     * @return
     */
    long updateDuplicateCount(int hash, long companyId) throws InvalidInputException;


    /**
     * Gets all feeds on social monitor based on the flag, status and feed type
    /** Gets all feeds on social monitor based on the flag
     * @param profileId
     * @param profileLevel
     * @param startIndex
     * @param limit
     * @param flag
     * @return
     * @throws InvalidInputException
     */
    public SocialMonitorResponseData getAllSocialPosts(int startIndex, int limit, String status, boolean flag, List<String> feedtype, Long companyId, List<Long> regionIds, List<Long> branchIds, List<Long> agentIds) throws InvalidInputException;
        
    /**
     * Update actions and macros on feeds
     * @param socialFeedsActionUpdate
     * @param companyId
     * @throws InvalidInputException
     */
    public void updateActionForFeeds(SocialFeedsActionUpdate socialFeedsActionUpdate, Long companyId) throws InvalidInputException;
    
    /**
     * Fetch macros based on companyId
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public List<SocialMonitorMacro> getMacros(long companyId) throws InvalidInputException; 
    
    /**
     * add macros to a company
     * @param socialMonitorMacro
     * @param companyId
     * @throws InvalidInputException
     */
    public void updateMacrosForFeeds(SocialMonitorMacro socialMonitorMacro, long companyId) throws InvalidInputException;
    
    /**
     * 
     * @param macroId
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public SocialMonitorMacro getMacroById(String macroId, Long companyId) throws InvalidInputException;
    
    /**
     * fetches all the regions and branches of a company
     * @param companyId
     * @param startIndex
     * @param batchSize
     * @return
     * @throws InvalidInputException
     */
    public SegmentsVO getSegmentsByCompanyId(Long companyId, int startIndex, int batchSize) throws InvalidInputException;
    
    /**
     * fetches all the users of a company
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public List<SocialMonitorUsersVO> getUsersByCompanyId(Long companyId) throws InvalidInputException, ProfileNotFoundException;


    
}
