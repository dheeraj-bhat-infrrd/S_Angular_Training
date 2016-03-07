<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div style="border-top: 1px solid #dcdcdc;margin-top: 10px;padding-top: 10px;">
	
	<div class="container" style="margin-top: 20px;">
<div class="un-heading">Unmatched/Processed Transactions</div>
		<!-- Nav tabs -->
		<ul class="nav nav-tabs" role="tablist">
			<li class="active" ><a href="#new-tab" role="tab" data-toggle="tab" onclick="newUnmatched();" >
					New <i class="fa fa-folder-open-o"></i></a></li>
			<li><a href="#processed-tab" role="tab" data-toggle="tab" onclick="unmatchProcess();">
					Processed <i class="fa fa-folder-o"></i></a></li>

		</ul>

		<div class="tab-content"
			style="border: #d2dedf 1px solid; ">
			<div id="new-tab" class="tab-pane fade active in" >
				<div>
					<div id="unmatchd-new" class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width:10%" class="float-left unmatchtab unhr-row">ID</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">User</div>
						<div style="width:40%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Date</div>
						<div style="width:10%" class="float-left unmatchtab unhr-row">Action</div>
					</div>
					<div id="new" style="height:100px;overflow:hidden;position: relative;">
					
					</div>
				</div>

			</div>
			<div class="tab-pane fade" id="processed-tab" >
				<div>
					<div id="unmatchd-processed" class="clearfix"
						style="border: #d2dedf 1px solid;">
						<div style="width:10%" class="float-left unmatchtab unhr-row">ID</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">User</div>
						<div style="width:40%" class="float-left unmatchtab unhr-row">Customer</div>
						<div style="width:20%" class="float-left unmatchtab unhr-row">Date</div>
						<div style="width:10%" class="float-left unmatchtab unhr-row">Action</div>
					</div>
					<div id="processed" style="height:100px;overflow:hidden;position: relative;">
					
					</div>
				</div>

			</div>

		</div>

	</div>
	</div>
	<script>
$(document).ready(function(){
	newUnmatched();
	
	
}); 
</script>