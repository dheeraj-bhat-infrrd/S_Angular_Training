var webAddressRegEx = /[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/;
var timer = 0;
var delay = (function() {
	return function(callback, ms) {
		clearTimeout(timer);
		timer = setTimeout(callback, ms);
	};
})();

// Toggle text editor
$(document).on('focus', '.prof-edditable', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user' && $('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]')) {
		$(this).addClass('prof-name-edit');
		$('#prof-all-lock').val('modified');
	}
});

$(document).on('blur', '.prof-edditable', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user' && $('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]')) {
		$(this).removeClass('prof-name-edit');
	}
});

$(document).on('focus', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user' && $('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]')) {
		$(this).addClass('prof-name-edit');
		$('#prof-all-lock').val('modified');
	}
});

$(document).on('blur', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if ($('#' + lockId).attr('data-control') == 'user' && $('#' + lockId).attr('data-state') == 'unlocked' && !$(this).is('[readonly]')) {
		$(this).removeClass('prof-name-edit');
	}
});


// Lock Settings
$(document).on('click', '.lp-edit-locks', function(e) {
	e.stopImmediatePropagation();
	var lockId = $(this).attr("id");
	var fieldId = lockId.substr(0, lockId.lastIndexOf("-lock"));

	if ($(this).attr('data-control') == 'user') {
		if($(this).hasClass('lp-edit-locks-locked')) {
			$(this).removeClass('lp-edit-locks-locked');
			$(this).attr('data-state', 'unlocked');
			updateLockSettings(lockId, false);
			$("#" + fieldId).attr("readonly", false);
			
		} else {
			$(this).addClass('lp-edit-locks-locked');
			$(this).attr('data-state', 'locked');
			updateLockSettings(lockId, true);
			$("#" + fieldId).attr("readonly", true);
		}
	} else {
		$('#overlay-toast').html("Settings locked by Admin");
		showToast();
	}
});

$(document).on('click', '.prof-img-lock-item', function(e) {
	e.stopImmediatePropagation();
	var lockId = $(this).attr("id");
	var fieldId = lockId.substr(0, lockId.lastIndexOf("-lock"));

	if ($(this).attr('data-control') == 'user') {
		if($(this).hasClass('prof-img-lock-locked')) {
			$(this).removeClass('prof-img-lock-locked');
			$(this).attr('data-state', 'unlocked');
			updateLockSettings(lockId, false);
			$("#" + fieldId).attr("disabled", false);

		} else {
			$(this).addClass('prof-img-lock-locked');
			$(this).attr('data-state', 'locked');
			updateLockSettings(lockId, true);
			$("#" + fieldId).attr("disabled", true);
		}
	} else {
		$('#overlay-toast').html("Settings locked by Admin");
		showToast();
	}
});

function updateLockSettings(id, state) {
	if (id == undefined || id == "") {
		return;
	}
	
	console.log(id + state);
	delay(function() {
		var payload = {
			"id" : id,
			"state" : state
		};
		callAjaxPostWithPayloadData("./updatelocksettings.do", callBackUpdateLock, payload);
	}, 0);
}

function callBackUpdateLock () {
	showMainContent('./showprofilepage.do');
}


// Update AboutMe details
function callBackShowAboutMe(data) {
	$('#intro-about-me').html(data);
	adjustImage();
}

$(document).on('click', '#intro-body-text', function() {
	if ($('#aboutme-lock').attr('data-state') == 'unlocked') {
		$(this).hide();
		var textContent = $(this).text().trim();
		if ($('#aboutme-status').val() == 'new') {
			textContent = "";
		}
		$('#intro-body-text-edit').val(textContent);
		$('#intro-body-text-edit').show();
	}
});

$(document).on('blur', '#intro-body-text-edit', function() {
	if ($('#aboutme-lock').attr('data-state') == 'unlocked') {
		
		var aboutMe = $('#intro-body-text-edit').val().trim();
		if (aboutMe == undefined || aboutMe == "") {
			return;
		}
		delay(function() {
			var payload = {
				"aboutMe" : aboutMe
			};
			callAjaxPostWithPayloadData("./addorupdateaboutme.do", callBackOnEditAdboutMeDetails, payload);
		}, 0);
	}
});

