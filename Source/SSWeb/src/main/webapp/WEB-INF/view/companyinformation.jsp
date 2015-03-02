<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.information.key" /></title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>

<body>
<div class="hdr-wrapper">
	<div class="container hdr-container clearfix">
		<div class="float-left hdr-logo"></div>
		<div class="float-right clearfix hdr-btns-wrapper">
			<div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key" /></div>
			<div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key" /></div>
		</div>
	</div>
</div>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center"><spring:message code="label.signupstartjourney.key" /></div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header"><spring:message code="label.companysettings.header.key"/></div>
		
		<form id="company-info-form" method="POST" action="./addcompanyinformation.do" enctype="multipart/form-data">
			<div class="reg_form_wrapper_2">
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.company.key"/></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-company"></div>
						<input class="rfr_txt_fld" id="com-company" data-non-empty="true"
							name="company" placeholder='<spring:message code="label.company.key"/>'>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.logo.key"/></div>
					<div class="float-left rfr_txt">
						<div class="icn-lname input-file-icn-left" id="input-file-icn-left"></div>
						<input type="text" class="rfr_txt_fld" id="com-logo-decoy" placeholder='<spring:message code="label.logo.key"/>'>
						<input type="file" class="rfr_txt_fld" id="com-logo" name="logo" placeholder='<spring:message code="label.logo.key"/>'>
						<div class="float-right input-icon-internal icn-file file-pick-logo" id="icn-file"></div>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.address.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-fname"></div>
						<input class="rfr_txt_fld" id="com-address1" data-non-empty="true"
							name="address1" placeholder='<spring:message code="label.address1.key"/>'>
					</div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-lname"></div>
						<input class="rfr_txt_fld" id="com-address2"
							name="address2" placeholder='<spring:message code="label.address2.key"/>'>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl" style="visibility:hidden;"><spring:message code="label.address.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-fname"></div>
						<input class="rfr_txt_fld" id="com-country" data-non-empty="true"
							name="country" placeholder='<spring:message code="label.country.key"/>'>
					</div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-lname"></div>
						<input class="rfr_txt_fld" id="com-zipcode" data-non-empty="true" data-zipcode="true"
							name="zipcode" placeholder='<spring:message code="label.zipcode.key"/>'>
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.phoneno.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn icn-mbl"></div>
						<input class="rfr_txt_fld" id="com-contactno" data-non-empty="true" data-phone="true"
							name="contactno" placeholder="<spring:message code="label.phoneno.key" />">
					</div>
				</div>
				<div class="reg_form_row clearfix">
					<div class="float-left rfr_lbl"><spring:message code="label.businessvertical.key" /></div>
					<div class="float-left rfr_txt">
						<div class="rfr_icn"></div>
						<c:if test="${not empty verticals}">
							<select name="vertical" id="select-vertical" class="rfr_txt_fld">
								<option disabled selected><spring:message code="label.vertical.key"/></option>
								<c:forEach items="${verticals }" var="vertical">
									<option id="vertical-${vertical.verticalsMasterId}">${vertical.verticalName}</option>
								</c:forEach>
							</select>
						</c:if>
					</div>
				</div>
				
				<div class="reg_form_row clearfix">
					<div class="reg_btn" id="company-info-submit"><spring:message code="label.done.key" /></div>
				</div>
			</div>
			<input type="hidden" value="${emailid}" name="originalemailid" id="originalemailid">
			<input type="hidden" name="countrycode" id="country-code">
		</form>
		
	</div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/countrydata.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/zipcoderegex.js"></script>
<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script>
var isCompanyInfoPageValid;
var selectedCountryRegEx = "";
$(document).ready(function() {
	isCompanyInfoPageValid = false;

	$('#company-info-submit').click(function() {
		submitCompanyInfoForm();
	});
	
	$('#icn-file').click(function(){
		$('#com-logo').trigger('click');
	});
	
	$('#com-logo').change(function(){
		var fileAdd = $(this).val().split('\\');
		$('#com-logo-decoy').val(fileAdd[fileAdd.length - 1]);
	});
	
	// Integrating autocomplete with country input text field
	$( "#com-country" ).autocomplete({
		minLength: 1,
		source: countryData,
		delay : 0,
		open : function( event, ui ) {
			$( "#country-code" ).val("");
		},
		focus: function( event, ui ) {
			$( "#com-country" ).val( ui.item.label );
			return false;
		},
		select: function( event, ui ) {
			$( "#com-country" ).val( ui.item.label );
			$( "#country-code" ).val( ui.item.code );
			for(var i=0;i<postCodeRegex.length;i++){
				if(postCodeRegex[i].code == ui.item.code){
					selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
					selectedCountryRegEx = new RegExp(selectedCountryRegEx);
					break;
				}
			}
			return false;
		},
		close: function( event, ui ) {
		}})
		.autocomplete( "instance" )._renderItem = function( ul, item ) {
		return $( "<li>" )
			.append(item.label)
			.appendTo( ul );
	  };
});

$('#com-company').blur(function() {
	if (validateCompany(this.id)) {
		hideError();
	}
});

