<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>
<c:if test="${not empty contactdetail}">
	<c:set value="${contactdetail.mail_ids}" var="mailIds"></c:set>
	<c:set value="${contactdetail.contact_numbers}" var="contactNumbers"></c:set>
	<c:set value="${contactdetail.web_addresses}" var="webAddresses"></c:set>
</c:if>
<c:if test="${not empty contactNumbers && not empty contactNumbers.phone1}">
	<c:set value="${contactNumbers.phone1.countryAbbr}" var="countryAbbr"></c:set>
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
<input type="hidden" id="sel-prof-country-code" value="${countryAbbr}">
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-mail"></div>
	<%-- <div class="float-left lp-con-row-item" data-email="work">${mailIds.work}</div> --%>
	<%-- <input id="email-id-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-email="work" data-status="${mailIds.isWorkEmailVerified}" value="${mailIds.work}">
	<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
	<div id="email-id-work-lock" data-state="unlocked" data-control="user" class="hide float-left"></div> --%>
	<c:choose>
		<c:when test="${not empty mailIds && not empty mailIds.work }">
			<input id="email-id-work" class="float-left lp-con-row-item blue-text <c:if test="${not mailIds.isWorkEmailVerified}">not-verified</c:if> prof-edditable-sin" data-email="work" data-status="${mailIds.isWorkEmailVerified}" value="${mailIds.work}" <c:if test="${not mailIds.isWorkEmailVerified}">title="${workMailVerificationTitle}"
							</c:if>>
			<c:choose>
				<c:when test="${not parentLock.isWorkEmailLocked && profilemasterid != 1}">
					<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
					<div id="email-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
				</c:when>
				<c:when test="${parentLock.isWorkEmailLocked && profilemasterid != 1}">
					<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
					<div id="email-work-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
				</c:when>
				<c:when test="${parentLock.isWorkEmailLocked && profilemasterid == 1}">
					<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
					<div id="email-work-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
				</c:when>
				<c:when test="${not parentLock.isWorkEmailLocked && lock.isWorkEmailLocked && profilemasterid == 1}">
					<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
					<div id="email-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
				</c:when>
				<c:when test="${not parentLock.isWorkEmailLocked && not lock.isWorkEmailLocked && profilemasterid == 1 && isWorkEmailSetByEntity}">
					<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
					<div id="email-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
				</c:when>
				<c:when test="${not parentLock.isWorkEmailLocked && not lock.isWorkEmailLocked && profilemasterid == 1 && not isWorkEmailSetByEntity}">
					<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
					<div id="email-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
				</c:when>
			</c:choose>
		</c:when>
		<c:otherwise>
			<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
			<div id="email-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
		</c:otherwise>
	</c:choose>
</div>
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-web"></div>
	<div>
		<c:choose>
			<c:when test="${not empty webAddresses && not empty webAddresses.work }">
				<c:choose>
					<c:when test="${parentLock.isWebAddressLocked && profilemasterid != 4}">
						<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>' readonly>
						<div id="web-address-work-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
					</c:when>
					<c:when test="${parentLock.isWebAddressLocked && profilemasterid == 4}">
						<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>' readonly>
						<div id="web-address-work-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
					</c:when>
					<c:when test="${not parentLock.isWebAddressLocked && profilemasterid == 4}">
						<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
						<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
					</c:when>
					<c:when test="${not parentLock.isWebAddressLocked && lock.isWebAddressLocked && profilemasterid != 4}">
						<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
						<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
					</c:when>
					<c:when test="${not parentLock.isWebAddressLocked && not lock.isWebAddressLocked && profilemasterid != 4 && isWebAddressSetByEntity}">
						<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
						<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
					</c:when>
					<c:when test="${not parentLock.isWebAddressLocked && not lock.isWebAddressLocked && profilemasterid != 4 && not isWebAddressSetByEntity}">
						<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
						<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
					</c:when>
				</c:choose>
			</c:when>
			<c:otherwise>
				<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
				<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<%-- <div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-blog"></div>
	<div>
		<c:choose>
			<c:when	test="${parentLock.isBlogAddressLocked && profilemasterid != 4}">
				<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>' readonly>
				<div id="web-address-blogs-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${parentLock.isBlogAddressLocked && profilemasterid == 4}">
				<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>' readonly>
				<div id="web-address-blogs-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${not parentLock.isBlogAddressLocked && profilemasterid == 4}">
				<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>'>
				<div id="web-address-blogs-lock" data-state="unlocked" data-control="user" class="float-left"></div>
			</c:when>
			<c:when	test="${not parentLock.isBlogAddressLocked && lock.isBlogAddressLocked && profilemasterid != 4}">
				<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>'>
				<div id="web-address-blogs-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${not parentLock.isBlogAddressLocked && not lock.isBlogAddressLocked && profilemasterid != 4}">
				<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>'>
				<div id="web-address-blogs-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
			</c:when>
		</c:choose>
	</div>
