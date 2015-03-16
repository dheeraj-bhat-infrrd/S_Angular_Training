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
	$("#bd-edit-form-section").html(data);
	
	/**
	 * bind the click events
	 */
	$("#selected-user-txt").click(function() {
		getUsersList("",usersStartIndex,numOfRows);
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

	$("#selected-user-txt").keyup(function() {
		var text = $(this).val();
		usersStartIndex = 0;	
		if (text.length > 1) {
			delay(function() {
				getUsersList(text,usersStartIndex,numOfRows);
			}, 500);
		}
		else {
			delay(function() {
				getUsersList("",usersStartIndex,numOfRows);
			}, 500);
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
        }else if($(this).data('type') == "multiple"){
            $('#bd-single').hide();
            $('#bd-multiple').show();
            $('#selected-userid-hidden').val("");
        }
        $('#user-selection-info').attr('data-user-selection-type',$(this).data('type'));
    });
	
	$('#assign-to-selector').click(function(e) {
		e.stopPropagation();
		$("#assign-to-droplist").slideToggle(200);
	});
	
	$('.hm-assignto-options').click(function(e) {
		e.stopPropagation();
		var assignToOption = $(this).data('assign-to-option');
		$("#assign-to-txt").val($(this).html());
		$("#assign-to-txt").attr("data-assignto",assignToOption);
		
		switch(assignToOption) {
			case 'company':
				disableRegionSelector();
				disableOfficeSelector();
				break;
			case 'region':
				$("#selected-region-txt").prop("disabled",false);
				break;
			case 'branch':
				$("#selected-region-txt").prop("disabled",false);
				$("#selected-office-txt").prop("disabled",false);
				break;
			default:
				$("#selected-region-txt").prop("disabled",false);
				$("#selected-office-txt").prop("disabled",false);
		}
		$("#assign-to-droplist").slideToggle(200);
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
}

function disableRegionSelector(){
	$("#selected-region-txt").prop("disabled",true);
	$("#selected-region-txt").val("");
	$('#selected-region-id-hidden').val("");
}

function disableOfficeSelector(){
	$("#selected-office-txt").prop("disabled",true);
	$("#selected-office-txt").val("");
	$('#selected-office-id-hidden').val("");
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
	
	if(!validateRegionName('region-name-txt')){
		isRegionValid = false;
		if(!isFocussed){
			$('#region-name-txt').focus();
			isFocussed=true;
		}
	}
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
	
	if(isRegionValid){
		hideError();
	}
	return isRegionValid;
}

/**
 * clear input fields within specified form/div
 */
function resetInputFields(elementId) {
	$("#"+elementId+" :input").val("");
}

/**
 * js function for adding a region
 */
function addRegion(formId) {
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
	resetInputFields("edit-region-form");
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
			$.each(usersList,function(i,user) {
				htmlData = htmlData +'<div class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-user-options" data-userid="'+user.userId+'">'+user.firstName+" "+user.lastName+'</div>';
			});
		}
	}
	if(htmlData == "") {
		$("#users-droplist").slideUp(200);
	}
	else{	
		$("#users-droplist").html(htmlData).slideToggle(200);
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
	var assignToType = $("#assign-to-txt").data("assignto");
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
	
	if(!validateOfficeName('office-name-txt')){
		isOfficeValid = false;
		if(!isFocussed){
			$('#office-name-txt').focus();
			isFocussed=true;
		}
	}
	if(!validateRegionSelector('selected-region-txt', 'selected-region-id-hidden')) {
		isOfficeValid = false;
		if(!isFocussed){
			$('#selected-region-txt').focus();
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
	callAjaxFormSubmit(url, addOfficeCallBack, formId);
}

/**
 * call back function for add branch
 * 
 * @param data
 */
function addOfficeCallBack(data) {
	displayMessage(data);
	resetInputFields("edit-office-form");
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
					htmlData = htmlData +'<div data-regionId="'+region.regionId+'" class="bd-frm-rt-dd-item dd-com-item hm-dd-hover hm-region-option">'+region.regionName+'</div>';
			});
			
			$("#regions-droplist").html(htmlData).slideToggle(200);
			
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
			
		}
	}	
}