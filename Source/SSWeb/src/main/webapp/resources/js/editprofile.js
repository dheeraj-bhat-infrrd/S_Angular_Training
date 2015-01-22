/**
 * JavaScript file for company settings page
 */
var timer = 0;
var delay = (function() {
	return function(callback, ms) {
		clearTimeout(timer);
		timer = setTimeout(callback, ms);
	};
})();

// Adjust image
function adjustImage() {
	var windW = $(window).width();
	if (windW < 768) {
		var imgW = $('#prof-image').width();
		$('#prof-image').height(imgW * 0.7);
		var h2 = $('.prog-img-container').height() - 11;
		$('.prof-name-container').height(h2);
		var rowW = $('.lp-con-row').width() - 55 - 10 - 5;
		$('.lp-con-row-item').width(rowW + 'px');
		$('.footer-main-wrapper').hide();
	} else {
		$('.prof-name-container,#prof-image').height(200);
		var rowW = $('.lp-con-row').width() - 50;
		$('.lp-con-row-item').width(rowW + 'px');
		// $('.lp-con-row-item').width('auto');
		$('.footer-main-wrapper').show();
	}
}

$(document).on('focus', '.prof-edditable', function() {
	$('.prof-edditable').addClass('prof-name-edit');
});

$(document).on('blur', '.prof-edditable', function() {
	$('.prof-edditable').removeClass('prof-name-edit');
});

$(document).on('focus', '.prof-edditable-sin', function() {
	$(this).addClass('prof-name-edit');
});

$(document).on('blur', '.prof-edditable-sin', function() {
	$(this).removeClass('prof-name-edit');
});

$(document).on('click', '#prof-image-upload', function() {
	$('#prof-image-edit').click();
});

$(document).on('change', '#prof-image-edit', function() {
	var formData = new FormData();
	formData.append("logo", $(this).prop("files")[0]);
	formData.append("logoFileName", $(this).prop("files")[0].name);
	updateLogoImage(formData);
});

// Function to call when the company profile page is loaded
function startCompanyProfilePage() {
	showContactDetails();
	showAssociationList();
	showAchievementList();
	showLicenceList();
	showAddressDetails();
	showProfileImage();
}

// Function to populate contact details container
function showContactDetails() {
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);
}

function callBackShowContactDetails(data) {
	$('#contant-info-container').html(data);
	adjustImage();
}

// Function to populate associations container
function showAssociationList() {
	callAjaxGET("./fetchassociations.do", callBackShowAssociationList);
}

function callBackShowAssociationList(data) {
	$('#association-container').html(data);
	adjustImage();
}

// Function to populate achievement list container
function showAchievementList() {
	callAjaxGET("./fetchachievements.do", callBackShowAchievementList);
}

function callBackShowAchievementList(data) {
	$('#achievement-container').html(data);
	adjustImage();
}

// Function to populate licence list container
function showLicenceList() {
	callAjaxGET("./fetchlicences.do", callBackShowLicenceList);
}

function callBackShowLicenceList(data) {
	$('#authorised-in-container').html(data);
	adjustImage();
}

// Function to populate address details container
function showAddressDetails() {
	callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails);
}

function callBackShowAddressDetails(data) {
	$('#prof-name-container').html(data);
	adjustImage();
}

// Function to populate profile logo container
function showProfileImage() {
	callAjaxGET("./fetchprofileimage.do", callBackShowProfileImage);
}

function callBackShowProfileImage(data) {
	$('#prof-img-container').html(data);
	var logoImageUrl = $('#prof-image').css("background-image");
	if (logoImageUrl == undefined || logoImageUrl == "none") {
		return;
	}
	if ($('#header-user-info').find('.user-info-logo').length <= 0) {
		var userInfoDivider = $('<div>').attr({
			"class" : "float-left user-info-seperator"
		});
		var userInfoLogo = $('<div>').attr({
			"class" : "float-left user-info-logo"
		}).css({
			"background" : logoImageUrl + " no-repeat center",
			"background-size" : "100% auto"
		});
		$('#header-user-info').append(userInfoDivider).append(userInfoLogo);
	} else {
		$('.user-info-logo').css("background-image", logoImageUrl);
	}
	adjustImage();
}

// Function to append an association
function addAnAssociation() {
	if ($('#association-container > input').length <= 0) {
		$('#association-container').empty();
	}
	var newAssociation = $('<input>').attr({
		"class" : "lp-assoc-row lp-row clearfix prof-edditable-sin",
		"placeholder" : "New Associaion"
	});
	$('#association-container').append(newAssociation);
	newAssociation.focus();
}

