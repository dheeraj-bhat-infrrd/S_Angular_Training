<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!-- Branches -->
<c:choose>
	<c:when test="${not empty branches}">
		<c:forEach var="branch" items="${branches}">
			<div class="bd-hr-item-l2 comp-region-branch" data-openstatus="closed" data-branchid="${branch.branchId}">
				<div class="bd-hr-item bd-lt-l2 clearfix">
					<div id="" class="float-left profile-image-display" style="">
						<span id="">${fn:substring(branch.branch, 0, 1)}</span>
					</div>
					<div class="float-left margin-left-10 bd-hr-txt">${branch.branch}</div>
				</div>
				<div class="bd-hr-item-l3 hide" id="comp-branch-individuals-${branch.branchId}"></div>
			</div>
		</c:forEach>
	</c:when>
</c:choose>

<!-- Individuals -->
<c:choose>
	<c:when test="${not empty individuals}">
		<c:forEach var="individual" items="${individuals}">
			<div class="bd-hr-item-l2 comp-region-individual" data-agentid="${individual.iden}">
				<div class="bd-hr-item bd-lt-l3 clearfix">
					<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image" data-imageurl="${individual.profileImageUrl}"></div>
					<div class="bd-hr-txt">${individual.contact_details.name}</div>
				</div>
			</div>
		</c:forEach>
	</c:when>
</c:choose>
