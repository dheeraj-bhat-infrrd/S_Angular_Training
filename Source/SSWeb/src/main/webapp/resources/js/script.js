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
var companyNameRegEx = /^[a-zA-Z0-9 ]*$/;
var numberRegEx = /^[1-9][0-9]*?$/;
var minPwdLength = 6;
var maxPwdLength = 15;
var firstNamePatternRegex = /^[a-zA-Z]{2,}$/;
var lastNamePatternRegEx = /^[a-zA-Z]{2,}$/;

var pageInitialized = false;


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
$(document).on('click', '.err-new-close', function() {
	hideError();
	hideInfo();
});


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
    $('body').addClass('body-no-scroll');
});

$('#header-slider-wrapper').click(function(){
    $('#header-slider-wrapper').removeClass('rt-panel-slide');
    $('body').removeClass('body-no-scroll');
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
function validateEmailId(elementId){
	var emailId = $('#'+elementId).val();
	if (emailId != "") {
		emailId = emailId.trim();
		if (emailRegex.test(emailId) == true) {
			return true;
		} else {
			showErrorMobileAndWeb('Please enter a valid Email Id');
			return false;
		}
	} else {
		showErrorMobileAndWeb('Please enter a valid Email Id');
		return false;
	}
}

//Function to validate the first name
function validateFirstName(elementId){
	if ($('#'+elementId).val() != "") {
		if (nameRegex.test($('#'+elementId).val()) == true) {
			return true;
		} else {
			showErrorMobileAndWeb('Please enter a valid first name');
			return false;
		}
	} else {
		showErrorMobileAndWeb('Please enter a valid first name');
		return false;
	}
}

/**
 * Function to validate the last name
 */
function validateLastName(elementId){
	if ($('#'+elementId).val() != "") {
		if (lastNameRegEx.test($('#'+elementId).val()) == true) {
			return true;
		} else {
			showErrorMobileAndWeb('Please enter a valid last name');
			return false;
		}
	} else {
		return true;
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
		showErrorMobileAndWeb("Please enter password");
		return false;
	}
	else if (password.length < minPwdLength) {
		showErrorMobileAndWeb('Password must be at least 6 characters');
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
			showErrorMobileAndWeb('Passwords do not match');
			return false;
		} else {
			return true;
		}
	} else {
		showErrorMobileAndWeb('Please enter confirm password');
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

//Function to validate the phone number
function validatePhoneNumber(elementId) {
	if ($(window).width()<768) {
		if ($('#'+elementId).val() != "") {
			if (phoneRegex.test($('#'+elementId).val()) == true) {
				return true;
			} else {
				// $('#overlay-toast').html('Please enter a valid phone number.');
				// showToast();
				showError('Please enter a valid phone number');
				return false;
			}
		} else {
			// $('#overlay-toast').html('Please enter phone number.');
			// showToast();
			showError('Please enter phone number');
			return false;
		}
	} else {
    	if ($('#'+elementId).val() != "") {
			if (phoneRegex.test($('#'+elementId).val()) == true) {
				// $('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			} else {
				// $('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid phone number.');
				// $('#'+elementId).parent().next('.login-reg-err').show();
				showError('Please enter a valid phone number');
				return false;
			}
		} else {
			// $('#'+elementId).parent().next('.login-reg-err').html('Please enter phone number.');
			// $('#'+elementId).parent().next('.login-reg-err').show();
			showError('Please enter phone number');
			return false;
		}
	}
}

//Function to validate Address 1
function validateAddress1(elementId){
	if ($('#'+elementId).val() != "") {
			return true;
	} else {
		showErrorMobileAndWeb('Please enter address');
		return false;
	}
}

function validateAddress2(elementId) {
	return true;
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

$(window).resize(function(){
   if($(window).width() > 767){
       $('#header-slider-wrapper').removeClass('rt-panel-slide');
       $('body').removeClass('body-no-scroll');
   }
});



function upgradeToPaidPlan(){
	 console.log("upgrade plan button clicked");
	 var url = "./upgradetopaidplanpage.do";
	    
	    $.ajax({
	    	url: url,
	    	type: "GET",
	    	success: function(data){
	        	$('#outer-payment').html(data);
	        	$('#outer-payment').show();
	        	},
	        error : function(e) {
	    			console.log(e);
	    		}
	    	});
}

function loadDisplayPicture(){
	console.log('Reached Method');
	var success = false;
	$.ajax({
		url : "./getdisplaypiclocation.do",
		type : "GET",
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				console.log("Image location : "+data.responseJSON);
				var imageUrl = data.responseJSON;
				if(imageUrl != '' && imageUrl != undefined){
					$("#hdr-usr-img").css("background", "url("+imageUrl+") no-repeat center");
					$("#hdr-usr-img").css("background-size","cover");
					$("#usr-initl").html("");
				}
				return data.responseJSON;
			}
		},
		error : function() {
			
		}
	});
}

/**
 * Method to redirect to error page
 */
function redirectTo404ErrorPage(){
	location.href =  window.location.origin + "/error";
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
	window.location = "/";
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
		if ($('#' + elementId).text() != "") {
			return true;
		} else {
			return false;
		}
	} else {
		if ($('#' + elementId).text() != "") {
			return true;
		} else {
			return false;
		}
	}
}

function validateCountryZipcode(elementId) {
	var zipcode = $('#' + elementId).val();
	if ($(window).width() < 768) {
		if (zipcode != "") {
			if (selectedCountryRegEx.test(zipcode) == true) {
				return true;
			} else {
				showError('Please enter a valid zipcode');
				return false;
			}
		} else {
			showError('Please enter zipcode');
			return false;
		}
	} else {
		if (zipcode != "") {
			if (selectedCountryRegEx.test(zipcode) == true) {
				return true;
			} else {
				showError('Please enter a valid zipcode');
				return false;
			}
		} else {
			showError('Please enter zipcode');
			return false;
		}
	}
}