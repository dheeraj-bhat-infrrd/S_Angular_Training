/**
 * js functions for hierarchy and user management
 */
var usersStartIndex = 0;
var numOfRows = 6;

/**
 * function to display success/failure message to user after an action
 * @param data
 */
function displayMessage(data) {
	$("#temp-message").html(data);
	var displayMessageDiv = $("#display-msg-div");
	if($(displayMessageDiv).hasClass("success-message")) {
		showInfoMobileAndWeb($(displayMessageDiv).html());
	}
	else if($(displayMessageDiv).hasClass("error-message")) {
		showErrorMobileAndWeb($(displayMessageDiv).html());
	}	
	$("#temp-message").html("");
}

/**
 * checks whether is authorized to build hierarchy and displays message to the user
 */
function checkUserAuthorization(){
	var data = $("#server-message").html();
	var isUserAuthorized = $("#is-user-authorized").val();
	if(isUserAuthorized == "false") {
		displayMessage(data);
	}
}

/**
 * Method to fetch the company hierarchy
 */
function fetchCompanyHierarchy() {
	var profileName = $("#profile-name").val();
    fetchHierarchy("companyProfileName", profileName);
}

/**
 * function to get the edit form based on tab value 
 */
function getEditSectionForm(tabValue) {
	switch(tabValue){
    case 'region': 
    	getRegionEditPage();
        break;
    case 'office': 
        getOfficeEditPage();
        break;
    case 'individual': 
    	getIndividualEditPage();
        break;
    case 'csv': 
    	getCsvUploadPage();
        break;
    default: 
    	getRegionEditPage();
        break;
	}
}

/**
 * function to get the region edit page
 */
function getRegionEditPage(){
	var url = "./getregioneditpage.do";
	callAjaxGET(url, paintEditSection, true);
}

/**
 * function to get the office edit page
 */
function getOfficeEditPage(){
	var url = "./getofficeeditpage.do";
	callAjaxGET(url, paintEditSection, true);
}
/**
 * function to get the individual edit page
 */
function getIndividualEditPage(){
	var url = "./getindividualeditpage.do";
	callAjaxGET(url, paintEditSection, true);
}

