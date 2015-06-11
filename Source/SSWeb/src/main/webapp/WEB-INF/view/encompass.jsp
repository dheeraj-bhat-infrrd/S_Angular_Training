<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="clearfix um-panel-content">
	<div class="row">
		<div class="um-top-row cleafix">
			<div class="clearfix um-top-form-wrapper">
				<!-- set encompass details -->
				<c:if test="${accountSettings != null && accountSettings.crm_info != null}">
					<c:set var="encompassusername"
						value="${accountSettings.crm_info.crm_username}" />
					<c:set var="encompasspassword"
						value="${accountSettings.crm_info.crm_password}" />
					<c:set var="encompassurl" value="${accountSettings.crm_info.url}" />
				</c:if>
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
					<div class="hm-item-row item-row-OR clearfix float-left">
						<div class="um-item-row-left text-right">
							<spring:message code="label.crm.username.key" />
						</div>
						<div class="clearfix float-right st-username-icons">
							<div class="um-item-row-icon margin-left-0"></div>
							<div class="um-item-row-icon margin-left-0"></div>
						</div>
						<div
							class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
							<input id="encompass-username" type="text"
								class="um-item-row-txt um-item-row-txt-OR"
								placeholder="Username" name="encompass-username"
								value="${encompassusername}">
							<div id="encompass-username-error" class="hm-item-err-2"></div>
						</div>
					</div>
				</div>
				<div
					class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item overflow-hidden">
					<div class="hm-item-row item-row-OR clearfix float-left">
						<div class="um-item-row-left text-right">
							<spring:message code="label.crm.password.key" />
						</div>
						<div class="clearfix float-right st-password-icons">
							<div class="um-item-row-icon margin-left-0"></div>
							<div class="um-item-row-icon margin-left-0"></div>
						</div>
						<div
							class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
							<input id="encompass-password" type="password"
								class="um-item-row-txt um-item-row-txt-OR"
								placeholder="Password" name="encompass-password"
								value="${encompasspassword}">
							<div id="encompass-password-error" class="hm-item-err-2 hide"></div>
						</div>
					</div>
				</div>
				<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
					<div class="hm-item-row item-row-OR clearfix float-left">
						<div class="um-item-row-left text-right">
							<spring:message code="label.crm.url.key" />
						</div>
						<div
							class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
							<input id="encompass-url" type="text"
								class="encompass-url-adj um-item-row-txt um-item-row-txt-OR"
								placeholder="URL" name="encompass-url" value="${encompassurl}">
							<div id="encompass-url-error" class="hm-item-err-2"></div>
						</div>
						<div class="clearfix float-left st-url-icons">
							<div id="encompass-testconnection"
								class="encompass-testconnection-adj um-item-row-icon icn-spanner margin-left-0 cursor-pointer"></div>
							<div id="encompass-save"
								class="um-item-row-icon icn-blue-tick margin-left-0 cursor-pointer"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>