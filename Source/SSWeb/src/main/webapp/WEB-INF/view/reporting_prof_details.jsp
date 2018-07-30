<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.vertical}" var="companyvertical"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
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

<meta property="isAutoLogin" content="${isAutoLogin}" />
<meta property="allowOverrideForSocialMedia" content="${allowOverrideForSocialMedia}" />
<c:choose>
	<c:when test="${isAutoLogin == 'true' && allowOverrideForSocialMedia == 'false' }">
		<c:set var="isAutoLogin" value="true"></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="isAutoLogin" value="false"></c:set>
	</c:otherwise>
</c:choose>

<c:if test="${not empty cannonicalusersettings && not empty cannonicalusersettings.companySettings && not empty cannonicalusersettings.companySettings.vertical}">
	<c:set value="${cannonicalusersettings.companySettings.vertical}" var="verticalVal"></c:set>
</c:if>

<div id="prof-message-header" class="hide"></div>
<c:if test="${(highestrole != 1 && highestrole != 2 && highestrole != 3)}">
<div id="rep-user-details" class="col-sm-6 col-lg-6 col-md-6 col-xs-6 rep-user-details">
	<div id="rep-rank-prof-pic" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-rank-prof-pic">
		<div class="rep-rank-rect" style="display:inline-grid;">
			<div id="rep-rank" style="display: -webkit-box;display:inline-flex; margin-top: 5px;">
				<span>Rank#</span>
				<span id="rank-span" style="font-size: 22px;font-weight: bold !important;margin-left: 8px;line-height: 15px;">NA</span><span id="rank-count"></span>
			</div>
			<div id="rep-user-score" style="display: -webkit-box;display:inline-flex;">
				<span>Userscore</span>
				<span id="user-score-span" style="font-size: 18px;font-weight: bold !important;margin-left: 8px;line-height: 15px;">NA</span>
			</div>
		</div>
		<div class="rep-prof-pic-circle rep-prof-circle-user">
			<%@ include file="reporting_profileimage.jsp" %>
		</div>
	</div>
	<div id="rep-user-info" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-user-info" >
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="name" class="prof-name prof-name-txt rep-dsh-large-text dsh-txt-1">${contactdetail.name}</div>
		</div>
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="designation-nmls" class="prof-addline2 prof-name-txt rep-dsh-medium-text dsh-txt-2" >${contactdetail.title}
					| ${verticalVal}
			</div>
		</div>
		<div id="prof-rating-review-count" class="prof-rating clearfix rep-prof-rating">
			<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
			<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
		</div>
	</div>
</div>
</c:if>

<c:if test="${(highestrole == 1 || highestrole == 2 || highestrole == 3)}">
<div id="rep-user-details" class="col-sm-6 col-lg-6 col-md-6 col-xs-6 rep-user-details" >
	<div id="rep-rank-prof-pic" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-rank-prof-pic">
		<div class="rep-prof-pic-circle">
			<jsp:include page="reporting_profileimage.jsp"></jsp:include>
		</div>
	</div>
	<div id="rep-user-info" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-user-info">
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="name" class="prof-name prof-name-txt rep-dsh-large-text dsh-txt-1">${contactdetail.name}</div>
		</div>
		<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
			<div id="designation-nmls" class="prof-addline2 prof-name-txt rep-dsh-medium-text dsh-txt-2" >${contactdetail.title}
					| ${verticalVal}
			</div>
		</div>
		<div id="prof-rating-review-count" class="prof-rating clearfix rep-prof-rating">
			<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
			<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
		</div>
	</div>
</div>
</c:if>

<div id="rep-dash-btns" class="col-sm-6 col-lg-6 col-md-6 col-xs-6 rep-dash-btns">
	<div id="rep-send-survey" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-send-survey-div" >
		<span>Build your online reputation by surveying each and every customer</span>
		<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix rep-dash-btn-wrapper" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}" style="">
			<div id="dsh-btn1" class="dsh-btn-complete float-left rep-dash-btn "><spring:message code="label.sendsurvey.btn.key" /></div>
		</div>	
	</div>
	<div id="rep-fix-social-media" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-social-media-div hide">
		<button id="rep-dismiss-fix-social-media" type="button" class="close" style="position: absolute; top: 0; right: 0; margin-right: 5px;">&times;</button>
		<span>Reconnect to your social media accounts.</span>
		<div id="rep-pro-fix-cmplt-stars" class="dsh-star-wrapper clearfix rep-dash-btn-wrapper" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}">
			<div id="dsh-btn0" class="dsh-btn-complete dsh-btn-red float-left rep-dash-btn">Reconnect</div>
		</div>
	</div>
	<div id="rep-social-media" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-social-media-div hide">
		<button id="rep-dismiss-social-media" type="button" class="close" style="position: absolute; top: 0; right: 0; margin-right: 5px;">&times;</button>
		<span>Extend your social reach by connecting to all your social media accounts.</span>
		<div id="rep-pro-cmplt-stars" class="dsh-star-wrapper clearfix rep-dash-btn-wrapper" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}">
			<div id="dsh-btn2" class="dsh-btn-complete dsh-btn-orange float-left hide rep-dash-btn"></div>
		</div>
	</div>
	<div id="empty-rep-social-media" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 rep-social-media-div hide" style="line-height: 40px;">
		<span>You have successfully connected to all your social media accounts.</span>
	</div>
</div>