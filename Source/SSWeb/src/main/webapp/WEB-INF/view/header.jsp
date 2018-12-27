<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!-- in highest roles comparison, 1=companyAdmin, 2=regionAdmin, 3=branchAdmin, 4=agent, 5=no profile  -->
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<!-- Account masters 1=Individual, 2=Team, 3=Company, 4=Enterprise, 5=Free Account -->
<c:set var="accountMasterId" value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}"/>
<c:set var="hiddenSectionDashboard" value="${hiddenSection}"/>
<c:set var="isRealTechOrSSAdmin" value="${isRealTechOrSSAdmin}"></c:set>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta charset="utf-8">
	<title><spring:message code="label.login.title.key" /></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/datepicker3.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/jcrop/jquery.Jcrop.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/intlTelInput.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/spectrum.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/fontselector.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/prettify.css">
	<link rel="stylesheet" href="${initParam.widgetResourcesPath}/widget/css/widget.css">
</head>
<body>
	<div id="overlay-linkedin-import" class="overlay-login overlay-main hide"></div>
	<div id="overlay-send-survey" class="overlay-login overlay-main hide"></div>
	<div id="srv-req-pop" class="survey-request-popup-container hide">
		<div class="survey-request-popup"></div>
	</div>
	<div id="overlay-incomplete-survey"
		class="overlay-login overlay-main hide">
		<div id="incomplete-survey-popup" class="welcome-popup-wrapper">
			<div class="welcome-popup-hdr-wrapper clearfix">
				<div class="float-left wc-hdr-txt">
					<spring:message code="label.header.incompletesurvey.key" />
				</div>
				<div class="float-right popup-close-icn" onclick="hideIncompleteSurveyListPopup();"></div>
			</div>
			<div class="welcome-popup-body-wrapper clearfix icn-sur-popup-wrapper">
				<div id="icn-sur-popup-cont" data-start="0" data-total="0" data-batch="5" class="icn-sur-popup-cont"></div>
				<div class="mult-sur-icn-wrapper">
					<div id="resend-mult-sur-icn" class="mult-sur-icn resend-mult-sur-icn float-left" title="Resend"></div>
					<%-- <div id="del-mult-sur-icn" class="mult-sur-icn del-mult-sur-icn float-right" title="Delete"></div> --%>
				</div>
			</div>
			<div class="paginate-buttons-survey clearfix">
				<div id="sur-previous" class="float-left sur-paginate-btn">&lt; Prev</div>
				<div class="paginate-sel-box float-left">
					<input id="sel-page" type="text" pattern="[0-9]*" class="sel-page"/>
					<span class="paginate-divider">/</span>
					<span id="paginate-total-pages" class="paginate-total-pages"></span>
				</div>
				<div id="sur-next" class="float-right sur-paginate-btn">Next &gt;</div>
			</div>
		</div>
	</div>
	<div id="overlay-edit-positions" class="overlay-login overlay-main hide">
	</div>
	<div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
    <div class="overlay-payment hide" id="outer-payment"></div>
    <div class="overlay-loader hide"></div>
    <div id="message-header" class="hide"></div>
    
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
	
	<div id="zillow-popup" class="overlay-main hide">
		<div id="zillow-popup-body"  class="welcome-popup-wrapper zillow-popup-wrapper"> <!--  class="overlay-disable-wrapper overlay-disable-wrapper-zillow container login-container"> -->
		</div>
	</div>
	
	
	<div id="overlay-main-survey" class="overlay-main-survey hide">
		<div id="overlay-pop-up" class="overlay-disable-wrapper">
			<div id="overlay-header-survey" class="ol-header">
				<!-- Populated by javascript -->
			</div>
			<div class="ol-content">
				<div id="overlay-text-survey" class="ol-txt">
					<!-- Populated by javascript -->
				</div>
				<div class="clearfix">
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-continue-survey" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-cancel-survey" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="email-map-pop-up" class="bd-srv-email hide">
	<input type="hidden" id="current-user-id">
	<div class="container bd-q-container">
	<div id="email-overlay">
		<div id="user-email" class="bd-q-wrapper">
		<div class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width:40%" class="float-left unmatchtab unhr-row">Email</div>
						<div style="width:15%" class="float-left unmatchtab unhr-row">Status</div>
						<div style="width:25%" class="float-left unmatchtab unhr-row">Created</div>
						<div style="width:15%" class="float-left unmatchtab unhr-row">Action</div>
		</div>
			<div id="mapped-emil-info" style="border: 1px solid #dcdcdc;"></div>
			<div id="input-email" class="hide">
			<form method="post" id="email-form" style="border:1px solid #dcdcdc;">
			<div class="email-map-add-txt">Add email(s)</div>
			
			<div id="new-email-wrapper">
				 <span style="padding:5px">Enter another email-id </span><input type="email" name="email1" class="email-input-txt"
					id="email1" placeholder="someone@example.com"/> <br /> 
			</div>
			</form>
			</div>
			<div class="email-map-wrapper clearfix">
			 <div id="email-map-save" class="float-left hide">Save</div>
			    <div id="email-map-add" class="float-left">Map new email</div>
				<div id="email-map-cancel" class="float-left" style="margin-left:10px;"><spring:message code="label.cancel.key" /></div>
			</div>
		</div>
		</div>
	</div>
