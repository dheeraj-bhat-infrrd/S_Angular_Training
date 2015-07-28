<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="welcome-popup-wrapper">
	<div class="welcome-popup-hdr-wrapper clearfix">
		<div class="float-left wc-hdr-txt">
			<spring:message code="label.sendsurvey.key" />
		</div>
		<div class="float-right popup-close-icn wc-skip-btn wc-final-skip"></div>
	</div>
	<div class="welcome-popup-body-wrapper clearfix icn-sur-popup-cont">
		<c:if test="${not empty incompleteSurveys}">
			<c:forEach var="survey" items="${incompleteSurveys}">
				<div class="dash-lp-item clearfix" data-iden="sur-pre-${survey.surveyPreIntitiationId }">
					<div class="float-left dash-lp-txt">
						<div>Customer Name - ${survey.customerFirstName} ${survey.customerLastName}</div>
						<div>Agent Name - ${survey.agentName}</div>
						<div>Reminder count - ${survey.reminderCounts}</div>
						<div class="font-11 opensanslight"
							data-modifiedon="<fmt:formatDate type="date" pattern="yyyy-MM-dd-hh-mm-ss"
							value="${survey.modifiedOn}" />">
						</div>
					</div>
					<div class="float-right dash-icn-close cursor-pointer"
						title="Cancel Survey Reminder" onclick="removeIncompleteSurveyRequest(${survey.surveyPreIntitiationId })"></div>
					<div
						data-custname="${survey.customerFirstName} ${survey.customerLastName}"
						data-agentid="${survey.agentId}"
						data-agentname="${survey.agentName}"
						data-custemail="${survey.customerEmailId}"
						class="float-right dash-lp-rt-img cursor-pointer"
						title="Resend Survey"></div>
				</div>
			</c:forEach>
		</c:if>
	</div>
</div>
<script>
	$(document).ready(function() {
		$('.opensanslight').each(function(index, currentElement) {
			var dateSplit = $(this).attr('data-modifiedon').split('-');
			var date = convertTimeStampToLocalTimeStamp(new Date(dateSplit[0],dateSplit[1] - 1, dateSplit[2], dateSplit[3], dateSplit[4], dateSplit[5]));
			$(this).html(date);
		});
	});
</script>