function paintEditSection(data) {
	var isUserAuthorized = $("#is-user-authorized").val();
	$("#bd-edit-form-section").html(data);
	/**
	 * allow hierarchy management only if the user is authorized
	 */
	if(isUserAuthorized == "false") {
		$("#bd-edit-form-section :input").prop("disabled",true);
		$("#bd-edit-form-section").click(function(){
			return false;
		});
		return false;
	}	
	
	/**
	 * bind the click events
	 */
	$("#selected-user-txt").click(function() {
		getUsersList("",usersStartIndex,numOfRows);
	});
	$("#selected-user-txt").keydown(function(e) {
		bindArrowKeysWithSelector(e, "selected-user-txt", "users-droplist", getUsersList, "selected-userid-hidden", "data-userid");
	});
	
	$("#btn-region-save").click(function(e){
		if(validateRegionForm()){
			addRegion("edit-region-form");
		}
	});
	
	$('#region-name-txt').blur(function() {
		if(validateRegionName(this.id)){
			hideError();
		}
	});

	$("#selected-user-txt").keyup(function(e) {
		if(e.which != 38 && e.which != 40 && e.which != 13) {
			var text = $(this).val();
			usersStartIndex = 0;	
			if (text.length > 0) {
				delay(function() {
					getUsersList(text,usersStartIndex,numOfRows);
				}, 500);
			}
			else {
				delay(function() {
					getUsersList("",usersStartIndex,numOfRows);
				}, 500);
			}
		}
	});
	
	$('.bd-check-img').click(function(e) {
		 $(this).toggleClass('bd-check-img-checked');
		/**
		 * If class is "bd-check-img-checked", check box is unchecked ,
		 * hence setting the hidden value as false
		 */
		 if($(this).hasClass('bd-check-img-checked') ){
			$(this).next("#is-admin-chk").val("false");
		 }
		 else {
			$(this).next("#is-admin-chk").val("true");
		 }
    });
	
	$('.bd-cust-rad-img').click(function(e) {
        $('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
        $(this).toggleClass('bd-cust-rad-img-checked');
        if($(this).data('type') == "single"){
            $('#bd-single').show();
            $('#bd-multiple').hide();
            showAdminPrivilegesChk();
        }else if($(this).data('type') == "multiple"){
            $('#bd-single').hide();
            $('#bd-multiple').show();
            $('#selected-userid-hidden').val("");
            hideAdminPrivilegesChk();
        }
        $('#user-selection-info').attr('data-user-selection-type',$(this).data('type'));
    });
	
	$('#assign-to-selector').click(function(e) {
		e.stopPropagation();
		$("#assign-to-droplist").slideToggle(200);
	});
	
	$('.hm-assignto-options').click(function(e) {
		e.stopPropagation();
		var assignToOption = $(this).attr('data-assign-to-option');
		$("#assign-to-txt").val($(this).html());
		$("#assign-to-txt").attr("data-assignto",assignToOption);
		
		switch(assignToOption) {
			case 'company':
				disableRegionSelector();
				disableOfficeSelector();
				break;
			case 'region':
				$("#selected-region-txt").prop("disabled",false);
				disableOfficeSelector();
				$("#bd-region-selector").show();
				break;
			case 'office':
				$("#selected-office-txt").prop("disabled",false);
				$("#bd-office-selector").show();
				disableRegionSelector();
				break;
			default:
				$("#selected-region-txt").prop("disabled",false);
				$("#selected-office-txt").prop("disabled",false);
		}
		$("#assign-to-droplist").slideToggle(200);
	});
	
	$("#selected-region-txt").keyup(function(e) {
		if(e.which != 38 && e.which != 40 && e.which != 13) {
			var text = $("#selected-region-txt").val();
			if (text.length > 0) {
				delay(function() {
					populateRegionsSelector(text);
				}, 500);
			}else{
				$("#regions-droplist").slideUp(200);
			}
		}
	});
	
	$("#region-selector").click(function(e){
		e.stopPropagation();
		if(!$('#selected-region-txt').is(':disabled')){
			var regionPattern = $("#selected-region-txt").val();
			if(regionPattern == undefined) {
				regionPattern = "";
			}
			populateRegionsSelector(regionPattern);
		}		
	});
	
	$("#btn-office-save").click(function(e){
		if(validateOfficeForm()){
			addOffice("edit-office-form");
		}
	});
	
	$('#office-name-txt').blur(function() {
		if(validateOfficeName(this.id)){
			hideError();
		}
	});
	
	$("#office-selector").click(function(e){
		e.stopPropagation();
		if(!$('#selected-office-txt').is(':disabled')){
			var officePattern = $("#selected-office-txt").val();
			if(officePattern == undefined) {
				officePattern = "";
			}
			populateOfficesSelector(officePattern);
		}		
	});
	
	$("#selected-office-txt").keyup(function() {
		var text = $("#selected-office-txt").val();
		if (text.length > 0) {
			delay(function() {
				populateOfficesSelector(text);
			}, 500);
		}else {
			$("#offices-droplist").slideUp(200);
		}
	});
	
	$("#btn-individual-save").click(function(e){
		if(validateIndividualForm()){
			addIndividual("edit-individual-form");
		}
	});
}

function showAdminPrivilegesChk(){
	$("#admin-privilege-div").show();
	if($('.bd-check-img').hasClass('bd-check-img-checked') ){
		$('.bd-check-img').next("#is-admin-chk").val("true");
		$('.bd-check-img').removeClass('bd-check-img-checked');
	}
}

function hideAdminPrivilegesChk(){
	$("#admin-privilege-div").hide();
	$('.bd-check-img').next("#is-admin-chk").val("false");
	$('.bd-check-img').addClass('bd-check-img-checked');
}

function disableRegionSelector(){
	$("#selected-region-txt").prop("disabled",true);
	$("#selected-region-txt").val("");
	$('#selected-region-id-hidden').val("");
	$("#bd-region-selector").hide();
}

function disableOfficeSelector(){
	$("#selected-office-txt").prop("disabled",true);
	$("#selected-office-txt").val("");
	$('#selected-office-id-hidden').val("");
	$('#selected-region-id-hidden').val("");
	$("#bd-office-selector").hide();
}

/**
 * Region details validation
 */
var isRegionValid;

/**
 * Function to validate Region name
 */
function validateRegionName(elementId){
	if ($('#'+elementId).val() != "") {
		if (companyNameRegEx.test($('#'+elementId).val()) == true) {
			return true;
		}else {
			showErrorMobileAndWeb('Please enter a valid region name.');
			return false;
		}
	}else{
		showErrorMobileAndWeb('Please enter region name.');
		return false;
	}
}

function validateUserEmailTextArea(elementId) {
	var emailIds = $('#'+elementId).val();
	if (emailIds != "") {
		var emailIdsArray = emailIds.split(",");
		for(var i = 0; i < emailIdsArray.length; i++) {
			if(emailRegex.test(emailIdsArray[i]) == true){
				return true;
			}
			else {
				showErrorMobileAndWeb('Please enter valid email addresses');
				return false;
			}
		}
	}
}

function validateUserSelection(elementId,hiddenElementId) {
	if ($('#'+elementId).val() != "") {
		if($("#"+hiddenElementId).val() != ""){
			return true;
		}
		else if (emailRegex.test($('#'+elementId).val()) == true) {
			return true;
		}
		else {
			showErrorMobileAndWeb('Please select a valid user');
			return false;
		}
	}
	return true;
}

/**
 * function to validate region form
 */
function validateRegionForm() {
	isRegionValid = true;
	var isFocussed = false;
	
	var userSelectionType = $('#user-selection-info').data('user-selection-type');
	if(userSelectionType =="single"){
		if(!validateUserSelection('selected-user-txt','selected-userid-hidden')){
			isRegionValid = false;
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
		}
	}
	else {
		if(!validateUserEmailTextArea('selected-user-txt-area')){
			isRegionValid = false;
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
		}
		
	}
	if(!validateRegionName('region-name-txt')){
		isRegionValid = false;
		if(!isFocussed){
			$('#region-name-txt').focus();
			isFocussed=true;
		}
	}
	
	if(isRegionValid){
		hideError();
	}
	return isRegionValid;
}

/**
 * clear input fields within specified form/div
 */
function resetInputFields(elementId) {
	$("#"+elementId+" :input:not('.ignore-clear')").val("");
}

/**
 * js function for adding a region
 */
function addRegion(formId) {
	var url = "./addregion.do";
	showOverlay();
	callAjaxFormSubmit(url, addRegionCallBack, formId);
}

/**
 * call back function for add region
 * 
 * @param data
 */
function addRegionCallBack(data) {
	hideOverlay();
	displayMessage(data);
	resetInputFields("edit-region-form");
	fetchCompanyHierarchy();
}

/**
 * Method to fetch list of already existing users
 * @param searchKey
 * @param start
 * @param rows
 */
function getUsersList(searchKey,start,rows) {
	var url="./finduserbyemail.do?startIndex="+start+"&batchSize="+rows+"&searchKey="+searchKey;
	callAjaxGET(url, paintUsersList, true);
}

/**
 * Callback for getUsersList, populates the drop down with users list obtained
 * @param data
 */
function paintUsersList(data) {
	var usersList = $.parseJSON(data);
	var htmlData = "";
	if(usersList != null) {
		var len = usersList.length;
		if(len > 0) {
			$('#selected-userid-hidden').val("");
			$.each(usersList,function(i,user) {
				var displayName = user.firstName;
				if(user.lastName != undefined) {
					displayName = displayName + user.lastName;
				}
				htmlData = htmlData +'<div class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-user-options" data-userid="'+user.userId+'">'+displayName+'</div>';
			});
		}
	}
	if(htmlData != "") {
		$("#users-droplist").html(htmlData).slideDown(200);
	}
	else{	
		$("#users-droplist").slideUp(200);
		
	}
	
	// bind the click event of selector
	$(".hm-user-options").click(function() {
		$('#selected-user-txt').val($(this).html());
		$('#selected-userid-hidden').val($(this).data('userid'));
		$('#users-droplist').slideToggle(200);
	});
}

var isOfficeValid;

/**
 * Function to validate office name
 */
function validateOfficeName(elementId){
	if ($('#'+elementId).val() != "") {
		if (companyNameRegEx.test($('#'+elementId).val()) == true) {
			return true;
		}else {
			showErrorMobileAndWeb('Please enter a valid office name.');
			return false;
		}
	}else{
		showErrorMobileAndWeb('Please enter office name.');
		return false;
	}
}

/**
 * function to validate the region selector
 * 
 * @param hiddenElementId
 * @param textElementId
 * @returns {Boolean}
 */
function validateRegionSelector(hiddenElementId,textElementId) {
	var assignToType = $("#assign-to-txt").attr("data-assignto");
	if(assignToType == 'region'){
		if ($('#'+hiddenElementId).val() == "" || $('#'+textElementId).val() == "") {
			showErrorMobileAndWeb('Please select a region');
			return false;
		}
		return true;
	}
	return true;
	
}

/**
 * function to validate office form
 */
function validateOfficeForm() {
	isOfficeValid = true;
	var isFocussed = false;
	
	if(!validateRegionSelector('selected-region-txt', 'selected-region-id-hidden')) {
		isOfficeValid = false;
		if(!isFocussed){
			$('#selected-region-txt').focus();
			isFocussed=true;
		}
	}
	if(!validateAddress1('office-address-txt')){
		isOfficeValid = false;
		if(!isFocussed){
			$('#office-address-txt').focus();
			isFocussed=true;
		}
	}
	
	var userSelectionType = $('#user-selection-info').data('user-selection-type');
	if(userSelectionType =="single"){
		if(!validateUserSelection('selected-user-txt','selected-userid-hidden')){
			isOfficeValid = false;
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
		}
	}
	else {
		if(!validateUserEmailTextArea('selected-user-txt-area')){
			isOfficeValid = false;
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
		}		
	}
	if(!validateOfficeName('office-name-txt')){
		isOfficeValid = false;
		if(!isFocussed){
			$('#office-name-txt').focus();
			isFocussed=true;
		}
	}
	
	if(isOfficeValid){
		hideError();
	}
	return isOfficeValid;
}

/**
 * js function for adding a branch
 */
function addOffice(formId) {
	var url = "./addbranch.do";
	showOverlay();
	callAjaxFormSubmit(url, addOfficeCallBack, formId);
}

/**
 * call back function for add branch
 * 
 * @param data
 */
function addOfficeCallBack(data) {
	hideOverlay();
	displayMessage(data);
	resetInputFields("edit-office-form");
	fetchCompanyHierarchy();
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
	var searchResult = $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		console.log("searchResult is "+searchResult);
		if(len > 0) {
			$.each(searchResult,function(i,region) {
					htmlData = htmlData +'<div data-regionId="'+region.regionId+'" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-region-option">'+region.regionName+'</div>';
			});
		}
		if(htmlData != ""){
			$("#regions-droplist").html(htmlData).slideDown(200);
			// bind the click event of selector
			$('.hm-region-option').click(function(e) {
				e.stopPropagation();
				$('#selected-region-txt').val($(this).html());
				$('#selected-region-id-hidden').val($(this).data('regionid'));
				$('#regions-droplist').slideToggle(200);
			});	
			
			//bind the hover event
			$(".hm-dd-hover").hover(function() {
				$(".hm-region-option").removeClass("hm-dd-item-keys-selected");
			});
			$("#selected-region-txt").keydown(function(e){
				bindArrowKeysWithSelector(e, "selected-region-txt", "regions-droplist", populateRegionsSelector, "selected-region-id-hidden", "data-regionid");
			});			
		}
		else {
			$("#regions-droplist").html(htmlData).slideUp(200);	
		}
	}	
}

