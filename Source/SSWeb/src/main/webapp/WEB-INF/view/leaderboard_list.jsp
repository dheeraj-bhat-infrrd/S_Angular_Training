<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${cannonicalusersettings.companySettings.iden}" var="companyId"></c:set>
<c:set value="${userId}" var="userId"></c:set>

<style>
.leaderboard-table{
	margin-bottom: -10px;
	
}

.lead-name-alignment{
	text-align: left;
    padding-left: 11.1%;
}
</style>
<div id="leaderboard-tbl">

</div>

<script>
	var batchSize = 10;
	var startIndex=0;
	var count=0;
	
	var currentDate = new Date();
	var currentYear = currentDate.getFullYear();
	var currentMonth = currentDate.getMonth();
	
	$(document).ready(function(){
		var companyId = "${companyId}";
		var userId = "${userId}";
		
		var userRankingCount = getUserRankingCount("companyId", companyId, currentYear, currentMonth, batchSize, 1);
		if(userRankingCount != null){
			startIndex= userRankingCount.startIndex;
			count=userRankingCount.Count;
		}
		
		var userRankingList = getUserRankingList("companyId",companyId, currentYear, currentMonth, startIndex, batchSize, 1);
		
		var tableData='';
		if(userRankingList != null && userRankingList.length != 0){
			tableData=drawLeaderboardTableStructure(userRankingList, userId)
			$('#leaderboard-list').removeClass('hide');
			$('#leaderboard-tbl').html(tableData);
			$('#leaderboard-empty-list-msg-div').addClass('hide');
		}else{
			$('#leaderboard-list').addClass('hide');
			$('#leaderboard-empty-list-msg-div').removeClass('hide');
		}
			
});
</script>
