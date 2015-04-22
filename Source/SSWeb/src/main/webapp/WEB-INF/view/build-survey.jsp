<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.title.registeruser.key"/></title>
	<link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>

<body>
    
    <div id="view-srv-pu" class="bd-srv-pu hide">
        <div class="container view-srv-container">
            <div class="bd-q-wrapper">
                <div class="bd-q-pu-header bd-q-pu-header-adj clearfix">
                    <div class="float-left bd-q-pu-header-lft">View Survey</div>
                </div>
                <div class="vs-q-wrapper">
                    <div class="vs-srv-tbl-row clearfix">
                        <div class="float-left vs-tbl-num">
                            <span>1</span>
                        </div>
                        <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                        <div class="clearfix vs-ans-row">
                            <div class="float-left vs-ans-lbl">Ans:</div>
                            <div class="float-left vs-ans-txt">Some random asn asweef sj adhf fkdshjhj hjdkjyu </div>
                        </div>
                    </div>
                    <div class="vs-srv-tbl-row clearfix">
                        <div class="float-left vs-tbl-num">
                            <span>2</span>
                        </div>
                        <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                        <div class="clearfix vs-ans-row">
                            <div class="float-left vs-ans-lbl">Ans:</div>
                            <div class="float-left vs-ans-txt vs-ans-icn vs-ans-icn-smiley"></div>
                        </div>
                    </div>
                    <div class="vs-srv-tbl-row clearfix">
                        <div class="float-left vs-tbl-num">
                            <span>3</span>
                        </div>
                        <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                        <div class="clearfix vs-ans-row">
                            <div class="float-left vs-ans-lbl">Ans:</div>
                            <div class="float-left vs-ans-txt vs-ans-icn vs-ans-icn-star"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div id="bd-srv-pu" class="bd-srv-pu hide">
        <div class="container bd-q-container">
            <div class="bd-q-wrapper">
                
                <div class="bd-quest-item">
                    <div class="bd-q-pu-header bd-q-pu-header-adj clearfix">
                        <div class="float-left bd-q-pu-header-lft">Create Your Survey Questions Here</div>
                        <div class="float-right bd-q-pu-header-rt cursor-pointer">Need Help?</div>
                    </div>
                    <div class="bd-q-pu-txt-wrapper pos-relative">
                        <input class="bd-q-pu-txt" data-nextquest="false" data-qno="1">
                        <div class="bd-q-pu-close hide"></div>
                    </div>
                    <div class="bs-ans-wrapper hide">
                        <div class="bd-and-header-txt">I want my customer replying using</div>
                        <div class="bd-ans-options-wrapper">
                            <div class="bd-ans-header clearfix">
                                <div class="bd-ans-hd-container clearfix float-left">
                                    <div id="" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel">Rating</div>
                                    <div id="" class="bd-tab-com float-left bd-ans-tab-item">Comment</div>
                                    <div id="" class="bd-tab-mcq float-left bd-ans-tab-item">Mutiple Choice</div>
                                </div>
                            </div>
                            <div id="" class="bd-ans-type-rating bd-ans-type-item">
                                <div class="bd-and-tier2">My Customers can answer using</div>
                                <div class="row clearfix bd-ans-type bd-ans-type-rating-adj">
                                    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                                        <div class="bd-ans-img-wrapper">
                                            <div class="bd-ans-img bd-ans-smiley"></div>
                                            <div class="bd-ans-img-txt">Smiley</div>
                                        </div>
                                    </div>
                                    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                                        <div class="bd-ans-img-wrapper">
                                            <div class="bd-ans-img bd-ans-star"></div>
                                            <div class="bd-ans-img-txt">Stars</div>
                                        </div>
                                    </div>
                                    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                                        <div class="bd-ans-img-wrapper">
                                            <div class="bd-ans-img bd-ans-scale"></div>
                                            <div class="bd-ans-img-txt">Scale</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div id="" class="bd-ans-type-mcq bd-ans-type-item hide">
                                <div class="bd-and-tier2">My Customers can answer from</div>
                                <div class="clearfix bd-ans-type bd-ans-type-mcq-adj">
                                    <div class="bd-mcq-row clearfix">
                                        <div class="float-left bd-mcq-lbl">Option</div>
                                        <input class="float-left bd-mcq-txt">
                                        <div class="float-left bd-mcq-close"></div>
                                    </div>
                                    <div class="bd-mcq-row clearfix">
                                        <div class="float-left bd-mcq-lbl">Option</div>
                                        <input class="float-left bd-mcq-txt">
                                        <div class="float-left bd-mcq-close"></div>
                                    </div>
                                </div>
                            </div>
                            <div id="" class="bd-ans-type-com bd-ans-type-item hide">
                                <div class="clearfix bd-com-wrapper">
                                    <div class="float-left bd-com-chk"></div>
                                    <div class="float-left bd-com-txt">Textarea</div>
                                </div>
                            </div>
                        </div>
                        <div class="bd-q-status-wrapper text-center hide">
                            <span class="bd-spinner">`</span>
                            <span class="bd-q-status-txt">Saving</span>
                        </div>
                    </div>
                </div>
                
<!--
                <div class="bd-quest-item hide">
                    <div class="bd-q-pu-header clearfix">
                        <div class="float-left bd-q-pu-header-lft">I Would Like To Add Another Question</div>
                    </div>
                    <div class="bd-q-pu-txt-wrapper pos-relative">
                        <input class="bd-q-pu-txt" data-qno="2">
                        <div class="bd-q-pu-close hide"></div>
                    </div>
                    <div class="bs-ans-wrapper hide">
                    </div>
                </div>
-->
                
                <div class="bd-q-pu-done-wrapper clearfix">
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
<!--                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>-->
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="srv-tbl-btns clearfix float-right">
                        <div class="float-left srv-tbl-move-dn"></div>
                        <div class="float-left srv-tbl-move-up"></div>
                        <div class="float-left srv-tbl-edit">Edit</div>
                        <div class="float-left srv-tbl-rem">Remove</div>
                    </div>
                </div>
                <div class="bd-srv-tbl-row clearfix">
                    <div class="float-left srv-tbl-num">
                        <span>2</span>
                    </div>
<!--                    <div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>-->
                    <div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
                    <div class="srv-tbl-btns clearfix float-right">
                        <div class="float-left srv-tbl-move-dn"></div>
                        <div class="float-left srv-tbl-move-up"></div>
                        <div class="float-left srv-tbl-edit">Edit</div>
                        <div class="float-left srv-tbl-rem">Remove</div>
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
        hideOverlay();
        $(document).on('click','.bd-srv-tbl-row',function(){
            if($(window).width() < 768){
                if($(this).find('.srv-tbl-rem').css('display') == 'none'){
                    $(this).find('.srv-tbl-rem').show();
                    $(this).find('.srv-tbl-edit').show();
                    $(this).find('.srv-tbl-move-up').show();
                    $(this).find('.srv-tbl-move-dn').show();
                }else{
                    $(this).find('.srv-tbl-rem').hide();
                    $(this).find('.srv-tbl-edit').hide();
                    $(this).find('.srv-tbl-move-up').hide();
                    $(this).find('.srv-tbl-move-dn').hide();
                }
            }else{
//                $(this).find('.srv-tbl-rem').hide();
//                $(this).find('.srv-tbl-edit').hide();
            }
        });
        
        $(document).on('mouseover','.bd-srv-tbl-row',function(){
            if($(window).width() > 768){
                $(this).addClass('bd-srv-tbl-row-hover');
                $(this).find('.srv-tbl-rem').show();
                $(this).find('.srv-tbl-edit').show();
                $(this).find('.srv-tbl-move-up').show();
                $(this).find('.srv-tbl-move-dn').show();
            }
        });
        
        $(document).on('mouseout','.bd-srv-tbl-row',function(){
            if($(window).width() > 768){
                $(this).removeClass('bd-srv-tbl-row-hover');
                $(this).find('.srv-tbl-rem').hide();
                $(this).find('.srv-tbl-edit').hide();
                $(this).find('.srv-tbl-move-up').hide();
                $(this).find('.srv-tbl-move-dn').hide();
            }
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
            $('#bd-srv-pu').show();
            $('body').addClass('body-no-scroll-y');
        }); 
        
        $('.bd-q-btn-done').click(function(){
            $('#bd-srv-pu').hide();
            $('body').removeClass('body-no-scroll-y');
        }); 
        
        $(document).on('click','.bd-tab-rat',function(){
            $(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
            $(this).addClass('bd-ans-tab-sel');
            $(this).parent().parent().parent().find('.bd-ans-type-item').hide();
            $(this).parent().parent().parent().find('.bd-ans-type-rating').show();
        });
        
        $(document).on('click','.bd-tab-mcq',function(){
            $(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
            $(this).addClass('bd-ans-tab-sel');
            $(this).parent().parent().parent().find('.bd-ans-type-item').hide();
            $(this).parent().parent().parent().find('.bd-ans-type-mcq').show();
        });
        
        $(document).on('click','.bd-tab-com',function(){
            $(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
            $(this).addClass('bd-ans-tab-sel');
            $(this).parent().parent().parent().find('.bd-ans-type-item').hide();
            $(this).parent().parent().parent().find('.bd-ans-type-com').show();
        });
        
        $(document).on('click','.bd-com-chk',function(){
            if($(this).hasClass('bd-com-unchk')){
                $(this).removeClass('bd-com-unchk');
            }else{
                $(this).addClass('bd-com-unchk');
            }
        });
        
        $(document).on('click','.bd-ans-img-wrapper',function(){
            $(this).parent().parent().find('.bd-ans-img').addClass('bd-img-sel');
            $(this).find('.bd-ans-img').removeClass('bd-img-sel');
        });
        
        var newQuestTemplateWithTopTxt = '<div class="bd-quest-item hide"><div class="bd-q-pu-header clearfix"><div class="float-left bd-q-pu-header-lft">I Would Like To Add Another Question</div></div><div class="bd-q-pu-txt-wrapper pos-relative"><input class="bd-q-pu-txt" data-nextquest="false" data-qno="2"><div class="bd-q-pu-close hide"></div></div><div class="bs-ans-wrapper hide"><div class="bd-and-header-txt">I want my customer replying using</div><div class="bd-ans-options-wrapper"><div class="bd-ans-header clearfix"><div class="bd-ans-hd-container clearfix float-left"><div id="" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel">Rating</div><div id="" class="bd-tab-com float-left bd-ans-tab-item">Comment</div><div id="" class="bd-tab-mcq float-left bd-ans-tab-item">Mutiple Choice</div></div></div><div id="" class="bd-ans-type-rating bd-ans-type-item"><div class="bd-and-tier2">My Customers can answer using</div><div class="row clearfix bd-ans-type bd-ans-type-rating-adj"><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"><div class="bd-ans-img-wrapper"><div class="bd-ans-img bd-ans-smiley"></div><div class="bd-ans-img-txt">Smiley</div></div></div><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"><div class="bd-ans-img-wrapper"><div class="bd-ans-img bd-ans-star"></div><div class="bd-ans-img-txt">Stars</div></div></div><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"><div class="bd-ans-img-wrapper"><div class="bd-ans-img bd-ans-scale"></div><div class="bd-ans-img-txt">Scale</div></div></div></div></div><div id="" class="bd-ans-type-mcq bd-ans-type-item hide"><div class="bd-and-tier2">My Customers can answer from</div><div class="clearfix bd-ans-type bd-ans-type-mcq-adj"><div class="bd-mcq-row clearfix"><div class="float-left bd-mcq-lbl">Option</div><input class="float-left bd-mcq-txt"><div class="float-left bd-mcq-close"></div></div><div class="bd-mcq-row clearfix"><div class="float-left bd-mcq-lbl">Option</div><input class="float-left bd-mcq-txt"><div class="float-left bd-mcq-close"></div></div></div></div><div id="" class="bd-ans-type-com bd-ans-type-item hide"><div class="clearfix bd-com-wrapper"><div class="float-left bd-com-chk"></div><div class="float-left bd-com-txt">Textarea</div></div></div></div><div class="bd-q-status-wrapper text-center hide"><span class="bd-spinner">`</span><span class="bd-q-status-txt">Saving</span></div></div></div>';
        
        $(document).on("input", '.bd-q-pu-txt', function() {
            if($(this).val().trim().length > 0){
                $(this).parent().next('.bs-ans-wrapper').show();
                if($(this).data('nextquest') == false){
                    $(this).parent().parent().after(newQuestTemplateWithTopTxt);
                    $(this).parent().parent().next('.bd-quest-item').show();
                    $(this).data('nextquest','true');
                }
            }
            if($(this).data('qno') != '1'){
                $(this).next('.bd-q-pu-close').show();
            }
        });
        
        $(document).on('click','.bd-q-pu-close',function(){
            $(this).parent().parent().remove(); 
        });
        
        
        $(document).on('click','.srv-tbl-edit',function(){
            
            var editQuestion = '<div class="sb-edit-q-wrapper"><div class="bd-quest-item"> <div class="bd-q-pu-header bd-q-pu-header-adj clearfix"> <div class="float-left bd-q-pu-header-lft">Edit Your Question Here</div></div><div class="bd-q-pu-txt-wrapper pos-relative"> <input class="bd-q-pu-txt-edit" data-nextquest="false"></div><div class="bs-ans-wrapper hide" style="display: block;"> <div class="bd-and-header-txt">I want my customer replying using</div><div class="bd-ans-options-wrapper"> <div class="bd-ans-header clearfix"> <div class="bd-ans-hd-container clearfix float-left"> <div id="" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel">Rating</div><div id="" class="bd-tab-com float-left bd-ans-tab-item">Comment</div><div id="" class="bd-tab-mcq float-left bd-ans-tab-item">Mutiple Choice</div></div></div><div id="" class="bd-ans-type-rating bd-ans-type-item"> <div class="bd-and-tier2">My Customers can answer using</div><div class="row clearfix bd-ans-type bd-ans-type-rating-adj"> <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"> <div class="bd-ans-img-wrapper"> <div class="bd-ans-img bd-ans-smiley"></div><div class="bd-ans-img-txt">Smiley</div></div></div><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"> <div class="bd-ans-img-wrapper"> <div class="bd-ans-img bd-ans-star"></div><div class="bd-ans-img-txt">Stars</div></div></div><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"> <div class="bd-ans-img-wrapper"> <div class="bd-ans-img bd-ans-scale"></div><div class="bd-ans-img-txt">Scale</div></div></div></div></div><div id="" class="bd-ans-type-mcq bd-ans-type-item hide"> <div class="bd-and-tier2">My Customers can answer from</div><div class="clearfix bd-ans-type bd-ans-type-mcq-adj"> <div class="bd-mcq-row clearfix"> <div class="float-left bd-mcq-lbl">Option</div><input class="float-left bd-mcq-txt"> <div class="float-left bd-mcq-close"></div></div><div class="bd-mcq-row clearfix"> <div class="float-left bd-mcq-lbl">Option</div><input class="float-left bd-mcq-txt"> <div class="float-left bd-mcq-close"></div></div></div></div><div id="" class="bd-ans-type-com bd-ans-type-item hide"> <div class="clearfix bd-com-wrapper"> <div class="float-left bd-com-chk"></div><div class="float-left bd-com-txt">Textarea</div></div></div></div></div></div><div class="bd-q-pu-done-wrapper clearfix"><div class="bd-q-btn-done-edit float-left">Done</div></div></div>';
            
            
            $(this).parent().parent().after(editQuestion);
        });
        
        $(document).on('click','.bd-q-btn-done-edit',function(){
            $(this).parent().parent().remove(); 
        });
        
        
    });
</script>

</body>
</html>