var isIndividualValid;

/**
 * function to validate the office selector
 * 
 * @param hiddenElementId
 * @param textElementId
 * @returns {Boolean}
 */
function validateOfficeSelector(hiddenElementId,textElementId) {
	var assignToType = $("#assign-to-txt").attr("data-assignto");
	if(assignToType == 'office'){
		if ($('#'+hiddenElementId).val() == "" || $('#'+textElementId).val() == "") {
			showErrorMobileAndWeb('Please select an office');
			return false;
		}
		return true;
	}
	return true;
}

/**
 * function to validate user selection in case of individual addition
 * @param elementId
 * @returns {Boolean}
 */
function validateIndividualSelection(elementId) {
	if ($('#'+elementId).val() == "") {
		showErrorMobileAndWeb('Please select a user or enter atleast one email address');
		return false;
	}
	return true;
}

/**
 * function to validate the individual form
 * @returns {Boolean}
 */
function validateIndividualForm() {
	isIndividualValid = true;
	var isFocussed = false;
	
	if(!validateRegionSelector('selected-region-txt', 'selected-region-id-hidden')) {
		isIndividualValid = false;
		if(!isFocussed){
			$('#selected-region-txt').focus();
			isFocussed=true;
		}
	}
	
	if(!validateOfficeSelector('selected-office-txt', 'selected-office-id-hidden')) {
		isIndividualValid = false;
		if(!isFocussed){
			$('#selected-office-txt').focus();
			isFocussed=true;
		}
	}
	
	var userSelectionType = $('#user-selection-info').data('user-selection-type');
	if(userSelectionType =="single"){
		if(!validateIndividualSelection('selected-user-txt')) {
			isIndividualValid = false;
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
		}
		if(!validateUserSelection('selected-user-txt','selected-userid-hidden')){
			isOfficeValid = false;
			if(!isFocussed){
				$('#selected-user-txt').focus();
				isFocussed=true;
			}
		}
	}
	else {
		if(!validateIndividualSelection('selected-user-txt-area')) {
			isIndividualValid = false;
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
		}
		if(!validateUserEmailTextArea('selected-user-txt-area')){
			isOfficeValid = false;
			if(!isFocussed){
				$('#selected-user-txt-area').focus();
				isFocussed=true;
			}
		}		
	}
	
	if(isIndividualValid){
		hideError();
	}
	return isIndividualValid;
}

