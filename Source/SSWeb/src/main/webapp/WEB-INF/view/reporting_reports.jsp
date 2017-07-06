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

<div class="dash-stats-wrapper bord-bot-dc clearfix" style="height:700px">
	<c:choose>
				<c:when test="${profilemasterid == 4 || accounttype == 'INDIVIDUAL' }">
					<div class="float-left dash-btn-dl-sd dash-btn-dl-sd-admin" style="float:left; margin-left:20px; width:auto;">
						<select id="download-survey-reports" class="float-left dash-download-sel-item" style="width:auto">
							<option value=2 data-report="survey-results"><spring:message code="label.downloadsurveydata.two.key" /></option>
							<option value=4 data-report="incomplete-survey"><spring:message code="label.incompletesurveydata.key" /></option>
						</select>
						
					</div>
				</c:when>
				<c:otherwise>
					<div style="display:inline-flex; margin-left:5%">
						<span style="height:30px;line-height:30px">Report</span>
						<div class="dash-btn-dl-sd-admin" style="float:left; margin-left:20px; width:auto;">
							<select id="generate-survey-reports" class="float-left dash-download-sel-item" style="width:auto">
								<option value=12 data-report="survey-stats">Survey Statistics</option>
								<option value=2 data-report="survey-results">Survey Results</option>
								<c:if test="${not empty realTechAdminId }">
									<option value=5 data-report="user-adoption"><spring:message code="label.downloaduseradoptiondata.key" /></option> 
								</c:if>
							</select>	
						</div>
						<div id="date-pickers" style="display:inline-flex; margin-left:40px;">
							<span style="height:30px;line-height:30px;">Date Range</span>
							<div class="dash-btn-dl-sd-admin" style="float:left; margin-left:20px; width:auto;">
								<input id="dsh-start-date" class="dsh-date-picker" placeholder="<spring:message code="label.startdate.key" />" style="width:auto; text-align:center">
							</div>
							<span style="height:30px;line-height:30px">-</span>
							<div class="dash-btn-dl-sd-admin" style="float:left; margin-left:20px; width:auto;">
								<input id="dsh-end-date" class="dsh-date-picker" placeholder="<spring:message code="label.enddate.key" />" style="width:auto; text-align:center">
							</div>
						</div>
						<div class="dash-btn-dl-sd-admin" style="float:left; margin-left:40px; width:120px;">
							<div id="reports-generate-report-btn" class="dash-down-go-button float-right cursor-pointer" style="width:100%;">
								<spring:message code="label.downloadsurveydata.key.click" />
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
			<div class="st-widget-txt" style="font-weight: 600 !important; color:#a09d9c; margin: 20px; margin-left: 5%;">
				Select the name and date range for report and click "Generate Report"
			</div>
			<div class="hm-header-main-wrapper hm-hdr-bord-bot" style="border-top:1px solid #009fe0; width:90%; margin:0 auto;">
				<div class="container">
					<div class="hm-header-row clearfix">
						<div class="float-left hm-header-row-left hr-dsh-adj-lft">
							Recent Activity
						</div>
					</div>
				</div>
			</div>
			<div class="v-um-tbl-wrapper" id="recent-activity-list">
				<!-- Fill in the recent activity list jsp -->
			</div>
			<div id="paginate-buttons" style="width: 100px; margin: 0 auto;">
				<div id="page-previous" class="float-left paginate-button"><spring:message code="label.previous.key" /></div>
				<div id="page-next" class="float-right paginate-button"><spring:message code="label.next.key" /></div>
			</div>
			
</div>

<script>
$(document).ready(function() {
	$(document).attr("title", "Reporting Dashboard");
	updateViewAsScroll();
	bindDatePickerforSurveyDownload();
	$('#date-pickers').hide();
	

		
});
</script>