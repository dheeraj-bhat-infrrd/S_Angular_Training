function fetchCompanyStatistics(){
	var url = './fetchcompanystatistics.do'
	$('#serv-det-loader').show();
	$('#ss-data-loader').show();
	var loaders = '#serv-det-loader,#ss-data-loader';
	callAjaxGETForAdminDashboard(url, paintCompanyStatistics, loaders);	
}

function paintCompanyStatistics(data){
	var companyStatistics = JSON.parse(data);
	
	//customer success info
	insertValueById('#ad-company-id',companyStatistics.companyId);
	
	//service detail
	insertValueById('#ad-totaL-users',companyStatistics.userCount);
	insertValueById('#ad-verified-user',companyStatistics.verifiedUserCount);
	insertValueById('#ad-verified-perc',companyStatistics.verifiedPercent);
	insertValueById('#ad-com-survey-count',companyStatistics.completedSurveyCountAllTime);
	insertValueById('#ad-com-survey-count-90-days',companyStatistics.completedSurveyCount90Days);
	insertValueById('#ad-com-survey-count-this-year',companyStatistics.completedSurveyCountThisYear);
	insertValueById('#ad-com-survey-count-this-month',companyStatistics.completedSurveyCountThisMonth);
	insertValueById('#ad-regions-count',companyStatistics.regionCount);
	insertValueById('#ad-branch-count',companyStatistics.branchCount);
	
	//Social Survey Data
	insertValueById('#ad-reg-verified-gmb',companyStatistics.regionVerifiedGmb);
	insertValueById('#ad-reg-missing-gmb',companyStatistics.regionMissingGmb);
	insertValueById('#ad-bra-verified-gmb',companyStatistics.branchVerifiedGmb);
	insertValueById('#ad-bra-missing-gmb',companyStatistics.branchMissingGmb);
	insertValueById('#ad-reg-gmb-perc',companyStatistics.regionGmbPercent);
	insertValueById('#ad-bra-gmb-perc',companyStatistics.branchGmbPercent);
	
	insertValueById('#ad-mismatches',companyStatistics.mismatchCount);
	insertValueById('#ad-mismatches-90-days',companyStatistics.mismatchCount90Days);
	insertValueById('#ad-missing-photos-users',companyStatistics.missingPhotoCountForUsers);
	insertValueById('#ad-missing-photos-users-perc',companyStatistics.missingPhotoPercentForUsers);
	insertValueById('#ad-fb-connects',companyStatistics.facebookConnectionCount);
	insertValueById('#ad-fb-percentage',companyStatistics.facebookPercent);
	insertValueById('#ad-twitter-connects',companyStatistics.twitterConnectionCount);
	insertValueById('#ad-twitter-perc',companyStatistics.twitterPercent);
	insertValueById('#ad-linked-in-connects',companyStatistics.linkedinConnectionCount);
	insertValueById('#ad-linked-in-perc',companyStatistics.linkedInPercent);
}

function fetchCustomerSuccessInfo(){
	var url = './fetchcustomersuccessinfo.do'
	$('#cust-suc-loader').show();
	$('#poc-loader').show();
	var loaders = '#cust-suc-loader,#poc-loader';
	callAjaxGETForAdminDashboard(url, paintCustomerSuccessInfo, loaders);	
}

