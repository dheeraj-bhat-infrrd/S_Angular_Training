<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<!-- in highest roles comparison, 1=companyAdmin, 2=regionAdmin, 3=branchAdmin, 4=agent, 5=no profile  -->

<c:set var="accountMasterId" value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}"/>
<!-- Account masters 1=Individual, 2=Team, 3=Company, 4=Enterprise, 5=Free Account -->

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.login.title.key" /></title>
	<link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/perfect-scrollbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/perfect-scrollbar.min.css">
</head>
<body>
	<c:if test="${user.numOfLogins == 1}">
		<div class="overlay-login overlay-main">
			
			<!-- Fetch data from linkedIn -->
			<div id="welocome-step1" class="welcome-popup-wrapper">
				<div class="welcome-popup-hdr-wrapper clearfix">
					<div class="float-left wc-hdr-txt"><spring:message code="label.linkedin.connect.key" /></div>
					<div class="float-right wc-hdr-step"><spring:message code="label.step.one.key" /></div>
				</div>
				<div class="welcome-popup-body-wrapper clearfix">
					<div class="wc-popup-body-hdr"><spring:message code="label.linkedin.import.key" /></div>
					<div class="wc-popup-body-cont">
						<div class="linkedin-img"></div>
						<div class="wc-connect-txt">
							<spring:message code="label.linkedin.profile.key" /><br />
							<spring:message code="label.linkedin.savetime.key" />
						</div>
						<div class="wl-import-btn" onclick="openAuthPageRegistration('linkedin');">
							<spring:message code="label.linkedin.import.button.key" />
						</div>
						<div class="wc-connect-txt">
							<spring:message code="label.linkedin.noaccount.key" /><br />
							<spring:message code="label.linkedin.also.key" />
							<span class="txt-highlight"><spring:message code="label.linkedin.manually.key" /></span>
						</div>
					</div>
				</div>
				<div class="wc-btn-row clearfix" data-page="one">
					<div class="wc-btn-col float-left">
						<div class="wc-skip-btn float-right"><spring:message code="label.skipthisstep.key" /></div>
					</div>
					<div class="wc-btn-col float-left">
						<div class="wc-sub-btn float-left wc-next-btn"><spring:message code="label.nextstep.key" /></div>
					</div>
				</div>
			</div>
			
			<!-- View/Edit data from linkedIn -->
			<div id="welocome-step2" class="welcome-popup-wrapper hide">
				<!-- populated by javascript -->
			</div>
			
			<!-- Authorize social profiles -->
			<div id="welocome-step3" class="welcome-popup-wrapper hide">
				<div class="welcome-popup-hdr-wrapper clearfix">
					<div class="float-left wc-hdr-txt"><spring:message code="label.socialaccounts.key" /></div>
					<div class="float-right wc-hdr-step"><spring:message code="label.step.three.key" /></div>
				</div>
				<div class="welcome-popup-body-wrapper clearfix">
					<div class="wc-popup-body-hdr"><spring:message code="label.sharehappyreviews.key" /></div>
					<div class="wc-popup-body-cont wc-step3-body-cont">
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn float-left i-fb" onclick="openAuthPage('facebook');"></div>
							<div class="wc-icn-txt float-left">www.facebook.com/scott-harris</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn float-left i-twt" onclick="openAuthPage('twitter');"></div>
							<div class="wc-icn-txt float-left">www.twitter.com/scott-harris</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn float-left i-ln" onclick="openAuthPage('linkedin');"></div>
							<div class="wc-icn-txt float-left">www.linkedin.com/scott-harris</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn float-left i-gplus" onclick="openAuthPage('google');"></div>
							<div class="wc-icn-txt float-left">www.googleplus.com/scott-harris</div>
						</div>
						<!-- <div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-rss float-left"></div>
							<div class="wc-icn-txt float-left">blogs.scott-harris.com</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-yelp float-left"></div>
							<div class="wc-icn-txt float-left">www.Yelp.com/scott-harris</div>
						</div> -->
					</div>
				</div>
				<div class="wc-btn-row clearfix" data-page="three">
					<div class="wc-btn-col float-left">
						<div class="wc-skip-btn float-right wc-final-skip"><spring:message code="label.skip.key" /></div>
					</div>
					<div class="wc-btn-col float-left">
						<div class="wc-sub-btn float-left wc-final-submit"><spring:message code="label.done.key" /></div>
					</div>
				</div>
			</div>
			
		</div>
	</c:if>
	
	<c:if test="${user.numOfLogins > 1}">
		<div class="overlay-login overlay-main">
			<div class="welcome-popup-wrapper">
				<div class="welcome-popup-hdr-wrapper clearfix">
					<div class="float-left wc-hdr-txt">Send Survey Request</div>
				</div>
				<div class="welcome-popup-body-wrapper clearfix">
					<div class="wc-popup-body-hdr">Connect so we can share your happy customer reviews</div>
					<div class="wc-popup-body-cont">
						<div class="wc-review-table">
							<div class="wc-review-tr wc-review-hdr clearfix">
								<div class="wc-review-th1 float-left">First Name</div>
								<div class="wc-review-th2 float-left">Last Name</div>
								<div class="wc-review-th3 float-left">Email Address</div>
								<div class="wc-review-th4 float-left"></div>
							</div>
							
							<div class="wc-review-tr clearfix">
								<div class="wc-review-tc1 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc2 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc3 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc4 float-left">
									<div class="wc-review-rmv-icn"></div>
								</div>
							</div>
							
							<div class="wc-review-tr clearfix">
								<div class="wc-review-tc1 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc2 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc3 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc4 float-left">
									<div class="wc-review-rmv-icn"></div>
								</div>
							</div>
							
							<div class="wc-review-tr clearfix">
								<div class="wc-review-tc1 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc2 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc3 float-left">
									<input class="wc-review-input">
								</div>
								<div class="wc-review-tc4 float-left">
									<div class="wc-review-rmv-icn"></div>
								</div>
							</div>
							
						</div>
					</div>
				</div>
				<div class="wc-btn-row clearfix">
					<div class="wc-btn-col float-left">
						<div class="wc-skip-btn float-right wc-final-skip">Skip this step</div>
					</div>
					<div class="wc-btn-col float-left">
						<div class="wc-sub-btn float-left wc-final-submit">Send</div>
					</div>
				</div>
			</div>
		</div>
	</c:if>
	
	<div id="srv-req-pop" class="survey-request-popup-container hide">
		<div class="survey-request-popup"></div>
	</div>
	<div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
    <div class="overlay-payment hide" id="outer-payment"></div>
    <div class="overlay-loader hide"></div>
    <div id="message-header" class="hide"></div>
    
	<div id="overlay-main" class="overlay-main hide">
		<div class="overlay-disable-wrapper">
			<div id="overlay-header" class="ol-header">
				<!-- Populated by javascript -->
			</div>
			<div class="ol-content">
				<div id="overlay-text" class="ol-txt">
					<!-- Populated by javascript -->
				</div>
				<div class="clearfix">
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-continue" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-cancel" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="header-slider-wrapper" class="header-slider-wrapper">
		<div class="header-slider">
			<div id="header-links-slider" class="header-links header-links-slider float-left clearfix">
				<div class="header-links-item">
					<a id="dashboard-link" href="javascript:showMainContent('./dashboard.do')"><spring:message code="label.header.dashboard.key" /></a>
				</div>
				<c:if test="${(accountMasterId == 2 || accountMasterId == 3 || accountMasterId == 4) && (highestrole == 1 || highestrole == 2 || highestrole == 3)}">
					<div class="header-links-item">
						<a href="javascript:showMainContent('./showbuildhierarchypage.do')"><spring:message code="label.header.buildhierarchy.key" /></a>
					</div>
				</c:if>
				<c:if test="${accountMasterId != 5}">
					<div class="header-links-item">
						<a href="javascript:showMainContent('./showcompanysettings.do')"><spring:message code="label.editsettings.key" /></a>
					</div>
				</c:if>
				<c:if test="${accountMasterId > 1 && accountMasterId <5 && highestrole != 4}">
					<div class="header-links-item">
						<a href="javascript:showMainContent('./showusermangementpage.do')"><spring:message code="label.header.usermanagement.key" /></a>
					</div>
				</c:if>
				<c:if test="${accountMasterId < 4}">
					<div class="header-links-item">
						<a href="javascript:showMainContent('./upgradepage.do')"><spring:message code="label.header.upgrade.key" /></a>
					</div>
				</c:if>
				<div class="header-links-item">
					<a href="javascript:showMainContent('./showprofilepage.do')"><spring:message code="label.editprofile.key" /></a>
				</div>
				<div class="header-links-item" >
					<a href="javascript:showMainContent('./showchangepasswordpage.do')"><spring:message code="label.changepassword.key"/></a>
				</div>
				<div class="header-links-item" >
					<a href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
				</div>
			</div>
		</div>
	</div>

	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo cursor-pointer"></div>
			<div class="float-left hdr-links clearfix">
				<div class="hdr-link-item hdr-link-active">
					<a id="dashboard-link" href="javascript:showMainContent('./dashboard.do')" onclick="showOverlay();"><spring:message code="label.header.dashboard.key" /></a>
				</div>
				<c:if test="${(accountMasterId == 2 || accountMasterId == 3 || accountMasterId == 4) && (highestrole == 1 || highestrole == 2 || highestrole == 3)}">
					<div class="hdr-link-item">
						<a href="javascript:showMainContent('./showbuildhierarchypage.do')" onclick="showOverlay();"><spring:message code="label.header.buildhierarchy.key" /></a>
					</div>
				</c:if>
				<c:if test="${highestrole == 1 && accountMasterId != 5}">
					<div class="hdr-link-item">
						<a href="javascript:showMainContent('./showbuildsurveypage.do')" onclick="showOverlay();"><spring:message code="label.header.buildsurvey.key" /></a>
					</div>
				</c:if>
				<c:if test="${accountMasterId > 1 && accountMasterId < 5 && highestrole != 4 }">
					<div class="hdr-link-item">
						<a href="javascript:showMainContent('./showusermangementpage.do')" onclick="showOverlay();"><spring:message code="label.header.usermanagement.key" /></a>
					</div>
				</c:if>
			</div>
			<div id="header-user-info" class="header-user-info float-right clearfix">
				<div id="hdr-usr-img" class="float-left user-info-initial">
					<span id="usr-initl">${fn:substring(user.firstName, 0, 1)}</span>
					<div class="initial-dd-wrapper hide blue-arrow-bot text-normal">
						<c:if test="${accountMasterId != 5}">
							<div class="initial-dd-item" id="company-setting" onclick="showMainContent('./showcompanysettings.do'); showOverlay();">
								<spring:message code="label.editsettings.key" />
							</div>
						</c:if>
						<div class="initial-dd-item" id="profile-setting" onclick="showMainContent('./showprofilepage.do'); showOverlay();">
							<spring:message code="label.editprofile.key" />
						</div>
						<div class="initial-dd-item" id="change-password" onclick="showMainContent('./showchangepasswordpage.do'); showOverlay();">
							<spring:message code="label.changepassword.key"/>
						</div>
						<c:if test="${accountMasterId < 4}">
							<div class="initial-dd-item" id="upgrade-plan" onclick="showMainContent('./upgradepage.do')">
								<spring:message	code="label.header.upgrade.key" />
							</div>
						</c:if>
						<c:if test="${accountMasterId == 5}">
							<div class="initial-dd-item" id="upgrade-plan" onclick="showMainContent('./upgradetopaidplanpage.do')">
								<spring:message	code="label.header.upgrade.key" />
							</div>
						</c:if>
					</div>
				</div>
                <div class="float-left user-info-sing-out">
                    <a class="" href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
                </div>
				<c:if test="${displaylogo != null}">
					<div class="float-left user-info-seperator"></div>
					<div class="float-left user-info-logo"
						style="background: url(${displaylogo}) no-repeat center; background-size: contain;"></div>
				</c:if>
			</div>
			<div id="header-menu-icn" class="header-menu-icn icn-menu hide float-right"></div>
		</div>
	</div>
