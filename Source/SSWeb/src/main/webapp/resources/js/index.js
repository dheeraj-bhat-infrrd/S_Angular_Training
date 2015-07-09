// Function to validate login user name
function validateUserId(elementId) {
	if ($('#' + elementId).val() != "") {
		if (emailRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			$('#overlay-toast').html('Please enter a valid user name.');
			showToast();
			return false;
		}
	} else {
		$('#overlay-toast').html('Please enter user name.');
		showToast();
		return false;
	}
}

// Function to validate login password
function validateLoginPassword(elementId) {
	if ($('#' + elementId).val() != "") {
		return true;
	} else {
		$('#overlay-toast').html('Please enter password.');
		showToast();
		return false;
	}
}

// Function to validate the login form
function validateLoginForm(id) {
	// hide the server error
	$("#serverSideerror").hide();
	if (!validateUserId('login-user-id')) {
		$('#login-user-id').focus();
		return false;
	}
	if (!validateLoginPassword('login-pwd')) {
		$('#login-pwd').focus();
		return false;
	}
	return true;
}

// Function to validate registration form
function validateRegistrationForm(id) {
	// hide the server error
	$("#serverSideerror").hide();
	// Validate form input elements
	if (!validateRegFirstName('reg-fname')) {
		$('#reg-fname').focus();
		return false;
	}
	if (!validateRegLastName('reg-lname')) {
		$('#reg-lname').focus();
		return false;

	}
	if (!validateRegEmailId('reg-email')) {
		$('#reg-email').focus();
		return false;

	}
	
	if (!validateRegPassword('reg-pwd')) {
		$('#reg-pwd').focus();
		return false;
	}
	if (!validateRegConfirmPassword('reg-pwd', 'reg-conf-pwd')) {
		$('#reg-conf-pwd').focus();
		return false;
	}
	return true;
}

function validatePreRegistrationForm(id) {
	// hide the server error
	$("#serverSideerror").hide();
	// Validate form input elements
	if (!validateRegFirstName('reg-fname')) {
		$('#reg-fname').focus();
		return false;
	}
	if (!validateRegLastName('reg-lname')) {
		$('#reg-lname').focus();
		return false;
	}
	if (!validateRegEmailId('reg-email')) {
		$('#reg-email').focus();
		return false;
	}
	return true;
}

// Function to validate email id in a form
function validateRegEmailId(elementId) {
	if ($('#' + elementId).val() != "") {
		if (emailRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			// $('#overlay-toast').html('Please enter a valid email id.');
			// showToast();
			showRegErr('Please enter a valid email address');
			return false;
		}
	} else {
		// $('#overlay-toast').html('Please enter email id.');
		// showToast();
		showRegErr('Please enter email address');
		return false;
	}
}

// Function to validate the first name
function validateRegFirstName(elementId) {
	if ($('#' + elementId).val() != "") {
		if (nameRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			// $('#overlay-toast').html('Please enter a valid first name.');
			// showToast();
			showRegErr('Please enter a valid first name');
			return false;
		}
	} else {
		// $('#overlay-toast').html('Please enter first name.');
		// showToast();
		showRegErr('Please enter first name');
		return false;
	}
}

// Function to validate the last name
function validateRegLastName(elementId) {
	if ($('#' + elementId).val() != "") {
		if (lastNameRegEx.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			// $('#overlay-toast').html('Please enter a valid last name.');
			// showToast();
			showRegErr('Please enter a valid last name.');
			return false;
		}
	} else {
		showRegErr('Please enter a valid last name.');
		return false;
	}
}

// function to validate a password in form
function validateRegPassword(elementId) {
	var password = $('#' + elementId).val();
	if (password != "") {
		// check if password length is proper
		if (password.length < minPwdLength || password.length > maxPwdLength) {
			$('#overlay-toast').html('Password must be between 6-15 characters.');
			showToast();
			return false;
		} else if (passwordRegex.test(password) == true) {
			return true;
		} else {
			$('#overlay-toast').html(
					'Password must contain one special character.');
			showToast();
			return false;
		}
	} else {
		$('#overlay-toast').html('Please enter password.');
		showToast();
		return false;
	}
}

// Function to match password and confirm password
function validateRegConfirmPassword(pwdId, confirmPwdId) {
	/* === Validate passwords === */
	if ($('#' + confirmPwdId).val() != "") {
		if ($('#' + pwdId).val() != $('#' + confirmPwdId).val()) {
			$('#overlay-toast').html('Passwords do not match.');
			showToast();
			return false;
		} else {
			return true;
		}
	} else {
		$('#overlay-toast').html('Please enter confirm password.');
		showToast();
		return false;
	}

}