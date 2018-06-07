<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="stream-post-cont" class="dash-stats-wrapper bord-bot-dc clearfix stream-container"  >
	<div id="stream-post-details-cont" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 soc-mon-post-container bottom-padding-stream">
		<div id="stream-post-icn-cont" class="col-lg-2 col-md-2 col-sm-2 col-xs-2">
			<input type="hidden" class="post-id-details" data-post-id=>
			<img src="${initParam.resourcesPath}/resources/images/check-no.png" class="stream-unchecked float-left stream-checkbox soc-mon-post-checkbox">
			<img src="${initParam.resourcesPath}/resources/images/check-yes.png" class="stream-checked hide float-left stream-checkbox soc-mon-post-checkbox">
			<img src="${initParam.resourcesPath}/resources/images/flag-gray.png" class="cursor-pointer stream-unflagged-icn hide float-left soc-mon-post-icn">
			<img src="${initParam.resourcesPath}/resources/images/flag-yellow.png" class="cursor-pointer stream-flagged-icn hide float-left soc-mon-post-icn">
			<img src="${initParam.resourcesPath}/resources/images/escalated-orange.png" class="stream-esc-icn hide float-left soc-mon-post-icn">
			<img src="${initParam.resourcesPath}/resources/images/verified-green.png" class="stream-res-icn hide float-left soc-mon-post-icn">
		</div>
		<div id="stream-post-details" class="col-lg-10 col-md-10 col-sm-10 col-xs-10 soc-mon-post-details">
			<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2 float-left soc-mon-post-prof-pic-div">
				<!-- <div class="soc-mon-def-prof hide"></div> -->
				<img src="" class="stream-res-icn float-left soc-mon-prof-pic">
				<img src="" class="stream-res-feed-icn float-left soc-mon-prof-pic-media">
			</div>
			<div class="col-lg-7 col-md-7 col-sm-7 col-xs-7 float-left" >
				<div class="stream-user-name row soc-mon-user-name">
								
				</div>
				<div class="stream-feed-type row soc-mon-post-media" data-link="">
								
				</div>
				<div class="stream-post-date row soc-mon-post-date">
								
				</div>
			</div>
			<div class="col-lg-3 col-md-2 col-sm-2 col-xs-2 float-left ts-container hide">
				<div class=" float-right ts-source" data-source="" data-trusted=false>
					
				</div>
				<div class="float-right ts-act-icon ts-add"></div>
				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 float-left trusted-source hide">
					Trusted Source
				</div>
			</div>
			<div class="stream-dup-container col-lg-12 col-md-12 col-sm-12 col-xs-12 float-right hide">
				<div class="post-dup col-lg-3 col-md-3 col-sm-3 col-xs-3 float-right soc-mon-post-dup hide">
					<img src="${initParam.resourcesPath}/resources/images/duplicates.png" class="float-left soc-mon-post-dup-icn">
					<div class="soc-mon-post-dup-num float-left">Manage <span class="dup-count"></span> duplicates</div>
				</div>
			</div>
		</div>
		<div class="stream-post-details-text col-lg-10 col-md-10 col-sm-10 col-xs-10 stream-post-text float-right">
		
		</div>
	</div>
	<div id="action-form-cont" class="action-form-cont col-lg-6 col-md-6 col-sm-6 col-xs-6 bottom-padding-stream">
		<div class="hide add-action-stream-dash" ></div>
		<form id="add-post-action" class="">
		<input type="hidden" id="form-is-dup" class="form-is-dup" name="form-is-dup" value="false">
		<input type="hidden" id="form-post-id" class="form-post-id" name="form-post-id" value="">
		<input type="hidden" id="form-flagged" class="form-flagged" name="form-flagged" value="false">
		<input type="hidden" id="form-status" name="form-status" class="form-status" value="false">
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<input type="hidden" id="form-text-act-type" class="form-text-act-type" name="form-text-act-type" value="SEND_EMAIL">
			<div class="send-mail-post col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note stream-post-mail-note-active">Send Email</div>
			<div class="private-note-post col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note">Private Note</div>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<textarea class="form-post-textbox form-control stream-post-textbox" rows="3" name="form-post-textbox" id="form-post-textbox" placeholder="Send an email message to offending user or take a private note"></textarea>
		</div>
		<div id="action-form-container" class="action-form-container col-lg-12 col-md-12 col-sm-12 col-xs-12 stream-actions-btn-container">
			<input type="hidden" id="form-post-act-macro-id" name="form-post-act-macro-id" value=>
			<div class="stream-macro-dropdown col-lg-5 col-md-5 col-sm-5 col-xs-5 macro-dropdown">
				<img src="${initParam.resourcesPath}/resources/images/flash.png" class="macro-dropdown-icn">
						 	 Apply Macro 
				<img src="${initParam.resourcesPath}/resources/images/chevron-down.png" class="mac-chevron-down macro-dropdown-chevron">
				<img src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide mac-chevron-up macro-dropdown-chevron">
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn stream-action-unflag">
							Unflag
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn stream-action-flag">
							Flag
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn stream-action-esc">
							Escalate
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn stream-action-res">
							Resolve
			</div>
			<div class="hide col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn stream-action-submit">
							Submit
			</div>
			<div class="hide macro-options-list float-left macro-options">
				
			</div>
		</div>
		</form>
		<div id="action-history" class="col-lg-12 col-md-12 col-sm-12 col-xs-12" data-count>
			
		</div>
	</div>
</div>