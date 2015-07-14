<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty companyObj && not empty companyObj.company}">
	<div class="v-hr-header">${companyObj.company}</div>
</c:if>
<div class="v-hr-tbl-wrapper">
	<table class="v-hr-tbl">
		<tr class="v-tbl-header" id="hierarchy-list-header">
			<td class="v-tbl-line"></td>
			<td class="v-tbl-name"><spring:message code="label.name.key" /></td>
			<td class="v-tbl-add"><spring:message code="label.emailid.key" /></td>
			<td class="v-tbl-role"><spring:message code="label.role.key" /></td>
			<td class="v-tbl-btns"></td>
			<td class="v-tbl-spacer"></td>
		</tr>
		<c:if test="${not empty regions}">
			<c:forEach var="region" items="${regions}">
				<tr id="tr-region-${region.regionId}" clicked="false"
					class="v-tbl-row v-tbl-row-sel region-row"
					data-regionid="${region.regionId}">
					<td class="v-tbl-line">
						<div class="v-line-rgn"></div>
					</td>
					<td class="v-tbl-name">${region.regionName}</td>
					<td class="v-tbl-add"><c:if
							test="${not empty region.address1}">${region.address1}</c:if>&nbsp;<c:if
							test="${not empty region.address2}">${region.address2}</c:if></td>
					<td class="v-tbl-role"></td>
					<td class="v-tbl-btns">
						<div class="clearfix v-tbl-icn-wraper">
							<div class="float-left v-tbl-icn v-icn-close region-del-icn hidden"
								data-regionid="${region.regionId}"></div>
							<div class="float-left v-tbl-icn v-icn-edit region-edit-icn"
								clicked="false" data-regionid="${region.regionId}"></div>
						</div>
					</td>
					<td class="v-tbl-spacer"></td>
				</tr>
				<tr class="v-tbl-row v-tbl-row-sel tr-region-edit hide">
					<td colspan="7" id="td-region-edit-${region.regionId}"
						class="td-region-edit">
						<!--edit form comes here  -->
					</td>
				</tr>
			</c:forEach>
		</c:if>
		<c:if test="${not empty branches}">
			<c:forEach var="branch" items="${branches}">
				<tr id="tr-branch-row-${branch.branchId}" clicked="false"
					class="v-tbl-row v-tbl-row-sel v-tbl-row-brnch branch-row sel-b${branch.branchId}"
					data-branchid="${branch.branchId}">
					<td class="v-tbl-line">
						<div class="v-line-brnch v-line-comp-brnch"></div>
					</td>
					<td class="v-tbl-name">${branch.branchName}</td>
					<td class="v-tbl-add"><c:if
							test="${not empty branch.address1}">${branch.address1}</c:if>&nbsp;<c:if
							test="${not empty branch.address2}">${branch.address2}</c:if></td>
					<td class="v-tbl-role"></td>
					<td class="v-tbl-btns">
						<div class="clearfix v-tbl-icn-wraper">
							<div class="float-left v-tbl-icn v-icn-close branch-del-icn hidden"
								data-branchid="${branch.branchId}"></div>
							<div class="float-left v-tbl-icn v-icn-edit branch-edit-icn"
								clicked="false" data-branchid="${branch.branchId}"></div>
						</div>
					</td>
					<td class="v-tbl-spacer"></td>
				</tr>
				<tr class="v-tbl-row v-tbl-row-sel tr-branch-edit hide">
					<td colspan="7" id="td-branch-edit-${branch.branchId}"
						class="td-branch-edit">
						<!--edit form for branch comes here  -->
					</td>
				</tr>
			</c:forEach>
		</c:if>
		<c:if test="${not empty individuals}">
			<c:forEach var="compUser" items="${individuals}">
				<tr id="user-row-${compUser.userId}" clicked="false"
					class="v-tbl-row v-tbl-row-sel v-tbl-row-ind sel-u${compUser.userId}"
					data-userid="${compUser.userId}">
					<td class="v-tbl-line">
						<div class="v-line-ind v-line-comp-ind"></div>
					</td>
					<td class="v-tbl-name">${compUser.displayName}</td>
					<td class="v-tbl-add"><c:if
							test="${not empty compUser.emailId}">${compUser.emailId}</c:if></td>
					<td class="v-tbl-role"><c:choose>
							<c:when
								test="${(compUser.isRegionAdmin || compUser.isBranchAdmin) && compUser.isAgent}">
								<spring:message code="label.admin.key" />&#44;&nbsp;<spring:message
									code="label.user.key" />
							</c:when>
							<c:when
								test="${compUser.isRegionAdmin || compUser.isBranchAdmin}">
								<spring:message code="label.admin.key" />
							</c:when>
							<c:when test="${compUser.isAgent}">
								<spring:message code="label.user.key" />
							</c:when>
						</c:choose></td>
					<td class="v-tbl-btns">
						<div class="clearfix v-tbl-icn-wraper">
							<div class="float-left v-tbl-icn v-icn-close user-del-icn hidden"
								data-userid="${compUser.userId}"></div>
							<div class="float-left v-tbl-icn v-icn-edit user-edit-icn"
								clicked="false" data-userid="${compUser.userId}" data-iscom-admin="${compUser.isOwner}"></div>
						</div>
					</td>
					<td class="v-tbl-spacer"></td>
				</tr>
				<tr id="user-edit-row-${compUser.userId}"
					class="v-tbl-row v-tbl-row-sel tr-user-edit user-edit-row hide">
					<td id="user-details-and-assignments-${compUser.userId}"
						colspan="7" class="td-user-edit user-assignment-edit-div">
						<!--edit form comes here for the user -->
					</td>
				</tr>
			</c:forEach>
		</c:if>
	</table>
</div>
