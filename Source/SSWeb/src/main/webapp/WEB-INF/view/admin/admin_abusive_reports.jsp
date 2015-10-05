<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty abusiveReviewReportList}">
	<c:forEach items="${abusiveReviewReportList}" var="abusiveReviewReportItem">
		<c:forEach items="${abusiveReviewReportItem.abuseReporterDetails.abuseReporters}" var="reporterDetailItem">
			<div class="abuse-review-row row">
				<div class="abuse-report-col capitalize col-lg-3 col-md-3 col-sm-3 col-xs-3">
					<c:if test="${not empty abusiveReviewReportItem.survey.agentName }">
						${abusiveReviewReportItem.survey.agentName}
					</c:if>
				</div>
				<div class="abuse-report-col col-lg-3 col-md-3 col-sm-3 col-xs-3">
					<c:if test="${not empty abusiveReviewReportItem.survey.review }">
						${abusiveReviewReportItem.survey.review}
					</c:if>
				</div>
				<div class="abuse-report-col capitalize col-lg-3 col-md-3 col-sm-3 col-xs-3">${reporterDetailItem.reporterName}</div>
				<div class="abuse-report-col col-lg-3 col-md-3 col-sm-3 col-xs-3">${reporterDetailItem.reporterEmail}</div>
			</div>
		</c:forEach>
	</c:forEach>
</c:if>