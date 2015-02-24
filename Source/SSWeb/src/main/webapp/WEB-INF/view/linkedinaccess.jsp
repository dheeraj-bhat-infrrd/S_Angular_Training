<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message
		code="label.completeregistration.title.key"></spring:message></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
	<div id="overlay-toast" class="overlay-toast"></div>
	<div id="overlay-loader" class="overlay-loader hide"></div>
	<div class="login-main-wrapper padding-001 login-wrapper-min-height">
		<div class="container login-container">
			<div class="row login-row">
				<div id="reset-pwd-div"
					class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
					<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
					<div class="login-txt text-center font-24 margin-bot-20">
						<spring:message code="label.linkedin.access.key"></spring:message>
					</div>

					<div style="padding: 0px 20px;" class="clearfix">
						<div
							style="margin-bottom: 30px; font-size: 15px; text-align: center; padding: 0px 20px;">
							<spring:message code="label.allowaccess.message" />
						</div>
						<input id="allow-access" type="button"
							onclick="redirectToAuthPage();" class="btn-payment float-left"
							value='<spring:message code="label.allowaccess.key"/>' /> <input
							id="skip-button" type="button" class="btn-payment float-right"
							value='<spring:message code="label.skip.key"/>' />
					</div>

					<div class="footer-copyright text-center">
						<spring:message code="label.copyright.key" />
						&copy;
						<spring:message code="label.footer.socialsurvey.key" />
						<span class="center-dot">.</span>
						<spring:message code="label.allrightscopyright.key" />
					</div>
				</div>
			</div>
		</div>
	</div>

	<script
		src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>

	<script>
		
		
		function redirectToAuthPage() {
			window.open("./linkedinauthpage.do","Authorization Page","width=600,height=600,scrollbars=yes");
		}

		$("#skip-button").click(function() {
			location.href = "./landing.do";
		});
	</script>
</body>
</html>