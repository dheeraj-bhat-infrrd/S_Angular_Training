/**
 * js functions for hierarchy management
 */

var numOfRows = 6;
var branchesStartIndex = 0;
var regionStartIndex = 0;
var usersStartIndex = 0;

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
	displayMessage(data);
	showBranches();
	$(".create-branch-dd input[type='text']").val("");
}

/**
 * function to display success/failure message to user after an action
 * @param data
 */
function displayMessage(data) {
	console.log("display message called :data "+data);
	$("#temp-message").html(data);
	var displayMessageDiv = $("#display-msg-div");
	if($(displayMessageDiv).hasClass("success-message")) {
		$("#overlay-toast").html($(displayMessageDiv).html());
		showToast();
	}	
	$("#temp-message").html("");
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
	//reset the start index
	branchesStartIndex = 0;
	
	if($("#account-type").attr('account-type') == "company") {
		searchBranchesForCompany("",branchesStartIndex,numOfRows);
	}
	else {
		searchBranches("",branchesStartIndex,numOfRows);
	}	
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
 * function to search for more branches called on clicking view more
 */updateBranch
function viewMoreBranches(obj) {
	var branchPattern = $("#search-branch-txt").val();
	if(branchPattern == undefined) {
		branchPattern = "";
	}
	branchesStartIndex = branchesStartIndex + numOfRows;
	if($("#account-type").attr('account-type') == "company") {
		searchBranchesForCompany("",branchesStartIndex,numOfRows);
	}
	else  {
		searchBranches(branchPattern,branchesStartIndex,numOfRows);
	}
	
	$(obj).hide();
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
	displayMessage(data);	
	showBranches();
}

/**
 * Method to populate the update form with selected branch contents
 * 
 * @param obj
 */
function populateUpdateBranchForm(obj) {
	var branchId = $(obj).data('branchid');
	var url = "./fetchbranchtoupdate.do?branchId="+branchId;
	callAjaxGET(url, populateUpdateBranchFormCallBack, true);	
}

/**
 * Call back method for populating the update branch form
 * @param data
 */
function populateUpdateBranchFormCallBack(data) {
	var branchsettings = $.parseJSON(data);
	if(branchsettings != null) {
		var organizationUnitSettings = branchsettings.organizationUnitSettings;
		var contactDetails = organizationUnitSettings.contact_details;
		if(contactDetails != null) {
			$("#branch-id-hidden").val(organizationUnitSettings.iden);
			$("#selected-region-id-hidden").val(branchsettings.regionId);
			$('#selected-region-txt').val(branchsettings.regionName);
			$('#branch-name-txt').val(contactDetails.name);
			$("#branch-address1-txt").val(contactDetails.address1);
			$("#branch-address2-txt").val(contactDetails.address2);
			window.scrollTo(200,0);
		}		
	}
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
	displayMessage(data);
	showBranches();
	$(".create-branch-dd input[type='text']").val("");
	$(".create-branch-dd input[type='hidden']").val("");
}

/**
 * Method to perform search on solr for provided branch pattern
 * @param branchPattern
 */
function searchBranches(branchPattern,start,rows) {
		var url = "./searchbranches.do?branchPattern="+branchPattern+"&start="+start+"&rows="+rows;
		callAjaxGET(url, searchBranchesCallBack, true);
}

/**
 * Callback method for search branches
 * 
 * @param data
 */
function searchBranchesCallBack(data) {
	var searchResult =  $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		if(len > 0) {
			
			if(len > numOfRows) {
				searchResult.splice(len-1,1);
			}
			
			$.each(searchResult,function(i,branch) {
				htmlData = htmlData +'<div class="hm-sub-item clearfix">';
				htmlData = htmlData +'<div class="float-left hm-sub-item-left branch-element" data-branchid = "'+branch.branchId+'" data-regionid = "'+branch.regionId+'" data-regionname = "'+branch.regionName+'">'+branch.branchName+'</div>';
				htmlData = htmlData +'<div class="float-right icn-remove cursor-pointer hm-item-height-adjust" onclick ="deleteBranchPopup('+branch.branchId+')"></div></div>';
			});
			
			if(len > numOfRows) {
				htmlData = htmlData +'<div id="view-more-branch-div" class="hm-um-btn-view-all blue-text" onclick=viewMoreBranches(this)><span class="um-hm-viewall cursor-pointer">View All...</span></div>';
			}
		}
		else {
			if(branchesStartIndex == 0) {
				htmlData = 'No branches are added yet';
			}
			else {
				htmlData = 'No more branches present';
			}
		}
		if(branchesStartIndex == 0) {
			$("#existing-branches").html(htmlData);
		}
		else {
			$("#existing-branches").append(htmlData);
		}		
		
		// bind the click event of branches with edit
		$(".branch-element").click(function() {
			populateUpdateBranchForm(this);
		});
	}
}

