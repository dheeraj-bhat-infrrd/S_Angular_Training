package com.realtech.socialsurvey.web.controller;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.DisplayMessage;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.entities.UserHierarchyAssignments;
import com.realtech.socialsurvey.core.entities.UserSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.reportingmanagement.ReportingDashboardManagement;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;


// JIRA SS-37 BY RM02 BOC
/**
 * Controller to manage hierarchy
 */
@Controller
@SuppressWarnings ( "unchecked")
public class HierarchyManagementController
{

    private static final Logger LOG = LoggerFactory.getLogger( HierarchyManagementController.class );

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private ReportingDashboardManagement reportingDashboardManagement;


    /**
     * Method to call services for showing up the build hierarchy page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/showbuildhierarchypage", method = RequestMethod.GET)
    public String showBuildHierarchyPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showBuildHierarchyPage called" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        AccountType accountType = (AccountType) session.getAttribute( CommonConstants.ACCOUNT_TYPE_IN_SESSION );
        boolean isRegionAdditionAllowed = false;
        boolean isBranchAdditionAllowed = false;
        boolean isUserAuthorized = true;
        UserSettings userSettings = (UserSettings) session.getAttribute( CommonConstants.CANONICAL_USERSETTINGS_IN_SESSION );
        String profileName = null;
        try {
            try {
                //			    Commented as this threw an error when adding team members under a non-verified admin
                //				if (user.getStatus() != CommonConstants.STATUS_ACTIVE) {
                //					LOG.error("Inactive or unauthorized users can not access build hierarchy page");
                //					isUserAuthorized = false;
                //					model.addAttribute("message", messageUtils.getDisplayMessage(DisplayMessageConstants.HIERARCHY_MANAGEMENT_NOT_AUTHORIZED,
                //							DisplayMessageType.ERROR_MESSAGE, "javascript:resendVerificationMail()"));
                //				}
                LOG.debug( "Calling service for checking the if the region addition is allowed" );
                isRegionAdditionAllowed = organizationManagementService.isRegionAdditionAllowed( user, accountType );

                LOG.debug( "Calling service for checking the if the branches addition is allowed" );
                isBranchAdditionAllowed = organizationManagementService.isBranchAdditionAllowed( user, accountType );

                LOG.debug( "Obtaining profile name from settings present in session" );
                OrganizationUnitSettings companySettings = userSettings.getCompanySettings();
                profileName = companySettings.getProfileName();
                LOG.debug( "Profile name obtained is : " + profileName );

                LOG.debug( "Obtaining profile level for the user" );

            } catch ( InvalidInputException e ) {
                throw new InvalidInputException(
                    "InvalidInputException while checking for max region addition. Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            model.addAttribute( "profileName", profileName );
            model.addAttribute( "isUserAuthorized", isUserAuthorized );
            model.addAttribute( "isRegionAdditionAllowed", isRegionAdditionAllowed );
            model.addAttribute( "isBranchAdditionAllowed", isBranchAdditionAllowed );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException in showBuildHierarchyPage. Reason:" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Successfully completed method to showBuildHierarchyPage" );
        return JspResolver.BUILD_HIERARCHY;
    }


    /**
     * Method to get the view hierarchy page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/viewhierarchy", method = RequestMethod.GET)
    public String showViewHierarchyPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showViewHierarchyPage called" );
        User user = sessionHelper.getCurrentUser();
        String companyName = null;
        try {
            Company company = user.getCompany();
            if ( company == null ) {
                throw new NoRecordsFetchedException( "company not found for the current user",
                    DisplayMessageConstants.GENERAL_ERROR );
            }
            companyName = company.getCompany();
            model.addAttribute( "companyName", companyName );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException in showViewHierarchyPage. Reason:" + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        LOG.info( "Method showViewHierarchyPage executed successfully" );
        return JspResolver.VIEW_HIERARCHY;
    }

    /**
     * Fetch the list of branches for the company
     * 
     * @param model
     * @param request
     * @return
     */
    /*
     * @RequestMapping(value = "/fetchallbranches", method = RequestMethod.GET) public String
     * fetchAllBranches(Model model, HttpServletRequest request) {
     * LOG.info("Fetching all the branches for current user"); HttpSession session =
     * request.getSession(false); User user = sessionHelper.getCurrentUser(); AccountType
     * accountType = (AccountType) session.getAttribute(CommonConstants.ACCOUNT_TYPE_IN_SESSION);
     * String jspToReturn = null; try { try {
     * LOG.debug("Calling service to get the list of branches in company"); List<Branch> branches =
     * organizationManagementService.getAllBranchesForCompany(user.getCompany());
     * LOG.debug("Successfully executed service to get the list of branches in company : " +
     * branches); model.addAttribute("branches", branches);
     *//**
       * UI for enterprise branches and regions is different hence deciding which jsp to return
       */
    /*
     * if (accountType == AccountType.ENTERPRISE) { jspToReturn =
     * JspResolver.EXISTING_ENTERPRISE_BRANCHES; } else { jspToReturn =
     * JspResolver.EXISTING_BRANCHES; } } catch (InvalidInputException e) {
     * LOG.error("Error occurred while fetching the branch list in method getAllBranchesForCompany"
     * ); throw new InvalidInputException(
     * "Error occurred while fetching the branch list in method getAllBranchesForCompany",
     * DisplayMessageConstants.GENERAL_ERROR, e); } } catch (NonFatalException e) {
     * LOG.error("NonFatalException while fetching all branches. Reason : " + e.getMessage(), e);
     * model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(),
     * DisplayMessageType.ERROR_MESSAGE)); return JspResolver.MESSAGE_HEADER; } return jspToReturn;
     * }
     *//**
       * Fetch the list of regions for the company
       * 
       * @param model
       * @param request
       * @return
       */
    /*
     * @RequestMapping(value = "/fetchallregions", method = RequestMethod.GET) public String
     * fetchAllRegions(Model model, HttpServletRequest request) {
     * LOG.info("Fetching all the regions for current user"); User user =
     * sessionHelper.getCurrentUser(); try { try {
     * LOG.debug("Calling service to get the list of regions in company"); List<Region> regions =
     * organizationManagementService.getAllRegionsForCompany(user.getCompany());
     * LOG.debug("Sucessfully executed service to get the list of regions in company : " + regions);
     * model.addAttribute("regions", regions); } catch (InvalidInputException e) {
     * LOG.error("Error occurred while fetching the regions list in method getAllRegionsForCompany"
     * ); throw new InvalidInputException(
     * "Error occurred while fetching the region list in method getAllRegionsForCompany",
     * DisplayMessageConstants.GENERAL_ERROR, e); } } catch (NonFatalException e) {
     * LOG.error("NonFatalException while fetching all regions. Reason : " + e.getMessage(), e);
     * model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(),
     * DisplayMessageType.ERROR_MESSAGE)); return JspResolver.MESSAGE_HEADER; }
     * LOG.info("Successfully fetched the list of regions"); return
     * JspResolver.EXISTING_ENTERPRISE_REGIONS; }
     *//**
       * Method to fetch all regions for selector
       * 
       * @param model
       * @param request
       * @return
       */
    /*
     * @RequestMapping(value = "/fetchregionsselector", method = RequestMethod.GET) public String
     * fetchRegionsSelector(Model model, HttpServletRequest request) {
     * LOG.info("Method fetchRegionsSelector called in HierarchyManagementController "); User user =
     * sessionHelper.getCurrentUser(); try { try {
     * LOG.debug("Calling service to get the list of regions in company"); List<Region> regions =
     * organizationManagementService.getAllRegionsForCompany(user.getCompany());
     * LOG.debug("Sucessfully executed service to get the list of regions in company : " + regions);
     * model.addAttribute("regions", regions); } catch (InvalidInputException e) {
     * LOG.error("Error occurred while fetching the regions list in method fetchRegionsSelector");
     * throw new InvalidInputException(
     * "Error occurred while fetching the region list in method fetchRegionsSelector",
     * DisplayMessageConstants.GENERAL_ERROR, e); } } catch (NonFatalException e) {
     * LOG.error("NonFatalException while fetchRegionsSelector. Reason : " + e.getMessage(), e);
     * model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(),
     * DisplayMessageType.ERROR_MESSAGE)); return JspResolver.MESSAGE_HEADER; }
     * LOG.info("Successfully fetched the list of regions"); return
     * JspResolver.REGIONS_AUTOCOMPLETE; }
     */


