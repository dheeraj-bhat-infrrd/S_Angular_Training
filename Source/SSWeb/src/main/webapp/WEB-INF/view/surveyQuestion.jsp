<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message code="label.survey.title.key" /></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
     <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
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
            

            <div id="header-user-info" class="header-user-info float-right clearfix">
                <%-- <div class="float-left user-info-initial">
                    <span>${fn:substring(user.firstName, 0, 1)}</span>
                    <div class="initial-dd-wrapper hide blue-arrow-bot">
                        <c:if test="${highestrole == 1}">
                            <!-- show the company settings only of the user has company admin as a role -->
                            <div class="initial-dd-item" id="company-setting">
                                <spring:message code="label.settings.company.key" />
                            </div>
                        </c:if>
                        <div class="initial-dd-item" id="profile-setting">
                            <spring:message code="label.profilesetting.key" />
                        </div>
                        <div class="initial-dd-item" id="account-setting">
                            <spring:message code="label.accountsetting.key" />
                        </div>
                        <div class="initial-dd-item" id="change-password"onclick="showMainContent('./showchangepasswordpage.do')">
                        <spring:message code="label.changepassword.key"/>
                        </div>
                        <c:if test="${user.company.licenseDetails[0].accountsMaster.accountsMasterId < 4}">
                            <div class="initial-dd-item" id="upgrade-plan" onclick="upgradePlan();">
                                <spring:message	code="label.header.upgrade.key" />
                            </div>
                        </c:if>
                        <a class="initial-dd-item" href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
                    </div>
                </div> --%>


                <c:if test="${displaylogo != null}">
                    <div class="float-left user-info-seperator"></div>
                    <div class="float-left user-info-logo"
                        style="background: url(${displaylogo}) no-repeat center; background-size: 100% auto;"></div>
                </c:if>

            </div>
            <div id="header-menu-icn" class="header-menu-icn icn-menu hide float-right"></div>
        </div>
    </div>


<div id="prof-container" data-agentId="${agentId}" data-agentName="${agentName}"
	class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
	<div class="container">
		<div class="sq-ques-wrapper">
			
			<div data-ques-type="user-details" class="sq-quest-item">
				<div class="sq-top-img"></div>
				<!-- <div class="sq-main-txt">Survey Question</div> -->
				<div class="sq-ques">
					<i><span  class="sq-ques-txt">Please fill in your details to take survey</span></i>
				</div>
				<div class="sq-bord-bot-sm"></div>
				<div class="sq-rat-wrapper">
					<div class="sq-star-wrapper sq-i-container clearfix">
						<div class="clearfix sq-info-wrapper">
							<div class="sq-i-lbl float-left">First Name</div>
							<div class="sq-i-txt float-left">
								<input id="firstName" class="sq-i-txt-fld">
							</div>
						</div>
						<div class="clearfix sq-info-wrapper">
							<div class="sq-i-lbl float-left">Last Name</div>
							<div class="sq-i-txt float-left">
								<input id="lastName" class="sq-i-txt-fld">
							</div>
						</div>
						<div class="clearfix sq-info-wrapper">
							<div class="sq-i-lbl float-left">Email</div>
							<div class="sq-i-txt float-left">
								<input id="email" class="sq-i-txt-fld">
							</div>
						</div>
						<div class="sq-btn-wrapper">
							<div id="start-btn" class="sq-btn-continue">Start</div>
						</div>
					</div>
				</div>
			</div>
			
			
			<div data-ques-type="stars" class="sq-quest-item">
				<div class="sq-top-img"></div>
				<div class="sq-main-txt">Survey Question</div>
				<div class="sq-ques">
					<i><span id="ques-text" class="sq-ques-txt"></span></i>
				</div>
				<div class="sq-bord-bot-sm"></div>
				<div class="sq-rat-wrapper">
					<div id="sq-stars" class="sq-star-wrapper clearfix">
						<div star-no="1" class="sq-star"></div>
						<div star-no="2" class="sq-star"></div>
						<div star-no="3" class="sq-star"></div>
						<div star-no="4" class="sq-star"></div>
						<div star-no="5" class="sq-star"></div>
					</div>
				</div>
				<div class="sq-np-wrapper clearfix">
					<div id="prev-star" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
					<div id="next-star" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
				</div>
			</div>
			<div data-ques-type="smiley" class="sq-quest-item hide">
				<div class="sq-top-img"></div>
				<div class="sq-main-txt">lorema ipsum lorema ipsum lorema
					ipsum</div>
				<div class="sq-ques">
					<i><span id="ques-text-smiley" class="sq-ques-txt"></span></i>
				</div>
				<div class="sq-bord-bot-sm"></div>
				<div id="sq-smiles" class="sq-rat-wrapper">
					<div class="sq-star-wrapper clearfix">
						<div smile-no="1" class="sq-smile"></div>
						<div smile-no="2" class="sq-smile"></div>
						<div smile-no="3" class="sq-smile"></div>
						<div smile-no="4" class="sq-smile"></div>
						<div smile-no="5" class="sq-smile"></div>
					</div>
				</div>
				<div class="sq-np-wrapper clearfix">
					<div id="prev-smile" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
					<div id="next-smile" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
				</div>
			</div>

			<!-- Div for rating questions of  scale  -->
			<div data-ques-type="scale" class="sq-quest-item hide">
				<div class="sq-top-img"></div>
				<div class="sq-main-txt"></div>
				<div id="ques-text-scale" class="sq-ques">
					<i><span class="sq-ques-txt"></span></i>
				</div>
				<div class="sq-bord-bot-sm"></div>
				<div class="sq-rat-wrapper mgn-bot-40">
                    <div class="sq-slider-wrapper pts-wrap-adj clearfix">
                        <div class="sq-pts-item-hover pts-hover-1 pts-arr-bot">Poor</div>
                        <div class="sq-pts-item-hover pts-hover-2 pts-arr-bot">Not Bad</div>
                        <div class="sq-pts-item-hover pts-hover-3 pts-arr-bot">Good</div>
                        <div class="sq-pts-item-hover pts-hover-4 pts-arr-bot">V Good</div>
                        <div class="sq-pts-item-hover pts-hover-5 pts-arr-bot">Exccellent</div>
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
				<div class="sq-top-img"></div>
				<div class="sq-main-txt"></div>
				<div class="sq-ques">
					<i><span id="ques-text-textarea" class="sq-ques-txt"></span></i>
				</div>
				<div class="sq-bord-bot-sm"></div>
				<div class="sq-rat-wrapper">
                    <textarea id="text-area" class="sq-txt-area"></textarea>
					<div id="smiles-final" class="sq-happy-wrapper clearfix">
						<div id="happy-smile" star-no="1"
							class="sq-smile-icn-wrapper sq-happy-smile"></div>
						<div id="neutral-smile" star-no="2"
							class="sq-smile-icn-wrapper sq-neutral-smile"></div>
						<div id="sad-smile" star-no="3"
							class="sq-smile-icn-wrapper sq-sad-smile"></div>
					</div>

				</div>
				<div class="sq-np-wrapper clearfix">
					<div id="prev-textarea-smiley"
						class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
					<div id="next-textarea-smiley"
						class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
				</div>
				<div class="sq-btn-wrapper">
					<div id="submit" class="sq-btn-continue hide">Submit</div>
				</div>
			</div>

			<!-- Div for MC type questions -->
			<div data-ques-type="mcq" class="sq-quest-item hide">
				<div class="sq-top-img"></div>
				<div class="sq-main-txt">lorema ipsum lorema ipsum lorema
					ipsum</div>
				<div class="sq-ques">
					<i><span id="mcq-ques-text" class="sq-ques-txt">lorem
							ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it
							ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem
							ipsum dore it ler.</span></i>
				</div>
				<div class="sq-bord-bot-sm"></div>
				<div class="sq-rat-wrapper">
					<div id="answer-options" class="sq-mcq-wrapper">
						
					</div>
				</div>
				<div class="sq-np-wrapper clearfix">
					<div id="prev-mcq" class="float-left sq-np-item sq-np-item-prev btn-com">Previous</div>
					<div id="next-mcq" class="float-left sq-np-item sq-np-item-next btn-com">Next</div>
				</div>
			</div>
		</div>
	</div>
