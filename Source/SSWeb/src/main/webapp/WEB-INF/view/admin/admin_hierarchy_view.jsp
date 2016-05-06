<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<div id="hm-header-main-wrapper" class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">
				<spring:message code="label.viewcompanyhierachy.key" />
			</div>
			<div class="v-um-hdr-right float-right">
				<label class="fil-label">Company Status : </label>
				<select class="com-sel-filter" id="com-filter">
					<option value="active">Active</option>
					<option value="inactive">Inactive</option>
					<option value="incomplete">Incomplete</option>
					<option value="deleted">Deleted</option>
				</select>
			</div>
		</div>
	</div>
</div>
<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div id="comp-hierarchy-cont" class="container v-hr-container">
	<div class="v-um-header clearfix">
		<div class="v-um-hdr-left float-left">
			<label class="fil-label">Company Type : </label> 
			<select class="com-sel-filter" id="com-type-filter">
				<option value="all">All</option>
				<option value="individual">Individual</option>
				<option value="enterprise">Enterprise</option>
			</select>
		</div>
		
		
		<div id="company-criterial-interval-wrapper" class="float-left margin-left-50">
				<input type="hidden" class="hide" name="companyCriteriaInterval">
	            <div class="float-left bd-cust-rad-item clearfix">
	                <div id="srchcmpnyalldays" data-type="-1" class="float-left bd-cust-rad-img bd-cust-rad-img-checked"></div>
	                <div class="float-left bd-cust-rad-txt">All</div>
	            </div>
	            <div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix">
	                <div id="srchcmpnythirtydays" data-type="30" class="float-left bd-cust-rad-img"></div>
	                <div class="float-left bd-cust-rad-txt">Past 30 days</div>
	            </div>
	        </div>
		
		<div class="v-um-hdr-right float-right">
			<div style="position: relative;">
				<div style="float: left;">
					<input id="hr-comp-sel" class="v-comp-inp float-left" placeholder="Search Company" srch-type="company">
					<div id="hr-drpdwn-icn" class="v-icn-dropdown float-left"></div>
				</div>
				<span id="hr-comp-icn" class="um-search-icn float-left"></span>
				<div id="srch-crtria-list" class="hr-dd-wrapper hide ps-container" style="display: none;">
					<div class="hr-dd-item" style="display: block;" srch-type="company" >Company</div>
					<div class="hr-dd-item" style="display: block;" srch-type="region">Region</div>
					<div class="hr-dd-item" style="display: block;" srch-type="office">Office</div>
					<div class="hr-dd-item" style="display: block;" srch-type="user">User</div>
				</div>
			</div>
				
		</div>
	</div>
	<div class="v-hr-tbl">
		<div class="v-tbl-header comp-row" id="hierarchy-list-header">
			<div class="v-tbl-line"></div>
			<!-- <div class="v-tbl-img"></div> -->
			<div class="v-tbl-name">Name</div>
			<div class="v-tbl-add">Address</div>
			<div class="v-tbl-role">Role</div>
			<div class="v-tbl-btns"></div>
			<div class="v-tbl-spacer"></div>
		</div>
		<div id="admin-com-list">
			<!-- Get the company list from the JavaScript -->
		</div>
	</div>
</div>
<div id="temp-message" class="hide"></div>
<script>
	$(document).ready(function(){
		$(document).attr("title", "Hierarchy");
		bindCompanyIntervalCriteriaSelection();
		searchAndDisplayCompanies("");
	});
</script>