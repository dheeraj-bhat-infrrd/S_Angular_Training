<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${cannonicalusersettings.companySettings.iden}" var="companyId"></c:set>
<c:set value="${userId}" var="userId"></c:set>

<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>

<c:set value="${columnName}" var="columnName"></c:set>
<c:set value="${columnValue}" var="columnId"></c:set>
<c:set value="${hasRegion}" var="hasRegion"></c:set>
<c:set value="${hasBranch}" var="hasBranch"></c:set>

<style>
.block-display{
 display:block !important;
}
</style>

<input type="hidden"  id="user-ranking-data" data-start-index=0 data-count=0>

<div class="hm-header-main-wrapper hm-hdr-bord-bot"
	style="background: #2f69aa">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div id="time-div" class="float-right board-div" style="margin-bottom:0">
				<div class="dash-btn-dl-sd-admin time-selector">
					<select id="time-selector"
						class="float-right dash-download-sel-item board-selector-choice">
						<option value=1 data-report="thisYear">This Year</option>
						<option value=2 data-report="thisMonth">This Month</option>
						<option value=3 data-report="lastYear">Last Year</option>
						<option value=4 data-report="lastMonth">Last Month</option>
						<option value=5 data-report="pastYears">Past Years</option>
					</select>
				</div>
			</div>
		</div>
	</div>
</div>

<c:if test="${profilemasterid == 4}">
	<div id="board-div" class="float-right board-div" style="margin-top: 20px;width: 250px;margin-right: 25px;">
		<span class="board-div-span">Filter</span>
		<div class="dash-btn-dl-sd-admin board-selector" >
			<select id="board-selector" class="float-left dash-download-sel-item board-selector-choice">
				<option value=1 data-report="company">My Company</option>
				<c:if test="${hasRegion == 1}">
					<option value=2 data-report="region">My Region</option>
				</c:if>
				<c:if test="${hasBranch == 1}">
					<option value=3 data-report="branch">My Branch</option>
				</c:if>
			</select>	
		</div>
	</div>
</c:if>
<c:if test="${profilemasterid == 3}">
	<div id="board-div" class="float-right board-div" style="margin-top: 20px;width: 250px;margin-right: 25px;">
		<span class="board-div-span">Filter</span>
		<div class="dash-btn-dl-sd-admin board-selector" >
			<select id="board-selector" class="float-left dash-download-sel-item board-selector-choice">
				<c:if test="${hasBranch == 1}">
					<option value=3 data-report="branch">My Branch</option>
				</c:if>
				<c:if test="${hasRegion == 1}">
					<option value=2 data-report="region">My Region</option>
				</c:if>
				<option value=1 data-report="region">My Company</option>
			</select>	
		</div>
	</div>
</c:if>
<c:if test="${profilemasterid == 2}">
	<div id="board-div" class="float-right board-div" style="margin-top: 20px;width: 250px;margin-right: 25px;">
		<span class="board-div-span">Filter</span>
		<div class="dash-btn-dl-sd-admin board-selector" >
			<select id="board-selector" class="float-left dash-download-sel-item board-selector-choice">
				<c:if test="${hasRegion == 1}">
					<option value=2 data-report="region">My Region</option>
				</c:if>
				<option value=1 data-report="region">My Company</option>
			</select>	
		</div>
	</div>
</c:if>

<c:if test="${profilemasterid == 1}">
	<div id="board-div" class="float-right board-div" style="margin-top: 20px;width: 250px;margin-right: 25px;">
		<span class="board-div-span">Filter</span>
		<div class="dash-btn-dl-sd-admin board-selector" >
			<select id="board-selector" class="float-left dash-download-sel-item board-selector-choice">
				<option value=1 data-report="region">My Company</option>
			</select>	
		</div>
	</div>
</c:if>

<c:if test="${profilemasterid == 4}">
	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
		<div id="lead-ranks-above" class="lead-ranks-above">
			<div id="lead-ranks-above-btn" class="float-right cursor-pointer lead-ranks-above-btn">Load More</div>
		</div>
		<div id="top-ten-ranks" class="top-ten-ranks">
			<div id="top-ten-ranks-btn" class="float-right cursor-pointer top-ten-ranks-btn">Top Ten Ranks</div>
		</div>
		<div id="my-rank" class="my-rank">
			<div id="my-rank-btn" class="float-right cursor-pointer top-ten-ranks-btn">Me</div>
		</div>
	</div>
