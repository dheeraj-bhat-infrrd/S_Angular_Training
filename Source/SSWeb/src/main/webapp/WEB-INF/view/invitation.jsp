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
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
    <div class="overlay-loader hide"></div>
	<div class="login-main-wrapper invitation-min-height padding-001 login-wrapper-min-height">
		<div class="container login-container">
			<div class="row login-row">
				<div id="message-header"></div>
				<form id="invitation-form">
					<div id="inv-form"
						class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
						<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
						<div class="login-txt text-center font-24 margin-bot-20">
							<spring:message code="label.signupstartjourney.key" />
						</div>
                        <div id="serverSideerror" class="validation-msg-wrapper" >
                            <!--Use this container to input all the messages from JS and server-->
                        </div>
                        <div id="jsError" class="validation-msg-wrapper hide">
                            <!--Use this container to input all the messages from JS and server-->
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
							<input class="float-left login-wrapper-txt" id="inv-fname"
								data-non-empty="true" name="firstName"
								placeholder='<spring:message code="label.firstname.key"/>'>
						</div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-lname"></div>
							<input class="float-left login-wrapper-txt" id="inv-lname"
								data-non-empty="true" name="lastName"
								placeholder='<spring:message code="label.lastname.key"/>'>
						</div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-email"></div>
							<input class="float-left login-wrapper-txt" id="inv-email"
								data-non-empty="true" data-email = "true" name="emailId"
								placeholder='<spring:message code="label.emailid.key"/>'>
						</div>
						<!-- <div class="login-input-wrapper margin-0-auto clearfix"></div> -->
						<div class="btn-submit margin-0-auto cursor-pointer font-18 text-center" id="inv-submit">
							<spring:message code="label.submit.key" />
						</div>
                        <div id="message-header" class="error-msg hide"></div>
                        <div class="hide have-account-mobile cursor-pointer margin-bottom-15">
                            <spring:message code="label.alreadyhaveanacoount.key" />
						  ? 
                        <span class="cursor-pointer">
                            <a class="login-link" href="./login.do"><strong><spring:message code="label.login.key" /></strong></a>
                        </span>
                        </div>
					</div>
				</form>
				<div
					class="login-footer-wrapper login-footer-txt clearfix margin-0-auto margin-bottom-50 col-xs-12">
					<div class="float-right">
						<spring:message code="label.alreadyhaveanacoount.key" />
						  ? 
                        <span class="cursor-pointer">
                            <a class="login-link" href="./login.do"><strong><spring:message code="label.login.key" /></strong></a>
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

	<script
		src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
	<%-- <script type="text/javascript"
	src="https://www.google.com/recaptcha/api/challenge?k=6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/captchaScript.js"></script> --%>

	<script>
		$(document)
				.ready(
						function() {
							adjustOnResize();

							$(window).resize(adjustOnResize);

							function adjustOnResize() {
								var winH2 = $(window).height() / 2;
								var conH2 = $('.login-row').height() / 2;
								var offset = winH2 - conH2;
								if (offset > 25) {
									$('.login-row').css('margin-top',
											offset + 'px');
								}
							}

							/*
								Function for submitting invitation form
							 */
							function submitInvitationForm() {
								console
										.log("Method to submit Invitation form called");
								if (!$('#message-header').hasClass("hide"))
									$('#message-header').addClass("hide");
								var url = "./corporateinvite.do";
								callAjaxFormSubmit(url,
										submitInvitationFormCallBack,
										"invitation-form");
								console
										.log("Method to submit Invitation form finished");
							}

							/*
								Call back function for submitting invitation form
							 */
							function submitInvitationFormCallBack(data) {
								$('#message-header').html(data);
								$('#message-header').removeClass("hide");
								if ($('#message-header').find('div').hasClass(
										'success-message')) {
									$('#invitation-form')[0].reset();
								}
								//Recaptcha.reload();
							}

							$('#inv-submit').click(function(e) {
								if (validateForm('inv-form')) {
									/* ===== FORM VALIDATED ===== */
									console.log("form validated !!");
									submitInvitationForm();
								}
							});

						});
	</script>

</body>
</html>