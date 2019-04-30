var doStopAjaxRequestForUsersListMultiSelect = false;

$(document).on('click', '.ms-ba-dropdown', function (e) {
	e.stopPropagation();

	$(this).find('.ms-ba-options').slideToggle('fast');
	$(this).find('.ms-ba-chevron-down').toggle();
	$(this).find('.ms-ba-chevron-up').toggle();

	$(document).off('click', '.ms-option');
	$(document).on('click', '.ms-option', function (e) {
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();
		
		if ($(this).hasClass('ms-option-locked')) {
			return;
		}

		$(this).closest('.ms-ba-options').slideToggle('fast');
		$(this).closest('.ms-ba-dropdown').find('.ms-ba-chevron-down').toggle();
		$(this).closest('.ms-ba-dropdown').find('.ms-ba-chevron-up').toggle();

		if ($(this).hasClass('ms-reinvite')) {
			selectMultiSelectAction('ms-reinvite');
		} else if ($(this).hasClass('ms-delete')) {
			selectMultiSelectAction('ms-delete');
		} else if ($(this).hasClass('ms-smadmin')) {
			selectMultiSelectAction('ms-smadmin');
		} else if ($(this).hasClass('ms-assign-reg')) {
			selectMultiSelectAction('ms-assign-reg');
		} else if ($(this).hasClass('ms-assign-bra')) {
			selectMultiSelectAction('ms-assign-bra');
		} else if ($(this).hasClass('ms-auto-post-score')) {
			selectMultiSelectAction('ms-auto-post-score');
		} else if ($(this).hasClass('ms-prof-image')) {
			selectMultiSelectAction('ms-prof-image');
		} else if ($(this).hasClass('ms-logo-image')) {
			selectMultiSelectAction('ms-logo-image');
		}
	});
});

$(document).on('click', '.ms-filters-dropdown', function (e) {
	e.stopPropagation();

	$(this).find('.ms-filters-options').slideToggle('fast');
	$(this).find('.ms-fil-chevron-down').toggle();
	$(this).find('.ms-fil-chevron-up').toggle();

	$(document).off('click', '.ms-filter');
	$(document).on('click', '.ms-filter', function (e) {
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();

		$(this).closest('.ms-filters-options').slideToggle('fast');
		$(this).closest('.ms-filters-dropdown').find('.ms-fil-chevron-down').toggle();
		$(this).closest('.ms-filters-dropdown').find('.ms-fil-chevron-up').toggle();

		$('.ms-reinvite').show();
		
		if ($(this).hasClass('ms-sel-all')) {
			selectFilter('ms-sel-all', this);
			$(this).html('Deselect and Reset Users');
			$(this).removeClass('ms-sel-all');
			$(this).addClass('ms-dsel-all');
			$('.ms-sel-all').html('Deselect and Reset Users');
			$('.ms-sel-all').addClass('ms-dsel-all');
			$('.ms-sel-all').removeClass('ms-sel-all');
			
			$('.ms-dsel-all-unv').html('Select all Unverified users');
			$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
			$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

			$('.ms-dsel-all-ver').html('Select all Verified users');
			$('.ms-dsel-all-ver').addClass('ms-sel-verified');
			$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

			$('.ms-filter-hdr-txt').html('Selected all active users');
		} else if ($(this).hasClass('ms-sel-unverified')) {
			selectFilter('ms-sel-unverified', this);
			$(this).html('Deselect and Reset Users');
			$(this).removeClass('ms-sel-unverified');
			$(this).addClass('ms-dsel-all-unv');
			$('.ms-sel-unverified').html('Deselect and Reset Users');
			$('.ms-sel-unverified').addClass('ms-dsel-all-unv');
			$('.ms-sel-unverified').removeClass('ms-sel-unverified');

			$('.ms-dsel-all').addClass('ms-sel-all');
			$('.ms-dsel-all').removeClass('ms-dsel-all');

			$('.ms-dsel-all-ver').html('Select all Verified users');
			$('.ms-dsel-all-ver').addClass('ms-sel-verified');
			$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

			$('.ms-filter-hdr-txt').html('Selected all Unverified users');
		} else if ($(this).hasClass('ms-sel-verified')) {
			selectFilter('ms-sel-verified', this);
			$('.ms-reinvite').hide();
			$(this).html('Deselect and Reset Users');
			$(this).removeClass('ms-sel-verified');
			$(this).addClass('ms-dsel-all-ver');
			$('.ms-sel-verified').html('Deselect and Reset Users');
			$('.ms-sel-verified').addClass('ms-dsel-all-ver');
			$('.ms-sel-verified').removeClass('ms-sel-verified');

			$('.ms-dsel-all').addClass('ms-sel-all');
			$('.ms-dsel-all').removeClass('ms-dsel-all');

			$('.ms-dsel-all-unv').html('Select all Unverified users');
			$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
			$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

			$('.ms-filter-hdr-txt').html('Selected all Verified users');
		} else if ($(this).hasClass('ms-dsel-all')) {
			selectFilter('ms-dsel-all', this);
			$('.ms-dsel-all').addClass('ms-sel-all');
			$('.ms-dsel-all').removeClass('ms-dsel-all');

			$('.ms-filter-hdr-txt').html('Select Filter');
		} else if ($(this).hasClass('ms-dsel-all-unv')) {
			selectFilter('ms-dsel-all', this);
			$('.ms-dsel-all-unv').html('Select all Unverified users');
			$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
			$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

			$('.ms-filter-hdr-txt').html('Select Filter');
		} else if ($(this).hasClass('ms-dsel-all-ver')) {
			selectFilter('ms-dsel-all', this);
			$('.ms-dsel-all-ver').html('Select all Verified users');
			$('.ms-dsel-all-ver').addClass('ms-sel-verified');
			$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

			$('.ms-filter-hdr-txt').html('Select Filter');
		}

	});
});

$(document).on('click', '.ms-batch-size-dropdown', function (e) {
	e.stopPropagation();

	$(this).find('.ms-batch-size-options').slideToggle('fast');
	$(this).find('.ms-bs-chevron-down').toggle();
	$(this).find('.ms-bs-chevron-up').toggle();

	$(document).off('click', '.ms-batchSize');
	$(document).on('click', '.ms-batchSize', function (e) {
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();

		$(this).closest('.ms-batch-size-options').slideToggle('fast');
		$(this).closest('.ms-batch-size-dropdown').find('.ms-bs-chevron-down').toggle();
		$(this).closest('.ms-batch-size-dropdown').find('.ms-bs-chevron-up').toggle();

		$('.ms-bs-value').html($(this).html());
		$('#um-batch-size').val($(this).html());

		var batchSize = parseInt($('#um-batch-size').val());
		if(batchSize == undefined || batchSize == null){
			batchSize = 10;
		}
		var searchKey = $('#ms-search-users-key').val();

		var startIndex = 0;
		$('#ms-user-data').attr('data-startIndex', startIndex);

		if (searchKey == undefined || searchKey == "") {
			fetchUsersForUserManagementForAdmin(startIndex);
		} else {
			fetchUsersForUserManagementBySearchKey(searchKey);
		}
	});
});

