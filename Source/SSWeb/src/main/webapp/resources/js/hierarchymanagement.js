/**
 * js functions for hierarchy management
 */

/**
 * js function for adding a branch
 */
function addBranch(formId){
	console.log("add branch called for form "+formId);
	var url = "./addbranch.do";
	callAjaxFormSubmit(url, addBranchCallBack, formId);
}

/**
 * call back function for add branch
 * @param data
 */
function addBranchCallBack(data) {
	console.log("Added branch successfully");
	showBranches();
	$(".create-branch-dd input[type='text']").val("");
}

/**
 * function to display branches
 */
function showBranches(){
	var url = "./fetchallbranches.do";
	callAjaxGET(url, showBranchesCallBack, true);
}

/**
 * call back function for displaying branches
 * @param data
 */
function showBranchesCallBack(data) {
	$("#existing-branches").html(data);
}

/**
 * Function to delete a branch
 * @param branchId
 */
function deleteBranch(branchId) {
	var url="./deactivatebranch.do?branchId="+branchId;
	console.log("delete branch url : "+url);
	callAjaxPOST(url, deleteBranchCallBack, true);
}

/**
 * Call back function for deleting a branch
 * @param data
 */
function deleteBranchCallBack(data) {
	showBranches();
}

/**
 * function to display regions
 */
function showRegions(){
	var url = "./fetchallregions.do";
	callAjaxGET(url, showRegionsCallBack, true);
}

/**
 * call back function for displaying regions
 * @param data
 */
function showRegionsCallBack(data) {
	$("#existing-regions").html(data);
}

/**
 * js function for adding a region
 */
function addRegion(formId){
	console.log("add region called for form "+formId);
	var url = "./addregion.do";
	callAjaxFormSubmit(url, addRegionCallBack, formId);
}

/**
 * call back function for add region
 * @param data
 */
function addRegionCallBack(data) {
	console.log("Added reigion successfully");
	showRegions();
	$(".create-region-dd input[type='text']").val("");
}

/**
 * Function to delete a region
 * @param branchId
 */
function deleteRegion(regionId) {
	var url="./deactivateregion.do?regionId="+regionId;
	console.log("delete region url : "+url);
	callAjaxPOST(url, deleteRegionCallBack, true);
}

/**
 * Call back function for deleting a region
 * @param data
 */
function deleteRegionCallBack(data) {
	showRegions();
}