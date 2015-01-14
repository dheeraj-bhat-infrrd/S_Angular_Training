var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var passwordRegex = /^(?=.*[a-zA-Z0-9])(?=.*[!@#$%&*()_+=|<>?{}~-]).{6,15}$/;
var minPwdLength = 6;
var maxPwdLength = 15;



function showOverlay(){
    $('.overlay-loader').show();
}

function hideOverlay(){
    $('.overlay-loader').hide();
}

//show Toast
function showToast(){
    $('#overlay-toast').fadeIn();
    setTimeout(function(){
        $('#overlay-toast').fadeOut();
    },3000);
}
//function to validate current password
function validateCurrentPassword(elementId) {
	var oldpassword = $('#'+elementId).val();
	if($(window).width()<768){
		if (oldpassword != "") {
			//check if current password length is proper
			if(oldpassword.length < minPwdLength || oldpassword.length > maxPwdLength){
				$('#overlay-toast').html('Password must be between 6-15 characters.');
				showToast();
				return false;
			}
			else if (passwordRegex.test(oldpassword) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Password must contain one special character.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter newpassword.');
			showToast();
			return false;
		}
	}else{
		if (oldpassword != "") {
			//check if current password length is proper
			if(oldpassword.length < minPwdLength || oldpassword.length > maxPwdLength){
				$('#'+elementId).next('.input-error-2').html('Password must be between 6-15 characters.');
				$('#'+elementId).next('.input-error-2').show();
				return false;
			}
			else if (passwordRegex.test(oldpassword) == true) {
				$('#'+elementId).next('.input-error-2').hide();
				return true;
			}else {
				$('#'+elementId).next('.input-error-2').html('Password must contain one special character.');
				$('#'+elementId).next('.input-error-2').show();
				return false;
			}
		}else{
			$('#'+elementId).next('.input-error-2').html('Please enter current password.');
			$('#'+elementId).next('.input-error-2').show();
			return false;
		}
	}
}

//function to validate new password
function validateNewPassword(elementId) {
	var newpassword = $('#'+elementId).val();
	if($(window).width()<768){
		if (newpassword != "") {
			//check if newpassword length is proper
			if(newpassword.length < minPwdLength || newpassword.length > maxPwdLength){
				$('#overlay-toast').html('Password must be between 6-15 characters.');
				showToast();
				return false;
			}
			else if (passwordRegex.test(newpassword) == true) {
				return true;
			}else {
				$('#overlay-toast').html('Password must contain one special character.');
				showToast();
				return false;
			}
		}else{
			$('#overlay-toast').html('Please enter newpassword.');
			showToast();
			return false;
		}
	}else{
		if (newpassword != "") {
			//check if new password length is proper
			if(newpassword.length < minPwdLength || newpassword.length > maxPwdLength){
				$('#'+elementId).next('.input-error-2').html('Password must be between 6-15 characters.');
				$('#'+elementId).next('.input-error-2').show();
				return false;
			}
			else if (passwordRegex.test(newpassword) == true) {
				$('#'+elementId).next('.input-error-2').hide();
				return true;
			}else {
				$('#'+elementId).next('.input-error-2').html('Password must contain one special character.');
				$('#'+elementId).next('.input-error-2').show();
				return false;
			}
		}else{
			$('#'+elementId).next('.input-error-2').html('Please enter newpassword.');
			$('#'+elementId).next('.input-error-2').show();
			return false;
		}
	}
}
//Function to match new password and confirm new password
function validateConfirmNewPassword(pwdId, confirmPwdId){
	/* === Validate new passwords === */
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
			$('#overlay-toast').html('Please enter confirm newpassword.');
			showToast();
			return false;
		}
		
	}else{
		if($('#'+confirmPwdId).val() != ""){
			if($('#'+pwdId).val() != $('#'+confirmPwdId).val()) {
				$('#'+confirmPwdId).next('.input-error-2').html('Passwords do not match.');
				$('#'+confirmPwdId).next('.input-error-2').show();
				return false;
			}else {
				$('#'+confirmPwdId).next('.input-error-2').hide();
				return true;
			}
		}else{
			$('#'+confirmPwdId).next('.input-error-2').html('Please enter confirm new password.');
			$('#'+confirmPwdId).next('.input-error-2').show();
			return false;
		}
	}
		
}


