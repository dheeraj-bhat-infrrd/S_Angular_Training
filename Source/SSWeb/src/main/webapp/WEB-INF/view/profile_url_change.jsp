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
<div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
<div class="overlay-loader hide"></div>
<div class="login-main-wrapper padding-001">
	<div class="container login-container">
		<div class="row login-row">
			<div class="profile-url-wrapper padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
				<div class="logo login-logo margin-bottom-25 margin-top-25"></div>
				<div class="login-txt text-center font-24 margin-bot-20">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;">
						<div>
						<form id="profileUrlEditForm" action="/profileUrlSaveInfo.do" method="post">			
 							<div class="url-input-container clearfix">
								<label class="url-change-input-label"><spring:message code="label.profileurlchange.key"/></label>
								<div>
									<span>${profileBaseUrl}</span>
									<input class="profile-url-input" name="profileUrlBlock" type="text" autofocus="autofocus" value = "${profileSettings.getProfileName()}">
									<span>/</span>
								</div>
							</div>
							<div class="profile-url-sub-btn" onclick="saveProfileUrl()"><spring:message code="label.submit.key"/></div>
						</form>
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
<script type="${initParam.resourcesPath}/resources/js/common.js"></script>
<script>
$(document).ready(function() {
	
});


function saveProfileUrl() {
	if(!validateprofileUrlEditForm()){
		return false;
	}

}



function validateprofileUrlEditForm() {
	var profileUrl = $('input[name="profileUrlBlock"]').val();
	if(profileUrl == undefined ||  profileUrl == "") {
		$('#overlay-toast').text("Please enter a valid profile name");
		showToast();
		return false;
	}
	$.ajax({
		url : "./updateprofileurl.do?searchKey=" + profileUrl,
		type : "GET",
		cache : false,
		dataType : "html",
		async : true,
		success : function(data) {
			var profileExists = data;
			if(profileExists == "true"){
				$('#overlay-toast').text("The entered profile name already exists");
				showToast();
				return false;
			}
			else{
				$('#overlay-toast').text("Url updated successfully");
				showToast();
				window.opener.$("#prof-header-url").html(data);
				setTimeout(function(){
				    window.close();
				},3000);
				return true;
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			//redirectErrorpage();
		}
	});
}

$(window).on('unload',
	function() {
			var parentWindow = null;
			if (window.opener != null && !window.opener.closed) {
				parentWindow = window.opener;
			}
	}
);
</script>

</body>
</html>
