<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.login.title.key" /></title>
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
            	<form id="frm-login" method="POST" action="./userlogin.do">
	                <div id="login-form" class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
	                    <div class="logo login-logo margin-bottom-25 margin-top-25"></div>
	                    <div class="login-txt text-center font-24 margin-bot-20"><spring:message code="label.logintodosurvey.key"/> </div>
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
	                        <input class="float-left login-wrapper-txt" id="login-user-id" data-non-empty="true" name="loginName" data-email="true" placeholder='<spring:message code="label.username.key"/>'>
	                    </div>
                        <div id="login-page-username" class="input-error-2 margin-0-auto"></div>
	                    <div class="login-input-wrapper margin-0-auto clearfix">
	                        <div class="float-left login-wrapper-icon icn-password"></div>
	                        <input type="password" class="float-left login-wrapper-txt" id="login-pwd" data-non-empty="true" name="password" placeholder='<spring:message code="label.password.key"/>'>
	                    </div>
                        <div id="login-page-password" class="input-error-2 margin-0-auto"></div>
	                    <div class="btn-submit margin-0-auto cursor-pointer font-18 text-center" id="login-submit"><spring:message code="label.login.button.key"/></div>
	                    <div class="hide forgot-pwd-mobile"><a class="login-link" href="./forgotPassword.do"><spring:message code="label.forgotpassword.key" />?</a></div>
	                </div>
               	 </form>
                <div class="login-footer-wrapper login-footer-txt clearfix margin-0-auto margin-bottom-50 col-xs-12">
                    <div class="float-left cursor-pointer"><a class="login-link" href="./forgotPassword.do"><spring:message code="label.forgotpassword.key" />?</a></div>
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
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
    
    <script>
    var isLoginFormValid;
        $(document).ready(function(){
        	isLoginFormValid = false;
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
            
            function loginUser() {
            	console.log("submitting login form");
				if(validateLoginForm('login-form')){
					$('#frm-login').submit();
					showOverlay();
				}
            }
            
            $('input').keypress(function(e){
            	
	        	// detect enter
	        	if (e.which==13){
	        		e.preventDefault();
	        		loginUser();
	        	}
			});
            
            //Function to validate the login form
            function validateLoginForm(id){
            	//hide the server error
            	$("#serverSideerror").hide();
<<<<<<< HEAD
            	
            	/* //check if login password is valid
            	if(!validatePassword('login-pwd')){
            		return false;
            	} */
            	if (!validate) {
            		$('#jsError').show();
            		return false;
            	} else {
            		$('#jsError').hide();
            		/* Form validated. */
            		return true;
=======
            	isLoginFormValid=true;
            	var isFocussed = false;
            	var isSmallScreen = false;
            	if($(window).width()<768){
            		isSmallScreen = true;
            	}
            	if(!validateUserId('login-user-id')){
            		isLoginFormValid=false;
            		if(!isFocussed){
            			$('#login-user-id').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isLoginFormValid;
            		}
>>>>>>> master
            	}
            	if(!validateLoginPassword('login-pwd')){
            		isLoginFormValid = false;
            		if(!isFocussed){
            			$('#login-pwd').focus();
            			isFocussed=true;
            		}
            		if(isSmallScreen){
            			return isLoginFormValid;
            		}
            	}
            	return isLoginFormValid;
            }
            
            function validateUserId(elementId) {
            	
            	if($(window).width()<768){
            		if ($('#'+elementId).val() != "") {
	    				if (emailRegex.test($('#'+elementId).val()) == true) {
	    					return true;
	    				}else {
	    					$('#overlay-toast').html('Please enter a valid user name.');
	    					showToast();
	    					return false;
	    				}
	    			}else{
	    				$('#overlay-toast').html('Please enter user name.');
	    				showToast();
	    				return false;
	    			}
            	}else{
	            	if ($('#'+elementId).val() != "") {
	    				if (emailRegex.test($('#'+elementId).val()) == true) {
	    					$('#'+elementId).parent().next('.input-error-2').hide();
	    					return true;
	    				}else {
	    					$('#'+elementId).parent().next('.input-error-2').html('Please enter a valid user name.');
	    					$('#'+elementId).parent().next('.input-error-2').show();
	    					return false;
	    				}
	    			}else{
	    				$('#'+elementId).parent().next('.input-error-2').html('Please enter user name.');
	    				$('#'+elementId).parent().next('.input-error-2').show();
	    				return false;
	    			}
            	}
			}
            
            function validateLoginPassword(elementId){
            	if($(window).width()<768){
            		if ($('#'+elementId).val() != "") {
            			return true;
	    			}else{
	    				$('#overlay-toast').html('Please enter password.');
	    				showToast();    
	    				return false;
	    			}
            	}else{
	            	if ($('#'+elementId).val() != "") {
	    					$('#'+elementId).parent().next('.input-error-2').hide();
	    					return true;
	    			}else{
	    				$('#'+elementId).parent().next('.input-error-2').html('Please enter password.');
	    				$('#'+elementId).parent().next('.input-error-2').show();    
	    				return false;
	    			}
            	}
            }
            
            $('#login-submit').click(function(e){
                loginUser();
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
        });
    </script>
    
</body>
</html>