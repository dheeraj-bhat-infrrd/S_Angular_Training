<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
.nav-tabs>li>a {
    background-color: #fff;
    height: 40px;
    width: 150px;
    text-align: center;
    color: #777;
    border-radius: 3px;
    font-size: 13px;
    font-weight: bold !important;
    cursor: pointer;
    border: 1px solid #D3D3D7;
    box-shadow: inset 0 0 0 0 rgba(0, 0, 0, .4), -2px -3px 5px -2px rgba(0, 0, 0, .4);
    border-bottom-color: #2f69aa;
}

.nav-tabs>li.active>a, .nav-tabs>li.active>a:hover, .nav-tabs>li.active>a:focus {
    background: #2f69aa;
    color: #fff;
    box-shadow: inset 0 0 0 0 rgba(0, 0, 0, .4), -2px -3px 5px -2px rgba(0, 0, 0, .4);
    border-bottom-color: #2f69aa;
    }

.people-say-wrapper{
	width: 100%;
   	padding-top: 0;
    margin-top: -30px;
    margin-left: 30px;
}

.dash-lp-txt{
	    font-size: 15px;
}
</style>

<c:set value="${cannonicalusersettings.companySettings.iden}" var="companyId"></c:set>
<c:set value="${userId}" var="userId"></c:set>

<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>
<%-- <c:set var="activeSession" value="${activeSession }"></c:set>
<c:set var="hasRegisteredForSummit" value="${hasRegisteredForSummit }"></c:set>
<c:set var="isShowSummitPopup" value="${isShowSummitPopup }"></c:set> --%>

<div class="overlay-loader hide"></div>
<input type="hidden"  id="reporting-data-div" data-profile-master-id="${profilemasterid}" data-user-id="${userId}" data-column-name="${columnName}" data-column-value="${columnValue}">
<div id="rep-prof-container" data-profile-master-id="${profilemasterid}"
			data-column-name="${columnName}" data-account-type="${accounttype}"
			data-column-value="${columnValue}" class="hide dash-top-info dash-prof-wrapper pos-relative dash-size" >
			<div id="top-dash" class="hide" ></div>
</div>
<div>
	<c:choose>
		<c:when test="${profilemasterid == 1}">
			<input type="hidden" id="prof-company-id"
				value="${profileSettings.iden}">
			<input type="hidden" id="company-profile-name"
				value="${profileSettings.profileName}">
		</c:when>
		<c:when test="${profilemasterid == 2}">
			<input type="hidden" id="prof-region-id" value="${entityId}">
		</c:when>
		<c:when test="${profilemasterid == 3}">
			<input type="hidden" id="prof-branch-id" value="${entityId}">
		</c:when>
		<c:when test="${profilemasterid == 4}">
			<input type="hidden" id="prof-agent-id" value="${entityId}">
		</c:when>
	</c:choose>
	<input type="hidden" id="profile-id" value="${profile.userProfileId}" />
	<input type="hidden" id="profile-min-post-score"
		value="${profileSettings.survey_settings.show_survey_above_score}" />
	<input type="hidden" id="rep-real-tech-check" value="${isRealTechOrSSAdmin}" />

</div>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft">
				<spring:message code="label.header.dashboard.key" />
			</div>
			<!-- Add user assignment dropdown -->
			<%@ include file="user_assignment_dropdown.jsp" %>
		</div>
	</div>
</div>

