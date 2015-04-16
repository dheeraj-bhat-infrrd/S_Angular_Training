/**
 * Entry point for profile view pages.
 */
package com.realtech.socialsurvey.web.profile;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class ProfileViewController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProfileViewController.class);
	
	@Autowired
	private MessageUtils messageUtils;
	
	@Autowired
	private ProfileManagementService profileManagementService;
	
	
	/**
	 * Method to return company profile page
	 * 
	 * @param profileName
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/company/{profileName}", method = RequestMethod.GET)
	public String initCompanyProfilePage(@PathVariable String profileName, Model model) {
		LOG.info("Service to initiate company profile page called");
		String message = null;
		if (profileName == null || profileName.isEmpty()) {
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
			model.addAttribute("message", message);
			return JspResolver.MESSAGE_HEADER;
		}
		model.addAttribute("companyProfileName", profileName);
		model.addAttribute("profileLevel", CommonConstants.PROFILE_LEVEL_COMPANY);
		LOG.info("Service to initiate company profile page executed successfully");
		return JspResolver.PROFILE_PAGE;
	}
	
	
	/**
	 * Method to return region profile page
	 * 
	 * @param companyProfileName
	 * @param regionProfileName
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/region/{companyProfileName}/{regionProfileName}")
	public String initRegionProfilePage(@PathVariable String companyProfileName, @PathVariable String regionProfileName, Model model) {
		LOG.info("Service to initiate region profile page called");
		String message = null;
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
			model.addAttribute("message", message);
			return JspResolver.MESSAGE_HEADER;
		}
		if (regionProfileName == null || regionProfileName.isEmpty()) {
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_REGION_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
			model.addAttribute("message", message);
			return JspResolver.MESSAGE_HEADER;
		}
		model.addAttribute("companyProfileName", companyProfileName);
		model.addAttribute("regionProfileName", regionProfileName);
		model.addAttribute("profileLevel", CommonConstants.PROFILE_LEVEL_REGION);
		LOG.info("Service to initiate region profile page executed successfully");
		return JspResolver.PROFILE_PAGE;
	}

	/**
	 * Method to return branch profile page
	 * 
	 * @param companyProfileName
	 * @param branchProfileName
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/office/{companyProfileName}/{branchProfileName}")
	public String initBranchProfilePage(@PathVariable String companyProfileName, @PathVariable String branchProfileName, Model model) {
		LOG.info("Service to initiate branch profile page called");
		String message = null;
		if (companyProfileName == null || companyProfileName.isEmpty()) {
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_COMPANY_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
			model.addAttribute("message", message);
			return JspResolver.MESSAGE_HEADER;
		}
		if (branchProfileName == null || branchProfileName.isEmpty()) {
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_BRANCH_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
			model.addAttribute("message", message);
			return JspResolver.MESSAGE_HEADER;
		}
		model.addAttribute("companyProfileName", companyProfileName);
		model.addAttribute("branchProfileName", branchProfileName);
		model.addAttribute("profileLevel", CommonConstants.PROFILE_LEVEL_BRANCH);
		LOG.info("Service to initiate branch profile page executed successfully");
		return JspResolver.PROFILE_PAGE;
	}

	/**
	 * Method to return agent profile page
	 * 
	 * @param agentProfileName
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{agentProfileName}")
	public String initBranchProfilePage(@PathVariable String agentProfileName, Model model) {
		LOG.info("Service to initiate agent profile page called");
		String message = null;
		if (agentProfileName == null || agentProfileName.isEmpty()) {
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_INDIVIDUAL_PROFILENAME, DisplayMessageType.ERROR_MESSAGE)
					.getMessage();
			model.addAttribute("message", message);
			return JspResolver.MESSAGE_HEADER;
		}
		model.addAttribute("agentProfileName", agentProfileName);
		model.addAttribute("profileLevel", CommonConstants.PROFILE_LEVEL_INDIVIDUAL);
		LOG.info("Service to initiate agent profile page executed successfully");
		return JspResolver.PROFILE_PAGE;
	}
	
	/**
	 * Method called on click of the contact us link on all profile pages
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/profile/sendmail",method=RequestMethod.POST)
	public @ResponseBody String sendEmail(HttpServletRequest request){
		
		LOG.info("Contact us mail controller called!");
		
		String profileType = request.getParameter("profiletype");
		String returnMessage = null;

		try {
			
			if( profileType == null || profileType.isEmpty()){
				LOG.error("Profile type not mentioned!");
				throw new InvalidInputException("Profile type not mentioned!");
			}
			
			String profileName = request.getParameter("profilename"); 				
			String senderMailId = request.getParameter("email");
			String message = request.getParameter("message");
			
			LOG.debug("Sending mail to :  "  + profileName + " from : " + senderMailId);
				
			profileManagementService.findProfileMailIdAndSendMail(profileName, message,
					senderMailId,profileType);
			LOG.debug("Mail sent!");
			returnMessage = messageUtils.getDisplayMessage(DisplayMessageConstants.CONTACT_US_MESSAGE_SENT, DisplayMessageType.SUCCESS_MESSAGE).toString();
		} catch (InvalidInputException e) {
			LOG.error("InvalidInputException : message : " + e.getMessage(),e);
			returnMessage = messageUtils.getDisplayMessage(DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE).toString();
		} catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException : message : " + e.getMessage(),e);
			returnMessage = messageUtils.getDisplayMessage(DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE).toString();
		} catch (UndeliveredEmailException e) {
			LOG.error("UndeliveredEmailException : message : " + e.getMessage(),e);
			returnMessage = messageUtils.getDisplayMessage(DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE).toString();
		} catch (Exception e) {
			LOG.error("Exception : message : " + e.getMessage(),e);			
			returnMessage = messageUtils.getDisplayMessage(DisplayMessageConstants.CONTACT_US_MESSAGE_UNSUCCESSFUL, DisplayMessageType.ERROR_MESSAGE).toString();
		}
		
		return returnMessage;
	}

}
