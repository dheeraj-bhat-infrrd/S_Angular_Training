<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.emailsettings.key" />
			</div>
		</div>
	</div>
</div>
<div class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">

		<!-- Starting code for Mail text -->
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
						&nbsp&nbsp&nbsp&nbsp[AgentName] : User Name,
						&nbsp&nbsp&nbsp&nbsp[AgentSignature] : User's Signature,
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
				
				
				<!-- survey reminder mail -->
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
								<div class="float-left settings-btn-text"><spring:message code="label.edit.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.save.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.reset.key" /></div>
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
				
				<!-- survey completion mail -->
				
				<div class="clearfix st-bottom-wrapper margin-top-50">
					<div class="st-header-txt-lft-rt clearfix margin-top-25">
						<div class="float-left st-header-txt-lft"><spring:message code="label.header.survey.completion.mailer.content.key" /></div>
						<div class="float-right clearfix st-header-txt-rt">
							<div class="clearfix">
								<div id="edit-survey-completion-mail-content"
									class="float-left st-header-txt-rt-icn icn-pen cursor-pointer icn-pen-blue"></div>
								<div id="edit-survey-completion-mail-content-disabled"
									class="float-left st-header-txt-rt-icn icn-pen hide"></div>
		
								<div id="save-survey-completion-mail-content"
									class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20 cursor-pointer hide"></div>
								<div id="save-survey-completion-mail-content-disabled"
									class="float-left st-header-txt-rt-icn margin-left-20 icn-grey-tick"></div>
									
								<div id="revert-survey-completion-mail"
									class="float-left st-header-txt-rt-icn margin-left-20 cursor-pointer icn-blue-ellipse"></div>
							</div>
							<div class="clearfix">
								<div class="float-left settings-btn-text"><spring:message code="label.edit.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.save.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.reset.key" /></div>
							</div>
						</div>
					</div>
					<div class="st-subject-cont clearfix">
						<div class="st-subject-label float-left"><spring:message code="label.subject.survey.completion.text" /></div>
						<div class="st-subject-input-cont float-left">
							<input type="text" id="survey-completion-subject" name="survey-completion-subject"
								class="st-subject-input" value="${surveycompletionmailsubject}" readonly>
						</div>
					</div>
					<div class="st-header-txt-wrapper">
						<textarea id="survey-completion-mailcontent" name="survey-completion-mailcontent"
							class="st-header-txt-input">${surveycompletionmailbody}</textarea>
					</div>
				</div>
				
				<!-- social post reminder mail -->
				<div class="clearfix st-bottom-wrapper margin-top-50">
					<div class="st-header-txt-lft-rt clearfix margin-top-25">
						<div class="float-left st-header-txt-lft"><spring:message code="label.header.socialpost.reminder.mailer.content.key" /></div>
						<div class="float-right clearfix st-header-txt-rt">
							<div class="clearfix">
								<div id="edit-social-post-reminder-mail-content"
									class="float-left st-header-txt-rt-icn icn-pen cursor-pointer icn-pen-blue"></div>
								<div id="edit-social-post-reminder-mail-content-disabled"
									class="float-left st-header-txt-rt-icn icn-pen hide"></div>
		
								<div id="save-social-post-reminder-mail-content"
									class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20 cursor-pointer hide"></div>
								<div id="save-social-post-reminder-mail-content-disabled"
									class="float-left st-header-txt-rt-icn margin-left-20 icn-grey-tick"></div>
									
								<div id="revert-social-post-reminder-mail"
									class="float-left st-header-txt-rt-icn margin-left-20 cursor-pointer icn-blue-ellipse"></div>
							</div>
							<div class="clearfix">
								<div class="float-left settings-btn-text"><spring:message code="label.edit.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.save.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.reset.key" /></div>
							</div>
						</div>
					</div>
					<div class="st-subject-cont clearfix">
						<div class="st-subject-label float-left"><spring:message code="label.subject.socialpost.reminder.text" /></div>
						<div class="st-subject-input-cont float-left">
							<input type="text" id="social-post-reminder-subject" name="social-post-reminder-subject"
								class="st-subject-input" value="${socialpostremindermailsubject}" readonly>
						</div>
					</div>
					<div class="st-header-txt-wrapper">
						<textarea id="social-post-reminder-mailcontent" name="social-post-reminder-mailcontent"
							class="st-header-txt-input">${socialpostremindermailbody}</textarea>
					</div>
				</div>
				
				<!-- incomplete survey reminder mail -->
				<div class="clearfix st-bottom-wrapper margin-top-50">
					<div class="st-header-txt-lft-rt clearfix margin-top-25">
						<div class="float-left st-header-txt-lft"><spring:message code="label.header.restart.survey.mailer.content.key" /></div>
						<div class="float-right clearfix st-header-txt-rt">
							<div class="clearfix">
								<div id="edit-incomplete-survey-reminder-mail-content"
									class="float-left st-header-txt-rt-icn icn-pen cursor-pointer icn-pen-blue"></div>
								<div id="edit-incomplete-survey-reminder-mail-content-disabled"
									class="float-left st-header-txt-rt-icn icn-pen hide"></div>
		
								<div id="save-incomplete-survey-reminder-mail-content"
									class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20 cursor-pointer hide"></div>
								<div id="save-incomplete-survey-reminder-mail-content-disabled"
									class="float-left st-header-txt-rt-icn margin-left-20 icn-grey-tick"></div>
									
								<div id="revert-incomplete-survey-reminder-mail"
									class="float-left st-header-txt-rt-icn margin-left-20 cursor-pointer icn-blue-ellipse"></div>
							</div>
							<div class="clearfix">
								<div class="float-left settings-btn-text"><spring:message code="label.edit.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.save.key" /></div>
								<div class="float-left settings-btn-text margin-left-20"><spring:message code="label.reset.key" /></div>
							</div>
						</div>
					</div>
					<div class="st-subject-cont clearfix">
						<div class="st-subject-label float-left"><spring:message code="label.subject.restart.survey.text" /></div>
						<div class="st-subject-input-cont float-left">
							<input type="text" id="incomplete-survey-mailreminder-subject" name="incomplete-survey-mailreminder-subject"
								class="st-subject-input" value="${restartsurveymailsubject}" readonly>
						</div>
					</div>
					<div class="st-header-txt-wrapper">
						<textarea id="incomplete-survey-reminder-mailcontent" name="incomplete-survey-reminder-mailcontent"
							class="st-header-txt-input">${restartsurveymailbody}</textarea>
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
	</div>
