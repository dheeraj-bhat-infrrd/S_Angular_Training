<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty accountSettings && not empty accountSettings.survey_settings}">
	<c:set value="${accountSettings.survey_settings}" var="surveysettings"></c:set>
</c:if>
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

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left"><spring:message code="label.title.settings.key" /></div>
			<c:if test="${not empty assignments}">
				<div id="se-dd-wrapper" class="float-right header-right clearfix hr-dsh-adj-rt hdr-prof-sel">
					<div class="float-left hr-txt1"><spring:message code="label.viewas.key" /></div>
					<div id="setting-sel" class="float-left hr-txt2 cursor-pointer">${entityName}</div>
					<div id="se-dd-wrapper-profiles" class="va-dd-wrapper hide">
						<c:forEach var="company" items="${assignments.companies}">
							<div class="se-dd-item" data-column-type="companyId"
								data-column-name="${company.value}"
								data-column-value="${company.key}">${company.value}</div>
						</c:forEach>
						<c:forEach var="region" items="${assignments.regions}">
							<div class="se-dd-item" data-column-type="regionId" 
								data-column-name="${region.value}"
								data-column-value="${region.key}">${region.value}</div>
						</c:forEach>
						<c:forEach var="branch" items="${assignments.branches}">
							<div class="se-dd-item" data-column-type="branchId"
								data-column-name="${branch.value}"
								data-column-value="${branch.key}">${branch.value}</div>
						</c:forEach>
						<c:forEach var="agent" items="${assignments.agents}">
							<div class="se-dd-item" data-column-type="agentId"
								data-column-name="${agent.value}"
								data-column-value="${agent.key}">${agent.value}</div>
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

		<!-- Starting code for CRM details -->
		<c:if test="${profilemasterid == 1}">
			<!-- Select which CRM jsp to include -->
			<c:if test="${not empty crmMappings }">
				<form id="encompass-form">
					<div class="um-top-container">
						<c:choose>
						<c:when test="${fn:length(crmMappings) gt 1}">
							<div class="um-header crm-setting-hdr crm-settings-dropdown">
								<span id="crm-settings-dropdown-sel-text">${crmMappings[0].crmMaster.crmName }</span> Settings</div>
							<div class="hide crm-settings-dropdown-cont va-dd-wrapper">
								<c:forEach items="${crmMappings }" var="mapping">
									<div class="crm-settings-dropdown-item"
										data-crm-type="${mapping.crmMaster.crmName }">${mapping.crmMaster.crmName }</div>
								</c:forEach>
							</div>
						</c:when>
						<c:otherwise>
							<div class="um-header crm-setting-hdr">
								${crmMappings[0].crmMaster.crmName } Settings</div>
						</c:otherwise>
						</c:choose>
						<c:forEach items="${crmMappings }" var="mapping" varStatus="loop">
							<c:choose>
								<c:when test="${loop.index gt 0}">
									<c:set var="hideClass" value="hide"></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="hideClass" value=""></c:set>
								</c:otherwise>
							</c:choose>
							<div class="crm-setting-cont ${hideClass}" data-crm-type="${mapping.crmMaster.crmName }">
								<c:if test="${mapping.crmMaster.crmName == 'Encompass'}">
									<jsp:include page="encompass.jsp"></jsp:include>
								</c:if>
							</div>
						</c:forEach>
					</div>
				</form>
			</c:if>
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
								<div class="st-rating-wrapper settings-rating-wrapper float-left clearfix" id="rating-min-post-parent">
									<div class="rating-star icn-full-star"></div>
									<div class="rating-star icn-full-star"></div>
									<div class="rating-star icn-half-star"></div>
									<div class="rating-star icn-no-star"></div>
									<div class="rating-star icn-no-star"></div>
								</div>
								<div class="st-rating-txt float-left">
									<!-- set the min rating -->
									<c:if test="${accountSettings != null && accountSettings.survey_settings!= null
										&& accountSettings.survey_settings.show_survey_above_score != null}">
										<c:set var="minpostscore" value="${accountSettings.survey_settings.show_survey_above_score}"/>
									</c:if>
									<input type="text" name="rating-min-post" id="rating-min-post" class="st-item-row-txt cursor-pointer dd-arrow-dn" autocomplete="off" value="${minpostscore}">
									<div class="st-dd-wrapper hide" id="st-dd-wrapper-min-post"></div>
								</div>
							</div>
							<div>
								<div id="atpst-chk-box" class="float-left bd-check-img"></div>
								<input type="hidden" id="at-pst-cb" name="autopost" value="${autoPostEnabled}">
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
						<div class="float-left reset-icon cursor-pointer"><spring:message code="label.reset.key" /></div>
					</div>
					
					<div class="bd-mcq-row clearfix txtareaRow">
						<div class="float-left cs-gq-lbl"><spring:message code="label.flow.ok.label.text" /></div>
						<textarea id="neutral-text" class="float-left textarea-bd-mcq-txt" style=""></textarea>
						<div class="float-left reset-icon cursor-pointer"><spring:message code="label.reset.key" /></div>
					</div>
					
					<div class="bd-mcq-row clearfix txtareaRow">
						<div class="float-left cs-gq-lbl"><spring:message code="label.flow.sad.label.text" /></div>
						<textarea id="sad-text" class="float-left textarea-bd-mcq-txt" style=""></textarea>
						<div class="float-left reset-icon cursor-pointer"><spring:message code="label.reset.key" /></div>
					</div>
				</div>
				<div class="um-gateway-cont">
					<div class="um-header-detail"><spring:message code="label.complete.desc.text.key" /></div>
					<div class="clearfix um-panel-content">
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl"><spring:message code="label.complete.happy.label.text" /></div>
							<textarea id="happy-text-complete" class="float-left textarea-bd-mcq-txt" style=""></textarea>
							<div class="float-left reset-icon cursor-pointer"><spring:message code="label.reset.key" /></div>
						</div>
						
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl"><spring:message code="label.complete.ok.label.text" /></div>
							<textarea id="neutral-text-complete" class="float-left textarea-bd-mcq-txt" style=""></textarea>
							<div class="float-left reset-icon cursor-pointer"><spring:message code="label.reset.key" /></div>
						</div>
						
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl"><spring:message code="label.complete.sad.label.text" /></div>
							<textarea id="sad-text-complete" class="float-left textarea-bd-mcq-txt" style=""></textarea>
							<div class="float-left reset-icon cursor-pointer"><spring:message code="label.reset.key" /></div>
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
				<div class="float-left st-social-score col-lg-4 col-md-4 col-sm-4 col-xs-12">
					<spring:message code="label.socialconnect.desc.key" />
				</div>
				<input type="hidden" name="ratingcategory" id="ratingcategory">
				<div class="clearfix float-right col-lg-8 col-md-8 col-sm-8 col-xs-12">
					<div id="social-media-token-cont" class="soc-nw-wrapper clearfix">
						<%-- <jsp:include page="settings_socialauth.jsp"></jsp:include> --%>
					</div>
				</div>
			</div>
		</div>
		
		<!-- Starting code for Mail text -->
		<c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="um-top-container" style="padding-bottom: 0">
				<form id="mail-body-settings-form">
					<input type="hidden" name="mailcategory" id="mailcategory">
					<div class="um-header margin-top-25"><spring:message code="label.header.email.configuration.key" /></div>
					<div class="clearfix st-bottom-wrapper margin-top-50">
						<div class="clearfix">Legends: <br />
							&nbsp&nbsp&nbsp&nbsp[BaseUrl] : Url of the SocialSurvey,
							&nbsp&nbsp&nbsp&nbsp[LogoUrl] : Url of the SocialSurvey Logo,
							&nbsp&nbsp&nbsp&nbsp[Link] : Url of the survey,
							<br />
							&nbsp&nbsp&nbsp&nbsp[Name] : Customer Name,
							&nbsp&nbsp&nbsp&nbsp[FirstName] : Customer First Name,
							&nbsp&nbsp&nbsp&nbsp[AgentName] : Agent Name,
							&nbsp&nbsp&nbsp&nbsp[AgentSignature] : Agent's Signature,
							<br />
							&nbsp&nbsp&nbsp&nbsp[RecipientEmail] : Receipient's Email,
							&nbsp&nbsp&nbsp&nbsp[SenderEmail] : Sender's Email,
							&nbsp&nbsp&nbsp&nbsp[CompanyName] : Company Name,
							<br />
							&nbsp&nbsp&nbsp&nbsp[InitiatedDate] : Survey Initiated On,
							&nbsp&nbsp&nbsp&nbsp[CurrentYear] : Current Year,
							&nbsp&nbsp&nbsp&nbsp[FullAddress] : Address of SocialSurvey
							<br />
							<br />
							Note: Only [AgentName] is allowed in Mail Subject
						</div>
						<div class="st-header-txt-lft-rt clearfix margin-top-25">
							<div class="float-left st-header-txt-lft"><spring:message code="label.header.mailer.content.key" /></div>
							<div class="float-right clearfix st-header-txt-rt">
								<div class="clearfix">
									<div id="edit-participation-mail-content"
										class="float-left st-header-txt-rt-icn icn-pen cursor-pointer icn-pen-blue"></div>
									<div id="edit-participation-mail-content-disabled"
										class="float-left st-header-txt-rt-icn icn-pen hide"></div>

									<div id="save-participation-mail-content"
										class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20 cursor-pointer hide"></div>
									<div id="save-participation-mail-content-disabled"
										class="float-left st-header-txt-rt-icn margin-left-20 icn-grey-tick"></div>
										
									<div id="revert-participation-mail"
										class="float-left st-header-txt-rt-icn margin-left-20 cursor-pointer icn-blue-ellipse"></div>
								</div>
								<div class="clearfix">
									<div class="float-left settings-btn-text">Edit</div>
									<div class="float-left settings-btn-text margin-left-20">Save</div>
									<div class="float-left settings-btn-text margin-left-20">Reset</div>
								</div>
							</div>
						</div>
						<div class="st-subject-cont clearfix">
							<div class="st-subject-label float-left"><spring:message code="label.subject.mailer.text" /></div>
							<div class="st-subject-input-cont float-left">
								<input type="text" id="survey-mailcontent-subject" name="survey-mailcontent-subject"
									class="st-subject-input" value="${surveymailsubject}" readonly>
							</div>
						</div>
						<div class="st-header-txt-wrapper">
							<textarea id="survey-participation-mailcontent" name="survey-participation-mailcontent"
								class="st-header-txt-input">${surveymailbody}</textarea>
						</div>
					</div>
					<div class="clearfix st-bottom-wrapper margin-top-50">
						<div class="st-header-txt-lft-rt clearfix margin-top-25">
							<div class="float-left st-header-txt-lft"><spring:message code="label.header.reminder.mailer.content.key" /></div>
							<div class="float-right clearfix st-header-txt-rt">
								<div class="clearfix">
									<div id="edit-participation-reminder-mail-content"
										class="float-left st-header-txt-rt-icn icn-pen cursor-pointer icn-pen-blue"></div>
									<div id="edit-participation-reminder-mail-content-disabled"
										class="float-left st-header-txt-rt-icn icn-pen hide"></div>

									<div id="save-participation-reminder-mail-content"
										class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20 cursor-pointer hide"></div>
									<div id="save-participation-reminder-mail-content-disabled"
										class="float-left st-header-txt-rt-icn margin-left-20 icn-grey-tick"></div>
										
									<div id="revert-participation-reminder-mail"
										class="float-left st-header-txt-rt-icn margin-left-20 cursor-pointer icn-blue-ellipse"></div>
								</div>
								<div class="clearfix">
									<div class="float-left settings-btn-text">Edit</div>
									<div class="float-left settings-btn-text margin-left-20">Save</div>
									<div class="float-left settings-btn-text margin-left-20">Reset</div>
								</div>
							</div>
						</div>
						<div class="st-subject-cont clearfix">
							<div class="st-subject-label float-left"><spring:message code="label.subject.reminder.text" /></div>
							<div class="st-subject-input-cont float-left">
								<input type="text" id="survey-mailreminder-subject" name="survey-mailreminder-subject"
									class="st-subject-input" value="${surveyremindermailsubject}" readonly>
							</div>
						</div>
						<div class="st-header-txt-wrapper">
							<textarea id="survey-participation-reminder-mailcontent" name="survey-participation-reminder-mailcontent"
								class="st-header-txt-input">${surveyremindermailbody}</textarea>
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
				<div class="um-header margin-top-10"><spring:message code="label.othersettings.key" /></div>
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
									<div id="st-delete-account" class="st-checkbox st-settings-checkbox st-checkbox-off"></div>
									<!-- <div id="st-settings-location-on" class="st-checkbox st-settings-checkbox st-checkbox-on"></div>
									<div id="st-settings-location-off" class="st-checkbox st-settings-checkbox st-checkbox-off hide"></div> -->
								</div>
								<div class="float-left st-check-txt-OR"><spring:message code="label.delete.account.key" /></div>
							</div>
							<div class="st-settings-text"><spring:message code="label.delete.account.desc.key" /></div>
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
								<div class="float-left st-check-txt-OR" id="st-chg-payment-info">
									<spring:message code="label.change.payment.key" />
								</div>
							</div>
							<div class="st-settings-text"><spring:message code="label.change.payment.desc.key" /></div>
						</div>
					</div>
				</form>
			</div>
		</c:if>
		
	</div>
