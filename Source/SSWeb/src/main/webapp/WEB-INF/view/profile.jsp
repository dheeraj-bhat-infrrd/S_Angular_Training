<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${not empty profile}">
	<c:if
		test="${not empty profile.contact_details && not empty profile.contact_details.name}">
		<c:set var="profName" value="${profile.contact_details.name}"></c:set>
	</c:if>
	<c:if test="${not empty profile.contact_details}">
		<c:set var="contact_details" value="${profile.contact_details}"></c:set>
		<c:if
			test="${ not empty  contact_details && not empty contact_details.location}">
			<c:set var="location" value="${contact_details.location}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.title}">
			<c:set var="title" value="${contact_details.title}"></c:set>
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
		<c:if test="${not empty contact_details.mail_ids }">
			<c:set var="workEmail" value="${contact_details.mail_ids.work}"></c:set>
		</c:if>
		<c:if test="${not empty contact_details.firstName && profileLevel == 'INDIVIDUAL'}">
			<c:set var="agentFirstName" value="${contact_details.firstName}"></c:set>
		</c:if>
	</c:if>
	
	<c:if test="${not empty profile.vertical}">
		<c:set var="vertical" value="${profile.vertical}"></c:set>
	</c:if>
	
	<c:if
		test="${not empty profile.contact_details && not empty profile.contact_details.name }">
		<c:set var="profName" value="${profile.contact_details.name }"></c:set>
	</c:if>
	<c:if test="${not empty title and title != ''}">
		<c:set var="includeTitle" value=" ${firstName} is the ${title} of ${companyName}."></c:set>
	</c:if>
	<c:if test="${not empty location and location != '' }">
		<c:set var="includeLocation" value=" in ${location}"></c:set>
	</c:if>
	<c:if test="${not empty city or not empty state or not empty country }">
		<c:set var="includeLoc" value=" in ${city} ${state} ${country}"></c:set>
	</c:if>
	
	<c:choose>
		<c:when test="${profileLevel == 'INDIVIDUAL'}">
			<c:if test="${not empty profile.companyProfileData}">
				<c:set var="companyProfileData"
					value="${profile.companyProfileData}"></c:set>
				<c:if test="${not empty companyProfileData.name}">
					<c:set var="companyName" value="${companyProfileData.name}"></c:set>
				</c:if>
			</c:if>
		</c:when>
	</c:choose>
</c:if>

    <c:choose>
		<c:when test="${not empty profile.contact_details && not empty profile.contact_details.about_me && not fn:containsIgnoreCase(profile.contact_details.about_me, '<') }">
			<c:set var="descriptionTag" value="${profile.contact_details.about_me}"></c:set>
		</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${profileLevel == 'INDIVIDUAL'}">
				<c:set var="descriptionTag" value="${profName} has ${reviewsCount} reviews. ${firstName} is a ${vertical} professional ${includeLocation}.${includeTitle}"></c:set>				
			</c:when>
			<c:when test="${profileLevel == 'REGION'}">
				<c:set var="descriptionTag" value="${companyName} ${profName} has ${reviewsCount} reviews. ${profName} is a region for the ${vertical} company ${companyName}"></c:set>				
			</c:when>
			<c:when test="${profileLevel == 'BRANCH'}">
				<c:set var="descriptionTag" value="${companyName} ${profName} has ${reviewsCount} reviews. ${profName} is a office for the ${vertical} company ${companyName}"></c:set>				
			</c:when>
			<c:otherwise>
				<c:set var="descriptionTag" value="${profName} has ${reviewsCount} reviews. ${profName} is a ${vertical} company ${includeLoc}."></c:set>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
	</c:choose>

