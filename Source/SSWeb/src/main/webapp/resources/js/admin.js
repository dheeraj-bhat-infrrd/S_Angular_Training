// Contains js functions for realtech admin

var abuseReportStartIndex = 0;
var abuseReportBatch = 10;
var doStopAbuseReportPagination = false;
var isAbuseReportRequestRunning = false;

$(document).on('click','#dsh-ind-report-dwn-btn',function(e){
	var startDate = $('#indv-dsh-start-date').val();
	var endDate = $("#indv-dsh-end-date").val();
	var idenVal = $('#report-sel').attr('data-idenVal');
	var selectedProf = $('#report-sel').attr('data-iden');
	var mailId = $("#dsh-survey-report-mail-id").val();
	
	if(idenVal == undefined || idenVal == "") {
		$('#overlay-toast').html('Please select a valid profile value.');
		showToast();
		return;
	}
	
	if (emailRegex.test(mailId) || mailId == "") {
		payload = { "mailid" : mailId, "startDate":startDate, "endDate":endDate, "columnValue" : idenVal, "columnName": selectedProf};
		callAjaxGetWithPayloadData("./generatecustomersurveyresults.do", function(data) {
			$('#overlay-toast').html(data);
			showToast();
		}, payload, true);
	} else {
		$('#overlay-toast').html('Please enter a valid email address.');
		showToast();
	}
});

