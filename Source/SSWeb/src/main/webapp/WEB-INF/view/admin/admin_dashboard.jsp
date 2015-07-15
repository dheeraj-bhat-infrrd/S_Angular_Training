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
						<input id="dsh-grph-sel-item" class="dash-sel-item" type="text" placeholder="<spring:message code="label.starttyping.key" />"
							onkeyup="searchBranchRegionOrAgent(this.value, 'graph')">
						<div id="dsh-grph-srch-res"></div>
					</div>
				</div>
				
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.format.key" /></div>
					<select id="dsh-grph-format" class="float-left dash-sel-item">
						<option value="weekly"><spring:message code="label.format.one.key" /></option>
						<option value="monthly"><spring:message code="label.format.two.key" /></option>
						<option value="yearly"><spring:message code="label.format.three.key" /></option>
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
							<spring:message code="label.socialposts.key" /><span class="lgn-col-item lgn-col-red"></span>
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
										placeholder="Start typing..."
										onkeyup="searchBranchRegionOrAgent(this.value, 'reports')">
									<div id="dsh-srch-report"></div>
								</div>
							</div>
						</div>
						<div class="dwnl-bnt-col float-right">
							<div id="dsh-admin-rep-bnt" class="float-right dash-btn-dl-sd-admin hide">
								<select id="download-survey-reports" class="float-left dash-download-sel-item">
									<option value=1 data-report="loan-officer-ranking"><spring:message
											code="label.downloadsurveydata.one.key" /></option>
									<option value=2 data-report="customer-survey"><spring:message
											code="label.downloadsurveydata.two.key" /></option>
									<option value=3 data-report="social-monitor"><spring:message
											code="label.downloadsurveydata.three.key" /></option>
								</select> 
								<input id="dsh-start-date" class="dsh-date-picker"
									placeholder="<spring:message code="label.startdate.key" />">
								<span>-</span> <input id="dsh-end-date" class="dsh-date-picker"
									placeholder="<spring:message code="label.enddate.key" />">
								<div id="dsh-admin-report-dwn-btn"
									class="dash-down-go-button float-right cursor-pointer">
									<spring:message code="label.downloadsurveydata.key.click" />
								</div>
							</div>

							<div id="dsh-ind-rep-bnt" class="float-right dash-btn-dl-sd">
								<div id="dsh-ind-report-dwn-btn"
									class="dsh-dwnld-btn float-left cursor-pointer">
									<spring:message code="label.downloadsurveydata.key" />
								</div>
								<input id="dsh-start-date" class="dsh-date-picker"
									placeholder="<spring:message code="label.startdate.key" />">
								<span>-</span> <input id="dsh-end-date" class="dsh-date-picker"
									placeholder="<spring:message code="label.enddate.key" />">
							</div>
						</div>
					</div>
				</div>
			</div>


			<%-- <div class="dash-panel-right col-lg-8 col-md-8 col-sm-8 col-xs-12 resp-adj">
				<div class="people-say-wrapper rt-content-main rt-content-main-adj">
					<div class="main-con-header clearfix pad-bot-10-resp">
						<div id="review-desc" class="float-left dash-ppl-say-lbl">
							<spring:message code="label.peoplesayabout.key" />${profileName}
						</div>

						<div id="dsh-admin-cmp-dwnld"
							class="float-right dash-btn-dl-sd-admin hide">
							<select id="download-survey-reports"
								class="float-left dash-download-sel-item">
								<option value=1 data-report="loan-officer-ranking"><spring:message
										code="label.downloadsurveydata.one.key" /></option>
								<option value=2 data-report="customer-survey"><spring:message
										code="label.downloadsurveydata.two.key" /></option>
								<option value=3 data-report="social-monitor"><spring:message
										code="label.downloadsurveydata.three.key" /></option>
							</select> <input id="dsh-start-date" class="dsh-date-picker"
								placeholder="<spring:message code="label.startdate.key" />">
							<span>-</span> <input id="dsh-end-date" class="dsh-date-picker"
								placeholder="<spring:message code="label.enddate.key" />">
							<div id="dsh-dwnld-report-btn"
								class="dash-down-go-button float-right cursor-pointer">
								<spring:message code="label.downloadsurveydata.key.click" />
							</div>
						</div>

						<div id="dsh-cmp-dwnld" class="float-right dash-btn-dl-sd hide">
							<div id="dsh-dwnld-btn"
								class="dsh-dwnld-btn float-left cursor-pointer">
								<spring:message code="label.downloadsurveydata.key" />
							</div>
							<input id="dsh-start-date" class="dsh-date-picker"
								placeholder="<spring:message code="label.startdate.key" />">
							<span>-</span> <input id="dsh-end-date" class="dsh-date-picker"
								placeholder="<spring:message code="label.enddate.key" />">
						</div>
					</div>

				</div>
			</div> --%>
		</div>

<script>
$(document).ready(function() {
	colName = "superAdmin";
	hideOverlay();
	$(document).attr("title", "Dashboard");
	bindSelectButtons();
});
</script>