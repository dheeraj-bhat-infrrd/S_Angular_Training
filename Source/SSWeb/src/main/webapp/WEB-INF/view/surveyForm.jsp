<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user"
   value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<!DOCTYPE html>
<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>
         <spring:message code="label.survey.title.key" />
      </title>
      <link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
      <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
      <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
      <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
      <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
      <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
      <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
   </head>
   <body>
      <div id="toast-container" class="toast-container">
         <span id="overlay-toast" class="overlay-toast"></span>
      </div>
      <div class="overlay-loader hide"></div>
      <div class="hdr-wrapper">
         <div class="container hdr-container clearfix">
            <div class="float-left hdr-logo"></div>
         </div>
      </div>
      <div id="err-nw-wrapper" class="err-nw-wrapper"
         style="margin-bottom: 10px;">
         <span class="err-new-close"></span> <span id="err-nw-txt"></span>
      </div>
      <div id="prof-container" data-q="${q}" data-agentid="${agentId}"
         data-agentName="${agentName}" data-agent-email="${agentEmail}"
         data-last-name="${lastName}" data-first-name="${firstName}"
         class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
         <div class="container">
            <div class="sq-ques-wrapper">
               <div id="agnt-img" class="sq-top-img"></div>
               <div data-ques-type="user-details" class="sq-quest-item">
                  <div class="sq-ques">
                     <i>
                        <span class="sq-ques-txt">
                           <spring:message
                              code="label.surveyquestion.header.key" />
                           <br> 
                           <spring:message
                              code="label.surveyquestion.header2.key" />
                           <br> <span
                              class="semibold">${agentName}</span>
                        </span>
                     </i>
                  </div>
                  <div class="sq-bord-bot-sm"></div>
                  <input type="hidden" value="${message}" data-status="${status}"
                     name="message" id="message" />
                  <div class="sq-rat-wrapper">
                     <form id="survey-request-form" action="/rest/survey/triggersurvey">
                        <div
                           class="sq-star-wrapper sq-i-container clearfix ques-wrapper-adj">
                           <div class="clearfix sq-info-wrapper">
                              <div class="sq-i-lbl float-left"><spring:message code="label.firstname.key"/></div>
                              <div class="sq-i-txt float-left">
                                 <div class="hide sq-img-adj icn-fname"></div>
                                 <input id="firstName" class="sq-i-txt-fld" name="firstName"
                                    value="${firstName}"
                                    placeholder='<spring:message code="label.firstname.key"/>'>
                              </div>
                           </div>
                           <div class="clearfix sq-info-wrapper">
                              <div class="sq-i-lbl float-left"><spring:message code="label.lastname.key"/></div>
                              <div class="sq-i-txt float-left">
                                 <div class="hide sq-img-adj icn-lname"></div>
                                 <input id="lastName" class="sq-i-txt-fld" name="lastName"
                                    value="${lastName}"
                                    placeholder='<spring:message code="label.lastname.key"/>'>
                              </div>
                           </div>
                           <div class="clearfix sq-info-wrapper">
                              <div class="sq-i-lbl float-left"><spring:message code="label.email.key"/></div>
                              <div class="sq-i-txt float-left">
                                 <div class="hide sq-img-adj icn-email"></div>
                                 <input id="email" class="sq-i-txt-fld" name="customerEmail"
                                    value="${customerEmail}"
                                    placeholder='<spring:message code="label.emailid.key"/>'>
                              </div>
                           </div>
                           <div class="clearfix sq-info-wrapper">
                              <div class="sq-i-lbl float-left sq-i-checkbox">
                                 <div id="cust-agent-verify"
                                    class="bd-check-img bd-check-img-checked float-right sq-checkbox"></div>
                              </div>
                              <div class="sq-i-txt float-left sq-i-checkbox-txt">
                                 <div class="sq-i-check-txt"><spring:message code="label.custverify.text.key"/> ${agentName}
                                 </div>
                                 <!-- <select id="cust-agnt-rel" class="sq-i-txt-fld"></select> -->
                              </div>
                           </div>
                           <div
                              class="clearfix reg-captcha-wrapper reg-item reg-cap-nw-adj">
                              <div class="reg-cap-nw-adj-container">
                                 <div class="g-recaptcha"
                                    data-sitekey="6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K"></div>
                              </div>
                           </div>
                           <div style="display: none">
                              <input type="hidden" name="agentId" value="${agentId}">
                              <input type="hidden" name="agentName" value="${agentName}">
                              <input type="hidden" name="relationship"> <input
                                 type="hidden" name="g-recaptcha-response">
                           </div>
                           <div class="sq-btn-wrapper">
                              <div id="start-btn" class="sq-btn-continue"><spring:message code="label.start.btn.key"/></div>
                           </div>
                           <div id="privacy-policy-link"
                              class="privacy-policy-link take-sur-link">
                              <a href="https://www.socialsurvey.me/survey/privacy-policy/">Privacy
                              Policy</a>
                           </div>
                        </div>
                     </form>
                  </div>
               </div>
            </div>
         </div>
         <script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
         <script src="${initParam.resourcesPath}/resources/js/common.js"></script>
         <script src="${initParam.resourcesPath}/resources/js/script.js"></script>
         <script src='//www.google.com/recaptcha/api.js' defer="defer" async="async"></script>
         <script src="${initParam.resourcesPath}/resources/js/googletracking.js" defer="defer" async="async"></script>
         <script>
            $(document).ready(function() {
            	initializeSurveyFormPage();
            });
         </script>
      </div>
      <!-- Page footer -->
      <jsp:include page="footer.jsp" />
   </body>
</html>