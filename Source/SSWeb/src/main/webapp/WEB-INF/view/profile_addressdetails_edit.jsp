<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

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

<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
</c:if>
<c:if test="${profilemasterid == 4 }">
	<c:if test="${empty contactdetail.address1 && not empty profileSettings.companyProfileData}">
		<c:set value="${profileSettings.companyProfileData}" var="contactdetail"></c:set>
	</c:if>
</c:if>
<div id="prof-address-edit-container" class="prof-user-address prof-edit-icn">
	<form id="prof-edit-address-form">
		<input id="prof-address1" name="address1" class="pu-edit-fields" value="${contactdetail.address1}" placeholder='<spring:message code="label.address.address1.key"/>'>
		<input id="prof-address2" name="address2" class="pu-edit-fields" value="${contactdetail.address2}" placeholder='<spring:message code="label.address.address2.key"/>'>
		<input id="prof-country" name="country" class="pu-edit-fields" value="${contactdetail.country}" placeholder='<spring:message code="label.address.country.key"/>'>
		<input type="hidden" name="countryCode" id="prof-country-code" class="pu-edit-fields" value="${contactdetail.countryCode}">
		<div id="prof-address-state-city-row">
			<select id="prof-state" name="state" class="pu-edit-fields" data-value="${contactdetail.state}">
				<option disabled selected><spring:message code="label.select.state.key"/></option>
			</select>
			<input id="prof-city" name="city" class="pu-edit-fields" value="${contactdetail.city}" placeholder='<spring:message code="label.address.city.key"/>'>
		</div>
		<input id="prof-zipcode" name="zipCode" class="pu-edit-fields" value="${contactdetail.zipcode}" placeholder='<spring:message code="label.address.zipcode.key"/>'>
	</form>
</div>