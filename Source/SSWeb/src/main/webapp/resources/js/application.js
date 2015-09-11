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

//colName and colValue contains profile level of logged in user and value for
//colName is present in colValue.
var colName;
var colValue;
var searchColumn;
var lastColNameForCount;
var lastColValueForCount;
var lastColNameForGraph;
var lastColValueForGraph;


//Variables for processing Edit profile
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

//User management
var usersStartIndex = 0;
var numOfRows = 10;

//User management
var userStartIndex = 0;
var userBatchSize = 10;
var doStopAjaxRequestForUsersList = false;
var listOfBranchesForAdmin;
var isUserManagementAuthorized = true;
var isAddUser = true;

//pagination variables for pro List page
var rowSize = 10;
var startIndex = 0;

//Variables for editprofile page
var editProfileForYelp = false;
var editProfileForLicense = false;
var editProfileForHobbies = false;
var editProfileForAchievements = false;

//Variables for survey question page
var qno = 0;
var questions;
var questionDetails;
var agentId;
var agentName;
var customerResponse;
var customerEmail;
var mood;
var stage;
var isSmileTypeQuestion=true;
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
var rating;
var firstName;
var lastName;
var surveyUrl = "/rest/survey/";
var editable;
var yelpEnabled;
var googleEnabled;
var zillowEnabled;
var lendingtreeEnabled;
var realtorEnabled;
var agentProfileLink;
var agentFullProfileLink;
var companyLogo;

//Verticals master
var verticalsMasterList;


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
	hideOverlay();
}


/*
 * This module helps in browser navigation support to give a single page app
 * 
 * Functions for history support
 */

var historyCallback = false;
var refreshSupport = true;

function getRandomID() {
	return (Math.floor(Math.random() * 10000) + Math
			.floor(Math.random() * 10000));
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
		console.log('refresh not supported');
		return;
	}
	var newLocation = window.location.hash.substring(1);
	if (newLocation) {
		showMainContent("/"+newLocation+".do");
	}
}
/*End of functions for history support*/


/*
 * Click event to close survey popup
 */
$(document).on('click',  function(e){
	if($('#overlay-send-survey').is(':visible')){
		$('#overlay-send-survey').hide();
	}
});

$(document).on('keyup',  function(e){
	if (e.keyCode == 27){
		if($('#overlay-send-survey').is(':visible')){
			$('#overlay-send-survey').hide();
		}
	}
});

$(document).on('click', '#welcome-popup-invite', function(e){
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
	
	confirmRetakeSurveyReminderMail(this);
	
	
	/*var firstName = $(this).parent().parent().parent().parent().attr('data-firstname');
	var lastName = $(this).parent().parent().parent().parent().attr('data-lastname');
	var agentName = $(this).parent().parent().parent().parent().attr('data-agentname');
	var customerEmail = $(this).parent().parent().parent().parent().attr('data-customeremail');
	var agentId = $(this).parent().parent().parent().parent().attr('data-agentid');
	var payload = {
			"customerEmail" : customerEmail,
			"agentId" : agentId,
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName
	};
	callAjaxGetWithPayloadData('./restartsurvey.do', '', payload, true);
	$('#overlay-toast').html('Mail sent to '+firstName +' '+' to retake the survey for you.');
	showToast();*/
});


function confirmRetakeSurveyReminderMail(element) {
	
	
		$('#overlay-header').html("Resend survey");
		$('#overlay-text').html("Are you sure you want to delete this survey and send a request to the customer to retake their survey?");
		$('#overlay-continue').html("Yes");
		$('#overlay-cancel').html("No");
		$('#overlay-continue').off();
		$('#overlay-continue').click(function(){
			retakeSurveyReminderMail(element);
		});
		
		$('#overlay-main').show();
		disableBodyScroll();
	}

	



function retakeSurveyReminderMail(element) {
	var firstName = $(element).parent().parent().parent().parent().attr('data-firstname');
	var lastName = $(element).parent().parent().parent().parent().attr('data-lastname');
	var agentName = $(element).parent().parent().parent().parent().attr('data-agentname');
	var customerEmail = $(element).parent().parent().parent().parent().attr('data-customeremail');
	var agentId = $(element).parent().parent().parent().parent().attr('data-agentid');
	var payload = {
			"customerEmail" : customerEmail,
			"agentId" : agentId,
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName
	};
	callAjaxGetWithPayloadData('./restartsurvey.do', '', payload, true);
	$('#overlay-toast').html('Mail sent to '+firstName +' '+' to retake the survey for you.');
	showToast();
	$('#overlay-cancel').click();
}

$(document).on('click', '.report-abuse-txt', function(e) {
	var reviewElement = $(this).parent().parent().parent().parent();
	var payload = {
		"customerEmail" : reviewElement.attr('data-customeremail'),
		"agentId" : reviewElement.attr('data-agentid'),
		"firstName" : reviewElement.attr('data-cust-first-name'),
		"lastName" : reviewElement.attr('data-cust-last-name'),
		"agentName" : reviewElement.attr('data-agent-name'),
		"review" : reviewElement.attr('data-review')
	};
	$("#report-abuse-txtbox").val('');
	
	// Unbind click events for button
	$('.rpa-cancel-btn').off('click');
	$('.rpa-report-btn').off('click');
	
	$('#report-abuse-overlay').show();
	$('.rpa-cancel-btn').on('click', function() {
		$('#report-abuse-overlay').hide();
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
	//check if report text is empty
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
	}, payload, true);
}

$('body').click(function() {
	$('#hr-dd-wrapper').slideUp(200);
});

function paintDashboard(profileMasterId, newProfileName, newProfileValue, typeoOfAccount) {
	accountType = typeoOfAccount;
	startIndexCmp = 0;
	batchSizeCmp = 5;
	totalReviews = 0;
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
	if (newProfileName == "companyId") {
		showCompanyAdminFlow(newProfileName, newProfileValue);
	} else if (newProfileName == "regionId") {
		showRegionAdminFlow(newProfileName, newProfileValue);
	} else if (newProfileName == "branchId") {
		showBranchAdminFlow(newProfileName, newProfileValue);
	} else if (newProfileName == "agentId") {
		showAgentFlow(newProfileName, newProfileValue);
	}
	colName = newProfileName;
	colValue = newProfileValue;
	
	showDisplayPic();
	getIncompleteSurveyCount(colName, colValue);
	getReviewsCountAndShowReviews(colName, colValue);
}

function showCompanyAdminFlow(newProfileName, newProfileValue) {

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();

	showProfileDetails(newProfileName, 0, 90);
	bindSelectButtons();
	if((accountType!="INDIVIDUAL") && (accountType!="FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, 0);
	showSurveyStatisticsGraphically(newProfileName, 0);
}

function showRegionAdminFlow(newProfileName, newProfileValue) {

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();

	showProfileDetails(newProfileName, newProfileValue, 90);
	bindSelectButtons();
	if((accountType!="INDIVIDUAL") && (accountType!="FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);
}

function showBranchAdminFlow(newProfileName, newProfileValue) {

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();

	showProfileDetails(newProfileName, newProfileValue, 90);
	bindSelectButtons();
	if((accountType!="INDIVIDUAL") && (accountType!="FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);
}

function showAgentFlow(newProfileName, newProfileValue) {
	
	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").hide();
	$("#dsh-grph-srch-survey-div").hide();

	showProfileDetails(newProfileName, 0, 90);
	bindSelectButtons();
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);
}

function showProfileDetails(columnName, columnValue, numberOfDays) {
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"numberOfDays" : numberOfDays
	};
	callAjaxGetWithPayloadData("./profiledetails.do", function(data) {
		$('#dash-profile-detail-circles').html(data);
	}, payload, false);
}

function bindSelectButtons() {
	$("#selection-list").unbind('change');
	$("#graph-sel-list").unbind('change');
	$("#dsh-grph-format").unbind('change');
	$("#survey-count-days").unbind('change');

	$("#selection-list").change(function() {
		$('#dsh-sel-item').val('');
		$('.dsh-res-display').hide();
		
		if ($("#selection-list").val() == 'companyId') {
			$('#dsh-srch-survey-div').hide();
			showSurveyStatistics('companyId', 0);
		} else {
			$('#dsh-srch-survey-div').show();
		}
	});
	$("#graph-sel-list").change(function() {
		$('#dsh-grph-sel-item').val('');
		$('.dsh-res-display').hide();
		
		if ($("#graph-sel-list").val() == 'companyId') {
			$('#dsh-grph-srch-survey-div').hide();
			showSurveyStatisticsGraphically('companyId', 0);
		} else {
			$('#dsh-grph-srch-survey-div').show();
		}
	});
	
	$("#dsh-grph-format").change(function() {
		var columnName = colName;
		var columnValue = colValue;
		if($('#dsh-grph-srch-survey-div').is(':visible')){
			columnName = lastColNameForGraph;
			columnValue = lastColValueForGraph;
		}
		showSurveyStatisticsGraphically(columnName, columnValue);
	});
	$("#survey-count-days").change(function() {
		var columnName = colName;
		var columnValue = colValue;
		if($('#dsh-srch-survey-div').is(':visible')){
			columnName = lastColNameForCount;
			columnValue = lastColValueForCount;
			console.info("lastColNameForCount:"+lastColNameForCount+" lastColValueForCount:"+lastColValueForCount);
		}
		showSurveyStatistics(columnName, columnValue);
	});
}

function populateSurveyStatisticsList(columnName) {
	$("#region-div").show();
	$("#graph-sel-div").show();
	
	var options = "";
	var optionsForGraph = "";
	if ((columnName == "companyId") && (accountType == "ENTERPRISE" || accountType == "COMPANY")) {
		options += "<option value=companyId>Company</option>";
		optionsForGraph += "<option value=companyId>Company</option>";
	}
	if ((columnName == "companyId") && (accountType == "ENTERPRISE")) {
		options += "<option value=regionName>Region</option>";
		optionsForGraph += "<option value=regionName>Region</option>";
	}
	if (accountType == "ENTERPRISE" || accountType == "COMPANY") {
		if (columnName == "companyId" || columnName == "regionId") {
			options += "<option value=branchName>Branch</option>";
			optionsForGraph += "<option value=branchName>Branch</option>";
		}
	}
	if (columnName == "companyId" || columnName == "regionId" || columnName == "branchId") {
		options += "<option value=displayName>Individual</option>";
		optionsForGraph += "<option value=displayName>Individual</option>";
	}
	
	$("#selection-list").html(options);
	$("#graph-sel-list").html(options);
	
	if (columnName == "companyId") {
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
	callAjaxGetWithPayloadData("./surveycount.do", function(data) {
		$('#dash-survey-status').html(data);
	}, payload, false);
}

function showIncompleteSurvey(columnName, columnValue) {
	
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"startIndex" : startIndexInc,
		"batchSize" : batchSizeInc
	};
	
	var totalIncReviews = parseInt($('#dsh-inc-srvey').attr("data-total"));
	if(totalIncReviews == 0) {
		$("#incomplete-survey-header").html("No incomplete surveys found");
		return;
	}
	
	callAjaxGetWithPayloadData("./fetchdashboardincompletesurvey.do", function(data) {
		if (startIndexInc == 0) {
			console.info("startIndex ==0:"+startIndexInc);
			$('#dsh-inc-srvey').html(data);
			$("#dsh-inc-dwnld").show();
		}
		else {
			$('#dsh-inc-srvey').append(data);
		}
		$('#dsh-inc-srvey').perfectScrollbar();
		startIndexInc += batchSizeInc;
	}, payload, false);
	
}

$(document).on('click', '.dash-lp-rt-img', function() {
	var surveyPreInitiationId = $(this).data("surveypreinitiationid");
	/*var agentId = $(this).data("agentid");
	var agentName = $(this).data("agentname");
	var customerEmail = $(this).data("custemail");*/
	var customerName = $(this).data("custname");
	sendSurveyReminderMail(surveyPreInitiationId, customerName);
});

function getReviewsCountAndShowReviews(columnName, columnValue) {
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue
	};
	callAjaxGetWithPayloadData("./fetchdashboardreviewCount.do", function(totalReview) {
		totalReviews = totalReview;
		callAjaxGetWithPayloadData("./fetchName.do", function(name) {
			if (totalReview == 0) {
				$("#review-desc").html("No reviews found for " + name);
				$("#dsh-admin-cmp-dwnld").hide();
				$("#dsh-cmp-dwnld").hide();
				$("#review-details").html('');
				return;
			} else {
				$("#review-desc").html("What people say about " + name.substring(1, name.length - 1));
				if (colName != "agentId"){
					$("#dsh-cmp-dwnld").hide();
					$("#dsh-admin-cmp-dwnld").show();
				}
				else if (accountType == "INDIVIDUAL") {
					$("#dsh-cmp-dwnld").hide();
					$("#dsh-admin-cmp-dwnld").show();
				}
				else { 
					$("#dsh-admin-cmp-dwnld").hide();
					$("#dsh-cmp-dwnld").show();
				}
				
				// initializing datepickers
				bindDatePickerforSurveyDownload();
				bindDatePickerforIndividualSurveyDownload();
			}
		}, payload, false);
		
		if (parseInt(totalReview) > 0) {
			showReviews(columnName, columnValue);
		}
	}, payload, false);
}

function showReviews(columnName, columnValue) {
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"startIndex" : startIndexCmp,
		"batchSize" : batchSizeCmp
	};
	callAjaxGetWithPayloadData("./fetchdashboardreviews.do", function(data) {
		if (startIndexCmp == 0)
			$('#review-details').html(data);
		else
			$('#review-details').append(data);
		
		$(".review-ratings").each(function() {
			changeRatingPattern($(this).data("rating"), $(this), false, $(this).data("source"));
		});
		/*$('.icn-fb').unbind('click');
		$(".icn-fb").click(function() {
			var firstName = $(this).parent().parent().parent().attr('data-firstname');
			var lastName = $(this).parent().parent().parent().attr('data-lastname');
			var agentName = $(this).parent().parent().parent().attr('data-agentname');
			var review = $(this).parent().parent().parent().attr('data-review');
			var score = $(this).parent().parent().parent().attr('data-score');
			var agentId = $(this).parent().parent().parent().attr('data-agentid');
			shareOnFacebook(firstName, lastName, agentName, review, score, agentId);
		});
		$('.icn-twit').unbind('click');
		$(".icn-twit").click(function() {
			var firstName = $(this).parent().parent().parent().attr('data-firstname');
			var lastName = $(this).parent().parent().parent().attr('data-lastname');
			var agentName = $(this).parent().parent().parent().attr('data-agentname');
			var review = $(this).parent().parent().parent().attr('data-review');
			var score = $(this).parent().parent().parent().attr('data-score');
			var agentId = $(this).parent().parent().parent().attr('data-agentid');
			shareOnTwitter(firstName, lastName, agentName, review, score, agentId);
		});
		$('.icn-lin').unbind('click');
		$(".icn-lin").click(function() {
			var firstName = $(this).parent().parent().parent().attr('data-firstname');
			var lastName = $(this).parent().parent().parent().attr('data-lastname');
			var agentName = $(this).parent().parent().parent().attr('data-agentname');
			var review = $(this).parent().parent().parent().attr('data-review');
			var score = $(this).parent().parent().parent().attr('data-score');
			var agentId = $(this).parent().parent().parent().attr('data-agentid');
			shareOnLinkedin(firstName, lastName, agentName, review, score, agentId);
		});
		$('.icn-yelp').unbind('click');
		$(".icn-yelp").click(function() {
			var agentId = $(this).parent().parent().parent().attr('data-agentid');
			shareOnYelp(agentId, window.location.origin+"/rest/survey/");
		});
		$('.icn-gplus').unbind('click');
		$(".icn-gplus").click(function() {
			var agentId = $(this).parent().parent().parent().attr('data-agentid');
			shareOnGooglePlus(agentId, window.location.origin+"/rest/survey/");
		});*/
		
		startIndexCmp += batchSizeCmp;
	}, payload, false);
}

$(document).on('scroll', '#dsh-inc-srvey', function() {
	console.log($('.ps-scrollbar-y').css('top'));
});

function showSurveyStatisticsGraphically(columnName, columnValue) {
	var element = document.getElementById("dsh-grph-format");
	var numberOfDays = element.options[element.selectedIndex].value;
	showSurveyGraph(columnName, columnValue, numberOfDays);
}

function showSurveyGraph(columnName, columnValue, numberOfDays) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"numberOfDays" : numberOfDays
	};
	$.ajax({
		url : "./surveydetailsforgraph.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				graphData = data.responseJSON;
				paintSurveyGraph();
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintSurveyGraph() {
	if (graphData == undefined)
		return;
	var allTimeslots = [];
	var timeslots = [];
	var clickedSurveys = [];
	var sentSurveys = [];
	var socialPosts = [];
	var completedSurveys = [];
	var index = 0;

	$.each(graphData.clicked, function(key, value) {
		allTimeslots[index] = key;
		clickedSurveys[index] = value;
		index++;
	});
	
	index = 0;
	if (timeslots.length > allTimeslots.length) {
		allTimeslots = timeslots;
		timeslots = [];
	}
	$.each(graphData.sent, function(key, value) {
		timeslots[index] = key;
		sentSurveys[index] = value;
		index++;
	});
	
	index = 0;
	if (timeslots.length > allTimeslots.length) {
		allTimeslots = timeslots;
		timeslots = [];
	}
	$.each(graphData.complete, function(key, value) {
		timeslots[index] = key;
		completedSurveys[index] = value;
		index++;
	});
	
	index = 0;
	if (timeslots.length > allTimeslots.length) {
		allTimeslots = timeslots;
		timeslots = [];
	}
	$.each(graphData.socialposts, function(key, value) {
		timeslots[index] = key;
		socialPosts[index] = value;
		index++;
	});
	
	if (timeslots.length > allTimeslots.length) {
		allTimeslots = timeslots;
		timeslots = [];
	}
	
	var element = document.getElementById("dsh-grph-format");
	if(element == null){
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

	if (format != '365') {
		allTimeslots.reverse();
		clickedSurveys.reverse();
		sentSurveys.reverse();
		completedSurveys.reverse();
		socialPosts.reverse();
	}
	
	var internalData = [];
	var nestedInternalData = [];
	nestedInternalData.push(type, 'No. of surveys sent',
			'No. of surveys clicked', 'No. of surveys completed',
			'No. of social posts');
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
	console.log(internalData);

	var data = google.visualization.arrayToDataTable(internalData);
	var options = {
		chartArea : {
			width : '90%',
			height : '80%'
		},
		colors : [ 'rgb(28,242,0)', 'rgb(0,174,239)', 'rgb(255,242,0)',
				'rgb(255,202,145)' ],
		legend : {
			position : 'none'
		}
	};

	var chart = new google.visualization.LineChart(document.getElementById('util-gph-item'));
	chart.draw(data, options);
}

//Being called from dashboard.jsp on key up event.
function searchBranchRegionOrAgent(searchKeyword, flow) {
	var e;
	
	if(flow == 'icons') {
		e = document.getElementById("selection-list");
	} else if (flow == 'graph'){
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
	
	callAjaxGetWithPayloadData("./findregionbranchorindividual.do", function(data) {
		if (flow == 'icons'){
			$('#dsh-srch-res').addClass('dsh-sb-dd');
			$('#dsh-srch-res').html(data);
		} else if (flow == 'graph'){
			$('#dsh-grph-srch-res').addClass('dsh-sb-dd');
			$('#dsh-grph-srch-res').html(data);
		} else if (flow == 'reports'){
			$('#dsh-srch-report').addClass('dsh-sb-dd');
			$('#dsh-srch-report').html(data);
		}
		$('.dsh-res-display').click(function() {
			
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
			
			if (flow == 'icons'){
			    $('#dsh-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-sel-item').val($(this).html());
			}
			else if (flow == 'graph') {
				$('#dsh-grph-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-grph-sel-item').val($(this).html());
			}
			else if (flow == 'reports'){
				$('#dsh-srch-report').removeClass('dsh-sb-dd');
				$('#admin-report-dwn').val($(this).html());
				$('#report-sel').attr('data-iden',columnName);
				$('#report-sel').attr('data-idenVal',value);
				if (searchColumn == "displayName") {
					$('#dsh-ind-rep-bnt').show();
					$('#dsh-admin-rep-bnt').hide();
				} else {
					$('#dsh-admin-rep-bnt').show();
					$('#dsh-ind-rep-bnt').hide();
				}
				
			}
			
			if (flow == 'icons'){
				lastColNameForCount = columnName;
				lastColValueForCount = value;
				showSurveyStatistics(columnName, value);
			}
			else if (flow == 'graph'){
				lastColNameForGraph = columnName;
				lastColValueForGraph = value;
				showSurveyStatisticsGraphically(columnName, value);
			}
			$('.dsh-res-display').hide();
		});
	}, payload, false);
}

function sendSurveyReminderMail(surveyPreInitiationId, customerName) {
	var success = false;
	var payload = {
		"surveyPreInitiationId" : surveyPreInitiationId
	};
	$.ajax({
		url : "./sendsurveyremindermail.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				$('#overlay-toast').html("Reminder Mail sent successfully to " + customerName);
				showToast();
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
			$('#overlay-toast').html('Something went wrong while sending mail. Please try again after sometime.');
			showToast();
		}
	});
}

/*function changeRatingPattern(rating, ratingParent) {
	var counter = 0;
	ratingParent.children().each(function() {
		$(this).addClass("icn-no-star");
		$(this).removeClass("icn-half-star");
		$(this).removeClass("icn-full-star");

		if (rating >= counter) {
			if (rating - counter >= 1) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-full-star");
			} else if (rating - counter == 0.5) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-half-star");
			}
		}
		counter++;
	});
}*/

function showDisplayPic() {
	$.ajax({
		url : "./getdisplaypiclocation.do",
		type : "GET",
		dataType : "JSON",
		success : function(data) {
			
		},
		complete : function(data) {
			if (data.errCode == undefined){
				console.log("Image location : " + data.responseJSON);
				var imageUrl = data.responseJSON;
				if (imageUrl != undefined && imageUrl != "undefined" && imageUrl.trim() != "") {
					$("#dsh-prsn-img").removeClass('dsh-pers-default-img');
					$("#dsh-prsn-img").removeClass('dsh-office-default-img');
					$("#dsh-prsn-img").removeClass('dsh-region-default-img');
					$("#dsh-prsn-img").removeClass('dsh-comp-default-img');
					
					$("#dsh-prsn-img").css("background", "url(" + imageUrl + ") no-repeat center");
					$("#dsh-prsn-img").css("background-size", "cover");
				}
				return data.responseJSON;
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.log("Logged in id as : "+colName);
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

function updateCurrentProfile(entityType, entityValue) {
	var url = "./updatecurrentprofile.do?entityId=" + entityValue + "&entityType=" + entityType;
	callAjaxGET(url, function(data) {}, true);
}

function showSurveyRequestPage(){
	callAjaxGET('./redirecttosurveyrequestpage.do', function(data) {
		$('#srv-req-pop').removeClass('hide');
		$('#srv-req-pop').addClass('survey-request-popup-container');
		$('#srv-req-pop').show();
		$('#srv-req-pop').find('.survey-request-popup').html(data);
		
	});
	//window.open('./redirecttosurveyrequestpage.do', '_self');
}

$(document).on('click','#dashboard-sel',function(e){
	e.stopPropagation();
	$('#da-dd-wrapper-profiles').slideToggle(200);
});

$(document).on('click','.da-dd-item',function(e){
	showOverlay();
	$('#dashboard-sel').html($(this).html());
	$('#da-dd-wrapper-profiles').slideToggle(200);
	$('#da-dd-wrapper-profiles').perfectScrollbar('update');
	
	attrName = $(this).attr('data-column-type');
	attrVal = $(this).attr('data-column-value');
	
	// update selected profile in session
	updateCurrentProfile($(this).attr('data-column-type'), $(this).attr('data-column-value'));

	var selectedTab = window.location.hash.split("#")[1];
	
	showMainContent('./' + selectedTab + '.do');
	//showMainContent('./dashboard.do');
});

$(document).click(function(){
	if ($('#da-dd-wrapper-profiles').css('display') == "block") {
		$('#da-dd-wrapper-profiles').toggle();
	}
});

//Populate Existing Survey Questions 
function commonActiveSurveyCallback(response){
	showInfo(response);
	loadActiveSurveyQuestions();
	$('.err-nw-wrapper').delay(2000).fadeOut();
}

function loadActiveSurveyQuestions() {
	var url = "./getactivesurveyquestions.do";
	callAjaxGET(url, function(data) {
		$('#bs-ques-wrapper').html(data);
		resizeAdjBuildSurvey();
	}, true);
}

function resizeAdjBuildSurvey(){
	var winW = window.innerWidth;
	if (winW < 768) {
		var txtW = winW - 118;
		$('.srv-tbl-txt').width(txtW);
	}
	else {}
}

// On Hover
$(document).on('click', '.bd-srv-tbl-row', function() {
	if ($(window).width() < 768) {
		if ($(this).find('.srv-tbl-rem').css('display') == 'none') {
			$(this).find('.srv-tbl-rem').show();
			$(this).find('.srv-tbl-edit').show();
            $(this).find('.srv-tbl-move-up').show();
            $(this).find('.srv-tbl-move-dn').show();
		} else {
			$(this).find('.srv-tbl-rem').hide();
			$(this).find('.srv-tbl-edit').hide();
            $(this).find('.srv-tbl-move-up').hide();
            $(this).find('.srv-tbl-move-dn').hide();
		}
	} else {
		// $(this).find('.srv-tbl-rem').hide();
		// $(this).find('.srv-tbl-edit').hide();
	}
});

$(document).on('mouseover', '.bd-srv-tbl-row', function() {
	if ($(window).width() > 768) {
		$(this).addClass('bd-srv-tbl-row-hover');
		$(this).find('.srv-tbl-rem').show();
		$(this).find('.srv-tbl-edit').show();
        $(this).find('.srv-tbl-move-up').show();
        $(this).find('.srv-tbl-move-dn').show();
	}
});

$(document).on('mouseout', '.bd-srv-tbl-row', function() {
	if ($(window).width() > 768) {
		$(this).removeClass('bd-srv-tbl-row-hover');
		$(this).find('.srv-tbl-rem').hide();
		$(this).find('.srv-tbl-edit').hide();
        $(this).find('.srv-tbl-move-up').hide();
        $(this).find('.srv-tbl-move-dn').hide();
	}
});

// Add Survey Question overlay
$(document).on('click', '#btn-add-question', function() {
	$('#bd-srv-pu').show();
	$(document).addClass('body-no-scroll');
});

function revertQuestionOverlay() {
	var url = "./revertquestionoverlay.do";
	callAjaxGET(url, function(data) {
		$('#bd-quest-wrapper').html(data);
	}, true);
	
	$('#bd-srv-pu').hide();
	$(document).removeClass('body-no-scroll');
	currentQues = 1;
}

$(document).on('click', '.bd-q-btn-done', function() {
	var lastQuestion = currentQues - 1;
	var count = 1;
	var editedStatus = true;
	while (count <= currentQues) {
		if ($('#bs-question-' + count).attr('data-status') == 'edited') {
			editedStatus = true;
			break;
		}
		else {
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

	$('#overlay-continue').click(function(){
		var count = 1;
		while (count <= lastQuestion) {
			// submit for adding question
			if (count > 0 && $('#bs-question-' + count).attr('data-state') == 'new'
				&& $('#bs-question-' + count).attr('data-status') == 'edited') {
				
				if ($('#sb-question-txt-' + count).val() == '' || $('#sb-question-type-' + count).val() == '') {
					$("#overlay-toast").html('Please finish adding the Question');
					showToast();
				} else {
					var url = "./addquestiontosurvey.do?order=" + count;
					$('#bs-question-' + count).attr('data-state', 'editable');
					$('#bs-question-' + count).attr('data-status', 'new');
					callAjaxFormSubmit(url, function(data) {
						var map =  $.parseJSON(data);
						$("#overlay-toast").html(map.message);
						showToast();
						
						if (map.status == "success") {
							$('#bs-question-' + count).attr('data-quesref', map.questionId);
							revertQuestionOverlay();
						} else {
							$('#bs-question-' + count).attr('data-state', 'new');
							$('#bs-question-' + count).attr('data-status', 'edited');
						}
					}, 'bs-question-' + count);
				}
			}
			// submit for modifying question
			else if (count > 0 && $('#bs-question-' + count).attr('data-state') == 'editable'
				&& $('#bs-question-' + count).attr('data-status') == 'edited') {
				
				if ($('#sb-question-txt-' + count).val() == '' || $('#sb-question-type-' + count).val() == '') {
					$("#overlay-toast").html('Please finish editing the Question');
					showToast();
				} else {
					var questionId = $('#bs-question-' + count).attr('data-quesref');
					var url = "./updatequestionfromsurvey.do?order=" + count + "&questionId=" + questionId;
					callAjaxFormSubmit(url, function(data) {
						var map =  $.parseJSON(data);
						$("#overlay-toast").html(map.message);
						showToast();
						
						if (map.status == "success") {
							revertQuestionOverlay();
							$('#bs-question-' + count).attr('data-status', 'new');
						} else {
							$('#bs-question-' + count).attr('data-status', 'edited');
						}
					}, 'bs-question-' + count);
				}
			}
			count ++;
		}
		
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		overlayRevert();
		setTimeout(function() {
			loadActiveSurveyQuestions();
		}, 2000);
	});
	$('#overlay-cancel').click(function(){
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		overlayRevert();
		
		revertQuestionOverlay();
		loadActiveSurveyQuestions();
	});
});

$(document).on('click', '.bd-q-pu-close', function() {
	$(this).parent().parent().remove();
});

// Question edit
$(document).on('click', '.srv-tbl-edit', function() {
	var questionId = $(this).parent().parent().data('questionid');
	var url = "./getsurveyquestion.do?questionId=" + questionId;

	callAjaxGET(url, function(response) {
		$('.sb-edit-q-wrapper').remove();
		$('.bd-q-pu-done-wrapper').remove();
		$('.bd-srv-tbl-row-' + questionId).after(response);
		revertQuestionOverlay();
	}, true);
});

$(document).on('input', '.bd-q-pu-txt-edit', function() {
	var quesNum = $(this).closest('form').data('quesnum');
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
			var map =  $.parseJSON(data);
			
			if (map.status == "success") {
				showInfo(map.message);
				$('.bd-srv-tbl-row-' + questionId).next().remove();
				
				delay(function() {
					loadActiveSurveyQuestions();
				}, 500);
			} else {
				showStatus('#bs-question-edit-' + questionId, 'Retry Saving');
			}
		}, 'bs-question-edit-' + questionId);
	}
});

// Select question type
$(document).on('click', '.bd-tab-rat', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-rating').show();
});

$(document).on('click', '.bd-tab-mcq', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-mcq').show();
	
	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum+'"]').val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-tab-com', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-com').show();

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum+'"]').val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-ans-img-wrapper', function() {
	$(this).parent().parent().find('.bd-ans-img').addClass('bd-img-sel');
	$(this).find('.bd-ans-img').removeClass('bd-img-sel');

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('input[name="sb-question-type-' + quesNum+'"]').val($(this).data('id'));
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

// Submit previous question
var currentQues = 1;
$(document).on("focus", '.bd-q-pu-txt', function() {
	var quesOrder = $(this).closest('form').data('quesnum') - 1;
	
	// submit for adding new question
	if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'new'
			&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
		
		if ($('#sb-question-txt-' + quesOrder).val() == '' || $('#sb-question-type-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish adding the Question');
			showToast('error');
		} else {
			var url = "./addquestiontosurvey.do?order=" + quesOrder;
			showProgress('#bs-question-' + quesOrder);
			$('#bs-question-' + quesOrder).attr('data-state', 'editable');
			$('#bs-question-' + quesOrder).attr('data-status', 'new');
			callAjaxFormSubmit(url, function(data) {
				var map =  $.parseJSON(data);
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
			}, 'bs-question-' + quesOrder);
		}
	}
	// submit for modifying question
	else if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'editable'
		&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
		
		if ($('#sb-question-txt-' + quesOrder).val() == '' || $('#sb-question-type-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish editing the Question');
			showToast();
		} else {
			var questionId = $('#bs-question-' + quesOrder).attr('data-quesref');
			var url = "./updatequestionfromsurvey.do?order=" + quesOrder + "&questionId=" + questionId;
			showProgress('#bs-question-' + quesOrder);
			callAjaxFormSubmit(url, function(data) {
				var map =  $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();
				
				if (map.status == "success") {
					showStatus('#bs-question-' + quesOrder, 'Saved');
					$('#bs-question-' + quesOrder).attr('data-status', 'new');
				} else {
					showStatus('#bs-question-' + quesOrder, 'Retry Saving');
					$('#bs-question-' + quesOrder).attr('data-status', 'edited');
				}
			}, 'bs-question-' + quesOrder);
		}
	}
});

$(document).on("input", '.bd-q-pu-txt', function() {
	var quesPresent = $(this).closest('form').data('quesnum');
	
	// Setting status
	showStatus('#bs-question-' + quesPresent, 'Edited');
	$('#bs-question-' + quesPresent).attr('data-status', 'edited');
	
	// populating next question
	if ($(this).val().trim().length > 0) {
		$(this).parent().next('.bs-ans-wrapper').show();

		if ($(this).data('nextquest') == false) {
			currentQues ++;
			
			var url = "./populatenewform.do?order=" + currentQues;
			$('#sb-question-txt-' + quesPresent).data('nextquest', 'true');
			callAjaxGET(url, function(data) {
				$('#bs-question-' + quesPresent).after(data);
				$('#bs-question-' + quesPresent).next('.bd-quest-item').show();
			}, true);
		}
	}
	
	/*if ($(this).data('qno') != '1') {
		$(this).next('.bd-q-pu-close').show();
	}*/
});

$(document).on('input', '.bd-mcq-txt', function(){
	// changing status to edited
	var name = $(this).attr('name');
	var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));
	
	showStatus('#bs-question-' + addMcqTextOption, 'Edited');
	$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
});

$(document).on('blur', '.bd-mcq-txt', function(){
	if ($(this).parent().is(':last-child')) {
		var name = $(this).attr('name');
		var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));

		// changing status to edited
		showStatus('#bs-question-' + addMcqTextOption, 'Edited');
		$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
		
		var htmlData = '<div class="bd-mcq-row clearfix">'
				+ '<div class="float-left bd-mcq-lbl">Option</div>'
				+ '<input name="sb-answers-' + addMcqTextOption + '[]" class="float-left bd-mcq-txt">'
				+ '<div class="float-left bd-mcq-close"></div>'
			+ '</div>';
		$(this).parent().after(htmlData);
		
		// enable remove button
		if ($(this).parent().parent().children().length > 2) {
			$('.bd-mcq-close').removeClass('hide');
		}
	}
});