function callBackOnEditAdboutMeDetails(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('success-message')) {
		$('#intro-body-text-edit').hide();
		var textContent = $('#intro-body-text-edit').val().trim();
		$('#intro-body-text').text(textContent);
		$('#intro-body-text').show();
	}
	else {
		$('#intro-body-text-edit').hide();
		$('#intro-body-text').show();
	}

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}


// Update Contact details
function callBackShowContactDetails(data) {
	$('#contant-info-container').html(data);
	adjustImage();
}

// Phone numbers in contact details
$(document).on('blur', '#contant-info-container input[data-phone-number]', function() {
	if ($('#prof-all-lock').val() != 'modified' || !$(this).val() || !phoneRegex.test(this.value) || $(this).is('[readonly]')) {
		return;
	}

	delay(function() {
		var phoneNumbers = [];
		$('#contant-info-container input[data-phone-number]').each(function() {
			if (this.value != "" && phoneRegex.test(this.value) && !$(this).is('[readonly]')) {
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
		callAjaxPostWithPayloadData("./updatephonenumbers.do", callBackOnUpdatePhoneNumbers, payload);
	}, 0);
});

function callBackOnUpdatePhoneNumbers(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Function to update web addresses in contact details
$(document).on('blur', '#contant-info-container input[data-web-address]', function() {
	if ($('#prof-all-lock').val() != 'modified' || !$(this).val() || !isValidUrl($(this).val().trim()) || $(this).is('[readonly]')) {
		return;
	}

	delay(function() {
		var webAddresses = [];
		var i = 0;
		var webAddressValid = true;
		$('#contant-info-container input[data-web-address]').each(function() {
			var link = $.trim(this.value);
			if (link != "") {
				if (isValidUrl(link) && !$(this).is('[readonly]')) {
					var webAddress = {};
					webAddress.key = $(this).attr("data-web-address");
					webAddress.value = link;
					webAddresses[i++] = webAddress;
				} else {
					return;
					$(this).focus();
					webAddressValid = false;
				}
			}
		});
		if (!webAddressValid) {
			alert("Invalid web address");
			return false;
		}
		webAddresses = JSON.stringify(webAddresses);
		console.log(webAddresses);
		var payload = {
			"webAddresses" : webAddresses
		};
		callAjaxPostWithPayloadData("./updatewebaddresses.do", callBackOnUpdateWebAddresses, payload);
	}, 0);
});

function callBackOnUpdateWebAddresses(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Update Address detail
function callBackShowAddressDetails(data) {
	$('#prof-address-container').html(data);
	adjustImage();
}

$(document).on('click', '#prof-address-container', function() {
	callAjaxGET("./fetchaddressdetailsedit.do", callBackEditAddressDetails);
});

function callBackEditAddressDetails(data) {
	var header = "Edit Address Detail";
	createPopupConfirm(header, data);
}

$(document).on('click', '#overlay-continue', function() {
	var profName = $('#prof-name').val();
	var profAddress1 = $('#prof-address1').val();
	var profAddress2 = $('#prof-address2').val();
	var country = $('#prof-country').val();
	var zipCode = $('#prof-zipcode').val();
	if (!profName || !profAddress1 || !country || !zipCode) {
		return;
	}
	
	delay(function() {
		var payload = {
			"profName" : profName,
			"address1" : profAddress1,
			"address2" : profAddress2,
			"country" : country,
			"zipCode" : zipCode
		};
		callAjaxPostWithPayloadData("./updateprofileaddress.do", callBackUpdateAddressDetails, payload);
	}, 0);

	$('#overlay-continue').unbind('click');
});

function callBackUpdateAddressDetails(data) {
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchbasicdetails.do", callBackShowBasicDetails);
	callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	overlayRevert();
}

$('#overlay-cancel').click(function(){
	$('#overlay-continue').unbind('click');
	overlayRevert();
	$('#othercategory').val('');
});

function createPopupConfirm(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-text').html(body);
	$('#overlay-continue').html("Ok");
	$('#overlay-cancel').html("Cancel");

	$('#overlay-main').show();
	$('#overlay-continue').unbind('click');
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


// Update Basic detail
function callBackShowBasicDetails(response) {
	$('#prof-basic-container').html(response);
	adjustImage();
}

$(document).on('blur', '#prof-basic-container input', function() {
	if ($('#prof-all-lock').val() != 'modified' || !$(this).val()) {
		return;
	}

	delay(function() {
		var profName = $('#prof-name').val().trim();
		var profTitle = $('#prof-title').val().trim();
		var payload = {
			"profName" : profName,
			"profTitle" : profTitle
		};
		callAjaxPostWithPayloadData("./updatebasicprofile.do", callBackUpdateBasicDetails, payload);
	}, 0);
});

function callBackUpdateBasicDetails(data) {
	$('#prof-all-lock').val('locked');
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchbasicdetails.do", callBackShowBasicDetails);
	callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}


// Function to update profile image
function callBackShowProfileImage(data) {
	$('#prof-img-container').html(data);
	var profileImageUrl = $('#prof-image-edit').css("background-image");
	if (profileImageUrl == undefined || profileImageUrl == "none") {
		return;
	}
	adjustImage();
}

$(document).on('change', '#prof-image', function() {
	var formData = new FormData();
	formData.append("logo", $(this).prop("files")[0]);
	formData.append("logoFileName", $(this).prop("files")[0].name);
	callAjaxPOSTWithTextData("./updateprofileimage.do", callBackOnProfileImageUpload, false, formData);
});

function callBackOnProfileImageUpload(data) {
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchprofileimage.do", callBackShowProfileImage);
	
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}


// Function to update profile logo image
function callBackShowProfileLogo(data) {
	$('#prof-logo-container').html(data);
	var logoImageUrl = $('#prof-logo-edit').css("background-image");
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

$(document).on('change', '#prof-logo', function() {
	var formData = new FormData();
	formData.append("logo", $(this).prop("files")[0]);
	formData.append("logoFileName", $(this).prop("files")[0].name);
	callAjaxPOSTWithTextData("./updatelogo.do", callBackOnLogoUpload, false, formData);
});

function callBackOnLogoUpload(data) {
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchprofilelogo.do", callBackShowProfileLogo);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
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


//Function to show social media links
function showProfileSocialLinks() {
	$('#social-token-text').hide();
	callAjaxGET("./fetchprofilesociallinks.do", callBackShowProfileSocialLinks);
}

function callBackShowProfileSocialLinks(data) {
	$('#prof-edit-social-link').html(data);
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

	var newAssociationButton = $('<div>').attr({
		"class" : "float-left lp-ach-item-img",
	});
	$('#association-container').append(newAssociationButton);

	newAssociation.focus();
}

// Function to append an achievement
$(document).on('click', '.lp-ach-item-img', function() {
	$(this).prev().remove();
	$(this).remove();
});

function addAnAchievement() {
	if ($('#achievement-container > input').length <= 0) {
		$('#achievement-container').empty();
	}
	var newAchievement = $('<input>').attr({
		"class" : "lp-ach-row lp-row clearfix prof-edditable-sin",
		"placeholder" : "New Achievement"
	});
	$('#achievement-container').append(newAchievement);

	var newAchievementButton = $('<div>').attr({
		"class" : "float-left lp-ach-item-img",
	});
	$('#achievement-container').append(newAchievementButton);

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

	var newAuthorizationButton = $('<div>').attr({
		"class" : "float-left lp-ach-item-img",
	});
	$('#authorised-in-container').append(newAuthorizationButton);

	newAuthorisation.focus();
}

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
		} else {
			return;
		}
	});
	associationList = JSON.stringify(associationList);
	var payload = {
		"associationList" : associationList
	};
	callAjaxPostWithPayloadData("./updateassociations.do", callBackUpdateAssociations, payload);
}

function callBackUpdateAssociations(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	showAssociationList();
}

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
		} else {
			return;
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
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	showAchievementList();
}

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
		} else {
			return;
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
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	showLicenceList();
}


// Update Social links
$('body').on('click','#prof-edit-social-link .icn-fb',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add facebook link",
		"value" : link,
		"onblur" : "updateFacebookLink(this.value);$('#social-token-text').hide();"
	});
});