function searchBranchesForCompany(branchPattern,start,rows){
	var url = "./searchbranches.do?branchPattern="+branchPattern+"&start="+start+"&rows="+rows;
	callAjaxGET(url, searchBranchesForCompanyCallBack, true);
}

function searchBranchesForCompanyCallBack(data) {
	var searchResult =  $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = '<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-bottom-panel-item padding-right-30" id="hm-branches-left"></div>';
		console.log("searchResult is "+searchResult+" len : "+len+" numrows : "+numOfRows+" branchesStartIndex:"+branchesStartIndex);
		var leftColHtml = "";
		var rightColHtml = "";
		if(len > 0) {
			if(len > numOfRows) {
				searchResult.splice(len-1,1);
			}
			
			$.each(searchResult,function(i,branch) {
				if(i % 2 == 0) {
					leftColHtml = leftColHtml +'<div class="hm-sub-item clearfix">';
					leftColHtml = leftColHtml +'<div class="float-left hm-sub-item-left branch-element" data-branchid = "'+branch.branchId+'" data-regionid = "'+branch.regionId+'" data-regionname = "'+branch.regionName+'">'+branch.branchName+'</div>';
					leftColHtml = leftColHtml +'<div class="float-right icn-remove cursor-pointer hm-item-height-adjust" id="branch-"'+branch.branchId+'"} onclick ="deleteBranchPopup('+branch.branchId+')"></div></div>';
				}
			});
			
			htmlData = htmlData + ' <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-bottom-panel-item padding-left-30" id="hm-branches-right"></div>';
			$.each(searchResult,function(i,branch) {
				if(i % 2 != 0) {
					rightColHtml = rightColHtml +'<div class="hm-sub-item clearfix">';
					rightColHtml = rightColHtml +'<div class="float-left hm-sub-item-left branch-element" data-branchid = "'+branch.branchId+'" data-regionid = "'+branch.regionId+'" data-regionname = "'+branch.regionName+'">'+branch.branchName+'</div>';
					rightColHtml = rightColHtml +'<div class="float-right icn-remove cursor-pointer hm-item-height-adjust" id="branch-"'+branch.branchId+'"} onclick ="deleteBranchPopup('+branch.branchId+')"></div></div>';
				}
			});
		}
		else {
			if(branchesStartIndex == 0) {
				htmlData = 'No branches are added yet';
			}
			else {
				htmlData = 'No more branches present';
			}
		}
		if(branchesStartIndex == 0) {
			$("#existing-branches").html(htmlData);
			$("#hm-branches-left").html(leftColHtml);
			$("#hm-branches-right").html(rightColHtml);
		}
		else {
			$("#existing-branches").append(htmlData);
			$("#hm-branches-left").append(leftColHtml);
			$("#hm-branches-right").append(rightColHtml);
		}
		
		if(len > numOfRows) {
			$("#view-more-branch-div").remove();
			$("#hm-all-existing-comp-branches").after('<div id="view-more-branch-div" class="hm-um-btn-view-all blue-text" onclick=viewMoreBranches(this)><span class="um-hm-viewall cursor-pointer">View All...</span></div>');
		}
		
		// bind the click event of branches with edit
		$(".branch-element").click(function() {
			populateUpdateBranchForm(this);
		});
	}
}

function clearBranchForm() {
	$("#add-branch-form :input").val("");
	$("#add-branch-form .error-msg").hide();
	$("#hm-add-region-admin-txt").hide();
}

