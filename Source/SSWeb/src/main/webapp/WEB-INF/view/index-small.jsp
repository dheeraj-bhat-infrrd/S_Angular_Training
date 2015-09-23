<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
<!--
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
-->
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1-small.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1-small.css">
</head>
<body class="index-body">
    
    <div class="body-wrapper">
        
        <div class="container">
            
            <div class="header-row clearfix">
                <div class="float-left header-logo-wrapper ss-logo"></div>
                <div class="float-right header-login-wrapper clearfix">
               	 	<form id="login-form" method="POST" action="j_spring_security_check">
	                    <div class="float-left h-email-wrapper">
	                        <div class="h-lbl">email address</div>
	                        <div class="h-inp-wrapper">
	                            <input type="text" class="h-inp" placeholder="Username" id="login-user-id" name="j_username">
	                        </div>
	                    </div>
	                    <div class="float-left h-pwd-wrapper">
	                        <div class="h-lbl clearfix">
	                            <div class="float-left">Password</div>
	                            <div class="float-right fgt-pwd"><a href ="./forgotpassword.do">Forgot your Password?</a></div>
	                        </div>
	                        <div class="h-inp-wrapper">
	                            <input type="password" class="h-inp" placeholder="Password" id="login-pwd" name="j_password">
	                        </div>
	                    </div>
	                    <div class="float-left h-login-btn-wrapper">
	                        <div id="login-submit" class="h-btn-login"></div>
	                    </div>
                    </form>
                </div>
            </div>
            
            <div class="hero-txt-wrapper"><span class="txt-bold">Control</span> your online <span class="txt-bold">presence, read, write</span> and <span class="txt-bold">Share reviews</span></div>
            
            <div class="content-wrapper clearfix">
                <div class="float-left c-pic-panel">
                    <div class="clearfix c-pic-panel-top">
                        <div class="float-left c-pic-top-1">
                        	<div class="blue-ext-left"></div>
                            <div class="c-panel-txt-top txt-bold">Lorem Ipsum Dore It</div>
                            <div class="c-panel-txt-bot">Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It Lorem Ipsum Dore It </div>
                        </div>
                        <div class="float-left c-pic-img c-pic-top-2">
                            <div class="pic-bot-txt">
<!--
                                <div class="pic-bot-txt-top">John Doe</div>
                                <div class="pic-bot-txt-bot">CEO</div>
-->
                            </div>
                        </div>
                    </div>
                    <div class="clearfix c-pic-panel-bot">
                        <div class="float-left c-pic-img c-pic-bot-1">
                            <div class="pic-bot-txt">
<!--
                                <div class="pic-bot-txt-top">John Doe</div>
                                <div class="pic-bot-txt-bot">CEO</div>
-->
                            </div>
                        </div>
                        <div class="float-left c-pic-img c-pic-bot-2">
                            <div class="pic-bot-txt">
<!--
                                <div class="pic-bot-txt-top">John Doe</div>
                                <div class="pic-bot-txt-bot">CEO</div>
-->
                            </div>
                        </div>
                        <div class="float-left c-pic-img c-pic-bot-3">
                            <div class="pic-bot-txt">
<!--
                                <div class="pic-bot-txt-top">John Doe</div>
                                <div class="pic-bot-txt-bot">CEO</div>
-->
                            </div>
                        </div>
                    </div>
                </div>
                <div class="float-left c-reg-panel">
               		<form id="registration-form" method="POST" action="./registration.do">
	                    <div class="reg-txt-top1">Get Started - It's Free</div>
	                    <div class="reg-txt-top2">Sign Up to Start Your Survey</div>
	                    <div class="clearfix reg-item">
	                        <input id="reg-fname" name="firstName" class="reg-inp reg-inp-1" placeholder="First Name">
	                        <input id="reg-lname" name="lastName" class="reg-inp reg-inp-2" placeholder="Last Name">
	                    </div>
	                    <div class="clearfix reg-item">
	                        <input type="text" name="emailId" id="reg-email" class="reg-inp reg-inp-3" placeholder="Email">
	                    </div>
	                    <div class="clearfix reg-captcha-wrapper reg-item">
	                        <div class="reg-captcha-img"></div>
	                        <div class="reg-captcha-btns clearfix">
	                            <input class="float-left reg-cap-txt" placeholder="Type the above text">
	                            <div class="clearfix reg-btns-wrapper float-right">
	                                <div class="float-left reg-cap-img reg-cap-reload"></div>
	                                <div class="float-left reg-cap-img reg-cap-sound"></div>
	                                <div class="float-left reg-cap-img reg-cap-info"></div>
	                            </div>
	                        </div>
	                    </div>
	                    <div class="clearfix reg-item">
	                        <input id="reg-submit"  type="button" class="reg-inp reg-btn" value="Submit">
	                    </div>
                    </form>
                </div>
            </div>
            
            <div class="pro-wrapper clearfix">
                <div class="pro-left-wrapper float-left">Find a professional</div>
                <div class="pro-right-wrapper clearfix float-left">
                	<div class="blue-ext-right"></div>
                    <input class="pro-inp" placeholder="First Name">
                    <input class="pro-inp" placeholder="Last Name">
                    <input type="button" class="pro-inp pro-btn" value="Submit">
                </div>
            </div>
            
            <div class="footer-wrapper">
                    &copy; Copyright 2015. All Rights Reserved.
            </div>
            
        </div>

    </div>
    
    <script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/script-1.1.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/common.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/script.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/index.js"></script>
    <script>
        $(document).ready(function(){
            resizeFunc();
            $(window).resize(resizeFunc);
            
            function resizeFunc(){
                var winW = $(window).width();
                if(winW < 768){
                    var offset = winW - 114 - 20;
                    $('.reg-cap-txt').css('max-width',offset+'px');
                }
            }
            
            /* $('#login-form input').keypress(function(e){
	        	// detect enter
	        	if (e.which==13){
	        		e.preventDefault();
	        		loginUser();
	        	}
			});
            
            $('#registration-form input').keypress(function(e){
	        	// detect enter
	        	if (e.which==13){
	        		e.preventDefault();
	        		submitRegistrationForm();
	        	}
			}); */
            
            $('#login-submit').click(function(){
                loginUser();
            });
           
            $('#reg-submit').click(function() {
				submitRegistrationForm();
			});
            
            /**
            *Form validation for login page
            */
            $('#login-user-id').blur(function() {
            	validateUserId(this.id);
            });
            $('#login-pwd').blur(function(){
            	validateLoginPassword(this.id);
            });
            
            /* ==Functions to trigger form validation of various input elements== */
			$('#reg-fname').blur(function() {
				validateRegFirstName(this.id);
			});
			
			$('#reg-lname').blur(function() {
				validateRegLastName(this.id);
			});
			
			$('#reg-email').blur(function() {
				validateRegEmailId(this.id);
			});
			
			
			function loginUser() {
				if (validateLoginForm('login-form')) {
					$('#login-form').submit();
					showOverlay();
				}
			}

			function submitRegistrationForm() {
				if(validatePreRegistrationForm('reg-form')){
					$('#registration-form').submit();
					showOverlay();
				}
			}
            
        });
    </script>
    
</body>
</html>