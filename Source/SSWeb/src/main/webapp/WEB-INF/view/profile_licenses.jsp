<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty profile && not empty profile.licenses}">
	<c:set value="${profile.licenses.authorized_in}" var="authorisedInList"></c:set>
</c:if>
<c:choose>
	<c:when test="${not empty authorisedInList}">
		<c:forEach items="${authorisedInList}" var="authorisedIn">
			<input class="lp-auth-row lp-row clearfix prof-edditable-sin" value="${authorisedIn}">
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div><spring:message code="label.licenses.empty.key"></spring:message></div>
	</c:otherwise>
</c:choose>