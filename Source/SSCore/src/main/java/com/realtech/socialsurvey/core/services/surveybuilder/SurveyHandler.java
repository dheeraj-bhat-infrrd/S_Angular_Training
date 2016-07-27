package com.realtech.socialsurvey.core.services.surveybuilder;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.SurveyImportVO;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import org.apache.solr.client.solrj.SolrServerException;

import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.BulkSurveyDetail;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;
import org.springframework.transaction.annotation.Transactional;


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
    /*
     * Method to update answers to all the questions and current stage in MongoDB.
     * @param agentId
     * @param customerEmail
     * @param question
     * @param answer
     * @param stage
     * @throws Exception
     */
    public void updateCustomerAnswersInSurvey( String surveyId, String question, String questionType, String answer,
        int stage );


    /*
     * Method to update customer review and final score on the basis of rating questions in
     * SURVEY_DETAILS.
     */
    public void updateGatewayQuestionResponseAndScore( String surveyId, String mood, String review, boolean isAbusive,
        String agreedToShare );


    public SurveyDetails storeInitialSurveyDetails( long agentId, String customerEmail, String firstName, String lastName,
        int reminderCount, String custRelationWithAgent, String url, String source, String state, String city,
        long surveyPreIntitiationId, boolean isOldRecord, boolean retakeSurvey )
        throws SolrException, NoRecordsFetchedException, InvalidInputException;


    public SurveyDetails getSurveyDetails( long agentId, String customerEmail, String firstName, String lastName );


    public String getApplicationBaseUrl();


    public void updateSurveyAsClicked( String surveyMongoId );


    public String getSwearWords();


    public void updateReminderCount( long surveyPreInitiationId, boolean reminder );


    public void markSurveyAsSent( SurveyPreInitiation surveyPreInitiation );


    public Map<String, String> getEmailIdsOfAdminsInHierarchy( long agentId ) throws InvalidInputException;


    public List<SurveyPreInitiation> getIncompleteSurveyCustomersEmail( Company company );


    public void updateReminderCount( List<Long> agents, List<String> customers );


    public List<SurveyDetails> getIncompleteSocialPostSurveys( long companyId );


    public String getMoodsToSendMail();


    public void increaseSurveyCountForAgent( long agentId )
        throws SolrException, NoRecordsFetchedException, InvalidInputException;


    public void decreaseSurveyCountForAgent( long agentId )
        throws SolrException, NoRecordsFetchedException, InvalidInputException;


    public String getGoogleShareUri();


    public String getSurveyUrl( long agentId, String customerEmail, String baseUrl ) throws InvalidInputException;


    public void changeStatusOfSurvey( String surveyId, boolean editable );


    public void sendSurveyInvitationMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, boolean isAgent, String source ) throws InvalidInputException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException;


    public void sendSurveyRestartMail( String custFirstName, String custLastName, String custEmail,
        String custRelationWithAgent, User user, String link )
        throws InvalidInputException, UndeliveredEmailException, ProfileNotFoundException;


    public SurveyPreInitiation getPreInitiatedSurvey( long agentId, String customerEmail, String custFirstName,
        String custLastName );


    public SurveyPreInitiation getPreInitiatedSurvey( long surveyPreInitiationId );


    public void deleteSurveyPreInitiationDetailsPermanently( SurveyPreInitiation surveyPreInitiation );


    public String composeLink( long userId, String custEmail, String custFirstName, String custaLastName,
        long surveyPreInitiationId, boolean retakeSurvey ) throws InvalidInputException;


    public void markSurveyAsStarted( SurveyPreInitiation surveyPreInitiation );


    public void updateReminderCountForSocialPosts( Long agentId, String customerEmail );


    public Map<String, Object> mapAgentsInSurveyPreInitiation();


    public Map<String, Integer> getReminderInformationForCompany( long companyId );


    public boolean checkSurveyReminderEligibility( long lastRemindedTime, long systemTime, int reminderInterval );


    void insertSurveyDetails( SurveyDetails surveyDetails );


    SurveyDetails getSurveyDetailsBySourceIdAndMongoCollection( String surveySourceId, long iden, String collectionName );


    SurveyPreInitiation getPreInitiatedSurveyById( long surveyPreInitiationId ) throws NoRecordsFetchedException;


    /**
     * @param surveyPreInitiation
     */
    public void saveSurveyPreInitiationObject( SurveyPreInitiation surveyPreInitiation ) throws InvalidInputException;


    void updateSurveyAsAbusive( String surveymongoId, String reporterEmail, String reporterName );


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
        String recipientLastname, String source )
        throws DuplicateSurveyRequestException, InvalidInputException, SelfSurveyInitiationException, SolrException,
        NoRecordsFetchedException, UndeliveredEmailException, ProfileNotFoundException;

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


    public void moveSurveysToAnotherUser( long fromUserId, long toUserId )
        throws InvalidInputException, NoRecordsFetchedException, SolrException;


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


    public String replaceGatewayQuestionText( String questionText, OrganizationUnitSettings agentSettings, User user,
        OrganizationUnitSettings companySettings, SurveyDetails survey ) throws InvalidInputException;
}