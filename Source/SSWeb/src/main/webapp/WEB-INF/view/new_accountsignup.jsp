<%-- <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="signup_header.jsp" />
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.5/angular.min.js"></script>
<div id="signup-content" style="padding:0px;height:871px;" ng-app="SocialSurvey">
	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="padding:0px;" ng-controller="SignUp">
		<div class="reg-left-container col-lg-6 col-md-6 col-sm-12 col-xs-12">
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12"
				style="margin-top: 20px;">
				<div
					class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reg-create-account" style="float: none;">Quick
					setup</div>
				<div class="reg-linkedin-border">
				<div class="reg-auth-linkedin-wrapper">
					<div class="reg-linkedin-imp">Important your Profile from LinedIn</div>
					<div class="reg-linkedin-imp-info">If you already have a LinkedIn profile, let us autofill
						most of your profile for you.</div>
					<div class="reg-auth-linkedin-btn" id="reg-auth-linkedin">Authenticate
						your LinkedIn account</div>
				</div>
				</div>
				<div class="clearfix">
					<div class="float-left"></div>
					<div class="float-left">or</div>
					<div class="float-left"></div>
				</div>
				<div>Click next to manually enter all profile information.</div>
			</div>
			<div>Next</div>
		</div>

		<div class="reg-right-container col-lg-6 col-md-6 col-sm-12 col-xs-12" style="height:872px;"></div>
	</div>

</div>
<jsp:include page="scripts.jsp"/>


<jsp:include page="footer.jsp" /> --%>

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
<body ng-app="SocialSurvey">


<div ng-view></div>

</div>
</body>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.5/angular.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.2.1/angular-route.min.js"></script>
<script src='//www.google.com/recaptcha/api.js' defer="defer" async="async"></script>
 <script src="${initParam.resourcesPath}/resources/js/signup.js"></script> 
 <script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
</body>
</html>