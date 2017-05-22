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
<c:set value="${settings.contact_details}" var="contactDetails"/>

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
										class="col-lg-6 col-md-6 col-sm-12 col-xs-12 um-panel-item">
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
								<div class="row" style="clear: both;width: 252px;margin: 0 auto;">
  									<div class="col-md-6">
    									<div id="vndsta-form-submit-save" class="col-md-12 cursor-pointer bd-vms-btn-save" style="">Save
   										</div>
  									</div>
  									<div class="col-md-6">
   										<div id="vndsta-form-submit-create" class="col-md-12 cursor-pointer bd-vms-btn-save">Start Fresh
    									</div>
  									</div>
								</div>
							</form>
							<form id="vendasta-create-accnt-form" class="hide" method="post">
								<div style="border-right: 1px solid #dcdcdc;float: left;width: 50%;">							
									<div class="reg_form_row clearfix">
										<c:choose>
											<c:when test="${profilemasterid == 1 }">
												<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.company.name"/></div>
											</c:when>
											<c:when test="${profilemasterid == 2 }">
												<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.regionname.key"/></div>
											</c:when>
											<c:when test="${profilemasterid == 3 }">
												<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.branchname.key"/></div>
											</c:when>
										</c:choose>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<c:choose>
												<c:when test="${profilemasterid == 1 }">
													<input type="text" class="rfr_input_fld" id="vendasta-hierarchy-name" name="vendasta-hierarchy-name" placeholder='<spring:message code="label.vendasta.account.create.company.name"/>' value="${contactDetails.name }">													
												</c:when>
												<c:when test="${profilemasterid == 2 }">
													<input type="text" class="rfr_input_fld" id="vendasta-hierarchy-name" name="vendasta-hierarchy-name" placeholder='<spring:message code="label.regionname.key"/>' value="${contactDetails.name }">
												</c:when>
												<c:when test="${profilemasterid == 3 }">
													<input type="text" class="rfr_input_fld" id="vendasta-hierarchy-name" name="vendasta-hierarchy-name" placeholder='<spring:message code="label.branchname.key"/>' value="${contactDetails.name }">
												</c:when>
											</c:choose>
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.country.name" /></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-country-name" name="vendasta-country-name" placeholder='<spring:message code="label.vendasta.account.create.country.name"/>' value="${contactDetails.country }">
											<input type="hidden" name="vendasta-country-code" id="vendasta-country-code" value="${contactDetails.countryCode }">
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.state.name"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-state-name" name="vendasta-state-name" placeholder='<spring:message code="label.vendasta.account.create.state.name"/>' value="${contactDetails.state }">
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.city.name"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-city-name" name="vendasta-city-name" placeholder='<spring:message code="label.vendasta.account.create.city.name"/>' value="${contactDetails.city }">
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.address"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-address" name="vendasta-address" placeholder='<spring:message code="label.vendasta.account.create.address"/>' value="${contactDetails.address1 }">
											</div>
											<div class="vendasta-account-create-form-error"></div>
										</div>
									</div>
									<div class="reg_form_row clearfix">
										<div class="float-left rfr_lbl rfr_lbl_width-106"><spring:message code="label.vendasta.account.create.zip"/></div>
										<div class="float-left rfr_txt">
											<div class="rfr_icn icn-fname"></div>
											<div class="rfr_txt_fld">
											<input type="text" class="rfr_input_fld" id="vendasta-zip" name="vendasta-zip" placeholder='<spring:message code="label.vendasta.account.create.zip"/>' value="${contactDetails.zipcode }">
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
											<div class="reg_btn" id="enter-account-id-instead" style="margin-top: 30px;">Enter Account Identifier</div>
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
		$(document).on('click', '#vndsta-form-submit-save', function(e) {
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
		
		$(document).on('click', '#vndsta-form-submit-create', function(e) {
			$('#vendasta-settings-form').hide();
			$('#vendasta-create-accnt-form').show();
			$('#vendasta-rm-create-account').show();
		});
		
		
		$(document).on('click', '#enter-account-id-instead', function(e){
			$('#vendasta-settings-form').show();
			$('#vendasta-create-accnt-form').hide();
			$('#vendasta-rm-create-account').hide();
		});
	
		initiateVendastaAccountCreation();
		
		vendastaCountryAutoComplete();
	});
</script>