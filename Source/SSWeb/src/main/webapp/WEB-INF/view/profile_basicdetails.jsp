<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.vertical}" var="companyvertical"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>
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
	<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
	<div class="prof-edit-field-icn hide"></div>
</div>
<div class="prof-address">
	<div class="prof-edditable-cont">
		<input id="prof-title" class="prof-addline2 prof-edditable" value="${contactdetail.title}"
			placeholder='<spring:message code="label.profiletitle.placeholder.key"/>'>
		<div id="prof-title-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
		<div class="prof-edit-field-icn hide"></div>
	</div>
	<c:if test="${profilemasterid == 4}">
		<div class="prof-edditable-cont">
			<input id="prof-location" class="prof-addline2 prof-edditable" value="${contactdetail.location}"
				placeholder='<spring:message code="label.location.placeholder.key"/>'>
			<div id="prof-location-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
			<div class="prof-edit-field-icn hide"></div>
		</div>
	</c:if>
	<c:choose>
		<c:when test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="prof-edditable-cont">
				<input id="prof-vertical" class="prof-addline2 prof-edditable" value="${verticalVal}"
					placeholder='<spring:message code="label.profilevertical.placeholder.key"/>'>
				<div id="prof-vertical-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
				<div class="prof-edit-field-icn hide"></div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="prof-edditable-cont">
			<input class="prof-addline2 prof-edditable" value="${verticalVal}"
				placeholder='<spring:message code="label.profilevertical.placeholder.key"/>' readonly="readonly">
		</div>
		</c:otherwise>
	</c:choose>
</div>
<div id="prof-rating-review-count" class="prof-rating clearfix">
	<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
	<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
</div>
<script>
	initializeVerticalsMasterForProfilePage();
</script>