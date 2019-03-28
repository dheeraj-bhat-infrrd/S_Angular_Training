<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:if test="${not empty profile}">
	<c:if
		test="${not empty profile.contact_details && not empty profile.contact_details.name }">
		<c:set var="profName" value="${profile.contact_details.name }"></c:set>
	</c:if>
	<c:if test="${not empty profile.contact_details}">
		<c:set var="contact_details" value="${profile.contact_details }"></c:set>
		<c:if
			test="${ not empty  contact_details && not empty contact_details.location}">
			<c:set var="location" value="${contact_details.location}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.title}">
			<c:set var="title" value="${contact_details.title}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.industry}">
			<c:set var="vertical" value="${contact_details.industry}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.firstName }">
			<c:set var="firstName" value="${contact_details.firstName}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.lastName }">
			<c:set var="lastName" value="${contact_details.lastName}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.country }">
			<c:set var="country" value="${contact_details.country}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.state }">
			<c:set var="state" value="${contact_details.state}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.city }">
			<c:set var="city" value="${contact_details.city}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.firstName && profileLevel == 'INDIVIDUAL'}">
			<c:set var="agentFirstName" value="${contact_details.firstName}"></c:set>
		</c:if>
	</c:if>
	<c:if test="${empty vertical && not empty profile.vertical}">
		<c:set var="vertical" value="${profile.vertical}"></c:set>
	</c:if>
	<c:choose>
		<c:when test="${profileLevel == 'INDIVIDUAL' }">
			<c:if test="${not empty profile.companyProfileData}">
				<c:set var="companyProfileData"
					value="${profile.companyProfileData }"></c:set>
				<c:if test="${not empty companyProfileData.name}">
					<c:set var="companyName" value="${companyProfileData.name}"></c:set>
				</c:if>
			</c:if>
		</c:when>
	</c:choose>
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
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<meta property="og:image" content="${profile.profileImageUrlThumbnail}" />

<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
<c:if test="${not empty profile}">
	<c:if test="${not empty profile.completeProfileUrl }">
		<link rel="canonical" href="${profile.completeProfileUrl}">
	</c:if>
	<c:if test="${not empty title }">
		<c:set var="includeTitle" value=" ${firstName} is the ${title} of ${companyName}."></c:set>
	</c:if>
	<c:if test="${not empty location }">
		<c:set var="includeLocation" value=" in ${location}"></c:set>
	</c:if>
	<c:if test="${not empty city or not empty state or not empty country }">
		<c:set var="includeLoc" value=" in ${city} ${state} ${country}"></c:set>
	</c:if>
	<c:choose>
		<c:when test="${not empty profName}">
			<c:choose>
				<c:when test="${profileLevel == 'INDIVIDUAL' }">
					<title>${profName} ${title} ${companyName} ${location} ${vertical} Professional Reviews</title>
					<meta name="keywords"
						content="${profName}, ${title }, ${companyName }, ${location }, ${vertical }, professional, online, reputation, social, survey, reviews, rating">
					<meta name="description"
						content="Reviews for ${profName}. ${firstName} has ${reviewsCount} reviews. ${firstName} is a ${vertical} professional${includeLocation}.${includeTitle}">
				</c:when>
				<c:otherwise>
					<title>${profName} ${vertical} Reviews</title>
					<meta name="keywords"
						content="${profName}, ${vertical}, professional, online, reputation, social, survey, reviews, rating">
					<meta name="description"
						content="Reviews for ${profName}. ${profName} has ${reviewsCount} reviews. ${profName} is a ${vertical} company${includeLoc}.">
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<title><spring:message code="label.profile.title.key" /></title>
		</c:otherwise>
	</c:choose>
	<c:if test="${not empty averageRating}">
		<fmt:formatNumber var="floatingAverageRating" type="number"
			value="${averageRating}" maxFractionDigits="2" minFractionDigits="2" />
		<fmt:formatNumber var="floatingAverageGoogleRating" type="number"
			value="${averageRating}" maxFractionDigits="2" minFractionDigits="2" />
		<fmt:formatNumber var="integerAverageRating" type="number"
			value="${averageRating}" maxFractionDigits="0" />
		<c:set var="integerAverageRating" value="${integerAverageRating}"></c:set>
		<c:if test="${integerAverageRating == 0}">
			<c:set var="integerAverageRating" value="1"></c:set>
		</c:if>
	</c:if>
