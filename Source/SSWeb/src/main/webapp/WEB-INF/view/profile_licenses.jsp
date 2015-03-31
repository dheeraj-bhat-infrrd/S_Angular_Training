<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty profileSettings && not empty profileSettings.licenses}">
	<c:set value="${profileSettings.licenses.authorized_in}" var="authorisedInList"></c:set>
</c:if>
<c:choose>
	<c:when test="${not empty authorisedInList}">
		<c:forEach items="${authorisedInList}" var="authorisedIn">
			<input class="lp-auth-row lp-row clearfix prof-edditable-sin-agent" value="${authorisedIn}">
			<div class="float-right lp-ach-item-img" data-type="license"></div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div><spring:message code="label.licenses.empty.key"></spring:message></div>
	</c:otherwise>
</c:choose>