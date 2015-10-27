<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="widget-container" class="prof-user-address prof-edit-icn">
	<textarea id="widget-code-area"></textarea>
</div>
<script>
$(document).ready(function() {
	var iden = "${ iden }";
	var profileLevel = "${ profileLevel }";
	var appBaseUrl = "${ applicationBaseUrl }";
	var body = "";
	if (iden == undefined || profileLevel == undefined || profileLevel == ""){
		body = "Incorrect parameters. Please check your selection.";
	} else {
		body = "&lt;iframe id = \"ss-widget-iframe\" src=\"" + appBaseUrl +  "rest/widget/" + profileLevel + "/" + iden + "\" frameborder=\"0\" width=\"100%\"  height=\"100%\" scrolling=\"no\" /&gt;";
	}
	$("#widget-code-area").html(body);
	
	// browser compatibility: get method for event 
    // addEventListener(FF, Webkit, Opera, IE9+) and attachEvent(IE5-8)
    var myEventMethod = 
        window.addEventListener ? "addEventListener" : "attachEvent";
    // create event listener
    var myEventListener = window[myEventMethod];
    // browser compatibility: attach event uses onmessage
    var myEventMessage = 
        myEventMethod == "attachEvent" ? "onmessage" : "message";
    // register callback function on incoming message
    myEventListener(myEventMessage, function (e) {
        // we will get a string (better browser support) and validate
        // if it is an int - set the height of the iframe #my-iframe-id
        if (e.data === parseInt(e.data)) 
            document.getElementById('ss-widget-iframe').height = e.data + "px";
    }, false);
});

//all content including images has been loaded
window.onload = function() {
    // post our message to the parent
    window.parent.postMessage(
        // get height of the content
        document.body.scrollHeight
        // set target domain
        ,"*"
    )
};
</script>