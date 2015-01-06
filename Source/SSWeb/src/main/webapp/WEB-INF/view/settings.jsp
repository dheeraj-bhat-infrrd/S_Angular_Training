<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="overlay-toast" class="overlay-toast"></div>

<div class="overlay-disable hide" id="overlay-confirm">
    <div class="overlay-disable-wrapper">
        <div id="overlay-header" class="ol-header">Disable Account</div>
        <div class="ol-content">
            <div id="overlay-text" class="ol-txt"></div>
            <div class="clearfix">
                <div class="float-left ol-btn-wrapper">
                    <div id="overlay-continue" class="ol-btn">Continue</div>
                </div>
                <div class="float-left ol-btn-wrapper">
                    <div id="overlay-cancel" class="ol-btn">Cancel</div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left"><spring:message code="label.title.settings.key" /></div>
        </div>
    </div>
</div>

<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
    	<form id="encompass-form">
	        <div class="um-top-container">
	            <div class="um-header"><spring:message code="label.header.encompass.configuration.key" /></div>
	            <div class="clearfix um-panel-content">
	                <div class="row">
	                    <div class="um-top-row cleafix">
	                        <div class="clearfix um-top-form-wrapper">
	                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
	                                <div class="hm-item-row item-row-OR clearfix">
	                                    <div class="um-item-row-left text-right"><spring:message code="label.encompass.username.key" /></div>
	                                    <div class="clearfix float-right st-username-icons">
	                                        <div class="um-item-row-icon margin-left-0"></div>
	                                        <div class="um-item-row-icon margin-left-0"></div>
	                                    </div>
	                                    <div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj">
		                                    <!-- check the encompass username -->
		                                    <c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.crm_info!= null && cannonicalusersettings.companySettings.crm_info.crm_username != null}">
		                                    	<c:set var="encomapssusername" value="${cannonicalusersettings.companySettings.crm_info.crm_username }"/>
		                                    </c:if>
		                                    <input id="encompass-username" type="text" class="um-item-row-txt um-item-row-txt-OR" placeholder="Username" name="encompass-username" value="${encomapssusername}">
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
	                                    	<!-- check the encompass password -->
		                                    <c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.crm_info!= null && cannonicalusersettings.companySettings.crm_info.crm_password != null}">
		                                    	<c:set var="encomapsspassword" value="${cannonicalusersettings.companySettings.crm_info.crm_password }"/>
		                                    </c:if>
	                                        <input id="encompass-password" type="password" class="um-item-row-txt um-item-row-txt-OR" placeholder="Password" name="encompass-password"  value="${encomapsspassword}">
		                                    <div id="encompass-password-error" class="hm-item-err-2"></div>
	                                    </div>
	                                </div>
	                            </div>
	                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
	                                <div class="hm-item-row item-row-OR clearfix">
	                                    <div class="um-item-row-left text-right"><spring:message code="label.encompass.url.key" /></div>
	                                    <div class="clearfix float-right st-url-icons">
	                                        <div id="encompass-testconnection" class="um-item-row-icon icn-spanner margin-left-0"></div>
	                                        <div id="encompass-save" class="um-item-row-icon icn-blue-tick margin-left-0"></div>
	                                    </div>
	                                    <div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj">
	                                    	<!-- check the encompass password -->
		                                    <c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.crm_info!= null && cannonicalusersettings.companySettings.crm_info.url != null}">
		                                    	<c:set var="encomapssurl" value="${cannonicalusersettings.companySettings.crm_info.url }"/>
		                                    </c:if>
	                                        <input id="encompass-url" type="text" class="um-item-row-txt um-item-row-txt-OR" placeholder="URL" name="encompass-url" value="${encomapssurl}">
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
        <div class="um-top-container">
            <div class="um-header margin-top-25">Score Post Settings</div>
            <div class="clearfix st-score-wrapper">
                <div class="float-left st-score-txt">Lorem ipsum doret it emle Lorem ipsum doret it emle Lorem ipsum doret it emle Lorem ipsum doret it emle Lorem ipsum doret it emle Lorem ipsum doret it emle </div>
            	<form id="rating-settings-form">
            	<input type="hidden" name="ratingcategory" id="ratingcategory">
                <div class="clearfix float-right st-score-rt">
                    <div class="float-left score-rt-post score-rt-auto bord-rt-dc">
                        <div class="st-score-rt-top">Set auto post score</div>
                        <div class="st-score-rt-line2 clearfix">
                            <div class="st-rating-wrapper float-left clearfix" id="rating-auto-post-parent">
                                <div class="rating-star icn-full-star"></div>
                                <div class="rating-star icn-full-star"></div>
                                <div class="rating-star icn-half-star"></div>
                                <div class="rating-star icn-no-star"></div>
                                <div class="rating-star icn-no-star"></div>
                            </div>
                            <div class="st-rating-txt float-left">
	                            <!-- set the auto rating -->
		                        <c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.survey_settings!= null && cannonicalusersettings.companySettings.survey_settings.auto_post_score != null}">
		                          	<c:set var="autopostscore" value="${cannonicalusersettings.companySettings.survey_settings.auto_post_score}"/>
								</c:if>
				            	<input type="text" name="rating-auto-post" id="rating-auto-post" class="st-item-row-txt cursor-pointer" autocomplete="off" value="${autopostscore}">
                                <div class="st-dd-wrapper hide" id="st-dd-wrapper-auto-post"></div>
                            </div>
                        </div>
                    </div>
                    <div class="float-left score-rt-post score-rt-post-OR score-rt-min">
                        <div class="st-score-rt-top">Minimum score to post to profile</div>
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
		                        <c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.survey_settings!= null && cannonicalusersettings.companySettings.survey_settings.show_survey_above_score != null}">
		                          	<c:set var="minpostscore" value="${cannonicalusersettings.companySettings.survey_settings.show_survey_above_score}"/>
								</c:if>
				            	<input type="text" name="rating-min-post" id="rating-min-post" class="st-item-row-txt cursor-pointer" autocomplete="off" value="${minpostscore}">
			                    <div class="st-dd-wrapper hide" id="st-dd-wrapper-min-post"></div>
                            </div>
                        </div>
                    </div>
                </div>
                </form>
            </div>
        </div>
        <div class="um-top-container">
        	<form id="mail-body-settings-form">
        		<input type="hidden" name="mailcategory" id="mailcategory">
	            <div class="um-header margin-top-25"><spring:message code="label.header.email.configuration.key" /></div>
	            <div class="clearfix st-bottom-wrapper margin-top-50">
	                <div class="st-header-txt-lft-rt clearfix margin-top-25">
	                    <div class="float-left st-header-txt-lft"><spring:message code="label.header.mailer.content.key" /></div>
	                    <div class="float-right clearfix st-header-txt-rt">
	                        <div class="float-left st-header-txt-rt-icn icn-pen"></div>
	                        <div id="save-participation-mail-content" class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20"></div>
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
	                        <div class="float-left st-header-txt-rt-icn icn-pen"></div>
	                        <div id="save-participation-reminder-mail-content" class="float-left st-header-txt-rt-icn icn-blue-tick margin-left-20"></div>
	                    </div>
	                </div>
	                <div class="st-header-txt-wrapper">
	                    <textarea id="survey-participation-reminder-mailcontent" name="survey-participation-reminder-mailcontent" class="st-header-txt-input">${surveyremindermailbody}</textarea>
	                </div>
	            </div>
	            <div class="clearfix st-bottom-wrapper st-reminder-wrapper">
	                <div class="float-left"><spring:message code="label.reminder.interval.key" /></div>
	                <div class="clearfix float-left">
	                    <div class="float-left st-input-reminder">
							<c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.survey_settings!= null && cannonicalusersettings.companySettings.survey_settings.survey_reminder_interval_in_days != null}">
								<c:set var="reminderinterval" value="${cannonicalusersettings.companySettings.survey_settings.survey_reminder_interval_in_days}" />
							</c:if>
				            <input class="st-rating-input" name="reminder-interval" id="reminder-interval" value="${reminderinterval}">
		                    <div id="reminder-interval-error" class="hm-item-err-2"></div>
						</div>
	                    <div class="float-left"><spring:message code="label.days.key" /></div>
	                </div>
	                <div class="clearfix st-check-main float-left">
	                    <div class="float-left st-check-wrapper">
		                    <c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.survey_settings!= null && cannonicalusersettings.companySettings.survey_settings.isReminderDisabled != null}">
		                    	<c:set var="isreminderdisabled" value="${cannonicalusersettings.companySettings.survey_settings.isReminderDisabled}"/>
							</c:if>
                            <input type="hidden" name="reminder-needed-hidden" id="reminder-needed-hidden" value="${isreminderdisabled}">
	                        <div id="st-reminder-on" class="st-checkbox st-checkbox-on hide"></div>
	                        <div id="st-reminder-off" class="st-checkbox st-checkbox-off"></div>
	                    </div>
	                    <div class="float-left st-check-txt-OR">Click here if you do not want to send any reminder</div>
	                </div>
	            </div>
            </form>
        </div>
        <div class="um-top-container border-0">
            <div class="um-header margin-top-25">Other Settings</div>
            <form id="other-settings-form">
            <div class="st-others-wrapper clearfix">
	            <input type="hidden" name="othercategory" id="othercategory">
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12 st-settings-tab">
                    <div class="clearfix st-settings-item-wrapper">
                        <div class="float-left st-settings-check-wrapper">
                            <!-- set the min rating -->
		                    <c:if test="${cannonicalusersettings.companySettings !=null && cannonicalusersettings.companySettings.isLocationEnabled != null}">
		                    	<c:set var="islocationenabled" value="${cannonicalusersettings.companySettings.isLocationEnabled}"/>
							</c:if>
                            <input type="hidden" name="other-location" id="other-location" value="${islocationenabled}">
                            <div id="st-settings-location-on" class="st-checkbox st-settings-checkbox st-checkbox-on"></div>
                            <div id="st-settings-location-off" class="st-checkbox st-settings-checkbox st-checkbox-off hide"></div>
                        </div>
                        <div class="float-left st-check-txt-OR">Enable Location</div>
                    </div>
                    <div class="st-settings-text">Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="clearfix st-settings-item-wrapper">
                        <div class="float-left st-settings-check-wrapper">
                            <div id="st-settings-account-on" class="st-checkbox st-settings-checkbox st-checkbox-on hide"></div>
                            <div id="st-settings-account-off" class="st-checkbox st-settings-checkbox st-checkbox-off"></div>
                        </div>
                        <div class="float-left st-check-txt-OR">Disable Account</div>
                    </div>
                    <div class="st-settings-text">Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="clearfix st-settings-item-wrapper">
                        <div class="float-left st-settings-check-wrapper">
                            <div id="st-settings-payment-on" class="st-checkbox st-settings-checkbox st-checkbox-on"></div>
                            <div id="st-settings-payment-off" class="st-checkbox st-settings-checkbox st-checkbox-off hide"></div>
                        </div>
                        <div class="float-left st-check-txt-OR" id="st-chg-payment-info">Change Payment Information</div>
                    </div>
                    <div class="st-settings-text">Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay Lorem ipsum dore it ler sun soay </div>
                </div>
            </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/settings.js"></script>