<%-- <c:if test="${profilemasterid == 1}">
	<c:if test="${isEncompassDisabled == false}">
		<c:choose>
			<c:when test="${encompassStatus == 'SUCCESS'}">
				<div id="dsh-enc-banner" class="hm-header-main-wrapper hm-hdr-bord-bot" style="background:#5fba7d">
					<div class="container">
						<div class="hm-header-row clearfix">
							<div class="float-left last-run-time-div">
								<div class="align-center">
									<c:if test='${encompassNotification != "" && encompassNotification != null && encompassNotification !=undefined}'>
										<span class="incomplete-trans-span rep-disc-span last-run-time-span">${encompassNotification}</span>
									</c:if>
								</div>
							</div>
							<c:if test="${ isRealTechOrSSAdmin == true}">
								<div class="close-enc-banner float-right">
		                            <div id="close-enc-banner" class="close-enc-circle">x</div>
		    					</div>
	    					</c:if>
						</div>
					</div>
				</div>
			</c:when>
			<c:when test="${encompassStatus == 'ERROR'}">
				<div id="dsh-enc-banner" class="hm-header-main-wrapper hm-hdr-bord-bot" style="background:#e87c68">
					<div class="container">
						<div class="hm-header-row clearfix">
							<div class="float-left last-run-time-div">
								<div class="align-center">
									<c:if test='${encompassNotification != "" && encompassNotification != null && encompassNotification !=undefined}'>
										<span class="incomplete-trans-span rep-disc-span last-run-time-span">${encompassNotification}</span>
									</c:if>
								</div>
							</div>
							<c:if test="${ isRealTechOrSSAdmin == true}">
								<div class="close-enc-banner float-right">
		                            <div id="close-enc-banner" class="close-enc-circle">x</div>
		    					</div>
	    					</c:if>
						</div>
					</div>
				</div>
			</c:when>
		</c:choose>
	</c:if>
</c:if> --%>

<c:if test="${profilemasterid == 1}">
	<c:if test="${isEncompassDisabled == false}">
		<c:choose>
			<c:when test="${encompassStatus == 'SUCCESS'}">
				<div id="dsh-enc-banner" class="enc-banner-outer enc-banner-success">
					<div class="container">
						<div class="enc-banner-body">
							<div class="enc-banner-txt">
								<c:if test='${encompassNotification != "" && encompassNotification != null && encompassNotification !=undefined}'>
									${encompassNotification}
								</c:if>
							</div>
							<c:if test="${ isRealTechOrSSAdmin == true}">
								<div id="close-enc-banner" class="enc-banner-close cursor-pointer">x</div>
		    				</c:if>
						</div>
					</div>
				</div>
			</c:when>
			<c:when test="${encompassStatus == 'ERROR'}">
				<div id="dsh-enc-banner" class="enc-banner-outer enc-banner-error">
					<div class="container">
						<div class="enc-banner-body">
							<div class="enc-banner-txt">
								<c:if test='${encompassNotification != "" && encompassNotification != null && encompassNotification !=undefined}'>
									${encompassNotification}
								</c:if>
							</div>
							<c:if test="${ isRealTechOrSSAdmin == true}">
								<div id="close-enc-banner" class="enc-banner-close cursor-pointer">x</div>
		    				</c:if>
						</div>
					</div>
				</div>
			</c:when>
		</c:choose>
	</c:if>
</c:if>

<div class="hm-header-main-wrapper hm-hdr-bord-bot" style="background:#2f69aa">
	<div class="container">
		<div class="hm-header-row clearfix">
			<c:if test='${lastSuccessfulRun != "" && lastSuccessfulRun != null && lastSuccessfulRun !=undefined}'>
				<div class="float-left last-run-time-div">
					<div class="align-center">
						<span class="incomplete-trans-span rep-disc-span last-run-time-span">Last time data refresh run: ${lastSuccessfulRun}</span>
					</div>
				</div>
			</c:if>		
		</div>
	</div>
</div>

<%--  <div id="fb-policy-banner" class="hm-header-main-wrapper hm-hdr-bord-bot fb-policy-change-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left " style="height:  100%;">
				<div class="col-lg-2 col-sm-2 col-md-2 col-xs-2 fb-policy-ban-cont">
					<div class="alert-fb-policy"></div>
				</div>
				
				<div class="fb-policy-txt align-center col-lg-8 col-sm-8 col-md-8 col-xs-8 fb-policy-ban-cont">
					Due to recent data breach with Facebook, your FB connection might get disconnected with SocialSurvey soon. Please reconnect your account as soon as possible. Facebook's announcement about the issue can be found  <a  target= "_blank" href="https://developers.facebook.com/blog/post/2018/09/28/security-update/" >here</a>.
				</div>
				
				<div class="col-lg-2 col-sm-2 col-md-2 col-xs-2 fb-policy-ban-cont">
					<div id="fb-policy-close" class="close-fb-policy cursor-pointer"></div>
				</div>
			</div>
		</div>
	</div>
