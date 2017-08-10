<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${cannonicalusersettings.companySettings.iden}" var="companyId"></c:set>
<c:set value="${userId}" var="userId"></c:set>

<style>
.block-display{
 display:block !important;
}
.board-selector{
	float: right;
    margin-left: 20px;
    width: 250px;
    height: 40px;
    border: 1px solid #dcdcdc;
}

.time-selector{
    margin-left: 40px;
    width: 250px;
    height: 40px;
    border: 1px solid #dcdcdc;
}

.board-selector-choice{
	width: 100%;
    border-bottom: 0 !important;
    color: #666;
    padding-left: 10px;
    height: 38px;
}

.board-div-span{
	height:30px;
	line-height:38px;
}

.board-div{
	    margin-bottom: 30px;
}

.leaderboard-pic-circle{
	position: relative !important;
    margin: 2px !important;
    border-radius: 50% !important;
    width: 55px !important;
    height: 55px !important;
}

.leaderboard-name-div{
	display: inline-flex;
    width: 100%;
    height: 42px;
    padding-left:10%;
}

.selected-row td:first-child{
    -moz-border-radius:15px 0 0 15px;
    -webkit-border-radius:15px 0 0 15px;
}
.selected-row td:last-child{
    -moz-border-radius:0 15x 15px 0;
    -webkit-border-radius:0 15px 15px 0;
}
.selected-row td{
	background:#4f85ca;
	color:white;
}

.leaderboard-row{
	border-bottom:0 !important;
}

.leaderboard-table{
    border-collapse: separate;
    border-spacing: 0 35px !important;
}

.lead-img-div{
    margin-top: -8px;
    position: absolute;
    margin-left:-10px;
}
.lead-name-span{
	line-height: 42px;
    margin-left: 82px;
    overflow: hidden;
}

img.lead-img {
    max-width: 100%;
    max-height: 100%;
    display: block;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
}

.lead-tbl-ln-of {
    width: 10%;
}

.lead-ranks-above{
	width: 100px;
    margin: 0;
    margin-left: 89%;
}

.lead-ranks-above-btn{
	margin-bottom: -15px;
    font-size: 14px;
    font-weight: bold !important;
}

.lead-ranks-below{
    width: 100px;
    margin: 0;
    margin-left: 89%
}

.lead-ranks-below-btn{
	font-size: 14px;
    font-weight: bold !important;
}
</style>
<div id="time-div" class="float-left board-div">
	<div class="dash-btn-dl-sd-admin time-selector" >
		<select id="time-selector" class="float-left dash-download-sel-item board-selector-choice">
			<option value=1 data-report="thisYear">This Year</option>
			<option value=2 data-report="thisMonth">This Month</option>
			<option value=3 data-report="lastYear">Last Year</option>
			<option value=4 data-report="lastMonth">Last Month</option>
			<option value=5 data-report="pastYears">Past Years</option>
		</select>	
	</div>
</div>

<div id="board-div" class="float-right board-div">
	<span class="board-div-span">Board</span>
	<div class="dash-btn-dl-sd-admin board-selector" >
		<select id="board-selector" class="float-left dash-download-sel-item board-selector-choice">
			<option value=1 data-report="company">Company</option>
			<option value=2 data-report="region">Region</option>
			<option value=3 data-report="branch">Branch</option>
		</select>	
	</div>
</div>
	
<div id="lead-ranks-above" class="lead-ranks-above">
	<div id="lead-ranks-above-btn" class="float-right paginate-button lead-ranks-above-btn">Ranks above</div>
</div>
<div class="v-um-tbl-wrapper" id="leaderboard-list" style="width:100%;">
	<jsp:include page="leaderboard_list.jsp"></jsp:include>
</div>
<div id="lead-ranks-below" class="lead-ranks-below">
	<div id="lead-ranks-below-btn" class="float-right paginate-button lead-ranks-below-btn">Ranks below</div>
