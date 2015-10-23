<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!-- Setting common page variables -->
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profileSettings.completeProfileUrl}">
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
	<c:if test="${not empty socialMediaTokens.facebookToken && not empty socialMediaTokens.facebookToken.facebookPageLink}">
		<c:set value="${socialMediaTokens.facebookToken.facebookPageLink}" var="fbLink"></c:set>
	</c:if>
	<c:if test="${not empty socialMediaTokens.twitterToken && not empty socialMediaTokens.twitterToken.twitterPageLink}">
		<c:set value="${socialMediaTokens.twitterToken.twitterPageLink}" var="twtLink"></c:set>
	</c:if>
	<c:if test="${not empty socialMediaTokens.linkedInToken && not empty socialMediaTokens.linkedInToken.linkedInPageLink}">
		<c:set value="${socialMediaTokens.linkedInToken.linkedInPageLink}" var="lnLink"></c:set>
	</c:if>
	<c:if test="${not empty socialMediaTokens.googleToken && not empty socialMediaTokens.googleToken.profileLink}">
		<c:set value="${socialMediaTokens.googleToken.profileLink}" var="googleLink"></c:set>
	</c:if>
	<c:if test="${not empty socialMediaTokens.zillowToken && not empty socialMediaTokens.zillowToken.zillowProfileLink}">
		<c:set value="${socialMediaTokens.zillowToken.zillowProfileLink}" var="zillowLink"></c:set>
	</c:if>
	<c:set value="${socialMediaTokens.yelpToken}" var="yelpToken"></c:set>
	<c:set value="${socialMediaTokens.lendingTreeToken}" var="lendingTreeToken"></c:set>
	<c:set value="${socialMediaTokens.realtorToken}" var="realtorToken"></c:set>
</c:if>
<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>

