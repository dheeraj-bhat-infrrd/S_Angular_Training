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
                <div class="float-left hm-header-row-left text-center">Build Your Company Hierarchy</div>
            </div>
        </div>
    </div>

    <div class="container bd-hr-container">
        <div class="bd-hr-left-panel col-lg-3 col-md-3 col-sm-3">
            <div class="bd-hr-lp-header">Our Company</div>
            <div class="bd-hr-lp-content-wrapper">
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                        <div class="bd-hr-item-l3">
                            <div class="bd-hr-item bd-lt-l3 clearfix">
                                <div class="float-left bd-hr-img"></div>
                                <div class="bd-hr-txt">Sample test</div>
                            </div>
                        </div>
                        <div class="bd-hr-item-l3">
                            <div class="bd-hr-item bd-lt-l3 clearfix">
                                <div class="float-left bd-hr-img"></div>
                                <div class="bd-hr-txt">Sample test</div>
                            </div>
                        </div>
                        <div class="bd-hr-item-l3">
                            <div class="bd-hr-item bd-lt-l3 clearfix">
                                <div class="float-left bd-hr-img"></div>
                                <div class="bd-hr-txt">Sample test</div>
                            </div>
                        </div>
                    </div>
                    <div class="bd-hr-item-l2">
                        <div class="bd-hr-item bd-lt-l2 clearfix">
                            <div class="bd-hr-txt">Sample test</div>
                        </div>
                    </div>
                </div>
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                </div>
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                </div>
                <div class="bd-hr-item-l1">
                    <div class="bd-hr-item bd-lt-l1 clearfix">
                        <div class="bd-hr-txt">Sample test</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="bd-hr-rt-panel col-lg-9 col-md-9 col-sm-9 col-xs-12">
            <div class="bd-hr-rt-header"><i>Would you like to add <strong>New</strong></i></div>
            <div class="bd-hr-tabs-wrapper">
                <div class="bd-hr-tabs-header clearfix">
                    <div class="bd-hr-tabs-header-item float-left">
                        <span data-tab="region" class="bd-hdr-span bd-hdr-active-arr bd-hdr-active">Region</span>
                    </div>
                    <div class="bd-hr-tabs-header-item float-left">
                        <span data-tab="office" class="bd-hdr-span">Office</span>
                    </div>
                    <div class="bd-hr-tabs-header-item float-left">
                        <span data-tab="individual" class="bd-hdr-span">Individual</span>
                    </div>
                    <div class="bd-hr-tabs-header-item hdr-txt-rt-adj float-left">
                        <span data-tab="csv" class="bd-hdr-span">Upload CSV</span>
                    </div>
                </div>
            </div>
            <div id="bd-form-region" class="bd-hr-form-wrapper">
                <div class="bd-hr-form-item clearfix">
                    <div class="float-left bd-frm-left">Region Name</div>
                    <div class="float-left bd-frm-right">
                        <input class="bd-frm-rt-txt">
                    </div>
                </div>
                <div class="bd-hr-form-item clearfix">
                    <div class="float-left bd-frm-left">Address Line1</div>
                    <div class="float-left bd-frm-right">
                        <input class="bd-frm-rt-txt">
                    </div>
                </div>
                <div class="bd-hr-form-item clearfix">
                    <div class="float-left bd-frm-left">Address Line2</div>
                    <div class="float-left bd-frm-right">
                        <input class="bd-frm-rt-txt">
                    </div>
                </div>
                <div class="bd-hr-form-item clearfix">
                    <div class="float-left bd-frm-left"></div>
                    <div class="float-left bd-frm-right">
                        <div class="bd-frm-rad-wrapper clearfix">
                            <div class="float-left bd-cust-rad-item clearfix">
                                <div data-type="single" class="float-left bd-cust-rad-img bd-cust-rad-img-checked"></div>
                                <div class="float-left bd-cust-rad-txt">Add Single User</div>
                            </div>
                            <div class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix">
                                <div data-type="multiple" class="float-left bd-cust-rad-img"></div>
                                <div class="float-left bd-cust-rad-txt">Add Multiple User</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="bd-multiple" class="bd-hr-form-item clearfix hide">
                    <div class="float-left bd-frm-left">Add Multiple Users</div>
                    <div class="float-left bd-frm-right">
                        <textarea class="bd-frm-rt-txt-area"></textarea>
                    </div>
                </div>
                <div id="bd-single" class="bd-hr-form-item clearfix">
                    <div class="float-left bd-frm-left">Choose User For This Region</div>
                    <div class="float-left bd-frm-right pos-relative">
                        <input class="bd-frm-rt-txt bd-frm-rt-dd bd-frm-rt-dd-adj dd-com-main bd-dd-img">
                        <div class="bd-frm-rt-dd-list dd-com-list hide">
                            <div data-option="test1" class="bd-frm-rt-dd-item dd-com-item">test1</div>
                            <div data-option="test2" class="bd-frm-rt-dd-item dd-com-item">test2</div>
                            <div data-option="test3" class="bd-frm-rt-dd-item dd-com-item">test3</div>
                            <div data-option="test4" class="bd-frm-rt-dd-item dd-com-item">test4</div>
                        </div>
                    </div>
                </div>
                <div class="bd-hr-form-item clearfix">
                    <div class="float-left bd-frm-left"></div>
                    <div class="float-left bd-frm-right">
                        <div class="bd-frm-check-wrapper clearfix">
                            <div class="float-left bd-check-img"></div>
                            <div class="float-left bd-check-txt">Grant Administrative Privileges</div>
                        </div>
                    </div>
                </div>
                <div class="bd-hr-form-item clearfix">
                    <div class="float-left bd-frm-left"></div>
                    <div class="float-left bd-frm-right">
                        <div class="bd-btn-save cursor-pointer">SAVE</div>
                    </div>
                </div>
            </div>
            <div id="bd-form-office" class="bd-hr-form-wrapper hide">office</div>
            <div id="bd-form-individual" class="bd-hr-form-wrapper hide">individual</div>
            <div id="bd-form-csv" class="bd-hr-form-wrapper hide">csv</div>
        </div>
    </div>