function addIndividual(formId) {
	var url = "./addindividual.do";
	showOverlay();
	callAjaxFormSubmit(url, addIndividualCallBack, formId);
}

function addIndividualCallBack(data) {
	hideOverlay();
	displayMessage(data);
	resetInputFields("edit-individual-form");
	fetchCompanyHierarchy();
}

/**
 * Method to fetch offices(branches) from solr for populating offices selector
 * 
 * @param officePattern
 */
function populateOfficesSelector(officePattern) {
	console.log("Method populateOfficesSelector called for officePattern : "+officePattern);
	var url = "./searchbranches.do?branchPattern="+officePattern+"&start=0&rows=-1";
	callAjaxGET(url, populateOfficesSelectorCallBack, true);
}

/**
 * callback method for fetching offices(branches) from solr for populating office selector
 * @param data
 */
function populateOfficesSelectorCallBack(data) {
	var searchResult = $.parseJSON(data);
	if(searchResult != null) {
		var len = searchResult.length;
		var htmlData = "";
		console.log("searchResult is "+searchResult);
		if(len > 0) {
			$.each(searchResult,function(i,branch) {
					htmlData = htmlData +'<div data-regionid="'+branch.regionId+'" data-officeid="'+branch.branchId+'" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-office-option">'+branch.branchName+'</div>';
			});
		}
		if(htmlData != ""){
			$("#offices-droplist").html(htmlData).slideDown(200);	
			
			// bind the click event of selector
			$('.hm-office-option').click(function(e) {
				e.stopPropagation();
				$('#selected-office-txt').val($(this).html());
				$('#selected-region-id-hidden').val($(this).data('regionid'));
				$('#selected-office-id-hidden').val($(this).data('officeid'));
				$('#offices-droplist').slideToggle(200);
			});	
			
			//bind the hover event
			$(".hm-dd-hover").hover(function() {
				$(".hm-office-option").removeClass("hm-dd-item-keys-selected");
			});
		}
		else {
			$("#offices-droplist").html(htmlData).slideUp(200);	
		}
	}	
}

