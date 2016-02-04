package com.realtech.socialsurvey.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.noggit.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.AbusiveSurveyReportWrapper;
import com.realtech.socialsurvey.core.entities.BranchFromSearch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionFromSearch;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserFromSearch;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.admin.AdminAuthenticationService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.CustomerDeletionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.RequestUtils;


@Controller
public class AdminController
{

    private static final Logger LOG = LoggerFactory.getLogger( AdminController.class );

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private AdminAuthenticationService adminAuthenticationService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private Payment payment;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    SocialManagementService socialManagementService;

    @Autowired
    private RequestUtils requestUtils;


    @RequestMapping ( value = "/admindashboard")
    public String adminDashboard( Model model, HttpServletRequest request )
    {

        LOG.info( "Inside adminDashboard() method in admin controller" );

        return JspResolver.ADMIN_DASHBOARD;
    }


    @RequestMapping ( value = "/purgeCompany")
    public @ResponseBody String purgeCompanyInformation( @RequestParam long companyId )
    {
        Company company = organizationManagementService.getCompanyById( companyId );
        String message = CommonConstants.SUCCESS_ATTRIBUTE;

        // delete company from braintree

        if ( company != null ) {
            try {
                List<LicenseDetail> licenseDetails = company.getLicenseDetails();
                if ( licenseDetails.size() > 0 ) {
                    LicenseDetail licenseDetail = licenseDetails.get( 0 );
                    if ( licenseDetail.getPaymentMode().equals( CommonConstants.BILLING_MODE_AUTO ) ) {
                        LOG.debug( "Deleting company from braintree " );
                        payment.deleteCustomer( Long.toString( company.getCompanyId() ) );
                    }
                }

            } catch ( CustomerDeletionUnsuccessfulException | InvalidInputException e ) {
                LOG.error( "Exception Caught " + e.getMessage() );
                message = CommonConstants.ERROR;
            }
            if ( company.getStatus() == CommonConstants.STATUS_INACTIVE ) {
                try {
                    organizationManagementService.purgeCompany( company );
                } catch ( InvalidInputException e ) {
                    LOG.error( "Exception Caught " + e.getMessage() );
                    message = CommonConstants.ERROR;
                } catch ( SolrException e ) {
                    LOG.error( "Exception Caught " + e.getMessage() );
                    message = CommonConstants.ERROR;
                }
            }
        }
        return message;
    }


    @RequestMapping ( value = "/adminhierarchy")
    public String adminHierarchyPage( Model model, HttpServletRequest request )
    {

        LOG.info( "Inside adminHierarchyPage() method in admin controller" );
        /*List<OrganizationUnitSettings> companies = organizationManagementService.getAllActiveCompaniesFromMongo();
        model.addAttribute("companyList", companies);*/
        return JspResolver.ADMIN_HIERARCHY_VIEW;
    }


