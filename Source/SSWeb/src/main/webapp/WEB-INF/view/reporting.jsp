<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
.nav-tabs>li>a {
    background-color: #fff;
    height: 40px;
    width: 150px;
    text-align: center;
    color: #777;
    border-radius: 3px;
    font-size: 13px;
    font-weight: bold !important;
    cursor: pointer;
    border: 1px solid #D3D3D7;
    box-shadow: inset 0 0 0 0 rgba(0, 0, 0, .4), -2px -3px 5px -2px rgba(0, 0, 0, .4);
    border-bottom-color: #2f69aa;
}

.nav-tabs>li.active>a, .nav-tabs>li.active>a:hover, .nav-tabs>li.active>a:focus {
    background: #2f69aa;
    color: #fff;
    box-shadow: inset 0 0 0 0 rgba(0, 0, 0, .4), -2px -3px 5px -2px rgba(0, 0, 0, .4);
    border-bottom-color: #2f69aa;
    }
</style>

<c:set value="${cannonicalusersettings.companySettings.iden}" var="companyId"></c:set>
<c:set value="${userId}" var="userId"></c:set>

<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>

<div class="overlay-loader hide"></div>

<div id="rep-prof-container" data-profile-master-id="${profileMasterId}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="hide dash-top-info dash-prof-wrapper pos-relative dash-size" >
			<div id="top-dash" class="hide" ></div>
</div>
<div>
	<c:choose>
		<c:when test="${profilemasterid == 1}">
			<input type="hidden" id="prof-company-id"
				value="${profileSettings.iden}">
			<input type="hidden" id="company-profile-name"
				value="${profileSettings.profileName}">
		</c:when>
		<c:when test="${profilemasterid == 2}">
			<input type="hidden" id="prof-region-id" value="${entityId}">
		</c:when>
		<c:when test="${profilemasterid == 3}">
			<input type="hidden" id="prof-branch-id" value="${entityId}">
		</c:when>
		<c:when test="${profilemasterid == 4}">
			<input type="hidden" id="prof-agent-id" value="${entityId}">
		</c:when>
	</c:choose>
	<input type="hidden" id="profile-id" value="${profile.userProfileId}" />
	<input type="hidden" id="profile-min-post-score"
		value="${profileSettings.survey_settings.show_survey_above_score}" />
	<input type="hidden" id="rep-real-tech-check" value="${isRealTechOrSSAdmin}" />

</div>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft">
				<spring:message code="label.reporting.key" />
			</div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>

