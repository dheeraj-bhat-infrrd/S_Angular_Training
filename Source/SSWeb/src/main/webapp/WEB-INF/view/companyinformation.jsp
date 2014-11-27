<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- JIRA : SS-24 by RM-02
	Settings page for capturing company details while registration
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.title.settings.key" /></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
	<div id="registrationMainWrapper" class="mainWrapper">
		<div class="overlay">
			<div class="formModalContainer">
				<div class="hide" id="messageHeader"></div>
				<div id="registrationContainer" class="formWrapper">
					<div id="formHeaderBar"></div>
					<div class="formBody" id="companyInfoBody">
						<div class="formBodyMainText">
							<spring:message code="label.companysettings.header.key"></spring:message>
						</div>
						<div class="formContainer">
							<form role="form" id="companyInfoForm" method="post"
								action="">
								<div class="form-group formInputField">
									<input id="company" name="company"
										class="form-control formInput" type="text"
										placeholder='<spring:message code="label.company.key"/>'>
								</div>
								<div class="form-group formInputField">
									<input id="logo" name="logo" class="form-control formInput"
										type="file"
										placeholder='<spring:message code="label.logo.key"/>'>
								</div>
								<div class="form-group formInputField">
									<input id="address1" name="address1"
										class="form-control formInput" type="text"
										placeholder='<spring:message code="label.address1.key"/>'>
								</div>
								<div class="form-group formInputField">
									<input id="address2" name="address2"
										class="form-control formInput" type="text"
										placeholder='<spring:message code="label.address2.key"/>'>
								</div>
								<div class="form-group formInputField">
									<input id="zipcode" name="zipcode"
										class="form-control formInput" type="text"
										placeholder='<spring:message code="label.zipcode.key"/>'>
								</div>
								<div class="form-group formInputField">
									<input id="contactno" name="contactno"
										class="form-control formInput" type="password"
										placeholder='<spring:message code="label.companycontactno.key"/>'>
								</div>
								<button class="formButton" id="formSubmit">
									<spring:message code="label.done.key" />
								</button>
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
								code="label.login.key" /></a>
					</div>
				</div>
				<div class="formPageFooter">Copyright © 2014 Social Survey.
					All rights reserved.</div>
			</div>
		</div>
	</div>
</body>
</html>