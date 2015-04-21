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
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" sizes="16x16">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/perfect-scrollbar.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
</head>
<body>
    
    <div id="contact-us-pu-wrapper" class="bd-srv-pu hide">
        <div class="container cntct-us-container">
            <div class="contact-us-pu">
                <div class="bd-quest-item">
                    <div class="bd-q-pu-header bd-q-pu-header-adj clearfix">
                        <div class="float-left bd-q-pu-header-lft">Please enter your message.</div>
                    </div>
                    <div class="bd-q-pu-txt-wrapper pos-relative">
                        <textarea class="bd-q-pu-txtarea"></textarea>
                    </div>
                </div>
                <div class="cntct-us-btns-wrapper clearfix">
                    <div class="bd-q-btn-cancel float-left">Cancel</div>
                    <div class="bd-q-btn-done-pu float-left">Send</div>
                </div>
            </div>
        </div>
    </div>
    
    
<input type="hidden" value="${companyProfileName}" id="company-profile-name">
<input type="hidden" value="${regionProfileName}" id="region-profile-name">
<input type="hidden" value="${branchProfileName}" id="branch-profile-name">
<input type="hidden" value="${agentProfileName}" id="agent-profile-name">
<input type="hidden" id="profile-fetch-info" fetch-all-reviews="false" total-reviews="0" profile-level="${profileLevel}"/>
<div class="hdr-wrapper">
    <div class="container hdr-container clearfix">
        <div class="float-left hdr-logo"></div>
        <div class="float-left hdr-links clearfix"></div>
        <div class="float-right clearfix hdr-btns-wrapper">
            <div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key"/></div>
            <div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key"/></div>
        </div>
    </div>
</div>

    
<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row hm-header-row-main clearfix">
            <div class="float-left hm-header-row-left padding-10"><spring:message code="label.readwritesharereviews.key"/></div>
            <div class="float-right hm-find-pro-right clearfix">
            	<form id="find-pro-form" method="POST" action="${pageContext.request.contextPath}/findapro.do">
		           	<div class="float-left prof-input-header">Find a professional</div>
		           	<div class="float-left prof-input-cont">
		           		<input id="find-pro-first-name" name="find-pro-first-name" type="text" placeholder="First Name">
		           	</div>
		           	<div class="float-left prof-input-cont">
		           		<input id="find-pro-last-name" name="find-pro-last-name" type="text" placeholder="Last Name">
		           	</div>
		           	<input id="find-pro-start-index" name="find-pro-start-index" type="hidden" value="0">
					<input id="find-pro-row-size" name="find-pro-row-size" type="hidden" value="10">
		           	<input id="find-pro-submit" type="button" class="float-left prof-submit-btn" value="Search">
            	</form>
            </div>
        </div>
    </div>
</div>

