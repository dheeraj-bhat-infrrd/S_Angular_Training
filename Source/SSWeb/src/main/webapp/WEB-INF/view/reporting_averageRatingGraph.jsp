<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
google.charts.load("current", {packages:["corechart"]});
google.charts.setOnLoadCallback(drawChart);
function drawChart() {
	
	$.ajax({
		url : "/fetchaveragereportingrating.do",
		type : "GET",
		cache : false,
		dataType : "json",
		success : function(data) {
			chartData = data;
			
			var avgRatingChartData = new Array(chartData.length+1);
			for (var k = 0; k <= chartData.length; k++) {
				avgRatingChartData[k] = new Array(2);
			}
			avgRatingChartData[0] = ['X','Y'];
			
			for(var i=1;i<=chartData.length;i++){
				avgRatingChartData[i][0] = chartData[i-1][1] + "/" + chartData[i-1][0];
				avgRatingChartData[i][1] = chartData[i-1][2];	
			}
			
			
  		var data = google.visualization.arrayToDataTable(avgRatingChartData);

  		var options = {
  			legend: 'none',
    		height:300,
    		width:1000,
    		vAxis: { 
    				title:'Average Rating',
    				minValue:0, 
    				maxValue:6,
    				gridlines : {
      					count : 7
      				}
  		   		},
    		colors: ['009fe0'],
    		pointSize: 5
 		};

  		var chart = new google.visualization.LineChart(document.getElementById('average_chart_div'));
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

  <div id="average_chart_div" style="width:100%"></div>