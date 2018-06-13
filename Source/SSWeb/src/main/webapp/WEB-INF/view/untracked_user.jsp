<%@page import="com.realtech.socialsurvey.core.commons.CommonConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div style="height:100%; width:100%;">
	<div class="container" style="margin-top: 20px;height:100%; width:100%;">
		<div style="height:40px">
			<div style="display:inline; font-weight: 600 !important;">Unmatched/Processed Transactions</div>
			<div class="float-right" id="download-report" style="color:#009FE0;cursor:pointer" onclick="downloadSurveyReport(selectedTabIdOnSurveyRecordsSection,${entityId})">
				Download mismatch records report
			</div>
		</div>
		<!-- Nav tabs -->
		<ul class="nav nav-tabs" role="tablist">
			<li class="active"><a id="new-tab-id" href="#new-tab" role="tab" data-toggle="tab" onclick="initializeUnmatchedUserPageOnSelect();" data-iden="<%=CommonConstants.UNMATCHED_USER_TABID %>">
					New <i class="fa fa-folder-open-o"></i></a></li>
			<li><a id="processed-tab-id" href="#processed-tab" role="tab" data-toggle="tab" onclick="initializeProcesedUserPageOnSelect();" data-iden="<%=CommonConstants.PROCESSED_USER_TABID %>">
					Processed <i class="fa fa-folder-o"></i></a></li>
            <li><a id="mapped-tab-id" href="#mapped-tab" role="tab" data-toggle="tab" onclick="initializeMappedOnSelect();" data-iden="<%=CommonConstants.MAPPED_USER_TABID %>">
                 	Mapped Users <i class="fa fa-map-o"></i></a></li>
            <li><a id="corrupt-tab-id" href="#corrupt-tab" role="tab" data-toggle="tab" onclick="initializeCorruptRecordsPageOnSelect();" data-iden="<%=CommonConstants.CORRUPT_USER_TABID %>">
                 	Corrupt Records <i class="fa fa-folder-open-o"></i></a></li>
		</ul>

		<div class="tab-content"
			style="border: #d2dedf 1px solid; ">
			<div id="new-tab" class="tab-pane fade active in" >
				<div>
					<div class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width:15%" class="float-left unmatchtab unhr-row">Agent Name</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Transaction Email</div>
						<div style="width:30%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:15%" class="float-left unmatchtab unhr-row">Date</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Action</div>
						<!-- this is adding new header -->
						<div style="width:20%" class="float-left unmatchtab unhr-row">A</div>
					</div>
					<div id="new" ></div>
					<div id="popup-header" class="hide" style="background-color:#0d6cbf;color: white;border-top: 0;line-height: 25px;padding: 10px;
    font-size: 20px;">
						<div id="popup_user" style="font-weight: bold !important;"></div>
						<div style="font-size: 12px">could not be automatically be associated with a user in your account.</div>
					</div>
					<div id="popup-new" class="col-lg-12 hide">
						<div
							style="background-color: #b5b7bc; font-size: 15px; height: 40px; color: #3a62a3; font-weight: bolder;">
							<div class="col-lg-4"></div>
							<div
								style="height: inherit; color: #3a62a3; background-color: #d9dce2; text-align: left; padding-top: 10px;"
								class="col-lg-8">What can we do for you?</div>
						</div>
						<div id="popup-new-left" class="col-lg-4">
							<div>you have 9 transactions in this status</div>
						</div>
						<div id="popup-new-right" class="col-lg-8">
							<div>
								<div class="">Add <span id="popup_user"></span> as an alias for a current user and send surveys for all transactions. </div>
								<div class="float-left bd-frm-left-un pos-relative">
									<input id="match-user-email" class="bd-frm-rt-txt bd-dd-img">
								</div>
								<div>Assign</div>
							</div>
							<div>
								<div>Create a <b>new user</b> account for <span id="popup_user" style="color:#3a62a3">invalid@domain</span>and <b>send</b> 
								surveys for all transactions</div>
								<div>Add User</div>
							</div>
							<div>
								<div><b>Archive</b> these transactions and automatically <b>ignore</b> <span id="popup_user" style="color:#3a62a3">invalid@domain</span>
								going forward</div>
								<div class="bd-hr-form-item clearfix" id="ignore">
								<div class="float-left bd-frm-left-un"></div>
								<div class="float-left bd-frm-right">
									<div class="bd-frm-check-wrapper clearfix bd-check-wrp">
										<div class="float-left bd-check-img bd-check-img-checked"></div>
										<input type="hidden" name="isIgnore" value="false"
											id="is-ignore" class="ignore-clear">
										<div class="float-left bd-check-txt bd-check-sm">Always
											Ignore</div>
									</div>
								</div>
							</div>
							</div>
						</div>
					</div>
					<div id="new-no-data" class="hide un-no-data"></div>
					<div id="un-new-paginate-btn" class="paginate-buttons-survey clearfix hide" data-start="0" data-total="0" data-batch="10">
						<div id="un-new-prev" class="float-left sur-paginate-btn">&lt; Prev</div>
						<div class="paginate-sel-box float-left">
							<input id="sel-page-un-new-list" type="text" pattern="[0-9]*" class="sel-page" value="0"/>
							<span class="paginate-divider">/</span>
							<span id="un-new-total-pages" class="paginate-total-pages">0</span>
						</div>
						<div id="un-new-next" class="float-right sur-paginate-btn">Next &gt;</div>
					</div>
				</div>
			</div>
			<div class="tab-pane fade" id="processed-tab" >
				<div>
					<div  class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width:15%" class="float-left unmatchtab unhr-row">Agent Name</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Transaction Email</div>
						<div style="width:30%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:15%" class="float-left unmatchtab unhr-row">Date</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Action</div>
					</div>
					<div id="processed"></div>
					<div id="processed-no-data" class="hide un-no-data"></div>
					<div id="un-processed-paginate-btn" class="paginate-buttons-survey clearfix hide" data-start="0" data-total="0" data-batch="10">
						<div id="un-processed-prev" class="float-left sur-paginate-btn">&lt; Prev</div>
						<div class="paginate-sel-box float-left">
							<input id="sel-page-un-processed-list" type="text" pattern="[0-9]*" class="sel-page" value="0"/>
							<span class="paginate-divider">/</span>
							<span id="un-processed-total-pages" class="paginate-total-pages">0</span>
						</div>
						<div id="un-processed-next" class="float-right sur-paginate-btn">Next &gt;</div>
					</div>
				</div>
			</div>
			
			<div class="tab-pane fade" id="mapped-tab">
				<div>
					<div class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width: 30%" class="float-left unmatchtab unhr-row">Name</div>
						<div style="width: 40%" class="float-left unmatchtab unhr-row">Mapped email ID(s)</div>
						<div style="width: 20%" class="float-left unmatchtab unhr-row">Edit</div>
					</div>
					<div id="mapped"></div>
					<div id="mapped-no-data" class="hide un-no-data"></div>
					<div id="mapped-paginate-btn" class="paginate-buttons-survey clearfix hide" data-start="0" data-total="0" data-batch="10">
						<div id="mapped-prev" class="float-left sur-paginate-btn">&lt; Prev</div>
						<div class="paginate-sel-box float-left">
							<input id="sel-page-mapped-list" type="text" pattern="[0-9]*" class="sel-page" value="0"/>
							<span class="paginate-divider">/</span>
							<span id="mapped-total-pages" class="paginate-total-pages">0</span>
						</div>
						<div id="mapped-next" class="float-right sur-paginate-btn">Next &gt;</div>
					</div>
				</div>
				
			</div>
			<div id="corrupt-tab" class="tab-pane fade" >
				<div>
					<div class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width:15%" class="float-left unmatchtab unhr-row">Agent Name</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Transaction Email</div>
						<div style="width:30%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:15%" class="float-left unmatchtab unhr-row">Date</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Reason</div>
					</div>
					<div id="corrupt" ></div>
					<div id="corrupt-no-data" class="hide un-no-data"></div>
					<div id="corrupt-paginate-btn" class="paginate-buttons-survey clearfix hide" data-start="0" data-total="0" data-batch="10">
						<div id="corrupt-prev" class="float-left sur-paginate-btn">&lt; Prev</div>
						<div class="paginate-sel-box float-left">
							<input id="sel-page-corrupt-list" type="text" pattern="[0-9]*" class="sel-page" value="0"/>
							<span class="paginate-divider">/</span>
							<span id="corrupt-total-pages" class="paginate-total-pages">0</span>
						</div>
						<div id="corrupt-next" class="float-right sur-paginate-btn">Next &gt;</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
		bindEventForUnmatchedUserPage();
		bindEventsForProcessUserPage();
		bindEventForMappedUserPage();
		bindEventForCorruptRecordPage();
		//comment unecessary calls made while loading apps 
		initializeUnmatchedUserPage();
		//initializeProcesedUserPage();
		//initializeMapped();
		//initializeCorruptRecordsPage();
	}); 
	
	var selectedTabIdOnSurveyRecordsSection=1;
	
	function initializeUnmatchedUserPageOnSelect(){
		selectedTabIdOnSurveyRecordsSection = $('#new-tab-id').data('iden');
		$('#download-report').html('Download mismatch records report');
		initializeUnmatchedUserPage();
	}
	
	function initializeProcesedUserPageOnSelect(){
		selectedTabIdOnSurveyRecordsSection = $('#processed-tab-id').data('iden');
		$('#download-report').html('Download processed records report');
		initializeProcesedUserPage();
	}
		
	function initializeMappedOnSelect(){
		selectedTabIdOnSurveyRecordsSection = $('#mapped-tab-id').data('iden');
		$('#download-report').html('Download mapped records report');
		initializeMapped();
	}
	
	function initializeCorruptRecordsPageOnSelect(){
		selectedTabIdOnSurveyRecordsSection = $('#corrupt-tab-id').data('iden');
		$('#download-report').html('Download corrupt records report');
		initializeCorruptRecordsPage();
	}
</script>