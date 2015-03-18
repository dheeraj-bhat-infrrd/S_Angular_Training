<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!-- Setting common page variables -->
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.logo}" var="profilelogo"></c:set>
	<c:set value="${profile.profileImageUrl}" var="profileimage"></c:set>
	<c:set value="${profile.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profile.lockSettings}" var="lock"></c:set>
	<c:set value="${profile.socialMediaTokens}" var="socialMediaTokens"></c:set>
</c:if>
<c:if test="${not empty contactdetail}">
	<c:set value="${contactdetail.mail_ids}" var="mailIds"></c:set>
	<c:set value="${contactdetail.contact_numbers}" var="contactNumbers"></c:set>
	<c:set value="${contactdetail.web_addresses}" var="webAddresses"></c:set>
</c:if>
<c:if test="${not empty socialMediaTokens}">
	<c:set value="${socialMediaTokens.facebookToken}" var="facebookToken"></c:set>
	<c:set value="${socialMediaTokens.twitterToken}" var="twitterToken"></c:set>
	<c:set value="${socialMediaTokens.linkedInToken}" var="linkedInToken"></c:set>
	<c:set value="${socialMediaTokens.yelpToken}" var="yelpToken"></c:set>
</c:if>

<!-- Setting agent page variables -->
<c:if test="${not empty profile && not empty profile.associations}">
	<c:set value="${profile.associations}" var="associations"></c:set>
</c:if>
<c:if test="${not empty profile && not empty profile.achievements}">
	<c:set value="${profile.achievements}" var="achievements"></c:set>
</c:if>
<c:if test="${not empty profile && not empty profile.licenses}">
	<c:set value="${profile.licenses.authorized_in}" var="authorisedInList"></c:set>
</c:if>

