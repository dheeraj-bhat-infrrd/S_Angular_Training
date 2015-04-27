<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->

<c:set var="accountMasterId" value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}"/>
<!-- Account masters 1=Individual, 2=Team, 3=Company,4=Enterprise,5=Free Account -->

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
	<c:if test="${user.numOfLogins == 1 }">
		<div class="overlay-login overlay-main">
			<div id="welocome-step1" class="welcome-popup-wrapper">
				<div class="welcome-popup-hdr-wrapper clearfix">
					<div class="float-left wc-hdr-txt">Connect to Linkedin</div>
					<div class="float-right wc-hdr-step">Step 1</div>
				</div>
				<div class="welcome-popup-body-wrapper clearfix">
					<div class="wc-popup-body-hdr">Left's Import your profile data to get you setup quickly</div>
					<div class="wc-popup-body-cont">
						<div class="linkedin-img"></div>
						<div class="wc-connect-txt">
							There's no need to enter your profile manually if you have a
							Linkedin account.<br />
							We'll import your data to save you some time
						</div>
						<div class="wl-import-btn">Import From Linkedin</div>
						<div class="wc-connect-txt">
							Don't have account?<br />
							You can also <span class="txt-highlight">manually enter your profile information</span>
						</div>
					</div>
				</div>
				<div class="wc-btn-row clearfix">
					<div class="wc-btn-col float-left">
						<div class="wc-skip-btn float-right">Skip this Step</div>
					</div>
					<div class="wc-btn-col float-left">
						<div class="wc-sub-btn float-left wc-next-btn">Next</div>
					</div>
				</div>
			</div>
			<div id="welocome-step2" class="welcome-popup-wrapper hide">
				<div class="welcome-popup-hdr-wrapper clearfix">
					<div class="float-left wc-hdr-txt">Tell us about You and Your Business</div>
					<div class="float-right wc-hdr-step">Step 2</div>
				</div>
				<div class="welcome-popup-body-wrapper clearfix">
					<div class="wc-popup-body-hdr">Data Imported From Linkedin
					<div class="float-right linkedin-import-hdr"></div>
					</div>
					<div class="wc-popup-body-cont">
						<div class="wc-step2-body-row">
							<div class="wc-step2-body-row-hdr">Upload/Edit Your Photo</div>
							<div class="wc-step2-body-row-cont">
								<div class="wc-edit-photo-cont clearfix">
									<div class="wc-edit-photo-cont-col float-left">
										<div class="float-right">
											<div class="wc-linkedin-photo"></div>
											<div class="wc-linkedin-photo-txt">Photo imported from <span class="wc-highlight">Linkedin</span></div>
										</div>
									</div>
									<div class="wc-div-txt float-left">(Or)</div>
									<div class="wc-edit-photo-cont-col float-left">
										<div class="float-left">
											<div class="wc-photo-upload"></div>
											<div class="wc-submit-btn">Upload</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="wc-step2-body-row">
							<div class="wc-step2-body-row-hdr">Business Name, Address and Logo</div>
							<div class="wc-step2-body-row-cont">
								<div class="wc-form-container">
									<div class="wc-form-row clearfix">
										<div class="float-left wc-form-txt">Company</div>
										<div class="float-left wc-form-input-cont">
											<input class="wc-form-input" id="com-company" data-non-empty="true"
								name="company" value="${companyName}" placeholder='<spring:message code="label.company.key"/>'>
										</div>
									</div>
									<div class="wc-form-row clearfix">
										<div class="float-left wc-form-txt">Logo</div>
										<div class="float-left wc-form-input-cont">
											<input class="wc-form-input" placeholder='<spring:message code="label.logo.placeholder.key"/>'>
											<input type="file" class="rfr_txt_fld com-logo-comp-info" id="com-logo" name="logo">
											<div class="float-right input-icon-internal icn-file file-pick-logo file-pick-logo-adj"></div>
										</div>
									</div>
									<div class="wc-form-row clearfix">
										<div class="float-left wc-form-txt">Address</div>
										<div class="float-left wc-form-input-cont">
											<input class="wc-form-input" id="com-address1" data-non-empty="true"
								name="address1" value="${address1}" placeholder='<spring:message code="label.address1.key"/>'>
										</div>
										<div class="float-left wc-form-input-cont">
											<input class="wc-form-input" id="com-address2"
								name="address2" value="${address2}" placeholder='<spring:message code="label.address2.key"/>'>
										</div>
									</div>
									<div class="wc-form-row clearfix">
										<div class="float-left wc-form-txt"></div>
										<div class="float-left wc-form-input-cont">
											<input class="wc-form-input" id="com-country" data-non-empty="true"
								name="country" value="${country}" placeholder='<spring:message code="label.country.key"/>'>
										</div>
										<div class="float-left wc-form-input-cont">
											<input class="wc-form-input" id="com-zipcode" data-non-empty="true" data-zipcode="true"
								name="zipcode" value="${zipCode}" placeholder='<spring:message code="label.zipcode.key"/>'>
										</div>
									</div>
									<div class="wc-form-row clearfix">
										<div class="float-left wc-form-txt">Phone No</div>
										<div class="float-left wc-form-input-cont">
											<input class="wc-form-input" id="com-contactno" data-non-empty="true" data-phone="true"
								name="contactno" value="${companyContactNo}" placeholder="<spring:message code="label.phoneno.key" />">
										</div>
									</div>
									<div class="wc-form-row clearfix">
										<div class="float-left wc-form-txt">Business Type</div>
										<div class="float-left wc-form-input-cont">
											<select name="vertical" id="select-vertical"
												class="rfr_txt_fld">
												<option disabled selected>Select a business
													type</option>
												<option id="vertical-1">MORTGAGE</option>
											</select>
										</div>
									</div>
									<div class="wc-form-row clearfix">
										<div class="reg_btn">Update</div>
									</div>
								</div>
							</div>
						</div>
						<div class="wc-step2-body-row">
							<div class="wc-step2-body-row-hdr">Tell us about yourself</div>
							<div class="wc-step2-body-row-cont">
								<div class="wc-prof-details">
									<div class="wc-prof-hdr">About Scott Haris</div>
									<div class="wc-prof-details-row clearfix">
										<div class="wc-prof-input-cont float-left">
											<input class="wc-form-input">
										</div>
										<div class="wc-prof-input-cont float-left">
											<input class="wc-form-input">
										</div>
										<div class="wc-linkedin-photo-txt float-right">Photo imported from <span class="wc-highlight">Linkedin</span></div>
									</div>
									<div class="wc-prof-details-row clearfix">
										<textarea class="wc-about-prof-txt"></textarea>
									</div>
									
									<div class="wc-prof-details-row clearfix">
										<div class="wc-submit-btn float-right">Update</div>
									</div>
									
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="wc-btn-row clearfix">
					<div class="wc-btn-col float-left">
						<div class="wc-skip-btn float-right">Skip this Step</div>
					</div>
					<div class="wc-btn-col float-left">
						<div class="wc-sub-btn float-left wc-next-btn">Next</div>
					</div>
				</div>
			</div>
			<div id="welocome-step3" class="welcome-popup-wrapper hide">
				<div class="welcome-popup-hdr-wrapper clearfix">
					<div class="float-left wc-hdr-txt">Connect to Your Social Accounts</div>
					<div class="float-right wc-hdr-step">Step 3</div>
				</div>
				<div class="welcome-popup-body-wrapper clearfix">
					<div class="wc-popup-body-hdr">Connect so we can share your happy customer views</div>
					<div class="wc-popup-body-cont wc-step3-body-cont">
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-fb float-left"></div>
							<div class="wc-icn-txt float-left">www.facebook.com/scott-harris</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-twt float-left"></div>
							<div class="wc-icn-txt float-left">www.twitter.com/scott-harris</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-ln float-left"></div>
							<div class="wc-icn-txt float-left">www.linkedin.com/scott-harris</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-gplus float-left"></div>
							<div class="wc-icn-txt float-left">www.googleplus.com/scott-harris</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-rss float-left"></div>
							<div class="wc-icn-txt float-left">blogs.scott-harris.com</div>
						</div>
						<div class="wc-social-icn-row clearfix">
							<div class="wc-social-icn i-yelp float-left"></div>
							<div class="wc-icn-txt float-left">www.Yelp.com/scott-harris</div>
						</div>
					</div>
				</div>
				<div class="wc-btn-row clearfix">
					<div class="wc-btn-col float-left">
						<div class="wc-skip-btn float-right wc-final-skip">Skip</div>
					</div>
					<div class="wc-btn-col float-left">
						<div class="wc-sub-btn float-left wc-final-submit">Done</div>
					</div>
				</div>
			</div>
		</div>
	</c:if>
	
	<c:if test="${user.numOfLogins > 1 }">
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