<div id="profile-main-content" class="prof-main-content-wrapper margin-top-25 hide">
    <div class="">
    	<div class="container">
        <div class="row prof-pic-name-wrapper">
            <div id="prof-img-cont" class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper hide">
                <div class="prog-img-container">
                    <div id="prof-image" class="prof-image pos-relative"></div>
                </div>
            </div>
            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper">
                <div class="prof-name-container" id="prof-company-head-content">
                    <!-- name comes here -->
                </div>
            </div>
            <div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper float-right">
                <div class="prof-user-logo" id="prof-company-logo"></div>
                <div class="prof-user-address" id="prof-company-address">
                    <!-- address comes here -->
                </div>
            </div>
        </div>
        </div>

		<div class="prof-details-header">
			<div class="container">
				<div class="prof-details-header-row clearfix">
					<div class="prof-link-header float-left clearfix">
						<div id="prof-header-rating" class="rating-image float-left"></div>
						<div id="prof-header-url" class="rating-image-txt float-left"></div>
					</div>
					<div id="web-addr-header" class="web-addr-header float-left clearfix hide">
						<div class="web-address-img float-left"></div>
						<div id="web-address-txt" class="web-address-txt float-left"></div>
					</div>
					<div class="float-right hm-hr-row-right clearfix">
						<div id="social-connect-txt" class="float-left social-connect-txt"></div>
						<div id="icn-fb" class="float-left social-item-icon icn-fb"></div>
						<div id="icn-twit" class="float-left social-item-icon icn-twit"></div>
						<div id="icn-lin" class="float-left social-item-icon icn-lin"></div>
						<div id="icn-yelp" class="float-left social-item-icon icn-yelp"></div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="container">
			<div class="row margin-top-10">
            <div class="prof-left-panel-wrapper col-lg-4 col-md-4 col-sm-4 col-xs-12">
                
                <!-- <div class="prof-left-row prof-left-info bord-bot-dc">
                    <div class="left-contact-wrapper">
                        <div class="left-panel-header cursor-pointer vcard-download">Download VCard</div>
                    </div>
                </div> -->
                <%-- <div id="contact-info" class="prof-left-row prof-left-info bord-bot-dc hide">
                    <div class="left-contact-wrapper">
                        <div class="left-panel-header"><spring:message code="label.contactinformation.key"/></div>
                        <div class="left-panel-content" id="prof-contact-information">
                            <!--contact info comes here  -->
                        </div>
                    </div>
                </div> --%>
                
                 	<c:choose>
                   		<c:when test="${not empty branchProfileName}">
                   			<div id="branch-hierarchy" class="prof-left-row prof-left-assoc bord-bot-dc hide">
                   				<div class="left-assoc-wrapper">
                   					<div class="left-panel-header"><spring:message code="label.ourbranch.key"/></div>
                        			<div class="left-panel-content left-panel-content-adj" id="branch-individuals">
                            			<!--branch hierarchy is displayed here  -->
                        			</div>
                        		</div>
              					</div>
                  		 	</c:when>
                  		 	<c:when test="${not empty regionProfileName}">
                   		 	<div id="region-hierarchy" class="prof-left-row prof-left-assoc bord-bot-dc hide">
                   				<div class="left-assoc-wrapper">
		                   		 	<input type="hidden" id="branchid-hidden"/>
		                   		 		<div class="left-panel-header"><spring:message code="label.ourregion.key"/></div>
			                        	<div class="left-panel-content left-panel-content-adj" id="region-branches">
			                            	<!--region hierarchy is displayed here  -->
			                        	</div>
			                    </div>
			               </div>
                  		 	</c:when>
                  		 	<c:when test="${not empty agentProfileName}">
                  		 		<div id="individual-details">
                  		 			<!-- individual details like associations/hobbies/achievements come here -->
                  		 		</div>
                  		 	</c:when>
                  		 	<c:when test="${not empty companyProfileName}">
                  		 		<div id="comp-hierarchy" class="prof-left-row prof-left-assoc bord-bot-dc hide">
                  					<div class="left-assoc-wrapper">
	                   		 			<input type="hidden" id="regionid-hidden"/>
	                   		 			<input type="hidden" id="branchid-hidden"/>
	                   		 			<div class="left-panel-header"><spring:message code="label.ourcompany.key"/></div>
		                        		<div class="left-panel-content left-panel-content-adj" id="comp-regions-content">
		                            		<!--company hierarchy is displayed here  -->
		                        		</div>
	                        		</div>
		               			</div>
                  		 	</c:when>
              		</c:choose>
                    <div class="prof-left-row prof-left-assoc bord-bot-dc">
                    	<div class="left-contact-wrapper">
                    		<div id="prof-contact-hdr" class="left-panel-header prof-contact-hdr"></div>
                    		<div class="left-panel-content">
                    		<form id="prof-contact-form" action="">
	                    			<div class="lp-row">
	                    				<div class="lp-input-cont">
	                    					<div class="float-left lp-username-icn lp-input-icn"></div>
	                    					<input id="lp-input-name" type="text" class="lp-input" placeholder="example: John Doe">
	                    				</div>
	                    			</div>
	                    			<div class="lp-row">
	                    				<div class="lp-input-cont lp-email">
	                    					<div class="float-left lp-email-icn lp-input-icn"></div>
	                    					<input id="lp-input-email" type="email" class="lp-input" placeholder="example: office@example.com">
	                    				</div>
	                    			</div>
	                    			<div class="lp-row">
	                    				<div class="lp-input-cont lp-textarea-cont">
	                    					<div class="float-left lp-textarea-icn lp-input-icn"></div>
	                    					<textarea id="lp-input-message" type="email" class="lp-input" placeholder="example: I'd like to say 'good job!'"></textarea>
	                    				</div>
	                    			</div>
	                    			<div class="lp-row">
		                    			<div class="prof-captcha-cont">
											<div id="prof-captcha-img" class="prof-captcha-img"></div>
											<div class="reg-captcha-btns clearfix">
												<input id="captcha-text" class="float-left prof-cap-txt"
													name="captchaResponse" placeholder="Type the above text"
													autocomplete="off" autocorrect="off" autocapitalize="off">
												<div class="clearfix reg-btns-wrapper float-right">
													<div class="float-left reg-cap-img reg-cap-reload"></div>
													<div class="float-left reg-cap-img reg-cap-sound"></div>
													<div class="float-left reg-cap-img reg-cap-info"></div>
												</div>
											</div>
										</div>
	                    			</div>
	                    			<div class="lp-row">
	                    				<div class="lp-button">Submit Your message</div>
	                    			</div>
                    			</form>
                    		</div>
                    	</div>
                    </div>  
                    
            </div>
            <div class="row prof-right-panel-wrapper col-lg-8 col-md-8 col-sm-8 col-xs-12">
                <div class="intro-wrapper rt-content-main bord-bot-dc hide" id="prof-company-intro">
                    <!-- about me comes here  -->
                </div>
                <div class="rt-content-main bord-bot-dc clearfix hide" id="recent-post-container">
                    <div class="float-left panel-tweet-wrapper">
                        <div class="main-con-header">Recent Posts</div>
                        <div class="tweet-panel tweet-panel-left tweet-panel-left-adj" id="prof-posts">
                            <!-- <div class="tweet-panel-item bord-bot-dc clearfix">
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
                            </div> -->
                        </div>
                    </div>
                </div>
                <div class="people-say-wrapper rt-content-main hide" id="reviews-container">
                	<div class="clearfix hide">
	                    <div class="main-con-header float-left" id="prof-reviews-header"></div>
	                    
	                    <div id="prof-reviews-sort" class="prof-reviews-sort clearfix float-right hide">
	                    	<div id="sort-by-feature" class="prof-review-sort-link float-left">Sort by Feature</div>
	                    	<div class="prof-reviews-sort-divider float-left">|</div>
	                    	<div id="sort-by-date" class="prof-review-sort-link float-right">Sort by Date</div>
	                    </div>
                    </div>
                    
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
</div>

