<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
</c:if>

<div id="prof-address-edit-container" class="prof-user-address prof-edit-icn">
	<form id="prof-edit-address-form">
		<input id="prof-name" name="profName" class="pu-edit-fields" value="${contactdetail.name}" placeholder='<spring:message code="label.address.displayname.key"/>'>
		<input id="prof-address1" name="address1" class="pu-edit-fields" value="${contactdetail.address1}" placeholder='<spring:message code="label.address.address1.key"/>'>
		<input id="prof-address2" name="address2" class="pu-edit-fields" value="${contactdetail.address2}" placeholder='<spring:message code="label.address.address2.key"/>'>
		<input id="prof-country" name="country" class="pu-edit-fields" value="${contactdetail.country}" placeholder='<spring:message code="label.address.country.key"/>'>
		<input type="hidden" name="countryCode" id="prof-country-code" class="pu-edit-fields" value="${contactdetail.countryCode}">
		<input id="prof-state" name="state" class="pu-edit-fields" value="${contactdetail.state}" placeholder='<spring:message code="label.address.state.key"/>'>
		<input id="prof-city" name="city" class="pu-edit-fields" value="${contactdetail.city}" placeholder='<spring:message code="label.address.city.key"/>'>
		<input id="prof-zipcode" name="zipCode" class="pu-edit-fields" value="${contactdetail.zipcode}" placeholder='<spring:message code="label.address.zipcode.key"/>'>
	</form>
</div>
<script>
$("#prof-country").autocomplete({
	minLength: 1,
	source: countryData,
	delay : 0,
	open : function(event, ui) {
		$( "#prof-country-code" ).val("");
	},
	focus: function(event, ui) {
		$( "#prof-country" ).val(ui.item.label);
		return false;
	},
	select: function(event, ui) {
		$("#prof-country").val(ui.item.label);
		$("#prof-country-code").val(ui.item.code);
		for (var i = 0; i < postCodeRegex.length; i++) {
			if (postCodeRegex[i].code == ui.item.code) {
				selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
				selectedCountryRegEx = new RegExp(selectedCountryRegEx);
				break;
			}
		}
		if(ui.item.code=="US"){
			$('#prof-state').show();
			$('#prof-city').show();
			/* callAjaxGET("", function(data){
				
			}, true); */
		}else{
			$('#prof-state').hide().val('');
			$('#prof-city').hide().val('');
		}
		return false;
	},
	close: function(event, ui) {},
	create: function(event, ui) {
        $('.ui-helper-hidden-accessible').remove();
	}
}).autocomplete("instance")._renderItem = function(ul, item) {
	return $("<li>").append(item.label).appendTo(ul);
};
</script>