<div id="prof-message-header" class="hide"></div>
<div class="hm-header-main-wrapper">
	<div>
		<c:choose>
			<c:when test="${user.companyAdmin}">
				<input type="hidden" id="prof-company-id" value="${profile.iden}">
				<input type="hidden" id="company-profile-name" value="${profile.profileName}">
			</c:when>
			<c:when test="${user.regionAdmin}">
				<input type="hidden" id="prof-region-id" value="${profile.iden}">
				<%-- <input type="hidden" id="prof-region-name" value="${profile.profileName}"> --%>
			</c:when>
			<c:when test="${user.branchAdmin}">
				<input type="hidden" id="prof-branch-id" value="${profile.iden}">
				<%-- <input type="hidden" id="prof-branch-name" value="${profile.profileName}"> --%>
			</c:when>
			<c:when test="${user.agent}">
				<input type="hidden" id="prof-agent-id" value="${profile.iden}">
				<%-- <input type="hidden" id="prof-agent-name" value="${profile.profileName}"> --%>
			</c:when>
		</c:choose>
		<input type="hidden" id="profile-min-post-score" value="${profile.survey_settings.show_survey_above_score}"/>
	</div>
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left"><spring:message code="label.profileheader.key" /></div>
			<div id="prof-edit-social-link" class="prof-edit-social-link float-right hm-hr-row-right clearfix">
				<div class="float-left social-item-icon icn-fb" data-link="${facebookToken.facebookPageLink}"></div>
				<div class="float-left social-item-icon icn-twit" data-link="${twitterToken.twitterPageLink}"></div>
				<div class="float-left social-item-icon icn-lin" data-link="${linkedInToken.linkedInPageLink}"></div>
				<div class="float-left social-item-icon icn-yelp" data-link="${yelpToken.yelpPageLink}"></div>
				<input id="social-token-text" type="text" class="social-token-text hide" placeholder='<spring:message code="label.socialpage.placeholder.key"/>'>
			</div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<input id="prof-all-lock" type="hidden" value="locked">
	<div class="container">
		<div class="row prof-pic-name-wrapper">
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper">
				<div id="prof-img-container" class="prog-img-container prof-img-lock-wrapper">
					<c:choose>
						<c:when test="${not empty profileimage}">
							<div id="prof-image-edit" class="prof-image prof-image-edit pos-relative cursor-pointer" style="background: url(${profileimage}) no-repeat center; background-size: cover;"></div>
						</c:when>
						<c:otherwise>
							<div id="prof-image-edit" class="prof-image prof-image-edit pos-relative cursor-pointer" style="background-image:initial; background: no-repeat center; background-size: cover;"></div>
						</c:otherwise>
					</c:choose>
					<form class="form_contact_image" enctype="multipart/form-data">
						<input type='file' id="prof-image" class="con_img_inp_file" />
					</form>
					<div class="prof-rating-mobile-wrapper hide">
						<div class="st-rating-wrapper maring-0 clearfix">
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-half-star"></div>
							<div class="rating-star icn-no-star"></div>
							<div class="rating-star icn-no-star"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper">
				<div id="prof-basic-container" class="prof-name-container">
					<div class="float-left lp-edit-wrapper clearfix float-left">
						<c:choose>
							<c:when	test="${parentLock.isDisplayNameLocked && not user.agent}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}" readonly>
								<div id="prof-name-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
							</c:when>
							<c:when	test="${parentLock.isDisplayNameLocked && user.agent}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}" readonly>
								<div id="prof-name-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
							</c:when>
							<c:when	test="${not parentLock.isDisplayNameLocked && user.agent}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
								<div id="prof-name-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left"></div>
							</c:when>
							<c:when	test="${not parentLock.isDisplayNameLocked && lock.isDisplayNameLocked && not user.agent}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
								<div id="prof-name-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
							</c:when>
							<c:when	test="${not parentLock.isDisplayNameLocked && not lock.isDisplayNameLocked && not user.agent}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
								<div id="prof-name-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
							</c:when>
						</c:choose>
					</div>
					<div class="prof-address">
						<div class="prof-addline1 prof-edditable">${profile.vertical}</div>
						
						<input id="prof-title" class="prof-addline2 prof-edditable" value="${profile.contact_details.title}" placeholder='<spring:message code="label.profiletitle.placeholder.key"/>'>
						<div id="prof-title-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
					</div>
					
					<div id="prof-rating-review-count" class="prof-rating clearfix">
						<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp">
							<div class="rating-star icn-no-star"></div>
							<div class="rating-star icn-no-star"></div>
							<div class="rating-star icn-no-star"></div>
							<div class="rating-star icn-no-star"></div>
							<div class="rating-star icn-no-star"></div>
						</div>
						<div class="float-left review-count-left" id="prof-company-review-count"></div>
					</div>
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper">
				<div id="prof-logo-container" class="lp-prog-img-container" style="position: relative;">
					<c:choose>
						<c:when test="${not empty profilelogo}">
							<div id="prof-logo-edit" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background: url(${profilelogo}) center; 50% 50% no-repeat; background-size: 100% auto;"></div>
							<c:choose>
								<c:when	test="${parentLock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="locked" data-control="parent" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
									</form>
								</c:when>
								<c:when	test="${parentLock.isLogoLocked && user.agent}">
									<div id="prof-logo-lock" data-state="locked" data-control="parent" class="hide prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && user.agent}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="hide prof-img-lock-item prof-img-lock"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && lock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && not lock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
							</c:choose>
						</c:when>
						<c:otherwise>
							<div id="prof-logo" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background-image:initial; 50% 50% no-repeat; background: no-repeat center; background-size: cover;"></div>
							<form class="form_contact_image" enctype="multipart/form-data">
								<input type="file" id="prof-logo" class="con_img_inp_file">
							</form>
						</c:otherwise>
					</c:choose>
				</div>
				
				<div id="prof-address-container" class="prof-user-address prof-edit-icn cursor-pointer">
					<div class="prof-user-addline1 prof-edditable prof-addr-center" >${contactdetail.name}</div>
					<c:if test="${not empty contactdetail.address}">
						<div class="prof-user-addline2 prof-edditable prof-addr-center" >${contactdetail.address}</div>
					</c:if>
					<c:if test="${not empty contactdetail.country && not empty contactdetail.zipcode}">
						<div class="prof-user-addline3 prof-edditable prof-addr-center" >${contactdetail.country}, ${contactdetail.zipcode}</div>
					</c:if>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="prof-left-panel-wrapper margin-top-25 col-lg-4 col-md-4 col-sm-4 col-xs-12">
				<div class="prof-left-row prof-left-info bord-bot-dc main-rt-adj">
					<div class="left-contact-wrapper">
						<div class="clearfix">
							<div class="float-left left-panel-header"><spring:message code="label.contactinformation.key" /></div>
						</div>
						<div class="left-panel-content" id="contant-info-container">
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-mail"></div>
								<div class="float-left lp-con-row-item" data-email="work">${mailIds.work}</div>
								<%-- <input id="email-id-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-email="work" data-status="${mailIds.isWorkEmailVerified}" value="${mailIds.work}">
								<input id="email-id-work-old" type="hidden" value="${mailIds.work}">
								<div id="email-id-work-lock" data-state="unlocked" data-control="user" class="hide float-left"></div> --%>
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
											<div id="web-address-work-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isWebAddressLocked && user.agent}">
											<input id="web-address-work" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
											<div id="web-address-work-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left"></div>
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
											<div id="web-address-blogs-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isBlogAddressLocked && user.agent}">
											<input id="web-address-blogs" class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="blogs" value="${webAddresses.blogs}" placeholder='<spring:message code="label.blog.placeholder.key"/>'>
											<div id="web-address-blogs-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left"></div>
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
											<div id="phone-number-work-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isWorkPhoneLocked && user.agent}">
											<input id="phone-number-work" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}" placeholder='<spring:message code="label.workphone.placeholder.key"/>'>
											<div id="phone-number-work-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left"></div>
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
											<div id="phone-number-personal-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isPersonalPhoneLocked && user.agent}">
											<input id="phone-number-personal" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${contactNumbers.personal}" placeholder='<spring:message code="label.personal.placeholder.key"/>'>
											<div id="phone-number-personal-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left"></div>
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
											<div id="phone-number-fax-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
										</c:when>
										<c:when	test="${not parentLock.isFaxPhoneLocked && user.agent}">
											<input id="phone-number-fax" class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
											<div id="phone-number-fax-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left"></div>
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
						</div>
					</div>
				</div>
				
				<div id="prof-agent-container">
				<c:choose>
					<c:when	test="${user.agent}">
						<div class="prof-left-row prof-left-assoc bord-bot-dc">
							<div class="left-assoc-wrapper">
								<div class="clearfix">
									<div class="float-left left-panel-header"><spring:message code="label.membership.key" /></div>
									<div class="float-right icn-share icn-plus-open-agent" onclick="addAnAssociation();"></div>
								</div>
								<div id="association-container" class="left-panel-content">
									<c:choose>
										<c:when test="${not empty associations}">
											<c:forEach items="${associations}" var="association">
												<input class="lp-assoc-row lp-row clearfix prof-edditable-sin-agent" value="${association.name}">
												<div class="float-right lp-ach-item-img" data-type="association"></div>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<div><spring:message code="label.membership.empty.key"></spring:message></div>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</div>
						<div class="prof-left-row prof-left-ach bord-bot-dc">
							<div class="left-ach-wrapper">
								<div class="clearfix">
									<div class="float-left left-panel-header"><spring:message code="label.achievement.key" /></div>
									<div class="float-right icn-share icn-plus-open-agent" onclick="addAnAchievement();"></div>
								</div>
								<div id="achievement-container" class="left-panel-content">
									<c:choose>
										<c:when test="${not empty achievements}">
											<c:forEach items="${achievements}" var="achievement">
												<input class="lp-ach-row lp-row clearfix prof-edditable-sin-agent" value="${achievement.achievement}">
												<div class="float-right lp-ach-item-img" data-type="achievement"></div>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<div><spring:message code="label.achievement.empty.key"></spring:message></div>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</div>
						<div class="prof-left-row prof-left-auth bord-bot-dc">
							<div class="left-auth-wrapper">
								<div class="clearfix">
									<div class="float-left left-panel-header"><spring:message code="label.licenses.key" /></div>
									<div class="float-right icn-share icn-plus-open-agent" onclick="addAuthorisedIn();"></div>
								</div>
								<div id="authorised-in-container" class="left-panel-content">
									<c:choose>
										<c:when test="${not empty authorisedInList}">
											<c:forEach items="${authorisedInList}" var="authorisedIn">
												<input class="lp-auth-row lp-row clearfix prof-edditable-sin-agent" value="${authorisedIn}">
												<div class="float-right lp-ach-item-img" data-type="license"></div>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<div><spring:message code="label.licenses.empty.key"></spring:message></div>
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="prof-left-panel-wrapper margin-top-25 col-lg-4 col-md-4 col-sm-4 col-xs-12"></div>
					</c:otherwise>
				</c:choose>
				</div>
				<div class="bd-hr-left-panel">
        			<div id ="prof-hierarchy-container" class="hide">
        				<!-- hierarchy structure comes here  -->
        			</div>
   				</div>
			</div>
			
			<div class="row prof-right-panel-wrapper margin-top-25 col-lg-8 col-md-8 col-sm-8 col-xs-12">
				
				<div id="intro-about-me" class="intro-wrapper rt-content-main bord-bot-dc main-rt-adj">
					<div class="main-con-header main-con-header-adj clearfix">
						<div class="float-left">
							<spring:message code="label.about.key" /> ${contactdetail.name}
						</div>
						<div class="float-left">
							<c:choose>
								<c:when	test="${parentLock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${parentLock.isAboutMeLocked && user.agent}">
									<div id="aboutme-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && user.agent}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && lock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && not lock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<c:choose>
						<c:when	test="${not empty contactdetail.about_me && not empty fn:trim(contactdetail.about_me)}">
							<div class="pe-whitespace intro-body" id="intro-body-text">${fn:trim(contactdetail.about_me)}</div>
							<textarea class="pe-whitespace sb-txtarea hide" id="intro-body-text-edit">${fn:trim(contactdetail.about_me)}</textarea>
						</c:when>
						<c:otherwise>
							<div class="intro-body" id="intro-body-text">
								<spring:message code="label.aboutcompany.empty.key" />
							</div>
							<input type="hidden" id="aboutme-status" value="new"/>
							<textarea class="pe-whitespace sb-txtarea hide" id="intro-body-text-edit"></textarea>
						</c:otherwise>
					</c:choose>
				</div>
				
                <div class="rt-content-main bord-bot-dc clearfix">
                	<div class="float-left panel-tweet-wrapper">
                        <div class="main-con-header"><spring:message code="label.sspost.key"/></div>
                        <textarea class="pe-whitespace sb-txtarea" id="intro-body-text-edit"></textarea>
						<div class="pe-btn-post"><spring:message code="label.socialpost.key"/></div>
                    </div>
                    <div class="float-left panel-tweet-wrapper">
                        <div class="main-con-header"><spring:message code="label.latestposts.key"/></div>
                        <div class="tweet-panel tweet-panel-left">
                            <div class="tweet-panel-item bord-bot-dc clearfix">
                                <div class="tweet-icn icn-tweet float-left"></div>
                                <div class="tweet-txt float-left">
                                    <div class="tweet-text-main">Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit </div>
                                    <div class="tweet-text-link"><em>http://abblk.com</em></div>
                                    <div class="tweet-text-time"><em>24 minutes ago</em></div>
                                </div>
                            </div>
                            <div class="tweet-panel-item bord-bot-dc clearfix">
                                <div class="tweet-icn icn-tweet float-left"></div>
                                <div class="tweet-txt float-left">
                                    <div class="tweet-text-main">Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit </div>
                                    <div class="tweet-text-link"><em>http://abblk.com</em></div>
                                    <div class="tweet-text-time"><em>24 minutes ago</em></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

				<div id="reviews-container" class="people-say-wrapper rt-content-main">
					<div class="main-con-header">
						<span class="ppl-say-txt-st"><spring:message code="label.peoplesayabout.key"/></span>${contactdetail.name}
					</div>
                    <div id="prof-review-item" class="prof-reviews">
	                	<!--  reviews get populated here --> 
                    </div>
               	</div>
			</div>
		</div>
	</div>
