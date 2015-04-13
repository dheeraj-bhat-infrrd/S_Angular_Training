<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.login.title.key" /></title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>

<body>
<div class="overlay-loader hide"></div>
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
			<div class="hm-header-row-left text-center lgn-adj padding-10"><spring:message code="label.logintodosurvey.key"/></div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header"><spring:message code="label.logintodosurvey.key"/></div>
		
		<form id="frm-login" method="POST" action="j_spring_security_check">
			<div id="login-form" class="reg_form_wrapper_1">
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_width-98"><spring:message code="label.username.key"/></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-fname"></div>
						<input class="rfr_txt_fld" id="login-user-id" data-non-empty="true"
							name="j_username" data-email="true" placeholder='<spring:message code="label.username.key"/>'>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_width-98"><spring:message code="label.password.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-password"></div>
						<input type="password" class="rfr_txt_fld" id="login-pwd" data-non-empty="true"
							name="j_password" placeholder='<spring:message code="label.password.key"/>'>
					</div>
				</div>
				<div class="btn-forgot-passwd-link">
					<a href="./forgotpassword.do"><spring:message code="label.forgotpassword.key"/></a>
				</div>
				<div class="reg_form_row clearfix">
					<div class="reg_btn" id="login-submit"><spring:message code="label.login.button.key"/></div>
				</div>
			</div>
			<input type="hidden" value="${message}" data-status="${status}" name="message" id="message"/>
		</form>
		
	</div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
var isLoginFormValid;
$(document).ready(function(){
	isLoginFormValid = false;

	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
			showError($('#message').val());
		} else {
			showInfo($('#message').val());
		}
	}

	$('#login-submit').click(function(e){
		loginUser();
	});
   
	$('input').keypress(function(e){
		// detect enter
		if (e.which==13){
			e.preventDefault();
			loginUser();
		}
	});
	
	function loginUser() {
		console.log("submitting login form");
		if(validateLoginForm('login-form')){
			$('#frm-login').submit();
			showOverlay();
		}
	}
	
	// Function to validate the login params
	$('#login-user-id').blur(function() {
		if (validateUserId(this.id)) {
			hideError();
		}
	});
	
	$('#login-pwd').blur(function(){
		if (validateLoginPassword(this.id)) {
			hideError();
		}
	});

	function validateLoginForm(id){
		isLoginFormValid=true;
		var isFocussed = false;
		var isSmallScreen = false;
		if ($(window).width()<768) {
			isSmallScreen = true;
		}
		if (!validateUserId('login-user-id')) {
			isLoginFormValid=false;
			if (!isFocussed) {
				$('#login-user-id').focus();
				isFocussed=true;
			}
			if (isSmallScreen) {
				return isLoginFormValid;
			}
		}
		if (!validateLoginPassword('login-pwd')) {
			isLoginFormValid = false;
			if (!isFocussed) {
				$('#login-pwd').focus();
				isFocussed=true;
			}
			if (isSmallScreen) {
				return isLoginFormValid;
			}
		}
		return isLoginFormValid;
	}
	
	function validateUserId(elementId) {
		if ($(window).width()<768) {
			if ($('#'+elementId).val() != "") {
				if (emailRegex.test($('#'+elementId).val()) == true) {
					return true;
				} else {
					// $('#overlay-toast').html('Please enter a valid user name.');
					// showToast();
					showError('Please enter a valid user name');
					return false;
				}
			} else {
				// $('#overlay-toast').html('Please enter user name.');
				// showToast();
				showError('Please enter user name');
				return false;
			}
		}else{
			if ($('#'+elementId).val() != "") {
				if (emailRegex.test($('#'+elementId).val()) == true) {
					// $('#'+elementId).parent().next('.login-reg-err').hide();
					return true;
				} else {
					// $('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid user name.');
					// $('#'+elementId).parent().next('.login-reg-err').show();
					showError('Please enter a valid user name');
					return false;
				}
			} else {
				// $('#'+elementId).parent().next('.login-reg-err').html('Please enter user name.');
				// $('#'+elementId).parent().next('.login-reg-err').show();
				showError('Please enter user name');
				return false;
			}
		}
	}
	
	function validateLoginPassword(elementId){
		if ($(window).width()<768) {
			if ($('#'+elementId).val() != "") {
				return true;
			} else {
				// $('#overlay-toast').html('Please enter password.');
				// showToast();	
				showError('Please enter password');
				return false;
			}
		} else {
			if ($('#'+elementId).val() != "") {
					// $('#'+elementId).parent().next('.login-reg-err').hide();
					return true;
			} else {
				// $('#'+elementId).parent().next('.login-reg-err').html('Please enter password.');
				// $('#'+elementId).parent().next('.login-reg-err').show();	
				showError('Please enter password');
				return false;
			}
		}
	}
});
</script>

</body>
</html>