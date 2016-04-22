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
<body>
<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="padding:0px;">
	<div class="reg-left-container col-lg-6 col-md-6 col-sm-12 col-xs-12">
		<div class="reg-ss-logo"></div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12" style="margin-top:20px;">
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 reg-create-account">Create an account</div>
			<div class="reg-req-field col-lg-12 col-md-12 col-sm-12 col-xs-12" style="margin-top:30px;">Your Name *</div>
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<input class=" reg-name" type="text" placeholder="First Name" />
					<input class=" reg-name float-right" style="margin-left: 2%;" type="text" placeholder="Last Name" />
				</div>
				<div class="reg-req-field col-lg-12 col-md-12 col-sm-12 col-xs-12">Company Name *</div>
				<div class=" col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<input class="reg-details" type="text" placeholder="Company Name" />
				</div>
				<div class="reg-req-field col-lg-12 col-md-12 col-sm-12 col-xs-12">Email Address *</div>
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<input class="reg-details" type="email" placeholder="Email" />
				</div>
				<div class="reg-pswd-info col-lg-12 col-md-12 col-sm-12 col-xs-12">(Don't worry about setting a password right now,we'll email you a link to create one)</div>
				<div class="clearfix reg-captcha-wrapper reg-item" style="width: 305px;
    margin: auto;">
							<div class="g-recaptcha" data-sitekey="6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K" ></div>
						</div>
						<div class="clearfix" style="width:260;margin:25px auto;">
						<div class="float-left reg-pln-btn" id="reg-start-individual">Start<br/> Individual</div>
						<div class="float-left reg-pln-btn" id="reg-start-business" style="margin-left: 10px;">Start<br/> Business</div>
						</div>
						
			</div>
		</div>
	</div>
	<div class="reg-right-container col-lg-6 col-md-6 col-sm-12 col-xs-12"></div>
</div>


<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script src='//www.google.com/recaptcha/api.js' defer="defer" async="async"></script>
 <script src="${initParam.resourcesPath}/resources/js/signup.js"></script> 
 <script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
</body>
</html>
