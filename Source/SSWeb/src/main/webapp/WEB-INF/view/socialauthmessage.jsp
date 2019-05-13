 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.completeregistration.title.key"></spring:message></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body>
<div id="overlay-toast" class="overlay-toast"></div>
<div class="overlay-loader hide"></div> 
<div class="login-main-wrapper padding-001 login-wrapper-min-height">
	<div class="container login-container">
		<div class="row login-row">
			<div class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
				<div class="ss-logo-blue login-logo margin-bottom-25 margin-top-25"></div>
				<div class="login-txt text-center font-24 margin-bot-20">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
							<c:choose>
								<c:when test="${success == 1}"><spring:message code="label.authorization.success" /></c:when>
								<c:when test="${image == 1}"><spring:message code="label.authorization.success.sm.image" /></c:when>
								<c:when test="${nogoogleplusfound == 1}"><spring:message code="label.no.google.found" /></c:when>
								<c:when test="${message == 1}"><spring:message code="label.waitmessage.key" /></c:when>
								<c:otherwise><spring:message code="label.authorization.failure" /></c:otherwise>
							</c:choose>
						</div>
					</div>
					<div style="font-size: 11px; text-align: center;">
						<c:choose>
							<c:when test="${success == 1}"><spring:message code="label.timer.key" /></c:when>
							<c:when test="${message == 1}"></c:when>
							<c:otherwise><spring:message code="label.timer.key" /></c:otherwise>
						</c:choose>
					</div>
				</div>

				<div class="footer-copyright text-center">
					<spring:message code="label.copyright.key" />&copy; <span id="ss-cc-year"></span>
					<spring:message code="label.footer.socialsurvey.key" /><span class="center-dot">.</span>
					<spring:message code="label.allrightscopyright.key" />
				</div>
			</div>
		</div>
	</div>
</div>


 <script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script> 
