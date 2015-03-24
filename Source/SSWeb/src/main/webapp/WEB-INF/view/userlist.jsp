<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table class="v-um-tbl">
	<tr class="u-tbl-header">
		<td class="v-tbl-uname"><spring:message code="label.usermanagement.username.key" /></td>
		<td class="v-tbl-email"><spring:message code="label.emailid.key" /></td>
		<td class="v-tbl-rgn-adm text-center"><spring:message code="label.region.key" /><br /><spring:message code="label.admin.key" /></td>
		<td class="v-tbl-of-adm text-center"><spring:message code="label.office.key" /><br /><spring:message code="label.admin.key" /></td>
		<td class="v-tbl-ln-of text-center"><spring:message code="label.individual.key" /></td>
		<td class="v-tbl-mail"></td>
		<td class="v-tbl-online"></td>
		<td class="v-tbl-rem"></td>
		<td class="v-tbl-edit"></td>
	</tr>
	<c:forEach var="userfromsearch" items="${userslist}">
		<!-- For Region admin -->
		<c:set var="regionadmintickclass" value=""/>
		<c:if test="${userfromsearch.isRegionAdmin != null && userfromsearch.isRegionAdmin}">
			<c:set var="regionadmintickclass" value="v-icn-tick"/>
		</c:if>

		<!-- For Branch admin -->
		<c:set var="branchadmintickclass" value=""/>
		<c:if test="${userfromsearch.isBranchAdmin != null && userfromsearch.isBranchAdmin}">
			<c:set var="branchadmintickclass" value="v-icn-tick"/>
		</c:if>

		<!-- For Agent -->
		<c:set var="agenttickclass" value=""/>
		<c:if test="${userfromsearch.isAgent != null && userfromsearch.isAgent}">
			<c:set var="agenttickclass" value="v-icn-tick"/>
		</c:if>

		<!-- If status is 2, then user has not acted on invitation -->
		<c:set var="regstatustickclass" value=""/>
		<c:set var="userstatustickclass" value="v-icn-onl"/>
		<c:if test="${userfromsearch.status == 2}">
			<c:set var="regstatustickclass" value="v-icn-fmail"/>
			<c:set var="userstatustickclass" value="v-icn-off"/>
		</c:if>
		
		<tr class="u-tbl-row" id="user-row-${userfromsearch.userId}">
			<td class="v-tbl-uname fetch-name" data-first-name="${userfromsearch.firstName}" data-last-name="${userfromsearch.lastName}" data-user-id="${userfromsearch.userId}">${userfromsearch.displayName}</td>
			<td class="v-tbl-email fetch-email">${userfromsearch.emailId}</td>
			<td class="v-tbl-rgn-adm ${regionadmintickclass}"></td>
			<td class="v-tbl-of-adm ${branchadmintickclass}"></td>
			<td class="v-tbl-ln-of ${agenttickclass}"></td>
			<c:choose>
				<c:when test="${not empty regstatustickclass}">
					<td class="v-tbl-mail v-tbl-icn ${regstatustickclass}" title="<spring:message code="label.resendmail.key" />"></td>
				</c:when>
				<c:otherwise>
					<td class="v-tbl-mail v-tbl-icn"></td>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${not empty userstatustickclass}">
					<td class="v-tbl-online v-tbl-icn ${userstatustickclass} v-icn-onl-off" title="<spring:message code="label.active.key" />"></td>
				</c:when>
				<c:otherwise>
					<td class="v-tbl-online v-tbl-icn v-icn-onl-off" title="<spring:message code="label.inactive.key" />"></td>
				</c:otherwise>
			</c:choose>
			<td class="v-tbl-rem v-tbl-icn v-icn-rem-user" title="Remove"></td>
			<td class="v-tbl-edit v-tbl-icn v-icn-edit-user" title="Edit"></td>
		</tr>
		<tr class="u-tbl-row u-tbl-row-sel hide">
			<td id="user-details-and-assignments-${userfromsearch.userId}" class="u-tbl-edit-td" colspan="9">
				<!-- data populated from um-edit-row.jsp -->
			</td>
		</tr>
	</c:forEach>
	<!--<tr class="u-tbl-row u-tbl-row-sel"></tr>-->
</table>