function paintCustomerSuccessInfo(data){
	var customerSuccessInfo = JSON.parse(data);
	
	//left column
	$('#ad-customer-success-name').attr('data-customersuccessid',customerSuccessInfo.customerSuccessId);
	$('#ad-customer-success-name').attr('data-sel-customersuccessid',customerSuccessInfo.customerSuccessId);
	$('#ad-customer-success-name').html(customerSuccessInfo.customerSuccessName);
	
	insertValueById('#ad-company-name',customerSuccessInfo.companyName);
	insertValueById('#ad-account-status',customerSuccessInfo.accountStatus);
	insertValueById('#ad-tmc-client',customerSuccessInfo.tmcClient);
	insertValueById('#ad-survey-data-source',customerSuccessInfo.surveyDataSource);
	insertValueById('#ad-company-dba',customerSuccessInfo.dbaForCompany);
	
	var gapAnalysisDateStr = '';
	if(customerSuccessInfo.gapAnalysisDate != null && customerSuccessInfo.gapAnalysisDate != 0 && customerSuccessInfo.gapAnalysisDate != undefined ){
		var gapAnalysisDate = getDateObject(customerSuccessInfo.gapAnalysisDate);
		gapAnalysisDateStr = gapAnalysisDate.month + ' ' + gapAnalysisDate.date + ', ' + gapAnalysisDate.year;
	}	
	
	insertValueById('#ad-gap-analysis-date',gapAnalysisDateStr);
	if(customerSuccessInfo.gapAnalysisDate != 0){
		$("#ad-gap-analysis-date").datepicker("setDate", new Date(customerSuccessInfo.gapAnalysisDate));
	}
	
	insertValueById('#ad-services-sold',customerSuccessInfo.servicesSold);
	insertValueById('#ad-tag',customerSuccessInfo.tag);
	
	//right column
	insertValueById('#ad-customer-success-owner',customerSuccessInfo.customerSuccessOwner);
	insertValueById('#ad-rvp',customerSuccessInfo.rvp);
	
	closedDateStr = '';
	if(customerSuccessInfo.closedDate != null && customerSuccessInfo.closedDate != 0 && customerSuccessInfo.closedDate != undefined ){
		var closedDate = getDateObject(customerSuccessInfo.closedDate);
		closedDateStr = closedDate.month + ' ' + closedDate.date + ', ' + closedDate.year;
	}
	insertValueById('#ad-closed-date',closedDateStr);
	if(customerSuccessInfo.closedDate != 0){
		$("#ad-closed-date").datepicker("setDate", new Date(customerSuccessInfo.closedDate));
	}
	
	var createdOnStr = '';
	var createdByStr = ((customerSuccessInfo.createdBy == null || customerSuccessInfo.createdBy == undefined || customerSuccessInfo.createdBy == 'null')?'':(customerSuccessInfo.createdBy + '<br/>'));
	if(customerSuccessInfo.createdOn != null && customerSuccessInfo.createdOn != 0 && customerSuccessInfo.createdOn != undefined ){
		var createdOn = getDateObject(customerSuccessInfo.createdOn);
		createdOnStr = createdOn.day + ', ' + createdOn.date + ' ' + createdOn.month + ' ' + createdOn.year + ' ' + createdOn.time;
		createdByStr+= createdOnStr; 
	}
	insertValueById('#ad-created-by',createdByStr);
	
	var modifiedOnStr = '';
	var modifiedByStr = ((customerSuccessInfo.modifiedBy == null || customerSuccessInfo.modifiedBy == undefined || customerSuccessInfo.modifiedBy == 'null')?'':(customerSuccessInfo.modifiedBy + '<br/>'));
	if(customerSuccessInfo.modifiedOn != null && customerSuccessInfo.modifiedOn != 0 && customerSuccessInfo.modifiedOn != undefined ){
		var modifiedOn = getDateObject(customerSuccessInfo.modifiedOn);
		modifiedOnStr = modifiedOn.day + ', ' + modifiedOn.date + ' ' + modifiedOn.month + ' ' + modifiedOn.year + ' ' + modifiedOn.time;
		modifiedByStr+= modifiedOnStr; 
	}
	insertValueById('#ad-modified-by',modifiedByStr);
	insertValueById('#ad-potential',customerSuccessInfo.potential);
	insertValueById('#ad-customer-settings',customerSuccessInfo.customerSettings);
	insertValueById('#ad-transfer-review-policy',customerSuccessInfo.transferReviewPolicy);
	insertValueById('#ad-realtor-surveys',customerSuccessInfo.realtorSurveys);
	insertValueById('#ad-happy-workflow-setting',customerSuccessInfo.happyWorkflowSettings);
	insertValueById('#ad-profile-completion',customerSuccessInfo.profileCompletion);
	
	//service detail
	insertValueById('#ad-company-gmb',customerSuccessInfo.companyGmb);
	insertValueById('#ad-pixel-campaign',customerSuccessInfo.pixelCampaign);
	insertValueById('#ad-company-gmb',customerSuccessInfo.companyGmb);
	
	//poc
	insertValueById('#ad-primary-poc',customerSuccessInfo.primaryPoc);
	insertValueById('#ad-email',customerSuccessInfo.email);
	insertValueById('#ad-secondary-email',customerSuccessInfo.secondaryEmail);
	insertValueById('#ad-phone',customerSuccessInfo.phone);
	
	lastConversationDateStr = '';
	if(customerSuccessInfo.lastConversationDate != null && customerSuccessInfo.lastConversationDate != 0 && customerSuccessInfo.lastConversationDate != undefined ){
		var lastConversationDate = getDateObject(customerSuccessInfo.lastConversationDate);
		lastConversationDateStr = lastConversationDate.month + ' ' + lastConversationDate.date + ', ' + lastConversationDate.year;
	}
	insertValueById('#ad-last-conv-date',lastConversationDateStr);
	if(customerSuccessInfo.lastConversationDate != 0){
		$("#ad-last-conv-date").datepicker("setDate", new Date(customerSuccessInfo.lastConversationDate));
	}
	
	insertValueById('#ad-poc-2',customerSuccessInfo.poc2);
	insertValueById('#ad-email-poc-2',customerSuccessInfo.emailPoc2);
	insertValueById('#ad-phone-poc-2',customerSuccessInfo.phonePoc2);
}

