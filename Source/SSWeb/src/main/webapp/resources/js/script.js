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
var lastNameRegEx = /^[a-zA-Z ]*$/;
var companyNameRegEx = /^[a-zA-Z0-9 ]*$/;
var numberRegEx = /^[1-9][0-9]*?$/;
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
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (emailRegex.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid email id.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter email id.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (emailRegex.test($('#'+elementId).val()) == true) {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			}else {
				$('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid email id.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
		}else{
			$('#'+elementId).parent().next('.login-reg-err').html('Please enter email id.');
			$('#'+elementId).parent().next('.login-reg-err').show();
			return false;
		}
	}
}

//Function to validate the first name
function validateFirstName(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (nameRegex.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid first name.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter first name.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (nameRegex.test($('#'+elementId).val()) == true) {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			}else {
				$('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid first name.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
		}else{
			$('#'+elementId).parent().next('.login-reg-err').html('Please enter first name.');
			$('#'+elementId).parent().next('.login-reg-err').show();
			return false;
		}
	}
}

//Function to validate the last name
function validateLastName(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (lastNameRegEx.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid last name.');
				showToast();
				return false;
			}
		}else{
			return true;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (lastNameRegEx.test($('#'+elementId).val()) == true) {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			}else {
				$('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid last name.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
		}else{
			return true;
		}
	}
}

//function to validate a password in form
function validatePassword(elementId) {
	var password = $('#'+elementId).val();
	if($(window).width()<768){
		if (password != "") {
			//check if password length is proper
			if(password.length < minPwdLength || password.length > maxPwdLength){
				$('#overlay-toast').html('Password must be between 6-15 characters.');
				showToast();
				return false;
			}
			else if (passwordRegex.test(password) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Password must contain one special character.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter password.');
			showToast();
			return false;
		}
	}else{
		if (password != "") {
			//check if password length is proper
			if(password.length < minPwdLength || password.length > maxPwdLength){
				$('#'+elementId).parent().next('.login-reg-err').html('Password must be between 6-15 characters.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
			else if (passwordRegex.test(password) == true) {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			}else {
				$('#'+elementId).parent().next('.login-reg-err').html('Password must contain one special character.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
		}else{
			$('#'+elementId).parent().next('.login-reg-err').html('Please enter password.');
			$('#'+elementId).parent().next('.login-reg-err').show();
			return false;
		}
	}
}
//Function to match password and confirm password
function validateConfirmPassword(pwdId, confirmPwdId){
	/* === Validate passwords === */
	if($(window).width()<768){
		if($('#'+confirmPwdId).val() != ""){
			if($('#'+pwdId).val() != $('#'+confirmPwdId).val()) {
				$('#overlay-toast').html('Passwords do not match.');
				showToast();
				return false;
			}else {
				return true;
			}
		}else{
			$('#overlay-toast').html('Please enter confirm password.');
			showToast();
			return false;
		}
		
	}else{
		if($('#'+confirmPwdId).val() != ""){
			if($('#'+pwdId).val() != $('#'+confirmPwdId).val()) {
				$('#'+confirmPwdId).parent().next('.login-reg-err').html('Passwords do not match.');
				$('#'+confirmPwdId).parent().next('.login-reg-err').show();
				return false;
			}else {
				$('#'+confirmPwdId).parent().next('.login-reg-err').hide();
				return true;
			}
		}else{
			$('#'+confirmPwdId).parent().next('.login-reg-err').html('Please enter confirm password.');
			$('#'+confirmPwdId).parent().next('.login-reg-err').show();
			return false;
		}
	}
		
}


//Function to validate company name
function validateCompany(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (companyNameRegEx.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid company name.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter company name.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (companyNameRegEx.test($('#'+elementId).val()) == true) {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			}else {
				$('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid company name.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
		}else{
			$('#'+elementId).parent().next('.login-reg-err').html('Please enter company name.');
			$('#'+elementId).parent().next('.login-reg-err').show();
			return false;
		}
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
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (phoneRegex.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid phone number.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter phone number.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (phoneRegex.test($('#'+elementId).val()) == true) {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
			}else {
				$('#'+elementId).parent().next('.login-reg-err').html('Please enter a valid phone number.');
				$('#'+elementId).parent().next('.login-reg-err').show();
				return false;
			}
		}else{
			$('#'+elementId).parent().next('.login-reg-err').html('Please enter phone number.');
			$('#'+elementId).parent().next('.login-reg-err').show();
			return false;
		}
	}
}

//Function to validate Address 1
function validateAddress1(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
				return true;
		}else{
			$('#overlay-toast').html('Please enter address.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
				$('#'+elementId).parent().next('.login-reg-err').hide();
				return true;
		}else{
			$('#'+elementId).parent().next('.login-reg-err').html('Please enter address.');
			$('#'+elementId).parent().next('.login-reg-err').show();
			return false;
		}
	}
}

//Function to validate Address 2
function validateAddress2(elementId){
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

//Function to validate Region name
function validateRegionName(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
			if (companyNameRegEx.test($('#'+elementId).val()) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Please enter a valid region name.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter region name.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
			if (companyNameRegEx.test($('#'+elementId).val()) == true) {
				$('#'+elementId).next('.input-error-2').hide();
				return true;
			}else {
				$('#'+elementId).next('.input-error-2').html('Please enter a valid region name.');
				$('#'+elementId).next('.input-error-2').show();
				return false;
			}
		}else{
			$('#'+elementId).next('.input-error-2').html('Please enter region name.');
			$('#'+elementId).next('.input-error-2').show();
			return false;
		}
	}
}

//Function to validate Company/Enterprise Address 1
function validateCompanyEnterpriseAddress1(elementId){
	if($(window).width()<768){
		if ($('#'+elementId).val() != "") {
				return true;
		}else{
			$('#overlay-toast').html('Please enter address.');
			showToast();
			return false;
		}
	}else{
    	if ($('#'+elementId).val() != "") {
				$('#'+elementId).next('.input-error-2').hide();
				return true;
		}else{
			$('#'+elementId).next('.input-error-2').html('Please enter address.');
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

/*$('body').on('click','#upgrade-plan',function(){
    console.log("upgrade plan button clicked");
    var url = "./upgradepage.do";
    
    $.ajax({
    	url: url,
    	type: "GET",
    	success: function(data){
        	$('.overlay-payment').html(data);
        	$('.overlay-payment').show();
        	},
        error : function(e) {
    			console.log(e);
    		}
    	});
});*/

function upgradePlan(){
	 console.log("upgrade plan button clicked");
	 var url = "./upgradepage.do";
	    
	    $.ajax({
	    	url: url,
	    	type: "GET",
	    	success: function(data){
	        	$('.overlay-payment').html(data);
	        	$('.overlay-payment').show();
	        	},
	        error : function(e) {
	    			console.log(e);
	    		}
	    	});
}