</c:if>
</head>
<body>
	<c:if test="${not empty reviewAggregate}">
		<c:set value="true" var="popup" ></c:set>
		<div id="single-review-page" class="overlay-login overlay-single-review">
			<button type="button" class="close dismiss-single-review-popup" id="dismiss-single-review-popup">&times;</button>
			<c:choose>
				<c:when test="${ reviewAggregate.surveyIdValid }">
						<jsp:include page="single_review_page.jsp"></jsp:include>
				</c:when>
				<c:otherwise>
						<div id="single-review-popup" class="single-review-popup-wrapper">
							<div class="invalid-message-div">
								<span>${reviewAggregate.invalidMessage}</span>
							</div>
						</div>
				</c:otherwise>
			</c:choose>	
		</div>
	</c:if>
	<div id="contact-us-pu-wrapper" class="bd-srv-pu hide">
		<div class="container cntct-us-container">
			<div class="contact-us-pu">
				<div class="bd-quest-item">
					<div class="bd-q-pu-header bd-q-pu-header-adj clearfix">
						<div class="float-left bd-q-pu-header-lft">Please enter your
							message.</div>
					</div>
					<div class="bd-q-pu-txt-wrapper pos-relative">
						<textarea class="bd-q-pu-txtarea"></textarea>
					</div>
				</div>
				<div class="cntct-us-btns-wrapper clearfix">
					<div class="bd-q-btn-cancel float-left">Cancel</div>
					<div class="bd-q-btn-done-pu float-left">Send</div>
				</div>
			</div>
		</div>
	</div>

	<input type="hidden" name="reviewsCount" value="${reviewsCount }">
	<input type="hidden" name="averageRatings" value="${averageRating }">
	<input type="hidden" value="${companyProfileName}"
		id="company-profile-name">
	<input type="hidden" value="${regionProfileName}"
		id="region-profile-name">
	<input type="hidden" value="${branchProfileName}"
		id="branch-profile-name">
	<input type="hidden" value="${agentProfileName}"
		id="agent-profile-name">
	<input type="hidden" id="profile-fetch-info" fetch-all-reviews="false"
		total-reviews="${reviewsCount }" profile-level="${profileLevel}" />
	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
			<div class="float-left hdr-links clearfix"></div>
			<div class="float-right clearfix hdr-btns-wrapper">
				<div class="float-left hdr-log-btn hdr-log-reg-btn">
					<spring:message code="label.signin.key" />
				</div>
				<div class="float-left hdr-reg-btn hdr-log-reg-btn">
					<spring:message code="label.joinus.key" />
				</div>
			</div>
		</div>
	</div>


	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left padding-10">
					<spring:message code="label.readwritesharereviews.key" />
				</div>
				<div class="float-right hm-find-pro-right clearfix">
					<form id="find-pro-form" method="GET" action="/findapro.do">
						<div class="float-left prof-input-header">Find a
							professional</div>
						<div class="float-left prof-input-cont">
							<input id="find-pro-first-name" name="find-pro-first-name"
								type="text" placeholder="First Name">
						</div>
						<div class="float-left prof-input-cont">
							<input id="find-pro-last-name" name="find-pro-last-name"
								type="text" placeholder="Last Name">
						</div>
						<input id="find-pro-start-index" name="find-pro-start-index"
							type="hidden" value="0"> 
						<input id="find-pro-row-size"
							name="find-pro-row-size" type="hidden" value="10"> 
						<input id="find-pro-profile-name" name="find-pro-profile-name" type="hidden" value="${findProCompanyProfileName}">
						<input id="find-pro-submit" type="button"
							class="float-left prof-submit-btn" value="Search">
					</form>
				</div>
			</div>
		</div>
	</div>

	<div id="profile-main-content"
		class="prof-main-content-wrapper margin-top-10 margin-bottom-25">
		<div itemscope itemtype="https://schema.org/ProfilePage" class="">
			<div class="container">
				<div class="row prof-pic-name-wrapper">
					<c:if test="${not empty profile.profileImageUrlThumbnail }">
						<c:set var="profileNameClass" value="profile-name-img-wrapper"></c:set>
						<div id="prog-img-container"
							class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper prog-img-container">
							<div class="prog-img-container" style="background-color: white !important; display: flex;align-items: center;">
								<img itemprop="primaryImageOfPage"
									class="prof-image pos-relative height-auto-resp"
									src="${profile.profileImageUrlThumbnail}"
									alt="Photo of ${profName}"></img>
							</div>
						</div>
					</c:if>
					<div
						class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper ${profileNameClass}">
						<div class="prof-name-container">
							<div itemprop="about" itemscope=""
								itemtype="http://schema.org/Person">
								<div itemprop="name" class="prof-name">${profName}</div>
								<div class="prof-address">
									<c:if test="${not empty profile.vertical}">
										<div class="prof-addline1">${profile.vertical}</div>
									</c:if>
									<c:if
										test="${not empty profile.contact_details &&  not empty profile.contact_details.title}">
										<div itemprop="jobTitle" class="prof-addline2">${profile.contact_details.title}</div>
									</c:if>
								</div>
							</div>
							<!-- Apply aggregate rating only when we have reviews -->
							<c:choose>
								<c:when test="${reviewsCount > 0 }">
									<div itemprop="aggregateRating" itemscope
										itemtype="http://schema.org/AggregateRating"
										class="prof-rating clearfix">
										<div class="prof-rating-wrapper maring-0 clearfix float-left"
											id="rating-avg-comp">
											<div
												class='rating-image float-left smiley-rat-${integerAverageRating }'></div>
											<div class='rating-rounded float-left'>
												<span itemprop="ratingValue">${floatingAverageRating}</span>
												-
											</div>
										</div>
										<div class="float-left review-count-left cursor-pointer"
											id="prof-company-review-count">
											<span itemprop="reviewCount">${reviewsCount }</span>
											Reviews(s)
										</div>
									</div>
								</c:when>
								<c:otherwise>
									<div class="prof-rating clearfix">
										<div class="prof-rating-wrapper maring-0 clearfix float-left"
											id="rating-avg-comp">
											<div
												class='rating-image float-left smiley-rat-${integerAverageRating }'></div>
											<div class='rating-rounded float-left'>
												<span>${floatingAverageRating}</span> -
											</div>
										</div>
										<div class="float-left review-count-left cursor-pointer"
											id="prof-company-review-count">
											<span>${reviewsCount }</span> Reviews(s)
										</div>
									</div>
								</c:otherwise>
							</c:choose>
							<div class="prof-btn-wrapper clearfix">
								<c:if test="${not empty contact_details.mail_ids.work}">
									<div class="prof-btn-contact float-left"
										onclick="focusOnContact()">
										Contact
										<c:choose>
											<c:when test="${not empty agentFirstName}"> ${agentFirstName}</c:when>
											<c:otherwise> ${profName}</c:otherwise>
										</c:choose>
									</div>
								</c:if>
								<c:if test="${!profile.hiddenSection }">
									<div class="prof-btn-survey float-left"
										id="read-write-share-btn">Write a Review</div>
								</c:if>

							</div>
						</div>
					</div>
					<div
						class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper float-right">
						<c:choose>
							<c:when test="${not empty profile.logo }">
								<div class="prof-user-logo" id="prof-company-logo"
									style="background: url(${profile.logo}) no-repeat center; background-size: 100% auto;"></div>
							</c:when>
							<c:otherwise>
								<div class="prof-user-logo" id="prof-company-logo"></div>
							</c:otherwise>
						</c:choose>
						<c:if test="${not empty contact_details}">
							<div itemprop="about" itemscope=""
								itemtype="http://schema.org/Person">
								<div class="prof-user-address" itemprop="address" itemscope
									itemtype="http://schema.org/PostalAddress">
									<c:if
										test="${not empty contact_details.address1 || not empty contact_details.address2 }">
										<div itemprop="streetAddress">
											<c:if test="${not empty  contact_details.address1}">
												<div class="prof-user-addline1">${contact_details.address1}</div>
											</c:if>
											<c:if test="${not empty  contact_details.address2}">
												<div class="prof-user-addline2">${contact_details.address2}</div>
											</c:if>
										</div>
									</c:if>
									<c:if
										test="${not empty contact_details.zipcode || not empty contact_details.city || not empty contact_details.state }">
										<div class="prof-user-addline2">
											<c:if test="${not empty contact_details.city}">
												<span itemprop="addressLocality">${contact_details.city}</span>
											</c:if>
											<c:if test="${not empty contact_details.state}">
												<span itemprop="addressRegion">${contact_details.state}</span>
											</c:if>
											<c:if test="${not empty contact_details.zipcode}">
												<span itemprop="postalCode">${contact_details.zipcode}</span>
											</c:if>
										</div>
										<c:if test="${not empty contact_details.country}">
											<meta itemprop="addressCountry"
												content="${contact_details.country}" />
										</c:if>
									</c:if>
								</div>
							</div>
						</c:if>
					</div>
					<div class="mob-contact-btn-wrapper">
						<div class="mob-contact-btn-row clearfix">
							<div class="mob-contact-btn float-left">
								<div id="mob-contact-btn"
									class="mob-prof-contact-btn float-right"
									onclick="focusOnContact()">
									Contact
									<c:choose>
										<c:when test="${not empty agentFirstName}"> ${agentFirstName}</c:when>
										<c:otherwise> ${profName}</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="mob-contact-btn float-left">
								<div id="mob-review-btn" class="mob-prof-contact-btn float-left">Write
									a review</div>
							</div>
						</div>
						<!-- <div class="vcard-download cursor-pointer">Download Contact</div> -->
					</div>
				</div>
			</div>

			<div class="prof-details-header">
				<div class="container">
					<div class="prof-details-header-row clearfix">
						<c:if test="${not empty profile.completeProfileUrl}">
							<div class="prof-link-header float-left clearfix">
								<div id="prof-header-rating"
									class="rating-image float-left smiley-rat-5"></div>
								<div id="prof-header-url" class="rating-image-txt float-left"
									title="${profile.completeProfileUrl}">
									${profile.completeProfileUrl}</div>
							</div>
						</c:if>
						<c:if
							test="${not empty profile.contact_details && not empty profile.contact_details.web_addresses && not empty profile.contact_details.web_addresses.work}">
							<div id="web-addr-header"
								class="web-addr-header float-left clearfix">
								<div class="web-address-img float-left"></div>
								<div id="web-address-txt"
									class="web-address-txt float-left web-address-link"
									data-link="${profile.contact_details.web_addresses.work}">${profile.contact_details.web_addresses.work}</div>
							</div>
						</c:if>
						<c:if
							test="${not empty profile.contact_details && not empty profile.contact_details.contact_numbers && not empty profile.contact_details.contact_numbers.work}">
							<div class="web-addr-header float-left clearfix">
								<div class="phn-num-img float-left"></div>
								<div class="phone-number-txt float-left">${profile.contact_details.contact_numbers.work}</div>
							</div>
						</c:if>
						<div class="float-right hm-hr-row-right clearfix">
							<c:if test="${not empty profile.socialMediaTokens}">
								<div id="social-connect-txt"
									class="float-left social-connect-txt">Connect with
									${profName }:</div>
								<c:if
									test="${not empty profile.socialMediaTokens.facebookToken && not empty profile.socialMediaTokens.facebookToken.facebookPageLink}">
									<div id="icn-fb" class="float-left social-item-icon icn-fb"
										data-link="${profile.socialMediaTokens.facebookToken.facebookPageLink }"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.twitterToken && not empty profile.socialMediaTokens.twitterToken.twitterPageLink}">
									<div id="icn-twit" class="float-left social-item-icon icn-twit"
										data-link="${profile.socialMediaTokens.twitterToken.twitterPageLink }"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.linkedInV2Token && not empty profile.socialMediaTokens.linkedInV2Token.linkedInPageLink}">
									<div id="icn-lin" class="float-left social-item-icon icn-lin"
										data-link="${profile.socialMediaTokens.linkedInV2Token.linkedInPageLink }"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.yelpToken && not empty profile.socialMediaTokens.yelpToken.yelpPageLink}">
									<div id="icn-yelp" class="float-left social-item-icon icn-yelp"
										data-link="${profile.socialMediaTokens.yelpToken.yelpPageLink }"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.googleToken && not empty profile.socialMediaTokens.googleToken.profileLink}">
									<div id="icn-gplus"
										class="float-left social-item-icon icn-gplus"
										data-link="${profile.socialMediaTokens.googleToken.profileLink }"></div>
								</c:if>
							</c:if>
						</div>
					</div>
				</div>
			</div>

			<div class="container">
				<div class="row margin-top-10">
					<div
						class="prof-left-panel-wrapper col-lg-4 col-md-4 col-sm-4 col-xs-12">

						<c:if test="${not empty profile.contact_details }">
							<c:if
								test="${not empty profile.contact_details.web_addresses || not empty profile.contact_details.contact_numbers}">
								<div
									class="prof-left-row prof-left-info bord-bot-dc prof-contact-info">
									<div class="left-contact-wrapper">
										<div class="left-panel-header">
											<spring:message code="label.contactinformation.key" />
										</div>
										<div class="left-panel-content" id="prof-contact-information">
											<c:if
												test="${not empty profile.contact_details.web_addresses && not empty profile.contact_details.web_addresses.work}">
												<div class="lp-con-row lp-row clearfix">
													<div class="float-left lp-con-icn icn-web"></div>
													<div id="web-addr-link-lp"
														class="float-left lp-con-row-item blue-text web-address-link"
														data-link="${profile.contact_details.web_addresses.work}">
													</div>
												</div>
											</c:if>
											<c:if
												test="${not empty profile.contact_details.contact_numbers && not empty profile.contact_details.contact_numbers.work}">
												<div class="lp-con-row lp-row clearfix">
													<div class="float-left lp-con-icn icn-phone"></div>
													<div class="float-left lp-con-row-item">${profile.contact_details.contact_numbers.work}</div>
												</div>
											</c:if>
										</div>
									</div>
								</div>
							</c:if>
						</c:if>
						<div id="prof-agent-container">
							<c:choose>
								<c:when test="${not empty branchProfileName}">
									<div id="branch-hierarchy"
										class="prof-left-row prof-left-assoc bord-bot-dc hide">
										<div class="left-assoc-wrapper">
											<div class="left-panel-header">
												<spring:message code="label.ourbranch.key" />
											</div>
											<div class="left-panel-content left-panel-content-adj"
												id="branch-individuals">
												<!--branch hierarchy is displayed here  -->
											</div>
										</div>
									</div>
								</c:when>
								<c:when test="${not empty regionProfileName}">
									<div id="region-hierarchy"
										class="prof-left-row prof-left-assoc bord-bot-dc hide">
										<div class="left-assoc-wrapper">
											<input type="hidden" id="branchid-hidden" />
											<div class="left-panel-header">
												<spring:message code="label.ourregion.key" />
											</div>
											<div class="left-panel-content left-panel-content-adj"
												id="region-branches">
												<!--region hierarchy is displayed here  -->
											</div>
										</div>
									</div>
								</c:when>
								<c:when
									test="${not empty companyProfileName && !profile.hiddenSection}">
									<div id="comp-hierarchy"
										class="prof-left-row prof-left-assoc bord-bot-dc hide">
										<div class="left-assoc-wrapper">
											<input type="hidden" id="regionid-hidden" /> <input
												type="hidden" id="branchid-hidden" />
											<div class="left-panel-header">
												<spring:message code="label.ourcompany.key" />
											</div>
											<div class="left-panel-content left-panel-content-adj"
												id="comp-regions-content">
												<!--company hierarchy is displayed here  -->
											</div>
										</div>
									</div>
								</c:when>
								<c:when test="${not empty agentProfileName}">
									<div id="individual-details">
										<!-- individual details like associations/hobbies/achievements come here -->
										<c:if
											test="${not empty profile.licenses && not empty profile.licenses.authorized_in }">
											<div class="prof-left-row prof-left-auth bord-bot-dc">
												<div class="left-auth-wrapper">
													<div class="left-panel-header lph-dd lph-dd-closed">Licenses</div>
													<div class="left-panel-content">
														<c:forEach items="${profile.licenses.authorized_in }"
															var="licenseItem">
															<div class="lp-auth-row lp-row clearfix">${licenseItem}</div>
														</c:forEach>
													</div>
												</div>
											</div>
										</c:if>
										<c:if test="${not empty profile.positions }">
											<div class="prof-left-row prof-left-assoc bord-bot-dc">
												<div class="left-postions-wrapper">
													<div
														class="left-panel-header lph-dd lph-dd-closed lph-dd-open">Positions</div>
													<div class="left-panel-content">
														<c:forEach items="${profile.positions}" var="positionItem">
															<div class="postions-content">
																<c:if test="${not empty positionItem.name}">
																	<div class="lp-pos-row-1 lp-row clearfix">${positionItem.name}</div>
																</c:if>
																<c:if test="${not empty positionItem.title}">
																	<div class="lp-pos-row-2 lp-row clearfix">${positionItem.title}</div>
																</c:if>
																<c:choose>
																	<c:when test="${not positionItem.isCurrent}">
																		<c:if
																			test="${not empty positionItem.startTime && not empty positionItem.endTime}">
																			<div class="lp-pos-row-3 lp-row clearfix">
																				${positionItem.startTime} - ${positionItem.endTime}
																			</div>
																		</c:if>
																	</c:when>
																	<c:otherwise>
																		<c:if test="${not empty positionItem.startTime}">
																			<div class="lp-pos-row-3 lp-row clearfix">
																				${positionItem.startTime} - Current</div>
																		</c:if>
																	</c:otherwise>
																</c:choose>
															</div>
														</c:forEach>
													</div>
												</div>
											</div>
										</c:if>
										<c:if test="${not empty profile.associations }">
											<div class="prof-left-row prof-left-auth bord-bot-dc">
												<div class="left-auth-wrapper">
													<div class="left-panel-header lph-dd lph-dd-closed">Memberships</div>
													<div class="left-panel-content">
														<c:forEach items="${profile.associations }"
															var="associationItem">
															<div class="lp-auth-row lp-row clearfix">${associationItem.name}</div>
														</c:forEach>
													</div>
												</div>
											</div>
										</c:if>
										<c:if test="${not empty profile.expertise }">
											<div class="prof-left-row prof-left-auth bord-bot-dc">
												<div class="left-auth-wrapper">
													<div class="left-panel-header lph-dd lph-dd-closed">Specialties</div>
													<div class="left-panel-content">
														<c:forEach items="${profile.expertise }"
															var="expertiseItem">
															<div class="lp-auth-row lp-row clearfix">${expertiseItem}</div>
														</c:forEach>
													</div>
												</div>
											</div>
										</c:if>
										<c:if test="${not empty profile.achievements }">
											<div class="prof-left-row prof-left-auth bord-bot-dc">
												<div class="left-auth-wrapper">
													<div class="left-panel-header lph-dd lph-dd-closed">Achievements</div>
													<div class="left-panel-content">
														<c:forEach items="${profile.achievements }"
															var="achievementItem">
															<div class="lp-auth-row lp-row clearfix">${achievementItem.achievement}</div>
														</c:forEach>
													</div>
												</div>
											</div>
										</c:if>
										<c:if test="${not empty profile.hobbies }">
											<div class="prof-left-row prof-left-auth bord-bot-dc">
												<div class="left-auth-wrapper">
													<div class="left-panel-header lph-dd lph-dd-closed">Hobbies</div>
													<div class="left-panel-content">
														<c:forEach items="${profile.hobbies }" var="hobbyItem">
															<div class="lp-auth-row lp-row clearfix">${hobbyItem}</div>
														</c:forEach>
													</div>
												</div>
											</div>
										</c:if>
									</div>
								</c:when>
							</c:choose>
							<c:if test="${not empty contact_details.mail_ids.work}">
								<div class="prof-left-row prof-left-assoc bord-bot-dc">
									<div class="left-contact-wrapper">
										<div id="prof-contact-hdr"
											class="left-panel-header prof-contact-hdr">Contact
											${profName }</div>
										<div class="left-panel-content">
											<form id="prof-contact-form" action="">
												<div class="lp-row">
													<div class="lp-input-cont">
														<div class="float-left lp-username-icn lp-input-icn"></div>
														<input id="lp-input-name" type="text" class="lp-input"
															placeholder="Your name">
													</div>
												</div>
												<div class="lp-row">
													<div class="lp-input-cont lp-email">
														<div class="float-left lp-email-icn lp-input-icn"></div>
														<input id="lp-input-email" type="email" class="lp-input"
															placeholder="Email Address">
													</div>
												</div>
												<div class="lp-row">
													<div class="lp-input-cont lp-textarea-cont">
														<div class="float-left lp-textarea-icn lp-input-icn"></div>
														<textarea id="lp-input-message" class="lp-input"
															placeholder="I'd like to get in contact with you about.."></textarea>
													</div>
												</div>
												<div class="lp-row">
													<div class="prof-captcha-cont">
														<div class="g-recaptcha"
															data-sitekey="6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K"></div>
														<!-- <div id="prof-captcha-img" class="prof-captcha-img"></div>
											<div class="reg-captcha-btns clearfix">
												<input id="captcha-text" class="float-left prof-cap-txt"
													name="captchaResponse" placeholder="Type the above text"
													autocomplete="off" autocorrect="off" autocapitalize="off">
												<div class="clearfix reg-btns-wrapper float-right">
													<div class="float-left reg-cap-img reg-cap-reload"></div>
													<div class="float-left reg-cap-img reg-cap-sound"></div>
													<div class="float-left reg-cap-img reg-cap-info"></div>
												</div>
											</div> -->
													</div>
												</div>
												<div class="lp-row">
													<div class="lp-button">Submit Your Message</div>
												</div>
												<div class="privacy-policy-disclaimer">We will only
													use information you provide on this form to send your
													message to this professional.</div>
												<div id="privacy-policy-link" class="privacy-policy-link">Privacy
													Policy</div>
											</form>
										</div>
									</div>
								</div>
							</c:if>
						</div>
					</div>

					<div
						class="row prof-right-panel-wrapper col-lg-8 col-md-8 col-sm-8 col-xs-12">
						<div class="intro-wrapper rt-content-main bord-bot-dc hide"
							id="prof-company-intro">
							<div class="main-con-header">About ${profName}</div>
							<c:choose>
								<c:when
									test="${not empty profile.contact_details && not empty profile.contact_details.about_me }">
									<div itemprop="description" class="pe-whitespace intro-body">${profile.contact_details.about_me}</div>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${profileLevel == 'INDIVIDUAL'}">
											<div class="intro-body">
												Reviews for <span class="capitalize">${profName}</span>. <span
													class="capitalize">${firstName}</span> has ${reviewsCount}
												reviews.
												<c:if test="${not empty  vertical && not empty location}">
													<span class="capitalize">${firstName}</span> is a ${vertical} professional in ${location}.
										</c:if>
												<c:if test="${not empty title}">
													<span class="capitalize">${firstName}</span> is the ${title} of ${companyName}.
										</c:if>
											</div>
										</c:when>
										<c:otherwise>
											<div class="intro-body">
												Reviews for <span class="capitalize">${profName}</span>. <span
													class="capitalize">${profName}</span> has ${reviewsCount}
												reviews.
												<c:if test="${not empty country && not empty vertical}">
													<span class="capitalize">${profName}</span> is a ${vertical} company in ${city} ${state} ${country}.
                    					</c:if>
											</div>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</div>
						<!--<c:set var="postsHidden" value="no" />
                <c:if test="${not empty profile.hideSectionsFromProfilePage}">
					<c:forEach var="item" items="${profile.hideSectionsFromProfilePage}">
					  <c:if test="${item == 'recent_posts'}">
					    <c:set var="postsHidden" value="yes" />
					  </c:if>
					</c:forEach>
				</c:if>
                <c:if test="${postsHidden == 'no' && not empty posts}">
                <div class="rt-content-main bord-bot-dc clearfix" id="recent-post-container">
                    <div class="float-left panel-tweet-wrapper">
                        <div class="main-con-header">Recent Posts</div>
                        <div class="tweet-panel tweet-panel-left tweet-panel-left-adj" id="prof-posts">
                        	<c:forEach var="postItem" items="${posts}">
                        	<c:choose>
                        		<c:when test="${postItem.source == 'google' }">
                        			<c:set var="iconClass" value="icn-gplus"></c:set>
                        		</c:when>
                        		<c:when test="${postItem.source == 'SocialSurvey' }">
                        			<c:set var="iconClass" value="icn-ss"></c:set>
                        		</c:when>
                        		<c:when test="${postItem.source == 'facebook' }">
                        			<c:set var="iconClass" value="icn-fb"></c:set>
                        		</c:when>
                        		<c:when test="${postItem.source == 'twitter' }">
                        			<c:set var="iconClass" value="icn-twit"></c:set>
                        		</c:when>
                        		<c:when test="${postItem.source == 'linkedin' }">
                        			<c:set var="iconClass" value="icn-lin"></c:set>
                        		</c:when>
                        	</c:choose>
                        	<jsp:useBean id="postDate" class="java.util.Date"/> 
                        	<c:set target="${postDate}" property="time" value="${postItem.timeInMillis}"/>
                        	<div class="tweet-panel-item bord-bot-dc clearfix">
                        		<div class="tweet-icn float-left ${iconClass}"></div>
                        		<div class="tweet-txt float-left">
                        			<div class="tweet-text-main">${postItem.postText }</div>
                        			<div class="tweet-text-link"><em>${postItem.postedBy }</em></div>
                        			<div class="tweet-text-time"><em><fmt:formatDate value="${postDate}" type="both" dateStyle="medium" timeStyle="long"/></em></div>
                        		</div>
                        	</div>
                        	</c:forEach>
                        </div>
                    </div>
                </div>
                </c:if>-->
						<c:if test="${not empty reviews}">
							<div class="people-say-wrapper rt-content-main"
								id="reviews-container">
								<div class="clearfix"
									style="display: block; border-bottom: 1px solid #dcdcdc; padding: 15px 0;">
									<div class="main-con-header float-left"
										id="prof-reviews-header">
										<span class="ppl-say-txt-st">What people say</span> about
										${profName }
									</div>

									<div id="prof-reviews-sort"
										class="prof-reviews-sort clearfix float-right">
										<div id="sort-by-feature"
											class="prof-review-sort-link float-left">Sort by
											Feature</div>
										<div class="prof-reviews-sort-divider float-left">|</div>
										<div id="sort-by-date"
											class="prof-review-sort-link float-right">Sort by Date</div>
									</div>
								</div>

								<div id="prof-review-item" class="prof-reviews">
									<c:forEach items="${reviews }" var="reviewItem">
										<fmt:formatNumber var="integerRating"
											value="${reviewItem.score }" maxFractionDigits="0" />
										<c:if test="${integerRating == 0}">
											<c:set var="integerRating" value="1"></c:set>
										</c:if>
										
										<c:if test="${ not empty reviewItem.customerLastName }">
											<c:set value="${ reviewItem.customerFirstName } ${ reviewItem.customerLastName }" var="customerName"></c:set>
										</c:if>
										
										<c:if test="${ empty reviewItem.customerLastName }">
											<c:set value="${ reviewItem.customerFirstName }" var="customerName"></c:set>
										</c:if>
										
										
										<c:set value="${fn:split(customerName, ' ')}" var="nameArray"></c:set>
										
										<c:choose>
											<c:when test="${ not empty nameArray[1] }">
												<c:set value="${ nameArray[0] } ${ nameArray[1].substring( 0, 1 ).toUpperCase() }" var="customerDisplayName"></c:set>
											</c:when>
											<c:otherwise>
												<c:set value="${ nameArray[0] }" var="customerDisplayName"></c:set>
											</c:otherwise>
										</c:choose>
										
										<div itemprop="review" itemscope
											itemtype="http://schema.org/Review" class="ppl-review-item"
											data-cust-first-name="${reviewItem.customerFirstName }"
											data-cust-last-name="${reviewItem.customerLastName }"
											data-agent-name="${reviewItem.agentName }"
											data-rating="${reviewItem.score }"
											data-review="${reviewItem.review}">
											<div class="ppl-header-wrapper clearfix">
												<div class="float-left ppl-header-right">
													<div
														class="st-rating-wrapper maring-0 clearfix review-ratings float-right"
														data-modified="false" data-rating="${reviewItem.score}"
														data-source="${reviewItem.source }">
														<c:set var="star" value="${reviewItem.score}" />
														<fmt:formatNumber var="star1" value="${star}"
															minFractionDigits="1" maxFractionDigits="1" />
														<fmt:formatNumber var="star2" value="${star1}"
															maxFractionDigits="2" />
														<fmt:parseNumber var="star3" integerOnly="true"
															type="number" value="${star2*4}" />
														<fmt:formatNumber var="star4" value="${star3/4}"
															minFractionDigits="2" maxFractionDigits="2" />
														<c:choose>
															<c:when test="${reviewItem.source =='Zillow'}">
																<div
																	class="rating-image cursor-pointer  float-left star-rating-green-${star4}"
																	title="${star1}/5.0"></div>
															</c:when>
															<c:otherwise>
																<div
																	class="rating-image  cursor-pointer float-left star-rating-${star4}"
																	title="${star1}/5.0"></div>
															</c:otherwise>
														</c:choose>

														<div class="rating-rounded float-left"
															style="font-size: 15px; line-height: 22px;">${star1}</div>
													</div>
												</div>
												<c:choose>
													<c:when test="${reviewItem.source =='encompass'}">
														<div class='verified-badge  verify-image float-right'
															title='Click here to know more'></div>
													</c:when>
													<c:when test="${reviewItem.source =='DOTLOOP'}">
														<div class='verified-badge  verify-image float-right'
															title='Click here to know more'></div>
													</c:when>
													<c:when test="${reviewItem.source =='API'}">
														<div class='verified-badge  verify-image float-right'
															title='Click here to know more'></div>
													</c:when>
													<c:when test="${reviewItem.source =='Zillow'}">
														<div class='zillow-badge  verify-image-zillow float-right'></div>
													</c:when>
													<c:when test="${reviewItem.source =='facebook'}">
														<div class='fb-verified-image verify-image-fb float-right'></div>
													</c:when>
													<c:when test="${reviewItem.source =='google'}">
														<div class='google-verified-image verify-image-google float-right'></div>
													</c:when>
													<c:otherwise>
														<div class='unverified-badge  verify-image-ss float-right'></div>
													</c:otherwise>
												</c:choose>
												<div class=" ppl-header-left review-sm-screen ">
													<c:set value="${fn:escapeXml(reviewItem.review)}"
														var="review"></c:set>
													<%-- <c:choose>
						<c:when test="${ not empty reviewItem.surveyCompletedDate}">
							<div class="ppl-head-2 review-detail-profile float-left"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${reviewItem.surveyCompletedDate}" />">
							</div>
						</c:when>
						<c:otherwise>
							<div class="ppl-head-2 review-detail-profile float-left"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${reviewItem.modifiedOn}" />">
							</div>
						</c:otherwise>
					</c:choose> --%>
													<c:choose>
														<c:when
															test="${ not empty reviewItem.surveyUpdatedDate and reviewItem.surveyUpdatedDate != reviewItem.surveyCompletedDate }">
															<div class="ppl-head-2 review-detail-profile float-left"> Survey updated on </div> 
															<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;">
																<fmt:formatDate type="date" pattern="MMMM  d, YYYY"
																	value="${reviewItem.surveyUpdatedDate}" />
															</div>
														</c:when>
														<c:when
															test="${ not empty reviewItem.surveyCompletedDate}">
															<div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div>
															<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;">
																<fmt:formatDate type="date" pattern="MMMM d, YYYY"
																	value="${reviewItem.surveyCompletedDate}" />
															</div>
														</c:when>
														<c:otherwise>
														<div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div> 
															<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;">
																<fmt:formatDate type="date" pattern="MMMM d, YYYY"
																	value="${reviewItem.modifiedOn}" />
															</div>
														</c:otherwise>
													</c:choose>
													<div class="ppl-head-1 " style="clear: both">
														<span class="float-left"> Reviewed by </span>
														<span>${customerDisplayName}</span>
														<c:if test="${profilemasterid !=4}">
															<c:if test="${not empty reviewItem.agentName}">
																<span class="float-left" style="margin-left: 5px;">for
																	<c:if test="${profile.hiddenSection}">
							 ${reviewItem.agentName}
							 </c:if> <c:if test="${!profile.hiddenSection}">
																		<a class="cursor-pointer"
																			style="color: #236CAF; font-weight: 600 !important;"
																			href="${reviewItem.completeProfileUrl}"
																			target="_blank"> ${reviewItem.agentName}</a>
																	</c:if>
																</span>
															</c:if>
														</c:if>


														<c:choose>
															<c:when test="${ not empty reviewItem.summary }">
																<div class="ppl-content"
																	style="clear: both; padding-top: 0px !important;">${reviewItem.summary}</div>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when
																		test="${not (empty reviewItem.surveyGeoLocation and empty reviewItem.surveyType)}">
																		<div class="ppl-content "
																			style="clear: both; padding-top: 0px !important;">${reviewItem.surveyGeoLocation}
																			<span>${reviewItem.surveyType}</span>
																		</div>
																	</c:when>
																	<c:otherwise>
																		<c:if test="${reviewItem.source ne 'customer'}">
																			<div style="clear: both">
																				<spring:message
																					code="label.completedTransaction.key" />
																				<c:choose>
																					<c:when
																						test= "${not empty reviewItem.surveyTransactionDate}">
																						<span class="completedOn">
																						<fmt:formatDate
																								type="date" pattern="MMMM YYYY"
																								value="${ reviewItem.surveyTransactionDate}" /></span>
																					</c:when>
																					<c:otherwise>
																						<span class="completedOn">
																						<fmt:formatDate
																								type="date" pattern="MMMM YYYY"
																								value="${reviewItem.modifiedOn}" /></span>
																					</c:otherwise>
																				</c:choose>
																			</div>
																		</c:if>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</div>
												</div>
											</div>
											<div class="ppl-content" itemprop="reviewBody">${reviewItem.review}</div>
											<!-- <div class="ppl-share-wrapper clearfix">
										<div class="float-left blue-text ppl-share-shr-txt">Share</div>
										<div class="float-left clearfix ppl-share-social">
											<div class="float-left ppl-share-icns icn-fb icn-fb-pp"></div>
											<div class="float-left ppl-share-icns icn-twit icn-twit-pp"></div>
											<div class="float-left ppl-share-icns icn-lin icn-lin-pp"></div>
											<div class="float-left ppl-share-icns icn-gplus"></div>
											<div class="float-left ppl-share-icns icn-yelp"></div>
										</div>
									</div> -->
											<div class="ppl-share-wrapper clearfix share-plus-height">
												<div class="float-left clearfix ppl-share-social ">
													<c:if test="${not empty reviewItem.agentName}">
														<c:set var="includeAgentName" value="for ${reviewItem.agentName} "></c:set>
													</c:if>
													<span id="fb_${loop.index}"
														class="float-left ppl-share-icns icn-fb-rev"
														title="Facebook" 
														data-link="https://www.facebook.com/dialog/share?${reviewItem.faceBookShareUrl}&href=${fn:replace(reviewItem.completeProfileUrl, 'localhost', '127.0.0.1')}/${reviewItem._id}&quote=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey -${fn:escapeXml(reviewItem.review)}&redirect_uri=https://www.facebook.com"></span>

													<c:choose>
														<c:when test="${fn:length(reviewItem.review) > 180}">
															<c:set var="twitterReview"
																value="${fn:substring(reviewItem.review,0,176)}..."></c:set>
														</c:when>
														<c:otherwise>
															<c:set var="twitterReview" value="${reviewItem.review}"></c:set>
														</c:otherwise>
													</c:choose>
													<input type="hidden" id="twttxt_${loop.index}"
														class="twitterText_loop"
														value="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(twitterReview)}" />

													<span class="float-left ppl-share-icns icn-twit-rev"
														id="twitt_${loop.index}"
														onclick="twitterProfileFn(${loop.index},this);"
														data-link="https://twitter.com/intent/tweet?text=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(twitterReview)}&url=${reviewItem.completeProfileUrl}/${reviewItem._id}"></span>
													<span class="float-left ppl-share-icns icn-lin-rev"
														title="LinkedIn" data-link="https://www.linkedin.com/shareArticle?mini=true&url=${reviewItem.completeProfileUrl}/${reviewItem._id}&title=&summary=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(reviewItem.review)} + &source="></span>
													<span class="float-left" title="Google+">
														<button
															class="g-interactivepost float-left ppl-share-icns icn-gplus-rev"
															data-contenturl="${reviewItem.completeProfileUrl}/${reviewItem._id}"
															data-clientid="${reviewItem.googleApi}"
															data-cookiepolicy="single_host_origin"
															data-prefilltext="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(reviewItem.review)}"
															data-calltoactionlabel="USE"
															data-calltoactionurl="${reviewItem.completeProfileUrl}">
															<span class="icon">&nbsp;</span> <span class="label">share</span>
														</button>
													</span>
												</div>
												<div class="float-right dash-flag-retake ">
													<div class="clearfix">
														<div
															class="icn-flag float-left report-abuse-txt cursor-pointer "
															title="Report"></div>
													</div>
												</div>
											</div>

										</div>
									</c:forEach>
								</div>
								<div id="prof-hidden-review-count"
									class="prof-hidden-review-link">
									<!--  count of hidden reviews get populated here -->
								</div>
							</div>
						</c:if>
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
	<!-- EOC -->
</body>
</html>
