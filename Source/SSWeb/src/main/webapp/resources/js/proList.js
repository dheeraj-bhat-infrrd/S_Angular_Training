// pagination variables
var rowSize = 10;
var startIndex = 0;

function adjustTextContainerWidthOnResize() {
	var parentWidth = $('.ctnt-list-item').width();
	var imgWidth = $('.ctnt-list-item .ctnt-list-item-img').width();
	// var buttonWidth = $('.ctnt-list-item .ctnt-list-item-btn-wrap').width();
	// var textContainerWidth = parentWidth - (imgWidth + buttonWidth) - 20;
	var textContainerWidth = parentWidth - imgWidth - 20;
	$('.ctnt-list-item .ctnt-list-item-txt-wrap').width(textContainerWidth);
}

// Function to validate the first name pattern
function validateProFirstNamePattern(elementId) {
	if ($('#' + elementId).val() != "") {
		if (firstNamePatternRegex.test($('#' + elementId).val()) == true) {
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
		if (lastNamePatternRegEx.test($('#' + elementId).val()) == true) {
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

$('#find-pro-submit').click(function(e) {
	e.preventDefault();
	if(validateFindProForm('find-pro-form')){
		console.log("Submitting Find a Profile form");
		$('#find-pro-form').submit();
	}
	showOverlay();
});

$(window).scroll(function() {
	var newIndex = startIndex + rowSize;
	if ((window.innerHeight + window.pageYOffset) >= (document.body.offsetHeight) && newIndex < $('#srch-num').html()) {
		fetchUsers(newIndex);
		startIndex = newIndex;
	}
});

function fetchUsers(newIndex) {
	var formData = new FormData();
	formData.append("find-pro-first-name", $('#fp-first-name-pattern').val());
	formData.append("find-pro-last-name", $('#fp-last-name-pattern').val());
	formData.append("find-pro-start-index", newIndex);
	formData.append("find-pro-row-size", rowSize);

	callAjaxPOSTWithTextData("./findaproscroll.do", infiniteScrollCallback, true, formData);
}

function infiniteScrollCallback(response) {
	var users =  $.parseJSON(response);
	var htmlData = "";
	if (users != null) {
		var loopStatus = $('#fp-users-size').val();
		$.each(users, function(i, user) {
			var evenOdd = (loopStatus % 2 == 0) ? '' : 'ctnt-list-item-even';
			htmlData = htmlData + '<div class="ctnt-list-item clearfix ' + evenOdd + '">'
				+ '<div class="float-left ctnt-list-item-img" style="background: url(' + user.profileImageUrl + ') no-repeat center; background-size: contain;"></div>'
				+ '<div class="float-left ctnt-list-item-txt-wrap">'
					+ '<div class="ctnt-item-name">' + user.displayName + '</div>'
					+ '<div class="ctnt-item-desig">' + user.title + '</div>'
					+ '<div class="ctnt-item-comment">' + user.aboutMe + '</div>'
				+ '</div>'
				+ '<div class="float-left ctnt-list-item-btn-wrap">'
					+ '<div class="ctnt-review-btn" user="' + user.userId + '">Review</div>'
				+ '</div>'
			+ '</div>';
			loopStatus ++;
		});
		
		$('#ctnt-list-wrapper').append(htmlData);
		$('#fp-users-size').val(loopStatus);
	}
}

$(document).on('click', '.ctnt-review-btn', function(){
	console.log($(this).attr('user'));
	var payload = {
		"userId" : $(this).attr('user')
	};
	$.ajax({
		url : "./../rest/survey/redirecttodetailspage",
		type : "GET",
		data : payload,
		datatype : "html",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				window.open(data.responseText);
			}
		}
	});
});
