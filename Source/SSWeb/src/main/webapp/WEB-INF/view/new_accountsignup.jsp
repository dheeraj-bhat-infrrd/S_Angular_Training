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
<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/intlTelInput.css">
<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/angular-dropdowns.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/dropzone.css">
<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/countrySelect.css">
<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/payment.css">
<link href='http://fonts.googleapis.com/css?family=Lato:400,700' rel='stylesheet' type='text/css'>
<script src="https://www.google.com/recaptcha/api.js?onload=vcRecaptchaApiLoaded&render=explicit" async defer></script>
</head>
<script type="text/javascript">
	var userId = eval('(' + '${userId}' + ')');
	var companyId = eval('(' + '${companyId}' + ')');
</script>
<body ng-app="SocialSurvey" ng-controller="newSignupController">
	<div class="overlay-loader hide"></div>
	<div id="toast-container" class="toast-container">
		<span id="overlay-toast" class="overlay-toast"></span>
	</div>
	<div ng-view></div>
</body>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.5/angular.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.1/angular-route.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/intlTelInput.js"></script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src="${initParam.resourcesPath}/resources/js/angular-recaptcha.js"></script>
<script src="${initParam.resourcesPath}/resources/js/jquery.mask.js"></script>
<script src="${initParam.resourcesPath}/resources/js/countrySelect.js"></script>
<script src="${initParam.resourcesPath}/resources/js/app.js"></script>
<script src="${initParam.resourcesPath}/resources/js/controllers/signup.js"></script>
<script src="${initParam.resourcesPath}/resources/js/services/login.js"></script>
<script src="${initParam.resourcesPath}/resources/js/angular-dropdowns.js"></script>
<script src="https://js.braintreegateway.com/v2/braintree.js"></script>
<script src="${initParam.resourcesPath}/resources/js/phoneFormat.js"></script>
<script src="${initParam.resourcesPath}/resources/js/card-validator/card-validator.min.js"></script>
</body>
</html>