<div class="hm-header-main-wrapper hm-hdr-bord-bot" style="background:#2f69aa">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div id="timeFrame_container">
				<jsp:include page="reporting_timeFrame_dropdown.jsp"></jsp:include>
			</div>
			<c:if test='${lastSuccessfulRun != "" && lastSuccessfulRun != null && lastSuccessfulRun !=undefined}'>
				<div class="float-left last-run-time-div">
					<div class="align-center">
						<span class="incomplete-trans-span rep-disc-span last-run-time-span">Last time data refresh run: ${lastSuccessfulRun}</span>
					</div>
				</div>
			</c:if>		
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div>
		<div class="container pos-relative">
			<div id="logo-dash" class="hide"></div>
			<div id="reporting-prof-details" class="row prof-pic-name-wrapper edit-prof-pic-name-wrapper rep-prof-details" style="border-bottom:1px solid #d2cdcd">
				<jsp:include page="reporting_prof_details.jsp"></jsp:include>
			</div>
			
			<div id="reporting-trans-details" class="row prof-pic-name-wrapper edit-prof-pic-name-wrapper reporting-trans-details">
				<jsp:include page="reporting_transaction_details.jsp"></jsp:include>
			</div>
			
			<div id="reportingDashTabs" class=" col-lg-12 col-md-12 col-sm-12 col-xs-12" style="margin-top:15px; display:inline-block; padding: 0;">
				<ul class="nav nav-tabs" role="tablist">
					<li id="overview-btn" class="active"><a href="#overview-tab" data-toggle="tab">Overview</a></li>
					<li id="leaderboard-btn"><a href="#leaderboard-tab" data-toggle="tab">LeaderBoard</a></li>
					<li id="score-stats-btn"><a href="#score-stats-tab" data-toggle="tab">Score Stats</a></li>
					<li id="reviews-btn"><a href="#reviews-tab" data-toggle="tab">Reviews</a></li>
					<li id="incomplete-surveys-btn" ><a href="#incomplete-surveys-tab" data-toggle="tab" style="padding-left:2px; padding-right:2px">Incomplete Surveys</a></li>
				</ul>
				<div class="tab-content rep-tab-content">
					<div class="tab-pane fade active in" id="overview-tab"  style="margin-top: 40px;">
						<div id="overviewSuccess" class="hide">
							<jsp:include page="reporting_overview.jsp"></jsp:include>
						</div>
						<div id="overviewFailure" class="hide">
							<div style="text-align:center; margin:5% auto">
								<span class="incomplete-trans-span" style="font-size:large">Sorry!!!</span>
								<div style="clear: both">
									<span class="incomplete-trans-span" style="font-size:large">Incomplete Data Found in Overview</span> 
								</div>
							</div>
						</div>
					</div>
					<div class="tab-pane fade col-lg-12 col-md-12 col-sm-12 col-xs-12" id="leaderboard-tab" style="padding: 0;">
						<jsp:include page="reporting_leaderboard.jsp"></jsp:include>
					</div>
					<div class="tab-pane fade col-lg-12 col-md-12 col-sm-12 col-xs-12" id="score-stats-tab">
						<jsp:include page="reporting_score_stats.jsp"></jsp:include>
					</div>
					<div class="tab-pane fade" id="reviews-tab" style="margin-top: 30px;">
						<jsp:include page="reporting_reviews.jsp"></jsp:include>
					</div>
					<div class="tab-pane fade" id="incomplete-surveys-tab" style="max-height:600px; margin-top: 30px;">
						<jsp:include page="reporting_incomplete_surveys.jsp"></jsp:include>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		$(document).attr("title", "Reporting Dashboard");
	
		updateViewAsScroll();
		
		paintForReportingDash()
		
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
	
	var profileMasterId = $('#rep-prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#rep-prof-container').attr('data-column-name');
	var currentProfileValue = $('#rep-prof-container').attr('data-column-value');
	var accountType = $('#rep-prof-container').attr('data-account-type');
	
	
	if( "${isSocialMediaExpired}" == "true" ){
		$("#rep-fix-social-media").removeClass("hide");
	} else {
		drawReportingDashButtons(currentProfileName, currentProfileValue);
	}
	
	drawOverviewPage();
	
	var showOverview = getOverviewData();
	
		if (showOverview == null) {
			$('#overviewSuccess').hide();
			$('#overviewFailure').show();
		} else {
			$('#overviewSuccess').show();
			$('#overviewFailure').hide();
		}
		
		$(window).resize();
		
	//Score stats
		var entityType = "${columnName}";
		var entityId = "${columnValue}";

		var companyId = "${companyId}";
		var userId = "${userId}";
		var profileMasterIdLead = "${profilemasterid}";
		var columnName = "${columnName}";
		var columnId = "${columnValue}";
		
		
		drawLeaderboardPage(columnName, columnId, profileMasterIdLead, userId, companyId);
		
		drawOverallScoreStatsGraph(entityId, entityType);		
		
		drawQuestionScoreStatsGraph(entityId, entityType);
		
		paintReportingDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);
		
		$('#rep-pro-cmplt-stars').on('click', '#dsh-btn2', function(e) {
			e.stopPropagation();
			var buttonId = 'dsh-btn2';
			var task = $('#dsh-btn2').data('social');
			dashboardButtonAction(buttonId, task, colName, colValue);
			$('#rep-social-media').fadeOut(500);
			if($('#rep-prof-container').length>0){
					var currentProfileName = $('#rep-prof-container').attr('data-column-name');
					var currentProfileValue = $('#rep-prof-container').attr('data-column-value');
					changeSocialMedia(currentProfileName, currentProfileValue);
			}	
		});
		
		$('#rep-pro-fix-cmplt-stars').on('click', '#dsh-btn0', function(e) {
			e.stopPropagation();
			var buttonId = 'dsh-btn0';
			// getSocialMediaToFix
			var payload = {
					"columnName" : $('#rep-prof-container').attr('data-column-name'),
					"columnValue" : $('#rep-prof-container').attr('data-column-value')
				};
			callAjaxGetWithPayloadData('./socialmediatofix.do', paintFixSocialMedia, payload, true);	
		});
		
		$('#rep-social-media').on('click','#rep-dismiss-social-media',function(e){
			$('#rep-social-media').fadeOut(500);
			delay(function(){
				e.stopPropagation();
				var currentProfileName = $('#rep-prof-container').attr('data-column-name');
				var currentProfileValue = $('#rep-prof-container').attr('data-column-value');
				changeSocialMedia(currentProfileName, currentProfileValue);
			},500);
		});
		
		
		$('#rep-fix-social-media').on('click','#rep-dismiss-fix-social-media',function(e){
			$('#rep-fix-social-media').fadeOut(500);
			delay(function(){
				drawReportingDashButtons(currentProfileName, currentProfileValue);
			},500);
		});
		
		cssForSafari();
		
		hideOverlay();
	});
</script>
