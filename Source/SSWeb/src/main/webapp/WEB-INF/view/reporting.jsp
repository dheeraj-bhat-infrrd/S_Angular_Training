<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<style>
.nav-tabs {
	border-bottom: 1px solid #c5c5c5;
}

.nav>li>a:hover {
	background-color: #009FE0 !important;
}
</style>
<script src="${initParam.resourcesPath}/resources/js/googleloader.js"></script>
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
<div id="prof-container" data-profile-master-id="${profileMasterId}"
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
</div>

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft">
				<spring:message code="label.reporting.key" />
			</div>
			<div id="timeFrame_container">
			<jsp:include page="timeFrame_dropdown.jsp"></jsp:include>
			</div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
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
			
			<div id="reportingDashTabs" style="margin-top:-40px; display:inline-block">
				<ul class="nav nav-tabs" role="tablist" style=" margin-bottom:40px;">
					<li id="overview-btn" class="active"><a href="#overview-tab" data-toggle="tab">Overview</a></li>
					<li id="leaderboard-btn"><a href="#leaderboard-tab" data-toggle="tab">LeaderBoard</a></li>
					<li id="score-stats-btn"><a href="#score-stats-tab" data-toggle="tab">Score Stats</a></li>
					<li id="reviews-btn"><a href="#reviews-tab" data-toggle="tab">Reviews</a></li>
					<li id="incomplete-surveys-btn" ><a href="#incomplete-surveys-tab" data-toggle="tab" style="padding-left:2px; padding-right:2px">Incomplete Surveys</a></li>
				</ul>
				<div class="tab-content rep-tab-content">
					<div class="tab-pane fade active in" id="overview-tab">
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
					<div class="tab-pane fade" id="leaderboard-tab"></div>
					<div class="tab-pane fade" id="score-stats-tab"></div>
					<div class="tab-pane fade" id="activity-tab"></div>
					<div class="tab-pane fade" id="reviews-tab">
						<jsp:include page="reporting_reviews.jsp"></jsp:include>
					</div>
					<div class="tab-pane fade" id="incomplete-surveys-tab">
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
	
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	var accountType = $('#prof-container').attr('data-account-type');
		
	paintReportingDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);
	
	var showOverview = getOverviewData();
	
		if (showOverview == null) {
			$('#overviewSuccess').hide();
			$('#overviewFailure').show();
		} else {
			$('#overviewSuccess').show();
			drawCompletionRateGraph();
			drawSpsStatsGraph();
			$(window).resize();
			$('#overviewFailure').hide();
		}
		
	});
</script>
