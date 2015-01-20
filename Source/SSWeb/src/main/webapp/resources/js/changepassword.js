// function to validate current password
function validateCurrentPassword(elementId) {
	var oldpassword = $('#' + elementId).val();
	if ($(window).width() < 768) {
		if (oldpassword != "") {
			// check if current password length is proper
			if (oldpassword.length < minPwdLength
					|| oldpassword.length > maxPwdLength) {
				$('#overlay-toast').html(
						'Password must be between 6-15 characters.');
				showToast();
				return false;
			} else if (passwordRegex.test(oldpassword) == true) {
				return true;
			} else {
				$('#overlay-toast').html(
						'Password must contain one special character.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('Please enter newpassword.');
			showToast();
			return false;
		}
	} else {
		if (oldpassword != "") {
			// check if current password length is proper
			if (oldpassword.length < minPwdLength
					|| oldpassword.length > maxPwdLength) {
				$('#' + elementId).parent().next('.cp-err').html(
						'Password must be between 6-15 characters.');
				$('#' + elementId).parent().next('.cp-err').show();
				return false;
			} else if (passwordRegex.test(oldpassword) == true) {
				$('#' + elementId).parent().next('.cp-err').hide();
				return true;
			} else {
				$('#' + elementId).parent().next('.cp-err').html(
						'Password must contain one special character.');
				$('#' + elementId).parent().next('.cp-err').show();
				return false;
			}
		} else {
			$('#' + elementId).parent().next('.cp-err').html(
					'Please enter current password.');
			$('#' + elementId).parent().next('.cp-err').show();
			return false;
		}
	}
}

// function to validate new password
function validateNewPassword(elementId) {
	var newpassword = $('#' + elementId).val();
	if ($(window).width() < 768) {
		if (newpassword != "") {
			// check if new password length is proper
			if (newpassword.length < minPwdLength
					|| newpassword.length > maxPwdLength) {
				$('#overlay-toast').html(
						'Password must be between 6-15 characters.');
				showToast();
				return false;
			} else if (passwordRegex.test(newpassword) == true) {
				return true;
			} else {
				$('#overlay-toast').html(
						'Password must contain one special character.');
				showToast();
				return false;
			}
		} else {
			$('#overlay-toast').html('Please enter new password.');
			showToast();
			return false;
		}
	} else {
		if (newpassword != "") {
			// check if new password length is proper
			if (newpassword.length < minPwdLength
					|| newpassword.length > maxPwdLength) {
				$('#' + elementId).parent().next('.cp-err').html(
						'Password must be between 6-15 characters.');
				$('#' + elementId).parent().next('.cp-err').show();
				return false;
			} else if (passwordRegex.test(newpassword) == true) {
				$('#' + elementId).parent().next('.cp-err').hide();
				return true;
			} else {
				$('#' + elementId).parent().next('.cp-err').html(
						'Password must contain one special character.');
				$('#' + elementId).parent().next('.cp-err').show();
				return false;
			}
		} else {
			$('#' + elementId).parent().next('.cp-err').html(
					'Please enter new password.');
			$('#' + elementId).parent().next('.cp-err').show();
			return false;
		}
	}
}
// Function to match new password and confirm new password
function validateConfirmNewPassword(pwdId, confirmPwdId) {
	/* === Validate new passwords === */
	if ($(window).width() < 768) {
		if ($('#' + confirmPwdId).val() != "") {
			if ($('#' + pwdId).val() != $('#' + confirmPwdId).val()) {
				$('#overlay-toast').html('Passwords do not match.');
				showToast();
				return false;
			} else {
				return true;
			}
		} else {
			$('#overlay-toast').html('Please enter confirm new password.');
			showToast();
			return false;
		}

	} else {
		if ($('#' + confirmPwdId).val() != "") {
			if ($('#' + pwdId).val() != $('#' + confirmPwdId).val()) {
				$('#' + confirmPwdId).parent().next('.cp-err').html(
						'Passwords do not match.');
				$('#' + confirmPwdId).parent().next('.cp-err').show();
				return false;
			} else {
				$('#' + confirmPwdId).parent().next('.cp-err').hide();
				return true;
			}
		} else {
			$('#' + confirmPwdId).parent().next('.cp-err').html(
					'Please enter confirm new password.');
			$('#' + confirmPwdId).parent().next('.cp-err').show();
			return false;
		}
	}

}
