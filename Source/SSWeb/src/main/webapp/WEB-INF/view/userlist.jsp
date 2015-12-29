<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Account masters 1=Individual, 2=Team, 3=Company,4=Enterprise,5=Free Account -->
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:set var="accountTypeId" value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" />

<table class="v-um-tbl">
	<tr id="u-tbl-header" class="u-tbl-header" data-num-found="${numFound}">
		<td class="v-tbl-uname"><spring:message code="label.usermanagement.username.key" /></td>
		<td class="v-tbl-email"><spring:message code="label.emailid.key" /></td>
		<td class="v-tbl-rgn-adm text-center">
			<c:if test="${accountTypeId == 4}">
				<spring:message code="label.region.key" /><br />
				<spring:message code="label.admin.key" />
			</c:if>
		</td>
		<td class="v-tbl-of-adm text-center">
			<c:if test="${accountTypeId == 4 || accountTypeId == 3}">
				<spring:message code="label.office.key" /><br />
				<spring:message code="label.admin.key" />
			</c:if>
		</td>
		<td class="v-tbl-ln-of text-center"><spring:message code="label.individual.key" /></td>
		<td class="v-tbl-mail"></td>
		<td class="v-tbl-wid"></td>
		<td class="v-tbl-online"></td>
		<td class="v-tbl-rem"></td>
		<td class="v-tbl-edit"></td>
		<td class="v-tbl-spacer"></td>
	</tr>
	<c:choose>
		<c:when test="${not empty userslist}">
			<c:forEach var="userfromsearch" items="${userslist}">
				<!-- For Region admin -->
				<c:if test="${accountTypeId == 4}">
					<c:set var="regionadmintickclass" value="" />
					<c:if test="${userfromsearch.isRegionAdmin != null && userfromsearch.isRegionAdmin}">
						<c:set var="regionadmintickclass" value="v-icn-tick" />
					</c:if>
				</c:if>

				<!-- For Branch admin -->
				<c:if test="${accountTypeId == 4 || accountTypeId == 3}">
					<c:set var="branchadmintickclass" value="" />
					<c:if test="${userfromsearch.isBranchAdmin != null && userfromsearch.isBranchAdmin}">
						<c:set var="branchadmintickclass" value="v-icn-tick" />
					</c:if>
				</c:if>

				<!-- For Agent -->
				<c:set var="agenttickclass" value="" />
				<c:if test="${userfromsearch.isAgent != null && userfromsearch.isAgent}">
					<c:set var="agenttickclass" value="v-icn-tick" />
				</c:if>

				<!-- If status is 2, then user has not acted on invitation -->
				<c:set var="regstatustickclass" value="" />
				<c:set var="userstatustickclass" value="v-icn-verified" />
				<c:if test="${userfromsearch.status == 2}">
					<c:set var="regstatustickclass" value="v-icn-fmail" />
					<c:set var="userstatustickclass" value="v-icn-notverified" />
				</c:if>

				<!-- if admin can edit -->
				<c:choose>
					<c:when test="${userfromsearch.canEdit}">
						<c:set var="admincaneditclass" value="v-tbl-icn" />
					</c:when>
					<c:otherwise>
						<c:set var="admincaneditclass" value="v-tbl-icn-disabled" />
					</c:otherwise>
				</c:choose>

				<c:choose>
					<c:when test="${userfromsearch.canEdit && user.userId != userfromsearch.userId}">
						<c:set var="admincanremoveclass" value="v-tbl-icn" />
					</c:when>
					<c:otherwise>
						<c:set var="admincanremoveclass" value="v-tbl-icn-disabled" />
					</c:otherwise>
				</c:choose>

				<tr class="u-tbl-row user-row" id="user-row-${userfromsearch.userId}" data-editable="${userfromsearch.canEdit}">
					<td class="v-tbl-uname fetch-name" data-first-name="${userfromsearch.firstName}" data-last-name="${userfromsearch.lastName}"
						data-user-id="${userfromsearch.userId}">${userfromsearch.displayName}</td>
					<td class="v-tbl-email fetch-email">${userfromsearch.emailId}</td>
					<td class="v-tbl-rgn-adm ${regionadmintickclass}"></td>
					<td class="v-tbl-of-adm ${branchadmintickclass}"></td>
					<td class="v-tbl-ln-of ${agenttickclass}"></td>
					<c:choose>
						<c:when test="${not empty regstatustickclass}">
							<td class="v-tbl-mail ${admincaneditclass} ${regstatustickclass}"
								title="<spring:message code="label.resendmail.key" />"></td>
						</c:when>
						<c:otherwise>
							<td class="v-tbl-mail ${admincaneditclass}"></td>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${userfromsearch.isAgent != null && userfromsearch.isAgent}">
							<td class="v-tbl-wid v-icn-wid ${admincaneditclass}"
								title="<spring:message code="label.widget.key" />"
								onclick="generateWidget($(this),${ userfromsearch.userId }, 'individual');"></td>
						</c:when>
						<c:otherwise>
							<td class="v-tbl-spacer" ></td>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${userfromsearch.status == 2}">
							<td class="v-tbl-online v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.notverified.key" />"></td>
						</c:when>
						<c:otherwise>
							<td class="v-tbl-online v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.verified.key" />"></td>
						</c:otherwise>
					</c:choose>
					<td class="v-tbl-rem ${admincanremoveclass} v-icn-rem-user" title="<spring:message code="label.remove.key" />"></td>
					<td class="v-tbl-edit ${admincaneditclass} v-icn-edit-user edit-user" title="<spring:message code="label.edit.key" />"></td>
					<c:choose>
					 <c:when test="${user.userId != userfromsearch.userId}">
				   		<td class="v-tbl-online v-tbl-icn v-icn-login user-login-icn" data-iden="${userfromsearch.userId}" title="login as"></td>
				   </c:when>
				   <c:otherwise>
							<td class="v-tbl-spacer" ></td>
						</c:otherwise>
				   </c:choose>
				</tr>
				<tr class="u-tbl-row u-tbl-row-sel hide user-assignment-edit-row">
					<td id="user-details-and-assignments-${userfromsearch.userId}" class="u-tbl-edit-td user-assignment-edit-div" colspan="11">
						<!-- data populated from um-edit-row.jsp -->
					</td>
				</tr>
				
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr class="u-tbl-row"><spring:message code="label.nousersfound.key" /></tr>
		</c:otherwise>
	</c:choose>
</table>
<script>
	bindAppUserLoginEvent();
</script>