function getSocialSurveyAdmins(){
	var url = './getsocialsurveyadmins.do'
	callAjaxGET(url, paintSocialSurveyAdmins, true);	
}

function paintSocialSurveyAdmins(data){
	var socialSurveyAdmins = JSON.parse(data);
	
	$('#ad-customer-success-name-dropdown').html('');
	for(var i=0; i<socialSurveyAdmins.length; i++){
		var adminItem = '<div class="ad-customer-success-name-item" data-customersuccessid="'+ socialSurveyAdmins[i].userId +'">'+ socialSurveyAdmins[i].fullName +'</div>'
		$('#ad-customer-success-name-dropdown').append(adminItem);
	}
	
	$('.cust-suc-info-val-dropdown').click(function(e){
		e.stopImmediatePropagation();
		e.preventDefault();
		e.stopPropagation();
		
		var disabled = $(this).attr('data-disabled');
		
		if(disabled == true || disabled == 'true'){
			return;
		}
		
		$('#ad-customer-success-name-dropdown').slideToggle(200);
	});
	
	$('.ad-customer-success-name-item').click(function(e){
		e.stopImmediatePropagation();
		e.preventDefault();
		e.stopPropagation();
		
		var customerSuccessId = $(this).attr('data-customersuccessid');
		var customerSuccessName = $(this).html();
		$('#ad-customer-success-name').attr('data-sel-customersuccessid', customerSuccessId);
		$('#ad-customer-success-name').html(customerSuccessName);
				
		var keyToBeUpdated = $('#ad-customer-success-name-dropdown').attr('data-columnname');
		var value = customerSuccessId+','+customerSuccessName;
		
		$('#ad-customer-success-name-dropdown').attr('data-disabled',true);
		$('#ad-customer-success-name-dropdown').slideToggle(200);
		
		updateCustomerInformation(keyToBeUpdated, value, $('#ad-customer-success-name-dropdown'));
		
		
	});
}

function fetchNotes(){
	var url = './fetchnotes.do'
	callAjaxGET(url, paintSocialSurveyAdmins, true);	
}

function paintNotes(data){
	console.log(data);
}

