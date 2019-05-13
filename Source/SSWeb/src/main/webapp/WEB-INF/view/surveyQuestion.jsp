<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<c:choose>
	<c:when test="${isAutoLogin == 'true' && allowOverrideForSocialMedia == 'false' }">
		<c:set var="socialDisabled" value="social-auth-disabled"></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="isAutoLogin" value="false"></c:set>
	</c:otherwise>
</c:choose>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.survey.title.key" /></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
</head>

<body>
<input  type="hidden" data-edited=false>
	<div id="toast-container" class="toast-container">
		<span id="overlay-toast" class="overlay-toast"></span>
	</div>
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

	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
			<div id="header-user-info" class="header-user-info float-right clearfix sur-com-logo">
				<c:if test="${companyLogo != null}">
					<div class="float-left user-info-seperator"></div>
					<div class="float-left user-info-logo" style="background: url(${companyLogo}) no-repeat center; background-size: contain;"></div>
				</c:if>
			</div>
		</div>
	</div>
	<div id="prof-container" data-q="${q}" data-agentid="${agentId}" data-agentName="${agentName}" data-agent-email="${agentEmail}"
	data-last-name="${lastName}" data-first-name="${firstName}" data-copy-to-clipboard=""
		 class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
		 <div id="survey-dash" style="display:none"></div>
		<div class="container">
			<div class="sq-ques-wrapper" data-edited=false>
				<div id="agnt-img" class="sq-top-img"></div>
				<div data-ques-type="stars" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt">Survey Question</div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="ques-text" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper">
						<div id="sq-stars" class="sq-star-wrapper clearfix" selected-star-no="">
							<div star-no="1" class="sq-star opacity-red"></div>
							<div star-no="2" class="sq-star opacity-red"></div>
							<div star-no="3" class="sq-star opacity-red"></div>
							<div star-no="4" class="sq-star opacity-red"></div>
							<div star-no="5" class="sq-star opacity-red"></div>
						</div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-star" class="float-left sq-np-item sq-np-item-prev btn-com"><spring:message code="label.prev.btn.key"/></div>
						<div id="next-star" class="float-left sq-np-item sq-np-item-next btn-com"><spring:message code="label.nxt.btn.key"/></div>
					</div>
				</div>
				<div data-ques-type="smiley" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt"></div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="ques-text-smiley" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div id="sq-smiles" class="sq-rat-wrapper" selected-smiles-no="">
						<div class="sq-star-wrapper clearfix">
							<div smile-no="1" class="sq-smile sq-smile-1 opacity-red"></div>
							<div smile-no="2" class="sq-smile sq-smile-2 opacity-red"></div>
							<div smile-no="3" class="sq-smile sq-smile-3 opacity-red"></div>
							<div smile-no="4" class="sq-smile sq-smile-4 opacity-red"></div>
							<div smile-no="5" class="sq-smile sq-smile-5 opacity-red"></div>
						</div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-smile" class="float-left sq-np-item sq-np-item-prev btn-com"><spring:message code="label.prev.btn.key"/></div>
						<div id="next-smile" class="float-left sq-np-item sq-np-item-next btn-com"><spring:message code="label.nxt.btn.key"/></div>
					</div>
				</div>

				<!-- Div for rating questions of  scale  -->
				<div data-ques-type="scale" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt"></div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="ques-text-scale" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper mgn-bot-40">
						<div class="sq-slider-wrapper pts-wrap-adj clearfix">
							<div class="sq-pts-item-hover pts-hover-1 pts-arr-bot"><spring:message code="label.reponse.poor.key"/></div>
							<div class="sq-pts-item-hover pts-hover-2 pts-arr-bot"><spring:message code="label.reponse.notbad.key"/></div>
							<div class="sq-pts-item-hover pts-hover-3 pts-arr-bot"><spring:message code="label.reponse.good.key"/></div>
							<div class="sq-pts-item-hover pts-hover-4 pts-arr-bot"><spring:message code="label.reponse.vgood.key"/></div>
							<div class="sq-pts-item-hover pts-hover-5 pts-arr-bot"><spring:message code="label.reponse.excellent.key"/></div>
						</div>
						<div class="sq-slider-wrapper clearfix">
							<div value="1" class="sq-pts-item sq-pts-red">1</div>
							<div value="2" class="sq-pts-item sq-pts-org">2</div>
							<div value="3" class="sq-pts-item sq-pts-lgreen">3</div>
							<div value="4" class="sq-pts-item sq-pts-military">4</div>
							<div value="5" class="sq-pts-item sq-pts-dgreen">5</div>
						</div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-scale" class="float-left sq-np-item sq-np-item-prev btn-com"><spring:message code="label.prev.btn.key"/></div>
						<div id="next-scale" class="float-left sq-np-item sq-np-item-next btn-com"><spring:message code="label.nxt.btn.key"/></div>
					</div>
				</div>

				<!-- Div for 1-10 rating questions -->
				<div data-ques-type="sb-range-0to10" class="sq-quest-item hide">
					<div class="sq-main-txt"></div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="ques-text-1to10" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div id="sq-radio-1to10" class="sq-1to10-range" selected-rating-radio="">
						<div class="radio-container">
							<div class="container-1to10 inner-container-1to10">
								<c:forEach begin="0" end="10" varStatus="loop">
    								<%-- <div class="radio-1to10">
  										<input type="radio" name="sb-range-0to10"><label></label>
									</div> --%>
									<c:choose>
										<c:when test="${loop.index == 0}">
											<div class="radio-div radio-div-first-1to10">
												<div class="radio-outer-gray sq-radio" id="radio-${loop.index}">
													<div class="radio-inner hide"></div>
												</div>
											</div>
										</c:when>
										<c:otherwise>
											<div class="radio-div radio-div-1to10">
												<div class="radio-outer-gray sq-radio" id="radio-${loop.index}">
													<div class="radio-inner hide"></div>
												</div>
											</div>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</div>
							<div class="container-1to10">
								<c:forEach begin="0" end="10" varStatus="numLoop">
									<div class="span-1to10">${numLoop.index}</div>
								</c:forEach>
							</div>
							<div class="sq-1to10-range-val">
                                <div id="notAtAllLikelyDiv" style="float:left">Not at all likely</div>
    							<div id="veryLikelyDiv" style="float:right;">Very Likely</div>
							</div>
						</div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-radio" class="float-left sq-np-item sq-np-item-prev btn-com"><spring:message code="label.prev.btn.key"/></div>
						<div id="next-radio" class="float-left sq-np-item sq-np-item-next btn-com"><spring:message code="label.nxt.btn.key"/></div>
					</div>
				</div>
				
				<div data-ques-type="sb-range-0to10-nps" class="sq-quest-item hide">
					<div class="sq-main-txt"></div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="ques-text-1to10-nps" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div id="sq-radio-1to10-nps" class="sq-1to10-range-nps" selected-rating-radio="">
						<div class="radio-container">
							<div id="nps-range-text" class="sq-1to10-range-val">
                                <div id="notAtAllLikelyDivNps" class="radio-range-text" style="float:left">Not at all likely</div>
    							<div id="veryLikelyDivNps" class="radio-range-text" style="float:right;">Very Likely</div>
							</div>
							<div class="container-1to10 inner-container-1to10-nps">
								<c:forEach begin="0" end="10" varStatus="loop">
    								<%-- <div class="radio-1to10">
  										<input type="radio" name="sb-range-0to10"><label></label>
									</div> --%>
									<c:choose>
										<c:when test="${loop.index == 0}">
											<div class="radio-div radio-div-first">
												<div class="popover fade top in radio-popup-orange hide" role="tooltip" id="popover${loop.index}">
													<div class="arrow radio-arrow-orange" style="left: 50%;"></div>
													<div class="radio-popup-text-orange">No</div>
												</div>
												<div class="radio-outer-orange sq-radio" id="radio-nps-${loop.index}">
													<div class="radio-inner-orange hide"></div>
												</div>
											</div>
										</c:when>
										<c:when test="${loop.index > 0 && loop.index <= 6}">
											<div class="radio-div">
												<div class="popover fade top in radio-popup-orange hide" role="tooltip" id="popover${loop.index}">
													<div class="arrow radio-arrow-orange" style="left: 50%;"></div>
													<div class="radio-popup-text-orange">No</div>
												</div>
												<div class="radio-outer-orange sq-radio" id="radio-nps-${loop.index}">
													<div class="radio-inner-orange hide"></div>
												</div>
											</div>
										</c:when>
										<c:when test="${loop.index >= 7 && loop.index <= 8}">
											<div class="radio-div">
												<div class="popover fade top in radio-popup-yellow hide" role="tooltip" id="popover${loop.index}">
													<div class="arrow radio-arrow-yellow" style="left: 50%;"></div>
													<div class="radio-popup-text-yellow">Maybe</div>
												</div>
												<div class="radio-outer-yellow sq-radio" id="radio-nps-${loop.index}">
														<div class="radio-inner-yellow hide"></div>
												</div>
											</div>
										</c:when>
										<c:otherwise>
											<div class="radio-div">
												<div class="popover fade top in radio-popup-green hide" role="tooltip" id="popover${loop.index}">
													<div class="arrow radio-arrow-green" style="left: 50%;"></div>
													<div class="radio-popup-text-green">Yes</div>
												</div>
												<div class="radio-outer-green sq-radio" id="radio-nps-${loop.index}">
													<div class="radio-inner-green hide"></div>
												</div>
											</div>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</div>
							<div class="container-1to10">
								<c:forEach begin="0" end="10" varStatus="numLoop">
									<c:choose>
										<c:when test="${numLoop.index == 0}">
											<div class="span-1to10 span-1to10-orange" style="text-align: right; margin-left:-19px">${numLoop.index}</div>
										</c:when>
										<c:when test="${numLoop.index >0 && numLoop.index<=6}">
											<div class="span-1to10 span-1to10-orange" style="text-align: right">${numLoop.index}</div>
										</c:when>
										<c:when test="${numLoop.index >=7 && numLoop.index<=8}">
											<div class="span-1to10 span-1to10-yellow" style="text-align: right">${numLoop.index}</div>
										</c:when>
										<c:otherwise>
											<div class="span-1to10 span-1to10-green" style="text-align: right">${numLoop.index}</div>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</div>
						</div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-radio-nps" class="float-left sq-np-item sq-np-item-prev btn-com"><spring:message code="label.prev.btn.key"/></div>
						<div id="next-radio-nps" class="float-left sq-np-item sq-np-item-next btn-com"><spring:message code="label.nxt.btn.key"/></div>
					</div>
				</div>
				
				<!-- For text area -->
				<div data-ques-type="smiley-text-final" class="sq-quest-item hide">
					<input type="hidden" id="survey-profile-image-url" value="">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt"></div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="ques-text-textarea" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper">
						<textarea id="text-area" class="sq-txt-area" maxlength="500" onkeydown="checkCharacterLimit(this);"></textarea>
						<div id="text-box-disclaimer" class="bd-check-txt-disclaimer">
							<spring:message code="label.survey.disclaimer.key"/>
						</div>
						<div id="smiles-final" class="sq-happy-wrapper clearfix">
							<div id="sq-happy-smile" class="sq-smile-icn-container">
								<div id="happy-smile" star-no="1" class="sq-smile-icn-wrapper sq-happy-smile"></div>
								<div class="sq-smile-icn-text sq-smile-happy-text float-left">
									<spring:message code="label.smile.happy.text"/>
								</div>
							</div>
							<div id="sq-neutral-smile" class="sq-smile-icn-container">
								<div id="neutral-smile" star-no="2" class="sq-smile-icn-wrapper sq-neutral-smile"></div>
								<div class="sq-smile-icn-text sq-smile-neutral-text float-left">
									<spring:message code="label.smile.neutral.text"/>
								</div>
							</div>
							<div id="sq-sad-smile" class="sq-smile-icn-container">
								<div id="sad-smile" star-no="3" class="sq-smile-icn-wrapper sq-sad-smile"></div>
								<div class="sq-smile-icn-text sq-smile-sad-text float-left">
									<spring:message code="label.smile.sad.text"/>
								</div>
							</div>
						</div>
	
						<div id="pst-srvy-div" class="pst-srvy">
							<div id="shr-post-chk-box" class="float-left bd-check-img"></div>
