<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="clearfix um-panel-content">
	<div class="row">
		<div class="um-top-row cleafix">
			<div class="clearfix um-top-form-wrapper">
				<!-- set encompass details -->
				<c:if test="${appSettings != null && appSettings.crm_info != null && appSettings.crm_info.crm_source == 'encompass'}">
					<c:set var="encompassusername" value="${appSettings.crm_info.crm_username}" />
					<c:set var="encompasspassword" value="${appSettings.crm_info.crm_password}" />
					<c:set var="encompassurl" value="${appSettings.crm_info.url}" />
					<c:set var="encompassfieldid" value="${appSettings.crm_info.crm_fieldId}" />
					<c:set var="encompassstate" value="${ appSettings.crm_info.state }" />
					<c:set var="encompassversion" value="${ appSettings.crm_info.version }" />
					
					<c:set var="loanOfficerEmail" value="${ appSettings.crm_info.loanOfficerEmail }" />
					<c:set var="loanOfficerName" value="${ appSettings.crm_info.loanOfficerName }" />
					
					<c:set var="buyerAgentEmail" value="${ appSettings.crm_info.buyerAgentEmail }" />
					<c:set var="buyerAgentName" value="${ appSettings.crm_info.buyerAgentName }" />
					<c:set var="sellerAgentEmail" value="${ appSettings.crm_info.sellerAgentEmail }" />
					<c:set var="sellerAgentName" value="${ appSettings.crm_info.sellerAgentName }" />
					
					<c:set var="propertyAddress" value="${ appSettings.crm_info.propertyAddress }" />
					<c:set var="loanProcessorEmail" value="${ appSettings.crm_info.loanProcessorEmail }" />
					<c:set var="loanProcessorName" value="${ appSettings.crm_info.loanProcessorName }" />
				</c:if>
				<c:if test="${alertEmails != null}">
				    <c:set var="alertEmail" value="${ alertEmails }" />
				</c:if>
				<form id="encompass-form">
					<input id="encompass-state" type="hidden" value="${ encompassstate }" />
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.username.key" />
							</div>
							<div class="clearfix float-right st-username-icons">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-fname en-icn-fname"></div>
								<input id="encompass-username" type="text" readonly onfocus="$(this).removeAttr('readonly');" class="um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Username" name="encompass-username" value="${encompassusername}">
								<div id="encompass-username-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.password.key" />
							</div>
							<div class="clearfix float-right st-password-icons">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-password en-icn-pswd"></div>
								<%
								    String password = (String) pageContext.getAttribute("encompasspassword");
											String formattedPassword = password;
											if (password != null && password.contains("\"")) {
												formattedPassword = password.substring(0, password.indexOf("\"")) + "&quot;"
														+ password.substring(password.indexOf("\"") + 1);
											}
								%>
								<c:choose>
								 <c:when test="${formattedPassword==null}">
								 <c:set var="password" value=""/>
								 </c:when>
								 <c:otherwise>
								 <c:set var="password" value="${formattedPassword}"/>
								 </c:otherwise>
								</c:choose>
								<input id="encompass-password" type="password" readonly onfocus="$(this).removeAttr('readonly');" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Password" name="encompass-password" value="${password}">
								<div id="encompass-password-error" class="hm-item-err-2 hide"></div>
							</div>
						</div>
					</div>
					
					
					<!-- encompass url -->
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.url.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="encompass-url" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="URL" name="encompass-url" value="${encompassurl}">
								<div id="encompass-url-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
						<!-- trigger  field -->
					
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden"">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.fieldId.key" />
							</div>
							<div class="clearfix float-right ">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-field-id en-icn"></div>
								<input id="encompass-fieldId" type="text" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Trigger fieldId" name="encompass-fieldId" value="${encompassfieldid}">
								<div id="encompass-fieldId-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					<!-- loan officer details -->
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right um-item-row-left-overflow">
								<spring:message code="label.loan.officer.email.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="loan-officer-email" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Loan Officer Email" name="loan-officer-email" value="${loanOfficerEmail}">
							</div>
						</div>
					</div>
					
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right um-item-row-left-overflow">
								<spring:message code="label.loan.officer.name.key" />
							</div>
							<div class="clearfix float-right ">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-field-id en-icn"></div>
								<input id="loan-officer-name" type="text" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Loan Officer Name" name="loan-officer-name" value="${loanOfficerName}">
							</div>
						</div>
					</div>
					
					<!-- buyers agent details -->
					
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item ">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.buyerAgentEmail.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="buyer-agent-email" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Buyer Agent Email Id" name="buyer-agent-email" value="${buyerAgentEmail}">
								<div id="encompass-url-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden " >
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.buyerAgentName.key" />
							</div>
							<div class="clearfix float-right ">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-field-id en-icn"></div>
								<input id="buyer-agent-name" type="text" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Buyer Agent Name" name="buyer-agent-name" value="${buyerAgentName}">
								<div id="encompass-fieldId-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					
					<!-- Seller agent details -->
					
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.sellerAgentEmail.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="seller-agent-email" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Seller Agent Email Id" name="seller-agnt-email" value="${sellerAgentEmail}">
								<div id="encompass-url-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden" >
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.sellerAgentName.key" />
							</div>
							<div class="clearfix float-right ">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-field-id en-icn"></div>
								<input id="seller-agent-name" type="text" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Seller Agent Name" name="seller-agnt-name" value="${sellerAgentName}">
								<div id="encompass-fieldId-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					<!-- LOAN PROCESSOR NAME -->
					
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.loanProcessorName.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="loan-processor-name" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Loan Processor Name" name="loan-processor-name" value="${loanProcessorName}">
								<div id="encompass-url-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					
					<!-- LOAN PROCESSOR EMAIL -->
					
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden" >
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.loanProcessorEmail.key" />
							</div>
							<div class="clearfix float-right ">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-field-id en-icn"></div>
								<input id="loan-processor-email" type="text" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Loan Processor Email" name="loan-processor-email" value="${loanProcessorEmail}">
								<div id="encompass-fieldId-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					<!-- PROPERTY ADDRESS -->
					
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.propertyAddress.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="property-address" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Property Address" name="property-address" value="${propertyAddress}">
								<div id="encompass-url-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					<!-- ENCOMPASS SDK VERSION -->

					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item en-botttom-padding overflow-hidden">
							<div class="um-item-row-left width-offset text-right">SDK Version</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<select id="sdk-version-selection-list" name="sdk-version-selection-list" class="float-left app-stng-sel-item">
								<c:forEach items="${encompassVersionList}" var="version">
    								<option value="${version.sdkVersion}" <c:if test ="${version.sdkVersion == encompassversion}">selected</c:if>>${version.sdkVersion}</option>
       							</c:forEach>
								</select>
								<div id="encompass-version-error" class="hm-item-err-2" style="display: none;"></div>
							</div>
					</div>

					<!-- ALERT EMAIL -->

                    <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
                        <div class="hm-item-row item-row-OR clearfix float-left">
                            <div class="um-item-row-left width-offset text-right">
                                <spring:message code="label.crm.alertEmail.key" />
                            </div>
                            <div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
                                <div class="rfr_icn icn-url en-icn"></div>
                                <input id="alert-email" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Alert Emails" name="alert-email" value="${alertEmail}">
                                <div id="encompass-url-error" class="hm-item-err-2"></div>
                            </div>
                        </div>
                    </div>

					<div class="encompass-btn clearfix">
						<div>
							<div id="en-dry-save" class="float-left enc-state-icon cursor-pointer">Save</div>
							<div id="en-dry-enable" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Enable</div>
							<div id="en-disconnect" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Disconnect</div>
						</div>
						<c:if test="${isRealTechOrSSAdmin}">
							<div id="en-test-connection" class="float-left enc-state-icon cursor-pointer" onclick="encompassCretentials();">Test Connection</div>
							<div id="en-generate-report" class="float-left enc-state-icon cursor-pointer hide">Generate Report</div>
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
		showEncompassButtons();
		isRealTechOrSSAdmin = $
		{
			isRealTechOrSSAdmin
		}
		;
	});
</script>
