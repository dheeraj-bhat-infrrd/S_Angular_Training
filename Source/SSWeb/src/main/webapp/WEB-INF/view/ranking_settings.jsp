<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="isRealTechOrSSAdmin" value="${ isRealTechOrSSAdmin }"></c:set>
<c:set var="monthOffset" value="${monthOffset}"></c:set>
<c:set var="yearOffset" value="${yearOffset}"></c:set>
<c:set var="columnName" value="${columnName}"></c:set>
<c:set var="columnId" value="${columnId}"></c:set>
<c:set var="isEtlRunning" value="${isEtlRunning}"></c:set>
<c:set var="lastSuccessfulRun" value="${lastSuccessfulRun}"></c:set>
<c:set var="highestrole" value="${highestrole}"></c:set>

<style>
	.ranking-settings-ip-div{
		position: relative;
    	width: 52px;
    	border: 1px solid #dcdcdc;
    	border-radius: 3px;
    	padding: 0 5px;
    	height: 38px;
	}
	
	.ranking-settings-ip{
		width: 45px;
    	border: 0;
    	box-shadow: none;
    	height: 32px;
    	font-size: 15px !important;
	}
	
	.min-req-div{
		margin: 5px 0px 20px 30px;
	}
	
	.min-req-span{
		line-height: 38px;
    	margin-left: 25px;
    	font-size: 14px;
    	font-weight: bold !important;
	}
</style>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft">
				Ranking Settings
			</div>
		</div>
	</div>
</div>

<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div id="min-req-container" class="container pos-relative">
		<div id="min-req-settings">
			<div class="st-score-rt-top width-three-five-zero">Minimum Requirements</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="days-registration" class="ranking-settings-ip" value ="${minDaysOfRegistration}" placeholder="${minDaysOfRegistration}">
				</div>
				<span class="min-req-span">Days registration</span>
			</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="survey-completion" class="ranking-settings-ip" value="${minCompletedPercentage}" placeholder="${minCompletedPercentage}">
				</div>
				<span class="min-req-span">Survey Completion Percentage</span>
			</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="minimum-reviews" class="ranking-settings-ip" value="${minNoOfReviews}" placeholder="${minNoOfReviews}">
				</div>
				<span class="min-req-span">Minimum Reviews</span>
			</div>
		</div>
		<c:if test="${ (isRealTechOrSSAdmin == true or isRealTechOrSSAdmin == 'true') }">
			<div id="offset-value-settings" style="margin-top:20px">
				<div class="st-score-rt-top width-three-five-zero">Offset Value</div>
				<div class="min-req-div">
					<div class="v-um-hdr-right ranking-settings-ip-div float-left">
						<input id="month-offset" class="ranking-settings-ip" value="${monthOffset}" placeholder="${monthOffset}">
					</div>
					<span class="min-req-span">Month Offset</span>
				</div>
				<div class="min-req-div">
					<div class="v-um-hdr-right ranking-settings-ip-div float-left">
						<input id="year-offset" class="ranking-settings-ip" value="${yearOffset}" placeholder="${yearOffset}">
					</div>
					<span class="min-req-span">Year Offset</span>
				</div>
			</div>
		</c:if>
		<c:if test="${ (isRealTechOrSSAdmin == true or isRealTechOrSSAdmin == 'true') && highestrole == 1}">
			<div style="margin-top:20px">
					<div class="st-score-rt-top width-three-five-zero"><spring:message code="lable.ranking.settings.recalculate.key" /></div>
					<div class="recalculate-user-ranking">
						<div class="recalculate-usr-rank-btn-active text-center" onclick="javascript:recalculateUserRanking()">
							<spring:message code="lable.ranking.settings.recalculate.btn.key" />
						</div>
						<div class="recalculate-usr-rank-btn-inactive text-center">
							<spring:message code="lable.ranking.settings.recalculate.btn.key" />
						</div>
					<c:if test="${ (lastSuccessfulRun != undefined and lastSuccessfulRun != '' and lastSuccessfulRun != null) }">
						<span class="min-req-span">
							Last ETL Runtime : ${lastSuccessfulRun}
						</span>
					</c:if>
					</div>
			</div>
		</c:if>
	</div>
</div>
<script>
var isRealTechOrSSAdmin = "${isRealTechOrSSAdmin}";
var monthOffset = "${monthOffset}";
var yearOffset = "${yearOffset}";
var columnName = "${columnName}";
var columnId = "${columnId}";
var isEtlRunning = "${isEtlRunning}";
var lastSuccessfulRun = "${lastSuccessfulRun}";
var highestrole = "${highestrole}";

console.log("isEtlRunning: ", highestrole);

$(document).ready(function(){
	$('#min-req-container').on('blur','.ranking-settings-ip',function(e){
		var message = getAndSaveRankingSettingsVal(columnName,isRealTechOrSSAdmin,monthOffset,yearOffset);
		$('#overlay-toast').html(message);
		showToast();
	});
	
	if(isEtlRunning == true || isEtlRunning == 'true'){
		$('.recalculate-usr-rank-btn-inactive').show();
		$('.recalculate-usr-rank-btn-active').hide();
	}else if(isEtlRunning == false || isEtlRunning == 'false'){
		$('.recalculate-usr-rank-btn-inactive').hide();
		$('.recalculate-usr-rank-btn-active').show();
	}
});
</script>