<div id="outer_captcha" style="display: none;">
	<div id="recaptcha"></div>
</div>

<div class="mobile-tabs hide clearfix">
    <div class="float-left mob-icn mob-icn-active icn-person"></div>
    <div class="float-left mob-icn icn-ppl"></div>
    <div class="float-left mob-icn icn-star-smile"></div>
    <div class="float-left mob-icn inc-more"></div>
</div>
<div style="display: none">
	<script src="https://www.google.com/recaptcha/api/challenge?k=6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-"></script>
</div>
<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/date.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/index.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/profile_common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/profile.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/googletracking.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/googlemaps.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/perfect-scrollbar.jquery.min.js"></script>
<script>
    $(document).ready(function(){
        adjustImage();
        var gaLabel;
        var gaName;
        
        /**
	    	If region profile name is mentioned, fetch the region profile 
    		since this would be a call to fetch region profile page 
	    */
        var regionProfileName = $("#region-profile-name").val();
        var branchProfileName = $("#branch-profile-name").val();
        var agentProfileName = $("#agent-profile-name").val();
        if(regionProfileName.length > 0) {
        	fetchRegionProfile(regionProfileName);
        	gaLabel = 'region';
        	gaName = regionProfileName;
        }
        else if(branchProfileName.length > 0){
        	fetchBranchProfile(branchProfileName);
        	gaLabel = 'office';
        	gaName = branchProfileName;
        }
        else if(agentProfileName.length > 0){
        	fetchAgentProfile(agentProfileName);
        	gaLabel = 'individual';
        	gaName = agentProfileName;
        } 
        else{
        	fetchCompanyProfile();
        	gaLabel = 'company';
        	gaName = companyProfileName;
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
                var rowW = $('.lp-con-row').width() - 55 - 10;
                $('.lp-con-row-item').width(rowW+'px');
                $('.footer-main-wrapper').hide();
                $('.footer-main-wrapper').hide();
            }else{
                $('.prof-name-container,#prof-image').height(200);
                $('.lp-con-row-item').width('auto');
                $('.footer-main-wrapper').show();
            }
        }
        
        $(document).on('click','.bd-q-contact-us',function(){
            $('#contact-us-pu-wrapper').show();
            $('body').addClass('body-no-scroll-y');
        });
        
        $(document).on('click','.bd-q-btn-cancel',function(){
            $('#contact-us-pu-wrapper').hide();
            $('body').removeClass('body-no-scroll-y');
        });
        
        $('.lp-button').click(function(event){
        	
        	if(validateContactUsForm()){
        		
        		url = window.location.origin + "/pages/profile/sendmail.do";
    			data = "";
    			if($("#agent-profile-name").val() != ""){
    				data += "profilename=" + $("#agent-profile-name").val();
    				data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
    			}
    			else if($("#company-profile-name").val() != ""){
    				data += "profilename=" + $("#company-profile-name").val();
    				data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
    			}
    			else if($("#region-profile-name").val() != ""){
    				data += "profilename=" + $("#region-profile-name").val();
    				data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
    			}
    			else if($("#branch-profile-name").val() != ""){
    				data += "profilename=" + $("#branch-profile-name").val();
    				data += "&profiletype=" + $("#profile-fetch-info").attr("profile-level");
    			}
    			
    			data += "&name=" + $('#lp-input-name').val();
    			data += "&email=" + $('#lp-input-email').val();
    			data += "&message=" + $('#lp-input-message').val();
    			data += "&recaptcha_challenge_field=" + $('#recaptcha_challenge_field').val();
    			data += "&recaptcha_input=" + $('#captcha-text').val();
    			
    			showOverlay();
    			callAjaxPostWithPayloadData(url,showMessage,data,true);
        	}			
		});
        
        function showMessage(data){
        	var jsonData = JSON.parse(data);
        	console.log("Data recieved : " + jsonData);
        	if(jsonData["success"] == 1){
        		console.log("Added toast message. Showing it now");
	    		showInfoMobileAndWeb(jsonData["message"]);
        		console.log("Finished showing the toast");
    			$(".reg-cap-reload").click();
        	}
        	else{
        		console.error("Error occured while sending contact us message. ");
        		showErrorMobileAndWeb(jsonData["message"]);
        		console.log("Finished showing the toast");
    			$(".reg-cap-reload").click();
        	}
        }
        
        $(document).on('click','.vcard-download', function(){
        	var agentName = $("#agent-profile-name").val();
        	downloadVCard(agentName);
        });
        
    	// Google analytics for reviews
    	ga('send', {
    		'hitType': 'event',
    		'eventCategory': 'review',
    		'eventAction': 'click',
    		'eventLabel': gaLabel,
    		'eventValue': gaName
    	});
    	
    	
    	// Find a pro
    	$('#find-pro-submit').click(function(e) {
    		e.preventDefault();
    		submitFindProForm();
    	});
    	
    	function submitFindProForm() {
    		console.log("Submitting Find a Profile form");
    		if(validateFindProForm('find-pro-form')){
    			$('#find-pro-form').submit();
    		}
    		showOverlay();
    	}
    	
    	
    	
    	var captchaText = true;
    	try {
    		
    		Recaptcha.create('6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-',
    				'recaptcha', {
    					theme : 'white',
    					callback : captchaLoaded
    				});
    		console.log("Captcha loaded");
    	} catch (error) {
    		console.log("Could not load captcha");
    	}
    	
    	function captchaLoaded() {
    		var imgData = $(".recaptcha_image_cell").html();
    		console.log("Captcha image data : " + imgData);
    		var challenge = Recaptcha.get_challenge('6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-');
    		if(challenge == undefined){
    			$(".reg-cap-reload").trigger('click');
    		}else{
    			$("#prof-captcha-img").html(imgData);
    		}
    	}

    	$(".reg-cap-reload").click(function() {
    		console.log("Captcha reload button clicked");
    		Recaptcha.reload();
    		console.log("Initiated the click of hidden reload");
    	});

    	$(".reg-cap-sound").click(function() {
    		if (captchaText == true) {
    			console.log("Captcha sound button clicked");
    			$("#recaptcha_switch_audio").click();
    			console.log("Initiated the click of hidden sound");
    			captchaText = false;
    			$(this).addClass('reg-cap-text');
    		} else {
    			console.log("Captcha text button clicked");
    			$("#recaptcha_switch_img").click();
    			console.log("Initiated the click of hidden text");
    			captchaText = true;
    			$(this).removeClass('reg-cap-text');
    		}
    	});

    	$(".reg-cap-info").click(function() {
    		console.log("Info button clicked");
    		$("#recaptcha_whatsthis").click();
    	});
    	
    	// Contact us form validation functions
    	
    	function validateMessage(elementId) {
    		if ($('#'+elementId).val() != "") {
    			return true;
	    	} else {
	    		showErrorMobileAndWeb('Please enter your message!');
	    		return false;
	    	}
    	}
    	
    	function validateName(elementId){
    		if ($('#'+elementId).val() != "") {
    			if (nameRegex.test($('#'+elementId).val()) == true) {
    				return true;
    			} else {
    				showErrorMobileAndWeb('Please enter your valid name!');
    				return false;
    			}
    		} else {
    			showErrorMobileAndWeb('Please enter your valid name!');
    			return false;
    		}
    	}
    	
    	$('#lp-input-name').blur(function() {
    		if (validateName(this.id)) {
    			hideError();
    		}
    	});
    	
    	$('#lp-input-email').blur(function() {
    		if (validateEmailId(this.id)) {
    			hideError();
    		}
    	});
    	
    	$('#lp-input-message').blur(function() {
    		if (validateMessage(this.id)) {
    			hideError();
    		}
    	});
    	
    	$('#captcha-text').blur(function() {
    		if (validateMessage(this.id)) {
    			hideError();
    		}
    	});
    	
    	
    	function validateContactUsForm() {
        	isContactUsFormValid = true;

        	var isFocussed = false;
        	var isSmallScreen = false;
        	if($(window).width() < 768){
        		isSmallScreen = true;
        	}
        	
        	// Validate form input elements
    		if (!validateName('lp-input-name')) {
    			isContactUsFormValid = false;
    			if (!isFocussed) {
        			$('#lp-input-name').focus();
        			isFocussed=true;
        		}
        		if (isSmallScreen) {
        			return isContactUsFormValid;
        		}
    		}
        	
    		if (!validateEmailId('lp-input-email')) {
    			isContactUsFormValid = false;
    			if (!isFocussed) {
        			$('#lp-input-email').focus();
        			isFocussed=true;
        		}
        		if (isSmallScreen) {
        			return isContactUsFormValid;
        		}
    		}
    		
    		if (!validateMessage('lp-input-message')) {
    			isContactUsFormValid = false;
    			if (!isFocussed) {
        			$('#lp-input-message').focus();
        			isFocussed=true;
        		}
        		if (isSmallScreen) {
        			return isContactUsFormValid;
        		}
    		}
    		
    		if (!validateMessage('captcha-text')) {
    			isContactUsFormValid = false;
    			if (!isFocussed) {
        			$('#captcha-text').focus();
        			isFocussed=true;
        		}
        		if (isSmallScreen) {
        			return isContactUsFormValid;
        		}
    		}
    		
        	return isContactUsFormValid;
    	}    	
    });
</script>
</body>
</html>