</div> 
	
	
	<div id="report-abuse-overlay" class="overlay-main hide">
    	<div id="report-abuse-pop-up" class="overlay-disable-wrapper">
    		<div id="overlay-header" class="ol-header">Why do you want to report the review?</div>
    		<div class="ol-content">
    			<textarea id="report-abuse-txtbox" class="report-abuse-txtbox" placeholder="Type here on why do you want to report the review...."></textarea>
    		</div>
    		<div class="rpa-overlay-btn-cont clearfix">
    			<div class="rpa-btn rpa-report-btn ol-btn cursor-pointer float-left"><spring:message code="label.report.key"/></div>
    			<div class="rpa-btn rpa-cancel-btn ol-btn cursor-pointer float-right"><spring:message code="label.cancel.key"/></div>
    		</div>
    	</div>
    </div>
	
	<div id="header-slider-wrapper" class="header-slider-wrapper">
		<div class="header-slider">
			<div id="header-links-slider" class="header-links header-links-slider float-left clearfix">
				<div class="header-links-item" onclick="showMainContent('./showreportingpage.do')"><spring:message code="label.header.dashboard.key" /></div>
				<div class="header-links-item" onclick="javascript:showMainContent('./dashboard.do')"><spring:message code="label.header.legacy.dashboard.key" /></div>
				<div class="header-links-item" onclick="showMainContent('./showreportspage.do')"><spring:message code="label.reporting.key" /></div>
				<c:if test="${(accountMasterId == 2 || accountMasterId == 3 || accountMasterId == 4) && (highestrole == 1 || highestrole == 2 || highestrole == 3)}">
					<div class="header-links-item" onclick="showMainContent('./showbuildhierarchypage.do')"><spring:message code="label.header.buildhierarchy.key" /></div>
				</c:if>
				<c:if test="${highestrole == 1 && accountMasterId != 5}">
					<div class="header-links-item" onclick="showMainContent('./showbuildsurveypage.do');"><spring:message code="label.header.buildsurvey.key" /></div>
				</c:if>
				<c:if test="${accountMasterId != 5}">
				<c:if test="${hiddenSectionDashboard && highestrole != 4 }">
					<div class="header-links-item" onclick="showMainContent('./showcompanysettings.do');"><spring:message code="label.editsettings.key" /></div>
				</c:if>
				<c:if test="${!hiddenSectionDashboard}">
					<div class="header-links-item" onclick="showMainContent('./showcompanysettings.do');"><spring:message code="label.editsettings.key" /></div>
				</c:if>
				</c:if>
				<c:if test="${highestrole == 1}">
					<div class="header-links-item" onclick="showMainContent('./showcomplaintressettings.do')"><spring:message code="label.complaintregsettings.key" /></div>
				</c:if>
				<!-- show apps for company admin other then free account -->
				<c:if test="${highestrole == 1 && accountMasterId != 5}">
					<div class="header-links-item" onclick="showMainContent('./showemailsettings.do')"><spring:message code="label.emailsettings.key" /></div>
				</c:if>
				<!-- show apps for company admin other then individual/free account -->
				<c:if test="${accountMasterId > 1 && accountMasterId != 5}">
				<c:if test="${hiddenSectionDashboard && highestrole != 4 }">
					<div class="header-links-item" onclick="showMainContent('./showapps.do')"><spring:message code="label.appsettings.key" /></div>
				</c:if>
				<c:if test="${!hiddenSectionDashboard}">
					<div class="header-links-item" onclick="showMainContent('./showapps.do')"><spring:message code="label.appsettings.key" /></div>
				</c:if>
				<c:if test="${ highestrole == 1 }">
					<div class="header-links-item" onclick="javascript:showMainContent('./showrankingsettings.do')"><spring:message code="label.ranking.settings.key" /></div>
				</c:if>
				<c:if test="${not empty realTechAdminId}">
					<div id="vndsta-setting-one" class="header-links-item hide" onclick="showMainContent('./showlistingsmanagersettings.do')"><spring:message code="label.vendastaproductsettings.key" /></div>
				</c:if>
				<c:if test="${!hiddenSectionDashboard}">
					<div class="header-links-item" onclick="showMainContent('./showwidget.do');"><spring:message code="label.showwidget.key" /></div>
					<div class="header-links-item" onclick="showMainContent('./shownewwidget.do');"><spring:message code="label.shownewwidget.key" /></div>
				</c:if>
				<c:if test="${hiddenSectionDashboard && highestrole != 4 }">
					<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showwidget.do');">
						<spring:message code="label.showwidget.key" />
					</div>
					<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./shownewwidget.do');">
						<spring:message code="label.shownewwidget.key" />
					</div>
				</c:if>
				</c:if>
				<c:if test="${accountMasterId > 1 && accountMasterId <5 && highestrole != 4}">
					<div class="header-links-item" onclick="showMainContent('./viewhierarchy.do');"><spring:message code="label.viewcompanyhierachy.key" /></div>
				</c:if>
				<c:if test="${accountMasterId > 1 && accountMasterId <5 && highestrole != 4}">
					<div class="header-links-item" onclick="javascript:showMainContent('./showusermangementpage.do')"><spring:message code="label.header.usermanagement.key" /></div>
				</c:if>
				<c:if test="${accountMasterId < 4 }">	
					<div class="header-links-item" onclick="showMainContent('./upgradepage.do')"><spring:message code="label.header.upgrade.key" /></div>
				</c:if>
				<%-- <c:if test="${ highestrole == 1 }">
					<div class="header-links-item" onclick="showMainContent('./showsocialmonitortpage.do')"><spring:message code="label.socialmonitor.key" /></div>
					<div id="listings-manager-slider" class="hide">
					<div class="header-links-item" onclick="showMainContent('./showlistingsmanagerpage.do')"><spring:message code="label.listingsmanager.key" /></div>
					</div>					
				</c:if> --%>
				<c:if test="${isSocialMonitorEnabled == true}">
					<c:if test="${isSocialMonitorAdmin == true}">
						<div class="header-links-item" onclick="showMainContent('./showsocialmonitorstreampage.do')"><spring:message code="label.social.monitor.key" /></div>
					</c:if>
				</c:if>
				<c:if test="${hiddenSectionDashboard && highestrole != 4}">
				<div class="header-links-item" onclick="showMainContent('./showprofilepage.do')"><spring:message code="label.editprofile.key" /></div>
				</c:if>
				<c:if test="${!hiddenSectionDashboard}">
				<div class="header-links-item" onclick="showMainContent('./showprofilepage.do')"><spring:message code="label.editprofile.key" /></div>
				</c:if>
				<div class="header-links-item" onclick="showMainContent('./showchangepasswordpage.do')"><spring:message code="label.changepassword.key"/></div>
				<div class="header-links-item" onclick="showMainContent('./showhelppage.do')"><spring:message code="label.help.key"/></div>
				<c:choose>
					<c:when test="${not empty realTechAdminId}">
						<div class="header-links-item" onclick="userSwitchToAdmin();"><spring:message code="label.switch.key" /></div>
					</c:when>
					<c:when test="${not empty companyAdminSwitchId || not empty regionAdminSwitchId || not empty branchAdminSwitchId}">
						<div class="header-links-item" onclick="userSwitchToCompAdmin();"><spring:message code="label.switch.key" /></div>
					</c:when>
				</c:choose>
				<a href="j_spring_security_logout"><span class="header-links-item" ><spring:message code="label.logout.key" />
					</span>
				</a>
			</div>
		</div>
	</div>

	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo cursor-pointer"></div>
			<div class="float-left hdr-links clearfix">
				<div id="hdr-dashboard-item" class="hdr-link-item hdr-link-active hdr-link-item hdr-link-item-config pos-relative">
					<a id="dashboard-link" href="javascript:showMainContent('./showreportingpage.do')" onclick="showOverlay();"><spring:message code="label.header.dashboard.key" /></a>
					<div id="hdr-dashboard-dropdown" class="hdr-link-item-dropdown-icn"></div>
						<div id="hdr-link-item-dropdown-dash" class="hdr-link-item-dropdown hide">
							<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showreportingpage.do');">
								<spring:message code="label.header.dashboard.key" />
							</div>
							<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./dashboard.do');">
								<spring:message code="label.header.legacy.dashboard.key" />
							</div>
							<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showreportspage.do');">
								<spring:message code="label.reporting.key" />
							</div>
						</div>
				</div>
