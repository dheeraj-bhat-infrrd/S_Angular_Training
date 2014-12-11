<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.login.title.key"></spring:message></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/bootstrapValidator.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/loginScript.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<script>
	$(function() {
		//Bootstap form validator
		$('#loginForm')
				.bootstrapValidator(
						{
							feedbackIcons : {
								valid : 'glyphicon glyphicon-ok',
								invalid : 'glyphicon glyphicon-remove',
								validating : 'glyphicon glyphicon-refresh'
							},
							fields : {
								userId : {
									validators : {
										notEmpty : {
											message : 'The username is required'
										}
									}
								},
								password : {
									validators : {
										notEmpty : {
											message : 'The password is required'
										},
										stringLength : {
											max : 15,
											min : 5,
											message : "Password must be between 5 and 15 characters"
										}
									}
								}
							}
						});

	});
</script>
</head>
<body>
	<div id="loginMainWrapper" class="mainWrapper">
		<div class="overlay">
			<div class="formModalContainer">
				<div class="hide" id="messageHeader"></div>
				<div id="loginContainer" class="formWrapper">
					<div id="formHeaderBar"></div>
					<div class="formBody" id="invitationBody">
						<div class="formBodyMainText">
							<spring:message code="label.login.key"></spring:message>
						</div>
						<div class="formContainer">
							<form role="form" id="loginForm">
								<div class="form-group formInputField">
									<input id="loginName" name="loginName" class="form-control formInput"
										type="text" placeholder="User ID">
								</div>
								<div class="form-group formInputField">
									<input id="password" name="password"
										class="form-control formInput" type="password"
										placeholder="* * * * * *">
								</div>
								<button type="button" class="formButton" id="loginFormSubmit" onclick="loginUser();">Submit</button>
							</form>
						</div>
					</div>
				</div>
				<div class="formModalFooter clearfix">
					<div class="floatLeft formModalFooterContent">
						<a href="./forgotPassword.do">Forgot Password?</a>
					</div>
					<div class="floatRight formModalFooterContent">
						<span>Don't have an account?</span> <a href="./invitation.do"
							class="loginLink">Sign Up</a>
					</div>
				</div>
				<div class="formPageFooter">Copyright © 2014 Social Survey.
					All rights reserved.</div>
			</div>
		</div>
	</div>
</body>
</html>