</div>
<div style="display: none"><form id='deleteAccountForm' action="./deletecompany.do" method="get"></form></div>
<style>
	.body-no-scroll{
			overflow: hidden;
	}
</style>

<script>
$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "Edit Settings");
	
	//social media urls
	loadSocialMediaUrlInSettingsPage();
	
	if ($("#se-dd-wrapper-profiles > div").length <= 1) {
		$('#se-dd-wrapper').remove();
	}
	
	if("${autoPostEnabled}" == "false"){
		$('#atpst-chk-box').addClass('bd-check-img-checked');
	}
	
	var profileMasterId = "${profilemasterid}";
	var accountMasterId = "${accountMasterId}";
	if (profileMasterId == 1 || accountMasterId == 1) {
		try {
			$('#survey-participation-mailcontent').ckeditor();
			$('#survey-participation-mailcontent').ckeditorGet().config.readOnly = true;
			
			$('#survey-participation-reminder-mailcontent').ckeditor();
			$('#survey-participation-reminder-mailcontent').ckeditorGet().config.readOnly = true;
		} catch(e) {
			console.log("ckeditor not supported for the environment");
		}
		
		autoAppendRatingDropdown('#st-dd-wrapper-min-post', "st-dd-item st-dd-item-min-post");
		changeRatingPattern($('#rating-min-post').val(), $('#rating-min-post-parent'));
		$('#rating-min-post').click(function(){
			$('#st-dd-wrapper-min-post').slideToggle(200);
		});
		
		autoSetCheckboxStatus('#st-settings-location-on', '#st-settings-location-off', '#other-location');
		autoSetCheckboxStatus('#st-settings-account-on', '#st-settings-account-off', '#other-account');
		autoSetCheckboxStatus('#st-reminder-on', '#st-reminder-off', '#reminder-needed-hidden');
		autoSetReminderIntervalStatus();
		var happyTxt=$('#hm-main-content-wrapper').attr("data-hpy");
		if(happyTxt == ""){
			happyTxt = "${defaultSurveyProperties.happyText}";
		}
		var nuTxt=$('#hm-main-content-wrapper').attr("data-nutl");
		if(nuTxt == ""){
			nuTxt = "${defaultSurveyProperties.neutralText}";
		}
		
		var sadTxt=$('#hm-main-content-wrapper').attr("data-sad");
		if(sadTxt == ""){
			sadTxt = "${defaultSurveyProperties.sadText}";
		}
		var happyTxtComplete=$('#hm-main-content-wrapper').attr("data-hpy-compl");
		if(happyTxtComplete == ""){
			happyTxtComplete = "${defaultSurveyProperties.happyTextComplete}";
		}
		var nuTxtComplete=$('#hm-main-content-wrapper').attr("data-nutl-compl");
		if(nuTxtComplete == ""){
			nuTxtComplete = "${defaultSurveyProperties.neutralTextComplete}";
		}
		var sadTxtComplete=$('#hm-main-content-wrapper').attr("data-sad-compl");
		if(sadTxtComplete == ""){
			sadTxtComplete = "${defaultSurveyProperties.sadTextComplete}";
		}
		
		paintTextForMood(happyTxt, nuTxt,sadTxt,happyTxtComplete, nuTxtComplete,sadTxtComplete);		
	}
	
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
		try {
			$('#survey-participation-mailcontent').ckeditorGet().setReadOnly(false);
		} catch(e) {
			console.log("ckeditor not supported for the environment");
		}
		$('#survey-mailcontent-subject').attr("readonly", false);
		
		$('#save-participation-mail-content').show();
		$('#save-participation-mail-content-disabled').hide();
		
		$('#edit-participation-mail-content-disabled').show();
		$(this).hide();
	});
	$('#save-participation-mail-content').click(function() {
		$('#mailcategory').val('participationmail');
		updateMailContent("mail-body-settings-form");
		
		try {
			$('#survey-participation-mailcontent').ckeditorGet().setReadOnly(true);
		} catch(e) {
			console.log("ckeditor not supported for the environment");
		}
		$('#survey-mailcontent-subject').attr("readonly", true);
		
		$(this).hide();
		$('#save-participation-mail-content-disabled').show();

		$('#edit-participation-mail-content').show();
		$('#edit-participation-mail-content-disabled').hide();
	});
	$('#revert-participation-mail').click(function() {
		revertMailContent('participationmail');
	});

	$('#edit-participation-reminder-mail-content').click(function() {
		try {
			$('#survey-participation-reminder-mailcontent').ckeditorGet().setReadOnly(false);
		} catch(e) {
			console.log("ckeditor not supported for the environment");
		}
		$('#survey-mailreminder-subject').attr("readonly", false);
		
		$('#save-participation-reminder-mail-content').show();
		$('#save-participation-reminder-mail-content-disabled').hide();
		
		$('#edit-participation-reminder-mail-content-disabled').show();
		$(this).hide();
	});
	$('#save-participation-reminder-mail-content').click(function() {
		$('#mailcategory').val('participationremindermail');
		updateMailContent("mail-body-settings-form");
		
		try { 
			$('#survey-participation-reminder-mailcontent').ckeditorGet().setReadOnly(true);
		} catch(e) {
			console.log("ckeditor not supported for the environment");
		}
		$('#survey-mailreminder-subject').attr("readonly", true);
		
		$(this).hide();
		$('#save-participation-reminder-mail-content-disabled').show();

		$('#edit-participation-reminder-mail-content').show();
		$('#edit-participation-reminder-mail-content-disabled').hide();
	});
	$('#revert-participation-reminder-mail').click(function() {
		revertMailContent('participationremindermail');
	});
	
	function revertMailContent(mailcategory) {
	    showOverlay();
		var payload = {
			"mailcategory" : mailcategory
		};
		callAjaxPostWithPayloadData('./revertsurveyparticipationmail.do', function (data) {
			showMainContent('./showcompanysettings.do');
			hideOverlay();
			$("#overlay-toast").html(data);
			showToast();
		}, payload, true);
	}

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

	$('#st-delete-account').click(function() {
		$('#other-account').val('true');
		createPopupConfirm("Delete Account",
			"This action cannot be undone.<br/>All user setting will be permanently deleted and your subscription will terminate permanently immediately.");
		overlayDeleteAccount();
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
		saveTextForMoodFlow($("#happy-text-complete").val(), "happyComplete");
	});
	$('#neutral-text-complete').blur(function() {
		saveTextForMoodFlow($("#neutral-text-complete").val(), "neutralComplete");
	});
	$('#sad-text-complete').blur(function() {
		saveTextForMoodFlow($("#sad-text-complete").val(), "sadComplete");
	});

	$('.reset-icon').click(function() {
		var resetId = $(this).prev().attr('id');
		var resetTag = "";
		
		if (resetId == 'happy-text') {
			resetTag = 'happy';
		}
		else if (resetId == 'neutral-text') {
			resetTag = 'neutral';
		}
		else if (resetId == 'sad-text') {
			resetTag = 'sad';
		}
		else if (resetId == 'happy-text-complete') {
			resetTag = 'happyComplete';
		}
		else if (resetId == 'neutral-text-complete') {
			resetTag = 'neutralComplete';
		}
		else if (resetId == 'sad-text-complete') {
			resetTag = 'sadComplete';
		}
		
	    showOverlay();
		resetTextForMoodFlow(resetTag, resetId);
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
	$('body').on('click',function(){
		$('.crm-settings-dropdown-cont').slideUp(200);
	});
	$('.crm-settings-dropdown').on('click',function(e){
		e.stopPropagation();
		$('.crm-settings-dropdown-cont').slideToggle(200);
	});
	$('.crm-settings-dropdown-item').on('click',function(e){
		var crmType = $(this).attr('data-crm-type');
		$('#crm-settings-dropdown-sel-text').text(crmType);
		$('.crm-setting-cont').hide();
		$('.crm-setting-cont[data-crm-type="'+crmType+'"]').show();
	});
});
</script>