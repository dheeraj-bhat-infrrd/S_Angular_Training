<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<input type="hidden" id="hierarchyType" value="${entityType}" />
<input type="hidden" id="hierarchyValue" value="${entityId}" />
<div id="welcome-popup-invite" class="welcome-popup-wrapper">
	<div class="welcome-popup-hdr-wrapper clearfix">
		<div class="float-left wc-hdr-txt"><spring:message code="label.sendsurvey.key" /></div>
		<div class="float-right popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
	</div>
	<div class="welcome-popup-body-wrapper clearfix">
		<div class="wc-popup-body-hdr"><spring:message code="label.happyreviews.key" /></div>
		<div id="wc-review-table" class="wc-popup-body-cont wc-review-table-cont wc-admin-table">
			<div id="wc-review-table-inner" class="wc-review-table" style="position:relative" data-role="admin" user-email-id="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.emailId}">
				<div id="send-survey-dash" class="hide wc-review-hdr"></div>
				<div class="wc-review-tr wc-review-hdr clearfix">
					<div class="wc-review-th1 float-left"><spring:message code="label.agentname.key" /></div>
					<div class="wc-review-th2 float-left"><spring:message code="label.firstname.key" /></div>
					<div class="wc-review-th3 float-left"><spring:message code="label.lastname.key" /></div>
					<div class="wc-review-th4 float-left"><spring:message code="label.emailid.key" /></div>
					<div class="wc-review-th6 float-left"><spring:message code="label.contactnumber.key" text="Contact Number" />
					<div class="send-allow-sms" style="font-size: 0.8em;line-height: 6px;">(Survey SMS Reminder)</div>
					</div>
					<div class="wc-review-th5 float-left"></div>
				</div>
				<div id="survey-1" class="wc-review-tr wc-review-data-row clearfix">
					<div class="wc-review-data-col wc-review-tc1 survey-user  float-left pos-relative">
						<input data-name="agent-name" class="wc-review-input  wc-review-agentname" placeholder="User Name"><div class="validation validationagent hidden"></div>
					</div>
					<div class="wc-review-data-col wc-review-tc2 survey-fname float-left"><input class="wc-review-input wc-review-fname" placeholder="First Name" ><div class="validation validationfname hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc3 float-left"><input class="wc-review-input wc-review-lname" placeholder="Last Name"><div class="validation validationlname hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc4 survey-email float-left"><input class="wc-review-input wc-review-email" placeholder="Email"><div class="validation validationemail hidden"></div></div>
					<!-- adding contact number on dashboard (Send a Survey) -->
					<div class="wc-review-data-col wc-review-tc6 survey-contact float-left"><jsp:include page="country_code.jsp"></jsp:include><div class="validation validationcontact float-left hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc5 last float-left"><div class="wc-review-rmv-icn hide"></div></div>
				</div>
				<div id="survey-2" class="wc-review-tr wc-review-data-row clearfix">
					<div class="wc-review-data-col wc-review-tc1 survey-user float-left pos-relative">
						<input data-name="agent-name" class="wc-review-input wc-review-agentname" placeholder="User Name"><div class="validation validationagent hidden"></div>
					</div>
					<div class="wc-review-data-col wc-review-tc2 survey-fname float-left"><input class="wc-review-input wc-review-fname" placeholder="First Name"><div class="validation validationfname hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc3 float-left"><input class="wc-review-input wc-review-lname" placeholder="Last Name"><div class="validation validationlname hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc4 survey-email float-left"><input class="wc-review-input wc-review-email" placeholder="Email"><div class="validation  validationemail hidden"></div></div>
					<!-- adding contact number on dashboard (Send a Survey) -->
					<div class="wc-review-data-col wc-review-tc6 survey-contact float-left"><jsp:include page="country_code.jsp"></jsp:include><div class="validation validationcontact float-left hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc5 last float-left"><div class="wc-review-rmv-icn"></div></div>
				</div>
				<div id="survey-3" class="wc-review-tr wc-review-data-row clearfix">
					<div class="wc-review-data-col wc-review-tc1 survey-user float-left pos-relative">
						<input data-name="agent-name" class="wc-review-input wc-review-agentname" placeholder="User Name"><div class="validation validationagent hidden"></div>
					</div>
					<div class="wc-review-data-col wc-review-tc2 survey-fname float-left"><input class="wc-review-input wc-review-fname" placeholder="First Name"><div class="validation validationfname hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc3 float-left"><input class="wc-review-input wc-review-lname" placeholder="Last Name"><div class="validation validationlname hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc4 survey-email float-left"><input class="wc-review-input wc-review-email" placeholder="Email"><div class="validation validationemail hidden"></div></div>
					<!-- adding contact number on dashboard (Send a Survey) -->
					<div class="wc-review-data-col wc-review-tc6 survey-contact float-left"><jsp:include page="country_code.jsp"></jsp:include><div class="validation validationcontact float-left hidden"></div></div>
					<div class="wc-review-data-col wc-review-tc5 last float-left"><div class="wc-review-rmv-icn"></div></div>
				</div>
			</div>
		</div>
	</div>
	<div class="wc-btn-row clearfix">

	  <div class="float-left wc-width"  id="wc-popup<c:out value='_${disableCookie}'/>true">
	    <input id="wc-dashboard-popup" class="float-left" type="checkbox" />
	     <div class="float-left wc-dashboard-text"><spring:message code="label.donot.send.key"/></div>
	 </div>
	<div class="wc-btn-col float-left clearfix wc-dash-btn">
		<div id="wc-skip-send-survey" class="wc-skip-btn float-left wc-final-skip"><spring:message code="label.skip.key" /></div>
		<div id="wc-send-survey" class="wc-sub-send-btn float-left wc-final-submit"><spring:message code="label.send.key" /></div>	
		<div id="wc-send-survey-upload-csv" class="survey-csv-send-btn survey-csv-send-btn-resp float-left wc-final-upload-csv"><spring:message code="label.csv.upload.key" /></div>
	</div>
	</div>
	
	<div class="survey-upload-csv hide clearfix">
		<div id="send-survey-csv-dash" class="hide"></div>
		<div class="float-left " style="width: 50%;">
	   		<div class="survey-csv-upload-logo">	
	            <input type="file" class="rfr_input_fld opacity-complete-invisibility survey-csv-file-input" id="survey-file-intake">
	            <div class="display-load"><spring:message code="label.survey.csv.load.file.key" /></div>
		   	</div>
		   	
		   	<div class="survey-csv-file-info hide">
				<div>
					<spring:message code="label.survey.csv.upload.filename.key" /> 
					<span id="survey-csv-file-name" class="rfr_txt_fld" style="font-weight: bold !important;">
					</span>
				</div>
			</div>
			
	   	</div>
		<div class="float-right" style="width: 50%;">
	   		<div class="rfr_txt_fld" style=" margin: 0 auto;">
				<input type="text" class="rfr_input_fld" id="survey-uploader-email" spellcheck="false" placeholder='<spring:message code="label.survey.uploader.email.placeholder.key"/>'>
			</div>
			<div id="upload-email-invalid" class="hm-item-err-2 survey-csv-email-invalid hide"><spring:message code="label.survey.csv.uploader.email.invalid.key"/></div>
			<div class = "survey-csv-buttons-resp survey-csv-buttons">
		   		<div id="wc-send-survey-upload-confirm" class=" float-left survey-csv-button disable">confirm</div>
				<div id="wc-send-survey-upload-cancel" class=" float-left survey-csv-button">cancel</div>
			</div>
		</div>	
		<div class="clearfix">
			<div class="survey-csv-template cursor-pointer">
				<input type="hidden" id="template-url" value="${templateUrl}"/>
				<span>download Template</span>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function(){
	disableBodyScroll();
	var cookieValue= $.cookie("doNotShowPopup");
	if(cookieValue=="true"){
		$('#wc-dashboard-popup').prop("checked",true);
	}else{
		$('#wc-dashboard-popup').prop("checked",false);
	}
	
	$('#wc-popup').remove();
	var doNotShowPopup ="false";
	
	$("#wc-dashboard-popup").change(function() {
		if ($(this).is(":checked")) {
			$.cookie("doNotShowPopup", "true",{ expires: 365 * 10 });
		} else {
			$.cookie("doNotShowPopup", "false");
		}
	});
	
	$('.survey-csv-template').on('click',function(e){
		e.stopPropagation();
		window.open($("#template-url").val(),"_blank");
	});
});
</script>
