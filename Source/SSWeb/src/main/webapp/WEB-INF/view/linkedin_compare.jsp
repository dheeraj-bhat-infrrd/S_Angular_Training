<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profile}">
	<c:set value="${profile.profilesMaster.profileId}" var="profilemasterid"></c:set>
</c:if>
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.linkedInProfileData}" var="linkedInData"></c:set>
</c:if>

<div class="welcome-popup-hdr-wrapper clearfix">
	<div class="float-left wc-hdr-txt"><spring:message code="label.aboutbusiness.key" /></div>
	<div class="float-right wc-hdr-step"><spring:message code="label.step.two.key" /></div>
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
							<div id="wc-photo-upload" class="wc-photo-upload cursor-pointer"></div>
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
<div class="wc-btn-row clearfix" data-page="two">
	<div class="wc-btn-col float-left">
		<div class="wc-skip-btn float-right"><spring:message code="label.skipthisstep.key" /></div>
	</div>
	<div class="wc-btn-col float-left">
		<div class="wc-sub-btn float-left wc-next-btn"><spring:message code="label.nextstep.key" /></div>
	</div>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/jcrop/jquery.Jcrop.min.css">
<script src="${pageContext.request.contextPath}/resources/jcrop/jquery.Jcrop.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/jcrop.js"></script>
<script>
var selectedCountryRegEx = "";
var profilemasterid = "${profilemasterid}";
$(document).ready(function() {
	if ($('#com-country').val() != "" && $('#country-code').val() != "") {
		var countryCode = $('#country-code').val();
		for (var i = 0; i < postCodeRegex.length; i++) {
			if (postCodeRegex[i].code == countryCode) {
				selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
				selectedCountryRegEx = new RegExp(selectedCountryRegEx);
				break;
			}
		}
	}
	
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
		open : function(event, ui) {
			$("#country-code").val("");
		},
		focus : function(event, ui) {
			$("#com-country").val(ui.item.label);
			return false;
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
			return false;
		},
		close : function(event, ui) {
		}
	}).autocomplete("instance")._renderItem = function(ul, item) {
		return $("<li>").append(item.label).appendTo(ul);
	};
});

// Profile image upload
$(document).on('click', '#prof-image-upload-btn', function() {
	$('#prof-image').trigger('click');
});

$(document).on('change', '#prof-image', function() {
	initiateJcrop(this);
});

function callBackOnProfileImageUpload(data) {
	$('#message-header').html(data);
	callAjaxGET("./fetchuploadedprofileimage.do", function(profileImageUrl) {
		if (profilemasterid == 4) {
			$("#wc-photo-upload").removeClass('dsh-pers-default-img');
		} else if (profilemasterid == 3) {
			$("#wc-photo-upload").removeClass('dsh-office-default-img');
		} else if (profilemasterid == 2) {
			$("#wc-photo-upload").removeClass('dsh-region-default-img');
		} else if (profilemasterid == 1) {
			$("#wc-photo-upload").removeClass('dsh-comp-default-img');
		}
		
		$('#wc-photo-upload').css("background", "url(" + profileImageUrl + ") no-repeat center");
		$('#wc-photo-upload').css("background-size","contain");
		hideOverlay();
	});

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Company information
function validateCompanyInformationForm(elementId) {
	var isCompanyInfoPageValid = true;
	var isFocussed = false;

	if (!validateAddress1('com-address1')) {
		isCompanyInfoPageValid = false;
		if (!isFocussed) {
			$('#com-address1').focus();
			isFocussed = true;
		}
		return isCompanyInfoPageValid;
	}
	if (!validateAddress2('com-address2')) {
		isCompanyInfoPageValid = false;
		if (!isFocussed) {
			$('#com-address2').focus();
			isFocussed = true;
		}
		return isCompanyInfoPageValid;
	}
	if (!validateCountry('com-country')) {
		isCompanyInfoPageValid = false;
		if (!isFocussed) {
			$('#com-country').focus();
			isFocussed = true;
		}
		return isCompanyInfoPageValid;
	}
	if (!validateCountryZipcode('com-zipcode')) {
		isCompanyInfoPageValid = false;
		if (!isFocussed) {
			$('#com-zipcode').focus();
			isFocussed = true;
		}
		return isCompanyInfoPageValid;
	}
	if (!validatePhoneNumber('com-contactno')) {
		isCompanyInfoPageValid = false;
		if (!isFocussed) {
			$('#com-contactno').focus();
			isFocussed = true;
		}
		return isCompanyInfoPageValid;
	}
	return isCompanyInfoPageValid;
}

function validateCountry() {
	var country = $.trim($('#com-country').val());
	if (country == "") {
		return false;
	} else {
		var countryCode = $.trim($('#country-code').val());
		if (countryCode == "") {
			return false;
		} else {
			return true;
		}
	}
}

$(document).on('click', '#wc-address-submit', function() {
	if (validateCompanyInformationForm()) {
		var payload = {
			"address1" : $('#com-address1').val(),
			"address2" : $('#com-address2').val(),
			"country" : $('#com-country').val(),
			"countrycode" : $('#country-code').val(),
			"zipcode" : $('#com-zipcode').val(),
			"contactno" : $('#com-contactno').val()
		};
		callAjaxPostWithPayloadData("./editcompanyinformation.do", function(data) {
			$('#message-header').html(data);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload, false);
	}
});

// Summary
function validateSummaryForm() {
	var isFocussed = false;
	var isFormValid = true;

	if (!validateInputField('wc-industry')) {
		$('#overlay-toast').html('Please enter industry');
		showToast();

		isFormValid = false;
		if (!isFocussed) {
			$('#wc-industry').focus();
			isFocussed = true;
		}
		return isFormValid;
	}
	if (!validateInputField('wc-location')) {
		$('#overlay-toast').html('Please enter location');
		showToast();

		isFormValid = false;
		if (!isFocussed) {
			$('#wc-location').focus();
			isFocussed = true;
		}
		return isFormValid;
	}
	if (!validateTextArea('wc-summary')) {
		$('#overlay-toast').html('Please add or edit summary');
		showToast();

		isFormValid = false;
		if (!isFocussed) {
			$('#wc-summary').focus();
			isFocussed = true;
		}
		return isFormValid;
	}
	return isFormValid;
}

$(document).on('click', '#wc-summary-submit', function() {
	if (validateSummaryForm()) {
		var payload = {
			"industry" : $('#wc-industry').val(),
			"location" : $('#wc-location').val(),
			"aboutme" : $('#wc-summary').val()
		};
		callAjaxPostWithPayloadData("./updatesummarydata.do", function(data) {
			$('#message-header').html(data);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload, false);
	}
});
</script>