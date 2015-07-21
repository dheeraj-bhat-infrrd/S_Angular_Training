<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Fetch data from linkedIn -->
<div id="welocome-step1" class="welcome-popup-wrapper" data-page="one">
	<div class="welcome-popup-hdr-wrapper clearfix">
		<div class="float-left wc-hdr-txt"><spring:message code="label.linkedin.connect.key" /></div>
		<div class="float-right popup-close-icn wc-skip-btn"></div>
		<%-- <div class="float-right wc-hdr-step"><spring:message code="label.step.one.key" /></div> --%>
	</div>
	<div class="welcome-popup-body-wrapper clearfix">
		<div class="wc-popup-body-hdr"><spring:message code="label.linkedin.import.key" /></div>
		<div class="wc-popup-body-cont">
			<div class="linkedin-img"></div>
			<div class="wc-connect-txt">
				<spring:message code="label.linkedin.profile.key" /><br />
				<spring:message code="label.linkedin.savetime.key" />
			</div>
			<div id="wl-import-btn" class="wl-import-btn" onclick="openAuthPageRegistration('linkedin');">
				<spring:message code="label.linkedin.import.button.key" />
			</div>
			<div id="wl-import-btn-msg" class="wl-import-btn hide">
				<spring:message code="label.linkedin.connected.key" />
			</div>
			<div id="wc-connect-link" class="wc-connect-txt wc-connect-link"></div>
			<div class="wc-connect-txt">
				<spring:message code="label.linkedin.noaccount.key" /><br />
				<spring:message code="label.linkedin.also.key" />
				<span class="txt-highlight"><spring:message code="label.linkedin.manually.key" /></span>
			</div>
		</div>
	</div>
	<div class="wc-btn-row clearfix">
		<div class="wc-btn-col float-left">
			<div class="wc-skip-btn float-right"><spring:message code="label.skipthisstep.key" /></div>
		</div>
		<div class="wc-btn-col float-left">
			<div class="wc-sub-btn float-left wc-next-btn"><spring:message code="label.nextstep.key" /></div>
		</div>
	</div>
</div>

<!-- View/Edit data from linkedIn -->
<div id="welocome-step2" class="welcome-popup-wrapper hide"  data-page="two">
	<!-- populated by javascript -->
</div>

<!-- Authorize social profiles -->
<div id="welocome-step3" class="welcome-popup-wrapper hide" data-page="three">
	<div class="welcome-popup-hdr-wrapper clearfix">
		<div class="float-left wc-hdr-txt"><spring:message code="label.socialaccounts.key" /></div>
		<div class="float-right popup-close-icn wc-skip-btn wc-final-skip"></div>
		<%-- <div class="float-right wc-hdr-step"><spring:message code="label.step.three.key" /></div> --%>
	</div>
	<div class="welcome-popup-body-wrapper clearfix">
		<div class="wc-popup-body-hdr"><spring:message code="label.sharehappyreviews.key" /></div>
		<div id="wc-step3-body-cont" class="wc-popup-body-cont wc-step3-body-cont">
			<jsp:include page="linkedin_import_social_links.jsp"></jsp:include>
		</div>
	</div>
	<div class="wc-btn-row clearfix">
		<div class="wc-btn-col float-left">
			<div class="wc-skip-btn float-right wc-final-skip"><spring:message code="label.skip.key" /></div>
		</div>
		<div class="wc-btn-col float-left">
			<div class="wc-sub-btn float-left wc-final-skip wc-final-submit"><spring:message code="label.done.key" /></div>
		</div>
	</div>
</div>