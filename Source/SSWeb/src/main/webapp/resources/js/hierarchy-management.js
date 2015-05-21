/**
 * js functions for hierarchy and user management
 */
var usersStartIndex = 0;
var numOfRows = 10;
/*in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile*/

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
function fetchCompleteHierarchy() {
	var profileName = $("#profile-name").val();
	fetchCompanyHierarchy("companyProfileName", profileName);
}
/**
 * Method to change the arrow in tabs according to the form displayed 
 * @param spanId
 */
function changeTabArrow(spanId) {
	$('.bd-hdr-span').removeClass('bd-hdr-active');
    $('.bd-hdr-span').removeClass('bd-hdr-active-arr');
    $("#"+spanId).addClass('bd-hdr-active');
    $("#"+spanId).addClass('bd-hdr-active-arr');
}

/**
 * function to get the edit form based on tab value 
 */
function getEditSectionFormByTab(tabValue) {
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
 * Method to get the edit section form based on the account type and highest role of user
 */
function getEditSection() {
	var accountType = $("#account-type").val();
	var highestRole = $("#highest-role").val();
	switch(accountType){
    case 'Enterprise':
    	if(highestRole == 1) {
    		getRegionEditPage();
    	}
    	else if(highestRole == 2) {
    		getOfficeEditPage();
    	}
    	else if(highestRole == 3){
    		getIndividualEditPage();
    	}
    	else {
    		showErrorMobileAndWeb("Sorry you are not authorized to build hierarchy");
    	}
        break;
    case 'Company': 
    	if(highestRole == 1 || highestRole == 2) {
    		getOfficeEditPage();
    	}
    	else if(highestRole == 3){
    		getIndividualEditPage();
    	}
    	else {
    		showErrorMobileAndWeb("Sorry you are not authorized to build hierarchy");
    	}
        break;
    case 'Team': 
    	getIndividualEditPage();
        break;
    default: 
    	showErrorMobileAndWeb("Sorry you are not authorized to build hierarchy");
        break;
	}
}

/**
 * function to get the region edit page
 */
function getRegionEditPage(){
	var url = "./getregioneditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-region-tab");
}

/**
 * function to get the office edit page
 */
function getOfficeEditPage(){
	var url = "./getofficeeditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-office-tab");
}
/**
 * function to get the individual edit page
 */
function getIndividualEditPage(){
	var url = "./getindividualeditpage.do";
	callAjaxGET(url, paintEditSection, true);
	changeTabArrow("hr-individual-tab");
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
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);
	
	bindUserSelector();
	
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

	bindAdminCheckBoxClick();
	
	bindSingleMultipleSelection();
	bindAssignToSelectorClick();
	
	bindRegionSelectorEvents();
	
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
	
	bindOfficeSelectorEvents();
	
	$("#btn-individual-save").click(function(e){
		if(validateIndividualForm()){
			addIndividual("edit-individual-form");
		}
	});
}

function bindSingleMultipleSelection() {
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
}

