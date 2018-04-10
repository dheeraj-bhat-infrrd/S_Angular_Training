<input type="hidden" id="feed-data" data-feeds=[] val="">
<div id="stream-feed-dropdown" class="float-left soc-mon-stream-dropdown">
	<div class="stream-dropdown-select">Feeds (<div id="stream-feed-count" class="stream-dropdown-count-div">5</div> Selected)<img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="feed-chevron-down" class="float-right macro-dropdown-chevron"><img id="feed-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right macro-dropdown-chevron"></div>
	<div id="stream-feed-dropdown-options" class="hide float-left stream-dropdown-actions">
		<div class="stream-dropdown-option-container" data-feed="FACEBOOK">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/ss-icon-small-facebook.png" class="float-left margin-right-10 stream-dropdown-img-circle">
			<div class="float-left stream-dropdown-name-txt-bold">Facebook</div>
		</div>
		<div class="stream-dropdown-option-container" data-feed="TWITTER">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/ss-icon-small-twitter.png" class="float-left margin-right-10 stream-dropdown-img-circle">
			<div class="float-left stream-dropdown-name-txt-bold">Twitter</div>
		</div>
		<div class="stream-dropdown-option-container" data-feed="LINKEDIN">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/ss-icon-small-linkedin.png" class="float-left margin-right-10 stream-dropdown-img-circle">
			<div class="float-left stream-dropdown-name-txt-bold">LinedIn</div>
		</div>
		<div class="stream-dropdown-option-container" data-feed="GOOGLEPLUS">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/ss-icon-small-gplus.png" class="float-left margin-right-10 stream-dropdown-img-circle">
			<div class="float-left stream-dropdown-name-txt-bold">Google+</div>
		</div>
		<div class="stream-dropdown-option-container" data-feed="ZILLOW">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/Zillow_logo_blue.png" class="float-left margin-right-10 stream-dropdown-img-circle">
			<div class="float-left stream-dropdown-name-txt-bold">Zillow</div>
		</div>
		<div class="stream-dropdown-option-container" data-feed="INSTAGRAM">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/ss-icon-instagram.png" class="float-left margin-right-10 stream-dropdown-img-circle">
			<div class="float-left stream-dropdown-name-txt-bold">Instagram</div>
		</div>
	</div>
</div>

<script>
$(document).ready(function(){
	var feedTypes = ["FACEBOOK","TWITTER","LINKEDIN","GOOGLEPLUS","ZILLOW","INSTAGRAM"];
	$('#feed-data').data('feeds',feedTypes);
	$('#stream-feed-count').html(feedTypes.length);
});
</script>