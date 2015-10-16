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
<div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
<div class="overlay-loader hide"></div>
<div class="login-main-wrapper padding-001">
	<div class="container login-container">
		<div class="row login-row">
			<div class="profile-url-wrapper padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
				<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
				<div class="login-txt text-center font-24 margin-bot-20">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
						<div>
							<label class="widget-input-label"><spring:message code="label.widgetcode.key"/></label>
							<div>
								<code class="widget-code" id="widgetCodeBlock"></code>
							</div>
						</div>
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
<script type="${initParam.resourcesPath}/resources/js/common.js"></script>
<script>
$(document).ready(function() {
	var iden = "${ iden }";
	var profileLevel = "${ profileLevel }";
	var body = "";
	if (iden == undefined || profileLevel == undefined || profileLevel == ""){
		body = "Incorrect parameters. Please check your selection.";
	} else {
		body = "&lt;iframe src=\"http://localhost:8080/rest/widget/" + profileLevel + "/" + iden + "\" height=\"100%\" style=\"border : 0\"&gt;&lt;p&gt;Your browser does not support iframes.&lt;/p&gt;&lt;/iframe&gt;";
	}
	$("#widgetCodeBlock").html(body);
});
</script>

</body>
</html>
