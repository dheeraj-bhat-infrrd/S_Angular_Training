<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>

<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.help.key" /></div>
			<c:if test="${not empty assignments}">
				
			</c:if>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div id="prof-container" data-profile-master-id="${profileMasterId}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="dash-top-info dash-prof-wrapper">
			<div id="dash-profile-detail-circles" class="row row-dash-top-adj">
				<!-- Populated by dashboard_profiledetail.jsp -->
			</div>
		</div>

		

		<div class="dash-stats-wrapper bord-bot-dc clearfix">
		
		<form action="http://localhost:8080/landing.do#showhelppage" method="post" class="contact-form">
								<p class="half">
					<label for="sender_name">Name</label><input name="sender_name" id="sender_name" value="">
				</p>
				
					<p><label for="subect">Subject</label><input name="subject" id="help_subject" value="">
					<!-- <span class="select"><span class="value"><span>Choose</span></span><select name="subject" id="subject" style="opacity: 0;">
						<option value="0">Choose</option>
												<option value="Support">Support</option>
											</select></span> -->
				</p> 
				<p>
					<label for="message">Message</label> <textarea name="message" id="message" rows="15" cols="20"></textarea>
				</p>
				<p><button name="send" type="submit" value="1">Send message</button></p>
			</form>
			
			
			
			
		</div>
		
		
	</div>
</div>

<script>
$(document).ready(function() {
	
	$('.va-dd-wrapper').perfectScrollbar({
		suppressScrollX : true
	});
	$('.va-dd-wrapper').perfectScrollbar('update');
	
	hideOverlay();
	$(document).attr("title", "Dashboard");
	
	if ($("#da-dd-wrapper-profiles").children('.da-dd-item').length <= 1) {
		$('#da-dd-wrapper').remove();
	}
	
	var profileMasterId = $('#prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#prof-container').attr('data-column-name');
	var currentProfileValue = $('#prof-container').attr('data-column-value');
	var accountType = $('#prof-container').attr('data-account-type');
	
	var popupStatus = "${popupStatus}";
	var showSendSurveyPopupAdmin = "${showSendSurveyPopupAdmin}";
	
	if (showSendSurveyPopupAdmin == "true" && popupStatus == "Y") {
		sendSurveyInvitationAdmin(currentProfileName, currentProfileValue);
	}
	
	paintDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);
});
</script>