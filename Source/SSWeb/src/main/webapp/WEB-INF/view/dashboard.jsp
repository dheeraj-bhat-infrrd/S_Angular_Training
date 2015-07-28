<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.header.dashboard.key" /></div>
			<c:if test="${not empty assignments}">
				<div id="da-dd-wrapper" class="float-right header-right clearfix hr-dsh-adj-rt hdr-prof-sel">
					<div class="float-left hr-txt1"><spring:message code="label.viewas.key" /></div>
					<div id="dashboard-sel" class="float-left hr-txt2 cursor-pointer">${entityName}</div>
					<div id="da-dd-wrapper-profiles" class="va-dd-wrapper hide">
						<c:forEach var="company" items="${assignments.companies}">
							<div class="da-dd-item" data-column-type="companyId"
								data-column-name="${company.value}"
								data-column-value="${company.key}">${company.value}</div>
						</c:forEach>
						<c:forEach var="region" items="${assignments.regions}">
							<div class="da-dd-item" data-column-type="regionId" 
								data-column-name="${region.value}"
								data-column-value="${region.key}">${region.value}</div>
						</c:forEach>
						<c:forEach var="branch" items="${assignments.branches}">
							<div class="da-dd-item" data-column-type="branchId"
								data-column-name="${branch.value}"
								data-column-value="${branch.key}">${branch.value}</div>
						</c:forEach>
						<c:forEach var="agent" items="${assignments.agents}">
							<div class="da-dd-item" data-column-type="agentId"
								data-column-name="${agent.value}"
								data-column-value="${agent.key}">${agent.value}</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div id="prof-container" data-profile-master-id="${profileMasterId}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="dash-top-info dash-prof-wrapper">
			<div id="dash-profile-detail-circles" class="row row-dash-top-adj">
				<!-- Populated by dashboard_profiledetail.jsp -->
			</div>
		</div>

		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head"><spring:message code="label.surveystatus.key" /></div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<select id="selection-list" class="float-left dash-sel-item"></select>
				</div>
				<div id="dsh-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<div class="dsh-inp-wrapper float-left">
						<input id="dsh-sel-item" class="dash-sel-item" type="text" placeholder="<spring:message code="label.starttyping.key" />"
							onkeyup="searchBranchRegionOrAgent(this.value, 'icons')">
						<div id="dsh-srch-res"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.duration.key" /></div>
					<select id="survey-count-days" class="float-left dash-sel-item">
						<option value="30"><spring:message code="label.duration.one.key" /></option>
						<option value="60"><spring:message code="label.duration.two.key" /></option>
						<option value="90"><spring:message code="label.duration.three.key" /></option>
						<option value="365"><spring:message code="label.duration.four.key" /></option>
					</select>
				</div>
			</div>
			<div id="dash-survey-status" >
				<!-- Populated by dashboard_surveystatus.jsp -->
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
						<input id="dsh-grph-sel-item" class="dash-sel-item" type="text" placeholder="<spring:message code="label.starttyping.key" />"
							onkeyup="searchBranchRegionOrAgent(this.value, 'graph')">
						<div id="dsh-grph-srch-res"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.duration.key" /></div>
					<select id="dsh-grph-format" class="float-left dash-sel-item">
						<option value="30"><spring:message code="label.duration.one.key" /></option>
						<option value="60"><spring:message code="label.duration.two.key" /></option>
						<option value="90"><spring:message code="label.duration.three.key" /></option>
						<option value="365"><spring:message code="label.duration.four.key" /></option>
						<%-- <option value="weekly"><spring:message code="label.format.one.key" /></option>
						<option value="monthly"><spring:message code="label.format.two.key" /></option>
						<option value="yearly"><spring:message code="label.format.three.key" /></option> --%>
					</select>
				</div>
			</div>
			
			<div class="float-left stats-right stats-right-adj">
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
		</div>
		
		<div class="dash-panels-wrapper">
			<div class="row">
				<div id="dash-survey-incomplete" class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12">
					<div class="dash-lp-header clearfix" id="incomplete-survey-header">
						<div class="float-left"><spring:message code="label.incompletesurveys.key" /></div>
						<div class="float-right dash-sur-link" onclick="showIncompleteSurveyListPopup()">View All</div>
					</div>
					<div id="dsh-inc-srvey" class="dash-lp-item-grp clearfix">
						<!-- Populated with dashboard_incompletesurveys.jsp -->
					</div>
					<div id="dsh-inc-dwnld" class="dash-btn-sur-data hide"><spring:message code="label.incompletesurveydata.key" /></div>
				</div>
				
				<div class="dash-panel-right col-lg-8 col-md-8 col-sm-8 col-xs-12 resp-adj">
					<div class="people-say-wrapper rt-content-main rt-content-main-adj">
						<div class="main-con-header clearfix pad-bot-10-resp">
							<div id="review-desc" class="float-left dash-ppl-say-lbl">
								<spring:message code="label.peoplesayabout.key" />${profileName}
							</div>
							
							<div id="dsh-admin-cmp-dwnld" class="float-right dash-btn-dl-sd-admin hide">
								<select id="download-survey-reports" class="float-left dash-download-sel-item">
									<option value=1 data-report="agent-ranking"><spring:message code="label.downloadsurveydata.one.key" /></option>
									<option value=2 data-report="survey-results"><spring:message code="label.downloadsurveydata.two.key" /></option>
									<option value=3 data-report="social-monitor"><spring:message code="label.downloadsurveydata.three.key" /></option>
								</select>
								<input id="indv-dsh-start-date" class="dsh-date-picker" placeholder="<spring:message code="label.startdate.key" />">
								<span>-</span>
								<input id="indv-dsh-end-date" class="dsh-date-picker" placeholder="<spring:message code="label.enddate.key" />">
								<div id="dsh-dwnld-report-btn" class="dash-down-go-button float-right cursor-pointer">
									<spring:message code="label.downloadsurveydata.key.click" />
								</div>
							</div>
							
							<div id="dsh-cmp-dwnld" class="float-right dash-btn-dl-sd hide">
								<div id="dsh-dwnld-btn" class="dsh-dwnld-btn float-left cursor-pointer">
									<spring:message code="label.downloadsurveydata.key" />
								</div>
								<input id="dsh-start-date" class="dsh-date-picker" placeholder="<spring:message code="label.startdate.key" />">
								<span>-</span>
								<input id="dsh-end-date" class="dsh-date-picker" placeholder="<spring:message code="label.enddate.key" />">
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
<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/datepicker3.css">

<script>
$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "Dashboard");
	
	if ($("#da-dd-wrapper-profiles > div").length <= 1) {
		$('#da-dd-wrapper').remove();
	}
	
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	var accountType = $('#prof-container').attr('data-account-type');
	
	var popupStatus = "${popupStatus}";
	var showSendSurveyPopupAdmin = "${showSendSurveyPopupAdmin}";
	
	if (showSendSurveyPopupAdmin == "true" && popupStatus == "Y") {
		sendSurveyInvitationAdmin(currentProfileName, currentProfileValue);
	}
	
	paintDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);
});
</script>