function bindUserSelector() {
	$("#selected-user-txt").click(function() {
		getUsersList("",usersStartIndex,numOfRows);
	});
	$("#selected-user-txt").keydown(function(e) {
		bindArrowKeysWithSelector(e, "selected-user-txt", "users-droplist", getUsersList, "selected-userid-hidden", "data-userid");
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
}

/**
 * binds the click and keyup of region selector
 */
function bindRegionSelectorEvents(){
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
}

/**
 * binds the click and keyup of office selector
 */
function bindOfficeSelectorEvents(){
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
}

/**
 * binds the click of assign to selector
 */
function bindAssignToSelectorClick(){
	$('#assign-to-selector').click(function(e) {
		e.stopPropagation();
		$("#assign-to-droplist").slideToggle(200);
	});
	
	$('.hm-assignto-options').click(function(e) {
		e.stopPropagation();
		var assignToOption = $(this).attr('data-assign-to-option');
		$("#assign-to-txt").val($(this).html());
		$("#assign-to-txt").attr("data-assignto",assignToOption);
		
		showSelectorsByAssignToOption(assignToOption);
		$("#assign-to-droplist").slideToggle(200);
	});
}

/**
 * binds the check and uncheck of admin privileges checkbox
 */
function bindAdminCheckBoxClick(){
	$('.bd-check-img').unbind('click');
	$('.bd-check-img').click(function(){
		/* $(this).toggleClass('bd-check-img-checked');*/
		/**
		 * If class is "bd-check-img-checked", check box is unchecked ,
		 * hence setting the hidden value as false
		 */
		 if($(this).hasClass('bd-check-img-checked') ){
			$(this).removeClass('bd-check-img-checked');
			$(this).next("#is-admin-chk").val("true");
		 }
		 else {
			$(this).addClass('bd-check-img-checked');
			$(this).next("#is-admin-chk").val("false");
		 }
	});
}

/**
 * Method to show/hide the other selectors based on the assign to option selected
 * @param assignToOption
 */
function showSelectorsByAssignToOption(assignToOption) {
	console.log("selector----------"+assignToOption);
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
			var emailId = emailIdsArray[i].trim();
			if(emailRegex.test(emailId) == true){
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
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
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
	$('#region-state-city-row').hide();
	resetInputFields("edit-region-form");
	fetchCompleteHierarchy();
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
					displayName = displayName +" "+ user.lastName;
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
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
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
	$('#office-state-city-row').hide();
	resetInputFields("edit-office-form");
	fetchCompleteHierarchy();
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
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, addIndividualCallBack, formId);
}

function addIndividualCallBack(data) {
	hideOverlay();
	displayMessage(data);
	resetInputFields("edit-individual-form");
	fetchCompleteHierarchy();
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

function showViewHierarchyPage() {
	closeMoblieScreenMenu();
	var url = "./viewhierarchy.do";
	callAjaxGET(url, viewHierarchyCallBack, true);
}

function viewHierarchyCallBack(data) {
	$("#main-content").html(data);
}

function fetchHierarchyViewBranches(regionId) {
	var url = "./fetchhierarchyviewbranches.do?regionId="+regionId;
	callAjaxGET(url, function(data) {
		paintHierarchyViewBranches(data,regionId);
	}, true);
}

function paintHierarchyViewBranches(data,regionId) {
	$("#td-region-edit-"+regionId).parent(".tr-region-edit").after(data);
	$("#tr-region-"+regionId).slideDown(200);
	$(".tr-region-edit").slideUp(200);
	bindUserEditClicks();
	bindBranchListClicks();
}

function bindBranchListClicks(){
	$(".branch-edit-icn").unbind('click');
	$(".branch-edit-icn").click(function(e){
		e.stopPropagation();
		var branchId = $(this).attr("data-branchid");
		if($(this).attr('clicked') == "false"){
			showBranchEdit(branchId);
			$(this).attr('clicked','true');
		}
		else {
			hideBranchEdit(branchId);
			$(this).attr('clicked','false');
		}		
	});
	$(".branch-row").unbind('click');
	$(".branch-row").click(function(e){
		e.stopPropagation();
		var branchId = $(this).attr("data-branchid");
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			fetchUsersForBranch(branchId,regionId);
			 $(this).attr('clicked','true');
		}
		else {
			$('.user-row-'+branchId).html("").hide(); 
            $(this).attr('clicked','false');
		}
	});
	$(".branch-del-icn").unbind('click');
	$(".branch-del-icn").click(function(e){
		var branchId = $(this).attr("data-branchid");
		deleteBranchPopup(branchId);
	});
}

function fetchHierarchyViewList() {
	var url = "./fetchhierarchyviewlist.do";
	callAjaxGET(url, paintHierarchyViewList, true);
}
function paintHierarchyViewList(data) {
	$("#hierarchy-list-header").siblings().remove();
	$("#hierarchy-list-header").after(data);
	bindRegionListClicks();
    $('.v-tbl-icn').click(function(e){
        e.stopPropagation();
    });
    bindBranchListClicks();
    bindUserEditClicks();
}

function bindRegionListClicks() {
	$(".region-row").click(function(e){
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			fetchHierarchyViewBranches(regionId);
			 $(this).attr('clicked','true');
		}
		else {
			$("tr[class*='sel-r"+regionId+"'").html("").hide();
            $(this).attr('clicked','false');
		}
	});
	$(".region-edit-icn").click(function(e){
		e.stopPropagation();
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			showRegionEdit(regionId);
			$(this).attr('clicked','true');
		}
		else {
			hideRegionEdit(regionId);
			$(this).attr('clicked','false');
		}		
	});
	$(".region-del-icn").unbind('click');
	$(".region-del-icn").click(function(e){
		var regionId = $(this).attr("data-regionid");
		deleteRegionPopup(regionId);
	});
}

function showRegionEdit(regionId) {
	var url = "./getregioneditpage.do?regionId="+regionId;
	callAjaxGET(url, function(data){
		showRegionEditCallBack(data, regionId);
	}, true);
}
function showRegionEditCallBack(data,regionId) {
	$(".td-region-edit").html("").hide();
	$(".tr-region-edit").hide();
	$("#td-region-edit-"+regionId).parent(".tr-region-edit").slideDown(200);
	$("#td-region-edit-"+regionId).html(data).slideDown(200);
	bindSingleMultipleSelection();
	bindUserSelector();
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);
	bindAssignToSelectorClick();
	$("#btn-region-update").click(function(e) {
		var regionId = $(this).attr("data-regionid");
		if(validateRegionForm()){
			updateRegion("edit-region-form",regionId);
		}
	});
	
}

function hideRegionEdit(regionId) {
	$(".td-region-edit").html("").hide();
	$("#td-region-edit-"+regionId).hide();
	$(".tr-region-edit").hide();
}

