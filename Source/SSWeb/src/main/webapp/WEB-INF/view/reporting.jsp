<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<style>
	.nav-tabs{
		border-bottom:1px solid #c5c5c5;
	}
	
	.nav>li>a:hover {
       background-color: #009FE0 !important;
     }
</style>
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

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
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
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft">
				<spring:message code="label.reporting.key" />
			</div>
			<div class="header-links-item" onclick="javascript:showMainContent('./showreports.do')"><spring:message code="label.reporting.reports.key" /></div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>

<div id="prof-container" data-profile-master-id="${profileMasterId}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="hide dash-top-info dash-prof-wrapper pos-relative dash-size" >
			<div id="top-dash" class="hide" ></div>
</div>
<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div>
		<div class="container pos-relative">
			<div id="logo-dash" class="hide"></div>
			<div class="row prof-pic-name-wrapper edit-prof-pic-name-wrapper">
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper">
					<div id="prof-img-container" class="prog-img-container prof-img-lock-wrapper">
						<jsp:include page="reporting_profileimage.jsp"></jsp:include>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper edit-prof-name-wrapper">
					<div id="prof-basic-container" class="prof-name-container">
						<jsp:include page="reporting_basicdetails.jsp"></jsp:include>
					</div>
				</div>
				<div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper float-right">
					<div id="prof-basic-container" class="prof-name-container">
						<jsp:include page="reporting_dashbuttons.jsp"></jsp:include>
					</div>
				</div>
			</div>
			<div id="reportingDashTabs" style="margin-top:40px; display:inline-block">
				<ul class="nav nav-tabs" role="tablist" style=" margin-bottom:40px;">
					<li id="overview-btn" class="active"><a href="#overview-tab" data-toggle="tab">Overview</a></li>
					<li id="leaderboard-btn"><a href="#leaderboard-tab" data-toggle="tab">LeaderBoard</a></li>
					<li id="score-stats-btn"><a href="#score-stats-tab" data-toggle="tab">Score Stats</a></li>
					<li id="activity-btn"><a href="#activity-tab" data-toggle="tab">Activity</a></li>
					<li id="reviews-btn"><a href="#reviews-tab" data-toggle="tab">Reviews</a></li>
					<li id="incomplete-surveys-btn" ><a href="#incomplete-surveys-tab" data-toggle="tab" style="padding-left:2px; padding-right:2px">Incomplete Surveys</a></li>
				</ul>
				<div class="tab-content" style="margin-left:-40px">
					<div class="tab-pane fade active in" id="overview-tab">
						<div id="overviewSuccess" class="hide">
							<jsp:include page="reporting_overview.jsp"></jsp:include>
						</div>
						<div id="overviewFailure" class="hide">
							<div style="text-align:center; margin:5% auto">
								<span class="incomplete-trans-span" style="font-size:large">Sorry!!!</span>
								<div style="clear: both">
									<span class="incomplete-trans-span" style="font-size:larger">Incomplete Data Found in Overview</span> 
								</div>
							</div>
						</div>
					</div>
					<div class="tab-pane fade" id="leaderboard-tab"></div>
					<div class="tab-pane fade" id="score-stats-tab"></div>
					<div class="tab-pane fade" id="activity-tab"></div>
					<div class="tab-pane fade" id="reviews-tab">
						<div class="people-say-wrapper rt-content-main rt-content-main-adj">
						<div class="main-con-header clearfix pad-bot-10-resp" style="display: block;border-bottom: 1px solid #dcdcdc;padding: 15px 0;">
							<div id="review-desc" class="float-left dash-ppl-say-lbl" data-profile-name="${profileName}">
							</div>
						</div>
						<div id="review-details" class="ppl-review-item-wrapper">
							<!-- Populated with dashboard_reviews.jsp -->
						</div>
					</div>
					</div>
					<div class="tab-pane fade" id="incomplete-surveys-tab">
						<div id="dash-survey-incomplete" class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12">
							<div class="dash-lp-header clearfix" id="incomplete-survey-header">
								<div class="float-left"><spring:message code="label.incompletesurveys.key" /></div>
								<div class="float-right dash-sur-link" onclick="showIncompleteSurveyListPopup(event)">View All</div>
							</div>
							<div id="dsh-inc-srvey" class="dash-lp-item-grp clearfix" data-total="0">
								<!-- Populated with dashboard_incompletesurveys.jsp -->
							</div>
							<%-- <div id="dsh-inc-dwnld" class="dash-btn-sur-data hide"><spring:message code="label.incompletesurveydata.key" /></div> --%>
						</div>
					</div>
				</div>
			</div>
			
		</div>
		
	</div>

</div>

<script>
$(document).ready(function() {
	$(document).attr("title", "Reporting Dashboard");
	
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
	
	$(window).off('scroll');
	$(window).scroll(function() {
		if(window.location.hash.substr(1) == "dashboard") {
			dashbaordReviewScroll();		
		}
	});
	
	var scrollContainer = document.getElementById('dsh-inc-srvey');
	scrollContainer.onscroll = function() {
		if (scrollContainer.scrollTop >= ((scrollContainer.scrollHeight * 0.75) - scrollContainer.clientHeight)) {
			if(!doStopIncompleteSurveyPostAjaxRequest || $('#dsh-inc-srvey>div.dsh-icn-sur-item.hide').length > 0) {
					fetchIncompleteSurvey(false);
					$('#dsh-inc-srvey').perfectScrollbar('update');
			}
		}
	};
	
	updateViewAsScroll();
	
	paintForReportingDash();
	
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	var accountType = $('#prof-container').attr('data-account-type');
	
	paintReportingDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);
	
	var showOverview = getOverviewData();
	
	if(showOverview == null){
		$('#overviewSuccess').hide();
		$('#overviewFailure').show();
	}else{
		$('#overviewSuccess').show();
		$('#overviewFailure').hide();
	}
	
});
</script>