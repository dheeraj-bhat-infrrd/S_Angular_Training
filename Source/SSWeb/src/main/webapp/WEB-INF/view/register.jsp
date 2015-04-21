<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.login.title.key" /></title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" sizes="16x16">
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
                <div class="t-login-wrapper float-right clearfix">
                    <input class="t-inp" placeholder="Username">
                    <input class="t-inp" type="password" placeholder="Password">
                    <div class="t-btn">Login</div>
                </div>
            </div>
        </div>
        <div class="t-pro-wrapper">
            <div class="t-container clearfix">
                <div class="float-left t-pro-txt">Looking for a Pro?</div>
                <div class="float-right t-pro-search">
                    <input class="t-inp" placeholder="First Name">
                    <input class="t-inp" placeholder="Last Name">
                    <div class="t-btn">Search</div>
                </div>
            </div>
        </div>
        <div class="t-main-wrapper t-container clearfix">
            <div class="float-left t-main-pic">
                <div class="t-bg-sample-img"></div>
            </div>
            <div class="float-left t-main-reg">
                <div class="t-reg-wrapper">
                    <input class="t-reg-txt" placeholder="First Name">
                    <input class="t-reg-txt" placeholder="Last Name">
                    <input class="t-reg-txt" placeholder="Email">
                    <input class="t-reg-txt" placeholder="Password">
                    <input class="t-reg-txt" placeholder="Confirm Password">
                    <div class="t-btn-reg">Register</div>
                </div>
            </div>
        </div>
        <div class="footer-main-wrapper">
            <div class="t-container text-center footer-text">
                Copyright © Social Survey <span class="center-dot">.</span> All Rights Reserved.
            </div>
        </div>
    </div>
    
    
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
    <script>
        $(document).ready(function(){
            
        });
    </script>
    
</body>
</html>