<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message
		code="label.completeregistration.title.key" /></title>
<link rel="shortcut icon"
	href="${pageContext.request.contextPath}/favicon.ico" sizes="16x16">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>
<body>
	<div id="toast-container" class="toast-container">
		<span id="overlay-toast" class="overlay-toast"></span>
	</div>
	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
			<div class="float-right clearfix hdr-btns-wrapper">
				<div class="float-left hdr-log-btn hdr-log-reg-btn">
					<spring:message code="label.signin.key" />
				</div>
				<div class="float-left hdr-reg-btn hdr-log-reg-btn">
					<spring:message code="label.joinus.key" />
				</div>
			</div>
		</div>
	</div>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="hm-header-row-left text-center">
					<spring:message code="label.completeregistration.title.key" />
				</div>
			</div>
		</div>
	</div>
	
	<div id="survey-invite-success" class="prof-main-content-wrapper margin-top-25 margin-bottom-25 hide">
		<div class="container">
			<div class="inv-succ-text1 text-center">GREAT!</div>
			<div class="inv-succ-text2 text-center">Now, check your email</div>
			<div class="inv-succ-email text-center">sent to:${customerEmail}</div>
			<div class="inv-succ-text3">We just sent you a link to access the requested survey.</div>
			<div class="inv-succ-link" onclick="resendSurveyActivationLink();">Resend access link</div>
		</div>
	</div>
	<div id="survey-invite-fail" class="prof-main-content-wrapper margin-top-25 margin-bottom-25 hide">
		<div class="container">
			<div class="inv-succ-text1 text-center">OOPS! It looks like you have already taken a survey for ${agentName}.</div>
			<div class="inv-succ-text2 text-center">Are you trying to amend a prior response? If so click the link below and we will email you the access required</div>
			<div id='changeSurvey' onclick="retakeSurveyLink();" class="inv-succ-link">Link to resend origin Survey Responses</div>
		</div>
	</div>
	<div style="display:none;">
		<form id="survey-form">
			<input type="text" name="agentId" value="${agentId}">
			<input type="text" name="agentName" value="${agentName}">
			<input type="text" name="firstName" value="${firstname}">
			<input type="text" name="lastName" value="${lastname}">
			<input type="text" name="customerEmail" value="${customerEmail}">
			<input type="text" name="relationship" value="${relation}">
		</form>
	</div>
	<script
		src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
	<script>
		if (!window.jQuery) {
			document.write('<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js""><\/script>');
		}
	</script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			
			var surveyCompleted = "${surveyCompleted}";
			if(surveyCompleted == 'yes'){
				$('#survey-invite-fail').removeClass('hide');
			}
			else{
				$('#survey-invite-success').removeClass('hide');
			}
		});
		function resendSurveyActivationLink() {
			var payload = $('#survey-form').serialize();
			$.ajax({
				url : window.location.origin + "/rest/survey/resendsurveylink",
				data : payload,
				type : "POST",
				dataType : "TEXT",
				success : function(){
					$('#overlay-toast').html("We just sent you a link to access the requested survey.");
					showToast();
				},
				error : function(e){
					console.log(e);
				}
			});
		}
		
		function retakeSurveyLink(){
			var payload = $('#survey-form').serialize();
			$.ajax({
				url : window.location.origin + "/rest/survey/restartsurvey",
				data : payload,
				type : "GET",
				dataType : "TEXT",
				success : function(){
					$('#overlay-toast').html('Mail sent to your registered email id for retaking the survey for ' + "${agentName}");
					showToast();
				},
				error : function(e){
					console.log(e);
				}
			});
		}
	</script>
</body>
</html>