// Function to append an achievement
function addAnAchievement() {
	if ($('#achievement-container > input').length <= 0) {
		$('#achievement-container').empty();
	}
	var newAchievement = $('<input>').attr({
		"class" : "lp-ach-row lp-row clearfix prof-edditable-sin",
		"placeholder" : "New Achievement"
	});
	$('#achievement-container').append(newAchievement);
	newAchievement.focus();
}

function addAuthorisedIn() {
	if ($('#authorised-in-container > input').length <= 0) {
		$('#authorised-in-container').empty();
	}
	var newAuthorisation = $('<input>').attr({
		"class" : "lp-auth-row lp-row clearfix prof-edditable-sin",
		"placeholder" : "Authorized in"
	});
	$('#authorised-in-container').append(newAuthorisation);
	newAuthorisation.focus();
}

$(document).on('keyup', '#association-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateAssociations();
		}, 0);
		return;
	}
	delay(function() {
		updateAssociations();
	}, 3000);
});

$(document).on('blur', '#association-container input', function() {
	delay(function() {
		updateAssociations();
	}, 0);
});

// Function to update association list
function updateAssociations() {
	var associationList = [];
	$('#association-container').children('input').each(function() {
		if (this.value != "") {
			var asssociation = {};
			asssociation.name = this.value;
			associationList.push(asssociation);
		}
	});
	associationList = JSON.stringify(associationList);
	var payload = {
		"associationList" : associationList
	};
	callAjaxPostWithPayloadData("./updateassociations.do",
			callBackUpdateAssociations, payload);
}

function callBackUpdateAssociations(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showAssociationList();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

$(document).on('keyup', '#achievement-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateAchievements();
		}, 0);
		return;
	}
	delay(function() {
		updateAchievements();
	}, 3000);
});

$(document).on('blur', '#achievement-container input', function() {
	delay(function() {
		updateAchievements();
	}, 0);
});

// Function to update achievement list
function updateAchievements() {
	var achievementList = [];
	$('#achievement-container').children('input').each(function() {
		if (this.value != "") {
			var achievement = {};
			achievement.achievement = this.value;
			achievementList.push(achievement);
		}
	});
	achievementList = JSON.stringify(achievementList);
	var payload = {
		"achievementList" : achievementList
	};
	callAjaxPostWithPayloadData("./updateachievements.do",
			callBackUpdateAchievements, payload);
}

function callBackUpdateAchievements(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showAchievementList();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

$(document).on('keyup', '#authorised-in-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateLicenseAuthorizations();
		}, 0);
		return;
	}
	delay(function() {
		updateLicenseAuthorizations();
	}, 3000);
});

$(document).on('blur', '#authorised-in-container input', function() {
	delay(function() {
		updateLicenseAuthorizations();
	}, 0);
});

// Function to update License authorizations
function updateLicenseAuthorizations() {
	var licenceList = [];
	$('#authorised-in-container').children('input').each(function() {
		if (this.value != "") {
			var licence = this.value;
			licenceList.push(licence);
		}
	});
	licenceList = JSON.stringify(licenceList);
	var payload = {
		"licenceList" : licenceList
	};
	callAjaxPostWithPayloadData("./updatelicenses.do",
			callBackUpdateLicenseAuthorizations, payload);
}

function callBackUpdateLicenseAuthorizations(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showLicenceList();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

$(document).on('keyup', '#prof-name-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateAddressDetails();
		}, 0);
		return;
	}
	delay(function() {
		updateAddressDetails();
	}, 3000);
});

$(document).on('blur', '#prof-name-container input', function() {
	delay(function() {
		updateAddressDetails();
	}, 0);
});

// Function to update addresses
function updateAddressDetails() {
	var profName = $('#prof-name').val();
	var profAddress1 = $('#prof-address1').val();
	var profAddress2 = $('#prof-address2').val();
	var payload = {
		"profName" : profName,
		"address1" : profAddress1,
		"address2" : profAddress2
	};
	callAjaxPostWithPayloadData("./updateprofileaddress.do",
			callBackUpdateAddressDetails, payload);
}

