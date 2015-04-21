// Encompass
function saveEncompassDetails(formid) {
	if (validateEncompassInput(formid)) {
		var url = "./saveencompassdetails.do";
		callAjaxFormSubmit(url, saveEncompassDetailsCallBack, formid);
	}
}

function saveEncompassDetailsCallBack(response) {
	$("#overlay-toast").html(response);
	showToast();
}

function testEncompassConnection(formid) {
	if (validateEncompassInput(formid)) {
		var url = "./testencompassconnection.do";
		callAjaxFormSubmit(url, testEncompassConnectionCallBack, formid);
	}
}

function testEncompassConnectionCallBack(response) {
	$("#overlay-toast").html(response);
	showToast();
}

var isEncompassValid;
function validateEncompassInput(elementId) {
	isEncompassValid = true;
	var isFocussed = false;
	
	if(!validateEncompassUserName('encompass-username')){
		isEncompassValid = false;
		if(!isFocussed){
			$('#encompass-username').focus();
			isFocussed=true;
		}
	}
	if(!validateEncompassPassword('encompass-password')){
		isEncompassValid = false;
		if(!isFocussed){
			$('#encompass-password').focus();
			isFocussed=true;
		}
	}
	if (!validateURL('encompass-url')) {
		isEncompassValid = false;
		if(!isFocussed){
			$('#encompass-url').focus();
			isFocussed=true;
		}
	}
	return isEncompassValid;
}


// Mail content
function updateMailContent(formid){
	var url = "./savesurveyparticipationmail.do";
	callAjaxFormSubmit(url, updateMailContentCallBack, formid);
}

function updateMailContentCallBack(response){
	$("#overlay-toast").html(response);
	showToast();
}


// Mail Reminder
function autoAppendReminderDropdown(reminderId, reminderDefault) {
	autoAppendDropdown(reminderId, 15, 1);
}

function updateReminderSettings(formid) {
	var url = "./updatesurveyremindersettings.do";
	callAjaxFormSubmit(url, updateReminderSettingsCallBack, formid);
}

function updateReminderSettingsCallBack(response){
	$("#overlay-toast").html(response);
	showToast();
}


// Ratings Settings
function autoAppendRatingDropdown(ratingId, classes) {
	autoAppendDropdown(ratingId, classes, 5, 0.5);
}

function updatePostScore(formid) {
	var url = "./updatesurveysettings.do";
	callAjaxFormSubmit(url, updatePostScoreCallBack, formid);
}
function updatePostScoreCallBack(response) {
	$('#ratingcategory').val('');
	$("#overlay-toast").html(response);
	showToast();
}

function changeRatingPattern(rating, ratingParent) {
	var counter = 0;
	ratingParent.children().each(function() {
		$(this).addClass("icn-no-star");
		$(this).removeClass("icn-half-star");
		$(this).removeClass("icn-full-star");

		if (rating >= counter) {
			if (rating - counter >= 1) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-full-star");
			} else if (rating - counter == 0.5) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-half-star");
			}
		}
		counter++;
	});
}

// Other settings
function autoSetCheckboxStatus(on, off, status) {
	if($(status).val() == 'true') {
	$(on).show();
        $(off).hide();
	} else if($(status).val() == 'false') {
        $(on).hide();
	$(off).show();
	}
}

function updateOtherSettings(formid) {
	var url = "./updateothersettings.do";
	callAjaxFormSubmit(url, updateOtherSettingsCallBack, formid);
}
function updateOtherSettingsCallBack(response) {
	$('#othercategory').val('');
	$("#overlay-toast").html(response);
	showToast();
}


// Generic functions
function autoAppendDropdown(elementId, classes, maxVal, minVal) {
	var value = 0;
	while (maxVal - value >= minVal) {
		$(elementId).append($('<div/>').addClass(classes).text(maxVal - value));
		value += minVal;
	}
}

$('#encompass-username').blur(function() {
	validateEncompassUserName(this.id);
});
$('#encompass-password').blur(function() {
	validateEncompassPassword(this.id);
});
$('#encompass-url').blur(function() {
	validateURL(this.id);
});
$('#encompass-save').click(function(){
	if(validateEncompassInput('encompass-form-div')) {
		saveEncompassDetails("encompass-form");
	}
}); 
$('#encompass-testconnection').click(function(){
	if(validateEncompassInput('encompass-form-div')) {
		testEncompassConnection("encompass-form");
	}
});

