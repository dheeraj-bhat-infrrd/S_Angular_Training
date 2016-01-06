<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
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
	<c:set value="${profileSettings.linkedInProfileData}" var="linkedInData"></c:set>
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
</c:if>
<%-- <c:if test="${not empty cannonicalusersettings}">
	<c:set value="${cannonicalusersettings.companySettings.contact_details}" var="contactdetail"></c:set>
</c:if> --%>

<div class="welcome-popup-hdr-wrapper clearfix">
	<div class="float-left wc-hdr-txt"><spring:message code="label.aboutbusiness.key" /></div>
	<div class="float-right popup-close-icn wc-skip-btn"></div>
	<%-- <div class="float-right wc-hdr-step"><spring:message code="label.step.two.key" /></div> --%>
</div>
<div class="welcome-popup-body-wrapper clearfix">
	<div class="wc-popup-body-hdr"><spring:message code="label.linkedin.imported.key" />
		<div class="float-right linkedin-import-hdr"></div>
	</div>
	<div class="wc-popup-body-cont">
		<div class="wc-step2-body-row">
			<div class="wc-step2-body-row-hdr"><spring:message code="label.photo.upload.key" /></div>
			<div class="wc-step2-body-row-cont">
				<div class="wc-edit-photo-cont clearfix">
					<div class="wc-edit-photo-cont-col float-left">
						<div class="float-right">
							<div class="wc-linkedin-photo"
								style="background: url(${linkedInData.pictureUrls.values[0]}) no-repeat center; background-size: contain"></div>
							<div class="wc-linkedin-photo-txt">
								<spring:message code="label.photo.from.key" />
								<span class="wc-highlight"><spring:message code="label.linkedin.key" /></span>
							</div>
						</div>
					</div>
					<div class="wc-div-txt float-left"><spring:message code="label.or.key" /></div>
					<div class="wc-edit-photo-cont-col float-left">
						<div class="float-left">
							<div id="wc-photo-upload" class="wc-photo-upload"></div>
							<form class="hide" enctype="multipart/form-data">
								<input type='file' id="prof-image" />
							</form>
							<div id="prof-image-upload-btn" class="wc-submit-btn"><spring:message code="label.upload.key" /></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="wc-step2-body-row">
			<div class="wc-step2-body-row-hdr"><spring:message code="label.editaddress.key"/></div>
			<div class="wc-step2-body-row-cont">
				<div class="wc-form-container">
					<form id="wc-address-form">
						<div class="wc-form-row clearfix">
							<div class="float-left wc-form-txt"><spring:message code="label.address.key"/></div>
							<div class="float-left wc-form-input-cont">
								<input class="wc-form-input" id="com-address1" data-non-empty="true" name="address1"
									value="${contactdetail.address1}" placeholder='<spring:message code="label.address1.key"/>'>
							</div>
							<div class="float-left wc-form-input-cont">
								<input class="wc-form-input" id="com-address2" name="address2"
									value="${contactdetail.address2}" placeholder='<spring:message code="label.address2.key"/>'>
							</div>
						</div>
						<div class="wc-form-row clearfix">
							<div class="float-left wc-form-txt"></div>
							<div class="float-left wc-form-input-cont">
								<input class="wc-form-input" id="com-country" data-non-empty="true" name="country"
									value="${contactdetail.country}" placeholder='<spring:message code="label.country.key"/>'>
								<input type="hidden" value="${contactdetail.countryCode}" name="countrycode" id="country-code">
							</div>
							<div class="float-left wc-form-input-cont">
								<input class="wc-form-input" id="com-zipcode" data-non-empty="true" data-zipcode="true" name="zipcode"
									value="${contactdetail.zipcode}" placeholder='<spring:message code="label.zipcode.key"/>'>
							</div>
						</div>
						<div id="state-city-row" class="wc-form-row clearfix hide">
							<div class="float-left wc-form-txt"></div>
							<div class="float-left wc-form-input-cont">
								<select class="wc-form-input" id="com-state" data-non-empty="true" name="state"
									data-value="${contactdetail.state}">
									<option disabled selected><spring:message code="label.select.state.key"/></option>
								</select>
							</div>
							<div class="float-left wc-form-input-cont">
								<input class="wc-form-input" id="com-city" data-non-empty="true" data-zipcode="true" name="city"
									value="${contactdetail.city}" placeholder='<spring:message code="label.city.key"/>'>
							</div>
						</div>
						<div class="wc-form-row clearfix">
							<div class="float-left wc-form-txt"><spring:message code="label.phoneno.key" /></div>
							<div class="float-left wc-form-input-cont">
								<input class="wc-form-input" id="com-contactno" data-non-empty="true" data-phone="true" name="contactno"
									value="${contactdetail.contact_numbers.work}" placeholder="<spring:message code="label.phoneno.key" />">
							</div>
						</div>
						<div class="wc-form-row clearfix">
							<div id="wc-address-submit" class="reg_btn"><spring:message code="label.update.key" /></div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="wc-step2-body-row">
			<div class="wc-step2-body-row-hdr"><spring:message code="label.tellus.key" /></div>
			<div class="wc-step2-body-row-cont">
				<div class="wc-prof-details">
					<div class="wc-prof-hdr"><spring:message code="label.about.key" />${contactdetail.name}</div>
					<form id="wc-summary-form">
						<div class="wc-prof-details-row clearfix">
							<div class="wc-prof-input-cont float-left">
								<input id="wc-industry" class="wc-form-input" value="${linkedInData.industry}"
									placeholder='<spring:message code="label.industry.key"/>'>
							</div>
							<div class="wc-prof-input-cont float-left">
								<input id="wc-location" class="wc-form-input" value="${linkedInData.location.name}"
									placeholder='<spring:message code="label.location.key"/>'>
							</div>
							<div class="wc-linkedin-photo-txt float-right">
								<spring:message code="label.data.from.key" />
								<span class="wc-highlight"><spring:message code="label.linkedin.key" /></span>
							</div>
						</div>
						<div class="wc-prof-details-row clearfix">
							<textarea id="wc-summary" class="wc-about-prof-txt"
								placeholder='<spring:message code="label.aboutcompany.empty.key"/>'>${linkedInData.summary}</textarea>
						</div>
						<div class="wc-prof-details-row clearfix">
							<div id="wc-summary-submit" class="wc-submit-btn float-right"><spring:message code="label.update.key" /></div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="wc-btn-row clearfix">
	<div class="wc-btn-col float-left">
		<div class="wc-skip-btn float-right"><spring:message code="label.skipthisstep.key" /></div>
	</div>
	<div class="wc-btn-col float-left">
		<div class="wc-sub-btn float-left wc-next-btn"><spring:message code="label.nextstep.key" /></div>
	</div>
