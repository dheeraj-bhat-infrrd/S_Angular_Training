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
		<div id="overall-rating-chart" style="width:80%; height:300px;margin: 20px 20px 20px 60px;"></div>
	</div>
</div>

<div id="question-ratings-div" class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; float:left; width:100%; margin-left:15px">
	
</div>

<script>
var overallChartDiv = "overall-rating-chart";
var overallChartData = [
					['Month','Rating'],
					[ 'Sep/2016', 1.0 ],
					[ 'Oct/2016', 2.0 ],
					[ 'Nov/2016', 3.0 ],
					[ 'Dec/2016', 2.5 ],
					[ 'Jan/2017', 3.5 ],
					[ 'Feb/2017', 5.0 ],
					[ 'Mar/2017', 3.0 ],
					[ 'Apr/2017', 3.0 ],
					[ 'May/2017', 4.5 ],
					[ 'Jun/2017', 5.0 ],
					[ 'Jul/2017', 3.5 ],
					[ 'Aug/2017', 4.0 ]];
drawLineGraphForScoreStats(overallChartDiv, overallChartData);

var questionOneChartData = [
        					['Month','Rating'],
        					[ 'Sep/2016', 5.0 ],
        					[ 'Oct/2016', 2.0 ],
        					[ 'Nov/2016', 1.0 ],
        					[ 'Dec/2016', 3.5 ],
        					[ 'Jan/2017', 3.5 ],
        					[ 'Feb/2017', 5.0 ],
        					[ 'Mar/2017', 4.0 ],
        					[ 'Apr/2017', 3.0 ],
        					[ 'May/2017', 4.5 ],
        					[ 'Jun/2017', 5.0 ],
        					[ 'Jul/2017', 3.5 ],
        					[ 'Aug/2017', 4.0 ]];
        					
var questionTwoChartData = [
        					['Month','Rating'],
        					[ 'Sep/2016', 4.0 ],
        					[ 'Oct/2016', 5.0 ],
        					[ 'Nov/2016', 2.0 ],
        					[ 'Dec/2016', 4.5 ],
        					[ 'Jan/2017', 3.5 ],
        					[ 'Feb/2017', 2.0 ],
        					[ 'Mar/2017', 4.0 ],
        					[ 'Apr/2017', 3.0 ],
        					[ 'May/2017', 4.5 ],
        					[ 'Jun/2017', 5.0 ],
        					[ 'Jul/2017', 3.5 ],
        					[ 'Aug/2017', 4.0 ]];
        					
var questionThreeChartData = [
        					['Month','Rating'],
							[ 'Sep/2016', 1.0 ],
							[ 'Oct/2016', 2.0 ],
							[ 'Nov/2016', 3.0 ],
							[ 'Dec/2016', 2.5 ],
							[ 'Jan/2017', 3.5 ],
							[ 'Feb/2017', 5.0 ],
							[ 'Mar/2017', 3.0 ],
							[ 'Apr/2017', 3.0 ],
							[ 'May/2017', 4.5 ],
							[ 'Jun/2017', 5.0 ],
							[ 'Jul/2017', 3.5 ],
							[ 'Aug/2017', 4.0 ]];
							
var questionChartData = [ questionOneChartData, questionTwoChartData, questionThreeChartData];

var questionOne = "Was your Rental Agent, friendly and knowledgeable?";
var questionTwo = "How would you rate Advantage Rent A Car Team?";
var questionThree = "How would you rate cleanliness and condition of your car?";

var questions = [questionOne , questionTwo , questionThree ];

var count = 3;

for(var i=0 ; i<3 ; i++){
	var graphDivHtml = '';
	graphDivHtml += '<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12" style="margin-top: 10px; display: inline-block; float:left; width:100%;height:350px; margin-left:15px">'
				+ '<span class="rep-sps-lbl" style="margin-top: 13px;">'+ questions[i] + '</span>'
				+ '<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12"> '
				+ '<div id="question-rating-chart-'+i+'" style="width:80%; height:300px;margin: 20px 20px 20px 60px;"></div>'
				+ '</div></div>';
	
	$('#question-ratings-div').append(graphDivHtml);
}

for(var i=0; i<3 ; i++){
	drawLineGraphForScoreStats("question-rating-chart-"+i, questionChartData[i]);
}

getOverallScoreStats(3, "companyId");

</script>