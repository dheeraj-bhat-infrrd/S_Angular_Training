<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="dup-post-cont" class="dash-stats-wrapper bord-bot-dc clearfix stream-container dup-post-cont"  >
	<div id="dup-post-details-cont" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 soc-mon-post-container bottom-padding-stream dup-post-details ">
		
	</div>
	<div id="dup-post-action-form-cont" class="action-form-cont col-lg-12 col-md-12 col-sm-12 col-xs-12 bottom-padding-stream dup-post-act-cont">
		<form id="dup-post-add-post-action" class="">
		<input type="hidden" id="form-is-dup" class="form-is-dup" name="form-is-dup" value="false">
		<input type="hidden" id="form-post-id" class="form-post-id" name="form-post-id" value="">
		<input type="hidden" id="form-status" name="form-status" class="form-status" value="false">
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 dup-post-act-cont">
			<input type="hidden" id="form-text-act-type" class="form-text-act-type" name="form-text-act-type" value="SEND_EMAIL">
			<div class="send-mail-post col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note stream-post-mail-note-active">Send Email</div>
			<div class="private-note-post col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note">Private Note</div>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 dup-post-act-cont">
			<textarea class="form-post-textbox form-control stream-post-textbox" rows="3" name="form-post-textbox" id="form-post-textbox" placeholder="Send an email message to offending user or take a private note"></textarea>
		</div>
		<div id="dup-post-action-form-container" class="dup-post-act-cont action-form-container col-lg-12 col-md-12 col-sm-12 col-xs-12 stream-actions-btn-container">
			<input type="hidden" id="form-post-act-macro-id" name="form-post-act-macro-id" value=>
			<div class="stream-macro-dropdown col-lg-5 col-md-5 col-sm-5 col-xs-5 macro-dropdown">
				<img src="${initParam.resourcesPath}/resources/images/flash.png" class="macro-dropdown-icn">
						 	 Apply Macro 
				<img src="${initParam.resourcesPath}/resources/images/chevron-down.png" class="mac-chevron-down macro-dropdown-chevron">
				<img src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide mac-chevron-up macro-dropdown-chevron">
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn dup-stream-action-unflag">
							Unflag
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn dup-stream-action-flag">
							Flag
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn dup-stream-action-esc">
							Escalate
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn dup-stream-action-res">
							Resolve
			</div>
			<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn dup-stream-action-submit">
							Submit
			</div>
			<div class="hide macro-options-list float-left macro-options dup-macro-options">
				
			</div>
		</div>
		</form>
	</div>
</div>
<script>
	$(document).ready(function(){
		$('#dup-post-action-form-container').find('.macro-options-list').css('width',$('#dup-post-action-form-container').find('.stream-macro-dropdown').css('width'));
	});
</script>