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

     .rep-sps-nps-header{
		font-size: 20px;
    	text-align:left;
   		margin-left: 50px;
   		padding: 0;
     }
</style>
<span class="rep-sps-nps-header col-xs-12 col-sm-12 col-md-12 col-lg-12">Social Promoter Score (SPS)</span>
<div class="col-lg-3 col-md-3 col-sm-3 rep-sps-div">
	
	<div id="spsGaugeSuccess" class="hide">
		<%@ include file="reporting_spsGauge.jsp" %>
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
		<div class="float-left dash-sel-lbl rep-dash-sel-lbl-det rep-sps-nps-lbl">Detractors</div>
		<div id="detractorsBar" class="float-left dash-sel-lbl rep-dash-bar-margin"
			style=" height:65%; background:#E8341F; "></div>
		<div id="detractorsValue" class="float-left dash-sel-lbl" style="color: #E8341F; text-align:left; margin-left:5px"></div>
	</div>
	<div class="rep-det-div">
		<div class="float-left dash-sel-lbl rep-dash-sel-lbl-pas rep-sps-nps-lbl">Passives</div>
		<div id="passivesBar" class="float-left dash-sel-lbl rep-dash-bar-margin"
			style=" height:65%; background:#999999; "></div>
		<div id="passivesValue" class="float-left dash-sel-lbl" style="color: #999999; text-align:left; margin-left:5px"></div>
	</div>
	<div class="rep-det-div">
		<div class="float-left dash-sel-lbl rep-dash-lbl-sel-prom rep-sps-nps-lbl">Promoters</div>
		<div id="promotersBar" class="float-left dash-sel-lbl rep-dash-bar-margin"
			style=" height:65%; background:#7ab400; "></div>
		<div id="promotersValue" class="float-left dash-sel-lbl" style="color: #7ab400; text-align:left; margin-left:5px"></div>
	</div>
</div>

<div class="col-lg-6 col-md-6 col-sm-6">
	<div id="sps-dash" class="hide" ></div>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> 
		<div id="chart_div" style="width:100%; min-height:300px"></div>
	</div>
</div>

<div id="nps-row">
	<span class="rep-sps-nps-header col-xs-12 col-sm-12 col-md-12 col-lg-12">Net Promoter Score (NPS)</span>
	<div class="col-lg-3 col-md-3 col-sm-3 rep-sps-div">

		<div id="npsGaugeSuccess" class="hide">
			<%@ include file="reporting_npsGauge.jsp" %>
		</div>
		<div id="npsGaugeFailure" class=hide>
			<div style="text-align: center; margin: 30% auto">
				<span class="incomplete-trans-span">There are No</span>
				<div style="clear: both">
					<span class="incomplete-trans-span">Detractors, Passives and Promoters</span>
				</div>
				<div style="clear: both">
					<span class="incomplete-trans-span">NPS score is 0</span>
				</div>
			</div>
		</div>
	</div>
	<div class="col-lg-3 col-md-3 col-sm-3 rep-sps-scores">
		<div class="rep-det-div">
			<div class="float-left dash-sel-lbl rep-dash-sel-lbl-det rep-sps-nps-lbl">Detractors</div>
			<div id="npsDetractorsBar" class="float-left dash-sel-lbl rep-dash-bar-margin" style="height: 65%; background: #E8341F;"></div>
			<div id="npsDetractorsValue" class="float-left dash-sel-lbl" style="color: #E8341F; text-align: left; margin-left: 5px"></div>
		</div>
		<div class="rep-det-div">
			<div class="float-left dash-sel-lbl rep-dash-sel-lbl-pas rep-sps-nps-lbl">Passives</div>
			<div id="npsPassivesBar" class="float-left dash-sel-lbl rep-dash-bar-margin" style="height: 65%; background: #999999;"></div>
			<div id="npsPassivesValue" class="float-left dash-sel-lbl" style="color: #999999; text-align: left; margin-left: 5px"></div>
		</div>
		<div class="rep-det-div">
			<div class="float-left dash-sel-lbl rep-dash-lbl-sel-prom rep-sps-nps-lbl">Promoters</div>
			<div id="npsPromotersBar" class="float-left dash-sel-lbl rep-dash-bar-margin" style="height: 65%; background: #7ab400;"></div>
			<div id="npsPromotersValue" class="float-left dash-sel-lbl" style="color: #7ab400; text-align: left; margin-left: 5px"></div>
		</div>
	</div>

	<div class="col-lg-6 col-md-6 col-sm-6">
		<div id="nps-dash" class="hide"></div>
		<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
			<div id="nps_chart_div" style="width: 100%; min-height: 300px"></div>
		</div>
	</div>
</div>

<div id="graphTabs" class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; display: inline-block; float:left; width:100%;height:360px; border-top:1px solid #d2cdcd;margin-left:15px">
	<span class="rep-sps-lbl" style="margin-top: 13px;">Completion Rate</span>
	<div id="completion-graph-dash" class="hide" ></div>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> 
		<div id="completion_chart_div" style="width:100%; height:300px; margin-top:20px; "></div>
	</div>
</div>

<script>
$(window).resize(function(){
	if($('#completion_chart_div').length>0){
		if($('#overview-tab').hasClass('active')){
			drawCompletionRateGraph();
		}	
	}
	
	 if($('#chart_div').length>0){
		 if($('#overview-tab').hasClass('active')){
		 	drawSpsStatsGraph();
		 }
	 }
	 
	 var entityType = "${columnName}";
	 var entityId = "${columnValue}";
	 
	 if($('#nps_chart_div').length>0){
		 if($('#overview-tab').hasClass('active')){
		 	drawNpsStatsGraph(entityId,entityType);
		 }
	 }
});

</script>