var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/;
var zipcodeRegex = /^\d{5}([\-]?\d{4})?$/;
var phoneRegex = /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/;
var ausPhoneRegex = /^\+?[0-9]{0,2}[-\s\.]{0,1}[(]{0,1}[0-9]{4}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{3}$/;
var passwordRegex = /^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+=|<>?{}~-]).{6,15}$/;
var nameRegex = /^[a-zA-Z ]*$/;
var lastNameRegEx = /^[a-zA-Z0-9 ]*$/;
var companyNameRegEx = /^[a-zA-Z0-9\\/ ]*$/;
var numberRegEx = /^[1-9][0-9]*?$/;
var minPwdLength = 6;
var maxPwdLength = 15;
var firstNamePatternRegex = /^[a-zA-Z][a-zA-Z\s]{2,}$/;
var lastNamePatternRegEx = /^[a-zA-Z][a-zA-Z\s]{2,}$/;
var pageInitialized = false;
var currentPhoneRegEx; // Vary the phone regex according to masking
var stateList; // usStateList
var cityLookupList; // cityLookupList
var phoneFormat = '(ddd) ddd-dddd'; // defualt phone format
var selectedCountryRegEx = "";
var findProCompanyProfileName;

function getPhoneNumber(phoneId) {
	var countryData = $('#' + phoneId).intlTelInput("getSelectedCountryData");
	var number = $('#' + phoneId).intlTelInput("getNumber");
	if (number != "") {
		number = number.substring(countryData.dialCode.length + 1, number.length + 1);
		return {
			"number" : number,
			"countryCode" : "+" + countryData.dialCode,
			"extension" : $('#' + phoneId).intlTelInput("getExtension"),
			"countryAbbr" : countryData.iso2,
			"formattedPhoneNumber" : $('#' + phoneId).val()
		};
	}
}

function maskPhoneNumber(phoneId, iso2) {
	if (iso2 == 'us') {
		$('#' + phoneId).mask(phoneFormatWithExtension, usPhoneRegEx);
	} else {
		$('#' + phoneId).unmask(phoneFormat);
		$('#' + phoneId).keypress(function(e) {
			var count = $('#' + phoneId).val().length;
			if (count > 24) {
				return false;
			} else {
				var regex = new RegExp("^[0-9-.() ]+$");
				var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
				if (regex.test(str)) {
					return true;
				}
				e.preventDefault();
				return false;
			}
		});
	}
}

function buildMessageDiv() {
	if ($('.err-nw-wrapper').length == 0) {
		var errorDiv = $("<div id='err-nw-wrapper' class='err-nw-wrapper'>");
		var closeSpan = $('<span class="err-new-close">');
		var textSpan = $('<span id="err-nw-txt">');
		errorDiv.append(closeSpan);
		errorDiv.append(textSpan);
		$('.hm-header-main-wrapper').after(errorDiv);
	}
}

function buildMessageInvalidDiv() {
	if ($('.err-nw-wrapper-invalid').length == 0) {
		var errorDiv = $("<div id='err-nw-wrapper-invalid' class='err-nw-wrapper-invalid'>");
		var closeSpan = $('<span class="err-new-close-invalid">');
		var textSpan = $('<span id="err-nw-txt-invalid">');
		errorDiv.append(closeSpan);
		errorDiv.append(textSpan);
		$('.hm-header-main-wrapper').after(errorDiv);
	}
}
function buildMessageSuccessDiv() {
	if ($('.err-nw-wrapper-success').length == 0) {
		var errorDiv = $("<div id='err-nw-wrapper-success' class='err-nw-wrapper-success'>");
		var closeSpan = $('<span class="err-new-close-success">');
		var textSpan = $('<span id="err-nw-txt-success">');
		errorDiv.append(closeSpan);
		errorDiv.append(textSpan);
		$('.hm-header-main-wrapper').after(errorDiv);
	}

}
function showError(msg) {
	buildMessageDiv();
	$('#err-nw-txt').html(msg);
	$('#err-nw-wrapper').removeClass('bg-black-success');
	$('#err-nw-wrapper').slideDown(200);
	$(window).scrollTop($('#err-nw-wrapper').offset().top);
}

function showInvalidError(msg) {
	buildMessageInvalidDiv();
	$('#err-nw-txt-invalid').html(msg);
	$('#err-nw-wrapper-invalid').removeClass('bg-black-success');
	$('#err-nw-wrapper-invalid').slideDown(200);
	$(window).scrollTop($('#err-nw-wrapper-invalid').offset().top);
}
function showErrorSuccess(msg) {
	buildMessageSuccessDiv();
	$('#err-nw-txt-success').html(msg);
	$('#err-nw-wrapper-success').removeClass('bg-black-success');
	$('#err-nw-wrapper-success').slideDown(200);
	$(window).scrollTop($('#err-nw-wrapper-success').offset().top);
}
function hideError() {
	$('#err-nw-wrapper').slideUp(200);
}
function hideErrorInvalid() {
	$('#err-nw-wrapper-invalid').slideUp(200);
}
function hideErrorSuccess() {
	$('#err-nw-wrapper-success').slideUp(200);
}

function showInfo(msg) {
	buildMessageDiv();
	$('#err-nw-txt').html(msg);
	$('#err-nw-wrapper').slideDown(200);
	$(window).scrollTop($('#err-nw-wrapper').offset().top);
	$('#err-nw-wrapper').addClass('bg-black-success');
}
function showInfoSuccess(msg) {
	buildMessageSuccessDiv();
	$('#err-nw-txt-success').html(msg);
	$('#err-nw-wrapper-success').slideDown(200);
	$(window).scrollTop($('#err-nw-wrapper-success').offset().top);
	$('#err-nw-wrapper-success').addClass('bg-black-success');
}

function hideInfo() {
	$('#err-nw-wrapper').slideUp(200);
	setTimeout(function() {
		$('#err-nw-wrapper').removeClass('bg-black-success');
	}, 200);
}
function hideInfoInvalid() {
	$('#err-nw-wrapper-invalid').slideUp(200);
	setTimeout(function() {
		$('#err-nw-wrapper-invalid').removeClass('bg-black-success');
	}, 200);
}
function hideInfoSuccess() {
	$('#err-nw-wrapper-success').slideUp(200);
	setTimeout(function() {
		$('#err-nw-wrapper-success').removeClass('bg-black-success');
	}, 200);
}

$(document).on('click', '.err-new-close', function() {
	hideError();
	hideInfo();
});
$(document).on('click', '.err-new-close-invalid', function() {
	hideErrorInvalid();
	hideInfoInvalid();
});

$(document).on('click', '.err-new-close-success', function() {
	hideErrorSuccess();
	hideInfoSuccess();
});

function showRegErr(msg) {
	$('#reg-err-pu-msg').html(msg);
	$('#reg-err-pu').fadeIn();
}

function hideRegErr() {
	$('#reg-err-pu').fadeOut();
}
function showErrorMobileAndWeb(msg) {
	if ($(window).width() < 768) {
		$('#overlay-toast').html(msg);
		showToast();
	} else {
		showError(msg);
	}
}
function showErrorInvalidMobileAndWeb(msg) {
	if ($(window).width() < 768) {
		$('#overlay-toast').html(msg);
		showToast();
	} else {
		showInvalidError(msg);
	}
}
function showErrorSuccessMobileAndWeb(msg) {
	if ($(window).width() < 768) {
		$('#overlay-toast').html(msg);
		showToast();
	} else {
		showErrorSuccess(msg);
	}
}
function showInfoMobileAndWeb(msg) {
	if ($(window).width() < 768) {
		$('#overlay-toast').html(msg);
		showToast();
	} else {
		showInfo(msg);
	}
}
function showInfoSuccessMobileAndWeb(msg) {
	if ($(window).width() < 768) {
		$('#overlay-toast').html(msg);
		showToast();
	} else {
		showInfoSuccess(msg);
	}
}

function validateForm(id) {
	var validate = true;

	// hide the server error
	$("#serverSideerror").hide();

	$('#' + id).find('input').each(function() {

		if ($(this).data('non-empty') == true) {
			if ($(this).val() == "") {
				$(this).parent().addClass('input-error');
				$('#jsErrTxt').html('Please enter the required fields.');
				validate = false;
			} else {
				$(this).parent().removeClass('input-error');
			}
		}

		if ($(this).data('email') == true) {
			if ($(this).val() != "") {
				if (emailRegex.test($(this).val()) == true) {
					$(this).parent().removeClass('input-error');
				} else {
					validate = false;
					$('#jsErrTxt').html('Please enter a valid emailId.');
					$(this).parent().addClass('input-error');
				}
			}
		}

		if ($(this).data('zipcode') == true) {
			if ($(this).val() != "") {
				if (zipcodeRegex.test($(this).val()) == true) {
					$(this).parent().removeClass('input-error');
				} else {
					validate = false;
					$('#jsErrTxt').html('Please enter a valid zipcode.');
					$(this).parent().addClass('input-error');
				}
			}
		}

		if ($(this).data('phone') == true) {
			if ($(this).val() != "") {
				if (phoneRegex.test($(this).val()) == true || ausPhoneRegex.test($(this).val()) == true) {
					$(this).parent().removeClass('input-error');
				} else {
					validate = false;
					$('#jsErrTxt').html('Please enter a valid phone number.');
					$(this).parent().addClass('input-error');
				}
			}
		}
	});

	if (!validate) {
		$('#jsError').show();
		return false;
	} else {
		$('#jsError').hide();
		/* Form validated. */
		return true;
	}
}

function showOverlay() {
	$('.overlay-loader').show();
}

function hideOverlay() {
	$('.overlay-loader').hide();
}
function showDashOverlay(dashid) {
	$(dashid).show();
}

function hideDashOverlay(dashid) {
	$(dashid).hide();
}

// show Toast
function showToast() {
	$('#toast-container').fadeIn();
	setTimeout(function() {
		$('#toast-container').fadeOut();
	}, 3000);
}

// detectBrowser();

function detectBrowser() {
	if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1) {
		is_safari = true;
		$('.input-file-text').css('left', '-51px');
	} else if (is_firefox) {
		$('.input-file-text').css('left', '-44px');
	}
}

function moveFileBtn() {
	if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1) {
		$('#com-logo').css('left', ($('#input-file-icn-left').width() - 101) + 'px');
	} else if (is_firefox) {
		$('#com-logo').css('left', ($('#input-file-icn-left').width() - 97) + 'px');
	} else {
		$('#com-logo').css('left', ($('#input-file-icn-left').width() - 97) + 'px');
	}
}

$('#hm-item-dd-top').click(function() {
	$('#hm-dd-wrapper-top').slideToggle(200);
});

$('#hm-item-dd-bottom').click(function() {
	$('#hm-dd-wrapper-bottom').slideToggle(200);
});

$('#header-menu-icn').click(function() {
	// $('#header-links').slideToggle(200);
	$('#header-slider-wrapper').addClass('rt-panel-slide');
	disableBodyScroll();
});

$('#header-slider-wrapper').click(function() {
	$('#header-slider-wrapper').removeClass('rt-panel-slide');
	enableBodyScroll();
});

$('#header-links-slider, .header-slider').click(function(e) {
	e.stopPropagation();
});

$('.hm-header-dd-icn').click(function() {
	$(this).parent().next('.create-branch-dd').slideToggle();
});
$('.dd-icn-type2').click(function() {
	$(this).parent().parent().next('.create-branch-dd').slideToggle();
});

function showPayment() {
	$('.overlay-payment').show();
}

function hidePayment() {
	$('.overlay-payment').hide();
}

$('#icn-status-green').click(function() {
	$(this).hide();
	$('#icn-status-red').show();
});

$('#icn-status-red').click(function() {
	$(this).hide();
	$('#icn-status-green').show();
});

/**
 * Functionality to remove input error class on blur of any input text box
 */
$('.login-wrapper-txt').focus(function() {
	$(this).parent().removeClass('input-error');
});

// Function to validate email id in a form
function validateEmailId(elementId, isOnlyToast) {
	var emailId = $('#' + elementId).val();
	var message = 'Please enter a valid Email Address';
	if (emailId != "") {
		emailId = emailId.trim();
		if (emailRegex.test(emailId) == true) {
			return true;
		} else {
			if (isOnlyToast) {
				$('#overlay-toast').text(message);
				showToast();
			} else {
				showError(message);
			}
			return false;
		}
	} else {
		if (isOnlyToast) {
			$('#overlay-toast').text(message);
			showToast();
		} else {
			showError(message);
		}
		return false;
	}
}

// Function to validate the first name
function validateFirstName(elementId) {
	if ($('#' + elementId).val() != "") {
		if (nameRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			showError('Please enter a valid first name');
			return false;
		}
	} else {
		showError('Please enter a valid first name');
		return false;
	}
}

/**
 * Function to validate the last name
 */
function validateLastName(elementId) {

	if ($('#' + elementId).val() == "" || lastNameRegEx.test($('#' + elementId).val()) == true) {
		return true;
	} else {
		showError('Please enter a valid last name');
		return false;
	}

}

/**
 * Function to validate a password in form
 * 
 * @param elementId
 * @returns {Boolean}
 */
function validatePassword(elementId) {
	var password = $('#' + elementId).val();
	if (password.trim() == "") {
		showError("Please enter password");
		return false;
	} else if (password.length < minPwdLength) {
		showError('Password must be at least 6 characters');
		return false;
	}
	return true;
}
/**
 * Function to match password and confirm password
 * 
 * @param pwdId
 * @param confirmPwdId
 * @returns {Boolean}
 */
function validateConfirmPassword(pwdId, confirmPwdId) {
	if ($('#' + confirmPwdId).val() != "") {
		if ($('#' + pwdId).val() != $('#' + confirmPwdId).val()) {
			showError('Passwords do not match');
			return false;
		} else {
			return true;
		}
	} else {
		showError('Please enter confirm password');
		return false;
	}
}

/**
 * Function to validate company name
 */
function validateCompany(elementId) {
	if ($('#' + elementId).val() != "") {
		if ($('#' + elementId).val().indexOf("\"") == -1) {
			return true;
		} else {
			showErrorMobileAndWeb('Please enter a valid company name');
			return false;
		}
	} else {
		showErrorMobileAndWeb('Please enter company name');
		return false;
	}
}

// Function to validate the zipcode
function validateZipcode(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (zipcodeRegex.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid zipcode.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('Please enter zipcode.');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (zipcodeRegex.test($('#' + elementId).val()) == true) {
				$('#' + elementId).parent().next('.login-reg-err').hide();
				return true;
			} else {
				$('#' + elementId).parent().next('.login-reg-err').html('Please enter a valid zipcode.');
				$('#' + elementId).parent().next('.login-reg-err').show();
				return false;
			}
		} else {
			$('#' + elementId).parent().next('.login-reg-err').html('Please enter zipcode.');
			$('#' + elementId).parent().next('.login-reg-err').show();
			return false;
		}
	}
}

function escapeRegExp(str) {
	return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
}

// Function to validate the phone number
function validatePhoneNumber(elementId, isOnlyShowToast) {
	var regExToTest = phoneRegex;
	if (currentPhoneRegEx && currentPhoneRegEx != "") {
		var regExToTestStr = currentPhoneRegEx;
		regExToTestStr = escapeRegExp(currentPhoneRegEx);
		regExToTestStr = regExToTestStr.replace(/d/g, '\\d');
		regExToTest = new RegExp(regExToTestStr);
	}
	if ($('#' + elementId).val() != "") {
		if (regExToTest.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			var msg = 'Please enter a valid phone number';
			if (isOnlyShowToast) {
				$('#overlay-toast').html(msg);
				showToast();
			} else {
				showErrorMobileAndWeb(msg);
			}
			return false;
		}
	} else {
		var msg = 'Please enter phone number';
		if (isOnlyShowToast) {
			$('#overlay-toast').html(msg);
			showToast();
		} else {
			showErrorMobileAndWeb(msg);
		}
		return false;
	}
}

// Function to validate Address 1
function validateAddress1(elementId, isOnlyShowToast) {
	if ($('#' + elementId).val() != "") {
		return true;
	} else {
		var msg = 'Please enter address';

		if (isOnlyShowToast) {
			$('#overlay-toast').html(msg);
			showToast();
		} else {
			showErrorMobileAndWeb(msg);
		}

		return false;
	}
}

function validateAddress2(elementId) {
	return true;
}

