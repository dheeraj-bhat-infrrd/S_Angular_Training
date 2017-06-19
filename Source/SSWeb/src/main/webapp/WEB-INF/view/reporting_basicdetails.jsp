<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.vertical}" var="companyvertical"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>
<%--c:if test="${not empty USER_ZILLOW_NMLS_ID}">
	<c:set value="${USER_ZILLOW_NMLS_ID}" var="nmls"/>
</c:if --%>

<c:set value="${NMLS}" var="nmls"/>
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
<c:if test="${not empty cannonicalusersettings && not empty cannonicalusersettings.companySettings && not empty cannonicalusersettings.companySettings.vertical}">
	<c:set value="${cannonicalusersettings.companySettings.vertical}" var="verticalVal"></c:set>
</c:if>

<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
	<div id="name" class="prof-name prof-name-txt rep-dsh-large-text dsh-txt-1">${contactdetail.name}</div>
</div>
<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
	<div id="designation-nmls" class="prof-addline2 prof-name-txt rep-dsh-medium-text dsh-txt-2" >${contactdetail.title}| ${nmls}</div>
</div>
<div id="prof-rating-review-count" class="prof-rating clearfix">
	<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
	<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
</div>
<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}" style="margin:0">
	<div id="dsh-btn1" class="dsh-btn-complete float-left"><spring:message code="label.sendsurvey.btn.key" /></div>
</div>

