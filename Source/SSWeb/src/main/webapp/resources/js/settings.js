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

/*function changeRatingPattern(rating, ratingParent) {
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
}*/

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

function autoSetReminderIntervalStatus() {
	if($('#reminder-needed-hidden').val() == 'true') {
		$('#reminder-interval').attr("disabled", true);
	} else if($('#reminder-needed-hidden').val() == 'false') {
		$('#reminder-interval').removeAttr("disabled");
	}
}

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
				console.log("success");
				$('#overlay-toast').html("Content added successfully!");
			}
		},
		error : function() {
			$('#overlay-toast').html("Oops! Something went wrong. Please try again later.");
		}
	});
}

function paintTextForMood(happyText, neutralText, sadText, happyTextComplete, neutralTextComplete, sadTextComplete){
	$('#happy-text').html(happyText);
	$('#neutral-text').html(neutralText);
	$('#sad-text').html(sadText);

	$('#happy-text-complete').html(happyTextComplete);
	$('#neutral-text-complete').html(neutralTextComplete);
	$('#sad-text-complete').html(sadTextComplete);
}