$(document).on('click', '.ms-checkbox', function (e) {
	e.stopPropagation();
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	$('#ms-user-data').attr('data-selected', 'default');
	
	var userCount = parseInt($('#ms-user-data').attr('data-userCount'));

	$('.ms-dsel-all').html('Select all ' + userCount + ' users');
	$('.ms-dsel-all').addClass('ms-sel-all');
	$('.ms-dsel-all').removeClass('ms-dsel-all');

	$('.ms-dsel-all-unv').html('Select all Unverified users');
	$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
	$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

	$('.ms-dsel-all-ver').html('Select all Verified users');
	$('.ms-dsel-all-ver').addClass('ms-sel-verified');
	$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

	$('.ms-filter-hdr-txt').html('Select Filter');

	if($('#ms-user-data').attr('data-userStatus') == 'unverified'){
		$('.ms-sel-unverified').html('Deselect and reset users');
		$('.ms-sel-unverified').addClass('ms-dsel-all-unv');
		$('.ms-sel-unverified').removeClass('ms-sel-unverified');
		$('.ms-filter-hdr-txt').html('Selected Unverified Users');
	}else if($('#ms-user-data').attr('data-userStatus') == 'verified'){
		$('.ms-sel-verified').html('Deselect and reset users');
		$('.ms-sel-verified').addClass('ms-dsel-all-ver');
		$('.ms-sel-verified').removeClass('ms-sel-verified');
		$('.ms-filter-hdr-txt').html('Selected Verified Users');
	}
	
	if ($(this).attr('id') == 'ms-checkbox-main') {
		if ($(this).hasClass('ms-checkbox-unchecked')) {
			$('.ms-checkbox-unchecked').each(function (i) {
				if ($(this).attr('id') != 'ms-checkbox-main') {
					$(this).removeClass('ms-checkbox-unchecked');
					$(this).addClass('ms-checkbox-checked');

					var userId = parseInt($(this).attr('data-userId'));
					var status = parseInt($(this).attr('data-status'));
					var emailId = $(this).attr('data-emailId');
					var user = {
						userId: userId,
						status: status,
						emailId: emailId
					};
					if (inSelecteedUsers(userId, selectedUsers) == -1) {
						selectedUsers.push(user);
					}
				}
			});

			$(this).removeClass('ms-checkbox-unchecked');
			$(this).addClass('ms-checkbox-checked');

			$('#ms-user-data').data('selectedUsers', selectedUsers);
		} else if ($(this).hasClass('ms-checkbox-partial') || $(this).hasClass('ms-checkbox-checked')) {
			$('.ms-checkbox-checked').each(function (i) {
				if ($(this).attr('id') != 'ms-checkbox-main') {
					$(this).removeClass('ms-checkbox-checked');
					$(this).addClass('ms-checkbox-unchecked');
					var userId = parseInt($(this).attr('data-userId'));
					var index = inSelecteedUsers(userId, selectedUsers);
					if (index != -1) {
						selectedUsers.splice(index, 1);
					}
				}
			});

			$(this).removeClass('ms-checkbox-partial');
			$(this).removeClass('ms-checkbox-checked');
			$(this).addClass('ms-checkbox-unchecked');

			$('#ms-user-data').data('selectedUsers', selectedUsers);
		}
	} else {
		if ($(this).hasClass('ms-checkbox-unchecked')) {
			$(this).removeClass('ms-checkbox-unchecked');
			$(this).addClass('ms-checkbox-checked');

			var userId = parseInt($(this).attr('data-userId'));
			var status = parseInt($(this).attr('data-status'));
			var emailId = $(this).attr('data-emailId');

			var user = {
				userId: userId,
				status: status,
				emailId: emailId
			};
			if (inSelecteedUsers(userId, selectedUsers) == -1) {
				selectedUsers.push(user);
			}

			markMainCheckbox();

			$('#ms-user-data').data('selectedUsers', selectedUsers);
		} else if ($(this).hasClass('ms-checkbox-checked')) {
			$(this).removeClass('ms-checkbox-checked');
			$(this).addClass('ms-checkbox-unchecked');

			var userId = parseInt($(this).attr('data-userId'));
			var index = inSelecteedUsers(userId, selectedUsers);
			if (index != -1) {
				selectedUsers.splice(index, 1);
			}

			markMainCheckbox();

			$('#ms-user-data').data('selectedUsers', selectedUsers);
		}
	}

	paintSelectedUserCount();
});

function inSelecteedUsers(selectedUserId, userlist) {
	for (var i = 0; i < userlist.length; i++) {
		var userId = userlist[i].userId;
		if (userId == selectedUserId) {
			return i;
		}
	}
	return -1;
}

function selectMultiSelectAction(id) {
	switch (id) {
		case 'ms-reinvite': multiSelectReinvite();
			break;

		case 'ms-delete': multiSelectDelete();
			break;

		case 'ms-smadmin': multiSelectSMAdmin()
			break;
		case 'ms-assign-reg': multiSelectAssignRegion();
			break;
		case 'ms-assign-bra': multiSelectAssignBranch();
			break;
		case 'ms-auto-post-score': mSBindAutoPostEvents();
			break;
		case 'ms-prof-image': multiSelectUploadProfImg();
			break;
		case 'ms-logo-image': multiSelectUploadLogoImg();
			break;
	}
}

function multiSelectReinvite() {
	mSReinviteUsers();
}

function multiSelectDelete() {
	mSDeleteUsers();
}

function multiSelectSMAdmin() {
	mSAssignSocialMonitorAdmin();
}

function selectFilter(id, element) {
	var searchKey = $('#ms-search-users-key').val();
	var isSearchBykey = false;
	if (searchKey != '' && searchKey != null && searchKey != undefined) {
		isSearchBykey = true;
	}
	switch (id) {
		case 'ms-sel-all': $('#ms-user-data').attr('data-selected', 'all');
			$('#ms-user-data').attr('data-userStatus', 'all');
			var startIndex = parseInt($('#ms-user-data').attr('data-startIndex'));

			if (isSearchBykey) {
				fetchUsersForUserManagementBySearchKey(searchKey);
			} else {
				fetchUsersForUserManagementForAdmin(startIndex);
			}
			getAllUsers();
			mSSelectAllUsers();
			break;

		case 'ms-sel-unverified': $('#ms-user-data').attr('data-selected', 'unverified');
			$('#ms-user-data').attr('data-userStatus', 'unverified');
			$('#ms-user-data').attr('data-startIndex', 0);

			if (isSearchBykey) {
				fetchUsersForUserManagementBySearchKey(searchKey);
			} else {
				fetchUsersForUserManagementForAdmin(0);
			}

			getAllUnverifiedUsers();
			mSSelectAllUsers();
			break;

		case 'ms-sel-verified': $('#ms-user-data').attr('data-selected', 'verified');
			$('#ms-user-data').attr('data-userStatus', 'verified');
			$('#ms-user-data').attr('data-startIndex', 0);

			if (isSearchBykey) {
				fetchUsersForUserManagementBySearchKey(searchKey);
			} else {
				fetchUsersForUserManagementForAdmin(0);
			}
			getAllVerifiedUsers();
			mSSelectAllUsers();

			break;
		case 'ms-dsel-all': $('#ms-user-data').attr('data-selected', 'none');
			$('#ms-user-data').attr('data-userStatus', 'none');
			var startIndex = 0;
			if ($(element).hasClass('ms-sel-all-option')) {
				startIndex = parseInt($('#ms-user-data').attr('data-startIndex'));
			} else {
				startIndex = 0;
				$('#ms-user-data').attr('data-startIndex', 0);
			}

			if (isSearchBykey) {
				fetchUsersForUserManagementBySearchKey(searchKey);
			} else {
				fetchUsersForUserManagementForAdmin(startIndex);
			}
			mSDeselectUsers();
			break;
	}
}

