<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<div id="hm-header-main-wrapper" class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">
				<spring:message code="label.viewcompanyhierachy.key" />
			</div>
			<c:if
				test="${not empty realTechAdminId && entityType == 'companyId' }">
				<div class="float-right hm-header-right text-center"
					onclick="javascript:showMainContent('./hierarchyupload.do')">
					<spring:message code="label.header.Hierarchyupload.key" />
				</div>
			</c:if>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./viewhierarchy.do');">
				<spring:message code="label.viewcompanyhierachy.key" />
			</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./showusermangementpage.do')">
				<spring:message code="label.header.editteam.key" />
			</div>
			<c:if test="${canAdd}">
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./showbuildhierarchypage.do')">
				<spring:message code="label.header.buildhierarchy.key" />
			</div>
			</c:if>

		</div>
	</div>
</div>
<div id="server-message" class="hide">
	<jsp:include page="messageheader.jsp"></jsp:include>
</div>
<div class="container v-hr-container">
	<div class="v-hr-header">${companyName}</div>
	<div class="v-hr-tbl-wrapper">
		<table class="v-hr-tbl">
			<tr class="v-tbl-header" id="hierarchy-list-header">
				<td class="v-tbl-line"></td>
				<!-- <td class="v-tbl-img"> -->
				<td class="v-tbl-name"><spring:message code="label.name.key" /></td>
				<td class="v-tbl-add"><spring:message code="label.address.key" /></td>
				<td class="v-tbl-role"><spring:message code="label.role.key" /></td>
				<td class="v-tbl-btns v-tbl-btns-hr"></td>
				<td class="v-tbl-spacer"></td>
			</tr>
			<!--  regions and branches list appear here-->
		</table>
	</div>
</div>
<div id="temp-message" class="hide"></div>
<script>
	$(document).ready(function() {
		fetchHierarchyViewList();
	});
</script>