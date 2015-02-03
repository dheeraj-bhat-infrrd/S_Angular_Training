<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if
	test="${not empty cannonicalusersettings && not empty cannonicalusersettings.companySettings}">
<c:set value="${cannonicalusersettings.companySettings.achievements }"
	var="achievements"></c:set>
</c:if>
<c:choose>
	<c:when test="${not empty achievements }">
		<c:forEach items="${achievements }" var="achievement">
			<input class="lp-ach-row lp-row clearfix prof-edditable-sin"
				value="${achievement.achievement }">
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div><spring:message code="label.achievement.empty.key"></spring:message></div>
	</c:otherwise>
</c:choose>