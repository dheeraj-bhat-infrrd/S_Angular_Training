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
	var currentMonth = currentDate.getMonth()+1;
	
	$(document).ready(function(){
		var companyId = "${companyId}";
		var userId = "${userId}";
		var profileMasterId = "${profilemasterid}";
		var userRankingCount =null;
		var columnName = "${columnName}";
		var columnId = "${columnId}";
		
		console.log(columnName,columnId);
		if(profileMasterId != 4){
			userRankingCount = getUserRankingCountForAdmins(columnName, columnId, currentYear, currentMonth, batchSize, 1)
			if(userRankingCount != null){
				startIndex= 0;
				count=userRankingCount.Count;
			}
		}else{
			userRankingCount = getUserRankingCount("companyId", companyId, currentYear, currentMonth, batchSize, 1);
			if(userRankingCount != null){
				startIndex= userRankingCount.startIndex;
				count=userRankingCount.Count;
			}
		}
		
		$('#rank-count').html('/'+count);
		
		var userRankingList = null;
		if(profileMasterId != 4){
			userRankingList = getUserRankingList(columnName,columnId, currentYear, currentMonth, startIndex, batchSize, 1);
		}else{
			userRankingList = getUserRankingList("companyId",companyId, currentYear, currentMonth, startIndex, batchSize, 1);
		}
		
		var tableData='';
		if(userRankingList != null && userRankingList.length != 0){
			tableData=drawLeaderboardTableStructure(userRankingList, userId,profileMasterId);
			$('#leaderboard-list').removeClass('hide');
			$('#leaderboard-tbl').html(tableData);
			$('#leaderboard-empty-list-msg-div').addClass('hide');
		}else{
			$('#leaderboard-list').addClass('hide');
			$('#leaderboard-empty-list-msg-div').removeClass('hide');
		}
			
});
</script>