<script>
 $(document).ready(function() {
	var curDate = new Date();
	$('#ss-cc-year').html(curDate.getFullYear());
	var profImage = "${profileImage}";
	
	var waitMessage = "${message}";
	if (parseInt(waitMessage) == 1) {
		var authUrl = "${authUrl}";
		if (authUrl != null) {
			location.href = authUrl;
			return false;
		}
	}
	
	// select parent Window
	var parentWindow;
	if (window.opener != null && !window.opener.closed) {
		parentWindow = window.opener;
		if(profImage != null && profImage != undefined && profImage != ''){
			parentWindow.showProfileImageForSurvey(profImage);
		}
	}
	
	// close on error
	var error = "${error}";
	if (parseInt(error) == 1) {
		setTimeout(function() {
			window.close();
		}, 3000);
	}
	
	// close on success
	setTimeout(function() {
		window.close();
	}, 3000);
});
 
 var isImagePopup = "${isImagePopup}";
 if(isImagePopup == '' || isImagePopup == null || isImagePopup == undefined){
$(window).on('unload', function(){

	var parentWindow = null;
	if (window.opener != null && !window.opener.closed) {
		parentWindow = window.opener;
	}
	
	var fromDashboard = "${fromDashboard}";
	var restful = "${restful}";
	var flow = "${socialFlow}";
	var isFixSocialMedia ="${isFixSocialMedia}";
	var waitMessage = "${message}";
	var isManual = "${isManual}"
	
	var payloadForLinkedInPopup;
	if (flow == "registration") {
		payloadForLinkedInPopup = "linkedin";
	}else{
		payloadForLinkedInPopup = "${socialNetwork}";
	}
	
	if(payloadForLinkedInPopup == 'linkedin'){
		parentWindow.fetchSocialProfileUrl("linkedin",function(data){
			if(data.status == 200){
				parentWindow.$('#linked-in-prof-url-popup').attr('data-fromDashboard',"${fromDashboard}");
				parentWindow.$('#linked-in-prof-url-popup').attr('data-restful',"${restful}");
				parentWindow.$('#linked-in-prof-url-popup').attr('data-socialFlow',"${socialFlow}");
				parentWindow.$('#linked-in-prof-url-popup').attr('data-isFixSocialMedia',"${isFixSocialMedia}");
				parentWindow.$('#linked-in-prof-url-popup').attr('data-message',"${message}");
				parentWindow.$('#linked-in-prof-url-popup').attr('data-isManual',"${message}");
				parentWindow.$('#linked-in-prof-url-popup').attr('data-columnName',"${fromDashboard}");
				parentWindow.$('#linked-in-prof-url-popup').attr('data-columnValue',"${columnValue}");
				
				var responseObj = JSON.parse(data.responseText);
				
				var profileUrlLink = responseObj.url;
				
				
				if(responseObj.connected){
					parentWindow.showProfileLinkInEditProfilePage("${socialNetwork}", profileUrlLink);
					$('.social-item-icon[data-source="${socialNetwork}"').attr('data-connected', true)
				} else {
					parentWindow.removeProfileLinkInEditProfilePage("${socialNetwork}");
					$('.social-item-icon[data-source="${socialNetwork}"').attr('data-connected', false)
				}
				
				if(profileUrlLink == '' || profileUrlLink == null || profileUrlLink == undefined || profileUrlLink.length <=0){
					parentWindow.$('#linked-in-popup-text').html('We are sorry we cannot find the Linkedin URL for your profile, please provide the url in the following format "https://www.linkedin.com/in/esanchezmtg"');
					parentWindow.$('#linked-in-prof-url-popup-remove').parent().hide();
				}else{
					parentWindow.$('#linked-in-popup-text').html('Please confirm your LinkedIn Profile Url');
					parentWindow.$('#linked-in-popup-inp').val(profileUrlLink);
					parentWindow.$('#linked-in-prof-url-popup-remove').parent().show();
				}
				
				parentWindow.$('#linked-in-prof-url-popup-main').show();
			}
		});
	}else{
		updateUIForSocialMedia();
	}
	
	function updateUIForSocialMedia(){
		var fromDashboard = "${fromDashboard}";
		var restful = "${restful}";
		var flow = "${socialFlow}";
		var isFixSocialMedia ="${isFixSocialMedia}";
		var waitMessage = "${message}";
		var isManual = "${isManual}"
		
		if(fromDashboard == 1){
			var columnName = "${columnName}";
			var columnValue = "${columnValue}";
			
			if(isFixSocialMedia != undefined && isFixSocialMedia == 1 && parseInt(waitMessage) != 1 && isManual == "true"){
				parentWindow.fixSocialMediaResponse(columnName, columnValue);
			}
			
			parentWindow.showDashboardButtons(columnName, columnValue);
		}
		else if(restful != "1"){
			if (flow == "registration") {
				parentWindow.fetchSocialProfileUrl("linkedin", function(data) {
					if(data.status == 200){
						var responseObj = JSON.parse(data.responseText);
						var profileUrlLink = responseObj.url;
						parentWindow.showLinkedInProfileUrl(profileUrlLink);
						parentWindow.showProfileLink("linkedin", profileUrlLink);
					}
				});
			}
			else {
				var socialNetwork = "${socialNetwork}";
				parentWindow.fetchSocialProfileUrl(socialNetwork, function(data){
					if(data.status == 200){

						parentWindow.loadSocialMediaUrlInPopup();
						parentWindow.loadSocialMediaUrlInSettingsPage();
						
						var responseObj = JSON.parse(data.responseText);
						
						var profileUrlLink = responseObj.url;
						
						if(responseObj.connected){
							parentWindow.showProfileLinkInEditProfilePage("${socialNetwork}", profileUrlLink);
						} else {
							parentWindow.removeProfileLinkInEditProfilePage("${socialNetwork}");
						}
					}
				});
			}
		}
	}
});
 }
</script>

