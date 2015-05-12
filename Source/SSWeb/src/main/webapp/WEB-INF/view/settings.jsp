<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profile}">
	<c:set value="${profile.profilesMaster.profileId}" var="profilemasterid"></c:set>
</c:if>
<c:if test="${not empty accountSettings && not empty accountSettings.survey_settings}">
	<c:set value="${accountSettings.survey_settings}" var="surveysettings"></c:set>
</c:if>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left"><spring:message code="label.title.settings.key" /></div>
			<c:if test="${not empty profileList && fn:length(profileList) > 1}">
				<div class="float-right header-right clearfix hr-dsh-adj-rt" style="z-index: 99; margin-left: 50px;">
					<div class="float-left hr-txt1"><spring:message code="label.viewas.key" /></div>
					<div id="setting-sel" class="float-left hr-txt2 cursor-pointer">${profileName}</div>
					<div id="se-dd-wrapper-profiles" class="va-dd-wrapper hide">
						<c:forEach var="userprofile" items="${profileList}">
							<div class="se-dd-item" data-profile-id="${userprofile.key}">${userprofile.value.userProfileName}</div>
						</c:forEach>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>

<div id="temp-div"></div>
<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25"
	data-hpy="${surveysettings.happyText}" data-hpy-compl="${surveysettings.happyTextComplete}" 
	data-nutl="${surveysettings.neutralText}" data-nutl-compl="${surveysettings.neutralTextComplete}" 
	data-sad="${surveysettings.sadText}" data-sad-compl="${surveysettings.sadTextComplete}">
	
	<div class="container">

		<!-- Starting code for Encompass details -->
		<c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<form id="encompass-form">
				<div class="um-top-container">
					<div class="um-header"><spring:message code="label.header.encompass.configuration.key" /></div>
					<div class="clearfix um-panel-content">
						<div class="row">
							<div class="um-top-row cleafix">
								<div class="clearfix um-top-form-wrapper">
									<!-- set encompass details -->
									<c:if test="${accountSettings != null && accountSettings.crm_info != null}">
										<c:set var="encompassusername" value="${accountSettings.crm_info.crm_username}"/>
										<c:set var="encompasspassword" value="${accountSettings.crm_info.crm_password}"/>
										<c:set var="encompassurl" value="${accountSettings.crm_info.url}"/>
									</c:if>
									<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
										<div class="hm-item-row item-row-OR clearfix">
											<div class="um-item-row-left text-right"><spring:message code="label.encompass.username.key" /></div>
											<div class="clearfix float-right st-username-icons">
												<div class="um-item-row-icon margin-left-0"></div>
												<div class="um-item-row-icon margin-left-0"></div>
											</div>
											<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj">
												<input id="encompass-username" type="text" class="um-item-row-txt um-item-row-txt-OR" placeholder="Username" name="encompass-username" value="${encompassusername}">
												<div id="encompass-username-error" class="hm-item-err-2"></div>
											</div>
										</div>
									</div>
									<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item overflow-hidden">
										<div class="hm-item-row item-row-OR clearfix">
											<div class="um-item-row-left text-right"><spring:message code="label.encompass.password.key" /></div>
											<div class="clearfix float-right st-password-icons">
												<div class="um-item-row-icon margin-left-0"></div>
												<div class="um-item-row-icon margin-left-0"></div>
											</div>
											<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj">
												<input id="encompass-password" type="password" class="um-item-row-txt um-item-row-txt-OR" placeholder="Password" name="encompass-password"  value="${encompasspassword}">
												<div id="encompass-password-error" class="hm-item-err-2"></div>
											</div>
										</div>
									</div>
									<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
										<div class="hm-item-row item-row-OR clearfix">
											<div class="um-item-row-left text-right"><spring:message code="label.encompass.url.key" /></div>
											<div class="clearfix float-right st-url-icons">
												<div id="encompass-testconnection" class="encompass-testconnection-adj um-item-row-icon icn-spanner margin-left-0 cursor-pointer"></div>
												<div id="encompass-save" class="um-item-row-icon icn-blue-tick margin-left-0 cursor-pointer"></div>
											</div>
											<div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj">
												<input id="encompass-url" type="text" class="encompass-url-adj um-item-row-txt um-item-row-txt-OR" placeholder="URL" name="encompass-url" value="${encompassurl}">
												<div id="encompass-url-error" class="hm-item-err-2"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</c:if>
		
		<!-- Starting code for Autopost Score -->
		<c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="um-top-container">
				<div class="um-header margin-top-25"><spring:message code="label.scorepost.key" /></div>
				<div class="clearfix st-score-wrapper">
					<div class="float-left st-score-txt"><spring:message code="label.scorepost.desc.key" /></div>
					<form id="rating-settings-form">
						<input type="hidden" name="ratingcategory" id="ratingcategory">
						<div class="clearfix float-right st-score-rt">
						<div class="float-left score-rt-post score-rt-post-OR score-rt-min">
							<div class="st-score-rt-top"><spring:message code="label.scorepost.min.key" /></div>
							<div class="st-score-rt-line2 clearfix">
								<div class="st-rating-wrapper float-left clearfix" id="rating-min-post-parent">
									<div class="rating-star icn-full-star"></div>
									<div class="rating-star icn-full-star"></div>
									<div class="rating-star icn-half-star"></div>
									<div class="rating-star icn-no-star"></div>
									<div class="rating-star icn-no-star"></div>
								</div>
								<div class="st-rating-txt float-left">
									<!-- set the min rating -->
									<c:if test="${accountSettings != null && accountSettings.survey_settings!= null && accountSettings.survey_settings.show_survey_above_score != null}">
										<c:set var="minpostscore" value="${accountSettings.survey_settings.show_survey_above_score}"/>
									</c:if>
									<input type="text" name="rating-min-post" id="rating-min-post" class="st-item-row-txt cursor-pointer dd-arrow-dn" autocomplete="off" value="${minpostscore}">
									<div class="st-dd-wrapper hide" id="st-dd-wrapper-min-post"></div>
								</div>
							</div>
							<div>
								<div id="atpst-chk-box" class="float-left bd-check-img"></div>
								<input type="hidden" id="at-pst-cb" name="autopost" value="true">
								<div class="float-left bd-check-txt">Allow user to autopost</div>
							</div>
						</div>
					</div>
					</form>
				</div>
			</div>
		</c:if>
		
		<!-- Starting code for Text for Happy/Neutral/Sad flow -->
		<c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="um-top-container">
				<div class="um-header  margin-top-25"><spring:message code="label.flow.text.key" /></div>
				<div class="um-header-detail"><spring:message code="label.flow.desc.text.key" /></div>
				<div class="clearfix um-panel-content">
					<div class="bd-mcq-row clearfix txtareaRow">
						<div class="float-left cs-gq-lbl"><spring:message code="label.flow.happy.label.text" /></div>
						<textarea id="happy-text" class="float-left textarea-bd-mcq-txt" style=""></textarea>
					</div>
					
					<div class="bd-mcq-row clearfix txtareaRow">
						<div class="float-left cs-gq-lbl"><spring:message code="label.flow.ok.label.text" /></div>
						<textarea id="neutral-text" class="float-left textarea-bd-mcq-txt" style=""></textarea>
					</div>
					
					<div class="bd-mcq-row clearfix txtareaRow">
						<div class="float-left cs-gq-lbl"><spring:message code="label.flow.sad.label.text" /></div>
						<textarea id="sad-text" class="float-left textarea-bd-mcq-txt" style=""></textarea>
					</div>
				</div>
				<div class="um-gateway-cont">
					<div class="um-header-detail"><spring:message code="label.complete.desc.text.key" /></div>
					<div class="clearfix um-panel-content">
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl"><spring:message code="label.complete.happy.label.text" /></div>
							<textarea id="happy-text-complete" class="float-left textarea-bd-mcq-txt" style=""></textarea>
						</div>
						
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl"><spring:message code="label.complete.ok.label.text" /></div>
							<textarea id="neutral-text-complete" class="float-left textarea-bd-mcq-txt" style=""></textarea>
						</div>
						
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl"><spring:message code="label.complete.sad.label.text" /></div>
							<textarea id="sad-text-complete" class="float-left textarea-bd-mcq-txt" style=""></textarea>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		
		<!-- Starting code for Social Authentication -->
		<c:if test="${profilemasterid == 1}">
			<c:set var="containerclass" value="um-top-container"/>
		</c:if>
		<div class="${containerclass}">
			<div class="um-header margin-top-25"><spring:message code="label.socialconnect.key" /></div>
			<div class="clearfix st-score-wrapper">
				<div class="float-left st-score-txt"><spring:message code="label.socialconnect.desc.key" /></div>
				<input type="hidden" name="ratingcategory" id="ratingcategory">
				<div class="clearfix float-right st-score-rt">
					<div class="soc-nw-wrapper clearfix">
						<div class="float-left soc-nw-icns cursor-pointer icn-wide-fb soc-nw-adj" onclick="openAuthPage('facebook');"></div>
						<!-- <div class="float-left soc-nw-icns cursor-pointer icn-wide-gplus" onclick="openAuthPage('google');"></div> -->
						<div class="float-left soc-nw-icns cursor-pointer icn-wide-twitter" onclick="openAuthPage('twitter');"></div>
						<!-- <div class="float-left soc-nw-icns cursor-pointer icn-wide-rss" onclick="openAuthPage('rss');"></div> -->
						<div class="float-left soc-nw-icns cursor-pointer icn-wide-linkedin soc-nw-adj" onclick="openAuthPage('linkedin');"></div>
						<!-- <div class="float-left soc-nw-icns cursor-pointer icn-wide-yelp" onclick="openAuthPage('yelp');"></div> -->
						<div class="float-left soc-nw-icns cursor-pointer icn-wide-gplus" onclick="openAuthPage('google');"></div>
					</div>
				</div>
			</div>
		</div>
		
		<!-- Starting code for Mail text -->
		<c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="um-top-container">
				<form id="mail-body-settings-form">
					<input type="hidden" name="mailcategory" id="mailcategory">
					<div class="um-header margin-top-25"><spring:message code="label.header.email.configuration.key" /></div>
					<div class="clearfix st-bottom-wrapper margin-top-50">
					<div class="clearfix">Legends:&nbsp&nbsp&nbsp&nbsp[Name]:Customer's Name,&nbsp&nbsp&nbsp&nbsp[AgentName] : Agent's Name,&nbsp&nbsp&nbsp&nbsp[Link] : Url of the survey</div>
						<div class="st-header-txt-lft-rt clearfix margin-top-25">
							<div class="float-left st-header-txt-lft"><spring:message code="label.header.mailer.content.key" /></div>
							<div class="float-right clearfix st-header-txt-rt">
								<div id="edit-participation-mail-content" class="float-left st-header-txt-rt-icn icn-pen cursor-pointer icn-pen-blue"></div>
								<div id="edit-participation-mail-content-disabled" class="float-left st-header-txt-rt-icn icn-pen hide"></div>
								
								<div id="save-participation-mail-content" class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20 cursor-pointer hide"></div>
								<div id="save-participation-mail-content-disabled" class="float-left st-header-txt-rt-icn margin-left-20 icn-grey-tick"></div>
							</div>
						</div>
						<div class="st-header-txt-wrapper">
							<textarea id="survey-participation-mailcontent" name="survey-participation-mailcontent" class="st-header-txt-input">${surveymailbody}</textarea>
						</div>
					</div>
					<div class="clearfix st-bottom-wrapper margin-top-50">
						<div class="st-header-txt-lft-rt clearfix margin-top-25">
							<div class="float-left st-header-txt-lft"><spring:message code="label.header.reminder.mailer.content.key" /></div>
							<div class="float-right clearfix st-header-txt-rt">
								<div id="edit-participation-reminder-mail-content" class="float-left st-header-txt-rt-icn icn-pen cursor-pointer icn-pen-blue"></div>
								<div id="edit-participation-reminder-mail-content-disabled" class="float-left st-header-txt-rt-icn icn-pen hide"></div>
								
								<div id="save-participation-reminder-mail-content" class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20 cursor-pointer hide"></div>
								<div id="save-participation-reminder-mail-content-disabled" class="float-left st-header-txt-rt-icn margin-left-20 icn-grey-tick"></div>
							</div>
						</div>
						<div class="st-header-txt-wrapper">
							<textarea id="survey-participation-reminder-mailcontent" name="survey-participation-reminder-mailcontent" class="st-header-txt-input">${surveyremindermailbody}</textarea>
						</div>
					</div>
					
					<!-- set the mail body details -->
					<c:if test="${accountSettings != null && accountSettings.survey_settings!= null}">
						<c:set var="reminderinterval" value="${accountSettings.survey_settings.survey_reminder_interval_in_days}" />
						<c:set var="isreminderdisabled" value="${accountSettings.survey_settings.isReminderDisabled}"/>
					</c:if>
					<div class="clearfix st-bottom-wrapper st-reminder-wrapper">
						<div class="float-left"><spring:message code="label.reminder.interval.key" /></div>
						<div class="clearfix float-left">
							<div class="float-left st-input-reminder">
								<input class="st-rating-input" name="reminder-interval" id="reminder-interval" value="${reminderinterval}">
								<div id="reminder-interval-error" class="hm-item-err-2"></div>
							</div>
							<div class="float-left"><spring:message code="label.days.key" /></div>
						</div>
						<div class="clearfix st-check-main float-left">
							<div class="float-left st-check-wrapper">
								<input type="hidden" name="reminder-needed-hidden" id="reminder-needed-hidden" value="${isreminderdisabled}">
								<div id="st-reminder-on" class="st-checkbox st-checkbox-on hide"></div>
								<div id="st-reminder-off" class="st-checkbox st-checkbox-off"></div>
							</div>
							<div class="float-left st-check-txt-OR"><spring:message code="label.noreminder.key" /></div>
						</div>
					</div>
				</form>
			</div>
		</c:if>
		
		<!-- Starting code for Other settings -->
		<c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="um-top-container border-0">
				<div class="um-header margin-top-25"><spring:message code="label.othersettings.key" /></div>
				<form id="other-settings-form">
					<div class="st-others-wrapper clearfix">
						<input type="hidden" name="othercategory" id="othercategory">
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12 st-settings-tab">
							<div class="clearfix st-settings-item-wrapper">
								<div class="float-left st-settings-check-wrapper">
									<!-- set the min rating -->
									<c:if test="${accountSettings != null && accountSettings.isLocationEnabled != null}">
										<c:set var="islocationenabled" value="${accountSettings.isLocationEnabled}"/>
									</c:if>
									<input type="hidden" name="other-location" id="other-location" value="${islocationenabled}">
									<div id="st-settings-location-on" class="st-checkbox st-settings-checkbox st-checkbox-on"></div>
									<div id="st-settings-location-off" class="st-checkbox st-settings-checkbox st-checkbox-off hide"></div>
								</div>
								<div class="float-left st-check-txt-OR"><spring:message code="label.enable.location.key" /></div>
							</div>
							<div class="st-settings-text">Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay </div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
							<div class="clearfix st-settings-item-wrapper">
								<div class="float-left st-settings-check-wrapper">
									<c:if test="${accountSettings != null && accountSettings.isAccountDisabled != null}">
										<c:set var="isaccountdisabled" value="${accountSettings.isAccountDisabled}"/>
									</c:if>
									<input type="hidden" name="other-account" id="other-account" value="${isaccountdisabled}">
									<div id="st-settings-account-on" class="st-checkbox st-settings-checkbox st-checkbox-on hide"></div>
									<div id="st-settings-account-off" class="st-checkbox st-settings-checkbox st-checkbox-off"></div>
								</div>
								<div class="float-left st-check-txt-OR"><spring:message code="label.disable.account.key" /></div>
							</div>
							<div class="st-settings-text"><spring:message code="label.disable.account.des.key" /><span class="accounts-email"><spring:message code="label.account.email.key" /></span></div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
							<div class="clearfix st-settings-item-wrapper">
							   <div class="float-left st-settings-check-wrapper">
									<div id="st-settings-payment-on" class="st-checkbox st-settings-checkbox st-checkbox-on hide"></div>
									<div id="st-settings-payment-off" class="st-checkbox st-settings-checkbox st-checkbox-off"></div>
								</div>
								<div class="float-left st-check-txt-OR" id="st-chg-payment-info"><spring:message code="label.change.payment.key" /></div>
							</div>
							<div class="st-settings-text"><spring:message code="label.change.payment.desc.key" /></div>
						</div>
					</div>
				</form>
			</div>
		</c:if>
		
	</div>
