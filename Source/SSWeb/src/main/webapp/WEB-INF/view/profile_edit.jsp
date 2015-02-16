<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.contact_details}" var="contactdetail"></c:set>
</c:if>
<c:if test="${not empty contactdetail}">
	<c:set value="${contactdetail.mail_ids}" var="mailIds"></c:set>
	<c:set value="${contactdetail.contact_numbers}" var="contactNumbers"></c:set>
	<c:set value="${contactdetail.web_addresses}" var="webAddresses"></c:set>
</c:if>
<div id="prof-message-header" class="hide"></div>
<div class="hm-header-main-wrapper">
	<div id="principal-detail">
		<input id="profile-user-id" name="profile-user-id" type="hidden" value="${user.userId}">
		<input id="profile-user" type="hidden" value="${profile}">
		<input id="profile-user-mail" type="hidden" value="${mailIds.work}">
		<input id="profile-user-contact" type="hidden" value="${contactNumbers.work}">
		<input id="profile-user-web" type="hidden" value="${webAddresses}">
	</div>
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left"><spring:message code="label.profileheader.key" /></div>
			<div id="prof-edit-social-link" class="prof-edit-social-link float-right hm-hr-row-right clearfix">
				<!-- Call JavaScript function to load social page links -->
			</div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<div class="row prof-pic-name-wrapper">
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper prof-img-wrapper">
				<div id="prof-img-container" class="prog-img-container prof-img-lock-wrapper">
					<div class="prof-img-lock-item prof-img-lock"></div>
					<!-- Call JavaScript function to populate profile image -->
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 prof-wrapper pos-relative prof-name-wrapper">
				<div id="prof-basic-container" class="prof-name-container">
					<div class="float-left lp-edit-wrapper clearfix float-left">
						<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
						<div state="unlocked" class="rp-edit-locks float-left lp-edit-locks-locked"></div>
					</div>
					<div class="prof-address">
						<input id="prof-vertical" class="prof-addline1 prof-edditable" value="${profile.vertical}">
						<input id="prof-title" class="prof-addline2 prof-edditable" value="${profile.contact_details.title}">
					</div>
					<div class="prof-rating clearfix">
						<div class="st-rating-wrapper maring-0 clearfix float-left">
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-half-star"></div>
							<div class="rating-star icn-no-star"></div>
							<div class="rating-star icn-no-star"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-lg-4 col-md-4 col-sm-4 prof-wrapper prof-map-wrapper">
				<!-- <div class="prof-user-logo"></div> -->
				<div id="" class="lp-prog-img-container" style="position: relative;">
					<div id="prof-image" class="prof-image-rp prof-image-edit pos-relative cursor-pointer">
						
					</div>
					<form class="form_contact_image" enctype="multipart/form-data">
						<input type="file" class="con_img_inp_file" id="prof-image-edit">
					</form>
				</div>
				
				<div id="prof-address-container" class="prof-user-address">
					<input id="prof-name" class="prof-user-addline1 prof-edditable prof-addr-center" value="${contactdetail.name}">
					<input id="prof-address1" class="prof-user-addline1 prof-edditable prof-addr-center" value="${contactdetail.address1}">
					<input id="prof-address2" class="prof-user-addline2 prof-edditable prof-addr-center" value="${contactdetail.address2}">
					<input id="prof-country" class="prof-user-addline2 prof-edditable prof-addr-center" value="${contactdetail.country}">
					<input id="prof-zipcode" class="prof-user-addline2 prof-edditable prof-addr-center" value="${contactdetail.zipcode}">
				</div>
			</div>
		</div>

		<div class="row">
			<div class="prof-left-panel-wrapper margin-top-25 col-lg-4 col-md-4 col-sm-4 col-xs-12">
				<div class="prof-left-row prof-left-info bord-bot-dc">
					<div class="left-contact-wrapper">
						<div class="clearfix">
							<div class="float-left left-panel-header"><spring:message code="label.contactinformation.key" /></div>
						</div>
						<div class="left-panel-content" id="contant-info-container">
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-mail"></div>
								<div class="float-left lp-con-row-item" data-email="work">${mailIds.work}</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-web"></div>
								<div>
									<input class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}" placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
	  								<div><input type="button" value="Lock" class="ep-lock ep-lock-btn ep-ulock-btn" style="height: 24px;line-height: 20px;"></div>
	  							</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-phone"></div>
								<div class="float-left lp-edit-wrapper clearfix float-left">
									<input class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}">
									<div state="unlocked" class="lp-edit-locks float-left"></div>
								</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-mbl"></div>
								<div class="float-left lp-edit-wrapper clearfix float-left">
									<input class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}">
									<div state="unlocked" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</div>
							</div>
							<div class="lp-con-row lp-row clearfix">
								<div class="float-left lp-con-icn icn-fax"></div>
								<div>
									<input class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}" placeholder='<spring:message code="label.fax.placeholder.key"/>'>
									<div><input type="button" value="Lock" class="ep-lock ep-lock-btn ep-ulock-btn" style="height: 24px;line-height: 20px;"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<c:choose>
					<c:when	test="${user.companyAdmin}">
						<div class="prof-left-row prof-left-assoc bord-bot-dc">
							<div class="left-assoc-wrapper">
								<div class="clearfix">
									<div class="float-left left-panel-header"><spring:message code="label.membership.key" /></div>
									<div class="float-right icn-share icn-plus-open" onclick="addAnAssociation();"></div>
								</div>
								<div id="association-container" class="left-panel-content">
									<!-- Call javascript function to populate association list -->
								</div>
							</div>
						</div>
						<div class="prof-left-row prof-left-ach bord-bot-dc">
							<div class="left-ach-wrapper">
								<div class="clearfix">
									<div class="float-left left-panel-header"><spring:message code="label.achievement.key" /></div>
									<div class="float-right icn-share icn-plus-open" onclick="addAnAchievement();"></div>
								</div>
								<div id="achievement-container" class="left-panel-content">
									<!--  Call javascript function to populate Achievement list -->
								</div>
							</div>
						</div>
						<div class="prof-left-row prof-left-auth bord-bot-dc">
							<div class="left-auth-wrapper">
								<div class="clearfix">
									<div class="float-left left-panel-header"><spring:message code="label.licenses.key" /></div>
									<div class="float-right icn-share icn-plus-open" onclick="addAuthorisedIn();"></div>
								</div>
								<div id="authorised-in-container" class="left-panel-content">
									<!-- Call javascript function to populate authorised in list -->
								</div>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="prof-left-panel-wrapper margin-top-25 col-lg-4 col-md-4 col-sm-4 col-xs-12"></div>
					</c:otherwise>
				</c:choose>
			</div>
			
			<div class="row prof-right-panel-wrapper margin-top-25 col-lg-8 col-md-8 col-sm-8 col-xs-12">
				<div id="intro-about-me" class="intro-wrapper rt-content-main bord-bot-dc">
					<div class="main-con-header main-con-header-adj clearfix">
						<div class="float-left">
							<spring:message code="label.about.key" /> ${contactdetail.name}
						</div>
						<div class="float-right">
							<div state="unlocked" class="abt-locks-adj lp-edit-locks-locked"></div>
						</div>
					</div>
					<div class="intro-body" id="intro-body-text">
						<c:choose>
							<c:when	test="${not empty contactdetail.about_me && not empty fn:trim(contactdetail.about_me)}">${contactdetail.about_me}</c:when>
							<c:otherwise><spring:message code="label.aboutcompany.empty.key" /></c:otherwise>
						</c:choose>
					</div>
					<textarea class="sb-txtarea hide" id="intro-body-text-edit"></textarea>
				</div>
				<div class="rt-content-main bord-bot-dc clearfix">
					<div class="float-left panel-tweet-wrapper">
						<div class="main-con-header"><spring:message code="label.recenttweets.key"/></div>
						<div class="tweet-panel tweet-panel-left">
							<div class="tweet-panel-item bord-bot-dc clearfix">
								<div class="tweet-icn icn-tweet float-left"></div>
								<div class="tweet-txt float-left">
									<div class="tweet-text-main">Lorem ipsunmm dore tit sre
										leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit
										sre leru Lorem ipsunmm dore tit</div>
									<div class="tweet-text-link">
										<em>http://abblk.com</em>
									</div>
									<div class="tweet-text-time">
										<em>24 minutes ago</em>
									</div>
								</div>
							</div>
							<div class="tweet-panel-item bord-bot-dc clearfix">
								<div class="tweet-icn icn-tweet float-left"></div>
								<div class="tweet-txt float-left">
									<div class="tweet-text-main">Lorem ipsunmm dore tit sre
										leru Lorem ipsunmm dore tit sre leru Lorem ipsunmm dore tit
										sre leru Lorem ipsunmm dore tit</div>
									<div class="tweet-text-link">
										<em>http://abblk.com</em>
									</div>
									<div class="tweet-text-time">
										<em>24 minutes ago</em>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="float-left panel-tweet-wrapper posts-wrapper">
						<div class="main-con-header"><spring:message code="label.latestposts.key"/></div>
						<div class="posts-panel posts-panel-right">
							<div class="posts-panel-item bord-bot-dc">
								<div class="post-txt">Lorem ipsunmm dore tit sre leru
									Lorem ipsunmm dore tit sre leru Lorem</div>
								<div class="post-lnk blue-text">
									<em>The about.me blog</em>
								</div>
							</div>
							<div class="posts-panel-item bord-bot-dc">
								<div class="post-txt">Lorem ipsunmm dore tit sre leru
									Lorem ipsunmm dore tit sre leru Lorem</div>
								<div class="post-lnk blue-text">
									<em>The about.me blog</em>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="people-say-wrapper rt-content-main bord-bot-dc">
					<div class="main-con-header">
						<span class="ppl-say-txt-st">What people say</span> about Anna Thomas
					</div>
					<div class="ppl-review-item">
						<div class="ppl-header-wrapper clearfix">
							<div class="float-left ppl-header-left">
								<div class="ppl-head-1">Matt and Gina Conelly - Lehi, UT</div>
								<div class="ppl-head-2">
									12<sup>th</sup> Sept 2014
								</div>
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
						<div class="ppl-content">Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at
							Sntiner lorenm ipsim dore et ie las.</div>
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

