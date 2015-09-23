/*
 * This module helps in browser navigation support to give a single page app
 */

var historyCallback = false;
var refreshSupport = true;

function getRandomID() {
	return (Math.floor(Math.random() * 10000) + Math
			.floor(Math.random() * 10000));
}

function saveState(url) {

	var hashUrl = "";
	hashUrl = url.substring(2, url.length - 3);
	if (!historyCallback) {
		history.pushState(getRandomID(), null, "#" + hashUrl);

	}
	historyCallback = false;
}

function retrieveState() {
	if (!refreshSupport) {
		return;
	}
	var newLocation = window.location.hash.substring(1);
	if (newLocation) {
		showMainContent("/"+newLocation+".do");
	}
}