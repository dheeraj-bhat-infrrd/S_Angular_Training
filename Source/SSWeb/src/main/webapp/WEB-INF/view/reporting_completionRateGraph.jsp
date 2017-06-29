<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
	
	$.ajax({
		url : "/fetchreportingcompletionrate.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			chartData = data;
			var maxTransactionValue = 0;
			var compRateChartData = new Array(chartData.length+1);
			for (var k = 0; k <= chartData.length; k++) {
				compRateChartData[k] = new Array(3);
			}
			compRateChartData[0] = ['Month', 'Completed Transactions ', 'Incomplete Transactions '];
			
			for(var i=1;i<=chartData.length;i++){
				compRateChartData[i][0] = chartData[i-1][1] + "/" + chartData[i-1][0];
				compRateChartData[i][1] = chartData[i-1][2];
				compRateChartData[i][2] = chartData[i-1][3];
				
				if(compRateChartData[i][1] > maxTransactionValue){
					maxTransactionValue =  compRateChartData[i][1];
				}
				
				if(compRateChartData[i][2] > maxTransactionValue){
					maxTransactionValue =  compRateChartData[i][2];
				}
			}
		
			var maxVAxisValue = 0;
			
			if(maxTransactionValue<10){
				maxVAxisValue = maxTransactionValue + 5;
			}else{
				maxVAxisValue = maxTransactionValue + 10;
			}
			
  		var data = google.visualization.arrayToDataTable(compRateChartData);

  		var options = {
    		title: 'Completion Rate',
    		height:300,
    		width:1100,
    		chartArea:{width:'78%'},
    		vAxis: { minValue:0, maxValue: maxVAxisValue ,gridlines : {count : 6	}},
    		legend: { position: 'right',alignment:'center',maxLines:2},
    		pointSize:5
  		};

 		 var chart = new google.visualization.LineChart(document.getElementById('completion_chart_div'));

  		chart.draw(data, options);
		},
	error : function(e) {
		if (e.status == 504) {
			redirectToLoginPageOnSessionTimeOut(e.status);
			return;
		}
		redirectErrorpage();
	}
	});
}
</script>

<div id="completion_chart_div" style="width:100%; height:300px"></div>