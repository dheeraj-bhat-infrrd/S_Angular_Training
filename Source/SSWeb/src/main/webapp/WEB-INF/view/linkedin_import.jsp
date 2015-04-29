<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profile}">
	<c:set value="${profile.profilesMaster.profileId}" var="profilemasterid"></c:set>
</c:if>

<c:if test="${user.numOfLogins == 0 && profilemasterid == 4}">
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
</c:if>