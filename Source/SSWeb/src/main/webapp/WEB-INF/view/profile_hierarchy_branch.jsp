<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<!-- Individuals -->
<c:choose>
	<c:when test="${not empty individuals}">
		<c:forEach var="individual" items="${individuals}">
			<div class="bd-hr-item-l3 comp-individual" data-agentid="${individual.iden}">
				<div class="bd-hr-item bd-lt-l3 clearfix">
					<div class="float-left bd-hr-img pers-default-img comp-individual-prof-image" data-imageurl="${individual.profileImageUrlThumbnail}"></div>
					<div class="bd-hr-txt">${individual.contact_details.name}</div>
				</div>
			</div>
		</c:forEach>
	</c:when>
</c:choose>