$('body').on('click','.st-dd-item-auto-post',function(){
	$('#rating-auto-post').val($(this).html());
	$('#st-dd-wrapper-auto-post').slideToggle(200);

	$('#ratingcategory').val('rating-auto-post');
	var rating = $('#rating-auto-post').val();
	var ratingParent = $('#rating-auto-post-parent');

	changeRatingPattern(rating, ratingParent);
	updatePostScore("rating-settings-form");
});

$('body').on('click','.st-dd-item-min-post',function(){
	$('#rating-min-post').val($(this).html());
	$('#st-dd-wrapper-min-post').slideToggle(200);
	
	$('#ratingcategory').val('rating-min-post');
	
	var rating = $('#rating-min-post').val();
	var ratingParent = $('#rating-min-post-parent');
	changeRatingPattern(rating, ratingParent);
	
	updatePostScore("rating-settings-form");
});


$('#edit-participation-mail-content').click(function(){
	$('#survey-participation-mailcontent').ckeditorGet().setReadOnly(false);
	
	$('#save-participation-mail-content').show();
	$('#save-participation-mail-content-disabled').hide();
	
	$('#edit-participation-mail-content-disabled').show();
	$(this).hide();
});
$('#save-participation-mail-content').click(function(){
	$('#mailcategory').val('participationmail');
	updateMailContent("mail-body-settings-form");
	
	$('#survey-participation-mailcontent').ckeditorGet().setReadOnly(true);
	
	$(this).hide();
	$('#save-participation-mail-content-disabled').show();

	$('#edit-participation-mail-content').show();
	$('#edit-participation-mail-content-disabled').hide();
});


$('#edit-participation-reminder-mail-content').click(function(){
	$('#survey-participation-reminder-mailcontent').ckeditorGet().setReadOnly(false);
	
	$('#save-participation-reminder-mail-content').show();
	$('#save-participation-reminder-mail-content-disabled').hide();
	
	$('#edit-participation-reminder-mail-content-disabled').show();
	$(this).hide();
});
$('#save-participation-reminder-mail-content').click(function(){
	$('#mailcategory').val('participationremindermail');
	updateMailContent("mail-body-settings-form");
	;
	$('#survey-participation-reminder-mailcontent').ckeditorGet().setReadOnly(true);
	
	$(this).hide();
	$('#save-participation-reminder-mail-content-disabled').show();

	$('#edit-participation-reminder-mail-content').show();
	$('#edit-participation-reminder-mail-content-disabled').hide();
});


$('#reminder-interval').change(function() {
	$('#mailcategory').val('reminder-interval');
	if(validateReminderInterval('reminder-interval')) {
		updateReminderSettings("mail-body-settings-form");
	}
});
function autoSetReminderIntervalStatus() {
	if($('#reminder-needed-hidden').val() == 'true') {
		$('#reminder-interval').attr("disabled", true);
	} else if($('#reminder-needed-hidden').val() == 'false') {
		$('#reminder-interval').removeAttr("disabled");
	}
}

$('#st-reminder-on').click(function(){
	$('#mailcategory').val('reminder-needed');
	
	$('#reminder-needed-hidden').val('false');
	$('#st-reminder-off').show();
	$(this).hide();

	$('#reminder-interval').removeAttr("disabled");
	updateReminderSettings("mail-body-settings-form");
});
$('#st-reminder-off').click(function(){
	$('#mailcategory').val('reminder-needed');

	$('#reminder-needed-hidden').val('true');
	$('#st-reminder-on').show();
	$(this).hide();
	
	$('#reminder-interval').attr("disabled", true);
	updateReminderSettings("mail-body-settings-form");
});


$('#st-settings-location-on').click(function(){
	$('#othercategory').val('other-location');
	$('#other-location').val('false');
	
	$('#st-settings-location-off').show();
	$(this).hide();
	
	updateOtherSettings("other-settings-form");
});
$('#st-settings-location-off').click(function(){
	$('#othercategory').val('other-location');
	$('#other-location').val('true');

	$('#st-settings-location-on').show();
	$(this).hide();
	
	updateOtherSettings("other-settings-form");
});


$('#st-settings-payment-on').click(function(){
	$('#st-settings-payment-off').show();
	$(this).hide();
});
$('#st-settings-payment-off').click(function(){
	$('#st-settings-payment-on').show();
	$(this).hide();
	showPaymentOptions();
});


