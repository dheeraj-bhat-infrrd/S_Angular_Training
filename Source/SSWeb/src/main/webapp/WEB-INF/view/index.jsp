<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
    <script type="text/javascript" src="https://www.google.com/recaptcha/api/challenge?k=6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-"></script>
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
	                            <input class="float-left reg-cap-txt" name="captchaResponse" placeholder="Type the above text" autocomplete="off" autocorrect="off" autocapitalize="off">
	                            <div class="clearfix reg-btns-wrapper float-right">
	                                <div class="float-left reg-cap-img reg-cap-reload"></div>
	                                <div class="float-left reg-cap-img reg-cap-sound"></div>
	                                <div class="float-left reg-cap-img reg-cap-info"></div>
	                            </div>
	                        </div>
	                    </div>
	                    <div id="recaptcha" class="hide"></div>
	                    <div class="clearfix reg-item">
	                        <input id="reg-submit"  type="submit" class="reg-inp reg-btn" value="Submit">
	                    </div>
                    </form>
                </div>
            </div>
            
			<div class="pro-wrapper clearfix">
				<div class="pro-left-wrapper float-left">Find a professional</div>
				<form id="find-pro-form" method="POST" action="./findapro.do">
					<div class="pro-right-wrapper clearfix float-left">
						<div class="blue-ext-right"></div>
						<input id="find-pro-first-name" name="find-pro-first-name" class="pro-inp" placeholder="First Name">
						<input id="find-pro-last-name" name="find-pro-last-name" class="pro-inp" placeholder="Last Name">
						<input id="find-pro-submit" type="button" class="pro-inp pro-btn" value="Submit">
					</div>
				</form>
			</div>
            
            <div class="footer-wrapper">
                    &copy; Copyright 2015. All Rights Reserved.
            </div>
        </div>

    </div>
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script-1.1.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/index.js"></script>
    <script>
        $(document).ready(function(){
        	
        	var captchaText=true;
        	
            resizeFunc();
            $(window).resize(resizeFunc);
            
            function resizeFunc(){
                var winW = $(window).width();
                if(winW < 768){
                    var offset = winW - 114 - 20;
                    $('.reg-cap-txt').css('max-width',offset+'px');
                }
            }
            
            console.log("Loading captcha");
            Recaptcha.create('6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-', 'recaptcha', {
            	theme : 'white',
            	callback : captchaLoaded
            	});
            console.log("Captcha loaded");
            
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
				event.preventDefault();
				submitRegistrationForm();
			});
            
            $('#find-pro-submit').click(function() {
            	event.preventDefault();
            	submitFindProForm();
            });
            
            // Form validation for login page
            $('#login-user-id').blur(function() {
            	validateUserId(this.id);
            });
            $('#login-pwd').blur(function(){
            	validateLoginPassword(this.id);
            });
            
            // Functions to trigger form validation of various input elements
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
				console.log("submitting login form");
				if (validateLoginForm('login-form')) {
					$('#login-form').submit();
					showOverlay();
				}
			}

			function submitRegistrationForm() {
				console.log("submitting registration form");
				if(validatePreRegistrationForm('reg-form')){
					$('#registration-form').submit();
					showOverlay();
				}
			}
			function captchaLoaded() {
				/* $('#registerForm').on('added.field.bv',function(e, data) {
						// The field "recaptcha_response_field" has just been
						// added
						if (data.field === 'recaptcha_response_field') {
						// Find the icon
						var $parent = data.element.parents('.form-group'), $icon = $parent
						.find('.form-control-feedback[data-bv-icon-for="'
						+ data.field + '"]');
						// Move icon to other position
						$icon.insertAfter('#recaptcha');
						}
				}); */
				
				var imgData = $(".recaptcha_image_cell").html();
				console.log("Captcha image data : " + imgData);
				$(".reg-captcha-img").html(imgData);
				
			}
			
			$(".reg-cap-reload").click(function(){
				console.log("Captcha reload button clicked");
				$("#recaptcha_reload").click();
				console.log("Initiated the click of hidden reload");
			});
			
			$(".reg-cap-sound").click(function(){
				if(captchaText == true){
					console.log("Captcha sound button clicked");
					$("#recaptcha_switch_audio").click();
					console.log("Initiated the click of hidden sound");
					captchaText=false;
				}
				else{
					console.log("Captcha text button clicked");
					$("#recaptcha_switch_img").click();
					console.log("Initiated the click of hidden text");
					captchaText=true;
				}
				
			});
			
			$(".reg-cap-info").click(function(){
				console.log("Info button clicked");
				$("#recaptcha_whatsthis").click();
			});
            
            function submitFindProForm() {
				console.log("Submitting Find a Profile form");
				if(validateFindProForm('find-pro-form')){
					$('#find-pro-form').submit();
				}
				showOverlay();
			}
        });
    </script>
</body>
</html>