    @RequestMapping ( value = "/companyhierarchy")
    public String companyHierarchyView( Model model, HttpServletRequest request )
    {

        LOG.info( "Inside companyHierarchyView() method in admin controller" );

        String companyIdStr = request.getParameter( "companyId" );
        List<RegionFromSearch> regions = null;
        List<BranchFromSearch> branches = null;
        List<UserFromSearch> users = null;
        int start = 0;

        try {
            long companyId = 0;
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                throw new NonFatalException( "Invalid company id was passed", e );
            }

            Company company = organizationManagementService.getCompanyById( companyId );

            int regionCount = (int) solrSearchService.getRegionsCount( "*", company, null );
            String regionsJson = solrSearchService.searchRegions( "*", company, null, start, regionCount );
            Type searchedRegionsList = new TypeToken<List<RegionFromSearch>>() {}.getType();
            regions = new Gson().fromJson( regionsJson, searchedRegionsList );

            try {
                LOG.debug( "fetching branches under company from solr" );
                branches = organizationManagementService.getBranchesUnderCompanyFromSolr( company, start );

                LOG.debug( "fetching users under company from solr" );
                users = organizationManagementService.getUsersUnderCompanyFromSolr( company, start );
            } catch ( NoRecordsFetchedException e ) {
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( companyId );
                if ( companySettings != null && companySettings.getContact_details() != null ) {
                    ContactDetailsSettings companyContactDetail = companySettings.getContact_details();
                    if ( companyContactDetail.getContact_numbers() != null ) {
                        model.addAttribute( "workContactNo", companyContactDetail.getContact_numbers().getWork() );
                    }
                    if ( companyContactDetail.getMail_ids() != null ) {
                        model.addAttribute( "workMailId", companyContactDetail.getMail_ids().getWork() );
                        // Get the user user name
                        if ( companyContactDetail.getMail_ids().getWork() != null
                            && !companyContactDetail.getMail_ids().getWork().isEmpty() ) {
                            User user = userManagementService.getUserByEmail( companyContactDetail.getMail_ids().getWork() );
                            model.addAttribute( "userName",
                                user.getFirstName() + " " + ( user.getLastName() != null ? user.getLastName() : "" ) );
                        }
                    }
                }
                LOG.error( "No records found for company branch or region, reason : " + e.getMessage() );
                model.addAttribute(
                    "message",
                    messageUtils.getDisplayMessage( DisplayMessageConstants.COMPANY_NOT_REGISTERD,
                        DisplayMessageType.SUCCESS_MESSAGE ).getMessage() );

                return JspResolver.ADMIN_COMPANY_NOT_REGISTERED;
            }
            model.addAttribute( "companyObj", company );

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
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        return JspResolver.ADMIN_COMPANY_HIERARCHY;
    }


    @RequestMapping ( value = "/fetchcompaniesbykey", method = RequestMethod.GET)
    public String fetchCompaniesByKey( Model model, HttpServletRequest request )
    {

        LOG.info( "Inside fetchCompaniesByKey() method" );

        String searchKey = request.getParameter( "searchKey" );
        String filerValue = request.getParameter( "comSelFilter" );
        String accountTypeStr = request.getParameter( "accountType" );
        List<OrganizationUnitSettings> unitSettings = null;
        int accountType = -1;
        int status = CommonConstants.STATUS_ACTIVE;
        ;
        // Check for company status filer
        if ( filerValue != null && filerValue.equals( "inactive" ) ) {
            status = CommonConstants.STATUS_INACTIVE;
        }

        // Check for account type filter
        if ( accountTypeStr != null && accountTypeStr != "all" ) {
            if ( accountTypeStr.equals( "individual" ) ) {
                accountType = CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL;
            } else if ( accountTypeStr.equals( "enterprise" ) ) {
                accountType = CommonConstants.ACCOUNTS_MASTER_ENTERPRISE;
            }
        }

        unitSettings = organizationManagementService.getCompaniesByKeyValueFromMongo( searchKey, accountType, status );

        model.addAttribute( "companyList", unitSettings );

        return JspResolver.ADMIN_COMPANY_LIST;
    }