$('#st-settings-account-on').click(function(){
	$('#other-account').val('false');
	createPopupConfirm("Enable Account", "Do you want to Continue?");
	overlayAccount();
});
$('#st-settings-account-off').click(function(){
	$('#other-account').val('true');
	createPopupConfirm("Disable Account", "You will not be able to access the application after your billing cycle.<br/> Do you want to Continue?");
	overlayAccount();
});
function overlayAccount(){
	$('#othercategory').val('other-account');

	$('#overlay-continue').click(function(){
		$('#st-settings-account-off').toggle();
		$('#st-settings-account-on').toggle();

		overlayRevert();
		updateOtherSettings("other-settings-form");
		$('#othercategory').val('');
		$('#overlay-continue').unbind('click');
	});
	$('#overlay-cancel').click(function(){
		$('#overlay-continue').unbind('click');
		overlayRevert();
		$('#othercategory').val('');
	});
}

function createPopupConfirm(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Ok");
	$('#overlay-cancel').html("Cancel");

	$('#overlay-main').show();
}
function overlayRevert() {
	$('#overlay-main').hide();
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');
}

function showPaymentOptions() {
	console.log("Calling payment controller for payment upgrade page");	
	$('body').addClass('body-no-scroll');
	var url = "./paymentchange.do";
    showOverlay();
    callAjaxGET(url,displayPopup);
}

function displayPopup(data){
	console.log("display message called :data "+data);
	$("#temp-div").html(data);
	var displayMessageDiv = $("#display-msg-div");
	if($(displayMessageDiv).hasClass("message")) {
		console.log("Error occured. Hiding Overlay");
		hideOverlay();
		$('#st-settings-payment-off').show();
   		$('#st-settings-payment-on').hide();
		console.log("Removing no-scroll class from body");
		$('body').removeClass('body-no-scroll');
		$("#overlay-toast").html($(displayMessageDiv).html());
		showToast();
	}	
	else{
		$('.overlay-payment').html(data);
    	console.log("Html content loaded");
    	hideOverlay();
    	$('.overlay-payment').show();
    	console.log("Showing popup");
    }
	$("#temp-div").html("");
}

$('#happy-text').blur(function() {
	  saveTextForMoodFlow($("#happy-text").val(), "happy");
});

$('#neutral-text').blur(function() {
	saveTextForMoodFlow($("#neutral-text").val(), "neutral");
});

$('#sad-text').blur(function() {
	saveTextForMoodFlow($("#sad-text").val(), "sad");
});

$('#atpst-chk-box').click(function(){
	if($('#atpst-chk-box').hasClass('bd-check-img-checked')){
		$('#atpst-chk-box').removeClass('bd-check-img-checked');
		updateAutoPostSetting(true);
	}
	else{
		$('#atpst-chk-box').addClass('bd-check-img-checked');
		updateAutoPostSetting(false);
	}
});

function updateAutoPostSetting(isautopostenabled){
	
	var payload = {
		"autopost" : isautopostenabled
	};
	var success = false;
	$.ajax({
		url : "./updateautopostforsurvey.do",
		type : "GET",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				$('#overlay-toast').html("Content added successfully!");
			}
		},
		error : function() {
			$('#overlay-toast').html(
					"Oops! Something went wrong. Please try again later.");
		}
	});
}

function saveTextForMoodFlow(content, mood){
	var payload = {
		"text" : content,
		"mood" : mood
	};
	var success = false;
	$.ajax({
		url : "./storetextforflow.do",
		type : "GET",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				$('#overlay-toast').html("Content added successfully!");
			}
		},
		error : function() {
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
	});
}

function paintTextForMood(happyText, neutralText, sadText){
	console.log(happyText);
	console.log(neutralText);
	console.log(sadText);
	$('#happy-text').html(happyText);
	$('#neutral-text').html(neutralText);
	$('#sad-text').html(sadText);
}

// Settings View as
$('#setting-sel').click(function(e){
    e.stopPropagation();
	$('#se-dd-wrapper-profiles').slideToggle(200);
});

$('.se-dd-item').click(function(){
	var newProfileId = $(this).data('profile-id');
	
	$('#setting-sel').html($(this).html());
	$('#se-dd-wrapper-profiles').slideToggle(200);
	
	showMainContent('./showcompanysettings.do?profileId=' + newProfileId);
});

$('body').click(function(e){
	e.stopImmediatePropagation();
	if ($('#se-dd-wrapper-profiles').css('display') == "block") {
		$('#se-dd-wrapper-profiles').slideToggle(200);
	}
});