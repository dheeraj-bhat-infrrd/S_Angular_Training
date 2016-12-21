<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.vendastaproductsettings.key" />
			</div>
		</div>
	</div>
</div>
<div class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<div class="clearfix um-panel-content">
			<div class="row">
				<div class="um-top-row cleafix">
					<div class="clearfix um-top-form-wrapper">
						<form method="post">
							<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
								<div class="hm-item-row item-row-OR clearfix float-left">
									<div class="um-item-row-left text-right">
										<spring:message code="label.apiuser.key" />
									</div>
									<div class="clearfix float-right st-username-icons">
										<div class="um-item-row-icon margin-left-0"></div>
										<div class="um-item-row-icon margin-left-0"></div>
									</div>
									<div
										class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
										<div class="rfr_icn icn-fname en-icn-fname"></div>
										<input id="api-user" name="api-user"
											class="um-item-row-txt um-item-row-txt-OR en-form-align-left"
											placeholder="<spring:message code="label.vendasta.apiuser.key" />">
									</div>
								</div>
							</div>
							<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
								<div class="hm-item-row item-row-OR clearfix float-left">
									<div class="um-item-row-left text-right">
										<spring:message code="label.apikey.key" />
									</div>
									<div class="clearfix float-right st-password-icons">
										<div class="um-item-row-icon margin-left-0"></div>
										<div class="um-item-row-icon margin-left-0"></div>
									</div>
									<div
										class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
										<div class="rfr_icn icn-password en-icn-pswd"></div>
										<input id="api-key" name="api-key"
											class="um-item-row-txt um-item-row-txt-OR en-form-align-left"
											placeholder="<spring:message code="label.vendasta.apikey.key" />">
									</div>
								</div>
							</div>
							<div
								class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item en-botttom-padding">
								<div class="hm-item-row item-row-OR clearfix float-left">
									<div class="um-item-row-left text-right">
										<spring:message code="label.account.iden.key" />
									</div>
									<div class="clearfix float-right st-password-icons">
										<div class="um-item-row-icon margin-left-0"></div>
										<div class="um-item-row-icon margin-left-0"></div>
									</div>
									<div
										class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
										<div class="rfr_icn icn-password en-icn-pswd"></div>
										<input id="account-iden" name="account-iden"
											class="um-item-row-txt um-item-row-txt-OR en-form-align-left"
											placeholder="<spring:message code="label.vendasta.account.identifier.key" />">
									</div>
								</div>
							</div>
							<div id="vndsta-form-submit"
								class="bd-vms-btn-save cursor-pointer vms-resp-submit">Save</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(
			function() {
				if ("${accountId}" != "") {
					$('#account-iden').val('${accountId}');
				}
				if ("${apiUser}" != "") {
					$('#api-user').val('${apiUser}');
				}
				if ("${apiKey}" != "") {
					$('#api-key').val('${apiKey}');
				}
				$(document).on(
						'click',
						'#vndsta-form-submit',
						function() {
							showOverlay();
							var formData = {
								"accountId" : $('#account-iden').val(),
								"apiUser" : $('#api-user').val(),
								"apiKey" : $('#api-key').val()
							};
							callAjaxPostWithPayloadData(
									"/updatevendastasettings.do",
									function(data) {
										hideOverlay();
										$('#overlay-toast').html(data);
										showToast();
									}, formData, true, '#vndsta-form-submit');
						});
			});
</script>