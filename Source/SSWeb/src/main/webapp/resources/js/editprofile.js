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
	if($('#' + lockId).attr('data-state') == 'unlocked') {
		$(this).addClass('prof-name-edit');
	}
});

$(document).on('blur', '.prof-edditable', function() {
	var lockId = $(this).attr("id") + "-lock";
	if($('#' + lockId).attr('data-state') == 'unlocked') {
		$(this).removeClass('prof-name-edit');
	}
});

$(document).on('focus', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if($('#' + lockId).attr('data-state') == 'unlocked') {
		$(this).addClass('prof-name-edit');
	}
});

$(document).on('blur', '.prof-edditable-sin', function() {
	var lockId = $(this).attr("id") + "-lock";
	if($('#' + lockId).attr('data-state') == 'unlocked') {
		$(this).removeClass('prof-name-edit');
	}
});


// TODO Lock Settings
$('.lp-edit-locks').click(function() {
	var id = $(this).attr("id");

	if($(this).hasClass('lp-edit-locks-locked')) {
		$(this).removeClass('lp-edit-locks-locked');
		$(this).attr('data-state', 'unlocked');
		updateLockSettings(id, false);

	} else {
		$(this).addClass('lp-edit-locks-locked');
		$(this).attr('data-state', 'locked');
		updateLockSettings(id, true);
	}
});

$('.prof-img-lock-item').click(function() {
	var id = $(this).attr("id");

	if($(this).hasClass('prof-img-lock-locked')) {
		$(this).removeClass('prof-img-lock-locked');
		$(this).attr('data-state', 'unlocked');
		updateLockSettings(id, false);

	} else {
		$(this).addClass('prof-img-lock-locked');
		$(this).attr('data-state', 'locked');
		updateLockSettings(id, true);
	}
});

function updateLockSettings(id, state) {
	if (id == undefined || id == "") {
		return;
	}
	delay(function() {
		var payload = {
			"id" : id,
			"state" : state
		};
		callAjaxPostWithPayloadData("./updatelocksettings.do", callBackUpdateLock, payload);
	}, 0);
}

function callBackUpdateLock () {
	console.log("updated");
}

// TODO Function to call when the company profile page is loaded
function startCompanyProfilePage() {
	// showAssociationList();
	// showAchievementList();
	// showLicenceList();
}


// TODO Update AboutMe details
function callBackShowAboutMe(data) {
	$('#intro-about-me').html(data);
	adjustImage();
}

