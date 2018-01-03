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
      <title>Email Unsubscribe</title>
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
            <c:if test="${not empty logo}">
                <div class="float-right" style="background: url(${logo}) no-repeat center; background-size: contain; width: 140px; height: 60px;"></div>
            </c:if>

         </div>
      </div>
      <div id="err-nw-wrapper" class="err-nw-wrapper"
         style="margin-bottom: 10px;">
         <span class="err-new-close"></span> <span id="err-nw-txt"></span>
      </div>
      <div class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
         <div class="container">
         	<form id="add-ss-admin-form">
		<div class="reg_form_wrapper_2">
			<div class="reg_form_row clearfix">
				<div class="float-left rfr_lbl" style='width: 265px !important;'>Enter Email id to unsubscribe</div>
				<div class="float-left rfr_txt">
					<div class="rfr_icn icn-email"></div>
					<div class="rfr_txt_fld">
						<input type="email" class="rfr_input_fld" data-non-empty="true" id="add-unsubscribe-email" name="emailId" placeholder="Email Address">
					</div>
				</div>
				<div class="float-left rfr_txt">
				<div class="float-left hm-header-right text-center" id="add-unsubscribe-email-form-submit">Add</div>
				</div>
			</div>
			
		</div>
	</form>
         
            <div class="sq-ques-wrapper">
              
               <div data-ques-type="user-details" class="sq-quest-item">
                  <div class="sq-ques">
                     <i><span class="sq-ques-txt">Unsubscribed Email Addresses</span>
                     </i>
                  </div>
                  <div class="sq-bord-bot-sm"></div>
                  <input type="hidden" value="${message}" data-status="${status}"  name="message" id="message" />
                  <div class="sq-rat-wrapper">
                  	<table class="v-um-tbl">
	<tbody><tr id="u-tbl-header" class="u-tbl-header" data-num-found="0">
		<td class="v-tbl-uname">Email Id</td>
		<td class="v-tbl-email">Created On</td>
		<td class="v-tbl-rgn-adm text-center">

		</td><td class="v-tbl-ln-of text-center">Action</td>
	</tr>
	
		
		
			<tr class="u-tbl-row"></tr>
		
	
</tbody></table>
                   </div>
               </div>
            </div>
         </div>
         <script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
         <script src="${initParam.resourcesPath}/resources/js/common.js"></script>
         <script src="${initParam.resourcesPath}/resources/js/script.js"></script>
         <script src='//www.google.com/recaptcha/api.js' defer="defer" async="async"></script>
         <script>
            $(document).ready(function() {
            	bindEmailUnsubscribeClickEvent();
            	
            });
         </script>
      </div>
      <!-- Page footer -->
      <jsp:include page="footer.jsp" />
   </body>
</html>