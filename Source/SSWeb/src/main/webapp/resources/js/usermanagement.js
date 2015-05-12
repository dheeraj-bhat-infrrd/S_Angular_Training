var userStartIndex = 0;
var userBatchSize = 10;
var doStopAjaxRequestForUsersList = false;
var listOfBranchesForAdmin;
var isUserManagementAuthorized = true;
var isAddUser = true;

$(document).on('click', '.um-user-row', function() {
	if (!isUserManagementAuthorized)
		return false;
	console.log("user row clicked");
	isAddUser = false;
	var userId = this.id;
	userId = userId.substr("um-user-".length);
	paintUserDetailsForm(userId);
});

$(document).on('click', '.tm-table-remove-icn', function(event) {
	if (!isUserManagementAuthorized)
		return false;
	event.stopPropagation();
	var userId = $(this).closest('.row').attr("data-id");
	userId = userId.substr("um-user-".length);
	confirmDeleteUser(userId);
});

$(document).on('blur', '#um-fname', function() {
	validateUserFirstName(this.id);
});
$(document).on('blur', '#um-lname', function() {
	validateUserLastName(this.id);
});
$(document).on('blur', '#um-emailid', function() {
	validateUserEmailId(this.id);
});

function initUserManagementPage() {
	userStartIndex = 0;
	paintUserListInUserManagement(userStartIndex);
	updatePaginateButtons();
}

function selectBranch(element) {
	var branch = $(element).html();
	var branchId = element.id.substr("branch-".length);
	$(element).parent().children('.um-dd-wrapper').show();
	$(element).parent().toggle();
	$('#um-assignto').val(branch);
	$('#um-assignto').attr("branchId", branchId);
}

$(document).on('click', '#um-add-user', function() {
	if (!isUserManagementAuthorized) {
		return false;
	}
	
	var userId;
	if (isAddUser) {
		// TODO Add code to create a new user.
		if (!validateUserInviteDetails()) {
			return false;
		}
		inviteUser();
		isAddUser=false;
		userId = $('#mh-userId').val();
	} else {
		userId = $('#um-user-details-container').attr("data-id");
	}
	
	if (!validateUserInviteDetails()) {
		return false;
	}
	if (userId == "" || userId == undefined) {
		return false;
	}

	var branchId = $('#um-assignto').attr("branchId");
	assignUserToBranch(userId, branchId);
});

