<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set
	value="${cannonicalusersettings.companySettings.licenses.authorized_in }"
	var="authorisedInList"></c:set>
<c:choose>
	<c:when test="${not empty authorisedInList }">
		<c:forEach items="${authorisedInList }" var="authorisedIn">
			<input class="lp-auth-row lp-row clearfix prof-edditable-sin"
				value="${authorisedIn }">
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div>No authorisation added yet</div>
	</c:otherwise>
</c:choose>