function saveEncompassDetails(formid) {
	// TODO: validate form elements
	if (validateEncompassInput(formid)) {
		var url = "./saveencompassdetails.do";
		callAjaxFormSubmit(url, saveEncompassDetailsCallBack, formid);
	}

}

function testEncompassConnection(formid) {
	if (validateEncompassInput(formid)) {
		var url = "./testencompassconnection.do";
		callAjaxFormSubmit(url, testEncompassConnectionCallBack, formid);
	}
}

function saveSurveyParticipationMailBodyContent(formid){
	$('#mailcategory').val('participationmail');
	updateMailContent(formid);
}

function saveSurveyParticipationReminderMailBodyContent(formid){
	$('#mailcategory').val('');
	updateMailContent(formid);
}

function updateMailContent(formid){
	var url = "./savesurveyparticipationmail.do";
	callAjaxFormSubmit(url, updateMailContentCallBack, formid);
}

function validateEncompassInput(formid) {
	return true;
}

function saveEncompassDetailsCallBack(data) {
	alert("Details saved.");
}

function testEncompassConnectionCallBack(data) {
	alert("Connection succesful");
}

function updateMailContentCallBack(data){
	alert("Mail content updated");
}