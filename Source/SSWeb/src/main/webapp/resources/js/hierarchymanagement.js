/**
 * js functions for hierarchy management
 */

/**
 * js function for adding a branch
 */
function addBranch(formId) {
	console.log("add branch called for form " + formId);
	var url = "./addbranch.do";
	callAjaxFormSubmit(url, addBranchCallBack, formId);
}

/**
 * call back function for add branch
 * 
 * @param data
 */
function addBranchCallBack(data) {
	console.log("Added branch successfully");
	showBranches();
	$(".create-branch-dd input[type='text']").val("");
}

function addOrUpdateBranch(formId) {
	/**
	 * if branch id is set in the form, branch needs an update else it is a new
	 * branch addition
	 */
	if ($("#branch-id-hidden").val().length > 0) {
		console.log("updating branch");
		updateBranch(formId);
	} else {
		console.log("adding new branch");
		addBranch(formId);
	}
}

/**
 * function to display branches
 */
function showBranches() {
	var url = "./fetchallbranches.do";
	callAjaxGET(url, showBranchesCallBack, true);
}

/**
 * call back function for displaying branches
 * 
 * @param data
 */
function showBranchesCallBack(data) {
	$("#existing-branches").html(data);

	// bind the click event of branches with edit
	$(".branch-element").click(function() {
		populateUpdateBranchForm(this);
	});
}

/**
 * Function to delete a branch
 * 
 * @param branchId
 */
function deleteBranch(branchId) {
	var url = "./deactivatebranch.do?branchId=" + branchId;
	console.log("delete branch url : " + url);
	callAjaxPOST(url, deleteBranchCallBack, true);
}

/**
 * Call back function for deleting a branch
 * 
 * @param data
 */
function deleteBranchCallBack(data) {
	showBranches();
}

/**
 * Method to populate the update form with selected branch contents
 * 
 * @param obj
 */
function populateUpdateBranchForm(obj) {
	console.log(obj);
	$('#branch-name-txt').val($(obj).html());
	$("#selected-region-id-hidden").val($(obj).data('regionid'));
	$("#branch-id-hidden").val($(obj).data('branchid'));
	$('#selected-region-txt').val($(obj).data('regionname'));
}

/**
 * Method to update a branch
 * 
 * @param formId
 */
function updateBranch(formId) {
	var url = "./updatebranch.do";
	callAjaxFormSubmit(url, updateBranchCallBack, formId);
}

/**
 * Call back method after updating a branch
 * 
 * @param data
 */
function updateBranchCallBack(data) {
	showBranches();
	$(".create-branch-dd input[type='text']").val("");
	$(".create-branch-dd input[type='hidden']").val("");
}

/**
 * function to display regions
 */
function showRegions() {
	var url = "./fetchallregions.do";
	callAjaxGET(url, showRegionsCallBack, true);
}

/**
 * call back function for displaying regions
 * 
 * @param data
 */
function showRegionsCallBack(data) {
	$("#existing-regions").html(data);
	populateRegionsSelector();
	
	if($("#enable-branches-form").length > 0) {
		$("#add-branch-form :input").prop("disabled", false);
		$("#branch-actions").children().attr("disabled", false);
	} else {
		$("#add-branch-form :input").prop("disabled", true);
		$("#branch-actions").children().attr("disabled", true);
	}
	
	// bind the click event of branches with edit
	$(".region-element").click(function() {
		populateUpdateRegionForm(this);
	});
}

/**
 * js function for adding a region
 */
function addRegion(formId) {
	console.log("add region called for form " + formId);
	var url = "./addregion.do";
	callAjaxFormSubmit(url, addRegionCallBack, formId);
}

/**
 * call back function for add region
 * 
 * @param data
 */
function addRegionCallBack(data) {
	console.log("Added region successfully");
	showRegions();
	$(".create-branch-dd input[type='text']").val("");
}

/**
 * Method to add or update a region based on the hidden value of regionId in
 * form
 * 
 * @param formId
 */
function addOrUpdateRegion(formId) {
	if ($("#region-id-hidden").val().length > 0) {
		console.log("updating region");
		updateRegion(formId);
	} else {
		console.log("adding new region");
		addRegion(formId);
	}
}

/**
 * Function to delete a region
 * 
 * @param branchId
 */
function deleteRegion(regionId) {
	var url = "./deactivateregion.do?regionId=" + regionId;
	console.log("delete region url : " + url);
	callAjaxPOST(url, deleteRegionCallBack, true);
}

/**
 * Call back function for deleting a region
 * 
 * @param data
 */
function deleteRegionCallBack(data) {
	showRegions();
}

/**
 * Method to populate regions in selector drop down
 */
function populateRegionsSelector() {
	var url = "./fetchregionsselector.do";
	callAjaxGET(url, populateRegionsSelectorCallBack, true);

}

/**
 * Call back method for populate regions selector
 * 
 * @param data
 */
function populateRegionsSelectorCallBack(data) {
	$("#hm-dd-wrapper-bottom").html(data);

	// bind the click event of selector
	$('.hm-dd-item').click(function() {
		$('#selected-region-txt').val($(this).html());
		$('#selected-region-id-hidden').val($(this).data('regionid'));
		$('#hm-dd-wrapper-bottom').slideToggle(200);
	});
}

/**
 * Method to populate the update region form
 * 
 * @param obj
 */
function populateUpdateRegionForm(obj) {
	console.log(obj);
	$('#region-name-txt').val($(obj).html());
	$("#region-id-hidden").val($(obj).data('regionid'));
}

/**
 * Method to update a region
 * 
 * @param formId
 */
function updateRegion(formId) {
	var url = "./updateregion.do";
	callAjaxFormSubmit(url, updateRegionCallBack, formId);
}

