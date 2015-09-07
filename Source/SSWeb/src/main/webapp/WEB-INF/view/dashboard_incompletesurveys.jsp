<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${not empty incompleteSurveys}">
	<c:forEach var="survey" items="${incompleteSurveys}"  varStatus="loop">
		<c:choose>
			<c:when test="${not empty survey.customerLastName}">
				<c:set value="${survey.customerFirstName} ${survey.customerLastName}" var="customerName"></c:set>
			</c:when>
			<c:otherwise>
				<c:set value="${survey.customerFirstName}" var="customerName"></c:set>
			</c:otherwise>
		</c:choose>
		<c:set var="customerNameParts" value="${fn:split(customerName, ' ')}"></c:set>
		<div class="dash-lp-item clearfix"  data-iden="sur-pre-${survey.surveyPreIntitiationId }">
			<div class="float-left dash-lp-txt text-capitalize">
				${customerNameParts[0]} 
				 <c:if test="${fn:length(customerNameParts) > 1}">
				 	${fn:substring(customerNameParts[1], 0, 1)}
				 </c:if>
					<div class="font-11 opensanslight" data-value="<fmt:formatDate value="${survey.modifiedOn}" pattern="yyyy-MM-dd-H-mm-s-S"/>">
					</div>
			</div>
			<div
				data-surveypreinitiationid="${survey.surveyPreIntitiationId }"
				data-custname="${survey.customerFirstName} ${survey.customerLastName}"
				data-agentid="${survey.agentId}" data-agentname="${survey.agentName}"
				data-custemail="${survey.customerEmailId}"
				class="float-right dash-lp-rt-img cursor-pointer"
				title="Resend Survey"></div>
		</div>
	</c:forEach>
</c:if>
<script>
$(document).ready(function(){
	$('.opensanslight').each(function(index, currentElement) {
		var dateStr = $(this).attr('data-value');
		$(this).html(getDateStrToUTC(dateStr));
	});
});
</script>