-							<input type="hidden" id="shr-pst-cb" name="sharepost" value="true">
							<div class="float-left bd-check-txt survey-authorize-font-size"><spring:message code="label.survey.authorize.key"/></div>

							<%-- <div class="reg_form_row clearfix">
					       <div class="reg_btn" id="import-facebook" onclick="openAuthPageForSurvey(event,'facebook', ${isAutoLogin}, this);" data-link="${facebookLink}">Import profile pic from facebook</div>
					       <div class="reg_btn" id="import-linkedIn" onclick="openAuthPageForSurvey(event,'linkedin', ${isAutoLogin}, this);" data-link="${facebookLink}">Import profile pic from facebook</div>
					       <div class="reg_btn" id="import-Twitter" onclick="openAuthPageForSurvey(event,'twitter', ${isAutoLogin}, this);" data-link="${facebookLink}">Import profile pic from facebook</div>
				          </div> --%>
						</div>
						
						<div id="ss-survey-prof-image-div" class="ss-survey-prof-image-div">
							<div class="ss-survey-prof-img-sel-cont">
						        <div class="ss-survey-pi-sel-txt">Import my picture from </div>
						        <div class="ss-survey-pi-sel-icn-cont">
						            <div class="ss-survey-soc-icon icn-fb" id="import-facebook" onclick="openAuthPageForSurvey(event,'facebook', ${isAutoLogin}, this);"></div>
						            <div class="ss-survey-soc-icon icn-lin" id="import-linkedIn" onclick="openAuthPageForSurvey(event,'linkedin', ${isAutoLogin}, this);"></div>
						            <div class="ss-survey-soc-icon icn-twit" id="import-Twitter" onclick="openAuthPageForSurvey(event,'twitter', ${isAutoLogin}, this);"></div>
						        </div>
						    </div>
						    <div id="ss-survey-prof-img-cont" class="ss-survey-prof-img-cont ss-survey-pi-hide">
						       	<div class="ss-survey-prof-img-div">
									<img id="ss-survey-prof-img" class="ss-survey-prof-img" src="https://s3-us-west-1.amazonaws.com/agent-survey/dev/userprofilepics/d-e0bcca9de9f1c945da2e354e2a1444ded8836532bef7aafb6d0c1d4d71f23c8eb5ba48561d9ad09ccbe220cb59ed5db004c62124e446ff01c096304d1e8ab5e3">
								</div>
								<div class="ss-survey-prof-img-actions">
									<div class="ss-survey-pi-act-txt">
									    <input id="ss-survey-img-check" type="hidden" val="false">
									    <div id="ss-survey-pi-chk-box" class="ss-survey-act-icns ss-survey-pi-chk-box ss-survey-pi-chk-box-unchecked"></div>
									    Post my picture with the review.
									</div>
								    <div class="ss-survey-pi-act-txt">
								    	<div id="ss-survey-pi-rem-icn" class="ss-survey-act-icns ss-survey-pi-rem-icn">x</div>
								    	Remove my picture.
								    </div>
								</div>
							</div>
						</div>

						<div class="sq-np-wrapper clearfix">
							<div id="prev-textarea-smiley" class="float-left sq-np-item sq-np-item-prev btn-com"><spring:message code="label.prev.btn.key"/></div>
							<div id="next-textarea-smiley" class="float-left sq-np-item sq-np-item-next btn-com" data-survey-submit-disabled=false><spring:message code="label.nxt.btn.key"/></div>
						</div>
					</div>
				</div>

				<!-- Div for MC type questions -->
				<div data-ques-type="mcq" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt"></div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="mcq-ques-text" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper">
						<div id="answer-options" class="sq-mcq-wrapper"></div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-mcq" class="float-left sq-np-item sq-np-item-prev btn-com"><spring:message code="label.prev.btn.key"/></div>
						<div id="next-mcq" class="float-left sq-np-item sq-np-item-next btn-com"><spring:message code="label.nxt.btn.key"/></div>
					</div>
				</div>

				<!-- Div for Error Messages -->
				<div data-ques-type="error" class="sq-quest-item hide">
				
				<div id="profile-link" class="sq-main-link"></div>
					
					<div id="content-head" class="sq-main-txt">Error</div>
					<hr class="hr-survey">
					<div class="sq-ques">
						<i><span id="content" class="sq-ques-txt"></span></i>
					</div>
					<div id="social-post-links" class="share-social-link-cont hide row">
						<a id="realtor-btn" target="_blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.realtor_com.txt"/></a>
						<a id="google-business-btn" target="blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.google_business.txt"/></a>
						<a id="lt-btn" target="_blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.lending_tree.txt"/></a>
						<a id="zillow-btn" target="_blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.zillow.txt"/></a>
						<%-- <a id="ylp-btn" target="_blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.yelp.txt"/></a> --%>
						<a id="linkedin-btn" target="_blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.linkedin.txt"/></a>
						<a id="twitter-btn" target="_blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.twitter.txt"/></a>
						<a id="fb-btn" target="_blank" class="sq-btn-continue-survey sq-btn-post-social-btn col-sm-6"><spring:message code="btn.label.facebook.txt"/></a>
					</div>
				</div>
		</div>

		<!-- temp caching of images -->
		<div class="hide">
			<div class="float-left sq-mcq-chk hide st-mcq-chk-on hide"></div>
			<div class="float-left sq-mcq-chk hide st-mcq-chk-off"></div>
			<div class="sq-star sq-full-star hide"></div>
		</div>
	</div>
		
<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<!-- <script src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization','version':'1','packages':['corechart']}]}"></script> -->
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src="${initParam.resourcesPath}/resources/js/application.js"></script>
<script>
$(document).ready(function() {
	initializeTakeSurveyPage();
	//update google analytics
	updateGoogleTrackingId();
});
</script>
</div>
<!-- Page footer -->
<jsp:include page="footer.jsp" />
</body>
</html>