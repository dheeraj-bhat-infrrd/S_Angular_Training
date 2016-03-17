<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="float-left stats-right stats-right-survey">
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.surveyssent.key" /></div>
		<div id="all-surv-icn" class="float-left stat-icns-item clearfix">
			<c:choose>
				<c:when test="${not empty allSurveySent && allSurveySent != 0}">
					<c:forEach begin="1" end="${allSurveySent<20?allSurveySent:20}" var="countone">
						<div class='float-left stat-icn-img stat-icn-img-green'></div>
					</c:forEach>
					<div id='survey-sent' class='float-left stat-icn-txt-rt'>${allSurveySent}</div>
				</c:when>
				<c:otherwise>
					<div id='survey-sent' class='float-left stat-icn-txt-rt'>0</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.surveysclicked.key" /></div>
		<div id="clicked-surv-icn" class="float-left stat-icns-item clearfix">
			<c:choose>
				<c:when test="${not empty allSurveySent && allSurveySent != 0 && not empty clickedSurvey && clickedSurvey != 0}">
					<fmt:formatNumber type="number" var="clickedSurveyCount" value="${clickedSurvey * (allSurveySent<20?allSurveySent:20) / allSurveySent}" maxFractionDigits="0"/>
					<fmt:formatNumber type="number" var="clickedPrc" value="${clickedSurvey*100/allSurveySent}" maxFractionDigits="0" />
					<c:forEach begin="1" end="${clickedSurveyCount}" var="counttwo">
						<div class='float-left stat-icn-img stat-icn-img-blue'></div>
					</c:forEach>
					<div id='survey-clicked' class='float-left stat-icn-txt-rt'>${clickedSurvey} &nbsp; (${clickedPrc}%) </div>
				</c:when>
				<c:otherwise>
					<div id='survey-clicked' class='float-left stat-icn-txt-rt'>0%</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.surveyscompleted.key" /></div>
		<div id="completed-surv-icn" class="float-left stat-icns-item clearfix">
			<c:choose>
				<c:when test="${not empty allSurveySent && allSurveySent != 0 && not empty completedSurvey && completedSurvey != 0}">
					<fmt:formatNumber type="number" var="completedSurveyCount" value="${completedSurvey * (allSurveySent<20?allSurveySent:20) / allSurveySent}" maxFractionDigits="0"/>
					<fmt:formatNumber type="number" var="completedPrc" value="${completedSurvey*100/allSurveySent}" maxFractionDigits="0" />
					<c:forEach begin="1" end="${completedSurveyCount}" var="counttwo">
						<div class="float-left stat-icn-img stat-icn-img-yellow"></div>
					</c:forEach>
					<div id='survey-completed' class='float-left stat-icn-txt-rt'>${completedSurvey} &nbsp; (${completedPrc}%) </div>
				</c:when>
				<c:otherwise>
					<div id='survey-completed' class='float-left stat-icn-txt-rt'>0%</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	
	<div class="clearfix stat-icns-wrapper">
		<div class="float-left stat-icn-lbl"><spring:message code="label.surveyssocialposts.key" /></div>
		<div id="social-post-icn" class="float-left stat-icns-item clearfix">
			<c:choose>
				<c:when test="${not empty allSurveySent && allSurveySent != 0 && not empty socialPosts && socialPosts != 0}">
					<fmt:formatNumber type="number" var="socialPostsCount" value="${socialPosts * 20 / allSurveySent}" maxFractionDigits="0"/>
					<c:forEach begin="1" end="${socialPosts<20?socialPosts:20}" var="counttwo">
						<div class="float-left stat-icn-img stat-icn-img-red"></div>
					</c:forEach>
					<div id="social-posts" class="float-left stat-icn-txt-rt">${socialPosts}</div>
				</c:when>
				<c:otherwise>
					<div id="social-posts" class="float-left stat-icn-txt-rt">0</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<c:if test="${not empty importedFromZillow && importedFromZillow > 0 }">
		<div class="clearfix stat-icns-wrapper">
			<div class="float-left stat-icn-lbl"><spring:message code="label.zillowreviews.key" /></div>
			<div id="clicked-surv-icn" class="float-left stat-icns-item clearfix">
				<fmt:formatNumber type="number" var="importedFromZillowCount" value="${importedFromZillow}" maxFractionDigits="0" />
				<c:forEach begin="1" end="${importedFromZillow<20?importedFromZillow:20}" var="counttwo">
					<div class='float-left stat-icn-img stat-icn-img-blue'></div>
				</c:forEach>
				<div id='survey-zillow' class='float-left stat-icn-txt-rt'>${importedFromZillow}</div>
			</div>
		</div>
	</c:if>
</div>