function submitCompanyInfoForm() {
	console.log("submitting company information form");
	if (validateCompanyInformationForm('company-info-div')) {
		$('#company-info-form').submit();
	}
}

$('input').keypress(function(e){
	// detect enter
	if (e.which==13){
		e.preventDefault();
		submitCompanyInfoForm();
	}
});

// Logo upload
$("#com-logo").on("change", function() {
	var formData = new FormData();
	formData.append("logo", $('#com-logo').prop("files")[0]);
	formData.append("logo_name", $('#com-logo').prop("files")[0].name);
	callAjaxPOSTWithTextData("./uploadcompanylogo.do", uploadImageSuccessCallback, true, formData);
});

function uploadImageSuccessCallback(response) {
	$("#serverSideerror").html(response);
	var success = "Logo has been uploaded successfully";
	var successMsg = $("#serverSideerror").find('.display-message').text().trim();
	if (success != successMsg) {
		$('#com-logo').val('');
		$('#com-logo-decoy').val('');
	}
}

// Validate Country
$('#com-address1').blur(function() {
	if (validateAddress1(this.id)) {
		hideError();
	}
});

$('#com-address2').blur(function() {
	validateAddress2(this.id);
});

$('#com-zipcode').blur(function() {
	if (validateCountryZipcode(this.id)) {
		hideError();
	}
});

$('#com-contactno').blur(function() {
	if (validatePhoneNumber(this.id)) {
		hideError();
	}
});

function validateCountry() {
	var country = $.trim($('#com-country').val());
	if (country == "") {
		// $('#com-page-country').html("Please enter country name");
		// $('#com-page-country').show();
		showError('Please enter country name');
		return false;
	} else {
		var countryCode = $.trim($('#country-code').val());
		if (countryCode == "") {
			// $('#com-page-country').html("Please enter valid country name");
			// $('#com-page-country').show();
			showError('Please enter valid country name');
			return false;
		} else {
			// $('#com-page-country').html("");
			// $('#com-page-country').hide();
			return true;
		}
	}
}

//Function to validate the zipcode
function validateCountryZipcode(elementId) {
	var zipcode = $('#'+elementId).val();
	if ($(window).width()<768) {
		if (zipcode != "") {
			if (selectedCountryRegEx.test(zipcode) == true) {
				return true;
			} else {
				// $('#overlay-toast').html('Please enter a valid zipcode.');
				// showToast();
				showError('Please enter a valid zipcode');
				return false;
			}
		} else {
			// $('#overlay-toast').html('Please enter zipcode.');
			// showToast();
			showError('Please enter zipcode');
			return false;
		}
	} else {
		if (zipcode != "") {
			if (selectedCountryRegEx.test(zipcode) == true) {
				// $('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			} else {
				// $('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid zipcode.');
				// $('#'+elementId).parent().next('.login-reg-err').show();
				showError('Please enter a valid zipcode');
				return false;
			}
		} else {
			// $('#'+elementId).parent().next('.login-reg-err').html('Please enter zipcode.');
			// $('#'+elementId).parent().next('.login-reg-err').show();
			showError('Please enter zipcode');
			return false;
		}
	}
}

function validateCompanyInformationForm(elementId) {
	isCompanyInfoPageValid = true;
	var isFocussed = false;
	var isSmallScreen = false;
	if($(window).width()<768){
		isSmallScreen = true;
	}
	
	if(!validateCompany('com-company')){
		isCompanyInfoPageValid = false;
		if(!isFocussed){
			$('#com-company').focus();
			isFocussed=true;
		}
		if(isSmallScreen){
			return isCompanyInfoPageValid;
		}
	}
	if(!validateAddress1('com-address1')){
		isCompanyInfoPageValid = false;
		if(!isFocussed){
			$('#com-address1').focus();
			isFocussed=true;
		}
		if(isSmallScreen){
			return isCompanyInfoPageValid;
		}
	}
	if(!validateAddress2('com-address2')){
		isCompanyInfoPageValid = false;
		if(!isFocussed){
			$('#com-address2').focus();
			isFocussed=true;
		}
		if(isSmallScreen){
			return isCompanyInfoPageValid;
		}
	}
	
	if(!validateCountry('com-country')){
		isCompanyInfoPageValid = false;
		if(!isFocussed){
			$('#com-country').focus();
			isFocussed=true;
		}
		if(isSmallScreen){
			return isCompanyInfoPageValid;
		}
	}
	
	if(!validateCountryZipcode('com-zipcode')){
		isCompanyInfoPageValid = false;
		if(!isFocussed){
			$('#com-zipcode').focus();
			isFocussed=true;
		}
		if(isSmallScreen){
			return isCompanyInfoPageValid;
		}
	}
	if(!validatePhoneNumber('com-contactno')){
		isCompanyInfoPageValid = false;
		if(!isFocussed){
			$('#com-contactno').focus();
			isFocussed=true;
		}
		if(isSmallScreen){
			return isCompanyInfoPageValid;
		}
	}
	return isCompanyInfoPageValid;
}
</script>

</body>
</html>