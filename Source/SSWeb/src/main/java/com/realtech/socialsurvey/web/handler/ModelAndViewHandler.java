package com.realtech.socialsurvey.web.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.realtech.socialsurvey.core.commons.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.PublicProfileAggregate;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InternalServerException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.ProfileRedirectionException;
import com.realtech.socialsurvey.core.exception.ProfileServiceErrorCode;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.BotRequestUtils;


@Component
public class ModelAndViewHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( ModelAndViewHandler.class );

    private static final String PROFILE_JSON = "profileJson";

    private static final String REVIEW_SORT_CRITERIA = "reviewSortCriteria";

    private static final String AVG_RATING = "averageRating";

    private static final String REVIEWS_COUNT = "reviewsCount";

    private static final String REVIEWS = "reviews";

    private static final String PROFILE = "profile";

    private static final String COMPANY_PROFILE_NAME = "companyProfileName";

    private static final String PROFILE_LEVEL = "profileLevel";

    private static final String FIND_A_PRO_COMPANY_PROFILE_NAME = "findProCompanyProfileName";

    private static final String REGION_PROFILE_NAME = "regionProfileName";

    private static final String BRANCH_PROFILE_NAME = "branchProfileName";

    private static final String AGENT_PROFILE_NAME = "agentProfileName";

    private static final String REVIEW_AGGREGATE = "reviewAggregate";

    private static final String PROFILE_URL = "pageUrl";
    
    private static final String COMPANY_NAME = "companyName";
    
    private static final String COMPANY_NAME_FOR_TITLE = "companyNameForTitle";
    
    private static final String ADD_PHOTOS_TO_REVIEW = "addPhotosToReview";

    @Autowired
    private BotRequestUtils botRequestUtils;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Value( "${AMAZON_ENV_PREFIX}" )
    private String enviromment;

    @Value( "${AMAZON_ENDPOINT}" )
    private String amazonEndpoint;

    @Value( "${AMAZON_BUCKET}" )
    private String amazonBucket;

    public String handlePublicProfileModelAndView( Model model, PublicProfileAggregate profileAggregate, boolean isBotRequest )
        throws ProfileNotFoundException
    {

        LOG.debug( "method publicProfileModelAndViewHandler() started." );

        if(enviromment.equalsIgnoreCase( "P" ) &&  profileAggregate.getProfile() != null &&
            !StringUtils.isEmpty( profileAggregate.getProfile().getProfileImageUrlThumbnail() )){
            profileAggregate.getProfile().setProfileImageUrlThumbnail(
                Utils.convertCloudFrontUrlToS3Url(profileAggregate.getProfile().getProfileImageUrlThumbnail(), amazonEndpoint, amazonBucket));
        }

        model.addAttribute( REVIEWS_COUNT, profileAggregate.getReviewCount() );
        model.addAttribute( PROFILE_JSON, profileAggregate.getProfileJson() );
        model.addAttribute( AVG_RATING, profileAggregate.getAverageRating() );
        model.addAttribute( REVIEW_SORT_CRITERIA, profileAggregate.getReviewSortCriteria() );
        model.addAttribute( REVIEWS, profileAggregate.getReviews() );
        model.addAttribute( PROFILE, profileAggregate.getProfile() );
        model.addAttribute( PROFILE_LEVEL, profileAggregate.getProfileLevel() );
        model.addAttribute( FIND_A_PRO_COMPANY_PROFILE_NAME, profileAggregate.getFindAProCompanyProfileName() );
        model.addAttribute( COMPANY_PROFILE_NAME, profileAggregate.getCompanyProfileName() );
        model.addAttribute( PROFILE_URL, profileAggregate.getProfileUrl() );
        model.addAttribute( COMPANY_NAME, profileAggregate.getCompanyName() );
        model.addAttribute( COMPANY_NAME_FOR_TITLE, profileAggregate.getCompanyName() );
        model.addAttribute( ADD_PHOTOS_TO_REVIEW, profileAggregate.isAddPhototsToReview() );

        if ( CommonConstants.PROFILE_LEVEL_REGION.equals( profileAggregate.getProfileLevel() ) ) {
            model.addAttribute( REGION_PROFILE_NAME, profileAggregate.getProfileName() );
        } else if ( CommonConstants.PROFILE_LEVEL_BRANCH.equals( profileAggregate.getProfileLevel() ) ) {
            model.addAttribute( BRANCH_PROFILE_NAME, profileAggregate.getProfileName() );
        } else if ( CommonConstants.PROFILE_LEVEL_INDIVIDUAL.equals( profileAggregate.getProfileLevel() ) ) {
            model.addAttribute( AGENT_PROFILE_NAME, profileAggregate.getProfileName() );
        } else if ( !CommonConstants.PROFILE_LEVEL_COMPANY.equals( profileAggregate.getProfileLevel() ) ) {
            LOG.error( "Profile level is invalid" );
            throw new ProfileNotFoundException( "Please specify a valid profile level." );
        }

        // individual review information
        model.addAttribute( REVIEW_AGGREGATE, profileAggregate.getReviewAggregate() );

        LOG.debug( "method publicProfileModelAndViewHandler() finished." );
        return isBotRequest ? JspResolver.PROFILE_PAGE_NOSCRIPT : JspResolver.PROFILE_PAGE;

    }


    public boolean isItABotRequest( HttpServletRequest request )
    {
        return botRequestUtils.checkBotRequest( request.getHeader( BotRequestUtils.USER_AGENT_HEADER ) );
    }


    public String handlePublicProfileContactUsMailModelAndView( Object conclusion )
    {
        LOG.debug( " method handlePublicProfileContactUsMailModelAndView() started." );
        String returnMessage = null;

        if ( conclusion instanceof String ) {

            if ( CommonConstants.SUCCESS_ATTRIBUTE.equals( (String) conclusion ) ) {
                messageUtils
                    .getDisplayMessage( DisplayMessageConstants.CONTACT_US_MESSAGE_SENT, DisplayMessageType.SUCCESS_MESSAGE )
                    .toString();
            } else if ( CommonConstants.INVALID_CAPTCHA.equals( (String) conclusion ) ) {
                returnMessage = messageUtils
                    .getDisplayMessage( DisplayMessageConstants.INVALID_CAPTCHA, DisplayMessageType.ERROR_MESSAGE ).toString();
            }

        } else if ( conclusion instanceof Exception ) {
            returnMessage = messageUtils
                .getDisplayMessage( DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE )
                .toString();
        }

        LOG.debug( " method handlePublicProfileContactUsMailModelAndView() finished." );
        return returnMessage;

    }


    public String handlePublicProfileExceptionsModelAndView( PublicProfileAggregate profileAggregate, int errorCode,
        int serviceId, Exception error, Model model, RedirectAttributes redirectAttributes, HttpServletResponse response )
    {
        LOG.debug( "method handlePublicProfileExceptionsModelAndView() started." );
        String returnTemplate = null;

        if ( error instanceof ProfileRedirectionException ) {

            LOG.debug( "method handlePublicProfileExceptionsModelAndView() redirecting: ...." );
            returnTemplate = redirectToAnotherPublicProfile( profileAggregate, response, model );

        } else if ( error instanceof InvalidInputException ) {

            LOG.warn( "InvalidInputException caught while fetching {} profile : {}", profileAggregate.getProfileLevel(),
                profileAggregate.getProfileName() );
            throw new InternalServerException(
                new ProfileServiceErrorCode( errorCode, serviceId, "InvalidInputException occured while fetching "
                    + profileAggregate.getProfileLevel() + " profile: " + profileAggregate.getProfileName() ),
                error.getMessage(), error );

        } else if ( error instanceof InvalidSettingsStateException ) {

            LOG.warn( "InvalidSettingsStateException caught while fetching {} profile : {}", profileAggregate.getProfileLevel(),
                profileAggregate.getProfileName() );
            throw new InternalServerException(
                new ProfileServiceErrorCode( errorCode, serviceId, "InvalidSettingsStateException occured while fetching "
                    + profileAggregate.getProfileLevel() + " profile: " + profileAggregate.getProfileName() ),
                error.getMessage(), error );

        } else if ( error instanceof ProfileNotFoundException ) {

            LOG.error( "ProfileNotFoundException caught while fetching {} profile : {}", profileAggregate.getProfileLevel(),
                error.getMessage() );
            
            returnTemplate = JspResolver.SS_PAGE_NOT_FOUND;
           /* Map<String, String> nameMap = profileManagementService
                .findNamesfromProfileName( profileAggregate.getProfileName() );
            redirectAttributes.addFlashAttribute( CommonConstants.PATTERN_FIRST, nameMap.get( CommonConstants.PATTERN_FIRST ) );
            redirectAttributes.addFlashAttribute( CommonConstants.PATTERN_LAST, nameMap.get( CommonConstants.PATTERN_LAST ) );
            returnTemplate = "redirect:/" + JspResolver.FINDAPRO + ".do";*/

        } else if ( error instanceof NonFatalException ) {

            LOG.error( "NonFatalException caught : {}", error.getMessage(), error );
            model.addAttribute( "message", messageUtils
                .getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ).getMessage() );
            returnTemplate = JspResolver.MESSAGE_HEADER;

        } else {

            LOG.warn( "Unhandled exception", error );
            throw new InternalServerException(
                new ProfileServiceErrorCode( errorCode, serviceId, "Unknown Error occured while fetching "
                    + profileAggregate.getProfileLevel() + " profile:  " + profileAggregate.getProfileName() ),
                error.getMessage(), error );
        }


        if ( StringUtils.isEmpty( returnTemplate ) ) {
            returnTemplate = JspResolver.NOT_FOUND_PAGE;
        }

        LOG.debug( "method handlePublicProfileExceptionsModelAndView() finished." );
        return returnTemplate;
    }


    private String redirectToAnotherPublicProfile( PublicProfileAggregate profileAggregate, HttpServletResponse response,
        Model model )
    {
        try {
            response.sendRedirect( profileManagementService.publicProfileRedirection( profileAggregate ) );
            return null;

        } catch ( Exception redirectionException ) {

            LOG.error( "IOException caught while redirecting from {} profile : ", profileAggregate.getProfileLevel(),
                profileAggregate.getProfileName(), redirectionException );
            model.addAttribute( "message", messageUtils
                .getDisplayMessage( DisplayMessageConstants.GENERAL_ERROR, DisplayMessageType.ERROR_MESSAGE ).getMessage() );
            return JspResolver.MESSAGE_HEADER;
        }

    }
}
