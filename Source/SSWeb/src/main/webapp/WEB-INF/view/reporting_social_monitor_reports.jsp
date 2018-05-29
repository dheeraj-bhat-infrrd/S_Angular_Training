<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import = "java.io.*,java.util.*" %>

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
				Social Monitor Reporting
			</div>
		</div>
	</div>
</div>


<div id="reports_page_container" class="dash-stats-wrapper bord-bot-dc clearfix">
	
				<div class="generate-report-div">
					<span class="generate-report-span-report" >Report</span>
					<div class="dash-btn-dl-sd-admin report-selector" >
						<select id="generate-survey-reports" class="float-left dash-download-sel-item report-selector-choice">
							<option value=<spring:message code="label.social.monitor.date.report.value" /> data-report="social-monitor-date-report"><spring:message code="label.social.monitor.date.report.key" /></option>
							<option value=<spring:message code="label.social.monitor.date.keyword.report.value" /> data-report="social-monitor-date-keyword-report"><spring:message code="label.social.monitor.date.keyword.report.key" /></option>
						</select>	
					</div>
					<div class="generate-report-date-range" id="date-pickers">
						<div style="display: -webkit-box; display:inline-flex;">
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
					<div id="sm-keywords" class="float-left board-div hide">
						<div class="dash-btn-dl-sd-admin time-selector" style="width:150px; margin-top:-4px">
							<select id="sm-keywords-selector" class="float-left dash-download-sel-item board-selector-choice" style="width:100%" required>
								<option value="" selected disabled>--Select keyword--</option>
							</select>	
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
	$(document).attr("title", "Reporting");
	updateViewAsScroll();
	bindDatePickerforSurveyDownload();
	drawTransReportTimeFrames();
	drawPhraseList();
	
	var selectedVal = $('#generate-survey-reports').val();
	var key = parseInt(selectedVal);
	
	if(key == 302){
		$('#sm-keywords').removeClass('hide');
	}else{
		$('#sm-keywords').addClass('hide');
	}
	
	var startIndex=0;
	var recentActivityCount;
	var tableHeaderData= getTableHeader();
	$('#rec-act-page-previous').click(function(){
		startIndex=getStartIndex();	
		startIndex-=10;
		recentActivityCount=getRecentActivityCount();
		drawRecentActivity(startIndex, batchSize,tableHeaderData,recentActivityCount);
		showHidePaginateButtons(startIndex, recentActivityCount);
	});

	$('#rec-act-page-next').click(function(){
		startIndex=getStartIndex();
		startIndex+=10;
		recentActivityCount=getRecentActivityCount();
		drawRecentActivity(startIndex, batchSize,tableHeaderData,recentActivityCount);
		showHidePaginateButtons(startIndex, recentActivityCount);
	});
	
	autoRefresh(tableHeaderData);	
		
});
</script>