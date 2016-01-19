<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

        <div class="hm-item-row item-row-OR clearfix float-left">
            <div class="um-item-row-left text-right">
                EmailID
            </div>
           <!--  <div class="clearfix float-right st-username-icons">
                <div class="um-item-row-icon margin-left-0"></div>
                <div class="um-item-row-icon margin-left-0"></div>
            </div> -->
            <div class="hm-item-row-right um-item-row-right margin-right-10 hm-item-height-adj float-left">
                <input id="encompass-report-email" type="text" class="um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="EmailID" name="encompass-username" >
                <div id="encompass-username-error" class="hm-item-err-2"></div>
            </div>
            <div class="clearfix dash-sel-wrapper">
					<div class="float-left dash-sel-lbl"><spring:message code="label.duration.key" /></div>
					<input id="encompass-no-of-days" type="text" class="um-item-row-txt um-item-row-txt-OR en-user-name en-form-align-left" placeholder="No Of Days" name="encompass-username" value="">
				</div>
        </div>