</div>

<div class="mobile-tabs hide clearfix">
	<div class="float-left mob-icn mob-icn-active icn-person"></div>
	<div class="float-left mob-icn icn-ppl"></div>
	<div class="float-left mob-icn icn-star-smile"></div>
	<div class="float-left mob-icn inc-more"></div>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/jcrop/jquery.Jcrop.min.css">

<script src="${pageContext.request.contextPath}/resources/jcrop/jquery.Jcrop.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/jcrop/jcrop.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/editprofile.js"></script>
<script>
	$(document).ready(function() {
		$(document).attr("title", "Profile Settings");
		adjustImage();
		$(window).resize(adjustImage);
		
		if ($('#aboutme-status').val() != 'new') {
			$('#intro-body-text').text($('#intro-body-text-edit').val().trim());
		}
		paintForProfile();
		
		$('.ppl-share-wrapper .icn-plus-open').click(function() {
			$(this).hide();
			$(this).parent().find('.ppl-share-social,.icn-remove').show();
		});

		$('.ppl-share-wrapper .icn-remove').click(function() {
			$(this).hide();
			$(this).parent().find('.ppl-share-social').hide();
			$(this).parent().find('.icn-plus-open').show();
		});

		$('.icn-person').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
			$('.prof-left-panel-wrapper').show();
			$('.prof-right-panel-wrapper').hide();
			adjustImage();
		});

		$('.icn-ppl').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
			$('.prof-left-panel-wrapper').hide();
			$('.prof-right-panel-wrapper').show();
		});

		$('.icn-star-smile').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
		});

		$('.inc-more').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
		});
	});
</script>