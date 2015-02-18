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
        <div class="float-left hdr-links clearfix">
            <div class="hdr-link-item hdr-link-active">Dashboard</div>
            <div class="hdr-link-item">Build Hierarchy</div>
            <div class="hdr-link-item">Build Survey</div>
            <div class="hdr-link-item">User Management</div>
        </div>
        <div class="float-right clearfix hdr-btns-wrapper">
            <div class="float-left hdr-log-btn hdr-log-reg-btn">Sign In</div>
            <div class="float-left hdr-reg-btn hdr-log-reg-btn">Join Us</div>
        </div>
    </div>
</div>

    
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row hm-header-row-main clearfix">
            <div class="float-left hm-header-row-left">Read, Write and Share Reviews</div>
            <div class="float-right hm-hr-row-right clearfix">
                <div class="float-left social-item-icon icn-fb"></div>
                <div class="float-left social-item-icon icn-twit"></div>
                <div class="float-left social-item-icon icn-lin"></div>
                <div class="float-left social-item-icon icn-yelp"></div>
            </div>
        </div>
    </div>
</div>


<div id="" class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
        <div class="row prof-pic-name-wrapper">
            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper">
                <div class="prog-img-container">
                    <div id="prof-image" class="prof-image pos-relative"></div>
                    <div class="prof-rating-mobile-wrapper hide">
                        <div class="st-rating-wrapper maring-0 clearfix">
                            <div class="rating-star icn-full-star"></div>
                            <div class="rating-star icn-full-star"></div>
                            <div class="rating-star icn-half-star"></div>
                            <div class="rating-star icn-no-star"></div>
                            <div class="rating-star icn-no-star"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper">
                <div class="prof-name-container">
                    <div class="prof-name">Anna Thomas</div>
                    <div class="prof-address">
                        <div class="prof-addline1">Real Estate</div>
                        <div class="prof-addline2">Chairman's Jacob Mason</div>
                    </div>
                    <div class="prof-rating clearfix">
                        <div class="st-rating-wrapper maring-0 clearfix float-left">
                            <div class="rating-star icn-full-star"></div>
                            <div class="rating-star icn-full-star"></div>
                            <div class="rating-star icn-half-star"></div>
                            <div class="rating-star icn-no-star"></div>
                            <div class="rating-star icn-no-star"></div>
                        </div>
                        <div class="float-left review-count-left">250 Reviews</div>
                    </div>
                    <div class="prof-btn-wrapper">
                        <div class="prof-btn-survey">Read Write and Share Reviews</div>
                    </div>
                </div>
            </div>
            <div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper">
                <div class="prof-user-logo"></div>
                <div class="prof-user-address">
                    <div class="prof-user-addline1">Providential Utah Real Estate</div>
                    <div class="prof-user-addline2">574 South State Street<br/>Orem, UT 94058</div>
                </div>
            </div>
        </div>
        
        <div class="row">
            <div class="prof-left-panel-wrapper margin-top-25 col-lg-4 col-md-4 col-sm-4 col-xs-12">
                
                <div class="prof-left-row prof-left-info bord-bot-dc">
                    <div class="left-contact-wrapper">
                        <div class="left-panel-header">Contact Information</div>
                        <div class="left-panel-content">
                            <div class="lp-con-row lp-row clearfix">
                                <div class="float-left lp-con-icn icn-mail"></div>
                                <div class="float-left lp-con-row-item">Contact Us</div>
                            </div>
                            <div class="lp-con-row lp-row clearfix">
                                <div class="float-left lp-con-icn icn-web"></div>
                                <div class="float-left lp-con-row-item blue-text">Our Website</div>
                            </div>
                            <div class="lp-con-row lp-row clearfix">
                                <div class="float-left lp-con-icn icn-blog"></div>
                                <div class="float-left lp-con-row-item blue-text">Our Blogs</div>
                            </div>
                            <div class="lp-con-row lp-row clearfix">
                                <div class="float-left lp-con-icn icn-mbl"></div>
                                <div class="float-left lp-con-row-item">+1 (801) 123456789</div>
                            </div>
                            <div class="lp-con-row lp-row clearfix">
                                <div class="float-left lp-con-icn icn-phone"></div>
                                <div class="float-left lp-con-row-item">+1 (801) 123456789</div>
                            </div>
                            <div class="lp-con-row lp-row clearfix">
                                <div class="float-left lp-con-icn icn-fax"></div>
                                <div class="float-left lp-con-row-item">+1 (801) 123456789</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="prof-left-row prof-left-assoc bord-bot-dc">
                    <div class="left-assoc-wrapper">
                        <div class="left-panel-header lph-dd lph-dd-closed lph-dd-open">Association</div>
                        <div class="left-panel-content lph-dd-content">
                            <div class="lp-assoc-row lp-row clearfix">National Association of Realtors</div>
                            <div class="lp-assoc-row lp-row clearfix">Independant Association of Realtors</div>
                            <div class="lp-assoc-row lp-row clearfix">National Association of Realtors</div>
                        </div>
                    </div>
                </div>
                <div class="prof-left-row prof-left-expertise bord-bot-dc">
                    <div class="left-expertise-wrapper">
                        <div class="left-panel-header lph-dd lph-dd-closed">Expertise</div>
                        <div class="left-panel-content lph-dd-content">
                            <div class="lp-exp-row lp-row clearfix">Buyer's Agent</div>
                            <div class="lp-exp-row lp-row clearfix">Listing Agent</div>
                            <div class="lp-exp-row lp-row clearfix">Foreclosure Agent</div>
                        </div>
                    </div>
                </div>
                <div class="prof-left-row prof-left-ach bord-bot-dc">
                    <div class="left-ach-wrapper">
                        <div class="left-panel-header lph-dd lph-dd-closed">Achievements</div>
                        <div class="left-panel-content lph-dd-content">
                            <div class="lp-ach-row lp-row clearfix">Market Leader</div>
                            <div class="lp-ach-row lp-row clearfix">Top Seller</div>
                        </div>
                    </div>
                </div>
                <div class="prof-left-row prof-left-hob bord-bot-dc">
                    <div class="left-hob-wrapper">
                        <div class="left-panel-header lph-dd lph-dd-closed">Hobbies</div>
                        <div class="left-panel-content lph-dd-content">
                            <div class="lp-hob-row lp-row clearfix">Bowling</div>
                            <div class="lp-hob-row lp-row clearfix">Designing</div>
                        </div>
                    </div>
                </div>
                <div class="prof-left-row prof-left-auth bord-bot-dc">
                    <div class="left-auth-wrapper">
                        <div class="left-panel-header lph-dd lph-dd-closed">Authorised In</div>
                        <div class="left-panel-content lph-dd-content">
                            <div class="lp-auth-row lp-row clearfix">California (<span class="blue-text">CA#1234556789</span>)</div>
                            <div class="lp-auth-row lp-row clearfix">N. Carolina (<span class="blue-text">CA#1234556789</span>)</div>
                            <div class="lp-auth-row lp-row clearfix">N. Jersey (<span class="blue-text">CA#1234556789</span>)</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row prof-right-panel-wrapper margin-top-25 col-lg-8 col-md-8 col-sm-8 col-xs-12">
                <div class="intro-wrapper rt-content-main bord-bot-dc">
                    <div class="main-con-header">About Anna Thomas</div>
                    <div class="intro-body">Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. </div>
                </div>
                <div class="rt-content-main bord-bot-dc clearfix">
                    <div class="float-left panel-tweet-wrapper">
                        <div class="main-con-header">Recent Tweets</div>
                        <div class="tweet-panel tweet-panel-left">
                            <div class="tweet-panel-item bord-bot-dc clearfix">
                                <div class="tweet-icn icn-tweet float-left"></div>
                                <div class="tweet-txt float-left">
                                    <div class="tweet-text-main">Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit </div>
                                    <div class="tweet-text-link"><em>http://abblk.com</em></div>
                                    <div class="tweet-text-time"><em>24 minutes ago</em></div>
                                </div>
                            </div>
                            <div class="tweet-panel-item bord-bot-dc clearfix">
                                <div class="tweet-icn icn-tweet float-left"></div>
                                <div class="tweet-txt float-left">
                                    <div class="tweet-text-main">Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit </div>
                                    <div class="tweet-text-link"><em>http://abblk.com</em></div>
                                    <div class="tweet-text-time"><em>24 minutes ago</em></div>
                                </div>
                            </div>
                            <div class="tweet-panel-item bord-bot-dc clearfix">
                                <div class="tweet-icn icn-tweet float-left"></div>
                                <div class="tweet-txt float-left">
                                    <div class="tweet-text-main">Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit </div>
                                    <div class="tweet-text-link"><em>http://abblk.com</em></div>
                                    <div class="tweet-text-time"><em>24 minutes ago</em></div>
                                </div>
                            </div>
                            <div class="tweet-panel-item bord-bot-dc clearfix">
                                <div class="tweet-icn icn-tweet float-left"></div>
                                <div class="tweet-txt float-left">
                                    <div class="tweet-text-main">Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit </div>
                                    <div class="tweet-text-link"><em>http://abblk.com</em></div>
                                    <div class="tweet-text-time"><em>24 minutes ago</em></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="people-say-wrapper rt-content-main">
                    <div class="main-con-header"><span class="ppl-say-txt-st">What people say</span> about Anna Thomas</div>
                    <div class="ppl-review-item">
                        <div class="ppl-header-wrapper clearfix">
                            <div class="float-left ppl-header-left">
                                <div class="ppl-head-1">Matt & Gina Conelly - Lehi, UT</div>
                                <div class="ppl-head-2">12<sup>th</sup> Sept 2014</div>
                            </div>
                            <div class="float-right ppl-header-right">
                                <div class="st-rating-wrapper maring-0 clearfix">
                                    <div class="rating-star icn-full-star"></div>
                                    <div class="rating-star icn-full-star"></div>
                                    <div class="rating-star icn-half-star"></div>
                                    <div class="rating-star icn-no-star"></div>
                                    <div class="rating-star icn-no-star"></div>
                                </div>
                            </div>
                        </div>
                        <div class="ppl-content">Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las.</div>
                        <div class="ppl-share-wrapper clearfix">
                            <div class="float-left blue-text ppl-share-shr-txt">Share</div>
                            <div class="float-left icn-share icn-plus-open"></div>
                            <div class="float-left clearfix ppl-share-social hide">
                                <div class="float-left ppl-share-icns icn-fb"></div>
                                <div class="float-left ppl-share-icns icn-twit"></div>
                                <div class="float-left ppl-share-icns icn-lin"></div>
                                <div class="float-left ppl-share-icns icn-yelp"></div>
                            </div>
                            <div class="float-left icn-share icn-remove icn-rem-size hide"></div>
                        </div>
                    </div>
                    <div class="profile-addl-links clearfix">
                        <span class="p-a-l-item">100 additional reviews not recommended</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="mobile-tabs hide clearfix">
    <div class="float-left mob-icn mob-icn-active icn-person"></div>
    <div class="float-left mob-icn icn-ppl"></div>
    <div class="float-left mob-icn icn-star-smile"></div>
    <div class="float-left mob-icn inc-more"></div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>

<script>
    $(document).ready(function(){
        adjustImage();
        $(window).resize(adjustImage);
        
        $('.icn-plus-open').click(function(){
            $(this).hide();
            $(this).parent().find('.ppl-share-social,.icn-remove').show();
        });
        
        $('.icn-remove').click(function(){
            $(this).hide();
            $(this).parent().find('.ppl-share-social').hide();
            $(this).parent().find('.icn-plus-open').show();
        });
        
        $('.icn-person').click(function(){
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
            $('.prof-left-panel-wrapper').show();
            $('.prof-right-panel-wrapper').hide();
            adjustImage();
        });
        
        $('.icn-ppl').click(function(){
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
            $('.prof-left-panel-wrapper').hide();
            $('.prof-right-panel-wrapper').show();
        });
        
        $('.icn-star-smile').click(function(){
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
        });
        
        $('.inc-more').click(function(){
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
        });
        
        $('.lph-dd').click(function(){
            $(this).next('.lph-dd-content').slideToggle(200);
        });
        
        function adjustImage(){
            var windW = $(window).width();
            if(windW < 768){
                var imgW = $('#prof-image').width();
                $('#prof-image').height(imgW * 0.7);
                var h2 = $('.prog-img-container').height() - 11;
//                $('.prof-name-container').height(h2);
                var rowW = $('.lp-con-row').width() - 55 - 10;
                $('.lp-con-row-item').width(rowW+'px');
                $('.footer-main-wrapper').hide();
            }else{
                $('.prof-name-container,#prof-image').height(200);
                $('.lp-con-row-item').width('auto');
                $('.footer-main-wrapper').show();
            }
        }
    });
</script>

</body>
</html>