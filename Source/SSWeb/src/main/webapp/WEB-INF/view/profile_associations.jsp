<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty cannonicalusersettings && not empty cannonicalusersettings.companySettings}">
	<c:set value="${cannonicalusersettings.companySettings.associations}" var="associations"></c:set>
</c:if>
<c:choose>
	<c:when test="${not empty associations}">
		<c:forEach items="${associations}" var="association">
			<input class="lp-assoc-row lp-row clearfix prof-edditable-sin" value="${association.name}">
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div><spring:message code="label.membership.empty.key"></spring:message></div>
	</c:otherwise>
</c:choose>