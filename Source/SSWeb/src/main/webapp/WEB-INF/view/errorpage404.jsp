<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
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

	<div class="err-wrapper-main">
        <div class="header-main-wrapper err-header-wrapper">
            <div class="container clearfix">
                <div class="header-logo float-left"></div>
                <div class="header-user-info float-right clearfix">
                    <c:if test="${displaylogo != null}">
                        <div class="float-left user-info-seperator"></div>
                        <div class="float-left user-info-logo" style="background: url(${pageContext.request.contextPath}/resources/logos/${displaylogo}) no-repeat center; background-size: 100% auto;"></div>
                    </c:if>
                </div>			
            </div>
        </div>
        <div class="hm-header-main-wrapper">
            <div class="container">
                <div class="hm-header-row hm-header-row-main clearfix">
                    <div class="text-center err-header-row">404 - Page Not Found</div>
                </div>
            </div>
        </div>

        <div class="error-main-wrapper container">
            <div class="err-line-1 text-center">OOPS !!! The page you are looking for cannot be found</div>
            <div class="err-line-2 text-center">404</div>
            <div class="err-line-3 text-center"><em>Lorem ipsumm dore eit lewht njbskjlhjn jb hhtreke Lorem ipsumm dore eit lewht njbskjlhjn jb hhtreke</em></div>
        </div>

        <div class="footer-main-wrapper">
            <div class="container text-center footer-text">
                <spring:message code="label.copyright.key"/> &copy; <spring:message code="label.footer.socialsurvey.key"/> <span class="center-dot">.</span> <spring:message code="label.allrightscopyright.key"/>
            </div>
        </div>
    </div>
    <jsp:include page="scripts.jsp"/>
    
    <script>
        
        $(document).ready(function(){
            adjustMinHeight();
            $(window).resize(adjustMinHeight);
            function adjustMinHeight(){
                var winH = $(window).height();
                if($(window).width() < 768){
                    var minH = winH - 50 - 50 - 5 - 1;
                }else{
                    var minH = winH - 83 - 78 - 78 + 4;
                }
                $('.error-main-wrapper').css('min-height',minH+'px');
            }
            
        });
        
    </script>
    
</body>
</html>