$(document).on('click', '.bd-mcq-close', function(){
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

// Remove Question from survey
$(document).on('click', '.srv-tbl-rem', function(e){
	e.stopPropagation();
	var questionId = $(this).parent().parent().data('questionid');
	var url = "./removequestionfromsurvey.do?questionId=" + questionId;
	
	createPopupConfirm("Delete Question", "Do you want to delete the question ?", "Delete", "Cancel");
	$('#overlay-continue').click(function(){
		overlayRevert();
		$('#overlay-continue').unbind('click');

		callAjaxPOST(url, commonActiveSurveyCallback, true);
	});
	$('#overlay-cancel').click(function(){
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		overlayRevert();
		
		loadActiveSurveyQuestions();
	});
});

// Reorder Question in survey
$(document).on('click', '.srv-tbl-move-up', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().parent().data('questionid'));
	formData.append("reorderType", "up");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

$(document).on('click', '.srv-tbl-move-dn', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().parent().data('questionid'));
	formData.append("reorderType", "down");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

// Overlay Popup
function createPopupConfirm(header, text, ok, cancel) {
	$('#overlay-header').html(header);
	$("#overlay-text").html(text);
	$('#overlay-continue').html(ok);
	$('#overlay-cancel').html(cancel);

	$('#overlay-main').show();
}
/*function overlayRevert() {
	$('#overlay-main').hide();
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');
}*/

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
 * @param data
 */
function displayMessage(data) {
	$("#temp-message").html(data);
	var displayMessageDiv = $("#temp-message #display-msg-div");
	if($(displayMessageDiv).hasClass("success-message")) {
		showInfoMobileAndWeb($(displayMessageDiv).html());
	}
	else if($(displayMessageDiv).hasClass("error-message")) {
		showErrorMobileAndWeb($(displayMessageDiv).html());
	}	
	var invalidMessage = $('#invalid-display-msg-div').text();
	if(invalidMessage != undefined && invalidMessage != ""){
		$('#overlay-toast').html(invalidMessage);
		showToast();
	}
	$("#temp-message").html("");
}

/**
 * checks whether is authorized to build hierarchy and displays message to the user
 */
function checkUserAuthorization(){
	var data = $("#server-message").html();
	var isUserAuthorized = $("#is-user-authorized").val();
	if(isUserAuthorized == "false") {
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
 * @param spanId
 */
function changeTabArrow(spanId) {
	$('.bd-hdr-span').removeClass('bd-hdr-active');
    $('.bd-hdr-span').removeClass('bd-hdr-active-arr');
    $("#"+spanId).addClass('bd-hdr-active');
    $("#"+spanId).addClass('bd-hdr-active-arr');
}

/**
 * function to get the edit form based on tab value 
 */
function getEditSectionFormByTab(tabValue) {
	switch(tabValue){
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
	switch(accountType){
    case 'Enterprise':
    	if(highestRole == 1) {
    		getRegionEditPage();
    	}
    	else if(highestRole == 2) {
    		getOfficeEditPage();
    	}
    	else if(highestRole == 3){
    		getIndividualEditPage();
    	}
    	else {
    		showErrorMobileAndWeb("Sorry you are not authorized to build hierarchy");
    	}
        break;
    case 'Company': 
    	if(highestRole == 1 || highestRole == 2) {
    		getOfficeEditPage();
    	}
    	else if(highestRole == 3){
    		getIndividualEditPage();
    	}
    	else {
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
function getRegionEditPage(){
	var url = "./getregioneditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-region-tab");
}

/**
 * function to get the office edit page
 */
function getOfficeEditPage(){
	var url = "./getofficeeditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-office-tab");
}
/**
 * function to get the individual edit page
 */
function getIndividualEditPage(){
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
	if(isUserAuthorized == "false") {
		$("#bd-edit-form-section :input").prop("disabled",true);
		$("#bd-edit-form-section").click(function(){
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
	
	$("#btn-region-save").click(function(e){
		if(validateRegionForm()){
			addRegion("edit-region-form");
		}
	});
	
	$('#region-name-txt').blur(function() {
		if(validateRegionName(this.id)){
			hideError();
		}
	});

	bindAdminCheckBoxClick();
	
	bindSingleMultipleSelection();
	bindAssignToSelectorClick();
	
	bindRegionSelectorEvents();
	
	$("#btn-office-save").click(function(e){
		if(validateOfficeForm()){
			addOffice("edit-office-form");
		}
	});
	
	$('#office-name-txt').blur(function() {
		if(validateOfficeName(this.id)){
			hideError();
		}
	});
	
	bindOfficeSelectorEvents();
	
	$("#btn-individual-save").click(function(e){
		if(validateIndividualForm()){
			addIndividual("edit-individual-form");
		}
	});
}

function bindSingleMultipleSelection() {
	$('.bd-cust-rad-img').click(function(e) {
        $('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
        $(this).toggleClass('bd-cust-rad-img-checked');
        if($(this).data('type') == "single"){
            $('#bd-single').show();
            $('#bd-multiple').hide();
            showAdminPrivilegesChk();
        }else if($(this).data('type') == "multiple"){
            $('#bd-single').hide();
            $('#bd-multiple').show();
            $('#selected-userid-hidden').val("");
            hideAdminPrivilegesChk();
        }
        $('#user-selection-info').attr('data-user-selection-type',$(this).data('type'));
    });
}

function bindUserSelector() {
	$("#selected-user-txt").click(function() {
		getUsersList("",usersStartIndex,numOfRows);
	});
	$("#selected-user-txt").keydown(function(e) {
		bindArrowKeysWithSelector(e, "selected-user-txt", "users-droplist", getUsersList, "selected-userid-hidden", "data-userid");
	});
	$("#selected-user-txt").keyup(function(e) {
		if(e.which != 38 && e.which != 40 && e.which != 13) {
			var text = $(this).val();
			usersStartIndex = 0;	
			if (text.length > 0) {
				delay(function() {
					getUsersList(text,usersStartIndex,numOfRows);
				}, 500);
			}
			else {
				delay(function() {
					getUsersList("",usersStartIndex,numOfRows);
				}, 500);
			}
		}
	});
}

/**
 * binds the click and keyup of region selector
 */
function bindRegionSelectorEvents(){
	$("#selected-region-txt").keyup(function(e) {
		if(e.which != 38 && e.which != 40 && e.which != 13) {
			var text = $("#selected-region-txt").val();
			if (text.length > 0) {
				delay(function() {
					populateRegionsSelector(text);
				}, 500);
			}else{
				$("#regions-droplist").slideUp(200);
			}
		}
	});
	
	$("#region-selector").click(function(e){
		e.stopPropagation();
		if(!$('#selected-region-txt').is(':disabled')){
			var regionPattern = $("#selected-region-txt").val();
			if(regionPattern == undefined) {
				regionPattern = "";
			}
			populateRegionsSelector(regionPattern);
		}		
	});
}

/**
 * binds the click and keyup of office selector
 */
function bindOfficeSelectorEvents(){
	$("#office-selector").click(function(e){
		e.stopPropagation();
		if(!$('#selected-office-txt').is(':disabled')){
			var officePattern = $("#selected-office-txt").val();
			if(officePattern == undefined) {
				officePattern = "";
			}
			populateOfficesSelector(officePattern);
		}		
	});
	
	$("#selected-office-txt").keyup(function() {
		var text = $("#selected-office-txt").val();
		if (text.length > 0) {
			delay(function() {
				populateOfficesSelector(text);
			}, 500);
		}else {
			$("#offices-droplist").slideUp(200);
		}
	});
}

/**
 * binds the click of assign to selector
 */
function bindAssignToSelectorClick(){
	$('#assign-to-selector').click(function(e) {
		e.stopPropagation();
		$("#assign-to-droplist").slideToggle(200);
	});
	
	$('.hm-assignto-options').click(function(e) {
		e.stopPropagation();
		var assignToOption = $(this).attr('data-assign-to-option');
		$("#assign-to-txt").val($(this).html());
		$("#assign-to-txt").attr("data-assignto",assignToOption);
		
		showSelectorsByAssignToOption(assignToOption);
		$("#assign-to-droplist").slideToggle(200);
	});
}

/**
 * binds the check and uncheck of admin privileges checkbox
 */
function bindAdminCheckBoxClick(){
	$('.bd-check-img').unbind('click');
	$('.bd-check-img').click(function(){
		/* $(this).toggleClass('bd-check-img-checked');*/
		/**
		 * If class is "bd-check-img-checked", check box is unchecked ,
		 * hence setting the hidden value as false
		 */
		 if($(this).hasClass('bd-check-img-checked') ){
			$(this).removeClass('bd-check-img-checked');
			$(this).next("#is-admin-chk").val("true");
		 }
		 else {
			$(this).addClass('bd-check-img-checked');
			$(this).next("#is-admin-chk").val("false");
		 }
	});
}

/**
 * Method to show/hide the other selectors based on the assign to option selected
 * @param assignToOption
 */
function showSelectorsByAssignToOption(assignToOption) {
	console.log("selector----------"+assignToOption);
	switch(assignToOption) {
	case 'company':
		disableRegionSelector();
		disableOfficeSelector();
		hideAdminPrivilegesChk();
		break;
	case 'region':
		$("#selected-region-txt").prop("disabled",false);
		disableOfficeSelector();
		$("#bd-region-selector").show();
		showAdminPrivilegesChk();
		break;
	case 'office':
		$("#selected-office-txt").prop("disabled",false);
		$("#bd-office-selector").show();
		showAdminPrivilegesChk();
		disableRegionSelector();
		break;
	default:
		$("#selected-region-txt").prop("disabled",false);
		$("#selected-office-txt").prop("disabled",false);
	}
}

function showAdminPrivilegesChk(){
	$("#admin-privilege-div").show();
	if(!$('.bd-check-img').hasClass('bd-check-img-checked') ){
		$('.bd-check-img').next("#is-admin-chk").val("true");
		$('.bd-check-img').removeClass('bd-check-img-checked');
	}
}

function hideAdminPrivilegesChk(){
	$("#admin-privilege-div").hide();
	$('.bd-check-img').next("#is-admin-chk").val("false");
	$('.bd-check-img').addClass('bd-check-img-checked');
}

function disableRegionSelector(){
	$("#selected-region-txt").prop("disabled",true);
	$("#selected-region-txt").val("");
	$('#selected-region-id-hidden').val("");
	$("#bd-region-selector").hide();
}

function disableOfficeSelector(){
	$("#selected-office-txt").prop("disabled",true);
	$("#selected-office-txt").val("");
	$('#selected-office-id-hidden').val("");
	$('#selected-region-id-hidden').val("");
	$("#bd-office-selector").hide();
}

/**
 * Region details validation
 */
var isRegionValid;

/**
 * Function to validate Region name
 */
function validateRegionName(elementId){
	if ($('#'+elementId).val() != "") {
		if (companyNameRegEx.test($('#'+elementId).val()) == true) {
			return true;
		}else {
			showErrorMobileAndWeb('Please enter a valid region name.');
			return false;
		}
	}else{
		showErrorMobileAndWeb('Please enter region name.');
		return false;
	}
}

function validateUserEmailTextArea(elementId) {
	var emailIds = $('#'+elementId).val();
	if (emailIds != "") {
		var emailIdsArray = emailIds.split(/[,;\n]/);
		for(var i = 0; i < emailIdsArray.length; i++) {
			var emailId = emailIdsArray[i].trim();
			if(emailId == ""){
				continue;
			}
			if(emailId.indexOf(">") > -1){
				emailId = emailId.substring(emailId.indexOf("<")+1,emailId.length-1);
			}

			if(emailRegex.test(emailId) == false){
				showErrorMobileAndWeb('Please enter valid email addresses');
				return false;
			}
		}
		return true;
	}
}

function validateUserSelection(elementId,hiddenElementId) {
	if ($('#'+elementId).val() != "") {
		var emailId = $('#'+elementId).val();
		if(emailId.indexOf('"') > -1){
			emailId = emailId.split('"').join("");
		}
		if(emailId.indexOf("<") > -1){
			emailId = emailId.substring(emailId.indexOf("<")+1,emailId.indexOf(">"));
		}
		if($("#"+hiddenElementId).val() != ""){
			return true;
		}
		
		else if (emailRegex.test(emailId) == true) {
			return true;
		}
		else {
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
	
	
	if(!validateRegionName('region-name-txt')){
		isRegionValid = false;
		if(!isFocussed){
			$('#region-name-txt').focus();
			isFocussed=true;
		}
		return isRegionValid;
	}
	
	if(!validateAddress1('region-address1-txt')){
		isOfficeValid = false;
		if(!isFocussed){
			$('#office-address-txt').focus();
			isFocussed=true;
		}
		return isRegionValid;
	}
	
	var userSelectionType = $('#user-selection-info').data('user-selection-type');
	if(userSelectionType =="single"){
	
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
	
	}
	else {
		
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
		
		
	}
	
	
	if(isRegionValid){
		hideError();
	}
	return isRegionValid;
}

/**
 * clear input fields within specified form/div
 */
function resetInputFields(elementId) {
	$("#"+elementId+" :input:not('.ignore-clear')").val("");
}

/**
 * js function for adding a region
 */
function addRegion(formId) {
	var url = "./addregion.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, addRegionCallBack, formId);
}

/**
 * call back function for add region
 * 
 * @param data
 */
function addRegionCallBack(data) {
	hideOverlay();
	displayMessage(data);
	$('#region-state-city-row').hide();
	resetInputFields("edit-region-form");
	fetchCompleteHierarchy();
}

/**
 * Method to fetch list of already existing users
 * @param searchKey
 * @param start
 * @param rows
 */
function getUsersList(searchKey,start,rows) {
	var url="./finduserbyemail.do?startIndex="+start+"&batchSize="+rows+"&searchKey="+searchKey;
	callAjaxGET(url, paintUsersList, true);
}

/**
 * Callback for getUsersList, populates the drop down with users list obtained
 * @param data
 */
function paintUsersList(data) {
	var usersList = $.parseJSON(data);
	var htmlData = "";
	if(usersList != null) {
		var len = usersList.length;
		if(len > 0) {
			$('#selected-userid-hidden').val("");
			$.each(usersList,function(i,user) {
				var displayName = user.firstName;
				if(user.lastName != undefined) {
					displayName = displayName +" "+ user.lastName;
				}
				htmlData = htmlData +'<div class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-user-options" data-userid="'+user.userId+'">'+displayName+'</div>';
			});
		}
	}
	if(htmlData != "") {
		$("#users-droplist").html(htmlData).slideDown(200);
	}
	else{	
		$("#users-droplist").slideUp(200);
		
	}
	
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
function validateOfficeName(elementId){
	if ($('#'+elementId).val() != "") {
		if (companyNameRegEx.test($('#'+elementId).val()) == true) {
			return true;
		}else {
			showErrorMobileAndWeb('Please enter a valid office name.');
			return false;
		}
	}else{
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
function validateRegionSelector(hiddenElementId,textElementId) {
	var assignToType = $("#assign-to-txt").attr("data-assignto");
	if(assignToType == 'region'){
		if ($('#'+hiddenElementId).val() == "" || $('#'+textElementId).val() == "") {
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
	
	if(!validateOfficeName('office-name-txt')){
		isOfficeValid = false;
		if(!isFocussed){
			$('#office-name-txt').focus();
			isFocussed=true;
		}
		return isOfficeValid;
	}
	
	if(!validateRegionSelector('selected-region-txt', 'selected-region-id-hidden')) {
		isOfficeValid = false;
		if(!isFocussed){
			$('#selected-region-txt').focus();
			isFocussed=true;
		}
		return isOfficeValid;
	}
	if(!validateAddress1('office-address-txt')){
		isOfficeValid = false;
		if(!isFocussed){
			$('#office-address-txt').focus();
			isFocussed=true;
		}
		return isOfficeValid;
	}
	
	var userSelectionType = $('#user-selection-info').data('user-selection-type');
	if(userSelectionType =="single"){
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
	}
	else {
		
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
				
	}
	
	if(isOfficeValid){
		hideError();
	}
	return isOfficeValid;
}

/**
 * js function for adding a branch
 */
function addOffice(formId) {
	var url = "./addbranch.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, addOfficeCallBack, formId);
}

/**
 * call back function for add branch
 * 
 * @param data
 */
function addOfficeCallBack(data) {
	hideOverlay();
	displayMessage(data);
	$('#office-state-city-row').hide();
	resetInputFields("edit-office-form");
	fetchCompleteHierarchy();
}

/**
 * Method to fetch regions from solr for populating region selector
 * 
 * @param regionPattern
 */
function populateRegionsSelector(regionPattern) {
	console.log("Method populateRegionsSelector called for regionPattern : "+regionPattern);
	var url = "./searchregions.do?regionPattern="+regionPattern+"&start=0&rows=-1";
	callAjaxGET(url, populateRegionsSelectorCallBack, true);
}

/**
 * callback method for fetching regions from solr for populating region selector
 * @param data
 */
function populateRegionsSelectorCallBack(data) {
	var searchResult = $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		console.log("searchResult is "+searchResult);
		if(len > 0) {
			$.each(searchResult,function(i,region) {
					htmlData = htmlData +'<div data-regionId="'+region.regionId+'" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-region-option">'+region.regionName+'</div>';
			});
		}
		if(htmlData != ""){
			$("#regions-droplist").html(htmlData).slideDown(200);
			// bind the click event of selector
			$('.hm-region-option').click(function(e) {
				e.stopPropagation();
				$('#selected-region-txt').val($(this).html());
				$('#selected-region-id-hidden').val($(this).data('regionid'));
				$('#regions-droplist').slideToggle(200);
			});	
			
			//bind the hover event
			$(".hm-dd-hover").hover(function() {
				$(".hm-region-option").removeClass("hm-dd-item-keys-selected");
			});
			$("#selected-region-txt").keydown(function(e){
				bindArrowKeysWithSelector(e, "selected-region-txt", "regions-droplist", populateRegionsSelector, "selected-region-id-hidden", "data-regionid");
			});			
		}
		else {
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
function validateOfficeSelector(hiddenElementId,textElementId) {
	var assignToType = $("#assign-to-txt").attr("data-assignto");
	if(assignToType == 'office'){
		if ($('#'+hiddenElementId).val() == "" || $('#'+textElementId).val() == "") {
			showErrorMobileAndWeb('Please select an office');
			return false;
		}
		return true;
	}
	return true;
}

/**
 * function to validate user selection in case of individual addition
 * @param elementId
 * @returns {Boolean}
 */
function validateIndividualSelection(elementId) {
	if ($('#'+elementId).val() == "") {
		showErrorMobileAndWeb('Please select a user or enter atleast one email address');
		return false;
	}
	return true;
}

/**
 * function to validate the individual form
 * @returns {Boolean}
 */
function validateIndividualForm() {
	isIndividualValid = true;
	var isFocussed = false;
	
	if(!validateRegionSelector('selected-region-txt', 'selected-region-id-hidden')) {
		isIndividualValid = false;
		if(!isFocussed){
			$('#selected-region-txt').focus();
			isFocussed=true;
		}
	}
	
	if(!validateOfficeSelector('selected-office-txt', 'selected-office-id-hidden')) {
		isIndividualValid = false;
		if(!isFocussed){
			$('#selected-office-txt').focus();
			isFocussed=true;
		}
	}
	
	var userSelectionType = $('#user-selection-info').data('user-selection-type');
	if(userSelectionType =="single"){
		if(!validateIndividualSelection('selected-user-txt')) {
			isIndividualValid = false;
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
		}
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
	}
	else {
		if(!validateIndividualSelection('selected-user-txt-area')) {
			isIndividualValid = false;
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
		}
		
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
				
	}
	
	if(isIndividualValid){
		hideError();
	}
	return isIndividualValid;
}

function addIndividual(formId) {
	var url = "./addindividual.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, addIndividualCallBack, formId);
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
	console.log("Method populateOfficesSelector called for officePattern : "+officePattern);
	var url = "./searchbranches.do?branchPattern="+officePattern+"&start=0&rows=-1";
	callAjaxGET(url, populateOfficesSelectorCallBack, true);
}

/**
 * callback method for fetching offices(branches) from solr for populating office selector
 * @param data
 */
function populateOfficesSelectorCallBack(data) {
	var searchResult = $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		console.log("searchResult is "+searchResult);
		if(len > 0) {
			$.each(searchResult,function(i,branch) {
					htmlData = htmlData +'<div data-regionid="'+branch.regionId+'" data-officeid="'+branch.branchId+'" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-office-option">'+branch.branchName+'</div>';
			});
		}
		if(htmlData != ""){
			$("#offices-droplist").html(htmlData).slideDown(200);	
			
			// bind the click event of selector
			$('.hm-office-option').click(function(e) {
				e.stopPropagation();
				$('#selected-office-txt').val($(this).html());
				$('#selected-region-id-hidden').val($(this).data('regionid'));
				$('#selected-office-id-hidden').val($(this).data('officeid'));
				$('#offices-droplist').slideToggle(200);
			});	
			
			//bind the hover event
			$(".hm-dd-hover").hover(function() {
				$(".hm-office-option").removeClass("hm-dd-item-keys-selected");
			});
		}
		else {
			$("#offices-droplist").html(htmlData).slideUp(200);	
		}
	}	
}

function bindArrowKeysWithSelector(e,textBoxId,dropListId,populatorFunction,hiddenFieldId,attrName) {
	console.log(e.which);
	if(e.which == 40) {
		var text = $("#"+textBoxId).val();
		if(text == undefined) {
			text = "";
		}
		if (!($("#"+dropListId).css("display") =="block")){
			delay(function() {
				populatorFunction(text);
			}, 500);
		}else {
			var current = $("#"+dropListId).find(".hm-dd-item-keys-selected");
			if(current.length > 0) {
				$(current).removeClass("hm-dd-item-keys-selected");
				$(current).next().addClass("hm-dd-item-keys-selected");
			}
			else {
				$("#"+dropListId +" :first-child").addClass("hm-dd-item-keys-selected");
			}
			$("#"+dropListId).show();
		}
		
	}	
	else if(e.which == 38){
		var current = $("#"+dropListId).find(".hm-dd-item-keys-selected");
		if(current.length > 0) {
			$(current).removeClass("hm-dd-item-keys-selected");
			$(current).prev().addClass("hm-dd-item-keys-selected");
		}else {
			$('#'+dropListId).slideUp(200);
		}
	}else if(e.which == 13) {
		var selectedItem = $("#"+dropListId).find(".hm-dd-item-keys-selected");
		if(selectedItem.length == 0) {
			selectedItem = $("#"+dropListId+" :first-child");
		}
		$('#'+textBoxId).val($(selectedItem).html());
		$('#'+hiddenFieldId).val($(selectedItem).attr(attrName));
		$('#'+dropListId).slideToggle(200);	
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
	var url = "./fetchhierarchyviewbranches.do?regionId="+regionId;
	callAjaxGET(url, function(data) {
		paintHierarchyViewBranches(data,regionId);
	}, true);
}

function paintHierarchyViewBranches(data,regionId) {
	$("#td-region-edit-"+regionId).parent(".tr-region-edit").after(data);
	$("#tr-region-"+regionId).slideDown(200);
	$(".tr-region-edit").slideUp(200);
	bindUserEditClicks();
	bindBranchListClicks();
}

function bindBranchListClicks(){
	$(".branch-edit-icn").unbind('click');
	$(".branch-edit-icn").click(function(e){
		e.stopPropagation();
		var branchId = $(this).attr("data-branchid");
		if($(this).attr('clicked') == "false"){
			showBranchEdit(branchId);
			$(this).attr('clicked','true');
		}
		else {
			hideBranchEdit(branchId);
			$(this).attr('clicked','false');
		}		
	});
	$(".branch-row").unbind('click');
	$(".branch-row").click(function(e){
		e.stopPropagation();
		var branchId = $(this).attr("data-branchid");
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			fetchUsersForBranch(branchId,regionId);
			 $(this).attr('clicked','true');
		}
		else {
			$('.user-row-'+branchId).html("").hide(); 
            $(this).attr('clicked','false');
		}
	});
	$(".branch-del-icn").unbind('click');
	$(".branch-del-icn").click(function(e){
		var branchId = $(this).attr("data-branchid");
		deleteBranchPopup(branchId);
	});
}

function fetchHierarchyViewList() {
	var url = "./fetchhierarchyviewlist.do";
	callAjaxGET(url, paintHierarchyViewList, true);
}
function paintHierarchyViewList(data) {
	$("#hierarchy-list-header").siblings().remove();
	$("#hierarchy-list-header").after(data);
	bindRegionListClicks();
    $('.v-tbl-icn').click(function(e){
        e.stopPropagation();
    });
    bindBranchListClicks();
    bindUserEditClicks();
}

function bindRegionListClicks() {
	$(".region-row").click(function(e){
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			fetchHierarchyViewBranches(regionId);
			 $(this).attr('clicked','true');
		}
		else {
			$("tr[class*='sel-r"+regionId+"'").html("").hide();
            $(this).attr('clicked','false');
		}
	});
	$(".region-edit-icn").click(function(e){
		e.stopPropagation();
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			showRegionEdit(regionId);
			$(this).attr('clicked','true');
		}
		else {
			hideRegionEdit(regionId);
			$(this).attr('clicked','false');
		}		
	});
	$(".region-del-icn").unbind('click');
	$(".region-del-icn").click(function(e){
		var regionId = $(this).attr("data-regionid");
		deleteRegionPopup(regionId);
	});
}

function showRegionEdit(regionId) {
	var url = "./getregioneditpage.do?regionId="+regionId;
	callAjaxGET(url, function(data){
		showRegionEditCallBack(data, regionId);
	}, true);
}
function showRegionEditCallBack(data,regionId) {
	$(".td-region-edit").html("").hide();
	$(".tr-region-edit").hide();
	$("#td-region-edit-"+regionId).parent(".tr-region-edit").slideDown(200);
	$("#td-region-edit-"+regionId).html(data).slideDown(200);
	bindSingleMultipleSelection();
	bindAdminCheckBoxClick();
	bindUserSelector();
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);
	bindAssignToSelectorClick();
	$("#btn-region-update").click(function(e) {
		var regionId = $(this).attr("data-regionid");
		if(validateRegionForm()){
			updateRegion("edit-region-form",regionId);
		}
	});
	
}

function hideRegionEdit(regionId) {
	$(".td-region-edit").html("").hide();
	$("#td-region-edit-"+regionId).hide();
	$(".tr-region-edit").hide();
}

function showBranchEdit(branchId) {
	var url = "./getofficeeditpage.do?branchId="+branchId;
	callAjaxGET(url, function(data){
		showBranchEditCallBack(data, branchId);
	}, true);
}

function showBranchEditCallBack(data,branchId) {
	$("#td-branch-edit-"+branchId).parent(".tr-branch-edit").slideDown(200);
	$("#td-branch-edit-"+branchId).html(data).slideDown(200);
	bindSingleMultipleSelection();
	bindUserSelector();
	bindRegionSelectorEvents();
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);
	bindAssignToSelectorClick();
	$("#btn-office-update").click(function(e){
		updateBranch("edit-office-form", branchId);
	});
}

function hideBranchEdit(branchId) {
	$("#td-branch-edit-"+branchId).slideUp(200);
	$("#td-branch-edit-"+branchId).parent(".tr-branch-edit").hide();
}

function fetchUsersForBranch(branchId,regionId) {
	var url="./fetchbranchusers.do?branchId="+branchId+"&regionId="+regionId;
	callAjaxGET(url, function(data) {
		paintUsersFromBranch(data,branchId);
	}, true);
}

function paintUsersFromBranch(data,branchId,regionId) {
	$("#td-branch-edit-"+branchId).parent(".tr-branch-edit").after(data);
	$("#tr-branch-"+branchId).slideDown(200);
	$(".tr-branch-edit").slideUp(200);
	bindUserEditClicks();
}

function bindUserEditClicks() {
	$(".user-edit-icn").unbind('click');
	$('.user-edit-icn').click(function(e){
		e.stopPropagation();
		if($(this).attr('clicked') == "false") {
			// make an ajax call and fetch the details of the user
			var userId = $(this).attr('data-userid');
			$(".user-assignment-edit-div").html("");
			$(".user-edit-row").slideUp();
			var elementToAppendTo = $(this).closest('tr').next('tr.user-edit-row').find('td.td-user-edit');
			getUserAssignments(userId,elementToAppendTo);
			$(this).parent().parent().parent().next('.user-edit-row').slideDown(200);
			$(this).attr('clicked','true');
	    }else {
			$(this).parent().parent().parent().next('.user-edit-row').slideUp(200);
			$(".user-assignment-edit-div").html("");
			$(".user-edit-row").slideUp();
			$(this).attr('clicked','false');
	    }
	});
	$(".user-del-icn").unbind('click');
	$(".user-del-icn").click(function(e){
		e.stopPropagation();
		var userId = $(this).attr("data-userid");
		confirmDeleteUser(userId);
	});
}

function updateRegion(formId,regionId) {
	var url = "./updateregion.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, function(data){
		updateRegionCallBack(data, regionId);
	}, formId);
}

function updateRegionCallBack(data,regionId) {
	hideOverlay();
	displayMessage(data);
	hideRegionEdit(regionId);
	fetchHierarchyViewList();
}

function updateBranch(formId,branchId) {
	var url = "./updatebranch.do";
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, function(data){
		updateBranchCallBack(data,branchId);
	}, formId);
}

function updateBranchCallBack(data,branchId) {
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
		
		$('#overlay-continue').click(function(){
			if ($('#overlay-continue').attr("disabled") != "disabled") {
				if(regionId != null) {
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
		createPopupConfirm("Remove Branch");
		
		$('#overlay-continue').click(function(){
			if ($('#overlay-continue').attr("disabled") != "disabled") {
				if(branchId != null) {
					overlayRevert();
					deleteBranch(branchId);
					branchId = null;
				}
				$('#overlay-continue').unbind('click');
			}
		});
	} else {
		createPopupInfo("Remove Branch");
		branchId = null;
	}
}

//Pop-up Overlay modifications
$('#overlay-cancel').click(function(){
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
/*function overlayRevert() {
	$('#overlay-main').hide();
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');
}*/

/**
 * Function to delete a region
 * 
 * @param branchId
 */
function deleteRegion(regionId) {
	var url = "./deactivateregion.do?regionId=" + regionId;
	callAjaxPOST(url, function(data){
		deleteRegionCallBack(data,regionId);
	}, true);
}

/**
 * Call back function for deleting a region
 * 
 * @param data
 */
function deleteRegionCallBack(data,regionId) {
	displayMessage(data);
	$("#tr-region-"+regionId).hide();
	$("#tr-region-"+regionId).next(".tr-region-edit").hide();
}

/**
 * Function to delete a branch
 * 
 * @param branchId
 */
function deleteBranch(branchId) {
	var url = "./deactivatebranch.do?branchId=" + branchId;
	callAjaxPOST(url, function(data){
		deleteBranchCallBack(data,branchId);
	}, true);
}

/**
 * Call back function for deleting a branch
 * 
 * @param data
 */
function deleteBranchCallBack(data,branchId) {
	displayMessage(data);
	$("#tr-branch-row-"+branchId).hide();
	$("#tr-branch-row-"+branchId).next(".tr-branch-edit").hide();
}

function resendVerificationMail(){
	$.ajax({
		url : "./sendverificationmail.do",
		type : "GET",
		dataType : "text",
		success : function(data) {
			if (data.errCode == undefined){
				$('#overlay-toast').html(data);
				showToast();
				hideError();
				hideInfo();
			}
			else {
				$('#overlay-toast').html(data);
				showToast();
				hideError();
				hideInfo();
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
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
		callAjaxFormSubmit(url, saveEncompassDetailsCallBack, formid);
	}
}

function saveEncompassDetailsCallBack(response) {
	$("#overlay-toast").html(response);
	showToast();
}

function testEncompassConnection(formid) {
	if (validateEncompassInput(formid)) {
		var url = "./testencompassconnection.do";
		callAjaxFormSubmit(url, testEncompassConnectionCallBack, formid);
	}
}

function testEncompassConnectionCallBack(response) {
	$("#overlay-toast").html(response);
	showToast();
}

var isEncompassValid;
function validateEncompassInput(elementId) {
	isEncompassValid = true;
	var isFocussed = false;
	
	if(!validateEncompassUserName('encompass-username')){
		isEncompassValid = false;
		if(!isFocussed){
			$('#encompass-username').focus();
			isFocussed=true;
		}
	}
	if(!validateEncompassPassword('encompass-password')){
		isEncompassValid = false;
		if(!isFocussed){
			$('#encompass-password').focus();
			isFocussed=true;
		}
	}
	if (!validateURL('encompass-url')) {
		isEncompassValid = false;
		if(!isFocussed){
			$('#encompass-url').focus();
			isFocussed=true;
		}
	}
	return isEncompassValid;
}


// Mail content
function updateMailContent(formid){
	var url = "./savesurveyparticipationmail.do";
	callAjaxFormSubmit(url, updateMailContentCallBack, formid);
}

function updateMailContentCallBack(response){
	$("#overlay-toast").html(response);
	showToast();
}


// Mail Reminder
function autoAppendReminderDropdown(reminderId, reminderDefault) {
	autoAppendDropdown(reminderId, 15, 1);
}

function updateReminderSettings(formid) {
	var url = "./updatesurveyremindersettings.do";
	callAjaxFormSubmit(url, updateReminderSettingsCallBack, formid);
}

function updateReminderSettingsCallBack(response){
	$("#overlay-toast").html(response);
	showToast();
}


// Ratings Settings
function autoAppendRatingDropdown(ratingId, classes) {
	autoAppendDropdown(ratingId, classes, 5, 0.5);
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
	if($(status).val() == 'true') {
	$(on).show();
        $(off).hide();
	} else if($(status).val() == 'false') {
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
function autoAppendDropdown(elementId, classes, maxVal, minVal) {
	var value = 0;
	while (maxVal - value >= minVal) {
		$(elementId).append($('<div/>').addClass(classes).text(maxVal - value));
		value += minVal;
	}
}

function autoSetReminderIntervalStatus() {
	if($('#reminder-needed-hidden').val() == 'true') {
		$('#reminder-interval').attr("disabled", true);
	} else if($('#reminder-needed-hidden').val() == 'false') {
		$('#reminder-interval').removeAttr("disabled");
	}
}

function overlayAccount(){
	$('#othercategory').val('other-account');

	$('#overlay-continue').click(function(){
		$('#st-settings-account-off').toggle();
		$('#st-settings-account-on').toggle();

		overlayRevert();
		updateOtherSettings("other-settings-form");
		$('#othercategory').val('');
		$('#overlay-continue').unbind('click');
	});
	$('#overlay-cancel').click(function(){
		$('#overlay-continue').unbind('click');
		overlayRevert();
		$('#othercategory').val('');
	});
}

function overlayDeleteAccount(){
	$('#othercategory').val('other-account');

	$('#overlay-continue').click(function(){
		overlayRevert();
		confirmDeleteAccount();
		$('#overlay-continue').unbind('click');
	});
	$('#overlay-cancel').click(function(){
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
/*function overlayRevert() {
	$('#overlay-main').hide();
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');
}*/

function showPaymentOptions() {
	console.log("Calling payment controller for payment upgrade page");	
	disableBodyScroll();
	var url = "./paymentchange.do";
    showOverlay();
    callAjaxGET(url,displayPopup);
}

function displayPopup(data){
	console.log("display message called :data "+data);
	$("#temp-div").html(data);
	
	var displayMessageDiv = $("#display-msg-div");
	if($(displayMessageDiv).hasClass("message")) {
		console.log("Error occured. Hiding Overlay");
		hideOverlay();
		$('#st-settings-payment-off').show();
   		$('#st-settings-payment-on').hide();
		console.log("Removing no-scroll class from body");
		enableBodyScroll();
		$("#overlay-toast").html($(displayMessageDiv).html());
		showToast();
	}	
	else{
		$('.overlay-payment').html(data);
    	console.log("Html content loaded");
    	hideOverlay();
    	$('.overlay-payment').show();
    	console.log("Showing popup");
    }
	$("#temp-div").html("");
}

function updateAutoPostSetting(isautopostenabled){
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
			if (success) {
				$('#overlay-toast').html("Content added successfully!");
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			$('#overlay-toast').html(
					"Oops! Something went wrong. Please try again later.");
		}
	});
}

function resetTextForMoodFlow(mood, resetId){
	var payload = {
		"mood" : mood
	};
	callAjaxGetWithPayloadData("./resettextforflow.do", function(data) {
		hideOverlay();
		var map =  $.parseJSON(data);

		if (map.success == 1 && map.message) {
			$('#' + resetId).val(map.message);
			$('#overlay-toast').html("Content reverted successfully!");
		} else {
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
		showToast();
	}, payload, true);
}

function saveTextForMoodFlow(content, mood){
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

function paintTextForMood(happyText, neutralText, sadText, happyTextComplete, neutralTextComplete, sadTextComplete){
	$('#happy-text').html(happyText);
	$('#neutral-text').html(neutralText);
	$('#sad-text').html(sadText);

	$('#happy-text-complete').html(happyTextComplete);
	$('#neutral-text-complete').html(neutralTextComplete);
	$('#sad-text-complete').html(sadTextComplete);
}

//User management
$(document).on('click', '.um-user-row', function() {
	if (!isUserManagementAuthorized)
		return false;
	console.log("user row clicked");
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
		isAddUser=false;
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
	isAddUser=true;
	paintUserDetailsForm("");
	/*if (!validateUserInviteDetails()) {
		return false;
	}
	inviteUser();*/
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
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e);
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
				console.log("User successfully unassigned from branch "
						+ branchId);
				$('#branch-to-unassign-' + branchId).remove();
				
				// check if there are any assigned branches left
				if ($('#um-assigned-branch-container > div').length <= 0) {
					$('#um-assignto').parent().parent().find(
							'.um-item-row-icon').removeClass('icn-tick')
							.addClass('icn-save');
				}
			} else {
				createPopupInfo("Error!", "Branch deletion unsuccessful");
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e);
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
					createPopupInfo("Limit Exceeded",
							"Maximum limit of users exceeded.");
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
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e);
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
	
	$.ajax({
		url : "./removeexistinguser.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			var map =  $.parseJSON(data);
			if (map.status == "success") {
				showInfo(map.message);
			} else {
				showError(map.message);
			}
			
			// hide the row of the user deleted
			$('#user-row-' + userId).next('.v-tbl-row').remove();
			$('#user-row-' + userId).next('.u-tbl-row').remove();
			$('#user-row-' + userId).remove();
		},
		complete : function() {
			hideOverlay();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e);
		}
	});
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
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e);
		}
	});
}

/*
 * Function paint the user list in user management page
 */
function paintUserListInUserManagement(startIndex) {
	var payload = {
		"startIndex" : startIndex,
		"batchSize" : userBatchSize
	};

	$.ajax({
		url : "./findusersforcompany.do",
		type : "GET",
		data : payload,
		dataType : "html",
		success : function(data) {
			$('#user-list').html(data);
			userStartIndex = startIndex;
			updatePaginateButtons();
			bindEditUserClick();
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e);
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
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e);
		}
	});
}

// function to validate input fields before sending the user invite
function validateUserInviteDetails() {
	console.log("Validating user invite input fields");
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

// Function to validate email id in a form
function validateUserEmailId(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (emailRegex.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid email id.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('Please enter email id.');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (emailRegex.test($('#' + elementId).val()) == true) {
				$('#' + elementId).next('.input-error-2').hide();
				return true;
			} else {
				$('#' + elementId).next('.input-error-2').html('Please enter a valid email id.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			$('#' + elementId).next('.input-error-2').html('Please enter email id.');
			$('#' + elementId).next('.input-error-2').show();
			return false;
		}
	}
}

// Function to validate the first name
function validateUserFirstName(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (nameRegex.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid first name.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('please enter first name.');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (nameRegex.test($('#' + elementId).val()) == true) {
				$('#' + elementId).next('.input-error-2').hide();
				return true;
			} else {
				$('#' + elementId).next('.input-error-2').html('Please enter a valid first name.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			$('#' + elementId).next('.input-error-2').html('Please enter first name.');
			$('#' + elementId).next('.input-error-2').show();
			return false;
		}
	}
}

// Function to validate the last name
function validateUserLastName(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (lastNameRegEx.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid last name.');
				showToast();
				return false;
			}
		} else {
			return true;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (lastNameRegEx.test($('#' + elementId).val()) == true) {
				$('#' + elementId).next('.input-error-2').hide();
				return true;
			} else {
				$('#' + elementId).next('.input-error-2').html('Please enter a valid last name.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			return true;
		}
	}
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
		console.log("Enter");
		searchUsersByNameEmailLoginId($(this).val());
	}
});

$(document).on('click', '#um-search-icn', function(e) {
	searchUsersByNameEmailLoginId($('#search-users-key').val());
});

function searchUsersByNameEmailLoginId(searchKey) {
	userStartIndex = 0;
	var url = "./findusers.do";
	var payload = {
		"searchKey" : searchKey
	};
	callAjaxGetWithPayloadData(url, searchUsersByNameEmailLoginIdCallBack, payload, true);
}

function searchUsersByNameEmailLoginIdCallBack(data) {
	$('#user-list').html(data);
	bindEditUserClick();
}

/*function paintUsersList(data) {
	if (userStartIndex == 0) {
		$('#um-user-list').find('tbody').html("");
	}
	userStartIndex += data.length;
	var searchResult = data;
	if (searchResult != null) {
		var len = searchResult.length;
		if (len > 0) {
			$.each(searchResult, function(i, user) {
				var row = $('<tr>').attr({
					"id" : "um-user-" + user.userId,
					"class" : "um-user-row"
				});
				
				var col1 = $('<td>').attr({
					"class" : "col-username um-table-content"
				}).html(user.firstName + " " + user.lastName);
				
				var col2 = $('<td>').attr({
					"class" : "col-email um-table-content"
				}).html(user.emailId);
				
				var col3 = $('<td>').attr({
					"class" : "col-loanoff um-table-content clearfix"
				});
				
				if (user.isAgent) {
					var colImage = $('<div>').attr({
						"class" : "float-left tm-table-tick-icn icn-right-tick"
					});
					col3.append(colImage);
				}
				
				var col4 = $('<td>').attr({
					"class" : "col-status um-table-content clearfix"
				});
				
				if (user.status == 1) {
					var statusIcon = $('<div>').attr({
						"class" : "tm-table-status-icn icn-green-col float-left"
					});
					col4.append(statusIcon);
				} else if (user.status == 3) {
					var statusIcon = $('<div>').attr({
						"class" : "tm-table-status-icn icn-green-brown float-left"
					});
					col4.append(statusIcon);
				}
				
				var col5 = $('<td>').attr({
					"class" : "col-remove um-table-content clearfix"
				});
				
				var iconRemove = $('<div>').attr({
					"class" : "tm-table-remove-icn icn-remove-user float-left cursor-pointer"
				});
				
				col5.append(iconRemove);
				row.append(col1).append(col2).append(col3).append(col4).append(col5);
				$('#um-user-list').find('tbody').append(row);
			});
		} else {
			$('#um-user-list').find('tbody').append("No results found");
		}
	}
}*/

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
			if(e.status == 504) {
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
	callAjaxGET(url, function(data){
		
		if(element == undefined){
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
        $('.tbl-switch-on').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 0);
        });

        // activate user profile
        $('.tbl-switch-off').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 1);
        });
		
		setTimeout(function() {
			$('#profile-tbl-wrapper-' + userId).perfectScrollbar();
		}, 1000);

		$(document).on('click', 'body', function() {
            $('.dd-droplist').slideUp(200);
        });
	} , true);
}

$(document).on('click','#user-edit-btn',function(e){
	
	$('#user-edit-btn-row').hide();
	$('form input[data-editable="true"]').removeAttr("readonly");
	$('#btn-save-user-assignment').show();
	
	$("#user-edit-save").off('click');
	$("#user-edit-save").on('click',function(e){
		if(validateUserDetailsUserManagement()){
			saveUserDetailsByAdmin();
			
			// refreshing right section after assignment
			setTimeout(function() {
				getUserAssignments($('#selected-userid-hidden').val());
			}, 2000);
		}
	});
	$('#user-edit-cancel').on('click',function(){
		setTimeout(function() {
			getUserAssignments($('#selected-userid-hidden').val());
		}, 1000);
	});
});

$(document).on('click','#user-assign-btn',function(e){
	
	$('#user-edit-btn-row').hide();
	$('#user-assignment-cont').show();
	$('#btn-save-user-assignment').show();
	
	$("#user-edit-save").off('click');
	$("#user-edit-save").on('click',function(e){
		if(validateIndividualForm()){
			saveUserAssignment("user-assignment-form");
			
			// refreshing right section after assignment
			setTimeout(function() {
				getUserAssignments($('#selected-userid-hidden').val());
			}, 2000);
		}
	});
	$('#user-edit-cancel').on('click',function(){
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
 * @param formId
 */
function saveUserDetailsByAdmin() {
	var url = "./updateuserbyadmin.do";
	var userId = $('#selected-userid-hidden').val();
	var firstName = $('#um-user-first-name').val();
	var lastName = $('#um-user-last-name').val();
	var emailID = $('#selected-user-txt').val();
	var name = firstName;
	if(lastName && lastName != ""){
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

		//view hierarchy page
		$('.v-tbl-row[data-userid="'+userId+'"]').find('.v-tbl-name').text(name);
		$('.v-tbl-row[data-userid="'+userId+'"]').find('.v-tbl-add').text(emailID);
		
		//user management page
		$('td[data-user-id="'+userId+'"]').text(name).attr("data-first-name",firstName).attr("data-last-name",lastName);
		$('td[data-user-id="'+userId+'"]').parent().find('.v-tbl-email').text(emailID);
		
		$('#overlay-toast').html(data);
		showToast();
	}, payload, true);
}

/**
 * Method to save the assignment of user with branch/region or company
 * @param formId
 */
function saveUserAssignment(formId) {
	var url = "./addindividual.do";
	showOverlay();
	callAjaxFormSubmit(url, saveUserAssignmentCallBack, formId);
}

/**
 * callback for saveUserAssignment
 * @param data
 */
function saveUserAssignmentCallBack(data) {
	hideOverlay();
	displayMessage(data);
}

// remove user
$(document).on('click', '.v-icn-rem-user', function() {
	if ($(this).hasClass('v-tbl-icn-disabled')) {
		return;
	}

	var userId = $(this).parent().find('.fetch-name').attr('data-user-id');
    var adminId = '${user.userId}';
    confirmDeleteUser(userId, adminId);
});

// resend verification mail
$(document).on('click', '.v-icn-fmail', function() {
	if ($(this).hasClass('v-tbl-icn-disabled')) {
		return;
	}

	var firstName = $(this).parent().find('.fetch-name').attr('data-first-name');
    var lastName = $(this).parent().find('.fetch-name').attr('data-last-name');
    var emailId = $(this).parent().find('.fetch-email').html();
    reinviteUser(firstName, lastName, emailId);
});

/**
 * Method to send invite link
 */
function reinviteUser(firstName, lastName, emailId) {
	var payload = {
		"firstName" : firstName,
		"lastName" : lastName,
		"emailId" : emailId
	};
	var url="./reinviteuser.do";
	showOverlay();
	callAjaxGetWithPayloadData(url, reinviteUserCallBack, payload, true);
}

function reinviteUserCallBack(data){
	var map =  $.parseJSON(data);
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
		
		var map =  $.parseJSON(data);
		if (map.status == "success") {
			showInfo(map.message);
			if (profileStatus == 1) {
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').removeClass('tbl-switch-off');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').addClass('tbl-switch-on');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').attr('title', 'Active');
				
				// de-activate user profile
				$('.tbl-switch-on').unbind('click');
		        $('.tbl-switch-on').click(function(){
		            var profileId = $(this).parent().data('profile-id');
		            updateUserProfile(profileId, 0);
		        });
			}
			else if (profileStatus == 0) {
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').removeClass('tbl-switch-on');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').addClass('tbl-switch-off');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').attr('title', 'InActive');

				// activate user profile
				$('.tbl-switch-off').unbind('click');
		        $('.tbl-switch-off').click(function(){
		            var profileId = $(this).parent().data('profile-id');
		            updateUserProfile(profileId, 1);
		        });
			}
		} else {
			showError(map.message);
		}
	}, payload, false);
}

function bindEditUserClick(){
	$('.edit-user').click(function(e){
		e.stopPropagation();
		if ($(this).hasClass('v-tbl-icn-disabled')) {
			return;
		}
		
		// de-activate user profile
        $('.tbl-switch-on').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 0);
        });

        // activate user profile
        $('.tbl-switch-off').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 1);
        });

		if ($(this).parent().hasClass('u-tbl-row-sel')) {
	        $(this).parent().removeClass('u-tbl-row-sel');
	        $(this).parent().next('.user-assignment-edit-row').slideUp(200);
	    } else {
	        // make an ajax call and fetch the details of the user
	        var userId = $(this).parent().find('.fetch-name').attr('data-user-id');
			$(".user-assignment-edit-div").html("");
			$(".user-row").removeClass('u-tbl-row-sel');
			$(".user-assignment-edit-row").slideUp();

			getUserAssignments(userId);

	        $(this).parent().next('.user-assignment-edit-row').slideDown(200);
	        $(this).parent().addClass('u-tbl-row-sel');
	        
			setTimeout(function() {
				$('#profile-tbl-wrapper-' + userId).perfectScrollbar();
			}, 1000);
	    }
	});
}

$(document).on('click', '#page-previous.paginate-button', function(){
	var newIndex = userStartIndex - userBatchSize;
	if (newIndex < $('#users-count').val()) {
		paintUserListInUserManagement(newIndex);
	}
});

$(document).on('click', '#page-next.paginate-button', function(){
	var newIndex = userStartIndex + userBatchSize;
	if (newIndex < $('#users-count').val()) {
		paintUserListInUserManagement(newIndex);
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
	}
	else {
		$('#paginate-buttons').hide();
	}
}

//Edit profile dropdown
//Profile View as
$('body').on('click','#profile-sel',function(e) {
	e.stopPropagation();
	$('#pe-dd-wrapper-profiles').slideToggle(200);
});
$('body').on('click','.pe-dd-item',function(e) {
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

//Settings page dropdown
//Settings View as
$('body').on('click','#setting-sel',function(e){
	e.stopPropagation();
	$('#se-dd-wrapper-profiles').slideToggle(200);
});

$('body').on('click','.se-dd-item',function(e) {
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

//Linked In Import
function authenticate(socialNetwork) {
	openAuthPage(socialNetwork);
	payload = {
		'socialNetwork' : socialNetwork
	};
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
	
	var payload = {
		"yelplink" : yelpLink
	};
	if (isValidUrl(yelpLink)) {
		callAjaxPostWithPayloadData("./updateyelplink.do", function(data) {
			$('#yelp-profile-url-display').html(yelpLink);
			$('#yelp-profile-url-display').removeClass('hide');
	        
			$('#yelp-profile-url').addClass('hide');
			
			$('#message-header').html(data);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload);
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
	callAjaxGET('/getsocialmediatokenonsettingspage.do', function(data){
		$('#social-media-token-cont').html(data);
	}, false);
}

function loadSocialMediaUrlInPopup() {
	callAjaxGET('/fetchsociallinksinpopup.do', function(data){
		$('#wc-step3-body-cont').html(data);
	}, false);
}

function showProfileLink(source, profileUrl){
	if(source=='facebook'){
		$('#fb-profile-url').html(profileUrl);
	}
	else if(source=='twitter'){
		$('#twitter-profile-url').html(profileUrl);
	}
	else if(source=='linkedin'){
		$('#linkedin-profile-url').html(profileUrl);
	}
	else if(source=='google'){
		$('#ggl-profile-url').html(profileUrl);
	}
}

function adjustTextContainerWidthOnResize() {
	var parentWidth = $('.ctnt-list-item').width();
	var imgWidth = $('.ctnt-list-item .ctnt-list-item-img').width();
	var textContainerWidth = parentWidth - imgWidth - 35;
	$('.ctnt-list-item .ctnt-list-item-txt-wrap').width(textContainerWidth);
}

// Function to validate the first name pattern
function validateProFirstNamePattern(elementId) {
	if ($('#' + elementId).val() != "") {
		if (firstNamePatternRegex.test($('#' + elementId).val().trim()) == true) {
			return true;
		} else {
			$('#overlay-toast').html('Please enter a first name pattern.');
			showToast();
			return false;
		}
	} else {
		$('#overlay-toast').html('Please enter a first name pattern.');
		showToast();
		return false;
	}
}

// Function to validate the last name pattern
function validateProLastNamePattern(elementId) {
	if ($('#' + elementId).val() != "") {
		if (lastNamePatternRegEx.test($('#' + elementId).val().trim()) == true) {
			return true;
		} else {
			$('#overlay-toast').html('Please enter a valid last name pattern.');
			showToast();
			return false;
		}
	} else {
		return false;
	}
}

// Function to validate registration form
function validateFindProForm() {
	$("#serverSideerror").hide();
	if (!validateProFirstNamePattern('find-pro-first-name') && !validateProLastNamePattern('find-pro-last-name')) {
		$('#find-pro-first-name').focus();
		return false;
	}
	return true;
}

function submitFindAProForm() {
	if (validateFindProForm()) {
		console.log("Submitting Find a Profile form");
		$('#find-pro-form').submit();
		showOverlay();
	} else {
		if (!($('#find-pro-first-name').val() == "" && $('#find-pro-last-name').val() == ""))
			showError("Please enter either a valid First Name or Last Name to search for");
	}
}

$('#find-pro-submit').click(function(e) {
	e.preventDefault();
	submitFindAProForm();
});

$('#find-pro-form input').keypress(function(e) {
	// detect enter
	if (e.which==13) {
		e.preventDefault();
		submitFindAProForm();
	}
});

/**
 * Method to fetch users list based on the criteria i.e if profile level is specified,
 *  bring all users of that level else search based on first/last name
 * @param newIndex
 */
function fetchUsers(newIndex) {
	var profileLevel = $("#fp-profile-level-fetch-info").data("profile-level");
	var iden = $("#fp-profile-level-fetch-info").data("iden");
	
	if (profileLevel != undefined && profileLevel != "") {
		fetchUsersByProfileLevel(iden, profileLevel, startIndex);
	} else {
		var formData = new FormData();
		formData.append("find-pro-first-name", $('#fp-first-name-pattern').val());
		formData.append("find-pro-last-name", $('#fp-last-name-pattern').val());
		formData.append("find-pro-start-index", newIndex);
		formData.append("find-pro-row-size", rowSize);
		
		if (!($('#find-pro-first-name').val() == "" && $('#find-pro-last-name').val() == ""))
			callAjaxPOSTWithTextData("./findaproscroll.do", infiniteScrollCallback, true, formData);
	}
}

function infiniteScrollCallback(response) {
	var reponseJson = $.parseJSON(response);
	
	// error message
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#ctnt-list-wrapper').append("No Profiles found");
	}
	else {
		var usersSize = reponseJson.userFound;
		if (usersSize > 0) {
			$('#srch-num').text(usersSize);
		}
		paintProList(reponseJson.users);
	}
}

function paintProList(usersList) {
	if (usersList != undefined) {
		var usersSize = usersList.length;
		if (usersSize > 0) {
			$('#fp-users-size').val(usersSize);
		}
		
		var usersHtml = "";
		if (usersSize > 0) {
			$.each(usersList, function(i, user) {
				var evenOddClass = (i % 2 == 0) ? '' : 'ctnt-list-item-even';
				usersHtml = usersHtml + '<div class="ctnt-list-item clearfix ' + evenOddClass + '" data-profilename="' + user.profileUrl + '">';
				
				if (user.profileImageUrl != undefined && user.profileImageUrl.trim() != "") {
					usersHtml = usersHtml + '<div class="float-left ctnt-list-item-img" style="background: url(' + user.profileImageUrl + ') no-repeat center; background-size: cover;"></div>';
				} else {
					usersHtml = usersHtml + '<div class="float-left ctnt-list-item-img pro-list-default-img"></div>';
				}
				usersHtml = usersHtml + '<div class="float-left ctnt-list-item-txt-wrap">'
					+ '<div class="ctnt-item-name user-display-name">' + user.displayName + '</div>';

				if (user.title != undefined) {
					usersHtml = usersHtml + '<div class="ctnt-item-desig">' + user.title + '</div>';
				}
				if (user.location != undefined) {
					usersHtml = usersHtml + '<div class="pro-addr-cont">' + user.location;
	            	if (user.industry != undefined) {
	            		usersHtml += " | " + user.industry;
	            	}
	            	usersHtml += "</div>";
	            }
				if (user.aboutMe != undefined) {
					usersHtml = usersHtml + '<div class="ctnt-item-comment">' + user.aboutMe + '</div>';
				}
				
				var reviewCount = 0;
				if (user.reviewCount) {
					reviewCount  = user.reviewCount;
				}
				
				var reviewScore = 0;
				if (user.reviewScore) {
					reviewScore  = user.reviewScore;
				}
				
				usersHtml = usersHtml + '</div>';
				usersHtml = usersHtml + '<div class="float-left ctnt-list-item-btn-wrap clearfix">'
					+ '<div class="float-left ctnt-review-score" data-score="' + reviewScore + '"></div>'
					+ '<div class="float-left ctnt-review-count" user="' + user.userId + '">' + reviewCount + ' Review(s)</div>'
				+ '</div>';
				usersHtml = usersHtml + '</div>';
			});
			$('#ctnt-list-wrapper').append(usersHtml);
			
			$('.ctnt-review-score').each(function(){
				changeRatingPattern($(this).attr("data-score"), $(this));
				$(this).append(" - ");
			});
			
			$(".ctnt-list-item").click(function(e){
				var agentProfileName = $(this).attr("data-profilename");
				var url = window.location.origin + "/pages" + agentProfileName;
				window.open(url);
			});
		}
	}
}


function fetchUsersByProfileLevel(iden, profileLevel, startIndex) {
	if (iden == undefined) {
		console.log("iden is undefined for fetchUsersByProfileLevel");
		return;
	}
	var url = window.location.origin + "/rest/profile/individuals/" + iden
			+ "?profileLevel=" + profileLevel + "&start=" + startIndex;
	callAjaxGET(url, fetchUsersByProfileLevelCallback, false);
}

function fetchUsersByProfileLevelCallback(data) {
	var response = $.parseJSON(data);
	if (response != undefined) {
		var usersList = $.parseJSON(response.entity);
		if(usersList.userFound > 0)
			$('#srch-num').text(usersList.userFound);
		paintProList(usersList.users);
	}
}

$(document).on('click', '.ctnt-review-btn', function(){
	initSurveyReview($(this).attr('user'));
});

//Functions for survey question page
$(document).on('click', '.sq-np-item-next', function() {
});

/*
 * Function to initiate survey. It hits controller to get list of all the
 * questions which are shown one after one to the customer.
 */
function initSurvey(firstName, lastName, email, agentId, agentName, grecaptcharesponse) {
	this.agentId = agentId;
	this.agentName = agentName;
	customerEmail = email;
	/*var payload = {
		"agentId" : agentId,
		"firstName" : firstName,
		"lastName" : lastName,
		"customerEmail" : email,
		"g-recaptcha-response" : grecaptcharesponse,
		"relationship" : relationship
	};*/
	
	
	//$('input[relationship]').val(relationship);
	$('input[g-recaptcha-response]').val(grecaptcharesponse);
	
	if($('#cust-agent-verify').hasClass('bd-check-img-checked')){
		$('#overlay-toast').html("Verify that you have done business with the agent");
		showToast();
		return false;
	}
	
	$('#survey-request-form').submit();
	
	/*$.ajax({
		url : window.location.origin + surveyUrl + "triggersurvey",
		type : "GET",
		dataType : "TEXT",
		data : payload,
		success : function(data) {			
			$('#overlay-toast').html(data);
			$("#recaptcha_reload").click();
			showToast();
			clearForm();
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});*/
}

function initSurveyWithUrl(q) {
	var success = false;
	var payload = {
		"q" : q
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "triggersurveywithurl",
		type : "GET",
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
				paintSurveyPage(data);
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			showPageNotFoundError();
		}
	});
}

function showPageNotFoundError(){
	window.location = window.location.origin + surveyUrl + "notfound";
}

function loadAgentPic(agentId){
	var imageUrl;
	var success = false;
	var payload = {
		"agentId" : agentId
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "displaypiclocationofagent",
		type : "GET",
		dataType : "text",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				imageUrl = data.responseText;
				if(imageUrl!='' && imageUrl!=null) {
					$("#agnt-img").html("<img class='hr-ind-img' src='"+imageUrl+"'/>");
				}
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
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
	autoPost = jsonData.responseJSON.autopostEnabled;
	autoPostScore = jsonData.responseJSON.autopostScore;
	yelpEnabled = Boolean(jsonData.responseJSON.yelpEnabled);
	googleEnabled = Boolean(jsonData.responseJSON.googleEnabled);
	zillowEnabled = Boolean(jsonData.responseJSON.zillowEnabled);
	lendingtreeEnabled = Boolean(jsonData.responseJSON.lendingtreeEnabled);
	realtorEnabled = Boolean(jsonData.responseJSON.realtorEnabled);
	agentProfileLink = jsonData.responseJSON.agentProfileLink;
	agentFullProfileLink = jsonData.responseJSON.agentFullProfileLink;
	
	
	//If social token availiable populate the links
	if (googleEnabled) {
		var googleElement = document.getElementById('ggl-btn');
		shareOnGooglePlus(agentId, window.location.origin + "/rest/survey/", googleElement);
	} else {
		$('#ggl-btn').remove();
	}
	
	if (yelpEnabled) {
		$('#ylp-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.yelpLink));
	} else {
		$('#ylp-btn').remove();
	}
	
	if (zillowEnabled) {
		$('#zillow-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.zillowLink));
	} else {
		$('#zillow-btn').remove();
	}
	
	if (lendingtreeEnabled) {
		$('#lt-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.lendingtreeLink));
	} else {
		$('#lt-btn').remove();
	}
	
	if (realtorEnabled) {
		$('#realtor-btn').attr("href", returnValidWebAddress(jsonData.responseJSON.realtorLink));
	} else {
		$('#realtor-btn').remove();
	}
	
	companyLogo = jsonData.responseJSON.companyLogo;
	
	if (stage != undefined)
		qno = stage;
	paintSurveyPageFromJson();
}

/*
 * It gets the questions from array of questions and finds out the current
 * question based upon current index for question number. It also checks over
 * various conditions of the question and renders the page accordingly.
 */
function paintSurveyPageFromJson() {
	$("div[data-ques-type]").hide();
	if (qno == -1 && editable == false) {
		$("div[data-ques-type]").hide();
		$("div[data-ques-type='error']").show();
		$('#content-head').html('Survey');
		$('#content').html(
				"OOPS! It looks like you have already taken a survey for " + agentName + "."
				+"<br/><br/>"
				+"Are you trying to amend a prior response? If so click the link below and we will email you the access required<br/><br/>")
				.append("<div>Link to resend original Survey Responses so they can be amended</div>");
		
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
	var questionType = questionDetails.questionType;
	var isRatingQuestion = questionDetails.isRatingQuestion;
	if (isRatingQuestion == 1) {
		$("#skip-ques").hide();
		$("#next-star").addClass("btn-com-disabled");
		$("#next-smile").addClass("btn-com-disabled");
		$("#next-scale").addClass("btn-com-disabled");
	}
	if (questionType == "sb-range-star") {
		$("div[data-ques-type='stars']").show();
		$("#ques-text").html(question);
		$("#sq-stars").show();
		if(questionDetails.customerResponse!=undefined && !isNaN(parseInt(questionDetails.customerResponse))){
			increaseOpacityOfStars(parseInt(questionDetails.customerResponse));
			$("#next-star").removeClass("btn-com-disabled");
		}
	} else if (questionType == "sb-range-smiles") {
		$("div[data-ques-type='smiley']").show();
		$("#ques-text-smiley").html(question);
		$("#sq-smiles").show();
		if(questionDetails.customerResponse!=undefined && !isNaN(parseInt(questionDetails.customerResponse))){
			increaseOpacityOfStars(parseInt(questionDetails.customerResponse));
			$("#next-smile").removeClass("btn-com-disabled");
		}
	} else if (questionType == "sb-range-scale") {
		$("div[data-ques-type='scale']").show();
		$("#ques-text-scale").html(question);
		$("#sq-stars").show();
	} else if (questionType == "sb-sel-mcq") {
		$("div[data-ques-type='mcq']").show();
		$("#mcq-ques-text").html(question);
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
		$("#ques-text-textarea").html(question);
		$("#text-area").show();
		
		var val = questionDetails.customerResponse;
		if (val != undefined) {
			$("#text-area").val(val);
		}
		else {
			$("#text-area").val('');
		}
		
		$('#text-box-disclaimer').show();
		$("#smiles-final").hide();
		if(questionDetails.customerResponse!=undefined)
			$("#text-area").html(questionDetails.customerResponse);
	} else if (questionType == "sb-master") {
		$("div[data-ques-type='smiley-text-final']").show();
		$("#text-area").hide();
		$('#text-box-disclaimer').hide();
		$("#smiles-final").show();
		$("#ques-text-textarea").html(question);
	}
	togglePrevAndNext();
	if (qno == questions.length - 1) {
		$("#next-mcq").addClass("btn-com-disabled");
		$("#next-smile").addClass("btn-com-disabled");
		$("#next-star").addClass("btn-com-disabled");
		$("#next-textarea-smiley").addClass("btn-com-disabled");
		$("#skip-ques-mcq").hide();
	}
	$(".sq-main-txt").html("Survey for " + agentName);
	
	if (companyLogo != undefined && companyLogo != "") {
		var companylogoHtml = '<div class="float-left user-info-seperator"></div>';
		companylogoHtml += '<div class="float-left user-info-logo" style="background: url('
			+ companyLogo + ') no-repeat center; background-size: 100% auto;"></div>';
		$('#header-user-info').html(companylogoHtml);
	}
}

function togglePrevAndNext(){
	if (qno == 0) {
		$("#prev-star").addClass("btn-com-disabled");
		$("#prev-smile").addClass("btn-com-disabled");
		$("#prev-scale").addClass("btn-com-disabled");
		$("#prev-mcq").addClass("btn-com-disabled");
		$("#prev-textarea-smiley").addClass("btn-com-disabled");
	} else {
		$("#prev-star").removeClass("btn-com-disabled");
		$("#prev-smile").removeClass("btn-com-disabled");
		$("#prev-scale").removeClass("btn-com-disabled");
		$("#prev-mcq").removeClass("btn-com-disabled");
		$("#prev-textarea-smiley").removeClass("btn-com-disabled");
	}
}

function retakeSurveyRequest(){
	var payload = {
			"customerEmail" : customerEmail,
			"agentId" : agentId,
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName
	};
	callAjaxGetWithPayloadData(window.location.origin + surveyUrl + 'restartsurvey', '', payload, true);
	$('#overlay-toast').html('Mail sent to your registered email id for retaking the survey for '+agentName);
	showToast();
}

/*
 * This method is used to store the answer provided by the customer for a
 * specific question.
 */
function storeCustomerAnswer(customerResponse) {
	var success = false;
	var payload = {
		"answer" : customerResponse,
		"question" : questionDetails.question,
		"questionType" : questionDetails.questionType,
		"stage" : qno + 1,
		"agentId" : agentId,
		"customerEmail" : customerEmail
	};
	questionDetails.customerResponse = customerResponse;
	$.ajax({
		url : window.location.origin + surveyUrl + "data/storeAnswer",
		type : "GET",
		data : payload,
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				if (qno == (questions.length - 1)) {
					var parsed = data.responseJSON;
					for ( var x in parsed) {
						swearWords.push(parsed[x]);
					}
				}
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : ");
		}
	});
}

function updateCustomerResponse(feedback, agreedToShare) {
	var success = false;
	isAbusive = false;
	var feedbackArr = feedback.split(" ");
	for (var i = 0; i < feedbackArr.length; i++) {
		if ($.inArray(feedbackArr[i], swearWords) != -1) {
			isAbusive = true;
		}
	}
	var payload = {
		"mood" : mood,
		"feedback" : feedback,
		"agentId" : agentId,
		"customerEmail" : customerEmail,
		"firstName" : firstName,
		"lastName" : lastName,
		"isAbusive" : isAbusive,
		"agreedToShare" : agreedToShare
	};
	questionDetails.customerResponse = customerResponse;
	$.ajax({
		url : window.location.origin + surveyUrl + "data/storeFeedback",
		type : "GET",
		data : payload,
		dataType : "TEXT",
		success : function(data) {
			if (data != undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				console.log(data);
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : "+e);
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
		for(var i=0;i<questions.length;i++){
			var currQuestion = questions[i];
			if((currQuestion.questionType=='sb-range-smiles')||(currQuestion.questionType=='sb-range-scale')
					||(currQuestion.questionType=='sb-range-star')){
				if(!isNaN(parseInt(currQuestion.customerResponse))){
					counter++;
					currResponse += parseInt(currQuestion.customerResponse);
				}
			}
		}
		rating = currResponse/(counter);
		rating = parseFloat(rating).toFixed(3);
		if((rating >= autoPostScore) && (Boolean(autoPost) == true))
			$("#pst-srvy-div").show();
		break;
	case "OK":
		question = neutralText;
		$("#ques-text-textarea").html(question);
		break;
	case "Unpleasant":
		question = sadText;
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
		divToPopulate = "<div data-answer='" + answer
				+ "' class='sq-mcq-item clearfix'>"
				+ "<div class='sq-mcq-chk-wrapper float-left'>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-on'>"
				+ "</div>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-off hide'>"
				+ "</div>" + "</div>"
				+ "<div class='sq-mcq-ans-wrapper float-left'>" + answer
				+ "</div></div>";
	} else {
		divToPopulate = "<div data-answer='" + answer
				+ "' class='sq-mcq-item clearfix'>"
				+ "<div class='sq-mcq-chk-wrapper float-left'>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-on hide'>"
				+ "</div>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-off'>"
				+ "</div>" + "</div>"
				+ "<div class='sq-mcq-ans-wrapper float-left'>" + answer
				+ "</div></div>";
	}
	return divToPopulate;
}

function paintListOptions(agentName) {
	var divToPopulate = "<option value='select'>--Select an Option--"
			+ "<option value='transacted'>Transacted with " + agentName
			+ "<option value='enquired'>Enquired with " + agentName;
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

function showMasterQuestionPage(){
	if (isSmileTypeQuestion) {
		showFeedbackPage(mood);
	} else {
		//if ($('#pst-srvy-div').is(':visible'))
		//	autoPost = $('#post-survey').is(":checked");
		var feedback = $("#text-area").val();
		if (feedback == null || feedback == "") {
			$('#overlay-toast').html('Please enter feedback to continue');
			showToast();
			return;
		}
		if ($('#shr-post-chk-box').hasClass('bd-check-img') && (rating >= autoPostScore) && (Boolean(autoPost) == true)) {
			postToSocialMedia(feedback);
			/*$('#social-post-lnk').show();
			if((mood == 'Great') && (yelpEnabled || googleEnabled) && !(yelpEnabled && googleEnabled)){
				$('.sq-btn-social-wrapper').css({
					"float" : "none",
					"width" : "100%"
				});
				$('.sq-btn-post-social').css({
					"float" : "none"
				});
			}
			if (yelpEnabled && (mood == 'Great')){
				$('#ylp-btn').show();
				//var yelpElement = document.getElementById('ylp-btn');
				//shareOnYelp(agentId, window.location.origin+"/rest/survey/", yelpElement);
			}
			else {
				$('#ylp-btn').parent().remove();
			}
			if (googleEnabled && (mood == 'Great')){
				var googleElement = document.getElementById('ggl-btn');
				shareOnGooglePlus(agentId, window.location.origin+"/rest/survey/", googleElement);
			}
			else {
				$('#ggl-btn').parent().remove();
			}*/
			
			if(mood == 'Great') {
				$('#social-post-links').show();
			}
		}
		
		updateCustomerResponse(feedback, $('#shr-pst-cb').val());
		$("div[data-ques-type]").hide();
		$("div[data-ques-type='error']").show();
		$('#profile-link').html('View ' + agentName + '\'s profile at <a href="' + agentFullProfileLink + '" target="_blank">' + agentFullProfileLink + '</a>');
		$('#icn-fb-shr').attr("href","https://www.facebook.com/sharer/sharer.php?u="+agentFullProfileLink);
		$('#icn-google-shr').attr("href","https://plus.google.com/share?url="+agentFullProfileLink);
		$('#icn-linkedin-shr').attr("href","https://www.linkedin.com/shareArticle?mini=true&url="+agentFullProfileLink+"&title=&summary="+rating+"-star response from " +firstName+ " " +lastName+ " for "+agentName+ " at SocialSurvey - "+ feedback + "&source=");
		$('#icn-twitter-shr').attr("href","https://twitter.com/home?status="+agentFullProfileLink);
		$('#content-head').html('Survey Completed');
			if (mood == 'Great')
				$('#content').html(happyTextComplete);
			else if(mood == 'OK')
				$('#content').html(neutralTextComplete);
			else
				$('#content').html(sadTextComplete);
	//	$('#content').html("Congratulations! You have completed survey for " + agentName+ ".\nThanks for your participation.");
	}
	return;
}

function postToSocialMedia(feedback){
	var success = false;
	var payload = {
		"agentId" : agentId,
		"firstName" : firstName,
		"lastName" : lastName,
		"agentName" : agentName,
		"rating" : rating,
		"customerEmail" : customerEmail,
		"feedback" : feedback,
		"agentProfileLink" : agentProfileLink
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "posttosocialnetwork",
		type : "GET",
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
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function updateSharedOn(socialSite, agentId, customerEmail){
	var success = false;
	var payload = {
		"agentId" : agentId,
		"customerEmail" : customerEmail,
		"socialSite" : socialSite
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "updatesharedon",
		type : "GET",
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
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function reduceOpacityOfStars(){
	$('#sq-stars').find('.sq-star').each(function(index) {
		if (index < 5) {
			$(this).removeClass('sq-full-star-click');
			$(this).addClass('opacity-red');
		}
	});
}

function reduceOpacityOfSmiles(){
	$('#sq-smiles').find('.sq-smile').each(
		function(index) {
			if (index < 5) {
				$(this).removeClass('sq-full-smile-click');
				$(this).addClass('opacity-red');
			}
	});
}

function increaseOpacityOfStars(value){
	$('#sq-stars').find('.sq-star').each(function(index) {
		if (index < value) {
			$(this).addClass('sq-full-star-click');
			$(this).removeClass('opacity-red');
		}
	});
}

function increaseOpacityOfSmiles(value){
	$('#sq-smiles').find('.sq-smile').each(
		function(index) {
			if (index < value) {
				$(this).addClass('sq-full-smile-click');
				$(this).removeClass('opacity-red');
			}
	});
}

function clearForm(){
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
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < starVal) {
			$(this).removeClass('opacity-red');
			$(this).addClass('sq-full-star-click');
		}
		else{
			if(!$(this).hasClass('opacity-red'))
				$(this).addClass('opacity-red');
		}
	});
	if (qno != questions.length - 1) {
		$("#next-star").removeClass("btn-com-disabled");
	}
	storeCustomerAnswer(starVal);
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

$('.sq-np-item-next')
		.click(
				function() {
					if (questionDetails.questionType == "sb-master") {
						showMasterQuestionPage();
					}

					if (questionDetails.questionType == "sb-sel-mcq"
							&& customerResponse != undefined) {
						storeCustomerAnswer(customerResponse);
					} else if (questionDetails.questionType == "sb-sel-desc") {
						customerResponse = $("#text-area").val();
						if (customerResponse == undefined) {
							customerResponse = "";
						}
						storeCustomerAnswer(customerResponse);
					} else if (questionDetails.questionType == "sb-range-star") {
						reduceOpacityOfStars();
						if ($('#next-star').hasClass("btn-com-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-range-smiles") {
						reduceOpacityOfSmiles();
						if ($('#next-smile').hasClass("btn-com-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-range-scale") {
						if ($('#next-scale').hasClass("btn-com-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-master") {
						return;
					}
					$(".sq-star").removeClass('sq-full-star');
					$(".sq-smile").removeClass('sq-full-smile');
					qno++;
					paintSurveyPageFromJson();

					if (questionDetails.questionType == "sb-range-star") {
						var starVal = parseInt(questionDetails.customerResponse);
						if (!isNaN(starVal)) {
							$("#next-star").removeClass("btn-com-disabled");
							$('#sq-stars').find('.sq-star').each(
									function(index) {
										if (index < starVal) {
											$(this).addClass('sq-full-star-click');
											$(this).removeClass('opacity-red');
										}
									});
						}
					}
					if (questionDetails.questionType == "sb-range-smiles") {
						var smileVal = parseInt(questionDetails.customerResponse);
						if (!isNaN(smileVal)) {
							$("#next-smile").removeClass("btn-com-disabled");
							$('#sq-smiles').find('.sq-smile').each(
									function(index) {
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
						if(questionDetails.customerResponse==undefined || questionDetails.customerResponse=="")
							customerResponse = "";
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
	$("#next-star").removeClass("btn-com-disabled");
	$("#next-smile").removeClass("btn-com-disabled");
	$("#next-scale").removeClass("btn-com-disabled");
	$("#next-textarea-smiley").removeClass("btn-com-disabled");
});

/* Click event on grey smile. */
$('.sq-smile').click(function() {
	$(this).parent().find('.sq-smile').removeClass('sq-full-smile');
	$(this).parent().find('.sq-smile').removeClass('sq-full-smile-click');
	var smileVal = $(this).attr('smile-no');
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			$(this).removeClass('opacity-red');
			$(this).addClass('sq-full-smile-click');
		}
		else{
			if(!$(this).hasClass('opacity-red'))
				$(this).addClass('opacity-red');
		}
	});
	if (qno != questions.length - 1) {
		$("#next-smile").removeClass("btn-com-disabled");
	}
	storeCustomerAnswer(smileVal);
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

$('#start-btn').click(function() {
	firstName = $('#firstName').val().trim();
	lastName = $('#lastName').val().trim();
	var email = $('#email').val().trim();
	var grecaptcharesponse = $('#g-recaptcha-response').val();
	// var recaptcha_challenge_field = $('#recaptcha_challenge_field').val();
	
	if (!validateUserFirstName('firstName')) {
		$('#overlay-toast').html('Please enter valid First Name!');
		showToast();
		return;
	}
	if (!validateUserEmailId('email')) {
		$('#overlay-toast').html('Please enter valid Email Id!');
		showToast();
		return;
	}
	
	var agentEmail = $('#prof-container').attr("data-agent-email");
	if(agentEmail.toUpperCase() == email.toUpperCase()){
		$('#overlay-toast').html('Agents can not take survey for themselves!');
		showToast();
		return;
	}
	
	/*if (!validateUserEmailId('email')) {
		$('#overlay-toast').html('Please enter valid Email Id!');
		showToast();
		return;
	}*/
	
	
	var agentId = $('#prof-container').attr("data-agentId");
	var agentName = $('#prof-container').attr("data-agentName");
	//var e = document.getElementById("cust-agnt-rel");
	//var relationship = e.options[e.selectedIndex].value;
	initSurvey(firstName, lastName, email, agentId, agentName,
			grecaptcharesponse);
	
	// Google analytics for reviews
	ga('send', {
		'hitType': 'event',
		'eventCategory': 'review',
		'eventAction': 'click',
		'eventLabel': 'Reviews',
		'eventValue': agentId
	});
});

$('input[type="range"]').rangeslider({
	polyfill : false,

	// Default CSS classes
	rangeClass : 'rangeslider',
	fillClass : 'rangeslider__fill',
	handleClass : 'rangeslider__handle',

	onSlide : function(position, value) {
		// $('div[quest-no="' + survQuesNo + '"]').find(
		// '.sq-slider-val').html(value);
		$('#range-slider-value').html(value);
	},
	// Callback function
	onSlideEnd : function(position, value) {
		$('#range-slider-value').html(value);
		storeCustomerAnswer(value);
	},
});

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

$('#ylp-btn').click(function(e) {
	//e.stopImmediatePropagation();
	updateSharedOn("yelp", agentId, customerEmail);
});

$('#ggl-btn').click(function(e) {
	//e.stopImmediatePropagation();
	updateSharedOn("google", agentId, customerEmail);
});

$('#zillow-btn').click(function(e) {
	//e.stopImmediatePropagation();
	updateSharedOn("zillow", agentId, customerEmail);
});

$('#lt-btn').click(function(e) {
	//e.stopImmediatePropagation();
	updateSharedOn("lendingtree", agentId, customerEmail);
});

$('#realtor-btn').click(function(e) {
	//e.stopImmediatePropagation();
	updateSharedOn("realtor", agentId, customerEmail);
});

$('#shr-post-chk-box').click(function(){
	if($('#shr-post-chk-box').hasClass('bd-check-img-checked')){
		$('#shr-post-chk-box').removeClass('bd-check-img-checked');
		$('#shr-pst-cb').val('true');
		autoPost = true;
	}
	else{
		$('#shr-post-chk-box').addClass('bd-check-img-checked');
		$('#shr-pst-cb').val('false');
		autoPost = false;
	}
});

//Edit profile functions
$(document).ajaxStop(function() {
	adjustImage();
});

// Toggle text editor
$(document).on('focus', '.prof-edditable', function() {
	var lockId = $(this).attr("id") + "-lock";
	if($('#'+lockId).length == 0){
		$(this).addClass('prof-name-edit');
	}
	if ($('#' + lockId).attr('data-control') == 'user'
			|| ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]')) ) {
		$(this).addClass('prof-name-edit');
		$('#prof-all-lock').val('modified');
	}
});

$(document).on('blur', '.prof-edditable', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user'
			|| ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]'))) {
		$(this).removeClass('prof-name-edit');
	}
});

$(document).on('focus', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user'
			|| ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]'))) {
		$(this).addClass('prof-name-edit');
		$('#prof-all-lock').val('modified');
	}
});

$(document).on('blur', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user'
			|| ($('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]'))) {
		$(this).removeClass('prof-name-edit');
	}
});

/*$(document).on('click', '.fb-shr', function() {
	var firstName = $(this).parent().parent().parent().attr('data-firstname');
	var lastName = $(this).parent().parent().parent().attr('data-lastname');
	var agentName = $(this).parent().parent().parent().attr('data-agentname');
	var review = $(this).parent().parent().parent().attr('data-review');
	var score = $(this).parent().parent().parent().attr('data-score');
	shareOnFacebook(firstName, lastName, agentName, review, score);
});

$(document).on('click', '.twt-shr', function() {
	var firstName = $(this).parent().parent().parent().attr('data-firstname');
	var lastName = $(this).parent().parent().parent().attr('data-lastname');
	var agentName = $(this).parent().parent().parent().attr('data-agentname');
	var review = $(this).parent().parent().parent().attr('data-review');
	var score = $(this).parent().parent().parent().attr('data-score');
	shareOnTwitter(firstName, lastName, agentName, review, score);
});

$(document).on('click', '.lnkdn-shr', function() {
	var firstName = $(this).parent().parent().parent().attr('data-firstname');
	var lastName = $(this).parent().parent().parent().attr('data-lastname');
	var agentName = $(this).parent().parent().parent().attr('data-agentname');
	var review = $(this).parent().parent().parent().attr('data-review');
	var score = $(this).parent().parent().parent().attr('data-score');
	shareOnLinkedin(firstName, lastName, agentName, review, score);
});*/

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
				}
				else if (state == true) {
					$('#' + id).addClass('lp-edit-locks-locked');
					$('#' + id).addClass('prof-img-lock-locked');
					$('#' + id).attr('data-state', 'locked');
				}
			}
			
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload);
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
			$('#overlay-toast')
					.html("Please add a few words about you");
			showToast();
			return;
		}
		delay(function() {
			var payload = {
				"aboutMe" : aboutMe
			};
			callAjaxPostWithPayloadData("./addorupdateaboutme.do",
					callBackOnEditAdboutMeDetails, payload);
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

// Phone numbers in contact details
$(document).on(
		'blur',
		'#contant-info-container input[data-phone-number]',
		function() {
			if ($('#prof-all-lock').val() != 'modified' || !$(this).val()
					|| $(this).is('[readonly]')) {
				return;
			}
			if (!phoneRegex.test(this.value)) {
				$('#overlay-toast').html("Please add a valid phone number");
				showToast();
				return;
			}

			delay(function() {
				var phoneNumbers = [];
				$('#contant-info-container input[data-phone-number]').each(
						function() {
							if (this.value != "" && phoneRegex.test(this.value)
									&& !$(this).is('[readonly]')) {
								var phoneNumber = {};
								phoneNumber.key = $(this).attr(
										"data-phone-number");
								phoneNumber.value = this.value;
								phoneNumbers.push(phoneNumber);
							}
						});
				phoneNumbers = JSON.stringify(phoneNumbers);
				var payload = {
					"phoneNumbers" : phoneNumbers
				};
				callAjaxPostWithPayloadData("./updatephonenumbers.do",
						callBackOnUpdatePhoneNumbers, payload);
			}, 0);
		});

function callBackOnUpdatePhoneNumbers(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Function to update web addresses in contact details
$(document).on(
		'blur',
		'#contant-info-container input[data-web-address]',
		function() {
			if ($('#prof-all-lock').val() != 'modified' || !$(this).val()
					|| $(this).is('[readonly]')) {
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
				$('#contant-info-container input[data-web-address]').each(
						function() {
							var link = $.trim(this.value);
							if (link != "") {
								if (isValidUrl(link)
										&& !$(this).is('[readonly]')) {
									var webAddress = {};
									webAddress.key = $(this).attr(
											"data-web-address");
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
					alert("Invalid web address");
					return false;
				}
				webAddresses = JSON.stringify(webAddresses);
				var payload = {
					"webAddresses" : webAddresses
				};
				callAjaxPostWithPayloadData("./updatewebaddresses.do",
						callBackOnUpdateWebAddresses, payload);
			}, 0);
		});

function callBackOnUpdateWebAddresses(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Update Address detail
function callBackShowAddressDetails(data) {
	$('#prof-address-container').html(data);
	adjustImage();
}

function showEditAddressPopup() {
	callAjaxGET("./fetchaddressdetailsedit.do", callBackEditAddressDetails);
}

function callBackEditAddressDetails(data) {
	var header = "Edit Address Detail";
	createEditAddressPopup(header, data);

	$('#overlay-continue').click(function() {
		var profName = $('#prof-name').val();
		var profAddress1 = $('#prof-address1').val();
		//var profAddress2 = $('#prof-address2').val();
		var country = $('#prof-country').val();
		var zipCode = $('#prof-zipcode').val();
		if (!profName || !profAddress1 || !country || !zipCode) {
			return;
		}

		delay(function() {
			/*var payload = {
				"profName" : profName,
				"address1" : profAddress1,
				"address2" : profAddress2,
				"country" : country,
				"zipCode" : zipCode
			};*/
			payload = $('#prof-edit-address-form').serialize();
			callAjaxPostWithPayloadData("./updateprofileaddress.do", callBackUpdateAddressDetails, payload);
		}, 0);

		$('#overlay-continue').unbind('click');
	});

	$('.overlay-disable-wrapper').addClass('pu_arrow_rt');
	disableBodyScroll();
	//$('body').css('overflow', 'hidden');
	$('body').scrollTop('0');
}

function callBackUpdateAddressDetails(data) {
	$('body').css('overflow','auto');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchbasicdetails.do", callBackShowBasicDetails);
	callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);
	
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	overlayRevert();
}

$('#overlay-cancel').click(function() {
	$('#overlay-continue').unbind('click');
	$('body').css('overflow','auto');
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

	//$('body').css('overflow', 'auto');
	enableBodyScroll();
	$('.overlay-disable-wrapper').removeClass('pu_arrow_rt');
}

// Update Basic detail
function callBackShowBasicDetails(response) {
	$('#prof-basic-container').html(response);
	adjustImage();
	//fetchAvgRating(attrName, attrVal);
	//fetchReviewCount(attrName, attrVal, minScore);
}

$(document).on('blur', '#prof-basic-container input', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#'+lockId).length > 0) {
		//if ($('#prof-all-lock').val() != 'modified' || !$(this).val()) {
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
		if($('#prof-vertical').val()){
			payload["profVertical"] = $('#prof-vertical').val().trim();
		}
		if($('#prof-location').val()){
			payload["profLocation"] = $('#prof-location').val().trim();
		}
		
		callAjaxPostWithPayloadData("./updatebasicprofile.do", callBackUpdateBasicDetails, payload);
	}, 0);
});

function callBackUpdateBasicDetails(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	//callAjaxGET("./fetchbasicdetails.do", callBackShowBasicDetails);
	//callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails);

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
	if ($('#header-user-info').find('.user-info-logo').length <= 0) {
		var userInfoDivider = $('<div>').attr({
			"class" : "float-left user-info-seperator"
		});
		var userInfoLogo = $('<div>').attr({
			"class" : "float-left user-info-logo"
		}).css({
			"background" : logoImageUrl + " no-repeat center",
			"background-size" : "contain"
		});
		$('#header-user-info').append(userInfoDivider).append(userInfoLogo);
	} else {
		$('.user-info-logo').css("background-image", logoImageUrl);
	}
	adjustImage();
	hideOverlay();
}

$(document).on('change', '#prof-logo', function() {
	showOverlay();

	var formData = new FormData();
	formData.append("logo", $(this).prop("files")[0]);
	formData.append("logoFileName", $(this).prop("files")[0].name);

	delay(function() {
		callAjaxPOSTWithTextData("./updatelogo.do", function(data) {
			$('#prof-message-header').html(data);
			callAjaxGET("./fetchprofilelogo.do", callBackShowProfileLogo);

			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, false, formData);
	}, 1000);
});

// Function to crop and upload profile image
function callBackOnProfileImageUpload(data) {

	if ($('#overlay-linkedin-import').is(":visible")) {
		$('#message-header').html(data);
		callAjaxGET("./fetchuploadedprofileimage.do",
				function(profileImageUrl) {
					if (profilemasterid == 4) {
						$("#wc-photo-upload").removeClass('dsh-pers-default-img');
					} else if (profilemasterid == 3) {
						$("#wc-photo-upload").removeClass('dsh-office-default-img');
					} else if (profilemasterid == 2) {
						$("#wc-photo-upload").removeClass('dsh-region-default-img');
					} else if (profilemasterid == 1) {
						$("#wc-photo-upload").removeClass('dsh-comp-default-img');
					}

					$('#wc-photo-upload').css("background",
							"url(" + profileImageUrl + ") no-repeat center");
					$('#wc-photo-upload').css("background-size", "contain");
					hideOverlay();
				});

		$('#overlay-toast').html($('#display-msg-div').text().trim());
		showToast();
	} else {
		$('#prof-message-header').html(data);

		callAjaxGET("./fetchprofileimage.do",
				function(data) {
					$('#prof-img-container').html(data);
					var profileImageUrl = $('#prof-image-edit').css(
							"background-image");
					if (profileImageUrl == undefined
							|| profileImageUrl == "none") {
						return;
					}
					adjustImage();
					hideOverlay();
				});

		$('#overlay-toast').html($('#display-msg-div').text().trim());
		showToast();
		loadDisplayPicture();
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
	callAjaxPostWithPayloadData("./updateassociations.do",
			callBackUpdateAssociations, payload);
}

function callBackUpdateAssociations(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#association-container').find('input').length) {
		$('#association-container').append(
				'<span>No association added yet</span>');
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
	callAjaxPostWithPayloadData("./updateachievements.do",
			callBackUpdateAchievements, payload);
}

function callBackUpdateAchievements(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#achievement-container').find('input').length) {
		$('#achievement-container').append(
				'<span>No achievement added yet</span>');
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
	$('#authorised-in-container').children().last().append(
			newAuthorizationButton);
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
	callAjaxPostWithPayloadData("./updatelicenses.do",
			callBackUpdateLicenseAuthorizations, payload);
}

function callBackUpdateLicenseAuthorizations(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#authorised-in-container').find('input').length) {
		$('#authorised-in-container').append(
				'<span>No license added yet</span>');
	}
}

//TODO:Postions

//Function to update Expertise
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
	$('#expertise-container').children().last().append(
			newExpertiseButton);
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
	callAjaxPostWithPayloadData("./updateexpertise.do",
			callBackUpdateExpertise, payload);
}

function callBackUpdateExpertise(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#expertise-container').find('input').length) {
		$('#expertise-container').append(
				'<span>No license added yet</span>');
	}
}

//Function to update Hobbies
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
	$('#hobbies-container').children().last().append(
			newExpertiseButton);
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
	callAjaxPostWithPayloadData("./updatehobbies.do",
			callBackUpdateHobbies, payload);
}

function callBackUpdateHobbies(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	if (!$('#expertise-container').find('input').length) {
		$('#expertise-container').append(
				'<span>No license added yet</span>');
	}
}


// Update Social links - facebook
/*$('body').on('click', '#prof-edit-social-link .icn-fb', function() {
	$('#social-token-text').show();
	var link = $(this).attr('data-link');
	$('#social-token-text').attr({
		"placeholder" : "Add facebook link",
		"onblur" : "updateFacebookLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

function updateFacebookLink(link) {
	var payload = {
		"fblink" : link	
	};
	if (isValidUrl(link)) {
        callAjaxPostWithPayloadData("./updatefacebooklink.do", callBackUpdateSocialLink, payload);
        $('#icn-fb').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}*/

// Update Social links - twitter
/*$('body').on('click', '#prof-edit-social-link .icn-twit', function() {
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Twitter link",
		"onblur" : "updateTwitterLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

function updateTwitterLink(link) {
	var payload = {
		"twitterlink" : link	
	};
	if (isValidUrl(link)) {
        callAjaxPostWithPayloadData("./updatetwitterlink.do", callBackUpdateSocialLink, payload);
        $('#icn-twit').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}*/

// Update Social links - linkedin
/*$('body').on('click', '#prof-edit-social-link .icn-lin', function() {
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add LinkedIn link",
		"onblur" : "updateLinkedInLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

function updateLinkedInLink(link) {
	var payload = {
		"linkedinlink" : link	
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updatelinkedinlink.do", callBackUpdateSocialLink, payload);
        $('#icn-lin').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}*/

// Update Social links - google plus
/*$('body').on('click', '#prof-edit-social-link .icn-gplus', function() {
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Google link",
		"onblur" : "updateGoogleLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

function updateGoogleLink(link) {
	var payload = {
		"gpluslink" : link	
	};
	if (isValidUrl(link)) {
        callAjaxPostWithPayloadData("./updategooglelink.do", callBackUpdateSocialLink, payload);
        $('#icn-gplus').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}*/

// Update Social links - yelp
$('body').on('click', '#prof-edit-social-link .icn-yelp', function() {
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Yelp link",
		"onblur" : "updateYelpLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

function updateYelpLink(link) {
	var payload = {
		"yelplink" : link
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updateyelplink.do", callBackUpdateSocialLink, payload);
        $('#icn-yelp').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

// Update Social links - zillow
/*$('body').on('click', '#prof-edit-social-link .icn-zillow', function() {
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Zillow link",
		"onblur" : "updateZillowLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});*/

function updateZillowLink(link) {
	var payload = {
		"zillowlink" : link
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updatezillowlink.do", callBackUpdateSocialLink, payload);
        $('#icn-zillow').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

// Update Social links - lendingTree
$('body').on('click', '#prof-edit-social-link .icn-lendingtree', function() {
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
		callAjaxPostWithPayloadData("./updatelendingtreelink.do", callBackUpdateSocialLink, payload);
        $('#icn-lendingtree').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

$('body').on('click', '#prof-edit-social-link .icn-realtor', function() {
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Realtor link",
		"onblur" : "updateRealtorLink(this.value);$('#social-token-text').hide();"
	});
	$('#social-token-text').val(link);
});

function updateRealtorLink(link) {
	var payload = {
		"realtorLink" : link
	};
	if (isValidUrl(link)) {
		callAjaxPostWithPayloadData("./updateRealtorlink.do", callBackUpdateSocialLink, payload);
        $('#icn-realtor').attr("data-link", link);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

function callBackUpdateSocialLink(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
	
	$('#social-token-text').val('');
}

function isValidUrl(url){
	var myVariable = url;
	if(webAddressRegEx.test(myVariable)) {
		return true;
	} else {
		return false;
	}
}

// Adjust image
function adjustImage() {
	var windW = window.innerWidth;
	if (windW < 768) {
		//$('.mobile-tabs').children('.mob-icn-active').click();
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
		// $('.lp-con-row-item').width('auto');
		$('.footer-main-wrapper').show();
	}
}

// Function to show map on the screen
/*
 * function initializeGoogleMap() { var mapCanvas =
 * document.getElementById('map-canvas'); var geocoder = new
 * google.maps.Geocoder(); var address = "Raremile technologies,HSR
 * layout,bangalore, 560102"; var latitude = 45; var longitude = -73;
 * geocoder.geocode({ 'address' : address }, function(results, status) { if
 * (status == google.maps.GeocoderStatus.OK) { latitude =
 * results[0].geometry.location.lat(); longitude =
 * results[0].geometry.location.lng(); var mapOptions = { center: new
 * google.maps.LatLng(latitude, longitude), zoom: 15, mapTypeId:
 * google.maps.MapTypeId.ROADMAP };
 * 
 * map = new google.maps.Map(mapCanvas, mapOptions);
 * map.setCenter(results[0].geometry.location); marker = new
 * google.maps.Marker({ position: results[0].geometry.location, map: map, title:
 * "RM" }); } }); }
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
	// Common call for all cases
	fetchAvgRating(attrName, attrVal);
	fetchReviewCount(attrName, attrVal, minScore);
	fetchReviews(attrName, attrVal, minScore, startIndex, numOfRows);
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

	/**
	 * Click on region
	 */
	$('.comp-region').unbind('click');
	$('.comp-region').click(
			function(e) {
				if ($(this).attr("data-openstatus") == "closed") {
					fetchRegionHierarchyOnClick($(this).attr('data-regionid'));
					$(this).attr("data-openstatus", "open");
				} else {
					$('#comp-region-branches-' + $(this).attr('data-regionid'))
							.slideUp(200);
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
	$("." + bindingClass).click(
			function(e) {
				e.stopPropagation();
				if ($(this).attr("data-openstatus") == "closed") {
					fetchBranchHierarchyOnClick($(this).attr('data-branchid'));
					$(this).attr("data-openstatus", "open");
				} else {
					$(
							'#comp-branch-individuals-'
									+ $(this).attr('data-branchid')).slideUp(
							200);
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
	$("." + imgDivClass).each(
			function() {
				var imageUrl = $(this).attr('data-imageurl');
				if (imageUrl != "" && imageUrl != undefined) {
					$(this).css("background",
							"url(" + imageUrl + ") no-repeat center");
					$(this).css("background-size", "100%");
				}
			});
}

function fetchReviews(attrName, attrVal, minScore, startIndex, numOfRows) {
	var url = "./fetchreviews.do?" + attrName + "=" + attrVal + "&minScore="
			+ minScore + "&startIndex=" + startIndex + "&numOfRows="
			+ numOfRows;
	callAjaxGET(url, function(data) {
		$("#prof-review-item").append(data);
		$(".review-ratings").each(function() {
			changeRatingPattern($(this).attr("data-rating"), $(this), false, $(this).attr("data-source"));
		});

		$('.icn-plus-open').click(function() {
			$(this).hide();
			$(this).parent().find('.ppl-share-social,.icn-remove').show();
		});

		$('.icn-remove').click(function() {
			$(this).hide();
			$(this).parent().find('.ppl-share-social').hide();
			$(this).parent().find('.icn-plus-open').show();
		});
	}, true);
}

// fetch review count
function fetchReviewCount(attrName, attrVal, minScore) {
	var url = "./fetchreviewcount.do?" + attrName + "=" + attrVal
			+ "&minScore=" + minScore;
	callAjaxGET(url, paintReviewCount, true);
}

function paintReviewCount(reviewCount) {
	if (reviewCount != undefined) {
		if (reviewCount > 0) {
			$("#prof-company-review-count").click(function() {
				if(window.innerWidth < 768){
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
		changeRatingPattern(avgRating, $("#rating-avg-comp"),true);
	}
}

// Edit EmailIds
$(document).on('blur', '#contant-info-container input[data-email]', function() {
	if (!$(this).val() || !emailRegex.test(this.value)
			|| ($(this).val() == $('#' + $(this).attr("id") + '-old').val())) {
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
		callAjaxPostWithPayloadData("./updateemailids.do", callBackOnUpdateMailIds, payload);
	}, 0);
});

function callBackOnUpdateMailIds(data) {
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

function bindClickForIndividuals(elementClass) {
	$("." + elementClass).unbind('click');
	$("." + elementClass).click(function(e) {
		e.stopPropagation();
	});
}

function countPosts() {
	var success = false;
	proPostStartIndex = 0;
	$.ajax({
		url : "./postscountforuser.do",
		type : "GET",
		dataType : "text",
		async : false,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				proPostCount = parseInt(data.responseText);
				showPosts();
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error(e.responseText);
		}
	});
}

function showPosts(fromStart) {
	if(fromStart){
		proPostStartIndex = 0;
		proPostCount++;
	}
	var success = false;
	var payload = {
		"batchSize" : proPostBatchSize,
		"startIndex" : proPostStartIndex
	};
	$.ajax({
		url : "./postsforuser.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		async : false,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintPosts(data.responseJSON);
				proPostStartIndex += proPostBatchSize;
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function paintPosts(posts) {
	var divToPopulate = "";
	$.each(posts, function(i, post) {
		var iconClass = "";
		var href="javascript:void(0)";
		if(post.source == "google"){
			iconClass = "icn-gplus";
		}
		else if(post.source == "SocialSurvey")
			iconClass = "icn-ss";
		else if(post.source == "facebook"){
			iconClass = "icn-fb";
			href="http://www.facebook.com/"+post.postId;
		}
		else if(post.source == "twitter"){
			iconClass = "icn-twit";
			var res = post.postText.split("http");
			href="http"+res[1];
		}
		else if(post.source == "linkedin"){
			iconClass = "icn-lin";
		}
		if(typeof post.postUrl!=  "undefined" ){
			 href= post.postUrl;
		}
		var hrefComplet='<a href='+href+' target="_blank">';
		
		divToPopulate += '<div class="tweet-panel-item bord-bot-dc clearfix">'		
				+ hrefComplet
				+ '<div class="tweet-icn ' + iconClass + ' float-left"></div>'
				+"</a>"
				+ '<div class="tweet-txt float-left">'
				+ '<div class="tweet-text-main">' + linkify(post.postText) + '</div>'
				+ '<div class="tweet-text-link"><em>' + post.postedBy
				+ '</em></div>' + '<div class="tweet-text-time"><em>'
				+ convertUserDateToWeekFormt(new Date(post.timeInMillis)) + '</em></div>'
				+ '</div>';
		
		if(post.source == "SocialSurvey"){
			var divToDeleteSurvey = '<div class="dlt-survey-wrapper hide"><div surveymongoid=' + post._id + ' class="post-dlt-icon reg-err-pu-close float-left">'
								+ '</div></div>';
			divToPopulate += divToDeleteSurvey;
		}
		
		divToPopulate += '</div>';
		
		
		
	});

	if (proPostStartIndex == 0){
		$('#prof-posts').html(divToPopulate);
		$('#prof-posts').perfectScrollbar({
			suppressScrollX : true
		});
		$('#prof-posts').perfectScrollbar('update');
	}
	else{
		$('#prof-posts').append(divToPopulate);
		$('#prof-posts').perfectScrollbar('update');
	}

	$('#prof-posts').on('scroll',function(){
		var scrollContainer = this;
		if (scrollContainer.scrollTop === scrollContainer.scrollHeight
					- scrollContainer.clientHeight) {
				if (proPostStartIndex < proPostCount)
					showPosts();
		}
	});
}

function showDashboardButtons(columnName, columnValue){
	console.log(columnName);
	console.log(columnValue);
	var payload={
			"columnName" : columnName,
			"columnValue" : columnValue
	};
	callAjaxGetWithPayloadData('./dashboardbuttonsorder.do', paintDashboardButtons, payload, true);
}

function paintDashboardButtons(data){
	data = $.parseJSON(data);
	var columnName = data.columnName;
	var columnValue = data.columnValue;
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
			}else if (stages[i].profileStageKey == 'ZILLOW_PRF') {
				contentToDisplay = 'Connect to Zillow';
			}else if (stages[i].profileStageKey == 'GOOGLE_PRF') {
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
			}
			if (i == 0) {
				$('#dsh-btn2').data('social', stages[i].profileStageKey);
				$('#dsh-btn2').html(contentToDisplay);
				$('#dsh-btn2').removeClass('hide');
			}
			if (i == 1) {
				$('#dsh-btn3').data('social', stages[i].profileStageKey);
				$('#dsh-btn3').html(contentToDisplay);
				$('#dsh-btn3').removeClass('hide');
			}
		}
	}
	$('#dsh-btn1').click(function() {
		if (columnName == 'agentId') {
			sendSurveyInvitation();
		} else if (accountType == "INDIVIDUAL") {
			sendSurveyInvitation();
		} else {
			sendSurveyInvitationAdmin(columnName, columnValue);
		}
	});
	$('#dsh-btn2').click(function(){
		var buttonId = 'dsh-btn2';
		var task = $('#dsh-btn2').data('social');
		dashboardButtonAction(buttonId, task, columnName, columnValue);
	});
	$('#dsh-btn3').click(function(){
		var buttonId = 'dsh-btn3';
		var task = $('#dsh-btn3').data('social');
		dashboardButtonAction(buttonId, task, columnName, columnValue);
	});
}

function dashboardButtonAction(buttonId, task, columnName, columnValue){
	if(task=='FACEBOOK_PRF'){
		openAuthPageDashboard('facebook', columnName, columnValue);
	}
	else if(task=='GOOGLE_PRF'){
		openAuthPageDashboard('google', columnName, columnValue);
	}
	else if(task=='ZILLOW_PRF'){
		openAuthPageDashboard('zillow', columnName, columnValue);
	}
	else if(task=='YELP_PRF'){
		showMainContent('./showprofilepage.do');
		editProfileForYelp = true;
	}
	else if(task=='LINKEDIN_PRF'){
		openAuthPageDashboard('linkedin', columnName, columnValue);
	}
	else if(task=='TWITTER_PRF'){
		openAuthPageDashboard('twitter', columnName, columnValue);
	}
	else if(task=='LICENSE_PRF'){
		showMainContent('./showprofilepage.do');
		editProfileForLicense = true;
	}
	else if(task=='HOBBIES_PRF'){
		showMainContent('./showprofilepage.do');
		editProfileForHobbies = true;
	}
	else if(task=='ACHIEVEMENTS_PRF'){
		showMainContent('./showprofilepage.do');
		editProfileForAchievements = true;
	}
}

// Update Disclaimer details
$(document).on('click', '#disclaimer-text', function() {
	$('#disclaimer-text').addClass('prof-name-edit');
});

$(document).on('blur', '#disclaimer-text', function() {
	$('#disclaimer-text').removeClass('prof-name-edit');
	
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
				$('#disclaimer-default').val(disclaimer);
			}

			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload);
	}
});

//Dashboard admin reports
$(document).on('change','#download-survey-reports',function(){
//	var selectedValue =
});

$(document).on('click','#dsh-dwnld-report-btn',function(){
	var selectedValue = $('#download-survey-reports').val();
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	
	var key = parseInt(selectedValue);
	switch (key) {
	case 1:
		console.log("agent-ranking");
		window.location.href = "/downloadagentrankingreport.do?columnName=" + colName + "&columnValue=" + colValue
			+ "&startDate=" + startDate + "&endDate=" + endDate;
		break;
	case 2:
		console.log("survey-results");
		window.location.href = "/downloadcustomersurveyresults.do?columnName=" + colName + "&columnValue=" + colValue
			+ "&startDate=" + startDate + "&endDate=" + endDate;
		break;
	case 3:
		console.log("social-monitor");
		window.location.href = "/downloaddashboardsocialmonitor.do?columnName=" + colName + "&columnValue=" + colValue
			+ "&startDate=" + startDate + "&endDate=" + endDate;
		break;
	case 4:
		console.log("incomplete-survey");
		window.location.href = "/downloaddashboardincompletesurvey.do?columnName=" + colName + "&columnValue=" + colValue
			+ "&startDate=" + startDate + "&endDate=" + endDate;
		break;
	default:
		break;
	}
});

//function to switch to admin 
function userSwitchToAdmin() {
	callAjaxGET("/switchtoadmin.do", function(data){
		if(data == "success") {
			window.location = window.location.origin + '/userlogin.do';
		}
	}, true);
}

function initializeVerticalAutcomplete() {
	$('#prof-vertical').autocomplete({
		minLength: 1,
		source: verticalsMasterList,
		delay : 0,
		autoFocus : true,
		close: function(event, ui) {},
		select: function(event, ui) {},
		create: function(event, ui) {
	        $('.ui-helper-hidden-accessible').remove();
		}
	});
	$("#prof-vertical").keydown(function(e){
  	    if( e.keyCode != $.ui.keyCode.TAB) return; 
  	    
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

function getIncompleteSurveyCount(colName, colValue){
	startIndexInc = 0;
	var payload = {
		"columnName" : colName,
		"columnValue" : colValue
	};
	callAjaxGetWithPayloadData("./fetchdashboardincompletesurveycount.do", function(data) {
		$('#icn-sur-popup-cont').attr("data-total", data);
		$('#dsh-inc-srvey').attr("data-total", data);
		var totalCount = parseInt(data);
		var batchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch")); 
		var numPages = 0;
		if(parseInt( totalCount % batchSize) == 0) {
			numPages = parseInt( totalCount / batchSize);
		}else {
			numPages = parseInt(parseInt( totalCount / batchSize) + 1);
		}
		$('#paginate-total-pages').html(numPages);
		
		//Show dashboard incomplete reviews
		showIncompleteSurvey(colName, colValue);
		$('#dsh-inc-srvey').perfectScrollbar('update');
		
	}, payload, true);
}

$(document).on('click', '#sur-next.paginate-button',function(){
	var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
	var incompleteSurveyBatchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	incompleteSurveyStartIndex = incompleteSurveyStartIndex + incompleteSurveyBatchSize;
	$('#icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);	
});

$(document).on('click', '#sur-previous.paginate-button',function(){
	var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
	var incompleteSurveyBatchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	if(incompleteSurveyStartIndex % incompleteSurveyBatchSize == 0) {
		incompleteSurveyStartIndex = parseInt(incompleteSurveyStartIndex / incompleteSurveyBatchSize)  - 1;
	} else {
		incompleteSurveyStartIndex = parseInt(incompleteSurveyStartIndex / incompleteSurveyBatchSize);	
	}
	incompleteSurveyStartIndex = incompleteSurveyStartIndex * incompleteSurveyBatchSize ;
	$('#icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
});


$(document).on('keypress', '#sel-page', function(e) {
	//if the letter is not digit then don't type anything
	if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
		return false;
	}
	var batchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	var total = parseInt($('#icn-sur-popup-cont').attr("data-total"));
	var prevPageNoVal = parseInt($('#sel-page').val());
	if(prevPageNoVal == NaN) {
		prevPageNoVal = 0;
	}
	var pageNo = prevPageNoVal + String.fromCharCode(e.which);
	pageNo = parseInt(pageNo);
	var incompleteSurveyStartIndex = parseInt(pageNo-1) * batchSize;
	if(incompleteSurveyStartIndex >= total || incompleteSurveyStartIndex <= 0) {
		return false;
	}
});

function paginateIncompleteSurvey(){
	$('#sel-page').blur();
	var pageNo = parseInt($('#sel-page').val());
	if(pageNo == NaN || pageNo <= 0) {
		return false;
	}
	var incompleteSurveyStartIndex = 0;
	var batchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	incompleteSurveyStartIndex = parseInt(pageNo-1) * batchSize;
	
	$('#icn-sur-popup-cont').attr("data-start", incompleteSurveyStartIndex);
	paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
}

$(document).on('keyup', '#sel-page', function(e) {
	if(e.which == 13) {
		paginateIncompleteSurvey();
	}
});

$(document).on('change', '#sel-page', function(e) {
	delay(function() {
		paginateIncompleteSurvey();
	}, 100);
});

function showIncompleteSurveyListPopup() {
	$('#icn-sur-popup-cont').attr("data-start", 0);
	$("#overlay-incomplete-survey").show();
	paintIncompleteSurveyListPopupResults(0);		
}

function paintIncompleteSurveyListPopupResults(incompleteSurveystartIndex){
	var incompleteSurveyBatchSize = parseInt($('#icn-sur-popup-cont').attr("data-batch"));
	$('#sel-page').val((incompleteSurveystartIndex / incompleteSurveyBatchSize) + 1);
	var payload = {
		"columnName" : colName,
		"columnValue" : colValue,
		"startIndex" : incompleteSurveystartIndex,
		"batchSize" : $('#icn-sur-popup-cont').attr("data-batch")
	};
	callAjaxGetWithPayloadData("./fetchincompletesurveypopup.do", function(data) {
		disableBodyScroll();
		$('#icn-sur-popup-cont').html(data);
		if(parseInt(incompleteSurveystartIndex) > 0 ) {
			$('#sur-previous').addClass('paginate-button');
		} else {
			$('#sur-previous').removeClass('paginate-button');
		}
		incompleteSurveystartIndex = parseInt(incompleteSurveystartIndex) + parseInt($('#icn-sur-popup-cont').children('.dash-lp-item').size());
		var totalSurveysCount = parseInt($('#icn-sur-popup-cont').attr("data-total"));
		if(incompleteSurveystartIndex < totalSurveysCount ) {
			$('#sur-next').addClass('paginate-button');
		} else {
			$('#sur-next').removeClass('paginate-button');
		}
	}, payload, false);
}

function hideIncompleteSurveyListPopup() {
	enableBodyScroll();
	$("#overlay-incomplete-survey").hide();
	$('#icn-sur-popup-cont').html('');
	$('#icn-sur-popup-cont').attr("data-start", 0);
	$('#icn-sur-popup-cont').data('selected-survey', new Array());
}

$(document).on('click','#del-mult-sur-icn.mult-sur-icn-active',function(){
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
			
			//unselect all the options after deleting
			$('#icn-sur-popup-cont').data('selected-survey',[]);
			
			var totalIncSurveys = $('#icn-sur-popup-cont').attr('data-total');
			totalIncSurveys = totalIncSurveys - incompleteSurveyIds.length;
			$('#icn-sur-popup-cont').attr('data-total', totalIncSurveys);
			var batchSize = parseInt($('#icn-sur-popup-cont').attr('data-batch'));
			var newTotalPages = 0;
			if(totalIncSurveys % batchSize == 0) {
				newTotalPages =  totalIncSurveys / batchSize;
			} else {
				newTotalPages =  parseInt(totalIncSurveys / batchSize) + 1;
			}
			$('#paginate-total-pages').html(newTotalPages);
			for (var i=0; i < incompleteSurveyIds.length; i++) {
				$('div[data-iden="sur-pre-'+incompleteSurveyIds[i]+'"]').remove();
			}
			
			$('#overlay-toast').html('Survey reminder request deleted successfully');
			showToast();
			
			//update the page
			var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
			paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
			
			//Update the incomplete survey on dashboard
			getIncompleteSurveyCount(colName, colValue);
			
			$('#del-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#resend-mult-sur-icn').removeClass('mult-sur-icn-active');
		}
	}, true, {});
}

$(document).on('click','#resend-mult-sur-icn.mult-sur-icn-active',function(){
	var selectedSurveys = $('#icn-sur-popup-cont').data('selected-survey');
	resendMultipleIncompleteSurveyRequests(selectedSurveys);
});

function resendMultipleIncompleteSurveyRequests(incompleteSurveyIds) {
	callAjaxPOSTWithTextData("/resendmultipleincompletesurveyrequest.do?surveysSelected=" + incompleteSurveyIds, function(data) {
		if (data == "success") {
			//unselect all the options after deleting
			$('#icn-sur-popup-cont').data('selected-survey',[]);
			
			$('#overlay-toast').html('Survey reminder request resent successfully');
			showToast();
			$('#del-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#resend-mult-sur-icn').removeClass('mult-sur-icn-active');
			$('#icn-sur-popup-cont').data('selected-survey', []);
			$('.sur-icn-checkbox').addClass('sb-q-chk-yes').removeClass('sb-q-chk-no');
			
			//Update the incomplete survey on dashboard
			showIncompleteSurvey(colName, colValue);
			$('#dsh-inc-srvey').perfectScrollbar('update');
			
			//update the page
			var incompleteSurveyStartIndex = parseInt($('#icn-sur-popup-cont').attr("data-start"));
			paintIncompleteSurveyListPopupResults(incompleteSurveyStartIndex);
		}
	}, true, {});
}

function bindDatePickerforSurveyDownload() {
	// initializing datepickers
	var startDate;
	var fromEndDate = new Date();
	var toEndDate = new Date();
	$("#dsh-start-date").datepicker({
		orientation: "auto",
		format: 'mm/dd/yyyy',
		endDate: fromEndDate,
		todayHighlight: true,
		clearBtn: true,
		autoclose: true
	})
	.on('changeDate', function(selected){
        startDate = new Date(selected.date.valueOf());
        startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
        $('#dsh-end-date').datepicker('setStartDate', startDate);
    });
	
	$("#dsh-end-date").datepicker({
		orientation: "auto",
		format: 'mm/dd/yyyy',
		endDate: toEndDate,
		todayHighlight: true,
		clearBtn: true,
		autoclose: true
	})
	.on('changeDate', function(selected){
        fromEndDate = new Date(selected.date.valueOf());
        fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
        $('#dsh-start-date').datepicker('setEndDate', fromEndDate);
    });
}

function bindDatePickerforIndividualSurveyDownload() {
	// initializing datepickers
	var startDate;
	var fromEndDate = new Date();
	var toEndDate = new Date();
	$("#indv-dsh-start-date").datepicker({
		orientation: "auto",
		format: 'mm/dd/yyyy',
		endDate: fromEndDate,
		todayHighlight: true,
		clearBtn: true,
		autoclose: true
	})
	.on('changeDate', function(selected){
        startDate = new Date(selected.date.valueOf());
        startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
        $('#indv-dsh-end-date').datepicker('setStartDate', startDate);
    });
	
	$("#indv-dsh-end-date").datepicker({
		orientation: "auto",
		format: 'mm/dd/yyyy',
		endDate: toEndDate,
		todayHighlight: true,
		clearBtn: true,
		autoclose: true
	})
	.on('changeDate', function(selected){
        fromEndDate = new Date(selected.date.valueOf());
        fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
        $('#indv-dsh-start-date').datepicker('setEndDate', fromEndDate);
    });
}

function editPositions() {
	callAjaxGET("/geteditpositions.do", function(data) {
		createEditPositionsPopup("Edit positions", data);
		
		addDatePcikerForPositions();
		$('.pos-edit-icn').click(function() {
			$(this).parent().find('input').prop('readonly',false);
		});
		$('.add-pos-link').click(function() {
			var htmlToAppned = "<div class='pos-cont margin-top-10 text-left'>" + 
					"<div class='checkbox-input-cont'>" +
					"<div class='checkbox-input checkbox-iscurrent' data-checked='false'></div>" +
					"Current Employer</div>" +
					"<input name='companyName' class='pos-input' placeholder='Company Name'>" +
					"<input name='title' class='pos-input' placeholder='Job Title'>" +
					"<input name='startTime' class='pos-input'placeholder='Start Date'>" +
					"<input name='endTime' class='pos-input' placeholder='End Date'>" +
					"<div class='pos-remove-icn'></div>" +
					"</div>";
			$(this).before(htmlToAppned);
			//$(this).remove();
			addDatePcikerForPositions();
		});
	}, true);
	
}
/**
 * Method to call warning popup controller method.
 */
function editProfileUrl() {
	callAjaxGET("/showurleditwarning.do", function(data) {
		createEditProfileUrlPopup("Warning", data);
	}, true);
	
}

/**
 * Warning popup
 * @param header
 * @param body
 */
function createEditProfileUrlPopup(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Continue");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-continue').off();
	$('#overlay-continue').click(function(){
		//Add method to show the zillow type popup
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		updateProfileUrl();
		overlayRevert();
	});
	
	$('#overlay-main').show();
	disableBodyScroll();
}

function updateProfileUrl(){
	window.open("./editprofileurl.do","_blank", "width=800,height=600,scrollbars=yes");
}

$(document).on('click', '.checkbox-iscurrent', function(e){
	var isCurrent = $(this).attr('data-checked');
	if(isCurrent == "true") {
		$(this).attr('data-checked',"false");
		$(this).parent().parent().find('input[name="endTime"]').show();
	} else {
		$(this).attr('data-checked',"true");
		$(this).parent().parent().find('input[name="endTime"]').hide();
	}
});

$(document).on('click', '.pos-remove-icn', function(e){
	$(this).parent().remove();
	updatePositions();
});

function addDatePcikerForPositions() {
	
	var startDate;
	var fromEndDate = new Date();
	var toEndDate = new Date();
	
	$('input[name="endTime"]').datepicker({
		orientation: "auto",
		format: "mm-yyyy",
	    startView: "months", 
	    minViewMode: "months",
	    endDate : toEndDate,
	    todayHighlight: true,
		clearBtn: true,
		autoclose: true
	}).on('changeDate', function(selected){
        fromEndDate = new Date(selected.date.valueOf());
        fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
        $(this).parent().find('input[name="startTime"]').datepicker('setEndDate', fromEndDate);
    });
	$('input[name="startTime"]').datepicker({
		orientation: "auto",
		format: "mm-yyyy",
	    startView: "months", 
	    minViewMode: "months",
	    endDate : toEndDate,
	    todayHighlight: true,
		clearBtn: true,
		autoclose: true
	}).on('changeDate', function(selected){
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
	$('#overlay-continue').click(function(){
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
		
		if(isCurrentString == "true") {
			isCurrent = true;
		}
		
		if(companyName == undefined || companyName == '') {
			$(this).find('input[name="companyName"]').focus();
			$('#overlay-toast').html("Please enter company name");
			showToast();
			isFormValid = false;
			return false;
		}
		
		if(title == undefined || title == '') {
			$(this).find('input[name="title"]').focus();
			$('#overlay-toast').html("Please enter title");
			showToast();
			isFormValid = false;
			return false;
		}
		
		if(startTime == undefined || startTime == '') {
			$(this).find('input[name="startTime"]').focus();
			$('#overlay-toast').html("Please enter start time");
			showToast();
			isFormValid = false;
			return false;
		} else {
			var startDateSplit = startTime.split("-");
			if(startDateSplit.length < 2) {
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
		
		if(!isCurrent) {
			if(endTime == undefined || endTime == ''){
				$(this).find('input[name="endTime"]').focus();
				$('#overlay-toast').html("Please enter end time");
				showToast();
				isFormValid = false;
				return false;
			} else {
				var endDateSplit = endTime.split("-");
				if(endDateSplit.length < 2) {
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
	
	if(positions.length > 0) {
		positions = JSON.stringify(positions);
	} else {
		return false;
		$('#overlay-toast').html("Add positions");
		showToast();
	}
	
	callAjaxPOSTWithTextData("/updatepositions.do?positions="+positions, function(data) {
		if(data == "success") {
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
	if(positions != undefined && positions != "")
		positionsArray = JSON.parse(positions);
	if(positionsArray.length > 0) {
		for(var index in positionsArray) {
			var position = positionsArray[index];
			contentToAppend += '<div class="postions-content">';
			contentToAppend += '<div class="lp-pos-row-1 lp-row clearfix">'+position.name+'</div>';
			contentToAppend += '<div class="lp-pos-row-2 lp-row clearfix">'+position.title+'</div>';
			if(position.isCurrent) {
				contentToAppend += '<div class="lp-pos-row-3 lp-row clearfix">'+position.startTime + ' - Current' +'</div>';
			} else {
				contentToAppend += '<div class="lp-pos-row-3 lp-row clearfix">'+position.startTime + ' - ' + position.endTime +'</div>';
			}
		}
	} else {
		contentToAppend = "No positions added yet";
	}
	
	$('#positions-container').html(contentToAppend);
}

$(document).on('click','#hdr-config-settings-dropdown', function(e) {
	$('#hdr-link-item-dropdown').toggle();
});

$(document).on('mouseover','#hdr-link-item-config', function(e) {
	$('#hdr-link-item-dropdown').show();
});

$(document).on('mouseout','#hdr-link-item-config', function(e) {
	$('#hdr-link-item-dropdown').hide();
});

$(document).on('click','.hdr-link-item-dropdown-item',function(e) {
	$('#hdr-link-item-dropdown').hide();
	showOverlay();
});


//Disconnect social media
function disconnectSocialMedia(socialMedia) {
	if($('div[data-social="'+socialMedia+'"]').text() == undefined || $('div[data-social="'+socialMedia+'"]').text() == ''){
		return;
	}
	
	var payload = {
		"socialMedia" : socialMedia	
	};
	
	callAjaxPostWithPayloadData("/disconnectsocialmedia.do", function(data) {
		if(data == "success"){
			$('div[data-social="'+socialMedia+'"]').html('');
			$('div[data-social="'+socialMedia+'"]').parent().find('.social-media-disconnect').addClass('social-media-disconnect-disabled').removeAttr("onclick").removeAttr("title");
			$('#overlay-toast').html('Successfully disconnected ' + socialMedia);
			showToast();
		} else {
			$('#overlay-toast').html('Some error occurred while disconnecting ' + socialMedia);
			showToast();
		}
	}, payload, true);	
}


function showProfileLinkInEditProfilePage(source, profileUrl){
	if(source=='facebook'){
		$('#edt-prof-fb-lnk').html(profileUrl);
	}
	else if(source=='twitter'){
		$('#edt-prof-twt-lnk').html(profileUrl);
	}
	else if(source=='linkedin'){
		$('#edt-prof-linkedin-lnk').html(profileUrl);
	}
	else if(source=='google'){
		$('#edt-prof-ggl-lnk').html(profileUrl);
	}
}

// Send Survey Agent
$(document).on('input', '#wc-review-table-inner[data-role="agent"] input', function() {
	var parentDiv = $(this).parent().parent();
	if (parentDiv.is(':last-child')) {
		var htmlData = '<div class="wc-review-tr clearfix">'
			+ '<div class="wc-review-tc1 float-left"><input class="wc-review-input wc-review-fname"></div>'
			+ '<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-lname"></div>'
			+ '<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-email"></div>'
			+ '<div class="wc-review-tc4 float-left"><div class="wc-review-rmv-icn hide"></div></div>'
		+ '</div>';
		parentDiv.after(htmlData);
		
		// enable remove button
		if ($('#wc-review-table-inner').children().length > 2) {
			$('.wc-review-rmv-icn').show();
		}
		
		// setting up perfect scrollbar
		setTimeout(function() {
			$('#wc-review-table').perfectScrollbar();
			$('#wc-review-table').perfectScrollbar('update');
		}, 1000);
	}
});

//Send Survey Admin
$(document).on('input', '#wc-review-table-inner[data-role="admin"] input', function() {
	var parentDiv = $(this).parent().parent();
	if (parentDiv.is(':last-child')) {
		var htmlData = '<div class="wc-review-tr clearfix">'
			+ '<div class="wc-review-tc1 float-left pos-relative"><input data-name="agent-name" class="wc-review-input wc-review-agentname"></div>'
			+ '<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-fname"></div>'
			+ '<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-lname"></div>'
			+ '<div class="wc-review-tc4 float-left"><input class="wc-review-input wc-review-email"></div>'
			+ '<div class="wc-review-tc5 float-left"><div class="wc-review-rmv-icn hide"></div></div>'
		+ '</div>';
		parentDiv.after(htmlData);
		
		// enable remove button
		if ($('#wc-review-table-inner').children().length > 2) {
			$('.wc-review-rmv-icn').show();
		}
		
		// setting up perfect scrollbar
		setTimeout(function() {
			$('#wc-review-table').perfectScrollbar();
			$('#wc-review-table').perfectScrollbar('update');
		}, 1000);
	}
});

$(document).on('click', '.wc-review-rmv-icn', function() {
	var parentDiv = $('#wc-review-table-inner');
	
	// disable remove button
	if (parentDiv.children().length <= 3) {
		$('.wc-review-rmv-icn').hide();
	}
	$(this).parent().parent().remove();

	// setting up perfect scrollbar
	setTimeout(function() {
		$('#wc-review-table').perfectScrollbar();
		$('#wc-review-table').perfectScrollbar('update');
	}, 1000);
});

$(document).on('click', '#wc-send-survey', function() {
	var receiversList = [];
	var agentId = undefined;
	var columnName = undefined;
	var firstname = "";
	var lastname = "";
	var idx=0;
	var exit = false;
	$('#wc-review-table-inner').children().each(function() {
		if (!$(this).hasClass('wc-review-hdr')) {
			var dataName = $(this).find('input.wc-review-agentname').first().attr('data-name');
			if (dataName == 'agent-name') {
				agentId = $(this).find('input.wc-review-agentname').first().attr('agent-id');

				if (idx == 0) {
					columnName = $(this).find('input.wc-review-agentname').first().attr('column-name');
					idx ++;
				}
			}
			
			firstname = $(this).find('input.wc-review-fname').first().val();
			lastname = $(this).find('input.wc-review-lname').first().val();
			
			var emailId = $(this).find('input.wc-review-email').first().val();
			
			if(firstname == "" && emailId != ""){
				$('#overlay-toast').html('Please enter Firstname for all the customer');
				showToast();
				exit = true;
				return false;
			} else if (agentId != undefined && firstname == ""){
				$('#overlay-toast').html('Please enter Firstname for all the customer');
				showToast();
				exit = true;
				return false;
			}
			if (emailRegex.test(emailId)) {
				var receiver = new Object();
				receiver.firstname = firstname;
				receiver.lastname = lastname;
				receiver.emailId = emailId;
				if (dataName == 'agent-name') {
					receiver.agentId = agentId;
					if(agentId == undefined){
						$('#overlay-toast').html('Please enter Agent name for all survey requests');
						showToast();
						exit = true;
						return false;					
					}
				}
				receiversList.push(receiver);
			} else if(firstname != ""){
				$('#overlay-toast').html('Please enter valid email for ' + firstname);
				showToast();
				exit = true;
				return false;
			}
		}
	});

	if(exit){
		exit = false;
		return false;
	}
	
	//Check if recievers list empty
	if(receiversList.length == 0){
		$('#overlay-toast').html('Add customers to send survey request!');
		showToast();
		exit = false;
		return false;
	}
	
	receiversList = JSON.stringify(receiversList);
	var payload = {
		"receiversList" : receiversList,
		"source" : 'agent'
	};
	if (columnName != undefined) {
		payload = {
			"receiversList" : receiversList,
			"source" : 'admin',
			"columnName" : columnName,
		};
	}

	$(this).closest('.overlay-login').hide();
	callAjaxPostWithPayloadData("./sendmultiplesurveyinvites.do", function(data) {
		
		//Update the incomplete survey on dashboard
		getIncompleteSurveyCount(colName, colValue);
		
		$('#overlay-toast').html('Survey request sent successfully!');
		showToast();
		enableBodyScroll();
	}, payload);
});

$(document).on('click', '#wc-skip-send-survey', function() {
	$('#overlay-send-survey').html('');
	enableBodyScroll();
});

function sendSurveyInvitation() {
	disableBodyScroll();
	callAjaxGET("./sendsurveyinvitation.do", function(data) {
		$('#overlay-send-survey').html(data);
		if ($("#welcome-popup-invite").length) {
			$('#overlay-send-survey').removeClass("hide");
			$('#overlay-send-survey').show();
		}
	}, true);
}

function sendSurveyInvitationAdmin(columnName, columnValue) {
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
	}, payload, true);
}

function linkedInDataImport() {
	disableBodyScroll();
	callAjaxGET("./linkedindataimport.do", function(data) {
		$('#overlay-linkedin-import').html(data);
		if ($("#welocome-step1").length) {
			$('#overlay-linkedin-import').removeClass("hide");
			$('#overlay-linkedin-import').show();
		}
	}, true);
}
