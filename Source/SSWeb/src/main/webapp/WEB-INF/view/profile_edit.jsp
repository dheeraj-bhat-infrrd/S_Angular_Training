<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!-- Setting common page variables -->
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profile}">
	<c:set value="${profile.profilesMaster.profileId}" var="profilemasterid"></c:set>
</c:if>
<c:if test="${not empty profile && not empty profileSettings.completeProfileUrl}">
	<c:set value="${profileSettings.completeProfileUrl}" var="completeProfileUrl"></c:set>
</c:if>
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
	<c:set value="${profileSettings.logo}" var="profilelogo"></c:set>
	<c:set value="${profileSettings.profileImageUrl}" var="profileimage"></c:set>
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.socialMediaTokens}" var="socialMediaTokens"></c:set>
	<c:set value="${profileSettings.disclaimer}" var="disclaimer"></c:set>
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
	<c:set value="${socialMediaTokens.googleToken}" var="googleToken"></c:set>
</c:if>

<!-- Setting agent page variables -->
<c:if test="${profilemasterid == 4}">
	<c:if test="${not empty profile && not empty profileSettings.licenses}">
		<c:set value="${profileSettings.licenses.authorized_in}" var="authorisedInList"></c:set>
	</c:if>
	<c:if test="${not empty profile && not empty profileSettings.positions}">
		<c:set value="${profileSettings.positions}" var="positions"></c:set>
	</c:if>
	<c:if test="${not empty profile && not empty profileSettings.associations}">
		<c:set value="${profileSettings.associations}" var="associations"></c:set>
	</c:if>
	<c:if test="${not empty profile && not empty profileSettings.expertise}">
		<c:set value="${profileSettings.expertise}" var="expertiseList"></c:set>
	</c:if>
	<c:if test="${not empty profile && not empty profileSettings.achievements}">
		<c:set value="${profileSettings.achievements}" var="achievements"></c:set>
	</c:if>
	<c:if test="${not empty profile && not empty profileSettings.hobbies}">
		<c:set value="${profileSettings.hobbies}" var="hobbies"></c:set>
	</c:if>
</c:if>