</c:if>
<c:if test="${profilemasterid != 4}">
	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
		<div id="lead-ranks-above" class="lead-ranks-above">
			<div id="lead-ranks-above-btn" class="float-right cursor-pointer lead-ranks-above-btn">Load More</div>
		</div>
		<div id="top-ten-ranks" class="top-ten-ranks">
			<div id="top-ten-ranks-btn" class="float-right cursor-pointer top-ten-ranks-btn">Top Ten Ranks</div>
		</div>
	</div>
</c:if>

<div class="v-um-tbl-wrapper" id="leaderboard-list" style="width:100%;">
	<%@ include file="leaderboard_list.jsp" %>
</div>
<div id="lead-ranks-below" class="lead-ranks-below">
	<div id="lead-ranks-below-btn" class="float-right cursor-pointer lead-ranks-below-btn">Load More</div>
</div>

<div id="leaderboard-empty-list-msg-div" class="hide">
	<div style="text-align:center; margin:30% auto">
		<span class="incomplete-trans-span" style="font-size:large">No records found for the chosen time frame</span>
	</div>
</div>
<script>
$(document).ready(function(){
	
	var batchSize = 10;
	var startIndex=0;
	var count=0;
	
	var currentDate = new Date();
	var currentYear = currentDate.getFullYear();
	var currentMonth = currentDate.getMonth()+1;
	
	var companyId = "${companyId}";
	var userId = "${userId}";
	var profileMasterId = "${profilemasterid}";
	
	var timeFrameStr = $('#time-selector').val();
	var timeFrame = parseInt(timeFrameStr);
	
	var boardStr = $('#board-selector').val();
	var board = parseInt(boardStr);
	var entityType = 'companyId';
	
	var columnName = "${columnName}";
	var columnId = "${columnId}";
	
	switch(board){
	case 1: entityType = 'companyId';
		break;
	case 2: entityType = 'regionId';
		break;
	case 3: entityType = 'branchId';
		break;
	default: entityType = 'companyId'
	}
	
	var year = currentYear;
	var month = currentMonth;
	
	var tableData='';
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	}
	
	var entityId = companyId;
	
$(document).on('click','#lead-ranks-above-btn',function(){
	startIndex = parseInt($('#user-ranking-data').attr('data-start-index'));
	count = parseInt($('#user-ranking-data').attr('data-count'));
	
	showDashOverlay('#leaderboard-dash');
	showOverlay();
	startIndex -= 10;
	if(startIndex<=0){
		startIndex = 0;
	}
	
	timeFrameStr = $('#time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	}
	
	var boardStr = $('#board-selector').val();
	var board = parseInt(boardStr);
	var entityType = 'companyId';
	
	switch(board){
	case 1: entityType = 'companyId';
		break;
	case 2: entityType = 'regionId';
		break;
	case 3: entityType = 'branchId';
		break;
	default: entityType = 'companyId'
	}
	
	var userRankingList = null;
	
	var entityId = companyId;
	
	if(profileMasterId != 4){
		if(entityType == "regionId" && profileMasterId == 2){
			entityId = columnId;
		}else if(profileMasterId == 3 && entityType == "regionId"){
			entityId = columnId;
		}else if(entityType == "branchId" && profileMasterId == 3){
			entityId = columnId;
		}
		userRankingList = getUserRankingList(entityType,entityId, year, month, startIndex, batchSize, timeFrame);
	}else{
		userRankingList = getUserRankingList(entityType,companyId, year, month, startIndex, batchSize, timeFrame);
	}
	
	showHideRankPaginateBtns(startIndex, count);
	
	$('#user-ranking-data').attr('data-start-index',startIndex);
	$('#user-ranking-data').attr('data-count',count);
	
	 $('html, body').animate({
	        scrollTop: $('#leaderboard-tbl').offset().top - 20
	    }, 'slow');
});

$(document).on('click','#lead-ranks-below-btn',function(){
	showDashOverlay('#leaderboard-dash'); 
	
	startIndex = parseInt($('#user-ranking-data').attr('data-start-index'));
	count = parseInt($('#user-ranking-data').attr('data-count'));
	
	startIndex += 10;
	if(startIndex>=count){
		startIndex = count;
	}
	
	timeFrameStr = $('#time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	}
	
	var boardStr = $('#board-selector').val();
	var board = parseInt(boardStr);
	var entityType = 'companyId';
	
	switch(board){
	case 1: entityType = 'companyId';
		break;
	case 2: entityType = 'regionId';
		break;
	case 3: entityType = 'branchId';
		break;
	default: entityType = 'companyId'
	}
	
	var userRankingList = null;
	
	var entityId = companyId;
	
	if(profileMasterId != 4){
		if(entityType == "regionId" && profileMasterId == 2){
			entityId = columnId;
		}else if(profileMasterId == 3 && entityType == "regionId"){
			entityId = columnId;
		}else if(entityType == "branchId" && profileMasterId == 3){
			entityId = columnId;
		}
		userRankingList = getUserRankingList(entityType,entityId, year, month, startIndex, batchSize, timeFrame);
	}else{
		userRankingList = getUserRankingList(entityType,companyId, year, month, startIndex, batchSize, timeFrame);
	}
	
	showHideRankPaginateBtns(startIndex, count);
	
	$('#user-ranking-data').attr('data-start-index',startIndex);
	$('#user-ranking-data').attr('data-count',count);
	
	$('html, body').animate({
        scrollTop: $('#leaderboard-tbl').offset().top - 20
    }, 'slow');
});

$(document).on('change', '#time-selector', function() {
	showDashOverlay('#leaderboard-dash');
	
	count = parseInt($('#user-ranking-data').attr('data-count'));
	
	timeFrameStr = $('#time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	startIndex = 0;
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	case 5: year = currentYear - 1
	}
	
	var boardStr = $('#board-selector').val();
	var board = parseInt(boardStr);
	var entityType = 'companyId';
	
	switch(board){
	case 1: entityType = 'companyId';
		break;
	case 2: entityType = 'regionId';
		break;
	case 3: entityType = 'branchId';
		break;
	default: entityType = 'companyId'
	}
	
	var entityId = companyId;
	
	if(profileMasterId != 4){
		if(entityType == "regionId" && profileMasterId == 2){
			entityId = columnId;
		}else if(profileMasterId == 3 && entityType == "regionId"){
			entityId = columnId;
		}else if(entityType == "branchId" && profileMasterId == 3){
			entityId = columnId;
		}
		userRankingCount = getUserRankingCountForAdmins(entityType, entityId, year, month, batchSize, timeFrame)
		if(userRankingCount != null){
			startIndex= 0;
			count=userRankingCount.Count;
		}
	}else{
		userRankingCount = getUserRankingCount(entityType, companyId, year, month, batchSize, timeFrame);
		if(userRankingCount != null){
			startIndex= userRankingCount.startIndex;
			count=userRankingCount.Count;
		}
	}
	
	var userRankingList = null;
	
	if(profileMasterId != 4){
		if(entityType == "regionId" && profileMasterId == 2){
			entityId = columnId;
		}else if(profileMasterId == 3 && entityType == "regionId"){
			entityId = columnId;
		}else if(entityType == "branchId" && profileMasterId == 3){
			entityId = columnId;
		}
		userRankingList = getUserRankingList(entityType,entityId, year, month, startIndex, batchSize, timeFrame);
	}else{
		userRankingList = getUserRankingList(entityType,companyId, year, month, startIndex, batchSize, timeFrame);
	}
	
	showHideRankPaginateBtns(startIndex, count);
	
	$('#user-ranking-data').attr('data-start-index',startIndex);
	$('#user-ranking-data').attr('data-count',count);
	
	 $('html, body').animate({
	        scrollTop: $('#leaderboard-tbl').offset().top - 20
	    }, 'slow');
	 
});

$(document).on('change', '#board-selector', function() {
	showDashOverlay('#leaderboard-dash');
	
	count = parseInt($('#user-ranking-data').attr('data-count'));
	
	timeFrameStr = $('#time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	startIndex = 0;
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	case 5: year = currentYear - 1
	}
	
	var boardStr = $('#board-selector').val();
	var board = parseInt(boardStr);
	var entityType = 'companyId';
	
	switch(board){
	case 1: entityType = 'companyId';
		break;
	case 2: entityType = 'regionId';
		break;
	case 3: entityType = 'branchId';
		break;
	default: entityType = 'companyId'
	}
	var entityId = companyId;
	
	if(profileMasterId != 4){
		if(entityType == "regionId" && profileMasterId == 2){
			entityId = columnId;
		}else if(profileMasterId == 3 && entityType == "regionId"){
			entityId = columnId;
		}else if(entityType == "branchId" && profileMasterId == 3){
			entityId = columnId;
		}
		userRankingCount = getUserRankingCountForAdmins(entityType, entityId, year, month, batchSize, timeFrame)
		if(userRankingCount != null){
			startIndex= 0;
			count=userRankingCount.Count;
		}
	}else{
		userRankingCount = getUserRankingCount(entityType, companyId, year, month, batchSize, timeFrame);
		if(userRankingCount != null){
			startIndex= userRankingCount.startIndex;
			count=userRankingCount.Count;
		}
	}
	
	var userRankingList = null;
	
	if(profileMasterId != 4){
		if(entityType == "regionId" && profileMasterId == 2){
			entityId = columnId;
		}else if(profileMasterId == 3 && entityType == "regionId"){
			entityId = columnId;
		}else if(entityType == "branchId" && profileMasterId == 3){
			entityId = columnId;
		}
		userRankingList = getUserRankingList(entityType,entityId, year, month, startIndex, batchSize, timeFrame);
	}else{
		userRankingList = getUserRankingList(entityType,companyId, year, month, startIndex, batchSize, timeFrame);
	}
	
	showHideRankPaginateBtns(startIndex, count);
	
	$('#user-ranking-data').attr('data-start-index',startIndex);
	$('#user-ranking-data').attr('data-count',count);
	
	 $('html, body').animate({
	        scrollTop: $('#leaderboard-tbl').offset().top - 20
	    }, 'slow');
	 
});

$(document).on('click','#top-ten-ranks-btn',function(){
	showDashOverlay('#leaderboard-dash');
	
	count = parseInt($('#user-ranking-data').attr('data-count'));
	startIndex=0;
	
	timeFrameStr = $('#time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	}
	
	var boardStr = $('#board-selector').val();
	var board = parseInt(boardStr);
	var entityType = 'companyId';
	
	switch(board){
	case 1: entityType = 'companyId';
		break;
	case 2: entityType = 'regionId';
		break;
	case 3: entityType = 'branchId';
		break;
	default: entityType = 'companyId'
	}
	
	var entityId = companyId;
	
	var userRankingList = null;
	
	if(profileMasterId != 4){
		if(entityType == "regionId" && profileMasterId == 2){
			entityId = columnId;
		}else if(profileMasterId == 3 && entityType == "regionId"){
			entityId = columnId;
		}else if(entityType == "branchId" && profileMasterId == 3){
			entityId = columnId;
		}
		userRankingList = getUserRankingList(entityType,entityId, year, month, startIndex, batchSize, timeFrame);
	}else{
		userRankingList = getUserRankingList(entityType,companyId, year, month, startIndex, batchSize, timeFrame);
	}

	showHideRankPaginateBtns(startIndex, count);
	
	$('#user-ranking-data').attr('data-start-index',startIndex);
	$('#user-ranking-data').attr('data-count',count);
	
	$('html, body').animate({
        scrollTop: $('#leaderboard-tbl').offset().top - 20
    }, 'slow');
	
});

$(document).on('click','#my-rank-btn',function(){
	showDashOverlay('#leaderboard-dash');
	
	count = parseInt($('#user-ranking-data').attr('data-count'));
	startIndex=0;
	
	timeFrameStr = $('#time-selector').val();
	timeFrame = parseInt(timeFrameStr);
	
	switch(timeFrame){
	case 1: year = currentYear;
		break;
	case 2: year = currentYear;
		month = currentMonth;
		break;
	case 3: year = currentYear - 1;
		break;
	case 4: year = currentYear;
		month=currentMonth -1;
		if(month<=0){
			month=12;
			year--;
		}
		break;
	}
	
	var boardStr = $('#board-selector').val();
	var board = parseInt(boardStr);
	var entityType = 'companyId';
	
	switch(board){
	case 1: entityType = 'companyId';
		break;
	case 2: entityType = 'regionId';
		break;
	case 3: entityType = 'branchId';
		break;
	default: entityType = 'companyId'
	}
	
	
	userRankingCount = getUserRankingCount(entityType, companyId, currentYear, currentMonth, batchSize, timeFrame);
	if(userRankingCount != null){
			startIndex= userRankingCount.startIndex;
			count=userRankingCount.Count;
	}
	
	
	var userRankingList = null;
	
	userRankingList = getUserRankingList(entityType,companyId, year, month, startIndex, batchSize, timeFrame);

	showHideRankPaginateBtns(startIndex, count);
	
	$('#user-ranking-data').attr('data-start-index',startIndex);
	$('#user-ranking-data').attr('data-count',count);
	
	$('html, body').animate({
        scrollTop: $('#leaderboard-tbl').offset().top - 20
    }, 'slow');
	
});

});
</script>