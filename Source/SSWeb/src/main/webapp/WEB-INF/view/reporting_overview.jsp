<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<style>
	.incomplete-trans-span{
		text-align:center;
		font-family: cursive;
		font-size: small;
		font-style: italic;
		font-variant: small-caps;
		font-weight: bold !important;
		line-height: 15px;
	}
	
	.nav-tabs{
		border-bottom:1px solid #c5c5c5;
	}
	
	.nav>li>a:hover{
       background-color: #009FE0 !important;
     }
</style>
<script src="${initParam.resourcesPath}/resources/js/googleloader.js"></script>
<div class="col-lg-3 col-md-3 col-sm-3" style="padding-left:50px">
	<div id="spsGaugeSuccess" class="hide">
		<jsp:include page="reporting_spsGauge.jsp"></jsp:include>
	</div>
	<div id="spsGaugeFailure" class=hide>
		<div style="text-align:center; margin:30% auto">
			<span class="incomplete-trans-span">There are No</span>
			<div style="clear: both">
				<span class="incomplete-trans-span">Detractors, Passives and Promoters</span> 
			</div>
			<div style="clear:both">
				<span class="incomplete-trans-span">SPS score is 0</span>
			</div>
		</div>
	</div>
</div>
<div class="col-lg-3 col-md-3 col-sm-3"
	style="display: grid; margin-top: 20px;">
	<div style="display: inline-flex; margin-top: 10px">
		<div class="float-left dash-sel-lbl" style="padding-left:12px; font-weight:600 !important">Detractors</div>
		<div id="detractorsBar" class="float-left dash-sel-lbl"
			style=" height:65%; background:#E8341F; margin:auto 2px;"></div>
		<div id="detractorsValue" class="float-left dash-sel-lbl" style="color: #E8341F; text-align:left; margin-left:5px"></div>
	</div>
	<div style="display: inline-flex; margin-top: 10px">
		<div class="float-left dash-sel-lbl" style="padding-left:25px; font-weight:600 !important">Passives</div>
		<div id="passivesBar" class="float-left dash-sel-lbl"
			style=" height:65%; background:#999999; margin:auto 2px"></div>
		<div id="passivesValue" class="float-left dash-sel-lbl" style="color: #999999; text-align:left; margin-left:5px"></div>
	</div>
	<div style="display: inline-flex; margin-top: 10px">
		<div class="float-left dash-sel-lbl" style="padding-left:12px; font-weight:600 !important">Promotors</div>
		<div id="promotersBar" class="float-left dash-sel-lbl"
			style=" height:65%; background:#7ab400; margin:auto 2px"></div>
		<div id="promotersValue" class="float-left dash-sel-lbl" style="color: #7ab400; text-align:left; margin-left:5px"></div>
	</div>
</div>

<div class="col-lg-3 col-md-3 col-sm-3 donut-chart">
	<div id="donutChartSuccess">
		<jsp:include page="reporting_donutChart.jsp"></jsp:include>
	</div>
	<div id="donutChartFailure">
	
		<div style="text-align:center; margin:30% auto">
			<span class="incomplete-trans-span">Congratulations</span>
			<div style="clear: both">
				<span class="incomplete-trans-span">You have No</span> 
				<span class="incomplete-trans-span">Incomplete Transactions</span>
			</div>
		</div>
	</div>
</div>

<div class="col-lg-3 col-md-3 col-sm-3">
	<div class="col-lg-6 col-md-6 col-sm-6" style="margin-top: 10px">
		<div class="row-lg-6 row-md-6 row-sm-6"
			style="font-weight: bold !important; font-size: medium">
			Surveys<br/>Sent
			<div id="surveysSentValue" style="font-size: -webkit-xxx-large"></div>
		</div>
		<div class="row-lg-6 row-md-6 row-sm-6"
			style="font-weight: bold !important; font-size: medium">
			Surveys Completed
			<div id="surveysCompValue" style="font-size: -webkit-xxx-large"></div>
		</div>
	</div>
	<div class="col-lg-6 col-md-6 col-sm-6" style="margin-top: 10px">
		<div class="row-lg-6 row-md-6 row-sm-6"
			style="font-weight: bold !important; font-size: medium">
			Social<br/>Posts
			<div id="socialPostsValue" style="font-size: -webkit-xxx-large"></div>
		</div>
		<div class="row-lg-6 row-md-6 row-sm-6"
			style="font-weight: bold !important; font-size: medium;">
			Zillow<br/>Reviews
			<div id="zillowReviewsValue" style="font-size: -webkit-xxx-large"></div>
		</div>
	</div>
</div>
<div id="graphTabs" style="margin-top: 10px; display: inline-block; float:left; width:100%;height:420px">
	<ul class="nav nav-tabs" role="tablist" style="margin-left:50px">
		<li id="sps-stats-btn" class="active"><a href="#sps-stats-tab"
			data-toggle="tab">SPS Stats</a></li>
		<li id="average-rating-btn"><a href="#average-rating-tab"
			data-toggle="tab">Average Rating</a></li>
		<li id="completion-rate-btn"><a href="#completion-rate-tab"
			data-toggle="tab">Completion Rate</a></li>
	</ul>

	<div class="tab-content" >
		<div class="tab-pane fade active in" id="sps-stats-tab" style="width:100%;"><jsp:include
				page="reporting_spsStatsGraph.jsp"></jsp:include></div>
		<div class="tab-pane fade" id="average-rating-tab"><jsp:include
				page="reporting_averageRatingGraph.jsp"></jsp:include></div>
		<div class="tab-pane fade" id="completion-rate-tab"><jsp:include
				page="reporting_completionRateGraph.jsp"></jsp:include></div>
	</div>
</div>

<script>
drawOverviewPage();
</script>