</div>

<script>
var selectedCountryRegEx = "";
var phoneFormat = '(ddd) ddd-dddd';
var profilemasterid = "${profilemasterid}";
$(document).ready(function() {
	bindIndividualSignupPathEvents();
	var countryCode = $('#country-code').val();
	if ($('#com-country').val() != "" && $('#country-code').val() != "") {
		for (var i = 0; i < postCodeRegex.length; i++) {
			if (postCodeRegex[i].code == countryCode) {
				selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
				selectedCountryRegEx = new RegExp(selectedCountryRegEx);
				break;
			}
		}
	}
	
	if(countryCode == "US"){
		showStateCityRow("state-city-row", "com-state", "com-city");
		if( $('input[name="country"]').val() == null || $('input[name="country"]').val() == "" ){
			$('input[name="country"]').val("United States");
			$('#country-code').val(countryCode);
		}
		selectedCountryRegEx = "^" + "\\b\\d{5}\\b(?:[- ]{1}\\d{4})?" + "$";
		selectedCountryRegEx = new RegExp(selectedCountryRegEx);
	}
	
	if(countryCode && countryCode != ""){
		phoneFormat = phoneFormatList[$('#country-code').val()];
		var countryCode = $('#country-code').val();
		for (var i = 0; i < postCodeRegex.length; i++) {
			if (postCodeRegex[i].code == countryCode) {
				selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
				selectedCountryRegEx = new RegExp(selectedCountryRegEx);
				break;
			}
		}
	}
	
	$('#com-contactno').mask(phoneFormat, {'translation': {d: {pattern: /[0-9*]/}}});
	currentPhoneRegEx = phoneFormat;
	// update default image
	if (profilemasterid == 4) {
		$("#wc-photo-upload").addClass('dsh-pers-default-img');
	} else if (profilemasterid == 3) {
		$("#wc-photo-upload").addClass('dsh-office-default-img');
	} else if (profilemasterid == 2) {
		$("#wc-photo-upload").addClass('dsh-region-default-img');
	} else if (profilemasterid == 1) {
		$("#wc-photo-upload").addClass('dsh-comp-default-img');
	}
	
	// Integrating autocomplete with country input text field
	$("#com-country").autocomplete({
		minLength : 1,
		source : countryData,
		delay : 0,
		autoFocus : true,
		open : function(event, ui) {
			$("#country-code").val("");
		},
		select : function(event, ui) {
			$("#com-country").val(ui.item.label);
			$("#country-code").val(ui.item.code);
			for (var i = 0; i < postCodeRegex.length; i++) {
				if (postCodeRegex[i].code == ui.item.code) {
					selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
					selectedCountryRegEx = new RegExp(selectedCountryRegEx);
					break;
				}
			}
			if(ui.item.code=="US"){
				showStateCityRow("state-city-row", "com-state", "com-city");
			}else{
				hideStateCityRow("state-city-row", "com-state");
			}
			$('#com-contactno').unmask();
			phoneFormat = phoneFormatList[ui.item.code];
			currentPhoneRegEx = phoneFormat;
			$('#com-contactno').mask(phoneFormat, {'translation': {d: {pattern: /[0-9*]/}}});
			return false;
		},
		close : function(event, ui) {
		}
	}).autocomplete("instance")._renderItem = function(ul, item) {
		return $("<li>").append(item.label).appendTo(ul);
	};
});

$('#com-state').on('change',function(e){
	var stateId = $(this).find(":selected").attr('data-stateid');
	callAjaxGET("./getzipcodesbystateid.do?stateId="+stateId, function(data){
		var uniqueSearchData = getUniqueCitySearchData(data);
		initializeCityLookup(uniqueSearchData, "com-city");
	}, true);
});

$('#com-city').bind('focus', function(){ 
	if($('#com-state').val() &&  $('#com-state').val() != ""){
		$(this).trigger('keydown');
		//$(this).autocomplete("search");		
	}
});</script>