function validateCountryProfile() {
	var country = $.trim($('#prof-country').val());
	if (country == "") {
		var msg = 'Please enter country name';
		$('#overlay-toast').html(msg);
		showToast();
		return false;
	} else {
		var countryCode = $.trim($('#prof-country').val());
		if (countryCode == "") {
			var msg = 'Please enter valid country name';
			$('#overlay-toast').html(msg);
			showToast();
			return false;
		} else {
			return true;
		}
	}
}

// Function to validate Branch name
function validateBranchName(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (companyNameRegEx.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid branch name.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('Please enter branch name.');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (companyNameRegEx.test($('#' + elementId).val()) == true) {
				$('#' + elementId).next('.input-error-2').hide();
				return true;
			} else {
				$('#' + elementId).next('.input-error-2').html('Please enter a valid branch name.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			$('#' + elementId).next('.input-error-2').html('Please enter branch name.');
			$('#' + elementId).next('.input-error-2').show();
			return false;
		}
	}
}

// Function to validate the username
function validateEncompassUserName(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#overlay-toast').html('Please enter user name');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#' + elementId).next('.hm-item-err-2').html('Please enter user name.');
			$('#' + elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}

// function to validate a password in form
function validateEncompassPassword(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#overlay-toast').html('Please enter password');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#' + elementId).next('.hm-item-err-2').html('Please enter password.');
			$('#' + elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}

// Function to validate the url
function validateURL(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#overlay-toast').html('Please enter url');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#' + elementId).next('.hm-item-err-2').html('Please enter url.');
			$('#' + elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}

// Function to validate Lone Wolf Input
function validateLoneWolf(elementId) {
	var msg;
	if (elementId == 'lone-client') {
		msg = "Please enter Client Code.";
	}
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#overlay-toast').html(msg);
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#' + elementId).next('.hm-item-err-2').html(msg);
			$('#' + elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}

function validateReminderInterval(elementId) {
	if ($('#' + elementId).val() != "") {
		if (numberRegEx.test($('#' + elementId).val())) {
			$('#' + elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#' + elementId).next('.hm-item-err-2').html('Please enter a valid number.');
			$('#' + elementId).next('.hm-item-err-2').show();
			return false;
		}
	} else {
		$('#' + elementId).next('.hm-item-err-2').html('Please enter number');
		$('#' + elementId).next('.hm-item-err-2').show();
		return false;
	}
}

$('.user-info-initial').click(function(e) {
	e.stopPropagation();
	$('.initial-dd-wrapper').slideToggle(200);
});

$('body').click(function() {
	if ($('.initial-dd-wrapper').css('display') == "block") {
		$('.initial-dd-wrapper').slideUp(200);
	}
});

function upgradeToPaidPlan() {
	var url = "./upgradetopaidplanpage.do";

	callAjaxGET(url, function(data) {
		$('#outer-payment').html(data);
		$('#outer-payment').show();
	}, false);
}

function loadDisplayPicture(profileMasterId) {
	var payload = {
		"profileMasterId" : profileMasterId
	};
	callAjaxGETWithTextData("./getdisplaypiclocation.do", function(data) {
		if (data != undefined) {
			if (data != undefined && data != "") {
				var imageUrl = JSON.parse(data);
				if (imageUrl != undefined && imageUrl != "undefined" && imageUrl.trim() != "") {
					$("#hdr-usr-img").css("background", "url(" + imageUrl + ") no-repeat center");
					$("#hdr-usr-img").css("background-size", "cover");
					$("#usr-initl").html("");
				} else {
					callAjaxGET('./initialofusername.do', displayPicCallback, false);
				}
			}

		}
		return data.responseJSON;
	}, false, payload);
}
function displayPicCallback(data) {
	$("#hdr-usr-img").css("background", "");
	$("#usr-initl").html(data);
}

/**
 * Method to redirect to error page
 */
function redirectTo404ErrorPage() {
	// location.href = window.location.origin + "/error";
	location.href = getLocationOrigin() + "/error";
}

function sendVerificationMail(emailUrl) {
	var payload = {
		"emailUrl" : emailUrl
	};
	callAjaxGetWithPayloadData("./sendverificationmail", verificationCallback, payload, true);
}

function verificationCallback() {
	$('#overlay-toast').html('Verification mail sent to the registered Email Id');
	showOverlay();
}

// Header buttons
$(document).on('click', '.hdr-log-btn', function() {
	window.location = "/login.do";
});
$(document).on('click', '.hdr-reg-btn', function() {
	window.location = "/accountsignupredirect.do?newUser=true";
});
$(document).on('click', '.hdr-logo', function() {
	showOverlay();
	window.location = "/home.do";
});

// ESC functionality
$(document).keyup(function(e) {
	if (e.keyCode == 27) {
		if ($('#overlay-main').is(":visible"))
			overlayRevert();

		else if ($('#bd-srv-pu').is(":visible"))
			$('.bd-q-btn-done').trigger('click');
	}
});

function validateInputField(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			return true;
		} else {
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			return true;
		} else {
			return false;
		}
	}
}

function validateTextArea(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			return true;
		} else {
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			return true;
		} else {
			return false;
		}
	}
}

function validateCountryZipcode(elementId, isOnlyShowToast) {

	if (selectedCountryRegEx == "" || selectedCountryRegEx == '/^$/') {

		var countryCode = $('#country-code').val();
		var flag = false;

		for (var i = 0; i < postCodeRegex.length; i++) {

			if (postCodeRegex[i].code == countryCode) {
				selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
				selectedCountryRegEx = new RegExp(selectedCountryRegEx);
				flag = true;
				break;
			}
		}

		if (!flag) {

			selectedCountryRegEx = ".*";
			selectedCountryRegEx = new RegExp(selectedCountryRegEx);

		}
	}

	var zipcode = $('#' + elementId).val();
	if (zipcode != "") {
		if (selectedCountryRegEx.test(zipcode) == true) {
			return true;
		} else {
			var msg = 'Please enter a valid zipcode';
			if (isOnlyShowToast) {
				$('#overlay-toast').html(msg);
				showToast();
			} else {
				showErrorMobileAndWeb(msg);
			}
			return false;
		}
	} else {
		var msg = 'Please enter the zipcode';
		if (isOnlyShowToast) {
			$('#overlay-toast').html(msg);
			showToast();
		} else {
			showErrorMobileAndWeb(msg);
		}
		return false;
	}
}

function toTitleCase(str) {
	return str.replace(/\w\S*/g, function(txt) {
		return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
	});
}

// Sign up page functions
function signupUser() {
	if (validateSignUpForm()) {
		$('#frm-signup').submit();
		showOverlay();
	}
}

function initializeSingupPage() {
	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
			showError($('#message').val());
		} else {
			showInfo($('#message').val());
		}
	}

	$('#signup-submit').click(function(e) {
		signupUser();
	});

	$('input').keypress(function(e) {
		// detect enter
		if (e.which == 13) {
			e.preventDefault();
			signupUser();
		}
	});
}

function validateSignUpForm() {
	hideError();
	if (!validateFirstName('sign-fname')) {
		$('#sign-fname').focus();
		return false;
	}
	if (!validateLastName('sign-lname')) {
		$('#sign-lname').focus();
		return false;
	}
	if (!validateEmailId('sign-email')) {
		$('#sign-email').focus();
		return false;
	}
	return true;
}

// Function to validate multiple email ids in a form
function validateMultipleEmailIds(elementId) {
	var emailIdStr = $('#' + elementId).val();
	if (emailIdStr != "") {
		emailIdStr = emailIdStr.trim();
		var emailIds = emailIdStr.split(",");
		for (var i = 0; i < emailIds.length; i++) {
			if (emailRegex.test(emailIds[i].trim()) == false) {
				showErrorMobileAndWeb('Please enter a valid Email Address');
				return false;
			}
		}
		return true;
	} else {
		showErrorMobileAndWeb('Please enter a valid Email Address');
		return false;
	}
}

// Function to validate complaint registraion form
function validateComplaintRegistraionForm() {
	var validate = true;

	// hide the server error
	$("#serverSideerror").hide();

	// validate error code on email id
	validate = validateMultipleEmailIds('comp-mailId')

	// check if checkbox is enabled
	if ($('input[name="enabled"]').prop("checked")) {
		// check whether rating selectd is 0 and mood is empty
		var rating = $("#comp-rating-post").val();
		var mood = $("#comp-mood").val();

		if (rating == 0 && mood == '') {
			// reset the check box if score is 0 and mood not selected
			$('#compl-checkbox').addClass('bd-check-img-checked');
			$('input[name="enabled"]').prop("checked", false);
			$('input[name="enabled"]').val("");
		}

	}

	if (!validate) {
		return false;
	} else {
		/* Form validated. */
		return true;
	}
}

// Functions for login page
function loginUserLoginPage() {
	if (validateFormLoginPage('login-form')) {
		$('#frm-login').submit();
		showOverlay();
	}
}

function validateFormLoginPage(id) {
	if (!validateUserNameLoginPage('login-user-id')) {
		$('#login-user-id').focus();
		return false;
	}
	if (!validatePasswordLoginPage('login-pwd')) {
		$('#login-pwd').focus();
		return false;
	}
	return true;
}

function validateUserNameLoginPage(elementId) {
	if ($('#' + elementId).val() != "") {
		if (emailRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			showError('Please enter a valid user name.');
			return false;
		}
	} else {
		showError('Please enter user name.');
		return false;
	}
}

function validatePasswordLoginPage(elementId) {
	if ($('#' + elementId).val() != "") {
		return true;
	} else {
		showError('Please enter password.');
		return false;
	}
}

function initializeLoginPage() {
	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
			showError($('#message').val());
		} else {
			showInfo($('#message').val());
		}
	}

	$('#login-submit').click(function(e) {
		loginUserLoginPage();
	});

	$('input').keypress(function(e) {
		// detect enter
		if (e.which == 13) {
			e.preventDefault();
			loginUserLoginPage();
		}
	});
}

// Functions for forgot password page
function validateForgotPasswordForm(id) {
	var isFocussed = false;
	var isSmallScreen = false;
	isForgotPasswordFormValid = true;
	if ($(window).width() < 768) {
		isSmallScreen = true;
	}
	if (!validateEmailId('login-user-id')) {
		isForgotPasswordFormValid = false;
		if (!isFocussed) {
			$('#login-user-id').focus();
			isFocussed = true;
		}
		if (isSmallScreen) {
			return isForgotPasswordFormValid;
		}
	}
	return isForgotPasswordFormValid;
}

function submitForgotPasswordForm() {
	if (validateForgotPasswordForm('forgot-pwd-form')) {
		$('#forgot-pwd-form').submit();
	}
}

function initializeForgotPasswordPage() {
	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'SUCCESS_MESSAGE') {
			showInfo($('#message').val());
		} else {
			showError($('#message').val());
		}
	}

	$('input').keypress(function(e) {
		e.stopPropagation();
		// detect enter
		if (e.which == 13) {
			e.preventDefault();
			submitForgotPasswordForm();
		}
	});

	$('#forgot-pwd-submit').click(function(e) {
		submitForgotPasswordForm();
	});

	$('#login-user-id').blur(function() {
		if (validateEmailId(this.id)) {
			hideError();
		}
	});
}

// Functions for reset password page
function submitResetPasswordForm() {
	if (validateResetPasswordForm('reset-pwd-form')) {
		$('#reset-pwd-form').submit();
	}
}

function validateResetPasswordForm(id) {
	if (!validateEmailId('login-user-id')) {
		$('#login-user-id').focus();
		return false;
	}
	if (!validatePassword('login-pwd')) {
		$('#login-pwd').focus();
		return false;
	}
	if (!validateConfirmPassword('login-pwd', 'login-cnf-pwd')) {
		$('#login-cnf-pwd').focus();
		return false;
	}
	return true;
}

function initializeResetPasswordPage() {
	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'SUCCESS_MESSAGE') {
			showInfo($('#message').val());
		} else {
			showError($('#message').val());
		}
	}

	$('#reset-pwd-submit').click(function(e) {
		submitResetPasswordForm();
	});

	$('input').keypress(function(e) {
		// detect enter
		if (e.which == 13) {
			e.preventDefault();
			submitResetPasswordForm();
		}
	});

	$('#login-user-id').blur(function() {
		validateEmailId(this.id);
	});

	$('#login-pwd').blur(function() {
		validatePassword(this.id);
	});

	$('#login-cnf-pwd').blur(function() {
		validateConfirmPassword('login-pwd', this.id);
	});
}

// Function to validate country
function validateCountry(elementId) {
	var country = $.trim($('#' + elementId).val());
	if (country == "") {
		return false;
	} else {
		var countryCode = $.trim($('#country-code').val());
		if (countryCode == "") {
			return false;
		} else {
			return true;
		}
	}
}

function validateCompanyInformationForm() {
	if (!validateCompany('com-company')) {
		$('#com-company').focus();
		return false;
	}
	if (!validateAddress1('com-address1')) {
		$('#com-address1').focus();
		return false;
	}
	if (!validateAddress2('com-address2')) {
		$('#com-address2').focus();
		return false;
	}
	if (!validateCountry('com-country')) {
		$('#com-country').focus();
		return false;
	}
	if (!validateCountryZipcode('com-zipcode')) {
		$('#com-zipcode').focus();
		return false;
	}
	if (!validatePhoneNumber('com-contactno')) {
		$('#com-contactno').focus();
		return false;
	}
	return true;
}

// Functions for home page
function resizeHomePageFunc() {
	var winW = window.innerWidth;
	if (winW < 768) {
		var offset = winW - 114 - 50;
		$('.reg-cap-txt').css('width', offset + 'px');
		if ($('#pro-wrapper-top').html() == "") {
			$('#pro-wrapper-top').html($('#pro-wrapper').html());
			$('#pro-wrapper').html('');
		}
	} else {
		if ($('#pro-wrapper').html() == "") {
			$('#pro-wrapper').html($('#pro-wrapper-top').html());
			$('#pro-wrapper-top').html('');
		}
	}
}

function loginUserHomePage() {
	if (validateLoginForm('login-form')) {
		$('#login-form').submit();
		showOverlay();
	}
}

function submitRegistrationFormHomePage() {
	if (validatePreRegistrationForm('reg-form')) {
		$('#registration-form').submit();
		showOverlay();
	}
}

function submitFindProFormHomePage() {
	$('#find-pro-form').submit();
	showOverlay();
}

// Initialize home page
function initializeHomePage() {
	resizeHomePageFunc();
	$(window).resize(resizeHomePageFunc);

	// Functions to trigger form validation of various input elements
	if ($('#message').val() != "") {
		showRegErr($('#message').val());
	}

	// Login form
	$('#login-form').on('keyup', 'input', function(e) {
		if (e.which == 13) {
			$('#login-submit').trigger('click');
		}
	});

	$('#login-form').on('click', '#login-submit', function() {
		loginUserHomePage();
	});

	$('#registration-form').on('click', '#reg-submit', function(e) {
		e.preventDefault();
		submitRegistrationFormHomePage();
	});

	$('#registration-form').on('keyup', 'input', function(e) {
		// detect enter
		if (e.which == 13) {
			$('#reg-submit').trigger('click');
		}
	});

	$('#reg-err-pu-close').click(function() {
		hideRegErr();
	});

	// Find a pro
	$('#find-pro-form').on('click', '#find-pro-submit', function(e) {
		e.preventDefault();
		submitFindProFormHomePage();
	});

	$('#find-pro-form').on('keyup', 'input', function(e) {
		if (e.which == 13) {
			$('#find-pro-submit').trigger('click');
		}
	});

	$('#header-search-icn').click(function(e) {
		$('#pro-wrapper-top').slideToggle(200);
	});
}

// Functions for find a pro page
// pagination variables for pro List page
var rowSize = 10;
var startIndex = 0;

