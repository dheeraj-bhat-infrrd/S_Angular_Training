<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="isRealTechOrSSAdmin" value="${ isRealTechOrSSAdmin }"></c:set>
<c:set var="monthOffset" value="${monthOffset}"></c:set>
<c:set var="yearOffset" value="${yearOffset}"></c:set>
<c:set var="columnName" value="${columnName}"></c:set>
<c:set var="columnId" value="${columnId}"></c:set>
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
<div class="hm-header-main-wrapper hm-hdr-bord-bot" style="background-color: #009fe0;">
	<div class="container">
		<div class="hm-header-row clearfix rep-disclaimer" style="color: #f2f2f2;">
			<div id="rep-beta-disclaimer">
				<div style="text-align:center; margin-top:15px;">
					<span class="rep-disc-span" style="text-align: center; line-height: 15px; font-size:small; font-weight: bold !important;">Please note that this is a beta version of the page which is still undergoing final testing before its official release. Should you encounter any bugs, glitches or other problems on the page / reports, please notify us immediately. Your help in this regard is greatly appreciated.</span>
				</div>
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
	</div>
</div>
<script>
var isRealTechOrSSAdmin = "${isRealTechOrSSAdmin}";
var monthOffset = "${monthOffset}";
var yearOffset = "${yearOffset}";
var columnName = "${columnName}";
var columnId = "${columnId}";

$(document).ready(function(){
	$('#min-req-container').on('blur','.ranking-settings-ip',function(e){
		var message = getAndSaveRankingSettingsVal(columnName,isRealTechOrSSAdmin,monthOffset,yearOffset);
		$('#overlay-toast').html(message);
		showToast();
	});
});
</script>