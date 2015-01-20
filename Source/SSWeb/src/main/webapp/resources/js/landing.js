/**
 * js functions for landing page
 */

/**
 * function to change the content of page through ajax
 * 
 * @param url
 */
function showMainContent(url) {
	callAjaxGET(url, showMainContentCallBack, true);
}

/**
 * Callback for showMainContent, displays data in the main content section
 * 
 * @param data
 */
function showMainContentCallBack(data) {
	$("#main-content").html(data);
}
/**
 * function for logout
 */
function logoutuser(){
	console.log('Logging out user');
	callAjaxGET("./logout.do", logoutCallBack, true);
}

function logoutCallBack(data){
	console.log("redirecting to login page");
	window.location="login.do";
}