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
				</c:if>
				<form id="lone-wolf-form">
					<input id="lone-state" name="lone-state" type="hidden" value="${lonestate}" />
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item en-botttom-padding">
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
					
					<div class=" col-sm-12 clearfix um-panel-item en-botttom-padding margin-left-20">
						<div id="classification-list-wrapper" class="hm-item-row item-row-OR clearfix float-left">
							
						</div>
					</div>
					
					
					<div class="encompass-btn clearfix">
						<div>
							<div id="lone-dry-save" class="float-left enc-state-icon cursor-pointer">Save</div>
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
		isRealTechOrSSAdmin = $
		{
			isRealTechOrSSAdmin
		}
		;
	});
</script>
