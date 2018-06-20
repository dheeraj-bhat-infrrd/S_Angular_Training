<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty abusiveReviewReportList}">
	<c:forEach items="${abusiveReviewReportList}" var="abusiveReviewReportItem">
		<c:forEach items="${abusiveReviewReportItem.abuseReporterDetails.abuseReporters}" var="reporterDetailItem">
			<div class="abuse-review-item abuse-review-row row" data-iden="${abusiveReviewReportItem.survey._id}">
				<div class="abuse-text-window capitalize col-lg-2 col-md-2 col-sm-2 col-xs-2">
					<c:if test="${not empty abusiveReviewReportItem.survey.agentName }">
						${abusiveReviewReportItem.survey.agentName}
					</c:if>
				</div>
				<div class="abuse-text-window overflow-y-scroll col-lg-3 col-md-3 col-sm-3 col-xs-3">
					<c:if test="${not empty abusiveReviewReportItem.survey.review }">
						${abusiveReviewReportItem.survey.review}
					</c:if>
				</div>
				<div class="abuse-text-window capitalize col-lg-2 col-md-2 col-sm-2 col-xs-2">${reporterDetailItem.reporterName}</div>
				<div class="abuse-text-window overflow-x-scroll col-lg-2 col-md-2 col-sm-2 col-xs-2">${reporterDetailItem.reporterEmail}</div>
				<div class="abuse-text-window abuse-report-col overflow-y-scroll col-lg-2 col-md-2 col-sm-2 col-xs-2">
					<c:if test="${not empty reporterDetailItem.reportReason }">
						${reporterDetailItem.reportReason}
					</c:if>
				</div>
				<div class="abuse-text-window col-lg-1 col-md-1 col-sm-1 col-xs-1">
					<div class="v-tbl-icn v-icn-edit-user unmark-abusive-icn" title="Unmark Abusive"></div>
				</div>
			</div>
		</c:forEach>
	</c:forEach>
</c:if>