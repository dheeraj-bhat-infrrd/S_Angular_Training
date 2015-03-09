$('.icn-plus-open').click(function() {
	$(this).hide();
	$(this).parent().find('.ppl-share-social,.icn-remove').show();
});

$('.icn-remove').click(function() {
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

function paintDashboard(companyAdmin, regionAdmin, branchAdmin, regionNames,
		regionIds, branchNames, branchIds, agent, accountType) {
	if (companyAdmin) {
		showCompanyAdminFlow(regionNames, regionIds, branchNames, branchIds,
				accountType);
	} else if (regionAdmin) {
		showRegionAdminFlow(user);
	} else if (branchAdmin) {
		showBranchAdminFlow(user);
	} else if (agent) {
		showAgentFlow(user);
	}
}

function showCompanyAdminFlow(regionNames, regionIds, branchNames, branchIds,
		accountType) {

	showProfileDetails("companyId", 0, 30);
	
	$("#region-div").hide();
	$("#branch-div").hide();

	var branchNameArr = branchNames.substring(1, branchNames.length - 1).split(
			",");
	var branchIdArr = branchIds.substring(1, branchIds.length - 1).split(",");

	if (regionNames != undefined) {
		var regionNameArr = regionNames.substring(1, regionNames.length - 1)
				.split(",");
		var regionIdArr = regionIds.substring(1, regionIds.length - 1).split(
				",");
	}
	if (accountType == "ENTERPRISE") {
		populateRegionList(regionIdArr, regionNameArr);
	} else if (accountType == "COMPANY") {
		populateBranchList(branchIdArr, branchNameArr);
	}
	bindSelectButtons();
	
}

function populateRegionList(regionIdArr, regionNameArr) {
	$("#region-div").show();
	$("#branch-div").show();
	var regionOptions = "";
	regionOptions += "<option value=0>All Regions</option>";
	for ( var key in regionNameArr) {
		regionOptions += "<option value=" + regionIdArr[key] + ">"
				+ regionNameArr[key] + "</option>";
	}
	$("#regions-list").html(regionOptions);
	populateBranchList(undefined, undefined);
}

function populateBranchList(branchIdArr, branchNameArr) {
	$("#branch-div").show();
	var branchOptions = "";
	branchOptions += "<option value=0>All Branches</option>";
	for ( var key in branchNameArr) {
		branchOptions += "<option value=" + branchIdArr[key] + ">"
				+ branchNameArr[key] + "</option>";
	}
	$("#branch-list").html(branchOptions);
	showSurveyStatistics();
}

function bindSelectButtons() {
	$("#branch-list").change(function() {
		showSurveyStatistics();
	});

	$("#regions-list").change(function() {
		var success = false;
		var e = document.getElementById("regions-list");
		var regionId = e.options[e.selectedIndex].value;
		var branchIdArr = [];
		var branchNameArr = [];
		var payload = {
			"regionId" : regionId
		};
		$.ajax({
			url : "./fetchbranchesforregion.do",
			type : "GET",
			dataType : "JSON",
			data : payload,
			success : function(data) {
				if (data.errCode == undefined)
					success = true;
			},
			complete : function(data) {
				if (success) {
					var branches = data.responseJSON;
					for (var i = 0; i < branches.length; i++) {
						var branch = branches[i];
						branchIdArr.push(branch.branchId);
						branchNameArr.push(branch.branchName);
						populateBranchList(branchIdArr, branchNameArr);
					}
				}
			},
			error : function(e) {
				console.error("error : " + e.responseText);
				$('#overlay-toast').html(e.responseText);
				showToast();
			}
		});
	});

	$("#survey-count-days").change(function() {
		showSurveyStatistics();
	});
}

function showSurveyStatistics() {
	var e = document.getElementById("branch-list");
	var branchId = e.options[e.selectedIndex].value;
	var element = document.getElementById("survey-count-days");
	var numberOfDays = element.options[element.selectedIndex].value;
	if (branchId == 0) {
		e = document.getElementById("regions-list");
		var regionId = e.options[e.selectedIndex].value;
		if (regionId == 0) {
			showSurveyCount("companyId", 0, numberOfDays);
			return;
		}
		showSurveyCount("regionId", regionId, numberOfDays);
		return;
	}
	showSurveyCount("branchId", branchId, numberOfDays);
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
	for (var i = 0; i < 20; i++) {
		sentSurveyDiv += "<div class='float-left stat-icn-img stat-icn-img-green'></div>";
	}
	sentSurveyDiv += " <div id='survey-sent' class='float-left stat-icn-txt-rt'></div>";
	$("#survey-sent").html(sentSurveyCount);

	var prcntForClicked = parseInt(data.responseJSON.clickedSurvey) * 100
			/ sentSurveyCount;
	if (isNaN(prcntForClicked))
		prcntForClicked = 0;
	var icnForClicked = prcntForClicked * 20 / 100;
	for (var i = 0; i < parseInt(icnForClicked); i++) {
		clickedSurveyDiv += "<div class='float-left stat-icn-img stat-icn-img-blue'></div>";
	}
	clickedSurveyDiv += "<div id='survey-clicked' class='float-left stat-icn-txt-rt'></div>";
	$("#clicked-surv-icn").html(clickedSurveyDiv);
	$("#survey-clicked").html(prcntForClicked + "%");

	var prcntForCompleted = parseInt(data.responseJSON.completedSurvey) * 100
			/ sentSurveyCount;
	if (isNaN(prcntForCompleted))
		prcntForCompleted = 0;
	var icnForCompleted = prcntForCompleted * 20 / 100;
	for (var i = 0; i < parseInt(icnForCompleted); i++) {
		completedSurveyDiv += '<div class="float-left stat-icn-img stat-icn-img-yellow"></div>';
	}
	completedSurveyDiv += "<div id='survey-completed' class='float-left stat-icn-txt-rt'></div>";
	$("#completed-surv-icn").html(completedSurveyDiv);
	$("#survey-completed").html(prcntForCompleted + "%");

	var prcntForSocialPosts = parseInt(data.responseJSON.socialPosts) * 100
			/ sentSurveyCount;
	if (isNaN(prcntForSocialPosts))
		prcntForSocialPosts = 0;
	var icnForSocialPosts = prcntForSocialPosts * 20 / 100;
	for (var i = 0; i < parseInt(icnForSocialPosts); i++) {
		socialPostsDiv += '<div class="float-left stat-icn-img stat-icn-img-red"></div>';
	}
	socialPostsDiv += '<div id="social-posts" class="float-left stat-icn-txt-rt"></div>';
	$("#social-post-icn").html(socialPostsDiv);
	$("#social-posts").html(prcntForSocialPosts + "%");
}

function showProfileDetails(columnName, columnValue, numberOfDays){
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

function paintProfileDetails(data){
	$("#socl-post").html(data.responseJSON.socialPosts);
	$("#srv-snt-cnt").html(data.responseJSON.surveyCount);
	$("#srv-scr").html(data.responseJSON.socialScore+"/5");
}