<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta property="og:image" content="${profile.profileImageUrlThumbnail}" />
<meta property="og:description" content="${descriptionTag}" />
<meta property="og:profileLevel" content="${profileLevel}" />
<c:choose>
	<c:when test="${not empty profName}">
		<c:choose>
			<c:when test="${profileLevel == 'INDIVIDUAL'}">
				<c:choose>
					<c:when test="${companyName == companyNameForTitle}">
						<meta property="og:title" content="${profName} ${title} ${companyNameForTitle} ${vertical} Professional Reviews" />
					</c:when>
					<c:otherwise>
						<meta property="og:title" content="${profName} ${title} ${companyName} ${companyNameForTitle} ${vertical} Professional Reviews" />
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<meta property="og:title" content="${profName} ${vertical} Reviews" />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<meta property="og:title" content="<spring:message code="label.profile.title.key" />" />
		<title></title>
	</c:otherwise>
</c:choose>
<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/search-engine.css">
<c:if test="${not empty profile}">
	<c:if
		test="${not empty profile.contact_details && not empty profile.contact_details.name }">
		<c:set var="profName" value="${profile.contact_details.name }"></c:set>
	</c:if>
	<c:if test="${not empty title and title != ''}">
		<c:set var="includeTitle" value=" ${firstName} is the ${title} of ${companyName}."></c:set>
	</c:if>
	<c:if test="${not empty location and location != '' }">
		<c:set var="includeLocation" value=" in ${location}"></c:set>
	</c:if>
	<c:if test="${not empty city or not empty state or not empty country }">
		<c:set var="includeLoc" value=" in ${city} ${state} ${country}"></c:set>
	</c:if>
	<c:choose>
		<c:when test="${not empty profName}">
			<c:choose>
				<c:when test="${profileLevel == 'INDIVIDUAL'}">
					<c:choose>
						<c:when test="${companyName == companyNameForTitle}">
							<title>${profName} ${title} ${companyNameForTitle} ${vertical} Professional Reviews</title>
						</c:when>
						<c:otherwise>
							<title>${profName} ${title} ${companyName} ${companyNameForTitle} ${vertical} Professional Reviews</title>
						</c:otherwise>
					</c:choose>
					<meta name="keywords"
						content="${profName}, ${title}, ${companyName}, ${location}, ${vertical}, professional, online, reputation, social, survey, reviews, rating">
					<meta name="description"
						content="${firstName} has ${reviewsCount} reviews. ${firstName} is a ${vertical} professional${includeLocation}.${includeTitle}">
				</c:when>
				<c:otherwise>
					<title>${profName} ${vertical} Reviews</title>
					<meta name="keywords"
						content="${profName}, ${vertical}, professional, online, reputation, social, survey, reviews, rating">
					<meta name="description"
						content="${profName} has ${reviewsCount} reviews. ${profName} is a ${vertical} company${includeLoc}.">
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<title><spring:message code="label.profile.title.key" /></title>
		</c:otherwise>
	</c:choose>
	<c:if test="${not empty profile.completeProfileUrl}">
		<link rel="canonical" href="${profile.completeProfileUrl}">
	</c:if>
	<c:if test="${not empty averageRating}">
		<fmt:formatNumber var="floatingAverageRating" type="number"
			value="${averageRating}" maxFractionDigits="2" minFractionDigits="2" />
		<fmt:formatNumber var="floatingAverageGoogleRating" type="number"
			value="${averageRating}" maxFractionDigits="2" minFractionDigits="2" />
		<fmt:formatNumber var="integerAverageRating" type="number"
			value="${averageRating}" maxFractionDigits="0" />
		<c:if test="${integerAverageRating == 6}">
			<c:set var="integerAverageRating" value="5"></c:set>
		</c:if>
		<%-- <c:if test="${integerAverageRating == 0}">
    			<c:set var="integerAverageRating" value="1"></c:set>
    		</c:if> --%>
	</c:if>
