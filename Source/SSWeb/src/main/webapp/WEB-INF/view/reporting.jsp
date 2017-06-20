<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

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
<c:set value="${detractor}" var="detractors"></c:set>
<c:set value="${passives}" var="passives"></c:set>
<c:set value="${promoters}" var="promoters"></c:set>
<c:set value="${Survey_sent}" var="surveysSent"></c:set>
<c:set value="${Survey_completed}" var="surveysCompleted"></c:set>
<c:set value="${Social_posts}" var="socialPosts"></c:set>
<c:set value="${Zillow_reviews}" var="zillowReviews"></c:set>

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
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>

<div id="prof-container" data-profile-master-id="${profileMasterId}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="hide dash-top-info dash-prof-wrapper pos-relative dash-size" >
			<div id="top-dash" class="hide" ></div>
			<div id="dash-profile-detail-circles" class="row row-dash-top-adj" >
				<!-- Populated by dashboard_profiledetail.jsp -->
			</div>
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
			<div>
			<button type="button" class="btn btn-secondary">Overview</button>
			<button type="button" class="btn btn-secondary">LeaderBoard</button>
			<button type="button" class="btn btn-secondary">Score Stats</button>
			<button type="button" class="btn btn-secondary" style="margin-left:-3px !important">Activity</button>
			<button type="button" class="btn btn-secondary" style="margin-left:-3px !important">Reviews</button>
			<button type="button" class="btn btn-secondary" style="margin-left:-3px !important">Incomplete Surveys</button>
			</div>
			<div id="overviewTab" style="border-top:1px solid gray">
				<div class="col-lg-3 col-md-3 col-sm-3">
				
				</div>
				<div class="col-lg-3 col-md-3 col-sm-3" style="display:grid; margin-top:20px;">
					<div style="display:inline-flex; margin-top:10px">
						<div class="float-left dash-sel-lbl">Detractors</div>
						<div class="float-left dash-sel-lbl" style="width:${detractors}%;; height:65%; background:red; margin:auto 2px"></div>
						<div class="float-left dash-sel-lbl" style="color:red;">${detractors}%</div>
					</div>
					<div style="display:inline-flex; margin-top:10px">
						<div class="float-left dash-sel-lbl">Passives</div>
						<div class="float-left dash-sel-lbl" style="width:${passives}%; height:65%; background:gray; margin:auto 2px"></div>
						<div class="float-left dash-sel-lbl" style="color:gray;">${passives}%</div>
					</div>
					<div style="display:inline-flex; margin-top:10px">
						<div class="float-left dash-sel-lbl">Promotors</div>
						<div class="float-left dash-sel-lbl" style="width:${promoters}%;; height:65%; background:green; margin:auto 2px"></div>
						<div class="float-left dash-sel-lbl" style="color:green;">${promoters}%</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-3 col-sm-3 donut-chart">
				
				</div>
				<div class="col-lg-3 col-md-3 col-sm-3">
					<div class="col-lg-6 col-md-6 col-sm-6" style="margin-top:10px">
						<div class="row-lg-6 row-md-6 row-sm-6" style="font-weight:bold !important; font-size:medium">
							Surveys Sent
							<div style="font-size:-webkit-xxx-large">${surveysSent}</div>
						</div>
						<div class="row-lg-6 row-md-6 row-sm-6" style="font-weight:bold !important; font-size:medium">
							Surveys Completed
							<div style="font-size:-webkit-xxx-large">${surveysCompleted}</div>
						</div>						
					</div>
					<div class="col-lg-6 col-md-6 col-sm-6" style="margin-top:10px">
						<div class="row-lg-6 row-md-6 row-sm-6" style="font-weight:bold !important; font-size:medium">
							Social Posts
							<div style="font-size:-webkit-xxx-large">${socialPosts}</div>
						</div>
						<div class="row-lg-6 row-md-6 row-sm-6" style="font-weight:bold !important; font-size:medium">
							Zillow Reviews
							<div style="font-size:-webkit-xxx-large">${zillowReviews}</div>
						</div>
					</div>
				</div>
				<div>
					<button type="button" class="btn btn-secondary">SPS Stats</button>
					<button type="button" class="btn btn-secondary" style="margin-left:-3px !important">Average Rating</button>
					<button type="button" class="btn btn-secondary" style="margin-left:-3px !important">Completion Rate</button>
				</div>
			</div>
		</div>
		
	</div>

</div>
<script src="http://d3js.org/d3.v3.min.js"></script>
<script>
$(document).ready(function() {
	$(document).attr("title", "Reporting Dashboard");
	
	$(window).off('scroll');
	$(window).scroll(function() {
		if(window.location.hash.substr(1) == "dashboard") {
			dashbaordReviewScroll();		
		}
	});
	
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
	
	paintForReportingDash();
	
	updateViewAsScroll();
	
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	var accountType = $('#prof-container').attr('data-account-type');
	
	paintReportingDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);
	
	
});
</script>