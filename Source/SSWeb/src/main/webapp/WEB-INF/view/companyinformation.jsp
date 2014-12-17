<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message code="label.title.settings.key" /></title>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
    <div class="overlay-loader hide"></div>
	<div class="login-main-wrapper padding-001 company-wrapper-min-height">
		<div class="container login-container">
			<div class="row login-row">
				<form id="company-info-form" method="POST" action="./addcompanyinformation.do">
					<div id="company-info-div" class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
						<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
						<div class="login-txt text-center font-24 margin-bot-20">
							<spring:message code="label.companysettings.header.key"/>
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
							<div class="float-left login-wrapper-icon icn-company"></div>
							<input class="float-left login-wrapper-txt" id="com-company" data-non-empty="true" name="company" placeholder='<spring:message code="label.company.key"/>'>
						</div>
						<div class="login-input-wrapper margin-0-auto clearfix pos-relative input-file-company">
							<div class="float-left login-wrapper-icon icn-lname input-file-icn-left" id="input-file-icn-left"></div>
							<input type="file" class="float-left login-wrapper-txt txt-company-logo input-file-text" id="com-logo" data-non-empty="true" name="logo" placeholder='Logo'>
                            <div class="float-right input-icon-internal icn-file" id="icn-file"></div>
						</div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-address"></div>
							<input class="float-left login-wrapper-txt" id="com-address1" data-non-empty="true" name="address1" placeholder='<spring:message code="label.address1.key"/>'>
						</div>
                        <div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-address"></div>
							<input class="float-left login-wrapper-txt" id="com-address2" name="address2" placeholder='<spring:message code="label.address2.key"/>'>
						</div>
                        <div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-zip"></div>
							<input class="float-left login-wrapper-txt" id="com-zipcode" data-non-empty="true" data-zipcode = "true" name="zipcode" placeholder='<spring:message code="label.zipcode.key"/>'>
						</div>
                        <div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-contact"></div>
							<input class="float-left login-wrapper-txt" id="com-contactno" data-non-empty="true" data-phone = "true" name="contactno" placeholder='<spring:message code="label.companycontactno.key"/>'>
						</div>
						<div class="btn-submit margin-0-auto cursor-pointer font-18 text-center" id="company-info-submit">
                            <spring:message code="label.done.key" />
						</div>
					</div>
					<input type="hidden" value="${emailid}" name="originalemailid" id="originalemailid">
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
					<spring:message code="label.copyright.key" />
					&copy;
					<spring:message code="label.copyrightposttext.key" />
				</div>
                
			</div>
		</div>
	</div>

	<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>

	<script>
		$(document).ready(function() {
			adjustOnResize();

			$(window).resize(adjustOnResize);

			function adjustOnResize() {
				var winH = $(window).height();
				var winH2 = winH / 2;
				var conH2 = $('.login-row').height() / 2;
				var offset = winH2 - conH2;
				if (offset > 25) {
					$('.login-row').css('margin-top', offset + 'px');
				}
			}
			
			function submitCompanyInfoForm() {
				console.log("submitting company information form");
				$('#company-info-form').submit();
			}
            
            $('#icn-file').click(function(){
                $('#com-logo').trigger('click');
            });
            
            $('#company-info-submit').click(function() {
            	if (validateForm('company-info-div')) {
            		/* ===== FORM VALIDATED ===== */
					submitCompanyInfoForm();
				}
            });
			
		});
	</script>

</body>
</html>