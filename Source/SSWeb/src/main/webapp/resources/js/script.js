var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/;
var zipcodeRegex = /^\d{5}([\-]?\d{4})?$/;
var phoneRegex = /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/;
var passwordRegex = /^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+=|<>?{}~-]).{6,15}$/;
var nameRegex = /^[a-zA-Z ]*$/;
var lastNameRegEx = /^[a-zA-Z0-9 ]*$/;
var companyNameRegEx = /^[a-zA-Z0-9\\/ ]*$/;
var numberRegEx = /^[1-9][0-9]*?$/;
var minPwdLength = 6;
var maxPwdLength = 15;
var firstNamePatternRegex = /^[a-zA-Z]{2,}$/;
var lastNamePatternRegEx = /^[a-zA-Z]{2,}$/;
var pageInitialized = false;
var currentPhoneRegEx; //Vary the phone regex according to masking
var stateList; //usStateList
var cityLookupList; //cityLookupList
var phoneFormat = '(ddd) ddd-dddd'; //defualt phone format
var selectedCountryRegEx = "";

function buildMessageDiv(){
	if($('.err-nw-wrapper').length == 0){
        var errorDiv = $("<div id='err-nw-wrapper' class='err-nw-wrapper'>");
            var closeSpan = $('<span class="err-new-close">');
            var textSpan = $('<span id="err-nw-txt">');
            errorDiv.append(closeSpan);
            errorDiv.append(textSpan);
        $('.hm-header-main-wrapper').after(errorDiv);
    }
}

function showError(msg){
	buildMessageDiv();
    $('#err-nw-txt').html(msg);
    $('#err-nw-wrapper').removeClass('bg-black-success');
    $('#err-nw-wrapper').slideDown(200);
    $(window).scrollTop($('#err-nw-wrapper').offset().top);
}

function hideError(){
    $('#err-nw-wrapper').slideUp(200);
}

function showInfo(msg){
	buildMessageDiv();
    $('#err-nw-txt').html(msg);
    $('#err-nw-wrapper').slideDown(200);
    $(window).scrollTop($('#err-nw-wrapper').offset().top);
    $('#err-nw-wrapper').addClass('bg-black-success');
}

function hideInfo(){
    $('#err-nw-wrapper').slideUp(200);
    setTimeout(function(){
        $('#err-nw-wrapper').removeClass('bg-black-success');
    },200);
}

$(document).on('click', '.err-new-close', function() {
	hideError();
	hideInfo();
});

function showRegErr(msg){
    $('#reg-err-pu-msg').html(msg);
    $('#reg-err-pu').fadeIn();
}

function hideRegErr(){
    $('#reg-err-pu').fadeOut();
}
function showErrorMobileAndWeb(msg) {
	if($(window).width() < 768){
		$('#overlay-toast').html(msg);
		showToast();
	}
	else {
		showError(msg);
	}
}
function showInfoMobileAndWeb(msg) {
	if($(window).width() < 768){
		$('#overlay-toast').html(msg);
		showToast();
	}
	else {
		showInfo(msg);
	}
}

function validateForm(id) {
	var validate = true;

	//hide the server error
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
				}else {
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
				}else {
					validate = false;
	                $('#jsErrTxt').html('Please enter a valid zipcode.');
					$(this).parent().addClass('input-error');
				}
			}
		}
		
		if ($(this).data('phone') == true) {
			if ($(this).val() != "") {
				if (phoneRegex.test($(this).val()) == true) {
					$(this).parent().removeClass('input-error');
				}else {
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

function showOverlay(){
    $('.overlay-loader').show();
}

function hideOverlay(){
    $('.overlay-loader').hide();
}
function showDashOverlay(dashid){
    $(dashid).show();
}

function hideDashOverlay(dashid){
    $(dashid).hide();
}

//show Toast
function showToast(){
    $('#toast-container').fadeIn();
    setTimeout(function(){
        $('#toast-container').fadeOut();
    },3000);
}

//detectBrowser();

function detectBrowser(){
    if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1){
        is_safari = true;
        $('.input-file-text').css('left','-51px');
    }else if(is_firefox){
        $('.input-file-text').css('left','-44px');
    }
}

function moveFileBtn(){
    if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1){
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 101) + 'px');
    }else if(is_firefox){
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 97) + 'px');
    }else{
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 97) + 'px');
    }
}

