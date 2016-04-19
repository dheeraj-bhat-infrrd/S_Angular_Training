<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.login.title.key" /></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
</head>
<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
	<div class="reg-left-container col-lg-6 col-md-6 col-sm-12 col-xs-12">
		<div class="reg-ss-logo"></div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<div class="reg-create-account">Create an account</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<input class=" reg-name" type="text" placeholder="first name" />
					<input class=" reg-name" style="margin-left: 10px;" type="text" placeholder="second name" />
				</div>
				<div class="reg-big-input">
					<input class="reg-" type="text" placeholder="companyname" />
				</div>
				<div class="reg-big-input">
					<input type="email" placeholder="enter email" />
				</div>
			</div>
		</div>
	</div>
	<div class="reg-right-container col-lg-6 col-md-6 col-sm-12 col-xs-12"></div>
</div>

<body>
<%-- <script src="${initParam.resourcesPath}/resources/js/signup.js"></script> --%>
</body>
</html>