    /**
     * Deactivates a region status
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/deactivateregion", method = RequestMethod.POST)
    public String deactivateRegion( Model model, HttpServletRequest request )
    {
        LOG.info( "Deactivating region" );
        try {
            long regionId = 0l;
            UserHierarchyAssignments assignments = null;
            User user = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession( false );
            try {
                regionId = Long.parseLong( request.getParameter( "regionId" ) );
                assignments = (UserHierarchyAssignments) session.getAttribute( CommonConstants.USER_ASSIGNMENTS );
                organizationManagementService.deleteRegionDataFromAllSources( regionId, user, assignments, CommonConstants.STATUS_INACTIVE );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception occurred while parsing the region id.Reason :" + e.getMessage(), e );
                throw new InvalidInputException( "Number format exception occurred while parsing the region id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error occurred while deactivating the region", e );
                throw new InvalidInputException( "Error occurred while deactivating the region",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            LOG.info( "Successfully deactivated the region " + regionId );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.REGION_DELETE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while deactivating the region. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Check for associated branches for the region
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/checkbranchesinregion", method = RequestMethod.POST)
    public String checkBranchesInRegion( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching all the branches for current region" );
        String messageToReturn = null;

        try {
            long regionId = 0l;
            try {
                regionId = Long.parseLong( request.getParameter( "regionId" ) );
                LOG.debug( "Calling service to get the count of branches in region" );
                long branchCount = organizationManagementService.getCountBranchesInRegion( regionId );
                LOG.debug( "Successfully executed service to get the count of branches in region : " + branchCount );

                if ( branchCount > 0l ) {
                    model.addAttribute( "message",
                        messageUtils.getDisplayMessage( "BRANCH_MAPPING_EXISTS", DisplayMessageType.ERROR_MESSAGE ) );
                    messageToReturn = JspResolver.MESSAGE_HEADER;
                } else {
                    model.addAttribute( "message",
                        messageUtils.getDisplayMessage( "REGION_CAN_DELETE", DisplayMessageType.SUCCESS_MESSAGE ) );
                    messageToReturn = JspResolver.MESSAGE_HEADER;
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Error occurred while fetching the branch count in method checkBranchesInRegion" );
                throw new InvalidInputException(
                    "Error occurred while fetching the branch count in method checkBranchesInRegion",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching branch count for region. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            messageToReturn = JspResolver.MESSAGE_HEADER;
        }
        return messageToReturn;
    }


    /**
     * Deactivates a branch status
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/deactivatebranch", method = RequestMethod.POST)
    public String deactivateBranch( Model model, HttpServletRequest request )
    {
        LOG.info( "Deactivating branch" );
        try {
            long branchId = 0l;
            UserHierarchyAssignments assignments = null;
            User user = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession( false );
            try {
                branchId = Long.parseLong( request.getParameter( "branchId" ) );
                assignments = (UserHierarchyAssignments) session.getAttribute( CommonConstants.USER_ASSIGNMENTS );
                organizationManagementService.deleteBranchDataFromAllSources( branchId, user, assignments, CommonConstants.STATUS_INACTIVE );
            } catch ( NumberFormatException e ) {
                LOG.error( "Number format exception occurred while parsing branch id", e );
                throw new InvalidInputException( "Number format exception occurred while parsing branch id",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            } catch ( InvalidInputException e ) {
                LOG.error( "Error occurred while deactivating the branch", e );
                throw new InvalidInputException( "Error occurred while deactivating the branch",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            LOG.info( "Successfully deactived the branch " + branchId );
            model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.BRANCH_DELETE_SUCCESSFUL,
                DisplayMessageType.SUCCESS_MESSAGE ) );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while deactivating a branch. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Check for associated user profiles for the branch
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/checkusersinbranch", method = RequestMethod.POST)
    public String checkUsersInBranch( Model model, HttpServletRequest request )
    {
        LOG.info( "Fetching count of users for current branch" );
        String messageToReturn = null;

        try {
            long branchId = 0l;
            try {
                branchId = Long.parseLong( request.getParameter( "branchId" ) );
                LOG.debug( "Calling service to get the count of users in branch" );
                long usersCount = organizationManagementService.getCountUsersInBranch( branchId );
                LOG.debug( "Successfully executed service to get the count of users in branch : " + usersCount );

                if ( usersCount > 0l ) {
                    model.addAttribute( "message",
                        messageUtils.getDisplayMessage( "USER_MAPPING_EXISTS", DisplayMessageType.ERROR_MESSAGE ) );
                    messageToReturn = JspResolver.MESSAGE_HEADER;
                } else {
                    model.addAttribute( "message",
                        messageUtils.getDisplayMessage( "BRANCH_CAN_DELETE", DisplayMessageType.SUCCESS_MESSAGE ) );
                    messageToReturn = JspResolver.MESSAGE_HEADER;
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Error occurred while fetching the users count in method checkUsersInBranch" );
                throw new InvalidInputException( "Error occurred while fetching the users count in method checkUsersInBranch",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching users count for branch. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            messageToReturn = JspResolver.MESSAGE_HEADER;
        }
        return messageToReturn;
    }


    /**
     * Method to add a new region
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "addregion", method = RequestMethod.POST)
    public String addRegion( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to add a region called in controller" );
        HttpSession session = request.getSession( false );
        Long adminId = (Long)session.getAttribute( CommonConstants.REALTECH_USER_ID );

        try {
            String regionName = request.getParameter( "regionName" );
            String regionAddress1 = request.getParameter( "regionAddress1" );
            String regionAddress2 = request.getParameter( "regionAddress2" );
            String regionCountry = request.getParameter( "regionCountry" );
            String regionCountryCode = request.getParameter( "regionCountrycode" );
            String regionState = request.getParameter( "regionState" );
            String regionCity = request.getParameter( "regionCity" );
            String regionZipcode = request.getParameter( "regionZipcode" );
            String selectedUserIdStr = request.getParameter( "selectedUserId" );
            String userSelectionType = request.getParameter( "userSelectionType" );

            if ( regionName != null && !regionName.isEmpty() ) {
                regionName = regionName.trim();
            }
            if ( regionAddress1 != null && !regionAddress1.isEmpty() ) {
                regionAddress1 = regionAddress1.trim();
            }
            if ( regionAddress2 != null && !regionAddress2.isEmpty() ) {
                regionAddress2 = regionAddress2.trim();
            }
            if ( regionCity != null && !regionCity.isEmpty() ) {
                regionCity = regionCity.trim();
            }
            if ( regionState != null && !regionState.isEmpty() ) {
                regionState = regionState.trim();
            }
            if ( regionCountry != null && !regionCountry.isEmpty() ) {
                regionCountry = regionCountry.trim();
            }

            String selectedUserEmail = "";
            if ( userSelectionType != null ) {
                if ( userSelectionType.equalsIgnoreCase( CommonConstants.USER_SELECTION_TYPE_SINGLE ) ) {
                    selectedUserEmail = request.getParameter( "selectedUserEmail" );
                } else {
                    selectedUserEmail = request.getParameter( "selectedUserEmailArray" );
                }
            }

            long selectedUserId = 0l;
            if ( selectedUserIdStr != null && !selectedUserIdStr.isEmpty() ) {
                try {
                    selectedUserId = Long.parseLong( selectedUserIdStr );
                } catch ( NumberFormatException e ) {
                    throw new InvalidInputException( "NumberFormatException while parsing selected userId in add region",
                        DisplayMessageConstants.INVALID_USER_SELECTED );
                }
            }

            boolean isAdmin = false;
            String isAdminStr = request.getParameter( "isAdmin" );
            if ( isAdminStr != null && !isAdminStr.isEmpty() ) {
                isAdmin = Boolean.parseBoolean( isAdminStr );
            }
            validateRegionForm( regionName );

            String[] assigneeEmailIds = parseEmailIdsIntoArray( selectedUserEmail );

            User loggedInUser = sessionHelper.getCurrentUser();
            LOG.debug( "Calling service to add a new region and assigning user to it if specified" );
            try {
                Map<String, Object> map = organizationManagementService.addNewRegionWithUser( loggedInUser, regionName.trim(),
                    CommonConstants.NO, regionAddress1, regionAddress2, regionCountry, regionCountryCode, regionState,
                    regionCity, regionZipcode, selectedUserId, assigneeEmailIds, isAdmin, false, ( adminId != null && adminId > 0 ) ? true : false );
                Region region = (Region) map.get( CommonConstants.REGION_OBJECT );
                List<User> invalidUserList = (List<User>) map.get( CommonConstants.INVALID_USERS_LIST );
                addOrUpdateRegionInSession( region, session );
                String invalidMessage = "These email address ";
                if ( invalidUserList != null && invalidUserList.size() > 0 ) {
                    String emailaddressses = "";
                    for ( User invalidUser : invalidUserList ) {
                        emailaddressses = emailaddressses.concat( invalidUser.getEmailId() ).concat( "," );
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserList.size() < 2 ) {
                        invalidMessage = "This email address " + emailaddressses + " is invalid";
                    } else {
                        invalidMessage = invalidMessage + emailaddressses + " are invalid";
                    }
                }
                invalidMessage = invalidMessage.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
                List<User> invalidUserAssignList = (List<User>) map.get( CommonConstants.INVALID_USERS_ASSIGN_LIST );
                String invalidUserAssignMessage = "These email address ";
                if ( invalidUserAssignList != null && invalidUserAssignList.size() > 0 ) {
                    String emailaddressses = "";
                    for ( User invalidUserAssign : invalidUserAssignList ) {
                        emailaddressses = emailaddressses + invalidUserAssign.getEmailId() + ",";
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserAssignList.size() < 2 ) {
                        invalidUserAssignMessage = "This email address " + emailaddressses + " is"
                            + CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX;
                    } else {
                        invalidUserAssignMessage = invalidUserAssignMessage + emailaddressses + " are"
                            + CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX;
                    }
                }
                DisplayMessage message = messageUtils.getDisplayMessage( DisplayMessageConstants.REGION_ADDTION_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE );
                DisplayMessage invalidEmailAddressMessage = null;
                DisplayMessage alreadyExistEmailAddress = null;

                if ( invalidMessage.endsWith( "invalid" ) ) {
                    invalidEmailAddressMessage = new DisplayMessage( invalidMessage, DisplayMessageType.ERROR_MESSAGE );
                }

                if ( invalidUserAssignMessage.endsWith( CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX ) ) {
                    alreadyExistEmailAddress = new DisplayMessage( invalidUserAssignMessage, DisplayMessageType.ERROR_MESSAGE );
                }
                model.addAttribute( "message", message );
                model.addAttribute( "invalidEmailAddressMessage", invalidEmailAddressMessage );
                model.addAttribute( "alreadyExistEmailAddress", alreadyExistEmailAddress );
            } catch ( UserAssignmentException e ) {
                throw new UserAssignmentException( e.getMessage(), DisplayMessageConstants.REGION_USER_ASSIGNMENT_ERROR, e );
            } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                throw new InvalidInputException( "Exception occured while adding new region.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while adding a region. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Successfully completed method to add a region in controller" );
        return JspResolver.MESSAGE_HEADER;
    }


    private String[] parseEmailIdsIntoArray( String emailIdList )
    {
        List<String> emailIds = new ArrayList<>();

        if ( emailIdList != null && !emailIdList.isEmpty() ) {

            StringTokenizer tokenizer = new StringTokenizer( emailIdList, ",|;|\n" );
            while ( tokenizer.hasMoreTokens() ) {
                String emailId = tokenizer.nextToken();
                if ( emailId.equalsIgnoreCase( "\r" ) || emailId.equalsIgnoreCase( "\n" ) ) {
                    continue;
                }

                emailId = emailId.trim();
                emailIds.add( emailId );
            }
        }
        String[] emailIdsArray = new String[emailIds.size()];
        emailIdsArray = emailIds.toArray( emailIdsArray );

        LOG.info( "Method validateAndParseEmailIds finished. Returning emailIdsArray:" + emailIdsArray );
        return emailIdsArray;
    }


    /**
     * Method to add a new branch
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "addbranch", method = RequestMethod.POST)
    public String addBranch( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to add a branch called in controller" );
        HttpSession session = request.getSession( false );
        Long adminId = (Long)session.getAttribute( CommonConstants.REALTECH_USER_ID );

        try {
            String branchName = request.getParameter( "officeName" );
            String branchAddress1 = request.getParameter( "officeAddress1" );
            String branchAddress2 = request.getParameter( "officeAddress2" );
            String branchCountry = request.getParameter( "officeCountry" );
            String branchCountryCode = request.getParameter( "officeCountrycode" );
            String branchState = request.getParameter( "officeState" );
            String branchCity = request.getParameter( "officeCity" );
            String branchZipcode = request.getParameter( "officeZipcode" );
            String strRegionId = request.getParameter( "regionId" );
            String selectedUserIdStr = request.getParameter( "selectedUserId" );
            String userSelectionType = request.getParameter( "userSelectionType" );

            if ( branchName != null && !branchName.isEmpty() ) {
                branchName = branchName.trim();
            }
            if ( branchAddress1 != null && !branchAddress1.isEmpty() ) {
                branchAddress1 = branchAddress1.trim();
            }
            if ( branchAddress2 != null && !branchAddress2.isEmpty() ) {
                branchAddress2 = branchAddress2.trim();
            }
            if ( branchCity != null && !branchCity.isEmpty() ) {
                branchCity = branchCity.trim();
            }
            if ( branchState != null && !branchState.isEmpty() ) {
                branchState = branchState.trim();
            }
            if ( branchCountry != null && !branchCountry.isEmpty() ) {
                branchCountry = branchCountry.trim();
            }

            String selectedUserEmail = "";
            if ( userSelectionType != null ) {
                if ( userSelectionType.equalsIgnoreCase( CommonConstants.USER_SELECTION_TYPE_SINGLE ) ) {
                    selectedUserEmail = request.getParameter( "selectedUserEmail" );
                } else {
                    selectedUserEmail = request.getParameter( "selectedUserEmailArray" );
                }
            }

            long selectedUserId = 0l;
            if ( selectedUserIdStr != null && !selectedUserIdStr.isEmpty() ) {
                try {
                    selectedUserId = Long.parseLong( selectedUserIdStr );
                } catch ( NumberFormatException e ) {
                    throw new InvalidInputException( "NumberFormatException while parsing selected userId in add branch",
                        DisplayMessageConstants.INVALID_USER_SELECTED );
                }
            }

            boolean isAdmin = false;
            String isAdminStr = request.getParameter( "isAdmin" );
            if ( isAdminStr != null && !isAdminStr.isEmpty() ) {
                isAdmin = Boolean.parseBoolean( isAdminStr );
            }

            validateBranchForm( branchName, branchAddress1 );
            String[] assigneeEmailIds = parseEmailIdsIntoArray( selectedUserEmail );

            long regionId = 0l;
            try {
                /**
                 * parse the regionId if a region is selected for the branch in case company is
                 * selected, regionId is null
                 */
                if ( strRegionId != null && !strRegionId.isEmpty() ) {
                    regionId = Long.parseLong( strRegionId );
                }
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "NumberFormatException while parsing regionId",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            User user = sessionHelper.getCurrentUser();
            try {
                LOG.debug( "Calling service to add a new branch" );
                Map<String, Object> map = organizationManagementService.addNewBranchWithUser( user, branchName.trim(), regionId,
                    CommonConstants.NO, branchAddress1, branchAddress2, branchCountry, branchCountryCode, branchState,
                    branchCity, branchZipcode, selectedUserId, assigneeEmailIds, isAdmin, false, ( adminId != null && adminId > 0 ) ? true : false );
                Branch branch = (Branch) map.get( CommonConstants.BRANCH_OBJECT );
                List<User> invalidUserList = (List<User>) map.get( CommonConstants.INVALID_USERS_LIST );
                LOG.debug( "Successfully executed service to add a new branch" );
                String invalidMessage = "These email address ";
                if ( invalidUserList != null && invalidUserList.size() > 0 ) {
                    String emailaddressses = "";
                    for ( User invalidUser : invalidUserList ) {
                        emailaddressses = emailaddressses.concat( invalidUser.getEmailId() ).concat( "," );
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserList.size() < 2 ) {

                        invalidMessage = "This email address " + emailaddressses + " is invalid";
                    } else {
                        invalidMessage = invalidMessage + emailaddressses + " are invalid";
                    }
                }
                invalidMessage = invalidMessage.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
                List<User> invalidUserAssignList = (List<User>) map.get( CommonConstants.INVALID_USERS_ASSIGN_LIST );
                String invalidUserAssignMessage = "These email address ";
                if ( invalidUserAssignList != null && invalidUserAssignList.size() > 0 ) {
                    String emailaddressses = "";
                    for ( User invalidUserAssign : invalidUserAssignList ) {
                        emailaddressses = emailaddressses + invalidUserAssign.getEmailId() + ",";
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserAssignList.size() < 2 ) {
                        invalidUserAssignMessage = "This email address " + emailaddressses + " is"
                            + CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX;
                    } else {
                        invalidUserAssignMessage = invalidUserAssignMessage + emailaddressses + " are"
                            + CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX;
                    }
                }
                addOrUpdateBranchInSession( branch, session );
                DisplayMessage message = messageUtils.getDisplayMessage( DisplayMessageConstants.BRANCH_ADDITION_SUCCESSFUL,
                    DisplayMessageType.SUCCESS_MESSAGE );
                DisplayMessage invalidEmailAddressMessage = null;
                DisplayMessage alreadyExistEmailAddress = null;

                if ( invalidMessage.endsWith( "invalid" ) ) {
                    invalidEmailAddressMessage = new DisplayMessage( invalidMessage, DisplayMessageType.ERROR_MESSAGE );
                }

                if ( invalidUserAssignMessage.endsWith( CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX ) ) {
                    alreadyExistEmailAddress = new DisplayMessage( invalidUserAssignMessage, DisplayMessageType.ERROR_MESSAGE );
                }

                model.addAttribute( "message", message );
                model.addAttribute( "invalidEmailAddressMessage", invalidEmailAddressMessage );
                model.addAttribute( "alreadyExistEmailAddress", alreadyExistEmailAddress );
            } catch ( UserAssignmentException e ) {
                throw new UserAssignmentException( e.getMessage(), DisplayMessageConstants.BRANCH_USER_ASSIGNMENT_ERROR, e );
            } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                throw new InvalidInputException( "Exception occured while adding new branch.REason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while adding a branch. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Successfully completed controller to add a branch" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to add an individual under a branch/region or company
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/addindividual", method = RequestMethod.POST)
    public String addIndividual( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to add an individual called in controller" );
        HttpSession session = request.getSession( false );
        User user = sessionHelper.getCurrentUser();
        Long adminId = (Long)session.getAttribute( CommonConstants.REALTECH_USER_ID );

        try {
            String strRegionId = request.getParameter( "regionId" );
            String strBranchId = request.getParameter( "officeId" );
            String selectedUserIdStr = request.getParameter( "selectedUserId" );
            String isAdminStr = request.getParameter( "isAdmin" );
            String userSelectionType = request.getParameter( "userSelectionType" );
            String isSocialMonitorAdminStr = request.getParameter( "isSocialMonitorAdmin" );
            String firstName = request.getParameter( "firstName" );
            String lastName = request.getParameter( "lastName" );

            String selectedUserEmail = "";
            if ( userSelectionType != null ) {
                if ( userSelectionType.equalsIgnoreCase( CommonConstants.USER_SELECTION_TYPE_SINGLE ) ) {
                    selectedUserEmail = request.getParameter( "selectedUserEmail" );
                    if ( selectedUserEmail != null ) {
                        selectedUserEmail.replaceAll( "\\s", "" );
                    }
                } else {
                    selectedUserEmail = request.getParameter( "selectedUserEmailArray" );
                }
            }

            long selectedUserId = 0l;
            if ( selectedUserIdStr != null && !selectedUserIdStr.isEmpty() ) {
                try {
                    selectedUserId = Long.parseLong( selectedUserIdStr );
                } catch ( NumberFormatException e ) {
                    throw new InvalidInputException( "NumberFormatException while parsing selected userId in add individual",
                        DisplayMessageConstants.INVALID_USER_SELECTED );
                }
            }

            boolean isAdmin = false;
            if ( isAdminStr != null && !isAdminStr.isEmpty() ) {
                isAdmin = Boolean.parseBoolean( isAdminStr );
            }
            
            boolean isSocialMonitorAdmin = false;
            if ( isSocialMonitorAdminStr != null && !isSocialMonitorAdminStr.isEmpty() ) {
                isSocialMonitorAdmin = Boolean.parseBoolean( isSocialMonitorAdminStr );
            }

            try {
                validateAndParseIndividualDetails( user, selectedUserId, selectedUserEmail, ( adminId != null && adminId > 0 ) ? true : false );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException while parsing email ids", DisplayMessageConstants.GENERAL_ERROR, e );
            }
            String[] assigneeEmailIds = parseEmailIdsIntoArray( selectedUserEmail );

            long regionId = 0l;
            try {
                /**
                 * parse the regionId if a region is selected for the individual in case company is
                 * selected, regionId is null
                 */
                if ( strRegionId != null && !strRegionId.isEmpty() ) {
                    regionId = Long.parseLong( strRegionId );
                }
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "NumberFormatException while parsing regionId",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            long branchId = 0l;
            try {
                /**
                 * parse the branchId if a branch is selected for the individual in case company is
                 * selected, branchId is null
                 */
                if ( strBranchId != null && !strBranchId.isEmpty() ) {
                    branchId = Long.parseLong( strBranchId );
                }
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "NumberFormatException while parsing branchId",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            //check if user is admin. if yes than not allow to assign
            if ( user.getUserId() == selectedUserId && isAdmin ) {
                LOG.info( "User is company admin so can't assign it." );
                DisplayMessage message = messageUtils.getDisplayMessage( DisplayMessageConstants.REDUNDANT_ASSIGNMANT,
                    DisplayMessageType.ERROR_MESSAGE );
                model.addAttribute( "message", message );
                return JspResolver.MESSAGE_HEADER;
            }

            try {
                LOG.debug( "Calling service to add/assign invidual(s)" );
                Map<String, Object> map = organizationManagementService.addIndividual( user, selectedUserId, branchId, regionId,
                        assigneeEmailIds, isAdmin, false, true, ( adminId != null && adminId > 0 ) ? true : false, isSocialMonitorAdmin, firstName, lastName );

                List<User> invalidUserList = (List<User>) map.get( CommonConstants.INVALID_USERS_LIST );
                LOG.debug( "Successfully executed service to add a new branch" );
                String invalidMessage = "These email address ";
                if ( invalidUserList != null && invalidUserList.size() > 0 ) {
                    String emailaddressses = "";
                    for ( User invalidUser : invalidUserList ) {
                        emailaddressses = emailaddressses.concat( invalidUser.getEmailId() ).concat( "," );
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserList.size() < 2 ) {

                        invalidMessage = "This email address " + emailaddressses + " is invalid";
                    } else {
                        invalidMessage = invalidMessage + emailaddressses + " are invalid";
                    }
                }
                List<User> invalidUserAssignList = (List<User>) map.get( CommonConstants.INVALID_USERS_ASSIGN_LIST );
                String invalidUserAssignMessage = "These email address ";
                if ( invalidUserAssignList != null && invalidUserAssignList.size() > 0 ) {
                    String emailaddressses = "";
                    for ( User invalidUserAssign : invalidUserAssignList ) {
                        emailaddressses = emailaddressses + invalidUserAssign.getEmailId() + ",";
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserAssignList.size() < 2 ) {
                        invalidUserAssignMessage = "This email address " + emailaddressses + " is"
                            + CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX;
                    } else {
                        invalidUserAssignMessage = invalidUserAssignMessage + emailaddressses + " are"
                            + CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX;
                    }
                }
                LOG.debug( "Successfully executed service to add/assign an invidual(s)" );
                invalidMessage = invalidMessage.replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" );
                DisplayMessage message = null;
                if ( selectedUserId > 0l ) {
                    message = messageUtils.getDisplayMessage( DisplayMessageConstants.INDIVIDUAL_ADDITION_SUCCESSFUL,
                        DisplayMessageType.SUCCESS_MESSAGE );
                } else {
                    int invalidAddressCount = 0;
                    if ( invalidUserList != null && invalidUserList.size() > 0 ) {
                        invalidAddressCount = invalidUserList.size();
                    }
                    if ( invalidUserAssignList != null && invalidUserAssignList.size() > 0 ) {
                        invalidAddressCount += invalidUserAssignList.size();
                    }
                    if ( invalidAddressCount == 0 ) {
                        message = messageUtils.getDisplayMessage(
                            DisplayMessageConstants.INDIVIDUAL_MULTIPLE_ADDITION_SUCCESSFUL,
                            DisplayMessageType.SUCCESS_MESSAGE );
                    } else {
                        message = messageUtils.getDisplayMessage(
                            DisplayMessageConstants.INDIVIDUAL_MULTIPLE_ADDITION_UNSUCCESSFUL,
                            DisplayMessageType.SUCCESS_MESSAGE );
                    }
                    if ( invalidMessage.endsWith( "invalid" ) ) {
                        message.setType( DisplayMessageType.ERROR_MESSAGE );
                        if ( !message.getMessage().trim().isEmpty() ) {
                            message.setMessage( message.getMessage() + "<br>" + invalidMessage );
                        } else {
                            message.setMessage( invalidMessage );
                        }
                    }
                    if ( invalidUserAssignMessage.endsWith( CommonConstants.EMAIL_ADDRESS_TAKEN_ERROR_SUFFIX ) ) {
                        message.setType( DisplayMessageType.ERROR_MESSAGE );
                        if ( !message.getMessage().trim().isEmpty() ) {
                            message.setMessage( message.getMessage() + "<br>" + invalidUserAssignMessage );
                        } else {
                            message.setMessage( invalidUserAssignMessage );
                        }
                    }
                }

                model.addAttribute( "message", message );
            } catch ( UserAssignmentException e ) {
                throw new UserAssignmentException( e.getMessage(), DisplayMessageConstants.BRANCH_USER_ASSIGNMENT_ERROR, e );
            } catch ( InvalidInputException | NoRecordsFetchedException | SolrException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            // updating session with new assignment
            if ( user.getUserId() == selectedUserId ) {
                sessionHelper.getCanonicalSettings( session );
            }

            sessionHelper.processAssignments( session, user );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while adding an individual. Reason : " + e.getMessage(), e );
            model.addAttribute( "message", new DisplayMessage( e.getMessage(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Successfully completed controller to add an individual" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to update a branch and assign a user to branch if specified
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updatebranch", method = RequestMethod.POST)
    public String updateBranch( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateBranch called in HierarchyManagementController" );

        try {
            String strBranchId = request.getParameter( "branchId" );
            String branchName = request.getParameter( "officeName" );
            String branchAddress1 = request.getParameter( "officeAddress1" );
            String branchAddress2 = request.getParameter( "officeAddress2" );
            String branchCountry = request.getParameter( "officeCountry" );
            String branchCountryCode = request.getParameter( "officeCountrycode" );
            String branchState = request.getParameter( "officeState" );
            String branchCity = request.getParameter( "officeCity" );
            String branchZipcode = request.getParameter( "officeZipcode" );
            String strRegionId = request.getParameter( "regionId" );
            String selectedUserIdStr = request.getParameter( "selectedUserId" );
            String userSelectionType = request.getParameter( "userSelectionType" );

            String selectedUserEmail = "";
            if ( userSelectionType != null ) {
                if ( userSelectionType.equalsIgnoreCase( CommonConstants.USER_SELECTION_TYPE_SINGLE ) ) {
                    selectedUserEmail = request.getParameter( "selectedUserEmail" );
                } else {
                    selectedUserEmail = request.getParameter( "selectedUserEmailArray" );
                }
            }

            long selectedUserId = 0l;
            if ( selectedUserIdStr != null && !selectedUserIdStr.isEmpty() ) {
                try {
                    selectedUserId = Long.parseLong( selectedUserIdStr );
                } catch ( NumberFormatException e ) {
                    throw new InvalidInputException( "NumberFormatException while parsing selected userId in add branch",
                        DisplayMessageConstants.INVALID_USER_SELECTED );
                }
            }

            boolean isAdmin = false;
            String isAdminStr = request.getParameter( "isAdmin" );
            if ( isAdminStr != null && !isAdminStr.isEmpty() ) {
                isAdmin = Boolean.parseBoolean( isAdminStr );
            }

            // To replace all the white spaces present in the string.
            selectedUserEmail = selectedUserEmail.replaceAll( "[ \t\\x0B\f\r]+", "" );
            validateBranchForm( branchName, branchAddress1 );
            String[] assigneeEmailIds = parseEmailIdsIntoArray( selectedUserEmail );
            ;

            long regionId = 0l;
            try {
                /**
                 * parse the regionId if a region is selected for the branch
                 */
                if ( strRegionId != null && !strRegionId.isEmpty() ) {
                    regionId = Long.parseLong( strRegionId );
                }
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Error while parsing regionId in update branch. Reason : " + e.getMessage(),
                    DisplayMessageConstants.INVALID_REGION_SELECTED, e );
            }

            long branchId = 0l;
            try {
                branchId = Long.parseLong( strBranchId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Error while parsing branchId in update branch.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            User user = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession( false );
            Long adminId = (Long)session.getAttribute( CommonConstants.REALTECH_USER_ID );

            try {
                LOG.debug( "Calling service to update branch with Id : " + branchId );
                Map<String, Object> map = organizationManagementService.updateBranch( user, branchId, regionId, branchName,
                    branchAddress1, branchAddress2, branchCountry, branchCountryCode, branchState, branchCity, branchZipcode,
                    selectedUserId, assigneeEmailIds, isAdmin, false, ( adminId != null && adminId > 0 ) ? true : false );
                Branch branch = (Branch) map.get( CommonConstants.BRANCH_OBJECT );
                List<User> invalidUserList = (List<User>) map.get( CommonConstants.INVALID_USERS_LIST );
                addOrUpdateBranchInSession( branch, session );
                String invalidMessage = "These email address ";
                if ( invalidUserList != null ) {
                    String emailaddressses = "";
                    for ( User invalidUser : invalidUserList ) {
                        emailaddressses = emailaddressses.concat( invalidUser.getEmailId() ).concat( "," );
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserList.size() < 2 ) {

                        invalidMessage = "This email address " + emailaddressses + " is invalid";
                    } else {
                        invalidMessage = invalidMessage + emailaddressses + " are invalid";
                    }
                }
                if ( invalidUserList != null && !invalidUserList.isEmpty() ) {
                    model.addAttribute( "invalidEmailAddress", invalidMessage );
                }
                LOG.debug( "Successfully executed service to update a branch" );
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.BRANCH_UPDATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException(
                    "InvalidInputException occured while updating branch.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating branch. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to update branch completed successfully" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to update the branch in session
     * 
     * @param branch
     * @param session
     * @throws NoRecordsFetchedException
     */
    private void addOrUpdateBranchInSession( Branch branch, HttpSession session ) throws NoRecordsFetchedException
    {
        LOG.info( "Method addOrUpdateBranchInSession called for branch:" + branch );
        UserHierarchyAssignments assignments = (UserHierarchyAssignments) session
            .getAttribute( CommonConstants.USER_ASSIGNMENTS );
        Map<Long, String> branches = assignments.getBranches();
        branches.put( branch.getBranchId(), branch.getBranch() );
        LOG.info( "Method addOrUpdateBranchInSession completed successfully" );
    }


    /**
     * Method to update a region
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/updateregion", method = RequestMethod.POST)
    public String updateRegion( Model model, HttpServletRequest request )
    {
        LOG.info( "Method updateRegion called in HierarchyManagementController" );
        String regionName = request.getParameter( "regionName" );
        String strRegionId = request.getParameter( "regionId" );
        String regionAddress1 = request.getParameter( "regionAddress1" );
        String regionAddress2 = request.getParameter( "regionAddress2" );
        String regionCountry = request.getParameter( "regionCountry" );
        String regionCountryCode = request.getParameter( "regionCountrycode" );
        String regionState = request.getParameter( "regionState" );
        String regionCity = request.getParameter( "regionCity" );
        String regionZipcode = request.getParameter( "regionZipcode" );
        String selectedUserIdStr = request.getParameter( "selectedUserId" );
        String userSelectionType = request.getParameter( "userSelectionType" );

        String selectedUserEmail = "";
        if ( userSelectionType != null ) {
            if ( userSelectionType.equalsIgnoreCase( CommonConstants.USER_SELECTION_TYPE_SINGLE ) ) {
                selectedUserEmail = request.getParameter( "selectedUserEmail" );
            } else {
                selectedUserEmail = request.getParameter( "selectedUserEmailArray" );
            }
        }

        long selectedUserId = 0l;
        try {
            long regionId = 0l;
            try {
                regionId = Long.parseLong( strRegionId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "regionid is invalid in update region. Reason:" + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            if ( selectedUserIdStr != null && !selectedUserIdStr.isEmpty() ) {
                try {
                    selectedUserId = Long.parseLong( selectedUserIdStr );
                } catch ( NumberFormatException e ) {
                    throw new InvalidInputException( "NumberFormatExcyeption while parsing selected userId in add region",
                        DisplayMessageConstants.INVALID_USER_SELECTED );
                }
            }

            boolean isAdmin = false;
            String isAdminStr = request.getParameter( "isAdmin" );
            if ( isAdminStr != null && !isAdminStr.isEmpty() ) {
                isAdmin = Boolean.parseBoolean( isAdminStr );
            }
            validateRegionForm( regionName );

            String[] assigneeEmailIds = parseEmailIdsIntoArray( selectedUserEmail );

            User user = sessionHelper.getCurrentUser();
            HttpSession session = request.getSession( false );
            Long adminId = (Long)session.getAttribute( CommonConstants.REALTECH_USER_ID );

            try {
                LOG.debug( "Calling service to update region with Id : " + regionId );
                Map<String, Object> map = organizationManagementService.updateRegion( user, regionId, regionName,
                    regionAddress1, regionAddress2, regionCountry, regionCountryCode, regionState, regionCity, regionZipcode,
                    selectedUserId, assigneeEmailIds, isAdmin, false, ( adminId != null && adminId > 0 ) ? true : false );
                Region region = (Region) map.get( CommonConstants.REGION_OBJECT );
                List<User> invalidUserList = (List<User>) map.get( CommonConstants.INVALID_USERS_LIST );
                addOrUpdateRegionInSession( region, session );
                String invalidMessage = "These email address ";
                if ( invalidUserList != null ) {
                    String emailaddressses = "";
                    for ( User invalidUser : invalidUserList ) {
                        emailaddressses = emailaddressses.concat( invalidUser.getEmailId() ).concat( "," );
                    }
                    if ( emailaddressses.endsWith( "," ) ) {
                        emailaddressses = emailaddressses.substring( 0, emailaddressses.length() - 1 );
                    }

                    if ( invalidUserList.size() < 2 ) {

                        invalidMessage = "This email address " + emailaddressses + " is invalid";
                    } else {
                        invalidMessage = invalidMessage + emailaddressses + " are invalid";
                    }
                }
                if ( invalidUserList != null && !invalidUserList.isEmpty() ) {
                    model.addAttribute( "invalidEmailAddress", invalidMessage );
                }
                LOG.debug( "Successfully executed service to update a region" );
                model.addAttribute( "message", messageUtils.getDisplayMessage(
                    DisplayMessageConstants.REGION_UPDATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException(
                    "InvalidInputException occured while updating region.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while updating region. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method to update region completed successfully" );
        return JspResolver.MESSAGE_HEADER;
    }


    /**
     * Method to update region in session
     * 
     * @param region
     * @param session
     * @throws NoRecordsFetchedException
     */
    private void addOrUpdateRegionInSession( Region region, HttpSession session ) throws NoRecordsFetchedException
    {
        LOG.info( "Method addOrUpdateRegionInSession called for region:" + region );
        UserHierarchyAssignments assignments = (UserHierarchyAssignments) session
            .getAttribute( CommonConstants.USER_ASSIGNMENTS );
        Map<Long, String> regions = assignments.getRegions();
        regions.put( region.getRegionId(), region.getRegion() );
        LOG.info( "Method addOrUpdateRegionInSession executed successfully" );
    }


    /**
     * Method to fetch regions from solr for a given pattern, if no pattern is provided fetches all
     * the regions for logged in user's company
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/searchregions", method = RequestMethod.GET)
    public String searchRegions( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to search region called in controller" );
        String regionPattern = request.getParameter( "regionPattern" );
        User user = sessionHelper.getCurrentUser();
        String searchRegionJson = null;
        String strStart = request.getParameter( "start" );
        String strRows = request.getParameter( "rows" );
        HttpSession session = request.getSession( false );
        int start = 0;
        int rows = -1;
        Set<Long> regionIds = null;
        try {
            if ( regionPattern == null || regionPattern.isEmpty() ) {
                regionPattern = "*";
            }
            /**
             * if start index is present in request, parse and use it else use the default start
             * index
             */
            if ( strStart != null && !strStart.isEmpty() ) {
                try {
                    start = Integer.parseInt( strStart );
                } catch ( NumberFormatException e ) {
                    LOG.error(
                        "Number format exception while parsing start index value" + strStart + ".Reason :" + e.getMessage(),
                        e );
                }
            }
            /**
             * if number of rows is present in request, parse and use it else fetch default number
             * of rows
             */
            if ( strRows != null && !strRows.isEmpty() ) {
                try {
                    rows = Integer.parseInt( strRows );
                } catch ( NumberFormatException e ) {
                    LOG.error( "Number format exception while parsing rows value" + strRows + ".Reason :" + e.getMessage(), e );
                }
            }

            try {
                int highestRole = (int) session.getAttribute( CommonConstants.HIGHEST_ROLE_ID_IN_SESSION );
                if ( highestRole == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                    regionIds = organizationManagementService.getRegionIdsForUser( user, highestRole );
                }
                LOG.debug( "Calling solr search service to get the regions" );
                searchRegionJson = solrSearchService.searchRegions( regionPattern, user.getCompany(), regionIds, start,
                    rows + 1 );
                LOG.debug( "Calling solr search service to get the regions" );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while searching regions. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method to search region completed successfully. Returning json : " + searchRegionJson );
        return searchRegionJson;
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchregions", method = RequestMethod.GET)
    public String fetchRegions( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to fetch region  called in controller" );
        User user = sessionHelper.getCurrentUser();
        HttpSession session = request.getSession();
        String searchRegionJson = "";
        Set<Long> regionIds = null;
        int defaultRowSize = 1000; //TODO:remove hardcoding for max rows
        try {

            try {
                int highestRole = (int) session.getAttribute( CommonConstants.HIGHEST_ROLE_ID_IN_SESSION );
                if ( highestRole == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                    searchRegionJson = organizationManagementService.fetchRegionsByCompany( user.getCompany().getCompanyId() );
                } else if ( highestRole == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                    regionIds = organizationManagementService.getRegionIdsForUser( user, highestRole );
                    searchRegionJson = solrSearchService.searchRegions( "", user.getCompany(), regionIds, 0, defaultRowSize );
                }
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            } catch ( MalformedURLException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching regions for company. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method to fetch region completed successfully. Returning json : " + searchRegionJson );
        return searchRegionJson;
    }


    /**
     * Method to fetch branches from solr for a given pattern, if no pattern is provided fetches all
     * the branches for logged in user's company
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/searchbranches", method = RequestMethod.GET)
    public String searchBranches( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to search branches called in controller" );
        String branchPattern = request.getParameter( "branchPattern" );
        User user = sessionHelper.getCurrentUser();
        String searchBranchJson = null;
        String strStart = request.getParameter( "start" );
        String strRows = request.getParameter( "rows" );
        int start = 0;
        int rows = -1;
        HttpSession session = request.getSession( false );
        Set<Long> ids = null;
        try {
            if ( branchPattern == null || branchPattern.isEmpty() ) {
                branchPattern = "*";
            }
            /**
             * if start index is present in request, parse and use it else use the default start
             * index
             */
            if ( strStart != null && !strStart.isEmpty() ) {
                try {
                    start = Integer.parseInt( strStart );
                } catch ( NumberFormatException e ) {
                    LOG.error(
                        "Number format exception while parsing start index value" + strStart + ".Reason :" + e.getMessage(),
                        e );
                }
            }
            /**
             * if number of rows is present in request, parse and use it else fetch default number
             * of rows
             */
            if ( strRows != null && !strRows.isEmpty() ) {
                try {
                    rows = Integer.parseInt( strRows );
                } catch ( NumberFormatException e ) {
                    LOG.error( "Number format exception while parsing rows value" + strRows + ".Reason :" + e.getMessage(), e );
                }
            }

            int highestRole = (int) session.getAttribute( CommonConstants.HIGHEST_ROLE_ID_IN_SESSION );
            String idColumnName = null;
            /**
             * Selective fetch of branches is done in the case of region admin or branch admin
             */
            try {
                if ( highestRole == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                    ids = organizationManagementService.getRegionIdsForUser( user, highestRole );
                    idColumnName = CommonConstants.REGION_ID_SOLR;

                } else if ( highestRole == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                    ids = organizationManagementService.getBranchIdsForUser( user, highestRole );
                    idColumnName = CommonConstants.BRANCH_ID_SOLR;
                }

            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                LOG.error( "Exception occured while getting branchIds for user.Reason:" + e.getMessage() );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }

            try {
                LOG.debug( "Calling solr search service to get the branches" );
                searchBranchJson = solrSearchService.searchBranches( branchPattern, user.getCompany(), idColumnName, ids, start,
                    rows + 1 );
                LOG.debug( "Calling solr search service to get the branches" );
            } catch ( InvalidInputException e ) {
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while searching branches. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method to search branches completed successfully." );
        return searchBranchJson;
    }


    @ResponseBody
    @RequestMapping ( value = "/fetchbranches", method = RequestMethod.GET)
    public String fetchBranches( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to search branches called in controller" );
        User user = sessionHelper.getCurrentUser();
        String searchBranchJson = "";
        HttpSession session = request.getSession( false );
        int defaultRowSize = 1000; //TODO:remove hardcoding for max rows
        Set<Long> ids = null;
        try {
            int highestRole = (int) session.getAttribute( CommonConstants.HIGHEST_ROLE_ID_IN_SESSION );
            String idColumnName = null;
            /**
             * Selective fetch of branches is done in the case of region admin or branch admin
             */
            try {
                if ( highestRole == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                    searchBranchJson = organizationManagementService.fetchBranchesByCompany( user.getCompany().getCompanyId() );
                } else if ( highestRole == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                    ids = organizationManagementService.getRegionIdsForUser( user, highestRole );
                    idColumnName = CommonConstants.REGION_ID_SOLR;
                    searchBranchJson = solrSearchService.searchBranches( "", user.getCompany(), idColumnName, ids, 0,
                        defaultRowSize );
                } else if ( highestRole == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                    ids = organizationManagementService.getBranchIdsForUser( user, highestRole );
                    idColumnName = CommonConstants.BRANCH_ID_SOLR;
                    searchBranchJson = solrSearchService.searchBranches( "", user.getCompany(), idColumnName, ids, 0,
                        defaultRowSize );
                }
            } catch ( InvalidInputException | NoRecordsFetchedException | MalformedURLException e ) {
                LOG.error( "Exception occured while getting branchIds for user.Reason:" + e.getMessage() );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while searching branches. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method to search branches completed successfully." );
        return searchBranchJson;
    }


    /**
     * Method to fetch all the users in a company
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchusers", method = RequestMethod.GET)
    public String fetchUsers( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to search users called in controller" );
        User user = sessionHelper.getCurrentUser();
        String searchUserJson = "";
        HttpSession session = request.getSession( false );
        try {
            int highestRole = (int) session.getAttribute( CommonConstants.HIGHEST_ROLE_ID_IN_SESSION );
            //Get all users in the company only if the highest role is company admin
            try {
                if ( highestRole == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                    searchUserJson = organizationManagementService.getAllUsersUnderCompanyFromSolr( user.getCompany() );
                }
            } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                LOG.error( "Exception occured while getting userIds for user.Reason:" + e.getMessage() );
                throw new InvalidInputException( e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
            //TODO : Add for region and branch selective fetch
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while searching users. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method to search users completed successfully." );
        return searchUserJson;
    }


    /**
     * Method to fetch a region details based on id
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchregiontoupdate", method = RequestMethod.GET)
    public String fetchRegionToUpdate( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchRegionToUpdate called in controller" );
        String strRegionId = request.getParameter( "regionId" );
        long regionId = 0l;
        String regionToUpdateJson = null;
        try {
            try {
                regionId = Long.parseLong( strRegionId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException(
                    "Error while parsing regionId in fetchRegionToUpdate.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            LOG.debug( "Calling service to fetch region settings" );
            OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
            regionToUpdateJson = new Gson().toJson( regionSettings );
            LOG.debug( "Fetched region .regionToUpdateJson is :" + regionToUpdateJson );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching Region To Update. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }

        LOG.info( "Method fetchRegionToUpdate finished in controller" );
        return regionToUpdateJson;

    }


    /**
     * Method to fetch branch along with settings for update
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchbranchtoupdate", method = RequestMethod.GET)
    public String fetchBranchToUpdate( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchBranchToUpdate called in controller" );
        String strBranchId = request.getParameter( "branchId" );
        long branchId = 0l;
        String branchToUpdateJson = null;
        try {
            try {
                branchId = Long.parseLong( strBranchId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException(
                    "Error while parsing branchId in fetchBranchToUpdate.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            LOG.debug( "Calling service to fetch branch settings" );
            BranchSettings branchSettings = organizationManagementService.getBranchSettings( branchId );
            branchToUpdateJson = new Gson().toJson( branchSettings );
            LOG.debug( "Fetched branch .branchToUpdateJson is :" + branchToUpdateJson );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching branch To Update. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method fetchBranchToUpdate finished in controller. Returning : " + branchToUpdateJson );
        return branchToUpdateJson;

    }


    // JIRA SS-137 BY RM-05 : BOC
    /**
     * Method to fetch branch along with settings for update
     * 
     * @param model
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping ( value = "/fetchbranchesforregion", method = RequestMethod.GET)
    public String fetchBranchesInRegion( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchBranchesInRegion called in controller" );
        String strRegionId = request.getParameter( "regionId" );
        long regionId = 0l;
        String branches = null;
        try {
            try {
                regionId = Long.parseLong( strRegionId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException(
                    "Error while parsing regionId in fetchBranchesInRegion.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            branches = solrSearchService.searchBranchesByRegion( regionId, CommonConstants.INITIAL_INDEX, -1 );
            LOG.debug( "Fetched branch .branchToUpdateJson is :" + branches );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching branches in a region. Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method fetchBranchesInRegion finished in controller. Returning : " + branches );
        return branches;

    }


    /**
     * Method to fetch branches for a region in hierarchy view page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/fetchhierarchyviewbranches", method = RequestMethod.GET)
    public String fetchHierarchyViewBranches( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchHierarchyViewBranches called in controller" );
        String strRegionId = request.getParameter( "regionId" );
        long regionId = 0l;
        List<Branch> branches = null;
        int start = 0;
        int rows = -1;
        try {
            try {
                regionId = Long.parseLong( strRegionId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException(
                    "Error while parsing regionId in fetchHierarchyViewBranches.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            LOG.debug( "Fetching branches under region:" + regionId );
            branches = organizationManagementService.getBranchesByRegionId( regionId );

            Set<Long> regionIds = new HashSet<Long>();
            regionIds.add( regionId );
            LOG.debug( "Fetching users under region:" + regionId );
            List<UserFromSearch> users = organizationManagementService.getUsersUnderRegionFromSolr( regionIds, start, rows );

            User admin = sessionHelper.getCurrentUser();
            /**
             * fetching admin details
             */
            UserFromSearch adminUser = null;
            try {
                String adminUserDoc = JSONUtil.toJSON( solrSearchService.getUserByUniqueId( admin.getUserId() ) );
                Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
                adminUser = new Gson().fromJson( adminUserDoc.toString(), searchedUser );
            } catch ( SolrException e ) {
                LOG.error( "SolrException while searching for user id. Reason : " + e.getMessage(), e );
                throw new NonFatalException( "SolrException while searching for user id.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            users = userManagementService.checkUserCanEdit( admin, adminUser, users );

            //add profile image url
            /*for(BranchFromSearch branch : branches){
                OrganizationUnitSettings branchSetting =  organizationManagementService.getBranchSettingsDefault( branch.getBranchId() );
                branch.setProfileImageUrl( branchSetting.getProfileImageUrl() );
            }
            
            for(UserFromSearch user : users){
                OrganizationUnitSettings userSetting =  organizationManagementService.getAgentSettings( user.getUserId() );
                user.setProfileImageUrl( userSetting.getProfileImageUrl() );
            }*/

            model.addAttribute( "branches", branches );
            model.addAttribute( "individuals", users );
            model.addAttribute( "regionId", regionId );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching branches in a region . Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        LOG.info( "Method fetchHierarchyViewBranches finished in controller. Returning : " + branches );
        return JspResolver.VIEW_HIERARCHY_BRANCH_LIST;
    }


    /**
     * Method to fetch the list of users under a branch for the hierarchy view page
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/fetchbranchusers", method = RequestMethod.GET)
    public String fetchHierarchyViewUsersForBranch( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchHierarchyViewUsersForBranch called in Hierarchy management controller" );
        String strBranchId = request.getParameter( "branchId" );
        String strRegionId = request.getParameter( "regionId" );
        long branchId = 0l;
        int start = 0;
        try {
            try {
                branchId = Long.parseLong( strBranchId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException(
                    "Error while parsing branchId in fetchHierarchyViewBranches.Reason : " + e.getMessage(),
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }
            int userCount = (int) solrSearchService.getUsersCountByIden( branchId, CommonConstants.BRANCHES_SOLR, false );
            Collection<UserFromSearch> usersResult = solrSearchService.searchUsersByIden( branchId,
                CommonConstants.BRANCHES_SOLR, false, start, userCount );
            String usersJson = new Gson().toJson( usersResult );
            LOG.debug( "Solr result returned for users of branch is:" + usersJson );
            /**
             * convert users to Object
             */
            Type searchedUsersList = new TypeToken<List<UserFromSearch>>() {}.getType();
            List<UserFromSearch> usersList = new Gson().fromJson( usersJson, searchedUsersList );

            User admin = sessionHelper.getCurrentUser();
            /**
             * fetching admin details
             */
            UserFromSearch adminUser = null;
            try {
                String adminUserDoc = JSONUtil.toJSON( solrSearchService.getUserByUniqueId( admin.getUserId() ) );
                Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
                adminUser = new Gson().fromJson( adminUserDoc.toString(), searchedUser );
            } catch ( SolrException e ) {
                LOG.error( "SolrException while searching for user id. Reason : " + e.getMessage(), e );
                throw new NonFatalException( "SolrException while searching for user id.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            usersList = userManagementService.checkUserCanEdit( admin, adminUser, usersList );

            //add profile image url
            /*for(UserFromSearch user : usersList){
                OrganizationUnitSettings userSetting =  organizationManagementService.getAgentSettings( user.getUserId() );
                user.setProfileImageUrl( userSetting.getProfileImageUrl() );
            }*/

            model.addAttribute( "users", usersList );
            model.addAttribute( "branchId", branchId );
            model.addAttribute( "regionId", strRegionId );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching users in a branch . Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Method fetchHierarchyViewUsersForBranch executed successfully" );
        return JspResolver.VIEW_HIERARCHY_USERS_LIST;

    }


    /**
     * Method to fetch the hierarchy list for edit fetches regions,branches and individuals directly
     * under the company
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/fetchhierarchyviewlist", method = RequestMethod.GET)
    public String fetchHierarchyViewList( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchHierarchyViewList called" );
        HttpSession session = request.getSession( false );
        User admin = sessionHelper.getCurrentUser();

        Set<Long> regionIds = null;
        Set<Long> branchIds = null;
        List<Region> regions = null;
        List<Branch> branches = null;
        List<UserFromSearch> users = null;
        String jspToReturn = null;
        int start = 0;
        try {
            // fetching admin details
            UserFromSearch adminUser = null;
            try {
                String adminUserDoc = JSONUtil.toJSON( solrSearchService.getUserByUniqueId( admin.getUserId() ) );
                Type searchedUser = new TypeToken<UserFromSearch>() {}.getType();
                adminUser = new Gson().fromJson( adminUserDoc.toString(), searchedUser );
            } catch ( SolrException e ) {
                LOG.error( "SolrException while searching for user id. Reason : " + e.getMessage(), e );
                throw new NonFatalException( "SolrException while searching for user id.",
                    DisplayMessageConstants.GENERAL_ERROR, e );
            }

            long companyId = admin.getCompany().getCompanyId();
            int highestRole = (int) session.getAttribute( CommonConstants.HIGHEST_ROLE_ID_IN_SESSION );
            if ( highestRole == CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID ) {
                LOG.debug( "fetching regions under company" );
                regions = organizationManagementService.getRegionsForCompany( companyId );

                LOG.debug( "fetching branches under company" );
                branches = organizationManagementService.getBranchesUnderCompany( companyId );

                LOG.debug( "fetching users under company from solr" );
                users = organizationManagementService.getUsersUnderCompanyFromSolr( admin.getCompany(), start );

                users = userManagementService.checkUserCanEdit( admin, adminUser, users );
                jspToReturn = JspResolver.VIEW_HIERARCHY_REGION_LIST;
            } else if ( highestRole == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                LOG.debug( "Getting list of regions for the region admin" );
                regionIds = organizationManagementService.getRegionIdsForUser( admin, highestRole );
                regions = organizationManagementService.getRegionsForRegionIds( regionIds );

                jspToReturn = JspResolver.VIEW_HIERARCHY_REGION_LIST;
            } else if ( highestRole == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                LOG.debug( "Getting list of branches for the branch admin" );
                branchIds = organizationManagementService.getBranchIdsForUser( admin, highestRole );
                branches = organizationManagementService.getBranchesForBranchIds( branchIds );

                jspToReturn = JspResolver.VIEW_HIERARCHY_BRANCH_LIST;
            } else {
                throw new InvalidInputException( "not aurhorised to view hierarchy",
                    DisplayMessageConstants.HIERARCHY_EDIT_NOT_AUTHORIZED );
            }

            //add profile image url
            /*for(RegionFromSearch region : regions){
                OrganizationUnitSettings regionSetting =  organizationManagementService.getRegionSettings( region.getRegionId() );
                region.setProfileImageUrl( regionSetting.getProfileImageUrl() );
            }
            
            for(BranchFromSearch branch : branches){
                OrganizationUnitSettings branchSetting =  organizationManagementService.getBranchSettingsDefault( branch.getBranchId() );
                branch.setProfileImageUrl( branchSetting.getProfileImageUrl() );
            }
            
            for(UserFromSearch user : users){
                OrganizationUnitSettings userSetting =  organizationManagementService.getAgentSettings( user.getUserId() );
                user.setProfileImageUrl( userSetting.getProfileImageUrl() );
            }*/

            model.addAttribute( "regions", regions );
            model.addAttribute( "branches", branches );
            model.addAttribute( "individuals", users );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while fetching hierarchy view list main page Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            jspToReturn = JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Method fetchHierarchyViewList executed successfully. JspToReturn: " + jspToReturn );
        return jspToReturn;
    }
    // JIRA SS-137 BY RM-05 : EOC


    /**
     * Method to get page containing form for editing region
     * 
     * @param model
     * @return
     * @throws InvalidInputException
     */
    @RequestMapping ( value = "/getregioneditpage", method = RequestMethod.GET)
    public String getRegionEditPage( Model model, HttpServletRequest request ) throws InvalidInputException
    {
        LOG.info( "Method getRegionEditPage called" );
        String strRegionId = request.getParameter( "regionId" );
        boolean isUpdateCall = false;
        try {
            if ( strRegionId != null && !strRegionId.isEmpty() ) {
                /*HttpSession session = request.getSession();
                @SuppressWarnings("unchecked") Map<Long, RegionFromSearch> regions = (Map<Long, RegionFromSearch>) session
                		.getAttribute(CommonConstants.REGIONS_IN_SESSION);*/
                long regionId = 0;
                try {
                    regionId = Long.parseLong( strRegionId );
                } catch ( NumberFormatException e ) {
                    throw new InvalidInputException( "Number Format exception occured while parsing region Id" );
                }
                OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( regionId );
                model.addAttribute( "regionSettings", regionSettings );

                /*RegionFromSearch regionToUpdate = null;
                if (regions.containsKey(regionId)) {
                	regionToUpdate = regions.get(regionId);
                }
                else {
                	throw new NoRecordsFetchedException("Region not present in list of regions present in session. RegionId:" + regionId,
                			DisplayMessageConstants.GENERAL_ERROR);
                }
                model.addAttribute("region", regionToUpdate);*/
                isUpdateCall = true;
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while getting region edit Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        model.addAttribute( "isUpdateCall", isUpdateCall );
        LOG.info( "Method getRegionEditPage executed succesfully" );
        return JspResolver.HIERARCHY_REGION_EDIT;

    }


    /**
     * Method to get page containing form for editing office
     * 
     * @param model
     * @return
     */
    @RequestMapping ( value = "/getofficeeditpage", method = RequestMethod.GET)
    public String getOfficeEditPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getOfficeEditPage called" );
        String strBranchId = request.getParameter( "branchId" );
        boolean isUpdateCall = false;
        try {
            if ( strBranchId != null && !strBranchId.isEmpty() ) {
                boolean isCompanyBranch = false;
                /*HttpSession session = request.getSession();
                BranchFromSearch branch = null;
                @SuppressWarnings("unchecked") Map<Long, BranchFromSearch> branches = (Map<Long, BranchFromSearch>) session
                		.getAttribute(CommonConstants.BRANCHES_IN_SESSION);*/
                long branchId = Long.parseLong( strBranchId );

                BranchSettings branchSettings = organizationManagementService.getBranchSettings( branchId );
                model.addAttribute( "branchSettings", branchSettings );

                if ( branchSettings.getRegionName().equals( CommonConstants.DEFAULT_REGION_NAME ) ) {
                    isCompanyBranch = true;
                }

                /*if (branches.containsKey(branchId)) {
                	branch = branches.get(branchId);
                	*//**
                      * check if the branch is under default region, if yes the branch is under
                      * company directly hence set isCompanyBranch as true
                      *//*
                        if (branch.getRegionName().equals(CommonConstants.DEFAULT_REGION_NAME)) {
                        isCompanyBranch = true;
                        }
                        }
                        else {
                        throw new NoRecordsFetchedException("Branch not present in list of branches present in session. branchId:" + branchId,
                        	DisplayMessageConstants.GENERAL_ERROR);
                        }
                        model.addAttribute("branch", branch);*/
                isUpdateCall = true;
                model.addAttribute( "isCompanyBranch", isCompanyBranch );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException while getting office edit Reason : " + e.getMessage(), e );
            model.addAttribute( "message",
                messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        model.addAttribute( "isUpdateCall", isUpdateCall );
        LOG.info( "Method getOfficeEditPage executed succesfully" );
        return JspResolver.HIERARCHY_OFFICE_EDIT;

    }


    /**
     * Method to get page containing form for editing individual
     * 
     * @param model
     * @return
     */
    @RequestMapping ( value = "/getindividualeditpage", method = RequestMethod.GET)
    public String getIndividualEditPage( Model model )
    {
        LOG.info( "Method getIndividualEditPage called" );
        
        User admin = sessionHelper.getCurrentUser();
        long companyId = admin.getCompany().getCompanyId();
        
        try {
        	boolean isSocialMonitorEnabled = reportingDashboardManagement.isSocialMonitorEnabled(companyId);
        	model.addAttribute( "isSocialMonitorEnabled", isSocialMonitorEnabled );
        }catch ( InvalidInputException e ) {
            LOG.error( "fetching isSocialMonitorEnabled varibale value failed.", e );
        }catch ( NoRecordsFetchedException e ) {
            LOG.error( "No records found while checking if social monitor enabled.", e );
        }
        
        return JspResolver.HIERARCHY_INDIVIDUAL_EDIT;
    }


    /**
     * Method to validate branch addition/updation form
     * 
     * @param branchName
     * @param branchAddress1
     * @throws InvalidInputException
     */
    private void validateBranchForm( String branchName, String branchAddress1 ) throws InvalidInputException
    {
        LOG.debug( "Validating branch add/update form" );
        if ( branchName == null || branchName.isEmpty() ) {
            throw new InvalidInputException( "Branch name is invalid while adding/updating branch",
                DisplayMessageConstants.INVALID_BRANCH_NAME );
        }
        if ( branchAddress1 == null || branchAddress1.isEmpty() ) {
            throw new InvalidInputException( "Branch address is invalid while adding/updating branch",
                DisplayMessageConstants.INVALID_BRANCH_ADDRESS );
        }
        LOG.debug( "Successsfully validated branch add/update form" );
    }


    /**
     * Method to validate add/update region form
     * 
     * @param regionName
     * @param selectedUserIdStr
     * @param selectedUserEmail
     * @throws InvalidInputException
     */
    private void validateRegionForm( String regionName ) throws InvalidInputException
    {
        LOG.debug( "Validating region add/update form called for regionName:" + regionName );
        if ( regionName == null || regionName.isEmpty() ) {
            throw new InvalidInputException( "Region name is invalid while adding region",
                DisplayMessageConstants.INVALID_REGION_NAME );
        }
        LOG.debug( "Validating region add/update form" );
    }


    /**
     * Method to validate single/multiple individual details provided for assigning a user to a
     * hierarchy level
     * 
     * @param admin
     * @param selectedUserId
     * @param selectedUserEmail
     * @param isAddedByRealtechOrSSAdmin
     * @return
     * @throws InvalidInputException
     */
    private String[] validateAndParseIndividualDetails( User admin, long selectedUserId, String selectedUserEmail, boolean isAddedByRealtechOrSSAdmin )
        throws InvalidInputException
    {
        LOG.info( "Method validateAndParseIndividualDetails called for selectedUserIdStr:" + selectedUserId
            + ", selectedUserEmail:" + selectedUserEmail );

        List<String> emailIds = new ArrayList<String>();
        if ( selectedUserId <= 0l && selectedUserEmail != null && !selectedUserEmail.isEmpty() ) {

            // Tokenizing the string input per individual
            List<String> inputTokens = new ArrayList<String>();
            selectedUserEmail = selectedUserEmail.replaceAll( ";|\\||\n|\r", "," ).replaceAll( ",{2,}", "," );
            StringTokenizer tokenizerIndiv = new StringTokenizer( selectedUserEmail, "," );
            while ( tokenizerIndiv.hasMoreTokens() ) {
                String inputToken = tokenizerIndiv.nextToken();
                if ( tokenizerIndiv.countTokens() == 1 && ( inputToken == null || inputToken.isEmpty() ) ) {
                    throw new InvalidInputException( "Input token is invalid", DisplayMessageConstants.INVALID_EMAILID );
                }

                inputTokens.add( inputToken.trim() );
            }

            // creating users
            UserFromSearch userEntity = new UserFromSearch();
            List<String> inputTokensIndivDetail = null;
            for ( String inputStr : inputTokens ) {
                inputStr = inputStr.trim();
                inputTokensIndivDetail = new ArrayList<String>();

                // Tokenize individual details
                StringTokenizer tokenizerIndivDetail = new StringTokenizer( inputStr, " <|>" );
                while ( tokenizerIndivDetail.hasMoreTokens() ) {
                    String inputToken = tokenizerIndivDetail.nextToken();
                    if ( tokenizerIndivDetail.countTokens() == 1 && ( inputToken == null || inputToken.isEmpty() ) ) {
                        throw new InvalidInputException( "Input token is invalid", DisplayMessageConstants.INVALID_EMAILID );
                    }

                    inputTokensIndivDetail.add( inputToken.trim() );
                }

                if ( inputStr.contains( "<" ) && inputStr.contains( ">" ) && inputTokensIndivDetail.size() > 0 ) {

                    String indivName = "";
                    for ( String inputStrIndiv : inputTokensIndivDetail ) {

                        if ( organizationManagementService.validateEmail( inputStrIndiv.trim() ) ) {
                            // Setting EmailId
                            if ( userEntity.getEmailId() == null || userEntity.getEmailId().isEmpty() ) {
                                userEntity.setEmailId( inputStrIndiv );
                            }

                            // Setting First/Last names
                            if ( indivName != null && !indivName.isEmpty() ) {
                                indivName = indivName.trim();
                                if ( indivName.contains( " " ) ) {
                                    userEntity.setFirstName( indivName.substring( 0, indivName.lastIndexOf( " " ) ) );
                                    userEntity.setLastName( indivName.substring( indivName.lastIndexOf( " " ) + 1 ) );
                                } else {
                                    userEntity.setFirstName( indivName );
                                }

                                indivName = "";
                            }

                            // resetting user entity on creating new user
                            try {
                                String firstName = ( userEntity.getFirstName() != null ) ? userEntity.getFirstName()
                                    : userEntity.getEmailId().substring( 0, userEntity.getEmailId().indexOf( "@" ) );
                                String lastName = ( userEntity.getLastName() != null ) ? userEntity.getLastName() : null;
                                userManagementService.inviteUserToRegister( admin, firstName, lastName, userEntity.getEmailId(),
                                    false, true, false, isAddedByRealtechOrSSAdmin );

                                userEntity = new UserFromSearch();
                            } catch ( UserAlreadyExistsException | UndeliveredEmailException | NoRecordsFetchedException e ) {
                                LOG.debug( "Exception in validateAndParseIndividualDetails while inviting a new user. Reason:"
                                    + e.getMessage(), e );
                            }

                            // adding to email id's list
                            emailIds.add( inputStrIndiv );
                        } else {
                            indivName = indivName + " " + inputStrIndiv;
                        }
                    }
                } else if ( !inputStr.contains( "<" ) && !inputStr.contains( ">" ) && inputTokensIndivDetail.size() > 0 ) {
                    emailIds.add( inputStr );
                } else {
                    throw new InvalidInputException( "Individual details entered are invalid",
                        DisplayMessageConstants.INVALID_EMAILID );
                }
            }

            if ( emailIds.isEmpty() ) {
                throw new InvalidInputException( "Individual details entered are invalid",
                    DisplayMessageConstants.INVALID_EMAILID );
            }
        }

        String[] emailIdsArray = new String[emailIds.size()];
        emailIdsArray = emailIds.toArray( emailIdsArray );

        LOG.info( "Method validateAndParseIndividualDetails finished. Returning emailIdsArray:" + emailIdsArray );
        return emailIdsArray;
    }
}
// JIRA SS-37 BY RM02 EOC