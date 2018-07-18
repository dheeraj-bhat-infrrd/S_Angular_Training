<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.account.disabled.title.key" /></title>
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/application.js"></script>
</head>
<body>

<div id="toast-container" class="toast-container">
   <span id="overlay-toast" class="overlay-toast"></span>
</div>
<div class="overlay-payment hide" id="outer-payment"></div>
<div class="overlay-loader hide"></div>

<div class="hdr-wrapper">
	<div class="container hdr-container clearfix">
		<div class="float-left hdr-logo"></div>
		<div class="float-right clearfix hdr-btns-wrapper">
			<div class="float-left hdr-log-btn hdr-log-reg-btn" >
				<a href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
			</div>
		</div>
	</div>
</div>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">Your Login is prevented</div>
		</div>
	</div>
</div>

<div id="overlay-main" class="overlay-main hide">
		<div id="overlay-pop-up" class="overlay-disable-wrapper">
			<div class="ol-content">
				<div id="overlay-text" class="ol-txt">
					<!-- Populated by javascript -->
				</div>
				<div class="clearfix">
					<div class="ol-btn-wrapper" style="margin: 0 auto">
						<div id="overlay-cancel" class="ol-btn cursor-pointer">
							OK
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

<div id="" class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header text-center"><spring:message code="label.account.disabled.notification.key" /></div>
		<div class="reg_lkin_wrapper text-center">
			<div class="reg_lin_lin2"><spring:message code="label.account.login.disabled.title.key" /></div>
			<div class="reg_lin_lin2"><spring:message code="label.account.login.disabled.text.key" /></div>
			<div class="reg_btn" id="enable-login">Enable Login</div>
			<div style="display: none"><form id='enableLoginForm' action="./rest/user/enableuserlogin/${userId}" method="get"></form></div>
		</div>
	</div>
</div>

<script>
$(document).ready(function() {
	var userUnverified = "${userUnverified}";
	if(userUnverified==true||userUnverified=="true"){
		$('#overlay-main').show();
		$('#overlay-continue').show();
		$('#overlay-text').html("A verification email has been sent to your email-id. Please click on the verification link to log in.");
	}
	$('#enable-login').on('click',function(){
		if(userUnverified==false||userUnverified==''){
		$('#enableLoginForm').submit();
		}
	});

});
</script>
</body>
</html>