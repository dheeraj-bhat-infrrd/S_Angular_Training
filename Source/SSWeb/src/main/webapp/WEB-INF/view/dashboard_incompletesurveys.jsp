<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${not empty incompleteSurveys}">
		<c:forEach var="survey" items="${incompleteSurveys}">
			<div class="dash-lp-header" id="incomplete-survey-header"><spring:message code="label.incompletesurveys.key" /></div>
			<div id="dsh-inc-srvey" class="dash-lp-item-grp">
				<div class="dash-lp-item clearfix">
					<div class="float-left dash-lp-txt">
						${survey.customerFirstName} ${survey.customerLastName} <span>${survey.modifiedOn}</span>
					</div>
					<div data-custname="${survey.customerFirstName} ${survey.customerLastName}"
						data-agentid="${survey.agentId}" data-agentname="${survey.agentName}"
						data-custemail="${survey.customerEmail}" class="float-right dash-lp-rt-img cursor-pointer"></div>
				</div>
			</div>
			<div id="dsh-inc-dwnld" class="dash-btn-sur-data"><spring:message code="label.incompletesurveydata.key" /></div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="dash-lp-header" id="incomplete-survey-header"><spring:message code="label.noincompletesurveys.key" /></div>
	</c:otherwise>
</c:choose>