//Contains js functions for realtech admin


$(document).on('click','#dsh-ind-report-dwn-btn',function(e){
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	var idenVal = $('#report-sel').attr('data-idenVal');
	var selectedProf = $('#report-sel').attr('data-iden');
	if(idenVal == undefined || idenVal == "") {
		return;
	}
	window.location.href = "./downloaddashboardcompletesurvey.do?columnName="
			+ selectedProf + "&columnValue=" + idenVal + "&startDate=" + startDate + "&endDate=" + endDate;
});

$(document).on('click','#dsh-admin-report-dwn-btn',function(){
	var selectedValue = $('#download-survey-reports').val();
	var startDate = $('#dsh-start-date').val();
	var endDate = $("#dsh-end-date").val();
	var key = parseInt(selectedValue);
	var idenVal = $('#report-sel').attr('data-idenVal');
	var selectedProf = $('#report-sel').attr('data-iden');
	switch (key) {
	case 0:
		console.log("complete-survey");
		window.location.href = "/downloaddashboardcompletesurvey.do?columnName="+selectedProf+"&columnValue="+idenVal+"&startDate="+startDate+"&endDate="+endDate;
		break;
	case 1:
		console.log("loan-officer-ranking");
		window.location.href = "/downloadagentrankingreport.do?columnName="+selectedProf+"&columnValue="+idenVal+"&startDate="+startDate+"&endDate="+endDate;
		break;
	case 2:
		console.log("customer-survey");
		window.location.href = "/downloadcustomersurveyresults.do?columnName="+selectedProf+"&columnValue="+idenVal+"&startDate="+startDate+"&endDate="+endDate;
		break;
	case 3:
		console.log("social-monitor");
		window.location.href = "/downloaddashboardsocialmonitor.do?columnName="+selectedProf+"&columnValue="+idenVal+"&startDate="+startDate+"&endDate="+endDate;
		break;
	default:
		break;
	}
});