package com.realtech.socialsurvey.web.api;


import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.MultiplePhrasesVO;
import com.realtech.socialsurvey.core.entities.SocialFeedsActionUpdate;
import com.realtech.socialsurvey.core.entities.SocialMonitorMacro;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.web.api.entities.AccountRegistrationAPIRequest;
import com.realtech.socialsurvey.web.api.entities.CaptchaAPIRequest;
import com.realtech.socialsurvey.web.api.entities.VendastaRmCreateRequest;
import com.realtech.socialsurvey.web.entities.CompanyProfile;
import com.realtech.socialsurvey.web.entities.Payment;
import com.realtech.socialsurvey.web.entities.PersonalProfile;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;


public interface SSApiIntegration
{
    @POST ( "/v1/nocaptcha")
    Response validateCaptcha( @Body CaptchaAPIRequest captchaRequest );


    @POST ( "/v1/companies/register")
    Response initateRegistration( @Body AccountRegistrationAPIRequest registrationRequest );


    @GET ( "/v1/users/{userId}")
    Response getUserProfile( @Path ( "userId") String userId );


    @PUT ( "/v1/users/{userId}")
    Response updateUserProfile( @Body PersonalProfile personalProfile, @Path ( "userId") String userId );


    @PUT ( "/v1/users/{userId}/stage/{stage}")
    Response updateUserProfileStage( @Path ( "userId") String userId, @Path ( "stage") String stage );


    @GET ( "/v1/companies/{companyId}")
    Response getCompanyProfile( @Path ( "companyId") String companyId );


    @PUT ( "/v1/companies/{companyId}")
    Response updateCompanyProfile( @Body CompanyProfile companyProfile, @Path ( "companyId") String companyId,
        @Query ( "userId") String userId );


    @PUT ( "/v1/companies/{companyId}/stage/{stage}")
    Response updateCompanyProfileStage( @Path ( "companyId") String companyId, @Path ( "stage") String stage );


    @GET ( "/v1/industries")
    Response getVerticals();


    @GET ( "/v1/payment/plans")
    Response getPaymentPlans();


    @GET ( "/v1/users/{userId}/stage")
    Response getUserStage( @Path ( "userId") String userId );


    @GET ( "/v1/companies/{companyId}/stage")
    Response getCompanyStage( @Path ( "companyId") String companyId );


    @PUT ( "/v1/companies/{companyId}/profileimage")
    Response updateCompanyLogo( @Path ( "companyId") String companyId, @Query ( "userId") String userId, @Body String logoUrl );


    @DELETE ( "/v1/companies/{companyId}/profileimage")
    Response removeCompanyLogo( @Path ( "companyId") String companyId, @Query ( "userId") String userId );


    @PUT ( "/v1/users/{userId}/profileimage")
    Response updateUserProfileImage( @Path ( "userId") String userId, @Body String imageUrl );


    @DELETE ( "/v1/users/{userId}/profileimage")
    Response removeUserProfileImage( @Path ( "userId") String userId );


    @POST ( "/v1/companies/{companyId}/hierarchy")
    Response generateDefaultHierarchy( @Path ( "companyId") String companyId );


    @POST ( "/v1/companies/{companyId}/plan/{planId}/payment")
    Response makePayment( @Path ( "companyId") String companyId, @Path ( "planId") String planId, @Body Payment payment );


    @PUT ( "/v1/users/{userId}/password")
    Response savePassword( @Path ( "userId") String userId, @Body String password );


    @GET ( "/v1/usstates")
    Response getUsStates();


    @POST ( "/v1/webaddress")
    Response validateWebAddress( @Body String webAddress );

    //vendasta: BEGIN
    
    @POST ( "/v1/vendasta/rm/account/create" )
    Response createVendastaRmAccount( @Body VendastaRmCreateRequest accountDetails, @Query ( "isForced" ) boolean isForced );
    
    //vendasta: END
    
    //reporting: BEGIN
    
    @GET ( "/v1/getcompletionrate" )
    Response getReportingCompletionRateApi(@Query ("entityId") Long entityId , @Query ("entityType") String entityType);
    
    
    @GET ( "/v1/getspsstats" )
    Response getReportingSpsStats(@Query ("entityId") Long entityId , @Query ("entityType") String entityType);
    
    @GET("/v1/getspsfromoverview")
    Response getSpsStatsFromOverview(@Query ("entityId") Long entityId , @Query ("entityType") String entityType);
 
    @GET("/v1/getalltimefromoverview")
    Response getAllTimeDataOverview(@Query ("entityId") Long entityId , @Query ("entityType") String entityType);
 
    @GET("/v1/getrecentactivityforreporting")
    Response getRecentActivity(@Query ("entityId") Long entityId , @Query ("entityType") String entityType ,@Query ("startIndex") int startIndex , @Query ("batchSize") int batchSize);
    
