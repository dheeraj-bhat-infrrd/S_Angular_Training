package com.realtech.socialsurvey.web.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.registration.RegistrationService;
import com.realtech.socialsurvey.core.services.usermanagement.UserManagementService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

// JIRA: SS-24 BY RM02 BOC

/**
 * Controller to manage the organizational settings and information provided by the user
 */
@Controller
public class OrganizationManagementController {
	private static final Logger LOG = LoggerFactory.getLogger(OrganizationManagementController.class);

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private Payment gateway;

	/**
	 * Method to call service for adding company information for a user
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addcompanyinformation", method = RequestMethod.POST)
	public String addCompanyInformation(Model model, HttpServletRequest request) {
		LOG.info("Method addCompanyInformation of UserManagementController called");
		String companyName = request.getParameter("company");
		String address1 = request.getParameter("address1");
		String address2 = request.getParameter("address2");
		String zipCode = request.getParameter("zipcode");
		String companyContactNo = request.getParameter("contactno");

		try {
			validateCompanyInfoParams(companyName, address1, zipCode, companyContactNo);
			String address = getCompleteAddress(address1, address2);

			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute(CommonConstants.USER_IN_SESSION);

			Map<String, String> companyDetails = new HashMap<String, String>();
			companyDetails.put(CommonConstants.COMPANY_NAME, companyName);
			companyDetails.put(CommonConstants.ADDRESS, address);
			companyDetails.put(CommonConstants.ZIPCODE, zipCode);
			companyDetails.put(CommonConstants.COMPANY_CONTACT_NUMBER, companyContactNo);

			LOG.debug("Calling services to add company details");
			user = organizationManagementService.addCompanyInformation(user, companyDetails);

			LOG.debug("Updating profile completion stage");
			registrationService.updateProfileCompletionStage(user, CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID,
					CommonConstants.ADD_ACCOUNT_TYPE_STAGE);

			LOG.debug("Successfully executed service to add company details");

		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException while adding company information. Reason :" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.COMPANY_INFORMATION;
		}
		LOG.info("Method addCompanyInformation of UserManagementController completed successfully");
		return JspResolver.ACCOUNT_TYPE_SELECTION;

	}

	/**
	 * Method to validate form parameters of company information provided by the user
	 * 
	 * @param companyName
	 * @param address
	 * @param zipCode
	 * @param companyContactNo
	 * @throws InvalidInputException
	 */
	private void validateCompanyInfoParams(String companyName, String address, String zipCode, String companyContactNo) throws InvalidInputException {
		LOG.debug("Method validateCompanyInfoParams called  for companyName : " + companyName + " address : " + address + " zipCode : " + zipCode
				+ " companyContactNo : " + companyContactNo);

		String PHONENUMBER_REGEX = "^((\\+)|(00)|(\\*)|())[0-9]{3,14}((\\#)|())$";
		String ZIPCODE_REGEX = "\\d{5}(-\\d{4})?";
		if (companyName == null || companyName.isEmpty()) {
			throw new InvalidInputException("Company name is null or empty while adding company information",
					DisplayMessageConstants.INVALID_COMPANY_NAME);
		}
		if (address == null || address.isEmpty()) {
			throw new InvalidInputException("Address is null or empty while adding company information", DisplayMessageConstants.INVALID_ADDRESS);
		}

		if (zipCode == null || zipCode.isEmpty() || !zipCode.matches(ZIPCODE_REGEX)) {
			throw new InvalidInputException("Zipcode is not valid while adding company information", DisplayMessageConstants.INVALID_ZIPCODE);
		}
		if (companyContactNo == null || companyContactNo.isEmpty() || !companyContactNo.matches(PHONENUMBER_REGEX)) {
			throw new InvalidInputException("Company contact number is not valid while adding company information",
					DisplayMessageConstants.INVALID_COMPANY_PHONEN0);
		}
		LOG.debug("Returning from validateCompanyInfoParams after validating parameters");
	}

	/**
	 * Method to get complete address from multiple address lines
	 * 
	 * @param address1
	 * @param address2
	 * @return
	 */
	private String getCompleteAddress(String address1, String address2) {
		LOG.debug("Getting complete address for address1 : " + address1 + " and address2 : " + address2);
		String address = address1;
		/**
		 * if address line 2 is present, append it to address1 else the complete address is address1
		 */
		if (address1 != null && !address1.isEmpty() && address2 != null && !address2.isEmpty()) {
			address = address1 + " " + address2;
		}
		LOG.debug("Returning complete address" + address);
		return address;
	}

	/**
	 * Method to call services for saving the selected account type(plan)
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addaccounttype", method = RequestMethod.POST)
	public String addAccountType(Model model, HttpServletRequest request) {
		LOG.info("Method addAccountType of UserManagementController called");
		String strAccountType = request.getParameter("accounttype");
		try {
			if (strAccountType == null || strAccountType.isEmpty()) {
				throw new InvalidInputException("Accounttype is null for adding account type", DisplayMessageConstants.INVALID_ADDRESS);
			}
			LOG.debug("AccountType obtained : " + strAccountType);
			
			
			LOG.debug("Initialising payment gateway");
			model.addAttribute("accounttype", strAccountType);
			model.addAttribute("clienttoken", gateway.getClientToken());
			model.addAttribute("message",
					messageUtils.getDisplayMessage(DisplayMessageConstants.ACCOUNT_TYPE_SELECTION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE));

			LOG.info("Method addAccountType of UserManagementController completed successfully");
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException while adding account type. Reason: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
		return JspResolver.PAYMENT;

	}
}
// JIRA: SS-24 BY RM02 EOC