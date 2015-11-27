<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.registeruser.key"/></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
</head>

<body>
<div id="toast-container" class="toast-container">
	<span id="overlay-toast" class="overlay-toast"></span>
</div>
<div class="hdr-wrapper">
	<div class="container hdr-container clearfix">
		<div class="float-left hdr-logo"></div>
		<div class="float-right clearfix hdr-btns-wrapper">
			<div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key" /></div>
			<div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key" /></div>
		</div>
	</div>
</div>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="hm-header-row-left text-center padding-10"><spring:message code="label.signupstartjourney.key" /></div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header"><spring:message code="label.signupfree.key" /></div>
		
		<form id="registration-form" method="POST" action="./register.do">
			<div class="reg_form_wrapper_2">
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.name.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-fname"></div>
						<div class="rfr_txt_fld">
						<input class="rfr_input_fld" id="reg-fname" data-non-empty="true" name="firstname" value="${firstname}"
							placeholder="<spring:message code="label.firstname.key" />">
						</div>
					</div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-lname"></div>
						<c:choose>
							<c:when test="${not empty lastname}">
								<div class="rfr_txt_fld">
									<input class="rfr_input_fld" id="reg-lname" name="lastname" value="${lastname}"
										placeholder="<spring:message code="label.lastname.key" />">
								</div>
							</c:when>
							<c:otherwise>
								<div class="rfr_txt_fld">
									<input class="rfr_input_fld" id="reg-lname" name="lastname"
										placeholder="<spring:message code="label.lastname.key" />">
								</div>
							</c:otherwise>
					</c:choose>
						
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.emailid.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-mail"></div>
						<c:choose>
							<c:when test="${not isDirectRegistration}">
								<div class="rfr_txt_fld">
									<input class="rfr_input_fld" id="reg-email" data-non-empty="true" name="emailid"
										value="${emailid}" placeholder='<spring:message code="label.emailid.key" />' readonly="readonly">
								</div>
							</c:when>
							<c:otherwise>
							<div class="rfr_txt_fld">
								<input class="rfr_input_fld" id="reg-email" data-non-empty="true" data-email="true" name="emailid"
									value="${emailid}" placeholder='<spring:message code="label.emailid.key" />' readonly="readonly">
							</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.password.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-password"></div>
						<div class="rfr_txt_fld">
							<input type="password" class="rfr_input_fld" id="reg-pwd" data-non-empty="true"
								name="password" placeholder="<spring:message code="label.password.key" />">
						</div>
					</div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-confirm-password"></div>
						<div class="rfr_txt_fld">
							<input type="password" class="rfr_input_fld" id="reg-conf-pwd" data-non-empty="true"
								name="confirmpassword" placeholder="<spring:message code="label.confirmpassword.key" />">
						</div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="reg_btn" id="reg-submit"><spring:message code="label.submit.key" /></div>
				</div>
			</div>
			<input type="hidden" value="${message}" name="message" id="message"/>
			<input type="hidden" value="${emailid}" name="originalemailid" id="originalemailid"/>
			<input type="hidden" value="${isDirectRegistration}" name="isDirectRegistration" id="isDirectRegistration"/>
			<input type="hidden" value="${uniqueIdentifier}" name="uniqueIdentifier" id="uniqueIdentifier"/>
		</form>
		
	</div>
</div>

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
$(document).ready(function() {
	var isRegistrationFormValid = false;
	
	if ($('#message').val() != "") {
		showError($('#message').val());
	}
	
	$('#reg-submit').click(function(e) {
		submitRegistrationForm();
	});
	
	$('input').keypress(function(e){
    	if (e.which==13){
    		e.preventDefault();
    		submitRegistrationForm();
    	}
	});

	function submitRegistrationForm() {
		if (validateRegistrationForm('reg-form')) {
			$('#registration-form').submit();
			showOverlay();
		}
	}
	

	// Functions to trigger form validation of various input elements
	$('#reg-fname').blur(function() {
		if (validateFirstName(this.id)) {
			hideError();
		}
	});
	
	$('#reg-lname').blur(function() {
		if (validateLastName(this.id)) {
			hideError();
		}
	});
	
	$('#reg-email').blur(function() {
		if (validateEmailId(this.id)) {
			hideError();
		}
	});
	
	$('#reg-pwd').blur(function() {
		if (validatePassword(this.id)) {
			hideError();
		}
	});
	
	$('#reg-conf-pwd').blur(function(){
		if (validateConfirmPassword('reg-pwd', this.id)) {
			hideError();
		}
	});
	
	function validateRegistrationForm(id) {
    	isRegistrationFormValid = true;

    	var isFocussed = false;
    	var isSmallScreen = false;
    	if($(window).width() < 768){
    		isSmallScreen = true;
    	}
    	
    	// Validate form input elements
		if (!validateFirstName('reg-fname')) {
			isRegistrationFormValid = false;
			if (!isFocussed) {
    			$('#reg-fname').focus();
    			isFocussed=true;
    		}
    		if (isSmallScreen) {
    			return isRegistrationFormValid;
    		}
    		return isRegistrationFormValid;
		}
		 if (!validateLastName('reg-lname')) {
			isRegistrationFormValid = false;
			if (!isFocussed) {
    			$('#reg-lname').focus();
    			isFocussed=true;
    		}
    		if (isSmallScreen) {
    			return isRegistrationFormValid;
    		}
    		return isRegistrationFormValid;
		} 
		if (!validateEmailId('reg-email')) {
			isRegistrationFormValid = false;
			if (!isFocussed) {
    			$('#reg-email').focus();
    			isFocussed=true;
    		}
    		if (isSmallScreen) {
    			return isRegistrationFormValid;
    		}
    		return isRegistrationFormValid;
		}
		if (!validatePassword('reg-pwd')) {
			isRegistrationFormValid = false;
			if (!isFocussed) {
    			$('#reg-pwd').focus();
    			isFocussed=true;
    		}
    		if (isSmallScreen) {
    			return isRegistrationFormValid;
    		}
    		return isRegistrationFormValid;
		}
		if (!validateConfirmPassword('reg-pwd', 'reg-conf-pwd')) {
			isRegistrationFormValid = false;
			if (!isFocussed) {
    			$('#reg-conf-pwd').focus();
    			isFocussed=true;
    		}
    		if (isSmallScreen) {
    			return isRegistrationFormValid;
    		}
    		return isRegistrationFormValid;
		}
    	return isRegistrationFormValid;
	}
});
</script>

</body>
</html>