<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="hm-item-row clearfix float-left">
	<div class="encompass-item-row-left text-right">EmailID</div>
	<div class="margin-right-10 hm-item-height-adj float-left" style="width: 70%;">
		<input id="report-email" type="text" class="encompass-item-row-txt" placeholder="EmailID" value="${ emailId }">
	</div>
	<div class="encompass-item-row-left text-right" style="clear: left;">Duration</div>
	<div class="margin-right-10 hm-item-height-adj float-left" style="width: 70%;">
		<input id="no-of-days" type="text" class="encompass-item-row-txt" placeholder="No Of Days" value="${ NumberOfDays }">
	</div>
</div>