var userManagementUserListStartIndex = 0;
var doStopAjaxRequestForUsersList = false;
var listOfBranchesForAdmin;
var isUserManagementAuthorized=true;


$(document).on('blur','#um-fname',function() {
	validateUserFirstName(this.id);
});
$(document).on('blur','#um-lname',function() {
	validateUserLastName(this.id);
});
$(document).on('blur','#um-emailid',function() {
	validateUserEmailId(this.id);
});

function initUserManagementPage() {
	paintUserDetailsForm("");
	paintUserListInUserManagement();
}

function selectBranch(element) {
	var brach = $(element).html();
	var branchId = element.id.substr("branch-".length);
	$(element).parent().children('.um-dd-wrapper').show();
	$(element).parent().toggle();
	$('#um-assignto').val(brach);
	$('#um-assignto').attr("brachId", branchId);
}

$(document).on('click', '#um-assignto-con .icn-save', function() {
	if(!isUserManagementAuthorized){
		return false;
	}
	var branchId = $('#um-assignto').attr("brachId");
	var userId = $(this).closest('.row').attr("id");
	if (!validateUserInviteDetails()) {
		return false;
	}
	if (userId == "" || userId == undefined) {
		return false;
	}
	assignUserToBranch(userId, branchId);
});

$(document).on('click', '#um-emailid-con .icn-save', function() {
	if(!isUserManagementAuthorized){
		return false;
	}
	if (!validateUserInviteDetails()) {
		return false;
	}
	inviteUser();
});

$(window).scroll(
	function(event) {
		// check if scroll position is at the bottom
		if ((window.innerHeight + window.pageYOffset) >= (document.body.offsetHeight)) {
			if (!doStopAjaxRequestForUsersList) {
				paintUserListInUserManagement();
			}
	}
});
/*
 * Function to assign branch to a user
 */
