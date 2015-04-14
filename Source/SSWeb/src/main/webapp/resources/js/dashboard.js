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

// colName and colValue contains profile level of logged in user and value for
// colName is present in colValue.
var colName;
var colValue;
var searchColumn;

$(document).on('click', '.icn-plus-open', function() {
	$(this).hide();
	$(this).parent().find('.ppl-share-social,.icn-remove').show();
});

$(document).on('click', '.icn-remove', function() {
	$(this).hide();
	$(this).parent().find('.ppl-share-social').hide();
	$(this).parent().find('.icn-plus-open').show();
});

$('#hr-txt2').click(function(e) {
	e.stopPropagation();
	$('#hr-dd-wrapper').slideToggle(200);
});

$('.hr-dd-item').click(function(e) {
	e.stopPropagation();
});

$('body').click(function() {
	$('#hr-dd-wrapper').slideUp(200);
});

$(document).scroll(function() {
	if ((window.innerHeight + window.pageYOffset) >= (document.body.offsetHeight) && startIndexCmp < totalReviews) {
		showReviews(colName, colValue);
	}
});

function paintDashboard(profileMasterId, newProfileName, newProfileValue) {
	startIndexCmp = 0;
	batchSizeCmp = 1;
	totalReviews = 0;
	reviewsFetchedSoFar = 0;
	startIndexInc = 0;
	batchSizeInc = 6;
	totalReviewsInc = 0;
	surveyFetchedSoFarInc = 0;
	showDisplayPic();

	var oldConW = $('.container').width();
	var newConW = $('.container').width();
	$(window).resize(function() {
		newConW = $('.container').width();
		if (newConW != oldConW) {
			showSurveyStatisticsGraphically(colName, colValue);
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
}

function showCompanyAdminFlow(newProfileName, newProfileValue) {
	colName = newProfileName;
	colValue = newProfileValue;

	$("#region-div").hide();
	$("#graph-sel-div").hide();

	showProfileDetails(newProfileName, 0, 30);
	bindSelectButtons();
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

	showProfileDetails(newProfileName, newProfileValue, 30);
	bindSelectButtons();
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
	$("#dsh-srch-survey-div").hide();
	$("#dsh-grph-srch-survey-div").hide();

	showProfileDetails(newProfileName, newProfileValue, 30);
	bindSelectButtons();
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
	$("#selection-list").change(function() {
		$('#dsh-sel-item').val('');
		$('.dsh-res-display').hide();
	});
	$("#graph-sel-list").change(function() {
		$('#dsh-grph-sel-item').val('');
		$('.dsh-res-display').hide();
	});
	$("#dsh-grph-format").change(function() {
		showSurveyStatisticsGraphically(colName, colValue);
	});
	$("#survey-count-days").change(function() {
		showSurveyStatistics(colName, colValue);
	});
}

function populateSurveyStatisticsList(columnName) {
	$("#region-div").show();
	$("#graph-sel-div").show();
	var options = "";
	var optionsForGraph = "";
	if (columnName == "companyId") {
		options += "<option value=regionName>Region</option>";
		optionsForGraph += "<option value=regionName>Region</option>";
	}
	if (columnName == "companyId" || columnName == "regionId") {
		options += "<option value=branchName>Branch</option>";
		optionsForGraph += "<option value=branchName>Branch</option>";
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
				paintSurveyGraph(data.responseJSON);
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintSurveyGraph(graphData) {
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
	var success = false;
	$.ajax({
		url : "./findregionbranchorindividual.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintList(searchColumn, data.responseJSON, flow);
			}
		}
	});
}

function paintList(searchColumn, results, flow) {
	var divToPopulate = "";
	$.each(results, function(i, result) {
		if (searchColumn == "regionName") {
			divToPopulate += '<div class="dsh-res-display" data-attr="'
					+ result.regionId + '">' + result.regionName + '</div>';
		} else if (searchColumn == "branchName") {
			divToPopulate += '<div class="dsh-res-display" data-attr="'
					+ result.branchId + '">' + result.branchName + '</div>';
		} else if (searchColumn == "displayName") {
			divToPopulate += '<div class="dsh-res-display" data-attr="'
					+ result.userId + '">' + result.displayName + '</div>';
		}
	});
	
	if (flow == 'icons')
		$('#dsh-srch-res').html(divToPopulate);
	else if (flow == 'graph')
		$('#dsh-grph-srch-res').html(divToPopulate);

	$('.dsh-res-display').click(function() {
		if (flow == 'icons')
			$('#dsh-sel-item').val($(this).html());
		else if (flow == 'graph') {
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
		
		if (flow == 'icons')
			showSurveyStatistics(columnName, value);
		else if (flow == 'graph')
			showSurveyStatisticsGraphically(columnName, value);
		$('.dsh-res-display').hide();
	});
}

function getReviewsCountAndShowReviews(columnName, columnValue) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue
	};
	$.ajax({
		url : "./fetchdashboardreviewCount.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				totalReviews = data.responseJSON;
				paintName(columnName, columnValue);
				if (parseInt(totalReviews) > 0) {
					showReviews(columnName, columnValue);
				}
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function showReviews(columnName, columnValue) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"startIndex" : startIndexCmp,
		"batchSize" : batchSizeCmp
	};
	$.ajax({
		url : "./fetchdashboardreviews.do",
		type : "GET",
		dataType : "JSON",
		async : false,
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintReviews(data.responseJSON);
				startIndexCmp += batchSizeCmp;
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintReviews(result) {
	var divToPopulate = "";
	$.each(result, function(i, feedback) {
		divToPopulate += '<div data-fname="'
			+ feedback.customerFirstName
			+ '" '
			+ ' data-lname="'
			+ feedback.customerLastName
			+ '" data-agentname='
			+ agentName
			+ ' data-review="'
			+ feedback.review
			+ '" class="ppl-review-item">'
			+ ' <div class="ppl-header-wrapper clearfix"><div class="float-left ppl-header-left">'
			+ ' <div class="ppl-head-1">'
			+ feedback.customerFirstName
			+ ' '
			+ feedback.customerLastName
			+ ' </div>'
			+ ' <div class="ppl-head-2">'
			+ feedback.modifiedOn
			+ ' </div></div><div class="float-right ppl-header-right">'
			+ ' <div class="st-rating-wrapper maring-0 clearfix review-ratings" data-rating='
			+ feedback.score
			+ '><div class="rating-star icn-full-star"></div>'
			+ ' <div class="rating-star icn-full-star"></div><div class="rating-star icn-half-star"></div>'
			+ ' <div class="rating-star icn-no-star"></div><div class="rating-star icn-no-star"></div></div></div></div>'
			+ ' <div class="ppl-content">'
			+ feedback.review
			+ ' </div><div class="ppl-share-wrapper clearfix">'
			+ ' <div class="float-left blue-text ppl-share-shr-txt">Share</div>'
			+ ' <div class="float-left icn-share icn-plus-open" style="display: block;"></div>'
			+ ' <div class="float-left clearfix ppl-share-social hide" style="display: none;"><div class="float-left ppl-share-icns icn-fb">'
			+ ' </div><div class="float-left ppl-share-icns icn-twit"></div><div class="float-left ppl-share-icns icn-lin"></div>'
			+ ' <div class="float-left ppl-share-icns icn-yelp"></div></div>'
			+ ' <div class="float-left icn-share icn-remove icn-rem-size hide" style="display: none;"></div></div></div>';
	});
	if (startIndexCmp == 0)
		$("#review-details").html(divToPopulate);
	else
		$("#review-details").append(divToPopulate);
	
	$(".review-ratings").each(function() {
		changeRatingPattern($(this).data("rating"), $(this));
	});
	
	$(".icn-fb").click(function() {
		var firstName = $(this).parent().parent().parent().attr('data-firstname');
		var lastName = $(this).parent().parent().parent().attr('data-lastname');
		var agentName = $(this).parent().parent().parent().attr('data-agentname');
		var review = $(this).parent().parent().parent().attr('data-review');
		var score = $(this).parent().parent().parent().attr('data-score');
		shareOnFacebook(firstName, lastName, agentName, review, score);
	});
	
	$(".icn-twit").click(function() {
		var firstName = $(this).parent().parent().parent().attr('data-firstname');
		var lastName = $(this).parent().parent().parent().attr('data-lastname');
		var agentName = $(this).parent().parent().parent().attr('data-agentname');
		var review = $(this).parent().parent().parent().attr('data-review');
		var score = $(this).parent().parent().parent().attr('data-score');
		shareOnTwitter(firstName, lastName, agentName, review, score);
	});
	
	$(".icn-lin").click(function() {
		var firstName = $(this).parent().parent().parent().attr('data-firstname');
		var lastName = $(this).parent().parent().parent().attr('data-lastname');
		var agentName = $(this).parent().parent().parent().attr('data-agentname');
		var review = $(this).parent().parent().parent().attr('data-review');
		var score = $(this).parent().parent().parent().attr('data-score');
		shareOnLinkedin(firstName, lastName, agentName, review, score);
	});
	
	$(".icn-yelp").click(function() {
		var firstName = $(this).parent().parent().parent().attr('data-firstname');
		var lastName = $(this).parent().parent().parent().attr('data-lastname');
		var agentName = $(this).parent().parent().parent().attr('data-agentname');
		var review = $(this).parent().parent().parent().attr('data-review');
		var score = $(this).parent().parent().parent().attr('data-score');
		shareOnYelp(firstName, lastName, agentName, review, score);
	});
}

function paintName(columnName, columnValue) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue
	};
	$.ajax({
		url : "./fetchName.do",
		type : "GET",
		dataType : "html",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				if (totalReviews == 0){
					$("#review-desc").html("No review found for " + data.responseText);
					return;
				}
				else{
					$("#review-desc").html("What people say about "+ data.responseText.substring(1,data.responseText.length - 1));
					$("#dsh-cmp-dwnld").show();
				}				
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function showIncompleteSurvey(columnName, columnValue) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"startIndex" : startIndexInc,
		"batchSize" : batchSizeInc
	};
	$.ajax({
		url : "./fetchdashboardincompletesurvey.do",
		type : "GET",
		async : false,
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintIncompleteSurvey(data.responseJSON);
				startIndexInc += batchSizeInc;
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintIncompleteSurvey(result) {
	var divToPopulate = "";
	$.each(result,function(i, survey) {
		divToPopulate += '<div class="dash-lp-item clearfix">'
				+ '<div class="float-left dash-lp-txt">'
				+ survey.customerFirstName
				+ " "
				+ survey.customerLastName
				+ ' <span>'
				+ survey.modifiedOn
				+ '</span></div>'
				+ '<div data-custname='
				+ survey.customerFirstName
				+ ' '
				+ survey.customerLastName
				+ ' data-agentid='
				+ survey.agentId
				+ ' data-agentname='
				+ survey.agentName
				+ ' data-custemail='
				+ survey.customerEmail
				+ ' class="float-right dash-lp-rt-img cursor-pointer"></div></div>';
		});
	if (startIndexInc == 0){
		if(divToPopulate == ""){
			$("#incomplete-survey-header").html("No incomplete surveys found");
			return;
		}
		$("#dsh-inc-srvey").html(divToPopulate);
		$("#dsh-inc-dwnld").show();
	}
	else{
		$("#dsh-inc-srvey").append(divToPopulate);
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
		sendSurveyReminderMail(agentId, agentName, customerEmail,
				customerName);
	});
}

$(document).on('scroll', '#dsh-inc-srvey', function() {
	console.log($('.ps-scrollbar-y').css('top'));
});

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
				$('#overlay-toast').html(
						"Reminder Mail sent successfully to " + customerName);
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function changeRatingPattern(rating, ratingParent) {
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
}

function showDisplayPic() {
	var success = false;
	$.ajax({
		url : "./getdisplaypiclocation.do",
		type : "GET",
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				console.log("Image location : " + data.responseJSON);
				var imageUrl = data.responseJSON;
				if (imageUrl != '' || imageUrl != undefined) {
					$("#dsh-prsn-img").css("background", "url(" + imageUrl + ") no-repeat center");
					$("#dsh-prsn-img").css("background-size", "cover");
				}
				return data.responseJSON;
			}
		},
		error : function() {
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

// Dashboard View as
$('#dashboard-sel').click(function(){
	$('#da-dd-wrapper-profiles').slideToggle(200);
});

$('.da-dd-item').click(function(){
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
});
