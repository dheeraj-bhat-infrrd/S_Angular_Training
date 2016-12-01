<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="clearfix um-panel-content">
	<div class="row">
		<div class="um-top-row cleafix">
			<div class="clearfix um-top-form-wrapper">
				<!-- set lone wolf details -->
				<c:if test="${appSettings != null && appSettings.crm_info != null && appSettings.crm_info.crm_source == 'LONEWOLF'}">
					<c:set var="loneclient" value="${ appSettings.crm_info.clientCode }" />
					<c:set var="lonestate" value="${ appSettings.crm_info.state }" />
					<c:set var="lonedate" value="${ appSettings.crm_info.transactionStartDate }" />
				</c:if>
				<form id="lone-wolf-form">
					<input id="lone-state" name="lone-state" type="hidden" value="${lonestate}" />
					<input id="transaction-start-date" name="transaction-start-date" type="hidden" value="${lonedate}" />	
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left text-right">
								<spring:message code="label.crm.client.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-fname en-icn-fname"></div>
								<input id="lone-client" name="lone-client" value="${loneclient}" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Client Code">
								<div id="lone-client-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					
					<div id="transaction-start-div" class="hide col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left text-right">Start Date</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-fname en-icn-fname"></div>
								<input id="lone-transaction-start-date"  data-date-type="startDate" placeholder="Start Date" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left">
							</div>
						</div>
					</div>
					
					<!-- <div id="transaction-start-div" class="hide clearfix dash-sel-wrapper">
						<div class="float-left dash-sel-lbl">Choose</div>
						<div class="dsh-inp-wrapper float-left">
							<input id="comp-start-date" data-date-type="startDate" class="dash-sel-item picker-sm" placeholder="Start Date">
						</div>
					</div> -->
					
						
					
					<div id="classification-div" class="hide col-sm-12 clearfix um-panel-item en-botttom-padding margin-left-20">
						<div class="classification-text"> Classification : Send survey to :</div>
						<div id="classification-list-wrapper" class="hm-item-row item-row-OR clearfix float-left classification-code-wrapper">
							
						</div>
					</div>
					
					
					<div class="encompass-btn clearfix">
						<div>
							<div id="lone-dry-cancel" class="float-left enc-state-icon enc-state-text-blue cursor-pointer hide" style="display: none;">Cancel</div>
							<div id="lone-get-classification" class="float-left enc-state-icon cursor-pointer">Get Classification</div>
							<div id="lone-data-save" class="hide float-left enc-state-icon cursor-pointer">Save</div>
							<div id="lone-dry-enable" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Enable</div>
							<div id="lone-disconnect" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Disconnect</div>
						</div>
						<c:if test="${isRealTechOrSSAdmin}">
							<div id="lone-test-connection" class="float-left enc-state-icon cursor-pointer" onclick="loneWolfCretentials();">Test Connection</div>
							<div id="lone-generate-report" class="float-left enc-state-icon cursor-pointer hide">Generate Report</div>
						</c:if>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<div id="toast-container" class="toast-container">
	<span id="overlay-toast" class="overlay-toast"></span>
</div>
<script>
	$(document).ready(function() {
		showLoneWolfButtons();
		isRealTechOrSSAdmin = ${isRealTechOrSSAdmin};
		var startDate;
		var fromEndDate = new Date();
		var toEndDate = new Date();
		$("input[data-date-type='startDate']").datepicker({
			orientation: "auto",
			format: 'mm/dd/yyyy',
			endDate: fromEndDate,
			todayHighlight: true,
			clearBtn: true,
			autoclose: true
		})
		.on('changeDate', function(selected){
		    startDate = new Date(selected.date.valueOf());
		    startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
		    $("input[data-date-type='endDate']").datepicker('setStartDate', startDate);
		});
	});
</script>