// Function to initialiize find a pro page
function initializeFindAProPage() {
	startIndex = 0;
	fetchUsers(startIndex);
	adjustTextContainerWidthOnResize();

	$(window).resize(function() {
		if ($(window).width() < 768) {
			adjustTextContainerWidthOnResize();
		}
	});

	$('#find-pro-form').on('click', '#find-pro-submit', function(e) {
		e.preventDefault();
		submitFindAProForm();
	});

	$('#find-pro-form').on('keypress', 'input', function(e) {
		// detect enter
		if (e.which == 13) {
			e.preventDefault();
			submitFindAProForm();
		}
	});

	// Click events proList pagination buttons
	$('#pro-paginate-btn').on('click', '#pro-next.paginate-button', function(e) {
		var start = parseInt($('#pro-paginate-btn').attr("data-start"));
		var batch = parseInt($('#pro-paginate-btn').attr("data-batch"));

		start += batch;
		$('#pro-paginate-btn').attr("data-start", start);
		fetchUsers(start);
	});

	$('#pro-paginate-btn').on('click', '#pro-prev.paginate-button', function(e) {
		var start = parseInt($('#pro-paginate-btn').attr("data-start"));
		var batch = parseInt($('#pro-paginate-btn').attr("data-batch"));

		start -= batch;
		$('#pro-paginate-btn').attr("data-start", start);
		fetchUsers(start);
	});

	$('#pro-paginate-btn').on('keypress', '#sel-page-prolist', function(e) {
		// if the letter is not digit then don't type anything
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		var totalPage = parseInt($('#pro-total-pages').text());
		var prevPageNoVal = parseInt($('#sel-page-prolist').val());
		if (prevPageNoVal == NaN) {
			prevPageNoVal = 0;
		}
		var pageNo = prevPageNoVal + String.fromCharCode(e.which);
		pageNo = parseInt(pageNo);
		if (pageNo >= totalPage || pageNo <= 0) {
			return false;
		}
	});

	$('#pro-paginate-btn').on('keyup', '#sel-page-prolist', function(e) {
		if (e.which == 13) {
			$(this).trigger('blur');
		}
	});

	$('#pro-paginate-btn').on('blur', '#sel-page-prolist', function(e) {
		var batch = parseInt($('#pro-paginate-btn').attr("data-batch"));
		var pageNoVal = parseInt($('#sel-page-prolist').val());
		start = (pageNoVal - 1) * batch;
		$('#pro-paginate-btn').attr("data-start", start);
		fetchUsers(start);
	});
}
var MappedUserSize = 10;
var MappedUserStartIndex = 0;
var MappedUserCount = -1;
function initializeMapped() {
	$('#mapped').html('');
	$('#mapped-paginate-btn').attr("data-start", 0);
	MappedUserStartIndex = 0;
	fetchMappedUsers(MappedUserStartIndex);

}

var UnmatchedUserSize = 10;
var UnmatchedUserStartIndex = 0;
var UnmatchedUserCount = -1;
function initializeUnmatchedUserPage() {
	$('#new').html('');
	$('#un-new-paginate-btn').attr("data-start", 0);
	UnmatchedUserStartIndex = 0;
	fetchUnmatchedUsers(UnmatchedUserStartIndex);
}

var CorruptRecordsSize = 10;
var CorruptRecordsStartIndex = 0;
var CorruptRecordsCount = -1;
function initializeCorruptRecordsPage() {
	$('#corrupt').html('');
	$('#corrupt-paginate-btn').attr("data-start", 0);
	CorruptRecordsStartIndex = 0;
	fetchCorruptRecords(CorruptRecordsStartIndex);
}

function downloadSurveyReport(tabId, companyId) {
	window.location.href = "/downloadusersurveyreport.do?tabId=" + tabId + "&companyId=" + companyId;
}

function bindEventForCorruptRecordPage() {

	// Click events proList pagination buttons
	$('#corrupt-paginate-btn').on('click', '#corrupt-next.paginate-button', function(e) {
		var start = parseInt($('#corrupt-paginate-btn').attr("data-start"));
		var batch = parseInt($('#corrupt-paginate-btn').attr("data-batch"));

		start += batch;
		$('#corrupt-paginate-btn').attr("data-start", start);
		fetchCorruptRecords(start);
	});

	$('#corrupt-paginate-btn').on('click', '#corrupt-prev.paginate-button', function(e) {
		var start = parseInt($('#corrupt-paginate-btn').attr("data-start"));
		var batch = parseInt($('#corrupt-paginate-btn').attr("data-batch"));

		start -= batch;
		$('#corrupt-paginate-btn').attr("data-start", start);
		fetchCorruptRecords(start);
	});

	$('#corrupt-paginate-btn').on('keypress', '#sel-page-corrupt-list', function(e) {
		// if the letter is not digit then don't type anything
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		var totalPage = parseInt($('#corrupt-total-pages').text());
		var prevPageNoVal = parseInt($('#sel-page-corrupt-list').val());
		if (isNaN(prevPageNoVal)) {
			prevPageNoVal = 1;
		}
		var pageNo = prevPageNoVal + String.fromCharCode(e.which);
		pageNo = parseInt(pageNo);
		if (pageNo > totalPage || pageNo <= 0) {
			$('#sel-page-corrupt-list').val('1');
			return false;
		}
	});

	$('#corrupt-paginate-btn').on('keyup', '#sel-page-corrupt-list', function(e) {
		if (e.which == 13) {
			$(this).trigger('blur');
		}
	});

	$('#corrupt-paginate-btn').on('blur', '#sel-page-corrupt-list', function(e) {
		var batch = parseInt($('#corrupt-paginate-btn').attr("data-batch"));
		var pageNoVal = parseInt($('#sel-page-corrupt-list').val());
		if (isNaN(pageNoVal)) {
			pageNoVal = 1;
		}
		UnmatchedUserStartIndex = (pageNoVal - 1) * batch;
		$('#corrupt-paginate-btn').attr("data-start", UnmatchedUserStartIndex);
		fetchCorruptRecords(UnmatchedUserStartIndex);
	});
}

function bindEventForMappedUserPage() {
	// Click events proList pagination buttons
	$('#mapped-paginate-btn').on('click', '#mapped-next.paginate-button', function(e) {
		var start = parseInt($('#mapped-paginate-btn').attr("data-start"));
		var batch = parseInt($('#mapped-paginate-btn').attr("data-batch"));

		start += batch;
		$('#mapped-paginate-btn').attr("data-start", start);
		fetchMappedUsers(start);
	});

	$('#mapped-paginate-btn').on('click', '#mapped-prev.paginate-button', function(e) {
		var start = parseInt($('#mapped-paginate-btn').attr("data-start"));
		var batch = parseInt($('#mapped-paginate-btn').attr("data-batch"));

		start -= batch;
		$('#mapped-paginate-btn').attr("data-start", start);
		fetchMappedUsers(start);
	});

	$('#mapped-paginate-btn').on('keypress', '#sel-page-mapped-list', function(e) {
		// if the letter is not digit then don't type anything
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		var totalPage = parseInt($('#mapped-total-pages').text());
		var prevPageNoVal = parseInt($('#sel-page-mapped-list').val());
		if (isNaN(prevPageNoVal)) {
			prevPageNoVal = 1;
		}
		var pageNo = prevPageNoVal + String.fromCharCode(e.which);
		pageNo = parseInt(pageNo);
		if (pageNo > totalPage || pageNo <= 0) {
			$('#sel-page-mapped-list').val('1');
			return false;
		}
	});

	$('#mapped-paginate-btn').on('keyup', '#sel-page-mapped-list', function(e) {
		if (e.which == 13) {
			$(this).trigger('blur');
		}
	});

	$('#mapped-paginate-btn').on('blur', '#sel-page-mapped-list', function(e) {
		var batch = parseInt($('#mapped-paginate-btn').attr("data-batch"));
		var pageNoVal = parseInt($('#sel-page-mapped-list').val());
		if (isNaN(pageNoVal)) {
			pageNoVal = 1;
		}
		MappedUserStartIndex = (pageNoVal - 1) * batch;
		$('#mapped-paginate-btn').attr("data-start", MappedUserStartIndex);
		fetchMappedUsers(MappedUserStartIndex);
	});
}
function bindEventForUnmatchedUserPage() {

	// Click events proList pagination buttons
	$('#un-new-paginate-btn').on('click', '#un-new-next.paginate-button', function(e) {
		var start = parseInt($('#un-new-paginate-btn').attr("data-start"));
		var batch = parseInt($('#un-new-paginate-btn').attr("data-batch"));

		start += batch;
		$('#un-new-paginate-btn').attr("data-start", start);
		fetchUnmatchedUsers(start);
	});

	$('#un-new-paginate-btn').on('click', '#un-new-prev.paginate-button', function(e) {
		var start = parseInt($('#un-new-paginate-btn').attr("data-start"));
		var batch = parseInt($('#un-new-paginate-btn').attr("data-batch"));

		start -= batch;
		$('#un-new-paginate-btn').attr("data-start", start);
		fetchUnmatchedUsers(start);
	});

	$('#un-new-paginate-btn').on('keypress', '#sel-page-un-new-list', function(e) {
		// if the letter is not digit then don't type anything
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		var totalPage = parseInt($('#un-new-total-pages').text());
		var prevPageNoVal = parseInt($('#sel-page-un-new-list').val());
		if (isNaN(prevPageNoVal)) {
			prevPageNoVal = 1;
		}
		var pageNo = prevPageNoVal + String.fromCharCode(e.which);
		pageNo = parseInt(pageNo);
		if (pageNo > totalPage || pageNo <= 0) {
			$('#sel-page-un-new-list').val('1');
			return false;
		}
	});

	$('#un-new-paginate-btn').on('keyup', '#sel-page-un-new-list', function(e) {
		if (e.which == 13) {
			$(this).trigger('blur');
		}
	});

	$('#un-new-paginate-btn').on('blur', '#sel-page-un-new-list', function(e) {
		var batch = parseInt($('#un-new-paginate-btn').attr("data-batch"));
		var pageNoVal = parseInt($('#sel-page-un-new-list').val());
		if (isNaN(pageNoVal)) {
			pageNoVal = 1;
		}
		UnmatchedUserStartIndex = (pageNoVal - 1) * batch;
		$('#un-new-paginate-btn').attr("data-start", UnmatchedUserStartIndex);
		fetchUnmatchedUsers(UnmatchedUserStartIndex);
	});
}

var ProcessedUserSize = 10;
var ProcessedUserStartIndex = 0;
var ProcessedUserCount = -1;
function initializeProcesedUserPage() {
	$('#processed').html('');
	$('#un-processed-paginate-btn').attr("data-start", 0);
	ProcessedUserStartIndex = 0;
	fetchProcessedUsers(ProcessedUserStartIndex);
}

function bindEventsForProcessUserPage() {
	$('#un-processed-paginate-btn').on('click', '#un-processed-next.paginate-button', function(e) {
		var start = parseInt($('#un-processed-paginate-btn').attr("data-start"));
		var batch = parseInt($('#un-processed-paginate-btn').attr("data-batch"));

		start += batch;
		$('#un-processed-paginate-btn').attr("data-start", start);
		fetchProcessedUsers(start);
	});

	$('#un-processed-paginate-btn').on('click', '#un-processed-prev.paginate-button', function(e) {
		var start = parseInt($('#un-processed-paginate-btn').attr("data-start"));
		var batch = parseInt($('#un-processed-paginate-btn').attr("data-batch"));

		start -= batch;
		$('#un-processed-paginate-btn').attr("data-start", start);
		fetchProcessedUsers(start);
	});

	$('#un-processed-paginate-btn').on('keypress', '#sel-page-un-processed-list', function(e) {
		// if the letter is not digit then don't type anything
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		var totalPage = parseInt($('#un-processed-total-pages').text());
		var prevPageNoVal = parseInt($('#sel-page-un-processed-list').val());
		if (isNaN(prevPageNoVal)) {
			prevPageNoVal = 1;
		}
		var pageNo = prevPageNoVal + String.fromCharCode(e.which);
		pageNo = parseInt(pageNo);
		if (pageNo > totalPage || pageNo <= 0) {
			$('#sel-page-un-processed-list').val('1');
			return false;
		}
	});

	$('#un-processed-paginate-btn').on('keyup', '#sel-page-un-processed-list', function(e) {
		if (e.which == 13) {
			$(this).trigger('blur');
		}
	});

	$('#un-processed-paginate-btn').on('blur', '#sel-page-un-processed-list', function(e) {
		var batch = parseInt($('#un-processed-paginate-btn').attr("data-batch"));
		var pageNoVal = parseInt($('#sel-page-un-processed-list').val());
		if (isNaN(pageNoVal)) {
			pageNoVal = 1;
		}
		ProcessedUserStartIndex = (pageNoVal - 1) * batch;
		$('#un-processed-paginate-btn').attr("data-start", ProcessedUserStartIndex);
		fetchProcessedUsers(ProcessedUserStartIndex);
	});
}

/**
 * Method to fetch users list based on the criteria i.e if profile level is specified, bring all users of that level else search based on first/last name
 * 
 * @param newIndex
 */
function fetchUsers(newIndex) {
	showOverlay();
	var profileLevel = $("#fp-profile-level-fetch-info").data("profile-level");
	var iden = $("#fp-profile-level-fetch-info").data("iden");

	if (profileLevel != undefined && profileLevel != "") {
		fetchUsersByProfileLevel(iden, profileLevel, newIndex);
	} else {
		var formData = new FormData();
		formData.append("find-pro-first-name", $('#fp-first-name-pattern').val());
		formData.append("find-pro-last-name", $('#fp-last-name-pattern').val());
		formData.append("find-pro-start-index", newIndex);
		formData.append("find-pro-row-size", rowSize);
		if (findProCompanyProfileName != undefined) {
			formData.append("find-pro-profile-name", findProCompanyProfileName);
		}

		if (!($('#find-pro-first-name').val() == "" && $('#find-pro-last-name').val() == ""))
			callAjaxPOSTWithTextData("./findaproscroll.do", paginateUsersProList, true, formData);
		else
			hideOverlay();
	}
}
function fetchMappedUsers(newIndex) {
	showOverlay();
	var payload = {
		"batchSize" : MappedUserSize,
		"count" : MappedUserCount,
		"startIndex" : newIndex

	};

	callAjaxGetWithPayloadData("./getuserwithaliasedemails.do", paginateMappedUser, payload, true);

}
function fetchUnmatchedUsers(newIndex) {
	showOverlay();
	var payload = {
		"batchSize" : UnmatchedUserSize,
		"count" : UnmatchedUserCount,
		"startIndex" : newIndex

	};

	callAjaxGetWithPayloadData("./getunmatchedpreinitiatedsurveys.do", paginateUnmatchedUser, payload, true);

}

function fetchCorruptRecords(newIndex) {
	showOverlay();
	var payload = {
		"batchSize" : CorruptRecordsSize,
		"count" : CorruptRecordsCount,
		"startIndex" : newIndex

	};

	callAjaxGetWithPayloadData("./getcorruptpreinitiatedsurveys.do", paginateCorruptRecords, payload, true);
}

function fetchProcessedUsers(newIndex) {
	showOverlay();
	var payload = {
		"batchSize" : ProcessedUserSize,
		"count" : ProcessedUserCount,
		"startIndex" : newIndex

	};

	callAjaxGetWithPayloadData("./getprocessedpreinitiatedsurveys.do", paginateProcessedUser, payload, true);
}
// Function to validate registration form
function validateFindProForm() {
	$("#serverSideerror").hide();
	if (!validateProFirstNamePattern('find-pro-first-name') && !validateProLastNamePattern('find-pro-last-name')) {
		$('#find-pro-first-name').focus();
		return false;
	}
	return true;
}

function submitFindAProForm() {
	if (validateFindProForm()) {
		$('#find-pro-form').submit();
		// showOverlay();
	} else {
		if (!($('#find-pro-first-name').val() == "" && $('#find-pro-last-name').val() == ""))
			showError("Please enter either a valid First Name or Last Name to search for");
	}
}