function mSSelectAllUsers() {
	$('.ms-checkbox-unchecked').each(function (i) {
		if ($(this).attr('id') != 'ms-checkbox-main') {
			$(this).removeClass('ms-checkbox-unchecked');
			$(this).addClass('ms-checkbox-checked');
		}
	});

	markMainCheckbox();
	paintSelectedUserCount();
}

function mSDeselectUsers() {
	$('.ms-checkbox-checked').each(function (i) {
		if ($(this).attr('id') != 'ms-checkbox-main') {
			$(this).removeClass('ms-checkbox-checked');
			$(this).addClass('ms-checkbox-unchecked');
		}
	});

	markMainCheckbox();

	$('#ms-user-data').data('selectedUsers', []);
	$('#ms-user-data').attr('data-selected', 'none');
	$('#ms-user-data').attr('data-userStatus', 'none');
	
	paintSelectedUserCount();
}

function initializeMultiSelect() {
	var dataSelected = $('#ms-user-data').attr('data-selected');
	var batchSize = parseInt($('#um-batch-size').val());
	if(batchSize == undefined || batchSize == null){
		batchSize = 10;
	}
	
	var selectedUsers = $('#ms-user-data').data('selectedUsers');


	if (dataSelected == 'default') {
		initializeUserSelection(selectedUsers);
	} else if (dataSelected == 'none') {
		mSDeselectUsers();
	} else {
		mSSelectAllUsers();
	}
}

function initializeUserSelection(selectedUsers) {

	$('.ms-checkbox-unchecked').each(function (i) {
		var userId = parseInt($(this).attr('data-userId'));

		if (inSelecteedUsers(userId, selectedUsers) != -1) {
			$(this).removeClass('ms-checkbox-unchecked');
			$(this).addClass('ms-checkbox-checked');
		}
	}).promise().done(function () {
		markMainCheckbox();
		paintSelectedUserCount();
	});
}

function markMainCheckbox() {
	switch (typeOfSelection()) {
		case 0: $('#ms-checkbox-main').removeClass('ms-checkbox-partial');
			$('#ms-checkbox-main').removeClass('ms-checkbox-checked');
			$('#ms-checkbox-main').addClass('ms-checkbox-unchecked');
			break;
		case 1: $('#ms-checkbox-main').addClass('ms-checkbox-partial');
			$('#ms-checkbox-main').removeClass('ms-checkbox-checked');
			$('#ms-checkbox-main').removeClass('ms-checkbox-unchecked');
			break;
		case 2: $('#ms-checkbox-main').removeClass('ms-checkbox-partial');
			$('#ms-checkbox-main').addClass('ms-checkbox-checked');
			$('#ms-checkbox-main').removeClass('ms-checkbox-unchecked');
			break;
	}
}

//typeOfSelection: 0-unchecked 1-partial 2-checked
function typeOfSelection() {
	var typeOfSelection = 0;

	var batchSize = parseInt($('#um-batch-size').val());
	if(batchSize == undefined || batchSize == null){
		batchSize = 10;
	}
	
	var checkedCount = $('.ms-checkbox-checked').length;
	if ($('#ms-checkbox-main').hasClass('ms-checkbox-checked')) {
		checkedCount--;
	}

	var unCheckedCount = $('.ms-checkbox-unchecked').length;
	if ($('#ms-checkbox-main').hasClass('ms-checkbox-unchecked')) {
		unCheckedCount--;
	}

	if (checkedCount == batchSize || unCheckedCount == 0) {
		typeOfSelection = 2;
	} else if (unCheckedCount == batchSize || checkedCount == 0) {
		typeOfSelection = 0;
	} else {
		typeOfSelection = 1;
	}

	return typeOfSelection;
}

