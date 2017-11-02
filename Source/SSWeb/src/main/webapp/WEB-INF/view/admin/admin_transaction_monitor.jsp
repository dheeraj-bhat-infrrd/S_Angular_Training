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
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
				<div class="dash-sub-head">Input Transaction Statistics</div>
				<div id="region-div" class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl">Choose</div>
					<select disabled id="selection-list-transaction" class="float-left dash-sel-item">
						<option value="displayName">Individual</option>
						<option value="branchName">Branch</option>
						<option value="regionName">Region</option>
						<option selected value="company">Company</option>
					</select>
				</div>
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
						<option value="10">10 Days</option>
						<option value="15">15 Days</option>
						<option value="20">20 Days</option>
					</select>
				</div>
			</div>
			
			<div class="float-left stats-right stats-right-adj pos-relative" >
			<div id="low-trans" class="hide"></div>
				<div class="util-graph-wrapper">
					<div id="trans-gph-item" class="util-gph-item"></div>
					<div class="util-gph-legend clearfix">
						<div class="util-gph-legend-item">
							Total Transactions<span class="lgn-col-item lgn-col-grn"></span>
						</div>
						<div class="util-gph-legend-item">
							API Transactions<span class="lgn-col-item lgn-col-blue"></span>
						</div>
						<div class="util-gph-legend-item">
							Encompass Transactions<span class="lgn-col-item lgn-col-yel"></span>
						</div>
						<div class="util-gph-legend-item">
							FTP Transactions<span class="lgn-col-item lgn-col-red"></span>
						</div>
					</div>
				</div>
			</div>
			
		</div>
		
		<!-- wrapper for processed transction graph -->
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
			<div class="float-left stats-left clearfix">
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
						<option value="10">10 Days</option>
						<option value="15">15 Days</option>
						<option value="20">20 Days</option>
					</select>
				</div>
			</div>
			
			<div class="float-left stats-right stats-right-adj pos-relative" >
			<div id="low-proc-sur" class="hide"></div>
				<div class="util-graph-wrapper">
					<div id="pro-survey-gph-item" class="util-gph-item"></div>
					<div class="util-gph-legend clearfix">
						<div class="util-gph-legend-item">
							Total Survey Invitation Sent<span class="lgn-col-item lgn-col-grn"></span>
						</div>
						<div class="util-gph-legend-item">
							Total Survey Reminder Sent<span class="lgn-col-item lgn-col-blue"></span>
						</div>
						<div class="util-gph-legend-item">
							Total Survey Completed<span class="lgn-col-item lgn-col-yel"></span>
						</div>
						<div class="util-gph-legend-item">
							Total Transactions Received<span class="lgn-col-item lgn-col-red"></span>
						</div>
					</div>
				</div>
			</div>
			
		</div>
				
		
		<!-- wrapper for active users graph -->
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
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
						<option value="10">10 Days</option>
						<option value="15">15 Days</option>
						<option value="20">20 Days</option>
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
			
		</div>		
						
	</div>
</div>
<div id="temp-message" class="hide"></div>
<script>
$(document).ready(function() {
	colName = "superAdmin";
	$(document).attr("title", "Transaction Stats");
	bindSelectButtonsForTranStats();
	bindAutosuggestForCompanySearch('trans-sel-item');
	bindAutosuggestForCompanySearch('proc-sur-sel-item');
	bindAutosuggestForCompanySearch('actv-usr-sel-item');
	
});
</script>