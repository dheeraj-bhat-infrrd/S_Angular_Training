<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
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
				<input id="hr-comp-sel" class="v-comp-inp" placeholder="Search Company">
				<span id="hr-comp-icn" class="um-search-icn"></span>
			</div>
		</div>
	</div>
 </div>
 <div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div id="comp-hierarchy-cont" class="container v-hr-container">
	<div class="v-hr-tbl">
		<div class="v-tbl-header comp-row" id="hierarchy-list-header">
			<div class="v-tbl-line"></div>
			<div class="v-tbl-name">Name</div>
			<div class="v-tbl-add">Email Address</div>
			<div class="v-tbl-role">Role</div>
			<div class="v-tbl-btns"></div>
			<div class="v-tbl-spacer"></div>
		</div>
		<div id="admin-com-list">
			<jsp:include page="admin_company_list.jsp"></jsp:include>
		</div>
	</div>
</div>
 <div id="temp-message" class="hide"></div>