<!-- Setting agent page variables -->
<c:if test="${profilemasterid == 4}">
	<c:if test="${not empty profileSettings && not empty profileSettings.licenses}">
		<c:set value="${profileSettings.licenses.authorized_in}" var="authorisedInList"></c:set>
	</c:if>
	<c:if test="${not empty profileSettings && not empty profileSettings.positions}">
		<c:set value="${profileSettings.positions}" var="positions"></c:set>
	</c:if>
	<c:if test="${not empty profileSettings && not empty profileSettings.associations}">
		<c:set value="${profileSettings.associations}" var="associations"></c:set>
	</c:if>
	<c:if test="${not empty profileSettings && not empty profileSettings.expertise}">
		<c:set value="${profileSettings.expertise}" var="expertiseList"></c:set>
	</c:if>
	<c:if test="${not empty profileSettings && not empty profileSettings.achievements}">
		<c:set value="${profileSettings.achievements}" var="achievements"></c:set>
	</c:if>
	<c:if test="${not empty profileSettings && not empty profileSettings.hobbies}">
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
				<input type="hidden" id="prof-region-id" value="${entityId}">
			</c:when>
			<c:when test="${profilemasterid == 3}">
				<input type="hidden" id="prof-branch-id" value="${entityId}">
			</c:when>
			<c:when test="${profilemasterid == 4}">
				<input type="hidden" id="prof-agent-id" value="${entityId}">
			</c:when>
		</c:choose>
		<input type="hidden" id="profile-id" value="${profile.userProfileId}"/>
		<input type="hidden" id="profile-min-post-score" value="${profileSettings.survey_settings.show_survey_above_score}"/>
	</div>
	
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left hm-header-row-left-edit-pr"><spring:message code="label.profileheader.key" /></div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
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
					<jsp:include page="profile_profileimage.jsp"></jsp:include>
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper edit-prof-name-wrapper">
				<div id="prof-basic-container" class="prof-name-container">
					<jsp:include page="profile_basicdetails.jsp"></jsp:include>
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper float-right">
				<div id="prof-logo-container" class="lp-prog-img-container" style="position: relative;">
					<jsp:include page="profile_profilelogo.jsp"></jsp:include>
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
							<div id="prof-header-url" class="rating-image-txt float-left edit-pro-rat-txt">
								<a href="${completeProfileUrl}" target="_blank">${completeProfileUrl}</a>
							</div>
							<c:if test="${ profilemasterid != 1 }">
								<div class="float-left edit-pos-icn" onclick="editProfileUrl();" title="Edit"></div>
							</c:if>
						</c:if>
						
					</div>
					<%-- <c:if test="${not empty webAddresses.work}">
						<div id="web-addr-header" class="web-addr-header float-left clearfix">
							<div class="web-address-img float-left"></div>
							<div id="web-address-txt" class="web-address-txt float-left">${webAddresses.work}</div>
						</div>
					</c:if> --%>

					<c:if test="${accountMasterId != 5}">
						<div id="prof-edit-social-link" class="prof-edit-social-link float-right hm-hr-row-right clearfix">
							<div id="icn-fb" class="float-left social-item-icon icn-fb" data-source="facebook" data-link="${fbLink}" onclick="openAuthPage('facebook');" title="Facebook"></div>
							<div id="icn-twit" class="float-left social-item-icon icn-twit" data-source="twitter" data-link="${twtLink}" onclick="openAuthPage('twitter');" title="Twitter"></div>
							<div id="icn-lin" class="float-left social-item-icon icn-lin" data-source="linkedin" data-link="${lnLink}" onclick="openAuthPage('linkedin');" title="LinkedIn"></div>
                            <div id="icn-gplus" class="float-left social-item-icon icn-gplus" data-source="google" data-link="${googleLink}" onclick="openAuthPage('google');" title="Google+"></div>
							<div id="icn-yelp" class="float-left social-item-icon icn-yelp" data-source="yelp" data-link="${yelpToken.yelpPageLink}" title="Yelp"></div>
							<div id="icn-zillow" class="float-left social-item-icon icn-zillow" data-source="zillow" title="Zillow" data-link="${zillowLink}" onclick="openAuthPage('zillow');"></div>
							<div id="icn-lendingtree" class="float-left social-item-icon icn-lendingtree" data-source="lendingtree" data-link="${lendingTreeToken.lendingTreeProfileLink}" title="LendingTree"></div>
							<div id="icn-realtor" class="float-left social-item-icon icn-realtor" data-source="realtor" data-link="${realtorToken.realtorProfileLink}" title="Realtor"></div>
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
							<!-- Positions left panel -->
							<div class="prof-left-row prof-left-auth bord-bot-dc">
								<div class="left-auth-wrapper">
									<div class="clearfix">
										<div class="float-left left-panel-header"><spring:message code="label.positions.key" /></div>
										<div class="float-right icn-share edit-pos-icn" onclick="editPositions();" title="Edit"></div>
									</div>
									<div id="positions-container" class="left-panel-content">
										<c:choose>
											<c:when test="${not empty positions}">
												<c:forEach items="${positions}" var="positionItem">
													<div class="postions-content">
              		 									<c:if test="${not empty positionItem.name}">
              		 										<div class="lp-pos-row-1 lp-row clearfix">${positionItem.name}</div>
              		 									</c:if>
              		 									<c:if test="${not empty positionItem.title}">
              		 										<div class="lp-pos-row-2 lp-row clearfix">${positionItem.title}</div>
              		 									</c:if>
              		 									<c:choose>
              		 										<c:when test="${not positionItem.isCurrent}">
              		 											<c:if test="${not empty positionItem.startTime && not empty positionItem.endTime}">
              		 												<div class="lp-pos-row-3 lp-row clearfix">
              		 													${positionItem.startTime} - ${positionItem.endTime}
              		 												</div>
              		 											</c:if>
              		 										</c:when>
              		 										<c:otherwise>
              		 											<c:if test="${not empty positionItem.startTime}">
               		 											<div class="lp-pos-row-3 lp-row clearfix">
               		 												${positionItem.startTime} - Current
               		 											</div>
              		 											</c:if>
              		 										</c:otherwise>
              		 									</c:choose>
              		 								</div>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<span><spring:message code="label.positions.empty.key"></spring:message></span>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</div>
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
														<div class="float-right lp-ach-item-img hide" data-type="expertise"></div>
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
														<div class="float-right lp-ach-item-img hide" data-type="hobby"></div>
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
							<%-- <input id="disclaimer-text" textarea class="float-left lp-con-row-item blue-text prof-editable-disclaimer"
								value="${disclaimer}" placeholder='<spring:message code="label.disclaimer.placeholder.key"/>'>
							<input id="disclaimer-default" type="hidden" value="${disclaimer}"> --%>
							<c:if test="${not empty disclaimer }">
								<c:set value="${fn:trim(disclaimer)}" var="disclaimerVal"></c:set>
							</c:if>
							<textarea class="pe-whitespace sb-txtarea disclaimer-text" id="disclaimer-text" placeholder="Add a Disclaimer">${disclaimerVal}</textarea>
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
					<jsp:include page="profile_aboutme.jsp"></jsp:include>
				</div>
				
				<div id="ppl-post-cont" class="rt-content-main bord-bot-dc clearfix">
					<div class="float-left panel-tweet-wrapper">
						<textarea class="pe-whitespace sb-txtarea" id="status-body-text-edit" placeholder="<spring:message code="label.sspost.key"/>"></textarea>
						<div id="prof-post-btn" class="pe-btn-post"><spring:message code="label.socialpost.key"/></div>
					</div>
					<div class="float-left panel-tweet-wrapper">
						<div class="main-con-header"><spring:message code="label.latestposts.key"/></div>
						<div id="prof-posts" class="tweet-panel tweet-panel-left">
							<!--  latest posts get populated here --> 
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
		
		$('.va-dd-wrapper').perfectScrollbar({
			suppressScrollX : true
		});
		$('.va-dd-wrapper').perfectScrollbar('update');
		
		hideOverlay();
		countPosts();
		$(document).attr("title", "Profile Settings");
		
		if ($("#da-dd-wrapper-profiles").children('.da-dd-item').length <= 1) {
			$('#da-dd-wrapper').remove();
		} else {
			$('#da-dd-wrapper').show();
		}
		
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
			if (textContent == undefined || textContent == "") {
				$('#overlay-toast').html("Please enter valid data to post");
				showToast();
				return;
			}
			
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
				},
				error : function(e) {
					if(e.status == 504) {
						redirectToLoginPageOnSessionTimeOut(e.status);
						return;
					}
					redirectErrorpage();
				}
			});
		});

	$('.ppl-share-wrapper .icn-remove').click(function() {
		$(this).hide();
		$(this).parent().find('.ppl-share-social').hide();
		$(this).parent().find('.icn-plus-open').show();
	});

	$('.icn-person').on('click touchstart', function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#contact-wrapper').show();
		$('#prof-agent-container').hide();
		$('#intro-about-me').hide();
		$('#reviews-container').hide();
		$('#ppl-post-cont').hide();
		adjustImage();
	});

	$('.icn-ppl').on('click touchstart', function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#ppl-post-cont').show();
		$('#contact-wrapper').hide();
		$('#prof-agent-container').hide();
		$('#intro-about-me').hide();
		$('#reviews-container').hide();
	});

	$('.icn-star-smile').on('click touchstart', function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#reviews-container').show();
		$('#contact-wrapper').hide();
		$('#prof-agent-container').hide();
		$('#intro-about-me').hide();
		$('#ppl-post-cont').hide();
	});

	$('.inc-more').on('click touchstart', function() {
		$('.mob-icn').removeClass('mob-icn-active');
		$(this).addClass('mob-icn-active');
		$('#prof-agent-container').show();
		$('#intro-about-me').hide();
		$('#contact-wrapper').hide();
		$('#reviews-container').hide();
		$('#ppl-post-cont').hide();
	});
	$('#prof-basic-container').on('mouseover',function(e){
		$('#prof-basic-container .prof-edit-field-icn').show();
		$('#prof-basic-container .prof-edditable').addClass('prof-name-edit');
	});
	$('#prof-basic-container').on('mouseleave',function(e){
		if(!$('#prof-basic-container input').is(':focus')){
			$('#prof-basic-container .prof-edit-field-icn').hide();
			$('#prof-basic-container .prof-edditable').removeClass('prof-name-edit');			
		}
	});
	
	$('#prof-posts').on('mouseover', '.tweet-panel-item' , function(e){
		$(this).find('.dlt-survey-wrapper').removeClass('hide');
	});

	$('#prof-posts').on('mouseleave', '.tweet-panel-item', function(e){
		$(this).find('.dlt-survey-wrapper').addClass('hide');
	});

	
	$('#prof-posts').on('click' , '.post-dlt-icon' , function(e){
		var surveyMongoId = $(this).attr('surveymongoid');
		
		$('#overlay-main').show();
		$('#overlay-continue').show();
		$('#overlay-continue').html("Delete");
		$('#overlay-cancel').html("Cancel");
		$('#overlay-header').html("Delete Post");
		$('#overlay-text').html("Are you sure you want to delete the post ?");
		$('#overlay-continue').attr("onclick", "removeUserPost('" + surveyMongoId + "');");

	});

});
	
	function removeUserPost(surveyMongoId){
		
		$('#overlay-continue').removeAttr("onclick");
		$('#overlay-main').hide();
		var payload = {
				"statusmongoid" : surveyMongoId
			};
			$.ajax({
				url : "./deletestatus.do",
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
						$('#overlay-toast').html(data.responseText);
						showToast();
						showPosts(true);
					}else{
						$('#overlay-toast').html(data.responseText);
						showToast();
					}
				},
				error : function(e) {
					if(e.status == 504) {
						redirectToLoginPageOnSessionTimeOut(e.status);
						return;
					}
					redirectErrorpage();
				}
			});
	};
	
	$('#prof-edit-social-link').children('.social-item-icon').each(function() {
		var dataLink = $(this).attr('data-link');
		if(dataLink == undefined || dataLink == "") {
			$(this).addClass('icn-social-add');
		}
	});
	
</script>