<div id="prof-message-header" class="hide"></div>
<div class="hm-header-main-wrapper">
	<div>
		<c:choose>
			<c:when test="${profilemasterid == 1}">
				<input type="hidden" id="prof-company-id" value="${profileSettings.iden}">
				<input type="hidden" id="company-profile-name" value="${profileSettings.profileName}">
			</c:when>
			<c:when test="${profilemasterid == 2}">
				<input type="hidden" id="prof-region-id" value="${profile.regionId}">
			</c:when>
			<c:when test="${profilemasterid == 3}">
				<input type="hidden" id="prof-branch-id" value="${profile.branchId}">
			</c:when>
			<c:when test="${profilemasterid == 4}">
				<input type="hidden" id="prof-agent-id" value="${profile.agentId}">
			</c:when>
		</c:choose>
		<input type="hidden" id="profile-id" value="${profile.userProfileId}"/>
		<input type="hidden" id="profile-min-post-score" value="${profileSettings.survey_settings.show_survey_above_score}"/>
	</div>
	
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left"><spring:message code="label.profileheader.key" /></div>
			<c:if test="${not empty profileList && fn:length(profileList) > 1}">
				<div class="float-right header-right clearfix hr-dsh-adj-rt" style="z-index: 99; margin-left: 50px;">
					<div class="float-left hr-txt1"><spring:message code="label.viewas.key" /></div>
					<div id="profile-sel" class="float-left hr-txt2 cursor-pointer">${profileName}</div>
					<div id="pe-dd-wrapper-profiles" class="va-dd-wrapper hide">
						<c:forEach var="userprofile" items="${profileList}">
							<div class="pe-dd-item" data-profile-id="${userprofile.key}">${userprofile.value.userProfileName}</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<input id="prof-all-lock" type="hidden" value="locked">
	<div>
        <div class="container">
		<div class="row prof-pic-name-wrapper edit-prof-pic-name-wrapper">
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper">
				<div id="prof-img-container" class="prog-img-container prof-img-lock-wrapper">
					<c:choose>
						<c:when test="${not empty profileimage}">
							<div id="prof-image-edit" class="prof-image prof-image-edit pos-relative cursor-pointer" style="background: url(${profileimage}) no-repeat center; background-size: contain"></div>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${profilemasterid == 1}">
									<c:set value="comp-default-img" var="defaultprofimageclass"></c:set>
								</c:when>
								<c:when test="${profilemasterid == 2}">
									<c:set value="region-default-img" var="defaultprofimageclass"></c:set>
								</c:when>
								<c:when test="${profilemasterid == 3}">
									<c:set value="office-default-img" var="defaultprofimageclass"></c:set>
								</c:when>
								<c:when test="${profilemasterid == 4}">
									<c:set value="pers-default-big" var="defaultprofimageclass"></c:set>
								</c:when>
							</c:choose>	
							<div id="prof-image-edit" class="prof-image prof-image-edit ${defaultprofimageclass} pos-relative cursor-pointer"></div>						
						</c:otherwise>
					</c:choose>
					<form class="form_contact_image" enctype="multipart/form-data">
						<input type='file' id="prof-image" class="con_img_inp_file" />
					</form>
					<div class="prof-rating-mobile-wrapper hide">
						<div class="st-rating-wrapper maring-0 clearfix"></div>
					</div>
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper edit-prof-name-wrapper">
				<div id="prof-basic-container" class="prof-name-container">
					<div id="prof-name-container" class="float-left lp-edit-wrapper clearfix float-left">
						<c:choose>
							<c:when	test="${parentLock.isDisplayNameLocked && profilemasterid != 4}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}" readonly>
								<div id="prof-name-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
							</c:when>
							<c:when	test="${parentLock.isDisplayNameLocked && profilemasterid == 4}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}" readonly>
								<div id="prof-name-lock" data-state="locked" data-control="parent" class="float-left lp-edit-locks-locked"></div>
							</c:when>
							<c:when	test="${not parentLock.isDisplayNameLocked && profilemasterid == 4}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
								<div id="prof-name-lock" data-state="unlocked" data-control="user" class="float-left"></div>
							</c:when>
							<c:when	test="${not parentLock.isDisplayNameLocked && lock.isDisplayNameLocked && profilemasterid != 4}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
								<div id="prof-name-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
							</c:when>
							<c:when	test="${not parentLock.isDisplayNameLocked && not lock.isDisplayNameLocked && profilemasterid != 4}">
								<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
								<div id="prof-name-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
							</c:when>
						</c:choose>
					</div>
					<div class="prof-address">
						<c:if test="${profilemasterid != 1}">
							<input id="prof-title" class="prof-addline2 prof-edditable" value="${contactdetail.title}" placeholder='<spring:message code="label.profiletitle.placeholder.key"/>'>
							<div id="prof-title-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
						</c:if>
						<input id="prof-vertical" class="prof-addline2 prof-edditable" value="${profileSettings.vertical}" placeholder='<spring:message code="label.profilevertical.placeholder.key"/>'>
						<div id="prof-vertical-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
					</div>
					
					<div id="prof-rating-review-count" class="prof-rating clearfix">
						<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
						<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
					</div>
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper float-right">
				<div id="prof-logo-container" class="lp-prog-img-container" style="position: relative;">
					<c:choose>
						<c:when test="${not empty profilelogo}">
							<div id="prof-logo-edit" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background: url(${profilelogo}) no-repeat center; 50% 50% no-repeat; background-size: contain;"></div>
							<c:choose>
								<c:when test="${accountMasterId == 1 || accountMasterId == 5}">
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
								<c:when	test="${parentLock.isLogoLocked && profilemasterid != 4}">
									<div id="prof-logo-lock" data-state="locked" data-control="parent" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
									</form>
								</c:when>
								<c:when	test="${parentLock.isLogoLocked && profilemasterid == 4}">
									<div id="prof-logo-lock" data-state="locked" data-control="parent" class="prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && profilemasterid == 4}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class=""></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && lock.isLogoLocked && profilemasterid != 4}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && not lock.isLogoLocked && profilemasterid != 4}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
							</c:choose>
						</c:when>
						<c:otherwise>
							<div id="prof-logo-edit" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background-image:initial; 50% 50% no-repeat; background: no-repeat center; background-size: cover;"></div>
							<form class="form_contact_image" enctype="multipart/form-data">
								<input type="file" id="prof-logo" class="con_img_inp_file">
							</form>
						</c:otherwise>
					</c:choose>
				</div>
				
				<div id="prof-address-container" class="prof-user-address prof-edit-icn cursor-pointer" onclick="showEditAddressPopup();">
					<jsp:include page="profile_addressdetails.jsp"></jsp:include>
				</div>
			</div>
		</div>
		</div>
		<div class="prof-details-header">
			<div class="container">
				<div class="prof-details-header-row clearfix">
					<div class="prof-link-header float-left clearfix">
						<div id="prof-header-rating" class="rating-image float-left smiley-rat-5"></div>
						<c:if test="${not empty completeProfileUrl}">
							<div id="prof-header-url" class="rating-image-txt float-left">
								<a href="${completeProfileUrl}" target="_blank">${completeProfileUrl}</a>
							</div>
						</c:if>
						
					</div>
					<c:if test="${not empty webAddresses.work}">
						<div id="web-addr-header" class="web-addr-header float-left clearfix">
							<div class="web-address-img float-left"></div>
							<div id="web-address-txt" class="web-address-txt float-left">${webAddresses.work}</div>
						</div>
					</c:if>

					<c:if test="${accountMasterId != 5}">
						<div id="prof-edit-social-link"
							class="prof-edit-social-link float-right hm-hr-row-right clearfix">
							<div id="icn-fb" class="float-left social-item-icon icn-fb" data-link="${facebookToken.facebookPageLink}"></div>
							<div id="icn-twit" class="float-left social-item-icon icn-twit" data-link="${twitterToken.twitterPageLink}"></div>
							<div id="icn-lin" class="float-left social-item-icon icn-lin" data-link="${linkedInToken.linkedInPageLink}"></div>
							<div id="icn-yelp" class="float-left social-item-icon icn-yelp" data-link="${yelpToken.yelpPageLink}"></div>
                            <div id="icn-gplus" class="float-left social-item-icon icn-gplus" data-link="${googleToken.profileLink}"></div>
							<input id="social-token-text" type="text" class="social-token-text hide"
								placeholder='<spring:message code="label.socialpage.placeholder.key"/>'>
						</div>
					</c:if>
				</div>
			</div>
		</div>
		
		<div class="container">
		<div class="row">
			<div class="prof-left-panel-wrapper margin-top-25 col-lg-4 col-md-4 col-sm-4 col-xs-12">
				<div id="contact-wrapper" class="prof-left-row prof-left-info bord-bot-dc main-rt-adj">
					<div class="left-contact-wrapper">
						<div class="clearfix">
							<div class="float-left left-panel-header"><spring:message code="label.contactinformation.key" /></div>
						</div>
						<div class="left-panel-content" id="contant-info-container">
							<!-- Include the profile contact details jsp -->
							<jsp:include page="profile_contactdetails.jsp"></jsp:include>
						</div>
					</div>
				</div>
				
				<div id="prof-agent-container">
					<c:choose>
						<c:when	test="${profilemasterid == 4}">
							<!-- Licences left panel -->
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
													<div class="lp-dummy-row clearfix">
														<input class="lp-auth-row lp-row clearfix prof-edditable-sin-agent" value="${authorisedIn}" data-status="saved">
														<div class="float-right lp-ach-item-img hide" data-type="license"></div>
													</div>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<span><spring:message code="label.licenses.empty.key"></spring:message></span>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</div>
							<!-- Associations left panel -->
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
													<div class="lp-dummy-row clearfix">
														<input class="lp-assoc-row lp-row clearfix prof-edditable-sin-agent" value="${association.name}" data-status="saved">
														<div class="float-right lp-ach-item-img hide" data-type="association"></div>
													</div>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<span><spring:message code="label.membership.empty.key"></spring:message></span>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</div>
							<!-- Expertise left panel -->
							<div class="prof-left-row prof-left-auth bord-bot-dc">
								<div class="left-expertise-wrapper">
									<div class="clearfix">
										<div class="float-left left-panel-header"><spring:message code="label.expertise.key" /></div>
										<div class="float-right icn-share icn-plus-open-agent" onclick="addExpertise();"></div>
									</div>
									<div id="expertise-container" class="left-panel-content">
										<c:choose>
											<c:when test="${not empty expertiseList}">
												<c:forEach items="${expertiseList}" var="expertise">
													<div class="lp-dummy-row clearfix">
														<input class="lp-expertise-row lp-row clearfix prof-edditable-sin-agent" value="${expertise}" data-status="saved">
														<div class="float-right lp-expertise-item-img hide" data-type="expertise"></div>
													</div>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<span><spring:message code="label.expertise.empty.key"></spring:message></span>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</div>
							<!-- Achievements left panel -->
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
													<div class="lp-dummy-row clearfix">
														<input class="lp-ach-row lp-row clearfix prof-edditable-sin-agent" value="${achievement.achievement}" data-status="saved">
														<div class="float-right lp-ach-item-img hide" data-type="achievement"></div>
													</div>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<span><spring:message code="label.achievement.empty.key"></spring:message></span>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</div>
							<!-- Hobbies left panel -->
							<div class="prof-left-row prof-left-auth bord-bot-dc">
								<div class="left-hobbies-wrapper">
									<div class="clearfix">
										<div class="float-left left-panel-header"><spring:message code="label.hobbies.key" /></div>
										<div class="float-right icn-share icn-plus-open-agent" onclick="addHobby();"></div>
									</div>
									<div id="hobbies-container" class="left-panel-content">
										<c:choose>
											<c:when test="${not empty hobbies}">
												<c:forEach items="${hobbies}" var="hobby">
													<div class="lp-dummy-row clearfix">
														<input class="lp-hobby-row lp-row clearfix prof-edditable-sin-agent" value="${hobby}" data-status="saved">
														<div class="float-right lp-hobby-item-img hide" data-type="hobby"></div>
													</div>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<span><spring:message code="label.hobbies.empty.key"></spring:message></span>
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
				<div id="disclaimer-wrapper" class="prof-left-row prof-left-info main-rt-adj">
					<div class="left-contact-wrapper">
						<div class="clearfix">
							<div class="float-left left-panel-header"><spring:message code="label.disclaimer.key" /></div>
						</div>
						<div class="left-panel-content" id="disclaimer-container">
							<input id="disclaimer-text" class="float-left lp-con-row-item blue-text prof-editable-disclaimer"
								value="${disclaimer}" placeholder='<spring:message code="label.disclaimer.placeholder.key"/>'>
							<input id="disclaimer-default" type="hidden" value="${disclaimer}">
						</div>
					</div>
				</div>
				
				<c:choose>
					<c:when	test="${profilemasterid != 4}">
						<div class="clearfix bd-hr-left-panel">
							<c:choose>
								<c:when	test="${profilemasterid == 1}">
									<div class="bd-hr-lp-header"><spring:message code="label.ourcompany.key"/></div>
								</c:when>
								<c:when	test="${profilemasterid == 2}">
									<div class="bd-hr-lp-header"><spring:message code="label.ourregion.key"/></div>
								</c:when>
								<c:when	test="${profilemasterid == 3}">
									<div class="bd-hr-lp-header"><spring:message code="label.ourbranch.key"/></div>
								</c:when>
							</c:choose>
							<div id="prof-hierarchy-container" class="hide">
								<!-- hierarchy structure comes here  -->
							</div>
   						</div>
					</c:when>
				</c:choose>
			</div>
			
			<div class="row prof-right-panel-wrapper margin-top-25 col-lg-8 col-md-8 col-sm-8 col-xs-12">
				<div id="intro-about-me" class="intro-wrapper rt-content-main bord-bot-dc main-rt-adj">
					<div class="main-con-header main-con-header-adj clearfix">
						<div class="float-left">
							<spring:message code="label.about.key" /> ${contactdetail.name}
						</div>
						<div class="float-left">
							<c:choose>
								<c:when	test="${parentLock.isAboutMeLocked && profilemasterid != 4}">
									<div id="aboutme-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${parentLock.isAboutMeLocked && profilemasterid == 4}">
									<div id="aboutme-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && profilemasterid == 4}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && lock.isAboutMeLocked && profilemasterid != 4}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && not lock.isAboutMeLocked && profilemasterid != 4}">
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
				
				<div id="ppl-post-cont" class="rt-content-main bord-bot-dc clearfix">
					<div class="float-left panel-tweet-wrapper">
						<textarea class="pe-whitespace sb-txtarea" id="status-body-text-edit" placeholder="<spring:message code="label.sspost.key"/>"></textarea>
						<div id="prof-post-btn" class="pe-btn-post"><spring:message code="label.socialpost.key"/></div>
					</div>
					<div class="float-left panel-tweet-wrapper">
						<div class="main-con-header"><spring:message code="label.latestposts.key"/></div>
						<div id="prof-posts" class="tweet-panel tweet-panel-left">
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
</div>

