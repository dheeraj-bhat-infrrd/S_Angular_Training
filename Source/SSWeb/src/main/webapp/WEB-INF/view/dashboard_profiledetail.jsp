<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
	<c:when test="${entityType == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>

<div class="float-right dash-main-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
	<div class="dsh-graph-wrapper">
		<div class="dsh-g-wrap dsh-g-wrap-1">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-1" class="dsh-graph-img"></div>
				<div id="srv-scr" class="dsh-graph-num">${socialScore}</div>
				<div class="dsh-graph-txt dsh-graph-txt-1"><spring:message code="label.surveyscore.key" /></div>
			</div>
		</div>
		<div class="dsh-g-wrap dsh-g-wrap-2">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-2" class="dsh-graph-img"></div>
				<div id="srv-snt-cnt" class="dsh-graph-num">${surveyCount}</div>
				<div class="dsh-graph-txt dsh-graph-txt-2"><spring:message code="label.totalsurveys.key" /></div>
			</div>
		</div>
		<div class="dsh-g-wrap dsh-g-wrap-3">
			<div class="dsh-graph-item dsh-graph-item-3">
				<div id="dg-img-3" class="dsh-graph-img"></div>
				<div id="socl-post" class="dsh-graph-num">${socialPosts}</div>
				<div class="dsh-graph-txt dsh-graph-txt-3"><spring:message code="label.socialposts.key" /></div>
			</div>
		</div>
		
		<div class="dsh-g-wrap dsh-g-wrap-4">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-4" class="dsh-graph-img dsh-graph-img-4"></div>
				<div id="dsh-prsn-img" class="dsh-graph-num dsh-graph-num-4"></div>
				<div class="dsh-graph-txt dsh-graph-txt-4"><spring:message code="label.profilecomplete.key" /></div>
				<div id="badges" class="dsg-g-rbn dsg-g-rbn-${badges}"></div>
			</div>
		</div>
	</div>
</div>

<div class="float-left dash-main-left col-lg-6 col-md-6 col-sm-6 col-xs-12">
	<div class="dash-left-txt-wrapper">
		<div class="dsh-name-wrapper">
			<div id="name" class="dsh-txt-1">${name}</div>
			<c:if test="${not empty title}">
				<div id="designation" class="dsh-txt-2">${title}</div>
			</c:if>
			<c:if test="${not empty location || not empty vertical}">
				<div class="dsh-txt-3">
					<c:if test="${not empty location}">
						${location }
						<c:set var="isLocationTrue" value="yes"></c:set>	
					</c:if>
					<c:if test="${not empty vertical }">
						<c:if test="${isLocationTrue == 'yes'}"> | </c:if>
						${vertical}
					</c:if>
				</div>
			</c:if>
			<c:if test="${not empty company}">
				<div id="company" class="dsh-txt-3 hide">${company}</div>
			</c:if>
		</div>
		<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}">
			<div id="dsh-btn1" class="dsh-btn-complete float-left"><spring:message code="label.sendsurvey.btn.key" /></div>
			<div id="dsh-btn2" class="dsh-btn-complete dsh-btn-orange float-left hide"></div>
			<div id="dsh-btn3" class="dsh-btn-complete dsh-btn-green float-left hide"></div>
		</div>
	</div>
</div>