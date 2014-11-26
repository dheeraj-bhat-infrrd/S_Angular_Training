<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty message.message || not empty param.message}">
<div class="${message.type.name}">
	<ul style="list-style-type: none;">
		<li>${message.message} ${param.message}</li>
	</ul>
</div>
</c:if>