// Function to update the pagination buttons
function updatePaginationBtnsForProList() {
	var start = parseInt($('#pro-paginate-btn').attr("data-start"));
	var total = parseInt($('#pro-paginate-btn').attr("data-total"));
	var batch = parseInt($('#pro-paginate-btn').attr("data-batch"));

	// update previous button
	if (start == 0) {
		$('#pro-prev').removeClass('paginate-button');
	} else {
		$('#pro-prev').addClass('paginate-button');
	}

	// update next button
	if (start + batch >= total) {
		$('#pro-next').removeClass('paginate-button');
	} else {
		$('#pro-next').addClass('paginate-button');
	}

	// update page no
	var pageNo = 0;
	if (start < total) {
		pageNo = start / batch + 1;
	} else {
		pageNo = start / batch;
	}
	var emptyPageNo = isNaN(pageNo);
	if (emptyPageNo) {
		$('#pro-paginate-btn').attr("data-start", 0);
		$('#pro-prev').removeClass('paginate-button');
		pageNo = 1;
	}
	$('#sel-page-prolist').val(pageNo);
}
function updatePaginationBtnsForMappedUser() {
	var start = parseInt($('#mapped-paginate-btn').attr("data-start"));
	var total = parseInt($('#mapped-paginate-btn').attr("data-total"));
	var batch = parseInt($('#mapped-paginate-btn').attr("data-batch"));

	// update previous button
	if (start == 0) {
		$('#mapped-prev').removeClass('paginate-button');
	} else {
		$('#mapped-prev').addClass('paginate-button');
	}

	// update next button
	if (start + batch >= total) {
		$('#mapped-next').removeClass('paginate-button');
	} else {
		$('#mapped-next').addClass('paginate-button');
	}

	// update page no
	var pageNo = 0;
	if (start < total) {
		pageNo = (start / batch) + 1;
	} else {
		pageNo = start / batch;
	}
	var emptyPageNo = isNaN(pageNo);
	if (emptyPageNo) {
		$('#mapped-paginate-btn').attr("data-start", 0);
		$('#mapped-prev').removeClass('paginate-button');
		pageNo = 1;
	}
	if (pageNo == 0) {
		pageNo = 1;
	}
	$('#sel-page-mapped-list').val(pageNo);
}
function updatePaginationBtnsForUnmatchedUser() {
	var start = parseInt($('#un-new-paginate-btn').attr("data-start"));
	var total = parseInt($('#un-new-paginate-btn').attr("data-total"));
	var batch = parseInt($('#un-new-paginate-btn').attr("data-batch"));

	// update previous button
	if (start == 0) {
		$('#un-new-prev').removeClass('paginate-button');
	} else {
		$('#un-new-prev').addClass('paginate-button');
	}

	// update next button
	if (start + batch >= total) {
		$('#un-new-next').removeClass('paginate-button');
	} else {
		$('#un-new-next').addClass('paginate-button');
	}

	// update page no
	var pageNo = 0;
	if (start < total) {
		pageNo = (start / batch) + 1;
	} else {
		pageNo = start / batch;
	}
	var emptyPageNo = isNaN(pageNo);
	if (emptyPageNo) {
		$('#un-new-paginate-btn').attr("data-start", 0);
		$('#un-new-prev').removeClass('paginate-button');
		pageNo = 1;
	}
	if (pageNo == 0) {
		pageNo = 1;
	}
	$('#sel-page-un-new-list').val(pageNo);
}
function updatePaginationBtnsForCorruptRecords() {
	var start = parseInt($('#corrupt-paginate-btn').attr("data-start"));
	var total = parseInt($('#corrupt-paginate-btn').attr("data-total"));
	var batch = parseInt($('#corrupt-paginate-btn').attr("data-batch"));

	// update previous button
	if (start == 0) {
		$('#corrupt-prev').removeClass('paginate-button');
	} else {
		$('#corrupt-prev').addClass('paginate-button');
	}

	// update next button
	if (start + batch >= total) {
		$('#corrupt-next').removeClass('paginate-button');
	} else {
		$('#corrupt-next').addClass('paginate-button');
	}

	// update page no
	var pageNo = 0;
	if (start < total) {
		pageNo = (start / batch) + 1;
	} else {
		pageNo = start / batch;
	}
	var emptyPageNo = isNaN(pageNo);
	if (emptyPageNo) {
		$('#corrupt-paginate-btn').attr("data-start", 0);
		$('#corrupt-prev').removeClass('paginate-button');
		pageNo = 1;
	}
	if (pageNo == 0) {
		pageNo = 1;
	}
	$('#sel-page-corrupt-list').val(pageNo);
}
function updatePaginationBtnsForProcessedUser() {
	var startProcess = parseInt($('#un-processed-paginate-btn').attr("data-start"));
	var totalProcess = parseInt($('#un-processed-paginate-btn').attr("data-total"));
	var batchProcess = parseInt($('#un-processed-paginate-btn').attr("data-batch"));

	// update previous button
	if (startProcess == 0) {
		$('#un-processed-prev').removeClass('paginate-button');
	} else {
		$('#un-processed-prev').addClass('paginate-button');
	}

	// update next button
	if (startProcess + batchProcess >= totalProcess) {
		$('#un-processed-next').removeClass('paginate-button');
	} else {
		$('#un-processed-next').addClass('paginate-button');
	}

	// update page no
	var pageNoProcess = 0;
	if (startProcess < totalProcess) {
		pageNoProcess = startProcess / batchProcess + 1;
	} else {
		pageNoProcess = startProcess / batchProcess;
	}
	var emptyPageNo = isNaN(pageNoProcess);
	if (emptyPageNo) {
		$('#un-processed-paginate-btn').attr("data-start", 0);
		$('#un-processed-prev').removeClass('paginate-button');
		pageNoProcess = 1;
	}
	if (pageNoProcess == 0) {
		pageNoProcess = 1;
	}
	$('#sel-page-un-processed-list').val(pageNoProcess);
}
function paginateUsersProList(response) {
	var reponseJson = $.parseJSON(response);
	var start = parseInt($('#pro-paginate-btn').attr("data-start"));
	var batch = parseInt($('#pro-paginate-btn').attr("data-batch"));

	// error message
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#ctnt-list-wrapper').append("No Profiles found");
	} else {
		if (start == 0) {
			var usersSize = reponseJson.userFound;
			if (usersSize > 0) {
				$('#srch-num').text(usersSize);
				$('#pro-paginate-btn').show().attr("data-total", usersSize);
				var totalPage = 0;
				if (usersSize % batch == 0) {
					totalPage = parseInt(usersSize / batch);
				} else {
					totalPage = parseInt(usersSize / batch + 1);
				}

				$('#pro-total-pages').text(totalPage);
			}
			$('#srch-num-list').show();
		}
		paintProList(reponseJson.users);
	}
	updatePaginationBtnsForProList();
	scrollToTop();
	hideOverlay();
}
function paginateMappedUser(response) {
	var reponseJson = $.parseJSON(response);
	var start = parseInt($('#mapped-paginate-btn').attr("data-start"));
	var batch = parseInt($('#mapped-paginate-btn').attr("data-batch"));

	// error message
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#mapped').append("No Data found");
		hideOverlay();
	} else {
		if (start == 0) {
			var usersSize = reponseJson.totalRecord;
			MappedUserCount = usersSize ;
			if (usersSize > 0) {
				$('#mapped-paginate-btn').show().attr("data-total", usersSize);
				var totalPage = 0;
				if (usersSize % batch == 0) {
					totalPage = parseInt(usersSize / batch);
				} else {
					totalPage = parseInt(usersSize / batch + 1);
				}

				$('#mapped-total-pages').text(totalPage);
			} else if (usersSize == 0) {
				$('#mapped-no-data').html("No data found");
				$('#mapped-no-data').show();

			}

		}
		paintMappedUser(reponseJson);
	}
	updatePaginationBtnsForMappedUser();
	hideOverlay();
}
function paginateUnmatchedUser(response) {
	var reponseJson = $.parseJSON(response);
	var start = parseInt($('#un-new-paginate-btn').attr("data-start"));
	var batch = parseInt($('#un-new-paginate-btn').attr("data-batch"));

	// error message
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#new').append("No Data found");
		hideOverlay();
	} else {
		if (start == 0) {
			var usersSize = reponseJson.totalRecord;
			UnmatchedUserCount = usersSize;
			if (usersSize > 0) {
				$('#un-new-paginate-btn').show().attr("data-total", usersSize);
				var totalPage = 0;
				if (usersSize % batch == 0) {
					totalPage = parseInt(usersSize / batch);
				} else {
					totalPage = parseInt(usersSize / batch + 1);
				}

				$('#un-new-total-pages').text(totalPage);
			} else if (usersSize == 0) {
				$('#new-no-data').html("No data found");
				$('#new-no-data').show();
			}

		}
		paintUnmatchedUser(reponseJson);
	}
	updatePaginationBtnsForUnmatchedUser();
	hideOverlay();
}
function paginateCorruptRecords(response) {
	var reponseJson = $.parseJSON(response);
	var start = parseInt($('#corrupt-paginate-btn').attr("data-start"));
	var batch = parseInt($('#corrupt-paginate-btn').attr("data-batch"));

	// error message
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#corrupt').append("No Data found");
		hideOverlay();
	} else {
		if (start == 0) {
			var usersSize = reponseJson.totalRecord;
			CorruptRecordsCount = usersSize ;
			if (usersSize > 0) {
				$('#corrupt-paginate-btn').show().attr("data-total", usersSize);
				var totalPage = 0;
				if (usersSize % batch == 0) {
					totalPage = parseInt(usersSize / batch);
				} else {
					totalPage = parseInt(usersSize / batch + 1);
				}

				$('#corrupt-total-pages').text(totalPage);
			} else if (usersSize == 0) {
				$('#corrupt-no-data').html("No data found");
				$('#corrupt-no-data').show();
			}

		}
		paintCorruptRecords(reponseJson);
	}
	updatePaginationBtnsForCorruptRecords();
	hideOverlay();
}
function paginateProcessedUser(response) {
	var reponseJson = $.parseJSON(response);
	var start = parseInt($('#un-processed-paginate-btn').attr("data-start"));
	var batch = parseInt($('#un-processed-paginate-btn').attr("data-batch"));

	// error message
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#processed').append("No Data found");
		hideOverlay();
	} else {
		if (start == 0) {
			var usersSize = reponseJson.totalRecord;
			ProcessedUserCount = usersSize ;
			if (usersSize > 0) {
				$('#un-processed-paginate-btn').show().attr("data-total", usersSize);
				var totalPage = 0;
				if (usersSize % batch == 0) {
					totalPage = parseInt(usersSize / batch);
				} else {
					totalPage = parseInt(usersSize / batch + 1);
				}

				$('#un-processed-total-pages').text(totalPage);
			} else if (usersSize == 0) {
				$('#processed-no-data').html("No data found");
				$('#processed-no-data').show();
			}

		}
		paintProcessedUser(reponseJson);
	}
	updatePaginationBtnsForProcessedUser();
	hideOverlay();
}
function paintProList(usersList) {
	if (usersList != undefined) {
		var usersSize = usersList.length;

		var usersHtml = "";
		if (usersSize > 0) {
			$.each(usersList, function(i, user) {
				var evenOddClass = (i % 2 == 0) ? '' : 'ctnt-list-item-even';
				usersHtml = usersHtml + '<div class="ctnt-list-item clearfix ' + evenOddClass + '" data-profilename="' + user.profileUrl + '">';

				if (user.profileImageUrl != undefined && user.profileImageUrl.trim() != "") {
					usersHtml = usersHtml + '<div class="float-left ctnt-list-item-img" style="background: url(' + user.profileImageUrl + ') no-repeat center; background-size: cover;"></div>';
				} else {
					usersHtml = usersHtml + '<div class="float-left ctnt-list-item-img pro-list-default-img"></div>';
				}
				usersHtml = usersHtml + '<div class="float-left ctnt-list-item-txt-wrap">' + '<div class="ctnt-item-name user-display-name">' + user.displayName + '</div>';

				if (user.title != undefined) {
					usersHtml = usersHtml + '<div class="ctnt-item-desig">' + user.title + '</div>';
				}
				if (user.location != undefined) {
					usersHtml = usersHtml + '<div class="pro-addr-cont">' + user.location;
					if (user.industry != undefined) {
						usersHtml += " | " + user.industry;
					}
					usersHtml += "</div>";
				}
				if (user.aboutMe != undefined) {
					usersHtml = usersHtml + '<div class="ctnt-item-comment">' + user.aboutMe + '</div>';
				}

				var reviewCount = 0;
				if (user.reviewCount) {
					reviewCount = user.reviewCount;
				}

				var reviewScore = 0;
				if (user.reviewScore) {
					reviewScore = user.reviewScore;
				}

				usersHtml = usersHtml + '</div>';
				usersHtml = usersHtml + '<div class="float-right ctnt-list-item-btn-wrap clearfix pro-star-mar">' + '<div class="float-left ctnt-review-count" user="' + user.userId + '">';
				if (reviewCount == 0) {
					usersHtml += 'Write a review </div>';
				} else {
					usersHtml += reviewCount + ' Review(s) </div>'
				}
				usersHtml += '<div class="float-left ctnt-review-score" data-score="' + reviewScore + '"></div>' + '</div>';
				usersHtml = usersHtml + '</div>';
			});
			$('#ctnt-list-wrapper').html(usersHtml);

			$('.ctnt-review-score').each(function() {
				$(this).append(" - ");
				proRatingPattern($(this).attr("data-score"), $(this));

			});

			$(".ctnt-list-item").click(function(e) {
				var agentProfileName = $(this).attr("data-profilename");
				// var url = window.location.origin + "/pages" +
				// agentProfileName;
				var url = getLocationOrigin() + "/pages" + agentProfileName;
				window.open(url);
			});
		}
	}
}
function undefinedval(hierval) {
	if (hierval == undefined) {
		return "";
	} else if (hierval == "null") {
		return "";
	}
	return hierval;
}
function paintMappedUser(usersList) {
	if (usersList != undefined) {
		var usersSize = usersList.users.length;

		var untrack = "";
		if (usersSize > 0) {
			usersList.users.forEach(function(arrayItem) {

				untrack += '<div class="un-row">' + '						<div style="width:30%" class="float-left unmatchtab ss-name" title="' + undefinedval(arrayItem.firstName) + '">' + undefinedval(arrayItem.firstName) + '</div>' + ' <div class="hide ss-id" title="' + undefinedval(arrayItem.userId) + '">' + undefinedval(arrayItem.userId) + '</div>' + '						<div style="width:40%" class="float-left unmatchtab ss-eids" title="' + undefinedval(arrayItem.mappedEmails) + '">' + undefinedval(arrayItem.mappedEmails) + '</div>' + '						<div style="width:20%;color:#009FE0;" class="float-left unmatchtab v-mapped-edit cursor-pointer"></div>' + '						</div>';

			});

			$('#mapped').html(untrack);
			/* bindClickEventForMappedButton(); */
			editMap();
		}
	}
}
function editMap() {
	$('.v-mapped-edit').click(function(e) {
		disableBodyScroll();
		var id = parseInt($(this).parent().find(".ss-id").text());
		$('#current-user-id').val(id);
		var payload = {
			"agentId" : id
		};
		callAjaxGetWithPayloadData("./getemailmappingsforuser.do", editEmailMap, payload, true);
	});

}

// save email mappings ajax request running var
var isSaveEmailForUserRunning = false;

