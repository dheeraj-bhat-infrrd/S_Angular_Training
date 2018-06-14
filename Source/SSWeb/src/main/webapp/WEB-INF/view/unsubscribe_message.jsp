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
      <style>
      	.unsubscribe-cont{
      		font-size: 20px;
    		font-weight: bold !important;
		    height: 200px;
		    text-align: center;
		    margin-top: 60px;
      	}
      </style>
   </head>
   <body>
      <div id="toast-container" class="toast-container">
         <span id="overlay-toast" class="overlay-toast"></span>
      </div>
      <div class="overlay-loader hide"></div>
      <div class="hdr-wrapper">
         <div class="container hdr-container clearfix">
            <div class="float-left hdr-logo"></div>
            <c:if test="${not empty logo}">
                <div class="float-right" style="background: url(${logo}) no-repeat center; background-size: contain; width: 140px; height: 60px;"></div>
            </c:if>

         </div>
      </div>
      <div id="err-nw-wrapper" class="err-nw-wrapper"
         style="margin-bottom: 10px;">
         <span class="err-new-close"></span> <span id="err-nw-txt"></span>
      </div>
      <div id="prof-container" class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
         <div class="container unsubscribe-cont">
         <centre><b>${message} </b></centre>
         </div>
      </div>
      <!-- Page footer -->
      <jsp:include page="footer.jsp" />
   </body>
</html>