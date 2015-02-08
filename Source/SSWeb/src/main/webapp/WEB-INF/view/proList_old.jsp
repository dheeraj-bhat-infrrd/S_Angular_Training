<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.findapro.title.key" /></title>
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
	<div class="t-main-container">
		<div class="t-header-main">
			<div class="t-heade-wrapper t-container clearfix">
				<div class="t-logo float-left"></div>
			</div>
		</div>
		<div class="t-pro-wrapper">
			<div class="t-container clearfix">
				<div class="float-left t-pro-search-txt">
					<spring:message code="label.profilesearch.key" /><span class="t-pro-search-name"> ${patternFirst} ${patternLast}</span>
				</div>
			</div>
		</div>
		<div class="t-main-wrapper t-container clearfix">
			<div class="t-pro-list-wrapper">
			
				<!-- Populating user search results -->
				<c:choose>
					<c:when test="${not empty users}">
						<c:forEach var="user" items="${users}">
							<div class="t-pro-item clearfix">
								<div class="t-pro-img-wrapper float-left">
									<img src="../SSWeb-1.0/resources/images/t-pt.jpg" width="100">
								</div>
								<div class="float-left t-pro-txt-wrapper">
									<div class="t-pro-line1">${user.displayName}</div>
									<div class="t-pro-line2">${user.displayName}</div>
									<div class="t-pro-line3">${user.emailId}</div>
								</div>
								<div class="float-left t-pro-btn t-pro-btn-adj"><spring:message code="label.review.key" /></div>
								<div class="float-left t-pro-btn"><spring:message code="label.viewprofile.key" /></div>
							</div>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<spring:message code="label.noregionexist.key" />
					</c:otherwise>
				</c:choose>

				<!-- Example user search results -->
				<div class="t-pro-item clearfix hide">
					<div class="t-pro-img-wrapper float-left">
						<img src="../SSWeb-1.0/resources/images/t-pt.jpg" width="100">
					</div>
					<div class="float-left t-pro-txt-wrapper">
						<div class="t-pro-line1">John Doe</div>
						<div class="t-pro-line2">Some more info on John Doe</div>
						<div class="t-pro-line3">http://johndoe.com</div>
					</div>
					<div class="float-left t-pro-btn t-pro-btn-adj">Review</div>
					<div class="float-left t-pro-btn">View Profile</div>
				</div>
			</div>
		</div>
		<div class="footer-main-wrapper">
			<div class="t-container text-center footer-text">
                <spring:message code="label.copyright.key"/> &copy; <spring:message code="label.footer.socialsurvey.key"/> <span class="center-dot">.</span> <spring:message code="label.allrightscopyright.key"/>
			</div>
		</div>
	</div>

	<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
	<script>
        $(document).ready(function(){
            
        });
    </script>
</body>
</html>