function adjustTextContainerWidthOnResize() {
	var parentWidth = $('.ctnt-list-item').width();
	var imgWidth = $('.ctnt-list-item .ctnt-list-item-img').width();
	var buttonWidth = $('.ctnt-list-item .ctnt-list-item-btn-wrap').width();
	var textContainerWidth = parentWidth - (imgWidth + buttonWidth) - 20;
	$('.ctnt-list-item .ctnt-list-item-txt-wrap').width(textContainerWidth);
}

//Function to validate the first name pattern
function validateProFirstNamePattern(elementId) {
	if ($('#' + elementId).val() != "") {
		if (nameRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			$('#overlay-toast').html('Please enter a first name pattern.');
			showToast();
			return false;
		}
	} else {
		$('#overlay-toast').html('Please enter a first name pattern.');
		showToast();
		return false;
	}
}

// Function to validate the last name pattern
function validateProLastNamePattern(elementId) {
	if ($('#' + elementId).val() != "") {
		if (lastNameRegEx.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			$('#overlay-toast').html('Please enter a valid last name pattern.');
			showToast();
			return false;
		}
	} else {
		return false;
	}
}

// Function to validate registration form
function validateFindProForm(id) {
	$("#serverSideerror").hide();
	if (!validateProFirstNamePattern('find-pro-first-name') && !validateProLastNamePattern('find-pro-last-name')) {
		$('#find-pro-first-name').focus();
		return false;
	}
	return true;
}