function callBackUpdateAddressDetails(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showAddressDetails();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

// Function to update logo image
function updateLogoImage(payload) {
	callAjaxPOSTWithTextData("./addoruploadlogo.do", callBackOnLogoUpload,
			false, payload);
}

function callBackOnLogoUpload(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showProfileImage();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

$(document).on('keyup', '#contant-info-container input[data-email]',
		function(e) {
			if (e.which == 13) {
				delay(function() {
					updateEmailIdsInContactDetails();
				}, 0);
				return;
			}
			delay(function() {
				updateEmailIdsInContactDetails();
			}, 3000);

		});

$(document).on('blur', '#contant-info-container input[data-email]', function() {
	delay(function() {
		updateEmailIdsInContactDetails();
	}, 0);
});

// Function to update email id's in contact details
function updateEmailIdsInContactDetails() {
	var mailIds = [];
	$('#contant-info-container input[data-email]').each(function() {
		if (this.value != "") {
			var mailId = {};
			mailId.key = $(this).attr("data-email");
			mailId.value = this.value;
			mailIds.push(mailId);
		}
	});
	mailIds = JSON.stringify(mailIds);
	var payload = {
		"mailIds" : mailIds
	};
	callAjaxPostWithPayloadData("./updateemailids.do", callBackOnUpdateMailIds,
			payload);
}

function callBackOnUpdateMailIds(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showContactDetails();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

$(document).on('keyup', '#contant-info-container input[data-phone-number]',
		function(e) {
			if (e.which == 13) {
				delay(function() {
					updatePhoneNumbersInContactDetails();
				}, 0);
				return;
			}

			delay(function() {
				updatePhoneNumbersInContactDetails();
			}, 3000);

		});

$(document).on('blur', '#contant-info-container input[data-phone-number]',
		function() {
			delay(function() {
				updatePhoneNumbersInContactDetails();
			}, 0);
		});

// Function to update phone numbers in contact details
function updatePhoneNumbersInContactDetails() {
	var phoneNumbers = [];
	$('#contant-info-container input[data-phone-number]').each(function() {
		if (this.value != "") {
			var phoneNumber = {};
			phoneNumber.key = $(this).attr("data-phone-number");
			phoneNumber.value = this.value;
			phoneNumbers.push(phoneNumber);
		}
	});
	phoneNumbers = JSON.stringify(phoneNumbers);
	var payload = {
		"phoneNumbers" : phoneNumbers
	};
	callAjaxPostWithPayloadData("./updatephonenumbers.do",
			callBackOnUpdatePhoneNumbers, payload);
}

function callBackOnUpdatePhoneNumbers(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showContactDetails();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

$(document).on('keyup', '#contant-info-container input[data-web-address]',
		function(e) {
			if (e.which == 13) {
				delay(function() {
					updateWebAddressesInContactDetails();
				}, 0);
				return;
			}

			delay(function() {
				updateWebAddressesInContactDetails();
			}, 3000);

		});

$(document).on('blur', '#contant-info-container input[data-web-address]',
		function() {
			delay(function() {
				updateWebAddressesInContactDetails();
			}, 0);
		});

// Function to update web addresses in contact details
function updateWebAddressesInContactDetails() {
	var webAddresses = [];
	$('#contant-info-container input[data-web-address]').each(function() {
		if (this.value != "") {
			var webAddress = {};
			webAddress.key = $(this).attr("data-web-address");
			webAddress.value = this.value;
			webAddresses.push(webAddress);
		}
	});
	webAddresses = JSON.stringify(webAddresses);
	var payload = {
		"webAddresses" : webAddresses
	};
	callAjaxPostWithPayloadData("./updatewebaddresses.do",
			callBackOnUpdateWebAddresses, payload);
}

function callBackOnUpdateWebAddresses(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		showContactDetails();
	}else{
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}

$(document).on('click', '#intro-body-text', function() {
	$(this).hide();
	var textContent = $(this).text();
	$('#intro-body-text-edit').val(textContent);
	$('#intro-body-text-edit').show();
});

$(document).on('keyup', '#intro-body-text-edit', function() {
	var textContent = $('#intro-body-text').text();
	if (textContent == undefined || textContent == "") {
		return;
	}
	delay(function() {
		editAdboutMeDetails();
	}, 3000);
});

function editAdboutMeDetails() {
	var aboutMe = $('#intro-body-text-edit').val().trim();
	var payload = {
		"aboutMe" : aboutMe
	};
	callAjaxPostWithPayloadData("./addorupdateaboutme.do",
			callBackOnEditAdboutMeDetails, payload);
}

function callBackOnEditAdboutMeDetails(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		$('#intro-body-text-edit').hide();
		var textContent = $('#intro-body-text-edit').val().trim();
		$('#intro-body-text').text(textContent);
		$('#intro-body-text').show();
	}else{
		$('#intro-body-text-edit').hide();
		$('#intro-body-text').show();
		createPopupInfo("Error!",$('#prof-message-header #display-msg-div p').text());
	}
}