$('body').on('click','#prof-edit-social-link .icn-twit',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Twitter link",
		"value" : link,
		"onblur" : "updateTwitterLink(this.value);$('#social-token-text').hide();"
	});
});

$('body').on('click','#prof-edit-social-link .icn-lin',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add LinkedIn link",
		"value" : link,
		"onblur" : "updateLinkedInLink(this.value);$('#social-token-text').hide();"
	});
});

$('body').on('click','#prof-edit-social-link .icn-yelp',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Yelp link",
		"value" : link,
		"onblur" : "updateYelpLink(this.value);$('#social-token-text').hide();"
	});
});

function updateFacebookLink(link) {
	var payload = {
		"fblink" : link	
	};
	if(isValidUrl(link)){
        callAjaxPostWithPayloadData("./updatefacebooklink.do", callBackUpdateFacebookLink, payload);
	}else{
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

function callBackUpdateFacebookLink(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	showProfileSocialLinks();
}

function updateTwitterLink(link) {
	var payload = {
		"twitterlink" : link	
	};
	if(isValidUrl(link)){
        	callAjaxPostWithPayloadData("./updatetwitterlink.do", callBackUpdateTwitterLink, payload);
	}else{
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

function callBackUpdateTwitterLink(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	showProfileSocialLinks();
}

function updateLinkedInLink(link) {
	var payload = {
		"linkedinlink" : link	
	};
	if(isValidUrl(link)){
		callAjaxPostWithPayloadData("./updatelinkedinlink.do", callBackUpdateLinkedInLink, payload);
	}else{
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

function callBackUpdateLinkedInLink(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	showProfileSocialLinks();
}

function updateYelpLink(link) {
	var payload = {
		"yelplink" : link	
	};
	if(isValidUrl(link)){
		callAjaxPostWithPayloadData("./updateyelplink.do", callBackUpdateYelpLink, payload);
	}else{
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
}

function callBackUpdateYelpLink(data) {
	$('#prof-message-header').html(data);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();

	showProfileSocialLinks();
}



function isValidUrl(url){
	var myVariable = url;
	if(/^(http|https|ftp):\/\/[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/i.test(myVariable)) {
		return true;
	} else {
		return false;
	}  
}

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
	}
	else {
		$('.prof-name-container,#prof-image').height(200);
		var rowW = $('.lp-con-row').width() - 50 - 50; // left image-50; right-locks-50
		$('.lp-con-row-item').width(rowW + 'px');
		// $('.lp-con-row-item').width('auto');
		$('.footer-main-wrapper').show();
	}
}

// Function to show map on the screen
function initializeGoogleMap() {
    var mapCanvas = document.getElementById('map-canvas');
    var geocoder = new google.maps.Geocoder();
	var address = "Raremile technologies,HSR layout,bangalore, 560102";
	var latitude = 45;
	var longitude = -73;
	geocoder.geocode({
		'address' : address
	}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			latitude = results[0].geometry.location.lat();
			longitude = results[0].geometry.location.lng();
			var mapOptions = {
				      center: new google.maps.LatLng(latitude, longitude),
				      zoom: 15,
				      mapTypeId: google.maps.MapTypeId.ROADMAP
				    };
			
			map = new google.maps.Map(mapCanvas, mapOptions);
	        map.setCenter(results[0].geometry.location);
	        marker = new google.maps.Marker({
	            position: results[0].geometry.location,
	            map: map,
	            title: "RM"
	        });
		}
	});
}


// TODO Other Data
function paintForProfile() {
	var companyId = $('#prof-company-id').val();
	var regionId = $('#prof-region-id').val();
	var branchId = $('#prof-branch-id').val();
	var agentId = $('#prof-agent-id').val();
	minScore = $('#profile-min-post-score').val();
	currentProfileIden = companyId;
	
	if (companyId != "") {
		fetchAverageRatings(companyId);

		fetchReviewsCountForCompany(companyId, paintAllReviewsCount);
		$("#profile-fetch-info").attr("fetch-all-reviews", "false");
		if(minScore > 0){
			fetchReviewsCountForCompany(companyId, paintHiddenReviewsCount, minScore);
		}
		
		fetchReviewsForCompany(companyId, startIndex, numOfRows, minScore);
		
		fetchCompanyRegions();
		fetchCompanyBranches();
		fetchCompanyIndividuals();
	}
}