$("#branch-clear-icon").click(function() {
	clearBranchForm();
});

/**
 * function to display regions
 */
function showRegions() {
	//reset the start index
	regionStartIndex = 0;
	searchRegions("",regionStartIndex,numOfRows);
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
	displayMessage(data);
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
 * function to search for more regions called on clicking view more
 */
function viewMoreRegions(obj) {
	var regionPattern = $("#search-region-txt").val();
	if(regionPattern == undefined) {
		regionPattern = "";
	}
	regionStartIndex = regionStartIndex + numOfRows;
	searchRegions(regionPattern,regionStartIndex,numOfRows);
	$(obj).hide();
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
	displayMessage(data);
	showRegions();
}

/**
 * Method to populate the update region form
 * 
 * @param obj
 */
function populateUpdateRegionForm(obj) {
	var regionId = $(obj).data('regionid');
	var url = "./fetchregiontoupdate.do?regionId="+regionId;
	callAjaxGET(url, populateUpdateRegionFormCallBack, true);	
}

/**
 * Call back function for populating update region form
 * @param data
 */
function populateUpdateRegionFormCallBack(data) {
	var regionsettings = $.parseJSON(data);
	if(regionsettings != null) {
		var contactDetails = regionsettings.contact_details;
		if(contactDetails != null) {
			console.log("contactDetails "+contactDetails);
			$("#region-id-hidden").val(regionsettings.iden);
			$('#region-name-txt').val(contactDetails.name);
			$("#region-address1-txt").val(contactDetails.address1);
			$("#region-address2-txt").val(contactDetails.address2);
			window.scrollTo(200,0);
		}		
	}
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
	displayMessage(data);
	showRegions();
	$(".create-branch-dd input[type='text']").val("");
	$(".create-branch-dd input[type='hidden']").val("");
}

/**
 * Region Delete popup overlay
 * 
 * @param regionId
 */
function deleteRegionPopup(regionId) {
	var urlCheck = "./checkbranchesinregion.do?regionId=" + regionId;
	callAjaxPOST(urlCheck, function(response) {
		deleteRegionCheckCallBack(response, regionId);
	}, true);
}
function deleteRegionCheckCallBack(response, regionId) {
	$('#overlay-text').html(response);
	$('.msg-err-icn').remove();

	var success = "Selected Region could be deleted";
	var successMsg = $("#overlay-text").find('.success-message').text().trim();
	if (success == successMsg) {
		createPopupConfirm("Remove Region");
		
		$('#overlay-continue').click(function(){
			if ($('#overlay-continue').attr("disabled") != "disabled") {
				if(regionId != null) {
					overlayRevert();
					deleteRegion(regionId);
					regionId = null;
				}
				$('#overlay-continue').unbind('click');
			}
		});
	} else {
		createPopupInfo("Remove Region");
		regionId = null;
	}
}


/**
 * Branch Delete popup overlay
 * 
 * @param branchId
 */
function deleteBranchPopup(branchId) {
	var urlCheck = "./checkusersinbranch.do?branchId=" + branchId;
	callAjaxPOST(urlCheck, function(response) {
		deleteBranchCheckCallBack(response, branchId);
	}, true);
}
function deleteBranchCheckCallBack(response, branchId) {
	$("#overlay-text").html(response);
	$('.msg-err-icn').remove();
	var success = "Selected Branch could be deleted";
	var successMsg = $("#overlay-text").find('.success-message').text().trim();
	if (success == successMsg) {
		createPopupConfirm("Remove Branch");
		
		$('#overlay-continue').click(function(){
			if ($('#overlay-continue').attr("disabled") != "disabled") {
				if(branchId != null) {
					overlayRevert();
					deleteBranch(branchId);
					branchId = null;
				}
				$('#overlay-continue').unbind('click');
			}
		});
	} else {
		createPopupInfo("Remove Branch");
		branchId = null;
	}
}

/**
 * Method to clear out all input fields in region addition form
 */
function clearRegionForm() {
	$("#add-region-form :input").val("");
	$("#add-region-form .error-msg").hide();
	$("#hm-add-branch-admin-txt").hide();
}

$("#region-clear-icon").click(function() {
	clearRegionForm();
});

// Pop-up Overlay modifications
$('#overlay-cancel').click(function(){
	$('#overlay-continue').unbind('click');
	overlayRevert();
	branchId = null;
	regionId = null;
});
function createPopupConfirm(header) {
	$('#overlay-header').html(header);
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$('#overlay-continue').removeClass("btn-disabled");
	$('#overlay-continue').html('Continue');
	$('#overlay-cancel').html('Cancel');

	$('#overlay-main').show();
}

function createPopupInfo(header) {
	$('#overlay-header').html(header);
	$('#overlay-continue').attr("disabled", true);
	$('#overlay-continue').addClass("btn-disabled");
	$('#overlay-continue').html('Continue');
	$('#overlay-cancel').html('Cancel');
	
   	$('#overlay-main').show();
}
function overlayRevert() {
	$('#overlay-main').hide();
	if ($('#overlay-continue').attr("disabled") == "disabled") {
		$('#overlay-continue').removeAttr("disabled");
	}
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');
}

/**
 * Branch details validation
 */

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
$('#selected-region-txt').blur(function(){
	validateRegionSelector('selected-region-id-hidden','selected-region-txt');
});

function validateRegionSelector(hiddenElementId,textElementId) {
	if($(window).width()<768){
		if ($('#'+hiddenElementId).val() == "" || $('#'+textElementId).val() == "") {
			$('#overlay-toast').html('Please select a region');
			showToast();
			return false;
		}
		return true;
	}else{
		if ($('#'+hiddenElementId).val() == "" || $('#'+textElementId).val() == "") {
			$('#'+hiddenElementId).next('.input-error-2').html('Please select a region');
			$('#'+hiddenElementId).next('.input-error-2').show();
			return false;
		}		
		$('#'+hiddenElementId).next('.input-error-2').hide();
		return true;
	}
}

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
	
	if($('#selected-region-id-hidden').length > 0){
		if(!validateRegionSelector('selected-region-id-hidden','selected-region-txt')){
			isRegionValid = false;
			if(!isFocussed){
				$('#selected-region-txt').focus();
				isFocussed=true;
			}
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
			
			var branchId = $("#branch-id-hidden").val();
			if(branchId != undefined && branchId != "") {
				assignBranchAdmin();
			}
		}
	}
});