</div> --%>

<c:if test='${vertical == "mortgage"}'>
<!-- <div id="summit-ribbon-outer" class="summit-ribbon-outer">
	<div id="summit-ribbon-body" class="summit-ribbon-body  cursor-pointer">
		<div id="summit-ribbon-close-btn" class="summit-ribbon-close-btn cursor-pointer">x</div>
	</div>
</div> -->

<div id="summit-popup-outer" class="summit-popup-outer hide">
	<div id="summit-popup-body" class="summit-popup-body cursor-pointer">
		<div class="summit-popup-close-cont">
			<div id="summit-popup-close-btn" class="summit-popup-close-btn cursor-pointer"></div>
		</div>
		<div class="summit-checkbox-cont clearfix">
			<div class="float-left wc-width summit-check-contain" id="">
				<div id="summit-do-not-show" class="float-left summit-check" data-checked=false></div>
	     		<div class="float-left wc-dashboard-text summit-check-text">Do not show this again</div>
			</div>
		</div>
	</div>
</div>
</c:if>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div>
		<div class="container pos-relative">
			<div id="logo-dash" class="hide"></div>
			<div id="reporting-prof-details" class="row prof-pic-name-wrapper edit-prof-pic-name-wrapper rep-prof-details" style="border-bottom:1px solid #d2cdcd">
				<%@ include file="reporting_prof_details.jsp" %>
			</div>

			<div id="reportingDashTabs" class=" col-lg-12 col-md-12 col-sm-12 col-xs-12" style="margin-top:15px; display:inline-block; padding: 0;">
				<ul class="nav nav-tabs" role="tablist">
					<li id="transaction-stats-btn"  class="active float-left"><a href="#trans-stats-tab" data-toggle="tab">Transaction Stats</a></li>
					<li id="overview-btn" onclick="showOverviewTab()" class="float-left"><a href="#overview-tab" data-toggle="tab">Promoter Stats</a></li>
					<li id="leaderboard-btn"  class="float-left"><a href="#leaderboard-tab" data-toggle="tab">LeaderBoard</a></li>
					<li id="score-stats-btn" class="float-left"><a href="#score-stats-tab" data-toggle="tab">Score Stats</a></li>
					<li id="reviews-btn" class="float-left"><a href="#reviews-tab" data-toggle="tab">Reviews</a></li>
					<li id="incomplete-surveys-btn" class="float-left" ><a href="#incomplete-surveys-tab" data-toggle="tab" style="padding-left:2px; padding-right:2px">Incomplete Surveys</a></li>
				</ul>
				<div class="tab-content rep-tab-content">
					<div class="tab-pane fade col-lg-12 col-md-12 col-sm-12 col-xs-12 active in" id="trans-stats-tab" style="padding: 0;">
						<div id="reporting-trans-details" data-switch="true" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 prof-pic-name-wrapper edit-prof-pic-name-wrapper reporting-trans-details">
							<div id="trans-stats-dash" class="hide" ></div>
							<%-- <%@ include file="reporting_transaction_details.jsp" %> --%>
						</div>
					</div>

					<div class="tab-pane fade" id="overview-tab"  style="margin-top: 40px;">
						<div id="overviewSuccess" class="hide col-lg-12 col-md-12 col-sm-12 col-xs-12" style="padding:0">
							<%@ include file="reporting_overview.jsp" %>
						</div>
						<div id="overviewFailure" class="hide">
							<div style="text-align:center; margin:5% auto">
								<span class="incomplete-trans-span" style="font-size:large">Sorry!!!</span>
								<div style="clear: both">
									<span class="incomplete-trans-span" style="font-size:large">Incomplete Data Found in Overview</span>
								</div>
							</div>
						</div>
					</div>

					<div class="tab-pane fade col-lg-12 col-md-12 col-sm-12 col-xs-12" id="leaderboard-tab" style="padding: 0;">
						<%@ include file="reporting_leaderboard.jsp" %>
					</div>

					<div class="tab-pane fade col-lg-12 col-md-12 col-sm-12 col-xs-12" id="score-stats-tab">
						<div id="score-stats-dash" class="hide" ></div>
						<div id="overall-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12 score-stats-overall-con">
							<span class="score-stats-lbl">Overall Rating</span>
							<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 score-stats-graph-con">
								<div id="overall-rating-chart" style="width: 100%; min-height: 300px"></div>
							</div>
						</div>

						<div id="question-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12 score-stats-ques-con">

						</div>
						<div id="empty-questions-div" class="hide" style="position: relative;">
							<div style="text-align: center; margin: 5% auto">
								<span class="incomplete-trans-span" style="font-size: large">Sorry!!!</span>
								<div style="clear: both">
									<span class="incomplete-trans-span" style="font-size: large">No Questions found for your account</span>
								</div>
							</div>
						</div>
					</div>

					<div class="tab-pane fade" id="reviews-tab" style="margin-top: 30px;">
						<div id="rep-reviews-container" class="people-say-wrapper rt-content-main rt-content-main-adj" style="margin-left:0">
							<div class="main-con-header clearfix pad-bot-10-resp" style="display: block; border-bottom: 1px solid #dcdcdc; padding: 15px 0;">
								<div id="review-desc" class="float-left dash-ppl-say-lbl" data-profile-name="${profileName}"></div>
							</div>
							<div id="review-details" class="ppl-review-item-wrapper">
							</div>
						</div>
					</div>

					<div class="tab-pane fade" id="incomplete-surveys-tab" style="max-height:600px; margin-top: 30px;">
						<div id="rep-dash-survey-incomplete" class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12" style="width: 100%; padding-top:0; margin-top: -10px; margin-bottom: 20px;">
							<div class="dash-lp-header clearfix" id="incomplete-survey-header">
								<div class="float-left" style="font-size: 20px;"><spring:message code="label.incompletesurveys.key" /></div>
							</div>
							<div id="inc-survey-cont" class="welcome-popup-body-wrapper clearfix">
								<div id="rep-icn-sur-popup-cont" data-start="0" data-total="0" data-batch="5" class="icn-sur-popup-cont"></div>
								<div class="mult-sur-icn-wrapper">
									<div id="rep-resend-mult-sur-icn" class="mult-sur-icn resend-mult-sur-icn float-left" title="Resend"></div>
								    <div id="rep-del-mult-sur-icn" class="mult-sur-icn del-mult-sur-icn float-right" title="Delete"></div> 
								</div>
							</div>
							<div id="paginate-buttons-survey" class="paginate-buttons-survey clearfix">
								<div id="rep-sur-previous" class="float-left rep-sur-paginate-btn">Prev</div>
								<div class="paginate-sel-box float-left">
									<input id="rep-sel-page" type="text" pattern="[0-9]*" class="sel-page"/>
									<span class="paginate-divider">/</span>
									<span id="rep-paginate-total-pages" class="paginate-total-pages"></span>
									<span> </span>
								</div>
								<div id="rep-sur-next" class="float-right rep-sur-paginate-btn">Next</div>
							</div>
							<div id="rep-nil-dash-survey-incomplete" class="hide">
								<div style="text-align:center; margin:5% auto">
									<span class="incomplete-trans-span" style="font-size:large">Cheers!!!</span>
									<div style="clear: both">
										<span class="incomplete-trans-span" style="font-size:large">No Incomplete surveys found</span>
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		$(document).attr("title", "Dashboard");

		updateViewAsScroll();
		
		$('#pro-cmplt-stars').on('click', '#dsh-btn1', function(e) {
			e.stopPropagation();
			if (colName == 'agentId') {
				sendSurveyInvitation('#dsh-btn1');
			} else if (accountType == "INDIVIDUAL") {
				sendSurveyInvitation('#dsh-btn1');
			} else {
				sendSurveyInvitationAdmin(colName, colValue, '#dsh-btn1');
			}
		});

	var profileMasterId = $('#rep-prof-container').attr('data-profile-master-id');
	var currentProfileName = $('#rep-prof-container').attr('data-column-name');
	var currentProfileValue = $('#rep-prof-container').attr('data-column-value');
	var accountType = $('#rep-prof-container').attr('data-account-type');


	if( "${isSocialMediaExpired}" == "true" ){
		var columnName = "${columnName}";
		var columnValue = "${columnValue}";
		$("#rep-fix-social-media").removeClass("hide");

	} else {
		drawReportingDashButtons(currentProfileName, currentProfileValue);
	}

	drawTransactionDetailsTab();

	drawOverviewPage();

	//Score stats
		var companyId = "${companyId}";
		var userId = "${userId}";
		var profileMasterIdLead = "${profilemasterid}";
		var columnName = "${columnName}";
		var columnId = "${columnValue}";

		drawLeaderboardPage(columnName, columnId, profileMasterIdLead, userId, companyId);

		var entityType = $('#reporting-data-div').attr('data-column-name');
		var entityId = parseInt($('#reporting-data-div').attr('data-column-value'));

		getOverallScoreStats(entityId, entityType);

		getQuestionScoreStats(entityId,entityType);

		paintReportingDashboard(profileMasterId, currentProfileName, currentProfileValue, accountType);

		$(window).resize();

		var currentProfileName = $('#rep-prof-container').attr('data-column-name');
		var currentProfileValue = $('#rep-prof-container').attr('data-column-value');

		getIncompleteSurveyCountForNewDashboard(currentProfileName, currentProfileValue);

		$('#rep-icn-sur-popup-cont').attr("data-start", 0);
		paintIncompleteSurveyListForNewDashboard(0,currentProfileName,currentProfileValue);

		$('#rep-pro-cmplt-stars').on('click', '#dsh-btn2', function(e) {
			e.stopPropagation();
			var buttonId = 'dsh-btn2';
			var task = $('#dsh-btn2').data('social');
			dashboardButtonAction(buttonId, task, colName, colValue);
			$('#rep-social-media').fadeOut(500);
			if($('#rep-prof-container').length>0){
					var currentProfileName = $('#rep-prof-container').attr('data-column-name');
					var currentProfileValue = $('#rep-prof-container').attr('data-column-value');
					changeSocialMedia(currentProfileName, currentProfileValue);
			}
		});

		$('#rep-pro-fix-cmplt-stars').on('click', '#dsh-btn0', function(e) {
			e.stopPropagation();
			var buttonId = 'dsh-btn0';
			// getSocialMediaToFix
			var payload = {
					"columnName" : $('#rep-prof-container').attr('data-column-name'),
					"columnValue" : $('#rep-prof-container').attr('data-column-value')
				};
			callAjaxGetWithPayloadData('./socialmediatofix.do', paintFixSocialMedia, payload, true);
		});

		$('#rep-social-media').on('click','#rep-dismiss-social-media',function(e){
			$('#rep-social-media').fadeOut(500);
			delay(function(){
				e.stopPropagation();
				var currentProfileName = $('#rep-prof-container').attr('data-column-name');
				var currentProfileValue = $('#rep-prof-container').attr('data-column-value');
				changeSocialMedia(currentProfileName, currentProfileValue);
			},500);
		});


		$('#rep-fix-social-media').on('click','#rep-dismiss-fix-social-media',function(e){
			$('#rep-fix-social-media').fadeOut(500);
			delay(function(){
				drawReportingDashButtons(currentProfileName, currentProfileValue);
			},500);
		});

		$(window).off('scroll');
		$(window).scroll(function() {
			if(window.location.hash.substr(1) == "showreportingpage" && $('#reviews-tab').hasClass('active')) {
				dashbaordReviewScroll();
			}
		});

		cssForSafari();

		hideOverlay();
		
		showSummitRibbon();
		
		var isShowSummitPopup ="${isShowSummitPopup}";
		var newSession = sessionStorage.getItem("newSession");
		
		if(newSession == false || newSession == 'false'){
			$('#summit-popup').hide();
			showSummitRibbon();
		}else{
			if(isShowSummitPopup == 'false' || isShowSummitPopup == false){
				showSummitPopup();
			}else{
				showSummitRibbon();
			}
		}
		
		sessionStorage.setItem("newSession",false);
	});
</script>