</c:if>
</head>
<body>
	<div id="overlay-main" class="overlay-main hide">
		<div id="overlay-pop-up" class="overlay-disable-wrapper">
			<div id="overlay-header" class="ol-header">
				<!-- Populated by javascript -->
			</div>
			<div class="ol-content">
				<div id="overlay-text" class="ol-txt">
					<!-- Populated by javascript -->
				</div>
				<div class="clearfix">
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-cancel" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-continue" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="toast-container" class="toast-container">
		<span id="overlay-toast" class="overlay-toast"></span>
	</div>
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
	
	<div id="report-abuse-overlay" class="overlay-main hide">
		<div id="report-abuse-pop-up" class="overlay-disable-wrapper">
			<div id="overlay-header" class="ol-header">
				<spring:message code="label.publicprofile.reportabuse.title.key" />
			</div>
			<div class="ol-content">
				<input type="text" id="report-abuse-cus-name"
					class="report-abuse-input" placeholder="Name"> <input
					type="email" id="report-abuse-cus-email" class="report-abuse-input"
					placeholder="Email Address">
				<textarea id="report-abuse-txtbox" class="report-abuse-txtbox"
					placeholder='<spring:message code="label.publicprofile.reportabuse.key"/>'></textarea>
			</div>
			<div class="rpa-overlay-btn-cont clearfix">
				<div class="rpa-btn rpa-report-btn ol-btn cursor-pointer float-left">
					<spring:message code="label.report.key" />
				</div>
				<div
					class="rpa-btn rpa-cancel-btn ol-btn cursor-pointer float-right">
					<spring:message code="label.cancel.key" />
				</div>
			</div>
		</div>
	</div>
	<div id="contact-us-pu-wrapper" class="bd-srv-pu hide">
		<div class="container cntct-us-container">
			<div class="contact-us-pu">
				<div class="bd-quest-item">
					<div class="bd-q-pu-header bd-q-pu-header-adj clearfix">
						<div class="float-left bd-q-pu-header-lft">
							<spring:message code="label.publicprofile.entermessage.key" />
						</div>
					</div>
					<div class="bd-q-pu-txt-wrapper pos-relative">
						<textarea class="bd-q-pu-txtarea"></textarea>
					</div>
				</div>
				<div class="cntct-us-btns-wrapper clearfix">
					<div class="bd-q-btn-cancel float-left">
						<spring:message code="label.cancel.key" />
					</div>
					<div class="bd-q-btn-done-pu float-left">
						<spring:message code="label.send.key" />
					</div>
				</div>
			</div>
		</div>
	</div>

	<input type="hidden" name="reviewsCount" value="${reviewsCount}">
	<input type="hidden" name="averageRatings" value="${averageRating}">
	<input type="hidden" value="${companyProfileName}"
		id="company-profile-name">
	<input type="hidden" value="${regionProfileName}"
		id="region-profile-name">
	<input type="hidden" value="${branchProfileName}"
		id="branch-profile-name">
	<input type="hidden" value="${agentProfileName}"
		id="agent-profile-name">
	<input type="hidden" id="profile-fetch-info" fetch-all-reviews="false"
		total-reviews="${reviewsCount}" profile-level="${profileLevel}" />
	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
			<div class="float-left hdr-links clearfix"></div>
			<div class="float-right clearfix hdr-btns-wrapper">
				<div class="float-left hdr-log-btn hdr-log-reg-btn">
					<spring:message code="label.signin.key" />
				</div>
				<!-- <div class="float-left hdr-reg-btn hdr-log-reg-btn">
					<spring:message code="label.joinus.key" />
				</div>  -->
			</div>
		</div>
	</div>


	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left padding-10 pub-page-hdr-title">
					<spring:message code="label.readwritesharereviews.key" />
				</div>
				<%-- <div class="float-right hm-find-pro-right clearfix">
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
						<!-- <input id="find-pro-start-index" name="find-pro-start-index" type="hidden" value="0">
					<input id="find-pro-row-size" name="find-pro-row-size" type="hidden" value="10"> --> 
					<input id="find-pro-profile-name" name="find-pro-profile-name" type="hidden" value="${findProCompanyProfileName}">
						<input id="find-pro-submit" type="button"
							class="float-left prof-submit-btn" value="Search">
					</form>
				</div> --%>
				<div class="float-right hm-find-pro-right clearfix srch-eng-pub-page-srch col-lg-7 col-md-7 col-sm-6 col-xs-12">
					<div class="srch-eng-pub-page-cont">
						<div class="srch-eng-pub-page-inp-cont">
							<div class="srch-eng-txt-lbl srch-eng-inp-container srch-eng-txt-lbl-bord srch-eng-pub-page-bg" style="color: #2b609b;">Find</div>
							<input id="srch-eng-pub-page-inp" type="text" placeholder="mortgage brokers, real estate agents,car rentals" class="srch-eng-cat-inp srch-eng-inp-container srch-eng-pub-page-bg">
						</div>
						<div id="srch-eng-pub-page-srch-btn" class="srch-eng-srch-img-cont cursor-pointer">
		    				<img src="/resources/images/search_white.png" alt="search" class="srch-eng-srch-img">
		    			</div>
    				</div>
				</div>
			</div>
		</div>
	</div>

	<div class="bread-crum-hdr">
		<div class="container">
			<div id="bread-crum-cont" class="row bread-crum-row"></div>
		</div>
	</div>

	<div id="profile-main-content"
		class="prof-main-content-wrapper margin-top-10 margin-bottom-25">
		<div itemscope itemtype="https://schema.org/ProfilePage" class="">
			<div class="container">
				<div class="row prof-pic-name-wrapper">
					<c:if
						test="${not empty profile.profileImageUrl && not empty fn:trim(profile.profileImageUrl)}">
						<div id="prog-img-container"
							class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper">
							<div class="prog-img-container" style="background-color: white !important; display: flex;align-items: center;">
								<img itemprop="primaryImageOfPage" id="prof-image"
									class="prof-image pos-relative height-auto-resp"
									src="${profile.profileImageUrl}"></img>
							</div>
						</div>
					</c:if>
					<c:if
						test="${not empty profile.profileImageUrlThumbnail && not empty fn:trim(profile.profileImageUrlThumbnail)}">
						<c:set var="profileNameClass" value="profile-name-img-wrapper"></c:set>
					</c:if>
					<div
						class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper ${profileNameClass}">
						<div class="prof-name-container" id="prof-company-head-content">
							<div itemprop="about" itemscope=""
								itemtype="http://schema.org/Person">
								<div itemprop="name" class="prof-name">${profName}</div>
								<div class="prof-address">
									<c:if
										test="${not empty profile.contact_details &&  not empty profile.contact_details.title}">
										<div itemprop="jobTitle" class="prof-addline2">${profile.contact_details.title}</div>
									</c:if>
									<div class="prof-addline1">
										<c:if
											test="${not empty profile.contact_details && not empty profile.contact_details.location}">
	                				${profile.contact_details.location}
	                				<c:set var="isLocationTrue" value="yes"></c:set>
										</c:if>
										<c:if test="${not empty profile.vertical}">
											<c:if test="${isLocationTrue == 'yes'}"> | </c:if>
		                			${profile.vertical}
		                		</c:if>
									</div>
								</div>
							</div>
							<c:choose>
								<c:when test="${reviewsCount > 0 }">
									<div itemprop="aggregateRating" itemscope
										itemtype="http://schema.org/AggregateRating"
										class="prof-rating clearfix">
										<div class="prof-rating-wrapper maring-0 clearfix float-left"
											id="rating-avg-comp">
											<div
												class='rating-image float-left smiley-rat-${integerAverageRating}'></div>
											<div class='rating-rounded float-left'
												data-score="${floatingAverageRating}">
												<span itemprop="ratingValue">${floatingAverageRating}</span>
												-
											</div>
										</div>
										<div class="float-left review-count-left cursor-pointer"
											id="prof-company-review-count">
											<span itemprop="reviewCount">${reviewsCount}</span> Review(s)
										</div>
									</div>
								</c:when>
								<c:otherwise>
									<div class="prof-rating clearfix">
										<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
										<div class="float-left review-count-left cursor-pointer"
											id="prof-company-review-count">
											<span>${reviewsCount}</span> Reviews(s)
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
								<c:if test="${not empty profile && !profile.hiddenSection}">
									<c:choose>
										<c:when test="${profileLevel == 'INDIVIDUAL'}">
											<c:choose>
												<c:when test="${not empty profile.surveyUrl}">
													<a href="${profile.surveyUrl}" target="_blank"><span
														class="prof-btn-survey float-left"
														id="read-write-share-btn">Write a Review</span></a>
												</c:when>
												<c:otherwise>
													<a href="/rest/survey/showsurveypage/${profile.iden}"
														target="_blank"><span
														class="prof-btn-survey float-left"
														id="read-write-share-btn">Write a Review</span></a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<a
												href="/initfindapro.do?profileLevel=${profileLevel}&iden=${profile.iden}&searchCriteria=${profile.contact_details.name}"
												target="_blank"><span class="prof-btn-survey float-left"
												id="read-write-share-btn">Write a Review</span></a>
										</c:otherwise>
									</c:choose>
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
						<div class="prof-user-address" id="prof-company-address">
							<!-- address comes here -->
						</div>
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
								<c:choose>
									<c:when test="${profileLevel == 'INDIVIDUAL'}">
										<a href="/rest/survey/showsurveypage/${profile.iden}"
											target="_blank"><span
											class="mob-prof-contact-btn float-left" id="mob-review-btn">Write
												a Review</span></a>
									</c:when>
									<c:otherwise>
										<a
											href="/initfindapro.do?profileLevel=${profileLevel}&iden=${profile.iden}&searchCriteria=${profile.contact_details.name}"
											target="_blank"><span
											class="mob-prof-contact-btn float-left" id="mob-review-btn">Write
												a Review</span></a>
									</c:otherwise>
								</c:choose>
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
						<div id="social-token-container"
							class="float-right hm-hr-row-right pp-social-icn clearfix hide">
							<c:if test="${not empty profile.socialMediaTokens}">
								<div id="social-connect-txt"
									class="float-left social-connect-txt">Connect with
									${profName }:</div>
								<c:if
									test="${not empty profile.socialMediaTokens.facebookToken && not empty profile.socialMediaTokens.facebookToken.facebookPageLink}">
									<div id="icn-fb" class="float-left social-item-icon icn-fb"
										data-link="${profile.socialMediaTokens.facebookToken.facebookPageLink}"
										title="Facebook"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.instagramToken && not empty profile.socialMediaTokens.instagramToken.pageLink}">
									<div id="icn-fb" class="float-left social-item-icon icn-insta"
										data-link="${profile.socialMediaTokens.instagramToken.pageLink}"
										title="Instagram"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.twitterToken && not empty profile.socialMediaTokens.twitterToken.twitterPageLink}">
									<div id="icn-twit" class="float-left social-item-icon icn-twit"
										data-link="${profile.socialMediaTokens.twitterToken.twitterPageLink}"
										title="Twitter"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.linkedInToken && not empty profile.socialMediaTokens.linkedInToken.linkedInPageLink}">
									<div id="icn-lin" class="float-left social-item-icon icn-lin"
										data-link="${profile.socialMediaTokens.linkedInToken.linkedInPageLink}"
										title="LinkedIn"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.googleBusinessToken && not empty profile.socialMediaTokens.googleBusinessToken.googleBusinessLink}">
									<div id="icn-google-business" class="float-left social-item-icon icn-google-business"
										data-link="${profile.socialMediaTokens.googleBusinessToken.googleBusinessLink}"
										title="Google Business Rate &amp; Review Link"></div>
								</c:if>				
								<c:if
									test="${not empty profile.socialMediaTokens.googleToken && not empty profile.socialMediaTokens.googleToken.profileLink}">
									<div id="icn-gplus"
										class="float-left social-item-icon icn-gplus"
										data-link="${profile.socialMediaTokens.googleToken.profileLink}"
										title="Google+"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.yelpToken && not empty profile.socialMediaTokens.yelpToken.yelpPageLink}">
									<div id="icn-yelp" class="float-left social-item-icon icn-yelp"
										data-link="${profile.socialMediaTokens.yelpToken.yelpPageLink}"
										title="Yelp"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.zillowToken && not empty profile.socialMediaTokens.zillowToken.zillowProfileLink}">
									<div id="icn-zillow"
										class="float-left social-item-icon icn-zillow"
										data-link="${profile.socialMediaTokens.zillowToken.zillowProfileLink}"
										title="Zillow"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.lendingTreeToken && not empty profile.socialMediaTokens.lendingTreeToken.lendingTreeProfileLink}">
									<div id="icn-lendingtree"
										class="float-left social-item-icon icn-lendingtree"
										data-link="${profile.socialMediaTokens.lendingTreeToken.lendingTreeProfileLink}"
										title="LendingTree"></div>
								</c:if>
								<c:if
									test="${not empty profile.socialMediaTokens.realtorToken && not empty profile.socialMediaTokens.realtorToken.realtorProfileLink}">
									<div id="icn-realtor"
										class="float-left social-item-icon icn-realtor"
										data-link="${profile.socialMediaTokens.realtorToken.realtorProfileLink}"
										title="Realtor"></div>
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

						<!-- <div class="prof-left-row prof-left-info bord-bot-dc">
                    <div class="left-contact-wrapper">
                        <div class="left-panel-header cursor-pointer vcard-download">Download VCard</div>
                    </div>
                </div> -->
						<c:if test="${not empty profile.contact_details }">
							<c:if
								test="${(not empty profile.contact_details.web_addresses && not empty profile.contact_details.web_addresses.work) || (not empty profile.contact_details.contact_numbers && not empty profile.contact_details.contact_numbers.work)}">
								<div id="contact-info"
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
														class="float-left lp-con-row-item pp-lp-con-row-item blue-text web-address-link"
														data-link="${profile.contact_details.web_addresses.work}">
													</div>
												</div>
											</c:if>
											<c:if
												test="${not empty profile.contact_details.contact_numbers && not empty profile.contact_details.contact_numbers.work}">
												<div class="lp-con-row lp-row clearfix">
													<a
														href="tel:${profile.contact_details.contact_numbers.work}">
														<div class="float-left lp-con-icn icn-phone"></div>
														<div class="float-left lp-con-row-item pp-lp-con-row-item">${profile.contact_details.contact_numbers.work}</div>
													</a>
												</div>
											</c:if>
										</div>
									</div>
								</div>
							</c:if>
						</c:if>

						<div id="prof-agent-container">
							<div id="individual-details">
								<!-- individual details like associations/hobbies/achievements come here -->
							</div>
							<c:if test="${not empty profile && !profile.hiddenSection}">
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
									<c:when test="${not empty companyProfileName}">
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
								</c:choose>
							</c:if>

							
								<div class="prof-left-row prof-left-assoc bord-bot-dc">
									<div class="left-contact-wrapper">
										<c:if test="${not empty workEmail }">
										<div id="prof-contact-hdr"
											class="left-panel-header prof-contact-hdr">Contact
											${profName}</div>
										<div class="left-panel-content">
											<form id="prof-contact-form">
												<div class="lp-row">
													<div class="lp-input-cont">
														<div class="float-left lp-username-icn lp-input-icn"></div>
														<input id="lp-input-name" type="text" class="lp-input"
															placeholder='<spring:message code="label.publicprofile.nameplaceholder.key"/>'>
													</div>
												</div>
												<div class="lp-row">
													<div class="lp-input-cont lp-email">
														<div class="float-left lp-email-icn lp-input-icn"></div>
														<input id="lp-input-email" type="email" class="lp-input"
															placeholder='<spring:message code="label.emailid.key"/>'>
													</div>
												</div>
												<div class="lp-row">
													<div class="lp-input-cont lp-textarea-cont">
														<div class="float-left lp-textarea-icn lp-input-icn"></div>
														<textarea id="lp-input-message" class="lp-input"
															placeholder='<spring:message code="label.publicprofile.contactus.textplaceholder.key"/>'></textarea>
													</div>
												</div>
												<div class="lp-row">
													<div class="prof-captcha-cont">
														<div class="g-recaptcha profile-captcha"
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
													<div class="lp-button">
														<spring:message
															code="label.publicprofile.submitmessage.key" />
													</div>
												</div>
												<div class="privacy-policy-disclaimer">
													<spring:message
														code="label.publicprofile.privacydisclaimer.key" />
												</div>
												<div id="privacy-policy-link" class="privacy-policy-link">
													<a
														href="https://www.socialsurvey.com/privacy-policy/">Privacy
														Policy</a>
												</div>
											</form>
										</div>
										</c:if>
										<c:if test="${not empty profile.disclaimer }">
											<div class="prof-left-ach">
												<div class="left-ach-wrapper">
													<div class="left-panel-content">
														<div class="lp-disclaimer-row lp-row">${profile.disclaimer}</div>
													</div>
												</div>
											</div>
										</c:if>
									</div>
								</div>
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
												<c:if test="${reviewsCount > 0}">
													<span class="capitalize">${profName}</span> has ${reviewsCount} reviews.
										</c:if>
												<c:if test="${not empty  vertical && not empty location}">
													<span class="capitalize">${profName}</span> is a ${vertical} professional in ${location}.
										</c:if>
												<c:if test="${not empty title}">
													<span class="capitalize">${profName}</span> is the ${title} of ${companyName}.
										</c:if>
											</div>
										</c:when>
										
										<c:when test="${profileLevel == 'BRANCH'}">
											<div class="intro-body">
												<c:if test="${reviewsCount > 0}">
													<span class="capitalize">${companyProfileData.name} ${profName}</span> has ${reviewsCount} reviews.
										</c:if>
												<c:if test="${not empty  vertical}">
													<span class="capitalize">${profName}</span> is a office for the ${vertical} company ${companyName}.
										</c:if>
											</div>
										</c:when>
										<c:when test="${profileLevel == 'REGION'}">
											<div class="intro-body">
												<c:if test="${reviewsCount > 0}">
													<span class="capitalize">${companyProfileData.name} ${profName}</span> has ${reviewsCount} reviews.
										</c:if>
												<c:if test="${not empty  vertical }">
													<span class="capitalize">${profName}</span> is a office for the ${vertical} company ${companyName}.
										</c:if>
											</div>
										</c:when>
										<c:otherwise>
											<div class="intro-body">
												<c:if test="${reviewsCount > 0}">
													<span class="capitalize">${profName}</span> has ${reviewsCount} reviews.
                                        </c:if>
												<c:if test="${not empty country && not empty vertical}">
													<span class="capitalize">${profName}</span> is a ${vertical} company in ${city} ${state} ${country}.
                    					</c:if>
											</div>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</div>
						<%-- <div class="rt-content-main bord-bot-dc clearfix" id="recent-post-container" style="display: none;">
                    <div class="float-left panel-tweet-wrapper">
                        <div class="main-con-header"><spring:message code="label.publicprofile.recentpost.key"/></div>
                        <div class="tweet-panel tweet-panel-left tweet-panel-left-adj" id="prof-posts">
                            <!--  latest posts get populated here -->
                        </div>
                    </div>
                </div> --%>
						<div class="people-say-wrapper rt-content-main"
							id="reviews-container" style="display: none;">
							<div class="clearfix hide"
								style="display: block; border-bottom: 1px solid #dcdcdc; padding: 15px 0;">
								<div class="main-con-header float-left" id="prof-reviews-header">
									<span class="ppl-say-txt-st"><spring:message
											code="label.peoplesayabout.key" /></span> <span
										class="review-for-text-sm-screen"><spring:message
											code="label.reviewsfor.key" /></span> &nbsp;${profName }
								</div>
								<div id="prof-reviews-sort"
									class="prof-reviews-sort clearfix float-right">
									<div id="sort-by-feature"
										class="prof-review-sort-link float-left">
										<spring:message code="label.sortbyfeature.key" />
									</div>
									<div class="prof-reviews-sort-divider float-left">|</div>
									<div id="sort-by-date"
										class="prof-review-sort-link float-right">
										<spring:message code="label.sortbydate.key" />
									</div>
								</div>
							</div>

							<div id="prof-review-item" class="prof-reviews">
								<!--  reviews get populated here -->
							</div>
							<div id="prof-hidden-review-count"
								class="prof-hidden-review-link" data-nr-review-count="0">
								<!--  count of hidden reviews get populated here -->
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="mobile-tabs hide clearfix">
		<div class="float-left mob-icn mob-icn-active icn-person"></div>
		<!-- <div class="float-left mob-icn icn-ppl"></div> -->
		<div class="float-left mob-icn icn-star-smile"></div>
		<div class="float-left mob-icn inc-more"></div>
	</div>
	<script
		src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/date.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/profile.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/search-engine.js"></script>
	<script
		src="${initParam.resourcesPath}/resources/js/perfect-scrollbar.jquery.min.js"></script>
	<script src='//www.google.com/recaptcha/api.js' async="async"
		defer="defer"></script>
	<script>
		var hiddenSection = "${profile.hiddenSection}";
		var reviewsSortBy = "${reviewSortCriteria}";
		var pageUrl = "${pageUrl}";
		var showAllReviews = false;
		var avgRating = '${floatingAverageRating}';
		
		$(document).ready(function() {
						
			if( '${popup}' == "true" && !$('body').hasClass("overflow-hidden-important") ){
			 	$('body').addClass("overflow-hidden-important");
			 	
			 	// post icons
				buildReviewPopupShareData();
			}
			
			paintAvgRatingForPpf(avgRating);
			
			 $('body').on('click','#dismiss-single-review-popup',function(e){
				 
				 // just to make sure the page is scrollable after the dissmissal of pop-up 
				 $('#single-review-page').addClass('hide');
				 if( $('body').hasClass("overflow-hidden-important") ){
				 	$('body').removeClass("overflow-hidden-important");
				 }
				 
				 if( pageUrl != undefined && pageUrl != "" ){
					 window.location.href = pageUrl;
				 }
			 });	 
			 
			 $('#single-review-contact-btn').on('click', function(e){
				 e.stopPropagation();
				 if( $(this).data('contact-link') != "" ){
					 window.location.href = $(this).data('contact-link');
				 }
			 })
			 
			setUpPopupDismissListeners();
			
			if ($('#social-token-container').children('.social-item-icon').length == 0) {
				$('#social-token-container').remove();
			} else {
				$('#social-token-container').show();
			}

			if ( reviewsSortBy  != "feature" ){
				showAllReviews = true;
				$("#profile-fetch-info").attr("fetch-all-reviews","true");
			}
			
			profileJson = ${profileJson};
			
			var gaLabel;
			var gaName;
			/**
				If region profile name is mentioned, fetch the region profile 
				since this would be a call to fetch region profile page 
			 */
			var regionProfileName = $("#region-profile-name").val();
			var branchProfileName = $("#branch-profile-name").val();
			var agentProfileName = $("#agent-profile-name").val();
			if (regionProfileName.length > 0) {
				fetchRegionProfile();
				gaLabel = 'region';
				gaName = regionProfileName;
			} else if (branchProfileName.length > 0) {
				fetchBranchProfile();
				gaLabel = 'office';
				gaName = branchProfileName;
			} else if (agentProfileName.length > 0) {
				fetchAgentProfile();
				gaLabel = 'individual';
				gaName = agentProfileName;
			} else {
				fetchCompanyProfile();
				gaLabel = 'company';
				gaName = companyProfileName;
			}
			
			setUpReviewPopupListener();
			
			$(window).resize(adjustImageForPublicProfile);

			//update google analytics
			updateGoogleTrackingId();

			// Google analytics for reviews
			setTimeout(function() {
				ga('send', {
					'hitType' : 'event',
					'eventCategory' : 'review',
					'eventAction' : 'click',
					'eventLabel' : gaLabel,
					'eventValue' : gaName
				});
			}, 5000);
			
			if( window.location.hash != undefined ){
				$('body').scrollTop($(window.location.hash).offset().top);
			}
			
		});
	</script>
</body>
</html>