/**
 * Region details validation
 */
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

/**
 * Method to fetch regions from solr for populating region selector
 * 
 * @param regionPattern
 */
function populateRegionsSelector(regionPattern) {
	console.log("Method populateRegionsSelector called for regionPattern : "+regionPattern);
	var url = "./searchregions.do?regionPattern="+regionPattern+"&start=0&rows=-1";
	callAjaxGET(url, populateRegionsSelectorCallBack, true);
}

/**
 * callback method for fetching regions from solr for populating region selector
 * @param data
 */
function populateRegionsSelectorCallBack(data) {
	console.log("populateRegionsSelectorCallBack : "+data);
	var searchResult = $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		console.log("searchResult is "+searchResult);
		if(len > 0) {
			$.each(searchResult,function(i,region) {
					htmlData = htmlData +'<div class="hm-dd-item hm-dd-item-keys hm-dd-hover" data-regionid="'+region.regionId+'">'+region.regionName+'</div>';
			});
			
			$("#hm-dd-wrapper-bottom").html(htmlData).slideToggle(200);
			
			// bind the click event of selector
			$('.hm-dd-item-keys').click(function() {
				$('#selected-region-txt').val($(this).html());
				$('#selected-region-id-hidden').val($(this).data('regionid'));
				$('#selected-region-id-hidden').next('.input-error-2').hide();
				$('#hm-dd-wrapper-bottom').slideToggle(200);
			});	
			
			//bind the hover event
			$(".hm-dd-hover").hover(function() {
				$(".hm-dd-item-keys").removeClass("hm-dd-item-keys-selected");
			});
			
		}
	}	
}

/**
 * Method to search regions for provided pattern
 * 
 * @param regionPattern
 */
