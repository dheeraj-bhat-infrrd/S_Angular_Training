package com.realtech.socialsurvey.core.services.surveybuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.BulkSurveyDetail;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyImportVO;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;
import com.realtech.socialsurvey.core.vo.SurveysAndReviewsVO;


public interface SurveyHandler
{

    /**
     * Method to store question and answer format into mongo.
     * 
     * @param agentId
     * @throws InvalidInputException
     * @throws SolrServerException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     */
    /*
     * public void storeInitialSurveyDetails(long agentId, long companyId, long regionId, long
     * branchId, String customerEmail, int reminderCount) throws InvalidInputException,
     * SolrException, NoRecordsFetchedException, SolrServerException;
     */

    /**
     * @param surveyId
     * @param question
     * @param questionType
     * @param answer
     * @param stage
     * @param isUserRankingQuestion
     * @param isNpsQuestion
     * @param questionId
     * @param considerForScore
     */
    void updateCustomerAnswersInSurvey( String surveyId, String question, String questionType, String answer, int stage,
        boolean isUserRankingQuestion, boolean isNpsQuestion, int questionId, boolean considerForScore );

    /*
     * Method to update customer review and final score on the basis of rating questions in
     * SURVEY_DETAILS.
     */
    public void updateGatewayQuestionResponseAndScore( String surveyId, String mood, String review, boolean isAbusive,
        String agreedToShare );


    public SurveyDetails storeInitialSurveyDetails( User user, SurveyPreInitiation surveyPreInitiation, String baseUrl,
        boolean isOldRecord, boolean retakeSurvey ) throws SolrException, NoRecordsFetchedException, InvalidInputException;


    public SurveyDetails getSurveyDetails( long agentId, String customerEmail, String firstName, String lastName );


    public String getApplicationBaseUrl();


    public void updateSurveyAsClicked( String surveyMongoId );


    public String getSwearWords();


    public void updateReminderCount( long surveyPreInitiationId, boolean reminder );


    public void markSurveyAsSent( SurveyPreInitiation surveyPreInitiation );


    public Map<String, Map<String, String>> getEmailIdsOfAdminsInHierarchy( long agentId ) throws InvalidInputException;


    public List<SurveyPreInitiation> getIncompleteSurveyForReminderEmail( Company company, Date minLastReminderDate , Date maxLastReminderDate, int maxReminderCount );


    public void updateReminderCount( List<Long> agents, List<String> customers );


    public List<SurveyDetails> getIncompleteSocialPostSurveys( long companyId );


    public String getMoodsToSendMail();


    public void increaseSurveyCountForAgent( long agentId ) throws SolrException, NoRecordsFetchedException,
        InvalidInputException;


    public void decreaseSurveyCountForAgent( long agentId ) throws SolrException, NoRecordsFetchedException,
        InvalidInputException;


    public String getGoogleShareUri();


    public String getSurveyUrl( long agentId, String customerEmail, String baseUrl ) throws InvalidInputException;


    public void markSurveyAsRetake( String surveyId, boolean editable , String requestSource );


    public void storeSPIandSendSurveyInvitationMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, boolean isAgent, String source ) throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException;


    public void sendSurveyRestartMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, String link ) throws InvalidInputException, UndeliveredEmailException,
        ProfileNotFoundException;


    public SurveyPreInitiation getPreInitiatedSurvey( long agentId, String customerEmail, String custFirstName,
        String custLastName );


    public SurveyPreInitiation getPreInitiatedSurvey( long surveyPreInitiationId );


    public void deleteSurveyPreInitiationDetailsPermanently( SurveyPreInitiation surveyPreInitiation );


    public String composeLink( long userId, String custEmail, String custFirstName, String custaLastName,
        long surveyPreInitiationId, boolean retakeSurvey ) throws InvalidInputException;


    public void markSurveyAsStarted( SurveyPreInitiation surveyPreInitiation );


    public void updateReminderCountForSocialPosts( Long agentId, String customerEmail );


    public Map<String, Object> mapAgentsInSurveyPreInitiation();


    public Map<String, Object> getReminderInformationForCompany( long companyId );


    public boolean checkSurveyReminderEligibility( long lastRemindedTime, long systemTime, int reminderInterval );


    void insertSurveyDetails( SurveyDetails surveyDetails );


    SurveyDetails getSurveyDetailsBySourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName );


    SurveyPreInitiation getPreInitiatedSurveyById( long surveyPreInitiationId ) throws NoRecordsFetchedException;


    /**
     * @param surveyPreInitiation
     */
    public SurveyPreInitiation saveSurveyPreInitiationObject( SurveyPreInitiation surveyPreInitiation ) throws InvalidInputException;


    void updateSurveyAsAbusive( String surveymongoId, String reporterEmail, String reporterName, String reportReason );


    /**
     * Sends survey request to the customer on behalf of agent.
     * @param agentId
     * @param recipientEmailId
     * @param recipientFirstname
     * @param recipientLastname
     * @param source
     * @throws DuplicateSurveyRequestException
     * @throws InvalidInputException
     * @throws SelfSurveyInitiationException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     * @throws UndeliveredEmailException
     * @throws ProfileNotFoundException 
     */
    public void initiateSurveyRequest( long agentId, String recipientEmailId, String recipientFirstname,
        String recipientLastname, String source ) throws DuplicateSurveyRequestException, InvalidInputException,
        SelfSurveyInitiationException, SolrException, NoRecordsFetchedException, UndeliveredEmailException,
        ProfileNotFoundException;


    //    Commented as Zillow surveys are not stored in database, SS-1276
    //    void deleteZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException;

    //  Commented as Zillow surveys are not stored in database, SS-1276
    //    void deleteExcessZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException;


    public List<AbusiveSurveyReportWrapper> getSurveysReportedAsAbusive( int startIndex, int numOfRows );


    void sendSurveyCompletionMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException;


    void sendSurveyCompletionUnpleasantMail( String custEmail, String custFirstName, String custLastName, User user )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException;


    void sendSocialPostReminderMail( String custEmail, String custFirstName, String custLastName, User user, String links )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException;


    public SocialMediaPostDetails getSocialMediaPostDetailsBySurvey( SurveyDetails surveyDetails,
        OrganizationUnitSettings companyUnitSettings, List<OrganizationUnitSettings> regionUnitSettings,
        List<OrganizationUnitSettings> branchUnitSettings );


    void updateSurveyDetails( SurveyDetails surveyDetails );


    public List<SurveyDetails> getSurveyDetailsByAgentAndCompany( long companyId );


    public Boolean canPostOnSocialMedia( OrganizationUnitSettings unitSetting, Double rating );


    /**
     * @param params
     * @return
     */
    public boolean validateDecryptedApiParams( Map<String, String> params );


    /**
     * @param bulkSurveyDetailList
     * @param companyId
     * @return
     */
    public List<BulkSurveyDetail> processBulkSurvey( List<BulkSurveyDetail> bulkSurveyDetailList, long companyId );


    void updateModifiedOnColumnForAgentHierachy( long agentId ) throws InvalidInputException;


    public void updateSurveyAsUnderResolution( String surveyId );


    public List<AbusiveSurveyReportWrapper> getSurveysReportedAsAbusive( long companyId, int startIndex, int numOfRows );


    public List<SurveyDetails> getSurveysUnderResolution( long companyId, int startIndex, int numOfRows );


    public void updateModifiedOnColumnForEntity( String collectionName, long entityId );


    /**
     * Returns array of swear words. Its used only for testing. Not for development
     * @return
     */
    public String[] getSwearList();


    public void updateSurveyAsUnAbusive( String surveyId );


    public SurveyDetails getSurveyDetails( String surveyMongoId );


    public double getFormattedSurveyScore( double surveyScore );


    public void moveSurveysToAnotherUser( long fromUserId, long toUserId ) throws InvalidInputException,
        NoRecordsFetchedException, SolrException;


    public void deleteExistingZillowSurveysByEntity( String entityType, long entityId ) throws InvalidInputException;


    /**
     * Method to import SurveyVO object into MySQL and mongo
     * @param surveyImportVO
     * @throws InvalidInputException
     * @throws ProfileNotFoundException
     */
    public void importSurveyVOToDBs( SurveyImportVO surveyImportVO, String source ) throws NonFatalException;


    public void updateZillowSummaryInExistingSurveyDetails( SurveyDetails surveyDetails );


    SurveyPreInitiation preInitiateSurvey( User user, String custEmail, String custFirstName, String custLastName, int i,
        String custRelationWithAgent, String source );


    void updateSurveyDetailsBySurveyId( SurveyDetails surveyDetails );


    public boolean hasCustomerAlreadySurveyed( long currentAgentId, String customerEmailId );

    void begin3rdPartySurveyImport();

    public String replaceGatewayQuestionText( String questionText, OrganizationUnitSettings agentSettings, User user,
        OrganizationUnitSettings companySettings, SurveyDetails survey, String logoUrl,
        Map<SettingsForApplication, OrganizationUnit> mapPrimaryHierarchy, OrganizationUnitSettings rSettings,
        OrganizationUnitSettings bSettings, Map<String, String> surveyMap ) throws InvalidInputException;


    public String getLogoUrl( User user, AgentSettings agentSettings );


    public String getLogoUrlWithSettings( User user, AgentSettings agentSettings, OrganizationUnitSettings companySettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings branchSettings,
        Map<SettingsForApplication, OrganizationUnit> map );


    public void updateSurveyStageForYelp( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage );


    public void updateSurveyStageForZillow( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage );


    public void updateSurveyStageForLendingTree( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage );


    public void updateSurveyStageForRealtor( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage );


    public void updateSurveyStageForGoogleBusinessToken( OrganizationUnitSettings unitSettings, BranchSettings branchSettings,
        OrganizationUnitSettings regionSettings, OrganizationUnitSettings companySettings, Map<String, Object> surveyAndStage );


    SurveyDetails getSurveyBySurveyPreIntitiationId( long surveyPreIntitiationId );


    SurveysAndReviewsVO getSurveysByFilterCriteria( String mood , Long startSurveyID, Date startReviewDate , Date startTransactionDate , List<Long> userIds , boolean isRetaken,  int startIndex, int count , long companyId);


    public void prepareAndSendInvitationMail( SurveyPreInitiation survey ) throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException;


    void sendSurveyReminderEmail( SurveyPreInitiation survey ) throws InvalidInputException, ProfileNotFoundException;
    
    public void updateSurveyTransactionDateInMongo();


    void updateZillowSourceIdInExistingSurveyDetails( SurveyDetails surveyDetails );


    void updateZillowSurveyUpdatedDateInExistingSurveyDetails( SurveyDetails surveyDetails );


    public List<SurveyPreInitiation> getSurveyListToSendInvitationMail( Company company, Date epochDate );


    Map<String, Date> getMinMaxLastSurveyReminderTime( long systemTime, int reminderInterval );

    void moveAllSurveysAlongWithUser( long agentId, long branchId, long regionId, long companyId ) throws InvalidInputException;


    void copyAllSurveysAlongWithUser( long agentId, long branchId, long regionId, long companyId ) throws InvalidInputException;


    void disconnectAllSurveysFromWithUser( long agentId ) throws InvalidInputException;


    List<SurveyPreInitiation> validatePreinitiatedRecord( List<SurveyPreInitiation> surveyPreInitiations , long companyId ) throws InvalidInputException;
    
    /**
     * method to build survey completion threshold Map
     * @param survey
     * @return Map
     * @throws InvalidInputException 
     * @throws NoRecordsFetchedException 
     */
    public Map<String, Double> buildSurveyCompletionThresholdMap( SurveyDetails survey ) throws InvalidInputException, NoRecordsFetchedException;


    public Map<String, String> buildPreferredAdminEmailListForSurvey( SurveyDetails survey, double companyThreshold,
        double regionThreshold, double branchThreshold ) throws InvalidInputException;



    public boolean createEntryForSurveyUploadWithCsv( String hierarchyType, MultipartFile tempFile, String fileName, long hierarchyId,
        User user, String uploaderEmail ) throws NonFatalException, IOException;


    public void processActiveSurveyCsvUploads();


    public boolean isFileAlreadyUploaded( String fileName, String uploaderEmail );


    public Integer getSurveysCountByFilterCriteria( String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken, long companyId );


    public Float getSurveysAvgScoreByFilterCriteria( String mood, Long startSurveyID, Date startReviewDate,
        Date startTransactionDate, List<Long> userIds, boolean isRetaken, long companyId );


    public SurveysAndReviewsVO getIncompelteSurveysByFilterCriteria( Long startSurveyID, Date startTransactionDate,
        List<Long> userIds, int startIndex, int count, long companyId );

	public void validateAndProcessSurveyPreInitiation(SurveyPreInitiation survey , int duplicateSurveyInterval) throws InvalidInputException;

	public void updateSurveyAsAbusiveNotify(String get_id);
	
	public String[] fetchSwearWords( String entityType, long entityId ) throws InvalidInputException;

    void updateSwearWords( String entityType, long entityId, String[] swearWords ) throws InvalidInputException;

    public void moveSurveyBetweenUsers( long surveyPreinitiationId, long toUserId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;

}