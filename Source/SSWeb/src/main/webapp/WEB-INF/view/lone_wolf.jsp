<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="clearfix um-panel-content">
	<div class="row">
		<div class="um-top-row cleafix">
			<div class="clearfix um-top-form-wrapper">
				<form id="lone-wolf-form">
					<input id="lone-state" type="hidden" value="" />
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left text-right">
								<spring:message code="label.crm.apitoken.key" />
							</div>
							<div class="clearfix float-right st-username-icons">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-password en-icn-pswd"></div>
								<input id="lone-api" type="text"  class="um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="API Key" >
								<div id="lone-api-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left text-right">
								<spring:message code="label.crm.consumer.key" />
							</div>
							<div class="clearfix float-right st-password-icons">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-password en-icn-pswd"></div>
								<input id="lone-consumer-key" type="text"  class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Consumer Key" >
								<div id="lone-consumer-error" class="hm-item-err-2 hide"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item ">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left text-right">
								<spring:message code="label.crm.secretkey.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-password en-icn-pswd"></div>
								<input id="lone-secret-key" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Secret Key" >
								<div id="lone-secret-key-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden lone-space" style="height:90px">
						
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item en-botttom-padding">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left text-right">
								<spring:message code="label.crn.host.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="lone-host" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Host" >
								<div id="lone-host-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item en-botttom-padding">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left text-right">
								<spring:message code="label.crm.client.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-fname en-icn-fname"></div>
								<input id="lone-client" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Client Code" >
								<div id="lone-client-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="encompass-btn">
						<div>
							<div id="lone-dry-save" class="float-left enc-state-icon cursor-pointer">Save</div>
							<div id="lone-dry-enable" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Enable</div>
							<div id="lone-disconnect" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Disconnect</div>
						</div>
						<c:if test="${isRealTechOrSSAdmin}">
							<div id="lone-test-connection" class="float-left enc-state-icon cursor-pointer" onclick="">Test Connection</div>
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
	
</script>
