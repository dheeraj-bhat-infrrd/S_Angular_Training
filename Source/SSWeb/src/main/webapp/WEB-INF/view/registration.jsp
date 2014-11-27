<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- JIRA : SS-26 by RM-02
	Registration page which gets loaded after clicking registration link
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.title.registerUser.key" /></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">

<script type="text/javascript">
	
</script>
</head>
<body>
	<div id="registrationMainWrapper" class="mainWrapper">
		<div class="overlay">
			<div class="formModalContainer">
				<div class="hide" id="messageHeader"></div>
				<div id="registrationContainer" class="formWrapper">
					<div id="formHeaderBar"></div>
					<div class="formBody" id="registrationBody">
						<div class="formBodyMainText">
							<spring:message code="label.registration.header.key"></spring:message>
						</div>
						<div class="formContainer">
							<form role="form" id="registerForm" method="post" action="register.do">
								<div class="form-group formInputField">
									<input id="firstname" name="firstname"
										class="form-control formInput" type="text"
										value="${firstname}" placeholder="'<spring:message code = "label.firstname.key" />'">
								</div>
								<div class="form-group formInputField">
									<input id="lastname" name="lastname"
										class="form-control formInput" type="text" value="${lastname}"
										placeholder='<spring:message code = "label.lastname.key" />'>
								</div>
								<div class="form-group formInputField">
									<input id="emailid" name="emailid"
										class="form-control formInput" type="text" value="${emailid}"
										placeholder='<spring:message code = "label.emailid.key" />'>
								</div>
								<div class="form-group formInputField">
									<input id="username" name="username"
										class="form-control formInput" type="text"
										placeholder='<spring:message code = "label.username.key" />'>
								</div>
								<div class="form-group formInputField">
									<input id="password" name="password"
										class="form-control formInput" type="password"
										placeholder='<spring:message code = "label.password.key" />'>
								</div>
								<div class="form-group formInputField">
									<input id="confirmpassword" name="confirmpassword"
										class="form-control formInput" type="password"
										placeholder='<spring:message code = "label.confirmpassword.key" />'>
								</div>
								<input type="hidden" value="${emailid}" name="originalemailid"
									id="originalemailid">
								<button class="formButton" id="formSubmit"><spring:message code="label.submit.key"/></button>
							</form>
						</div>
					</div>
				</div>
				<div class="formModalFooter clearfix">
					<div class="floatLeft formModalFooterContent">
						<spring:message code="label.forgotpassword.key" />?
					</div>
					<div class="floatRight formModalFooterContent">
						<span><spring:message code="label.alreadyhaveanacoount.key" />?</span>
						<a href="#" class="loginLink"><spring:message
								code="label.login.title.key" /></a>
					</div>
				</div>
				<div class="formPageFooter">Copyright © 2014 Social Survey.
					All rights reserved.</div>
			</div>
		</div>
	</div>
</body>
</html>