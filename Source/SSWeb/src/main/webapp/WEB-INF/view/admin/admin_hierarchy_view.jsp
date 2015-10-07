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
		<div class="v-um-hdr-right float-right">
			<input id="hr-comp-sel" class="v-comp-inp"
				placeholder="Search Company"> <span id="hr-comp-icn"
				class="um-search-icn"></span>
		</div>
	</div>
	<div class="v-hr-tbl">
		<div class="v-tbl-header comp-row" id="hierarchy-list-header">
			<div class="v-tbl-line"></div>
			<div class="v-tbl-img"></div>
			<div class="v-tbl-name">Name</div>
			<div class="v-tbl-add">Email Address</div>
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
		searchAndDisplayCompanies("");
	});
</script>