function showBranchEdit(branchId) {
	var url = "./getofficeeditpage.do?branchId="+branchId;
	callAjaxGET(url, function(data){
		showBranchEditCallBack(data, branchId);
	}, true);
}

function showBranchEditCallBack(data,branchId) {
	$("#td-branch-edit-"+branchId).parent(".tr-branch-edit").slideDown(200);
	$("#td-branch-edit-"+branchId).html(data).slideDown(200);
	bindSingleMultipleSelection();
	bindUserSelector();
	bindRegionSelectorEvents();
	var assignToOption = $("#assign-to-txt").attr('data-assignto');
	showSelectorsByAssignToOption(assignToOption);
	bindAssignToSelectorClick();
	$("#btn-office-update").click(function(e){
		updateBranch("edit-office-form", branchId);
	});
}

function hideBranchEdit(branchId) {
	$("#td-branch-edit-"+branchId).slideUp(200);
	$("#td-branch-edit-"+branchId).parent(".tr-branch-edit").hide();
}

function fetchUsersForBranch(branchId,regionId) {
	var url="./fetchbranchusers.do?branchId="+branchId+"&regionId="+regionId;
	callAjaxGET(url, function(data) {
		paintUsersFromBranch(data,branchId);
	}, true);
}

function paintUsersFromBranch(data,branchId,regionId) {
	$("#td-branch-edit-"+branchId).parent(".tr-branch-edit").after(data);
	$("#tr-branch-"+branchId).slideDown(200);
	$(".tr-branch-edit").slideUp(200);
	bindUserEditClicks();
}

function bindUserEditClicks() {
	$(".user-edit-icn").unbind('click');
	$('.user-edit-icn').click(function(e){
		e.stopPropagation();
		if($(this).attr('clicked') == "false") {
			// make an ajax call and fetch the details of the user
			var userId = $(this).attr('data-userid');
			$(".user-assignment-edit-div").html("");
			$(".user-edit-row").slideUp();
			getUserAssignments(userId);
			$(this).parent().parent().parent().next('.user-edit-row').slideDown(200);
			$(this).attr('clicked','true');
	    }else {
			$(this).parent().parent().parent().next('.user-edit-row').slideUp(200);
			$(".user-assignment-edit-div").html("");
			$(".user-edit-row").slideUp();
			$(this).attr('clicked','false');
	    }
	});
	$(".user-del-icn").unbind('click');
	$(".user-del-icn").click(function(e){
		e.stopPropagation();
		var userId = $(this).attr("data-userid");
		confirmDeleteUser(userId);
	});
}

function updateRegion(formId,regionId) {
	var url = "./updateregion.do";
	showOverlay();
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, function(data){
		updateRegionCallBack(data, regionId);
	}, formId);
}

function updateRegionCallBack(data,regionId) {
	hideOverlay();
	displayMessage(data);
	hideRegionEdit(regionId);
	fetchHierarchyViewList();
}

function updateBranch(formId,branchId) {
	var url = "./updatebranch.do";
	var selectedType = $('.bd-cust-rad-img-checked').attr("data-type");
	$('input[name="userSelectionType"]').val(selectedType);
	callAjaxFormSubmit(url, function(data){
		updateBranchCallBack(data,branchId);
	}, formId);
}

function updateBranchCallBack(data,branchId) {
	hideOverlay();
	displayMessage(data);
	hideBranchEdit(branchId);
	fetchHierarchyViewList();
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
	var success = "Selected Office could be deleted";
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

//Pop-up Overlay modifications
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
 * Function to delete a region
 * 
 * @param branchId
 */
function deleteRegion(regionId) {
	var url = "./deactivateregion.do?regionId=" + regionId;
	callAjaxPOST(url, function(data){
		deleteRegionCallBack(data,regionId);
	}, true);
}

/**
 * Call back function for deleting a region
 * 
 * @param data
 */
function deleteRegionCallBack(data,regionId) {
	displayMessage(data);
	$("#tr-region-"+regionId).hide();
	$("#tr-region-"+regionId).next(".tr-region-edit").hide();
}

/**
 * Function to delete a branch
 * 
 * @param branchId
 */
function deleteBranch(branchId) {
	var url = "./deactivatebranch.do?branchId=" + branchId;
	callAjaxPOST(url, function(data){
		deleteBranchCallBack(data,branchId);
	}, true);
}

/**
 * Call back function for deleting a branch
 * 
 * @param data
 */
function deleteBranchCallBack(data,branchId) {
	displayMessage(data);
	$("#tr-branch-row-"+branchId).hide();
	$("#tr-branch-row-"+branchId).next(".tr-branch-edit").hide();
}

function resendVerificationMail(){
	$.ajax({
		url : "./sendverificationmail.do",
		type : "GET",
		dataType : "text",
		success : function(data) {
			if (data.errCode == undefined){
				$('#overlay-toast').html(data);
				showToast();
				hideError();
				hideInfo();
			}
			else {
				$('#overlay-toast').html(data);
				showToast();
				hideError();
				hideInfo();
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}