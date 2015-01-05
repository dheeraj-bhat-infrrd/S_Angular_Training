function saveEncompassDetails(formid) {
	// TODO: validate form elements
	if (validateEncompassInput(formid)) {
		var url = "./saveencompassdetails.do";
		callAjaxFormSubmit(url, saveEncompassDetailsCallBack, formid);
	}
}

function testEncompassConnection(formid) {
	if (validateEncompassInput(formid)) {
		var url = "./testencompassconnection.do";
		callAjaxFormSubmit(url, testEncompassConnectionCallBack, formid);
	}
}

function saveSurveyParticipationMailBodyContent(formid){
	$('#mailcategory').val('participationmail');
	updateMailContent(formid);
}

function saveSurveyParticipationReminderMailBodyContent(formid){
	$('#mailcategory').val('');
	updateMailContent(formid);
}

function updateMailContent(formid){
	var url = "./savesurveyparticipationmail.do";
	callAjaxFormSubmit(url, updateMailContentCallBack, formid);
}

function validateEncompassInput(formid) {
	return true;
}

function saveEncompassDetailsCallBack(data) {
	alert("Details saved.");
}

function testEncompassConnectionCallBack(data) {
	alert("Connection succesful");
}

function updateMailContentCallBack(data){
	alert("Mail content updated");
}

// Rating Settings
function autoAppendRatingDropdown(ratingId, ratingDefault) {
	var maxRating = 5;
	var minRating = 0.5;
	var rating = 0;
	while (maxRating - rating >= minRating) {
		$(ratingId).append($('<option/>').val(maxRating - rating).text(maxRating - rating));
		rating += minRating;
	}
	$(ratingId).val(parseFloat($(ratingDefault).val()));
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

// other settings
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