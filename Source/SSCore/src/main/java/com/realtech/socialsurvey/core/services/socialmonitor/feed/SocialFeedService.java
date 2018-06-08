package com.realtech.socialsurvey.core.services.socialmonitor.feed;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SegmentsVO;
import com.realtech.socialsurvey.core.entities.SocialFeedActionResponse;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorFeedTypeVO;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SocialMonitorResponseData;
import com.realtech.socialsurvey.core.entities.SocialMonitorUsersVO;
import com.realtech.socialsurvey.core.entities.SocialResponseObject;
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
     * @param id
     * @return
     */
    long updateDuplicateCount( int hash, long companyId, String id ) throws InvalidInputException;


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
    public SocialMonitorResponseData getAllSocialPosts(int startIndex, int limit, String status, boolean flag, List<String> feedtype, Long companyId, List<Long> regionIds, List<Long> branchIds, List<Long> agentIds, String searchText, boolean isCompanySet) throws InvalidInputException;
        
    /**
     * Update actions and macros on feeds
     * @param socialFeedsActionUpdate
     * @param companyId
     * @param duplicateFlag
     * @return
     * @throws InvalidInputException
     */
    public SocialFeedActionResponse updateActionForFeeds(SocialFeedsActionUpdate socialFeedsActionUpdate, Long companyId, boolean duplicateFlag) throws InvalidInputException;
    
    /**
     * Fetch macros based on companyId
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public List<SocialMonitorMacro> getMacros(long companyId, String searchMacros) throws InvalidInputException; 
    
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
     * @return
     * @throws InvalidInputException
     */
    public SegmentsVO getSegmentsByCompanyId(Long companyId) throws InvalidInputException;
    
    /**
     * fetches all the users of a company
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public List<SocialMonitorUsersVO> getUsersByCompanyId(Long companyId) throws InvalidInputException, ProfileNotFoundException;

    /**
     * Hits StreamApi to queue failed social feeds
     */
    public void retryFailedSocialFeeds();
    
    /**
     * Fetches all the feedtypes for a company
     * @param companyId
     * @return
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    public SocialMonitorFeedTypeVO getFeedTypesByCompanyId(Long companyId) throws InvalidInputException;

    List<SocialResponseObject> getSocialFeed( String keyword, long companyId, long startTime, long endTime, int pageSize, int skips )
        throws InvalidInputException;

    List<SocialResponseObject> getSocialFeed( long companyId, long startTime, long endTime, int pageSize, int skips )
        throws InvalidInputException;
   
    
    /**
     * Method to move document from social feed collection to archive collection
     * @return true if operation is successful, else false.
     */
    public boolean moveDocumentToArchiveCollection();

    /**
     * @param companyId
     * @param trustedSource
     * @throws InvalidInputException 
     */
    public void updateTrustedSourceForFormerLists( long companyId, String trustedSource ) throws InvalidInputException;
    
    /**
    * 
    * @param companyId
    * @param postId
    * @return
    * @throws InvalidInputException
    */
    public SocialMonitorResponseData getDuplicatePosts(Long companyId, String postId) throws InvalidInputException;

    
}