function searchRegions(regionPattern,start,rows){
	console.log("search regions called for regionPattern : "+regionPattern+" start:"+start+" rows:"+rows);
	var url = "./searchregions.do?regionPattern="+regionPattern+"&start="+start+"&rows="+rows;
	callAjaxGET(url, searchRegionsCallBack, true); 
}

/**
 * call back for searching regions with provided pattern
 * @param data
 */
function searchRegionsCallBack(data) {
	var searchResult =  $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		console.log("searchResult is "+searchResult);
		if(len > 0) {
			
			if(len > numOfRows) {
				searchResult.splice(len-1,1);
			}
			
			htmlData = htmlData +'<input type="hidden" id="enable-branches-form" value="true">';
			$.each(searchResult,function(i,region) {
				htmlData = htmlData +'<div class="hm-sub-item clearfix">';
					htmlData = htmlData + '<div class="float-left hm-sub-item-left region-element" data-regionid = '+region.regionId+'>'+region.regionName+'</div>';
					htmlData = htmlData + '<div class="float-right icn-remove cursor-pointer hm-item-height-adjust" onclick=deleteRegionPopup('+region.regionId+')></div></div>';
			});
			
			if(len > numOfRows) {
				htmlData = htmlData +'<div id="view-more-region-div" class="hm-um-btn-view-all blue-text" onclick=viewMoreRegions(this)><span class="um-hm-viewall cursor-pointer">View All...</span></div>';
			}
		}
		else {
			if(regionStartIndex == 0) {
				htmlData = 'No regions are added yet';
			}
			else {
				htmlData = 'No more regions present';
			}
		}
		if(regionStartIndex == 0) {
			$("#existing-regions").html(htmlData);
		}
		else {
			$("#existing-regions").append(htmlData);
		}	
		
		//validations for enabling branches form
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
}

$("#region-save-icon").click(function(e) {
	if(validateRegionInformation('enterprise-branch-region')){
		addOrUpdateRegion("add-region-form");
		
		var regionId = $("#region-id-hidden").val();
		if(regionId != undefined && regionId != "") {
			assignRegionAdmin();
		}
	}
});

$('#selected-region-txt').click(function(){
	populateRegionsSelector("");
});

$("#selected-region-txt").focus(function() {
	console.log("focus");
	var text = $("#selected-region-txt").val();
	if (text.length > 1) {
		delay(function() {
			populateRegionsSelector(text);
		}, 500);
	}
	else {
		$('#hm-dd-wrapper-bottom').slideUp(200);
	}
});

$("#selected-region-txt").keyup(function() {
	console.log("key up");
	var text = $("#selected-region-txt").val();
	if (text.length > 1) {
		delay(function() {
			populateRegionsSelector(text);
		}, 500);
	}
});

//binding arrow keys with region selector
$("#selected-region-txt").keydown(function(e) {
	if(e.which == 40) {
		var text = $("#selected-region-txt").val();
		if(text == undefined) {
			text = "";
		}
		if (!($("#hm-dd-wrapper-bottom").css("display") =="block")){
			delay(function() {
				populateRegionsSelector(text);
			}, 500);
		}else {
			var current = $("#hm-dd-wrapper-bottom").find(".hm-dd-item-keys-selected");
			if(current.length > 0) {
				$(current).removeClass("hm-dd-item-keys-selected");
				$(current).next().addClass("hm-dd-item-keys-selected");
			}
			else {
				$("#hm-dd-wrapper-bottom :first-child").addClass("hm-dd-item-keys-selected");
			}
			$("#hm-dd-wrapper-bottom").show();
		}
		
	}	
	else if(e.which == 38){
		var current = $("#hm-dd-wrapper-bottom").find(".hm-dd-item-keys-selected");
		if(current.length > 0) {
			$(current).removeClass("hm-dd-item-keys-selected");
			$(current).prev().addClass("hm-dd-item-keys-selected");
		}else {
			$('#hm-dd-wrapper-bottom').slideUp(200);
		}
	}else if(e.which == 13) {
		var selectedItem = $("#hm-dd-wrapper-bottom").find(".hm-dd-item-keys-selected");
		if(selectedItem.length == 0) {
			selectedItem = $("#hm-dd-wrapper-bottom :first-child");
		}
		$('#selected-region-txt').val($(selectedItem).html());
		$('#selected-region-id-hidden').val($(selectedItem).data('regionid'));
		$('#selected-region-id-hidden').next('.input-error-2').hide();
		$('#hm-dd-wrapper-bottom').slideToggle(200);
		
	}
});

