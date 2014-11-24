<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.title.invitation.key"/></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/bootstrapValidator.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<script type="text/javascript"
	src="https://www.google.com/recaptcha/api/challenge?k=6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/captchaScript.js"></script>
</head>
<body>
	<div id="invitationMainWrapper" class="mainWrapper">
		<div class="overlay">
			<div class="formModalContainer">
				<div id="invitationContainer" class="formWrapper">
					<div id="formHeaderBar"></div>
					<div class="formBody" id="invitationBody">
						<div class="formBodyMainText"><spring:message code="label.invitation.key"></spring:message></div>
						<div class="formContainer">
							<form role="form" id="registerForm" method="post"
								action="./corporateinvite.do">
								<div class="form-group formInputField">
									<input id="regStep1FirstName" name="firstName"
										class="form-control formInput" type="text"
										placeholder="First Name">
								</div>
								<div class="form-group formInputField">
									<input id="regStep1LastName" name="lastName"
										class="form-control formInput" type="text"
										placeholder="Last Name">
								</div>
								<div class="form-group formInputField">
									<input id="regStep1EmailId" name="emailId"
										class="form-control formInput" type="email"
										placeholder="Email ID">
								</div>
								<div class="form-group">
									<div class="col-sm-9 captchaContainer">
										<div id="recaptcha"></div>
									</div>
								</div>
								<button type="submit" class="formButton">Submit</button>
							</form>
						</div>
					</div>
				</div>
				<div class="formModalFooter clearfix">
					<div class="floatLeft formModalFooterContent">Forgot
						Password?</div>
					<div class="floatRight formModalFooterContent">
						<span>Already have an account?</span> <a href="#"
							class="loginLink">Login</a>
					</div>
				</div>
				<div class="formPageFooter">Copyright © 2014 Social Survey.
					All rights reserved.</div>
			</div>
		</div>
	</div>
</body>
</html>