$(document).on('click', '#um-clear-user-form', function() {
	if (!isUserManagementAuthorized) {
		return false;
	}
	isAddUser=true;
	paintUserDetailsForm("");
	/*if (!validateUserInviteDetails()) {
		return false;
	}
	inviteUser();*/
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
			if ($('#common-message-header').hasClass("success-message")) {
				success = true;
			}
			if ($('#common-message-header').hasClass("error-message")) {
				createPopupInfo("Error!", $('#message-header p').text());
			}
		},
		complete : function() {
			if (success) {
				paintUserDetailsForm(userId);
				userStartIndex = 0;
				paintUserListInUserManagement();
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
			if ($('#common-message-header').hasClass("success-message")) {
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
				if ($('#um-assigned-branch-container > div').length <= 0) {
					$('#um-assignto').parent().parent().find(
							'.um-item-row-icon').removeClass('icn-tick')
							.addClass('icn-save');
				}
			} else {
				createPopupInfo("Error!", "Branch deletion unsuccessful");
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
		async : false,
		data : payload,
		success : function(data) {
			$('#message-header').html(data);
			if ($('#common-message-header').hasClass("success-message")) {
				success = true;
			}
			if (success) {
				var userId = $('#mh-userId').val();
				paintUserDetailsForm(userId);
				userStartIndex = 0;
				paintUserListInUserManagement();
			} else {
				var userId = $('#mh-existing-userId').val();
				if (userId == undefined || userId == "") {
					createPopupInfo("Limit Exceeded",
							"Maximum limit of users exceeded.");
				} else {
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

function createPopupInfo(header, body) {
	$('#overlay-header').html(header);
	$('#overlay-continue').attr("disabled", true);
	$('#overlay-continue').addClass("btn-disabled");
	$('#overlay-cancel').html('OK');
	$('#overlay-text').html(body);
	$('#overlay-main').show();
	$('#overlay-continue').removeClass("btn-disabled");
}

$(document).on('click', '#overlay-cancel', function() {
	$('#overlay-continue').removeAttr("onclick");
	$('#overlay-main').hide();
});

function confirmDeleteUser(userId, adminId) {
	if (userId == adminId) {
		$('#overlay-continue').hide();
		createPopupInfo("Access Denied", "Can not delete the admin account !!!");
		return;
	}
	
	$('#overlay-main').show();
	$('#overlay-continue').show();
	$('#overlay-continue').html("Delete");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Delete User");
	$('#overlay-text').html("Are you sure you want to delete user ?");
	$('#overlay-continue').attr("onclick", "deleteUser('" + userId + "');");
}

/*
 * Function to deactivate a user and remove from company
 */
function deleteUser(userId) {
	$('#overlay-continue').removeAttr("onclick");
	$('#overlay-main').hide();
	
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
			var map =  $.parseJSON(data);
			if (map.status == "success") {
				showInfo(map.message);
			} else {
				showError(map.message);
			}
			
			// hide the row of the user deleted
			$('#user-row-' + userId).next('.v-tbl-row').remove();
			$('#user-row-' + userId).next('.u-tbl-row').remove();
			$('#user-row-' + userId).remove();
		},
		complete : function() {
			hideOverlay();
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
		complete : function() {
			if (!isUserManagementAuthorized) {
				$('input').prop("disabled", true);
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
function paintUserListInUserManagement(startIndex) {
	var payload = {
		"startIndex" : startIndex,
		"batchSize" : userBatchSize
	};

	$.ajax({
		url : "./findusersforcompany.do",
		type : "GET",
		data : payload,
		dataType : "html",
		success : function(data) {
			$('#user-list').html(data);
			userStartIndex = startIndex;
			updatePaginateButtons();
			bindEditUserClick();
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
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
			if ($('#common-message-header').hasClass("success-message")) {
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

/**
 * Function to check if branch name entered is null or empty
 */
function validateAssignToBranchName() {
	if ($('#' + elementId).val() == "") {
		showErrorMobileAndWeb("Please enter a branch name");
		return false;
	} else {
		return true;
	}
}

function searchUsersByNameEmailLoginId(searchKey) {
	userStartIndex = 0;
	var url = "./findusers.do";
	var payload = {
		"searchKey" : searchKey
	};
	callAjaxGetWithPayloadData(url, searchUsersByNameEmailLoginIdCallBack, payload, true);
}

function searchUsersByNameEmailLoginIdCallBack(data) {
	$('#user-list').html(data);
}

function paintUsersList(data) {
	if (userStartIndex == 0) {
		$('#um-user-list').find('tbody').html("");
	}
	userStartIndex += data.length;
	var searchResult = data;
	if (searchResult != null) {
		var len = searchResult.length;
		if (len > 0) {
			$.each(searchResult, function(i, user) {
				var row = $('<tr>').attr({
					"id" : "um-user-" + user.userId,
					"class" : "um-user-row"
				});
				
				var col1 = $('<td>').attr({
					"class" : "col-username um-table-content"
				}).html(user.firstName + " " + user.lastName);
				
				var col2 = $('<td>').attr({
					"class" : "col-email um-table-content"
				}).html(user.emailId);
				
				var col3 = $('<td>').attr({
					"class" : "col-loanoff um-table-content clearfix"
				});
				
				if (user.isAgent) {
					var colImage = $('<div>').attr({
						"class" : "float-left tm-table-tick-icn icn-right-tick"
					});
					col3.append(colImage);
				}
				
				var col4 = $('<td>').attr({
					"class" : "col-status um-table-content clearfix"
				});
				
				if (user.status == 1) {
					var statusIcon = $('<div>').attr({
						"class" : "tm-table-status-icn icn-green-col float-left"
					});
					col4.append(statusIcon);
				} else if (user.status == 3) {
					var statusIcon = $('<div>').attr({
						"class" : "tm-table-status-icn icn-green-brown float-left"
					});
					col4.append(statusIcon);
				}
				
				var col5 = $('<td>').attr({
					"class" : "col-remove um-table-content clearfix"
				});
				
				var iconRemove = $('<div>').attr({
					"class" : "tm-table-remove-icn icn-remove-user float-left cursor-pointer"
				});
				
				col5.append(iconRemove);
				row.append(col1).append(col2).append(col3).append(col4).append(col5);
				$('#um-user-list').find('tbody').append(row);
			});
		} else {
			$('#um-user-list').find('tbody').append("No results found");
		}
	}
}

function paginateUsersList() {
	if (!doStopAjaxRequestForUsersList) {
		paintUserListInUserManagement();
	}
}

$(document).on('keyup', '#um-assignto', function() {
	searchBranchesForUser(this.value);
});

/**
 * Method to perform search on solr for provided branch pattern
 * 
 * @param branchPattern
 */
function searchBranchesForUser(branchPattern) {
	var url = "./searchbranches.do?branchPattern=" + branchPattern;
	$.ajax({
		url : url,
		type : "GET",
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				searchBranchesForUserCallBack(data.responseJSON);
			}
		},
		error : function() {

		}
	});
}

function searchBranchesForUserCallBack(jsonData) {
	var branchListContainer = $('<div>').attr({
		"class" : "um-branch-list"
	});
	
	$('#um-assignto').parent().find('.um-branch-list').remove();
	var searchResult = jsonData;
	if (searchResult != null) {
		var len = searchResult.length;
		if (len > 0) {
			$.each(searchResult, function(i, branch) {
				var branchDiv = $('<div>').attr({
					"id" : "branch-" + branch.branchId,
					"class" : "um-dd-wrapper cursor-pointer",
					"onclick" : "selectBranch(this);"
				}).html(branch.branchName);
				branchListContainer.append(branchDiv);
			});
		}
		$('#um-assignto').parent().append(branchListContainer);
	}
}

/*
 * Function fetch assignments for user
 */
function getUserAssignments(userId) {
	var url = "./finduserassignments.do?userId=" + userId; 
	callAjaxGET(url, function(data){
		$('#user-details-and-assignments-' + userId).html(data);
		
		var assignToOption = $("#assign-to-txt").attr('data-assignto');
		showSelectorsByAssignToOption(assignToOption);
		
		/**
		 * bind the click and keyup events
		 */		
		bindAssignToSelectorClick();
		bindOfficeSelectorEvents();
		bindRegionSelectorEvents();
		bindAdminCheckBoxClick();
		
		// de-activate user profile
        $('.tbl-switch-on').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 0);
        });

        // activate user profile
        $('.tbl-switch-off').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 1);
        });
		
		$("#btn-save-user-assignment").click(function(e){
			if(validateIndividualForm()){
				saveUserAssignment("user-assignment-form");
				
				// refreshing right section after assignment
				setTimeout(function() {
					getUserAssignments(userId);
				}, 2000);
			}
		});
		
		setTimeout(function() {
			$('#profile-tbl-wrapper-' + userId).perfectScrollbar();
		}, 1000);

		$(document).on('click', 'body', function() {
            $('.dd-droplist').slideUp(200);
        });
	} , true);
}

/**
 * Method to save the assignment of user with branch/region or company
 * @param formId
 */
function saveUserAssignment(formId) {
	var url = "./addindividual.do";
	showOverlay();
	callAjaxFormSubmit(url, saveUserAssignmentCallBack, formId);
}

/**
 * callback for saveUserAssignment
 * @param data
 */
function saveUserAssignmentCallBack(data) {
	hideOverlay();
	displayMessage(data);
}

/**
 * Method to send invite link
 */
function reinviteUser(firstName, lastName, emailId) {
	var payload = {
		"firstName" : firstName,
		"lastName" : lastName,
		"emailId" : emailId
	};
	var url="./reinviteuser.do";
	showOverlay();
	callAjaxGetWithPayloadData(url, reinviteUserCallBack, payload, true);
}

function reinviteUserCallBack(data){
	var map =  $.parseJSON(data);
	if (map.status == "success") {
		showInfo(map.message);
	} else {
		showError(map.message);
	}
 }

function updateUserProfile(profileId, profileStatus) {
	showOverlay();
	var payload = {
		"profileId" : profileId,
		"status" : profileStatus
	};
	callAjaxPostWithPayloadData("./updateuserprofile.do", function(data) {
		hideOverlay();
		
		var map =  $.parseJSON(data);
		if (map.status == "success") {
			showInfo(map.message);
			if (profileStatus == 1) {
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').removeClass('tbl-switch-off');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').addClass('tbl-switch-on');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').attr('title', 'Active');
				
				// de-activate user profile
				$('.tbl-switch-on').unbind('click');
		        $('.tbl-switch-on').click(function(){
		            var profileId = $(this).parent().data('profile-id');
		            updateUserProfile(profileId, 0);
		        });
			}
			else if (profileStatus == 0) {
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').removeClass('tbl-switch-on');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').addClass('tbl-switch-off');
				$('#v-edt-tbl-row-' + profileId).find('.v-edt-tbl-switch').attr('title', 'InActive');

				// activate user profile
				$('.tbl-switch-off').unbind('click');
		        $('.tbl-switch-off').click(function(){
		            var profileId = $(this).parent().data('profile-id');
		            updateUserProfile(profileId, 1);
		        });
			}
		} else {
			showError(map.message);
		}
	}, payload, false);
}

function bindEditUserClick(){
	$('.edit-user').click(function(e){
		e.stopPropagation();
		if ($(this).hasClass('v-tbl-icn-disabled')) {
			return;
		}
		
		// de-activate user profile
        $('.tbl-switch-on').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 0);
        });

        // activate user profile
        $('.tbl-switch-off').click(function(){
            var profileId = $(this).parent().data('profile-id');
            updateUserProfile(profileId, 1);
        });

		if ($(this).parent().hasClass('u-tbl-row-sel')) {
	        $(this).parent().removeClass('u-tbl-row-sel');
	        $(this).parent().next('.user-assignment-edit-row').slideUp(200);
	    } else {
	        // make an ajax call and fetch the details of the user
	        var userId = $(this).parent().find('.fetch-name').attr('data-user-id');
			$(".user-assignment-edit-div").html("");
			$(".user-row").removeClass('u-tbl-row-sel');
			$(".user-assignment-edit-row").slideUp();

			getUserAssignments(userId);

	        $(this).parent().next('.user-assignment-edit-row').slideDown(200);
	        $(this).parent().addClass('u-tbl-row-sel');
	        
			setTimeout(function() {
				$('#profile-tbl-wrapper-' + userId).perfectScrollbar();
			}, 1000);
	    }
	});
}

$(document).on('click', '#page-previous', function(){
	var newIndex = userStartIndex - userBatchSize;
	if (newIndex < $('#users-count').val()) {
		paintUserListInUserManagement(newIndex);
	}
});

$(document).on('click', '#page-next', function(){
	var newIndex = userStartIndex + userBatchSize;
	if (newIndex < $('#users-count').val()) {
		paintUserListInUserManagement(newIndex);
	}
});

function updatePaginateButtons() {
	// next button
	if (userStartIndex <= 0) {
		$('#page-previous').removeClass('paginate-button');
	} else {
		$('#page-previous').addClass('paginate-button');
	}
	
	// previous button
	if (userStartIndex + userBatchSize >= $('#users-count').val()) {
		$('#page-next').removeClass('paginate-button');
	} else {
		$('#page-next').addClass('paginate-button');
	}
}