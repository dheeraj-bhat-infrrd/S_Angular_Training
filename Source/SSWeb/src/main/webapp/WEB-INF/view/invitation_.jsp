<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- JIRA : SS-17 by RM-06
	Invitation page to send user invite to register for the application 	
-->
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title><spring:message code="label.title.invitation.key" /></title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/bootstrapValidator.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/registerScript.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
    <div class="login-main-wrapper padding-001 login-wrapper-min-height">
        <div class="container login-container">
            <div class="row login-row">
                <div class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
                    <div class="logo login-logo"></div>
                    <div class="login-txt text-center font-24 margin-bot-20">Login To Do Your Survey</div>
                    <div class="login-input-wrapper margin-0-auto clearfix">
                        <div class="float-left login-wrapper-icon"></div>
                        <input class="float-left login-wrapper-txt" placeholder="First Name">
                    </div>
                    <div class="login-input-wrapper margin-0-auto clearfix">
                        <div class="float-left login-wrapper-icon"></div>
                        <input class="float-left login-wrapper-txt" placeholder="Last Name">
                    </div>
                    <div class="login-input-wrapper margin-0-auto clearfix">
                        <div class="float-left login-wrapper-icon"></div>
                        <input class="float-left login-wrapper-txt" placeholder="Email ID">
                    </div>
                    <div class="login-input-wrapper margin-0-auto clearfix">
                        <div>CAPTCHA</div>
                    </div>
                    <div class="btn-submit margin-0-auto cursor-pointer font-18 text-center">SUBMIT</div>
                </div>
                <div class="login-footer-wrapper login-footer-txt clearfix margin-0-auto col-xs-12">
                    <div class="float-left cursor-pointer">Forgot password?</div>
                    <div class="float-right">Already have an account? <span class="cursor-pointer"><strong>Login</strong></span></div>
                </div>
                <div class="footer-copyright">Copyright &copy; Social Survey. All rights reserved.</div>                
            </div>
        </div>
    </div>
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
    
    <script>
        $(document).ready(function(){
            adjustOnResize();
            
            $(window).resize(adjustOnResize);
            
            function adjustOnResize(){
                var winH2 = $(window).height()/2;
                var conH2 = $('.login-row').height()/2;
                var offset = winH2 - conH2;
                if(offset > 25){
                    $('.login-row').css('margin-top',offset+'px');
                }
            }
        });
    </script>
    
</body>
</html>