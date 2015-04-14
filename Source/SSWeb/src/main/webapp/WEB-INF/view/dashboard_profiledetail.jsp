<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="float-right dash-main-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
	<div class="dsh-graph-wrapper">
		<div class="dsh-g-wrap dsh-g-wrap-1">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-1" class="dsh-graph-img"></div>
				<div id="socl-post" class="dsh-graph-num"></div>
				<div class="dsh-graph-txt dsh-graph-txt-1"><spring:message code="label.socialposts.key" /></div>
			</div>
		</div>
		<div class="dsh-g-wrap dsh-g-wrap-2">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-2" class="dsh-graph-img"></div>
				<div id="srv-snt-cnt" class="dsh-graph-num"></div>
				<div class="dsh-graph-txt dsh-graph-txt-2"><spring:message code="label.totalsurveys.key" /></div>
			</div>
		</div>
		<div class="dsh-g-wrap dsh-g-wrap-3">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-3" class="dsh-graph-img"></div>
				<div id="srv-scr" class="dsh-graph-num"></div>
				<div class="dsh-graph-txt dsh-graph-txt-3"><spring:message code="label.surveyscore.key" /></div>
			</div>
		</div>
		<div class="dsh-g-wrap dsh-g-wrap-4">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-4" class="dsh-graph-img dsh-graph-img-4"></div>
				<div id="dsh-prsn-img" class="dsh-graph-num dsh-graph-num-4 <!-- person-img -->"></div>
				<div class="dsh-graph-txt dsh-graph-txt-4"><spring:message code="label.profilecomplete.key" /></div>
				<div id="badges" class="dsg-g-rbn"></div>
			</div>
		</div>
	</div>
</div>

<div class="float-left dash-main-left col-lg-6 col-md-6 col-sm-6 col-xs-12">
	<div class="dash-left-txt-wrapper">
		<div class="dsh-name-wrapper">
			<div id="name" class="dsh-txt-1">${name}</div>
			<div id="designation" class="dsh-txt-2">${title}</div>
			<div id="company" class="dsh-txt-3">${company}</div>
		</div>
		<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix">
			<div class="float-left dsh-star-item no-star"></div>
			<div class="float-left dsh-star-item no-star"></div>
			<div class="float-left dsh-star-item no-star"></div>
			<div class="float-left dsh-star-item no-star"></div>
			<div class="float-left dsh-star-item no-star"></div>
			<div id="profile-completed" class="float-right dsh-rating-item">0/5</div>
		</div>
		<div class="dsh-btn-complete" onclick="showMainContent('./showprofilepage.do')"><spring:message code="label.complete.profile.key" /></div>
	</div>
</div>
<script>
$(document).ready(function() {
	// Circles
	$('#dg-img-1').find('svg').remove();
	var circle1 = new ProgressBar.Circle('#dg-img-1', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	
	$('#dg-img-2').find('svg').remove();
	var circle2 = new ProgressBar.Circle('#dg-img-2', {
		color : '#E97F30',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	
	$('#dg-img-3').find('svg').remove();
	var circle3 = new ProgressBar.Circle('#dg-img-3', {
		color : '#5CC7EF',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	
	$('#dg-img-4').find('svg').remove();
	var circle4 = new ProgressBar.Circle('#dg-img-4', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});

	// Social Posts
	var socialPosts = "${socialPosts}";
	$("#socl-post").html(socialPosts);
	if ((parseInt(socialPosts) / maxSocialPosts) > 1)
		circle1.animate(1);
	else
		circle1.animate(parseInt(socialPosts) / maxSocialPosts);
	
	// Survey Count
	var surveyCount = "${surveyCount}";
	$("#srv-snt-cnt").html(surveyCount);
	if ((parseInt(surveyCount) / maxSurveySent) > 1)
		circle2.animate(1);
	else
		circle2.animate(parseInt(surveyCount) / maxSurveySent);
	
	// Social Score
	var socialScore = "${socialScore}";
	$("#srv-scr").html(socialScore + "/5");
	if ((parseInt(socialScore) / 5) > 1)
		circle3.animate(1);
	else
		circle3.animate(parseInt(socialScore) / 5);
	
	// Profile completion
	var profileCompleted = parseInt("${profileCompleteness}");
	var starVal = profileCompleted * 5 / 100;
	if ((profileCompleted / 100) > 1)
		circle4.animate(1);
	else
		circle4.animate(profileCompleted / 100);
	$("#pro-cmplt-stars").find('.dsh-star-item').removeClass('sq-full-star');
	$("#pro-cmplt-stars").find('.dsh-star-item').each(function(index) {
		if (index < starVal) {
			$(this).removeClass('no-star');
			$(this).addClass('sq-full-star');
		}
	});
	$("#profile-completed").html(starVal + "/5");
	
	// Badges
	$("#badges").removeClass(".dsg-g-rbn-1");
	$("#badges").removeClass(".dsg-g-rbn-2");
	$("#badges").removeClass(".dsg-g-rbn-3");
	var badges = parseInt("${badges}");
	if (badges == 1)
		$("#badges").addClass("dsg-g-rbn-1");
	else if (badges == 2)
		$("#badges").addClass("dsg-g-rbn-2");
	else if (badges == 3)
		$("#badges").addClass("dsg-g-rbn-3");
});
</script>