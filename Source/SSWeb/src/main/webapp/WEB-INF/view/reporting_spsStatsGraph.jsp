<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${detractor}" var="detractors"></c:set>
<c:set value="${passives}" var="passives"></c:set>
<c:set value="${promoters}" var="promoters"></c:set>

<script>
	var chartData;
	var spsChartData = [[ 'SPS', 'Detractors', 'Passives', 'Promoters'],[]];
	google.charts.load('current', {	packages : [ 'corechart', 'bar' ]});
	google.charts.setOnLoadCallback(drawStacked);
	
	function drawStacked() {
		
		$.ajax({
			url : "/fetchreportingspsstats.do",
			type : "GET",
			cache : false,
			dataType : "json",
			success : function(data) {
				chartData = data;
				
				var spsChartData = new Array(chartData.length+1);
				for (var k = 0; k <= chartData.length; k++) {
					  spsChartData[k] = new Array(4);
				}
				spsChartData[0] = [ 'SPS', 'Detractors', 'Passives', 'Promoters'];
				
				for(var i=1;i<=chartData.length;i++){
					spsChartData[i][0] = chartData[i-1][1] + "/" + chartData[i-1][0];
					spsChartData[i][1] = chartData[i-1][2];	
					spsChartData[i][2] = chartData[i-1][3];
					spsChartData[i][3] = chartData[i-1][4];
				}
				
				var data = google.visualization.arrayToDataTable(spsChartData);

				var options = { title : 'SPS Stats',
				                legend : {position : 'none'},
				                bar : {groupWidth : '40%'},
				                isStacked : true,
				                height : 300,
				                vAxis : {
				                         	gridlines : {
				                                  			count : 14
				                                  		}
				                        },
				                colors : [ '#E8341F', '#999999', '#7ab400' ]
				               };

				var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
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
 <div id="chart_div" style="width:100%"></div>