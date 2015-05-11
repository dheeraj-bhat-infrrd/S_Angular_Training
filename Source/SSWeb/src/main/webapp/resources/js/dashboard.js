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

$(document).on('click', '#restart-survey-mail', function(e) {
	
	var firstName = $(this).parent().parent().parent().attr('data-firstname');
	var lastName = $(this).parent().parent().parent().attr('data-lastname');
	var agentName = $(this).parent().parent().parent().attr('data-agentname');
	var customerEmail = $(this).parent().parent().parent().attr('data-customeremail');
	var agentId = $(this).parent().parent().parent().attr('data-agentid');
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
});

$('body').click(function() {
	$('#hr-dd-wrapper').slideUp(200);
});

$(document).scroll(function() {
	if ((window.innerHeight + window.pageYOffset) >= (document.body.offsetHeight) && startIndexCmp < totalReviews) {
		showReviews(colName, colValue);
	}
});

function paintDashboard(profileMasterId, newProfileName, newProfileValue, typeoOfAccount) {
	accountType = typeoOfAccount;
	startIndexCmp = 0;
	batchSizeCmp = 2;
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
	
	if (profileMasterId == 1) {
		showCompanyAdminFlow(newProfileName, newProfileValue);
	} else if (profileMasterId == 2) {
		showRegionAdminFlow(newProfileName, newProfileValue);
	} else if (profileMasterId == 3) {
		showBranchAdminFlow(newProfileName, newProfileValue);
	} else if (profileMasterId == 4) {
		showAgentFlow(newProfileName, newProfileValue);
	}

	$('#dsh-inc-dwnld').click(function() {
		window.location.href = "./downloaddashboardincompletesurvey.do?columnName="
				+ colName + "&columnValue=" + colValue;
	});

	$('#dsh-cmp-dwnld').click(function() {
		window.location.href = "./downloaddashboardcompletesurvey.do?columnName="
				+ colName + "&columnValue=" + colValue;
	});
	// Loads the image in circle od header.
	loadDisplayPicture(profileMasterId);
	// Loads the master image in dashboard.
	showDisplayPic();
}

function showCompanyAdminFlow(newProfileName, newProfileValue) {
	colName = newProfileName;
	colValue = newProfileValue;

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();

	showProfileDetails(newProfileName, 0, 30);
	bindSelectButtons();
	if((accountType!="INDIVIDUAL") && (accountType!="FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, 0);
	showSurveyStatisticsGraphically(newProfileName, 0);

	getReviewsCountAndShowReviews(colName, colValue);
	showIncompleteSurvey(colName, colValue);
}

function showRegionAdminFlow(newProfileName, newProfileValue) {
	colName = newProfileName;
	colValue = newProfileValue;

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();

	showProfileDetails(newProfileName, newProfileValue, 30);
	bindSelectButtons();
	if((accountType!="INDIVIDUAL") && (accountType!="FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);

	getReviewsCountAndShowReviews(colName, colValue);
	showIncompleteSurvey(colName, colValue);
}

function showBranchAdminFlow(newProfileName, newProfileValue) {
	colName = newProfileName;
	colValue = newProfileValue;

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").show();
	$("#dsh-grph-srch-survey-div").show();

	showProfileDetails(newProfileName, newProfileValue, 30);
	bindSelectButtons();
	if((accountType!="INDIVIDUAL") && (accountType!="FREE"))
		populateSurveyStatisticsList(newProfileName);
	showSurveyStatistics(newProfileName, newProfileValue);
	showSurveyStatisticsGraphically(newProfileName, newProfileValue);

	getReviewsCountAndShowReviews(colName, colValue);
	showIncompleteSurvey(colName, colValue);
}

function showAgentFlow(newProfileName, newProfileValue) {
	colName = newProfileName;
	colValue = newProfileValue;
	
	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").hide();
	$("#dsh-grph-srch-survey-div").hide();

	showProfileDetails(newProfileName, 0, 30);
	bindSelectButtons();
	showSurveyCount(newProfileName, 0, 30);
	showSurveyStatisticsGraphically(newProfileName, 0);

	getReviewsCountAndShowReviews(newProfileName, 0);
	showIncompleteSurvey(colName, colValue);
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
	});
	$("#graph-sel-list").change(function() {
		$('#dsh-grph-sel-item').val('');
		$('.dsh-res-display').hide();
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
		}
		showSurveyStatistics(columnName, columnValue);
	});
}

function populateSurveyStatisticsList(columnName) {
	$("#region-div").show();
	$("#graph-sel-div").show();
	
	var options = "";
	var optionsForGraph = "";
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
	callAjaxGetWithPayloadData("./fetchdashboardincompletesurveycount.do", function(totalIncompleteReviews) {
		if (totalIncompleteReviews == 0) {
			$("#incomplete-survey-header").html("No incomplete surveys found");
			return;
		}

		callAjaxGetWithPayloadData("./fetchdashboardincompletesurvey.do", function(data) {
			if (startIndexInc == 0) {
				$('#dsh-inc-srvey').html(data);
				$("#dsh-inc-dwnld").show();
			}
			else {
				$('#dsh-inc-srvey').append(data);
			}
			$('#dsh-inc-srvey').perfectScrollbar();
	
			var scrollContainer = document.getElementById('dsh-inc-srvey');
			scrollContainer.onscroll = function() {
				if (scrollContainer.scrollTop === scrollContainer.scrollHeight - scrollContainer.clientHeight) {
					showIncompleteSurvey(colName, colValue);
				}
			};
	
			$('.dash-lp-rt-img').click(function() {
				var agentId = $(this).data("agentid");
				var agentName = $(this).data("agentname");
				var customerEmail = $(this).data("custemail");
				var customerName = $(this).data("custname");
				sendSurveyReminderMail(agentId, agentName, customerEmail, customerName);
			});
			
			startIndexInc += batchSizeInc;
	}, payload, false);}, payload, false);
}

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
				return;
			} else {
				$("#review-desc").html("What people say about " + name.substring(1, name.length - 1));
				$("#dsh-cmp-dwnld").show();
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
			changeRatingPattern($(this).data("rating"), $(this));
		});
		$('.icn-fb').unbind('click');
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
		});
		
		startIndexCmp += batchSizeCmp;
	}, payload, false);
}

