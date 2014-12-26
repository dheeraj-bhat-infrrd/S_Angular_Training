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