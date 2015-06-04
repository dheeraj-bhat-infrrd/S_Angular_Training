<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.makepayment.title.key" /></title>			
    <link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
    <script type="text/javascript" src="${initParam.resourcesPath}/resources/js/common.js"></script>
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
    
<body>   	
	
	<div class="ol-header"><spring:message code="label.paymentmade.header" /></div>
        <div class="ol-content">
            <div class="ol-txt"><spring:message code="label.paymentmade.content" /></div>
            <div class="clearfix">
                <div class="float-left ol-btn-wrapper">
                    <div id="ol-btn-redirect" class="ol-btn"><spring:message code="label.paymentmade.redirect" /></div>
                </div>
                <div class="float-left ol-btn-wrapper">
                    <div id="ol-btn-cancel" class="ol-btn"><spring:message code="label.cancel.key" /></div>
                </div>
            </div>
        </div>
	   
    <script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/script.js"></script>
    <script>
    
    $('#ol-btn-redirect').click(function(e){
    	location.href = "./landing.do"
    });
    
    $("#ol-btn-cancel").click(function() {
 	   hidePayment();
    })
    
    </script>
   
</body>
</html>