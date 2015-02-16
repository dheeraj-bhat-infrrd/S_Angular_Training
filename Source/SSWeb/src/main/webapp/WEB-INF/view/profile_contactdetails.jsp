<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.contact_details.mail_ids}" var="mailIds"></c:set>
	<c:set value="${profile.contact_details.contact_numbers}" var="contactNumbers"></c:set>
	<c:set value="${profile.contact_details.web_addresses}" var="webAddresses"></c:set>
</c:if>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-mail"></div>
								<div class="float-left lp-con-row-item" data-email="work">${mailIds.work}</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-web"></div>
								<div>
									<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
									<c:choose>
										<c:when	test="${lock.isWebAddressLocked && not user.agent}">
											<div id="web-address-work-lock" data-state="locked" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not lock.isWebAddressLocked && not user.agent}">
											<div id="web-address-work-lock" data-state="unlocked" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
	  							</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-phone"></div>
								<div class="float-left lp-edit-wrapper clearfix float-left">
									<input id="phone-number-work" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}">
									<c:choose>
										<c:when	test="${lock.isWorkPhoneLocked && not user.agent}">
											<div id="phone-number-work-lock" data-state="locked" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not lock.isWorkPhoneLocked && not user.agent}">
											<div id="phone-number-work-lock" data-state="unlocked" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
								</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-mbl"></div>
								<div class="float-left lp-edit-wrapper clearfix float-left">
									<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}">
									<c:choose>
										<c:when	test="${lock.isPersonalPhoneLocked && not user.agent}">
											<div id="phone-number-personal-lock" data-state="locked" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not lock.isPersonalPhoneLocked && not user.agent}">
											<div id="phone-number-personal-lock" data-state="unlocked" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
								</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-fax"></div>
								<div>
									<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
									<c:choose>
										<c:when	test="${lock.isFaxPhoneLocked && not user.agent}">
											<div id="phone-number-fax-lock" data-state="locked" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not lock.isFaxPhoneLocked && not user.agent}">
											<div id="phone-number-fax-lock" data-state="unlocked" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
								</div>
							</div>