function fetchUsersForUserManagementForAdmin(startIndex) {

	if (doStopAjaxRequestForUsersListMultiSelect) {
		return;
	}

	var batchSize = parseInt($('#um-batch-size').val());
	if(batchSize == undefined || batchSize == null){
		batchSize = 10;
	}
	
	var userStatus = $('#ms-user-data').attr('data-userStatus');
	var sortingOrder = getSortingOrder();

	doStopAjaxRequestForUsersListMultiSelect = true;
	
	$('#ms-overlay-loader').show();

	var payload = {
		startIndex: startIndex,
		batchSize: batchSize,
		userStatus: userStatus,
		sortingOrder: sortingOrder
	}

	$.ajax({
		url: "./findusersforcompany.do",
		type: "GET",
		cache: false,
		data: payload,
		dataType: "html",
		success: paintUsersForUserManagement,
		complete: function () {
			doStopAjaxRequestForUsersListMultiSelect = false;
			paginateForMultiSelect();
			paintSelectedUserCount();
			$('#ms-overlay-loader').hide();
			
		},
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function fetchUsersForUserManagementBySearchKey(searchKey) {
	if (doStopAjaxRequestForUsersListMultiSelect) {
		return;
	}

	var batchSize = parseInt($('#um-batch-size').val());
	if(batchSize == undefined || batchSize == null){
		batchSize = 10;
	}
	
	var startIndex = parseInt($('#ms-user-data').attr('data-startIndex'));
	var userStatus = $('#ms-user-data').attr('data-userStatus');
	var sortingOrder = getSortingOrder();

	var payload = {
		startIndex: startIndex,
		batchSize: batchSize,
		searchKey: searchKey,
		userStatus: userStatus,
		sortingOrder: sortingOrder
	}
	doStopAjaxRequestForUsersListMultiSelect = true;

	$('#ms-overlay-loader').show();
	
	$.ajax({
		url: "./findusersunderadmin.do",
		type: "GET",
		cache: false,
		data: payload,
		dataType: "html",
		success: paintUsersForUserManagement,
		complete: function () {
			doStopAjaxRequestForUsersListMultiSelect = false;
			paginateForMultiSelect();
			paintSelectedUserCount();
			$('#ms-overlay-loader').hide();
		},
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function paintUsersForUserManagement(data) {
	$('#user-list').html(data);

	if ($('#ms-search-users-key').val() == '') {
		$('#ms-clear-input-icn').hide();
	}

	var numFound = $('#u-tbl-header').attr("data-num-found");
	$('#users-count').val(numFound);
	$('#ms-user-data').attr('data-userCount', numFound);
	$('#ms-user-count').val(numFound);
	$('.ms-page-user-total-count').html(numFound);

	bindEditUserClick();
	bindUMEvents();
	
	manageBatchSizeDropdown(numFound);
}

$(document).on('click', '.ms-pagi-icn[data-active="true"]', function (e) {
	e.stopPropagation();
	var startIndex = parseInt($('#ms-user-data').attr('data-startIndex'));
	var nextIndex = parseInt($('#ms-user-data').attr('data-nextIndex'));
	var prevIndex = parseInt($('#ms-user-data').attr('data-prevIndex'));
	var lastIndex = parseInt($('#ms-user-data').attr('data-lastIndex'));
	var firstIndex = 0;

	var batchSize = parseInt($('#um-batch-size').val());
	if(batchSize == undefined || batchSize == null){
		batchSize = 10;
	}
	
	var searchKey = $('#ms-search-users-key').val();
	var isSearchBykey = false;
	if (searchKey != '' && searchKey != null && searchKey != undefined) {
		isSearchBykey = true;
	}

	if ($(this).hasClass('ms-first')) {
		startIndex = firstIndex;
	} else if ($(this).hasClass('ms-prev')) {
		startIndex = prevIndex;
	} else if ($(this).hasClass('ms-next')) {
		startIndex = nextIndex;
	} else if ($(this).hasClass('ms-last')) {
		startIndex = lastIndex;
	}

	$('#ms-user-data').attr('data-startIndex', startIndex);

	if (isSearchBykey) {
		fetchUsersForUserManagementBySearchKey(searchKey);
	} else {
		fetchUsersForUserManagementForAdmin(startIndex);
	}

});


$(document).on('click', '#ms-search-icn', function (e) {
	e.stopPropagation();
	var searchKey = $('#ms-search-users-key').val();
	var isSearchBykey = false;
	if (searchKey != '' && searchKey != null && searchKey != undefined) {
		isSearchBykey = true;
	}

	var startIndex = 0;
	$('#ms-user-data').attr('data-startIndex', startIndex);

	var userCount = parseInt($('#ms-user-data').attr('data-userCount'));
	$('#ms-user-data').attr('data-selected', 'none');
	$('#ms-user-data').attr('data-userStatus', 'all');
	
	$('.ms-dsel-all').html('Select all ' + userCount + ' users');
	$('.ms-dsel-all').addClass('ms-sel-all');
	$('.ms-dsel-all').removeClass('ms-dsel-all');

	$('.ms-dsel-all-unv').html('Select all Unverified users');
	$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
	$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

	$('.ms-dsel-all-ver').html('Select all Verified users');
	$('.ms-dsel-all-ver').addClass('ms-sel-verified');
	$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

	$('.ms-filter-hdr-txt').html('Select Filter');
	mSDeselectUsers();
	
	if (isSearchBykey) {
		fetchUsersForUserManagementBySearchKey(searchKey);
	} else {
		fetchUsersForUserManagementForAdmin(startIndex);
	}
});

function getAllUsers() {
	var url = './users/active.do'
	var sortingOrder = getSortingOrder();

	var searchKey = $('#ms-search-users-key').val();
	if (searchKey == undefined) {
		searchKey = '';
	}

	var payload = {
		"sortingOrder": sortingOrder,
		"searchKey": searchKey
	};


	$.ajax({
		url: url,
		type: "GET",
		data: payload,
		async: true,
		success: storeSelectedUsers,
		complete: paintSelectedUserCount,
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			if (e.status == 0) {
				return;
			}
		}
	});
}

function getAllUnverifiedUsers() {
	var url = './users/unverified.do'

	var sortingOrder = getSortingOrder();

	var searchKey = $('#ms-search-users-key').val();
	if (searchKey == undefined) {
		searchKey = '';
	}

	var payload = {
		"sortingOrder": sortingOrder,
		"searchKey": searchKey
	};
	$.ajax({
		url: url,
		type: "GET",
		data: payload,
		async: true,
		success: storeSelectedUsers,
		complete: paintSelectedUserCount,
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			if (e.status == 0) {
				return;
			}
		}
	});
}

function getAllVerifiedUsers() {
	var url = './users/verified.do'

	var sortingOrder = getSortingOrder();

	var searchKey = $('#ms-search-users-key').val();
	if (searchKey == undefined) {
		searchKey = '';
	}

	var payload = {
		"sortingOrder": sortingOrder,
		"searchKey": searchKey
	};

	$.ajax({
		url: url,
		type: "GET",
		data: payload,
		async: true,
		success: storeSelectedUsers,
		complete: paintSelectedUserCount,
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			if (e.status == 0) {
				return;
			}
		}
	});
}

function storeSelectedUsers(data) {
	if (data != null && data != undefined) {
		var userList = JSON.parse(data);
		$('#ms-user-data').data('selectedUsers', userList);
	} else {
		$('#ms-user-data').data('selectedUsers', []);
	}
}

function mSReinviteUsers() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-overlay-loader').show();

	var url = './users/reinvite.do';

	var emailAddressList = [];
	for (var i = 0; i < selectedUsers.length; i++) {
		emailAddressList.push(selectedUsers[i].emailId);
	}
	$('#ms-confirm-popup-txt-hdr').html('Bulk Re-Invite');
	var action = 'reinvite';

	$.ajax({
		url: url,
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify(emailAddressList),
		async: false,
		success: function (data) { showMSSuccessPopup(data, action) },
		complete: actionCompleteCallback,
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function mSDeleteUsers() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-overlay-loader').show();

	var url = './users/delete.do';

	var userIdList = [];
	for (var i = 0; i < selectedUsers.length; i++) {
		userIdList.push(selectedUsers[i].userId);
	}

	var action = 'delete';

	$('#ms-confirm-popup-txt-hdr').html('Bulk Delete');
	$.ajax({
		url: url,
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify(userIdList),
		async: false,
		success: function (data) { showMSSuccessPopup(data, action) },
		complete: actionCompleteCallbackAndRefresh,
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function mSAssignSocialMonitorAdmin() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-overlay-loader').show();

	var url = './users/assignassocialmonitoradmin.do';

	var userIdList = [];
	for (var i = 0; i < selectedUsers.length; i++) {
		userIdList.push(selectedUsers[i].userId);
	}

	$('#ms-confirm-popup-txt-hdr').html('Bulk Add Social Monitor Admin');
	var action = 'smadmin';

	$.ajax({
		url: url,
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify(userIdList),
		async: false,
		success: function (data) { showMSSuccessPopup(data, action) },
		complete: actionCompleteCallback,
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
		}
	});
}

function showMSSuccessPopup(data, action) {
	var confirmation = data;

	$('#ms-confirm-popup-continue').off();
	$('#ms-confirm-popup-continue').click(function () {

		$('#ms-confirm-popup').hide();
		$('#ms-confirm-popup-continue').unbind('click');

		$('#ms-confirm-popup-txt-hdr').html('');
		$('#ms-confirm-failed').html('');
		$('#ms-confirm-success').html('');
		enableBodyScroll();

	});

	var selectedUsers = $('#ms-user-data').data('selectedUsers');
	
	var failedCount = -1;
	if(confirmation.failedItems != null && confirmation.failedItems != undefined){
		failedCount =  confirmation.failedItems.length;
	}
	
	var successCount = -1;
	if(confirmation.successItems != null && confirmation.successItems != undefined){
		successCount =  confirmation.successItems.length;
		
		if (confirmation.successItems.length == 1 && confirmation.successItems[0].userId == 0) {

			if (selectedUsers.length > 0) {
				successCount = selectedUsers.length;
			}
		}
	}
	
	if(successCount == -1 && failedCount == -1){
		$('#ms-confirm-popup-txt-hdr').html('Unable to perform action!!');
		$('#ms-confirm-success').html('');
		$('#ms-confirm-failed').html(confirmation);
	}else{
		if (action == 'reinvite') {
			if (failedCount != 0)
				$('#ms-confirm-failed').html(failedCount + ' Re-Invites failed.');

			if (successCount != 0)
				$('#ms-confirm-success').html(successCount + ' Re-Invites sent Successfully.');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to reinvite ' + failedCount + ' selected users.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully Re-invited ' + successCount + ' selected users.');
			}
		} else if (action == 'delete') {
			if (failedCount != 0)
				$('#ms-confirm-failed').html('Failed to delete ' + failedCount + ' users.');

			if (successCount != 0)
				$('#ms-confirm-success').html(successCount + ' users Deleted successfully.');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to delete ' + failedCount + ' selected users.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully deleted ' + successCount + ' selected users.');
			}
		} else if (action == 'smadmin') {
			if (failedCount != 0)
				$('#ms-confirm-failed').html('Failed to add ' + failedCount + ' users as Social Monitor Admins.');

			if (successCount != 0)
				$('#ms-confirm-success').html(successCount + ' used added as Social Monitor Admins successfully.');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to add ' + failedCount + ' selected users as Social Monitor admin.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully added ' + successCount + ' selected users as Social monitor admins.');
			}
		} else if (action == 'assignToRegion') {
			var region = $('#ms-sel-region-name').val();

			if (failedCount != 0)
				$('#ms-confirm-failed').html('Failed to assign' + failedCount + ' users to ' + region + ' region.');

			if (successCount != 0)
				$('#ms-confirm-success').html('Successfully assigned ' + successCount + ' users to ' + region + ' region.');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to assign ' + failedCount + ' selected users to ' + region + ' region.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully assigned ' + successCount + ' selected users to ' + region + ' region.');
			}
		} else if (action == 'assignToBranch') {
			var branch = $('#ms-sel-branch-name').val();

			if (failedCount != 0)
				$('#ms-confirm-failed').html('Failed to assign' + failedCount + ' users to ' + branch + ' branch.');

			if (successCount != 0)
				$('#ms-confirm-success').html('Successfully assigned ' + successCount + ' users to ' + branch + ' branch.');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to assign ' + failedCount + ' selected users to ' + branch + ' branch.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully assigned ' + successCount + ' selected users to ' + branch + ' branch.');
			}
		} else if (action == 'autopostscore') {
			var branch = $('#ms-sel-branch-name').val();
			if (failedCount != 0)
				$('#ms-confirm-failed').html('Failed to update auto post score for ' + failedCount + ' users');

			if (successCount != 0)
				$('#ms-confirm-success').html('Successfully updated auto post score for ' + successCount + ' users');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to update auto post score for ' + failedCount + ' selected users.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully updated auto post score for ' + successCount + ' selected users.');
			}
		} else if (action == 'profImgUpload') {
			var branch = $('#ms-sel-branch-name').val();
			if (failedCount != 0)
				$('#ms-confirm-failed').html('Failed to update Profile Image for ' + failedCount + ' users');

			if (successCount != 0)
				$('#ms-confirm-success').html('Successfully updated Profile Image for ' + successCount + ' users');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to update Profile Image for ' + failedCount + ' selected users.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully updated Profile Image the ' + successCount + ' selected users.');
			}
		}
		else if (action == 'logoImgUpload') {
			var branch = $('#ms-sel-branch-name').val();
			if (failedCount != 0)
				$('#ms-confirm-failed').html('Failed to update Logo for ' + failedCount + ' users');

			if (successCount != 0)
				$('#ms-confirm-success').html('Successfully updated Logo for ' + successCount + ' users');

			if (selectedUsers.length > 0 && failedCount == selectedUsers.length) {
				$('#ms-confirm-success').html('');
				$('#ms-confirm-failed').html('Failed to update Logo for ' + failedCount + ' selected users.');
			} else if (selectedUsers.length > 0 && successCount == selectedUsers.length) {
				$('#ms-confirm-failed').html('');
				$('#ms-confirm-success').html('Successfully updated Logo for ' + successCount + ' selected users.');
			}
		}
	}
	

	$('#ms-confirm-popup').show();
	disableBodyScroll();
}