$("#search-region-txt").keyup(function() {
	var text = $("#search-region-txt").val();
	regionStartIndex = 0;
	if (text.length > 1) {
		delay(function() {
			searchRegions(text,regionStartIndex,numOfRows);
		}, 500);
	}
	else {
		delay(function() {
			searchRegions("",regionStartIndex,numOfRows);
		}, 500);
	}
});

$("#search-branch-txt").keyup(function() {
	var text = $("#search-branch-txt").val();
	branchesStartIndex = 0;	
	if (text.length > 1) {
		delay(function() {
			searchBranches(text,branchesStartIndex,numOfRows);
		}, 500);
	}
	else {
		delay(function() {
			searchBranches("",branchesStartIndex,numOfRows);
		}, 500);
	}
});

$("#search-company-branch-txt").keyup(function() {
	var text = $("#search-company-branch-txt").val();
	branchesStartIndex = 0;	
	if (text.length > 1) {	
		delay(function() {
			searchBranchesForCompany(text,branchesStartIndex,numOfRows);
		}, 500);
	}
	else {
		delay(function() {
			searchBranchesForCompany("",branchesStartIndex,numOfRows);
		}, 500);
	}
});

/**
 * function for adding delay to a function call
 */
var delay = (function() {
	var timer = 0;
	return function(callback, ms) {
		clearTimeout(timer);
		timer = setTimeout(callback, ms);
	};
})();

$('.dd-icn-plus').click(function(){
	$(this).hide();	
	if($("#account-type").attr('account-type') == "company") {
		$('.dd-icn-minus').show();
		$(this).parent().parent().next('.hm-dd-main-content').slideToggle();
	}
	else {
		$(this).next('.dd-icn-minus').show();
		$(this).parent().next('.hm-dd-main-content').slideToggle();
	}	
	
});

$('.dd-icn-minus').click(function(){
	$(this).hide();	
	if($("#account-type").attr('account-type') == "company") {
		$('.dd-icn-plus').show();
		$(this).parent().parent().next('.hm-dd-main-content').slideToggle();
	}
	else {
		$(this).prev('.dd-icn-plus').show();
		$(this).parent().next('.hm-dd-main-content').slideToggle();
	}
});

/**
 * Method to fetch the users list for providing option to assign branch admin
 * 
 * @param searchKey
 * @param start
 * @param rows
 */
function showUsersForBranchAdmin(searchKey,start,rows) {
	var url="./finduserbyemail.do?startIndex="+start+"&batchSize="+rows+"&searchKey="+searchKey;
	callAjaxGET(url, showUsersForBranchAdminCallBack, true);
}

/**
 * Call back function for showUsersForBranchAdmin, populates the dropsown with users list obtained
 * @param data
 */
function showUsersForBranchAdminCallBack(data) {
	var usersList = $.parseJSON(data);
	var htmlData = "";
	if(usersList != null) {
		var len = usersList.length;
		if(len > 0) {
			$.each(usersList,function(i,user) {
				htmlData = htmlData +'<div class="hm-dd-item hm-branch-user hm-dd-hover" data-userid="'+user.userId+'">'+user.firstName+" "+user.lastName+'</div>';
			});
		}
	}	
	$("#hm-branch-userslist").html(htmlData).slideToggle(200);
	
	//bind the hover event
	$(".hm-dd-hover").hover(function() {
		$(".hm-dd-item-keys").removeClass("hm-dd-item-keys-selected");
	});
	
	// bind the click event of selector
	$(".hm-branch-user").click(function() {
		$('#hm-add-branch-admin-txt').val($(this).html());
		$('#hm-add-branch-admin-hidden-userid').val($(this).data('userid'));
		$('#hm-branch-userslist').slideToggle(200);
	});
}

