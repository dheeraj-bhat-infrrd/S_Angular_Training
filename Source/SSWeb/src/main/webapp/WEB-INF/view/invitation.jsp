<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body>
	<div id="overlay-toast" class="overlay-toast">Sample error
		message</div>
	<div class="overlay-loader hide"></div>
	<div class="login-main-wrapper invitation-min-height padding-001">
		<div class="container login-container">
			<div class="row login-row">
				<form id="invitation-form">
					<div id="inv-form"
						class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
						<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
						<div class="login-txt text-center font-24 margin-bot-20">
							<spring:message code="label.signupstartjourney.key" />
						</div>
						<div id="serverSideerror" class="validation-msg-wrapper">
							<!--Use this container to input all the messages from server-->
							<jsp:include page="messageheader.jsp" />
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
							<div class="float-left login-wrapper-icon icn-fname"></div>
							<input class="float-left login-wrapper-txt" id="inv-fname"
								data-non-empty="true" name="firstName"
								placeholder='<spring:message code="label.firstname.key"/>'>
						</div>
						<div id="inv-page-firstname" class="input-error-2 margin-0-auto">Please
							enter the above field</div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-lname"></div>
							<input class="float-left login-wrapper-txt" id="inv-lname"
								data-non-empty="true" name="lastName"
								placeholder='<spring:message code="label.lastname.key"/>'>
						</div>
						<div id="inv-page-lastname" class="input-error-2 margin-0-auto">Please
							enter the above field</div>
						<div class="login-input-wrapper margin-0-auto clearfix">
							<div class="float-left login-wrapper-icon icn-email"></div>
							<input class="float-left login-wrapper-txt" id="inv-email"
								data-non-empty="true" data-email="true" name="emailId"
								placeholder='<spring:message code="label.emailid.key"/>'>
						</div>
						<div id="inv-page-email" class="input-error-2 margin-0-auto">Please
							enter the above field</div>
						<div
							class="btn-submit margin-0-auto cursor-pointer font-18 text-center"
							id="inv-submit">
							<spring:message code="label.submit.key" />
						</div>
						<div
							class="hide have-account-mobile cursor-pointer margin-bottom-15">
							<spring:message code="label.alreadyhaveanacoount.key" />
							? <span class="cursor-pointer"> <a class="login-link"
								href="./login.do"><strong><spring:message
											code="label.login.key" /></strong></a>
							</span>
						</div>
					</div>
				</form>
				<div
					class="login-footer-wrapper login-footer-txt clearfix margin-0-auto margin-bottom-50 col-xs-12">
					<div class="float-right">
						<a class="login-link" href="./jumptodashboard.do"><spring:message
								code="label.alreadyhaveanacoount.key" /></a> ? <span
							class="cursor-pointer"> <a class="login-link"
							href="./login.do"><strong><spring:message
										code="label.login.key" /></strong></a>
						</span>
					</div>
				</div>
				<div class="footer-copyright text-center">
					 <spring:message code="label.copyright.key"/> 
					&copy; <span id="ss-cc-year"></span>
					<spring:message code="label.footer.socialsurvey.key"/> 
					<span class="center-dot">.</span> 
					<spring:message code="label.allrightscopyright.key"/>
				</div>
			</div>
		</div>
	</div>

	<script
		src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script
		src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
	<script type="text/javascript"
		src="${initParam.resourcesPath}/resources/js/common.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/script.js"></script>

	<script>
		$(document)
				.ready(
						function() {
							var curDate = new Date();
							$('#ss-cc-year').html(curDate.getFullYear());
							
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
								if (!$('#serverSideerror').hasClass("hide"))
									$('#serverSideerror').addClass("hide");
								var url = "./corporateinvite.do";
								showOverlay();
								callAjaxFormSubmit(url,
										submitInvitationFormCallBack,
										"invitation-form");
							}

							/*
								Call back function for submitting invitation form
							 */
							function submitInvitationFormCallBack(data) {
								hideOverlay();
								$('#serverSideerror').html(data);
								$('#serverSideerror').removeClass("hide");
								if ($('#serverSideerror').find('div').hasClass(
										'success-message')) {
									$('#invitation-form')[0].reset();
								}
								//Recaptcha.reload();
							}

							$('#inv-submit').click(function(e) {
								if (validateForm('inv-form')) {
									/* ===== FORM VALIDATED ===== */
									submitInvitationForm();
								}
							});

						});
	</script>

</body>
</html>