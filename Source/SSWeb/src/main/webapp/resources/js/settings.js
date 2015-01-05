// Encompass
function saveEncompassDetails(formid) {
	if (validateEncompassInput(formid)) {
		var url = "./saveencompassdetails.do";
		console.log("saving encompass details");
		callAjaxFormSubmit(url, saveEncompassDetailsCallBack, formid);
	}
}

function saveEncompassDetailsCallBack(data) {
	console.log("Encompass Details saved");
}

function testEncompassConnection(formid) {
	if (validateEncompassInput(formid)) {
		console.log("testing encompass connection");
		var url = "./testencompassconnection.do";
		callAjaxFormSubmit(url, testEncompassConnectionCallBack, formid);
	}
}

function testEncompassConnectionCallBack(data) {
	console.log("Connection successful");
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

function updateMailContentCallBack(data){
	console.log("Mail content updated");
}


// Mail Reminder
function autoAppendReminderDropdown(reminderId, reminderDefault) {
	autoAppendDropdown(reminderId, reminderDefault, 15, 1);
}

function updateReminderSettings(formid) {
	var url = "./updatesurveyremindersettings.do";
	callAjaxFormSubmit(url, updateReminderSettingsCallBack, formid);
}

function updateReminderSettingsCallBack(data){
	console.log("Reminder Settings Updated");
}


// Ratings Settings
function autoAppendRatingDropdown(ratingId, ratingDefault) {
	autoAppendDropdown(ratingId, ratingDefault, 5, 0.5);
}

function updatePostScore(formid) {
	var url = "./updatesurveysettings.do";
	callAjaxFormSubmit(url, updatePostScoreCallBack, formid);
}
function updatePostScoreCallBack() {
	$('#ratingcategory').val('');
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
function updateOtherSettingsCallBack() {
	$('#othercategory').val('');
}


// Generic functions
function autoAppendDropdown(elementId, elementDefault, maxVal, minVal) {
	var value = 0;
	while (maxVal - value >= minVal) {
		$(elementId).append($('<option/>').val(maxVal - value).text(maxVal - value));
		value += minVal;
	}
	$(elementId).val(parseFloat($(elementDefault).val()));
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


$('#rating-auto-post').change(function() {
	$('#ratingcategory').val('rating-auto-post');
	var rating = $('#rating-auto-post').val();
	var ratingParent = $('#rating-auto-post-parent');

	changeRatingPattern(rating, ratingParent);
	updatePostScore("rating-settings-form");
	$('#rating-auto-post-hidden').val(rating);
});
$('#rating-min-post').change(function() {
	$('#ratingcategory').val('rating-min-post');
	
	var rating = $('#rating-min-post').val();
	var ratingParent = $('#rating-min-post-parent');
	changeRatingPattern(rating, ratingParent);
	
	updatePostScore("rating-settings-form");
	$('#rating-min-post-hidden').val(rating);
});


$('#save-participation-mail-content').click(function(){
	console.log("saving participation details");
	$('#mailcategory').val('participationmail');
	updateMailContent("mail-body-settings-form");
});
$('#save-participation-reminder-mail-content').click(function(){
	console.log("saving participation reminder details");
	$('#mailcategory').val('');
	updateMailContent("mail-body-settings-form");
});

$('#reminder-interval').change(function() {
	$('#mailcategory').val('reminder-interval');

	var reminder = $('#rating-auto-post').val();
	updateReminderSettings("mail-body-settings-form");

	$('#reminder-interval-hidden').val(reminder);
});

$('#st-reminder-on').click(function(){
	$('#mailcategory').val('reminder-needed');
	
	$('#reminder-needed-hidden').val('false');
	$('#st-reminder-off').show();
	$(this).hide();
	
	updateReminderSettings("mail-body-settings-form");
});
$('#st-reminder-off').click(function(){
	$('#mailcategory').val('reminder-needed');

	$('#reminder-needed-hidden').val('true');
	$('#st-reminder-on').show();
	$(this).hide();
	
	updateReminderSettings("mail-body-settings-form");
});


$('#st-settings-location-on').click(function(){
	$('#other-location').val('false');
	$('#othercategory').val('enable-location');
	
	$('#st-settings-location-off').show();
	$(this).hide();
	
	updateOtherSettings("other-settings-form");
});
$('#st-settings-location-off').click(function(){
	$('#other-location').val('true');
	$('#othercategory').val('enable-location');

	$('#st-settings-location-on').show();
	$(this).hide();
	
	updateOtherSettings("other-settings-form");
});


$('#st-settings-account-on').click(function(){
	$('#st-settings-account-off').show();
	$(this).hide();
});
$('#st-settings-account-off').click(function(){
	$('#st-settings-account-on').show();
	$(this).hide();
	$('.overlay-disable').show();
});


$('#st-settings-payment-on').click(function(){
	$('#st-settings-payment-off').show();
	$(this).hide();
});
$('#st-settings-payment-off').click(function(){
	$('#st-settings-payment-on').show();
	$(this).hide();
});


$('#ol-btn-cancel').click(function(){
	$('#st-settings-account-off').show();
	$('#st-settings-account-on').hide();
	$('.overlay-disable').hide();
});