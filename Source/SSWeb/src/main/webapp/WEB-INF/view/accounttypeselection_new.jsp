<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.prolist.title.key"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>
<body>

<div class="hdr-wrapper">
    <div class="container hdr-container clearfix">
        <div class="float-left hdr-logo"></div>
        <div class="float-right clearfix hdr-btns-wrapper">
            <div class="float-left hdr-log-btn hdr-log-reg-btn">Sign In</div>
            <div class="float-left hdr-reg-btn hdr-log-reg-btn">Join Us</div>
        </div>
    </div>
</div>

    
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row hm-header-row-main clearfix">
            <div class="float-left hm-header-row-left text-center">Connect With Your LinkedIn Account</div>
        </div>
    </div>
</div>


<div id="" class="acc-type-main-wrapper margin-top-25 margin-bottom-25">
    
    <div class="acc-type-container container">
        <div class="acc-type-item text-center">
            <div class="act-header">Individual</div>
            <div class="act-price">$35<sup>99</sup><span>Per Month</span></div>
            <div class="act-txt-1">1 User Account</div>
            <div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
            <div class="act-txt-3">SUBSCRIBE</div>
        </div>
        <div class="acc-type-item text-center">
            <div class="act-header">Individual</div>
            <div class="act-price">$35<sup>99</sup><span>Per Month</span></div>
            <div class="act-txt-1">1 User Account</div>
            <div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
            <div class="act-txt-3">SUBSCRIBE</div>
        </div>
        <div class="acc-type-item text-center">
            <div class="act-header">Individual</div>
            <div class="act-price">$35<sup>99</sup><span>Per Month</span></div>
            <div class="act-txt-1">1 User Account</div>
            <div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
            <div class="act-txt-3">SUBSCRIBE</div>
        </div>
        <div class="acc-type-item text-center">
            <div class="act-header">Individual</div>
            <div class="act-price">$35<sup>99</sup><span>Per Month</span></div>
            <div class="act-txt-1">1 User Account</div>
            <div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
            <div class="act-txt-3">SUBSCRIBE</div>
        </div>
        <div class="acc-type-item text-center">
            <div class="act-header">Individual</div>
            <div class="act-price">$35<sup>99</sup><span>Per Month</span></div>
            <div class="act-txt-1">1 User Account</div>
            <div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
            <div class="act-txt-3">SUBSCRIBE</div>
        </div>
    </div>
    
</div>


<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>

<script>
    $(document).ready(function(){
        
        $('.acc-type-item').hover(
            function(){
                $(this).addClass('act-type-hover');
            },
            function(){
                $(this).removeClass('act-type-hover');
            }
        );
        
    });
</script>

</body>
</html>