$("#hm-add-branch-admin-txt").click(function() {
	showUsersForBranchAdmin("",usersStartIndex,numOfRows);
});

$("#hm-add-branch-admin-txt").keyup(function() {
	var text = $(this).val();
	usersStartIndex = 0;	
	if (text.length > 1) {
		delay(function() {
			showUsersForBranchAdmin(text,usersStartIndex,numOfRows);
		}, 500);
	}
	else {
		delay(function() {
			showUsersForBranchAdmin("",usersStartIndex,numOfRows);
		}, 500);
	}
});

$("#add-branch-admin-btn").click(function() {
	$('#hm-add-branch-admin-txt').show();
	showUsersForBranchAdmin("",usersStartIndex,numOfRows);
});

/**
 * Method to assign a user as branch admin
 */
function assignBranchAdmin(){
	var userId = $("#hm-add-branch-admin-hidden-userid").val();
	var branchId = $("#branch-id-hidden").val();
	var url = "./assignorunassignbranchadmin.do?branchId="+branchId+"&userId="+userId+"&isAssign=YES";
	callAjaxPOST(url, assignBranchAdminCallBack, true);
}

/**
 * callback for assignBranchAdmin
 * @param data
 */
function assignBranchAdminCallBack(data) {
	clearBranchForm();
}

/**
 * Method to fetch users for assigning region admin
 * @param searchKey
 * @param start
 * @param rows
 */
function showUsersForRegionAdmin(searchKey,start,rows) {
	var url="./finduserbyemail.do?startIndex="+start+"&batchSize="+rows+"&searchKey="+searchKey;
	callAjaxGET(url, showUsersForRegionAdminCallBack, true);
}

/**
 * Callback for showUsersForRegionAdmin, populates the dropdown with users list obtained
 * @param data
 */
function showUsersForRegionAdminCallBack(data) {
	var usersList = $.parseJSON(data);
	var htmlData = "";
	if(usersList != null) {
		var len = usersList.length;
		if(len > 0) {
			$.each(usersList,function(i,user) {
				htmlData = htmlData +'<div class="hm-dd-item hm-region-user hm-dd-hover" data-userid="'+user.userId+'">'+user.firstName+" "+user.lastName+'</div>';
			});
		}
	}	
	$("#hm-region-userslist").html(htmlData).slideToggle(200);
	
	//bind the hover event
	$(".hm-dd-hover").hover(function() {
		$(".hm-dd-item-keys").removeClass("hm-dd-item-keys-selected");
	});
	
	// bind the click event of selector
	$(".hm-region-user").click(function() {
		$('#hm-add-region-admin-txt').val($(this).html());
		$('#hm-add-region-admin-hidden-userid').val($(this).data('userid'));
		$('#hm-region-userslist').slideToggle(200);
	});
}

$("#hm-add-region-admin-txt").click(function() {
	showUsersForRegionAdmin("",usersStartIndex,numOfRows);
});

$("#hm-add-region-admin-txt").keyup(function() {
	var text = $(this).val();
	usersStartIndex = 0;	
	if (text.length > 1) {
		delay(function() {
			showUsersForRegionAdmin(text,usersStartIndex,numOfRows);
		}, 500);
	}
	else {
		delay(function() {
			showUsersForRegionAdmin("",usersStartIndex,numOfRows);
		}, 500);
	}
});

$("#add-region-admin-btn").click(function() {
	$('#hm-add-region-admin-txt').show();
	showUsersForRegionAdmin("",usersStartIndex,numOfRows);
});

/**
 * Method to assign a user as region admin
 */
function assignRegionAdmin(){
	var userId = $("#hm-add-region-admin-hidden-userid").val();
	var regionId = $("#region-id-hidden").val();
	var url = "./assignregionadmin.do?regionId="+regionId+"&userId="+userId;
	callAjaxPOST(url, assignRegionAdminCallBack, true);
}

/**
 * callback for assignRegionAdmin
 * @param data
 */
function assignRegionAdminCallBack(data) {
	clearRegionForm();
}