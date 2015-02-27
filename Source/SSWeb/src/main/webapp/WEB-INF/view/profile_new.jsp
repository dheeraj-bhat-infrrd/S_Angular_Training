<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.profile.title.key"/></title>
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
<input type="hidden" value="${companyProfileName}" id="company-profile-name">
<input type="hidden" value="${regionProfileName}" id="region-profile-name">
<input type="hidden" value="${branchProfileName}" id="branch-profile-name">
<input type="hidden" id="profile-fetch-info" fetch-all-reviews="false" total-reviews="0" profile-level="${profileLevel}"/>
<div class="hdr-wrapper">
    <div class="container hdr-container clearfix">
        <div class="float-left hdr-logo"></div>
        <div class="float-left hdr-links clearfix">
            <!-- <div class="hdr-link-item hdr-link-active">Dashboard</div>
            <div class="hdr-link-item">Build Hierarchy</div>
            <div class="hdr-link-item">Build Survey</div>
            <div class="hdr-link-item">User Management</div> -->
        </div>
        <div class="float-right clearfix hdr-btns-wrapper">
            <div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key"/></div>
            <div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key"/></div>
        </div>
    </div>
</div>

    
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row hm-header-row-main clearfix">
            <div class="float-left hm-header-row-left"><spring:message code="label.readwritesharereviews.key"/></div>
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
                    <!-- <div class="prof-rating-mobile-wrapper hide">
                        <div class="st-rating-wrapper maring-0 clearfix">
                            <div class="rating-star icn-full-star"></div>
                            <div class="rating-star icn-full-star"></div>
                            <div class="rating-star icn-half-star"></div>
                            <div class="rating-star icn-no-star"></div>
                            <div class="rating-star icn-no-star"></div>
                        </div>
                    </div> -->
                </div>
            </div>
            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper">
                <div class="prof-name-container" id="prof-company-head-content">
                    <!-- name comes here -->
                </div>
            </div>
            <div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper">
                <div class="prof-user-logo" id="prof-company-logo"></div>
                <div class="prof-user-address" id="prof-company-address">
                    <!-- address comes here -->
                </div>
            </div>
        </div>
        
        <div class="row">
            <div class="prof-left-panel-wrapper margin-top-25 col-lg-4 col-md-4 col-sm-4 col-xs-12">
                
                <div class="prof-left-row prof-left-info bord-bot-dc">
                    <div class="left-contact-wrapper">
                        <div class="left-panel-header"><spring:message code="label.contactinformation.key"/></div>
                        <div class="left-panel-content" id="prof-contact-information">
                            <!--contact info comes here  -->
                        </div>
                    </div>
                </div>
                <div class="prof-left-row prof-left-assoc bord-bot-dc">
                    <div class="left-assoc-wrapper">
                    	<c:choose>
                    		<c:when test="${not empty branchProfileName}">
                    			<div class="left-panel-header"><spring:message code="label.ourbranch.key"/></div>
	                        	<div class="left-panel-content left-panel-content-adj" id="branch-individuals">
	                            	<!--branch hierarchy is displayed here  -->
	                        	</div>
                   		 	</c:when>
                   		 	<c:when test="${not empty regionProfileName}">
                   		 	<input type="hidden" id="branchid-hidden"/>
                   		 		<div class="left-panel-header"><spring:message code="label.ourregion.key"/></div>
	                        	<div class="left-panel-content left-panel-content-adj" id="region-branches">
	                            	<!--region hierarchy is displayed here  -->
	                        	</div>
                   		 	</c:when>
                   		 	<c:when test="${not empty companyProfileName}">
                   		 		<input type="hidden" id="regionid-hidden"/>
                   		 		<input type="hidden" id="branchid-hidden"/>
                   		 		<div class="left-panel-header"><spring:message code="label.ourcompany.key"/></div>
	                        	<div class="left-panel-content left-panel-content-adj" id="comp-regions-content">
	                            	<!--company hierarchy is displayed here  -->
	                        	</div>
                   		 	</c:when>
                    	</c:choose>
                        
                    </div>
                </div>
            </div>
            <div class="row prof-right-panel-wrapper margin-top-25 col-lg-8 col-md-8 col-sm-8 col-xs-12">
                <div class="intro-wrapper rt-content-main bord-bot-dc" id="prof-company-intro">
                    <!-- about me comes here  -->
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
                        </div>
                    </div>
                </div>
                <div class="people-say-wrapper rt-content-main" id="reviews-container">
                    <div class="main-con-header" id="prof-reviews-header"></div>
                    <div id="prof-review-item" class="prof-reviews">
	                   <!--  reviews get populated here --> 
                    </div>
                    <div id="prof-hidden-review-count" class="prof-hidden-review-link">
	                   <!--  count of hidden reviews get populated here --> 
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
<script src="${pageContext.request.contextPath}/resourbranchIdces/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/date.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/profile.js"></script>
<script>
    $(document).ready(function(){
        adjustImage();
        /**
        	If region profile name is mentioned, fetch the region profile 
        	since this would be a call to fetch region profile page 
        */
        var regionProfileName = $("#region-profile-name").val();
        var branchProfileName = $("#branch-profile-name").val();
        if(regionProfileName.length > 0) {
        	fetchRegionProfile(regionProfileName);
        }
        else if(branchProfileName.length > 0){
        	fetchBranchProfile(branchProfileName);
        }
        else{
        	fetchCompanyProfile();
        }
        
       
        $(window).resize(adjustImage);
        
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