function editEmailMap(response) {
	var reponseJson = $.parseJSON(response);
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#mapped-email-info').append("No Data found");
	} else {

		var emailMap = "";
		var emails = reponseJson.length;
		if (emails > 0) {
			reponseJson.forEach(function(arrayEmail) {

				var status = arrayEmail.status;
				var statusDiv = "";
				var actionDiv = '';

				if (status == 0) {
					statusDiv = '<div style="width:15%" class="float-left unmatchtab email-status" status=' + arrayEmail.status + '>Inactive</div>';
					// actionDiv = '<div style="width:20%" class="float-left unmatchtab"><div class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-off" title="Inactive"></div></div>';
					actionDiv = '<div style="width:15%;height:38px;color: #009FE0;" class="float-left unmatchtab cursor-pointer" onclick="updateMappedEmail(' + undefinedval(arrayEmail.userEmailMappingId) + ',event , 1);">Active</div>';
				} else if (status == 1) {
					statusDiv = '<div style="width:15%" class="float-left unmatchtab email-status" status=' + arrayEmail.status + '>Active</div>';
					// actionDiv = '<div style="width:20%" class="float-left unmatchtab"><div class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-on" title="Active"></div></div>';
					actionDiv = '<div style="width:15%;height:38px;color: #009FE0;" class="float-left unmatchtab cursor-pointer" onclick="updateMappedEmail(' + undefinedval(arrayEmail.userEmailMappingId) + ',event , 0);">Inactive</div>';
				}

				emailMap += '<div id="user-email-row-' + arrayEmail.userEmailMappingId + '" class="un-row">' + '						<div style="width:40%" class="float-left unmatchtab email-id">' + undefinedval(arrayEmail.emailId) + '</div>' + statusDiv + '			<div style="width:25%" class="float-left unmatchtab email-created">' + undefinedval(arrayEmail.createdOn) + '</div>' + actionDiv + '</div>';
			});

			$("input", $("#email-form")).bind("keyup", function() {
				AdditionEvent()
			});
			$('#mapped-emil-info').html(emailMap);
			$('#email-map-pop-up').show();
			$('#email-map-cancel').click(function(e) {
				enableBodyScroll();
				$('#email-form')[0].reset();
				$('#email1').nextAll().remove();
				$('#email-map-add').show();
				$('#email-map-save').hide();
				$('#input-email').hide();
				$('#email-map-pop-up').hide();
				initializeMapped();
			});
			$('#email-map-add').click(function(e) {
				$('#email-map-add').hide();
				$('#email-map-save').show();
				$('#input-email').show();
			});

			// save button
			$('#email-map-save').click(function(e) {
				// check if ajax is already running
				enableBodyScroll();
				if (isSaveEmailForUserRunning)
					return;

				var emails = "";
				var isValidated = true;
				$("#new-email-wrapper :input").each(function() {
					var curEmail = $(this).val();
					if (curEmail != "") {
						if (emailRegex.test(curEmail) == true) {
							emails += curEmail;
							emails += ",";
						} else {
							$('#overlay-toast').html("Please enter valid email id : " + curEmail);
							showToast();
							isValidated = false;
							return;
						}
					}

				});
				if (emails == "" || isValidated == false)
					return;
				var eLast = emails.lastIndexOf(",");
				emails = emails.substring(0, eLast);
				var cureid = $('#current-user-id').val();
				var payload = {
					"emailIds" : emails,
					"agentId" : cureid
				};
				isSaveEmailForUserRunning = true;
				callAjaxPostWithPayloadData("./saveemailmappingsforuser.do", saveEmailMap, payload, true);
			});

		}
	}
}
function saveEmailMap(data) {
	// make false the ajax running function
	isSaveEmailForUserRunning = false;
	if (data == "Successfully added email address") {
		$('#email1').nextAll().remove();
		$('#email-form')[0].reset();
		$('#email-map-pop-up').hide();
		$('#email-map-add').show();
		$('#email-map-save').hide();
		$('#input-email').hide();
		initializeMapped();
	}
	$('#overlay-toast').html(data);
	showToast();
}
function updateMappedEmail(emailMapId, e, updatedStatus) {
	e.stopPropagation();
	confirmUpdateEmailMap(emailMapId, updatedStatus);
};
function confirmUpdateEmailMap(emailMapId, updatedStatus) {
	$('#overlay-main').show();
	$('#overlay-continue').show();
	if (updatedStatus == 0) {
		$('#overlay-continue').html("Inactive");
		$('#overlay-cancel').html("Cancel");
		$('#overlay-header').html("Inactive Email");
		$('#overlay-text').html("Are you sure you want to inactive the email ?");
	} else if (updatedStatus == 1) {
		$('#overlay-continue').html("Active");
		$('#overlay-cancel').html("Cancel");
		$('#overlay-header').html("Active Email");
		$('#overlay-text').html("Are you sure you want to active the email ?");
	}

	$('#overlay-continue').attr("onclick", "updateEmailMap(" + emailMapId + "," + updatedStatus + ");");
}
function updateEmailMap(id, status) {
	var url = "updateuseremailmapping.do?emailMappingId=" + id + "&status=" + status;
	callAjaxGET(url, function(data) {
		$('#overlay-main').hide();
		var response = $.parseJSON(data);
		if (response.succeed == true) {
			// repaint the data
			var id = $('#current-user-id').val();
			var payload = {
				"agentId" : id
			};
			callAjaxGetWithPayloadData("./getemailmappingsforuser.do", editEmailMap, payload, true);

			$('#overlay-toast').html(response.message);
			showToast();
		}
	}, false);
}
function AllInputsFilled() {
	return $("input[type='email']", $("#email-form")).filter(function() {
		return $(this).val().trim() === "";
	}).size() === 0;
}

function AdditionEvent() {
	if (AllInputsFilled()) {
		AddInput();
	}
}

function AddInput() {
	var cnt = $("input[type='email']", $("#email-form")).size() + 1;
	/* $("#email-form").children().last().append("<br><span style='padding:5px;'>Enter another email-id </span><input type='email' class='email-input-txt' placeholder='someone@example.com' name='email" + cnt+ "' id='email" + cnt+ "' /><span><img src='resources/images/close_srv.png' class='remove-email-input' onclick='bindRemoveEmailInput(this);'> </span>"); */
	$("#email-form").children().last().append("<br><span style='padding:5px;'>Enter another email-id </span><input type='email' class='email-input-txt' placeholder='someone@example.com' name='email" + cnt + "' id='email" + cnt + "' />");
	$("input", $("#email-form")).unbind("keyup").bind("keyup", function() {
		AdditionEvent()
	});

}
/*
 * $(document).on("input", '.email-input-txt', function() { var url="./populateemail.do"; callAjaxGET(url, function(data) { $('#input-email').after(data); }, true);
 * 
 * });
 */
function bindRemoveEmailInput(emailInput) {

	$(emailInput).parent().prev().prev().remove();
	$(emailInput).parent().prev().remove();
	/* $(emailInput).remove() */;
}

function paintUnmatchedUser(usersList) {
	if (usersList != undefined) {
		var usersSize = usersList.surveyPreInitiationList.length;

		var untrack = "";
		if (usersSize > 0) {
			usersList.surveyPreInitiationList.forEach(function(arrayItem) {

				var engagementClosedate = new Date(arrayItem.engagementClosedTime);
				var engagementClosedatePart = engagementClosedate.toDateString();
				untrack += '<div class="un-row">' + '						<div style="width:15%" class="float-left unmatchtab ss-id" title="' + undefinedval(arrayItem.agentName) + '">' + undefinedval(arrayItem.agentName) + '</div>' + '						<div style="width:20%" class="float-left unmatchtab ss-eid" title="' + undefinedval(arrayItem.agentEmailId) + '">' + undefinedval(arrayItem.agentEmailId) + '</div>' + '						<div style="width:30%" class="float-left unmatchtab ss-cname" title="' + undefinedval(arrayItem.customerFirstName) + '">' + undefinedval(arrayItem.customerFirstName) + '<span style="margin-left:2px;">' + undefinedval(arrayItem.customerLastName) + '</span> <br> <span style="margin-left:2px;" title="' + undefinedval(arrayItem.customerEmailId) + '"> < ' + undefinedval(arrayItem.customerEmailId) + ' > </span></div>' + '						<div style="width:15%" class="float-left unmatchtab ss-date" title="' + undefinedval(engagementClosedatePart) + '">' + undefinedval(engagementClosedatePart) + '</div>' + '						<div style="width:20%;color:#009FE0;" class="float-left unmatchtab ss-process cursor-pointer" >Process</div>' + '						</div>';

			});

			$('#new').html(untrack);
			bindClickEventForProcessButton();

		}
	}
}
function paintCorruptRecords(usersList) {
	if (usersList != undefined) {
		var usersSize = usersList.surveyPreInitiationList.length;

		var untrack = "";
		if (usersSize > 0) {
			usersList.surveyPreInitiationList.forEach(function(arrayItem) {
				var engagementClosedate = new Date(arrayItem.engagementClosedTime);
				var engagementClosedatePart = engagementClosedate.toDateString();
				untrack += '<div class="un-row">' + '						<div style="width:15%" class="float-left unmatchtab ss-id" title="' + undefinedval(arrayItem.agentName) + '">' + undefinedval(arrayItem.agentName) + '</div>' + '						<div style="width:20%" class="float-left unmatchtab ss-eid" title="' + undefinedval(arrayItem.agentEmailId) + '">' + undefinedval(arrayItem.agentEmailId) + '</div>' + '						<div style="width:30%" class="float-left unmatchtab ss-cname" title="' + undefinedval(arrayItem.customerFirstName) + '">' + undefinedval(arrayItem.customerFirstName) + '<span style="margin-left:2px;">' + undefinedval(arrayItem.customerLastName) + '</span> <br> <span style="margin-left:2px;" title="' + undefinedval(arrayItem.customerEmailId) + '"> < ' + undefinedval(arrayItem.customerEmailId) + ' > </span></div>' + '						<div style="width:15%" class="float-left unmatchtab ss-date" title="' + undefinedval(engagementClosedatePart) + '">' + undefinedval(engagementClosedatePart) + '</div>' + '						<div style="width:20%" class="float-left unmatchtab ss-date" title="' + undefinedval(arrayItem.errorCodeDescription) + '">' + undefinedval(arrayItem.errorCodeDescription) + '</div></div>';
			});

			$('#corrupt').html(untrack);
		}
	}
}
function paintProcessedUser(usersList) {
	if (usersList != undefined) {
		var usersSize = usersList.surveyPreInitiationList.length;

		var unprocess = "";
		if (usersSize > 0) {
			usersList.surveyPreInitiationList.forEach(function(arrayItem) {
				if (undefinedval(arrayItem.status) == 9) {
					var action = "Always Ignore";
				} else {

					var action = "Aliased";
					if (arrayItem.user != undefined && arrayItem.user.loginName != undefined && arrayItem.user.loginName != "")
						action += "<br><span title=" + arrayItem.user.loginName + ">" + arrayItem.user.loginName + "</span> "
				}

				var engagementClosedate = new Date(arrayItem.engagementClosedTime);
				var engagementClosedatePart = engagementClosedate.toDateString();
				unprocess += '<div class="un-row">' + '						<div style="width:15%" class="float-left unmatchtab ss-id" title="' + undefinedval(arrayItem.agentName) + '">' + undefinedval(arrayItem.agentName) + '</div>' + '						<div style="width:20%" class="float-left unmatchtab ss-eid" title="' + undefinedval(arrayItem.agentEmailId) + '">' + undefinedval(arrayItem.agentEmailId) + '</div>' + '						<div style="width:30%" class="float-left unmatchtab ss-cname" title="' + undefinedval(arrayItem.customerFirstName) + '">' + undefinedval(arrayItem.customerFirstName) + '<span style="margin-left:2px;">' + undefinedval(arrayItem.customerLastName) + '</span> <br> <span style="margin-left:2px;" title="' + undefinedval(arrayItem.customerEmailId) + '"> < ' + undefinedval(arrayItem.customerEmailId) + ' > </span></div>' + '						<div style="width:15%" class="float-left unmatchtab ss-date" title="' + undefinedval(engagementClosedatePart) + '">' + undefinedval(engagementClosedatePart) + '</div>' + '						<div style="width:20%" class="float-left unmatchtab" >' + action + '</div>' + '						</div>';

			});

			$('#processed').html(unprocess);

		}
	}
}
function bindClickEventForMappedButton() {
	$('.v-mapped-edit').click(function(e) {
		var id = $(this).parent().find("ss-id").text();
	});
	if ($('#is-ignore').val() == true) {
		if ($('#match-user-email').val() != "") {
			$('#match-user-email').val('');
			$('#match-user-email').attr('agent-id', 0);
			$('#match-user-email').attr("disabled");
		}
	}

}

function bindClickEventForProcessButton() {

	$('.ss-process').click(function(e) {
		var id = $(this).parent().find(".ss-id").text();
		var user = $(this).parent().find(".ss-eid").text();
		var customer = $(this).parent().find(".ss-cname").text();

		var popup = '<div class="bd-hr-form-item clearfix">' + '	     <div class="float-left bd-frm-left-un">ID</div>' + '	      <div class="float-left bd-frm-right-un" title="' + id + '">' + id + '</div>' + '	 </div>' + '	 <div class="bd-hr-form-item clearfix">' + '	     <div class="float-left bd-frm-left-un">User</div>' + '	      <div class="float-left bd-frm-right-un" title="' + user + '">' + user + '</div>' + '	 </div>' + '<div class="bd-hr-form-item clearfix">' + '	     <div class="float-left bd-frm-left-un">Customer</div>' + '	      <div class="float-left bd-frm-right-un" title="' + customer + '">' + customer + '</div>' + '	 </div><div class="bd-hr-form-item clearfix" id="ignore">' + '	     <div class="float-left bd-frm-left-un"></div>' + '	     <div class="float-left bd-frm-right">' + '	         <div class="bd-frm-check-wrapper clearfix bd-check-wrp">' + '	             <div class="float-left bd-check-img bd-check-img-checked"></div>' + '	             <input type="hidden" name="isIgnore" value="false" id="is-ignore" class="ignore-clear">' + '	             <div class="float-left bd-check-txt bd-check-sm">Always Ignore</div>' + '	         </div>' + '	     </div>' + '	 </div>' + '<div id="bd-single" class="bd-hr-form-item clearfix">' + '	    <div class="float-left bd-frm-left-un">Alias</div>' + '	    <div class="float-left bd-frm-left-un pos-relative">' + '	        <input id="match-user-email" class="bd-frm-rt-txt bd-dd-img">' + '	    </div>' + '	</div>';

		e.stopPropagation();
		$('#overlay-continue').html("Save");
		$('#overlay-cancel').html("Cancel");
		$('#overlay-header').html("Match User");
		$('#overlay-text').html(popup);

		$('#overlay-continue').click(function() {
			saveUserMap(user);
			$('#overlay-continue').unbind('click');
		});
		$('#overlay-main').show();
		bindAdminCheckBoxClick();
		attachAutocompleteAliasDropdown();
	});
	if ($('#is-ignore').val() == true) {
		if ($('#match-user-email').val() != "") {
			$('#match-user-email').val('');
			$('#match-user-email').attr('agent-id', 0);
			$('#match-user-email').attr("disabled");
		}
	}

}

var insaved = false;
function saveUserMap(aliasMail) {
	if (insaved == true) {
		return;
	}
	var isIgnore = $('#is-ignore').val();
	var agentId = $('#match-user-email').attr('agent-id');

	if (isIgnore == 'true') {
		agentId = 0;
	} else {
		if (agentId == undefined || agentId <= 0) {
			$('#overlay-toast').html('Please enter valid alias!');
			showToast();
			return;
		}

	}

	insaved == true;
	var payload = {
		"emailAddress" : aliasMail,
		"agentId" : agentId,
		"ignoredEmail" : isIgnore

	};

	isAjaxRequestRunningProcessedUser = true;

	callAjaxGetWithPayloadData('./saveemailmapping.do', function(data) {
		insaved == false;
		$('#overlay-main').hide();
		$('#overlay-toast').html(data);
		showToast();

		initializeUnmatchedUserPage();
		initializeProcesedUserPage();

	}, payload, true);

}

/**
 * Function to fetch the users by profile level in pro list page
 * 
 * @param iden
 * @param profileLevel -
 *            office/region/company
 * @param startIndex
 */
function fetchUsersByProfileLevel(iden, profileLevel, startIndex) {
	if (iden == undefined) {
		return;
	}
	var url = getLocationOrigin() + "/rest/profile/individuals/" + iden + "?profileLevel=" + profileLevel + "&start=" + startIndex;
	callAjaxGET(url, function(data) {
		var response = $.parseJSON(data);
		if (response != undefined) {
			paginateUsersProList(response.entity);
		}
	}, false);
}

// Function to adjust image width
function adjustTextContainerWidthOnResize() {
	var parentWidth = $('.ctnt-list-item').width();
	var imgWidth = $('.ctnt-list-item .ctnt-list-item-img').width();
	var textContainerWidth = parentWidth - imgWidth - 35;
	/* $('.ctnt-list-item .ctnt-list-item-txt-wrap').width(textContainerWidth); */
}

