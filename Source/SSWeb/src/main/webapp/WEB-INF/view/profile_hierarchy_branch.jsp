<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
					<!-- Individuals -->
					<c:choose>
						<c:when test="${not empty individuals}">
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
