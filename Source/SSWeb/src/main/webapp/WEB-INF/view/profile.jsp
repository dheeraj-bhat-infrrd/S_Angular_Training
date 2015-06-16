<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset="utf-8">
    <link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/rangeslider.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
    <link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
    <script src='//www.google.com/recaptcha/api.js'></script>
    <c:if test="${not empty profile}">
    	<c:if test="${not empty profile.contact_details && not empty profile.contact_details.name }">
    		<c:set var="profName" value="${profile.contact_details.name }"></c:set>
    	</c:if>
	    <c:choose>
	    	<c:when test="${not empty profile.contact_details && not empty profile.contact_details.name}">
	    		<title>${profName} Ratings & Reviews
	    			<c:if test="${not empty profile.vertical }">
	    				 - [${profile.vertical }] Reviews
	    			</c:if>
	    		</title>	
	    	</c:when>
			<c:otherwise>
				<title><spring:message code="label.profile.title.key" /></title>
			</c:otherwise>
		</c:choose>
    	<c:if test="${not empty profile.completeProfileUrl}">
    		<link rel="canonical" href="${profile.completeProfileUrl}">
    	</c:if>
    	<c:if test="${not empty profile.contact_details && not empty profile.contact_details.name}">
    		<meta name="desciption" content="Use SocialSurvey Ratings & Reviews to find out how customers have rated ${profName}.">
    		<meta name="keywords" content="${profName}, ${profName} ratings, ${profName} reviews, ${profName} scorecard, ${profName} ratings and reviews">
    	</c:if>
    	<c:if test="${not empty averageRating}">
    		<fmt:formatNumber var="floatingAverageRating" type="number" value="${averageRating}" maxFractionDigits="2" minFractionDigits="2"/>
    		<fmt:formatNumber var="integerAverageRating" type="number" value="${averageRating}" maxFractionDigits="0"/>
    		<c:if test="${integerAverageRating == 6}">
    			<c:set var="integerAverageRating" value="5"></c:set>
    		</c:if>
    	</c:if>
    </c:if>
</head>
<body>
    <div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
    <div id="report-abuse-overlay" class="overlay-main hide">
    	<div class="overlay-disable-wrapper">
    		<div id="overlay-header" class="ol-header"><spring:message code="label.publicprofile.reportabuse.title.key"/></div>
    		<div class="ol-content">
    			<input type="text" id="report-abuse-cus-name" class="report-abuse-input" placeholder="Name">
    			<input type="email" id="report-abuse-cus-email" class="report-abuse-input" placeholder="Email Address">
    			<textarea id="report-abuse-txtbox" class="report-abuse-txtbox" placeholder='<spring:message code="label.publicprofile.reportabuse.key"/>'></textarea>
    		</div>
    		<div class="rpa-overlay-btn-cont clearfix">
    			<div class="rpa-btn rpa-report-btn ol-btn cursor-pointer">Report</div>
    			<div class="rpa-btn rpa-cancel-btn ol-btn cursor-pointer">Cancel</div>
    		</div>
    	</div>
    </div>
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

<input type="hidden" name="reviewsCount" value="${reviewsCount }">    
<input type="hidden" name="averageRatings" value="${averageRating }">
<input type="hidden" value="${companyProfileName}" id="company-profile-name">
<input type="hidden" value="${regionProfileName}" id="region-profile-name">
<input type="hidden" value="${branchProfileName}" id="branch-profile-name">
<input type="hidden" value="${agentProfileName}" id="agent-profile-name">
<input type="hidden" id="profile-fetch-info" fetch-all-reviews="false" total-reviews="${reviewsCount }" profile-level="${profileLevel}"/>
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
            	<form id="find-pro-form" method="GET" action="/findapro.do">
		           	<div class="float-left prof-input-header">Find a professional</div>
		           	<div class="float-left prof-input-cont">
		           		<input id="find-pro-first-name" name="find-pro-first-name" type="text" placeholder="First Name">
		           	</div>
		           	<div class="float-left prof-input-cont">
		           		<input id="find-pro-last-name" name="find-pro-last-name" type="text" placeholder="Last Name">
		           	</div>
		           	<!-- <input id="find-pro-start-index" name="find-pro-start-index" type="hidden" value="0">
					<input id="find-pro-row-size" name="find-pro-row-size" type="hidden" value="10"> -->
		           	<input id="find-pro-submit" type="button" class="float-left prof-submit-btn" value="Search">
            	</form>
            </div>
        </div>
    </div>
