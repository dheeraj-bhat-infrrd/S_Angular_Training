<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="hm-item-row clearfix float-left">
	<div class="encompass-item-row-left text-right">EmailID</div>
	<div
		class="margin-right-10 hm-item-height-adj float-left" style="width:70%;">
		<input id="encompass-report-email" type="text"
			class="encompass-item-row-txt" placeholder="EmailID" >
		<div id="encompass-username-error" class="hm-item-err-2"></div>
	</div>
	<div class="encompass-item-row-left text-right" style="clear: left;">Duration</div>
	<div
		class="margin-right-10 hm-item-height-adj float-left" style="width:70%;">
		<input id="encompass-no-of-days" type="text"
			class="encompass-item-row-txt" placeholder="No Of Days" >
		<div id="encompass-username-error" class="hm-item-err-2"></div>
	</div>
	<%-- <div class="clearfix dash-sel-wrapper">
		<div class="float-left dash-sel-lbl">
			<spring:message code="label.duration.key" />
		</div>
		<input id="encompass-no-of-days" type="text"
			class="um-item-row-txt" placeholder="No Of Days">
	</div> --%>
</div>