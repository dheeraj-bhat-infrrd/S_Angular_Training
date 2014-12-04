/*
 *Contains all JavaScript for login purpose
 */

function loginUser() {
	console.log("authenticate user");

	var payLoad = $("#loginForm").serialize();
	
	$.ajax({
		url : "./userlogin.do",
		type : "POST",
		data : payLoad,
		success : function(data) {
			alert("Success");
			console.log(data);
		}
	});

}
