<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.header.dashboard.key" /></div>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left">Company Report</div>
			<div class="dwnl-bnt-col float-right clear-both-sm">
				<div id="dsh-ind-rep-bnt" class="float-right dash-btn-dl-sd btn-wid-sm">
					<div class="dsh-dwnld-btn float-left cursor-pointer" onclick="downloadCompanyReport()">Download Report</div>
					<input id="comp-start-date" data-date-type="startDate"
						class="dsh-date-picker picker-sm" placeholder="Start Date"> <span>-</span>
					<input id="comp-end-date" data-date-type="endDate"
						class="dsh-date-picker picker-sm" placeholder="End Date">
				</div>
			</div>
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="dash-sub-head float-left">Billing Report</div>
			<div id="dsh-billing-report-div" class="clearfix ">
				<div class="float-left dash-sel-lbl"><spring:message code="label.email.key" /></div>
				<div class="dsh-inp-wrapper float-left" style="margin-bottom: 10px;">
					<input id="dsh-mail-id" class="dash-sel-item" type="text" placeholder='<spring:message code="label.username.key"/>' >
				</div>
				<div class="dwnl-bnt-col float-right clear-both-sm">
					<div id="dsh-bill-rep-bnt" class="float-right dash-btn-dl-br float-none">
						<div class="dsh-br-dwnld-btn float-left cursor-pointer"
							onclick="downloadBillingReport()">Generate Report</div>
					</div>
				</div>
			</div>
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head"><spring:message code="label.surveystatus.key" /></div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<select id="selection-list" class="float-left dash-sel-item">
						<option value="displayName">Individual</option>
						<option value="branchName">Branch</option>
						<option value="regionName">Region</option>
						<option value="company">Company</option>
					</select>
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
						<option value="90"><spring:message code="label.duration.three.key" /></option>
						<option value="365"><spring:message code="label.duration.four.key" /></option>
					</select>
				</div>
			</div>
			<div id="dash-survey-status" >
				<!-- Populated by survey status -->
			</div>
		</div>

		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head"><spring:message code="label.utilization.key" /></div>
				<div id="graph-sel-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.choose.key" /></div>
					<select id="graph-sel-list" class="float-left dash-sel-item">
						<option value="displayName">Individual</option>
						<option value="branchName">Branch</option>
						<option value="regionName">Region</option>
						<option value="company">Company</option>
					</select>
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

			<div class="dash-panels-wrapper">
				<div class="row">
					<div class="clearfix admin-report-dwn-row">
						<div class="admin-report-sel-col float-left">
							<div class="clearfix dash-sel-wrapper">
								<div class="float-left dash-sel-lbl">
									<spring:message code="label.choose.key" />
								</div>
								<select id="report-sel" class="float-left dash-sel-item">
									<option value="displayName">Individual</option>
									<option value="branchName">Branch</option>
									<option value="regionName">Region</option>
									<option value="company">Company</option>
								</select>
							</div>
						</div>
						<div class="admin-report-val-col float-left">
							<div id="dsh-srch-survey-div" class="clearfix dash-sel-wrapper"
								style="display: block;">
								<div class="float-left dash-sel-lbl">Choose</div>
								<div class="dsh-inp-wrapper float-left">
									<input id="admin-report-dwn" class="dash-sel-item" type="text"
										placeholder="Start typing..." data-prev-val="" data-search-target='reports'>
									<div id="dsh-srch-report" class="dsh-sel-dropdwn-cont"></div>
								</div>
							</div>
						</div>
						<div class="dwnl-bnt-col float-right">
							

							<div id="dsh-ind-rep-bnt" class="float-right dash-btn-dl-sd">
								<div id="dsh-ind-report-dwn-btn"
									class="dsh-dwnld-btn float-left cursor-pointer">
									<spring:message code="label.downloadsurveydata.key" />
								</div>
								<input id="indv-dsh-start-date" data-date-type="startDate" class="dsh-date-picker"
									placeholder="<spring:message code="label.startdate.key" />">
								<span>-</span> <input id="indv-dsh-end-date" data-date-type="endDate" class="dsh-date-picker"
									placeholder="<spring:message code="label.enddate.key" />">
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {
	colName = "superAdmin";
	hideOverlay();
	$(document).attr("title", "Admin Dashboard");
	bindSelectButtons();
	// initializing datepickers
	bindDatePickerForCompanyReport();
	bindDatePickerforSurveyDownload();
	bindDatePickerforIndividualSurveyDownload();
	bindAutosuggestForIndividualRegionBranchSearch('dsh-sel-item');
	bindAutosuggestForIndividualRegionBranchSearch('dsh-grph-sel-item');
	bindAutosuggestForIndividualRegionBranchSearch('admin-report-dwn');
});
</script>