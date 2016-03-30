<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft">
				<spring:message code="label.header.dashboard.key" />
			</div>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
  <div class="dash-sub-head float-left">Company Users Report</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
          
            <div class="float-left clearfix">
				<div id="admin-billing-report-div" class="float-left">
					<div class="float-left dash-sel-lbl">
						<spring:message code="label.email.key" />
					</div>
					<div class="dsh-inp-wrapper float-left"
						style="margin-bottom: 10px;">
						<input id="admin-mail-id" class="admin-sel-item" type="text"
							placeholder='<spring:message code="label.username.key"/>'>
					</div>
				</div>
				<div class="admin-report-val-col float-left">
					<div id="admin-srch-survey-div" class="clearfix admin-sel-wrapper"
						style="display: block;">
						<div class="float-left dash-sel-lbl">Company</div>
						<div class="dsh-inp-wrapper float-left">
							<input id="admin-report-down" class="admin-sel-item" type="text"
								placeholder="Start typing..." data-prev-val=""
								data-search-target='reports'>
							<div id="admin-srch-report" class="dsh-sel-dropdwn-cont"></div>
						</div>
					</div>
				</div>
				<div id="admin-bill-rep-bnt" class="float-right admin-btn-dl-br float-none" style="margin-left:30px;">
						<div class="dsh-br-dwnld-btn float-left cursor-pointer">Generate Report</div>
					</div>
			</div>
		</div>
	</div>
		<div class="dash-container container">
  <div class="dash-sub-head float-left">Company Hierarchy Report</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix">
          
            <div class="float-left clearfix">
				<div id="admin-hierarchy-report-div" class="float-left">
					<div class="float-left dash-sel-lbl">
						<spring:message code="label.email.key" />
					</div>
					<div class="dsh-inp-wrapper float-left"
						style="margin-bottom: 10px;">
						<input id="hierarchy-mail-id" class="admin-sel-item" type="text"
							placeholder='<spring:message code="label.username.key"/>'>
					</div>
				</div>
				<div class="admin-report-val-col float-left">
					<div id="hierarchy-srch-survey-div" class="clearfix admin-sel-wrapper"
						style="display: block;">
						<div class="float-left dash-sel-lbl">Company</div>
						<div class="dsh-inp-wrapper float-left">
							<input id="hierarchy-report-down" class="admin-sel-item" type="text"
								placeholder="Start typing..." data-prev-val=""
								data-search-target='hierarchy'>
							<div id="hierarchy-srch-report" class="dsh-sel-dropdwn-cont"></div>
						</div>
					</div>
				</div>
				<div id="admin-hierarchy-rep-bnt" class="float-right admin-btn-dl-br float-none" style="margin-left:30px;">
						<div class="dsh-br-dwnld-btn float-left cursor-pointer">Generate Report</div>
					</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function(){
	colName = "superAdmin";
	bindAutosuggestForCompanySearch('admin-report-down');
	bindAutosuggestForCompanySearch('hierarchy-report-down');
});
</script>