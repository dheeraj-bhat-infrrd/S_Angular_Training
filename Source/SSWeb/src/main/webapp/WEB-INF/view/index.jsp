<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Professional Reputation Management | SocialSurvey.me</title>
	<meta name="norton-safeweb-site-verification" 
 		 content="s97qgdn0xldafh23hpih0sd0qpc19jjqnb37xio342-fltqdwg1jjoe2o5mnkfonqua26k1hukucj52b7c6r8-2ts8zcwvh-zv42i5rcg06bimirv1mykg0ucoupvxr3" />
	<meta name="keywords"
		content="socialsurvey.me, socialsurvey, professional, online, reputation management, social, survey, reviews, rating">
	<meta name="description"
		content="Rate and review professionals online. Engaging customers and enhance your online reputation. Create a winning social strategy and boost customer satisfaction.">
	<link rel="canonical" href="https://socialsurvey.me">
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
</head>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<c:if test="${not empty user }">
	<c:redirect url="/userlogin.do"></c:redirect>
</c:if>

<div id="toast-container" class="toast-container">
   <span id="overlay-toast" class="overlay-toast"></span>
</div>

<body class="index-body">
	<div class="body-wrapper">
		<div class="container">
			
			<div class="header-row clearfix">
				<div id="header-search-icn" class="header-search-icn"></div>
				<div class="float-left header-logo-wrapper ss-logo"></div>
				<div id="pro-wrapper-top" class="pro-wrapper-top pro-wrapper clearfix hide float-left"></div>
				
				<div class="float-right header-login-wrapper clearfix">
					<div id="" class="pro-left-wrapper pro-left-wrapper-login hide"><div class="fp-txt-1">Login</div></div>
			   	 	
			   	 	<form id="login-form" method="POST" action="j_spring_security_check">
						<div class="float-left h-email-wrapper">
							<div class="h-lbl">email address</div>
							<div class="h-inp-wrapper">
								<input type="email" class="h-inp" placeholder="Email Address" id="login-user-id" name="j_username">
							</div>
						</div>
						<div class="float-left h-pwd-wrapper">
							<div class="h-lbl clearfix">
								<div class="float-left">Password</div>
								<div class="float-right fgt-pwd"><span id="forgot-pwd" onclick ="openForgotPasswordPage();">Forgot Password?</span></div>
							</div>
							<div class="h-inp-wrapper">
								<input type="password" class="h-inp" placeholder="Password" id="login-pwd" name="j_password">
							</div>
						</div>
						<div class="float-left h-login-btn-wrapper">
							<div id="login-submit" class="h-btn-login">
								<span class="h-btn-login-txt">Login</span>
							</div>
						</div>
					</form>
					
				</div>
			</div>
			
			<div class="hero-txt-wrapper">
                <span style="color: #333;">Enterprise Reputation Management</span><br/>
                <span> <a target="_blank" class="anchor-home" style="font-size: 20px; color: #2a6496;" href="https://www.socialsurvey.com/scott-harris-talks-about-socialsurvey-2/">To Learn More Click Here</a></span>
			</div>
			<div class="content-wrapper clearfix">
				<div class="float-left c-pic-panel">
					<div class="clearfix c-pic-panel-top">
						<div class="float-left c-pic-top-1">
							<div class="blue-ext-left"></div>
							<div class="c-panel-txt-top txt-bold">Manage Your Online Reputation</div>
							<div class="c-panel-txt-bot">We offer companies and quality professionals a simple way to manage their online reputation and capture customer data to create a winning social strategy and boost customer satisfaction.</div>
						</div>
						<div class="float-left c-pic-img c-pic-top-2">
							<div class="pic-bot-txt">
								<div class="pic-bot-txt-top">Faramarz Moeen Ziai</div>
								<div class="pic-bot-txt-bot">SVP. Mortgage</div>
							</div>
						</div>
					</div>
					<div class="clearfix c-pic-panel-bot">
						<div class="float-left c-pic-img c-pic-bot-1">
							<div class="pic-bot-txt">
								<div class="pic-bot-txt-top">John Jackson</div>
								<div class="pic-bot-txt-bot">Counselor, Professional Services</div>
							</div>
						</div>
						<div class="float-left c-pic-img c-pic-bot-2">
							<div class="pic-bot-txt">
								<div class="pic-bot-txt-top">Tyler Morton</div>
								<div class="pic-bot-txt-bot">Broker-Owner, Real Estate</div>
							</div>
						</div>
						<div class="float-left c-pic-img c-pic-bot-3">
							<div class="pic-bot-txt">
								<div class="pic-bot-txt-top">Laura Ryan</div>
								<div class="pic-bot-txt-bot">Director. Real Estate</div>
							</div>
						</div>
                        <div class="float-left c-pic-img c-pic-bot-4">
							<div class="pic-bot-txt">
								<div class="pic-bot-txt-top">David Kawata</div>
								<div class="pic-bot-txt-bot">CEO, Software</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="float-left c-reg-panel pos-relative">
					<div id="reg-err-pu" class="reg-err-pu hide">
						<span id="reg-err-pu-msg" class="reg-err-pu-msg"></span>
						<span id="reg-err-pu-close" class="reg-err-pu-close">`</span>
					</div>
					
			   		<form id="registration-form" class="registration-form-cls" method="POST" action="./registration.do">
						<div class="reg-txt-top1">Start Your Free Trial.</div>
						<div class="reg-txt-top2">Sign up Now. It takes less than 2 minutes.</div>
						<div class="clearfix reg-item reg-item-bord-top">
							<input id="reg-fname" name="firstName" class="reg-inp reg-inp-1 reg-inp-1-adj" value="${firstname}" placeholder="First Name">
							<input id="reg-lname" name="lastName" class="reg-inp reg-inp-2" value="${lastname}" placeholder="Last Name">
						</div>
						<div class="clearfix reg-item">
							<input type="email" name="emailId" id="reg-email" class="reg-inp reg-inp-3" value="${emailid}" placeholder="Email">
						</div>
						<div class="clearfix reg-captcha-wrapper reg-item">
							<div class="g-recaptcha" data-sitekey="6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K"></div>
						</div>
						<div class="clearfix reg-item">
							<input id="reg-submit"  type="submit" class="reg-inp reg-btn" value="Join Now">
						</div>
                        <div class="disclaimer">* By joining, I agree to Social-Survey's terms and conditions.</div>
						<input type="hidden" value="${message}" name="message" id="message"/>
					</form>
					
				</div>
			</div>
			
			<div id="pro-wrapper" class="pro-wrapper clearfix">
				<div id="pro-list-con" class="pro-left-wrapper float-left">
					<div class="blue-ext-right blue-ext-right-adj hide"></div>
					<div class="fp-txt-1">Find a Professional</div>
				</div>
				<form id="find-pro-form" method="GET" action="./findapro.do">
					<div class="pro-right-wrapper clearfix float-left">
						<div class="blue-ext-right"></div>
						<input id="find-pro-first-name" name="find-pro-first-name" class="pro-inp" placeholder="First Name">
						<input id="find-pro-last-name" name="find-pro-last-name" class="pro-inp" placeholder="Last Name">
						<input id="find-pro-submit" type="button" class="pro-inp pro-btn" value="Search">
					</div>
				</form>
			</div>
			
			<div id="footer-wrapper" class="footer-wrapper">
				&copy; Copyright 2019. All Rights Reserved.<br/>
				Created by BuyersRoad, Inc. in San Francisco 
				<span class="footer-ul">
					&bull; <a href="https://www.socialsurvey.com/privacy-policy/">Privacy Policy</a>
					&bull; <a href="https://www.socialsurvey.com/pricing/">Pricing</a>
					&bull; <a href="https://www.socialsurvey.com/features/">Features</a><br/>
					&bull; Report Bugs to <a href="mailto:support@socialsurvey.me">support@socialsurvey.me</a>
					&bull; Press Inquiries: <a href="mailto:pr@socialsurvey.me">pr@socialsurvey.me</a><br/>
					&bull; <a href="https://www.socialsurvey.com/terms-of-use/">Terms of Service</a>
				</span>
			</div>
		</div>
	</div>
 
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script>
	if (!window.jQuery) { document.write('<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js""><\/script>'); }
</script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src='//www.google.com/recaptcha/api.js' defer="defer" async="async"></script>
<script>
$(document).ready(function(){
	initializeHomePage();
});
</script>

</body>
</html>