$(document).on('scroll', '#dsh-inc-srvey', function() {
	console.log($('.ps-scrollbar-y').css('top'));
});

function showSurveyStatisticsGraphically(columnName, columnValue) {
	var element = document.getElementById("dsh-grph-format");
	var format = element.options[element.selectedIndex].value;
	showSurveyGraph(columnName, columnValue, format);
}

function showSurveyGraph(columnName, columnValue, format) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"reportType" : format
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
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintSurveyGraph() {
	
	if(graphData == undefined)
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
	if (format == 'weekly') {
		type = 'Date';
	} else if (format == 'monthly') {
		type = 'Week Starting';
	} else if (format == 'yearly') {
		type = 'Month';
	}

	if (format != 'yearly') {
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

// Being called from dashboard.jsp on key up event.
function searchBranchRegionOrAgent(searchKeyword, flow) {
	var e = document.getElementById("selection-list");
	if (flow == 'graph')
		e = document.getElementById("graph-sel-list");
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
		}
		else if (flow == 'graph'){
			$('#dsh-grph-srch-res').addClass('dsh-sb-dd');
			$('#dsh-grph-srch-res').html(data);
		}
		$('.dsh-res-display').click(function() {
			if (flow == 'icons'){
				$('#dsh-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-sel-item').val($(this).html());
			}
			else if (flow == 'graph') {
				$('#dsh-grph-srch-res').removeClass('dsh-sb-dd');
				$('#dsh-grph-sel-item').val($(this).html());
			}
			
			var value = $(this).data('attr');
			if (searchColumn == "regionName") {
				columnName = "regionId";
			} else if (searchColumn == "branchName") {
				columnName = "branchId";
			} else if (searchColumn == "displayName") {
				columnName = "agentId";
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

function sendSurveyReminderMail(agentId, agentName, customerEmail, customerName) {
	var success = false;
	var payload = {
		"agentName" : agentName,
		"customerEmail" : customerEmail,
		"customerName" : customerName,
		"agentId" : agentId
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
				if (imageUrl != '' && imageUrl != undefined && imageUrl != "undefined") {
					$("#dsh-prsn-img").css("background", "url(" + imageUrl + ") no-repeat center");
					$("#dsh-prsn-img").css("background-size", "cover");
				}
				return data.responseJSON;
			}
		},
		error : function() {
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

function updateCurrentProfile(profileId) {
	var url = "./updatecurrentprofile.do?profileId=" + profileId;
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
	$('#dashboard-sel').html($(this).html());
	$('#da-dd-wrapper-profiles').slideToggle(200);
	
	// update selected profile in session
	var newProfileId = $(this).attr('data-profile-id');
	updateCurrentProfile(newProfileId);

	var newProfileMasterId = $(this).attr('data-profile-master-id');
	var newProfileName = $(this).attr('data-column-name');
	var newProfileValue = $(this).attr('data-column-value');
	paintDashboard(newProfileMasterId, newProfileName, newProfileValue);
	
	// updating data
	$('#prof-container').attr('data-profile-id', newProfileId);
	$('#prof-container').attr('data-profile-master-id', newProfileMasterId);
	$('#prof-container').attr('data-column-name', newProfileName);
	$('#prof-container').attr('data-column-value', newProfileValue);
	
	colName = newProfileName;
	colValue = newProfileValue;
});

$(document).click(function(){
	if ($('#da-dd-wrapper-profiles').css('display') == "block") {
		$('#da-dd-wrapper-profiles').toggle();
	}
});
