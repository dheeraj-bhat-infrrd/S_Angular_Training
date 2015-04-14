<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="float-left stats-right">
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.surveyssent.key" /></div>
		<div id="all-surv-icn" class="float-left stat-icns-item clearfix"></div>
	</div>
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.surveysclicked.key" /></div>
		<div id="clicked-surv-icn" class="float-left stat-icns-item clearfix"></div>
	</div>
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.surveyscompleted.key" /></div>
		<div id="completed-surv-icn" class="float-left stat-icns-item clearfix"></div>
	</div>
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.socialposts.key" /></div>
		<div id="social-post-icn" class="float-left stat-icns-item clearfix"></div>
	</div>
</div>

<script>
$(document).ready(function() {
	// Surveys sent
	var sentSurveyCount = parseInt("${allSurveySent}");
	var sentSurveyDiv = "";
	if (sentSurveyCount > 0) {
		for (var i = 0; i < 20; i++) {
			sentSurveyDiv += "<div class='float-left stat-icn-img stat-icn-img-green'></div>";
		}
	}
	sentSurveyDiv += " <div id='survey-sent' class='float-left stat-icn-txt-rt'></div>";
	$('#all-surv-icn').html(sentSurveyDiv);
	$("#survey-sent").html(sentSurveyCount);
	
	// Surveys clicked
	var clicked = parseInt("${clickedSurvey}");
	if (isNaN(clicked)) {
		clicked = 0;
	}
	var icnForClicked = clicked * 20 / sentSurveyCount;
	icnForClicked = Math.round(icnForClicked);
	var clickedSurveyDiv = "";
	for (var i = 0; i < parseInt(icnForClicked); i++) {
		clickedSurveyDiv += "<div class='float-left stat-icn-img stat-icn-img-blue'></div>";
	}
	clickedSurveyDiv += "<div id='survey-clicked' class='float-left stat-icn-txt-rt'></div>";
	$("#clicked-surv-icn").html(clickedSurveyDiv);
	$("#survey-clicked").html(clicked);

	// Surveys completed
	var completed = parseInt("${completedSurvey}");
	if (isNaN(completed)) {
		completed = 0;
	}
	var icnForCompleted = completed * 20 / sentSurveyCount;
	icnForCompleted = Math.round(icnForCompleted);
	var completedSurveyDiv = "";
	for (var i = 0; i < parseInt(icnForCompleted); i++) {
		completedSurveyDiv += '<div class="float-left stat-icn-img stat-icn-img-yellow"></div>';
	}
	completedSurveyDiv += "<div id='survey-completed' class='float-left stat-icn-txt-rt'></div>";
	$("#completed-surv-icn").html(completedSurveyDiv);
	$("#survey-completed").html(completed);

	// Social Posts
	var socialPosts = parseInt("${socialPosts}");
	if (isNaN(socialPosts)) {
		socialPosts = 0;
	}
	var icnForSocialPosts = socialPosts * 20 / sentSurveyCount;
	icnForSocialPosts = Math.round(icnForSocialPosts);
	var socialPostsDiv = "";
	for (var i = 0; i < parseInt(icnForSocialPosts); i++) {
		socialPostsDiv += '<div class="float-left stat-icn-img stat-icn-img-red"></div>';
	}
	socialPostsDiv += '<div id="social-posts" class="float-left stat-icn-txt-rt"></div>';
	$("#social-post-icn").html(socialPostsDiv);
	$("#social-posts").html(socialPosts);
});
</script>