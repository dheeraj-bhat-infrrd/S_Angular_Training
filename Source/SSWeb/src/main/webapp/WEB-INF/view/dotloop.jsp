<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="clearfix um-panel-content">
	<div class="row">
		<div class="um-top-row cleafix">
			<div class="clearfix um-top-form-wrapper">
				<!-- set encompass details -->
				<c:if
					test="${appSettings != null && appSettings.crm_info != null && appSettings.crm_info.crm_source == 'DOTLOOP'}">
					<c:set var="dotloopapi" value="${appSettings.crm_info.api}" />
				</c:if>
				<form id="dotloop-form">
					<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 um-panel-item">
						<div class="hm-item-dl clearfix float-left">
							<div class="um-item-row-left text-right">API</div>
							<div class="clearfix float-right st-username-icons">
								<div class="um-item-row-icon margin-left-0"></div>
								<div class="um-item-row-icon margin-left-0"></div>
							</div>
							<div
								class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
								<input id="dotloop-apikey" type="text"
									class="um-item-row-txt um-item-row-txt-OR" placeholder="Api"
									name="dotloop-api" value="${dotloopapi}">
								<div id="dotloop-api" class="hm-item-err-2"></div>
							</div>
							<div class="clearfix float-left st-url-icons">
								<div id="dotloop-testconnection"
									class="encompass-testconnection-adj um-item-row-icon icn-spanner margin-left-0 cursor-pointer"></div>
								<div id="dotloop-save"
									class="um-item-row-icon icn-blue-tick margin-left-0 cursor-pointer"></div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<hr />
	<h4>About dotloop Closing Survey</h4>

	<p>With your business plus account you can utilize the API feature.
		This features is useful for brokerages that wish to streamline their
		processes with SocialSurvey. dotloop's api allows you the ability to
		push data generated in dotloop into SocialSurvey. This feature must
		first be activated by your success manager at dotloop so make sure to
		reach out to your contact if you wish to integrate this feature into
		your account.</p>

	<p>You can access the api key from the my account sections on your
		dashboard + profile, this key can be used by the other system’s
		development team to pull data out of dotloop and put it into
		SocialSurvey. SocialSurvey cannot push data into dotloop via our api.</p>

	<p>
		<strong>Learn more at:</strong> <a
			href="https://support.dotloop.com/hc/en-us/articles/204405303-API-SSO-and-Branding"
			target="_blank">https://support.dotloop.com/hc/en-us/articles/204405303-API-SSO-and-Branding</a>
	</p>

	<p>
		<i><strong>Note:</strong> SocialSurvey will not send a survey
			until you move the loop into Closed status.</i>
	</p>

	<iframe src="//player.vimeo.com/video/116064341" width="500"
		height="281" frameborder="0" allowfullscreen=""></iframe>
</div>