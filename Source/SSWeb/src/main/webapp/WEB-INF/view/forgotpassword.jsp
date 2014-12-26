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
	<div id="overlay-toast" class="overlay-toast">Sample error message</div>
    <div class="overlay-loader hide"></div>
    <div class="login-main-wrapper padding-001 login-wrapper-min-height">
        <div class="container login-container">
            <div class="row login-row">
            	<form id="forgot-pwd-form" method="POST" action="./sendresetpasswordlink.do">
	                <div id="forgot-pwd-div" class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
	                    <div class="logo login-logo margin-bottom-25 margin-top-25"></div>
	                    <div class="login-txt text-center font-24 margin-bot-20"><spring:message code="label.forgotpassword.key"/></div>
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
	                        <input class="float-left login-wrapper-txt" id="login-user-id" data-non-empty="true" data-email="" name="emailId" placeholder='<spring:message code="label.emailid.key"/>'>
	                    </div>
	                    <div id="login-page-username" class="input-error-2 margin-0-auto">Please enter the above field</div>
	                    <div class="btn-submit margin-0-auto cursor-pointer font-18 text-center" id="forgot-pwd-submit"><spring:message code="label.submit.key"/></div>
	                </div>
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
    	var isForgotPasswordFormValid;
        $(document).ready(function(){
        	isForgotPasswordFormValid=false;
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
            
            function submitForgotPasswordForm() {
            	console.log("submitting forgot password form");
            	if(validateForgotPasswordForm('forgot-pwd-form')){
            		$('#forgot-pwd-form').submit();
                }
            }
            
            $('#forgot-pwd-submit').click(function(e){
                	submitForgotPasswordForm();
            });
            
            $('#login-user-id').blur(function() {
				validateEmailId(this.id);
			});
            
            function validateForgotPasswordForm(id) {
            	var isFocussed = false;
            	var isSmallScreen = false;
            	isForgotPasswordFormValid = true;
            	if($(window).width()<768){
            		isSmallScreen = true;
            	}
            	if(!validateEmailId('login-user-id')){
					isForgotPasswordFormValid = false;
					if(!isFocussed){
            			$('#login-user-id').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isForgotPasswordFormValid;
            		}
            	}
            	return isForgotPasswordFormValid;
			}
            
        });
    </script>
    
</body>
</html>