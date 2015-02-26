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
            <div class="float-left hm-header-row-left">Sign Up To Start Your Survey</div>
        </div>
    </div>
</div>


<div id="" class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container reg_panel_container">
        
        <div class="reg_header">Get Started - It's Free</div>
        
        <div class="reg_form_wrapper_2">
            <div class="reg_form_row clearfix">
                <div class="float-left rfr_lbl">Name</div>
                <div class="float-left rfr_txt">
                    <div class="rfr_icn icn-fname"></div>
                    <input class="rfr_txt_fld" placeholder="First Name">
                </div>
                <div class="float-left rfr_txt">
                    <div class="rfr_icn icn-lname"></div>
                    <input class="rfr_txt_fld" placeholder="Last Name">
                </div>
            </div>
            <div class="reg_form_row clearfix">
                <div class="float-left rfr_lbl">Phone No</div>
                <div class="float-left rfr_txt">
                    <div class="rfr_icn icn-mbl"></div>
                    <input class="rfr_txt_fld" placeholder="Phone No">
                </div>
            </div>
            <div class="reg_form_row clearfix">
                <div class="float-left rfr_lbl">Email ID</div>
                <div class="float-left rfr_txt">
                    <div class="rfr_icn icn-mail"></div>
                    <input class="rfr_txt_fld" placeholder="Email ID">
                </div>
            </div>
            <div class="reg_form_row clearfix">
                <div class="float-left rfr_lbl">Vertical</div>
                <div class="float-left rfr_txt">
                    <div class="rfr_icn"></div>
                    <input class="rfr_txt_fld" placeholder="Vertical">
                </div>
            </div>
            <div class="reg_form_row clearfix">
                <div class="float-left rfr_lbl">Password</div>
                <div class="float-left rfr_txt">
                    <div class="rfr_icn icn-password"></div>
                    <input class="rfr_txt_fld" placeholder="Password">
                </div>
                <div class="float-left rfr_txt">
                    <div class="rfr_icn icn-confirm-password"></div>
                    <input class="rfr_txt_fld" placeholder="Confirm Password">
                </div>
            </div>
            <div class="reg_form_row clearfix">
                <div class="reg_btn">Submit</div>
            </div>
        </div>
        
        
    </div>
</div>


<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>

<script>
    $(document).ready(function(){
        
    });
</script>

</body>
</html>