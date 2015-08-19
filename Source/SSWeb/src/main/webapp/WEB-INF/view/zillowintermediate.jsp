<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.completeregistration.title.key"></spring:message></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body>
<div id="overlay-toast" class="overlay-toast"></div>
<div class="overlay-loader hide"></div>
<div class="login-main-wrapper padding-001 login-wrapper-min-height">
	<div class="container login-container">
		<div class="row login-row">
			<div class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
				<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
				<div class="login-txt text-center font-24 margin-bot-20">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
						<div>
						<form id="zillowForm">
							<div class="zillow-input-container">
								<label class="zillow-input-label">Email Address</label>
								<input class="zillow-input" name="zillowEmailAddress" type="email">
							</div>
							<div class="zillow-input-container">
								<label class="zillow-input-label">First Name</label>
								<input class="zillow-input" name="zillowFirstName" type="text">
							</div>
							<div class="zillow-input-container">
								<label class="zillow-input-label">Last Name</label>
								<input class="zillow-input" name="zillowLastName" type="text">
							</div>

							<input type="button" value="Submit" onclick="saveZillowEmailAddress()">
						</form>
						</div>
							<c:choose>
								<c:when test="${message == 1}"><spring:message code="label.waitmessage.key" /></c:when>
							</c:choose>
						</div>
					</div>
					<div style="font-size: 11px; text-align: center;"></div>
				</div>
				<div class="footer-copyright text-center">
					<spring:message code="label.copyright.key" />&copy;
					<spring:message code="label.footer.socialsurvey.key" /><span class="center-dot">.</span>
					<spring:message code="label.allrightscopyright.key" />
				</div>
			</div>
		</div>
	</div>
</div>

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
$(document).ready(function() {
	
});

function saveZillowEmailAddress()
{
	var payload = $('#zillowForm').serialize();
	$.ajax({
	url : './zillowSaveInfo.do',
	type : "POST",
	data : payload,
	async : false,
	complete : function(data) {
		setTimeout(function() {
			window.close();
		}, 3000);
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
$(window).on('unload', function(){
	var parentWindow = null;
	if (window.opener != null && !window.opener.closed) {
		parentWindow = window.opener;
	}
	var payload = {
		'socialNetwork' : "zillow"
	};
	fetchSocialProfileUrl(payload, function(data) {
		parentWindow.showLinkedInProfileUrl(data);
		parentWindow.showProfileLink("zillow", data);
		parentWindow.showProfileLinkInEditProfilePage("zillow", data.responseText);	
	});
});

function fetchSocialProfileUrl(payload, callBackFunction){
	$.ajax({
		url : './profileUrl.do',
		type : "GET",
		data : payload,
		async : false,
		complete : callBackFunction,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}
</script>

</body>
</html>