<script src="${pageContext.request.contextPath}/resources/js/editprofile.js"></script>
<script>
	$(document).ready(function() {
		$(document).attr("title", "Profile Settings");
		adjustImage();
		$(window).resize(adjustImage);
		startCompanyProfilePage();
		
		$('.ep-lock').click(function() {
			$(this).val(function(i, text){
		        text === "Lock" ? "Unlock" : "Lock";
		        $(this).val(text);
		    });
		});

		$('.ppl-share-wrapper .icn-plus-open').click(function() {
			$(this).hide();
			$(this).parent().find('.ppl-share-social,.icn-remove').show();
		});

		$('.ppl-share-wrapper .icn-remove').click(function() {
			$(this).hide();
			$(this).parent().find('.ppl-share-social').hide();
			$(this).parent().find('.icn-plus-open').show();
		});

		$('.icn-person').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
			$('.prof-left-panel-wrapper').show();
			$('.prof-right-panel-wrapper').hide();
			adjustImage();
		});

		$('.icn-ppl').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
			$('.prof-left-panel-wrapper').hide();
			$('.prof-right-panel-wrapper').show();
		});

		$('.icn-star-smile').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
		});

		$('.inc-more').click(function() {
			$('.mob-icn').removeClass('mob-icn-active');
			$(this).addClass('mob-icn-active');
		});
	});
</script>