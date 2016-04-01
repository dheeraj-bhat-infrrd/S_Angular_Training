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

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.header.dashboard.key" /></div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div id="prof-container" data-profile-master-id="${profileMasterId}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="dash-top-info dash-prof-wrapper pos-relative dash-size" >
			<div id="top-dash" class="hide" ></div>
			<div id="dash-profile-detail-circles" class="row row-dash-top-adj" >
				<!-- Populated by dashboard_profiledetail.jsp -->
			</div>
		</div>

		<div class="dash-stats-wrapper bord-bot-dc clearfix"  >
		
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head"><spring:message code="label.surveystatus.key" /></div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<select id="selection-list" class="float-left dash-sel-item"></select>
				</div>
				<div id="dsh-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<div class="dsh-inp-wrapper float-left">
						<input id="dsh-sel-item" class="dash-sel-item" type="text" placeholder="<spring:message code="label.starttyping.key" />" data-prev-val="" data-search-target='icons'>
						<div id="dsh-srch-res" class="dsh-sel-dropdwn-cont"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.duration.key" /></div>
					<select id="survey-count-days" class="float-left dash-sel-item">
						<option value="30"><spring:message code="label.duration.one.key" /></option>
						<option value="60"><spring:message code="label.duration.two.key" /></option>
						<option value="90" ><spring:message code="label.duration.three.key" /></option>
						<option value="365" selected><spring:message code="label.duration.four.key" /></option>
						<option value="-1"><spring:message code="label.duration.five.key"/></option>
					</select>
				</div>
			</div>
			<div class="pos-relative">
			 <div id="mid-dash" class="hide" > </div>
			<div id="dash-survey-status" >
				<!--  Populated by dashboard_surveystatus.jsp -->
			</div>
			</div> 
		</div>

		
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head"><spring:message code="label.utilization.key" /></div>
				<div id="graph-sel-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<select id="graph-sel-list" class="float-left dash-sel-item"></select>
				</div>
				<div id="dsh-grph-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<div class="dsh-inp-wrapper float-left">
						<input id="dsh-grph-sel-item" class="dash-sel-item" type="text" placeholder="<spring:message code="label.starttyping.key" />" data-prev-val="" data-search-target='graph'>
						<div id="dsh-grph-srch-res" class="dsh-sel-dropdwn-cont"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.duration.key" /></div>
					<select id="dsh-grph-format" class="float-left dash-sel-item">
						<option value="30"><spring:message code="label.duration.one.key" /></option>
						<option value="60"><spring:message code="label.duration.two.key" /></option>
						<option value="90" ><spring:message code="label.duration.three.key" /></option>
						<option value="365" selected><spring:message code="label.duration.four.key" /></option>
					</select>
				</div>
				
			</div>
			
			<div class="float-left stats-right stats-right-adj pos-relative" >
			<div id="low-dash" class="hide"></div>
				<div class="util-graph-wrapper">
					<div id="util-gph-item" class="util-gph-item"></div>
					<div class="util-gph-legend clearfix">
						<div class="util-gph-legend-item">
							<spring:message code="label.surveyssent.key" /><span class="lgn-col-item lgn-col-grn"></span>
						</div>
						<div class="util-gph-legend-item">
							<spring:message code="label.surveysclicked.key" /><span class="lgn-col-item lgn-col-blue"></span>
						</div>
						<div class="util-gph-legend-item">
							<spring:message code="label.surveyscompleted.key" /><span class="lgn-col-item lgn-col-yel"></span>
						</div>
						<div class="util-gph-legend-item">
							<spring:message code="label.surveyssocialposts.key" /><span class="lgn-col-item lgn-col-red"></span>
						</div>
					</div>
				</div>
			</div>
			<c:choose>
				<c:when test="${profilemasterid == 4 || accounttype == 'INDIVIDUAL' }">
					<div class="float-right dash-btn-dl-sd dash-btn-dl-sd-admin">
						<select id="download-survey-reports" class="float-left dash-download-sel-item">
							<option value=2 data-report="survey-results"><spring:message code="label.downloadsurveydata.two.key" /></option>
							<option value=4 data-report="incomplete-survey"><spring:message code="label.incompletesurveydata.key" /></option>
						</select>
						<input id="dsh-start-date" class="dsh-date-picker" placeholder="<spring:message code="label.startdate.key" />">
						<span>-</span>
						<input id="dsh-end-date" class="dsh-date-picker" placeholder="<spring:message code="label.enddate.key" />">
						<div id="dsh-dwnld-report-btn" class="dash-down-go-button float-right cursor-pointer">
							<spring:message code="label.downloadsurveydata.key.click" />
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="dash-btn-dl-sd-admin">
						<select id="download-survey-reports" class="float-left dash-download-sel-item">
							<option value=1 data-report="agent-ranking"><spring:message code="label.downloadsurveydata.one.key" /></option>
							<option value=2 data-report="survey-results"><spring:message code="label.downloadsurveydata.two.key" /></option>
							<option value=3 data-report="social-monitor"><spring:message code="label.downloadsurveydata.three.key" /></option>
							<option value=4 data-report="incomplete-survey"><spring:message code="label.incompletesurveydata.key" /></option>
							<c:if test="${profilemasterid == 1 || accounttype == 'COMPANY' }">
							
								<!--  <option value=6 data-report="company-hierarchy"><spring:message code="label.downloadcompanyhierarchydata.key" /></option>-->
								<option value=7 data-report="user-list-report"><spring:message code="label.downloaduserlistdata.key" /></option>
							</c:if>
							<c:if test="${not empty realTechAdminId }">
								<option value=5 data-report="user-adoption"><spring:message code="label.downloaduseradoptiondata.key" /></option> 
							</c:if>
						</select>
						<input id="dsh-start-date" class="dsh-date-picker" placeholder="<spring:message code="label.startdate.key" />">
						<span>-</span>
						<input id="dsh-end-date" class="dsh-date-picker" placeholder="<spring:message code="label.enddate.key" />">
						<div id="dsh-dwnld-report-btn" class="dash-down-go-button float-right cursor-pointer">
							<spring:message code="label.downloadsurveydata.key.click" />
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<div class="dash-panels-wrapper">
			<div class="row">
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
				
				<div class="dash-panel-right col-lg-8 col-md-8 col-sm-8 col-xs-12 resp-adj">
					<div class="people-say-wrapper rt-content-main rt-content-main-adj">
						<div class="main-con-header clearfix pad-bot-10-resp">
							<div id="review-desc" class="float-left dash-ppl-say-lbl" data-profile-name="${profileName}">
							</div>
						</div>
						<div id="review-details" class="ppl-review-item-wrapper">
							<!-- Populated with dashboard_reviews.jsp -->
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
var  is_dashboard_loaded = window.is_dashboard_loaded;
$(document).ready(function() {
	$(document).attr("title", "Dashboard");

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
	
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	var accountType = $('#prof-container').attr('data-account-type');
	
	var popupStatus = "${popupStatus}";
	var showSendSurveyPopupAdmin = "${showSendSurveyPopupAdmin}";
	var cookieValue = $.cookie("doNotShowPopup");
	
	if (cookieValue != "true" && showSendSurveyPopupAdmin == "true" && popupStatus == "Y") {
		sendSurveyInvitationAdmin(currentProfileName, currentProfileValue,'');
	}
	
	paintDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);
});
</script>