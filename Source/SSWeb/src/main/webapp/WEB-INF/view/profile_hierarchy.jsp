<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<div class="prof-left-row prof-left-assoc bord-bot-dc">
	<div class="left-assoc-wrapper clearfix">
		<c:choose>
			<c:when	test="${user.companyAdmin}">
				<div class="left-panel-header lp-align-adj"><spring:message code="label.ourcompany.key"/></div>
			</c:when>
			<c:when	test="${user.regionAdmin}">
				<div class="left-panel-header lp-align-adj"><spring:message code="label.ourregion.key"/></div>
			</c:when>
			<c:when	test="${user.branchAdmin}">
				<div class="left-panel-header lp-align-adj"><spring:message code="label.ourbranch.key"/></div>
			</c:when>
		</c:choose>
		<div class="left-panel-content left-panel-content-adj">

			<!-- Regions -->
			<c:choose>
				<c:when test="${not empty regions && user.companyAdmin}">
					<c:forEach var="region" items="${regions}">
						<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-region" data-openstatus="closed" data-regionid="${region.regionId}">
							<div class="lp-sub-header clearfix flat-left-bord">
								<div class="lp-sub-img icn-company"></div>
								<div class="lp-sub-txt">${region.region}</div>
								<div class="lpsub-2 hide" id="comp-region-branches-${region.regionId}"></div>
							</div>
						</div>
					</c:forEach>
				</c:when>
			</c:choose>

			<!-- Branches -->
			<c:choose>
				<c:when test="${not empty branches && (user.companyAdmin || user.regionAdmin)}">
					<c:forEach var="branch" items="${branches}">
						<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-branch" data-openstatus="closed" data-branchid="${branch.branchId}">
							<div class="lp-sub-header clearfix flat-left-bord">
								<div class="lp-sub-img icn-rgn"></div>
								<div class="lp-sub-txt">${branch.branch}</div>
								<div class="lpsub-2 hide" id="comp-branch-individuals-${branch.branchId}"></div>
							</div>
						</div>
					</c:forEach>
				</c:when>
			</c:choose>

			<!-- Individuals -->
			<c:choose>
				<c:when test="${not empty individuals && (user.companyAdmin || user.regionAdmin || user.branchAdmin)}">
					<c:forEach var="individual" items="${individuals}">
						<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-individual" data-agentid="${individual.iden}">
							<div class="lp-sub-header clearfix flat-left-bord">
								<div class="lp-sub-img lp-pers-img comp-individual-prof-image" data-imageurl="${individual.profileImageUrl}"></div>
								<div class="lp-sub-txt">${individual.contact_details.name}</div>
							</div>
						</div>
					</c:forEach>
				</c:when>
			</c:choose>

		</div>
	</div>
</div>