function actionCompleteCallback() {
	var userCount = parseInt($('#ms-user-data').attr('data-userCount'));

	$('.ms-dsel-all').html('Select all ' + userCount + ' users');
	$('.ms-dsel-all').addClass('ms-sel-all');
	$('.ms-dsel-all').removeClass('ms-dsel-all');

	$('.ms-dsel-all-unv').html('Select all Unverified users');
	$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
	$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

	$('.ms-dsel-all-ver').html('Select all Verified users');
	$('.ms-dsel-all-ver').addClass('ms-sel-verified');
	$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

	$('.ms-filter-hdr-txt').html('Select Filter');
	mSDeselectUsers();
	
	if($('#ms-user-data').attr('data-userStatus') == 'unverified'){
		$('.ms-sel-unverified').html('Deselect and reset users');
		$('.ms-sel-unverified').addClass('ms-dsel-all-unv');
		$('.ms-sel-unverified').removeClass('ms-sel-unverified');
		$('.ms-filter-hdr-txt').html('Selected Unverified Users');
	}else if($('#ms-user-data').attr('data-userStatus') == 'verified'){
		$('.ms-sel-verified').html('Deselect and reset users');
		$('.ms-sel-verified').addClass('ms-dsel-all-ver');
		$('.ms-sel-verified').removeClass('ms-sel-verified');
		$('.ms-filter-hdr-txt').html('Selected Verified Users');
	}

	$('#ms-overlay-loader').hide();
}

function actionCompleteCallbackAndRefresh() {
	var userCount = parseInt($('#ms-user-data').attr('data-userCount'));

	$('.ms-dsel-all').html('Select all ' + userCount + ' users');
	$('.ms-dsel-all').addClass('ms-sel-all');
	$('.ms-dsel-all').removeClass('ms-dsel-all');

	$('.ms-dsel-all-unv').html('Select all Unverified users');
	$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
	$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

	$('.ms-dsel-all-ver').html('Select all Verified users');
	$('.ms-dsel-all-ver').addClass('ms-sel-verified');
	$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

	$('.ms-filter-hdr-txt').html('Select Filter');
	mSDeselectUsers();
	
	if($('#ms-user-data').attr('data-userStatus') == 'unverified'){
		$('.ms-sel-unverified').html('Deselect and reset users');
		$('.ms-sel-unverified').addClass('ms-dsel-all-unv');
		$('.ms-sel-unverified').removeClass('ms-sel-unverified');
		$('.ms-filter-hdr-txt').html('Selected Unverified Users');
	}else if($('#ms-user-data').attr('data-userStatus') == 'verified'){
		$('.ms-sel-verified').html('Deselect and reset users');
		$('.ms-sel-verified').addClass('ms-dsel-all-ver');
		$('.ms-sel-verified').removeClass('ms-sel-verified');
		$('.ms-filter-hdr-txt').html('Selected Verified Users');
	}

	$('#ms-overlay-loader').hide();

	var searchKey = $('#ms-search-users-key').val();
	var isSearchBykey = false;
	if (searchKey != '' && searchKey != null && searchKey != undefined) {
		isSearchBykey = true;
	}

	var startIndex = parseInt($('#ms-user-data').attr('data-startIndex'));
	if (isSearchBykey) {
		fetchUsersForUserManagementBySearchKey(searchKey);
	} else {
		fetchUsersForUserManagementForAdmin(startIndex);
	}
}