    @GET("/v1/getmonthdataoverviewfordashboard")
    Response getMonthDataOverviewForDashboard(@Query ("entityId") Long entityId , @Query ("entityType") String entityType ,@Query ("month") int month , @Query ("year") int year);
    
    @GET("/v1/getyeardataoverviewfordashboard")
    Response getYearDataOverviewForDashboard(@Query ("entityId") Long entityId , @Query ("entityType") String entityType ,@Query ("year") int year);
    
    @GET("/v1/getuserrankingforthisyear")
    Response getUserRankingForThisYear(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("year") int year, @Query ("startIndex") int startIndex, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingforpastyear")
    Response getUserRankingForPastYear(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("year") int year, @Query ("startIndex") int startIndex, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingforthismonth")
    Response getUserRankingForThisMonth(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("month") int month, @Query ("year") int year, @Query ("startIndex") int startIndex, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingforpastmonth")
    Response getUserRankingForPastMonth(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("month") int month, @Query ("year") int year, @Query ("startIndex") int startIndex, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingforpastyears")
    Response getUserRankingForPastYears(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("startIndex") int startIndex, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingrankcountthisyear")
    Response getUserRankingRankCountForThisYear(@Query ("userId") Long userId, @Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingrankcountthismonth")
    Response getUserRankingRankCountForThisMonth(@Query ("userId") Long userId, @Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("month") int month, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingrankcountpastyears")
    Response getUserRankingRankCountForPastYears(@Query ("userId") Long userId, @Query ("entityId") Long entityId, @Query ("entityType") String entityType,  @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingrankcountpastmonth")
    Response getUserRankingRankCountForPastMonth(@Query ("userId") Long userId, @Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("month") int month, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingrankcountpastyear")
    Response getUserRankingRankCountForPastYear(@Query ("userId") Long userId, @Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingcountthisyear")
    Response getUserRankingCountForThisYear(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingcountthismonth")
    Response getUserRankingCountForThisMonth(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("month") int month, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingcountpastyear")
    Response getUserRankingCountForPastYear(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingcountpastmonth")
    Response getUserRankingCountForPastMonth(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("month") int month, @Query ("year") int year, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getuserrankingcountpastyears")
    Response getUserRankingCountForPastYears(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("batchSize") int batchSize);
    
    @GET("/v1/getscorestatsoverall")
    Response getScoreStatsOverall(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("currentMonth") int currentMonth, @Query ("currentYear") int currentYear);
    
    @GET("/v1/getscorestatsquestion")
    Response getScoreStatsQuestion(@Query ("entityId") Long entityId, @Query ("entityType") String entityType, @Query ("currentMonth") int currentMonth, @Query ("currentYear") int currentYear);
    
    @GET("/v1/getaccountstatisticsreportstatus")
	Response getAccountStatisticsRecentActivity(@Query("reportId") Long reportId);
    //reporting:END
    
    @POST("/v2/addquestiontosurvey")
	Response addQuestionToExistingSurvey(@Body SurveyQuestionDetails questionDetails);

    @PUT("/v2/updatesurveyquestion")
	Response updateQuestionFromExistingSurvey(@Body SurveyQuestionDetails questionDetails);
    
    //survey api's 
    @POST("/v2/surveys/{surveyId}/response")
    Response updateSurveyResponse(@Path ("surveyId") String surveyId, @Query ("question") String question, @Query ("questionType") String questionType, @Query ("answer") String answer, @Query ("stage") int stage,
        @Query ("isUserRankingQuestion") boolean isUserRankingQuestion, @Query ("isNpsQuestion") boolean isNpsQuestion, @Query ("questionId") int questionId, @Query ("considerForScore") boolean considerForScore  );
    
    @POST("/v2/surveys/{surveyId}/score")
    Response updateScore(@Path ("surveyId") String surveyId, @Query ("mood") String mood, @Query ("feedback") String feedback,
        @Query ("isAbusive") boolean isAbusive, @Query ("agreedToShare") String agreedToShare  );
    
    @GET("/v2/swearwords")
    Response getSwearWordsList(@Query ("companyId") long companyId);

    @DELETE("/v2/removesurveyquestion")
	Response removeQuestionFromSurvey(@Query("userId") long userId,@Query("surveyQuestionId") long surveyQuestionId);
    
    @GET ( "/v1/npsstats" )
    Response getReportingNpsStats(@Query ("entityId") Long entityId , @Query ("entityType") String entityType);
    
    @GET ( "/v1/lastsuccessfuletltime" )
    Response getLastSuccessfulEtlTimeApi();
    
    @GET("/v1/companies/{companyId}/keywords")
    public Response getCompanyKeywords(@Path ( "companyId") long companyId, @Query("startIndex") int startIndex, @Query("limit") int limit,  @Query("monitorType") String monitorType, @Query("searchPhrase") String searchPhrase, @Header("authorizationHeader") String authorizationHeader);
    
    @POST("/v1/companies/{companyId}/keywords")
    public Response addKeywordsToCompany(@Path ( "companyId") long companyId, @Body List<Keyword> keywordsRequest);
    
	@GET("/v1/showsocialfeeds")
	public Response showStreamSocialPosts(@Query("startIndex") int startIndex, @Query("limit") int limit,
			@Query("status") String status, @Query("flag") boolean flag, @Query("feedtype") List<String> feedtype,
			@Query("companyId") Long companyId, @Query("regionIds") List<Long> regionIds,
			@Query("branchIds") List<Long> branchIds, @Query("agentIds") List<Long> agentIds, @Query("searchText") String searchText,@Query("isCompanySet") boolean isCompanySet, @Header("authorizationHeader") String authorizationHeader);
    
    @PUT( "/v1/updatesocialfeeds/action")
    public Response saveSocialFeedsForAction(@Body SocialFeedsActionUpdate socialFeedsActionUpdate,
			@Query("companyId") Long companyId, @Query("duplicateFlag") boolean duplicateFlag, @Header("authorizationHeader") String authorizationHeader);
    
    @GET("/v1/socialfeedsmacro/company/{companyId}")
    public Response showMacrosForEntity(@Path("companyId") long companyId, @Query("searchMacros") String searchMacros, @Header("authorizationHeader") String authorizationHeader);
    
    @POST("/v1/update/socialfeedsmacro")
    public Response updateMacrosForEntity(@Body SocialMonitorMacro socialMonitorMacro, @Query("companyId") long companyId, @Header("authorizationHeader") String authorizationHeader);
    
    @GET("/v1/socialfeedsmacro/company/{companyId}/macro/{macroId}")
    public Response getMacroById(@Path("companyId") Long companyId, @Path("macroId") long macroId, @Header("authorizationHeader") String authorizationHeader);
    
    @GET("/v1/segments/company/{companyId}")
    public Response getSegmentsByCompanyId(@Path("companyId") Long companyId, @Header("authorizationHeader") String authorizationHeader);
    
    @GET("/v1/users/company/{companyId}")
    public Response getUsersByCompanyId(@Path("companyId") Long companyId, @Header("authorizationHeader") String authorizationHeader);
    
    @POST("/v1/company/{companyId}/keyword")
    public Response addKeywordToCompany(@Path ( "companyId") long companyId, @Body Keyword keywordsRequest, @Header("authorizationHeader") String authorizationHeader);
    
    @POST("/v1/company/{companyId}/keyword/phrases")
    public Response addMultiplePhrasesToCompany(@Path ( "companyId") long companyId, @Body MultiplePhrasesVO multiplePhrasesVO, @Header("authorizationHeader") String authorizationHeader);
    
    @DELETE ( "/v1/company/{companyId}/keywords")
    public Response deleteKeywordsFromCompany( @Path ( "companyId") long companyId,
        @Query ( "keywordIds") List<String> keywordIds, @Header("authorizationHeader") String authorizationHeader );
    
    @GET("/v1/feedtypes/company/{companyId}")
    public Response getFeedTypesByCompanyId(@Path("companyId") Long companyId, @Header("authorizationHeader") String authorizationHeader);
    
    @GET ( "/v1/lastsuccessfuletltime/isetlrunning" )
    Response isEtlRunning();
    
    @GET ( "/v1/lastsuccessfuletltime/{entityId}/{entityType}" )
    Response lastRunForEntity(@Path ("entityId") long entityId,@Path ("entityType") String entityType);
    
    @GET ( "/v1/lastsuccessfuletltime/recal/{entityId}/{entityType}" )
    Response recalUserRanking(@Path ("entityId") long entityId,@Path ("entityType") String entityType);
    
    @POST( "/v1/updateabusivemail" )
    Response updateAbusiveMail(@Query ("entityId") long entityId,@Query ("mailId") String mailId);
    
    @POST( "/v1/unsetabusivemail" )
    Response unsetAbusiveMail(@Query ("entityId") long entityId);
    
    @POST( "/v1/unsetcompres" )
    Response unsetCompRes(@Query ("entityId") long entityId);

    @POST("/v1/unsetwebadd")
    Response unsetWebAdd(@Query ("entityId") long entityId , @Query ("entityType") String entityType);

    @GET( "/v1/userprofileflags" )
    Response getUserProfileFlags(@Query ("userId") long userId);
    
    @GET("/v1/getrecentactivityforsocialmonitorreporting")
    Response getRecentActivityForSocialMonitor(@Query ("entityId") Long entityId , @Query ("entityType") String entityType ,@Query ("startIndex") int startIndex , @Query ("batchSize") int batchSize);

    @POST( "/v1/sethasregisteredforsummit" )
    Response setHasRegisteredForSummit(@Query("companyId") Long companyId,@Query("hasRegisteredForSummit") boolean hasRegisteredForSummit);
    
    @POST( "/v1/setshowsummitpopup" )
    Response setShowSummitPopup(@Query("companyId") Long companyId,@Query("isShowSummitPopup") boolean isShowSummitPopup);
    
}