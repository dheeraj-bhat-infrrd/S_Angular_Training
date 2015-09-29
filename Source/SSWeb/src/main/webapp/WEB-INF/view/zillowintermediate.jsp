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
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<c:if test="${not empty accountSettings}">
	<c:set var = "profile" value = "${ accountSettings }"></c:set>
</c:if>
<c:if test="${empty accountSettings}">
	<c:if test="${ not empty profileSettings }">
		<c:set var = "profile" value = "${ profileSettings }"></c:set>
	</c:if>
</c:if>
<body>
<div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
<div class="overlay-loader hide"></div>
<div class="login-main-wrapper padding-001 login-wrapper-min-height">
	<div class="container login-container">
		<div class="row login-row">
			<div class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
				<div class="ss-logo-blue login-logo margin-bottom-25 margin-top-25"></div>
				<div class="login-txt text-center font-24 margin-bot-20">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
						<div>
							<form id="zillowForm" action="/zillowSaveInfo.do" method="post">
	 							<div class="zillow-input-container clearfix">
									<label class="zillow-input-label"><spring:message code="label.zillowconnect.key"/></label>
									<div class="zillow-input-cont">
										<span><spring:message code="label.zillowconnect.link.key"/></span>
										<input class="zillow-input" name="zillowProfileName" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">
										<span>/</span>
									</div>
								</div>
								<div class="zillow-sub-btn" onclick="saveZillowEmailAddress()"><spring:message code="label.submit.key"/></div>
							</form>
							<div class="zillow-example-cont">
								<div class="zillow-exm-url">
									<span>eg.</span> 
									<span class="zillow-url"><spring:message code="label.zillow.exampleurl.key" /></span>
								</div>
								<div class="zillow-exm-profile">
									<span><spring:message code="label.zillow.exampleprofilename.text.key" /></span> 
									<span class="zillow-exm-profilename"><spring:message code="label.zillow.exampleprofilename.key" /></span>
								</div>
							</div>
						</div>
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
	
});


function saveZillowEmailAddress() {
	if(!validateZillowForm()){
		return false;
	}
	
	$('#zillowForm').submit();
	
	/* var payload = $('#zillowForm').serialize();
	$.ajax({
		url : './zillowSaveInfo.do',
		type : "POST",
		data : payload,
		async : false,
		complete : function(data) {
			setTimeout(function() {
				window.close();
			}, 3000);
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	}); */
}

function validateZillowForm() {
	/* var zillowEmailAddress = $('input[name="zillowEmailAddress"]').val();
	if(zillowEmailAddress != undefined && zillowEmailAddress != "" && emailRegex.test(zillowEmailAddress)) {
		return true;
	} */
	var zillowProfileName = $('input[name="zillowProfileName"]').val();
	if(zillowProfileName == undefined ||  zillowProfileName == "") {
		$('#overlay-toast').text("Please enter a valid profile name");
		showToast();
		return false;
	}else {
		return true;
	}
}

$(window).on('unload',
		function() {
			var parentWindow = null;
			if (window.opener != null && !window.opener.closed) {
				parentWindow = window.opener;
			}
			var payload = {
				'socialNetwork' : "zillow"
			};
			fetchSocialProfileUrl(payload, function(data) {
				parentWindow.showLinkedInProfileUrl(data);
				parentWindow.showProfileLink("zillow", data);
				parentWindow.showProfileLinkInEditProfilePage("zillow",
						data.responseText);
			});
		});

function fetchSocialProfileUrl(payload, callBackFunction) {
	$.ajax({
		url : './profileUrl.do',
		type : "GET",
		cache : false,
		data : payload,
		async : false,
		complete : callBackFunction,
		error : function(e) {
			if (e.status == 504) {
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