    @RequestMapping ( value = "/fetchhierarchyviewbranchesforadmin", method = RequestMethod.GET)
    public String fetchHierarchyViewBranchesForRealTechAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchHierarchyViewBranches called in controller" );
        String strRegionId = request.getParameter( "regionId" );
        String companyIdStr = request.getParameter( "companyId" );
        long regionId = 0l;
        long companyId = 0l;
        List<BranchFromSearch> branches = null;
        int start = 0;
        int rows = -1;
        try {
            try {
                regionId = Long.parseLong( strRegionId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Error while parsing regionId in fetchHierarchyViewBranches.Reason : "
                    + e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Error while parsing company in fetchHierarchyViewBranches.Reason : "
                    + e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
            int branchCount = (int) solrSearchService.getBranchCountByRegion( regionId );
            String branchesJson = solrSearchService.searchBranchesByRegion( regionId, start, branchCount );
            LOG.debug( "Fetched branch .branches json is :" + branchesJson );
            Type searchedBranchesList = new TypeToken<List<BranchFromSearch>>() {}.getType();
            branches = new Gson().fromJson( branchesJson, searchedBranchesList );

            Set<Long> regionIds = new HashSet<Long>();
            regionIds.add( regionId );
            LOG.debug( "Fetching users under region:" + regionId );
            List<UserFromSearch> users = organizationManagementService.getUsersUnderRegionFromSolr( regionIds, start, rows );

            User admin = userManagementService.getCompanyAdmin( companyId );
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
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }
        LOG.info( "Method fetchHierarchyViewBranches finished in controller. Returning : " + branches );
        return JspResolver.ADMIN_REGION_HIERARCHY;
    }


    @RequestMapping ( value = "/fetchbranchusersforadmin", method = RequestMethod.GET)
    public String fetchHierarchyViewUsersForBranchForAdmin( Model model, HttpServletRequest request )
    {
        LOG.info( "Method fetchHierarchyViewUsersForBranch called in admin controller" );
        String strBranchId = request.getParameter( "branchId" );
        String strRegionId = request.getParameter( "regionId" );
        String strCompanyId = request.getParameter( "companyId" );
        long branchId = 0l;
        long companyId = 01;
        int start = 0;
        try {
            try {
                branchId = Long.parseLong( strBranchId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Error while parsing branchId in fetchHierarchyViewBranches.Reason : "
                    + e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
            }
            try {
                companyId = Long.parseLong( strCompanyId );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Error while parsing companyId in fetchHierarchyViewBranches.Reason : "
                    + e.getMessage(), DisplayMessageConstants.GENERAL_ERROR, e );
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

            User admin = userManagementService.getCompanyAdmin( companyId );
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
            model
                .addAttribute( "message", messageUtils.getDisplayMessage( e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE ) );
            return JspResolver.MESSAGE_HEADER;
        }

        LOG.info( "Method fetchHierarchyViewUsersForBranch executed successfully" );
        return JspResolver.ADMIN_BRANCH_HIERARCHY;

    }


    @ResponseBody
    @RequestMapping ( value = "/loginadminas", method = RequestMethod.GET)
    public String loginAdminAsUser( Model model, HttpServletRequest request )
    {

        LOG.info( "Inside loginAdminAsUser() method in admin controller" );

        String columnName = request.getParameter( "colName" );
        String columnValue = request.getParameter( "colValue" );

        try {

            if ( columnName == null || columnName.isEmpty() ) {
                throw new InvalidInputException( "Column name passed null/empty" );
            }

            if ( columnValue == null || columnValue.isEmpty() ) {
                throw new InvalidInputException( "Column value passed null/empty" );
            }

            long id = 01;

            try {
                id = Long.parseLong( columnValue );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Invalid id was passed", e );
            }

            HttpSession session = request.getSession();
            User adminUser = sessionHelper.getCurrentUser();
            session.invalidate();

            User newUser = userManagementService.getUserByUserId( id );

            HttpSession newSession = request.getSession( true );
            
            //Set the autologin attribute as true
            newSession.setAttribute( CommonConstants.IS_AUTO_LOGIN, "true" );
            newSession.setAttribute( CommonConstants.REALTECH_USER_ID, adminUser.getUserId() );

            sessionHelper.loginAdminAs( newUser.getLoginName(), CommonConstants.BYPASS_PWD );

        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException occurred in loginAdminAsUser(), reason : " + e.getMessage() );
        }
        return "success";
    }


    @ResponseBody
    @RequestMapping ( value = "/switchtoadmin", method = RequestMethod.GET)
    public String switchToAdminUser( Model model, HttpServletRequest request )
    {

        HttpSession session = request.getSession();
        Long adminUserid = (Long) session.getAttribute( CommonConstants.REALTECH_USER_ID );

        // Logout current user
        session.invalidate();
        SecurityContextHolder.clearContext();

        session = request.getSession( true );

        try {
            User adminUser = userManagementService.getUserObjByUserId( adminUserid );

            if ( !adminUser.isSuperAdmin() ) {
                throw new InvalidInputException( "Admin user in session is not realtech admin" );
            }
            sessionHelper.loginAdminAs( adminUser.getLoginName(), CommonConstants.BYPASS_PWD );
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occurred in switchToAdminUser() method , reason : " + e.getMessage() );
            return "failure";
        }
        return "success";
    }


    @RequestMapping ( value = "/showsendinvition", method = RequestMethod.GET)
    public String showSendInvition()
    {
        LOG.info( "Inside showSendInvition() method" );

        return JspResolver.ADMIN_INVITE_VIEW;
    }


    @RequestMapping ( value = "/downloadcompanyregistrationreport")
    public void downloadCompanyRegistrationReport( HttpServletRequest request, HttpServletResponse response )
    {

        LOG.info( "Method called to download the company registration report" );

        try {
            Date startDate = null;
            String startDateStr = request.getParameter( "startDate" );
            if ( startDateStr != null && !startDateStr.isEmpty() ) {
                try {
                    startDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( startDateStr );
                } catch ( ParseException e ) {
                    throw new InvalidInputException(
                        "ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e );
                }
            }

            Date endDate = Calendar.getInstance().getTime();
            String endDateStr = request.getParameter( "endDate" );
            if ( endDateStr != null && !endDateStr.isEmpty() ) {
                try {
                    endDate = new SimpleDateFormat( CommonConstants.DATE_FORMAT ).parse( endDateStr );
                } catch ( ParseException e ) {
                    throw new InvalidInputException(
                        "ParseException caught in getCompleteSurveyFile() while parsing startDate. Nested exception is ", e );
                }
            }

            List<Company> companyList = organizationManagementService.getCompaniesByDateRange( startDate, endDate );
            String fileName = "Company_Registration_Report" + CommonConstants.EXCEL_FILE_EXTENSION;

            XSSFWorkbook workbook = organizationManagementService.downloadCompanyReport( companyList, fileName );
            response.setContentType( CommonConstants.EXCEL_FORMAT );
            String headerKey = CommonConstants.CONTENT_DISPOSITION_HEADER;
            String headerValue = String.format( "attachment; filename=\"%s\"", new File( fileName ).getName() );
            response.setHeader( headerKey, headerValue );

            // write into file
            OutputStream responseStream = null;
            try {
                responseStream = response.getOutputStream();
                workbook.write( responseStream );
            } catch ( IOException e ) {
                throw new NonFatalException( "IOException caught in getIncompleteSurveyFile(). Nested exception is ", e );
            } finally {
                try {
                    responseStream.close();
                } catch ( IOException e ) {
                    throw new NonFatalException( "IOException caught in getIncompleteSurveyFile(). Nested exception is ", e );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception occured while downloading the company report , reason " + e.getMessage() );
        }
    }


    @RequestMapping ( value = "/showabusereports", method = RequestMethod.GET)
    public String showAbuseReports()
    {
        LOG.info( "Inside showAbuseReports() method" );

        return JspResolver.ADMIN_ABUSE_REPORTS_VIEW;
    }


    @RequestMapping ( value = "/fetchsurveybyabuse", method = RequestMethod.GET)
    public String fetchSurveyByAbuse( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get abusive surveys fetchSurveyByAbuse() started." );
        try {
            String startIndexStr = request.getParameter( "startIndex" );
            String batchSizeStr = request.getParameter( "batchSize" );
            int startIndex = Integer.parseInt( startIndexStr );
            int batchSize = Integer.parseInt( batchSizeStr );

            List<AbusiveSurveyReportWrapper> abusiveSurveyReports = surveyHandler.getSurveysReportedAsAbusive( startIndex,
                batchSize );

            model.addAttribute( "abusiveReviewReportList", abusiveSurveyReports );
        } catch ( NumberFormatException e ) {
            LOG.error(
                "NumberFormat exception caught in fetchSurveyByAbuse() while fetching abusive reviews. Nested exception is ", e );
            model.addAttribute( "message", e.getMessage() );
        }
        LOG.info( "Method to get abusive surveys fetchSurveyByAbuse() finished." );
        return JspResolver.ADMIN_ABUSIVE_REPORTS;
    }


    @RequestMapping ( value = "/unmarkabusivereview", method = RequestMethod.GET)
    public String unmarkAbusiveReview( Model model, HttpServletRequest request )
    {
        LOG.info( "Method unmarkAbusiveReview started." );

        try {
            String surveyId = request.getParameter( "surveyId" );
            if ( surveyId == null || surveyId.isEmpty() ) {
                throw new InvalidInputException( "Invalid input survey id in " );
            }

            SurveyDetails surveyDetails = surveyHandler.getSurveyDetails( surveyId );
            if ( surveyDetails == null ) {
                throw new InvalidInputException( "No survey found for the survey id :  " + surveyId );
            }

            User user = userManagementService.getUserByUserId( surveyDetails.getAgentId() );
            if ( user == null ) {
                throw new InvalidInputException( "No user found for the survey id :  " + surveyId );
            }

            surveyHandler.updateSurveyAsUnAbusive( surveyId );
            //post on social media if review is reported abusive by application
            if ( ! surveyDetails.isAbuseRepByUser() ) {
                if ( surveyDetails.getAgreedToShare().equalsIgnoreCase( CommonConstants.AGREE_SHARE_COLUMN_TRUE ) ) {
                    LOG.debug( "Survey is reported bu user so auto posting on social media" );
                    String serverBaseUrl = requestUtils.getRequestServerName( request );
                    socialManagementService.postToSocialMedia( surveyDetails.getAgentName(), user.getProfileUrl(),
                        surveyDetails.getCustomerFirstName(), surveyDetails.getCustomerLastName(), surveyDetails.getAgentId(),
                        surveyDetails.getScore(), surveyDetails.getCustomerEmail(), surveyDetails.getReview(), false,
                        serverBaseUrl , false );
                }
            }

        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInput exception caught in unmarkAbusiveReview(). Nested exception is ", e );
            model.addAttribute( "message", messageUtils.getDisplayMessage( e.getMessage(), DisplayMessageType.ERROR_MESSAGE ) );
        } catch (NonFatalException e) {
            LOG.error( "NonFatal exception caught in unmarkAbusiveReview(). Nested exception is ", e );
            model.addAttribute( "message", messageUtils.getDisplayMessage( e.getMessage(), DisplayMessageType.ERROR_MESSAGE ) );
        }
        LOG.info( "Method unmarkAbusiveReview finished." );
        model.addAttribute( "message", messageUtils.getDisplayMessage(
            DisplayMessageConstants.UNMARK_ABUSIVE_SURVEY_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE ) );
        return JspResolver.MESSAGE_HEADER;
    }


    @RequestMapping ( value = "/generateApiKey", method = RequestMethod.POST)
    public @ResponseBody String generateApiKey( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to get generateApiKey() started." );
        String message = "";
        Map<String, String> map = new HashMap<String, String>();
        boolean error = false;
        String companyId = request.getParameter( "companyId" );
        String apiKey = request.getParameter( "apiKey" );
        String apiSecret = request.getParameter( "apiSecret" );
        if ( companyId == null || companyId.isEmpty() ) {
            message = DisplayMessageConstants.INVALID_COMPANY_ID;
            error = true;
        }
        if ( apiKey == null || apiKey.isEmpty() ) {
            message = DisplayMessageConstants.INVALID_API_KEY;
            error = true;
        }
        if ( apiSecret == null || apiSecret.isEmpty() ) {
            message = DisplayMessageConstants.INVALID_API_SECRET;
            error = true;
        }
        if ( !error ) {
            map.put( CommonConstants.COMPANY_ID_COLUMN, companyId );
            map.put( CommonConstants.API_KEY_COLUMN, apiKey );
            map.put( CommonConstants.API_SECRET_COLUMN, apiSecret );
            try {
                userManagementService.validateUserApiKey( apiKey, apiSecret, Long.valueOf( companyId ) );
            } catch ( NumberFormatException e ) {
                LOG.error( "Invalid Company Id " );
                message = DisplayMessageConstants.INVALID_COMPANY_ID;
                error = true;
            } catch ( InvalidInputException e ) {
                message = DisplayMessageConstants.INVALID_DETAILS_PROVIDED;
                error = true;
            }
        }

        if ( !error ) {
            LOG.debug( "All values provided are valid, hence generating api key for this company " + companyId );

            StringBuilder plainText = new StringBuilder();

            // The parameters are arranged in format key=value separated by &.      
            for ( String key : map.keySet() ) {
                plainText.append( key );
                plainText.append( "=" );
                plainText.append( map.get( key ) );
                plainText.append( "&" );
            }


            try {
                message = encryptionHelper.encryptAES( plainText.toString(), "" );
            } catch ( InvalidInputException e ) {
                message = DisplayMessageConstants.TRY_AGAIN;
            }
        }

        return message;

    }
}
