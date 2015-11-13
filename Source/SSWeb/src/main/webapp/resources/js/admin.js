// Contains js functions for realtech admin
$(document).on('click','#dsh-ind-report-dwn-btn',function(e){
	var startDate = $('#indv-dsh-start-date').val();
	var endDate = $("#indv-dsh-end-date").val();
	var idenVal = $('#report-sel').attr('data-idenVal');
	var selectedProf = $('#report-sel').attr('data-iden');
	if(idenVal == undefined || idenVal == "") {
		return;
	}
	window.location.href = "./downloadcustomersurveyresults.do?columnName="
			+ selectedProf + "&columnValue=" + idenVal + "&startDate=" + startDate + "&endDate=" + endDate;
});

$(document).on('click','#dsh-admin-report-dwn-btn',function(){
	var selectedValue = $('#download-survey-reports').val();
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	var idenVal = $('#report-sel').attr('data-idenVal');
	var selectedProf = $('#report-sel').attr('data-iden');
	
	var key = parseInt(selectedValue);
	switch (key) {
	case 1:
		window.location.href = "/downloadagentrankingreport.do?columnName="+selectedProf+"&columnValue="+idenVal+"&startDate="+startDate+"&endDate="+endDate;
		break;
	case 2:
		window.location.href = "/downloadcustomersurveyresults.do?columnName="+selectedProf+"&columnValue="+idenVal+"&startDate="+startDate+"&endDate="+endDate;
		break;
	case 3:
		window.location.href = "/downloaddashboardsocialmonitor.do?columnName="+selectedProf+"&columnValue="+idenVal+"&startDate="+startDate+"&endDate="+endDate;
		break;
	default:
		break;
	}
});

$(document).on('keyup','#hr-comp-sel',function(e){
	if(e.which == 13) {
		var key = $(this).val();
		searchAndDisplayCompanies(key);
	}
});

$(document).on('click','#hr-comp-icn',function(e){
	var key = $('#hr-comp-sel').val();
	searchAndDisplayCompanies(key);
});

function searchAndDisplayCompanies(key) {
	var filterValue = $('#com-filter').val();
	var accountType = $('#com-type-filter').val();
	callAjaxGET("/fetchcompaniesbykey.do?searchKey="+key+"&comSelFilter="+filterValue+"&accountType="+accountType, function(data) {
		$('#admin-com-list').html(data);
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
	callAjaxGET("/companyhierarchy.do?companyId="+companyId, function(data) {
		$('.comp-hr-cont[data-iden="'+companyId+'"]').html(data).show();
		bindAdminRegionListClicks();
	    $('.v-tbl-icn').click(function(e){
	        e.stopPropagation();
	    });
	    bindAdminBranchListClicks();
	    bindUserEditClicks();
	}, true);
}

function bindAdminRegionListClicks() {
	$(".region-row").click(function(e){
		var regionId = $(this).attr("data-regionid");
		if($(this).attr('clicked') == "false"){
			var companyId = $(this).closest('.comp-hr-cont').attr('data-iden');
			fetchAdminHierarchyViewBranches(regionId, companyId);
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
			var companyId = $(this).closest('.comp-hr-cont').attr('data-iden');
			fetchAdminUsersForBranch(branchId,regionId, companyId);
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

function fetchAdminHierarchyViewBranches(regionId, companyId) {
	var url = "./fetchhierarchyviewbranchesforadmin.do?regionId="+regionId + "&companyId="+companyId;
	callAjaxGET(url, function(data) {
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
	var url="./fetchbranchusersforadmin.do?branchId="+branchId+"&regionId="+regionId + "&companyId="+companyId;
	callAjaxGET(url, function(data) {
		paintAdminUsersFromBranch(data,branchId);
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
		e.stopImmediatePropagation();
		var payload = {
			"colName" : "userId",
			"colValue" : $(this).attr('data-iden')
		};
		callAjaxGETWithTextData("/loginadminas.do", function(data) {
			// window.location = window.location.origin + '/userlogin.do';
			window.location = getLocationOrigin() + '/userlogin.do';
		}, true, payload);
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
	searchAndDisplayCompanies(key);
});

//Company select filter
$(document).on('change', '#com-type-filter', function(e){
	var key = "";
	$('#hr-comp-sel').val(key);
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
        startDate = new Date(selected.date.valueOf());
        startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
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
        fromEndDate = new Date(selected.date.valueOf());
        fromEndDate.setDate(fromEndDate.getDate(new Date(selected.date.valueOf())));
        $("input[data-date-type='startDate']").datepicker('setEndDate', fromEndDate);
    });
}

function downloadCompanyReport() {
	var startDate = $('#comp-start-date').val();
	var endDate = $('#comp-end-date').val();
	window.location.href = "/downloadcompanyregistrationreport.do?startDate="
			+ startDate + "&endDate=" + endDate;
}

function showAbusiveReviews(startIndexCmp,batchSizeCmp) {
	var payload = {
		"startIndex" : startIndexCmp,
		"batchSize" : batchSizeCmp
	};
	callAjaxGetWithPayloadData("./fetchsurveybyabuse.do", function(data) {
		if (startIndexCmp == 0)
			$('#admin-abs-sur-list').html(data);
		else
			$('#admin-abs-sur-list').append(data);
		
		startIndexCmp += batchSizeCmp;
	}, payload, false);
}

$(document).on('scroll', '#dsh-inc-srvey', function() {
});


$(document).on('click', '.unmark-abusive-icn', function() {
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
	
	callAjaxGetWithPayloadData("./unmarkabusivereview.do", function(data) {
		
		displayMessage(data);
		showAbusiveReviews(0,10);
	}, payload, true);
}
