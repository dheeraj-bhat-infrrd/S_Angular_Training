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
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body>
<div id="overlay-toast" class="overlay-toast"></div>
<div class="overlay-loader hide"></div>
<div class="login-main-wrapper padding-001 login-wrapper-min-height">
	<div class="container login-container">
		<div class="row login-row">
			<div class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
				<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
				<div class="login-txt text-center font-24 margin-bot-20">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
						<div id="page"></div>
							<c:choose>
								<c:when test="${message == 1}"><spring:message code="label.waitmessage.key" /></c:when>
							</c:choose>
						</div>
					</div>
					<div style="font-size: 11px; text-align: center;"></div>
				</div>
				<div class="footer-copyright text-center">
					<spring:message code="label.copyright.key" />&copy;
					<spring:message code="label.footer.socialsurvey.key" /><span class="center-dot">.</span>
					<spring:message code="label.allrightscopyright.key" />
				</div>
			</div>
		</div>
	</div>
</div>

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
$(document).ready(function() {
	// Onload before auth Url
	var waitMessage = "${message}";
	if (parseInt(waitMessage) == 1) {
		var authUrl = "${authUrl}";
		if (authUrl != null) {
			location.href = authUrl;
		}
		else {
			console.log("authUrl not found!");
		}
	}
	
	// select parent Window
	var parentWindow;
	if (window.opener != null && !window.opener.closed) {
		parentWindow = window.opener;
	}
	else {
		console.log("Unable to access parent window!");
	}
	
	var radioButtonDiv= $("<div style='text-align:left;margin-left:130px;'>")
	<c:forEach var="page" items="${pageNames}" varStatus="loop">
		radioButtonDiv.append('<input type="radio" name="pageselection" value="${loop.index}"/>'+"${page.name}"+" <br/>");
	</c:forEach>
	$("#page").append(radioButtonDiv);
	
	var saveButton= $("<div class='reg_btn'>save</div>");
	<c:if test="${not empty pageNames}">
		$("#page").append(saveButton);
	</c:if>
	
	saveButton.click(function() {
		var selectedPage=$('input:radio[name=pageselection]:checked').val();
		var selectedAccessFacebookToken;
		var selectedProfileUrl;
		<c:forEach var="page" items="${pageNames}"  varStatus="loop">
		  if("${loop.index}" == selectedPage){
			  selectedProfileUrl= "${page.profileUrl}";
			  selectedAccessFacebookToken= "${page.accessToken}";
		  }
		</c:forEach>
		var facebookToken = {
			'selectedAccessFacebookToken' : selectedAccessFacebookToken,
			'selectedProfileUrl' :  selectedProfileUrl
		};
		$.ajax({
			url : './saveSelectedAccessFacebookToken.do',
			type : "GET",
			data : facebookToken,
			async : false,
			complete :function(e){
				setTimeout(function() {
					window.close();
				}, 3000);
			},
			error : function(e) {
				if(e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				redirectErrorpage();
			}
		});
    });
});

$(window).on('unload', function(){
	var parentWindow = null;
	if (window.opener != null && !window.opener.closed) {
		parentWindow = window.opener;
	}
	var fromDashboard = "${fromDashboard}";
	var restful = "${restful}";
	var flow = "${socialFlow}";
	if(fromDashboard == 1){
		var columnName = "${columnName}";
		var columnValue = "${columnValue}";
		parentWindow.showDashboardButtons(columnName, columnValue);
	}
	else if(restful != "1"){
		if (flow == "registration") {
			var payload = {
				'socialNetwork' : "linkedin"
			};
			fetchSocialProfileUrl(payload, function(data) {
				parentWindow.showLinkedInProfileUrl(data);
				parentWindow.showProfileLink("linkedin", data);
			});
		}
		else {
			var payload = {
				'socialNetwork' : "${socialNetwork}"
			};
			fetchSocialProfileUrl(payload, function(data) {
				if(data.statusText == 'OK'){
				//	parentWindow.loadSocialMediaUrlInSettingsPage();
					//parentWindow.showProfileLink("${socialNetwork}", data.responseText);
					parentWindow.showProfileLinkInEditProfilePage("${socialNetwork}", data.responseText);					
				}
			});
		}
	}
});

function fetchSocialProfileUrl(payload, callBackFunction){
	$.ajax({
		url : './profileUrl.do',
		type : "GET",
		data : payload,
		async : false,
		complete : callBackFunction,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}
</script>

</body>
</html>