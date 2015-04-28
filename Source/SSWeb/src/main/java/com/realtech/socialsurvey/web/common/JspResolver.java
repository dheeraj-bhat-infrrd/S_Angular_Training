package com.realtech.socialsurvey.web.common;

/**
 * Holds the jsp name as constants
 */
public interface JspResolver {
	// Registration
	public static final String REGISTRATION = "registration";
	public static final String COMPANY_INFORMATION = "companyinformation";
	public static final String ACCOUNT_TYPE_SELECTION = "accounttypeselection";
	public static final String COMPLETE_REGISTRATION = "completeregistration";
	public static final String LINKEDIN_ACCESS = "linkedinaccess";
	public static final String LINKEDIN_COMPARE = "linkedin_compare";
	public static final String INVITATION = "invitation";
	public static final String USER_ID_ON_INVITE = "useridoninvite";

	// Generic pages
	public static final String INDEX = "index";
	public static final String MESSAGE_HEADER = "messageheader";
	public static final String ERROR_PAGE = "errorpage500";
	public static final String LOGIN = "login";
	public static final String PROFILE_LIST = "proList";
	public static final String PROFILE_PAGE = "profile";

	// Password handling
	public static final String CHANGE_PASSWORD = "changepassword";
	public static final String FORGOT_PASSWORD = "forgotpassword";
	public static final String RESET_PASSWORD = "resetpassword";

	// Payment handling
	public static final String PAYMENT = "payment";
	public static final String PAYMENT_ALREADY_MADE = "paymentalreadymade";
	public static final String UPGRADE_CONFIRMATION = "upgradeconfirmation";

	// Dashboard
	public static final String USER_LOGIN = "userlogin";
	public static final String ACCOUNT_DISABLED_PAGE = "accountdisabled";
	public static final String LANDING = "landing";
	public static final String DASHBOARD = "dashboard";
	public static final String DASHBOARD_PROFILEDETAIL = "dashboard_profiledetail";
	public static final String DASHBOARD_SURVEYSTATUS = "dashboard_surveystatus";
	public static final String DASHBOARD_SEARCHRESULTS = "dashboard_searchresults";
	public static final String DASHBOARD_INCOMPLETESURVEYS = "dashboard_incompletesurveys";
	public static final String DASHBOARD_REVIEWS = "dashboard_reviews";

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

	// User Management
	public static final String USER_MANAGEMENT = "view-user-management";
	public static final String USER_LIST_FOR_MANAGEMENT = "userlist";
	public static final String USER_MANAGEMENT_EDIT_USER_DETAILS = "um-edit-row";
	public static final String USER_DETAILS = "userdetails";
	public static final String USER_LIST = "userslist";

	// Edit Settings
	public static final String EDIT_SETTINGS = "settings";
	public static final String SOCIAL_AUTH_MESSAGE = "socialauthmessage";

	// Profile Settings
	public static final String PROFILE_EDIT = "profile_edit";
	public static final String PROFILE_CONTACT_DETAILS = "profile_contactdetails";
	public static final String PROFILE_ABOUT_ME = "profile_aboutme";
	public static final String PROFILE_ADDRESS_DETAILS = "profile_addressdetails";
	public static final String PROFILE_ADDRESS_DETAILS_EDIT = "profile_addressdetails_edit";
	public static final String PROFILE_BASIC_DETAILS = "profile_basicdetails";
	public static final String PROFILE_IMAGE = "profile_profileimage";
	public static final String PROFILE_LOGO = "profile_profilelogo";
	public static final String PROFILE_REVIEWS = "profile_reviews";
	public static final String PROFILE_HIERARCHY = "profile_hierarchy";
	public static final String PROFILE_HIERARCHY_CLICK_REGION = "profile_hierarchy_region";
	public static final String PROFILE_HIERARCHY_CLICK_BRANCH = "profile_hierarchy_branch";
}