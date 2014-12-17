<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.login.title.key" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
    <div class="header-main-wrapper">
        <div class="container clearfix header-container">
            <div class="header-logo float-left"></div>
            <div class="header-links float-left clearfix">
                <div class="header-links-item">Dashboard</div>
                <div class="header-links-item">Company</div>
                <div class="header-links-item">Build Survey</div>
                <div class="header-links-item">User Management</div>
            </div>
            <div class="header-user-info float-right clearfix">
                <div class="float-left user-info-initial">J</div>
                <div class="float-left user-info-seperator"></div>
                <div class="float-left user-info-logo"></div>
            </div>
        </div>
    </div>