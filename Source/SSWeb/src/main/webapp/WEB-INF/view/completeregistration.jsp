<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.completeregistration.title.key" /></title>
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
			<div class="hm-header-row-left text-center"><spring:message code="label.completeregistration.title.key" /></div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header"><spring:message code="label.completeregistration.header.key" /></div>
		
		<form id="complete-registration-form" method="POST" action="./completeregistration.do">
			<div class="reg_form_wrapper_2">
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.name.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-fname"></div>
						<div class="rfr_txt_fld">
							<input class="rfr_input_fld" id="complete-reg-fname" data-non-empty="true"
								name="firstName" value="${firstName}" placeholder='<spring:message code="label.firstname.key" />'>
						</div>
					</div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-lname"></div>
						<c:choose>
							<c:when test="${not empty lastName}">
								<div class="rfr_txt_fld">
									<input class="rfr_input_fld" id="complete-reg-lname" name="lastName" value="${lastName}" 
										placeholder='<spring:message code="label.lastname.key" />'>
								</div>
							</c:when>
							<c:otherwise>
								<div class="rfr_txt_fld">
									<input class="rfr_input_fld" id="complete-reg-lname" name="lastName"
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
						<div class="rfr_txt_fld">
						<input class="rfr_input_fld" id="complete-reg-user-id" data-non-empty="true" data-email="true"
							name="emailId" value="${emailId}" placeholder='<spring:message code="label.emailid.key"/>' readonly="readonly" >
						</div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.profilepage.key" /></div>
					<div class="float-left rfr_txt">
						<div class="reg-prof-url"><a href="${profileUrl}" target="_blank">${profileUrl}</a></div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.password.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-password"></div>
						<div class="rfr_txt_fld">
						<input type="password" class="rfr_input_fld" id="complete-reg-pwd" data-non-empty="true"
							name="password" placeholder='<spring:message code="label.password.key" />'>
						</div>
					</div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-confirm-password"></div>
						<div class="rfr_txt_fld">
						<input type="password" class="rfr_input_fld" id="complete-reg-cnf-pwd" data-non-empty="true"
							name="confirmPassword" placeholder='<spring:message code="label.confirmpassword.key" />'>
						</div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="reg_btn" id="comp-reg-submit"><spring:message code="label.submit.key" /></div>
				</div>
			</div>
			<input type="hidden" value="${message}" name="message" id="message"/>
			<input type="hidden" value="${q}" name="q">
			<input type="hidden" value="${company}" name="companyId">
		</form>
		
	</div>
</div>

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
var isFormValid;
$(document).ready(function(){
	isFormValid = false;

	if ($('#message').val() != "") {
		showError($('#message').val());
	}

	function submitCompleteRegistrationForm() {
		if(validateCompleteRegistrationForm()){
			$('#complete-registration-form').submit();
		}
	}
	
	$('#comp-reg-submit').click(function(e){
		submitCompleteRegistrationForm();
	});
	
	$('input').keypress(function(e){
		// detect enter
		if (e.which==13){
			e.preventDefault();
			submitCompleteRegistrationForm();
		}
	});
	
	$('#complete-reg-fname').blur(function(){
		if (validateFirstName(this.id)) {
			hideError();
		}
	});
	
	$('#complete-reg-lname').blur(function(){
		if (validateLastName(this.id)) {
			hideError();
		}
	});
	
	$('#complete-reg-user-id').blur(function() {
		if (validateEmailId(this.id)) {
			hideError();
		}
	});
	
	$('#complete-reg-pwd').blur(function() {
		if (validatePassword(this.id)) {
			hideError();
		}
	});
	
	$('#complete-reg-cnf-pwd').blur(function() {
		if (validateConfirmPassword('complete-reg-pwd', this.id)) {
			hideError();
		}
	});
	
	function validateCompleteRegistrationForm() {
		var isFocussed = false;
		isFormValid = true;
		var isSmallScreen = false;
		if($(window).width()<768){
			isSmallScreen = true;
		}
		if(!validateFirstName('complete-reg-fname')){
			isFormValid = false;
			if(!isFocussed){
				$('#complete-reg-fname').focus();
				isFocussed=true;
			}
			if(isSmallScreen){
				return isFormValid;
			}
		}
		if(!validateLastName('complete-reg-lname')){
			isFormValid = false;
			if(!isFocussed){
				$('#complete-reg-lname').focus();
				isFocussed=true;
			}
			if(isSmallScreen){
				return isFormValid;
			}
		}
		if(!validateEmailId('complete-reg-user-id')){
			isFormValid = false;
			if(!isFocussed){
				$('#complete-reg-user-id').focus();
				isFocussed=true;
			}
			if(isSmallScreen){
				return isFormValid;
			}
		}
		if(!validatePassword('complete-reg-pwd')){
			isFormValid = false;
			if(!isFocussed){
				$('#complete-reg-pwd').focus();
				isFocussed=true;
			}
			if(isSmallScreen){
				return isFormValid;
			}
		}
		if(!validateConfirmPassword('complete-reg-pwd', 'complete-reg-cnf-pwd')){
			isFormValid = false;
			if(!isFocussed){
				$('#complete-reg-cnf-pwd').focus();
				isFocussed=true;
			}
			if(isSmallScreen){
				return isFormValid;
			}
		}
		return isFormValid;
	}
});
</script>

</body>
</html>