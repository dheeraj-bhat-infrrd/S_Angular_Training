<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.completeregistration.title.key"></spring:message></title>
	<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
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
								<c:choose>
									<c:when test="${success == 1}"><spring:message code="label.authorization.success" /></c:when>
									<c:when test="${message == 1}"><spring:message code="label.waitmessage.key" /></c:when>
									<c:otherwise><spring:message code="label.authorization.failure" /></c:otherwise>
								</c:choose>
							</div>
						</div>
						<div style="font-size: 11px; text-align: center;">
							<c:choose>
								<c:when test="${success == 1}"><spring:message code="label.timer.key" /></c:when>
								<c:when test="${message == 1}"></c:when>
								<c:otherwise><spring:message code="label.timer.key" /></c:otherwise>
							</c:choose>
						</div>
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

	<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
	<script>
	$(document).ready(function() {
		// Onload before auth Url
		var waitMessage = "${message}";
		if (parseInt(waitMessage) == 1) {
			var authUrl = "${authUrl}";
			if (authUrl != null) {
				location.href = authUrl;
			}
			else {
				console.log("authUrl not found!");
			}
		}
		
		// select parent Window
		var parentWindow;
		if (window.opener != null && !window.opener.closed) {
			parentWindow = window.opener;
		}
		else {
			console.log("Unable to access parent window!");
		}
		
		// close on error
		var error = "${error}";
		if (parseInt(error) == 1) {
			// parentWindow.location.href = "./landing.do";
           	window.close();
		}
		
		// close on success
		setTimeout(function() {
			// parentWindow.location.href = "./landing.do";
			window.close();
		}, 3000);
	});
	</script>
</body>
</html>