</div>

<div id="profile-main-content" class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="">
    	<div class="container">
        <div class="row prof-pic-name-wrapper">
			<c:if test="${not empty profile.profileImageUrl}">
				<div id="prog-img-container" class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper">
		            <div id="prof-image" class="prof-image pos-relative" style="background: url(${profile.profileImageUrl}) no-repeat center;"></div>
	            </div>
			</c:if>
			<c:if test="${not empty profile.profileImageUrl}">
				<c:set var="profileNameClass" value="profile-name-img-wrapper"></c:set>
			</c:if>
            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper ${profileNameClass}">
                <div class="prof-name-container" id="prof-company-head-content">
                	<div class="prof-name">${profName}</div>
                	<div class="prof-address">
                		<c:if test="${not empty profile.contact_details &&  not empty profile.contact_details.title}">
                			<div class="prof-addline2">${profile.contact_details.title}</div>
                		</c:if>
                		<c:if test="${not empty profile.vertical}">
                			<div class="prof-addline1">${profile.vertical}</div>
                		</c:if>
                	</div>
					<div class="prof-rating clearfix">
						<div class="prof-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp">
							<div class='rating-image float-left smiley-rat-${integerAverageRating }'></div>
							<div class='rating-rounded float-left'>${floatingAverageRating}</div>
						</div>
						<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count">${reviewsCount } Reviews(s)</div>
					</div>
					<div class="prof-btn-wrapper clearfix">
						<div class="prof-btn-contact float-left" onclick="focusOnContact()" >Contact
						<c:choose>
							<c:when test="${not empty agentFirstName}"> ${agentFirstName}</c:when>
							<c:otherwise> ${profName}</c:otherwise>
						</c:choose>
						</div>
						<div class="prof-btn-survey float-left" id="read-write-share-btn">Write a Review</div>
					</div>
            	</div>
            </div>
            <div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper float-right">
            	<c:choose>
            		<c:when test="${not empty profile.logo }">
            			<div class="prof-user-logo" id="prof-company-logo" style="background: url(${profile.logo}) no-repeat center; background-size: 100% auto;"></div>
            		</c:when>
            		<c:otherwise>
            			<div class="prof-user-logo" id="prof-company-logo"></div>
            		</c:otherwise>
            	</c:choose>
                <div class="prof-user-address" id="prof-company-address">
                    <!-- address comes here -->
                </div>
            </div>
            <div class="mob-contact-btn-wrapper">
                <div class="mob-contact-btn-row clearfix">
                    <div class="mob-contact-btn float-left">
                        <div id="mob-contact-btn" class="mob-prof-contact-btn float-right" onclick="focusOnContact()">Contact
	                        <c:choose>
								<c:when test="${not empty agentFirstName}"> ${agentFirstName}</c:when>
								<c:otherwise> ${profName}</c:otherwise>
							</c:choose>
						</div>
                    </div>
                    <div class="mob-contact-btn float-left">
                        <div id="mob-review-btn" class="mob-prof-contact-btn float-left">Write a review</div>
                    </div>
                </div>
                <!-- <div class="vcard-download cursor-pointer">Download Contact</div> -->
            </div>
        </div>
        </div>

		<div class="prof-details-header">
			<div class="container">
				<div class="prof-details-header-row clearfix">
					<div class="prof-link-header float-left clearfix">
						<div id="prof-header-rating" class="rating-image float-left smiley-rat-5"></div>
						<div id="prof-header-url" class="rating-image-txt float-left">
							<c:if test="${not empty profile.completeProfileUrl}">${profile.completeProfileUrl}</c:if>
						</div>
					</div>
					<c:if test="${not empty profile.contact_details && not empty profile.contact_details.web_addresses && not empty profile.contact_details.web_addresses.work}">
						<div id="web-addr-header" class="web-addr-header float-left clearfix">
							<div class="web-address-img float-left"></div>
							<div id="web-address-txt" class="web-address-txt float-left web-address-link" data-link="${profile.contact_details.web_addresses.work}">${profile.contact_details.web_addresses.work}</div>
						</div>
					</c:if>
					<div class="float-right hm-hr-row-right clearfix">
						<c:if test="${not empty profile.socialMediaTokens}">
							<div id="social-connect-txt" class="float-left social-connect-txt">Connect with ${profName }:</div>
							<c:if test="${not empty profile.socialMediaTokens.facebookToken && not empty profile.socialMediaTokens.facebookToken.facebookPageLink}">
								<div id="icn-fb" class="float-left social-item-icon icn-fb" data-link="${profile.socialMediaTokens.facebookToken.facebookPageLink}"></div>
							</c:if>
							<c:if test="${not empty profile.socialMediaTokens.twitterToken && not empty profile.socialMediaTokens.twitterToken.twitterPageLink}">
								<div id="icn-twit" class="float-left social-item-icon icn-twit" data-link="${profile.socialMediaTokens.twitterToken.twitterPageLink}"></div>
							</c:if>
							<c:if test="${not empty profile.socialMediaTokens.linkedInToken && not empty profile.socialMediaTokens.linkedInToken.linkedInPageLink}">
								<div id="icn-lin" class="float-left social-item-icon icn-lin" data-link="${profile.socialMediaTokens.linkedInToken.linkedInPageLink}"></div>
							</c:if>
							<c:if test="${not empty profile.socialMediaTokens.googleToken && not empty profile.socialMediaTokens.googleToken.profileLink}">
								<div id="icn-gplus" class="float-left social-item-icon icn-gplus" data-link="${profile.socialMediaTokens.googleToken.profileLink}"></div>
							</c:if>
							<c:if test="${not empty profile.socialMediaTokens.yelpToken && not empty profile.socialMediaTokens.yelpToken.yelpPageLink}">
								<div id="icn-yelp" class="float-left social-item-icon icn-yelp" data-link="${profile.socialMediaTokens.yelpToken.yelpPageLink}"></div>
							</c:if>
							<c:if test="${not empty profile.socialMediaTokens.zillowToken && not empty profile.socialMediaTokens.zillowToken.zillowProfileLink}">
								<div id="icn-zillow" class="float-left social-item-icon icn-zillow" data-link="${profile.socialMediaTokens.zillowToken.zillowProfileLink}"></div>
							</c:if>
						</c:if>
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
                <c:if test="${not empty profile.contact_details }">
                	<c:if test="${not empty profile.contact_details.web_addresses || not empty profile.contact_details.contact_numbers}">
						<div id="contact-info" class="prof-left-row prof-left-info bord-bot-dc prof-contact-info">
							<div class="left-contact-wrapper">
								<div class="left-panel-header">
									<spring:message code="label.contactinformation.key" />
								</div>
								<div class="left-panel-content" id="prof-contact-information">
									<c:if test="${not empty profile.contact_details.web_addresses && not empty profile.contact_details.web_addresses.work}">
										<div class="lp-con-row lp-row clearfix">
											<div class="float-left lp-con-icn icn-web"></div>
											<div id="web-addr-link-lp" class="float-left lp-con-row-item blue-text web-address-link" data-link="${profile.contact_details.web_addresses.work}">
											</div>
										</div>
									</c:if>
									<c:if test="${not empty profile.contact_details.contact_numbers && not empty profile.contact_details.contact_numbers.work}">
										<div class="lp-con-row lp-row clearfix">
											<div class="float-left lp-con-icn icn-phone"></div>
											<div class="float-left lp-con-row-item">${profile.contact_details.contact_numbers.work}</div>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</c:if>
                </c:if>
                
                <div id="prof-agent-container">
          			<div id="individual-details">
           				<!-- individual details like associations/hobbies/achievements come here -->
           			</div>
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
                    		<div id="prof-contact-hdr" class="left-panel-header prof-contact-hdr">Contact ${profName}</div>
                    		<div class="left-panel-content">
                    			<form id="prof-contact-form" action="">
	                    			<div class="lp-row">
	                    				<div class="lp-input-cont">
	                    					<div class="float-left lp-username-icn lp-input-icn"></div>
	                    					<input id="lp-input-name" type="text" class="lp-input" placeholder="Your name">
	                    				</div>
	                    			</div>
	                    			<div class="lp-row">
	                    				<div class="lp-input-cont lp-email">
	                    					<div class="float-left lp-email-icn lp-input-icn"></div>
	                    					<input id="lp-input-email" type="email" class="lp-input" placeholder="Email Address">
	                    				</div>
	                    			</div>
	                    			<div class="lp-row">
	                    				<div class="lp-input-cont lp-textarea-cont">
	                    					<div class="float-left lp-textarea-icn lp-input-icn"></div>
	                    					<textarea id="lp-input-message" type="email" class="lp-input"
	                    						placeholder="I'd like to get in contact with you about.."></textarea>
	                    				</div>
	                    			</div>
	                    			<div class="lp-row">
		                    			<div class="prof-captcha-cont">
		                    			<div class="g-recaptcha" data-sitekey="6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K"></div>
		                    				<!-- <div id="prof-captcha-img" class="prof-captcha-img"></div>
											<div class="reg-captcha-btns clearfix">
												<input id="captcha-text" class="float-left prof-cap-txt"
													name="captchaResponse" placeholder="Type the above text"
													autocomplete="off" autocorrect="off" autocapitalize="off">
												<div class="clearfix reg-btns-wrapper float-right">
													<div class="float-left reg-cap-img reg-cap-reload"></div>
													<div class="float-left reg-cap-img reg-cap-sound"></div>
													<div class="float-left reg-cap-img reg-cap-info"></div>
												</div>
											</div> -->
										</div>
	                    			</div>
	                    			<div class="lp-row">
	                    				<div class="lp-button">Submit Your Message</div>
	                    			</div>
	                    			<div class="privacy-policy-disclaimer">
	                    				We will only use information you provide on this form to send your message to this professional.
	                    			</div>
	                    			<div id="privacy-policy-link" class="privacy-policy-link"><a href="https://www.socialsurvey.me/survey/privacy-policy/">Privacy Policy</a></div>
                    			</form>
                    		</div>
                    	</div>
                    </div>  
                </div>
            </div>
            
            <div class="row prof-right-panel-wrapper col-lg-8 col-md-8 col-sm-8 col-xs-12">
            	<c:if test="${not empty profile.contact_details && not empty profile.contact_details.about_me }">
	                <div class="intro-wrapper rt-content-main bord-bot-dc hide" id="prof-company-intro">
	                    <div class="main-con-header">About ${profName}</div>
	                    <div class="pe-whitespace intro-body">${profile.contact_details.about_me}</div>
	                </div>
                </c:if>
                <div class="rt-content-main bord-bot-dc clearfix hide" id="recent-post-container">
                    <div class="float-left panel-tweet-wrapper">
                        <div class="main-con-header">Recent Posts</div>
                        <div class="tweet-panel tweet-panel-left tweet-panel-left-adj" id="prof-posts">
                            <!--  latest posts get populated here -->
                        </div>
                    </div>
                </div>
                <div class="people-say-wrapper rt-content-main hide" id="reviews-container">
                	<div class="clearfix hide">
	                    <div class="main-con-header float-left" id="prof-reviews-header">
	                    	<span class="ppl-say-txt-st">What people are saying</span> about ${profName }
	                    </div>
	                    
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

