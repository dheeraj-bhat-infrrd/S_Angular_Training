<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<!DOCTYPE>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.information.key" /></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
</head>

<body>
	<div id="toast-container" class="toast-container">
		<span id="overlay-toast" class="overlay-toast"></span>
	</div>
	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
			<div class="float-right clearfix hdr-btns-wrapper">
				<div id="header-user-info" class="header-user-info float-right clearfix">
					<div class="float-left user-info-initial">
						<span id="usr-initl">${fn:substring(user.firstName, 0, 1)}</span>
					</div>
               		<div class="float-left user-info-sing-out">
                   		<a class="" href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
               		</div>
				</div>
			</div>
		</div>
	</div>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="hm-header-row-left text-center lgn-adj"><spring:message code="label.signupstartjourney.key" /></div>
			</div>
		</div>
	</div>
	
	<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
		<div class="container reg_panel_container" id="company-info-load">
			<div class="reg_header"><spring:message code="label.companysettings.header.key"/></div>
			
			<form id="company-info-form" method="POST" action="./addcompanyinformation.do" enctype="multipart/form-data" autocomplete="off" >
				<div class="reg_form_wrapper_2">
					<div class="reg_form_row clearfix">
						<div class="float-left rfr_lbl"><spring:message code="label.company.key"/></div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-company"></div>
							<div class="rfr_txt_fld">
								<input class="rfr_input_fld" id="com-company" data-non-empty="true"
								name="company" value="${companyName}" placeholder='<spring:message code="label.company.key"/>'>
							</div>
						</div>
					</div>
					<div class="reg_form_row clearfix">
						<div class="float-left rfr_lbl"><spring:message code="label.logo.key"/></div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-logo"></div>
							<div class="icn-lname input-file-icn-left" id="input-file-icn-left"></div>
							<div class="rfr_txt_fld">
								<input type="text" class="rfr_input_fld" id="com-logo-decoy" name="logoDecoyName"
									 placeholder='<spring:message code="label.logo.placeholder.key"/>' value="${logoDecoyName}">
							</div>
							<div><input type="file" class="rfr_input_fld com-logo-comp-info" id="com-logo" name="logo"></div>
							<div class="float-right input-icon-internal icn-file file-pick-logo file-pick-logo-adj" id="icn-file"></div>
						</div>
					</div>
					<div class="reg_form_row clearfix">
						<div class="float-left rfr_lbl"><spring:message code="label.address.key" /></div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-address1"></div>
							<div class="rfr_txt_fld"><input class="rfr_input_fld" id="com-address1" data-non-empty="true"
								name="address1" value="${address1}" placeholder='<spring:message code="label.address1.key"/>'>
							</div>
						</div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-address2"></div>
							<div class="rfr_txt_fld">
								<input class="rfr_input_fld" id="com-address2" name="address2"
									value="${address2}"
									placeholder='<spring:message code="label.address2.key"/>'>
							</div>
						</div>
					</div>
					<div class="reg_form_row clearfix">
						<div class="float-left rfr_lbl" style="visibility:hidden;"><spring:message code="label.address.key" /></div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-country"></div>
							<div class="rfr_txt_fld">
							<input class="rfr_input_fld" id="com-country" data-non-empty="true"
								name="country" value="${country}" placeholder='<spring:message code="label.country.key"/>'>
							</div>
						</div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-zip"></div>
							<div class="rfr_txt_fld">
							<input class="rfr_input_fld" id="com-zipcode" data-non-empty="true" data-zipcode="true"
								name="zipcode" value="${zipCode}" placeholder='<spring:message code="label.zipcode.key"/>'>
							</div>
						</div>
					</div>
					<div id="state-city-row" class="reg_form_row clearfix hide">
						<div class="float-left rfr_lbl" style="visibility:hidden;"><spring:message code="label.address.key" /></div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-state"></div>
							<select class="rfr_txt_fld" id="com-state" data-non-empty="true"
								name="state" data-value="${state}">
								<option disabled selected><spring:message code="label.select.state.key"/></option>
							</select>
						</div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-city"></div>
							<div class="rfr_txt_fld">
							<input class="rfr_input_fld" id="com-city" data-non-empty="true"
								name="city" value="${city}" placeholder='<spring:message code="label.city.key"/>'>
								</div>
						</div>
					</div>
					<div class="reg_form_row clearfix">
						<div class="float-left rfr_lbl"><spring:message code="label.phoneno.key" /></div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn icn-mbl"></div>
							<div class="rfr_txt_fld">
								<input type="tel" class="rfr_input_fld" id="com-contactno"
									data-non-empty="true" data-phone="true" name="contactno"
									value="${companyContactNo}"
									placeholder="<spring:message code="label.phoneno.key" />">
								<input type="hidden" id="com-phone-format" name="phoneFormat" value="${phoneFormat}">
							</div>
						</div>
					</div>
					<div class="reg_form_row clearfix">
						<div class="float-left rfr_lbl"><spring:message code="label.businessvertical.key" /></div>
						<div class="float-left rfr_txt">
							<div class="rfr_icn"></div>
							<c:if test="${not empty verticals}">
								<select name="vertical" id="select-vertical" class="rfr_txt_fld" data-value="${vertical}">
									<option disabled selected><spring:message code="label.vertical.key"/></option>
									<c:forEach items="${verticals}" var="vertical">
										<c:if test="${vertical.verticalsMasterId > 0}">
											<option id="vertical-${vertical.verticalsMasterId}">${vertical.verticalName}</option>
										</c:if>								
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
				<input type="hidden" value="${countryCode}" name="countrycode" id="country-code">
				<input type="hidden" value="${message}" data-status="${status}" name="message" id="message"/>
				<input type="hidden" value="${isDirectRegistration}" name="isDirectRegistration" id="isDirectRegistration"/>
				<input type="hidden" value="${uniqueIdentifier}" name="uniqueIdentifier" id="uniqueIdentifier"/>
			</form>
			
		</div>
	</div>

<script src="//code.jquery.com/jquery-2.1.1.min.js"></script>
<script src="//code.jquery.com/ui/1.11.2/jquery-ui.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/perfect-scrollbar.jquery.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src="${initParam.resourcesPath}/resources/js/countrydata.js"></script>
<script src="${initParam.resourcesPath}/resources/js/zipcoderegex.js"></script>
<script src="${initParam.resourcesPath}/resources/js/phoneFormat.js"></script>
<script src="${initParam.resourcesPath}/resources/js/jquery.mask.js"></script>
<script>
$(document).ready(function() {
	initializeCompanyInformationPage();
});
</script>

</body>
</html>