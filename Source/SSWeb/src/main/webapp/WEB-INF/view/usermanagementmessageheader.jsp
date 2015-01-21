<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty message.message || not empty param.message}">
	<div class="${message.type.name}" id="common-message-header">
		<div class="um-error-message">${message.message}${param.message}</div>
	</div>
</c:if>


<style>
.um-error-message {
	width: 75%;
	margin: 0 auto;
}
</style>