$('#hm-item-dd-top').click(function(){
   $('#hm-dd-wrapper-top').slideToggle(200);
});

$('#hm-item-dd-bottom').click(function(){
   $('#hm-dd-wrapper-bottom').slideToggle(200);
});

$('#header-menu-icn').click(function(){
//    $('#header-links').slideToggle(200);
    $('#header-slider-wrapper').addClass('rt-panel-slide');
    disableBodyScroll();
});

$('#header-slider-wrapper').click(function(){
    $('#header-slider-wrapper').removeClass('rt-panel-slide');
    enableBodyScroll();
});

$('#header-links-slider, .header-slider').click(function(e){
    e.stopPropagation();
});

$('.hm-header-dd-icn').click(function(){
    $(this).parent().next('.create-branch-dd').slideToggle();
});
$('.dd-icn-type2').click(function(){
    $(this).parent().parent().next('.create-branch-dd').slideToggle();
});

function showPayment(){
	$('.overlay-payment').show();
}

function hidePayment(){
	$('.overlay-payment').hide();
}

$('#icn-status-green').click(function(){
    $(this).hide();
    $('#icn-status-red').show();
});

$('#icn-status-red').click(function(){
    $(this).hide();
    $('#icn-status-green').show();
});

/**
 * Functionality to remove input error class on blur of any input text box
 */
$('.login-wrapper-txt').focus(function(){
	$(this).parent().removeClass('input-error');
});


//Function to validate email id in a form
function validateEmailId(elementId, isOnlyToast){
	var emailId = $('#'+elementId).val();
	var message = 'Please enter a valid Email Address';
	if (emailId != "") {
		emailId = emailId.trim();
		if (emailRegex.test(emailId) == true) {
			return true;
		} else {
			if(isOnlyToast) {
				$('#overlay-toast').text(message);
				showToast();
			} else {
				showError(message);
			}
			return false;
		}
	} else {
		if(isOnlyToast) {
			$('#overlay-toast').text(message);
			showToast();
		} else {
			showError(message);
		}
		return false;
	}
}