function bindArrowKeysWithSelector(e,textBoxId,dropListId,populatorFunction,hiddenFieldId,attrName) {
	console.log(e.which);
	if(e.which == 40) {
		var text = $("#"+textBoxId).val();
		if(text == undefined) {
			text = "";
		}
		if (!($("#"+dropListId).css("display") =="block")){
			delay(function() {
				populatorFunction(text);
			}, 500);
		}else {
			var current = $("#"+dropListId).find(".hm-dd-item-keys-selected");
			if(current.length > 0) {
				$(current).removeClass("hm-dd-item-keys-selected");
				$(current).next().addClass("hm-dd-item-keys-selected");
			}
			else {
				$("#"+dropListId +" :first-child").addClass("hm-dd-item-keys-selected");
			}
			$("#"+dropListId).show();
		}
		
	}	
	else if(e.which == 38){
		var current = $("#"+dropListId).find(".hm-dd-item-keys-selected");
		if(current.length > 0) {
			$(current).removeClass("hm-dd-item-keys-selected");
			$(current).prev().addClass("hm-dd-item-keys-selected");
		}else {
			$('#'+dropListId).slideUp(200);
		}
	}else if(e.which == 13) {
		var selectedItem = $("#"+dropListId).find(".hm-dd-item-keys-selected");
		if(selectedItem.length == 0) {
			selectedItem = $("#"+dropListId+" :first-child");
		}
		$('#'+textBoxId).val($(selectedItem).html());
		$('#'+hiddenFieldId).val($(selectedItem).attr(attrName));
		$('#'+dropListId).slideToggle(200);	
	}
}