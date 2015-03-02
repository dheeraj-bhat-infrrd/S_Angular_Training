<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
					<!-- Branches -->
					<c:choose>
						<c:when test="${not empty branches}">
							<c:forEach var="branch" items="${branches}">
								<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-region-branch" data-openstatus="closed" data-branchid="${branch.branchId}">
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
						<c:when test="${not empty individuals}">
							<c:forEach var="individual" items="${individuals}">
								<div class="lp-sub lp-sub-l1 bord-left-panel mgn-left-0 comp-region-individual" data-agentid="${individual.iden}">
									<div class="lp-sub-header clearfix flat-left-bord">
										<div class="lp-sub-img lp-pers-img comp-individual-prof-image" data-imageurl="${individual.profileImageUrl}"></div>
										<div class="lp-sub-txt">${individual.contact_details.name}</div>
									</div>
								</div>
							</c:forEach>
						</c:when>
					</c:choose>