<!--    temp caching of images -->
    <div class="hide">
        <div class="float-left sq-mcq-chk hide st-mcq-chk-on hide"></div>
        <div class="float-left sq-mcq-chk hide st-mcq-chk-off"></div>
        <div class="sq-star sq-full-star hide"></div>
    </div>
<!--    close-->
</div>

<jsp:include page="scripts.jsp" />

<script>
	$(document).ready(
			function() {
				var survQuesNo = 1;
				var nextQ, prevQ;

                adjustMinHeight();
                $(window).resize(adjustMinHeight);
                function adjustMinHeight(){
                    var winH = $(window).height();
                    if($(window).width() < 768){
                        var minH = winH - 50 - 50 - 5 - 1;
                    }else{
                        var minH = winH - 80 - 78 - 78 -1;
                    }
                    $('.min-height-container').css('min-height',minH+'px');
                }
                
                $('.sq-pts-red').hover(
                    function(){
                        $('.pts-hover-1').show();
                    },function(){
                        $('.pts-hover-1').hide();
                    }
                );
                
                $('.sq-pts-org').hover(
                    function(){
                        $('.pts-hover-2').show();
                    },function(){
                        $('.pts-hover-2').hide();
                    }
                );
                
                $('.sq-pts-lgreen').hover(
                    function(){
                        $('.pts-hover-3').show();
                    },function(){
                        $('.pts-hover-3').hide();
                    }
                );
                
                $('.sq-pts-military').hover(
                    function(){
                        $('.pts-hover-4').show();
                    },function(){
                        $('.pts-hover-4').hide();
                    }
                );
                
                $('.sq-pts-dgreen').hover(
                    function(){
                        $('.pts-hover-5').show();
                    },function(){
                        $('.pts-hover-5').hide();
                    }
                );
                
				$('.st-checkbox-on').click(function() {
					$(this).hide();
					$(this).parent().find('.st-checkbox-off').show();
				});

				$('.st-checkbox-off').click(function() {
					$(this).hide();
					$(this).parent().find('.st-checkbox-on').show();
				});
				$("div[data-ques-type]").hide();
				$("div[data-ques-type='user-details']").show();
//				initSurvey($('#prof-container').attr("data-agentId"));
			});
</script>

<jsp:include page="footer.jsp" />