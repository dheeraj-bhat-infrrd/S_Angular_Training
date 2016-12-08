<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<body>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left text-center">
					<spring:message code="label.reviewsmonitor.key" />
				</div>
			</div>
		</div>
	</div>
	<div class="vendasta-container">
		<iframe id="vendasta-iframe" height="600px" width="1366px" src="http://social-survery-sandbox.steprep-demo-hrd.appspot.com/overview/?sso_token=RM101" position="inherit" allowfullscreen="">
		  Vendasta Integration
		</iframe>
	</div>
</body>

	