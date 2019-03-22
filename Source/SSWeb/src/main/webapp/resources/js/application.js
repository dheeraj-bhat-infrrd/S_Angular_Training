//Functions to detect browser
var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;

// Default maximum value for number of social posts, surveys sent in 30 days.
var maxSocialPosts = 10;
var maxSurveySent = 10;
var startIndexCmp;
var batchSizeCmp;
var totalReviews;
var reviewsFetchedSoFar;
var startIndexInc;
var batchSizeInc;
var totalReviewsInc;
var surveyFetchedSoFarInc;
var accountType;
var graphData;

// colName and colValue contains profile level of logged in user and value for
// colName is present in colValue.
var colName;
var colValue;
var searchColumn;
var lastColNameForCount;
var lastColValueForCount;
var lastColNameForGraph;
var lastColValueForGraph;
var lastColNameForGraphTrans;
var lastColValueForGraphTrans;
var lastColNameForGraphProcSurvey;
var lastColValueForGraphProcSurvey;

var lastColNameForGraphActvUser;
var lastColValueForGraphActvUser;

// Variables for processing Edit profile
var startIndex = 0;
var numOfRows = 3;
var minScore = 0;
var attrName = null;
var attrVal = null;
var webAddressRegEx = /((http(s?):\/\/)?)(www.)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w.?=&_]+)?/;
var timer = 0;
var profileId;
var proPostBatchSize = 10;
var proPostStartIndex = 0;
var proPostCount = 0;
var delay = (function() {
	return function(callback, ms) {
		clearTimeout(timer);
		timer = setTimeout(callback, ms);
	};
})();

// User management
var usersStartIndex = 0;
var numOfRows = 10;

// User management
var userStartIndex = 0;
var userBatchSize = 10;
var doStopAjaxRequestForUsersList = false;
var listOfBranchesForAdmin;
var isUserManagementAuthorized = true;
var isAddUser = true;

// Variables for editprofile page
var editProfileForYelp = false;
var editProfileForLicense = false;
var editProfileForHobbies = false;
var editProfileForAchievements = false;

// Variables for survey question page
var qno = 0;
var questions;
var questionDetails;
var agentId;
var agentName;
var customerResponse;
var customerEmail;
var companyName;
var surveyId;
var mood;
var stage;
var isSmileTypeQuestion = true;
var swearWords = [];
var isAbusive;
var autoPost;
var autoPostScore;
var happyText;
var neutralText;
var sadText;
var happyTextComplete;
var neutralTexCompletet;
var sadTextComplete;
var happyUrl;
var okUrl;
var sadUrl;
var rating = -1; // default value to be used to post on social survey in case of "ok" or "unpleasant" mood
var reviewText;
var reviewRating;
var firstName;
var lastName;
var surveyUrl = "/rest/survey/";
var editable;
var yelpEnabled;
var googleEnabled;
var zillowEnabled;
var lendingtreeEnabled;
var realtorEnabled;
var googleBusinessEnabled;
var agentProfileLink;
var agentFullProfileLink;
var companyLogo;
var zillowReviewLink;
var isAutoFillReviewContentForZillowPost;
var subjectContentForZillowPost;
var reviewFooterContentForZillowPost;

// Verticals master
var verticalsMasterList;

// Variables for social monitor
var autocompleteData;
var companyIdForSocialMonitor;
var smScrollTop = 0;

var defaultCountryCode = "US";
var defaultCountry = "United States";

var fb_app_id;
var google_plus_app_id;
var isZillowReviewsCallRunning = false;
var zillowCallBreak = false;
var existingCall;
var classificationsList = [];

var ratingQuestionCount= 0;

var npsQuestionText = '';
var veryLikelyText = '';
var notVeryLikelyText = '';
var npsOrder = 999;

var defaultNpsQuestion = 'How likely are you to refer friends and family to [name]?';
var defaultNotVeryLikely = 'Not Very Likely';
var defaultVeryLikely = 'Very Likely';

//variables for transaction monitor
var sysAutoTransGraphId = 'sys-auto-trans-graph';
var sysCompTransGraphId = 'sys-sur-comp-graph';
var sysInvSentGraphId = 'sys-invite-sent-graph';
var sysRemSentGraphId = 'sys-rem-sent-graph';
var sysUnproTransGraphId = 'sys-unpro-trans-graph';

// URL redirect changes
var suveySourceId;
var participationType;
var transactionType;
var state;
var city;
var customeField1;
var customeField2;
var customeField3;
var customeField4;
var customeField5;

// Social Monitor setting
var SOCIAL_MONITOR_PAGE_SIZE = 25;


var dangerGraphWrapper = '<div class="dash-stats-wrapper bord-bot-dc clearfix trans-monitor-wrapper">'
						+'<div class="trans-monitor-sub-header-danger">'
						+'<div class="trans-monitor-sub-header-box-danger"></div>'
						+'<span id="trans-wrapper-header-span-danger" class="trans-monitor-sub-header-span"></span>'
						+'</div>'
						+'<div id="trans-graph-container" class="trans-monitor-graphs-wrapper"></div></div>';

var warnGraphWrapper = '<div class="dash-stats-wrapper bord-bot-dc clearfix trans-monitor-wrapper">'
						+'<div class="trans-monitor-sub-header-warn">'
						+'<div class="trans-monitor-sub-header-box-warn"></div>'
						+'<span id="trans-wrapper-header-span-warn" class="trans-monitor-sub-header-span"></span>'
						+'</div>'
						+'<div id="trans-graph-container" class="trans-monitor-graphs-wrapper"></div></div>';

var grayGraphWrapper = '<div class="dash-stats-wrapper bord-bot-dc clearfix trans-monitor-wrapper">'
					  +'<div class="trans-monitor-sub-header-gray">'
					  +'<div class="trans-monitor-sub-header-box-gray"></div>'
					  +'<span id="trans-wrapper-header-span-gray" class="trans-monitor-sub-header-span"></span>'
					  +'</div>'
					  +'<div id="trans-graph-container" class="trans-monitor-graphs-wrapper"></div></div>';

var normalGraphWrapper = '<div class="dash-stats-wrapper bord-bot-dc clearfix trans-monitor-wrapper">'
	  					+'<div class="trans-monitor-sub-header-normal">'
	  					+'<div class="trans-monitor-sub-header-box-normal"></div>'
	  					+'<span id="trans-wrapper-header-span-normal" class="trans-monitor-sub-header-span"></span>'
	  					+'</div>'
	  					+'<div id="trans-graph-container" class="trans-monitor-graphs-wrapper"></div></div>';

var dangerGraphContainer = '<div class="trans-monitor-graph-col-danger">'
						  +'<span class="trans-monitor-graph-span"></span>'
						  +'<div id="trans-graph" class="trans-monitor-graph-div"></div></div>';

var warnGraphContainer = '<div class="trans-monitor-graph-col-warn">'
	  					+'<span class="trans-monitor-graph-span"></span>'
	  					+'<div id="trans-graph" class="trans-monitor-graph-div"></div></div>';

var grayGraphContainer = '<div class="trans-monitor-graph-col-gray">'
	  					+'<span class="trans-monitor-graph-span"></span>'
	  					+'<div id="trans-graph" class="trans-monitor-graph-div"></div></div>';

var normalGraphContainer = '<div class="trans-monitor-graph-col-normal">'
	  					  +'<span class="trans-monitor-graph-span"></span>'
	  					  +'<div id="trans-graph" class="trans-monitor-graph-div"></div></div>';

var automatedTransText = 'Automated Transactions';
var inviteSentText = 'Invitations-sent';
var reminderSentText = 'Reminders Sent';
var completedTransText = 'Surveys Completed';
var unprocessedTransText = 'Unprocessed Transactions';

var autoType = 1;
var inviType = 2;
var remType = 3;
var compType = 4;
var unproType = 5;

var pastWeekTransMonAutoGraphData = new Array();
var pastWeekTransMonInviGraphData = new Array();
var pastWeekTransMonRemGraphData = new Array();
var pastWeekTransMonCompGraphData = new Array();
var pastWeekTransMonUnproGraphData = new Array();

var curWeekTransMonAutoGraphData = new Array();
var curWeekTransMonInviGraphData = new Array();
var curWeekTransMonRemGraphData = new Array();
var curWeekTransMonCompGraphData = new Array();
var curWeekTransMonUnproGraphData = new Array();

var KEYWORD_MONITOR='KEYWORD_MONITOR';
var GOOGLE_ALERTS='GOOGLE_ALERTS';
var jspData;

var widgetDropDownHandlerSetup = false;

/**
 * js functions for landing page
 */
/**
 * function to change the content of page through ajax
 * 
 * @param url
 */
function showMainContent(url) {

	closeMoblieScreenMenu();
	saveState(url);
	callAjaxGET(url, showMainContentCallBack, true);
}

/**
 * Callback for showMainContent, displays data in the main content section
 * 
 * @param data
 */
function showMainContentCallBack(data) {
	$("#main-content").html(data);
	hideDashOverlay('#logo-dash');
	hideOverlay();
}

$(window).resize(function() {
	if ($(window).width() > 767) {
		if ($('#header-slider-wrapper').hasClass('rt-panel-slide')) {
			closeMoblieScreenMenu();
		}
	}
});

function closeMoblieScreenMenu() {
	$('#header-slider-wrapper').removeClass('rt-panel-slide');
	enableBodyScroll();
}

// Function to logout
function userLogout() {
	if(sessionStorage) {
		sessionStorage.clear();
	}
	
	window.location.href = 'j_spring_security_logout';
}

/*
 * This module helps in browser navigation support to give a single page app
 * 
 * Functions for history support
 */

var historyCallback = false;
var refreshSupport = true;

function getRandomID() {
	return (Math.floor(Math.random() * 10000) + Math.floor(Math.random() * 10000));
}

function saveState(url) {

	var hashUrl = "";
	hashUrl = url.substring(2).split('.')[0];
	if (!historyCallback) {
		history.pushState(getRandomID(), null, "#" + hashUrl);

	}
	historyCallback = false;
}

function retrieveState() {
	if (!refreshSupport) {
		// refresh not supported
		return;
	}
	var newLocation = window.location.hash.substring(1);
	if (newLocation) {
		showMainContent("/" + newLocation + ".do");
	}
}
/* End of functions for history support */

/*
 * Click event to close survey popup
 */

$(document).on('click', function(e) {
	var close = false;
	if ($('#overlay-send-survey').is(':visible')) {
		$('#overlay-send-survey').find('#wc-review-table-inner').children().each(function() {
			if (!$(this).hasClass('wc-review-hdr')) {
				$(this).children().each(function() {
					if (!$(this).hasClass('last')) {
						var input = $(this).children(":input").val();
						console.log(input);
						if (input != "") {
							close = true;
							$('#overlay-header-survey').html("Warning");
							$('#overlay-text-survey').html("Closing this window without submitting will delete any data rows entered.Are you sure you want to close?")
							$('#overlay-continue-survey').html("Ok");
							$('#overlay-cancel-survey').html("Cancel");
							$('#overlay-main-survey').show();
							$('#overlay-continue-survey').off();
							$('#overlay-continue-survey').click(function() {
								$('#overlay-main-survey').hide();
								$('#overlay-send-survey').hide();
								enableBodyScroll();
							});
							$('#overlay-cancel-survey').off();
							$('#overlay-cancel-survey').click(function() {
								$('#overlay-main-survey').hide();
							});
						}
					}

				});

			}

		});
		if (!close) {
			$('#overlay-send-survey').hide();
			enableBodyScroll();
		}

	}
	if ($('#report-abuse-overlay').is(':visible')) {
		$('#report-abuse-overlay').hide();
		enableBodyScroll();
	}
	if ($('#overlay-main').is(':visible')) {
		$('#overlay-main').hide();
		enableBodyScroll();
	}
	if ($('.overlay-payment').is(':visible')) {
		$('.overlay-payment').hide();
		enableBodyScroll();
	}
	if ($('#overlay-incomplete-survey').is(':visible')) {
		$('#overlay-incomplete-survey').hide();
		enableBodyScroll();
	}
	if ($('#email-map-pop-up').is(':visible')) {
		$('#email-map-pop-up').hide();
		enableBodyScroll();
	}
	if ($('#zillow-popup').is(':visible')) {
		$('#zillow-popup-body').html('');
		$('#zillow-popup').hide();
		enableBodyScroll();
	} 
	checkSocMonDropdowns(null);
	
	if($('#monitor-bulk-action-options').is(':visible')){
		$('#monitor-bulk-action-options').toggle();
		$('#monitor-chevron-down').toggle();
		$('#monitor-chevron-up').toggle();
	}
	
	if ($('#add-mon-type-options').is(':visible')) {
		$('#add-mon-type-options').hide();
		$('#add-mon-type-chevron-down').show();
		$('#add-mon-type-chevron-up').hide();
	}
	
	if ($('#action-popup').is(':visible')) {
		actionPopupRevert();
	}
	
	if ($('#mon-type-options').is(':visible')) {
		$('#mon-type-options').toggle();
		$('#mon-type-chevron-down').toggle();
		$('#mon-type-chevron-up').toggle();	
	}
	
	if ($('#add-macro-alerts-options').is(':visible')) {
		$('#add-macro-alerts-options').toggle();
		$('#macro-alerts-chevron-down').toggle();
		$('#macro-alerts-chevron-up').toggle();
	}

	if ($('#summit-popup-body').is(':visible')) {
		closeSummitPopup();
	}
	

	if ($('#add-macro-action-options').is(':visible')) {
		$('#add-macro-action-options').toggle();
		$('#macro-action-chevron-down').toggle();
		$('#macro-action-chevron-up').toggle();
	}
	
});

function checkSocMonDropdowns(e){
	
	if(e!=null){
		if(e.currentTarget.attributes[0].nodeValue != 'stream-usr-selection'){
			if($('#stream-usr-dropdown-options').is(':visible')){
				$('#stream-usr-dropdown-options').toggle();
				$('#usr-chevron-down').toggle();
				$('#usr-chevron-up').toggle();
			}
		}
		
		if(e.currentTarget.attributes[0].nodeValue != 'stream-seg-selection'){
			if($('#stream-seg-dropdown-options').is(':visible')){
				$('#stream-seg-dropdown-options').toggle();
				$('#seg-chevron-down').toggle();
				$('#seg-chevron-up').toggle();
			}	
		}
		
		if(e.currentTarget.attributes[0].nodeValue != 'stream-feed-selection'){
			if($('#stream-feed-dropdown-options').is(':visible')){
				$('#stream-feed-dropdown-options').toggle();
				$('#feed-chevron-down').toggle();
				$('#feed-chevron-up').toggle();
			}
		}
			
		if(e.currentTarget.attributes[0].nodeValue != 'stream-bulk-actions'){
			if($('#stream-bulk-action-options').is(':visible')){
				$('#stream-bulk-action-options').toggle();
				$('#chevron-down').toggle();
				$('#chevron-up').toggle();
			}
		}
	}else{
		if($('#stream-usr-dropdown-options').is(':visible')){
			$('#stream-usr-dropdown-options').toggle();
			$('#usr-chevron-down').toggle();
			$('#usr-chevron-up').toggle();
		}
		
		if($('#stream-seg-dropdown-options').is(':visible')){
			$('#stream-seg-dropdown-options').toggle();
			$('#seg-chevron-down').toggle();
			$('#seg-chevron-up').toggle();
		}	
		
		if($('#stream-feed-dropdown-options').is(':visible')){
			$('#stream-feed-dropdown-options').toggle();
			$('#feed-chevron-down').toggle();
			$('#feed-chevron-up').toggle();
		}
		
		if($('#stream-bulk-action-options').is(':visible')){
			$('#stream-bulk-action-options').toggle();
			$('#chevron-down').toggle();
			$('#chevron-up').toggle();
		}
	}	
	
	
	$('.macro-options-list').each(function(){
		if($(this).is(':visible')){
			$(this).addClass('hide');
			$(this).parent().find('.stream-macro-dropdown').find('.mac-chevron-down').removeClass('hide');
			$(this).parent().find('.stream-macro-dropdown').find('.mac-chevron-up').addClass('hide');
		}
	});
}

$(document).on('keyup', function(e) {
	if (e.keyCode == 27) {
		if ($('#overlay-send-survey').is(':visible')) {
			$('#overlay-send-survey').hide();
			enableBodyScroll();
		}
		if ($('#report-abuse-overlay').is(':visible')) {
			$('#report-abuse-overlay').hide();
			enableBodyScroll();
		}
		if ($('#overlay-main').is(':visible')) {
			$('#overlay-main').hide();
			enableBodyScroll();
		}
		if ($('.overlay-payment').is(':visible')) {
			$('.overlay-payment').hide();
			enableBodyScroll();
		}
		if ($('#overlay-incomplete-survey').is(':visible')) {
			$('#overlay-incomplete-survey').hide();
			enableBodyScroll();
		}
		if ($('#email-map-pop-up').is(':visible')) {
			$('#email-map-pop-up').hide();
			enableBodyScroll();
		}
		if ($('#zillow-popup').is(':visible')) {
			$('#zillow-popup').hide();
			$('#zillow-popup-body').html('');
			enableBodyScroll();
		}

		checkSocMonDropdowns(null);
		
		if($('#monitor-bulk-action-options').is(':visible')){
			$('#monitor-bulk-action-options').toggle();
			$('#monitor-chevron-down').toggle();
			$('#monitor-chevron-up').toggle();
		}
		
		$('#bulk-options-popup').hide();
		
		if($('#duplicate-post-popup').length > 0){
			$('#dup-post-add-post-action').find('.form-is-dup').val(false);
			$('#macro-form-is-dup').val(false);
			$('#duplicate-post-popup').addClass('hide');
		}
		
		hideAddMonitorPopup();
		
		if ($('#action-popup').is(':visible')) {
			actionPopupRevert();
		}
		
		if ($('#mon-type-options').is(':visible')) {
			$('#mon-type-options').toggle();
			$('#mon-type-chevron-down').toggle();
			$('#mon-type-chevron-up').toggle();	
		}
		
		if ($('#add-macro-alerts-options').is(':visible')) {
			$('#add-macro-alerts-options').toggle();
			$('#macro-alerts-chevron-down').toggle();
			$('#macro-alerts-chevron-up').toggle();
		}

		if ($('#add-macro-action-options').is(':visible')) {
			$('#add-macro-action-options').toggle();
			$('#macro-action-chevron-down').toggle();
			$('#macro-action-chevron-up').toggle();
		}
		if ($('#summit-popup-body').is(':visible')) {
			closeSummitPopup();
		}
		
		if($('#mismatch-new-popup-main').is(':visible')){
			$('#mismatch-new-popup-main').addClass('hide');
			resetMismatchPopup();
		}
	}
});

/**
 * if($('#report-abuse-overlay' ).is(':visible')){ $('#report-abuse-overlay').hide(); enableBodyScroll(); } if($('#overlay-main' ).is(':visible')){ $('#overlay-main').hide(); enableBodyScroll(); } if($('#report-abuse-overlay' ).is(':visible')){ $('#report-abuse-overlay').hide(); enableBodyScroll(); } if($('#overlay-main' ).is(':visible')){ $('#overlay-main').hide(); enableBodyScroll(); }
 */
$(document).on('click', '#email-overlay', function(e) {
	e.stopPropagation();
});
$(document).on('click', '#payment-data-container', function(e) {
	e.stopPropagation();
});
$(document).on('click', '#welcome-popup-invite', function(e) {
	e.stopPropagation();
});
$(document).on('click', '#overlay-pop-up', function(e) {
	e.stopPropagation();
});

$(document).on('click', '#disconnect-overlay-pop-up', function(e) {
	e.stopPropagation();
});
$(document).on('click', '#zillow-popup-body', function(e) {
	e.stopPropagation();
});
$(document).on('click', '.datepicker-months', function(e) {
	e.stopPropagation();
});
$(document).on('click', '.month', function(e) {
	e.stopPropagation();
});
$(document).on('click', '.year', function(e) {
	e.stopPropagation();
});
$(document).on('click', '#report-abuse-pop-up', function(e) {
	e.stopPropagation();
});
$(document).on('click', '#incomplete-survey-popup', function(e) {
	e.stopPropagation();
});

$(document).on('click', '.icn-plus-open', function() {
	$(this).hide();
	$(this).parent().find('.ppl-share-social,.icn-remove').show();
});

$(document).on('click', '.icn-remove', function() {
	$(this).hide();
	$(this).parent().find('.ppl-share-social').hide();
	$(this).parent().find('.icn-plus-open').show();
});

$(document).on('click', '#hr-txt2', function(e) {
	e.stopPropagation();
	$('#hr-dd-wrapper').slideToggle(200);
});

$(document).on('click', '.hr-dd-item', function(e) {
	e.stopPropagation();
});

$(document).on('click', '.restart-survey-mail-txt', function(e) {
	e.stopPropagation();
	confirmRetakeSurveyReminderMail(this);

});

function confirmRetakeSurveyReminderMail(element) {

	$('#overlay-header').html("Retake survey");
	$('#overlay-text').html('<div style="text-align:left; display: grid;">Are you sure that you want to email a request to your customer to re-take this survey?<span style="margin-top:20px; text-align:left"><span style="font-weight:bold !important">Note:</span> You should have already spoken with the customer, resolved their concerns and obtained permission to send them a re-take link. Once the customer has retaken this survey it will be updated and the original comments removed.</span></div>');
	$('#overlay-continue').html("Send");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-continue').off();
	$('#overlay-continue').click(function() {
		retakeSurveyReminderMail(element);
	});

	$('#overlay-main').show();
	disableBodyScroll();
}

function retakeSurveyReminderMail(element) {
	var surveyId = $(element).parent().parent().parent().parent().attr('survey-mongo-id');

	var payload = {
		"surveyId" : surveyId
	};

	callAjaxGetWithPayloadData('./restartsurvey.do', function() {
		$('#overlay-toast').html('Mail sent to customer to retake the survey for you.');
		showToast();
		$('#overlay-cancel').click();
		getIncompleteSurveyCount(colName, colValue);
	}, payload, true);
}

$(document).on('click', '.report-abuse-txt', function(e) {
	disableBodyScroll();
	e.stopPropagation();
	var reviewElement = $(this).closest('.dsh-review-cont');
	var payload = {
		"surveyMongoId" : reviewElement.attr('survey-mongo-id')
	};
	var r = reviewElement.attr('data-firstname');
	$("#report-abuse-txtbox").val('');
	console.log(r);
	// Unbind click events for button
	$('.rpa-cancel-btn').off('click');
	$('.rpa-report-btn').off('click');
	// disableBodyScroll();
	$('#report-abuse-overlay').show();
	$('.rpa-cancel-btn').on('click', function() {
		$('#report-abuse-overlay').hide();
		enableBodyScroll();
	});

	$('.rpa-report-btn').on('click', function() {

		var reportText = $("#report-abuse-txtbox").val();
		if (validateReportAbuseUserForm(reportText)) {
			showOverlay();
			payload.reportText = reportText;
			confirmUserReportAbuse(payload);
		}
	});
});

function validateReportAbuseUserForm(reportText) {
	// check if report text is empty
	if (reportText == undefined || reportText == "") {
		$('#overlay-toast').html('Please enter why you want to report the review!');
		showToast();
		return false;
	}
	return true;
}

function confirmUserReportAbuse(payload) {
	callAjaxGetWithPayloadData('./reportabuse.do', function(status) {
		$('#report-abuse-overlay').hide();

		if (status == 'success') {
			$('#overlay-toast').html('Reported Successfully!');
		} else {
			$('#overlay-toast').html('Failed to report abuse, Please try again later');
		}
		hideOverlay();
		showToast();
		enableBodyScroll();
	}, payload, true);
}

$('body').click(function() {
	$('#hr-dd-wrapper').slideUp(200);	
});

function paintDashboard(profileMasterId, newProfileName, newProfileValue, typeoOfAccount) {
	accountType = typeoOfAccount;
	startIndexCmp = 0;
	batchSizeCmp = 5;
	doStopPaginationDashboard = false;
	isDashboardReviewRequestRunning = false;
	reviewsFetchedSoFar = 0;
	startIndexInc = 0;
	batchSizeInc = 6;
	totalReviewsInc = 0;
	surveyFetchedSoFarInc = 0;

	var oldConW = $('.container').width();
	var newConW = $('.container').width();
	$(window).resize(function() {
		newConW = $('.container').width();
		if (newConW != oldConW) {
			paintSurveyGraph();
			oldConW = $('.container').width();
		}
	});
	lastColNameForCount = newProfileName;
	lastColValueForCount = newProfileValue;

	colName = newProfileName;
	colValue = newProfileValue;

	if (newProfileName == "companyId") {
		showCompanyAdminFlow(newProfileName, newProfileValue);
	} else if (newProfileName == "regionId") {
		showRegionAdminFlow(newProfileName, newProfileValue);
	} else if (newProfileName == "branchId") {
		showBranchAdminFlow(newProfileName, newProfileValue);
	} else if (newProfileName == "agentId") {
		showAgentFlow(newProfileName, newProfileValue);
	}

	// initializing datepickers
	bindDatePickerforSurveyDownload();
	bindDatePickerforIndividualSurveyDownload();

	getIncompleteSurveyCount(colName, colValue);
	/*
	 * if(is_dashboard_loaded === undefined){ //file never entered. the global var was not set. window.is_dashboard_loaded = 1; fetchReviewsOnDashboard(false); }else{ return; }
	 */
	fetchReviewsOnDashboard(false);
	bindAutosuggestForIndividualRegionBranchSearch('dsh-sel-item');
	bindAutosuggestForIndividualRegionBranchSearch('dsh-grph-sel-item');
}

function paintReportingDashboard(profileMasterId, newProfileName, newProfileValue, typeoOfAccount) {
	accountType = typeoOfAccount;
	startIndexCmp = 0;
	batchSizeCmp = 9;
	doStopPaginationDashboard = false;
	isDashboardReviewRequestRunning = false;
	reviewsFetchedSoFar = 0;
	startIndexInc = 0;
	totalReviewsInc = 0;
	surveyFetchedSoFarInc = 0;

	lastColNameForCount = newProfileName;
	lastColValueForCount = newProfileValue;

	colName = newProfileName;
	colValue = newProfileValue;

	fetchReviewsOnDashboard(false);
}

function bindAutosuggestForIndividualRegionBranchSearch(elementId) {
	// Bind keyup on search for region, branch, individual for dashboard
	$('#' + elementId).on('keyup', function(e) {
		var value = $(this).val();
		var prevVal = $(this).attr('data-prev-val');

		if (value != prevVal) {
			if (value === undefined || value == null || value.length <= 0) {
				$('#dsh-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-srch-res').hide();
				$('#dsh-srch-res').empty();
				return;
			}
			$(this).attr('data-prev-val', value);
			searchBranchRegionOrAgent(value, $(this).attr('data-search-target'));
		}
		// Detect arrow key down
		else if (e.which == 40) {
			if ($(this).next().is(':visible')) {
				var parentElement = $(this).next();
				var selectedElement = parentElement.find('.dsh-res-hover');
				if (selectedElement && selectedElement.length > 0 && selectedElement.next('.dsh-res-display') && selectedElement.next('.dsh-res-display').length > 0) {
					selectedElement.removeClass('dsh-res-hover');
					selectedElement.next('.dsh-res-display').addClass('dsh-res-hover');

					var updatedSelectedElement = parentElement.find('.dsh-res-hover');
					// check if the top of current selected element is over the parents top
					if ((updatedSelectedElement.offset().top - parentElement.offset().top + updatedSelectedElement[0].clientHeight) > parentElement[0].clientHeight) {
						var scrollTopPos = parentElement[0].scrollTop + updatedSelectedElement[0].clientHeight;
						parentElement[0].scrollTop = scrollTopPos;
					}
				} else {
					$(this).next().children('.dsh-res-display').removeClass('dsh-res-hover');
					$(this).next().children('.dsh-res-display').first('.dsh-res-display').addClass('dsh-res-hover');
					parentElement[0].scrollTop = 0;
				}
			}
		}
		// Detect arrow key up
		else if (e.which == 38) {
			if ($(this).next().is(':visible')) {
				var parentElement = $(this).next();
				var selectedElement = parentElement.find('.dsh-res-hover');
				if (selectedElement && selectedElement.length > 0 && selectedElement.prev('.dsh-res-display') && selectedElement.prev('.dsh-res-display').length > 0) {
					selectedElement.removeClass('dsh-res-hover');
					selectedElement.prev('.dsh-res-display').addClass('dsh-res-hover');

					var updatedSelectedElement = parentElement.find('.dsh-res-hover');
					// check if the top of current selected element is over the parents top
					if ((updatedSelectedElement.offset().top - parentElement.offset().top) < 0) {
						var scrollTopPos = parentElement[0].scrollTop - updatedSelectedElement[0].clientHeight;
						parentElement[0].scrollTop = scrollTopPos;
					}
				} else {
					$(this).next().children('.dsh-res-display').removeClass('dsh-res-hover');
					$(this).next().children('.dsh-res-display').last('.dsh-res-display').addClass('dsh-res-hover');
					parentElement[0].scrollTop = parentElement[0].scrollHeight;
				}
			}
		}

		// Detect enter key
		else if (e.which == 13) {
			if ($(this).next().is(':visible')) {
				var selectedElement = $(this).next().find('.dsh-res-hover');
				if (selectedElement && selectedElement.length > 0) {
					selectedElement.click();
				}
			}
		}
	});

	$('#' + elementId).on('blur', function(e) {
		if ($(this).next().is(':visible')) {
			var selectedElement = $(this).next().find('.dsh-res-hover');
			if (selectedElement && selectedElement.length > 0) {
				selectedElement.click();
			} else {
				$(this).next().children('.dsh-res-display').first().click();
			}
		}
	});
}
function bindAutosuggestForCompanySearch(elementId) {
	// Bind keyup on search for company for dashboard
	$('#' + elementId).on('keyup', function(e) {
		var value = $(this).val();
		var prevVal = $(this).attr('data-prev-val');

		if (value != prevVal) {
			$(this).attr('data-prev-val', value);
			searchCompany(value, $(this).attr('data-search-target'));
		}
		// Detect arrow key down
		else if (e.which == 40) {
			if ($(this).next().is(':visible')) {
				var parentElement = $(this).next();
				var selectedElement = parentElement.find('.dsh-res-hover');
				if (selectedElement && selectedElement.length > 0 && selectedElement.next('.dsh-res-display') && selectedElement.next('.dsh-res-display').length > 0) {
					selectedElement.removeClass('dsh-res-hover');
					selectedElement.next('.dsh-res-display').addClass('dsh-res-hover');

					var updatedSelectedElement = parentElement.find('.dsh-res-hover');
					// check if the top of current selected element is over the parents top
					if ((updatedSelectedElement.offset().top - parentElement.offset().top + updatedSelectedElement[0].clientHeight) > parentElement[0].clientHeight) {
						var scrollTopPos = parentElement[0].scrollTop + updatedSelectedElement[0].clientHeight;
						parentElement[0].scrollTop = scrollTopPos;
					}
				} else {
					$(this).next().children('.dsh-res-display').removeClass('dsh-res-hover');
					$(this).next().children('.dsh-res-display').first('.dsh-res-display').addClass('dsh-res-hover');
					parentElement[0].scrollTop = 0;
				}
			}
		}
		// Detect arrow key up
		else if (e.which == 38) {
			if ($(this).next().is(':visible')) {
				var parentElement = $(this).next();
				var selectedElement = parentElement.find('.dsh-res-hover');
				if (selectedElement && selectedElement.length > 0 && selectedElement.prev('.dsh-res-display') && selectedElement.prev('.dsh-res-display').length > 0) {
					selectedElement.removeClass('dsh-res-hover');
					selectedElement.prev('.dsh-res-display').addClass('dsh-res-hover');

					var updatedSelectedElement = parentElement.find('.dsh-res-hover');
					// check if the top of current selected element is over the parents top
					if ((updatedSelectedElement.offset().top - parentElement.offset().top) < 0) {
						var scrollTopPos = parentElement[0].scrollTop - updatedSelectedElement[0].clientHeight;
						parentElement[0].scrollTop = scrollTopPos;
					}
				} else {
					$(this).next().children('.dsh-res-display').removeClass('dsh-res-hover');
					$(this).next().children('.dsh-res-display').last('.dsh-res-display').addClass('dsh-res-hover');
					parentElement[0].scrollTop = parentElement[0].scrollHeight;
				}
			}
		}

		// Detect enter key
		else if (e.which == 13) {
			if ($(this).next().is(':visible')) {
				var selectedElement = $(this).next().find('.dsh-res-hover');
				if (selectedElement && selectedElement.length > 0) {
					selectedElement.click();
				}
			}
		}
	});

	$('#' + elementId).on('blur', function(e) {
		if ($(this).next().is(':visible')) {
			var selectedElement = $(this).next().find('.dsh-res-hover');
			if (selectedElement && selectedElement.length > 0) {
				selectedElement.click();
			} else {
				$(this).next().children('.dsh-res-display').first().click();
			}
		}
	});
}

function showCompanyAdminFlow(newProfileName, newProfileValue) {

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();
	// get profile data for all the records , noOfDays = -1
	showProfileDetails(newProfileName, newProfileValue, -1);
	bindSelectButtons(newProfileValue);
	if ((accountType != "INDIVIDUAL") && (accountType != "FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);
}

function showRegionAdminFlow(newProfileName, newProfileValue) {

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();
	// get profile data for all the records , noOfDays = -1
	showProfileDetails(newProfileName, newProfileValue, -1);
	bindSelectButtons(newProfileValue);
	if ((accountType != "INDIVIDUAL") && (accountType != "FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);
}

function showBranchAdminFlow(newProfileName, newProfileValue) {

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();
	// get profile data for all the records , noOfDays = -1
	showProfileDetails(newProfileName, newProfileValue, -1);
	bindSelectButtons(newProfileValue);
	if ((accountType != "INDIVIDUAL") && (accountType != "FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);
}

function showAgentFlow(newProfileName, newProfileValue) {

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").hide();
	$("#dsh-grph-srch-survey-div").hide();
	// get profile data for all the records , noOfDays = -1
	showProfileDetails(newProfileName, newProfileValue, -1);
	bindSelectButtons(newProfileValue);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);
}

function showProfileDetails(columnName, columnValue, numberOfDays) {
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"numberOfDays" : numberOfDays
	};
	showDashOverlay('#top-dash');
	callAjaxGetWithPayloadData("./profiledetails.do", function(data) {
		$('#dash-profile-detail-circles').html(data);
		showDashboardButtons(columnName, columnValue);
		showDisplayPic();
		updateDashboardProfileEvents();
	}, payload, true);
}

function updateDashboardProfileEvents() {
	// Social Posts
	$('#dg-img-3').find('svg').remove();
	var socialPosts = $('#socl-post').text();
	var circle1 = new ProgressBar.Circle('#dg-img-3', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	if ((parseFloat(socialPosts) / maxSocialPosts) > 1)
		circle1.animate(1);
	else
		circle1.animate(parseFloat(socialPosts) / maxSocialPosts);
	// Survey Count
	$('#dg-img-2').find('svg').remove();
	var surveyCount = $("#srv-snt-cnt").text();
	var circle2 = new ProgressBar.Circle('#dg-img-2', {
		color : '#E97F30',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	if ((parseInt(surveyCount) / maxSurveySent) > 1)
		circle2.animate(1);
	else
		circle2.animate(parseInt(surveyCount) / maxSurveySent);
	// Social Score
	$('#dg-img-1').find('svg').remove();
	var socialScore = $("#srv-scr").text();
	var circle3 = new ProgressBar.Circle('#dg-img-1', {
		color : '#5CC7EF',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	if ((parseFloat(socialScore) / 5) > 1)
		circle3.animate(1);
	else
		circle3.animate(parseFloat(socialScore) / 5);
	// Profile completion
	$('#dg-img-4').find('svg').remove();
	var circle4 = new ProgressBar.Circle('#dg-img-4', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	var profileCompleted = parseInt($('#pro-cmplt-stars').attr("data-profilecompleteness"));
	if ((profileCompleted / 100) > 1)
		circle4.animate(1);
	else
		circle4.animate(profileCompleted / 100);

	// update dashboard button events
	$('#pro-cmplt-stars').on('click', '#dsh-btn1', function(e) {
		e.stopPropagation();
		if (colName == 'agentId') {
			sendSurveyInvitation('#dsh-btn1');
		} else if (accountType == "INDIVIDUAL") {
			sendSurveyInvitation('#dsh-btn1');
		} else {
			sendSurveyInvitationAdmin(colName, colValue, '#dsh-btn1');
		}
	});
	$('#pro-cmplt-stars').on('click', '#dsh-btn2', function(e) {
		e.stopPropagation();
		var buttonId = 'dsh-btn2';
		var task = $('#dsh-btn2').data('social');
		dashboardButtonAction(buttonId, task, colName, colValue);
	});
	$('#pro-cmplt-stars').on('click', '#dsh-btn3', function(e) {
		e.stopPropagation();
		var buttonId = 'dsh-btn3';
		var task = $('#dsh-btn3').data('social');
		dashboardButtonAction(buttonId, task, colName, colValue);
	});
	
	
	
	$('#pro-cmplt-stars').on('click', '#dsh-btn0', function(e) {
		e.stopPropagation();
		var buttonId = 'dsh-btn0';
		// getSocialMediaToFix
		var payload = {
				"columnName" : colName,
				"columnValue" : colValue
			};
			callAjaxGetWithPayloadData('./socialmediatofix.do', paintFixSocialMedia, payload, true);
	});
}

function paintFixSocialMedia(data){
	
	var popup = "";
	var parsedData = JSON.parse(data);
	var columnName = parsedData.columnName;
	var columnValue = parsedData.columnValue;
	var socialMedias = parsedData.socialMedias;
	
	for (var i = 0; i < socialMedias.length; i++){
		var socialMedia = socialMedias[i];
		if(socialMedia == "facebook"){
			var facebookDiv = '<div class="clearfix display-inline-block"><div class="float-left soc-nw-icns cursor-pointer icn-wide-fb soc-nw-adj " onclick="openAuthPageFixSocialMedia('+ "'facebook'" +', '+ "'" + columnName + "'" +', '+columnValue+',true'+');"></div></div>';
			popup += facebookDiv;
		}else if(socialMedia == "linkedin"){
			var linkedinDiv = '<div class="clearfix display-inline-block"><div class="float-left soc-nw-icns cursor-pointer icn-wide-linkedin soc-nw-adj " onclick="openAuthPageFixSocialMedia(' + "'linkedin'" + ',' + "'" + columnName + "'" + ', '+columnValue+',true'+');" data-link=""></div></div>';
			popup += linkedinDiv;
		}
	}
	
	if(socialMedias.length == 0){
		var noSMDiv = '<div class="clearfix"><div></div class="float-left bd-frm-left-un">Successfully connected!</div>';
		popup += noSMDiv;
		$('#dsh-btn0').addClass("hide");
		if( window.location.hash.substr(1) == "showreportingpage" ){
			$('#rep-fix-social-media').fadeOut(500);
			delay(function(){
				drawReportingDashButtons(columnName, columnValue);
			},500);	
		}
	}
	
// e.stopPropagation();
	$('#overlay-continue').html("");
	$('#overlay-cancel').html("");
	$('#overlay-header').html("Reconnect Social Media");
	$('#overlay-text').html(popup);

	$('#overlay-main').show();
}

function bindSelectButtonsForTranStats(){
	
	$("#selection-list-transaction").unbind('change');
	$("#selection-list-proc-survey").unbind('change');
	$("#selection-list-actv-usr").unbind('change');
	
	
	$("#transaction-count-days").unbind('change');
	$("#proc-sur-count-days").unbind('change');
	$("#actv-usr-count-days").unbind('change');
	
	
	$("#selection-list-transaction").change(function() {
		$('#trans-sel-item').val('');
		$('.dsh-res-display').hide();

		if ($("#selection-list-transaction").val() == 'companyId') {
			$('#dsh-srch-survey-div').hide();
			showTransactionStatisticsGraphically('companyId', newProfileValue);
		} else if ($("#selection-list-transaction").val() == 'regionId') {
			$('#dsh-srch-survey-div').hide();
			showTransactionStatisticsGraphically('regionId', newProfileValue);
		} else if ($("#selection-list-transaction").val() == 'branchId') {
			$('#dsh-srch-survey-div').hide();
			showTransactionStatisticsGraphically('branchId', newProfileValue);
		} else {
			$('#dsh-srch-survey-div').show();
		}
	});
	
	$("#selection-list-proc-survey").change(function() {
		$('#proc-sur-sel-item').val('');
		$('.dsh-res-display').hide();

		if ($("#selection-list-proc-survey").val() == 'companyId') {
			$('#proc-srch-survey-div').hide();
			showProsSurveyStatisticsGraphically('companyId', newProfileValue);
		} else if ($("#selection-list-proc-survey").val() == 'regionId') {
			$('#proc-srch-survey-div').hide();
			showProsSurveyStatisticsGraphically('regionId', newProfileValue);
		} else if ($("#selection-list-proc-survey").val() == 'branchId') {
			$('#proc-srch-survey-div').hide();
			showProsSurveyStatisticsGraphically('branchId', newProfileValue);
		} else {
			$('#proc-srch-survey-div').show();
		}
	});
	
	$("#selection-list-actv-usr").change(function() {
		$('#actv-usr-srch-div').val('');
		$('.dsh-res-display').hide();

		if ($("#selection-list-actv-usr").val() == 'companyId') {
			$('#actv-usr-srch-div').hide();
			showActiveUsersStatisticsGraphically('companyId', newProfileValue);
		} else if ($("#selection-list-actv-usr").val() == 'regionId') {
			$('#actv-usr-srch-div').hide();
			showActiveUsersStatisticsGraphically('regionId', newProfileValue);
		} else if ($("#selection-list-actv-usr").val() == 'branchId') {
			$('#actv-usr-srch-div').hide();
			showActiveUsersStatisticsGraphically('branchId', newProfileValue);
		} else {
			$('#actv-usr-srch-div').show();
		}
	});
	
	$("#transaction-count-days").change(function() {
		var columnName = lastColNameForGraphTrans;
		var columnValue = lastColValueForGraphTrans;
		if ($('#trans-srch-survey-div').is(':visible')) {
			if ($('#trans-sel-item').val() == '') {
				$('#trans-sel-item').addClass("empty-field");
				if ($('#selection-list-transaction').val() == "regionName") {
					$('#overlay-toast').html("Please choose a valid Region Name");
				} else if ($('#selection-list-transaction').val() == "branchName") {
					$('#overlay-toast').html("Please choose a valid Office Name");
				} else if ($('#selection-list-transaction').val() == "displayName") {
					$('#overlay-toast').html("Please choose a valid User Name");
				} else if ($('#selection-list-transaction').val() == "company") {
					$('#overlay-toast').html("Please choose a valid Company Name");
				}
				
				showToast();
				return;
			}
		}
		showTransactionStatisticsGraphically(columnName, columnValue);
	});
	
	$("#proc-sur-count-days").change(function() {
		var columnName = lastColNameForGraphProcSurvey;
		var columnValue = lastColValueForGraphProcSurvey;
		if ($('#proc-srch-survey-div').is(':visible')) {
			if ($('#proc-sur-sel-item').val() == '') {
				$('#proc-sur-sel-item').addClass("empty-field");
				if ($('#selection-list-proc-survey').val() == "regionName") {
					$('#overlay-toast').html("Please choose a valid Region Name");
				} else if ($('#selection-list-proc-survey').val() == "branchName") {
					$('#overlay-toast').html("Please choose a valid Office Name");
				} else if ($('#selection-list-proc-survey').val() == "displayName") {
					$('#overlay-toast').html("Please choose a valid User Name");
				} else if ($('#selection-list-proc-survey').val() == "company") {
					$('#overlay-toast').html("Please choose a valid Company Name");
				}
				showToast();
				return;
			}
		}
		showProsSurveyStatisticsGraphically(columnName, columnValue);
	});
	
	$("#actv-usr-count-days").change(function() {
		var columnName = lastColNameForGraphActvUser;
		var columnValue = lastColValueForGraphActvUser;
		if ($('#actv-usr-srch-div').is(':visible')) {
			if ($('#actv-usr-sel-item').val() == '') {
				$('#actv-usr-sel-item').addClass("empty-field");
				if ($('#selection-list-actv-usr').val() == "regionName") {
					$('#overlay-toast').html("Please choose a valid Region Name");
				} else if ($('#selection-list-actv-usr').val() == "branchName") {
					$('#overlay-toast').html("Please choose a valid Office Name");
				} else if ($('#selection-list-actv-usr').val() == "displayName") {
					$('#overlay-toast').html("Please choose a valid User Name");
				} else if ($('#selection-list-actv-usr').val() == "company") {
					$('#overlay-toast').html("Please choose a valid Company Name");
				}
				showToast();
				return;
			}
		}
		showActiveUsersStatisticsGraphically(columnName, columnValue);
	});	
}

function bindSelectButtons(newProfileValue) {
	$("#selection-list").unbind('change');
	$("#graph-sel-list").unbind('change');
	$("#dsh-grph-format").unbind('change');
	$("#survey-count-days").unbind('change');

	$("#selection-list").change(function() {
		$('#dsh-sel-item').val('');
		$('.dsh-res-display').hide();

		if ($("#selection-list").val() == 'companyId') {
			$('#dsh-srch-survey-div').hide();
			showSurveyStatistics('companyId', newProfileValue);
		} else if ($("#selection-list").val() == 'regionId') {
			$('#dsh-srch-survey-div').hide();
			showSurveyStatistics('regionId', newProfileValue);
		} else if ($("#selection-list").val() == 'branchId') {
			$('#dsh-srch-survey-div').hide();
			showSurveyStatistics('branchId', newProfileValue);
		} else {
			$('#dsh-srch-survey-div').show();
		}
	});
	$("#graph-sel-list").change(function() {
		$('#dsh-grph-sel-item').val('');
		$('.dsh-res-display').hide();

		if ($("#graph-sel-list").val() == 'companyId') {
			$('#dsh-grph-srch-survey-div').hide();
			showSurveyStatisticsGraphically('companyId', newProfileValue);
		} else if ($("#graph-sel-list").val() == 'regionId') {
			$('#dsh-grph-srch-survey-div').hide();
			showSurveyStatisticsGraphically('regionId', newProfileValue);
		} else if ($("#graph-sel-list").val() == 'branchId') {
			$('#dsh-grph-srch-survey-div').hide();
			showSurveyStatisticsGraphically('branchId', newProfileValue);
		} else {
			$('#dsh-grph-srch-survey-div').show();
		}
	});

	$("#dsh-grph-format").change(function() {
		var columnName = colName;
		var columnValue = colValue;
		if ($('#dsh-grph-srch-survey-div').is(':visible')) {
			if ($('#dsh-grph-sel-item').val() == '') {
				$('#dsh-grph-sel-item').addClass("empty-field");
				if ($('#graph-sel-list').val() == "regionName") {
					$('#overlay-toast').html("Please choose a valid Region Name");
				} else if ($('#graph-sel-list').val() == "branchName") {
					$('#overlay-toast').html("Please choose a valid Office Name");
				}
				showToast();
				return;
			}

			columnName = lastColNameForGraph;
			columnValue = lastColValueForGraph;
		}
		showSurveyStatisticsGraphically(columnName, columnValue);
	});
	$("#survey-count-days").change(function() {
		var columnName = colName;
		var columnValue = colValue;

		if ($('#dsh-srch-survey-div').is(':visible')) {
			if ($('#dsh-sel-item').val() == '') {
				$('#dsh-sel-item').addClass("empty-field");
				if ($('#selection-list').val() == "regionName") {
					$('#overlay-toast').html("Please choose a valid Region Name");
				} else if ($('#selection-list').val() == "branchName") {
					$('#overlay-toast').html("Please choose a valid Office Name");
				}
				showToast();
				return;
			}

			columnName = lastColNameForCount;
			columnValue = lastColValueForCount;

		}
		showSurveyStatistics(columnName, columnValue);
	});
}

function populateSurveyStatisticsList(columnName) {
	$("#region-div").show();
	$("#graph-sel-div").show();

	var options = "";
	if ((columnName == "companyId") && (accountType == "ENTERPRISE" || accountType == "COMPANY")) {
		options += "<option value=companyId>Company</option>";
	}
	if ((columnName == "companyId") && (accountType == "ENTERPRISE")) {
		options += "<option value=regionName>Region</option>";
	} else if ((columnName == "regionId") && (accountType == "ENTERPRISE")) {
		options += "<option value=regionId>Region</option>";
	}
	if (accountType == "ENTERPRISE" || accountType == "COMPANY") {
		if (columnName == "companyId" || columnName == "regionId") {
			options += "<option value=branchName>Office</option>";
		} else if (columnName == "branchId") {
			options += "<option value=branchId>Office</option>";
		}
	}
	if (columnName == "companyId" || columnName == "regionId" || columnName == "branchId") {
		options += "<option value=displayName>Individual</option>";
	}

	$("#selection-list").html(options);
	$("#graph-sel-list").html(options);

	if (columnName == "companyId") {
		$('#dsh-srch-survey-div').hide();
		$('#dsh-grph-srch-survey-div').hide();
	} else if (columnName == "regionId" || columnName == "branchId") {
		$('#dsh-srch-survey-div').hide();
		$('#dsh-grph-srch-survey-div').hide();
	}
}

function showSurveyStatistics(columnName, columnValue) {
	var element = document.getElementById("survey-count-days");
	var numberOfDays = element.options[element.selectedIndex].value;
	showSurveyCount(columnName, columnValue, numberOfDays);
}

function showSurveyCount(columnName, columnValue, numberOfDays) {
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"numberOfDays" : numberOfDays
	};
	showDashOverlay('#mid-dash');
	callAjaxGetWithPayloadData("./surveycount.do", function(data) {
		$('#dsh-sel-item').removeClass("empty-field");
		$('#dash-survey-status').html(data);
	}, payload, true);
}

var isIncompleteSurveyAjaxRequestRunning = false;
var doStopIncompleteSurveyPostAjaxRequest = false;

function fetchIncompleteSurvey(isNextBatch) {

	if (!isNextBatch && $('#dsh-inc-srvey>div.dsh-icn-sur-item.hide').length > 0) {
		showLoaderOnPagination($('#dsh-inc-srvey'));

		// paint the posts
		setTimeout(function() {
			displayIncompleteSurveysOnDashboard();
		}, 500);
		return;
	}

	if (isIncompleteSurveyAjaxRequestRunning)
		return; // Return if request is running

	var payload = {
		"columnName" : colName,
		"columnValue" : colValue,
		"startIndex" : startIndexInc,
		"batchSize" : batchSizeInc
	};

	var totalIncReviews = parseInt($('#dsh-inc-srvey').attr("data-total"));
	if (totalIncReviews == 0) {
		$("#incomplete-survey-header").html("No incomplete surveys found");
		return;
	}

	// Show loader icon if not next batch
	if (!isNextBatch) {
		showLoaderOnPagination($('#dsh-inc-srvey'));
	}

	isIncompleteSurveyAjaxRequestRunning = true;
	callAjaxGetWithPayloadData("./fetchdashboardincompletesurvey.do", function(data) {

		isIncompleteSurveyAjaxRequestRunning = false;
		startIndexInc += batchSizeInc;

		var tempDiv = $("<div>");
		tempDiv.html(data);

		if (tempDiv.children('div.dsh-icn-sur-item').length < batchSizeInc) {
			doStopIncompleteSurveyPostAjaxRequest = true;
		}

		if (startIndexInc == 0) {
			$('#dsh-inc-srvey').html(data);
			$("#dsh-inc-dwnld").show();
		} else {
			$('#dsh-inc-srvey').append(data);
		}

		$('.dsh-inc-sur-date[data-modified="false"]').each(function(index, currentElement) {
			var dateStr = $(this).attr('data-value');
			$(this).html(getDateStrToUTC(dateStr)).attr("data-modified", "true");
		});

		if (isNextBatch) {
			// Fetch the next batch
			if (!doStopIncompleteSurveyPostAjaxRequest && $('#dsh-inc-srvey>div.dsh-icn-sur-item.hide').length <= batchSizeInc) {
				fetchIncompleteSurvey(true);
			}
		} else if ($('#dsh-inc-srvey>div.dsh-icn-sur-item.hide').length > 0) {
			fetchIncompleteSurvey(false);
		}
	}, payload, true);

}

function displayIncompleteSurveysOnDashboard() {
	hideLoaderOnPagination($('#dsh-inc-srvey'));
	$('#dsh-inc-srvey>div.dsh-icn-sur-item.hide').each(function(index, currentElement) {
		if (index >= batchSizeInc) {
			return false;
		}
		$(this).removeClass("hide");
	});
	$('#dsh-inc-srvey').perfectScrollbar();

	// Fetch the next batch
	if (!doStopIncompleteSurveyPostAjaxRequest && $('#dsh-inc-srvey>div.dsh-icn-sur-item.hide').length <= batchSizeInc) {
		fetchIncompleteSurvey(true);
	}
}

$(document).on('click', '.dash-lp-rt-img', function() {
	var surveyPreInitiationId = $(this).data("surveypreinitiationid");
	var customerName = $(this).data("custname");
	sendSurveyReminderMail(surveyPreInitiationId, customerName, '#dsh-inc-srvey');
});

var isDashboardReviewRequestRunning = false;
var doStopPaginationDashboard = false;

var isAjaxInProgress = false;
function fetchReviewsOnDashboard(isNextBatch) {
	if (isAjaxInProgress == true) {
		return;
	}
	if (isDashboardReviewRequestRunning)
		return; // Return if ajax request is still running

	var payload = {
		"columnName" : colName,
		"columnValue" : colValue,
		"startIndex" : startIndexCmp,
		"batchSize" : batchSizeCmp
	};

	isDashboardReviewRequestRunning = true;
	if (!isNextBatch) {
		showLoaderOnPagination($('#review-details'));
	}
	isAjaxInProgress = true;
	callAjaxGetWithPayloadData("./fetchdashboardreviews.do", function(data) {
		isAjaxInProgress = false;
		var tempDiv = $('<div>').html(data);
		var reviewsCount = tempDiv.children('div.dsh-review-cont').length;
		var ssReviewsPresent = true;
		// check if no reviews found
		if (startIndexCmp == 0) {
			var name = $('#review-desc').attr('data-profile-name');
			if (reviewsCount == 0) {
				$("#review-desc").html("No reviews found for " + name);
				$("#review-details").html('');
				// return;
				ssReviewsPresent = false;
			} else {
				$("#review-desc").html("What people say about " + name);
			}
		}

		if (reviewsCount < batchSizeCmp) {
			doStopPaginationDashboard = true;
		}

		if (ssReviewsPresent) {
			if (startIndexCmp == 0)
				$('#review-details').html(data);
			else
				$('#review-details').append(data);

			// Update events
			updateEventOnDashboardPageForReviews();
		}
		startIndexCmp += batchSizeCmp;

		if (!isNextBatch) {
			displayReviewOnDashboard();
		}
		isDashboardReviewRequestRunning = false;
		
		if($('#review-details').length > 0 ){
			if ($('div.dsh-review-cont.hide').length <= batchSizeCmp && !doStopPaginationDashboard) {
				fetchReviewsOnDashboard(true);
			} else if ($('div.dsh-review-cont.hide').length < (2 * batchSizeCmp)) {
				fetchZillowReviewsBasedOnProfile(colName, colValue, isZillowReviewsCallRunning, true, startIndexCmp, batchSizeCmp, name);
			}
		}
		
	}, payload, true);
}

var isDashboardReviewScrollRunning = false;

function dashbaordReviewScroll() {
	if ((window.innerHeight + window.pageYOffset) >= ($('#review-details').offset().top + $('#review-details').height() - 200) && (!doStopPaginationDashboard || $('div.dsh-review-cont.hide').length > 0)) {
		if (isDashboardReviewScrollRunning)
			return; // return if the scroll is running
		if ($('div.dsh-review-cont.hide').length > 0) {
			showLoaderOnPagination($('#review-details'));
			isDashboardReviewScrollRunning = true;
			setTimeout(displayReviewOnDashboard, 500);
		} else {
			fetchReviewsOnDashboard(false);
		}
	}
}

function displayReviewOnDashboard() {

	isDashboardReviewScrollRunning = false;
	$('.dsh-review-cont').removeClass("ppl-review-item-last").addClass("ppl-review-item");

	hideLoaderOnPagination($('#review-details'));
	var nextBatchReviews = $('div.dsh-review-cont.hide').length;
	$('div.dsh-review-cont.hide').each(function(index, currentElement) {
		$(this).removeClass("hide");
		if (index >= batchSizeCmp - 1 || index >= nextBatchReviews - 1) {
			$(this).addClass("ppl-review-item-last").removeClass("ppl-review-item");
			return false;
		}
	});

	// Get the next batch
	if ($('div.dsh-review-cont.hide').length <= batchSizeCmp && !doStopPaginationDashboard) {
		fetchReviewsOnDashboard(true);
	}
}

function updateEventOnDashboardPageForReviews() {
	$('.ppl-head-2[data-modified="false"]').each(function(index, currentElement) {
		var dateSplit = $(this).attr('data-modifiedon').split('-');
		var month = dateSplit[0];
		var day = dateSplit[1];
		var year = dateSplit[2];
		var reviewDay = month + " " + day + ", " + year;
		$(this).html(reviewDay).attr("data-modified", "true");
	});
	$('.completedOn[data-modified="false"]').each(function(index, currentElement) {
		var dateSplit = $(this).attr('data-modifiedon').split('-');
		var month = dateSplit[0];
		var year = dateSplit[1];
		var reviewDay = month + " " + year;
		$(this).html(reviewDay).attr("data-modified", "true");
	});

	$('.review-ratings[data-modified="false"]').each(function() {
		changeRatingPattern($(this).data("rating"), $(this), false, $(this).data("source"));
		$(this).attr("data-modified", "true");
	});

	$('.ppl-share-icns').unbind('click');
	$('.ppl-share-icns').bind('click', function() {
		var link = $(this).attr('data-link');
		var title = $(this).attr('title');
		var surveyMongoId  = $(this).closest('.ppl-review-item').attr('survey-mongo-id');
		if (surveyMongoId == null){
			surveyMongoId  = $(this).closest('.ppl-review-item-last').attr('survey-mongo-id');
		}
		var entityId = $('#rep-prof-container').data('column-value');
		var entityType = $('#rep-prof-container').data('column-name');
		
		
		var payload = {
			"surveyMongoId" :surveyMongoId,
			"entityId" : entityId,
			"entityType" : entityType
		};
		
		if(title == 'LinkedIn'){
			$.ajax({
				url : "./postonlinkedin.do",
				type : "POST",
				data : payload,
				success : function(data) {
					var response = JSON.parse(data);
					linkedInShare(response,link,title);
				},
				error : function(e) {
					if (e.status == 504) {
						redirectToLoginPageOnSessionTimeOut(e.status);
						return;
					}
					$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
				}
			});
		}else{
			if (link == undefined || link == "") {
				return false;
			}
			window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
		}		
	});
}

function linkedInShare(data,link,title){
	if(data == false || data == 'false'){
		if(title == 'LinkedIn'){
			
			var copyText = $(this).parent().find('.linkedInSummary').val();
			copyToClipboard(copyText);
			
			$('#overlay-header').html("");
			$('#overlay-text').html('<div style="text-align:left; display: grid;">The text of the post has been copied to clipboard. Please use the text to post in LinkedIn Page.</div>');
			$('#overlay-continue').html("Ok");
			$('#overlay-cancel').html("Cancel");
			
			$('#overlay-continue').off();
			$('#overlay-continue').click(function() {
				overlayRevert();
				if (link == undefined || link == "") {
					return false;
				}
				window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
			});
			
			$('#overlay-cancel').click(function() {
				$('#overlay-continue').unbind('click');
				$('#overlay-cancel').unbind('click');
				overlayRevert();

			});
			$('#overlay-main').show();
			
		}else{
			if (link == undefined || link == "") {
				return false;
			}
			window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
		}
	}else if(data == true || data == 'true'){
		$('#overlay-toast').html('Successfully posted to LinkedIn.');
		showToast();
	}
}

function showSurveyStatisticsGraphically(columnName, columnValue) {
	var element = document.getElementById("dsh-grph-format");
	var numberOfDays = element.options[element.selectedIndex].value;
	showDashOverlay('#low-dash');
	showSurveyGraph(columnName, columnValue, numberOfDays);
}
var isSurveydetailsforgraph = false;
function showSurveyGraph(columnName, columnValue, numberOfDays) {
	if (isSurveydetailsforgraph == true) {
		return;
	}
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"numberOfDays" : numberOfDays
	};
	isSurveydetailsforgraph = true;
	$.ajax({
		url : "./surveydetailsforgraph.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			isSurveydetailsforgraph = false;
			$('#dsh-grph-sel-item').removeClass("empty-field");
			graphData = data;
			paintSurveyGraph();
			hideDashOverlay('#low-dash');
		},
		error : function(e) {
			isSurveydetailsforgraph = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function showActiveUsersStatisticsGraphically(columnName, columnValue) {
	var element = document.getElementById("proc-sur-count-days");
	var numberOfDays = element.options[element.selectedIndex].value;
	showDashOverlay('#low-actv-usr');
	$('#actv-user-gph-item').html('');
	showActvUserGraph(columnName, columnValue, numberOfDays);
}

function showActvUserGraph(columnName,companyId, numberOfDays) {
	if (isSurveydetailsforgraph == true) {
		return;
	}
	var payload = {
		"companyId" : companyId,
		"noOfDays" : numberOfDays
	};
	isSurveydetailsforgraph = true;
	$.ajax({
		url : "./getcompanyactiveusercountforpastndays.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			isSurveydetailsforgraph = false;
			$('#actv-usr-sel-item').removeClass("empty-field");
			graphData = data;
			paintActvUserGraph();
			hideDashOverlay('#low-actv-usr');
		},
		error : function(e) {
			isSurveydetailsforgraph = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}


function paintActvUserGraph() {
	if (graphData == undefined)
		return;
	console.log()
	var allTimeslots = [];
	var totalActiveUserCount = [];

	var element = document.getElementById("proc-sur-count-days");
	if (element == null) {
		return;
	}

	var format = element.options[element.selectedIndex].value;
	var type = '';
	if (format == '10') {
		type = 'Date';
	} else if (format == '20') {
		type = 'Date';
	} else if (format == '30') {
		type = 'Date';
	}

	var keys = getKeysFromGraphFormat(format);

	for (var i = 0; i < keys.length; i++) {
		if (format == '365') {
			allTimeslots[i] = convertYearMonthKeyToDate(keys[i]);
		} else if (format == '10' || format == '20') {
			allTimeslots[i] = convertYearMonthDayKeyToDate(keys[i]);
		}else {
			allTimeslots[i] = convertYearWeekKeyToDate(keys[i]);
		}
		totalActiveUserCount[i] =  0;
	}
	
	if(graphData != undefined){
		for(var i in graphData ){
			var graphDataEntity = graphData[i];
			
			var entityDate = graphDataEntity.statsDate;
		    var formattedDate = new Date(Date.parse(entityDate));
		    //get date similar to keys formay
		    
		    var month = formattedDate.getMonth() + 1;
			var monthStr = "";
			if (month < 10) {
				monthStr = '0' + month.toString();
				
			}else{
				monthStr = month.toString();
			}
			
			var dayStr = "";
			var day  = formattedDate.getDate();
			if (day < 10) {
				dayStr = '0' + day.toString();
				
			}else{
				dayStr = day.toString();
			}
			
			var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
			if(keys.indexOf(keyFormattedDate)){
				var index = keys.indexOf(keyFormattedDate);
				totalActiveUserCount[index] =  graphDataEntity.noOfActiveUsers;
			}
		}
	}
	
	var internalData = [];
	var nestedInternalData = [];
	nestedInternalData.push(type, 'Total Active Users');
	internalData.push(nestedInternalData);
	for (var itr = 0; itr < allTimeslots.length; itr++) {
		nestedInternalData = [];
		var curTotalActiveUserCount;

		if (isNaN(parseInt(totalActiveUserCount[itr]))) {
			curTotalActiveUserCount = 0;
		} else {
			curTotalActiveUserCount = parseInt(totalActiveUserCount[itr]);
		}


		nestedInternalData.push(allTimeslots[itr], curTotalActiveUserCount);
		internalData.push(nestedInternalData);
	}

	var data = google.visualization.arrayToDataTable(internalData);
	var options = {
		chartArea : {
			width : '90%',
			height : '80%'
		},
		colors : [ 'rgb(0,174,239)'],
		legend : {
			position : 'none'
		}
	};

	removeAllPreviousGraphToolTip();

	var chart = new google.visualization.LineChart(document.getElementById('actv-user-gph-item'));
	chart.draw(data, options);
}

function showProsSurveyStatisticsGraphically(columnName, columnValue) {
	var element = document.getElementById("proc-sur-count-days");
	var numberOfDays = element.options[element.selectedIndex].value;
	showDashOverlay('#low-proc-sur');
	$('#pro-survey-gph-item').html('');
	showProcSurveyGraph(columnName, columnValue, numberOfDays);
}

var isGettingCompaniesForTransactionMonitor = false;

function getCompaniesForTransactionMonitor(){
	var companyDetails;
	
	if (isGettingCompaniesForTransactionMonitor == true) {
		return;
	}
	
	isGettingCompaniesForTransactionMonitor = true;
	
	$.ajax({
		url : "./getcompaniesfortransactionmonitor.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		success : function(data){
			
			isGettingCompaniesForTransactionMonitor = false;
			companyDetails = data;
		},
		complete: function(){
			showProcSurveyGraph("company", companyDetails[0].iden, 14, companyDetails,0);
			
		},
		error : function(e){
			isGettingCompaniesForTransactionMonitor = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}
function showProcSurveyGraph(columnName,companyId, numberOfDays, companyDetails,currentId) {
	
	if (isSurveydetailsforgraph == true) {
		return;
	}
	var payload = {
		"companyId" : companyId,
		"noOfDays" : numberOfDays
	};
	isSurveydetailsforgraph = true;
	$.ajax({
		url : "./getcompanysurveystatuscountforpastndays.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			
			isSurveydetailsforgraph = false;
			$('#proc-sur-sel-item').removeClass("empty-field");
			graphData = data;
			$('#proc-trans-header').html(companyDetails[currentId].contact_details.name);
			paintProcSurveyGraph();
			hideDashOverlay('#low-proc-sur');
		},
		complete: function(){
			
			setTimeout(function(){
				currentId++;
				if( currentId == companyDetails.length ) currentId = 0;
				
				showProcSurveyGraph("company", companyDetails[currentId].iden, 14, companyDetails, currentId);
				
			}, 30000);
		},
		error : function(e) {
			isSurveydetailsforgraph = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintProcSurveyGraph() {
	if (graphData == undefined)
		return;
	console.log()
	var allTimeslots = [];
	var totalReceivedTransactionsCount = [];
	var completedTransactionCount = [];
	var sentSurveyInvitationTransactionsCount = [];
	var sentSurveyReminderTransactionsCount = [];
	var unprocessedTransactionsCount = [];
	
	var element = document.getElementById("proc-sur-count-days");
	
	var format = 14;
	if (element != null) {
		format = element.options[element.selectedIndex].value;
	}
	
	var type = 'Date';

	var keys = getKeysFromGraphFormat(format);
	//remove today's date
	keys.pop();

	for (var i = 0; i < keys.length; i++) {
		allTimeslots[i] = convertYearMonthDayKeyToMonthDay(keys[i]);		
		totalReceivedTransactionsCount[i] =  0;
		completedTransactionCount[i] =  0;
		sentSurveyInvitationTransactionsCount[i] =  0;
		sentSurveyReminderTransactionsCount[i] =  0;
		unprocessedTransactionsCount[i]=0;
	}
	
	if(graphData != undefined){
		for(var i in graphData ){
			var graphDataEntity = graphData[i];
			
			var entityDate = graphDataEntity.transactionDate;
		    var formattedDate = new Date(Date.parse(entityDate));
		    //get date similar to keys formay
		    
		    var month = formattedDate.getMonth() + 1;
			var monthStr = "";
			if (month < 10) {
				monthStr = '0' + month.toString();
				
			}else{
				monthStr = month.toString();
			}
			
			var dayStr = "";
			var day  = formattedDate.getDate();
			if (day < 10) {
				dayStr = '0' + day.toString();
				
			}else{
				dayStr = day.toString();
			}
			
			var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
			if(keys.indexOf(keyFormattedDate) > -1){
				var index = keys.indexOf(keyFormattedDate);
				totalReceivedTransactionsCount[index] =  graphDataEntity.transactionReceivedCount;
				completedTransactionCount[index] =  graphDataEntity.surveycompletedCount;
				sentSurveyInvitationTransactionsCount[index] =  graphDataEntity.surveyInvitationSentCount;
				sentSurveyReminderTransactionsCount[index] =  graphDataEntity.surveyReminderSentCount;
				unprocessedTransactionsCount[index] = graphDataEntity.transactionReceivedCount - graphDataEntity.surveyInvitationSentCount;
			}
		}
	}
	
	var internalData = [];
	var nestedInternalData = [];
	nestedInternalData.push(type, 'Total', 'Unprocessed', 'Invitations', 'Reminders','Completed');
	internalData.push(nestedInternalData);
	for (var itr = 0; itr < allTimeslots.length; itr++) {
		nestedInternalData = [];
		var curTotalReceivedTransactionsCount;
		var curCompletedTransactionCount;
		var curSentSurveyInvitationTransactionsCount;
		var curSentSurveyReminderTransactionsCount;
		var curUnprocessedTransactionsCount;

		if (isNaN(parseInt(totalReceivedTransactionsCount[itr]))) {
			curTotalReceivedTransactionsCount = 0;
		} else {
			curTotalReceivedTransactionsCount = parseInt(totalReceivedTransactionsCount[itr]);
		}

		if (isNaN(parseInt(completedTransactionCount[itr]))) {
			curCompletedTransactionCount = 0;
		} else {
			curCompletedTransactionCount = parseInt(completedTransactionCount[itr]);
		}

		if (isNaN(parseInt(sentSurveyInvitationTransactionsCount[itr]))) {
			curSentSurveyInvitationTransactionsCount = 0;
		} else {
			curSentSurveyInvitationTransactionsCount = parseInt(sentSurveyInvitationTransactionsCount[itr]);
		}

		if (isNaN(parseInt(sentSurveyReminderTransactionsCount[itr]))) {
			curSentSurveyReminderTransactionsCount = 0;
		} else {
			curSentSurveyReminderTransactionsCount = parseInt(sentSurveyReminderTransactionsCount[itr]);
		}
		
		if (isNaN(parseInt(unprocessedTransactionsCount[itr]))) {
			curUnprocessedTransactionsCount = 0;
		} else {
			curUnprocessedTransactionsCount = parseInt(unprocessedTransactionsCount[itr]);
		}

		nestedInternalData.push(allTimeslots[itr], curTotalReceivedTransactionsCount, curUnprocessedTransactionsCount, curSentSurveyInvitationTransactionsCount, curSentSurveyReminderTransactionsCount, curCompletedTransactionCount);
		internalData.push(nestedInternalData);
	}

	var data = google.visualization.arrayToDataTable(internalData);
	var options = {
		chartArea : {
			width : '90%',
			height : '80%'
		},
		colors : [ 'rgb(0, 0, 0)','rgb(255, 0, 0)' , 'rgb(0, 135, 255)', 'rgb(169,169,169)','rgb(0, 255, 0)' ],
		legend : {
			position : 'none'
		},
		vAxis : { 
			baselineColor : 'rgb(238,238,238)',
			gridlines : { color : 'rgb(238,238,238)'},
			viewWindow: {
		        min: 0
		    }
		}
		
	};

	removeAllPreviousGraphToolTip();

	var chart = new google.visualization.LineChart(document.getElementById('pro-survey-gph-item'));
	chart.draw(data, options);
}

var isSurveyDetailForOverallGraph=false;
function showOverallSurveyGraph(columnName,companyId, numberOfDays) {
	
	if (isSurveyDetailForOverallGraph == true) {
		return;
	}
	var payload = {
		"companyId" : companyId,
		"noOfDays" : numberOfDays
	};
	isSurveyDetailForOverallGraph = true;
	$.ajax({
		url : "./getcompanysurveystatuscountforpastndays.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			
			isSurveyDetailForOverallGraph = false;
			$('#proc-sur-sel-item').removeClass("empty-field");
			graphData = data;
			paintOverallSurveyGraph();
			hideDashOverlay('#low-trans');
		},
		error : function(e) {
			isSurveyDetailForOverallGraph = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}
function paintOverallSurveyGraph() {
	if (graphData == undefined)
		return;
	console.log()
	var allTimeslots = [];
	var totalReceivedTransactionsCount = [];
	var completedTransactionCount = [];
	var sentSurveyInvitationTransactionsCount = [];
	var sentSurveyReminderTransactionsCount = [];
	var unprocessedTransactionsCount = [];

	var element = document.getElementById("proc-sur-count-days");
	
	var format = 14;
	if (element != null) {
		format = element.options[element.selectedIndex].value;
	}

	
	var type = 'Date';
	

	var keys = getKeysFromGraphFormat(format);
	//remove today's date
	keys.pop();
	
	for (var i = 0; i < keys.length; i++) {
		allTimeslots[i] = convertYearMonthDayKeyToMonthDay(keys[i]);
		totalReceivedTransactionsCount[i] =  0;
		completedTransactionCount[i] =  0;
		sentSurveyInvitationTransactionsCount[i] =  0;
		sentSurveyReminderTransactionsCount[i] =  0;
		unprocessedTransactionsCount[i] = 0;
	}
	
	if(graphData != undefined){
		for(var i in graphData ){
			var graphDataEntity = graphData[i];
			
			var entityDate = graphDataEntity.transactionDate;
		    var formattedDate = new Date(Date.parse(entityDate));
		    //get date similar to keys formay
		    
		    var month = formattedDate.getMonth() + 1;
			var monthStr = "";
			if (month < 10) {
				monthStr = '0' + month.toString();
				
			}else{
				monthStr = month.toString();
			}
			
			var dayStr = "";
			var day  = formattedDate.getDate();
			if (day < 10) {
				dayStr = '0' + day.toString();
				
			}else{
				dayStr = day.toString();
			}
			
			var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
			if(keys.indexOf(keyFormattedDate) > -1){
				var index = keys.indexOf(keyFormattedDate);
				totalReceivedTransactionsCount[index] =  graphDataEntity.transactionReceivedCount;
				completedTransactionCount[index] =  graphDataEntity.surveycompletedCount;
				sentSurveyInvitationTransactionsCount[index] =  graphDataEntity.surveyInvitationSentCount;
				sentSurveyReminderTransactionsCount[index] =  graphDataEntity.surveyReminderSentCount;
				unprocessedTransactionsCount[index] = graphDataEntity.transactionReceivedCount - graphDataEntity.surveyInvitationSentCount;
			}
		}
	}
	
	var internalData = [];
	var nestedInternalData = [];
	nestedInternalData.push(type, 'Total', 'Unprocessed', 'Invitations', 'Reminders', 'Completed');
	internalData.push(nestedInternalData);
	for (var itr = 0; itr < allTimeslots.length; itr++) {
		nestedInternalData = [];
		var curTotalReceivedTransactionsCount;
		var curCompletedTransactionCount;
		var curSentSurveyInvitationTransactionsCount;
		var curSentSurveyReminderTransactionsCount;
		var curUnprocessedTransactionsCount;

		if (isNaN(parseInt(totalReceivedTransactionsCount[itr]))) {
			curTotalReceivedTransactionsCount = 0;
		} else {
			curTotalReceivedTransactionsCount = parseInt(totalReceivedTransactionsCount[itr]);
		}

		if (isNaN(parseInt(completedTransactionCount[itr]))) {
			curCompletedTransactionCount = 0;
		} else {
			curCompletedTransactionCount = parseInt(completedTransactionCount[itr]);
		}

		if (isNaN(parseInt(sentSurveyInvitationTransactionsCount[itr]))) {
			curSentSurveyInvitationTransactionsCount = 0;
		} else {
			curSentSurveyInvitationTransactionsCount = parseInt(sentSurveyInvitationTransactionsCount[itr]);
		}

		if (isNaN(parseInt(sentSurveyReminderTransactionsCount[itr]))) {
			curSentSurveyReminderTransactionsCount = 0;
		} else {
			curSentSurveyReminderTransactionsCount = parseInt(sentSurveyReminderTransactionsCount[itr]);
		}
		
		if (isNaN(parseInt(unprocessedTransactionsCount[itr]))) {
			curUnprocessedTransactionsCount = 0;
		} else {
			curUnprocessedTransactionsCount = parseInt(unprocessedTransactionsCount[itr]);
		}

		nestedInternalData.push(allTimeslots[itr], curTotalReceivedTransactionsCount,curUnprocessedTransactionsCount , curSentSurveyInvitationTransactionsCount, curSentSurveyReminderTransactionsCount,curCompletedTransactionCount);
		internalData.push(nestedInternalData);
	}

	var data = google.visualization.arrayToDataTable(internalData);
	var options = {
		chartArea : {
			width : '90%',
			height : '80%'
		},
		colors : [ 'rgb(0, 0, 0)','rgb(255, 0, 0)', 'rgb(0, 135, 255)', 'rgb(169,169,169)', 'rgb(0, 255, 0)'],
		legend : {
			position : 'none'
		},
		vAxis : { 
			baselineColor : 'rgb(238,238,238)',
			gridlines : { color : 'rgb(238,238,238)'},
			viewWindow: {
		        min: 0
		    }
		}
	};

	removeAllPreviousGraphToolTip();

	var chart = new google.visualization.LineChart(document.getElementById('trans-gph-item'));
	chart.draw(data, options);
}

function showTransactionStatisticsGraphically(columnName, columnValue) {
	var element = document.getElementById("transaction-count-days");
	var numberOfDays = element.options[element.selectedIndex].value;
	showDashOverlay('#low-trans');
	$('#trans-gph-item').html('');
	showTransactionGraph(columnName, columnValue, numberOfDays);
}

var isSurveydetailsforgraph = false;
function showTransactionGraph(columnName,companyId, numberOfDays) {
	if (isSurveydetailsforgraph == true) {
		return;
	}
	var payload = {
		"companyId" : companyId,
		"noOfDays" : numberOfDays
	};
	isSurveydetailsforgraph = true;
	$.ajax({
		url : "./getcompanyinputtransactionsforpastndays.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			isSurveydetailsforgraph = false;
			$('#trans-sel-item').removeClass("empty-field");
			graphData = data;
			paintTransactionGraph();
			hideDashOverlay('#low-trans');
		},
		error : function(e) {
			isSurveydetailsforgraph = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintTransactionGraph() {
	if (graphData == undefined)
		return;
	console.log()
	var allTimeslots = [];
	var totalTransactionsCount = [];
	var apiTransactionsCount = [];
	var encompassTransactionsCount = [];
	var ftpTransactionsCount = [];

	var format = 10;
	var element = document.getElementById("transaction-count-days");
	if (element != null) {
		format = element.options[element.selectedIndex].value;
	}

	var type = '';
	if (format == '10') {
		type = 'Date';
	} else if (format == '20') {
		type = 'Date';
	} else if (format == '30') {
		type = 'Date';
	}

	var keys = getKeysFromGraphFormat(format);

	for (var i = 0; i < keys.length; i++) {
		if (format == '365') {
			allTimeslots[i] = convertYearMonthKeyToDate(keys[i]);
		} else if (format == '10' || format == '20' ) {
			allTimeslots[i] = convertYearMonthDayKeyToDate(keys[i]);
		}else {
			allTimeslots[i] = convertYearWeekKeyToDate(keys[i]);
		}
			totalTransactionsCount[i] =  0;
			apiTransactionsCount[i] =  0;
			encompassTransactionsCount[i] =  0;
			ftpTransactionsCount[i] =  0;
	}
	
	if(graphData != undefined){
		for(var i in graphData ){
			var graphDataEntity = graphData[i];
			
			var entityDate = graphDataEntity.transactionDate;
		    var formattedDate = new Date(Date.parse(entityDate));
		    //get date similar to keys formay
		    
		    var month = formattedDate.getMonth() + 1;
			var monthStr = "";
			if (month < 10) {
				monthStr = '0' + month.toString();
				
			}else{
				monthStr = month.toString();
			}
			
			var dayStr = "";
			var day  = formattedDate.getDate();
			if (day < 10) {
				dayStr = '0' + day.toString();
				
			}else{
				dayStr = day.toString();
			}
			
			var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
			if(keys.indexOf(keyFormattedDate)){
				var index = keys.indexOf(keyFormattedDate);
				totalTransactionsCount[index] =  graphDataEntity.totalTransactionsCount;
				apiTransactionsCount[index] =  graphDataEntity.apiTransactionsCount;
				encompassTransactionsCount[index] =  graphDataEntity.encompassTransactionsCount;
				ftpTransactionsCount[index] =  graphDataEntity.ftpTransactionsCount;
			}
		}
	}
	
	var internalData = [];
	var nestedInternalData = [];
	nestedInternalData.push(type, 'Total Transactions', 'API Transactions', 'Encompass Transactions', 'FTP Transactions');
	internalData.push(nestedInternalData);
	for (var itr = 0; itr < allTimeslots.length; itr++) {
		nestedInternalData = [];
		var curTotalTransactionsCount;
		var curApiTransactionsCount;
		var curEncompassTransactionsCount;
		var curFtpTransactionsCount;

		if (isNaN(parseInt(totalTransactionsCount[itr]))) {
			curTotalTransactionsCount = 0;
		} else {
			curTotalTransactionsCount = parseInt(totalTransactionsCount[itr]);
		}

		if (isNaN(parseInt(apiTransactionsCount[itr]))) {
			curApiTransactionsCount = 0;
		} else {
			curApiTransactionsCount = parseInt(apiTransactionsCount[itr]);
		}

		if (isNaN(parseInt(encompassTransactionsCount[itr]))) {
			curEncompassTransactionsCount = 0;
		} else {
			curEncompassTransactionsCount = parseInt(encompassTransactionsCount[itr]);
		}

		if (isNaN(parseInt(ftpTransactionsCount[itr]))) {
			curFtpTransactionsCount = 0;
		} else {
			curFtpTransactionsCount = parseInt(ftpTransactionsCount[itr]);
		}

		nestedInternalData.push(allTimeslots[itr], curTotalTransactionsCount, curApiTransactionsCount, curEncompassTransactionsCount, curFtpTransactionsCount);
		internalData.push(nestedInternalData);
	}

	var data = google.visualization.arrayToDataTable(internalData);
	var options = {
		chartArea : {
			width : '90%',
			height : '80%'
		},
		colors : [ 'rgb(28,242,0)', 'rgb(0,174,239)', 'rgb(255,242,0)', 'rgb(255,202,145)' ],
		legend : {
			position : 'none'
		}
	};

	removeAllPreviousGraphToolTip();

	var chart = new google.visualization.LineChart(document.getElementById('trans-gph-item'));
	chart.draw(data, options);
}

function paintSurveyGraph() {
	if (graphData == undefined)
		return;
	var allTimeslots = [];
	var clickedSurveys = [];
	var sentSurveys = [];
	var socialPosts = [];
	var completedSurveys = [];

	var element = document.getElementById("dsh-grph-format");
	if (element == null) {
		return;
	}

	var format = element.options[element.selectedIndex].value;
	var type = '';
	if (format == '30') {
		type = 'Week Starting';
	} else if (format == '60') {
		type = 'Week Starting';
	} else if (format == '90') {
		type = 'Week Starting';
	} else if (format == '365') {
		type = 'Month';
	}

	var keys = getKeysFromGraphFormat(format);

	for (var i = 0; i < keys.length; i++) {
		if (format == '365') {
			allTimeslots[i] = convertYearMonthKeyToDate(keys[i]);
		} else {
			allTimeslots[i] = convertYearWeekKeyToDate(keys[i]);
		}
		if (graphData != undefined) {
			if (graphData.clicked != undefined)
				clickedSurveys[i] = graphData.clicked[keys[i]] || 0;
			if (graphData.sent != undefined)
				sentSurveys[i] = graphData.sent[keys[i]] || 0;
			if (graphData.complete != undefined)
				completedSurveys[i] = graphData.complete[keys[i]] || 0;
			if (graphData.socialposts != undefined)
				socialPosts[i] = graphData.socialposts[keys[i]] || 0;
		}
	}
	var internalData = [];
	var nestedInternalData = [];
	nestedInternalData.push(type, 'No. of surveys sent', 'No. of surveys clicked', 'No. of surveys completed', 'No. of social posts');
	internalData.push(nestedInternalData);
	for (var itr = 0; itr < allTimeslots.length; itr++) {
		nestedInternalData = [];
		var sentSurvey;
		var clickedSurvey;
		var completedSurvey;
		var socialPost;

		if (isNaN(parseInt(sentSurveys[itr]))) {
			sentSurvey = 0;
		} else {
			sentSurvey = parseInt(sentSurveys[itr]);
		}

		if (isNaN(parseInt(clickedSurveys[itr]))) {
			clickedSurvey = 0;
		} else {
			clickedSurvey = parseInt(clickedSurveys[itr]);
		}

		if (isNaN(parseInt(completedSurveys[itr]))) {
			completedSurvey = 0;
		} else {
			completedSurvey = parseInt(completedSurveys[itr]);
		}

		if (isNaN(parseInt(socialPosts[itr]))) {
			socialPost = 0;
		} else {
			socialPost = parseInt(socialPosts[itr]);
		}

		nestedInternalData.push(allTimeslots[itr], sentSurvey, clickedSurvey, completedSurvey, socialPost);
		internalData.push(nestedInternalData);
	}

	var data = google.visualization.arrayToDataTable(internalData);
	var options = {
		chartArea : {
			width : '90%',
			height : '80%'
		},
		colors : [ 'rgb(28,242,0)', 'rgb(0,174,239)', 'rgb(255,242,0)', 'rgb(255,202,145)' ],
		legend : {
			position : 'none'
		}
	};

	removeAllPreviousGraphToolTip();

	var chart = new google.visualization.LineChart(document.getElementById('util-gph-item'));
	chart.draw(data, options);
}

// Function to remove all previous tool tips popped up from charts
function removeAllPreviousGraphToolTip() {
	$('.footer-main-wrapper').nextAll("div").filter(function() {
		return $(this).css("display") == "none" && $(this).css("position") == "absolute" && $(this).children().css("font-family") == "Arial";
	}).remove();
}

function convertYearWeekKeyToDate(key) {
	var year = parseInt(key.substr(0, 4));
	var weekNumber = key.substr(4);
	return getDateFromWeekAndYear(year, parseInt(weekNumber));
}

function convertYearMonthKeyToDate(key) {
	var year = parseInt(key.substr(0, 4));
	var monthStr = key.substr(4, key.length);
	var monthInt = parseInt(monthStr, "10"); // add base value
	var monthNumber = monthInt - 1;
	return Date.today().set({
		day : 1,
		month : monthNumber,
		year : year
	}).toString("MMM d, yyyy");
}

function convertYearMonthDayKeyToDate(key) {
	var year = parseInt(key.substr(0, 4));
	var monthStr = key.substr(4, 2);
	var monthInt = parseInt(monthStr, "10"); // add base value
	var monthNumber = monthInt - 1;

	var dayStr = key.substr(6, 2);
	var dayInt = parseInt(dayStr, "10"); // add base value
	dayNumber = dayInt;
	
	return Date.today().set({
		day : dayNumber,
		month : monthNumber,
		year : year
	}).toString("MMM d, yyyy");
}

function convertYearMonthKeyToMonthDay(key) {
	var year = parseInt(key.substr(0, 4));
	var monthStr = key.substr(4, key.length);
	var monthInt = parseInt(monthStr, "10"); // add base value
	var monthNumber = monthInt - 1;
	return Date.today().set({
		day : 1,
		month : monthNumber,
		year : year
	}).toString("MMM d");
}

function convertYearMonthDayKeyToMonthDay(key) {
	var year = parseInt(key.substr(0, 4));
	var monthStr = key.substr(4, 2);
	var monthInt = parseInt(monthStr, "10"); // add base value
	var monthNumber = monthInt - 1;

	var dayStr = key.substr(6, 2);
	var dayInt = parseInt(dayStr, "10"); // add base value
	dayNumber = dayInt;
	
	return Date.today().set({
		day : dayNumber,
		month : monthNumber,
		year : year
	}).toString("MMM d");
}

function convertYearMonthDayKeyToMonthDayYear(key) {
	var year = parseInt(key.substr(0, 4));
	var monthStr = key.substr(4, 2);
	var monthInt = parseInt(monthStr, "10"); // add base value
	var monthNumber = monthInt - 1;

	var dayStr = key.substr(6, 2);
	var dayInt = parseInt(dayStr, "10"); // add base value
	dayNumber = dayInt;
	
	return Date.today().set({
		day : dayNumber,
		month : monthNumber,
		year : year
	}).toString("MMM d, yyyy")
}

function getKeysFromGraphFormat(format) {
	var firstDate;
	var keys = [];
	if (format == '365') {
		firstDate = Date.today().add({
			months : -11
		});
		var key = firstDate.getFullYear().toString() + (firstDate.getMonth() + 1).toString();
		keys.push(key);
		for (var i = 1; i < 12; i++) {
			var date = Date.today().add({
				months : -11
			}).addMonths(i);
			var month = date.getMonth() + 1;
			if (month < 10) {
				keys.push(date.getFullYear().toString() + ("0" + (date.getMonth() + 1).toString()));
			} else {
				keys.push(date.getFullYear().toString() + (date.getMonth() + 1).toString());
			}

		}

	} else if(format == '7' || format == '14' || format == '21'  || format == '28') {
		firstDate = Date.today().add({
			days : -parseInt(format)
		});
		var count = parseInt(format);
		/*
		 * var key = firstDate.getFullYear().toString() + (firstDate.getWeek()).toString(); keys.push(key);
		 */
		for (var i = 1; i <= count; i++) {
			var date = firstDate;
			var month = date.getMonth() + 1;
			var monthStr = "";
			if (month < 10) {
				monthStr = '0' + month.toString();
				
			}else{
				monthStr = month.toString();
			}
			
			var dayStr = "";
			var day  = date.getDate();
			if (day < 10) {
				dayStr = '0' + day.toString();
				
			}else{
				dayStr = day.toString();
			}
			
			keys.push(date.getFullYear().toString() + monthStr + dayStr);
			
			date = firstDate.add({
				days : 1
			});

		}
	}else {
		firstDate = Date.today().add({
			days : -parseInt(format)
		});
		var count = parseInt(parseInt(format) / 7);
		if (parseInt(format) % 7 != 0) {
			count += 1;
		}
		/*
		 * var key = firstDate.getFullYear().toString() + (firstDate.getWeek()).toString(); keys.push(key);
		 */
		for (var i = 1; i <= count; i++) {
			var date = firstDate.add({
				days : 7
			});
			var week = date.getWeek();
			if (week < 10) {
				week = "0" + week.toString();
				keys.push(date.getFullYear().toString() + week);
			} else if (week > 52) {
				if (date.getMonth() == 11) {
					keys.push((date.getFullYear() + 1).toString() + '00');
				} else {
					keys.push(date.getFullYear().toString() + '00');
				}

			} else {
				keys.push(date.getFullYear().toString() + week.toString());
			}

		}
	}
	return keys;
}

// Detect mousedown event to close to autocomplete list on outside click
$(document).mousedown(function(event) {
	if ($('.dsh-res-display').is(':visible') && !$(event.target).hasClass('dsh-res-display')) {
		$('.dsh-res-display').parent().hide();
	}
});

// Being called from dashboard.jsp on key up event.
function searchBranchRegionOrAgent(searchKeyword, flow) {
	var e;
	if (flow == 'icons'|| flow == 'transactions') {
		e = document.getElementById("selection-list");
	} else if (flow == 'graph') {
		e = document.getElementById("graph-sel-list");
	} else if (flow == 'reports') {
		e = document.getElementById("report-sel");
	} else {
		return false;
	}
	searchColumn = e.options[e.selectedIndex].value;
	var payload = {
		"columnName" : colName,
		"columnValue" : colValue,
		"searchColumn" : searchColumn,
		"searchKey" : searchKeyword
	};
	if (existingCall != undefined && existingCall != null) {
		existingCall.abort();
	}
	existingCall = callAjaxGetWithPayloadData("./findregionbranchorindividual.do", function(data) {
		if (flow == 'icons') {
			$('#dsh-srch-res').addClass('dsh-sb-dd');
			$('#dsh-srch-res').html(data).show().perfectScrollbar();
			$('#dsh-srch-res').perfectScrollbar('update');
			if ($('#dsh-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#dsh-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-srch-res').hide();
			}
		} else if (flow == 'graph') {
			$('#dsh-grph-srch-res').addClass('dsh-sb-dd');
			$('#dsh-grph-srch-res').html(data).show().perfectScrollbar();
			$('#dsh-grph-srch-res').perfectScrollbar('update');
			if ($('#dsh-grph-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#dsh-grph-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-grph-srch-res').hide();
			}
		} else if (flow == 'reports') {
			$('#dsh-srch-report').addClass('dsh-sb-dd');
			$('#dsh-srch-report').html(data).show().perfectScrollbar();
			$('#dsh-srch-report').perfectScrollbar('update');
			if ($('#dsh-srch-report').children('div.dsh-res-display').length <= 0) {
				$('#dsh-srch-report').removeClass('dsh-sb-dd');
				$('#dsh-srch-report').hide();
			}
		}else if ( flow == 'transactions') {
			$('#trans-srch-res').addClass('dsh-sb-dd');
			$('#trans-srch-res').html(data).show().perfectScrollbar();
			$('#trans-srch-res').perfectScrollbar('update');
			if ($('#trans-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#trans-srch-res').removeClass('dsh-sb-dd');
				$('#trans-srch-res').hide();
			}
		}else if ( flow == 'procSurvey') {
			$('#proc-sur-srch-res').addClass('dsh-sb-dd');
			$('#proc-sur-srch-res').html(data).show().perfectScrollbar();
			$('#proc-sur-srch-res').perfectScrollbar('update');
			if ($('#proc-sur-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#proc-sur-srch-res').removeClass('dsh-sb-dd');
				$('#proc-sur-srch-res').hide();
			}
		}else if ( flow == 'actvUser') {
			$('#actv-usr-srch-res').addClass('dsh-sb-dd');
			$('#actv-usr-srch-res').html(data).show().perfectScrollbar();
			$('#actv-usr-srch-res').perfectScrollbar('update');
			if ($('#actv-usr-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#actv-usr-srch-res').removeClass('dsh-sb-dd');
				$('#actv-usr-srch-res').hide();
			}
		}

		$('.dsh-res-display').off('click');
		$('.dsh-res-display').click(function(event) {
			event.stopPropagation();
			var value = $(this).data('attr');
			if (searchColumn == "regionName") {
				columnName = "regionId";
			} else if (searchColumn == "branchName") {
				columnName = "branchId";
			} else if (searchColumn == "displayName") {
				columnName = "agentId";
			} else if (searchColumn == "company") {
				columnName = "companyId";
			}

			if (flow == 'icons') {
				$('#dsh-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForCount = columnName;
				lastColValueForCount = value;
				showSurveyStatistics(columnName, value);
			} else if (flow == 'graph') {
				$('#dsh-grph-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-grph-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForGraph = columnName;
				lastColValueForGraph = value;
				showSurveyStatisticsGraphically(columnName, value);
			} else if (flow == 'reports') {
				$('#dsh-srch-report').removeClass('dsh-sb-dd');
				$('#admin-report-dwn').val($(this).html()).attr('data-prev-val', "");
				$('#report-sel').attr('data-iden', columnName);
				$('#report-sel').attr('data-idenVal', value);
			} else if (flow == 'transactions') {
				$('#trans-srch-res').removeClass('dsh-sb-dd');
				$('#trans-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForGraphTrans = columnName;
				lastColValueForGraphTrans = value;
			} else if (flow == 'procSurvey') {
				$('#proc-sur-srch-res').removeClass('dsh-sb-dd');
				$('#proc-sur-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForGraphProcSurvey = columnName;
				lastColValueForGraphProcSurvey = value;
				showProsSurveyStatisticsGraphically(columnName, value);
			}else if (flow == 'actvUser') {
				$('#actv-usr-srch-res').removeClass('dsh-sb-dd');
				$('#actv-usr-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForGraphActvUser = columnName;
				lastColValueForGraphActvUser = value;
				showActiveUsersStatisticsGraphically(columnName, value);
			}
			
			$('.dsh-res-display').hide();
		});
		$('.dsh-res-display').off('mouseover');
		$('.dsh-res-display').on('mouseover', function() {
			$('.dsh-res-display').removeClass('dsh-res-hover');
			$(this).addClass('dsh-res-hover');
		});
		$('.dsh-res-display').off('mouseout');
		$('.dsh-res-display').on('mouseout', function() {
			$(this).removeClass('dsh-res-hover');
		});
	}, payload, true);
}
function searchCompany(searchKeyword, flow) {
	/* var e; */

	/*
	 * if (flow == 'reports') { e = document.getElementById("report-sel"); } else { return false; }
	 */
	searchColumn = "company";
	var payload = {
		"columnName" : colName,
		"columnValue" : colValue,
		"searchColumn" : searchColumn,
		"searchKey" : searchKeyword
	};

	callAjaxGetWithPayloadData("./findregionbranchorindividual.do", function(data) {
		if (flow == 'reports') {
			$('#admin-srch-report').addClass('dsh-sb-dd');
			$('#admin-srch-report').html(data).show().perfectScrollbar();
			$('#admin-srch-report').perfectScrollbar('update');
			if ($('#admin-srch-report').children('div.dsh-res-display').length <= 0) {
				$('#admin-srch-report').removeClass('dsh-sb-dd');
				$('#admin-srch-report').hide();
			}
		} else if (flow == 'hierarchy') {
			// TODO: Replace this stuff
			$('#hierarchy-srch-report').addClass('dsh-sb-dd');
			$('#hierarchy-srch-report').html(data).show().perfectScrollbar();
			$('#hierarchy-srch-report').perfectScrollbar('update');
			if ($('#hierarchy-srch-report').children('div.dsh-res-display').length <= 0) {
				$('#hierarchy-srch-report').removeClass('dsh-sb-dd');
				$('#hierarchy-srch-report').hide();
			}
		} else if (flow == 'transactions') {
			$('#trans-srch-res').addClass('dsh-sb-dd');
			$('#trans-srch-res').html(data).show().perfectScrollbar();
			$('#trans-srch-res').perfectScrollbar('update');
			if ($('#trans-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#trans-srch-res').removeClass('dsh-sb-dd');
				$('#trans-srch-res').hide();
			}
		}else if ( flow == 'procSurvey') {
			$('#proc-sur-srch-res').addClass('dsh-sb-dd');
			$('#proc-sur-srch-res').html(data).show().perfectScrollbar();
			$('#proc-sur-srch-res').perfectScrollbar('update');
			if ($('#proc-sur-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#proc-sur-srch-res').removeClass('dsh-sb-dd');
				$('#proc-sur-srch-res').hide();
			}
		}else if ( flow == 'actvUser') {
			$('#actv-usr-srch-res').addClass('dsh-sb-dd');
			$('#actv-usr-srch-res').html(data).show().perfectScrollbar();
			$('#actv-usr-srch-res').perfectScrollbar('update');
			if ($('#actv-usr-srch-res').children('div.dsh-res-display').length <= 0) {
				$('#actv-usr-srch-res').removeClass('dsh-sb-dd');
				$('#actv-usr-srch-res').hide();
			}
		}

		$('.dsh-res-display').off('click');
		$('.dsh-res-display').click(function(event) {
			event.stopPropagation();
			var value = $(this).data('attr');
			if (searchColumn == "company") {
				columnName = "companyId";
			}

			if (flow == 'reports') {
				$('#dsh-srch-report').removeClass('dsh-sb-dd');
				$('#admin-report-down').val($(this).html()).attr('data-prev-val', "");
				$('#admin-report-down').attr('data-iden', columnName);
				$('#admin-report-down').attr('data-idenVal', value);
			} else if (flow == 'hierarchy') {
				// TODO: Replace this stuff
				$('#dsh-srch-report').removeClass('dsh-sb-dd');
				$('#hierarchy-report-down').val($(this).html()).attr('data-prev-val', "");
				$('#hierarchy-report-down').attr('data-iden', columnName);
				$('#hierarchy-report-down').attr('data-idenVal', value);
			} else if (flow == 'transactions') {
				$('#trans-srch-res').removeClass('dsh-sb-dd');
				$('#trans-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForGraphTrans = columnName;
				lastColValueForGraphTrans = value;
				showTransactionStatisticsGraphically(columnName, value);
			}else if (flow == 'procSurvey') {
				$('#proc-sur-srch-res').removeClass('dsh-sb-dd');
				$('#proc-sur-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForGraphProcSurvey = columnName;
				lastColValueForGraphProcSurvey = value;
				showProsSurveyStatisticsGraphically(columnName, value);
			}else if (flow == 'actvUser') {
				$('#actv-usr-srch-res').removeClass('dsh-sb-dd');
				$('#actv-usr-sel-item').val($(this).html()).attr('data-prev-val', "");
				lastColNameForGraphActvUser = columnName;
				lastColValueForGraphActvUser = value;
				showActiveUsersStatisticsGraphically(columnName, value);
			}
			
			$('.dsh-res-display').hide();
		});
		$('.dsh-res-display').off('mouseover');
		$('.dsh-res-display').on('mouseover', function() {
			$('.dsh-res-display').removeClass('dsh-res-hover');
			$(this).addClass('dsh-res-hover');
		});
		$('.dsh-res-display').off('mouseout');
		$('.dsh-res-display').on('mouseout', function() {
			$(this).removeClass('dsh-res-hover');
		});
	}, payload, true);
}
$(document).on('click', '#admin-bill-rep-bnt', function(e) {
	var email = $('#admin-mail-id').val();
	var idenVal = $('#admin-report-down').attr('data-idenVal');
	var selectedProf = $('#admin-report-down').attr('data-iden');

	if (email != undefined && email != "") {
		if (emailRegex.test(email) == false) {
			showErrorMobileAndWeb('Please enter a valid email address');
		}
	}
	if (idenVal == undefined || idenVal == "") {
		showErrorMobileAndWeb('Please select a company');
		return;
	}
	var payload = {
		"mailid" : email,
		"companyId" : idenVal
	}
	callAjaxGetWithPayloadData("./downloadcompanyuserreport.do", function(data) {
		if (data == "success") {
			$('#overlay-toast').html('The User List Report will be mailed to you shortly');
			showToast();
		}
	}, payload, true);

});

$(document).on('click', '#admin-hierarchy-rep-bnt', function(e) {
	var email = $('#hierarchy-mail-id').val();
	var idenVal = $('#hierarchy-report-down').attr('data-idenVal');
	var selectedProf = $('#hierarchy-report-down').attr('data-iden');

	if (email != undefined && email != "") {
		if (emailRegex.test(email) == false) {
			showErrorMobileAndWeb('Please enter a valid email address');
		}
	}
	if (idenVal == undefined || idenVal == "") {
		showErrorMobileAndWeb('Please select a company');
		return;
	}
	var payload = {
		"mailid" : email,
		"companyId" : idenVal
	}
	callAjaxGetWithPayloadData("./downloadcompanyhierarchyreport.do", function(data) {
		if (data == "success") {
			$('#overlay-toast').html('The Company Hierarchy Report will be mailed to you shortly');
			showToast();
		}
	}, payload, true);

});

function sendSurveyReminderMail(surveyPreInitiationId, customerName, disableEle) {
	if ($(disableEle).data('requestRunning')) {
		return;
	}

	disable(disableEle);
	var payload = {
		"surveyPreInitiationId" : surveyPreInitiationId
	};
	$.ajax({
		url : "./sendsurveyremindermail.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			if (data.errMsg == undefined || data.errMsg == ""){
				var toastmsg = data.success;
				$('#overlay-toast').html(toastmsg);
				showToastLong();
			}else{
				var toastmsg = data.errMsg;
				$('#overlay-toast').html(toastmsg);
				showToastLong();
			}
			
		},
		complete : function(data) {
			enable(disableEle);
			
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html('Something went wrong while sending mail. Please try again after sometime.');
			showToast();
		}
	});
}

function showDisplayPic() {
	$.ajax({
		url : "./getdisplaypiclocation.do",
		type : "GET",
		cache : false,
		dataType : "JSON",
		success : function(data) {

		},
		complete : function(data) {
			if (data.errCode == undefined) {
				var imageUrl = data.responseJSON;
				if (imageUrl != undefined && imageUrl != "undefined" && imageUrl.trim() != "") {
					$("#dsh-prsn-img").removeClass('dsh-pers-default-img');
					$("#dsh-prsn-img").removeClass('dsh-office-default-img');
					$("#dsh-prsn-img").removeClass('dsh-region-default-img');
					$("#dsh-prsn-img").removeClass('dsh-comp-default-img');

					$("#dsh-prsn-img").css("background", "url(" + imageUrl + ") no-repeat center");
					$("#dsh-prsn-img").css("background-size", "cover");
					$("#dsh-prsn-img").attr("data-img", imageUrl);
				}
				return data.responseJSON;
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$("#dsh-prsn-img").removeClass('person-img');
			if (colName == 'agentId') {
				$("#dsh-prsn-img").addClass('dsh-pers-default-img');
			} else if (colName == 'branchId') {
				$("#dsh-prsn-img").addClass('dsh-office-default-img');
			} else if (colName == 'regionId') {
				$("#dsh-prsn-img").addClass('dsh-region-default-img');
			} else if (colName == 'companyId') {
				$("#dsh-prsn-img").addClass('dsh-comp-default-img');
			}
		}
	});
}

function updateCurrentProfile(entityType, entityValue, callbackFunction) {
	var url = "./updatecurrentprofile.do?entityId=" + entityValue + "&entityType=" + entityType;
	callAjaxGET(url, callbackFunction, true);
}

function showSurveyRequestPage() {
	callAjaxGET('./redirecttosurveyrequestpage.do', function(data) {
		$('#srv-req-pop').removeClass('hide');
		$('#srv-req-pop').addClass('survey-request-popup-container');
		$('#srv-req-pop').show();
		$('#srv-req-pop').find('.survey-request-popup').html(data);

	}, true);
}

$(document).on('click', '#dashboard-sel', function(e) {
	e.stopPropagation();
	$('#da-dd-wrapper-profiles').slideToggle(200);
});

$(document).on('click', '.da-dd-item', function(e) {
	showOverlay();
	$('#dashboard-sel').html($(this).html());
	$('#da-dd-wrapper-profiles').slideToggle(200);

	attrName = $(this).attr('data-column-type');
	attrVal = $(this).attr('data-column-value');

	// update selected profile in session

	updateCurrentProfile($(this).attr('data-column-type'), $(this).attr('data-column-value'), function() {
		showDashOverlay('#logo-dash');
		showDashOverlay('#latest-post-ep');
		showDashOverlay('#review-ep');
		showDashOverlay('#hierarchy-ep');
		showDashOverlay('#config-setting-dash');
		showDashOverlay('#social-media-dash');
		var selectedTab = window.location.hash.split("#")[1];
		showMainContent('./' + selectedTab + '.do');
		callAjaxGetWithPayloadData("/isvendastaaccessibleforthesession.do", function(data) {
			vendastaAccess = JSON.parse(data);
			showOrHideListingsManager( vendastaAccess );
			showOrHideVendastaProductSettings( vendastaAccess );
		});
	});
	
	sessionStorage.setItem("newSession",false);
});

function setUpListenerForSortCriteriaDropdown(){
	$('.sort-option-item').on('click',function(event){
		$('#sort-criteria-sel').val($(this).html());
		$('#sort-options').slideToggle(200);
		
		var sortCr;
		if( $('#sort-criteria-sel').val() == "Sort responses by Featured Reviews" ){
			sortCr = "feature";
		} else {
			sortCr = "date";
		}
		
		var payload = {
			"sortCriteria" : sortCr
		};
		
		callAjaxPostWithPayloadData( "./updatesortcriteria.do", function(data){
				var message = JSON.parse(data);
				$('#overlay-toast').html(message.message);
				showToast();
			}, payload, false);
	});
}

function setUpListenerForEmailOptionDropdown(){
	$('.email-option-item').on('click',function(event){
		$('#email-sel').val($(this).html());
		$('#email-options').slideToggle(200);
		var payload = {
			"sendEmailThrough" : $("#email-sel").val()
		};
		
		callAjaxPostWithPayloadData( "./updatesendemailthrough.do", function(data){
				var message = JSON.parse(data);
				$('#overlay-toast').html(message.message);
				showToast();
			}, payload, false);
	});
}

function autoAppendSortOrderDropdown(sortOrderId, classes) {
	autoAppendTextDropdown(sortOrderId, classes, ["Sort responses by Date", "Sort responses by Featured Reviews"]);
}

function autoAppendEmailCriteriaDropdown(emailCriteriaId, classes) {
	autoAppendTextDropdown(emailCriteriaId, classes, ["socialsurvey.me" , "socialsurvey.us"]);
}


//Generic functions
function autoAppendTextDropdown(elementId, classes, listOfValues, appendAsHtml ) {
	
	if( true === appendAsHtml ){
		listOfValues.map(function( item ){
			$(elementId).append($('<div/>').addClass(classes).html(item));
		});
	} else {
		listOfValues.map(function( item ){
			$(elementId).append($('<div/>').addClass(classes).text(item));
		});
	}
}

var ratingMouseUp = function (e){
	var container = $('#st-dd-wrapper-min-post');
	if (!container.is(e.target) && container.has(e.target).length == 0){
		container.slideToggle(200);
	}
	$(document).unbind("mouseup",ratingMouseUp);
};

var sortCriteriaMouseUp = function (e){
	var container = $('#sort-options');
	if (!container.is(e.target) && container.has(e.target).length == 0){
		container.slideToggle(200);
	}
	$(document).unbind("mouseup",sortCriteriaMouseUp);
};

var emailCriteriaMouseUp = function (e){
	var container = $('#email-options');
	if (!container.is(e.target) && container.has(e.target).length == 0){
		container.slideToggle(200);
	}
	$(document).unbind("mouseup",emailCriteriaMouseUp);
};

var surveyMailThresholdMouseUp = function (e){
	var container = $('#st-dd-wrapper-survey-mail-thrs');
	if (!container.is(e.target) && container.has(e.target).length == 0){
		container.slideToggle(200);
	}
	$(document).unbind("mouseup",surveyMailThresholdMouseUp);
};

$(document).click(function(e) {
	e.stopPropagation();
	if ($('#da-dd-wrapper-profiles').css('display') == "block") {
		$('#da-dd-wrapper-profiles').toggle();
	}

	if ($('#srch-crtria-list').css('display') == "block") {
		$('#srch-crtria-list').toggle();
	}
	
	if ($('#time-frame-options').css('display') == "block") {
		$('#time-frame-options').toggle();
	}
	

	/*
	 * if($('.v-tbl-icn-wraper').is(':visible')) { $('.v-tbl-icn-wraper').hide(); }
	 */
});

// Populate Existing Survey Questions
function commonActiveSurveyCallback(response) {
	showInfo(response);
	loadActiveSurveyQuestions();
	$('.err-nw-wrapper').delay(2000).fadeOut();
}

function loadActiveSurveyQuestions() {
	var url = "./getactivesurveyquestions.do";
	callAjaxGET(url, function(data) {
		$('#bs-ques-wrapper').html(data);
		bindEditSurveyEvents();
		resizeAdjBuildSurvey();
	}, true);
}

function resizeAdjBuildSurvey() {
	var winW = window.innerWidth;
	if (winW < 768) {
		var txtW = winW - 118;
		$('.srv-tbl-txt').width(txtW);
	} else {
	}
}

function bindEditSurveyEvents() {
	// On Hover
	$('.bd-srv-tbl-row').off('click');
	$('.bd-srv-tbl-row').on('click', function() {
		if (getWindowWidth() < 768) {
			if (!$(this).find('.srv-tbl-btns').is(':visible')) {
				$('.srv-tbl-btns').hide();
				$(this).find('.srv-tbl-btns').show();
			} else {
				$(this).find('.srv-tbl-btns').hide();
			}
		}
	});
	$('.bd-srv-tbl-row').off('touchStart');
	$('.bd-srv-tbl-row').on('touchStart', function() {
		$(this).trigger('click');
	});
	$('.bd-srv-tbl-row').off('mouseover');
	$('.bd-srv-tbl-row').on('mouseover', function() {
		if (getWindowWidth() > 768) {
			$(this).addClass('bd-srv-tbl-row-hover');
			$(this).find('.srv-tbl-btns').show();
		}
	});
	$('.bd-srv-tbl-row').off('mouseout');
	$('.bd-srv-tbl-row').on('mouseout', function() {
		if (getWindowWidth() > 768) {
			$(this).removeClass('bd-srv-tbl-row-hover');
			$(this).find('.srv-tbl-btns').hide();
		}
	});

	// Add Survey Question overlay
	$('#btn-add-question').off('click');
	$('#btn-add-question').on('click', function() {
		$('#bd-srv-pu').show();
		$(document).addClass('body-no-scroll');
	});
	// Question edit
	$('.srv-tbl-edit').off('click');
	$('.srv-tbl-edit').on('click', function(e) {
		e.stopPropagation();
		if ($(this).parent().parent().next().hasClass('sb-edit-q-wrapper')) {
			return;
		}
		var questionId = $(this).parent().parent().data('questionid');
		var url = "./getsurveyquestion.do?questionId=" + questionId;

		callAjaxGET(url, function(response) {
			$('.sb-edit-q-wrapper').remove();
			$('.bd-q-pu-done-wrapper').remove();
			$('.bd-srv-tbl-row-' + questionId).after(response);
			revertQuestionOverlay();
		}, true);
	});
	// Remove Question from survey
	$('.srv-tbl-rem').off('click');
	$('.srv-tbl-rem').on('click', function(e) {
		e.stopPropagation();
		var questionId = $(this).parent().parent().data('questionid');
		
		if( ratingQuestionCount <= 1 && $(this).parent().parent().data('rating-question') == true ){
			$('#overlay-toast').html( "Cannot remove the question, there must be a minimum of 1 ranking type question." );
			showToast();
			return;
		} else {
			ratingQuestionCount--;
		}
		
		var url = "./removequestionfromsurvey.do?questionId=" + questionId;

		createPopupConfirm("Delete Question", "Do you want to delete the question ?", "Delete", "Cancel");
		$('#overlay-continue').click(function() {
			overlayRevert();
			$('#overlay-continue').unbind('click');

			callAjaxPOST(url, commonActiveSurveyCallback, true);
		});
		$('#overlay-cancel').click(function() {
			$('#overlay-continue').unbind('click');
			$('#overlay-cancel').unbind('click');
			overlayRevert();

			// loadActiveSurveyQuestions();
		});
	});

	// Reorder Question in survey
	$('.srv-tbl-move-up').off('click');
	$('.srv-tbl-move-up').on('click', function(e) {
		e.stopPropagation();
		var formData = new FormData();
		formData.append("questionId", $(this).parent().parent().data('questionid'));
		formData.append("reorderType", "up");

		callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
	});
	$('.srv-tbl-move-dn').off('click');
	$('.srv-tbl-move-dn').on('click', function(e) {
		e.stopPropagation();
		var formData = new FormData();
		formData.append("questionId", $(this).parent().parent().data('questionid'));
		formData.append("reorderType", "down");

		callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
	});

	// Save the changes
	$('.bd-q-btn-done').off('click');
	$('.bd-q-btn-done').on('click', function(e) {
		e.stopPropagation();
		var lastQuestion = currentQues - 1;
		var count = 1;
		var editedStatus = true;
		while (count <= currentQues) {
			if ($('#bs-question-' + count).attr('data-status') == 'edited') {
				editedStatus = true;
				break;
			} else {
				editedStatus = false;
			}
			count++;
		}
		if (editedStatus == false) {
			revertQuestionOverlay();
			setTimeout(function() {
				loadActiveSurveyQuestions();
			}, 2000);
			return;
		}

		createPopupConfirm("Unsaved changes detected", "Do you want to save your changes ?", "Save", "Cancel");

		$('#overlay-continue').off('click');
		$('#overlay-continue').on('click', function() {
			var count = 1;
			while (count <= lastQuestion) {
				// submit for adding question
				if (count > 0 && $('#bs-question-' + count).attr('data-state') == 'new' && $('#bs-question-' + count).attr('data-status') == 'edited') {

					if ($('#sb-question-txt-' + count).val() == '' || $('#sb-question-type-' + count).val() == '') {
						$("#overlay-toast").html('Please finish adding the Question');
						showToast();
					} else {
						var url = "./addquestiontosurvey.do?order=" + count;
						$('#bs-question-' + count).attr('data-state', 'editable');
						$('#bs-question-' + count).attr('data-status', 'new');
						callAjaxFormSubmit(url, function(data) {
							var map = $.parseJSON(data);
							$("#overlay-toast").html(map.message);
							showToast();

							if (map.status == "success") {
								$('#bs-question-' + count).attr('data-quesref', map.questionId);
								revertQuestionOverlay();
							} else {
								$('#bs-question-' + count).attr('data-state', 'new');
								$('#bs-question-' + count).attr('data-status', 'edited');
							}
						}, 'bs-question-' + count, '#overlay-continue');
					}
				}
				// submit for modifying question
				else if (count > 0 && $('#bs-question-' + count).attr('data-state') == 'editable' && $('#bs-question-' + count).attr('data-status') == 'edited') {

					if ($('#sb-question-txt-' + count).val() == '' || $('#sb-question-type-' + count).val() == '') {
						$("#overlay-toast").html('Please finish editing the Question');
						showToast();
					} else {
						var questionId = $('#bs-question-' + count).attr('data-quesref');
						var url = "./updatequestionfromsurvey.do?order=" + count + "&questionId=" + questionId;
						callAjaxFormSubmit(url, function(data) {
							var map = $.parseJSON(data);
							$("#overlay-toast").html(map.message);
							showToast();

							if (map.status == "success") {
								revertQuestionOverlay();
								$('#bs-question-' + count).attr('data-status', 'new');
							} else {
								$('#bs-question-' + count).attr('data-status', 'edited');
							}
						}, 'bs-question-' + count, '#overlay-continue');
					}
				}
				count++;
			}

			$('#overlay-continue').unbind('click');
			$('#overlay-cancel').unbind('click');
			overlayRevert();
			setTimeout(function() {
				loadActiveSurveyQuestions();
			}, 2000);
		});
		$('#overlay-cancel').click(function() {
			$('#overlay-continue').unbind('click');
			$('#overlay-cancel').unbind('click');
			overlayRevert();

			revertQuestionOverlay();
			loadActiveSurveyQuestions();
		});
	});

	$('.bd-nps-btn-done').on('click', function(e) {
		e.stopPropagation();
		var npsQuestion = $('#sb-nps-question-txt').val();
		var editedStatus=false;
		
		if ($('#nps-question-form').attr('data-status') == 'edited') {
			editedStatus=true;
		}
		/*var count=1;
		while (count <= currentQues) {
			if ($('#nps-question-form').attr('data-status') == 'edited') {
				editedStatus = true;
				break;
			} else {
				editedStatus = false;
			}
			count++;
		}*/
		
		if (editedStatus == false) {
			$("#overlay-toast").html('No changes detected. Retry Editing Question.');
			showToast();
			revertQuestionOverlay();
			setTimeout(function() {
				loadActiveSurveyQuestions();
			}, 2000);
			return;
		}
		
		createPopupConfirm("Unsaved changes detected", "Do you want to save your changes ?", "Save", "Cancel");

		$('#overlay-continue').off('click');
		$('#overlay-continue').on('click', function() {
				
				npsQuestion = $('#sb-nps-question-txt').val();
				if(npsQuestion == '' || npsQuestion == null || npsQuestion == undefined){
					$("#overlay-toast").html('Please finish adding the Question');
					showToast();
					return;
				}else{
					if ($('#nps-question-form').attr('data-state') == 'new' && $('#nps-question-form').attr('data-status') == 'edited') {
						
						var url = "./addquestiontosurvey.do?order="+ npsOrder;
						callAjaxFormSubmit(url, function(data) {
							var map = $.parseJSON(data);
							$("#overlay-toast").html(map.message);
							showToast();

							if (map.status == "success") {
								$('#nps-question-form').attr('data-quesref', map.questionId);
								$("#overlay-toast").html("Successfully added NPS Question");
								showToast();
								revertQuestionOverlay();
							} else {
								$('#nps-question-form').attr('data-state', 'new');
								$('#nps-question-form').attr('data-status', 'edited');
								$("#overlay-toast").html("Retry Saving NPS Question");
								showToast();
							}
						}, 'nps-question-form', '#overlay-continue');
						
					}else if ($('#nps-question-form').attr('data-state') == 'editable' && $('#nps-question-form').attr('data-status') == 'edited'){
						
						var questionId = $('#nps-question-form').attr('data-quesref');
						var url = "./updatequestionfromsurvey.do?order=" + npsOrder + "&questionId=" + questionId;
						callAjaxFormSubmit(url, function(data) {
							var map = $.parseJSON(data);
							$("#overlay-toast").html(map.message);
							showToast();

							if (map.status == "success") {
								$('#nps-question-form').attr('data-status', 'new');
								$("#overlay-toast").html("Successfully edited NPS Question");
								showToast();
								revertQuestionOverlay();
							} else {
								$('#nps-question-form').attr('data-status', 'edited');
								$("#overlay-toast").html("Retry Saving NPS Question");
								showToast();
							}
						}, 'nps-question-form', '#overlay-continue');
					}
				}
			

			$('#overlay-continue').unbind('click');
			$('#overlay-cancel').unbind('click');
			overlayRevert();
			setTimeout(function() {
				loadActiveSurveyQuestions();
			}, 2000);
		});
		$('#overlay-cancel').click(function() {
			$('#overlay-continue').unbind('click');
			$('#overlay-cancel').unbind('click');
			overlayRevert();

			revertQuestionOverlay();
			loadActiveSurveyQuestions();
		});
	});
}

function revertQuestionOverlay() {
	var url = "./revertquestionoverlay.do";
	callAjaxGET(url, function(data) {
		$('#bd-quest-wrapper').html(data);
	}, true);

	$('#bd-srv-pu').hide();
	$(document).removeClass('body-no-scroll');
	currentQues = 1;
}

// Clear the current edited question
$(document).on('click', '.bd-q-pu-close', function() {
	$(this).parent().parent().remove();
});

$(document).on('input', '.bd-q-pu-txt-edit', function() {
	var quesNum = $(this).closest('form').data('quesnum');
	$('#nps-ques-edit').val(false);
	$('#bs-question-edit-' + quesNum).attr('data-status', 'edited');
	showStatus('#bs-question-edit-' + quesNum, 'Edited');
});

$(document).on('click', '.bd-q-btn-done-edit', function() {
	var questionId = $(this).data('quesnum');

	if ($('#sb-question-edit-txt-' + questionId).val() == '' || $('#sb-question-edit-type-' + questionId).val() == '') {
		$("#overlay-toast").html('Please finish editing the Question');
		showToast();
	} else {
		var url = "./updatequestionfromsurvey.do?order=" + questionId + "&questionId=" + questionId;
		showProgress('#bs-question-edit-' + questionId);
		callAjaxFormSubmit(url, function(data) {
			var map = $.parseJSON(data);

			if (map.status == "success") {
				showInfo(map.message);
				$('.bd-srv-tbl-row-' + questionId).next().remove();

				delay(function() {
					loadActiveSurveyQuestions();
				}, 500);
			} else {
				showStatus('#bs-question-edit-' + questionId, 'Retry Saving');
			}
		}, 'bs-question-edit-' + questionId, '.bd-q-btn-done-edit');
	}
});

// Select question type
$(document).on('click', '.bd-tab-rat', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-rating').show();

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum + '"]').val('sb-range-smiles');
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-tab-rad', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-radio').show();
	
	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum + '"]').val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-tab-mcq', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-mcq').show();

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum + '"]').val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-tab-com', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-com').show();

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum + '"]').val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-ans-img-wrapper', function() {
	$(this).parent().parent().find('.bd-ans-img').addClass('bd-img-sel');
	$(this).find('.bd-ans-img').removeClass('bd-img-sel');

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum + '"]').val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-com-chk', function() {
	if ($(this).hasClass('bd-com-unchk')) {
		$(this).removeClass('bd-com-unchk');
	} else {
		$(this).addClass('bd-com-unchk');
	}
});

$(document).on('click', '#user-ranking-chkbox-wrapper', function() {
	if ($('#user-ranking-chkbox').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques').val(true);
	} else {		
		$('#user-ranking-chkbox').addClass('bd-check-img-checked')
		 $('#user-ranking-ques').val(false);
	}
});

$(document).on('click', '#user-ranking-chkbox-wrapper-edit', function() {
	if ($('#user-ranking-chkbox-edit').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox-edit').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques-edit').val(true);
	} else {		
		$('#user-ranking-chkbox-edit').addClass('bd-check-img-checked')
		 $('#user-ranking-ques-edit').val(false);
	}
});

$(document).on('click', '#user-ranking-chkbox-wrapper-overlay', function() {
	if ($('#user-ranking-chkbox-overlay').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox-overlay').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques-overlay').val(true);
	} else {		
		$('#user-ranking-chkbox-overlay').addClass('bd-check-img-checked')
		 $('#user-ranking-ques-overlay').val(false);
	}
});

$(document).on('click', '#user-ranking-chkbox-wrapper-new', function() {
	if ($('#user-ranking-chkbox-new').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox-new').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques-new').val(true);
	} else {		
		$('#user-ranking-chkbox-new').addClass('bd-check-img-checked')
		 $('#user-ranking-ques-new').val(false);
	}
});

$(document).on('click', '#user-ranking-chkbox-nps-wrapper', function() {
	if ($('#user-ranking-nps-chkbox').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-nps-chkbox').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques').val(true);
	} else {		
		$('#user-ranking-nps-chkbox').addClass('bd-check-img-checked')
		 $('#user-ranking-ques').val(false);
	}
});

$(document).on('click', '#user-ranking-chkbox-wrapper-nps', function() {
	if ($('#user-ranking-chkbox-nps').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox-nps').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques-nps').val(true);
	} else {		
		$('#user-ranking-chkbox-nps').addClass('bd-check-img-checked')
		 $('#user-ranking-ques-nps').val(false);
	}
	$('#nps-question-form').attr('data-status','edited');
});

$(document).on('click', '#user-ranking-chkbox-wrapper-edit-nps', function() {
	if ($('#user-ranking-chkbox-edit-nps').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox-edit-nps').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques-edit').val(true);
	} else {		
		$('#user-ranking-chkbox-edit-nps').addClass('bd-check-img-checked')
		 $('#user-ranking-ques-edit').val(false);
	}
});

$(document).on('click', '#user-ranking-chkbox-wrapper-overlay-nps', function() {
	if ($('#user-ranking-chkbox-overlay-nps').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox-overlay-nps').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques-overlay').val(true);
	} else {		
		$('#user-ranking-chkbox-overlay-nps').addClass('bd-check-img-checked')
		 $('#user-ranking-ques-overlay').val(false);
	}
});

$(document).on('click', '#user-ranking-chkbox-wrapper-new-nps', function() {
	if ($('#user-ranking-chkbox-new-nps').hasClass('bd-check-img-checked')) {		
		$('#user-ranking-chkbox-new-nps').removeClass('bd-check-img-checked');
		 $('#user-ranking-ques-new').val(true);
	} else {		
		$('#user-ranking-chkbox-new-nps').addClass('bd-check-img-checked')
		 $('#user-ranking-ques-new').val(false);
	}
});

$(document).on('click', '#avg-score-chkbox-wrapper-new', function() {
	if ($('#avg-score-chkbox-new').hasClass('bd-check-img-checked')) {		
		$('#avg-score-chkbox-new').removeClass('bd-check-img-checked');
		 $('#avg-score-ques-new').val(true);
	} else {		
		$('#avg-score-chkbox-new').addClass('bd-check-img-checked')
		 $('#avg-score-ques-new').val(false);
	}
});

$(document).on('click', '#avg-score-chkbox-wrapper-overlay', function() {
	if ($('#avg-score-chkbox-overlay').hasClass('bd-check-img-checked')) {		
		$('#avg-score-chkbox-overlay').removeClass('bd-check-img-checked');
		 $('#avg-score-ques-overlay').val(true);
	} else {		
		$('#avg-score-chkbox-overlay').addClass('bd-check-img-checked')
		 $('#avg-score-ques-overlay').val(false);
	}
});

$(document).on('click', '#avg-score-chkbox-wrapper-edit', function() {
	if ($('#avg-score-chkbox-edit').hasClass('bd-check-img-checked')) {		
		$('#avg-score-chkbox-edit').removeClass('bd-check-img-checked');
		 $('#avg-score-ques-edit').val(true);
	} else {		
		$('#avg-score-chkbox-edit').addClass('bd-check-img-checked')
		 $('#avg-score-ques-edit').val(false);
	}
});

$(document).on('click', '#avg-score-chkbox-wrapper', function() {
	if ($('#avg-score-chkbox').hasClass('bd-check-img-checked')) {		
		$('#avg-score-chkbox').removeClass('bd-check-img-checked');
		 $('#avg-score-ques').val(true);
	} else {		
		$('#avg-score-chkbox').addClass('bd-check-img-checked')
		 $('#avg-score-ques').val(false);
	}
});

$(document).on('click', '#avg-score-chkbox-wrapper-nps', function() {
	if ($('#avg-score-chkbox-nps').hasClass('bd-check-img-checked')) {		
		$('#avg-score-chkbox-nps').removeClass('bd-check-img-checked');
		 $('#avg-score-ques-nps').val(true);
	} else {		
		$('#avg-score-chkbox-nps').addClass('bd-check-img-checked')
		 $('#avg-score-ques-nps').val(false);
	}
	$('#nps-question-form').attr('data-status','edited');
});

$(document).on('click', '#nps-chkbox-wrapper', function(e) {
	if ($('#nps-chkbox').hasClass('bd-check-img-checked')) {		
		$('#nps-add-edit').show();
		$('#nps-chkbox').removeClass('bd-check-img-checked');
		$('#nps-question-form').attr('data-state','new');
		$('#nps-question-form').attr('data-status','edited');
		$('#nps-ques').val(true);
	} else {		
		e.stopPropagation();
		var questionId = $('#nps-question-form').attr('data-quesref');
		$('#nps-chkbox').addClass('bd-check-img-checked');
		$('#nps-ques').val(false);
		$('#nps-add-edit').hide();
		
		if($('#nps-question-form').attr('data-state') != 'new'){
			var url = "./removequestionfromsurvey.do?questionId=" + questionId;

			callAjaxPOST(url, function(){
				$("#overlay-toast").html('Enable NPS Question to add/edit the NPS question');
				$('#nps-question-form').attr('data-state','new');
				$('#nps-question-form').attr('data-status','new');
				$('#sb-nps-question-txt').val(defaultNpsQuestion);
				$('#sq-not-very-likely-nps').val(defaultNotVeryLikely);
				$('#sq-very-likely-nps').val(defaultVeryLikely);
				showToast('error');
			}, true);
		}		
	}
});

// Submit previous question
var currentQues = 1;
$(document).on("focus", '.bd-q-pu-txt', function() {
	var quesOrder = $(this).closest('form').data('quesnum') - 1;

	// submit for adding new question
	if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'new' && $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {

		if ($('#sb-question-txt-' + quesOrder).val() == '' || $('#sb-question-type-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish adding the Question');
			showToast('error');
		} else {
			var url = "./addquestiontosurvey.do?order=" + quesOrder;
			showProgress('#bs-question-' + quesOrder);
			$('#bs-question-' + quesOrder).attr('data-state', 'editable');
			$('#bs-question-' + quesOrder).attr('data-status', 'new');
			callAjaxFormSubmit(url, function(data) {
				var map = $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();

				if (map.status == "success") {
					$('#bs-question-' + quesOrder).attr('data-quesref', map.questionId);
					showStatus('#bs-question-' + quesOrder, 'Saved');
				} else {
					$('#bs-question-' + quesOrder).attr('data-state', 'new');
					$('#bs-question-' + quesOrder).attr('data-status', 'edited');
					showStatus('#bs-question-' + quesOrder, 'Retry Saving');
				}
			}, 'bs-question-' + quesOrder, '');
		}
	}
	// submit for modifying question
	else if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'editable' && $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {

		if ($('#sb-question-txt-' + quesOrder).val() == '' || $('#sb-question-type-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish editing the Question');
			showToast();
		} else {
			var questionId = $('#bs-question-' + quesOrder).attr('data-quesref');
			var url = "./updatequestionfromsurvey.do?order=" + quesOrder + "&questionId=" + questionId;
			showProgress('#bs-question-' + quesOrder);
			callAjaxFormSubmit(url, function(data) {
				var map = $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();

				if (map.status == "success") {
					showStatus('#bs-question-' + quesOrder, 'Saved');
					$('#bs-question-' + quesOrder).attr('data-status', 'new');
				} else {
					showStatus('#bs-question-' + quesOrder, 'Retry Saving');
					$('#bs-question-' + quesOrder).attr('data-status', 'edited');
				}
			}, 'bs-question-' + quesOrder, '');
		}
	}
});

$(document).on("focus",'#sb-nps-question-txt',function(){
	npsQuestionText = $('#sb-nps-question-txt').val();
	$('#nps-question-form').attr('data-status', 'edited');
});

$(document).on("focus",'#sq-not-very-likely-nps',function(){
	notVeryLikelyText = $('#sq-not-very-likely-nps').val();
});

$(document).on("focus",'#sq-very-likely-nps',function(){
	veryLikelyText = $('#sq-very-likely-nps').val();
});

$(document).on("blur",'#sq-not-very-likely-nps',function(){
	if(notVeryLikelyText != $('#sq-not-very-likely-nps').val()){
		$('#nps-question-form').attr('data-status', 'edited');
	}	
});

$(document).on("blur",'#sq-very-likely-nps',function(){
	if(veryLikelyText != $('#sq-very-likely-nps').val()){
		$('#nps-question-form').attr('data-status', 'edited');
	}
});


$(document).on("input", '.bd-q-pu-txt', function() {
	$('#nps-ques-new').val(false);
	$('#nps-ques-overlay').val(false);
	var quesPresent = $(this).closest('form').data('quesnum');

	// Setting status
	showStatus('#bs-question-' + quesPresent, 'Edited');
	$('#bs-question-' + quesPresent).attr('data-status', 'edited');

	// populating next question
	if ($(this).val().trim().length > 0) {
		$(this).parent().next('.bs-ans-wrapper').show();

		if ($(this).data('nextquest') == false) {
			currentQues++;

			var url = "./populatenewform.do?order=" + currentQues;
			$('#sb-question-txt-' + quesPresent).data('nextquest', 'true');
			callAjaxGET(url, function(data) {
				$('#bs-question-' + quesPresent).after(data);
				$('#bs-question-' + quesPresent).next('.bd-quest-item').show();
			}, true);
		}
	}

	/*
	 * if ($(this).data('qno') != '1') { $(this).next('.bd-q-pu-close').show(); }
	 */
});

$(document).on('input', '.bd-mcq-txt', function() {
	// changing status to edited
	var name = $(this).attr('name');
	var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));

	showStatus('#bs-question-' + addMcqTextOption, 'Edited');
	$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
});

$(document).on('blur', '.bd-mcq-txt', function() {
	if ($(this).parent().is(':last-child')) {
		var name = $(this).attr('name');
		var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));

		// changing status to edited
		showStatus('#bs-question-' + addMcqTextOption, 'Edited');
		$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');

		var htmlData = '<div class="bd-mcq-row clearfix">' + '<div class="float-left bd-mcq-lbl">Option</div>' + '<input name="sb-answers-' + addMcqTextOption + '[]" class="float-left bd-mcq-txt">' + '<div class="float-left bd-mcq-close"></div>' + '</div>';
		$(this).parent().after(htmlData);

		// enable remove button
		if ($(this).parent().parent().children().length > 2) {
			$('.bd-mcq-close').removeClass('hide');
		}
	}
});

$(document).on('click', '.bd-mcq-close', function() {
	var parentDiv = $(this).parent().parent();
	$(this).parent().remove();

	// disable remove button
	if (parentDiv.children().length <= 3) {
		$('.bd-mcq-close').addClass('hide');
	}

	// changing status to edited
	var name = $(this).attr('name');
	var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));

	showStatus('#bs-question-' + addMcqTextOption, 'Edited');
	$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
});

// Overlay Popup
function createPopupConfirm(header, text, ok, cancel) {
	$('#overlay-header').html(header);
	$("#overlay-text").html(text);
	$('#overlay-continue').html(ok);
	$('#overlay-cancel').html(cancel);

	$('#overlay-main').show();
}
/*
 * function overlayRevert() { $('#overlay-main').hide(); $("#overlay-header").html(''); $("#overlay-text").html(''); $('#overlay-continue').html(''); $('#overlay-cancel').html(''); }
 */

// Progress Bar
function hideProgress(formId) {
	$(formId).find('.bd-q-status-wrapper').hide();
	$(formId).find('.bd-q-status-wrapper').find('.bd-q-status-txt').html('');
}
function showProgress(formId) {
	$(formId).find('.bd-q-status-wrapper').show();
	$(formId).find('.bd-q-status-wrapper').find('.bd-spinner').show();
	$(formId).find('.bd-q-status-wrapper').find('.bd-q-status-txt').html('Saving');
}
function showStatus(formId, text) {
	$(formId).find('.bd-q-status-wrapper').show();
	$(formId).find('.bd-q-status-wrapper').find('.bd-spinner').hide();
	$(formId).find('.bd-q-status-wrapper').find('.bd-q-status-txt').html(text);
}

/**
 * function to display success/failure message to user after an action
 * 
 * @param data
 */
function displayMessage(data) {
	$("#temp-message").html(data);
	var displayMessageDiv = $("#temp-message #display-msg-div");
	if ($(displayMessageDiv).hasClass("success-message")) {
		showInfoMobileAndWeb($(displayMessageDiv).html());
	} else if ($(displayMessageDiv).hasClass("error-message")) {
		showErrorMobileAndWeb($(displayMessageDiv).html());
	}
	var invalidMessage = $('#invalid-display-msg-div').text();
	if (invalidMessage != undefined && invalidMessage != "") {
		$('#overlay-toast').html(invalidMessage);
		showToast();
	}
	$("#temp-message").html("");
}

/**
 * function to display success and failure message to user after adding region and branch action
 * 
 * @param data
 */
function displayMessageForRegionAndBranchAddition(data) {
	$("#temp-message").html(data);
	var displayMessageDiv = $("#temp-message #display-msg-div");
	var invalidEmailAddressDiv = $("#display-invalid-email-addr-msg-div");
	var alreadyExistEmailAddressDiv = $("#display-already-exist-email-addr-msg-div");
	if ($(displayMessageDiv).hasClass("success-message")) {
		showInfoSuccessMobileAndWeb($(displayMessageDiv).html());
	} else if ($(displayMessageDiv).hasClass("error-message")) {
		showErrorSuccessMobileAndWeb($(displayMessageDiv).html());
	}
	if ($(invalidEmailAddressDiv).hasClass("error-message")) {
		showErrorInvalidMobileAndWeb($(invalidEmailAddressDiv).html());
	}
	if ($(alreadyExistEmailAddressDiv).hasClass("error-message")) {
		showErrorMobileAndWeb($(alreadyExistEmailAddressDiv).html());
	}
	var invalidMessage = $('#invalid-display-msg-div').text();
	if (invalidMessage != undefined && invalidMessage != "") {
		$('#overlay-toast').html(invalidMessage);
		showToast();
	}
	$("#temp-message").html("");
}

/**
 * checks whether is authorized to build hierarchy and displays message to the user
 */
function checkUserAuthorization() {
	var data = $("#server-message").html();
	var isUserAuthorized = $("#is-user-authorized").val();
	if (isUserAuthorized == "false") {
		displayMessage(data);
	}
}

/**
 * Method to fetch the company hierarchy
 */
function fetchCompleteHierarchy() {
	var profileName = $("#profile-name").val();
	fetchCompanyHierarchy("companyProfileName", profileName);
}
/**
 * Method to change the arrow in tabs according to the form displayed
 * 
 * @param spanId
 */
function changeTabArrow(spanId) {
	$('.bd-hdr-span').removeClass('bd-hdr-active');
	$('.bd-hdr-span').removeClass('bd-hdr-active-arr');
	$("#" + spanId).addClass('bd-hdr-active');
	$("#" + spanId).addClass('bd-hdr-active-arr');
}

/**
 * function to get the edit form based on tab value
 */
function getEditSectionFormByTab(tabValue) {
	switch (tabValue) {
	case 'region':
		getRegionEditPage();
		break;
	case 'office':
		getOfficeEditPage();
		break;
	case 'individual':
		getIndividualEditPage();
		break;
	case 'csv':
		getCsvUploadPage();
		break;
	default:
		getRegionEditPage();
		break;
	}
}

/**
 * Method to get the edit section form based on the account type and highest role of user
 */
function getEditSection() {
	var accountType = $("#account-type").val();
	var highestRole = $("#highest-role").val();
	switch (accountType) {
	case 'Enterprise':
		if (highestRole == 1 || highestRole == 2 || highestRole == 3) {
			getIndividualEditPage();
		} else {
			showErrorMobileAndWeb("Sorry you are not authorized to build hierarchy");
		}
		break;
	case 'Company':
		if (highestRole == 1 || highestRole == 2 || highestRole == 3) {
			getIndividualEditPage();
		} else {
			showErrorMobileAndWeb("Sorry you are not authorized to build hierarchy");
		}
		break;
	case 'Team':
		getIndividualEditPage();
		break;
	default:
		showErrorMobileAndWeb("Sorry you are not authorized to build hierarchy");
		break;
	}
}

/**
 * function to get the region edit page
 */
function getRegionEditPage() {
	var url = "./getregioneditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-region-tab");
}

/**
 * function to get the office edit page
 */
function getOfficeEditPage() {
	var url = "./getofficeeditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-office-tab");
}
/**
 * function to get the individual edit page
 */
function getIndividualEditPage() {
	var url = "./getindividualeditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-individual-tab");
}

function paintEditSection(data) {
	var isUserAuthorized = $("#is-user-authorized").val();
	$("#bd-edit-form-section").html(data);
	/**
	 * allow hierarchy management only if the user is authorized
	 */
	if (isUserAuthorized == "false") {
		$("#bd-edit-form-section :input").prop("disabled", true);
		$("#bd-edit-form-section").click(function() {
			return false;
		});
		return false;
	}
	/**
	 * bind the click events
	 */
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);

	bindUserSelector();

	$("#btn-region-save").click(function(e) {
		if (validateRegionForm()) {
			addRegion("edit-region-form", '#btn-region-save');
		}
	});

	$('#region-name-txt').blur(function() {
		if (validateRegionName(this.id)) {
			hideError();
		}
	});

	bindAdminCheckBoxClick();

	bindSingleMultipleSelection();
	bindAssignToSelectorClick();
	
	bindRegionSelectorEvents();

	$("#btn-office-save").click(function(e) {
		if (validateOfficeForm()) {
			addOffice("edit-office-form", '#btn-office-save');
		}
	});

	$('#office-name-txt').blur(function() {
		if (validateOfficeName(this.id)) {
			hideError();
		}
	});

	bindOfficeSelectorEvents();

	$("#btn-individual-save").click(function(e) {
		if (validateIndividualForm()) {
			addIndividual("edit-individual-form", '#btn-individual-save');
		}
	});
}

function bindSingleMultipleSelection() {
	$('.bd-cust-rad-img').click(function(e) {
		$('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
		$(this).toggleClass('bd-cust-rad-img-checked');
		if ($(this).data('type') == "single") {
			$('#bd-single').show();
			$('#bd-multiple').hide();
			showAdminPrivilegesChk();
		} else if ($(this).data('type') == "multiple") {
			$('#bd-single').hide();
			$('#bd-multiple').show();
			$('#selected-userid-hidden').val("");
			hideAdminPrivilegesChk();
		}
		$('#user-selection-info').attr('data-user-selection-type', $(this).data('type'));
	});
}

function bindUserSelector() {
	/*
	 * $("#selected-user-txt").click(function() { getUsersList("", -1 , -1 ); });
	 */
	/*
	 * $("#selected-user-txt").keydown(function(e) { bindArrowKeysWithSelector(e, "selected-user-txt", "users-droplist", getUsersList, "selected-userid-hidden", "data-userid"); });
	 */
	/*
	 * $("#selected-user-txt").keyup(function(e) { if(e.which != 38 && e.which != 40 && e.which != 13) { var text = $(this).val(); usersStartIndex = 0; if (text.length > 0) { delay(function() { getUsersList(text, -1 , -1); }, 500); } else { delay(function() { getUsersList("", -1 , -1); }, 500); } } });
	 */

	// using autocomplete instead of normal search
	attachAutocompleteUserListDropdown();

}

/**
 * binds the click and keyup of region selector
 */
function bindRegionSelectorEvents() {
	callAjaxGET("/fetchregions.do", function(data) {
		var regionList = [];
		if (data != undefined && data != "")
			regionList = $.parseJSON(data);
		var searchData = [];
		for (var i = 0, j = 0; i < regionList.length; i++) {
			if (regionList[i].isDefaultBySystem == 0) {
				searchData[j] = {};
				searchData[j].label = regionList[i].regionName;
				searchData[j].regionId = regionList[i].regionId;
				j++;
			}
		}
		$("#selected-region-txt").autocomplete({
			source : searchData,
			minLength : 0,
			delay : 0,
			autoFocus : true,
			select : function(event, ui) {
				$("#selected-region-txt").val(ui.item.label);
				$('#selected-region-id-hidden').val(ui.item.regionId);
				return false;
			},
			close : function(event, ui) {
			},
			create : function(event, ui) {
				$('.ui-helper-hidden-accessible').remove();
			}
		}).autocomplete("instance")._renderItem = function(ul, item) {
			return $("<li>").append(item.label).appendTo(ul);
		};
		$("#selected-region-txt").off('focus');
		$("#selected-region-txt").focus(function() {
			$(this).autocomplete('search');
		});
	}, true);
}

/**
 * binds the click and keyup of office selector
 */
function bindOfficeSelectorEvents() {
	callAjaxGET("/fetchbranches.do", function(data) {
		var branchList = [];
		if (data != undefined && data != "")
			branchList = $.parseJSON(data);
		var searchData = [];
		for (var i = 0, j = 0; i < branchList.length; i++) {
			if (branchList[i].isDefaultBySystem == 0) {
				searchData[j] = {};
				searchData[j].label = branchList[i].branchName;
				searchData[j].branchId = branchList[i].branchId;
				searchData[j].regionId = branchList[i].regionId;
				j++;
			}
		}
		$("#selected-office-txt").autocomplete({
			source : searchData,
			minLength : 0,
			delay : 0,
			autoFocus : true,
			select : function(event, ui) {
				$("#selected-office-txt").val(ui.item.label);
				$('#selected-office-id-hidden').val(ui.item.branchId);
				$('#selected-region-id-hidden').val(ui.item.regionId);
				return false;
			},
			close : function(event, ui) {
			},
			create : function(event, ui) {
				$('.ui-helper-hidden-accessible').remove();
			}
		}).autocomplete("instance")._renderItem = function(ul, item) {
			return $("<li>").append(item.label).appendTo(ul);
		};
		$("#selected-office-txt").off('focus');
		$("#selected-office-txt").focus(function() {
			$(this).autocomplete('search');
		});
	}, true);
}

/**
 * binds the click of assign to selector
 */
function bindAssignToSelectorClick() {
	$('#assign-to-selector').click(function(e) {
		e.stopPropagation();
		$("#assign-to-droplist").slideToggle(200);
	});

	$('.hm-assignto-options').click(function(e) {
		e.stopPropagation();
		var assignToOption = $(this).attr('data-assign-to-option');
		$("#assign-to-txt").val($(this).html());
		$("#assign-to-txt").attr("data-assignto", assignToOption);

		showSelectorsByAssignToOption(assignToOption);
		$("#assign-to-droplist").slideToggle(200);
	});
}

/**
 * binds the check and uncheck of admin privileges checkbox
 */
function bindAdminCheckBoxClick() {
	$('.bd-check-img').unbind('click');
	$('.bd-check-img').click(function() {
		/* $(this).toggleClass('bd-check-img-checked'); */
		/**
		 * If class is "bd-check-img-checked", check box is unchecked , hence setting the hidden value as false
		 */
		if ($(this).hasClass('bd-check-img-checked')) {
			$(this).removeClass('bd-check-img-checked');
			$(this).next("#is-admin-chk").val("true");
			$(this).next("#is-soc-mon-admin-chk").val("true");
			$(this).next("#is-ignore").val("true");
		} else {
			$(this).addClass('bd-check-img-checked');
			$(this).next("#is-admin-chk").val("false");
			$(this).next("#is-soc-mon-admin-chk").val("false");
			$(this).next("#is-ignore").val("false");
		}
		
		if ($('#is-soc-mon-admin-chk').val() == "true") {
			$('#user-assignment-cont').hide();
			$('#bd-assign-to').hide();
			$('#bd-region-selector').hide();
			$('#bd-office-selector').hide();
			$('#admin-privilege-div').hide();
		}else{
			$('#user-assignment-cont').show();
			$('#admin-privilege-div').show();
			$('#bd-assign-to').show();
			
			if($(this).attr('type') == 'socialMonitorCheckbox'){
				var assignTo = $("#assign-to-txt").attr("data-assignto");
				if(assignTo == 'office' || assignTo == 'Office'){
					$('#bd-office-selector').show();
				}else if(assignTo == 'region' || assignTo == 'Region'){
					$('#bd-region-selector').show();
				}
				showSelectorsByAssignToOption(assignTo);
			}
			
		}
		
		if ($('#is-ignore').val() == "true") {
			if ($('#match-user-email').val() != "") {
				$('#match-user-email').val('');
				$('#match-user-email').attr('agent-id', 0);
				$('#match-user-email').attr("disabled", true);
			}
		} else if ($('#is-ignore').val() == "false") {

			$('#match-user-email').removeAttr("disabled");
		}

	});
}



/**
 * Method to show/hide the other selectors based on the assign to option selected
 * 
 * @param assignToOption
 */
function showSelectorsByAssignToOption(assignToOption) {
	switch (assignToOption) {
	case 'company':
		disableRegionSelector();
		disableOfficeSelector();
		if ($("#assign-to-selector").data("profile") == "individual")
			hideAdminPrivilegesChk();
		break;
	case 'region':
		$("#selected-region-txt").prop("disabled", false);
		disableOfficeSelector();
		$("#bd-region-selector").show();
		showAdminPrivilegesChk();
		break;
	case 'office':
		$("#selected-office-txt").prop("disabled", false);
		$("#bd-office-selector").show();
		showAdminPrivilegesChk();
		disableRegionSelector();
		break;
	default:
		$("#selected-region-txt").prop("disabled", false);
		$("#selected-office-txt").prop("disabled", false);
	}
}

function showAdminPrivilegesChk() {
	$("#admin-privilege-div").show();
	if (!$('.bd-check-img').hasClass('bd-check-img-checked')) {
		$('.bd-check-img').next("#is-admin-chk").val("true");
		$('.bd-check-img').removeClass('bd-check-img-checked');
	}
}

function hideAdminPrivilegesChk() {
	$("#admin-privilege-div").hide();
	$('.bd-check-img').next("#is-admin-chk").val("false");
	$('.bd-check-img').addClass('bd-check-img-checked');
}

function disableRegionSelector() {
	$("#selected-region-txt").prop("disabled", true);
	$("#selected-region-txt").val("");
	$('#selected-region-id-hidden').val("");
	$("#bd-region-selector").hide();
}

function disableOfficeSelector() {
	$("#selected-office-txt").prop("disabled", true);
	$("#selected-office-txt").val("");
	$('#selected-office-id-hidden').val("");
	// $('#selected-region-id-hidden').val("");
	$("#bd-office-selector").hide();
}

/**
 * Region details validation
 */
var isRegionValid;

/**
 * Function to validate Region name
 */
function validateRegionName(elementId) {
	if ($('#' + elementId).val() != "") {
		if (companyNameRegEx.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			showErrorMobileAndWeb('Please enter a valid region name.');
			return false;
		}
	} else {
		showErrorMobileAndWeb('Please enter region name.');
		return false;
	}
}

function validateUserEmailTextArea(elementId) {
	var emailIds = $('#' + elementId).val();
	if (emailIds != "") {
		var emailIdsArray = emailIds.split(/[,;\n]/);
		for (var i = 0; i < emailIdsArray.length; i++) {
			var emailId = emailIdsArray[i].trim();
			if (emailId == "") {
				continue;
			}
			if (emailId.indexOf(">") > -1) {
				emailId = emailId.substring(emailId.indexOf("<") + 1, emailId.length - 1);
			}

			if (emailRegex.test(emailId) == false) {
				showErrorMobileAndWeb('Please enter valid email addresses');
				return false;
			}
		}
		return true;
	}
}

function validateUserSelection(elementId, hiddenElementId) {
	if ($('#' + elementId).val() != "") {
		var emailId = $('#' + elementId).val();
		if (emailId.indexOf('"') > -1) {
			emailId = emailId.split('"').join("");
		}
		if (emailId.indexOf("<") > -1) {
			emailId = emailId.substring(emailId.indexOf("<") + 1, emailId.indexOf(">"));
		}
		if ($("#" + hiddenElementId).val() != "") {
			return true;
		}

		else if (emailRegex.test(emailId) == true) {
			return true;
		} else {
			showErrorMobileAndWeb('Please select a valid user');
			return false;
		}
	}
	return true;
}

/**
 * function to validate region form
 */
function validateRegionForm() {
	isRegionValid = true;
	var isFocussed = false;

	if (!validateRegionName('region-name-txt')) {
		isRegionValid = false;
		if (!isFocussed) {
			$('#region-name-txt').focus();
			isFocussed = true;
		}
		return isRegionValid;
	}

	var userSelectionType = $('#user-selection-info').attr('data-user-selection-type');
	if (userSelectionType == "single") {

		if (!isFocussed) {
			$('#selected-user-txt').focus();
			isFocussed = true;
		}

	} else {

		if (!isFocussed) {
			$('#selected-user-txt-area').focus();
			isFocussed = true;
		}

	}

	if (isRegionValid) {
		hideError();
	}
	return isRegionValid;
}

/**
 * clear input fields within specified form/div
 */
function resetInputFields(elementId) {
	document.getElementById(elementId).reset();
}

/**
 * js function for adding a region
 */
function addRegion(formId, disableEle) {
	var url = "./addregion.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, addRegionCallBack, formId, disableEle);
}

/**
 * call back function for add region
 * 
 * @param data
 */
function addRegionCallBack(data) {
	hideOverlay();
	displayMessageForRegionAndBranchAddition(data);
	showStateCityRow("region-state-city-row", "region-state-txt", "region-city-txt");
	resetInputFields("edit-region-form");
	$('#region-country').val(defaultCountry);
	$('#region-country-code').val(defaultCountryCode);
	fetchCompleteHierarchy();
}

/**
 * Method to fetch list of already existing users
 * 
 * @param searchKey
 * @param start
 * @param rows
 */
function getUsersList(searchKey, start, rows) {
	var url = "./finduserbyemail.do?startIndex=" + start + "&batchSize=" + rows + "&searchKey=" + searchKey;
	// encode the url so it can accept the special characters also
	callAjaxGET(encodeURI(url), paintUsersList, true);
}

/**
 * Callback for getUsersList, populates the drop down with users list obtained
 * 
 * @param data
 */
function paintUsersList(data) {
	var usersList = $.parseJSON(data);
	var htmlData = "";
	if (usersList != null) {
		var len = usersList.length;
		if (len > 0) {
			$('#selected-userid-hidden').val("");
			$.each(usersList, function(i, user) {
				var displayName = user.firstName;
				if (user.lastName != undefined) {
					displayName = displayName + " " + user.lastName;
				}
				htmlData = htmlData + '<div class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-user-options" data-userid="' + user.userId + '">' + displayName + '</div>';
			});
		}
	}

	if (htmlData != "") {
		$("#users-droplist").html(htmlData).slideDown(200);
	} else {
		$("#users-droplist").slideUp(200);

	}

	$('#users-droplist').perfectScrollbar();
	$('#users-droplist').perfectScrollbar('update');

	// bind the click event of selector
	$(".hm-user-options").click(function() {
		$('#selected-user-txt').val($(this).html());
		$('#selected-userid-hidden').val($(this).data('userid'));
		$('#users-droplist').slideToggle(200);
	});
}

var isOfficeValid;

/**
 * Function to validate office name
 */
function validateOfficeName(elementId) {
	if ($('#' + elementId).val() != "") {
		if (companyNameRegEx.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			showErrorMobileAndWeb('Please enter a valid office name.');
			return false;
		}
	} else {
		showErrorMobileAndWeb('Please enter office name.');
		return false;
	}
}

/**
 * function to validate the region selector
 * 
 * @param hiddenElementId
 * @param textElementId
 * @returns {Boolean}
 */
function validateRegionSelector(hiddenElementId, textElementId) {
	
	if($('#is-soc-mon-admin-chk').val() == true || $('#is-soc-mon-admin-chk').val() == 'true'){
		return true;
	}
	
	var assignToType = $("#assign-to-txt").attr("data-assignto");
	if (assignToType == 'region') {
		if ($('#' + hiddenElementId).val() == "" || $('#' + textElementId).val() == "") {
			showErrorMobileAndWeb('Please select a region');
			return false;
		}
		return true;
	}
	return true;

}

/**
 * function to validate office form
 */
function validateOfficeForm() {
	isOfficeValid = true;
	var isFocussed = false;
	
	if (!validateOfficeName('office-name-txt')) {
		isOfficeValid = false;
		if (!isFocussed) {
			$('#office-name-txt').focus();
			isFocussed = true;
		}
		return isOfficeValid;
	}

	if (!validateRegionSelector('selected-region-txt', 'selected-region-id-hidden')) {
		isOfficeValid = false;
		if (!isFocussed) {
			$('#selected-region-txt').focus();
			isFocussed = true;
		}
		return isOfficeValid;
	}
	if (!validateAddress1('office-address-txt')) {
		isOfficeValid = false;
		if (!isFocussed) {
			$('#office-address-txt').focus();
			isFocussed = true;
		}
		return isOfficeValid;
	}

	var userSelectionType = $('#user-selection-info').attr('data-user-selection-type');
	if (userSelectionType == "single") {
		if (!isFocussed) {
			$('#selected-user-txt').focus();
			isFocussed = true;
		}
	} else {

		if (!isFocussed) {
			$('#selected-user-txt-area').focus();
			isFocussed = true;
		}

	}

	if (isOfficeValid) {
		hideError();
	}
	return isOfficeValid;
}

/**
 * js function for adding a branch
 */
function addOffice(formId, disableEle) {
	var url = "./addbranch.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, addOfficeCallBack, formId, disableEle);
}

/**
 * call back function for add branch
 * 
 * @param data
 */
function addOfficeCallBack(data) {
	hideOverlay();
	displayMessageForRegionAndBranchAddition(data);
	showStateCityRow("office-state-city-row", "office-state-txt", "office-city-txt");
	resetInputFields("edit-office-form");
	$('#office-country').val(defaultCountry);
	$('#office-country-code').val(defaultCountryCode);
	fetchCompleteHierarchy();
}

/**
 * Method to fetch regions from solr for populating region selector
 * 
 * @param regionPattern
 */
function populateRegionsSelector(regionPattern) {
	var url = "./searchregions.do?regionPattern=" + regionPattern + "&start=0&rows=-1";
	callAjaxGET(url, populateRegionsSelectorCallBack, true);
}

/**
 * callback method for fetching regions from solr for populating region selector
 * 
 * @param data
 */
function populateRegionsSelectorCallBack(data) {
	var searchResult = $.parseJSON(data);
	if (searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		if (len > 0) {
			$.each(searchResult, function(i, region) {
				htmlData = htmlData + '<div data-regionId="' + region.regionId + '" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-region-option">' + region.regionName + '</div>';
			});
		}
		if (htmlData != "") {
			$("#regions-droplist").html(htmlData).slideDown(200);
			// bind the click event of selector
			$('.hm-region-option').click(function(e) {
				e.stopPropagation();
				$('#selected-region-txt').val($(this).html());
				$('#selected-region-id-hidden').val($(this).data('regionid'));
				$('#regions-droplist').slideToggle(200);
			});

			// bind the hover event
			$(".hm-dd-hover").hover(function() {
				$(".hm-region-option").removeClass("hm-dd-item-keys-selected");
			});
			$("#selected-region-txt").keydown(function(e) {
				bindArrowKeysWithSelector(e, "selected-region-txt", "regions-droplist", populateRegionsSelector, "selected-region-id-hidden", "data-regionid");
			});
		} else {
			$("#regions-droplist").html(htmlData).slideUp(200);
		}
	}
}

var isIndividualValid;

/**
 * function to validate the office selector
 * 
 * @param hiddenElementId
 * @param textElementId
 * @returns {Boolean}
 */
function validateOfficeSelector(hiddenElementId, textElementId) {
	
	if($('#is-soc-mon-admin-chk').val() == true || $('#is-soc-mon-admin-chk').val() == 'true'){
		return true;
	}
	
	var assignToType = $("#assign-to-txt").attr("data-assignto");
	if (assignToType == 'office') {
		if ($('#' + hiddenElementId).val() == "" || $('#' + textElementId).val() == "") {
			showErrorMobileAndWeb('Please select an office');
			return false;
		}
		return true;
	}
	return true;
}

/**
 * function to validate user selection in case of individual addition
 * 
 * @param elementId
 * @returns {Boolean}
 */
function validateIndividualSelection(elementId) {
	if ($('#' + elementId).val() == "") {
		showErrorMobileAndWeb('Please select a user or enter atleast one email address');
		return false;
	}
	return true;
}

/**
 * function to validate the individual form
 * 
 * @returns {Boolean}
 */
function validateIndividualForm() {
	isIndividualValid = true;
	var isFocussed = false;
	
	if (!validateRegionSelector('selected-region-txt', 'selected-region-id-hidden')) {
		isIndividualValid = false;
		if (!isFocussed) {
			$('#selected-region-txt').focus();
			isFocussed = true;
		}
	}

	if (!validateOfficeSelector('selected-office-txt', 'selected-office-id-hidden')) {
		isIndividualValid = false;
		if (!isFocussed) {
			$('#selected-office-txt').focus();
			isFocussed = true;
		}
	}

	var userSelectionType = $('#user-selection-info').attr('data-user-selection-type');
	if (userSelectionType == "single") {
		if (!validateIndividualSelection('selected-user-txt')) {
			isIndividualValid = false;
			if (!isFocussed) {
				$('#selected-user-txt').focus();
				isFocussed = true;
			}
		}
		if (!isFocussed) {
			$('#selected-user-txt').focus();
			isFocussed = true;
		}
	} else {
		if (!validateIndividualSelection('selected-user-txt-area')) {
			isIndividualValid = false;
			if (!isFocussed) {
				$('#selected-user-txt-area').focus();
				isFocussed = true;
			}
		}

		if (!isFocussed) {
			$('#selected-user-txt-area').focus();
			isFocussed = true;
		}

	}

	if (isIndividualValid) {
		hideError();
	}
	return isIndividualValid;
}

function addIndividual(formId, disableEle) {
	var url = "./addindividual.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, addIndividualCallBack, formId, disableEle);
}

function addIndividualCallBack(data) {
	hideOverlay();
	displayMessage(data);
	resetInputFields("edit-individual-form");
	fetchCompleteHierarchy();
}

/**
 * Method to fetch offices(branches) from solr for populating offices selector
 * 
 * @param officePattern
 */
function populateOfficesSelector(officePattern) {
	var url = "./searchbranches.do?branchPattern=" + officePattern + "&start=0&rows=-1";
	callAjaxGET(url, populateOfficesSelectorCallBack, true);
}

/**
 * callback method for fetching offices(branches) from solr for populating office selector
 * 
 * @param data
 */
function populateOfficesSelectorCallBack(data) {
	var searchResult = $.parseJSON(data);
	if (searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		if (len > 0) {
			$.each(searchResult, function(i, branch) {
				htmlData = htmlData + '<div data-regionid="' + branch.regionId + '" data-officeid="' + branch.branchId + '" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-office-option">' + branch.branchName + '</div>';
			});
		}
		if (htmlData != "") {
			$("#offices-droplist").html(htmlData).slideDown(200);

			// bind the click event of selector
			$('.hm-office-option').click(function(e) {
				e.stopPropagation();
				$('#selected-office-txt').val($(this).html());
				$('#selected-region-id-hidden').val($(this).data('regionid'));
				$('#selected-office-id-hidden').val($(this).data('officeid'));
				$('#offices-droplist').slideToggle(200);
			});

			// bind the hover event
			$(".hm-dd-hover").hover(function() {
				$(".hm-office-option").removeClass("hm-dd-item-keys-selected");
			});
		} else {
			$("#offices-droplist").html(htmlData).slideUp(200);
		}
	}
}

function bindArrowKeysWithSelector(e, textBoxId, dropListId, populatorFunction, hiddenFieldId, attrName) {
	if (e.which == 40) {
		var text = $("#" + textBoxId).val();
		if (text == undefined) {
			text = "";
		}
		if (!($("#" + dropListId).css("display") == "block")) {
			delay(function() {
				populatorFunction(text);
			}, 500);
		} else {
			var current = $("#" + dropListId).find(".hm-dd-item-keys-selected");
			if (current.length > 0) {
				$(current).removeClass("hm-dd-item-keys-selected");
				$(current).next().addClass("hm-dd-item-keys-selected");
			} else {
				$("#" + dropListId + " :first-child").addClass("hm-dd-item-keys-selected");
			}
			$("#" + dropListId).show();
		}

	} else if (e.which == 38) {
		var current = $("#" + dropListId).find(".hm-dd-item-keys-selected");
		if (current.length > 0) {
			$(current).removeClass("hm-dd-item-keys-selected");
			$(current).prev().addClass("hm-dd-item-keys-selected");
		} else {
			$('#' + dropListId).slideUp(200);
		}
	} else if (e.which == 13) {
		var selectedItem = $("#" + dropListId).find(".hm-dd-item-keys-selected");
		if (selectedItem.length == 0) {
			selectedItem = $("#" + dropListId + " :first-child");
		}
		$('#' + textBoxId).val($(selectedItem).html());
		$('#' + hiddenFieldId).val($(selectedItem).attr(attrName));
		$('#' + dropListId).slideToggle(200);
	}
}

function showViewHierarchyPage() {
	closeMoblieScreenMenu();
	var url = "./viewhierarchy.do";
	callAjaxGET(url, viewHierarchyCallBack, true);
}

function viewHierarchyCallBack(data) {
	$("#main-content").html(data);
}

function fetchHierarchyViewBranches(regionId) {
	var url = "./fetchhierarchyviewbranches.do?regionId=" + regionId;
	callAjaxGET(url, function(data) {
		paintHierarchyViewBranches(data, regionId);
	}, true);
}

function paintHierarchyViewBranches(data, regionId) {
	$("#td-region-edit-" + regionId).parent(".tr-region-edit").after(data);
	$("#tr-region-" + regionId).slideDown(200);
	$(".tr-region-edit").slideUp(200);
	bindUserEditClicks();
	bindBranchListClicks();
	bindHierarchyEvents();
	bindAppUserLoginEvent();
}

function bindBranchListClicks() {
	$(".branch-edit-icn").unbind('click');
	$(".branch-edit-icn").click(function(e) {
		e.stopPropagation();
		$('.v-hr-tbl-icn-wraper').hide();
		var branchId = $(this).attr("data-branchid");
		if ($(this).attr('clicked') == "false") {
			showBranchEdit(branchId);
			$(this).attr('clicked', 'true');
		} else {
			hideBranchEdit(branchId);
			$(this).attr('clicked', 'false');
		}
	});
	$(".branch-row").unbind('click');
	$(".branch-row").click(function(e) {
		// e.stopPropagation();
		var branchId = $(this).attr("data-branchid");
		var regionId = $(this).attr("data-regionid");
		if ($(this).attr('clicked') == "false") {
			fetchUsersForBranch(branchId, regionId);
			$(this).attr('clicked', 'true');
		} else {
			$('.user-row-' + branchId).html("").hide();
			$(this).attr('clicked', 'false');
		}
	});
	$(".branch-del-icn").unbind('click');
	$(".branch-del-icn").click(function(e) {
		e.stopPropagation();
		$('.v-hr-tbl-icn-wraper').hide();
		var branchId = $(this).attr("data-branchid");
		deleteBranchPopup(branchId);
	});
}

function fetchHierarchyViewList() {
	var url = "./fetchhierarchyviewlist.do";
	callAjaxGET(url, function(data) {
		$("#hierarchy-list-header").siblings().remove();
		$("#hierarchy-list-header").after(data);
		bindRegionListClicks();
		/*
		 * $('.v-tbl-icn').click(function(e){ e.stopPropagation(); });
		 */
		bindBranchListClicks();
		bindUserEditClicks();
		bindHierarchyEvents();
		bindAppUserLoginEvent();
	}, true);
}

function bindRegionListClicks() {
	$(".region-row").click(function(e) {
		var regionId = $(this).attr("data-regionid");
		if ($(this).attr('clicked') == "false") {
			fetchHierarchyViewBranches(regionId);
			$(this).attr('clicked', 'true');
		} else {
			$("tr[class*='sel-r" + regionId + "']").html("").hide();
			$(this).attr('clicked', 'false');

		}
	});
	$(".region-edit-icn").click(function(e) {
		e.stopPropagation();
		$('.v-hr-tbl-icn-wraper').hide();
		var regionId = $(this).attr("data-regionid");
		if ($(this).attr('clicked') == "false") {
			showRegionEdit(regionId);
			$(this).attr('clicked', 'true');
		} else {
			hideRegionEdit(regionId);
			$(this).attr('clicked', 'false');
		}
	});
	$(".region-del-icn").unbind('click');
	$(".region-del-icn").click(function(e) {
		e.stopPropagation();
		$('.v-hr-tbl-icn-wraper').hide();
		var regionId = $(this).attr("data-regionid");
		deleteRegionPopup(regionId);
	});
}

function bindHierarchyEvents() {
	$('.v-tbn-icn-dropdown').off('click');
	$('.v-tbn-icn-dropdown').on('click', function(e) {
		e.stopPropagation();
		var element = $(this);
		if (element.next('.v-hr-tbl-icn-wraper').is(':visible')) {
			$(this).next('.v-hr-tbl-icn-wraper').hide();
		} else {
			$('.v-hr-tbl-icn-wraper').hide();
			$(this).next('.v-hr-tbl-icn-wraper').show();
		}
	});
	$('.v-icn-wid.v-tbl-icn-sm').off('click');
	$('.v-icn-wid.v-tbl-icn-sm').on('click', function(e) {
		e.stopPropagation();
		var element = $(this);
		generateWidget(element, element.data('iden'), element.data('profile'));
	});
	$('.v-icn-femail').off('click');
	$('.v-icn-femail').on('click', function(e) {
		e.stopPropagation();
		$('.v-hr-tbl-icn-wraper').hide();
		if ($(this).hasClass('v-tbl-icn-disabled')) {
			return;
		}

		var firstName = $(this).parent().parent().parent().find('.v-tbl-name').html();
		var lastName = $(this).parent().parent().parent().find('.v-tbl-name').html();
		var emailId = $(this).parent().parent().parent().find('.v-tbl-add').html();
		reinviteUser(firstName, lastName, emailId, '.v-icn-femail');
	});
}

function showRegionEdit(regionId) {
	var url = "./getregioneditpage.do?regionId=" + regionId;
	callAjaxGET(url, function(data) {
		showRegionEditCallBack(data, regionId);
	}, true);
}
function showRegionEditCallBack(data, regionId) {
	$(".td-region-edit").html("").hide();
	$(".tr-region-edit").hide();
	$("#td-region-edit-" + regionId).parent(".tr-region-edit").slideDown(200);
	$("#td-region-edit-" + regionId).html(data).slideDown(200);
	bindSingleMultipleSelection();
	bindAdminCheckBoxClick();
	bindUserSelector();
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);
	bindAssignToSelectorClick();
	$("#btn-region-update").click(function(e) {
		var regionId = $(this).attr("data-regionid");
		if (validateRegionForm()) {
			updateRegion("edit-region-form", regionId);
		}
	});

}

function hideRegionEdit(regionId) {
	$(".td-region-edit").html("").hide();
	$("#td-region-edit-" + regionId).hide();
	$(".tr-region-edit").hide();
}

function showBranchEdit(branchId) {
	var url = "./getofficeeditpage.do?branchId=" + branchId;
	callAjaxGET(url, function(data) {
		$('.td-branch-edit').parent().hide();
		$('.td-branch-edit').html('');
		showBranchEditCallBack(data, branchId);
	}, true);
}

function showBranchEditCallBack(data, branchId) {
	$("#td-branch-edit-" + branchId).parent(".tr-branch-edit").slideDown(200);
	$("#td-branch-edit-" + branchId).html(data).slideDown(200);
	bindSingleMultipleSelection();
	bindUserSelector();
	bindRegionSelectorEvents();
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);
	bindAssignToSelectorClick();
	$("#btn-office-update").click(function(e) {
		updateBranch("edit-office-form", branchId);
	});
}

function hideBranchEdit(branchId) {
	$("#td-branch-edit-" + branchId).slideUp(200);
	$("#td-branch-edit-" + branchId).parent(".tr-branch-edit").hide();
}

function fetchUsersForBranch(branchId, regionId) {
	var url = "./fetchbranchusers.do?branchId=" + branchId + "&regionId=" + regionId;
	callAjaxGET(url, function(data) {
		paintUsersFromBranch(data, branchId);
	}, true);
}

function paintUsersFromBranch(data, branchId, regionId) {
	$("#td-branch-edit-" + branchId).parent(".tr-branch-edit").after(data);
	$("#tr-branch-" + branchId).slideDown(200);
	$(".tr-branch-edit").slideUp(200);
	bindUserEditClicks();
	bindHierarchyEvents();
	bindAppUserLoginEvent();
}

function bindUserEditClicks() {
	$(".user-edit-icn").unbind('click');
	$('.user-edit-icn').click(function(e) {
		e.stopPropagation();
		$('.v-hr-tbl-icn-wraper').hide();
		if ($(this).attr('clicked') == "false") {
			// make an ajax call and fetch the details of the user
			var userId = $(this).attr('data-userid');
			$(".user-assignment-edit-div").html("");
			$(".user-edit-row").slideUp();
			var elementToAppendTo = $(this).closest('tr').next('tr.user-edit-row').find('td.td-user-edit');
			getUserAssignments(userId, elementToAppendTo);
			$(this).parent().parent().parent().next('.user-edit-row').slideDown(200);
			$(this).attr('clicked', 'true');
		} else {
			$(this).parent().parent().parent().next('.user-edit-row').slideUp(200);
			$(".user-assignment-edit-div").html("");
			$(".user-edit-row").slideUp();
			$(this).attr('clicked', 'false');
		}
	});
	$(".user-del-icn").unbind('click');
	$(".user-del-icn").click(function(e) {
		e.stopPropagation();
		var userId = $(this).attr("data-userid");
		$('.v-hr-tbl-icn-wraper').hide();
		confirmDeleteUser(userId);
	});
}

function updateRegion(formId, regionId) {
	var url = "./updateregion.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, function(data) {
		updateRegionCallBack(data, regionId);
	}, formId);
}

function updateRegionCallBack(data, regionId) {
	hideOverlay();
	displayMessage(data);
	hideRegionEdit(regionId);
	fetchHierarchyViewList();
}

function updateBranch(formId, branchId) {
	if (validateBranchForm()) {
		var url = "./updatebranch.do";
		var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
		$('input[name="userSelectionType"]').val(selectedType);
		callAjaxFormSubmit(url, function(data) {
			updateBranchCallBack(data, branchId);
		}, formId);
	}
}

function validateBranchForm() {
	// check for region dropdown open
	if ($('#selected-region-txt').is(':visible')) {
		if ($('#selected-region-txt').val() == undefined || $('#selected-region-txt').val().trim() == "") {
			$('#selected-region-txt').focus();
			showErrorMobileAndWeb("Please enter region name");
			return false;
		}
		if ($('#selected-region-id-hidden').val() == undefined || isNaN(parseInt($('#selected-region-id-hidden').val()))) {
			$('#selected-region-txt').focus();
			showErrorMobileAndWeb("Please enter region name");
			return false;
		}
	}
	return true;
}

function updateBranchCallBack(data, branchId) {
	hideOverlay();
	displayMessage(data);
	hideBranchEdit(branchId);
	fetchHierarchyViewList();
}

/**
 * Region Delete popup overlay
 * 
 * @param regionId
 */
function deleteRegionPopup(regionId) {
	var urlCheck = "./checkbranchesinregion.do?regionId=" + regionId;
	callAjaxPOST(urlCheck, function(response) {
		deleteRegionCheckCallBack(response, regionId);
	}, true);
}
function deleteRegionCheckCallBack(response, regionId) {
	$('#overlay-text').html(response);
	$('.msg-err-icn').remove();

	var success = "Selected Region could be deleted";
	var successMsg = $("#overlay-text").find('.success-message').text().trim();
	if (success == successMsg) {
		createPopupConfirm("Remove Region");

		$('#overlay-continue').click(function() {
			if ($('#overlay-continue').attr("disabled") != "disabled") {
				if (regionId != null) {
					overlayRevert();
					deleteRegion(regionId);
					regionId = null;
				}
				$('#overlay-continue').unbind('click');
			}
		});
	} else {
		createPopupInfo("Remove Region");
		regionId = null;
	}
}

/**
 * Branch Delete popup overlay
 * 
 * @param branchId
 */
function deleteBranchPopup(branchId) {
	var urlCheck = "./checkusersinbranch.do?branchId=" + branchId;
	callAjaxPOST(urlCheck, function(response) {
		deleteBranchCheckCallBack(response, branchId);
	}, true);
}
function deleteBranchCheckCallBack(response, branchId) {
	$("#overlay-text").html(response);
	$('.msg-err-icn').remove();
	var success = "Selected Office could be deleted";
	var successMsg = $("#overlay-text").find('.success-message').text().trim();
	if (success == successMsg) {
		createPopupConfirm("Remove Office");

		$('#overlay-continue').click(function() {
			if ($('#overlay-continue').attr("disabled") != "disabled") {
				if (branchId != null) {
					overlayRevert();
					deleteBranch(branchId);
					branchId = null;
				}
				$('#overlay-continue').unbind('click');
			}
		});
	} else {
		createPopupInfo("Remove Office");
		branchId = null;
	}
}

// Pop-up Overlay modifications
$('#overlay-cancel').click(function() {
	$('#overlay-continue').unbind('click');
	overlayRevert();
	branchId = null;
	regionId = null;
});
function createPopupConfirm(header) {
	$('#overlay-header').html(header);
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$('#overlay-continue').removeClass("btn-disabled");
	$('#overlay-continue').html('Continue');
	$('#overlay-cancel').html('Cancel');

	$('#overlay-main').show();
}

function createPopupInfo(header) {
	$('#overlay-header').html(header);
	$('#overlay-continue').attr("disabled", true);
	$('#overlay-continue').addClass("btn-disabled");
	$('#overlay-continue').html('Continue');
	$('#overlay-cancel').html('Cancel');

	$('#overlay-main').show();
}
/*
 * function overlayRevert() { $('#overlay-main').hide(); if ($('#overlay-continue').attr("disabled") == "disabled") { $('#overlay-continue').removeAttr("disabled"); } $("#overlay-header").html(''); $("#overlay-text").html(''); $('#overlay-continue').html(''); $('#overlay-cancel').html(''); }
 */

/**
 * Function to delete a region
 * 
 * @param branchId
 */
function deleteRegion(regionId) {
	var url = "./deactivateregion.do?regionId=" + regionId;
	callAjaxPOST(url, function(data) {
		deleteRegionCallBack(data, regionId);
	}, true);
}

/**
 * Call back function for deleting a region
 * 
 * @param data
 */
function deleteRegionCallBack(data, regionId) {
	displayMessage(data);
	$("#tr-region-" + regionId).hide();
	$("#tr-region-" + regionId).next(".tr-region-edit").hide();
}

/**
 * Function to delete a branch
 * 
 * @param branchId
 */
function deleteBranch(branchId) {
	var url = "./deactivatebranch.do?branchId=" + branchId;
	callAjaxPOST(url, function(data) {
		deleteBranchCallBack(data, branchId);
	}, true);
}

/**
 * Call back function for deleting a branch
 * 
 * @param data
 */
function deleteBranchCallBack(data, branchId) {
	displayMessage(data);
	$("#tr-branch-row-" + branchId).hide();
	$("#tr-branch-row-" + branchId).next(".tr-branch-edit").hide();
}

function resendVerificationMail() {
	$.ajax({
		url : "./sendverificationmail.do",
		type : "GET",
		cache : false,
		dataType : "text",
		success : function(data) {
			if (data.errCode == undefined) {
				$('#overlay-toast').html(data);
				showToast();
				hideError();
				hideInfo();
			} else {
				$('#overlay-toast').html(data);
				showToast();
				hideError();
				hideInfo();
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

/*
 * Functions for settings page
 */
// Encompass
function saveEncompassDetails(formid) {
	if (validateEncompassInput(formid)) {
		var url = "./saveencompassdetails.do";
		callAjaxFormSubmit(url, testConnectionSaveCallBack, formid);
	}
}

//ftp
function saveFtpDetails(formid) {
	if (validateFtpInput(formid)) {
		var url = "./saveftpdetails.do";
		callAjaxFormSubmit(url, testConnectionSaveCallBack, formid);
	}
}

function saveLoneWolfDetails(formid , warn) {
	if (validateLoneWolfInput(formid)) {
		var lonewolfClientId = $("#lone-client").val();
		var loeWolfState = $("#lone-state").val();
		var transactionStartDate = $("#lone-transaction-start-date").val();
		
		disableIcon = false;
		var formData = new FormData();
		formData.append("lonewolfClient", lonewolfClientId);
		formData.append("lonewolfState", loeWolfState); 
		formData.append("transactionStartDate", transactionStartDate); 
		formData.append("classifications", JSON.stringify(classificationsList));
		
		showOverlay();
		callAjaxPOSTWithTextDataUpload("./savelonewolfdetails.do" , saveLoneWolfCallBack, true, formData);
		if (warn) {
			$('#overlay-cancel').click();
		}
	}
}

function saveEncompassDetailsCallBack(response) {

	var map = $.parseJSON(response);
	if (map.status == true) {
		saveEncompassDetails("encompass-form");
	} else {
		showError(map.message);
	}
	/*
	 * $("#overlay-toast").html(response); showToast();
	 */

}
function saveTestLoneDetailsCallBack(response) {
	var map = $.parseJSON(response);
	classificationsList = map.classifications;
	var savedClassificationsByCode = map.savedClassificationsByCode;
	
	if (map.status == true) {
		// show classification list
		for (var i = 0; i < classificationsList.length; i++) {
		    var classification = classificationsList[i];
		    var $classificationTypeBuyer = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="B" class="margin-right-o float-left bd-cust-rad-img"></div><div class="float-left bd-cust-rad-txt">Buyer</div></div>';
			var $classificationTypeSeller = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="S" class="margin-right-o float-left bd-cust-rad-img"></div><div class="float-left bd-cust-rad-txt">Seller</div></div>';
			var $classificationTypeBoth = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="SB" class="margin-right-o float-left bd-cust-rad-img"></div><div class="float-left bd-cust-rad-txt">Both</div></div>';
			var $classificationTypeNone = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="N" class="margin-right-o float-left bd-cust-rad-img"></div><div class="float-left bd-cust-rad-txt">None</div></div>';
		    if(savedClassificationsByCode != null && savedClassificationsByCode != undefined && savedClassificationsByCode[classification.Code] != null){
		    	classification.loneWolfTransactionParticipantsType = savedClassificationsByCode[classification.Code];
		    	if(classification.loneWolfTransactionParticipantsType == "B"){
		    		$classificationTypeBuyer = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="B" class="margin-right-o float-left bd-cust-rad-img bd-cust-rad-img-checked"></div><div class="float-left bd-cust-rad-txt">Buyer</div></div>';
		    	}else if(classification.loneWolfTransactionParticipantsType == "S"){
		    		$classificationTypeSeller = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="S" class="margin-right-o float-left bd-cust-rad-img bd-cust-rad-img-checked"></div><div class="float-left bd-cust-rad-txt">Seller</div></div>';
		    	}else if(classification.loneWolfTransactionParticipantsType == "SB"){
		    		$classificationTypeBoth = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="SB" class="margin-right-o float-left bd-cust-rad-img bd-cust-rad-img-checked"></div><div class="float-left bd-cust-rad-txt">Both</div></div>';
		    	}else{
		    		$classificationTypeNone = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="N" class="margin-right-o float-left bd-cust-rad-img bd-cust-rad-img-checked"></div><div class="float-left bd-cust-rad-txt">None</div></div>';
		    	}
		    }else{
		    	classification.loneWolfTransactionParticipantsType = "N";
		    	$classificationTypeNone = '<div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix"><div data-type="N" class="margin-right-o float-left bd-cust-rad-img bd-cust-rad-img-checked"></div><div class="float-left bd-cust-rad-txt">None</div></div>';
		    }
		    classificationsList[i] = classification;
		    
			var $classificationCode = '<div class="float-left opacity-red sq-smile-icn-text clasfction-code-txt compl-sq-smile-sad-text-disabled">' + classification.Code + ' - ' + classification.Name + '</div>';

		    
		    var $classificationRow = $("<div>", {id: "classification_" + i  , "class": "bd-frm-rad-wrapper clearfix"}).attr( "index" , i);
		    $classificationRow.html($classificationCode + $classificationTypeBuyer + $classificationTypeSeller + $classificationTypeBoth + $classificationTypeNone );
		    
		    $("#classification-list-wrapper").append($classificationRow);
		}
		
		bindClickToClassificationTypeButton();
		$("#lone-data-save").show();
		$("#lone-get-classification").hide();
		$("#lone-disconnect").hide();
		$("#lone-dry-enable").hide();
		$("#lone-dry-cancel").show();
		
		$("#lone-test-connection").hide();
		$("#lone-generate-report").hide();
				
		$("#classification-div").show();
		
		if($("#lone-state").val() == 'prod'){
			$("#lone-transaction-start-date").prop("disabled", true);		
		}else{
			$("#lone-transaction-start-date").prop("disabled", false);		
		}
		$("#transaction-start-div").show();
		if( $("#lone-transaction-start-date").val() == "" ){
			$("#lone-transaction-start-date").val(formatDate(((String)($("#transaction-start-date").val())),"/","MDY"));
		}		
		showInfo(map.message);
	} else {
		showError(map.message);
	}
}

function formatDate(date,seperator,order){
	if( date != "" && date != undefined ){
		var dateString = (String)(date);
		var year = dateString.slice(-4),
		    month = ['Jan','Feb','Mar','Apr','May','Jun',
		             'Jul','Aug','Sep','Oct','Nov','Dec'].indexOf(dateString.substr(4,3))+1,
		    day = dateString.substr(8,2);
		if(order == "MDY")
			return ((month<10?'0':'') + month + seperator + day + seperator + year);
		else if(order == "DMY")
			return ( day + seperator + (month<10?'0':'') + month + seperator + year);
		else if(order == "YDM")
			return (year + seperator + day + seperator + (month<10?'0':'') + month);
		else 
			return "please specifiy date order as MDY or DMY or YDM"
	}
	else {
		return "";
	}
}

function bindClickToClassificationTypeButton(){
	$('.bd-cust-rad-img').click(function(e) {
		$(this).parent().parent().find('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
		$(this).toggleClass('bd-cust-rad-img-checked');
		// update type in row
		$(this).parent().parent().attr('data-type', $(this).data('type'));
		var curIndex = $(this).parent().parent().attr('index');
		// update classification list
		( classificationsList[curIndex]).loneWolfTransactionParticipantsType = $(this).data('type');
	});

	
}

function testConnectionSaveCallBack(response) {
	var map = $.parseJSON(response);
	if (map.status == true) {
		// If state = prod/ state = dryrun, don't make any changes
		// else state = dryrun
		var state = $("#encompass-state").val();
		if (state != 'dryrun' && state != 'prod') {
			$("#encompass-state").val('dryrun');
			showEncompassButtons();
		}
		showInfo(map.message);
	} else {
		showError(map.message);
	}
};
function saveLoneWolfCallBack(response) {
	var map = $.parseJSON(response);
	if (map.status == true) {
		// If state = prod/ state = dryrun, don't make any changes
		// else state = dryrun
		var state = $("#lone-state").val();
		if (state != 'dryrun' && state != 'prod') {
			$("#lone-state").val('dryrun');
		}
		
		showLoneWolfButtons();
		
		$("#lone-test-connection").show();
		$("#lone-data-save").hide();
		$("#lone-dry-cancel").hide();
		$("#lone-get-classification").show();
		$("#classification-list-wrapper").html('');
		$("#classification-div").hide();
		$("#transaction-start-div").hide();
		showInfo(map.message);
	} else {
		showError(map.message);
	}
};

function testEncompassConnectionCallBack(response) {
	var map = $.parseJSON(response);
	if (map.status == true) {
		showInfo(map.message);
	} else {
		showError(map.message);
	}

}
function testLoneConnectionCallBack(response) {
	var map = $.parseJSON(response);
	if (map.status == true) {
		showInfo(map.message);
	} else {
		showError(map.message);
	}

}

var isEncompassValid;
function validateEncompassInput(elementId) {
    console.log("IN validate encompass")
	isEncompassValid = true;
	var isFocussed = false;

	if (!validateEncompassUserName('encompass-username')) {
		isEncompassValid = false;
		if (!isFocussed) {
			$('#encompass-username').focus();
			isFocussed = true;
		}
	}
	if (!validateEncompassPassword('encompass-password')) {
		isEncompassValid = false;
		if (!isFocussed) {
			$('#encompass-password').focus();
			isFocussed = true;
		}
	}
	if (!validateURL('encompass-url')) {
		isEncompassValid = false;
		if (!isFocussed) {
			$('#encompass-url').focus();
			isFocussed = true;
		}
	}
    if (!validateAlertEmail('alert-email')) {
            console.log("IN validate alertmail")
    		isEncompassValid = false;
    		if (!isFocussed) {
    			$('#alert-email').focus();
    			isFocussed = true;
    		}
    }
	return isEncompassValid;
}

//validate ftp 
var isFtpValid;
function validateFtpInput(elementId) {
	isFtpValid = true;
	var isFocussed = false;

	if (!validateFtpUserName('ftp-username')) {
		isFtpValid = false;
		if (!isFocussed) {
			$('#ftp-username').focus();
			isFocussed = true;
		}
	}
	if (!validateFtpPassword('ftp-password')) {
		isFtpValid = false;
		if (!isFocussed) {
			$('#ftp-password').focus();
			isFocussed = true;
		}
	}
	if (!validateURL('ftp-url')) {
		isFtpValid = false;
		if (!isFocussed) {
			$('#ftp-url').focus();
			isFocussed = true;
		}
	}

	return isFtpValid;
}

// Check for encompass input fields for testConnection (except fieldid)
function validateEncompassTestInput(elementId) {
	isEncompassValid = true;
	var isFocussed = false;

	if (!validateEncompassUserName('encompass-username')) {
		isEncompassValid = false;
		if (!isFocussed) {
			$('#encompass-username').focus();
			isFocussed = true;
		}
	}
	if (!validateEncompassPassword('encompass-password')) {
		isEncompassValid = false;
		if (!isFocussed) {
			$('#encompass-password').focus();
			isFocussed = true;
		}
	}
	if (!validateURL('encompass-url')) {
		isEncompassValid = false;
		if (!isFocussed) {
			$('#encompass-url').focus();
			isFocussed = true;
		}
	}

	return isEncompassValid;
}
var isLoneValid;
function validateLoneWolfInput(elementId) {
	isLoneValid = true;
	var isFocussed = false;

	if (!validateLoneWolf('lone-client')) {
		isLoneValid = false;
		if (!isFocussed) {
			$('#lone-client').focus();
			isFocussed = true;
		}
	}

	return isLoneValid;
}

// validate dotloop form
function validateDotloopInput() {

	if (!validateDotloopKey('encompass-apikey')) {
		$('#encompass-username').focus();
		return false;
	}
	return true;
}

// app settings event binding

$('body').on('click', function() {
	$('.crm-settings-dropdown-cont').slideUp(200);
});
$('body').on('click', '.crm-settings-dropdown', function(e) {
	e.stopPropagation();
	$('.crm-settings-dropdown-cont').slideToggle(200);
});
$('body').on('click', '.crm-settings-dropdown-item', function(e) {
	var crmType = $(this).attr('data-crm-type');
	$('#crm-settings-dropdown-sel-text').text(crmType);
	$('.crm-setting-cont').hide();
	$('.hm-item-err-2').hide();
	$('.crm-setting-cont[data-crm-type="' + crmType + '"]').show();
});

$('body').on('blur', '#encompass-username', function() {
	validateEncompassUserName(this.id);
});
$('body').on('blur', '#encompass-password', function() {
	validateEncompassPassword(this.id);
});
$('body').on('blur', '#encompass-url', function() {
	validateURL(this.id);
});
$('body').on('blur', '#alert-email', function() {
	validateAlertEmail(this.id);
});
// Lone Wolf input

$('body').on('blur', '#lone-client', function() {
	validateLoneWolf(this.id);
});

$('#dotloop-apikey').blur(function() {
	validateDotloopKey(this.id);
});
$('body').on('click', '#dotloop-save', function() {
	if (validateDotloopInput()) {
		showOverlay();
		saveDotloopDetails("dotloop-form", '#dotloop-save');
	}
});
$('body').on('click', '#dotloop-testconnection', function() {
	if (validateDotloopInput()) {
		testDotloopConnection("dotloop-form", '#dotloop-testconnection');
	}
});

// Dotloop function
function saveDotloopDetails(formid, disableEle) {
	if (validateDotloopInput()) {
		var url = "./savedotloopdetails.do";
		callAjaxFormSubmit(url, function(response) {
			hideOverlay();
			$("#overlay-toast").html(response);
			showToast();
		}, formid, disableEle);
	}
}

function testDotloopConnection(formid, disableEle) {
	if (validateDotloopInput(formid)) {
		var url = "./testdotloopconnection.do";
		callAjaxFormSubmit(url, function(response) {
			$("#overlay-toast").html(response);
			showToast();
		}, formid, disableEle);
	}
}

// Function to validate the api key
function validateDotloopKey(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#overlay-toast').html('Please enter valid api key');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#' + elementId).next('.hm-item-err-2').html('Please enter valid api key');
			$('#' + elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}

// Mail content
function updateMailContent(formid, disableEle) {
	var url = "./savesurveyparticipationmail.do";
	callAjaxFormSubmit(url, updateMailContentCallBack, formid, disableEle);
}

function updateMailContentCallBack(response) {
	$("#overlay-toast").html(response);
	showToast();
}

// Mail Reminder
function autoAppendReminderDropdown(reminderId, reminderDefault) {
	autoAppendDropdown(reminderId, 15, 1);
}

function updateReminderSettings(payload) {
	var url = "./updatesurveyremindersettings.do";
	callAjaxPostWithPayloadData(url, updateReminderSettingsCallBack, payload);
}

function updateReminderSettingsCallBack(response) {
	$("#overlay-toast").html(response);
	showToast();
}

// Ratings Settings
function autoAppendRatingDropdown(ratingId, classes) {
	autoAppendDropdown(ratingId, classes, 5, 0.5);
}

function autoAppendSurveyMailDropdown(ratingId, classes) {
	autoAppendDropdown(ratingId, classes, 5, 0);
}

// Ratings Settings
function autoAppendRatingDropdownComplaint(ratingId, classes, maxPoint, minPoint, diff) {
	var value = diff;
	while (maxPoint >= minPoint) {
		$(ratingId).append($('<div/>').addClass(classes).text(maxPoint));
		maxPoint -= diff;
	}
}

function updatePostScore(formid) {
	var url = "./updatesurveysettings.do";
	callAjaxFormSubmit(url, updatePostScoreCallBack, formid);
}
function updatePostScoreCallBack(response) {
	$('#ratingcategory').val('');
	$("#overlay-toast").html(response);
	showToast();
}

// Other settings
function autoSetCheckboxStatus(on, off, status) {
	if ($(status).val() == 'true') {
		$(on).show();
		$(off).hide();
	} else if ($(status).val() == 'false') {
		$(on).hide();
		$(off).show();
	}
}

function updateOtherSettings(formid) {
	var url = "./updateothersettings.do";
	callAjaxFormSubmit(url, updateOtherSettingsCallBack, formid);
}
function updateOtherSettingsCallBack(response) {
	$('#othercategory').val('');
	$("#overlay-toast").html(response);
	showToast();
}

// Generic functions
// NOTE: minVal value below 0.5 will not work
function autoAppendDropdown(elementId, classes, maxVal, minVal) {
	var value = 0;
	var zeroMinVal = false;
	
	if( minVal == 0 ){	
		minVal = 0.5;
		zeroMinVal = true;
	}
	
	while (maxVal - value >= minVal) {
		$(elementId).append($('<div/>').addClass(classes).text(maxVal - value));
		value += minVal;
	}
	
	if( zeroMinVal ){
		$(elementId).append($('<div/>').addClass(classes).text("0"));
	}
}

function autoSetReminderIntervalStatus() {
	if ($('#reminder-needed-hidden').val() == 'true') {
		$('#reminder-interval').attr("disabled", true);
	} else if ($('#reminder-needed-hidden').val() == 'false') {
		$('#reminder-interval').removeAttr("disabled");
	}
}

function overlayAccount() {
	$('#othercategory').val('other-account');

	$('#overlay-continue').click(function() {
		$('#st-settings-account-off').toggle();
		$('#st-settings-account-on').toggle();

		overlayRevert();
		updateOtherSettings("other-settings-form");
		$('#othercategory').val('');
		$('#overlay-continue').unbind('click');
	});
	$('#overlay-cancel').click(function() {
		$('#overlay-continue').unbind('click');
		overlayRevert();
		$('#othercategory').val('');
	});
}

function overlayDeleteAccount() {
	$('#othercategory').val('other-account');

	$('#overlay-continue').click(function() {
		overlayRevert();
		confirmDeleteAccount();
		$('#overlay-continue').unbind('click');
	});
	$('#overlay-cancel').click(function() {
		overlayRevert();
	});
}

function confirmDeleteAccount() {
	$('#deleteAccountForm').submit();
}

function createPopupConfirm(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Ok");
	$('#overlay-cancel').html("Cancel");

	$('#overlay-main').show();
}
/*
 * function overlayRevert() { $('#overlay-main').hide(); if ($('#overlay-continue').attr("disabled") == "disabled") { $('#overlay-continue').removeAttr("disabled"); } $("#overlay-header").html(''); $("#overlay-text").html(''); $('#overlay-continue').html(''); $('#overlay-cancel').html(''); }
 */

function showPaymentOptions() {
	disableBodyScroll();
	var url = "./paymentchange.do";
	showOverlay();
	callAjaxGET(url, displayPopup, true);
}

function displayPopup(data) {
	$("#temp-div").html(data);

	var displayMessageDiv = $("#display-msg-div");
	if ($(displayMessageDiv).hasClass("message")) {
		hideOverlay();
		$('#st-settings-payment-off').show();
		$('#st-settings-payment-on').hide();
		enableBodyScroll();
		$("#overlay-toast").html($(displayMessageDiv).html());
		showToast();
	} else {
		$('.overlay-payment').html(data);
		hideOverlay();
		$('.overlay-payment').show();
	}
	$("#temp-div").html("");
}

function updateAutoPostSetting(isautopostenabled, disableEle) {

	if ($(disableEle).data('requestRunning')) {
		return;
	}

	disable(disableEle);

	var payload = {
		"autopost" : isautopostenabled
	};
	var success = false;
	$.ajax({
		url : "./updateautopostforsurvey.do",
		type : "POST",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			enable(disableEle);
			if (success) {
				$('#overlay-toast').html("Content added successfully!");
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
	});
}

function updateEntitySettings(settingName, settingStatus) {
	var payload = {
		"settingName" : settingName,
		"settingStatus" : settingStatus
	};
	
	callAjaxPostWithPayloadData("./updateentitysettings.do", function(data) {
		$('#overlay-toast').html(data);
		showToast();
	}, payload, true);
	
}

function updateAutoPostLinkToUserSiteSetting(isautopostlinktositeenabled, disableEle) {
	var payload = {
		"autopostlinktousersite" : isautopostlinktositeenabled
	};
	
	callAjaxPostWithPayloadData("./updateautopostlinktousersiteforsurvey.do",function(data) {
		if (data == "success") $('#overlay-toast').html("Content updated successfully");
	}, payload, true, disableEle);
	
}

function updateSendDigestMailSiteSetting(issenddigestmailenabled, disableEle) {
	var payload = {
		"sendMonthlyDigestMail" : issenddigestmailenabled
	};
	
	callAjaxPostWithPayloadData("./updatesenddigestmailtoggle.do",function(data) {
		if (data == "true") {
			$('#overlay-toast').html("Send Monthly Digest Mail toggle Updated Sucessfully.");
		} else {
			$('#overlay-toast').html("Unable update send Monthly Digest Mail.");
		}
		showToast();
	}, payload, true, disableEle);
	
}

function enableSocialMonitorToggleSetting(issocialmonitorenabled, disableEle) {
	var payload = {
		"isSocialMonitorEnabled" : issocialmonitorenabled
	};
	
	callAjaxPostWithPayloadData("./enablesocialmonitortoggle.do",function(data) {
		if (data == "true") {
			$('#overlay-toast').html("Social Monitor toggle Updated Sucessfully.");
		} else {
			$('#overlay-toast').html("Unable to toggle Social Monitor.");
		}
		showToast();
	}, payload, true, disableEle);
	
}

//To update Branch and Region admin access permission.
function updateAdminAccess(allowAdminAddOrDeleteUser,  typeOfCheckBox, disableEle) {
	var payload = {
			"allowadminaddordeleteuser" : allowAdminAddOrDeleteUser,
			"typeofcheckbox" : typeOfCheckBox
	};

	callAjaxPostWithPayloadData("./updateadminaccess.do",function(data) {
		if(data == "true") {
			$('#overlay-toast').html(" Toggle Updated Sucessfully.");
		} else {
			$('#overlay-toast').html("Unable to update toggle");
		}
		showToast();
	}, payload,true,disableEle);
}


function enableIncompleteSurveyDeleteToggleSetting(isincompletesurveydeleteenabled, disableEle) {
	var payload = {
		"isIncompleteSurveyDeleteEnabled" : isincompletesurveydeleteenabled
	};
	
	callAjaxPostWithPayloadData("./enableincompletesurveydeletetoggle.do",function(data) {
		if (data == "true") {
			$('#overlay-toast').html("Incomplete Survey Delete toggle Updated Sucessfully.");
		} else {
			$('#overlay-toast').html("Unable to toggle Incomplete Survey Delete.");
		}
		showToast();
	}, payload, true, disableEle);
	
}

function updateVendastaAccessSetting(hasVendastaAcess, disableEle) {
	var payload = {
		"hasVendastaAcess" : hasVendastaAcess
	};
	
	callAjaxPostWithPayloadData("./updatevendastaaccesssetting.do",function(data) {
		if (data == "success") $('#overlay-toast').html("Content updated successfully");
	}, payload, true, disableEle);
	
}

function updateAllowPartnerSurveySettingForCompany(allowPartnerSurvey, disableEle) {
	var payload = {
		"allowPartnerSurvey" : allowPartnerSurvey
	};
	
	callAjaxPostWithPayloadData("./updateallowpartnersurveyforcompany.do",function(data) {
		if (data == "success"){
			if ($('#alw-ptnr-srvy-chk-box').hasClass('bd-check-img-checked')) {
				$('#alw-ptnr-srvy-chk-box').removeClass('bd-check-img-checked');
			}	
			else{
				$('#alw-ptnr-srvy-chk-box').addClass('bd-check-img-checked');
			}
			$('#overlay-toast').html("Content updated successfully");
		}else{
			$('#overlay-toast').html(data);
		}
		showToast();
	}, payload, true, disableEle);
	
}

function updateUpdateTransactionMonitorForCompany(updateTransactionMonitorSetting, disableEle) {
	var payload = {
		"updateTransactionMonitorSetting" : updateTransactionMonitorSetting
	};
	
	callAjaxGetWithPayloadData("./updatetransactionmonitorsettingforcompany.do",function(data) {
		if (data == "success"){
			if ($('#incld-fr-trans-mntr-chk-box').hasClass('bd-check-img-checked')) {
				$('#incld-fr-trans-mntr-chk-box').removeClass('bd-check-img-checked');
			}	
			else{
				$('#incld-fr-trans-mntr-chk-box').addClass('bd-check-img-checked');
			}
			$('#overlay-toast').html("Content updated successfully");
		}else{
			$('#overlay-toast').html(data);
		}
		showToast();
	}, payload, true, disableEle);
	
}

function updateCopyToClipBoardSettings(updateCopyToClipBoardSetting, disableEle) {
	var payload = {
		"updateCopyToClipBoardSetting" : updateCopyToClipBoardSetting
	};
	
	callAjaxGetWithPayloadData("./updatecopytoclipboardsettings.do",function(data) {
		if (data == "success"){
			if ($('#copyto-clipboard-chk-box').hasClass('bd-check-img-checked')) {
				$('#copyto-clipboard-chk-box').removeClass('bd-check-img-checked');
			}	
			else{
				$('#copyto-clipboard-chk-box').addClass('bd-check-img-checked');
			}
			$('#overlay-toast').html("Content updated successfully");
		}else{
			$('#overlay-toast').html(data);
		}
		showToast();
	}, payload, true, disableEle);
	
}

function resetOptOutTextFlow(resetId) {
	
	callAjaxGET("./resetoptouttext.do", function(data) {
		hideOverlay();
		if (data != null) {
			$('#' + resetId).val(data);
			$('#overlay-toast').html("Content reverted successfully!");
		} else {
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
		showToast();
	}, true);
}

function storeOptOutText(content, mood) {
	//encode text before sending to server
	var content = window.btoa( unescape( encodeURIComponent( content ) ) );
	var payload = {
		"text" : content
	};
	callAjaxGetWithPayloadData("./storeoptouttext.do", function(data) {
		if (data == "success") {
			$('#overlay-toast').html("Content updated successfully!");
		} else {
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
		showToast();
	}, payload, true);
}

function showEnableLoginButton(isLoginEnabled, disableEle) {
	var payload = {
		"isLoginEnabled" : isLoginEnabled
	};
	
	callAjaxPostWithPayloadData("./showenableloginbutton.do",function(data) {
		
		if (data == "true") {
			$('#enable-login-chk-box').removeClass('bd-check-img-checked');
			$('#overlay-toast').html("EnableLogin button is visible!");
		}
		else if (data == "false") {
			$('#enable-login-chk-box').addClass('bd-check-img-checked');
			$('#overlay-toast').html("EnableLogin button is hidden!");
		}
		else {
			$('#overlay-toast').html("Unable update show/hide EnableLogin button!!");
		}
		showToast();
	}, payload, true, disableEle);
	
}

function resetTextForMoodFlow(mood, resetId) {
	var payload = {
		"mood" : mood
	};
	callAjaxGetWithPayloadData("./resettextforflow.do", function(data) {
		hideOverlay();
		var map = $.parseJSON(data);

		if (map.success == 1 && map.message) {
			$('#' + resetId).val(map.message);
			$('#overlay-toast').html("Content reverted successfully!");
		} else {
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
		showToast();
	}, payload, true);
}

function saveTextForMoodFlow(content, mood) {
	//encode text before sending to server
	var content = window.btoa( unescape( encodeURIComponent( content ) ) );
	var payload = {
		"text" : content,
		"mood" : mood
	};
	callAjaxGetWithPayloadData("./storetextforflow.do", function(data) {
		if (data == "success") {
			$('#overlay-toast').html("Content updated successfully!");
		} else {
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
		showToast();
	}, payload, true);
}

function paintTextForMood(happyText, neutralText, sadText, happyTextComplete, neutralTextComplete, sadTextComplete,
		happyUrl, okUrl, sadUrl) {
	$('#happy-text').text(decodeURIComponent( escape( window.atob( happyText ) ) ));
	$('#neutral-text').text(decodeURIComponent( escape( window.atob( neutralText ) ) ));
	$('#sad-text').text(decodeURIComponent( escape( window.atob( sadText ) ) ));
	
	$('#happy-text-complete').text(decodeURIComponent( escape( window.atob( happyTextComplete ) ) ));
	$('#neutral-text-complete').text(decodeURIComponent( escape( window.atob( neutralTextComplete ) ) ));
	$('#sad-text-complete').text(decodeURIComponent( escape( window.atob( sadTextComplete ) ) ));
	
	$('#happy-complete-url').text(decodeURIComponent( escape( window.atob( happyUrl) ) ));
	$('#ok-complete-url').text(decodeURIComponent( escape( window.atob( okUrl) ) ));
	$('#sad-complete-url').text(decodeURIComponent( escape( window.atob( sadUrl))));	
}

// User management
$(document).on('click', '.um-user-row', function() {
	if (!isUserManagementAuthorized)
		return false;
	isAddUser = false;
	var userId = this.id;
	userId = userId.substr("um-user-".length);
	paintUserDetailsForm(userId);
});

$(document).on('click', '.tm-table-remove-icn', function(event) {
	if (!isUserManagementAuthorized)
		return false;
	event.stopPropagation();
	var userId = $(this).closest('.row').attr("data-id");
	userId = userId.substr("um-user-".length);
	confirmDeleteUser(userId);
});

$(document).on('blur', '#um-fname', function() {
	validateUserFirstName(this.id);
});
$(document).on('blur', '#um-lname', function() {
	validateUserLastName(this.id);
});
$(document).on('blur', '#um-emailid', function() {
	validateUserEmailId(this.id);
});

function initUserManagementPage() {
	userStartIndex = 0;
	paintUserListInUserManagement(userStartIndex);
}

function selectBranch(element) {
	var branch = $(element).html();
	var branchId = element.id.substr("branch-".length);
	$(element).parent().children('.um-dd-wrapper').show();
	$(element).parent().toggle();
	$('#um-assignto').val(branch);
	$('#um-assignto').attr("branchId", branchId);
}

$(document).on('click', '#um-add-user', function() {
	if (!isUserManagementAuthorized) {
		return false;
	}

	var userId;
	if (isAddUser) {
		// TODO Add code to create a new user.
		if (!validateUserInviteDetails()) {
			return false;
		}
		inviteUser();
		isAddUser = false;
		userId = $('#mh-userId').val();
	} else {
		userId = $('#um-user-details-container').attr("data-id");
	}

	if (!validateUserInviteDetails()) {
		return false;
	}
	if (userId == "" || userId == undefined) {
		return false;
	}

	var branchId = $('#um-assignto').attr("branchId");
	assignUserToBranch(userId, branchId);
});

$(document).on('click', '#um-clear-user-form', function() {
	if (!isUserManagementAuthorized) {
		return false;
	}
	isAddUser = true;
	paintUserDetailsForm("");
	/*
	 * if (!validateUserInviteDetails()) { return false; } inviteUser();
	 */
});

/*
 * Function to assign branch to a user
 */
function assignUserToBranch(userId, branchId) {
	var success = false;
	var payload = {
		"userId" : userId,
		"branchId" : branchId
	};
	showOverlay();
	$.ajax({
		url : "./assignusertobranch.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#common-message-header').hasClass("success-message")) {
				success = true;
			}
			if ($('#common-message-header').hasClass("error-message")) {
				createPopupInfo("Error!", $('#message-header p').text());
			}
		},
		complete : function() {
			if (success) {
				paintUserDetailsForm(userId);
				userStartIndex = 0;
				paintUserListInUserManagement();
			}
			hideOverlay();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

/*
 * Function to usassign branch from a user
 */
function unassignUserFromBranch(userId, branchId) {

	var success = false;
	showOverlay();
	var payload = {
		"userId" : userId,
		"branchId" : branchId
	};
	$.ajax({
		url : "./unassignuserfrombranch.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#common-message-header').hasClass("success-message")) {
				success = true;
			}
		},
		complete : function() {
			$('#overlay-cancel').click();
			hideOverlay();
			if (success) {
				$('#branch-to-unassign-' + branchId).remove();

				// check if there are any assigned branches left
				if ($('#um-assigned-branch-container > div').length <= 0) {
					$('#um-assignto').parent().parent().find('.um-item-row-icon').removeClass('icn-tick').addClass('icn-save');
				}
			} else {
				createPopupInfo("Error!", "Branch deletion unsuccessful");
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function inviteUser() {
	var success = false;
	var firstName = $('#um-fname').val();
	var lastName = $('#um-lname').val();
	var emailId = $('#um-emailid').val();
	showOverlay();
	var payload = {
		"firstName" : firstName,
		"lastName" : lastName,
		"emailId" : emailId
	};
	$.ajax({
		url : "./invitenewuser.do",
		type : "POST",
		dataType : "html",
		async : false,
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#common-message-header').hasClass("success-message")) {
				success = true;
			}
			if (success) {
				var userId = $('#mh-userId').val();
				paintUserDetailsForm(userId);
				userStartIndex = 0;
				paintUserListInUserManagement();
			} else {
				var userId = $('#mh-existing-userId').val();
				if (userId == undefined || userId == "") {
					createPopupInfo("Limit Exceeded", "Maximum limit of users exceeded.");
				} else {
					paintUserDetailsForm(userId);
					return;
				}
			}
		},
		complete : function() {
			hideOverlay();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function createPopupInfo(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-continue').attr("disabled", true);
	$('#overlay-continue').addClass("btn-disabled");
	$('#overlay-cancel').html('OK');
	$('#overlay-text').html(body);
	$('#overlay-main').show();
	$('#overlay-continue').removeClass("btn-disabled");
}

$(document).on('click', '#overlay-cancel', function() {
	$('#overlay-continue').removeAttr("onclick");
	$('#overlay-main').hide();
});

function confirmDeleteUser(userId, adminId) {
	if (userId == adminId) {
		$('#overlay-continue').hide();
		createPopupInfo("Access Denied", "Can not delete the admin account !!!");
		return;
	}

	$('#overlay-main').show();
	$('#overlay-continue').show();
	$('#overlay-continue').html("Delete");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Delete User");
	$('#overlay-text').html("Are you sure you want to delete user ?");
	$('#overlay-continue').attr("onclick", "deleteUser('" + userId + "');");
}

function confirmDeleteUserProfile(profileId, userId) {
	$('#overlay-main').show();
	$('#overlay-continue').show();
	$('#overlay-continue').html("Delete");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Delete User Profile");
	$('#overlay-text').html("Are you sure you want to delete user profile?");
	$('#overlay-continue').attr("onclick", "deleteUserProfile('" + profileId + "', '" + userId + "');");
}

/*
 * Function to deactivate a user and remove from company
 */
function deleteUser(userId) {
	$('#overlay-continue').removeAttr("onclick");
	$('#overlay-main').hide();

	var payload = {
		"userIdToRemove" : userId
	};
	showOverlay();
	callAjaxPostWithPayloadData("./removeexistinguser.do", function(data) {
		var map = $.parseJSON(data);
		if (map.status == "success") {
			showInfo(map.message);
		} else {
			showError(map.message);
		}

		// hide the row of the user deleted
		$('#user-row-' + userId).next('.v-tbl-row').remove();
		$('#user-row-' + userId).next('.u-tbl-row').remove();
		$('#user-row-' + userId).remove();
	}, payload, true);
}

// Function to delete user profile
function deleteUserProfile(profileId,userId) {
	showOverlay();
	var payload = {
		"profileId" : profileId
	};
	callAjaxPostWithPayloadData("./deleteuserprofile.do", function(data) {
		if (data == "success") {
			updateUserProfileTicksInManageTeam(userId);
			// close the popup
			$('#overlay-cancel').click();
			// remove the tab from UI
			$('#v-edt-tbl-row-' + profileId).remove();
		} else {
			// close the popup
			$('#overlay-cancel').click();
			$('#overlay-toast').html(data);
			showToast();
		}
	}, payload, true);
}

function updateUserProfileTicksInManageTeam(userId){
	var payload = {
		"userId" : userId
	};
	callAjaxGetWithPayloadData("./fetchuserprofileflags.do", function(data) {
		if( data != undefined ){
			var response = JSON.parse(data);
			if( response != undefined && response.success != undefined ){
				if( response.success == "true" ){
					if( response.isRegionAdmin  == "true" ){
						addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-rgn-adm'), "v-icn-tick" );
					} else {
						removeClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-rgn-adm'), "v-icn-tick" );
					}
					
					if( response.isBranchAdmin  == "true" ){
						addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-of-adm'), "v-icn-tick" );
					} else {
						removeClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-of-adm'), "v-icn-tick" );
					}
					
					if( response.isAgent  == "true" ){
						addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-ln-of'), "v-icn-tick" );
					} else {
						removeClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-ln-of'), "v-icn-tick" );
					}
				}
			}
		}
	}, payload, true);
}

/*
 * Paint the user details form in the user management page
 */
function paintUserDetailsForm(userId) {
	var payload = {
		"userId" : userId
	};
	$.ajax({
		url : "./finduserandbranchesbyuserid.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#user-details-container').html(data);
		},
		complete : function() {
			if (!isUserManagementAuthorized) {
				$('input').prop("disabled", true);
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

/*
 * Function paint the user list in user management page
 */
function paintUserListInUserManagement(startIndex) {
	
	var entityId = $(this).attr('data-column-value');
	var entityType = $(this).attr('data-column-type');
	var payload = {
		"startIndex" : startIndex,
		"batchSize" : userBatchSize,
		"entityId" : entityId,
		"entityType" : entityType
	};

	$.ajax({
		url : "./findusersforcompany.do",
		type : "GET",
		cache : false,
		data : payload,
		dataType : "html",
		success : function(data) {
			$('#user-list').html(data);
			var numFound = $('#u-tbl-header').attr("data-num-found");
			$('#users-count').val(numFound);
			userStartIndex = startIndex;
			updatePaginateButtons();
			bindEditUserClick();
			bindUMEvents();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

/*
 * Function to activate or deactivate user
 */
function activateOrDeactivateUser(isActive, userId) {
	var isAssign;
	var success = false;
	if (isActive) {
		isAssign = "yes";
	} else {
		isAssign = "no";
	}
	var payload = {
		"isAssign" : isAssign,
		"userIdToUpdate" : userId
	};
	showOverlay();
	$.ajax({
		url : "./updateuser.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#common-message-header').hasClass("success-message")) {
				success = true;
			}
		},
		complete : function() {
			$('#overlay-cancel').click();
			hideOverlay();
			if (success) {
				if (isActive) {
					$('#icn-status-red').addClass("hide");
					$('#icn-status-green').removeClass("hide");
					$('#um-icn-status-text').html("Active");
				} else {
					$('#icn-status-green').addClass("hide");
					$('#icn-status-red').removeClass("hide");
					$('#um-icn-status-text').html("Inactive");
				}
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

// function to validate input fields before sending the user invite
function validateUserInviteDetails() {
	var isFormValid = true;
	var isFocussed = false;
	var isSmallScreen = false;
	if ($(window).width() < 768) {
		isSmallScreen = true;
	}
	if (!validateUserFirstName('um-fname')) {
		isFormValid = false;
		if (!isFocussed) {
			$('#um-fname').focus();
			isFocussed = true;
		}
		if (isSmallScreen) {
			return isLoginFormValid;
		}
	}
	if (!validateUserLastName('um-lname')) {
		isFormValid = false;
		if (!isFocussed) {
			$('#um-lname').focus();
			isFocussed = true;
		}
		if (isSmallScreen) {
			return isLoginFormValid;
		}
	}
	if (!validateUserEmailId('um-emailid')) {
		isFormValid = false;
		if (!isFocussed) {
			$('#um-emailid').focus();
			isFocussed = true;
		}
		if (isSmallScreen) {
			return isLoginFormValid;
		}
	}
	return isFormValid;
}

/**
 * Function to check if branch name entered is null or empty
 */
function validateAssignToBranchName() {
	if ($('#' + elementId).val() == "") {
		showErrorMobileAndWeb("Please enter a branch name");
		return false;
	} else {
		return true;
	}
}

$(document).on('keyup', '#search-users-key', function(e) {
	// detect enter
	if (e.keyCode == 13) {
		userStartIndex = 0;
		if($(this).val() != '' && $(this).val() != null && $(this).val() != undefined ){
			searchUsersByNameEmailLoginId($(this).val());
		}else{
			initUserManagementPage();
		}
	}
});

$(document).on('click', '#um-search-icn', function(e) {
	userStartIndex = 0;
	if($('#search-users-key').val() != '' && $('#search-users-key').val() != null && $('#search-users-key').val() != undefined ){
		searchUsersByNameEmailLoginId($('#search-users-key').val());
	}else{
		initUserManagementPage();
	}
	
});

function searchUsersByNameEmailLoginId(searchKey) {
	var url = "./findusersunderadmin.do";
	var entityType = $(this).attr('data-column-type');
	var payload = {
		"searchKey" : searchKey,
		"startIndex" : userStartIndex,
		"batchSize" : userBatchSize,
		"entityType" : entityType
	};
	callAjaxGetWithPayloadData(url, searchUsersByNameEmailLoginIdCallBack, payload, true);
}

function searchUsersByNameEmailLoginIdCallBack(data) {
	var numFound = $('#u-tbl-header').attr("data-num-found");
	$('#users-count').val(numFound);
	$('#user-list').html(data);
	updatePaginateButtons();
	bindEditUserClick();
	bindUMEvents();
}

function bindUMEvents() {
	$('.v-tbn-icn-dropdown').off('click');
	$('.v-tbn-icn-dropdown').on('click', function(e) {
		e.stopPropagation();
		if (!$(this).next('.v-um-tbl-icn-wraper').is(':visible')) {
			$('.v-um-tbl-icn-wraper').hide();
			$(this).next('.v-um-tbl-icn-wraper').show();
		} else {
			$(this).next('.v-um-tbl-icn-wraper').hide();
		}
	});
	// resend verification mail
	$('.v-icn-fmail').off('click');
	$('.v-icn-fmail').on('click', function() {
		if ($(this).hasClass('v-tbl-icn-disabled')) {
			return;
		}
		$('.v-um-tbl-icn-wraper').hide();
		var $parentRowElemt = $(this).closest('.user-row');
		var firstName = $parentRowElemt.find('.fetch-name').attr('data-first-name');
		var lastName = $parentRowElemt.find('.fetch-name').attr('data-last-name');
		var emailId = $parentRowElemt.find('.fetch-email').text();
		reinviteUser(firstName, lastName, emailId, '.v-icn-fmail');
	});
}

function paginateUsersList() {
	if (!doStopAjaxRequestForUsersList) {
		paintUserListInUserManagement();
	}
}

$(document).on('keyup', '#um-assignto', function() {
	searchBranchesForUser(this.value);
});

/**
 * Method to perform search on solr for provided branch pattern
 * 
 * @param branchPattern
 */
function searchBranchesForUser(branchPattern) {
	var url = "./searchbranches.do?branchPattern=" + branchPattern;
	$.ajax({
		url : url,
		type : "GET",
		cache : false,
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				searchBranchesForUserCallBack(data.responseJSON);
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function searchBranchesForUserCallBack(jsonData) {
	var branchListContainer = $('<div>').attr({
		"class" : "um-branch-list"
	});

	$('#um-assignto').parent().find('.um-branch-list').remove();
	var searchResult = jsonData;
	if (searchResult != null) {
		var len = searchResult.length;
		if (len > 0) {
			$.each(searchResult, function(i, branch) {
				var branchDiv = $('<div>').attr({
					"id" : "branch-" + branch.branchId,
					"class" : "um-dd-wrapper cursor-pointer",
					"onclick" : "selectBranch(this);"
				}).html(branch.branchName);
				branchListContainer.append(branchDiv);
			});
		}
		$('#um-assignto').parent().append(branchListContainer);
	}
}

/*
 * Function fetch assignments for user
 */
function getUserAssignments(userId, element) {
	var url = "./finduserassignments.do?userId=" + userId;
	callAjaxGET(url, function(data) {

		if (element == undefined) {
			$('#user-details-and-assignments-' + userId).html(data);
		} else {
			element.html(data);
		}

		var assignToOption = $("#assign-to-txt").attr('data-assignto');
		showSelectorsByAssignToOption(assignToOption);

		/**
		 * bind the click and keyup events
		 */
		bindAssignToSelectorClick();
		bindOfficeSelectorEvents();
		bindRegionSelectorEvents();
		bindAdminCheckBoxClick();

		// de-activate user profile
		$('.tbl-switch-on').click(function() {
			var profileId = $(this).parent().data('profile-id');
			updateUserProfile(profileId, 0);
		});

		// activate user profile
		$('.tbl-switch-off').click(function() {
			var profileId = $(this).parent().data('profile-id');
			updateUserProfile(profileId, 1);
		});

		/*
		 * setTimeout(function() { $('#profile-tbl-wrapper-' + userId).perfectScrollbar(); }, 1000);
		 */

		$(document).on('click', 'body', function() {
			$('.dd-droplist').slideUp(200);
		});
	}, true);
}

$(document).on('click', '#user-edit-btn', function(e) {

	$('#user-edit-btn-row').hide();
	$('form input[data-editable="true"]').removeAttr("readonly");
	$('#btn-save-user-assignment').show();

	//$("#alw-ptnr-srvy-for-usr-chk-box").removeClass('disable-click');
	//$("#alw-ptnr-srvy-for-usr-chk-box").addClass('enable-click');
	
	$("#user-edit-save").off('click');
	$("#user-edit-save").on('click', function(e) {
		if (validateUserDetailsUserManagement()) {
			saveUserDetailsByAdmin();

			// refreshing right section after assignment
			setTimeout(function() {
				getUserAssignments($('#selected-userid-hidden').val());
			}, 2000);
		}
	});
	$('#user-edit-cancel').on('click', function() {
		setTimeout(function() {
			getUserAssignments($('#selected-userid-hidden').val());
		}, 1000);
	});
});

$(document).on('click', '#user-assign-btn', function(e) {

	$('#user-edit-btn-row').hide();
	$('#user-assignment-cont').show();
	$('#soc-mon-admin-privilege-div').show();
	$('#btn-save-user-assignment').show();

	$("#user-edit-save").off('click');
	$("#user-edit-save").on('click', function(e) {
		
		if (validateIndividualForm()) {
			saveUserAssignment("user-assignment-form", '#user-edit-save');

			// refreshing right section after assignment
			setTimeout(function() {
				getUserAssignments($('#selected-userid-hidden').val());
			}, 2000);
		}
	});
	$('#user-edit-cancel').on('click', function() {
		setTimeout(function() {
			getUserAssignments($('#selected-userid-hidden').val());
		}, 1000);
	});
});

function validateUserDetailsUserManagement() {

	var isUserDetailsFormValid = true;

	return isUserDetailsFormValid;
}

/**
 * Method to update user details edited by admin
 * 
 * @param formId
 */
function saveUserDetailsByAdmin() {
	var url = "./updateuserbyadmin.do";
	var userId = $('#selected-userid-hidden').val();
	var firstName = $('#um-user-first-name').val();
	var lastName = $('#um-user-last-name').val();
	var emailID = $('#selected-user-txt').val();
	var name = firstName;
	if (lastName && lastName != "") {
		name += " " + lastName;
	}
	var payload = {
		"userId" : userId,
		"name" : name,
		"firstName" : firstName,
		"lastName" : lastName,
		"emailId" : emailID
	};

	showOverlay();
	callAjaxPostWithPayloadData(url, function(data) {
		hideOverlay();

		// view hierarchy page
		$('.v-tbl-row[data-userid="' + userId + '"]').find('.v-tbl-name').text(name);
		$('.v-tbl-row[data-userid="' + userId + '"]').find('.v-tbl-add').text(emailID);

		// user management page
		$('td[data-user-id="' + userId + '"]').text(name).attr("data-first-name", firstName).attr("data-last-name", lastName);
		$('td[data-user-id="' + userId + '"]').parent().find('.v-tbl-email').text(emailID);

		$('#overlay-toast').html(data);
		showToast();
	}, payload, true, '#user-edit-save');
}

/**
 * Method to save the assignment of user with branch/region or company
 * 
 * @param formId
 */
function saveUserAssignment(formId, disableEle) {
	var url = "./addindividual.do";
	var userId = $( "#" + formId).find("#selected-userid-hidden").val();
	var isAdminCheck = $( "#" + formId).find("#is-admin-chk").val();
	var assignToText = $("#assign-to-txt").val();
	showOverlay();
	callAjaxFormSubmit(url, function(data){
		hideOverlay();
		displayMessage(data);
		if( userId != undefined && data != undefined && data.indexOf("success") > -1 && assignToText != undefined && isAdminCheck != undefined ){
			if( "Company" == assignToText ){
				if( "true" == isAdminCheck ){
					// do nothing
				} else if( "false" == isAdminCheck ){
					addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-ln-of'), "v-icn-tick" );
				}
			} else if( "Region" == assignToText ){
				if( "true" == isAdminCheck ){
					addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-rgn-adm'), "v-icn-tick" );
				} else if( "false" == isAdminCheck ){
					addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-ln-of'), "v-icn-tick" );
				}
			} else if( "Office" == assignToText ){
				if( "true" == isAdminCheck ){
					addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-of-adm'), "v-icn-tick" );
				} else if( "false" == isAdminCheck ){
					addClassToJQueryElement( $('#user-row-' + userId).find('.v-tbl-ln-of'), "v-icn-tick" );
				}
			}
		}
	}, formId, disableEle);
}


function addClassToJQueryElement( element, clazz ){
	if( element != undefined && !element.hasClass(clazz) ){
		element.addClass(clazz);
	}
}

function removeClassToJQueryElement( element, clazz ){
	if( element != undefined && element.hasClass(clazz) ){
		element.removeClass(clazz);
	}
}

// remove user profile
$(document).on('click', '.v-icn-rem-userprofile', function(e) {
	e.stopPropagation();
	if ($(this).hasClass('v-tbl-icn-disabled')) {
		return;
	}

	var profileId = $(this).parent().data('profile-id');
	var userIdArr = $(this).parent().parent().parent().parent().attr("id").split("-");
	var userId = userIdArr[userIdArr.length -1];
	confirmDeleteUserProfile(profileId, userId);
});

// remove user
$(document).on('click', '.v-icn-rem-user', function() {
	if ($(this).hasClass('v-tbl-icn-disabled')) {
		return;
	}

	var userId = $(this).closest('.user-row').find('.fetch-name').attr('data-user-id');
	var adminId = '${user.userId}';
	confirmDeleteUser(userId, adminId);
});

/**
 * Method to send invite link
 */
function reinviteUser(firstName, lastName, emailId, disableEle) {
	var payload = {
		"firstName" : firstName,
		"lastName" : lastName,
		"emailId" : emailId
	};
	var url = "./reinviteuser.do";
	showOverlay();
	callAjaxGetWithPayloadData(url, reinviteUserCallBack, payload, true, disableEle);
}

function reinviteUserCallBack(data) {
	var map = $.parseJSON(data);
	if (map.status == "success") {
		showInfo(map.message);
	} else {
		showError(map.message);
	}
}

function updateUserProfile(profileId, profileStatus) {
	showOverlay();
	var payload = {
		"profileId" : profileId,
		"status" : profileStatus
	};
	callAjaxPostWithPayloadData("./updateuserprofile.do", function(data) {
		hideOverlay();

		var map = $.parseJSON(data);
		if (map.status == "success") {
			showInfo(map.message);
			if (profileStatus == 1) {
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').removeClass('tbl-switch-off');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').addClass('tbl-switch-on');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').attr('title', 'Active');

				// de-activate user profile
				$('.tbl-switch-on').unbind('click');
				$('.tbl-switch-on').click(function() {
					var profileId = $(this).parent().data('profile-id');
					updateUserProfile(profileId, 0);
				});
			} else if (profileStatus == 0) {
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').removeClass('tbl-switch-on');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').addClass('tbl-switch-off');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').attr('title', 'InActive');

				// activate user profile
				$('.tbl-switch-off').unbind('click');
				$('.tbl-switch-off').click(function() {
					var profileId = $(this).parent().data('profile-id');
					updateUserProfile(profileId, 1);
				});
			}
		} else {
			showError(map.message);
		}
	}, payload, false);
}

function bindEditUserClick() {
	$('.edit-user').click(function(e) {
		e.stopPropagation();
		$('.v-um-tbl-icn-wraper').hide();
		if ($(this).hasClass('v-tbl-icn-disabled')) {
			return;
		}

		// de-activate user profile
		$('.tbl-switch-on').click(function() {
			var profileId = $(this).parent().data('profile-id');
			updateUserProfile(profileId, 0);
		});

		// activate user profile
		$('.tbl-switch-off').click(function() {
			var profileId = $(this).parent().data('profile-id');
			updateUserProfile(profileId, 1);
		});

		if ($(this).closest('.user-row').hasClass('u-tbl-row-sel')) {
			$(this).closest('.user-row').removeClass('u-tbl-row-sel');
			$(this).closest('.user-row').next('.user-assignment-edit-row').slideUp(200);
		} else {
			// make an ajax call and fetch the details of the user
			var userId = $(this).closest('.user-row').find('.fetch-name').attr('data-user-id');
			$(".user-assignment-edit-div").html("");
			$(".user-row").removeClass('u-tbl-row-sel');
			$(".user-assignment-edit-row").slideUp();

			getUserAssignments(userId);

			$(this).closest('.user-row').next('.user-assignment-edit-row').slideDown(200);
			$(this).closest('.user-row').addClass('u-tbl-row-sel');

			setTimeout(function() {
				$('#profile-tbl-wrapper-' + userId).perfectScrollbar();
			}, 1000);
		}
	});
}

$(document).on('click', '#page-previous.paginate-button', function() {
	var newIndex = userStartIndex - userBatchSize;
	var searchKey = $('#search-users-key').val();
	if (newIndex < $('#users-count').val()) {
		if (searchKey == undefined || searchKey == "") {
			paintUserListInUserManagement(newIndex);
		} else {
			userStartIndex = newIndex;
			searchUsersByNameEmailLoginId(searchKey);
		}
	}
});

$(document).on('click', '#page-next.paginate-button', function() {
	var newIndex = userStartIndex + userBatchSize;
	var searchKey = $('#search-users-key').val();
	if (newIndex < $('#users-count').val()) {
		if (searchKey == undefined || searchKey == "") {
			paintUserListInUserManagement(newIndex);
		} else {
			userStartIndex = newIndex;
			searchUsersByNameEmailLoginId(searchKey);
		}
	}
});
function updatePaginateButtons() {
	var numFound = $('#u-tbl-header').attr('data-num-found');
	if (numFound > userBatchSize) {
		$('#paginate-buttons').show();

		// next button
		if (userStartIndex <= 0) {
			$('#page-previous').removeClass('paginate-button');
		} else {
			$('#page-previous').addClass('paginate-button');
		}

		// previous button
		if (userStartIndex + userBatchSize >= $('#users-count').val()) {
			$('#page-next').removeClass('paginate-button');
		} else {
			$('#page-next').addClass('paginate-button');
		}
	} else {
		$('#paginate-buttons').hide();
	}
}

// Edit profile dropdown
// Profile View as
$('body').on('click', '#profile-sel', function(e) {
	e.stopPropagation();
	$('#pe-dd-wrapper-profiles').slideToggle(200);
});
$('body').on('click', '.pe-dd-item', function(e) {
	$('#profile-sel').html($(this).html());
	$('#pe-dd-wrapper-profiles').slideToggle(200);

	var entityId = $(this).attr('data-column-value');
	var entityType = $(this).attr('data-column-type');

	showMainContent("./showprofilepage.do?entityId=" + entityId + "&entityType=" + entityType);
});

$('body').click(function() {
	if ($('#pe-dd-wrapper-profiles').css('display') == "block") {
		$('#pe-dd-wrapper-profiles').toggle();
	}
});

// Settings page dropdown
// Settings View as
$('body').on('click', '#setting-sel', function(e) {
	e.stopPropagation();
	$('#se-dd-wrapper-profiles').slideToggle(200);
});

$('body').on('click', '.se-dd-item', function(e) {
	$('#setting-sel').html($(this).html());
	$('#se-dd-wrapper-profiles').slideToggle(200);

	var entityId = $(this).attr('data-column-value');
	var entityType = $(this).attr('data-column-type');

	showMainContent("./showcompanysettings.do?entityId=" + entityId + "&entityType=" + entityType);
});

$('body').click(function() {
	if ($('#se-dd-wrapper-profiles').css('display') == "block") {
		$('#se-dd-wrapper-profiles').toggle();
	}
});

// Linked In Import
function authenticate(event, socialNetwork) {
	openAuthPage(event, socialNetwork);
	payload = {
		'socialNetwork' : socialNetwork
	};
}

function authenticateZillow(event) {
	openAuthPageZillow(event);
}

// update yelp profile url
function showYelpInput() {
	$('#yelp-profile-url-display').addClass('hide');

	$('#yelp-profile-url').removeClass('hide');
	$('#yelp-profile-url').focus();
}

$(document).on('blur', '#yelp-profile-url', function() {
	var yelpLink = $('#yelp-profile-url').val();

	// check if link edited or not
	if (yelpLink == $('#yelp-profile-url-display').html().trim()) {
		$('#yelp-profile-url-display').removeClass('hide');
		$('#yelp-profile-url').addClass('hide');
		return;
	}

	if (isValidUrl(yelpLink)) {
		var payload = {
				"yelplink" : yelpLink
			};
		callAjaxPostWithPayloadData("./updateyelplink.do", function(data) {
			$('#yelp-profile-url-display').html(yelpLink);
			$('#yelp-profile-url-display').removeClass('hide');

			$('#yelp-profile-url').addClass('hide');

			$('#message-header').html(data);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload, true);
	} else if( yelpLink == '') {
		confirmDisconnectSocialMedia("yelp");
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
});

// show social profile urls
function showLinkedInProfileUrl(data) {
	if (data == undefined || data == null) {
		return;
	}

	$('#wl-import-btn').remove();
	$('#wl-import-btn-msg').removeClass('hide');
	$('#wc-connect-link').html('LinkedIn Profile <u><a href=' + data + ' target="_blank">' + data + '</a></u>');
}

function loadSocialMediaUrlInSettingsPage() {
	callAjaxGET('/getsocialmediatokenonsettingspage.do', function(data) {
		$('#social-media-token-cont').html(data);
	}, false);
}

function loadSocialMediaUrlInPopup() {
	callAjaxGET('/fetchsociallinksinpopup.do', function(data) {
		$('#wc-step3-body-cont').html(data);
	}, false);
}

function showProfileLink(source, profileUrl) {
	if (source == 'facebook') {
		$('#fb-profile-url').html(profileUrl);
	} else if (source == 'twitter') {
		$('#twitter-profile-url').html(profileUrl);
	} else if (source == 'linkedin') {
		$('#linkedin-profile-url').html(profileUrl);
	} else if (source == 'google') {
		$('#ggl-profile-url').html(profileUrl);
	} else if (source == 'instagram') {
		$('#instagram-profile-url').html(profileUrl);
	}
}

$(document).on('click', '.ctnt-review-btn', function() {
	initSurveyReview($(this).attr('user'));
});

function initSurveyWithUrl(q) {
	var success = false;
	var payload = {
		"q" : q
	};
	$.ajax({
		url : getLocationOrigin() + surveyUrl + "triggersurveywithurl",
		type : "GET",
		cache : false,
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
			else {
				$('#overlay-toast').html(data.errMessage);
				$("#recaptcha_reload").click();
				showToast();
			}
		},
		complete : function(data) {
			if (success) {
				agentId = data.responseJSON.agentId;
				loadAgentPic(agentId);
				agentName = data.responseJSON.agentName;
				customerEmail = data.responseJSON.customerEmail;
				firstName = data.responseJSON.customerFirstName;
				lastName = data.responseJSON.customerLastName;
				surveyId = data.responseJSON.surveyId;
				hiddenSection=data.responseJSON.hiddenSection;
				companyName = data.responseJSON.companyName;
				var copyToClipboard = data.responseJSON.copyToClipBoard;
				$('#prof-container').attr('data-copy-to-clipboard',copyToClipboard);
				companyId = data.responseJSON.companyId;
				paintSurveyPage(data);
				var message = $("#pst-srvy-div .bd-check-txt").html();
				if(hiddenSection){
					$("#pst-srvy-div .bd-check-txt").html(message.replace("%s", companyName));
				}else{
					$("#pst-srvy-div .bd-check-txt").html(message.replace("%s", agentName));
				}
				swearWords=getSwearWords(companyId);
			} 
			else {
				$('.sq-ques-wrapper').addClass( 'sq-main-txt' );
				$('.sq-ques-wrapper').empty();
				$('.sq-ques-wrapper').html("<div class='error-main-wrapper container' style='min-height: 504px;'>  <div class='err-line-1 text-center'>SURVEY REQUEST EXPIRED</div>  <br>  <br>  <div class='err-line-2'>We're really excited to offer the opportunity for you to share your experience; however, the link you clicked has expired. If you feel this is an error, please call us at <a href='tel:+1-888-701-4512'>1-888-701-4512</a> or <a href='https://socialsurvey.zendesk.com/hc/en-us/requests/new'>send us a message</a>.</div>  <br>  <div class='err-page-btn'>Close</div></div>");			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			showPageNotFoundError();
		}
	});
}

function showPageNotFoundError() {
	window.location = getLocationOrigin() + surveyUrl + "notfound";
}

function loadAgentPic(agentId) {
	var imageUrl;
	var success = false;
	var payload = {
		"agentId" : agentId
	};
	$.ajax({
		url : getLocationOrigin() + surveyUrl + "displaypiclocationofagent",
		type : "GET",
		dataType : "text",
		cache : false,
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {

			if (success) {
				imageUrl = data.responseText;
				if (imageUrl.trim() != '' && imageUrl != null) {
					$("#agnt-img").html("<img class='hr-ind-img' src='" + imageUrl + "'/>");
				}
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function paintSurveyPage(jsonData) {
	$("#pst-srvy-div").hide();
	questions = jsonData.responseJSON.survey;
	stage = jsonData.responseJSON.stage;
	editable = Boolean(jsonData.responseJSON.editable);
	happyText = jsonData.responseJSON.happyText;
	neutralText = jsonData.responseJSON.neutralText;
	sadText = jsonData.responseJSON.sadText;
	happyTextComplete = jsonData.responseJSON.happyTextComplete;
	neutralTextComplete = jsonData.responseJSON.neutralTextComplete;
	sadTextComplete = jsonData.responseJSON.sadTextComplete;
	happyUrl = jsonData.responseJSON.happyUrl;
	okUrl = jsonData.responseJSON.okUrl;
	sadUrl = jsonData.responseJSON.sadUrl;
	autoPost = jsonData.responseJSON.autopostEnabled;
	autoPostScore = jsonData.responseJSON.autopostScore;
	yelpEnabled = Boolean(jsonData.responseJSON.yelpEnabled);
	googleEnabled = Boolean(jsonData.responseJSON.googleEnabled);
	zillowEnabled = Boolean(jsonData.responseJSON.zillowEnabled);
	lendingtreeEnabled = Boolean(jsonData.responseJSON.lendingtreeEnabled);
	realtorEnabled = Boolean(jsonData.responseJSON.realtorEnabled);
	googleBusinessEnabled = Boolean(jsonData.responseJSON.googleBusinessEnabled);
	agentProfileLink = jsonData.responseJSON.agentProfileLink;
	agentFullProfileLink = jsonData.responseJSON.agentFullProfileLink;
	fb_app_id = jsonData.responseJSON.fbAppId;
	google_plus_app_id = jsonData.responseJSON.googlePlusAppId;
	surveyId = jsonData.responseJSON.surveyId;
	zillowReviewLink =  jsonData.responseJSON.zillowLink;
	subjectContentForZillowPost = jsonData.responseJSON.subjectContentForZillowPost;
	isAutoFillReviewContentForZillowPost = jsonData.responseJSON.isAutoFillReviewContentForZillowPost;
	reviewFooterContentForZillowPost = jsonData.responseJSON.reviewFooterContentForZillowPost;
	
	
	// URL redirect changes
	surveySourceId = jsonData.responseJSON.surveySourceId;
	participationtype = jsonData.responseJSON.participationType;
	transactionType = jsonData.responseJSON.transactionType;
	city = jsonData.responseJSON.city;
	state = jsonData.responseJSON.state;
	customeField1 = jsonData.responseJSON.customField1;
	customeField2 = jsonData.responseJSON.customField2;
	customeField3 = jsonData.responseJSON.customField3;
	customeField4 = jsonData.responseJSON.customField4;
	customeField5 = jsonData.responseJSON.customField5;

	// If social token availiable populate the links
	// if (googleEnabled) {
	// var googleElement = document.getElementById('ggl-btn');
	// //shareOnGooglePlus(agentId, window.location.origin + "/rest/survey/", googleElement);
	// shareOnGooglePlus(agentId, getLocationOrigin() + "/rest/survey/", googleElement);
	// } else {
	// $('#ggl-btn').remove();
	// }
	$('#google-btn').attr("href", "https://plus.google.com/share?url=" + agentFullProfileLink + "/" + surveyId );

	//SS-1452 remove yelp from all the pages
	/*if (yelpEnabled) {
		$('#ylp-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.yelpLink));
	} else {
		$('#ylp-btn').remove();
	}*/

	if (zillowEnabled) {
		$('#zillow-btn').attr("href", returnValidWebAddress(zillowReviewLink));
	} else {
		$('#zillow-btn').remove();
	}

	if (lendingtreeEnabled) {
		$('#lt-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.lendingtreeLink));
	} else {
		$('#lt-btn').remove();
	}

	if (realtorEnabled) {
		$('#realtor-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.realtorLink) + "#reviews-section");
	} else {
		$('#realtor-btn').remove();
	}

	if (googleBusinessEnabled) {
		$('#google-business-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.googleBusinessLink));
	} else {
		$('#google-business-btn').remove();
	}

	companyLogo = jsonData.responseJSON.companyLogo;

	if (stage != undefined)
		qno = stage;
	paintSurveyPageFromJson();
}


function paintSocialMediaIconsOnSurveyCompletion(){
if (zillowEnabled) {
		
		//add rating for zillow post
		var unProcessedZillowReviewLink = zillowReviewLink + "&rating=" + rating;
		// add title to zillow post
		if(subjectContentForZillowPost != null && subjectContentForZillowPost != undefined){
			unProcessedZillowReviewLink = unProcessedZillowReviewLink + "&title=" + subjectContentForZillowPost;
		}else{
			var subject = "";
			if(reviewText.includes('.')){
				subject = reviewText.substring(0, reviewText.indexOf('.'));
		    }else if(reviewText.length > 250){
		    		subject = reviewText.substring(0, 249);	
		    }else{
		    		subject = reviewText;
		    }   
			unProcessedZillowReviewLink = unProcessedZillowReviewLink + "&title=" + subject;		
		}
		//add review footer if given along with review
		var reviewTextForZillow = reviewText;
		if(reviewFooterContentForZillowPost != null && reviewFooterContentForZillowPost != undefined )
			reviewTextForZillow = reviewTextForZillow + " " + reviewFooterContentForZillowPost;
		//add review text for zillow post if company enabled
		if(isAutoFillReviewContentForZillowPost)
			unProcessedZillowReviewLink = unProcessedZillowReviewLink + "&content=" + reviewTextForZillow;
		
		$('#zillow-btn').attr("href", returnValidWebAddress(unProcessedZillowReviewLink));
		
		$('#zillow_workflow_url').attr("href", returnValidWebAddress(unProcessedZillowReviewLink));
		
		
	} else {
		$('#zillow-btn').remove();
	}
}

/*
 * It gets the questions from array of questions and finds out the current question based upon current index for question number. It also checks over various conditions of the question and renders the page accordingly.
 */
function paintSurveyPageFromJson() {
	$("div[data-ques-type]").hide();
	if (qno == -1 && editable == false) {
		$("div[data-ques-type]").hide();
		$("div[data-ques-type='error']").show();
		$('#content-head').html('Survey');
		$('#content').html("OOPS! It looks like you have already taken a survey for " + agentName + "." + "<br/><br/>" + "Are you trying to amend a prior response? If so click the link below and we will email you the access required<br/><br/>").append("<div>Link to resend original Survey Responses so they can be amended</div>");

		var linkToResendSurvey = $('<div>').attr({
			"id" : "changeSurvey",
			"class" : "change-survey-btn"
		}).html("Retake survey");

		$('#content').append(linkToResendSurvey);

		$(document).on('click', '#changeSurvey', function() {
			retakeSurveyRequest();
		});
		return;
	}
	questionDetails = questions[qno];
	var question = questionDetails.question;

	question = question.replace(/\[name\]/gi, agentName);
	//get agentFirstName
	var AgentFirstName = agentName;
	if(agentName.indexOf(" ") > -1)
		var AgentFirstName = agentName.substr(0, agentName.indexOf(" "));
	//replace first name
	question = question.replace(/\[AgentFirstName\]/gi, AgentFirstName);
		
	var questionType = questionDetails.questionType;
	var isRatingQuestion = questionDetails.isRatingQuestion;
	if (isRatingQuestion == 1) {
		$("#skip-ques").hide();
		$("#next-star").addClass("btn-com-disabled");
		$("#next-smile").addClass("btn-com-disabled");
		$("#next-scale").addClass("btn-com-disabled");
		$("#next-radio").addClass("btn-com-disabled");
		$("#next-radio-nps").addClass("btn-com-disabled");
	}
	
	var questionTextEdited = question.replace(/&lt;/g,'<').replace(/&gt;/g, '>').replace(/&quot;/g,'"');
	if (questionType == "sb-range-star") {
		$("div[data-ques-type='stars']").show();
		$("#ques-text").html(questionTextEdited);
		$("#sq-stars").show();
		if (questionDetails.customerResponse != undefined && !isNaN(parseInt(questionDetails.customerResponse))) {
			var starVal = parseInt(questionDetails.customerResponse);
			if(starVal > 5)
				starVal = Math.floor(starVal/2);
			$('#sq-stars').attr('selected-star-no' , starVal);
			increaseOpacityOfStars(parseInt(starVal));
			$("#next-star").removeClass("btn-com-disabled");
		}
	} else if (questionType == "sb-range-smiles") {
		$("div[data-ques-type='smiley']").show();
		$("#ques-text-smiley").html(questionTextEdited);
		$("#sq-smiles").show();
		if (questionDetails.customerResponse != undefined && !isNaN(parseInt(questionDetails.customerResponse))) {
			var starVal = parseInt(questionDetails.customerResponse);
			if(starVal > 5)
				starVal = Math.floor(starVal/2);
			$('#sq-smiles').attr('selected-smiles-no' , starVal);
			increaseOpacityOfStars(parseInt(starVal));
			$("#next-smile").removeClass("btn-com-disabled");
		}
	} else if (questionType == "sb-range-scale") {
		$("div[data-ques-type='scale']").show();
		$("#ques-text-scale").html(questionTextEdited);
		$("#sq-stars").show();
	} else if (questionType == "sb-sel-mcq") {
		$("div[data-ques-type='mcq']").show();
		$("#mcq-ques-text").html(questionTextEdited);
		$("#skip-ques-mcq").show();
		$("#next-mcq").show();
		$("#next-mcq").removeClass("btn-com-disabled");
		var options = "";
		for ( var option in questionDetails.answers) {
			options += paintMcqAnswer(questionDetails.answers[option].answerText);
		}
		$("#answer-options").html(options);
		bindMcqCheckButton();
	} else if (questionType == "sb-sel-desc") {
		$("div[data-ques-type='smiley-text-final']").show();
		$("#ques-text-textarea").html(questionTextEdited);
		$("#text-area").show();

		var val = questionDetails.customerResponse;
		if (val != undefined) {
			$("#text-area").val(val);
		} else {
			$("#text-area").val('');
		}

		$('#text-box-disclaimer').show();
		$("#smiles-final").hide();
		if (questionDetails.customerResponse != undefined)
			$("#text-area").html(questionDetails.customerResponse);
	} else if (questionType == "sb-master") {
		$("div[data-ques-type='smiley-text-final']").show();
		$("#text-area").hide();
		$('#text-box-disclaimer').hide();
		$("#smiles-final").show();
		$("#ques-text-textarea").html(questionTextEdited);
	} else if (questionType == "sb-range-0to10"){
		if(questionDetails.isNPSQuestion == 0){
			$("#ques-text-1to10").html(questionTextEdited)
			$("div[data-ques-type='sb-range-0to10']").show();
			$('#notAtAllLikelyDiv').html(questionDetails.notAtAllLikely);
			$('#veryLikelyDiv').html(questionDetails.veryLikely);
			if (questionDetails.customerResponse != undefined && !isNaN(parseInt(questionDetails.customerResponse))) {
				var ratingVal = parseInt(questionDetails.customerResponse);
				$('.sq-radio').each(function() {
				    $(this).removeClass('radio-outer-gray');
				    $(this).children().hide();
				    $(this).addClass('radio-outer');
				    $(this).css("cursor","pointer");
				});
				$('#radio-'+ratingVal).children().show();
				$('#radio-'+ratingVal).css("cursor","default");
				$('#sq-radio-1to10').attr('selected-rating-radio',ratingVal);
				$("#next-radio").removeClass("btn-com-disabled");
			}
		}else{
			$("#ques-text-1to10-nps").html(questionTextEdited)
			$("div[data-ques-type='sb-range-0to10-nps']").show();
			$('#notAtAllLikelyDivNps').html(questionDetails.notAtAllLikely);
			$('#veryLikelyDivNps').html(questionDetails.veryLikely);
			$('#nps-range-text').css('opacity',1);
			if (questionDetails.customerResponse != undefined && !isNaN(parseInt(questionDetails.customerResponse))) {
				var ratingVal = parseInt(questionDetails.customerResponse);
				$('.sq-radio').each(function() {
				    $(this).children().hide();
				    $(this).parent().find('.popover').hide();
				    $(this).css("cursor","pointer");
				});
				$('#radio-nps-'+ratingVal).children().show();
				$('#radio-nps-'+ratingVal).parent().find('.popover').show();
				$('#radio-nps-'+ratingVal).css("cursor","default");
				$('#nps-range-text').css('opacity',0);
				$('#sq-radio-1to10-nps').attr('selected-rating-radio',ratingVal);
				$("#next-radio-nps").removeClass("btn-com-disabled");
			}
		}
		
	}
	togglePrevAndNext();
	if (qno == questions.length - 1) {
		$("#next-mcq").addClass("btn-com-disabled");
		$("#next-smile").addClass("btn-com-disabled");
		$("#next-star").addClass("btn-com-disabled");
		$("#next-radio").addClass("btn-com-disabled");
		$("#next-radio-nps").addClass("btn-com-disabled");
		$("#next-textarea-smiley").addClass("btn-com-disabled");
		$("#skip-ques-mcq").hide();
	}
	if(hiddenSection){
		$(".sq-main-txt").html("Survey for " + companyName);
	}else{
		$(".sq-main-txt").html("Survey for " + agentName);
	}

	if (companyLogo != undefined && companyLogo != "") {
		var companylogoHtml = '<div class="float-left user-info-seperator"></div>';
		companylogoHtml += '<div class="float-left user-info-logo" style="background: url(' + companyLogo + ') no-repeat center; background-size: contain"></div>';
		$('#header-user-info').html(companylogoHtml);
	}
}

function togglePrevAndNext() {
	if (qno == 0) {
		$("#prev-star").addClass("btn-com-disabled");
		$("#prev-smile").addClass("btn-com-disabled");
		$("#prev-scale").addClass("btn-com-disabled");
		$("#prev-mcq").addClass("btn-com-disabled");
		$("#prev-radio").addClass("btn-com-disabled");
		$("#prev-radio-nps").addClass("btn-com-disabled");
		$("#prev-textarea-smiley").addClass("btn-com-disabled");
	} else {
		$("#prev-star").removeClass("btn-com-disabled");
		$("#prev-smile").removeClass("btn-com-disabled");
		$("#prev-scale").removeClass("btn-com-disabled");
		$("#prev-mcq").removeClass("btn-com-disabled");
		$("#prev-radio").removeClass("btn-com-disabled");
		$("#prev-radio-nps").removeClass("btn-com-disabled");
		$("#prev-textarea-smiley").removeClass("btn-com-disabled");
	}
}

function retakeSurveyRequest() {
	var payload = {
		"customerEmail" : customerEmail,
		"agentId" : agentId,
		"firstName" : firstName,
		"lastName" : lastName,
		"agentName" : agentName,
		"surveyId" : surveyId
	};
	callAjaxGetWithPayloadData(getLocationOrigin() + surveyUrl + 'restartsurvey', '', payload, true);
	$('#overlay-toast').html('Mail sent to your registered email id for retaking the survey for ' + agentName);
	showToast();
}

/*
 * This method is used to store the answer provided by the customer for a specific question.
 */
function storeCustomerAnswer(customerResponse) {
	
	var success = false;
	//encode question and response
	var encodedCustomerResponse = window.btoa( unescape( encodeURIComponent( customerResponse ) ) );
	var encodedQuestion =  window.btoa( unescape( encodeURIComponent( questionDetails.question ) ) );
	
	var considerForScore = questionDetails.considerForScore;
	if(questionDetails.questionType != 'sb-range-0to10'){
		considerForScore = 1;
	}
	
	var payload = {
			  "answer" : encodedCustomerResponse,
			  "question" : encodedQuestion,
			  "questionType" : questionDetails.questionType,
			  "isUserRankingQuestion" : questionDetails.isUserRankingQuestion,
			  "isNPSQuestion" : questionDetails.isNPSQuestion,
			  "stage" : qno + 1,
			  "surveyId" : surveyId,
			  "considerForScore": considerForScore,
			  "questionId" : questionDetails.questionId
	};
	showOverlay();
	questionDetails.customerResponse = customerResponse;
	$.ajax({
		url : getLocationOrigin() + surveyUrl + "data/storeAnswer",
		type : "GET",
		cache : false,
		data : payload,
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			hideOverlay();
			if (success) {
				if (swearWords.length <= 0) {
					var parsed = data.responseJSON;
					for ( var x in parsed) {
						swearWords.push(parsed[x]);
					}
				}
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function updateCustomerResponse(feedback, agreedToShare, isAbusive, isIsoEncoded, onlyPostToSocialSurvey) {
	var success = false;

	$('#survey-dash').show();
	$('#next-textarea-smiley').attr('data-survey-submit-disabled',true);
	
	var payload = {
		"mood" : mood,
		"feedback" : feedback,
		"agentId" : agentId,
		"customerEmail" : customerEmail,
		"firstName" : firstName,
		"lastName" : lastName,
		"isAbusive" : isAbusive,
		"agreedToShare" : agreedToShare,
		"isIsoEncoded" : isIsoEncoded,
		"surveyId" : surveyId,
		"agentName" : agentName,
		"onlyPostToSocialSurvey" : onlyPostToSocialSurvey,
		"agentProfileLink" : agentProfileLink
	};
	questionDetails.customerResponse = customerResponse;
	$.ajax({
		url : getLocationOrigin() + surveyUrl + "data/storeFeedback",
		type : "GET",
		cache : false,
		data : payload,
		dataType : "TEXT",
		success : function(data) {
			if (data != undefined)
				success = true;
			    rating = data;
		},
		complete : function(data) {
			if (success) {
				redirectPageUponSurveySubmit();				
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#next-textarea-smiley').attr('data-survey-submit-disabled',false);
		}
	});
}

function showFeedbackPage(mood) {
	$("div[data-ques-type]").hide();
	$("div[data-ques-type='smiley-text-final']").show();
	$('#text-box-disclaimer').show();
	$("#text-area").show();
	$("#text-area").val("");
	$("#smiles-final").hide();
	$("#next-textarea-smiley").html("Post My Review");
	$("#next-textarea-smiley").removeClass("btn-com-disabled");
	isSmileTypeQuestion = false;
	switch (mood) {
	case "Great":
		question = happyText;
		$("#ques-text-textarea").html(question);
		var currResponse = 0;
		var counter = 0;
		for (var i = 0; i < questions.length; i++) {
			var currQuestion = questions[i];
			if ((currQuestion.questionType == 'sb-range-smiles') || (currQuestion.questionType == 'sb-range-scale') || (currQuestion.questionType == 'sb-range-star') || (currQuestion.questionType == 'sb-range-0to10')) {
				if (!isNaN(parseInt(currQuestion.customerResponse))) {
					var responseCurrQuestion = parseInt(currQuestion.customerResponse);
					if(currQuestion.questionType == 'sb-range-0to10' &&	currQuestion.considerForScore == true){
						responseCurrQuestion = 	responseCurrQuestion/2;
						counter++;
						currResponse += responseCurrQuestion;
					}else if(currQuestion.questionType != 'sb-range-0to10'){
						counter++;
						currResponse += responseCurrQuestion;
					}
				}
			}
		}
		rating = currResponse / (counter);
		rating = parseFloat(rating).toFixed(3);
		$("#pst-srvy-div").show();
		$('#shr-pst-cb').val('true');
		$('#shr-post-chk-box').removeClass('bd-check-img-checked');
		break;
	case "OK":
		question = neutralText;
		$('#shr-pst-cb').val('false');// Update the agree to share checkbox false if mood is ok
		$('#shr-post-chk-box').addClass('bd-check-img-checked');
		$("#ques-text-textarea").html(question);
		break;
	case "Unpleasant":
		question = sadText;
		$('#shr-pst-cb').val('false');// Update the agree to share checkbox false if mood is unpleasant
		$('#shr-post-chk-box').addClass('bd-check-img-checked');
		$("#ques-text-textarea").html(question);
		break;
	}
	$("#prev-textarea-smiley").removeClass("btn-com-disabled");
}

/*
 * This is used to render all the possible choices for an MCQ.
 */
function paintMcqAnswer(answer) {
	var divToPopulate;
	customerResponse = questionDetails.customerResponse;
	if (answer == customerResponse) {
		divToPopulate = "<div data-answer='" + answer + "' class='sq-mcq-item clearfix'>" + "<div class='sq-mcq-chk-wrapper float-left'>" + "<div class='float-left sq-mcq-chk st-mcq-chk-on'>" + "</div>" + "<div class='float-left sq-mcq-chk st-mcq-chk-off hide'>" + "</div>" + "</div>" + "<div class='sq-mcq-ans-wrapper float-left'>" + answer + "</div></div>";
	} else {
		divToPopulate = "<div data-answer='" + answer + "' class='sq-mcq-item clearfix'>" + "<div class='sq-mcq-chk-wrapper float-left'>" + "<div class='float-left sq-mcq-chk st-mcq-chk-on hide'>" + "</div>" + "<div class='float-left sq-mcq-chk st-mcq-chk-off'>" + "</div>" + "</div>" + "<div class='sq-mcq-ans-wrapper float-left'>" + answer + "</div></div>";
	}
	return divToPopulate;
}

function paintListOptions(agentName) {
	var divToPopulate = "<option value='select'>--Select an Option--" + "<option value='transacted'>Transacted with " + agentName + "<option value='enquired'>Enquired with " + agentName;
	return divToPopulate;
}

function bindMcqCheckButton() {
	$('.st-mcq-chk-on').click(function() {
		$(this).hide();
		$(this).parent().find('.st-mcq-chk-off').show();
	});

	$('.st-mcq-chk-off').click(function() {
		customerResponse = $(this).parent().parent().attr('data-answer');
		$('.sq-mcq-wrapper').find('.st-mcq-chk-on').hide();
		$('.sq-mcq-wrapper').find('.st-mcq-chk-off').show();
		$(this).hide();
		$(this).parent().find('.st-mcq-chk-on').show();
	});
}

function paintRangeScale() {
	if (questionDetails.questionType == "sb-range-scale") {
		var value = parseInt(questionDetails.customerResponse);
		if (value == $('.sq-pts-red').html()) {
			$('.pts-hover-1').addClass('showHoverTab');
		} else if (value == $('.sq-pts-org').html()) {
			$('.pts-hover-2').addClass('showHoverTab');
		} else if (value == $('.sq-pts-lgreen').html()) {
			$('.pts-hover-3').addClass('showHoverTab');
		} else if (value == $('.sq-pts-military').html()) {
			$('.pts-hover-4').addClass('showHoverTab');
		} else if (value == $('.sq-pts-dgreen').html()) {
			$('.pts-hover-5').addClass('showHoverTab');
		}
	}
}

function showMasterQuestionPage() {
	if (isSmileTypeQuestion) {
		showFeedbackPage(mood);
	} else {
		// if ($('#pst-srvy-div').is(':visible'))
		// autoPost = $('#post-survey').is(":checked");
		reviewText  = $("#text-area").val(); //update global variable
		var feedback = $("#text-area").val();
		if (feedback == null || feedback == "") {
			$('#overlay-toast').html('Please enter feedback to continue');
			showToast();
			return;
		}

		//console.log(swearWords);
		var isAbusive = false;
		var feedbackArr = feedback.split(" ");
		for (var i = 0; i < feedbackArr.length; i++) {
			if ($.inArray((feedbackArr[i]).toLowerCase(), swearWords) != -1) {
				isAbusive = true;
			}
		}
		console.log("isAbusive:"+isAbusive);
		var onlyPostToSocialSurvey = true;
		if ($('#shr-post-chk-box').hasClass('bd-check-img-checked') == false ) {
			if (isAbusive == false) {
				onlyPostToSocialSurvey = false;
			}
		}
		
		// copy review text to clipboard
		var copyToClipBoard = $('#prof-container').attr('data-copy-to-clipboard');
		if(copyToClipBoard == 'true'){
			copyToClipboard( feedback, "Your feedback has been copied to clipboard" );
		}		
		
		if( isAbusive == false ){
			if (mood != 'Great') {
				$('#social-post-links').find('*').remove();
				$('#social-post-links').addClass('review-abusive-share-parent');
			}
			$('#social-post-links').show();

		}


		// Check character encoding
		var isIsoEncoded = false;
		try {
			feedback = decodeURIComponent(encodeURIComponent(feedback));
		} catch (err) {
			isIsoEncoded = true;
		}

		// save survey response
		updateCustomerResponse(feedback, $('#shr-pst-cb').val(), isAbusive, isIsoEncoded, onlyPostToSocialSurvey);
		
	}
	return;
}

function redirectPageUponSurveySubmit(){
	
	$("div[data-ques-type]").hide();
	
	if((mood == 'Great') && (happyUrl != null)){
		happyUrl = updateRedirectUrlWithValues(happyUrl);
		window.location = happyUrl;
	}
	else if((mood == 'OK') && (okUrl != null)){
		okUrl = updateRedirectUrlWithValues(okUrl);
		window.location = okUrl;
	}
	else if((mood == 'Unpleasant') && (sadUrl != null)){
		sadUrl = updateRedirectUrlWithValues(sadUrl);
		window.location = sadUrl;
	}
	else{	
		$("div[data-ques-type='error']").show();
		if(!hiddenSection){
			$('#profile-link').html('View ' + agentName + '\'s profile at <a href="' + agentFullProfileLink + "/" + surveyId + '" target="_blank">' + agentFullProfileLink + '</a>');
		}
		var feedback = $("#text-area").val();
		var fmt_rating = Number(rating).toFixed(1);
		$('#linkedin-btn').attr("href", "https://www.linkedin.com/shareArticle?mini=true&url=" + agentFullProfileLink + "/" + surveyId + "&title=&summary=" + fmt_rating + "-star response from " + firstName + " " + getInitials( lastName ) + " for " + agentName + " at SocialSurvey - " + feedback + ".&source=");
		var twitterFeedback = feedback;
		if (twitterFeedback.length > 180) {
			twitterFeedback = twitterFeedback.substring(0, 176);
			twitterFeedback = twitterFeedback + "...";
		} else {
			twitterFeedback = feedback;
		}
		$('#twitter-btn').attr("href", "https://twitter.com/intent/tweet?text=" + fmt_rating + "-star response from " + firstName + " " + getInitials( lastName ) + " for " + agentName + " at SocialSurvey - " + twitterFeedback + "&url='" + agentFullProfileLink+ "/" + surveyId + "'");
		$('#fb-btn').attr("href", "https://www.facebook.com/dialog/share?app_id=" + fb_app_id + "&href=" + agentFullProfileLink + "/" + surveyId + "&quote=" + fmt_rating + "-star response from " + firstName + " " + getInitials( lastName ) + " for " + agentName + " at SocialSurvey - " + feedback + "&redirect_uri=https://www.facebook.com");
	
		$('#content-head').html('Survey Completed');
		if (mood == 'Great')
			$('#content').html(happyTextComplete);
		else if (mood == 'OK')
			$('#content').html(neutralTextComplete);
		else
			$('#content').html(sadTextComplete);
		// $('#content').html("Congratulations! You have completed survey for " + agentName+ ".\nThanks for your participation.");
		
		// call method to post the review and update the review count
		//postToSocialMedia(feedback, isAbusive, onlyPostToSocialSurvey, isIsoEncoded);
	
		//paint socialmedia icons on survey thank you page
		paintSocialMediaIconsOnSurveyCompletion();
		$('#survey-dash').hide();
	}
}

function updateRedirectUrlWithValues(redirectUrl){
	var resultUrl = redirectUrl;
	if(redirectUrl.indexOf('[SURVEY_SOURCE_ID]') != -1)
		resultUrl = redirectUrl.replace('[SURVEY_SOURCE_ID]', surveySourceId);
	if(redirectUrl.indexOf('[PARTICIPANT_TYPE]') != -1)
		resultUrl = resultUrl.replace('[PARTICIPANT_TYPE]', participationtype);
	if(redirectUrl.indexOf('[TRANSACTION_TYPE]') != -1)
		resultUrl = resultUrl.replace('[TRANSACTION_TYPE]', transactionType);
	if(redirectUrl.indexOf('[STATE]') != -1)
		resultUrl = resultUrl.replace('[STATE]', state);
	if(redirectUrl.indexOf('[CITY]') != -1)
		resultUrl = resultUrl.replace('[CITY]', city);
	if(redirectUrl.indexOf('[CUSTOM_FIELD_ONE]') != -1)
		resultUrl = resultUrl.replace('[CUSTOM_FIELD_ONE]', customeField1);
	if(redirectUrl.indexOf('[CUSTOM_FIELD_TWO]') != -1)
		resultUrl = resultUrl.replace('[CUSTOM_FIELD_TWO]', customeField2);
	if(redirectUrl.indexOf('[CUSTOM_FIELD_THREE]') != -1)
		resultUrl = resultUrl.replace('[CUSTOM_FIELD_THREE]', customeField3);
	if(redirectUrl.indexOf('[CUSTOM_FIELD_FOUR]') != -1)
		resultUrl = resultUrl.replace('[CUSTOM_FIELD_FOUR]', customeField4);
	if(redirectUrl.indexOf('[CUSTOM_FIELD_FIVE]') != -1)
		resultUrl = resultUrl.replace('[CUSTOM_FIELD_FIVE]', customeField5);
		return resultUrl;
	
}

function postToSocialMedia(feedback, isAbusive, onlyPostToSocialSurvey, isIsoEncoded) {
	var success = false;
	var payload = {
		"agentId" : agentId,
		"firstName" : firstName,
		"lastName" : lastName,
		"agentName" : agentName,
		"rating" : rating,
		"isAbusive" : isAbusive,
		"customerEmail" : customerEmail,
		"feedback" : feedback,
		"agentProfileLink" : agentProfileLink,
		"onlyPostToSocialSurvey" : onlyPostToSocialSurvey,
		"isIsoEncoded" : isIsoEncoded,
		"surveyId" : surveyId
	};
	$.ajax({
		url : getLocationOrigin() + surveyUrl + "posttosocialnetwork",
		type : "GET",
		cache : false,
		dataType : "TEXT",
		data : payload,
		success : function(data) {
			success = true;
		},
		complete : function(data) {
			if (success) {

			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function updateSharedOn(socialSite, agentId, customerEmail) {
	var success = false;
	var payload = {
		"agentId" : agentId,
		"customerEmail" : customerEmail,
		"socialSite" : socialSite
	};
	$.ajax({
		url : getLocationOrigin() + surveyUrl + "updatesharedon",
		type : "GET",
		cache : false,
		dataType : "TEXT",
		data : payload,
		success : function(data) {
			success = true;
		},
		complete : function(data) {
			if (success) {

			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function reduceOpacityOfStars() {
	$('#sq-stars').find('.sq-star').each(function(index) {
		if (index < 5) {
			$(this).removeClass('sq-full-star-click');
			$(this).addClass('opacity-red');
		}
	});
}

function reduceOpacityOfSmiles() {
	$('#sq-smiles').find('.sq-smile').each(function(index) {
		if (index < 5) {
			$(this).removeClass('sq-full-smile-click');
			$(this).addClass('opacity-red');
		}
	});
}

function increaseOpacityOfStars(value) {
	$('#sq-stars').find('.sq-star').each(function(index) {
		if (index < value) {
			$(this).addClass('sq-full-star-click');
			$(this).removeClass('opacity-red');
		}
	});
}

function increaseOpacityOfSmiles(value) {
	$('#sq-smiles').find('.sq-smile').each(function(index) {
		if (index < value) {
			$(this).addClass('sq-full-smile-click');
			$(this).removeClass('opacity-red');
		}
	});
}

function clearForm() {
	$('#firstName').val('');
	$('#lastName').val('');
	$('#email').val('');
	$('#captcha-text').val('');
}

// Starting click events.

// Code to be executed on click of stars of rating question.
$('.sq-star').click(function() {
	$(this).parent().find('.sq-star').removeClass('sq-full-star');
	$(this).parent().find('.sq-star').removeClass('sq-full-star-click');
	var starVal = $(this).attr('star-no');
	$('#sq-stars').attr('selected-star-no' , starVal);
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < starVal) {
			$(this).removeClass('opacity-red');
			$(this).addClass('sq-full-star-click');
		} else {
			if (!$(this).hasClass('opacity-red'))
				$(this).addClass('opacity-red');
		}
	});
	if (qno != questions.length - 1) {
		$("#next-star").removeClass("btn-com-disabled");
	}
});

$('.sq-star').hover(function() {
	var smileVal = $(this).attr('star-no');
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < smileVal) {
			$(this).removeClass('opacity-red');
		}
	});
}, function() {
	var smileVal = $(this).attr('star-no');
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < smileVal) {
			if ($(this).hasClass('sq-full-star-click')) {
				$(this).removeClass('opacity-red');
			} else {
				$(this).addClass('opacity-red');
			}
		}
	});
});

// Code to be executed on click of next for all types of questions.
$('.sq-np-item-next').click(function() {

	if (questionDetails.questionType == "sb-sel-mcq" && customerResponse != undefined) {
			storeCustomerAnswer(customerResponse);
		} else if (questionDetails.questionType == "sb-sel-desc") {
			customerResponse = $("#text-area").val();
			if (customerResponse == undefined) {
				customerResponse = "";
			}
			storeCustomerAnswer(customerResponse);
		} else if (questionDetails.questionType == "sb-range-star") {
			reduceOpacityOfStars();
			if ($('#next-star').hasClass("btn-com-disabled") || $('#sq-stars').attr('selected-star-no') == 0) {
				$('#overlay-toast').html('Please answer the question. You can not skip a rating question.');
				showToast();
				return;
			}
			var starVal = $('#sq-stars').attr('selected-star-no');
			if(starVal > 5)
				starVal = Math.floor(starVal/2);
			storeCustomerAnswer(starVal);
		} else if (questionDetails.questionType == "sb-range-smiles" ) {
			reduceOpacityOfSmiles();
			if ($('#next-smile').hasClass("btn-com-disabled") || $('#sq-smiles').attr('selected-smiles-no') == 0) {
				$('#overlay-toast').html('Please answer the question. You can not skip a rating question.');
				showToast();
				return;
			}
			var smileVal = $('#sq-smiles').attr('selected-smiles-no');
			if(smileVal > 5)
				smileVal = Math.floor(smileVal/2);
			storeCustomerAnswer(smileVal);
		} else if (questionDetails.questionType == "sb-range-scale") {
			if ($('#next-scale').hasClass("btn-com-disabled")) {
				$('#overlay-toast').html('Please answer the question. You can not skip a rating question.');
				showToast();
				return;
			}
		} else if (questionDetails.questionType == "sb-master") {
			if ($('#next-textarea-smiley').hasClass("btn-com-disabled")) {
				$('#overlay-toast').html('Please answer this question.');
				showToast();
			} else if($('#next-textarea-smiley').attr('data-survey-submit-disabled') == true || $('#next-textarea-smiley').attr('data-survey-submit-disabled') == 'true'){
				return;
			}else {
				showMasterQuestionPage();
			}
			return;
		} else if (questionDetails.questionType == "sb-range-0to10"){
			if ($('#next-radio').hasClass("btn-com-disabled") && questionDetails.isNPSQuestion == 0) {
				$('#overlay-toast').html('Please answer the question. You can not skip a rating question.');
				showToast();
				return;
			}else if ($('#next-radio-nps').hasClass("btn-com-disabled") && questionDetails.isNPSQuestion == 1){
				$('#overlay-toast').html('Please answer the question. You can not skip a rating question.');
				showToast();
				return;
			}
			
			var ratingVal = 0;
			if(questionDetails.isNPSQuestion==0){
				ratingVal = parseInt($('#sq-radio-1to10').attr('selected-rating-radio'));
			}else{
				ratingVal = parseInt($('#sq-radio-1to10-nps').attr('selected-rating-radio'));
			}
			
			storeCustomerAnswer(ratingVal);
		} 
		
		$(".sq-star").removeClass('sq-full-star');
		$(".sq-smile").removeClass('sq-full-smile');
		$('.sq-radio').each(function() {
		    $(this).removeClass('radio-outer');
		    $(this).children().hide();
		    if(questionDetails.isNPSQuestion==0){
		    	$(this).addClass('radio-outer-gray');
		    	$(this).css("cursor","pointer");
		    }else{
		    	$(this).parent().find('.popover').hide();
		    	$(this).css("cursor","pointer");
		    	$('#nps-range-text').css('opacity',1);
		    }	
		});

	qno++;
	paintSurveyPageFromJson();

	if (questionDetails.questionType == "sb-range-star") {
		var starVal = parseInt(questionDetails.customerResponse);
		if(starVal > 5)
			starVal = Math.floor(starVal/2);
		if (!isNaN(starVal)) {
			$("#next-star").removeClass("btn-com-disabled");
			$('#sq-stars').find('.sq-star').each(function(index) {
				if (index < starVal) {
					$(this).addClass('sq-full-star-click');
					$(this).removeClass('opacity-red');
				}
			});
		}
	}
	if (questionDetails.questionType == "sb-range-smiles") {
		var smileVal = parseInt(questionDetails.customerResponse);
		if(smileVal > 5)
			smileVal = Math.floor(smileVal/2);
		if (!isNaN(smileVal)) {
			$("#next-smile").removeClass("btn-com-disabled");
			$('#sq-smiles').find('.sq-smile').each(function(index) {
				if (index < smileVal) {
					$(this).addClass('sq-full-smile-click');
					$(this).removeClass('opacity-red');
				}
			});
		}
	}
	if (questionDetails.questionType == "sb-range-scale") {
		$("#next-scale").removeClass("btn-com-disabled");
		paintRangeScale();
	}
	if (questionDetails.questionType == "sb-sel-mcq") {
		if (questionDetails.customerResponse == undefined || questionDetails.customerResponse == "")
			customerResponse = "";
	}
	
	if(questionDetails.questionType == "sb-range-0to10"){
		
		if(questionDetails.isNPSQuestion==1){
			$('.sq-radio').each(function() {
				$(this).removeClass('radio-outer-gray');
		    	$(this).parent().find('.popover').hide();
		    	$(this).css("cursor","pointer");
		    });
			$('#nps-range-text').css('opacity',1);
		}
		
		var ratingVal = parseInt(questionDetails.customerResponse);
		if(!isNaN(ratingVal)){
			$('.sq-radio').each(function() {
			    $(this).removeClass('radio-outer-gray');
			    $(this).children().hide();
			    if(questionDetails.isNPSQuestion==0){
			    	$(this).addClass('radio-outer');
			    	$(this).css("cursor","pointer");
			    }else{
			    	$(this).parent().find('.popover').hide();
			    	$(this).css("cursor","pointer");
			    }
			});
			
			if(questionDetails.isNPSQuestion==0){
				$('#radio-'+ratingVal).children().show();
				$('#radio-'+ratingVal).css("cursor","default");
				$('#sq-radio-1to10').attr('selected-rating-radio',ratingVal);
			}else{
				$('#radio-nps-'+ratingVal).children().show();
				$('#radio-nps-'+ratingVal).parent().find('.popover').show();
				$('#radio-nps-'+ratingVal).css("cursor","default");
				$('#nps-range-text').css('opacity',0);
				$('#sq-radio-1to10-nps').attr('selected-rating-radio',ratingVal);
			}
			
		}
		
		if(questionDetails.isNPSQuestion==0){
			$('#notAtAllLikelyDiv').html(questionDetails.notAtAllLikely);
			$('#veryLikelyDiv').html(questionDetails.veryLikely);
		}else{
			$('#notAtAllLikelyDivNps').html(questionDetails.notAtAllLikely);
			$('#veryLikelyDivNps').html(questionDetails.veryLikely);
		}	
	}
});

// Code to be executed on click of previous for star and smile questions.
$('.sq-np-item-prev').click(function() {
	$("#pst-srvy-div").hide();
	if (qno == 0) {
		return;
	}
	$("#next-textarea-smiley").html("Next");
	$(".sq-star").removeClass('sq-full-star');
	$(".sq-smile").removeClass('sq-full-smile');
	if (isSmileTypeQuestion)
		qno--;
	isSmileTypeQuestion = true;
	paintSurveyPageFromJson();
	if (questionDetails.questionType == "sb-range-star") {
		reduceOpacityOfStars();
		var starVal = parseInt(questionDetails.customerResponse);
		if(starVal > 5)
			starVal = Math.floor(starVal/2);
		$('#sq-stars').find('.sq-star').each(function(index) {
			if (index < starVal) {
				$(this).addClass('sq-full-star-click');
				$(this).removeClass('opacity-red');
			}
		});
	}
	paintRangeScale();
	if (questionDetails.questionType == "sb-range-smiles") {
		reduceOpacityOfSmiles();
		var starVal = parseInt(questionDetails.customerResponse);
		if(starVal > 5)
			starVal = Math.floor(starVal/2);
		$('#sq-smiles').find('.sq-smile').each(function(index) {
			if (index < starVal) {
				$(this).addClass('sq-full-smile-click');
				$(this).removeClass('opacity-red');
			}
		});
	}
	if (questionDetails.questionType == "sb-range-scale") {
		var value = parseInt(questionDetails.customerResponse);
		$('#range-slider-value').html(value);
	}
	if (questionDetails.questionType == "sb-sel-desc") {
		var val = questionDetails.customerResponse;
		if (val != undefined) {
			$("#text-area").val(val);
		}
	}
	
	if(questionDetails.questionType == "sb-range-0to10"){
		var ratingVal = parseInt(questionDetails.customerResponse);
		if(questionDetails.isNPSQuestion==0){
			if(!isNaN(ratingVal)){
				$('.sq-radio').each(function() {
				    $(this).removeClass('radio-outer-gray');
				    $(this).children().hide();
				    $(this).addClass('radio-outer');
				    $(this).css("cursor","pointer");
				});
				$('#radio-'+ratingVal).children().show();
				$('#sq-radio-1to10').attr('selected-rating-radio',ratingVal);
				$('#radio-'+ratingVal).css("cursor","default");
			}
			$('#notAtAllLikelyDiv').html(questionDetails.notAtAllLikely);
			$('#veryLikelyDiv').html(questionDetails.veryLikely);	
		} else{
			if(!isNaN(ratingVal)){
				$('.sq-radio').each(function() {
				    $(this).children().hide();
				    $(this).parent().find('.popover').hide();
				    $(this).css("cursor","pointer");
				});
				$('#radio-nps-'+ratingVal).children().show();
				$('#radio-nps-'+ratingVal).parent().find('.popover').show();
				$('#radio-nps-'+ratingVal).css("cursor","default");
				$('#nps-range-text').css('opacity',0);
				$('#sq-radio-1to10-nps').attr('selected-rating-radio',ratingVal);
			}
			$('#notAtAllLikelyDivNps').html(questionDetails.notAtAllLikely);
			$('#veryLikelyDivNps').html(questionDetails.veryLikely);
		}
		
	}
	
	$("#next-star").removeClass("btn-com-disabled");
	$("#next-smile").removeClass("btn-com-disabled");
	$("#next-scale").removeClass("btn-com-disabled");
	$("#next-radio").removeClass("btn-com-disabled");
	$("#next-radio-nps").removeClass("btn-com-disabled");
	$("#next-textarea-smiley").removeClass("btn-com-disabled");
});

$('.sq-radio').click(function(){
	$('.sq-radio').each(function() {
	    $(this).removeClass('radio-outer-gray');
	    $(this).children().hide();
	    if(questionDetails.isNPSQuestion == 0){
	    	$(this).addClass('radio-outer');
	    	$(this).css("cursor","pointer");
	    }else{
	    	$(this).parent().find('.popover').hide();
	    	$(this).css("cursor","pointer");
	    }
	});
	$(this).removeClass('radio-outer-gray');
	$(this).children().show();
	$(this).parent().find('.popover').show();
	$(this).css("cursor","default");
	if(questionDetails.isNPSQuestion == 0){
		$('#sq-radio-1to10').attr('selected-rating-radio',$(this).attr('id').split('-').pop());
		if (qno != questions.length - 1) {
			$("#next-radio").removeClass("btn-com-disabled");
		}
	}else{
		$('#sq-radio-1to10-nps').attr('selected-rating-radio',$(this).attr('id').split('-').pop());
		if (qno != questions.length - 1) {
			$("#next-radio-nps").removeClass("btn-com-disabled");
		}
		$('#nps-range-text').css('opacity',0);
	}
	
});

/* Click event on grey smile. */
$('.sq-smile').click(function() {
	$(this).parent().find('.sq-smile').removeClass('sq-full-smile');
	$(this).parent().find('.sq-smile').removeClass('sq-full-smile-click');
	var smileVal = $(this).attr('smile-no');
	$('#sq-smiles').attr('selected-smiles-no' , smileVal);
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			$(this).removeClass('opacity-red');
			$(this).addClass('sq-full-smile-click');
		} else {
			if (!$(this).hasClass('opacity-red'))
				$(this).addClass('opacity-red');
		}
	});
	if (qno != questions.length - 1) {
		$("#next-smile").removeClass("btn-com-disabled");
	}
	$("#next-star").removeClass("btn-com-disabled");
});

$('.sq-smile').hover(function() {
	var smileVal = $(this).attr('smile-no');
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			$(this).removeClass('opacity-red');
		}
	});
}, function() {
	var smileVal = $(this).attr('smile-no');
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			if ($(this).hasClass('sq-full-smile-click')) {
				$(this).removeClass('opacity-red');
			} else {
				$(this).addClass('opacity-red');
			}
		}
	});
});

$('#sq-happy-smile').click(function() {
	// Update customer's mood in db and ask for cutomer's kind words.
	mood = "Great";
	$('#next-textarea-smiley').removeClass("btn-com-disabled");
	isSmileTypeQuestion = true;
	$(this).parent().find('.sq-smile-icn-container').addClass('opacity-red');
	$(this).removeClass('opacity-red');
});
$('#sq-neutral-smile').click(function() {
	// Update customer's mood in db and ask for feedback that could have made
	// him happy.
	mood = "OK";
	$('#next-textarea-smiley').removeClass("btn-com-disabled");
	isSmileTypeQuestion = true;
	$(this).parent().find('.sq-smile-icn-container').addClass('opacity-red');
	$(this).removeClass('opacity-red');
});
$('#sq-sad-smile').click(function() {
	// Update customer's mood in db and ask what went wrong during the entire
	// course.
	mood = "Unpleasant";
	$('#next-textarea-smiley').removeClass("btn-com-disabled");
	isSmileTypeQuestion = true;
	$(this).parent().find('.sq-smile-icn-container').addClass('opacity-red');
	$(this).removeClass('opacity-red');
});

$(document).on('input','.sq-txt-area',function(){
});

/*
 * $('input[type="range"]').rangeslider({ polyfill : false, // Default CSS classes rangeClass : 'rangeslider', fillClass : 'rangeslider__fill', handleClass : 'rangeslider__handle',
 * 
 * onSlide : function(position, value) { $('#range-slider-value').html(value); }, // Callback function onSlideEnd : function(position, value) { $('#range-slider-value').html(value); storeCustomerAnswer(value); }, });
 */

$('.sq-pts-red').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-1').addClass('showHoverTab');
	var answer = $('.sq-pts-red').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-org').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-2').addClass('showHoverTab');
	var answer = $('.sq-pts-org').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-lgreen').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-3').addClass('showHoverTab');
	var answer = $('.sq-pts-lgreen').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-military').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-4').addClass('showHoverTab');
	var answer = $('.sq-pts-military').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-dgreen').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-5').addClass('showHoverTab');
	var answer = $('.sq-pts-dgreen').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('#ggl-btn').click(function(e) {
	updateSharedOn("google", agentId, customerEmail);
});

$('#shr-post-chk-box').click(function() {
	if ($('#shr-post-chk-box').hasClass('bd-check-img-checked')) {
		$('#shr-post-chk-box').removeClass('bd-check-img-checked');
		$('#shr-pst-cb').val('true');
		autoPost = true;
	} else {
		$('#shr-post-chk-box').addClass('bd-check-img-checked');
		$('#shr-pst-cb').val('false');
		autoPost = false;
	}
});

// Edit profile functions
$(document).ajaxStop(function() {
	var hashString = document.baseURI.split('#')[1];
	if(hashString != "showprofilepage"){
		$('.footer-main-wrapper').show();
		return;
	}
	adjustImage();
});

// Toggle text editor
$(document).on('focus', '.prof-edditable', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).length == 0) {
		$(this).addClass('prof-name-edit');
	}
	if ($('#' + lockId).attr('data-control') == 'user' || ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]'))) {
		$(this).addClass('prof-name-edit');
		$('#prof-all-lock').val('modified');
	}
});

$(document).on('blur', '.prof-edditable', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user' || ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]'))) {
		$(this).removeClass('prof-name-edit');
	}
});

$(document).on('focus', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user' || ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]'))) {
		$(this).addClass('prof-name-edit');
		$('#prof-all-lock').val('modified');
	}
});

$(document).on('blur', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user' || ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]'))) {
		$(this).removeClass('prof-name-edit');
	}

});


// On hover for lock icons
$(document).on('mouseover', '#prof-logo-container', function(e) {
	$(this).find('.prof-img-lock:first').show();
});
$(document).on('mouseout', '#prof-logo-container', function(e) {
	$(this).find('.prof-img-lock:first').hide();
});

$(document).on('mouseover', '#prof-name-container', function(e) {
	$(this).find('.lp-edit-locks:first').show();
});
$(document).on('mouseout', '#prof-name-container', function(e) {
	$(this).find('.lp-edit-locks:first').hide();
});

$(document).on('mouseover', '.lp-con-row', function(e) {
	$(this).find('.lp-edit-locks:first').show();
});
$(document).on('mouseout', '.lp-con-row', function(e) {
	$(this).find('.lp-edit-locks:first').hide();
});

// Lock Settings
$(document).on('click', '.lp-edit-locks', function(e) {
	e.stopImmediatePropagation();
	var lockId = $(this).attr("id");

	if ($(this).attr('data-control') == 'user') {
		if ($(this).hasClass('lp-edit-locks-locked')) {
			updateLockSettings(lockId, false);
		} else {
			updateLockSettings(lockId, true);
		}
	} else {
		$('#overlay-toast').html("Settings locked by Admin");
		showToast();
	}
});

$(document).on('click', '.prof-img-lock-item', function(e) {
	e.stopImmediatePropagation();
	var lockId = $(this).attr("id");

	if ($(this).attr('data-control') == 'user') {
		if ($(this).hasClass('prof-img-lock-locked')) {
			updateLockSettings(lockId, false);
		} else {
			updateLockSettings(lockId, true);
		}
	} else {
		$('#overlay-toast').html("Settings locked by Admin");
		showToast();
	}
});

function updateLockSettings(id, state) {
	if (id == undefined || id == "") {
		return;
	}

	delay(function() {
		var payload = {
			"id" : id,
			"state" : state
		};
		callAjaxPostWithPayloadData("./updatelocksettings.do", function(data) {
			$('#prof-message-header').html(data);
			if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
				if (state == false) {
					$('#' + id).removeClass('lp-edit-locks-locked');
					$('#' + id).removeClass('prof-img-lock-locked');
					$('#' + id).attr('data-state', 'unlocked');
				} else if (state == true) {
					$('#' + id).addClass('lp-edit-locks-locked');
					$('#' + id).addClass('prof-img-lock-locked');
					$('#' + id).attr('data-state', 'locked');
				}
			}

			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload, true);
	}, 0);
}

// Update AboutMe details
function callBackShowAboutMe(data) {
	$('#intro-about-me').html(data);
	adjustImage();
}

$(document).on('click', '#intro-body-text', function() {
	if ($('#aboutme-lock').attr('data-state') == 'unlocked') {
		$(this).hide();
		var textContent = $(this).text().trim();
		if ($('#aboutme-status').val() == 'new') {
			textContent = "";
		}
		$('#intro-body-text-edit').val(textContent);
		$('#intro-body-text-edit').show();
		$('#intro-body-text-edit').focus();
	}
});

$(document).on('blur', '#intro-body-text-edit', function() {
	if ($('#aboutme-lock').attr('data-state') == 'unlocked') {

		var aboutMe = $('#intro-body-text-edit').val().trim();
		if (aboutMe == undefined || aboutMe == "") {
			$('#overlay-toast').html("Please add a few words about you");
			showToast();
			return;
		}
		delay(function() {
			var payload = {
				"aboutMe" : aboutMe
			};
			callAjaxPostWithPayloadData("./addorupdateaboutme.do", callBackOnEditAdboutMeDetails, payload, true);
		}, 0);
	}
});

function callBackOnEditAdboutMeDetails(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		$('#intro-body-text-edit').hide();
		var textContent = $('#intro-body-text-edit').val().trim();
		$('#intro-body-text').text(textContent);
		$('#intro-body-text').show();
	} else {
		$('#intro-body-text-edit').hide();
		$('#intro-body-text').show();
	}

	if ($('#aboutme-status').val() == 'new') {
		callAjaxGET("./fetchaboutme.do", function(data) {
			$('#intro-about-me').html(data);
		}, true);
	}

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Update Contact details
function callBackShowContactDetails(data) {
	$('#contant-info-container').html(data);
	adjustImage();
}
var phoneFormatWithExtension = '(ddd) ddd-dddd x yyyyy';
var usPhoneRegEx = {
	'translation' : {
		d : {
			pattern : /[0-9*]/
		},
		y : {
			pattern : /[0-9*]/
		}
	}
};

var countryPhone = /^[0-9-.()x ]+$/;

// Phone numbers in contact details
$(document).on('blur', '#contant-info-container input[data-phone-number]', function() {
	if ($('#prof-all-lock').val() != 'modified' || !$(this).val() || $(this).is('[readonly]')) {
		return;
	}
	if (!countryPhone.test(this.value)) {
		$('#overlay-toast').html("Please add a valid phone number");
		showToast();
		return;
	}

	delay(function() {
		var phoneNumbers = [];
		$('#contant-info-container input[data-phone-number]').each(function() {
			if (this.value != "" && (countryPhone.test(this.value)) && !$(this).is('[readonly]')) {
				var phoneNumber = {};
				phoneNumber.key = $(this).attr("data-phone-number");
				if (phoneNumber.key == 'work') {
					phoneNumber.value = JSON.stringify(getPhoneNumber('phone-number-work'));
				} else if (phoneNumber.key == 'personal') {
					phoneNumber.value = JSON.stringify(getPhoneNumber('phone-number-personal'));
				} else {
					phoneNumber.value = this.value;
				}
				phoneNumbers.push(phoneNumber);
			}
		});
		phoneNumbers = JSON.stringify(phoneNumbers);
		var payload = {
			"phoneNumbers" : phoneNumbers
		};
		callAjaxPostWithPayloadData("./updatephonenumbers.do", callBackOnUpdatePhoneNumbers, payload, true);
	}, 0);
});

function callBackOnUpdatePhoneNumbers(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails, true);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Function to update web addresses in contact details
$(document).on('blur', '#contant-info-container input[data-web-address]', function() {
	if(!$(this).val()){
		delay(function() {
			callAjaxPOST("./unsetwebapp.do", callBackOnUpdateWebAddresses, true);
		}, 0);
		$('#overlay-toast').html("Web address has been removed");
		showToast();
		return;
	}

	if ($('#prof-all-lock').val() != 'modified' || $(this).is('[readonly]')) {
		return;
	}
	if (!isValidUrl($(this).val().trim())) {
		$('#overlay-toast').html("Please add a valid web address");
		showToast();
		return;
	}

	delay(function() {
		var webAddresses = [];
		var i = 0;
		var webAddressValid = true;
		$('#contant-info-container input[data-web-address]').each(function() {
			var link = $.trim(this.value);
			if (link != "") {
				if (isValidUrl(link) && !$(this).is('[readonly]')) {
					var webAddress = {};
					webAddress.key = $(this).attr("data-web-address");
					webAddress.value = link;
					webAddresses[i++] = webAddress;
				} else {
					return;
					$(this).focus();
					webAddressValid = false;
				}
			}
		});
		if (!webAddressValid) {
			//alert("Invalid web address");
			return false;
		}
		webAddresses = JSON.stringify(webAddresses);
		var payload = {
			"webAddresses" : webAddresses
		};
		callAjaxPostWithPayloadData("./updatewebaddresses.do", callBackOnUpdateWebAddresses, payload, true);
	}, 0);
});

function callBackOnUpdateWebAddresses(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails, true);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Update Address detail
function callBackShowAddressDetails(data) {
	$('#prof-address-container').html(data);
	adjustImage();
}

function showEditAddressPopup() {
	callAjaxGET("./fetchaddressdetailsedit.do", callBackEditAddressDetails, true);
}

function callBackEditAddressDetails(data) {

	var header = "Edit Address Detail";
	createEditAddressPopup(header, data);

	// update events
	updateEventsEditAddress();

	$('#overlay-continue').click(function() {
		var isFocussed = false;
		var profName = $('#prof-name').val();
		var profAddress1 = $('#prof-address1').val();

		// var profAddress2 = $('#prof-address2').val();
		var country = $('#prof-country').val();
		var zipCode = $('#prof-zipcode').val();
		if (!validateAddress1('prof-address1', true)) {

			if (!isFocussed) {
				$('#prof-address1').focus();
				isFocussed = true;
			}
			return;
		}
		if (!validateCountryProfile(country)) {

			if (!isFocussed) {
				$('#prof-country').focus();
				isFocussed = true;
			}
			return;
		}
		if (!validateCountryZipcode('prof-zipcode', true)) {

			if (!isFocussed) {
				$('#prof-zipcode').focus();
				isFocussed = true;
			}
			return;
		}

		delay(function() {
			payload = $('#prof-edit-address-form').serialize();
			
			//data attr for gmb connection
			var contactDetailsObj = unserializeFormData(payload);
			$('#gmb-data').attr('data-city',contactDetailsObj.city);
			$('#gmb-data').attr('data-state',contactDetailsObj.state);
			$('#gmb-data').attr('data-country',contactDetailsObj.country);
			
			callAjaxPostWithPayloadData("./updateprofileaddress.do", callBackUpdateAddressDetails, payload, true);
		}, 0);

		$('#overlay-continue').unbind('click');
	});

	$('.overlay-disable-wrapper').addClass('pu_arrow_rt');
	disableBodyScroll();
	$('body').scrollTop('0');
}

function unserializeFormData(data) {
    var objs = [], temp;
    var temps = data.split('&');

    for(var i = 0; i < temps.length; i++){
        temp = temps[i].split('=');
        objs.push(temp[0]);
        objs[temp[0]] = temp[1]; 
    }
    return objs; 
}

// Function to update events on edit profile page
function updateEventsEditAddress() {
	var countryCode = $('#prof-country-code').val();
	if (countryCode == "US") {
		showStateCityRow('prof-address-state-city-row', 'prof-state', 'prof-city');
		selectedCountryRegEx = "^" + "\\b\\d{5}\\b(?:[- ]{1}\\d{4})?" + "$";
		selectedCountryRegEx = new RegExp(selectedCountryRegEx);
	} else {
		hideStateCityRow('prof-address-state-city-row', 'prof-state');
	}

	attachAutocompleteCountry('prof-country', 'prof-country-code', 'prof-state', 'prof-address-state-city-row', 'prof-city');
}

function callBackUpdateAddressDetails(data) {
	$('body').css('overflow', 'auto');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchbasicdetails.do", callBackShowBasicDetails, true);
	callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails, true);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails, true);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	overlayRevert();
}

$('#overlay-cancel').click(function() {
	$('#overlay-continue').unbind('click');
	$('body').css('overflow', 'auto');
	overlayRevert();
	$('#prof-image').val('');
});

function createEditAddressPopup(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Ok");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-main').show();
}
function overlayRevert() {
	$('#overlay-main').hide();
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');

	$('#overlay-continue').unbind('click');

	enableBodyScroll();
	$('.overlay-disable-wrapper').removeClass('pu_arrow_rt');

	$("#overlay-pop-up").removeClass("overlay-disable-wrapper-zillow");
	$('#zillow-popup').hide();
	$('#zillow-popup-body').html('');
	$('#disconnect-overlay-main').hide();
}

// Update Basic detail
function callBackShowBasicDetails(response) {
	$('#prof-basic-container').html(response);
	adjustImage();
}

$(document).on('blur', '#prof-basic-container input', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).length > 0) {
		// if ($('#prof-all-lock').val() != 'modified' || !$(this).val()) {
		if ($('#prof-all-lock').val() != 'modified') {
			return;
		}
	} else {
		if (!$(this).val()) {
			return;
		}
	}

	delay(function() {
		var profName = $('#prof-name').val().trim();
		var profTitle = $('#prof-title').val().trim();
		var payload = {
			"profName" : profName,
			"profTitle" : profTitle
		};
		if ($('#prof-vertical').val()) {
			payload["profVertical"] = $('#prof-vertical').val().trim();
		}
		if ($('#prof-location').val()) {
			payload["profLocation"] = $('#prof-location').val().trim();
		}

		callAjaxPostWithPayloadData("./updatebasicprofile.do", callBackUpdateBasicDetails, payload, true);
	}, 0);
});

function callBackUpdateBasicDetails(data) {
	var profileMasterId = $('#gmb-data').attr('data-profile-master-id');
	if(profileMasterId == 1){
		var companyName = $('#prof-name').val().trim();
		$('#gmb-data').attr('data-companyName',companyName);
	}
	
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Function to update profile logo image
function callBackShowProfileLogo(data) {
	$('#prof-logo-container').html(data);
	var logoImageUrl = $('#prof-logo-edit').css("background-image");
	if (logoImageUrl == undefined || logoImageUrl == "none") {
		return;
	}

	// update logo if it is company admin or it does not have logo
	if ($('#header-user-info').find('.user-info-logo').length <= 0 || colName == "companyId") {
		var userInfoDivider = $('<div>').attr({
			"class" : "float-left user-info-seperator"
		});
		var userInfoLogo = $('<div>').attr({
			"class" : "float-left user-info-logo"
		}).css({
			"background" : logoImageUrl + " no-repeat center",
			"background-size" : "contain"
		});
		$('#header-user-info').find('.user-info-logo').remove();
		$('#header-user-info').append(userInfoDivider).append(userInfoLogo);
	}
	adjustImage();
	hideOverlay();
}

$(document).on('change', '#prof-logo', function() {

	if (!logoValidate('#prof-logo')) {
		console.log("inside log");
		return false;
	}
	showOverlay();
	var formData = new FormData();
	formData.append("logo", $(this).prop("files")[0]);
	formData.append("logoFileName", $(this).prop("files")[0].name);
	delay(function() {
		callAjaxPOSTWithTextData("./updatelogo.do", function(data) {
			$('#prof-message-header').html(data);
			callAjaxGET("./fetchprofilelogo.do", callBackShowProfileLogo, true);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, false, formData);
	}, 1000);
});


// Function to crop and upload profile image
function callBackOnProfileImageUpload(data) {
	
	$('#prof-message-header').html(data);
	
	if( $('#new-dash-flag').val() == "true" ){
		
		$('#overlay-toast').html($('#display-msg-div').text().trim());
		showToast();
		
		callAjaxGET("./fetchprofileimagefornewdashboard.do", function(data) {
			hideOverlay();
			$('.rep-prof-pic-circle').html(data);
		}, true);
		
		$('#prof-message-header').html('');
		
	} else {
		
		$('#prof-message-header').html('');
		if ($('#overlay-linkedin-import').is(":visible")) {
			$('#message-header').html(data);
			callAjaxGET("./fetchuploadedprofileimage.do", function(profileImageUrl) {
				if (profilemasterid == 4) {
					$("#wc-photo-upload").removeClass('dsh-pers-default-img');
				} else if (profilemasterid == 3) {
					$("#wc-photo-upload").removeClass('dsh-office-default-img');
				} else if (profilemasterid == 2) {
					$("#wc-photo-upload").removeClass('dsh-region-default-img');
				} else if (profilemasterid == 1) {
					$("#wc-photo-upload").removeClass('dsh-comp-default-img');
				}
	
				$('#wc-photo-upload').css("background", "url(" + profileImageUrl + ") no-repeat center");
				$('#wc-photo-upload').css("background-size", "contain");
				hideOverlay();
			}, true);
	
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		} else {
	
			$('#prof-message-header').html(data);
			callAjaxGET("./fetchprofileimage.do", function(data) {
				$('#prof-img-container').html(data);
				var profileImageUrl = $('#prof-image-edit').css("background-image");
				if (profileImageUrl == undefined || profileImageUrl == "none") {
					return;
				}
				adjustImage();
				hideOverlay();
			}, true);
	
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
			loadDisplayPicture();
		}
	}
}

// Agent details
$(document).on('focus', '.prof-edditable-sin-agent', function() {
	$(this).addClass('prof-name-edit');
});

$(document).on('input', '.prof-edditable-sin-agent', function() {
	$(this).attr('data-status', 'edited');
});

$(document).on('blur', '.prof-edditable-sin-agent', function() {
	$(this).removeClass('prof-name-edit');
});

$(document).on('mouseover', '.lp-dummy-row', function() {
	$(this).children().last().removeClass('hide');
});
$(document).on('mouseout', '.lp-dummy-row', function() {
	$(this).children().last().addClass('hide');
});

// remove agent details
$(document).on('click', '.lp-ach-item-img', function(e) {
	e.stopPropagation();
	$(this).prev().attr('data-status', 'removed');

	var type = $(this).data('type');
	$(this).parent().hide();

	if (type == 'association') {
		updateAssociations();
	} else if (type == 'achievement') {
		updateAchievements();
	} else if (type == 'license') {
		updateLicenseAuthorizations();
	} else if (type == 'expertise') {
		updateExpertise();
	} else if (type == 'hobby') {
		updateHobbies();
	}

});

// Function to update association/membership list
function addAnAssociation() {
	if ($('#association-container > div').length <= 0) {
		$('#association-container').empty();
	}

	var newDiv = $('<div>').attr({
		"class" : "lp-dummy-row clearfix"
	});
	$('#association-container').append(newDiv);

	var newAssociation = $('<input>').attr({
		"class" : "lp-assoc-row lp-row clearfix prof-edditable-sin-agent",
		"placeholder" : "New Association",
		"data-status" : "new"
	});
	var newAssociationButton = $('<div>').attr({
		"class" : "lp-ach-item-img hide",
		"data-type" : "association"
	});

	$('#association-container').children().last().append(newAssociation);
	$('#association-container').children().last().append(newAssociationButton);
	newAssociation.focus();
	newAssociationButton.addClass('float-right');
}

$(document).on('blur', '#association-container input', function(e) {
	e.stopPropagation();
	delay(function() {
		updateAssociations();
	}, 0);
});

function updateAssociations() {
	var associationList = [];
	var statusEdited = false;

	$('#association-container').children().each(function() {
		var status = $(this).children().first().attr('data-status');
		var value = $(this).children().first().val();
		if (value != "" && status != 'removed') {
			var association = {};
			association.name = value;
			associationList.push(association);

			statusEdited = true;
		} else if (status == 'removed') {
			statusEdited = true;
		}
	});
	if (!statusEdited) {
		return;
	}

	associationList = JSON.stringify(associationList);
	var payload = {
		"associationList" : associationList
	};
	callAjaxPostWithPayloadData("./updateassociations.do", callBackUpdateAssociations, payload, true);
}

function callBackUpdateAssociations(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#association-container').find('input').length) {
		$('#association-container').append('<span>No association added yet</span>');
	}
}

// Function to update achievement list
function addAnAchievement() {
	if ($('#achievement-container > div').length <= 0) {
		$('#achievement-container').empty();
	}

	var newDiv = $('<div>').attr({
		"class" : "lp-dummy-row clearfix"
	});
	$('#achievement-container').append(newDiv);

	var newAchievement = $('<input>').attr({
		"class" : "lp-ach-row lp-row clearfix prof-edditable-sin-agent",
		"placeholder" : "New Achievement",
		"data-status" : "new"
	});
	var newAchievementButton = $('<div>').attr({
		"class" : "lp-ach-item-img hide",
		"data-type" : "achievement"
	});

	$('#achievement-container').children().last().append(newAchievement);
	$('#achievement-container').children().last().append(newAchievementButton);
	newAchievement.focus();
	newAchievementButton.addClass('float-right');
}

$(document).on('blur', '#achievement-container input', function(e) {
	e.stopPropagation();
	delay(function() {
		updateAchievements();
	}, 0);
});

function updateAchievements() {
	var achievementList = [];
	var statusEdited = false;

	$('#achievement-container').children().each(function() {
		var status = $(this).children().first().attr('data-status');
		var value = $(this).children().first().val();
		if (value != "" && status != 'removed') {
			var achievement = {};
			achievement.achievement = value;
			achievementList.push(achievement);

			statusEdited = true;
		} else if (status == 'removed') {
			statusEdited = true;
		}
	});
	if (!statusEdited) {
		return;
	}

	achievementList = JSON.stringify(achievementList);
	var payload = {
		"achievementList" : achievementList
	};
	callAjaxPostWithPayloadData("./updateachievements.do", callBackUpdateAchievements, payload, true);
}

function callBackUpdateAchievements(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#achievement-container').find('input').length) {
		$('#achievement-container').append('<span>No achievement added yet</span>');
	}
}

// Function to update License authorizations
function addAuthorisedIn() {
	if ($('#authorised-in-container > div').length <= 0) {
		$('#authorised-in-container').empty();
	}

	var newDiv = $('<div>').attr({
		"class" : "lp-dummy-row clearfix"
	});
	$('#authorised-in-container').append(newDiv);

	var newAuthorisation = $('<input>').attr({
		"class" : "lp-auth-row lp-row clearfix prof-edditable-sin-agent",
		"placeholder" : "Authorized in",
		"data-status" : "new"
	});
	var newAuthorizationButton = $('<div>').attr({
		"class" : "lp-ach-item-img hide",
		"data-type" : "license"
	});

	$('#authorised-in-container').children().last().append(newAuthorisation);
	$('#authorised-in-container').children().last().append(newAuthorizationButton);
	newAuthorisation.focus();
	newAuthorizationButton.addClass('float-right');
}

$(document).on('blur', '#authorised-in-container input', function(e) {
	e.stopPropagation();
	delay(function() {
		updateLicenseAuthorizations();
	}, 0);
});

function updateLicenseAuthorizations() {
	var licenceList = [];
	var statusEdited = false;

	$('#authorised-in-container').children().each(function() {
		var status = $(this).children().first().attr('data-status');
		var value = $(this).children().first().val();
		if (value != "" && status != 'removed') {
			var licence = value;
			licenceList.push(licence);

			statusEdited = true;
		} else if (status == 'removed') {
			statusEdited = true;
		}
	});
	if (!statusEdited) {
		return;
	}

	licenceList = JSON.stringify(licenceList);
	var payload = {
		"licenceList" : licenceList
	};
	callAjaxPostWithPayloadData("./updatelicenses.do", callBackUpdateLicenseAuthorizations, payload, true);
}

function callBackUpdateLicenseAuthorizations(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#authorised-in-container').find('input').length) {
		$('#authorised-in-container').append('<span>No license added yet</span>');
	}
}

// TODO:Postions

// Function to update Expertise
function addExpertise() {
	if ($('#expertise-container > div').length <= 0) {
		$('#expertise-container').empty();
	}

	var newDiv = $('<div>').attr({
		"class" : "lp-dummy-row clearfix"
	});
	$('#expertise-container').append(newDiv);

	var newExpertise = $('<input>').attr({
		"class" : "lp-expertise-row lp-row clearfix prof-edditable-sin-agent",
		"placeholder" : "Add Skill",
		"data-status" : "new"
	});
	var newExpertiseButton = $('<div>').attr({
		"class" : "lp-ach-item-img hide",
		"data-type" : "expertise"
	});

	$('#expertise-container').children().last().append(newExpertise);
	$('#expertise-container').children().last().append(newExpertiseButton);
	newExpertise.focus();
	newExpertiseButton.addClass('float-right');
}

$(document).on('blur', '#expertise-container input', function(e) {
	e.stopPropagation();
	delay(function() {
		updateExpertise();
	}, 0);
});

function updateExpertise() {
	var expertiseList = [];
	var statusEdited = false;

	$('#expertise-container').children().each(function() {
		var status = $(this).children().first().attr('data-status');
		var value = $(this).children().first().val();
		if (value != "" && status != 'removed') {
			var licence = value;
			expertiseList.push(licence);

			statusEdited = true;
		} else if (status == 'removed') {
			statusEdited = true;
		}
	});
	if (!statusEdited) {
		return;
	}

	expertiseList = JSON.stringify(expertiseList);
	var payload = {
		"expertiseList" : expertiseList
	};
	callAjaxPostWithPayloadData("./updateexpertise.do", callBackUpdateExpertise, payload, true);
}

function callBackUpdateExpertise(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#expertise-container').find('input').length) {
		$('#expertise-container').append('<span>No license added yet</span>');
	}
}

// Function to update Hobbies
function addHobby() {
	if ($('#hobbies-container > div').length <= 0) {
		$('#hobbies-container').empty();
	}

	var newDiv = $('<div>').attr({
		"class" : "lp-dummy-row clearfix"
	});
	$('#hobbies-container').append(newDiv);

	var newExpertise = $('<input>').attr({
		"class" : "lp-hobby-row lp-row clearfix prof-edditable-sin-agent",
		"placeholder" : "Add Hobby",
		"data-status" : "new"
	});
	var newExpertiseButton = $('<div>').attr({
		"class" : "lp-ach-item-img hide",
		"data-type" : "hobby"
	});

	$('#hobbies-container').children().last().append(newExpertise);
	$('#hobbies-container').children().last().append(newExpertiseButton);
	newExpertise.focus();
	newExpertiseButton.addClass('float-right');
}

$(document).on('blur', '#hobbies-container input', function(e) {
	e.stopPropagation();
	delay(function() {
		updateHobbies();
	}, 0);
});

function updateHobbies() {
	var hobbies = [];
	var statusEdited = false;

	$('#hobbies-container').children().each(function() {
		var status = $(this).children().first().attr('data-status');
		var value = $(this).children().first().val();
		if (value != "" && status != 'removed') {
			var licence = value;
			hobbies.push(licence);

			statusEdited = true;
		} else if (status == 'removed') {
			statusEdited = true;
		}
	});
	if (!statusEdited) {
		return;
	}

	hobbies = JSON.stringify(hobbies);
	var payload = {
		"hobbiesList" : hobbies
	};
	callAjaxPostWithPayloadData("./updatehobbies.do", callBackUpdateHobbies, payload, true);
}

function callBackUpdateHobbies(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#expertise-container').find('input').length) {
		$('#expertise-container').append('<span>No license added yet</span>');
	}
}

// Update Social links - facebook
/*
 * $('body').on('click', '#prof-edit-social-link .icn-fb', function() { $('#social-token-text').show(); var link = $(this).attr('data-link'); $('#social-token-text').attr({ "placeholder" : "Add facebook link", "onblur" : "updateFacebookLink(this.value);$('#social-token-text').hide();" }); $('#social-token-text').val(link); });
 * 
 * function updateFacebookLink(link) { var payload = { "fblink" : link }; if (isValidUrl(link)) { callAjaxPostWithPayloadData("./updatefacebooklink.do", callBackUpdateSocialLink, payload); $('#icn-fb').attr("data-link", link); } else { $('#overlay-toast').html("Enter a valid url"); showToast(); } }
 */

// Update Social links - twitter
/*
 * $('body').on('click', '#prof-edit-social-link .icn-twit', function() { $('#social-token-text').show(); var link = $(this).attr("data-link"); $('#social-token-text').attr({ "placeholder" : "Add Twitter link", "onblur" : "updateTwitterLink(this.value);$('#social-token-text').hide();" }); $('#social-token-text').val(link); });
 * 
 * function updateTwitterLink(link) { var payload = { "twitterlink" : link }; if (isValidUrl(link)) { callAjaxPostWithPayloadData("./updatetwitterlink.do", callBackUpdateSocialLink, payload); $('#icn-twit').attr("data-link", link); } else { $('#overlay-toast').html("Enter a valid url"); showToast(); } }
 */

// Update Social links - linkedin
/*
 * $('body').on('click', '#prof-edit-social-link .icn-lin', function() { $('#social-token-text').show(); var link = $(this).attr("data-link"); $('#social-token-text').attr({ "placeholder" : "Add LinkedIn link", "onblur" : "updateLinkedInLink(this.value);$('#social-token-text').hide();" }); $('#social-token-text').val(link); });
 * 
 * function updateLinkedInLink(link) { var payload = { "linkedinlink" : link }; if (isValidUrl(link)) { callAjaxPostWithPayloadData("./updatelinkedinlink.do", callBackUpdateSocialLink, payload); $('#icn-lin').attr("data-link", link); } else { $('#overlay-toast').html("Enter a valid url"); showToast(); } }
 */

// Update Social links - google plus
/*
 * $('body').on('click', '#prof-edit-social-link .icn-gplus', function() { $('#social-token-text').show(); var link = $(this).attr("data-link"); $('#social-token-text').attr({ "placeholder" : "Add Google link", "onblur" : "updateGoogleLink(this.value);$('#social-token-text').hide();" }); $('#social-token-text').val(link); });
 * 
 * function updateGoogleLink(link) { var payload = { "gpluslink" : link }; if (isValidUrl(link)) { callAjaxPostWithPayloadData("./updategooglelink.do", callBackUpdateSocialLink, payload); $('#icn-gplus').attr("data-link", link); } else { $('#overlay-toast').html("Enter a valid url"); showToast(); } }
 */

// Update Social links - yelp
$('body').on('click', '#prof-edit-social-link .icn-yelp', function(e) {
	e.stopPropagation();
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Yelp link",
		"onblur" : "updateYelpLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});
$(document).on('click',function(){
	$('#social-token-text').hide();
});
// hide input textbox for link
$(document).on('click','#social-token-text',function(e){
	e.stopPropagation();
});
function updateYelpLink(link) {
	var payload = {
		"yelplink" : link
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updateyelplink.do", callBackUpdateSocialLink, payload, true);
		showProfileLinkInEditProfilePage("yelp", link);
	} else if( link == '') {
		confirmDisconnectSocialMedia("yelp");
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

// Update Social links - zillow
/*
 * $('body').on('click', '#prof-edit-social-link .icn-zillow', function() { $('#social-token-text').show(); var link = $(this).attr("data-link"); $('#social-token-text').attr({ "placeholder" : "Add Zillow link", "onblur" : "updateZillowLink(this.value);$('#social-token-text').hide();" }); $('#social-token-text').val(link); });
 */

/*
 * function updateZillowLink(link) { var payload = { "zillowlink" : link }; if (isValidUrl(link)) { callAjaxPostWithPayloadData("./updatezillowlink.do", callBackUpdateSocialLink, payload); $('#icn-zillow').attr("data-link", link); } else { $('#overlay-toast').html("Enter a valid url"); showToast(); } }
 */

// Update Social links - lendingTree
$('body').on('click', '#prof-edit-social-link .icn-lendingtree', function(e) {
	e.stopPropagation();
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add LendingTree link",
		"onblur" : "updateLendingTreeLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});


function updateLendingTreeLink(link) {
	var payload = {
		"lendingTreeLink" : link
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updatelendingtreelink.do", callBackUpdateSocialLink, payload, true);
		showProfileLinkInEditProfilePage("lendingtree", link);
	} else if( link == '') {
		confirmDisconnectSocialMedia("lendingtree");
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

//Update Social links - facebook pixel
$('body').on('click', '#prof-edit-social-link .icn-fb-pxl', function(e) {
	e.stopPropagation();
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Facebook pixel id",
		"onblur" : "updateFacebookPixelId(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

function updateFacebookPixelId(pixelId) {
	var parsedPixelId = parseInt(pixelId, 10);
	var isPixelIdInt = parsedPixelId == pixelId;
	if (pixelId != undefined && pixelId != '' && isPixelIdInt ) {
		var payload = {
				"pixelId" : pixelId
			};
		callAjaxPostWithPayloadData("./updatefacebookpixelid.do", callBackUpdateSocialLink, payload, true);
		showProfileLinkInEditProfilePage("facebookPixel", pixelId);
	} else if( pixelId == '') {
		confirmDisconnectSocialMedia("facebookPixel");
	} else {
		$('#overlay-toast').html("Enter a valid pixel id");
		showToast();
	}
}

$('body').on('click', '#prof-edit-social-link .icn-realtor', function(e) {
	e.stopPropagation();
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Realtor link",
		"onblur" : "updateRealtorLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

$('body').on('click', '#prof-edit-social-link .icn-google-business', function(e) {
	e.stopPropagation();
    $('#overlay-gmb-popup').removeClass('hide');
    
    var connectedLink = $(this).attr("data-link");
    if(connectedLink=='' || connectedLink==null || connectedLink==undefined || connectedLink.length==0){
    	connectedLink = 'No connections found'
    }
    
    $('#gmb-connected-placeId').html(connectedLink);
    $('#gm-con-link').attr('href',connectedLink);
    for(var i=1;i<=5;i++){
    	if(!($('#gmb-radio-'+i).hasClass('hide'))){
    		$('#gmb-radio-'+i).addClass('hide');
    	}	
    }
    
    var companyName = '';
	var city = '';
	var state = '';
	var country = '';
		
	city = $('#gmb-data').attr('data-city');
	if(city!='' && city!=undefined && city!=null){
		city=city+', '
	}
	state = $('#gmb-data').attr('data-state');
	if(state!='' && state!=undefined && state!=null){
		state=state+', '
	}
	country = $('#gmb-data').attr('data-country');
	companyName = $('#gmb-data').attr('data-companyName');
	
	var query = companyName + '+in+' + city+state+country; 
	getPlaceIds(query);
	
	$('body').on('click','#dismiss-gmb-popup',function(e){
		 $('#overlay-gmb-popup').addClass('hide');
		 if( $('body').hasClass("overflow-hidden-important") ){
		 	$('body').removeClass("overflow-hidden-important");
		 }
	 });

	$('#placeIdSelector input').on('change',function(){
		var placeId = $('input[name=placeId]:checked', '#placeIdSelector').val();
		if(placeId!='customPlace'){
			$('#gmb-placeId-selected').html(placeId);
			$('#gmb-url-placeId').html("https://search.google.com/local/writereview?placeid="+placeId);
			$('#gm-sel-link').attr('href',"https://search.google.com/local/writereview?placeid="+placeId);
		}else{
			placeId = $('#gmb-placeId').val();
			if(placeId != '' && placeId!=null){
				$('#gmb-placeId-selected').html(placeId);
				$('#gmb-url-placeId').html("https://search.google.com/local/writereview?placeid="+placeId);	
				$('#gm-sel-link').attr('href',"https://search.google.com/local/writereview?placeid="+placeId);
			}
		}
	});
	
	$('#gmb-placeId').onblur=function(){
		var placeId = $('input[name=placeId]:checked', '#placeIdSelector').val();
		if(placeId=='customPlace'){
			placeId = $('#gmb-placeId').val();
			$('#gmb-placeId-selected').html(placeId);
			$('#gmb-url-placeId').html("https://search.google.com/local/writereview?placeid="+placeId);
			$('#gm-sel-link').attr('href',"https://search.google.com/local/writereview?placeid="+placeId)
		}
	}
	
	$('body').on('click','#gmb-add-link',function(){
		var placeId = $('#gmb-placeId-selected').html();
		
		var link = "" 
		if(placeId!='' && placeId!=undefined && placeId!=null){
			link = "https://search.google.com/local/writereview?placeid="+placeId;
		}	
		
		updateGoogleBusinessLink(link);
		$('#overlay-gmb-popup').addClass('hide');	
	});
    /*$('#social-token-text').show();
    var link = $(this).attr("data-link");
    $('#social-token-text').attr({
        "placeholder" : "Add Google Business link",
        "onblur" : "updateGoogleBusinessLink(this.value);$('#social-token-text').hide();"
    });
    $('#social-token-text').val(link);*/
	$('body').on('click','#gmb-disconnect-link',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		disconnectSocialMedia(e,"google business", false);
		/*var payload = {
				"socialmedia" : "google business"
			};
		callAjaxPostWithPayloadData("./disconnectparticularsocialmedia.do", function(data) {
				$('#overlay-toast').html(data);
				showToast();
			}, payload, true);
		*/
		$('#overlay-gmb-popup').addClass('hide');
		removeProfileLinkInEditProfilePage( "googleBusiness" );
	});

});

function updateRealtorLink(link) {
	var payload = {
		"realtorLink" : link
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updateRealtorlink.do", callBackUpdateSocialLink, payload, true);
		showProfileLinkInEditProfilePage("realtor", link);
	} else if( link == '') {
		confirmDisconnectSocialMedia("realtor");
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

function updateGoogleBusinessLink(link) {
	var payload = {
		"googleBusinessLink" : link
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updateGoogleBusinessLink.do", callBackUpdateSocialLink, payload, true);
		showProfileLinkInEditProfilePage("googleBusiness", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

function confirmDisconnectSocialMedia(socialMedia) {

	$('#disconnect-overlay-header').html("Confirm Disconnect");
	$('#disconnect-overlay-text').html("This action will disconnect your account from " + socialMedia);
	$('#disconnect-overlay-continue').html("Disconnect");
	$('#disconnect-overlay-cancel').html("Cancel");
	$('#disconnect-overlay-continue').off();
	$('#disconnect-overlay-continue').click(function() {
		disconnectParticularSocialMedia(socialMedia);
		overlayRevert();
	});
	
	$('#disconnect-overlay-cancel').off();
	$('#disconnect-overlay-cancel').click(function() {
		overlayRevert();
	});
	
	$('#disconnect-overlay-main').show();
}

function disconnectParticularSocialMedia(socialMedia) {
	var payload = {
			"socialmedia" : socialMedia
		};
	callAjaxPostWithPayloadData("./disconnectparticularsocialmedia.do", function(data) {
			$('#overlay-toast').html(data);
			showToast();
		}, payload, true);
	removeProfileLinkInEditProfilePage( socialMedia );
}

function callBackUpdateSocialLink(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
	$('#social-token-text').val('');
}

function isValidUrl(url) {
	var myVariable = url;
	if (webAddressRegEx.test(myVariable)) {
		return true;
	} else {
		return false;
	}
}

// Adjust image
function adjustImage() {
	var windW = window.innerWidth;
	if (windW < 768) {
		$('.mobile-tabs').children('.mob-icn-active').click();
		var imgW = $('#prof-image').width();
		$('#prof-image').height(imgW * 0.7);
		var h2 = $('.prog-img-container').height() - 11;
		$('.prof-name-container').height(h2);
		var rowW = $('.lp-con-row').width() - 55 - 45;
		$('.lp-con-row-item').width(rowW + 'px');
		$('.footer-main-wrapper').hide();
	} else {
		$('.prof-name-container,#prof-image').height(200);
		var rowW = $('.lp-con-row').width() - 50 - 50; // left image-50;
		// right-locks-50
		$('.lp-con-row-item').width(rowW + 'px');
		$('.footer-main-wrapper').show();
		// show all the containers
		$('#reviews-container, #prof-company-intro, #prof-agent-container').show();
		$('#recent-post-container, #ppl-post-cont, #contact-wrapper, #intro-about-me').show();
	}
}

// Function to show map on the screen
/*
 * function initializeGoogleMap() { var mapCanvas = document.getElementById('map-canvas'); var geocoder = new google.maps.Geocoder(); var address = "Raremile technologies,HSR layout,bangalore, 560102"; var latitude = 45; var longitude = -73; geocoder.geocode({ 'address' : address }, function(results, status) { if (status == google.maps.GeocoderStatus.OK) { latitude = results[0].geometry.location.lat(); longitude = results[0].geometry.location.lng(); var mapOptions = { center: new google.maps.LatLng(latitude, longitude), zoom: 15, mapTypeId: google.maps.MapTypeId.ROADMAP };
 * 
 * map = new google.maps.Map(mapCanvas, mapOptions); map.setCenter(results[0].geometry.location); marker = new google.maps.Marker({ position: results[0].geometry.location, map: map, title: "RM" }); } }); }
 */

// Data population for Admin
function paintForProfile() {
	profileId = $('#profile-id').val();
	var companyId = $('#prof-company-id').val();
	var regionId = $('#prof-region-id').val();
	var branchId = $('#prof-branch-id').val();
	var agentId = $('#prof-agent-id').val();

	if (companyId != undefined) {
		attrName = "companyId";
		attrVal = companyId;

		fetchHierarchy("companyProfileName", $("#company-profile-name").val());
	} else if (regionId != undefined) {
		attrName = "regionId";
		attrVal = regionId;

		fetchHierarchy(attrName, attrVal);
	} else if (branchId != undefined) {
		attrName = "branchId";
		attrVal = branchId;

		fetchHierarchy(attrName, attrVal);
	} else if (agentId != undefined) {
		attrName = "agentId";
		attrVal = agentId;
	}
	startIndex = 0;
	doStopReviewsPaginationEditProfile = false;
	isReviewsRequestRunningEditProfile = false;
	$('#prof-review-item').html('');
	// Common call for all cases
	fetchAvgRating(attrName, attrVal);
	fetchReviewCount(attrName, attrVal, minScore);
	fetchReviewsOnEditProfile(attrName, attrVal, false);
}

function focusOnElement() {
	if (editProfileForYelp) {
		$('#social-token-text').show();
		var link = $(this).attr("data-link");
		$('#social-token-text').attr({
			"placeholder" : "Add Yelp link",
			"onblur" : "updateYelpLink(this.value);$('#social-token-text').hide();"
		});
		$('#social-token-text').val(link);
		editProfileForYelp = false;
	}
	if (editProfileForLicense) {
		addAuthorisedIn();
		editProfileForLicense = false;
	} else if (editProfileForHobbies) {
		addHobby();
		editProfileForHobbies = false;
	} else if (editProfileForAchievements) {
		addAnAchievement();
		editProfileForAchievements = false;
	}
}

// Hierarchy data population
function fetchHierarchy(attrName, attrValue) {
	var url = "./getadminhierarchy.do?" + attrName + "=" + attrValue;
	callAjaxGET(url, paintHierarchy, true);
}

// Hierarchy data population
function fetchCompanyHierarchy(attrName, attrValue) {
	var url = "./getcompanyhierarchy.do?" + attrName + "=" + attrValue;
	callAjaxGET(url, paintHierarchy, true);
}

function paintHierarchy(data) {
	$("#prof-hierarchy-container").html(data);
	$("#prof-hierarchy-container").show();
	hideDashOverlay('#hierarchy-ep');

	/**
	 * Click on region
	 */
	$('.comp-region').unbind('click');
	$('.comp-region').click(function(e) {
		if ($(this).attr("data-openstatus") == "closed") {
			fetchRegionHierarchyOnClick($(this).attr('data-regionid'));
			$(this).attr("data-openstatus", "open");
		} else {
			$('#comp-region-branches-' + $(this).attr('data-regionid')).slideUp(200);
			$(this).attr("data-openstatus", "closed");
		}
	});
	// Click on branch
	bindClickBranchForIndividuals("comp-region-branch");
	bindClickBranchForIndividuals("comp-branch");

	// Individuals
	paintProfImage("comp-individual-prof-image");
}

// region hierarchy on click
function fetchRegionHierarchyOnClick(regionId) {
	var url = "./getregionhierarchy.do?regionId=" + regionId;
	callAjaxGET(url, function(data) {
		$("#comp-region-branches-" + regionId).html(data).slideDown(200);
		bindClickBranchForIndividuals("comp-region-branch");
		bindClickForIndividuals("comp-region-individual");
	}, true);
}

function bindClickBranchForIndividuals(bindingClass) {
	$("." + bindingClass).unbind('click');
	$("." + bindingClass).click(function(e) {
		e.stopPropagation();
		if ($(this).attr("data-openstatus") == "closed") {
			fetchBranchHierarchyOnClick($(this).attr('data-branchid'));
			$(this).attr("data-openstatus", "open");
		} else {
			$('#comp-branch-individuals-' + $(this).attr('data-branchid')).slideUp(200);
			$(this).attr("data-openstatus", "closed");
		}
	});
}

// Branch hierarchy on click
function fetchBranchHierarchyOnClick(branchId) {
	var url = "./getbranchhierarchy.do?branchId=" + branchId;
	callAjaxGET(url, function(data) {
		$("#comp-branch-individuals-" + branchId).html(data).slideDown(200);
		paintProfImage("comp-individual-prof-image");
		bindClickForIndividuals("comp-individual");
	}, true);
}

function paintProfImage(imgDivClass) {
	$("." + imgDivClass).each(function() {
		var imageUrl = $(this).attr('data-imageurl');
		if (imageUrl != undefined && imageUrl.trim() != "") {
			$(this).css("background", "url(" + imageUrl + ") no-repeat center");
			$(this).css("background-size", "100%");
		}
	});
}

var doStopReviewsPaginationEditProfile = false;
var isReviewsRequestRunningEditProfile = false;
var isReviewsLoadingEditProfile = false;

function fetchReviewsEditProfileScroll() {

	// check if the current page is edit profile
	if (location.hash != "#showprofilepage") {
		return;
	}
	if ((window.innerHeight + window.pageYOffset) >= ($('#prof-review-item').offset().top + $('#prof-review-item').height() - 200) && (!doStopReviewsPaginationEditProfile || $('div.dsh-review-cont.hide').length > 0)) {
		if (isReviewsLoadingEditProfile)
			return; // return if the scroll is running
		if ($('div.dsh-review-cont.hide').length > 0) {
			showLoaderOnPagination($('#prof-review-item'));
			isReviewsLoadingEditProfile = true;
			setTimeout(displayReviewOnEditProfile, 500);
		} else {
			fetchReviewsOnEditProfile(attrName, attrVal, false);
		}
	}
}

function fetchReviewsOnEditProfile(attrName, attrVal, isNextBatch) {

	if (isReviewsRequestRunningEditProfile)
		return; // Return if ajax request is still running
	var url = "./fetchreviews.do?" + attrName + "=" + attrVal + "&minScore=" + minScore + "&startIndex=" + startIndex + "&numOfRows=" + numOfRows+ "&hiddenSection=" +hiddenSection;

	isReviewsRequestRunningEditProfile = true;
	if (!isNextBatch) {
		showLoaderOnPagination($('#prof-review-item'));
	}
	callAjaxGET(url, function(data) {
		// Check if list revcieved is empty
		var tempDiv = $("<div>").html(data);

		var countOfReviewsFetched = tempDiv.children('.dsh-review-cont').length;

		if (countOfReviewsFetched <= 0) {
			if (startIndex == 0) {
				$("#prof-review-item").html("<span>No Reviews Found</span>");
			}
		} else {
			// Populate the reviews
			$("#prof-review-item").append(data);
		}

		if (countOfReviewsFetched < numOfRows) {
			doStopReviewsPaginationEditProfile = true;
		}

		// Update events
		updateEventOnDashboardPageForReviews();
		startIndex = startIndex + numOfRows;

		if (!isNextBatch) {
			displayReviewOnEditProfile();
		}
		isReviewsRequestRunningEditProfile = false;
		if ($('div.dsh-review-cont.hide').length <= numOfRows && !doStopReviewsPaginationEditProfile) {
			fetchReviewsOnEditProfile(attrName, attrVal, true);
		} else if ($('div.dsh-review-cont.hide').length < (2 * numOfRows)) {
			fetchZillowReviewsBasedOnProfile(attrName, attrVal, isZillowReviewsCallRunning, false, countOfReviewsFetched, numOfRows, "");
		}
	}, true);
}

function fetchZillowReviewsBasedOnProfile(profileLevel, currentProfileIden, isNextBatch, isFromDashBoard, start, batchSize, name) {
	if (currentProfileIden == undefined || currentProfileIden == "" || isZillowReviewsCallRunning) {
		return; // Return if profile id is undefined
	}
	var url = "/rest/profile/";
	if (profileLevel == 'companyId') {
		// url += "company/";
		return;
	} else if (profileLevel == 'regionId') {
		// url += "region/";
		return;
	} else if (profileLevel == 'branchId') {
		// url += "branch/";
		return;
	} else if (profileLevel == 'agentId') {
		// url += "individual/";
		return;
	}
	url += currentProfileIden + "/zillowreviews";
	isZillowReviewsCallRunning = true;

	callAjaxGET(url, function(data) {
		isZillowReviewsCallRunning = false;
		if (data != undefined && data != "") {
			var responseJson = $.parseJSON(data);
			if (responseJson != undefined) {
				var result = $.parseJSON(responseJson.entity);
				zillowCallBreak = result.zillowCallBreak;
				if (!zillowCallBreak) {
					stopFetchReviewPagination = true; // Stop pagination as zillow reviews are fetch one shot
					if (result != undefined && result.length > 0) {
						// build zillow reviews html here
						var lastIndex = start - batchSize;
						// remove the No Reviews Found
						if (isFromDashBoard && lastIndex <= 0) {
							$("#review-desc").html("What people say about " + name);
						} else if (!isFromDashBoard && start <= 0) {
							$("#prof-review-item").html("");
						}
						paintReviews(result, isFromDashBoard);
					}
				}
			}
		}
	}, true);
}

// Display the review on edit profile
function displayReviewOnEditProfile() {
	isReviewsLoadingEditProfile = false;
	$('.dsh-review-cont').removeClass("ppl-review-item-last").addClass("ppl-review-item");
	hideLoaderOnPagination($('#prof-review-item'));
	var total = $('div.dsh-review-cont.hide').length;
	$('div.dsh-review-cont.hide').each(function(index, currentElement) {
		$(this).removeClass("hide");
		if (index >= numOfRows - 1 || index >= total - 1) {
			$(this).addClass("ppl-review-item-last").removeClass("ppl-review-item");
			return false;
		}
	});

	if ($('div.dsh-review-cont.hide').length <= numOfRows && !doStopReviewsPaginationEditProfile) {
		fetchReviewsOnEditProfile(attrName, attrVal, true);
	}
}

// fetch review count
function fetchReviewCount(attrName, attrVal, minScore) {
	var url = "./fetchreviewcount.do?" + attrName + "=" + attrVal + "&minScore=" + minScore;
	callAjaxGET(url, paintReviewCount, true);
}

function paintReviewCount(reviewCount) {
	if (reviewCount != undefined) {
		if (reviewCount > 0) {
			$("#prof-company-review-count").click(function() {
				if (window.innerWidth < 768) {
					$('.icn-star-smile').click();
				}
				$(window).scrollTop($('#reviews-container').offset().top);
			});
		}
		reviewCount = reviewCount + ' Review(s)';
		$("#prof-company-review-count").html(reviewCount);
	}
}

// fetch avg rating
function fetchAvgRating(attrName, attrVal) {
	var url = "./fetchaveragerating.do?" + attrName + "=" + attrVal;
	callAjaxGET(url, paintAvgRating, true);
}

function paintAvgRating(avgRating) {
	if (avgRating != undefined) {
		changeRatingPattern(avgRating, $("#rating-avg-comp"), true);
	}
}

// Edit EmailIds
$(document).on('blur', '#contant-info-container input[data-email]', function() {
	if (!$(this).val() || !emailRegex.test(this.value) || ($(this).val() == $('#' + $(this).attr("id") + '-old').val())) {
		$('#overlay-toast').html("Please enter valid email address");
		showToast();
		return;
	}

	delay(function() {
		var mailIds = [];
		$('#contant-info-container input[data-email]').each(function() {
			if (this.value != "") {
				var mailId = {};
				mailId.key = $(this).attr("data-email");
				mailId.value = this.value;
				mailIds.push(mailId);
			}
		});
		mailIds = JSON.stringify(mailIds);
		var payload = {
			"mailIds" : mailIds
		};
		callAjaxPostWithPayloadData("./updateemailids.do", callBackOnUpdateMailIds, payload, true);
	}, 0);
});

function callBackOnUpdateMailIds(data) {
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails, true);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

function bindClickForIndividuals(elementClass) {
	$("." + elementClass).unbind('click');
	$("." + elementClass).click(function(e) {
		e.stopPropagation();
	});
}

// Bind scroll event for public posts on edit profile page
function attachPostsScrollEvent() {
	$('#prof-posts').off('scroll');
	$('#prof-posts').on('scroll', function() {
		var scrollContainer = this;
		if (scrollContainer.scrollTop >= ((scrollContainer.scrollHeight) - (scrollContainer.clientHeight / 0.75))) {
			if (!doStopPostPaginationEditProfile || publicPostsBatch.length > 0) {
				fetchPublicPostEditProfile(false);
			}

		}
	});
}

var doStopPostPaginationEditProfile = false;
var isAjaxRequestRunningEditProfile = false;
var isLoaderRunningEditProfile = false;
var publicPostsBatch = [];

/**
 * Method to fetch public posts on edit profile page
 * 
 * @param isNextBatch
 */
function fetchPublicPostEditProfile(isNextBatch) {

	if (proPostStartIndex == 0) {
		doStopPostPaginationEditProfile = false;
		publicPostsBatch = [];
		$('#prof-posts').html('');
		$('#last-post').attr('hidden', 'true');
	}

	// Show from existing batch if the data is present
	if (!isNextBatch && publicPostsBatch.length > 0) {
		$('#last-post').removeAttr('hidden');
		var posts = publicPostsBatch.slice(0, proPostBatchSize);
		if (publicPostsBatch.length > proPostBatchSize) {
			publicPostsBatch = publicPostsBatch.slice(proPostBatchSize);
		} else {
			publicPostsBatch = [];
		}

		if (isLoaderRunningEditProfile) {
			hideLoaderOnPagination($('#prof-posts'));
		}
		showLoaderOnPagination($('#prof-posts'));
		isLoaderRunningEditProfile = true;
		// paint the posts
		setTimeout(function() {
			paintPosts(posts);
			isLoaderRunningEditProfile = false;
			// Fetch the next batch
			if (!doStopPostPaginationEditProfile && publicPostsBatch.length <= proPostBatchSize) {
				fetchPublicPostEditProfile(true);
			}
		}, 500);

		return;
	}

	if (!isNextBatch) {
		showLoaderOnPagination($('#prof-posts'));
	}

	if (isAjaxRequestRunningEditProfile)
		return; // Return if ajax request running to fetch the social posts

	var payload = {
		"batchSize" : proPostBatchSize,
		"startIndex" : proPostStartIndex
	};

	isAjaxRequestRunningEditProfile = true;
	callAjaxGetWithPayloadData("./postsforuser.do", function(data) {

		isAjaxRequestRunningEditProfile = false;
		if (data.errCode == undefined) {
			if (data != "") {

				var posts = JSON.parse(data);
				if (posts.length <= 0 && proPostStartIndex == 0) {
					doStopPostPaginationEditProfile = true;
					hideLoaderOnPagination($('#prof-posts'));
					return;
				}
				if (posts.length < proPostBatchSize) {
					doStopPostPaginationEditProfile = true;
				}

				// update start index
				proPostStartIndex += proPostBatchSize;

				// update the batch
				publicPostsBatch = publicPostsBatch.concat(posts);

				if (isNextBatch) {
					// Fetch the next batch
					if (!doStopPostPaginationEditProfile && publicPostsBatch.length <= proPostBatchSize) {
						fetchPublicPostEditProfile(true);
					}
				} else {
					if (posts && posts.length > 0)
						fetchPublicPostEditProfile(false);
				}

			}
		}
	}, payload, true);
}

function paintPosts(posts) {
	var divToPopulate = "";
	var postsLength = posts.length;
	var elementClass;
	$('#prof-posts').children('.tweet-panel-item').removeClass('bord-bot-none');
	$.each(posts, function(i, post) {
		var iconClass = "";
		var href = "javascript:void(0)";
		if (post.source == "google") {
			iconClass = "icn-gplus";
		} else if (post.source == "SocialSurvey")
			iconClass = "icn-ss";
		else if (post.source == "facebook") {
			iconClass = "icn-fb";
			href = "http://www.facebook.com/" + post.postId;
		} else if (post.source == "twitter") {
			iconClass = "icn-twit";
			var res = post.postText.split("http");
			href = "http" + res[1];
		} else if (post.source == "linkedin") {
			iconClass = "icn-lin";
		}
		if (typeof post.postUrl != "undefined") {
			href = post.postUrl;
		}
		var hrefComplet = '<a href=' + href + ' target="_blank">';

		elementClass = "tweet-panel-item bord-bot-dc clearfix";

		if (i >= postsLength - 1) {
			elementClass += " bord-bot-none";
		}

		divToPopulate += '<div class="' + elementClass + '">' + hrefComplet + '<div class="tweet-icn ' + iconClass + ' float-left"></div>' + "</a>" + '<div class="tweet-txt float-left">' + '<div class="tweet-text-main">' + linkify(escapeHtml(post.postText)) + '</div>' + '<div class="tweet-text-link"><em>' + post.postedBy + '</em></div>' + '<div class="tweet-text-time"><em>' + convertUserDateToWeekFormt(new Date(post.timeInMillis)) + '</em></div>' + '</div>';

		if (post.source == "SocialSurvey") {
			var divToDeleteSurvey = '<div class="dlt-survey-wrapper hide"><div surveymongoid=' + post._id + ' class="post-dlt-icon reg-err-pu-close float-left">' + '</div></div>';
			divToPopulate += divToDeleteSurvey;
		}

		divToPopulate += '</div>';
	});

	// Hide the loader icon
	hideLoaderOnPagination($('#prof-posts'));

	if ($('#prof-posts').children('.tweet-panel-item').length == 0) {
		$('#prof-posts').html(divToPopulate);
		$('#prof-posts').perfectScrollbar({
			suppressScrollX : true
		});
		$('#prof-posts').perfectScrollbar('update');
	} else {
		$('#prof-posts').append(divToPopulate);
		$('#prof-posts').perfectScrollbar('update');
	}
}


function fixSocialMediaResponse(columnName, columnValue){
	var payload = {
			"columnName" : columnName,
			"columnValue" : columnValue
		};
		callAjaxGetWithPayloadData('./socialmediatofix.do', paintFixSocialMedia, payload, true);
}


function showDashboardButtons(columnName, columnValue) {
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue
	};
	callAjaxGetWithPayloadData('./dashboardbuttonsorder.do', paintDashboardButtons, payload, true);
}

function paintDashboardButtons(data) {
	data = $.parseJSON(data);
	var stages = data.stages;
	var max = 2;
	if (stages != undefined && stages.length != 0) {
		if (stages.length < max) {
			$('#dsh-btn2').addClass('hide');
			$('#dsh-btn3').addClass('hide');
			max = stages.length;
		}
		for (var i = 0; i < max; i++) {
			var contentToDisplay = '';
			if (stages[i].profileStageKey == 'FACEBOOK_PRF') {
				contentToDisplay = 'Connect to Facebook';
			} else if (stages[i].profileStageKey == 'ZILLOW_PRF') {
				contentToDisplay = 'Connect to Zillow';
			} else if (stages[i].profileStageKey == 'GOOGLE_PRF') {
				contentToDisplay = 'Connect to Google+';
			} else if (stages[i].profileStageKey == 'TWITTER_PRF') {
				contentToDisplay = 'Connect to Twitter';
			} else if (stages[i].profileStageKey == 'YELP_PRF') {
				contentToDisplay = 'Connect to Yelp';
			} else if (stages[i].profileStageKey == 'LINKEDIN_PRF') {
				contentToDisplay = 'Connect to Linkedin';
			} else if (stages[i].profileStageKey == 'LICENSE_PRF') {
				contentToDisplay = 'Enter license details';
			} else if (stages[i].profileStageKey == 'HOBBIES_PRF') {
				contentToDisplay = 'Enter hobbies';
			} else if (stages[i].profileStageKey == 'ACHIEVEMENTS_PRF') {
				contentToDisplay = 'Enter achievements';
			} else if (stages[i].profileStageKey == 'INSTAGRAM_PRF') {
				contentToDisplay = 'Connect to Instagram';
			}
			if (i == 0) {
				$('#dsh-btn2').data('social', stages[i].profileStageKey);
				$('#dsh-btn2').html(contentToDisplay);
				$('#dsh-btn2').removeClass('hide');
			}
			
			if($('#dsh-btn0').hasClass('hide')){
				if (i == 1) {
					$('#dsh-btn3').data('social', stages[i].profileStageKey);
					$('#dsh-btn3').html(contentToDisplay);
					$('#dsh-btn3').removeClass('hide');
				}
			}
			
		}
	}
}

function dashboardButtonAction(buttonId, task, columnName, columnValue) {
	if (task == 'FACEBOOK_PRF') {
		openAuthPageDashboard('facebook', columnName, columnValue);
	} else if (task == 'GOOGLE_PRF') {
		openAuthPageDashboard('google', columnName, columnValue);
	} else if (task == 'ZILLOW_PRF') {
		openAuthPageDashboardZillow('#dsh-btn3');
	} else if (task == 'YELP_PRF') {
		showMainContent('./showprofilepage.do');
		editProfileForYelp = true;
	} else if (task == 'LINKEDIN_PRF') {
		openAuthPageDashboard('linkedin', columnName, columnValue);
	} else if (task == 'TWITTER_PRF') {
		openAuthPageDashboard('twitter', columnName, columnValue);
	} else if (task == 'LICENSE_PRF') {
		showMainContent('./showprofilepage.do');
		editProfileForLicense = true;
	} else if (task == 'HOBBIES_PRF') {
		showMainContent('./showprofilepage.do');
		editProfileForHobbies = true;
	} else if (task == 'ACHIEVEMENTS_PRF') {
		showMainContent('./showprofilepage.do');
		editProfileForAchievements = true;
	} else if (task == 'INSTAGRAM_PRF') {
		openAuthPageDashboard('instagram', columnName, columnValue);
	}
}

// Update Disclaimer details
$(document).on('blur', '#disclaimer-text', function() {

	var disclaimer = $('#disclaimer-text').val().trim();
	if (disclaimer == undefined || disclaimer == "") {
		$('#overlay-toast').html("Please add disclaimer");
		showToast();
		return;
	}

	if ($('#disclaimer-default').val() != disclaimer) {
		var payload = {
			"disclaimer" : disclaimer
		};

		callAjaxPostWithPayloadData("./updatedisclaimer.do", function(data) {
			$('#prof-message-header').html(data);
			if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
				if (disclaimer != undefined) {
					$('#disclaimer-default').val(disclaimer.trim());
					$('#disclaimer-text').val(disclaimer.trim());
				}
			}

			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload, true);
	}
});

// Dashboard admin reports
$(document).on('change', '#download-survey-reports', function() {
	// var selectedValue =
});

$(document).on('click', '#dsh-dwnld-report-btn', function(e) {
	var selectedValue = $('#download-survey-reports').val();
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	var defaultEmailId = $("#default-email-id").val();
	var popupMsg = '<div>We will mail you the report. Please specify email address to send report to: <br><br>'
					+'	<input id="dsh-report-email-id" name="dsh-report-email-id" type="text" class="dash-sel-item" placeholder="Email Address" value="'+defaultEmailId+'">'
					+'</div>';
	
	e.stopPropagation();
	$('#overlay-continue').html("Submit");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Generate Report");
	$('#overlay-text').html(popupMsg);
	

	$('#overlay-continue').click(function() {
		var emailId = $("#dsh-report-email-id").val();
		var emailIdMsg = (emailId != null && emailId != undefined && emailId != "") ? emailId : defaultEmailId;
		if(emailId == null || emailId == undefined || emailId == "" || validateEmailId("dsh-report-email-id", true)){
			var key = parseInt(selectedValue);
			switch (key) {
			case 1:
				var payload = { "startDate":startDate, "endDate":endDate, "columnValue" : colValue, "columnName": colName, "mailid": emailId};
				callAjaxGetWithPayloadData("./downloadagentrankingreport.do", function(data) {
					$('#overlay-main').hide();
					showInfo("User Ranking Report will be mailed to: "+emailIdMsg+" shortly.");
				}, payload, true);
				break;
			case 2:
				var payload = { "startDate":startDate, "endDate":endDate, "columnValue" : colValue, "columnName": colName, "mailid": emailId};
				callAjaxGetWithPayloadData("./generatecustomersurveyresults.do", function(data) {
					$('#overlay-main').hide();
					showInfo("Survey Results Report will be mailed to: "+emailIdMsg+" shortly.");
				}, payload, true);
				break;
			case 3:
				var payload = { "startDate":startDate, "endDate":endDate, "columnValue" : colValue, "columnName": colName, "mailid": emailId};
				callAjaxGetWithPayloadData("./downloaddashboardsocialmonitor.do", function(data) {
					$('#overlay-main').hide();
					showInfo("Social Monitor Report will be mailed to: "+emailIdMsg+" shortly.");
				}, payload, true);
				break;
			case 4:
				var payload = { "startDate":startDate, "endDate":endDate, "columnValue" : colValue, "columnName": colName, "mailid": emailId};
				callAjaxGetWithPayloadData("./downloaddashboardincompletesurvey.do", function(data) {
					$('#overlay-main').hide();
					showInfo("Incomplete Survey Report will be mailed to: "+emailIdMsg+" shortly.");
				}, payload, true);
				break;
			case 5:
				var payload = {"columnValue" : colValue, "columnName": colName, "mailid": emailId};
				callAjaxGetWithPayloadData("./downloaduseradoptionreport.do", function(data) {
					$('#overlay-main').hide();
					showInfo("User Adoption Report will be mailed to: "+emailIdMsg+" shortly.");
				}, payload, true);
				break;
			case 6:
				var payload = {"columnValue" : colValue, "columnName": colName, "mailid": emailId};
				callAjaxGetWithPayloadData("./downloadcompanyhierarchyreport.do", function(data) {
					$('#overlay-main').hide();
					showInfo("Company Hierarchy Report will be mailed to: "+emailIdMsg+" shortly.");
				}, payload, true);
				break;
			default:
				break;
			}
			$('#overlay-continue').unbind('click');
			
		}
	});
	$('#overlay-main').show();
});



// function to switch to admin
function userSwitchToAdmin() {
	callAjaxGET("/switchtoadmin.do", function(data) {
		if (data == "success") {
			// window.location = window.location.origin + '/userlogin.do';
			window.location = getLocationOrigin() + '/userlogin.do';
		}
	}, true);
}

// function to switch to company admin
function userSwitchToCompAdmin() {
	callAjaxGET("/switchtocompanyadmin.do", function(data) {
		if (data == "success") {
			// window.location = window.location.origin + '/userlogin.do';
			window.location = getLocationOrigin() + '/userlogin.do';
		}
	}, true);
}

function bindAppUserLoginEvent() {
	$('.user-login-icn').off('click');
	$('.user-login-icn').on('click', function(e) {
		$('.user-login-icn').unbind("click");
		e.stopImmediatePropagation();
		var payload = {
			"colName" : "userId",
			"colValue" : $(this).attr('data-iden')
		};
		callAjaxGETWithTextData("/logincompanyadminas.do", function(data) {

			window.location = getLocationOrigin() + '/userlogin.do';
		}, true, payload, '.user-login-icn');
	});
}

function initializeVerticalAutcomplete() {
	$('#prof-vertical').autocomplete({
		minLength : 1,
		source : verticalsMasterList,
		delay : 0,
		autoFocus : true,
		close : function(event, ui) {
		},
		select : function(event, ui) {
		},
		create : function(event, ui) {
			$('.ui-helper-hidden-accessible').remove();
		}
	});
	$("#prof-vertical").keydown(function(e) {
		if (e.keyCode != $.ui.keyCode.TAB)
			return;

		e.keyCode = $.ui.keyCode.DOWN;
		$(this).trigger(e);

		e.keyCode = $.ui.keyCode.ENTER;
		$(this).trigger(e);
	});
	$("#prof-vertical").focus(function() {
		$(this).trigger('keydown');
		$(this).autocomplete("search");
	});
}
var isfetchreview = false;
function getIncompleteSurveyCount(colName, colValue) {
	if (isfetchreview == true) {
		return;
	}
	startIndexInc = 0;
	var payload = {
		"columnName" : colName,
		"columnValue" : colValue
	};
	isfetchreview = true;
	callAjaxGetWithPayloadData("./fetchdashboardincompletesurveycount.do", function(data) {
		isfetchreview = false;
		$('#icn-sur-popup-cont').attr("data-total", data);
		$('#dsh-inc-srvey').attr("data-total", data);
		var totalCount = parseInt(data);
		var batchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
		var numPages = 0;
		if (parseInt(totalCount % batchSize) == 0) {
			numPages = parseInt(totalCount / batchSize);
		} else {
			numPages = parseInt(parseInt(totalCount / batchSize) + 1);
		}
		$('#paginate-total-pages').html(numPages);

		// Show dashboard incomplete reviews
		doStopIncompleteSurveyPostAjaxRequest = false;
		$('#dsh-inc-srvey').html('');
		fetchIncompleteSurvey(false);
		$('#dsh-inc-srvey').perfectScrollbar({
			suppressScrollX : true
		});
		$('#dsh-inc-srvey').perfectScrollbar('update');

	}, payload, true);
}

$(document).on('click', '#sur-next.paginate-button', function() {
	var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
	var incompleteSurveyBatchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	incompleteSurveyStartIndex = incompleteSurveyStartIndex + incompleteSurveyBatchSize;
	$('#icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
});

$(document).on('click', '#sur-previous.paginate-button', function() {
	var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
	var incompleteSurveyBatchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	if (incompleteSurveyStartIndex % incompleteSurveyBatchSize == 0) {
		incompleteSurveyStartIndex = parseInt(incompleteSurveyStartIndex / incompleteSurveyBatchSize) - 1;
	} else {
		incompleteSurveyStartIndex = parseInt(incompleteSurveyStartIndex / incompleteSurveyBatchSize);
	}
	incompleteSurveyStartIndex = incompleteSurveyStartIndex * incompleteSurveyBatchSize;
	$('#icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
});

$(document).on('keypress', '#sel-page', function(e) {
	// if the letter is not digit then don't type anything
	if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		return false;
	}
	var batchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	var total = parseInt($('#icn-sur-popup-cont').attr("data-total"));
	var prevPageNoVal = parseInt($('#sel-page').val());
	if (prevPageNoVal == NaN) {
		prevPageNoVal = 0;
	}
	var pageNo = prevPageNoVal + String.fromCharCode(e.which);
	pageNo = parseInt(pageNo);
	var incompleteSurveyStartIndex = parseInt(pageNo - 1) * batchSize;
	if (incompleteSurveyStartIndex >= total || incompleteSurveyStartIndex <= 0) {
		return false;
	}
});

function paginateIncompleteSurvey() {
	$('#sel-page').blur();
	var pageNo = parseInt($('#sel-page').val());
	if (pageNo == NaN || pageNo <= 0) {
		return false;
	}
	var incompleteSurveyStartIndex = 0;
	var batchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	incompleteSurveyStartIndex = parseInt(pageNo - 1) * batchSize;

	$('#icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
}

$(document).on('keyup', '#sel-page', function(e) {
	if (e.which == 13) {
		paginateIncompleteSurvey();
	}
});

$(document).on('change', '#sel-page', function(e) {
	delay(function() {
		paginateIncompleteSurvey();
	}, 100);
});

function showIncompleteSurveyListPopup(event) {
	event.stopPropagation();
	$('#icn-sur-popup-cont').attr("data-start", 0);
	$("#overlay-incomplete-survey").show();
	paintIncompleteSurveyListPopupResults(0);
}

function paintIncompleteSurveyListPopupResults(incompleteSurveystartIndex) {
	var incompleteSurveyBatchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	$('#sel-page').val((incompleteSurveystartIndex / incompleteSurveyBatchSize) + 1);
	var payload = {
		"columnName" : colName,
		"columnValue" : colValue,
		"startIndex" : incompleteSurveystartIndex,
		"batchSize" : $('#icn-sur-popup-cont').attr("data-batch"),
		"origin" : "oldDashboard"
	};
	callAjaxGetWithPayloadData("./fetchincompletesurveypopup.do", function(data) {
		disableBodyScroll();
		$('#icn-sur-popup-cont').html(data);
		if (parseInt(incompleteSurveystartIndex) > 0) {
			$('#sur-previous').addClass('paginate-button');
		} else {
			$('#sur-previous').removeClass('paginate-button');
		}
		incompleteSurveystartIndex = parseInt(incompleteSurveystartIndex) + parseInt($('#icn-sur-popup-cont').children('.dash-lp-item').size());
		var totalSurveysCount = parseInt($('#icn-sur-popup-cont').attr("data-total"));
		if (incompleteSurveystartIndex < totalSurveysCount) {
			$('#sur-next').addClass('paginate-button');
		} else {
			$('#sur-next').removeClass('paginate-button');
		}
	}, payload, true);
}

function hideIncompleteSurveyListPopup() {
	enableBodyScroll();
	$("#overlay-incomplete-survey").hide();
	$('#icn-sur-popup-cont').html('');
	$('#icn-sur-popup-cont').attr("data-start", 0);
	$('#icn-sur-popup-cont').data('selected-survey', new Array());
}

$(document).on('click', '#del-mult-sur-icn.mult-sur-icn-active', function() {
	var selectedSurveys = $('#icn-sur-popup-cont').data('selected-survey');
	removeMultipleIncompleteSurveyRequest(selectedSurveys);
});

function removeIncompleteSurveyRequest(incompleteSurveyId) {
	var selectedSurveys = [];
	selectedSurveys.push(incompleteSurveyId);
	removeMultipleIncompleteSurveyRequest(selectedSurveys);
}

function removeMultipleIncompleteSurveyRequest(incompleteSurveyIds) {
	callAjaxPOSTWithTextData("/deletemultipleincompletesurveyrequest.do?surveySetToDelete=" + incompleteSurveyIds, function(data) {
		if (data == "success") {

			// unselect all the options after deleting
			$('#icn-sur-popup-cont').data('selected-survey', []);

			var totalIncSurveys = $('#icn-sur-popup-cont').attr('data-total');
			totalIncSurveys = totalIncSurveys - incompleteSurveyIds.length;
			$('#icn-sur-popup-cont').attr('data-total', totalIncSurveys);
			var batchSize = parseInt($('#icn-sur-popup-cont').attr('data-batch'));
			var newTotalPages = 0;
			if (totalIncSurveys % batchSize == 0) {
				newTotalPages = totalIncSurveys / batchSize;
			} else {
				newTotalPages = parseInt(totalIncSurveys / batchSize) + 1;
			}
			$('#paginate-total-pages').html(newTotalPages);
			for (var i = 0; i < incompleteSurveyIds.length; i++) {
				$('div[data-iden="sur-pre-' + incompleteSurveyIds[i] + '"]').remove();
			}

			$('#overlay-toast').html('Survey reminder request deleted successfully');
			showToast();

			// update the page
			var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
			paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);

			// Update the incomplete survey on dashboard
			getIncompleteSurveyCount(colName, colValue);

			$('#del-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#resend-mult-sur-icn').removeClass('mult-sur-icn-active');
		}
	}, true, {});
}

$(document).on('click', '#resend-mult-sur-icn.mult-sur-icn-active', function() {
	var selectedSurveys = $('#icn-sur-popup-cont').data('selected-survey');
	resendMultipleIncompleteSurveyRequests(selectedSurveys);
});

function resendMultipleIncompleteSurveyRequests(incompleteSurveyIds) {
	showOverlay();
	callAjaxPOSTWithTextData("/resendmultipleincompletesurveyrequest.do?surveysSelected=" + incompleteSurveyIds, function(data) {
		data = JSON.parse(data);
		if (data.errMsg == undefined || data.errMsg == "") {
			// unselect all the options after deleting
			$('#icn-sur-popup-cont').data('selected-survey', []);

			var toastmsg = data.success;
			$('#overlay-toast').html(toastmsg);
			showToastLong();
			
			$('#del-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#resend-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#icn-sur-popup-cont').data('selected-survey', []);
			$('.sur-icn-checkbox').addClass('sb-q-chk-yes').removeClass('sb-q-chk-no');

			// Update the incomplete survey on dashboard
			startIndexInc = 0;
			doStopIncompleteSurveyPostAjaxRequest = false;
			fetchIncompleteSurvey(false);
			$('#dsh-inc-srvey').perfectScrollbar('update');

			// update the page
			var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
			paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
		}else{
			var toastmsg = data.errMsg;
			$('#overlay-toast').html(errCode);
			showToastLong();
		}
	}, true, {});
}

function bindDatePickerforSurveyDownload() {
	// initializing datepickers
	var startDate;
	var fromEndDate = new Date();
	var toEndDate = new Date();
	$("#dsh-start-date").datepicker({
		orientation : "auto",
		format : 'mm/dd/yyyy',
		endDate : fromEndDate,
		todayHighlight : true,
		clearBtn : true,
		autoclose : true
	}).on('changeDate', function(selected) {
		if (selected.date == undefined) {
			startDate = null; // reset start date
		} else {
			startDate = new Date(selected.date.valueOf());
			startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
		}
		$('#dsh-end-date').datepicker('setStartDate', startDate);
	});

	$("#dsh-end-date").datepicker({
		orientation : "auto",
		format : 'mm/dd/yyyy',
		endDate : toEndDate,
		todayHighlight : true,
		clearBtn : true,
		autoclose : true
	}).on('changeDate', function(selected) {
		if (selected.date == undefined) {
			fromEndDate = null; // reset end date
		} else {
			fromEndDate = new Date(selected.date.valueOf());
			fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
		}
		$('#dsh-start-date').datepicker('setEndDate', fromEndDate);
	});
}

function bindDatePickerforIndividualSurveyDownload() {
	// initializing datepickers
	var startDate;
	var fromEndDate = new Date();
	var toEndDate = new Date();
	$("#indv-dsh-start-date").datepicker({
		orientation : "auto",
		format : 'mm/dd/yyyy',
		endDate : fromEndDate,
		todayHighlight : true,
		clearBtn : true,
		autoclose : true
	}).on('changeDate', function(selected) {
		if(selected.date != undefined){
			startDate = new Date(selected.date.valueOf());
			startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
		}else{
			startDate = null;
		}
		$('#indv-dsh-end-date').datepicker('setStartDate', startDate);
	});

	$("#indv-dsh-end-date").datepicker({
		orientation : "auto",
		format : 'mm/dd/yyyy',
		endDate : toEndDate,
		todayHighlight : true,
		clearBtn : true,
		autoclose : true
	}).on('changeDate', function(selected) {
		if(selected.date != undefined){
			fromEndDate = new Date(selected.date.valueOf());
			fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
		}else{
			fromEndDate = null;
		}
		$('#indv-dsh-start-date').datepicker('setEndDate', fromEndDate);
	});
}

function editPositions(disableEle) {
	callAjaxGET("/geteditpositions.do", function(data) {
		createEditPositionsPopup("Edit positions", data);

		addDatePcikerForPositions();
		$('.pos-edit-icn').click(function() {
			$(this).parent().find('input').prop('readonly', false);
		});
		$('.add-pos-link').click(function() {
			var htmlToAppned = "<div class='pos-cont margin-top-10 text-left'>" + "<div class='checkbox-input-cont'>" + "<div class='checkbox-input checkbox-iscurrent' data-checked='false'></div>" + "Current Employer</div>" + "<input name='companyName' class='pos-input' placeholder='Company Name'>" + "<input name='title' class='pos-input' placeholder='Job Title'>" + "<input name='startTime' class='pos-input'placeholder='Start Date'>" + "<input name='endTime' class='pos-input' placeholder='End Date'>" + "<div class='pos-remove-icn'></div>" + "</div>";
			$(this).before(htmlToAppned);
			// $(this).remove();
			addDatePcikerForPositions();
		});
	}, true, disableEle);

}
/**
 * Method to call warning popup controller method.
 */
function editProfileUrl(disableEle) {
	callAjaxGET("/showurleditwarning.do", function(data) {
		createEditProfileUrlPopup("Warning", data);
	}, true, disableEle);

}
// Get all the required elements and show popup

function generateWidget(clickedAttr, iden, profileLevel) {
	$('.v-hr-tbl-icn-wraper').hide();
	if ($(clickedAttr).hasClass('v-tbl-icn-disabled')) {
		return;
	} else {
		callAjaxGET("./showwidgetpage.do?profileLevel=" + profileLevel + "&iden=" + iden, callBackShowWidget, true);
	}
}

function callBackShowWidget(data) {
	var header = "Widget";
	createWidgetPopup(header, data);

	$('#overlay-continue').click(function() {
		copyWidgetToClipboard("widget-code-area");
		$('#overlay-continue').unbind('click');
	});

	$('.overlay-disable-wrapper').addClass('pu_arrow_rt');
	disableBodyScroll();
	// $('body').css('overflow', 'hidden');
	$('body').scrollTop('0');
}

function createWidgetPopup(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Copy to clipboard");
	$('#overlay-cancel').html("Close");

	$('#overlay-main').show();
}

function copyWidgetToClipboard(elementId) {
	var encoded = document.getElementById(elementId).innerHTML;
	copyToClipboard( encoded, "Copied to clipboard", "Unable to copy to clicboard" );
}

/**
 * Warning popup
 * 
 * @param header
 * @param body
 */
function createEditProfileUrlPopup(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Continue");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-continue').off();
	$('#overlay-continue').click(function() {
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		updateProfileUrl();
		overlayRevert();
	});

	$('#overlay-main').show();
	disableBodyScroll();
}
function createEditProfileUrlPopup2(body) {

	$('#overlay-text').html(body);
	$('#overlay-continue').html("Submit");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-continue').off();
	$('#overlay-continue').click(function() {
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		saveProfileUrl();
		overlayRevert();
	});

	$('#overlay-main').show();
	disableBodyScroll();
}

function noScreenNameSavedBind() {
	$('#no-screen-name-saved').show();
	$('#screen-name-saved').hide();
	$('#overlay-no-screen-name-saved-cancel-zillow').click(function() {
		overlayRevert();
		$('#zillow-popup').hide();
		$('#zillow-popup-body').html('');
	});
	
	$('#overlay-screen-name-to-saved-zillow').click(function() {
		//call service to update screen name in the profile
		var zillowProfileName = $('input[name="zillowProfileName"]').val();
		if(!validInput(zillowProfileName)) {
			return;
		}

		var vertical = $('#profileType').val();
		var zillowProfileNameURI = ""; 
		if(vertical == 'Mortgage')
			zillowProfileNameURI = $('#zillowLenderPath').val();
		else
			zillowProfileNameURI = $('#zillowNonLenderURI').val();
		$('#zillow-profile-link').attr("href", zillowProfileNameURI + zillowProfileName);
		$('#zillow-profile-link').html(zillowProfileNameURI + zillowProfileName);
		
		screenNameSavedBind();
	});
	
	$('#overlay-disconnect-zillow-byscreen-name').click(function() {
		$('#no-screen-name-saved').hide();
		$('#no-screen-name-container').hide();
		$('#disconnect-zillow-container').show();
		var nmls = $('input[name="nmlsId"]').val();					
		var zillowProfileName = $('input[name="zillowProfileName"]').val();
		
		$('#overlay-cancel-disconnect-zillow').click(function() {
			overlayRevert();
		});
		
		$('#overlay-keepreview-disconnect-zillow').click(function() {
			disconnectZillow(profileType, zillowProfileName, nmls, "keep-review");
		});
		
		$('#overlay-deletereview-disconnect-zillow').click(function() {
			disconnectZillow(profileType, zillowProfileName, nmls, "delete-review");
		});
	});
}

function screenNameSavedBind() {
	$('#no-screen-name-saved').hide();
	$('#screen-name-saved').show();
	
	$('#overlay-save-zillow-screen-name').click(function() {
		var nmls = $('input[name="nmlsId"]').val();					
		var zillowProfileName = $('input[name="zillowProfileName"]').val();
		
		if(validInput(zillowProfileName)) {
			saveZillowProfile(profileType, zillowProfileName, nmls);
		}
	});
	
	$('#overlay-change-zillow-screen-name').click(function() {
		noScreenNameSavedBind();
	});
}


function createZillowProfileUrlPopup(body) {	
	$('#overlay-toast').text('');
	$('#zillow-popup-body').html(body);
	$('#overlay-header').hide();
	
	var vertical = $('#profileType').val();
	var nmlsHidden = $('#nmlsIdHidden').val();
	var screenHidden = $('#screenNameHidden').val();
	$('#screen-name-found-container').hide();
	//if no nmls/screen
	if(vertical == 'Mortgage') {
		if(screenHidden != "") {
			showZillowPageWithProfileLink(screenHidden);
		} else {
			$('#main-container').show();
			$('#screen-name-found-container').hide();
		}
	} else if(vertical != 'Mortgage' && screenHidden != "") {
		screenNameSavedBind();
	} else if(vertical != 'Mortgage' && screenHidden == "") {
		noScreenNameSavedBind();
	} else {
		$('#main-container').show();
		$('#screen-name-found-container').hide();
	}
	
	
	$('#no-screen-name-container').hide();
	$('#by-screen-name-container').hide();
	$('#disconnect-zillow-container').hide();
	$('#zillow-help-container').hide();
	
	var profileType = $('#profileType').val();
	//put condition for profileType
	if(profileType == 'Mortgage') {		
		//$('#overlay-continue').off();
		$('#overlay-continue-zillow').click(function() {
			//by screen name
			openNextScreenForZillowScreenName(profileType, "by-screen-name", null);
		});
		
		$('#overlay-next-zillow').click(function() {
			//show Screen non editable-name found by NMLS, Ok, Back, Cancle buttons 
			var nmlsId = $('input[name="nmlsId"]').val();		
			var formData = new FormData();
			formData.append("nmlsId", nmlsId);	
			if(validInput(nmlsId)) {
				openNextScreenForZillowScreenName(profileType, "next", nmlsId);
			}
		});
	} else { // non-mortgage
		
		$('#overlay-save-zillow').click(function() {
			$('#overlay-save-zillow').unbind('click');
			$('#overlay-cancel-zillow').unbind('click');
			var zillowProfileName = $('input[name="zillowProfileName"]').val();
			if(validInput(zillowProfileName)) {
				saveZillowProfile(profileType, zillowProfileName, "");
			}
		});
	}
	
	$('#overlay-cancel-zillow').click(function(){
		overlayRevert();
		$('#zillow-popup').hide();
		$('#zillow-popup-body').html('');
		//$("#overlay-pop-up").removeClass("overlay-disable-wrapper-zillow");
	});
	
	$('.all-cancel').click(function(){
		overlayRevert();
	});
	
	$('#zillow-popup').show();
	disableBodyScroll();
}

function validInput(value) {
	if(value == undefined || value == "" || value.trim().length == 0) {
		$('#overlay-toast').html("Please enter valid input");
		showToast();
		return false;
	} else 
		return true;
}

//toDisplayOnClose will be hidden on click of Help icon and will be displayed if Calcel is clicked on Help popup
function openHelpPopup(toDisplayOnClose) {
	$('#'+ toDisplayOnClose).hide();
	$('#zillow-help-container').show();
	
	$('#overlay-contact-support').click(function(){
		overlayRevert();
		//call help page
		showMainContent('./showhelppage.do');	
	});
	
	$('#overlay-contact-support-cancel').click(function(){
		$('#zillow-help-container').hide();
		$('.non-zillow-help-container').hide();
		$('#'+ toDisplayOnClose).show();
	});
}

/*
 * function saveZillowEmailAddress1(){ console.info("before zillosaveinfo is called"); callAjaxGET("/zillowSaveInfo.do", function(data) { createZillowProfileUrlPopupPath( data); }, true); }
 */
function createZillowProfileUrlPopupPath(body) {
	$('#overlay-text').html(body);
	$('#overlay-continue').html("ok");
	var profileType = $('#profileType').val();
	$('#overlay-continue').click(function() {
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		saveZillowEmailAddress(profileType);
		overlayRevert();
	});
}

/*
 * function updateProfileUrl(){ window.open("./editprofileurl.do","_blank", "width=800,height=600,scrollbars=yes"); }
 */

function updateProfileUrl() {
	callAjaxGET("/editprofileurl.do", function(data) {
		createEditProfileUrlPopup2(data);
	}, true);

}

$(document).on('click', '.checkbox-iscurrent', function(e) {
	var isCurrent = $(this).attr('data-checked');
	if (isCurrent == "true") {
		$(this).attr('data-checked', "false");
		$(this).parent().parent().find('input[name="endTime"]').show();
	} else {
		$(this).attr('data-checked', "true");
		$(this).parent().parent().find('input[name="endTime"]').hide();
	}
});

$(document).on('click', '.pos-remove-icn', function(e) {
	e.stopPropagation();
	$(this).parent().remove();
	updatePositions();
});

function addDatePcikerForPositions() {

	var startDate;
	var fromEndDate = new Date();
	var toEndDate = new Date();

	$('input[name="endTime"]').datepicker({
		orientation : "auto",
		format : "mm-yyyy",
		startView : "months",
		minViewMode : "months",
		endDate : toEndDate,
		todayHighlight : true,
		clearBtn : true,
		autoclose : true
	}).on('changeDate', function(selected) {
		fromEndDate = new Date(selected.date.valueOf());
		fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
		$(this).parent().find('input[name="startTime"]').datepicker('setEndDate', fromEndDate);
	});
	$('input[name="startTime"]').datepicker({
		orientation : "auto",
		format : "mm-yyyy",
		startView : "months",
		minViewMode : "months",
		endDate : toEndDate,
		todayHighlight : true,
		clearBtn : true,
		autoclose : true
	}).on('changeDate', function(selected) {
		startDate = new Date(selected.date.valueOf());
		startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
		$(this).parent().find('input[name="endTime"]').datepicker('setStartDate', startDate);
	});
}

function createEditPositionsPopup(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Save");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-continue').off();
	$('#overlay-continue').click(function() {
		updatePositions();
	});

	$('#overlay-main').show();
	disableBodyScroll();
}

function updatePositions() {
	var positions = [];
	var isFormValid = true;
	$('#prof-position-edit-container').find('.pos-cont').each(function() {
		var position = {};
		var companyName = $(this).find('input[name="companyName"]').val();
		var title = $(this).find('input[name="title"]').val();
		var startTime = $(this).find('input[name="startTime"]').val();
		var endTime = $(this).find('input[name="endTime"]').val();
		var startMonth, startYear, endMonth, endYear;
		var isCurrent = false;

		var isCurrentString = $(this).find('.checkbox-input-cont').find('.checkbox-iscurrent').attr("data-checked");

		if (isCurrentString == "true") {
			isCurrent = true;
		}

		if (companyName == undefined || companyName == '') {
			$(this).find('input[name="companyName"]').focus();
			$('#overlay-toast').html("Please enter company name");
			showToast();
			isFormValid = false;
			return false;
		}

		if (title == undefined || title == '') {
			$(this).find('input[name="title"]').focus();
			$('#overlay-toast').html("Please enter title");
			showToast();
			isFormValid = false;
			return false;
		}

		if (startTime == undefined || startTime == '') {
			$(this).find('input[name="startTime"]').focus();
			$('#overlay-toast').html("Please enter start time");
			showToast();
			isFormValid = false;
			return false;
		} else {
			var startDateSplit = startTime.split("-");
			if (startDateSplit.length < 2) {
				$('#overlay-toast').html("Please enter valid start time");
				showToast();
			}
			startMonth = parseInt(startDateSplit[0]);
			startYear = parseInt(startDateSplit[1]);
		}

		position["name"] = companyName;
		position["title"] = title;
		position["startTime"] = startTime;
		position["startMonth"] = startMonth;
		position["startYear"] = startYear;

		if (!isCurrent) {
			if (endTime == undefined || endTime == '') {
				$(this).find('input[name="endTime"]').focus();
				$('#overlay-toast').html("Please enter end time");
				showToast();
				isFormValid = false;
				return false;
			} else {
				var endDateSplit = endTime.split("-");
				if (endDateSplit.length < 2) {
					$('#overlay-toast').html("Please enter valid end time");
					showToast();
					isFormValid = false;
					return false;
				}
				endMonth = parseInt(endDateSplit[0]);
				endYear = parseInt(endDateSplit[1]);
				position["endTime"] = endTime;
				position["endMonth"] = endMonth;
				position["endYear"] = endYear;
			}
		}

		position["isCurrent"] = isCurrent;

		positions.push(position);
	});

	if (!isFormValid) {
		return;
	}

	if (positions.length > 0) {
		positions = JSON.stringify(positions);
	} else {
		$('#overlay-toast').html("No positions added.");
		showToast();
	}

	callAjaxPOSTWithTextData("/updatepositions.do?positions=" + encodeURIComponent(positions), function(data) {
		if (data == "success") {
			$('#overlay-toast').html("Positions updated successfully");
			showToast();
			updatePositionInLeftSection(positions);
			$('#overlay-cancel').click();
		}
		enableBodyScroll();
	}, true, {});
}

function updatePositionInLeftSection(positions) {
	var contentToAppend = "";
	var positionsArray = [];
	if (positions != undefined && positions != "")
		positionsArray = JSON.parse(positions);
	if (positionsArray.length > 0) {
		for ( var index in positionsArray) {
			var position = positionsArray[index];
			contentToAppend += '<div class="postions-content">';
			contentToAppend += '<div class="lp-pos-row-1 lp-row clearfix">' + position.name + '</div>';
			contentToAppend += '<div class="lp-pos-row-2 lp-row clearfix">' + position.title + '</div>';
			if (position.isCurrent) {
				contentToAppend += '<div class="lp-pos-row-3 lp-row clearfix">' + position.startTime + ' - Current' + '</div>';
			} else {
				contentToAppend += '<div class="lp-pos-row-3 lp-row clearfix">' + position.startTime + ' - ' + position.endTime + '</div>';
			}
		}
	} else {
		contentToAppend = "No positions added yet";
	}

	$('#positions-container').html(contentToAppend);
}

$(document).on('click', '#hdr-config-settings-dropdown', function(e) {
	$('#hdr-link-item-dropdown').toggle();
});

$(document).on('mouseover', '#hdr-link-item-config', function(e) {
	$('#hdr-link-item-dropdown').show();
});

$(document).on('mouseout', '#hdr-link-item-config', function(e) {
	$('#hdr-link-item-dropdown').hide();
});

$(document).on('click', '.hdr-link-item-dropdown-item', function(e) {
	$('#hdr-link-item-dropdown').hide();
	showOverlay();
});

$(document).on('click', '#hdr-sm-settings-dropdown', function(e) {
	$('#hdr-link-item-dropdown-sm').toggle();
});

$(document).on('mouseover', '#hdr-link-item-sm', function(e) {
	$('#hdr-link-item-dropdown-sm').show();
});

$(document).on('mouseout', '#hdr-link-item-sm', function(e) {
	$('#hdr-link-item-dropdown-sm').hide();
});

$(document).on('click', '.hdr-link-item-dropdown-item-sm', function(e) {
	$('#hdr-link-item-dropdown-sm').hide();
	showOverlay();
});

$(document).on('click', '#hdr-dashboard-dropdown', function(e) {
	$('#hdr-link-item-dropdown-dash').toggle();
});

$(document).on('mouseover', '#hdr-dashboard-item', function(e) {
	$('#hdr-link-item-dropdown-dash').show();
});

$(document).on('mouseout', '#hdr-dashboard-item', function(e) {
	$('#hdr-link-item-dropdown-dash').hide();
});

// Help page onclick function
$(document).on('click', '#send-help-mail-button', function() {
	var subject = "";
	var message = "";
	var emailId = "";

	if ($("#email-id").val() != undefined) {
		emailId = $("#email-id").val().trim();
	}

	if ($("#subject-id").val() != undefined) {
		subject = $("#subject-id").val().trim();
	}

	if ($("#user-message").val() != undefined) {
		message = $("#user-message").val().trim();
	}

	if ((emailId == "") || (emailId == undefined)) {
		$('#overlay-toast').html('Please enter a valid email address');
		showToast();
		return;
	}

	if (emailRegex.test(emailId) != true) {
		$('#overlay-toast').html('Please enter a valid email address');
		showToast();
		return;
	}

	if (subject == "" || subject == undefined) {
		$('#overlay-toast').html('Please enter the subject');
		showToast();
		return;
	}

	if ((message == "") || (message == undefined)) {
		$('#overlay-toast').html('Please enter the message');
		showToast();
		return;
	}

	var payload = {
		"subject" : subject,
		"mailText" : message,
		"emailId" : emailId
	};

	callAjaxPostWithPayloadData("./sendhelpmailtoadmin.do", function(data) {
		$('#overlay-toast').html('Message sent successfully!');
		$("#subject-id").val("");
		$("#user-message").val("");
		showToast();
	}, payload, true, '#send-help-mail-button');
});

// Disconnect social media
function disconnectSocialMedia(event, socialMedia, isAutoLogin) {
	event.stopPropagation();
	event.stopImmediatePropagation();
	event.preventDefault();
	if (isAutoLogin) {
		$('#overlay-toast').html('Insufficient permission to disconnect from ' + socialMedia);
		showToast();
		return;
	}
	if (socialMedia != "google business" && ($('div[data-social="' + socialMedia + '"]').text() == undefined || $('div[data-social="' + socialMedia + '"]').text() == '')) {
		return;
	}
	if (socialMedia == 'linkedin') {
		keepFeed();
	} else {
		$('#overlay-header').html("Social Feed");
		$("#overlay-text").html("What would you like to do with your current news feed?");
		$('#overlay-continue').html("Keep");
		$('#overlay-cancel').html("Delete");
		$('#overlay-continue').attr("onclick", "");
		$('#overlay-main').show();
		$('#overlay-continue').click(function() {
			keepFeed();
			overlayRevert();
		});
		$('#overlay-cancel').click(function() {
			$('#overlay-continue').unbind('click');
			$('#overlay-cancel').unbind('click');
			overlayRevert();
			deleteFeed();
		});
	}

	function deleteFeed() {
		// delete feed function
		showOverlay();
		processSocialMediaDisconnect(true);
	}

	function keepFeed() {
		showOverlay();
		processSocialMediaDisconnect(false);
	}

	function processSocialMediaDisconnect(removeFeed) {
		var payload = {
			"socialMedia" : socialMedia,
			"removeFeed" : removeFeed
		};

		callAjaxPostWithPayloadData("/disconnectsocialmedia.do", function(data) {
			if (data == "success") {
				$('div[data-social="' + socialMedia + '"]').html('');
				$('div[data-social="' + socialMedia + '"]').parent().find('.social-media-disconnect').addClass('social-media-disconnect-disabled').removeAttr("onclick").removeAttr("title");
				$('#overlay-toast').html('Successfully disconnected ' + socialMedia);
				hideOverlay();
				showToast();
			} else {
				$('#overlay-toast').html('Some error occurred while disconnecting ' + socialMedia);
				hideOverlay();
				showToast();
			}
		}, payload, true);
	}
	/*
	 * var payload = { "socialMedia" : socialMedia };
	 * 
	 * callAjaxPostWithPayloadData("/disconnectsocialmedia.do", function(data) { if(data == "success"){ $('div[data-social="'+socialMedia+'"]').html(''); $('div[data-social="'+socialMedia+'"]').parent().find('.social-media-disconnect').addClass('social-media-disconnect-disabled').removeAttr("onclick").removeAttr("title"); $('#overlay-toast').html('Successfully disconnected ' + socialMedia); showToast(); } else { $('#overlay-toast').html('Some error occurred while disconnecting ' + socialMedia); showToast(); } }, payload, true);
	 */
}

function showProfileLinkInEditProfilePage(source, profileUrl) {
	$('.social-item-icon[data-source="' + source + '"').attr('data-link', profileUrl).removeClass('icn-social-add');
}

function removeProfileLinkInEditProfilePage(source) {
	$('.social-item-icon[data-source="' + source + '"').attr('data-link','' ).addClass('icn-social-add');
}

function showSurveysUnderResolution(startIndexCmp, batchSizeCmp) {
	var payload = {
		"startIndex" : startIndexCmp,
		"batchSize" : batchSizeCmp
	};
	callAjaxGetWithPayloadData("./fetchsurveysunderresolution.do", function(data) {
		if (startIndexCmp == 0)
			$('#sur-under-res-list').html(data);
		else
			$('#sur-under-res-list').append(data);

		startIndexCmp += batchSizeCmp;
	}, payload, true);
}
var surveyId = 4;
// Send Survey Agent
$(document).on('input', '#wc-review-table-inner[data-role="agent"] input', function() {
	$(this).removeClass('error-survey');
	$(this).siblings().addClass('hidden');
	var parentDiv = $(this).parent().parent();
	if (parentDiv.is(':last-child')) {
		var htmlData = '<div id="survey-' + surveyId + '" class="wc-review-tr  clearfix">' + '<div class="wc-review-tc1 survey-fname float-left"><input class="wc-review-input wc-review-fname" placeholder="First Name"><div class="validation validationfname hidden"></div></div>' + '<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-lname" placeholder="Last Name"><div class="validation validationlname hidden"></div></div>' + '<div class="wc-review-tc3 survey-email float-left"><input class="wc-review-input wc-review-email" placeholder="Email"><div class="validation validationemail hidden"></div></div>' + '<div class="wc-review-tc4 last float-left"><div class="wc-review-rmv-icn hide"></div></div>' + '</div>';
		parentDiv.after(htmlData);
		surveyId++;
		// enable remove button
		if ($('#wc-review-table-inner').children().length > 2) {
			$('.wc-review-rmv-icn').show();
		}
		$('#wc-review-table-inner').find(':nth-child(3)').find('.wc-review-rmv-icn').hide();

		// setting up perfect scrollbar
		setTimeout(function() {
			$('#wc-review-table').perfectScrollbar();
			$('#wc-review-table').perfectScrollbar('update');
		}, 1000);
	}
});

var surveyAdminId = 4;
// Send Survey Admin
$(document).on('input', '#wc-review-table-inner[data-role="admin"] input', function() {
	$(this).removeClass('error-survey');
	$(this).siblings().addClass('hidden');
	var parentDiv = $(this).parent().parent();
	if (parentDiv.is(':last-child')) {
		var htmlData = '<div id="survey-' + surveyAdminId + '"class="wc-review-tr clearfix">' + '<div class="wc-review-tc1 survey-user float-left pos-relative"><input data-name="agent-name" class="wc-review-input wc-review-agentname" placeholder="User Name"><div class="validation validationagent hidden"></div></div>' + '<div class="wc-review-tc2 survey-fname float-left"><input class="wc-review-input wc-review-fname" placeholder="First Name"><div class="validation validationfname hidden"></div></div>' + '<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-lname" placeholder="Last Name"><div class="validation validationlname hidden"></div></div>' + '<div class="wc-review-tc4 survey-email float-left"><input class="wc-review-input wc-review-email" placeholder="Email"><div class="validation validationemail hidden"></div></div>' + '<div class="wc-review-tc5 last float-left"><div class="wc-review-rmv-icn hide"></div></div>' + '</div>';
		parentDiv.after(htmlData);
		surveyAdminId++;
		// enable remove button
		if ($('#wc-review-table-inner').children().length > 2) {
			$('.wc-review-rmv-icn').show();
		}
		$('#wc-review-table-inner').find(':nth-child(3)').find('.wc-review-rmv-icn').hide();

		// setting up perfect scrollbar
		setTimeout(function() {
			$('#wc-review-table').perfectScrollbar();
			$('#wc-review-table').perfectScrollbar('update');
		}, 1000);

		attachAutocompleteAgentSurveyInviteDropdown();
	}
});

$(document).on('click', '.wc-review-rmv-icn', function() {
	var parentDiv = $('#wc-review-table-inner');

	// disable remove button
	if (parentDiv.children().length <= 4) {
		$('.wc-review-rmv-icn').hide();
	}
	$(this).parent().parent().remove();

	// setting up perfect scrollbar
	setTimeout(function() {
		$('#wc-review-table').perfectScrollbar();
		$('#wc-review-table').perfectScrollbar('update');
	}, 1000);
});


var surveysent=false;
$(document).on('click', '#wc-send-survey', function() {
	var allowrequest = true;
	var receiversList = [];
	var agentId = undefined;
	var columnName = undefined;
	var firstname = "";
	var lastname = "";
	var idx = 0;
	var agentname = "";
	var myself = false;
	
	if(surveysent ){
		return;
	}
	
	
	if ( !removeErrorMessagesAndDetermineIfRequiredDataIsPresent() ) {
		allowrequest = checkIfRequestCanBeMadeAndDisplayErrorMessagesIfNeeded();
	}

	$('#wc-review-table-inner').children().each(function() {
		if (!$(this).hasClass('wc-review-hdr')) {
			firstname = $(this).find('input.wc-review-fname').first().val();
			lastname = $(this).find('input.wc-review-lname').first().val();
			agentname = $(this).find('input.wc-review-agentname').first().attr('agent-name');
			var emailId = $(this).find('input.wc-review-email').first().val();

			var dataName = $(this).find('input.wc-review-agentname').first().attr('data-name');
			if (dataName == 'agent-name') {
				agentId = $(this).find('input.wc-review-agentname').first().attr('agent-id');
				var agentEmailId = $(this).find('input.wc-review-agentname').first().attr('email-id');

				if (idx == 0) {
					columnName = $(this).find('input.wc-review-agentname').first().attr('column-name');
					idx++;
				}
			} else {
				agentEmailId = $("#wc-review-table-inner").attr('user-email-id');
				agentId = "";
				agentname = "";
				myself = true;
			}
			// when no data is typed
			if (agentId == undefined) {
				if (firstname != "" || emailId != "" || lastname != "") {
					$(this).find('.wc-review-agentname').addClass("error-survey");
					$(this).find('.validationagent').html("User not found.").removeClass("hidden");
					allowrequest = false;
					if (firstname == "") {
						$(this).find('.wc-review-fname').addClass("error-survey");
						$(this).find('.validationfname').html("Firstname is required.").removeClass("hidden");
					}
					if (emailId == "") {
						$(this).find('.wc-review-email').addClass("error-survey");
						$(this).find('.validationemail').html("EmailId is required.").removeClass("hidden");
					}

				}
			}// when username is typed and is not matched with exisiting users
			else if (agentname != "" && agentId == "") {

				$(this).find('.wc-review-agentname').addClass("error-survey");
				$(this).find('.validationagent').html("User not found.").removeClass("hidden");
				allowrequest = false;
				if (firstname == "") {
					$(this).find('.wc-review-fname').addClass("error-survey");
					$(this).find('.validationfname').html("Firstname is required.").removeClass("hidden");
				}
				if (emailId == "") {
					$(this).find('.wc-review-email').addClass("error-survey");
					$(this).find('.validationemail').html("EmailId is required.").removeClass("hidden");
				}
			}

			// when username is typed and then removed
			else if (agentname == "") {
				if (firstname != "" || emailId != "" || lastname != "") {
					if (!myself) {
						$(this).find('.wc-review-agentname').addClass("error-survey");
						$(this).find('.validationagent').html("User not found.").removeClass("hidden");
						allowrequest = false;
					}

					if (firstname == "") {
						$(this).find('.wc-review-fname').addClass("error-survey");
						$(this).find('.validationfname').html("Firstname is required.").removeClass("hidden");
						allowrequest = false;
					}
					if (emailId == "") {
						$(this).find('.wc-review-email').addClass("error-survey");
						$(this).find('.validationemail').html("EmailId is required.").removeClass("hidden");
						allowrequest = false;
					}
				}

			}// when username is typed and also is matched with exisiting users
			else if (agentId != undefined && agentId != "") {

				if (firstname == "") {
					$(this).find('.wc-review-fname').addClass("error-survey");
					$(this).find('.wc-review-fname').siblings().css("display", "block");
					$(this).find('.validationfname').html("Firstname is required.").removeClass("hidden");
					allowrequest = false;
				}
				if (emailId == "") {
					$(this).find('.wc-review-email').addClass("error-survey");
					$(this).find('.wc-review-email').siblings().css("display", "block");
					$(this).find('.validationemail').html("EmailId is required.").removeClass("hidden");
					allowrequest = false;
				}
			}

			if (emailId != "") {
				if (customerEmailRegex.test(emailId.trim())) {
					var receiver = new Object();
					receiver.firstname = firstname;
					receiver.lastname = lastname;
					receiver.emailId = emailId;
					receiver.agentEmailId = agentEmailId;
					receiver.agentId = agentId;

					// check if agent mail id is not same as recipient mail id
					if (emailId == agentEmailId) {
						$('#overlay-toast').html("You can't a send survey request to the agent initiating the survey");
						allowrequest = false;
						return false;
					}
					receiversList.push({
						"key" : $(this).attr('id'),
						"value" : receiver
					});
				} else {
					$(this).find('.wc-review-email').addClass("error-survey");
					$(this).find('.validationemail').html("Enter a valid email.").removeClass("hidden");
					$(this).find('.wc-review-email').siblings().css("display", "block");
					allowrequest = false;
				}
			}
		}
	});

	// Check if recievers list empty
	/*
	 * if (receiversList.length == 0) { $('#overlay-toast').html('Add customers to send survey request!'); showToast(); allowrequest = false; return false; }
	 */

	// check if there is no duplicate entries
	var receiversListLength = receiversList.length;
	var surveyIndex = [];
	var duplicate = false;
	for (var i = 0; i < receiversListLength; i++) {
		for (var j = i + 1; j < receiversListLength; j++) {
			if (receiversList[i].value.emailId == receiversList[j].value.emailId && receiversList[i].value.agentEmailId == receiversList[j].value.agentEmailId) {
				surveyIndex.push(i);
				surveyIndex.push(j);
			}
		}
	}
	if (surveyIndex.length != 0) {
		duplicate = true;
		for (var k = 0; k < surveyIndex.length; k++) {
			$("#" + receiversList[surveyIndex[k]].key).find(".survey-email").find(':nth-child(1)').addClass("error-survey");
			$("#" + receiversList[surveyIndex[k]].key).find(".survey-email").find(':nth-child(2)').html("Duplicate not allowed.").removeClass("hidden");
		}

	}
	if (duplicate) {
		allowrequest = false;
		return false;
	}

	var rec = [];
	for (key in receiversList) {
		rec.push(receiversList[key].value);
	}
	var receiverList = JSON.stringify(rec);
	var payload = {
		"receiversList" : receiverList,
		"source" : 'agent'
	};
	if (columnName != undefined) {
		payload = {
			"receiversList" : receiverList,
			"source" : 'admin',
			"columnName" : columnName,
		};
	}
	var surveyed = [];
	var alreadysureyed = false;
	if (allowrequest) {
		callAjaxPostWithPayloadData("./getalreadysurveyedemailids.do", function(data) {
			var alreadySurveyedEmails = $.parseJSON(data);
			// To check if the email had already surveyed
			if (alreadySurveyedEmails.length != 0) {
				for (var i = 0; i < receiversListLength; i++) {
					for (var j = 0; j < alreadySurveyedEmails.length; j++) {
						if (receiversList[i].value.emailId == alreadySurveyedEmails[j]) {
							alreadysureyed = true;
							surveyed.push(i);
						}
					}
				}

				if (surveyed.length != 0) {
					for (var k = 0; k < surveyed.length; k++) {
						$("#" + receiversList[surveyed[k]].key).find(".survey-email").find(':nth-child(1)').addClass("error-survey");
						$("#" + receiversList[surveyed[k]].key).find(".survey-email").find(':nth-child(2)').html("Already surveyed.").removeClass("hidden");
					}

				}
				if (alreadysureyed) {
					allowrequest = false;
					return false;
				}

			} else {
				$('#send-survey-dash').removeClass("hide");
				if(surveysent){
					return;
				}
				surveysent=true;
				$('.ps-container').scrollTop(0).perfectScrollbar('update');
				callAjaxPostWithPayloadData("./sendmultiplesurveyinvites.do", function(data) {
					
					$('#send-survey-dash').addClass("hide");
					$('.overlay-login').hide();
					// Update the incomplete survey on dashboard
					getIncompleteSurveyCount(colName, colValue);
					if (data == "error") {
						showError("Error while sending survey request!");
						surveysent=false;
					} else if (data.indexOf("Success") > -1) {
						var response = $.parseJSON(data);
						if (response.surveySentCount == 1){
							showInfo(response.surveySentCount + ' Survey Request Sent Successfully!');
						    surveysent=false;
						}
							
						else{
							showInfo(response.surveySentCount + ' Survey Requests Sent Successfully!');
							surveysent=false;
						}
							
					} else {
						$('#overlay-toast').html(data);
					}

					enableBodyScroll();
				}, payload, true);
			}
		}, payload, true);
	}
});


function checkIfRequestCanBeMadeAndDisplayErrorMessagesIfNeeded(){
	var allowrequest = true;
	$('#wc-review-table-inner').children().each(function() {
		if (!$(this).hasClass('wc-review-hdr')) {
			$(this).children().each(function() {
				if ($(this).hasClass('survey-user')) {
					$(this).find(':nth-child(1)').addClass("error-survey");
					$(this).find(':nth-child(2)').html("User is required.").removeClass("hidden");
					allowrequest = false;
				} else if ($(this).hasClass('survey-fname')) {
					$(this).find(':nth-child(1)').addClass("error-survey");
					$(this).find(':nth-child(2)').html("Firstname is required.").removeClass("hidden");
					allowrequest = false;
				} else if ($(this).hasClass('survey-email')) {
					$(this).find(':nth-child(1)').addClass("error-survey");
					$(this).find(':nth-child(2)').html("Email is required.").removeClass("hidden");
					allowrequest = false;
				}
			});
		}
	});
	return allowrequest;
}

function removeErrorMessagesAndDetermineIfRequiredDataIsPresent(){
	
	var end = false;
	$('#wc-review-table-inner').children().each(function() {
		if (!$(this).hasClass('wc-review-hdr')) {
			$(this).children().each(function() {
				$(this).find(':nth-child(1)').removeClass("error-survey");
				$(this).find(':nth-child(2)').addClass("hidden");
				if (!$(this).hasClass('last')) {
					var input = $(this).children(":input").val();
					if (input != "") {
						end = true;
					}
				}
			});
		}
	});
	return end;
}


$(document).on('click', '#wc-skip-send-survey', function() {
	$('#overlay-send-survey').hide();
	enableBodyScroll();
});

function sendSurveyInvitation(disableEle) {
	disableBodyScroll();
	callAjaxGET("./sendsurveyinvitation.do", function(data) {
		$('#overlay-send-survey').html(data);
		if ($("#welcome-popup-invite").length) {
			$('#overlay-send-survey').removeClass("hide");
			$('#overlay-send-survey').show();
		}
	}, true, disableEle);
}

function sendSurveyInvitationAdmin(columnName, columnValue, disableEle) {
	disableBodyScroll();
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue
	};
	callAjaxGetWithPayloadData("./sendsurveyinvitationadmin.do", function(data) {
		$('#overlay-send-survey').html(data);
		if ($("#welcome-popup-invite").length) {
			$('#overlay-send-survey').removeClass("hide");
			$('#overlay-send-survey').show();
		}
		$('#wc-review-table').perfectScrollbar();
		$('#wc-review-table').perfectScrollbar('update');
		attachAutocompleteAgentSurveyInviteDropdown();
	}, payload, true, disableEle);
}

function linkedInDataImport() {
	disableBodyScroll();
	callAjaxGET("./linkedindataimport.do", function(data) {
		$('#overlay-linkedin-import').html(data);
		disableBodyScroll();
		if ($("#welocome-step1").length) {
			$('#overlay-linkedin-import').removeClass("hide");
			$('#overlay-linkedin-import').show();
		}
	}, true);
}

function revertMailContent(mailcategory, disableEle) {
	showOverlay();
	var payload = {
		"mailcategory" : mailcategory
	};
	callAjaxPostWithPayloadData('./revertsurveyparticipationmail.do', function(data) {
		showMainContent('./showemailsettings.do');
		hideOverlay();
		$('#overlay-main').hide();
		$("#overlay-toast").html(data);
		showToast();
	}, payload, true, disableEle);
}

// settings page event binding
$('body').on('click', '.st-dd-item-auto-post', function() {
	$('#rating-auto-post').val($(this).html());
	$('#st-dd-wrapper-auto-post').slideToggle(200);

	$('#ratingcategory').val('rating-auto-post');
	var rating = $('#rating-auto-post').val();
	var ratingParent = $('#rating-auto-post-parent');

	changeRatingPattern(rating, ratingParent);
	updatePostScore("rating-settings-form");
});

$('body').on('click', '.st-dd-item-min-post', function() {
	var pageHash = window.location.hash;
	if (pageHash.toLowerCase() == "#showcomplaintressettings") {
		$('#comp-rating-post').val($(this).html());
		$('#st-dd-wrapper-min-post').slideToggle(200);
		return;
	}

	$('#rating-min-post').val($(this).html());
	$('#st-dd-wrapper-min-post').slideToggle(200);

	$('#ratingcategory').val('rating-min-post');

	var rating = $('#rating-min-post').val();
	var ratingParent = $('#rating-min-post-parent');
	changeRatingPattern(rating, ratingParent);

	updatePostScore("rating-settings-form");
});

$('body').on('click', '#st-settings-location-on', function() {
	$('#othercategory').val('other-location');
	$('#other-location').val('false');

	$('#st-settings-location-off').show();
	$(this).hide();

	updateOtherSettings("other-settings-form");
});
$('body').on('click', '#st-settings-location-off', function() {
	$('#othercategory').val('other-location');
	$('#other-location').val('true');

	$('#st-settings-location-on').show();
	$(this).hide();

	updateOtherSettings("other-settings-form");
});

$('body').on('click', '#st-settings-payment-on', function() {
	$('#st-settings-payment-off').show();
	$(this).hide();
});
$('body').on('click', '#st-settings-payment-off', function() {
	$('#st-settings-payment-on').show();
	$(this).hide();
	showPaymentOptions();
});

$('body').on('click', '#st-delete-account', function(e) {
	e.stopPropagation();
	$('#other-account').val('true');
	createPopupConfirm("Delete Account", "This action cannot be undone.<br/>All user setting will be permanently deleted and your subscription will terminate permanently immediately.");
	overlayDeleteAccount();
});

$('body').on('click', '#st-settings-account-on', function(e) {
	e.stopPropagation();
	$('#other-account').val('false');
	createPopupConfirm("Enable Account", "Do you want to Continue?");
	overlayAccount();
});
$('body').on('click', '#st-settings-account-off', function(e) {
	e.stopPropagation();
	$('#other-account').val('true');
	createPopupConfirm("Cancel Your Subscription", "By cancelling your subscription, you will not be able to access your SocialSurvey profile after the current billing cycle. Also for Branch or Company Accounts, this will disable all accounts in your hierarchy under this account.<br/> Do you want to Continue?");
	overlayAccount();
});

$('body').on('blur', '#happy-text', function() {
	saveTextForMoodFlow($("#happy-text").val(), "happy");
});
$('body').on('blur', '#neutral-text', function() {
	saveTextForMoodFlow($("#neutral-text").val(), "neutral");
});
$('body').on('blur', '#sad-text', function() {
	saveTextForMoodFlow($("#sad-text").val(), "sad");
});

$('body').on('blur', '#happy-text-complete', function() {
	saveTextForMoodFlow($("#happy-text-complete").val(), "happyComplete");
});
$('body').on('blur', '#neutral-text-complete', function() {
	saveTextForMoodFlow($("#neutral-text-complete").val(), "neutralComplete");
});
$('body').on('blur', '#sad-text-complete', function() {
	saveTextForMoodFlow($("#sad-text-complete").val(), "sadComplete");
});
$('body').on('blur', '#happy-complete-url', function() {
	saveTextForMoodFlow($("#happy-complete-url").val(), "happyUrl");
});
$('body').on('blur', '#ok-complete-url', function() {
	saveTextForMoodFlow($("#ok-complete-url").val(), "okUrl");
});
$('body').on('blur', '#sad-complete-url', function() {
	saveTextForMoodFlow($("#sad-complete-url").val(), "sadUrl");
});
$('body').on('blur', '#opt-out-text', function() {
	storeOptOutText($("#opt-out-text").val(), "optOutText");
});

$('body').on('click', '.reset-icon', function() {
	var resetId = $(this).prev().attr('id');
	var resetTag = "";

	if (resetId == 'happy-text') {
		resetTag = 'happy';
	} else if (resetId == 'neutral-text') {
		resetTag = 'neutral';
	} else if (resetId == 'sad-text') {
		resetTag = 'sad';
	} else if (resetId == 'happy-text-complete') {
		resetTag = 'happyComplete';
	} else if (resetId == 'neutral-text-complete') {
		resetTag = 'neutralComplete';
	} else if (resetId == 'sad-text-complete') {
		resetTag = 'sadComplete';
	}
	showOverlay();
	resetTextForMoodFlow(resetTag, resetId);
});

$('body').on('click', '.reset-opt-out-icon', function() {
	var resetId = $(this).prev().attr('id');
	showOverlay();
	resetOptOutTextFlow(resetId);
});

$('body').on('click', '#atpst-chk-box', function() {
	if ($('#atpst-chk-box').hasClass('bd-check-img-checked')) {
		$('#atpst-chk-box').removeClass('bd-check-img-checked');
		updateAutoPostSetting(true, '#atpst-chk-box');
	} else {
		$('#atpst-chk-box').addClass('bd-check-img-checked');
		updateAutoPostSetting(false, '#atpst-chk-box');
	}
});

$('body').on('click', '#hide-pp-chk-box', function() {
	if ($('#hide-pp-chk-box').hasClass('bd-check-img-checked')) {
		$('#hide-pp-chk-box').removeClass('bd-check-img-checked');
		updateEntitySettings( "hidePublicPage", true);
	} else {
		$('#hide-pp-chk-box').addClass('bd-check-img-checked');
		updateEntitySettings( "hidePublicPage", false);
	}
});

$('body').on('click', '#hide-bread-crumb-chk-box', function() {
	if ($('#hide-bread-crumb-chk-box').hasClass('bd-check-img-checked')) {
		$('#hide-bread-crumb-chk-box').removeClass('bd-check-img-checked');
		updateEntitySettings( "hideFromBreadCrumb", true);
	} else {
		$('#hide-bread-crumb-chk-box').addClass('bd-check-img-checked');
		updateEntitySettings( "hideFromBreadCrumb", false);
	}
});

$('body').on('click', '#hidden-section-chk-box', function() {
	if ($('#hidden-section-chk-box').hasClass('bd-check-img-checked')) {
		$('#hidden-section-chk-box').removeClass('bd-check-img-checked');
		updateEntitySettings( "hiddenSection", true);
	} else {
		$('#hidden-section-chk-box').addClass('bd-check-img-checked');
		updateEntitySettings( "hiddenSection", false);
	}
});

$('body').on('click', '#mail-frm-cmpny-chk-box', function() {
	if ($('#mail-frm-cmpny-chk-box').hasClass('bd-check-img-checked')) {
		$('#mail-frm-cmpny-chk-box').removeClass('bd-check-img-checked');
		updateEntitySettings( "sendEmailFromCompany", true);
	} else {
		$('#mail-frm-cmpny-chk-box').addClass('bd-check-img-checked');
		updateEntitySettings( "sendEmailFromCompany", false);
	}
});

$('body').on('click', '#ovride-sm-chk-box', function() {
	if ($('#ovride-sm-chk-box').hasClass('bd-check-img-checked')) {
		$('#ovride-sm-chk-box').removeClass('bd-check-img-checked');
		updateEntitySettings( "allowOverrideForSocialMedia", true);
	} else {
		$('#ovride-sm-chk-box').addClass('bd-check-img-checked');
		updateEntitySettings( "allowOverrideForSocialMedia", false);
	}
});

$('body').on('click', '#atpst-lnk-usr-ste-chk-box', function() {
	if ($('#atpst-lnk-usr-ste-chk-box').hasClass('bd-check-img-checked')) {
		$('#atpst-lnk-usr-ste-chk-box').removeClass('bd-check-img-checked');
		updateAutoPostLinkToUserSiteSetting(true, '#atpst-lnk-usr-ste-chk-box');
	} else {
		$('#atpst-lnk-usr-ste-chk-box').addClass('bd-check-img-checked');
		updateAutoPostLinkToUserSiteSetting(false, '#atpst-lnk-usr-ste-chk-box');
	}
});

$('body').on('click', '#alw-ptnr-srvy-chk-box', function() {
	if ($('#alw-ptnr-srvy-chk-box').hasClass('bd-check-img-checked')) {
		updateAllowPartnerSurveySettingForCompany(true, '#alw-ptnr-srvy-chk-box');
	} else {
		updateAllowPartnerSurveySettingForCompany(false, '#alw-ptnr-srvy-chk-box');
	}
});

$('body').on('click', '#incld-fr-trans-mntr-chk-box', function() {
	if ($('#incld-fr-trans-mntr-chk-box').hasClass('bd-check-img-checked')) {
		updateUpdateTransactionMonitorForCompany(true, '#incld-fr-trans-mntr-chk-box');
	} else {
		updateUpdateTransactionMonitorForCompany(false, '#incld-fr-trans-mntr-chk-box');
	}
});
// Copy to clipboard check box action
$('body').on('click', '#copyto-clipboard-chk-box', function() {
	if ($('#copyto-clipboard-chk-box').hasClass('bd-check-img-checked')) {
		updateCopyToClipBoardSettings(true, '#copyto-clipboard-chk-box');
	} else {
		updateCopyToClipBoardSettings(false, '#copyto-clipboard-chk-box');
	}
});

$('body').on('click', '#survey-mail-thrhld-chk-box', function() {
	if ($('#survey-mail-thrhld-chk-box').hasClass('bd-check-img-checked')) {
		$('#survey-mail-thrhld-chk-box').removeClass('bd-check-img-checked');
		updateSendDigestMailSiteSetting(true, '#survey-mail-thrhld-chk-box');
	} else {
		$('#survey-mail-thrhld-chk-box').addClass('bd-check-img-checked');
		updateSendDigestMailSiteSetting(false, '#survey-mail-thrhld-chk-box');
	}
});

$('body').on('click', '#enable-login-chk-box', function() {
	if ($('#enable-login-chk-box').hasClass('bd-check-img-checked')) {
		$('#enable-login-chk-box').removeClass('bd-check-img-checked');
		showEnableLoginButton(true, '#enable-login-chk-box');
	} else {
		$('#enable-login-chk-box').addClass('bd-check-img-checked');
		showEnableLoginButton(false, '#enable-login-chk-box');
	}
});

$('body').on('click', '#soc-mon-access-chk-box', function() {
	if ($('#soc-mon-access-chk-box').hasClass('bd-check-img-checked')) {
		$('#soc-mon-access-chk-box').removeClass('bd-check-img-checked');
		enableSocialMonitorToggleSetting(true, '#soc-mon-access-chk-box');
	} else {
		$('#soc-mon-access-chk-box').addClass('bd-check-img-checked');
		enableSocialMonitorToggleSetting(false, '#soc-mon-access-chk-box');
	}
});

$('body').on('click', '.admin-access-chk-box', function() {
	var typeOfCheckBox = $(this).attr('data-typeOfCheckBox');
	var id;
	if(typeOfCheckBox == "branchAdminDeleteAccess")
		id = '#alw-br-admin-del-usr-chk-box';
	else if(typeOfCheckBox == "regionAdminDeleteAccess")
		id = '#alw-rgn-admin-del-usr-chk-box';
	else if(typeOfCheckBox == "branchAdminAddAccess")
		id = '#alw-br-admin-add-usr-chk-box';
	else if(typeOfCheckBox == "regionAdminAddAccess")
		id = '#alw-rgn-admin-add-usr-chk-box';
	if ($(id).hasClass('bd-check-img-checked')){
		$(id).removeClass('bd-check-img-checked');
		updateAdminAccess(true, typeOfCheckBox,id);
	} else {
		$(id).addClass('bd-check-img-checked');
		updateAdminAccess(false, typeOfCheckBox, id);
	}
});
		
$('body').on('click', '#incomplete-survey-delete-chk-box', function() {
	if ($('#incomplete-survey-delete-chk-box').hasClass('bd-check-img-checked')) {
		$('#incomplete-survey-delete-chk-box').removeClass('bd-check-img-checked');
		enableIncompleteSurveyDeleteToggleSetting(true, '#incomplete-survey-delete-chk-box');
	} else {
		$('#incomplete-survey-delete-chk-box').addClass('bd-check-img-checked');
		enableIncompleteSurveyDeleteToggleSetting(false, '#incomplete-survey-delete-chk-box');
	}
});

$('body').on('blur', '#digest-recipients', function() {
	
	// format email IDs
	var emails = $("#digest-recipients").val();
	
	if( emails == undefined ){
		return;
	}
	
	var payload = {
		"emails" : emails
	};
	
	callAjaxPostWithPayloadData("./updatedigestrecipients.do", function(data) {
		$('#overlay-toast').html(data);
		showToast();
	}, payload, true);
	
});

$('body').on('click', '#alw-ptnr-srvy-for-usr-chk-box', function(e) {
	e.stopPropagation();
	
	var isPartnerSurveyAllowed = true;
	
	if ($('#alw-ptnr-srvy-for-usr-chk-box').hasClass('bd-check-img-checked')) {		
		isPartnerSurveyAllowed = true;
	} else {		
		isPartnerSurveyAllowed = false;
	}
	
		$('#overlay-main').show();
		$('#overlay-continue').show();
		if (isPartnerSurveyAllowed) {
			$('#overlay-continue').html("Enable");
			$('#overlay-cancel').html("Cancel");
			$('#overlay-header').html("Allow Partner Survey");
			$('#overlay-text').html("Are you sure you want to enable partner survey for user ?");
		} else  {
			$('#overlay-continue').html("Disable");
			$('#overlay-cancel').html("Cancel");
			$('#overlay-header').html("Allow Partner Survey");
			$('#overlay-text').html("Are you sure you want to enable partner survey for user ?");
		}

		var userId = $('#selected-userid-hidden').val();
		$('#overlay-continue').attr("onclick", "updateAllowPartnerSurveySettingForUser(" + isPartnerSurveyAllowed + "," + userId + ");");
	
	
});

function updateAllowPartnerSurveySettingForUser(allowPartnerSurvey, userId ) {
	var disableEle = '#alw-ptnr-srvy-for-usr-chk-box';
	var payload = {
		"allowPartnerSurvey" : allowPartnerSurvey,
		"userId" : userId
	};
	
	callAjaxPostWithPayloadData("./updateallowpartnersurveyforuser.do",function(data) {
		$('#overlay-main').hide();
		if (data == "success"){
			if ($('#alw-ptnr-srvy-for-usr-chk-box').hasClass('bd-check-img-checked')){
				$('#alw-ptnr-srvy-for-usr-chk-box').removeClass('bd-check-img-checked');
			}else{
				$('#alw-ptnr-srvy-for-usr-chk-box').addClass('bd-check-img-checked');
			}
			$('#overlay-toast').html("Content updated successfully");
		}else{
			$('#overlay-toast').html(data);			
		}
		showToast();
	}, payload, true, disableEle);
	
}

$('body').on('click', '#vndsta-access-chk-box', function() {
	if ($('#vndsta-access-chk-box').hasClass('bd-check-img-checked')) {
		$('#vndsta-access-chk-box').removeClass('bd-check-img-checked');
		showOrHideListingsManager( true );
		showOrHideVendastaProductSettings( true );
		updateVendastaAccessSetting(true, '#vndsta-access-chk-box');
	} else {
		$('#vndsta-access-chk-box').addClass('bd-check-img-checked');
		showOrHideListingsManager( false );
		showOrHideVendastaProductSettings( false );
		updateVendastaAccessSetting(false, '#vndsta-access-chk-box');
	}
});

function showOrHideListingsManager(vendastaAccess)
{
	if( vendastaAccess == true || vendastaAccess == "true" )
		{
		$("#listings-manager-main").show();
		$("#listings-manager-slider").show();
		}
	else{
		$("#listings-manager-main").hide();
		$("#listings-manager-slider").hide();
	}
}

function showOrHideVendastaProductSettings(flag)
{	
	if( flag == true || flag == "true" )
	{
		$("#vndsta-setting-one").show();
		$("#vndsta-setting-two").show();
		$("#vndsta-setting-three").show();
	}
	else{
		$("#vndsta-setting-one").hide();
		$("#vndsta-setting-two").hide();
		$("#vndsta-setting-three").hide();
	}	
}

//NOTE: picture and caption are deprecated for facebook, facebook no longer supports "picture" and "caption" parameters
// Dashboard fb and twitter share
function getDashboardImageandCaption(loop) {
	var name = "";
	var designation = "";
	var company = "";
	var pictureandCaptionLink = "";
	var fblink = "";
	if ($("#fb_" + loop) != undefined) {

		fblink = $("#fb_" + loop).data('link');
	}

	var imgId = $('#dsh-prsn-img').attr('data-img') || "";

	if (document.getElementById("name") != null) {
		name = document.getElementById("name").innerHTML;
	}

	if (document.getElementById("designation") != null) {
		designation = document.getElementById("designation").innerHTML;
	}

	if (document.getElementById("company") != null) {
		company = document.getElementById("company").innerHTML;

	}

	pictureandCaptionLink = "&picture=" + imgId + "&caption=" + name + "," + designation + "," + company;
	fblink = fblink.concat(pictureandCaptionLink);
	if (document.getElementById('fb_' + loop) != null)
		document.getElementById('fb_' + loop).setAttribute('data-link', fblink);
}

function twitterDashboardFn(loop, twitterElement) {
	var twitText = "";
	var twitLink = $("#twitt_" + loop).data('link');
	var String = twitLink.substring(twitLink.indexOf("=") + 1, twitLink.lastIndexOf("&"));
	var twitId = 'twttxt_' + loop;
	if ($("#" + twitId) != undefined) {
		twitText = $("#" + twitId).val();
	}

	var length = twitText.length;
	if (length > 180) {
		var arr = twitLink.split('');
		var twittStrnDot = "...";
		var substringed = twitText.substring(0, 176);
		var finalString = substringed.concat(twittStrnDot);
		if ($("#" + twitId) != undefined) {
			$("#" + twitId).val(finalString);
		}

		twitLink = twitLink.replace(String, finalString);
		if (document.getElementById('twitt_' + loop) != undefined)
			document.getElementById('twitt_' + loop).setAttribute('data-link', twitLink);
	}

}

// Edit profile fb and twitter share functions
function twitterProfileFn(loop, twitterElement) {

	var twitLink = $("#twitt_" + loop).data('link');
	var String = twitLink.substring(twitLink.indexOf("=") + 1, twitLink.lastIndexOf("&"));
	var twitId = 'twttxt_' + loop;
	var twitText = $("#" + twitId).val();
	var length = twitText.length;
	if (length > 180) {
		var arr = twitLink.split('');
		var twittStrnDot = "...";
		var substringed = twitText.substring(0, 176);
		var finalString = substringed.concat(twittStrnDot);
		$("#" + twitId).val(finalString);
		twitLink = twitLink.replace(String, finalString);

		if (document.getElementById('twitt_' + loop) != null) {

			document.getElementById('twitt_' + loop).setAttribute('data-link', twitLink);
		}

	}

}
function getImageandCaptionProfile(loop) {

	var pictureandCaptionLink = "";
	var fblink = $("#fb_" + loop).data('link');
	var name = "";
	var title = "";
	var vertical = "";
	var imgid = "";
	if (document.getElementById("prof-image-edit") != null && document.getElementById("prof-image-edit").getAttribute("src") != null) {

		imgid = document.getElementById("prof-image-edit").getAttribute("src");
	}
	if ($("#prof-name") != undefined) {
		name = $("#prof-name").val();
	}
	if ($("#prof-title") != undefined) {

		title = $("#prof-title").val();
	}
	if ($("#prof-vertical") != undefined) {

		vertical = $("#prof-vertical").val();
	}
	pictureandCaptionLink = "&picture=" + imgid + "&caption=" + name + "," + title + "," + vertical;

	fblink = fblink.concat(pictureandCaptionLink);
	if (document.getElementById('fb_' + loop) != null) {
		document.getElementById('fb_' + loop).setAttribute('data-link', fblink);
	}

}

var isSocialMonitorPostAjaxRequestRunning = false;
var doStopSocialMonitorPostAjaxRequest = false;
var isSocialMonitorPostLoaderRunning = false;
var socialMonitorPostBatch = [];

function postsSearch() {
	proPostStartIndex = 0;
	$('#prof-posts').html("");
	socialMonitorPostBatch = [];
	doStopSocialMonitorPostAjaxRequest = false;
	fetchSearchedPostsSolr(false);
}

function fetchSearchedPostsSolr(isNextBatch) {

	if (!isNextBatch && socialMonitorPostBatch.length > 0) {
		var posts = socialMonitorPostBatch.slice(0, proPostBatchSize);
		if (socialMonitorPostBatch.length > proPostBatchSize) {
			socialMonitorPostBatch = socialMonitorPostBatch.slice(proPostBatchSize);
		} else {
			socialMonitorPostBatch = [];
		}

		// paint the posts
		showLoaderOnPagination($('#prof-posts'));
		isSocialMonitorPostLoaderRunning = true;
		setTimeout(function() {
			isSocialMonitorPostLoaderRunning = false;
			paintPostsSolr(posts);
		}, 500);

		// Fetch the next batch
		if (!doStopSocialMonitorPostAjaxRequest && socialMonitorPostBatch.length <= proPostBatchSize) {
			fetchSearchedPostsSolr(true);
		}
		return;
	}

	if (!isNextBatch) {
		showLoaderOnPagination($('#prof-posts'));
	}

	if (isSocialMonitorPostAjaxRequestRunning)
		return;// Return if posts fetch is still working

	var entityType = $("#select-hierarchy-level").val();
	var entityId;
	entityId = $("#selected-entity-id-hidden").val();
	if (entityType == undefined || entityType == "companyId") {
		entityType = "companyId";
		entityId = companyIdForSocialMonitor;
	} else if (entityId == undefined || entityId <= 0) {
		$('#overlay-toast').html("Please select a valid " + $("#select-hierarchy-level").find(':selected').data('entity'));
		showToast();
		return;
	}
	var searchQuery = $("#post-search-query").val();

	var payload = {
		"entityType" : entityType,
		"entityId" : entityId,
		"batchSize" : proPostBatchSize,
		"startIndex" : proPostStartIndex,
		"searchQuery" : searchQuery
	};

	isSocialMonitorPostAjaxRequestRunning = true;
	callAjaxGetWithPayloadData("./searchSocialPosts.do", function(response, e) {
		isSocialMonitorPostAjaxRequestRunning = false;
		var data = $.parseJSON(response);
		var posts = data.socialMonitorPosts;
		var profilePics = data.profileImageUrlDataList;

		// check if posts are empty
		if (proPostStartIndex == 0 && posts.length <= 0) {
			doStopSocialMonitorPostAjaxRequest = true;
			hideLoaderOnPagination($('#prof-posts'));
			return;
		} else if (posts.length < proPostBatchSize) {
			doStopSocialMonitorPostAjaxRequest = true;
		}
		proPostStartIndex += proPostBatchSize;

		// Process images
		for (var i = 0; i < posts.length; i++) {
			var post = posts[i];
			var profileImg = "";
			$.each(profilePics, function(i, pic) {
				if (pic.profileImageUrl != "") {
					// post by region
					if (post.regionId > 0 && pic.entityType == "regionId" && pic.entityId == post.regionId) {
						profileImg = pic.profileImageUrl;
						// post by branch
					} else if (post.branchId > 0 && pic.entityType == "branchId" && pic.entityId == post.branchId) {
						profileImg = pic.profileImageUrl;
						// post by agent
					} else if (post.agentId > 0 && pic.entityType == "userId" && pic.entityId == post.agentId) {
						profileImg = pic.profileImageUrl;
						// post by company
					} else if (post.companyId > 0 && post.regionId <= 0 && post.branchId <= 0 && post.agentId <= 0 && pic.entityType == "companyId" && pic.entityId == post.companyId) {
						profileImg = pic.profileImageUrl;
					}
				}
			});
			post["profileImage"] = profileImg;
		}
		;

		// update the batch
		socialMonitorPostBatch = socialMonitorPostBatch.concat(posts);

		if (isNextBatch) {
			// Fetch the next batch
			if (!doStopSocialMonitorPostAjaxRequest && socialMonitorPostBatch.length <= proPostBatchSize) {
				fetchSearchedPostsSolr(true);
			}
		} else if (posts && posts.length > 0) {
			fetchSearchedPostsSolr(false);
		}

	}, payload, true);
}

function paintPostsSolr(posts, entityType, entityId) {
	var divToPopulate = "";
	$.each(posts, function(i, post) {
		var profImgClass = "sm-default-img";
		var profImgStyle = "";
		var iconClass = "";
		var href = "javascript:void(0)";
		if (post.source == "google") {
			iconClass = "icn-gplus";
		} else if (post.source == "SocialSurvey")
			iconClass = "icn-ss";
		else if (post.source == "facebook") {
			iconClass = "icn-fb";
			href = "http://www.facebook.com/" + post.postId;
		} else if (post.source == "twitter") {
			iconClass = "icn-twit";
			var res = post.postText.split("http");
			href = "http" + res[1];
		} else if (post.source == "linkedin") {
			iconClass = "icn-lin";
		}
		if (typeof post.postUrl != "undefined") {
			href = post.postUrl;
		}
		var profileImg = post.profileImage;
		if (profileImg != "") {
			profImgClass = "sm-custom-img";
			profImgStyle = 'style="background:url(' + profileImg + ') no-repeat center; background-size: 50px;"';
		}

		var hrefComplet = '<a href=' + href + ' target="_blank">';
		divToPopulate += '<div class="tweet-panel-item bord-bot-dc sm-tweet-item clearfix">';
		var profName = "";
		if (post.companyName != undefined && post.companyName != "") {
			profName = post.companyName;
		}
		if (post.regionName != undefined && post.regionName != "") {
			profName = post.regionName;
		}
		if (post.branchName != undefined && post.branchName != "") {
			profName = post.branchName;
		}
		if (post.agentName != undefined && post.agentName != "") {
			profName = post.agentName;
		}
		divToPopulate += '<div class="float-left ' + profImgClass + '" ' + profImgStyle + ' ></div>';
		divToPopulate += '<div class="sm-prof-name">' + profName + '</div>' + '<div class="sm-post-row float-left">' + hrefComplet + '<div class="tweet-icn-sm ' + iconClass + ' float-left"></div>' + "</a>" + '<div class="tweet-txt float-left">' + '<div class="tweet-text-main">' + linkify(post.postText) + '</div>' + '<div class="tweet-text-time"><em>' + convertUserDateToWeekFormt(new Date(post.timeInMillis)) + '</em></div>';
		divToPopulate += '</div>';

		divToPopulate += '</div>';
		divToPopulate += '</div>';

	});

	hideLoaderOnPagination($('#prof-posts'));

	if ($('#prof-posts').children('.tweet-panel-item').length == 0) {
		$('#prof-posts').html(divToPopulate);
		$('#prof-posts').perfectScrollbar({
			suppressScrollX : true
		});
		$('#prof-posts').perfectScrollbar('update');
	} else {
		$('#prof-posts').append(divToPopulate);
		$('#prof-posts').perfectScrollbar('update');
	}
}

function setColDetails(currentProfileName, currentProfileValue, parentCompanyId) {
	colName = currentProfileName;
	colValue = currentProfileValue;
	companyIdForSocialMonitor = parentCompanyId;
}

// complaint registration event binding
$(document).on('click', '#comp-reg-form-submit', function(e) {
	
	var emailIdStr = document.getElementById("comp-mailId").value;
	if(emailIdStr == ""){
		$('#overlay-main').show();
		e.stopPropagation();
		$('#other-account').val('true');
		createPopupConfirm("Unsubscribe Complaint Resolution Emails", "Are you sure you want to unsubscribe compalint resolution emails");
		overlayUnsetCompRes();
	}else if (validateComplaintRegistraionForm()) {
		var formData = $('#comp-reg-form').serialize();
		callAjaxPostWithPayloadData("/updatecomplaintressettings.do", function(data) {
			$('#overlay-toast').html(data);
			showToast();
		}, formData, true, '#comp-reg-form-submit');
	}
});

function overlayUnsetCompRes() {

	$('#overlay-continue').click(function() {
		callAjaxPOST('./unsetcomplaintresolution.do', function() {
			$('#overlay-continue').unbind('click');
			overlayRevert();
		}, true)});
	$('#overlay-cancel').click(function() {
		overlayRevert();
	});
}


// abusive alert
$(document).on('click', '#abusive-email-form-submit', function(e) {
	var emailIdStr = document.getElementById("abusive-mailId").value;
	if(emailIdStr == ""){
		$('#overlay-main').show();
		e.stopPropagation();
		$('#other-account').val('true');
		createPopupConfirm("Unsubscribe Alert Emails", "Are you sure you want to unsubscribe abusive emails");
		overlayUnsetAbusive();
	}else if(validateAbusiveEmailForm()) {
		var formData = $('#abusive-reg-form').serialize();
		callAjaxPostWithPayloadData("/updateabusivesurveysettings.do", function(data) {
			$('#overlay-toast').html(data);
			showToast();
		}, formData, true, '#abusive-email-form-submit');
	}
});


function overlayUnsetAbusive() {

	$('#overlay-continue').click(function() {
		callAjaxPOST('./unsetabusivesurveysettings.do', function() {
			$('#overlay-continue').unbind('click');
			overlayRevert();
		}, true)});
	$('#overlay-cancel').click(function() {
		overlayRevert();
	});
}

$(document).on('click touchstart', '#compl-checkbox', function() {
	if ($(this).hasClass('bd-check-img-checked')) {
		if (validateMultipleEmailIds('comp-mailId')) {
			$(this).removeClass('bd-check-img-checked');
			$('input[name="enabled"]').prop("checked", true);
			$('input[name="enabled"]').val("enable");
		}
	} else {
		$(this).addClass('bd-check-img-checked');
		$('input[name="enabled"]').prop("checked", false);
		$('input[name="enabled"]').val("");
	}
});


// function to remove social post
function removeUserPost(surveyMongoId) {

	$('#overlay-continue').removeAttr("onclick");
	$('#overlay-main').hide();
	var payload = {
		"statusmongoid" : surveyMongoId
	};

	callAjaxPostWithPayloadData("./deletestatus.do", function(data) {
		if (data.errCode == undefined) {
			$('#overlay-toast').html(data.responseText);
			showToast();
			proPostStartIndex = 0;
			fetchPublicPostEditProfile(false);
		} else {
			$('#overlay-toast').html(data.responseText);
			showToast();
		}
	}, payload, true);
}

// Edit profile events
$(document).on('click', '#prof-post-btn', function() {
	var textContent = $('#status-body-text-edit').val().trim();
	if (textContent == undefined || textContent == "") {
		$('#overlay-toast').html("Please enter valid data to post");
		showToast();
		return;
	}

	$('#status-body-text-edit').val('');
	var payload = {
		"text" : textContent
	};

	callAjaxPostWithPayloadData("./savestatus.do", function(data) {
		if (data.errCode == undefined) {
			proPostStartIndex = 0;
			fetchPublicPostEditProfile(false);
		}
	}, payload, true, '#prof-post-btn');
});

/*
 * $(document).on('click', '.ppl-share-wrapper .icn-remove', function() { $(this).hide(); $(this).parent().find('.ppl-share-social').hide(); $(this).parent().find('.icn-plus-open').show(); });
 */

$(document).on('click touchstart', '.icn-person', function() {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#contact-wrapper').show();
	$('#prof-agent-container').hide();
	$('#intro-about-me').hide();
	$('#reviews-container').hide();
	$('#ppl-post-cont').hide();
});

$(document).on('click touchstart', '.icn-ppl', function() {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#ppl-post-cont').show();
	$('#contact-wrapper').hide();
	$('#prof-agent-container').hide();
	$('#intro-about-me').hide();
	$('#reviews-container').hide();
});

$(document).on('click touchstart', '.icn-star-smile', function() {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#reviews-container').show();
	$('#contact-wrapper').hide();
	$('#prof-agent-container').hide();
	$('#intro-about-me').hide();
	$('#ppl-post-cont').hide();
});

$(document).on('click touchstart', '.inc-more', function() {
	$('.mob-icn').removeClass('mob-icn-active');
	$(this).addClass('mob-icn-active');
	$('#prof-agent-container').show();
	$('#intro-about-me').hide();
	$('#contact-wrapper').hide();
	$('#reviews-container').hide();
	$('#ppl-post-cont').hide();
});

$(document).on('mouseover', '#prof-basic-container', function(e) {
	$('#prof-basic-container .prof-edit-field-icn').show();
	$('#prof-basic-container .prof-edditable').addClass('prof-name-edit');
});
$(document).on('mouseleave', '#prof-basic-container', function(e) {
	if (!$('#prof-basic-container input').is(':focus')) {
		$('#prof-basic-container .prof-edit-field-icn').hide();
		$('#prof-basic-container .prof-edditable').removeClass('prof-name-edit');
	}
});

$(document).on('mouseover', '#prof-posts .tweet-panel-item', function(e) {
	$(this).find('.dlt-survey-wrapper').removeClass('hide');
});

$(document).on('mouseleave', '#prof-posts .tweet-panel-item', function(e) {
	$(this).find('.dlt-survey-wrapper').addClass('hide');
});

$(document).on('click', '#prof-posts .post-dlt-icon', function(e) {
	e.stopPropagation();
	var surveyMongoId = $(this).attr('surveymongoid');
	$('#overlay-main').show();
	$('#overlay-continue').show();
	$('#overlay-continue').html("Delete");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Delete Post");
	$('#overlay-text').html("Are you sure you want to delete the post ?");
	$('#overlay-continue').attr("onclick", "removeUserPost('" + surveyMongoId + "');");

});

/*
 * $(document).on('click', '.ppl-share-wrapper .icn-plus-open', function() { $(this).hide(); $(this).parent().find('.ppl-share-social,.icn-remove').show(); });
 */
function getRelevantEntities() {
	// Remove pre-existing options
	$('#select-entity-id').val("");
	$("#selected-entity-id-hidden").val("");
	$("#entity-selection-panel").show();
	// Get the entity type
	var entityType = $("#select-hierarchy-level").val();
	// If branch
	if (entityType == "branchId") {
		callAjaxGET("/fetchbranches.do", function(data) {
			var branchList = [];
			if (data != undefined && data != "")
				branchList = $.parseJSON(data);
			var searchData = [];
			for (var i = 0, j = 0; i < branchList.length; i++) {
				if (branchList[i].isDefaultBySystem == 0) {
					searchData[j] = {};
					searchData[j].label = branchList[i].branchName;
					searchData[j].branchId = branchList[i].branchId;
					j++;
				}
			}
			$("#select-entity-id").autocomplete({
				source : searchData,
				minLength : 0,
				delay : 0,
				autoFocus : true,
				select : function(event, ui) {
					$("#select-entity-id").val(ui.item.label);
					$('#selected-entity-id-hidden').val(ui.item.branchId);
					postsSearch();
					return false;
				},
				close : function(event, ui) {
				},
				create : function(event, ui) {
					$('.ui-helper-hidden-accessible').remove();
				}
			}).autocomplete("instance")._renderItem = function(ul, item) {
				$(ul).addClass("social-monitor-autocomplete");
				return $('<li>').append(item.label).appendTo(ul);
			};
			$("#select-entity-id").off('focus');
			$("#select-entity-id").focus(function() {
				$(this).autocomplete('search');
			});

		}, true);
	} else if (entityType == "regionId") {
		callAjaxGET("/fetchregions.do", function(data) {
			var regionList = [];
			if (data != undefined && data != "")
				regionList = $.parseJSON(data);
			autocompleteData = data;
			var searchData = [];
			for (var i = 0, j = 0; i < regionList.length; i++) {
				if (regionList[i].isDefaultBySystem == 0) {
					searchData[j] = {};
					searchData[j].label = regionList[i].regionName;
					searchData[j].regionId = regionList[i].regionId;
					j++;
				}
			}
			$("#select-entity-id").autocomplete({
				source : searchData,
				minLength : 0,
				delay : 0,
				autoFocus : true,
				select : function(event, ui) {
					$("#select-entity-id").val(ui.item.label);
					$('#selected-entity-id-hidden').val(ui.item.regionId);
					postsSearch();
					return false;
				},
				close : function(event, ui) {
				},
				create : function(event, ui) {
					$('.ui-helper-hidden-accessible').remove();
				}
			}).autocomplete("instance")._renderItem = function(ul, item) {
				$(ul).addClass("social-monitor-autocomplete");
				return $("<li>").append(item.label).appendTo(ul);
			};
			$("#select-entity-id").off('focus');
			$("#select-entity-id").focus(function() {
				$(this).autocomplete('search');
			});

		}, true);
	} else if (entityType == "userId") {
		callAjaxGET("/fetchusers.do", function(data) {
			var userList = [];
			if (data != undefined && data != "")
				userList = $.parseJSON(data);
			autocompleteData = data;
			var searchData = [];
			for (var i = 0, j = 0; i < userList.length; i++) {
				if (userList[i].isOwner == 0) {
					searchData[j] = {};
					searchData[j].label = userList[i].firstName;
					if (userList[i].lastName != undefined)
						searchData[j].label += " " + userList[i].lastName;
					searchData[j].userId = userList[i].userId;
					j++;
				}
			}
			$("#select-entity-id").autocomplete({
				source : searchData,
				minLength : 0,
				delay : 0,
				autoFocus : true,
				select : function(event, ui) {
					$("#select-entity-id").val(ui.item.label);
					$('#selected-entity-id-hidden').val(ui.item.userId);
					postsSearch();
					return false;
				},
				close : function(event, ui) {
				},
				create : function(event, ui) {
					$('.ui-helper-hidden-accessible').remove();
				}
			}).autocomplete("instance")._renderItem = function(ul, item) {
				$(ul).addClass("social-monitor-autocomplete");
				return $("<li>").append(item.label).appendTo(ul);
			};
			$("#select-entity-id").off('focus');
			$("#select-entity-id").focus(function() {
				$(this).autocomplete('search');
			});

		}, true);
	} else if (entityType == "companyId") {
		$("#entity-selection-panel").hide();
		postsSearch();
	}
}

$(document).on("keyup", "#post-search-query", function(e) {
	if (e.which == 13) {
		postsSearch();
	}
});

// send survey popup admin events
function attachAutocompleteAgentSurveyInviteDropdown() {
	$('.wc-review-agentname[data-name="agent-name"]').autocomplete({
		source : function(request, response) {
			if ((request.term).trim().length == 0) {
				return;
			}
			callAjaxGetWithPayloadData("/fetchagentsforadmin.do", function(data) {
				var responseData = JSON.parse(data);
				response($.map(responseData, function(item) {
					return {
						label : item.displayName + " <" + item.emailId + ">",
						value : item.displayName + " <" + item.emailId + ">",
						userId : item.userId,
						emailId : item.emailId
					};
				}));
			}, {
				"searchKey" : (request.term).trim(),
				"columnName" : colName,
				"columnValue" : colValue
			}, true);
		},
		minLength : 1,
		select : function(event, ui) {
			event.stopPropagation();
			var element = event.target;
			$(element).attr('agent-id', ui.item.userId);
			$(element).attr('column-name', colName);
			$(element).attr('email-id', ui.item.emailId);
			$(element).attr('val', ui.item.value);
		},
		close : function(event, ui) {
		},
		create : function(event, ui) {
			$('.ui-helper-hidden-accessible').remove();
		},
		open : function() {
			$('.ui-autocomplete').addClass('ui-hdr-agent-dropdown').perfectScrollbar({
				suppressScrollX : true
			});
			$('.ui-autocomplete').perfectScrollbar('update');
		}
	});

	$('.wc-review-agentname[data-name="agent-name"]').keyup(function(e) {
		var oldVal = $(this).attr('val');
		var cuurentVal = $(this).val();
		$(this).attr('agent-name', cuurentVal);
		if (oldVal == cuurentVal) {
			return;
		}
		$(this).attr('agent-id', "");
		$(this).attr('column-name', "");
		$(this).attr('email-id', "");
	});
}

function attachAutocompleteAliasDropdown() {
	var companyId = $('#cur-company-id').val();
	$('#match-user-email').autocomplete({
		source : function(request, response) {
			callAjaxGetWithPayloadData("/fetchagentsforadmin.do", function(data) {
				var responseData = JSON.parse(data);
				response($.map(responseData, function(item) {
					return {
						label : item.displayName + " <" + item.emailId + ">",
						value : item.displayName + " <" + item.emailId + ">",
						userId : item.userId,
						emailId : item.emailId
					};
				}));
			}, {
				"searchKey" : request.term,
				"columnName" : "companyId",
				"columnValue" : companyId
			}, true);
		},
		minLength : 1,
		select : function(event, ui) {
			event.stopPropagation();
			var element = event.target;
			$(element).attr('agent-id', ui.item.userId);
			$(element).attr('column-name', colName);
			$(element).attr('email-id', ui.item.emailId);
			$(element).attr('val', ui.item.value);
		},
		close : function(event, ui) {
		},
		create : function(event, ui) {
			$('.ui-helper-hidden-accessible').remove();
		},
		open : function() {
			$('.ui-autocomplete').addClass('ui-hdr-agent-dropdown').perfectScrollbar({
				suppressScrollX : true
			});
			$('.ui-autocomplete').perfectScrollbar('update');
		}
	});

	$('#match-user-email').keyup(function(e) {
		var oldVal = $(this).attr('val');
		var cuurentVal = $(this).val();
		if (oldVal == cuurentVal) {
			return;
		}
		$(this).attr('agent-id', "");
		$(this).attr('column-name', "");
		$(this).attr('email-id', "");
	});
}

// send survey popup admin events
function attachAutocompleteUserListDropdown() {
	$('#selected-user-txt').autocomplete({
		source : function(request, response) {
			var start = -1;
			var rows = -1;
			var url = "./finduserbyemail.do?startIndex=" + start + "&batchSize=" + rows + "&searchKey=" + request.term;
			callAjaxGET(encodeURI(url), function(data) {
				var responseData = JSON.parse(data);
				response($.map(responseData, function(item) {
					var displayName = item.firstName;
					if (item.lastName != undefined) {
						displayName = displayName + " " + item.lastName;
					}
					return {
						label : displayName,
						value : displayName,
						userId : item.userId
					};
				}));
			}, true);
		},
		minLength : 0,
		select : function(event, ui) {
			event.stopPropagation();
			$('#selected-user-txt').val(ui.item.value);
			$('#selected-user-txt').attr('val', ui.item.value);
			$('#selected-userid-hidden').val(ui.item.userId);
		},
		close : function(event, ui) {
		},
		create : function(event, ui) {
		},
		open : function() {
			$('.ui-autocomplete').addClass('ui-hdr-agent-dropdown').perfectScrollbar({
				suppressScrollX : true
			});
			$('.ui-autocomplete').perfectScrollbar('update');
		}
	});

	$('#selected-user-txt').keyup(function(e) {
		var oldVal = $(this).attr('val');
		var cuurentVal = $(this).val();
		if (oldVal == cuurentVal) {
			return;
		}
		$('#selected-userid-hidden').val("");
	});

}

// url change popup
function saveProfileUrl() {
	if (!validateprofileUrlEditForm()) {
		return false;
	}

}

function validateprofileUrlEditForm() {
	var profileUrl = $('input[name="profileUrlBlock"]').val();
	if (profileUrl == undefined || profileUrl == "") {
		$('#overlay-toast').text("Please enter a valid profile name");
		showToast();
		return false;
	}
	
	$.ajax({
		url : "./updateprofileurl.do?searchKey=" + profileUrl,
		type : "GET",
		cache : false,
		dataType : "html",
		async : true,
		success : function(data) {
			var profileExists = data;
			if (profileExists == "true") {
				$('#overlay-toast').text("The entered profile name already exists");
				showToast();
				return false;
			} else {
				$('#overlay-toast').text("Url updated successfully");
				showToast();
				hideActiveUserLogoutOverlay();
				console.log(data);
				$("#prof-header-url").html(data);
				return true;
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			// redirectErrorpage();
		}
	});
}

function initializeVerticalsMasterForProfilePage() {
	if (verticalsMasterList == undefined) {
		callAjaxGETWithTextData("/fetchverticalsmaster.do", function(data) {
			var parsedData = JSON.parse(data);
			if (parsedData.errCode == undefined) {
				verticalsMasterList = parsedData;
				initializeVerticalAutcomplete();
			}
		}, true, {});
	} else {
		initializeVerticalAutcomplete();
	}
}

function attachEventsOnSocialMonitor() {
	$("#select-hierarchy-level").off('change');
	$("#select-hierarchy-level").on('change', function() {
		autocompleteData = [];
		getRelevantEntities();
	});
	$('#prof-posts').off('scroll');
	$('#prof-posts').on('scroll', function() {
		var scrollContainer = this;
		if ((scrollContainer.scrollTop >= ((scrollContainer.scrollHeight) - (scrollContainer.clientHeight / 0.75))) && !isSocialMonitorPostLoaderRunning) {

			if (!doStopSocialMonitorPostAjaxRequest || socialMonitorPostBatch.length > 0) {
				fetchSearchedPostsSolr(false);
			}
		}
	});
}

function disconnectZillow(profileType, zillowProfileName, nmls, keepOrDeleteReview) {
	callAjaxFormSubmit("/disconnectZillow.do?keepOrDeleteReview="+keepOrDeleteReview, function(data) {
		if (data && data == "success") {
			loadSocialMediaUrlInSettingsPage();
			loadSocialMediaUrlInPopup();
			overlayRevert();
			$('#icn-zillow').addClass('icn-social-add');//disable
			$('#overlay-toast').text("Zillow disconnected successful");
			showToast();
		} else if(data && data == 'no-zillow') {
			$('#overlay-toast').text("No Zillow account to disconnect");
			showToast();
		} else {
			$('#overlay-toast').text("Some problem occurred while disconnecting zillow");
			showToast();
		}
	}, "zillowForm");
}
		
// Zillow connect functions
function saveZillowProfile(profileType, zillowProfileName, nmls) {
	if (!validateZillowForm(profileType)) {
		return false;
	}
	callAjaxFormSubmit("/zillowSaveInfo.do?zillowScreenName="+zillowProfileName+"&nmls="+nmls, function(data) {
		if (data && data == "success") {
			overlayRevert();
			showProfileLinkInEditProfilePage("zillow", $('input[name="zillowProfileName"]').val());
			loadSocialMediaUrlInSettingsPage();
			loadSocialMediaUrlInPopup();
			$('#overlay-toast').text("Zillow update successful");
			showToast();
		} else if(data && data == "zillow-error"){
			$('#overlay-toast').text("Invalid Zillow profile");
			showToast();
		} else {
			$('#overlay-toast').text("Some problem occurred while saving zillow");
			showToast();
		}
	}, "zillowForm");
}

//SS-1225
//Zillow connect  by screen name for Mortgage functions
function saveZillowProfileForMortgage(profileType, zillowProfileName, nmls) {
	if (!validateZillowForm(profileType)) {
		return false;
	}
	callAjaxFormSubmit("/zillowSaveInfoByScreenNameForMortgage.do?zillowScreenName="+zillowProfileName, function(data) {
		if (data && data == "success") {
			overlayRevert();
			showProfileLinkInEditProfilePage("zillow", $('input[name="zillowProfileName"]').val());
			loadSocialMediaUrlInSettingsPage();
			loadSocialMediaUrlInPopup();
			$('#overlay-toast').text("Zillow update successful");
			showToast();
		} else if(data && data == "zillow-error"){
			$('#overlay-toast').text("Invalid Zillow profile");
			showToast();
		} else {
			$('#overlay-toast').text("Some problem occurred while saving zillow");
			showToast();
		}
	}, "zillowForm");
}

// Zillow connect functions
function saveZillowEmailAddress(profileType) {
	if (!validateZillowForm(profileType)) {
		return false;
	}

	callAjaxFormSubmit("/zillowSaveInfo.do", function(data) {
		if (data && data == "success") {
			showProfileLinkInEditProfilePage("zillow", $('input[name="zillowProfileName"]').val());
			loadSocialMediaUrlInSettingsPage();
			loadSocialMediaUrlInPopup();
			$('#overlay-toast').text("Zillow update successful");
			showToast();
		} else if(data && data == "zillow-error"){
			$('#overlay-toast').text("Invalid Zillow profile");
			showToast();
		} else if(data && data == "zillow-nmls-required-error"){
			$('#overlay-toast').text("NMLS is required");
			showToast();
		} else {
			$('#overlay-toast').text("Some problem occurred while saving zillow");
			showToast();
		}
	}, "zillowForm");
}

//Open show Screen name for Zillow
function openNextScreenForZillowScreenName(profileType, button, nmls) {
	disableBodyScroll();
	
	if (button != 'by-screen-name' && !validateZillowForm(profileType)) {
		return false;
	}
	
	if (button != 'by-screen-name') {
		callAjaxPOST("/zillowValidateNMLS.do?nmls="+nmls, function(data) {
			if(data == 'invalid-nmls') {
				$('#overlay-toast').text("No corresponding Zillow profile found, please connect using screen name");
				showToastLong();
			} else if(data == 'no-screen-name')  {
				//show section to insert screen name
				$('#main-container').hide();
				$('#screen-name-found-container').hide();
				$('#by-screen-name-container').hide();
				$('#disconnect-zillow-container').hide();
				$('#zillow-help-container').hide();
				$('#no-screen-name-container').show();
				
				if($('input[name="zillowProfileNameNoScreenForNMLS"]').val().length > 0) {
			    	showHideDisconnectZillowLink(true);
			    } else {
			    	showHideDisconnectZillowLink(false);
			    }
				
				$('#overlay-next-noscreen').click(function() {
					var zillowScreenName = $('input[name="zillowProfileNameNoScreenForNMLS"]').val();
					if(zillowScreenName == undefined || zillowScreenName == "")
						zillowScreenName = $('input[name="zillowProfileNameForNoNMLS"]').val();
					var zillowProfileNameURI = ""; 
					if(profileType == 'Mortgage')
						zillowProfileNameURI = $('#zillowLenderPath').val();
					else
						zillowProfileNameURI = $('#zillowNonLenderURI').val();
					$('#zillow-profile-lender-new-link').attr("href", zillowProfileNameURI + zillowScreenName);
					$('#zillow-profile-lender-new-link').html(zillowProfileNameURI + zillowScreenName);
					
					$('#no-screen-name-container').hide();
					$('#no-screen-name-confirm-container').show();
					
					if($('input[name="zillowProfileName"]').val().trim().length > 0) {
				    	showHideDisconnectZillowLink(true);
				    } else {
				    	showHideDisconnectZillowLink(false);
				    }
				});
				
				$('#overlay-save-noscreen').click(function() {
					var nmls = $('input[name="nmlsId"]').val();					
					var zillowProfileName = $('input[name="zillowProfileNameNoScreenForNMLS"]').val();
					if(zillowProfileName == undefined || zillowProfileName == "")
						zillowProfileName = $('input[name="zillowProfileNameForNoNMLS"]').val();
					if(validInput(zillowProfileName)) {
						saveZillowProfile(profileType, zillowProfileName, nmls);
					}
				});
				
				$('#overlay-disconnect-noscreen').click(function() {
					$('#no-screen-name-container').hide();
					$('#disconnect-zillow-container').show();
					var nmls = $('input[name="nmlsId"]').val();					
					var zillowProfileName = $('input[name="zillowProfileNameNoScreenForNMLS"]').val();
					
					$('#overlay-cancel-disconnect-zillow').click(function() {
						overlayRevert();
					});
					
					$('#overlay-keepreview-disconnect-zillow').click(function() {
						disconnectZillow(profileType, zillowProfileName, nmls, "keep-review");
					});
					
					$('#overlay-deletereview-disconnect-zillow').click(function() {
						disconnectZillow(profileType, zillowProfileName, nmls, "delete-review");
					});
				});
				
				$('#overlay-cancel-noscreen').click(function() {
					overlayRevert();
				});
			} else {//if screen name is found by nmls
				data = $.parseJSON(data);
				var socialMediaTokens = data.socialMediaTokens;
				var zillowScreenName = data.socialMediaTokens.zillowToken.zillowScreenName;	
				//SS-1224 - zillowScreenName update
				$('#screenNameTempHidden').val(zillowScreenName);
				
				showZillowPageWithProfileLink(zillowScreenName);
								
			}
		}, "zillowForm");
	}
	
	if (button == 'by-screen-name') {
		$('#main-container').hide();
		$('#screen-name-found-container').hide();
		$('#no-screen-name-container').hide();
		$('#disconnect-zillow-container').hide();
		$('#zillow-help-container').hide();
		$('#by-screen-name-container').show();
		
		$('#overlay-save-zillow-byscreen-name').click(function() {			
			var zillowScreenName = $('input[name="zillowProfileNameNoScreenForNMLS"]').val();
			if(zillowScreenName == undefined || zillowScreenName == "" || zillowScreenName.trim().length == 0)
				zillowScreenName = $('input[name="zillowProfileNameForNoNMLS"]').val();
			
			if(validInput(zillowScreenName)) {
				var zillowProfileNameURI = ""; 
				if(profileType == 'Mortgage')
					zillowProfileNameURI = $('#zillowLenderPath').val();
				else
					zillowProfileNameURI = $('#zillowNonLenderURI').val();
				$('#zillow-profile-lender-new-link').attr("href", zillowProfileNameURI + zillowScreenName);
				$('#zillow-profile-lender-new-link').html(zillowProfileNameURI + zillowScreenName);
				
				$('#by-screen-name-container').hide();
				$('#no-screen-name-confirm-container').show();
				
				$('#overlay-save-noscreen').click(function() {
					var nmls = $('input[name="nmlsId"]').val();					
					var zillowProfileName = $('input[name="zillowProfileNameNoScreenForNMLS"]').val();
					if(zillowProfileName == undefined || zillowProfileName == "" || zillowProfileName.trim().length == 0)
						zillowProfileName = $('input[name="zillowProfileNameForNoNMLS"]').val();
					if(validInput(zillowProfileName)) {
						saveZillowProfileForMortgage(profileType, zillowProfileName, nmls);
					}
				});
				
				$('#overlay-disconnect-noscreen').click(function() {
					$('#no-screen-name-container').hide();
					$('#disconnect-zillow-container').show();
					var nmls = $('input[name="nmlsId"]').val();					
					var zillowProfileName = $('input[name="zillowProfileNameForNoNMLS"]').val();
					
					$('#overlay-cancel-disconnect-zillow').click(function() {
						overlayRevert();
					});
					
					$('#overlay-keepreview-disconnect-zillow').click(function() {
						disconnectZillow(profileType, zillowProfileName, nmls, "keep-review");
					});
					
					$('#overlay-deletereview-disconnect-zillow').click(function() {
						disconnectZillow(profileType, zillowProfileName, nmls, "delete-review");
					});
				});
				
				$('#overlay-cancel-noscreen').click(function() {
					overlayRevert();
				});
			}
		});
		
		$('#overlay-disconnect-zillow-byscreen-name').click(function() {
			$('#by-screen-name-container').hide();
			$('#disconnect-zillow-container').show();
			var nmls = $('input[name="nmlsId"]').val();					
			var zillowProfileName = $('input[name="zillowProfileNameForNoNMLS"]').val();
			
			$('#overlay-cancel-disconnect-zillow').click(function() {
				overlayRevert();
			});
			
			$('#overlay-keepreview-disconnect-zillow').click(function() {
				disconnectZillow(profileType, zillowProfileName, nmls, "keep-review");
			});
			
			$('#overlay-deletereview-disconnect-zillow').click(function() {
				disconnectZillow(profileType, zillowProfileName, nmls, "delete-review");
			});
		});
	}
	
}

//if nmls / screen name is present show the page
function showZillowPageWithProfileLink(zillowScreenName) {
	var profileType = $('#profileType').val();
	var zillowProfileNameURI = ""; 
	if(profileType == 'Mortgage')
		zillowProfileNameURI = $('#zillowLenderPath').val();
	else
		zillowProfileNameURI = $('#zillowNonLenderURI').val();
	$('#zillow-profile-lender-link').attr("href", zillowProfileNameURI + zillowScreenName);
	$('#zillow-profile-lender-link').html(zillowProfileNameURI + zillowScreenName);
	
	
	$('.zillowProfileName').val(zillowScreenName);
	$('.zillowProfileNameSpan').text(zillowScreenName);				
	
	$('#main-container').hide();
	$('#screen-name-found-container').show();
	$('#disconnect-zillow-container').hide();
	$('#zillow-help-container').hide();
	
	$('#overlay-disconnect-zillow').click(function() {
		$('#screen-name-found-container').hide();
		$('#disconnect-zillow-container').show();
		var nmls = $('input[name="nmlsId"]').val();					
		var zillowProfileName = $('input[name="zillowProfileNameNoScreenForNMLS"]').val();
		
		$('#overlay-cancel-disconnect-zillow').click(function() {
			overlayRevert();
		});
		
		$('#overlay-keepreview-disconnect-zillow').click(function() {
			disconnectZillow(profileType, zillowProfileName, nmls, "keep-review");
		});
		
		$('#overlay-deletereview-disconnect-zillow').click(function() {
			disconnectZillow(profileType, zillowProfileName, nmls, "delete-review");
		});
	});
	
	$('#overlay-save-zillow-byscreen').click(function() {
		var nmls = $('input[name="nmlsId"]').val();	
		var zillowScreenName = $("#screenNameTempHidden").val();	
		if(validInput(nmls)) {
			saveZillowProfile(profileType, zillowScreenName, nmls);
		}
	});
	
	$('#overlay-change-zillow').click(function() {
		 
		    
		$('#main-container').show();
		$('#screen-name-found-container').hide();
		
		$('#overlay-cancel-zillow').click(function() {
			overlayRevert();
		});
	});
}

function validateZillowForm(profileType) {
	if(profileType != 'Mortgage') {
		var zillowProfileName = $('input[name="zillowProfileName"]').val();
		if (zillowProfileName == undefined || zillowProfileName == "") {
			$('#overlay-toast').text("Please enter a valid profile name");
			showToast();
			return false;
		}
		return true;
	} else {
		return true;
	}
}

// Fucntion to update view as scroll in dashboard
function updateViewAsScroll() {
	if ($("#da-dd-wrapper-profiles").children('.da-dd-item').length <= 1) {
		$('#da-dd-wrapper').remove();
	} else {
		$('#da-dd-wrapper').show();
		$('.va-dd-wrapper').perfectScrollbar({
			suppressScrollX : true
		});
		$('.va-dd-wrapper').perfectScrollbar('update');
	}
}

// Sign up path functions

// Address infromataion validation
function validateIndividaulAddressForm() {

	if (!validateAddress1('com-address1', true)) {
		$('#com-address1').focus();
		return false;
	}
	if (!validateAddress2('com-address2')) {
		$('#com-address2').focus();
		return false;
	}
	if (!validateCountry('com-country')) {
		$('#com-country').focus();
		return false;
	}
	if (!validateCountryZipcode('com-zipcode', true)) {
		$('#com-zipcode').focus();
		return false;
	}
	if (!validatePhoneNumber('com-contactno', true)) {
		$('#com-contactno').focus();
		return false;
	}
	return true;
}

// Summary form validation
function validateSummaryForm() {
	if (!validateInputField('wc-industry')) {
		$('#overlay-toast').html('Please enter industry');
		showToast();
		$('#wc-industry').focus();
		return false;
	}
	if (!validateInputField('wc-location')) {
		$('#overlay-toast').html('Please enter location');
		showToast();
		$('#wc-location').focus();
		return false;
	}
	if (!validateTextArea('wc-summary')) {
		$('#overlay-toast').html('Please add or edit summary');
		showToast();
		$('#wc-summary').focus();
		return false;
	}
	return true;
}

function bindIndividualSignupPathEvents() {

	// Profile image upload
	$('#prof-image-upload-btn').on('click', function() {
		$('#prof-image').trigger('click');
	});

	$('#wc-address-submit').on('click', function() {
		if (validateIndividaulAddressForm()) {
			var payload = {
				"address1" : $('#com-address1').val(),
				"address2" : $('#com-address2').val(),
				"country" : $('#com-country').val(),
				"countrycode" : $('#country-code').val(),
				"zipcode" : $('#com-zipcode').val(),
				"contactno" : $('#com-contactno').val(),
				"state" : $('select[name="state"]').val(),
				"city" : $('input[name="city"]').val()
			};
			callAjaxPostWithPayloadData("./editcompanyinformation.do", function(data) {
				$('#message-header').html(data);
				$('#overlay-toast').html($('#display-msg-div').text().trim());
				showToast();
			}, payload, false);
		}
	});

	$('#wc-summary-submit').on('click', function() {
		if (validateSummaryForm()) {
			var payload = {
				"industry" : $('#wc-industry').val(),
				"location" : $('#wc-location').val(),
				"aboutme" : $('#wc-summary').val()
			};
			callAjaxPostWithPayloadData("./updatesummarydata.do", function(data) {
				$('#message-header').html(data);
				$('#overlay-toast').html($('#display-msg-div').text().trim());
				showToast();
			}, payload, false);
		}
	});
}

/**
 * Functions for session info
 */
function showActiveUserLogoutOverlay() {
	$('#overlay-header').html("Active User Detected");
	$("#overlay-text").html("Please logout active user to proceed");
	$('#overlay-continue').addClass("hide");
	$('#overlay-cancel').html("Ok");

	$('#overlay-cancel').click(function() {
		hideActiveUserLogoutOverlay();
		landingFlow();
	});

	$('#overlay-main').show();
}

function hideActiveUserLogoutOverlay() {
	$('#overlay-main').hide();
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').removeClass("hide");
	$('#overlay-cancel').html('');

	$('#overlay-cancel').unbind('click');
}

/**
 * Functions to confirm social authentication
 */
function confirmSocialAuth(socialNetwork, callBackFunction, link) {

	var message = "";

	if (link && link.trim() != "") {
		message = "Are you sure you want to disconnect your previous connection to " + socialNetwork + " and connect again";
	} else {
		message = "Are you sure you want to connect to " + socialNetwork;
	}

	$('#overlay-header').html("Confirm user Authentication");
	$("#overlay-text").html(message);
	$('#overlay-continue').html("Ok");
	$('#overlay-continue').attr("onclick", "");

	$('#overlay-continue').click(function() {
		if (callBackFunction != undefined && typeof (callBackFunction) == "function") {
			$('#overlay-main').hide();
			$('#overlay-continue').unbind('click');
			callBackFunction();
		}
	});

	$('#overlay-cancel').html("Cancel");
	$('#overlay-main').show();
};

function confirmSocialAuthOk(callBackFunction) {
	if (callBackFunction != undefined && typeof (callBackFunction) == "function") {
		$('#overlay-main').hide();
		callBackFunction();
	}
}

/*
 * callAjaxGET("./sendsurveyinvitation.do", function(data) { $('#overlay-send-survey').html(data); if ($("#welcome-popup-invite").length) { $('#overlay-send-survey').removeClass("hide"); $('#overlay-send-survey').show(); } }, true);
 * 
 * $('#overlay-main').show(); $('#overlay-continue').show(); $('#overlay-continue').html("Submit"); $('#overlay-cancel').html("Cancel"); $('#overlay-header').html("Start DryRun"); $('#overlay-text').html("Are you sure you want to delete user ?"); $('#overlay-continue').attr("onclick", "");
 */

$(document).on('click', '#en-dry-save', function(e) {
	e.stopPropagation();
	if (validateEncompassInput('encompass-form-div')) {
		var state = $("#encompass-state").val();
		var warn = true;
		if (state != 'prod') {
			warn = false;
		}
		if (warn) {
			confirmEncompassEdit();
		} else {
			initiateEncompassSaveConnection(false);
		}
	}

});

$(document).on('click', '#ftp-save', function(e) {
	e.stopPropagation();
	if (validateFtpInput('ftp-form-div')) {
			initiateFtpSaveConnection(false);
	}

});

$(document).on('click', '#lone-get-classification', function(e) {
	e.stopPropagation();
	if (validateLoneWolfInput('lone-wolf-form-div')) {
		var state = $("#lone-state").val();
		var warn = true;
		if (state != 'prod') {
			warn = false;
		}

		initiateLoneWolfSaveConnection(false);

	}

});


$(document).on('click', '#lone-data-save', function(e) {
	e.stopPropagation();
	if (validateLoneWolfInput('lone-wolf-form-div')) {
		var state = $("#lone-state").val();
		var warn = true;
		if (state != 'prod') {
			warn = false;
		}
		if (warn) {
			confirmLoneEdit();
		} else {
			saveLoneWolfDetails("lone-wolf-form" , false);
		}
	}
});

function confirmEncompassEdit() {

	$('#overlay-header').html("Confirm Edit");
	$('#overlay-text').html("This action can affect the way we fetch your encompass records");
	$('#overlay-continue').html("Edit");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-continue').off();
	$('#overlay-continue').click(function() {
		initiateEncompassSaveConnection(true);
	});

	$('#overlay-main').show();
	disableBodyScroll();
}
function confirmLoneEdit() {

	$('#overlay-header').html("Confirm Edit");
	$('#overlay-text').html("This action can affect the way we fetch your Lone Wolf records");
	$('#overlay-continue').html("Edit");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-continue').off();
	$('#overlay-continue').click(function() {
		saveLoneWolfDetails("lone-wolf-form" , true);
	});

	$('#overlay-main').show();
	disableBodyScroll();
}

function initiateEncompassSaveConnection(warn) {
	var username = document.getElementById('encompass-username').value;
	var password = document.getElementById('encompass-password').value;
	var url = document.getElementById('encompass-url').value;
	var version = $( "#sdk-version-selection-list option:selected" ).text();
	var payload = {
		"username" : username,
		"password" : password,
		"url" : url,
		"version"  : version
	};
	//TODO uncomment the test connection after development 
	// by passed test credentials for develpment
	//showOverlay();
	//callAjaxGetWithPayloadData(getLocationOrigin() + "/rest/encompass/testcredentials.do", saveEncompassDetailsCallBack, payload, true, '#en-dry-save');
	saveEncompassDetails("encompass-form");
	if (warn) {
		$('#overlay-cancel').click();
	}
}

function initiateFtpSaveConnection(warn) {
	var username = document.getElementById('ftp-username').value;
	var password = document.getElementById('ftp-password').value;
	var url = document.getElementById('ftp-url').value;
	var directory = document.getElementById('ftp-dir').value;
	var payload = {
		"username" : username,
		"password" : password,
		"url" : url,
		"directory"  : directory
	};
	//TODO uncomment the test connection after development 
	// by passed test credentials for develpment
	//showOverlay();
	//callAjaxGetWithPayloadData(getLocationOrigin() + "/rest/encompass/testcredentials.do", saveEncompassDetailsCallBack, payload, true, '#en-dry-save');
	saveFtpDetails("ftp-form");
	if (warn) {
		$('#overlay-cancel').click();
	}
}

function initiateLoneWolfSaveConnection(warn) {
	var client = document.getElementById('lone-client').value;
	var payload = {
		"clientCode" : client
	};
	showOverlay();
	callAjaxGetWithPayloadData(getLocationOrigin() + "/getlonewolfclassifications.do", saveTestLoneDetailsCallBack, payload, true, '#lone-get-classification');
	if (warn) {
		$('#overlay-cancel').click();
	}
}

$(document).on('click', '#en-dry-enable', function() {

	callAjaxPOST("/enableencompassdetails.do", testEnableCompassCallBack, true, '#en-dry-enable');

});
$(document).on('click', '#lone-dry-enable', function() {

	callAjaxPOST("/enablelonewolfdetails.do", testEnableLoneCallBack, true, '#lone-dry-enable');

});
function testEnableCompassCallBack(response) {
	var map = response;
	if (map == "Successfully enabled encompass connection") {
		showInfo(map);
		$("#encompass-state").val('prod');
		showEncompassButtons();
	} else {
		showError(map);
	}

};
function testEnableLoneCallBack(response) {
	var map = response;
	if (map == "Successfully enabled lone wolf connection") {
		showInfo(map);
		$("#lone-state").val('prod');
		showLoneWolfButtons();
	} else {
		showError(map);
	}

};
// encompass button state
function showEncompassButtons() {
	var state = $("#encompass-state").val();
	if (state == 'dryrun') {
		$('#en-dry-enable').show();
		$('#en-generate-report').show();
		$('#en-disconnect').hide();
	} else if (state == 'prod') {
		$('#en-disconnect').show();
		$('#en-dry-enable').hide();
		$('#en-generate-report').hide();
	} else {
		$('#en-disconnect').hide();
		$('#en-dry-enable').hide();
		$('#en-generate-report').hide();
	}
}

//ftp button state
function showFtpButtons() {
	var state = $("#ftp-state").val();
	if (state == 'dryrun') {
		$('#en-dry-enable').show();
		$('#en-generate-report').show();
		$('#en-disconnect').hide();
	} else if (state == 'prod') {
		$('#en-disconnect').show();
		$('#en-dry-enable').hide();
		$('#en-generate-report').hide();
	} else {
		$('#en-disconnect').hide();
		$('#en-dry-enable').hide();
		$('#en-generate-report').hide();
	}
}

// lone wolf button state
function showLoneWolfButtons() {
	var state = $("#lone-state").val();
	if (state == 'dryrun') {
		$('#lone-dry-enable').show();
		// TODO : uncooment the generate report show code when back end is ready
	// $('#lone-generate-report').show();
		$('#lone-disconnect').hide();
	} else if (state == 'prod') {
		$('#lone-disconnect').show();
		$('#lone-dry-enable').hide();
		$('#lone-generate-report').hide();
	} else {
		$('#lone-disconnect').hide();
		$('#lone-dry-enable').hide();
		$('#lone-generate-report').hide();
	}
}
$(document).on('click', '#en-disconnect', function() {
	if (isRealTechOrSSAdmin) {
		callAjaxPOST("/disableencompassdetails.do", testDisconnectCompassCallBack, true, '#en-disconnect');
	} else {
		$('#overlay-toast').html('Please contact SuccessTeam@SocialSurvey.com or call 1-888-701-4512.');
		showToast();
	}
});

function testDisconnectCompassCallBack(response) {
	var map = response;
	if (map == "Successfully disabled encompass connection") {
		$("#encompass-state").val('dryrun');
		showEncompassButtons();
		showInfo(map);
	} else {
		showError(map);
	}

};
$(document).on('click', '#lone-disconnect', function() {
	if (isRealTechOrSSAdmin) {
		callAjaxPOST("/disablelonewolfdetails.do", testDisconnectLoneWolfCallBack, true, '#lone-disconnect');
	} else {
		$('#overlay-toast').html('Please contact SuccessTeam@SocialSurvey.com or call 1-888-701-4512.');
		showToast();
	}
});

$(document).on('click', '#lone-dry-cancel', function() {
	showLoneWolfButtons();
	$('#lone-dry-cancel').hide();
	$('#lone-data-save').hide();
	
	$('#classification-div').hide();
	$('#transaction-start-div').hide();
	
	$('#lone-get-classification').show();
	$("#lone-test-connection").show();
	
	

});

function testDisconnectLoneWolfCallBack(response) {
	var map = response;
	if (map == "Successfully disabled lone wolf connection") {
		$("#lone-state").val('dryrun');
		showLoneWolfButtons();
		showInfo(map);
	} else {
		showError(map);
	}

};

$(document).on('click', '#en-generate-report', function() {
	disableBodyScroll();
	callAjaxGET("./dryrun.do", function(data) {
		enableReportGeneration(data, "/enableencompassreportgeneration.do", testGenerateReportCallBack, '#en-generate-report');
	}, true);
});
$(document).on('click', '#lone-generate-report', function() {
	disableBodyScroll();
	callAjaxGET("./lonedryrun.do", function(data) {
		enableReportGeneration(data, "/enablelonewolfreportgeneration.do", testLoneGenerateReportCallBack, '#lone-generate-report');
	}, true);
});

function enableReportGeneration(data, url, successCallback, reportGenerateButtonId) {
	$('#overlay-text').html(data);
	$('#overlay-continue').show();
	$('#overlay-continue').html("Submit");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Send Report");
	$('#overlay-main').show();
	$('#overlay-continue').off();
	$('#overlay-continue').click(function() {
		var noOfdays = document.getElementById('no-of-days').value;
		var reportEmail = document.getElementById('report-email').value;
		var payload = {
			"noOfdays" : noOfdays,
			"reportEmail" : reportEmail
		};
		callAjaxPostWithPayloadData(url, successCallback, payload, true, reportGenerateButtonId);
	});
}
function testLoneGenerateReportCallBack(response) {
	$('#overlay-cancel').click();
	var map = response;
	if (map == "Successfully enabled lone wolf report generation ") {
		showInfo(map);
	} else {
		showError(map);
	}
}
function testGenerateReportCallBack(response) {
	$('#overlay-cancel').click();
	var map = response;
	if (map == "Successfully enabled encompass report generation ") {
		showInfo(map);
	} else {
		showError(map);
	}
};

function encompassCretentials() {
	var username = document.getElementById('encompass-username').value;
	var password = document.getElementById('encompass-password').value;
	var url = document.getElementById('encompass-url').value;
	var version = $( "#sdk-version-selection-list option:selected" ).text();
	var payload = {
		"username" : username,
		"password" : password,
		"url" : url,
		"version" : version
	};

	if (validateEncompassTestInput('encompass-form-div')) {
		showOverlay();
		callAjaxGetWithPayloadData(getLocationOrigin() + "/rest/encompass/testcredentials.do", testEncompassConnectionCallBack, payload, true, '#en-test-connection');
	}
	;

};
function loneWolfCretentials() {
	var client = document.getElementById('lone-client').value;
	var payload = {
		"clientCode" : client
	};

	if (validateLoneWolfInput('lone-wolf-form-div')) {
		showOverlay();
		callAjaxGetWithPayloadData(getLocationOrigin() + "/rest/lonewolf/testcredentials.do", testLoneConnectionCallBack, payload, true, '#lone-test-connection');
	}
	;

};

function paintReviews(result, isRequestFromDashBoard) {
	// Check if there are more reviews left
	var resultSize = result.length;

	if (isRequestFromDashBoard) {
		displayReviewOnDashboard();
	} else {
		displayReviewOnEditProfile();
	}

	$('.ppl-review-item-last').removeClass('ppl-review-item-last').addClass('ppl-review-item');

	var reviewsHtml = "";
	$.each(result, function(i, reviewItem) {
		var scoreFixVal = 1;
		var date = Date.parse(reviewItem.modifiedOn);
		var lastItemClass = "ppl-review-item";
		if (i == resultSize - 1) {
			lastItemClass = "ppl-review-item-last";
		}
		var custName = reviewItem.customerFirstName;
		if (reviewItem.customerLastName != undefined) {
			custName += ' ' + reviewItem.customerLastName;
		}
		custName = custName || "";
		var custNameArray = custName.split(' ');
		var custDispName = custNameArray[0];
		if (custNameArray[1] != undefined && custNameArray[1].trim() != "") {
			custDispName += ' ' + custNameArray[1].substr(0, 1).toUpperCase() + '.';
		}
		reviewsHtml = reviewsHtml + '<div class="' + lastItemClass + '" data-cust-first-name=' + encodeURIComponent(reviewItem.customerFirstName) + ' data-rating=' + reviewItem.score + ' data-review="' + encodeURIComponent(reviewItem.review)  + '" data-agentid="' + reviewItem.agentId + '" survey-mongo-id="' + reviewItem._id + '">';
		reviewsHtml += '	<div class="ppl-header-wrapper clearfix">';
		reviewsHtml += '		<div class="float-left ppl-header-left">';
		reviewsHtml += '			<div class="ppl-head-1">' + custDispName + '</div>';
		if (date != null) {
			date = convertUserDateToLocale(date);
			reviewsHtml += '		<div class="ppl-head-2">' + date.toString("MMMM d, yyyy") + '</div>';
		}

		reviewsHtml += '		</div>';
		if (isRequestFromDashBoard) {
			reviewsHtml += '<div class="st-rating-wrapper maring-0 clearfix review-ratings float-right" data-modified="false" data-rating="' + reviewItem.source + '" data-source="' + reviewItem.score + '">';
			if (reviewItem.source == "Zillow") {
				reviewsHtml += '<div class="rating-image float-left icn-zillow" title="Zillow"></div>';
				reviewsHtml += '<div class="rating-rounded float-left">' + Number.parseFloat(reviewItem.score).toFixed(1) + '</div>';
			}
			reviewsHtml += '</div>';
			reviewsHtml += '</div>';
		} else {
			reviewsHtml += '    	<div class="float-right ppl-header-right">';
			reviewsHtml += '    	    <div class="st-rating-wrapper maring-0 clearfix review-ratings" data-source="' + reviewItem.source + '" data-rating="' + reviewItem.score + '"></div>';
			reviewsHtml += '		</div>';
			reviewsHtml += '	</div>';
		}

		if (reviewItem.summary != null && reviewItem.summary.length > 0) {
			reviewsHtml += '<div class="ppl-content">' + reviewItem.summary + '</div>';
		}

		if (reviewItem.review.length > 250) {
			reviewsHtml += '<div class="ppl-content"><span class="review-complete-txt">' + reviewItem.review + '</span><span class="review-less-text">' + reviewItem.review.substr(0, 250) + '</span><span class="review-more-button">More</span>';
		} else {
			reviewsHtml += '<div class="ppl-content">' + reviewItem.review;
		}
		if (reviewItem.source == "Zillow") {
			reviewsHtml += '<br><a class="view-zillow-link" href="' + reviewItem.sourceId + '"  target="_blank">View on zillow</a>';
		}
		if (reviewItem.customerLastName != null && reviewItem.customerLastName != "")
			reviewItem.customerLastName = reviewItem.customerLastName.substring(0, 1).toUpperCase() + ".";
		else
			reviewItem.customerLastName = "";
		if (reviewItem.agentName == undefined || reviewItem.agentName == null)
			reviewItem.agentName = "us";

		reviewsHtml += '	</div>';

		reviewsHtml += '	<div class="ppl-share-wrapper clearfix share-plus-height">';
		reviewsHtml += '		<div class="float-left blue-text ppl-share-shr-txt">Share</div>';
		reviewsHtml += '		<div class="float-left icn-share icn-plus-open"></div>';
		reviewsHtml += '		<div class="float-left clearfix ppl-share-social hide">';
		reviewsHtml += '			<span id ="fb_' + i + '"class="float-left ppl-share-icns icn-fb icn-fb-pp" title="Facebook" data-link="https://www.facebook.com/dialog/share?' + reviewItem.faceBookShareUrl + '&href=' + reviewItem.completeProfileUrl.replace("localhost", "127.0.0.1") + '/' + reviewItem._id + '&quote=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '&redirect_uri=https://www.facebook.com"></span>';
		reviewsHtml += '            <input type="hidden" id="twttxt_' + i + '" class ="twitterText_loop" value ="' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '"/></input>';
		reviewsHtml += '			<span id ="twitt_' + i + '" class="float-left ppl-share-icns icn-twit icn-twit-pp" onclick="twitterFn(' + i + ');" title="Twitter" data-link="https://twitter.com/intent/tweet?text=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + ' &url=' + reviewItem.completeProfileUrl + '/' + reviewItem._id + '"></span>';
		reviewsHtml += '			<span class="float-left ppl-share-icns icn-lin icn-lin-pp" title="LinkedIn" data-link="https://www.linkedin.com/shareArticle?mini=true&url=' + reviewItem.completeProfileUrl + '/' + reviewItem._id + '&title=&summary=' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '&source="></span>';
		reviewsHtml += '			<span class="float-left" title="Google+"> <button class="g-interactivepost float-left ppl-share-icns icn-gplus" data-contenturl="' + reviewItem.completeProfileUrl + '/' + reviewItem._id + '" data-clientid="' + reviewItem.googleApi + '"data-cookiepolicy="single_host_origin" data-prefilltext="' + reviewItem.score.toFixed(scoreFixVal) + '-star response from ' + encodeURIComponent(custDispName) + ' for ' + encodeURIComponent(reviewItem.agentName) + ' at SocialSurvey - ' + encodeURIComponent(reviewItem.review) + '" data-calltoactionlabel="USE"' + '' + 'data-calltoactionurl=" ' + reviewItem.completeProfileUrl + '/' + reviewItem._id + '"> <span class="icon">&nbsp;</span> <span class="label">share</span> </button> </span>';
		reviewsHtml += '		</div>';
		reviewsHtml += '		<div class="float-right" style="margin: 0 -5px;">';
		if (reviewItem.source != "Zillow")
			reviewsHtml += '			<div class="report-abuse-txt report-txt prof-report-abuse-txt">Report Abuse</div>';
		reviewsHtml += '		</div>';
		reviewsHtml += '		<div class="float-left icn-share icn-remove icn-rem-size hide"></div>';
		reviewsHtml += '	</div>';
		reviewsHtml += '</div>';
	});

	if (result.length > 0) {
		$('#reviews-container').show();
	}

	if (isRequestFromDashBoard) {
		hideLoaderOnPagination($('#review-details'));
	} else {
		hideLoaderOnPagination($('#prof-review-item'));
	}
	/*
	 * if($("#profile-fetch-info").attr("fetch-all-reviews") == "true" && startIndex == 0) { $("#prof-review-item").html(''); }
	 */
	if (isRequestFromDashBoard) {
		$('#review-details').append(reviewsHtml);
	} else {
		$("#prof-review-item").append(reviewsHtml);

		$("#prof-reviews-header").parent().show();
		$(".review-ratings").each(function() {
			changeRatingPattern($(this).data("rating"), $(this), false, $(this).data("source"));
		});
	}
	setTimeout(function() {
		$(window).trigger('scroll');
	}, 100);
}
$(document).on('click', '.review-more-button', function() {
	$(this).parent().find('.review-less-text').hide();
	$(this).parent().find('.review-complete-txt').show();
	$(this).parent().find('.view-zillow-link').show();
	$(this).parent().find('.view-fb-link').show();
	$(this).parent().find('.view-goo-link').show();
	$(this).hide();
});
$(document).on('click','ul.accordion li',function(){
	if($(this).find(".email-category").hasClass('expanded')){
		$(this).find(".email-category").removeClass('expanded');
		$(this).find(".email-content").css('display','none');
		return false;
	}else{
		$(this).find(".email-category").addClass('expanded');
		$(this).find(".email-content").css('display','block');
		return false;
		
	}
	
});
$(document).on('click','.email-content',function(event){
	event.stopPropagation();
});

var isVendastaValid;
function validateVendastaFields(){
	isVendastaValid = true;
	var isFocussed = false;

	if (!validateVendastaAccountId('account-iden')) {
		isVendastaValid = false;
		if (!isFocussed) {
			$('#account-iden').focus();
			isFocussed = true;
		}
	}

	return isVendastaValid;
}

function validateVendastaAccountCreationForm(){
	var isVendastaAccountCreationValid = true;
	var isFocussed = false;

	if (!validateVendastaCompanyName('vendasta-hierarchy-name')) {
		isVendastaAccountCreationValid = false;
		if (!isFocussed) {
			$('#vendasta-hierarchy-name').focus();
			isFocussed = true;
		}
	}
	if (!validateVendastaCountry('vendasta-country-name', 'vendasta-country-code')) {
		isVendastaAccountCreationValid = false;
		if (!isFocussed) {
			$('#vendasta-country-name').focus();
			isFocussed = true;
		}
	}
	if (!validateVendastaState('vendasta-state-name')) {
		isVendastaAccountCreationValid = false;
		if (!isFocussed) {
			$('#vendasta-state-name').focus();
			isFocussed = true;
		}
	}
	if (!validateVendastaCity('vendasta-city-name')) {
		isVendastaAccountCreationValid = false;
		if (!isFocussed) {
			$('#vendasta-city-name').focus();
			isFocussed = true;
		}
	}
	if (!validateVendastaAddress('vendasta-address')) {
		isVendastaAccountCreationValid = false;
		if (!isFocussed) {
			$('#vendasta-address').focus();
			isFocussed = true;
		}
	}
	if (!validateVendastaZip('vendasta-zip')) {
		isVendastaAccountCreationValid = false;
		if (!isFocussed) {
			$('#vendasta-zip').focus();
			isFocussed = true;
		}
	}

	return isVendastaAccountCreationValid;
}

function initiateVendastaAccountCreation(){ 
	$(document).on('click', '#vendasta-rm-create-account', function(e) {
		e.stopPropagation();
		hideTapedMessages();
		if (validateVendastaAccountCreationForm()) {
			showOverlay();
			var formData = {
				"companyName" : $('#vendasta-hierarchy-name').val(),
				"country" : $('#vendasta-country-code').val(),
				"state" : $('#vendasta-state-name').val(),
				"city" : $('#vendasta-city-name').val(),
				"address" : $('#vendasta-address').val(),
				"zip" : $('#vendasta-zip').val()
			};
			callAjaxPostWithPayloadData("/setuplistingsmanager.do", function(data) {
				hideOverlay();
				var result = JSON.parse(data);
				if (result.type != "ERROR_MESSAGE") {
					
					var apiResult = JSON.parse(result.message);
					
					$('#account-iden').val(apiResult.customerIdentifier);
					$('#vendasta-create-accnt-form').hide();
					$('#vendasta-rm-create-account').hide();
					$('#vendasta-settings-form').show();
					showInfoMobileAndWeb(apiResult.message.replace(/^"(.+)"$/,'$1'));
				} else {
					showErrorInvalidMobileAndWeb(result.message.replace(/^"(.+)"$/,'$1'));
				}
			}, formData, true, '#vendasta-rm-create-account');
		}
	});
}

function vendastaCountryAutoComplete(){
	// Integrating autocomplete with country input text field
	$("#vendasta-country-name").autocomplete({
		minLength : 1,
		source : countryData,
		delay : 0,
		autoFocus : true,
		open : function(event, ui) {
			$("#vendasta-country-code").val("");
		},
		select : function(event, ui) {
			$("#vendasta-country-name").val(ui.item.label);
			$("#vendasta-country-code").val(ui.item.code);
			return false;
		},
		close : function(event, ui) {
		},
		create : function(event, ui) {
			$('.ui-helper-hidden-accessible').remove();
		}
	}).autocomplete("instance")._renderItem = function(ul, item) {
		return $("<li>").append(item.label).appendTo(ul);
	};
}

function hideTapedMessages(){
	hideInfo();
	hideError();
	hideErrorSuccess();
	hideInfoSuccess();
	hideErrorInvalid();
	hideInfoInvalid();
}

$('body').on('click', '.st-dd-item-survey-mail-thrs', function() {

	$('#survey-mail-threshold').val($(this).html());
	$('#st-dd-wrapper-survey-mail-thrs').slideToggle(200);
	
	var payload = {
			"surveyCompletedMailThreshold" : $('#survey-mail-threshold').val()
		};
		
	callAjaxPostWithPayloadData( "./updatesurveymailthreshold.do", function(data){
				$('#overlay-toast').html(data);
				showToast();
			}, payload, false);
});

function getInitials( name ){
    if( name != undefined && name != "" ){
        return name.charAt(0).toUpperCase();
    } else {
        return "";
    }
}

function downloadAccountStatsReport(){
	
	callAjaxPOST('./downloadaccountstatisticsreport.do', function(data){
		$('#overlay-toast').html(data);
		showToast();
		getAccStatsReportStatus();
	}, false);
}

function getAccStatsReportStatus(){
	callAjaxGET('./getaccountstatisticsreportstatus.do', function(data){
		var reportDetails = JSON.parse(JSON.parse(data));
		
		if(reportDetails.status == 1 || reportDetails.status == 2){
			if($('#acc-stats-gen-rep').hasClass('acc-stats-rep-btn-enabled')){
				$('#acc-stats-gen-rep').removeClass('acc-stats-rep-btn-enabled');
			}

			$('#acc-stats-rep-bnt').css('pointer-events','none');
			$('#acc-stats-gen-rep').addClass('acc-stats-rep-btn-disabled');
			
			if($('#account-stats-status-link').hasClass('download-acc-stats-rep')){
				$('#account-stats-status-link').removeClass('download-acc-stats-rep')
			}
			$('#account-stats-status-link').addClass('pending-acc-stats-rep')
			$('#account-stats-status-link').html('Report Pending');
			$('#account-stats-status-link').removeAttr('href');
			$('#account-stats-status-link').css('pointer-events','none');
		}else if(reportDetails.status  == 0){
			if($('#acc-stats-gen-rep').hasClass('acc-stats-rep-btn-disabled')){
				$('#acc-stats-gen-rep').removeClass('acc-stats-rep-btn-disabled');
			}
			$('#acc-stats-rep-bnt').css('pointer-events','auto');
			$('#acc-stats-gen-rep').addClass('acc-stats-rep-btn-enabled');
			
			if($('#account-stats-status-link').hasClass('pending-acc-stats-rep')){
				$('#account-stats-status-link').removeClass('pending-acc-stats-rep');
			}
			$('#account-stats-status-link').addClass('download-acc-stats-rep')
			$('#account-stats-status-link').html('Download the report');
			$('#account-stats-status-link').attr('href',reportDetails.fileName);
			$('#account-stats-status-link').css('pointer-events','auto');
		}else if(reportDetails.status == 4){
			if($('#acc-stats-gen-rep').hasClass('acc-stats-rep-btn-disabled')){
				$('#acc-stats-gen-rep').removeClass('acc-stats-rep-btn-disabled');
			}
			$('#acc-stats-rep-bnt').css('pointer-events','auto');
			$('#acc-stats-gen-rep').addClass('acc-stats-rep-btn-enabled');
			
			if($('#account-stats-status-link').hasClass('download-acc-stats-rep')){
				$('#account-stats-status-link').removeClass('download-acc-stats-rep')
			}
			$('#account-stats-status-link').addClass('pending-acc-stats-rep')
			$('#account-stats-status-link').html('Report Generation Failed. Please Try Again.');
			$('#account-stats-status-link').removeAttr('href');
			$('#account-stats-status-link').css('pointer-events','none');
		}
	}, false);
}

$(document).on('click','#acc-stats-rep-bnt',function(){
	downloadAccountStatsReport();
});


// survey csv file functions

$(document).on('change', '.survey-csv-file-input', function(){
	$("#upload-email-invalid").hide();
	processAndValidateCsvForm( true );
});

$(document).on('click','#wc-send-survey-upload-cancel',function(event){
	$(".wc-btn-row").show();
	$(".welcome-popup-body-wrapper").show();
	$(".survey-upload-csv").hide();
});

$(document).on('click','#wc-send-survey-upload-confirm',function(event){
	$('#send-survey-csv-dash').removeClass("hide");
	if( !processAndValidateCsvForm( false ) ){
		
		if( !$('#send-survey-csv-dash').hasClass("hide") ){
			$('#send-survey-csv-dash').addClass("hide");
		}
		hideOverlay();
		return;
	}

	var formData = new FormData();
	formData.append("file", $('#survey-file-intake').prop("files")[0]);
	formData.append("filename", $('#survey-file-intake').prop("files")[0].name);
	formData.append( "uploaderEmail", $('#survey-uploader-email').val() );
	formData.append("hierarchyType",$('#hierarchyType').val() );
	formData.append("hierarchyValue",$('#hierarchyValue').val() );
	callAjaxPOSTWithTextData("./savesurveycsvfile.do", function(callbackData){
		$('#send-survey-csv-dash').addClass("hide");
		var response = JSON.parse(callbackData);
		$("#overlay-toast").html(response.message);
		showToast();
		
	}, true, formData);
});

$(document).on('click','#wc-send-survey-upload-csv',function(event){
	$(".wc-btn-row").hide();
	$(".welcome-popup-body-wrapper").hide();
	$(".survey-upload-csv").show();
});

function csvFileValidate(inputFileElement, whileUploading) {
	
	$('.display-load').hide();
	
	if( whileUploading ){
		$('.survey-csv-file-info').hide();
	}

	if ($(inputFileElement).attr("type") == "file") {
		var fileName = $(inputFileElement).val();
		if (fileName.length > 0) {
			if (fileName.substr(fileName.length - 4, 4).toLowerCase() == ".csv") {
								
				var fileAddress = $(inputFileElement).val().split('\\');
				$('#survey-csv-file-name').text(fileAddress[fileAddress.length - 1]);
				$('.survey-csv-file-info').show();
				return true;
			}
		} else {
			$('.display-load').show();
			$(inputFileElement).val = "";
			return false;
		}
	} else {
		$('.display-load').show();
		return false;
	}
}

function uploaderEmailValidate(){
	return ( $('#survey-uploader-email').val() == undefined ||  $('#survey-uploader-email').val() == '' ) ? false : true;
}

function processAndValidateCsvForm(whileUploading){
	
	$("#upload-email-invalid").hide();
	
	if( !csvFileValidate("#survey-file-intake") )
	{
		if( !$("#wc-send-survey-upload-confirm").hasClass('disable') ){
			$("#wc-send-survey-upload-confirm").addClass('disable');
		}
		$('.display-load').show();
		$('.survey-csv-file-info').hide();
		$("#overlay-toast").html("Please select a valid csv file");
		showToast();
		return false;
	} else if( !uploaderEmailValidate() && !whileUploading){
		$("#upload-email-invalid").show();
		return false;
	} else {
		if( $("#wc-send-survey-upload-confirm").hasClass('disable') ){
			$("#wc-send-survey-upload-confirm").removeClass('disable');
		}
		return true;
	}
}

$(document).on('click', '#survey-uploader-email', function(){
	$("#upload-email-invalid").hide();
});

//google my business apis
var map;
var service;
var gmbQuery;
function getPlaceIds(query){
	/*var key="AIzaSyAy49K94uo1F2PGylIPcsTEpTCtsDEnK48"
	var url="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+query+"&key="+key;
	payload={
		"query":query,
		"key":key
	};
	
	$.ajax({
		url : url,
		type : "GET",
		dataType : "jsonp",
		cache : false,
		success : function(data) {
			console.log(data);
			//console.log(JSON.parse(data));
		},
		error : function(e) {
			isSurveydetailsforgraph = false;

	});*/
	gmbQuery=query;
	initializeGmb();
	
}

function initializeGmb() {
	  var place = new google.maps.LatLng(36.778259,-119.417931);
	  
	  map = new google.maps.Map(document.getElementById('gmb-map'), {
	      center: place,
	      zoom: 0
	    });
	  	var searchQuery = gmbQuery;
	  var request = {
	    query: searchQuery
	  };

	  service = new google.maps.places.PlacesService(map);
	  service.textSearch(request, callback);
	}

	function callback(results, status) {
		var index=1;
	  if (status == google.maps.places.PlacesServiceStatus.OK) {
		
		if(!($('#zero-suggestions-gmb').hasClass('hide'))){
			$('#zero-suggestions-gmb').addClass('hide');
		}
		
	    for (var i = 0; i < results.length && index<6; i++) {
	      var place = results[i];
	      if(i==0){
	    	  $('#gmb-radio-'+index).removeClass('hide');
	    	  $('#placeId'+index).attr('value',place.place_id);
	    	  $('#gmb-address'+index).html(place.name+', '+place.formatted_address);
	    	  $('#gmb-placeId'+index++).html(place.place_id);
	    	  $('#gmb-placeId-selected').html(place.place_id);
	    	  $('#gmb-url-placeId').html("https://search.google.com/local/writereview?placeid="+place.place_id);
	    	  $('#gm-sel-link').attr('href',"https://search.google.com/local/writereview?placeid="+place.place_id);
	      }else{
	    	  $('#gmb-radio-'+index).removeClass('hide');
	    	  $('#placeId'+index).attr('value',place.place_id);
	    	  $('#gmb-address'+index).html(place.name+', '+place.formatted_address);
	    	  $('#gmb-placeId'+index++).html(place.place_id);
	      }
	      
	    }
	  }else{
		  if($('#zero-suggestions-gmb').hasClass('hide')){
			  $('#zero-suggestions-gmb').removeClass('hide');
		  }
	  }
	  
	}


/*transaction monitor*/
var sysOptions = {
		chartArea : {
			width : '80%',
			height : '75%'
		},
		colors : [ '#cdcdcd','#812ebf'],
		legend : {
			position : 'none'
		},
		vAxis : { 
			baselineColor : 'rgb(238,238,238)',
			gridlines : { color : 'rgb(238,238,238)',count:3},
			viewWindow: {
		        min: 0
		    }
		},
		hAxis: {
			 slantedText: false,
			 gridlines : {count:8},
           textStyle: { fontSize: 8 }
       },
       pointSize: 5
};

var dangerOptions = {
		chartArea : {
			width : '80%',
			height : '75%'
		},
		colors : [ '#cdcdcd','#ff2424'],
		legend : {
			position : 'none'
		},
		vAxis : { 
			baselineColor : 'rgb(238,238,238)',
			gridlines : { color : 'rgb(238,238,238)',count:3},
			viewWindow: {
		        min: 0
		    }
		},
		hAxis: {
			 slantedText: false,
			 gridlines : {count:8},
	           textStyle: { fontSize: 8 }
	       },
	       pointSize: 5
};

var warnOptions = {
		chartArea : {
			width : '80%',
			height : '75%'
		},
		colors : [ '#cdcdcd','#ffb524'],
		legend : {
			position : 'none'
		},
		vAxis : { 
			baselineColor : 'rgb(238,238,238)',
			gridlines : { color : 'rgb(238,238,238)',count:3},
			viewWindow: {
		        min: 0
		    }
		},
		hAxis: {
			 slantedText: false,
             textStyle: { fontSize: 8 },
             gridlines : {count:8}
       },
       pointSize: 5
};

var grayOptions = {
		chartArea : {
			width : '80%',
			height : '75%'
		},
		colors : [ 'transparent','transparent'],
		legend : {
			position : 'none'
		},
		vAxis : { 
			baselineColor : 'rgb(238,238,238)',
			gridlines : { color : 'rgb(238,238,238)',count:3},
			viewWindow: {
		        min: 0
		    }
		},
		hAxis: {
			 slantedText: false,
           textStyle: { fontSize: 8 },
		 gridlines : {count:8}
       },
       pointSize: 5
};

var normalOptions = {
		chartArea : {
			width : '80%',
			height : '75%'
		},
		colors : [ '#cdcdcd','#4583cd'],
		legend : {
			position : 'none'
		},
		vAxis : { 
			baselineColor : 'rgb(238,238,238)',
			gridlines : { color : 'rgb(238,238,238)',count:3},
			viewWindow: {
		        min: 0
		    }
		},
		hAxis: {
			 slantedText: false,
			 gridlines : {count:8},
            textStyle: { fontSize: 8 }
        },
        pointSize: 5
};

var isSystemTransactionGraph=false;
function showSystemSurveyGraph(companyId, numberOfDays) {
	
	showOverlay();
	
	if (isSystemTransactionGraph == true) {
		return;
	}
	
	var payload = {
		"companyId" : companyId,
		"noOfDays" : numberOfDays
	};
	isSystemTransactionGraph = true;
	$.ajax({
		url : "./getcompanysurveystatuscountforpastndays.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			showOverlay();
			isSystemTransactionGraph = false;
			graphData = data;
			paintTransactionMonitorGraph(graphData);
		},
		error : function(e) {
			isSystemTransactionGraph = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function emptySlotDataConstructor(emptySlotDataObject){
	this.companyId = emptySlotDataObject.companyId;
	this.transactionDate = emptySlotDataObject.transactionDate;
	this.surveyInvitationSentCount = emptySlotDataObject.surveyInvitationSentCount;
	this.transactionReceivedCount = emptySlotDataObject.transactionReceivedCount;
	this.surveycompletedCount = emptySlotDataObject.surveycompletedCount;
	this.surveyReminderSentCount = emptySlotDataObject.surveyReminderSentCount;
	this.corruptedCount = emptySlotDataObject.corruptedCount;
	this.duplicateCount = emptySlotDataObject.duplicateCount;
	this.oldRecordCount = emptySlotDataObject.oldRecordCount;
	this.ignoredCount = emptySlotDataObject.ignoredCount;
	this.mismatchedCount = emptySlotDataObject.mismatchedCount;
	this.notAllowedCount = emptySlotDataObject.notAllowedCount;
}
var pastWeekData = new Array();
var currentWeekData = new Array();

function setPastCurGraphData(graphData){
	
	var emptySlotGraphData = {
			 "companyId": 0,
		     "transactionDate": "Nov 30, 2017",
		     "surveyInvitationSentCount": 0,
		     "transactionReceivedCount": 0,
		     "surveycompletedCount": 0,
		     "surveyReminderSentCount": 0,
		     "corruptedCount": 0,
		     "duplicateCount": 0,
		     "oldRecordCount": 0,
		     "ignoredCount": 0,
		     "mismatchedCount": 0,
		     "notAllowedCount": 0
	}
	
	var allTimeSlots = new Array();
	var graphTimeSlots = new Array();
	var emptyGraphTimeSlots = new Array();
	
	var format = 14;
	var type = 'Date';
	var keys = getKeysFromGraphFormat(format);
	for(var i=0;i<keys.length;i++){
		allTimeSlots[i] = convertYearMonthDayKeyToMonthDay(keys[i]);
		emptyGraphTimeSlots[i]=convertYearMonthDayKeyToMonthDayYear(keys[i]);
	}
	
	for(var i=0;i<graphData.length;i++){
		var j=0;
		var hasSlotDate = false;
		var entityDate = graphData[i].transactionDate;
		
		var formattedDate = new Date(Date.parse(entityDate));
	    //get date similar to keys formay
	    
	    var month = formattedDate.getMonth() + 1;
		var monthStr = "";
		if (month < 10) {
			monthStr = '0' + month.toString();
		}else{
			monthStr = month.toString();
		}
		
		var dayStr = "";
		var day  = formattedDate.getDate();
		if (day < 10) {
			dayStr = '0' + day.toString();
		}else{
			dayStr = day.toString();
		}
		
		var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
		var graphDate = convertYearMonthDayKeyToMonthDay(keyFormattedDate);
		graphTimeSlots[i]= graphDate;
		
	}
	
	for(var i=0;i<allTimeSlots.length;i++){
		var j=0;
		hasSlotDate = false;
		while(j<graphTimeSlots.length){
			if(allTimeSlots[i]==graphTimeSlots[j]){
				hasSlotDate = true;
				break;
			}else{
				j++;
			}
		}

		emptySlotGraphData.transactionDate = emptyGraphTimeSlots[i];
		if(hasSlotDate){
			if(i<7){
				pastWeekData[i] = graphData[j];
			}else{
				currentWeekData[i-7] = graphData[j];
			}
		}else{
			if(i<7){
				pastWeekData[i] = new emptySlotDataConstructor(emptySlotGraphData);
			}else{
				currentWeekData[i-7] = new emptySlotDataConstructor(emptySlotGraphData);
			}
		}
	}
}

function paintTransactionMonitorGraph(graphData) {
	
	if (graphData == undefined)
		return;
	
	pastWeekData = new Array();
	currentWeekData = new Array();
	var allTimeslots = new Array();
	
	var pastWeekAutomatedTransactions = new Array();
	var pastWeekInvitationsSent = new Array();
	var pastWeekUnprocessedTransactions = new Array();
	var pastWeekRemindersSent = new Array();
	var pastWeekCompletedTransactions = new Array();
	
	var currentWeekAutomatedTransactions = new Array();
	var currentWeekInvitationsSent = new Array();
	var currentWeekUnprocessedTransactions = new Array();
	var currentWeekRemindersSent = new Array();
	var currentWeekCompletedTransactions = new Array();
	
	var format = 7;
	var type = 'Date';
	var keys = getKeysFromGraphFormat(format);
	
	
	for(var i=0;i<keys.length;i++){
		allTimeslots[i] = convertYearMonthDayKeyToMonthDay(keys[i]);
		pastWeekAutomatedTransactions[i] = 0;
		pastWeekInvitationsSent[i] = 0;
		pastWeekUnprocessedTransactions[i] = 0;
		pastWeekRemindersSent[i] = 0;
		pastWeekCompletedTransactions[i] = 0;
			
		currentWeekAutomatedTransactions[i] = 0;
		currentWeekInvitationsSent[i] = 0;
		currentWeekUnprocessedTransactions[i] = 0;
		currentWeekRemindersSent[i] = 0;
		currentWeekCompletedTransactions[i] = 0;
	}
	
	setPastCurGraphData(graphData);
	
	if(graphData != undefined){
		for(i=0;i<currentWeekData.length;i++){
			var currentWeekDataEntity = currentWeekData[i];
			var pastWeekDataEntity = pastWeekData[i];
			
			var entityDate = currentWeekDataEntity.transactionDate;
			
			var formattedDate = new Date(Date.parse(entityDate));
		    //get date similar to keys formay
		    
		    var month = formattedDate.getMonth() + 1;
			var monthStr = "";
			if (month < 10) {
				monthStr = '0' + month.toString();
			}else{
				monthStr = month.toString();
			}
			
			var dayStr = "";
			var day  = formattedDate.getDate();
			if (day < 10) {
				dayStr = '0' + day.toString();
			}else{
				dayStr = day.toString();
			}
			
			var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
			if(keys.indexOf(keyFormattedDate) > -1){
				var index = keys.indexOf(keyFormattedDate);
				
				pastWeekAutomatedTransactions[index] = pastWeekDataEntity.transactionReceivedCount;
				pastWeekCompletedTransactions[index] =  pastWeekDataEntity.surveycompletedCount;
				pastWeekInvitationsSent[index] =  pastWeekDataEntity.surveyInvitationSentCount;
				pastWeekRemindersSent[index] =  pastWeekDataEntity.surveyReminderSentCount;
				pastWeekUnprocessedTransactions[index] = pastWeekDataEntity.transactionReceivedCount - pastWeekDataEntity.surveyInvitationSentCount;
			
				currentWeekAutomatedTransactions[index] = currentWeekDataEntity.transactionReceivedCount;
				currentWeekCompletedTransactions[index] =  currentWeekDataEntity.surveycompletedCount;
				currentWeekInvitationsSent[index] =  currentWeekDataEntity.surveyInvitationSentCount;
				currentWeekRemindersSent[index] =  currentWeekDataEntity.surveyReminderSentCount;
				currentWeekUnprocessedTransactions[index] = currentWeekDataEntity.transactionReceivedCount - currentWeekDataEntity.surveyInvitationSentCount;
			}
		}
	}
	
	var automatedData= [];
	var sentData = [];
	var completedData = [];
	var remindersData = [];
	var unprocessedData = [];
	
	var nestedInternalAutomatedData = [];
	var nestedInternalSentData = [];
	var nestedInternalCompletedData = [];
	var nestedInternalRemindersData = [];
	var nestedInternalUnprocessedData = [];
	
	nestedInternalAutomatedData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalSentData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalCompletedData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalRemindersData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalUnprocessedData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	
	automatedData.push(nestedInternalAutomatedData);
	sentData.push(nestedInternalSentData);
	completedData.push(nestedInternalCompletedData);
	remindersData.push(nestedInternalRemindersData);
	unprocessedData.push(nestedInternalUnprocessedData);
	
	var xAxisTimeSlots = formatAllTimeSlots(allTimeslots);
	
	for (var itr = 0; itr < allTimeslots.length; itr++) {
		nestedInternalAutomatedData = [];
		nestedInternalSentData = [];
		nestedInternalCompletedData = [];
		nestedInternalRemindersData = [];
		nestedInternalUnprocessedData = [];
		
		var curAutomatedTransactionCount;
		var curCompletedTransactionCount;
		var curSentInvitationCount;
		var curReminderSentCount;
		var curUnprocessedTransactionsCount;
		
		var prevAutomatedTransactionCount;
		var prevCompletedTransactionCount;
		var prevSentInvitationCount;
		var prevReminderSentCount;
		var prevUnprocessedTransactionsCount;
		
		if (isNaN(parseInt(currentWeekAutomatedTransactions[itr]))) {
			curAutomatedTransactionCount = 0;
		} else {
			curAutomatedTransactionCount = parseInt(currentWeekAutomatedTransactions[itr]);
		}
		
		if (isNaN(parseInt(pastWeekAutomatedTransactions[itr]))) {
			prevAutomatedTransactionCount = 0;
		} else {
			prevAutomatedTransactionCount = parseInt(pastWeekAutomatedTransactions[itr]);
		}

		if (isNaN(parseInt(currentWeekInvitationsSent[itr]))) {
			curSentInvitationCount = 0;
		} else {
			curSentInvitationCount = parseInt(currentWeekInvitationsSent[itr]);
		}
		
		if (isNaN(parseInt(pastWeekInvitationsSent[itr]))) {
			prevSentInvitationCount = 0;
		} else {
			prevSentInvitationCount = parseInt(pastWeekInvitationsSent[itr]);
		}
		
		if (isNaN(parseInt(currentWeekRemindersSent[itr]))) {
			curReminderSentCount = 0;
		} else {
			curReminderSentCount = parseInt(currentWeekRemindersSent[itr]);
		}
		
		if (isNaN(parseInt(pastWeekRemindersSent[itr]))) {
			prevReminderSentCount = 0;
		} else {
			prevReminderSentCount = parseInt(pastWeekRemindersSent[itr]);
		}
		
		if (isNaN(parseInt(currentWeekCompletedTransactions[itr]))) {
			curCompletedTransactionCount = 0;
		} else {
			curCompletedTransactionCount = parseInt(currentWeekCompletedTransactions[itr]);
		}
		
		if (isNaN(parseInt(pastWeekCompletedTransactions[itr]))) {
			prevCompletedTransactionCount = 0;
		} else {
			prevCompletedTransactionCount = parseInt(pastWeekCompletedTransactions[itr]);
		}
		
		if (isNaN(parseInt(currentWeekUnprocessedTransactions[itr]))) {
			curUnprocessedTransactionsCount = 0;
		} else {
			curUnprocessedTransactionsCount = parseInt(currentWeekUnprocessedTransactions[itr]);
		}
		
		if (isNaN(parseInt(pastWeekUnprocessedTransactions[itr]))) {
			prevUnprocessedTransactionsCount = 0;
		} else {
			prevUnprocessedTransactionsCount = parseInt(pastWeekUnprocessedTransactions[itr]);
		}

		nestedInternalAutomatedData.push(xAxisTimeSlots[itr], prevAutomatedTransactionCount,'Previous Week: '+prevAutomatedTransactionCount,curAutomatedTransactionCount);
		nestedInternalSentData.push(xAxisTimeSlots[itr], prevSentInvitationCount, 'Previous Week: '+prevSentInvitationCount, curSentInvitationCount);
		nestedInternalCompletedData.push(xAxisTimeSlots[itr], prevCompletedTransactionCount, 'Previous Week: '+prevCompletedTransactionCount, curCompletedTransactionCount);
		nestedInternalRemindersData.push(xAxisTimeSlots[itr], prevReminderSentCount, 'Previous Week: '+prevReminderSentCount,curReminderSentCount);
		nestedInternalUnprocessedData.push(xAxisTimeSlots[itr], prevUnprocessedTransactionsCount, 'Previous Week: '+prevUnprocessedTransactionsCount, curUnprocessedTransactionsCount);
		
		automatedData.push(nestedInternalAutomatedData);
		sentData.push(nestedInternalSentData);
		remindersData.push(nestedInternalRemindersData);
		completedData.push(nestedInternalCompletedData);
		unprocessedData.push(nestedInternalUnprocessedData);
	}

	if(isSystemTransMonGraphEmpty(automatedData)){
		drawTransactionMonitorGraphs(automatedData, grayOptions, sysAutoTransGraphId);
	}else{
		drawTransactionMonitorGraphs(automatedData, sysOptions, sysAutoTransGraphId);
	}
	
	if(isSystemTransMonGraphEmpty(completedData)){
		drawTransactionMonitorGraphs(completedData, grayOptions, sysCompTransGraphId);
	}else{
		drawTransactionMonitorGraphs(completedData, sysOptions, sysCompTransGraphId);
	}
	
	if(isSystemTransMonGraphEmpty(sentData)){
		drawTransactionMonitorGraphs(sentData, grayOptions, sysInvSentGraphId);
	}else{
		drawTransactionMonitorGraphs(sentData, sysOptions, sysInvSentGraphId);
	}
	
	if(isSystemTransMonGraphEmpty(remindersData)){
		drawTransactionMonitorGraphs(remindersData, grayOptions, sysRemSentGraphId);
	}else{
		drawTransactionMonitorGraphs(remindersData, sysOptions, sysRemSentGraphId);
	}
	
	if(isSystemTransMonGraphEmpty(unprocessedData)){
		drawTransactionMonitorGraphs(unprocessedData, grayOptions, sysUnproTransGraphId);
	}else{
		drawTransactionMonitorGraphs(unprocessedData, sysOptions, sysUnproTransGraphId);
	}
	
}

function isSystemTransMonGraphEmpty(graphData){
	var isEmpty = true;
	for(var i=0;i<graphData.length;i++){
		if(graphData[i][1]>0 || graphData[i][3]>0){
			isEmpty = false;
			break;
		}
	}
	return isEmpty;
}

function drawTransactionMonitorGraphs(graphData,options,graphDiv){
	var data = google.visualization.arrayToDataTable(graphData);
	
	removeAllPreviousGraphToolTip();
	
	var chart = new google.visualization.LineChart(document.getElementById(graphDiv));
	chart.draw(data, options);
	
}

function drawTransGraphWrapper(companyId,alert,companyName){
	
	if(alert == 'danger'){
		$('#transaction-monitor-graph-container').append(dangerGraphWrapper);
		$('#trans-wrapper-header-span-danger').attr('id','trans-wrapper-header-span-danger-'+companyId);
		$('#trans-wrapper-header-span-danger-'+companyId).html(companyName);
		$('#trans-graph-container').attr('id','trans-graph-container-'+companyId);
		
	}else if(alert == 'warn'){
		$('#transaction-monitor-graph-container').append(warnGraphWrapper);
		$('#trans-wrapper-header-span-warn').attr('id','trans-wrapper-header-span-warn-'+companyId);
		$('#trans-wrapper-header-span-warn-'+companyId).html(companyName);
		$('#trans-graph-container').attr('id','trans-graph-container-'+companyId);
		
	}else if(alert == 'gray'){
		$('#transaction-monitor-graph-container').append(grayGraphWrapper);
		$('#trans-wrapper-header-span-gray').attr('id','trans-wrapper-header-span-gray-'+companyId);
		$('#trans-wrapper-header-span-gray-'+companyId).html(companyName);
		$('#trans-graph-container').attr('id','trans-graph-container-'+companyId);
		
	}else if(alert == 'normal'){
		$('#transaction-monitor-graph-container').append(normalGraphWrapper);
		$('#trans-wrapper-header-span-normal').attr('id','trans-wrapper-header-span-normal-'+companyId);
		$('#trans-wrapper-header-span-normal-'+companyId).html(companyName);
		$('#trans-graph-container').attr('id','trans-graph-container-'+companyId);
		
	}
}

function drawTransGraphContainer(companyId,alert,type,alertType){
	
	if(alert == 'danger'){
		$('#trans-graph-container-'+companyId).attr('id','trans-graph-container-'+alertType+'-'+companyId);
		$('#trans-graph-container-'+alertType+'-'+companyId).append(dangerGraphContainer);
		$('#trans-graph').attr('id','trans-graph-danger-'+alertType+'-'+companyId);
		return drawTypeSpanText(type,'trans-graph-danger-'+alertType+'-'+companyId);
		
	}else if(alert == 'warn'){
		$('#trans-graph-container-'+companyId).attr('id','trans-graph-container-'+alertType+'-'+companyId);
		$('#trans-graph-container-'+alertType+'-'+companyId).append(warnGraphContainer);
		$('#trans-graph').attr('id','trans-graph-warn-'+alertType+'-'+companyId);
		return drawTypeSpanText(type,'trans-graph-warn-'+alertType+'-'+companyId);
		
	}else if(alert == 'gray'){
		$('#trans-graph-container-'+companyId).attr('id','trans-graph-container-'+alertType+'-'+companyId);
		$('#trans-graph-container-'+alertType+'-'+companyId).append(grayGraphContainer);
		$('#trans-graph').attr('id','trans-graph-gray-'+alertType+'-'+companyId);
		return drawTypeSpanText(type,'trans-graph-gray-'+alertType+'-'+companyId);
		
	}else if(alert == 'normal'){
		$('#trans-graph-container-'+companyId).attr('id','trans-graph-container-'+alertType+'-'+companyId);
		$('#trans-graph-container-'+alertType+'-'+companyId).append(normalGraphContainer);
		$('#trans-graph').attr('id','trans-graph-normal-'+alertType+'-'+companyId);
		return drawTypeSpanText(type,'trans-graph-normal-'+alertType+'-'+companyId);
		
	}
	
}

function drawTypeSpanText(type,graphId){
	if(type == autoType){
		$('#'+graphId).attr('id',graphId+'-auto');
		$('#'+graphId+'-auto').siblings('.trans-monitor-graph-span').html(automatedTransText);
		return graphId+'-auto';
	}else if(type == inviType){
		$('#'+graphId).attr('id',graphId+'-invi');
		$('#'+graphId+'-invi').siblings('.trans-monitor-graph-span').html(inviteSentText);
		return graphId+'-invi';
	}else if(type == remType){
		$('#'+graphId).attr('id',graphId+'-rem');
		$('#'+graphId+'-rem').siblings('.trans-monitor-graph-span').html(reminderSentText);
		return graphId+'-rem';
	}else if(type == compType){
		$('#'+graphId).attr('id',graphId+'-comp');
		$('#'+graphId+'-comp').siblings('.trans-monitor-graph-span').html(completedTransText);
		return graphId+'-comp';
	}else if(type == unproType){
		$('#'+graphId).attr('id',graphId+'-unpro');
		$('#'+graphId+'-unpro').siblings('.trans-monitor-graph-span').html(unprocessedTransText);
		return graphId+'-unpro';
	}
}

var transactionMonitorData;
var isFetchingTransactionData=false;
var hasFetchedWarningData = false;
var hasFetchedNormalData = false;
var pastWeekTransData = new Array();
var curWeekTransData = new Array();
var errorCompanies = new Array();

function getTransactionMonitorData(alertType,noOfDays) {
	showOverlay();
	
	if (isFetchingTransactionData == true) {
		hideOverlay();
		return;
	}
	
	var payload = {
		"alertType" : alertType,
		"noOfDays" : noOfDays
	};
	
	isFetchingTransactionData = true;
	$.ajax({
		url : "./gettrnsactionmonitordatabydaysandalerttype.do",
		type : "GET",
		dataType : "JSON",
		cache : false,
		data : payload,
		success : function(data) {
			isFetchingTransactionData = false;
			transactionMonitorData = data;
			drawTransactionMonitorAlertGraphs(alertType,transactionMonitorData);
			if(hasFetchedWarningData && hasFetchedNormalData){
				hideOverlay();
			}
		},
		complete : function(){
			if(hasFetchedWarningData == false){
				hasFetchedWarningData = true;
				getTransactionMonitorData('warning',14);
			}
			
			if(alertType == 'warning' && hasFetchedNormalData == false){
				hasFetchedNormalData = true;
				getTransactionMonitorData('normal',14);
			}
		},
		error : function(e) {
			isFetchingTransactionData = false;
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(e.responseText);
			showToast();
			hideOverlay();
		}
	});
}

function drawTransactionMonitorAlertGraphs(alertType,transactionMonitorData){
	if(alertType == 'error'){
		drawTransMonDangerGraphs(transactionMonitorData);
	}else if(alertType == 'warning'){
		drawTransMonWarnGraphs(transactionMonitorData);
	}else if(alertType == 'normal'){
		drawTransMonNormalGraphs(transactionMonitorData);
	}
}

function drawTransMonDangerGraphs(transactionMonitorData){
	errorCompanies = new Array();
	
	for(var i=0;i<transactionMonitorData.length;i++){
		
		var transData = transactionMonitorData[i];
		var companyId = transData.companyId;
		var companyName = transData.companyName;
		
		errorCompanies.push(companyId);
		
		var autoStatus = 'normal';
		var inviStatus = 'normal';
		var remStatus = 'normal';
		var unproStatus = 'normal';
		var compStatus = 'normal';
		var currentWarningAlerts = transData.entityAlertDetails.currentWarningAlerts;
		var currentErrorAlerts = transData.entityAlertDetails.currentErrorAlerts;
		
		for(var j=0;j<currentWarningAlerts.length;j++){
			if(currentWarningAlerts[j].toUpperCase() == ('lessTransactionInPastDays').toUpperCase() || currentWarningAlerts[j].toUpperCase() == ('lessTransactionInPastWeek').toUpperCase()){
				autoStatus = 'warn';
			}else if(currentWarningAlerts[j].toUpperCase() == ('lessInvitationInPastDays').toUpperCase() || currentWarningAlerts[j].toUpperCase() == ('lessInvitationInPastWeek').toUpperCase()){
				inviStatus = 'warn';
			}else if(currentWarningAlerts[j].toUpperCase() == ('moreReminderInPastDays').toUpperCase() || currentWarningAlerts[j].toUpperCase() == ('moreReminderInPastWeek').toUpperCase()){
				remStatus = 'warn';
			}else if(currentWarningAlerts[j].toUpperCase() == ('lessSurveyCompletedInPastDays').toUpperCase() || currentWarningAlerts[j].toUpperCase() == ('lessSurveyCompletedInPastWeek').toUpperCase()){
				compStatus = 'warn';
			}else if(currentWarningAlerts[j].toUpperCase() == ('moreSurveyUnprocessedInPastDays').toUpperCase() || currentWarningAlerts[j].toUpperCase() == ('moreSurveyUnprocessedInPastWeek').toUpperCase()){
				unproStatus = 'warn';
			}
		}

		for(var j=0;j<currentErrorAlerts.length;j++){
			if(currentErrorAlerts[j] == 'lessTransactionInPastDays'){
				autoStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'lessInvitationInPastDays'){
				inviStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'moreReminderInPastDays'){
				remStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'lessSurveyCompletedInPastDays'){
				compStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'moreSurveyUnprocessedInPastDays'){
				unproStatus = 'danger';
			}
		}
		
		drawTransGraphWrapper(companyId, 'danger', companyName);
		
		var autoGraphId = drawTransGraphContainer(companyId, autoStatus, autoType,'error');
		var inviGraphId = drawTransGraphContainer(companyId, inviStatus, inviType,'error');
		var remGraphId = drawTransGraphContainer(companyId, remStatus, remType,'error');
		var unproGraphId = drawTransGraphContainer(companyId, unproStatus, unproType,'error');
		var compGraphId= drawTransGraphContainer(companyId, compStatus, compType,'error');
		var graphDetails = {
				"autoGraphId": autoGraphId,
				"autoStatus": autoStatus,
				"inviGraphId": inviGraphId,
				"inviStatus": inviStatus,
				"remGraphId": remGraphId,
				"remStatus": remStatus,
				"unproGraphId": unproGraphId,
				"unproStatus": unproStatus,
				"compGraphId": compGraphId,
				"compStatus": compStatus
		}
		drawAlertTypeGraphs(transData,graphDetails);
	}
	/*setPastAndCurWeekDataForTransactionMonitor(transactionMonitorData[0].companySurveyStatusStatslist);*/
}

function drawTransMonWarnGraphs(transactionMonitorData){
	for(var i=0;i<transactionMonitorData.length;i++){
		
		var transData = transactionMonitorData[i];
		var companyId = transData.companyId;
		var companyName = transData.companyName;
		var isErrorCompany = false;
		
		//Check if company comes under error alert type
		for(var errorItr=0;errorItr<errorCompanies.length;errorItr++){
			if(companyId == errorCompanies[errorItr]){
				isErrorCompany = true;
				break;
			}
		}
		
		if(isErrorCompany){
			continue;
		}
		
		var autoStatus = 'normal';
		var inviStatus = 'normal';
		var remStatus = 'normal';
		var unproStatus = 'normal';
		var compStatus = 'normal';
		var currentWarningAlerts = transData.entityAlertDetails.currentWarningAlerts;
		var currentErrorAlerts = transData.entityAlertDetails.currentErrorAlerts;
		
		for(var j=0;j<currentWarningAlerts.length;j++){
			if(currentWarningAlerts[j] == 'lessTransactionInPastDays'){
				autoStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'lessInvitationInPastDays'){
				inviStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'moreReminderInPastDays'){
				remStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'lessSurveyCompletedInPastDays'){
				compStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'moreSurveyUnprocessedInPastDays'){
				unproStatus = 'warn';
			}
		}

		for(var j=0;j<currentErrorAlerts.length;j++){
			if(currentErrorAlerts[j] == 'lessTransactionInPastDays'){
				autoStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'lessInvitationInPastDays'){
				inviStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'moreReminderInPastDays'){
				remStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'lessSurveyCompletedInPastDays'){
				compStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'moreSurveyUnprocessedInPastDays'){
				unproStatus = 'danger';
			}
		}
		
		drawTransGraphWrapper(companyId, 'warn', companyName);
		
		var autoGraphId = drawTransGraphContainer(companyId, autoStatus, autoType,'warning');
		var inviGraphId = drawTransGraphContainer(companyId, inviStatus, inviType,'warning');
		var remGraphId = drawTransGraphContainer(companyId, remStatus, remType,'warning');
		var unproGraphId = drawTransGraphContainer(companyId, unproStatus, unproType,'warning');
		var compGraphId = drawTransGraphContainer(companyId, compStatus, compType,'warning');
		
		var graphDetails = {
				"autoGraphId": autoGraphId,
				"autoStatus": autoStatus,
				"inviGraphId": inviGraphId,
				"inviStatus": inviStatus,
				"remGraphId": remGraphId,
				"remStatus": remStatus,
				"unproGraphId": unproGraphId,
				"unproStatus": unproStatus,
				"compGraphId": compGraphId,
				"compStatus": compStatus
		}
		drawAlertTypeGraphs(transData,graphDetails);
	}
}

function drawTransMonNormalGraphs(transactionMonitorData){
	for(var i=0;i<transactionMonitorData.length;i++){
		
		var transData = transactionMonitorData[i];
		var companyId = transData.companyId;
		var companyName = transData.companyName;
		
		var autoStatus = 'normal';
		var inviStatus = 'normal';
		var remStatus = 'normal';
		var unproStatus = 'normal';
		var compStatus = 'normal';
		var currentWarningAlerts = transData.entityAlertDetails.currentWarningAlerts;
		var currentErrorAlerts = transData.entityAlertDetails.currentErrorAlerts;
		
		for(var j=0;j<currentWarningAlerts.length;j++){
			if(currentWarningAlerts[j] == 'lessTransactionInPastDays'){
				autoStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'lessInvitationInPastDays'){
				inviStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'moreReminderInPastDays'){
				remStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'lessSurveyCompletedInPastDays'){
				compStatus = 'warn';
			}else if(currentWarningAlerts[j] == 'moreSurveyUnprocessedInPastDays'){
				unproStatus = 'warn';
			}
		}

		for(var j=0;j<currentErrorAlerts.length;j++){
			if(currentErrorAlerts[j] == 'lessTransactionInPastDays'){
				autoStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'lessInvitationInPastDays'){
				inviStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'moreReminderInPastDays'){
				remStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'lessSurveyCompletedInPastDays'){
				compStatus = 'danger';
			}else if(currentErrorAlerts[j] == 'moreSurveyUnprocessedInPastDays'){
				unproStatus = 'danger';
			}
		}
		
		drawTransGraphWrapper(companyId, 'normal', companyName);
		
		var autoGraphId = drawTransGraphContainer(companyId, autoStatus, autoType,'normals');
		var inviGraphId = drawTransGraphContainer(companyId, inviStatus, inviType,'normals');
		var remGraphId = drawTransGraphContainer(companyId, remStatus, remType,'normals');
		var unproGraphId = drawTransGraphContainer(companyId, unproStatus, unproType,'normals');
		var compGraphId = drawTransGraphContainer(companyId, compStatus, compType,'normals');
		
		var graphDetails = {
				"autoGraphId": autoGraphId,
				"autoStatus": autoStatus,
				"inviGraphId": inviGraphId,
				"inviStatus": inviStatus,
				"remGraphId": remGraphId,
				"remStatus": remStatus,
				"unproGraphId": unproGraphId,
				"unproStatus": unproStatus,
				"compGraphId": compGraphId,
				"compStatus": compStatus
		}
		drawAlertTypeGraphs(transData,graphDetails);
	}
}

function setEmptyPastAndCurWeekDataForTransactionMonitor(companyId){
	var emptySlotGraphData = {
			"dailySurveyStatusStatsId": "abcd",
            "companyId": companyId,
            "transactionDate": "",
            "surveyInvitationSentCount": 0,
            "transactionReceivedCount": 0,
            "surveycompletedCount": 0,
            "surveyReminderSentCount": 0,
            "corruptedCount": 0,
            "duplicateCount": 0,
            "oldRecordCount": 0,
            "ignoredCount": 0,
            "mismatchedCount": 0,
            "notAllowedCount": 0
	}
	
	for(var i=0;i<7;i++){
		pastWeekTransData[i]= new emptySlotDataConstructor(emptySlotGraphData);
		curWeekTransData[i]= new emptySlotDataConstructor(emptySlotGraphData);
	}
}

function setPastAndCurWeekDataForTransactionMonitor(graphData){
	var emptySlotGraphData = {
			 "dailySurveyStatusStatsId": "e",
             "companyId": 3,
             "transactionDate": "",
             "surveyInvitationSentCount": 0,
             "transactionReceivedCount": 0,
             "surveycompletedCount": 0,
             "surveyReminderSentCount": 0,
             "corruptedCount": 0,
             "duplicateCount": 0,
             "oldRecordCount": 0,
             "ignoredCount": 0,
             "mismatchedCount": 0,
             "notAllowedCount": 0
	}
	var companyId = graphData[0].companyId;
	emptySlotGraphData.companyId = companyId;
	
	var allTimeSlots = new Array();
	var graphTimeSlots = new Array();
	var emptyGraphTimeSlots = new Array();
	
	var format = 14;
	var type = 'Date';
	var keys = getKeysFromGraphFormat(format);
	for(var i=0;i<keys.length;i++){
		allTimeSlots[i] = convertYearMonthDayKeyToMonthDay(keys[i]);
		emptyGraphTimeSlots[i]=convertYearMonthDayKeyToMonthDayYear(keys[i]);
	}
	
	for(var i=0;i<graphData.length;i++){
		var j=0;
		var hasSlotDate = false;
		var entityDate = graphData[i].transactionDate;
		
		var formattedDate = new Date(Date.parse(entityDate));
	    //get date similar to keys formay
	    
	    var month = formattedDate.getMonth() + 1;
		var monthStr = "";
		if (month < 10) {
			monthStr = '0' + month.toString();
		}else{
			monthStr = month.toString();
		}
		
		var dayStr = "";
		var day  = formattedDate.getDate();
		if (day < 10) {
			dayStr = '0' + day.toString();
		}else{
			dayStr = day.toString();
		}
		
		var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
		var graphDate = convertYearMonthDayKeyToMonthDay(keyFormattedDate);
		graphTimeSlots[i]= graphDate;
		
	}
	
	var k=0;
	for(var i=0;i<allTimeSlots.length;i++){
		var j=0;
		hasSlotDate = false;
		while(j<graphTimeSlots.length){
			if(allTimeSlots[i]==graphTimeSlots[j]){
				hasSlotDate = true;
				break;
			}else{
				j++;
			}
		}

		emptySlotGraphData.transactionDate = emptyGraphTimeSlots[i];
		if(hasSlotDate){
			if(i<7){
				pastWeekTransData[i] = graphData[j];
			}else{
				curWeekTransData[i-7] = graphData[j];
			}
		}else{
			if(i<7){
				pastWeekTransData[i] = new emptySlotDataConstructor(emptySlotGraphData);
			}else{
				curWeekTransData[i-7] = new emptySlotDataConstructor(emptySlotGraphData);
			}
		}
	}
}

function drawAlertTypeGraphs(transactionMonitorData,graphDetails){
	
	var transData = transactionMonitorData.companySurveyStatusStatslist;
	var isEmptyData = false;
	
	var allTimeslots = new Array();
	pastWeekTransData = new Array();
	curWeekTransData = new Array();
	
	var pastWeekAutomatedTransactions = new Array();
	var pastWeekInvitationsSent = new Array();
	var pastWeekUnprocessedTransactions = new Array();
	var pastWeekRemindersSent = new Array();
	var pastWeekCompletedTransactions = new Array();
	
	var currentWeekAutomatedTransactions = new Array();
	var currentWeekInvitationsSent = new Array();
	var currentWeekUnprocessedTransactions = new Array();
	var currentWeekRemindersSent = new Array();
	var currentWeekCompletedTransactions = new Array();
	
	var format = 7;
	var type = 'Date';
	var keys = getKeysFromGraphFormat(format);
	
	
	for(var i=0;i<keys.length;i++){
		allTimeslots[i] = convertYearMonthDayKeyToMonthDay(keys[i]);
		pastWeekAutomatedTransactions[i] = 0;
		pastWeekInvitationsSent[i] = 0;
		pastWeekUnprocessedTransactions[i] = 0;
		pastWeekRemindersSent[i] = 0;
		pastWeekCompletedTransactions[i] = 0;
			
		currentWeekAutomatedTransactions[i] = 0;
		currentWeekInvitationsSent[i] = 0;
		currentWeekUnprocessedTransactions[i] = 0;
		currentWeekRemindersSent[i] = 0;
		currentWeekCompletedTransactions[i] = 0;
	}
	
	if(transData == undefined){
		setEmptyPastAndCurWeekDataForTransactionMonitor(transactionMonitorData.companyId);
		isEmptyData = true;
	}else{
		setPastAndCurWeekDataForTransactionMonitor(transData);
	}
		
	for(var i=0;i<curWeekTransData.length;i++){
			
			var currentWeekDataEntity = curWeekTransData[i];
			var pastWeekDataEntity = pastWeekTransData[i];
			
			var entityDate = currentWeekDataEntity.transactionDate;
			
			var formattedDate = new Date(Date.parse(entityDate));
		    //get date similar to keys formay
		    
		    var month = formattedDate.getMonth() + 1;
			var monthStr = "";
			if (month < 10) {
				monthStr = '0' + month.toString();
			}else{
				monthStr = month.toString();
			}
			
			var dayStr = "";
			var day  = formattedDate.getDate();
			if (day < 10) {
				dayStr = '0' + day.toString();
			}else{
				dayStr = day.toString();
			}
			
			var keyFormattedDate = formattedDate.getFullYear().toString() + monthStr + dayStr;
			
			if(keys.indexOf(keyFormattedDate) > -1){
				var index = keys.indexOf(keyFormattedDate);
				
				pastWeekAutomatedTransactions[index] = pastWeekDataEntity.transactionReceivedCount;
				pastWeekCompletedTransactions[index] =  pastWeekDataEntity.surveycompletedCount;
				pastWeekInvitationsSent[index] =  pastWeekDataEntity.surveyInvitationSentCount;
				pastWeekRemindersSent[index] =  pastWeekDataEntity.surveyReminderSentCount;
				pastWeekUnprocessedTransactions[index] = pastWeekDataEntity.transactionReceivedCount - pastWeekDataEntity.surveyInvitationSentCount;
			
				currentWeekAutomatedTransactions[index] = currentWeekDataEntity.transactionReceivedCount;
				currentWeekCompletedTransactions[index] =  currentWeekDataEntity.surveycompletedCount;
				currentWeekInvitationsSent[index] =  currentWeekDataEntity.surveyInvitationSentCount;
				currentWeekRemindersSent[index] =  currentWeekDataEntity.surveyReminderSentCount;
				currentWeekUnprocessedTransactions[index] = currentWeekDataEntity.transactionReceivedCount - currentWeekDataEntity.surveyInvitationSentCount;
			}
	}

	var automatedData= [];
	var sentData = [];
	var completedData = [];
	var remindersData = [];
	var unprocessedData = [];
	
	var nestedInternalAutomatedData = [];
	var nestedInternalSentData = [];
	var nestedInternalCompletedData = [];
	var nestedInternalRemindersData = [];
	var nestedInternalUnprocessedData = [];
	
	nestedInternalAutomatedData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalSentData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalCompletedData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalRemindersData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	nestedInternalUnprocessedData.push(type, 'PreviousWeek',{type: 'string', role: 'tooltip'}, 'CurrentWeek');
	
	automatedData.push(nestedInternalAutomatedData);
	sentData.push(nestedInternalSentData);
	completedData.push(nestedInternalCompletedData);
	remindersData.push(nestedInternalRemindersData);
	unprocessedData.push(nestedInternalUnprocessedData);
	
	var xAxisTimeSlots = formatAllTimeSlots(allTimeslots);
	for (var itr = 0; itr < allTimeslots.length; itr++) {
		nestedInternalAutomatedData = [];
		nestedInternalSentData = [];
		nestedInternalCompletedData = [];
		nestedInternalRemindersData = [];
		nestedInternalUnprocessedData = [];
		
		var curAutomatedTransactionCount;
		var curCompletedTransactionCount;
		var curSentInvitationCount;
		var curReminderSentCount;
		var curUnprocessedTransactionsCount;
		
		var prevAutomatedTransactionCount;
		var prevCompletedTransactionCount;
		var prevSentInvitationCount;
		var prevReminderSentCount;
		var prevUnprocessedTransactionsCount;

		if (isNaN(parseInt(currentWeekAutomatedTransactions[itr]))) {
			curAutomatedTransactionCount = 0;
		} else {
			curAutomatedTransactionCount = parseInt(currentWeekAutomatedTransactions[itr]);
		}
		
		if (isNaN(parseInt(pastWeekAutomatedTransactions[itr]))) {
			prevAutomatedTransactionCount = 0;
		} else {
			prevAutomatedTransactionCount = parseInt(pastWeekAutomatedTransactions[itr]);
		}

		if (isNaN(parseInt(currentWeekInvitationsSent[itr]))) {
			curSentInvitationCount = 0;
		} else {
			curSentInvitationCount = parseInt(currentWeekInvitationsSent[itr]);
		}
		
		if (isNaN(parseInt(pastWeekInvitationsSent[itr]))) {
			prevSentInvitationCount = 0;
		} else {
			prevSentInvitationCount = parseInt(pastWeekInvitationsSent[itr]);
		}
		
		if (isNaN(parseInt(currentWeekRemindersSent[itr]))) {
			curReminderSentCount = 0;
		} else {
			curReminderSentCount = parseInt(currentWeekRemindersSent[itr]);
		}
		
		if (isNaN(parseInt(pastWeekRemindersSent[itr]))) {
			prevReminderSentCount = 0;
		} else {
			prevReminderSentCount = parseInt(pastWeekRemindersSent[itr]);
		}
		
		if (isNaN(parseInt(currentWeekCompletedTransactions[itr]))) {
			curCompletedTransactionCount = 0;
		} else {
			curCompletedTransactionCount = parseInt(currentWeekCompletedTransactions[itr]);
		}
		
		if (isNaN(parseInt(pastWeekCompletedTransactions[itr]))) {
			prevCompletedTransactionCount = 0;
		} else {
			prevCompletedTransactionCount = parseInt(pastWeekCompletedTransactions[itr]);
		}
		
		if (isNaN(parseInt(currentWeekUnprocessedTransactions[itr]))) {
			curUnprocessedTransactionsCount = 0;
		} else {
			curUnprocessedTransactionsCount = parseInt(currentWeekUnprocessedTransactions[itr]);
		}
		
		if (isNaN(parseInt(pastWeekUnprocessedTransactions[itr]))) {
			prevUnprocessedTransactionsCount = 0;
		} else {
			prevUnprocessedTransactionsCount = parseInt(pastWeekUnprocessedTransactions[itr]);
		}
		
		nestedInternalAutomatedData.push(xAxisTimeSlots[itr], prevAutomatedTransactionCount,'Previous Week: '+prevAutomatedTransactionCount,curAutomatedTransactionCount);
		nestedInternalSentData.push(xAxisTimeSlots[itr], prevSentInvitationCount, 'Previous Week: '+prevSentInvitationCount, curSentInvitationCount);
		nestedInternalCompletedData.push(xAxisTimeSlots[itr], prevCompletedTransactionCount, 'Previous Week: '+prevCompletedTransactionCount, curCompletedTransactionCount);
		nestedInternalRemindersData.push(xAxisTimeSlots[itr], prevReminderSentCount, 'Previous Week: '+prevReminderSentCount,curReminderSentCount);
		nestedInternalUnprocessedData.push(xAxisTimeSlots[itr], prevUnprocessedTransactionsCount, 'Previous Week: '+prevUnprocessedTransactionsCount, curUnprocessedTransactionsCount);
		
		automatedData.push(nestedInternalAutomatedData);
		sentData.push(nestedInternalSentData);
		remindersData.push(nestedInternalRemindersData);
		completedData.push(nestedInternalCompletedData);
		unprocessedData.push(nestedInternalUnprocessedData);
	}
	
	var transGraphOption = getTransMonGraphOptions('gray');
	
	if(isEmptyData){
		modifyGraphContainerToGray('#'+graphDetails.autoGraphId);
		modifyGraphContainerToGray('#'+graphDetails.inviGraphId);
		modifyGraphContainerToGray('#'+graphDetails.remGraphId);
		modifyGraphContainerToGray('#'+graphDetails.unproGraphId);
		modifyGraphContainerToGray('#'+graphDetails.compGraphId);
		
		drawTransactionMonitorGraphs(automatedData, transGraphOption, graphDetails.autoGraphId);
		drawTransactionMonitorGraphs(sentData, transGraphOption, graphDetails.inviGraphId);
		drawTransactionMonitorGraphs(remindersData, transGraphOption, graphDetails.remGraphId);
		drawTransactionMonitorGraphs(unprocessedData, transGraphOption, graphDetails.unproGraphId);
		drawTransactionMonitorGraphs(completedData, transGraphOption, graphDetails.compGraphId);
	}else{
		
		if(isTransGraphEmpty(automatedData)){
			modifyGraphContainerToGray('#'+graphDetails.autoGraphId);
			transGraphOption = getTransMonGraphOptions('gray');
		}else{
			transGraphOption = getTransMonGraphOptions(graphDetails.autoStatus);
		}		
		drawTransactionMonitorGraphs(automatedData, transGraphOption, graphDetails.autoGraphId);
		
		if(isTransGraphEmpty(sentData)){
			modifyGraphContainerToGray('#'+graphDetails.inviGraphId);
			transGraphOption = getTransMonGraphOptions('gray');
		}else{
			transGraphOption = getTransMonGraphOptions(graphDetails.inviStatus);
		}
		drawTransactionMonitorGraphs(sentData, transGraphOption, graphDetails.inviGraphId);
		
		if(isTransGraphEmpty(remindersData)){
			modifyGraphContainerToGray('#'+graphDetails.remGraphId);
			transGraphOption = getTransMonGraphOptions('gray');
		}else{
			transGraphOption = getTransMonGraphOptions(graphDetails.remStatus);
		}
		drawTransactionMonitorGraphs(remindersData, transGraphOption, graphDetails.remGraphId);
		
		if(isTransGraphEmpty(unprocessedData)){
			modifyGraphContainerToGray('#'+graphDetails.unproGraphId);
			transGraphOption = getTransMonGraphOptions('gray');
		}else{
			transGraphOption = getTransMonGraphOptions(graphDetails.unproStatus);
		}
		drawTransactionMonitorGraphs(unprocessedData, transGraphOption, graphDetails.unproGraphId);
		
		if(isTransGraphEmpty(completedData)){
			modifyGraphContainerToGray('#'+graphDetails.compGraphId);
			transGraphOption = getTransMonGraphOptions('gray');
		}else{
			transGraphOption = getTransMonGraphOptions(graphDetails.compStatus);
		}
		drawTransactionMonitorGraphs(completedData, transGraphOption, graphDetails.compGraphId);
	}
}

function getTransMonGraphOptions(alert){
	if(alert == 'danger'){
		return dangerOptions;
	}else if(alert == 'warn'){
		return warnOptions;
	}else if(alert == 'gray'){
		return grayOptions;
	}else if(alert == 'normal'){
		return normalOptions;
	}
}

function modifyGraphContainerToGray(id){
	
	if($(id).parent().hasClass('trans-monitor-graph-col-danger')){
		$(id).parent().removeClass('trans-monitor-graph-col-danger');
	}
	if($(id).parent().hasClass('trans-monitor-graph-col-warn')){
		$(id).parent().removeClass('trans-monitor-graph-col-warn');
	}
	if($(id).parent().hasClass('trans-monitor-graph-col-normal')){
		$(id).parent().removeClass('trans-monitor-graph-col-normal');
	}
	
	$(id).parent().addClass('trans-monitor-graph-col-gray');
}

function isTransGraphEmpty(graphDataArray){
	var isGraphEmpty = true;
	for(var i=0;i<graphDataArray.length;i++){
		if(graphDataArray[i][1]>0 || graphDataArray[i][3]>0){
			isGraphEmpty =false;
			break;
		}
	}
	
	return isGraphEmpty;
}

function formatAllTimeSlots(dates){
	var monthText = dates[0].match(/[a-zA-Z]*/);
	monthText = monthText?monthText[0]:null;
	var xAxisData = new Array();
	var index = 0;
	for(i=0;i<dates.length;i++){
		if(i==0){
	  	xAxisData[i] = dates[i];
	  }else{
	  	var nextMonth = dates[i].match(/[a-zA-Z]*/);
	    nextMonth = nextMonth?nextMonth[0]:null;
	    if(monthText == nextMonth){
	    var d = parseInt(dates[i].replace ( /[^\d.]/g, '' ));
	      xAxisData[i]=d;
	    }else{
	    	xAxisData[i] = dates[i];
	      nextMonth = dates[i].match(/[a-zA-Z]*/);
	     monthText = nextMonth?nextMonth[0]:null;
	    }
	  }
	}
	return xAxisData;
}

function getSwearWords(companyId) {
	var payload = {
			  "companyId" : companyId
	};
	 $.ajax({
	 url : getLocationOrigin() + surveyUrl + "data/getSwearWords",
	 async:true,
	 type : "GET",
	 cache : false,
	 data : payload,
	 dataType : "JSON",
	 success : function(data) {
	 swearWords=JSON.parse(data);
	 },
	 error : function(e) {
	 if (e.status == 504) {
	 redirectToLoginPageOnSessionTimeOut(e.status);
	 return;
	 }
	 }
	 });
}


/*
 * Social monitor
 */

$(document).on('click','#soc-mon-stream-tab',function(e){
	e.stopPropagation();
	
	var streamTabClickDisabled = $('#soc-mon-stream-tab').data('disabled');
	if(streamTabClickDisabled){
		return;
	}
	
	$('#soc-mon-alerts-tab').data('disabled',false);
	$('#soc-mon-escalated-tab').data('disabled',false);
	$('#soc-mon-stream-tab').data('disabled',true);
	$('#soc-mon-resolved-tab').data('disabled',false);
	$('#soc-mon-trusted-tab').data('disabled',false);
	
	$('#soc-mon-stream-tab').addClass('soc-mon-stream-active');
	$('#stream-inactive').hide();
	$('#stream-active').show();
	
	$('#soc-mon-alerts-tab').removeClass('soc-mon-alert-active');
	$('#alert-inactive').show();
	$('#alert-active').hide();
	
	$('#soc-mon-escalated-tab').removeClass('soc-mon-esc-active');
	$('#esc-inactive').show();
	$('#esc-active').hide();
	
	$('#soc-mon-resolved-tab').removeClass('soc-mon-res-active');
	$('#res-inactive').show();
	$('#res-active').hide();
	
	$('#soc-mon-trusted-tab').removeClass('soc-mon-trust-active');
	$('#trust-inactive').show();
	$('#trust-active').hide();
	
	$('#stream-tabs').data('status','NEW');
	$('#stream-tabs').data('trusted-source', false);
	
	drawBulkMacroListDropdown(macrosForStream);
	var text = $('#search-post').val();
	
	getStreamPosts(0,'NEW',text);
	
});

$(document).on('click','#soc-mon-alerts-tab',function(e){
	e.stopPropagation();

	var streamTabClickDisabled = $('#soc-mon-alerts-tab').data('disabled');
	if(streamTabClickDisabled){
		return;
	}
	
	$('#soc-mon-alerts-tab').data('disabled',true);
	$('#soc-mon-escalated-tab').data('disabled',false);
	$('#soc-mon-stream-tab').data('disabled',false);
	$('#soc-mon-resolved-tab').data('disabled',false);
	$('#soc-mon-trusted-tab').data('disabled',false);
	
	$('#soc-mon-stream-tab').removeClass('soc-mon-stream-active');
	$('#stream-inactive').show();
	$('#stream-active').hide();
	
	$('#soc-mon-alerts-tab').addClass('soc-mon-alert-active');
	$('#alert-inactive').hide();
	$('#alert-active').show();
	
	$('#soc-mon-escalated-tab').removeClass('soc-mon-esc-active');
	$('#esc-inactive').show();
	$('#esc-active').hide();
	
	$('#soc-mon-resolved-tab').removeClass('soc-mon-res-active');
	$('#res-inactive').show();
	$('#res-active').hide();
	
	$('#soc-mon-trusted-tab').removeClass('soc-mon-trust-active');
	$('#trust-inactive').show();
	$('#trust-active').hide();
	
	$('#stream-tabs').data('status','ALERT');
	$('#stream-tabs').data('trusted-source', false);
	
	drawBulkMacroListDropdown(macrosForStream);
	
	var text = $('#search-post').val();
	
	getStreamPosts(0,'ALERT' ,text);
});

$(document).on('click','#soc-mon-escalated-tab',function(e){
	e.stopPropagation();
	
	var streamTabClickDisabled = $('#soc-mon-escalated-tab').data('disabled');
	if(streamTabClickDisabled){
		return;
	}
	
	$('#soc-mon-alerts-tab').data('disabled',false);
	$('#soc-mon-escalated-tab').data('disabled',true);
	$('#soc-mon-stream-tab').data('disabled',false);
	$('#soc-mon-resolved-tab').data('disabled',false);
	$('#soc-mon-trusted-tab').data('disabled',false);
	
	$('#soc-mon-stream-tab').removeClass('soc-mon-stream-active');
	$('#stream-inactive').show();
	$('#stream-active').hide();
	
	$('#soc-mon-alerts-tab').removeClass('soc-mon-alert-active');
	$('#alert-inactive').show();
	$('#alert-active').hide();
	
	$('#soc-mon-escalated-tab').addClass('soc-mon-esc-active');
	$('#esc-inactive').hide();
	$('#esc-active').show();
	
	$('#soc-mon-resolved-tab').removeClass('soc-mon-res-active');
	$('#res-inactive').show();
	$('#res-active').hide();
	
	$('#soc-mon-trusted-tab').removeClass('soc-mon-trust-active');
	$('#trust-inactive').show();
	$('#trust-active').hide();
	
	$('#stream-tabs').data('status','ESCALATED');
	$('#stream-tabs').data('trusted-source', false);
	
	drawBulkMacroListDropdown(macrosForStream);
	
	var text = $('#search-post').val();
	
	getStreamPosts(0,'ESCALATED',text);
	
});

$(document).on('click','#soc-mon-resolved-tab',function(e){
	e.stopPropagation();
	
	var streamTabClickDisabled = $('#soc-mon-resolved-tab').data('disabled');
	if(streamTabClickDisabled){
		return;
	}

	$('#soc-mon-alerts-tab').data('disabled',false);
	$('#soc-mon-escalated-tab').data('disabled',false);
	$('#soc-mon-stream-tab').data('disabled',false);
	$('#soc-mon-resolved-tab').data('disabled',true);
	$('#soc-mon-trusted-tab').data('disabled',false);
	
	$('#soc-mon-stream-tab').removeClass('soc-mon-stream-active');
	$('#stream-inactive').show();
	$('#stream-active').hide();
	
	$('#soc-mon-alerts-tab').removeClass('soc-mon-alert-active');
	$('#alert-inactive').show();
	$('#alert-active').hide();
	
	$('#soc-mon-escalated-tab').removeClass('soc-mon-esc-active');
	$('#esc-inactive').show();
	$('#esc-active').hide();
	
	$('#soc-mon-resolved-tab').addClass('soc-mon-res-active');
	$('#res-inactive').hide();
	$('#res-active').show();
	
	$('#soc-mon-trusted-tab').removeClass('soc-mon-trust-active');
	$('#trust-inactive').show();
	$('#trust-active').hide();
	
	$('#stream-tabs').data('status','RESOLVED');
	$('#stream-tabs').data('trusted-source', false);
	
	drawBulkMacroListDropdown(macrosForStream);
	
	var text = $('#search-post').val();
	
	getStreamPosts(0,'RESOLVED', text);
	
});

$(document).on('click','#soc-mon-trusted-tab',function(e){
	e.stopPropagation();
	
	var streamTabClickDisabled = $('#soc-mon-trusted-tab').data('disabled');
	if(streamTabClickDisabled){
		return;
	}

	$('#soc-mon-alerts-tab').data('disabled',false);
	$('#soc-mon-escalated-tab').data('disabled',false);
	$('#soc-mon-stream-tab').data('disabled',false);
	$('#soc-mon-resolved-tab').data('disabled',false);
	$('#soc-mon-trusted-tab').data('disabled',true)
	
	$('#soc-mon-stream-tab').removeClass('soc-mon-stream-active');
	$('#stream-inactive').show();
	$('#stream-active').hide();
	
	$('#soc-mon-alerts-tab').removeClass('soc-mon-alert-active');
	$('#alert-inactive').show();
	$('#alert-active').hide();
	
	$('#soc-mon-escalated-tab').removeClass('soc-mon-esc-active');
	$('#esc-inactive').show();
	$('#esc-active').hide();
	
	$('#soc-mon-resolved-tab').removeClass('soc-mon-res-active');
	$('#res-inactive').show();
	$('#res-active').hide();
	
	$('#soc-mon-trusted-tab').addClass('soc-mon-trust-active');
	$('#trust-inactive').hide();
	$('#trust-active').show();
	
	$('#stream-tabs').data('status',null);
	$('#stream-tabs').data('trusted-source', true);
	
	drawBulkMacroListDropdown(macrosForStream);
	
	var text = $('#search-post').val();
	
	getStreamPosts(0,null, text);
	
});

$(document).on('click','#stream-bulk-actions',function(e){
	e.stopPropagation();
	
	checkSocMonDropdowns(e);
	
	$('#stream-bulk-action-options').toggle();
	$('#chevron-down').toggle();
	$('#chevron-up').toggle();
});

$(document).on('click','#stream-bulk-edit',function(e){
	e.stopPropagation();
	
	var selectedPosts = $('#selected-post-ids').data('post-ids');
	if(selectedPosts.length == 0){
		$("#overlay-toast").html("Select at least 1 post to proceed.");
		showToast();
		
		return;
	}
	
	$('#bulk-options-popup').show();
	$('#bulk-options-popup').css('display','flex');
	$('#stream-bulk-action-options').toggle();
	$('#chevron-down').toggle();
	$('#chevron-up').toggle();
});

$(document).on('click','#dismiss-bulk-options',function(e){
	e.stopPropagation();
	$('#bulk-options-popup').hide();
});

$(document).on('click','#add-macro-status',function(e){
	e.stopPropagation();
	$('#add-macro-status-options').toggle();
	$('#macro-status-chevron-down').toggle();
	$('#macro-status-chevron-up').toggle();
});


$(document).on('click','#add-macro-alerts',function(e){
	e.stopPropagation();
	$('#add-macro-alerts-options').toggle();
	$('#macro-alerts-chevron-down').toggle();
	$('#macro-alerts-chevron-up').toggle();
	
	if ($('#add-macro-action-options').is(':visible')) {
		$('#add-macro-action-options').toggle();
		$('#macro-action-chevron-down').toggle();
		$('#macro-action-chevron-up').toggle();
	}
});

$(document).on('click','#add-macro-action',function(e){
	e.stopPropagation();
	$('#add-macro-action-options').toggle();
	$('#macro-action-chevron-down').toggle();
	$('#macro-action-chevron-up').toggle();
	
	if ($('#add-macro-alerts-options').is(':visible')) {
		$('#add-macro-alerts-options').toggle();
		$('#macro-alerts-chevron-down').toggle();
		$('#macro-alerts-chevron-up').toggle();
	}

});


$(document).on('click','#macro-status-dropdown',function(e){
	e.stopPropagation();
	$('#macro-status-options').toggle();
	$('#macro-page-chevron-down').toggle();
	$('#macro-page-chevron-up').toggle();
});

$(document).on('click','#macro-active-container',function(e){
	e.stopPropagation();
	$('#macro-active-container').addClass('macro-tabs-active');
	$('#macro-inactive-container').removeClass('macro-tabs-active');
	
	if($('#active-macros-list').children('.macro-item').length > 0){
		$('#active-macros-list').removeClass('hide');
		$('#empty-macros-list').addClass('hide');
	}else{
		$('#active-macros-list').addClass('hide');
		$('#empty-macros-list').removeClass('hide');
	}

	
	$('#inactive-macros-list').addClass('hide');
});

$(document).on('click','#macro-inactive-container',function(e){
	e.stopPropagation();
	$('#macro-active-container').removeClass('macro-tabs-active');
	$('#macro-inactive-container').addClass('macro-tabs-active');
	
	if($('#inactive-macros-list').children('.macro-item').length > 0){
		$('#inactive-macros-list').removeClass('hide');
		$('#empty-macros-list').addClass('hide');
	}else{
		$('#inactive-macros-list').addClass('hide');
		$('#empty-macros-list').removeClass('hide');
	}
	
	$('#active-macros-list').addClass('hide');
});

$(document).on('input','#macro-name',function(){
	if($('#macro-name').val()!= '' &&  $('#macro-name').val()!= undefined && $('#macro-name').val()!= null){
		$('#add-macro-save-inactive').hide();
		$('#add-macro-save-active').show();
		$('#add-macro-form').attr('data-state','new');
		$('#add-macro-form').attr('data-status','edited');
	}else{
		$('#add-macro-save-inactive').show();
		$('#add-macro-save-active').hide();
		$('#add-macro-form').attr('data-state','new');
		$('#add-macro-form').attr('data-status','new');
	}
});

$(document).on('click','#mon-type-dropdown',function(e){
	e.stopPropagation();
	$('#mon-type-options').toggle();
	$('#mon-type-chevron-down').toggle();
	$('#mon-type-chevron-up').toggle();	
	
	if($('#monitor-bulk-action-options').is(':visible')){
		$('#monitor-bulk-action-options').toggle();
		$('#monitor-chevron-down').toggle();
		$('#monitor-chevron-up').toggle();
	}
});

$(document).on('click','#add-mon-type-dropdown',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();	
	$('#add-mon-type-options').toggle();
	$('#add-mon-type-chevron-down').toggle();
	$('#add-mon-type-chevron-up').toggle();
	
	$('#add-mon-type-options').css('width',$('.add-mon-type-dropdown').css('width'));
	
	$('#add-mon-type-km').off();
	$('#add-mon-type-km').click(function(e){
		e.stopPropagation();
					
		if($('#add-keyword-mon-unchecked').hasClass('hide')){
			$('#add-keyword-mon-unchecked').removeClass('hide');
			$('#add-keyword-mon-checked').addClass('hide');
		}else{
			$('#add-keyword-mon-unchecked').addClass('hide');
			$('#add-keyword-mon-checked').removeClass('hide');
		}
		
		setMonitorType();
		
	});
	
	$('#add-mon-type-ga').off();
	$('#add-mon-type-ga').click(function(e){
		e.stopPropagation();
				
		if($('#add-google-alerts-mon-unchecked').hasClass('hide')){
			$('#add-google-alerts-mon-unchecked').removeClass('hide');
			$('#add-google-alerts-mon-checked').addClass('hide');
		}else{
			$('#add-google-alerts-mon-unchecked').addClass('hide');
			$('#add-google-alerts-mon-checked').removeClass('hide');
		}
		
		setMonitorType();
		
	});
	
});

function setMonitorType(){
	if($('#add-keyword-mon-unchecked').hasClass('hide') && $('#add-google-alerts-mon-unchecked').hasClass('hide')){
		
		$('#add-mon-type-select').attr('data-mon-type',0);
		$('#monitor-type').val(0);
		$('#add-mon-type-sel-txt').html('Keyword Monitor');
		$('.add-mon-type-dropdown').css('width','150px');
		
	}else if($('#add-keyword-mon-unchecked').hasClass('hide')){
		
		$('#add-mon-type-select').attr('data-mon-type',0);
		$('#monitor-type').val(0);
		$('#add-mon-type-sel-txt').html('Keyword Monitor');
		$('.add-mon-type-dropdown').css('width','150px');
		
	}else{
		$('#add-mon-type-select').attr('data-mon-type',999);
		$('#monitor-type').val(999);
		$('#add-mon-type-sel-txt').html('None');
		$('.add-mon-type-dropdown').css('width','150px');
	}
	
	$('#add-mon-type-options').css('width',$('.add-mon-type-dropdown').css('width'));
}

$(document).on('click','#mon-type-keyword-mon',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	if($('#keyword-mon-unchecked').hasClass('hide')){
		$('#keyword-mon-unchecked').removeClass('hide');
		$('#keyword-mon-checked').addClass('hide');
		getMonitors();	
	}else{
		$('#keyword-mon-unchecked').addClass('hide');
		$('#keyword-mon-checked').removeClass('hide');
		getMonitors();
	}
});

$(document).on('click','#mon-type-google-alerts',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	if($('#google-alerts-mon-unchecked').hasClass('hide')){
		$('#google-alerts-mon-unchecked').removeClass('hide');
		$('#google-alerts-mon-checked').addClass('hide');
		getMonitors();
	}else{
		$('#google-alerts-mon-unchecked').addClass('hide');
		$('#google-alerts-mon-checked').removeClass('hide');
		getMonitors();
	}
});


$(document).on('click','#stream-usr-selection',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	checkSocMonDropdowns(e);
	
	$('#stream-usr-dropdown-options').toggle();
	$('#usr-chevron-down').toggle();
	$('#usr-chevron-up').toggle();
	
});

$(document).on('click','#stream-seg-selection',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	checkSocMonDropdowns(e);
	
	$('#stream-seg-dropdown-options').toggle();
	$('#seg-chevron-down').toggle();
	$('#seg-chevron-up').toggle();

});

$(document).on('click','#stream-feed-selection',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	checkSocMonDropdowns(e);
	
	$('#stream-feed-dropdown-options').toggle();
	$('#feed-chevron-down').toggle();
	$('#feed-chevron-up').toggle();
	
});

$(document).on('click','#stream-usr-dropdown-options',function(e){
	e.stopPropagation();
	e.stopImmediatePropagation();
	e.preventDefault();
});

$(document).on('click','#stream-seg-dropdown-options',function(e){
	e.stopPropagation();
	e.stopImmediatePropagation();
	e.preventDefault();
});

$(document).on('click','#stream-feed-dropdown-options',function(e){
	e.stopPropagation();
	e.stopImmediatePropagation();
	e.preventDefault();
});

function showAddMonitorPopup(){
	$('#add-mon-popup').removeClass('hide');
	
	$(document).on('blur','#monitor-keyphrase',function(e){
		e.stopPropagation();
		if($('#monitor-keyphrase').val() != '' && $('#monitor-keyphrase').val() != undefined && $('#monitor-keyphrase').val() != null){
			$('#add-mon-save-active').show();
			$('#add-mon-save-inactive').hide();
		}else{
			$('#add-mon-save-active').hide();
			$('#add-mon-save-inactive').show();
		}
	});
}

function hideAddMonitorPopup(){
	$('#monitor-keyphrase').val('');
	$('#add-mon-type-select').attr('data-mon-type',0);
	$('#monitor-type').val(0);
	$('#add-mon-type-sel-txt').html('Keyword Monitor');
	$('.add-mon-type-dropdown').css('width','auto');
	$('#add-mon-type-options').css('width',$('.add-mon-type-dropdown').css('width'));
	
	if($('#add-keyword-mon-checked').hasClass('hide')){
		$('#add-keyword-mon-unchecked').addClass('hide');
		$('#add-keyword-mon-checked').removeClass('hide');
	}
	
	if($('#add-google-alerts-mon-checked').hasClass('hide')){
		$('#add-google-alerts-mon-unchecked').addClass('hide');
		$('#add-google-alerts-mon-checked').removeClass('hide');
	}
	
	$('#add-mon-popup').addClass('hide');
}

function drawMonitorList(monitorData){
	
	var monitorDataInput = '<input type="hidden" id="selectedMonitors" data-idList= val="">';
	var monListHeader = '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 clearfix mon-type-hdr"  >'
					+'<img id="edit-mon-unchecked" src="resources/images/check-no.png"  class="float-left mon-type-checkbox">'
					+'<img id="edit-mon-checked" src="resources/images/check-yes.png"  class="hide float-left mon-type-checkbox">'
					+'<div class="col-lg-8 col-md-8 col-sm-8 col-xs-8 soc-mon-txt-bold">Keyphrase</div>'
					+'<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 mon-type-hdr-txt">Monitor Type</div></div>';
	
	$('#monitor-list-container').html(monitorDataInput+monListHeader);
	
	var emptyMonitorsDiv = '<div id="empty-monitors" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 clearfix monitors-empty-div hide">'
					+'<span class="incomplete-trans-span monitors-empty-span">No monitors found</span></div>';
	$('#monitor-list-container').append(emptyMonitorsDiv);
	
	var selectedMonitors = [];
	$('#selectedMonitors').data('idList',selectedMonitors);
	
	if(monitorData==null || monitorData.length <= 0 || monitorData==undefined){
		$('#empty-monitors').show();
		return;
	}else{
		$('#empty-monitors').hide();
	}
	
	for(var i=0;i<monitorData.length;i++){
		
		if(monitorData[i].status == 1){
			var monitorId = monitorData[i].id;
			var monitorType = (monitorData[i].monitorType == KEYWORD_MONITOR)?'Keyword Monitor':((monitorData[i].monitorType == GOOGLE_ALERTS)?'Google Alerts':'Unknown');
			var container = 'mon-type-gray-container';
			
			if(i%2==0){
				container = 'mon-type-gray-container';
			}else{
				container = 'mon-type-white-container';
			}
			
			var monListRow = '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 clearfix '+container+'"  data-checked=false>'
						+'<img id="edit-mon-unchecked-'+monitorId+'" data-id='+monitorId+' src="resources/images/check-no.png"  class="float-left mon-type-checkbox mon-type-checked">'
						+'<img id="edit-mon-checked-'+monitorId+'" data-id='+monitorId+' src="resources/images/check-yes.png"  class="hide float-left mon-type-checkbox mon-type-checked">'
						+'<div class="col-lg-8 col-md-8 col-sm-8 col-xs-8 mon-type-keyphrase">'+monitorData[i].phrase+'</div>'
						+'<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 mon-type-tbl-txt">'+monitorType+'</div></div>';
			
			$('#monitor-list-container').append(monListRow);
		}	
	}
	
	$('.mon-type-checked').unbind('click');
	$('.mon-type-checked').bind('click',function(){
		var id=$(this).data('id');
		
		var selectedIds = $('#selectedMonitors').data('idList');
		var index = isInArray(id,selectedIds);
		
		var checked = $(this).parent().attr('data-checked');
		if(checked == false || checked == 'false'){
			$(this).parent().attr('data-checked',true);
			$('#edit-mon-checked-'+id).show();
			
			if(index == -1){
				selectedIds.push(id);
			}
			
		}else{
			$(this).parent().attr('data-checked',false);
			$('#edit-mon-unchecked-'+id).show();
			
			if(index > -1){
				selectedIds.splice(index,1);
			}
		}
		
		$('#selectedMonitors').data('idList',selectedIds);
		$(this).hide();
	});
	
	$('#edit-mon-unchecked').unbind('click');
	$('#edit-mon-unchecked').bind('click', function() {
		
		var selectedIds = [];
		
		$('#edit-mon-unchecked').toggle();
		$('#edit-mon-checked').toggle();
		for(var i=0;i<monitorData.length;i++){
			$('#edit-mon-unchecked-'+monitorData[i].id).hide();
			$('#edit-mon-checked-'+monitorData[i].id).show();
			$('#edit-mon-unchecked-'+monitorData[i].id).parent().attr('data-checked',true);
			selectedIds.push(monitorData[i].id);
		}
		
		$('#selectedMonitors').data('idList',selectedIds);
	});
	
	$('#edit-mon-checked').unbind('click');
	$('#edit-mon-checked').bind('click', function() {
		
		var selectedIds = [];
		
		$('#edit-mon-unchecked').toggle();
		$('#edit-mon-checked').toggle();
		for(var i=0;i<monitorData.length;i++){
			$('#edit-mon-checked-'+monitorData[i].id).hide();
			$('#edit-mon-unchecked-'+monitorData[i].id).show();
			$('#edit-mon-unchecked-'+monitorData[i].id).parent().attr('data-checked',false);
		}
		
		$('#selectedMonitors').data('idList',selectedIds);
	});
}

$(document).on('input','#monitor-keyphrase',function(){
	$('#add-monitor-form').attr('data-status','edited');
	$('#add-mon-save-inactive').addClass('hide');
	$('#add-mon-save-active').removeClass('hide');
});

function addMonitor(){
	
	if($('#add-keyword-mon-checked').hasClass('hide') && $('#add-google-alerts-mon-checked').hasClass('hide')){
		
		$("#overlay-toast").html("Please select a Monitor type.");
		showToast();
		return;
	}
	
	var keyPhrase = $('#monitor-keyphrase').val();
	if ($('#add-monitor-form').attr('data-status') == 'edited') {
		if(keyPhrase == '' || keyPhrase == null || keyPhrase == undefined){
			
			$("#overlay-toast").html('Please enter a keyphrase');
			showToast();
			$('#add-mon-save-inactive').removeClass('hide');
			$('#add-mon-save-active').addClass('hide');
			return;
			
		}else{
			
			var url = './addmonitorkeyword.do';
			callAjaxFormSubmit(url, function(data) {
				var map = $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();

				if (map.status == "success") {
					$('#add-monitor-form').attr('data-status', 'new');
					$("#overlay-toast").html("Successfully Added Monitor");
					
					var monitorData = JSON.parse(map.keywords);
					drawMonitorList(monitorData);
					
					hideAddMonitorPopup();
					showToast();
				} else {
					$('#add-monitor-form').attr('data-status', 'edited');
					$("#overlay-toast").html("Failed to add new Monitor. Please Try again");
					showToast();
				}
			}, 'add-monitor-form', '#add-mon-save-active');
		}
	}else{
		
		$("#overlay-toast").html('Please enter a keyphrase');
		showToast();
		$('#add-mon-save-inactive').removeClass('hide');
		$('#add-mon-save-active').addClass('hide');
		return;
	}
}

var lastgetMonitorsRequestToDelete = null;
function getMonitors(text){
	var monitorType = null;
	if(!$('#keyword-mon-checked').hasClass('hide') && !$('#google-alerts-mon-checked').hasClass('hide')){
		monitorType=null;
	}else if(!$('#keyword-mon-checked').hasClass('hide')){
		monitorType=KEYWORD_MONITOR;
	}else if(!$('#google-alerts-mon-checked').hasClass('hide')){
		monitorType=GOOGLE_ALERTS;
	}else{
		monitorType='NONE';
	}
	
	var startIndex = 0;
	var batchSize = -1;
	
	if(text == undefined || text == null){
		text = '';
	}
	
	var payload = {
			"startIndex" : startIndex,
			"batchSize" : batchSize,
			"monitorType" : monitorType,
			"text" : text
	}
	
	lastgetMonitorsRequestToDelete = $.ajax({
		url : "/getmonitorslistbytype.do",
		type : "GET",
		data : payload,
		cache : false,
		dataType : "json",
		beforeSend : function()    {           
	        if(lastgetMonitorsRequestToDelete != null) {
	        	lastgetMonitorsRequestToDelete.abort();
	        }
	    },
		success : function(response) {
			var monitorData = response.filterKeywords;
			
			drawMonitorList(monitorData);
		},
		error : function(e){
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#empty-monitors').removeClass('hide');
			$('#overlay-toast').html('Unable to fetch monitors.');
			showToast();
		}
	});
}

function drawMacroList(macroList){
	
	var macroDiv = '';
	$('#active-macros-list').html('');
	$('#inactive-macros-list').html('');
	
	for(var i=0;i<macroList.length;i++){
		var macroId = macroList[i].macroId;
		var macroName=macroList[i].macroName;
		var description=macroList[i].description;
		var actions=macroList[i].actions;
		var active=macroList[i].active;
		var usage=macroList[i].count;
		var createdOn=macroList[i].modifiedOn;
		var last7DaysMacroCount = macroList[i].last7DaysMacroCount;
		
		macroDiv ='<div id="'+macroId+'" class="dash-stats-wrapper bord-bot-dc clearfix macro-list-div macro-item cursor-pointer">'
					+'<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 macro-list-item">'+macroName+'</div>'
					+'<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-list-item-usage">'+last7DaysMacroCount+'</div>'
					+'<input type="hidden" id="macro-'+macroId+'" name="monitor-type" data-macroId="'+macroId+'" data-macro-name="'+macroName+'" data-description="'+description
					+'" data-alert="'+actions.socialFeedStatus+' data-action-type="'+actions.textActionType+'" data-action-text="'+actions.text+'" data-active='+active+' data-last-used='+last7DaysMacroCount+' data-usage='+usage+' data-last-updated="'+createdOn+'" >'
					+'</div>';
		
		if(active==true || active=='true'){
			$('#active-macros-list').append(macroDiv);
		}else if(active==false || active=='false'){
			$('#inactive-macros-list').append(macroDiv);
		}
	}
	
	$('.macro-item').unbind('click');
	$('.macro-item').bind('click', function() {
		
	var macroId = $(this).attr('id');
	
	var macroData ={
		 "macroName" : $('#macro-'+macroId).attr('data-macro-name'),
		 "description" : $('#macro-'+macroId).attr('data-description'),
		 "alert" : $('#macro-'+macroId).attr('data-alert'),
		 "actionType" : $('#macro-'+macroId).attr('data-action-type'),
		 "actionText" : $('#macro-'+macroId).attr('data-action-text'),
		 "active" : $('#macro-'+macroId).attr('data-active'),
		 "lastUpdated" : $('#macro-'+macroId).attr('data-last-updated'),
		 "last7DaysMacroCount" : $('#macro-'+macroId).attr('data-last-used')
		};
		
		showAddMacroPage('./showsocialmonitoraddmacropage.do',macroId,macroData);
	});
}

function showAddMacroPage(url,macroId,macroData){
	closeMoblieScreenMenu();
	saveState(url);
	
	$.ajax({
		url : url,
		type : "GET",
		dataType : "html",
		async : true,
		cache : false,
		success : showMainContentCallBack,
		complete: function(){
			drawMacroData(macroId,macroData);
			hideOverlay();		
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

function drawMacroData(macroId,macroData){
	var macroName= macroData.macroName;
	var description= macroData.description;
	var alert= macroData.alert;
	var actionType= macroData.actionType;
	var actionText= macroData.actionText;
	var active= macroData.active;
	var usage= macroData.last7DaysMacroCount;
	var lastUpdatedStr= macroData.lastUpdated;
	
	var lastUpdated = parseInt(lastUpdatedStr);
	var lastUpdatedDate = new Date(lastUpdated);
	
	var monthName=new Array("January","February","March","April","May","June","July","August","September","October","November","December");
	var lastUpdatedDateStr = monthName[lastUpdatedDate.getMonth()]+' '+(lastUpdatedDate.getDate()%10!=lastUpdatedDate.getDate()?lastUpdatedDate.getDate():('0'+lastUpdatedDate.getDate()))+', '+lastUpdatedDate.getFullYear()
							+' at '+(lastUpdatedDate.getHours()%10!=lastUpdatedDate.getHours()?lastUpdatedDate.getHours():('0'+lastUpdatedDate.getHours()))+':'
							+(lastUpdatedDate.getMinutes()%10!=lastUpdatedDate.getMinutes()?lastUpdatedDate.getMinutes():('0'+lastUpdatedDate.getMinutes()))+':'
							+(lastUpdatedDate.getSeconds()%10!=lastUpdatedDate.getSeconds()?lastUpdatedDate.getSeconds():('0'+lastUpdatedDate.getSeconds()));
	$('#macro-name-hdr').html(macroName);
	$('#macro-updated-date').html("Last updated "+lastUpdatedDateStr);
	$('#macro-id').val(macroId);
	$('#macro-usage').val(usage);
	$('#macro-name').text(macroName);
	$('#macro-description').text(description);
	
	$('#macro-status').val(active);
	
	$('.add-mac-radio').each(function() {
	    $(this).removeClass('macro-radio');
	    $(this).children().hide();
	    $(this).addClass('macro-radio-outer');
	    $(this).css("cursor","pointer");
	});
	
	if(active == true || active == 'true'){
		
		$('#add-mac-active-radio').removeClass('macro-radio');
		$('#add-mac-active-radio').children().show();
		$('#add-mac-active-radio').css("cursor","default");
		
	}else if(active == false || active == 'false'){
		
		$('#add-mac-inactive-radio').removeClass('macro-radio');
		$('#add-mac-inactive-radio').children().show();
		$('#add-mac-inactive-radio').css("cursor","default");
		
	}
	
	if(alert == 'NEW'){
			$('#macro-alert').val(0);
			$('#macro-alert-text').html('Unflag');
	}else if(alert == 'ALERT'){
		$('#macro-alert').val(1);
		$('#macro-alert-text').html('Flag');
	}else if(alert == 'ESCALATED'){
		$('#macro-alert').val(2);
		$('#macro-alert-text').html('Escalate');
	}else if(alert == 'RESOLVED'){
		$('#macro-alert').val(3);
		$('#macro-alert-text').html('Resolve');
	}else{
		$('#macro-alert').val(0);
		$('#macro-alert-text').html('Alerts');
	}
	
	$('#macro-action-type').val(actionType);
	if(actionType == 'PRIVATE_NOTE'){
		$('#macro-action-type-text').html('Private Note');
	}else{
		$('#macro-action-type-text').html('Send Mail');
	}
	
	$('#macro-action-text').text(actionText);
	
	$('#add-macro-form').attr('data-state','edited');
	$('#add-macro-form').attr('data-status', 'new');
	
	$('#add-macro-save-inactive').removeClass('hide');
	$('#add-macro-save-active').addClass('hide');
}

function getMacros(text){
	
	if(text == undefined || text == null){
		text = '';
	}
	
	var payload = {
		"text" : text
	}
	
	$.ajax({
		url : "/getmacrosbycompanyid.do",
		type : "GET",
		data : payload,
		cache : false,
		dataType : "json",
		success : function(response) {
			var macroList = response;
			drawMacroList(macroList);
		},
		error : function(e){
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html('Unable to fetch macros.');
			showToast();
		}
	});
}

function addMacro(){
	
	var macroName = $('#macro-name').val();
	if ($('#add-macro-form').attr('data-status') == 'edited') {
		if(macroName == '' || macroName == null || macroName == undefined){
			
			$("#overlay-toast").html('Please enter a Macro Name');
			showToast();
			$('#add-macro-save-inactive').removeClass('hide');
			$('#add-macro-save-active').addClass('hide');
			return;
			
		}else{
			
			var alertSelected = parseInt($('#macro-alert').val());
			var commentText = $('#macro-action-text').val();
			
			if(alertSelected == 2){
				if(commentText == undefined || commentText == null || commentText == ''){
					$("#overlay-toast").html("Please enter some text for Mail/Private note. Escalation not allowed without a comment for the user");
					showToast();
					
					return;
				}
			}else if(alertSelected == 3){
				if(commentText == undefined || commentText == null || commentText == ''){
					$("#overlay-toast").html("Please enter some text for Mail/Private note. Resolution not allowed without a comment for the user");
					showToast();
					
					return;
				}
			}
			
			var url = './updatemacro.do';
			callAjaxFormSubmit(url, function(data) {
				var map = $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();

				if (map.status == "success") {
					$('#add-macro-form').attr('data-status', 'new');
					
					var dataState= $('#add-macro-form').attr('data-state');
					
					if(dataState == 'new'){
						$("#overlay-toast").html("Successfully Added Macro");
					}else{
						$("#overlay-toast").html("Successfully Updated Macro");
					}
					
					$('#add-macro-form').attr('data-state','new');
					
					showToast();
					
					$('#macro-name-hdr').html('Add Macro');
					$('#macro-updated-date').html('A macro is a prepared response or action that is used to manage posts.');
					$('#macro-id').val('');
					$('#macro-usage').val(0);
					$('#macro-name').val('');
					$('#macro-description').val('');
					$('#macro-status').val('true');
					$('#macro-status-text').html('Status');
					$('#macro-alert').val(0);
					$('#macro-alert-text').html('Alerts');
					$('#macro-action-type').val('PRIVATE_NOTE');
					$('#macro-action-type-text').html('Private Note');
					
					showMainContent('./showsocialmonitormacropage.do');
				} else {
					$('#add-monitor-form').attr('data-status', 'edited');
					$("#overlay-toast").html("Failed to add new Macro. Please Try again");
					showToast();
				}
			}, 'add-macro-form', '#add-macro-save-active');
		}
	}else{
		
		$("#overlay-toast").html('Please enter a Macro Name');
		showToast();
		$('#add-macro-save-inactive').removeClass('hide');
		$('#add-macro-save-active').addClass('hide');
		return;
	}
}

$(document).on('click','.add-mac-radio',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var macroStatus = $('#macro-status').val();
	var isChanged = false;
	
	$('.add-mac-radio').each(function() {
	    $(this).removeClass('macro-radio');
	    $(this).children().hide();
	    $(this).addClass('macro-radio-outer');
	    $(this).css("cursor","pointer");
	});
	
	$(this).removeClass('macro-radio');
	$(this).children().show();
	$(this).css("cursor","default");
	
	var radioId = $(this).attr('id');
	
	if(radioId == 'add-mac-active-radio'){
		if(macroStatus != 'true' || macroStatus != true){
			isChanged=true;
		}
		$('#macro-status').val(true);
	}else if(radioId == 'add-mac-inactive-radio'){
		if(macroStatus != 'false' || macroStatus != false){
			isChanged=true;
		}
		
		$('#macro-status').val(false);
	}
	
	if(isChanged){
		if($('#macro-name').val()!= '' &&  $('#macro-name').val()!= undefined && $('#macro-name').val()!= null){
			$('#add-macro-form').attr('data-status', 'edited');
			$('#add-macro-save-inactive').addClass('hide');
			$('#add-macro-save-active').removeClass('hide');
		}
	}
});

$(document).on('click','#macro-status-active',function(){
	$('#macro-status').val('true');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('click','#macro-status-inactive',function(){
	$('#macro-status').val('false');
	$('#macro-status-text').html('Inactive');
	$('#add-macro-status-options').addClass('hide');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('click','#macro-unflag',function(){
	$('#macro-alert').val(0);
	$('#macro-alert-text').html('Unflag');
	$('#add-macro-alerts-options').addClass('hide');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('click','#macro-flag',function(){
	$('#macro-alert').val(1);
	$('#macro-alert-text').html('Flag');
	$('#add-macro-alerts-options').addClass('hide');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('click','#macro-esc',function(){
	$('#macro-alert').val(2);
	$('#macro-alert-text').html('Escalate');
	$('#add-macro-alerts-options').addClass('hide');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('click','#macro-res',function(){
	$('#macro-alert').val(3);
	$('#macro-alert-text').html('Resolve');
	$('#add-macro-alerts-options').addClass('hide');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('click','#macro-pr-note',function(){
	$('#macro-action-type').val('PRIVATE_NOTE');
	$('#macro-action-type-text').html('Private Note');
	$('#add-macro-action-options').addClass('hide');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('click','#macro-sn-mail',function(){
	$('#macro-action-type').val('SEND_EMAIL');
	$('#macro-action-type-text').html('Send Mail');
	$('#add-macro-action-options').addClass('hide');
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('input','#macro-description',function(){
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

$(document).on('input','#macro-action-text',function(){
	var macroName = $('#macro-name').html();
	if(macroName != '' || macroName != null || macroName != undefined){
		$('#add-macro-form').attr('data-status', 'edited');
		$('#add-macro-save-inactive').addClass('hide');
		$('#add-macro-save-active').removeClass('hide');
	}
});

function getJspData(url) {
	callAjaxGET(url, fetchJspData, false);
}

function fetchJspData(data){
	jspData = data;
}

var streamActionContainer;
var dupContainer;

var lastgetStreamPostRequestToDelete=null;
function getStreamPosts(startIndex,status,text){
	
	var batchSize = SOCIAL_MONITOR_PAGE_SIZE;
	showDashOverlay('#stream-dash');
	if(status == undefined){
		status = 'none';
	}
	
	var fromTrustedSource = false;
	if($('#stream-tabs').data('trusted-source') == true){
		fromTrustedSource = true;
	}
	
	if(text == null || text == undefined){
		text = '';
	}
	
	$('#seg-reg-data').val($('#segment-data').data('regionIds'));
	$('#seg-bra-data').val($('#segment-data').data('branchIds'));
	$('#usr-data').val($('#usr-list-data').data('userIds'));
	$('#feed-data').val($('#feed-data').data('feeds'));
	
	var regionIds = $('#seg-reg-data').val();
	var branchIds = $('#seg-bra-data').val();
	var userIds = $('#usr-data').val();
	var companyId = $('#segment-data').data('companyId');
	var feeds = $('#feed-data').val();
	
	/*if(feeds == '' || feeds == null || feeds == undefined){
		var startFlag = true;
		$.each($('.feed-unchecked'), function( index, value ) {
			  if(!$(this).hasClass('hide')){
				  startFlag = false;
			  }
		});
		
		if(startFlag){
			feeds = "FACEBOOK,TWITTER,INSTAGRAM";
		}
	}*/
	
	var socMonOnLoad = $('#stream-tabs').attr('data-socMonOnLoad');
	$('#stream-tabs').attr('data-socMonOnLoad',false);
	
	var payload = {
		"startIndex":startIndex,
		"batchSize":batchSize,
		"status":status,
		"company":companyId,
		"region":regionIds,
		"branch":branchIds,
		"user":userIds,
		"feeds": feeds,
		"text" : text,
		"fromTrustedSource": fromTrustedSource,
		"socMonOnLoad": socMonOnLoad
	};
	
	lastgetStreamPostRequestToDelete = $.ajax({
		url : "/getsocialpostsforstream.do",
		type : "POST",
		data: payload,
		cache : false,
		dataType : "json",
		beforeSend : function()    {           
	        if(lastgetStreamPostRequestToDelete != null) {
	        	lastgetStreamPostRequestToDelete.abort();
	        }
	    },
		success : function(response) {
			var streamPostList = response.socialMonitorFeedData;
			drawPaginationForSocialMonitor(response.count,startIndex,batchSize);
			
			if(streamPostList != undefined && streamPostList != null){
				if(streamPostList.length > 0){
					$('#empty-stream').addClass('hide');
					$('#stream-posts').removeClass('hide');
					drawStreamPage(streamPostList);
				}else{
					$('#empty-stream').removeClass('hide');
					$('#stream-posts').addClass('hide');
				}
			}else{
				$('#empty-stream').removeClass('hide');
				$('#stream-posts').addClass('hide');
			}
			
			$('#stream-pagination').data('startIndex',startIndex);
			$('#stream-pagination').data('count',response.count);
			$('#stream-pagination-bottom').data('startIndex',startIndex);
			$('#stream-pagination-bottom').data('count',response.count);
			
		},
		complete: function(){
			hideDashOverlay('#stream-dash');
		},
		error : function(e){
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			
			if(e.statusText == 'abort'){
				return;
			}	
			
			hideDashOverlay('#stream-dash');
			$('#empty-stream').removeClass('hide');
			$('#stream-posts').addClass('hide');
			$('#overlay-toast').html('Unable to fetch posts.');
			showToast();
		}
	});
}

var macrosForStream;
function getMacrosForStream(){
	
	$.ajax({
		url : "/getmacrosbycompanyid.do",
		type : "GET",
		async: false,
		dataType : "json",
		success : function(response) {
			macrosForStream = response;
			drawBulkMacroListDropdown(macrosForStream);
		},
		error : function(e){
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html('Unable to fetch macros.');
			showToast();
		}
	});
}

function drawPaginationForSocialMonitor(count,startIndex,batchSize){
	if(count <= (batchSize+startIndex)){
		$('#stream-next-page-active').addClass('hide');
		$('#stream-next-page').removeClass('hide');
		$('#stream-next-page-active-bottom').addClass('hide');
		$('#stream-next-page-bottom').removeClass('hide');
	}else{
		$('#stream-next-page-active').removeClass('hide');
		$('#stream-next-page').addClass('hide');
		$('#stream-next-page-active-bottom').removeClass('hide');
		$('#stream-next-page-bottom').addClass('hide');
	}
	
	if(startIndex < count-(count%SOCIAL_MONITOR_PAGE_SIZE) && count > batchSize){
		$('#stream-end-page-active').removeClass('hide');
		$('#stream-end-page').addClass('hide');
		$('#stream-end-page-active-bottom').removeClass('hide');
		$('#stream-end-page-bottom').addClass('hide');
	}else{
		$('#stream-end-page-active').addClass('hide');
		$('#stream-end-page').removeClass('hide');
		$('#stream-end-page-active-bottom').addClass('hide');
		$('#stream-end-page-bottom').removeClass('hide');
	}
	
	if(startIndex < batchSize){
		$('#stream-prev-page-active').addClass('hide');
		$('#stream-prev-page').removeClass('hide');
		$('#stream-prev-page-active-bottom').addClass('hide');
		$('#stream-prev-page-bottom').removeClass('hide');
	}else{
		$('#stream-prev-page').addClass('hide');
		$('#stream-prev-page-active').removeClass('hide');
		$('#stream-prev-page-bottom').addClass('hide');
		$('#stream-prev-page-active-bottom').removeClass('hide');
	}
	
	if(startIndex == 0){
		$('#stream-start-page-active').addClass('hide');
		$('#stream-start-page').removeClass('hide');
		$('#stream-start-page-active-bottom').addClass('hide');
		$('#stream-start-page-bottom').removeClass('hide');
	}else{
		$('#stream-start-page').addClass('hide');
		$('#stream-start-page-active').removeClass('hide');
		$('#stream-start-page-bottom').addClass('hide');
		$('#stream-start-page-active-bottom').removeClass('hide');			
	}
	
	
	$('#stream-item-count').html(count);
	$('#stream-item-count-bottom').html(count);
	var pageCount = Math.ceil(count/SOCIAL_MONITOR_PAGE_SIZE);
	var pageNo = (startIndex/SOCIAL_MONITOR_PAGE_SIZE)+1;
	$('#stream-page-count').html(pageCount);
	$('#stream-page-no').html(pageNo);
	$('#stream-page-count-bottom').html(pageCount);
	$('#stream-page-no-bottom').html(pageNo);
	$('#sel-page-soc-mon').val(pageNo);
	$('#sel-page-soc-mon-bottom').val(pageNo);
	
}

$(document).on('click','#stream-start-page-active',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('click','#stream-prev-page-active',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();	

	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex') - SOCIAL_MONITOR_PAGE_SIZE;
	var text = $('#search-post').val();
	
	getStreamPosts(startIndex, status, text);
});

$(document).on('click','#stream-next-page-active',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();	

	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex') + SOCIAL_MONITOR_PAGE_SIZE;
	var text = $('#search-post').val();
	
	getStreamPosts(startIndex, status, text);
});

$(document).on('click','#stream-end-page-active',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();	

	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var pageNo = (startIndex/SOCIAL_MONITOR_PAGE_SIZE)+1;
	var count = $('#stream-pagination').data('count');
	startIndex = count-(count%SOCIAL_MONITOR_PAGE_SIZE);
	if(count%SOCIAL_MONITOR_PAGE_SIZE == 0){
		startIndex-=SOCIAL_MONITOR_PAGE_SIZE;
	}
	var text = $('#search-post').val();
	
	getStreamPosts(startIndex, status, text);

});

$(document).on('click','#stream-start-page-active-bottom',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('click','#stream-prev-page-active-bottom',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();	

	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex') - SOCIAL_MONITOR_PAGE_SIZE;
	var text = $('#search-post').val();
	
	getStreamPosts(startIndex, status, text);

});

$(document).on('click','#stream-next-page-active-bottom',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();	

	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex') + SOCIAL_MONITOR_PAGE_SIZE;
	var text = $('#search-post').val();
	
	getStreamPosts(startIndex, status, text);
});

$(document).on('click','#stream-end-page-active-bottom',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();	

	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var pageNo = (startIndex/SOCIAL_MONITOR_PAGE_SIZE)+1;
	var count = $('#stream-pagination').data('count');
	startIndex = count-(count%SOCIAL_MONITOR_PAGE_SIZE);
	if(count%SOCIAL_MONITOR_PAGE_SIZE == 0){
		startIndex-=SOCIAL_MONITOR_PAGE_SIZE;
	}
	var text = $('#search-post').val();
	
	getStreamPosts(startIndex, status,text);
});

function drawStreamPage(streamPostList){
	$('#stream-posts').html('');
	var macroList = macrosForStream;
	
	getJspData('./getstreamcontainer.do');
	var streamContainer = jspData;
	
	getJspData('./getdupcontainer.do');
	dupContainer = jspData;
	
	getJspData('./getstreamactioncontainer.do');
	streamActionContainer = jspData;
	
	for(var i=0;i<streamPostList.length;i++){
		var postId = streamPostList[i].postId;
		$('#stream-posts').append(streamContainer);
	
		$('#stream-post-cont').attr('id','stream-post-cont-'+postId);
		$('#stream-post-details-cont').attr('id','stream-post-details-cont-'+postId);
		$('#stream-post-icn-cont').attr('id','stream-post-icn-cont-'+postId);
		$('#action-form-container').attr('id','action-form-container'+postId);
		
		$('#stream-post-icn-cont-'+postId).find('.post-id-details').attr('data-post-id',postId);
		
		if(i%2 != 0){
			$('#stream-post-cont-'+postId).addClass('stream-container-gray');
		}
		
		var status = streamPostList[i].status;
		
		if(status=='NEW'){
				$('#stream-post-icn-cont-'+postId).find('.stream-unflagged-icn').removeClass('hide');
				$('#action-form-container'+postId).find('.stream-action-flag').removeClass('hide');
				$('#action-form-container'+postId).find('.stream-action-esc').removeClass('hide');
				$('#action-form-container'+postId).find('.stream-action-submit').removeClass('hide');
		}else if(status == 'ALERT'){
			$('#stream-post-icn-cont-'+postId).find('.stream-flagged-icn').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-unflag').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-esc').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-submit').removeClass('hide');
		}else if(status == 'ESCALATED'){
			$('#stream-post-icn-cont-'+postId).find('.stream-esc-icn').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-res').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-submit').removeClass('hide');
		}else if(status == 'RESOLVED'){
			$('#stream-post-icn-cont-'+postId).find('.stream-res-icn').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-esc').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-submit').removeClass('hide');
		}else {
			$('#stream-post-icn-cont-'+postId).find('.stream-res-icn').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-esc').removeClass('hide');
			$('#action-form-container'+postId).find('.stream-action-submit').removeClass('hide');
		}
		
		$('#stream-post-details').attr('id','stream-post-details-'+postId);
		var ownerProfileImage = streamPostList[i].ownerProfileImage;
		if(ownerProfileImage == null){
			ownerProfileImage = 'resources/images/place-holder-individual.png';
			$('#stream-post-details-'+postId).find('.stream-res-icn').css('border','2px solid #e1e1e1');
		}
			
		$('#stream-post-details-'+postId).find('.stream-res-icn').attr('src',ownerProfileImage);
		
		if(streamPostList[i].type == 'FACEBOOK'){
			$('#stream-post-details-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/ss-icon-small-facebook.png');
		}else if(streamPostList[i].type == 'LINKEDIN'){
			$('#stream-post-details-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/ss-icon-small-linkedin.png');
		}else if(streamPostList[i].type == 'TWITTER'){
			$('#stream-post-details-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/ss-icon-small-twitter.png');
		}else if(streamPostList[i].type == 'INSTAGRAM'){
			$('#stream-post-details-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/social_instagram.png');
			$('#stream-post-details-'+postId).find('.stream-res-feed-icn').addClass('stream-feed-insta');
		}
		
		$('#stream-post-details-'+postId).find('.stream-user-name').html(streamPostList[i].ownerName);
		$('#stream-post-details-'+postId).find('.stream-feed-type').html(streamPostList[i].type);
		$('#stream-post-details-'+postId).find('.stream-feed-type').attr('data-link',streamPostList[i].postLink);
		
		var lastUpdated = parseInt(streamPostList[i].updatedOn);
		var lastUpdatedDate = new Date(lastUpdated);
		
		var monthName=new Array("January","February","March","April","May","June","July","August","September","October","November","December");
		var lastUpdatedDateStr = monthName[lastUpdatedDate.getMonth()]+' '+(lastUpdatedDate.getDate()%10!=lastUpdatedDate.getDate()?lastUpdatedDate.getDate():('0'+lastUpdatedDate.getDate()))+', '+lastUpdatedDate.getFullYear()
								+' at '+(lastUpdatedDate.getHours()%10!=lastUpdatedDate.getHours()?lastUpdatedDate.getHours():('0'+lastUpdatedDate.getHours()))+':'
								+(lastUpdatedDate.getMinutes()%10!=lastUpdatedDate.getMinutes()?lastUpdatedDate.getMinutes():('0'+lastUpdatedDate.getMinutes()))+':'
								+(lastUpdatedDate.getSeconds()%10!=lastUpdatedDate.getSeconds()?lastUpdatedDate.getSeconds():('0'+lastUpdatedDate.getSeconds()));
		
		$('#stream-post-details-'+postId).find('.stream-post-date').html(lastUpdatedDateStr);
		
		if(streamPostList[i].duplicateCount > 1){
			$('#stream-post-details-'+postId).find('.stream-dup-container').removeClass('hide');
			$('#stream-post-details-'+postId).find('.post-dup').find('.dup-count').html(streamPostList[i].duplicateCount);
		}
		
		var postSource = streamPostList[i].postSource;
		var fromTrustedSource = streamPostList[i].fromTrustedSource;
		
		if(postSource != "" && postSource != null && postSource != undefined){
			$('#stream-post-details-'+postId).find('.ts-container').removeClass('hide');
			$('#stream-post-details-'+postId).find('.ts-source').html(postSource);
			$('#stream-post-details-'+postId).find('.ts-source').attr('data-trusted',fromTrustedSource);
			$('#stream-post-details-'+postId).find('.ts-source').attr('data-source',postSource);
			
			if(fromTrustedSource == true || fromTrustedSource == 'true'){
				$('#stream-post-details-'+postId).find('.ts-act-icon').removeClass('ts-add');
				$('#stream-post-details-'+postId).find('.ts-act-icon').addClass('ts-remove');
				$('#stream-post-details-'+postId).find('.trusted-source').removeClass('hide');
				$('#stream-post-details-'+postId).find('.ts-act-icon').attr('title','Remove Trusted Source')
			}
		}
		
		$('#stream-post-details-cont-'+postId).find('.email-reply-text').html(streamPostList[i].textHighlighted);
		
		if(streamPostList[i].mediaEntities != null && streamPostList[i].mediaEntities != undefined){
			for(var picI=0; picI<streamPostList[i].mediaEntities.length; picI++){
				if(streamPostList[i].mediaEntities[picI] != null && streamPostList[i].mediaEntities[picI] != undefined && streamPostList[i].mediaEntities[picI] != ''){
					if(streamPostList[i].mediaEntities[picI].type === 'VIDEO') {
						var videoIcon = '<div class="video-icon"><a href="'+streamPostList[i].postLink+'" target="_blank"><svg width="60" height="60" xmlns="http://www.w3.org/2000/svg"><title/><desc/><g><title>background</title><rect fill="none" id="canvas_background" height="62" width="62" y="-1" x="-1"/></g><g><title>Play</title><g stroke="null" id="Page-1" fill-rule="evenodd" fill="none"><g stroke="null" id="Icons-AV" fill="#009FE0"><g stroke="null" id="play-circle-outline"><path stroke="null" id="Shape" d="m24.200001,42.825l17.4,-12.825l-17.4,-12.825l0,25.65l0,0zm5.8,-41.325c-15.95,0 -29,12.825 -29,28.5c0,15.675 13.05,28.5 29,28.5c15.95,0 29,-12.825 29,-28.5c0,-15.675 -13.05,-28.5 -29,-28.5l0,0zm0,51.3c-12.76,0 -23.2,-10.26 -23.2,-22.8c0,-12.54 10.44,-22.8 23.2,-22.8c12.76,0 23.2,10.26 23.2,22.8c0,12.54 -10.44,22.8 -23.2,22.8l0,0z"/></g></g></g></g></svg></a></div>';
						var picContainer = '<div class="col-lg-10 col-md-10 col-sm-10 col-xs-10 float-right stream-post-pic-div" >'
				   			+'<img src="'+streamPostList[i].mediaEntities[picI].thumbnailUrl+'" class="stream-post-details-pic float-left stream-post-pic">'+videoIcon+'</div>';
						$('#stream-post-details-cont-'+postId).append(picContainer);
					} else {
						var picContainer = '<div class="col-lg-10 col-md-10 col-sm-10 col-xs-10 float-right stream-post-pic-div" >'
				   			+'<img src="'+streamPostList[i].mediaEntities[picI].url+'" class="stream-post-details-pic float-left stream-post-pic"></div>';
						$('#stream-post-details-cont-'+postId).append(picContainer);
					}
				}
			}	
		}
		
		$('#action-form-cont').attr('id','action-form-cont'+postId);
		
		$(document).on('click','.send-mail-post',function(e){
			e.stopPropagation();
			$(this).addClass('stream-post-mail-note-active');
			$(this).closest('.action-form-cont').find('.private-note-post').removeClass('stream-post-mail-note-active');
			$(this).parent().find('.form-text-act-type').val('SEND_EMAIL');
		});
		
		$(document).on('click','.private-note-post',function(e){
			e.stopPropagation();
			$(this).addClass('stream-post-mail-note-active');
			$(this).closest('.action-form-cont').find('.send-mail-post').removeClass('stream-post-mail-note-active');
			$(this).parent().find('.form-text-act-type').val('PRIVATE_NOTE');
		});
		
		$('#add-post-action').attr('id','add-post-action-'+postId);
		$('#add-post-action-'+postId).find('.form-status').val(streamPostList[i].status);
		$('#add-post-action-'+postId).find('.form-post-id').val(postId);
		
		drawMacroListDropdown(postId,macroList);
				
		$(document).on('click','.stream-macro-dropdown',function(e){
			e.stopImmediatePropagation();
			e.preventDefault();
			
			if($(this).closest('.action-form-container').find('.macro-options-list').hasClass('hide')){
				$(this).closest('.action-form-container').find('.macro-options-list').removeClass('hide');
			}else{
				$(this).closest('.action-form-container').find('.macro-options-list').addClass('hide');
			}
			
			if($(this).find('.mac-chevron-down').hasClass('hide')){
				$(this).find('.mac-chevron-down').removeClass('hide');
			}else{
				$(this).find('.mac-chevron-down').addClass('hide');
			}
			
			if($(this).find('.mac-chevron-up').hasClass('hide')){
				$(this).find('.mac-chevron-up').removeClass('hide');
			}else{
				$(this).find('.mac-chevron-up').addClass('hide');
			}
		});
		
		$(document).on('click','.macro-opt',function(e){
			e.stopImmediatePropagation();
			e.preventDefault();
			var postId =  $(this).find('.macro-list-data').attr('data-post-id');
			var macroId = $(this).find('.macro-list-data').attr('data-macro-id');
			var macroName =$(this).find('.macro-list-data').attr('data-macro-name');
			var status = $(this).find('.macro-list-data').attr('data-status');
			var textActionType = $(this).find('.macro-list-data').attr('data-text-action-type');
			var text = $(this).find('.macro-list-data').attr('data-text');
			
			if(text == undefined || text == null){
				text='';
			}
			
			$('#macro-form-post-id').val(postId);
			$('#macro-form-status').val(status);
			$('#macro-form-text-act-type').val(textActionType);
			$('#macro-form-macro-id').val(macroId);
			$('#macro-form-text').val(text);
			
			$('#add-post-action-'+postId).find('.form-status').val(status);
			
			var url = './updatepostactionwithmacro.do';
			callFormAjaxPostForSocMonBtn(url,'macro-form-apply',postId);
			
			if($(this).closest('.stream-macro-dropdown').find('.mac-chevron-down').hasClass('hide')){
				$(this).closest('.stream-macro-dropdown').find('.mac-chevron-down').removeClass('hide');
			}else{
				$(this).closest('.stream-macro-dropdown').find('.mac-chevron-down').addClass('hide');
			}
			
			if($(this).closest('.stream-macro-dropdown').find('.mac-chevron-up').hasClass('hide')){
				$(this).closest('.stream-macro-dropdown').find('.mac-chevron-up').removeClass('hide');
			}else{
				$(this).closest('.stream-macro-dropdown').find('.mac-chevron-up').addClass('hide');
			}
			$(this).closest('.macro-options-list').addClass('hide');
		});
		
		$('#action-history').attr('id','action-history'+postId);
		
		var sortedActionHistory = [];
		var latestAction = 0;
		var l=0;
		while(streamPostList[i].actionHistory.length>0){
		
			var k=0;
			latestAction=0;
			
			for(var j=0;j<streamPostList[i].actionHistory.length;j++){
				var action = streamPostList[i].actionHistory[j];
				
				if(action.createdDate > latestAction){
					latestAction=action.createdDate;
					k=j;
				}
			}
			
			sortedActionHistory[l++]=streamPostList[i].actionHistory[k];
			streamPostList[i].actionHistory.splice(k,1);
		}
		
		for(var j=0;j<sortedActionHistory.length;j++){
			$('#action-history'+postId).append(streamActionContainer);
			$('#act-cont').show();
			$('#act-cont').attr('id','act-cont'+postId+'-'+j);
			$('#action-history'+postId).attr('data-count',(j+1));
			
			if($('#stream-post-cont-'+postId).hasClass('stream-container-gray')){
				$('#act-cont'+postId+'-'+j).addClass('stream-action-container-white');
			}
			
			if(sortedActionHistory[j].actionType == 'SUBMIT'){
				$('#act-cont'+postId+'-'+j).find('.act-action-msg').removeClass('hide');
				if(sortedActionHistory[j].messageType == 'EMAIL' || sortedActionHistory[j].messageType == 'EMAIL_REPLY'){
					$('#act-cont'+postId+'-'+j).find('.action-msg-icn').removeClass('hide');
					$('#act-cont'+postId+'-'+j).find('.action-mail-icn').removeClass('hide');
				}
				$('#act-cont'+postId+'-'+j).addClass('stream-action-mail');

				if(sortedActionHistory[j].messageType == 'EMAIL_REPLY'){
					$('#act-cont'+postId+'-'+j).find('.act-action').removeClass('hide');
					$('#act-cont'+postId+'-'+j).find('.act-details-text').html(sortedActionHistory[j].text);
					$('#act-cont'+postId+'-'+j).find('.act-details-msg-type').removeClass('hide');
					$('#act-cont'+postId+'-'+j).find('.act-action-msg').removeClass('hide');
					$('#act-cont'+postId+'-'+j).find('.act-details-msg-type').html('Email reply');
					$('#act-cont'+postId+'-'+j).find('.act-details-msg-text').html("<pre class='email-reply-text'>"+sortedActionHistory[j].message+"</pre>");
				} else {
					$('#act-cont'+postId+'-'+j).find('.act-details-msg-text').html(sortedActionHistory[j].message);
				}
			}else{
				$('#act-cont'+postId+'-'+j).find('.action-icn').removeClass('hide');
				if(sortedActionHistory[j].actionType == 'UNFLAGGED'){
					$('#act-cont'+postId+'-'+j).find('.action-flag-icn').removeClass('hide');
				}else if(sortedActionHistory[j].actionType == 'FLAGGED'){
					$('#act-cont'+postId+'-'+j).find('.action-flag-icn').removeClass('hide');
				}else if(sortedActionHistory[j].actionType == 'ESCALATE'){
					$('#act-cont'+postId+'-'+j).find('.action-esc-icn').removeClass('hide');
				}else if(sortedActionHistory[j].actionType == 'RESOLVED'){
					$('#act-cont'+postId+'-'+j).find('.action-res-icn').removeClass('hide');
				}
				
				$('#act-cont'+postId+'-'+j).find('.act-action').removeClass('hide');
				$('#act-cont'+postId+'-'+j).find('.act-details-text').html(sortedActionHistory[j].text);
				
				if(sortedActionHistory[j].messageType != "" && sortedActionHistory[j].messageType != null && sortedActionHistory[j].messageType != undefined){
					$('#act-cont'+postId+'-'+j).find('.act-details-msg-type').removeClass('hide');
					$('#act-cont'+postId+'-'+j).find('.act-action-msg').removeClass('hide');
					
					if(sortedActionHistory[j].messageType == 'PRIVATE_MESSAGE'){
						$('#act-cont'+postId+'-'+j).find('.act-details-msg-type').html('Private Note');
					}else if(sortedActionHistory[j].messageType == 'EMAIL'){
						$('#act-cont'+postId+'-'+j).find('.act-details-msg-type').html('Email');
					}
					
					$('#act-cont'+postId+'-'+j).find('.act-details-msg-text').html(sortedActionHistory[j].message);
				}
			}
			
			var lastUpdatedAction = parseInt(sortedActionHistory[j].createdDate);
			var lastUpdatedDateAction = new Date(lastUpdatedAction);
			var lastUpdatedDateActionStr = monthName[lastUpdatedDateAction.getMonth()]+' '+(lastUpdatedDateAction.getDate()%10!=lastUpdatedDateAction.getDate()?lastUpdatedDateAction.getDate():('0'+lastUpdatedDateAction.getDate()))+', '+lastUpdatedDateAction.getFullYear()
			+' at '+(lastUpdatedDateAction.getHours()%10!=lastUpdatedDateAction.getHours()?lastUpdatedDateAction.getHours():('0'+lastUpdatedDateAction.getHours()))+':'
			+(lastUpdatedDateAction.getMinutes()%10!=lastUpdatedDateAction.getMinutes()?lastUpdatedDateAction.getMinutes():('0'+lastUpdatedDateAction.getMinutes()))+':'
			+(lastUpdatedDateAction.getSeconds()%10!=lastUpdatedDateAction.getSeconds()?lastUpdatedDateAction.getSeconds():('0'+lastUpdatedDateAction.getSeconds()));
			
			$('#act-cont'+postId+'-'+j).find('.act-details-date').html(lastUpdatedDateActionStr);
		}
	}	
}

function drawMacroListDropdown(postId,macroList){
	$('#action-form-container'+postId).find('.macro-options-list').html('');
	
	$('#action-form-container'+postId).find('.macro-options-list').css('width',$('#action-form-container'+postId).find('.stream-macro-dropdown').css('width'));
	
	for(var m=0;m<macroList.length;m++){
		var macroId = macroList[m].macroId;
		var macroName=macroList[m].macroName;
		var active = macroList[m].active;
		var actions=macroList[m].actions;
		var status = actions.socialFeedStatus;
		var textActionType = actions.textActionType;
		var text = actions.text;
		if(text == undefined || text == null){
			text='';
		}
		
		var postStatus = $('#add-post-action-'+postId).find('.form-status').val();
		
		var inputDiv = '<input type="hidden" id="macro-'+postId+'-'+macroId+'" class="macro-list-data" data-post-id="'+postId+'" data-macro-id="'+macroId
		+'" data-macro-name="'+macroName+'" data-status="'+status+'" data-text-action-type="'+textActionType+'" data-text="'+text+'">';
		var macroDiv = '<div id="macro-option-'+macroId+'" data-macro-id="'+macroId+'" class="macro-opt">'+inputDiv+macroName+'</div>';
		var macroDivDisabled = '<div id="macro-option-'+macroId+'" data-macro-id="'+macroId+'" class="macro-disabled">'+inputDiv+macroName+'</div>';
		
		var disableMacro = false;
		if(active == true || active == 'true'){
			if((postStatus == 'RESOLVED')){
				if(status != 'NEW' && status != 'ALERT' && status != 'RESOLVED'){	
					$('#action-form-container'+postId).find('.macro-options-list').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else if(postStatus == 'ESCALATED'){
				if(status != 'NEW' && status != 'ALERT' && status != 'ESCALATED'){	
					$('#action-form-container'+postId).find('.macro-options-list').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else if(postStatus == 'NEW'){
				if(status != 'NEW' && status != "RESOLVED"){
					$('#action-form-container'+postId).find('.macro-options-list').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else if(postStatus == 'ALERT'){
				if(status != 'ALERT' && status != "RESOLVED"){
					$('#action-form-container'+postId).find('.macro-options-list').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}
		}
		
		if(disableMacro){
			$('#action-form-container'+postId).find('.macro-options-list').append(macroDivDisabled);
		}
					
	}
}

function drawBulkMacroListDropdown(macroList){
	$('#bulk-macro-options').html('');
	
	for(var m=0;m<macroList.length;m++){
		var macroId = macroList[m].macroId;
		var macroName=macroList[m].macroName;
		var active = macroList[m].active;
		var actions=macroList[m].actions;
		var status = actions.socialFeedStatus;
		var textActionType = actions.textActionType;
		var text = actions.text;
		if(text == undefined || text == null){
			text='';
		}
		
		var postStatus = 'NEW';
				
		if( $('#soc-mon-stream-tab').hasClass('soc-mon-stream-active')){
			postStatus = 'NEW';
			
			$('#bulk-edit-unflag').show();
			$('#bulk-edit-flag').show();
			$('#bulk-edit-esc').show();
			$('#bulk-edit-res').show();
			
			$('#bulk-edit-unflag').addClass('bulk-act-btn');
			$('#bulk-edit-flag').addClass('bulk-act-btn');
			$('#bulk-edit-esc').addClass('bulk-act-btn');
			$('#bulk-edit-res').addClass('bulk-act-btn');
			$('#bulk-edit-sub').addClass('bulk-act-btn');
		}else if( $('#soc-mon-escalated-tab').hasClass('soc-mon-esc-active')){
			postStatus = 'ESCALATED';
			
			$('#bulk-edit-unflag').hide();
			$('#bulk-edit-flag').hide();
			$('#bulk-edit-esc').hide();
			$('#bulk-edit-res').show();
			
			$('#bulk-edit-unflag').removeClass('bulk-act-btn');
			$('#bulk-edit-flag').removeClass('bulk-act-btn');
			$('#bulk-edit-esc').removeClass('bulk-act-btn');
			$('#bulk-edit-res').removeClass('bulk-act-btn');
			$('#bulk-edit-sub').removeClass('bulk-act-btn');
		}else if( $('#soc-mon-resolved-tab').hasClass('soc-mon-res-active')){
			postStatus = 'RESOLVED';
			
			$('#bulk-edit-unflag').hide();
			$('#bulk-edit-flag').hide();
			$('#bulk-edit-esc').show();
			$('#bulk-edit-res').hide();
			
			$('#bulk-edit-unflag').removeClass('bulk-act-btn');
			$('#bulk-edit-flag').removeClass('bulk-act-btn');
			$('#bulk-edit-esc').removeClass('bulk-act-btn');
			$('#bulk-edit-res').removeClass('bulk-act-btn');
			$('#bulk-edit-sub').removeClass('bulk-act-btn');
		}else if($('#soc-mon-alerts-tab').hasClass('soc-mon-alert-active')){
			postStatus = 'ALERT';
			$('#bulk-edit-unflag').show();
			$('#bulk-edit-flag').hide();
			$('#bulk-edit-esc').show();
			$('#bulk-edit-res').hide();
			
			$('#bulk-edit-unflag').removeClass('bulk-act-btn');
			$('#bulk-edit-flag').removeClass('bulk-act-btn');
			$('#bulk-edit-esc').removeClass('bulk-act-btn');
			$('#bulk-edit-res').removeClass('bulk-act-btn');
			$('#bulk-edit-sub').removeClass('bulk-act-btn');
		}
		
		var inputDiv = '<input type="hidden" id="bulk-macro-'+macroId+'" class="macro-list-data" data-macro-id="'+macroId
		+'" data-macro-name="'+macroName+'" data-status="'+status+'" data-text-action-type="'+textActionType+'" data-text="'+text+'">';
		var macroDiv = '<div id="bulk-macro-option-'+macroId+'" data-macro-id="'+macroId+'" class="bulk-macro-opt">'+inputDiv+macroName+'</div>';
		var macroDivDisabled = '<div id="bulk-macro-option-'+macroId+'" data-macro-id="'+macroId+'" class="macro-disabled">'+inputDiv+macroName+'</div>';
		
		var disableMacro = false;
		
		if(active == true || active == 'true'){
			if((postStatus == 'RESOLVED')){
				if(status != 'NEW' && status != 'ALERT' && status != 'RESOLVED'){	
					$('#bulk-macro-options').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else if(postStatus == 'ESCALATED'){
				if(status != 'NEW' && status != 'ALERT' && status != 'ESCALATED'){	
					$('#bulk-macro-options').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else if(postStatus == 'ALERT'){
				if(status != 'ALERT' && status != "RESOLVED"){
					$('#bulk-macro-options').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else{
				$('#bulk-macro-options').append(macroDiv);
			}			
		}
		
		if(disableMacro){
			$('#bulk-macro-options').append(macroDivDisabled);
		}				
	}
}

$(document).on('click','#bulk-macro-dropdown',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	if($('#bulk-macro-options').hasClass('hide')){
		$('#bulk-macro-options').removeClass('hide');
		$('#bulk-macro-options').css('width',$('#bulk-macro-dropdown').css('width'));
	}else{
		$('#bulk-macro-options').addClass('hide');
	}
	
	if($('#bulk-mac-chevron-down').hasClass('hide')){
		$('#bulk-mac-chevron-down').removeClass('hide');
	}else{
		$('#bulk-mac-chevron-down').addClass('hide');
	}
	
	if($('#bulk-mac-chevron-up').hasClass('hide')){
		$('#bulk-mac-chevron-up').removeClass('hide');
	}else{
		$('#bulk-mac-chevron-up').addClass('hide');
	}
});

$(document).on('click','.bulk-macro-opt',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#action-type-sel').val(2);
	
	var macroId = $(this).find('.macro-list-data').attr('data-macro-id');
	var status = $(this).find('.macro-list-data').attr('data-status');
	var textActionType = $(this).find('.macro-list-data').attr('data-text-action-type');
	var text = $(this).find('.macro-list-data').attr('data-text');
	
	if(text == undefined || text == null){
		text='';
	}
	
	$('#bulk-actions-apply').find('.form-status').val(status);
	$('#macro-form-post-id').val($('#selected-post-ids').data('post-ids'));
	$('#macro-form-status').val(status);
	$('#macro-form-text-act-type').val(textActionType);
	$('#macro-form-macro-id').val(macroId);
	$('#macro-form-text').val(text);
	
	$('#bulk-options-popup').hide();
	
	var url = './updatepostactionwithmacro.do';
	var disableEle = '#bulk-edit-unflag';
	
	callFormAjaxPostForSocMon(url,disableEle,'macro-form-apply');
	
	$('#bulk-macro-options').addClass('hide');
	
	$('#bulk-mac-chevron-down').removeClass('hide');
	
	$('#bulk-mac-chevron-up').addClass('hide');
});

$(document).on('click','#bulk-send-mail-post',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('stream-post-mail-note-active');
	$('#bulk-private-note-post').removeClass('stream-post-mail-note-active');
	$('#bulk-actions-apply').find('.form-text-act-type').val('SEND_EMAIL');
});

$(document).on('click','#bulk-private-note-post',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('stream-post-mail-note-active');
	$('#bulk-send-mail-post').removeClass('stream-post-mail-note-active');
	$('#bulk-actions-apply').find('.form-text-act-type').val('PRIVATE_NOTE');
});

$(document).on('click','#bulk-edit-unflag',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
	$('#bulk-actions-apply').find('.form-status').val('NEW');
	$('#bulk-actions-apply').find('.form-macro-id').val('');
	$('#bulk-actions-apply').find('.form-post-textbox').val( $('#bulk-edit-txt-box').val() );
	
	$('#bulk-options-popup').hide();
	
	var url = './updatepostaction.do';
	var disableEle = '#bulk-edit-unflag';
	
	callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	
});

$(document).on('click','#bulk-edit-flag',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
	$('#bulk-actions-apply').find('.form-status').val('ALERT');
	$('#bulk-actions-apply').find('.form-macro-id').val('');
	$('#bulk-actions-apply').find('.form-post-textbox').val( $('#bulk-edit-txt-box').val() );
	
	$('#bulk-options-popup').hide();
	
	var url = './updatepostaction.do';
	var disableEle = '#bulk-edit-flag';
	
	callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	
});

$(document).on('click','#bulk-edit-esc',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
	$('#bulk-actions-apply').find('.form-status').val('ESCALATED');
	$('#bulk-actions-apply').find('.form-macro-id').val('');
	$('#bulk-actions-apply').find('.form-post-textbox').val( $('#bulk-edit-txt-box').val() );
	
	var text = $('#bulk-edit-txt-box').val();
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note. Escalation not allowed without a comment for the user");
		showToast();
		
		return;
	}
	
	$('#bulk-options-popup').hide();
	
	var url = './updatepostaction.do';
	var disableEle = '#bulk-edit-esc';
	
	callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	
});

$(document).on('click','#bulk-edit-res',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
	$('#bulk-actions-apply').find('.form-status').val('RESOLVED');
	$('#bulk-actions-apply').find('.form-macro-id').val('');
	$('#bulk-actions-apply').find('.form-post-textbox').val( $('#bulk-edit-txt-box').val() );
	
	var text = $('#bulk-edit-txt-box').val();
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note. Resolution not allowed without a comment for the user");
		showToast();
		
		return;
	}
	
	$('#bulk-options-popup').hide();
	
	var url = './updatepostaction.do';
	var disableEle = '#bulk-edit-res';
	
	callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	
});

$(document).on('click','#bulk-edit-sub',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var text = $('#bulk-edit-txt-box').val();
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note.");
		showToast();
		
		return;
	}
	
	$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
	$('#bulk-actions-apply').find('.form-status').val('SUBMIT');
	$('#bulk-actions-apply').find('.form-macro-id').val('');
	$('#bulk-actions-apply').find('.form-post-textbox').val( $('#bulk-edit-txt-box').val() );
	
	$('#bulk-options-popup').hide();
	
	var url = './updatepostaction.do';
	var disableEle = '#bulk-edit-sub';
	
	callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	
});


var lastUpdatedAction;
var lastUpdatedDateAction;
var lastUpdatedDateActionStr;
var monthName=new Array("January","February","March","April","May","June","July","August","September","October","November","December");

function callFormAjaxPostForSocMonBtn(url,formId,postId){
	
	var status = $('#stream-tabs').data('status');

	var startIndex = $('#stream-pagination').data('startIndex');
	
	var $form = $('#'+formId);
	var payLoad = $form.serialize();
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payLoad,
		success : showToastForSocMonActions,
		complete: function(data){
			getStreamPosts(startIndex,status);
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$("#overlay-toast").html("Failed to update post. Please Try again");
			showToast();
		}
	});
}

function showToastForSocMonActions(data){
	var map = $.parseJSON(data);
	if (map.status == "success") {
		
		var message = 'Record updated successfully';
		$("#overlay-toast").html(message);
		showToast();
			
	} else {
		$("#overlay-toast").html("Failed to update post. Please Try again");
		showToast();
	}
}

function callFormAjaxPostForSocMonDupBtn(url,formId,postId){
	
	var status = $('#stream-tabs').data('status');

	var startIndex = $('#stream-pagination').data('startIndex');
	
	var $form = $('#'+formId);
	var payLoad = $form.serialize();
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payLoad,
		success : showToastForDupActions,
		complete: function(data){
			getStreamPosts(startIndex,status);
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$("#overlay-toast").html("Failed to update post. Please Try again");
			showToast();
		}
	});
}

function showToastForDupActions(data){
	var map = $.parseJSON(data);
	var status = $('#dup-post-action-form-cont').find('.form-status').val();
	
	if (map.status == "success") {
		var totalPosts = $('#selected-post-ids').data('post-ids');
		var successPosts = (JSON.parse(map.Success)).successPostIds;
		
		if(successPosts == null || successPosts == undefined){
			$("#overlay-toast").html("Failed to update posts. Please Try again");
			showToast();
			return;
		}
		
		var successCount = successPosts.length;
		var total = parseInt($('#dup-post-cont').attr('data-count'));
		var failedCount = total - successCount;
				
		var updatedAction = 'Unflagged';
		if(status == 'NEW'){
			updatedAction = "Unflagged";
		}else if(status == 'ALERT'){
			updatedAction = "Flagged";
		}else if(status == 'ESCALATED'){
			updatedAction = 'Escalated';
		}else if(status == 'RESOLVED'){
			updatedAction = 'Resolved';
		}else{
			var message = totalPosts.length +' records updated successfully';
			$("#overlay-toast").html(message);
			showToast();
			return;
		}
		
		var message = successCount +' records successfully moved to '+ updatedAction +'. \n'+ failedCount + ' failed to be moved  to ' + updatedAction + '.';
		$("#overlay-toast").html(message);
		showToast();
		
	} else {
		$("#overlay-toast").html("Failed to update posts. Please Try again");
		showToast();
	}
}


$(document).on('click','.stream-action-unflag',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	$(this).attr('id','stream-action-unflag-'+postId);
	var formId = ('add-post-action-'+postId);
	var disableEle = ('#stream-action-unflag-'+postId);
	var textActionType = $('#action-form-cont'+postId).find('.form-text-act-type').val();
	
	$(this).closest('.action-form-cont').find('.form-status').val('NEW');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonBtn(url,formId,postId);
});

$(document).on('click','.stream-action-flag',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	$(this).attr('id','stream-action-flag-'+postId);
	var formId = ('add-post-action-'+postId);
	var disableEle = ('#stream-action-flag-'+postId);
	var textActionType = $('#action-form-cont'+postId).find('.form-text-act-type').val();
	
	$(this).closest('.action-form-cont').find('.form-status').val('ALERT');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonBtn(url,formId,postId);
});

$(document).on('click','.stream-unflagged-icn',function(e){
	e.stopPropagation();
	var postId = $(this).parent().find('.post-id-details').attr('data-post-id');
	var formId = ('add-post-action-'+postId);
	var disableEle = ('#stream-action-unflag-'+postId);
	var textActionType = $('#action-form-cont'+postId).find('.form-text-act-type').val();
	
	$('#action-form-cont'+postId).find('.form-status').val('ALERT');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonBtn(url,formId,postId);
});

$(document).on('click','.stream-flagged-icn',function(e){
	e.stopPropagation();
	var postId = $(this).parent().find('.post-id-details').attr('data-post-id');
	var formId = ('add-post-action-'+postId);
	var disableEle = ('#stream-action-flag-'+postId);
	var textActionType = $('#action-form-cont'+postId).find('.form-text-act-type').val();
	
	$('#action-form-cont'+postId).find('.form-status').val('NEW');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonBtn(url,formId,postId);
});

$(document).on('click','.stream-action-esc',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	$(this).attr('id','stream-action-esc-'+postId);
	var formId = ('add-post-action-'+postId);
	var disableEle = ('#stream-action-esc-'+postId);
	var textActionType = $('#action-form-cont'+postId).find('.form-text-act-type').val();
	var text = $('#action-form-cont'+postId).find('.form-post-textbox').val();
	
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note. Escalation not allowed without a comment for the user.");
		showToast();
		
		return;
	}
	
	$(this).closest('.action-form-cont').find('.form-status').val('ESCALATED');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonBtn(url,formId,postId);
});

$(document).on('click','.stream-action-res',function(e){
	e.stopPropagation();
	
	
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	$(this).attr('id','stream-action-res-'+postId);
	var formId = ('add-post-action-'+postId);
	var disableEle = ('#stream-action-res-'+postId);
	var textActionType = $('#action-form-cont'+postId).find('.form-text-act-type').val();
	var text = $('#action-form-cont'+postId).find('.form-post-textbox').val();
	
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note. Resolution not allowed without a comment for the user");
		showToast();
		
		return;
	}
	
	$(this).closest('.action-form-cont').find('.form-status').val('RESOLVED');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonBtn(url,formId,postId);
});

$(document).on('click','.stream-action-submit',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	$(this).attr('id','stream-action-submit-'+postId);
	var formId = ('add-post-action-'+postId);
	var disableEle = ('#stream-action-submit-'+postId);
	
	var text = $('#action-form-cont'+postId).find('.form-post-textbox').val();
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note.");
		showToast();
		
		return;
	}
	
	var currentStatus = $(this).closest('.action-form-cont').find('.form-status').val();
	
	$(this).closest('.action-form-cont').find('.form-status').val('SUBMIT');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonBtn(url,formId,postId);
});

$(document).on('click','#stream-unchecked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#stream-unchecked').toggle();
	$('#stream-checked').toggle();
	$('.stream-unchecked').hide();
	$('.stream-checked').show();
	
	var selPostIds = [];
	var idIndex = 0;
	$('.stream-checked').each(function(){
		selPostIds[idIndex++]=$(this).parent().find('.post-id-details').data('post-id');
	});
	
	$('#selected-post-ids').data('post-ids',selPostIds);
	
	
});

$(document).on('click','#stream-checked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#stream-unchecked').toggle();
	$('#stream-checked').toggle();
	$('.stream-unchecked').show();
	$('.stream-checked').hide();
	
	var selPostIds = [];
	$('#selected-post-ids').data('post-ids',selPostIds);
});

$(document).on('click','.stream-unchecked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var selPostIds = $('#selected-post-ids').data('post-ids');
	
	var thisPostId = $(this).parent().find('.post-id-details').data('post-id');
	var isSelected = false;
	for(var i=0;i<selPostIds.length;i++){
		if(selPostIds[i] == thisPostId){
			isSelected = true;
			break;
		}
	}
	
	var index = selPostIds.length;
	if(!isSelected){
		selPostIds[index]=thisPostId;
		$('#selected-post-ids').data('post-ids',selPostIds);
	}
	
	$(this).hide();
	$(this).parent().find('.stream-checked').show();
});

$(document).on('click','.stream-checked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).hide();
	$(this).parent().find('.stream-unchecked').show();
	$('#stream-checked').hide();
	$('#stream-unchecked').show();
	
	var selPostIds = $('#selected-post-ids').data('post-ids');
	
	var thisPostId = $(this).parent().find('.post-id-details').data('post-id');
	var indexOfPostId = 0;
	var isPresent = false;
	
	for(var i=0;i<selPostIds.length;i++){
		if(selPostIds[i] == thisPostId){
			indexOfPostId = i;
			isPresent = true;
			break;
		}
	}
	
	if(isPresent){
		selPostIds.splice(indexOfPostId,1);
		$('#selected-post-ids').data('post-ids',selPostIds);
	}
});

$(document).on('click','#action-send-mail-post',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	$('#bulk-actions-apply').find('.form-text-act-type').val('SEND_EMAIL');
	
	$(this).addClass('stream-post-mail-note-active');
	$('#action-private-note-post').removeClass('stream-post-mail-note-active');
});

$(document).on('click','#action-private-note-post',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	$('#bulk-actions-apply').find('.form-text-act-type').val('PRIVATE_NOTE');
	
	$(this).addClass('stream-post-mail-note-active');
	$('#action-send-mail-post').removeClass('stream-post-mail-note-active');
});

$(document).on('click','#stream-bulk-unflag',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();

	var selectedPosts = $('#selected-post-ids').data('post-ids');
	if(selectedPosts.length == 0){
		$("#overlay-toast").html("Select at least 1 post to apply the action.");
		showToast();
		
		return;
	}
	
	$('#action-popup').removeClass('hide');
	$('#action-edit-unflag').show();
	
	$(document).on('click','#action-edit-unflag',function(e){
		e.stopImmediatePropagation();
		e.preventDefault();
		
		$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
		$('#bulk-actions-apply').find('.form-status').val('NEW');
		$('#bulk-actions-apply').find('.form-macro-id').val('');
		
		var text = $('#action-edit-txt-box').val();
		$('#bulk-actions-apply').find('.form-post-textbox').val(text);
		
		actionPopupRevert();
		
		var url = './updatepostaction.do';
		var disableEle = '#action-edit-unflag';
		
		callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	});
});

$(document).on('click','#stream-bulk-flag',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();

	var selectedPosts = $('#selected-post-ids').data('post-ids');
	if(selectedPosts.length == 0){
		$("#overlay-toast").html("Select at least 1 post to apply the action.");
		showToast();
		
		return;
	}
	
	$('#action-popup').removeClass('hide');
	$('#action-edit-flag').show();
	
	$(document).on('click','#action-edit-flag',function(e){
		e.stopImmediatePropagation();
		e.preventDefault();
		
		$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
		$('#bulk-actions-apply').find('.form-status').val('ALERT');
		$('#bulk-actions-apply').find('.form-macro-id').val('');
		
		var text = $('#action-edit-txt-box').val();
		$('#bulk-actions-apply').find('.form-post-textbox').val(text);
		
		actionPopupRevert();
		
		var url = './updatepostaction.do';
		var disableEle = '#action-edit-flag';
		
		callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	});
});

$(document).on('click','#stream-bulk-esc',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var selectedPosts = $('#selected-post-ids').data('post-ids');
	if(selectedPosts.length == 0){
		$("#overlay-toast").html("Select at least 1 post to apply the action.");
		showToast();
		
		return;
	}
	
	$('#action-popup').removeClass('hide');
	$('#action-edit-esc').show();
	
	$(document).on('click','#action-edit-esc',function(e){
		e.stopImmediatePropagation();
		e.preventDefault();
		
		$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
		$('#bulk-actions-apply').find('.form-status').val('ESCALATED');
		$('#bulk-actions-apply').find('.form-macro-id').val('');

		var text = $('#action-edit-txt-box').val();
		if(text == '' || text == undefined || text == null){
			$("#overlay-toast").html("Please enter some text for Mail/Private note. Escalation not allowed without a comment for the user");
			showToast();
			
			return;
		}
		$('#bulk-actions-apply').find('.form-post-textbox').val(text);
		
		actionPopupRevert();
		
		var url = './updatepostaction.do';
		var disableEle = '#action-edit-esc';
		
		callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	});
});

$(document).on('click','#stream-bulk-res',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var selectedPosts = $('#selected-post-ids').data('post-ids');
	if(selectedPosts.length == 0){
		$("#overlay-toast").html("Select at least 1 post to apply the action.");
		showToast();
		
		return;
	}
	
	$('#action-popup').removeClass('hide');
	$('#action-edit-res').show();
	
	$(document).on('click','#action-edit-res',function(e){
		
		$('#bulk-actions-apply').find('.form-post-id').val($('#selected-post-ids').data('post-ids'));
		$('#bulk-actions-apply').find('.form-status').val('RESOLVED');
		$('#bulk-actions-apply').find('.form-macro-id').val('');

		var text = $('#action-edit-txt-box').val();
		if(text == '' || text == undefined || text == null){
			$("#overlay-toast").html("Please enter some text for Mail/Private note. Resolution not allowed without a comment for the user");
			showToast();
			
			return;
		}
		$('#bulk-actions-apply').find('.form-post-textbox').val(text);
		
		actionPopupRevert();
		
		var url = './updatepostaction.do';
		var disableEle = '#action-edit-res';
		
		callFormAjaxPostForSocMon(url,disableEle,'bulk-actions-apply');
	});
});

$(document).on('click','#action-cancel',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	actionPopupRevert();
});

function actionPopupRevert(){
	
	$('#action-popup').addClass('hide');
	
	$('#action-edit-res').hide();
	$('#action-edit-esc').hide();
	$('#action-edit-flag').hide();
	$('#action-edit-unflag').hide();
	
	$('#action-edit-txt-box').val('');
	
	$('#stream-bulk-action-options').hide();
	$('#chevron-down').toggle();
	$('#chevron-up').toggle();
	
}

function callFormAjaxPostForSocMon(url,disableEle,formId){
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	
	var $form = $('#'+formId);
	var payLoad = $form.serialize();
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payLoad,
		success : showToastForBulkActions,
		complete: function(data){
			enable(disableEle);
			getStreamPosts(startIndex,status);
			$('#stream-unchecked').toggle();
			$('#stream-checked').toggle();
			var selPostIds = [];
			$('#selected-post-ids').data('post-ids',selPostIds);
			$('#action-type-sel').val(1);
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$("#overlay-toast").html("Failed to update posts. Please Try again");
			showToast();
		}
	});
}

function showToastForBulkActions(data){
	var map = $.parseJSON(data);
	var status = $('#bulk-actions-apply').find('.form-status').val();
	
	var actionTypeSel = parseInt($('#action-type-sel').val());
	if (map.status == "success") {
		var totalPosts = $('#selected-post-ids').data('post-ids');
		var successPosts = (JSON.parse(map.Success)).successPostIds;
		
		var successCount = successPosts.length;
		var total = totalPosts.length;
		var failedCount = total - successCount;
				
		var updatedAction = 'Unflagged';
		if(status == 'NEW'){
			updatedAction = "Unflagged";
		}else if(status == 'ALERT'){
			updatedAction = "Flagged";
		}else if(status == 'ESCALATED'){
			updatedAction = 'Escalated';
		}else if(status == 'RESOLVED'){
			updatedAction = 'Resolved';
		}else{
			var message = totalPosts.length +' records updated successfully';
			$("#overlay-toast").html(message);
			showToast();
			return;
		}
		
		var message = successCount +' records successfully moved to '+ updatedAction +'. \n'+ failedCount + ' failed to be moved  to ' + updatedAction + '.';
		$("#overlay-toast").html(message);
		showToast();
		
	} else {
		$("#overlay-toast").html("Failed to update posts. Please Try again");
		showToast();
	}
}

function getSegmentsByCompanyId(){
	
	var callAjax = true;
	var companyId = $("#companyId").val();
	if(sessionStorage) {
		var segments = JSON.parse(sessionStorage.getItem("sm-filter-segments-"+companyId));
		if(segments){
			drawSegmentList(segments);
			getUsersByCompanyId();
			callAjax = false;
		} 
	} 
	
	if(callAjax){
		$.ajax({
			url : "/getsegmentsbycompanyid.do",
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(response) {
				var segments = response;
				drawSegmentList(segments);
				if(sessionStorage) {
					sessionStorage.setItem("sm-filter-segments-"+companyId, JSON.stringify(segments));
				}
			},
			complete:function(e){
				getUsersByCompanyId();
			},
			error : function(e){
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
			}
		});
	}
}

function drawSegmentList(segments){
	
	var company = segments.segmentsEntity;
	var regions = segments.regionDetails;
	var branchs = segments.branchDetails;	
	
	var regionIds = [];
	var branchIds = [];
	
	var segContainer = '<div id="seg" class="stream-dropdown-option-container" data-segId=0  data-segType="">'
			+ '<img src="resources/images/check-no.png"  class="seg-unchecked hide float-left margin-right-10 cursor-pointer">'
			+'<img src="resources/images/check-yes.png"  class="seg-checked float-left margin-right-10 cursor-pointer">'
			+'<div class="seg-img float-left margin-right-10 stream-dropdown-img-circle"></div>'
			+'<div class="seg-name float-left stream-dropdown-name-txt-bold"></div></div>';
	
	var usrOuterContainer = '<div id="region" class="usr-list-opt stream-dropdown-option-container" data-iden=0 data-regionId=0>'
		+'<img src="resources/images/check-no.png"  class="usr-seg-unchecked hide float-left margin-right-10 cursor-pointer">'
		+'<img src="resources/images/check-yes.png"  class="usr-seg-checked float-left margin-right-10 cursor-pointer">'
		+'<div class="usr-seg-partial hide float-left chkbox-partial-sel-outer"><div class="chkbox-partial-sel-inner"></div></div>'
		+'<img src="resources/images/chevron-down.png" class="hide usr-list-chev-down float-left">'
		+'<img src="resources/images/chevron-up.png" class="usr-list-chev-up float-left">'
		+'<div class="usr-list-img float-left stream-dropdown-img-circle"></div>'
		+'<div class="usr-list-name float-left stream-dropdown-name-txt-bold"></div></div>';
	
	var isDefaultCompImage = false;
	var companyProfileImageUrl = company.profileImageUrl;
	if(companyProfileImageUrl == null || companyProfileImageUrl == '' || companyProfileImageUrl == undefined){
		companyProfileImageUrl = 'resources/images/place-holder-Company.png';
		isDefaultCompImage = true;
	}
	
	$('#stream-seg-dropdown-options').html('');
	
	$('#stream-seg-dropdown-options').append(segContainer);
	$('#seg').data('segId',company.iden);
	$('#seg').data('segType','COMPANY');
	$('#seg').find('.seg-img').css("background-image", "url(" + companyProfileImageUrl + ")");
	if(isDefaultCompImage){
		$('#seg').find('.seg-img').css('border','2px solid #e1e1e1');
	}
	$('#seg').find('.seg-name').html(company.name);
	$('#seg').attr('id','company-'+company.iden);
	
	if(regions != null && regions != undefined && regions.length != 0){
		$('#stream-seg-dropdown-options').append('<div class="stream-dropdown-option-container stream-dropdown-segement-hdr">Regions</div>');
	}
		
	$('#UsrOptions').html('');
	
	$('#UsrOptions').append(usrOuterContainer);
	$('#region').data('iden',company.iden);
	$('#region').find('.usr-list-img').css("background-image", "url(" + companyProfileImageUrl + ")");
	if(isDefaultCompImage){
		$('#region').find('.usr-list-img').css('border','2px solid #e1e1e1');
	}
	$('#region').find('.usr-list-name').html(company.name);
	$('#region').addClass('com-option-con');
	$('#region').attr('id','usr-company-'+company.iden);
	
	if(regions != null && regions != undefined){
		for(var i=0; i<regions.length; i++){
			
			var isDefaultImage = false;
			var regionProfileImageUrl = regions[i].profileImageUrl;
			if(regionProfileImageUrl == null || regionProfileImageUrl == '' || regionProfileImageUrl == undefined){
				regionProfileImageUrl = 'resources/images/place-holder-Region.png';
				isDefaultImage = true;
			}
			
			$('#stream-seg-dropdown-options').append(segContainer);
			$('#seg').data('segId',regions[i].iden);
			$('#seg').data('segType','REGION');
			$('#seg').find('.seg-img').css("background-image", "url(" + regionProfileImageUrl + ")");
			if(isDefaultImage){
				$('#seg').find('.seg-img').css('border','2px solid #e1e1e1');
			}
			$('#seg').find('.seg-name').html(regions[i].name);
			$('#seg').attr('id','region-'+regions[i].iden);
			
			$('#UsrOptions').append('<div id="companyOptions" class="usr-dropdown-toggle stream-dropdown-option"></div>');
			
			$('#companyOptions').append(usrOuterContainer);
			$('#region').data('iden',regions[i].iden);
			$('#region').find('.usr-list-img').css("background-image", "url(" + regionProfileImageUrl + ")");
			if(isDefaultImage){
				$('#region').find('.usr-list-img').css('border','2px solid #e1e1e1');
			}
			$('#region').find('.usr-list-name').html(regions[i].name);
			$('#region').addClass('reg-option-con');
			$('#region').attr('id','usr-region-'+regions[i].iden);
			$('#companyOptions').attr('id','reg-opt-'+regions[i].iden);
			
			regionIds.push(regions[i].iden);
		}		
	}
	
	if(branchs != null && branchs != undefined && branchs.length != 0){
		$('#stream-seg-dropdown-options').append('<div class="stream-dropdown-option-container stream-dropdown-segement-hdr">Offices</div>');
	}
	
	if(branchs != null && branchs != undefined){
		for(var i=0; i<branchs.length; i++){
			var isDefaultImage = false;
			var branchProfileImageUrl = branchs[i].profileImageUrl;
			if(branchProfileImageUrl == null || branchProfileImageUrl == '' || branchProfileImageUrl == undefined){
				branchProfileImageUrl = 'resources/images/place-holder-Office.png';
				isDefaultImage =true;
			}
			
			$('#stream-seg-dropdown-options').append(segContainer);
			$('#seg').data('segId',branchs[i].iden);
			$('#seg').data('segType','BRANCH');
			$('#seg').find('.seg-img').css("background-image", "url(" + branchProfileImageUrl + ")");
			if(isDefaultImage){
				$('#seg').find('.seg-img').css('border','2px solid #e1e1e1');
			}
			$('#seg').find('.seg-name').html(branchs[i].name);
			$('#seg').attr('id','branch-'+branchs[i].iden);
			
			var branchRegId = branchs[i].regionId;
			if(isInArray(branchRegId,regionIds) == -1){
				$('#UsrOptions').append('<div id="companyOptions" class="usr-dropdown-toggle stream-dropdown-option"></div>');
				
				$('#companyOptions').append(usrOuterContainer);
				$('#region').data('iden',branchs[i].iden);
				$('#region').find('.usr-list-img').css("background-image", "url(" + branchProfileImageUrl + ")");
				if(isDefaultImage){
					$('#region').find('.usr-list-img').css('border','2px solid #e1e1e1');
				}
				$('#region').find('.usr-list-name').html(branchs[i].name);
				$('#region').addClass('bra-option-con');
				$('#region').attr('id','usr-branch-'+branchs[i].iden);
				$('#companyOptions').attr('id','bra-opt-'+branchs[i].iden);
				
			}else{
				$('#reg-opt-'+branchRegId).append('<div id="regionOptions" class="usr-dropdown-toggle stream-dropdown-option"></div>');
				
				$('#regionOptions').append(usrOuterContainer);
				$('#region').data('iden',branchs[i].iden);
				$('#region').data('regionId',branchRegId);
				$('#region').find('.usr-list-img').css("background-image", "url(" + branchProfileImageUrl + ")");
				if(isDefaultImage){
					$('#region').find('.usr-list-img').css('border','2px solid #e1e1e1');
				}
				$('#region').find('.usr-list-name').html(branchs[i].name);
				$('#region').addClass('bra-option-con');
				$('#region').attr('id','usr-branch-'+branchs[i].iden);
				$('#regionOptions').attr('id','bra-opt-'+branchs[i].iden);
			}
			
			
			branchIds.push(branchs[i].iden);
		}		
	}
	
	var count = 0;
	if(!$('#company'+company.iden).find('.seg-checked').hasClass('hide')){
		count++;
	}
	$('#stream-segment-count').html(count+regionIds.length+branchIds.length);
	$('#segment-data').data('companyId',company.iden);
	$('#segment-data').data('regionIds',regionIds);
	$('#segment-data').data('branchIds',branchIds);
		
}

function isInArray(val,array){
	if(array != null && array != undefined){
		for(var i=0;i<array.length;i++){
			if(array[i]==val){
				return i;
			}
		}
	}
	return -1;
}

$(document).on('click','.seg-unchecked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.seg-checked').removeClass('hide');
	
	var regionIds = $('#segment-data').data('regionIds');
	var branchIds = $('#segment-data').data('branchIds');
	
	var iden = $(this).parent().data('segId');
	var type = $(this).parent().data('segType');
	
	var index = -1;
	
	if(type == 'REGION'){
		index = isInArray(iden,regionIds);
		if(index == -1){
			regionIds.push(iden);
		}
	}else if(type == 'BRANCH'){
		index = isInArray(iden,branchIds);
		if(index == -1){
			branchIds.push(iden);
		}
	}else if(type == 'COMPANY'){
		$('#segment-data').data('companyId',iden);
	}
	
	var count = 0;
	var comId = $('#segment-data').data('companyId');
	if(comId != 0 && !$('#company'+comId).find('.seg-checked').hasClass('hide')){
		count++;
	}
	$('#stream-segment-count').html(count+regionIds.length+branchIds.length);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
	
});

$(document).on('click','.seg-checked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.seg-unchecked').removeClass('hide');
	
	var regionIds = $('#segment-data').data('regionIds');
	var branchIds = $('#segment-data').data('branchIds');
	
	var iden = $(this).parent().data('segId');
	var type = $(this).parent().data('segType');
	
	var index = -1;
	
	if(type == 'REGION'){
		index = isInArray(iden,regionIds);
		if(index > -1){
			regionIds.splice(index,1);
		}
	}else if(type == 'BRANCH'){
		index = isInArray(iden,branchIds);
		if(index > -1){
			branchIds.splice(index,1);
		}
	}else if(type == 'COMPANY'){
		$('#segment-data').data('companyId',0);
	}
	
	var count = 0;
	var comId = $('#segment-data').data('companyId');
	if(comId != 0 && !$('#company'+comId).find('.seg-checked').hasClass('hide')){
		count++;
	}
	$('#stream-segment-count').html(count+regionIds.length+branchIds.length);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

function getUsersByCompanyId(){
	var callAjax = true;
	var companyId = $("#companyId").val();
	if(sessionStorage) {
		var userList = JSON.parse(sessionStorage.getItem("sm-filter-users-"+companyId));
		if(userList){
			drawUserList(userList);
			callAjax = false;
		} 
	} 
	
	if(callAjax){
		$.ajax({
			url : "/getusersbycompanyid.do",
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(response) {
				var userList = response;
				drawUserList(userList);
				if(sessionStorage) {
					sessionStorage.setItem("sm-filter-users-"+companyId, JSON.stringify(userList));
				}
			},
			error : function(e){
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
			}
		});
	}
}

function drawUserList(userList){
	
	var usrCon = '<div id="usrOption" class="usr-dropdown-toggle stream-dropdown-option">'
			+'<div id="usr" class=" usr-option-con stream-dropdown-option-container" data-iden=0 data-regionId=0 data-branchId=0>'
			+'<img src="resources/images/check-no.png"  class="usr-unchecked hide float-left margin-right-10 cursor-pointer">'
			+'<img src="resources/images/check-yes.png"  class="usr-checked float-left margin-right-10 cursor-pointer">'
			+'<div class="usr-list-img float-left stream-dropdown-img-circle"></div>'
			+'<div class="usr-list-name float-left stream-dropdown-name-txt-bold"></div></div></div>';
	
	var userIds = [];
	
	if(userList != null && userList != undefined){
		for(var i=0; i<userList.length; i++){
			var userId = userList[i].userId;
			var regionId = userList[i].regionId;
			var branchId = userList[i].branchId;
			var name = userList[i].name;
			var profileImageUrl =userList[i].profileImageUrl;
			var isDefaultImage = false;
			
			if(profileImageUrl == null || profileImageUrl == undefined || profileImageUrl == ''){
				profileImageUrl='resources/images/no-star.png';
				isDefaultImage = true;
			}
			
			if(branchId != 0 && branchId != undefined && branchId != null && $('#bra-opt-'+branchId).length > 0){
				$('#bra-opt-'+branchId).append(usrCon);
				$('#usr').data('iden',userId);
				$('#usr').data('regionId',regionId);
				$('#usr').data('branchId',branchId);
				$('#usr').find('.usr-list-img').css("background-image", "url(" + profileImageUrl + ")");
				if(isDefaultImage){
					$('#usr').find('.usr-list-img').css('border','2px solid #e1e1e1');
				}
				$('#usr').find('.usr-list-name').html(name);
				$('#usr').attr('id','usr-'+userId);
				$('#usrOption').attr('id','usr-opt-'+userId);
			}else if(regionId != 0 && regionId != undefined && regionId != null && $('#reg-opt-'+regionId).length > 0){
				$('#reg-opt-'+regionId).append(usrCon);
				$('#usr').data('iden',userId);
				$('#usr').data('regionId',regionId);
				$('#usr').data('branchId',branchId);
				$('#usr').find('.usr-list-img').css("background-image", "url(" + profileImageUrl + ")");
				if(isDefaultImage){
					$('#usr').find('.usr-list-img').css('border','2px solid #e1e1e1');
				}
				$('#usr').find('.usr-list-name').html(name);
				$('#usr').attr('id','usr-'+userId);
				$('#usrOption').attr('id','usr-opt-'+userId);
			}else{
				$('#UsrOptions').append(usrCon);
				$('#usr').data('iden',userId);
				$('#usr').data('regionId',regionId);
				$('#usr').data('branchId',branchId);
				$('#usr').find('.usr-list-img').css("background-image", "url(" + profileImageUrl + ")");
				if(isDefaultImage){
					$('#usr').find('.usr-list-img').css('border','2px solid #e1e1e1');
				}
				$('#usr').find('.usr-list-name').html(name);
				$('#usr').attr('id','usr-'+userId);
				$('#usrOption').attr('id','usr-opt-'+userId);
			}
			
			userIds.push(userId);
		}
	}
	
	$('#stream-user-count').html(userIds.length);
	$('#usr-list-data').data('userIds',userIds);
	
	 checkUsrOptions();
		
}

$(document).on('click','.usr-seg-unchecked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.usr-seg-checked').removeClass('hide');
	
	var userIdList = $('#usr-list-data').data('userIds');
	
	$(this).parent().parent().find('.usr-seg-unchecked').each(function(){
		$(this).addClass('hide');
		$(this).parent().find('.usr-seg-checked').removeClass('hide');
	});
	
	$(this).parent().parent().find('.usr-unchecked').each(function(){
		$(this).addClass('hide');
		$(this).parent().find('.usr-checked').removeClass('hide');
		var userId = $(this).parent().data('iden');
		var index = isInArray(userId,userIdList);
		if(index == -1){
			userIdList.push(userId);
		}
	});
	
	$('#stream-user-count').html(userIdList.length);
	$('#usr-list-data').data('userIds',userIdList);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('click','.usr-seg-checked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.usr-seg-unchecked').removeClass('hide');
	
	var userIdList = $('#usr-list-data').data('userIds');
	
	$(this).parent().parent().find('.usr-seg-checked').each(function(){
		$(this).addClass('hide');
		$(this).parent().find('.usr-seg-unchecked').removeClass('hide');
	});
	
	$(this).parent().parent().find('.usr-checked').each(function(){
		$(this).addClass('hide');
		$(this).parent().find('.usr-unchecked').removeClass('hide');
		var userId = $(this).parent().data('iden');
		var index = isInArray(userId,userIdList);
		if(index > -1){
			userIdList.splice(index,1);
		}
	});
	
	$('#stream-user-count').html(userIdList.length);
	$('#usr-list-data').data('userIds',userIdList);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('click','.usr-unchecked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.usr-checked').removeClass('hide');
	$(this).parent().find('.usr-seg-partial').addClass('hide');
	
	var userIdList = $('#usr-list-data').data('userIds');
	var userId = $(this).parent().data('iden');
	
	var index = isInArray(userId,userIdList);
	if(index == -1){
		userIdList.push(userId);
	}
	
	$('#stream-user-count').html(userIdList.length);
	$('#usr-list-data').data('userIds',userIdList);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('click','.usr-checked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.usr-unchecked').removeClass('hide');
	
	var userIdList = $('#usr-list-data').data('userIds');
	var userId = $(this).parent().data('iden');
	
	var index = isInArray(userId,userIdList);
	if(index > -1){
		userIdList.splice(index,1);
	}
	
	$('#stream-user-count').html(userIdList.length);
	$('#usr-list-data').data('userIds',userIdList);
	 
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
	
});

$(document).on('click','.usr-list-chev-down',function(e){
	$(this).addClass('hide');
	$(this).parent().find('.usr-list-chev-up').removeClass('hide');
	$(this).parent().parent().find('.usr-dropdown-toggle').removeClass('hide');
	$(this).parent().parent().find('.usr-dropdown-toggle').find('.usr-list-chev-down').addClass('hide');
	$(this).parent().parent().find('.usr-dropdown-toggle').find('.usr-list-chev-up').removeClass('hide');
});

$(document).on('click','.usr-list-chev-up',function(e){
	$(this).addClass('hide');
	$(this).parent().find('.usr-list-chev-down').removeClass('hide');
	$(this).parent().parent().find('.usr-dropdown-toggle').addClass('hide');
	$(this).parent().parent().find('.usr-dropdown-toggle').find('.usr-list-chev-up').addClass('hide');
	$(this).parent().parent().find('.usr-dropdown-toggle').find('.usr-list-chev-down').removeClass('hide');
});

function checkUsrOptions(){
	$('.usr-list-opt').each(function(){
		if($(this).parent().find('.usr-dropdown-toggle').length <= 0){
			$(this).parent().addClass('hide');
			$(this).find('.usr-list-chev-down').addClass('hide');
			$(this).find('.usr-list-chev-up').addClass('hide');
		}
	})
}

$(document).on('click','.feed-unchecked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.feed-checked').removeClass('hide');
	
	var feedsList = $('#feed-data').data('feeds');
	
	var feed = $(this).parent().data('feed');
	
	var index = isInArray(feed,feedsList);
	if(index == -1){
		feedsList.push(feed);
	}
	
	$('#stream-feed-count').html(feedsList.length);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('click','.feed-checked',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$(this).addClass('hide');
	$(this).parent().find('.feed-unchecked').removeClass('hide');
	
	var feedsList = $('#feed-data').data('feeds');
	
	var feed = $(this).parent().data('feed');
	
	var index = isInArray(feed,feedsList);
	if(index > -1){
		feedsList.splice(index,1);
	}
	
	$('#stream-feed-count').html(feedsList.length);
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('click','#dismiss-duplicate-post-popup',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#duplicate-post-popup').addClass('hide');
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(false);
	$('#macro-form-is-dup').val(false);
});

function drawDuplicatePopup(){
	
	$.ajax({
		url : './showsocialduplicate.do',
		type : "GET",
		dataType : "html",
		cache : false,
		success : function(data){
			$('#dup-post-popup-body').html(data);
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function checkDupActionBtns(status,postId){
	if(status=='NEW'){
			$('.dup-stream-action-unflag').addClass('hide');
			$('.dup-stream-action-flag').removeClass('hide');
			$('.dup-stream-action-esc').removeClass('hide');
			$('.dup-stream-action-res').addClass('hide');
	}else if(status == 'ALERT'){
		$('.dup-stream-action-unflag').removeClass('hide');
		$('.dup-stream-action-flag').addClass('hide');
		$('.dup-stream-action-esc').removeClass('hide');
		$('.dup-stream-action-res').addClass('hide');
	}else if(status == 'ESCALATED'){
		$('.dup-stream-action-unflag').addClass('hide');
		$('.dup-stream-action-flag').addClass('hide');
		$('.dup-stream-action-esc').addClass('hide');
		$('.dup-stream-action-res').removeClass('hide');
	}else{
		$('.dup-stream-action-unflag').addClass('hide');
		$('.dup-stream-action-flag').addClass('hide');
		$('.dup-stream-action-esc').removeClass('hide');
		$('.dup-stream-action-res').addClass('hide');
	}
	
	drawDupMacroListDropdown(postId);
	
}

$(document).on('click','.dup-stream-action-unflag',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	var formId = 'dup-post-add-post-action';
	var disableEle = '.dup-stream-action-unflag';
	var textActionType = $('#dup-post-action-form-cont').find('.form-text-act-type').val();
	
	$(this).closest('.action-form-cont').find('.form-status').val('NEW');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonDupBtn(url,formId,postId);
	
	$('#duplicate-post-popup').addClass('hide');
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(false);
	$('#macro-form-is-dup').val(false);
});

$(document).on('click','.dup-stream-action-flag',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	var formId = 'dup-post-add-post-action';
	var disableEle = '.dup-stream-action-flag';
	var textActionType = $('#dup-post-action-form-cont').find('.form-text-act-type').val();
	
	$(this).closest('.action-form-cont').find('.form-status').val('ALERT');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonDupBtn(url,formId,postId);
	
	$('#duplicate-post-popup').addClass('hide');
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(false);
	$('#macro-form-is-dup').val(false);
});


$(document).on('click','.dup-stream-action-esc',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	var formId = 'dup-post-add-post-action';
	var disableEle = '.dup-stream-action-esc';
	var textActionType = $('#dup-post-action-form-cont').find('.form-text-act-type').val();
	var text = $('#dup-post-action-form-cont').find('.form-post-textbox').val();
	
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note. Escalation not allowed without a comment for the user");
		showToast();
		
		return;
	}
	
	$(this).closest('.action-form-cont').find('.form-status').val('ESCALATED');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonDupBtn(url,formId,postId);

	$('#duplicate-post-popup').addClass('hide');
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(false);
	$('#macro-form-is-dup').val(false);
});

$(document).on('click','.dup-stream-action-res',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	var formId = 'dup-post-add-post-action';
	var disableEle = '.dup-stream-action-res';
	var textActionType = $('#dup-post-action-form-cont').find('.form-text-act-type').val();
	var text = $('#dup-post-action-form-cont').find('.form-post-textbox').val();
	
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note. Resolution not allowed without a comment for the user");
		showToast();
		
		return;
	}
	
	$(this).closest('.action-form-cont').find('.form-status').val('RESOLVED');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonDupBtn(url,formId,postId);

	$('#duplicate-post-popup').addClass('hide');
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(false);
	$('#macro-form-is-dup').val(false);
});

$(document).on('click','.dup-stream-action-submit',function(e){
	e.stopPropagation();
	var postId = $(this).closest('.action-form-cont').find('.form-post-id').val();
	var formId = 'dup-post-add-post-action';
	var disableEle = '.dup-stream-action-submit';

	var text = $('#dup-post-action-form-cont').find('.form-post-textbox').val();
	if(text == '' || text == undefined || text == null){
		$("#overlay-toast").html("Please enter some text for Mail/Private note.");
		showToast();
		
		return;
	}
	
	$(this).closest('.action-form-cont').find('.form-status').val('SUBMIT');
	
	var url = './updatepostaction.do';
	callFormAjaxPostForSocMonDupBtn(url,formId,postId);

	$('#duplicate-post-popup').addClass('hide');
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(false);
	$('#macro-form-is-dup').val(false);
});

function drawDupMacroListDropdown(postId){
	$('#dup-post-action-form-container').find('.macro-options-list').html('');
	var macroList = macrosForStream;
	for(var m=0;m<macroList.length;m++){
		var macroId = macroList[m].macroId;
		var macroName=macroList[m].macroName;
		var active = macroList[m].active;
		var actions=macroList[m].actions;
		var status = actions.socialFeedStatus;
		var textActionType = actions.textActionType;
		var text = actions.text;
		if(text == undefined || text == null){
			text='';
		}
		
		var postStatus = $('#dup-post-add-post-action').find('.form-status').val();
		
		var inputDiv = '<input type="hidden" id="dup-macro-'+postId+'-'+macroId+'" class="macro-list-data" data-post-id="'+postId+'" data-macro-id="'+macroId
		+'" data-macro-name="'+macroName+'" data-status="'+status+'" data-text-action-type="'+textActionType+'" data-text="'+text+'">';
		var macroDiv = '<div id="dup-macro-option-'+macroId+'" data-macro-id="'+macroId+'" class="dup-macro-opt">'+inputDiv+macroName+'</div>';
		var macroDivDisabled = '<div id="dup-macro-option-'+macroId+'" data-macro-id="'+macroId+'" class="macro-disabled">'+inputDiv+macroName+'</div>';
		
		var disableMacro = false;
		if(active == true || active == 'true'){
			if((postStatus == 'RESOLVED')){
				if(status != 'NEW' && status != 'ALERT' && status != 'RESOLVED'){	
					$('#dup-post-action-form-container').find('.macro-options-list').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else if(postStatus == 'ESCALATED'){
				if(status != 'NEW' && status != 'ALERT' && status != 'ESCALATED'){	
					$('#dup-post-action-form-container').find('.macro-options-list').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}else if(postStatus == 'NEW'){
				if(status != 'ALERT' && status != "RESOLVED"){
					$('#dup-post-action-form-container').find('.macro-options-list').append(macroDiv);
				}else{
						disableMacro = true;
				}
			}else if(postStatus == 'ALERT'){
				if(status != 'ALERT' && status != "RESOLVED"){
					$('#dup-post-action-form-container').find('.macro-options-list').append(macroDiv);
				}else{
					disableMacro = true;
				}
			}
		}
		
		if(disableMacro){
			$('#dup-post-action-form-container').find('.macro-options-list').append(macroDivDisabled);
		}
	}
}

$(document).on('click','.dup-macro-opt',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var postId =  $(this).find('.macro-list-data').attr('data-post-id');
	var macroId = $(this).find('.macro-list-data').attr('data-macro-id');
	var macroName =$(this).find('.macro-list-data').attr('data-macro-name');
	var status = $(this).find('.macro-list-data').attr('data-status');
	var textActionType = $(this).find('.macro-list-data').attr('data-text-action-type');
	var text = $(this).find('.macro-list-data').attr('data-text');
	
	if(text == undefined || text == null){
		text='';
	}
	
	$('#macro-form-post-id').val(postId);
	$('#macro-form-status').val(status);
	$('#macro-form-text-act-type').val(textActionType);
	$('#macro-form-macro-id').val(macroId);
	$('#macro-form-text').val(text);
	
	$('#add-post-action-'+postId).find('.form-status').val(status);
	
	$('#dup-post-action-form-cont').find('.form-status').val(status);
	
	var url = './updatepostactionwithmacro.do';
	callFormAjaxPostForSocMonDupBtn(url,'macro-form-apply',postId);
	
	$('#duplicate-post-popup').addClass('hide');
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(false);
	$('#macro-form-is-dup').val(false);
	
	if($(this).closest('.stream-macro-dropdown').find('.mac-chevron-down').hasClass('hide')){
		$(this).closest('.stream-macro-dropdown').find('.mac-chevron-down').removeClass('hide');
	}else{
		$(this).closest('.stream-macro-dropdown').find('.mac-chevron-down').addClass('hide');
	}
	
	if($(this).closest('.stream-macro-dropdown').find('.mac-chevron-up').hasClass('hide')){
		$(this).closest('.stream-macro-dropdown').find('.mac-chevron-up').removeClass('hide');
	}else{
		$(this).closest('.stream-macro-dropdown').find('.mac-chevron-up').addClass('hide');
	}
	$(this).closest('.macro-options-list').addClass('hide');
});

$(document).on('click','.macro-disabled',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
});

$(document).on('input','#search-post',function(){
	$('#soc-mon-stream-search-clr').show();
});

$(document).on('click','#soc-mon-stream-search-clr',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#search-post').val('');
	$(this).hide();
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	
	getStreamPosts(0, status);
});

$(document).on('click','#soc-mon-stream-search-icn',function(e){
	
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	var text = $('#search-post').val();
	
	getStreamPosts(0, status, text);
});

$(document).on('keyup','#search-post', function(e){
	if(e.keyCode == 13){
		var status = $('#stream-tabs').data('status');
		var startIndex = $('#stream-pagination').data('startIndex');
		var text = $('#search-post').val();
		
		getStreamPosts(0, status, text);
	}else if(e.keyCode == 27){
		
		$('#search-post').val('');
		$('#soc-mon-stream-search-clr').hide();
		
		var status = $('#stream-tabs').data('status');
		var startIndex = $('#stream-pagination').data('startIndex');
		
		getStreamPosts(0, status);
	}
});

$(document).on('input','#search-macro',function(){
	$('#soc-mon-macro-search-clr').show();
});

$(document).on('click','#soc-mon-macro-search-clr',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#search-macro').val('');
	$(this).hide();
	
	getMacros();
});

$(document).on('click','#soc-mon-macro-search-icn',function(e){
	
	var text = $('#search-macro').val();
	
	getMacros(text);
});

$(document).on('keyup','#search-macro', function(e){
	if(e.keyCode == 13){
		var text = $('#search-macro').val();
		
		getMacros(text);
		
	}else if(e.keyCode == 27){
		
		$('#search-macro').val('');
		$('#soc-mon-macro-search-clr').hide();
		
		getMacros();
	}
});

$(document).on('input','#search-monitors-key',function(){
	$('#soc-mon-search-clr').show();
});

$(document).on('click','#soc-mon-search-clr',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#search-monitors-key').val('');
	$(this).hide();
	
	getMonitors();
});

$(document).on('click','#soc-mon-search-icn',function(e){
	
	var text = $('#search-monitors-key').val();
	
	getMonitors(text);
});

$(document).on('keyup','#search-monitors-key', function(e){
	if(e.keyCode == 13){
		var text = $('#search-monitors-key').val();
		
		getMonitors(text);
		
	}else if(e.keyCode == 27){
		
		$('#search-monitors-key').val('');
		$('#soc-mon-search-clr').hide();
		
		getMonitors();
	}
});

function deleteMonitors(){
	
	$('#selectedMonitors').val( $('#selectedMonitors').data('idList'));
	
	var selectedIds =$('#selectedMonitors').val();
	var url = './deletemonitorsbyid.do';
	
	var formData = new FormData();
	formData.append("monitorIds", selectedIds);
	
	$.ajax({
		url : url,
		type : "POST",
		dataType : "text",
		contentType : false,
		processData : false,
		cache : false,
		data : formData,
		success : function(data){
			var map = $.parseJSON(data);
			
			if (map.status == "success") {
				$('#overlay-toast').html('Successfully deleted selected Monitors.');
				showToast();
				var monitorData = JSON.parse(map.keywords);
				drawMonitorList(monitorData);
			}else{
				$('#overlay-toast').html('Failed to delete selected Monitors.');
				showToast();
			}
		},
		error : function(e) {
			$('#overlay-toast').html('Failed to delete selected Monitors.');
			showToast();
			
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
	
}

$(document).on('click','#monitor-bulk-actions',function(e){
	e.stopPropagation();
	
	$('#monitor-bulk-action-options').toggle();
	$('#monitor-chevron-down').toggle();
	$('#monitor-chevron-up').toggle();
	
	if ($('#mon-type-options').is(':visible')) {
		$('#mon-type-options').toggle();
		$('#mon-type-chevron-down').toggle();
		$('#mon-type-chevron-up').toggle();	
	}
	
});

$(document).on('click','#monitor-bulk-delete',function(e){
	e.stopPropagation();
	
	deleteMonitors();
	
	$('#monitor-bulk-action-options').toggle();
	$('#monitor-chevron-down').toggle();
	$('#monitor-chevron-up').toggle();
});

$(document).on('click','.stream-feed-type',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var pageLink = $(this).attr('data-link');
	
	if(!/^(http|https):\/\//.test(pageLink)){
		pageLink = "//" + pageLink;
    }
	var win = window.open(pageLink, '_blank');
	
	if (win) {
	   win.focus();
	} else {
		$('#overlay-toast').html('Failed to open page. Please allow popups for this page.');
		showToast();
	}
});

function getFeedTypes(){
		$.ajax({
			url : "/getfeedsbycompanyid.do",
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(response) {
				var feedData = response;
				checkFeedTypes(feedData);
			},
			error : function(e){
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
			}
		});
}

function checkFeedTypes(feedData){
	
	var feedTypes = [];
	if(feedData.facebook == true || feedData.facebook == 'true'){
		$('#feed-facebook').removeClass('hide');
		feedTypes.push("FACEBOOK");
	}
	
	if(feedData.twitter == true || feedData.twitter == 'true'){
		$('#feed-twitter').removeClass('hide');
		feedTypes.push("TWITTER");
	}
	
	if(feedData.linkedin == true || feedData.linkedin == 'true'){
		$('#feed-linkedin').addClass('hide');
//		feedTypes.push("LINKEDIN");
	}
	
	if(feedData.instagram == true || feedData.instagram == 'true'){
		$('#feed-instagram').removeClass('hide');
		feedTypes.push("INSTAGRAM");
	}
	
	if(feedTypes.length == 0){
		$('#feed-data').data('feeds',feedTypes);
		$('#stream-feed-count').html(0);
		$('#feed-empty').removeClass('hide');
		$('#stream-feed-dropdown-options').css('width',$('#stream-feed-dropdown').css('width'))
	}else{
		$('#feed-data').data('feeds',feedTypes);
		$('#stream-feed-count').html(feedTypes.length);
	}
}

$('body').on('blur', '#user-notification-recipients', function() {
	
	// format email IDs
	var emails = $("#user-notification-recipients").val();
	
	if( emails == undefined ){
		return;
	}
	
	var payload = {
		"emails" : emails
	};
	
	callAjaxPostWithPayloadData("./updateadddeletenotifyrecipients.do", function(data) {
		$('#overlay-toast').html(data);
		showToast();
	}, payload, true);
	
});


$(document).on('click','#action-edit-txt-box',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
});

function showSummitPopup(){
	$('#summit-popup-outer').show();
}

function closeSummitPopup(){
	$('#summit-popup-outer').hide();
}


function showSummitRibbon(){
	$('#summit-ribbon-outer').show();
}

function closeSummitRibbon(){
	
	$('#summit-ribbon-outer').hide();
}

function showLinkedinApiV2UpdateRibbon(){
	$('#linkedin-api-v2-update-ribbon-outer').show();
}

function closeLinkedinApiV2UpdateRibbon(){
	$('#linkedin-api-v2-update-ribbon-outer').hide();
}

$(document).on('click','#summit-popup-close-btn',function(e){
	e.stopPropagation();
	closeSummitPopup();
});

$(document).on('click','#summit-ribbon-outer',function(e){
	e.stopPropagation();
	e.stopImmediatePropagation();
	e.preventDefault();

	var profileMasterId = parseInt($('#rep-prof-container').attr('data-profile-master-id'));
	
	window.open('https://www.socialsurvey.com/top-performers-2018/', '_blank');
});

$(document).on('click','#linkedin-api-v2-update-ribbon-close-btn',function(e){
	e.stopPropagation();
	closeLinkedinApiV2UpdateRibbon();
});

$(document).on('click','#summit-ribbon-close-btn',function(e){
	e.stopPropagation();
		
	closeSummitRibbon();
});

$(document).on('click','#summit-popup-body',function(e){
	e.stopPropagation();
	e.stopImmediatePropagation();
	e.preventDefault();
	
	closeSummitPopup();
	
	var profileMasterId = parseInt($('#rep-prof-container').attr('data-profile-master-id'));
	
	window.open('https://www.socialsurvey.com/top-performers-2018/', '_blank');
});

function sendClickedEventInfo( event ){
	if( event != undefined ){
		// send the type of event that a user has triggered
		$.ajax({
			url : "./user/trackedevents/click.do",
			type : "POST",
			data : {"event" : String(event)},
			async : true
		});
	}
}

function getRecentActivityCountForSocialMonitor(){
	var recentActivityCount=0;
	$.ajax({
		url : "/fetchrecentactivitiescountforsocialmonitor.do",
		type : "GET",
		cache : false,
		async : false,
		dataType : "json",
		success : function(response) {
			recentActivityCount = parseInt(response);
			$('#soc-mon-rec-act-data').attr('data-count',recentActivityCount);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			recentActivityCount = 0;
			$('#soc-mon-rec-act-data').attr('data-count',recentActivityCount);
		}
	});
}

function getRecentActivityListForSocialMonitor(startIndex, batchSize,tableHeaderData,recentActivityCount){
	
	var recentActivityList=null;
	var payload={
			"startIndex" : startIndex,
			"batchSize" : batchSize
	}
	$.ajax({
		url : "/fetchrecentactivitiesforsocialmonitor.do?startIndex="+payload.startIndex+"&batchSize="+payload.batchSize,
		type : "GET",
		cache : false,
		dataType : "json",
		success :function(data){
			drawRecentActivityForSocialMonitor(data,startIndex, batchSize,tableHeaderData,recentActivityCount);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function setHasRegisteredForSummit(hasRegisteredForSummit){
	
	var payload = {
			"hasRegisteredForSummit" : hasRegisteredForSummit
	}
	
	var url = './sethasregisteredforsummit.do';
	
	$.ajax({
		url : url,
		type : "POST",
		data : payload,
		success : function(data){
			console.log(data);
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
	
}

function setShowSummitPopup(isShowSummitPopup){
	
	var payload = {
			"isShowSummitPopup" : isShowSummitPopup
	}
	
	var url = './setshowsummitpopup.do';
	
	$.ajax({
		url : url,
		type : "POST",
		data : payload,
		success : function(data){
			console.log(data);
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});	
}

function drawRecentActivityForSocialMonitor(data,startIndex, batchSize,tableHeaderData,recentActivityCount){
	var tableData=''; 
	var recentActivityList = JSON.parse(data);
	
	var curDate = new Date();
	var curYear = curDate.getFullYear();
	
	for(var i=0;i<recentActivityList.length;i++){
		
		var statusString = getStatusString(recentActivityList[i][6]);
		var startDate = getDateFromDateTime(recentActivityList[i][2]);
		var endDate =getDateFromDateTime(recentActivityList[i][3]);
		var monthStartDate = getMonthFromDateTime(recentActivityList[i][2]);
		var downloadLink = recentActivityList[i][7];
		var fileUploadId = recentActivityList[i][8];
		var reportType = recentActivityList[i][9];
		
		tableData += "<tr id='soc-mon-recent-activity-row"+i+"' class=\"u-tbl-row user-row \">"
			+"<td data-file-upload-id='"+fileUploadId+"' data-link='"+downloadLink+"' class=\"v-tbl-recent-activity fetch-name soc-mon-file-details hide\">"+i+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][0]+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-blue-text\">"+recentActivityList[i][1]+"</td>";
			
		if(recentActivityList[i][1] == 'Social Monitor Date based Report' || recentActivityList[i][1] == 'Social Monitor Date Report with keyword'){
				tableData += '<td class="v-tbl-recent-activity fetch-email txt-bold tbl-black-text ">';
				tableData += startDate + ' - ' + endDate;
				tableData += "</td>";
		}else{
			tableData += "<td class=\"v-tbl-recent-activity fetch-email txt-bold tbl-black-text "+(startDate==null?("\">"+"All Time till date "):("\">"+(endDate==null?monthStartDate:startDate)))+(endDate==null?" ":" - "+endDate)+"</td>";
		}
		
		tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][4]+" "+recentActivityList[i][5]+"</td>";
		
		if(recentActivityList[i][6]==0){	
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'><a id=\"downloadLink"+i+"\"class='txt-bold tbl-blue-text socMondownloadLink cursor-pointer'>"+statusString+"</a></td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold soc-mon-recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		}else if(recentActivityList[i][6]==3){
		  	tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'> No records found </td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold soc-mon-recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		  }else if(recentActivityList[i][6]==4){
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'>"+statusString+"</td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold\" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold soc-mon-recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		} else if(recentActivityList[i][6]==5){	
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'><a id=\"viewLink"+i+"\"class='txt-bold tbl-blue-text socMondownloadLink cursor-pointer'>"+statusString+"</a></td>"
			+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row"+i+"\" class='txt-bold soc-mon-recent-act-delete-x cursor-pointer'>X</a></td>"
			+"</tr>";
		}else{
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important;'>"+statusString+"</td>"
				+"<td class=\"v-tbl-recent-activity fetch-name txt-bold\" >  </td>"
				+"</tr>";
		}
	}
	
	var recentActivityCount = parseInt($('#soc-mon-rec-act-data').attr('data-count'));
	if(recentActivityCount == 0){
		tableData='';
		tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
		$('#soc-mon-recent-activity-list-table').html(tableData);
	}else{
		$('#soc-mon-recent-activity-list-table').html(tableHeaderData+tableData+"</table>");
	}
	
	$('#soc-mon-rec-act-data').attr('data-start-index',startIndex);
}

function showHidePaginateButtonsForSocialMonitor(startIndex,recentActivityCount){

	if(startIndex == 0){
		$('#soc-mon-rec-act-page-previous').hide();
	}else{
		$('#soc-mon-rec-act-page-previous').show();
	}
	
	if((recentActivityCount-startIndex)<=10){
		$('#soc-mon-rec-act-page-next').hide();
	}else{
		$('#soc-mon-rec-act-page-next').show();
	}
	
	if(recentActivityCount == 0){
		$('#soc-mon-rec-act-page-previous').hide();
		$('#soc-mon-rec-act-page-next').hide();
	}
}

$(document).on('click','.socMondownloadLink',function(e){
	var clickedID = this.id;
	var indexRecentActivity = clickedID.match(/\d+$/)[0];
	var downloadLink=$('#soc-mon-recent-activity-row'+indexRecentActivity).find('.soc-mon-file-details').attr('data-link');

	window.location=downloadLink;
});

$(document).on('click','.soc-mon-recent-act-delete-x',function(e){
	showOverlay();
	var clickedID = this.id;
	var indexRecentActivity = clickedID.match(/\d+$/)[0];
	var fileUploadId=parseInt($('#soc-mon-recent-activity-row'+indexRecentActivity).find('.soc-mon-file-details').attr('data-file-upload-id'));
	deleteRecentActivityForSocialMonitor(fileUploadId, indexRecentActivity);
});

function deleteRecentActivityForSocialMonitor(fileUploadId,idIndex){
	showOverlay();
	var tableHeaderData="<table class=\"v-um-tbl\" style=\"margin-bottom:15px\" >"
		+"<tr id=\"u-tbl-header\" class=\"u-tbl-header\">"
		+"<td class=\"v-tbl-recent-activity \">Requested On</td>"
		+"<td class=\"v-tbl-recent-activity\">Report</td>"
		+"<td class=\"v-tbl-recent-activity\" \>Date Range</td>"
		+"<td class=\"v-tbl-recent-activity \">Requested By</td>"
		+"<td class=\"v-tbl-recent-activity\" style='width:25%'>Status</td>"
		+"<td class=\"v-tbl-recent-activity \"></td>"
		+"</tr>";
	
	$.ajax({
		url : "./deletefromrecentactivities.do?fileUploadId="+fileUploadId,
		type : "POST",
		dataType:"TEXT",
		async:false,
		success : function(data) {
			success=true;
			messageToDisplay = data;
			
		},
		complete : function() {	
			hideOverlay();
			
			getRecentActivityCountForSocialMonitor();
			var recentActivityCount= parseInt($('#soc-mon-rec-act-data').attr('data-count'));
			var startIndex=parseInt($('#soc-mon-rec-act-data').attr('data-start-index'));
			
			$('#soc-mon-recent-activity-row'+idIndex).fadeOut(500)
				.promise()
				.done(function(){
					if(recentActivityCount <= startIndex){
						getRecentActivityListForSocialMonitor(startIndex-10, 10,tableHeaderData,recentActivityCount);
					}else if(recentActivityCount>=10){
						getRecentActivityListForSocialMonitor(startIndex, 10,tableHeaderData,recentActivityCount);
					}
					showHidePaginateButtonsForSocialMonitor(startIndex, recentActivityCount);
					
					if(recentActivityCount == 0){
						var tableData='';
						tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
						$('#soc-mon-recent-activity-list-table').html(tableData);
					}
				});
			},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			messageToDisplay="Sorry! Failed to delete the activity. Please try again later";
			showError(messageToDisplay);
		}
	});
}

$(document).on('click', '#soc-mon-reports-generate-report-btn', function(e) {
	
	var selectedValue = $('#generate-survey-reports').val();
	var key = parseInt(selectedValue);
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	var digestMonthValue = 0;
	var npsTimeFrame = parseInt($('#nps-report-time-selector').val());
	var d = new Date();
	var clientTimeZone = d.getTimezoneOffset();
	var keywordStr = document.getElementById('sm-keywords-selector');
	var keyword = keywordStr.options[keywordStr.selectedIndex].text;
	var keywordValue = parseInt($('#sm-keywords-selector').val());
	
	if(isNaN(keywordValue) && key == 302){
		$('#overlay-toast').html("Please select a keyword");
		showToast();
		return;
	}
	
	var success = false;
	var messageToDisplay;
	var payload = {
			"startDate" : startDate,
			"endDate" : endDate,
			"reportId" : key,
			"clientTimeZone": clientTimeZone,
			"keyword": keyword,
		};
	
	showOverlay();	
		$.ajax({
			url : "./savereportingdata.do",
			type : "POST",
			data: payload,
			dataType:"TEXT",
			async:false,
			success : function(data) {
				success=true;
				messageToDisplay = data;
				showInfoForReporting(messageToDisplay);
			},
			complete : function() {	
				hideOverlay();
				
				var tableHeaderData="<table class=\"v-um-tbl\" style=\"margin-bottom:15px\" >"
					+"<tr id=\"u-tbl-header\" class=\"u-tbl-header\">"
					+"<td class=\"v-tbl-recent-activity \">Requested On</td>"
					+"<td class=\"v-tbl-recent-activity\">Report</td>"
					+"<td class=\"v-tbl-recent-activity\" \>Date Range</td>"
					+"<td class=\"v-tbl-recent-activity \">Requested By</td>"
					+"<td class=\"v-tbl-recent-activity\" style='width:25%'>Status</td>"
					+"<td class=\"v-tbl-recent-activity \"></td>"
					+"</tr>";
				
				getRecentActivityCountForSocialMonitor();
				var recentActivityCount= parseInt($('#soc-mon-rec-act-data').attr('data-count'));
				
				getRecentActivityListForSocialMonitor(0, 10,tableHeaderData,recentActivityCount);
				showHidePaginateButtonsForSocialMonitor(0, recentActivityCount);
				
				if(recentActivityCount == 0){
					var tableData='';
					tableData+="</table><div style='text-align:center; margin:20px auto'><span class='incomplete-trans-span'>There are No Recent Activities</span></div>";
					$('#soc-mon-recent-activity-list-table').html(tableData);
				}
			},
			error : function(e) {
				showError("Your request could not be processed at the moment. Please try again later!");
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
			}
		});
});

$(document).on('click','.ts-add',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var trustedSource = $(this).parent().find('.ts-source').attr('data-source');
	addTrustedSource(trustedSource,$(this));
});

function addTrustedSource(trustedSource,element){
	
	var payload = {"trustedSource" : trustedSource};
	var fromTrustedSource = element.parent().find('.ts-source').attr('data-trusted');
	
	if(fromTrustedSource == true || fromTrustedSource == "true"){
		$('#overlay-toast').html("This is already a trusted source.");
		showToast();
		return;
	}
	
	$.ajax({
		url : './addtrustedsource.do',
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payload,
		success : function(data){
			var response = JSON.parse(data);
			
			$('#overlay-toast').html(response.message);
			
			if(response.status == 'success'){
				element.parent().find('.ts-source').attr('data-trusted',true);
				element.removeClass('ts-add');
				element.addClass('ts-remove');
				element.parent().find('.trusted-source').removeClass('hide');
			}
			showToast();
		},
		complete:refreshPostsForTrustedSource,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html("Failed to add trusted source");
			showToast();
		}
	});
}

$(document).on('click','.ts-remove',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var trustedSource = $(this).parent().find('.ts-source').attr('data-source');
	removeTrustedSource(trustedSource,$(this));
});

function removeTrustedSource(trustedSource,element){
	
	var payload = {"trustedSource" : trustedSource};
	var fromTrustedSource = element.parent().find('.ts-source').attr('data-trusted');
	
	if(fromTrustedSource == false || fromTrustedSource == "false"){
		$('#overlay-toast').html("This is not a trusted source.");
		showToast();
		return;
	}
	
	$.ajax({
		url : './removetrustedsource.do',
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payload,
		success : function(data){
			var response = JSON.parse(data);
			$('#overlay-toast').html(response.message);
			
			if(response.status == 'success'){
				element.parent().find('.ts-source').attr('data-trusted',false);
				element.addClass('ts-add');
				element.removeClass('ts-remove');
				element.parent().find('.trusted-source').addClass('hide');
			}
			showToast();

		},
		complete:refreshPostsForTrustedSource,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html("Failed to remove trusted source");
			showToast();
		}
	});
	
}

function refreshPostsForTrustedSource(){
	var status = $('#stream-tabs').data('status');
	var startIndex = $('#stream-pagination').data('startIndex');
	getStreamPosts(startIndex,status);
}

$(document).on('click','#summit-do-not-show',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var checked = $(this).attr('data-checked');
	if(checked == true || checked == 'true'){
		 $(this).attr('data-checked',false);
		 $(this).removeClass('summit-checked');
		 setShowSummitPopup(false);
	}else{
		$(this).attr('data-checked',true);
		 $(this).addClass('summit-checked');
		 setShowSummitPopup(true);
	}
});

$(document).on('click','#summit-already-reg',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	var checked = $(this).attr('data-checked');
	if(checked == true || checked == 'true'){
		 $(this).attr('data-checked',false);
		 $(this).removeClass('summit-checked');
		 setHasRegisteredForSummit(false);
	}else{
		$(this).attr('data-checked',true);
		 $(this).addClass('summit-checked');
		 setHasRegisteredForSummit(true);
	}
});

function setActiveSessionForPopup(){
	
	var url = './setactivesessionforpopup.do';
	
	$.ajax({
		url : url,
		type : "POST",
		success : function(data){
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function getDuplicatePosts(postId){
	
	var payload = {
			"postId" : postId
	}
	
	$.ajax({
		url : "/getduplicatesbypostid.do",
		type : "GET",
		cache : false,
		dataType : "json",
		data: payload,
		success : function(response) {
			var duplicatePosts = response;
			drawDuplicatePopupDetails(duplicatePosts,postId);
		},
		complete:function(){
			hideDashOverlay('#dup-dash');
		},
		error : function(e){
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
} 

$(document).on('click','.post-dup',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	
	$('#duplicate-post-popup').removeClass('hide');
	showDashOverlay('#dup-dash');
	
	var postId = $(this).parent().parent().parent().find('.post-id-details').attr('data-post-id');
	
	getDuplicatePosts(postId);
});

function drawDuplicatePopupDetails(postDetails,mainPostId){
	
	$('#dup-post-details-cont').html('');
	
	$('#dup-post-cont').attr('data-count',postDetails.socialMonitorFeedData.length);
	
	for(var i=0;i<postDetails.socialMonitorFeedData.length;i++){
		var post = postDetails.socialMonitorFeedData[i];
		
		var postId = post.postId;
		var text = post.textHighlighted;
		var pictures = post.pictures;
		var status = post.status;
		var ownerProfileImage = post.ownerProfileImage;
		var ownerName = post.ownerName;
		var type = post.type;
		
		var lastUpdated = parseInt(post.updatedOn);
		var lastUpdatedDate = new Date(lastUpdated);
		
		var monthName=new Array("January","February","March","April","May","June","July","August","September","October","November","December");
		var lastUpdatedDateStr = monthName[lastUpdatedDate.getMonth()]+' '+(lastUpdatedDate.getDate()%10!=lastUpdatedDate.getDate()?lastUpdatedDate.getDate():('0'+lastUpdatedDate.getDate()))+', '+lastUpdatedDate.getFullYear()
								+' at '+(lastUpdatedDate.getHours()%10!=lastUpdatedDate.getHours()?lastUpdatedDate.getHours():('0'+lastUpdatedDate.getHours()))+':'
								+(lastUpdatedDate.getMinutes()%10!=lastUpdatedDate.getMinutes()?lastUpdatedDate.getMinutes():('0'+lastUpdatedDate.getMinutes()))+':'
								+(lastUpdatedDate.getSeconds()%10!=lastUpdatedDate.getSeconds()?lastUpdatedDate.getSeconds():('0'+lastUpdatedDate.getSeconds()));
		
		
		$('#dup-post-details-cont').append(dupContainer);
		
		$('#stream-dup-cont').attr('id','stream-dup-cont-'+postId);
		
		if(i%2 != 0){
			$('#stream-dup-cont-'+postId).addClass('stream-container-gray');
		}
		
		$('#stream-dup-cont-'+postId).find('.post-id-details').data('post-id',postId);
		
		if(status == 'NEW'){
			$('#stream-dup-cont-'+postId).find('.dup-stream-unflagged-icn').removeClass('hide');
		}else if(status == 'ALERT'){
			$('#stream-dup-cont-'+postId).find('.dup-stream-flagged-icn').removeClass('hide');
		}else if(status == 'ESCALATED'){
			$('#stream-dup-cont-'+postId).find('.dup-stream-esc-icn').removeClass('hide');
		}else if(status == 'RESOLVED'){
			$('#stream-dup-cont-'+postId).find('.dup-stream-res-icn').removeClass('hide');
		}
		
		$('#stream-dup-cont-'+postId).find('.stream-res-icn').attr('src',ownerProfileImage);
		
		if(type == 'FACEBOOK'){
			$('#stream-dup-cont-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/ss-icon-small-facebook.png');
		}else if(type == 'LINKEDIN'){
			$('#stream-dup-cont-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/ss-icon-small-linkedin.png');
		}else if(type == 'TWITTER'){
			$('#stream-dup-cont-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/ss-icon-small-twitter.png');
		}else if(type == 'INSTAGRAM'){
			$('#stream-dup-cont-'+postId).find('.stream-res-feed-icn').attr('src','resources/images/social_instagram.png');
			$('#stream-dup-cont-'+postId).find('.stream-res-feed-icn').addClass('stream-feed-insta');
		}
		
		$('#stream-dup-cont-'+postId).find('.stream-user-name').html(ownerName);
		$('#stream-dup-cont-'+postId).find('.stream-feed-type').html(type);
		$('#stream-dup-cont-'+postId).find('.stream-post-date').html(lastUpdatedDateStr);
		
		var postSource = post.postSource;
		var fromTrustedSource = post.fromTrustedSource;
		
		if(postSource != "" && postSource != null && postSource != undefined){
			$('#stream-dup-cont-'+postId).find('.ts-container').removeClass('hide');
			$('#stream-dup-cont-'+postId).find('.ts-source').html(postSource);
			$('#stream-dup-cont-'+postId).find('.ts-source').attr('data-trusted',fromTrustedSource);
			$('#stream-dup-cont-'+postId).find('.ts-source').attr('data-source',postSource);
			
			if(fromTrustedSource == true || fromTrustedSource == 'true'){
				$('#stream-dup-cont-'+postId).find('.ts-act-icon').removeClass('ts-add');
				$('#stream-dup-cont-'+postId).find('.ts-act-icon').addClass('ts-remove');
				$('#stream-dup-cont-'+postId).find('.trusted-source').removeClass('hide');
			}
		}
		
		$('#stream-dup-cont-'+postId).find('.email-reply-text').html(text);
		
		if(pictures != null && pictures != undefined){
			for(var picI=0; picI<pictures.length; picI++){
				
				if(pictures[picI] != null && pictures[picI] != undefined && pictures[picI] != ''){
					var picContainer = '<div class="col-lg-10 col-md-10 col-sm-10 col-xs-10 float-right stream-post-pic-div" >'
			   			+'<img src="'+pictures[picI]+'" class="stream-post-details-pic float-left stream-post-pic"></div>';
		
					$('#stream-dup-cont-'+postId).append(picContainer);
				}
			}	
		}
		
	}
	
	var status = $('#add-post-action-'+mainPostId).find('.form-status').val();
	
	$('#dup-post-add-post-action').find('.form-is-dup').val(true);
	$('#macro-form-is-dup').val(true);
	$('#dup-post-add-post-action').find('.form-post-id').val(mainPostId);
	$('#dup-post-add-post-action').find('.form-status').val(status);
	
}

function paginateSocialMonitor() {
	var pageNo = parseInt($('#sel-page-soc-mon').val());
	var startIndex = 0;
	var batchSize = SOCIAL_MONITOR_PAGE_SIZE;
	startIndex = parseInt(pageNo - 1) * batchSize;
	var totalCount = parseInt($('#stream-pagination').data('count'));
	var pageCount = Math.ceil(totalCount/SOCIAL_MONITOR_PAGE_SIZE);
	//Set bottom count to the same value
	$('#sel-page-soc-mon-bottom').val(pageNo);
	
	var status = $('#stream-tabs').data('status');
	var text = $('#search-post').val();
	
	if (pageNo == NaN || pageNo <= 0) {
		getStreamPosts(0, status, text);
	}
	else if(pageNo > pageCount){
		startIndex = parseInt(pageCount - 1) * batchSize;
		getStreamPosts(startIndex, status, text);
	}
	else {
		getStreamPosts(startIndex, status, text);
	}
}

function paginateSocialMonitorBottom() {
	var pageNo = parseInt($('#sel-page-soc-mon-bottom').val());
	var startIndex = 0;
	var batchSize = SOCIAL_MONITOR_PAGE_SIZE;
	startIndex = parseInt(pageNo - 1) * batchSize;
	var totalCount = parseInt($('#stream-pagination-bottom').data('count'));
	var pageCount = Math.ceil(totalCount/SOCIAL_MONITOR_PAGE_SIZE);
	//Set top count to the same value
	$('#sel-page-soc-mon').val(pageNo);
	
	var status = $('#stream-tabs').data('status');
	var text = $('#search-post').val();
	
	if (pageNo == NaN || pageNo <= 0) {
		getStreamPosts(0, status, text);
	}
	else if(pageNo > pageCount){
		startIndex = parseInt(pageCount - 1) * batchSize;
		getStreamPosts(startIndex, status, text);
	}
	else {
		getStreamPosts(startIndex, status, text);
	}
	
}

$(document).on('keyup', '#sel-page-soc-mon', function(e) {
	if (e.which == 13) {
		paginateSocialMonitor();
	}
});

$(document).on('change', '#sel-page-soc-mon', function(e) {
		paginateSocialMonitor();
});	

$(document).on('keypress', '#sel-page-soc-mon', function(e) {
	// if the letter is not digit then don't type anything
	if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		return false;
	}
});

$(document).on('keyup', '#sel-page-soc-mon-bottom', function(e) {
	if (e.which == 13) {
		paginateSocialMonitorBottom();
	}
});

$(document).on('change', '#sel-page-soc-mon-bottom', function(e) {
		paginateSocialMonitorBottom();
});	

$(document).on('keypress', '#sel-page-soc-mon-bottom', function(e) {
	// if the letter is not digit then don't type anything
	if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		return false;
	}
	
});

$(document).on('click','#web-address-work',function(e){
	e.stopPropagation();
	if($('#web-address-work-lock').attr('data-state')=='locked'){
		$('#overlay-toast').html('Web address has been locked by your company. Contact admin');
		showToast();
	}
});

$(document).on('click','#phone-number-work',function(e){
	e.stopPropagation();
	if($('#phone-number-work-lock').attr('data-state')=='locked'){
		$('#overlay-toast').html('Phone number has been locked by your company. Contact admin');
		showToast();
	}
});


function initSpectrum(elementJQObject, color, allowEmpty, changeFn){
	elementJQObject.spectrum({
	    color: color,
	    flat: false,
	    showInput: true,
	    showInitial: true,
	    allowEmpty: allowEmpty,
	    showAlpha: false,
	    disabled: false,
	    localStorageKey: "widget",
	    showPalette: true,
	    showPaletteOnly: true,
	    togglePaletteOnly: true,
	    showSelectionPalette: true,
	    clickoutFiresChange: true,
	    cancelText: "cancel",
	    chooseText: "pick",
	    togglePaletteMoreText: "advanced",
	    togglePaletteLessText: "basic",
	    containerClassName: "widget-cp-cont",
	    replacerClassName: "widget-rp-cp-cont",
	    preferredFormat: "hex",
	    maxSelectionSize: 5,
	    palette: [        
	    	["#000","#444","#666","#999","#ccc","#eee","#f3f3f3","#fff"],
	        ["#f00","#f90","#ff0","#0f0","#0ff","#00f","#90f","#f0f"],
	        ["#f4cccc","#fce5cd","#fff2cc","#d9ead3","#d0e0e3","#cfe2f3","#d9d2e9","#ead1dc"],
	        ["#ea9999","#f9cb9c","#ffe599","#b6d7a8","#a2c4c9","#9fc5e8","#b4a7d6","#d5a6bd"],
	        ["#e06666","#f6b26b","#ffd966","#93c47d","#76a5af","#6fa8dc","#8e7cc3","#c27ba0"],
	        ["#c00","#e69138","#f1c232","#6aa84f","#45818e","#3d85c6","#674ea7","#a64d79"],
	        ["#900","#b45f06","#bf9000","#38761d","#134f5c","#0b5394","#351c75","#741b47"],
	        ["#600","#783f04","#7f6000","#274e13","#0c343d","#073763","#20124d","#4c1130"]],
	    selectionPalette: ["red", "green", "blue", "gold"],
	    change : changeFn
	});
}

function initFontPicker(targetInputTagJQObject, font, changeFn ) {
	targetInputTagJQObject.fontSelector({
		'hide_fallbacks' : true,
		'initial' : font,
		'fonts' : getFont(),
		'selected' : changeFn
	});
}

function getFont(font){
	
	var fonts = [
		'Arial,Arial,Helvetica,sans-serif',
		'Arial Black,Arial Black,Gadget,sans-serif',
		'Comic Sans MS,Comic Sans MS,cursive',
		'Courier New,Courier New,Courier,monospace',
		'Georgia,Georgia,serif',
		'Impact,Charcoal,sans-serif',
		'Lucida Console,Monaco,monospace',
		'Lucida Sans Unicode,Lucida Grande,sans-serif',
		'Palatino Linotype,Book Antiqua,Palatino,serif',
		'Tahoma,Geneva,sans-serif',
		'Times New Roman,Times,serif',
		'Trebuchet MS,Helvetica,sans-serif',
		'Verdana,Geneva,sans-serif',
		'Gill Sans,Geneva,sans-serif',
		'Open Sans, Sans-Serif'
		];
	
	if( font === undefined ){
		return fonts;
	}
	
	for( var i = 0; i < fonts.length; i++ ){
		if( fonts[i].indexOf(font.trim()) == 0 ){
			return font[i];
		}
	}
	
	return ""; 
	
}

//Overlay Popup generic
function createGenericConfirmPopup(header, text, ok, cancel) {
	$('#overlay-header').html(header);
	$("#overlay-text").html(text);
	$('#overlay-continue').html(ok);
	$('#overlay-cancel').html(cancel);

	$('#overlay-header').show()
	$('#overlay-main').show();
}

function shakeElement( id, direction, times, distance, speed ){
	$(id).effect("shake",{times:times,distance:distance,direction:direction},speed);
}

function setupWidgetDropdownHandler(){
	if( false === widgetDropDownHandlerSetup ){
		widgetDropDownHandlerSetup = true;
		$(document).on('click', socialSurveyJavascriptWidget.dropdownHandler);
	}
}

$(document).on('click','#fb-policy-close',function(e){
	e.stopImmediatePropagation();
	e.preventDefault();
	e.stopPropagation();
	
	$('#fb-policy-banner').hide();
	sessionStorage.setItem("fbPopup",false);
});

/*new mismatch popup*/
function fetchMismatchedSurveyForEmail(transactionEmail,startIndex,batchSize){
	
	var payload ={
			"startIndex" : startIndex,
			"batchSize" : batchSize,
			"transactionEmail" : transactionEmail
	};
	
	$('#mismatch-new-trans-list').attr('data-startIndex',startIndex);
	$('#mismatch-new-trans-list').attr('data-batchSize',batchSize);
	
	callAjaxGetWithPayloadData("./fetchmismatchedsurveyforemail.do", drawMismatchedList, payload, true);
}

function drawMismatchedList(data){
	var mismatchedSurveys = JSON.parse(data);
	
	if(mismatchedSurveys == null || mismatchedSurveys == undefined){
		$('#mismatch-new-trans-list').html('<div class="mis-trans-empty">No trasactions found</div>');
		return;
	}else{

		var totalRecords = mismatchedSurveys.totalRecord;
		var surveyPreInitiationList = mismatchedSurveys.surveyPreInitiationList;
		
		if(totalRecords == 0 || surveyPreInitiationList.length == 0 || surveyPreInitiationList == null || surveyPreInitiationList == undefined){
			$('#mismatch-new-trans-list').html('<div class="mis-trans-empty">No trasactions found</div>');
			return;
		}
		
		$('#mismatch-new-trans-list').html('');
		
		var mismatchRecordDiv ="";
			
		for(var i=0;i<surveyPreInitiationList.length;i++){
			var surveySourceId = ""
			if(surveyPreInitiationList[i].surveySourceId != null && surveyPreInitiationList[i].surveySourceId != 'NULL' && surveyPreInitiationList[i].surveySourceId != 'null'){
				surveySourceId = surveyPreInitiationList[i].surveySourceId;
			}
			
			var modifiedOn = (surveyPreInitiationList[i].modifiedOn != 0 ? surveyPreInitiationList[i].modifiedOn : surveyPreInitiationList[i].createdOn);
			var modifiedOnDate = new Date(modifiedOn);
			var monthName=new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
			var modifiedOnStr = monthName[modifiedOnDate.getMonth()]+' '+(modifiedOnDate.getDate()%10!=modifiedOnDate.getDate()?modifiedOnDate.getDate():('0'+modifiedOnDate.getDate()))+', '+modifiedOnDate.getFullYear();
			
			mismatchRecordDiv = '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mismatch-new-trans">'
							+ 		'<div class="col-lg-7 col-md-7 col-xs-7 col-sm-7 mismatch-trans-text">'
							+ 			'<p class="mismatch-trans-p">'+ surveyPreInitiationList[i].customerFirstName +' '+ surveyPreInitiationList[i].customerLastName +'</p>'
							+ 			'<p>'+ surveySourceId +'</p>'
							+ 		'</div>'
							+ 		'<div class="col-lg-5 col-md-5 col-xs-5 col-sm-5 mismatch-trans-date">'+ modifiedOnStr +'</div>'
							+ '</div>';
			
			$('#mismatch-new-trans-list').append(mismatchRecordDiv);
		}
		
		$('#mismatch-new-trans-num').html(totalRecords);
		$('#mismatch-new-trans-list').attr('data-total',totalRecords);
		
		drawPaginationForMismatchedList(totalRecords);
	}	
}

function drawPaginationForMismatchedList(totalRecords){
	
	var startIndex = parseInt($('#mismatch-new-trans-list').attr('data-startIndex'));
	var batchSize = parseInt($('#mismatch-new-trans-list').attr('data-batchSize'));
	
	if(startIndex == 0 || startIndex < batchSize || totalRecords == 0){
		$('.mismatch-trans-arrow-up').addClass('hide');
	}else if(startIndex >= batchSize){
		$('.mismatch-trans-arrow-up').removeClass('hide');
	}
	
	if(startIndex + batchSize <= totalRecords && totalRecords > batchSize){
		$('.mismatch-trans-arrow-down').removeClass('hide');
	}else{
		$('.mismatch-trans-arrow-down').addClass('hide');
	}
	
}

function bindNewMismatchProcessClicks(user){
	$(document).on('click','#mismatch-popup-new',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
	});
	
	$(document).on('click','#mismatch-new-popup-main',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		$('#mismatch-new-popup-main').addClass('hide');
		resetMismatchPopup();
	});
	
	$(document).off('click','.mismatch-trans-arrow-up').on('click','.mismatch-trans-arrow-up',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		var startIndex = parseInt($('#mismatch-new-trans-list').attr('data-startIndex'));
		var batchSize = parseInt($('#mismatch-new-trans-list').attr('data-batchSize'));
		
		startIndex = startIndex - batchSize;
		if(startIndex < 0){
			startIndex = 0;
		}
		
		var transactionEmail = $('#mismatch-trans-mail').val();
		
		fetchMismatchedSurveyForEmail(transactionEmail,startIndex,batchSize);
	});
	
	$(document).off('click','.mismatch-trans-arrow-down').on('click','.mismatch-trans-arrow-down',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		var startIndex = parseInt($('#mismatch-new-trans-list').attr('data-startIndex'));
		var batchSize = parseInt($('#mismatch-new-trans-list').attr('data-batchSize'));
		var totalRecords = parseInt($('#mismatch-new-trans-list').attr('data-total'));
		
		startIndex = startIndex + batchSize;
		if(startIndex > totalRecords){
			return;
		}
		
		var transactionEmail = $('#mismatch-trans-mail').val();
		
		fetchMismatchedSurveyForEmail(transactionEmail,startIndex,batchSize);
	});
	
	$(document).off('click','#mismatch-new-assign-btn').on('click','#mismatch-new-assign-btn',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		if($('#mismatch-new-eid').val() == ''){
			$('#overlay-toast').html('Please choose a valid user.');
			showToast();
			return;
		}
		
		$('#mismatch-new-alias-div').removeClass('hide');
		$('#mismatch-new-body-options').addClass('hide');
		$('#mismatch-sub-header-txt').html('Please verify');
		$('#mismatch-new-confirm').removeClass('hide');
	});
	
	$(document).off('click','#mismatch-new-user-btn').on('click','#mismatch-new-user-btn',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		$('#mismatch-new-add-div').removeClass('hide');
		$('#mismatch-new-body-options').addClass('hide');
		$('#mismatch-sub-header-txt').html('Please verify');
		$('#mismatch-new-confirm').removeClass('hide');
	});
	
	$(document).off('click','#mismatch-new-ignore-btn').on('click','#mismatch-new-ignore-btn',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		$('#mismatch-new-archive-div').removeClass('hide');
		$('#mismatch-new-body-options').addClass('hide');
		$('#mismatch-sub-header-txt').html('Please verify');
		$('#mismatch-new-confirm').removeClass('hide');
	});
	
	$(document).off('click','#mismatch-new-back').on('click','#mismatch-new-back',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		if(!$('#mismatch-new-body-options').hasClass('hide')){
			$('#mismatch-new-popup-main').addClass('hide');
			enableBodyScroll();
		}else{
			$('#mismatch-new-body-options').removeClass('hide');
		}
		
		resetMismatchPopup();

	});
	
	$(document).off('click','#mismatch-new-confirm').on('click','#mismatch-new-confirm',function(e){
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		if(!$('#mismatch-new-alias-div').hasClass('hide')){
			saveUserMapNew(user,false);
		}else if(!$('#mismatch-new-archive-div').hasClass('hide')){
			saveUserMapNew(user,true);
		}else if(!$('#mismatch-new-add-div').hasClass('hide')){
			$('#mismatch-new-add-div').addClass('hide');
			$('#mismatch-new-add-form-div').removeClass('hide');
			$('#mismatch-sub-header-txt').html('Who would you like to add?');
			bindAssignToSelectorMismatchClick();
			bindOfficeSelectorEventsForMismatch();
			bindRegionSelectorEventsForMismatch();
		}else{
			if($('#mis-first-name').val()=='' && $('#mis-last-name').val()=='' ){
				$('#overlay-toast').html('First name and last name cannot be empty for the new user.');
				return;
			}else if($('#mis-first-name').val()==''){
				$('#overlay-toast').html('First name cannot be empty for the new user.');
				showToast();
				return;
			}
			addIndividualForMismatch('mismatch-new-add-user-form','#mismatch-new-confirm');
			$('#mismatch-new-popup-main').addClass('hide');
			resetMismatchPopup();
		}
	});
}

function resetMismatchPopup(){
	$('#mismatch-new-archive-div').addClass('hide');
	$('#mismatch-new-alias-div').addClass('hide');
	$('#mismatch-new-add-div').addClass('hide');
	$('#mismatch-sub-header-txt').html('What can we do for you?');
	$('#mismatch-new-confirm').addClass('hide');
	$('#mismatch-new-eid').val('');
	$('#mis-first-name').val('');
	$('#mis-last-name').val('');
	$('#bd-region-selector-mis').hide();
	$('#bd-office-selector-mis').hide();
	$('#assign-to-txt-mis').val('Company');
	$("#assign-to-txt-mis").attr("data-assignto", 'company');
	$('#selected-region-id-hidden-mis').val(0);
	$('#selected-office-id-hidden-mis').val(0);
	$('#mismatch-new-add-form-div').addClass('hide');
	enableBodyScroll();
}

function addIndividualForMismatch(formId, disableEle) {
	var url = "./addindividual.do";
	showOverlay();
	
	disable(disableEle);
	var $form = $("#" + formId);
	var payLoad = $form.serialize();
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payLoad,
		success : addIndividualCallBackForMismatch,
		complete: function(data){
			enable(disableEle);
			updateSurveyForUser();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function updateSurveyForUser(){
	var url = "./updatesurveyforuser.do";
	var emailId = $('#mismatch-trans-mail').val();
	var payload = {
			"emailAddress" : emailId
	};
	
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payload,
		async : true,
		success : function(data){
			$('#overlay-toast').html(data);
			showToast();
		},
		complete: function(){
			hideOverlay();
			initializeUnmatchedUserPage();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function addIndividualCallBackForMismatch(data) {
	hideOverlay();
	displayMessage(data);
	resetInputFields("mismatch-new-add-user-form");
}

function attachAutocompleteAliasMismatchDropdown() {
	var companyId = $('#cur-company-id').val();
	$('#mismatch-new-eid').autocomplete({
		source : function(request, response) {
			callAjaxGetWithPayloadData("/fetchagentsforadmin.do", function(data) {
				var responseData = JSON.parse(data);
				response($.map(responseData, function(item) {
					return {
						label : item.displayName + " <" + item.emailId + ">",
						value : item.displayName + " <" + item.emailId + ">",
						userId : item.userId,
						emailId : item.emailId
					};
				}));
			}, {
				"searchKey" : request.term,
				"columnName" : "companyId",
				"columnValue" : companyId
			}, true);
		},
		minLength : 1,
		select : function(event, ui) {
			event.stopPropagation();
			var element = event.target;
			$(element).attr('agent-id', ui.item.userId);
			$(element).attr('column-name', colName);
			$(element).attr('email-id', ui.item.emailId);
			$(element).attr('val', ui.item.value);
		},
		close : function(event, ui) {
		},
		create : function(event, ui) {
			$('.ui-helper-hidden-accessible').remove();
		},
		open : function() {
			$('.ui-autocomplete').addClass('ui-mis-alias-new').perfectScrollbar({
				suppressScrollX : true
			});
			$('.ui-autocomplete').perfectScrollbar('update');
		}
	});

	$('#mismatch-new-eid').keyup(function(e) {
		var oldVal = $(this).attr('val');
		var cuurentVal = $(this).val();
		if (oldVal == cuurentVal) {
			return;
		}
		$(this).attr('agent-id', "");
		$(this).attr('column-name', "");
		$(this).attr('email-id', "");
	});
}


function bindAssignToSelectorMismatchClick() {
	$('#assign-to-selector-mis').click(function(e) {
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		$("#assign-to-droplist-mis").slideToggle(200);
	});

	$('.mis-assignto-options').click(function(e) {
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		var assignToOption = $(this).attr('data-assign-to-option');
		$("#assign-to-txt-mis").val($(this).html());
		$("#assign-to-txt-mis").attr("data-assignto", assignToOption);

		showMismatchSelectorsByAssignToOption(assignToOption);
		$("#assign-to-droplist-mis").slideToggle(200);
	});
}

function showMismatchSelectorsByAssignToOption(assignToOption) {
	switch (assignToOption) {
	case 'company':
		disableMismatchRegionSelector();
		disableMismatchOfficeSelector();
		break;
	case 'region':
		$("#selected-region-txt-mis").prop("disabled", false);
		disableMismatchOfficeSelector();
		$("#bd-region-selector-mis").show();
		break;
	case 'office':
		$("#selected-office-txt-mis").prop("disabled", false);
		disableMismatchRegionSelector();
		$("#bd-office-selector-mis").show();
		break;
	default:
		$("#selected-region-txt-mis").prop("disabled", false);
		$("#selected-office-txt-mis").prop("disabled", false);
	}
}

function disableMismatchRegionSelector() {
	$("#selected-region-txt-mis").prop("disabled", true);
	$("#selected-region-txt-mis").val("");
	$('#selected-region-id-hidden-mis').val("");
	$("#bd-region-selector-mis").hide();
}

function disableMismatchOfficeSelector() {
	$("#selected-office-txt-mis").prop("disabled", true);
	$("#selected-office-txt-mis").val("");
	$('#selected-office-id-hidden-mis').val("");
	// $('#selected-region-id-hidden').val("");
	$("#bd-office-selector-mis").hide();
}

function bindRegionSelectorEventsForMismatch() {
	callAjaxGET("/fetchregions.do", function(data) {
		var regionList = [];
		if (data != undefined && data != "")
			regionList = $.parseJSON(data);
		var searchData = [];
		for (var i = 0, j = 0; i < regionList.length; i++) {
			if (regionList[i].isDefaultBySystem == 0) {
				searchData[j] = {};
				searchData[j].label = regionList[i].regionName;
				searchData[j].regionId = regionList[i].regionId;
				j++;
			}
		}
		$("#selected-region-txt-mis").autocomplete({
			source : searchData,
			minLength : 0,
			delay : 0,
			autoFocus : true,
			select : function(event, ui) {
				$("#selected-region-txt-mis").val(ui.item.label);
				$('#selected-region-id-hidden-mis').val(ui.item.regionId);
				return false;
			},
			close : function(event, ui) {
			},
			create : function(event, ui) {
				$('.ui-helper-hidden-accessible').remove();
			},
			open : function() {
				$('.ui-autocomplete').addClass('ui-assign-mis-dd').perfectScrollbar({
					suppressScrollX : true
				});
				$('.ui-autocomplete').perfectScrollbar('update');
			}
		}).autocomplete("instance")._renderItem = function(ul, item) {
			return $("<li>").append(item.label).appendTo(ul);
		};
		$("#selected-region-txt-mis").off('focus');
		$("#selected-region-txt-mis").focus(function() {
			$(this).autocomplete('search');
		});
	}, true);
}

/**
 * binds the click and keyup of office selector
 */
function bindOfficeSelectorEventsForMismatch() {
	callAjaxGET("/fetchbranches.do", function(data) {
		var branchList = [];
		if (data != undefined && data != "")
			branchList = $.parseJSON(data);
		var searchData = [];
		for (var i = 0, j = 0; i < branchList.length; i++) {
			if (branchList[i].isDefaultBySystem == 0) {
				searchData[j] = {};
				searchData[j].label = branchList[i].branchName;
				searchData[j].branchId = branchList[i].branchId;
				searchData[j].regionId = branchList[i].regionId;
				j++;
			}
		}
		$("#selected-office-txt-mis").autocomplete({
			source : searchData,
			minLength : 0,
			delay : 0,
			autoFocus : true,
			select : function(event, ui) {
				$("#selected-office-txt-mis").val(ui.item.label);
				$('#selected-office-id-hidden-mis').val(ui.item.branchId);
				$('#selected-region-id-hidden-mis').val(ui.item.regionId);
				return false;
			},
			close : function(event, ui) {
			},
			create : function(event, ui) {
				$('.ui-helper-hidden-accessible').remove();
			},
			open : function() {
				$('.ui-autocomplete').addClass('ui-assign-mis-dd').perfectScrollbar({
					suppressScrollX : true
				});
				$('.ui-autocomplete').perfectScrollbar('update');
			}
		}).autocomplete("instance")._renderItem = function(ul, item) {
			return $("<li>").append(item.label).appendTo(ul);
		};
		$("#selected-office-txt-mis").off('focus');
		$("#selected-office-txt-mis").focus(function() {
			$(this).autocomplete('search');
		});
	}, true);
}

var insavedNew = false;
function saveUserMapNew(aliasMail,isIgnore) {
	if (insavedNew == true) {
		return;
	}
	
	var agentId = $('#mismatch-new-eid').attr('agent-id');
	var msg = '';
	
	if (isIgnore) {
		agentId = 0;
	} else {
		if (agentId == undefined || agentId <= 0) {
			$('#overlay-toast').html('Please enter valid alias!');
			showToast();
			return;
		}
	}

	insavedNew == true;
	var payload = {
		"emailAddress" : aliasMail,
		"agentId" : agentId,
		"ignoredEmail" : isIgnore

	};
	
	var url = './saveemailmapping.do';

	
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "GET",
		data : payload,
		async : true,
		cache : false,
		success :  function(data){
			insavedNew == false;
			$('#overlay-main').hide();
			if(isIgnore){
				$('#overlay-toast').html('Mail address added to the ignore list');
			}else{
				$('#overlay-toast').html(data);
			}
			showToast();
			
			$('#mismatch-new-popup-main').addClass('hide');
		},
		complete: function(){
			hideOverlay();
			resetMismatchPopup();
			initializeUnmatchedUserPage();
			initializeProcesedUserPage();
			initializeMapped();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			if(e.status == 0) {
				return;
			}
		}
	});
}

function summitTimer() {
	var currentDate = new Date();
	var endDate = new Date(2018,08,5,0,0,0);
	
	var ms = endDate.getTime() - currentDate.getTime();
	
	var d, h, m, s;
	s = Math.floor(ms / 1000);
	m = Math.floor(s / 60);
	s = s % 60;
	h = Math.floor(m / 60);
	
	m = (Math.floor(Math.floor(m % 60)/10) == 0 )? '0'+Math.floor(m % 60) : Math.floor(m % 60);
	d = (Math.floor(Math.floor(h / 24)/10) == 0 )? '0'+Math.floor(h / 24) : Math.floor(h / 24);
	h = (Math.floor(Math.floor(h % 24)/10) == 0 )? '0'+Math.floor(h % 24) : Math.floor(h % 24);
	
	m = m <=0 ? 0 : m;
	d = d <=0 ? 0 : d;
	h = h <=0 ? 0 : h;
	
	return { d: d, h: h, m: m};
}

$(document).on('click','#close-enc-banner',function(e){
	e.stopPropagation();
	e.stopImmediatePropagation();
	e.preventDefault();
	
	disableEncompassBanner();
	
});

function disableEncompassBanner(){
	
	var message = "";
	var url = "./disableencompassnotification.do"
		
	$.ajax({
		async : false,
		url : url,
		type : "PUT",
		cache : false,
		dataType : "text",
		success : function(data) {
			if(data == true || data == 'true'){
				message = "Successfully disabled Encompass Notification.";
				$('#dsh-enc-banner').hide();
			}else if(data == false || data == 'false'){
				message = "Unable to disable the Encompass notification."; 
			}
			
			$('#overlay-toast').html(message);
			showToast();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html("Unable to disable the Encompass notification.");
			showToast();
		}
	});
}

$('body').on('blur', '#enc-alert-mail-recipients', function() {
	
	// format email IDs
	var emails = $("#enc-alert-mail-recipients").val();
	
	if( emails == undefined || emails == "" || emails.length == 0){
		$('#overlay-toast').html("Encompass alert reciepients cannot be blank");
		showToast();
		$("#enc-alert-mail-recipients").val($('#encompass-alert-mails').val());
		return;
	}
	
	var payload = {
		"alertMails" : emails
	};
	
	callAjaxPostWithPayloadData("./updateencompassalertmail.do", function(data) {
		
		var emailList = data;
		var status;
		
		if(emailList == "INPUT_ERROR"){
			status = "Invalid email address inserted. Encompass alert recipients updation Failed!";
			$("#enc-alert-mail-recipients").val($('#encompass-alert-mails').val());
		}else {
			status = "Encompass alert recipients updated successfully!";
		}
		
		$('#overlay-toast').html(status);
		showToast();
	}, payload, true);
	
});
