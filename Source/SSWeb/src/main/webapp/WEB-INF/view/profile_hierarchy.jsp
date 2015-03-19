<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:choose>
	<c:when	test="${user.companyAdmin}">
		<div class="bd-hr-lp-header"><spring:message code="label.ourcompany.key"/></div>
	</c:when>
	<c:when	test="${user.regionAdmin}">
		<div class="bd-hr-lp-header"><spring:message code="label.ourregion.key"/></div>
	</c:when>
	<c:when	test="${user.branchAdmin}">
		<div class="bd-hr-lp-header"><spring:message code="label.ourbranch.key"/></div>
	</c:when>
</c:choose>
<div class="bd-hr-lp-content-wrapper">
	<!-- Regions -->
	<c:choose>
		<c:when test="${not empty regions && user.companyAdmin}">
			<c:forEach var="region" items="${regions}">
				<div class="bd-hr-item-l1 comp-region" data-openstatus="closed" data-regionid="${region.regionId}">
					<div class="bd-hr-item bd-lt-l1 clearfix">
						<div class="bd-hr-txt">${region.region}</div>
					</div>
					<div class="bd-hr-item-l2 hide" id="comp-region-branches-${region.regionId}"></div>
				</div>
			</c:forEach>
		</c:when>
	</c:choose>

	<!-- Branches -->
	<c:choose>
		<c:when test="${not empty branches && (user.companyAdmin || user.regionAdmin)}">
			<c:forEach var="branch" items="${branches}">
				<div class="bd-hr-item-l1 comp-branch" data-openstatus="closed" data-branchid="${branch.branchId}">
					<div class="bd-hr-item bd-lt-l2 clearfix">
						<div class="bd-hr-txt">${branch.branch}</div>
					</div>
					<div class="bd-hr-item-l2 hide" id="comp-branch-individuals-${branch.branchId}"></div>
				</div>
			</c:forEach>
		</c:when>
	</c:choose>

	<!-- Individuals -->
	<c:choose>
		<c:when test="${not empty individuals && (user.companyAdmin || user.regionAdmin || user.branchAdmin)}">
			<c:forEach var="individual" items="${individuals}">
				<div class="bd-hr-item-l1 comp-individual" data-agentid="${individual.iden}">
					<div class="bd-hr-item bd-lt-l3 clearfix">
						<div class="float-left bd-hr-img comp-individual-prof-image" data-imageurl="${individual.profileImageUrl}"></div>
						<div class="bd-hr-txt">${individual.contact_details.name}</div>
					</div>
				</div>
			</c:forEach>
		</c:when>
	</c:choose>
</div>