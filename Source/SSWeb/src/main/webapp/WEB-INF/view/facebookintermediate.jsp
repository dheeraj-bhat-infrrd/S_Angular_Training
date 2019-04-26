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
<body>
<div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
<!-- <div id="overlay-toast" class="overlay-toast"></div> -->
<div class="overlay-loader hide"></div>
<div class="login-main-wrapper padding-001 login-wrapper-min-height">
	<div class="container login-container">
		<div class="row login-row">
			<div class="login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-xs-12">
				<div class="ss-logo-blue login-logo margin-bottom-25 margin-top-25"></div>
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
					<spring:message code="label.copyright.key" />&copy; <span id="ss-cc-year"></span>
					<spring:message code="label.footer.socialsurvey.key" /><span class="center-dot">.</span>
					<spring:message code="label.allrightscopyright.key" />
				</div>
			</div>
		</div>
	</div>
</div>
<%-- <input type="hidden" id="md-token" value="${mediaTokens}"> --%>

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
var checkIfFacebookSet = false;
$(document).ready(function() {
	var curDate = new Date();
	$('#ss-cc-year').html(curDate.getFullYear());
	
	//Get media tokens from model
	/* var mediaTokens = $('#md-token').val(); */
	var mediaTokens = '${mediaTokens}';
	// Onload before auth Url
	var waitMessage = "${message}";
	var fbAccessToken = '${fbAccessToken}';
	if (parseInt(waitMessage) == 1) {
		var authUrl = "${authUrl}";
		if (authUrl != null) {
			location.href = authUrl;
		}
	}
	
	// select parent Window
	var parentWindow;
	if (window.opener != null && !window.opener.closed) {
		parentWindow = window.opener;
	}
	
	var isFixSocialMedia ="${isFixSocialMedia}";
	var isNewUser = "${isNewUser}";
	var fromDashboard = "${fromDashboard}";
	var restful = "${restful}";
	var flow = "${socialFlow}";
	var isFixSocialMedia ="${isFixSocialMedia}";
	var isManual = "${isManual}";
	var columnName = "${columnName}";
	var columnValue = "${columnValue}";
	var socialNetwork = "${socialNetwork}";
	var isFbImagePopup = "${isFbImagePopup}";

	if(isFbImagePopup == 'true'){
		$('#page').text('Thank you for sharing your ' + socialNetwork + ' picture.');
		parentWindow.showProfileImageForSurvey("${profileImage}");
		setTimeout(function() {
			window.close();
		}, 3000);
	} else {
		if(isFixSocialMedia ==  1){
			if(isNewUser==true || isNewUser=="true"){
				
				var closeSMParam = {
					"fromDashboard":fromDashboard,
					"restful":restful,
					"flow":flow,
					"isFixSocialMedia":isFixSocialMedia,
					"isManual":isManual,
					"columnName":columnName,
					"columnValue":columnValue,
					"socialNetwork":socialNetwork,
					"checkIfFacebookSet":checkIfFacebookSet
				};
				
				var errorPopupDiv = $("<div style='display:grid'>");
				errorPopupDiv.append('<span>Invalid Credentials.</span>');
				errorPopupDiv.append('<span style="font-size:15px; margin: 15px auto;" >Please disconnect older account, to link a new Facebook Account</span>');
				$('#page').append(errorPopupDiv);
				var okButton= $("<div class='reg_btn'>OK</div>");
				$('#page').append(okButton);
				
				okButton.click(function(){
					window.close();
				});
				
			}else if(isNewUser==false || isNewUser=="false"){
				
				parentWindow.loadSocialMediaUrlInSettingsPage();
				parentWindow.loadSocialMediaUrlInPopup();
				checkIfFacebookSet = true;
				
				var closeSMParam = {
					"fromDashboard":fromDashboard,
					"restful":restful,
					"flow":flow,
					"isFixSocialMedia":isFixSocialMedia,
					"isManual":isManual,
					"columnName":columnName,
					"columnValue":columnValue,
					"socialNetwork":socialNetwork,
					"checkIfFacebookSet":checkIfFacebookSet
				};
				
				setTimeout(function() {
					closeSMPopup(closeSMParam);
					window.close();
					
					
				}, 3000);
				
			}
		}else{
			
			var radioButtonDiv= $("<div style='text-align:left;margin-left:130px;max-height: 220px;overflow: auto;'>")
			<c:forEach var="page" items="${pageNames}" varStatus="loop">
				radioButtonDiv.append('<input type="radio" name="pageselection" value="${loop.index}"/>'+"${fn:escapeXml(page.name)}"+" <br/>");
			</c:forEach>
			$("#page").append(radioButtonDiv);

			var saveButton= $("<div class='reg_btn'>save</div>");
			
			<c:if test="${not empty pageNames}">
				$("#page").append(saveButton);
			</c:if>

		    <c:if test="${isPageListEmpty}">
				$("#page").append("<div>No connected instagram accounts</div>");
			</c:if>
			
			saveButton.click(function() {
				
				if ( $(this).data('requestRunning') ) {
					return;
			    }
				disable(this);
				var selectedPage=$('input:radio[name=pageselection]:checked').val();
				if(selectedPage == undefined){
					$('#overlay-toast').html("Please select an account");
					showToast();
					enable(this);
					return;
				}
				var selectedAccessFacebookToken;
				var selectedProfileUrl;
				var selectedProfileId;
				<c:forEach var="page" items="${pageNames}"  varStatus="loop">
				  if("${loop.index}" == selectedPage){
					  selectedProfileUrl= "${page.profileUrl}";
					  selectedAccessFacebookToken= "${page.accessToken}";
					  selectedProfileId = "${page.id}";
				  }
				</c:forEach>
				var facebookToken = {
					'selectedAccessFacebookToken' : selectedAccessFacebookToken,
					'selectedProfileUrl' :  selectedProfileUrl,
					'fbAccessToken' : fbAccessToken,
					'selectedProfileId' : selectedProfileId
				};
				$.ajax({
					url : "${callback}",
					type : "GET",
					cache : false,
					data : facebookToken,
					async : false,
					complete :function(e){
						enable(this);
						parentWindow.loadSocialMediaUrlInSettingsPage();
						parentWindow.loadSocialMediaUrlInPopup();
						parentWindow.fetchSocialProfileUrl("facebook", function(data){
							if(data.status == 200) {
								var responseObj = JSON.parse(data.responseText);
								var profileUrlLink = responseObj.url;
								if(responseObj.connected){
									parentWindow.showProfileLinkInEditProfilePage("facebook", profileUrlLink);
								} else {
									parentWindow.removeProfileLinkInEditProfilePage("facebook");
								}
							}
						});
						checkIfFacebookSet = true;
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
		}		
	}
});

function disable(disableEle) {

	if (disableEle) {
		$(disableEle).data('requestRunning', true);
		disableIcon = true;
	}
}
function enable(disableEle) {
	if (disableEle ) {
		$(disableEle).data("requestRunning", false);
		disableIcon = false;
	}
}

</script>

</body>
</html>