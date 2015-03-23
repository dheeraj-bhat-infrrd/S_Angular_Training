<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<table class="v-um-tbl">
	<tr class="u-tbl-header">
		<td class="v-tbl-uname"><spring:message
				code="label.usermanagement.username.key" /></td>
		<td class="v-tbl-email"><spring:message code="label.emailid.key" /></td>
		<td class="v-tbl-rgn-adm text-center">Region</br/>Admin
		</td>
		<td class="v-tbl-of-adm text-center">Office<br />Admin
		</td>
		<td class="v-tbl-ln-of text-center">Loan<br />Officer
		</td>
		<td class="v-tbl-mail"></td>
		<td class="v-tbl-online"></td>
		<td class="v-tbl-rem"></td>
		<td class="v-tbl-edit"></td>
	</tr>
	<c:forEach var="userfromsearch" items="${userslist}">
		<c:set var="regionadmintickclass" value=""/>
		<c:set var="branchadmintickclass" value=""/>
		<c:set var="agenttickclass" value=""/>
		<c:set var="regstatustickclass" value=""/>
		<c:if test="${userfromsearch.isRegionAdmin != null && userfromsearch.isRegionAdmin}"><c:set var="regionadmintickclass" value="v-icn-tick"/></c:if>
		<c:if test="${userfromsearch.isBranchAdmin != null && userfromsearch.isBranchAdmin}"><c:set var="branchadmintickclass" value="v-icn-tick"/></c:if>
		<c:if test="${userfromsearch.isAgent != null && userfromsearch.isAgent}"><c:set var="agenttickclass" value="v-icn-tick"/></c:if>
		<!-- If status is 2, then user has not acted on invitation -->
		<c:if test="${userfromsearch.status == 2}"><c:set var="regstatustickclass" value="v-icn-fmail"/></c:if>
		<tr class="u-tbl-row">
			<td class="v-tbl-uname">${userfromsearch.displayName}</td>
			<td class="v-tbl-email">${userfromsearch.emailId}</td>
			<td class="v-tbl-rgn-adm v-tbl-icn ${regionadmintickclass}"></td>
			<td class="v-tbl-of-adm v-tbl-icn ${branchadmintickclass}"></td>
			<td class="v-tbl-ln-of v-tbl-icn ${agenttickclass}"></td>
			<td class="v-tbl-mail v-tbl-icn ${regstatustickclass}"></td>
			<td class="v-tbl-online v-tbl-icn v-icn-onl v-icn-onl-off"></td>
			<td class="v-tbl-rem v-tbl-icn v-icn-rem-user"></td>
			<td class="v-tbl-edit v-tbl-icn v-icn-edit-user"></td>
		</tr>
	</c:forEach>
	<!--
                <tr class="u-tbl-row u-tbl-row-sel">
                        
                </tr>
-->
</table>