<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.title.forgotpassword.key" /></title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
    <div class="login-main-wrapper padding-001 login-wrapper-min-height">
        <div class="container login-container">
            <div class="row login-row">
            	<form id="reset-pwd-form" method="POST" action="./setnewpassword.do">
	                <div id="reset-pwd-div" class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
	                    <div class="logo login-logo margin-bottom-25 margin-top-25"></div>
	                    <div class="login-txt text-center font-24 margin-bot-20"><spring:message code="label.forgotpassword.key"/></div>
	                    <div class="login-input-wrapper margin-0-auto clearfix">
	                        <div class="float-left login-wrapper-icon icn-user-id"></div>
	                        <input class="float-left login-wrapper-txt" id="login-user-id" data-non-empty="true" name="emailId" placeholder='<spring:message code="label.emailid.key"/>'>
	                    </div>
	                    <div class="login-input-wrapper margin-0-auto clearfix">
	                        <div class="float-left login-wrapper-icon icn-password"></div>
	                        <input type="password" class="float-left login-wrapper-txt" id="login-pwd" data-non-empty="true" name="password" placeholder='<spring:message code="label.password.key" />'>
	                    </div>
	                    <div class="login-input-wrapper margin-0-auto clearfix">
	                        <div class="float-left login-wrapper-icon icn-confirm-password"></div>
	                        <input type="password" class="float-left login-wrapper-txt" id="login-cnf-pwd" data-non-empty="true" name="confirmPassword" placeholder='<spring:message code="label.confirmpassword.key" />'>
	                    </div>
	                    <div class="btn-submit margin-0-auto cursor-pointer font-18 text-center" id="reset-pwd-submit"><spring:message code="label.submit.key"/></div>
                        <div id="message-header" class="error-msg"><jsp:include page="messageheader.jsp"/></div>
	                </div>
	                <input type="hidden" value="${param.q}" name="q">
               	 </form>
                <div class="footer-copyright text-center"><spring:message code="label.copyright.key" /> &copy; 
                <spring:message code="label.copyrightposttext.key" /></div>                
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
                var winH = $(window).height();
                var winH2 = winH/2;
                var conH2 = $('.login-row').height()/2;
                var offset = winH2 - conH2;
                if(offset > 25){
                    $('.login-row').css('margin-top',offset+'px');
                }
            }
            
            function submitResetPasswordForm() {
            	console.log("submitting reset password form");
            	$('#reset-pwd-form').submit();
            }
            
            $('#reset-pwd-div').click(function(e){
                if(validateForm('reset-pwd-form')){
                    /* ===== FORM VALIDATED ===== */
                	submitResetPasswordForm();
                }
            });
            
        });
    </script>
    
</body>
</html>