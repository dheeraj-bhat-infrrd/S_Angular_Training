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
    
    <div id="bd-srv-pu" class="bd-srv-pu">
        <div class="container bd-q-container">
            <div class="bd-q-wrapper">
                <div class="bd-q-pu-header clearfix">
                    <div class="float-left bd-q-pu-header-lft">Create Your Survey Questions Here</div>
                    <div class="float-right bd-q-pu-header-rt cursor-pointer">Need Help?</div>
                </div>
                <div class="bd-q-pu-txt-wrapper pos-relative">
                    <input class="bd-q-pu-txt">
                    <div class="bd-q-pu-close"></div>
                </div>
                
                <div class="bs-ans-wrapper">
                    <div class="bd-and-header-txt">I want my replying using</div>
                    <div class="bd-ans-options-wrapper">
                        <div class="bd-ans-header clearfix">
                            <div class="bd-ans-hd-container clearfix float-left">
                                <div class="float-left bd-ans-tab-item bd-ans-tab-sel">Rating</div>
                                <div class="float-left bd-ans-tab-item">Comment</div>
                                <div class="float-left bd-ans-tab-item">Mutiple Choice</div>
                            </div>
                        </div>
                        <div class="bd-and-tier2">My Customers can answer using</div>
                        <div class="row clearfix bd-ans-type">
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                                <div class="bd-ans-img"></div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                                <div class="bd-ans-img"></div>
                            </div>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                                <div class="bd-ans-img"></div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="bd-q-pu-header clearfix">
                    <div class="float-left bd-q-pu-header-lft">I Would Like To Add Another Question</div>
                </div>
                <div class="bd-q-pu-txt-wrapper pos-relative">
                    <input class="bd-q-pu-txt">
                    <div class="bd-q-pu-close"></div>
                </div>
                
                <div class="bd-q-pu-done-wrapper clearfix">
                    <div class="bd-q-btn-cancel float-left">Cancel</div>
                    <div class="bd-q-btn-done float-left">Done</div>
                </div>
            </div>
        </div>
    </div>
    
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
                <div class="float-left hm-header-row-left text-center">Setup Your Survey</div>
            </div>
        </div>
    </div>

    <div class="container bd-svry-container">
        
        <div class="bd-svry-header clearfix">
            <div class="float-left bd-svry-left">Survey Questions</div>
            <div id="btn-add-question" class="float-right bd-svry-right">Add New Question</div>
        </div>
        
        <div class="bd-srv-q-tbl">
            <div class="bd-srv-tbl-header">Build Your Customer Feedback</div>
            <div class="bd-srv-rows">
                <div class="bd-srv-tbl-row clearfix">
                    <div class="float-left srv-tbl-num">
                        <span>1</span>
                    </div>
                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="float-right srv-tbl-rem">Remove</div>
                    <div class="float-right srv-tbl-edit">Edit</div>
                </div>
                <div class="bd-srv-tbl-row clearfix">
                    <div class="float-left srv-tbl-num">
                        <span>1</span>
                    </div>
                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="float-right srv-tbl-rem">Remove</div>
                    <div class="float-right srv-tbl-edit">Edit</div>
                </div>
                <div class="bd-srv-tbl-row clearfix">
                    <div class="float-left srv-tbl-num">
                        <span>1</span>
                    </div>
                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="float-right srv-tbl-rem">Remove</div>
                    <div class="float-right srv-tbl-edit">Edit</div>
                </div>
                <div class="bd-srv-tbl-row clearfix">
                    <div class="float-left srv-tbl-num">
                        <span>1</span>
                    </div>
                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="float-right srv-tbl-rem">Remove</div>
                    <div class="float-right srv-tbl-edit">Edit</div>
                </div>
                <div class="bd-srv-tbl-row clearfix">
                    <div class="float-left srv-tbl-num">
                        <span>1</span>
                    </div>
                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="float-right srv-tbl-rem">Remove</div>
                    <div class="float-right srv-tbl-edit">Edit</div>
                </div>
                <div class="bd-srv-tbl-row clearfix">
                    <div class="float-left srv-tbl-num">
                        <span>1</span>
                    </div>
                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="float-right srv-tbl-rem">Remove</div>
                    <div class="float-right srv-tbl-edit">Edit</div>
                </div>
            </div>
        </div>
        
    </div>


<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
    $(document).ready(function() {
        
        $(document).on('mouseover','.bd-srv-tbl-row',function(){
            $(this).addClass('bd-srv-tbl-row-hover');
            $(this).find('.srv-tbl-rem').show();
            $(this).find('.srv-tbl-edit').show();
        });
        
        $(document).on('mouseout','.bd-srv-tbl-row',function(){
            $(this).removeClass('bd-srv-tbl-row-hover');
            $(this).find('.srv-tbl-rem').hide();
            $(this).find('.srv-tbl-edit').hide();
        });
        
        resizeAdj();
        
        $(window).resize(resizeAdj);
        
        function resizeAdj(){
            var winW = $(window).width();
            if(winW < 768){
                var txtW = winW - 118;
                $('.srv-tbl-txt').width(txtW);
            }else{
            }
        }
        
        $('#btn-add-question').click(function(){
            
        });        
    });
</script>

</body>
</html>