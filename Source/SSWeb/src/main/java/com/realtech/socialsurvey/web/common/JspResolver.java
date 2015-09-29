package com.realtech.socialsurvey.web.common;

/**
 * Holds the jsp name as constants
 */
public interface JspResolver {
	// Registration
	public static final String INVITATION = "invitation";
	public static final String REGISTRATION = "registration";
	public static final String SIGNUP = "signup";
	public static final String REGISTRATION_PAGE = "registrationpage";
	public static final String COMPANY_INFORMATION = "companyinformation";
	public static final String COMPANY_INFORMATION_PAGE = "companyinformationpage";
	public static final String ACCOUNT_TYPE_SELECTION = "accounttypeselection";
	public static final String ACCOUNT_TYPE_SELECTION_PAGE = "selectaccounttype";
	public static final String COMPLETE_REGISTRATION = "completeregistration";
	public static final String COMPLETE_REGISTRATION_PAGE = "completeregistrationpage";
	public static final String USER_ID_ON_INVITE = "useridoninvite";
	public static final String REGISTRATION_INVITE_SUCCESSFUL = "registration-invite-successful";
	public static final String REGISTRATION_LINK_EXPIRED = "registration-link-expired";
	public static final String NO_PROFILES_FOUND = "noprofilesfound";
	public static final String NO_ACTIVE_PROFILES = "noactiveprofiles";
	public static final String LINK_EXPIRED = "link_expired";
	public static final String LINK_EXPIRED_PAGE = "link_expired_page";

	// Generic pages
	public static final String INDEX = "index";
	public static final String LOGIN = "login";
	public static final String PROFILE_LIST = "proList";
	public static final String PROFILE_LIST_NOSCRIPT = "proList_noscript";
	public static final String COMPANY_LIST = "company_list";
	public static final String PROFILE_PAGE = "profile";
	public static final String PROFILE_PAGE_NOSCRIPT = "profile_noscript";
	public static final String MESSAGE_HEADER = "messageheader";
	public static final String ERROR_PAGE = "errorpage500";
	public static final String NOT_FOUND_PAGE = "errorpage404";
	public static final String FINDAPRO = "findapro";

	// Password handling
	public static final String CHANGE_PASSWORD = "changepassword";
	public static final String FORGOT_PASSWORD = "forgotpassword";
	public static final String RESET_PASSWORD = "resetpassword";

	// Payment handling
	public static final String PAYMENT = "payment";
	public static final String PAYMENT_ALREADY_MADE = "paymentalreadymade";
	public static final String UPGRADE_CONFIRMATION = "upgradeconfirmation";

	// Dashboard
	public static final String LINKEDIN_IMPORT = "linkedin_import";
	public static final String LINKEDIN_IMPORT_SOCIAL_LINKS = "linkedin_import_social_links";
	public static final String LINKEDIN_COMPARE = "linkedin_compare";
	public static final String HEADER_SURVEY_INVITE = "header_sendsurveyinvite";
	public static final String HEADER_SURVEY_INVITE_ADMIN = "header_sendsurveyinvite_admin";
	public static final String ACCOUNT_DISABLED_PAGE = "accountdisabled";
	public static final String USER_LOGIN = "userlogin";
	public static final String LANDING = "landing";
	public static final String DASHBOARD = "dashboard";
	public static final String DASHBOARD_PROFILEDETAIL = "dashboard_profiledetail";
	public static final String DASHBOARD_SURVEYSTATUS = "dashboard_surveystatus";
	public static final String DASHBOARD_SEARCHRESULTS = "dashboard_searchresults";
	public static final String DASHBOARD_INCOMPLETESURVEYS = "dashboard_incompletesurveys";
	public static final String DASHBOARD_REVIEWS = "dashboard_reviews";
	public static final String HEADER_DASHBOARD_INCOMPLETESURVEYS = "header_incompletesurvey";

	// Hierarchy Management
	public static final String BUILD_HIERARCHY = "build-hierarchy";
	public static final String HIERARCHY_REGION_EDIT = "hierarchy-region-edit";
	public static final String HIERARCHY_OFFICE_EDIT = "hierarchy-office-edit";
	public static final String HIERARCHY_INDIVIDUAL_EDIT = "hierarchy-individual-edit";
	public static final String VIEW_HIERARCHY = "hierarchy-view";
	public static final String VIEW_HIERARCHY_REGION_LIST = "hierarchy-view-regions-list";
	public static final String VIEW_HIERARCHY_BRANCH_LIST = "hierarchy-view-branches-list";
	public static final String VIEW_HIERARCHY_USERS_LIST = "hierarchy-view-users-list";

