<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message code="label.title.registeruser.key" /></title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
    <div id="overlay-toast" class="overlay-toast"></div>
    <div class="overlay-loader hide"></div>
	<div class="login-main-wrapper padding-001 registration-wrapper-min-height">
		<div class="container login-container">
			<div class="row login-row">
				<form id="registration-form" method="POST" action="./register.do">
					<div id="reg-form"
						class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
						<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
						<div class="login-txt text-center font-24 margin-bot-20">
							<spring:message code="label.signuptostartsurvey.key" />
						</div>
                        <div id="serverSideerror" class="validation-msg-wrapper" >
                            <!--Use this container to input all the messages from server-->
                            <jsp:include page="messageheader.jsp"/>
                        </div>
                        <div id="jsError" class="validation-msg-wrapper hide">
                            <!--Use this container to input all the messages from JS-->
                            <div class="error-wrapper clearfix">
                                <div class="float-left msg-err-icn jsErrIcn"></div>
                                <div class="float-left msg-err-txt-area">
                                    <div class="err-msg-area">
                                        <div class="err-msg-con">
                                            <p id="jsErrTxt"></p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-fname"></div>
							<input class="float-left login-wrapper-txt" id="reg-fname"
								data-non-empty="true" name="firstname" value="${firstname}"
								placeholder='<spring:message code="label.firstname.key" />'>
						</div>
                        <div id="reg-page-firstname" class="login-reg-err  margin-0-auto"></div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-lname"></div>
							<input class="float-left login-wrapper-txt" id="reg-lname" name="lastname" value="${lastname}"
								placeholder='<spring:message code="label.lastname.key" />'>
						</div>
                        <div id="reg-page-lastname" class="login-reg-err  margin-0-auto"></div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-email"></div>
							<c:choose>
								<c:when test="${not empty emailid}">
								<input class="float-left login-wrapper-txt" id="reg-email" readonly="readonly" data-non-empty="true" name="emailid"
									value="${emailid}" placeholder='<spring:message code="label.emailid.key" />'>
								</c:when>
								<c:otherwise>
									<input class="float-left login-wrapper-txt" id="reg-email" data-non-empty="true" data-email = "true" name="emailid"
									 placeholder='<spring:message code="label.emailid.key" />'>
								</c:otherwise>
							</c:choose>
						</div>
                        <div id="reg-page-email" class="login-reg-err  margin-0-auto"></div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-password"></div>
							<input type="password" class="float-left login-wrapper-txt"
								id="reg-pwd" data-non-empty="true" name="password" placeholder='<spring:message code="label.password.key" />'>
						</div>
                        <div id="reg-page-password" class="login-reg-err  margin-0-auto"></div>
                        <div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-confirm-password"></div>
							<input type="password" class="float-left login-wrapper-txt" id="reg-conf-pwd" data-non-empty="true" name="confirmpassword"
								placeholder='<spring:message code="label.confirmpassword.key" />'>
						</div>
                        <div id="reg-page-conf-password" class="login-reg-err  margin-0-auto"></div>
						<div class="btn-submit margin-0-auto cursor-pointer font-18 text-center" id="reg-submit">
							<spring:message code="label.submit.key" />
						</div>
					</div>
					<input type="hidden" value="${emailid}" name="originalemailid" id="originalemailid"/>
					<input type="hidden" value = "${isDirectRegistration}" name="isDirectRegistration" id="isDirectRegistration"/>
				</form>
				<div class="login-footer-wrapper login-footer-txt clearfix margin-0-auto margin-bottom-50 col-xs-12">
					<div class="float-right">
						<spring:message code="label.alreadyhaveanacoount.key" />?
						<span class="cursor-pointer">
							<a class="login-link" href="./login.do">
								<strong><spring:message code="label.login.key" /></strong>
							</a>
						</span>
					</div>
				</div>
				<div class="footer-copyright text-center">
					<spring:message code="label.copyright.key"/> 
					&copy; 
					<spring:message code="label.footer.socialsurvey.key"/> 
					<span class="center-dot">.</span> 
					<spring:message code="label.allrightscopyright.key"/>
				</div>
			</div>
		</div>
	</div>

	<script	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>

	<script>
		var isRegistrationFormValid;
		$(document).ready(function() {
			isRegistrationFormValid=false;
//			adjustOnResize();
//			$(window).resize(adjustOnResize);

			function adjustOnResize() {
				var winH = $(window).height();
				var winH2 = winH / 2;
				var conH2 = $('.login-row').height() / 2;
				var offset = winH2 - conH2;
				if (offset > 25) {
					$('.login-row').css('margin-top', offset + 'px');
				}
			}

			function submitRegistrationForm() {
				console.log("submitting registration form");
				if(validateRegistrationForm('reg-form')){
					$('#registration-form').submit();
					showOverlay();
				}
			}
			
			$('input').keypress(function(e){
	        	// detect enter
	        	if (e.which==13){
	        		e.preventDefault();
	        		submitRegistrationForm();
	        	}
			});

			$('#reg-submit').click(function(e) {
				submitRegistrationForm();
			});
			
			/* ==Functions to trigger form validation of various input elements== */
			$('#reg-fname').blur(function() {
				validateFirstName(this.id);
			});
			
			$('#reg-lname').blur(function() {
				validateLastName(this.id);
			});
			
			$('#reg-email').blur(function() {
				validateEmailId(this.id);
			});
			
			$('#reg-pwd').blur(function() {
				validatePassword(this.id);
			});
			
			$('#reg-conf-pwd').blur(function(){
				validateConfirmPassword('reg-pwd',this.id);
			});
			
			function validateRegistrationForm(id) {
				//hide the server error
            	$("#serverSideerror").hide();
            	isRegistrationFormValid = true;
            	var isFocussed = false;
            	var isSmallScreen = false;
            	if($(window).width()<768){
            		isSmallScreen = true;
            	}
            	//Validate form input elements
				if(!validateFirstName('reg-fname')){
					isRegistrationFormValid = false;
					if(!isFocussed){
            			$('#reg-fname').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isRegistrationFormValid;
            		}
				}
				if(!validateLastName('reg-lname')){
					isRegistrationFormValid = false;
					if(!isFocussed){
            			$('#reg-lname').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isRegistrationFormValid;
            		}
				}
				if(!validateEmailId('reg-email')){
					isRegistrationFormValid = false;
					if(!isFocussed){
            			$('#reg-email').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isRegistrationFormValid;
            		}
				}
				if(!validatePassword('reg-pwd')){
					isRegistrationFormValid = false;
					if(!isFocussed){
            			$('#reg-pwd').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isRegistrationFormValid;
            		}
				}
				if(!validateConfirmPassword('reg-pwd', 'reg-conf-pwd')){
					isRegistrationFormValid = false;
					if(!isFocussed){
            			$('#reg-conf-pwd').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isRegistrationFormValid;
            		}
				}
            	return isRegistrationFormValid;
			}

		});
	</script>

</body>
</html>