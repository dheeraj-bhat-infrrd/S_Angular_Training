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
							<div class="reg_form_row clearfix">
								<div class="reg_btn" id="vendasta-rm-create-account"><spring:message code="label.vendasta.account.create"/></div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		$(document).attr("title", "Listings Manager Settings");
		if ("${accountId}" != "") {
			$('#account-iden').val('${accountId}');
			$('#vendasta-settings-form').show();
		} else {
			$('#vendasta-create-accnt-form').show();
		}
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
						resetInputFields("vendasta-settings-form");
					}
				}, formData, true, '#vndsta-form-submit');
			}
		});
	
		initiateVendastaAccountCreation();
	});
</script>