/**
 * Call back method to update a region
 * 
 * @param data
 */
function updateRegionCallBack(data) {
	showRegions();
	$(".create-branch-dd input[type='text']").val("");
	$(".create-branch-dd input[type='hidden']").val("");
}



// Region Delete popup overlay
function deleteRegionPopup(regionId) {
	$('#regionid-to-delete-hidden').val(regionId);
	var urlCheck = "./checkbranchesinregion.do?regionId=" + regionId;
	console.log("Check for deactivation url : " + urlCheck);
	callAjaxPOST(urlCheck, deleteRegionCheckCallBack, true);
}
function deleteRegionCheckCallBack(response) {
	$("#overlay-txt").html(response);
	$('.msg-err-icn').remove();

	var success = "Selected Region could be deleted";
	var successMsg = $("#overlay-txt").find('.success-message').text().trim();
	if (success == successMsg) {
		createPopupConfirm("Remove Region", "Are you Sure?");
	} else {
		createPopupInfo("Remove Region");
	}
}


// Branch Delete popup overlay
function deleteBranchPopup(branchId) {
	$('#branchid-to-delete-hidden').val(branchId);
	var urlCheck = "./checkusersinbranch.do?branchId=" + branchId;
	console.log("Check for deactivation url : " + urlCheck);
	callAjaxPOST(urlCheck, deleteBranchCheckCallBack, true);
}
function deleteBranchCheckCallBack(response) {
	$("#overlay-txt").html(response);
	$('.msg-err-icn').remove();
	var success = "Selected Branch could be deleted";
	var successMsg = $("#overlay-txt").find('.success-message').text().trim();
	if (success == successMsg) {
		createPopupConfirm("Remove Branch", "Are you Sure?");
	} else {
		createPopupInfo("Remove Branch");
	}
}


// Pop-up Overlay modifications
function createPopupConfirm(header, body) {
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$('#overlay-continue').removeClass("btn-disabled");
	$('#overlay-header').html(header);
	$('#overlay-txt').html(body);

	$('#overlay-confirm').show();
}
function createPopupInfo(header) {
	$('#overlay-continue').attr("disabled", true);
	$('#overlay-continue').addClass("btn-disabled");
	$('#overlay-header').html(header);
	
   	$('#overlay-confirm').show();
}
$('#overlay-continue').click(function(){
	if ($('#overlay-continue').attr("disabled") != "disabled") {
		$('#overlay-confirm').hide();
		$("#overlay-txt").html('');
		
		var regionIdDelete = $('#regionid-to-delete-hidden').val();
		var branchIdDelete = $('#branchid-to-delete-hidden').val();
		if(regionIdDelete != "") {
			deleteRegion(regionIdDelete);
			$('#regionid-to-delete-hidden').val('');
		} else if(branchIdDelete != "") {
			deleteBranch(branchIdDelete);
			$('#branchid-to-delete-hidden').val('');
		}
	}
});
$('#overlay-cancel').click(function(){
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$('#overlay-confirm').hide();
	$('#regionid-to-delete-hidden').val('');
	$('#branchid-to-delete-hidden').val('');
	$("#overlay-txt").html('');
});


// Branch details validation
var isBranchValid;
$('#branch-name-txt').blur(function() {
	validateBranchName(this.id);
});
$('#branch-address1-txt').blur(function() {
	validateCompanyEnterpriseAddress1(this.id);
});
$('#branch-address2-txt').blur(function() {
	validateAddress2(this.id);
});

function validateBranchInformation(elementId) {
	isBranchValid = true;
	var isFocussed = false;
	
	if(!validateBranchName('branch-name-txt')){
		isBranchValid = false;
		if(!isFocussed){
			$('#branch-name-txt').focus();
			isFocussed=true;
		}
	}
	if(!validateCompanyEnterpriseAddress1('branch-address1-txt')){
		isBranchValid = false;
		if(!isFocussed){
			$('#branch-address1-txt').focus();
			isFocussed=true;
		}
	}
	if(!validateAddress2('branch-address2-txt')){
		isBranchValid = false;
		if(!isFocussed){
			$('#branch-address2-txt').focus();
			isFocussed=true;
		}
	}
	return isBranchValid;
}

$("#branch-save-icon").click(function() {
	if ($("#branch-save-icon").attr("disabled") != "disabled") {
		console.log("submitting branch information form");
		if (validateBranchInformation('add-branch-form')) {
			addOrUpdateBranch("add-branch-form");
		}
	}
});


// Region details validation
var isRegionValid;
$('#region-name-txt').blur(function() {
	validateRegionName(this.id);
});
$('#region-address1-txt').blur(function() {
	validateCompanyEnterpriseAddress1(this.id);
});
$('#region-address2-txt').blur(function() {
	validateAddress2(this.id);
});

function validateRegionInformation(elementId) {
	isRegionValid = true;
	var isFocussed = false;
	
	if(!validateRegionName('region-name-txt')){
		isRegionValid = false;
		if(!isFocussed){
			$('#region-name-txt').focus();
			isFocussed=true;
		}
	}
	if(!validateCompanyEnterpriseAddress1('region-address1-txt')){
		isRegionValid = false;
		if(!isFocussed){
			$('#region-address1-txt').focus();
			isFocussed=true;
		}
	}
	if(!validateAddress2('region-address2-txt')){
		isRegionValid = false;
		if(!isFocussed){
			$('#region-address2-txt').focus();
			isFocussed=true;
		}
	}
	return isRegionValid;
}

$("#region-save-icon").click(function(e) {
	console.log("submitting region information form");
	if(validateRegionInformation('enterprise-branch-region')){
		addOrUpdateRegion("add-region-form");
	}
});