function updateCustomerInformation(keyToBeUpdated, value, element){
	var url = "./updatecustomerinformation.do"

	var setValue = element.attr('data-value');
	
	/*if(value == null || value == undefined || value == '' || value.length == 0){
		if(element.attr('id') == 'ad-customer-success-name-dropdown'){
			element.attr('data-disabled',false);
		}else if(element != undefined){
			element.val(setValue);
			element.prop('disabled', false);
			element.parent().removeClass('cust-suc-info-val-fixed');
			element.parent().addClass('cust-suc-info-val-editable');
		}
		
		$('#overlay-toast').html("Cannot update blank value.");
		showToast();
		return;
	}*/
	
	if(element.attr('id') == 'ad-email' || element.attr('id') == 'ad-email-poc-2' || element.attr('id') == 'ad-secondary-email'){
		if(!validateEmailId(element.attr('id'))){
			element.val(setValue);
			element.prop('disabled', false);
			element.parent().removeClass('cust-suc-info-val-fixed');
			element.parent().addClass('cust-suc-info-val-editable');
			$('#overlay-toast').html("Please Enter a valid email address.");
			showToast();
			return;
		}
	}
	
	if(element.attr('id') == 'ad-phone' || element.attr('id') == 'ad-phone-poc-2'){
		if(!validatePhoneNumber(element.attr('id'))){
			element.val(setValue);
			element.prop('disabled', false);
			element.parent().removeClass('cust-suc-info-val-fixed');
			element.parent().addClass('cust-suc-info-val-editable');
			$('#overlay-toast').html("Please Enter a valid phone number");
			showToast();
			return;
		}
	}
	
	var payload = {
			"keyToBeUpdated" : keyToBeUpdated,
			"value" : value
	};
	
	$.ajax({
		async : false,
		url : url,
		type : "POST",
		data: payload,
		success : function(data) {
			$('#overlay-toast').html(data);
			showToast();
		},
		complete : function(){
			if(element.attr('id') == 'ad-customer-success-name-dropdown'){
				element.attr('data-disabled',false);
			}else if(element != undefined){
				element.attr('data-value',payload.value);
				element.prop('disabled', false);
				element.parent().removeClass('cust-suc-info-val-fixed');
				element.parent().addClass('cust-suc-info-val-editable');
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			
			if(element.attr('id') == 'ad-customer-success-name-dropdown'){
				element.attr('data-disabled',false);
			}else if(element != undefined){
				element.val(setValue);
				element.prop('disabled', false);
				element.parent().removeClass('cust-suc-info-val-fixed');
				element.parent().addClass('cust-suc-info-val-editable');
			}
			
			$('#overlay-toast').html("Unable to update information.");
			showToast();
		}
	});
}

$(document).on('blur','.cust-suc-info-inp',function(e){
	
	if($('.datepicker').is(':visible')){return;}
	
	var keyToBeUpdated = $(this).attr('data-columnname');
	var value = $(this).val();
	
	if(keyToBeUpdated == 'closedDate'){
		if($('#ad-closed-date').val() == ''){
			value = $('#ad-closed-date').val();
		}else{		
			value = $('#ad-closed-date-ts').val();
		}
	}else if(keyToBeUpdated == 'lastConversationDate'){
		if($('#ad-last-conv-date').val() == ''){
			value = $('#ad-last-conv-date').val();
		}else{
			value = $('#ad-last-conv-date-ts').val();
		}		
	}else if(keyToBeUpdated == 'gapAnalysisDate'){
		if($('#ad-gap-analysis-date').val() == ''){
			value = $('#ad-gap-analysis-date').val();
		}else{
			value = $('#ad-gap-analysis-date-ts').val();
		}		
	}
		
	$(this).prop('disabled', true);
	$(this).parent().addClass('cust-suc-info-val-fixed');
	$(this).parent().removeClass('cust-suc-info-val-editable');
	
	updateCustomerInformation(keyToBeUpdated, value , $(this));
});

function insertValueById(id,value){
	var isEditable = $(id).data('editable');
	
	if(value == null || value == undefined || value =='null'){
		value='';
	}
	
	if(isEditable){
		$(id).val(value);
		$(id).attr('data-value',value);
	}else{
		$(id).html(value);
	}
}

function getDateObject(timestamp){
	var newDate = new Date(timestamp);
	
	var dateObject = {};
	
	dateObject.date = ("0" + newDate.getDate()).slice(-2);
	dateObject.month = getMonthName(newDate.getMonth());
	dateObject.year = newDate.getFullYear();
	dateObject.day = getDayName(newDate.getDay());
	
	dateObject.time = newDate.toLocaleTimeString(undefined, {
					    hour: '2-digit',
					    minute: '2-digit'
					  });
		
	return dateObject;
}

function getMonthName(monthNo){
	switch(monthNo){
		case 0: return 'Jan';
		case 1: return 'Feb';
		case 2: return 'Mar';
		case 3: return 'Apr';
		case 4: return 'May';
		case 5: return 'Jun';
		case 6: return 'Jul';
		case 7: return 'Aug';
		case 8: return 'Sep';
		case 9: return 'Oct';
		case 10: return 'Nov';
		case 11: return 'Dec';
		default: return '';
	}
}

function getDayName(monthNo){
	switch(monthNo){
		case 0: return 'Sun';
		case 1: return 'Mon';
		case 2: return 'Tue';
		case 3: return 'Wed';
		case 4: return 'Thu';
		case 5: return 'Fri';
		case 6: return 'Sat';
		default: return '';
	}
}

function callAjaxGETForAdminDashboard(url, callBackFunction, loaders) {
	var loaderArray = loaders.split(',');
	
	$.ajax({
		url : url,
		type : "GET",
		dataType : "html",
		async : true,
		cache : false,
		success : callBackFunction,
		complete: function(){
			for(var i=0;i<loaderArray.length;i++){
				$(loaderArray[i]).hide();
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			for(var i=0;i<loaderArray.length;i++){
				$(loaderArray[i]).hide();
			}
		}
	});
}

function maskPhoneInputs(){
	var phoneFormat = '(ddd) ddd-dddd';
	
	$('#ad-phone').mask(phoneFormat, {
		'translation' : {
			d : {
				pattern : /[0-9*]/
			}
		}
	});
	
	$('#ad-phone-poc-2').mask(phoneFormat, {
		'translation' : {
			d : {
				pattern : /[0-9*]/
			}
		}
	});
}

function bindDatePickerForAdminDashboard(){
	$('#ad-closed-date').datepicker({ format: 'M dd, yyyy'})
						.on('changeDate', function(selected) {
							var closedDate = null;
							if (selected.date != undefined) {
								closedDate = new Date(selected.date.valueOf());
								closedDate.setDate(closedDate.getDate(new Date(selected.date.valueOf())));
							}
							$('#ad-closed-date-ts').val(closedDate.getTime());
						});
	
	$('#ad-gap-analysis-date').datepicker({ format: 'M dd, yyyy'})
							  .on('changeDate', function(selected) {
								  var gapAnalysisDate = null;
								  if (selected.date != undefined) {
									  gapAnalysisDate = new Date(selected.date.valueOf());
									  gapAnalysisDate.setDate(gapAnalysisDate.getDate(new Date(selected.date.valueOf())));
								  }
								  $('#ad-gap-analysis-date-ts').val(gapAnalysisDate.getTime());
							  });
	
	$('#ad-last-conv-date').datepicker({ format: 'M dd, yyyy'})
						   .on('changeDate', function(selected) {
								var lastConversationDate = null;
								if (selected.date != undefined) {
									lastConversationDate = new Date(selected.date.valueOf());
									lastConversationDate.setDate(lastConversationDate.getDate(new Date(selected.date.valueOf())));
								}
								$('#ad-last-conv-date-ts').val(lastConversationDate.getTime());
							});
}