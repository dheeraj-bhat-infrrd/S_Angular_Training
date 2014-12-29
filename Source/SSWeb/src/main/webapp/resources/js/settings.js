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

function validateEncompassInput(formid) {
	return true;
}

function saveEncompassDetailsCallBack(data) {
	alert("Details saved.");
}

function testEncompassConnectionCallBack(data) {
	alert("Connection succesful");
}