$(document).on('blur', '.ms-page-no', function (e) {
	e.stopPropagation();

	var pageNoStr = $(this).val();
	if (/^[0-9]+$/.test(pageNoStr)) {
		var pageNo = parseInt(pageNoStr);
		var pageCount = parseInt($('#ms-user-data').attr('data-pageCount'));
		var batchSize = parseInt($('#um-batch-size').val());
		if(batchSize == undefined || batchSize == null){
			batchSize = 10;
		}
		
		if (pageNo < 1 || pageNo > pageCount) {
			$('#overlay-toast').html('Invalid Page Number!!');
			showToast();
			$(this).val(parseInt($('#ms-user-data').attr('data-pageNo')));
			return;
		}

		$('.ms-page-no').val($(this).val());
		var startIndex = (pageNo - 1) * batchSize;
		if (startIndex < 0 && startIndex >= batchSize) {
			startIndex = 0;
		}

		var searchKey = $('#ms-search-users-key').val();
		var isSearchBykey = false;
		if (searchKey != '' && searchKey != null && searchKey != undefined) {
			isSearchBykey = true;
		}

		$('#ms-user-data').attr('data-startIndex', startIndex);

		if (isSearchBykey) {
			fetchUsersForUserManagementBySearchKey(searchKey);
		} else {
			fetchUsersForUserManagementForAdmin(startIndex);
		}
	} else {
		$('#overlay-toast').html('Invalid Characters in Page Number!!');
		showToast();
		$(this).val(parseInt($('#ms-user-data').attr('data-pageNo')));
	}
});

function paintSelectedUserCount() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');
	var batchSize = parseInt($('#um-batch-size').val());
	if(batchSize == undefined || batchSize == null){
		batchSize = 10;
	}
	
	var totalUserCount = parseInt($('#users-count').val());
	var totalPageCount = Math.ceil(totalUserCount / batchSize);

	if (selectedUsers != undefined && selectedUsers != null) {
		var selectedUserCount = selectedUsers.length;

		var selectedUsersInPage = $('.ms-checkbox-checked').length;
		if ($('#ms-checkbox-main').hasClass('ms-checkbox-checked')) {
			selectedUsersInPage--;
		}

		if (selectedUsersInPage >= 0 && selectedUserCount > 0) {
			$('.ms-sel-usr-text').html('<div class="ms-user-count">' + selectedUserCount + '</div> of ' + totalUserCount + ' users selected.');
		}

		if ((totalPageCount == 1 && selectedUsersInPage == totalUserCount) || (selectedUsersInPage == batchSize && selectedUserCount == selectedUsersInPage)) {
			$('.ms-sel-usr-text').html('All <div class="ms-user-count">' + selectedUsersInPage + '</div> users on this page are selected.');
		} else if (selectedUserCount == selectedUsersInPage) {
			$('.ms-sel-usr-text').html('<div class="ms-user-count">' + selectedUsersInPage + '</div> users on this page are selected.');
		}

		if (selectedUserCount == 0) {
			$('.ms-sel-usr-text').html('No users selected');
		} else if (selectedUserCount > batchSize) {
			$('.ms-sel-usr-text').html('<div class="ms-user-count">' + selectedUserCount + '</div> of ' + totalUserCount + ' users selected.');
		} else if (selectedUserCount == totalUserCount && selectedUserCount != selectedUsersInPage) {
			$('.ms-sel-usr-text').html('All <div class="ms-user-count">' + totalUserCount + '</div> users are selected.')
		}
	}
}

function paginateForMultiSelect() {
	var startIndex = parseInt($('#ms-user-data').attr('data-startIndex'));
	var batchSize = parseInt($('#um-batch-size').val());
	if(batchSize == undefined || batchSize == null){
		batchSize = 10;
	}
	
	var totalUserCount = parseInt($('#users-count').val());

	var totalPageCount = Math.ceil(totalUserCount / batchSize);
	$('#ms-user-data').attr('data-pageCount', totalPageCount);
	$('.ms-total-pages').html(totalPageCount);

	var pageNo = (startIndex / batchSize) + 1;
	$('.ms-page-no').val(pageNo);
	$('#ms-user-data').attr('data-PageNo', pageNo);

	var nextIndex = startIndex + batchSize;
	var lastIndex = totalPageCount * batchSize;
	if (nextIndex <= lastIndex) {
		$('#ms-user-data').attr('data-nextIndex', nextIndex);
	}

	var prevIndex = startIndex - batchSize;
	if (prevIndex >= 0) {
		$('#ms-user-data').attr('data-prevIndex', prevIndex);
	}

	if (pageNo == 1) {
		$('.ms-first').attr('data-active', false);
		$('.ms-prev').attr('data-active', false);
	} else if (pageNo == 2) {
		$('.ms-first').attr('data-active', false);
		$('.ms-prev').attr('data-active', true);
	} else if (pageNo > 2) {
		$('.ms-first').attr('data-active', true);
		$('.ms-prev').attr('data-active', true);
	}

	if (pageNo == totalPageCount) {
		$('.ms-next').attr('data-active', false);
		$('.ms-last').attr('data-active', false);
	} else if (pageNo + 1 == totalPageCount) {
		$('.ms-next').attr('data-active', true);
		$('.ms-last').attr('data-active', false);
	} else if (pageNo + 1 < totalPageCount) {
		$('.ms-next').attr('data-active', true);
		$('.ms-last').attr('data-active', true);
	}

	if ($('#ms-search-users-key').val() != '') {
		if ($('#ms-user-data').attr('data-selected') == 'all' || $('#ms-user-data').attr('data-selected') == 'none') {
			$('#ms-user-data').attr('data-nameUsersCount', totalUserCount);
		}
		var totalActiveUsersCount = parseInt($('#ms-user-data').attr('data-nameUsersCount'));
		$('.ms-sel-all').html('Select all ' + totalActiveUsersCount + ' active users');
	} else {
		if ($('#ms-user-data').attr('data-selected') == 'all' || $('#ms-user-data').attr('data-selected') == 'none') {
			$('#ms-user-data').attr('data-activeUsersCount', totalUserCount);
		}
		var totalActiveUsersCount = parseInt($('#ms-user-data').attr('data-activeUsersCount'));
		$('.ms-sel-all').html('Select all ' + totalActiveUsersCount + ' active users');
	}


	var startCount = startIndex + 1;
	var endCount = startIndex + batchSize;
	if (endCount > totalUserCount) {
		endCount = totalUserCount;
	}
	$('.ms-page-user-start-count').html(startCount);
	$('.ms-page-user-end-count').html(endCount);
}

$(document).on('click', '.ms-sort[data-active="false"]', function (e) {
	$('.ms-sort').attr('data-active', false);

	if ($(this).hasClass('ms-sort-asc'))
		$('.ms-sort-asc').attr('data-active', true);
	else
		$('.ms-sort-desc').attr('data-active', true);

	$('#ms-user-data').attr('data-startIndex', 0);

	var searchKey = $('#ms-search-users-key').val();
	var isSearchBykey = false;
	if (searchKey != '' && searchKey != null && searchKey != undefined) {
		isSearchBykey = true;
	}

	var startIndex = 0;
	if (isSearchBykey) {
		fetchUsersForUserManagementBySearchKey(searchKey);
	} else {
		fetchUsersForUserManagementForAdmin(startIndex);
	}
});

function getSortingOrder() {
	if ($('.ms-sort[data-active="true"]').hasClass('ms-sort-asc')) {
		return 'ASC';
	} else {
		return 'DESC';
	}
}

