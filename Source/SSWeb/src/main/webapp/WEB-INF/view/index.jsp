<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
    <div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
    <div class="t-main-container">
        <div class="t-header-main">
            <div class="t-heade-wrapper t-container clearfix">
                <div class="t-logo float-left"></div>
                <div class="t-login-wrapper float-right clearfix">
                	<form id="login-form" method="POST" action="j_spring_security_check">
	                    <input type="text" id="login-user-id" name="j_username" class="t-inp" placeholder="Username">
	                    <input type="password" id="login-pwd" name="j_password" class="t-inp" type="password" placeholder="Password">
	                    <div id="login-submit" class="t-btn">Login</div>
                    </form>
                </div>
            </div>
        </div>
        <div class="t-pro-wrapper">
            <div class="t-container clearfix">
                <div class="float-left t-pro-txt">Looking for a Pro?</div>
                <form id="find-pro-form" method="POST" action="./findaprofile.do">
	                <div class="float-right t-pro-search">
    	                <input id="find-pro-first-name" name="find-pro-first-name" class="t-inp" placeholder="First Name">
    	                <input id="find-pro-last-name" name="find-pro-last-name" class="t-inp" placeholder="Last Name">
        	            <div id="find-pro-submit" class="t-btn">Search</div>
        	        </div>
                </form>
            </div>
        </div>
        <div class="t-main-wrapper t-container clearfix">
            <div class="float-left t-main-pic">
                <div class="t-bg-sample-img"></div>
            </div>
            <div class="float-left t-main-reg">
                <div class="t-reg-wrapper">
                	<form id="registration-form" method="POST" action="./register.do">
	                    <input type="text" id="reg-fname" name="firstname" class="t-reg-txt" placeholder="First Name" value="${firstname }">
	                    <input type="text" id="reg-lname" name="lastname" class="t-reg-txt" placeholder="Last Name" value="${lastname }">
	                    <input type="text" id="reg-email" name="emailid" class="t-reg-txt" placeholder="Email" value="${emailid }">
	                    <input type="password" id="reg-pwd" name="password" class="t-reg-txt" placeholder="Password">
	                    <input type="password" id="reg-conf-pwd" name="confirmpassword" class="t-reg-txt" placeholder="Confirm Password">
	                    <div id="reg-submit" class="t-btn-reg">Register</div>
                    </form>
                </div>
            </div>
        </div>
        <div class="footer-main-wrapper">
            <div class="t-container text-center footer-text">
                Copyright © Social Survey <span class="center-dot">.</span> All Rights Reserved.
            </div>
        </div>
    </div>
    
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/index.js"></script>
    <script>
        $(document).ready(function(){
            $('#login-form input').keypress(function(e){
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
			});
            
            $('#login-submit').click(function(){
                loginUser();
            });

            $('#reg-submit').click(function() {
            	submitRegistrationForm();
            });

            /**
            * Form validation for login page
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

            $('#reg-pwd').blur(function() {
            	validateRegPassword(this.id);
            });

            $('#reg-conf-pwd').blur(function(){
            	validateRegConfirmPassword('reg-pwd',this.id);
            });

            $('#find-pro-submit').click(function() {
            	submitFindProForm();
            });
		});
    </script>
</body>
</html>