	// Survey Management
	public static final String SURVEY_BUILDER = "buildSurvey";
	public static final String SURVEY_BUILDER_QUESTION_OVERLAY = "buildSurvey_questionoverlay";
	public static final String SURVEY_BUILDER_QUESTION_LIST = "buildSurvey_questionlist";
	public static final String SURVEY_BUILDER_QUESTION_EDIT = "buildSurvey_questionedit";
	public static final String SURVEY_BUILDER_QUESTION_NEW = "buildSurvey_questionnew";
	public static final String SHOW_SURVEY_QUESTIONS = "surveyQuestion";
	public static final String SURVEY_REQUEST = "surveyRequest";
	public static final String SURVEY_INVITE_SUCCESSFUL = "survey-request-successful";

	// User Management
	public static final String USER_MANAGEMENT = "view-user-management";
	public static final String USER_LIST_FOR_MANAGEMENT = "userlist";
	public static final String USER_MANAGEMENT_EDIT_USER_DETAILS = "um-edit-row";
	public static final String USER_DETAILS = "userdetails";
	public static final String USER_LIST = "userslist";

	// Edit Settings
	public static final String EDIT_SETTINGS = "settings";
	public static final String EMAIL_SETTINGS = "email_settingss";
	public static final String APP_SETTINGS = "app_settings";
	public static final String SOCIAL_AUTH_MESSAGE = "socialauthmessage";
	public static final String SOCIAL_FACEBOOK_INTERMEDIATE = "facebookintermediate";
	public static final String SOCIAL_ZILLOW_INTERMEDIATE = "zillowintermediate";
	public static final String LINKEDIN_ACCESS = "linkedinaccess";
	public static final String SOCIAL_MEDIA_TOKENS = "settings_socialauth";

	//Help Settings
	public static final String HELP_EDIT="help";
	
	// Profile Settings
	public static final String PROFILE_EDIT = "profile_edit";
	public static final String PROFILE_CONTACT_DETAILS = "profile_contactdetails";
	public static final String PROFILE_ABOUT_ME = "profile_aboutme";
	public static final String PROFILE_ADDRESS_DETAILS = "profile_addressdetails";
	public static final String PROFILE_ADDRESS_DETAILS_EDIT = "profile_addressdetails_edit";
	public static final String PROFILE_POSITIONS_EDIT = "profile_position_edit";
	public static final String PROFILE_BASIC_DETAILS = "profile_basicdetails";
	public static final String PROFILE_IMAGE = "profile_profileimage";
	public static final String PROFILE_LOGO = "profile_profilelogo";
	public static final String PROFILE_REVIEWS = "profile_reviews";
	public static final String PROFILE_HIERARCHY = "profile_hierarchy";
	public static final String PROFILE_HIERARCHY_CLICK_REGION = "profile_hierarchy_region";
	public static final String PROFILE_HIERARCHY_CLICK_BRANCH = "profile_hierarchy_branch";
	public static final String PROFILE_URL_WARNING = "profile_url_warning";
	public static final String PROFILE_URL_CHANGE = "profile_url_change";

	// Admin pages
	public static final String ADMIN_LOGIN = "admin/admin_login";
	public static final String ADMIN_LANDING = "admin/admin_landing";
	public static final String ADMIN_DASHBOARD = "admin/admin_dashboard";
	public static final String ADMIN_HIERARCHY_VIEW = "admin/admin_hierarchy_view";
	public static final String ADMIN_INVITE_VIEW = "admin/admin_invite_view";
	public static final String ADMIN_COMPANY_HIERARCHY = "admin/company_hierarchy";
	public static final String ADMIN_COMPANY_LIST = "admin/admin_company_list";
	public static final String ADMIN_REGION_HIERARCHY = "admin/admin_region_hierarchy";
	public static final String ADMIN_BRANCH_HIERARCHY = "admin/admin_branch_hierarchy";
	public static final String ADMIN_COMPANY_NOT_REGISTERED = "admin/company_not_registered";
}