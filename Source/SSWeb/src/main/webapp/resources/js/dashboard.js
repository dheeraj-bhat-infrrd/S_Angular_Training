// Default maximum value for number of social posts, surveys sent in 30 days.
var maxSocialPosts = 10;
var maxSurveySent = 10;
var startIndex = 0;
var batchSize = 1;
var totalReviews = 0;
var reviewsFetchedSoFar = 0;
var startIndexInc = 0;
var batchSizeInc = 1;
var totalReviewsInc = 0;
var surveyFetchedSoFarInc = 0;
// colName and colValue contains profile level of logged in user and value for
// colName is present in colValue.
var colName;
var colValue;
var searchColumn;

var circle1;
var circle2;
var circle3;
var circle4;

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

$(document)
		.scroll(
				function() {
					if ((window.innerHeight + window.pageYOffset) >= (document.body.offsetHeight)
							&& startIndex < totalReviews) {
						showReviews(colName, colValue);
					}
				});

$("#prnt-dsh-inc-srvey").scroll(
		function() {
			if ($(this)[0].scrollHeight - $(this).scrollTop() === $(this)
					.outerHeight()) {
				alert(1);
			}
		});


function paintDashboard(companyAdmin, regionAdmin, branchAdmin, regionNames,
		regionIds, branchNames, branchIds, agent, accountType) {
	

	var oldConW = $('.container').width();
	var newConW = $('.container').width();
	$(window).resize(function() {
		newConW = $('.container').width();
		if (newConW != oldConW) {
			showSurveyStatisticsGraphically(colName, colValue);
			oldConW = $('.container').width();
		}
	});
	
	circle1 = new ProgressBar.Circle('#dg-img-1', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});

	circle2 = new ProgressBar.Circle('#dg-img-2', {
		color : '#E97F30',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	circle3 = new ProgressBar.Circle('#dg-img-3', {
		color : '#5CC7EF',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});

	circle4 = new ProgressBar.Circle('#dg-img-4', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});

	if (companyAdmin) {
		showCompanyAdminFlow(regionNames, regionIds, branchNames, branchIds,
				accountType);
	} else if (regionAdmin) {
		showRegionAdminFlow(accountType, regionIds[1]);
	} else if (branchAdmin) {
		showBranchAdminFlow(accountType, branchIds[1]);
	} else if (agent) {
		showAgentFlow();
	}

	$('#dsh-inc-dwnld')
			.click(
					function() {
						window.location.href = "./downloaddashboardincompletesurvey.do?columnName="
								+ colName + "&columnValue=" + colValue;
					});

	$('#dsh-cmp-dwnld')
			.click(
					function() {
						window.location.href = "./downloaddashboardcompletesurvey.do?columnName="
								+ colName + "&columnValue=" + colValue;
					});

}

function showCompanyAdminFlow(accountType) {

	showProfileDetails("companyId", 0, 30);
	colName = "companyId";
	colValue = 0;
	getReviewsCountAndShowReviews(colName, colValue);
	showIncompleteSurvey(colName, colValue);

	$("#region-div").hide();
	$("#graph-sel-div").hide();
//	$("#dsh-srch-survey-div").hide();
	bindSelectButtons();

	populateSurveyStatisticsList("companyId");
	showSurveyStatistics("companyId", 0);
	showSurveyStatisticsGraphically("companyId", 0);
}

function showRegionAdminFlow(accountType, regionId) {

	showProfileDetails("regionId", regionId, 30);
	colName = "regionId";
	colValue = regionId;
	getReviewsCountAndShowReviews(colName, colValue);
	showIncompleteSurvey(colName, colValue);

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").hide();
	bindSelectButtons();

	populateSurveyStatisticsList("regionId");
	showSurveyStatistics("regionId", regionId);
	showSurveyStatisticsGraphically("regionId", regionId);
}

function showBranchAdminFlow(accountType, branchId) {

	showProfileDetails("branchId", branchId, 30);
	colName = "branchId";
	colValue = branchId;
	getReviewsCountAndShowReviews(colName, colValue);
	showIncompleteSurvey(colName, colValue);

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").hide();
	bindSelectButtons();

	populateSurveyStatisticsList("branchId");
	showSurveyStatistics("branchId", branchId);
	showSurveyStatisticsGraphically("branchId", branchId);
}

