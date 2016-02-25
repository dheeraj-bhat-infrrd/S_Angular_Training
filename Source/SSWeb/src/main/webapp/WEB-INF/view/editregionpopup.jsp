<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="prof-address-edit-container" class="prof-user-address prof-edit-icn">
	<form id="prof-edit-address-form">
		<input id="name" name="name" class="pu-edit-fields" value="" placeholder='Add Region Name'>
		<input id="address1" name="address1" class="pu-edit-fields" value="" placeholder='Add Address Line 1'>
		<input id="address2" name="address2" class="pu-edit-fields" value="" placeholder='Add Address Line 2'>
		<div id="address-state-city-row">
			<select id="state" name="state" class="pu-edit-fields" data-value="">
				<option disabled selected><spring:message code="label.select.state.key"/></option>
			</select>
			<input id="city" name="city" class="pu-edit-fields" value="" placeholder='<spring:message code="label.address.city.key"/>'>
		</div>
		<input id="zip" name="zip" class="pu-edit-fields" value="" placeholder='<spring:message code="label.address.zipcode.key"/>'>
	</form>
</div>