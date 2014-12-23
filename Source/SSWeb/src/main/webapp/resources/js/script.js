var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/;
var zipcodeRegex = /^\d{5}([\-]?\d{4})?$/;
var phoneRegex = /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/;
var passwordRegex = /^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+=|<>?{}~-]).{6,15}$/;
var nameRegex = /^[a-zA-Z]*$/;
var minPwdLength = 6;
var maxPwdLength = 15;

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
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 101) + 'px')
    }else if(is_firefox){
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 97) + 'px')
    }else{
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 97) + 'px')
    }
}

$('#hm-item-dd-top').click(function(){
   $('#hm-dd-wrapper-top').slideToggle(200);
});

$('#hm-item-dd-bottom').click(function(){
   $('#hm-dd-wrapper-bottom').slideToggle(200);
});

$('#header-menu-icn').click(function(){
    $('#header-links').slideToggle(200);
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
	if ($('#'+elementId).val() != "") {
		if (emailRegex.test($('#'+elementId).val()) == true) {
			$('#'+elementId).parent().removeClass('input-error');
			$('#jsError').addClass('hide');
			return true;
		}else {
			$('#jsErrTxt').html('Please enter a valid Email Id.');
			$('#jsError').removeClass('hide');
			$('#'+elementId).parent().addClass('input-error');
			return false;
		}
	}else{
		$('#jsErrTxt').html('Please enter the Email Id.');
		return false;
	}
}

//Function to validate the first name
function validateFirstName(elementId){
	if ($('#'+elementId).val() != "") {
		if (nameRegex.test($('#'+elementId).val()) == true) {
			$('#'+elementId).parent().removeClass('input-error');
			$('#jsError').addClass('hide');
			return true;
		}else {
			$('#jsErrTxt').html('Please enter a valid first name.');
			$('#jsError').removeClass('hide');
			$('#'+elementId).parent().addClass('input-error');
			return false;
		}
	}else{
		$('#jsErrTxt').html('Please enter the First Name.');
		return false;
	}
}

//Function to validate the last name
function validateLastName(elementId){
	if ($('#'+elementId).val() != "") {
		if (nameRegex.test($('#'+elementId).val()) == true) {
			$('#'+elementId).parent().removeClass('input-error');
			$('#jsError').addClass('hide');
			return true;
		}else {
			$('#jsErrTxt').html('Please enter a valid last name.');
			$('#jsError').removeClass('hide');
			$('#'+elementId).parent().addClass('input-error');
			return false;
		}
	}
}

//function to validate a password in form
function validatePassword(elementId) {
	var password = $('#'+elementId).val();
	if (password != "") {
		//check if password length is proper
		if(password.length < minPwdLength || password.length > maxPwdLength){
			$('#jsErrTxt').html('Password must be between 6-15 characters.');
			$('#jsError').removeClass('hide');
			$('#'+elementId).parent().addClass('input-error');
			return false;
		}
		if (passwordRegex.test(password) == true) {
			$('#'+elementId).parent().removeClass('input-error');
			$('#jsError').addClass('hide');
			return true;
		}else {
			$('#jsErrTxt').html('Password must have at least one special character.');
			$('#jsError').removeClass('hide');
			$('#'+elementId).parent().addClass('input-error');
			return false;
		}
	}else{
		$('#jsErrTxt').html('Please enter the Password.');
		return false;
	}
}

//Function to match password and confirm password
function validateConfirmPassword(pwdId, confirmPwdId){
	/* === Validate passwords === */
	if($('#'+pwdId).val() != $('#'+confirmPwdId).val()) {
		$('#'+pwdId).parent().addClass('input-error');
		$('#'+confirmPwdId).parent().addClass('input-error');
		$('#jsError').show();
        $('#jsErrTxt').html('Passwords do not match');
		return false;
	}else {
		$('#jsError').hide();
		$('#'+pwdId).parent().removeClass('input-error');
		$('#'+confirmPwdId).parent().removeClass('input-error');
		return true;
	}
}