function showAgentFlow() {

	colName = "agentId";
	colValue = 0;
	showProfileDetails("agentId", 0, 30);
	getReviewsCountAndShowReviews("agentId", 0);
	showIncompleteSurvey(colName, colValue);

	$("#region-div").hide();
	$("#graph-sel-div").hide();
	$("#dsh-srch-survey-div").hide();

	showSurveyCount("agentId", 0, 30);
	showSurveyStatisticsGraphically("agentId", 0);
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
	if (columnName == "companyId" || columnName == "regionId"
			|| columnName == "branchId") {
		options += "<option value=displayName>Individual</option>";
		optionsForGraph += "<option value=displayName>Individual</option>";
	}
	$("#selection-list").html(options);
	$("#graph-sel-list").html(options);
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
		},
		error : function() {

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
		else if (flow == 'graph'){
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
		if(flow=='icons')
			showSurveyStatistics(columnName, value);
		else if(flow=='graph')
			showSurveyStatisticsGraphically(columnName, value);
		$('.dsh-res-display').hide();
	});
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

function showSurveyStatistics(columnName, columnValue) {
	var element = document.getElementById("survey-count-days");
	var numberOfDays = element.options[element.selectedIndex].value;
	showSurveyCount(columnName, columnValue, numberOfDays);
}

function showSurveyStatisticsGraphically(columnName, columnValue) {
	var element = document.getElementById("dsh-grph-format");
	var format = element.options[element.selectedIndex].value;
	showSurveyGraph(columnName, columnValue, format);
}