function assignUserToBranch(userId, branchId) {
	var success = false;
	var payload = {
		"userId" : userId,
		"branchId" : branchId
	};
	showOverlay();
	$.ajax({
		url : "./assignusertobranch.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#message-header>div').hasClass("success-message")) {
				success = true;
			}
		},
		complete : function() {
			if (success) {
				paintUserDetailsForm(userId);
			}
			hideOverlay();
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

/*
 * Function to usassign branch from a user
 */
function unassignUserFromBranch(userId, branchId) {

	var success = false;
	showOverlay();
	var payload = {
		"userId" : userId,
		"branchId" : branchId
	};
	$.ajax({
		url : "./unassignuserfrombranch.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#message-header>div').hasClass("success-message")) {
				success = true;
			}
		},
		complete : function() {
			$('#overlay-cancel').click();
			hideOverlay();
			if (success) {
				console.log("User successfully unassigned from branch "
						+ branchId);
				$('#branch-to-unassign-' + branchId).remove();
				// check if there are any assigned branches left
				if ($('#um-assigned-brach-container > div').length <= 0) {
					$('#um-assignto').parent().parent().find(
							'.um-item-row-icon').removeClass('icn-tick')
							.addClass('icn-save');
				}
			} else {
				alert("branch delete unsuccessful");
			}
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

function inviteUser() {

	var success = false;

	var firstName = $('#um-fname').val();
	var lastName = $('#um-lname').val();
	var emailId = $('#um-emailid').val();
	showOverlay();
	var payload = {
		"firstName" : firstName,
		"lastName" : lastName,
		"emailId" : emailId
	};
	$.ajax({
		url : "./invitenewuser.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#common-message-header').hasClass("success-message")) {
				success = true;
			}
			if (success) {
				var userId = $('#mh-userId').val();
				paintUserDetailsForm(userId);
			} else {
				var userId = $('#mh-existing-userId').val();
				if (userId != undefined || userId != "") {
					paintUserDetailsForm(userId);
					return;
				}
			}
		},
		complete : function() {
			hideOverlay();
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}


function confirmDeleteUser(userId) {
	var adminId = $('#hm-main-content-wrapper').attr("data-admin-id");
	if(userId == adminId){
		alert("Can not delete the admin account");
		return false;
	}
	$('#overlay-main').show();
	$('#overlay-continue').html("Delete");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Delete User");
	$('#overlay-text').html("Are you sure you want to delete user??<br>This action will remove all the user details.");
	$('#overlay-continue').attr("onclick", "deleteUser('" + userId + "');");
}

/*
 * Function to deactivate a user and remove from company
 */
function deleteUser(userId) {
	var success = false;
	var payload = {
		"userIdToRemove" : userId
	};
	showOverlay();
	$.ajax({
		url : "./removeexistinguser.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#message-header>div').hasClass("success-message")) {
				success = true;
			}
		},
		complete : function() {
			$('#overlay-cancel').click();
			hideOverlay();
			if (success) {
				// paint blank user details form
				$('#um-user-' + userId).remove();
				paintUserDetailsForm("");
			}
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

/*
 * Paint the user details form in the user management page
 */
function paintUserDetailsForm(userId) {
	var payload = {
		"userId" : userId
	};
	$.ajax({
		url : "./finduserandbranchesbyuserid.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#user-details-container').html(data);
		},
		complete:function(){
			if(!isUserManagementAuthorized){
				$('input').prop("disabled",true);
			}
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});

}

/*
 * Function paint the user list in user management page
 */
function paintUserListInUserManagement() {
	$.ajax({
		url : "./findusersforcompany.do",
		type : "POST",
		dataType : "html",
		success : function(data) {
			paginateUserList(data);
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

function paginateUserList(data) {
	$('#um-user-list').find('tbody').append(data);
}

/*
 * Function to activate or deactivate user
 */
function activateOrDeactivateUser(isActive, userId) {
	var isAssign;
	var success = false;
	if (isActive) {
		isAssign = "yes";
	} else {
		isAssign = "no";
	}
	var payload = {
		"isAssign" : isAssign,
		"userIdToUpdate" : userId
	};
	showOverlay();
	$.ajax({
		url : "./updateuser.do",
		type : "POST",
		dataType : "html",
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#message-header>div').hasClass("success-message")) {
				success = true;
			}
		},
		complete : function() {
			$('#overlay-cancel').click();
			hideOverlay();
			if (success) {
				if (isActive) {
					$('#icn-status-red').addClass("hide");
					$('#icn-status-green').removeClass("hide");
					$('#um-icn-status-text').html("Active");
				} else {
					$('#icn-status-green').addClass("hide");
					$('#icn-status-red').removeClass("hide");
					$('#um-icn-status-text').html("Inactive");
				}
			}
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

// function to validate input fields before sending the user invite
function validateUserInviteDetails() {
	console.log("Validating user invite input fields");
	var isFormValid = true;
	var isFocussed = false;
	var isSmallScreen = false;
	if ($(window).width() < 768) {
		isSmallScreen = true;
	}
	if (!validateUserFirstName('um-fname')) {
		isFormValid = false;
		if (!isFocussed) {
			$('#um-fname').focus();
			isFocussed = true;
		}
		if (isSmallScreen) {
			return isLoginFormValid;
		}
	}
	if (!validateUserLastName('um-lname')) {
		isFormValid = false;
		if (!isFocussed) {
			$('#um-lname').focus();
			isFocussed = true;
		}
		if (isSmallScreen) {
			return isLoginFormValid;
		}
	}
	if (!validateUserEmailId('um-emailid')) {
		isFormValid = false;
		if (!isFocussed) {
			$('#um-emailid').focus();
			isFocussed = true;
		}
		if (isSmallScreen) {
			return isLoginFormValid;
		}
	}
	return isFormValid;
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
				$('#' + elementId).next('.input-error-2').html(
						'Please enter a valid email id.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			$('#' + elementId).next('.input-error-2').html(
					'Please enter email id.');
			$('#' + elementId).next('.input-error-2').show();
			return false;
		}
	}
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
				$('#' + elementId).next('.input-error-2').html(
						'Please enter a valid first name.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			$('#' + elementId).next('.input-error-2').html(
					'Please enter first name.');
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
				$('#' + elementId).next('.input-error-2').html(
						'Please enter a valid last name.');
				$('#' + elementId).next('.input-error-2').show();
				return false;
			}
		} else {
			return true;
		}
	}
}

// Function to check if branch name entered is null or empty
function validateAssignToBranchName() {
	if ($(window).width() < 768) {
		if ($('#' + elementId).val() == "") {
			$('#overlay-toast').html('Please enter a branch.');
			showToast();
			return false;
		} else {
			return true;
		}
	} else {
		if ($('#' + elementId).val() == "") {
			$('#' + elementId).next('.input-error-2').html(
					'Please enter a branch.');
			$('#' + elementId).next('.input-error-2').show();
			return false;
		} else {
			return true;
		}
	}
}

$(document).on('click', '#overlay-cancel', function() {
	$('#overlay-continue').removeAttr("onclick");
	$('#overlay-main').hide();
});
