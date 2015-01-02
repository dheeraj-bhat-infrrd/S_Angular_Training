<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="messageheader.jsp"></jsp:include>
<c:if test="${not empty userId }">
	<input type="hidden" value="${userId }" id="mh-userId">
</c:if>