</div>
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-mbl"></div>
	<div class="float-left lp-edit-wrapper clearfix float-left">
		<c:choose>
			<c:when	test="${parentLock.isPersonalPhoneLocked && profilemasterid != 4}">
				<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>' readonly>
				<div id="phone-number-personal-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${parentLock.isPersonalPhoneLocked && profilemasterid == 4}">
				<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>' readonly>
				<div id="phone-number-personal-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${not parentLock.isPersonalPhoneLocked && profilemasterid == 4}">
				<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>'>
				<div id="phone-number-personal-lock" data-state="unlocked" data-control="user" class="float-left"></div>
			</c:when>
			<c:when	test="${not parentLock.isPersonalPhoneLocked && lock.isPersonalPhoneLocked && profilemasterid != 4}">
				<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>'>
				<div id="phone-number-personal-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${not parentLock.isPersonalPhoneLocked && not lock.isPersonalPhoneLocked && profilemasterid != 4}">
				<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>'>
				<div id="phone-number-personal-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
			</c:when>
		</c:choose>
	</div>
</div>--%>
<div class="lp-con-row lp-row clearfix contact-phone">
	<div class="float-left lp-con-icn icn-phone"></div>
	<div class="float-left lp-edit-wrapper clearfix float-left contact-phone-edit">
		<c:choose>
			<c:when test="${not empty contactNumbers && not empty contactNumbers.work }">
				<c:choose>
					<c:when test="${parentLock.isWorkPhoneLocked && profilemasterid != 4}">
						<input id="phone-number-work" type="tel" class="reg-details float-left  prof-edditable-sin" data-phone-number="work">
						<div id="phone-number-work-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
					</c:when>
					<c:when test="${parentLock.isWorkPhoneLocked && profilemasterid == 4}">
						<input id="phone-number-work" type="tel" class="reg-details float-left  prof-edditable-sin" data-phone-number="work">
						<div id="phone-number-work-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
					</c:when>
					<c:when test="${not parentLock.isWorkPhoneLocked && profilemasterid == 4}">
						<input id="phone-number-work" type="tel" class="reg-details float-left  prof-edditable-sin" data-phone-number="work" title="${contactNumbers.work}">
						<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
						<!-- <input class="reg-details" type="tel" id="reg-phone-edit"  /> -->
					</c:when>
					<c:when test="${not parentLock.isWorkPhoneLocked && lock.isWorkPhoneLocked && profilemasterid != 4}">
						<input id="phone-number-work" type="tel" class="reg-details float-left  prof-edditable-sin" data-phone-number="work">
						<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
					</c:when>
					<c:when test="${not parentLock.isWorkPhoneLocked && not lock.isWorkPhoneLocked && profilemasterid != 4 && isContactNoSetByEntity}">
						<input id="phone-number-work" type="tel" class="reg-details float-left  prof-edditable-sin" data-phone-number="work">
						<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
					</c:when>
					<c:when test="${not parentLock.isWorkPhoneLocked && not lock.isWorkPhoneLocked && profilemasterid != 4 && not isContactNoSetByEntity}">
						<input id="phone-number-work" type="tel" class="reg-details float-left  prof-edditable-sin" data-phone-number="work">
						<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
					</c:when>
				</c:choose>
			</c:when>
			<c:otherwise>
				<input id="phone-number-work" class="reg-details float-left prof-edditable-sin" type="tel" data-phone-number="work">
				<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<%--
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-fax"></div>
	<div>
		<c:choose>
			<c:when	test="${parentLock.isFaxPhoneLocked && profilemasterid != 4}">
				<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>' readonly>
				<div id="phone-number-fax-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${parentLock.isFaxPhoneLocked && profilemasterid == 4}">
				<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>' readonly>
				<div id="phone-number-fax-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${not parentLock.isFaxPhoneLocked && profilemasterid == 4}">
				<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
				<div id="phone-number-fax-lock" data-state="unlocked" data-control="user" class="float-left"></div>
			</c:when>
			<c:when	test="${not parentLock.isFaxPhoneLocked && lock.isFaxPhoneLocked && profilemasterid != 4}">
				<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
				<div id="phone-number-fax-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
			</c:when>
			<c:when	test="${not parentLock.isFaxPhoneLocked && not lock.isFaxPhoneLocked && profilemasterid != 4}">
				<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
				<div id="phone-number-fax-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
			</c:when>
		</c:choose>
	</div>
</div> --%>
<script>
	$(document).ready(function() {
		$('#phone-number-work').intlTelInput({
			utilsScript : "../resources/js/utils.js"
		}).done(initializeWorkPhoneNumber);

		$('.dial-country-code').css('font-size', '14px');
		$('.dial-country-code').css('line-height', '26px');
		$('.reg-details').css('height', '28px');
		$('.intl-tel-input').css('float', 'left');

		var countryData = $('#phone-number-work').intlTelInput("getSelectedCountryData");
		$("#phone-number-work").on("countrychange", function(e, countryData) {
			maskPhoneNumber("phone-number-work", countryData.iso2);
		});

		initializeWorkPhoneNumber();

		if ($('#phone-number-work').val() == '(') {
			$('#phone-number-work').val('');
		}
		
		maskPhoneNumber("phone-number-work", countryData.iso2);
		
		var isWorkPhoneLocked = "${parentLock.isWorkPhoneLocked}";
		if(isWorkPhoneLocked == 'true'){
			$('#phone-number-work').prop('readonly',true);
		}
	});

	function initializeWorkPhoneNumber() {
		var countryCode = $('#sel-prof-country-code').val();
		if (countryCode == undefined || countryCode == "") {
			countryCode = "us";
		}
		
		var contactNumber = '${contactNumbers.work}';
		contactNumber = contactNumber.replace(/\(|\)|\-|x| /g,'')
		$('#phone-number-work').intlTelInput("setCountry", countryCode);
		$('#phone-number-work').intlTelInput("setNumber",contactNumber );
	}
</script>