</div>
<script>
	$(document).ready(function() {
		hideOverlay();
		$(document).attr("title", "Email Settings");
		
		try {
			$('#survey-participation-mailcontent').ckeditor();
			$('#survey-participation-mailcontent').ckeditorGet().config.readOnly = true;
			
			$('#survey-participation-reminder-mailcontent').ckeditor();
			$('#survey-participation-reminder-mailcontent').ckeditorGet().config.readOnly = true;
			
			$('#survey-completion-mailcontent').ckeditor();
			$('#survey-completion-mailcontent').ckeditorGet().config.readOnly = true;
			
			$('#social-post-reminder-mailcontent').ckeditor();
			$('#social-post-reminder-mailcontent').ckeditorGet().config.readOnly = true;
			
			$('#incomplete-survey-reminder-mailcontent').ckeditor();
			$('#incomplete-survey-reminder-mailcontent').ckeditorGet().config.readOnly = true;
		} catch(e) {
		}
		
		$('#edit-participation-mail-content').click(function() {
			try {
				$('#survey-participation-mailcontent').ckeditorGet().setReadOnly(false);
			} catch(e) {
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
			}
			$('#survey-mailcontent-subject').attr("readonly", true);
			
			$(this).hide();
			$('#save-participation-mail-content-disabled').show();

			$('#edit-participation-mail-content').show();
			$('#edit-participation-mail-content-disabled').hide();
		});
		$('#revert-participation-mail').click(function() {
			$('#overlay-main').show();
			$('#overlay-continue').show();
			$('#overlay-continue').html("Reset");
			$('#overlay-cancel').html("Cancel");
			$('#overlay-header').html("Reset Mail Template");
			$('#overlay-text').html("Are you sure you want to reset the mail template ?");
			$('#overlay-continue').attr("onclick", "revertMailContent('participationmail');");
			
		});
		
		//survey reminder mail
		$('#edit-participation-reminder-mail-content').click(function() {
			try {
				$('#survey-participation-reminder-mailcontent').ckeditorGet().setReadOnly(false);
			} catch(e) {
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
			}
			$('#survey-mailreminder-subject').attr("readonly", true);
			
			$(this).hide();
			$('#save-participation-reminder-mail-content-disabled').show();

			$('#edit-participation-reminder-mail-content').show();
			$('#edit-participation-reminder-mail-content-disabled').hide();
		});
		$('#revert-participation-reminder-mail').click(function() {
			$('#overlay-main').show();
			$('#overlay-continue').show();
			$('#overlay-continue').html("Reset");
			$('#overlay-cancel').html("Cancel");
			$('#overlay-header').html("Reset Mail Template");
			$('#overlay-text').html("Are you sure you want to reset the mail template ?");
			$('#overlay-continue').attr("onclick", "revertMailContent('participationremindermail');");
		});
		
		//for survey completion mail
		$('#edit-survey-completion-mail-content').click(function() {
			try {
				$('#survey-completion-mailcontent').ckeditorGet().setReadOnly(false);
			} catch(e) {
			}
			$('#survey-completion-mailcontent-subject').attr("readonly", false);
			
			$('#save-survey-completion-mail-content').show();
			$('#save-survey-completion-mail-content-disabled').hide();
			
			$('#edit-survey-completion-mail-content-disabled').show();
			$(this).hide();
		});
		$('#save-survey-completion-mail-content').click(function() {
			$('#mailcategory').val('surveycompletionmail');
			updateMailContent("mail-body-settings-form");
			
			try {
				$('#survey-completion-mailcontent').ckeditorGet().setReadOnly(true);
			} catch(e) {
			}
			$('#survey-completion-subject').attr("readonly", true);
			
			$(this).hide();
			$('#save-survey-completion-mail-content-disabled').show();

			$('#edit-survey-completion-mail-content').show();
			$('#edit-survey-completion-mail-content-disabled').hide();
		});
		$('#revert-survey-completion-mail').click(function() {
			$('#overlay-main').show();
			$('#overlay-continue').show();
			$('#overlay-continue').html("Reset");
			$('#overlay-cancel').html("Cancel");
			$('#overlay-header').html("Reset Mail Template");
			$('#overlay-text').html("Are you sure you want to reset the mail template ?");
			$('#overlay-continue').attr("onclick", "revertMailContent('surveycompletionmail');");
		});
		
		//social post reminder mail
		$('#edit-social-post-reminder-mail-content').click(function() {
			try {
				$('#social-post-reminder-mailcontent').ckeditorGet().setReadOnly(false);
			} catch(e) {
			}
			$('#social-post-reminder-subject').attr("readonly", false);
			
			$('#save-social-post-reminder-mail-content').show();
			$('#save-social-post-reminder-mail-content-disabled').hide();
			
			$('#edit-social-post-reminder-mail-content-disabled').show();
			$(this).hide();
		});
		$('#save-social-post-reminder-mail-content').click(function() {
			$('#mailcategory').val('socialpostremindermail');
			updateMailContent("mail-body-settings-form");
			
			try { 
				$('#social-post-reminder-mailcontent').ckeditorGet().setReadOnly(true);
			} catch(e) {
			}
			$('#social-post-reminder-subject').attr("readonly", true);
			
			$(this).hide();
			$('#save-social-post-reminder-mail-content-disabled').show();

			$('#edit-social-post-reminder-mail-content').show();
			$('#edit-social-post-reminder-mail-content-disabled').hide();
		});
		$('#revert-social-post-reminder-mail').click(function() {
			$('#overlay-main').show();
			$('#overlay-continue').show();
			$('#overlay-continue').html("Reset");
			$('#overlay-cancel').html("Cancel");
			$('#overlay-header').html("Reset Mail Template");
			$('#overlay-text').html("Are you sure you want to reset the mail template ?");
			$('#overlay-continue').attr("onclick", "revertMailContent('socialpostremindermail');");
		});
		
		//incomplete survey reminder mail
		$('#edit-incomplete-survey-reminder-mail-content').click(function() {
			try {
				$('#incomplete-survey-reminder-mailcontent').ckeditorGet().setReadOnly(false);
			} catch(e) {
			}
			$('#incomplete-survey-mailreminder-subject').attr("readonly", false);
			
			$('#save-incomplete-survey-reminder-mail-content').show();
			$('#save-incomplete-survey-reminder-mail-content-disabled').hide();
			
			$('#edit-incomplete-survey-reminder-mail-content-disabled').show();
			$(this).hide();
		});
		$('#save-incomplete-survey-reminder-mail-content').click(function() {
			$('#mailcategory').val('restartsurveymail');
			updateMailContent("mail-body-settings-form");
			
			try { 
				$('#incomplete-survey-reminder-mailcontent').ckeditorGet().setReadOnly(true);
			} catch(e) {
			}
			$('#incomplete-survey-mailreminder-subject').attr("readonly", true);
			
			$(this).hide();
			$('#save-incomplete-survey-reminder-mail-content-disabled').show();

			$('#edit-incomplete-survey-reminder-mail-content').show();
			$('#edit-incomplete-survey-reminder-mail-content-disabled').hide();
		});
		$('#revert-incomplete-survey-reminder-mail').click(function() {
			$('#overlay-main').show();
			$('#overlay-continue').show();
			$('#overlay-continue').html("Reset");
			$('#overlay-cancel').html("Cancel");
			$('#overlay-header').html("Reset Mail Template");
			$('#overlay-text').html("Are you sure you want to reset the mail template ?");
			$('#overlay-continue').attr("onclick", "revertMailContent('restartsurveymail');");
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

	});
</script>