<div class="mobile-tabs hide clearfix">
	<div class="float-left mob-icn mob-icn-active icn-person"></div>
	<div class="float-left mob-icn icn-ppl"></div>
	<div class="float-left mob-icn icn-star-smile"></div>
	<div class="float-left mob-icn inc-more"></div>
</div>
<script>
	$(document).ready(function() {
		hideOverlay();
		countPosts();
		$(document).attr("title", "Profile Settings");
		adjustImage();
		$(window).resize(adjustImage);
		
		if ($('#aboutme-status').val() != 'new') {
			$('#intro-body-text').text($('#intro-body-text-edit').val().trim());
		}
		paintForProfile();
		focusOnElement();
		$('.ppl-share-wrapper .icn-plus-open').click(function() {
			$(this).hide();
			$(this).parent().find('.ppl-share-social,.icn-remove').show();
		});
		
		$('#prof-post-btn').unbind('click');
		$('#prof-post-btn').click(function() {
			var textContent = $('#status-body-text-edit').val().trim();
			$('#status-body-text-edit').val('');
			var payload = {
				"text" : textContent
			};
			$.ajax({
				url : "./savestatus.do",
				type : "POST",
				dataType : "text",
				async : false,
				data : payload,
				success : function(data) {
					if (data.errCode == undefined)
						success = true;
				},
				complete : function(data) {
					if (success) {
						showPosts(true);
					}
				}
			});
		});

	$('.ppl-share-wrapper .icn-remove').click(function() {
		$(this).hide();
		$(this).parent().find('.ppl-share-social').hide();
		$(this).parent().find('.icn-plus-open').show();
	});

	$('.icn-person').click(function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#contact-wrapper').show();
		$('#prof-agent-container').hide();
		$('#intro-about-me').hide();
		$('#reviews-container').hide();
		$('#ppl-post-cont').hide();
		adjustImage();
	});

	$('.icn-ppl').click(function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#ppl-post-cont').show();
		$('#contact-wrapper').hide();
		$('#prof-agent-container').hide();
		$('#intro-about-me').hide();
		$('#reviews-container').hide();
	});

	$('.icn-star-smile').click(function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#reviews-container').show();
		$('#contact-wrapper').hide();
		$('#prof-agent-container').hide();
		$('#intro-about-me').hide();
		$('#ppl-post-cont').hide();
	});

	$('.inc-more').click(function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#prof-agent-container').show();
		$('#intro-about-me').hide();
		$('#contact-wrapper').hide();
		$('#reviews-container').hide();
		$('#ppl-post-cont').hide();
	});
});
</script>