function mSBindRegionSelectorEvents() {
	callAjaxGET("/fetchregions.do", function (data) {
		var regionList = [];
		if (data != undefined && data != "")
			regionList = $.parseJSON(data);
		var searchData = [];
		for (var i = 0, j = 0; i < regionList.length; i++) {
			if (regionList[i].isDefaultBySystem == 0) {
				searchData[j] = {};
				searchData[j].label = regionList[i].regionName;
				searchData[j].regionId = regionList[i].regionId;
				j++;
			}
		}
		$("#ms-assign-popup-inp-reg").autocomplete({
			source: searchData,
			minLength: 0,
			delay: 0,
			autoFocus: true,
			select: function (event, ui) {
				$("#ms-assign-popup-inp-reg").val(ui.item.label);
				$('#ms-sel-region-id').val(ui.item.regionId);
				$('#ms-sel-region-name').val(ui.item.label);

				return false;
			},
			close: function (event, ui) {
			},
			create: function (event, ui) {
				$('.ui-helper-hidden-accessible').remove();
			}
		}).autocomplete("instance")._renderItem = function (ul, item) {
			return $("<li>").append(item.label).appendTo(ul);
		};
		$("#ms-assign-popup-inp-reg").off('focus');
		$("#ms-assign-popup-inp-reg").focus(function () {
			$(this).autocomplete('search');
		});
	}, true);
}

/**
 * binds the click and keyup of office selector
 */
function mSBindOfficeSelectorEvents() {
	callAjaxGET("/fetchbranches.do", function (data) {
		var branchList = [];
		if (data != undefined && data != "")
			branchList = $.parseJSON(data);
		var searchData = [];
		for (var i = 0, j = 0; i < branchList.length; i++) {
			if (branchList[i].isDefaultBySystem == 0) {
				searchData[j] = {};
				searchData[j].label = branchList[i].branchName;
				searchData[j].branchId = branchList[i].branchId;
				searchData[j].regionId = branchList[i].regionId;
				j++;
			}
		}
		$("#ms-assign-popup-inp-bra").autocomplete({
			source: searchData,
			minLength: 0,
			delay: 0,
			autoFocus: true,
			select: function (event, ui) {
				$("#ms-assign-popup-inp-bra").val(ui.item.label);
				$('#ms-sel-branch-id').val(ui.item.branchId);
				$('#ms-sel-region-id').val(ui.item.regionId);
				$('#ms-sel-branch-name').val(ui.item.label);

				return false;
			},
			close: function (event, ui) {
			},
			create: function (event, ui) {
				$('.ui-helper-hidden-accessible').remove();
			}
		}).autocomplete("instance")._renderItem = function (ul, item) {
			return $("<li>").append(item.label).appendTo(ul);
		};
		$("#ms-assign-popup-inp-bra").off('focus');
		$("#ms-assign-popup-inp-bra").focus(function () {
			$(this).autocomplete('search');
		});
	}, true);
}

function multiSelectAssignRegion() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-assign-cont').show();
	$('#ms-assign-popup').show();
	$('#ms-assign-popup-hdr').html('Bulk Assign to Region');
	$('.ms-assign-popup-label').html('Select Region: ');

	$('#ms-assign-popup-inp-reg').show();
	$('#ms-assign-popup-inp-bra').hide();

	disableBodyScroll();
	
	mSBindRegionSelectorEvents();

	$(document).off('click', '#ms-assign-popup-assign');
	$(document).on('click', '#ms-assign-popup-assign', function (e) {
		e.stopPropagation();

		var selectedUsers = $('#ms-user-data').data('selectedUsers');

		if (selectedUsers.length <= 0) {
			$('#overlay-toast').html('No users selected');
			showToast();
			return;
		}

		$('#ms-overlay-loader').show();

		var url = './users/assigntoregion.do';

		var userIdList = [];
		for (var i = 0; i < selectedUsers.length; i++) {
			userIdList.push(selectedUsers[i].userId);
		}

		var action = 'assignToRegion';

		var regionId = parseInt($('#ms-sel-region-id').val());

		var manageTeamBulkRequest = {
			"userIds": userIdList,
			"regionId": regionId
		}

		$('#ms-confirm-popup-txt-hdr').html('Bulk Assign to Region');
		$.ajax({
			url: url,
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(manageTeamBulkRequest),
			async: false,
			success: function (data) { showMSSuccessPopup(data, action) },
			complete: function () {
				hideMSAssignPopup();
				actionCompleteCallbackAndRefresh();
				enableBodyScroll();
			},
			error: function (e) {
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				hideMSAssignPopup();
			}
		});
	});

	$(document).off('click', '#ms-assign-popup-cancel');
	$(document).on('click', '#ms-assign-popup-cancel', function (e) {
		e.stopPropagation();

		hideMSAssignPopup();
	});
}

function multiSelectAssignBranch() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-assign-cont').show();
	$('#ms-assign-popup').show();
	$('#ms-assign-popup-hdr').html('Bulk Assign to Branch');
	$('.ms-assign-popup-label').html('Select Office: ');

	$('#ms-assign-popup-inp-bra').show();
	$('#ms-assign-popup-inp-reg').hide();

	disableBodyScroll();
	
	mSBindOfficeSelectorEvents();

	$(document).off('click', '#ms-assign-popup-assign');
	$(document).on('click', '#ms-assign-popup-assign', function (e) {
		e.stopPropagation();

		var selectedUsers = $('#ms-user-data').data('selectedUsers');

		if (selectedUsers.length <= 0) {
			$('#overlay-toast').html('No users selected');
			showToast();
			return;
		}

		$('#ms-overlay-loader').show();

		var url = './users/assigntobranch.do';

		var userIdList = [];
		for (var i = 0; i < selectedUsers.length; i++) {
			userIdList.push(selectedUsers[i].userId);
		}

		var action = 'assignToBranch';

		var regionId = parseInt($('#ms-sel-region-id').val());
		var branchId = parseInt($('#ms-sel-branch-id').val());

		var manageTeamBulkRequest = {
			"userIds": userIdList,
			"regionId": regionId,
			"branchId": branchId
		}

		$('#ms-confirm-popup-txt-hdr').html('Bulk Assign to Branch');
		$.ajax({
			url: url,
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(manageTeamBulkRequest),
			async: false,
			success: function (data) { showMSSuccessPopup(data, action) },
			complete: function () {
				hideMSAssignPopup();
				actionCompleteCallbackAndRefresh();
				enableBodyScroll();
			},
			error: function (e) {
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				hideMSAssignPopup();
			}
		});
	});

	$(document).off('click', '#ms-assign-popup-cancel');
	$(document).on('click', '#ms-assign-popup-cancel', function (e) {
		e.stopPropagation();
		hideMSAssignPopup();
	});
}

function hideMSAssignPopup() {

	$('#ms-assign-popup-hdr').html('');
	$('.ms-assign-popup-lable').html('');

	$("#ms-assign-popup-inp-reg").val('');
	$('#ms-assign-popup-inp-reg').hide();
	$("#ms-assign-popup-inp-bra").val('');
	$('#ms-assign-popup-inp-bra').hide();

	$(document).off('click', '#ms-assign-popup-assign');
	$(document).off('click', '#ms-assign-popup-cancel');

	$('#ms-assign-popup').hide();
	$('#ms-assign-cont').hide();
}

