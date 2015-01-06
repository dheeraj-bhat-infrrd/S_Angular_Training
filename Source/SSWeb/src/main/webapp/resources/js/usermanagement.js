var dashboardLink = "dashboard";
var userManagementUserListStartIndex = 0;
var doStopAjaxRequestForUsersList = false;
var listOfBranchesForAdmin;

function initUserManagementPage(){
	paintUserDetailsForm("");
	paintUserListInUserManagement();
}

function selectBranch(element){
	var brach = $(element).html();
	var branchId = element.id.substr("branch-".length);
	$(element).parent().children('.um-dd-wrapper').show();
	$(element).hide();
	$(element).parent().toggle();
	$('#um-assignto').val(brach);
	$('#um-assignto').attr("brachId",branchId);
}


$(document).on('click','#um-assignto-con .icn-save',function(){
	var branchId = $('#um-assignto').attr("brachId");
	var userId = $(this).closest('.row').attr("id");
	assignUserToBranch(userId, branchId);
});

$(document).on('click','#um-emailid-con .icn-save',function(){
	inviteUser();
});

/*$('.um-user-row').click(function(){
	paintUserDetailsForm(this.id);
});

$('#icon-user-delete').click(function(){
	var userId = $(this).closest('.row').attr("id");
	deleteUser(userId);
});*/
$(window).scroll(function (event) {
    //check if scroll position is at the bottom
    if ((window.innerHeight + window.pageYOffset) >= (document.body.offsetHeight )){
    	if(!doStopAjaxRequestForUsersList){
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
			if($('#message-header>div').hasClass("success-message")){
				success = true;
			}
		},
		complete:function(){
			if(success){
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

	var success=false;
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
			if($('#message-header>div').hasClass("success-message")){
				success = true;
			}
		},
		complete: function(){
			hideOverlay();
			if(success){
				console.log("User successfully unassigned from branch " + branchId);
				$('#branch-to-unassign-'+branchId).remove();
				//check if there are any assigned branches left
				if($('#um-assigned-brach-container > div').length <= 0){
					$('#um-assignto').parent().parent().find('.um-item-row-icon').removeClass('icn-tick').addClass('icn-save');
				}
			}else{
				alert("branch delete unsuccessful");
			}
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

function inviteUser(){
	
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
			if($('#common-message-header').hasClass("success-message")){
				success = true;
			}
			if(success){
				$('#um-emailid').parent().parent().find('.um-item-row-icon').removeClass('icn-save');
				$('#um-emailid').parent().parent().find('.um-item-row-icon').addClass('icn-tick');
				var userId = $('#mh-userId').val();
				$('#um-emailid').closest('.row').attr("id",userId);
			}else{
				var userId = $('#mh-existing-userId').val();
				if(userId != undefined || userId != ""){
					paintUserDetailsForm(userId);
					return;
				}
				alert("user invite not successful");
			}
		},
		complete:function(){
			hideOverlay();
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
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
			if($('#message-header>div').hasClass("success-message")){
				success = true;
			}
		},
		complete:function(){
			hideOverlay();
			if(success){
				alert("User deleted");
				// paint blank user details form
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
		error : function(e) {
			console.error("error : " + e);
		}
	});

}

/*
 * Function paint the user list in user management page
 */
function paintUserListInUserManagement(){
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

function paginateUserList(data){
	$('#um-user-list').find('tbody').append(data);
}

/*
 * Function to activate or deactivate user 
 */
function activateOrDeactivateUser(isActive, userId){
	var isAssign;
	var success = false;
	if(isActive){
		isAssign = "yes";
	}else{
		isAssign = "no";
	}
	var payload = {
		"isAssign" : isAssign,
		"userIdToUpdate" : userId
	};
	$.ajax({
		url : "./updateuser.do",
		type : "POST",
		dataType : "html",
		data: payload,
		success : function(data) {
			$('#message-header').html(data);
			if($('#message-header>div').hasClass("success-message")){
				success = true;
			}
		},
		complete : function() {
			if(success){
				if(isActive){
					$('#icn-status-red').addClass("hide");
					$('#icn-status-green').removeClass("hide");
					$('#um-icn-status-text').html("Active");
				}else{
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

/*
*Onclick function for link to select tab
*/
$('.header-links-item-a').click(function(){
	$('#header-links').find('.header-links-item').find('.header-links-item-a').each(function(){
		$(this).attr("data-isclicked","false");
	});
	$(this).attr("data-isclicked","true");
	dashboardLink = $(this).data('link');
	
});