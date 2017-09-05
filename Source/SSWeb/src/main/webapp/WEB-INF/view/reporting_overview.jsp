<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<style>
	.incomplete-trans-span{
		text-align:center;
		font-size: small;
		font-variant: small-caps;
		line-height: 15px;
	}
	
     .rep-sps-lbl{
     	font-size: 20px;
    	position: absolute;
    	left: 0;
    	z-index: 1000;
    	float: left;
    	margin-top: -20px;
   		margin-left: 50px;
    	
     }
</style>
<span class="rep-sps-lbl">Social Promoter Score (SPS)</span>
<div class="col-lg-3 col-md-3 col-sm-3 rep-sps-div">
	
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
<div class="col-lg-3 col-md-3 col-sm-3 rep-sps-scores">
	<div class="rep-det-div">
		<div class="float-left dash-sel-lbl rep-dash-sel-lbl-det">Detractors</div>
		<div id="detractorsBar" class="float-left dash-sel-lbl"
			style=" height:65%; background:#E8341F; margin:auto 2px;"></div>
		<div id="detractorsValue" class="float-left dash-sel-lbl" style="color: #E8341F; text-align:left; margin-left:5px"></div>
	</div>
	<div class="rep-det-div">
		<div class="float-left dash-sel-lbl rep-dash-sel-lbl-pas">Passives</div>
		<div id="passivesBar" class="float-left dash-sel-lbl"
			style=" height:65%; background:#999999; margin:auto 2px"></div>
		<div id="passivesValue" class="float-left dash-sel-lbl" style="color: #999999; text-align:left; margin-left:5px"></div>
	</div>
	<div class="rep-det-div">
		<div class="float-left dash-sel-lbl rep-dash-lbl-sel-prom">Promotors</div>
		<div id="promotersBar" class="float-left dash-sel-lbl"
			style=" height:65%; background:#7ab400; margin:auto 2px"></div>
		<div id="promotersValue" class="float-left dash-sel-lbl" style="color: #7ab400; text-align:left; margin-left:5px"></div>
	</div>
</div>

<div class="col-lg-6 col-md-6 col-sm-6">
	<jsp:include page="reporting_spsStatsGraph.jsp"></jsp:include>
</div>

<div id="graphTabs" class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; display: inline-block; float:left; width:100%;height:360px; border-top:1px solid #d2cdcd;margin-left:15px">
	<span class="rep-sps-lbl" style="margin-top: 13px;">Completion Rate</span>
	<jsp:include page="reporting_completionRateGraph.jsp"></jsp:include>
</div>

<script>
drawOverviewPage();
</script>