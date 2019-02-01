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
									<div id="hide-pp-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hide-pp-cb" name="hidepublicpage" value="${hidePublicPage}">
									<div class="float-left customized-settings-child cust-resp-txt">Hide public page</div>
									<div id="hide-bread-crumb-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hide-bc-cb" name="hidebreadcrumb" value="${hideFromBreadCrumb}">
								<div class="float-left customized-settings-child cust-resp-txt">Hide from bread crumb</div>
								</c:if>	
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">	
									<div id="hide-pp-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hide-pp-cb" name="hidepublicpage" value="${hidePublicPage}">
									<div class="float-left customized-settings-child cust-resp-txt">Hide public page</div>
									<div id="hide-bread-crumb-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hide-bc-cb" name="hidebreadcrumb" value="${hideFromBreadCrumb}">
									<div class="float-left customized-settings-child cust-resp-txt">Hide from bread crumb</div>
									<div id="hidden-section-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="hidden-section-cb" name="hiddensection" value="${hiddenSection}">
									<div class="float-left customized-settings-child cust-resp-txt">Hide public pages of all agents</div>
									<div id="mail-frm-cmpny-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="mail-frm-cmpny-cb" name="sendmailfromcompany" value="${sendEmailFromCompany}">
									<div class="float-left customized-settings-child cust-resp-txt">Send all mails on behalf of company</div>
									<div id="ovride-sm-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="ovride-sm-cb" name="overridesm" value="${allowOverrideForSocialMedia}">
									<div class="float-left customized-settings-child cust-resp-txt">Allow admins to override social media</div>
								</c:if>				
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
									<div id="atpst-lnk-usr-ste-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="at-pst-lnk-usr-ste-cb" name="autopostlinktousersite" value="${autoPostLinkToUserSite}">
									<div class="float-left customized-settings-child cust-resp-txt">Allow autopost link to the user's website</div>
								</c:if>
								
								<!-- partner survey settings -->
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
									<div id="alw-ptnr-srvy-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="alw-ptnr-srvy-cb" name="allowpartnersurvey" value="${allowPartnerSurvey}">
									<div class="float-left customized-settings-child cust-resp-txt">Allow partner survey</div>
								</c:if>
								
								<!-- transaction monitor settings -->
								<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
									<div id="incld-fr-trans-mntr-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="incld-fr-trans-mntr-cb" name="includeForTransactionMonitor" value="${includeForTransactionMonitor}">
									<div class="float-left customized-settings-child cust-resp-txt">Include for transaction monitor</div>
								</c:if>
								
								<c:if test="${ isRealTechOrSSAdmin == 'true' and columnName != 'agentId' and accountMasterId != 1 }">
									<div id="vndsta-access-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="vndsta-access-cb" name="vendastaaccess" value="${vendastaAccess}">
									<div class="float-left listing-access-txt cust-resp-txt" style="margin-bottom:0px;">Allow access to Listings Manager</div>
								</c:if>
								
								<c:if test="${ columnName == 'companyId' }">
									<div id="copyto-clipboard-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="copyto-clipboard-cb" name="copytoclipboard" value="${copyToClipBoard}">
									<div class="float-left listing-access-txt cust-resp-txt" style="margin-bottom:0px;">Copy review text to clip-board</div>
								</c:if>

								<c:if test="${ isRealTechOrSSAdmin == true }">
									<div id="soc-mon-access-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="soc-mon-access-cb" name="surveymailthrhld" value="${isSocialMonitorEnabled}">
									<div class="float-left listing-access-txt cust-resp-txt">Enable Social Monitor</div>
								</c:if>
								
								<c:if test="${ columnName != 'agentId'}">
									<div id="survey-mail-thrhld-chk-box" class="float-left bd-check-img clear-both"></div>
									<input type="hidden" id="survey-mail-thrhld-cb" name="surveymailthrhld" value="${sendMonthlyDigestMail}">
									<div class="float-left listing-access-txt cust-resp-txt">Send Monthly Digest Mail</div>
									<textarea id="digest-recipients" class="dig-recp"  placeholder="<spring:message code="label.placehoder.digest.emails.key" />" autocorrect="off" autocomplete="off" autocapitalize="off" spellcheck="false">${digestRecipients}</textarea>
									<c:if test="${ columnName == 'companyId' }">
										<div class="float-left listing-access-txt cust-resp-txt">Send user Add/Delete Notification Mail</div>
										<textarea id="user-notification-recipients" class="dig-recp" style="margin-bottom:40px" placeholder="<spring:message code="label.placehoder.user.notify.emails.key" />" autocorrect="off" autocomplete="off" autocapitalize="off" spellcheck="false">${userNotifyRecipients}</textarea>
									</c:if>	
								</c:if>
							</c:if>
							
                            <c:if test="${ not empty companyAdminSwitchId or isRealTechOrSSAdmin == true or user.isOwner == 1 }">
								<div class="float-left clear-both comp-mail-thrs-txt"><spring:message code="label.agent.notify.threshold.key" /></div>
								<div class="float-left">
									<!-- set the minimum threshold for sending completed mail to administrators and agents -->
									<input type="text" name="survey-mail-threshold" id="survey-mail-threshold" class="st-item-row-txt cursor-pointer dd-arrow-dn" autocomplete="off" value="${surveyCompletedMailThreshold}">
									<div class="st-dd-wrapper hide" id="st-dd-wrapper-survey-mail-thrs"></div>
								</div>
							</c:if>
							
							<c:if test="${ columnName == 'companyId' }">
								<div class="float-left clear-both comp-mail-thrs-txt"><spring:message code="label.encompass.alert.mail.key" /></div>
								<input type="hidden" id="encompass-alert-mails" value="${encompassAlertEmails}">
								<textarea id="enc-alert-mail-recipients" class="dig-recp" style="margin-bottom:40px" placeholder="<spring:message code="label.placehoder.encompass.alert.emails.key" />" autocorrect="off" autocomplete="off" autocapitalize="off" spellcheck="false">${encompassAlertEmails}</textarea>
							</c:if>
							
							<c:if test="${ isRealTechOrSSAdmin == true and columnName == 'companyId' }">
							<div class="send-email-sel-col">
								<div class="clearfix padding-bottom-twenty">
									<div class="float-left st-score-rt-top email-setting-sel-lbl">
										<spring:message code="label.send.email.via.key" />
									</div>
									<div class="email-sel-wrapper email-resp email-resp-margin">
										<div class="email-sel-item">
											<input type="text" id="email-sel" class="float-left dd-arrow-dn cursor-pointer email-item-wrapper" spellcheck="false">
										</div>
										<div class="email-option-wrapper hide" id="email-options"></div>
									</div>
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
				<div class="clearfix float-right col-lg-8 col-md-8 col-sm-8 col-xs-12 pos-relative">
				<div id="social-media-dash" class="hide" ></div>
					<div id="social-media-token-cont" class="soc-nw-wrapper clearfix">
						<%-- <jsp:include page="settings_socialauth.jsp"></jsp:include> --%>
					</div>
				</div>
			</div>
		</div>
		
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
	
	if("${autoPostLinkToUserSite}" == "false" && "${isRealTechOrSSAdmin}" == "true"){
		$('#atpst-lnk-usr-ste-chk-box').addClass('bd-check-img-checked');
	}
	
	if("${vendastaAccess}" == "false" && "${isRealTechOrSSAdmin}" == "true"){
		$('#vndsta-access-chk-box').addClass('bd-check-img-checked');
	}

	if("${allowPartnerSurvey}" == "false" && "${isRealTechOrSSAdmin}" == "true"){
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
	
	var accountMasterId = "${accountMasterId}";
	if (accountMasterId != 5) {
		
		autoAppendRatingDropdown('#st-dd-wrapper-min-post', "st-dd-item st-dd-item-min-post");
		
		changeRatingPattern($('#rating-min-post').val(), $('#rating-min-post-parent'));
		$('#rating-min-post').off('click');
		$('#rating-min-post').on('click', function(){
			$('#email-options').hide();
			$('#sort-options').hide();
			$('#st-dd-wrapper-survey-mail-thrs').hide();
			$('#st-dd-wrapper-min-post').slideToggle(200);
			$(document).mouseup(ratingMouseUp);
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
		
		paintTextForMood(happyTxt, nuTxt,sadTxt,happyTxtComplete, nuTxtComplete,sadTxtComplete);		
	}
	
});
</script>
