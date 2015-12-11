<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div id="welcome-popup-invite" class="welcome-popup-wrapper">
	<div class="welcome-popup-hdr-wrapper clearfix">
		<div class="float-left wc-hdr-txt"><spring:message code="label.sendsurvey.key" /></div>
		<div class="float-right popup-close-icn wc-skip-btn wc-final-skip"></div>
	</div>
	<div class="welcome-popup-body-wrapper clearfix">
		<div class="wc-popup-body-hdr"><spring:message code="label.happyreviews.key" /></div>
		<div id="wc-review-table" class="wc-popup-body-cont wc-review-table-cont">
			<div id="wc-review-table-inner" class="wc-review-table" data-role="agent" user-email-id="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal.emailId}">
				<div class="wc-review-tr wc-review-hdr clearfix">
					<div class="wc-review-th1 float-left"><spring:message code="label.firstname.key" /></div>
					<div class="wc-review-th2 float-left"><spring:message code="label.lastname.key" /></div>
					<div class="wc-review-th3 float-left"><spring:message code="label.emailid.key" /></div>
					<div class="wc-review-th4 float-left"></div>
				</div>
				<div class="wc-review-tr clearfix">
					<div class="wc-review-tc1 float-left"><input class="wc-review-input wc-review-fname"></div>
					<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-lname"></div>
					<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-email"></div>
					<div class="wc-review-tc4 float-left"><div class="wc-review-rmv-icn hide"></div></div>
				</div>
				<div class="wc-review-tr clearfix">
					<div class="wc-review-tc1 float-left"><input class="wc-review-input wc-review-fname"></div>
					<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-lname"></div>
					<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-email"></div>
					<div class="wc-review-tc4 float-left"><div class="wc-review-rmv-icn"></div></div>
				</div>
				<div class="wc-review-tr clearfix">
					<div class="wc-review-tc1 float-left"><input class="wc-review-input wc-review-fname"></div>
					<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-lname"></div>
					<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-email"></div>
					<div class="wc-review-tc4 float-left"><div class="wc-review-rmv-icn"></div></div>
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
		</div>
	 
	 
	 
	 
	 
	 
		<%-- <div class="wc-btn-col float-left">
			<div id="wc-skip-send-survey" class="wc-skip-btn float-right wc-final-skip"><spring:message code="label.skip.key" /></div>
		</div>
		<div class="wc-btn-col float-left">
			<div id="wc-send-survey" class="wc-sub-send-btn float-left wc-final-submit"><spring:message code="label.send.key" /></div>
		</div> --%>
	</div>
</div>
<script>
	$(document).ready(function(){
		disableBodyScroll();
		var cookieValue= $.cookie("doNotShowPopup");
		if(cookieValue == "true"){
			$('#wc-dashboard-popup').prop("checked",true);
		}else {
			$('#wc-dashboard-popup').prop("checked",false);
		}
		
		$("#wc-dashboard-popup").change(function() {
			/* debugger; */
			if ($(this).is(":checked"))
			$.cookie("doNotShowPopup", "true",{ expires: 365 * 10 });
			
			
			else{
				$.cookie("doNotShowPopup", "false");
			}
		});
		$('#wc-review-table').perfectScrollbar();
		$('#wc-review-table').perfectScrollbar('update');
	});
</script>