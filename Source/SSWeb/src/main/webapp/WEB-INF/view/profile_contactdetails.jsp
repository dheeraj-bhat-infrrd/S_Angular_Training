<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profile.lockSettings}" var="lock"></c:set>
</c:if>
<c:if test="${not empty contactdetail}">
	<c:set value="${contactdetail.mail_ids}" var="mailIds"></c:set>
	<c:set value="${contactdetail.contact_numbers}" var="contactNumbers"></c:set>
	<c:set value="${contactdetail.web_addresses}" var="webAddresses"></c:set>
</c:if>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-mail"></div>
								<%-- <div class="float-left lp-con-row-item" data-email="work">${mailIds.work}</div> --%>
								<input id="email-id-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-email="work" data-status="${mailIds.isWorkEmailVerified}" value="${mailIds.work}">
								<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
								<div id="email-id-work-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-web"></div>
								<div>
									<c:choose>
										<c:when	test="${parentLock.isWebAddressLocked && not user.agent}">
											<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>' readonly>
											<div id="web-address-work-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${parentLock.isWebAddressLocked && user.agent}">
											<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>' readonly>
											<div id="web-address-work-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isWebAddressLocked && user.agent}">
											<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
											<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
										</c:when>
										<c:when	test="${not parentLock.isWebAddressLocked && lock.isWebAddressLocked && not user.agent}">
											<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
											<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isWebAddressLocked && not lock.isWebAddressLocked && not user.agent}">
											<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
											<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
	  							</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-web"></div>
								<div>
									<c:choose>
										<c:when	test="${parentLock.isBlogAddressLocked && not user.agent}">
											<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>' readonly>
											<div id="web-address-blogs-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${parentLock.isBlogAddressLocked && user.agent}">
											<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>' readonly>
											<div id="web-address-blogs-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isBlogAddressLocked && user.agent}">
											<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>'>
											<div id="web-address-blogs-lock" data-state="unlocked" data-control="user" class="float-left"></div>
										</c:when>
										<c:when	test="${not parentLock.isBlogAddressLocked && lock.isBlogAddressLocked && not user.agent}">
											<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>'>
											<div id="web-address-blogs-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isBlogAddressLocked && not lock.isBlogAddressLocked && not user.agent}">
											<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>'>
											<div id="web-address-blogs-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
	  							</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-phone"></div>
								<div class="float-left lp-edit-wrapper clearfix float-left">
									<c:choose>
										<c:when	test="${parentLock.isWorkPhoneLocked && not user.agent}">
											<input id="phone-number-work" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}" placeholder='<spring:message code="label.workphone.placeholder.key"/>' readonly>
											<div id="phone-number-work-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${parentLock.isWorkPhoneLocked && user.agent}">
											<input id="phone-number-work" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}" placeholder='<spring:message code="label.workphone.placeholder.key"/>' readonly>
											<div id="phone-number-work-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isWorkPhoneLocked && user.agent}">
											<input id="phone-number-work" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}" placeholder='<spring:message code="label.workphone.placeholder.key"/>'>
											<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="float-left"></div>
										</c:when>
										<c:when	test="${not parentLock.isWorkPhoneLocked && lock.isWorkPhoneLocked && not user.agent}">
											<input id="phone-number-work" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}" placeholder='<spring:message code="label.workphone.placeholder.key"/>'>
											<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isWorkPhoneLocked && not lock.isWorkPhoneLocked && not user.agent}">
											<input id="phone-number-work" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}" placeholder='<spring:message code="label.workphone.placeholder.key"/>'>
											<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
								</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-mbl"></div>
								<div class="float-left lp-edit-wrapper clearfix float-left">
									<c:choose>
										<c:when	test="${parentLock.isPersonalPhoneLocked && not user.agent}">
											<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>' readonly>
											<div id="phone-number-personal-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${parentLock.isPersonalPhoneLocked && user.agent}">
											<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>' readonly>
											<div id="phone-number-personal-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isPersonalPhoneLocked && user.agent}">
											<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>'>
											<div id="phone-number-personal-lock" data-state="unlocked" data-control="user" class="float-left"></div>
										</c:when>
										<c:when	test="${not parentLock.isPersonalPhoneLocked && lock.isPersonalPhoneLocked && not user.agent}">
											<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>'>
											<div id="phone-number-personal-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isPersonalPhoneLocked && not lock.isPersonalPhoneLocked && not user.agent}">
											<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>'>
											<div id="phone-number-personal-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
								</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-fax"></div>
								<div>
									<c:choose>
										<c:when	test="${parentLock.isFaxPhoneLocked && not user.agent}">
											<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>' readonly>
											<div id="phone-number-fax-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${parentLock.isFaxPhoneLocked && user.agent}">
											<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>' readonly>
											<div id="phone-number-fax-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isFaxPhoneLocked && user.agent}">
											<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
											<div id="phone-number-fax-lock" data-state="unlocked" data-control="user" class="float-left"></div>
										</c:when>
										<c:when	test="${not parentLock.isFaxPhoneLocked && lock.isFaxPhoneLocked && not user.agent}">
											<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
											<div id="phone-number-fax-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isFaxPhoneLocked && not lock.isFaxPhoneLocked && not user.agent}">
											<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
											<div id="phone-number-fax-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
										</c:when>
									</c:choose>
								</div>
							</div>