</div>

<style>
	.body-no-scroll{
			overflow: hidden;
	}
</style>

<script src="${pageContext.request.contextPath}/resources/js/settings.js"></script>
<script>
$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "Edit Settings");
	
	var profileMasterId = "${profilemasterid}";
	var accountMasterId = "${accountMasterId}";
	if (profileMasterId == 1 || accountMasterId == 1) {
		$('#survey-participation-mailcontent').ckeditor();
		$('#survey-participation-mailcontent').ckeditorGet().config.readOnly = true;
		
		$('#survey-participation-reminder-mailcontent').ckeditor();
		$('#survey-participation-reminder-mailcontent').ckeditorGet().config.readOnly = true;
		
		autoAppendRatingDropdown('#st-dd-wrapper-min-post', "st-dd-item st-dd-item-min-post");
		changeRatingPattern($('#rating-min-post').val(), $('#rating-min-post-parent'));
		$('#rating-min-post').click(function(){
			$('#st-dd-wrapper-min-post').slideToggle(200);
		});
		
		autoSetCheckboxStatus('#st-settings-location-on', '#st-settings-location-off', '#other-location');
		autoSetCheckboxStatus('#st-settings-account-on', '#st-settings-account-off', '#other-account');
		autoSetCheckboxStatus('#st-reminder-on', '#st-reminder-off', '#reminder-needed-hidden');
		autoSetReminderIntervalStatus();
		
		paintTextForMood($('#hm-main-content-wrapper').attr("data-hpy"), 
			$('#hm-main-content-wrapper').attr("data-nutl"),
			$('#hm-main-content-wrapper').attr("data-sad"),
			$('#hm-main-content-wrapper').attr("data-hpy-compl"), 
			$('#hm-main-content-wrapper').attr("data-nutl-compl"),
			$('#hm-main-content-wrapper').attr("data-sad-compl"));
	}
	
	// Settings View as
	$('body').on('click','#setting-sel',function(e){
		e.stopPropagation();
		$('#se-dd-wrapper-profiles').slideToggle(200);
	});
	
	$('body').on('click','.se-dd-item',function(e) {
		var newProfileId = $(this).data('profile-id');
		
		$('#setting-sel').html($(this).html());
		$('#se-dd-wrapper-profiles').slideToggle(200);
		
		showMainContent('./showcompanysettings.do?profileId=' + newProfileId);
	});
	
	$('body').click(function() {
		if ($('#se-dd-wrapper-profiles').css('display') == "block") {
			$('#se-dd-wrapper-profiles').toggle();
		}
	});
	
	$('#encompass-username').blur(function() {
		validateEncompassUserName(this.id);
	});
	$('#encompass-password').blur(function() {
		validateEncompassPassword(this.id);
	});
	$('#encompass-url').blur(function() {
		validateURL(this.id);
	});
	$('body').on('click','#encompass-save',function() {
		if(validateEncompassInput('encompass-form-div')) {
			saveEncompassDetails("encompass-form");
		}
	}); 
	$('body').on('click','#encompass-testconnection',function() {
		if(validateEncompassInput('encompass-form-div')) {
			testEncompassConnection("encompass-form");
		}
	});

	$('body').on('click','.st-dd-item-auto-post',function() {
		$('#rating-auto-post').val($(this).html());
		$('#st-dd-wrapper-auto-post').slideToggle(200);

		$('#ratingcategory').val('rating-auto-post');
		var rating = $('#rating-auto-post').val();
		var ratingParent = $('#rating-auto-post-parent');

		changeRatingPattern(rating, ratingParent);
		updatePostScore("rating-settings-form");
	});

	$('body').on('click','.st-dd-item-min-post',function() {
		$('#rating-min-post').val($(this).html());
		$('#st-dd-wrapper-min-post').slideToggle(200);
		
		$('#ratingcategory').val('rating-min-post');
		
		var rating = $('#rating-min-post').val();
		var ratingParent = $('#rating-min-post-parent');
		changeRatingPattern(rating, ratingParent);
		
		updatePostScore("rating-settings-form");
	});

	$('#edit-participation-mail-content').click(function() {
		$('#survey-participation-mailcontent').ckeditorGet().setReadOnly(false);
		
		$('#save-participation-mail-content').show();
		$('#save-participation-mail-content-disabled').hide();
		
		$('#edit-participation-mail-content-disabled').show();
		$(this).hide();
	});
	$('#save-participation-mail-content').click(function() {
		$('#mailcategory').val('participationmail');
		updateMailContent("mail-body-settings-form");
		$('#survey-participation-mailcontent').ckeditorGet().setReadOnly(true);
		
		$(this).hide();
		$('#save-participation-mail-content-disabled').show();

		$('#edit-participation-mail-content').show();
		$('#edit-participation-mail-content-disabled').hide();
	});


	$('#edit-participation-reminder-mail-content').click(function() {
		$('#survey-participation-reminder-mailcontent').ckeditorGet().setReadOnly(false);
		
		$('#save-participation-reminder-mail-content').show();
		$('#save-participation-reminder-mail-content-disabled').hide();
		
		$('#edit-participation-reminder-mail-content-disabled').show();
		$(this).hide();
	});
	$('#save-participation-reminder-mail-content').click(function() {
		$('#mailcategory').val('participationremindermail');
		updateMailContent("mail-body-settings-form");
		$('#survey-participation-reminder-mailcontent').ckeditorGet().setReadOnly(true);
		
		$(this).hide();
		$('#save-participation-reminder-mail-content-disabled').show();

		$('#edit-participation-reminder-mail-content').show();
		$('#edit-participation-reminder-mail-content-disabled').hide();
	});


	$('#reminder-interval').change(function() {
		$('#mailcategory').val('reminder-interval');
		if(validateReminderInterval('reminder-interval')) {
			updateReminderSettings("mail-body-settings-form");
		}
	});

	$('#st-reminder-on').click(function() {
		$('#mailcategory').val('reminder-needed');
		
		$('#reminder-needed-hidden').val('false');
		$('#st-reminder-off').show();
		$(this).hide();

		$('#reminder-interval').removeAttr("disabled");
		updateReminderSettings("mail-body-settings-form");
	});
	$('#st-reminder-off').click(function() {
		$('#mailcategory').val('reminder-needed');

		$('#reminder-needed-hidden').val('true');
		$('#st-reminder-on').show();
		$(this).hide();
		
		$('#reminder-interval').attr("disabled", true);
		updateReminderSettings("mail-body-settings-form");
	});


	$('#st-settings-location-on').click(function() {
		$('#othercategory').val('other-location');
		$('#other-location').val('false');
		
		$('#st-settings-location-off').show();
		$(this).hide();
		
		updateOtherSettings("other-settings-form");
	});
	$('#st-settings-location-off').click(function() {
		$('#othercategory').val('other-location');
		$('#other-location').val('true');

		$('#st-settings-location-on').show();
		$(this).hide();
		
		updateOtherSettings("other-settings-form");
	});


	$('#st-settings-payment-on').click(function() {
		$('#st-settings-payment-off').show();
		$(this).hide();
	});
	$('#st-settings-payment-off').click(function() {
		$('#st-settings-payment-on').show();
		$(this).hide();
		showPaymentOptions();
	});


	$('#st-settings-account-on').click(function() {
		$('#other-account').val('false');
		createPopupConfirm("Enable Account", "Do you want to Continue?");
		overlayAccount();
	});
	$('#st-settings-account-off').click(function() {
		$('#other-account').val('true');
		createPopupConfirm("Disable Account", "You will not be able to access your SocialSurvey profile after the current billing cycle. Also for Branch or Company Accounts, this will disable all accounts in your hierarchy under this account.<br/> Do you want to Continue?");
		overlayAccount();
	});

	$('#happy-text').blur(function() {
		saveTextForMoodFlow($("#happy-text").val(), "happy");
	});
	$('#neutral-text').blur(function() {
		saveTextForMoodFlow($("#neutral-text").val(), "neutral");
	});
	$('#sad-text').blur(function() {
		saveTextForMoodFlow($("#sad-text").val(), "sad");
	});

	$('#happy-text-complete').blur(function() {
		saveTextForMoodFlow($("#happy-text").val(), "happyComplete");
	});
	$('#neutral-text-complete').blur(function() {
		saveTextForMoodFlow($("#neutral-text").val(), "neutralComplete");
	});
	$('#sad-text-complete').blur(function() {
		saveTextForMoodFlow($("#sad-text").val(), "sadComplete");
	});

	$('#atpst-chk-box').click(function() {
		if ($('#atpst-chk-box').hasClass('bd-check-img-checked')) {
			$('#atpst-chk-box').removeClass('bd-check-img-checked');
			updateAutoPostSetting(true);
		} else {
			$('#atpst-chk-box').addClass('bd-check-img-checked');
			updateAutoPostSetting(false);
		}
	});
});
</script>