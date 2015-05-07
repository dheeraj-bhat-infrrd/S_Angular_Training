<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="hm-header-main-wrapper" class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="hm-header-row-left text-center"><spring:message code="label.changepassword.key" /></div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header"><spring:message code="label.changepassword.key" /></div>
		
		<form id="change-password-form" method="POST" action="./changepassword.do">
			<div class="reg_form_wrapper_2">
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_long"><spring:message code="label.currentpassword.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-password"></div>
						<div class="rfr_txt_fld">
						<input type="password" class="rfr_input_fld" id="current-pwd" data-non-empty="true"
							name="oldpassword" placeholder="<spring:message code="label.currentpassword.key" />">
						</div>
					</div>
				</div>
				
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_long"><spring:message code="label.newpassword.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-password"></div>
						<div class="rfr_txt_fld">
							<input type="password" class="rfr_input_fld" id="new-pwd" data-non-empty="true"
								name="newpassword" placeholder="<spring:message code="label.newpassword.key" />">
						</div>
					</div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-confirm-password"></div>
						<div class="rfr_txt_fld">
							<input type="password" class="rfr_input_fld" id="confnw-pwd" data-non-empty="true"
								name="confirmnewpassword" placeholder="<spring:message code="label.confirmnewpassword.key" />">
						</div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="reg_btn" id="save-pwd"><spring:message code="label.submit.key" /></div>
				</div>
			</div>
			<input type="hidden" value="${message}" data-status="${status}" name="message" id="message"/>
		</form>
		
	</div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script>
var isChangePasswordFormValid;
$(document).ready(function() {
	hideOverlay();
	isChangePasswordFormValid = false;

	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'SUCCESS_MESSAGE') {
			showInfo($('#message').val());
		} else {
			showError($('#message').val());
		}
	}

	function updatechangepassword(formid) {
		var url = "./changepassword.do";
		callAjaxFormSubmit(url, updatechangepasswordCallBack, formid);
	}
	
	function updatechangepasswordCallBack(response) {
		$("#main-content").html(response);
	}
	
	$('#save-pwd').click(function(e) {
		if(validateChangePasswordForm()){
			updatechangepassword('change-password-form');
		}
	});
	
	$('input').keypress(function(e) {
		// detect enter
		if (e.which == 13) {
			e.preventDefault();
			if(validateChangePasswordForm()){
				updatechangepassword('change-password-form');
			}
		}
	});

	$('#current-pwd').blur(function() {
		if (validatePassword(this.id)) {
			hideError();
		}
	});

	$('#new-pwd').blur(function() {
		if (validatePassword(this.id)) {
			hideError();
		}
	});

	$('#confnw-pwd').blur(function() {
		if (validateConfirmPassword('new-pwd', this.id)) {
			hideError();
		}
	});
	
	function validateChangePasswordForm(){
		$("#serverSideerror").hide();
		isChangePasswordFormValid=true;
    	var isFocussed = false;
    	var isSmallScreen = false;
    	if($(window).width()<768){
    		isSmallScreen = true;
    	}
		if (!validatePassword('current-pwd')) {
			isChangePasswordFormValid = false;
			if (!isFocussed) {
				$('#current-pwd').focus();
				isFocussed = true;
			}
			if (isSmallScreen) {
				return isChangePasswordFormValid;
			}
		}
		if (!validatePassword('new-pwd')) {
			isChangePasswordFormValid = false;
			if (!isFocussed) {
				$('#new-pwd').focus();
				isFocussed = true;
			}
			if (isSmallScreen) {
				return isChangePasswordFormValid;
			}
		}
		if (!validateConfirmPassword('new-pwd', 'confnw-pwd')) {
			isChangePasswordFormValid = false;
			if (!isFocussed) {
				$('#confnw-pwd').focus();
				isFocussed = true;
			}
			if (isSmallScreen) {
				return isChangePasswordFormValid;
			}
		}
		return isChangePasswordFormValid;
	}
});
</script>

</body>
</html>