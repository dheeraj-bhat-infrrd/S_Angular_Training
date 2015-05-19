<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty incompleteSurveys}">
	<c:forEach var="survey" items="${incompleteSurveys}">
		<div class="dash-lp-item clearfix">
			<div class="float-left dash-lp-txt">
				${survey.customerFirstName} ${survey.customerLastName}
				<div class="font-11 opensanslight">${survey.modifiedOn}</div>
			</div>
			<div data-custname="${survey.customerFirstName} ${survey.customerLastName}"
				data-agentid="${survey.agentId}" data-agentname="${agentName}"
				data-custemail="${survey.customerEmailId}" class="float-right dash-lp-rt-img cursor-pointer"></div>
		</div>
	</c:forEach>
</c:if>
