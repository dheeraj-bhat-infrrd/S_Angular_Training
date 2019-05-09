<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:set value="${user.userId}" var="userId"/>
<c:if test="${not empty accountSettings && not empty accountSettings.survey_settings}">
	<c:set value="${accountSettings.survey_settings}" var="surveysettings"></c:set>
</c:if>
<c:set var="highestrole" value="${highestrole}"></c:set>
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
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>

<div id="temp-div"></div>
<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	
	<div class="container">

		<!-- Starting code for Autopost Score -->
		<c:if test="${accountMasterId != 5}">
			<div class="um-top-container">
				<div class="um-header margin-top-25"><spring:message code="label.scorepost.key" /></div>
				<div class="clearfix st-score-wrapper">
					<div class="float-left st-score-txt stng-padng margin-bottom-twenty">
					<spring:message code="label.scorepost.desc.key" />
					</div>
					<form id="rating-settings-form">
						<input type="hidden" name="ratingcategory" id="ratingcategory">
						<div class="clearfix float-right st-score-rt pos-relative">
						<div id="config-setting-dash" class="hide" ></div>
						<div class="float-left score-rt-post score-rt-post-OR score-rt-min">
							<div class="st-score-rt-top width-three-five-zero"><spring:message code="label.scorepost.min.key" /></div>
							<div class="st-score-rt-line2 clearfix">
								<div class="st-rating-wrapper settings-rating-wrapper float-left clearfix" id="rating-min-post-parent">
								</div>
								<div class="st-rating-txt float-left">
									<!-- set the min rating -->
									<input type="text" name="rating-min-post" id="rating-min-post" class="st-item-row-txt cursor-pointer dd-arrow-dn" autocomplete="off" value="${minpostscore}">
									<div class="st-dd-wrapper hide" id="st-dd-wrapper-min-post"></div>
								</div>
							</div>
					    <div class="margin-top-twenty">
								<div id="atpst-chk-box" class="float-left bd-check-img"></div>
								<input type="hidden" id="at-pst-cb" name="autopost" value="${autoPostEnabled}">
								<div class="float-left bd-check-txt">Allow user to autopost</div>
							</div>

							<!-- Reply Settings Start -->
							<c:if test="${columnName == 'companyId' and isRealTechOrSSAdmin == true}">
								<div class="ss-admin-comp-settings" style="margin-top:0px">	
									<div id="allow-reply-all-chk-box" class="float-left bd-check-img"></div>
									<input type="hidden" id="at-pst-cb" name="allowreplytoall" value="${isReplyEnabledForCompany}">
									<div class="float-left bd-check-txt">Allow reply to reviews for company</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>	
							</c:if>
							<c:if test="${ (not empty companyAdminSwitchId) or isRealTechOrSSAdmin == true or user.isOwner == 1}">
									<c:choose>
							      <c:when test="${isReplyEnabledForCompany}">
											<div class="manage-reply-setting-section width-three-five-zero">		
										</c:when>		
										<c:otherwise>
											<div class="manage-reply-setting-section width-three-five-zero hide">		
										</c:otherwise>
									</c:choose>							

										<div class="st-score-rt-top width-three-five-zero"><spring:message code="label.scoretoreply.min.key" /></div>
							        
										<c:choose>
							      	<c:when test="${isReplyEnabled}">
												<div class="st-score-rt-line2 clearfix">		
											</c:when>		
											<c:otherwise>
												<div class="st-score-rt-line2 clearfix disable">	
											</c:otherwise>
										</c:choose>												
								        <div class="st-rating-wrapper settings-rating-wrapper float-left clearfix" id="rating-min-reply-parent"></div>
								        <div class="st-rating-txt float-left">
									        <!-- set the min rating -->
									        <input type="text" name="rating-min-reply" id="rating-min-reply" class="st-item-row-txt cursor-pointer dd-arrow-dn" autocomplete="off" value="${minreplyscore}">
									        <div class="st-dd-wrapper hide" id="st-dd-wrapper-min-reply"></div>
								        </div>
							        </div>

							        <div class="margin-top-twenty">
								        <div id="allow-reply-chk-box" class="float-left bd-check-img"></div>
								        <input type="hidden" id="at-pst-cb" name="allowreply" value="${isReplyEnabled}">
								        <div class="float-left bd-check-txt">Allow user to reply to reviews</div>
							       </div>
							       
							       <%-- Don't show 'Apply to all users' button to Agents --%>
							       <c:if test="${profilemasterid != 4}">
									 <div class="propagate-btn-section">
										 		<button type="button" class="propagate-btn" title="Apply the reply settings to lower hierarchy">Apply to all users</button>
									 </div>
								   </c:if>
								   
								</div>
							</c:if>	
							<!-- Reply Settings End -->

							<%-- <c:if test="${ (not empty companyAdminSwitchId or isRealTechOrSSAdmin == true or user.isOwner == 1) and columnName == 'companyId' }">
							         <div class="margin-top-twenty">
								        <div id="allow-reply-all-chk-box" class="float-left bd-check-img"></div>
								        <input type="hidden" id="at-pst-cb" name="allowreplytoall" value="${isReplyEnabledForCompany}">
								        <div class="float-left bd-check-txt">Allow all user to reply on SS reviews</div>
							        </div>
							</c:if> --%>						

							<c:if test="${ isRealTechOrSSAdmin == 'true' and columnName == 'companyId' }">
									<div class="review-sort-sel-col">
									<div class="clearfix setting-sel-wrapper">
										<div class="st-score-rt-top margin-top-twenty email-sel-item-resp sort-resp">
											<spring:message code="label.review.sort.criteria.key" />
										</div>
										<div class="sort-sel-wrapper">
											<input type="text" id="sort-criteria-sel" class="float-left dd-arrow-dn cursor-pointer review-sort-sel-item">
										</div>
										<div class="sort-option-wrapper review-sort-wrapper-resp hide" id="sort-options"></div>
									</div>
								</div>
							</c:if>	
							
							<c:if test="${ columnName != 'agentId'  or isRealTechOrSSAdmin == true }">
								<div id="customized-setting-div" class="st-score-rt-top" style="">Customized Feature Settings:</div>
								<c:if test="${ isRealTechOrSSAdmin == true and columnName != 'companyId' }">	
								<div class="ss-admin-comp-settings" style="">		
									<div id="hide-pp-chk-box" class="float-left bd-check-img clear-both"></div>	
									<input type="hidden" id="hide-pp-cb" name="hidepublicpage" value="${hidePublicPage}">	
									<div class="float-left customized-settings-child cust-resp-txt">Hide public page</div>	
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>	
								</div>	
								<div class="ss-admin-comp-settings" style="">	
									<div id="hide-bread-crumb-chk-box" class="float-left bd-check-img clear-both"></div>	
									<input type="hidden" id="hide-bc-cb" name="hidebreadcrumb" value="${hideFromBreadCrumb}">	
								<div class="float-left customized-settings-child cust-resp-txt">Hide from bread crumb</div>	
								<div class="ss-admin-only-visible">Only visible to SS-Admin</div>	
								</div>	
								</c:if>	
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
								<div class="ss-admin-comp-settings" style="">	
									<div id="hide-pp-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hide-pp-cb" name="hidepublicpage" value="${hidePublicPage}">
									<div class="float-left customized-settings-child cust-resp-txt">Hide public page</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								<div class="ss-admin-comp-settings" style="">
									<div id="hide-bread-crumb-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hide-bc-cb" name="hidebreadcrumb" value="${hideFromBreadCrumb}">
									<div class="float-left customized-settings-child cust-resp-txt">Hide from bread crumb</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								<div class="ss-admin-comp-settings" style="">
									<div id="hidden-section-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hidden-section-cb" name="hiddensection" value="${hiddenSection}">
									<div class="float-left customized-settings-child cust-resp-txt">Hide public pages of all agents</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								<div class="ss-admin-comp-settings" style="">
									<div id="mail-frm-cmpny-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="mail-frm-cmpny-cb" name="sendmailfromcompany" value="${sendEmailFromCompany}">
									<div class="float-left customized-settings-child cust-resp-txt">Send all mails on behalf of company</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								<div class="ss-admin-comp-settings" style="">
									<div id="ovride-sm-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="ovride-sm-cb" name="overridesm" value="${allowOverrideForSocialMedia}">
									<div class="float-left customized-settings-child cust-resp-txt">Allow admins to override social media</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								</c:if>				
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
								<div class="ss-admin-comp-settings" style="">
									<div id="atpst-lnk-usr-ste-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="at-pst-lnk-usr-ste-cb" name="autopostlinktousersite" value="${autoPostLinkToUserSite}">
									<div class="float-left customized-settings-child cust-resp-txt">Allow autopost link to the user's website</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								</c:if>
								
								<!-- partner survey settings -->
								<c:if test="${ (isRealTechOrSSAdmin == true and columnName == 'companyId') || (not empty companyAdminSwitchId && (columnName == 'regionId' || columnName == 'branchId' ) ) }">
									<c:choose>
										<c:when test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
											<div class="ss-admin-comp-settings" style="">
												<div id="alw-ptnr-srvy-chk-box" class="float-left bd-check-img clear-both"></div>
												<input type="hidden" id="alw-ptnr-srvy-cb" name="allowpartnersurvey" value="${allowPartnerSurvey}">
												<div class="float-left customized-settings-child cust-resp-txt">Allow partner survey</div>
												<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
											</div>
										</c:when>
										<c:otherwise>
											<c:if test="${ partnerSurveyAllowedAtCompany == true }">
												<div class="ss-comp-settings" style="">
													<div id="alw-ptnr-srvy-chk-box" class="float-left bd-check-img clear-both"></div>
													<input type="hidden" id="alw-ptnr-srvy-cb" name="allowpartnersurvey" value="${allowPartnerSurvey}">
													<div class="float-left customized-settings-child cust-resp-txt">Allow partner survey</div>
												</div>
											</c:if>
										</c:otherwise>
									</c:choose>
								</c:if>
								
								<!-- transaction monitor settings -->
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
								<div class="ss-admin-comp-settings" style="">	
									<div id="incld-fr-trans-mntr-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="incld-fr-trans-mntr-cb" name="includeForTransactionMonitor" value="${includeForTransactionMonitor}">
									<div class="float-left customized-settings-child cust-resp-txt">Include for transaction monitor</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								</c:if>
								
								<c:if test="${ isRealTechOrSSAdmin == 'true' and columnName != 'agentId' and accountMasterId != 1 }">
								<div class="ss-admin-comp-settings" style="">
									<div id="vndsta-access-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="vndsta-access-cb" name="vendastaaccess" value="${vendastaAccess}">
									<div class="float-left listing-access-txt cust-resp-txt" style="margin-bottom:0px;">Allow access to Listings Manager</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								</c:if>
								
								<c:if test="${ columnName == 'companyId' }">
								<div class="ss-comp-settings" style="">
									<div id="copyto-clipboard-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="copyto-clipboard-cb" name="copytoclipboard" value="${copyToClipBoard}">
									<div class="float-left listing-access-txt cust-resp-txt" style="margin-bottom:0px;">Copy review text to clip-board</div>
								</div>
								</c:if>
								
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
								<div class="ss-admin-comp-settings" style="">
									<div id="incomplete-survey-delete-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="incomplete-survey-delete-access-cb" name="deletecheckbox" value="${isIncompleteSurveyDeleteEnabled}">
									<div class="float-left listing-access-txt cust-resp-txt" style="margin-bottom:0px;">Enable Incomplete Survey Delete</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								</c:if>

								<c:if test="${ isRealTechOrSSAdmin == true }">
								<div class="ss-admin-comp-settings" style="">
									<div id="soc-mon-access-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="soc-mon-access-cb" name="surveymailthrhld" value="${isSocialMonitorEnabled}">
									<div class="float-left listing-access-txt cust-resp-txt">Enable Social Monitor</div>
									<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
								</div>
								</c:if>
								
								<c:if test="${ columnName == 'companyId' }">
								<div class="ss-comp-settings" style="">
									<div id="alw-br-admin-del-usr-chk-box" data-typeOfCheckBox="branchAdminDeleteAccess" class="float-left admin-access-chk-box bd-check-img clear-both"></div>
									<input type="hidden" id="alw-br-admin-del-usr-cb" name="allowBranchAdminToDeleteUser" value="${allowBranchAdminToDeleteUser}">
									<div class="float-left customized-settings-child cust-resp-txt"><spring:message code="label.companysettings.allowbranchadmin.deleteuser" /></div>
								</div>
								</c:if>

								<c:if test="${ columnName == 'companyId' }">
								<div class="ss-comp-settings" style="">
									<div id="alw-rgn-admin-del-usr-chk-box" data-typeOfCheckBox="regionAdminDeleteAccess" class="float-left admin-access-chk-box bd-check-img clear-both"></div>
									<input type="hidden" id="alw-rgn-admin-del-usr-cb" name="allowRegionAdminToDeleteUser" value="${allowRegionAdminToDeleteUser}">
									<div class="float-left customized-settings-child cust-resp-txt"><spring:message code="label.companysettings.allowregionadmin.deleteuser" /></div>
								</div>
								</c:if>

								<c:if test="${ columnName == 'companyId' }">
								<div class="ss-comp-settings" style="">
									<div id="alw-br-admin-add-usr-chk-box" data-typeOfCheckBox="branchAdminAddAccess" class="float-left admin-access-chk-box bd-check-img clear-both"></div>
									<input type="hidden" id="alw-br-admin-add-usr-cb" name="allowBranchAdminToAddUser" value="${allowBranchAdminToAddUser}">
									<div class="float-left customized-settings-child cust-resp-txt"><spring:message code="label.companysettings.allowbranchadmin.adduser" /></div>
								</div>
								</c:if>

								<c:if test="${ columnName == 'companyId' }">
								<div class="ss-comp-settings" style="">
									<div id="alw-rgn-admin-add-usr-chk-box" data-typeOfCheckBox="regionAdminAddAccess" class="float-left admin-access-chk-box bd-check-img clear-both"></div>
									<input type="hidden" id="alw-rgn-admin-add-usr-cb" name="allowBranchAdminToAddUser" value="${allowRegionAdminToAddUser}">
									<div class="float-left customized-settings-child cust-resp-txt"><spring:message code="label.companysettings.allowregionadmin.adduser" /></div>
								</div>
								</c:if>
								
								<c:if test="${ columnName != 'agentId'}">
								<div class="ss-comp-settings" style="">	
									<div id="survey-mail-thrhld-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="survey-mail-thrhld-cb" name="surveymailthrhld" value="${sendMonthlyDigestMail}">
									<div class="float-left listing-access-txt cust-resp-txt">Send Monthly Digest Mail</div>
								</div>
								<div class="ss-comp-settings" style="">	
									<textarea id="digest-recipients" class="dig-recp"  placeholder="<spring:message code="label.placehoder.digest.emails.key" />" autocorrect="off" autocomplete="off" autocapitalize="off" spellcheck="false">${digestRecipients}</textarea>
								</div>
									<c:if test="${ columnName == 'companyId' }">
									<div class="ss-comp-settings" style="">
										<div class="float-left listing-access-txt cust-resp-txt">Send user Add/Delete Notification Mail</div>
									</div>
									<div class="ss-comp-settings" style="">
										<textarea id="user-notification-recipients" class="dig-recp" style="margin-bottom:40px" placeholder="<spring:message code="label.placehoder.user.notify.emails.key" />" autocorrect="off" autocomplete="off" autocapitalize="off" spellcheck="false">${userNotifyRecipients}</textarea>
									</div>
									</c:if>
								</c:if>
							</c:if>
							
                            <c:if test="${ not empty companyAdminSwitchId or isRealTechOrSSAdmin == true or user.isOwner == 1 }">
                            <div class="ss-comp-settings" style="">
								<div class="float-left clear-both comp-mail-thrs-txt"><spring:message code="label.agent.notify.threshold.key" /></div>
								<div class="float-left">
									<!-- set the minimum threshold for sending completed mail to administrators and agents -->
									<input type="text" name="survey-mail-threshold" id="survey-mail-threshold" class="st-item-row-txt cursor-pointer dd-arrow-dn" autocomplete="off" value="${surveyCompletedMailThreshold}">
									<div class="st-dd-wrapper hide" id="st-dd-wrapper-survey-mail-thrs"></div>
								</div>
							</div>
							</c:if>
							<c:if test="${ ( not empty companyAdminSwitchId || highestrole == 1 ) && ( columnName == 'agentId' || columnName == 'branchId' || columnName == 'regionId' ) }">
								<div class="margin-top-twenty">
									<div id="conf-sec-flow-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="conf-sec-flow-cb" name="confsecworkflow" value="${allowToConfigSecondaryWorkflow}">
									<div class="float-left listing-access-txt cust-resp-txt">Allow to configure secondary workflow</div>
								</div>
							</c:if>
			
							<c:if test="${ columnName == 'companyId' }">
							<div class="ss-comp-settings" style="">
								<div class="float-left clear-both comp-mail-thrs-txt"><spring:message code="label.encompass.alert.mail.key" /></div>
								<input type="hidden" id="encompass-alert-mails" value="${encompassAlertEmails}">
								<textarea id="enc-alert-mail-recipients" class="dig-recp" style="margin-bottom:40px" placeholder="<spring:message code="label.placehoder.encompass.alert.emails.key" />" autocorrect="off" autocomplete="off" autocapitalize="off" spellcheck="false">${encompassAlertEmails}</textarea>
							</div>
							</c:if>
							
							<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
							<div class="send-email-sel-col">
							<div class="ss-admin-comp-settings" style="height: 80px;padding-top: 5px;padding-left:0px;">
								<div class="clearfix padding-bottom-twenty">
									<div class="float-left st-score-rt-top email-setting-sel-lbl">
										<spring:message code="label.send.email.via.key" />
									</div>
									<div class="email-sel-wrapper email-resp email-resp-margin">
										<div class="email-sel-item" style="background: #f7f4f4;">
											<input type="text" id="email-sel" class="float-left dd-arrow-dn cursor-pointer email-item-wrapper" spellcheck="false">
										</div>
										<div class="email-option-wrapper hide" id="email-options"></div>
									</div>
								</div>
								<div class="ss-admin-only-visible">Only visible to SS-Admin</div>
							</div>
							</div>
							</c:if>
						</div>
					</div>
					</form>
				</div>
			</div>
		</c:if>
		
		<%-- Starting code for Social Authentication --%>
		
		<c:if test="${profilemasterid == 1 || ( ( (columnName == 'agentId' || columnName == 'branchId' || columnName == 'regionId') &&  not empty companyAdminSwitchId ) && allowToConfigSecondaryWorkflow == true) }">
			<c:set var="containerclass" value="um-top-container"/>
		</c:if>
		<div class="${containerclass}">
			<div class="um-header margin-top-25"><spring:message code="label.socialconnect.key" /></div>
			<div class="clearfix st-score-wrapper">
				<div class="float-left st-social-score col-lg-4 col-md-4 col-sm-4 col-xs-12">
					<spring:message code="label.socialconnect.desc.key" />
				</div>
				<input type="hidden" name="ratingcategory" id="ratingcategory">
				<div class="clearfix float-right col-lg-8 col-md-8 col-sm-8 col-xs-12 pos-relative">
				<div id="social-media-dash" class="hide" ></div>
					<div id="social-media-token-cont" class="soc-nw-wrapper clearfix">
						<%-- <jsp:include page="settings_socialauth.jsp"></jsp:include> --%>
					</div>
				</div>
			</div>
		</div>
		
		<!-- URL Redirection Upon Submit Changes -->
		<c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="um-top-container">
				<div class="um-header-detail">
					<spring:message code="label.complete.desc.url.key" />
				</div>
				<div class="clearfix um-panel-content">
					<ul class="accordion"
						style="padding-left: 0px; margin: 0 10px 10px 10px; width: 75%">
						<li class="col-lg-13 col-md-12 col-sm-6 col-xs-6 "><a href="#"
							class="email-category">Redirect URL Parameters</a>
							<div class="email-content" style="">
								<div
									class="col-lg-7 col-md-6 col-sm-12 col-xs-12 float-left legend-height"
									style="height: 300px">
									<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 float-left">
										<div class="legend-header">Field</div>
										<div class="legend-wrapper">
											<span class="legend">[SURVEY_SOURCE_ID] </span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[PARTICIPANT_TYPE] </span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[TRANSACTION_TYPE] </span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[STATE] </span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[CITY]</span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[CUSTOM_FIELD_ONE] </span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[CUSTOM_FIELD_TWO] </span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[CUSTOM_FIELD_THREE]</span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[CUSTOM_FIELD_FOUR]</span>
										</div>
										<div class="legend-wrapper">
											<span class="legend">[CUSTOM_FIELD_FIVE] </span>
										</div>
									</div>
									<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6">
										<div class="legend-header">Description</div>
										<div class="legend-wrapper" title="Survey Source ID">Survey
											Source ID</div>
										<div class="legend-wrapper" title="Participant Type">Survey
											Participant Type</div>
										<div class="legend-wrapper" title="Transaction Type">Type
											of transaction</div>
										<div class="legend-wrapper" title="State">Customer State</div>
										<div class="legend-wrapper" title="City">Customer City</div>
										<div class="legend-wrapper" title="Custom Field One">Custom
											Field One</div>
										<div class="legend-wrapper" title="Custom Field Two">Custom
											Field Two</div>
										<div class="legend-wrapper" title="Custom Field Three">Custom
											Field Three</div>
										<div class="legend-wrapper" title="Custom Field Four">Custom
											Field Four</div>
										<div class="legend-wrapper" title="Custom Field five">Custom
											Field Five</div>
									</div>
								</div>
							</div>
						</li>
					</ul>
					<br> <br>
					<div class="clearfix um-panel-content">
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl">
								<spring:message code="label.complete.happy.url" />
							</div>
							<textarea id="happy-complete-url"
								class="float-left textarea-bd-mcq-txt" style=""
								placeholder='<spring:message code="label.redirect.url.placeholder.key" />'></textarea>
	
						</div>
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl">
								<spring:message code="label.complete.ok.url" />
							</div>
							<textarea id="ok-complete-url"
								class="float-left textarea-bd-mcq-txt" style=""
								placeholder='<spring:message code="label.redirect.url.placeholder.key" />'></textarea>
	
						</div>
						<div class="bd-mcq-row clearfix txtareaRow">
							<div class="float-left cs-gq-lbl">
								<spring:message code="label.complete.sad.url" />
							</div>
							<textarea id="sad-complete-url"
								class="float-left textarea-bd-mcq-txt" style=""
								placeholder='<spring:message code="label.redirect.url.placeholder.key" />'></textarea>
	
						</div>
					</div>
				</div>
			</div>
		</c:if>
		
		<!-- Starting code for Text for Happy/Neutral/Sad flow -->
		<c:if
			test="${profilemasterid == 1 || accountMasterId == 1 || ( ( columnName == 'agentId' || columnName == 'branchId' || columnName == 'regionId' ) && allowToConfigSecondaryWorkflow == true )}">
			<c:choose>
				<c:when
					test="${ allowPartnerSurvey == true && ( columnName == 'companyId'  || ( ( columnName == 'agentId' || columnName == 'branchId' || columnName == 'regionId' ) && ( allowToConfigSecondaryWorkflow == true ) ) ) }">
					<div style="padding-bottom: 20px;">
						<div class="um-header  margin-top-25">
							<spring:message code="label.flow.text.key" />
						</div>
						<ul class="nav nav-tabs" role="tablist">
							<li class="active"><a id="customer-tab-id"
								href="#customer-tab" role="tab" data-toggle="tab" data-iden="1">
									Customer Setting </a></li>
							<li><a id="partner-tab-id" href="#partner-tab" role="tab"
								data-toggle="tab" data-iden="2"> Partner Setting </a></li>
						</ul>
						<div class="tab-content" style="border: #d2dedf 1px solid;">
							<div id="customer-survey-panel-id-1"
								class="clearfix um-panel-content" style="margin-left: 10px;">
								<div class="um-header-detail">
									<spring:message code="label.flow.desc.text.key" />
								</div>
								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.flow.happy.label.text" />
									</div>
									<textarea id="happy-text"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>

								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.flow.ok.label.text" />
									</div>
									<textarea id="neutral-text"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>

								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.flow.sad.label.text" />
									</div>
									<textarea id="sad-text" class="float-left textarea-bd-mcq-txt"
										style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>
							</div>
							<div id="customer-survey-panel-id-2" class="um-gateway-cont"
								style="margin-left: 10px;">
								<div class="um-header-detail" style="margin-left: 15px;">
									<spring:message code="label.complete.desc.text.key" />
								</div>
								<div class="clearfix um-panel-content">
									<div class="bd-mcq-row clearfix txtareaRow">
										<div class="float-left cs-gq-lbl">
											<spring:message code="label.complete.happy.label.text" />
										</div>
										<textarea id="happy-text-complete"
											class="float-left textarea-bd-mcq-txt" style=""></textarea>
										<div class="float-left reset-icon cursor-pointer">
											<spring:message code="label.reset.key" />
										</div>
									</div>

									<div class="bd-mcq-row clearfix txtareaRow">
										<div class="float-left cs-gq-lbl">
											<spring:message code="label.complete.ok.label.text" />
										</div>
										<textarea id="neutral-text-complete"
											class="float-left textarea-bd-mcq-txt" style=""></textarea>
										<div class="float-left reset-icon cursor-pointer">
											<spring:message code="label.reset.key" />
										</div>
									</div>

									<div class="bd-mcq-row clearfix txtareaRow">
										<div class="float-left cs-gq-lbl">
											<spring:message code="label.complete.sad.label.text" />
										</div>
										<textarea id="sad-text-complete"
											class="float-left textarea-bd-mcq-txt" style=""></textarea>
										<div class="float-left reset-icon cursor-pointer">
											<spring:message code="label.reset.key" />
										</div>
									</div>
								</div>
							</div>

							<!-- partner survey setting -->
							<div id="partner-survey-panel-id-1"
								class="clearfix um-panel-content"
								style="margin-left: 10px; display: none;">
								<div class="um-header-detail">
									<spring:message code="label.partner.flow.desc.text.key" />
								</div>
								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.flow.happy.label.text" />
									</div>
									<textarea id="happy-text-partner"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>

								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.flow.ok.label.text" />
									</div>
									<textarea id="neutral-text-partner"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>

								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.flow.sad.label.text" />
									</div>
									<textarea id="sad-text-partner"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>
							</div>
							<div id="partner-survey-panel-id-2" class="um-gateway-cont"
								style="margin-left: 10px; display: none;">
								<div class="um-header-detail" style="margin-left: 15px;">
									<spring:message code="label.partner.complete.desc.text.key" />
								</div>
								<div class="clearfix um-panel-content">
									<div class="bd-mcq-row clearfix txtareaRow">
										<div class="float-left cs-gq-lbl">
											<spring:message code="label.complete.happy.label.text" />
										</div>
										<textarea id="happy-text-complete-partner"
											class="float-left textarea-bd-mcq-txt" style=""></textarea>
										<div class="float-left reset-icon cursor-pointer">
											<spring:message code="label.reset.key" />
										</div>
									</div>

									<div class="bd-mcq-row clearfix txtareaRow">
										<div class="float-left cs-gq-lbl">
											<spring:message code="label.complete.ok.label.text" />
										</div>
										<textarea id="neutral-text-complete-partner"
											class="float-left textarea-bd-mcq-txt" style=""></textarea>
										<div class="float-left reset-icon cursor-pointer">
											<spring:message code="label.reset.key" />
										</div>
									</div>

									<div class="bd-mcq-row clearfix txtareaRow">
										<div class="float-left cs-gq-lbl">
											<spring:message code="label.complete.sad.label.text" />
										</div>
										<textarea id="sad-text-complete-partner"
											class="float-left textarea-bd-mcq-txt" style=""></textarea>
										<div class="float-left reset-icon cursor-pointer">
											<spring:message code="label.reset.key" />
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:when>
			</c:choose>
			<c:choose>
				<c:when
					test="${ allowPartnerSurvey != true && ( columnName == 'companyId'  || ( ( columnName == 'agentId' || columnName == 'branchId' || columnName == 'regionId' ) && ( not empty companyAdminSwitchId || allowToConfigSecondaryWorkflow == true ) ) ) }">
					<div class = "um-top-container" style="padding-bottom: 20px;">
						<div class="um-header  margin-top-25">
							<spring:message code="label.flow.text.key" />
						</div>
						<div class="clearfix um-panel-content">
							<div class="um-header-detail">
								<spring:message code="label.flow.desc.text.key" />
							</div>
							<div class="bd-mcq-row clearfix txtareaRow">
								<div class="float-left cs-gq-lbl">
									<spring:message code="label.flow.happy.label.text" />
								</div>
								<textarea id="happy-text-customer"
									class="float-left textarea-bd-mcq-txt" style=""></textarea>
								<div class="float-left reset-icon cursor-pointer">
									<spring:message code="label.reset.key" />
								</div>
							</div>

							<div class="bd-mcq-row clearfix txtareaRow">
								<div class="float-left cs-gq-lbl">
									<spring:message code="label.flow.ok.label.text" />
								</div>
								<textarea id="neutral-text-customer"
									class="float-left textarea-bd-mcq-txt" style=""></textarea>
								<div class="float-left reset-icon cursor-pointer">
									<spring:message code="label.reset.key" />
								</div>
							</div>

							<div class="bd-mcq-row clearfix txtareaRow">
								<div class="float-left cs-gq-lbl">
									<spring:message code="label.flow.sad.label.text" />
								</div>
								<textarea id="sad-text-customer"
									class="float-left textarea-bd-mcq-txt" style=""></textarea>
								<div class="float-left reset-icon cursor-pointer">
									<spring:message code="label.reset.key" />
								</div>
							</div>
						</div>
						<div class="um-gateway-cont">
							<div class="um-header-detail">
								<spring:message code="label.complete.desc.text.key" />
							</div>
							<div class="clearfix um-panel-content">
								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.complete.happy.label.text" />
									</div>
									<textarea id="happy-text-complete-customer"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>

								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.complete.ok.label.text" />
									</div>
									<textarea id="neutral-text-complete-customer"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>

								<div class="bd-mcq-row clearfix txtareaRow">
									<div class="float-left cs-gq-lbl">
										<spring:message code="label.complete.sad.label.text" />
									</div>
									<textarea id="sad-text-complete-customer"
										class="float-left textarea-bd-mcq-txt" style=""></textarea>
									<div class="float-left reset-icon cursor-pointer">
										<spring:message code="label.reset.key" />
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:when>
			</c:choose>
		</c:if>
		
		<!-- Starting code for configuring opt-out text -->
		<!-- <c:if test="${profilemasterid == 1 || accountMasterId == 1}">
			<div class="um-top-container">
				<div class="um-header  margin-top-25"><spring:message code="label.optout.text.setting" /></div>
				<div class="clearfix um-panel-content">
					<div id="enable-login-chk-box" class="float-left bd-check-img clear-both"></div>
					<input type="hidden" id="enable-login-cb" name="enablelogin" value="${isEnableLogin}">
					<div class="float-left listing-access-txt cust-resp-txt"><spring:message code="label.show.enable.login.button" /></div>
				</div>
				<div class="clearfix um-panel-content">
					<div class="bd-mcq-row clearfix txtareaRow">
						<div class="float-left cs-gq-lbl"><spring:message code="label.optout.configure.text" /></div>
						<textarea id="opt-out-text" class="float-left textarea-bd-mcq-txt" style=""></textarea>
						<div class="float-left reset-opt-out-icon cursor-pointer"><spring:message code="label.reset.key" /></div>
					</div>
				</div>
			</div>
		</c:if> -->
		
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
						<c:if test="${billingMode == 'A'}">
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
						</c:if>
					</div>
				</form>
			</div>
		</c:if>
		
	</div>
