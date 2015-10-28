package com.realtech.socialsurvey.web.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.web.common.JspResolver;


@Controller
@RequestMapping ( value = "/widget")
public class WidgetController
{
    private static final Logger LOG = LoggerFactory.getLogger( WidgetController.class );

    private static final String PROFILE_TYPE_COMPANY = "company";
    private static final String PROFILE_TYPE_REGION = "region";
    private static final String PROFILE_TYPE_BRANCH = "branch";
    private static final String PROFILE_TYPE_INDIVIDUAL = "individual";
    private static final int WIDGET_MAX_REVIEWS = 3;

    @Autowired
    OrganizationManagementService organizationManagementService;

    @Autowired
    ProfileManagementService profileManagementService;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;


    /**
     * Method to show widget jsp in the iframe
     * 
     * @param profileType
     * @param iden
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    @RequestMapping ( value = "/{profileType}/{iden}", method = RequestMethod.GET)
    public String fetchWidget( @PathVariable String profileType, @PathVariable long iden, Model model,
        HttpServletRequest request, RedirectAttributes redirectAttributes ) throws InvalidInputException,
        NoRecordsFetchedException
    {
        LOG.info( "Fetching widget data for profile type : " + profileType + " and id : " + iden );
        long reviewsCount = 0;
        double averageRating = 0.0;
        double minScore = 0.0;
        List<SurveyDetails> surveys = null;
        if ( profileType == null || profileType.isEmpty() ) {
            LOG.error( "Invalid profileType : " + profileType );
        }
        if ( iden <= 0l ) {
            LOG.error( "Invalid iden : " + iden );
        }
        /*
         * Get average rating, review count and surveys for the specified profile
         */
        //If profile is of type company
        if ( profileType.equals( PROFILE_TYPE_COMPANY ) ) {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( iden );
            //Get auto-post score
            if ( companySettings.getSurvey_settings() != null
                && companySettings.getSurvey_settings().getAuto_post_score() >= 0.0 ) {
                minScore = companySettings.getSurvey_settings().getShow_survey_above_score();
            }
            averageRating = profileManagementService.getAverageRatings( iden, CommonConstants.PROFILE_LEVEL_COMPANY, false );
            reviewsCount = profileManagementService.getReviewsCount( iden, CommonConstants.MIN_RATING_SCORE,
                CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_COMPANY, false, false );
            surveys = profileManagementService.getReviews( iden, minScore, CommonConstants.MAX_RATING_SCORE, -1,
                WIDGET_MAX_REVIEWS, CommonConstants.PROFILE_LEVEL_COMPANY, false, null, null,
                CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
            model.addAttribute( "profile", companySettings );
            //If profile is of type region
        } else if ( profileType.equals( PROFILE_TYPE_REGION ) ) {
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( iden );
            //Get auto-post score
            if ( regionSettings.getSurvey_settings() != null && regionSettings.getSurvey_settings().getAuto_post_score() >= 0.0 ) {
                minScore = regionSettings.getSurvey_settings().getShow_survey_above_score();
            }
            averageRating = profileManagementService.getAverageRatings( iden, CommonConstants.PROFILE_LEVEL_REGION, false );
            reviewsCount = profileManagementService.getReviewsCount( iden, CommonConstants.MIN_RATING_SCORE,
                CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_REGION, false, false );
            surveys = profileManagementService.getReviews( iden, minScore, CommonConstants.MAX_RATING_SCORE, -1,
                WIDGET_MAX_REVIEWS, CommonConstants.PROFILE_LEVEL_REGION, false, null, null,
                CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
            model.addAttribute( "profile", regionSettings );
            //If profile is of type branch
        } else if ( profileType.equals( PROFILE_TYPE_BRANCH ) ) {
            OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( iden );
            //Get auto-post score
            if ( branchSettings.getSurvey_settings() != null && branchSettings.getSurvey_settings().getAuto_post_score() >= 0.0 ) {
                minScore = branchSettings.getSurvey_settings().getShow_survey_above_score();
            }
            averageRating = profileManagementService.getAverageRatings( iden, CommonConstants.PROFILE_LEVEL_BRANCH, false );
            reviewsCount = profileManagementService.getReviewsCount( iden, CommonConstants.MIN_RATING_SCORE,
                CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_BRANCH, false, false );
            surveys = profileManagementService.getReviews( iden, minScore, CommonConstants.MAX_RATING_SCORE, -1,
                WIDGET_MAX_REVIEWS, CommonConstants.PROFILE_LEVEL_BRANCH, false, null, null,
                CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
            model.addAttribute( "profile", branchSettings );
            //If profile is of type individual
        } else if ( profileType.equals( PROFILE_TYPE_INDIVIDUAL ) ) {
            OrganizationUnitSettings agentSettings = organizationManagementService.getAgentSettings( iden );
            //Get auto-post score
            if ( agentSettings.getSurvey_settings() != null && agentSettings.getSurvey_settings().getAuto_post_score() >= 0.0 ) {
                minScore = agentSettings.getSurvey_settings().getShow_survey_above_score();
            }
            averageRating = profileManagementService.getAverageRatings( iden, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false );
            reviewsCount = profileManagementService.getReviewsCount( iden, CommonConstants.MIN_RATING_SCORE,
                CommonConstants.MAX_RATING_SCORE, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, false );
            surveys = profileManagementService.getReviews( iden, minScore, CommonConstants.MAX_RATING_SCORE, -1,
                WIDGET_MAX_REVIEWS, CommonConstants.PROFILE_LEVEL_INDIVIDUAL, false, null, null,
                CommonConstants.REVIEWS_SORT_CRITERIA_FEATURE );
            model.addAttribute( "profile", agentSettings );
        } else {
            throw new InvalidInputException( "Invalid profileType : " + profileType );
        }
        model.addAttribute( "profileLevel", profileType.toUpperCase() );
        model.addAttribute( "averageRating", averageRating );
        model.addAttribute( "reviewsCount", reviewsCount );
        model.addAttribute( "surveys", surveys );
        LOG.info( "Finished fetching widget data for profile type : " + profileType + " and id : " + iden );
        return JspResolver.WIDGET_PAGE;
    }
}
