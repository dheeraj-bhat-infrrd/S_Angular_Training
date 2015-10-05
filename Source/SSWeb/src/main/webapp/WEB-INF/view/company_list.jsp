<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.prolist.title.key" /></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body>
	<div class="body-wrapper">
		<div class="hdr-wrapper">
			<div class="container hdr-container clearfix">
				<div class="float-left hdr-logo"></div>
				<div class="float-right clearfix hdr-btns-wrapper">
					<div class="float-left hdr-log-btn hdr-log-reg-btn">
						<spring:message code="label.signin.key" />
					</div>
					<div class="float-left hdr-reg-btn hdr-log-reg-btn">
						<spring:message code="label.joinus.key" />
					</div>
				</div>
			</div>
		</div>
		<div id="err-nw-wrapper" class="err-nw-wrapper">
			<span class="err-new-close"></span>
			<span id="err-nw-txt"></span>
		</div>
		
		<div class="ctnt-wrapper clearfix">
			<div class="ctnt-container container">
				<div class="row">
					<div class="ctnt-left-item col-lg-9 col-md-9 col-sm-9 col-xs-12">
						<div class="ctnt-list-header clearfix">
							<div class="ctnt-list-header-left float-left">
								<spring:message code="label.companyfoundfor.key" />
									<c:if test="${not empty verticalName}">
										<span class="srch-name">${verticalName}</span>
									</c:if>
							</div>
							<div class="ctnt-list-header-right float-right">
								<c:choose>
									<c:when test="${not empty numFound && numFound > 0}">
										<span id="srch-num" class="srch-num">${numFound} </span>
									</c:when>
									<c:otherwise>
										<span id="srch-num" class="srch-num"><spring:message code="label.no.key" /> </span>
									</c:otherwise>
								</c:choose>
								<spring:message code="label.companyfound.key" />
							</div>
						</div>
						
						<div id="ctnt-list-wrapper" class="ctnt-list-wrapper">
							<c:if test="${not empty companyList }">
								<c:forEach var="companyItem" items="${companyList}">
									<div class="ctnt-list-item clearfix " data-profilename="${companyItem.profileName}">
										<c:choose>
											<c:when test="${not empty  companyItem.logo}">
													<div class="float-left ctnt-list-item-img" style="background-image: url(${companyItem.logo});"></div>
											</c:when>
											<c:otherwise>
												<div class="float-left ctnt-list-item-img ctnt-com-default-img"></div>
											</c:otherwise>
										</c:choose>
										<div class="float-left ctnt-list-item-txt-wrap">
											<div class="ctnt-item-name user-display-name">${companyItem.profileName}</div>
											<c:if test="${ not empty companyItem.contact_details && not empty companyItem.contact_details.title }">
												<div class="ctnt-item-desig">${companyItem.contact_details.title}</div>
											</c:if>
											<c:if test="${not empty companyItem.contact_details && not empty companyItem.contact_details.about_me}">
												<div class="ctnt-item-comment">${companyItem.contact_details.about_me }</div>
											</c:if>
										</div>
									</div>
								</c:forEach>
							</c:if>
						</div>
					</div>
					<div class="ctnt-right-item col-lg-3 col-md-3 col-sm-3 col-xs-12 ads-container">
						<a href="http://mbshighway.com/sosur" target="_blank">
						<img class="mbs-highwayad" src="/resources/images/Sidebar_MBSHighway.png"></a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer_new.jsp"></jsp:include>
	<jsp:include page="scripts.jsp"></jsp:include>
	<script>
	$(document).ready(function() {
		adjustTextContainerWidthOnResize();
		
		$('.ctnt-list-item').on('click',function(e){
			var profLink = $(this).attr('data-profilename');
			//window.open(window.location.origin + '/pages/company/'+profLink , '_blank');
			window.open(getLocationOrigin() + '/pages/company/'+profLink , '_blank');
		});
		
		
		$(window).resize(function() {
			if ($(window).width() < 768) {
				adjustTextContainerWidthOnResize();
			}
		});
	});
	</script>
</body>
</html>