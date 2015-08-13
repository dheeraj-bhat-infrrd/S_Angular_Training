<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="messageheader.jsp"></jsp:include>
<c:if test="${not empty userId }">
	<input type="hidden" value="${userId }" id="mh-userId">
</c:if>
<c:if test="${not empty existingUserId }">
	<input type="hidden" value="${existingUserId }" id="mh-existing-userId">
</c:if>