</div>
<div style="display: none"><form id='deleteAccountForm' action="./deactivatecompany.do" method="get"></form></div>

<script>
$(document).ready(function() {
	$(document).attr("title", "Edit Settings");

	/*if( "${columnName}" == "companyId" || "${columnName}" == "companyId" ){
		$('#opt-out-text').val("${optOutText}");
		if("${isEnableLogin}" == "false"){
			$('#enable-login-chk-box').addClass('bd-check-img-checked');
		}
		else {
			$('#enable-login-chk-box').removeClass('bd-check-img-checked');
		}
	}*/
	if( "${companyAdminSwitchId}" != undefined ){

		if("${allowToConfigSecondaryWorkflow}" != undefined && "${allowToConfigSecondaryWorkflow}" == "true"){
			$('#conf-sec-flow-chk-box').removeClass('bd-check-img-checked');
		}
		else {
			$('#conf-sec-flow-chk-box').addClass('bd-check-img-checked');
		}
	}
	
	if( "${columnName}" != "agentId" && "${columnName}" != "companyId" ){
		$('#customized-setting-div').addClass('margin-top-hundred');
	}
	if( "${columnName}" == "companyId" ){
		$('#customized-setting-div').addClass('cust-resp');
		$('#customized-setting-div').addClass('cust-div-resp');
	}
	
	if( "${reviewSortCriteria}" == "feature" ){
		$("#sort-criteria-sel").val("Sort responses by Featured Reviews");
	} else {
		$("#sort-criteria-sel").val("Sort responses by Date");
	}
	$("#email-sel").val("${sendEmailThrough}");

	//social media urls
	loadSocialMediaUrlInSettingsPage();
	updateViewAsScroll();
	if("${autoPostEnabled}" == "false"){
		$('#atpst-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${isReplyEnabled}" == "false"){
		$('#allow-reply-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${isReplyEnabledForCompany}" == "false"){
		$('#allow-reply-all-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${autoPostLinkToUserSite}" == "false" && "${isRealTechOrSSAdmin}" == "true"){
		$('#atpst-lnk-usr-ste-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${vendastaAccess}" == "false" && "${isRealTechOrSSAdmin}" == "true"){
		$('#vndsta-access-chk-box').addClass('bd-check-img-checked');
	}

	if("${allowPartnerSurvey}" == "false" && ( "${isRealTechOrSSAdmin}" == "true" || "${companyAdminSwitchId}" != undefined ) ){
		$('#alw-ptnr-srvy-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${includeForTransactionMonitor}" == "false" && "${isRealTechOrSSAdmin}" == "true"){
		$('#incld-fr-trans-mntr-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${copyToClipBoard}" == "false"){
		$('#copyto-clipboard-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${sendMonthlyDigestMail}" == "false"){
		$('#survey-mail-thrhld-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${isSocialMonitorEnabled}" == "false"){
		$('#soc-mon-access-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${isIncompleteSurveyDeleteEnabled}" == "false"){
		$('#incomplete-survey-delete-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${hidePublicPage}" == "false"){
		$('#hide-pp-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${hideFromBreadCrumb}" == "false"){
		$('#hide-bread-crumb-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${hiddenSection}" == "false"){
		$('#hidden-section-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${sendEmailFromCompany}" == "false"){
		$('#mail-frm-cmpny-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${allowOverrideForSocialMedia}" == "false"){
		$('#ovride-sm-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${allowBranchAdminToDeleteUser}" == "false"){
		$('#alw-br-admin-del-usr-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${allowRegionAdminToDeleteUser}" == "false"){
		$('#alw-rgn-admin-del-usr-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${allowBranchAdminToAddUser}" == "false"){
		$('#alw-br-admin-add-usr-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${allowRegionAdminToAddUser}" == "false"){
		$('#alw-rgn-admin-add-usr-chk-box').addClass('bd-check-img-checked');
	}
	
	var accountMasterId = "${accountMasterId}";
	if (accountMasterId != 5) {
		
		autoAppendRatingDropdown('#st-dd-wrapper-min-post', "st-dd-item st-dd-item-min-post");
		
		changeRatingPattern($('#rating-min-post').val(), $('#rating-min-post-parent'));
		$('#rating-min-post').off('click');
		$('#rating-min-post').on('click', function(){
			$('#email-options').hide();
			$('#sort-options').hide();
			$('#st-dd-wrapper-min-reply').hide();
			$('#st-dd-wrapper-survey-mail-thrs').hide();

			if($('#st-dd-wrapper-min-post').is(':visible'))
				$('#st-dd-wrapper-min-post').slideUp(200);
			else
				$('#st-dd-wrapper-min-post').slideDown(200);

			$(document).mouseup(ratingMouseUp);
		});
		
		autoAppendReplyRatingDropdown('#st-dd-wrapper-min-reply', "st-dd-item st-dd-item-min-reply");
		changeRatingPattern($('#rating-min-reply').val(), $('#rating-min-reply-parent'));
		$('#rating-min-reply').off('click');
		$('#rating-min-reply').on('click', function(){
			$('#email-options').hide();
			$('#sort-options').hide();
			$('#st-dd-wrapper-min-post').hide();
			$('#st-dd-wrapper-survey-mail-thrs').hide();

			if($('#st-dd-wrapper-min-reply').is(':visible'))
			 $('#st-dd-wrapper-min-reply').slideUp(200);
			else
			 $('#st-dd-wrapper-min-reply').slideDown(200);

			$(document).mouseup(replyScoreMouseUp);
		});
		
		autoAppendSortOrderDropdown('#sort-options', "sort-option-item");
		$('#sort-criteria-sel').off('click');
		$('#sort-criteria-sel').on('click', function(){
			$('#email-options').hide();
			$('#st-dd-wrapper-min-post').hide();
			$('#st-dd-wrapper-survey-mail-thrs').hide();
			$('#sort-options').slideToggle(200);
			$(document).mouseup(sortCriteriaMouseUp);
		});
		
		autoAppendEmailCriteriaDropdown('#email-options', "email-option-item");
		$('#email-sel').off('click');
		$('#email-sel').on('click', function(){
			$('#sort-options').hide();
			$('#st-dd-wrapper-min-post').hide();
			$('#st-dd-wrapper-survey-mail-thrs').hide();
			$('#email-options').slideToggle(200);
			$(document).mouseup(emailCriteriaMouseUp);
		});
		
		autoAppendSurveyMailDropdown('#st-dd-wrapper-survey-mail-thrs', "st-dd-item st-dd-item-survey-mail-thrs");
		$('#survey-mail-threshold').off('click');
		$('#survey-mail-threshold').on('click', function(){
			$('#email-options').hide();
			$('#sort-options').hide();
			$('#st-dd-wrapper-min-post').hide();
			$('#st-dd-wrapper-survey-mail-thrs').slideToggle(200);
			$(document).mouseup(surveyMailThresholdMouseUp);
		});
		
		setUpListenerForEmailOptionDropdown();
		setUpListenerForSortCriteriaDropdown();


		autoSetCheckboxStatus('#st-settings-location-on', '#st-settings-location-off', '#other-location');
		autoSetCheckboxStatus('#st-settings-account-on', '#st-settings-account-off', '#other-account');
		autoSetCheckboxStatus('#st-reminder-on', '#st-reminder-off', '#reminder-needed-hidden');
		autoSetReminderIntervalStatus();
		var happyTxt="${surveysettings.happyText}";
		if(happyTxt == ""){
			happyTxt = '${defaultSurveyProperties.happyText}';
		}
		var nuTxt="${surveysettings.neutralText}";
		if(nuTxt == ""){
			nuTxt = "${defaultSurveyProperties.neutralText}";
		}
		var sadTxt="${surveysettings.sadText}";
		if(sadTxt == ""){
			sadTxt = "${defaultSurveyProperties.sadText}";
		}
		var happyTxtComplete="${surveysettings.happyTextComplete}";
		if(happyTxtComplete == ""){
			happyTxtComplete = "${defaultSurveyProperties.happyTextComplete}";
		}
		var nuTxtComplete="${surveysettings.neutralTextComplete}";
		if(nuTxtComplete == ""){
			nuTxtComplete = "${defaultSurveyProperties.neutralTextComplete}";
		}
		var sadTxtComplete="${surveysettings.sadTextComplete}";
		if(sadTxtComplete == ""){
			sadTxtComplete = "${defaultSurveyProperties.sadTextComplete}";
		}
		
		var happyTxtPartner = "${surveysettings.happyTextPartner}";
		if(happyTxtPartner == ""){
			happyTxtPartner = '${defaultSurveyPropertiesForPartner.happyTextPartner}';
		}
		var nuTxtPartner = "${surveysettings.neutralTextPartner}";
		if(nuTxtPartner == ""){
			nuTxtPartner = "${defaultSurveyPropertiesForPartner.neutralTextPartner}";
		}
		var sadTxtPartner = "${surveysettings.sadTextPartner}";
		if(sadTxtPartner == ""){
			sadTxtPartner = "${defaultSurveyPropertiesForPartner.sadTextPartner}";
		}
		var happyTxtCompletePartner = "${surveysettings.happyTextCompletePartner}";
		if(happyTxtCompletePartner == ""){
			happyTxtCompletePartner = "${defaultSurveyPropertiesForPartner.happyTextCompletePartner}";
		}
		var nuTxtCompletePartner = "${surveysettings.neutralTextCompletePartner}";
		if(nuTxtCompletePartner == ""){
			nuTxtCompletePartner = "${defaultSurveyPropertiesForPartner.neutralTextCompletePartner}";
		}
		var sadTxtCompletePartner = "${surveysettings.sadTextCompletePartner}";
		if(sadTxtCompletePartner == ""){
			sadTxtCompletePartner = "${defaultSurveyPropertiesForPartner.sadTextCompletePartner}";
		}
		var happyUrl="${surveysettings.happyUrl}";
		var okUrl="${surveysettings.okUrl}";
		var sadUrl="${surveysettings.sadUrl}";
		
		if("${allowPartnerSurvey}" == "true") {
			paintTextForMood(happyTxt, nuTxt, sadTxt, happyTxtComplete, nuTxtComplete, sadTxtComplete, happyUrl, okUrl, sadUrl);
			paintTextForMoodPartner(happyTxtPartner, nuTxtPartner, sadTxtPartner, happyTxtCompletePartner, nuTxtCompletePartner, sadTxtCompletePartner);
		}
		else {
			paintTextForMoodCustomer(happyTxt, nuTxt, sadTxt, happyTxtComplete, nuTxtComplete, sadTxtComplete, happyUrl, okUrl, sadUrl);
		}
	}
	
});
</script>