$(document).on('click', '#intro-body-text', function() {
	if ($('#aboutme-lock').attr('data-state') == 'unlocked') {
		$(this).hide();
		var textContent = $(this).text().trim();
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


// TODO Update Contact details
function callBackShowContactDetails(data) {
	// $('#contant-info-container').html(data);
	adjustImage();
}

// Phone numbers in contact details
$(document).on('blur', '#contant-info-container input[data-phone-number]', function() {
	delay(function() {
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
		callAjaxPostWithPayloadData("./updatephonenumbers.do", callBackOnUpdatePhoneNumbers, payload);
	}, 0);
});

function callBackOnUpdatePhoneNumbers(data) {
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('error-message')) {
		callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);
	}

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// Function to update web addresses in contact details
$(document).on('blur', '#contant-info-container input[data-web-address]', function() {
	delay(function() {
		var webAddresses = [];
		var i = 0;
		var webAddressValid = true;
		$('#contant-info-container input[data-web-address]').each(function() {
			var link = $.trim(this.value);
			console.log(link);
			if (link != "") {
				if (isValidUrl(link)) {
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
	$('#prof-message-header').html(data);
	if ($('#prof-message-header #display-msg-div').hasClass('error-message')) {
		callAjaxGET("./fetchcontactdetails.do", callBackShowContactDetails);
	}

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}

// TODO Update Address detail
function callBackShowAddressDetails(data) {
	$('#prof-address-container').html(data);
	adjustImage();
}

$(document).on('blur', '#prof-address-container input', function() {
	delay(function() {
		var profName = $('#prof-name').val().trim();
		var profAddress1 = $('#prof-address1').val().trim();
		var profAddress2 = $('#prof-address2').val().trim();
		var country = $('#prof-country').val().trim();
		var zipCode = $('#prof-zipcode').val().trim();
		var payload = {
			"profName" : profName,
			"address1" : profAddress1,
			"address2" : profAddress2,
			"country" : country,
			"zipCode" : zipCode
		};
		callAjaxPostWithPayloadData("./updateprofileaddress.do", callBackUpdateAddressDetails, payload);
	}, 0);
});

function callBackUpdateAddressDetails(data) {
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchbasicdetails.do", callBackShowBasicDetails);
	callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}


// TODO Update Basic detail
function callBackShowBasicDetails(response) {
	$('#prof-basic-container').html(response);
	adjustImage();
}

$(document).on('blur', '#prof-basic-container input', function() {
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
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchbasicdetails.do", callBackShowBasicDetails);
	callAjaxGET("./fetchaddressdetails.do", callBackShowAddressDetails);

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}


// TODO Function to update profile image
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

/*$(document).on('click', '#prof-image-upload', function() {
	$('#prof-image-edit').click();
});*/

$(document).on('change', '#prof-image-edit', function() {
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


//TODO Function to update profile logo image
function callBackShowProfileLogo(data) {
	$('#prof-logo-container').html(data);
	var logoImageUrl = $('#prof-logo').css("background-image");
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

/*$(document).on('click', '#prof-logo-upload', function() {
	$('#prof-logo-edit').click();
});*/

$(document).on('change', '#prof-logo-edit', function() {
	var formData = new FormData();
	formData.append("logo", $(this).prop("files")[0]);
	formData.append("logoFileName", $(this).prop("files")[0].name);
	callAjaxPOSTWithTextData("./updatelogo.do", callBackOnLogoUpload, false, formData);
});

function callBackOnLogoUpload(data) {
	$('#prof-message-header').html(data);
	callAjaxGET("./fetchprofilelogo.do", callBackShowProfileImage);
	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}


// TODO Function to populate associations container
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
	var temp = '<input class="float-left lp-ach-item-txt lp-ach-row lp-row clearfix prof-edditable-sin" value="temp"><div class="float-left lp-ach-item-img"></div>';
	$('#achievement-container').append(temp);
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

/*$(document).on('keyup', '#association-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateAssociations();
		}, 0);
		return;
	}
	delay(function() {
		updateAssociations();
	}, 3000);
});*/

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

/*$(document).on('keyup', '#achievement-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateAchievements();
		}, 0);
		return;
	}
	delay(function() {
		updateAchievements();
	}, 3000);
});*/

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

/*$(document).on('keyup', '#authorised-in-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateLicenseAuthorizations();
		}, 0);
		return;
	}
	delay(function() {
		updateLicenseAuthorizations();
	}, 3000);
});*/

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


// Update Social links
$('body').on('click','#prof-edit-social-link .icn-fb',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add facebook link",
		"value" : link,
		"onblur" : "updateFacebookLink(this.value);"
	});
});

$('body').on('click','#prof-edit-social-link .icn-twit',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Twitter link",
		"value" : link,
		"onblur" : "updateTwitterLink(this.value);"
	});
});

$('body').on('click','#prof-edit-social-link .icn-lin',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add LinkedIn link",
		"value" : link,
		"onblur" : "updateLinkedInLink(this.value);"
	});
});

$('body').on('click','#prof-edit-social-link .icn-yelp',function(){
	$('#social-token-text').show();
	var link = $(this).attr("data-link");
	$('#social-token-text').attr({
		"placeholder" : "Add Yelp link",
		"value" : link,
		"onblur" : "updateYelpLink(this.value);"
	});
});

function updateFacebookLink(link) {
	var payload = {
		"fblink" : link	
	};
	if(isValidUrl(link)){
        callAjaxPostWithPayloadData("./updatefacebooklink.do", callBackUpdateFacebookLink, payload);
	}else{
		alert("enter a valid url");
	}
}

function callBackUpdateFacebookLink(data) {
	$('#prof-message-header').html(data);
	showProfileSocialLinks();
}

function updateTwitterLink(link) {
	var payload = {
		"twitterlink" : link	
	};
	if(isValidUrl(link)){
        	callAjaxPostWithPayloadData("./updatetwitterlink.do", callBackUpdateTwitterLink, payload);
	}else{
		alert("enter a valid url");
	}
}

function callBackUpdateTwitterLink(data) {
	$('#prof-message-header').html(data);
	showProfileSocialLinks();
}

function updateLinkedInLink(link) {
	var payload = {
		"linkedinlink" : link	
	};
	if(isValidUrl(link)){
		callAjaxPostWithPayloadData("./updatelinkedinlink.do", callBackUpdateLinkedInLink, payload);
	}else{
		alert("enter a valid url");
	}
}

function callBackUpdateLinkedInLink(data) {
	$('#prof-message-header').html(data);
	showProfileSocialLinks();
}

function updateYelpLink(link) {
	var payload = {
		"yelplink" : link	
	};
	if(isValidUrl(link)){
		callAjaxPostWithPayloadData("./updateyelplink.do", callBackUpdateYelpLink, payload);
	}else{
		alert("enter a valid url");
	}
}

function callBackUpdateYelpLink(data) {
	$('#prof-message-header').html(data);
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
    /*var address1 = $('#prof-address1').val().trim();
	var address2 = $('#prof-address2').val().trim();*/
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