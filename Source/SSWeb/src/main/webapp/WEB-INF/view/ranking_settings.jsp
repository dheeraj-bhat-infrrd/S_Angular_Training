<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
	<div class="container pos-relative">
		<div id="min-req-settings">
			<div class="st-score-rt-top width-three-five-zero">Minimum Requirements</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="days-registration" class="ranking-settings-ip" placeholder="60">
				</div>
				<span class="min-req-span">Days registration</span>
			</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="survey-completion" class="ranking-settings-ip" placeholder="40%">
				</div>
				<span class="min-req-span">Survey Completion</span>
			</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="minimum-reviews" class="ranking-settings-ip" placeholder="25">
				</div>
				<span class="min-req-span">Minimum Reviews</span>
			</div>
		</div>
		<div id="offset-value-settings" style="margin-top:20px">
			<div class="st-score-rt-top width-three-five-zero">Offset Value</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="days-registration" class="ranking-settings-ip" placeholder="3">
				</div>
				<span class="min-req-span">Monthly Offset</span>
			</div>
			<div class="min-req-div">
				<div class="v-um-hdr-right ranking-settings-ip-div float-left">
					<input id="survey-completion" class="ranking-settings-ip" placeholder="-1">
				</div>
				<span class="min-req-span">Yearly Offset</span>
			</div>
		</div>
	</div>
</div>