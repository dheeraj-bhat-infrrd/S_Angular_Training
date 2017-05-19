<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>

<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>


<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.vendastaproductsettings.key" />
			</div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>

<c:set value="${settings.vendasta_rm_settings.accountId}" var="accountId"/>

<c:choose>
	<c:when test="${ settings != null and accountMasterId == 4 and profilemasterid != 4 }">
		<div class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
			<div class="container">
				<div class="clearfix um-panel-content">
					<div class="row">
						<div class="um-top-row cleafix">
							<div class="clearfix um-top-form-wrapper">
								<form id="vendasta-settings-form" class="hide" method="post">						
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
												autocomplete="off"
												class="um-item-row-txt um-item-row-txt-OR en-form-align-left"
												placeholder="<spring:message code="label.vendasta.account.identifier.key" />">
											<div id="vendasta-accountId-error" class="hm-item-err-2"></div>
										</div>
									</div>
								</div>
								<div id="vndsta-form-submit"
									class="bd-vms-btn-save cursor-pointer vms-resp-submit">Save</div>
							</form>
							<form id="vendasta-create-accnt-form" class="hide" method="post">
								<div style="border-right: 1px solid #dcdcdc;float: left;width: 50%;">							
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.company.name"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-company-name" name="vendasta-company-name" placeholder='<spring:message code="label.vendasta.account.create.company.name"/>'>
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.country.name" /></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-country-name" name="vendasta-country-name" placeholder='<spring:message code="label.vendasta.account.create.country.name"/>'>
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.state.name"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-state-name" name="vendasta-state-name" placeholder='<spring:message code="label.vendasta.account.create.state.name"/>'>
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.city.name"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-city-name" name="vendasta-city-name" placeholder='<spring:message code="label.vendasta.account.create.city.name"/>'>
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.address"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-address" name="vendasta-address" placeholder='<spring:message code="label.vendasta.account.create.address"/>'>
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.zip"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-zip" name="vendasta-zip" placeholder='<spring:message code="label.vendasta.account.create.zip"/>'>
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									</div>
									<div style="float: right;width: 50%;">
										<div style="margin: 50px;">
											<div class="um-header">
												Already have an account?
											</div>
											<div class="st-score-text">
												Alternatively, Account Identifier can also be entered to setup listings manager.
											</div>
											<div class="reg_btn" id="enter-account-id-instead" style="margin-top: 30px;">Enter account identifier</div>
										</div>
									</div>
								</form>
							</div>
							<div class="reg_form_row clearfix" style="width: 45%;">
								<div class="reg_btn hide" id="vendasta-rm-create-account"><spring:message code="label.vendasta.account.create"/></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div id="error-msg-listing-manager-setting" class="vendasta-setting-access-cont">Access to this setting is Unauthorized.</div>
	</c:otherwise>
</c:choose>


<script>
	$(document).ready(function() {
		$(document).attr("title", "Listings Manager Settings");
		if ("${accountId}" != "") {
			$('#account-iden').val('${accountId}');
			$('#vendasta-settings-form').show();
		} else {
			$('#vendasta-create-accnt-form').show();
			$('#vendasta-rm-create-account').show();
		}
		updateViewAsScroll();
		$(document).on('click', '#vndsta-form-submit', function(e) {
			e.stopPropagation();
			if (validateVendastaFields()) {
				showOverlay();
				var formData = {
					"accountId" : $('#account-iden').val()
				};
				callAjaxPostWithPayloadData("/updatevendastasettings.do", function(data) {
					hideOverlay();
					var message = JSON.parse(data);
					if (message.type != "ERROR_MESSAGE") {
						showInfoMobileAndWeb(message.message);
					} else {
						showErrorInvalidMobileAndWeb(message.message);
					}
				}, formData, true, '#vndsta-form-submit');
			}
		});
		
		
		$(document).on('click', '#enter-account-id-instead', function(e){
			$('#vendasta-settings-form').show();
			$('#vendasta-create-accnt-form').hide();
			$('#vendasta-rm-create-account').hide();
		});
	
		initiateVendastaAccountCreation();
	});
</script>