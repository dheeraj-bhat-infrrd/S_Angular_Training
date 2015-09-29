<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.joinus.key" /></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<script src='//www.google.com/recaptcha/api.js'></script>
</head>

<body>
<div id="toast-container" class="toast-container">
	<span id="overlay-toast" class="overlay-toast"></span>
</div>
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
			<div class="hm-header-row-left text-center lgn-adj padding-10"><spring:message code="label.signuptostartsurvey.key"/></div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header"><spring:message code="label.login.title.desc.key"/></div>
		
		<form id="frm-signup" method="POST" action="/registration.do">
			<div id="signup-form" class="reg_form_wrapper_1">
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_width-98"><spring:message code="label.firstname.key"/></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-fname"></div>
						<div class="rfr_txt_fld signup-inp-cont">
						<input type="text" class="rfr_input_fld" id="sign-fname" 
							name="firstName" value="${firstname}" placeholder='<spring:message code="label.firstname.key"/>'>
						</div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_width-98"><spring:message code="label.lastname.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-lname"></div>
						<div class="rfr_txt_fld signup-inp-cont">
						<input type="text" class="rfr_input_fld" id="sign-lname"
							name="lastName" value="${lastname}" placeholder='<spring:message code="label.lastname.key"/>'>
						</div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_width-98"><spring:message code="label.emailid.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-password"></div>
						<div class="rfr_txt_fld signup-inp-cont" style="margin-bottom: 0">
						<input type="email" class="rfr_input_fld" id="sign-email" data-non-empty="true"
							name="emailId" value="${emailid}" placeholder='<spring:message code="label.emailid.key"/>'>
						</div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl rfr_lbl_width-98 invisible">Captcha</div>
					<div class="g-recaptcha float-left" data-sitekey="6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K"></div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="reg_btn" id="signup-submit"><spring:message code="label.joinus.key"/></div>
				</div>
			</div>
			<input type="hidden" value="${message}" data-status="${status}" name="message" id="message"/>
		</form>
		
	</div>
</div>

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
$(document).ready(function(){

	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
			showError($('#message').val());
		} else {
			showInfo($('#message').val());
		}
	}

	$('#signup-submit').click(function(e){
		signupUser();
	});
   
	$('input').keypress(function(e){
		// detect enter
		if (e.which==13){
			e.preventDefault();
			signupUser();
		}
	});
	
});
</script>

</body>
</html>