function multiAutoPostScore() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-overlay-loader').show();

	var userIdList = [];
	for (var i = 0; i < selectedUsers.length; i++) {
		userIdList.push(selectedUsers[i].userId);
	}

	var autoPostScore = $('#ms-autopost').val();

	var manageTeamBulkRequest = {
		userIds: userIdList,
		minimumSocialPostScore: autoPostScore
	}

	var url = './users/autopostscore.do';

	var action = 'autopostscore';

	$.ajax({
		url: url,
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify(manageTeamBulkRequest),
		async: false,
		success: function (data) { showMSSuccessPopup(data, action) },
		complete: function () {
			hideMSAutoPostPopup();
			actionCompleteCallback();
		},
		error: function (e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			hideMSAutoPostPopup();
		}
	});
}

function mSBindAutoPostEvents() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-autopost-cont').show();
	$('#ms-assign-popup').show();
	$('#ms-assign-popup-hdr').html('Minimum score to post on social networks');

	$('#ms-autopost').val(0.5);
	var rating = 0.5;
	var ratingParent = $('#ms-minrat-star-cont');
	mSChangeRatingPattern(rating, ratingParent);

	$(document).off('click', '#ms-autopost');
	$(document).on('click', '#ms-autopost', function (e) {
		e.stopPropagation();
		e.stopImmediatePropagation();
		e.preventDefault();

		$('.ms-rat-dropdown').slideToggle('fast', 'swing');

		$(document).off('click', '.ms-rat-drop-item');
		$(document).on('click', '.ms-rat-drop-item', function (e) {
			e.stopPropagation();
			e.stopImmediatePropagation();
			e.preventDefault();

			$('#ms-autopost').val($(this).html());
			$('.ms-rat-dropdown').slideToggle('fast', 'swing');

			var rating = $('#ms-autopost').val();
			var ratingParent = $('#ms-minrat-star-cont');
			mSChangeRatingPattern(rating, ratingParent);
		});
	});

	$(document).off('click', '#ms-assign-popup-assign');
	$(document).on('click', '#ms-assign-popup-assign', function (e) {
		e.stopPropagation();

		multiAutoPostScore();
	});

	$(document).off('click', '#ms-assign-popup-cancel');
	$(document).on('click', '#ms-assign-popup-cancel', function (e) {
		e.stopPropagation();
		hideMSAutoPostPopup();
	});
}

function hideMSAutoPostPopup() {

	$('#ms-autopost').val(0.5);
	var rating = 0.5;
	var ratingParent = $('#ms-minrat-star-cont');
	mSChangeRatingPattern(rating, ratingParent);

	$('#ms-autopost-cont').hide();
	$('#ms-assign-popup-hdr').html('');
	$('#ms-assign-popup').hide();

}

function mSChangeRatingPattern(rating, ratingParent) {
	var ratingIntVal = 0;
	var roundedFloatingVal = parseFloat(rating).toFixed(2);
	var ratingFloat = parseFloat(roundedFloatingVal).toFixed(2);
	var ratingInt = parseInt(ratingFloat * 4);
	ratingIntVal = (ratingInt / 4).toFixed(2);

	roundedFloatingVal = parseFloat(roundedFloatingVal).toFixed(1);
	ratingImgHtml = "<div class='rating-image star-rating-" + ratingIntVal + "' title='" + roundedFloatingVal + "/5.0'></div>";
	ratingValHtml = "<div class='ms-minrat-star-text'>" + roundedFloatingVal + "</div>";

	ratingParent.html(ratingImgHtml);
	ratingParent.append(ratingValHtml);

};

function hideMSProfImgPopup() {
	$('#ms-prof-pic-cont').hide();

	$('#ms-prof-image').val('');
	$('#ms-prof-pic-cont').hide();
	$('#ms-assign-popup').hide();
	$('#ms-assign-popup-hdr').html('');
}

function multiSelectUploadProfImg() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-prof-image').trigger('click');
}

function multiSelectUploadLogoImg() {
	var selectedUsers = $('#ms-user-data').data('selectedUsers');

	if (selectedUsers.length <= 0) {
		$('#overlay-toast').html('No users selected');
		showToast();
		return;
	}

	$('#ms-logo-image').trigger('click');

	$(document).off('change', '#ms-logo-image');
	$(document).on('change', '#ms-logo-image', function (e) {
		e.stopPropagation();
		if (!logoValidate(this)) {
			return false;
		}

		var url = './users/uploadlogo.do';
		var action = 'logoImgUpload';

		var formData = new FormData();
		formData.append("file", $(this).prop("files")[0]);
		formData.append("userIds", JSON.stringify(selectedUsers.map(function (x) { return x.userId; })));
		formData.append("logoFileName", $(this).prop("files")[0].name);

		showOverlay();
		$.ajax({
			url: url,
			type: "POST",
			async: true,
			contentType: false,
			processData: false,
			dataType: "json",
			data: formData,
			success: function (d, status, xhr) {
				hideOverlay();
				callAjaxGET("./fetchprofilelogo.do", callBackShowProfileLogo, true);
				showMSSuccessPopup(d, action);
			},
			error: function (e) {
				hideOverlay();
				if (e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				redirectErrorpage();
			}
		});
	});
}

function manageBatchSizeDropdown(s) {
	var size = parseInt(s);

	if (size < 10){
		$('.ms-batch-size-dropdown').hide();
	}else {
		$('.ms-batch-size-dropdown').show();
		$('.ms-batch-size-dropdown').find('.ms-batchSize').each(function (i, v) {
			if ((i != 0 && i != 4) )
				if(parseInt($(v).html()) > size && size <= parseInt($($('.ms-batch-size-dropdown').find('.ms-batchSize')[i-1]).html()))
					$(v).hide();
				else
					$(v).show();
		});
	}
}

$(document).on('keyup', '#ms-search-users-key', function(e) {
	// detect enter
	if (e.keyCode == 13) {
		var searchKey = $('#ms-search-users-key').val();
		var isSearchBykey = false;
		if (searchKey != '' && searchKey != null && searchKey != undefined) {
			isSearchBykey = true;
		}
		
		var userCount = parseInt($('#ms-user-data').attr('data-userCount'));
		$('#ms-user-data').attr('data-selected', 'none');
		$('#ms-user-data').attr('data-userStatus', 'all');
				
		$('.ms-dsel-all').html('Select all ' + userCount + ' users');
		$('.ms-dsel-all').addClass('ms-sel-all');
		$('.ms-dsel-all').removeClass('ms-dsel-all');

		$('.ms-dsel-all-unv').html('Select all Unverified users');
		$('.ms-dsel-all-unv').addClass('ms-sel-unverified');
		$('.ms-dsel-all-unv').removeClass('ms-dsel-all-unv');

		$('.ms-dsel-all-ver').html('Select all Verified users');
		$('.ms-dsel-all-ver').addClass('ms-sel-verified');
		$('.ms-dsel-all-ver').removeClass('ms-dsel-all-ver');

		$('.ms-filter-hdr-txt').html('Select Filter');
		mSDeselectUsers();

		var startIndex = 0;
		$('#ms-user-data').attr('data-startIndex', startIndex);

		if (isSearchBykey) {
			fetchUsersForUserManagementBySearchKey(searchKey);
		} else {
			fetchUsersForUserManagementForAdmin(startIndex);
		}
	}
});