<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
    $(document).ready(function() {
        
        $(document).on('click', '.dd-com-main', function(e) {
            e.stopPropagation();
            $(this).next('.dd-com-list').slideToggle(200);
        });
        
        $(document).on('click', 'body', function() {
            $('.dd-com-list').slideUp(200);
        });
        
        $(document).on('click', '.dd-com-item', function(e) {
            $(this).parent().prev('.dd-com-main').html($(this).data('option'));
        });
        
        $(document).on('click', '.bd-check-img', function(e) {
            $(this).toggleClass('bd-check-img-checked');
        });
        
        $(document).on('click', '.bd-cust-rad-img', function(e) {
            $('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
            $(this).toggleClass('bd-cust-rad-img-checked');
            if($(this).data('type') == "single"){
                $('#bd-single').show();
                $('#bd-multiple').hide();
            }else if($(this).data('type') == "multiple"){
                $('#bd-single').hide();
                $('#bd-multiple').show();
            }
        });
        
        $(document).on('click','.bd-hdr-span',function(){
            $('.bd-hdr-span').removeClass('bd-hdr-active');
            $('.bd-hdr-span').removeClass('bd-hdr-active-arr');
            $(this).addClass('bd-hdr-active');
            $(this).addClass('bd-hdr-active-arr');
            $('.bd-hr-form-wrapper').hide();
            switch($(this).data('tab')){
                case 'region': 
                    $('#bd-form-region').show();
                    break;
                case 'office': 
                    $('#bd-form-office').show();
                    break;
                case 'individual': 
                    $('#bd-form-individual').show();
                    break;
                case 'csv': 
                    $('#bd-form-csv').show();
                    break;
                default: 
                    $('#bd-form-region').show();
                    break;
            }
        });

    });
</script>

</body>
</html>