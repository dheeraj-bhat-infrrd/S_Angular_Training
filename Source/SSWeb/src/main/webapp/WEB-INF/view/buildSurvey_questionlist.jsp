<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty surveyDetail && not empty surveyDetail.questions}">
	<c:set value="${fn:length(surveyDetail.questions)}" var="length" />
	<c:set value="${surveyDetail.status}" var="status" />
	<c:set value="1" var="count" scope="page" />
</c:if>

<c:choose>
	<c:when test="${not empty surveyDetail}">
		<c:forEach var="surveyQuestion" items="${surveyDetail.questions}">
			<div class="bd-srv-tbl-row clearfix bd-srv-tbl-row-${surveyQuestion.questionId}" data-questionid="${surveyQuestion.questionId}">
				<div class="float-left srv-tbl-num"><span>${surveyQuestion.questionOrder}</span></div>
				<div class="float-left srv-tbl-txt">${surveyQuestion.question}</div>
				<div class="srv-tbl-btns clearfix float-right">
					<c:choose>
						<c:when test="${count == 1}">
							<div class="float-left srv-tbl-move-dn"></div>
						</c:when>
						<c:when test="${count == length}">
							<div class="float-left srv-tbl-move-up"></div>
						</c:when>
						<c:otherwise>
							<div class="float-left srv-tbl-move-dn"></div>
							<div class="float-left srv-tbl-move-up"></div>
						</c:otherwise>
					</c:choose>
					<div class="float-left srv-tbl-edit"><spring:message code="label.edit.key" /></div>
					<div class="float-left srv-tbl-rem"><spring:message code="label.remove.key" /></div>
				</div>
			</div>
			<c:set value="${count + 1}" var="count" scope="page" />
		</c:forEach>
	</c:when>
	<c:otherwise>
		<spring:message code="label.noquestionsadded.key"/>
	</c:otherwise>
</c:choose>

<script>
$(document).ready(function() {
	var status = "${status}";
	if (status != "") {
		setTimeout(function() {
			showError(status);
		}, 3000);
	} else {
		hideError();
	}
});
</script>