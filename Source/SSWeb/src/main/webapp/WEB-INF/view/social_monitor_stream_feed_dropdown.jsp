<input type="hidden" id="feed-data" data-feeds=[] val="">
<div id="stream-feed-dropdown" class="float-left soc-mon-stream-dropdown">
	<div class="stream-dropdown-select">Feeds (<div id="stream-feed-count" class="stream-dropdown-count-div">0</div> Selected)<img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="feed-chevron-down" class="float-right macro-dropdown-chevron"><img id="feed-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right macro-dropdown-chevron"></div>
	<div id="stream-feed-dropdown-options" class="hide float-left stream-dropdown-actions">
		<div id="feed-facebook" class="stream-dropdown-option-container hide" data-feed="FACEBOOK">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/ss-icon-small-facebook.png" class="float-left margin-right-10 stream-dropdown-img-circle feeds-fb-icon">
			<div class="float-left stream-dropdown-name-txt-bold">Facebook</div>
		</div>
		<div id="feed-twitter" class="stream-dropdown-option-container hide" data-feed="TWITTER">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/ss-icon-small-twitter.png" class="float-left margin-right-10 stream-dropdown-img-circle feeds-tw-icon">
			<div class="float-left stream-dropdown-name-txt-bold">Twitter</div>
		</div>
		<div id="feed-linkedin" class="stream-dropdown-option-container hide" data-feed="LINKEDIN">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/linkedin_circle_color.png" class="float-left margin-right-10 stream-dropdown-img-circle feeds-lin-icon">
			<div class="float-left stream-dropdown-name-txt-bold">LinkedIn</div>
		</div>
		<div id="feed-instagram" class="stream-dropdown-option-container hide" data-feed="INSTAGRAM">
			<img src="${initParam.resourcesPath}/resources/images/check-no.png"  class="feed-unchecked hide float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="feed-checked float-left margin-right-10 cursor-pointer">
			<img src="${initParam.resourcesPath}/resources/images/social_instagram.png" class="float-left margin-right-10 stream-dropdown-img-circle">
			<div class="float-left stream-dropdown-name-txt-bold">Instagram</div>
		</div>
		<div id="feed-empty" class="stream-dropdown-option-container hide">
			<div class="float-left stream-dropdown-name-txt-bold">No feeds found</div>
		</div>
	</div>
</div>

<script>
$(document).ready(function(){
	
	getFeedTypes();
	
});
</script>