</div>

<div id="leaderboard-empty-list-msg-div" class="hide">
	<div style="text-align:center; margin:30% auto">
		<span class="incomplete-trans-span">No records found for the chosen time frame</span>
	</div>
</div>
<script>
$(document).ready(function(){
	var batchSize = 10;
	var startIndex=0;
	var count=0;
	
	var currentDate = new Date();
	var currentYear = currentDate.getFullYear();
	var currentMonth = currentDate.getMonth();
	
	var companyId = "${companyId}";
	var userId = "${userId}";
	
	var timeFrameStr = $('#time-selector').val();
	var timeFrame = parseInt(timeFrameStr);
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
		break;
	}
	
	var userRankingCount = getUserRankingCount("companyId", companyId, year, month, batchSize, timeFrame);
	if(userRankingCount != null){
		startIndex= userRankingCount.startIndex;
		count=userRankingCount.Count;
	}
	
showHideRankPaginateBtns(startIndex, count);
	
$(document).on('click','#lead-ranks-above-btn',function(){
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
		break;
	}
	
	var userRankingList = getUserRankingList("companyId",companyId, year, month, startIndex, batchSize, timeFrame);
	
	if(userRankingList != null && userRankingList.length != 0){
		tableData=drawLeaderboardTableStructure(userRankingList, userId)
		$('#leaderboard-list').removeClass('hide');
		$('#leaderboard-tbl').html(tableData);
		$('#leaderboard-empty-list-msg-div').addClass('hide');
	}else{
		$('#leaderboard-list').addClass('hide');
		$('#leaderboard-empty-list-msg-div').removeClass('hide');
	}
	
	showHideRankPaginateBtns(startIndex, count);
	
	 $('html, body').animate({
	        scrollTop: $('#leaderboard-tbl').offset().top - 20
	    }, 'slow');
	 
	hideOverlay();
});

$(document).on('click','#lead-ranks-below-btn',function(){
	showOverlay();
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
		break;
	}
	
	var userRankingList = getUserRankingList("companyId",companyId, year, month, startIndex, batchSize, timeFrame);
	
	if(userRankingList != null && userRankingList.length != 0){
		tableData=drawLeaderboardTableStructure(userRankingList, userId)
		$('#leaderboard-list').removeClass('hide');
		$('#leaderboard-tbl').html(tableData);
		$('#leaderboard-empty-list-msg-div').addClass('hide');
	}else{
		$('#leaderboard-list').addClass('hide');
		$('#leaderboard-empty-list-msg-div').removeClass('hide');
	}
	
	showHideRankPaginateBtns(startIndex, count);
	$('html, body').animate({
        scrollTop: $('#leaderboard-tbl').offset().top - 20
    }, 'slow');
	hideOverlay();
});

$(document).on('change', '#time-selector', function() {
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
		break;
	case 5: year = currentYear - 1
	}
	userRankingCount = getUserRankingCount("companyId", companyId, year, month, batchSize, timeFrame);
	if(userRankingCount != null){
		startIndex= userRankingCount.startIndex;
		count=userRankingCount.Count;
	}
	
	var userRankingList = getUserRankingList("companyId",companyId, year, month, startIndex, batchSize, timeFrame);
	
	if(userRankingList != null && userRankingList.length != 0){
		tableData=drawLeaderboardTableStructure(userRankingList, userId)
		$('#leaderboard-list').removeClass('hide');
		$('#leaderboard-tbl').html(tableData);
		$('#leaderboard-empty-list-msg-div').addClass('hide');
	}else{
		$('#leaderboard-list').addClass('hide');
		$('#leaderboard-empty-list-msg-div').removeClass('hide');
	}
	
	showHideRankPaginateBtns(startIndex, count);
	
	 $('html, body').animate({
	        scrollTop: $('#leaderboard-tbl').offset().top - 20
	    }, 'slow');
	 
	hideOverlay();
});
});
</script>