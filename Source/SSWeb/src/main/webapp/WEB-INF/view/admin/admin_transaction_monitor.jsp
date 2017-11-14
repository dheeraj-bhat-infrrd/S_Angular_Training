<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.transaction.monitor.key" /></div>			
		</div>
	</div>
</div>
<div class="dash-wrapper-main">
	<div class="dash-container container">				
		<!-- Wrapper for input transaction stats graph -->
		<div class="dash-stats-wrapper bord-bot-dc clearfix trans-monitor-graph-container">
		<div class="dash-sub-head" style="font-weight: bold !important">Overall Transaction Statistics</div>
			<!-- <div class="float-left stats-left clearfix">
				<div class="dash-sub-head">Total Transaction Statistics</div>
				<div id="trans-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<div class="dsh-inp-wrapper float-left">
						<input id="trans-sel-item" class="dash-sel-item" type="text" placeholder="Start typing..." data-prev-val="" data-search-target="transactions">
						<div id="trans-srch-res" class="dsh-sel-dropdwn-cont"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Duration</div>
					<select id="transaction-count-days" class="float-left dash-sel-item">
						<option value="7">7 Days</option>
						<option value="14">14 Days</option>
						<option value="21">21 Days</option>
						<option value="28">28 Days</option>
					</select>
				</div>
			</div> -->
			
			<div class="float-left stats-right stats-right-adj pos-relative trans-monitor-graph" >
			<div id="low-trans" class="hide"></div>
				<div class="util-graph-wrapper">
					<div id="trans-gph-item" class="trans-monitor-util-gph-item"></div>
					<div class="util-gph-legend clearfix trans-monitor-graph-legend">
						<div class="util-gph-legend-item trans-monitor-legend-item-first trans-monitor-legend-text-style">
							Total<span class="lgn-col-item trans-monitor-lgn-col-blk"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Unprocessed<span class="lgn-col-item trans-monitor-lgn-col-red"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Invitations<span class="lgn-col-item trans-monitor-lgn-col-blue"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Reminders<span class="lgn-col-item trans-monitor-lgn-col-gray"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Completed<span class="lgn-col-item trans-monitor-lgn-col-grn"></span>
						</div>
					</div>
				</div>
			</div>
			
		</div>
		
		
		<!-- wrapper for processed transction graph -->
		<div class="dash-stats-wrapper bord-bot-dc clearfix trans-monitor-graph-container" style="margin-bottom:10px">
			<!-- <div class="float-left stats-left clearfix">
				<div class="dash-sub-head">Processed Transaction Statistics</div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<select disabled id="selection-list-proc-survey" class="float-left dash-sel-item">
						<option value="displayName">Individual</option>
						<option value="branchName">Branch</option>
						<option value="regionName">Region</option>
						<option selected value="company">Company</option>
					</select>
				</div>
				<div id="proc-srch-survey-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<div class="dsh-inp-wrapper float-left">
						<input id="proc-sur-sel-item" class="dash-sel-item" type="text" placeholder="Start typing..." data-prev-val="" data-search-target="procSurvey">
						<div id="proc-sur-srch-res" class="dsh-sel-dropdwn-cont"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Duration</div>
					<select id="proc-sur-count-days" class="float-left dash-sel-item">
						<option value="7">7 Days</option>
						<option value="14">14 Days</option>
						<option value="21">21 Days</option>
						<option value="28">28 Days</option>
					</select>
				</div>
			</div> -->
			<div id="proc-trans-header" class="dash-sub-head" style="font-weight: bold !important"></div>
			<div class="float-left stats-right stats-right-adj pos-relative trans-monitor-graph" >
			<div id="low-proc-sur" class="hide"></div>
				<div class="util-graph-wrapper">
					<div id="pro-survey-gph-item" class="trans-monitor-util-gph-item"></div>
					<div class="util-gph-legend clearfix trans-monitor-graph-legend">
						<div class="util-gph-legend-item trans-monitor-legend-item-first trans-monitor-legend-text-style">
							Total<span class="lgn-col-item trans-monitor-lgn-col-blk"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Unprocessed<span class="lgn-col-item trans-monitor-lgn-col-red"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Invitations<span class="lgn-col-item trans-monitor-lgn-col-blue"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Reminders<span class="lgn-col-item trans-monitor-lgn-col-gray"></span>
						</div>
						<div class="util-gph-legend-item trans-monitor-legend-item trans-monitor-legend-text-style">
							Completed<span class="lgn-col-item trans-monitor-lgn-col-grn"></span>
						</div>
					</div>
				</div>
			</div>
			
		</div>
				
		
		<!-- wrapper for active users graph -->
		<!-- <div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head">Active Users Statistics</div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<select disabled id="selection-list-actv-usr" class="float-left dash-sel-item">
						<option value="displayName">Individual</option>
						<option value="branchName">Branch</option>
						<option value="regionName">Region</option>
						<option selected value="company">Company</option>
					</select>
				</div>
				<div id="actv-usr-srch-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<div class="dsh-inp-wrapper float-left">
						<input id="actv-usr-sel-item" class="dash-sel-item" type="text" placeholder="Start typing..." data-prev-val="" data-search-target="actvUser">
						<div id="actv-usr-srch-res" class="dsh-sel-dropdwn-cont"></div>
					</div>
				</div>
				<div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Duration</div>
					<select id="actv-usr-count-days" class="float-left dash-sel-item">
						<<option value="7">7 Days</option>
						<option value="14">14 Days</option>
						<option value="21">21 Days</option>
						<option value="28">28 Days</option>
					</select>
				</div>
			</div>
			
			<div class="float-left stats-right stats-right-adj pos-relative" >
			<div id="low-actv-usr" class="hide"></div>
				<div class="util-graph-wrapper">
					<div id="actv-user-gph-item" class="util-gph-item"></div>
					<div class="util-gph-legend clearfix">
						<div class="util-gph-legend-item">
							Number Of Active Users<span class="lgn-col-item lgn-col-blue"></span>
						</div>
						
					</div>
				</div>
			</div>
			
		</div>	 -->	
						
	</div>
</div>
<div id="temp-message" class="hide"></div>
<script>
$(document).ready(function() {
	colName = "superAdmin";
	$(document).attr("title", "Transaction Stats");
	/* bindSelectButtonsForTranStats();
	bindAutosuggestForCompanySearch('trans-sel-item');
	bindAutosuggestForCompanySearch('proc-sur-sel-item');
	bindAutosuggestForCompanySearch('actv-usr-sel-item'); */
	
	/* var companyDetails = [
	                      {companyId:2,companyName:"Company2"},
	                      {companyId:3,companyName:"Company3"},
	                      {companyId:4,companyName:"Company4"},
	                      {companyId:5,companyName:"Company5"},
	                      {companyId:597,companyName:"Company597"},
	                      {companyId:598,companyName:"Company598"}
	                      ]; */
	
	showOverallSurveyGraph("company", -1, 14);
	getCompaniesForTransactionMonitor();
});
</script>