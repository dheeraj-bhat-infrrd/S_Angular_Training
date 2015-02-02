/**
 * JavaScript file for company settings page
 */
var webAddressRegEx = /[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/;
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

//Function to show map on the screen
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
			
			//create map
			map = new google.maps.Map(mapCanvas, mapOptions);

			//center map
	        map.setCenter(results[0].geometry.location);
	        
	        //create marker
	        marker = new google.maps.Marker({
	            position: results[0].geometry.location,
	            map: map,
	            title: "RM"
	        });
		}
	});
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
	showProfileSocialLinks();
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
	initializeGoogleMap();
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

/*$(document).on('keyup', '#prof-name-container input', function(e) {
	if (e.which == 13) {
		delay(function() {
			updateAddressDetails();
		}, 0);
		return;
	}
	delay(function() {
		updateAddressDetails();
	}, 3000);
});*/

$(document).on('blur', '#prof-name-container input', function() {
	delay(function() {
		updateAddressDetails();
	}, 0);
});

// Function to update addresses
function updateAddressDetails() {
	var profName = $('#prof-name').val().trim();
	var profAddress1 = $('#prof-address1').val().trim();
	var profAddress2 = $('#prof-address2').val().trim();
	//var zipcode = profAddress2.substr(-5);
	//profAddress2 = profAddress2.substr(0,profAddress2.length-7);
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

/*$(document).on('keyup', '#contant-info-container input[data-email]',
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

		});*/

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

/*$(document).on('keyup', '#contant-info-container input[data-phone-number]',
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

		});*/

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

/*$(document).on('keyup', '#contant-info-container input[data-web-address]',
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

		});*/

$(document).on('blur', '#contant-info-container input[data-web-address]',
		function() {
			delay(function() {
				updateWebAddressesInContactDetails();
			}, 0);
		});

// Function to update web addresses in contact details
function updateWebAddressesInContactDetails() {
	var webAddresses = [];
	var i = 0;
	var webAddressValid = true;
	$('#contant-info-container input[data-web-address]').each(function() {
		var link = $.trim(this.value);
		if (link != "") {
			if(isValidUrl(link)){
					var webAddress = {};
					webAddress.key = $(this).attr("data-web-address");
					webAddress.value = link;
					webAddresses[i++] = webAddress;
			}else{
				return;
				$(this).focus();
				webAddressValid = false;
			}
				
		}
	});
	if(!webAddressValid){
		alert("Invalid web address");
		return false;
	}
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