$(document).on('keyup','#hr-comp-sel',function(e){
	if(e.which == 13) {
		var key = $(this).val();
		var encodedKey = encodeURIComponent( key ).replace(/[!'()]/g, escape).replace(/\*/g, "%2A");
		var srchType = $('#hr-comp-sel').attr("srch-type");
		
		if(srchType == "company")
			searchAndDisplayCompanies(encodedKey);
		else if(srchType == "region")
			searchAndDisplayRegions(encodedKey);
		else if(srchType == "office")
			searchAndDisplayBranches(encodedKey);
		else if(srchType == "user")
			searchAndDisplayUsers(encodedKey);
	}
});

$(document).on('click','#hr-comp-icn',function(e){
	var key = $('#hr-comp-sel').val();
	var encodedKey = encodeURIComponent( key ).replace(/[!'()]/g, escape).replace(/\*/g, "%2A");
	var srchType = $('#hr-comp-sel').attr("srch-type");
	if(srchType == "company")
		searchAndDisplayCompanies(encodedKey);
	else if(srchType == "region")
		searchAndDisplayRegions(encodedKey);
	else if(srchType == "office")
		searchAndDisplayBranches(encodedKey);
	else if(srchType == "user")
		searchAndDisplayUsers(encodedKey);
});

//deelte company
$(document).on('click','.comp-del-icn',function(e){
	var companyId = $(this).attr('data-iden');
	e.stopPropagation();
	confirmDeleteCompany(companyId);
});

function confirmDeleteCompany(companyId){
	$('#overlay-main').show();
	$('#overlay-continue').show();
	$('#overlay-continue').html("Delete");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Delete Company");
	$('#overlay-text').html("Are you sure you want to delete the company ?");
	$('#overlay-continue').attr("onclick", "deleteCompany('" + companyId + "');");
}

function deleteCompany(companyId){
	showOverlay();
	var payload = {
		"companyId" : companyId
	};
	//close the popup
	$('#overlay-cancel').click();
	callAjaxPostWithPayloadData("./deletecompany.do", function(data) {
		
		if (data == "success") {
			// remove the tab from UI
			$('#tr-comp-' + companyId).remove();
			$('#overlay-toast').html("Company successfully deleted. Clean up is in progress.");
		}else{
			$('#overlay-toast').html(data);
			showToast();
		}
	}, payload, true);
}

$(document).on('click','#hr-drpdwn-icn',function(e){
	e.stopPropagation();
	$('#srch-crtria-list').slideToggle(200);
});

$(document).on('click','.hr-dd-item',function(e){
	$('#hr-comp-sel').attr("placeholder" , "Search " + $(this).html());
	$('#hr-comp-sel').attr("srch-type" , $(this).attr("srch-type"));
	$('#hr-comp-sel').val('');
	$('#srch-crtria-list').slideToggle(200);
	if( $(this).attr("srch-type") == "company"){
		$("#company-criterial-interval-wrapper").show();
		$("#srchcmpnyalldays").addClass('bd-cust-rad-img-checked');
		$("#srchcmpnythirtydays").removeClass('bd-cust-rad-img-checked');
	}else{
		$("#company-criterial-interval-wrapper").hide();
	}
});

function searchAndDisplayCompanies(key) {
	var filterValue = $('#com-filter').val();
	var accountType = $('#com-type-filter').val();
	var noOfDays = $(".bd-cust-rad-img-checked").attr('data-type');
	showOverlay();
	callAjaxGETAndAbortLastRequest("/fetchcompaniesbykey.do?searchKey="+key+"&comSelFilter="+filterValue+"&accountType="+accountType+"&noOfDays="+noOfDays, function(data) {
		$('#admin-com-list').html(data);
	}, true);
}

function searchAndDisplayRegions(key) {
	
	showOverlay();
	callAjaxGETAndAbortLastRequest("/fetchregionsbykey.do?searchKey="+key, function(data) {
		$('#admin-com-list').html('<div data-iden="0" class="hide comp-hr-cont"></div>');
		$('.comp-hr-cont[data-iden="'+0+'"]').html(data).show();
		bindAdminRegionListClicks();
	    
	}, true);
}

function searchAndDisplayBranches(key) {
	
	showOverlay();
	callAjaxGETAndAbortLastRequest("/fetchbranchesbykey.do?searchKey="+key, function(data) {
		
		$('#admin-com-list').html('<div data-iden="0" class="hide comp-hr-cont"></div>');
		
		var tableRowForVirtualRegion = '<tr class="v-tbl-row v-tbl-row-sel tr-region-edit hide" style=""><td colspan="7" id="td-region-edit-0" class="td-region-edit"></td></tr>'
		var tableForVirtualCompany = '<table class="v-hr-tbl" style="margin-top: 0"><tbody>' + tableRowForVirtualRegion +'</tbody></table>';
		var tableWrapperDiv = '<div class="v-hr-tbl-wrapper">' + tableForVirtualCompany + '</div>';
		$('.comp-hr-cont[data-iden="'+0+'"]').html(tableWrapperDiv).show();
		
		$("#td-region-edit-"+0).parent(".tr-region-edit").after(data);
		$("#tr-region-"+0).slideDown(200);
		$(".tr-region-edit").slideUp(200);
		bindAdminBranchListClicks();
	}, true);
}

function searchAndDisplayUsers(key) {
	
	showOverlay();
	callAjaxGETAndAbortLastRequest("/fetchusersbykey.do?searchKey="+key, function(data) {
		
		$('#admin-com-list').html('<div data-iden="0" class="hide comp-hr-cont"></div>');
		
		var tableRowForVirtualBranch = '<tr class="v-tbl-row v-tbl-row-sel tr-branch-edit hide" style=""><td colspan="7" id="td-branch-edit-0" class="td-branch-edit"></td></tr>'
		var tableForVirtualCompany = '<table class="v-hr-tbl" style="margin-top: 0"><tbody>' + tableRowForVirtualBranch +'</tbody></table>';
		var tableWrapperDiv = '<div class="v-hr-tbl-wrapper">' + tableForVirtualCompany + '</div>';
		
		$('.comp-hr-cont[data-iden="'+0+'"]').html(tableWrapperDiv).show();
		
		$("#td-branch-edit-"+0).parent(".tr-branch-edit").after(data);
		$("#tr-branch-"+0).slideDown(200);
		$(".tr-branch-edit").slideUp(200);
		bindUserEditClicks();
	}, true);
}

function searchAdminCompanies (element) {
	var payload = {
		"searchColumn" : "company",
		"searchKey" : $(element).val()
	};
	

	callAjaxGetWithPayloadData("./findregionbranchorindividual.do", function(data) {
		
		$('#hr-comp-res').html(data).show();
		
		$('.dsh-res-display').click(function() {
			var value = $(this).data('attr');
			columnName = "companyId";
			$('#hr-comp-sel').val($(this).html());
			$('#hr-comp-sel').attr("data-iden", value);
			$('.dsh-res-display').hide();
			$('#hr-comp-res').hide();
			
			//showSelectedCompanyHierarchy(value);
		});
	}, payload, true);
}

function showSelectedCompanyHierarchy(companyId) {
	$('#tr-spinner-'+companyId).show();
	callAjaxGET("/companyhierarchy.do?companyId="+companyId, function(data) {
		$('.comp-hr-cont[data-iden="'+companyId+'"]').html(data).show();
		bindAdminRegionListClicks();
/*	    $('.v-tbl-icn').click(function(e){
	        e.stopPropagation();
	    });*/
	    bindAdminBranchListClicks();
	    bindUserEditClicks();
	    $('#tr-spinner-'+companyId).hide();
	}, true);
}

function bindAdminRegionListClicks() {
	$(".region-row").click(function(e){
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			fetchAdminHierarchyViewBranches(regionId);
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

function bindAdminBranchListClicks(){
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
			fetchAdminUsersForBranch(branchId,regionId);
			 $(this).attr('clicked','true');
		}
		else {
			$('.user-row-'+branchId).html("").hide(); 
            $(this).attr('clicked','false');
		}
	});
	$(".branch-del-icn").unbind('click');
	$(".branch-del-icn").click(function(e){
		e.stopPropagation();
		var branchId = $(this).attr("data-branchid");
		deleteBranchPopup(branchId);
	});
}

function fetchAdminHierarchyViewBranches(regionId) {
	$('#tr-spinner-'+regionId).show();
	var url = "./fetchhierarchyviewbranchesforadmin.do?regionId="+regionId;
	callAjaxGET(url, function(data) {
		$('#tr-spinner-'+regionId).hide();
		paintAdminHierarchyViewBranches(data,regionId);
	}, true);
}

function paintAdminHierarchyViewBranches(data,regionId) {
	$("#td-region-edit-"+regionId).parent(".tr-region-edit").after(data);
	$("#tr-region-"+regionId).slideDown(200);
	$(".tr-region-edit").slideUp(200);
	bindUserEditClicks();
	bindAdminBranchListClicks();
}

function fetchAdminUsersForBranch(branchId,regionId, companyId) {
	$('#tr-spinner-'+branchId).show();
	var url="./fetchbranchusersforadmin.do?branchId="+branchId+"&regionId="+regionId;
	callAjaxGET(url, function(data) {
		paintAdminUsersFromBranch(data,branchId);
		$('#tr-spinner-'+branchId).hide();
	}, true);
}

function paintAdminUsersFromBranch(data,branchId,regionId) {
	$("#td-branch-edit-"+branchId).parent(".tr-branch-edit").after(data);
	$("#tr-branch-"+branchId).slideDown(200);
	$(".tr-branch-edit").slideUp(200);
	bindUserEditClicks();
}

$(document).on('click', '.comp-row', function(e) {
	var element = this; 
	var companyId = $(element).attr('data-iden');
	$('.comp-hr-cont').html('').hide();
	if ($(element).attr('clicked') == "false") {
		showSelectedCompanyHierarchy(companyId);
		$('.comp-row').attr('clicked', 'false');
		$(element).attr('clicked', 'true');
	} else {
		$('.comp-hr-cont[data-iden="' + companyId + '"]').html('').hide();
		$('.comp-row').attr('clicked', 'false');
	}
});

function bindUserLoginEvent() {
	console.log("inside admin");
	$('.user-login-icn').on('click', function(e) {
		$( '.user-login-icn').unbind( "click" );
		e.stopImmediatePropagation();
		var payload = {
			"colName" : "userId",
			"colValue" : $(this).attr('data-iden')
		};
		callAjaxGETWithTextData("/loginadminas.do", function(data) {
			window.location = getLocationOrigin() + '/userlogin.do?eraseCache=true';
		}, true, payload,'.user-login-icn');
	});
}

$(document).on('click','#send-invite-form-submit',function(){
	var formData = $('#send-invite-form').serialize();
	callAjaxGetWithPayloadData("/generateregistrationurl.do", function(data){
		$('#overlay-toast').html(data);
		showToast();
	}, formData, true);
});

$(document).on('keyup','#send-invite-form',function(e){
	if(e.which==13){
		$('#send-invite-form-submit').trigger('click');
	}
});

//Company select filter
$(document).on('change', '#com-filter', function(e){
	var key = "";
	$('#hr-comp-sel').val(key);
	$('#hr-comp-sel').attr("placeholder" , "Search Company");
	$('#hr-comp-sel').attr("srch-type" , "company");
	
	searchAndDisplayCompanies(key);
});

//Company select filter
$(document).on('change', '#com-type-filter', function(e){
	var key = "";
	$('#hr-comp-sel').val(key);
	$('#hr-comp-sel').attr("placeholder" , "Search Company");
	$('#hr-comp-sel').attr("srch-type" , "company");
	
	searchAndDisplayCompanies(key);
});

function bindDatePickerForCompanyReport() {
	var startDate;
	var fromEndDate = new Date();
	var toEndDate = new Date();
	$("input[data-date-type='startDate']").datepicker({
		orientation: "auto",
		format: 'mm/dd/yyyy',
		endDate: fromEndDate,
		todayHighlight: true,
		clearBtn: true,
		autoclose: true
	})
	.on('changeDate', function(selected){
		if(selected.date != undefined){
	        startDate = new Date(selected.date.valueOf());
	        startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
		}else{
			startDate = null;
		}
        $("input[data-date-type='endDate']").datepicker('setStartDate', startDate);
    });
	
	$("input[data-date-type='endDate']").datepicker({
		orientation: "auto",
		format: 'mm/dd/yyyy',
		endDate: toEndDate,
		todayHighlight: true,
		clearBtn: true,
		autoclose: true
	})
	.on('changeDate', function(selected){
		if(selected.date != undefined){
	        fromEndDate = new Date(selected.date.valueOf());
	        fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
		}else{
			fromEndDate = null;
		}
        $("input[data-date-type='startDate']").datepicker('setEndDate', fromEndDate);
    });
}

function downloadCompanyReport() {
	var startDate = $('#comp-start-date').val();
	var endDate = $('#comp-end-date').val();
	var mailId = $("#dsh-company-report-mail-id").val();
	if (emailRegex.test(mailId) || mailId == "") {
		payload = { "mailid" : mailId, "startDate":startDate, "endDate":endDate };
		callAjaxGetWithPayloadData("./downloadcompanyregistrationreport.do", function(data) {
			$('#overlay-toast').html(data);
			showToast();
		}, payload, true);
	} else {
		$('#overlay-toast').html('Please enter a valid email address');
		showToast();
	}
}

function downloadBillingReport() {
	var mailId = $("#dsh-mail-id").val();
	if (emailRegex.test(mailId) || mailId == "") {
		payload = { "mailid" : mailId };
		callAjaxGetWithPayloadData("./downloadbillingreport.do", function(data) {
			$('#overlay-toast').html(data);
			showToast();
		}, payload, true);
	} else {
		$('#overlay-toast').html('Please enter a valid email address');
		showToast();
	}
}

function showAbusiveReviews(startIndexCmp,batchSizeCmp) {
	if(startIndexCmp == 0) {
		isAbuseReportRequestRunning = false;
		doStopAbuseReportPagination = false;
	}
	showLoaderOnPagination($('#admin-abs-sur-list'));
	var payload = {
		"startIndex" : startIndexCmp,
		"batchSize" : batchSizeCmp
	};
	isAbuseReportRequestRunning = true;
	callAjaxGetWithPayloadData("./fetchsurveybyabuse.do", function(data) {
		isAbuseReportRequestRunning = false;
		var tempDiv = $("<div>");
		tempDiv.html(data);
		
		if(tempDiv.children('.abuse-review-item').length < abuseReportBatch) {
			doStopAbuseReportPagination = true;
		}
	
		hideLoaderOnPagination($('#admin-abs-sur-list'));	
		
		if (startIndexCmp == 0)
			$('#admin-abs-sur-list').html(data);
		else
			$('#admin-abs-sur-list').append(data);
		
	}, payload, true);
}


function getSocialSurveyAdminList(){
	var payload = {};
	callAjaxGetWithPayloadData("./getsocialsurveyadminlist.do", function(data) {
	
			$('#ss-admin-list-wrapper').html(data);
		
	}, payload, true);
	
}


$(document).on('click', '.unmark-abusive-icn', function(e) {
	e.stopPropagation();
	var surveyMongoId = $(this).parent().parent().attr('data-iden');
	confirmUnmarkAbusiveReview(surveyMongoId);
});


function confirmUnmarkAbusiveReview(surveyId){
	
	$('#overlay-main').show();
	$('#overlay-continue').show();
	$('#overlay-continue').html("Unmark");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Unmark review");
	$('#overlay-text').html("Are you sure you want to unmark the review?");
	$('#overlay-continue').attr("onclick", "unmarkReviewFromAbusive('" + surveyId + "');");
	
}

function unmarkReviewFromAbusive(surveyId){
	var payload = {
			"surveyId" : surveyId
		};
	
	//close the popup
	overlayRevert();
	showOverlay();
	abuseReportStartIndex = 0;
	callAjaxGetWithPayloadData("./unmarkabusivereview.do", function(data) {
		
		displayMessage(data);
		showAbusiveReviews(abuseReportStartIndex, abuseReportBatch);
	}, payload, true);
}

function attachScrollEventOnAbuseReports(){
	$(window).scroll(function() {
		if ((window.innerHeight + window.pageYOffset) >= ($('#admin-abs-sur-list').offset().top + $('#admin-abs-sur-list').height()) ){
			if(!doStopAbuseReportPagination && !isAbuseReportRequestRunning) {
				abuseReportStartIndex += abuseReportBatch;
				showAbusiveReviews(abuseReportStartIndex, abuseReportBatch);				
			}
		}
	});
}


$(document).on('click','#add-ss-admin-form-submit',function(){
	if(validateFirstName("add-ss-admin-fname") && validateLastName("add-ss-admin-lname") && validateEmailId("add-ss-admin-emailid", true)){
		var formData = $('#add-ss-admin-form').serialize();
		showOverlay();
		callAjaxPostWithPayloadData("./createsocialsurveyadmin.do", function(data){
			data = JSON.parse(data);
			 if(data.isCreated){
				$("#add-ss-admin-fname").val('');
				$("#add-ss-admin-lname").val('');
				$("#add-ss-admin-emailid").val('');
				//repaint admin list
				showMainContent('./adminusermanagement.do');
			 }
			
			$('#overlay-toast').html(data.message);
			showToast();
		}, formData, true);
	}
});

$(document).on('keyup','#add-ss-admin-form',function(e){
	if(e.which==13){
		$('#add-ss-admin-form-submit').trigger('click');
	}
});


$(document).on('click','.v-icn-rem-ssadmin',function(e){
	var ssAdminId = $(this).attr('data-user-id');
	e.stopPropagation();
	if(ssAdminId > 0)
		confirmDeleteSSAdmin(ssAdminId);
})

function confirmDeleteSSAdmin(ssAdminId){
	$('#overlay-main').show();
	$('#overlay-continue').show();
	$('#overlay-continue').html("Delete");
	$('#overlay-cancel').html("Cancel");
	$('#overlay-header').html("Delete SocialSurvey Admin");
	$('#overlay-text').html("Are you sure you want to delete the SocialSurvey admin ?");
	$('#overlay-continue').attr("onclick", "deleteSSAdmin('" + ssAdminId + "');");
}

function deleteSSAdmin(ssAdminId) {
	var url = "./deletesocialsurveyadmin.do?userId=" + ssAdminId;
	//close the popup
	$('#overlay-cancel').click();
	showOverlay();
	callAjaxPOST(url, function(data){
		displayMessage(data);
		$("#user-row-"+ssAdminId).hide();
	}, true);
}

function bindCompanyIntervalCriteriaSelection() {
	$('.bd-cust-rad-img').click(function(e) {
        $('.bd-cust-rad-img').removeClass('bd-cust-rad-img-checked');
        $(this).toggleClass('bd-cust-rad-img-checked');
        $('#user-selection-info').attr('data-user-selection-type',$(this).data('type'));
        
        var key = "";
    	$('#hr-comp-sel').val(key);
    	$('#hr-comp-sel').attr("placeholder" , "Search Company");
    	$('#hr-comp-sel').attr("srch-type" , "company");
    	
    	searchAndDisplayCompanies(key);
    });
}


