<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div style="border-top: 1px solid #dcdcdc;margin-top: 10px;padding-top: 10px;">
	
	<div class="container" style="margin-top: 20px;">
<div class="un-heading">Unmatched/Processed Transactions</div>
		<!-- Nav tabs -->
		<ul class="nav nav-tabs" role="tablist">
			<li class="active" ><a href="#new-tab" role="tab" data-toggle="tab" onclick="initializeUnmatchedUserPage();" >
					New <i class="fa fa-folder-open-o"></i></a></li>
			<li><a href="#processed-tab" role="tab" data-toggle="tab" onclick="initializeProcesedUserPage();">
					Processed <i class="fa fa-folder-o"></i></a></li>
            <li><a href="#mapped-tab" role="tab" data-toggle="tab" onclick="initializeMapped();">
                 	Mapped Users <i class="fa fa-map-o"></i></a></li>
            <li><a href="#corrupt-tab" role="tab" data-toggle="tab" onclick="initializeCorrupRecordsPage();">
                 	Corrupt Records <i class="fa fa-folder-open-o"></i></a></li>
		</ul>

		<div class="tab-content"
			style="border: #d2dedf 1px solid; ">
			<div id="new-tab" class="tab-pane fade active in" >
				<div>
					<div class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width:10%" class="float-left unmatchtab unhr-row">Agent Name</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Transaction Email</div>
						<div style="width:30%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Date</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Action</div>
					</div>
					<div id="new" ></div>
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
						<div style="width:10%" class="float-left unmatchtab unhr-row">Agent Name</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Transaction Email</div>
						<div style="width:30%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Date</div>
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
						<div style="width:10%" class="float-left unmatchtab unhr-row">Agent Name</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Transaction Email</div>
						<div style="width:30%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Date</div>
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
	initializeUnmatchedUserPage();
	initializeProcesedUserPage();
	initializeMapped();
	initializeCorrupRecordsPage();
}); 
</script>