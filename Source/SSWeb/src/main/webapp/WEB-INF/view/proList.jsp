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
    
    <div class="t-main-container">
        <div class="t-header-main">
            <div class="t-heade-wrapper t-container clearfix">
                <div class="t-logo float-left"></div>
            </div>
        </div>
        <div class="t-pro-wrapper">
            <div class="t-container clearfix">
                <div class="float-left t-pro-search-txt">Search Results for : <span class="t-pro-search-name">John Doe</span> </div>
            </div>
        </div>
        <div class="t-main-wrapper t-container clearfix">
            <div class="t-pro-list-wrapper">
                <div class="t-pro-item clearfix">
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
                <div class="t-pro-item clearfix">
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
                <div class="t-pro-item clearfix">
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