function showSurveyCount(columnName, columnValue, numberOfDays) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"numberOfDays" : numberOfDays
	};
	$.ajax({
		url : "./surveycount.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintSurveyStatistics(data);
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintSurveyStatistics(data) {

	var sentSurveyDiv = "";
	var clickedSurveyDiv = "";
	var completedSurveyDiv = "";
	var socialPostsDiv = "";

	var sentSurveyCount = parseInt(data.responseJSON.allSurveySent);
	if (sentSurveyCount > 0) {
		for (var i = 0; i < 20; i++) {
			sentSurveyDiv += "<div class='float-left stat-icn-img stat-icn-img-green'></div>";
		}
	}

	sentSurveyDiv += " <div id='survey-sent' class='float-left stat-icn-txt-rt'></div>";
	$('#all-surv-icn').html(sentSurveyDiv);
	$("#survey-sent").html(sentSurveyCount);
	var clicked = 0;
	clicked = parseInt(data.responseJSON.clickedSurvey);
	if (isNaN(clicked)){
		clicked = 0;
	}
	var icnForClicked = prcntForClicked * 20 / sentSurveyCount;
	icnForClicked = Math.round(icnForClicked);
	for (var i = 0; i < parseInt(icnForClicked); i++) {
		clickedSurveyDiv += "<div class='float-left stat-icn-img stat-icn-img-blue'></div>";
	}
	clickedSurveyDiv += "<div id='survey-clicked' class='float-left stat-icn-txt-rt'></div>";
	$("#clicked-surv-icn").html(clickedSurveyDiv);
	$("#survey-clicked").html(clicked);

	var completed = parseInt(data.responseJSON.completedSurvey);
	if (isNaN(completed))
		completed = 0;
	var icnForCompleted = prcntForCompleted * 20 / sentSurveyCount;
	icnForCompleted = Math.round(icnForCompleted);
	for (var i = 0; i < parseInt(icnForCompleted); i++) {
		completedSurveyDiv += '<div class="float-left stat-icn-img stat-icn-img-yellow"></div>';
	}
	completedSurveyDiv += "<div id='survey-completed' class='float-left stat-icn-txt-rt'></div>";
	$("#completed-surv-icn").html(completedSurveyDiv);
	$("#survey-completed").html(completed);
	
	var socialPosts = 0;
	socialPosts = parseInt(data.responseJSON.socialPosts);
	if (isNaN(socialPosts)){
		socialPosts = 0;
	}
	var icnForSocialPosts = prcntForSocialPosts * 20 / sentSurveyCount;
	icnForSocialPosts = Math.round(icnForSocialPosts);
	for (var i = 0; i < parseInt(icnForSocialPosts); i++) {
		socialPostsDiv += '<div class="float-left stat-icn-img stat-icn-img-red"></div>';
	}
	socialPostsDiv += '<div id="social-posts" class="float-left stat-icn-txt-rt"></div>';
	$("#social-post-icn").html(socialPostsDiv);
	$("#social-posts").html(socialPosts);
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
	var allTimeslots=[];
	var timeslots=[];
	var clickedSurveys=[];
	var sentSurveys=[];
	var socialPosts=[];
	var completedSurveys=[];
	var index = 0;
	
	$.each(graphData.clicked, function(key, value){
		allTimeslots[index] = key;
		clickedSurveys[index] = value;
		index++;
	});
	index = 0;
	if(timeslots.length>allTimeslots.length){
		allTimeslots=timeslots;
		timeslots=[];
	}
	$.each(graphData.sent, function(key, value){
		timeslots[index] = key;
		sentSurveys[index] = value;
		index++;
	});
	index = 0;
	if(timeslots.length>allTimeslots.length){
		allTimeslots=timeslots;
		timeslots=[];
	}
	$.each(graphData.complete, function(key, value){
		timeslots[index] = key;
		completedSurveys[index] = value;
		index++;
	});
	index = 0;
	if(timeslots.length>allTimeslots.length){
		allTimeslots=timeslots;
		timeslots=[];
	}
	$.each(graphData.socialposts, function(key, value){
		timeslots[index] = key;
		socialPosts[index] = value;
		index++;
	});
	if(timeslots.length>allTimeslots.length){
		allTimeslots=timeslots;
		timeslots=[];
	}
	var element = document.getElementById("dsh-grph-format");
	var format = element.options[element.selectedIndex].value;
	var type = '';
	if(format=='weekly'){
		type = 'Date';
	}
	else if(format=='monthly'){
		type = 'Week Starting';
	}
	else  if(format=='yearly'){
		type = 'Month';
	}
	
    var internalData = [];
    var nestedInternalData = [];
    nestedInternalData.push(type,'No. of surveys sent', 'No. of surveys clicked','No. of surveys completed', 'No. of social posts');
    internalData.push(nestedInternalData);
    for(var itr=0;itr<allTimeslots.length;itr++){
    	nestedInternalData = [];
    	var sentSurvey;
    	var clickedSurvey;
    	var completedSurvey;
    	var socialPost;
    	if(isNaN(parseInt(sentSurveys[itr]))){
    		sentSurvey = 0;
    	}else{
    		sentSurvey = parseInt(sentSurveys[itr]);
    	}
    	if(isNaN(parseInt(clickedSurveys[itr]))){
    		clickedSurvey = 0;
    	}else{
    		clickedSurvey = parseInt(clickedSurveys[itr]);
    	}
    	if(isNaN(parseInt(completedSurveys[itr]))){
    		completedSurvey = 0;
    	}else{
    		completedSurvey = parseInt(completedSurveys[itr]);
    	}
    	if(isNaN(parseInt(socialPosts[itr]))){
    		socialPost = 0;
    	}else{
    		socialPost = parseInt(socialPosts[itr]);
    	}
    	nestedInternalData.push(allTimeslots[itr],sentSurvey,clickedSurvey,completedSurvey,socialPost);
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
    
  	var chart = new google.visualization.LineChart(document
		.getElementById('util-gph-item'));
  	
  	chart.draw(data, options);
}

function showProfileDetails(columnName, columnValue, numberOfDays) {
	var success = false;
	var payload = {
		"columnName" : columnName,
		"columnValue" : columnValue,
		"numberOfDays" : numberOfDays
	};
	$.ajax({
		url : "./profiledetails.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintProfileDetails(data);
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function paintProfileDetails(data) {
	$("#badges").removeClass(".dsg-g-rbn-1");
	$("#badges").removeClass(".dsg-g-rbn-2");
	$("#badges").removeClass(".dsg-g-rbn-3");
	$("#name").html(data.responseJSON.name);
	if (data.responseJSON.title != undefined)
		$("#designation").html(data.responseJSON.title);
	if (data.responseJSON.company != undefined)
		$("#company").html(data.responseJSON.company);
	$("#socl-post").html(data.responseJSON.socialPosts);
	circle1.animate(parseInt(data.responseJSON.socialPosts) / maxSocialPosts);
	$("#srv-snt-cnt").html(data.responseJSON.surveyCount);
	circle2.animate(parseInt(data.responseJSON.surveyCount) / maxSurveySent);
	$("#srv-scr").html(data.responseJSON.socialScore + "/5");
	circle3.animate(parseInt(data.responseJSON.socialScore) / 5);
	var profileCompleted = parseInt(data.responseJSON.profileCompleteness);
	circle4.animate(profileCompleted / 100);

	$("#pro-cmplt-stars").find('.dsh-star-item').removeClass('sq-full-star');
	var starVal = profileCompleted * 5 / 100;
	$("#pro-cmplt-stars").find('.dsh-star-item').each(function(index) {
		if (index < starVal) {
			$(this).removeClass('no-star');
			$(this).addClass('sq-full-star');
		}
	});

	$("#profile-completed").html(starVal + "/5");
	var badges = parseInt(data.responseJSON.badges);
	if (badges == 1)
		$("#badges").addClass("dsg-g-rbn-1");
	else if (badges == 2)
		$("#badges").addClass("dsg-g-rbn-2");
	else if (badges == 3)
		$("#badges").addClass("dsg-g-rbn-3");
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
		"startIndex" : startIndex,
		"batchSize" : batchSize
	};
	$.ajax({
		url : "./fetchdashboardreviews.do",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintReviews(data.responseJSON);
				startIndex += batchSize;
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
	$
			.each(
					result,
					function(i, feedback) {
						divToPopulate += '<div class="ppl-review-item">'
								+ '<div class="ppl-header-wrapper clearfix"><div class="float-left ppl-header-left">'
								+ '<div class="ppl-head-1">'
								+ feedback.customerFirstName
								+ ' '
								+ feedback.customerLastName
								+ '</div>'
								+ '<div class="ppl-head-2">'
								+ feedback.modifiedOn
								+ '</div></div><div class="float-right ppl-header-right">'
								+ '<div class="st-rating-wrapper maring-0 clearfix review-ratings" data-rating="'
								+ feedback.score
								+ '"><div class="rating-star icn-full-star"></div>'
								+ '<div class="rating-star icn-full-star"></div><div class="rating-star icn-half-star"></div>'
								+ '<div class="rating-star icn-no-star"></div><div class="rating-star icn-no-star"></div></div></div></div>'
								+ '<div class="ppl-content">'
								+ feedback.review
								+ '</div><div class="ppl-share-wrapper clearfix">'
								+ '<div class="float-left blue-text ppl-share-shr-txt">Share</div>'
								+ '<div class="float-left icn-share icn-plus-open" style="display: block;"></div>'
								+ '<div class="float-left clearfix ppl-share-social hide" style="display: none;"><div class="float-left ppl-share-icns icn-fb">'
								+ '</div><div class="float-left ppl-share-icns icn-twit"></div><div class="float-left ppl-share-icns icn-lin"></div>'
								+ '<div class="float-left ppl-share-icns icn-yelp"></div></div>'
								+ '<div class="float-left icn-share icn-remove icn-rem-size hide" style="display: none;"></div></div></div>';
					});
	if (startIndex == 0)
		$("#review-details").html(divToPopulate);
	else
		$("#review-details").append(divToPopulate);
	$(".review-ratings").each(function() {
		changeRatingPattern($(this).data("rating"), $(this));
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
				if (totalReviews == 0)
					$("#review-desc").html(
							"No review found for " + data.responseText);
				else
					$("#review-desc").html(
							"What people say about "
									+ data.responseText.substring(1,
											data.responseText.length - 1));
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
	$.each(result, function(i, survey) {
		divToPopulate += '<div class="dash-lp-item clearfix">'
				+ '<div class="float-left dash-lp-txt">' + survey.customerFirstName+" "+survey.customerLastName
				+ ' <span>' + survey.modifiedOn + '</span></div>'
				+ '<div data-custname=' + survey.customerFirstName+" "+survey.customerLastName
				+ ' data-agentname=' + survey.agentName + ' data-custemail='
				+ survey.customerEmail
				+ ' class="float-right dash-lp-rt-img"></div></div>';
	});
	$("#dsh-inc-srvey").html(divToPopulate);
	$('#dsh-inc-srvey').perfectScrollbar();

	$('.dash-lp-rt-img').click(function() {
		var agentName = $(this).data("agentname");
		var customerEmail = $(this).data("custemail");
		var customerName = $(this).data("custname");
		sendSurveyReminderMail(agentName, customerEmail, customerName);
	});
}

function sendSurveyReminderMail(agentName, customerEmail, customerName) {
	var success = false;
	var payload = {
		"agentName" : agentName,
		"customerEmail" : customerEmail,
		"customerName" : customerName
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
