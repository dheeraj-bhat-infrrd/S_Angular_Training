<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty abusiveReviewReportList}">
	<c:forEach items="${abusiveReviewReportList}" var="abusiveReviewReportItem">
		<tr>
			<td align="left" width="15%">
				<c:if test="${not empty abusiveReviewReportItem.survey.agentName }">
					${abusiveReviewReportItem.survey.agentName}
				</c:if>
			</td>
			<td width="3%"></td>
			<td align="left" width="30%">
				<c:if test="${not empty abusiveReviewReportItem.survey.review }">
					${abusiveReviewReportItem.survey.review}
				</c:if>
			</td>
			<td width="3%"></td>
			<c:if test="${not empty abusiveReviewReportItem.abuseReporterDetails}">
			    <%! int counter = 0; %>
				<c:forEach items="${abusiveReviewReportItem.abuseReporterDetails.abuseReporters}" var="reporterDetailItem">
					<td align="left" width="15%">${reporterDetailItem.reporterName}</td>
					<td width="3%"></td>
					<td align="left" width="30%">${reporterDetailItem.reporterEmail}</td>
					</tr>
					<% counter++; %>
					<c:if test="${fn:length(companies) ne counter}">
						<tr>
						<td align="left" width="15%"></td>
						<td width="3%"></td>
						<td align="left" width="15%"></td>
						<td width="3%"></td>
					</c:if>
				</c:forEach>
			</c:if>
		</tr>
	</c:forEach>
</c:if>