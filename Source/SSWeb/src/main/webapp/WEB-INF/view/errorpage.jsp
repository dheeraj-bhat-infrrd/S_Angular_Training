<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Error</title>
</head>
<body>
	<c:choose>
		<c:when test="${not empty errorMessage }">
			<h1>${errorMessage }</h1>
		</c:when>
		<c:otherwise>
			<h1>Sorry Something unexpected happened. Please log in back and
				try again</h1>
		</c:otherwise>
	</c:choose>
</body>
</html>