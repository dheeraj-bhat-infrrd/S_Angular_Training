<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.resetpassword.key"></spring:message></title>
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
		$('#resetPasswordForm')
				.bootstrapValidator(
						{
							feedbackIcons : {
								valid : 'glyphicon glyphicon-ok',
								invalid : 'glyphicon glyphicon-remove',
								validating : 'glyphicon glyphicon-refresh'
							},
							fields : {
								emailId : {
									validators : {
										notEmpty : {
											message : 'The email ID is required'
										},
										regexp : {
											regexp : /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/,
											message : 'Email address not valid'
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
										},
										identical : {
											field : 'confirmPassword',
											message : "Passwords do not match"
										}
									}
								},
								confirmPassword : {
									validators : {
										identical : {
											field : 'password',
											message : "Passwords do not match"
										}
									}
								}
							}
						});

	});
</script>
</head>
<body>
	<div id="resetPasswordMainWrapper" class="mainWrapper">
		<div class="overlay">
			<div class="formModalContainer">
				<div class="hide" id="messageHeader"></div>
				<div id="resetPasswordContainer" class="formWrapper">
					<div id="formHeaderBar"></div>
					<div class="formBody" id="invitationBody">
						<div class="formBodyMainText">
							<spring:message code="label.title.resetpassword.key"></spring:message>
						</div>
						<div class="formContainer">
							<form role="form" id="resetPasswordForm" method="post" action="./setnewpassword.do">
								<div class="form-group formInputField">
									<input id="emailId" name="emailId"
										class="form-control formInput" type="email"
										placeholder="Email ID">
								</div>
								<div class="form-group formInputField">
									<input id="password" name="password"
										class="form-control formInput" type="password"
										placeholder="New Password">
								</div>
								<div class="form-group formInputField">
									<input id="confirmPassword" name="confirmPassword"
										class="form-control formInput" type="password"
										placeholder="Confirm New Password">
								</div>
								<input type="hidden" value="${param.q}" name="q">
								<button type="submit" class="formButton" id="resetPasswordFormSubmit">Submit</button>
							</form>
						</div>
					</div>
				</div>
				<div class="formPageFooter">Copyright © 2014 Social Survey.
					All rights reserved.</div>
			</div>
		</div>
	</div>
</body>
</html>