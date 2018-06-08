<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="clearfix um-panel-content">
	<div class="row">
		<div class="um-top-row cleafix">
			<div class="clearfix um-top-form-wrapper">
				<!-- set ftp details -->
				<c:if test="${appSettings != null && appSettings.crm_info != null && appSettings.crm_info.crm_source == 'ftp'}">
					<c:set var="ftpusername" value="${appSettings.crm_info.crm_username}" />
					<c:set var="ftppassword" value="${appSettings.crm_info.crm_password}" />
					<c:set var="ftpurl" value="${appSettings.crm_info.url}" />
					<c:set var="ftpdir" value="${appSettings.crm_info.dir}" />
					
				
				</c:if>
				<form id="ftp-form">
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
								<input id="ftp-username" type="text" readonly onfocus="$(this).removeAttr('readonly');" class="um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="Username" name="ftp-username" value="${ftpusername}">
								<div id="ftp-username-error" class="hm-item-err-2"></div>
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
								    String password = (String) pageContext.getAttribute("ftppassword");
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
								<input id="ftp-password" type="password" readonly onfocus="$(this).removeAttr('readonly');" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Password" name="ftp-password" value="${password}">
								<div id="ftp-password-error" class="hm-item-err-2 hide"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.url.key" />
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-url en-icn"></div>
								<input id="ftp-url" type="text" class="ftp-url-adj um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="URL" name="ftp-url" value="${ftpurl}">
								<div id="ftp-url-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden"">
						<div class="hm-item-row item-row-OR clearfix float-left">
							<div class="um-item-row-left width-offset text-right">
								<spring:message code="label.crm.dir.key" />
							</div>
							<div class="clearfix float-right ">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<div class="rfr_icn icn-field-id en-icn"></div>
								<input id="ftp-dir" type="text" class="um-item-row-txt um-item-row-txt-OR en-form-align-left" placeholder="Directory" name="ftp-dir" value="${ftpdir}">
								<div id="ftp-dir-error" class="hm-item-err-2"></div>
							</div>
						</div>
					</div>
					
					<div class="encompass-btn clearfix">
						<div>
							<div id="ftp-save" class="float-left enc-state-icon cursor-pointer">Save</div>
							<div id="en-dry-enable" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Enable</div>
							<div id="en-disconnect" class="float-left enc-state-icon cursor-pointer hide" style="display: none;">Disconnect</div>
						</div>
						<c:if test="${isRealTechOrSSAdmin}">
							<!-- <div id="en-test-connection" class="float-left enc-state-icon cursor-pointer" onclick="encompassCretentials();">Test Connection</div>  -->
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
		showFtpButtons();
		isRealTechOrSSAdmin = $
		{
			isRealTechOrSSAdmin
		}
		;
	});
</script>