<!-- 				<div class="hdr-link-item hdr-link-active"> -->
<%-- 					<a id="dashboard-link" href="javascript:showMainContent('./dashboard.do')" onclick="showOverlay();"><spring:message code="label.header.dashboard.key" /></a> --%>
<!-- 				</div> -->
				<c:if test="${accountMasterId > 1 && accountMasterId < 5 && highestrole != 4}">
					<div class="hdr-link-item">
						<a href="javascript:showMainContent('./showusermangementpage.do')" onclick="showOverlay();"><spring:message code="label.header.usermanagement.key" /></a>
					</div>
				</c:if>
				<c:if test="${highestrole == 1 && accountMasterId != 5}">
					<div class="hdr-link-item">
						<a href="javascript:showMainContent('./showbuildsurveypage.do')" onclick="showOverlay();"><spring:message code="label.header.buildsurvey.key" /></a>
					</div>
				</c:if>
				<c:if test="${accountMasterId != 5 }">
				<c:if test="${hiddenSectionDashboard && highestrole!=4}">
					<div id="hdr-link-item-config" class="hdr-link-item hdr-link-item-config pos-relative">
						<a href="javascript:showMainContent('./showcompanysettings.do')" onclick="showOverlay();"><spring:message code="label.configure.key" /></a>
						<div id="hdr-config-settings-dropdown" class="hdr-link-item-dropdown-icn"></div>
						<div id="hdr-link-item-dropdown" class="hdr-link-item-dropdown hide">
							<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showcompanysettings.do');">
								<spring:message code="label.settings.key" />
							</div>
							<c:if test="${highestrole == 1}">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showcomplaintressettings.do');">
									<spring:message code="label.complaintregsettings.key" />
								</div>
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showemailsettings.do');">
									<spring:message code="label.emailsettings.key" />
								</div>
							</c:if>
							<c:if test="true">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showwidget.do');">
									<spring:message code="label.showwidget.key" />
								</div>
							</c:if>
							<c:if test="true">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./shownewwidget.do');">
									<spring:message code="label.shownewwidget.key" />
								</div>
							</c:if>
							<c:if test="${accountMasterId > 1 && accountMasterId != 5}">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showapps.do');">
									<spring:message code="label.appsettings.key" />
								</div>
							</c:if>
							<c:if test="${ highestrole == 1 }">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showrankingsettings.do');">
									<spring:message code="label.ranking.settings.key" />
								</div>
							</c:if>
							<c:if test="${not empty realTechAdminId}">
								<div id="vndsta-setting-two" class="hdr-link-item-dropdown-item hide" onclick="showMainContent('./showlistingsmanagersettings.do');">
									<spring:message code="label.vendastaproductsettings.key" />
								</div>
							</c:if>
						</div>
					</div>
					</c:if>
					<c:if test="${!hiddenSectionDashboard}">
					<div id="hdr-link-item-config" class="hdr-link-item hdr-link-item-config pos-relative">
						<a href="javascript:showMainContent('./showcompanysettings.do')" onclick="showOverlay();"><spring:message code="label.configure.key" /></a>
						<div id="hdr-config-settings-dropdown" class="hdr-link-item-dropdown-icn"></div>
						<div id="hdr-link-item-dropdown" class="hdr-link-item-dropdown hide">
							<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showcompanysettings.do');">
								<spring:message code="label.settings.key" />
							</div>
							<c:if test="${highestrole == 1}">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showcomplaintressettings.do');">
									<spring:message code="label.complaintregsettings.key" />
								</div>
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showemailsettings.do');">
									<spring:message code="label.emailsettings.key" />
								</div>
							</c:if>
							<c:if test="true">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showwidget.do');">
									<spring:message code="label.showwidget.key" />
								</div>
							</c:if>
							<c:if test="true">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./shownewwidget.do');">
									<spring:message code="label.shownewwidget.key" />
								</div>
							</c:if>
							<c:if test="${accountMasterId > 1 && accountMasterId != 5}">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showapps.do');">
									<spring:message code="label.appsettings.key" />
								</div>
							</c:if>
							<c:if test="${highestrole == 1}">
								<div class="hdr-link-item-dropdown-item" onclick="showMainContent('./showrankingsettings.do');">
									<spring:message code="label.ranking.settings.key" />
								</div>
							</c:if>
							<c:if test="${not empty realTechAdminId}">
								<div id="vndsta-setting-three" class="hdr-link-item-dropdown-item hide" onclick="showMainContent('./showlistingsmanagersettings.do');">
									<spring:message code="label.vendastaproductsettings.key" />
								</div>
							</c:if>
						</div>
					</div>
					</c:if>
				</c:if>
				<%-- <c:if test="${ highestrole == 1 }">
					<div id="hdr-link-item-sm" class="hdr-link-item hdr-link-item-sm pos-relative">
						<a href="javascript:showMainContent('./showsocialmonitortpage.do')" onclick="showOverlay();"><spring:message code="label.socialmonitor.key" /></a>
						<div id="listings-manager-main" class="hide">
							<div id="hdr-sm-settings-dropdown" class="hdr-link-item-dropdown-icn-sm"></div>
							<div id="hdr-link-item-dropdown-sm" class="hdr-link-item-dropdown-sm hide">
								<div class="hdr-link-item-dropdown-item-sm" onclick="showMainContent('./showsocialmonitortpage.do');">
									<spring:message code="label.socialmonitor.key" />
								</div>
								<div class="hdr-link-item-dropdown-item-sm" onclick="showMainContent('./showlistingsmanagerpage.do');">
									<spring:message code="label.listingsmanager.key" />
								</div>
							</div>
						</div>
					</div>
				</c:if> --%>
				<c:if test="${isSocialMonitorEnabled == true}">
					<c:if test="${isSocialMonitorAdmin == true}">
						<div class="hdr-link-item">
							<a href="javascript:showMainContent('./showsocialmonitorstreampage.do')" onclick="showOverlay();"><spring:message code="label.social.monitor.key" /></a>
						</div>
					</c:if>
				</c:if>
				<c:if test="${hiddenSectionDashboard && highestrole != 4 }">
				<div class="hdr-link-item">
					<a href="javascript:showMainContent('./showprofilepage.do')" onclick="showOverlay();"><spring:message code="label.editprofile.key" /></a>
				</div>
				</c:if>
				<c:if test="${!hiddenSectionDashboard}">
				<div class="hdr-link-item">
					<a href="javascript:showMainContent('./showprofilepage.do')" onclick="showOverlay();"><spring:message code="label.editprofile.key" /></a>
				</div>
				</c:if>
				
				<div class="hdr-link-item">
					<a href="javascript:showMainContent('./showhelppage.do')" onclick="showOverlay();"><spring:message code="label.help.key" /></a>
				</div>
			</div>
			<div id="header-menu-icn" class="header-menu-icn icn-menu hide float-right"></div>
			<div id="header-user-info" class="header-user-info float-right clearfix">
				<div id="hdr-usr-img" class="float-right user-info-initial">
					<span id="usr-initl">${fn:substring(user.firstName, 0, 1)}</span>
					<div class="initial-dd-wrapper hide blue-arrow-bot text-normal">
						<div class="initial-dd-item" id="change-password" onclick="showMainContent('./showchangepasswordpage.do'); showOverlay();">
							<spring:message code="label.changepassword.key"/>
						</div>
						<c:if test="${accountMasterId < 4}">
							<c:if test="${billingMode == 'A'}">	
								<div class="initial-dd-item" id="upgrade-plan" onclick="showMainContent('./upgradepage.do')">
									<spring:message	code="label.header.upgrade.key" />
								</div>
							</c:if>
						</c:if>
						<c:if test="${accountMasterId == 5}">
							<div class="initial-dd-item" id="upgrade-plan" onclick="showMainContent('./upgradetopaidplanpage.do')">
								<spring:message	code="label.header.upgrade.key" />
							</div>
						</c:if>
						<c:choose>
							<c:when test="${not empty realTechAdminId }">
								<div class="initial-dd-item" onclick="userSwitchToAdmin();">
									<spring:message code="label.switch.key" />
								</div>
							</c:when>
							<c:when test="${not empty companyAdminSwitchId }">
								<div class="initial-dd-item" onclick="userSwitchToCompAdmin();">
									<spring:message code="label.switch.key" />
								</div>
							</c:when>
							<c:when test="${not empty regionAdminSwitchId }">
								<div class="initial-dd-item" onclick="userSwitchToCompAdmin();">
									<spring:message code="label.switch.key" />
								</div>
							</c:when>
							<c:when test="${not empty branchAdminSwitchId }">
								<div class="initial-dd-item" onclick="userSwitchToCompAdmin();">
									<spring:message code="label.switch.key" />
								</div>
							</c:when>
						</c:choose>
						<div class="initial-dd-item" id="user-logout" onclick="userLogout();">
							<spring:message code="label.logout.key" />
						</div>
					</div>
				</div>
                <c:if test="${displaylogo != null}">
					<div class="float-left user-info-seperator"></div>
					<div class="float-left user-info-logo"
						style="background: url(${displaylogo}) no-repeat center; background-size: contain;"></div>
				</c:if>
			</div>
		</div>
	</div>
<script>var hiddenSection="${hiddenSectionDashboard}";</script>