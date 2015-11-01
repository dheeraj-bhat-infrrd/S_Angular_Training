<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

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
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<script src='//www.google.com/recaptcha/api.js'></script>
</head>

<body>
	<div id="toast-container" class="toast-container">
		<span id="overlay-toast" class="overlay-toast"></span>
	</div>
	<div class="overlay-loader hide"></div>
	<div class="overlay-payment hide"></div>

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

	<div class="hdr-wrapper">
		<div class="container hdr-container clearfix">
			<div class="float-left hdr-logo"></div>
<%-- 			<div class="float-right clearfix hdr-btns-wrapper">
				<div class="float-left hdr-log-btn hdr-log-reg-btn">
					<spring:message code="label.signin.key" />
				</div>
				<div class="float-left hdr-reg-btn hdr-log-reg-btn">
					<spring:message code="label.joinus.key" />
				</div>
			</div> --%>
			<div id="header-user-info" class="header-user-info float-right clearfix sur-com-logo">
				<c:if test="${companyLogo != null}">
					<div class="float-left user-info-seperator"></div>
					<div class="float-left user-info-logo" style="background: url(${companyLogo}) no-repeat center; background-size: 100% auto;"></div>
				</c:if>
			</div>
		</div>
	</div>
	<div id="err-nw-wrapper" class="err-nw-wrapper" style="margin-bottom: 10px;">
		<span class="err-new-close"></span>
		<span id="err-nw-txt"></span>
	</div>
	<div id="prof-container" data-q="${q}" data-agentid="${agentId}" data-agentName="${agentName}" data-agent-email="${agentEmail}"
	data-last-name="${lastName}" data-first-name="${firstName}"
		 class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
		<div class="container">
			<div class="sq-ques-wrapper">
				<div id="agnt-img" class="sq-top-img"></div>
				<div data-ques-type="user-details" class="sq-quest-item hide">
					<!-- <div id="agnt-img" cust-agnt-rel class="sq-top-img"></div> -->
					<!-- <div class="sq-main-txt">Survey Question</div> -->
					<div class="sq-ques">
						<i><span class="sq-ques-txt">
							<spring:message code="label.surveyquestion.header.key" /><br>
							<spring:message code="label.surveyquestion.header2.key" /><br>
							<span class="semibold">${agentName}</span>
						</span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<input type="hidden" value="${message}" data-status="${status}" name="message" id="message"/>
					<div class="sq-rat-wrapper">
						<form id="survey-request-form" action="/rest/survey/triggersurvey">
							<div class="sq-star-wrapper sq-i-container clearfix ques-wrapper-adj">
								<div class="clearfix sq-info-wrapper">
									<div class="sq-i-lbl float-left">First Name</div>
									<div class="sq-i-txt float-left">
										<div class="hide sq-img-adj icn-fname"></div>
										<input id="firstName" class="sq-i-txt-fld" name="firstName" value="${firstName}"
											placeholder='<spring:message code="label.firstname.key"/>'>
									</div>
								</div>
								<div class="clearfix sq-info-wrapper">
									<div class="sq-i-lbl float-left">Last Name</div>
									<div class="sq-i-txt float-left">
										<div class="hide sq-img-adj icn-lname"></div>
										<input id="lastName" class="sq-i-txt-fld" name="lastName" value="${lastName}"
											placeholder='<spring:message code="label.lastname.key"/>'>
									</div>
								</div>
								<div class="clearfix sq-info-wrapper">
									<div class="sq-i-lbl float-left">Email</div>
									<div class="sq-i-txt float-left">
										<div class="hide sq-img-adj icn-email"></div>
										<input id="email" class="sq-i-txt-fld" name="customerEmail" value="${customerEmail}"
											placeholder='<spring:message code="label.emailid.key"/>'>
									</div>
								</div>
								<div class="clearfix sq-info-wrapper">
									<div class="sq-i-lbl float-left sq-i-checkbox">
										<div id="cust-agent-verify" class="bd-check-img bd-check-img-checked float-right sq-checkbox"></div>
									</div>
									<div class="sq-i-txt float-left sq-i-checkbox-txt">
										<div class="sq-i-check-txt">I verify that I have done business with ${agentName}</div>
										<!-- <select id="cust-agnt-rel" class="sq-i-txt-fld"></select> -->
									</div>
								</div>
								<div class="clearfix reg-captcha-wrapper reg-item reg-cap-nw-adj">
									<div class="reg-cap-nw-adj-container">
										<div class="g-recaptcha" data-sitekey="6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K"></div>
										<!-- <div class="reg-captcha-img"></div>
										<div class="reg-captcha-btns clearfix">
											<input id="captcha-text" class="float-left reg-cap-txt"
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
								<!-- <div id="outer_captcha" style="display: none;">
									<div id="recaptcha"></div>
								</div> -->
								<div style="display: none">
									<input type="hidden" name="agentId" value="${agentId}">
									<input type="hidden" name="agentName" value="${agentName}">
									<input type="hidden" name="relationship">
									<input type="hidden" name="g-recaptcha-response">
								</div>
								<div class="sq-btn-wrapper">
									<div id="start-btn" class="sq-btn-continue">Start</div>
								</div>
								<div id="privacy-policy-link" class="privacy-policy-link take-sur-link"><a href="https://www.socialsurvey.me/survey/privacy-policy/">Privacy Policy</a></div>
							</div>
						</form>
					</div>
				</div>

				<div data-ques-type="stars" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt">Survey Question</div>
					<div class="sq-ques">
						<i><span id="ques-text" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper">
						<div id="sq-stars" class="sq-star-wrapper clearfix">
							<div star-no="1" class="sq-star opacity-red"></div>
							<div star-no="2" class="sq-star opacity-red"></div>
							<div star-no="3" class="sq-star opacity-red"></div>
							<div star-no="4" class="sq-star opacity-red"></div>
							<div star-no="5" class="sq-star opacity-red"></div>
						</div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-star" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
						<div id="next-star" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
					</div>
				</div>
				<div data-ques-type="smiley" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt">lorema ipsum lorema ipsum lorema ipsum</div>
					<div class="sq-ques">
						<i><span id="ques-text-smiley" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div id="sq-smiles" class="sq-rat-wrapper">
						<div class="sq-star-wrapper clearfix">
							<div smile-no="1" class="sq-smile sq-smile-1 opacity-red"></div>
							<div smile-no="2" class="sq-smile sq-smile-2 opacity-red"></div>
							<div smile-no="3" class="sq-smile sq-smile-3 opacity-red"></div>
							<div smile-no="4" class="sq-smile sq-smile-4 opacity-red"></div>
							<div smile-no="5" class="sq-smile sq-smile-5 opacity-red"></div>
						</div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-smile" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
						<div id="next-smile" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
					</div>
				</div>

				<!-- Div for rating questions of  scale  -->
				<div data-ques-type="scale" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt"></div>
					<div class="sq-ques">
						<i><span id="ques-text-scale" class="sq-ques-txt"></span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper mgn-bot-40">
						<div class="sq-slider-wrapper pts-wrap-adj clearfix">
							<div class="sq-pts-item-hover pts-hover-1 pts-arr-bot">Poor</div>
							<div class="sq-pts-item-hover pts-hover-2 pts-arr-bot">Not Bad</div>
							<div class="sq-pts-item-hover pts-hover-3 pts-arr-bot">Good</div>
							<div class="sq-pts-item-hover pts-hover-4 pts-arr-bot">V Good</div>
							<div class="sq-pts-item-hover pts-hover-5 pts-arr-bot">Excellent</div>
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
						<div id="prev-scale" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
						<div id="next-scale" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
					</div>
				</div>

				<!-- For text area -->
				<div data-ques-type="smiley-text-final" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt"></div>
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
							<div id="shr-post-chk-box" class="float-left bd-check-img bd-check-img-checked"></div>
							<input type="hidden" id="shr-pst-cb" name="sharepost" value="false">
							<div class="float-left bd-check-txt"><spring:message code="label.survey.authorize.key"/></div>
						</div>
						<div class="sq-np-wrapper clearfix">
							<div id="prev-textarea-smiley" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
							<div id="next-textarea-smiley" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
						</div>
						<!-- <div class="sq-btn-wrapper">
							<div id="submit" class="sq-btn-continue hide">Submit</div>
						</div> -->
					</div>
				</div>

				<!-- Div for MC type questions -->
				<div data-ques-type="mcq" class="sq-quest-item hide">
					<!-- <div class="sq-top-img"></div> -->
					<div class="sq-main-txt">lorema ipsum lorema ipsum lorema ipsum</div>
					<div class="sq-ques">
						<i><span id="mcq-ques-text" class="sq-ques-txt">lorem
								ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it
								ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem
								ipsum dore it ler.</span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper">
						<div id="answer-options" class="sq-mcq-wrapper"></div>
					</div>
					<div class="sq-np-wrapper clearfix">
						<div id="prev-mcq" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
						<div id="next-mcq" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
					</div>
				</div>

				<!-- Div for Error Messages -->
				<div data-ques-type="error" class="sq-quest-item hide">
					<div id="profile-link" class="sq-main-link"></div>
					<div id="content-head" class="sq-main-txt">Error</div>
					<div class="sq-ques">
						<i><span id="content" class="sq-ques-txt"></span></i>
					</div>
					<!-- <div id="social-post-lnk" class="clearfix hide">
						<div class="sq-bord-bot-sm"></div>
						<div class="sq-btn-social-wrapper float-left">
							<a id="ylp-btn" target="_blank" class="sq-btn-continue sq-btn-post-social float-right">Yelp</a>
						</div>
						<div class="sq-btn-social-wrapper float-left">
							<a id="ggl-btn" target="_blank" class="sq-btn-continue sq-btn-post-social float-left">Google+</a>
					</div> -->
					<div id="social-post-links" class="share-social-link-cont hide">
						 <a id="ylp-btn" target="_blank" class="sq-btn-continue sq-btn-post-social-btn">Yelp</a>
						<a id="ggl-btn" target="_blank" class="sq-btn-continue sq-btn-post-social-btn">Google+</a>
						<a id="zillow-btn" target="_blank" class="sq-btn-continue sq-btn-post-social-btn">Zillow</a>
						<a id="lt-btn" target="_blank" class="sq-btn-continue sq-btn-post-social-btn">Lending Tree</a> 
							<div class="ppl-share-wrapper clearfix rev-ppl-share-wrapper">
						<!-- <a id="realtor-btn" target="_blank" class="sq-btn-continue sq-btn-post-social-btn">Realtor</a> 
							<div class="ppl-share-wrapper clearfix rev-ppl-share-wrapper"> -->

					<div class="float-left blue-text ppl-share-shr-txt"><spring:message code="label.share.key"/></div>
					<div class="float-left clearfix ppl-share-social ">
						<a id="icn-fb-shr" target="_blank"><span class="float-left ppl-share-icns icn-fb fb-shr" title="Facebook"></span></a>
						<a id="icn-twitter-shr" target="_blank"><span class="float-left ppl-share-icns icn-twit twt-shr" title="Twitter"></span></a>
						<a id="icn-linkedin-shr" target="_blank"><span class="float-left ppl-share-icns icn-lin lnkdn-shr" title="LinkedIn"></span></a>
                        <a id="icn-google-shr"  target="_blank"><span class="float-left ppl-share-icns icn-gplus yelp-shr" title="Google+"></span></a>
					
					</div>
				</div>
				
				</div>
			</div>
		</div>

		<!-- temp caching of images -->
		<div class="hide">
			<div class="float-left sq-mcq-chk hide st-mcq-chk-on hide"></div>
			<div class="float-left sq-mcq-chk hide st-mcq-chk-off"></div>
			<div class="sq-star sq-full-star hide"></div>
		</div>
		<!-- close -->
	</div>
	
