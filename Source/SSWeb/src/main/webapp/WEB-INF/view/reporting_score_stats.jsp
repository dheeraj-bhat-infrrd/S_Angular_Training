<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<style>
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

<div id="overall-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; display: inline-block; float:left; width:100%;height:350px; margin-left:15px">
	<span class="rep-sps-lbl" style="margin-top: 13px;">Overall Rating</span>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> 
		<div id="overall-rating-chart" style="width:80%; height:300px; "></div>
	</div>
</div>

<script>
var overallChartDiv = "overall-rating-chart";
var overallChartData = [
					['Month','Rating'],
					[ 'Jan/2017', 4.0 ],
					[ 'Feb/2017', 5.0 ],
					[ 'Mar/2017', 3.0 ],
					[ 'Apr/2017', 3.0 ],
					[ 'May/2017', 4.5 ],
					[ 'Jun/2017', 5.0 ],
					[ 'Jul/2017', 3.5 ],
					[ 'Aug/2017', 4.0 ]];
drawLineGraphForScoreStats(overallChartDiv, overallChartData);

</script>