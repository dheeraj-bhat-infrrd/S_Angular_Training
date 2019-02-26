<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE">
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 <meta name="viewport" content="width=device-width, initial-scale=1">
 <title><spring:message code="label.Updateconnection.title.key"></spring:message></title>
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
 


<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
var checkIfFacebookSet = false;
var columnName;
var columnValue;
$(document).ready(function() {
	var curDate = new Date();
	$('#ss-cc-year').html(curDate.getFullYear());
	
 columnName = '${columnName}';
 columnValue = '${columnValue}';
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
  else {
  }
 }
 
 var pages = "${pageNames}";
 var selectPageText = $("<div style='margin-bottom: 10px; font-size: 19px; text-align: center; padding: 0px 10px;'> Select the profile, you want to connect to SocialSurvey! </div>");
 if(pages.length > 0)
 	$("#page").append(selectPageText);
 
 var radioButtonDiv= $("<div style='text-align:left;margin-left:130px;'>")
 <c:forEach var="page" items="${pageNames}" varStatus="loop">
  radioButtonDiv.append('<input type="radio" name="pageselection" value="${loop.index}"/>'+"${fn:escapeXml(page.name)}"+" <br/>");
 </c:forEach>
 $("#page").append(radioButtonDiv);
 
 var saveButton= $("<div class='reg_btn'>save</div>");
 <c:if test="${not empty pageNames}">
  $("#page").append(saveButton);
 </c:if>
  
 saveButton.data("requestRunning", false);
 
 saveButton.click(function() {
	 showOverlay();
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
  <c:forEach var="page" items="${pageNames}"  varStatus="loop">
    if("${loop.index}" == selectedPage){
     selectedProfileUrl= "${page.profileUrl}";
     selectedAccessFacebookToken= "${page.accessToken}";
    }
  </c:forEach>
  var ajaxData = {
   'selectedAccessFacebookToken' : selectedAccessFacebookToken,
   'selectedProfileUrl' :  selectedProfileUrl,
   'fbAccessToken' : fbAccessToken,
   'columnName' :  columnName,
   'columnValue' : columnValue
  };
  
  var updateSelectedFBToken = "/rest/saveSelectedAccessFacebookTokenForEmail.do?selectedAccessFacebookToken="
		+ selectedAccessFacebookToken + "&selectedProfileUrl=" + selectedProfileUrl 
		+ "&fbAccessToken=" + fbAccessToken 
		+ "&columnName=" + columnName 
		+ "&columnValue=" + columnValue ;
  window.location.href = updateSelectedFBToken;
  
  $.ajax({
   url : './saveSelectedAccessFacebookTokenForEmail.do',
   type : "GET",
   cache : false,
   data : ajaxData,
   async : false,
   complete :function(e){
    console.log("Request saveSelectedAccessFacebookTokenForEmail completed");
    enable(this);
    checkIfFacebookSet = true;
    setTimeout(function() {
     hideOverlay();
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