<script src="${pageContext.request.contextPath}/resources/ckeditor/ckeditor.js"></script>
<script src="${pageContext.request.contextPath}/resources/ckeditor/adapters/jquery.js"></script>
<script>
$(document).ready(function(){
	$(document).attr("title", "Settings");
	$('#survey-participation-mailcontent').ckeditor();
	$('#survey-participation-reminder-mailcontent').ckeditor();
	
	autoAppendRatingDropdown('#st-dd-wrapper-auto-post', "st-dd-item st-dd-item-auto-post");
	changeRatingPattern($('#rating-auto-post').val(), $('#rating-auto-post-parent'));
	$('#rating-auto-post').click(function(){
		$('#st-dd-wrapper-auto-post').slideToggle(200);
	});
	
	autoAppendRatingDropdown('#st-dd-wrapper-min-post', "st-dd-item st-dd-item-min-post");
	changeRatingPattern($('#rating-min-post').val(), $('#rating-min-post-parent'));
	$('#rating-min-post').click(function(){
		$('#st-dd-wrapper-min-post').slideToggle(200);
	});
	
	autoSetCheckboxStatus('#st-settings-location-on', '#st-settings-location-off', '#other-location');
	autoSetCheckboxStatus('#st-reminder-on', '#st-reminder-off', '#reminder-needed-hidden');
});
</script>

<style>
.st-item-row-txt {
    border: 1px solid #e3e3e3;
    border-radius: 3px;
    box-shadow: 0px 2px 2px 0px #e3e3e3;
    width: 75px;
    height: 50px;
    padding: 0 10px;
}

.st-dd-wrapper {
    position: absolute;
    width: 75px;
    background-color: #fff;
    padding: 8px;
    border: 1px solid #dcdcdc;
}

.st-dd-item {
    border-bottom: 1px solid #dcdcdc;
    line-height: 36px;
    cursor: pointer;
}

.st-dd-item:last-child {
    border-bottom: 0;
}
</style>