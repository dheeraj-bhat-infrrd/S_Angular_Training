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
			<div class="float-left hm-header-row-left hr-dsh-adj-lft">
				<spring:message code="label.reporting.reports.key" />
			</div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>
<div class="dash-stats-wrapper bord-bot-dc clearfix">
	
				<div class="generate-report-div">
					<span class="generate-report-span-report" >Report</span>
					<div class="dash-btn-dl-sd-admin report-selector" >
						<select id="generate-survey-reports" class="float-left dash-download-sel-item report-selector-choice">
							<option value=12 data-report="survey-stats">Survey Statistics</option>
							<c:if test="${profilemasterid == 1}">
								<option value=13 data-report="user-adoption"><spring:message code="label.downloaduseradoptiondata.key" /></option> 
							</c:if>
						</select>	
					</div>
					<div class="generate-report-date-range" id="date-pickers">
						<div style="display:inline-flex;">
							<span class="generate-report-span-date">Date</span>
							<span class="generate-report-span-date" style="margin-left: 3px; padding-left:0 !important">Range</span>
						</div>
						<div class="dash-btn-dl-sd-admin report-date">
							<input id="dsh-start-date" class="dsh-date-picker" placeholder="<spring:message code="label.startdate.key" />" style="width:100%; text-align:center">
						</div>
						<span class="generate-report-span-dash">-</span>
						<div class="dash-btn-dl-sd-admin report-date" >
							<input id="dsh-end-date" class="dsh-date-picker" placeholder="<spring:message code="label.enddate.key" />" style="width:100%; text-align:center">
						</div>
					</div>
					<div class="dash-btn-dl-sd-admin generate-report-btn">
						<div id="reports-generate-report-btn" class="dash-down-go-button float-right cursor-pointer" style="width:100%;">
							<spring:message code="label.downloadsurveydata.key.click" />
						</div>
					</div>
				</div>
				
			<div class="st-widget-txt generate-report-info-text">
				Select the name and date range for report and click "Generate Report"
			</div>
			<div class="hm-header-main-wrapper hm-hdr-bord-bot recent-activity-header">
				<div class="container">
					<div class="hm-header-row clearfix">
						<div class="float-left hm-header-row-left hr-dsh-adj-lft">
							Recent Activity
						</div>
					</div>
				</div>
			</div>
			<div class="v-um-tbl-wrapper" id="recent-activity-list" style="width:90%; margin:30px auto;">
				<jsp:include page="reporting_recentActivityList.jsp"></jsp:include>
			</div>
			<div id="empty-list-msg-div" class="hide">
				<div style="text-align:center; margin:30% auto">
					<span class="incomplete-trans-span">There are No Recent Activities</span>
				</div>
			</div>
			<div id="rec-act-paginate-buttons" style="width: 100px; margin: 0 auto; ">
				<div id="rec-act-page-previous" class="float-left paginate-button"><spring:message code="label.previous.key" /></div>
				<div id="rec-act-page-next" class="float-right paginate-button"><spring:message code="label.next.key" /></div>
			</div>		
</div>

<script>
$(document).ready(function() {
	$(document).attr("title", "Reports");
	updateViewAsScroll();
	bindDatePickerforSurveyDownload();
	$('#date-pickers').hide();
	
	var startIndex=0;
	var recentActivityCount=getRecentActivityCount();
	var tableHeaderData= getTableHeader();
	$('#rec-act-page-previous').click(function(){
		startIndex=getStartIndex();	
		startIndex-=10;
		drawRecentActivity(startIndex, batchSize,tableHeaderData);
		showHidePaginateButtons(startIndex, recentActivityCount);
	});

	$('#rec-act-page-next').click(function(){
		startIndex=getStartIndex();
		startIndex+=10;
		drawRecentActivity(startIndex, batchSize,tableHeaderData);
		showHidePaginateButtons(startIndex, recentActivityCount);
	});
		
});
</script>