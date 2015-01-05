<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
	<c:when test="${not empty regions}">
		<c:forEach var="region" items="${regions}">
			<div class="hm-dd-item" data-regionid="${region.regionId}">${region.region}</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<spring:message code = "label.noregionexist.key"/>
	</c:otherwise>
</c:choose>
