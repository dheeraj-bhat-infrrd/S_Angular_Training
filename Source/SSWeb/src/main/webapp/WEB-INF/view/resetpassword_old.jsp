<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Set new password</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
	<div id="overlay-toast" class="overlay-toast"></div>
    <div class="overlay-loader hide"></div>
    <div class="login-main-wrapper padding-001 login-wrapper-min-height">
        <div class="container login-container">
            <div class="row login-row">
            	<form id="reset-pwd-form" method="POST" action="./setnewpassword.do">
	                <div id="reset-pwd-div" class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
	                    <div class="logo login-logo margin-bottom-25 margin-top-25"></div>
	                    <div class="login-txt text-center font-24 margin-bot-20">Set new password</div>
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
	                        <div class="float-left login-wrapper-icon icn-user-id"></div>
	                        <input class="float-left login-wrapper-txt" id="login-user-id" data-non-empty="true" data-email = "true" name="emailId" value="${emailId }" placeholder='<spring:message code="label.emailid.key"/>'>
	                    </div>
	                    <div id="login-page-username" class="login-reg-err margin-0-auto"></div>
	                    <div class="login-input-wrapper margin-0-auto clearfix">
	                        <div class="float-left login-wrapper-icon icn-password"></div>
	                        <input type="password" class="float-left login-wrapper-txt" id="login-pwd" data-non-empty="true" name="password" placeholder='<spring:message code="label.password.key" />'>
	                    </div>
	                    <div id="login-page-pwd" class="login-reg-err margin-0-auto"></div>
	                    <div class="login-input-wrapper margin-0-auto clearfix">
	                        <div class="float-left login-wrapper-icon icn-confirm-password"></div>
	                        <input type="password" class="float-left login-wrapper-txt" id="login-cnf-pwd" data-non-empty="true" name="confirmPassword" placeholder='<spring:message code="label.confirmpassword.key" />'>
	                    </div>
	                    <div id="login-page-cnf-pwd" class="login-reg-err margin-0-auto"></div>
	                    <div class="btn-submit margin-0-auto cursor-pointer font-18 text-center" id="reset-pwd-submit"><spring:message code="label.submit.key"/></div>
	                </div>
	                <input type="hidden" value="${param.q}" name="q">
               	 </form>
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
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
    
    <script>
    	var isResetFormValid;
        $(document).ready(function(){
        	$(document).attr("title", "Reset Password");
            adjustOnResize();
            isResetFormValid = false;
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
            	if(validateResetPasswordForm('reset-pwd-form')){
                	$('#reset-pwd-form').submit();
                }
            }
            
            $('input').keypress(function(e){
	        	// detect enter
	        	if (e.which==13){
	        		e.preventDefault();
	        		submitResetPasswordForm();
	        	}
			});
            
            $('#login-user-id').blur(function() {
				validateEmailId(this.id);
			});
            
            $('#login-pwd').blur(function() {
				validatePassword(this.id);
			});
            
            $('#login-cnf-pwd').blur(function() {
				validateConfirmPassword('login-pwd',this.id);
			});
            
            function validateResetPasswordForm(id) {
            	var isFocussed = false;
            	isResetFormValid = true;
            	var isSmallScreen = false;
            	if($(window).width()<768){
            		isSmallScreen = true;
            	}
            	if(!validateEmailId('login-user-id')){
            		isResetFormValid = false;
            		if(!isFocussed){
            			$('#login-user-id').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isResetFormValid;
            		}
            	}
            	if(!validatePassword('login-pwd')){
            		isResetFormValid = false;
            		if(!isFocussed){
            			$('#login-pwd').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isResetFormValid;
            		}
            	}
            	if(!validateConfirmPassword('login-pwd', 'login-cnf-pwd')){
            		isResetFormValid = false;
            		if(!isFocussed){
            			$('#login-cnf-pwd').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isResetFormValid;
            		}
            	}
            	return isResetFormValid;
			}
            
            $('#reset-pwd-submit').click(function(e){
            	submitResetPasswordForm();
            });
            
        });
    </script>
    
</body>
</html>