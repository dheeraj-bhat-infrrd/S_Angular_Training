<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.registeruser.key"/></title>
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
                <div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key" /></div>
                <div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key" /></div>
            </div>
        </div>
    </div>
    <div class="hm-header-main-wrapper">
        <div class="container">
            <div class="hm-header-row hm-header-row-main clearfix">
                <div class="float-left hm-header-row-left text-center">Account Settings</div>
            </div>
        </div>
    </div>

    <div class="container sett-container">
        
        <div class="sett-tier1 sett-tier-item">
            <div class="panel-header clearfix">
                <div class="float-left panel-dd panel-header-wrapper">
                    <div class="ph-txt">Encompass Configuration</div>
                    <div class="panel-dd-wrapper hide">
                        <div class="panel-dd-item">One</div>
                        <div class="panel-dd-item">Two</div>
                        <div class="panel-dd-item">Three</div>
                    </div>
                </div>
            </div>
            
            <div class="sett-content row clearfix">
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
                    <div class="hm-item-row clearfix">
                        <div class="um-item-row-left text-right">
                            First Name
                        </div>
                        <div class="um-item-row-icon"></div>
                        <div class="um-item-row-icon um-item-row-icon-adj1"></div>
                        <div class="hm-item-row-right um-item-row-right">
                            <input type="text" id="um-fname" name="firstName" value="" class="um-item-row-txt mob-icn-fname" placeholder="First Name">
                            <div id="um-fname-error" class="input-error-2 error-msg"></div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
                    <div class="hm-item-row clearfix">
                        <div class="um-item-row-left text-right">
                            Last Name
                        </div>
                        <div class="um-item-row-icon"></div>
                        <div class="um-item-row-icon um-item-row-icon-adj1"></div>
                        <div class="hm-item-row-right um-item-row-right">
                            <input type="text" id="um-lname" name="lastName" value="" class="um-item-row-txt mob-icn-lname" placeholder="Last Name">
                            <div id="um-lname-error" class="input-error-2 error-msg"></div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
                    <div class="hm-item-row clearfix" id="um-emailid-con">
                        <div class="um-item-row-left text-right">
                            Email ID
                        </div>
                        <div class="um-item-row-icon icn-save cursor-pointer"></div>
                        <div class="um-item-row-icon icn-save cursor-pointer"></div>
                        <div class="hm-item-row-right um-item-row-right">
                            <input type="text" id="um-emailid" name="emailId" value="" class="um-item-row-txt mob-icn-email" placeholder="Email ID">
                            <div id="um-emailid-error" class="input-error-2 error-msg"></div>
                        </div>
                    </div>
                </div>
            </div>
            
        </div>
        
    </div>


<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
    $(document).ready(function() {
        
        $(document).on('click','.panel-dd',function(){
            $(this).find('.panel-dd-wrapper').slideToggle();
        });
        
    });
</script>

</body>
</html>