// Function to validate the first name pattern
function validateProFirstNamePattern(elementId) {
	if ($('#' + elementId).val() != "") {
		if (firstNamePatternRegex.test($('#' + elementId).val().trim()) == true) {
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
		if (lastNamePatternRegEx.test($('#' + elementId).val().trim()) == true) {
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

// Functions for take survey module
function checkCharacterLimit(element) {
	$('#toast-container').hide();
	if (element.value.length >= 500) {
		$('#overlay-toast').html('Maximum charter limit 500');
		showToast();
	}
}

// Resize function for take survey page
function resizeFuncTakeSurveyPage() {
	var winW = getWindowWidth();
	var winH = getWindowHeight();
	if (winW < 768) {
		minH = winH - 50 - 50 - 5 - 1;
	} else {
		minH = winH - 80 - 78 - 78 - 1;
	}
	$('.min-height-container').css('min-height', minH + 'px');
}

function initializeTakeSurveyPage() {
	resizeFuncTakeSurveyPage();
	$(window).resize(function() {
		resizeFuncTakeSurveyPage();
	});

	$("div[data-ques-type]").hide();
	var q = $('#prof-container').attr("data-q");

	if (q != undefined && q != "") {
		initSurveyWithUrl(q);
	} else {
		var agentId = $('#prof-container').attr("data-agentid");
		$("div[data-ques-type='user-details']").show();
		loadAgentPic(agentId);
	}

	$('.sq-pts-red').hover(function() {
		$('.pts-hover-1').show();
	}, function() {
		$('.pts-hover-1').hide();
	});

	$('.sq-pts-org').hover(function() {
		$('.pts-hover-2').show();
	}, function() {
		$('.pts-hover-2').hide();
	});

	$('.sq-pts-lgreen').hover(function() {
		$('.pts-hover-3').show();
	}, function() {
		$('.pts-hover-3').hide();
	});

	$('.sq-pts-military').hover(function() {
		$('.pts-hover-4').show();
	}, function() {
		$('.pts-hover-4').hide();
	});

	$('.sq-pts-dgreen').hover(function() {
		$('.pts-hover-5').show();
	}, function() {
		$('.pts-hover-5').hide();
	});

	$('.st-checkbox-on').click(function() {
		$(this).hide();
		$(this).parent().find('.st-checkbox-off').show();
	});

	$('.st-checkbox-off').click(function() {
		$(this).hide();
		$(this).parent().find('.st-checkbox-on').show();
	});

	$('#cust-agent-verify').on('click', function() {
		if ($(this).hasClass('bd-check-img-checked')) {
			$(this).removeClass('bd-check-img-checked');
		} else {
			$(this).addClass('bd-check-img-checked');
		}
	});
}

function initializeSurveyFormPage() {
	resizeFuncTakeSurveyPage();
	$(window).resize(function() {
		resizeFuncTakeSurveyPage();
	});

	if ($('#message').val() != "") {
		if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
			showError($('#message').val());
		} else {
			showInfo($('#message').val());
		}
	}

	$('#cust-agent-verify').on('click', function() {
		if ($(this).hasClass('bd-check-img-checked')) {
			$(this).removeClass('bd-check-img-checked');
		} else {
			$(this).addClass('bd-check-img-checked');
		}
	});

	$('#start-btn').click(function() {

		// Check if the form is valid
		if (!validateSurveyForm()) {
			return;
		}
		firstName = $('#firstName').val().trim();
		lastName = $('#lastName').val().trim();
		var email = $('#email').val().trim();
		var grecaptcharesponse = $('#g-recaptcha-response').val();
		var agentId = $('#prof-container').attr("data-agentId");
		var agentName = $('#prof-container').attr("data-agentName");
		initSurvey(firstName, lastName, email, agentId, agentName, grecaptcharesponse);

		// Google analytics for reviews
		ga('send', {
			'hitType' : 'event',
			'eventCategory' : 'review',
			'eventAction' : 'click',
			'eventLabel' : 'Reviews',
			'eventValue' : agentId
		});
	});
}

/*
 * Function to initiate survey. It hits controller to get list of all the questions which are shown one after one to the customer.
 */
function initSurvey(firstName, lastName, email, agentId, agentName, grecaptcharesponse) {
	this.agentId = agentId;
	this.agentName = agentName;
	customerEmail = email;

	$('input[g-recaptcha-response]').val(grecaptcharesponse);

	if ($('#cust-agent-verify').hasClass('bd-check-img-checked')) {
		$('#overlay-toast').html("Verify that you have done business with the agent");
		showToast();
		return false;
	}

	$('#survey-request-form').submit();
}

// Validate survey form
function validateSurveyForm() {
	if (!validateUserFirstName('firstName')) {
		$('#overlay-toast').html('Please enter valid First Name!');
		showToast();
		return false;
	}
	if (!validateUserEmailId('email')) {
		$('#overlay-toast').html('Please enter valid Email Id!');
		showToast();
		return false;
	}

	var agentEmail = $('#prof-container').attr("data-agent-email");
	var email = $('#email').val().trim();
	if (agentEmail.toUpperCase() == email.toUpperCase()) {
		$('#overlay-toast').html('Agents can not take survey for themselves!');
		showToast();
		return false;
	}
	return true;
}

// Function to validate the first name
function validateUserFirstName(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (nameRegex.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid first name.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('please enter first name.');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (nameRegex.test($('#' + elementId).val()) == true) {
				$('#' + elementId).next('.input-error-2').hide();
				return true;
			} else {
				$('#' + elementId).next('.input-error-2').html('Please enter a valid first name.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			$('#' + elementId).next('.input-error-2').html('Please enter first name.');
			$('#' + elementId).next('.input-error-2').show();
			return false;
		}
	}
}

// Function to validate the last name
function validateUserLastName(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (lastNameRegEx.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid last name.');
				showToast();
				return false;
			}
		} else {
			return true;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (lastNameRegEx.test($('#' + elementId).val()) == true) {
				$('#' + elementId).next('.input-error-2').hide();
				return true;
			} else {
				$('#' + elementId).next('.input-error-2').html('Please enter a valid last name.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			return true;
		}
	}
}

// Function to validate email id in a form
function validateUserEmailId(elementId) {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() != "") {
			if (emailRegex.test($('#' + elementId).val()) == true) {
				return true;
			} else {
				$('#overlay-toast').html('Please enter a valid email id.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('Please enter email id.');
			showToast();
			return false;
		}
	} else {
		if ($('#' + elementId).val() != "") {
			if (emailRegex.test($('#' + elementId).val()) == true) {
				$('#' + elementId).next('.input-error-2').hide();
				return true;
			} else {
				$('#' + elementId).next('.input-error-2').html('Please enter a valid email id.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			$('#' + elementId).next('.input-error-2').html('Please enter email id.');
			$('#' + elementId).next('.input-error-2').show();
			return false;
		}
	}
}

// Functions for complete registration page
function initializeCompleteRegistrationPage() {

	// show error message on page load
	if ($('#message').val() != "") {
		showError($('#message').val());
	}

	$('#comp-reg-submit').click(function(e) {
		submitCompleteRegistrationForm();
	});

	$('input').keypress(function(e) {
		// detect enter
		if (e.which == 13) {
			e.preventDefault();
			submitCompleteRegistrationForm();
		}
	});

	$('#complete-reg-fname').blur(function() {
		if (validateFirstName(this.id)) {
			hideError();
		}
	});

	$('#complete-reg-lname').blur(function() {
		if (validateLastName(this.id)) {
			hideError();
		}
	});

	$('#complete-reg-user-id').blur(function() {
		if (validateEmailId(this.id)) {
			hideError();
		}
	});

	$('#complete-reg-pwd').blur(function() {
		if (validatePassword(this.id)) {
			hideError();
		}
	});

	$('#complete-reg-cnf-pwd').blur(function() {
		if (validateConfirmPassword('complete-reg-pwd', this.id)) {
			hideError();
		}
	});
}

function validateCompleteRegistrationForm() {
	if (!validateFirstName('complete-reg-fname')) {
		$('#complete-reg-fname').focus();
		return false;
	}
	if (!validateLastName('complete-reg-lname')) {
		$('#complete-reg-lname').focus();
		return false;
	}
	if (!validateEmailId('complete-reg-user-id')) {
		$('#complete-reg-user-id').focus();
		return false;
	}
	if (!validatePassword('complete-reg-pwd')) {
		$('#complete-reg-pwd').focus();
		return false;
	}
	if (!validateConfirmPassword('complete-reg-pwd', 'complete-reg-cnf-pwd')) {
		$('#complete-reg-cnf-pwd').focus();
		return false;
	}
	return true;
}

function submitCompleteRegistrationForm() {
	if (validateCompleteRegistrationForm()) {
		$('#complete-registration-form').submit();
	}
}

// Function for user registration page when a company is registered
function submitRegistrationPageForm() {
	if (validateRegistrationpageForm('reg-form')) {
		$('#registration-form').submit();
		showOverlay();
	}
}

function validateRegistrationpageForm(id) {
	// Validate form input elements
	if (!validateFirstName('reg-fname')) {
		$('#reg-fname').focus();
		return false;
	}
	if (!validateLastName('reg-lname')) {
		$('#reg-lname').focus();
		return false;
	}
	if (!validateEmailId('reg-email')) {
		$('#reg-email').focus();
		return false;
	}
	if (!validatePassword('reg-pwd')) {
		$('#reg-pwd').focus();
		return false;
	}
	if (!validateConfirmPassword('reg-pwd', 'reg-conf-pwd')) {
		$('#reg-conf-pwd').focus();
		return false;
	}
	return true;
}

function initializeUserCompanyRegistrationPage() {
	if ($('#message').val() != "") {
		showError($('#message').val());
	}

	$('#reg-submit').click(function(e) {
		submitRegistrationPageForm();
	});

	$('input').keypress(function(e) {
		if (e.which == 13) {
			e.preventDefault();
			submitRegistrationPageForm();
		}
	});

	// Functions to trigger form validation of various input elements
	$('#reg-fname').blur(function() {
		if (validateFirstName(this.id)) {
			hideError();
		}
	});

	$('#reg-lname').blur(function() {
		if (validateLastName(this.id)) {
			hideError();
		}
	});

	$('#reg-email').blur(function() {
		if (validateEmailId(this.id)) {
			hideError();
		}
	});

	$('#reg-pwd').blur(function() {
		if (validatePassword(this.id)) {
			hideError();
		}
	});

	$('#reg-conf-pwd').blur(function() {
		if (validateConfirmPassword('reg-pwd', this.id)) {
			hideError();
		}
	});
}

var hierarchyUpload = {
	verified : false,
	canUpload : false,
	hierarchyJson : {},
	uploadType : "append",
	hierundefined : function(hierval) {
		if (hierval == undefined) {
			return "";
		}
		return hierval;
	},
	reverseUndef : function(hierval) {
		if (hierval == null || hierval == '' || hierval.trim() == '') {
			return undefined;
		}
		return hierval.trim();
	},

	// Method that adds blue text colour to modified records
	isModified : function(value) {
		if (value == true) {
			return "color-blue";
		} else {
			return "";
		}

	},
	getStatusCall : undefined,

	fileUpload : function() {
		$('#com-file').change(function() {
			$('#summary').hide();
			$('#uploadBatchStatus').empty();
			$('#xlsx-file-upload').addClass('disable');
			var fileAdd = $(this).val().split('\\');
			$('#com-xlsx-file').val(fileAdd[fileAdd.length - 1]);
			if (hierarchyUpload.fileValidate("#com-file")) {
				hierarchyUpload.verified = true;
			}

			if (hierarchyUpload.verified == false) {
				$('#xlsx-file-verify').addClass('disable');
				showError("Please upload xlsx file");
			}

			if (hierarchyUpload.verified == true) {
				hierarchyUpload.canUpload = true;
				hierarchyUpload.verified = false;
				$('#xlsx-file-verify').removeClass('disable');

				var formData = new FormData();
				formData.append("file", $('#com-file').prop("files")[0]);
				formData.append("filename", $('#com-file').prop("files")[0].name);
				showOverlay();
				callAjaxPOSTWithTextDataLogo("./savexlsxfile.do", hierarchyUpload.saveXlxsSuccessCallback, true, formData);
			}

			else {
				$('#xlsx-file-verify').addClass('disable');
				showError("Please select a valid file");
			}
		});

		$('#xlsx-file-verify').click(function() {
			$('#summary').hide();
			if (hierarchyUpload.hierarchyJson.upload != null && hierarchyUpload.hierarchyJson.upload.isModifiedFromUI) {
				var formData = new FormData();
				formData.append("hierarchyJson", JSON.stringify(hierarchyUpload.hierarchyJson));
				formData.append("uploadType", hierarchyUpload.uploadType);
				showOverlay();
				callAjaxPOSTWithTextDataUpload("./verifyxHierarchyUpload.do", hierarchyUpload.uploadXlxsSuccessCallback, true, formData);
			} else {
				var url = $("#fileUrl").val();
				if (url == undefined || url == '') {
					$('#xlsx-file-verify').addClass('disable');
					showError("Please upload a valid file");
				} else {
					var formData = new FormData();
					formData.append("fileUrl", url);
					formData.append("uploadType", hierarchyUpload.uploadType);
					showOverlay();
					callAjaxPOSTWithTextDataUpload("./verifyxlsxfile.do", hierarchyUpload.uploadXlxsSuccessCallback, true, formData);
				}
			}
		});

		$('#xlsx-file-upload').click(function() {
			if (hierarchyUpload.canUpload == true) {
				var formData = new FormData();
				formData.append("hierarchyJson", JSON.stringify(hierarchyUpload.hierarchyJson));
				showOverlay();
				callAjaxPOSTWithTextDataUpload("./putxlsxfileinbatch.do", hierarchyUpload.saveXlxsDataSuccessCallback, true, formData);
				hierarchyUpload.canUpload = false;
			} else {
				showError("File is not verified");
			}
			hierarchyUpload.hierarchyJson = {};

		});
		$('#icn-xlsxfile').click(function() {
			$('#com-file').trigger('click');
		});

		$('.bd-cust-rad-img').click(function(e) {
			$('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
			$(this).toggleClass('bd-cust-rad-img-checked');
			hierarchyUpload.uploadType = $(this).data('type');
		});
	},

	saveXlxsDataSuccessCallback : function(response) {
		if (!response) {
			showError(response);
		} else {
			var jsonResponse = $.parseJSON(response);
			if (!jsonResponse.status) {
				showError(jsonResponse.response);
			} else {
				showInfo(jsonResponse.response);
				// start fetching batch status
				callAjaxPOSTWithTextDataUpload("./fetchUploadBatchStatus.do", hierarchyUpload.fetchUploadBatchStatusCallback, true, null);
			}
		}
		$('#com-file').val('');
		$('#com-xlsx-file').val('');
		$('#fileUrl').val('');
		$('#summary').hide();
		$('#xlsVerifyUplaod').addClass('disable');
		$('#xlsx-file-upload').addClass('disable');
	},

	fetchUploadBatchStatusCallback : function(response) {
		$(window).on('hashchange', function() {
			if (hierarchyUpload.getStatusCall != undefined) {
				hierarchyUpload.getStatusCall.abort();
			}
		});
		$('#lastUploadRunTimestamp').hide();
		if (!response) {
			$('#com-file').val('');
			$('#com-xlsx-file').val('');
			$('#fileUrl').val('');
			showError(response);
		} else {
			var jsonResponse = $.parseJSON(response);
			if (!jsonResponse.status) {
				showError(jsonResponse.response);
			} else {
				// If not complete/error, keep making a request every 15 seconds
				if (!(jsonResponse.uploadStatus == 4 || jsonResponse.uploadStatus == 5 || jsonResponse.uploadStatus == 6 || jsonResponse.uploadStatus == -1)) {
					$('#uploadBatchStatus').empty();
					jsonResponse.response.forEach(function(uploadStatus) {
						$('<div>' + uploadStatus.message + '</div>').appendTo('#uploadBatchStatus');
					});
					$('#uploadBatchStatus').show();
					showLoaderOnPagination($('#uploadBatchStatus'));
					setTimeout(function() {
						hierarchyUpload.getStatusCall = callAjaxPOSTWithTextDataUpload("./fetchUploadBatchStatus.do", hierarchyUpload.fetchUploadBatchStatusCallback, true, null);
					}, 2000);
				} else {
					if (response != undefined && response != null && response != '' && jsonResponse.response != []) {
						$('#uploadBatchStatus').empty();
						jsonResponse.response.forEach(function(uploadStatus) {
							if (jsonResponse.uploadStatus != 6) {
								$('<div>' + uploadStatus.message + '</div>').appendTo('#uploadBatchStatus');
							}
						});
						$('#uploadBatchStatus').show();
					}
					hideLoaderOnPagination($('#uploadBatchStatus'));
					$('#xlsVerifyUplaod').removeClass('disable');
					if (jsonResponse.lastUploadRunTimestamp != null && jsonResponse.lastUploadRunTimestamp != '') {
						$('#lastUploadRunTimestamp').text('Last Imported On: ' + jsonResponse.lastUploadRunTimestamp);
						$('#lastUploadRunTimestamp').show();
					}
				}
			}
		}
	},

	uploadXlxsSuccessCallback : function(response) {
		if (!response) {
			$('#com-file').val('');
			$('#com-xlsx-file').val('');
			$('#fileUrl').val('');
			showError(response);
		} else {
			var jsonResponse = $.parseJSON(response);
			if (!jsonResponse.status) {
				showError(jsonResponse.response);
			} else {
				hierarchyUpload.hierarchyJson = jsonResponse.response;
				$('#region-added').empty();
				if (hierarchyUpload.hierarchyJson.numberOfRegionsAdded != 0) {
					$('<div style="color:#009FE0;">Regions added:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfRegionsAdded) + '</div>').appendTo('#region-added');
					$('#region-added').show();
				}
				$('#region-modified').empty();
				if (hierarchyUpload.hierarchyJson.numberOfRegionsModified != 0) {
					$('<div style="color:#009FE0;">Regions modified:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfRegionsModified) + '</div>').appendTo('#region-modified');
					$('#region-modified').show();
				}
				$('#region-deleted').empty();
				if (hierarchyUpload.hierarchyJson.numberOfRegionsDeleted != 0) {
					$('<div style="color:#009FE0;">Regions deleted:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfRegionsDeleted) + '</div>').appendTo('#region-deleted');
					$('#region-deleted').show();
				}
				$('#branch-added').empty();
				if (hierarchyUpload.hierarchyJson.numberOfBranchesAdded != 0) {
					$('<div style="color:#009FE0;">Offices added:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfBranchesAdded) + '</div>').appendTo('#branch-added');
					$('#branch-added').show();
				}
				$('#branch-modified').empty();
				if (hierarchyUpload.hierarchyJson.numberOfBranchesModified != 0) {
					$('<div style="color:#009FE0;">Offices modified:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfBranchesModified) + '</div>').appendTo('#branch-modified');
					$('#branch-modified').show();
				}
				$('#branch-deleted').empty();
				if (hierarchyUpload.hierarchyJson.numberOfBranchesDeleted != 0) {
					$('<div style="color:#009FE0;">Offices deleted:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfBranchesDeleted) + '</div>').appendTo('#branch-deleted');
					$('#branch-deleted').show();
				}
				$('#user-added').empty();
				if (hierarchyUpload.hierarchyJson.numberOfUsersAdded != 0) {
					$('<div style="color:#009FE0;">Users added:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfUsersAdded) + '</div>').appendTo('#user-added');
					$('#user-added').show();
				}
				$('#user-modified').empty();
				if (hierarchyUpload.hierarchyJson.numberOfUsersModified != 0) {
					$('<div style="color:#009FE0;">Users modified:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfUsersModified) + '</div>').appendTo('#user-modified');
					$('#user-modified').show();
				}
				$('#user-deleted').empty();
				if (hierarchyUpload.hierarchyJson.numberOfUsersDeleted != 0) {
					$('<div style="color:#009FE0;">Users deleted:' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.numberOfUsersDeleted) + '</div>').appendTo('#user-deleted');
					$('#user-deleted').show();
				}
				var regionlength = hierarchyUpload.hierarchyJson.upload.regions.length;
				if (regionlength != 0) {
					$('#region-upload').empty();
					for (var i = 0; i < regionlength; i++) {
						if (hierarchyUpload.hierarchyJson.upload.regions[i].isInAppendMode == true || hierarchyUpload.uploadType == 'replace') {
							if (hierarchyUpload.hierarchyJson.upload.regions[i].isRegionAdded == true) {
								var status = 'lgn-col-item hier-col-green';

							} else if (hierarchyUpload.hierarchyJson.upload.regions[i].isDeletedRecord == true) {
								var status = 'lgn-col-item hier-col-red';
							} else if (hierarchyUpload.hierarchyJson.upload.regions[i].isRegionModified == true) {
								var status = 'lgn-col-item hier-col-blue';
							} else {
								var status = '';
							}

							var regionEdit = '<div id="hier-address-edit-container"' + '	class="prof-user-address prof-edit-icn">' + '' + '	<form id="hier-region-form">' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Region Name</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" name="regionName" id="hier-name-txt-' + i + '"' + '					value=" ' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionName) + ' ">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Address 1</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" name="regionAddress1"' + '					id="hier-address1-txt-' + i + '" value=" ' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress1) + ' ">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Address 2</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-address2-txt-' + i + '"' + '					name="regionAddress2" value=" ' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress2) + ' ">' + '			</div>' + '		</div>' + '' + '		<div id="hier-state-city-row" class="hide" style="display: block;">' + '			<div class="bd-hr-form-item clearfix">' + '				<div class="float-left bd-frm-left">State</div>' + '				<div class="float-left bd-frm-right">' + '					<select class="bd-frm-rt-txt" id="hier-state-txt-' + i + '"' + '						name="regionState" data-value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionState) + '">' + '						<option disabled selected>Select State</option>' + '					</select>' + '				</div>' + '			</div>' + '			<div class="bd-hr-form-item clearfix">' + '				<div class="float-left bd-frm-left">City</div>' + '				<div class="float-left bd-frm-right">' + '					<input class="bd-frm-rt-txt" id="hier-city-txt-' + i + '" name="regionCity"' + '						value=" ' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionCity) + ' ">' + '				</div>' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Zip Code</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-zipcode-txt-' + i + '"' + '					name="regionZipcode" value=" ' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionZipcode) + ' ">' + '			</div>' + '		</div>' + '' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left"></div>' + '			<div class="float-left bd-frm-right">' + '				<div id="btn-region-update-' + i + '" onClick = "hierarchyUpload.saveEdittedRegion(' + i + ')" class="bd-btn-save cursor-pointer">Save</div>' + '' + '			</div>' + '		</div>' + '	</form>' + '' + '</div>';

							$('<tr style="height:35px;"><td><span class="' + status + '"></span></td><td id="editRegion-' + i + '"  class="v-hiararchy-edit" title="Edit" onClick="hierarchyUpload.editRegion(' + i + ')"></td><td>' + hierarchyUpload.addToolTip(hierarchyUpload.hierarchyJson.upload.regions[i]) + '</td><td><div class="hier-upload-td">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].rowNum) + '</div></td><td><div class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.regions[i].isSourceRegionIdModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].sourceRegionId) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].sourceRegionId) + ' </div></td><td><div id="regionName-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.regions[i].isRegionNameModified) + '" title="'

							+ hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionName) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionName) + '</div></td><td><div id="regionAddress1-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.regions[i].isRegionAddress1Modified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress1) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress1) + '</div></td><td><div id="regionAddress2-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.regions[i].isRegionAddress2Modified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress2) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress2) + '</div></td><td><div id="regionCity-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.regions[i].isRegionCityModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionCity) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionCity) + '</div></td><td><div id="regionState-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.regions[i].isRegionStateModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionState) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionState) + '</div></td><td><div id="regionZipcode-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.regions[i].isRegionZipcodeModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionZipcode) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionZipcode) + '</div></td></tr><tr class="hide hier-region-edit" style="background-color: #F9F9FB;" ><td colspan="9">' + regionEdit + '</td></tr>').appendTo('#region-upload');

							if (hierarchyUpload.hierarchyJson.upload.regions[i].isDeletedRecord == true) {
								$('#editRegion-' + i).addClass('disable');
							}
						}
					}
					$('#region-sum-btn').show();
					$('#summary').show();
				}

				var branchlength = hierarchyUpload.hierarchyJson.upload.branches.length;
				if (branchlength != 0) {
					$('#branch-upload').empty();
					for (var i = 0; i < branchlength; i++) {
						if (hierarchyUpload.hierarchyJson.upload.branches[i].isInAppendMode == true || hierarchyUpload.uploadType == 'replace') {
							if (hierarchyUpload.hierarchyJson.upload.branches[i].isBranchAdded == true) {
								var status = 'lgn-col-item hier-col-green';

							} else if (hierarchyUpload.hierarchyJson.upload.branches[i].isDeletedRecord == true) {
								var status = 'lgn-col-item hier-col-red';

							} else if (hierarchyUpload.hierarchyJson.upload.branches[i].isBranchModified == true) {
								var status = 'lgn-col-item hier-col-blue';
							} else {
								var status = '';
							}

							var branchEdit = '<div id="hier-branch-address-edit-container"' + '	class="prof-user-address prof-edit-icn">' + '' + '	<form id="hier-branch-form">' + '	<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Branch Name</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" name="branchName" id="hier-branch-name-txt-' + i + '"' + '					value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchName) + '">' + '			</div>' + '		</div>' + '		' + '	<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Region Id</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" name="branchName" id="hier-branch-sourceRegionId-txt-' + i + '"' + '					value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].sourceRegionId) + '">' + '			</div>' + '		</div>' + '		' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Address 1</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" name="branchAddress1"' + '					id="hier-branch-address1-txt-' + i + '" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress1) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Address 2</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-branch-address2-txt-' + i + '"' + '					name="branchAddress2" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress2) + '">' + '			</div>' + '		</div>' + '' + '		<div id="hier-branch-state-city-row" class="hide" style="display: block;">' + '			<div class="bd-hr-form-item clearfix">' + '				<div class="float-left bd-frm-left">State</div>' + '				<div class="float-left bd-frm-right">' + '					<select class="bd-frm-rt-txt" id="hier-branch-state-txt-' + i + '"' + '						name="branchState" data-value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchState) + '">' + '						<option disabled selected>Select State</option>' + '					</select>' + '				</div>' + '			</div>' + '			<div class="bd-hr-form-item clearfix">' + '				<div class="float-left bd-frm-left">City</div>' + '				<div class="float-left bd-frm-right">' + '					<input class="bd-frm-rt-txt" id="hier-branch-city-txt-' + i + '" name="branchCity"' + '						value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchCity) + '">' + '				</div>' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Zip Code</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-branch-zipcode-txt-' + i + '"' + '					name="branchZipcode" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchZipcode) + '">' + '			</div>' + '		</div>' + '' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left"></div>' + '			<div class="float-left bd-frm-right">' + '				<div id="btn-branch-update-' + i + '" class="bd-btn-save cursor-pointer" onClick="hierarchyUpload.saveEdittedBranch(' + i + ')">Save</div>' + '' + '			</div>' + '		</div>' + '	</form>' + '' + '</div>';

							$('<tr style=";height:35px;"><td><span class="' + status + '"></span></td><td class="v-hiararchy-edit" title="Edit" id="editBranch-' + i + '" onClick="hierarchyUpload.editBranch(' + i + ')"></td><td>' + hierarchyUpload.addToolTip(hierarchyUpload.hierarchyJson.upload.branches[i]) + '</td><td><div class="hier-upload-td">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].rowNum) + '</div></td><td><div class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isSourceBranchIdModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].sourceBranchId) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].sourceBranchId) + '</div></td><td><div id="branchName-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isBranchNameModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchName) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchName) + '</div></td><td><div id="sourceRegionId-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isSourceRegionIdModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].sourceRegionId) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].sourceRegionId) + '</div></td><td><div id="branchAddress1-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isBranchAddress1Modified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress1) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress1) + '</div></td><td><div id="branchAddress2-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isBranchAddress2Modified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress2) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress2) + '</div></td><td><div id="branchCity-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isBranchCityModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchCity) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchCity) + '</div></td><td><div id="branchState-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isBranchStateModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchState) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchState) + '</div></td><td><div id="branchZipcode-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.branches[i].isBranchZipcodeModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchZipcode) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchZipcode) + '</div></td></tr><tr class="hide hier-branch-edit" style="background-color: #F9F9FB;" ><td colspan="10">' + branchEdit + '</td></tr>').appendTo('#branch-upload');

							if (hierarchyUpload.hierarchyJson.upload.branches[i].isDeletedRecord == true) {
								$('#editBranch-' + i).addClass('disable');
							}
						}
					}
					$('#branch-sum-btn').show();
					if (regionlength == 0) {
						$('#branch-sum-btn').addClass('active');
						$('#upload-summary-region').removeClass('active in');
						$('#upload-summary-branch').addClass('active in');
						$('#summary').show();
					}

				}

				var userlength = hierarchyUpload.hierarchyJson.upload.users.length;
				if (userlength != 0) {
					$('#user-upload').empty();
					var userlength = hierarchyUpload.hierarchyJson.upload.users.length;
					for (var i = 0; i < userlength; i++) {
						if (hierarchyUpload.hierarchyJson.upload.users[i].isInAppendMode == true || hierarchyUpload.uploadType == 'replace') {
							if (hierarchyUpload.hierarchyJson.upload.users[i].isUserAdded == true) {
								var status = 'lgn-col-item hier-col-green';

							} else if (hierarchyUpload.hierarchyJson.upload.users[i].isDeletedRecord == true) {
								var status = 'lgn-col-item hier-col-red';

							} else if (hierarchyUpload.hierarchyJson.upload.users[i].isUserModified == true) {
								var status = 'lgn-col-item hier-col-blue';
							} else {
								var status = '';
							}

							var sendMailCode = "";

							if ((!hierarchyUpload.hierarchyJson.upload.users[i].isUserVerified && !hierarchyUpload.hierarchyJson.upload.users[i].isDeletedRecord) || hierarchyUpload.hierarchyJson.upload.users[i].isUserAdded) {
								// Add checkbox only for users who aren't
								// verified
								// and new users
								var sendMailCode = "<div id ='send-mail-" + i + "' class='send-mail-check-img ";
								if (!hierarchyUpload.hierarchyJson.upload.users[i].sendMail) {
									sendMailCode += "bd-check-img-checked"
								}
								sendMailCode += "'onClick='hierarchyUpload.toggleSendMail(" + i + ")'></div>";
							}

							var userEdit = '<div id="hier-user-detail-edit-container"' + '	class="prof-user-address prof-edit-icn">' + '' + '	<form id="hier-user-form">' + '	<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">First Name</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" name="userFirstname" id="hier-user-firstName-txt-' + i + '"' + '					value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].firstName) + '">' + '			</div>' + '		</div>' + '		' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Last Name</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" name="user-lastname"' + '					id="hier-user-lastName-txt-' + i + '" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].lastName) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Title</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-title-txt-' + i + '"' + '					name="userTitle" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].title) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Office Assignment(s)</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-assignedBranches-txt-' + i + '"' + '					name="userBranchAssignment" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranches) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Region Assignment(s)</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-assignedRegions-txt-' + i + '"' + '					name="userRegionAssignment" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegions) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Office Admin Privilege(s)</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-assignedBranchesAdmin-txt-' + i + '"' + '					name="userBranchPrivilege" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranchesAdmin) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Region Admin Privilege</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-assignedRegionsAdmin-txt-' + i + '"' + '					name="userRegionPrivilege" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegionsAdmin) + '">' + '			</div>' + '		</div>' + '		' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Email</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-emailId-txt-' + i + '"' + '					name="userEmail" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].emailId) + '">' + '			</div>' + '		</div>' + '		' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Phone</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-phoneNumber-txt-' + i + '"' + '					name="userPhone" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].phoneNumber) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Website</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-websiteUrl-txt-' + i + '"' + '					name="userWebsite" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].websiteUrl) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">License</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-license-txt-' + i + '"' + '					name="userLicense" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].license) + '">' + '			</div>' + '		</div>' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Legal Disclaimer</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-legalDisclaimer-txt-' + i + '"' + '					name="userLegalDisclaimer" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].legalDisclaimer) + '">' + '			</div>' + '		</div>' + '' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">Photo</div>' + '			<div class="float-left bd-frm-right">' + '				<input class="bd-frm-rt-txt" id="hier-user-userPhotoUrl-txt-' + i + '"' + '					name="userPhoto" value="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].userPhotoUrl) + '">' + '			</div>' + '		</div>' + '' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left">About Me Description</div>' + '			<div class="float-left bd-frm-right">' + '				<textarea class="hier-upload-txt-area" id="hier-user-aboutMeDescription-txt-' + i + '" name="userAboutMeDescription">' + escapeHtml(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].aboutMeDescription)) + '</textarea>' + '			</div>' + '		</div>' + '' + '		<div class="bd-hr-form-item clearfix">' + '			<div class="float-left bd-frm-left"></div>' + '			<div class="float-left bd-frm-right">' + '				<div id="btn-user-update-' + i + '" class="bd-btn-save cursor-pointer" onClick="hierarchyUpload.saveEdittedUser(' + i + ')">Save</div>' + '			</div>' + '		</div>' + '	</form>' + '' + '</div>';

							$('<tr style="height:35px;"><td><span class="' + status + '"></span></td><td id="editUser-' + i + '" class="v-hiararchy-edit" title="Edit" onClick="hierarchyUpload.editUser(' + i + ')"></td><td><div class="hier-upload-td">' + sendMailCode + '</div></td><td>' + hierarchyUpload.addToolTip(hierarchyUpload.hierarchyJson.upload.users[i]) + '</td><td><div class="hier-upload-td">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].rowNum) + '</div></td><td><div class="hier-upload-td" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].sourceUserId) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].sourceUserId) + '</div></td><td><div id="firstName-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isFirstNameModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].firstName) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].firstName) + '</div></td><td><div id="lastName-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isLastNameModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].lastName) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].lastName) + '</div></td><td><div id="title-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isTitleModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].title) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].title) + '</div></td><td><div id="assignedBranches-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isAssignedBranchesModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranches) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranches) + '</div></td><td><div id="assignedRegions-' + i + '" ="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isAssignedRegionsModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegions) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegions) + '</div></td><td><div id="assignedBranchesAdmin-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isAssignedBrachesAdminModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranchesAdmin) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranchesAdmin) + '</div></td><td><div id="assignedRegionsAdmin-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isAssignedRegionsAdminModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegionsAdmin) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegionsAdmin) + '</div></td><td><div id="emailId-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isEmailIdModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].emailId) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].emailId) + '</div></td><td><div id="phoneNumber-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isPhoneNumberModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].phoneNumber) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].phoneNumber) + '</div></td><td><div id="websiteUrl-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isWebsiteUrlModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].websiteUrl) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].websiteUrl) + '</div></td><td><div id="license-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isLicenseModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].license) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].license) + '</div></td><td><div id="legalDisclaimer-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isLegalDisclaimerModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].legalDisclaimer) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].legalDisclaimer) + '</div></td><td><div id="userPhotoUrl-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isUserPhotoUrlModified) + '" title="' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].userPhotoUrl) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].userPhotoUrl) + '</div></td><td><div id="aboutMeDescription-' + i + '" class="hier-upload-td ' + hierarchyUpload.isModified(hierarchyUpload.hierarchyJson.upload.users[i].isAboutMeDescriptionModified) + '" title="' + escapeHtml(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].aboutMeDescription)) + '">' + hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].aboutMeDescription) + '</div></td></tr><tr class="hide hier-users-edit" style="background-color: #F9F9FB;" ><td colspan="17">' + userEdit + '</td></tr>').appendTo('#user-upload');

							if (hierarchyUpload.hierarchyJson.upload.users[i].isDeletedRecord == true) {
								$('#editUser-' + i).addClass('disable');
							}
						}
					}
					$('#user-sum-btn').show();
					if (regionlength == 0 && branchlength == 0) {
						$('#user-sum-btn').addClass('active');
						$('#upload-summary-region').removeClass('active in');
						$('#upload-summary-branch').removeClass('active in');
						$('#upload-summary-user').addClass('active in');
						$('#summary').show();
					}
				}

				if ((hierarchyUpload.hierarchyJson.regionValidationErrors == null || hierarchyUpload.hierarchyJson.regionValidationErrors.length == 0) && (hierarchyUpload.hierarchyJson.branchValidationErrors == null || hierarchyUpload.hierarchyJson.branchValidationErrors.length == 0) && (hierarchyUpload.hierarchyJson.userValidationErrors == null || hierarchyUpload.hierarchyJson.userValidationErrors.length == 0)) {
					$('#xlsx-file-upload').removeClass('disable');
					showInfo("Data verified sucessfully with no validation errors.");
				} else {
					showError("There are some validation errors which need to be resolved before uploading the data.");
				}

				if (regionlength == 0 && branchlength == 0 && userlength == 0) {
					$('#no-data').show();
				}
			}
		}
	},

	saveXlxsSuccessCallback : function(response) {
		if (!response) {
			$('#com-file').val('');
			$('#com-xlsx-file').val('');
			showError("Error saving the file");
		} else {
			var jsonResponse = $.parseJSON(response);
			if (jsonResponse.status) {
				showInfo("Successfully saved the file");
				$("#fileUrl").val(jsonResponse.response);
			} else {
				showError("Error : " + jsonResponse.response);
			}
		}

	},

	toggleSendMail : function(iden) {
		if ($('#send-mail-' + iden).hasClass('bd-check-img-checked')) {
			$('#send-mail-' + iden).removeClass('bd-check-img-checked');
			hierarchyUpload.hierarchyJson.upload.users[iden].sendMail = true;
		} else {
			$('#send-mail-' + iden).addClass('bd-check-img-checked');
			hierarchyUpload.hierarchyJson.upload.users[iden].sendMail = false;
		}
	},

	fileValidate : function(fileformat) {
		var fileExtensions = ".xlsx";
		if ($(fileformat).attr("type") == "file") {
			var FileName = $(fileformat).val();
			if (FileName.length > 0) {
				var blnValid = false;
				if (FileName.substr(FileName.length - fileExtensions.length, fileExtensions.length).toLowerCase() == fileExtensions.toLowerCase()) {
					blnValid = true;
				}
			}
			if (!blnValid) {
				var msg = "Please upload xlsx file";
				showErrorMobileAndWeb(msg);
				$(fileformat).val = "";

				return false;
			}
		}
		return true;
	},

	fixStr : function(key) {
		var out = key.replace(/^[a-z]|[^\s][A-Z]/g, function(key, offset) {
			if (offset == 0) {
				return (key.toUpperCase());
			} else {
				return (key.substr(0, 1) + " " + key.substr(1).toUpperCase());
			}
		});
		return (out);
	},

	addToolTip : function(data) {
		var toolTipMsg = "";
		var toolTip = "";
		if (data.validationErrors.length > 0) {
			toolTipMsg += "Errors:\n";
			for (var i = 0; i < data.validationErrors.length; i++) {
				toolTipMsg += (i + 1) + ". " + data.validationErrors[i] + "\n";
			}
		}
		if (data.validationWarnings.length > 0) {
			toolTipMsg += "Warnings:\n";
			for (var i = 0; i < data.validationWarnings.length; i++) {
				toolTipMsg += (i + 1) + ". " + data.validationWarnings[i] + "\n";
			}
		}
		if (data.validationErrors.length > 0) {
			toolTip = '&nbsp;<span title="' + toolTipMsg + '"><img src="resources/images/Hazard_Red.png" style="width: 18px; height: 18px"></span>';
		} else if (data.validationWarnings.length > 0) {
			toolTip = '&nbsp;<span title="' + toolTipMsg + '"><img src="resources/images/Hazard_Yellow.png" style="width: 18px; height: 18px"></span>';
		}
		return toolTip;
	},

	saveEdittedRegion : function(i) {
		hierarchyUpload.hierarchyJson.upload.regions[i].regionName = hierarchyUpload.reverseUndef($('#hier-name-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress1 = hierarchyUpload.reverseUndef($('#hier-address1-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress2 = hierarchyUpload.reverseUndef($('#hier-address2-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.regions[i].regionCity = hierarchyUpload.reverseUndef($('#hier-city-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.regions[i].regionState = hierarchyUpload.reverseUndef($('#hier-state-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.regions[i].regionZipcode = hierarchyUpload.reverseUndef($('#hier-zipcode-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.isModifiedFromUI = true;

		$('#regionName-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionName));
		$('#regionAddress1-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress1));
		$('#regionAddress2-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionAddress2));
		$('#regionCity-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionCity));
		$('#regionZipcode-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionZipcode));
		$('#regionState-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.regions[i].regionState));

		$('#editRegion-' + i).parent().next('.hier-region-edit').slideToggle(200);

		$('#xlsx-file-upload').addClass('disable');
		showInfo("Successfully modified the region. Please click on 'Verify' button to validate the data!!");
	},

	editRegion : function(i) {
		$('#editRegion-' + i).parent().next('.hier-region-edit').slideToggle(200);
		showStateCityRow('hier-state-txt-' + i, 'hier-state-txt-' + i, 'hier-city-txt-' + i);
	},

	saveEdittedBranch : function(i) {
		hierarchyUpload.hierarchyJson.upload.branches[i].branchName = hierarchyUpload.reverseUndef($('#hier-branch-name-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.branches[i].sourceRegionId = hierarchyUpload.reverseUndef($('#hier-branch-sourceRegionId-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress1 = hierarchyUpload.reverseUndef($('#hier-branch-address1-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress2 = hierarchyUpload.reverseUndef($('#hier-branch-address2-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.branches[i].branchCity = hierarchyUpload.reverseUndef($('#hier-branch-city-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.branches[i].branchState = hierarchyUpload.reverseUndef($('#hier-branch-state-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.branches[i].branchZipcode = hierarchyUpload.reverseUndef($('#hier-branch-zipcode-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.isModifiedFromUI = true;

		$('#branchName-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchName));
		$('#sourceRegionId-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].sourceRegionId));
		$('#branchAddress1-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress1));
		$('#branchAddress2-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchAddress2));
		$('#branchCity-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchCity));
		$('#branchZipcode-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchZipcode));
		$('#branchState-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.branches[i].branchState));

		$('#editBranch-' + i).parent().next('.hier-branch-edit').slideToggle(200);

		$('#xlsx-file-upload').addClass('disable');
		showInfo("Successfully modified the branch. Please click on 'Verify' button to validate the data!!");
	},

	editBranch : function(i) {
		$('#editBranch-' + i).parent().next('.hier-branch-edit').slideToggle(200);
		showStateCityRow('hier-branch-state-txt-' + i, 'hier-branch-state-txt-' + i, 'hier-branch-city-txt-' + i);
	},

	saveEdittedUser : function(i) {
		hierarchyUpload.hierarchyJson.upload.users[i].firstName = hierarchyUpload.reverseUndef($('#hier-user-firstName-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].lastName = hierarchyUpload.reverseUndef($('#hier-user-lastName-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].title = hierarchyUpload.reverseUndef($('#hier-user-title-txt-' + i).val());

		// These are arrays, so process the string into an array
		var tempAssignedBranch = $('#hier-user-assignedBranches-txt-' + i).val();
		if (tempAssignedBranch == undefined || tempAssignedBranch == '') {
			tempAssignedBranch = [];
		} else {
			tempAssignedBranch = tempAssignedBranch.replace(/ /g, '').split(',');
		}
		hierarchyUpload.hierarchyJson.upload.users[i].assignedBranches = tempAssignedBranch;

		var tempAssignedRegion = $('#hier-user-assignedRegions-txt-' + i).val();
		if (tempAssignedRegion == undefined || tempAssignedRegion == '') {
			tempAssignedRegion = [];
		} else {
			tempAssignedRegion = tempAssignedRegion.replace(/ /g, '').split(',');
		}

		hierarchyUpload.hierarchyJson.upload.users[i].assignedRegions = tempAssignedRegion;

		var tempAssignedBranchesAdmin = $('#hier-user-assignedBranchesAdmin-txt-' + i).val();
		if (tempAssignedBranchesAdmin == undefined || tempAssignedBranchesAdmin == '') {
			tempAssignedBranchesAdmin = [];
		} else {
			tempAssignedBranchesAdmin = tempAssignedBranchesAdmin.replace(/ /g, '').split(',');
		}

		hierarchyUpload.hierarchyJson.upload.users[i].assignedBranchesAdmin = tempAssignedBranchesAdmin;

		var tempAssignedRegionsAdmin = $('#hier-user-assignedRegionsAdmin-txt-' + i).val();
		if (tempAssignedRegionsAdmin == undefined || tempAssignedRegionsAdmin == '') {
			tempAssignedRegionsAdmin = [];
		} else {
			tempAssignedRegionsAdmin = tempAssignedRegionsAdmin.replace(/ /g, '').split(',');
		}

		hierarchyUpload.hierarchyJson.upload.users[i].assignedRegionsAdmin = tempAssignedRegionsAdmin;

		hierarchyUpload.hierarchyJson.upload.users[i].emailId = hierarchyUpload.reverseUndef($('#hier-user-emailId-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].phoneNumber = hierarchyUpload.reverseUndef($('#hier-user-phoneNumber-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].websiteUrl = hierarchyUpload.reverseUndef($('#hier-user-websiteUrl-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].license = hierarchyUpload.reverseUndef($('#hier-user-license-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].legalDisclaimer = hierarchyUpload.reverseUndef($('#hier-user-legalDisclaimer-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].userPhotoUrl = hierarchyUpload.reverseUndef($('#hier-user-userPhotoUrl-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.users[i].aboutMeDescription = hierarchyUpload.reverseUndef($('#hier-user-aboutMeDescription-txt-' + i).val());
		hierarchyUpload.hierarchyJson.upload.isModifiedFromUI = true;

		$('#firstName-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].firstName));
		$('#lastName-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].lastName));
		$('#title-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].title));
		$('#assignedBranches-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranches));
		$('#assignedRegions-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegions));
		$('#assignedBranchesAdmin-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedBranchesAdmin));
		$('#assignedRegionsAdmin-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].assignedRegionsAdmin));
		$('#emailId-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].emailId));
		$('#phoneNumber-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].phoneNumber));
		$('#websiteUrl-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].websiteUrl));
		$('#license-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].license));
		$('#legalDisclaimer-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].legalDisclaimer));
		$('#userPhotoUrl-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].userPhotoUrl));
		$('#aboutMeDescription-' + i).text(hierarchyUpload.hierundefined(hierarchyUpload.hierarchyJson.upload.users[i].aboutMeDescription));

		$('#editUser-' + i).parent().next('.hier-users-edit').slideToggle(200);

		$('#xlsx-file-upload').addClass('disable');
		showInfo("Successfully modified the user. Please click on 'Verify' button to validate the data!!");
	},

	editUser : function(i) {
		$('#editUser-' + i).parent().next('.hier-users-edit').slideToggle(200);
	}

};
// Company registration variables
var companyRegistration = {
	isFormSubmitted : false, // is form submitted to avoid dbl click
	logo : true, // logo set or not
	logoSuccess : true, // logo upload successfully
	submitForm : function() {
		if (validateCompanyInformationForm('company-info-div')) {
			companyRegistration.isFormSubmitted = true;
			$('#company-info-form').submit();

		}
	},
	initializePage : function() {
		var verticalVal = $('#select-vertical').attr('data-value');
		if (verticalVal && verticalVal != "") {
			$('#select-vertical').val(verticalVal);
		}

		if ($('#message').val() != "") {
			if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
				showError($('#message').val());
			} else {
				showInfo($('#message').val());
			}
		}

		// Mask phone number
		if ($('#com-phone-format').val() || $('#com-phone-format').val() != '') {
			phoneFormat = $('#com-phone-format').val();
		}

		currentPhoneRegEx = phoneFormat;

		$('#com-contactno').mask(phoneFormat, {
			'translation' : {
				d : {
					pattern : /[0-9*]/
				}
			}
		});
		$('#company-info-submit').click(function() {
			if (!companyRegistration.logo) {
				$('#overlay-toast').html('Please upload files of type jpeg, png or jpg');
				showToast();
				return;
			}
			if (!companyRegistration.logoSuccess) {
				$('#overlay-toast').html('uploading logo please wait');
				showToast();
				return;
			}
			if (!companyRegistration.isFormSubmitted) {
				companyRegistration.submitForm();
			}

		});

		$('#icn-file').click(function() {
			$('#com-logo').trigger('click');
		});

		$('#com-logo').change(function() {
			var fileAdd = $(this).val().split('\\');
			$('#com-logo-decoy').val(fileAdd[fileAdd.length - 1]);
		});
		// Integrating autocomplete with country input text field
		attachAutocompleteCountry('com-country', 'country-code', 'com-state', 'state-city-row', 'com-city', 'com-contactno');

		$('input').keypress(function(e) {
			// detect enter
			if (e.which == 13) {
				e.preventDefault();
				companyRegistration.submitForm();
			}
		});

		$("#com-logo").on("change", function() {
			companyRegistration.logo = true;
			if (!logoValidate("#com-logo")) {
				companyRegistration.logo = false;
				return false;
			}
			companyRegistration.logoSuccess = false;
			var formData = new FormData();
			formData.append("logo", $('#com-logo').prop("files")[0]);
			formData.append("logo_name", $('#com-logo').prop("files")[0].name);
			callAjaxPOSTWithTextDataLogo("./uploadcompanylogo.do", companyRegistration.uploadImageSuccessCallback, true, formData);
		});
	},
	uploadImageSuccessCallback : function(response) {
		companyRegistration.logoSuccess = true;
		var success = "Logo has been uploaded successfully";
		if (success != response.trim()) {
			$('#com-logo').val('');
			$('#com-logo-decoy').val('');
			showError(response);
		} else {
			showInfo(response);
		}
	}
};

function escapeHtml(unsafe) {
	return unsafe.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}

function downloadCompanyHierarchyReport() {
	window.location.href = "/downloadcompanyhierarchyreport.do?columnName=" + colName + "&columnValue=" + colValue;
}


function validateVendastaAccountId(elementId) {
	if ($('#' + elementId).val() != "") {
		$('#' + elementId).next('.hm-item-err-2').hide();
		return true;
	} else {
		$('#' + elementId).next('.hm-item-err-2').html('Please enter Account ID.');
		$('#' + elementId).next('.hm-item-err-2').show();
		return false;
	}
}
