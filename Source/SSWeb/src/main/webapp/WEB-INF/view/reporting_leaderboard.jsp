<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
.board-selector{
	float: right;
    margin-left: 20px;
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
	margin-right: 40px;
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
</style>
<div id="board-div" class="float-right board-div">
	<span class="board-div-span">Board</span>
	<div class="dash-btn-dl-sd-admin board-selector" >
		<select id="board-selector" class="float-left dash-download-sel-item board-selector-choice">
			<option value=1 data-report="company">Company</option>
			<option value=2 data-report="region">Region</option>
			<option value=3 data-report="branch">Branch</option>
			<option value=4 data-report="users">Users</option>
		</select>	
	</div>
</div>

<table class="v-um-tbl leaderboard-table">
	<tr id="u-tbl-header" class="u-tbl-header">
		<td class="lead-tbl-ln-of text-center">Rank</td>
		<td class="v-tbl-uname">Name</td>
		<td class="lead-tbl-ln-of text-center">Ranking Score</td>
		<td class="lead-tbl-ln-of text-center">Reviews</td>
		<td class="lead-tbl-ln-of text-center">Average Score</td>
		<td class="lead-tbl-ln-of text-center">SPS</td>
		<td class="lead-tbl-ln-of text-center">Completion %</td>
	</tr>
	<tr class="u-tbl-row leaderboard-row" >
		<td class="lead-tbl-ln-of">#1</td>
		<td class="v-tbl-uname fetch-name">
			<div class="leaderboard-name-div">
				<div class="lead-img-div"><img id="prof-image-edit" class="lead-img prof-image-edit pos-relative leaderboard-pic-circle" src="${initParam.resourcesPath}/resources/images/smiley.png"></img></div>
				<span class="lead-name-span" >Dheeraj Bhat</span>
			</div>
		</td>
		<td class="lead-tbl-ln-of">4</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">4.5</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">60%</td>
	</tr>
	<tr class="u-tbl-row leaderboard-row" >
		<td class="lead-tbl-ln-of">#2</td>
		<td class="v-tbl-uname fetch-name">
			<div class="leaderboard-name-div">
				<div class="lead-img-div"><img id="prof-image-edit" class="lead-img prof-image-edit pos-relative leaderboard-pic-circle" src="${initParam.resourcesPath}/resources/images/smiley.png"></img></div>
				<span class="lead-name-span" >Dheeraj Bhat</span>
			</div>
		</td>
		<td class="lead-tbl-ln-of">4</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">4.5</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">60%</td>
	</tr>
	<tr class="u-tbl-row leaderboard-row selected-row " >
		<td class="lead-tbl-ln-of">#3</td>
		<td class="v-tbl-uname fetch-name">
			<div class="leaderboard-name-div">
				<div class="lead-img-div"><img id="prof-image-edit" class="lead-img prof-image-edit pos-relative leaderboard-pic-circle" src="${initParam.resourcesPath}/resources/images/bg-main.png"></img></div>
				<span class="lead-name-span">Dheeraj Bhat</span>
			</div>
		</td>
		<td class="lead-tbl-ln-of">4</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">4.5</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">60%</td>
	</tr>
	<tr class="u-tbl-row leaderboard-row" >
		<td class="lead-tbl-ln-of">#4</td>
		<td class="v-tbl-uname fetch-name">
			<div class="leaderboard-name-div">
				<div class="lead-img-div"><img id="prof-image-edit" class="lead-img prof-image-edit pos-relative leaderboard-pic-circle" src="${initParam.resourcesPath}/resources/images/smiley.png"></img></div>
				<span class="lead-name-span">Dheeraj Bhat</span>
			</div>
		</td>
		<td class="lead-tbl-ln-of">4</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">4.5</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">60%</td>
	</tr>
	<tr class="u-tbl-row leaderboard-row" >
		<td class="lead-tbl-ln-of">#5</td>
		<td class="v-tbl-uname fetch-name">
			<div class="leaderboard-name-div">
				<div class="lead-img-div"><img id="prof-image-edit" class="lead-img prof-image-edit pos-relative leaderboard-pic-circle" src="${initParam.resourcesPath}/resources/images/smiley.png"></img></div>
				<span class="lead-name-span" >Dheeraj Bhat</span>
			</div>
		</td>
		<td class="lead-tbl-ln-of">4</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">4.5</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">60%</td>
	</tr>
	<tr class="u-tbl-row leaderboard-row" >
		<td class="lead-tbl-ln-of">#6</td>
		<td class="v-tbl-uname fetch-name">
			<div class="leaderboard-name-div">
				<div class="lead-img-div"><img id="prof-image-edit" class="lead-img prof-image-edit pos-relative leaderboard-pic-circle" src="${initParam.resourcesPath}/resources/images/smiley.png"></img></div>
				<span class="lead-name-span" >Dheeraj Bhat</span>
			</div>
		</td>
		<td class="lead-tbl-ln-of">4</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">4.5</td>
		<td class="lead-tbl-ln-of">20</td>
		<td class="lead-tbl-ln-of">60%</td>
	</tr>
</table>