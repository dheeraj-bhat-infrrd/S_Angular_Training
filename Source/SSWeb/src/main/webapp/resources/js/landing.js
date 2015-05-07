/**
 * js functions for landing page
 */
/**
 * function to change the content of page through ajax
 * 
 * @param url
 */
function showMainContent(url) {
	closeMoblieScreenMenu();
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