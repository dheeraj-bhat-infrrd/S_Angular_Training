package com.realtech.socialsurvey.web.rest;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.web.common.JspResolver;


@Controller
@RequestMapping ( value = "/user")
public class UserManagementRestController
{
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    private static final Logger LOG = LoggerFactory.getLogger( UserManagementRestController.class );


    @RequestMapping ( value = "/enableuserlogin/{userId}", method = RequestMethod.GET)
    public String enableUserLogin( Model model, @PathVariable long userId, HttpServletResponse response,
        RedirectAttributes redirectAttributes )
    {
        LOG.info( "Login controller called for enabling user login" );
        User user = null;
        try {
            if ( userId <= 0l ) {
                LOG.error( "Invalid userId: {}", userId );
                throw new InvalidInputException( "Invalid userId passed" );
            }
            user = userManagementService.getUserByUserId( userId );

            organizationManagementService.updateIsLoginPreventedForUser( user.getUserId(), false );
            // update hidePublicPage as well
            organizationManagementService.updateHidePublicPageForUser( user.getUserId(), false );
            //update social media tokens
            organizationManagementService.updateSocialMediaForUser( user.getUserId(), false );
            if ( user.getStatus() == CommonConstants.STATUS_NOT_VERIFIED ) {
                String profileName = userManagementService.getUserSettings( user.getUserId() ).getProfileName();
                userManagementService.sendRegistrationCompletionLink( user.getEmailId(), user.getFirstName(),
                    user.getLastName(), user.getCompany().getCompanyId(), profileName, user.getLoginName(), false );
                model.addAttribute( "userUnverified", true );
                return JspResolver.LOGIN_DISABLED_PAGE;
            } else {
                redirectAttributes.addFlashAttribute( "message", "User has been enabled! Please log in to continue" );
                redirectAttributes.addFlashAttribute( "status", DisplayMessageType.SUCCESS_MESSAGE );
                return "redirect:/" + JspResolver.LOGIN + ".do";
            }
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Error while updating user log in prevention field ", e );
            redirectAttributes.addFlashAttribute( "message", "Unable to enable user login. Please try again later" );
            redirectAttributes.addFlashAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
            return "redirect:/" + JspResolver.LOGIN + ".do";
        }
    }

}
