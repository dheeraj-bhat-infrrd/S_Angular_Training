<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

        <div class="hm-item-row item-row-OR clearfix float-left">
            <div class="um-item-row-left text-right">
                EmailID
            </div>
            <div class="clearfix float-right st-username-icons">
                <div class="um-item-row-icon margin-left-0"></div>
                <div class="um-item-row-icon margin-left-0"></div>
            </div>
            <div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
                <div class="rfr_icn icn-fname en-icn-fname"></div>
                <input id="encompass-username" type="text" class="um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="EmailID" name="encompass-username" value="${encompassusername}">
                <div id="encompass-username-error" class="hm-item-err-2"></div>
            </div>
            <div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.duration.key" /></div>
					<select id="survey-count-days" class="float-left dash-sel-item">
						<option value="30"><spring:message code="label.duration.one.key" /></option>
						<option value="60"><spring:message code="label.duration.two.key" /></option>
						<option value="90" selected><spring:message code="label.duration.three.key" /></option>
						<option value="365"><spring:message code="label.duration.four.key" /></option>
					</select>
				</div>
        </div>