<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message code="label.linkexpired.title.key" /></title>
<link rel="shortcut icon"
	href="/favicon.ico" sizes="16x16">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/rangeslider.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
</head>
<body>
	<div id="toast-container" class="toast-container">
		<span id="overlay-toast" class="overlay-toast"></span>
	</div>
	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
			<div class="float-right clearfix hdr-btns-wrapper">
				<div class="float-left hdr-log-btn hdr-log-reg-btn">
					<spring:message code="label.signin.key" />
				</div>
				<div class="float-left hdr-reg-btn hdr-log-reg-btn">
					<spring:message code="label.joinus.key" />
				</div>
			</div>
		</div>
	</div>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="hm-header-row-left text-center">
					<spring:message code="label.linkexpired.title.key" />
				</div>
			</div>
		</div>
	</div>
	<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
		<div class="container">
			<div class="inv-succ-text1 text-center"><spring:message code="label.linkexpired.text1.key" /></div>
			<div class="inv-succ-text2 text-center"><spring:message code="label.linkexpired.text2.key" /></div>
			<div class="inv-succ-email text-center">${emailid}</div>
			<div class="inv-succ-text3"><spring:message code="label.linkexpired.text3.key" /></div>
			<div class="inv-succ-link" onclick="resendActivationLink();"><spring:message code="label.registerinvitesuccessful.resendlink.key" /></div>
			<div class="inv-succ-link" onclick="redirectToLoginpage();"><spring:message code="label.registerinvitesuccessful.loginlink.key" /></div>
		</div>
	</div>
	<div style="display:none;">
		<form id="registration-form">
			<input type="text" name="firstName" value="${firstname}">
			<input type="text" name="lastName" value="${lastname}">
			<input type="text" name="emailId" value="${emailid}">
		</form>
	</div>
	<script
		src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
	<script>
		if (!window.jQuery) {
			document.write('<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js""><\/script>');
		}
	</script>
	<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			
		});
		function resendActivationLink() {
			var payload = $('#registration-form').serialize();
			$.ajax({
				//url : window.location.origin + "/resendRegistrationMail.do",
				url : getLocationOrigin() + "/resendRegistrationMail.do",
				data : payload,
				type : "GET",
				cache : false,
				dataType : "TEXT",
				success : function(data){
					$('#overlay-toast').html(data);
					showToast();
				},
				error : function(e) {
					if(e.status == 504) {
						redirectToLoginPageOnSessionTimeOut(e.status);
						return;
					}
					redirectErrorpage();
				}
			});
		}
		function redirectToLoginpage() {
			// window.open( window.location.origin + "/login.do", '_self' );
			window.open( getLocationOrigin() + "/login.do", '_self' );
		}
	</script>
</body>
</html>