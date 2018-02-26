<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
	.people-say-wrapper{
		width: 100%;
   	 	padding-top: 0;
    	margin-top: -30px;
    	margin-left: 30px;
	}
</style>
<div id="rep-reviews-container" class="people-say-wrapper rt-content-main rt-content-main-adj" style="margin-left:0">
	<div class="main-con-header clearfix pad-bot-10-resp" style="display: block; border-bottom: 1px solid #dcdcdc; padding: 15px 0;">
		<div id="review-desc" class="float-left dash-ppl-say-lbl" data-profile-name="${profileName}"></div>
	</div>
	<div id="review-details" class="ppl-review-item-wrapper">
		<!-- Populated with dashboard_reviews.jsp -->
	</div>
</div>
<script>
$(document).ready(function() {
	$(window).off('scroll');
	$(window).scroll(function() {
		if(window.location.hash.substr(1) == "showreportingpage" && $('#reviews-tab').hasClass('active')) {
			dashbaordReviewScroll();		
		}
	});
});
</script>