//Function to validate the first name
function validateFirstName(elementId){
	if ($('#'+elementId).val() != "") {
		if (nameRegex.test($('#'+elementId).val()) == true) {
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
function validateLastName(elementId){
	
		if ($('#'+elementId).val() == ""||lastNameRegEx.test($('#'+elementId).val()) == true) {
			return true;
		} else {
			showError('Please enter a valid last name');
			return false;
		}
	
}

/**
 * Function to validate a password in form
 * @param elementId
 * @returns {Boolean}
 */
function validatePassword(elementId) { 
	var password = $('#'+elementId).val();
	if(password.trim() == "") {
		showError("Please enter password");
		return false;
	}
	else if (password.length < minPwdLength) {
		showError('Password must be at least 6 characters');
		return false;
	}
	return true;
}
/**
 * Function to match password and confirm password
 * @param pwdId
 * @param confirmPwdId
 * @returns {Boolean}
 */
function validateConfirmPassword(pwdId, confirmPwdId){
	if ($('#'+confirmPwdId).val() != "") {
		if ($('#'+pwdId).val() != $('#'+confirmPwdId).val()) {
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
function validateCompany(elementId){
	if ($('#'+elementId).val() != "") {
		if ($('#'+elementId).val().indexOf("\"") == -1) {
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

//Function to validate the zipcode
function validateZipcode(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (zipcodeRegex.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid zipcode.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter zipcode.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (zipcodeRegex.test($('#'+elementId).val()) == true) {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			}else {
				$('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid zipcode.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
		}else{
			$('#'+elementId).parent().next('.login-reg-err').html('Please enter zipcode.');
			$('#'+elementId).parent().next('.login-reg-err').show();
			return false;
		}
	}
}

function escapeRegExp(str) {
	return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
}

//Function to validate the phone number
function validatePhoneNumber(elementId, isOnlyShowToast) {
	var regExToTest = phoneRegex;
	if(currentPhoneRegEx && currentPhoneRegEx != ""){
		var regExToTestStr = currentPhoneRegEx;
		regExToTestStr = escapeRegExp(currentPhoneRegEx);
		regExToTestStr = regExToTestStr.replace(/d/g,'\\d');
		regExToTest = new RegExp(regExToTestStr);
	}
	if ($('#' + elementId).val() != "") {
		if (regExToTest.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			var msg = 'Please enter a valid phone number';
			if(isOnlyShowToast){
				$('#overlay-toast').html(msg);
				showToast();			
			} else {
				showErrorMobileAndWeb(msg);			
			}
			return false;
		}
	} else {
		var msg = 'Please enter phone number';
		if(isOnlyShowToast){
			$('#overlay-toast').html(msg);
			showToast();			
		} else {
			showErrorMobileAndWeb(msg);			
		}
		return false;
	}
}

//Function to validate Address 1
function validateAddress1(elementId ,isOnlyShowToast){
	if ($('#'+elementId).val() != "") {
			return true;
	} else {
		var msg = 'Please enter address';
		
		if(isOnlyShowToast){
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
		var msg='Please enter country name';
		$('#overlay-toast').html(msg);
		showToast();
		return false;
	} else {
		var countryCode = $.trim($('#prof-country').val());
		if (countryCode == "") {
			var msg='Please enter valid country name';
			$('#overlay-toast').html(msg);
			showToast();
			return false;
		} else {
			return true;
		}
	}
}

//Function to validate Branch name
function validateBranchName(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (companyNameRegEx.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid branch name.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter branch name.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (companyNameRegEx.test($('#'+elementId).val()) == true) {
				$('#'+elementId).next('.input-error-2').hide();
				return true;
			}else {
				$('#'+elementId).next('.input-error-2').html('Please enter a valid branch name.');
				$('#'+elementId).next('.input-error-2').show();
				return false;
			}
		}else{
			$('#'+elementId).next('.input-error-2').html('Please enter branch name.');
			$('#'+elementId).next('.input-error-2').show();
			return false;
		}
	}
}

//Function to validate the username
function validateEncompassUserName(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			$('#'+elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#overlay-toast').html('Please enter user name');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			$('#'+elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#'+elementId).next('.hm-item-err-2').html('Please enter user name');
			$('#'+elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}

//function to validate a password in form
function validateEncompassPassword(elementId) {
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			$('#'+elementId).next('.hm-item-err-2').hide();
			return true;
		}else{
			$('#overlay-toast').html('Please enter password');
			showToast();
			return false;
		}
	}else{
		if ($('#'+elementId).val() != "") {
			$('#'+elementId).next('.hm-item-err-2').hide();
			return true;
		}else{
			$('#'+elementId).next('.hm-item-err-2').html('Please enter password');
			$('#'+elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}



//Function to validate the url
function validateURL(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			$('#'+elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#overlay-toast').html('Please enter url');
			showToast();
			return false;
		}
	}else{
	   	if ($('#'+elementId).val() != "") {
			$('#'+elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#'+elementId).next('.hm-item-err-2').html('Please enter url');
			$('#'+elementId).next('.hm-item-err-2').show();
			return false;
		}
	}
}

function validateReminderInterval(elementId) {
	if ($('#'+elementId).val() != "") {
		if (numberRegEx.test($('#'+elementId).val())) {
			$('#'+elementId).next('.hm-item-err-2').hide();
			return true;
		} else {
			$('#'+elementId).next('.hm-item-err-2').html('Please enter a valid number.');
			$('#'+elementId).next('.hm-item-err-2').show();
			return false;
		}
	} else {
		$('#'+elementId).next('.hm-item-err-2').html('Please enter number');
		$('#'+elementId).next('.hm-item-err-2').show();
		return false;
	}
}

$('.user-info-initial').click(function(e){
    e.stopPropagation();
    $('.initial-dd-wrapper').slideToggle(200);
});

$('body').click(function(){
    if($('.initial-dd-wrapper').css('display') == "block"){
        $('.initial-dd-wrapper').slideUp(200);
    }
});

function upgradeToPaidPlan(){
	 var url = "./upgradetopaidplanpage.do";
	    
	 callAjaxGET(url, function(data){
     	$('#outer-payment').html(data);
    	$('#outer-payment').show();
	 }, false);
}

function loadDisplayPicture(profileMasterId){
	var payload = {
		"profileMasterId" : profileMasterId
	};
	callAjaxGETWithTextData("./getdisplaypiclocation.do", function(data) {
		if (data != undefined){
			if(data != undefined && data != ""){
				var imageUrl = JSON.parse(data);
				if (imageUrl != undefined && imageUrl != "undefined"
						&& imageUrl.trim() != "") {
					$("#hdr-usr-img").css("background",
							"url(" + imageUrl + ") no-repeat center");
					$("#hdr-usr-img").css("background-size", "cover");
					$("#usr-initl").html("");
				} else {
					callAjaxGET('./initialofusername.do', displayPicCallback,
							false);
				}
			}
			
		}
		return data.responseJSON;
	}, false, payload);
}
function displayPicCallback(data){
	$("#hdr-usr-img").css("background", "");
	$("#usr-initl").html(data);
}

/**
 * Method to redirect to error page
 */
function redirectTo404ErrorPage(){
	//location.href =  window.location.origin + "/error";
	location.href =  getLocationOrigin() + "/error";
}

function sendVerificationMail(emailUrl){
	var payload = {
			"emailUrl" : emailUrl
	};
	callAjaxGetWithPayloadData("./sendverificationmail", verificationCallback, payload, true);
}

function verificationCallback(){
	$('#overlay-toast').html('Verification mail sent to the registered Email Id');
	showOverlay();
}


// Header buttons
$(document).on('click', '.hdr-log-btn', function() {
	window.location = "/login.do";
});
$(document).on('click', '.hdr-reg-btn', function() {
	window.location = "/signup.do";
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
		selectedCountryRegEx = ".*";
		selectedCountryRegEx = new RegExp(selectedCountryRegEx);
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


//Sign up page functions
function signupUser() {
	if(validateSignUpForm()){
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

	$('#signup-submit').click(function(e){
		signupUser();
	});
   
	$('input').keypress(function(e){
		// detect enter
		if (e.which==13){
			e.preventDefault();
			signupUser();
		}
	});
}

function validateSignUpForm(){
	hideError();
	if(!validateFirstName('sign-fname')) {
		$('#sign-fname').focus();
		return false;
	}
	if(!validateLastName('sign-lname')) {
		$('#sign-lname').focus();
		return false;
	}
	if(!validateEmailId('sign-email')) {
		$('#sign-email').focus();
		return false;
	}
	return true;
}

//Function to validate multiple email ids in a form
function validateMultipleEmailIds(elementId){
	var emailIdStr = $('#'+elementId).val();
	if (emailIdStr != "") {
		emailIdStr = emailIdStr.trim();
		var emailIds  = emailIdStr.split(",");
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

//Function to validate complaint registraion form
function validateComplaintRegistraionForm(){
	var validate = true;

	//hide the server error
	$("#serverSideerror").hide();
	
	// validate error code on email id
	validate = validateMultipleEmailIds('comp-mailId')
	
	// check if checkbox is enabled
	if($('input[name="enabled"]').prop( "checked" )) {
		// check whether rating selectd is 0 and mood is empty
		var rating = $("#comp-rating-post").val();
		var mood = $("#comp-mood").val();
		
		if(rating == 0 && mood =='') {
			// reset the check box if score is 0 and mood not selected
			$('#compl-checkbox').addClass('bd-check-img-checked');
			$('input[name="enabled"]').prop( "checked" , false);
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


//Functions for login page
function loginUserLoginPage() {
	if(validateFormLoginPage('login-form')){
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

	$('#login-submit').click(function(e){
		loginUserLoginPage();
	});
   
	$('input').keypress(function(e){
		// detect enter
		if (e.which==13){
			e.preventDefault();
			loginUserLoginPage();
		}
	});
}

//Functions for forgot password page
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

//Functions for reset password page
function submitResetPasswordForm() {
	if(validateResetPasswordForm('reset-pwd-form')){
		$('#reset-pwd-form').submit();
	}
}

function validateResetPasswordForm(id) {
	if(!validateEmailId('login-user-id')){
			$('#login-user-id').focus();
			return false;
	}
	if(!validatePassword('login-pwd')){
			$('#login-pwd').focus();
			return false;
	}
	if(!validateConfirmPassword('login-pwd', 'login-cnf-pwd')){
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
	
	$('#reset-pwd-submit').click(function(e){
		submitResetPasswordForm();
	});
	
	$('input').keypress(function(e){
		// detect enter
		if (e.which==13){
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


//Function to validate country
function validateCountry(elementId) {
	var country = $.trim($('#'+elementId).val());
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
	if(!validateCompany('com-company')){
		$('#com-company').focus();
		return false;
	}
	if(!validateAddress1('com-address1')){
		$('#com-address1').focus();
		return false;
	}
	if(!validateAddress2('com-address2')){
		$('#com-address2').focus();
		return false;
	}
	if(!validateCountry('com-country')){
		$('#com-country').focus();
		return false;
	}
	if(!validateCountryZipcode('com-zipcode')){
		$('#com-zipcode').focus();
		return false;
	}
	if(!validatePhoneNumber('com-contactno')){
		$('#com-contactno').focus();
		return false;
	}
	return true;
}

//Functions for home page
function resizeHomePageFunc(){
	var winW = window.innerWidth;
	if (winW < 768) {
		var offset = winW - 114 - 50;
		$('.reg-cap-txt').css('width',offset+'px');
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

//Initialize home page
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

//Functions for find a pro page
//pagination variables for pro List page
var rowSize = 10;
var startIndex = 0;


//Function to initialiize find a pro page
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
		if (e.which==13) {
			e.preventDefault();
			submitFindAProForm();
		}
	});
	

	//Click events proList pagination buttons
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
		//if the letter is not digit then don't type anything
		if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
			return false;
		}
		var totalPage = parseInt($('#pro-total-pages').text());
		var prevPageNoVal = parseInt($('#sel-page-prolist').val());
		if(prevPageNoVal == NaN) {
			prevPageNoVal = 0;
		}
		var pageNo = prevPageNoVal + String.fromCharCode(e.which);
		pageNo = parseInt(pageNo);
		if(pageNo >= totalPage || pageNo <= 0) {
			return false;
		}
	});

	$('#pro-paginate-btn').on('keyup', '#sel-page-prolist', function(e) {
		if(e.which == 13) {
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

/**
 * Method to fetch users list based on the criteria i.e if profile level is specified,
 *  bring all users of that level else search based on first/last name
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
		
		if (!($('#find-pro-first-name').val() == "" && $('#find-pro-last-name').val() == ""))
			callAjaxPOSTWithTextData("./findaproscroll.do", paginateUsersProList, true, formData);
		else
			hideOverlay();
	}
}


//Function to validate registration form
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
		//showOverlay();
	} else {
		if (!($('#find-pro-first-name').val() == "" && $('#find-pro-last-name').val() == ""))
			showError("Please enter either a valid First Name or Last Name to search for");
	}
}

//Function to update the pagination buttons
function updatePaginationBtnsForProList() {
	var start = parseInt($('#pro-paginate-btn').attr("data-start"));
	var total = parseInt($('#pro-paginate-btn').attr("data-total"));
	var batch = parseInt($('#pro-paginate-btn').attr("data-batch"));
	
	//update previous button
	if(start == 0) {
		$('#pro-prev').removeClass('paginate-button');
	} else {
		$('#pro-prev').addClass('paginate-button');
	}
	
	//update next button
	if(start + batch >= total) {
		$('#pro-next').removeClass('paginate-button');
	} else {
		$('#pro-next').addClass('paginate-button');
	}
	
	//update page no
	var pageNo = 0;
	if(start < total){
		pageNo = start / batch + 1;	
	} else {
		pageNo = start / batch;
	}
	$('#sel-page-prolist').val(pageNo);
}

function paginateUsersProList(response) {
	var reponseJson = $.parseJSON(response);
	var start = parseInt($('#pro-paginate-btn').attr("data-start"));
	var batch = parseInt($('#pro-paginate-btn').attr("data-batch"));
	
	// error message
	if (reponseJson.errMessage) {
		showError(reponseJson.errMessage);
		$('#ctnt-list-wrapper').append("No Profiles found");
	}
	else {
		if(start == 0) {
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
				usersHtml = usersHtml + '<div class="float-left ctnt-list-item-txt-wrap">'
					+ '<div class="ctnt-item-name user-display-name">' + user.displayName + '</div>';

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
					reviewCount  = user.reviewCount;
				}
				
				var reviewScore = 0;
				if (user.reviewScore) {
					reviewScore  = user.reviewScore;
				}
				
				usersHtml = usersHtml + '</div>';
				usersHtml = usersHtml + '<div class="float-left ctnt-list-item-btn-wrap clearfix">'
					+ '<div class="float-left ctnt-review-score" data-score="' + reviewScore + '"></div>'
					+ '<div class="float-left ctnt-review-count" user="' + user.userId + '">' + reviewCount + ' Review(s)</div>'
				+ '</div>';
				usersHtml = usersHtml + '</div>';
			});
			$('#ctnt-list-wrapper').html(usersHtml);
			
			$('.ctnt-review-score').each(function(){
				changeRatingPattern($(this).attr("data-score"), $(this));
				$(this).append(" - ");
			});
			
			$(".ctnt-list-item").click(function(e){
				var agentProfileName = $(this).attr("data-profilename");
				// var url = window.location.origin + "/pages" + agentProfileName;
				var url = getLocationOrigin() + "/pages" + agentProfileName;
				window.open(url);
			});
		}
	}
}

/**
 * Function to fetch the users by profile level in pro list page
 * @param iden
 * @param profileLevel - office/region/company
 * @param startIndex
 */
function fetchUsersByProfileLevel(iden, profileLevel, startIndex) {
	if (iden == undefined) {
		return;
	}
	var url = getLocationOrigin() + "/rest/profile/individuals/" + iden
	+ "?profileLevel=" + profileLevel + "&start=" + startIndex;
	callAjaxGET(url, function(data) {
		var response = $.parseJSON(data);
		if (response != undefined) {
			paginateUsersProList(response.entity);
		}
	}, false);
}


//Function to adjust image width
function adjustTextContainerWidthOnResize() {
	var parentWidth = $('.ctnt-list-item').width();
	var imgWidth = $('.ctnt-list-item .ctnt-list-item-img').width();
	var textContainerWidth = parentWidth - imgWidth - 35;
	$('.ctnt-list-item .ctnt-list-item-txt-wrap').width(textContainerWidth);
}


//Function to validate the first name pattern
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

//Function to validate the last name pattern
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

//Functions for take survey module
function checkCharacterLimit(element) {
	$('#toast-container').hide();
	if(element.value.length >= 500){
		$('#overlay-toast').html('Maximum charter limit 500');
		showToast();		
	}
}


//Resize function for take survey page
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
	
	if(q != undefined && q!=""){
		initSurveyWithUrl(q);
	}
	else{
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

	$('#cust-agent-verify').on('click',function(){
		if($(this).hasClass('bd-check-img-checked')){
			$(this).removeClass('bd-check-img-checked');
		}else{
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

	$('#cust-agent-verify').on('click',function(){
		if($(this).hasClass('bd-check-img-checked')){
			$(this).removeClass('bd-check-img-checked');
		}else{
			$(this).addClass('bd-check-img-checked');
		}
	});
	
	$('#start-btn').click(function() {
		
		//Check if the form is valid
		if(!validateSurveyForm()) {
			return;
		}
		firstName = $('#firstName').val().trim();
		lastName = $('#lastName').val().trim();
		var email = $('#email').val().trim();
		var grecaptcharesponse = $('#g-recaptcha-response').val();
		var agentId = $('#prof-container').attr("data-agentId");
		var agentName = $('#prof-container').attr("data-agentName");
		initSurvey(firstName, lastName, email, agentId, agentName,
				grecaptcharesponse);
		
		// Google analytics for reviews
		ga('send', {
			'hitType': 'event',
			'eventCategory': 'review',
			'eventAction': 'click',
			'eventLabel': 'Reviews',
			'eventValue': agentId
		});
	});
}

/*
 * Function to initiate survey. It hits controller to get list of all the
 * questions which are shown one after one to the customer.
 */
function initSurvey(firstName, lastName, email, agentId, agentName, grecaptcharesponse) {
	this.agentId = agentId;
	this.agentName = agentName;
	customerEmail = email;
	
	$('input[g-recaptcha-response]').val(grecaptcharesponse);
	
	if($('#cust-agent-verify').hasClass('bd-check-img-checked')){
		$('#overlay-toast').html("Verify that you have done business with the agent");
		showToast();
		return false;
	}
	
	$('#survey-request-form').submit();
}

//Validate survey form
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
	if(agentEmail.toUpperCase() == email.toUpperCase()){
		$('#overlay-toast').html('Agents can not take survey for themselves!');
		showToast();
		return false;
	}
	return true;
}

//Function to validate the first name
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


//Function to validate email id in a form
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

//Functions for complete registration page
function initializeCompleteRegistrationPage() {
	
	//show error message on page load
	if ($('#message').val() != "") {
		showError($('#message').val());
	}

	
	$('#comp-reg-submit').click(function(e){
		submitCompleteRegistrationForm();
	});
	
	$('input').keypress(function(e){
		// detect enter
		if (e.which==13){
			e.preventDefault();
			submitCompleteRegistrationForm();
		}
	});
	
	$('#complete-reg-fname').blur(function(){
		if (validateFirstName(this.id)) {
			hideError();
		}
	});
	
	$('#complete-reg-lname').blur(function(){
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
	if(!validateFirstName('complete-reg-fname')){
		$('#complete-reg-fname').focus();
		return false;
	}
	if(!validateLastName('complete-reg-lname')){
		$('#complete-reg-lname').focus();
		return false;
	}
	if(!validateEmailId('complete-reg-user-id')){
		$('#complete-reg-user-id').focus();
		return false;
	}
	if(!validatePassword('complete-reg-pwd')){
		$('#complete-reg-pwd').focus();
		return false;
	}
	if(!validateConfirmPassword('complete-reg-pwd', 'complete-reg-cnf-pwd')){
		$('#complete-reg-cnf-pwd').focus();
		return false;
	}
	return true;
}

function submitCompleteRegistrationForm() {
	if(validateCompleteRegistrationForm()){
		$('#complete-registration-form').submit();
	}
}

//Function for user registration page when a company is registered
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
	
	$('input').keypress(function(e){
    	if (e.which==13){
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
	
	$('#reg-conf-pwd').blur(function(){
		if (validateConfirmPassword('reg-pwd', this.id)) {
			hideError();
		}
	});
}
var hierarchyUpload={
		verified:false,
		
		fileUpload:function(){
			$('#com-file').change(
		
				function() {
					var fileAdd = $(this).val().split('\\');
					$('#com-xlsx-file').val(
							fileAdd[fileAdd.length - 1]);
					if (hierarchyUpload.fileValidate("#com-file")) {
						hierarchyUpload.verified =true;
					}
					
					if(hierarchyUpload.verified==false){
						showError("Please upload xlsx file");
					}
					
					if (hierarchyUpload.verified == true) {
						var formData = new FormData();
						formData.append("file", $('#com-file')
								.prop("files")[0]);
						formData
								.append("filename", $(
										'#com-file').prop(
										"files")[0].name);
						showOverlay();
						callAjaxPOSTWithTextDataLogo("./savexlsxfile.do",
								hierarchyUpload.saveXlxsSuccessCallback, true, formData); 
						hierarchyUpload.verified=false;
					}
					
					else{
						showError("Please select a valid file");
					}
				});

		
				$('#xlsx-file-verify').click(
						function() {
							var url = $("#fileUrl").val();
							if (url == undefined || url == '') {
								showError("Please upload a valid file");
							} else {
								var formData = new FormData();
								formData.append("fileUrl", url);
								showOverlay();
								callAjaxPOSTWithTextDataLogo("./verifyxlsxfile.do",
										hierarchyUpload.uploadXlxsSuccessCallback, true, formData);
								hierarchyUpload.verified=false;
							}
						});
				$('#xlsx-file-upload').click(
						function() {
							/*if (hierarchyUpload.verified == true) {
								var formData = new FormData();
								formData.append("logo", $('#com-file')
										.prop("files")[0]);
								formData
										.append("logo_name", $(
												'#com-file').prop(
												"files")[0].name);
								callAjaxPOSTWithTextDataLogo("./uploadxlsxfile.do",
										hierarchyUpload.uploadXlxsSuccessCallback, true, formData); 
								hierarchyUpload.verified=false;
							}
							else{
								showError("File is not verified");
							}
							*/
						});
				$('#icn-xlsxfile').click(function() {
					$('#com-file').trigger('click');
				});
		},
		uploadXlxsSuccessCallback:function(response){
        if (!response) {
				$('#com-file').val('');
				$('#com-xlsx-file').val('');
				$('#fileUrl').val('');
				showError(response);
			} else {
				$('#xlsx-file-upload').show();
				function fixStr(key) {
				    var out = key.replace(/^[a-z]|[^\s][A-Z]/g, function(key, offset) {
				        if (offset == 0) {
				            return(key.toUpperCase());
				        } else {
				            return(key.substr(0,1) + " " + key.substr(1).toUpperCase());
				        }
				    });
				    return(out);
				}
				
				$.each($.parseJSON(response), function(key, value) {
						var number=key.indexOf("number");
						if(number!=-1){
							if(value!=0){
								$('<div style="float:left;padding:10px;color: #009FE0;">'+fixStr(key)+':'+value+'</div>').appendTo('#json-response');
							}
						}
					
					/*function fixStr(key) {
					    var out = key.replace(/^[a-z]|[^\s][A-Z]/g, function(key, offset) {
					        if (offset == 0) {
					            return(key.toUpperCase());
					        } else {
					            return(key.substr(0,1) + " " + key.substr(1).toUpperCase());
					        }
					    });
					    return(out);
					}
					if(value!=0){
					 $('<tr><td style="text-align:right"> '+fixStr(key)+':</td><td id="'+key+'">'+value+'</td><tr>').appendTo('#json-response');
					 $('#com-file').val('');
						$('#com-xlsx-file').val('');
					}
					*/
					});
			
				}
			
		},
		saveXlxsSuccessCallback:function(response){
			if(!response){
				$('#com-file').val('');
				$('#com-xlsx-file').val('');
				showError("Error saving the file");
			} else {
				var jsonResponse = $.parseJSON(response);
				if(jsonResponse.status){
					showInfo("Successfully saved the file");
					$("#fileUrl").val(jsonResponse.response);
				} else {
					showError("Error : " + jsonResponse.response);
				}
			}
			
		},
		fileValidate:function(fileformat){
			var fileExtensions = ".xlsx";
			if ($(fileformat).attr("type") == "file") {
				var FileName = $(fileformat).val();
				if (FileName.length > 0) {
					var blnValid = false;
					if (FileName.substr(
							FileName.length
									- fileExtensions.length,
							fileExtensions.length)
							.toLowerCase() == fileExtensions
							.toLowerCase()) {
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
		}
		

};
//Company registration variables
var companyRegistration = {
	isFormSubmitted : false, //is form submitted to avoid dbl click
	logo : true, //logo set or not
	logoSuccess : true, //logo upload successfully
	submitForm : function() {
		if (validateCompanyInformationForm('company-info-div')) {
			companyRegistration.isFormSubmitted=true;
			$('#company-info-form').submit();
			
		}
	},
	initializePage : function() {
		var verticalVal = $('#select-vertical').attr('data-value');
		if(verticalVal && verticalVal != ""){
			$('#select-vertical').val(verticalVal);
		}
		
		if ($('#message').val() != "") {
			if ($('#message').attr('data-status') == 'ERROR_MESSAGE') {
				showError($('#message').val());
			} else {
				showInfo($('#message').val());
			}
		}
		
		//Mask phone number
		if($('#com-phone-format').val() || $('#com-phone-format').val() != ''){
			phoneFormat = $('#com-phone-format').val();
		}
		
		currentPhoneRegEx = phoneFormat; 
		
		$('#com-contactno').mask(phoneFormat, {'translation': {d: {pattern: /[0-9*]/}}});
		$('#company-info-submit').click(function() {
			if(!companyRegistration.logo){
				$('#overlay-toast').html('Please upload files of type jpeg, png or jpg');
				showToast();
				return;
			}
			if(!companyRegistration.logoSuccess){
				$('#overlay-toast').html('uploading logo please wait');
				showToast();
				return;
			}
			if(!companyRegistration.isFormSubmitted ){
				companyRegistration.submitForm();
			}
			
		});
		
		$('#icn-file').click(function(){
			$('#com-logo').trigger('click');
		});
		
		$('#com-logo').change(function(){
			var fileAdd = $(this).val().split('\\');
			$('#com-logo-decoy').val(fileAdd[fileAdd.length - 1]);
		});
		// Integrating autocomplete with country input text field
		attachAutocompleteCountry('com-country', 'country-code', 'com-state', 'state-city-row', 'com-city', 'com-contactno');
		
		$('input').keypress(function(e){
			// detect enter
			if (e.which==13){
				e.preventDefault();
				companyRegistration.submitForm();
			}
		});

		$("#com-logo").on("change", function() {
			companyRegistration.logo=true;
			if(!logoValidate("#com-logo")){
				companyRegistration.logo =false;
				return false;
			}
			companyRegistration.logoSuccess=false;
			var formData = new FormData();
			formData.append("logo", $('#com-logo').prop("files")[0]);
			formData.append("logo_name", $('#com-logo').prop("files")[0].name);
			callAjaxPOSTWithTextDataLogo("./uploadcompanylogo.do", companyRegistration.uploadImageSuccessCallback, true, formData);
		});
	},
	uploadImageSuccessCallback : function(response) {
		companyRegistration.logoSuccess=true;
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

