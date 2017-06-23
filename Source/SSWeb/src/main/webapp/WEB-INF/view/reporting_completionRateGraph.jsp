<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script type="text/javascript">
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  var data = google.visualization.arrayToDataTable([
    ['Month', 'Completed Transactions ', 'Incomplete Transactions '],
    ['Jan', 24000,22000],
  	['Feb', 25000,23000],
 	['Mar', 26000,24000],
  	['Apr', 25000,23000],
  	['May', 26000,24000],
  	['Jun', 25000,23000],
  	['Jul', 24000,22000],
  	['Aug', 23000,23000],
  	['Sep', 25000,24000],
  	['Oct', 26000,23000],
  	['Nov', 27000,25000],
  	['Dec', 23000,22000],
  ]);

  var options = {
    title: 'Completion Rate',
    height:300,
    width:1100,
    chartArea:{width:'78%'},
    vAxis: { minValue:21000, maxValue:28000},
    legend: { position: 'right',alignment:'center',maxLines:2},
    pointSize:5
  };

  var chart = new google.visualization.LineChart(document.getElementById('completion_chart_div'));

  chart.draw(data, options);
}
</script>

<div id="completion_chart_div" style="width:100%; height:300px"></div>