<!-- <div id="outer_captcha" style="display: none;">
	<div id="recaptcha"></div>
</div> -->

<div class="mobile-tabs hide clearfix">
    <div class="float-left mob-icn mob-icn-active icn-person"></div>
    <div class="float-left mob-icn icn-ppl"></div>
    <div class="float-left mob-icn icn-star-smile"></div>
    <div class="float-left mob-icn inc-more"></div>
</div>
<!-- <div style="display: none">
	<script src="https://www.google.com/recaptcha/api/challenge?k=6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-"></script>
</div> -->

<!-- Code snippet to show aggregated ratings for agent in Google results : BOC-->
<div class="hide" itemscope itemtype="http://schema.org/Product">
	<span itemprop="name">Social Survey</span>
	<span id="agent-desc" itemprop="title"></span>
	<div itemprop="aggregateRating" itemscope itemtype="http://schema.org/AggregateRating">Rated 
		<span id="prof-schema-agent-rating" itemprop="ratingValue">${floatingAverageRating }</span>/5 based on 
		<span id="prof-schema-reviews" itemprop="reviewCount">${reviewsCount}</span> reviews
	</div>
</div>
<!-- EOC -->

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/date.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src="${initParam.resourcesPath}/resources/js/index.js"></script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/profile_common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/profile.js"></script>
<script src="${initParam.resourcesPath}/resources/js/googletracking.js"></script>
<script src="${initParam.resourcesPath}/resources/js/googlemaps.js"></script>
<script src="${initParam.resourcesPath}/resources/js/perfect-scrollbar.jquery.min.js"></script>
<script>
    $(document).ready(function(){
    	profileJson = ${profileJson};
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
        
        $('.icn-person').click(function() {
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
            $('#prof-company-intro').show();
            $('#contact-info').hide();
            $('#prof-agent-container').hide();
            $('#reviews-container').hide();
            $('#recent-post-container').hide();
        });

        $('.icn-ppl').click(function() {
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
            $('#recent-post-container').show();
            $('#contact-info').hide();
            $('#prof-agent-container').hide();
            $('#prof-company-intro').hide();
            $('#reviews-container').hide();
        });

        $('.icn-star-smile').click(function() {
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
            $('#reviews-container').show();
            $('#contact-info').hide();
            $('#prof-agent-container').hide();
            $('#prof-company-intro').hide();
            $('#recent-post-container').hide();
        });

        $('.inc-more').click(function() {
            $('.mob-icn').removeClass('mob-icn-active');
            $(this).addClass('mob-icn-active');
            $('#prof-agent-container').show();
            $('#prof-company-intro').hide();
            $('#contact-info').hide();
            $('#reviews-container').hide();
            $('#recent-post-container').hide();
        });
        
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
    			data += "&g-recaptcha-response=" + $('#g-recaptcha-response').val();
    			//data += "&recaptcha_input=" + $('#captcha-text').val();
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
    			
    			// resetting contact form and captcha
    			$('#prof-contact-form')[0].reset();
    			var recaptchaframe = $('.g-recaptcha iframe');
    	        var recaptchaSoure = recaptchaframe[0].src;
    	        recaptchaframe[0].src = '';
    	        setInterval(function () { recaptchaframe[0].src = recaptchaSoure; }, 500);
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
    	setTimeout(function() {
    		ga('send', {
        		'hitType': 'event',
        		'eventCategory': 'review',
        		'eventAction': 'click',
        		'eventLabel': gaLabel,
        		'eventValue': gaName
        	});
		}, 2000);
    	
    	
    	// Find a pro
    	$('#find-pro-form input').keyup(function(e) {
    		if(e.which == 13)
    			submitFindProForm();
		});
    	
    	$('#find-pro-submit').click(function(e) {
    		e.preventDefault();
    		submitFindProForm();
    	});
    	
    	function submitFindProForm() {
    		console.log("Submitting Find a Profile form");
			$('#find-pro-form').submit();
    		showOverlay();
    	}
    	
    	
    	
    	var captchaText = true;
    	/**try {
    		
    		Recaptcha.create('6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-',
    				'recaptcha', {
    					theme : 'white',
    					callback : captchaLoaded
    				});
    		console.log("Captcha loaded");
    	} catch (error) {
    		console.log("Could not load captcha");
    	}*/
    	
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
        		return isContactUsFormValid;
    		}
        	
    		if (!validateEmailId('lp-input-email')) {
    			isContactUsFormValid = false;
    			if (!isFocussed) {
        			$('#lp-input-email').focus();
        			isFocussed=true;
        		}
        		return isContactUsFormValid;
    		}
    		
    		if (!validateMessage('lp-input-message')) {
    			isContactUsFormValid = false;
    			if (!isFocussed) {
        			$('#lp-input-message').focus();
        			isFocussed=true;
        		}
        		return isContactUsFormValid;
    		}
    		
    		if (!validateMessage('captcha-text')) {
    			isContactUsFormValid = false;
    			if (!isFocussed) {
        			$('#captcha-text').focus();
        			isFocussed=true;
        		}
        		return isContactUsFormValid;
    		}
    		
        	return isContactUsFormValid;
    	}    	
		$("#prof-company-review-count").click(function(){
			if(window.innerWidth < 768){
				$('.icn-star-smile').click();					
			}
			$('html, body').animate({
				scrollTop : $('#reviews-container').offset().top
			},500);
		});
    });
</script>
</body>
</html>