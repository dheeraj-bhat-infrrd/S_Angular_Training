<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${not empty reviews}">
		<c:forEach var="feedback" varStatus="loop" items="${reviews}">
			<c:choose>
				<c:when test="${ feedback.score % 1 == 0 }">
					<c:set value="#" var="scoreformat"></c:set>
				</c:when>
				<c:otherwise>
					<c:set value="#.#" var="scoreformat"></c:set>
				</c:otherwise>
			</c:choose>
			<c:set
				value="${ feedback.customerFirstName } ${ feedback.customerLastName }"
				var="customerName">
			</c:set>
			<c:set value="${fn:split(customerName, ' ')}" var="nameArray"></c:set>
			<c:choose>
				<c:when test="${ not empty nameArray[1] }">
					<c:set
						value="${ nameArray[0] } ${ nameArray[1].substring( 0, 1 ).toUpperCase() }."
						var="customerDisplayName"></c:set>
				</c:when>
				<c:otherwise>
					<c:set value="${ nameArray[0] }" var="customerDisplayName"></c:set>
				</c:otherwise>
			</c:choose>
			<c:set value="ppl-review-item" var="reviewitemclass"></c:set>
			<div class="${reviewitemclass}">
				
				<div class="ppl-header-wrapper clearfix">
					<div class="float-left ppl-header-left">
						<div class="ppl-head-1">
							<spring:message code="label.agentname.text"/> : ${feedback.agentName}
						</div>
						<div class="ppl-head-1">
							<spring:message code="label.custname.key"/> : ${feedback.customerFirstName} ${feedback.customerLastName}
						</div>
					</div>
				</div>
				<div class="ppl-content"><spring:message code="label.score.text"/> : ${feedback.score}</div>
				<div class="ppl-content"><spring:message code="label.mood.text"/> : ${feedback.mood}</div>
				<div class="ppl-content"><spring:message code="label.review.text"/> : ${feedback.review}</div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="dash-lp-header" id="incomplete-survey-header"><spring:message code="label.nosurveysunderresolution.key" /></div>
	</c:otherwise>
</c:choose>