<!-- <div style="display: none">
	<script src="https://www.google.com/recaptcha/api/challenge?k=6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-"></script>
</div>
 -->
<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization','version':'1','packages':['corechart']}]}"></script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src="https://js.braintreegateway.com/v2/braintree.js"></script>
<script src="${initParam.resourcesPath}/resources/js/progressbar.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/rangeslider.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/application.js"></script>
<script src="${initParam.resourcesPath}/resources/js/googletracking.js"></script>
<script>
$(document).ready(function() {
	$("div[data-ques-type]").hide();
	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
			showError($('#message').val());
		} else {
			showInfo($('#message').val());
		}
	}
	var q = $('#prof-container').attr("data-q");
	
	function captchaLoaded() {
		var imgData = $(".recaptcha_image_cell").html();
		var challenge = Recaptcha.get_challenge('6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-');
		if(challenge == undefined){
			Recaptcha.reload();
		}else{
			$(".reg-captcha-img").html(imgData);
		}
	}

	$(".reg-cap-reload").click(function() {
		$("#recaptcha_reload").click();
	});

	$(".reg-cap-sound").click(function() {
		if (captchaText == true) {
			$("#recaptcha_switch_audio").click();
			captchaText = false;
			$(this).addClass('reg-cap-text');
		} else {
			$("#recaptcha_switch_img").click();
			captchaText = true;
			$(this).removeClass('reg-cap-text');
		}
	});

	$(".reg-cap-info").click(function() {
		$("#recaptcha_whatsthis").click();
	});
	// Code for captcha validation.
	var captchaText = true;
	resizeFunc();
	$(window).resize(resizeFunc);
	
	if(q != undefined && q!=""){
		initSurveyWithUrl(q);
	}
	else{
		var agentId = $('#prof-container').attr("data-agentid");
		$("div[data-ques-type='user-details']").show();
		loadAgentPic(agentId);
		
		/* $("#cust-agnt-rel").html(paintListOptions($('#prof-container').attr("data-agentName"))); */
	}
	var survQuesNo = 1;
	var nextQ, prevQ;

	adjustMinHeight();
	$(window).resize(adjustMinHeight);
	function adjustMinHeight() {
		var winH = $(window).height();
		if ($(window).width() < 768) {
			var minH = winH - 50 - 50 - 5 - 1;
		} else {
			var minH = winH - 80 - 78 - 78 - 1;
		}
		$('.min-height-container').css('min-height', minH + 'px');
	}

	$('.sq-pts-red').hover(function() {
		$('.pts-hover-1').show();
	}, function() {
		$('.pts-hover-1').hide();
	});

	$('.sq-pts-org').hover(function() {
		$('.pts-hover-2').show();
	}, function() {
		$('.pts-hover-2').hide();
	});

	$('.sq-pts-lgreen').hover(function() {
		$('.pts-hover-3').show();
	}, function() {
		$('.pts-hover-3').hide();
	});

	$('.sq-pts-military').hover(function() {
		$('.pts-hover-4').show();
	}, function() {
		$('.pts-hover-4').hide();
	});

	$('.sq-pts-dgreen').hover(function() {
		$('.pts-hover-5').show();
	}, function() {
		$('.pts-hover-5').hide();
	});

	$('.st-checkbox-on').click(function() {
		$(this).hide();
		$(this).parent().find('.st-checkbox-off').show();
	});

	$('.st-checkbox-off').click(function() {
		$(this).hide();
		$(this).parent().find('.st-checkbox-on').show();
	});

	function resizeFunc() {
		var winW = $(window).width();
		if (winW < 768) {
			var offset = winW - 114 - 20;
			$('.reg-cap-txt').css('max-width', offset + 'px');
		}
	}
	
	$('#cust-agent-verify').on('click',function(){
		if($(this).hasClass('bd-check-img-checked')){
			$(this).removeClass('bd-check-img-checked');
		}else{
			$(this).addClass('bd-check-img-checked');
		}
	});
});
function checkCharacterLimit(element) {
	$('#toast-container').hide();
	if(element.value.length >= 500){
		$('#overlay-toast').html('Maximum charter limit 500');
		showToast();		
	}
}
</script>
<jsp:include page="footer.jsp" />